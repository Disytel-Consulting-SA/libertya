package org.openXpertya.apps.form;

import java.util.ArrayList;
import java.util.Collection;

public class MultipleDocbaseTypeStub extends DocbaseTypeStub {

	private Collection types = new ArrayList();
	
	public MultipleDocbaseTypeStub(String description) {
		super(description);
	}
	
	public void addType(String docbaseType) {
		getTypes().add(docbaseType);
	}
	
	public Collection getDocbaseTypes() {
		return getTypes();
	}

	private Collection getTypes() {
		return types;
	}

	private void setTypes(Collection types) {
		this.types = types;
	}

}
