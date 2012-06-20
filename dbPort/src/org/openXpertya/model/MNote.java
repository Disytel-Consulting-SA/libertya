/*
 * @(#)MNote.java   12.oct 2007  Versión 2.2
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

import java.sql.ResultSet;

import java.util.Properties;
import java.util.logging.Level;

/**
 *  Note Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MNote.java,v 1.13 2005/03/11 20:28:35 jjanke Exp $
 */
public class MNote extends X_AD_Note {

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Note_ID id
     * @param trxName
     */
    public MNote(Properties ctx, int AD_Note_ID, String trxName) {

        super(ctx, AD_Note_ID, trxName);

        if (AD_Note_ID == 0) {

            setProcessed(false);
            setProcessing(false);
        }

    }		// MNote

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MNote(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MNote

    /**
     *  New Mandatory Constructor
     *      @param ctx context
     *  @param AD_Message_ID message
     *  @param AD_User_ID targeted user
     * @param trxName
     */
    public MNote(Properties ctx, int AD_Message_ID, int AD_User_ID, String trxName) {

        this(ctx, 0, trxName);
        setAD_Message_ID(AD_Message_ID);
        setAD_User_ID(AD_User_ID);

    }		// MNote

    /**
     *  New Mandatory Constructor
     *      @param ctx context
     *  @param AD_MessageValue message
     *  @param AD_User_ID targeted user
     * @param trxName
     */
    public MNote(Properties ctx, String AD_MessageValue, int AD_User_ID, String trxName) {
        this(ctx, MMessage.getAD_Message_ID(ctx, AD_MessageValue), AD_User_ID, trxName);
    }		// MNote

    /**
     *  New Constructor
     *      @param ctx context
     *  @param AD_MessageValue message
     *  @param AD_User_ID targeted user
     *  @param AD_Client_ID client
     *      @param AD_Org_ID org
     * @param trxName
     */
    public MNote(Properties ctx, String AD_MessageValue, int AD_User_ID, int AD_Client_ID, int AD_Org_ID, String trxName) {

        this(ctx, MMessage.getAD_Message_ID(ctx, AD_MessageValue), AD_User_ID, trxName);
        setClientOrg(AD_Client_ID, AD_Org_ID);

    }		// MNote

    /**
     *      Create Note
     *      @param ctx context
     *      @param AD_Message_ID message
     *      @param AD_User_ID user
     *      @param AD_Table_ID table
     *      @param Record_ID record
     *      @param TextMsg text message
     *      @param Reference reference
     * @param trxName
     */
    public MNote(Properties ctx, int AD_Message_ID, int AD_User_ID, int AD_Table_ID, int Record_ID, String Reference, String TextMsg, String trxName) {

        this(ctx, AD_Message_ID, AD_User_ID, trxName);
        setRecord(AD_Table_ID, Record_ID);
        setReference(Reference);
        setTextMsg(TextMsg);

    }		// MNote

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MNote[").append(getID()).append(",AD_Message_ID=").append(getAD_Message_ID()).append(",").append(getReference()).append(",Processed=").append(isProcessed()).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Message
     *      @return message
     */
    public String getMessage() {

        int		AD_Message_ID	= getAD_Message_ID();
        MMessage	msg		= MMessage.get(getCtx(), AD_Message_ID);

        return msg.getMsgText();

    }		// getMessage

    //~--- set methods --------------------------------------------------------

    /**
     *      Set AD_Message_ID.
     *      Looks up No Message Found if 0
     *      @param AD_Message_ID id
     */
    public void setAD_Message_ID(int AD_Message_ID) {

        if (AD_Message_ID == 0) {
            super.setAD_Message_ID(MMessage.getAD_Message_ID(getCtx(), "NoMessageFound"));
        } else {
            super.setAD_Message_ID(AD_Message_ID);
        }

    }		// setAD_Message_ID

    /**
     *      Set Record.
     *      (Ss Button and defaults to String)
     *      @param AD_Message AD_Message
     */
    public void setAD_Message_ID(String AD_Message) {

        int	AD_Message_ID	= DB.getSQLValue(null, "SELECT AD_Message_ID FROM AD_Message WHERE Value=?", AD_Message);

        if (AD_Message_ID != -1) {
            super.setAD_Message_ID(AD_Message_ID);
        } else {

            super.setAD_Message_ID(240);	// Error
            log.log(Level.SEVERE, "setAD_Message_ID - ID not found for '" + AD_Message + "'");
        }

    }		// setRecord_ID

    /**
     *      Set Record
     *      @param AD_Table_ID table
     *      @param Record_ID record
     */
    public void setRecord(int AD_Table_ID, int Record_ID) {

        setAD_Table_ID(AD_Table_ID);
        setRecord_ID(Record_ID);

    }		// setRecord
}	// MNote



/*
 * @(#)MNote.java   02.jul 2007
 * 
 *  Fin del fichero MNote.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
