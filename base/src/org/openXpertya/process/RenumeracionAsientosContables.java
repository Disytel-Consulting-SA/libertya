package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_CashLine;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class RenumeracionAsientosContables extends SvrProcess {
	private static final int ORDEN_VENTA = 1;
	private static final int ORDEN_COMPRA = 2;
	private static final int ORDEN_COBROS = 3;
	private static final int ORDEN_PAGOS = 4;
	private static final int ORDEN_RESTO = 5;
	private static final int SUBORDEN_ALLOCATION = 1;
	private static final int SUBORDEN_PAYMENT = 2;
	private static final int SUBORDEN_CASHLINE = 3;
	private static final int SUBORDEN_INVOICE_CREDIT = 4;
	private Date fecha;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("fecha")) {
				setFecha((Date) para[i].getParameter());
			}
		}
		
	}

	@Override
	protected String doIt() throws Exception {
		// Obtengo el nro de asiento máximo a partir de la fecha pasada por
		// parámetro
		int nro = consultarNumeroMax();
		// Renumero asientos
		if (renumerarAsientos(nro))
			return getMsg("SuccessfulRenumbering");
		else
			return getMsg("UnsuccessfulRenumbering");
	}

	protected String getMsg(String msg) {
		return Msg.translate(getCtx(), msg);
	}

	private int consultarNumeroMax() {
		// Retorna el nro de asiento máximo + 1 a partir de una fecha
		Integer nro = null;
		if (getFecha() != null) {
			String sql = "select MAX(journalno) from fact_acct where dateacct::date < '"
					+ getFecha() + "'::date";
			nro = DB.getSQLValue(get_TrxName(), sql);
		}
		// Si la fecha es null o el nro es < 0, devuelvo 1
		if ((nro == null) || (nro < 0)) {
			nro = 0;
		}
		return nro + 1;
	}

	private boolean renumerarAsientos(int nro) {
		Integer table_id;
		String columunname = null;
		String tipo = null;
		Map<Integer, Integer> payments = new HashMap<Integer, Integer>();
		Map<Integer, Integer> allocations = new HashMap<Integer, Integer>();
		Map<Integer, Integer> cashlines = new HashMap<Integer, Integer>();
		Map<Integer, Integer> invoice_credits = new HashMap<Integer, Integer>();
		Integer nroEnMapPayment;
		Integer nroEnMapAllocation;
		Integer nroEnMapCashline;
		Integer nroEnMapInvoiceCredit;
		Integer nroASetear = null;
		String update;
		//La consulta ordena primero por fecha, luego por venta, compra, pago, cobro y resto. Si se le pasa una fecha al proceso, la consulta se realiza
		//a partir de la fecha dada.
		String sqlGeneral = " SELECT dateacct, ORDEN, SUBORDEN, record_id, fact_acct_id, table_id, c_allocationhdr_id, c_payment_id, c_cashline_id, c_invoice_credit_id "
				+ "FROM ("
				+ sqlVentasComprasCobrosPagos()
				+ " ) as VenComCobPag "
				+ "UNION"
				+ " SELECT FSVCCP.dateacct, FSVCCP.ORDEN, FSVCCP.SUBORDEN, FSVCCP.record_id, FSVCCP.fact_acct_id, FSVCCP.table_id, CAST(NULL as int) c_allocationhdr_id, CAST(NULL as int) c_payment_id, CAST(NULL as int) c_cashline_id, CAST(NULL as int) c_invoice_credit_id "
				+ "FROM "
				+ " ("
				+ " select fac.dateacct, "
				+ ORDEN_RESTO
				+ " as ORDEN, "
				+ SUBORDEN_ALLOCATION
				+ " as SUBORDEN, record_id, fac.fact_acct_id, fac.ad_table_id as table_id"
				+ " from ("
				+ "		select fact_acct_id from fact_acct"
				+ "		EXCEPT"
				+ "		select distinct VCCP.fact_acct_id "
				+ "		from ( "
				+ sqlVentasComprasCobrosPagos()
				+ " ) as VCCP "
				+ "		 ) as exce"
				+ " inner join fact_acct fac "
				+ " on (fac.fact_acct_id = exce.fact_acct_id) "
				+ " ) as FSVCCP";
		if (getFecha() != null) {
			sqlGeneral = sqlGeneral + " where dateacct::date >= '" + getFecha()
					+ "'::date";
		}
		sqlGeneral = sqlGeneral
				+ " ORDER BY dateacct, ORDEN, SUBORDEN, table_id, record_id";
		PreparedStatement pstmt = null;
		try {
			pstmt = DB.prepareStatement(sqlGeneral.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				table_id = null;
				if ((rs.getInt("ORDEN") == ORDEN_COBROS)
						|| (rs.getInt("ORDEN") == ORDEN_PAGOS)) {
					// Como es un Cobro o Pago las lineas de fact_acct pertenecen a un allocation
					// pero pueden haber lineas que pertencen al allocation pero que no tengan el c_allocationhdr_id
					// Seteo el tipo que va a llevar la columna typefact_acct
					if (rs.getInt("ORDEN") == ORDEN_COBROS) {
						tipo = "PR";
					} else {
						tipo = "VP";
					}
					Integer allocationhdrId = rs.getInt("c_allocationhdr_id");
					if ((allocationhdrId != null) && (allocationhdrId > 0)) {
						//Pertenece a un allocation
						nroEnMapAllocation = allocations.get(allocationhdrId);
						if (nroEnMapAllocation != null) {
							//Si existe el c_allocationhdr_id en la map, me lo guardo para asignarselo a la linea
							nroASetear = nroEnMapAllocation;
						} else {
							//Sino guardo un nuevo journalno para ese allocation
							nroASetear = nro;
							nro++;
							allocations.put(allocationhdrId, nroASetear);
						}
					}
					Integer paymentID = rs.getInt("c_payment_id");
					if ((paymentID != null) && (paymentID > 0)) {
						//Es un payment
						nroEnMapPayment = payments.get(paymentID);
						if (nroEnMapPayment != null) {
							//Si existe en la map el c_payment_id me lo guardo
							nroASetear = nroEnMapPayment;
						} else {
							if ((allocationhdrId != null)
									&& (allocationhdrId > 0)) {
								//Sino, busco el journalno que le asigné al allocation al que pertenece el c_payment
								//A su vez me guardo la tabla y la columna para poder poner el mismo journalno a las lineas del c_payment
								nroASetear = allocations.get(allocationhdrId);
								table_id = X_C_Payment.Table_ID;
								columunname = "c_payment_id";
							} else {
								//Sino le asigno un nuevo nro
								nroASetear = nro;
								nro++;
							}
							payments.put(paymentID, nroASetear);
						}
					}
					//Si es un allocation que no existe en la map, guardo el journalno que le asigno
					Integer cashLineID = rs.getInt("c_cashline_id");
					if ((cashLineID != null) && (cashLineID > 0)) {
						//Es un cashline
						nroEnMapCashline = cashlines.get(cashLineID);
						if (nroEnMapCashline != null) {
							//Si existe en la map el c_chasline_id me lo guardo
							nroASetear = nroEnMapCashline;
						} else {
							if ((allocationhdrId != null)
									&& (allocationhdrId > 0)) {
								//Sino, busco el journalno que le asigné al allocation al que pertenece el cashline
								//A su vez me guardo la tabla y la columna para poder poner el mismo journalno a las lineas del cashline
								nroASetear = allocations.get(allocationhdrId);
								table_id = X_C_CashLine.Table_ID;
								columunname = "c_cashline_id";
							} else {
								//Sino le asigno un nuevo nro
								nroASetear = nro;
								nro++;
							}
							cashlines.put(cashLineID, nroASetear);
						}
					}

					Integer invoiceCreditID = rs.getInt("c_invoice_credit_id");
					if ((invoiceCreditID != null) && (invoiceCreditID > 0)) {
						//Es una invoice_credit_id
						nroEnMapInvoiceCredit = invoice_credits.get(cashLineID);
						if (nroEnMapInvoiceCredit != null) {
							//Si existe en la map el c_invoice_credit_id me lo guardo
							nroASetear = nroEnMapInvoiceCredit;
						} else {
							if ((allocationhdrId != null)
									&& (allocationhdrId > 0)) {
								//Sino, busco el journalno que le asigné al allocation al que pertenece el invoice_credit_id
								//A su vez me guardo la tabla y la columna para poder poner el mismo journalno a las lineas del invoice_credit_id
								nroASetear = allocations.get(allocationhdrId);
								table_id = X_C_Invoice.Table_ID;
								columunname = "c_invoice_credit_id";
							} else {
								nroASetear = nro;
								nro++;
							}
							invoice_credits.put(invoiceCreditID, nroASetear);
						}
					}
					//Updateo la línea actual del rs.
					update = "update fact_acct set journalno = " + nroASetear
							+ ", typefactacct = '" + tipo + "'"
							+ " where fact_acct_id = "
							+ rs.getInt("fact_acct_id");
					DB.executeUpdate(update, get_TrxName());
					//Updateo con el mismo journalno las líneas del c_payment, c_cashline y c_invoice_credit que pertenecen al allocation.
					if ((table_id != null) && (columunname != null)) {
						update = "update fact_acct set journalno = "
								+ nroASetear + ", typefactacct = '" + tipo
								+ "'" + " where ad_table_id = " + table_id
								+ " and record_id = " + rs.getInt(columunname);
						DB.executeUpdate(update, get_TrxName());
					}
				} else {
					// Seteo el tipo que va a llevar la columna typefact_acct
					if (rs.getInt("ORDEN") == ORDEN_VENTA) {
						tipo = "S";
					} else {
						if (rs.getInt("ORDEN") == ORDEN_COMPRA) {
							tipo = "P";
						} else {
							tipo = "R";
						}
					}
					//Updateo la línea actual del rs.
					update = "update fact_acct set journalno = " + nro
							+ ", typefactacct = '" + tipo + "'"
							+ " where fact_acct_id = "
							+ rs.getInt("fact_acct_id");
					DB.executeUpdate(update, get_TrxName());
					nro++;
				}
			}
			rs.close();
			pstmt.close();
			pstmt = null;
			return true;
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "doIt - get fact_acct_id ", ex);
			return false;
		}
	}

	private String sqlVentasComprasCobrosPagos() {
		String sql =
		// Ventas
		"("
				+ " select f.fact_acct_id, f.dateacct, i.c_invoice_id as record_id, CAST(NULL as int) c_payment_id, CAST(NULL as int) c_cashline_id, CAST(NULL as int) as c_invoice_credit_id, CAST(NULL as int) as  c_allocationhdr_id, "
				+ ORDEN_VENTA
				+ " as ORDEN, "
				+ SUBORDEN_ALLOCATION
				+ " as SUBORDEN, "
				+ X_C_Invoice.Table_ID
				+ " as table_id"
				+ " from c_invoice i "
				+ " inner join fact_acct f on (i.c_invoice_id = f.record_id)"
				+ " inner join c_doctype d on (i.c_doctype_id = d.c_doctype_id)"
				+ " where (f.ad_table_id = "
				+ X_C_Invoice.Table_ID
				+ ") and (i.issotrx = 'Y')"
				+ "			and (d.docbasetype = 'ARI') and (d.doctypekey not in ('RCR','RCI','RTR','RTI'))";
		if (getFecha() != null) {
			sql = sql + " and f.dateacct::date >='" + getFecha() + "'::date";
		}
		sql = sql
				+ ")"
				+ "UNION"
				// Compras
				+ "("
				+ " select f.fact_acct_id, f.dateacct, i.c_invoice_id as record_id, CAST(NULL as int) as c_payment_id, CAST(NULL as int) as c_cashline_id, CAST(NULL as int) as c_invoice_credit_id, CAST(NULL as int) as  c_allocationhdr_id, "
				+ ORDEN_COMPRA
				+ " as ORDEN, "
				+ SUBORDEN_ALLOCATION
				+ " as SUBORDEN, "
				+ X_C_Invoice.Table_ID
				+ " as table_id"
				+ " from c_invoice i"
				+ "	inner join fact_acct f	on (i.c_invoice_id = f.record_id)"
				+ "	inner join c_doctype d on (i.c_doctype_id = d.c_doctype_id)"
				+ "	where (f.ad_table_id = "
				+ X_C_Invoice.Table_ID
				+ ") and (i.issotrx = 'N')"
				+ " 	     and (d.docbasetype = 'API') and (d.doctypekey not in ('RCR','RCI','RTR','RTI'))";
		if (getFecha() != null) {
			sql = sql + " and f.dateacct::date >='" + getFecha() + "'::date";
		}
		sql = sql
				+ " ) "
				+ "UNION"
				// Cobros
				+ "("
				+ " select fact_acct_id, dateacct, a.c_allocationhdr_id as record_id, c_payment_id, c_cashline_id, c_invoice_credit_id, a.c_allocationhdr_id, "
				+ ORDEN_COBROS
				+ " as ORDEN, "
				+ SUBORDEN_ALLOCATION
				+ " as SUBORDEN, "
				+ X_C_AllocationHdr.Table_ID
				+ " as table_id"
				+ " from c_allocationline a inner join "
				+ "		("
				+ " 	select i.c_invoice_id from c_invoice i"
				+ " 	inner join fact_acct f on (i.c_invoice_id = f.record_id)"
				+ " 	inner join c_doctype d on (i.c_doctype_id = d.c_doctype_id)"
				+ " 	where (f.ad_table_id = "
				+ X_C_Invoice.Table_ID
				+ ") and (i.issotrx = 'Y')"
				+ " 		   and (d.docbasetype = 'ARI') and (d.doctypekey not in ('RCR','RCI','RTR','RTI'))"
				+ "		) as Ventas"
				+ " on (Ventas.c_invoice_id = a.c_invoice_id )"
				+ " inner join fact_acct fa on (fa.record_id = a.c_allocationhdr_id ) and fa.ad_table_id = "
				+ X_C_AllocationHdr.Table_ID;
		if (getFecha() != null) {
			sql = sql + " and dateacct::date >='" + getFecha() + "'::date";
		}
		sql = sql
				+ " ) "
				+ "UNION"
				// C_Payment, C_Cashline y C_Invoice_Credit que no estén en
				// allocation y que sean cobros.
				// C_payment
				+ getQueryPayment("Y", ORDEN_COBROS)
				+ "UNION"
				// C_Cashline
				+ getQueryCashline(">", ORDEN_COBROS)
				+ "UNION"
				// C_Invoice_Credit
				+ getQueryInvoiceCredit("Y", ORDEN_COBROS)
				+ "UNION"
				// Pagos
				+ "("
				+ " select fact_acct_id, dateacct, a.c_allocationhdr_id as record_id, c_payment_id, c_cashline_id, c_invoice_credit_id, a.c_allocationhdr_id, "
				+ ORDEN_PAGOS
				+ " as ORDEN, "
				+ SUBORDEN_ALLOCATION
				+ " as SUBORDEN, "
				+ X_C_AllocationHdr.Table_ID
				+ " as table_id"
				+ " from c_allocationline a inner join "
				+ "		("
				+ " 	select i.c_invoice_id from c_invoice i"
				+ "		inner join fact_acct f	on (i.c_invoice_id = f.record_id)"
				+ "		inner join c_doctype d on (i.c_doctype_id = d.c_doctype_id)"
				+ "		where (f.ad_table_id = "
				+ X_C_Invoice.Table_ID
				+ ") and (i.issotrx = 'N')"
				+ " 	     and (d.docbasetype = 'API') and (d.doctypekey not in ('RCR','RCI','RTR','RTI'))"
				+ "		) as Ventas"
				+ " on (Ventas.c_invoice_id = a.c_invoice_id )"
				+ " inner join fact_acct fa on (fa.record_id = a.c_allocationhdr_id ) and fa.ad_table_id = "
				+ X_C_AllocationHdr.Table_ID;
		if (getFecha() != null) {
			sql = sql + " and dateacct::date >='" + getFecha() + "'::date";
		}
		sql = sql + " ) " + "UNION"
		// C_Payment, C_Cashline y C_Invoice_Credit que no estén en
		// allocation y que sean pagos.
		// C_payment
				+ getQueryPayment("N", ORDEN_PAGOS) + "UNION"
				// C_Cashline
				+ getQueryCashline("<", ORDEN_PAGOS) + "UNION"
				// C_Invoice_Credit
				+ getQueryInvoiceCredit("N", ORDEN_PAGOS);
		return sql;
	}

	private void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	private Date getFecha() {
		return fecha;
	}

	private String getQueryPayment(String isreceipt, int orden) {
		//Obtengo los payment que pertenecen a pagos o a cobros (allocation) dependiendo de los parámetros
		String sql = "("
				+ "	select fa.fact_acct_id, fa.dateacct, p.c_payment_id as record_id, p.c_payment_id, CAST(NULL as int) as c_cashline_id, CAST(NULL as int) as c_invoice_credit_id, CAST(NULL as int) as c_allocationhdr_id, "
				+ orden
				+ " as ORDEN, "
				+ SUBORDEN_PAYMENT
				+ " as SUBORDEN, "
				+ X_C_Payment.Table_ID
				+ " as table_id"
				+ " from c_payment p"
				+ " inner join fact_acct fa on fa.record_id = p.c_payment_id and fa.ad_table_id = "
				+ X_C_Payment.Table_ID + " where not exists("
				+ "			select c_allocationline_id"
				+ "			from c_allocationline al"
				+ " 		where al.c_payment_id = p.c_payment_id" + " 		)"
				+ " and (p.isreceipt ='" + isreceipt + "')";
		if (getFecha() != null) {
			sql = sql + " and fa.dateacct::date >='" + getFecha() + "'::date";
		}
		sql = sql + " )";
		return sql;
	}

	private String getQueryCashline(String signo, int orden) {
		//Obtengo los cashline que pertenecen a pagos o a cobros (allocation) dependiendo de los parámetros
		String sql = "("
				+ " select fa.fact_acct_id, fa.dateacct, c.c_cashline_id as record_id, CAST(NULL as int) as c_payment_id, c.c_cashline_id, CAST(NULL as int) as c_invoice_credit_id, CAST(NULL as int) as c_allocationhdr_id, "
				+ orden
				+ " as ORDEN, "
				+ SUBORDEN_CASHLINE
				+ " as SUBORDEN, "
				+ X_C_CashLine.Table_ID
				+ " as table_id"
				+ " from c_cashline c"
				+ " inner join fact_acct fa on fa.record_id = c.c_cashline_id and fa.ad_table_id = "
				+ X_C_CashLine.Table_ID + " where not exists("
				+ "			select c_allocationline_id"
				+ "			from c_allocationline al"
				+ " 		where al.c_cashline_id = c.c_cashline_id" + " 		)"
				+ " and (c.amount " + signo + Env.ZERO + ")";
		if (getFecha() != null) {
			sql = sql + " and fa.dateacct::date >='" + getFecha() + "'::date";
		}
		sql = sql + " )";
		return sql;
	}

	private String getQueryInvoiceCredit(String issotrx, int orden) {
		//Obtengo los invoice_credit que pertenecen a pagos o a cobros (allocation) dependiendo de los parámetros
		String sql = "("
				+ "	select fa.fact_acct_id, fa.dateacct, i.c_invoice_id as record_id, CAST(NULL as int) as c_payment_id, CAST(NULL as int) as c_cashline_id, i.c_invoice_id as c_invoice_credit_id, CAST(NULL as int) as c_allocationhdr_id, "
				+ orden
				+ " as ORDEN, "
				+ SUBORDEN_INVOICE_CREDIT
				+ " as SUBORDEN, "
				+ X_C_Invoice.Table_ID
				+ " as table_id"
				+ " from c_invoice i"
				+ " inner join c_doctype d on (i.c_doctype_id = d.c_doctype_id)"
				+ " inner join fact_acct fa on fa.record_id = i.c_invoice_id and fa.ad_table_id = "
				+ X_C_Invoice.Table_ID + " where not exists("
				+ "			select c_allocationline_id"
				+ "			from c_allocationline al"
				+ " 		where al.c_cashline_id = i.c_invoice_id" + " 		)"
				+ " and (i.issotrx = '" + issotrx
				+ "') and (d.docbasetype = 'APC')";
		if (getFecha() != null) {
			sql = sql + " and fa.dateacct::date >='" + getFecha() + "'::date";
		}
		sql = sql + " )";
		return sql;
	}
}
