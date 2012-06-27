package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;


public class GeneratorPercepciones {

	
	public static void deletePercepciones(Integer invoiceID, String trxName) throws Exception{
		String sql = "DELETE FROM c_invoicetax WHERE c_invoice_id = ? AND c_tax_id IN (SELECT c_tax_id FROM c_tax WHERE ispercepcion = 'Y')";
		PreparedStatement ps = DB.prepareStatement(sql, trxName);
		ps.setInt(1, invoiceID);
		ps.executeUpdate();
	}
	
	public static List<MTax> getApplyPercepciones(Properties ctx, Integer orgID, String trxName) throws Exception{
		List<MTax> percepciones = new ArrayList<MTax>();
		String sql = "SELECT * FROM c_tax WHERE c_tax_id IN (SELECT distinct c_tax_id FROM ad_org_percepcion WHERE ad_org_id = ? AND isactive = 'Y')";
		PreparedStatement ps = DB.prepareStatement(sql, trxName);
		ps.setInt(1, orgID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			percepciones.add(new MTax(ctx, rs, trxName));
		}
		return percepciones;
	}
	
	public static void recalculatePercepciones(MInvoice invoice) throws Exception{
		// Eliminar las percepciones de la factura
		deletePercepciones(invoice.getID(), invoice.get_TrxName());
		// Calcular las percepciones para esta factura
		calculatePercepciones(invoice); 
	}
	
	public static void calculatePercepciones(MInvoice invoice) throws Exception{
		// Poner todas las percepciones en 0
		String sql = "UPDATE c_invoicetax SET taxamt = 0, taxbaseamt = 0 WHERE c_invoice_id = "
				+ invoice.getID()
				+ " AND c_tax_id IN (SELECT distinct c_tax_id FROM ad_org_percepcion WHERE ad_org_id = "
				+ invoice.getAD_Org_ID()+")";
		DB.executeUpdate(sql, invoice.get_TrxName());
		// Obtener las percepciones a percibir de la organización
		List<MTax> percepciones = getApplyPercepciones(invoice.getCtx(),
				invoice.getAD_Org_ID(), invoice.get_TrxName());
		// Recorrer las percepciones y agregarlas a las facturas
		BigDecimal exencionRate, percepcionAmt;
		BigDecimal invoiceNetTotalAmt = invoice.getTotalLinesNetWithoutDocumentDiscount();
		Integer scale = MCurrency.getStdPrecision(invoice.getCtx(),
				invoice.getC_Currency_ID(), invoice.get_TrxName());
		MInvoiceTax invoiceTax;
		for (MTax percepcion : percepciones) {
			// Verificar si la entidad comercial de la factura tiene una
			// exención en la fecha de la factura
			// La tasa de exención es 1 - porcentaje de exención/100
			exencionRate = MBPartner.getPercepcionExencionMultiplierRate(
					invoice.getC_BPartner_ID(), percepcion.getID(),
					invoice.getDateInvoiced(), scale, invoice.get_TrxName());
			// Calcular el monto de percepción
			percepcionAmt = percepcion.calculateTax(invoiceNetTotalAmt, false,
					scale).multiply(exencionRate);
			// Verificar si existe ese impuesto cargado en la factura, si es así
			// actualizarlo, sino crear uno nuevo
			invoiceTax = MInvoiceTax.get(invoice.getCtx(), invoice.getID(),
					percepcion.getID(), invoice.get_TrxName());
			// Si el monto de percepción es 0 y existe el impuesto agregado a la
			// factura entonces se elimina
			if(percepcionAmt.compareTo(BigDecimal.ZERO) == 0){
				if(invoiceTax != null){
					invoiceTax.delete(true);
				}
			}
			// Si el monto es distinto de 0 y existe el impuesto agregado,
			// entonces lo actualizo. En el caso que no exista ninguno se crea 
			else{
				// Si no existe ninguna, la agrego
				if(invoiceTax == null){
					invoiceTax = new MInvoiceTax(invoice.getCtx(), 0,
							invoice.get_TrxName());
				}
				invoiceTax.setC_Invoice_ID(invoice.getID());
				invoiceTax.setC_Tax_ID(percepcion.getID());
				invoiceTax.setTaxAmt(percepcionAmt);
				invoiceTax.setTaxBaseAmt(invoiceNetTotalAmt);
				if(!invoiceTax.save()){
					throw new Exception("ERROR updating percepcion invoice tax");
				}
			}
		}
	}
	
	public GeneratorPercepciones() {
		// TODO Auto-generated constructor stub
	}

}
