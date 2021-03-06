/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.properties;

import org.springframework.transaction.annotation.Transactional;

import com.hypersocket.resource.AbstractResource;

@Transactional
public class ResourcePropertyTemplate extends AbstractPropertyTemplate {

	AbstractResource resource;
	
	public ResourcePropertyTemplate(AbstractPropertyTemplate t, AbstractResource resource) {
		this.resourceKey = t.getResourceKey();
		this.defaultValue = t.getDefaultValue();
		this.metaData = t.getMetaData();
		this.mapping = t.getMapping();
		this.weight = t.getWeight();
		this.category = t.getCategory();
		this.hidden = t.isHidden();
		this.readOnly = t.isReadOnly();
		this.resource = resource;
		this.propertyStore = t.getPropertyStore();
	}
	
	public void setPropertyStore(ResourcePropertyStore propertyStore) {
		this.propertyStore = propertyStore;
	}
	
	public String getValue() {
		return ((ResourcePropertyStore)propertyStore).getPropertyValue(this, resource);
	}
	
	public void setResource(AbstractResource resource) {
		this.resource = resource;
	}
}
