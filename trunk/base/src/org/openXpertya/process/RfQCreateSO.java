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
import org.openXpertya.model.MRfQLine;
import org.openXpertya.model.MRfQLineQty;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RfQCreateSO extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_RfQ_ID = 0;

    /** Descripción de Campos */

    private int p_C_DocType_ID = 0;

    /** Descripción de Campos */

    private static BigDecimal ONEHUNDRED = new BigDecimal( 100 );

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
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
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

        log.info( "doIt - " + rfq );

        if( (rfq.getC_BPartner_ID() == 0) || (rfq.getC_BPartner_Location_ID() == 0) ) {
            throw new Exception( "No Business Partner/Location" );
        }

        MBPartner bp = new MBPartner( getCtx(),rfq.getC_BPartner_ID(),get_TrxName());
        MOrder order = new MOrder( getCtx(),0,get_TrxName());

        order.setIsSOTrx( true );

        if( p_C_DocType_ID != 0 ) {
            order.setC_DocTypeTarget_ID( p_C_DocType_ID );
        } else {
            order.setC_DocTypeTarget_ID();
        }

        order.setBPartner( bp );
        order.setC_BPartner_Location_ID( rfq.getC_BPartner_Location_ID());
        order.setSalesRep_ID( rfq.getSalesRep_ID());

        if( rfq.getDateWorkComplete() != null ) {
            order.setDatePromised( rfq.getDateWorkComplete());
        }

        order.save();

        MRfQLine[] lines = rfq.getLines();

        for( int i = 0;i < lines.length;i++ ) {
            MRfQLine      line = lines[ i ];
            MRfQLineQty[] qtys = line.getQtys();

            for( int j = 0;j < qtys.length;j++ ) {
                MRfQLineQty qty = qtys[ j ];

                if( qty.isActive() && qty.isOfferQty()) {
                    MOrderLine ol = new MOrderLine( order );

                    ol.setM_Product_ID( line.getM_Product_ID(),qty.getC_UOM_ID());
                    ol.setDescription( line.getDescription());
                    ol.setQty( qty.getQty());

                    //

                    BigDecimal price = qty.getOfferAmt();

                    if( (price == null) || (price.compareTo( Env.ZERO ) == 0) ) {
                        price = qty.getBestResponseAmt();

                        if( (price == null) || (price.compareTo( Env.ZERO ) == 0) ) {
                            price = Env.ZERO;
                            log.warning( " - BestResponse=0 - " + qty );
                        } else {
                            BigDecimal margin = qty.getMargin();

                            if( (margin == null) || (margin.compareTo( Env.ZERO ) == 0) ) {
                                margin = rfq.getMargin();
                            }

                            if( (margin != null) && (margin.compareTo( Env.ZERO ) != 0) ) {
                                margin = margin.add( ONEHUNDRED );
                                price  = price.multiply( margin ).divide( ONEHUNDRED,2,BigDecimal.ROUND_HALF_UP );
                            }
                        }
                    }    // price

                    ol.setPrice( price );
                    ol.save();
                }        // Offer Qty
            }            // All Qtys
        }                // All Lines

        //

        rfq.setC_Order_ID( order.getC_Order_ID());
        rfq.save();

        return order.getDocumentNo();
    }    // doIt
}



/*
 *  @(#)RfQCreateSO.java   02.07.07
 * 
 *  Fin del fichero RfQCreateSO.java
 *  
 *  Versión 2.2
 *
 */
