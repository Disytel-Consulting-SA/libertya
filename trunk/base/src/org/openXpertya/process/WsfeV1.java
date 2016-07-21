package org.openXpertya.process;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceTax;
import org.openXpertya.model.MTax;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * @author Castro, Juan Manul
 */
public class WsfeV1 extends Wsfe{
	
	protected CLogger log = CLogger.getCLogger(WsfexV1.class);
	
	public WsfeV1(MInvoice inv) {
		super(inv);
	}

	/**
	 * @author: Horacio Alvarez
	 * @descripcion: Crea el archivo de entrada para el WSFE.PY.
	 * Setea los valores necesarios con el objeto MInvoice.
	 */
	@Override
	protected void createInputFile(){
		try{
			StringBuffer line  = new StringBuffer();
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
			MDocType docType = new MDocType(Env.getCtx(),this.getInvoice().getC_DocTypeTarget_ID(),getTrxName());
			line.append(docType.getdocsubtypecae()+"\n");
			
			//*****TIPO DOC: 80 CUIT / 96 DNI
			MBPartner partner = new MBPartner(this.getM_ctx(),this.getInvoice().getC_BPartner_ID(),getTrxName());
			if(partner.isConsumidorFinal()){
				line.append("96"+"\n");
				line.append("1"+"\n");
			}
			else{
				line.append("80"+"\n");
				if(this.getInvoice().getCUIT() == null || this.getInvoice().getCUIT().equals("")){
					this.setMessageError(Msg.translate(this.getM_ctx(), "CaeNoCUIT"));
					return;
				}
				
				line.append(this.getInvoice().getCUIT().replaceAll("-", "")+"\n");
			}
			
			//*****IMPORTE TOTAL
			//line.append(this.getInvoice().getGrandTotal().toString().replace(".", "")+"\n");
			line.append(this.getInvoice().getGrandTotal()+"\n");
			
			//*****IMPORTE NETO
			//line.append(this.getInvoice().getTotalLines().toString().replace(".", "")+"\n");
			line.append(this.getInvoice().getNetAmount()+"\n");
			
			//*****FECHA
			if(this.getInvoice().getDateAcct() == null){
				this.setMessageError(Msg.translate(this.getM_ctx(), "CaeNoDateAcct"));
				return;
			}			
			line.append(formatTime(this.getInvoice().getDateAcct(), "yyyyMMdd")+"\n");
			
			//*****PRESTA SERVICIOS 0-->NO  1-->SI  
			line.append("0\n");
			
			//*****MONEDA
			MCurrency currency = new MCurrency(this.getM_ctx(), this.getInvoice().getC_Currency_ID(),getTrxName());
			line.append(currency.getWSFECode()+"\n");
			
			//*****CONVERSION
			// Se debe convertir a la moneda del comprobante desde la moneda de la compañía
			BigDecimal cotizacion = MCurrency.currencyConvert(Env.ONE,
					this.getInvoice().getC_Currency_ID(), 
					Env.getContextAsInt(this.getM_ctx(), "$C_Currency_ID"), 
					this.getInvoice().getDateAcct(), 0,
					this.getM_ctx());
			line.append(cotizacion+"\n");
			
			//*****IMPUESTO
			MInvoiceTax[] taxes = this.getInvoice().getTaxes(false);
			MTax tax = null;
			int size = taxes.length;
			boolean firstLineAppended = false;
			for (int i = 0; i < size; i++){
				tax = MTax.get(this.getM_ctx(), taxes[i].getC_Tax_ID(), getTrxName());
				if (!tax.isPercepcion()){
					taxes[i].getTaxAmt();
					taxes[i].getTaxBaseAmt();
					tax.getWSFECode();
					
					if(firstLineAppended){
						line.append(";");
					}
					line.append(tax.getWSFECode()+":"+getTaxBaseAmt(taxes[i].getTaxBaseAmt(), this.getInvoice().getGrandTotal(), this.getInvoice().getTaxesAmt())+":"+taxes[i].getTaxAmt());
					firstLineAppended = true;	
				}
			}
			
			line.append("\n");
			
			//*****IMPUESTOS PERCEPCIONES
			BigDecimal total_Perception = BigDecimal.ZERO;
			MInvoiceTax[] taxesPerc = this.getInvoice().getTaxes(false);
			MTax taxPerc = null;
			int sizePerc = taxesPerc.length;
			boolean firstLineAppended2 = false;
			for (int i = 0; i < sizePerc; i++){
				taxPerc = MTax.get(this.getM_ctx(), taxesPerc[i].getC_Tax_ID(), getTrxName());
				if (taxPerc.isPercepcion()){
					total_Perception = total_Perception.add(taxesPerc[i].getTaxAmt().setScale(2, BigDecimal.ROUND_HALF_UP)); 
					
					taxesPerc[i].getTaxAmt();
					taxesPerc[i].getTaxBaseAmt();
					taxPerc.getWSFECode();
					
					if(firstLineAppended2){
						line.append(";");
					}
					BigDecimal alic = taxPerc.getRate().setScale(2, BigDecimal.ROUND_HALF_UP);
					line.append(taxPerc.getWSFECode()+":"+getTaxBaseAmt(taxesPerc[i].getTaxBaseAmt(), this.getInvoice().getGrandTotal(), this.getInvoice().getTaxesAmt())+":"+taxesPerc[i].getTaxAmt()+":"+alic);
					firstLineAppended2 = true;	
				}
			}
			
			line.append("\n");
			
			//*****MONTO TOTAL DE PERCEPCIONES
			line.append(total_Perception+"\n");
			
			//*****C_INVOICE_ID para seguimiento
			line.append(this.getInvoice().getC_Invoice_ID()+"\n");
			
			File textFile = new File(getPath()+"entrada.txt");
			FileWriter textOut;
			textOut = new FileWriter(textFile);
			textOut.write(line.toString());
			textOut.close();
		}catch (Exception ex) {
			this.setMessageError(Msg.translate(this.getM_ctx(), "caeErrorCreateInputFile"));
			log.log(Level.SEVERE, this.getMessageError(),ex);
		}			
	}
	
	private BigDecimal getTaxBaseAmt(BigDecimal taxBaseAmt, BigDecimal grandTotal,	BigDecimal taxesAmt) {
		if ((Math.abs((grandTotal.subtract(taxesAmt).subtract(taxBaseAmt)).doubleValue()) >= 0.01) && (Math.abs((grandTotal.subtract(taxesAmt).subtract(taxBaseAmt)).doubleValue()) <= 0.02)){
			return (grandTotal.subtract(taxesAmt));
		}
		return taxBaseAmt;
	}
	
}
