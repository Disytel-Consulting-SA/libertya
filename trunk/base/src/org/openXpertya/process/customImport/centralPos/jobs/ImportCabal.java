package org.openXpertya.process.customImport.centralPos.jobs;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MCommissionConcepts;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MExpenseConcepts;
import org.openXpertya.model.MIVASettlements;
import org.openXpertya.model.MPerceptionsSettlement;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.MWithholdingSettlement;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.model.X_I_CabalPayments;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.mapping.CabalPaymentAndMovements;
import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.mapping.extras.CabalMovements;
import org.openXpertya.process.customImport.centralPos.mapping.extras.CabalPayments;
import org.openXpertya.process.customImport.centralPos.pojos.cabal.movimientos.CabalMovimientos;
import org.openXpertya.process.customImport.centralPos.pojos.cabal.pagos.CabalPagos;
import org.openXpertya.process.customImport.centralPos.pojos.cabal.pagos.Datum;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

public class ImportCabal extends Import {

	public ImportCabal(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_CABAL, ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException, Exception {
		CabalPagos response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(); // Metodo get para obtener pagos de cabal.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			
			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}
			
			StringBuffer fields = new StringBuffer();
			for (String field : CabalPayments.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			
			response = (CabalPagos) get.execute(CabalPagos.class); // Ejecuto la consulta.

			currentPage = response.getPagos().getCurrentPage();
			lastPage = response.getPagos().getLastPage();

			// Por cada resultado, inserto en la tabla de importación.
			List<Datum> data = response.getPagos().getData();

			CabalPaymentAndMovements pam;
			for (Datum datum: data) {
				CabalPayments payment = new CabalPayments(datum);
				// Obtener los movimientos de Cabal para tener el costo financiero
				List<CabalMovements> moves = getMovements(datum);
				BigDecimal costo_fin = BigDecimal.ZERO;
				// Este movimiento es el que se registra
				org.openXpertya.process.customImport.centralPos.pojos.cabal.movimientos.Datum theMove = new org.openXpertya.process.customImport.centralPos.pojos.cabal.movimientos.Datum();
				for (CabalMovements m : moves) {
					costo_fin = costo_fin.add(new BigDecimal((String)m.getValue("costo_fin_cup")));
				}
				
				// Armar un solo movimiento con el total del costo financiero para agregarlo al
				// pago actual 
				theMove.setId(datum.getId());
				theMove.setFecha_pago(datum.getFecha_pago());
				theMove.setNumero_comercio(datum.getNumero_comercio());
				theMove.setNumero_liquidacion(datum.getNumero_liquidacion());
				theMove.setCosto_fin_cup(String.valueOf(costo_fin));
				
				// Guardar el Pago Cabal
				pam = new CabalPaymentAndMovements(payment);
				pam.setMovement(new CabalMovements(theMove));
				int no = pam.save(ctx, trxName);
				if (no > 0) {
					processed += no;
				} else if (no < 0) {
					areadyExists += (no * -1);
				}
			}
			
			
			log.info("Procesados = " + processed + ", Preexistentes = " + areadyExists + ", Pagina = " + currentPage + "/" + lastPage);
			currentPage++;
		}
		return msg(new Object[] { processed, areadyExists });
	}

	
	/**
	 * Obtener los movimientos de cabal
	 * 
	 * @param pago datum del pago actual
	 * @return lista de movimientos de cabal para el ID parámetro
	 */
	private List<CabalMovements> getMovements(Datum pago){
		List<CabalMovements> moves = new ArrayList<CabalMovements>();
		String url = externalService.getAttributeByName("URL Movimientos").getName();
		//url = url.replace("{id}", pago.getId());
		Get get = makeGetter(url);
		// Los filtros deberían ser número de liquidación, fecha y número de comercio
		get.addQueryParam("numero_liquidacion", pago.getNumero_liquidacion());
		get.addQueryParam("numero_comercio", pago.getNumero_comercio());
		get.addQueryParam("fecha_pago", pago.getFecha_pago());
		
		CabalMovimientos movimientos = null;
		try {
			movimientos = (CabalMovimientos) get.execute(CabalMovimientos.class);
			if(movimientos.getMovimientos() != null) {
				for (org.openXpertya.process.customImport.centralPos.pojos.cabal.movimientos.Datum dm : movimientos.getMovimientos()) {
					moves.add(new CabalMovements(dm));
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return moves;
	}
	
	
	@Override
	public void setDateFromParam(Timestamp date) {
		if (date != null) {
			addParam("fecha_pago", Env.getDateFormatted(date));
		}
	}

	@Override
	public void setDateToParam(Timestamp date) {
		if (date != null) {
			addParam("fecha_pago", Env.getDateFormatted(date));
		}
	}

	@Override
	public void validate(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes,
			String trxName) throws Exception {
		
		int id = getC_BPartner_ID(ctx, rs.getString("numero_comercio"), trxName);
		if (id <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		
		String name = "IVA 21";
		id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto IVA 21");
		}
		
		name = "Importe Arancel";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Importe Arancel");
		}
		
		name = "Ret IVA";
		id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Ret IVA");
		}
		
		name = "Ret Ganancias";
		id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Ret Ganancias");
		}
		
		name = "Costo Financiero";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Costo plan acelerado cuotas");
		}
		
