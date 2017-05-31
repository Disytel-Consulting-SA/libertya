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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.openXpertya.model.MCommissionConcepts;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MExpenseConcepts;
import org.openXpertya.model.MExternalService;
import org.openXpertya.model.MIVASettlements;
import org.openXpertya.model.MPerceptionsSettlement;
import org.openXpertya.model.MTax;
import org.openXpertya.model.MWithholdingSettlement;
import org.openXpertya.model.X_C_CardSettlementConcepts;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
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
	/** Lista de mensajes de error. */
	private HashSet<String> errMessages;

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

				errMessages = validateForFirstData(attributes);

				if (!errMessages.isEmpty()) {
					Object[] params = new Object[] { "FirstData", 0, 0, errMessages.size() };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
				while (rs.next()) {
					someResults = true;
					resultMsg = createFromFirstData(rs, importClass.getTableName(), attributes);
					result.put(resultMsg, result.get(resultMsg) + 1);
					Object[] params = new Object[] { "FirstData", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
			} // Importacion elegida = Naranja
			else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_NARANJA)) {

				int C_ExternalService_ID = getExternalServiceByName(Import.EXTERNAL_SERVICE_NARANJA);
				MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
				Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();

				errMessages = validateForNaranja(attributes);

				if (!errMessages.isEmpty()) {
					Object[] params = new Object[] { "Naranja", 0, 0, errMessages.size() };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
				while (rs.next()) {
					someResults = true;
					resultMsg = createFromNaranja(rs, importClass.getTableName(), attributes);
					result.put(resultMsg, result.get(resultMsg) + 1);
					Object[] params = new Object[] { "Naranja", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
			} // Importacion elegida = American Express
			else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_AMEX)) {

				int C_ExternalService_ID = getExternalServiceByName(Import.EXTERNAL_SERVICE_AMEX);
				MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
				Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();

				errMessages = validateForAmex(attributes, rs);

				if (!errMessages.isEmpty()) {
					Object[] params = new Object[] { "Amex", 0, 0, errMessages.size() };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
				while (rs.next()) {
					someResults = true;
					resultMsg = createFromAmex(rs, importClass.getTableName(), attributes);
					result.put(resultMsg, result.get(resultMsg) + 1);
					Object[] params = new Object[] { "Amex", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
			} // Importacion elegida = Visa
			else if (p_CreditCardType.equals(MCreditCardSettlement.CREDITCARDTYPE_VISA)) {

				int C_ExternalService_ID = getExternalServiceByName(Import.EXTERNAL_SERVICE_VISA);
				MExternalService externalService = new MExternalService(getCtx(), C_ExternalService_ID, get_TrxName());
				Map<String, X_C_ExternalServiceAttributes> attributes = externalService.getAttributesMap();

				errMessages = validateForVisa(attributes);

				if (!errMessages.isEmpty()) {
					Object[] params = new Object[] { "Visa", 0, 0, errMessages.size() };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
				}
				while (rs.next()) {
					someResults = true;
					resultMsg = createFromVisa(rs, importClass.getTableName(), attributes);
					result.put(resultMsg, result.get(resultMsg) + 1);
					Object[] params = new Object[] { "Visa", result.get(SAVED), result.get(IGNORED), result.get(ERROR) };
					resultMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "SettlementGenerationResult", params);
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

	private String defaultErr(String name) {
		return "No se ha encontrado referencia para \"" + name + "\".";
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private HashSet<String> validateForFirstData(Map<String, X_C_ExternalServiceAttributes> attributes) {
		HashSet<String> errMsg = new HashSet<String>();

		String name = "Arancel";
		int id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "IVA 21";
		id = getTaxIDByName(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Costo Financiero";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Percepcion IVA";
		id = getTaxIDByName(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Ret Ganancias e Ing Brutos";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		return errMsg;
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
				sdf = new SimpleDateFormat("dd/MM/yyyy");
				String dateStr = "";
				if (date != null) {
					dateStr = sdf.format(date);
				}
				BigDecimal amt = safeMultiply(rs.getString("total_importe_total"), rs.getString("total_importe_total_signo"));

				MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

				settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_FIRSTDATA);
				settlement.setC_BPartner_ID(C_BPartner_ID);
				settlement.setPaymentDate(new Timestamp(date.getTime()));
				settlement.setPayment(rs.getString("numero_liquidacion"), defaultCurrency.getCurSymbol(), amt.toString(), dateStr, dateStr);
				settlement.setAmount(amt);
				settlement.setNetAmount(safeMultiply(rs.getString("neto_comercios"), rs.getString("neto_comercios_signo")));
				settlement.setCouponsTotalAmount(safeMultiply(rs.getString("total_importe_total"), rs.getString("total_importe_total_signo")));
				settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
				settlement.setSettlementNo(rs.getString("numero_liquidacion"));

				if (!settlement.save()) {
					return ERROR;
				} else {
					result = SAVED;
					markAsImported(tableName, rs.getInt(tableName + "_ID"));
				}
				/* -- -- -- */
				try {
					String name = "Arancel";
					int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("arancel"), rs.getString("arancel_signo"));
					MExpenseConcepts ec = getExpenseConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_CardSettlementConcept_ID);
					ec.setAmount(expense);
					ec.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA 21";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal iva = safeMultiply(rs.getString("iva_aranceles_ri"), rs.getString("iva_aranceles_ri_signo")).add(safeMultiply(rs.getString("iva_dto_pago_anticipado"), rs.getString("iva_dto_pago_anticipado_signo")));
					MIVASettlements iv = getIVASettlementsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Tax_ID);
					iv.setAmount(iva);
					iv.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Costo Financiero";
					int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal commission = safeMultiply(rs.getString("costo_financiero"), rs.getString("costo_financiero_signo"));
					MCommissionConcepts cc = getCommissionConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_CardSettlementConcepts_ID);
					cc.setAmount(commission);
					cc.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Percepcion IVA";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal perception = safeMultiply(rs.getString("percepc_iva_r3337"), rs.getString("percepc_iva_r3337_signo"));
					MPerceptionsSettlement ps = getPerceptionsSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Tax_ID);
					ps.setAmount(perception);
					ps.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Ret Ganancias e Ing Brutos";
					int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_imp_ganancias"), rs.getString("ret_imp_ganancias_signo")).add(safeMultiply(rs.getString("ret_imp_ingresos_brutos"), rs.getString("ret_imp_ingresos_brutos_signo")))
							.add(safeMultiply(rs.getString("ret_iva_ventas"), rs.getString("ret_iva_ventas_signo")));
					MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
					ws.setAmount(withholding);
					ws.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
			} else {
				result = IGNORED;
				log.log(Level.WARNING, "Número de comercio \"" + rs.getString("comercio_participante") + "\" ignorado.");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private HashSet<String> validateForNaranja(Map<String, X_C_ExternalServiceAttributes> attributes) {
		HashSet<String> errMsg = new HashSet<String>();

		String name = "Retencion IIBB";
		int id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Retencion IVA";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Retencion Ganancias";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Comisiones - Conceptos fact a descontar mes pago";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "IVA 21";
		id = getTaxIDByName(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Gastos - Conceptos fact a descontar mes pago";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		return errMsg;
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(rs.getString("fecha_pago"));
				sdf = new SimpleDateFormat("dd/MM/yyyy");
				String dateStr = sdf.format(date);

				BigDecimal amt = safeMultiply(rs.getString("neto"), rs.getString("signo_neto"));

				String settlementNo = rs.getString("nro_liquidacion");
				if (settlementNo == null || settlementNo.equals("null")) {
					settlementNo = "";
				}
				MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

				settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_NARANJA);
				settlement.setC_BPartner_ID(C_BPartner_ID);
				settlement.setPaymentDate(new Timestamp(date.getTime()));
				settlement.setPayment(settlementNo, defaultCurrency.getCurSymbol(), amt.toString(), dateStr, dateStr);
				settlement.setAmount(amt);
				settlement.setNetAmount(amt);
				settlement.setCouponsTotalAmount(amt);
				settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
				settlement.setSettlementNo(settlementNo);

				if (!settlement.save()) {
					return ERROR;
				} else {
					result = SAVED;
					markAsImported(tableName, rs.getInt(tableName + "_ID"));
				}
				/* -- -- -- */
				try {
					String name = "Retencion IIBB";
					int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_ingresos_brutos"), rs.getString("signo_ret_ing_brutos"));
					MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
					ws.setAmount(withholding);
					ws.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Retencion IVA";
					int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("retencion_iva_140"), rs.getString("signo_ret_iva_140"));
					MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
					ws.setAmount(withholding);
					ws.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Retencion Ganancias";
					int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("retencion_ganancias"), rs.getString("signo_ret_ganancias"));
					MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
					ws.setAmount(withholding);
					ws.save();
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
					MCommissionConcepts cc = getCommissionConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_CardSettlementConcepts_ID);
					cc.setAmount(commission);
					cc.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA 21";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal iva = safeMultiply(rs.getString("imp_iva_21_vto"), rs.getString("sig_iva_21_vto")).add(safeMultiply(rs.getString("imp_iva_21_facturado_30"), rs.getString("imp_iva_21_facturado_30")))
							.add(safeMultiply(rs.getString("imp_iva_21_facturado_60"), rs.getString("imp_iva_21_facturado_60"))).add(safeMultiply(rs.getString("imp_iva_21_facturado_90"), rs.getString("imp_iva_21_facturado_90")))
							.add(safeMultiply(rs.getString("imp_iva_21_facturado_120"), rs.getString("imp_iva_21_facturado_120")));
					MIVASettlements iv = getIVASettlementsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Tax_ID);
					iv.setAmount(iva);
					iv.save();
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
					MExpenseConcepts ec = getExpenseConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_CardSettlementConcept_ID);
					ec.setAmount(expense);
					ec.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
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

	private HashSet<String> validateForAmex(Map<String, X_C_ExternalServiceAttributes> attributes, ResultSet rs) {
		HashSet<String> errMsg = new HashSet<String>();

		String name = "Imp total desc aceleracion";
		int id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Importe Descuento";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "IVA 21";
		id = getTaxIDByName(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Percepcion IVA";
		id = getTaxIDByName(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Retencion IVA";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Retencion Ganancias";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}

		Set<String> en = new HashSet<String>();
		try {
			while (rs.next()) {
				en.add(rs.getString("num_est"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		String ens = "";
		for (String s : en) {
			ens += s + ",";
		}
		ens = ens.substring(ens.length() - 1);

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	rt.C_RetencionType_ID, ");
		sql.append("	ef.C_Region_ID ");
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
		sql.append("	AND ef.EstablishmentNumber IN (?) ");

		PreparedStatement ps = null;
		ResultSet rst = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setString(1, ens);
			rst = ps.executeQuery();

			if (!rst.next()) {
				errMsg.add(defaultErr("Tipo de Retención"));
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
		return errMsg;
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(rs.getString("fecha_pago"));
				sdf = new SimpleDateFormat("dd/MM/yyyy");
				String dateStr = sdf.format(date);

				BigDecimal amt = safeNumber(rs.getString("imp_neto_ajuste"));

				String codImp = rs.getString("cod_imp");
				if (codImp == null) {
					codImp = "";
				}

				MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

				settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_AMEX);
				settlement.setC_BPartner_ID(C_BPartner_ID);
				settlement.setPaymentDate(date != null ? new Timestamp(date.getTime()) : null);
				settlement.setPayment(rs.getString("num_sec_pago"), defaultCurrency.getCurSymbol(), amt.toString(), dateStr, dateStr);
				settlement.setAmount(safeNumber(rs.getString("imp_bruto_est")));
				settlement.setNetAmount(amt);
				settlement.setCouponsTotalAmount(amt);
				settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
				settlement.setSettlementNo(rs.getString("num_sec_pago"));

				if (!settlement.save()) {
					return ERROR;
				} else {
					result = SAVED;
					markAsImported(tableName, rs.getInt(tableName + "_ID"));
				}
				/* -- -- -- */
				try {
					String name = "Imp total desc aceleracion";
					int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeNumber(rs.getString("imp_tot_desc_acel"));
					MExpenseConcepts ec = getExpenseConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_CardSettlementConcepts_ID);
					ec.setAmount(expense);
					ec.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Importe Descuento";
					int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal commission = safeNumber(rs.getString("imp_desc_pago"));
					MCommissionConcepts cc = getCommissionConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_CardSettlementConcepts_ID);
					cc.setAmount(commission);
					cc.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					if (codImp.equals("01")) {
						String name = "IVA 21";
						int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
						BigDecimal perception = safeNumber(rs.getString("importe_imp"));
						MPerceptionsSettlement ps = getPerceptionsSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Tax_ID);
						ps.setAmount(perception);
						ps.save();
					}
					if (codImp.equals("02")) {
						String name = "Percepcion IVA";
						int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
						BigDecimal perception = safeNumber(rs.getString("importe_imp"));
						MPerceptionsSettlement ps = getPerceptionsSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Tax_ID);
						ps.setAmount(perception);
						ps.save();
					}
					if (codImp.equals("04")) {
						String name = "Retencion IVA";
						int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
						BigDecimal withholding = safeNumber(rs.getString("importe_imp"));
						MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
						ws.setAmount(withholding);
						ws.save();
					}
					if (codImp.equals("05")) {
						String name = "Retencion Ganancias";
						int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
						BigDecimal withholding = safeNumber(rs.getString("importe_imp"));
						MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
						ws.setAmount(withholding);
						ws.save();
					}
					if (codImp.equals("06") || codImp.equals("09")) { // IIBB o IIBB Bs.As.
						StringBuffer sql = new StringBuffer();

						sql.append("SELECT ");
						sql.append("	rt.C_RetencionType_ID, ");
						sql.append("	ef.C_Region_ID ");
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
						sql.append("	AND ef.EstablishmentNumber = ? ");

						PreparedStatement ps = null;
						ResultSet rst = null;

						int C_RetencionType_ID = -1;
						int C_Region_ID = -1;

						try {
							ps = DB.prepareStatement(sql.toString());
							ps.setString(1, rs.getString("num_est"));
							rst = ps.executeQuery();

							if (rst.next()) {
								C_RetencionType_ID = rst.getInt(1);
								C_Region_ID = rst.getInt(2);
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
						BigDecimal withholding = safeNumber(rs.getString("importe_imp"));
						MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
						ws.setC_Region_ID(C_Region_ID);
						ws.setAmount(withholding);
						ws.save();
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
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

	private HashSet<String> validateForVisa(Map<String, X_C_ExternalServiceAttributes> attributes) {
		HashSet<String> errMsg = new HashSet<String>();

		String name = "Ret IIBB";
		int id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Ret IVA";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Ret Ganancias";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Dto por ventas de campañas";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Costo plan acelerado cuotas";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Cargo adic por planes cuotas";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "Importe Arancel";
		id = getCardSettlementConceptIDByValue(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "IVA 10.5";
		id = getTaxIDByName(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "IVA 21";
		id = getTaxIDByName(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		name = "IVA";
		id = getTaxIDByName(attributes.get(name).getName());
		if (id <= 0) {
			errMsg.add(defaultErr(name));
		}
		return errMsg;
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
				String dateStr = sdf.format(date);

				BigDecimal netAmt = safeMultiply(rs.getString("impneto"), rs.getString("signo_3"));
				BigDecimal amt = safeMultiply(rs.getString("impbruto"), rs.getString("signo_1"));

				MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

				settlement.setCreditCardType(MCreditCardSettlement.CREDITCARDTYPE_VISA);
				settlement.setC_BPartner_ID(C_BPartner_ID);
				settlement.setPaymentDate(new Timestamp(date.getTime()));
				settlement.setPayment(rs.getString("nroliq"), defaultCurrency.getCurSymbol(), amt.toString(), dateStr, dateStr);
				settlement.setAmount(amt);
				settlement.setNetAmount(netAmt);
				settlement.setCouponsTotalAmount(netAmt);
				settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
				settlement.setSettlementNo(rs.getString("nroliq"));

				if (!settlement.save()) {
					return ERROR;
				} else {
					result = SAVED;
					markAsImported(tableName, rs.getInt(tableName + "_ID"));
				}
				/* -- -- -- */
				try {
					String name = "Ret IIBB";
					int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_ingbru"), rs.getString("signo_32"));
					MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
					ws.setAmount(withholding);
					ws.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Ret IVA";
					int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_iva"), rs.getString("signo_30"));
					MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
					ws.setAmount(withholding);
					ws.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Ret Ganancias";
					int C_RetencionType_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal withholding = safeMultiply(rs.getString("ret_gcias"), rs.getString("signo_31"));
					MWithholdingSettlement ws = getWithholdingSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_RetencionType_ID);
					ws.setAmount(withholding);
					ws.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Dto por ventas de campañas";
					int C_Cardsettlementconcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("dto_campania"), rs.getString("signo_04_3"));
					MExpenseConcepts ec = getExpenseConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Cardsettlementconcepts_ID);
					ec.setAmount(expense);
					ec.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Costo plan acelerado cuotas";
					int C_Cardsettlementconcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("costo_cuoemi"), rs.getString("signo_12"));
					MExpenseConcepts ec = getExpenseConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Cardsettlementconcepts_ID);
					ec.setAmount(expense);
					ec.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Cargo adic por planes cuotas";
					int C_Cardsettlementconcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("adic_plancuo"), rs.getString("signo_04_15"));
					MExpenseConcepts ec = getExpenseConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Cardsettlementconcepts_ID);
					ec.setAmount(expense);
					ec.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Cargo adic por op internacionales";
					int C_Cardsettlementconcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal expense = safeMultiply(rs.getString("adic_opinter"), rs.getString("signo_04_17"));
					MExpenseConcepts ec = getExpenseConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Cardsettlementconcepts_ID);
					ec.setAmount(expense);
					ec.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "Importe Arancel";
					int C_Cardsettlementconcepts_ID = getCardSettlementConceptIDByValue(attributes.get(name).getName());
					BigDecimal commission = safeMultiply(rs.getString("impret"), rs.getString("signo_2"));
					MCommissionConcepts cc = getCommissionConceptsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Cardsettlementconcepts_ID);
					cc.setAmount(commission);
					cc.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA 10.5";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal iva = safeMultiply(rs.getString("retiva_cuo1"), rs.getString("signo_13"));
					MIVASettlements iv = getIVASettlementsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Tax_ID);
					iv.setAmount(iva);
					iv.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA 21";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal iva = safeMultiply(rs.getString("retiva_d1"), rs.getString("signo_7")).add(safeMultiply(rs.getString("iva1_ad_plancuo"), rs.getString("signo_04_16")))
							.add(safeMultiply(rs.getString("iva1_ad_opinter"), rs.getString("signo_04_18")));
					MIVASettlements iv = getIVASettlementsByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Tax_ID);
					iv.setAmount(iva);
					iv.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
				try {
					String name = "IVA";
					int C_Tax_ID = getTaxIDByName(attributes.get(name).getName());
					BigDecimal perceptionAmt = safeMultiply(rs.getString("retiva_esp"), rs.getString("signo_5"));
					MPerceptionsSettlement ps = getPerceptionsSettlementByTypeAndSettlement(settlement.getC_CreditCardSettlement_ID(), C_Tax_ID);
					ps.setAmount(perceptionAmt);
					ps.save();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				/* -- -- -- */
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
			return input;
		}
		return input.negate();
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

	private MPerceptionsSettlement getPerceptionsSettlementByTypeAndSettlement(int C_CreditCardSettlement_ID, int C_Tax_ID) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + MPerceptionsSettlement.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = ? ");
		sql.append("	AND C_Tax_ID = ? ");

		MPerceptionsSettlement perception = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, C_CreditCardSettlement_ID);
			ps.setInt(2, C_Tax_ID);

			rs = ps.executeQuery();

			if (rs.next()) {
				perception = new MPerceptionsSettlement(getCtx(), rs, get_TrxName());
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "getPerceptionsSettlementByTypeAndSettlement", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return perception;
	}

	private MIVASettlements getIVASettlementsByTypeAndSettlement(int C_CreditCardSettlement_ID, int C_Tax_ID) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + MIVASettlements.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = ? ");
		sql.append("	AND C_Tax_ID = ? ");

		MIVASettlements iva = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, C_CreditCardSettlement_ID);
			ps.setInt(2, C_Tax_ID);

			rs = ps.executeQuery();

			if (rs.next()) {
				iva = new MIVASettlements(getCtx(), rs, get_TrxName());
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "getIVASettlementsByTypeAndSettlement", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return iva;
	}

	private MCommissionConcepts getCommissionConceptsByTypeAndSettlement(int C_CreditCardSettlement_ID, int C_CardSettlementConcepts_ID) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + MIVASettlements.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = ? ");
		sql.append("	AND C_CreditCardSettlement_ID = ? ");

		MCommissionConcepts commission = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, C_CreditCardSettlement_ID);
			ps.setInt(2, C_CardSettlementConcepts_ID);

			rs = ps.executeQuery();

			if (rs.next()) {
				commission = new MCommissionConcepts(getCtx(), rs, get_TrxName());
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "getCommissionConceptsByTypeAndSettlement", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return commission;
	}

	private MWithholdingSettlement getWithholdingSettlementByTypeAndSettlement(int C_CreditCardSettlement_ID, int C_RetencionType_ID) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + MWithholdingSettlement.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = ? ");
		sql.append("	AND C_RetencionType_ID = ? ");

		MWithholdingSettlement withholding = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, C_CreditCardSettlement_ID);
			ps.setInt(2, C_RetencionType_ID);

			rs = ps.executeQuery();

			if (rs.next()) {
				withholding = new MWithholdingSettlement(getCtx(), rs, get_TrxName());
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "getWithholdingSettlementByTypeAndSettlement", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return withholding;
	}

	private MExpenseConcepts getExpenseConceptsByTypeAndSettlement(int C_CreditCardSettlement_ID, int C_CardSettlementConcepts_ID) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + MExpenseConcepts.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = ? ");
		sql.append("	AND C_CreditCardSettlement_ID = ? ");

		MExpenseConcepts concept = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, C_CreditCardSettlement_ID);
			ps.setInt(2, C_CardSettlementConcepts_ID);

			rs = ps.executeQuery();

			if (rs.next()) {
				concept = new MExpenseConcepts(getCtx(), rs, get_TrxName());
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "getExpenseConceptsByTypeAndSettlement", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return concept;
	}

}
