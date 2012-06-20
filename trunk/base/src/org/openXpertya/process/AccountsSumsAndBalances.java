package org.openXpertya.process;

import org.openXpertya.model.MSumsAndBalance;
import org.openXpertya.model.MTreeNode;
import org.openXpertya.util.DB;

public class AccountsSumsAndBalances extends AccountsGeneralBalance {

	@Override
	protected String doIt() throws Exception {
		
		// delete all rows older than a week
		DB.executeUpdate("DELETE FROM t_sumsandbalance WHERE CREATED < ('now'::text)::timestamp(6) - interval '7 days'");		
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM t_sumsandbalance WHERE AD_PInstance_ID = " + getAD_PInstance_ID());
		
		// Procesamiento de la superclase. (Es necesario para que se calculen las
		// columnas Debe y Haber). 
		// Bypass para que no actualice el saldo ya que la tabla temporal de
		// esta clase no posee la columna balance y se rompe
		updateBalance = false;
		super.doIt();
		
		// Se borran las líneas que tiene Debe y Haber igual a cero.
		DB.executeUpdate(
				" DELETE FROM " + getReportTableName() + 
				" WHERE Debit = 0 AND Credit = 0 AND AD_PInstance_ID = " + getAD_PInstance_ID(), get_TrxName());

		// Se actualizan los campos Saldo Deudor y Acreedor.
		DB.executeUpdate(
			" UPDATE " + getReportTableName() +
			" SET " +
			"     DebitBalance  = CASE WHEN (Debit - Credit) >= 0 THEN (Debit - Credit) ELSE 0 END, " +
			"     CreditBalance = CASE WHEN (Debit - Credit) < 0 THEN (Debit - Credit) ELSE 0 END " +
			" WHERE AD_PInstance_ID = " + getAD_PInstance_ID(), get_TrxName());
 		
		return null;
	}

	@Override
	protected void createReportLine(AccountElement accountElement) throws Exception {
		// Solo se necesitan las cuentas Hojas.
		if (accountElement.elementValue != null && !accountElement.elementValue.isSummary()) {
			MSumsAndBalance line = new MSumsAndBalance(getCtx(), 0, get_TrxName());
			
			line.setAD_PInstance_ID(getAD_PInstance_ID());
			line.setSubindex(accountElement.subindex);
			line.setC_ElementValue_ID(accountElement.elementValueID);
			line.setAcct_Code(accountElement.code);
			line.setAcct_Description(accountElement.description);
			line.setAD_Org_ID(accountElement.orgID);
			line.setHierarchicalCode(accountElement.hierarchicalCode);
			line.setDateAcct(p_DateAcct_From);
			// Debe, Haber, Saldo Deudor y Saldo Acreedor se calculan masivamente
			// en el doIt.
			line.setDebit(null);
			line.setCredit(null);
			line.setCreditBalance(null);
			line.setDebitBalance(null);
			
			if (!line.save()) {
				log.severe("Cannot save X_T_SumsAndBalance line. C_ElementValue_ID=" + line.getC_ElementValue_ID());
				throw new Exception("@ProcessRunError@");
			}
		}
	}

	@Override
	protected String getIndentation(MTreeNode node) {
		return "";
	}

	@Override
	protected String getReportTableName() {
		return "T_SumsAndBalance";
	}

	@Override
	protected void clearDateAcct() {
		// En este informe se agregó obligadamente el campo DateAcct en la tabla temporal
		// y entonces no es necesario limpiar el valor del parámetro en AD_PInstance_Para.
	}

	
	
}
