/*
 * @(#)MSession.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CCache;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.TimeUtil;

//~--- Importaciones JDK ------------------------------------------------------

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.sql.ResultSet;

import java.util.Properties;
import java.util.logging.Level;

/**
 *      Session Model.
 *      Maintained in AMenu.
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MSession.java,v 1.12 2005/05/14 05:32:16 jjanke Exp $
 */
public class MSession extends X_AD_Session {

    /** Sessions */
    private static CCache	s_sessions	= Ini.isClient()
            ? new CCache("AD_Session_ID", 1, 0)		// one client session
            : new CCache("AD_Session_ID", 30, 0);	// no time-out

    /** Web Store Session */
    private boolean	m_webStoreSession	= false;

    /**
     *      New (local) Constructor
     *      @param ctx context
     * @param trxName
     */
    public MSession(Properties ctx, String trxName) {

        this(ctx, 0, trxName);

        try {

            InetAddress	lh	= InetAddress.getLocalHost();

            setRemote_Addr(lh.getHostAddress());
            setRemote_Host(lh.getHostName());

        } catch (UnknownHostException e) {
            log.log(Level.SEVERE, "MSession - No Local Host", e);
        }

    }		// MSession

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Session_ID id
     * @param trxName
     */
    public MSession(Properties ctx, int AD_Session_ID, String trxName) {

        super(ctx, AD_Session_ID, trxName);

        if (AD_Session_ID == 0) {
            setProcessed(false);
        }

    }		// MSession

    /**
     *      Load Costructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MSession(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MSession

    /**
     *      New (remote) Constructor
     *      @param ctx context
     * @param Remote_Addr
     * @param Remote_Host
     * @param WebSession
     * @param trxName
     */
    public MSession(Properties ctx, String Remote_Addr, String Remote_Host, String WebSession, String trxName) {

        this(ctx, 0, trxName);

        if (Remote_Addr != null) {
            setRemote_Addr(Remote_Addr);
        }

        if (Remote_Host != null) {
            setRemote_Host(Remote_Host);
        }

        if (WebSession != null) {
            setWebSession(WebSession);
        }

    }		// MSession

    /**
     *      Create Change Log only if table is logged
     *      @param TrxName transaction name
     *      @param AD_ChangeLog_ID 0 for new change log
     *      @param AD_Table_ID table
     *      @param AD_Column_ID column
     *      @param Record_ID record
     *      @param AD_Client_ID client
     *      @param AD_Org_ID org
     *      @param OldValue old
     *      @param NewValue new
     *      @return saved change log or null
     */
    public MChangeLog changeLog(String TrxName, int AD_ChangeLog_ID, int AD_Table_ID, int AD_Column_ID, int Record_ID, int AD_Client_ID, int AD_Org_ID, Object OldValue, Object NewValue, String valueUID, Integer valueComponentVersionID, String operationType, Integer displayType, Integer changeLogGroupID) throws Exception{

        // Null handling
        if ((OldValue == null) && (NewValue == null) && !(operationType.equals(MChangeLog.OPERATIONTYPE_Deletion))) {
            return null;
        }

        // Equal Value
        if ((OldValue != null) && (NewValue != null) && OldValue.equals(NewValue) && !(operationType.equals(MChangeLog.OPERATIONTYPE_Deletion))) {
            return null;
        }

        // Role Logging
        MRole	role	= MRole.getDefault(getCtx(), false);

        // Do we need to log
        if (m_webStoreSession					// log if WebStore
                || MChangeLog.isLogged(AD_Table_ID)		// im/explicit log
                || ((role != null) && role.isChangeLog())) {	// Role Logging
            ;
        } else {
            return null;
        }

        //
        log.finest("AD_ChangeLog_ID=" + AD_ChangeLog_ID + ", AD_Session_ID=" + getAD_Session_ID() + ", AD_Table_ID=" + AD_Table_ID + ", AD_Column_ID=" + AD_Column_ID + ": " + OldValue + " -> " + NewValue);

        boolean	success	= false;
        MChangeLog	cl = null;
        try {

            cl	= new MChangeLog(getCtx(), AD_ChangeLog_ID, TrxName, getAD_Session_ID(), AD_Table_ID, AD_Column_ID, Record_ID, AD_Client_ID, AD_Org_ID, OldValue, NewValue, valueUID, valueComponentVersionID, operationType, displayType, changeLogGroupID);

            if (!cl.insertDirect()) {
                throw new Exception("No se pudo registrar el log de cambios");
            }

        } catch (Exception e) {

            log.log(Level.SEVERE, "AD_ChangeLog_ID=" + AD_ChangeLog_ID + ", AD_Session_ID=" + getAD_Session_ID() + ", AD_Table_ID=" + AD_Table_ID + ", AD_Column_ID=" + AD_Column_ID, e);

            return null;
        }

        return cl;
    }		// changeLog

