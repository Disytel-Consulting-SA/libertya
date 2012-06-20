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

import java.util.logging.Level;

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InvoiceCreateInOut extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_Warehouse_ID = 0;

    /** Descripción de Campos */

    private int p_C_Invoice_ID = 0;
    
    /** Tipo de documento del remito a crear */
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
            } else if( name.equals( "M_Warehouse_ID" )) {
                p_M_Warehouse_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_DocType_ID" )) {
            	p_C_DocType_ID = para[i].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_C_Invoice_ID = getRecord_ID();
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
        log.info( "C_Invoice_ID=" + p_C_Invoice_ID + ", M_Warehouse_ID=" + p_M_Warehouse_ID );

        if( p_C_Invoice_ID == 0 ) {
            throw new IllegalArgumentException( "@NotFound@ @C_Invoice_ID@" );
        }

        if( p_M_Warehouse_ID == 0 ) {
            throw new IllegalArgumentException( "@NotFound@ @M_Warehouse_ID@" );
        }

        //

        MInvoice invoice = new MInvoice( getCtx(),p_C_Invoice_ID,null );

        if( invoice.getID() == 0 ) {
            throw new IllegalArgumentException( "@NotFound@ @C_Invoice_ID@" );
        }

        if( !MInvoice.DOCSTATUS_Completed.equals( invoice.getDocStatus())) {
            throw new IllegalArgumentException( "@InvoiceCreateDocNotCompleted@" );
        }

        //

        MInOut ship = new MInOut( invoice,p_C_DocType_ID,null,p_M_Warehouse_ID );

        if( !ship.save()) {
            throw new IllegalArgumentException( "@SaveError@ Receipt" );
        }

        //

        MInvoiceLine[] invoiceLines = invoice.getLines( false );

        for( int i = 0;i < invoiceLines.length;i++ ) {
            MInvoiceLine invoiceLine = invoiceLines[ i ];
            MInOutLine   sLine       = new MInOutLine( ship );

            sLine.setInvoiceLine( invoiceLine,0,    // Locator
                invoice.isSOTrx()
                ?invoiceLine.getQtyInvoiced()
                :Env.ZERO );
            sLine.setQtyEntered( invoiceLine.getQtyEntered());
            sLine.setMovementQty( invoiceLine.getQtyInvoiced());

            if( invoice.isCreditMemo()) {
                sLine.setQtyEntered( sLine.getQtyEntered().negate());
                sLine.setMovementQty( sLine.getMovementQty().negate());
            }

            if( !sLine.save()) {
                throw new IllegalArgumentException( "@SaveError@ @M_InOutLine_ID@" );
            }

            //

            invoiceLine.setM_InOutLine_ID( sLine.getM_InOutLine_ID());

            if( !invoiceLine.save()) {
                throw new IllegalArgumentException( "@SaveError@ @C_InvoiceLine_ID@" );
            }
        }

        return ship.getDocumentNo();
    }    // doIt
}    // InvoiceCreateInOut



/*
 *  @(#)InvoiceCreateInOut.java   02.07.07
 * 
 *  Fin del fichero InvoiceCreateInOut.java
 *  
 *  Versión 2.2
 *
 */
