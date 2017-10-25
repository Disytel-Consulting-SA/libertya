package org.openXpertya.process;

/**
 * @author Horacio Alvarez - SERVICIOS DIGITALES S.A.
 * @updated 2011-06-25
 * @notes Adaptado para Factura Electronica Argentina v1.0 - Vigencia a partir del 01-07-2011
 */

import java.sql.Timestamp;
import java.util.ArrayList;

import org.openXpertya.electronicInvoice.ElectronicInvoiceInterface;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.util.Env;

public class ProcessorWSFE implements ElectronicInvoiceInterface {
	
	private static ArrayList<String> exteriorDocumentTypes;	
	static {
		// Se inicializan los tipos de documentos del exterior
		exteriorDocumentTypes = new ArrayList<String>();
		exteriorDocumentTypes.add(MDocType.DOCSUBTYPECAE_FacturaDeExportaciónE);
		exteriorDocumentTypes.add(MDocType.DOCSUBTYPECAE_NotaDeDébitoPorOperacionesEnElExterior);
		exteriorDocumentTypes.add(MDocType.DOCSUBTYPECAE_NotaDeCréditoPorOperacionesEnElExterior);;
	}
	
	protected Wsfe wsfe = null;
	
	public Wsfe getWsfe() {
		return wsfe;
	}

	public void setWsfe(Wsfe wsfe) {
		this.wsfe = wsfe;
	}

	/**
	 * @author Castro, Juan Manuel
	 * @descripcion: Constructor. Crea a partir de la entrada MInvoice, el WSFE correspondiente.
	 */
	public ProcessorWSFE (MInvoice inv){
		Wsfe wsfe = null;
		
		MDocType docType = new MDocType(Env.getCtx(), inv.getC_DocTypeTarget_ID(), inv.get_TrxName());	
		if (exteriorDocumentTypes.contains(docType.getdocsubtypecae())){
			wsfe = new WsfexV1(inv); 
		}
		else{
			wsfe = new WsfeV1(inv);
		}
		this.wsfe = wsfe;
	}
	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Método principal.
	 */
	public String generateCAE(){
		this.getWsfe().deleteExistingFiles();
		if(this.getWsfe().getMessageError() == null)
			this.getWsfe().createInputFile();
		if(this.getWsfe().getMessageError() == null)
			this.getWsfe().callProcess();
		if(this.getWsfe().getMessageError() == null)
			this.getWsfe().searchForProblems();		
		if(this.getWsfe().getMessageError() == null)
			this.getWsfe().readOutput();		
		if(this.getWsfe().getMessageError() == null)
			this.getWsfe().setValues();		
		if(this.getWsfe().getMessageError() == null)
			this.getWsfe().check();		
		return this.getWsfe().getMessageError();
	}	
	
	public String getAccepted(){
		return this.wsfe.getAccepted();
	}
	
	public String getCAE(){
		return this.wsfe.getCAE();		
	}
	
	public String getNroCbte(){
		return this.wsfe.getNroCbte();		
	}	
	
	public Timestamp getDateCae(){
		return this.wsfe.getDateCae();		
	}
	
	public String getMensaje(){
		return this.wsfe.getMensaje();
	}

	@Override
	public String getErrorMsg() {
		return this.getWsfe().getMessageError();
	}
	
}
