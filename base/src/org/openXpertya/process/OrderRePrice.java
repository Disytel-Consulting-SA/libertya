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

import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class OrderRePrice extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_Order_ID = 0;

    /** Descripción de Campos */

    private int p_C_Invoice_ID = 0;

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
            } else if( name.equals( "C_Order_ID" )) {
                p_C_Order_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Invoice_ID" )) {
                p_C_Invoice_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
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
        log.info( "C_Order_ID=" + p_C_Order_ID + ", C_Invoice_ID=" + p_C_Invoice_ID );

        if( (p_C_Order_ID == 0) && (p_C_Invoice_ID == 0) ) {
            throw new IllegalArgumentException( "Nothing to do" );
        }

        String retValue = "";

        if( p_C_Order_ID != 0 ) {
            MOrder       order    = new MOrder( getCtx(),p_C_Order_ID,get_TrxName());
            BigDecimal   oldPrice = order.getGrandTotal();
            MOrderLine[] lines    = order.getLines();

            for( int i = 0;i < lines.length;i++ ) {
                lines[ i ].setPrice( order.getM_PriceList_ID());
                lines[ i ].save();
            }

            order = new MOrder( getCtx(),p_C_Order_ID,get_TrxName());

            BigDecimal newPrice = order.getGrandTotal();

            retValue = order.getDocumentNo() + ":  " + oldPrice + " -> " + newPrice;
        }

        if( p_C_Invoice_ID != 0 ) {
            MInvoice       invoice  = new MInvoice( getCtx(),p_C_Invoice_ID,null );
            BigDecimal     oldPrice = invoice.getGrandTotal();
            MInvoiceLine[] lines    = invoice.getLines( false );

            for( int i = 0;i < lines.length;i++ ) {
                lines[ i ].setPrice( invoice.getM_PriceList_ID(),invoice.getC_BPartner_ID());
                lines[ i ].save();
            }

            invoice = new MInvoice( getCtx(),p_C_Invoice_ID,null );

            BigDecimal newPrice = invoice.getGrandTotal();

            if( retValue.length() > 0 ) {
                retValue += Env.NL;
            }

            retValue += invoice.getDocumentNo() + ":  " + oldPrice + " -> " + newPrice;
        }

        //

        return retValue;
    }    // doIt
}    // OrderRePrice



/*
 *  @(#)OrderRePrice.java   02.07.07
 * 
 *  Fin del fichero OrderRePrice.java
 *  
 *  Versión 2.2
 *
 */
