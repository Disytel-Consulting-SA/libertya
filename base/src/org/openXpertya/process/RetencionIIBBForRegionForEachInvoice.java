package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/***
 * Este tipo de retención de ingresos brutos calcula la retención por cada factura de la
 * misma región que el esquema contable y la suma de cada una de ellas es la
 * retención final. La retención es por factura.
 * 
 * @author Matías Cap
 *
 */
public class RetencionIIBBForRegionForEachInvoice extends RetencionIIBBForRegion {

	/** Llevar los datos de retención de cada factura */
	private Map<MInvoice, RetencionDTO> retencionesToApply;
	
	protected BigDecimal calculateAmount() {		
		// Si no hay facturas, es adelantado, por lo tanto debe seguir el mismo
		// algoritmo que al superclase
		if(getInvoiceList().size() == 0){
			return super.calculateAmount();
		}
		
		// Filtrar las facturas por la región 
		changeInvoicesListByRegion();
		// Tomar cada factura y calcular la retención, sumar las mismas para
		// verificar si se debe retener
		BigDecimal baseImponible;      // [BI] Base imponible
		BigDecimal importeRetenido;    // [IR] Importe a retener.
		BigDecimal importeDeterminado;
		BigDecimal descuentoNeto;
		BigDecimal saldo;
		BigDecimal totalRetenciones = BigDecimal.ZERO;
		BigDecimal totalNet = BigDecimal.ZERO;
		BigDecimal retencionesAnteriores;
		BigDecimal exceptionPercent = getBPartner().getRetencionExenPercent(
				getRetencionSchema().getC_RetencionSchema_ID(), getDateTrx());
		RetencionDTO dto;
		setRetencionesToApply(new HashMap<MInvoice, RetencionDTO>());
		// Itero por las facturas y calculo las retenciones para cada caso
		for (int i = 0; i < getInvoiceList().size(); i++) {
			baseImponible = BigDecimal.ZERO;      
			importeRetenido = BigDecimal.ZERO;    
			descuentoNeto = BigDecimal.ZERO;
			importeDeterminado = BigDecimal.ZERO;
			retencionesAnteriores = BigDecimal.ZERO;
			totalNet = getPayNetAmt(getInvoiceList().get(i),getAmountList().get(i));
			saldo = totalNet.subtract(getImporteNoImponible());
			if(saldo.compareTo(BigDecimal.ZERO) > 0){
				// Realizo el cálculo de la retención
				baseImponible = totalNet;
				descuentoNeto = baseImponible.multiply(getDescuentoNeto()).divide(Env.ONEHUNDRED);
				baseImponible = baseImponible.subtract(descuentoNeto);
				BigDecimal porcentajeRetencion = getPorcentajePadron(getPadrones(), getPorcentajeRetencion());
				importeDeterminado = baseImponible.multiply(porcentajeRetencion).divide(Env.ONEHUNDRED);
				// Resto las retenciones realizadas anteriormente
				try{
					retencionesAnteriores = getSumAmts(new ArrayList<BigDecimal>(
							getSumaRetencionesPagosAnteriores(getBPartner(), getInvoiceList().get(i).getAD_Client_ID(),
									null, null, getRetencionSchema(), getInvoiceList().get(i).getID()).values()));
				} catch(Exception e){
					e.printStackTrace();
					retencionesAnteriores = BigDecimal.ZERO;
				}
				importeRetenido = importeDeterminado.subtract(retencionesAnteriores);
				
				if (importeRetenido.compareTo(getImporteMinimoRetencion()) < 0)
					importeRetenido = BigDecimal.ZERO;

				// Excepción 
				if (exceptionPercent.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal rate = Env.ONE.subtract(exceptionPercent.divide(Env.ONEHUNDRED));
					importeRetenido = importeRetenido.multiply(rate);
				}
				// Crear el pojo para esta retención
				dto = new RetencionDTO();
				dto.baseImposible = baseImponible;
				dto.importeDeterminado = importeDeterminado;
				dto.importeRetenido = importeRetenido;
				dto.pagoActual = totalNet;
				dto.porcentaje = porcentajeRetencion;
				dto.retencionSchemaID = getRetencionSchema().getID();
				dto.pagosAnteriores = BigDecimal.ZERO;
				dto.importeNoImponible = getImporteNoImponible();
				
				totalRetenciones = totalRetenciones.add(importeRetenido);
				getRetencionesToApply().put(getInvoiceList().get(i), dto);
			}
		}

		return totalRetenciones;		
	}

