package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class DeclaracionValoresMainDataSource extends DeclaracionValoresDataSource {

	public DeclaracionValoresMainDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresMainDataSource(Properties ctx, DeclaracionValoresDTO valoresDTO, String trxName) {
		super(ctx, valoresDTO, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void loadData() throws Exception {		
		String sqlInsert = " INSERT INTO t_pos_declaracionvalores (t_pos_declaracionvalores_id, ad_pinstance_id, createdby, updatedby, "
				+ "ad_client_id, ad_org_id, c_posjournal_id, ad_user_id, c_currency_id, datetrx, docstatus, "
				+ "category, tendertype, description, c_charge_id, chargename, doc_id, ingreso, egreso, "
				+ "c_invoice_id, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, "
				+ "bp_entidadfinanciera_value, bp_entidadfinanciera_name, cupon, creditcard, generated_invoice_documentno, "
				+ "allocation_active, c_pos_id, posname) ";
		String sqlSelect = " SELECT nextval('seq_t_pos_declaracionvalores'), " + getValoresDTO().getpInstanceID() + ", "
				+ Env.getAD_User_ID(getCtx()) + ", " + Env.getAD_User_ID(getCtx()) + ", ds.* "
				+ "FROM "+getDSDataTable()+" as ds ";
		int inserted = DB.executeUpdate(sqlInsert+sqlSelect, getTrxName());
	}
}
