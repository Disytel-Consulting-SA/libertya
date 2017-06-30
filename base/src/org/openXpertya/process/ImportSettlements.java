package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.MCommissionConcepts;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MExpenseConcepts;
import org.openXpertya.model.MExternalService;
import org.openXpertya.model.MIVASettlements;
import org.openXpertya.model.MPerceptionsSettlement;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.MTax;
import org.openXpertya.model.MWithholdingSettlement;
import org.openXpertya.model.X_C_CardSettlementConcepts;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.model.X_C_Region;
import org.openXpertya.model.X_C_RetencionSchema;
import org.openXpertya.model.X_C_RetencionType;
import org.openXpertya.process.customImport.centralPos.jobs.Import;
import org.openXpertya.process.customImport.centralPos.mapping.AmexPaymentsWithTaxes;
import org.openXpertya.process.customImport.centralPos.mapping.FirstDataTrailerAndDetail;
import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.mapping.NaranjaPayments;
import org.openXpertya.process.customImport.centralPos.mapping.VisaPayments;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Importación de liquidaciones desde tablas temporales.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportSettlements extends SvrProcess {

	private static final String SAVED = "Guardado correctamente";
	private static final String ERROR = "Con errores";
	private static final String IGNORED = "Ignorado";

	/** Eliminar registros importados previamente. */
	private boolean m_deleteOldImported = false;
	/** Tipo de tarjeta. */
	private String p_CreditCardType;
	/** Moneda por defecto. */
	private MCurrency defaultCurrency;


	@Override
	protected void prepare() {
		ProcessInfoParameter[] params = getParameter();
		for (int i = 0; i < params.length; i++) {
			String name = params[i].getParameterName();
			if (params[i].getParameter() == null) {
				;
			} else if (name.equalsIgnoreCase("CreditCardType")) {
				p_CreditCardType = (String) params[i].getParameter();
			} else if (name.equals("DeleteOldImported")) {
				m_deleteOldImported = "Y".equals(params[i].getParameter());
			} else {
				log.log(Level.SEVERE, "ImportSettlements.prepare - Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		GenericMap importClass = null;

		if (p_CreditCardType != null) {
			if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_FIRSTDATA)) {
				importClass = new FirstDataTrailerAndDetail();
			} else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_NARANJA)) {
				importClass = new NaranjaPayments();
			} else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_AMEX)) {
				importClass = new AmexPaymentsWithTaxes();
			} else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_VISA)) {
				importClass = new VisaPayments();
			} else {
				throw new Exception(Msg.getMsg(Env.getAD_Language(getCtx()), "UnknownCreditCardTypeParam"));
			}
		} else {
			throw new Exception(Msg.getMsg(Env.getAD_Language(getCtx()), "InvalidCreditCardTypeParam"));
		}

		// Delete Old Imported
		if (m_deleteOldImported) {
			StringBuffer sql = new StringBuffer();
			int no = 0;

			sql.append("DELETE ");
			sql.append("	" + importClass.getTableName() + " ");
			sql.append("WHERE ");
			sql.append("	I_IsImported = 'Y' ");

			no = DB.executeUpdate(sql.toString());
			log.fine("Delete Old Imported = " + no);
		}

		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		String resultMsg = null;
		ResultSet rs = null;

		// Recupero la moneda por defecto.
		getDefaultCurrency();

		// Selecciono las columnas a importar, de la tabla correspondiente.
		sql.append("SELECT ");
		for (int i = 0; i < importClass.getFields().length; i++) {
			String field = importClass.getFields()[i];
			sql.append("	" + field + ", ");
		}
		sql.append("	" + importClass.getTableName() + "_ID ");

		sql.append("FROM ");
		sql.append("	" + importClass.getTableName() + " ");

		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			// Un posible resultado es guardado, ignorado, o error. Luego con
			// esta información se emitirá un mensaje tras finalizar el proceso.
			Map<String, Integer> result = new HashMap<String, Integer>();
			result.put(SAVED, 0);
			result.put(IGNORED, 0);
			result.put(ERROR, 0);

			boolean someResults = false;

			// Importacion elegida = FirstData
			if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_FIRSTDATA)) {

				int C_ExternalService_ID = getExternalServiceByName(Import.EXTERNAL_SERVICE_FIRSTDATA);
				MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
				Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();

				while (rs.next()) {
					someResults = true;
					boolean isOK = validateForFirstData(rs, importClass.getTableName(), attributes);
					if (isOK) {
						resultMsg = createFromFirstData(rs, importClass.getTableName(), attributes);
						result.put(resultMsg, result.get(resultMsg) + 1);
						Object[] params = new Object[] { "FirstData", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
						resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
					} else {
						result.put(ERROR, result.get(ERROR) + 1);
						Object[] params = new Object[] { "FirstData", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
						resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
					}
				}
			} // Importacion elegida = Naranja
			else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_NARANJA)) {

				int C_ExternalService_ID = getExternalServiceByName(Import.EXTERNAL_SERVICE_NARANJA);
				MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
				Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();

				while (rs.next()) {
					someResults = true;
					boolean isOK = validateForNaranja(rs, importClass.getTableName(), attributes);
					if (isOK) {
						resultMsg = createFromNaranja(rs, importClass.getTableName(), attributes);
						result.put(resultMsg, result.get(resultMsg) + 1);
						Object[] params = new Object[] { "Naranja", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
						resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
					} else {
						result.put(ERROR, result.get(ERROR) + 1);
						Object[] params = new Object[] { "Naranja", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
						resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
					}
				}
			} // Importacion elegida = American Express
			else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_AMEX)) {

				int C_ExternalService_ID = getExternalServiceByName(Import.EXTERNAL_SERVICE_AMEX);
				MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
				Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();

				while (rs.next()) {
					someResults = true;
					boolean isOK = validateForAmex(rs, importClass.getTableName(), attributes);
					if (isOK) {
						resultMsg = createFromAmex(rs, importClass.getTableName(), attributes);
						result.put(resultMsg, result.get(resultMsg) + 1);
						Object[] params = new Object[] { "Amex", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
						resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
					} else {
						result.put(ERROR, result.get(ERROR) + 1);
						Object[] params = new Object[] { "Amex", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
						resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
					}
				}
			} // Importacion elegida = Visa
			else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_VISA)) {

				int C_ExternalService_ID = getExternalServiceByName(Import.EXTERNAL_SERVICE_VISA);
				MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
				Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();

				while (rs.next()) {
					someResults = true;
					boolean isOK = validateForVisa(rs, importClass.getTableName(), attributes);
					if (isOK) {
						resultMsg = createFromVisa(rs, importClass.getTableName(), attributes);
						result.put(resultMsg, result.get(resultMsg) + 1);
						Object[] params = new Object[] { "Visa", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
						resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
					} else {
						result.put(ERROR, result.get(ERROR) + 1);
						Object[] params = new Object[] { "Visa", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
						resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
					}
				}
			}
			if (!someResults) {
				resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationNoResults");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "ImportSettlements.doIt", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return resultMsg;
	}

	/**
	 * Recupero la moneda, por defecto de Argentina.
	 * Posible refactor: Pasar a AD_Preference para hacerlo configurable.
	 */
	private void getDefaultCurrency() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_Currency_ID ");
		sql.append("FROM ");
		sql.append("	" + MCurrency.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	ISO_Code = 'ARS' ");

		int C_Currency_ID = DB.getSQLValue(get_TrxName(), sql.toString());
		defaultCurrency = new MCurrency(getCtx(), C_Currency_ID, get_TrxName());
	}

	/**
	 * Obtiene el ID de una Entidad Comercial vinculada a Entidades
	 * Financieras a travéz del Número de Comercio.
	 * @param establishmentNumber Número de Comercio.
	 * @return ID de la Entidad Comercial, si se encontró, caso contrario -1.
	 */
	private int getC_BPartner_ID(String establishmentNumber) {
		if (establishmentNumber == null || establishmentNumber.trim().isEmpty()) {
			return -1;
		}
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_BPartner_ID ");
		sql.append("FROM ");
		sql.append("	" + MEntidadFinanciera.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	EstablishmentNumber = '" + establishmentNumber + "' ");

		return DB.getSQLValue(get_TrxName(), sql.toString());
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private boolean validateForFirstData(ResultSet rs, String tableName, Map<String, X_C_ExternalServiceAttributes> attributes) {
		try{
			int id = getC_BPartner_ID(rs.getString("comercio_participante"));
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "Ignorado: no se encontró Nro. de comercio en E.Financieras");
				return false;
			}
			
			String name = "Arancel";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Arancel");
				return false;
			}
			name = "IVA 21";
			id = getTaxIDByName(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto IVA 21");
				return false;
			}
			name = "Costo Financiero";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Costo Financiero");
				return false;
			}
			name = "Percepcion IVA";
			id = getTaxIDByName(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Percepción");
				return false;
			}
			name = "Ret Ganancias";
			id = getRetencionSchemaIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Ret Ganancias");
				return false;
			}
			name = "Ret IVA";
			id = getRetencionSchemaIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Ret IVA");
				return false;
			}
			//IIBB
			id = getRetencionSchemaByNroEst(rs.getString("comercio_participante"));
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No existe retención de IIBB para la región configurada en la E.Financiera");
				return false;
			} 
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Crea y guarda una liquidación para importaciones de First Data.
	 * @param rs ResultSet con los datos extraídos de la
	 * tabla de importación correspondiente a First Data.
	 * @param tableName Nombre de la tabla de importación.
	 * @param attributes Atributos de configuracion.
	 * @return Mensaje de resultado de la importación.
	 */
	private String createFromFirstData(ResultSet rs, String tableName, Map<String, X_C_ExternalServiceAttributes> attributes) {
		String result = null;
		try {
			int C_BPartner_ID = getC_BPartner_ID(rs.getString("comercio_participante"));
			if (C_BPartner_ID > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = null;
				if (rs.getString("fecha_vencimiento_clearing") != null && !rs.getString("fecha_vencimiento_clearing").trim().isEmpty()) {
					date = sdf.parse(rs.getString("fecha_vencimiento_clearing"));
				}

				BigDecimal amt = safeMultiply(rs.getString("total_importe_total"), rs.getString("total_importe_total_signo"));
				
				//Acumuladores para totales de impuestos, tasas, etc.
				BigDecimal withholdingAmt = new BigDecimal(0);
				BigDecimal perceptionAmt = new BigDecimal(0);
				BigDecimal expensesAmt = new BigDecimal(0);
				BigDecimal ivaAmt = new BigDecimal(0);
				BigDecimal commissionAmt = new BigDecimal(0);

				MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());
				settlement.setGenerateChildrens(false);

				settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_FIRSTDATA);
				settlement.setC_BPartner_ID(C_BPartner_ID);
				settlement.setPaymentDate(new Timestamp(date.getTime()));
				settlement.setAmount(amt);
				settlement.setNetAmount(safeMultiply(rs.getString("neto_comercios"), rs.getString("neto_comercios_signo")));
				settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
				settlement.setSettlementNo(rs.getString("numero_liquidacion"));

				if (!settlement.save(get_TrxName())) {
					return ERROR;
				}
				/* -- -- -- */
				try {
					String name = "Arancel";
					int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("arancel"), rs.getString("arancel_signo"));
					if (expense.compareTo(new BigDecimal(0)) != 0) {
						MExpenseConcepts ec = new MExpenseConcepts(getCtx(), 0, get_TrxName());
						ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
						ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ec.setAmount(expense);
						ec.save(get_TrxName());
						expensesAmt = expensesAmt.add(expense);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA 21";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal iva = safeMultiply(rs.getString("iva_aranceles_ri"), rs.getString("iva_aranceles_ri_signo")).add(safeMultiply(rs.getString("iva_dto_pago_anticipado"), rs.getString("iva_dto_pago_anticipado_signo")));
					if (iva.compareTo(new BigDecimal(0)) != 0) {
						MIVASettlements iv = new MIVASettlements(getCtx(), 0, get_TrxName()); 
						iv.setC_Tax_ID(C_Tax_ID);
						iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						iv.setAmount(iva);
						iv.save(get_TrxName());
						ivaAmt = ivaAmt.add(iva);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Costo Financiero";
					int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal commission = safeMultiply(rs.getString("costo_financiero"), rs.getString("costo_financiero_signo"));
					if (commission.compareTo(new BigDecimal(0)) != 0) {
						MCommissionConcepts cc = new MCommissionConcepts(getCtx(), 0, get_TrxName());
						cc.setC_CardSettlementConcepts_ID(C_CardSettlementConcepts_ID);
						cc.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						cc.setAmount(commission);
						cc.save(get_TrxName());
						commissionAmt = commissionAmt.add(commission); 
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Percepcion IVA";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal perception = safeMultiply(rs.getString("percepc_iva_r3337"), rs.getString("percepc_iva_r3337_signo"));
					if (perception.compareTo(new BigDecimal(0)) != 0) {
						MPerceptionsSettlement ps = new MPerceptionsSettlement(getCtx(), 0, get_TrxName());
						ps.setC_Tax_ID(C_Tax_ID);
						ps.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ps.setAmount(perception);
						ps.save(get_TrxName());
						perceptionAmt = perceptionAmt.add(perception); 
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Ret Ganancias";
					int C_RetencionSchema_ID = getRetencionSchemaIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_imp_ganancias"), rs.getString("ret_imp_ganancias_signo"));
					if (withholding.compareTo(new BigDecimal(0)) != 0) {
						MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
						ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
						ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ws.setAmount(withholding);
						ws.save(get_TrxName());
						withholdingAmt = withholdingAmt.add(withholding);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Ret IVA";
					int C_RetencionSchema_ID = getRetencionSchemaIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_iva_ventas"), rs.getString("ret_iva_ventas_signo"));
					if (withholding.compareTo(new BigDecimal(0)) != 0) {
						MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
						ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
						ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ws.setAmount(withholding);
						ws.save(get_TrxName());
						withholdingAmt = withholdingAmt.add(withholding);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* IIBB */
				try {
					int C_RetencionSchema_ID = getRetencionSchemaByNroEst(rs.getString("comercio_participante"));
					BigDecimal withholding = safeMultiply(rs.getString("ret_imp_ingresos_brutos"), rs.getString("ret_imp_ingresos_brutos_signo"));
					if (withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID > 0) {
						MRetencionSchema retSchema = new MRetencionSchema(getCtx(), C_RetencionSchema_ID, get_TrxName());
						MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
						ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
						ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ws.setC_Region_ID(retSchema.getC_Region_ID());
						ws.setAmount(withholding);
						ws.save(get_TrxName());
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
				if (!settlement.save(get_TrxName())) {
					return ERROR;
				} else {
					result = SAVED;
					markAsImported(tableName, rs.getInt(tableName + "_ID"));
				}
			} else {
				result = IGNORED;
				markAsError(tableName, rs.getInt(tableName + "_ID"), "Ignorado: no se encontró comercio participante en E.Financieras");
				log.log(Level.WARNING, "Número de comercio \"" + rs.getString("comercio_participante") + "\" ignorado.");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean validateForNaranja(ResultSet rs, String tableName, Map<String, X_C_ExternalServiceAttributes> attributes) {
		try {
			int id = getC_BPartner_ID(rs.getString("comercio"));
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "Ignorado: no se encontró Nro. de comercio en E.Financieras");
				return false;
			}
	
			String name = "Retencion IVA";
			id = getRetencionSchemaIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Retencion IVA");
				return false;
			}
			
			name = "Retencion Ganancias";
			id = getRetencionSchemaIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Retencion Ganancias");
				return false;
			}
			
			name = "Comisiones - Conceptos fact a descontar mes pago";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Comisiones - Conceptos fact a descontar mes pago");
				return false;
			}
			
			name = "IVA 21";
			id = getTaxIDByName(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto IVA 21");
				return false;
			}
			
			name = "Gastos - Conceptos fact a descontar mes pago";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Gastos - Conceptos fact a descontar mes pago");
				return false;
			}
			
			//IIBB
			id = getRetencionSchemaByNroEst(rs.getString("comercio"));
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No existe retención de IIBB para la región configurada en la E.Financiera");
				return false;
			} 
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Crea y guarda una liquidación para importaciones de Naranja.
	 * @param rs ResultSet con los datos extraídos de la
	 * tabla de importación correspondiente a Naranja.
	 * @param tableName Nombre de la tabla de importación.
	 * @param attributes Atributos de configuracion.
	 * @return Mensaje de resultado de la importación.
	 */
	private String createFromNaranja(ResultSet rs, String tableName, Map<String, X_C_ExternalServiceAttributes> attributes) {
		String result = null;
		try {
			int C_BPartner_ID = getC_BPartner_ID(rs.getString("comercio"));
			if (C_BPartner_ID > 0) {
				String settlementNo = rs.getString("nro_liquidacion");
				BigDecimal compraAmt = safeMultiply(rs.getString("compra"), "D".equals(rs.getString("tipo_mov")) ? "+" : "-");
				int C_CreditCardSettlement_ID = getSettlementIdFromNroAndBPartner(settlementNo, C_BPartner_ID);
				if (C_CreditCardSettlement_ID > 0) {
					MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), C_CreditCardSettlement_ID, get_TrxName());
					settlement.setAmount(settlement.getAmount().add(compraAmt));
					if (!settlement.save(get_TrxName())) {
						return ERROR;
					} else {
						result = SAVED;
						markAsImported(tableName, rs.getInt(tableName + "_ID"));
					}
				} else {
				
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = sdf.parse(rs.getString("fecha_pago"));				
	
					BigDecimal amt = safeMultiply(rs.getString("neto"), rs.getString("signo_neto"));
					if (settlementNo == null || settlementNo.equals("null")) {
						settlementNo = "";
					}
					
					//Acumuladores para totales de impuestos, tasas, etc.
					BigDecimal withholdingAmt = new BigDecimal(0);
					BigDecimal expensesAmt = new BigDecimal(0);
					BigDecimal ivaAmt = new BigDecimal(0);
					BigDecimal commissionAmt = new BigDecimal(0);
					
					MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());
					settlement.setGenerateChildrens(false);
	
					settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_NARANJA);
					settlement.setC_BPartner_ID(C_BPartner_ID);
					settlement.setPaymentDate(new Timestamp(date.getTime()));
					settlement.setAmount(compraAmt);
					settlement.setNetAmount(amt);
					settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
					settlement.setSettlementNo(settlementNo);
	
					if (!settlement.save()) {
						return ERROR;
					}
					
					/* IIBB */
					try {
						int C_RetencionSchema_ID = getRetencionSchemaByNroEst(rs.getString("comercio"));
						BigDecimal withholding = safeMultiply(rs.getString("ret_ingresos_brutos"), rs.getString("signo_ret_ing_brutos"));
						if (withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID > 0) {
							MRetencionSchema retSchema = new MRetencionSchema(getCtx(), C_RetencionSchema_ID, get_TrxName());
							MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
							ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
							ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							ws.setC_Region_ID(retSchema.getC_Region_ID());
							ws.setAmount(withholding);
							ws.save();
							withholdingAmt = withholdingAmt.add(withholding);
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					/* -- -- -- */
					try {
						String name = "Retencion IVA";
						int C_RetencionSchema_ID = getRetencionSchemaIDByValue(attributes.get(name).getName());
						BigDecimal withholding = safeMultiply(rs.getString("retencion_iva_140"), rs.getString("signo_ret_iva_140"));
						if (withholding.compareTo(new BigDecimal(0)) != 0) {
							MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
							ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
							ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							ws.setAmount(withholding);
							ws.save();
							withholdingAmt = withholdingAmt.add(withholding);
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					/* -- -- -- */
					try {
						String name = "Retencion Ganancias";
						int C_RetencionSchema_ID = getRetencionSchemaIDByValue(attributes.get(name).getName());
						BigDecimal withholding = safeMultiply(rs.getString("retencion_ganancias"), rs.getString("signo_ret_ganancias"));
						if (withholding.compareTo(new BigDecimal(0)) != 0) {
							MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
							ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
							ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							ws.setAmount(withholding);
							ws.save();
							withholdingAmt = withholdingAmt.add(withholding);
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					/* -- -- -- */
					try {
						String name = "Comisiones - Conceptos fact a descontar mes pago";
						int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
						BigDecimal commission = safeMultiply(rs.getString("importe_ara_vto"), rs.getString("signo_ara_vto")).add(safeMultiply(rs.getString("importe_ara_facturado_30"), rs.getString("signo_ara_facturado_30")))
								.add(safeMultiply(rs.getString("importe_ara_facturado_60"), rs.getString("signo_ara_facturado_60"))).add(safeMultiply(rs.getString("importe_ara_facturado_90"), rs.getString("signo_ara_facturado_90")))
								.add(safeMultiply(rs.getString("importe_ara_facturado_120"), rs.getString("signo_ara_facturado_120")));
						if (commission.compareTo(new BigDecimal(0)) != 0) {
							MCommissionConcepts cc = new MCommissionConcepts(getCtx(), 0, get_TrxName());
							cc.setC_CardSettlementConcepts_ID(C_CardSettlementConcepts_ID);
							cc.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							cc.setAmount(commission);
							cc.save();
							commissionAmt = commissionAmt.add(commission); 
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					/* -- -- -- */
					try {
						String name = "IVA 21";
						int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
						BigDecimal iva = safeMultiply(rs.getString("imp_iva_21_vto"), rs.getString("sig_iva_21_vto")).add(safeMultiply(rs.getString("imp_iva_21_facturado_30"), rs.getString("sig_iva_21_facturado_30")))
								.add(safeMultiply(rs.getString("imp_iva_21_facturado_60"), rs.getString("sig_iva_21_facturado_60"))).add(safeMultiply(rs.getString("imp_iva_21_facturado_90"), rs.getString("sig_iva_21_facturado_90")))
								.add(safeMultiply(rs.getString("imp_iva_21_facturado_120"), rs.getString("sig_iva_21_facturado_120")));
						if (iva.compareTo(new BigDecimal(0)) != 0) {
							MIVASettlements iv = new MIVASettlements(getCtx(), 0, get_TrxName()); 
							iv.setC_Tax_ID(C_Tax_ID);
							iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							iv.setAmount(iva);
							iv.save();
							ivaAmt = ivaAmt.add(iva);
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					/* -- -- -- */
					try {
						String name = "Gastos - Conceptos fact a descontar mes pago";
						int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
						BigDecimal expense = safeMultiply(rs.getString("imp_acre_liq_ant_vto"), rs.getString("sig_acre_liq_ant_vto")).add(safeMultiply(rs.getString("imp_acre_liq_ant_facturado_30"), rs.getString("sig_acre_liq_ant_facturado_30")))
								.add(safeMultiply(rs.getString("imp_acre_liq_ant_facturado_60"), rs.getString("sig_acre_liq_ant_facturado_60"))).add(safeMultiply(rs.getString("imp_acre_liq_ant_facturado_90"), rs.getString("sig_acre_liq_ant_facturado_90")))
								.add(safeMultiply(rs.getString("imp_acre_liq_ant_facturado_120"), rs.getString("sig_acre_liq_ant_facturado_120"))).add(safeMultiply(rs.getString("imp_int_plan_esp_vto"), rs.getString("sig_int_plan_esp_vto")))
								.add(safeMultiply(rs.getString("imp_int_plan_esp_facturado_30"), rs.getString("sig_int_plan_esp_facturado_30"))).add(safeMultiply(rs.getString("imp_int_plan_esp_facturado_60"), rs.getString("sig_int_plan_esp_facturado_60")))
								.add(safeMultiply(rs.getString("imp_int_plan_esp_facturado_90"), rs.getString("sig_int_plan_esp_facturado_90"))).add(safeMultiply(rs.getString("imp_int_plan_esp_facturado_120"), rs.getString("sig_int_plan_esp_facturado_120")));
						if (expense.compareTo(new BigDecimal(0)) != 0) {
							MExpenseConcepts ec = new MExpenseConcepts(getCtx(), 0, get_TrxName());
							ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
							ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							ec.setAmount(expense);
							ec.save();
							expensesAmt = expensesAmt.add(expense); 
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					/* -- -- -- */
					settlement.setWithholding(withholdingAmt);
					settlement.setExpenses(expensesAmt);
					settlement.setIVAAmount(ivaAmt);
					settlement.setCommissionAmount(commissionAmt);
					if (!settlement.save(get_TrxName())) {
						return ERROR;
					} else {
						result = SAVED;
						markAsImported(tableName, rs.getInt(tableName + "_ID"));
					}
				}
			} else {
				result = IGNORED;
				log.log(Level.WARNING, "Número de comercio \"" + rs.getString("comercio") + "\" ignorado.");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean validateForAmex(ResultSet rs, String tableName, Map<String, X_C_ExternalServiceAttributes> attributes) {
		try {
			int id = getC_BPartner_ID(rs.getString("num_est"));
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "Ignorado: no se encontró Nro. de comercio en E.Financieras");
				return false;
			}
			
			String name = "Imp total desc aceleracion";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Imp total desc aceleracion");
				return false;
			}
			
			name = "Importe Descuento";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Importe Descuento");
				return false;
			}
			
			name = "IVA 21";
			id = getTaxIDByName(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto IVA 21");
				return false;
			}
			
			name = "Percepcion IVA";
			id = getTaxIDByName(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Percepcion IVA");
				return false;
			}
			
			name = "Retencion IVA";
			id = getRetencionSchemaIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Retencion IVA");
				return false;
			}
			
			name = "Retencion Ganancias";
			id = getRetencionSchemaIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Retencion Ganancias");
				return false;
			}
	
			//IIBB
			id = getRetencionSchemaByNroEst(rs.getString("num_est"));
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No existe retención de IIBB para la región configurada en la E.Financiera");
				return false;
			} 
			
			//IIBB Bs. As.
			id = getRetencionSchemaForBsAs();
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No existe retención de IIBB para la provincia de Bs. As. (Cod. 09)");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Crea y guarda una liquidación para importaciones de American Express.
	 * @param rs ResultSet con los datos extraídos de la
	 * tabla de importación correspondiente a American Express.
	 * @param tableName Nombre de la tabla de importación.
	 * @param attributes Atributos de configuracion.
	 * @return Mensaje de resultado de la importación.
	 */
	private String createFromAmex(ResultSet rs, String tableName, Map<String, X_C_ExternalServiceAttributes> attributes) {
		String result = null;
		try {
			int C_BPartner_ID = getC_BPartner_ID(rs.getString("num_est"));
			if (C_BPartner_ID > 0) {
				String settlementNo = rs.getString("num_sec_pago");
				int C_CreditCardSettlement_ID = getSettlementIdFromNroAndBPartner(settlementNo, C_BPartner_ID);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(rs.getString("fecha_pago"));

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
					settlement = new MCreditCardSettlement(getCtx(), C_CreditCardSettlement_ID, get_TrxName());
					withholdingAmt = settlement.getWithholding();
					perceptionAmt = settlement.getPerception();
					expensesAmt = settlement.getExpenses();
					ivaAmt = settlement.getIVAAmount();
					commissionAmt = settlement.getCommissionAmount();
				} else {
					settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());
					settlement.setGenerateChildrens(false);

					settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_AMEX);
					settlement.setC_BPartner_ID(C_BPartner_ID);
					settlement.setPaymentDate(date != null ? new Timestamp(date.getTime()) : null);
					settlement.setAmount(safeNumber(rs.getString("imp_bruto_est")));
					settlement.setNetAmount(amt);
					settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
					settlement.setSettlementNo(rs.getString("num_sec_pago"));

					if (!settlement.save()) {
						return ERROR;
					}
					/* -- -- -- */
					try {
						String name = "Imp total desc aceleracion";
						int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
						BigDecimal expense = negativeValue(safeNumber(rs.getString("imp_tot_desc_acel")));
						if (expense.compareTo(new BigDecimal(0)) != 0) {
							MExpenseConcepts ec = new MExpenseConcepts(getCtx(), 0, get_TrxName());
							ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
							ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID()); 
							ec.setAmount(expense);
							ec.save();
							expensesAmt = expensesAmt.add(expense);
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					/* -- -- -- */
					try {
						String name = "Importe Descuento";
						int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
						BigDecimal commission = negativeValue(safeNumber(rs.getString("imp_desc_pago")));
						if (commission.compareTo(new BigDecimal(0)) != 0) {
							MCommissionConcepts cc = new MCommissionConcepts(getCtx(), 0, get_TrxName());
							cc.setC_CardSettlementConcepts_ID(C_CardSettlementConcepts_ID);
							cc.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							cc.setAmount(commission);
							cc.save();
							commissionAmt = commissionAmt.add(commission);
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				}
				
				
				/* -- -- -- */
				try {
					if (codImp.equals("01")) {
						String name = "IVA 21";
						int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
						BigDecimal iva = negativeValue(safeNumber(rs.getString("importe_imp")));
						if (iva.compareTo(new BigDecimal(0)) != 0) {
							MIVASettlements ps = new MIVASettlements(getCtx(), 0, get_TrxName());
							ps.setC_Tax_ID(C_Tax_ID);
							ps.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							ps.setAmount(iva);
							ps.save();
							ivaAmt = ivaAmt.add(iva);
						}
					}
					if (codImp.equals("02")) {
						String name = "Percepcion IVA";
						int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
						BigDecimal perception = negativeValue(safeNumber(rs.getString("importe_imp")));
						if (perception.compareTo(new BigDecimal(0)) != 0) {
							MPerceptionsSettlement ps = new MPerceptionsSettlement(getCtx(), 0, get_TrxName());
							ps.setC_Tax_ID(C_Tax_ID);
							ps.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							ps.setAmount(perception);
							ps.save();
							perceptionAmt = perceptionAmt.add(perception);
						}
					}
					if (codImp.equals("04")) {
						String name = "Retencion IVA";
						int C_RetencionSchema_ID = getRetencionSchemaIDByValue(attributes.get(name).getName());
						BigDecimal withholding = negativeValue(safeNumber(rs.getString("importe_imp")));
						if (withholding.compareTo(new BigDecimal(0)) != 0) {
							MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
							ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
							ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							ws.setAmount(withholding);
							ws.save();
							withholdingAmt = withholdingAmt.add(withholding);
						}
					}
					if (codImp.equals("05")) {
						String name = "Retencion Ganancias";
						int C_RetencionSchema_ID = getRetencionSchemaIDByValue(attributes.get(name).getName());
						BigDecimal withholding = negativeValue(safeNumber(rs.getString("importe_imp")));
						if (withholding.compareTo(new BigDecimal(0)) != 0) {
							MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
							ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
							ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
							ws.setAmount(withholding);
							ws.save();
							withholdingAmt = withholdingAmt.add(withholding);
						}
					}
					//Si es código 06, busco la región configurada en la E. Financiera y
					//recupero, si existe, un esquema de retención de IIBB para la región. 
					if (codImp.equals("06")) { // IIBB 
						int C_RetencionSchema_ID = getRetencionSchemaByNroEst(rs.getString("num_est"));
						
						if (C_RetencionSchema_ID > 0) {
							MRetencionSchema retSchema = new MRetencionSchema(getCtx(), C_RetencionSchema_ID, get_TrxName()); 
							BigDecimal withholding = negativeValue(safeNumber(rs.getString("importe_imp")));
							if (withholding.compareTo(new BigDecimal(0)) != 0) {
								MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
								ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
								ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
								ws.setC_Region_ID(retSchema.getC_Region_ID());
								ws.setAmount(withholding);
								ws.save();
								withholdingAmt = withholdingAmt.add(withholding);
							}
						}
					}
					//Si es código 09 recupero, si existe, un esquema de retención de IIBB para la Bs. As.. 
					if (codImp.equals("09")) { // IIBB o IIBB Bs.As.
						int C_RetencionSchema_ID = getRetencionSchemaForBsAs();
						
						if (C_RetencionSchema_ID > 0) {
							MRetencionSchema retSch= new MRetencionSchema(getCtx(), C_RetencionSchema_ID, get_TrxName()); 
							BigDecimal withholding = negativeValue(safeNumber(rs.getString("importe_imp")));
							if (withholding.compareTo(new BigDecimal(0)) != 0) {
								MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
								ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
								ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
								ws.setC_Region_ID(retSch.getC_Region_ID());
								ws.setAmount(withholding);
								ws.save();
								withholdingAmt = withholdingAmt.add(withholding);
							}
						}
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				settlement.setWithholding(withholdingAmt);
				settlement.setPerception(perceptionAmt);
				settlement.setExpenses(expensesAmt);
				settlement.setIVAAmount(ivaAmt);
				settlement.setCommissionAmount(commissionAmt);
				if (!settlement.save(get_TrxName())) {
					return ERROR;
				} else {
					result = SAVED;
					markAsImported(tableName, rs.getInt(tableName + "_ID"));
				}
			} else {
				result = IGNORED;
				log.log(Level.WARNING, "Número de comercio \"" + rs.getString("num_est") + "\" ignorado.");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean validateForVisa(ResultSet rs, String tableName, Map<String, X_C_ExternalServiceAttributes> attributes) {
		try {
			int id = getC_BPartner_ID(rs.getString("num_est"));
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "Ignorado: no se encontró Nro. de comercio en E.Financieras");
				return false;
			}
			
			//IIBB
			id = getRetencionSchemaByNroEst(rs.getString("num_est"));
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No existe retención de IIBB para la región configurada en la E.Financiera");
				return false;
			} 
			
			String name = "Ret IVA";
			id = getRetencionSchemaIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Ret IVA");
				return false;
			}
			
			name = "Ret Ganancias";
			id = getRetencionSchemaIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Ret Ganancias");
				return false;
			}
			
			name = "Dto por ventas de campañas";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Dto por ventas de campañas");
				return false;
			}
			name = "Costo plan acelerado cuotas";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Costo plan acelerado cuotas");
				return false;
			}
			name = "Cargo adic por planes cuotas";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Cargo adic por planes cuotas");
				return false;
			}
			name = "Importe Arancel";
			id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto Importe Arancel");
				return false;
			}
			name = "IVA 10.5";
			id = getTaxIDByName(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto IVA 10.5");
				return false;
			}
			name = "IVA 21";
			id = getTaxIDByName(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto IVA 21");
				return false;
			}
			name = "IVA";
			id = getTaxIDByName(attributes.get(name).getName());
			if (id <= 0) {
				markAsError(tableName, rs.getInt(tableName + "_ID"), "No se encontró el concepto IVA");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Crea y guarda una liquidación para importaciones de Visa.
	 * @param rs ResultSet con los datos extraídos de la
	 * tabla de importación correspondiente a Visa.
	 * @param tableName Nombre de la tabla de importación.
	 * @param attributes Atributos de configuracion.
	 * @return Mensaje de resultado de la importación.
	 */
	private String createFromVisa(ResultSet rs, String tableName, Map<String, X_C_ExternalServiceAttributes> attributes) {
		String result = null;
		try {
			int C_BPartner_ID = getC_BPartner_ID(rs.getString("num_est"));
			if (C_BPartner_ID > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date date = sdf.parse(rs.getString("fpag"));

				BigDecimal netAmt = safeMultiply(rs.getString("impneto"), rs.getString("signo_3"));
				BigDecimal amt = safeMultiply(rs.getString("impbruto"), rs.getString("signo_1"));

				//Acumuladores para totales de impuestos, tasas, etc.
				BigDecimal withholdingAmt = new BigDecimal(0);
				BigDecimal perceptionAmt = new BigDecimal(0);
				BigDecimal expensesAmt = new BigDecimal(0);
				BigDecimal ivaAmt = new BigDecimal(0);
				BigDecimal commissionAmt = new BigDecimal(0);
				
				MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());
				settlement.setGenerateChildrens(false);

				settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_VISA);
				settlement.setC_BPartner_ID(C_BPartner_ID);
				settlement.setPaymentDate(new Timestamp(date.getTime()));
				settlement.setAmount(amt);
				settlement.setNetAmount(netAmt);
				settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
				settlement.setSettlementNo(rs.getString("nroliq"));

				if (!settlement.save()) {
					return ERROR;
				} 
				
				/* -- -- -- */
				try {
					int C_RetencionSchema_ID = getRetencionSchemaByNroEst(rs.getString("num_est"));
					BigDecimal withholding = safeMultiply(rs.getString("ret_ingbru"), rs.getString("signo_32"));
					if (withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID > 0) {
						MRetencionSchema retSchema = new MRetencionSchema(getCtx(), C_RetencionSchema_ID, get_TrxName());
						MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
						ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
						ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ws.setC_Region_ID(retSchema.getC_Region_ID());
						ws.setAmount(withholding);
						ws.save();
						withholdingAmt = withholdingAmt.add(withholding);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Ret IVA";
					int C_RetencionSchema_ID = getRetencionSchemaIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_iva"), rs.getString("signo_30"));
					if (withholding.compareTo(new BigDecimal(0)) != 0) {
						MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
						ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
						ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ws.setAmount(withholding);
						ws.save();
						withholdingAmt = withholdingAmt.add(withholding);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Ret Ganancias";
					int C_RetencionSchema_ID = getRetencionSchemaIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_gcias"), rs.getString("signo_31"));
					if (withholding.compareTo(new BigDecimal(0)) != 0) {
						MWithholdingSettlement ws = new MWithholdingSettlement(getCtx(), 0, get_TrxName());
						ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
						ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ws.setAmount(withholding);
						ws.save();
						withholdingAmt = withholdingAmt.add(withholding);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Dto por ventas de campañas";
					int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("dto_campania"), rs.getString("signo_04_3"));
					if (expense.compareTo(new BigDecimal(0)) != 0) {
						MExpenseConcepts ec = new MExpenseConcepts(getCtx(), 0, get_TrxName());
						ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
						ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ec.setAmount(expense);
						ec.save();
						expensesAmt = expensesAmt.add(expense);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Costo plan acelerado cuotas";
					int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("costo_cuoemi"), rs.getString("signo_12"));
					if (expense.compareTo(new BigDecimal(0)) != 0) {
						MExpenseConcepts ec = new MExpenseConcepts(getCtx(), 0, get_TrxName());
						ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
						ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ec.setAmount(expense);
						ec.save();
						expensesAmt = expensesAmt.add(expense);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Cargo adic por planes cuotas";
					int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("adic_plancuo"), rs.getString("signo_04_15"));
					if (expense.compareTo(new BigDecimal(0)) != 0) {
						MExpenseConcepts ec = new MExpenseConcepts(getCtx(), 0, get_TrxName());
						ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
						ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ec.setAmount(expense);
						ec.save();
						expensesAmt = expensesAmt.add(expense);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Cargo adic por op internacionales";
					int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("adic_opinter"), rs.getString("signo_04_17"));
					if (expense.compareTo(new BigDecimal(0)) != 0) {
						MExpenseConcepts ec = new MExpenseConcepts(getCtx(), 0, get_TrxName());
						ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
						ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ec.setAmount(expense);
						ec.save();
						expensesAmt = expensesAmt.add(expense);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Importe Arancel";
					int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal commission = safeMultiply(rs.getString("impret"), rs.getString("signo_2"));
					if (commission.compareTo(new BigDecimal(0)) != 0) {
						MCommissionConcepts cc = new MCommissionConcepts(getCtx(), 0, get_TrxName());
						cc.setC_CardSettlementConcepts_ID(C_CardSettlementConcepts_ID);
						cc.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						cc.setAmount(commission);
						cc.save();
						commissionAmt = commissionAmt.add(commission);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA 10.5";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal iva = safeMultiply(rs.getString("retiva_cuo1"), rs.getString("signo_13"));
					if (iva.compareTo(new BigDecimal(0)) != 0) {
						MIVASettlements iv = new MIVASettlements(getCtx(), 0, get_TrxName()); 
						iv.setC_Tax_ID(C_Tax_ID);
						iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						iv.setAmount(iva);
						iv.save();
						ivaAmt = ivaAmt.add(iva);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA 21";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal iva = safeMultiply(rs.getString("retiva_d1"), rs.getString("signo_7")).add(safeMultiply(rs.getString("iva1_ad_plancuo"), rs.getString("signo_04_16")))
							.add(safeMultiply(rs.getString("iva1_ad_opinter"), rs.getString("signo_04_18")));
					if (iva.compareTo(new BigDecimal(0)) != 0) {
						MIVASettlements iv = new MIVASettlements(getCtx(), 0, get_TrxName()); 
						iv.setC_Tax_ID(C_Tax_ID);
						iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						iv.setAmount(iva);
						iv.save();
						ivaAmt = ivaAmt.add(iva);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal perception = safeMultiply(rs.getString("retiva_esp"), rs.getString("signo_5"));
					if (perception.compareTo(new BigDecimal(0)) != 0) {
						MPerceptionsSettlement ps = new MPerceptionsSettlement(getCtx(), 0, get_TrxName());
						ps.setC_Tax_ID(C_Tax_ID);
						ps.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
						ps.setAmount(perception);
						ps.save();
						perceptionAmt = perceptionAmt.add(perception);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				settlement.setWithholding(withholdingAmt);
				settlement.setPerception(perceptionAmt);
				settlement.setExpenses(expensesAmt);
				settlement.setIVAAmount(ivaAmt);
				settlement.setCommissionAmount(commissionAmt);
				if (!settlement.save(get_TrxName())) {
					return ERROR;
				} else {
					result = SAVED;
					markAsImported(tableName, rs.getInt(tableName + "_ID"));
				}
			} else {
				result = IGNORED;
				log.log(Level.WARNING, "Número de comercio \"" + rs.getString("num_est") + "\" ignorado.");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/**
	 * Realiza una multiplicación entre 2 números, o un número y un signo.
	 * @param op1 Necesariamente debe ser un número, si se desea multiplicar
	 * con un símbolo, el mismo debe ir en el op2.
	 * @param op2 Número, símbolo (+, -), o palabra (Negativo, Positivo)
	 * @return BigDecimal correspondiente. Si alguno de los operadores es
	 * nulo, o vacío, o el op1 no es un BigDecimal válido, retorna 0 (cero).
	 */
	private BigDecimal safeMultiply(String op1, String op2) {
		if (op1 == null || op2 == null || op1.trim().isEmpty() || op2.trim().isEmpty()) {
			return BigDecimal.ZERO;
		}
		BigDecimal num1 = null;
		try {
			num1 = new BigDecimal(op1);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
		String[] negatives = new String[] { "-", "Negativo" };
		for (String s : negatives) {
			if (op2.equalsIgnoreCase(s)) {
				return negativeValue(num1);
			}
		}
		
		String[] positives = new String[] { "+", "Positivo" };
		for (String s : positives) {
			if (op2.equalsIgnoreCase(s)) {
				return num1;
			}
		}
		
		BigDecimal num2 = null;
		try {
			num2 = new BigDecimal(op2);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
		return num1.multiply(num2);
	}

	/**
	 * A partir de un String, intenta generar un BigDecimal.
	 * @param number String a ser convertido.
	 * @return BigDecimal, en caso de que el String sea nulo,
	 * vacío, o inválido, devolverá 0.
	 */
	private BigDecimal safeNumber(String number) {
		if (number == null || number.trim().isEmpty()) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(number);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * Retorna el valor negado de un BigDecimal.
	 * @param input número de entrada a negar.
	 * @return Valor negado.
	 */
	private BigDecimal negativeValue(BigDecimal input) {
		if (input == null || input.signum() == 0) {
			return BigDecimal.ZERO;
		}
		if (input.signum() == -1) {
			return input.negate();
		}
		return input;
	}

	/**
	 * Marca un registro de la tabla de importación correspondiente, como importado.
	 * @param tableName Nombre de la tabla sobre la cual se realizará una actualización.
	 * @param id Clave primaria del registro a actualizar.
	 */
	private void markAsImported(String tableName, int id) {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + tableName + " ");
		sql.append("SET ");
		sql.append("	i_isimported = 'Y' ");
		sql.append("WHERE ");
		sql.append("	" + tableName + "_ID = " + id + " ");

		DB.executeUpdate(sql.toString(), get_TrxName());
	}
	
	/**
	 * Marca un registro de la tabla de importación correspondiente, como con error.
	 * @param tableName Nombre de la tabla sobre la cual se realizará una actualización.
	 * @param id Clave primaria del registro a actualizar.
	 */
	private void markAsError(String tableName, int id, String errorMsg) {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + tableName + " ");
		sql.append("SET ");
		sql.append("	i_errormsg = '" + errorMsg + "' ");
		sql.append("WHERE ");
		sql.append("	" + tableName + "_ID = " + id + " ");

		DB.executeUpdate(sql.toString(), get_TrxName());
	}

	/**
	 * Obtiene un objeto de configuración de servicios externos.
	 * @param name Nombre por el cual buscar el registro.
	 * @return ID del registro.
	 */
	private int getExternalServiceByName(String name) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_ExternalService_ID ");
		sql.append("FROM ");
		sql.append("	" + MExternalService.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	name = ? ");

		return DB.getSQLValue(get_TrxName(), sql.toString(), name);
	}

	private int getCardSettlementConceptIDByValue(String value) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_CardSettlementConcepts_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_CardSettlementConcepts.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	value = ? ");
		sql.append("	AND AD_Client_ID = ? ");

		return DB.getSQLValueEx(get_TrxName(), sql.toString(), value, Env.getAD_Client_ID(getCtx()));
	}

	private int getTaxIDByName(String name) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_Tax_ID ");
		sql.append("FROM ");
		sql.append("	" + MTax.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	name = ? ");
		sql.append("	AND AD_Client_ID = ? ");

		return DB.getSQLValueEx(get_TrxName(), sql.toString(), name, Env.getAD_Client_ID(getCtx()));
	}
	
	private int getRetencionSchemaIDByValue(String value) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_RetencionSchema_ID ");
		sql.append("FROM ");
		sql.append("	" + MRetencionSchema.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	value = ? ");
		sql.append("	AND AD_Client_ID = ? ");

		return DB.getSQLValueEx(get_TrxName(), sql.toString(), value, Env.getAD_Client_ID(getCtx()));
	}
	
	private int getRetencionSchemaByNroEst(String nroEst) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	rs.C_RetencionSchema_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("	INNER JOIN " + MEntidadFinanciera.Table_Name + " ef ");
		sql.append("		ON ef.C_Region_ID = rs.C_Region_ID ");
		sql.append("	INNER JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("		ON rs.C_RetencionType_ID = rs.C_RetencionType_ID ");
		sql.append("WHERE ");
		sql.append("	rs.RetencionApplication = 'S' ");
		sql.append("	AND rt.RetentionType = 'B' ");
		sql.append("	AND rt.IsActive = 'Y' ");
		sql.append("	AND rs.IsActive = 'Y' ");
		sql.append("	AND ef.EstablishmentNumber = ? ");

		PreparedStatement ps = null;
		ResultSet rst = null;

		int C_RetencionSchema_ID = -1;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setString(1, nroEst);
			rst = ps.executeQuery();

			if (rst.next()) {
				C_RetencionSchema_ID = rst.getInt(1);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "createFromAmex", e);
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		
		return C_RetencionSchema_ID;
	}
	
	/**
	 * Obtiene una liquidación a partir del número de liquidación y la 
	 * E.Comercial asociada.
	 * @param nro_liq Nombre por el cual buscar el registro.
	 * @param C_BPartner_ID ID Entidad Comercial.
	 * @return ID Liquidación o -1 si no existe.
	 */
	private int getSettlementIdFromNroAndBPartner(String nro_liq, int C_BPartner_ID) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + MCreditCardSettlement.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_bpartner_id = ? ");
		sql.append("	AND settlementno = ? ");

		return DB.getSQLValue(get_TrxName(), sql.toString(), C_BPartner_ID, nro_liq);
	}
	
	private int getRetencionSchemaForBsAs() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	rs.C_RetencionSchema_ID, ");
		sql.append("	rs.C_Region_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("	INNER JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("		ON rs.C_RetencionType_ID = rs.C_RetencionType_ID ");
		sql.append("	INNER JOIN " + X_C_Region.Table_Name + " rg ");
		sql.append("		ON rs.C_Region_ID = rg.C_Region_ID ");
		sql.append("WHERE ");
		sql.append("	rs.RetencionApplication = 'S' ");
		sql.append("	AND rt.RetentionType = 'B' ");
		sql.append("	AND rt.IsActive = 'Y' ");
		sql.append("	AND rs.IsActive = 'Y' ");
		sql.append("	AND rs.IsActive = 'Y' ");
		sql.append("	AND rg.jurisdictioncode = 902 ");

		PreparedStatement ps = null;
		ResultSet rst = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if (rst.next()) {
				return rst.getInt(1);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "createFromAmex", e);
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return -1;
	}

}

