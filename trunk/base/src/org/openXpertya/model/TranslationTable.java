/*
 * @(#)TranslationTable.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.util.ArrayList;

/**
 *      Translation Table Management
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: TranslationTable.java,v 1.4 2005/03/11 20:28:38 jjanke Exp $
 */
public class TranslationTable {

    /** Active Translations */
    private static Integer	s_activeLanguages	= null;

    /** Cache */
    private static CCache	s_cache	= new CCache("TranslationTable", 20);

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(TranslationTable.class);

    /** Logger */
    protected CLogger	log	= CLogger.getCLogger(getClass());

    /** Translation Table Name */
    private String	m_trlTableName	= null;

    /** Descripción de Campo */
    private ArrayList	m_columns	= new ArrayList();

    /** Base Table Name */
    private String	m_baseTableName	= null;

    /**
     *      Translation Table
     *      @param baseTableName base table name
     */
    protected TranslationTable(String baseTableName) {

        if (baseTableName == null) {
            throw new IllegalArgumentException("Base Table Name is null");
        }

        m_baseTableName	= baseTableName;
        m_trlTableName	= baseTableName + "_Trl";
        initColumns();
        log.fine(toString());

    }		// TranslationTable

    /**
     *      Create Translation record from PO
     *      @param po base table record
     *      @return true if inserted or no translation
     */
    public boolean createTranslation(PO po) {

        if (!isActiveLanguages(false)) {
            return true;
        }

        if (po.getID() == 0) {
            throw new IllegalArgumentException("PO ID is 0");
        }

        //
        StringBuffer	sql1	= new StringBuffer();

        sql1.append("INSERT INTO ").append(m_trlTableName).append(" (");

        StringBuffer	sql2	= new StringBuffer();

        sql2.append(") SELECT ");

        // Key Columns
        sql1.append(m_baseTableName).append("_ID,AD_Language");
        sql2.append("b.").append(m_baseTableName).append("_ID,l.AD_Language");

        // Base Columns
        sql1.append(", AD_Client_ID,AD_Org_ID,IsActive, Created,CreatedBy,Updated,UpdatedBy, IsTranslated");
        sql2.append(", b.AD_Client_ID,b.AD_Org_ID,b.IsActive, b.Created,b.CreatedBy,b.Updated,b.UpdatedBy, 'N'");

        for (int i = 0; i < m_columns.size(); i++) {

            String	columnName	= (String) m_columns.get(i);
            Object	value		= po.get_Value(columnName);

            //
            if (value == null) {
                continue;
            }

            sql1.append(",").append(columnName);
            sql2.append(",b.").append(columnName);
        }

        //
        StringBuffer	sql	= new StringBuffer();

        sql.append(sql1).append(sql2).append(" FROM AD_Language l, " + m_baseTableName + " b WHERE l.IsActive = 'Y' AND l.IsSystemLanguage = 'Y' AND b." + m_baseTableName + "_ID=").append(po.getID());

        int	no	= DB.executeUpdate(sql.toString());

        log.fine("createTranslation " + m_trlTableName + ": ID=" + po.getID() + " #" + no);

        return no != 0;

    }		// createTranslation

    /**
     *      Delete translation for po
     *      @param po persistent object
     *      @return true if no active language or translation deleted
     */
    public static boolean delete(PO po) {

        if (!TranslationTable.isActiveLanguages(false)) {
            return true;
        }

        TranslationTable	table	= TranslationTable.get(po.get_TableName());

        return table.deleteTranslation(po);

    }		// delete

    /**
     *      Delete Translation
     *      @param po po
     *      @return true if udeleted or no translations
     */
    public boolean deleteTranslation(PO po) {

        if (!isActiveLanguages(false)) {
            return true;
        }

        if (po.getIDOld() == 0) {
            throw new IllegalArgumentException("PO Old ID is 0");
        }

        //
        StringBuffer	sb	= new StringBuffer("DELETE ");

        sb.append(m_trlTableName).append(" WHERE ").append(m_baseTableName).append("_ID=").append(po.getIDOld());

        int	no	= DB.executeUpdate(sb.toString());

        log.fine("deleteTranslation " + m_trlTableName + ": ID=" + po.getIDOld() + " #" + no);

        return no != 0;

    }		// resetTranslationFlag

