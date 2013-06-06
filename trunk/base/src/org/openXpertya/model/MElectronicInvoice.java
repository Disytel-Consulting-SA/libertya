package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MElectronicInvoice extends X_E_ElectronicInvoice {

	public static final String impuestoIIBB = "IIBB";
	
	public MElectronicInvoice(Properties ctx, int E_ElectronicInvoice_ID,	String trxName) {
		super(ctx, E_ElectronicInvoice_ID, trxName);
	}

	public MElectronicInvoice(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public void createHdr(boolean isFiscal, MInvoice inv) throws Exception{
		String taxIdType = null;
		String name = null;
		int c_Categoria_Iva_ID = 0;	
		String iibb = null;
		PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
			ps = DB.prepareStatement("SELECT TaxIdType, Name, C_Categoria_Iva_ID, IIBB FROM C_BPartner WHERE C_BPartner_ID = ?", get_TrxName());
			ps.setInt(1, inv.getC_BPartner_ID());
			rs = ps.executeQuery();
			if (rs.next()){
				taxIdType = rs.getString("TaxIdType");
				name = rs.getString("Name");
				c_Categoria_Iva_ID = rs.getInt("C_Categoria_Iva_ID");	
				iibb = rs.getString("IIBB");
			}
		}
		catch (Exception e) {
			log.severe("Error finding c_bpartner. Error: " + e.getMessage());
		} finally{
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}
    	MDocType doctype = new MDocType(getCtx(), inv.getC_DocType_ID(), get_TrxName());
		setDateInvoiced(inv.getDateInvoiced());
		setTipo_Comprobante(getRefTablaComprobantes(inv.getC_DocType_ID()));	// Según tabla 1
		setIsFiscal(isFiscal);
		setPuntoDeVenta(inv.getPuntoDeVenta());
		setNumeroDeDocumento(inv.getNumeroDeDocumento());
		setCant_Hojas(1); 														// Se deja en 1 según se vio en una exportación WEB de la AFIP
		setDoc_Identificatorio_Comprador(Integer.parseInt(getInvoiceBPartnerTaxIdType(taxIdType))); // Según tabla 2
		setIdentif_Comprador(getCuit(inv.getCUIT()));							// CUIT del cliente según se vio en una exportación WEB de la AFIP
		setIdentif_Vendedor("0");												// No se usa
		setName(name);
		setGrandTotal(inv.getGrandTotal());
		setTaxBaseAmt(getInvoiceTaxBaseAmt(inv.getC_Invoice_ID())); 			// Suma los taxbaseamt de la factura
		setTotalLines(inv.getNetAmount());
		setTaxAmt(getInvoiceTaxAmt(inv.getC_Invoice_ID()));						// suma los taxamt de la factura
		setTipo_Responsable(getRefTablaTipoResponsable(c_Categoria_Iva_ID));// Según tabla 4
		setCod_Moneda(getRefTablaMoneda(inv.getC_Currency_ID()));	  			// sacar de c_currency	
		setMultiplyRate(new BigDecimal(1));										// No se usa
		setCant_Alicuotas_Iva(getInvoiceCantAlicuotas(inv.getC_Invoice_ID()));	// Campo calculado
		setCAI(doctype.getCAI());
		setOperacionesExentas(getInvoiceTaxAmtTaxExempt(inv.getC_Invoice_ID()));
		//Si el impuesto liquidado (campo 15) es igual a cero (0) y el importe total de conceptos que no integran el precio neto gravado (campo 13) es distinto de cero, se deberá completar 
		if ((getTaxAmt().compareTo(BigDecimal.ZERO) == 0) && (getOperacionesExentas().compareTo(BigDecimal.ZERO) == 0)){
			if (getTipo_Responsable() == 9){
				setCod_Operacion("X");													// Campo calculado				
			}
		}
		setRNI(BigDecimal.ZERO);
		setTransporte(BigDecimal.ZERO);
		setPercepcionesIIBB(getImpuestoIIBB(getRefTablaImpuestosIIBB(),inv.getC_Invoice_ID()));
		
		setImportePercepciones(BigDecimal.ZERO);										// No se usa
		setImpuestosMunicipales(BigDecimal.ZERO);										// No se usa
		setImpuestosInternos(BigDecimal.ZERO);											// No se usa
		
		
		setDateCAI(doctype.getDateCAI());
		if (inv.getDocStatus().equalsIgnoreCase("VO") || inv.getDocStatus().equalsIgnoreCase("RE")){
			setDateVoid(inv.getUpdated());
		}
		setIsSOTrx(inv.isSOTrx());
		setC_Invoice_ID(inv.getC_Invoice_ID());
		
		// Campos para TIPO 2
		setCUIT(getInvoiceCuit(inv.getAD_Client_ID()));						
		
		// Campos Ventas TIPO 1
		setNumeroComprobante(inv.getNumeroComprobante());
		setNombreCli(inv.getNombreCli());
		setDescription(inv.getDescription());
		
		// Campos para Compras TIPO 1
		setCod_Aduana(0);														// No se usa
		setCod_Destinacion("");													// No se usa
		setNroDespacho(0);														// No se usa
		setDigVerifNroDespacho(null);											// No se usa
		setDoc_Identificatorio_Vendedor(80);									// Según tabla 2 -- En Libertya siempre se usa CUIT
		setIdentif_Vendedor("0");												// No se usa
		
		// Campo para Otras Percepciones 
		setCod_Jurisdiccion_IIBB(getCodJurisdiccionIIBB(iibb));// Según tabla 8
		setJurImpuestosMunicipales("");
		
		// Salvo el nuevo HEADER
		if (!save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		
		// Armo las LINEAS
		MInvoiceLine lines[] = inv.getLines();
		for (MInvoiceLine line : lines){
			MElectronicInvoiceLine invoiceLine = new MElectronicInvoiceLine(getCtx(), 0, get_TrxName());
			String dateVoid = null;
			if (getDateVoid() != null)
				dateVoid = getDateVoid().toString();
			invoiceLine.createLine(line, getE_ElectronicInvoice_ID(), dateVoid);
		}
	}
	
	private String getCuit(String cuit){
		if (cuit == null){ return "00000000000"; }
		if (cuit.length() == 13){
			String numero = cuit.substring(0,2)+cuit.substring(3,11)+cuit.substring(12,13);
			return numero;
		}
		return cuit;
	}

	private int getInvoiceCantAlicuotas(int id) throws SQLException{
		String	sql = "SELECT count(it.c_tax_id) as cant_alicuotas FROM C_InvoiceTax it INNER JOIN C_Tax t ON (it.C_Tax_ID = t.C_Tax_ID) WHERE t.IsPercepcion = 'N' AND C_Invoice_ID = '"+id+"'";
		PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			return rs.getInt("cant_alicuotas");
		}
		
		return 0;
	}
	
	private String getInvoiceCuit(int id) throws SQLException{
		String	sql = " SELECT * FROM AD_ClientInfo Where ad_client_id = '"+id+"'";
		PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			MClientInfo clientinfo = new MClientInfo(getCtx(), rs, get_TrxName());
			return getCuit(clientinfo.getCUIT());
		}
		return null;
	}
	
