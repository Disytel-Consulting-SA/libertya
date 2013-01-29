/*
 * @(#)POInfoColumn.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CLogger;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.logging.Level;

/**
 *      PO Info Column Info Value Object
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: POInfoColumn.java,v 1.4 2005/04/25 05:04:19 jjanke Exp $
 */
public class POInfoColumn implements Serializable {

    /** Used by Remote FinReport */
    static final long	serialVersionUID	= -3983585608504631958L;

    /** Descripción de Campo */
    public BigDecimal	ValueMin_BD	= null;

    /** Descripción de Campo */
    public BigDecimal	ValueMax_BD	= null;

    /** Descripción de Campo */
    public int	AD_Column_ID;

    //

    /** Descripción de Campo */
    public int	AD_Reference_Value_ID;

    /** Descripción de Campo */
    public Class	ColumnClass;

    /** Descripción de Campo */
    public String	ColumnDescription;

    /** Descripción de Campo */
    public String	ColumnLabel;

    /** Descripción de Campo */
    public String	ColumnName;

    /** Descripción de Campo */
    public String	ColumnSQL;

    /** Descripción de Campo */
    public String	DefaultLogic;

    /** Descripción de Campo */
    public int	DisplayType;

    //

    /** Descripción de Campo */
    public int	FieldLength;

    /** Descripción de Campo */
    public boolean	IsKey;

    /** Descripción de Campo */
    public boolean	IsMandatory;

    /** Descripción de Campo */
    public boolean	IsParent;

    /** Descripción de Campo */
    public boolean	IsTranslated;

    /** Descripción de Campo */
    public boolean	IsUpdateable;

    /** Descripción de Campo */
    public String	ValidationCode;

    /** Descripción de Campo */
    public String	ValueMax;

    /** Descripción de Campo */
    public String	ValueMin;

    /**
     *  Constructor
     *      @param ad_Column_ID Column ID
     *      @param columnName Dolumn name
     *      @param columnSQL virtual column
     *      @param displayType Display Type
     *      @param isMandatory Mandatory
     *      @param isUpdateable Updateable
     *      @param defaultLogic Default Logic
     *      @param columnLabel Column Label
     *      @param columnDescription Column Description
     *      @param isKey true if key
     *      @param isParent true if parent
     *      @param ad_Reference_Value_ID reference value
     *      @param validationCode sql validation code
     *      @param fieldLength Field Length
     *      @param valueMin minimal value
     *      @param valueMax maximal value
     * @param isTranslated
     */
    public POInfoColumn(int ad_Column_ID, String columnName, String columnSQL, int displayType, boolean isMandatory, boolean isUpdateable, String defaultLogic, String columnLabel, String columnDescription, boolean isKey, boolean isParent, int ad_Reference_Value_ID, String validationCode, int fieldLength, String valueMin, String valueMax, boolean isTranslated) {

        AD_Column_ID	= ad_Column_ID;
        ColumnName	= columnName;
        ColumnSQL	= columnSQL;
        DisplayType	= displayType;

        if (columnName.equals("AD_Language")) {

            DisplayType	= org.openXpertya.util.DisplayType.String;
            ColumnClass	= String.class;

        } else if (columnName.equals("Posted") || columnName.equals("Processed") || columnName.equals("Processing")) {
            ColumnClass	= Boolean.class;
        } else if (columnName.equals("Record_ID")) {

            DisplayType	= org.openXpertya.util.DisplayType.ID;
            ColumnClass	= Integer.class;

        } else {
            ColumnClass	= org.openXpertya.util.DisplayType.getClass(displayType, true);
        }

        IsMandatory		= isMandatory;
        IsUpdateable		= isUpdateable;
        DefaultLogic		= defaultLogic;
        ColumnLabel		= columnLabel;
        ColumnDescription	= columnDescription;
        IsKey			= isKey;
        IsParent		= isParent;

        //
        AD_Reference_Value_ID	= ad_Reference_Value_ID;
        ValidationCode		= validationCode;

        //
        FieldLength	= fieldLength;
        ValueMin	= valueMin;

        try {

            if ((valueMin != null) && (valueMin.length() > 0)) {
                ValueMin_BD	= new BigDecimal(valueMin);
            }

        } catch (Exception ex) {
            CLogger.get().log(Level.SEVERE, "ValueMin=" + valueMin, ex);
        }

        ValueMax	= valueMax;

        try {

            if ((valueMax != null) && (valueMax.length() > 0)) {
                ValueMax_BD	= new BigDecimal(valueMax);
            }

        } catch (Exception ex) {
            CLogger.get().log(Level.SEVERE, "ValueMax=" + valueMax, ex);
        }

        IsTranslated	= isTranslated;

    }		// Column

