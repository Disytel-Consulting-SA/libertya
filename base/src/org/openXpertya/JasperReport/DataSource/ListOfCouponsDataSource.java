package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCouponsSettlements;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MReference;
import org.openXpertya.model.X_AD_Ref_List;
import org.openXpertya.model.X_AD_Ref_List_Trl;
import org.openXpertya.util.Env;

/**
 * Data Source para reporte de listado de cupones totalizados por estado.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class ListOfCouponsDataSource extends QueryDataSource {
	/** Contexto de ejecución. */
	private Properties ctx;

	private int C_BPartner_ID;
	private String auditState;
	private String dateFrom;
	private String dateTo;
	private int AD_Org_ID;

	public ListOfCouponsDataSource(String trxName) {
		super(trxName);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	bp.name, ");
		sql.append("	p.couponbatchnumber, ");
		sql.append("	COALESCE(s.settlementno, '') AS settlementno, ");
		sql.append("	p.couponnumber AS couponno, ");
		sql.append("	p.datetrx AS trxdate, ");
		sql.append("	l.name AS auditstatus, ");
		sql.append("	p.payamt AS amount ");
		sql.append("FROM ");
		sql.append("	" + MPayment.Table_Name + " p ");
		sql.append("	LEFT JOIN " + MCouponsSettlements.Table_Name + " c ");
		sql.append("		ON p.c_payment_id = c.c_payment_id ");
		sql.append("	LEFT JOIN " + MCreditCardSettlement.Table_Name + " s ");
		sql.append("		ON c.c_creditcardsettlement_id = s.c_creditcardsettlement_id ");
		sql.append("	LEFT JOIN " + MEntidadFinancieraPlan.Table_Name + " efp ");
		sql.append("		ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id ");
		sql.append("	LEFT JOIN " + MEntidadFinanciera.Table_Name + " ef ");
		sql.append("		ON efp.m_entidadfinanciera_id = ef.m_entidadfinanciera_id ");
		sql.append("	LEFT JOIN " + MBPartner.Table_Name + " bp ");
		sql.append("		ON ef.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("	LEFT JOIN ( ");
		sql.append("		SELECT ");
		sql.append("			r.value, ");
		sql.append("			tr.name ");
		sql.append("		FROM ");
		sql.append("			" + MReference.Table_Name + " a ");
		sql.append("			INNER JOIN " + X_AD_Ref_List.Table_Name + " r ");
		sql.append("				ON a.ad_reference_id = r.ad_reference_id ");
		sql.append("			INNER JOIN " + X_AD_Ref_List_Trl.Table_Name + " tr ");
		sql.append("				ON tr.ad_ref_list_id = r.ad_ref_list_id ");
		sql.append("		WHERE ");
		sql.append("			a.name = 'Audit Status' ");
		sql.append("			AND tr.ad_language = '" + Env.getAD_Language(ctx) + "' ");
		sql.append("	) l ON l.value = p.auditstatus ");
		sql.append("WHERE ");
		sql.append("	p.TenderType = '" + MPayment.TENDERTYPE_CreditCard + "' ");

		if (C_BPartner_ID > 0) {
			sql.append("	AND ef.c_bpartner_id = " + C_BPartner_ID + " ");
		}
		if (AD_Org_ID > 0) {
			sql.append("	AND p.ad_org_id = " + AD_Org_ID + " ");
		}
		if (auditState != null && !auditState.trim().isEmpty()) {
			sql.append("	AND p.auditstatus = '" + auditState + "' ");
		}
		if (dateFrom != null && !dateFrom.trim().isEmpty()) {
			sql.append("	AND p.datetrx >= '" + dateFrom + "' ");
		}
		if (dateTo != null && !dateTo.trim().isEmpty()) {
			sql.append("	AND p.datetrx <= '" + dateTo + "' ");
		}

		sql.append("ORDER BY ");
		sql.append("	p.auditstatus, ");
		sql.append("	c.trxdate, ");
		sql.append("	bp.name, ");
		sql.append("	c.paymentbatch, ");
		sql.append("	s.settlementno, ");
		sql.append("	c.couponno ");

		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return new Object[0];
	}

	@Override
	protected boolean isQueryNoConvert() {
		return true;
	}

	// SETTERS:

	public void setC_BPartner_ID(int C_BPartner_ID) {
		this.C_BPartner_ID = C_BPartner_ID;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public void setAD_Org_ID(int aD_Org_ID) {
		AD_Org_ID = aD_Org_ID;
	}

	public void setAuditState(String auditState) {
		this.auditState = auditState;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
