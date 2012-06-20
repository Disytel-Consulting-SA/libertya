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

//import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.1, 23.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Invoice {

    /**
     * Descripción de Método
     *
     *
     * @param p_C_Invoice_ID
     * @param p_C_InvoicePaySchedule_ID
     *
     * @return
     *
     * @throws SQLException
     */
	//private static CLogger log = CLogger.getCLogger( Invoice.class );
	
    public static BigDecimal open( int p_C_Invoice_ID,int p_C_InvoicePaySchedule_ID ) throws SQLException {
		return open(p_C_Invoice_ID, p_C_InvoicePaySchedule_ID, null);
	}

	public static BigDecimal open(int p_C_Invoice_ID, Timestamp date) throws SQLException {
		return open(p_C_Invoice_ID, 0, date);
	}

	
    public static BigDecimal open( int p_C_Invoice_ID,int p_C_InvoicePaySchedule_ID, Timestamp date) throws SQLException {

        // Invoice info

        int        C_Currency_ID       = 0;
        int        C_ConversionType_ID = 0;
        BigDecimal GrandTotal          = null;
        BigDecimal MultiplierAP        = null;
        BigDecimal MultiplierCM        = null;
        BigDecimal TotalOpenAmt;
        BigDecimal paidAmt;        
        //

        String sql = "SELECT MAX(C_Currency_ID),MAX(C_ConversionType_ID)," + " SUM(GrandTotal), MAX(MultiplierAP), MAX(Multiplier) " + "FROM C_Invoice_v "    // corrected for CM / Split Payment
                     + "WHERE C_Invoice_ID=?";
       // String sql = "SELECT MAX(C_Currency_ID),MAX(C_ConversionType_ID)," + " MAX(GrandTotal), MAX(MultiplierAP), MAX(Multiplier) " + "FROM C_Invoice_v "    // corrected for CM / Split Payment
       // + "WHERE C_Invoice_ID=?";
        
        if( p_C_InvoicePaySchedule_ID != 0 ) {
        	sql += " AND C_InvoicePaySchedule_ID=?";
        }
        

        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );
        //PreparedStatement pstmt = DB.prepareStatement( sql );

        pstmt.setInt( 1,p_C_Invoice_ID );

        if( p_C_InvoicePaySchedule_ID != 0 ) {
            pstmt.setInt( 2,p_C_InvoicePaySchedule_ID );
        }
        

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            C_Currency_ID       = rs.getInt( 1 );
            C_ConversionType_ID = rs.getInt( 2 );
            GrandTotal          = rs.getBigDecimal( 3 );
            MultiplierAP        = new BigDecimal(rs.getFloat( 4 ));
            MultiplierCM        = new BigDecimal(rs.getFloat( 5 ));


        }

        rs.close();
        pstmt.close();

        // No Invoice

        if( GrandTotal == null ) {
            return null;
        }
        //Modificado por ConSerTi, en la base de datos, los multiplicadores estan definidos como enteros, y los esta cogiendo
        // como BigDecimal, y da el siguiente error :"Cannot derive a value of class java.math.BigDecimal from an object of class java.lang.Integer".
        //BigDecimal MultiplierApBig = new BigDecimal(MultiplierAP);
        //BigDecimal MultiplierCMBig = new BigDecimal(MultiplierCM);
        
        // Do we have a Payment Schedule ?
        
        paidAmt = getAllocatedAmt( p_C_Invoice_ID,C_Currency_ID,C_ConversionType_ID, MultiplierAP );


        
        if( p_C_InvoicePaySchedule_ID == 0 ) {  
        	if (date != null) {
        		BigDecimal dateGrandTotal;
        		sql = "SELECT DueAmt, DueDate FROM C_InvoicePaySchedule WHERE C_Invoice_ID = ?";
        		
        		pstmt = OpenXpertya.prepareStatement(sql);
        		//pstmt = DB.prepareStatement( sql );
        		
        		pstmt.setInt(1, p_C_Invoice_ID);
        		rs = pstmt.executeQuery();
        		if (rs.next()) { // There's pay schedule
        			dateGrandTotal = new BigDecimal(0);
        			do {
            			if (rs.getTimestamp(2).compareTo(date) < 0) {
            				dateGrandTotal = dateGrandTotal.add(rs.getBigDecimal(1));
            			}
        			} while (rs.next());
        			GrandTotal = dateGrandTotal.multiply(MultiplierCM);
        		} // Else, grandtotal's still invoice grand total

        		//TotalOpenAmt = GrandTotal.add( paidAmt.multiply(MultiplierAP).multiply(MultiplierCM).multiply(new BigDecimal(-1)));
        		TotalOpenAmt = GrandTotal.subtract( paidAmt );

        	} else {
                //TotalOpenAmt = GrandTotal.add( paidAmt.multiply(MultiplierAP).multiply(MultiplierCM).multiply(new BigDecimal(-1)));
        		TotalOpenAmt = GrandTotal.subtract( paidAmt );
        	}
        } else {
            TotalOpenAmt = GrandTotal;

            BigDecimal remainingAmt = paidAmt;

            sql = "SELECT C_InvoicePaySchedule_ID, DueAmt " + "FROM C_InvoicePaySchedule " + "WHERE C_Invoice_ID=?" + " AND IsValid='Y' " + "ORDER BY DueDate";
            
            pstmt = OpenXpertya.prepareStatement( sql );
            //pstmt = DB.prepareStatement( sql );
            
            pstmt.setInt( 1,p_C_Invoice_ID );
            rs = pstmt.executeQuery();
            
            while( rs.next()) {
                int        C_InvoicePaySchedule_ID = rs.getInt( 1 );
                BigDecimal DueAmt                  = rs.getBigDecimal( 2 );

                //

                if( C_InvoicePaySchedule_ID == p_C_InvoicePaySchedule_ID ) {
                    if( DueAmt.signum() > 0 )                           // positive
                    {
                        if( DueAmt.compareTo( remainingAmt ) < 0 ) {    // paid more
                            TotalOpenAmt = OpenXpertya.ZERO;
                        } else {
                            //TotalOpenAmt = DueAmt.multiply( MultiplierCM ).subtract( remainingAmt );
                            TotalOpenAmt = DueAmt.subtract( remainingAmt );
                        }
                    } else {
                        if( DueAmt.compareTo( remainingAmt ) > 0 ) {    // paid more
                            TotalOpenAmt = OpenXpertya.ZERO;
                        } else {
                            //TotalOpenAmt = DueAmt.multiply( MultiplierCM ).add( remainingAmt );
                            TotalOpenAmt = DueAmt.subtract( remainingAmt );
                        }
                    }
                } else { //Pagos sobrantes
                    if( DueAmt.signum() > 0 )    // positive
                    {
                        remainingAmt = remainingAmt.subtract( DueAmt );

                        if( remainingAmt.signum() < 0 ) {
                            remainingAmt = OpenXpertya.ZERO;
                        }
                    } else {
                        remainingAmt = remainingAmt.add( DueAmt );

                        if( remainingAmt.signum() < 0 ) {
                            remainingAmt = OpenXpertya.ZERO;
                        }
                    }
                }
            }

            rs.close();
            pstmt.close();
        }                                        // Invoice Schedule

        // Rounding

        TotalOpenAmt = Currency.round( TotalOpenAmt,C_Currency_ID,null );
        //log.fine("Esto es lo que devuelve TotalAmount="+TotalOpenAmt);
        // Ignore Penny

        double open = TotalOpenAmt.doubleValue();

        if( (open >= -0.01) && (open <= 0.01) ) {
            TotalOpenAmt = OpenXpertya.ZERO;
        }

      
        return TotalOpenAmt;
      
    }    // open

    /**
     * Descripción de Método
     *
     *
     * @param p_C_Invoice_ID
     * @param p_C_Currency_ID
     * @param p_MultiplierAP
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal paid( int p_C_Invoice_ID,int p_C_Currency_ID,int p_MultiplierAP ) throws SQLException {
    	

        // Invalid Parameters
    	
        if( (p_C_Invoice_ID == 0) || (p_C_Currency_ID == 0) ) {
            return null;
        }

        // Parameters

        BigDecimal MultiplierAP = new BigDecimal( p_MultiplierAP );

        if( p_MultiplierAP == 0 ) {
            MultiplierAP = new BigDecimal( 1.0 );
        }

        int C_ConversionType_ID = 0;

        // Calculate Allocated Amount

        BigDecimal paymentAmt = getAllocatedAmt( p_C_Invoice_ID,p_C_Currency_ID,C_ConversionType_ID,MultiplierAP );

        return Currency.round( paymentAmt,p_C_Currency_ID,null );
    }    // paid

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     * @param C_Currency_ID
     * @param C_ConversionType_ID
     * @param MultiplierAP
     *
     * @return
     *
     * @throws SQLException
     */

    static BigDecimal getAllocatedAmt( int C_Invoice_ID,int C_Currency_ID,int C_ConversionType_ID,BigDecimal MultiplierAP ) throws SQLException {

        // Calculate Allocated Amount

        BigDecimal paidAmt = OpenXpertya.ZERO;
        String     sql     = "SELECT a.AD_Client_ID, a.AD_Org_ID," + 
                             " al.Amount, al.DiscountAmt, al.WriteOffAmt," + 
                             " a.C_Currency_ID, a.DateTrx, al.C_Invoice_Credit_ID " + 
                             "FROM C_AllocationLine al " + 
                             "INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID) " + 
                             "WHERE ( al.C_Invoice_ID=? OR al.C_Invoice_Credit_ID =  ? ) AND a.IsActive='Y'";
        
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );
        //PreparedStatement pstmt = DB.prepareStatement( sql );
        
        pstmt.setInt( 1,C_Invoice_ID );
        pstmt.setInt( 2,C_Invoice_ID );

        ResultSet rs = pstmt.executeQuery();

        while( rs.next()) {
            int        AD_Client_ID      = rs.getInt( 1 );
            int        AD_Org_ID         = rs.getInt( 2 );
            BigDecimal Amount            = rs.getBigDecimal( 3 );
            BigDecimal DiscountAmt       = rs.getBigDecimal( 4 );
            BigDecimal WriteOffAmt       = rs.getBigDecimal( 5 );
            int        C_CurrencyFrom_ID = rs.getInt( 6 );
            Timestamp  DateTrx           = rs.getTimestamp( 7 );
            // -----------------------------------------
            // Added by Franco Bonafine
            boolean has_invoice_credit_id = !(rs.getObject(8) == null); 
            int C_Invoice_Credit_ID      = (rs.getObject(8) == null?0:rs.getInt(8));  
            
            BigDecimal invAmt;
            // The invoice was allocated as a payment
            if(has_invoice_credit_id && C_Invoice_Credit_ID == C_Invoice_ID)
            	invAmt = Amount;
            else
            	invAmt = Amount.add( DiscountAmt ).add( WriteOffAmt );
            // -----------------------------------------
            
            BigDecimal allocation = Currency.convert( invAmt.multiply( MultiplierAP ),C_CurrencyFrom_ID,C_Currency_ID,DateTrx,C_ConversionType_ID,AD_Client_ID,AD_Org_ID );

            paidAmt = paidAmt.add( allocation );
        }

        rs.close();
        pstmt.close();

        //

        return paidAmt;
    }    // getAllocatedAmt

    /**
     * Descripción de Método
     *
     *
     * @param p_C_Invoice_ID
     * @param p_PayDate
     * @param p_C_InvoicePaySchedule_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal discount( int p_C_Invoice_ID,Timestamp p_PayDate,int p_C_InvoicePaySchedule_ID ) throws SQLException {

        // Parameters

        if( p_C_Invoice_ID == 0 ) {
            return null;
        }

        Timestamp PayDate = p_PayDate;

        if( PayDate == null ) {
            PayDate = new Timestamp( System.currentTimeMillis());
        }

        PayDate = OpenXpertya.trunc( PayDate );

        // Invoice Info

        boolean    IsDiscountLineAmt  = false;
        BigDecimal GrandTotal         = null;
        BigDecimal TotalLines         = null;
        int        C_PaymentTerm_ID   = 0;
        Timestamp  DateInvoiced       = null;
        boolean    IsPayScheduleValid = false;
        String     sql                = "SELECT ci.IsDiscountLineAmt, i.GrandTotal, i.TotalLines, " + " i.C_PaymentTerm_ID, i.DateInvoiced, i.IsPayScheduleValid " + "FROM C_Invoice i" + " INNER JOIN AD_ClientInfo ci ON (ci.AD_Client_ID=i.AD_Client_ID) " + "WHERE i.C_Invoice_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_Invoice_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            IsDiscountLineAmt  = "Y".equals( rs.getString( 1 ));
            GrandTotal         = rs.getBigDecimal( 2 );
            TotalLines         = rs.getBigDecimal( 3 );
            C_PaymentTerm_ID   = rs.getInt( 4 );
            DateInvoiced       = rs.getTimestamp( 5 );
            IsPayScheduleValid = "Y".equals( rs.getString( 6 ));
        }

        rs.close();
        pstmt.close();

        // Not found

        if( GrandTotal == null ) {
            return null;
        }

        // What Amount is the Discount Base?

        BigDecimal amount = GrandTotal;

        if( IsDiscountLineAmt ) {
            amount = TotalLines;
        }

        // Anything to discount?

        if( amount.signum() == 0 ) {
            return OpenXpertya.ZERO;
        }

        // Valid Payment Schedule (has discount)

        if( IsPayScheduleValid && (p_C_InvoicePaySchedule_ID > 0) ) {
            BigDecimal discount = OpenXpertya.ZERO;
            sql = "SELECT DiscountAmt " + "FROM C_InvoicePaySchedule " + "WHERE C_InvoicePaySchedule_ID=?" + " AND TRUNC(DiscountDate) <= ?";//Original
           // sql = "SELECT DiscountAmt " + "FROM C_InvoicePaySchedule " + "WHERE C_InvoicePaySchedule_ID=?" + " AND DiscountDate <= ?";
            pstmt = OpenXpertya.prepareStatement( sql );
            pstmt.setInt( 1,p_C_InvoicePaySchedule_ID );
            pstmt.setTimestamp( 2,PayDate );
            rs = pstmt.executeQuery();

            if( rs.next()) {
                discount = rs.getBigDecimal( 1 );
            }

            rs.close();
            pstmt.close();

            //

            return discount;
        }

        // return discount amount

        return PaymentTerm.discount( amount,C_PaymentTerm_ID,DateInvoiced,PayDate );
    }    // discount
}    // Invoice



/*
 *  @(#)Invoice.java   23.03.06
 * 
 *  Fin del fichero Invoice.java
 *  
 *  Versión 2.1
 *
 */
