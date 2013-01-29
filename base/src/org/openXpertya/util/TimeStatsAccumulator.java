package org.openXpertya.util;

import java.util.HashMap;


public class TimeStatsAccumulator {

	protected long accumulatedTime = 0;

	public long getAccumulatedTime() {
		return accumulatedTime;
	}

	public void setAccumulatedTime(long accumulatedTime) {
		this.accumulatedTime = accumulatedTime;
	}
	
	public void addElapsedTime(long elapsedTime) {
		accumulatedTime += elapsedTime;
	}
	
	public void resetAccumulator() {
		accumulatedTime = 0;
	}
	
	
	/** -- Componentes Estáticos de Uso y Acceso -- */
	
	public static HashMap<String, TimeStatsAccumulator> accumulators = new HashMap<String, TimeStatsAccumulator>();
	
	public static void addAccumulator(String accumulatorName) {
		accumulators.put(accumulatorName, new TimeStatsAccumulator());
	}
	
	public static TimeStatsAccumulator getAccumulator(String accumulatorName) {
		if (accumulators.get(accumulatorName) == null)
			accumulators.put(accumulatorName, new TimeStatsAccumulator());
		return accumulators.get(accumulatorName);
	}
}
