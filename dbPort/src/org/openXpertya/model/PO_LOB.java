/*
 * @(#)PO_LOB.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.interfaces.Server;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Trx;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.rmi.RemoteException;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.logging.Level;

/**
 *      Persistent Object LOB.
 *      Allows to store LOB remotely
 *      Currently Oracle specific!
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: PO_LOB.java,v 1.12 2005/03/11 20:28:38 jjanke Exp $
 */
public class PO_LOB implements Serializable {

    /** Logger */
    protected CLogger	log	= CLogger.getCLogger(getClass());

    /** Column Name */
    private String	m_columnName;

    /** Display Type */
    private int	m_displayType;

    /** Table Name */
    private String	m_tableName;

    /** Data */
    private Object	m_value;

    /** Where Clause */
    private String	m_whereClause;

    /**
     *      Constructor
     *      @param tableName table name
     *      @param columnName column name
     *      @param whereClause where
     *      @param displayType display type
     *      @param value value
     */
    public PO_LOB(String tableName, String columnName, String whereClause, int displayType, Object value) {

        m_tableName	= tableName;
        m_columnName	= columnName;
        m_whereClause	= whereClause;
        m_displayType	= displayType;
        m_value		= value;

    }		// PO_LOB

    /**
     *      Save LOB
     *      @param trxName trx name
     *      @return true if saved
     *      @see org.openXpertya.session.ServerBean#updateLOB
     */
    public boolean save(String trxName) {

        if ((m_value == null) || (!((m_value instanceof String) || (m_value instanceof byte[]))) || ((m_value instanceof String) && (m_value.toString().length() == 0)) || ((m_value instanceof byte[]) && ((byte[]) m_value).length == 0)) {

            StringBuffer	sql	= new StringBuffer("UPDATE ").append(m_tableName).append(" SET ").append(m_columnName).append("=null WHERE ").append(m_whereClause);
            int	no	= DB.executeUpdate(sql.toString(), trxName);

            log.fine("save [" + trxName + "] #" + no + " - no data - set to null - " + m_value);

            if (no == 0) {
                log.warning("save [" + trxName + "] - not updated - " + sql);
            }

            return true;
        }

        StringBuffer	sql	= new StringBuffer("UPDATE ").append(m_tableName).append(" SET ").append(m_columnName).append("=? WHERE ").append(m_whereClause);

        //
        boolean	success	= true;

        if (DB.isRemoteObjects()) {

            log.fine("save [" + trxName + "] - Remote - " + m_value);

            Server	server	= CConnection.get().getServer();

            try {

                if (server != null) {		// See ServerBean

                    success	= server.updateLOB(sql.toString(), m_displayType, m_value);

                    if (CLogMgt.isLevelFinest()) {
                        log.fine("save - server => " + success);
                    }

                    if (success) {
                        return true;
                    }
                }

                log.log(Level.SEVERE, "save - AppsServer not found");

            } catch (RemoteException ex) {
                log.log(Level.SEVERE, "save - AppsServer error", ex);
            }
        }

        log.fine("save [" + trxName + "] - Local - " + m_value);

        // Connection
        Trx	trx	= null;

        if (trxName != null) {
            trx	= Trx.get(trxName, false);
        }

        Connection	con	= null;

        // Create Connection
        if (trx != null) {
            con	= trx.getConnection();
        }

        if (con == null) {
            con	= DB.createConnection(false, Connection.TRANSACTION_READ_COMMITTED);
        }

        if (con == null) {

            log.log(Level.SEVERE, "Could not get Connection");

            return false;
        }

        PreparedStatement	pstmt	= null;

        success	= true;

        try {

            pstmt	= con.prepareStatement(sql.toString());

            if (m_displayType == DisplayType.TextLong) {
                pstmt.setString(1, (String) m_value);
            } else {
                pstmt.setBytes(1, (byte[]) m_value);
            }

            int	no	= pstmt.executeUpdate();

            if (no != 1) {

                log.fine("save [" + trxName + "] - Not updated #" + no + " - " + sql);
                success	= false;
            }

            //
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {

            log.log(Level.FINE, "[" + trxName + "] - " + sql, e);
            success	= false;
        }

        // Close Statement
        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        // Success - commit local trx
        if (success) {

            if (trx != null) {

                trx	= null;
                con	= null;

            } else {

                try {

                    con.commit();
                    con.close();
                    con	= null;

                } catch (Exception e) {

                    log.log(Level.SEVERE, "save [" + trxName + "] - commit ", e);
                    success	= false;
                }
            }
        }

        // Error - roll back
        if (!success) {

            log.severe("save [" + trxName + "] - rollback");

            if (trx != null) {

                trx.rollback();
                trx	= null;
                con	= null;

            } else {

                try {

                    con.rollback();
                    con.close();
                    con	= null;

                } catch (Exception ee) {
                    log.log(Level.SEVERE, "save [" + trxName + "] - rollback", ee);
                }
            }
        }

        // Clean Connection
        try {

            if (con != null) {
                con.close();
            }

            con	= null;

        } catch (Exception e) {
            con	= null;
        }

        return success;

    }		// save

    /**
     *      Save LOB
     *
     * @param whereClause
     *      @param trxName trx name
     *      @return true if saved
     */
    public boolean save(String whereClause, String trxName) {

        m_whereClause	= whereClause;

        return save(trxName);

    }		// save

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("PO_LOB[");

        sb.append(m_tableName).append(".").append(m_columnName).append(",DisplayType=").append(m_displayType).append("]");

        return sb.toString();

    }		// toString
}	// PO_LOB



/*
 * @(#)PO_LOB.java   02.jul 2007
 * 
 *  Fin del fichero PO_LOB.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