	@Override
	public BigDecimal getAmount() {
		// Las retenciones sufridas no deben ser calculadas.
		if (!getRetencionSchema().isSufferedRetencion()
				&& (amount == null || isRecalculateAmount())) {
			// Calcula el monto de la retención.
			BigDecimal amt = calculateAmount();
			// Si no hay facturas, es adelantado, por lo tanto se debe verificar
			// las excenciones
			if(getSrcInvoices().size() == 0){
				amt = calculateExceptions(amt);
			}
			// Se indica que no se debe recalcular el monto.
			setRecalculateAmount(false);
			// Se asigna el nuevo monto de la retencion.
			this.amount = amt;
		}
		return amount;
	}
	
	
	public List<X_M_Retencion_Invoice> save(MAllocationHdr alloc, boolean save) throws Exception {
		// Si no hay facturas, es adelantado, por lo tanto debe seguir el mismo
		// algoritmo que al superclase
		if(getSrcInvoices().size() == 0){
			return super.save(alloc, save);
		}
		
		// Si el monto de retención es menor o igual que cero, no se debe guardar
		// la retención ya que no se retuvo nada.
		if (getAmount().compareTo(Env.ZERO) <= 0)
			return null;
		
		// Se asigna el allocation header como el actual.
		setAllocationHrd(alloc);
		retenciones = new ArrayList<X_M_Retencion_Invoice>();
		RetencionDTO dto;
		
		
		for (MInvoice invoiceSrc : getRetencionesToApply().keySet()) {
			dto = getRetencionesToApply().get(invoiceSrc);
			// Si el importe retenido es mayor a 0, la creo
			if(!Util.isEmpty(dto.importeRetenido, true)){
				retencion = new X_M_Retencion_Invoice(Env.getCtx(),0,getTrxName());
				
				if(alloc != null){
					retencion.setC_AllocationHdr_ID(getAllocationHrd().getC_AllocationHdr_ID());
				}
			
				MInvoice factura_Recaudador = crearFacturaRecaudador(invoiceSrc, dto);
				MInvoice credito_proveedor = crearCreditoProveedor(invoiceSrc, dto);

				retencion.setamt_retenc(dto.importeRetenido);
				retencion.setC_RetencionSchema_ID(dto.retencionSchemaID);
				retencion.setC_Currency_ID(getCurrency().getC_Currency_ID());
				retencion.setC_Invoice_ID(credito_proveedor.getC_Invoice_ID());			
				retencion.setC_Invoice_Retenc_ID(factura_Recaudador.getC_Invoice_ID());
				retencion.setpagos_ant_acumulados_amt(dto.pagosAnteriores);
				retencion.setretenciones_ant_acumuladas_amt(dto.retencionesAnteriores); 
				retencion.setpago_actual_amt(dto.pagoActual);
				retencion.setimporte_no_imponible_amt(dto.importeNoImponible);
				retencion.setretencion_percent(dto.porcentaje);
				retencion.setimporte_determinado_amt(dto.importeDeterminado);
				retencion.setbaseimponible_amt(dto.baseImposible);
				retencion.setIsSOTrx(isSOTrx());
				retencion.setC_Invoice_Src_ID(invoiceSrc.getID());
				if (save)
					if(!retencion.save())
						throw new Exception(CLogger.retrieveErrorAsString());
				retenciones.add(retencion);	
			}
		}
		
		return retenciones;
	}

	
	private MInvoice crearFacturaRecaudador(MInvoice invoiceSrc, RetencionDTO dto) throws Exception {
		/*  Factura */
		MInvoice recaudador_fac = new MInvoice(Env.getCtx(),0,getTrxName());
		Integer nrolinea = 10;

		int locationID = DB.getSQLValue(getTrxName(), " select C_BPartner_Location_ID from C_BPartner_Location where C_BPartner_id = ? ", getRetencionSchema().getC_BPartner_Recaudador_ID());
		
		if (locationID == -1)
            throw new Exception( "@NoCollectorLocation@" );
        
		// Se obtiene el tipo de documento de factura de recaudador
		int docTypeID = getRetencionSchema().getCollectorInvoiceDocType(); 
		if (docTypeID > 0)
			recaudador_fac.setC_DocTypeTarget_ID(docTypeID);
		// Si no existe el tipo de doc específico asigno Factura de Proveedor.
		else
			recaudador_fac.setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_APInvoice);		

