/*
 * @(#)UpdateRecord_IDReports.java   12.oct 2007  Versión 2.2
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.logging.Level;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 02.jul 2007
 * @autor     Fundesle    
 */
public class UpdateRecord_IDReports {

    /** Descripción de Campo */
    private String	BigUpdate	= "CREATE VIEW \"ad_record_idreportnames_v\" AS ";

    /**
     * Descripción de Método
     *
     *
     * @param ad_table_id
     *
     * @return
     */
    public boolean HaveNameColumn(int ad_table_id) {

        String	sql	= "SELECT count(*) " + "FROM ad_column " + "WHERE name='Name' and ad_table_id=?";

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql);

            pstmt.setInt(1, ad_table_id);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next() && (rs.getInt(1) == 1)) {

                rs.close();

                return true;

            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Descripción de Clase
     * Update the sql view named Record_IDReportNames
     * @version 2.2, 29.06/2006
     * @author     Zarius - Dataware -
     */
    public void doIt() {

        String	sql	= "SELECT s1.ad_table_id, s1.tablename, s2.columnname " + "from " + "(SELECT T.ad_table_id, T.tablename " + "from ad_column C, ad_table T " + "where C.name='Name' " + "and T.ad_table_id=C.ad_table_id and T.isview='N'" + "and T.tablename NOT LIKE 'T_%') as s1, " + "(select columnname, ad_table_id from ad_column " + "where ad_reference_id=13 " + "order by columnname) as s2 " + "where s2.ad_table_id=s1.ad_table_id ";

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql);
            ResultSet		rs	= pstmt.executeQuery();
            int			i	= 0;

            while (rs.next()) {

                if (!rs.getString(2).contains("_Trl")) {

                    if (!rs.getString(2).contains("_trl")) {

                        if (i == 0) {
                            BigUpdate	= BigUpdate + "";
                        } else {
                            BigUpdate	= BigUpdate + " Union ";
                        }

                        BigUpdate	= BigUpdate + " SELECT " + rs.getString(1) + " as A, '" +	// ad_table_id
                            rs.getString(2) + "'::text as T, " +						// tablename
                                rs.getString(2) + ".name::text, " + rs.getString(3) + "::int4 as R " +		// Record_ID
                                    "FROM " + rs.getString(2);
                        i++;
                    }
                }
            }

            rs.close();

            int	no	= DB.executeUpdate("DROP VIEW ad_record_idreportnames_v");
            int	na	= DB.executeUpdate(BigUpdate);

            pstmt.close();

        } catch (Exception e) {}
    }

    /**
     * Descripción de Clase
     * prepara el evento:
     *  - recogemos si son 12 o 14 pagas.
     *  - si no hay periodos error.
     * @version 2.2, 29.06/2006
     * @author     Dataware
     */
    protected void prepare() {}
}



/*
 * @(#)UpdateRecord_IDReports.java   02.jul 2007
 * 
 *  Fin del fichero UpdateRecord_IDReports.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
