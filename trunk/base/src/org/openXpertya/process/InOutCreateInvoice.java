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

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InOutCreateInvoice extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_InOut_ID = 0;

    private int p_M_PriceList_ID = 0;

    /* Document No */

    private String p_InvoiceDocumentNo = null;

    private int p_POSNumber = 0;
    
    private int p_POSDocNo = 0;
    
    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "M_PriceList_ID" )) {
                p_M_PriceList_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "InvoiceDocumentNo" )) {
                p_InvoiceDocumentNo = ( String )para[ i ].getParameter();
            } else if( name.equals( "PuntoDeVenta" )) {
                p_POSNumber = para[ i ].getParameterAsInt();
            } else if (name.equals( "NumeroComprobante" )) {
            	p_POSDocNo = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_M_InOut_ID = getRecord_ID();
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
        log.info( "M_InOut_ID=" + p_M_InOut_ID + ", M_PriceList_ID=" + p_M_PriceList_ID + ", InvoiceDocumentNo=" + p_InvoiceDocumentNo );

        if( p_M_InOut_ID == 0 ) {
            throw new IllegalArgumentException( "No Shipment" );
        }

        //

        MInOut ship = new MInOut( getCtx(),p_M_InOut_ID,null );

        if( ship.getID() == 0 ) {
            throw new IllegalArgumentException( "Shipment not found" );
        }

        if( !MInOut.DOCSTATUS_Completed.equals( ship.getDocStatus())) {
            throw new IllegalArgumentException( "Shipment not completed" );
        }

        MInvoice invoice = new MInvoice( ship,null );

        if( p_M_PriceList_ID != 0 ) {
            invoice.setM_PriceList_ID( p_M_PriceList_ID );
        }

        if (CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
        	invoice.setNumeroComprobante(p_POSDocNo);
        	invoice.setPuntoDeVenta(p_POSNumber);
        	invoice.setC_DocTypeTarget_ID( MDocType.getDocType(getCtx(), MDocType.DOCTYPE_VendorInvoice, null).getC_DocType_ID() );    // API
        	invoice.setCUIT(new MBPartner(getCtx(), ship.getC_BPartner_ID(), null).getTaxID());
        } else {
        	if( (p_InvoiceDocumentNo != null) && (p_InvoiceDocumentNo.length() > 0) ) {
        		invoice.setDocumentNo( p_InvoiceDocumentNo );
        	}
        }

        if( !invoice.save()) {
            throw new IllegalArgumentException( "Cannot save Invoice - " + log.retrieveError().getValue() );
        }

        MInOutLine[] shipLines = ship.getLines( false );

        for( int i = 0;i < shipLines.length;i++ ) {
            MInOutLine   sLine = shipLines[ i ];
            MInvoiceLine line  = new MInvoiceLine( invoice );

            line.setShipLine( sLine );
            line.setQtyEntered( sLine.getQtyEntered());
            line.setQtyInvoiced( sLine.getMovementQty());

            if( !line.save()) {
                throw new IllegalArgumentException( "Cannot save Invoice Line" + log.retrieveError().getValue() );
            }
        }

        return invoice.getDocumentNo();
    }    // InOutCreateInvoice
}    // InOutCreateInvoice



/*
 *  @(#)InOutCreateInvoice.java   02.07.07
 * 
 *  Fin del fichero InOutCreateInvoice.java
 *  
 *  Versión 2.2
 *
 */
