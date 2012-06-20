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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProductPO;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectLine;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProjectGenPO extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_Project_ID = 0;

    /** Descripción de Campos */

    private int m_C_ProjectLine_ID = 0;

    /** Descripción de Campos */

    private boolean m_ConsolidateDocument = true;

    /** Descripción de Campos */

    private ArrayList m_pos = new ArrayList();

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_Project_ID" )) {
                m_C_Project_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_ProjectLine_ID" )) {
                m_C_ProjectLine_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "ConsolidateDocument" )) {
                m_ConsolidateDocument = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "doIt - C_Project_ID=" + m_C_Project_ID + " - C_ProjectLine_ID=" + m_C_ProjectLine_ID + " - Consolidate=" + m_ConsolidateDocument );

        if( m_C_ProjectLine_ID != 0 ) {
            MProjectLine projectLine = new MProjectLine( getCtx(),m_C_ProjectLine_ID,get_TrxName());
            MProject project = new MProject( getCtx(),projectLine.getC_Project_ID(),get_TrxName());

            createPO( project,projectLine );
        } else {
            MProject project = new MProject( getCtx(),m_C_Project_ID,get_TrxName());
            MProjectLine[] lines = project.getLines();

            for( int i = 0;i < lines.length;i++ ) {
                createPO( project,lines[ i ] );
            }
        }

        return "";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param project
     * @param projectLine
     */

    private void createPO( MProject project,MProjectLine projectLine ) {
        if( projectLine.getM_Product_ID() == 0 ) {
            addLog( projectLine.getLine(),null,null,"Line has no Product" );

            return;
        }

        if( projectLine.getC_OrderPO_ID() != 0 ) {
            addLog( projectLine.getLine(),null,null,"Line was ordered previously" );

            return;
        }

        // PO Record

        MProductPO[] pos = MProductPO.getOfProduct( getCtx(),projectLine.getM_Product_ID(),get_TrxName());

        if( (pos == null) || (pos.length == 0) ) {
            addLog( projectLine.getLine(),null,null,"Product has no PO record" );

            return;
        }

        // Create to Order

        MOrder order = null;

        // try to find PO to C_BPartner

        for( int i = 0;i < m_pos.size();i++ ) {
            MOrder test = ( MOrder )m_pos.get( i );

            if( test.getC_BPartner_ID() == pos[ 0 ].getC_BPartner_ID()) {
                order = test;

                break;
            }
        }

        if( order == null )    // create new Order
        {

            // Vendor

            MBPartner bp = new MBPartner( getCtx(),pos[ 0 ].getC_BPartner_ID(),get_TrxName());

            // New Order

            order = new MOrder( project,false,null );

            int AD_Org_ID = projectLine.getAD_Org_ID();

            if( AD_Org_ID == 0 ) {
                log.warning( "createPOfromProjectLine - AD_Org_ID=0" );
                AD_Org_ID = Env.getAD_Org_ID( getCtx());

                if( AD_Org_ID != 0 ) {
                    projectLine.setAD_Org_ID( AD_Org_ID );
                }
            }

            order.setClientOrg( projectLine.getAD_Client_ID(),AD_Org_ID );
            order.setBPartner( bp );
            order.save();

            // optionally save for consolidation

            if( m_ConsolidateDocument ) {
                m_pos.add( order );
            }
        }

        // Create Line

        MOrderLine orderLine = new MOrderLine( order );

        orderLine.setM_Product_ID( projectLine.getM_Product_ID(),true );
        orderLine.setQty( projectLine.getPlannedQty());
        orderLine.setDescription( projectLine.getDescription());

        // (Vendor) PriceList Price

        orderLine.setPrice();

        if( orderLine.getPriceActual().compareTo( Env.ZERO ) == 0 ) {

            // Try to find purchase price

            BigDecimal poPrice       = pos[ 0 ].getPricePO();
            int        C_Currency_ID = pos[ 0 ].getC_Currency_ID();

            //

            if( (poPrice == null) || (poPrice.compareTo( Env.ZERO ) == 0) ) {
                poPrice = pos[ 0 ].getPriceLastPO();
            }

            if( (poPrice == null) || (poPrice.compareTo( Env.ZERO ) == 0) ) {
                poPrice = pos[ 0 ].getPriceList();
            }

            // We have a price

            if( (poPrice != null) && (poPrice.compareTo( Env.ZERO ) != 0) ) {
                if( order.getC_Currency_ID() != C_Currency_ID ) {
                    poPrice = MConversionRate.convert( getCtx(),poPrice,C_Currency_ID,order.getC_Currency_ID(),order.getDateAcct(),order.getC_ConversionType_ID(),order.getAD_Client_ID(),order.getAD_Org_ID());
                }

                orderLine.setPrice( poPrice );
            }
        }

        orderLine.setTax();
        orderLine.save();

        // update ProjectLine

        projectLine.setC_OrderPO_ID( order.getC_Order_ID());
        projectLine.save();
        addLog( projectLine.getLine(),null,projectLine.getPlannedQty(),order.getDocumentNo());
    }    // createPOfromProjectLine
}    // ProjectGenPO



/*
 *  @(#)ProjectGenPO.java   02.07.07
 * 
 *  Fin del fichero ProjectGenPO.java
 *  
 *  Versión 2.2
 *
 */
