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



package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Doc_Order extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_Order( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }

    /** Descripción de Campos */

    private DocTax[] m_taxes = null;

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        try {
            p_vo.DateDoc     = rs.getTimestamp( "DateOrdered" );
            p_vo.TaxIncluded = rs.getString( "IsTaxIncluded" ).equals( "Y" );

            // Amounts

            p_vo.Amounts[ Doc.AMTTYPE_Gross ] = rs.getBigDecimal( "GrandTotal" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Gross ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Gross ] = Env.ZERO;
            }

            p_vo.Amounts[ Doc.AMTTYPE_Net ] = rs.getBigDecimal( "TotalLines" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Net ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Net ] = Env.ZERO;
            }

            p_vo.Amounts[ Doc.AMTTYPE_Charge ] = rs.getBigDecimal( "ChargeAmt" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Charge ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Charge ] = Env.ZERO;
            }

            // No DocType (e.g. voided) - assumes that document is priperly voided

            if( p_vo.C_DocType_ID == 0 ) {
                p_vo.C_DocType_ID = rs.getInt( "C_DocTypeTarget_ID" );
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        loadDocumentType();    // lines require doc type

        // Contained Objects

        p_lines = loadLines();
        m_taxes = loadTaxes();

        // log.fine( "Lines=" + p_lines.length + ", Taxes=" + m_taxes.length);

        return true;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private DocLine[] loadLines() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_OrderLine WHERE C_Order_ID=? ORDER BY Line";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int     Line_ID = rs.getInt( "C_OrderLine_ID" );
                DocLine docLine = new DocLine( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                docLine.loadAttributes( rs,p_vo );

                BigDecimal Qty = rs.getBigDecimal( "QtyOrdered" );

                docLine.setQty( Qty,p_vo.DocumentType.equals( DOCTYPE_SOrder ));

                BigDecimal LineNetAmt = rs.getBigDecimal( "LineNetAmt" );

                // BigDecimal PriceList = rs.getBigDecimal("PriceList");

                docLine.setAmount( LineNetAmt );
                list.add( docLine );
            }

            //

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadLines",e );
        }

        // Return Array

        DocLine[] dl = new DocLine[ list.size()];

        list.toArray( dl );

        return dl;
    }    // loadLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private DocTax[] loadTaxes() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT it.C_Tax_ID, t.Name, t.Rate, it.TaxBaseAmt, it.TaxAmt " + "FROM C_Tax t, C_OrderTax it " + "WHERE t.C_Tax_ID=it.C_Tax_ID AND it.C_Order_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int        C_Tax_ID   = rs.getInt( 1 );
                String     name       = rs.getString( 2 );
                BigDecimal rate       = rs.getBigDecimal( 3 );
                BigDecimal taxBaseAmt = rs.getBigDecimal( 4 );
                BigDecimal amount     = rs.getBigDecimal( 5 );

                //

                DocTax taxLine = new DocTax( C_Tax_ID,name,rate,taxBaseAmt,amount );

                list.add( taxLine );
            }

            //

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadTaxes",e );
        }

        // Return Array

        DocTax[] tl = new DocTax[ list.size()];

        list.toArray( tl );

        return tl;
    }    // loadTaxes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getBalance() {
        BigDecimal   retValue = new BigDecimal( 0.0 );
        StringBuffer sb       = new StringBuffer( " [" );

        // Total

        retValue = retValue.add( getAmount( Doc.AMTTYPE_Gross ));
        sb.append( getAmount( Doc.AMTTYPE_Gross ));

        // - Header Charge

        retValue = retValue.subtract( getAmount( Doc.AMTTYPE_Charge ));
        sb.append( "-" ).append( getAmount( Doc.AMTTYPE_Charge ));

        // - Tax

        if( m_taxes != null ) {
            for( int i = 0;i < m_taxes.length;i++ ) {
                retValue = retValue.subtract( m_taxes[ i ].getAmount());
                sb.append( "-" ).append( m_taxes[ i ].getAmount());
            }
        }

        // - Lines

        if( p_lines != null ) {
            for( int i = 0;i < p_lines.length;i++ ) {
                retValue = retValue.subtract( p_lines[ i ].getAmount());
                sb.append( "-" ).append( p_lines[ i ].getAmount());
            }

            sb.append( "]" );
        }

        //

        log.fine( toString() + " Balance=" + retValue + sb.toString());

        return retValue;
    }    // getBalance

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    public Fact createFact( MAcctSchema as ) {

        // Purchase Order

        if( p_vo.DocumentType.equals( DOCTYPE_POrder )) {
            updateProductInfo( as.getC_AcctSchema_ID());
        }

        // create Fact Header

        Fact fact = new Fact( this,as,Fact.POST_Actual );

        return fact;
    }    // createFact

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     */

    private void updateProductInfo( int C_AcctSchema_ID ) {
        log.fine( "updateProductInfo - C_Order_ID=" + getRecord_ID());

        // update Product PO info
        // should only be once, but here for every AcctSchema
        // ignores multiple lines with same product - just uses first

        StringBuffer sql = new StringBuffer( "UPDATE M_Product_PO po " + "SET PriceLastPO = (SELECT currencyConvert(ol.PriceActual,ol.C_Currency_ID,po.C_Currency_ID,o.DateOrdered,o.C_ConversionType_ID,o.AD_Client_ID,o.AD_Org_ID) " + "FROM C_Order o, C_OrderLine ol " + "WHERE o.C_Order_ID=ol.C_Order_ID" + " AND po.M_Product_ID=ol.M_Product_ID AND po.C_BPartner_ID=o.C_BPartner_ID" + " AND ROWNUM=1 AND o.C_Order_ID=" ).append( getRecord_ID()).append( ") " ).append( "WHERE EXISTS (SELECT * " + "FROM C_Order o, C_OrderLine ol " + "WHERE o.C_Order_ID=ol.C_Order_ID" + " AND po.M_Product_ID=ol.M_Product_ID AND po.C_BPartner_ID=o.C_BPartner_ID" + " AND o.C_Order_ID=" ).append( getRecord_ID()).append( ")" );
        int no = DB.executeUpdate( sql.toString(),getTrxName());

        log.fine( "M_Product_PO - Updated=" + no );

        // update Product Costing
        // requires existence of currency conversion !!
        // if there are multiple lines of the same product last price uses first

        sql = new StringBuffer( "UPDATE M_Product_Costing pc " + "SET PriceLastPO = " + "(SELECT currencyConvert(ol.PriceActual,ol.C_Currency_ID,a.C_Currency_ID,o.DateOrdered,o.C_ConversionType_ID,o.AD_Client_ID,o.AD_Org_ID) " + "FROM C_Order o, C_OrderLine ol, C_AcctSchema a " + "WHERE o.C_Order_ID=ol.C_Order_ID" + " AND pc.M_Product_ID=ol.M_Product_ID AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID" + " AND ROWNUM=1" + " AND pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND o.C_Order_ID=" ).append( getRecord_ID()).append( ") " ).append( "WHERE EXISTS (SELECT * " + "FROM C_Order o, C_OrderLine ol, C_AcctSchema a " + "WHERE o.C_Order_ID=ol.C_Order_ID" + " AND pc.M_Product_ID=ol.M_Product_ID AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID" + " AND pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND o.C_Order_ID=" ).append( getRecord_ID()).append( ")" );
        no = DB.executeUpdate( sql.toString(),getTrxName());
        log.fine( "M_Product_Costing - Updated=" + no );
    }    // updateProductInfo

	@Override
	public String applyCustomSettings(Fact fact) {
		return null;
	}
	
	
}    // Doc_Order



/*
 *  @(#)Doc_Order.java   24.03.06
 * 
 *  Fin del fichero Doc_Order.java
 *  
 *  Versión 2.2
 *
 */
