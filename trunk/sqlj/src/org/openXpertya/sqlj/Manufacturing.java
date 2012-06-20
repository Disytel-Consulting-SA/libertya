/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.sqlj;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Manufacturing {

    /**
     * Descripción de Método
     *
     *
     * @param p_MPC_MRP_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static String documentNo( int p_MPC_MRP_ID ) throws SQLException {
        if( p_MPC_MRP_ID == 0 ) {
            return "";
        }

        //

        String documentNo = "";

        // Get Base Info

        String sql = "SELECT CASE WHEN mrp.TypeMRP = 'FTC' THEN (SELECT f.Name FROM M_Forecast f WHERE f.M_Forecast_ID=mrp.M_Forecast_ID) " + "WHEN mrp.TypeMRP = 'POO' THEN (SELECT o.DocumentNo FROM C_Order o WHERE o.C_Order_ID=mrp.C_Order_ID) " + "WHEN mrp.TypeMRP = 'SOO' THEN (SELECT o.DocumentNo FROM C_Order o WHERE o.C_Order_ID=mrp.C_Order_ID) " + "WHEN mrp.TypeMRP = 'MOP' THEN (SELECT o.DocumentNo FROM MPC_Order o WHERE o.MPC_Order_ID=mrp.MPC_Order_ID) " + "WHEN mrp.TypeMRP = 'POR' THEN (SELECT r.DocumentNo FROM M_Requisition r WHERE r.M_Requisition_ID=mrp.M_Requisition_ID) END AS DocumentNo " + "FROM MPC_MRP mrp WHERE mrp.MPC_MRP_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_MPC_MRP_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            documentNo = rs.getString( 1 );
        }

        rs.close();
        pstmt.close();

        return documentNo;
    }    // getdocumentNo
}    // Manufacturing



/*
 *  @(#)Manufacturing.java   23.03.06
 * 
 *  Fin del fichero Manufacturing.java
 *  
 *  Versión 2.2
 *
 */
