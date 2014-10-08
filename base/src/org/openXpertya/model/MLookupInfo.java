/*
 * @(#)MLookupInfo.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;
import java.util.logging.Level;

/**
 *  Info Class for Lookup SQL (ValueObject)
 *
 *      @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *      @version        $Id: MLookupInfo.java,v 1.17 2005/05/14 05:32:16 jjanke Exp $
 */
public class MLookupInfo implements Serializable, Cloneable {

    /** Descripción de Campo */
    static final long	serialVersionUID	= -7958664359250070233L;

    /** SQL Query */
    public String	Query	= null;

    /** Table Name */
    public String	TableName	= "";

    /** Key Column */
    public String	KeyColumn	= "";

    /** Zoom Query */
    public MQuery	ZoomQuery	= null;

    /** Validation code */
    public String	ValidationCode	= "";

    /** Direct Access Query */
    public String	QueryDirect	= "";

    /** Validation flag */
    public boolean	IsValidated	= true;

    /** Parent Flag */
    public boolean	IsParent	= false;

    /** Key Flag */
    public boolean	IsKey	= false;

    /** Descripción de Campo */
    public Properties	ctx	= null;

    /** CreadedBy?updatedBy */
    public boolean	IsCreadedUpdatedBy	= false;

    /** Real AD_Reference_ID */
    public int	AD_Reference_Value_ID;

    /** AD_Column_Info or AD_Process_Para */
    public int	Column_ID;

    /** AD_Reference_ID */
    public int	DisplayType;

    /** Descripción de Campo */
    public int	WindowNo;

    /** Zoom Window */
    public int	ZoomWindow;

    /** Zoom Window */
    public int	ZoomWindowPO;
    
    /** Add Security Validation */
    public boolean addSecurityValidation = true;
    
    /**
     *  Constructor.
     *      (called from MLookupFactory)
     *  @param sqlQuery SQL query
     * @param tableName
     *  @param keyColumn key column
     *  @param zoomWindow zoom window
     *  @param zoomWindowPO PO zoom window
     *  @param zoomQuery zoom query
     */
    public MLookupInfo(String sqlQuery, String tableName, String keyColumn, int zoomWindow, int zoomWindowPO, MQuery zoomQuery) {

        if (sqlQuery == null) {
            throw new IllegalArgumentException("sqlQuery is null");
        }

        Query	= sqlQuery;

        if (keyColumn == null) {
            throw new IllegalArgumentException("keyColumn is null");
        }

        TableName	= tableName;
        KeyColumn	= keyColumn;
        ZoomWindow	= zoomWindow;
        ZoomWindowPO	= zoomWindowPO;
        ZoomQuery	= zoomQuery;

    }		// MLookupInfo

    /**
     *      Clone
     *      @return deep copy
     */
    public MLookupInfo cloneIt() {

        try {

            MLookupInfo	clone	= (MLookupInfo) super.clone();

            return clone;

        } catch (Exception e) {
            CLogger.get().log(Level.SEVERE, "cloneIt", e);
        }

        return null;

    }		// clone

    /**
     * String representation
     * @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MLookupInfo[").append(KeyColumn).append(" - Direct=").append(QueryDirect).append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *  Get first AD_Column_ID of matching ColumnName.
     *  Can have SQL LIKE placeholders.
     *  (This is more a development tool than used for production)
     *  @param columnName column name
     *  @return AD_Column_ID
     */
    public static int getAD_Column_ID(String columnName) {

        int	retValue	= 0;
        String	sql		= "SELECT c.AD_Column_ID,c.ColumnName,t.TableName " + "FROM AD_Column c, AD_Table t " + "WHERE c.ColumnName LIKE ? AND c.AD_Table_ID=t.AD_Table_ID";

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql, PluginUtils.getPluginInstallerTrxName());

            pstmt.setString(1, columnName);

            ResultSet	rs	= pstmt.executeQuery();

            //
            int		i	= 0;
            int		id	= 0;
            String	colName	= "";
            String	tabName	= "";

            while (rs.next()) {

                id	= rs.getInt(1);

                if (i == 0) {
                    retValue	= id;
                }

                colName	= rs.getString(2);
                tabName	= rs.getString(3);
                CLogger.get().config("AD_Column Name=" + colName + ", ID=" + id + ", Table=" + tabName);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            CLogger.get().log(Level.SEVERE, "getAD_Column_ID", e);
        }

        return retValue;

    }		// getAD_Reference_ID

    /**
     *  Get first AD_Reference_ID of a matching Reference Name.
     *  Can have SQL LIKE placeholders.
     *  (This is more a development tool than used for production)
     *  @param referenceName reference name
     *  @return AD_Reference_ID
     */
    public static int getAD_Reference_ID(String referenceName) {

        int	retValue	= 0;
        String	sql		= "SELECT AD_Reference_ID,Name,ValidationType,IsActive " + "FROM AD_Reference WHERE Name LIKE ?";

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql, PluginUtils.getPluginInstallerTrxName());

            pstmt.setString(1, referenceName);

            ResultSet	rs	= pstmt.executeQuery();

            //
            int		i		= 0;
            int		id		= 0;
            String	refName		= "";
            String	validationType	= "";
            boolean	isActive	= false;

            while (rs.next()) {

                id	= rs.getInt(1);

                if (i == 0) {
                    retValue	= id;
                }

                refName		= rs.getString(2);
                validationType	= rs.getString(3);
                isActive	= rs.getString(4).equals("Y");
                CLogger.get().config("AD_Reference Name=" + refName + ", ID=" + id + ", Type=" + validationType + ", Active=" + isActive);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            CLogger.get().log(Level.SEVERE, "getAD_Reference_ID", e);
        }

        return retValue;

    }		// getAD_Reference_ID
	public String InfoFactoryClass = null;
}	// MLookupInfo



/*
 * @(#)MLookupInfo.java   02.jul 2007
 * 
 *  Fin del fichero MLookupInfo.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
