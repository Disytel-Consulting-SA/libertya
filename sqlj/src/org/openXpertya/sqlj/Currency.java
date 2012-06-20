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
 * @version 2.2, 23.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Currency {

    /**
     * Descripción de Método
     *
     *
     * @param p_Amount
     * @param p_C_CurrencyFrom_ID
     * @param p_ConversionDate
     * @param p_AD_Client_ID
     * @param p_AD_Org_ID
     *
     * @return
     *
     * @throws SQLException
     */
	

    public static BigDecimal base( BigDecimal p_Amount,int p_C_CurrencyFrom_ID,Timestamp p_ConversionDate,int p_AD_Client_ID,int p_AD_Org_ID ) throws SQLException {

        // Return NULL

        if( (p_Amount == null) || (p_C_CurrencyFrom_ID == 0) ) {
            return null;
        }

        // Return Amount

        if( p_Amount.signum() == 0 ) {
            return p_Amount;
        }

        // Base Currency
        
        String sql = "SELECT ac.C_Currency_ID " + "FROM AD_ClientInfo ci" + " INNER JOIN C_AcctSchema ac ON (ci.C_AcctSchema1_ID=ac.C_AcctSchema_ID) " + "WHERE ci.AD_Client_ID=?";
        int C_CurrencyTo_ID = OpenXpertya.getSQLValue( sql,p_AD_Client_ID );

        // Return Amount

        if( p_C_CurrencyFrom_ID == C_CurrencyTo_ID ) {
            return p_Amount;
        }

        //

        return convert( p_Amount,p_C_CurrencyFrom_ID,C_CurrencyTo_ID,p_ConversionDate,0,p_AD_Client_ID,p_AD_Org_ID );
    }    // base

    /**
     * Descripción de Método
     *
     *
     * @param p_Amount
     * @param p_C_CurrencyFrom_ID
     * @param p_C_CurrencyTo_ID
     * @param p_ConversionDate
     * @param p_C_ConversionType_ID
     * @param p_AD_Client_ID
     * @param p_AD_Org_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal convert( BigDecimal p_Amount,int p_C_CurrencyFrom_ID,int p_C_CurrencyTo_ID,Timestamp p_ConversionDate,int p_C_ConversionType_ID,int p_AD_Client_ID,int p_AD_Org_ID ) throws SQLException {

        // Return NULL

        if( (p_Amount == null) || (p_C_CurrencyFrom_ID == 0) || (p_C_CurrencyTo_ID == 0) ) {
            return null;
        }

        // Return Amount

        if( (p_Amount.signum() == 0) || (p_C_CurrencyFrom_ID == p_C_CurrencyTo_ID) ) {
            return p_Amount;
        }

        // Get Rate

        BigDecimal rate = rate( p_C_CurrencyFrom_ID,p_C_CurrencyTo_ID,p_ConversionDate,p_C_ConversionType_ID,p_AD_Client_ID,p_AD_Org_ID );

        if( rate == null ) {
            return null;
        }

        // Round
        
    
        return round( p_Amount.multiply( rate ),p_C_CurrencyTo_ID,null );
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @param p_C_CurrencyFrom_ID
     * @param p_C_CurrencyTo_ID
     * @param p_ConversionDate
     * @param p_C_ConversionType_ID
     * @param p_AD_Client_ID
     * @param p_AD_Org_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal rate( int p_C_CurrencyFrom_ID,int p_C_CurrencyTo_ID,Timestamp p_ConversionDate,int p_C_ConversionType_ID,int p_AD_Client_ID,int p_AD_Org_ID, boolean useDivideRate ) throws SQLException {

        // No Conversion

        if( p_C_CurrencyFrom_ID == p_C_CurrencyTo_ID ) {
            return new BigDecimal( 1.0 );
        }

        // Get Defaults

        Timestamp ConversionDate = p_ConversionDate;

        if( ConversionDate == null ) {
            ConversionDate = new Timestamp( System.currentTimeMillis());
        }

        ConversionDate = OpenXpertya.trunc( ConversionDate );

        //

        int C_ConversionType_ID = p_C_ConversionType_ID;

        if( C_ConversionType_ID == 0 ) {
            String sql = "SELECT C_ConversionType_ID " + "FROM C_ConversionType " + "WHERE IsDefault='Y'" + " AND AD_Client_ID IN (0,?) " + "ORDER BY AD_Client_ID DESC";

            C_ConversionType_ID = OpenXpertya.getSQLValue( sql,p_AD_Client_ID );
        }

        // Get Rate

        BigDecimal rate = null;
        String     sql  = "SELECT " + (useDivideRate?"divideRate":"multiplyRate") +  " FROM C_Conversion_Rate " + "WHERE C_Currency_ID=? AND C_Currency_ID_To=?"    // from/to
                          + " AND C_ConversionType_ID=?" + " AND TRUNC(ValidFrom) <= ?" + " AND TRUNC(ValidTo) >= ?" + " AND AD_Client_ID IN (0,?) AND AD_Org_ID IN (0,?) " + " AND ISACTIVE = 'Y' ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_CurrencyFrom_ID );
        pstmt.setInt( 2,p_C_CurrencyTo_ID );
        pstmt.setInt( 3,C_ConversionType_ID );
        pstmt.setTimestamp( 4,ConversionDate );
        pstmt.setTimestamp( 5,ConversionDate );
        pstmt.setInt( 6,p_AD_Client_ID );
        pstmt.setInt( 7,p_AD_Org_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            rate = rs.getBigDecimal( 1 );
        }

        rs.close();
        pstmt.close();

        // Not found

        if( rate == null ) {
        	// si no hay conversion directa, prueba con inversa (invirtiendo currencyTo - currencyFrom), utilizando divideRate en lugar de multiplyRate.  
        	// si ya en este caso no puede hacer nada, devuelve null
            return (!useDivideRate)?rate(  p_C_CurrencyTo_ID, p_C_CurrencyFrom_ID, p_ConversionDate, p_C_ConversionType_ID, p_AD_Client_ID, p_AD_Org_ID, true ):null;
        }

        return rate;
    }    // rate

    
    // rate original que recibia un parámetro menos: useDivideRate no existia anteriormente
    public static BigDecimal rate( int p_C_CurrencyFrom_ID,int p_C_CurrencyTo_ID,Timestamp p_ConversionDate,int p_C_ConversionType_ID,int p_AD_Client_ID,int p_AD_Org_ID) throws SQLException {
    	return rate(  p_C_CurrencyFrom_ID, p_C_CurrencyTo_ID, p_ConversionDate, p_C_ConversionType_ID, p_AD_Client_ID, p_AD_Org_ID, false );
    }
    
    
    
    /**
     * Descripción de Método
     *
     *
     * @param p_Amount
     * @param p_C_Currency_ID
     * @param p_Costing
     *
     * @return
     *
     * @throws SQLException
     */

    static BigDecimal round( BigDecimal p_Amount,int p_C_Currency_ID,String p_Costing ) throws SQLException {
    	
        if( p_Amount == null ) {
            return null;
        }

        if( (p_Amount.signum() == 0) || (p_C_Currency_ID == 0) ) {
            return p_Amount;
        }

        //

        boolean costing = (p_Costing != null) && "Y".equals( p_Costing );

        //

        BigDecimal result = p_Amount;
        String     sql    = "SELECT StdPrecision, CostingPrecision " + "FROM C_Currency " + "WHERE C_Currency_ID=?";
        
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );
        //PreparedStatement pstmt = DB.prepareStatement( sql );

        pstmt.setInt( 1,p_C_Currency_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            int index = costing
                        ?1
                        :2;
            int prec  = rs.getInt( index );

            if( result.scale() > prec ) {
                result = result.setScale( prec,BigDecimal.ROUND_HALF_UP );
            }
        }

        rs.close();
        pstmt.close();

        //

        return result;
    }    // round
}    // Currency



/*
 *  @(#)Currency.java   23.03.06
 * 
 *  Fin del fichero Currency.java
 *  
 *  Versión 2.2
 *
 */
