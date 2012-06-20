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

import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceBatch;
import org.openXpertya.model.MInvoiceBatchLine;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InvoiceBatchProcess extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_InvoiceBatch_ID = 0;

    /** Descripción de Campos */

    private String p_DocAction = null;

    /** Descripción de Campos */

    private MInvoice m_invoice = null;

    /** Descripción de Campos */

    private String m_oldDocumentNo = null;

    /** Descripción de Campos */

    private int m_oldC_BPartner_ID = 0;

    /** Descripción de Campos */

    private int m_oldC_BPartner_Location_ID = 0;

    /** Descripción de Campos */

    private int m_count = 0;

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
            } else if( name.equals( "DocAction" )) {
                p_DocAction = ( String )para[ i ].getParameter();
            }
        }

        p_C_InvoiceBatch_ID = getRecord_ID();
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
        log.info( "C_InvoiceBatch_ID=" + p_C_InvoiceBatch_ID + ", DocAction=" + p_DocAction );

        if( p_C_InvoiceBatch_ID == 0 ) {
            throw new ErrorUsuarioOXP( "C_InvoiceBatch_ID = 0" );
        }

        MInvoiceBatch batch = new MInvoiceBatch( getCtx(),p_C_InvoiceBatch_ID,get_TrxName());

        if( batch.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @C_InvoiceBatch_ID@ - " + p_C_InvoiceBatch_ID );
        }

        if( batch.isProcessed()) {
            throw new ErrorUsuarioOXP( "@Processed@" );
        }

        //

        if( (batch.getControlAmt().signum() != 0) && (batch.getControlAmt().compareTo( batch.getDocumentAmt()) != 0) ) {
            throw new ErrorUsuarioOXP( "@ControlAmt@ <> @DocumentAmt@" );
        }

        //

        MInvoiceBatchLine[] lines = batch.getLines( false );

        for( int i = 0;i < lines.length;i++ ) {
            MInvoiceBatchLine line = lines[ i ];

            if( (line.getC_Invoice_ID() != 0) || (line.getC_InvoiceLine_ID() != 0) ) {
                continue;
            }

            if( ( (m_oldDocumentNo != null) &&!m_oldDocumentNo.equals( line.getDocumentNo())) || (m_oldC_BPartner_ID != line.getC_BPartner_ID()) || (m_oldC_BPartner_Location_ID != line.getC_BPartner_Location_ID())) {
                completeInvoice();
            }

            // New Invoice

            if( m_invoice == null ) {
                m_invoice = new MInvoice( batch,line );

                if( !m_invoice.save()) {
                    throw new ErrorUsuarioOXP( "Cannot save Invoice" );
                }

                //

                m_oldDocumentNo             = line.getDocumentNo();
                m_oldC_BPartner_ID          = line.getC_BPartner_ID();
                m_oldC_BPartner_Location_ID = line.getC_BPartner_Location_ID();
            }

            if( line.isTaxIncluded() != m_invoice.isTaxIncluded()) {

                // rollback

                throw new ErrorUsuarioOXP( "Line " + line.getLine() + " TaxIncluded inconsistent" );
            }

            // Add Line

            MInvoiceLine invoiceLine = new MInvoiceLine( m_invoice );

            invoiceLine.setDescription( line.getDescription());
            invoiceLine.setC_Charge_ID( line.getC_Charge_ID());
            invoiceLine.setQty( line.getQtyEntered());    // Entered/Invoiced
            invoiceLine.setPrice( line.getPriceEntered());
            invoiceLine.setC_Tax_ID( line.getC_Tax_ID());
            invoiceLine.setTaxAmt( line.getTaxAmt());
            invoiceLine.setLineNetAmt( line.getLineNetAmt());
            invoiceLine.setLineTotalAmt( line.getLineTotalAmt());

            if( !invoiceLine.save()) {

                // rollback

                throw new ErrorUsuarioOXP( "Cannot save Invoice Line" );
            }

            // Update Batch Line

            line.setC_Invoice_ID( m_invoice.getC_Invoice_ID());
            line.setC_InvoiceLine_ID( invoiceLine.getC_InvoiceLine_ID());
            line.save();
        }    // for all lines

        completeInvoice();

        //

        batch.setProcessed( true );
        batch.save();

        return "#" + m_count;
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    private void completeInvoice() {
        if( m_invoice == null ) {
            return;
        }

        m_invoice.setDocAction( p_DocAction );
        m_invoice.processIt( p_DocAction );
        m_invoice.save();
        addLog( 0,m_invoice.getDateInvoiced(),m_invoice.getGrandTotal(),m_invoice.getDocumentNo());
        m_count++;
        m_invoice = null;
    }    // completeInvoice
}    // InvoiceBatchProcess



/*
 *  @(#)InvoiceBatchProcess.java   02.07.07
 * 
 *  Fin del fichero InvoiceBatchProcess.java
 *  
 *  Versión 2.2
 *
 */
