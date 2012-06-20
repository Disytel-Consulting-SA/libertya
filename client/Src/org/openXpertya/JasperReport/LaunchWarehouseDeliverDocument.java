package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.WarehouseDeliverDocumentDataSource;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class LaunchWarehouseDeliverDocument extends JasperReportLaunch {

	/** Pedido */
	private MOrder order;
	
	/** Factura */
	private MInvoice invoice;
	
	@Override
	protected void loadReportParameters() throws Exception {
		if(getOrder() == null){
			throw new Exception(Msg.getMsg(getCtx(), "OrderIsMandatory"));
		}
		addReportParameter("ORDER_DOCUMENT_NO", getOrderDocumentNo());
		addReportParameter("INVOICE_DOCUMENT_NO", getInvoiceDocumentNo());
		addReportParameter("WAREHOUSE_NAME", getWarehouseName());
	}
	
	protected String getOrderDocumentNo(){
		return getOrder().getDocumentNo();
	}
	
	protected String getInvoiceDocumentNo(){
		return getInvoice() == null?null:getInvoice().getDocumentNo();
	}
	
	protected String getWarehouseName(){
		return JasperReportsUtil.getWarehouseName(getCtx(), getOrder()
				.getM_Warehouse_ID(), get_TrxName());
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new WarehouseDeliverDocumentDataSource(getCtx(), getOrder(), getInvoice(), get_TrxName());
	}

	protected void setOrder(MOrder order) {
		this.order = order;
	}

	protected MOrder getOrder() {
		if(order == null){
			MOrder the_order = null;
			// 1) Obtengo el pedido desde el parámetro
			if(getParameterValue("C_Order_ID") != null){
				the_order = new MOrder(getCtx(),
						(Integer) getParameterValue("C_Order_ID"),
						get_TrxName());
			}
			// 2) Si no existe, entonces lo busco por la factura
			if (the_order == null && getInvoice() != null
					&& !Util.isEmpty(getInvoice().getC_Order_ID(), true)) {
				the_order = new MOrder(getCtx(), getInvoice().getC_Order_ID(),
						get_TrxName());
			}
			setOrder(the_order);
		}
		return order;
	}

	public void setInvoice(MInvoice invoice) {
		this.invoice = invoice;
	}

	public MInvoice getInvoice() {
		if(invoice == null){
			if(getParameterValue("C_Invoice_ID") != null){
				MInvoice the_invoice = new MInvoice(getCtx(),
						(Integer) getParameterValue("C_Invoice_ID"),
						get_TrxName());
				setInvoice(the_invoice);
			}
		}
		return invoice;
	}

}
