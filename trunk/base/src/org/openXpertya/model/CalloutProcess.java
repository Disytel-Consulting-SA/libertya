package org.openXpertya.model;

import java.util.Map;
import java.util.Properties;

public interface CalloutProcess {

	public String start(Properties ctx, Integer windowNo, String methodName, MField field, Object value, Object oldValue,
			Map<String, MField> fields);
	
}
