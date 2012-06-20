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
import java.sql.Timestamp;

//import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Payment {

    /**
     * Descripción de Método
     *
     *
     * @param p_C_Payment_ID
     * @param p_C_Currency_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal allocated( int p_C_Payment_ID,int p_C_Currency_ID ) throws SQLException {

        // Charge - nothing available

        String sql = "SELECT C_Charge_ID " + "FROM C_Payment " + "WHERE C_Payment_ID=?";
        int C_Charge_ID = OpenXpertya.getSQLValue( sql,p_C_Payment_ID );

        if( C_Charge_ID > 0 ) {
            return OpenXpertya.ZERO;
        }

        int C_ConversionType_ID = 0;

        // Calculate Allocated Amount

        BigDecimal allocatedAmt = getAllocatedAmt( p_C_Payment_ID,p_C_Currency_ID,C_ConversionType_ID );

        // Round

        return Currency.round( allocatedAmt,p_C_Currency_ID,null );
    }    // allocated

    /**
     * Descripción de Método
     *
     *
     * @param p_C_Payment_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal available( int p_C_Payment_ID ) throws SQLException {
        if( p_C_Payment_ID == 0 ) {
            return null;
        }

        //

        int        C_Currency_ID       = 0;
        int        C_ConversionType_ID = 0;
        BigDecimal PayAmt              = null;
        int        C_Charge_ID         = 0;

        //

        String sql = "SELECT C_Currency_ID, C_ConversionType_ID, PayAmt, C_Charge_ID " + "FROM C_Payment_v "    // corrected for AP/AR
                     + "WHERE C_Payment_ID=?";
        
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );
        //PreparedStatement pstmt = DB.prepareStatement( sql );
        
        pstmt.setInt( 1,p_C_Payment_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            C_Currency_ID       = rs.getInt( 1 );
            C_ConversionType_ID = rs.getInt( 2 );
            PayAmt              = rs.getBigDecimal( 3 );
            C_Charge_ID         = rs.getInt( 4 );
        }

        rs.close();
        pstmt.close();

        // Not found

        if( PayAmt == null ) {
            return null;
        }

        // Charge - nothing available

        if( C_Charge_ID != 0 ) {
            return OpenXpertya.ZERO;
        }

        // Calculate Allocated Amount

        BigDecimal allocatedAmt = getAllocatedAmt( p_C_Payment_ID,C_Currency_ID,C_ConversionType_ID );
        BigDecimal available = PayAmt.subtract( allocatedAmt );

        // Round

        return Currency.round( available,C_Currency_ID,null );
    }    // available

    /**
     * Descripción de Método
     *
     *
     * @param p_C_Payment_ID
     * @param p_C_Currency_ID
     * @param p_C_ConversionType_ID
     *
     * @return
     *
     * @throws SQLException
     */

    static BigDecimal getAllocatedAmt( int p_C_Payment_ID,int p_C_Currency_ID,int p_C_ConversionType_ID ) throws SQLException {

        // Calculate Allocated Amount

        BigDecimal allocatedAmt = OpenXpertya.ZERO;
        String     sql          = "SELECT a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx " + "FROM C_AllocationLine al " + " INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID) " + "WHERE al.C_Payment_ID=?" + " AND a.IsActive='Y'";

        // AND al.C_Invoice_ID IS NOT NULL;

        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );
        //PreparedStatement pstmt = DB.prepareStatement( sql );

        pstmt.setInt( 1,p_C_Payment_ID );

        ResultSet rs = pstmt.executeQuery();

        while( rs.next()) {
            int        AD_Client_ID      = rs.getInt( 1 );
            int        AD_Org_ID         = rs.getInt( 2 );
            BigDecimal amount            = rs.getBigDecimal( 3 );
            int        C_CurrencyFrom_ID = rs.getInt( 4 );
            Timestamp  DateTrx           = rs.getTimestamp( 5 );

            //

            BigDecimal allocation = Currency.convert( amount,    // .multiply(MultiplierAP),
                C_CurrencyFrom_ID,p_C_Currency_ID,DateTrx,p_C_ConversionType_ID,AD_Client_ID,AD_Org_ID );

            allocatedAmt = allocatedAmt.add( allocation );
        }

        rs.close();
        pstmt.close();

        //

        return allocatedAmt;
    }    // getAllocatedAmt
}    // Payment



/*
 *  @(#)Payment.java   23.03.06
 * 
 *  Fin del fichero Payment.java
 *  
 *  Versión 2.2
 *
 */
