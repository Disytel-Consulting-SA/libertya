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

public class BPartner {

    /**
     * Descripción de Método
     *
     *
     * @param p_C_BPartner_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static int remitLocation( int p_C_BPartner_ID ) throws SQLException {
        int    C_Location_ID = 0;
        String sql           = "SELECT IsRemitTo, C_Location_ID " + "FROM C_BPartner_Location " + "WHERE C_BPartner_ID=? " + "ORDER BY IsRemitTo DESC";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_BPartner_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            C_Location_ID = rs.getInt( 2 );
        }

        rs.close();
        pstmt.close();

        //

        return C_Location_ID;
    }    // remitLocation
}    // BPartner



/*
 *  @(#)BPartner.java   23.03.06
 * 
 *  Fin del fichero BPartner.java
 *  
 *  Versión 2.2
 *
 */
