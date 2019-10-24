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
import org.openXpertya.model.X_I_AmexPaymentsAndTaxes;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.mapping.AmexPaymentsWithTaxes;
import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.mapping.extras.AmexPayments;
import org.openXpertya.process.customImport.centralPos.mapping.extras.AmexTaxes;
import org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos.AmexImpuestos;
import org.openXpertya.process.customImport.centralPos.pojos.amex.pagos.AmexPagos;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Proceso de importación. Amex.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportAmex extends Import {

	public ImportAmex(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_AMEX, ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException, Exception {
		AmexPagos response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(); // Metodo get para obtener pagos.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : AmexPayments.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			response = (AmexPagos) get.execute(AmexPagos.class); // Ejecuto la consulta.

			currentPage = response.getPagos().getCurrentPage();
			lastPage = response.getPagos().getLastPage();

			List<String> secNumbers = new ArrayList<String>();
			List<AmexPayments> payments = new ArrayList<AmexPayments>();

			// Por cada resultado, inserto en la tabla de importación.
			List<org.openXpertya.process.customImport.centralPos.pojos.amex.pagos.Datum> data = response.getPagos().getData();

			for (org.openXpertya.process.customImport.centralPos.pojos.amex.pagos.Datum datum: data) {
				AmexPayments payment = new AmexPayments(datum);
				secNumbers.add(payment.getNumSecPago());
				payments.add(payment);
			}

			List<AmexTaxes> taxes = importAmexTaxes(secNumbers);
			for (AmexPayments payment : payments) {
				if (payment != null) {
					AmexPaymentsWithTaxes apwt = null;
					for (AmexTaxes tax : taxes) {
						if (tax != null) {
							if (AmexPaymentsWithTaxes.match(payment, tax)) {
								apwt = new AmexPaymentsWithTaxes(payment, tax);
								int no = apwt.save(ctx, trxName);
								if (no > 0) {
									processed += no;
								} else if (no < 0) {
									areadyExists += (no * -1);
								}
							}
						}
					}
					if (apwt == null) {
						apwt = new AmexPaymentsWithTaxes(payment);
						int no = apwt.save(ctx, trxName);
						if (no > 0) {
							processed += no;
						} else if (no < 0) {
							areadyExists += (no * -1);
						}
					}
					
				}
			}
			log.info("Procesados = " + processed + ", Preexistentes = " + areadyExists + ", Pagina = " + currentPage + "/" + lastPage);
			currentPage++;
		}
		return msg(new Object[] { processed, areadyExists });
	}

	private List<AmexTaxes> importAmexTaxes(List<String> secNumbers) throws SaveFromAPIException, Exception {
		AmexImpuestos response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<AmexTaxes> taxes = new ArrayList<AmexTaxes>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(externalService.getAttributeByName("URL Impuestos").getName()); // Metodo get para obtener impuestos.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			// Filtro por "número secuencial de pago".
			StringBuffer secNumbersStr = new StringBuffer();
			for (String s : secNumbers) {
				secNumbersStr.append(s + ",");
			}
			if (secNumbersStr.length() > 0) {
				secNumbersStr.deleteCharAt(secNumbersStr.length() - 1);
				get.addQueryParam("num_sec_pago-in", secNumbersStr);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : AmexTaxes.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			response = (AmexImpuestos) get.execute(AmexImpuestos.class); // Ejecuto la consulta.

			currentPage = response.getImpuestos().getCurrentPage();
			lastPage = response.getImpuestos().getLastPage();

			List<org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos.Datum> data = response.getImpuestos().getData();

			for (org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos.Datum datum: data) {
				AmexTaxes tax = new AmexTaxes(datum);
				taxes.add(tax);
			}
			currentPage++;
		}
		return taxes;
	}

	@Override
	public void setDateFromParam(Timestamp date) {
		if (date != null) {
			addParam("fecha_pago-min", Env.getDateFormatted(date));
		}
	}

	@Override
	public void setDateToParam(Timestamp date) {
		if (date != null) {
			addParam("fecha_pago-max", Env.getDateFormatted(date));
		}
	}

	@Override
	public void validate(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes,
			String trxName) throws Exception {
		int id = getC_BPartner_ID(ctx, rs.getString("num_est"), trxName);
		if (id <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		
		String name = "Imp total desc aceleracion";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Imp total desc aceleracion");
		}
		
		name = "Importe Descuento";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Importe Descuento");
		}
		
		name = "IVA 21";
		id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto IVA 21");
		}
		
		name = "Percepcion IVA";
		id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Percepcion IVA");
		}
		
		name = "Retencion IVA";
		id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Retencion IVA");
		}
		
		name = "Retencion Ganancias";
		id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Retencion Ganancias");
		}

		//IIBB
		id = getRetencionSchemaByNroEst(ctx, rs.getString("num_est"), trxName);
		if (id <= 0) {
			throw new Exception("No existe retención de IIBB para la región configurada en la E.Financiera");
		} 
		
		//IIBB Bs. As.
		id = getRetencionSchemaForBsAs();
		if (id <= 0) {
			throw new Exception("No existe retención de IIBB para la provincia de Bs. As. (Cod. 09)");
		}
	}

	@Override
	public boolean create(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes,
			String trxName) throws Exception {
		int C_BPartner_ID = getC_BPartner_ID(ctx, rs.getString("num_est"), trxName);
		if (C_BPartner_ID <= 0) {
			throw new Exception("Ignorado: no se encontró comercio participante en E.Financieras");
		}
		
		int M_EntidadFinanciera_ID = getM_EntidadFinanciera_ID(ctx, rs.getString("num_est"), trxName);
		if (M_EntidadFinanciera_ID <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		MEntidadFinanciera ef = new MEntidadFinanciera(ctx, M_EntidadFinanciera_ID, trxName);
		String settlementNo = rs.getString("num_sec_pago");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(rs.getString("fecha_pago"));
		
		int C_CreditCardSettlement_ID = getSettlementIdFromNroAndBPartner(ctx, settlementNo, C_BPartner_ID,
				date != null ? new Timestamp(date.getTime()) : null, trxName);
		
		BigDecimal amt = safeNumber(rs.getString("imp_neto_ajuste"));

		String codImp = rs.getString("cod_imp");
		if (codImp == null) {
			codImp = "";
		}
		
		//Acumuladores para totales de impuestos, tasas, etc.
		BigDecimal withholdingAmt = new BigDecimal(0);
		BigDecimal perceptionAmt = new BigDecimal(0);
		BigDecimal expensesAmt = new BigDecimal(0);
		BigDecimal ivaAmt = new BigDecimal(0);
		BigDecimal commissionAmt = new BigDecimal(0);

		MCreditCardSettlement settlement = null;
		
		if (C_CreditCardSettlement_ID > 0) {
			settlement = new MCreditCardSettlement(ctx, C_CreditCardSettlement_ID, trxName);
			if (!settlement.getDocStatus().equals(MCreditCardSettlement.DOCSTATUS_Drafted)) {
				// Se marca importado si ya existe para que luego no se
				// levante como un registro pendiente de importación 
				return false;
			} else {
				withholdingAmt = settlement.getWithholding();
				perceptionAmt = settlement.getPerception();
				expensesAmt = settlement.getExpenses();
				ivaAmt = settlement.getIVAAmount();
				commissionAmt = settlement.getCommissionAmount();
			}
		} else {
			settlement = new MCreditCardSettlement(ctx, 0, trxName);
			settlement.setGenerateChildrens(false);
			settlement.setAD_Org_ID(ef.getAD_Org_ID());
			settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_AMEX);
			settlement.setC_BPartner_ID(C_BPartner_ID);
			settlement.setPaymentDate(date != null ? new Timestamp(date.getTime()) : null);
			settlement.setAmount(safeNumber(rs.getString("imp_bruto_est")));
			settlement.setNetAmount(amt);
			settlement.setC_Currency_ID(Env.getC_Currency_ID(ctx));
			settlement.setSettlementNo(rs.getString("num_sec_pago"));

			if (!settlement.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			
			try {
				String name = "Imp total desc aceleracion";
				int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal expense = negativeValue(safeNumber(rs.getString("imp_tot_desc_acel")));
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
				String name = "Importe Descuento";
				int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal commission = negativeValue(safeNumber(rs.getString("imp_desc_pago")));
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
		}
		
		try {
			if (codImp.equals("01")) {
				String name = "IVA 21";
				int C_Tax_ID = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
				BigDecimal iva = negativeValue(safeNumber(rs.getString("importe_imp")));
				if (iva.compareTo(new BigDecimal(0)) != 0) {
					MIVASettlements ps = MIVASettlements.get(ctx,
							settlement.getC_CreditCardSettlement_ID(), C_Tax_ID, trxName);
					if(ps == null){
						ps = new MIVASettlements(ctx, 0, trxName);
					}
					ps.setC_Tax_ID(C_Tax_ID);
					ps.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
					ps.setAD_Org_ID(settlement.getAD_Org_ID());
					ps.setAmount(iva);
					if(!ps.save()){
						throw new Exception(CLogger.retrieveErrorAsString());
					}
					ivaAmt = ivaAmt.add(iva);
				}
			}
			if (codImp.equals("02")) {
				String name = "Percepcion IVA";
				int C_Tax_ID = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
				BigDecimal perception = negativeValue(safeNumber(rs.getString("importe_imp")));
				if (perception.compareTo(new BigDecimal(0)) != 0) {
					MPerceptionsSettlement ps = MPerceptionsSettlement.get(ctx,
							settlement.getC_CreditCardSettlement_ID(), C_Tax_ID, trxName);
					if(ps == null){
						ps = new MPerceptionsSettlement(ctx, 0, trxName);
					}
					ps.setC_Tax_ID(C_Tax_ID);
					ps.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
					ps.setAD_Org_ID(settlement.getAD_Org_ID());
					ps.setAmount(perception);
					if(!ps.save()){
						throw new Exception(CLogger.retrieveErrorAsString());
					}
					perceptionAmt = perceptionAmt.add(perception);
				}
			}
			if (codImp.equals("04")) {
				String name = "Retencion IVA";
				int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal withholding = negativeValue(safeNumber(rs.getString("importe_imp")));
				if (withholding.compareTo(new BigDecimal(0)) != 0) {
					MWithholdingSettlement ws = MWithholdingSettlement.get(ctx,
							settlement.getC_CreditCardSettlement_ID(), C_RetencionSchema_ID, trxName);
					if(ws == null){
						ws = new MWithholdingSettlement(ctx, 0, trxName);
					}
					ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
					ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
					ws.setAD_Org_ID(settlement.getAD_Org_ID());
					ws.setAmount(withholding);
					if(!ws.save()){
						throw new Exception(CLogger.retrieveErrorAsString());
					}
					withholdingAmt = withholdingAmt.add(withholding);
				}
			}
			if (codImp.equals("05")) {
				String name = "Retencion Ganancias";
				int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal withholding = negativeValue(safeNumber(rs.getString("importe_imp")));
				if (withholding.compareTo(new BigDecimal(0)) != 0) {
					MWithholdingSettlement ws = MWithholdingSettlement.get(ctx,
							settlement.getC_CreditCardSettlement_ID(), C_RetencionSchema_ID, trxName);
					if(ws == null){
						ws = new MWithholdingSettlement(ctx, 0, trxName);
					}
					ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
					ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
					ws.setAD_Org_ID(settlement.getAD_Org_ID());
					ws.setAmount(withholding);
					if(!ws.save()){
						throw new Exception(CLogger.retrieveErrorAsString());
					}
					withholdingAmt = withholdingAmt.add(withholding);
				}
			}
			//Si es código 06, busco la región configurada en la E. Financiera y
			//recupero, si existe, un esquema de retención de IIBB para la región. 
			if (codImp.equals("06")) { // IIBB 
				int C_RetencionSchema_ID = getRetencionSchemaByNroEst(ctx, rs.getString("num_est"), trxName);
				
				if (C_RetencionSchema_ID > 0) {
					MRetencionSchema retSchema = new MRetencionSchema(ctx, C_RetencionSchema_ID, trxName); 
					BigDecimal withholding = negativeValue(safeNumber(rs.getString("importe_imp")));
					if (withholding.compareTo(new BigDecimal(0)) != 0) {
						MWithholdingSettlement ws = MWithholdingSettlement.get(ctx,
								settlement.getC_CreditCardSettlement_ID(), C_RetencionSchema_ID, trxName);
						if(ws == null){
							ws = new MWithholdingSettlement(ctx, 0, trxName);
						}
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
				}
			}
			//Si es código 09 recupero, si existe, un esquema de retención de IIBB para la Bs. As.. 
			if (codImp.equals("09")) { // IIBB o IIBB Bs.As.
				int C_RetencionSchema_ID = getRetencionSchemaForBsAs();
				
				if (C_RetencionSchema_ID > 0) {
					MRetencionSchema retSch= new MRetencionSchema(ctx, C_RetencionSchema_ID, trxName); 
					BigDecimal withholding = negativeValue(safeNumber(rs.getString("importe_imp")));
					if (withholding.compareTo(new BigDecimal(0)) != 0) {
						MWithholdingSettlement ws = MWithholdingSettlement.get(ctx,
								settlement.getC_CreditCardSettlement_ID(), C_RetencionSchema_ID, trxName);
						if(ws == null){
							ws = new MWithholdingSettlement(ctx, 0, trxName);
						}
						ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
						ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ws.setAD_Org_ID(settlement.getAD_Org_ID());
						ws.setC_Region_ID(retSch.getC_Region_ID());
						ws.setAmount(withholding);
						if(!ws.save()){
							throw new Exception(CLogger.retrieveErrorAsString());
						}
						withholdingAmt = withholdingAmt.add(withholding);
					}
				}
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
		return X_I_AmexPaymentsAndTaxes.Table_Name;
	}

	@Override
	public String[] getFilteredFields() {
		return GenericMap.joinArrays(AmexPayments.filteredFields, AmexTaxes.filteredFields);
	}
}
