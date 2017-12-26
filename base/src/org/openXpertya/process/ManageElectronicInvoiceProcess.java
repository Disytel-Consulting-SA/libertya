package org.openXpertya.process;

/**
 * Funcionalidad para gestion de factura electrónica en caso de que la misma haya quedado en estado IP.
 * Puede ocurrir que la factura quedó registrada en AFIP pero no recibió el ACK en LY, y el CAE no quedó
 * registrado, o bien que la factura no quedó registrada en AFIP y efecivamente debe informar a AFIP.
 * 
 *  Se contemplan ambas alternativas. 
 *    - En la primera simplemente se permite omitir la validación que requiere
 *      contar con un CAE si el estado es IP (y así poder registrar la factura en AFIP). 
 *    - En la segunda permite registrar manualmente el CAE.
 *  
 */

import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ManageElectronicInvoiceProcess extends SvrProcess {

	/** La factura se encontraba registrada en AFIP? */
	protected boolean isRegistered = false;
	/** CAE de la factura a asignar (en caso de que se encuentre registrada) */
	protected String cae;
	/** Vto CAE de la factura a asignar (en caso de que se encuentre registrada) */
	protected Timestamp vtoCae;
	/** DocumentNo de la factura a asignar (opcional) */
	protected int numeroComprobante = -1;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "ElectronicInvoiceRegistered" )) {
            	isRegistered = "Y".equals((String)para[ i ].getParameter());
            } else if( name.equals( "CAE" )) {
            	cae = (String)para[ i ].getParameter();
            } else if( name.equals( "VtoCAE" )) {
                vtoCae = (( Timestamp )para[ i ].getParameter());
            } else if( name.equals( "NumeroComprobante" )) {
            	numeroComprobante = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

	}

	@Override
	protected String doIt() throws Exception {
	
		String returnMessage = null;
		
		// Recuperar la factura
		MInvoice anInvoice = new MInvoice(getCtx(), getRecord_ID(), get_TrxName());
		
		MDocType invoiceDocType = new MDocType(getCtx(), anInvoice.getC_DocTypeTarget_ID(), get_TrxName());
		if (!invoiceDocType.iselectronic()) {
			throw new Exception("La factura seleccionada no es de tipo electronica");
		}
			
		
		// Si no esta registrada en AFIP, marcar la factura para poder completar sin que requiera nuevamente la validacion contra AFIP
		if (!isRegistered) {
			anInvoice.setcae(null);
			anInvoice.setvtocae(null);
			anInvoice.setSkipIPNoCaeValidation(true);
			returnMessage = "Gestion de factura satisfactoria. Intente completar nuevamente el documento a fin de obtener el CAE.";
		} else {
			// Si está registrada, simplemente almacenar los valores
			if (cae == null || cae.length() == 0)
				throw new Exception("CAE requerido");
			if (vtoCae == null)
				throw new Exception("Vencimiento CAE requerido");
			anInvoice.setcae(cae);
			anInvoice.setvtocae(vtoCae);
			if (numeroComprobante > 0) {
				anInvoice.setNumeroComprobante(numeroComprobante);
				String docNro = CalloutInvoiceExt.GenerarNumeroDeDocumento(anInvoice.getPuntoDeVenta(), numeroComprobante, anInvoice.getLetra(), anInvoice.isSOTrx(), false);
				anInvoice.setDocumentNo(docNro);
				anInvoice.setNumeroDeDocumento(docNro);
			}
			anInvoice.setcaeerror("Factura electronica editada manualmente por " + (MUser.get(getCtx(), Env.getAD_User_ID(getCtx()))).getName() );
			returnMessage = "Gestion de factura satisfactoria.  CAE asignado manualmente. ";
		}
		
		// Intentar persistir
		if (!anInvoice.save()) {
			throw new Exception("Error al gestionar factura: " + CLogger.retrieveErrorAsString());
		}
		
		return returnMessage;
	}

}
