package org.openXpertya.apps.form;

import java.util.ArrayList;
import java.util.Collection;

public abstract class DocbaseTypeStub {

	private String description;

	public DocbaseTypeStub() {
		super();
	}

	public DocbaseTypeStub(String description) {
		setDescription(description);
	}
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public abstract Collection getDocbaseTypes();

	public String toString() {
		return getDescription();
	}

}