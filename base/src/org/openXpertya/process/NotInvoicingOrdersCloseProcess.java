package org.openXpertya.process;

import java.sql.Timestamp;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MOrder;
import org.openXpertya.util.TimeUtil;

public class NotInvoicingOrdersCloseProcess extends SvrProcess {
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Transacción de Ventas */
	private boolean isSOTrx = true;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("DateTo")){
				setDateTo((Timestamp)para[i].getParameter());
			}
			else if(name.equals("IsSOTrx")){
				setSOTrx(((String)para[i].getParameter()).equals("Y"));
			}
		}
	}

	public String getAditionalWhereClause(){
		return " AND NOT EXISTS (SELECT C_Invoice.C_Invoice_ID FROM C_Invoice WHERE C_Invoice.C_Order_ID = C_Order.C_Order_ID AND C_Invoice.DocStatus IN ('CO','CL')) ";
	}
	
	@Override
	protected String doIt() throws Exception {
		DocumentCompleteProcess dcp = new DocumentCompleteProcess(getCtx(),
				MDocType.getDocType(getCtx(),
						isSOTrx() ? MDocType.DOCTYPE_StandarOrder
								: MDocType.DOCTYPE_PurchaseOrder, get_TrxName()),
				MOrder.DOCACTION_Close, null,
				TimeUtil.addDays(getDateTo(), -1), getAditionalWhereClause(),
				get_TrxName());
		return dcp.start();
	}

	protected Timestamp getDateTo() {
		return dateTo;
	}

	protected void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	protected boolean isSOTrx() {
		return isSOTrx;
	}

	protected void setSOTrx(boolean isSOTrx) {
		this.isSOTrx = isSOTrx;
	}

}
