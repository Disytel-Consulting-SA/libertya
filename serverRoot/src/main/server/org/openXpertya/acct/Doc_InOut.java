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
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MProduct;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Doc_InOut extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    public Doc_InOut( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // DocInOut

    /** Descripción de Campos */

    private int C_BPartner_Location_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.C_Currency_ID = NO_CURRENCY;

        try {
            p_vo.DateDoc           = rs.getTimestamp( "MovementDate" );
            C_BPartner_Location_ID = rs.getInt( "C_BPartner_Location_ID" );
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        loadDocumentType();    // lines require doc type

        // Contained Objects

        p_lines = loadLines();
        log.fine( "Lines=" + p_lines.length );

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
        String    sql  = "SELECT * FROM M_InOutLine WHERE M_InOut_ID=? ORDER BY Line";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                MInOutLine ioLine = new MInOutLine( getCtx(),rs,m_trxName );

                if( ioLine.isDescription() || (ioLine.getM_Product_ID() == 0) ) {
                    continue;
                }

                MProduct product = ioLine.getProduct();

                if( (product == null) ||!product.isStocked()) {
                    continue;
                }

                int              Line_ID = rs.getInt( "M_InOutLine_ID" );
                DocLine_Material docLine = new DocLine_Material( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                docLine.loadAttributes( rs,p_vo );

                BigDecimal Qty = rs.getBigDecimal( "MovementQty" );

                docLine.setQty( Qty,p_vo.DocumentType.equals( DOCTYPE_MatShipment ));    // sets Trx and Storage Qty
                docLine.setM_Locator_ID( rs.getInt( "M_Locator_ID" ));

                //

                if( docLine.getM_Product_ID() == 0 ) {
                    log.info( docLine.toString() + " - No Product - ignored" );
                } else {
                    log.fine( docLine.toString());
                    list.add( docLine );
                }
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

    public BigDecimal getBalance() {
        BigDecimal retValue = Env.ZERO;

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
        p_vo.C_Currency_ID = as.getC_Currency_ID();

        // create Fact Header

        Fact fact = new Fact( this,as,Fact.POST_Actual );

        // Line pointers

        FactLine dr = null;
        FactLine cr = null;

        // Sales - Shipment

        if( p_vo.DocumentType.equals( DOCTYPE_MatShipment )) {
            for( int i = 0;i < p_lines.length;i++ ) {
                DocLine_Material line = ( DocLine_Material )p_lines[ i ];

                // begin vpj-cd e-Evolution
                // BigDecimal costs = line.getProductCosts(as);
                // BigDecimal costs = line.getProductCosts(as);

                MLocator locator = new MLocator( getCtx(),line.getM_Locator_ID(),m_trxName );
                BigDecimal costs = line.getProductCosts( as,locator.getM_Warehouse_ID());

                log.info( " Cost : " + costs );

                // end vpj-cd e-Evolution

                // CoGS            DR

                dr = fact.createLine( line,line.getAccount( ProductInfo.ACCTTYPE_P_Cogs,as ),as.getC_Currency_ID(),costs,null );

                if( dr == null ) {
                    p_vo.Error = "FactLine DR not created: " + line;
                    log.log( Level.SEVERE,"createFact - " + p_vo.Error );

                    return null;
                }

                dr.setM_Locator_ID( line.getM_Locator_ID());
                dr.setLocationFromLocator( line.getM_Locator_ID(),true );    // from Loc
                dr.setLocationFromBPartner( C_BPartner_Location_ID,false );    // to Loc
                dr.setAD_Org_ID( line.getOrder_AD_Org_ID());    // Revenue X-Org
                dr.setQty( line.getQty().negate());

                // Inventory               CR

                cr = fact.createLine( line,line.getAccount( ProductInfo.ACCTTYPE_P_Asset,as ),as.getC_Currency_ID(),null,costs );

                if( cr == null ) {
                    p_vo.Error = "FactLine CR not created: " + line;
                    log.log( Level.SEVERE,"createFact - " + p_vo.Error );

                    return null;
                }

                cr.setM_Locator_ID( line.getM_Locator_ID());
                cr.setLocationFromLocator( line.getM_Locator_ID(),true );    // from Loc
                cr.setLocationFromBPartner( C_BPartner_Location_ID,false );    // to Loc
            }

            updateProductInfo( as.getC_AcctSchema_ID());    // only for SO!
        }

        // Purchasing

        else if( p_vo.DocumentType.equals( DOCTYPE_MatReceipt )) {
            for( int i = 0;i < p_lines.length;i++ ) {
                DocLine_Material line = ( DocLine_Material )p_lines[ i ];

                // begin vpj-cd e-Evolution
                // BigDecimal costs = line.getProductCosts(as);

                MLocator locator = new MLocator( getCtx(),line.getM_Locator_ID(),m_trxName );
                BigDecimal costs = line.getProductCosts( as,locator.getM_Warehouse_ID());

                log.info( " Cost : " + costs );

                // end vpj-cd e-Evolution

                // Inventory       DR

                dr = fact.createLine( line,line.getAccount( ProductInfo.ACCTTYPE_P_Asset,as ),as.getC_Currency_ID(),costs,null );

                if( dr == null ) {
                    p_vo.Error = "FactLine DR not created: " + line;
                    log.log( Level.SEVERE,"createFact - " + p_vo.Error );

                    return null;
                }

                dr.setM_Locator_ID( line.getM_Locator_ID());
                dr.setLocationFromBPartner( C_BPartner_Location_ID,true );    // from Loc
                dr.setLocationFromLocator( line.getM_Locator_ID(),false );    // to Loc

                // NotInvoicedReceipt      CR

                cr = fact.createLine( line,getAccount( Doc.ACCTTYPE_NotInvoicedReceipts,as ),as.getC_Currency_ID(),null,costs );

                if( cr == null ) {
                    p_vo.Error = "FactLine CR not created: " + line;
                    log.log( Level.SEVERE,"createFact - " + p_vo.Error );

                    return null;
                }

                cr.setM_Locator_ID( line.getM_Locator_ID());
                cr.setLocationFromBPartner( C_BPartner_Location_ID,true );    // from Loc
                cr.setLocationFromLocator( line.getM_Locator_ID(),false );    // to Loc
                cr.setQty( line.getQty().negate());
            }
        } else {
            p_vo.Error = "DocumentType unknown: " + p_vo.DocumentType;
            log.log( Level.SEVERE,"createFact - " + p_vo.Error );

            return null;
        }

        //

        return fact;
    }    // createFact

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     */

    private void updateProductInfo( int C_AcctSchema_ID ) {
        log.fine( "updateProductInfo - M_InOut_ID=" + getRecord_ID());

        StringBuffer sql = new StringBuffer( "UPDATE M_Product_Costing pc " + "SET CostAverageCumQty=" + "(SELECT CostAverageCumQty - SUM(il.MovementQty) " + "FROM M_InOutLine il " + "WHERE pc.M_Product_ID=il.M_Product_ID" + " AND il.M_InOut_ID=" ).append( getRecord_ID()).append( ") " ).append( "WHERE EXISTS (SELECT * " + "FROM M_InOutLine il " + "WHERE pc.M_Product_ID=il.M_Product_ID" + " AND il.M_InOut_ID=" ).append( getRecord_ID()).append( ")" );
        int no = DB.executeUpdate( sql.toString(),getTrxName());
        log.fine( "M_Product_Costing - Updated=" + no );
        
        sql = new StringBuffer( "UPDATE M_Product_Costing pc " + "SET CostAverageCumAmt=" + "(SELECT CostAverageCumAmt - SUM(il.MovementQty*CurrentCostPrice) " + "FROM M_InOutLine il " + "WHERE pc.M_Product_ID=il.M_Product_ID" + " AND il.M_InOut_ID=" ).append( getRecord_ID()).append( ") " ).append( "WHERE EXISTS (SELECT * " + "FROM M_InOutLine il " + "WHERE pc.M_Product_ID=il.M_Product_ID" + " AND il.M_InOut_ID=" ).append( getRecord_ID()).append( ")" );
        no = DB.executeUpdate( sql.toString(),getTrxName());
        log.fine( "M_Product_Costing - Updated=" + no );
    }    // updateProductInfo

	@Override
	public String applyCustomSettings(Fact fact) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
	
}    // Doc_InOut



/*
 *  @(#)Doc_InOut.java   24.03.06
 * 
 *  Fin del fichero Doc_InOut.java
 *  
 *  Versión 2.2
 *
 */
