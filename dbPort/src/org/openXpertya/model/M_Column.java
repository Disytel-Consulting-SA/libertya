/*
 * @(#)M_Column.java   12.oct 2007  Versión 2.2
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

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 *      Persistent Column Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: M_Column.java,v 1.12 2005/05/14 05:32:16 jjanke Exp $
 */
public class M_Column extends X_AD_Column {

    /** Cache */
    private static CCache	s_cache	= new CCache("AD_Column", 20);

    /**
     *      Parent Constructor
     *      @param parent table
     */
    public M_Column(M_Table parent) {

        this(parent.getCtx(), 0, parent.get_TrxName());
        setClientOrg(parent);
        setAD_Table_ID(parent.getAD_Table_ID());
        setEntityType(parent.getEntityType());

    }		// M_Column

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Column_ID
     * @param trxName
     */
    public M_Column(Properties ctx, int AD_Column_ID, String trxName) {

        super(ctx, AD_Column_ID, trxName);

        if (AD_Column_ID == 0) {

            // setAD_Element_ID (0);
            // setAD_Reference_ID (0);
            // setColumnName (null);
            // setName (null);
            // setEntityType (null);   // U
            setIsAlwaysUpdateable(false);	// N
            setIsEncrypted(false);
            setIsIdentifier(false);
            setIsKey(false);
            setIsMandatory(false);
            setIsParent(false);
            setIsSelectionColumn(false);
            setIsTranslated(false);
            setIsUpdateable(true);		// Y
            setVersion(Env.ZERO);
        }

    }						// M_Column

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public M_Column(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// M_Column

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        // Update Fields
        if (!newRecord) {
        	// Si cambio el nombre, descripcion o ayuda
        	if(is_ValueChanged("Name")
        			|| is_ValueChanged("Description")
        			|| is_ValueChanged("Help")){
            	// Obtener los campos de esta columna
            	List<PO> fields = PO.find(getCtx(), "ad_field", "ad_column_id = ? AND IsCentrallyMaintained='Y'", new Object[]{getID()}, null, get_TrxName());
            	// Itero por los campos y les actualizo el nombre, descripción y ayuda
            	for (PO field : fields) {
            		if(is_ValueChanged("Name")){
            			((M_Field)field).setName(getName());
            		}
            		if(is_ValueChanged("Description")){
            			((M_Field)field).setDescription(getDescription());
            		}
            		if(is_ValueChanged("Help")){
            			((M_Field)field).setHelp(getHelp());
            		}
            		// Guardo
            		if(!field.save()){
            			log.severe("Error al actualizar los campos referenciados por esta columna");
            		}
    			}
    		}
//            StringBuffer	sql	= new StringBuffer("UPDATE AD_Field SET Name=").append(DB.TO_STRING(getName())).append(", Description=").append(DB.TO_STRING(getDescription())).append(", Help=").append(DB.TO_STRING(getHelp())).append(" WHERE AD_Column_ID=").append(getID()).append(" AND IsCentrallyMaintained='Y'");
//            int	no	= DB.executeUpdate(sql.toString(), get_TrxName());
//
//            log.fine("afterSave - Fields updated #" + no);
        }

        return success;
    }		// afterSave

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true
     */
    protected boolean beforeSave(boolean newRecord) {

        if (isVirtualColumn()) {

            if (isMandatory()) {
                setIsMandatory(false);
            }

            if (isUpdateable()) {
                setIsUpdateable(false);
            }
        }

        if (isAlwaysUpdateable() &&!isUpdateable()) {
            setIsAlwaysUpdateable(false);
        }

        return true;

    }		// beforeSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get M_Column from Cache
     *      @param ctx context
     *      @param AD_Column_ID id
     *      @return M_Column
     */
    public static M_Column get(Properties ctx, int AD_Column_ID) {

        Integer		key		= new Integer(AD_Column_ID);
        M_Column	retValue	= (M_Column) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new M_Column(ctx, AD_Column_ID, null);

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    /**
     *      Get Column Name
     *      @param ctx context
     *      @param AD_Column_ID id
     *      @return Column Name or null
     */
    public static String getColumnName(Properties ctx, int AD_Column_ID) {

        M_Column	col	= M_Column.get(ctx, AD_Column_ID);

        if (col.getID() == 0) {
            return null;
        }

        return col.getColumnName();

    }		// getColumnName

    /**
     *      Get Table Constraint
     *      @param tableName table name
     *      @return table constraint
     */
    public String getConstraint(String tableName) {

        if (isKey()) {
            return "CONSTRAINT " + tableName + "_Key PRIMARY KEY (" + getColumnName() + ")";
        }

        /**
         * if (getAD_Reference_ID() == DisplayType.TableDir
         *       || getAD_Reference_ID() == DisplayType.Search)
         *       return "CONSTRAINT " ADTable_ADTableTrl
         *               + " FOREIGN KEY (" + getColumnName() + ") REFERENCES "
         *               + AD_Table(AD_Table_ID) ON DELETE CASCADE
         */
        return "";

    }		// getConstraint

    /**
     *      Get SQL Add command
     *      @param table table
     *      @return sql
     */
    public String getSQLAdd(M_Table table) {

        StringBuffer	sql	= new StringBuffer("ALTER TABLE ").append(table.getTableName()).append(" ADD ").append(getSQLDDL());

        return sql.toString();

    }		// getSQLAdd

    /**
     *      Get SQL DDL
     *      @return columnName datataype ..
     */
    public String getSQLDDL() {

        StringBuffer	sql	= new StringBuffer(getColumnName()).append(" ").append(getSQLDataType());

        // Default
        if ((getDefaultValue() != null) && (getDefaultValue().length() > 0)) {

            sql.append(" DEFAULT ");

            if (DisplayType.isText(getAD_Reference_ID())) {
                sql.append(DB.TO_STRING(getDefaultValue()));
            } else {
                sql.append(getDefaultValue());
            }
        }

        // Inline Constraint
        if (getAD_Reference_ID() == DisplayType.YesNo) {
            sql.append(" CHECK (").append(getColumnName()).append(" IN ('Y','N'))");
        }

        // Null
        if (isMandatory()) {
            sql.append(" NOT NULL");
        }

        return sql.toString();

    }		// getSQLDDL

    /**
     *      Get SQL Data Type
     *      @return e.g. NVARCHAR2(60)
     */
    private String getSQLDataType() {

        int	dt	= getAD_Reference_ID();

        if (DisplayType.isID(dt) || (dt == DisplayType.Integer)) {
            return "NUMBER(10)";
        }

        if (DisplayType.isDate(dt)) {
            return "DATE";
        }

        if (DisplayType.isNumeric(dt)) {
            return "NUMBER";
        }

        if ((dt == DisplayType.Binary) || (dt == DisplayType.Image)) {
            return "BLOB";
        }

        if (dt == DisplayType.TextLong) {
            return "CLOB";
        }

        if (dt == DisplayType.YesNo) {
            return "CHAR(1)";
        }

        if (dt == DisplayType.List) {
            return "CHAR(" + getFieldLength() + ")";
        } else if (!DisplayType.isText(dt)) {
            log.severe("Unhandled Data Type = " + dt);
        }

        return "NVARCHAR2(" + getFieldLength() + ")";

    }		// getSQLDataType

    /**
     *      Get SQL Modify command
     *      @param table table
     *      @return sql
     */
    public String getSQLModify(M_Table table) {
    	/*
    	 * Franco Bonafine - Disytel - 08-04-2011
    	 * Corregido para que funcione en PostgreSQL.
    	 * Para modificar los datos de una columna hay que ejecutar una sentencia ALTER por cada
    	 * dato (Obligatorio, Default, Tipo).
    	 * Se corrigió para que se genere correctamente el SQL del ALTER tal siguiendo el manual
    	 * de PostgreSQL v8.0
    	 * 
    	 * --> http://www.postgresql.org/docs/8.0/static/sql-altertable.html
    	 */
    	
    	String columnAlterBase = "ALTER TABLE " + table.getTableName() + " ALTER COLUMN " + getColumnName() + " ";
        //StringBuffer	sql	= new StringBuffer("ALTER TABLE ").append(table.getTableName()).append(" MODIFY ").append(getColumnName()).append(" ").append(getSQLDataType());
    	StringBuffer sql = new StringBuffer();
    	// Type
    	sql.append(columnAlterBase).append("TYPE ").append(getSQLDataType()).append("; ");

        // Default
        //sql.append(" DEFAULT ");
    	sql.append(columnAlterBase);
        
    	String	defaultValue	= getDefaultValue();

        if ((defaultValue != null) && (defaultValue.length() > 0) && (defaultValue.indexOf("@") != 0))		// no variables
        {
        	sql.append("SET DEFAULT ");
            if (DisplayType.isText(getAD_Reference_ID()) || (getAD_Reference_ID() == DisplayType.List)) {

                if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                    sql.append(defaultValue);
                } else {
                    sql.append(DB.TO_STRING(defaultValue));
                }

            } else {
                sql.append(defaultValue);
            }

        } else {
            //sql.append(" NULL ");
        	sql.append("DROP DEFAULT");
        }
        sql.append(";");

        // Constraint

        // Null
        sql.append(columnAlterBase);
        if (isMandatory()) {
            sql.append("SET NOT NULL");
        } else {
            sql.append("DROP NOT NULL");
        }
        sql.append(";");

        return sql.toString();

    }		// getSQLModify

    /**
     *      Is Standard Column
     *      @return true for AD_Client_ID, etc.
     */
    public boolean isStandardColumn() {

        String	columnName	= getColumnName();

        if (columnName.equals("AD_Client_ID") || columnName.equals("AD_Org_ID") || columnName.equals("IsActive") || columnName.startsWith("Created") || columnName.startsWith("Updated")) {
            return true;
        }

        return false;

    }		// isStandardColumn

    /**
     *      Is Virtual Column
     *      @return true if virtual column
     */
    public boolean isVirtualColumn() {

        String	s	= getColumnSQL();

        return (s != null) && (s.length() > 0);

    }		// isVirtualColumn
}	// M_Column



/*
 * @(#)M_Column.java   02.jul 2007
 * 
 *  Fin del fichero M_Column.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
