package org.openXpertya.process;

import org.openXpertya.OpenXpertya;
import org.openXpertya.electronicInvoice.ElectronicInvoiceInterface;
import org.openXpertya.electronicInvoice.ElectronicInvoiceProvider;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MInvoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class WSFERegisterInvoice {

	public static void main(String[] args) {

		try {
			// Parametros correctos?
			if (args==null || args.length<5) {
				System.out.println("Debe especificar los siguientes argumentos: 1) clientID, 2) orgID, 3) userID, 4) invoiceID, 5) updateNroCbte (Y/N)");
				System.exit(1);
			}
			
			int clientID = Integer.parseInt(args[0]);
			int orgID = Integer.parseInt(args[1]);
			int userID = Integer.parseInt(args[2]);
			int invoiceID = Integer.parseInt(args[3]);
			boolean updateNroCbte = "Y".equalsIgnoreCase(args[4]);
					
			// Obtener el OXP_HOME
			if (System.getenv("OXP_HOME") != null)
				System.setProperty("OXP_HOME", System.getenv("OXP_HOME"));
		  	if (!OpenXpertya.startupEnvironment( false )) {
		  		System.err.println("ERROR: Error al iniciar el ambiente cliente.  Verifique si hay conexión a la BBDD.  Revise la configuración.  Recuerde indicar la variable de entorno OXP_HOME.");
		  		return;
		  	}
		  	System.out.println("Iniciando: " + DB.getDatabaseInfo());
		  	
		  	// Configuracion 
		  	Env.setContext(Env.getCtx(), "#AD_Client_ID", clientID);
		  	Env.setContext(Env.getCtx(), "#AD_Org_ID", orgID);
		  	Env.setContext(Env.getCtx(), "#AD_User_ID", userID);
			MInvoice inv = new MInvoice(Env.getCtx(), invoiceID, null);
			if (inv!=null && inv.getcae()!=null && inv.getcae().length()>0) {
				System.out.println("Imposible generar CAE para factura " + invoiceID + ". La misma ya contiene un CAE: " + inv.getcae());
				System.exit(1);
			}
			int currencyID = DB.getSQLValue(null, "SELECT C_Currency_ID FROM C_AcctSchema WHERE AD_Client_ID = " + clientID);
			Env.setContext(Env.getCtx(), "$C_Currency_ID", currencyID);
			if (inv.getC_Invoice_ID() <= 0) {
				System.out.println("No se ha especificado una factura correcta (invoiceID " + invoiceID + " no encontrado)");
				System.exit(1);
			}
			if (!inv.getDocStatus().equalsIgnoreCase("CO") && !inv.getDocStatus().equalsIgnoreCase("CL")) {
				System.out.println("La factura no se encuentra en estado Completado o Cerrado");
				System.exit(1);
			}
			
			// Generar el CAE
			System.out.println("Generando CAE para factura " + invoiceID + ". NroCbte actual: " + inv.getDocumentNo());
			// Se intenta obtener un proveedor de WSFE, en caso de no encontrarlo se utiliza la vieja version (via pyafipws) 
			ElectronicInvoiceInterface processor = ElectronicInvoiceProvider.getImplementation(inv);
			if (processor==null) {
				processor = new ProcessorWSFE(inv);
			} 
			String errorMsg = processor.generateCAE();
			if (Util.isEmpty(processor.getCAE())) {
				System.out.println("Error obtenido: " + errorMsg);
			} else {
				System.out.println("OK. CAE:" + processor.getCAE() + " VTOCAE:" + processor.getDateCae() + " NROCBTE:" + processor.getNroCbte());
				inv.setcae(processor.getCAE());
				inv.setvtocae(processor.getDateCae());
				inv.setcaeerror(errorMsg);
				if (updateNroCbte) {
					System.out.println("Actualizando nro comprobante: " + inv.getNumeroComprobante() + " -> " + processor.getNroCbte());
					int nroCbte = Integer.parseInt(processor.getNroCbte());
					inv.setNumeroComprobante(nroCbte);
					inv.setDocumentNo(CalloutInvoiceExt.GenerarNumeroDeDocumento(inv.getPuntoDeVenta(), inv.getNumeroComprobante(), inv.getLetra(), inv.isSOTrx(), false));
				}
				if (!inv.save()) {
					System.out.println("Error al actualizar la factura " + CLogger.retrieveErrorAsString());
				} else {
					System.out.println("Factura actualizada OK");
				}
			}
		} catch (Exception e) {
			System.out.println("Error general de procesamiento: " + e.getMessage());
		}
		
	}

	
}
