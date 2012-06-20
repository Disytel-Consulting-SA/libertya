package org.openXpertya.sqlj;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CashLine {

    public static BigDecimal available( int p_C_CashLine_ID ) throws SQLException {
        if( p_C_CashLine_ID == 0 ) {
            return null;
        }

        //

        int        C_Currency_ID       = 0;
        int        C_ConversionType_ID = 0;
        BigDecimal amt              = null;
        //

        String sql = " SELECT C_Currency_ID, Amount " + 
                     " FROM C_CashLine " + 
                     " WHERE C_CashLine_ID=?";
        
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_CashLine_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            C_Currency_ID       = rs.getInt( "C_Currency_ID" );
            C_ConversionType_ID = 0;
            amt              = rs.getBigDecimal( "Amount" );
        }

        rs.close();
        pstmt.close();

        // Not found

        if( amt == null ) {
            return null;
        }

        // Calculate Allocated Amount
        int signum = amt.signum();
        BigDecimal allocatedAmt = getAllocatedAmt( p_C_CashLine_ID,C_Currency_ID,C_ConversionType_ID );
        BigDecimal available = amt.abs().subtract( allocatedAmt ).multiply(new BigDecimal(signum));

        // Round

        return Currency.round( available,C_Currency_ID,null );
    }    // available

    /**
     * Descripción de Método
     *
     *
     * @param p_C_CashLine_ID
     * @param p_C_Currency_ID
     * @param p_C_ConversionType_ID
     *
     * @return
     *
     * @throws SQLException
     */

    static BigDecimal getAllocatedAmt( int p_C_CashLine_ID,int p_C_Currency_ID,int p_C_ConversionType_ID ) throws SQLException {

        // Calculate Allocated Amount

        BigDecimal allocatedAmt = OpenXpertya.ZERO;
        String     sql          = " SELECT a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx " + 
                                  " FROM C_AllocationLine al " + 
                                  " INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID) " + 
                                  " WHERE al.C_CashLine_ID=?" + " AND a.IsActive='Y'";

        // AND al.C_Invoice_ID IS NOT NULL;

        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_CashLine_ID );

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
}    

	

