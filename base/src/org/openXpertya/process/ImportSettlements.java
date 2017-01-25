package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.X_C_Currency;
import org.openXpertya.model.X_M_EntidadFinanciera;
import org.openXpertya.process.customImport.centralPos.commons.CentralPosImport;
import org.openXpertya.process.customImport.centralPos.pojos.AmexPaymentsWithTaxes;
import org.openXpertya.process.customImport.centralPos.pojos.NaranjaPayments;
import org.openXpertya.process.customImport.centralPos.pojos.Pojo;
import org.openXpertya.process.customImport.centralPos.pojos.TrailerParticipant;
import org.openXpertya.process.customImport.centralPos.pojos.VisaPayments;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Importación de liquidaciones desde tablas temporales.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportSettlements extends SvrProcess {

	/** Eliminar registros importados previamente. */
	private boolean m_deleteOldImported = false;
	/** Tipo de tarjeta. */
	private String p_CreditCardType;
	/** Moneda por defecto. */
	private X_C_Currency defaultCurrency;

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
		Pojo importClass = null;

		if (p_CreditCardType != null) {
			if (p_CreditCardType.equals(CentralPosImport.FIRSTDATA)) {
				importClass = new TrailerParticipant();
			} else if (p_CreditCardType.equals(CentralPosImport.NARANJA)) {
				importClass = new NaranjaPayments();
			} else if (p_CreditCardType.equals(CentralPosImport.AMEX)) {
				importClass = new AmexPaymentsWithTaxes();
			} else if (p_CreditCardType.equals(CentralPosImport.VISA)) {
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
			log.fine("Delete Old Impored = " + no);
		}

		StringBuffer sql = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sql.append("SELECT ");
			sql.append("	* ");
			sql.append("FROM ");
			sql.append("	" + X_C_Currency.Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	ISO_Code = 'ARS' ");

			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				defaultCurrency = new X_C_Currency(getCtx(), rs, get_TrxName());
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "ImportSettlements.doIt", e);
		}

		sql = new StringBuffer();
		sql.append("SELECT ");
		for (int i = 0; i < importClass.getFields().length; i++) {
			String field = importClass.getFields()[i];
			sql.append("	" + field + ((i+1 == importClass.getFields().length) ? " " : ", "));
		}
		sql.append("FROM ");
		sql.append("	" + importClass.getTableName() + " ");

		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			if (p_CreditCardType.equals(CentralPosImport.FIRSTDATA)) {
				while (rs.next()) {
					createFromFirstData(rs);
				}
			} else if (p_CreditCardType.equals(CentralPosImport.NARANJA)) {
				while (rs.next()) {
					createFromNaranja(rs);
				}
			} else if (p_CreditCardType.equals(CentralPosImport.AMEX)) {
				while (rs.next()) {
					createFromAmex(rs);
				}
			} else if (p_CreditCardType.equals(CentralPosImport.VISA)) {
				while (rs.next()) {
					createFromVisa(rs);
				}
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
		return null;
	}

	/**
	 * Obtiene el ID de una entidad financiera a partir del código (columna "value").
	 * @param value valor por el cual se filtrará la búsqueda de entidad financiera.
	 * @return ID de la entidad financiera, caso contrario -1.
	 */
	private int getM_EntidadFinanciera_ID(String value) {
		if (value == null || value.trim().isEmpty()) {
			return -1;
		}
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	M_EntidadFinanciera_ID ");
		sql.append("FROM ");
		sql.append("	" + X_M_EntidadFinanciera.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	EstablishmentNumber = '" + value + "' ");

		return DB.getSQLValue(get_TrxName(), sql.toString());
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/**
	 * Crea y guarda una liquidación para importaciones de First Data.
	 * @param rs ResultSet con los datos extraídos de la
	 * tabla de importación correspondiente a First Data.
	 */
	private void createFromFirstData(ResultSet rs) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(rs.getString("fecha_vencimiento_clearing"));
			sdf = new SimpleDateFormat("dd/MM/yyyy");
			String dateStr = sdf.format(date);

			BigDecimal withholding = new BigDecimal(rs.getString("ret_iva_ventas")).multiply(new BigDecimal(rs.getString("ret_iva_ventas_signo")))
					.add(new BigDecimal(rs.getString("ret_imp_ganancias"))).multiply(new BigDecimal(rs.getString("ret_imp_ganancias_signo")))
					.add(new BigDecimal(rs.getString("ret_imp_ingresos_brutos"))).multiply(new BigDecimal(rs.getString("ret_imp_ingresos_brutos_signo")));

			BigDecimal iva = new BigDecimal(rs.getString("iva_aranceles_ri")).multiply(new BigDecimal(rs.getString("iva_aranceles_ri_signo")))
					.add(new BigDecimal(rs.getString("iva_dto_pago_anticipado"))).multiply(new BigDecimal(rs.getString("iva_dto_pago_anticipado_signo")));

			BigDecimal amt = new BigDecimal(rs.getString("total_importe_total")).multiply(new BigDecimal(rs.getString("total_importe_total_signo")));

			MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

			settlement.setCreditCardType(CentralPosImport.FIRSTDATA);
			settlement.setM_EntidadFinanciera_ID(getM_EntidadFinanciera_ID(rs.getString("comercio_participante")));
			settlement.setPaymentDate(new Timestamp(date.getTime()));
			settlement.setPayment(rs.getString("numero_liquidacion"), defaultCurrency.getCurSymbol(), amt.toString(), dateStr, dateStr);
			settlement.setAmount(amt);
			settlement.setNetAmount(new BigDecimal(rs.getString("neto_comercios")).multiply(new BigDecimal(rs.getString("neto_comercios_signo"))));
			settlement.setWithholding(withholding);
			settlement.setPerception(new BigDecimal(rs.getString("percepc_iva_r3337")).multiply(new BigDecimal(rs.getString("percepc_iva_r3337_signo"))));
			settlement.setExpenses(new BigDecimal(rs.getString("costo_financiero")).multiply(new BigDecimal(rs.getString("costo_financiero_signo"))));
			settlement.setCouponsTotalAmount(new BigDecimal(rs.getString("total_importe_total")).multiply(new BigDecimal(rs.getString("total_importe_total_signo"))));
			settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
			settlement.setSettlementNo(rs.getString("numero_liquidacion"));
			settlement.setIVAAmount(iva);
			settlement.setCommissionAmount(new BigDecimal(rs.getString("arancel")).multiply(new BigDecimal(rs.getString("arancel_signo"))));

			if (!settlement.save()) {
				CLogger.retrieveErrorAsString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Crea y guarda una liquidación para importaciones de Naranja.
	 * @param rs ResultSet con los datos extraídos de la
	 * tabla de importación correspondiente a Naranja.
	 */
	private void createFromNaranja(ResultSet rs) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(rs.getString("fecha_pago"));
			sdf = new SimpleDateFormat("dd/MM/yyyy");
			String dateStr = sdf.format(date);

			BigDecimal withholding = BigDecimal.ZERO;
			try {
				withholding = new BigDecimal(rs.getString("retencion_iva_140")).multiply(new BigDecimal(rs.getString("signo_ret_iva_140")))
						.add(new BigDecimal(rs.getString("retencion_ganancias"))).multiply(new BigDecimal(rs.getString("signo_ret_ganancias")))
						.add(new BigDecimal(rs.getString("ret_ingresos_brutos"))).multiply(new BigDecimal(rs.getString("signo_ret_ing_brutos")));
			} catch(Exception e) {
				// System.err.println("NO SE PUDIERON CALCULAR RETENCIONES, UNO O MAS VALORES NULOS");
			}

			BigDecimal expenses = BigDecimal.ZERO;
			try {
				expenses = new BigDecimal(rs.getString("imp_acre_liq_ant_vto")).multiply(new BigDecimal(rs.getString("sig_acre_liq_ant_vto")))
						.add(new BigDecimal(rs.getString("imp_acre_liq_ant_facturado_30"))).multiply(new BigDecimal(rs.getString("sig_acre_liq_ant_facturado_30")))
						.add(new BigDecimal(rs.getString("imp_acre_liq_ant_facturado_60"))).multiply(new BigDecimal(rs.getString("sig_acre_liq_ant_facturado_60")))
						.add(new BigDecimal(rs.getString("imp_acre_liq_ant_facturado_90"))).multiply(new BigDecimal(rs.getString("sig_acre_liq_ant_facturado_90")))
						.add(new BigDecimal(rs.getString("imp_acre_liq_ant_facturado_120"))).multiply(new BigDecimal(rs.getString("sig_acre_liq_ant_facturado_120")))
						.add(new BigDecimal(rs.getString("imp_int_plan_esp_vto"))).multiply(new BigDecimal(rs.getString("sig_int_plan_esp_vto")))
						.add(new BigDecimal(rs.getString("imp_int_plan_esp_facturado_30"))).multiply(new BigDecimal(rs.getString("sig_int_plan_esp_facturado_30")))
						.add(new BigDecimal(rs.getString("imp_int_plan_esp_facturado_60"))).multiply(new BigDecimal(rs.getString("sig_int_plan_esp_facturado_60")))
						.add(new BigDecimal(rs.getString("imp_int_plan_esp_facturado_90"))).multiply(new BigDecimal(rs.getString("sig_int_plan_esp_facturado_90")))
						.add(new BigDecimal(rs.getString("imp_int_plan_esp_facturado_120"))).multiply(new BigDecimal(rs.getString("sig_int_plan_esp_facturado_120")));
			} catch(Exception e) {
				// System.err.println("NO SE PUDIERON CALCULAR GASTOS, UNO O MAS VALORES NULOS");
			}

			BigDecimal iva = BigDecimal.ZERO;
			try {
				iva = new BigDecimal(rs.getString("imp_iva_21_vto")).multiply(new BigDecimal(rs.getString("sig_iva_21_vto")))
						.add(new BigDecimal(rs.getString("imp_iva_21_facturado_30"))).multiply(new BigDecimal(rs.getString("imp_iva_21_facturado_30")))
						.add(new BigDecimal(rs.getString("imp_iva_21_facturado_60"))).multiply(new BigDecimal(rs.getString("imp_iva_21_facturado_60")))
						.add(new BigDecimal(rs.getString("imp_iva_21_facturado_90"))).multiply(new BigDecimal(rs.getString("imp_iva_21_facturado_90")))
						.add(new BigDecimal(rs.getString("imp_iva_21_facturado_120"))).multiply(new BigDecimal(rs.getString("imp_iva_21_facturado_120")));
			} catch(Exception e) {
				// System.err.println("NO SE PUDIERON CALCULAR IMPUESTOS, UNO O MAS VALORES NULOS");
			}

			BigDecimal commission = BigDecimal.ZERO;
			try {
				commission = new BigDecimal(rs.getString("importe_ara_vto")).multiply(new BigDecimal(rs.getString("signo_ara_vto")))
						.add(new BigDecimal(rs.getString("importe_ara_facturado_30"))).multiply(new BigDecimal(rs.getString("signo_ara_facturado_30")))
						.add(new BigDecimal(rs.getString("importe_ara_facturado_60"))).multiply(new BigDecimal(rs.getString("signo_ara_facturado_60")))
						.add(new BigDecimal(rs.getString("importe_ara_facturado_90"))).multiply(new BigDecimal(rs.getString("signo_ara_facturado_90")))
						.add(new BigDecimal(rs.getString("importe_ara_facturado_120"))).multiply(new BigDecimal(rs.getString("signo_ara_facturado_120")));
			} catch(Exception e) {
				// System.err.println("NO SE PUDIERON CALCULAR COMISIONES, UNO O MAS VALORES NULOS");
			}

			BigDecimal amt = BigDecimal.ZERO;
			try {
				amt = new BigDecimal(rs.getString("neto")).multiply(new BigDecimal(rs.getString("signo_neto"))); 
			} catch (Exception e) {
				// System.err.println("NO SE PUDO CALCULAR EL IMPORTE NETO, UNO O MAS VALORES NULOS");
			}

			int entidadFinancieraID = getM_EntidadFinanciera_ID(rs.getString("comercio"));

			if (entidadFinancieraID > 0) {
				String settlementNo = rs.getString("nro_liquidacion");
				if (settlementNo == null || settlementNo.equals("null")) {
					settlementNo = "";
				}
				MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

				settlement.setCreditCardType(CentralPosImport.NARANJA);
				settlement.setM_EntidadFinanciera_ID(entidadFinancieraID);
				settlement.setPaymentDate(new Timestamp(date.getTime()));
				settlement.setPayment(settlementNo, defaultCurrency.getCurSymbol(), amt.toString(), dateStr, dateStr);
				settlement.setAmount(amt);
				settlement.setNetAmount(amt);
				settlement.setWithholding(withholding);
				settlement.setPerception(BigDecimal.ZERO);
				settlement.setExpenses(expenses);
				settlement.setCouponsTotalAmount(amt);
				settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
				settlement.setSettlementNo(settlementNo);
				settlement.setIVAAmount(iva);
				settlement.setCommissionAmount(commission);

				if (!settlement.save()) {
					CLogger.retrieveErrorAsString();
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Crea y guarda una liquidación para importaciones de American Express.
	 * @param rs ResultSet con los datos extraídos de la
	 * tabla de importación correspondiente a American Express.
	 */
	private void createFromAmex(ResultSet rs) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(rs.getString("fecha_pago"));
			sdf = new SimpleDateFormat("dd/MM/yyyy");
			String dateStr = sdf.format(date);

			BigDecimal expenses = new BigDecimal(rs.getString("imp_tot_desc_acel"));
			BigDecimal commission = new BigDecimal(rs.getString("imp_desc_pago"));
			BigDecimal amt = new BigDecimal(rs.getString("imp_neto_ajuste"));
			BigDecimal impAmt = BigDecimal.ZERO;

			if (rs.getString("importe_imp") != null && !rs.getString("importe_imp").equals("null")) {
				impAmt = new BigDecimal(rs.getString("importe_imp"));
			}
			String codImp = rs.getString("cod_imp");
			if (codImp == null) {
				codImp = "";
			}
			MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

			settlement.setCreditCardType(CentralPosImport.AMEX);
			settlement.setM_EntidadFinanciera_ID(getM_EntidadFinanciera_ID(rs.getString("num_est")));
			settlement.setPaymentDate(date != null ? new Timestamp(date.getTime()) : null);
			settlement.setPayment(rs.getString("num_sec_pago"), defaultCurrency.getCurSymbol(), amt.toString(), dateStr, dateStr);
			settlement.setAmount(new BigDecimal(rs.getString("imp_bruto_est")));
			settlement.setNetAmount(amt);
			settlement.setExpenses(expenses);
			settlement.setCouponsTotalAmount(amt);
			settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
			settlement.setSettlementNo(rs.getString("num_sec_pago"));
			settlement.setCommissionAmount(commission);

			if (codImp.equals("01")) { // I.V.A.
				settlement.setIVAAmount(impAmt);
			}
			if (codImp.equals("02")) { // Percepción I.V.A.
				settlement.setPerception(impAmt);
			}
			if (codImp.equals("04")) { // Retención I.V.A.
				settlement.setWithholding(impAmt);
			}
			if (codImp.equals("05")) { // Retención ganancias
				settlement.setWithholding(impAmt);
			}
			if (codImp.equals("06")) { // IIBB
				settlement.setExpenses(settlement.getExpenses().add(impAmt));
			}
			if (codImp.equals("09")) { // IIBB B.S. A.S.
				settlement.setExpenses(settlement.getExpenses().add(impAmt));
			}
			if (!settlement.save()) {
				CLogger.retrieveErrorAsString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Crea y guarda una liquidación para importaciones de Visa.
	 * @param rs ResultSet con los datos extraídos de la
	 * tabla de importación correspondiente a Visa.
	 */
	private void createFromVisa(ResultSet rs) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date date = sdf.parse(rs.getString("fpag"));
			String dateStr = sdf.format(date);

			BigDecimal withholding = new BigDecimal(rs.getString("ret_ingbru")).multiply((rs.getString("signo_32") == "-") ? new BigDecimal(-1) : new BigDecimal(1))
					.add(new BigDecimal(rs.getString("ret_iva"))).multiply((rs.getString("signo_30") == "-") ? new BigDecimal(-1) : new BigDecimal(1))
					.add(new BigDecimal(rs.getString("ret_gcias"))).multiply((rs.getString("signo_31") == "-") ? new BigDecimal(-1) : new BigDecimal(1));

			BigDecimal expenses = new BigDecimal(rs.getString("dto_campania")).multiply((rs.getString("signo_04_3") == "-") ? new BigDecimal(-1) : new BigDecimal(1))
					.add(new BigDecimal(rs.getString("costo_cuoemi"))).multiply((rs.getString("signo_12") == "-") ? new BigDecimal(-1) : new BigDecimal(1))
					.add(new BigDecimal(rs.getString("adic_plancuo"))).multiply((rs.getString("signo_04_15") == "-") ? new BigDecimal(-1) : new BigDecimal(1))
					.add(new BigDecimal(rs.getString("adic_opinter"))).multiply((rs.getString("signo_04_17") == "-") ? new BigDecimal(-1) : new BigDecimal(1));

			BigDecimal iva = new BigDecimal(rs.getString("retiva_cuo1")).multiply((rs.getString("signo_13") == "-") ? new BigDecimal(-1) : new BigDecimal(1))
					.add(new BigDecimal(rs.getString("retiva_d1"))).multiply((rs.getString("signo_7") == "-") ? new BigDecimal(-1) : new BigDecimal(1))
					.add(new BigDecimal(rs.getString("iva1_ad_plancuo"))).multiply((rs.getString("signo_04_16") == "-") ? new BigDecimal(-1) : new BigDecimal(1))
					.add(new BigDecimal(rs.getString("iva1_ad_opinter"))).multiply((rs.getString("signo_04_18") == "-") ? new BigDecimal(-1) : new BigDecimal(1));

			BigDecimal commission = new BigDecimal(rs.getString("impret")).multiply((rs.getString("signo_2") == "-") ? new BigDecimal(-1) : new BigDecimal(1));

			BigDecimal netAmt = new BigDecimal(rs.getString("impneto")).multiply((rs.getString("signo_3") == "-") ? new BigDecimal(-1) : new BigDecimal(1));
			BigDecimal amt = new BigDecimal(rs.getString("impbruto")).multiply((rs.getString("signo_1") == "-") ? new BigDecimal(-1) : new BigDecimal(1));

			MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

			settlement.setCreditCardType(CentralPosImport.VISA);
			settlement.setM_EntidadFinanciera_ID(getM_EntidadFinanciera_ID(rs.getString("num_est")));
			settlement.setPaymentDate(new Timestamp(date.getTime()));
			settlement.setPayment(rs.getString("nroliq"), defaultCurrency.getCurSymbol(), amt.toString(), dateStr, dateStr);
			settlement.setAmount(amt);
			settlement.setNetAmount(netAmt);
			settlement.setWithholding(withholding);
			settlement.setPerception(new BigDecimal(0));
			settlement.setExpenses(expenses);
			settlement.setCouponsTotalAmount(netAmt);
			settlement.setC_Currency_ID(defaultCurrency.getC_Currency_ID());
			settlement.setSettlementNo(rs.getString("nroliq"));
			settlement.setIVAAmount(iva);
			settlement.setCommissionAmount(commission);

			if (!settlement.save()) {
				CLogger.retrieveErrorAsString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
