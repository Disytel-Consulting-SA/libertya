package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;

public abstract class AbstractPercepcionProcessor {

	public static AbstractPercepcionProcessor getPercepcionProcessor(
			Properties ctx, Integer percepcionProcessorID, String trxName) throws Exception {
		return (AbstractPercepcionProcessor) MRetencionProcessor
				.getProcessorClass(ctx, percepcionProcessorID, trxName);
	}
	
	/** Datos de percepcion */
	private PercepcionProcessorData percepcionData;
	
	public AbstractPercepcionProcessor(){
		
	}
	
	public AbstractPercepcionProcessor(PercepcionProcessorData percepcionData){
		setPercepcionData(percepcionData);
	}
	
	public PercepcionProcessorData getPercepcionData() {
		return percepcionData;
	}

	public void setPercepcionData(PercepcionProcessorData percepcionData) {
		this.percepcionData = percepcionData;
	}
	
	/**
	 * @return el porcentaje de percepción a aplicar
	 */
	public abstract BigDecimal getPercepcionPercToApply();

}
