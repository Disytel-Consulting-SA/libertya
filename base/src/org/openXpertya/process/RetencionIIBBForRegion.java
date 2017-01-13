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
		setInvoiceList(sameRegionInvoices);
		setAmountList(sameRegionAmts);
	}
	
	protected boolean isForDeliveryRegion() {
		return forDeliveryRegion;
	}

	protected void setForDeliveryRegion(boolean forDeliveryRegion) {
		this.forDeliveryRegion = forDeliveryRegion;
	}

}
