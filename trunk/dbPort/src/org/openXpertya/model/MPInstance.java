/*
 * @(#)MPInstance.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Process Instance Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MPInstance.java,v 1.14 2005/05/08 15:17:13 jjanke Exp $
 */
public class MPInstance extends X_AD_PInstance {

    /** Descripción de Campo */
    public static final int	RESULT_OK	= 1;

    /** Descripción de Campo */
    public static final int	RESULT_ERROR	= 0;

    /** Parameters */
    private MPInstancePara[]	m_parameters	= null;

    /** Log Entries */
    private ArrayList	m_log	= new ArrayList();

    /**
     *      Create Process Instance from Process and create parameters
     *      @param process process
     *      @param Record_ID Record
     */
    //Añadido por ConSerTi
    public static int getAD_PInstance_ID (Properties ctx, int AD_Process_ID, int Record_ID)
	{
		MPInstance instance = new MPInstance (ctx, 0, null);
		instance.setAD_Process_ID(AD_Process_ID);
		instance.setRecord_ID(Record_ID);
		instance.setAD_User_ID(Env.getAD_User_ID(ctx));
		instance.save();
		return instance.getAD_PInstance_ID();
	}	//	getAD_Instance_ID
    //Fin Añadido
    
    public MPInstance(MProcess process, int Record_ID) {

        super(process.getCtx(), 0, process.get_TrxName());
        setAD_Process_ID(process.getAD_Process_ID());
        setRecord_ID(Record_ID);
        setAD_User_ID(Env.getAD_User_ID(process.getCtx()));

        if (!save()) {		// need to save for parameters
            throw new IllegalArgumentException("MPInstance - Cannot Save");
        }

        // Set Parameter Base Info
        MProcessPara[]	para	= process.getParameters();

        for (int i = 0; i < para.length; i++) {

            MPInstancePara	pip	= new MPInstancePara(this, para[i].getSeqNo());

            pip.setParameterName(para[i].getColumnName());
            pip.setInfo(para[i].getName());
            pip.save();
        }

    }		// MPInstance

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_PInstance_ID instance or 0
     * @param trxName
     */
    public MPInstance(Properties ctx, int AD_PInstance_ID, String trxName) {

        super(ctx, AD_PInstance_ID, trxName);

        if (AD_PInstance_ID == 0) {

            // setAD_Process_ID (0);   //      parent
            setRecord_ID(0);
            setIsProcessing(false);
        }

    }		// MPInstance

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MPInstance(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MPInstance

    /**
     *      New Constructor
     *      @param ctx context
     *      @param AD_Process_ID Process ID
     *      @param Record_ID record
     * @param trxName
     */
    public MPInstance(Properties ctx, int AD_Process_ID, int Record_ID, String trxName) {

        super(ctx, 0, trxName);
        setAD_Process_ID(AD_Process_ID);
        setRecord_ID(Record_ID);
        setAD_User_ID(Env.getAD_User_ID(ctx));
        setIsProcessing(false);

    }		// MPInstance

    /**
     *      @param P_Date date
     *      @param P_ID id
     *      @param P_Number number
     *      @param P_Msg msg
     */
    public void addLog(Timestamp P_Date, int P_ID, BigDecimal P_Number, String P_Msg) {

        MPInstanceLog	logEntry	= new MPInstanceLog(getAD_PInstance_ID(), m_log.size() + 1, P_Date, P_ID, P_Number, P_Msg);

        m_log.add(logEntry);

        // save it to DB ?
        // log.save();

    }		// addLog

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        // Update Statistics
        if (!newRecord &&!isProcessing() && is_ValueChanged("IsProcessing")) {

            long	ms	= System.currentTimeMillis() - getCreated().getTime();
            int		seconds	= (int) (ms / 1000);

            if (seconds < 1) {
                seconds	= 1;
            }

            MProcess	prc	= new MProcess(getCtx(), getAD_Process_ID(), get_TrxName());

            prc.addStatistics(seconds);

            if ((prc.getID() != 0) && prc.save()) {
                log.fine("afterSave - Process Statistics updated Sec=" + seconds);
            } else {
                log.warning("afterSave - Process Statistics not updated");
            }
        }

        return success;
    }		// afterSave

    /**
     *      Dump Log
     */
    public void log() {

        log.info(toString());

        MPInstanceLog[]	pil	= getLogger();

        for (int i = 0; i < pil.length; i++) {
            log.info(i + "=" + pil[i]);
        }

    }		// log

    /**
     *      String Representation
     *      @see java.lang.Object#toString()
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MPInstance[").append(getID()).append(",OK=").append(isOK());
        String	msg	= getErrorMsg();

        if ((msg != null) && (msg.length() > 0)) {
            sb.append(msg);
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Logs
     *      @return array of logs
     */
    public MPInstanceLog[] getLogger() {

        // load it from DB
        m_log.clear();

        String	sql	= "SELECT * FROM AD_PInstance_Log WHERE AD_PInstance_ID=? ORDER BY Log_ID";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, getAD_PInstance_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                m_log.add(new MPInstanceLog(rs));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLog", e);
        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (Exception e) {}

            pstmt	= null;
        }

        MPInstanceLog[]	retValue	= new MPInstanceLog[m_log.size()];

        m_log.toArray(retValue);

        return retValue;
    }		// getLog

    /**
     *      Get Parameters
     *      @return parameter array
     */
    public MPInstancePara[] getParameters() {

        if (m_parameters != null) {
            return m_parameters;
        }

        ArrayList	list	= new ArrayList();

        //
        String			sql	= "SELECT * FROM AD_PInstance_Para WHERE AD_PInstance_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, getAD_PInstance_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MPInstancePara(getCtx(), rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (Exception e) {}

            pstmt	= null;
        }

        //
        m_parameters	= new MPInstancePara[list.size()];
        list.toArray(m_parameters);

        return m_parameters;

    }		// getParameters

    /**
     *      Is it OK
     *      @return Result == OK
     */
    public boolean isOK() {
        return getResult() == RESULT_OK;
    }		// isOK

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Result
     *      @param ok
     */
    public void setResult(boolean ok) {

        super.setResult(ok
                        ? RESULT_OK
                        : RESULT_ERROR);

    }		// setResult
}	// MPInstance



/*
 * @(#)MPInstance.java   02.jul 2007
 * 
 *  Fin del fichero MPInstance.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
