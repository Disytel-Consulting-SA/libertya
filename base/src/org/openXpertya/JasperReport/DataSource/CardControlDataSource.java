package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openXpertya.model.X_C_CouponsSettlements;
import org.openXpertya.model.X_C_CreditCardCouponFilter;
import org.openXpertya.model.X_C_CreditCardSettlement;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.model.X_M_EntidadFinanciera;
import org.openXpertya.model.X_M_EntidadFinancieraPlan;
/**
 * Data Source para reporte de control de tarjetas.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class CardControlDataSource extends QueryDataSource {
	private int M_EntidadFinanciera_ID;
	private Timestamp dateFrom;
	private Timestamp dateTo;
	private int AD_Org_ID;

	public CardControlDataSource(String trxName) {
		super(trxName);
	}

	@Override
	protected String getQuery() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer sql = new StringBuffer();

		String dateFromStr = sdf.format(new Date(dateFrom.getTime()));
		String dateToStr = sdf.format(new Date(dateTo.getTime()));

		sql.append("SELECT ");
		sql.append("	a.name AS entidad_financiera, ");
		sql.append("	COALESCE(c.saldo,0) AS saldo_inicial, ");
		sql.append("	COALESCE(b.imp_liquidado, 0) AS importe_liquidado, ");
		sql.append("	COALESCE(a.imp_cobrado, 0) AS importe_cobrado, ");
		sql.append("	(COALESCE(c.saldo,0) + (COALESCE(b.imp_liquidado,0) - COALESCE(a.imp_cobrado,0))) AS saldo_final ");
		sql.append("FROM ( ");
		sql.append("	SELECT ");
		sql.append("		ef.M_EntidadFinanciera_ID, ");
		sql.append("		ef.name, ");
		sql.append("		p.AD_Org_ID, ");
		sql.append("		p.DateAcct, ");
		sql.append("		SUM(p.Payamt) AS imp_cobrado ");
		sql.append("	FROM ");
		sql.append("		" + X_C_Payment.Table_Name + " p ");
		sql.append("		INNER JOIN " + X_M_EntidadFinancieraPlan.Table_Name + " efp ON efp.M_EntidadFinancieraPlan_ID = p.M_EntidadFinancieraPlan_ID ");
		sql.append("		INNER JOIN " + X_M_EntidadFinanciera.Table_Name + " ef ON ef.M_EntidadFinanciera_ID = efp.M_EntidadFinanciera_ID ");
		sql.append("	GROUP BY ");
		sql.append("		ef.M_EntidadFinanciera_ID, ");
		sql.append("		ef.name, ");
		sql.append("		p.AD_Org_ID, ");
		sql.append("		p.DateAcct ");
		sql.append(") ");
		sql.append("a LEFT JOIN ");
		sql.append("( ");
		sql.append("	SELECT ");
		sql.append("		ef.M_EntidadFinanciera_ID, ");
		sql.append("		p.DateAcct, ");
		sql.append("		SUM(p.Payamt) AS imp_liquidado ");
		sql.append("	FROM ");
		sql.append("		" + X_C_CreditCardSettlement.Table_Name + " ccs ");
		sql.append("		INNER JOIN " + X_M_EntidadFinanciera.Table_Name + " ef ON ef.M_EntidadFinanciera_ID = ccs.M_EntidadFinanciera_ID ");
		sql.append("		INNER JOIN " + X_C_CreditCardCouponFilter.Table_Name + " cf ON cf.C_CreditCardSettlement_ID = ccs.C_CreditCardSettlement_ID ");
		sql.append("		INNER JOIN " + X_C_CouponsSettlements.Table_Name + " cs ON cs.C_CreditCardCouponFilter_ID = cf.C_CreditCardCouponFilter_ID ");
		sql.append("		INNER JOIN " + X_C_Payment.Table_Name + " p ON p.C_Payment_ID = cs.C_Payment_ID ");
		sql.append("	GROUP BY ");
		sql.append("		ef.M_EntidadFinanciera_ID, ");
		sql.append("		p.DateAcct ");
		sql.append(") b ON ( ");
		sql.append("	a.M_EntidadFinanciera_ID = b.M_EntidadFinanciera_ID ");
		sql.append("	AND a.DateAcct = b.DateAcct ");
		sql.append(") ");
		sql.append("LEFT JOIN  ");
		sql.append("( ");
		sql.append("	SELECT ");
		sql.append("	a.M_EntidadFinanciera_ID, ");
		sql.append("	a.name, ");
		sql.append("	COALESCE(b.imp_liquidado, 0) AS liquidado, ");
		sql.append("	COALESCE(a.imp_cobrado, 0) AS cobrado,	 ");
		sql.append("	COALESCE(COALESCE(b.imp_liquidado,0) - COALESCE(a.imp_cobrado,0), 0) AS saldo ");
		sql.append("	FROM ( ");
		sql.append("		SELECT ");
		sql.append("			ef.M_EntidadFinanciera_ID, ");
		sql.append("			ef.name, ");
		sql.append("			p.AD_Org_ID, ");
		sql.append("			p.DateAcct, ");
		sql.append("			SUM(p.Payamt) AS imp_cobrado ");
		sql.append("		FROM ");
		sql.append("			" + X_C_Payment.Table_Name + " p ");
		sql.append("			INNER JOIN " + X_M_EntidadFinancieraPlan.Table_Name + " efp ON efp.M_EntidadFinancieraPlan_ID = p.M_EntidadFinancieraPlan_ID ");
		sql.append("			INNER JOIN " + X_M_EntidadFinanciera.Table_Name + " ef ON ef.M_EntidadFinanciera_ID = efp.M_EntidadFinanciera_ID ");
		sql.append("		GROUP BY ");
		sql.append("			ef.M_EntidadFinanciera_ID, ");
		sql.append("			ef.name, ");
		sql.append("			p.AD_Org_ID, ");
		sql.append("			p.DateAcct ");
		sql.append("	) ");
		sql.append("	a LEFT JOIN ");
		sql.append("	( ");
		sql.append("		SELECT ");
		sql.append("			ef.M_EntidadFinanciera_ID, ");
		sql.append("			p.DateAcct, ");
		sql.append("			SUM(p.Payamt) AS imp_liquidado ");
		sql.append("		FROM ");
		sql.append("			" + X_C_CreditCardSettlement.Table_Name + " ccs ");
		sql.append("			INNER JOIN " + X_M_EntidadFinanciera.Table_Name + " ef ON ef.M_EntidadFinanciera_ID = ccs.M_EntidadFinanciera_ID ");
		sql.append("			INNER JOIN " + X_C_CreditCardCouponFilter.Table_Name + " cf ON cf.C_CreditCardSettlement_ID = ccs.C_CreditCardSettlement_ID ");
		sql.append("			INNER JOIN " + X_C_CouponsSettlements.Table_Name + " cs ON cs.C_CreditCardCouponFilter_ID = cf.C_CreditCardCouponFilter_ID ");
		sql.append("			INNER JOIN " + X_C_Payment.Table_Name + " p ON p.C_Payment_ID = cs.C_Payment_ID ");
		sql.append("		GROUP BY ");
		sql.append("			ef.M_EntidadFinanciera_ID, ");
		sql.append("			p.DateAcct ");
		sql.append("	) b ON ( ");
		sql.append("		a.M_EntidadFinanciera_ID = b.M_EntidadFinanciera_ID ");
		sql.append("		AND a.DateAcct = b.DateAcct ");
		sql.append("	) ");
		sql.append("	WHERE ");
		sql.append("		a.dateacct < '" + dateFromStr + "' "); // Fecha desde
		sql.append(") c ON c.M_EntidadFinanciera_ID = a.M_EntidadFinanciera_ID ");
		sql.append("WHERE ");
		sql.append("	1 = 1 ");

		if (AD_Org_ID > 0) {
			sql.append("	AND a.AD_Org_ID = " + AD_Org_ID + " "); // Organización
		}
		if (M_EntidadFinanciera_ID > 0) {
			sql.append("	AND a.M_EntidadFinanciera_ID = " + M_EntidadFinanciera_ID + " "); // Entidad financiera
		}
		sql.append("	AND a.dateacct >= '" + dateFromStr + "' "); // Fecha desde
		sql.append("	AND a.dateacct <= '" + dateToStr + "' "); // Fecha hasta

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

	public void setM_EntidadFinanciera_ID(int m_EntidadFinanciera_ID) {
		M_EntidadFinanciera_ID = m_EntidadFinanciera_ID;
	}

	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public void setAD_Org_ID(int aD_Org_ID) {
		AD_Org_ID = aD_Org_ID;
	}

}