		recaudador_fac.setC_BPartner_ID(getRetencionSchema().getC_BPartner_Recaudador_ID());
		recaudador_fac.setDateInvoiced(getDateTrx());
		recaudador_fac.setDateAcct(getDateTrx());
		recaudador_fac.setC_Currency_ID(getCurrency().getC_Currency_ID());
		recaudador_fac.setIsSOTrx(isSOTrx());
		recaudador_fac.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		recaudador_fac.setDocAction(MInvoice.DOCACTION_Complete);
		recaudador_fac.setC_BPartner_Location_ID(locationID);
		recaudador_fac.setCUIT(null);
		recaudador_fac.setPaymentRule(invoiceSrc.getPaymentRule());
		recaudador_fac.setCurrentAccountVerified(true);
		recaudador_fac.setC_Project_ID(getProjectID());
		recaudador_fac.setC_Campaign_ID(getCampaignID());
		
		char issotrx='N';
		if (recaudador_fac.isSOTrx())
			issotrx = 'Y';
		//Settear M_PriceList
		int priceListID = DB.getSQLValue(getTrxName(), "SELECT M_PriceList_ID FROM M_PriceList pl WHERE pl.issopricelist = '" + issotrx
				+ "' AND (pl.AD_Org_ID = " + recaudador_fac.getAD_Org_ID() + " OR pl.AD_Org_ID = 0) AND pl.C_Currency_ID = " + Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" )
				+ " AND pl.AD_Client_ID = " + getAD_Client_ID() + " AND pl.isActive = 'Y'"
				+ " ORDER BY pl.AD_Org_ID desc,pl.isDefault desc");
		
		if (priceListID <= 0) {
			String iso_code =DB.getSQLValueString(getTrxName(), "SELECT iso_Code FROM C_Currency WHERE C_Currency_ID = ?" , Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" ));
			throw new Exception(Msg.getMsg(Env.getCtx(), "ErrorCreatingCreditDebit", new Object[]{getMsg((recaudador_fac.isSOTrx()?"Purchase":"Sales")), iso_code}));
		}
		recaudador_fac.setM_PriceList_ID(priceListID);
		
		if (!recaudador_fac.save())  
			 throw new Exception( "@CollectorInvoiceSaveError@"+ ". " + CLogger.retrieveErrorAsString());
		
		/* Linea de la factura */
		MInvoiceLine fac_linea = new MInvoiceLine(Env.getCtx(),0,getTrxName());
		fac_linea.setC_Invoice_ID(recaudador_fac.getC_Invoice_ID());
		fac_linea.setM_Product_ID(getRetencionSchema().getProduct());
		fac_linea.setLineNetAmt(dto.pagoActual);
		fac_linea.setC_Tax_ID(taxExenc);
		fac_linea.setLine(nrolinea);
		fac_linea.setQty(1);
		fac_linea.setPriceEntered(dto.importeRetenido);
		fac_linea.setPriceActual(dto.importeRetenido);
		fac_linea.setC_Project_ID(recaudador_fac.getC_Project_ID());
		if(! fac_linea.save())  
			throw new Exception("@CollectorInvoiceLineSaveError@" + ". " + CLogger.retrieveErrorAsString());
		
		/*Completo la factura*/
		if(!DocumentEngine.processAndSave(recaudador_fac, DocAction.ACTION_Complete, false)){
			throw new Exception(recaudador_fac.getProcessMsg());
		}
		
		return recaudador_fac;
	}
	
	private MInvoice crearCreditoProveedor(MInvoice invoiceSrc, RetencionDTO dto) throws Exception {
		/*  Nota de Credito al proveedor por el dinero retenido  */
		
		MInvoice credito_prov = new MInvoice(Env.getCtx(),0,getTrxName());
		Integer nrolinea = 10;

		int locationID = DB.getSQLValue(null, " select C_BPartner_Location_ID from C_BPartner_Location where C_BPartner_id = ? ", getBPartner().getC_BPartner_ID());
		if(locationID == -1){
            throw new Exception( "@NoVendorLocation@" );
        }
		
		// Se obtiene el tipo de documento de comprobante de retencion a provvedor
		int docTypeID = getRetencionSchema().getRetencionCreditDocType();
		if (docTypeID > 0)
			credito_prov.setC_DocTypeTarget_ID(docTypeID);
		// Si no existe el tipo de doc específico asigno Abono de Proveedor.
		else
			credito_prov.setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_APCreditMemo);		
		
