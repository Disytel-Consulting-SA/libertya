/*
 * @(#)MRecordAccess.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.Msg;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Properties;
import java.util.logging.Level;

/**
 *      Record Access Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MRecordAccess.java,v 1.11 2005/03/11 20:28:35 jjanke Exp $
 */
public class MRecordAccess extends X_AD_Record_Access {

    // Key Column Name                 */

    /** Descripción de Campo */
    private String	m_keyColumnName	= null;

    /** TableName */
    private String	m_tableName;

    /**
     *      Persistency Constructor
     *      @param ctx context
     *      @param ignored ignored
     * @param trxName
     */
    public MRecordAccess(Properties ctx, int ignored, String trxName) {

        super(ctx, 0, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

    }		// MRecordAccess

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MRecordAccess(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MRecordAccess

    /**
     *      Full New Constructor
     *      @param ctx context
     *      @param AD_Role_ID role
     *      @param AD_Table_ID table
     *      @param Record_ID record
     * @param trxName
     */
    public MRecordAccess(Properties ctx, int AD_Role_ID, int AD_Table_ID, int Record_ID, String trxName) {

        super(ctx, 0, trxName);
        setAD_Role_ID(AD_Role_ID);
        setAD_Table_ID(AD_Table_ID);
        setRecord_ID(Record_ID);

        //
        setIsExclude(true);
        setIsReadOnly(false);
        setIsDependentEntities(false);

    }		// MRecordAccess

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MRecordAccess[AD_Role_ID=").append(getAD_Role_ID()).append(",AD_Table_ID=").append(getAD_Table_ID()).append(",Record_ID=").append(getRecord_ID()).append(",Active=").append(isActive()).append(",Exclude=").append(isExclude()).append(",ReadOnly=").append(isReadOnly()).append(",Dependent=").append(isDependentEntities()).append("]");

        return sb.toString();

    }		// toString

    /**
     *      Extended String Representation
     *
     * @param ctx
     *      @return extended info
     */
    public String toStringX(Properties ctx) {

        String		in	= Msg.getMsg(ctx, "Include");
        String		ex	= Msg.getMsg(ctx, "Exclude");
        StringBuffer	sb	= new StringBuffer();

        sb.append(Msg.translate(ctx, "AD_Table_ID")).append("=").append(getTableName(ctx)).append(", ").append(Msg.translate(ctx, "Record_ID")).append("=").append(getRecord_ID()).append(" - ").append(Msg.translate(ctx, "IsDependentEntities")).append("=").append(isDependentEntities()).append(" (").append(Msg.translate(ctx, "IsReadOnly")).append("=").append(isReadOnly()).append(") - ").append(isExclude()
                ? ex
                : in);

        return sb.toString();

    }		// toStringX

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Key Column Name
     *      @return Key Column Name
     */
    public String getKeyColumnName() {

        if (m_keyColumnName != null) {
            return m_keyColumnName;
        }

        //
        String	sql	= "SELECT ColumnName " + "FROM AD_Column " + "WHERE AD_Table_ID=? AND IsKey='Y' AND IsActive='Y'";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, getAD_Table_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                String	s	= rs.getString(1);

                if (m_keyColumnName == null) {
                    m_keyColumnName	= s;
                } else {
                    log.log(Level.SEVERE, "getKeyColumnName - more than one key = " + s);
                }
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getKeyColumnName", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        if (m_keyColumnName == null) {
            log.log(Level.SEVERE, "Record Access requires Table with one key column");
        }

        //
        return m_keyColumnName;

    }		// getKeyColumnName

    /**
     *      Get Key Column Name with consideration of Synonym
     *      @param tableInfo
     *      @return
     */
    public String getKeyColumnName(AccessSqlParser.TableInfo[] tableInfo) {

        String	columnSyn	= getSynonym();

        if (columnSyn == null) {
            return m_keyColumnName;
        }

        // We have a synonym - ignore it if base table inquired
        for (int i = 0; i < tableInfo.length; i++) {

            if (m_keyColumnName.equals("AD_User_ID")) {

                // List of tables where not to use SalesRep_ID
                if (tableInfo[i].getTableName().equals("AD_User")) {
                    return m_keyColumnName;
                }
            } else if (m_keyColumnName.equals("AD_ElementValue_ID")) {

                // List of tables where not to use Account_ID
                if (tableInfo[i].getTableName().equals("AD_ElementValue")) {
                    return m_keyColumnName;
                }
            }

        }	// tables to be ignored

        return columnSyn;

    }		// getKeyColumnInfo

    /**
     *      Get Synonym of Column
     *      @return Synonym Column Name
     */
    public String getSynonym() {

        if ("AD_User_ID".equals(getKeyColumnName())) {
            return "SalesRep_ID";
        } else if ("C_ElementValue_ID".equals(getKeyColumnName())) {
            return "Account_ID";
        }

        //
        return null;

    }		// getSynonym

    /**
     *      Get Table Name
     *      @param ctx context
     *      @return table name
     */
    public String getTableName(Properties ctx) {

        if (m_tableName == null) {

            String		sql	= "SELECT TableName FROM AD_Table WHERE AD_Table_ID=?";
            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql);
                pstmt.setInt(1, getAD_Table_ID());

                ResultSet	rs	= pstmt.executeQuery();

                if (rs.next()) {
                    m_tableName	= rs.getString(1);
                }

                rs.close();
                pstmt.close();
                pstmt	= null;

            } catch (Exception e) {
                log.log(Level.SEVERE, "getColumnName", e);
            }

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

                pstmt	= null;

            } catch (Exception e) {
                pstmt	= null;
            }

            // Get Clear Text
            String	realName	= Msg.translate(ctx, m_tableName + "_ID");

            if (!realName.equals(m_tableName + "_ID")) {
                m_tableName	= realName;
            }
        }

        return m_tableName;

    }		// getTableName

    /**
     *      Key Column has a Synonym
     *      @return true if Key Column has Synonym
     */
    public boolean isSynonym() {
        return getSynonym() == null;
    }		// isSynonym
}	// MRecordAccess



/*
 * @(#)MRecordAccess.java   02.jul 2007
 * 
 *  Fin del fichero MRecordAccess.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
