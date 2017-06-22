package org.openXpertya.util;

import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import org.postgresql.PGConnection;



public class PGStatementUtils  implements Runnable {
	
	/** Instancia unica */
	protected static PGStatementUtils instance;
	/** Tiempo de sleep entre ejecucion y ejecucion */
	protected static int sleepIntervalMilis = 30 * 1000;
	/** Pids a mantener activos */
	protected static HashMap<Integer, HashSet<Integer>> pidsBySession = new HashMap<Integer, HashSet<Integer>>();
	/** Logger */
	private static CLogger log = CLogger.getCLogger( PGStatementUtils.class );
	
	/** Singleton. La primera invocacion inicializa el thread de ejecucion */
	public static PGStatementUtils getInstance() {
		if (instance==null) {
			instance=new PGStatementUtils();
			Thread thread = new Thread(instance);
			thread.start();
		}
		return instance;
	}
	
	/** Incorporar un statement a la nomina */
	public void addStatement(int sessionID, Statement stmt) {
		int pid = 0;
		try {
			pid = ((PGConnection)stmt.getConnection()).getBackendPID();
			log.fine("KA+ " + sessionID + "-" + pid);
			synchronized (pidsBySession) {
				if (pidsBySession.get(sessionID) == null)
					pidsBySession.put(sessionID, new HashSet<Integer>());
				pidsBySession.get(sessionID).add(pid);
			}
			DB.executeUpdate("INSERT INTO AD_KeepAlive (ad_session_id, pid, created, updated) VALUES (" + sessionID + ", " + pid + ", now(), now())");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}
	}
	
	public void removeStatement(int sessionID, Statement stmt) {
		int pid = 0;
		try {
			synchronized (pidsBySession) {
				pid = ((PGConnection)stmt.getConnection()).getBackendPID();
				log.fine("KA- " + sessionID + "-" + pid);
				if (pidsBySession.get(sessionID) != null) {
					pidsBySession.get(sessionID).remove(pid);
					if (pidsBySession.get(sessionID).size()==0) {
						pidsBySession.remove(sessionID);
					}
				}
			}
			DB.executeUpdate("DELETE FROM ad_keepalive WHERE ad_session_id = " + sessionID + " AND pid = " + pid);
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
	}
	
	public void removeAllStatements(int sessionID) {
		log.fine("KA- " + sessionID);
		synchronized (pidsBySession) {
			pidsBySession.remove(sessionID);
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				synchronized (pidsBySession) {
					if (pidsBySession.size() > 0) {
						StringBuffer query = new StringBuffer();
						// Recorrer todas las sesiones
						for (Integer sessionID : pidsBySession.keySet()) {
							if (pidsBySession.get(sessionID)==null || pidsBySession.get(sessionID).size() ==0) {
								pidsBySession.remove(sessionID);
								continue;
							}
							// Recorrer todos los pids de cada sesion
							for (Object pid : pidsBySession.get(sessionID).toArray()) {
								query.append("'").append(sessionID).append("-").append((Integer)pid).append("',");
							}
						}
						log.fine("KA* " + query);
						DB.executeUpdate("UPDATE AD_KeepAlive SET updated = now() WHERE ad_session_id::varchar || '-' || pid::varchar IN (" + query.toString() + "'')");
					}
				}
				Thread.sleep(sleepIntervalMilis);
			} catch (Exception e) {
				log.severe(e.getMessage());
			}
		}
	}

}
