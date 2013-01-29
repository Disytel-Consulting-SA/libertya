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

public class Doc_Production extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    public Doc_Production( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_Production

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType  = DOCTYPE_MatProduction;
        p_vo.C_Currency_ID = NO_CURRENCY;

        try {
            p_vo.DateDoc = rs.getTimestamp( "MovementDate" );
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
        ArrayList list  = new ArrayList();
        String    sqlPP = "SELECT * FROM M_ProductionPlan pp " + "WHERE pp.M_Production_ID=? " + "ORDER BY pp.Line";
        String sqlPL = "SELECT * FROM M_ProductionLine pl " + "WHERE pl.M_ProductionPlan_ID=? " + "ORDER BY pl.Line";

        try {
            PreparedStatement pstmtPP = DB.prepareStatement( sqlPP,m_trxName );

            pstmtPP.setInt( 1,getRecord_ID());

            ResultSet rsPP = pstmtPP.executeQuery();

            //

            while( rsPP.next()) {
                int M_Product_ID        = rsPP.getInt( "M_Product_ID" );
                int M_ProductionPlan_ID = rsPP.getInt( "M_ProductionPlan_ID" );

                //

                PreparedStatement pstmtPL = DB.prepareStatement( sqlPL,m_trxName );

                pstmtPL.setInt( 1,M_ProductionPlan_ID );

                ResultSet rsPL = pstmtPP.executeQuery();

                while( rsPL.next()) {
                    int              Line_ID = rsPL.getInt( "M_ProductionLine_ID" );
                    DocLine_Material docLine = new DocLine_Material( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                    docLine.loadAttributes( rsPL,p_vo );
                    docLine.setQty( rsPL.getBigDecimal( "MovementQty" ),false );
                    docLine.setM_Locator_ID( rsPL.getInt( "M_Locator_ID" ));

                    // Identify finished BOM Product

                    docLine.setProductionBOM( rsPL.getInt( "M_Product_ID" ) == M_Product_ID );

                    //

                    log.fine( docLine.toString());
                    list.add( docLine );
                }

                rsPL.close();
                pstmtPL.close();
            }

            rsPP.close();
            pstmtPP.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sqlPP,e );
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

        // Line pointer

        FactLine fl = null;

        // Get BOM Cost

        BigDecimal bomCost = Env.ZERO;

        for( int i = 0;i < p_lines.length;i++ ) {
            DocLine_Material line = ( DocLine_Material )p_lines[ i ];

            if( !line.isProductionBOM()) {
                bomCost = bomCost.add( line.getProductCosts( as ));
            }
        }

        for( int i = 0;i < p_lines.length;i++ ) {
            DocLine_Material line  = ( DocLine_Material )p_lines[ i ];
            BigDecimal       costs = null;

            if( line.isProductionBOM()) {
                costs = bomCost.negate();
            } else {
                costs = line.getProductCosts( as );
            }

            // Inventory       DR      CR

            fl = fact.createLine( line,line.getAccount( ProductInfo.ACCTTYPE_P_Asset,as ),as.getC_Currency_ID(),costs );
            fl.setM_Locator_ID( line.getM_Locator_ID());
            fl.setQty( line.getQty());
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
}    // Doc_Production



/*
 *  @(#)Doc_Production.java   24.03.06
 * 
 *  Fin del fichero Doc_Production.java
 *  
 *  Versión 2.2
 *
 */
