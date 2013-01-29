package org.openXpertya.util;

import java.util.logging.Level;

public class TimeStatsLevel extends Level {
	
	/** Serial UID Generado */
	private static final long serialVersionUID = 303714448339262572L;

	/** Nivel: Time Stats (Estadísticas de Tiempo) */
	public static final Level TSTATS = new TimeStatsLevel("TSTATS", 850);
	
	/**
	 * @param name
	 * @param value
	 */
	protected TimeStatsLevel(String name, int value) {
		super(name, value);
	}
}
