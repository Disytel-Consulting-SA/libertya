package org.openXpertya.process;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.MPeriod;
import org.openXpertya.model.X_T_ElectronicInvoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class ExportElectronicInvoiceToFile extends SvrProcess {

	private int p_C_Period_ID;
	private int p_AD_ElectronicInvoiceFormat_ID;
	
	@Override
	protected String doIt() throws Exception {
	    // delete all rows older than a week
		DB.executeUpdate("DELETE FROM T_ELECTRONICINVOICE WHERE (AD_Client_ID = "+ getAD_Client_ID() +") AND (createdby = "+ getAD_User_ID()+") ");		
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM T_ELECTRONICINVOICE WHERE AD_PInstance_ID = " + getAD_PInstance_ID());
		
		// Recupero el periodo para calcular el mes completo
		MPeriod periodo = new MPeriod(getCtx(), p_C_Period_ID, get_TrxName());
				
		// Consulta con todos los datos
		StringBuffer sql = new StringBuffer();
		sql.append("select " +
				" (select count(e_electronicinvoice_id) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+") ) as factcantregtipo1, "+
				" (select count(e_electronicinvoice_id) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = '"+"Y"+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventacantregtipo1, "+
				" (select count(e_electronicinvoice_id) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = '"+"N"+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as compracantregtipo1, "+
				" (select sum(grandtotal * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as factgrandtotal, "+
				" (select sum(grandtotal * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventagrandtotal, "+
				" (select sum(grandtotal * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as compragrandtotal, "+
				// Importe total de conceptos que no integran el precio neto gravado
				" (select sum(taxbaseamt * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as facttaxbaseamt, "+
				" (select sum(taxbaseamt * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventataxbaseamt, "+
				" (select sum(taxbaseamt * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as comprataxbaseamt, "+
				// Importe neto gravado
				" (select sum(totallines * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as facttotallines, "+
				" (select sum(totallines * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventatotallines, "+
				" (select sum(totallines * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as compratotallines, "+
				// Impuesto liquidado
				" (select sum(taxamt * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as facttaxamt, "+
				" (select sum(taxamt * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventataxamt, "+
				" (select sum(taxamt * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as comprataxamt, "+
				" (select sum(rni * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as factrni, "+
				" (select sum(rni * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventarni, "+
				" (select sum(rni * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as comprarni, "+
				" (select 0 as factoperacionesexentas) as factoperacionesexentas, "+
				//" (select sum(operacionesexentas * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as factoperacionesexentas, "+
				" (select 0 as ventaoperacionesexentas) as ventaoperacionesexentas, "+
				//" (select sum(operacionesexentas * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventaoperacionesexentas, "+
				" (select sum(operacionesexentas * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as compraoperacionesexentas, "+
				" (select sum(importepercepciones * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as factimportepercepciones, "+
				" (select sum(importepercepciones * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventaimportepercepciones, "+
				" (select sum(importepercepciones * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as compraimportepercepciones, "+
				" (select sum(percepcionesiibb * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as factpercepcionesiibb, "+
				" (select sum(percepcionesiibb * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventapercepcionesiibb, "+
				" (select sum(percepcionesiibb * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as comprapercepcionesiibb, "+
				" (select sum(impuestosmunicipales * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as factimpuestosmunicipales, "+
				" (select sum(impuestosmunicipales * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventaimpuestosmunicipales, "+
				" (select sum(impuestosmunicipales * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as compraimpuestosmunicipales, "+
				" (select sum(impuestosinternos * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as factimpuestosinternos, "+
				" (select sum(impuestosinternos * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'Y' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as ventaimpuestosinternos, "+
				" (select sum(impuestosinternos * TotalSign) from e_electronicinvoice where dateinvoiced >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and issotrx = 'N' and (AD_Client_ID = "+ getAD_Client_ID() +") and (createdby = "+ getAD_User_ID()+")) as compraimpuestosinternos, "+
				" hdr.identif_comprador," +
				" hdr.identif_vendedor," +
				" hdr.multiplyrate," +
				" hdr.cod_aduana," +
				" hdr.cod_destinacion," +
				" hdr.nrodespacho," +
				" hdr.digverifnrodespacho," +
				" hdr.fechadespachoplaza," +
				" hdr.taxamt," +
				" hdr.taxbaseamt," +
				" hdr.cant_hojas," +
				" hdr.doc_identificatorio_comprador," +
				" '' as filler," +
				" hdr.c_invoice_id," +
				" hdr.isfiscal," +
				" hdr.name," +
				" hdr.nombrecli," +
				" hdr.cant_alicuotas_iva," +
				" hdr.cai," +
				" hdr.datecai," +
				" hdr.datevoid," +
				" hdr.totallines," +
				" hdr.issotrx," +
				" hdr.cod_moneda," +
				" hdr.dateinvoiced," +
				" hdr.puntodeventa," +
				" hdr.numerodedocumento," +
				" hdr.numerocomprobante," +
				" hdr.grandtotal, " +
				" hdr.cuit, " +
				" hdr.tipo_responsable," +
				" hdr.tipo_comprobante," +
				" hdr.importepercepciones," +
				" hdr.percepcionesiibb," +
				" hdr.impuestosmunicipales," +
				" hdr.impuestosinternos," +
				" hdr.jurimpuestosmunicipales," +
				" ln.istaxexempt," +
				" ln.indica_anulacion, " +
				" ln.diseno_libre, " +
				" ln.linenetamt, " +
				" ln.linetotalamt," +
				" ln.unidad_medida," +
				" ln.importe_bonificacion, " +
				" 0 as importe_ajuste, " +
				" ln.qtyinvoiced, " +
				" ln.rate " +
				" from e_electronicinvoice hdr");
		sql.append(" inner join e_electronicinvoiceline ln on (hdr.e_electronicinvoice_id = ln.e_electronicinvoice_id)");
		sql.append(" where date_trunc('day',dateinvoiced) >= '"+periodo.getStartDate()+"' and date_trunc('day',dateinvoiced) <= '"+periodo.getEndDate()+"' and (hdr.AD_Client_ID = "+ getAD_Client_ID() +") and (hdr.createdby = "+ getAD_User_ID()+") ");
				
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
		
			// Relleno los campos de la tabla temporal
			while (rs.next()) {
				X_T_ElectronicInvoice eInv = new X_T_ElectronicInvoice(getCtx(), 0, get_TrxName());
				
				eInv.setperiodo(rs.getString("dateinvoiced").substring(0,4)+rs.getString("dateinvoiced").substring(5,7));
				eInv.setAnio(rs.getString("dateinvoiced").substring(0,4));
				if (rs.getString("istaxexempt").equalsIgnoreCase("Y")){
					eInv.setTaxExempt("E");
				}else{
					eInv.setTaxExempt("G");
				}
				eInv.setAD_PInstance_ID(getAD_PInstance_ID());
				eInv.setTipoReg1(1);
				eInv.setTipoReg2(2);
				eInv.setDateInvoiced(rs.getString("dateinvoiced").substring(0,4)+rs.getString("dateinvoiced").substring(5,7)+rs.getString("dateinvoiced").substring(8,10));
				eInv.setPuntoDeVenta(rs.getInt("puntodeventa"));
				eInv.setNumeroDeDocumento(rs.getString("numerodedocumento"));
				
				eInv.setFactCantRegTipo1(rs.getInt("factcantregtipo1"));
				eInv.setVentaCantRegTipo1(rs.getInt("ventacantregtipo1"));
				eInv.setCompraCantRegTipo1(rs.getInt("compracantregtipo1"));
				eInv.setGrandTotal(rs.getBigDecimal("grandtotal"));
				
				eInv.setFactGrandTotal(rs.getBigDecimal("factgrandtotal"));
				eInv.setVentaGrandTotal(rs.getBigDecimal("ventagrandtotal"));
				eInv.setCompraGrandTotal(rs.getBigDecimal("compragrandtotal"));
				
				eInv.setFactTaxBaseAmt(rs.getBigDecimal("facttaxbaseamt"));
				eInv.setVentaTaxBaseAmt(rs.getBigDecimal("ventataxbaseamt"));
				eInv.setCompraTaxBaseAmt(rs.getBigDecimal("comprataxbaseamt"));
				
				eInv.setFactTotalLines(rs.getBigDecimal("facttotallines"));
				eInv.setVentaTotalLines(rs.getBigDecimal("ventatotallines"));
				eInv.setCompraTotalLines(rs.getBigDecimal("compratotallines"));
						
				eInv.setFactTaxAmt(rs.getBigDecimal("facttaxamt"));
				eInv.setVentaTaxAmt(rs.getBigDecimal("ventataxamt"));
				eInv.setCompraTaxAmt(rs.getBigDecimal("comprataxamt"));
				
				eInv.setTaxAmt(rs.getBigDecimal("taxamt"));
				
				eInv.setFactRNI(rs.getBigDecimal("factrni"));
				eInv.setVentaRNI(rs.getBigDecimal("ventarni"));
				eInv.setCompraRNI(rs.getBigDecimal("comprarni"));
				
				eInv.setFactOperacionesExentas(rs.getBigDecimal("factoperacionesexentas"));
				eInv.setVentaOperacionesExentas(rs.getBigDecimal("ventaoperacionesexentas"));
				eInv.setCompraOperacionesExentas(rs.getBigDecimal("compraoperacionesexentas"));
				
				eInv.setFactImportePercepciones(rs.getBigDecimal("factimportepercepciones"));
				eInv.setVentaImportePercepciones(rs.getBigDecimal("ventaimportepercepciones"));
				eInv.setCompraImportePercepciones(rs.getBigDecimal("compraimportepercepciones"));
				
				eInv.setFactPercepcionesIIBB(rs.getBigDecimal("factpercepcionesiibb"));
				eInv.setVentaPercepcionesIIBB(rs.getBigDecimal("ventapercepcionesiibb"));
				eInv.setCompraPercepcionesIIBB(rs.getBigDecimal("comprapercepcionesiibb"));
				
				eInv.setFactImpuestosMunicipales(rs.getBigDecimal("factimpuestosmunicipales"));
				eInv.setVentaImpuestosMunicipales(rs.getBigDecimal("ventaimpuestosmunicipales"));
				eInv.setCompraImpuestosMunicipales(rs.getBigDecimal("compraimpuestosmunicipales"));
				
				eInv.setFactImpuestosInternos(rs.getBigDecimal("factimpuestosinternos"));
				eInv.setVentaImpuestosInternos(rs.getBigDecimal("ventaimpuestosinternos"));
				eInv.setCompraImpuestosInternos(rs.getBigDecimal("compraimpuestosinternos"));
				
				eInv.setLineNetAmt(rs.getBigDecimal("linenetamt"));
				
				eInv.setC_Invoice_ID(rs.getInt("c_invoice_id"));
				eInv.setCod_Moneda(rs.getString("cod_moneda"));
				eInv.setNumeroComprobante(rs.getInt("numerocomprobante"));
				eInv.setName(rs.getString("name"));
				eInv.setNombreCli(rs.getString("nombrecli"));
				eInv.setCant_Alicuotas_Iva(rs.getInt("cant_alicuotas_iva"));
				eInv.setTotalLines(rs.getBigDecimal("totallines"));
				eInv.setCAI(rs.getString("cai"));
				eInv.setUnidad_Medida(rs.getString("unidad_medida"));
				eInv.setQtyInvoiced(rs.getBigDecimal("qtyinvoiced"));
				eInv.setIdentif_Comprador(rs.getString("identif_comprador"));
				eInv.setIdentif_Vendedor(rs.getString("identif_vendedor"));
				eInv.setMultiplyRate(rs.getBigDecimal("multiplyrate"));
				eInv.setCod_Aduana(rs.getInt("cod_aduana"));
				eInv.setCod_Destinacion(rs.getString("cod_destinacion"));
				eInv.setNroDespacho(rs.getInt("nrodespacho"));
				eInv.setDigVerifNroDespacho(rs.getString("digverifnrodespacho"));
				if (rs.getString("fechadespachoplaza") != null){
					eInv.setFechaDespachoPlaza(rs.getString("fechadespachoplaza").substring(0,4)+rs.getString("fechadespachoplaza").substring(5,7)+rs.getString("fechadespachoplaza").substring(8,10));
				}
				eInv.setImporte_Bonificacion(rs.getBigDecimal("importe_bonificacion"));
				eInv.setCant_Hojas(rs.getInt("cant_hojas"));
				eInv.setDoc_Identificatorio_Comprador(rs.getInt("doc_identificatorio_comprador"));
				eInv.setTaxBaseAmt(rs.getBigDecimal("taxbaseamt"));
				eInv.setLineTotalAmt(rs.getBigDecimal("linetotalamt"));
				eInv.setDiseno_Libre(rs.getString("diseno_libre"));
				eInv.setIndica_Anulacion(rs.getString("indica_anulacion"));
				eInv.setTipo_Responsable(rs.getInt("tipo_responsable"));
				eInv.setTipo_Comprobante(rs.getInt("tipo_comprobante"));
				eInv.setRate(rs.getBigDecimal("rate").setScale(2));
				eInv.setImportePercepciones(rs.getBigDecimal("importepercepciones"));
				eInv.setPercepcionesIIBB(rs.getBigDecimal("percepcionesiibb"));
				eInv.setImpuestosMunicipales(rs.getBigDecimal("impuestosmunicipales"));
				eInv.setImpuestosInternos(rs.getBigDecimal("impuestosinternos"));
				eInv.setJurImpuestosMunicipales(rs.getString("jurimpuestosmunicipales"));
				eInv.setCUIT(rs.getString("cuit"));
				if (rs.getString("datecai") != null)
					eInv.setDateCAI(rs.getString("datecai").substring(0,4)+rs.getString("datecai").substring(5,7)+rs.getString("datecai").substring(8,10));
				if (rs.getString("datevoid") != null)
					eInv.setDateVoid(rs.getString("datevoid").substring(0,4)+rs.getString("datevoid").substring(5,7)+rs.getString("datevoid").substring(8,10));
				if (rs.getString("issotrx").equalsIgnoreCase("Y"))
					eInv.settipo("V");
				else
					eInv.settipo("C");
				if (rs.getString("isfiscal").equalsIgnoreCase("Y"))
					eInv.setFiscal("C");
				
				if (!eInv.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fill T_ElectronicInvoice error", e);
			throw new Exception("Export Invoice Fiscal Error",e);
		}
		
		// Consulta recupera cabecera del formato elegido por parametro
		StringBuffer sqlFormatHdr = new StringBuffer();
		sqlFormatHdr.append("select * from ad_electronicinvoiceformathdr where ad_electronicinvoiceformat_id = '"+p_AD_ElectronicInvoiceFormat_ID+"'");
		PreparedStatement pstmtFormatHdr = null;
		ResultSet rsFormatHdr = null;
		
		// Consulta recupera formato de la linea en el orden especificado
		StringBuffer sqlLine = new StringBuffer();
		sqlLine.append("select * from ad_electronicinvoiceformatline where ad_electronicinvoiceformathdr_id = ? order by secuencia");
		pstmt = null;
		rs = null;

		// Consulta recupera datos para el AD_PInstance_ID actual
		StringBuffer sqlT_Temp = new StringBuffer();
		sqlT_Temp.append("select * from t_electronicinvoice where AD_PInstance_ID = " + getAD_PInstance_ID());
		PreparedStatement pstmt_t = null;
		ResultSet rs_t = null;
		
		try {
			pstmt = DB.prepareStatement(sqlLine.toString(), get_TrxName());
			pstmt_t = DB.prepareStatement(sqlT_Temp.toString(), get_TrxName());
			
			pstmtFormatHdr = DB.prepareStatement(sqlFormatHdr.toString(), get_TrxName());
			rsFormatHdr = pstmtFormatHdr.executeQuery();
		
			// Itero por cada archivo a crear especificado en la cabecera
			while (rsFormatHdr.next()){
				// Nombre de la columna donde no se quieren campos repetidos 
				String campoNoRepetir = rsFormatHdr.getString("distinto");
				// Tipo de registro a exportar (Compra, Venta, Ambos)
				String tipo = rsFormatHdr.getString("tipo");
				
				// Recupero los campos de condición
				String isCondicional = rsFormatHdr.getString("iscondicional");
				String campo1 = rsFormatHdr.getString("campo1");
				String campo2 = rsFormatHdr.getString("campo2");
				
				String valor = "";
				rs_t = pstmt_t.executeQuery();
				while (rs_t.next()) {
					String line = "";
					// Parametro para recuperar una linea del formato
					pstmt.setInt(1, rsFormatHdr.getInt("ad_electronicinvoiceformathdr_id"));
					rs = pstmt.executeQuery();
				
					// Verifico el campo condicional del formato de cabecera para valores en cero
					if (isCondicional.equalsIgnoreCase("Y")){
						boolean cumple1 = false;
						boolean cumple2 = false;
						
						cumple1 = cumpleCampoCondicional(rs_t, campo1);
						cumple2 = cumpleCampoCondicional(rs_t, campo2);
						
						if (!(cumple1 || cumple2)){
							continue;
						}
					}
					
					// Verifico si es necesario filtrar por Venta o Compra
					if (tipo.equalsIgnoreCase("V")){ 
						if (!rs_t.getString("tipo").equalsIgnoreCase("V")){
							continue;
						}
					}else{ 
						if(tipo.equalsIgnoreCase("C")){
							if (!rs_t.getString("tipo").equalsIgnoreCase("C")){
								continue;
							}
						}
					}
					
					// Verifico si el valor no es igual al anterior 
					if (campoNoRepetir != null){
						if ( !campoNoRepetir.trim().equalsIgnoreCase("")){
							if (rs_t.getString(campoNoRepetir).trim().equalsIgnoreCase(valor)){
								continue;
							}else {
								valor = rs_t.getString(campoNoRepetir);
							}
						}
					}
					// Itero armando la linea a exportar al archivo
					while(rs.next()){
						String campo = "";
						// Verifico si es el campo para el relleno 
						if (!rs.getString("nombre_campo").trim().equalsIgnoreCase("filler")){
							campo = rs_t.getString(rs.getString("nombre_campo").trim());
						}
						// A cada campo le aplico el formato definido
						line += formateoCampo(campo,rs.getInt("longitud"),rs.getString("relleno"), rs.getString("isleftalign"), rs.getString("punto_decimal"));
					}
					// Envío la linea al archivo
					escribeArchivo(line, rsFormatHdr.getString("expfilename"));
				}
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fill T_ElectronicInvoice error", e);
			throw new Exception(e.getMessage(),e);
		}
		return "Exportación finalizada correctamente";
	}
		
	private boolean cumpleCampoCondicional(ResultSet rs,String campo) throws SQLException{
		if (campo != null && campo.trim() != ""){
				if (rs.getBigDecimal(campo) != null){ 
					if (rs.getBigDecimal(campo).compareTo(Env.ZERO) == 1){
						return true;
					}
				 }
		}
		return false; 
	}

	private String formateoCampo(String campo,int longitud, String relleno, String leftalign, String punto_decimal){
		// Si es NULL se considera un blanco
		if (campo == null){
			campo = "";
		}
		// Si no tiene relleno se considera un blanco
		if (relleno == null || relleno == ""){
			relleno = " ";
		}
		String aux = "";
		
		// Saca los puntos (.) que tenga el campo
		if (punto_decimal.equalsIgnoreCase("Y")){
			String auxCampo = "";
			for (int i=0; i < campo.length(); i++) {
				  if (campo.charAt(i) != '.')
					  auxCampo += campo.charAt(i);
			}
			campo = auxCampo;
		}
		
		// Si no se especifica la longitud se devuelve el campo tal cual,
		// si no, se lo devuelve con la longitud indicada y con el relleno especificado
		if (longitud != 0){
			for(int i = 1;i<=longitud;i++){
				aux += relleno;
			}
			// Verifico la alineación del campo
			if (leftalign.equalsIgnoreCase("N")){
				aux += campo;
				return aux.substring(campo.length());
			}else{
				campo += aux;
				return campo.substring(0,longitud);
			}
		}
		else {		
			return campo;
		}
	}
	
	// Armo el archivo
	private int escribeArchivo(String line, String file){
		FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter(file, true);
            pw = new PrintWriter(fichero);
            pw.println(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
		return 0;
	}
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) ;
            else if( name.equalsIgnoreCase( "C_Period_ID" )) {
            	p_C_Period_ID = ((BigDecimal)para[ i ].getParameter()).intValue();
            } else if( name.equalsIgnoreCase( "AD_ElectronicInvoiceFormat_ID" )) {
            	p_AD_ElectronicInvoiceFormat_ID = ((BigDecimal)para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
		
	}

}
