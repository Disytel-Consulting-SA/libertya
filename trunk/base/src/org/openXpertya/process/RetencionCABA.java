package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MRetSchemaConfig;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.util.Util;

public class RetencionCABA extends RetencionIIBB {

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
		return getPayNetAmt(getSameDeliveryRegionInvoices(), getAmountList());
	}
	
	
	private List<MInvoice> getSameDeliveryRegionInvoices(){
		List<MInvoice> sameRegionInvoices = new ArrayList<MInvoice>();
		boolean existsRetSchemaRegion = !Util.isEmpty(getRetencionSchema().getC_Region_ID(), true);
		for (MInvoice invoice : getInvoiceList()) {
			if (existsRetSchemaRegion
					&& getRetencionSchema().getC_Region_ID() == invoice
							.getC_Region_Delivery_ID()) {
				sameRegionInvoices.add(invoice);
			}
		}
		return sameRegionInvoices;
	}
	
	
	protected boolean isForDeliveryRegion() {
		return forDeliveryRegion;
	}

	protected void setForDeliveryRegion(boolean forDeliveryRegion) {
		this.forDeliveryRegion = forDeliveryRegion;
	}

}