		name = "IVA";
		id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto IVA");
		}
		
		//IIBB
		id = getRetencionSchemaByNroEst(ctx, rs.getString("numero_comercio"), trxName);
		if (id <= 0) {
			throw new Exception("No existe retención de IIBB para la región configurada en la E.Financiera");
		} 
	}

	@Override
	public boolean create(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes,
			String trxName) throws Exception {
		int C_BPartner_ID = getC_BPartner_ID(ctx, rs.getString("numero_comercio"), trxName);
		if (C_BPartner_ID <= 0) {
			throw new Exception("Número de comercio \"" + rs.getString("numero_comercio") + "\" ignorado.");
		}
		
		int M_EntidadFinanciera_ID = getM_EntidadFinanciera_ID(ctx, rs.getString("numero_comercio"), trxName);
		if (M_EntidadFinanciera_ID <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		
		MEntidadFinanciera ef = new MEntidadFinanciera(ctx, M_EntidadFinanciera_ID, trxName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(rs.getString("fecha_pago"));
		
		int C_CreditCardSettlement_ID = getSettlementIdFromNroAndBPartner(ctx, rs.getString("numero_liquidacion"), C_BPartner_ID,
				new Timestamp(date.getTime()), trxName);
		MCreditCardSettlement settlement = null;
		if (C_CreditCardSettlement_ID > 0) {
			settlement = new MCreditCardSettlement(ctx, C_CreditCardSettlement_ID, trxName);
			if (!settlement.getDocStatus().equals(MCreditCardSettlement.DOCSTATUS_Drafted)) {
				// Se marca importado si ya existe para que luego no se
				// levante como un registro pendiente de importación 
				return false;
			}
		}

		BigDecimal netAmt = safeMultiply(rs.getString("importe_neto_final"), rs.getString("signo_importe_neto_final"), "+");
		BigDecimal amt = safeMultiply(rs.getString("importe_venta"), rs.getString("signo_importe_bruto"), "+");

		//Acumuladores para totales de impuestos, tasas, etc.
		BigDecimal withholdingAmt = new BigDecimal(0);
		BigDecimal perceptionAmt = new BigDecimal(0);
		BigDecimal expensesAmt = new BigDecimal(0);
		BigDecimal ivaAmt = new BigDecimal(0);
		BigDecimal commissionAmt = new BigDecimal(0);
		
		if(settlement == null) {
			settlement = new MCreditCardSettlement(ctx, 0, trxName);
		}
		settlement.setGenerateChildrens(false);
		settlement.setAD_Org_ID(ef.getAD_Org_ID());
		settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_CABAL);
		settlement.setC_BPartner_ID(C_BPartner_ID);
		settlement.setPaymentDate(new Timestamp(date.getTime()));
		settlement.setAmount(amt);
		settlement.setNetAmount(netAmt);
		settlement.setC_Currency_ID(Env.getC_Currency_ID(ctx));
		settlement.setSettlementNo(rs.getString("numero_liquidacion"));

		if (!settlement.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		} 
		
		try {
			int C_RetencionSchema_ID = getRetencionSchemaByNroEst(ctx, rs.getString("numero_comercio"), trxName);
			BigDecimal withholding = safeMultiply(rs.getString("retencion_ingresos_brutos"), rs.getString("signo_retencion_ingresos_brutos"), "+");
			if (withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID > 0) {
				MRetencionSchema retSchema = new MRetencionSchema(ctx, C_RetencionSchema_ID, trxName);
				MWithholdingSettlement ws = new MWithholdingSettlement(ctx, 0, trxName);
				ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
				ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ws.setAD_Org_ID(settlement.getAD_Org_ID());
				ws.setC_Region_ID(retSchema.getC_Region_ID());
				ws.setAmount(withholding);
				if(!ws.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				withholdingAmt = withholdingAmt.add(withholding);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			String name = "Ret IVA";
			int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
			BigDecimal withholding = safeMultiply(rs.getString("retencion_iva"), rs.getString("signo_retencion_iva"), "+");
			if (withholding.compareTo(new BigDecimal(0)) != 0) {
				MWithholdingSettlement ws = new MWithholdingSettlement(ctx, 0, trxName);
				ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
				ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ws.setAD_Org_ID(settlement.getAD_Org_ID());
				ws.setAmount(withholding);
				if(!ws.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				withholdingAmt = withholdingAmt.add(withholding);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			String name = "Ret Ganancias";
			int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
			BigDecimal withholding = safeMultiply(rs.getString("retencion_ganancias"), rs.getString("signo_retencion_ganancias"), "+");
			if (withholding.compareTo(new BigDecimal(0)) != 0) {
				MWithholdingSettlement ws = new MWithholdingSettlement(ctx, 0, trxName);
				ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
				ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ws.setAD_Org_ID(settlement.getAD_Org_ID());
				ws.setAmount(withholding);
				if(!ws.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				withholdingAmt = withholdingAmt.add(withholding);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			String name = "Importe Arancel";
			int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
			BigDecimal commission = safeMultiply(rs.getString("importe_arancel"), rs.getString("signo_importe_arancel"), "+");
			if (commission.compareTo(new BigDecimal(0)) != 0) {
				MCommissionConcepts cc = new MCommissionConcepts(ctx, 0, trxName);
				cc.setC_CardSettlementConcepts_ID(C_CardSettlementConcepts_ID);
				cc.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				cc.setAD_Org_ID(settlement.getAD_Org_ID());
				cc.setAmount(commission);
				if(!cc.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				commissionAmt = commissionAmt.add(commission);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			String name = "Costo Financiero";
			int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
			BigDecimal expense = safeMultiply(rs.getString("costo_fin_cup"), "+");
			if (expense.compareTo(new BigDecimal(0)) != 0) {
				MExpenseConcepts ec = new MExpenseConcepts(ctx, 0, trxName);
				ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
				ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ec.setAD_Org_ID(settlement.getAD_Org_ID());
				ec.setAmount(expense);
				if(!ec.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				expensesAmt = expensesAmt.add(expense);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			String name = "IVA";
			int C_Tax_ID = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
			BigDecimal perception = safeMultiply(rs.getString("percepcion_rg_3337"), rs.getString("signo_percepcion_3337"), "+");
			if (perception.compareTo(new BigDecimal(0)) != 0) {
				MPerceptionsSettlement ps = new MPerceptionsSettlement(ctx, 0, trxName);
				ps.setC_Tax_ID(C_Tax_ID);
				ps.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ps.setAD_Org_ID(settlement.getAD_Org_ID());
				ps.setAmount(perception);
				if(!ps.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				perceptionAmt = perceptionAmt.add(perception);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			String name = "IVA 21";
			int C_Tax_ID = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
			BigDecimal iva = safeMultiply(rs.getString("importe_iva_arancel"), rs.getString("signo_iva_sobre_arancel"), "+");
			if (iva.compareTo(new BigDecimal(0)) != 0) {
				MIVASettlements iv = new MIVASettlements(ctx, 0, trxName); 
				iv.setC_Tax_ID(C_Tax_ID);
				iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				iv.setAD_Org_ID(settlement.getAD_Org_ID());
				iv.setAmount(iva);
				if(!iv.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				ivaAmt = ivaAmt.add(iva);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		settlement.setWithholding(withholdingAmt);
		settlement.setPerception(perceptionAmt);
		settlement.setExpenses(expensesAmt);
		settlement.setIVAAmount(ivaAmt);
		settlement.setCommissionAmount(commissionAmt);
		if (!settlement.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		} 
		
		return true;
	}

	@Override
	public String getTableName() {
		return X_I_CabalPayments.Table_Name;
	}

	@Override
	public String[] getFilteredFields() {
		return GenericMap.joinArrays(CabalPayments.filteredFields, CabalMovements.filteredFields);
	}
}
