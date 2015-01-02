package com.hypersocket.tasks.alert;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hypersocket.events.EventDefinition;
import com.hypersocket.events.EventService;
import com.hypersocket.events.SystemEvent;
import com.hypersocket.i18n.I18N;
import com.hypersocket.i18n.Message;
import com.hypersocket.properties.ResourceTemplateRepository;
import com.hypersocket.properties.ResourceUtils;
import com.hypersocket.tasks.AbstractTaskProvider;
import com.hypersocket.tasks.Task;
import com.hypersocket.tasks.TaskProvider;
import com.hypersocket.tasks.TaskProviderService;
import com.hypersocket.tasks.TaskProviderServiceImpl;
import com.hypersocket.triggers.TaskResult;
import com.hypersocket.triggers.TriggerAction;
import com.hypersocket.triggers.TriggerResourceService;
import com.hypersocket.triggers.TriggerResourceServiceImpl;
import com.hypersocket.triggers.ValidationException;

@Component
public class AlertTask extends AbstractTaskProvider implements
		TaskProvider {

	public static final String ACTION_GENERATE_ALERT = "generateAlert";

	public static final String ATTR_KEY = "alert.key";
	public static final String ATTR_THRESHOLD = "alert.threshold";
	public static final String ATTR_TIMEOUT = "alert.timeout";
	public static final String ATTR_ALERT_ID = "alert.id";

	@Autowired
	AlertTaskRepository repository;

	@Autowired
	TriggerResourceService triggerService;

	@Autowired
	EventService eventService;

	@Autowired
	TaskProviderService taskService; 
	
	@PostConstruct
	private void postConstruct() {
		taskService.registerActionProvider(this);

		eventService.registerEvent(AlertEvent.class,
				TaskProviderServiceImpl.RESOURCE_BUNDLE);

		for (TriggerAction action : triggerService
				.getActionsByResourceKey(ACTION_GENERATE_ALERT)) {
			registerDynamicEvent(action);
		}
	}

	@Override
	public String getResourceBundle() {
		return TriggerResourceServiceImpl.RESOURCE_BUNDLE;
	}

	@Override
	public String[] getResourceKeys() {
		return new String[] { ACTION_GENERATE_ALERT };
	}

	@Override
	public void validate(Task task, Map<String, String> parameters)
			throws ValidationException {

	}

	@Override
	public TaskResult execute(Task task, SystemEvent event)
			throws ValidationException {

		StringBuffer key = new StringBuffer();

		for (String attr : ResourceUtils.explodeValues(repository.getValue(task,
				ATTR_KEY))) {
			if (key.length() > 0) {
				key.append("|");
			}
			key.append(event.getAttribute(attr));
		}

		int threshold = repository.getIntValue(task, ATTR_THRESHOLD);
		int timeout = repository.getIntValue(task, ATTR_TIMEOUT);

		AlertKey ak = new AlertKey();
		ak.setTask(task);
		ak.setKey(key.toString());

		Calendar c = Calendar.getInstance();
		ak.setTriggered(c.getTime());

		repository.saveKey(ak);

		c.add(Calendar.MINUTE, -timeout);
		long count = repository
				.getKeyCount(task, key.toString(), c.getTime());

		if (count >= threshold) {

			repository.deleteKeys(task, key.toString());

			return new AlertEvent(this, "event.alert", true,
					event.getCurrentRealm(), threshold, timeout, task, event);
		}
		return null;
	}

	@Override
	public ResourceTemplateRepository getRepository() {
		return repository;
	}

	private void registerDynamicEvent(TriggerAction action) {
		EventDefinition sourceEvent = eventService.getEventDefinition(action
				.getTrigger().getEvent());

		String resourceKey = "event.alert." + action.getId();

		I18N.overrideMessage(Locale.ENGLISH,
				new Message(TriggerResourceServiceImpl.RESOURCE_BUNDLE,
						resourceKey, action.getName(), action.getName()));
		I18N.overrideMessage(
				Locale.ENGLISH,
				new Message(TriggerResourceServiceImpl.RESOURCE_BUNDLE,
						resourceKey + ".warning", repository.getValue(action,
								"alert.text"), repository.getValue(action,
								"alert.text")));

		I18N.flushOverrides();
		EventDefinition def = new EventDefinition(
				TriggerResourceServiceImpl.RESOURCE_BUNDLE, resourceKey, null);
		def.getAttributeNames().addAll(sourceEvent.getAttributeNames());

		eventService.registerEventDefinition(def);
	}

	@Override
	public void taskCreated(Task task) {
		registerDynamicEvent((TriggerAction)task);
	}

	@Override
	public void taskUpdated(Task task) {
		super.taskUpdated(task);
	}

	@Override
	public void taskDeleted(Task task) {
		super.taskDeleted(task);
	}

	@Override
	public boolean supportsAutomation() {
		return false;
	}

}