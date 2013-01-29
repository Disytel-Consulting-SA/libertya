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

public class Doc_Movement extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    public Doc_Movement( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_Movement

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType  = DOCTYPE_MatMovement;
        p_vo.C_Currency_ID = NO_CURRENCY;

        try {
            p_vo.DateDoc = rs.getTimestamp( "MovementDate" );
        } catch( SQLException e ) {
            log.fine( e.toString());
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
        String    sql  = "SELECT * FROM M_MovementLine WHERE M_Movement_ID=? ORDER BY Line";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int              Line_ID = rs.getInt( "M_MovementLine_ID" );
                DocLine_Material docLine = new DocLine_Material( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                docLine.loadAttributes( rs,p_vo );
                docLine.setQty( rs.getBigDecimal( "MovementQty" ),false );
                docLine.setM_Locator_ID( rs.getInt( "M_Locator_ID" ));
                docLine.setM_LocatorTo_ID( rs.getInt( "M_LocatorTo_ID" ));

                //

                log.fine( docLine.toString());
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

        for( int i = 0;i < p_lines.length;i++ ) {
            DocLine_Material line = ( DocLine_Material )p_lines[ i ];

            // begin vpj-cd e-Evolution
            // BigDecimal costs = line.getProductCosts(as);

            MLocator locator = new MLocator( getCtx(),line.getM_Locator_ID(),m_trxName );
            BigDecimal costs = line.getProductCosts( as,locator.getM_Warehouse_ID());

            log.info( " Cost : " + costs );

            // end vpj-cd e-Evolution

            // Inventory       DR      CR

            dr = fact.createLine( line,line.getAccount( ProductInfo.ACCTTYPE_P_Asset,as ),as.getC_Currency_ID(),costs.negate());    // from (-) CR

            if( dr == null ) {
                continue;
            }

            dr.setM_Locator_ID( line.getM_Locator_ID());
            dr.setQty( line.getQty().negate());    // outgoing

            // InventoryTo     DR      CR

            cr = fact.createLine( line,line.getAccount( ProductInfo.ACCTTYPE_P_Asset,as ),as.getC_Currency_ID(),costs );    // to (+) DR

            if( cr == null ) {
                continue;
            }

            cr.setM_Locator_ID( line.getM_LocatorTo_ID());
            cr.setQty( line.getQty());
        }

        return fact;
    }    // createFact

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
}    // Doc_Movement



/*
 *  @(#)Doc_Movement.java   24.03.06
 * 
 *  Fin del fichero Doc_Movement.java
 *  
 *  Versión 2.2
 *
 */
