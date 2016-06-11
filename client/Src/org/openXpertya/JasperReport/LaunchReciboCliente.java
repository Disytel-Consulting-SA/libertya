package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.JasperReport.DataSource.OrdenPagoDataSource;
import org.openXpertya.JasperReport.DataSource.ReciboClienteDataSource;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MLocation;
import org.openXpertya.util.DB;

public class LaunchReciboCliente extends LaunchOrdenPago {

	@Override
	protected OrdenPagoDataSource getDataSource() {
		return new ReciboClienteDataSource(getAllocationHdrID());
	}

	/**
	 * @return Retorna el MJasperReport del subreporte de otros pagos.
	 */
	protected MJasperReport getOtherPaymentsSubreport() throws Exception {
		return getJasperReport("ReciboCliente-OtrosMedios");
	}
	
	/**
	 * @return Retorna el MJasperReport del subreporte de notas de crédito.
	 */
	protected MJasperReport getCreditNotesSubreport() throws Exception {
		return getJasperReport("ReciboCliente-NotasDeCredito");
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

	/**
	 * Retorna el monto total involucrado en Notas de Credito para este Allocation
	 * Reutiliza el query del DataSource a fin de evitar duplicidad del query
	 */
	@Override
	protected BigDecimal getCreditsAmount(MAllocationHdr allocation) {
		try {
			BigDecimal retValue = BigDecimal.ZERO;
			PreparedStatement pstmt = DB.prepareStatement(OrdenPagoDataSource.getCreditNotesQuery(), get_TrxName());
			pstmt.setInt(1, allocation.getC_AllocationHdr_ID());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				retValue = retValue.add(rs.getBigDecimal("Amount"));
			return retValue;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected String getUrlImagePreferenceName(){
		return "URL_IMAGE_RC";
	}
}