    /**
     *      Add Translation Columns
     */
    private void initColumns() {

        M_Table	table	= M_Table.get(Env.getCtx(), m_trlTableName);

        if (table == null) {
            throw new IllegalArgumentException("initColumns - Table Not found=" + m_trlTableName);
        }

        M_Column[]	columns	= table.getColumns(false);

        for (int i = 0; i < columns.length; i++) {

            M_Column	column	= columns[i];

            if (column.isStandardColumn()) {
                continue;
            }

            String	columnName	= column.getColumnName();

            if (columnName.endsWith("_ID") || columnName.startsWith("AD_Language") || columnName.equals("IsTranslated")) {
                continue;
            }

            //
            m_columns.add(columnName);
        }

        if (m_columns.size() == 0) {
            throw new IllegalArgumentException("initColumns - No Columns found=" + m_trlTableName);
        }

    }		// initColumns

    /**
     *      Reset Translation Flag
     *      @param po po
     *      @return true if updated or no translations
     */
    public boolean resetTranslationFlag(PO po) {

        if (!isActiveLanguages(false)) {
            return true;
        }

        if (po.getID() == 0) {
            throw new IllegalArgumentException("PO ID is 0");
        }

        //
        StringBuffer	sb	= new StringBuffer("UPDATE ");

        sb.append(m_trlTableName).append(" SET IsTranslated='N',Updated=SysDate WHERE ").append(m_baseTableName).append("_ID=").append(po.getID());

        int	no	= DB.executeUpdate(sb.toString());

        log.fine("resetTranslationFlag " + m_trlTableName + ": ID=" + po.getID() + " #" + no);

        return no != 0;

    }		// resetTranslationFlag

    /**
     *      Save translation for po
     *      @param po persistent object
     *      @param newRecord new
     *      @return true if no active language or translation saved/reset
     */
    public static boolean save(PO po, boolean newRecord) {

        if (!TranslationTable.isActiveLanguages(false)) {
            return true;
        }

        TranslationTable	table	= TranslationTable.get(po.get_TableName());

        if (newRecord) {
            return table.createTranslation(po);
        }

        return table.resetTranslationFlag(po);

    }		// save

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("TranslationTable[");

        sb.append(m_trlTableName).append("(").append(m_baseTableName).append(")");

        for (int i = 0; i < m_columns.size(); i++) {
            sb.append("-").append(m_columns.get(i));
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get TranslationTable from Cache
     *      @param baseTableName base table name
     *      @return TranslationTable
     */
    public static TranslationTable get(String baseTableName) {

        TranslationTable	retValue	= (TranslationTable) s_cache.get(baseTableName);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new TranslationTable(baseTableName);
        s_cache.put(baseTableName, retValue);

        return retValue;

    }		// get

    /**
     *      Get Number of active Translation Languages
     *      @param requery requery
     *      @return number of active Translations
     */
    public static int getActiveLanguages(boolean requery) {

        if ((s_activeLanguages != null) &&!requery) {
            return s_activeLanguages.intValue();
        }

        int	no	= DB.getSQLValue(null, "SELECT COUNT(*) FROM AD_Language WHERE IsActive='Y' AND IsSystemLanguage='Y'");

        s_activeLanguages	= new Integer(no);

        return s_activeLanguages.intValue();

    }		// getActiveLanguages

    /**
     *      Are there active Translation Languages
     *      @param requery requery
     *      @return true active Translations
     */
    public static boolean isActiveLanguages(boolean requery) {

        int	no	= getActiveLanguages(requery);

        return no > 0;

    }		// isActiveLanguages
}	// TranslationTable



/*
 * @(#)TranslationTable.java   02.jul 2007
 * 
 *  Fin del fichero TranslationTable.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
