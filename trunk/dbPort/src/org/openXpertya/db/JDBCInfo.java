/*
 * @(#)JDBCInfo.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.db;

import org.openXpertya.OpenXpertya;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.logging.Level;

/**
 *  JDBC Meta Info
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: JDBCInfo.java,v 1.4 2005/03/11 20:29:00 jjanke Exp $
 */
public class JDBCInfo {

    /** Logger */
    private static CLogger	log	= CLogger.getCLogger(JDBCInfo.class);

    /** Mata Data */
    private DatabaseMetaData	m_md	= null;

    /**
     *  Constructor
     *
     * @param conn
     *
     * @throws SQLException
     */
    public JDBCInfo(Connection conn) throws SQLException {

        m_md	= conn.getMetaData();
        log.info(m_md.getDatabaseProductName());
        log.config(m_md.getDatabaseProductVersion());

        // log.config(m_md.getDatabaseMajorVersion() + "/" + m_md.getDatabaseMinorVersion());
        //
        log.info(m_md.getDriverName());
        log.config(m_md.getDriverVersion());
        log.config(m_md.getDriverMajorVersion() + "/" + m_md.getDriverMinorVersion());

        //
        // log.info("JDBC = " + m_md.getJDBCMajorVersion() + "/" + m_md.getJDBCMinorVersion());

    }		// JDBCInfo

    /**
     *      Dump the current row of a Result Set
     *      @param rs result set
     *
     * @throws SQLException
     */
    public static void dump(ResultSet rs) throws SQLException {

        ResultSetMetaData	md	= rs.getMetaData();

        for (int i = 0; i < md.getColumnCount(); i++) {

            int		index	= i + 1;
            String	info	= md.getColumnLabel(index);
            String	name	= md.getColumnName(index);

            if (info == null) {
                info	= name;
            } else if ((name != null) &&!name.equals(info)) {
                info	+= " (" + name + ")";
            }

            info	+= " = " + rs.getString(index);
            info	+= " [" + md.getColumnTypeName(index) + "(" + md.getPrecision(index);

            if (md.getScale(index) != 0) {
                info	+= "," + md.getScale(index);
            }

            info	+= ")]";
            log.fine(info);
        }

    }		// dump

    /**
     *      List Catalogs
     *
     * @throws SQLException
     */
    public void listCatalogs() throws SQLException {

        log.info(m_md.getCatalogTerm() + " -> " + m_md.getCatalogSeparator());

        ResultSet	rs	= m_md.getCatalogs();

        while (rs.next()) {
            dump(rs);
        }

    }		// listCatalogs

    /**
     *      List Schemas
     *
     * @throws SQLException
     */
    public void listSchemas() throws SQLException {

        log.info(m_md.getSchemaTerm());

        ResultSet	rs	= m_md.getSchemas();

        while (rs.next()) {
            dump(rs);
        }

    }		// listSchemas

    /**
     *      List Types
     *
     * @throws SQLException
     */
    public void listTypes() throws SQLException {

        ResultSet	rs	= m_md.getTypeInfo();

        while (rs.next()) {

            log.info("");
            dump(rs);
        }

    }		// listTypes

    /**
     *      Test
     *      @param args ignored
     */
    public static void main(String[] args) {

        OpenXpertya.startup(true);
        CLogMgt.setLevel(Level.ALL);

        //
        try {

            JDBCInfo	info	= new JDBCInfo(DB.createConnection(true, Connection.TRANSACTION_READ_COMMITTED));

            info.listCatalogs();
            info.listSchemas();
            info.listTypes();

        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }

    }		// main
}	// JDBCInfo



/*
 * @(#)JDBCInfo.java   02.jul 2007
 * 
 *  Fin del fichero JDBCInfo.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
