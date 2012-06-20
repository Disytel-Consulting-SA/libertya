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
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MRfQ;
import org.openXpertya.model.MRfQResponse;
import org.openXpertya.model.MRfQResponseLine;
import org.openXpertya.model.MRfQResponseLineQty;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RfQCreatePO extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_RfQ_ID = 0;

    /** Descripción de Campos */

    private int p_C_DocType_ID = 0;

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
            } else if( name.equals( "C_DocType_ID" )) {
                p_C_DocType_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_C_RfQ_ID = getRecord_ID();
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
        MRfQ rfq = new MRfQ( getCtx(),p_C_RfQ_ID,get_TrxName());

        if( rfq.getID() == 0 ) {
            throw new IllegalArgumentException( "No RfQ found" );
        }

        log.info( rfq.toString());

        // Complete

        MRfQResponse[] responses = rfq.getResponses( true,true );

        log.config( "#Responses=" + responses.length );

        if( responses.length == 0 ) {
            throw new IllegalArgumentException( "No completed RfQ Responses found" );
        }

        // Winner for entire RfQ

        for( int i = 0;i < responses.length;i++ ) {
            MRfQResponse response = responses[ i ];

            if( !response.isSelectedWinner()) {
                continue;
            }

            //

            MBPartner bp = new MBPartner( getCtx(),response.getC_BPartner_ID(),get_TrxName());

            log.config( "Winner=" + bp );

            MOrder order = new MOrder( getCtx(),0,get_TrxName());

            order.setIsSOTrx( false );

            if( p_C_DocType_ID != 0 ) {
                order.setC_DocTypeTarget_ID( p_C_DocType_ID );
            } else {
                order.setC_DocTypeTarget_ID();
            }

            order.setBPartner( bp );
            order.setC_BPartner_Location_ID( response.getC_BPartner_Location_ID());
            order.setSalesRep_ID( rfq.getSalesRep_ID());

            if( response.getDateWorkComplete() != null ) {
                order.setDatePromised( response.getDateWorkComplete());
            } else if( rfq.getDateWorkComplete() != null ) {
                order.setDatePromised( rfq.getDateWorkComplete());
            }

            order.save();

            //

            MRfQResponseLine[] lines = response.getLines( false );

            for( int j = 0;j < lines.length;j++ ) {

                // Respones Line

                MRfQResponseLine line = lines[ j ];

                if( !line.isActive()) {
                    continue;
                }

                MRfQResponseLineQty[] qtys = line.getQtys( false );

                // Response Line Qty

                for( int k = 0;k < qtys.length;k++ ) {
                    MRfQResponseLineQty qty = qtys[ k ];

                    // Create PO Lline for all Purchase Line Qtys

                    if( qty.getRfQLineQty().isActive() && qty.getRfQLineQty().isPurchaseQty()) {
                        MOrderLine ol = new MOrderLine( order );

                        ol.setM_Product_ID( line.getRfQLine().getM_Product_ID(),qty.getRfQLineQty().getC_UOM_ID());
                        ol.setDescription( line.getDescription());
                        ol.setQty( qty.getRfQLineQty().getQty());

                        BigDecimal price = qty.getNetAmt();

                        ol.setPrice( price );
                        ol.save();
                    }
                }
            }

            response.setC_Order_ID( order.getC_Order_ID());
            response.save();

            return order.getDocumentNo();
        }

        // Selected Winner on Line Level

        int noOrders = 0;

        for( int i = 0;i < responses.length;i++ ) {
            MRfQResponse response = responses[ i ];
            MBPartner    bp       = null;
            MOrder       order    = null;

            // For all Response Lines

            MRfQResponseLine[] lines = response.getLines( false );

            for( int j = 0;j < lines.length;j++ ) {
                MRfQResponseLine line = lines[ j ];

                if( !line.isActive() ||!line.isSelectedWinner()) {
                    continue;
                }

                // New/different BP

                if( bp == null ) {
                    bp = new MBPartner( getCtx(),response.getC_BPartner_ID(),get_TrxName());
                    order = null;
                }

                log.config( "Line=" + line + ", Winner=" + bp );

                // New Order

                if( order == null ) {
                    order = new MOrder( getCtx(),0,get_TrxName());
                    order.setIsSOTrx( false );
                    order.setC_DocTypeTarget_ID();
                    order.setBPartner( bp );
                    order.setC_BPartner_Location_ID( response.getC_BPartner_Location_ID());
                    order.setSalesRep_ID( rfq.getSalesRep_ID());
                    order.save();
                    noOrders++;
                    addLog( 0,null,null,order.getDocumentNo());
                }

                // For all Qtys

                MRfQResponseLineQty[] qtys = line.getQtys( false );

                for( int k = 0;k < qtys.length;k++ ) {
                    MRfQResponseLineQty qty = qtys[ k ];

                    if( qty.getRfQLineQty().isActive() && qty.getRfQLineQty().isPurchaseQty()) {
                        MOrderLine ol = new MOrderLine( order );

                        ol.setM_Product_ID( line.getRfQLine().getM_Product_ID(),qty.getRfQLineQty().getC_UOM_ID());
                        ol.setDescription( line.getDescription());
                        ol.setQty( qty.getRfQLineQty().getQty());

                        BigDecimal price = qty.getNetAmt();

                        ol.setPriceActual( price );
                        ol.save();
                    }
                }    // for all Qtys
            }        // for all Response Lines

            if( order != null ) {
                response.setC_Order_ID( order.getC_Order_ID());
                response.save();
            }
        }

        return "#" + noOrders;
    }    // doIt
}    // RfQCreatePO



/*
 *  @(#)RfQCreatePO.java   02.07.07
 * 
 *  Fin del fichero RfQCreatePO.java
 *  
 *  Versión 2.2
 *
 */
