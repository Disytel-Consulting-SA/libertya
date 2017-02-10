package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MReference;
import org.openXpertya.model.X_AD_Ref_List;
import org.openXpertya.model.X_AD_Ref_List_Trl;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Reporte dinámico para listado de liquidaciones detallado.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class LaunchSettlementListDetailed extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {

		if (params.get("AD_Org_ID") != null && ((BigDecimal) params.get("AD_Org_ID")).intValue() > 0) {
			params.put("Organization", getOrgName((BigDecimal) params.get("AD_Org_ID")));
		}

		if (params.get("C_BPartner_ID") != null && ((BigDecimal) params.get("C_BPartner_ID")).intValue() > 0) {
			params.put("Card_Type_Name", getBPartnerName((BigDecimal) params.get("C_BPartner_ID")));
		}

		if (params.get("Doc_Status") != null && !((String) params.get("Doc_Status")).isEmpty()) {
			params.put("Doc_Status_Name", getRefName("_Document Status", (String) params.get("Doc_Status")));
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Timestamp leDate = null;

		if (params.get("Date1") != null) {
			leDate = (Timestamp) params.get("Date1");
			params.put("Date", leDate);
			params.put("Date_Text", sdf.format(new Date(leDate.getTime())));
		}

		if (params.get("Date2") != null) {
			leDate = (Timestamp) params.get("Date2");
			params.put("Date_To", leDate);
			params.put("Date_To_Text", sdf.format(new Date(leDate.getTime())));
		}
	}

	private String getRefName(String refName, String value) {
		if (refName == null || value == null) {
			return null;
		}
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	trl.name ");
		sql.append("FROM ");
		sql.append("	" + MReference.Table_Name + " r ");
		sql.append("	INNER JOIN " + X_AD_Ref_List.Table_Name + " rl ON r.ad_reference_id = rl.ad_reference_id ");
		sql.append("	INNER JOIN " + X_AD_Ref_List_Trl.Table_Name + " trl ON rl.ad_ref_list_id = trl.ad_ref_list_id ");
		sql.append("WHERE ");
		sql.append("	r.name = ? ");
		sql.append("	AND rl.value = ? ");
		sql.append("	AND ad_language = ? ");

		return DB.getSQLValueString("", sql.toString(), refName, value, Env.getAD_Language(Env.getCtx()));
	}

	private String getOrgName(BigDecimal id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	name ");
		sql.append("FROM ");
		sql.append("	" + MOrg.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	AD_Org_ID = ?");

		return DB.getSQLValueString("", sql.toString(), id.intValue());
	}

	private String getBPartnerName(BigDecimal id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	name ");
		sql.append("FROM ");
		sql.append("	" + MBPartner.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_BPartner_ID = ?");

		return DB.getSQLValueString("", sql.toString(), id.intValue());
	}

}
