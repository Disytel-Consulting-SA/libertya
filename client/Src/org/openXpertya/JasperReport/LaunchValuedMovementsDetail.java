package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.ValuedMovementsDetailDataSource;

public class LaunchValuedMovementsDetail extends LaunchValuedMovements {

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ValuedMovementsDetailDataSource(getCtx(), getOrgID(),
				getDateFrom(), getDateTo(), getWarehouseID(),
				getPriceListVersionID(), getChargeID(), get_TrxName());
	}

}
