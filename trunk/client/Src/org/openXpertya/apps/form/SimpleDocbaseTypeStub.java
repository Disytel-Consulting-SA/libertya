package org.openXpertya.apps.form;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleDocbaseTypeStub extends DocbaseTypeStub {
	String docbaseType;

	public SimpleDocbaseTypeStub(String docTypeId, String description) {
		setDescription(description);
		setDocbaseType(docTypeId);
	}

	
	public void setDocbaseType(String docTypeId) {
		this.docbaseType = docTypeId;
	}


	public Collection getDocbaseTypes() {
		ArrayList aux = new ArrayList();
		aux.add(docbaseType);
		return aux;
	}

}
