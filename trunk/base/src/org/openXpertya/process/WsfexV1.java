package org.openXpertya.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MUOM;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * @author Castro, Juan Manul
 */
public class WsfexV1 extends Wsfe{
	
	protected CLogger log = CLogger.getCLogger(WsfexV1.class);
	
	public WsfexV1(MInvoice inv) {
		super(inv);
	}

	@Override
	protected void createInputFile() {
		try{
			StringBuffer line  = new StringBuffer();
			
			/*
	ok		Cbte_nro          - Long (N8)      - Número del comprobante que se solicita autorización. Valores permitidos: desde 00000001 hasta 99999999                - Obligatorio
	ok		Tipo_expo         - Int (N1)       - Tipo de exportación. 1= Exportación definitiva de bienes 2= Servicios 4= Otros                                        - Obligatorio
	ok		Permiso_existente - String(C1)     - Indica si se posee documento aduanero de exportación (permiso de embarque). Posibles Valores: S, N, NULL (vacío)      - Obligatorio
	ok		Dst_cmp           - Double(N3)     - País de destino del comprobante.                                                                                      - Obligatorio
	ok		Cliente           - String (C200)  - Apellido y Nombre o Razón Social del comprador.                                                                       - Obligatorio
			Cuit_pais_cliente - Long (N11)     - CUIT del país destino/Contribuyente. No es obligatorio si se ingresó valor en el campo ID_impositivo                  - Optativo
	ok		Domicilio_cliente - String (C300)  - Domicilio comercial cliente.                                                                                          - Obligatorio
	ok		ID_impositivo     - String (C50)   - Clave de identificación tributaria del comprador. No es obligatorio si se ingresó valor en el campo Cuit_pais_cliente - Optativo
	ok		Moneda_ID         - String(C3)     - Código de moneda. Consultar método FEXGetPARAM_MON para valores permitidos.                                           - Obligatorio
	ok		Moneda_Ctz        - Decimal(N4,6)  - Cotización moneda.                                                                                                    - Obligatorio
			Obs_comerciales   - String(C4000)  - Observaciones comerciales                                                                                             - Optativo
	ok		Imp_total         - Decimal(N13,2) - Importe total                                                                                                         - Obligatorio
			Obs               - String(C1000)  - Observaciones                                                                                                         - Optativo
			Forma_pago        - String(C50)    - Descripción de forma de pago                                                                                          - Optativo
			Incoterms         - String(C3)     - Incoterms – Cláusula de Venta.                                                                                        - Optativo
			Incoterms_Ds      - String(C20)    - Información complementaria del incoterm                                                                               - Optativo
	ok		Idioma_cbte       - String(C1)     - Se deberá indicar en que idioma se hará el documento. Valores posibles: 1,2,3 1:Español 2: Inglés 3: Portugués        - Obligatorio
			Permisos          - Permiso        - Detalle de permisos de embarque y destinos de la mercadería. Se puede validar mediante el método: FEXCheck_Permiso    - Optativo
			Cmps_asoc         - Cmp_asoc       - Detalles de comprobantes asociados.                                                                                   - Optativo
	ok		Items             - Item           - Detalle de los ítems del comprobante                                                                                  - Obligatorio
			
			*/
			
			//*****NRO. COMPROBANTE
			if(this.getInvoice().getNumeroComprobante() == 0){
				this.setMessageError(Msg.translate(this.getM_ctx(), "CaeNoNumeroComprobante"));
				return;
			}
			line.append(this.getInvoice().getNumeroComprobante()+"\n");
			
			//*****PUNTO DE VENTA
			if(this.getInvoice().getPuntoDeVenta() == 0){
				this.setMessageError(Msg.translate(this.getM_ctx(), "CaeNoPuntoDeVenta"));
				return;
			}
			line.append(this.getInvoice().getPuntoDeVenta()+"\n");
			
			//*****TIPO DE COMPROBANTE
			MDocType docType = new MDocType(Env.getCtx(),this.getInvoice().getC_DocTypeTarget_ID(), this.getTrxName());
			line.append(docType.getdocsubtypecae()+"\n");
			
			//*****IMPORTE TOTAL
			line.append(this.getInvoice().getGrandTotal()+"\n");
			
			//*****FECHA
			if(this.getInvoice().getDateAcct() == null){
				this.setMessageError(Msg.translate(this.getM_ctx(), "CaeNoDateAcct"));
				return;
			}			
			line.append(formatTime(this.getInvoice().getDateAcct(), "yyyyMMdd")+"\n");
			
			MClientInfo clientInfo = MClientInfo.get(this.getM_ctx(), this.getInvoice().getAD_Client_ID());
			MOrgInfo orgInfo = MOrgInfo.get(this.getM_ctx(), this.getInvoice().getAD_Org_ID());
			
			//*****MONEDA
			MCurrency currency = new MCurrency(this.getM_ctx(), this.getInvoice().getC_Currency_ID(), this.getTrxName());
			line.append(currency.getWSFECode()+"\n");
			
			//*****COTIZACION
			// Se debe convertir a la moneda del comprobante desde la moneda de la compañía
			BigDecimal cotizacion = MCurrency.currencyConvert(Env.ONE, this.getInvoice().getC_Currency_ID(),
					Env.getContextAsInt(this.getM_ctx(), "$C_Currency_ID"), this.getInvoice().getDateInvoiced(), 0,
					this.getM_ctx());
			line.append(cotizacion+"\n");
			
			//*****TIPO EXPORTACION
			/* 1 Exportación definitiva de Bienes - 2 Servicios - 4 Otros */
			String exportTypeFE = "";
			if(!Util.isEmpty(orgInfo.getExportTypeFE())){
				exportTypeFE = orgInfo.getExportTypeFE();
			}
			else{
				if(!Util.isEmpty(clientInfo.getExportTypeFE())){
					exportTypeFE = clientInfo.getExportTypeFE();
				}
				else{
					this.setMessageError(Msg.translate(this.getM_ctx(), "CaeNoTipoExportacion"));
					return;
				}
			}
			line.append(exportTypeFE+"\n");
			
			//*****PERMISO DE EMBARQUE
			String shipmentPermitFE="";
			// “vacío” si el campo Cbte_Tipo es 20 ó 21 o si Cbte_Tipo es igual a 19 y el campo Tipo_expo es igual a 2 ó 4.
			// Si el tipo de exportación es "Exportación definitiva de Bienes" y el tipo de comprobante es Factura, entonces tomamos el valor de la configuración de Organización / Compañía
			if ((exportTypeFE.compareTo(MClientInfo.EXPORTTYPEFE_ExportaciónDefinitivaDeBienes) == 0) && (docType.getdocsubtypecae().compareTo(MDocType.DOCSUBTYPECAE_FacturaDeExportaciónE) == 0)){
				/* Veo si la configuración es a nivel de organización. */
				if(!Util.isEmpty(orgInfo.getExportTypeFE())){
					shipmentPermitFE = (orgInfo.isShipmentPermitFE() ? "S" : "N");
				}
				else{
					shipmentPermitFE = (clientInfo.isShipmentPermitFE() ? "S" : "N");
				}	
			}
			line.append(shipmentPermitFE+"\n");			
			
			//*****PAIS DESTINO
			String paisDestino;
			if(this.getInvoice().getBPartnerLocation() != null){
				MLocation location = new MLocation(this.getM_ctx(), this.getInvoice().getBPartnerLocation().getC_Location_ID(), this.getInvoice().get_TableName());
				paisDestino = location.getCountry().getCountryCodeFE();
			}
			else{
				this.setMessageError(Msg.translate(this.getM_ctx(), "CaeNoPaisDestino"));
				return;
			}
			line.append(paisDestino+"\n");
			
			//*****CLIENTE
			MBPartner partner = new MBPartner(this.getM_ctx(), this.getInvoice().getC_BPartner_ID(), this.getTrxName());
			line.append(partner.getName()+"\n");
			
			//*****DOMICILIO CLIENTE
			String domicilioCliente;
			if(this.getInvoice().getInvoice_Adress() != null){
				domicilioCliente = this.getInvoice().getInvoice_Adress();
			}
			else{
				this.setMessageError(Msg.translate(this.getM_ctx(), "CaeNoDomicilioCliente"));
				return;
			}
			line.append(domicilioCliente+"\n");
			
			//*****ID IMPOSITIVO
			line.append(partner.getTaxID()+"\n");
			
			//*****IDIOMA COMPROBANTE
			line.append("1"+"\n");
			
			/*
			Pro_codigo       - String (C50)    - Código de producto         - Optativo
			Pro_ds           - String (C4000)  - Descripción de producto    - Obligatorio
			Pro_qty          - Decimal (N12,6) - Cantidad                   - Optativo
			Pro_umed         - Int(N2)         - Unidad de medida.          - Obligatorio 
			Pro_precio_uni   - Decimal (N12,6) - Precio unitario            - Optativo
			Pro_bonificacion - Decimal (N12,6) - Bonificación sobre el item - Optativo
			Pro_total_item   - Decimal (N13,2) - Importe total ítem         - Obligatorio
			*/
			
			line.append("ITEMS"+"\n");
			line.append(this.getInvoice().getLines().length+"\n");
			for (MInvoiceLine invoiceLine : this.getInvoice().getLines()){
				line.append(invoiceLine.getProductName());
				line.append(":");
				line.append(invoiceLine.getQtyEntered());
				line.append(":");
				MUOM uom = MUOM.get(this.getM_ctx(), invoiceLine.getC_UOM_ID()); 
				line.append(uom.getUOMCodeFE());
				line.append(":");
				line.append(invoiceLine.getPriceEntered());
				line.append(":");
				line.append(invoiceLine.getLineTotalAmt());
				line.append("\n");
			}
		
			//*****C_INVOICE_ID para seguimiento
			line.append(this.getInvoice().getC_Invoice_ID()+"\n");
			
			Writer out = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(getPath()+"entrada.txt"), "UTF-8"));
			try {
			    out.write(line.toString());
			} finally {
			    out.close();
			}
		}catch (Exception ex) {
			this.setMessageError(Msg.translate(this.getM_ctx(), "caeErrorCreateInputFile"));
			log.log(Level.SEVERE, this.getMessageError(),ex);
		}		
	}
	
	
}
