package org.openXpertya.JasperReport.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProduct;

public class WarehouseDeliverDocumentDataSource implements OXPJasperDataSource {

	/** Pedido */
	private MOrder order;
	
	/** Línea de pedido Actual */
	private MOrderLine currentOrderLine;
	
	/** Índice de la línea actual */
	private Integer currentOrderLineIndex;
	
	/** Lista de líneas de pedido */
	private List<MOrderLine> orderLines;
	
	/** Contexto */
	private Properties ctx;
	
	/** Trx actual */
	private String trxName;
	
	public WarehouseDeliverDocumentDataSource(Properties ctx, MOrder order, MInvoice invoice, String trxName){
		setCtx(ctx);
		setTrxName(trxName);
		setOrder(order);
		setCurrentOrderLineIndex(0);
		setOrderLines(new ArrayList<MOrderLine>());
	}
	
	@Override
	public void loadData() throws RuntimeException {
		// Itero por todas las líneas y me quedo con las líneas del pedido que
		// se retiran por depósito y tienen cantidades pendiente de entrega
		for (MOrderLine orderLine : getOrder().getLines()) {
			if (orderLine.isDeliverDocumentPrintable()) {
				getOrderLines().add(orderLine);
			}
		}
	}
	
	@Override
	public Object getFieldValue(JRField arg0) throws JRException {
		Object value = null;
		if(arg0.getName().equalsIgnoreCase("QTY")){
			value = getCurrentOrderLine().getPendingDeliveredQty();
		}
		else if(arg0.getName().equalsIgnoreCase("PRODUCT_NAME")){
			value = getCurrentOrderLine().getProductName();
		}
		else if(arg0.getName().equalsIgnoreCase("PRODUCT_VALUE")){
			value = getCurrentOrderLine().getProductValue();
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if(getCurrentOrderLineIndex() < getOrderLines().size()){
			setCurrentOrderLine(getOrderLines().get(getCurrentOrderLineIndex()));
			setCurrentOrderLineIndex(getCurrentOrderLineIndex()+1);
			return true;
		}
		return false;
	}

	public void setOrder(MOrder order) {
		this.order = order;
	}

	public MOrder getOrder() {
		return order;
	}

	public void setCurrentOrderLine(MOrderLine currentOrderLine) {
		this.currentOrderLine = currentOrderLine;
	}

	public MOrderLine getCurrentOrderLine() {
		return currentOrderLine;
	}

	public void setOrderLines(List<MOrderLine> orderLines) {
		this.orderLines = orderLines;
	}

	public List<MOrderLine> getOrderLines() {
		return orderLines;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setCurrentOrderLineIndex(Integer currentOrderLineIndex) {
		this.currentOrderLineIndex = currentOrderLineIndex;
	}

	public Integer getCurrentOrderLineIndex() {
		return currentOrderLineIndex;
	}

}
