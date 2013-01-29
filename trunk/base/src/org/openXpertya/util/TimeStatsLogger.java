package org.openXpertya.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


public class TimeStatsLogger {

	/** Logger asociado */
	private static CLogger log = CLogger.getCLogger(TimeStatsLogger.class);

	/** Nivel de logging utilizado */
	private static final Level LEVEL = TimeStatsLevel.TSTATS; 
	
	/** Tareas activas */
	private static Map<String, Long> tasks = new HashMap<String, Long>();
	
	public static void beginTask(String taskName) {
		//log.log(LEVEL, "Comenzando Tarea: [" + taskName + "]");
		tasks.put(taskName, System.currentTimeMillis());
	}
	
	/** Backward compatibility */
	public static void endTask(String taskName) {
		endTask(taskName, null);
	}
	
	
	public static void endTask(String taskName, TimeStatsAccumulator accu) {
		long endTime = System.currentTimeMillis();
		Long initTime = tasks.get(taskName);
		if (initTime == null) {
			return;
		}
		
		long elapsedTime = endTime - initTime;
		log.log(LEVEL, "[" + taskName + "] - Duración: "
				+ elapsedTime + "ms (" + formatElapsedTime(elapsedTime) + ")");
		// Elimina la tarea de las Map con las tareas activas
		
		/* Si recibe un acumulador, sumar en dicho acumulador el tiempo transcurrido */
		if (accu != null)
			accu.addElapsedTime(elapsedTime);
		tasks.remove(taskName);
	}
	
	private static String formatElapsedTime(long elapsedTime) {
		long hours = elapsedTime/3600000;
		long hoursSurplus = elapsedTime%3600000;
		long minutes = hoursSurplus/60000;
		long minutesSurplus = hoursSurplus%60000;
		long seconds = minutesSurplus/1000;
		long miliseconds = minutesSurplus%1000;
		return (hours < 10 ? "0"+hours : hours) + ":" +
			   (minutes < 10 ? "0"+minutes : minutes) + ":" +
			   (seconds < 10 ? "0"+seconds : seconds) + "." +
			   (miliseconds < 100 ? "0"+miliseconds : miliseconds);
	}
}
