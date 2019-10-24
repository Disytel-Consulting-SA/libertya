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
import org.openXpertya.model.X_I_FirstDataTrailerAndDetail;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.mapping.FirstDataTrailerAndDetail;
import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.mapping.extras.DetailParticipant;
import org.openXpertya.process.customImport.centralPos.mapping.extras.TrailerParticipants;
import org.openXpertya.process.customImport.centralPos.pojos.firstdata.detalle.Detalle;
import org.openXpertya.process.customImport.centralPos.pojos.firstdata.trailer.Trailer;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Proceso de importación. FirstData.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportFirstData extends Import {

	public ImportFirstData(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_FIRSTDATA, ctx, trxName);
	}

	/**
	 * Inicia la importación.
	 * @return Total de elementos importados.
	 * @throws SaveFromAPIException
	 */
	public String excecute() throws SaveFromAPIException, Exception {
		Trailer response; // Respuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		Get get; // Método get.

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			get = makeGetter(); // Metodo get para obtener trailer de participantes.
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : TrailerParticipants.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}
			response = (Trailer) get.execute(Trailer.class); // Ejecuto la consulta.

			currentPage = response.getTrailerParticipantes().getCurrentPage();
			lastPage = response.getTrailerParticipantes().getLastPage();

			List<String> settlementNumbers = new ArrayList<String>();
			List<TrailerParticipants> trailers = new ArrayList<TrailerParticipants>();

			// Por cada resultado, inserto en la tabla de importación.
			for (org.openXpertya.process.customImport.centralPos.pojos.firstdata.trailer.Datum itemResultMap : response.getTrailerParticipantes().getData()) {
				TrailerParticipants tp = new TrailerParticipants(itemResultMap);
				settlementNumbers.add(tp.getSettlementNo());
				trailers.add(tp);
			}

			List<DetailParticipant> details = importFirstDataDetails(settlementNumbers);
			for (TrailerParticipants trailer : trailers) {
				if (trailer != null) {
					FirstDataTrailerAndDetail fdtad = null;
					for (DetailParticipant detail : details) {
						if (detail != null) {
							if (FirstDataTrailerAndDetail.match(trailer, detail)) {
								fdtad = new FirstDataTrailerAndDetail(trailer, detail);
								int no = fdtad.save(ctx, trxName);
								if (no > 0) {
									processed += no;
								} else if (no < 0) {
									areadyExists += (no * -1);
								}
							}
						}
					}
				}
			}
			log.info("Procesados = " + processed + ", Preexistentes = " + areadyExists + ", Pagina = " + currentPage + "/" + lastPage);
			currentPage++;
		}
		return msg(new Object[] { processed, areadyExists });
	}

	private List<DetailParticipant> importFirstDataDetails(List<String> settlementNumbers) throws SaveFromAPIException, Exception {
		Detalle response; // Repuesta.
		int currentPage = 1; // Pagina actual.
		int lastPage = 2; // Ultima pagina.
		Get get; // Método get.

		List<DetailParticipant> taxes = new ArrayList<DetailParticipant>();

		// Mientras resten páginas a importar
		while (currentPage <= lastPage) {
			// Metodo get para obtener detalles de liquidación.
			get = makeGetter(externalService.getAttributeByName("URL detalle liq").getName());
			get.addQueryParam("paginate", resultsPerPage); // Parametro de elem. por pagina.
			get.addQueryParam("page", currentPage); // Parametro de pagina a consultar.

			// Si hay parámetros extra, los agrego.
			if (!extraParams.isEmpty()) {
				get.addQueryParams(extraParams);
			}

			// Filtro por "número de liquidación".
			StringBuffer settlementNoStr = new StringBuffer();
			for (String s : settlementNumbers) {
				settlementNoStr.append(s + ",");
			}
			if (settlementNoStr.length() > 0) {
				settlementNoStr.deleteCharAt(settlementNoStr.length() - 1);
				get.addQueryParam("numero_liquidacion-in", settlementNoStr);
			}

			StringBuffer fields = new StringBuffer();
			for (String field : DetailParticipant.filteredFields) {
				fields.append(field + ",");
			}
			if (fields.length() > 0) {
				fields.deleteCharAt(fields.length() - 1);
				get.addQueryParam("_fields", fields); // Campos a recuperar.
			}

			response = (Detalle) get.execute(Detalle.class); // Ejecuto la consulta.

			lastPage = response.getLiquidacionParticipantes().getLastPage();
			currentPage = response.getLiquidacionParticipantes().getCurrentPage();

			for (org.openXpertya.process.customImport.centralPos.pojos.firstdata.detalle.Datum itemResultMap : response.getLiquidacionParticipantes().getData()) {
				DetailParticipant tax = new DetailParticipant(itemResultMap);
				taxes.add(tax);
			}
			currentPage++;
		}
		return taxes;
	}

	@Override
	public void setDateFromParam(Timestamp date) {
		if (date != null) {
			addParam("fecha_vencimiento_clearing-min", Env.getDateFormatted(date));
		}
	}

	@Override
	public void setDateToParam(Timestamp date) {
		if (date != null) {
			addParam("fecha_vencimiento_clearing-max", Env.getDateFormatted(date));
		}
	}

	@Override
	public void validate(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes, String trxName) throws Exception {
		int id = getC_BPartner_ID(ctx, rs.getString("comercio_participante") + rs.getString("producto"), trxName);
		if (id <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		
		String name = "Arancel";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Arancel");
		}
		
		name = "IVA 21";
		id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto IVA 21");
		}
		
		name = "Costo Financiero";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Costo Financiero");
		}
		
		name = "Percepcion IVA";
		id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Percepción");
		}
		
		name = "Ret Ganancias";
		id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Ret Ganancias");
		}
		
		name = "Ret IVA";
		id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Ret IVA");
		}
		
		name = "Gastos por promocion";
		id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto Gastos por promoción");
		}
		
		//IIBB
		id = getRetencionSchemaByNroEst(ctx, rs.getString("comercio_participante") + rs.getString("producto"), trxName);
		if (id <= 0) {
			throw new Exception("No existe retención de IIBB para la región configurada en la E.Financiera");
		} 
	}
	
	@Override
	public boolean create(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes, String trxName) throws Exception {
		int C_BPartner_ID = getC_BPartner_ID(ctx, rs.getString("comercio_participante") + rs.getString("producto"), trxName);
		if (C_BPartner_ID <= 0) {
			throw new Exception("Ignorado: no se encontró comercio participante en E.Financieras");
		}
		
		int M_EntidadFinanciera_ID = getM_EntidadFinanciera_ID(ctx, rs.getString("comercio_participante") + rs.getString("producto"), trxName);
		if (M_EntidadFinanciera_ID <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		
		MEntidadFinanciera ef = new MEntidadFinanciera(ctx, M_EntidadFinanciera_ID, trxName);			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		if (rs.getString("fecha_vencimiento_clearing") != null && !rs.getString("fecha_vencimiento_clearing").trim().isEmpty()) {
			date = sdf.parse(rs.getString("fecha_vencimiento_clearing"));
		}
		
		int C_CreditCardSettlement_ID = getSettlementIdFromNroAndBPartner(ctx, rs.getString("numero_liquidacion"),
				C_BPartner_ID, new Timestamp(date.getTime()), trxName);
		
		//Acumuladores para totales de impuestos, tasas, etc.
		BigDecimal withholdingAmt = new BigDecimal(0);
		BigDecimal perceptionAmt = new BigDecimal(0);
		BigDecimal expensesAmt = new BigDecimal(0);
		BigDecimal ivaAmt = new BigDecimal(0);
		BigDecimal commissionAmt = new BigDecimal(0);

		MCreditCardSettlement settlement = null;
		
		BigDecimal totalAmt = new BigDecimal(0);
		
		//Sumo o resto al total si el código_movimiento es "Cupon" o "Representacion Cupón"
		if (rs.getString("codigo_movimiento").equals("Cupón") ||
				rs.getString("codigo_movimiento").equals("Representación Cupón")) {
			totalAmt = safeMultiply(rs.getString("importe_total"), rs.getString("importe_total_signo"));
		}
		
		if (C_CreditCardSettlement_ID > 0) {
			settlement = new MCreditCardSettlement(ctx, C_CreditCardSettlement_ID, trxName);
			if (!settlement.getDocStatus().equals(MCreditCardSettlement.DOCSTATUS_Drafted)) {
				// Se marca importado si ya existe para que luego no se
				// levante como un registro pendiente de importación 
				return false;
			} else {
				settlement.setAmount(settlement.getAmount().add(totalAmt));
				if (!settlement.save(trxName)) {
					throw new Exception(CLogger.retrieveErrorAsString());
				} 
			}
		} else {
			settlement = new MCreditCardSettlement(ctx, 0, trxName);
			settlement.setGenerateChildrens(false);
			settlement.setAD_Org_ID(ef.getAD_Org_ID());
			settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_FIRSTDATA);
			settlement.setC_BPartner_ID(C_BPartner_ID);
			settlement.setPaymentDate(new Timestamp(date.getTime()));
			settlement.setAmount(totalAmt);
			settlement.setNetAmount(safeMultiply(rs.getString("neto_comercios"), rs.getString("neto_comercios_signo")));
			settlement.setC_Currency_ID(Env.getC_Currency_ID(ctx));
			settlement.setSettlementNo(rs.getString("numero_liquidacion"));

			if (!settlement.save(trxName)) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			
			try {
				String name = "Arancel";
				int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal commission = safeMultiply(rs.getString("arancel"), rs.getString("arancel_signo"));
				if (commission.compareTo(new BigDecimal(0)) != 0) {
					MCommissionConcepts cc = new MCommissionConcepts(ctx, 0, trxName);
					cc.setC_CardSettlementConcepts_ID(C_CardSettlementConcept_ID);
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
				BigDecimal iva = safeMultiply(rs.getString("iva_aranceles_ri"), rs.getString("iva_aranceles_ri_signo")).add(safeMultiply(rs.getString("iva_dto_pago_anticipado"), rs.getString("iva_dto_pago_anticipado_signo")));
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
				String name = "Costo Financiero";
				int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal expense = safeMultiply(rs.getString("costo_financiero"), rs.getString("costo_financiero_signo"));
				if (expense.compareTo(new BigDecimal(0)) != 0) {
					MExpenseConcepts cc = new MExpenseConcepts(ctx, 0, trxName);
					cc.setC_Cardsettlementconcepts_ID(C_CardSettlementConcepts_ID);
					cc.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
					cc.setAD_Org_ID(settlement.getAD_Org_ID());
					cc.setAmount(expense);
					if(!cc.save()){
						throw new Exception(CLogger.retrieveErrorAsString());
					}
					expensesAmt = expensesAmt.add(expense); 
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
			try {
				String name = "Percepcion IVA";
				int C_Tax_ID = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
				BigDecimal perception = safeMultiply(rs.getString("percepc_iva_r3337"), rs.getString("percepc_iva_r3337_signo"));
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
				String name = "Ret Ganancias";
				int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal withholding = safeMultiply(rs.getString("ret_imp_ganancias"), rs.getString("ret_imp_ganancias_signo"));
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
				String name = "Ret IVA";
				int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal withholding = safeMultiply(rs.getString("ret_iva_ventas"), rs.getString("ret_iva_ventas_signo"));
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
			/* IIBB */
			try {
				int C_RetencionSchema_ID = getRetencionSchemaByNroEst(ctx, rs.getString("comercio_participante") + rs.getString("producto"), trxName);
				BigDecimal withholding = safeMultiply(rs.getString("ret_imp_ingresos_brutos"), rs.getString("ret_imp_ingresos_brutos_signo"));
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
			settlement.setWithholding(withholdingAmt);
			settlement.setPerception(perceptionAmt);
			settlement.setExpenses(expensesAmt);
			settlement.setIVAAmount(ivaAmt);
			settlement.setCommissionAmount(commissionAmt);
			if (!settlement.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			} 
		}
		//Agrego "Gastos por Promoción"
		if (rs.getString("codigo_movimiento").equals("Cupón Crédito") ||
				rs.getString("codigo_movimiento").equals("Cargo") ||
				rs.getString("codigo_movimiento").equals("Contrapartida Cupón")) {
			try {
				String name = "Gastos por promocion";
				int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
				BigDecimal expense = negativeValue(safeMultiply(rs.getString("importe_total"), rs.getString("importe_total_signo")));
				if (expense.compareTo(new BigDecimal(0)) != 0) {
					int MExpenseConcepts_ID = getExpenseConceptIDByValueAndSettlementID(ctx, C_CardSettlementConcept_ID, C_CreditCardSettlement_ID, trxName);
					MExpenseConcepts ec = new MExpenseConcepts(ctx, MExpenseConcepts_ID > 0 ? MExpenseConcepts_ID : 0, trxName);
					if (MExpenseConcepts_ID > 0) {
						ec.setAmount(ec.getAmount().add(expense));
					} else {
						ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
						ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ec.setAD_Org_ID(settlement.getAD_Org_ID());
						ec.setAmount(expense);
					}
					if(!ec.save()){
						throw new Exception(CLogger.retrieveErrorAsString());
					}
					settlement.setExpenses(settlement.getExpenses().add(expense));
					if (!settlement.save()) {
						throw new Exception(CLogger.retrieveErrorAsString());
					} 
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public String getTableName() {
		return X_I_FirstDataTrailerAndDetail.Table_Name;
	}

	@Override
	public String[] getFilteredFields() {
		return GenericMap.joinArrays(TrailerParticipants.filteredFields, DetailParticipant.filteredFields);
	}

}