//	private String getInvoiceBPartnerName(int id){
//		MBPartner bpartner = new MBPartner(getCtx(), id, get_TrxName());
//		return bpartner.getName();
//	}
	
	private int getRefTablaTipoResponsable(int c_Categoria_Iva_ID) throws SQLException{
		// Instancio mcategoria para recuperar el codigo de la categoria de iva
		MCategoriaIva catIva = new MCategoriaIva(getCtx(), c_Categoria_Iva_ID, get_TrxName());
		
		String	sql = " SELECT * FROM E_ElectronicInvoiceRef Where tabla_ref = '"+FiscalDocumentExport.TABLAREF_TipoResponsable +"' and clave_busqueda = '"+catIva.getCodigo()+"'";
		PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			X_E_ElectronicInvoiceRef ref = new X_E_ElectronicInvoiceRef(getCtx(), rs, get_TrxName());
			return Integer.parseInt(ref.getCodigo());
		}
		
		return 0;
		
	}
	
	private int getRefTablaComprobantes(int id) throws SQLException{
		// Instancio mdoctype para recuperar el name del tipo de documento de factura
		MDocType doctype = new MDocType(getCtx(), id, get_TrxName());
				
		String	sql = " SELECT * FROM E_ElectronicInvoiceRef Where tabla_ref = '"+FiscalDocumentExport.TABLAREF_TablaComprobantes +"' and '"+doctype.getDocTypeKey()+"' ILIKE '%' || clave_busqueda || '%' ";
		PreparedStatement pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			X_E_ElectronicInvoiceRef ref = new X_E_ElectronicInvoiceRef(getCtx(), rs, get_TrxName());
			return Integer.parseInt(ref.getCodigo());
		}
		return 0;
	}
	
	private String getRefTablaMoneda(int id) throws SQLException{
		MCurrency currency = new MCurrency(getCtx(), id, get_TrxName());
		String	sql = " SELECT * FROM E_ElectronicInvoiceRef Where tabla_ref = '"+FiscalDocumentExport.TABLAREF_CodigosMoneda +"' and clave_busqueda = '"+currency.getISO_Code()+"'";
		PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			X_E_ElectronicInvoiceRef ref = new X_E_ElectronicInvoiceRef(getCtx(), rs, get_TrxName());
			return ref.getCodigo();
		}
		
		return null;
	}
	
	private String getRefTablaImpuestosIIBB() throws SQLException{
		
		String	sql = " SELECT * FROM E_ElectronicInvoiceRef Where tabla_ref = '"+FiscalDocumentExport.TABLAREF_TablaImpuestos +"' and clave_busqueda = '"+impuestoIIBB+"'";
		PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		
		if (rs.next()){
			X_E_ElectronicInvoiceRef ref = new X_E_ElectronicInvoiceRef(getCtx(), rs, get_TrxName());
			return ref.getCodigo();
		}
		
		return null;
	}
	
	private BigDecimal getImpuestoIIBB(String nombre, int id) throws SQLException{
		String sqlTax = " SELECT * FROM C_Tax Where name = '"+ nombre +"'";
		PreparedStatement pstmtTax = DB.prepareStatement(sqlTax, get_TrxName());
		ResultSet rsTax = pstmtTax.executeQuery();
		
		if (rsTax.next()){
			String sql = " SELECT * FROM C_InvoiceTax Where c_invoice_id = "+ id+" and c_tax_id = "+rsTax.getInt("c_tax_id");
			PreparedStatement pstmt	= DB.prepareStatement(sql, get_TrxName());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()){
				return rs.getBigDecimal("taxamt");
			}
		}
		return BigDecimal.ZERO;
	}
	
	private BigDecimal getInvoiceTaxBaseAmt(int id) throws SQLException{
		 String	sql = " SELECT * FROM C_Invoicetax Where c_invoice_id = "+ id + "ORDER BY Created desc LIMIT 1";
		 PreparedStatement pstmt	= DB.prepareStatement(sql, get_TrxName());
		 ResultSet rs = pstmt.executeQuery();
		 BigDecimal sumTaxBaseAmt = BigDecimal.ZERO;
		 while (rs.next()) {
		   MInvoiceTax invoiceTax = new MInvoiceTax(getCtx(), rs, get_TrxName());
		   sumTaxBaseAmt = sumTaxBaseAmt.add(invoiceTax.getTaxBaseAmt());
		 } 
		return sumTaxBaseAmt;
	}
	
	private BigDecimal getInvoiceTaxAmt(int id) throws SQLException{
		 String	sql = " SELECT * FROM C_InvoiceTax it INNER JOIN C_Tax t ON (it.C_Tax_ID = t.C_Tax_ID) WHERE t.IsPercepcion = 'N' AND C_Invoice_ID = "+ id;
		 PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		 ResultSet rs = pstmt.executeQuery();
		 BigDecimal sumTaxAmt = BigDecimal.ZERO;
		 while (rs.next()) {
		   MInvoiceTax invoiceTax = new MInvoiceTax(getCtx(), rs, get_TrxName());
		   sumTaxAmt = sumTaxAmt.add(invoiceTax.getTaxAmt());
		 } 
		return sumTaxAmt;
	}
	
	private BigDecimal getInvoiceTaxAmtTaxExempt(int id) throws SQLException{
		 String	sql = " SELECT * FROM C_Invoicetax Where c_invoice_id = "+ id;
		 PreparedStatement pstmt	= DB.prepareStatement(sql, get_TrxName());
		 ResultSet rs = pstmt.executeQuery();
		 BigDecimal sumTaxAmt = BigDecimal.ZERO;
		 while (rs.next()) {
			 MTax tax = new MTax(getCtx(), rs.getInt("c_tax_id"), get_TrxName());
			 if (tax.getRate() == BigDecimal.ZERO || tax.isTaxExempt() == true){
				 MInvoiceTax invoiceTax = new MInvoiceTax(getCtx(), rs, get_TrxName());
				 sumTaxAmt = sumTaxAmt.add(invoiceTax.getTaxAmt());
			 }
		 } 
		return sumTaxAmt;
	}
	
	private int getCodJurisdiccionIIBB(String iibb){
		if (!Util.isEmpty(iibb)){
			String cod = iibb;
			if (iibb.length() >= 2){
				cod = iibb.substring(iibb.length() - 2);
			}
			return Integer.parseInt(cod);
		}
		return 0;
	}
	
	private String getInvoiceBPartnerTaxIdType(String taxIdType){
		if (taxIdType == null){
			return "99";
		}
		else{
			return taxIdType;	
		}
	}
}
