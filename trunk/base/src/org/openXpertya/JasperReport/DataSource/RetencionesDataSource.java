package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MRegion;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.MRetencionType;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class RetencionesDataSource implements JRDataSource {

	/** Allocation */
	private MAllocationHdr allocationHdr;
	/** Neto */
	private BigDecimal allocationNetAmt;
	/**  */
	/** Nombre de Compañía */
	private String clientName;
	/** CUIT de Compañía */
	private String clientCUIT;
	/** Contexto */
	private Properties ctx;
	/** Retenciones */
	private List<RetencionDTO> retenciones;
	/** Índice de retención actual */
	private Integer currentRetencionIndex = -1;
	/** Retención actual */
	private RetencionDTO currentRetencion;
	
	private OrdenPagoDataSource ordenPagoDS;
	
	public RetencionesDataSource() {
		// TODO Auto-generated constructor stub
	}
	
	public RetencionesDataSource(Properties ctx, MAllocationHdr allocationHdr) {
		setCtx(ctx);
		setAllocationHdr(allocationHdr);
	}

	@Override
	public Object getFieldValue(JRField arg0) throws JRException {
		String name = arg0.getName().toUpperCase();
		Object value = null;
		if(name.equalsIgnoreCase("FECHA")){
			value = currentRetencion.invoice.getDateInvoiced();
		}
		else if(name.equalsIgnoreCase("DOCUMENTNO")){
			value = currentRetencion.invoice.getDocumentNo();
		}
		else if(name.equalsIgnoreCase("RAZONSOCIAL")){
			value = JasperReportsUtil.coalesce(currentRetencion.invoice.getNombreCli(), currentRetencion.bp.getName());
		}
		else if(name.equalsIgnoreCase("DIRECCION")){
			value = currentRetencion.retencionLocation;
		}
		else if(name.equalsIgnoreCase("CUIT")){
			value = JasperReportsUtil.coalesce(currentRetencion.bp.getTaxID(), "");
		}
		else if(name.equalsIgnoreCase("NRO_OC")){
			value = currentRetencion.orderDocumentNo;
		}
		else if(name.equalsIgnoreCase("DIRECCION_ORG")){
			value = currentRetencion.orgLocation;
		}
		else if(name.equalsIgnoreCase("CLIENT")){
			value = clientName;
		}
		else if(name.equalsIgnoreCase("CLIENT_CUIT")){
			value = clientCUIT;
		}
		else if(name.equalsIgnoreCase("RET_SCHEMA_NAME")){
			value = currentRetencion.retSchema.getName();
		}
		else if(name.equalsIgnoreCase("RET_RETENTION_TYPE_NAME")){
			value = currentRetencion.retType.getName();
		}
		else if(name.equalsIgnoreCase("RET_ALLOC_AMOUNT")){
			value = allocationNetAmt;
		}
		else if(name.equalsIgnoreCase("RET_AMOUNT")){
			value = currentRetencion.retInvoice.getamt_retenc();
		}
		else if(name.equalsIgnoreCase("RET_ALLOC_INVOICES")){
			value = currentRetencion.srcInvoices;
		}
		
		return value;
	}

	@Override
	public boolean next() throws JRException {
		currentRetencionIndex++;
		if(currentRetencionIndex < retenciones.size()){
			currentRetencion = retenciones.get(currentRetencionIndex);
			return true;
		}
		return false;
	}

	public void loadData() {
		// Se agrega el/los datasource del subreporte
		String sql = "select distinct m_retencion_invoice_id "
				+ " from m_retencion_invoice r "
				+ " where r.c_allocationhdr_id=" +getAllocationHdr().getID()
				+ " order by m_retencion_invoice_id ";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			pstmt = DB.prepareStatement(sql);
			rs = pstmt.executeQuery();
			allocationNetAmt = MAllocationHdr.getPayNetAmt(getCtx(), getAllocationHdr(), getAllocationHdr().get_TrxName());
			retenciones = new ArrayList<RetencionesDataSource.RetencionDTO>();
			RetencionDTO retencion;
			while (rs.next()) {
				retencion = new RetencionDTO();
				retencion.retInvoice = new X_M_Retencion_Invoice(getCtx(), rs.getInt("m_retencion_invoice_id"), null);
				
				MClient client = JasperReportsUtil.getClient(getCtx(), retencion.retInvoice.getAD_Client_ID());
				clientName = client.getName();
				clientCUIT = client.getCUIT(retencion.retInvoice.getAD_Org_ID());
	
				retencion.invoice = new MInvoice(Env.getCtx(), retencion.retInvoice.getC_Invoice_ID(), null);
				retencion.bp = new MBPartner(getCtx(), retencion.invoice.getC_BPartner_ID(), null);
				retencion.retSchema = new MRetencionSchema(getCtx(), retencion.retInvoice.getC_RetencionSchema_ID(), null);
				retencion.retType = new MRetencionType(getCtx(), retencion.retSchema.getC_RetencionType_ID(), null);
				retencion.srcInvoices = get_Retencion_Invoices(getAllocationHdr());
				retencion.orderDocumentNo = "";
				if(!Util.isEmpty(retencion.invoice.getC_Order_ID(), true)){
					retencion.orderDocumentNo = new MOrder(getCtx(), retencion.invoice.getC_Order_ID(), null)
							.getDocumentNo();
				}
							
				MBPartnerLocation bpLocation = new MBPartnerLocation(getCtx(), retencion.invoice.getC_BPartner_Location_ID(), null);
				MLocation location = new MLocation(getCtx(), bpLocation.getC_Location_ID(), null);
				
				retencion.retencionLocation = (String)JasperReportsUtil.coalesce(retencion.invoice.getInvoice_Adress(), JasperReportsUtil.formatLocation(getCtx(), location.getID(), false));
				
				// Direccción de la Organización asociada a la Factura
				MOrg org = MOrg.get(getCtx(), retencion.invoice.getAD_Org_ID());
				MLocation locationOrg = new MLocation(getCtx(), org.getInfo().getC_Location_ID(), null);
				MRegion regionOrg = null;
				if (locationOrg.getC_Region_ID() > 0)
					regionOrg = new MRegion(getCtx(), locationOrg.getC_Region_ID(), null);
				
				retencion.orgLocation = JasperReportsUtil.coalesce(locationOrg.getAddress1(),
						"")
						+ ". "
						+ JasperReportsUtil.coalesce(
								locationOrg.getCity(), "")
						+ ". ("
						+ JasperReportsUtil.coalesce(
								locationOrg.getPostal(), "")
						+ "). "
						+ JasperReportsUtil.coalesce(
								regionOrg == null ? "" : regionOrg
										.getName(), "");
				
				retenciones.add(retencion);
			}
		} catch(Exception e){
			e.printStackTrace();
		} try {
			if(pstmt != null)pstmt.close();
			if(rs != null)rs.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * El método retorna todos los DocumentNo. de las facturas asociadas al
	 * recibo recibido por parámetro.
	 */
	private String get_Retencion_Invoices(MAllocationHdr allocation) {
		String retencionInvoices = null;
		// Si es OPA o RCA no posee comprobantes
		if(!allocation.isAdvanced()){
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				stmt = DB.prepareStatement("SELECT DISTINCT factura "
										+ "FROM C_Allocation_Detail_V "
										+ "WHERE C_AllocationHdr_ID = ? "
										+ "ORDER BY factura DESC");
				stmt.setInt(1, allocation.getC_AllocationHdr_ID());
				rs = stmt.executeQuery();
	
				retencionInvoices = "- ";
				while (rs.next()) {
					retencionInvoices = retencionInvoices.concat(rs.getString(1).concat(" - "));
				}
	
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				try {
					if(stmt != null)stmt.close();
					if(rs != null)rs.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return retencionInvoices;
	}

	protected MAllocationHdr getAllocationHdr() {
		return allocationHdr;
	}

	protected void setAllocationHdr(MAllocationHdr allocationHdr) {
		this.allocationHdr = allocationHdr;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public void setOrdenPagoDataSource(OrdenPagoDataSource opds){
		ordenPagoDS = opds;
	}
	
	public OrdenPagoDataSource getOrdenPagoDataSource(){
		return ordenPagoDS;
	}
	
	private class RetencionDTO{
		protected MInvoice invoice;
		protected MBPartner bp;
		protected String retencionLocation;
		protected X_M_Retencion_Invoice retInvoice;
		protected String orderDocumentNo;
		protected MRetencionSchema retSchema;
		protected MRetencionType retType;
		protected String orgLocation;
		protected String srcInvoices;
	}
	
}
