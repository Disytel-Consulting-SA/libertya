package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MRetSchemaConfig;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.util.Util;

public class RetencionIIBBForRegion extends RetencionIIBB {

	/** Por lugar de entrega de la factura */
	private boolean forDeliveryRegion = false;
	
	/** Listado de facturas iniciales para que quede referencia */
	private List<MInvoice> srcInvoices;
	
	/** Listado de importes iniciales para que quede referencia */
	private List<BigDecimal> srcAmts;
	
	@Override
	public void loadConfig(MRetencionSchema retSchema) {
		super.loadConfig(retSchema);
		// Por lugar de entrega
		setForDeliveryRegion(getParamValueString(
				MRetSchemaConfig.NAME_PorLugarDeEntrega, "N").equals("Y"));
	}

	@Override
	public BigDecimal getPayNetAmt() {
		changeInvoicesListByRegion();
		return super.getPayNetAmt();
	}
	
	/**
	 * Actualiza las listas de importes y facturas quedando solo con las que
	 * pertenecen a la misma región
	 */
	protected void changeInvoicesListByRegion(){
		List<MInvoice> sameRegionInvoices = new ArrayList<MInvoice>();
		List<BigDecimal> sameRegionAmts = getInvoiceList().size() > 0 ? new ArrayList<BigDecimal>() : getAmountList();
		boolean existsRetSchemaRegion = !Util.isEmpty(getRetencionSchema().getC_Region_ID(), true);
		for (int i = 0; i < getInvoiceList().size(); i++) {
			MInvoice invoice = getInvoiceList().get(i);
			if (existsRetSchemaRegion
					&& getRetencionSchema().getC_Region_ID() == invoice
							.getC_Region_Delivery_ID()) {
				sameRegionInvoices.add(invoice);
				sameRegionAmts.add(getAmountList().get(i));
			}
		}
		setSrcInvoices(getInvoiceList());
		setSrcAmts(getAmountList());
		setInvoiceList(sameRegionInvoices);
		setAmountList(sameRegionAmts);
	}
	
	@Override
	public boolean clearAll() {
		getSrcAmts().clear();
		getSrcInvoices().clear();
		return super.clearAll();
	}
	
	protected boolean isForDeliveryRegion() {
		return forDeliveryRegion;
	}

	protected void setForDeliveryRegion(boolean forDeliveryRegion) {
		this.forDeliveryRegion = forDeliveryRegion;
	}

	protected List<MInvoice> getSrcInvoices() {
		return srcInvoices;
	}

	protected void setSrcInvoices(List<MInvoice> srcInvoices) {
		this.srcInvoices = srcInvoices;
	}

	protected List<BigDecimal> getSrcAmts() {
		return srcAmts;
	}

	protected void setSrcAmts(List<BigDecimal> srcAmts) {
		this.srcAmts = srcAmts;
	}

}
