/*
 * @(#)MProcessPara.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Properties;
import java.util.logging.Level;

/**
 *  Process Parameter Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MProcessPara.java,v 1.6 2005/03/11 20:28:36 jjanke Exp $
 */
public class MProcessPara extends X_AD_Process_Para {

    /** Descripción de Campo */
    public static int	WINDOW_NO	= 999;

    /** Descripción de Campo */
    public static int	TAB_NO	= 0;

    /** The Lookup */
    private Lookup	m_lookup	= null;

    /**
     *      Constructor
     *      @param ctx context
     *      @param AD_Process_Para_ID id
     * @param trxName
     */
    public MProcessPara(Properties ctx, int AD_Process_Para_ID, String trxName) {

        super(ctx, AD_Process_Para_ID, trxName);

        if (AD_Process_Para_ID == 0) {

            // setAD_Process_ID (0);   Parent
            // setName (null);
            // setColumnName (null);
            setFieldLength(0);
            setSeqNo(0);
            setAD_Reference_ID(0);
            setIsCentrallyMaintained(true);
            setIsRange(false);
            setIsMandatory(false);
            setEntityType(ENTITYTYPE_UserMaintained);
        }

    }		// MProcessPara

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MProcessPara(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MProcessPara

    /**
     *  Set Lookup for columns with lookup
     */
    public void loadLookup() {

        if (!isLookup()) {
            return;
        }

        log.fine("(" + getColumnName() + ")");

        int	displayType	= getAD_Reference_ID();

        if (DisplayType.isLookup(displayType)) {

            MLookupInfo	lookupInfo	= MLookupFactory.getLookupInfo(getCtx(), 0, getAD_Process_Para_ID(), getAD_Reference_ID(), Env.getLanguage(getCtx()), getColumnName(), getAD_Reference_Value_ID(), false, "");

            if (lookupInfo == null) {

                log.log(Level.SEVERE, "(" + getColumnName() + ") - No LookupInfo");

                return;
            }

            // Prevent loading of CreatedBy/UpdatedBy
            if ((displayType == DisplayType.Table) && (getColumnName().equals("CreatedBy") || getColumnName().equals("UpdatedBy"))) {
                lookupInfo.IsCreadedUpdatedBy	= true;
            }

            //
            MLookup	ml	= new MLookup(lookupInfo, TAB_NO);

            m_lookup	= ml;

        } else if (displayType == DisplayType.Location)		// not cached
        {

            MLocationLookup	ml	= new MLocationLookup(getCtx(), WINDOW_NO);

            m_lookup	= ml;

        } else if (displayType == DisplayType.Locator) {

            MLocatorLookup	ml	= new MLocatorLookup(getCtx(), WINDOW_NO);

            m_lookup	= ml;

        } else if (displayType == DisplayType.Account)		// not cached
        {

            MAccountLookup	ma	= new MAccountLookup(getCtx(), WINDOW_NO);

            m_lookup	= ma;

        } else if (displayType == DisplayType.PAttribute)	// not cached
        {

            MPAttributeLookup	pa	= new MPAttributeLookup(getCtx(), WINDOW_NO);

            m_lookup	= pa;
        }

        //
        if (m_lookup != null) {
            m_lookup.loadComplete();
        }

    }		// loadLookup

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MProcessPara[").append(getID()).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Lookup for Parameter
     *      @return lookup or null
     */
    public Lookup getLookup() {

        if ((m_lookup == null) && isLookup()) {
            loadLookup();
        }

        return m_lookup;

    }		// getLookup

    /**
     *  Is this field a Lookup?.
     *  @return true if lookup field
     */
    public boolean isLookup() {

        boolean	retValue	= false;
        int	displayType	= getAD_Reference_ID();

        if (DisplayType.isLookup(displayType)) {
            retValue	= true;
        } else if ((displayType == DisplayType.Location) || (displayType == DisplayType.Locator) || (displayType == DisplayType.Account) || (displayType == DisplayType.PAttribute)) {
            retValue	= true;
        }

        return retValue;

    }		// isLookup
    
    @Override
    protected boolean beforeSave(boolean newRecord) {
    	// No puede estar marcado en misma línea cuando es el primer parámetro
    	Integer minSeqNo = getMinSeqNo();
    	if(isSameLine() && (minSeqNo == null || minSeqNo == getSeqNo())){
    		log.saveError("SameLineInFirstParameter","");
    		return false;
    	}
		// No hay soporte todavía para misma línea y rango, por lo que no pueden
		// estar marcados Rango y Misma Linea al mismo tiempo
    	if(isSameLine() && isRange()){
    		log.saveError("SameLineRangeParameter","");
    		return false;
    	}
		return true;
	} // beforeSave
    
    /**
     * @return el mínimo nro de secuencia si es que existe alguno
     */
    protected Integer getMinSeqNo(){
		return DB
				.getSQLValue(
						get_TrxName(),
						"SELECT min(seqno) as seqno FROM ad_process_para WHERE ad_process_id = ?",
						getAD_Process_ID());
	}
    
}	// MProcessPara



/*
 * @(#)MProcessPara.java   02.jul 2007
 * 
 *  Fin del fichero MProcessPara.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
