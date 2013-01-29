/*
 * @(#)MColumnAccess.java   12.oct 2007  Versión 2.2
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
 *      Column Access Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MColumnAccess.java,v 1.7 2005/03/11 20:28:33 jjanke Exp $
 */
public class MColumnAccess extends X_AD_Column_Access {

    /** ColumnName */
    private String	m_columnName;

    /** TableName */
    private String	m_tableName;

    /**
     *      Persistency Constructor
     *      @param ctx context
     * @param ignored
     * @param trxName
     */
    public MColumnAccess(Properties ctx, int ignored, String trxName) {

        super(ctx, 0, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

    }		// MColumnAccess

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MColumnAccess(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MColumnAccess

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MColumnAccess[");

        sb.append("AD_Role_ID=").append(getAD_Role_ID()).append(",AD_Table_ID=").append(getAD_Table_ID()).append(",AD_Column_ID=").append(getAD_Column_ID()).append(",Exclude=").append(isExclude());
        sb.append("]");

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

        sb.append(Msg.translate(ctx, "AD_Table_ID")).append("=").append(getTableName(ctx)).append(", ").append(Msg.translate(ctx, "AD_Column_ID")).append("=").append(getColumnName(ctx)).append(" (").append(Msg.translate(ctx, "IsReadOnly")).append("=").append(isReadOnly()).append(") - ").append(isExclude()
                ? ex
                : in);

        return sb.toString();

    }		// toStringX

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Column Name
     *      @param ctx context
     *      @return column name
     */
    public String getColumnName(Properties ctx) {

        if (m_columnName == null) {

            String	sql	= "SELECT t.TableName,c.ColumnName, t.AD_Table_ID " + "FROM AD_Table t INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID) " + "WHERE AD_Column_ID=?";
            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql, get_TrxName());
                pstmt.setInt(1, getAD_Column_ID());

                ResultSet	rs	= pstmt.executeQuery();

                if (rs.next()) {

                    m_tableName		= rs.getString(1);
                    m_columnName	= rs.getString(2);

                    if (rs.getInt(3) != getAD_Table_ID()) {
                        log.log(Level.SEVERE, "getColumnName - AD_Table_ID inconsistent - Access=" + getAD_Table_ID() + " - Table=" + rs.getInt(3));
                    }
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

            m_columnName	= Msg.translate(ctx, m_columnName);
        }

        return m_columnName;

    }		// getColumnName

    /**
     *      Get Table Name
     *      @param ctx context
     *      @return table name
     */
    public String getTableName(Properties ctx) {

        if (m_tableName == null) {
            getColumnName(ctx);
        }

        return m_tableName;

    }		// getTableName
}	// MColumnAccess



/*
 * @(#)MColumnAccess.java   02.jul 2007
 * 
 *  Fin del fichero MColumnAccess.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
