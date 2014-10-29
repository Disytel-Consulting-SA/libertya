package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCashDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCheckDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCreditNoteDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCuentaCorrienteDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCuponDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresTransferDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasCurrentAccountPaymentsDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasDocTypeBaseKeyDataSource;
import org.openXpertya.model.MDocType;


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
		DeclaracionValoresCuponDataSource cuponDS = getCuponDS();
		// Venta
		// La venta pasada como parámetro se debe sacar del resumen de ventas
		ResumenVentasDocTypeBaseKeyDataSource ventaDS = getVentaDS();
		// Efectivo
		DeclaracionValoresCashDataSource cashDS = getCashDS();
		// Cheque
		DeclaracionValoresCheckDataSource checkDS = getCheckDS();
		// Transferencia
		DeclaracionValoresTransferDataSource transferDS = getTransferDS();
		// NC
		DeclaracionValoresCreditNoteDataSource creditNoteDS = getCreditNoteDS();
		// Cta Cte
		DeclaracionValoresCuentaCorrienteDataSource ccDS = getCurrentAccountDS();
		// Cobranza
		ResumenVentasCurrentAccountPaymentsDataSource capDS = getCurrentAccountPaymentsDS();
		// Agregar subreportes
		addReportParameter("TOTAL_CUPON", cuponDS != null?cuponDS.getTotalAmt():null);
		addReportParameter(
				"TOTAL_VENTA",
				ventaDS != null ? ventaDS
						.getTotalAmt(MDocType.DOCTYPE_CustomerInvoice) : null);
		addReportParameter("TOTAL_EFECTIVO", cashDS != null?cashDS.getTotalAmt():null);
		addReportParameter("TOTAL_CHEQUE", checkDS != null?checkDS.getTotalAmt():null);
		addReportParameter("TOTAL_TRANSFER", transferDS != null?transferDS.getTotalAmt():null);
		addReportParameter("TOTAL_NC", creditNoteDS != null?creditNoteDS.getTotalAmt():null);
		addReportParameter("TOTAL_CC", ccDS != null?ccDS.getTotalAmt():null);
		addReportParameter("TOTAL_COBRANZA", capDS != null?capDS.getTotalAmt():null);
	}
	
	
	protected DeclaracionValoresCuponDataSource getCuponDS(){
		return new DeclaracionValoresCuponDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
	}
	
	protected ResumenVentasDocTypeBaseKeyDataSource getVentaDS(){
		return new ResumenVentasDocTypeBaseKeyDataSource(
				get_TrxName(), getCtx(), getValoresDTO().getOrgID(),
				getValoresDTO().getDateFrom(), getValoresDTO().getDateTo(),
				null, getValoresDTO().getUserID(), false, false);
	}
	
	protected DeclaracionValoresCashDataSource getCashDS(){
		return new DeclaracionValoresCashDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
	}
	
	protected DeclaracionValoresCheckDataSource getCheckDS(){
		return new DeclaracionValoresCheckDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
	}
	
	protected DeclaracionValoresTransferDataSource getTransferDS(){
		return new DeclaracionValoresTransferDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
	}
	
	protected DeclaracionValoresCreditNoteDataSource getCreditNoteDS(){
		return new DeclaracionValoresCreditNoteDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
	}
	
	protected DeclaracionValoresCuentaCorrienteDataSource getCurrentAccountDS(){
		return new DeclaracionValoresCuentaCorrienteDataSource(
				getCtx(), getValoresDTO(), getSelect(), getGroupBy(),
				getOrderBy(), get_TrxName());
	}
	
	protected ResumenVentasCurrentAccountPaymentsDataSource getCurrentAccountPaymentsDS(){
		return new ResumenVentasCurrentAccountPaymentsDataSource(
				get_TrxName(), getCtx(), getValoresDTO().getOrgID(),
				getValoresDTO().getDateFrom(), getValoresDTO().getDateTo(),
				null, getValoresDTO().getUserID(), false, false);
	}
}