    /**
	 *  Constructor
	 *	@param ad_Column_ID Column ID
	 *	@param columnName Column name
	 *	@param columnSQL virtual column
	 *	@param displayType Display Type
	 *	@param isMandatory Mandatory
	 *	@param isUpdateable Updateable
	 *	@param defaultLogic Default Logic
	 *	@param columnLabel Column Label
	 *	@param columnDescription Column Description
	 *	@param isKey true if key
	 *	@param isParent true if parent
	 *	@param ad_Reference_Value_ID reference value
	 *	@param validationCode sql validation code
	 *	@param fieldLength Field Length
	 * 	@param valueMin minimal value
	 * 	@param valueMax maximal value
	 * 	@param isTranslated translated
	 * 	@param isEncrypted encrypted 
	 * 	@param isAllowLogging allow logging 
	 */
	public POInfoColumn (int ad_Column_ID, String columnName, String columnSQL, int displayType,
		boolean isMandatory, boolean isUpdateable, String defaultLogic,
		String columnLabel, String columnDescription,
		boolean isKey, boolean isParent,
		int ad_Reference_Value_ID, String validationCode,
		int fieldLength, String valueMin, String valueMax,
		boolean isTranslated, boolean isEncrypted, boolean isAllowLogging)
	{
		AD_Column_ID = ad_Column_ID;
		ColumnName = columnName;
		ColumnSQL = columnSQL;
		DisplayType = displayType;
		if (columnName.equals("AD_Language") || columnName.equals("EntityType"))
		{
			DisplayType = org.openXpertya.util.DisplayType.String;
			ColumnClass = String.class;
		}
		else if (columnName.equals("Posted") 
			|| columnName.equals("Processed")
			|| columnName.equals("Processing"))
		{
			ColumnClass = Boolean.class;
		}
		else if (columnName.equals("Record_ID"))
		{
			DisplayType = org.openXpertya.util.DisplayType.ID;
			ColumnClass = Integer.class;
		}
		else
			ColumnClass = org.openXpertya.util.DisplayType.getClass(displayType, true);
		IsMandatory = isMandatory;
		IsUpdateable = isUpdateable;
		DefaultLogic = defaultLogic;
		ColumnLabel = columnLabel;
		ColumnDescription = columnDescription;
		IsKey = isKey;
		IsParent = isParent;
		//
		AD_Reference_Value_ID = ad_Reference_Value_ID;
		ValidationCode = validationCode;
		//
		FieldLength = fieldLength;
		ValueMin = valueMin;
		try
		{
			if (valueMin != null && valueMin.length() > 0)
				ValueMin_BD = new BigDecimal(valueMin);
		}
		catch (Exception ex)
		{
			CLogger.get().log(Level.SEVERE, "ValueMin=" + valueMin, ex);
		}
		ValueMax = valueMax;
		try
		{
			if (valueMax != null && valueMax.length() > 0)
				ValueMax_BD = new BigDecimal(valueMax);
		}
		catch (Exception ex)
		{
			CLogger.get().log(Level.SEVERE, "ValueMax=" + valueMax, ex);
		}
		IsTranslated = isTranslated;
		IsEncrypted = isEncrypted;
		IsAllowLogging = isAllowLogging;
	}   //  Column

    
    /**
     *      String representation
     *  @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("POInfo.Column[");

        sb.append(ColumnName).append(",ID=").append(AD_Column_ID).append(",DisplayType=").append(DisplayType).append(",ColumnClass=").append(ColumnClass);
        sb.append("]");

        return sb.toString();

    }		// toString
    
	/**	Encrypted		*/
	public boolean		IsEncrypted;
	/**	Allow Logging		*/
	public boolean		IsAllowLogging;

}	// POInfoColumn



/*
 * @(#)POInfoColumn.java   02.jul 2007
 * 
 *  Fin del fichero POInfoColumn.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
