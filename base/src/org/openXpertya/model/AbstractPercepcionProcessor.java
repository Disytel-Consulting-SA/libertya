package org.openXpertya.model;

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
	 * @return la percepción de débito aplicada en este procesador
	 */
	public abstract Percepcion applyDebitPerception();

	/**
	 * @return la percepción de crédito aplicada en este procesador
	 */
	public abstract Percepcion applyCreditPerception();

}
