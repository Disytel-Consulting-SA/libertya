/*
 * @(#)MPInstanceLog.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *  Process Instance Log Model.
 *      (not standard table)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MPInstanceLog.java,v 1.3 2005/03/11 20:28:35 jjanke Exp $
 */
public class MPInstanceLog {

    /** Descripción de Campo */
    private int	m_AD_PInstance_ID;

    /** Descripción de Campo */
    private int	m_Log_ID;

    /** Descripción de Campo */
    private Timestamp	m_P_Date;

    /** Descripción de Campo */
    private int	m_P_ID;

    /** Descripción de Campo */
    private String	m_P_Msg;

    /** Descripción de Campo */
    private BigDecimal	m_P_Number;

    /**
     *      Load Constructor
     *      @param rs Result Set
     *      @throws SQLException
     */
    public MPInstanceLog(ResultSet rs) throws SQLException {

        setAD_PInstance_ID(rs.getInt("AD_PInstance_ID"));
        setLog_ID(rs.getInt("Log_ID"));
        setP_Date(rs.getTimestamp("P_Date"));
        setP_ID(rs.getInt("P_ID"));
        setP_Number(rs.getBigDecimal("P_Number"));
        setP_Msg(rs.getString("P_Msg"));

    }		// MPInstance_Log

    /**
     *      Full Constructor
     *      @param AD_PInstance_ID instance
     *      @param Log_ID log sequence
     *      @param P_Date date
     *      @param P_ID id
     *      @param P_Number number
     *      @param P_Msg msg
     */
    public MPInstanceLog(int AD_PInstance_ID, int Log_ID, Timestamp P_Date, int P_ID, BigDecimal P_Number, String P_Msg) {

        setAD_PInstance_ID(AD_PInstance_ID);
        setLog_ID(Log_ID);
        setP_Date(P_Date);
        setP_ID(P_ID);
        setP_Number(P_Number);
        setP_Msg(P_Msg);

    }		// MPInstance_Log

    /**
     *      Save to Database
     *      @return true if saved
     */
    public boolean save() {

        StringBuffer	sql	= new StringBuffer("INSERT INTO AD_PInstance_Log " + "(AD_PInstance_ID, Log_ID, P_Date, P_ID, P_Number, P_Msg)" + " VALUES (");

        sql.append(m_AD_PInstance_ID).append(",").append(m_Log_ID).append(",");

        if (m_P_Date == null) {
            sql.append("NULL,");
        } else {
            sql.append(DB.TO_DATE(m_P_Date, false)).append(",");
        }

        if (m_P_ID == 0) {
            sql.append("NULL,");
        } else {
            sql.append(m_P_ID).append(",");
        }

        if (m_P_Number == null) {
            sql.append("NULL,");
        } else {
            sql.append(m_P_Number).append(",");
        }

        if (m_P_Msg == null) {
            sql.append("NULL)");
        } else {

            sql.append(DB.TO_STRING(m_P_Msg, 2000)).append(")");

            //
        }

        int	no	= DB.executeUpdate(sql.toString());

        return no == 1;

    }		// save

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("PPInstance_Log[");

        sb.append(m_Log_ID);

        if (m_P_Date != null) {
            sb.append(",Date=").append(m_P_Date);
        }

        if (m_P_ID != 0) {
            sb.append(",ID=").append(m_P_ID);
        }

        if (m_P_Number != null) {
            sb.append(",Number=").append(m_P_Number);
        }

        if (m_P_Msg != null) {
            sb.append(",").append(m_P_Msg);
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getAD_PInstance_ID() {
        return m_AD_PInstance_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getLog_ID() {
        return m_Log_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public Timestamp getP_Date() {
        return m_P_Date;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getP_ID() {
        return m_P_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getP_Msg() {
        return m_P_Msg;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public BigDecimal getP_Number() {
        return m_P_Number;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param AD_PInstance_ID
     */
    public void setAD_PInstance_ID(int AD_PInstance_ID) {
        m_AD_PInstance_ID	= AD_PInstance_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param Log_ID
     */
    public void setLog_ID(int Log_ID) {
        m_Log_ID	= Log_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param P_Date
     */
    public void setP_Date(Timestamp P_Date) {
        m_P_Date	= P_Date;
    }

    /**
     * Descripción de Método
     *
     *
     * @param P_ID
     */
    public void setP_ID(int P_ID) {
        m_P_ID	= P_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param P_Msg
     */
    public void setP_Msg(String P_Msg) {
        m_P_Msg	= P_Msg;
    }

    /**
     * Descripción de Método
     *
     *
     * @param P_Number
     */
    public void setP_Number(BigDecimal P_Number) {
        m_P_Number	= P_Number;
    }
}	// MPInstance_Log



/*
 * @(#)MPInstanceLog.java   02.jul 2007
 * 
 *  Fin del fichero MPInstanceLog.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
