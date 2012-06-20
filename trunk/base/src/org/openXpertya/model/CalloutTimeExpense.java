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



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutTimeExpense extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer M_Product_ID = ( Integer )value;

        if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            return "";
        }

        setCalloutActive( true );

        BigDecimal priceActual = null;

        // get expense date - or default to today's date

        Timestamp DateExpense = Env.getContextAsDate( ctx,WindowNo,"DateExpense" );

        if( DateExpense == null ) {
            DateExpense = new Timestamp( System.currentTimeMillis());
        }

        try {
            boolean noPrice = true;

            // Search Pricelist for current version

            String sql = "SELECT bomPriceStd(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceStd," + "bomPriceList(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceList," + "bomPriceLimit(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceLimit," + "p.C_UOM_ID,pv.ValidFrom,pl.C_Currency_ID " + "FROM M_Product p, M_ProductPrice pp, M_Pricelist pl, M_PriceList_Version pv " + "WHERE p.M_Product_ID=pp.M_Product_ID" + " AND pp.M_PriceList_Version_ID=pv.M_PriceList_Version_ID" + " AND pv.M_PriceList_ID=pl.M_PriceList_ID" + " AND pv.IsActive='Y'" + " AND p.M_Product_ID=?"    // 1
                         + " AND pl.M_PriceList_ID=?"    // 2
                         + " ORDER BY pv.ValidFrom DESC";
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,M_Product_ID.intValue());
            pstmt.setInt( 2,Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" ));

            ResultSet rs = pstmt.executeQuery();

            while( rs.next() && noPrice ) {
                java.sql.Date plDate = rs.getDate( "ValidFrom" );

                // we have the price list
                // if order date is after or equal PriceList validFrom

                if( (plDate == null) ||!DateExpense.before( plDate )) {
                    noPrice = false;

                    // Price

                    priceActual = rs.getBigDecimal( "PriceStd" );

                    if( priceActual == null ) {
                        priceActual = rs.getBigDecimal( "PriceList" );
                    }

                    if( priceActual == null ) {
                        priceActual = rs.getBigDecimal( "PriceLimit" );
                    }

                    // Currency

                    Integer ii = new Integer( rs.getInt( "C_Currency_ID" ));

                    if( !rs.wasNull()) {
                        mTab.setValue( "C_Currency_ID",ii );
                    }
                }
            }

            rs.close();
            pstmt.close();

            // no prices yet - look base pricelist

            if( noPrice ) {

                // Find if via Base Pricelist

                sql = "SELECT bomPriceStd(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceStd," + "bomPriceList(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceList," + "bomPriceLimit(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceLimit," + "p.C_UOM_ID,pv.ValidFrom,pl.C_Currency_ID " + "FROM M_Product p, M_ProductPrice pp, M_Pricelist pl, M_Pricelist bpl, M_PriceList_Version pv " + "WHERE p.M_Product_ID=pp.M_Product_ID" + " AND pp.M_PriceList_Version_ID=pv.M_PriceList_Version_ID" + " AND pv.M_PriceList_ID=bpl.M_PriceList_ID" + " AND pv.IsActive='Y'" + " AND bpl.M_PriceList_ID=pl.BasePriceList_ID"    // Base
                      + " AND p.M_Product_ID=?"       // 1
                      + " AND pl.M_PriceList_ID=?"    // 2
                      + " ORDER BY pv.ValidFrom DESC";
                pstmt = DB.prepareStatement( sql );
                pstmt.setInt( 1,M_Product_ID.intValue());
                pstmt.setInt( 2,Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" ));
                rs = pstmt.executeQuery();

                while( rs.next() && noPrice ) {
                    java.sql.Date plDate = rs.getDate( "ValidFrom" );

                    // we have the price list
                    // if order date is after or equal PriceList validFrom

                    if( (plDate == null) ||!DateExpense.before( plDate )) {
                        noPrice = false;

                        // Price

                        priceActual = rs.getBigDecimal( "PriceStd" );

                        if( priceActual == null ) {
                            priceActual = rs.getBigDecimal( "PriceList" );
                        }

                        if( priceActual == null ) {
                            priceActual = rs.getBigDecimal( "PriceLimit" );
                        }

                        // Currency

                        Integer ii = new Integer( rs.getInt( "C_Currency_ID" ));

                        if( !rs.wasNull()) {
                            mTab.setValue( "C_Currency_ID",ii );
                        }
                    }
                }

                rs.close();
                pstmt.close();
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"Expense_Product",e );
            setCalloutActive( false );

            return e.getLocalizedMessage();
        }

        // finish

        setCalloutActive( false );    // calculate amount

        if( priceActual == null ) {
            priceActual = Env.ZERO;
        }

        mTab.setValue( "ExpenseAmt",priceActual );

        return "";
    }    // Expense_Product

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String amount( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive()) {
            return "";
        }

        setCalloutActive( true );

        // get values

        BigDecimal ExpenseAmt         = ( BigDecimal )mTab.getValue( "ExpenseAmt" );
        Integer    C_Currency_From_ID = ( Integer )mTab.getValue( "C_Currency_ID" );
        int       C_Currency_To_ID = Env.getContextAsInt( ctx,"$C_Currency_ID" );
        Timestamp DateExpense      = Env.getContextAsDate( ctx,WindowNo,"DateExpense" );

        //

        log.fine( "Amt=" + ExpenseAmt + ", C_Currency_ID=" + C_Currency_From_ID );

        // Converted Amount = Unit price

        BigDecimal ConvertedAmt = ExpenseAmt;

        // convert if required

        if( !ConvertedAmt.equals( Env.ZERO ) && (C_Currency_To_ID != C_Currency_From_ID.intValue())) {
            int AD_Client_ID = Env.getContextAsInt( ctx,WindowNo,"AD_Client_ID" );
            int AD_Org_ID = Env.getContextAsInt( ctx,WindowNo,"AD_Org_ID" );

            ConvertedAmt = MConversionRate.convert( ctx,ConvertedAmt,C_Currency_From_ID.intValue(),C_Currency_To_ID,DateExpense,0,AD_Client_ID,AD_Org_ID );
        }

        mTab.setValue( "ConvertedAmt",ConvertedAmt );
        log.fine( "= ConvertedAmt=" + ConvertedAmt );
        setCalloutActive( false );

        return "";
    }    // Expense_Amount
}    // CalloutTimeExpense



/*
 *  @(#)CalloutTimeExpense.java   02.07.07
 * 
 *  Fin del fichero CalloutTimeExpense.java
 *  
 *  Versión 2.2
 *
 */
