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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCommission;
import org.openXpertya.model.MCommissionRun;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MLetraAceptaIva;
import org.openXpertya.model.Tax;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CommissionAPInvoice extends SvrProcess {

    /**
     * Descripción de Método
     *
     */

	private int posNo = -1;
	private int docNo = -1;
	
    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
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
        log.info( "doIt - C_CommissionRun_ID=" + getRecord_ID());

        // Load Data

        MCommissionRun comRun = new MCommissionRun( getCtx(),getRecord_ID(),get_TrxName());

        if( comRun.getID() == 0 ) {
            throw new IllegalArgumentException( "CommissionAPInvoice - No Commission Run" );
        }

        if( Env.ZERO.compareTo( comRun.getGrandTotal()) == 0 ) {
            throw new IllegalArgumentException( "@GrandTotal@ = 0" );
        }

        MCommission com = new MCommission( getCtx(),comRun.getC_Commission_ID(),get_TrxName());

        if( com.getID() == 0 ) {
            throw new IllegalArgumentException( "CommissionAPInvoice - No Commission" );
        }

        if( com.getC_Charge_ID() == 0 ) {
            throw new IllegalArgumentException( "CommissionAPInvoice - No Charge on Commission" );
        }

        MBPartner bp = new MBPartner( getCtx(),com.getC_BPartner_ID(),get_TrxName());

        if( bp.getID() == 0 ) {
            throw new IllegalArgumentException( "CommissionAPInvoice - No BPartner" );
        }

        // Create Invoice

        MInvoice invoice = new MInvoice( getCtx(),0,null );

        invoice.setClientOrg( com.getAD_Client_ID(),com.getAD_Org_ID());
        invoice.setC_DocTypeTarget_ID( MDocType.getDocType(getCtx(), MDocType.DOCTYPE_VendorInvoice, null).getC_DocType_ID() );    // API
        invoice.setBPartner( bp );
        invoice.setDescription("EJECUCION DE COMPROBANTE DE COMISION DE VENTAS: " + bp.getName() );
        invoice.setIsSOTrx(false);
        
        // Disytel: Locale AR requiere mas informacion sobre la factura 
        if (CalloutInvoiceExt.ComprobantesFiscalesActivos())
        {
	        getNextPOSValues(bp);
	        invoice.setNumeroComprobante(docNo);
	        invoice.setPuntoDeVenta(posNo);
	        invoice.setCUIT(bp.getTaxID());
	        
	        Integer letraComprobante = CalloutInvoiceExt.darLetraComprobante(CalloutInvoiceExt.darCategoriaIvaClient(), bp.getC_Categoria_Iva_ID());
	        if (letraComprobante == null)
	        	throw new Exception( "No se pudo determinar la letra de comprobante a partir de la informacion del Empleado" );
	        invoice.setC_Letra_Comprobante_ID(letraComprobante);
	        invoice.setC_Currency_ID(com.getC_Currency_ID());
	        // ------------------------
        }
        
        // invoice.setDocumentNo (comRun.getDocumentNo());         //      may cause unique constraint
        invoice.setSalesRep_ID( getAD_User_ID());    // caller

        if( com.getC_Currency_ID() != invoice.getC_Currency_ID()) {
            throw new IllegalArgumentException( "CommissionAPInvoice - Currency of PO Price List not Commission Currency" );
        }

        //

        if( !invoice.save()) {
            throw new IllegalStateException( "CommissionAPInvoice - cannot save Invoice" );
        }

        // Create Invoice Line

        MInvoiceLine iLine = new MInvoiceLine( invoice );

        iLine.setC_Charge_ID( com.getC_Charge_ID());
        iLine.setQty( 1 );
        iLine.setPrice( comRun.getGrandTotal());
        
        Integer taxExcempt = Tax.getExemptTax(invoice.getAD_Client_ID());
        if (taxExcempt == 0)
        {
        	log.log(Level.WARNING, "Imposible determinar impuesto excento - calculando impuestos de manera standard");
        	iLine.setTax();
        }
        iLine.setC_Tax_ID(taxExcempt);

        if( !iLine.save()) {
            throw new IllegalStateException( "CommissionAPInvoice - cannot save Invoice Line" );
        }

        //

        return "@C_Invoice_ID@ = " + invoice.getDocumentNo();
    }    // doIt
    
    
    private void getNextPOSValues(MBPartner bp)
    {
    	try
    	{
	    	// determinar el ultimo nro de comprobante "emitido" por la Entidad Comercial
	    	PreparedStatement pstmt = DB.prepareStatement(	
					" SELECT COALESCE(MAX(NUMEROCOMPROBANTE)::integer,0::integer), COALESCE(MAX(PUNTODEVENTA)::integer,1::integer) " +
					" FROM C_INVOICE " +
					" WHERE C_BPARTNER_ID = " + bp.getC_BPartner_ID() +  
					" AND ISSOTRX = 'N' ");
			ResultSet rs = pstmt.executeQuery();

			// obtener los numeros a incorporar en la factura 
			rs.next();
			docNo = rs.getInt(1) + 1;
			posNo = rs.getInt(2);
			
			// si el numero de documento se excede, utilizar un nuevo pto. vta. 
			if (docNo == 99999999)
			{
				docNo = 1;
				posNo++;
			}	
    	}
    	catch (Exception e)
    	{
    		log.log(Level.SEVERE, "Excepcion al obtener el numero de comprobante");
    		e.printStackTrace();
    	}

    }
    
}    // CommissionAPInvoice



/*
 *  @(#)CommissionAPInvoice.java   02.07.07
 * 
 *  Fin del fichero CommissionAPInvoice.java
 *  
 *  Versión 2.2
 *
 */
