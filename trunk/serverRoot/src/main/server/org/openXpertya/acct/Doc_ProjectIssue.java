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
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MProjectType;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Doc_ProjectIssue extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    public Doc_ProjectIssue( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_ProjectIssue

    /** Descripción de Campos */

    private boolean m_assetProject = false;

    /** Descripción de Campos */

    private DocLine_Material m_line = null;

    /** Descripción de Campos */

    private int m_S_TimeExpenseLine_ID = 0;

    /** Descripción de Campos */

    private int m_M_InOutLine_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType  = DOCTYPE_ProjectIssue;
        p_vo.C_Currency_ID = NO_CURRENCY;

        try {
            p_vo.DateDoc           = rs.getTimestamp( "MovementDate" );
            m_M_InOutLine_ID       = rs.getInt( "M_InOutLine_ID" );
            m_S_TimeExpenseLine_ID = rs.getInt( "S_TimeExpenseLine_ID" );

            // Pseudo Line

            m_line = new DocLine_Material( p_vo.DocumentType,getRecord_ID(),getRecord_ID(),getTrxName());
            m_line.loadAttributes( rs,p_vo );
            m_line.setQty( rs.getBigDecimal( "MovementQty" ),true );    // sets Trx and Storage Qty
            m_line.setM_Locator_ID( rs.getInt( "M_Locator_ID" ));
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        // Pseudo Line Check

        if( m_line.getM_Product_ID() == 0 ) {
            log.warning( m_line.toString() + " - No Product" );
        }

        log.fine( m_line.toString());

        // Default is WIP project

        loadProjectCategory();

        return true;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     */

    private void loadProjectCategory() {
        String sql = "SELECT pj.ProjectCategory FROM C_ProjectType pj" + " INNER JOIN C_Project p ON (p.C_ProjectType_ID=pj.C_ProjectType_ID) " + "WHERE C_Project_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,m_trxName );
            pstmt.setInt( 1,p_vo.C_Project_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                String pc = rs.getString( 1 );

                if( MProjectType.PROJECTCATEGORY_AssetProject.equals( pc )) {
                    m_assetProject = true;
                }
            } else {
                log.warning( "loadProjectCategory - Not found for C_Project_ID=" + p_vo.C_Project_ID );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"loadProjectCategory",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }
    }    // loadProjectCategory

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

        // Issue Cost

        BigDecimal cost = null;

        if( m_M_InOutLine_ID != 0 ) {
            cost = getPOCost( as );
        } else if( m_S_TimeExpenseLine_ID != 0 ) {
            cost = getLaborCost( as );
        }

        if( cost == null )    // standard Product Costs

        // begin vpj-cd e-Evolution 01/31/2005

        {

            // cost = m_line.getProductCosts(as);

            MLocator locator = new MLocator( getCtx(),m_line.getM_Locator_ID(),m_trxName );

            cost = m_line.getProductCosts( as,locator.getM_Warehouse_ID());
            log.info( " Cost : " + cost );
        }

        // end vpj-cd e-Evolution 01/31/2005

        // cost = m_line.getProductCosts(as);
        // Project         DR

        dr = fact.createLine( m_line,getAccount( m_assetProject
                ?ACCTTYPE_ProjectAsset
                :ACCTTYPE_ProjectWIP,as ),as.getC_Currency_ID(),cost,null );
        dr.setQty( m_line.getQty().negate());

        // Inventory               CR

        cr = fact.createLine( m_line,m_line.getAccount( ProductInfo.ACCTTYPE_P_Asset,as ),as.getC_Currency_ID(),null,cost );
        cr.setM_Locator_ID( m_line.getM_Locator_ID());
        cr.setLocationFromLocator( m_line.getM_Locator_ID(),true );    // from Loc

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

    private BigDecimal getPOCost( MAcctSchema as ) {
        BigDecimal retValue = null;

        // Uses PO Date

        String sql = "SELECT currencyConvert(ol.PriceActual, o.C_Currency_ID, ?, o.DateOrdered, o.C_ConversionType_ID, ?, ?) " + "FROM C_OrderLine ol" + " INNER JOIN M_InOutLine iol ON (iol.C_OrderLine_ID=ol.C_OrderLine_ID)" + " INNER JOIN C_Order o ON (o.C_Order_ID=ol.C_Order_ID) " + "WHERE iol.M_InOutLine_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,m_trxName );
            pstmt.setInt( 1,as.getC_Currency_ID());
            pstmt.setInt( 2,p_vo.AD_Client_ID );
            pstmt.setInt( 3,p_vo.AD_Org_ID );
            pstmt.setInt( 4,m_M_InOutLine_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getBigDecimal( 1 );
                log.fine( "getPOCost = " + retValue );
            } else {
                log.warning( "getPOCost - Not found for M_InOutLine_ID=" + m_M_InOutLine_ID );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPOCost",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return retValue;
    }    // getPOCost();

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    private BigDecimal getLaborCost( MAcctSchema as ) {
        BigDecimal retValue = null;

        return retValue;
    }    // getLaborCost

	@Override
	public String applyCustomSettings( Fact fact, int index ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
}    // DocProjectIssue



/*
 *  @(#)Doc_ProjectIssue.java   24.03.06
 * 
 *  Fin del fichero Doc_ProjectIssue.java
 *  
 *  Versión 2.2
 *
 */
