package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.OrdenPagoDataSource;
import org.openXpertya.JasperReport.DataSource.ReciboClienteDataSource;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MLocation;

public class LaunchReciboCliente extends LaunchOrdenPago {

	@Override
	protected OrdenPagoDataSource getDataSource() {
		return new ReciboClienteDataSource(getAllocationHdrID());
	}

	@Override
	protected void loadReportParameters(MJasperReport jasperWrapper) {
		// Parámetros de OP
		super.loadReportParameters(jasperWrapper);
		
		// Parámetros específicos de RC
		MAllocationHdr rc = getAllocationHdr();
		
		// Dirección de la entidad comercial.
		String bpLoc = "N/A"; // No disponible por defecto.
		MBPartner bPartner = new MBPartner(getCtx(), rc.getC_BPartner_ID(), get_TrxName());
		MBPartnerLocation[] bpLocations = bPartner.getLocations(false);
		// Se utiliza la primer dirección configurada.
		if (bpLocations.length > 0) {
			MLocation location = bpLocations[0].getLocation(false);
			bpLoc = location.toString();
		}
		
		jasperWrapper.addParameter("BPARTNER_LOCATION", bpLoc);
	}

	
}
