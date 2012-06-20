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

public class Doc_MatchPO extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_MatchPO( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_MatchPO

    /** Descripción de Campos */

    private int m_C_OrderLine_ID = 0;

    /** Descripción de Campos */

    private int m_M_InOutLine_ID = 0;

    /** Descripción de Campos */

    private int m_C_InvoiceLine_ID = 0;

    /** Descripción de Campos */

    private ProductInfo m_pi;

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType = DOCTYPE_MatMatchPO;

        try {

            // Get Trx Date

            p_vo.DateDoc = rs.getTimestamp( "DateTrx" );

            //

            p_vo.M_Product_ID = rs.getInt( "M_Product_ID" );
            p_vo.Qty          = rs.getBigDecimal( "Qty" );

            //

            m_C_OrderLine_ID   = rs.getInt( "C_OrderLine_ID" );
            m_M_InOutLine_ID   = rs.getInt( "M_InOutLine_ID" );
            m_C_InvoiceLine_ID = rs.getInt( "C_InvoiceLine_ID" );

            //

            m_pi = new ProductInfo( p_vo.M_Product_ID,getTrxName());
            m_pi.setQty( p_vo.Qty );
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

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

        // create Fact Header

        Fact fact = new Fact( this,as,Fact.POST_Actual );

        // Nothing to do if no Product

        if( p_vo.M_Product_ID == 0 ) {
            return fact;
        }

        // No posting if not matched to Shipment

        if( m_M_InOutLine_ID == 0 ) {
            return fact;
        }

        //

        p_vo.C_Currency_ID = as.getC_Currency_ID();

        BigDecimal difference = loadInfo( as );    // PPV difference

        if( difference == null ) {
            p_vo.Error = "No PPV Difference";
            log.log( Level.SEVERE,"createFact - " + p_vo.Error );

            return null;
        }

        // Nothing to post

        if( difference.compareTo( Env.ZERO ) == 0 ) {
            return fact;
        }

        // Product PPV

        FactLine cr = fact.createLine( null,m_pi.getAccount( ProductInfo.ACCTTYPE_P_PPV,as ),as.getC_Currency_ID(),difference );

        if( cr != null ) {
            cr.setQty( p_vo.Qty );
        }

        // PPV Offset

        FactLine dr = fact.createLine( null,getAccount( Doc.ACCTTYPE_PPVOffset,as ),as.getC_Currency_ID(),difference.negate());

        if( dr != null ) {
            dr.setQty( p_vo.Qty.negate());
        }

        return fact;
    }    // createFact

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    private BigDecimal loadInfo( MAcctSchema as ) {
        BigDecimal poAmt = null;

        // get PO Amount (probably no Acct_Fact)

        String sql = "SELECT ol.PriceActual, COALESCE(ol.C_Project_ID, o.C_Project_ID) " + "FROM C_OrderLine ol INNER JOIN C_Order o ON (ol.C_Order_ID=o.C_Order_ID) " + "WHERE ol.C_OrderLine_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,m_C_OrderLine_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                poAmt             = rs.getBigDecimal( 1 );
                p_vo.C_Project_ID = rs.getInt( 2 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadInfo",e );
        }

        if( poAmt == null ) {
            return null;
        }

        // subtract Standard Cost (should get data from Receipt account entry) ??
        // is "more" correct if Average Costing - but issue if std price has changed in the meantime

        BigDecimal difference = poAmt.subtract( m_pi.getProductItemCost( as,MAcctSchema.COSTINGMETHOD_StandardCosting ));

        return difference.multiply( p_vo.Qty );
    }    // loadInfo

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     */

    private void updateProductInfo( int C_AcctSchema_ID ) {
        log.fine( "updateProductInfo - M_MatchPO_ID=" + getRecord_ID());

        // update Product Costing
        // requires existence of currency conversion !!

        StringBuffer sql = new StringBuffer( "UPDATE M_Product_Costing pc " + "SET CostStandardPOQty = " + "(SELECT CostStandardPOQty + m.Qty " + "FROM M_MatchPO m, C_OrderLine ol, C_AcctSchema a " + "WHERE m.C_OrderLine_ID=ol.C_OrderLine_ID" + " AND pc.M_Product_ID=ol.M_Product_ID" + " AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID" + "AND m.M_MatchPO_ID=" ).append( getRecord_ID()).append( ") " ).append( "WHERE pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND pc.M_Product_ID=" ).append( p_vo.M_Product_ID );
        int no = DB.executeUpdate( sql.toString(),getTrxName());
        log.fine( "M_Product_Costing - Updated=" + no );
        
        sql = new StringBuffer( "UPDATE M_Product_Costing pc " + "SET CostStandardPOAmt = " + "(SELECT CostStandardPOAmt + currencyConvert(ol.PriceActual,ol.C_Currency_ID,a.C_Currency_ID,ol.DateOrdered,null,ol.AD_Client_ID,ol.AD_Org_ID)*m.Qty " + "FROM M_MatchPO m, C_OrderLine ol, C_AcctSchema a " + "WHERE m.C_OrderLine_ID=ol.C_OrderLine_ID" + " AND pc.M_Product_ID=ol.M_Product_ID" + " AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID" + "AND m.M_MatchPO_ID=" ).append( getRecord_ID()).append( ") " ).append( "WHERE pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND pc.M_Product_ID=" ).append( p_vo.M_Product_ID );
        no = DB.executeUpdate( sql.toString(),getTrxName());        
        log.fine( "M_Product_Costing - Updated=" + no );
    }    // updateProductInfo

	@Override
	public String applyCustomSettings(Fact fact) {
		// TODO Auto-generated method stub
		return null;
	}
}    // Doc_MatchPO



/*
 *  @(#)Doc_MatchPO.java   24.03.06
 * 
 *  Fin del fichero Doc_MatchPO.java
 *  
 *  Versión 2.2
 *
 */
