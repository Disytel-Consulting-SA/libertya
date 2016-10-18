package org.openXpertya.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.util.DB;

public class ExportArciba extends SvrProcess {

	private String directorio;
	private Timestamp date_from;
    private Timestamp date_to;
    private int ad_org_id;
    
    /** Archivo a exportar */
	private File exportFile;
	/** Buffer de escritura del archivo */
	private Writer fileWriter;
	
    private static final String ENCODE_UTF8 = "UTF-8";
    
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		 for( int i = 0;i < para.length;i++ ) {
	            log.fine( "prepare - " + para[ i ] );

	            String name = para[ i ].getParameterName();

	            if( para[ i ].getParameter() == null ) {
	                ;
	            } else if( name.equalsIgnoreCase( "DateInvoiced" )) {
	            	date_from = (Timestamp)para[i].getParameter();
	            	date_to = (Timestamp)para[i].getParameter_To();
	            } else if( name.equalsIgnoreCase( "AD_Org_ID" )) {
	            	ad_org_id = para[ i ].getParameterAsInt();	            	
	            } else if( name.equalsIgnoreCase( "Directory" )) {
	            	directorio = (String)para[ i ].getParameter();
	            } else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
	        }
	}

	@Override
	protected String doIt() throws Exception {
		//Control del directorio de destino
		File targetDir = new File (directorio);
		if (!targetDir.exists())
				//targetDir.mkdir();
			System.out.println("Directorio inexistente.");
		
		createInvoiceTxt();
		createCreditNoteTxt();
		createCreditNoteNotImputedTxt();

 		return "Exportación Finalizada";
	}
	
	private void createInvoiceTxt() {
		String fullFileName = directorio+"ARCIBA_FACTURA_DEBITO" + getDate() + ".txt";
		String sql = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

		try	{
			this.createDocument(fullFileName);
			sql = getSqlArcibaInvoice();		        	         	 		
			pstmt = DB.prepareStatement(sql);	
			rs = pstmt.executeQuery();
			putRsInFileInvoice(rs);
			this.saveDocument();
		}
		catch (Exception e){ 
			log.saveError("Exportacion ARCIBA - Prepare", e);
			e.printStackTrace();
		}	
		finally{
			try{
				if(pstmt != null){
					pstmt.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch(Exception e2){
				log.saveError("Exportacion ARCIBA - Prepare", e2);
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * Creación del documento de exportación
	 * 
	 * @throws Exception
	 *             en caso de error en la creación del archivo
	 */
	protected void createDocument(String fullFileName) throws IOException {
		setExportFile(new File(fullFileName));
		
	    FileOutputStream fos = new FileOutputStream(fullFileName);
	    OutputStreamWriter osw = new OutputStreamWriter(fos, ENCODE_UTF8);
	    BufferedWriter bw = new BufferedWriter(osw);
	    
	    setFileWriter(new PrintWriter(bw));	
	}
	
	private void createCreditNoteTxt() {
		String fullFileName = directorio+"ARCIBA_NOTA_DE_CREDITO" + getDate() + ".txt";
		String sql = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
		try	{
			this.createDocument(fullFileName);
			sql = getSqlArcibaCreditNote(true);		        	         	 		
			pstmt = DB.prepareStatement(sql);	
			rs = pstmt.executeQuery();
			putRsInFileCreditNote(rs);
			this.saveDocument();
			pstmt.close();
		}
		catch (Exception e){ 
			log.saveError("Exportacion ARCIBA - Prepare", e);
			e.printStackTrace();
		}	
		finally{
			try{
				if(pstmt != null){
					pstmt.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch(Exception e2){
				log.saveError("Exportacion ARCIBA - Prepare", e2);
				e2.printStackTrace();
			}
		}
	}
	
	private void createCreditNoteNotImputedTxt() {
		String fullFileName = directorio+"ARCIBA_NC_NO_IMPUTADAS" + getDate() + ".txt";
		String sql = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
		try	{		
		    this.createDocument(fullFileName);
			sql = getSqlArcibaCreditNote(false);		        	         	 		
			pstmt = DB.prepareStatement(sql);	
			rs = pstmt.executeQuery();
			putRsInFileCreditNote(rs);
			this.saveDocument();
		}
		catch (Exception e){ 
			log.saveError("Exportacion ARCIBA - Prepare", e);
			e.printStackTrace();
		}	
		finally{
			try{
				if(pstmt != null){
					pstmt.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch(Exception e2){
				log.saveError("Exportacion ARCIBA - Prepare", e2);
				e2.printStackTrace();
			}
		}
	}
 		
	private String getSqlArcibaInvoice() {
		
		StringBuffer sqlReal = new StringBuffer();
		
		// 1 - Tipo de Operación - Número(1) - 1 - CONSTANTE 2
		sqlReal.append("SELECT (CASE WHEN (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_OPERACION_FACTURA_ARCIBA') IS NULL THEN '2' ELSE (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_OPERACION_FACTURA_ARCIBA') END) AS TIPO_DE_OPERACION, ");
		// 2 - Código de Norma - Número(3) - 3
		sqlReal.append("lpad(it.arcibanormcode::text,3,'0') AS CODIGO_DE_NORMA, ");
		// 3 - Fecha de Reten/Percep - Fecha(10) - 10
		sqlReal.append("To_char(i.dateinvoiced,'dd/MM/yyyy') AS FECHA_DE_RETEN_PERC, ");
		// 4 - Tipo de comprobante - Número(2) - 2
		sqlReal.append("(CASE WHEN (d.docbasetype = 'ARI' AND doctypekey ILIKE 'CI%') THEN (CASE WHEN (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_FACTURA_ARCIBA') IS NULL THEN '01' ELSE (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_FACTURA_ARCIBA') END) WHEN (d.docbasetype = 'ARI' AND doctypekey ILIKE 'CDN%') THEN (CASE WHEN (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_NOTA_DEBITO_ARCIBA') IS NULL THEN '09' ELSE (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_NOTA_DEBITO_ARCIBA') END) ELSE (CASE WHEN (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_OTRO_ARCIBA') IS NULL THEN '09' ELSE (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_OTRO_ARCIBA') END) END) AS TIPO_DE_COMPROBANTE, ");
		// 5 - Letra de comprobante - Texto(1) - 1
		sqlReal.append("lc.letra AS LETRA_DE_COMPROBANTE, ");
		// 6 - Nro. de comprobante - Número(16) - 16
		sqlReal.append("lpad(substring(i.documentno from 2 for 13),'16','0') AS NRO_DE_COMPROBANTE, ");
		// 7 - Fecha de comprobante - Fecha(10) - 10
		sqlReal.append("To_char(i.dateinvoiced,'dd/MM/yyyy') AS FECHA_DE_COMPROBANTE, ");
		// 8 - Monto - Número(13,2) - 16
		sqlReal.append("replace(lpad(i.grandtotal::text,16,'0'),'.',',') AS MONTO, ");
		// 9 - Nro de Certificado Propio - Texto(16) - 16 - CONSTANTE BLANCO EN PERC
		sqlReal.append("rpad('',16,' ') AS NRO_DE_CERTIFICADO_PROPIO, ");
		// 10 - Tipo documento - Número(1) - 1
		sqlReal.append("bp.taxidtype AS TIPO_DOCUMENTO, ");
		// 11 - Número de Documento - Número(11) - 11
		sqlReal.append("lpad(trim(bp.taxid),'11','0') AS NRO_DE_DOCUMENTO, ");
		// 12 - Situación Ingresos Brutos - Numérico(1) - 1 - 1: Local, 2: Convenio Multilateral, 4: No inscripto, 5: Reg.Simplificado
		sqlReal.append("COALESCE(bp.IIBBType,'4') AS SITUACION_IIBB, ");
		// 13 - Nro. Inscripción IB - Numérico(11) - 10 - CONSTANTE 00000000000
		sqlReal.append("COALESCE(lpad(replace(bp.iibb,'-',''),'11','0'),'00000000000') AS NRO_INCRIPCION_IIBB, ");
		// 14 - Situación IVA - Numérico(1) - 1
		sqlReal.append("(CASE WHEN (ci.codigo = 2) THEN '1' WHEN (ci.codigo = 3) THEN '2' WHEN (ci.codigo = 4) THEN '3' WHEN (ci.codigo = 5) THEN '4' WHEN (ci.codigo = 7) THEN '0' ELSE '5' END) AS SITUACION_IVA, ");		
		// 15 - Razón social del retenido - Texto(30) - 30
		sqlReal.append("rpad(bp.name,'30',' ') AS RAZON_SOCIAL_DEL_RETENIDO, ");
		// 16 - Otros Conceptos - Numero(13,2) - 16 - SE INDICA EL MONTO DE RETENCION/PERCEPCION PRACTICADA
		sqlReal.append("(CASE WHEN (lc.letra = 'A' OR lc.letra = 'M') THEN replace(lpad(it.taxamt::text,16,'0'),'.',',') ELSE replace(lpad(( it.taxamt + (SELECT SUM(ita.taxamt) AS IVA FROM C_InvoiceTax ita LEFT JOIN C_Tax ta ON (ta.C_Tax_ID = ita.C_Tax_ID) WHERE (ta.IsPercepcion = 'N') AND ita.C_Invoice_ID = i.C_Invoice_ID) )::text,16,'0'),'.',',') END) AS  OTROS_CONCEPTOS, ");
		// 17 - IVA	- Numero(13,2) - 16
		sqlReal.append("(CASE WHEN (lc.letra = 'A' OR lc.letra = 'M') THEN replace(lpad((SELECT SUM(ita.taxamt) AS IVA FROM C_InvoiceTax ita LEFT JOIN C_Tax ta ON (ta.C_Tax_ID = ita.C_Tax_ID) WHERE (ta.IsPercepcion = 'N') AND ita.C_Invoice_ID = i.C_Invoice_ID)::text,16,'0'),'.',',') ELSE '0000000000000,00' END) AS IVA, ");
		// 18 - Monto Sujeto a Ret/Percep - Numero(13,2) - 16
		sqlReal.append("replace(lpad(i.netamount::text,16,'0'),'.',',') AS MONTO_SUJETO_A_PERCEP_RETEN, ");
		// 19 - Alícuota - Numero(2,2) - 5
		sqlReal.append("lpad(substring(it.rate::text from 1 for position('.' in it.rate::text) - 1),2,'0')  || ',' || rpad(substring(it.rate::text from position('.' in it.rate::text) + 1 for 5),2,'0') AS ALICUOTA, ");		
		// 20 - Ret/Percep Practicadas - Numero(13,2) - 16
		sqlReal.append("replace(lpad(it.taxamt::text,16,'0'),'.',',') AS RET_PERCEP_PRACTICADAS, "); 
		// 21 - Total Monto Retenido - Numero(13,2) - 16
		sqlReal.append("replace(lpad(it.taxamt::text,16,'0'),'.',',') AS TOTAL_MONTO_RETENIDO ");
		
		sqlReal.append("FROM C_Invoice i ");
		sqlReal.append("LEFT JOIN C_InvoiceTax it ON (i.C_Invoice_ID = it.C_Invoice_ID) ");
		sqlReal.append("LEFT JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID) ");
		sqlReal.append("LEFT JOIN C_Letra_Comprobante lc ON (i.C_Letra_Comprobante_ID = lc.C_Letra_Comprobante_ID) "); 
		sqlReal.append("LEFT JOIN C_DocType d ON (i.C_DocType_ID = d.C_DocType_ID) ");
		sqlReal.append("LEFT JOIN C_BPartner bp ON (i.C_BPartner_ID = bp.C_BPartner_ID) ");
		sqlReal.append("LEFT JOIN C_Categoria_IVA ci ON (ci.C_Categoria_IVA_ID = bp.C_Categoria_IVA_ID) ");
		
		sqlReal.append("WHERE (i.isActive = 'Y') AND (i.AD_CLIENT_ID = "+getAD_Client_ID()+") AND (d.isfiscaldocument = 'Y') AND (t.ispercepcion = 'Y') AND (i.docstatus = 'CO' OR i.docstatus = 'CL' OR i.docstatus = 'VO' OR i.docstatus = 'RE') AND (t.C_Region_ID::text =(SELECT value FROM AD_Preference WHERE attribute = 'C_Region_ID_FACTURA_ARCIBA')) AND ((d.docbasetype = 'ARI' AND doctypekey ILIKE 'CI%') OR (d.docbasetype = 'ARI' AND doctypekey ILIKE 'CDN%')) ");
		if (ad_org_id > 0){
			sqlReal.append(" AND (i.AD_Org_ID = "+ad_org_id+")");
		}
		if (date_from != null){
			sqlReal.append(" AND (date_trunc('day',i.DateInvoiced) >= '"+date_from+"')");
		}
		if (date_to != null){
			sqlReal.append(" AND (date_trunc('day',i.DateInvoiced) <= '"+date_to+"')");
		}
		
		sqlReal.append("ORDER BY FECHA_DE_RETEN_PERC ");

		return sqlReal.toString();
	}
	
	private String getSqlArcibaCreditNote(boolean imputed) {
		
		StringBuffer sqlReal = new StringBuffer();
		
		// 1 - Tipo de Operación - Número(1) - 1 - CONSTANTE 2
		sqlReal.append("SELECT (CASE WHEN (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_OPERACION_NOTA_CREDITO_ARCIBA') IS NULL THEN '2' ELSE (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_OPERACION_NOTA_CREDITO_ARCIBA') END) AS TIPO_DE_OPERACION, ");
		// 2 - Numero de Nota Credito - Texto(12) - 12
		sqlReal.append("substring(i.documentno from 2 for 13) AS NRO_DE_NOTA_DE_CREDITO, ");
		// 3 - Fecha de Nota de Credito - Texto(10) - 10
		sqlReal.append("To_char(i.dateinvoiced,'dd/MM/yyyy') AS FECHA_NOTA_DE_CREDITO, ");
		// 4 - Monto de Nota de Credito - Número(16) - 16
		sqlReal.append("replace(lpad(i.grandtotal::text,16,'0'),'.',',') AS MONTO_NOTA_DE_CREDITO, ");
		// 5 - Nro Certificado Propio - Texto(16) - 16 - Texto en Blanco
		sqlReal.append("rpad('',16,' ') AS NRO_DE_CERTIFICADO_PROPIO, ");
		// 6 - Tipo Comprobante Origen - Número(2) - 2
		sqlReal.append("(SELECT (CASE WHEN (doc.docbasetype = 'ARI' AND doc.doctypekey ILIKE 'CI%') THEN (CASE WHEN (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_FACTURA_ARCIBA') IS NULL THEN '01' ELSE (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_FACTURA_ARCIBA') END) WHEN (doc.docbasetype = 'ARI' AND doc.doctypekey ILIKE 'CND%') THEN (CASE WHEN (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_NOTA_DEBITO_ARCIBA') IS NULL THEN '02' ELSE (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_NOTA_DEBITO_ARCIBA') END) ELSE (CASE WHEN (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_OTRO_ARCIBA') IS NULL THEN '09' ELSE (SELECT value FROM AD_Preference WHERE attribute = 'TIPO_DE_COMPROBANTE_OTRO_ARCIBA') END) END) FROM C_Invoice inv LEFT JOIN C_DocType doc ON (inv.C_DocType_ID = doc.C_DocType_ID) WHERE inv.C_Invoice_ID = i.C_Invoice_Orig_ID) AS TIPO_DE_COMPROBANTE_ORIGEN, ");
		// 7 - Letra de Comprobante - Texto(1) - 1
		sqlReal.append("(SELECT lc.letra FROM C_Invoice inv LEFT JOIN C_Letra_Comprobante lec ON (inv.C_Letra_Comprobante_ID = lec.C_Letra_Comprobante_ID) WHERE inv.C_Invoice_ID = i.C_Invoice_Orig_ID) AS LETRA_DE_COMPROBANTE, ");
		// 8 - Número de Comprobante - Texto(16) - 16
		sqlReal.append("(SELECT lpad(substring(inv.documentno from 2 for 13),'16','0') FROM C_Invoice inv WHERE inv.C_Invoice_ID = i.C_Invoice_Orig_ID) AS NRO_DE_COMPROBANTE, ");
		// 9 - Número de Documento del Retenido- Número(11) - 11
		sqlReal.append("lpad(trim(bp.taxid),'11','0') AS NRO_DE_DOCUMENTO, ");
		// 10 - Código de Norma - Número(3) - 3
		sqlReal.append("lpad((SELECT itax.arcibanormcode::text FROM C_InvoiceTax itax WHERE itax.C_Invoice_ID = i.C_Invoice_Orig_ID AND itax.arcibanormcode IS NOT NULL),3,'0') AS CODIGO_DE_NORMA, ");
		// 11 - Fecha de Reten/Percep - Fecha(10) - 10
		sqlReal.append("To_char((SELECT inv.dateinvoiced FROM C_Invoice inv WHERE inv.C_Invoice_ID = i.C_Invoice_Orig_ID),'dd/MM/yyyy') AS FECHA_DE_RETEN_PERC, ");
		// 12 - Ret/Percep Practicadas - Numero(13,2) - 16
		sqlReal.append("replace(lpad((it.taxamt::numeric(16,2))::text,16,'0'),'.',',') AS RET_PERCEP_PRACTICADAS, ");
		// 13 - Alícuota - Numero(2,2) - 5
		sqlReal.append("lpad(substring(it.rate::text from 1 for position('.' in it.rate::text) - 1),2,'0')  || ',' || rpad(substring(it.rate::text from position('.' in it.rate::text) + 1 for 5),2,'0') AS ALICUOTA, ");			
		// 99 - Fecha de Comprobante Origen - Utilizada para filtrar		
		sqlReal.append("(SELECT inv.dateinvoiced FROM C_Invoice inv WHERE inv.C_Invoice_ID = i.C_Invoice_Orig_ID) AS FECHA_COMPROBANTE_ORIGEN ");
		sqlReal.append("FROM C_Invoice i ");
		sqlReal.append("LEFT JOIN C_InvoiceTax it ON (i.C_Invoice_ID = it.C_Invoice_ID) ");
		sqlReal.append("LEFT JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID) ");
		sqlReal.append("LEFT JOIN C_Letra_Comprobante lc ON (i.C_Letra_Comprobante_ID = lc.C_Letra_Comprobante_ID) "); 
		sqlReal.append("LEFT JOIN C_DocType d ON (i.C_DocType_ID = d.C_DocType_ID) ");
		sqlReal.append("LEFT JOIN C_BPartner bp ON (i.C_BPartner_ID = bp.C_BPartner_ID) ");
		sqlReal.append("LEFT JOIN C_Categoria_IVA ci ON (ci.C_Categoria_IVA_ID = bp.C_Categoria_IVA_ID) ");

		sqlReal.append("WHERE (i.isActive = 'Y') AND (i.AD_CLIENT_ID = "+getAD_Client_ID()+") AND (d.isfiscaldocument = 'Y') AND (t.ispercepcion = 'Y') AND (i.docstatus = 'CO' OR i.docstatus = 'CL' OR i.docstatus = 'VO' OR i.docstatus = 'RE') AND (t.C_Region_ID::text =(SELECT value FROM AD_Preference WHERE attribute = 'C_Region_ID_FACTURA_ARCIBA')) AND (d.docbasetype = 'ARC' AND doctypekey ILIKE 'CCN%') ");
		if (ad_org_id > 0){
			sqlReal.append(" AND (i.AD_Org_ID = "+ad_org_id+")");
		}
		if (date_from != null){
			// NOTAS DE CREDITO IMPUTADAS
			if (imputed){
				sqlReal.append(" AND ((SELECT date_trunc('day',inv.dateinvoiced) FROM C_Invoice inv WHERE inv.C_Invoice_ID = i.C_Invoice_Orig_ID) >= '"+date_from+"')");
			}
			else{
				sqlReal.append(" AND (date_trunc('day',i.dateinvoiced) >= '"+date_from+"')");
			}
		}
		
		if (date_to != null){
			// NOTAS DE CREDITO IMPUTADAS
			if (imputed){
				sqlReal.append(" AND ((SELECT date_trunc('day',inv.dateinvoiced) FROM C_Invoice inv WHERE inv.C_Invoice_ID = i.C_Invoice_Orig_ID) <= '"+date_to+"')");
			}
			else{
				sqlReal.append(" AND (date_trunc('day',i.dateinvoiced) <= '"+date_to+"')");
			}
		}
		// NOTAS DE CREDITO IMPUTADAS
		if (imputed){
			sqlReal.append(" AND (c_invoice_orig_id IS NOT NULL) ");
		}
		else{
			sqlReal.append(" AND (c_invoice_orig_id IS NULL) ");
		}
	
		sqlReal.append("ORDER BY FECHA_NOTA_DE_CREDITO ");
 
		return sqlReal.toString();
	}
	
	private int putRsInFileInvoice(ResultSet rs) throws IOException, SQLException{
		String lineSeparator = System.getProperty("line.separator");
		
		int cant=0;
		StringBuffer s=new StringBuffer();
		
		while(rs.next())
		{
			s=new StringBuffer();
			
			s.append(rs.getString(1));
			s.append(rs.getString(2));
			s.append(rs.getString(3));
			s.append(rs.getString(4));
			/* En PERCEPCION: Si Tipo Comprobante = (01,06,07): A,B,C,M sino 1 dígito blanco. */
			if ( (rs.getString(4).compareTo("01") == 0) || (rs.getString(4).compareTo("06") == 0) || (rs.getString(4).compareTo("07") == 0) ){
				s.append(rs.getString(5));	
			}
			else{
				s.append(" ");
			}
			s.append(rs.getString(6));
			s.append(rs.getString(7));
			s.append(rs.getString(8));
			s.append(rs.getString(9));
			/* 1: CDI, 2: CUIL, 3: CUIT */ 
			if (rs.getString(10).compareTo(X_C_BPartner.TAXIDTYPE_DNI) == 0){s.append("1");}
			else{if (rs.getString(10).compareTo(X_C_BPartner.TAXIDTYPE_CUIT) == 0){s.append("3");}
			     else{if (rs.getString(10).compareTo(X_C_BPartner.TAXIDTYPE_CUIL) == 0){s.append("2");}
			          else{s.append("7");}}}
			s.append(rs.getString(11));
			/* Si Tipo de Documento=3: Situación IB del Retenido=(1,2,4,5)
			 * Si Tipo de Documento=(1,2): Situación IB del Retenido=4
			 * DONDE:
			 * 1: Local, 2: Convenio Multilateral, 4: No inscripto, 5: Reg.Simplificado 
			 */
			s.append(rs.getString(12));
			s.append(rs.getString(13));
			s.append(rs.getString(14));
			s.append(rs.getString(15));
			s.append(rs.getString(16));
			s.append(rs.getString(17));
			/* TODO
			 * Ajuste del campo Monto Sujeto a Ret/Percep
			 * Se debería analizar por qué se genera un diferencia de 1 centavo. 
			*/
			BigDecimal montoSujetoARetencion = new BigDecimal(rs.getString(18).replace(",", "."));
			BigDecimal montoTotal = new BigDecimal(rs.getString(8).replace(",", "."));
			BigDecimal otrosConceptos = new BigDecimal(rs.getString(16).replace(",", "."));
			BigDecimal importeIva = new BigDecimal(rs.getString(17).replace(",", "."));
			BigDecimal montoAux = montoTotal.subtract(otrosConceptos).subtract(importeIva);
			if ( montoSujetoARetencion.subtract(montoAux).abs().compareTo(new BigDecimal("0.03")) <= 0){
				String monto = montoAux.toString();
				s.append(("0000000000000000".substring(monto.length()) + monto).replace(".", ","));
			}
			else{
				s.append(rs.getString(18));	
			}
			s.append(rs.getString(19));
			s.append(rs.getString(20));
			s.append(rs.getString(21));
			
			s.append(lineSeparator);		
			this.getFileWriter().write(s.toString());
			s=null;
			cant++;
		}
	
		return cant;
	}
	
	private int putRsInFileCreditNote(ResultSet rs) throws IOException, SQLException{
		String lineSeparator = System.getProperty("line.separator");
		
		int cant=0;
		StringBuffer s=new StringBuffer();
		
		while(rs.next())
		{
			s=new StringBuffer();
			for (int i=1; i<= rs.getMetaData().getColumnCount() - 1; i++){
				s.append(rs.getString(i));	
			}
			s.append(lineSeparator);		
			this.getFileWriter().write(s.toString());
			s=null;
			cant++;
		}
	
		return cant;
	}
	
	/*
	 * Retorna el dia actual en formato anio mes dia
	 */
    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

	/**
	 * @return the exportFile
	 */
	public File getExportFile() {
		return exportFile;
	}

	/**
	 * @param exportFile the exportFile to set
	 */
	public void setExportFile(File exportFile) {
		this.exportFile = exportFile;
	}

	/**
	 * @return the fileWriter
	 */
	public Writer getFileWriter() {
		return fileWriter;
	}

	/**
	 * @param fileWriter the fileWriter to set
	 */
	public void setFileWriter(Writer fileWriter) {
		this.fileWriter = fileWriter;
	}       
    
	protected void saveDocument() throws Exception {
		// Cierro el archivo
		getFileWriter().close();
	}
    
}