    /**
     *      Session Logout
     */
    public void logout() {

        setProcessed(true);
        save();
        s_sessions.remove(new Integer(getAD_Session_ID()));
        log.info("logout - " + TimeUtil.formatElapsed(getCreated(), getUpdated()));

    }		// logout

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MSession[").append(getAD_Session_ID()).append(",AD_User_ID=").append(getCreatedBy()).append(",").append(getCreated()).append(",Remote=").append(getRemote_Addr());
        String	s	= getRemote_Host();

        if ((s != null) && (s.length() > 0)) {
            sb.append(",").append(s);
        }

        if (m_webStoreSession) {
            sb.append(",WebStoreSession");
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get existing or create local session
     *      @param ctx context
     *      @param createNew create if not found
     *      @return session session
     */
    public static MSession get(Properties ctx, boolean createNew) {

        int		AD_Session_ID	= Env.getContextAsInt(ctx, "#AD_Session_ID");
        MSession	session		= null;

        if (AD_Session_ID > 0) {
            session	= (MSession) s_sessions.get(new Integer(AD_Session_ID));
        }

        if ((session == null) && createNew) {

            session	= new MSession(ctx, null);	// local session
            session.save();
            AD_Session_ID	= session.getAD_Session_ID();
            Env.setContext(ctx, "#AD_Session_ID", AD_Session_ID);
            s_sessions.put(new Integer(AD_Session_ID), session);
        }

        return session;

    }		// get

    /**
     *      Get existing or create remote session
     *      @param ctx context
     * @param Remote_Addr
     * @param Remote_Host
     * @param WebSession
     *
     * @return
     */
    public static MSession get(Properties ctx, String Remote_Addr, String Remote_Host, String WebSession) {

        int		AD_Session_ID	= Env.getContextAsInt(ctx, "#AD_Session_ID");
        MSession	session		= null;

        if (AD_Session_ID > 0) {
            session	= (MSession) s_sessions.get(new Integer(AD_Session_ID));
        }

        if (session == null) {

            session	= new MSession(ctx, Remote_Addr, Remote_Host, WebSession, null);	// remote session
            session.save();
            AD_Session_ID	= session.getAD_Session_ID();
            Env.setContext(ctx, "#AD_Session_ID", AD_Session_ID);
            s_sessions.put(new Integer(AD_Session_ID), session);
        }

        return session;

    }		// get

    /**
     *      Is it a Web Store Session
     *      @return Returns true if Web Store Session.
     */
    public boolean isWebStoreSession() {
        return m_webStoreSession;
    }		// isWebStoreSession

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Web Store Session
     *      @param webStoreSession The webStoreSession to set.
     */
    public void setWebStoreSession(boolean webStoreSession) {
        m_webStoreSession	= webStoreSession;
    }		// setWebStoreSession
}	// MSession



/*
 * @(#)MSession.java   02.jul 2007
 * 
 *  Fin del fichero MSession.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
