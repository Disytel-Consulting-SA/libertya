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

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
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

public class Doc_Inventory extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    public Doc_Inventory( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_Inventory

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType  = DOCTYPE_MatInventory;
        p_vo.C_Currency_ID = NO_CURRENCY;

        try {
            p_vo.DateDoc = rs.getTimestamp( "MovementDate" );
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        loadDocumentType();    // lines require doc type

        // Contained Objects

        p_lines = loadLines();

        if( p_lines == null ) {
            return false;
        }

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
        String    sql  = "SELECT * FROM M_InventoryLine " + "WHERE M_Inventory_ID=? AND IsActive='Y'" + " AND (QtyBook<>QtyCount OR QtyInternalUse<>0) " + "ORDER BY Line";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int              Line_ID = rs.getInt( "M_InventoryLine_ID" );
                DocLine_Material docLine = new DocLine_Material( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                docLine.loadAttributes( rs,p_vo );

                BigDecimal Qty = rs.getBigDecimal( "QtyInternalUse" );

                if( (Qty != null) && (Env.ZERO.compareTo( Qty ) != 0) ) {
                    Qty = Qty.negate();         // Internal Use entered positive
                } else {
                    BigDecimal QtyBook  = rs.getBigDecimal( "QtyBook" );
                    BigDecimal QtyCount = rs.getBigDecimal( "QtyCount" );

                    Qty = QtyCount.subtract( QtyBook );
                }

                docLine.setQty( Qty,false );    // -5 => -5
                docLine.setM_Locator_ID( rs.getInt( "M_Locator_ID" ));

                // Set Charge ID only when Inventory Type = Charge

                if( !"C".equals( rs.getString( "InventoryType" ))) {
                    docLine.setC_Charge_ID( 0 );
                }

                //

                log.fine( docLine.toString());
                list.add( docLine );
            }

            //

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadLines",e );

            return null;
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

        // log.config( "Doc.Inventory.createFact");

        p_vo.C_Currency_ID = as.getC_Currency_ID();

        // create Fact Header

        Fact fact = new Fact( this,as,Fact.POST_Actual );

        // Line pointers

        FactLine dr = null;
        FactLine cr = null;

        for( int i = 0;i < p_lines.length;i++ ) {
            DocLine_Material line = ( DocLine_Material )p_lines[ i ];

            // begin vpj-cd e-Evolution
            // BigDecimal costs = line.getProductCosts(as);

            MLocator locator = new MLocator( getCtx(),line.getM_Locator_ID(),m_trxName );
            BigDecimal costs = line.getProductCosts( as,locator.getM_Warehouse_ID());

            log.info( " Cost : " + costs );

            // end vpj-cd e-Evolution

            // Inventory       DR      CR

            dr = fact.createLine( line,line.getAccount( ProductInfo.ACCTTYPE_P_Asset,as ),as.getC_Currency_ID(),costs );

            // may be zero difference - no line created.

            if( dr == null ) {
                continue;
            }

            dr.setM_Locator_ID( line.getM_Locator_ID());

            // InventoryDiff   DR      CR
            // or Charge

            MAccount invDiff = line.getChargeAccount( as,costs.negate());

            if( invDiff == null ) {
                invDiff = getAccount( Doc.ACCTTYPE_InvDifferences,as );
            }

            cr = fact.createLine( line,invDiff,as.getC_Currency_ID(),costs.negate());

            if( cr == null ) {
                continue;
            }

            cr.setM_Locator_ID( line.getM_Locator_ID());
            cr.setQty( line.getQty().negate());
        }

        return fact;
    }    // createFact

	@Override
	public String applyCustomSettings(Fact fact) {
		// TODO Auto-generated method stub
		return null;
	}
}    // Doc_Inventory



/*
 *  @(#)Doc_Inventory.java   24.03.06
 * 
 *  Fin del fichero Doc_Inventory.java
 *  
 *  Versión 2.2
 *
 */
