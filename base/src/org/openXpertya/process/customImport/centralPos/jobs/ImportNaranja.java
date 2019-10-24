package org.openXpertya.process.customImport.centralPos.jobs;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openXpertya.model.MCommissionConcepts;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MExpenseConcepts;
import org.openXpertya.model.MIVASettlements;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.MWithholdingSettlement;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.model.X_I_NaranjaPayments;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.mapping.NaranjaPayments;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaCoupons;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaHeaders;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaInvoicedConcepts;
import org.openXpertya.process.customImport.centralPos.pojos.naranja.conceptos.Conceptos;
import org.openXpertya.process.customImport.centralPos.pojos.naranja.cupones.Detalle;
import org.openXpertya.process.customImport.centralPos.pojos.naranja.headers.Headers;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Proceso de importación. Naranja.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportNaranja extends Import {

	public ImportNaranja(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_NARANJA, ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException, Exception {
		return importNaranjaCoupons();
	}

	private String importNaranjaCoupons() throws SaveFromAPIException, Exception {
		Detalle response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(); // Metodo get para obtener detalle de cupones con vencimiento en el mes de pago.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : NaranjaCoupons.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			response = (Detalle) get.execute(Detalle.class); // Ejecuto la consulta.

			if(response == null){
				continue;
			}
			
			currentPage = response.getCupones().getCurrentPage();
			lastPage = response.getCupones().getLastPage();

			Set<Map<String, String>> matchingFields = new HashSet<Map<String, String>>();
			List<NaranjaCoupons> coupons = new ArrayList<NaranjaCoupons>();

			for (org.openXpertya.process.customImport.centralPos.pojos.naranja.cupones.Datum datum : response.getCupones().getData()) {
				NaranjaCoupons coupon = new NaranjaCoupons(datum);

				Map<String, String> leFields = new HashMap<String, String>();
				leFields.put("comercio", (String) coupon.getValue("comercio"));
				leFields.put("fecha_pago", (String) coupon.getValue("fecha_pago"));
				
				matchingFields.add(leFields);
				coupons.add(coupon);
			}
			List<NaranjaHeaders> headers = importNaranjaHeaders(matchingFields);
			List<NaranjaInvoicedConcepts> invConcepts = importNaranjaInvoicedConcepts(matchingFields);
			for (NaranjaCoupons coupon : coupons) {
				if (coupon != null) {
					NaranjaPayments payment = new NaranjaPayments(coupon);

					for (NaranjaHeaders header : headers) {
						if (header != null) {
							if (NaranjaPayments.match(coupon, header)) {
								payment.setHeader(header);
								break;
							}
						}
					}
					for (NaranjaInvoicedConcepts invConcept : invConcepts) {
						if (invConcept != null) {
							if (NaranjaPayments.match(coupon, invConcept)) {
								payment.setInvoicedConcept(invConcept);
								break;
							}
						}
					}
					int no = payment.save(ctx, trxName);
					if (no > 0) {
						processed += no;
					} else if (no < 0) {
						areadyExists += (no * -1);
					}
				}
			}
			log.info("Procesados = " + processed + ", Preexistentes = " + areadyExists + ", Pagina = " + currentPage + "/" + lastPage);
			currentPage++;
		}
		return msg(new Object[] { processed, areadyExists });
	}

	private List<NaranjaHeaders> importNaranjaHeaders(Set<Map<String, String>> matchingFields) throws SaveFromAPIException, Exception {
		Headers response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<NaranjaHeaders> headers = new ArrayList<NaranjaHeaders>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(externalService.getAttributeByName("URL Headers").getName()); // Metodo get para obtener headers.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			// Filtro por campos de matching.
			NaranjaPayments p = new NaranjaPayments();
			String[] fieldNames = p.matchingFields;

			for (String field : fieldNames) {
				StringBuffer tmpStr = new StringBuffer();
				for (Map<String, String> map : matchingFields) {
					String str = map.get(field);
					if (str != null) {
						tmpStr.append(str + ",");
					}
				}
				if (tmpStr.length() > 0) {
					tmpStr.deleteCharAt(tmpStr.length() - 1);
					get.addQueryParam(field + "-in", tmpStr);
				}
			}
			StringBuffer fields = new StringBuffer();
			for (String field : NaranjaHeaders.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			response = (Headers) get.execute(Headers.class); // Ejecuto la consulta.

			currentPage = response.getHeaders().getCurrentPage();
			lastPage = response.getHeaders().getLastPage();

			for (org.openXpertya.process.customImport.centralPos.pojos.naranja.headers.Datum datum : response.getHeaders().getData()) {
				NaranjaHeaders header = new NaranjaHeaders(datum);
				headers.add(header);
			}
			currentPage++;
		}
		return headers;
	}

	private List<NaranjaInvoicedConcepts> importNaranjaInvoicedConcepts(Set<Map<String, String>> matchingFields) throws SaveFromAPIException, Exception {
		Conceptos response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<NaranjaInvoicedConcepts> invoicedConcepts = new ArrayList<NaranjaInvoicedConcepts>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			// Metodo get para obtener conceptos facturados a descontar en el mes de pago.
			get = makeGetter(externalService.getAttributeByName("URL Conceptos").getName());
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			// Filtro por campos de matching.
			NaranjaPayments p = new NaranjaPayments();
			String[] fieldNames = p.matchingFields;

			for (String field : fieldNames) {
				StringBuffer tmpStr = new StringBuffer();
				for (Map<String, String> map : matchingFields) {
					String str = map.get(field);
					if (str != null) {
						tmpStr.append(str + ",");
					}
				}
				if (tmpStr.length() > 0) {
					tmpStr.deleteCharAt(tmpStr.length() - 1);
					get.addQueryParam(field + "-in", tmpStr);
				}
			}
			StringBuffer fields = new StringBuffer();
			for (String field : NaranjaInvoicedConcepts.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			response = (Conceptos) get.execute(Conceptos.class); // Ejecuto la consulta.

			currentPage = response.getConceptosFacturadosMeses().getCurrentPage();
			lastPage = response.getConceptosFacturadosMeses().getLastPage();

			for (org.openXpertya.process.customImport.centralPos.pojos.naranja.conceptos.Datum datum : response.getConceptosFacturadosMeses().getData()) {
				NaranjaInvoicedConcepts invoicedConcept = new NaranjaInvoicedConcepts(datum);
				invoicedConcepts.add(invoicedConcept);
			}
			currentPage++;
		}
		return invoicedConcepts;
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

		int id = getC_BPartner_ID(ctx, rs.getString("comercio"), trxName);
		if (id <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}

		String name = "Retencion IVA";
		id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Retencion IVA");
		}
		
		name = "Retencion Ganancias";
		id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Retencion Ganancias");
		}
		
		name = "Comisiones - Conceptos fact a descontar mes pago";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Comisiones - Conceptos fact a descontar mes pago");
		}
		
		name = "IVA 21";
		id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto IVA 21");
		}
		
		name = "Gastos - Conceptos fact a descontar mes pago";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Gastos - Conceptos fact a descontar mes pago");
		}
		
		//IIBB
		id = getRetencionSchemaByNroEst(ctx, rs.getString("comercio"), trxName);
		if (id <= 0) {
			throw new Exception("No existe retención de IIBB para la región configurada en la E.Financiera");
		} 
	}

	@Override
	public boolean create(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes,
			String trxName) throws Exception {
		int C_BPartner_ID = getC_BPartner_ID(ctx, rs.getString("comercio"), trxName);
		if (C_BPartner_ID <= 0) {
			throw new Exception("Ignorado: no se encontró comercio participante en E.Financieras");
		}
		
		int M_EntidadFinanciera_ID = getM_EntidadFinanciera_ID(ctx, rs.getString("comercio"), trxName);
		if (M_EntidadFinanciera_ID <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		
		MEntidadFinanciera ef = new MEntidadFinanciera(ctx, M_EntidadFinanciera_ID, trxName);
		String settlementNo = rs.getString("nro_liquidacion");
		BigDecimal compraAmt = safeMultiply(rs.getString("compra"), "D".equals(rs.getString("tipo_mov")) ? "+" : "-");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(rs.getString("fecha_pago"));
		
		int C_CreditCardSettlement_ID = getSettlementIdFromNroAndBPartner(ctx, settlementNo, C_BPartner_ID,
				new Timestamp(date.getTime()), trxName);
		if (C_CreditCardSettlement_ID > 0) {
			MCreditCardSettlement settlement = new MCreditCardSettlement(ctx, C_CreditCardSettlement_ID, trxName);
			if (!settlement.getDocStatus().equals(MCreditCardSettlement.DOCSTATUS_Drafted)) {
				// Se marca importado si ya existe para que luego no se
				// levante como un registro pendiente de importación 
				return false;
			} else {
				settlement.setAmount(settlement.getAmount().add(compraAmt));
				if (!settlement.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		} else {				

			BigDecimal amt = safeMultiply(rs.getString("neto"), rs.getString("signo_neto"));
			if (settlementNo == null || settlementNo.equals("null")) {
				settlementNo = "";
			}
			
			//Acumuladores para totales de impuestos, tasas, etc.
			BigDecimal withholdingAmt = new BigDecimal(0);
			BigDecimal expensesAmt = new BigDecimal(0);
			BigDecimal ivaAmt = new BigDecimal(0);
			BigDecimal commissionAmt = new BigDecimal(0);
			
			MCreditCardSettlement settlement = new MCreditCardSettlement(ctx, 0, trxName);
			settlement.setGenerateChildrens(false);
			settlement.setAD_Org_ID(ef.getAD_Org_ID());
			settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_NARANJA);
			settlement.setC_BPartner_ID(C_BPartner_ID);
			settlement.setPaymentDate(new Timestamp(date.getTime()));
			settlement.setAmount(compraAmt);
			settlement.setNetAmount(amt);
			settlement.setC_Currency_ID(Env.getC_Currency_ID(ctx));
			settlement.setSettlementNo(settlementNo);

			if (!settlement.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			
			/* IIBB */
			try {
				int C_RetencionSchema_ID = getRetencionSchemaByNroEst(ctx, rs.getString("comercio"), trxName);
				BigDecimal withholding = safeMultiply(rs.getString("ret_ingresos_brutos"), rs.getString("signo_ret_ing_brutos"));
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
				String name = "Retencion IVA";
				int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal withholding = safeMultiply(rs.getString("retencion_iva_140"), rs.getString("signo_ret_iva_140"));
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
				String name = "Retencion Ganancias";
				int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal withholding = safeMultiply(rs.getString("retencion_ganancias"), rs.getString("signo_ret_ganancias"));
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
				String name = "Comisiones - Conceptos fact a descontar mes pago";
				int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal commission = safeMultiply(rs.getString("importe_ara_vto"), rs.getString("signo_ara_vto")).add(safeMultiply(rs.getString("importe_ara_facturado_30"), rs.getString("signo_ara_facturado_30")))
						.add(safeMultiply(rs.getString("importe_ara_facturado_60"), rs.getString("signo_ara_facturado_60"))).add(safeMultiply(rs.getString("importe_ara_facturado_90"), rs.getString("signo_ara_facturado_90")))
						.add(safeMultiply(rs.getString("importe_ara_facturado_120"), rs.getString("signo_ara_facturado_120")));
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
				String name = "IVA 21";
				int C_Tax_ID = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
				BigDecimal iva = safeMultiply(rs.getString("imp_iva_21_vto"), rs.getString("sig_iva_21_vto")).add(safeMultiply(rs.getString("imp_iva_21_facturado_30"), rs.getString("sig_iva_21_facturado_30")))
						.add(safeMultiply(rs.getString("imp_iva_21_facturado_60"), rs.getString("sig_iva_21_facturado_60"))).add(safeMultiply(rs.getString("imp_iva_21_facturado_90"), rs.getString("sig_iva_21_facturado_90")))
						.add(safeMultiply(rs.getString("imp_iva_21_facturado_120"), rs.getString("sig_iva_21_facturado_120")));
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
			
			try {
				String name = "Gastos - Conceptos fact a descontar mes pago";
				int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal expense = safeMultiply(rs.getString("imp_acre_liq_ant_vto"), rs.getString("sig_acre_liq_ant_vto")).add(safeMultiply(rs.getString("imp_acre_liq_ant_facturado_30"), rs.getString("sig_acre_liq_ant_facturado_30")))
						.add(safeMultiply(rs.getString("imp_acre_liq_ant_facturado_60"), rs.getString("sig_acre_liq_ant_facturado_60"))).add(safeMultiply(rs.getString("imp_acre_liq_ant_facturado_90"), rs.getString("sig_acre_liq_ant_facturado_90")))
						.add(safeMultiply(rs.getString("imp_acre_liq_ant_facturado_120"), rs.getString("sig_acre_liq_ant_facturado_120"))).add(safeMultiply(rs.getString("imp_int_plan_esp_vto"), rs.getString("sig_int_plan_esp_vto")))
						.add(safeMultiply(rs.getString("imp_int_plan_esp_facturado_30"), rs.getString("sig_int_plan_esp_facturado_30"))).add(safeMultiply(rs.getString("imp_int_plan_esp_facturado_60"), rs.getString("sig_int_plan_esp_facturado_60")))
						.add(safeMultiply(rs.getString("imp_int_plan_esp_facturado_90"), rs.getString("sig_int_plan_esp_facturado_90"))).add(safeMultiply(rs.getString("imp_int_plan_esp_facturado_120"), rs.getString("sig_int_plan_esp_facturado_120")));
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
			
			settlement.setWithholding(withholdingAmt);
			settlement.setExpenses(expensesAmt);
			settlement.setIVAAmount(ivaAmt);
			settlement.setCommissionAmount(commissionAmt);
			if (!settlement.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		return true;
	}

	@Override
	public String getTableName() {
		return X_I_NaranjaPayments.Table_Name;
	}

	@Override
	public String[] getFilteredFields() {
		return GenericMap.joinArrays(NaranjaCoupons.filteredFields, NaranjaHeaders.filteredFields,
				NaranjaInvoicedConcepts.filteredFields);
	}

}
