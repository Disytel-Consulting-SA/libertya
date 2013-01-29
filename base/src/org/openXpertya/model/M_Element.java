/*
 * @(#)M_Element.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *      System Element Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: M_Element.java,v 1.8 2005/04/20 04:56:58 jjanke Exp $
 */
public class M_Element extends X_AD_Element {

    /** Logger */
    private static CLogger	log	= CLogger.getCLogger(M_Element.class);

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Element_ID element
     * @param trxName
     */
    public M_Element(Properties ctx, int AD_Element_ID, String trxName) {

        super(ctx, AD_Element_ID, trxName);

        if (AD_Element_ID == 0) {

            // setColumnName (null);
            // setEntityType (null);   // U
            // setName (null);
            // setPrintName (null);
        }

    }		// M_Element

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public M_Element(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// M_Element

    /**
     *      Minimum Constructor
     *      @param ctx context
     *      @param columnName column
     *      @param EntityType entity type
     * @param trxName
     */
    public M_Element(Properties ctx, String columnName, String EntityType, String trxName) {

        super(ctx, 0, trxName);
        
        // Patron a buscar en columnName xx_xxxxxx_xxxx_id ==> XX_Xxxxxx_Xxxx_ID
        String patron = "([a-z]+\\_[a-z])(\\w*)(\\_id)";
        boolean cumplePatron = Pattern.matches(patron, columnName);
         // Comprueba si se cumple el patron
        if (cumplePatron){
        	columnName = ArmarIdentificador(patron, columnName);
        }
        // setear el elemento con columnName
        setColumnName(columnName);
        setName(columnName);
        setPrintName(columnName);

        //
        setEntityType(EntityType);	// U

    }					// M_Element
    
    private String ArmarIdentificador(String patron, String columnName){
    	Pattern p = Pattern.compile(patron);
    	Matcher matcher = p.matcher(columnName);
    	// Busca los grupos que se definieron en patron (se marcan entre parentesis)
    	matcher.find();
    	// Arma el columnName_upper con las mayusculas
    	String	columnName_upper = matcher.group(1).toUpperCase();
    	// Pasar a mayuscula el primer caracter despues de un _ (XX_Xxxxxx_Xxxx_ID)
    	if (matcher.group(2).contains("_")){
    		String cn = "";
    		int i = 0;
    		while (matcher.group(2).length() > i){
    			if (matcher.group(2).charAt(i) != '_'){
    				cn += matcher.group(2).charAt(i);
    			} 
    			else {
    				cn += matcher.group(2).charAt(i);
    				i++;
    				cn += matcher.group(2).substring(i,i+1).toUpperCase();
    			}
    			i++;
    		}
    		columnName_upper += cn;
    	}
    	else {
    		columnName_upper += matcher.group(2);
    	}
     	
    	return columnName_upper += matcher.group(3).toUpperCase();
     }

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

    	int elemID = getAD_Element_ID();
    	boolean result = success;
    	
        // Update Columns, Fields, Parameters, Print Info
        if (!newRecord) {
        	
        	try
        	{
                // Column
                PreparedStatement stmt = DB.prepareStatement("SELECT * FROM AD_Column WHERE AD_Element_ID=" + elemID, get_TrxName());
                ResultSet rs = stmt.executeQuery();
                while (rs.next())
                {
                	M_Column aColumn = new M_Column(getCtx(), rs, get_TrxName());
                	aColumn.setColumnName(getColumnName());
                	aColumn.setName(getName());
                	aColumn.setDescription(getDescription());
                	aColumn.setHelp(getHelp());
                	result = result && aColumn.save();
                }

                // Field
                stmt = DB.prepareStatement("SELECT * FROM AD_Field WHERE AD_Column_ID IN (SELECT AD_Column_ID FROM AD_Column WHERE AD_Element_ID="+elemID+") AND IsCentrallyMaintained='Y'", get_TrxName());
                rs = stmt.executeQuery();
                while (rs.next())
                {
                	M_Field aField = new M_Field(getCtx(), rs, get_TrxName());
                	aField.setName(getName());
                	aField.setDescription(getDescription());
                	aField.setHelp(getHelp());
                	result = result && aField.save();
                }

                // Parameter
                stmt = DB.prepareStatement("SELECT * FROM AD_Process_Para WHERE UPPER(ColumnName)='NOMBRECOLUMNABD2' AND IsCentrallyMaintained='Y' AND AD_Element_ID IS NULL", get_TrxName());
                rs = stmt.executeQuery();
                while (rs.next())
                {
                	MProcessPara para = new MProcessPara(getCtx(), rs, get_TrxName());
                	para.setColumnName(getColumnName());
                	para.setName(getName());
                	para.setDescription(getDescription());
                	para.setHelp(getHelp());
                	para.setAD_Element_ID(elemID);
                	result = result && para.save();
                }
                
                stmt = DB.prepareStatement("SELECT * FROM AD_Process_Para WHERE AD_Element_ID=" + elemID + " AND IsCentrallyMaintained='Y'", get_TrxName());
                rs = stmt.executeQuery();
                while (rs.next())
                {
                	MProcessPara para = new MProcessPara(getCtx(), rs, get_TrxName());
                	para.setColumnName(getColumnName());
                	para.setName(getName());
                	para.setDescription(getDescription());
                	para.setHelp(getHelp());
                	result = result && para.save();
                }
                
                // Print Info
                stmt = DB.prepareStatement("SELECT * FROM AD_PrintFormatItem pi	WHERE AD_Client_ID=0 AND EXISTS (SELECT * FROM AD_Column c WHERE c.AD_Column_ID=pi.AD_Column_ID AND c.AD_Element_ID=" + elemID + ")", get_TrxName());
                rs = stmt.executeQuery();
                while (rs.next())
                {
                	// Debo instanciar un generalPO en lugar de un MPrintFormatItem debido a la dependencia de paquetes al compilar
                	GeneralPO item = new GeneralPO(getCtx(), -1, rs, M_Table.getTableID("AD_PrintFormatItem"), get_TrxName());
                	item.set_Value("PrintName", getPrintName());
                	item.set_Value("Name", getName());
                	result = result && item.save();
                }	
        	}
        	catch (Exception e)
        	{
        		log.log(Level.WARNING, " Exception updating elements " + e.getMessage());
        		return false;
        	}
        }

        return result;
    }		// afterSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Element
     *
     * @param ctx
     *      @param columnName case insentitive column name
     *      @return case sensitive column name
     */
    public static M_Element get(Properties ctx, String columnName) {

        if ((columnName == null) || (columnName.length() == 0)) {
            return null;
        }

        M_Element		retValue	= null;
        String			sql		= "SELECT * FROM AD_Element WHERE UPPER(ColumnName)=?";
        PreparedStatement	pstmt		= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setString(1, columnName.toUpperCase());

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new M_Element(ctx, rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return retValue;

    }		// get

    /**
     *      Get case sensitive Column Name
     *      @param columnName case insentitive column name
     *      @return case sensitive column name
     */
    public static String getColumnName(String columnName) {

        if ((columnName == null) || (columnName.length() == 0)) {
            return columnName;
        }

        String	retValue	= columnName;
        String	sql		= "SELECT ColumnName FROM AD_Element WHERE UPPER(ColumnName)=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setString(1, columnName.toUpperCase());

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                retValue	= rs.getString(1);

                if (rs.next()) {
                    log.warning("Not unique: " + columnName + " -> " + retValue + " - " + rs.getString(1));
                }

            } else {
                log.warning("No found: " + columnName);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, columnName, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return retValue;

    }		// getColumnName
}	// M_Element



/*
 * @(#)M_Element.java   02.jul 2007
 * 
 *  Fin del fichero M_Element.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
