package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.openXpertya.util.Util;

public abstract class AbstractSvrProcess extends SvrProcess {

	/** Parámetros del proceso OXP */
	private Map<String, Object> parametersValues;
	/** Dato Info de los Parámetros del proceso OXP */
	private Map<String, String> parametersInfo;
	
	@Override
	protected void prepare() {
		// Se crea el Map de parámetros
		setParametersValues(new HashMap<String, Object>());
		setParametersInfo(new HashMap<String, String>());
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName().toUpperCase();
			// Valores del parámetro
			Object value = para[i].getParameter();
			// OXP retorna los enteros como BigDecimal. Para los campos que son IDs
			// se cambia el valor a un entero.
			if (name.endsWith("_ID"))
				value = ((BigDecimal)value).intValue();
			
			// Se guarda el valor del parámetro.
			getParametersValues().put(name , value);
			// Se obtiene el valor de fin de rango para determinar si el parámetro es un rango.
			Object value_to = para[i].getParameter_To();
			// Si es un rango, se guardan el valor del fin del rango (concatenando un _TO al nombre). 
			if (value_to != null) 
				getParametersValues().put(name + "_TO", value_to);
			// Guardar el dato info del parámetro y del parámetro TO
			getParametersInfo().put(name, para[i].getInfo());
			if(!Util.isEmpty(para[i].getInfo_To())){
				getParametersInfo().put(name + "_TO", para[i].getInfo_To());
			}
		}
	}

	protected Map<String, String> getParametersInfo() {
		return parametersInfo;
	}

	protected void setParametersInfo(Map<String, String> parametersInfo) {
		this.parametersInfo = parametersInfo;
	}

	protected Map<String, Object> getParametersValues() {
		return parametersValues;
	}

	protected void setParametersValues(Map<String, Object> parametersValues) {
		this.parametersValues = parametersValues;
	}
}