		credito_prov.setC_BPartner_ID(getBPartner().getC_BPartner_ID());
		credito_prov.setDateInvoiced(getDateTrx());
		credito_prov.setDateAcct(getDateTrx());
		credito_prov.setC_Currency_ID(getCurrency().getC_Currency_ID());
		credito_prov.setIsSOTrx(isSOTrx());
		credito_prov.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		credito_prov.setDocAction(MInvoice.DOCACTION_Complete);
		credito_prov.setC_BPartner_Location_ID(locationID);
		credito_prov.setCUIT(getBPartner().getTaxID());
		credito_prov.setPaymentRule(invoiceSrc.getPaymentRule());
		credito_prov.setCurrentAccountVerified(true);
		credito_prov.setC_Project_ID(getProjectID());
		credito_prov.setC_Campaign_ID(getCampaignID());
		
		if (getRetencionNumber() != null &&  !getRetencionNumber().trim().equals(""))
			credito_prov.setDocumentNo(getRetencionNumber());
		
		char issotrx='N';
		if (credito_prov.isSOTrx())
			issotrx = 'Y';
		//Settear M_PriceList
		int priceListID = DB.getSQLValue(getTrxName(), "SELECT M_PriceList_ID FROM M_PriceList pl WHERE pl.issopricelist = '" + issotrx
				+ "' AND (pl.AD_Org_ID = " + credito_prov.getAD_Org_ID() + " OR pl.AD_Org_ID = 0) AND pl.C_Currency_ID = " + Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" )
				+ " AND pl.AD_Client_ID = " + getAD_Client_ID() + " AND pl.isActive = 'Y'"
				+ " ORDER BY pl.AD_Org_ID desc,pl.isDefault desc");
		
		if (priceListID <= 0) {
			String iso_code =DB.getSQLValueString(getTrxName(), "SELECT iso_Code FROM C_Currency WHERE C_Currency_ID = ?" , Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" ));
			//throw new Exception(Msg.getMsg(Env.getCtx(), "ErrorCreatingCreditDebit", new Object[]{getMsg((recaudador_fac.isSOTrx()?"Purchase":"Sales")), iso_code}));
		}
		credito_prov.setM_PriceList_ID(priceListID);
		
		if(!credito_prov.save())		   
			throw new Exception("@VendorRetencionDocSaveError@"+ ". " + CLogger.retrieveErrorAsString());

		/* Linea de la nota de credito */
		MInvoiceLine cred_linea = new MInvoiceLine(Env.getCtx(),0,getTrxName());
		cred_linea.setC_Invoice_ID(credito_prov.getC_Invoice_ID());
		cred_linea.setM_Product_ID(getRetencionSchema().getProduct());
		cred_linea.setLineNetAmt(dto.pagoActual);
		cred_linea.setC_Tax_ID(taxExenc);
		cred_linea.setLine(nrolinea);
		cred_linea.setQty(1);
		cred_linea.setPriceEntered(dto.importeRetenido);
		cred_linea.setPriceActual(dto.importeRetenido);
		cred_linea.setC_Project_ID(credito_prov.getC_Project_ID());
		if(!cred_linea.save())		   
			 throw new Exception( "@VendorRetencionDocLineSaveError@"+ ". " + CLogger.retrieveErrorAsString());
		
		/*Completo la factura*/
		if(!DocumentEngine.processAndSave(credito_prov, DocAction.ACTION_Complete, false)){
			throw new Exception(credito_prov.getProcessMsg());
		}
		
		retencion.setC_InvoiceLine_ID(cred_linea.getC_InvoiceLine_ID());
		
		return credito_prov;
				
	}
	
	
	@Override
	public boolean clearAll() {
		getRetencionesToApply().clear();
		return super.clearAll();
	}

	protected Map<MInvoice, RetencionDTO> getRetencionesToApply() {
		return retencionesToApply;
	}

	protected void setRetencionesToApply(Map<MInvoice, RetencionDTO> retencionesToApply) {
		this.retencionesToApply = retencionesToApply;
	}
	
}
