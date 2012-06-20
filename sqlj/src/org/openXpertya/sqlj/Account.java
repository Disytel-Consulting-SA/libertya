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

import java.math.BigDecimal;
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

public class Account {

    /**
     * Descripción de Método
     *
     *
     * @param p_Account_ID
     * @param p_AmtDr
     * @param p_AmtCr
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal balance( int p_Account_ID,BigDecimal p_AmtDr,BigDecimal p_AmtCr ) throws SQLException {
        BigDecimal AmtDr = p_AmtDr;

        if( AmtDr == null ) {
            AmtDr = OpenXpertya.ZERO;
        }

        BigDecimal AmtCr = p_AmtCr;

        if( AmtCr == null ) {
            AmtCr = OpenXpertya.ZERO;
        }

        BigDecimal balance = AmtDr.subtract( AmtCr );

        //

        if( p_Account_ID != 0 ) {
            String sql = "SELECT AccountType, AccountSign " + "FROM C_ElementValue " + "WHERE C_ElementValue_ID=?";
            PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

            pstmt.setInt( 1,p_Account_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                String AccountType = rs.getString( 1 );
                String AccountSign = rs.getString( 2 );

                // Natural Account Sign -> D/C

                if( AccountSign.equals( "N" )) {
                    if( AccountType.equals( "A" ) || AccountType.equals( "E" )) {
                        AccountSign = "D";
                    } else {
                        AccountSign = "C";
                    }
                }

                // Debit Balance

                if( AccountSign.equals( "C" )) {
                    balance = AmtCr.subtract( AmtDr );
                }
            }

            rs.close();
            pstmt.close();
        }

        //

        return balance;
    }    // balance
}    // Account



/*
 *  @(#)Account.java   23.03.06
 * 
 *  Fin del fichero Account.java
 *  
 *  Versión 2.2
 *
 */
