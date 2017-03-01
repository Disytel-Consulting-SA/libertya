package org.openXpertya.process.customImport.centralPos.pojos;

import java.lang.reflect.Field;

import com.google.gson.annotations.SerializedName;

/**
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public abstract class GenericDatum extends Pojo {

	public static String get(String jsonFieldName, Object obj) {
		if (jsonFieldName == null || jsonFieldName.trim().isEmpty()) {
			return null;
		}
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field f : fields) {
				SerializedName sName = f.getAnnotation(SerializedName.class);
				if (sName.value().equals(jsonFieldName)) {
					f.setAccessible(true);
					return String.valueOf(f.get(obj));
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

}
