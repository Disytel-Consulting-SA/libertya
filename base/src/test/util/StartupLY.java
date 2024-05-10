package test.util;

import java.util.Properties;

import org.openXpertya.db.CConnection;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Secure;


/**
 * Codigo perteneciente a proyecto LYRestAPI
 */
public class StartupLY {
	
	private static StartupLY instance;
	private String dbHost = "localhost";
	private String dbPort = "5432";
	private String dbName = "testingautomation";
	private String dbUser = "libertya";
	private String dbPass = "libertya";
	private String clientID = "1010016";
	//1010016, 1010053
	

	//singleton
	private StartupLY() { }
	//singleton
    public static StartupLY getInstance() {
        if (instance == null) {
            instance = new StartupLY();
        }
        return instance;
    }
	
	
	public void init() throws Exception {
		setConnection();
        startupEnvironment();
	}
	
	
	/**
     * Realiza la configuración inicial a partir de la información recibida
     * @throws Exception en caso de error o rechazo
     */
	protected void setConnection() {
        Ini.getProperties().put(Ini.P_CONNECTION,
                Secure.CLEARTEXT +
                        "CConnection["
                        + "name=localhost{DEVELOPMENT-DEVELOPMENT},"
                        + "AppsHost=localhost,"
                        + "AppsPort=1099,"
                        + "RMIoverHTTP=false,"
                        + "type=PostgreSQL,"
                        + "DBhost="+dbHost+","
                        + "DBport="+dbPort+","
                        + "DBname="+dbName+","
                        + "BQ=false,"
                        + "FW=false,"
                        + "FWhost=,"
                        + "FWport=0,"
                        + "UID="+dbUser+","
                        + "PWD="+dbPass+"]");
    }
	
	 protected void startupEnvironment() throws Exception {
	        Env.setContext(Env.getCtx(), "#AD_Language", "es_AR");
	        Env.setContext(Env.getCtx(), "#AD_Client_ID", clientID);
	        Env.setContext(Env.getCtx(), "#AD_Org_ID", 0);
	        if (!setup())
	            throw new Exception ("Error al iniciar entorno (Hay conexión a Base de Datos?) ");
	        setCurrency(Env.getCtx());
	        setUseDefaults(Env.getCtx());
	        setReferencedValues(Env.getCtx());
	 }
	
	 protected boolean setup() {
	        if (DB.isConnected())
	            return true;
	        // La gestion de log corre por cuenta del aspect EventLogAspect
	        CLogMgt.shutdown();
	        // Gestion server-side
	        Ini.setClient(false);
	        // Conectar a BDD
	        CConnection cc = CConnection.get();
	        DB.setDBTarget(cc);
	        return DB.isConnected();
	    }

	    /**
	     * Setea la moneda de la compañía en el contexto
	     * @throws Exception en caso de error
	     */
	    protected void setCurrency(Properties ctx) {
	        // Incorporar al contexto la moneda de la compañía
	        String sql = 	" SELECT C_Currency_ID " +
	                " FROM C_AcctSchema a, AD_ClientInfo c " +
	                " WHERE a.C_AcctSchema_ID=c.C_AcctSchema1_ID " +
	                " AND c.AD_Client_ID = " + Env.getAD_Client_ID(ctx);
	        int currencyID = DB.getSQLValue(null, sql);
	        Env.setContext(ctx, "$C_Currency_ID", currencyID);
	    }

	    /** Usar valores por defecto definidos en los metadatos de las columnas? */
	    protected void setUseDefaults(Properties ctx) {
	        //Env.setContext(ctx, "#USE_DEFAULTS", useDefaults);
	    }

	    /** Retornar tambien los valores de las columnas que referencias a registos otras tablas? */
	    protected void setReferencedValues(Properties ctx) {
	        //Env.setContext(ctx, "#USE_REFERENCED_VALUES", referencedValues);
	    }

}
