package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCashDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCheckDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCreditNoteDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCuentaCorrienteDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCuponDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresTransferDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasCurrentAccountPaymentsDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasDocTypeBaseKeyDataSource;


public class LaunchDeclaracionValoresXOrg extends LaunchDeclaracionValores {

	public LaunchDeclaracionValoresXOrg() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getTitle(){
		return "Declaracion de Valores por Organización";
	}
	
	@Override
	protected void addSubreports() throws Exception{
		// Cupon
		DeclaracionValoresCuponDataSource cuponDS = new DeclaracionValoresCuponDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
		// Venta
		// La venta pasada como parámetro se debe sacar del resumen de ventas
		ResumenVentasDocTypeBaseKeyDataSource ventaDS = new ResumenVentasDocTypeBaseKeyDataSource(
				get_TrxName(), getCtx(), getValoresDTO().getOrgID(),
				getValoresDTO().getDateFrom(), getValoresDTO().getDateTo(),
				null, getValoresDTO().getUserID(), false, false);
		// Efectivo
		DeclaracionValoresCashDataSource cashDS = new DeclaracionValoresCashDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
		// Cheque
		DeclaracionValoresCheckDataSource checkDS = new DeclaracionValoresCheckDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
		// Transferencia
		DeclaracionValoresTransferDataSource transferDS = new DeclaracionValoresTransferDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
		// NC
		DeclaracionValoresCreditNoteDataSource creditNoteDS = new DeclaracionValoresCreditNoteDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
		// Cta Cte
		DeclaracionValoresCuentaCorrienteDataSource ccDS = new DeclaracionValoresCuentaCorrienteDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
		// Cobranza
		ResumenVentasCurrentAccountPaymentsDataSource capDS = new ResumenVentasCurrentAccountPaymentsDataSource(
				get_TrxName(), getCtx(), getValoresDTO().getOrgID(),
				getValoresDTO().getDateFrom(), getValoresDTO().getDateTo(),
				null, getValoresDTO().getUserID(), false, false);
		addReportParameter("TOTAL_CUPON", cuponDS != null?cuponDS.getTotalAmt():null);
		addReportParameter("TOTAL_VENTA", ventaDS != null?ventaDS.getTotalAmt():null);
		addReportParameter("TOTAL_EFECTIVO", cashDS != null?cashDS.getTotalAmt():null);
		addReportParameter("TOTAL_CHEQUE", checkDS != null?checkDS.getTotalAmt():null);
		addReportParameter("TOTAL_TRANSFER", transferDS != null?transferDS.getTotalAmt():null);
		addReportParameter("TOTAL_NC", creditNoteDS != null?creditNoteDS.getTotalAmt():null);
		addReportParameter("TOTAL_CC", ccDS != null?ccDS.getTotalAmt():null);
		addReportParameter("TOTAL_COBRANZA", capDS != null?capDS.getTotalAmt():null);
	}
}
