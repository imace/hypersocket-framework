package com.hypersocket.realm.events;

import java.util.List;

import com.hypersocket.realm.Principal;
import com.hypersocket.realm.Realm;
import com.hypersocket.realm.RealmProvider;
import com.hypersocket.session.Session;

public class GroupUpdatedEvent extends GroupEvent {

	private static final long serialVersionUID = 7661773189101981651L;

	public GroupUpdatedEvent(Object source, Session session, Realm realm,
			RealmProvider provider, Principal principal,
			List<Principal> associatedPrincipals) {
		super(source, "event.groupUpdated", session, realm, provider, principal,
				associatedPrincipals);
	}

	public GroupUpdatedEvent(Object source, Throwable e, Session session,
			Realm realm, RealmProvider provider, String principalName, 
			List<Principal> associatedPrincipals) {
		super(source, "event.groupUpdated", e, session, realm.getName(), provider,
				principalName, associatedPrincipals);
	}

}