package org.openXpertya.recovery;

import java.util.Properties;

/**
 * <ul>Los recuperos son medios de cobro/pago, por lo pronto el sistema cuenta con los siguientes:
 * <li>- Cobro/Pago</li>
 * <li>- Efectivo</li>
 * <li>- Crédito</li>
 * </ul>
 */
public interface IRecoverySource {
	public Integer getPaymentRecoveryID();
	public Integer getCashLineRecoveryID();
	public Integer getCreditRecoveryID();
	public Properties getCtx();
	public String getTrxName();
}
