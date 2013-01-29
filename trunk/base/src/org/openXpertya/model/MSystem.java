/*
 * @(#)MSystem.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import org.openXpertya.db.CConnection;
import org.openXpertya.db.Database;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Ini;
import org.openXpertya.util.TimeUtil;

//~--- Importaciones JDK ------------------------------------------------------

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;
import java.util.logging.Level;

/**
 *      System Record (just one)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MSystem.java,v 1.15 2005/03/11 20:28:38 jjanke Exp $
 */
public class MSystem extends X_AD_System {

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MSystem.class);

    /** System - cached */
    private static MSystem	s_system	= null;

    /**
     *      Constructor
     */
    public MSystem() {
        this(new Properties(), 0, null);
    }		// MSystem

    /**
     *      Default Constructor
     *      @param ctx context
     *      @param ignored id
     * @param mtrxName
     */
    public MSystem(Properties ctx, int ignored, String mtrxName) {

        super(ctx, 0, mtrxName);

        String	trxName	= null;

        load(trxName);		// load ID=0

        if (s_system == null) {
            s_system	= this;
        }

    }		// MSystem

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MSystem(Properties ctx, ResultSet rs, String trxName) {

        super(ctx, rs, trxName);

        if (s_system == null) {
            s_system	= this;
        }

    }		// MSystem

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true/false
     */
    protected boolean beforeSave(boolean newRecord) {

        if (getName().equals("?") || (getName().length() < 2)) {

            log.saveError("Error", "Define a unique System name (e.g. Company name)");

            return false;
        }

        if (getUserName().equals("?") || (getUserName().length() < 2)) {

            log.saveError("Error", "Use the same email address as in the OpenXpertya Web Store");

            return false;
        }

        if (getPassword().equals("?") || (getPassword().length() < 2)) {

            log.saveError("Error", "Use the same password as in the OpenXpertya Web Store");

            return false;
        }

        //
        if (!Ini.isClient()) {
            setInfo();
        }

        return true;

    }		// beforeSave

    /**
     *      Test
     *      @param args
     */
    public static void main(String[] args) {
        new MSystem();
    }		// main

    /**
     *      Save Record (ID=0)
     *      @return true if saved
     */
    public boolean save() {
        return saveUpdate();
    }		// save

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {
        return "MSystem[" + getName() + ",User=" + getUserName() + "]";
    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Load System Record
     *      @param ctx context
     *      @return System
     */
    public static MSystem get(Properties ctx) {

        if (s_system != null) {
            return s_system;
        }

        //
        String			sql	= "SELECT * FROM AD_System ORDER BY AD_System_ID";	// 0 first
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                s_system	= new MSystem(ctx, rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            s_log.log(Level.SEVERE, "get", ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        //
        if (!Ini.isClient() && s_system.setInfo()) {
            s_system.save();
        }

        return s_system;

    }		// get

    /**
     *      Get DB Info SQL
     *      @param dbType database type
     *      @return sql
     */
    public static String getDBInfoSQL(String dbType) {

        if (Database.DB_ORACLE.equals(dbType)) {
            return "SELECT SYS_CONTEXT('USERENV','HOST') || '/' || SYS_CONTEXT('USERENV','IP_ADDRESS') AS DBAddress," + "     SYS_CONTEXT('USERENV','CURRENT_USER') || '.' || SYS_CONTEXT('USERENV','DB_NAME')" + " || '.' || SYS_CONTEXT('USERENV','DB_DOMAIN') AS DBName " + "FROM DUAL";
        }

        return "SELECT null, null FROM AD_System WHERE AD_System_ID=-1";

    }		// getDBInfoSQL

    /**
     *      Is there a PDF License
     *      @return true if there is a PDF License
     */
    public boolean isPDFLicense() {

        String	key	= getSummary();

        return (key != null) && (key.length() > 25);

    }		// isPDFLicense

    /**
     *      Check valididity
     *      @return true if valid
     */
    public boolean isValid() {

        if ((getName() == null) || (getName().length() < 2)) {

            log.log(Level.SEVERE, "Name not valid: " + getName());

            return false;
        }

        if ((getPassword() == null) || (getPassword().length() < 2)) {

            log.log(Level.SEVERE, "Password not valid: " + getPassword());

            return false;
        }

        if ((getInfo() == null) || (getInfo().length() < 2)) {

            log.log(Level.SEVERE, "Need to run Migration once");

            return false;
        }

        return true;

    }		// isValid

    //~--- set methods --------------------------------------------------------

    /**
     *      Set DB Info
     */
    private void setDBInfo() {

        // This box
        String	dbAddress	= null;
        String	dbName		= CConnection.get().getConnectionURL();
        int	noProcessors	= Runtime.getRuntime().availableProcessors();

        //
        PreparedStatement	pstmt	= null;

        try {

            String	dbType	= CConnection.get().getDatabase().getName();
            String	sql	= getDBInfoSQL(dbType);

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                dbAddress	= rs.getString(1);
                dbName		= rs.getString(2);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "setDBInfo", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        //
        if (dbAddress == null) {

            try {
                dbAddress	= InetAddress.getLocalHost().toString();
            } catch (UnknownHostException e1) {
                dbAddress	= "??";
            }
        }

        // Set
        setDBAddress(dbAddress.toLowerCase());
        setDBInstance(dbName.toLowerCase());
        setNoProcessors(noProcessors);
    }		// setDBInfo

    /**
     *      Set/Derive Info if more then a day old
     *      @return true if set
     */
    private boolean setInfo() {

        // log.severe("setInfo");
        if (!TimeUtil.getDay(getUpdated()).before(TimeUtil.getDay(null))) {
            return false;
        }

        try {

            setDBInfo();
            setInternalUsers();

        } catch (Exception e) {

            setSupportUnits(9999);
            setInfo(e.getLocalizedMessage());
            log.log(Level.SEVERE, "setInfo", e);
        }

        return true;
    }		// setInfo

    /**
     *      Set Internal User Count
     */
    private void setInternalUsers() {

        String	sql	= "SELECT COUNT(DISTINCT (u.AD_User_ID)) AS iu " + "FROM AD_User u" + " INNER JOIN AD_User_Roles ur ON (u.AD_User_ID=ur.AD_User_ID) " + "WHERE u.AD_Client_ID<>11"	// no Demo
                          + " AND u.AD_User_ID NOT IN (0,100)";		// no System/SuperUser
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                int	internalUsers	= rs.getInt(1);

                setSupportUnits(internalUsers);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "setInternalUsers", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

    }		// setInternalUsers
    
    private static final String SYSTEM_ALLOW_REMEMBER_PASSWORD = "P";
    public static boolean isZKRememberPasswordAllowed() {
		String ca = MSysConfig.getValue("ZK_LOGIN_ALLOW_REMEMBER_ME", SYSTEM_ALLOW_REMEMBER_USER);
		return (ca.equalsIgnoreCase(SYSTEM_ALLOW_REMEMBER_PASSWORD));
	}
	public static boolean isZKRememberUserAllowed() {
		String ca = MSysConfig.getValue("ZK_LOGIN_ALLOW_REMEMBER_ME", SYSTEM_ALLOW_REMEMBER_USER);
		return (ca.equalsIgnoreCase(SYSTEM_ALLOW_REMEMBER_USER) || ca.equalsIgnoreCase(SYSTEM_ALLOW_REMEMBER_PASSWORD));
	}
    private static final String SYSTEM_ALLOW_REMEMBER_USER = "U";

    
}	// MSystem



/*
 * @(#)MSystem.java   02.jul 2007
 * 
 *  Fin del fichero MSystem.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
