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
import java.util.HashMap;
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLocator;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Doc_MatchInv extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_MatchInv( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_MatchInv

    // Loaded from MatchInv

    /** Descripción de Campos */

    private int m_C_InvoiceLine_ID = 0;

    /** Descripción de Campos */

    private int m_M_InOutLine_ID = 0;

    // Loaded from line info

    /** Descripción de Campos */

    private int m_C_Invoice_ID = 0;

    /** Descripción de Campos */

    private int m_M_InOut_ID = 0;

    //

    /** Descripción de Campos */

    private ProductInfo m_pi;

    // Receipt

    /** Descripción de Campos */

    private int m_M_Locator_ID;

    /** Descripción de Campos */

    private int m_C_BPartner_Location_ID;

    /** Descripción de Campos */

    private BigDecimal m_InOutQty = Env.ZERO;

    // Invoice

    /** Descripción de Campos */

    private BigDecimal m_LineNetAmt = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_InvoiceQty = new BigDecimal( 1.0 );

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType = DOCTYPE_MatMatchInv;

        try {

            // Get Trx Date

            p_vo.DateDoc = rs.getTimestamp( "DateTrx" );

            //

            p_vo.M_Product_ID = rs.getInt( "M_Product_ID" );
            p_vo.Qty          = rs.getBigDecimal( "Qty" );

            //

            m_C_InvoiceLine_ID = rs.getInt( "C_InvoiceLine_ID" );
            m_M_InOutLine_ID   = rs.getInt( "M_InOutLine_ID" );

            //

            m_pi = new ProductInfo( p_vo.M_Product_ID,getTrxName());
            m_pi.setQty( p_vo.Qty );
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        // Currency

        p_vo.C_Currency_ID = Doc.NO_CURRENCY;

        return false;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getBalance() {
        return Env.ZERO;
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

        // did not find required data

        p_vo.Error = loadData();

        if( p_vo.Error != null ) {
            log.log( Level.SEVERE,p_vo.Error );

            return null;
        }

        // create Fact Header

        p_vo.C_Currency_ID = as.getC_Currency_ID();

        Fact fact = new Fact( this,as,Fact.POST_Actual );

        // Nothing to do

        if( (p_vo.M_Product_ID == 0                            // no Product
                ) || (Env.ZERO.compareTo( m_InOutQty ) == 0    // Qty = 0
                    ) || (Env.ZERO.compareTo( p_vo.Qty ) == 0) ) {
            log.fine( "No Product/Qty - M_Product_ID=" + p_vo.M_Product_ID + ",Qty=" + p_vo.Qty + ",InOutQty=" + m_InOutQty );

            return fact;
        }

        // Needs to be handeled in PO Matching as no Receipt info

        if( m_pi.isService()) {
            log.fine( "Service" );

            return fact;
        }

        // NotInvoicedReceipt      DR
        // From Receipt
//              FactLine dr = fact.createLine(null,
//                      getAccount(Doc.ACCTTYPE_NotInvoicedReceipts, as),
//                      as.getC_Currency_ID(), m_pi.getProductCosts(as), null);
        // begin vpj-cd 26/01/2005 e-evolution
        // FactLine dr = fact.createLine(null,
        // getAccount(Doc.ACCTTYPE_NotInvoicedReceipts, as),
        // as.getC_Currency_ID(), m_pi.getProductCosts(as), null);

        MLocator locator = new MLocator( getCtx(),m_M_Locator_ID,"C_Locator" );
        FactLine dr      = fact.createLine( null,getAccount( Doc.ACCTTYPE_NotInvoicedReceipts,as ),as.getC_Currency_ID(),m_pi.getProductCosts( as,locator.getM_Warehouse_ID()),null );

        // end vpj-cd e-evolution 26/01/2005

        if( dr == null ) {
            p_vo.Error = "No Product Costs";

            return null;
        }

        dr.setQty( p_vo.Qty );

        // Should use costs of material receipt !!!

        dr.setM_Locator_ID( m_M_Locator_ID );
        dr.setLocationFromBPartner( m_C_BPartner_Location_ID,true );    // from Loc
        dr.setLocationFromLocator( m_M_Locator_ID,false );              // to Loc

        BigDecimal temp = dr.getAcctBalance();

        // Set AmtAcctCr/Dr from Receipt (sets also Project)

        if( !dr.updateReverseLine( MInOut.Table_ID,m_M_InOut_ID,m_M_InOutLine_ID,p_vo.Qty.divide( m_InOutQty,BigDecimal.ROUND_HALF_UP ).abs())) {
            p_vo.Error = "Mat.Receipt not posted yet";

            return null;
        }

        log.fine( "CR - Amt(" + temp + "->" + dr.getAcctBalance() + ") - " + dr.toString());

        // Expense                         CR
        // From Invoice

        FactLine cr = fact.createLine( null,m_pi.getAccount( ProductInfo.ACCTTYPE_P_Expense,as ),as.getC_Currency_ID(),null,m_LineNetAmt );

        if( cr == null ) {
            log.fine( "Line Net Amt=0 - M_Product_ID=" + p_vo.M_Product_ID + ",Qty=" + p_vo.Qty + ",InOutQty=" + m_InOutQty );

            return fact;
        }

        cr.setQty( p_vo.Qty.negate());
        temp = cr.getAcctBalance();

        // Set AmtAcctCr/Dr from Invoice (sets also Project)

        if( !cr.updateReverseLine( MInvoice.Table_ID,m_C_Invoice_ID,m_C_InvoiceLine_ID,p_vo.Qty.divide( m_InvoiceQty,BigDecimal.ROUND_HALF_UP ).abs())) {
            p_vo.Error = "Invoice not posted yet";

            return null;
        }

        log.fine( "DR - Amt(" + temp + "->" + cr.getAcctBalance() + ") - " + cr.toString());

        // Invoice Price Variance      difference

        BigDecimal ipv = cr.getAcctBalance().add( dr.getAcctBalance()).negate();

        if( ipv.compareTo( Env.ZERO ) != 0 ) {
            fact.createLine( null,m_pi.getAccount( ProductInfo.ACCTTYPE_P_IPV,as ),as.getC_Currency_ID(),ipv );
        }

        log.fine( "IPV=" + ipv + "; Balance=" + fact.getSourceBalance());

        // Update Costing

        updateProductInfo( as.getC_AcctSchema_ID(),MAcctSchema.COSTINGMETHOD_StandardCosting.equals( as.getCostingMethod()));

        return fact;
    }    // createFact

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String loadData() {

        // **  Not Invoiced Receipt Info from Receipt **

        String sql = "SELECT i.M_InOut_ID, i.C_BPartner_ID, i.C_BPartner_Location_ID,"    // 1..3
                     + " il.M_Locator_ID, il.C_UOM_ID, il.M_Product_ID, il.MovementQty,"    // 4..7
                     + "  i.C_Project_ID " + "FROM M_InOut i INNER JOIN M_InOutLine il ON (i.M_InOut_ID=il.M_InOut_ID) " + "WHERE il.M_InOutLine_ID=?";
        int M_Product_ID = -1;

        m_InvoiceQty = new BigDecimal( 1.0 );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,m_M_InOutLine_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_M_InOut_ID             = rs.getInt( 1 );
                p_vo.C_BPartner_ID       = rs.getInt( 2 );
                m_C_BPartner_Location_ID = rs.getInt( 3 );

                //

                m_M_Locator_ID = rs.getInt( 4 );

                // rs.getInt(5);    //  UOM

                M_Product_ID = rs.getInt( 6 );
                m_InOutQty   = rs.getBigDecimal( 7 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        if( M_Product_ID == -1 ) {
            return "Not found M_InOutLine_ID=" + m_M_InOutLine_ID;
        }

        if( M_Product_ID != p_vo.M_Product_ID ) {
            return "Product not the same InOut/Match - " + M_Product_ID + "/" + p_vo.M_Product_ID;
        }

        // **  Expense from Invoice **

        sql          = "SELECT C_Invoice_ID, PriceActual,"          // 1..2
                       + " C_UOM_ID, M_Product_ID, QtyInvoiced "    // 3..5
                       + "FROM C_InvoiceLine WHERE C_InvoiceLine_ID=?";
        M_Product_ID = -1;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,m_C_InvoiceLine_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_C_Invoice_ID = rs.getInt( 1 );
                m_LineNetAmt   = rs.getBigDecimal( 2 );

                if( m_LineNetAmt != null ) {
                    m_LineNetAmt.multiply( p_vo.Qty );
                }

                M_Product_ID = rs.getInt( 4 );
                m_InvoiceQty = rs.getBigDecimal( 5 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        if( M_Product_ID == -1 ) {
            return "Not found C_InvoiceLine_ID=" + m_C_InvoiceLine_ID;
        }

        if( M_Product_ID != p_vo.M_Product_ID ) {
            return "Product not the same Invoice/Match - " + M_Product_ID + "/" + p_vo.M_Product_ID;
        }

        // UOM Conversion ??
        // p_vo.Qty = m_pi.getQty();

        return null;
    }    // loadData

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     * @param standardCosting
     *
     * @return
     */

    private boolean updateProductInfo( int C_AcctSchema_ID,boolean standardCosting ) {
        log.fine( "M_MatchInv_ID=" + getRecord_ID());

        // update Product Costing Qty/Amt
        // requires existence of currency conversion !!

        /* Fue necesario splitear el query debido a que postgre no soporta actualizacion de multiples columnas bajo el formato: UPDATE T SET (x,y,z) = (Select (a,b,c) */
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("CostStandardCumQty", " pc.CostStandardCumQty + m.Qty ");
        map.put("CostStandardCumAmt", " pc.CostStandardCumAmt + currencyConvert(il.PriceActual,i.C_Currency_ID,a.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID)*m.Qty ");
        map.put("CostAverageCumQty",  " pc.CostAverageCumQty + m.Qty ");
        map.put("CostAverageCumAmt",  " pc.CostAverageCumAmt + currencyConvert(il.PriceActual,i.C_Currency_ID,a.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID)*m.Qty ");
        
        for (String field : map.keySet())
        {
        	StringBuffer sql = new StringBuffer( "UPDATE M_Product_Costing pc SET " + field + " = (SELECT " + map.get(field) + " FROM M_MatchInv m" + " INNER JOIN C_InvoiceLine il ON (m.C_InvoiceLine_ID=il.C_InvoiceLine_ID)" + " INNER JOIN C_Invoice i ON (il.C_Invoice_ID=i.C_Invoice_ID)," + " C_AcctSchema a " + "WHERE pc.C_AcctSchema_ID=a.C_AcctSchema_ID" + " AND pc.M_Product_ID=m.M_Product_ID" + " AND m.M_MatchInv_ID=" ).append( getRecord_ID()).append( ")"
        	+ "WHERE pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND EXISTS (SELECT * FROM M_MatchInv m " + "WHERE pc.M_Product_ID=m.M_Product_ID" + " AND m.M_MatchInv_ID=" ).append( getRecord_ID()).append( ")" );
        	int no = DB.executeUpdate( sql.toString(),getTrxName());
        	log.fine( "M_Product_Costing - Qty/Amt Updated #=" + no );	
        }
        
       
        
        // Update Average Cost

        StringBuffer sql = new StringBuffer( "UPDATE M_Product_Costing " + "SET CostAverage = CostAverageCumAmt/DECODE(CostAverageCumQty, 0,1, CostAverageCumQty) " + "WHERE C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND M_Product_ID=" ).append( p_vo.M_Product_ID );
        int no = DB.executeUpdate( sql.toString(),getTrxName());
        log.fine( "M_Product_Costing - AvgCost Updated #=" + no );

        // Update Current Cost

        if( !standardCosting ) {
            sql = new StringBuffer( "UPDATE M_Product_Costing " + "SET CurrentCostPrice = CostAverage " + "WHERE C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND M_Product_ID=" ).append( p_vo.M_Product_ID );
            no = DB.executeUpdate( sql.toString(),getTrxName());
            log.fine( "M_Product_Costing - CurrentCost Updated=" + no );
        }

        return true;
    }    // updateProductInfo

	@Override
	public String applyCustomSettings(Fact fact) {
		// TODO Auto-generated method stub
		return null;	
	}
}    // Doc_MatchInv



/*
 *  @(#)Doc_MatchInv.java   24.03.06
 * 
 *  Fin del fichero Doc_MatchInv.java
 *  
 *  Versión 2.2
 *
 */
