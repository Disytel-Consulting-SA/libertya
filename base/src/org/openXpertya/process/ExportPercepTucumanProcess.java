package org.openXpertya.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.util.DB;

public class ExportPercepTucumanProcess extends SvrProcess {

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
		for (int i = 0; i < para.length; i++) {
			log.fine("prepare - " + para[i]);

			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else if (name.equalsIgnoreCase("DateInvoiced")) {
				date_from = (Timestamp) para[i].getParameter();
				date_to = (Timestamp) para[i].getParameter_To();
			} else if (name.equalsIgnoreCase("AD_Org_ID")) {
				ad_org_id = para[i].getParameterAsInt();
			} else if (name.equalsIgnoreCase("Directory")) {
				directorio = (String) para[i].getParameter();
			} else {
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		// Control del directorio de destino
		File targetDir = new File(directorio);
		if (!targetDir.exists())
			// targetDir.mkdir();
			System.out.println("Directorio inexistente.");

		createFile("DATOS");
		createFile("RETPER");
		createFile("NCFACT");

		return "Exportación Finalizada";
	}
	
	private void createFile(String name) {
		String fullFileName = directorio + name + ".txt";
		String sql = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			this.createDocument(fullFileName);
			if (name.equalsIgnoreCase("DATOS")) {
				sql = getSqlDATOS();
				pstmt = DB.prepareStatement(sql);
				rs = pstmt.executeQuery();
				putRsInFileDATOS(rs);
			} else if (name.equalsIgnoreCase("RETPER")) {
				sql = getSqlRETPER();
				pstmt = DB.prepareStatement(sql);
				rs = pstmt.executeQuery();
				putRsInFileRETPER(rs);
			} else if (name.equalsIgnoreCase("NCFACT")) {
				sql = getSqlNCFACT();
				pstmt = DB.prepareStatement(sql);
				rs = pstmt.executeQuery();
				putRsInFileNCFACT(rs);
			}
			this.saveDocument();
		} catch (Exception e) {
			log.saveError("Exportación Percep Tucumán - " + name, e);
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e2) {
				log.saveError("Exportación Percep Tucumán - " + name, e2);
				e2.printStackTrace();
			}
		}
	}
	
	private String getSqlDATOS() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	TO_CHAR(i.dateinvoiced, 'YYYYmmdd') AS FECHA, ");
		sql.append("	bp.taxidtype AS TIPO_DOC, ");
		sql.append("	bp.taxid AS DOCUMENTO, ");
		sql.append("	COALESCE(dt.docsubtypecae,'00') AS TIPO_COMP, ");
		sql.append("	lc.letra AS LETRA, ");
		sql.append("	TRIM(TO_CHAR(COALESCE(i.puntodeventa,0),'0000')) AS COD_LUGAR_EMISION, ");
		sql.append("	TRIM(TO_CHAR(i.numerocomprobante,'00000000')) AS NUMERO, ");
		sql.append("	TRIM(TO_CHAR(it.taxbaseamt,'000000000000.00')) AS BASE_CALCULO, ");
		sql.append("	TRIM(TO_CHAR(COALESCE(it.rate,0),'000.00')) AS PORCENTAJE_ALICUOTA, ");
		sql.append("	TRIM(TO_CHAR(it.taxamt,'000000000000.00')) AS MONTO_RET_PER ");
		sql.append("FROM c_invoicetax it ");
		sql.append("INNER JOIN c_tax t ON it.c_tax_id = t.c_tax_id  ");
		sql.append("INNER JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id ");
		sql.append("INNER JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id  ");
		sql.append("INNER JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id ");
		sql.append("INNER JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id ");
		sql.append("WHERE i.isactive = 'Y' "); 
		sql.append("	AND i.issotrx = 'Y' ");
		sql.append(" 	AND i.ad_client_id = " + getAD_Client_ID() + " ");
		sql.append("	AND t.ispercepcion = 'Y' ");
		sql.append("	AND i.docstatus IN ('CL','CO','VO') ");
		sql.append("	AND t.name ILIKE '%TUCUMAN%' ");
		sql.append(getWhereFilters());
		sql.append("ORDER BY i.dateinvoiced");
		
		return sql.toString();
	}	
	
	private String getSqlRETPER() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	bp.taxidtype AS TIPO_DOC, ");
		sql.append("	bp.taxid AS DOCUMENTO, ");
		sql.append("	translate(bp.name::VARCHAR(40),'ÁÉÍÓÚÑáéíóúñ','AEIOUNaeioun') AS NOMBRE, ");
		sql.append("	translate(l.address1,'ÁÉÍÓÚÑáéíóúñ','AEIOUNaeioun') AS DOMICILIO, ");
		sql.append("	translate(l.city::VARCHAR(15),'ÁÉÍÓÚÑáéíóúñ','AEIOUNaeioun') AS LOCALIDAD, ");
		sql.append("	translate(r.name::VARCHAR(15),'ÁÉÍÓÚÑáéíóúñ','AEIOUNaeioun') AS PROVINCIA, ");
		sql.append("	l.postal AS C_POSTAL ");
		sql.append("FROM c_invoicetax it ");
		sql.append("INNER JOIN c_tax t ON it.c_tax_id = t.c_tax_id  ");
		sql.append("INNER JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id ");
		sql.append("INNER JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id ");
		sql.append("INNER JOIN c_bpartner_location bpl ON bp.c_bpartner_id = bpl.c_bpartner_id ");
		sql.append("LEFT JOIN c_location l ON bpl.c_location_id = l.c_location_id ");
		sql.append("LEFT JOIN c_region r ON l.c_region_id = r.c_region_id  ");
		sql.append("WHERE i.isactive = 'Y' "); 
		sql.append("	AND i.issotrx = 'Y' ");
		sql.append(" 	AND i.ad_client_id = " + getAD_Client_ID() + " ");
		sql.append("	AND t.ispercepcion = 'Y' ");
		sql.append("	AND i.docstatus IN ('CL','CO','VO') ");
		sql.append("	AND t.name ILIKE '%TUCUMAN%' ");
		sql.append(getWhereFilters());
		sql.append("ORDER BY i.dateinvoiced");
		
		return sql.toString();
	}
	
	private String getSqlNCFACT() {		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	TRIM(TO_CHAR(nc.puntodeventa,'0000')) COD_LUGAR_EMISION_NC, ");
		sql.append("	TRIM(TO_CHAR(nc.numerocomprobante,'00000000')) AS NUMERO_NC, ");
		sql.append("	TRIM(TO_CHAR(i.puntodeventa,'0000')) AS COD_LUGAR_EMISION_FAC, ");
		sql.append("	TRIM(TO_CHAR(i.numerocomprobante,'00000000')) AS NUMERO_FAC, ");
		sql.append("	COALESCE(dt.docsubtypecae,'00') AS TIPO_FAC ");
		sql.append("FROM c_invoicetax ncit ");
		sql.append("INNER JOIN c_tax nct ON ncit.c_tax_id = nct.c_tax_id  ");
		sql.append("INNER JOIN c_invoice nc ON nc.c_invoice_id = ncit.c_invoice_id ");
		sql.append("INNER JOIN c_doctype ncdt ON nc.c_doctype_id = ncdt.c_doctype_id  ");
		sql.append("INNER JOIN c_invoice i ON nc.c_invoice_orig_id = i.c_invoice_id ");
		sql.append("INNER JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id  ");
		sql.append("WHERE nc.isactive = 'Y' "); 
		sql.append("	AND nc.issotrx = 'Y' ");
		sql.append(" 	AND nc.ad_client_id = " + getAD_Client_ID() + " ");
		sql.append("	AND nct.ispercepcion = 'Y' ");
		sql.append("	AND nc.docstatus IN ('CL','CO','VO') ");
		sql.append("	AND nct.name ILIKE '%TUCUMAN%' ");
		sql.append("	AND ncdt.doctypekey ILIKE 'CCN%' ");
		sql.append(getWhereFilters());
		sql.append("ORDER BY i.dateinvoiced");
		
		return sql.toString();
	}	
	
	private Object getWhereFilters() {
		StringBuffer sql = new StringBuffer("");
		if (ad_org_id > 0) {
			sql.append(" AND (i.ad_org_id = " + ad_org_id + ")");
		}
		if (date_from != null) {
			sql.append(" AND (date_trunc('day',i.dateinvoiced) >= '" + date_from + "')");
		}
		if (date_to != null) {
			sql.append(" AND (date_trunc('day',i.dateinvoiced) <= '" + date_to + "')");
		}
		return sql.toString();
	}

	private int putRsInFileDATOS(ResultSet rs) throws IOException, SQLException {
		int cant = 0;
		
		while (rs.next()) {
			StringBuffer line = new StringBuffer();
			line.append(rs.getString(1).trim()); 	// FECHA
			line.append(rs.getString(2).trim()); 	// TIPO_DOC
			line.append(rs.getString(3).trim()); 	// DOCUMENTO
			line.append(rs.getString(4).trim()); 	// TIPO_COMP
			line.append(rs.getString(5).trim()); 	// LETRA
			line.append(rs.getString(6).trim()); 	// COD_LUGAR_EMISION
			line.append(rs.getString(7).trim()); 	// NUMERO
			line.append(rs.getString(8).trim()); 	// BASE_CALCULO
			line.append(rs.getString(9).trim()); 	// PORCENTAJE_ALICUOTA
			line.append(rs.getString(10).trim()); 	// MONTO_RET_PER
			line.append("\n"); 						// FIN DE LÍNEA
			this.getFileWriter().write(line.toString());
			cant++;
		}

		return cant;
	}
	
	private int putRsInFileRETPER(ResultSet rs) throws IOException, SQLException {
		int cant = 0;
		
		while (rs.next()) {
			StringBuffer line = new StringBuffer();
			line.append(rs.getString(1).trim()); 				// TIPO_DOC
			line.append(rs.getString(2).trim()); 				// DOCUMENTO
			line.append(padRight(rs.getString(3).trim(),40)); 	// NOMBRE
			
			// Tomo el domicilio completo y opero para quitarle el nro y verificar si supera los 40 char.
			String dom = rs.getString(4).trim();
			String nroDom = getNroDomicilio(dom);
			dom = !nroDom.equals("") ? dom.replaceAll(nroDom, "").replace("  ", " ").trim() : dom;
			dom = dom.length() > 40 ? dom.substring(0,39) : dom;
			
			line.append(padRight(dom, 40));						// DOMICILIO
			line.append(fillWithZerosAtStart(nroDom,5)); 		// NRO
			line.append(padRight(rs.getString(5).trim(),15)); 	// LOCALIDAD
			line.append(padRight(rs.getString(6).trim(),15)); 	// PROVINCIA
			line.append("           ");			 				// NO_USADO
			line.append("    " + rs.getString(7).trim()); 		// C_POSTAL
			line.append("\n"); 									// FIN DE LÍNEA
			this.getFileWriter().write(line.toString());
			cant++;
		}

		return cant;
	}
	
	private String padRight(String str, int n) {
		return String.format("%-" + n + "s", str);
	}
	
	private int putRsInFileNCFACT(ResultSet rs) throws IOException, SQLException {
		int cant = 0;
		
		while (rs.next()) {
			StringBuffer line = new StringBuffer();
			line.append(rs.getString(1).trim()); 	// COD_LUGAR_EMISION_NC
			line.append(rs.getString(2).trim()); 	// NUMERO_NC
			line.append(rs.getString(3).trim()); 	// COD_LUGAR_EMISION_FAC
			line.append(rs.getString(4).trim()); 	// NUMERO_FAC
			line.append(rs.getString(5).trim()); 	// TIPO_FAC
			line.append("\n"); 						// FIN DE LÍNEA
			this.getFileWriter().write(line.toString());
			cant++;
		}

		return cant;
	}
	
	// Metodo que obtiene el numero de domicilio de un string (Asumiendo que es efectivamente un nro)
	// Si no encuentra un nro, retorna "" (vacio).
	private String getNroDomicilio(String dom) {
		String nro = "";
		String[] split = dom.split(" ");
		
		int i = split.length - 1;
		while(i > 0) {
			char chStart = split[i].charAt(0); // Primer char del ultimo sub str del domicilio
			char chEnd = split[i].charAt(split[i].length() - 1); // Ultimo char del ultimo sub str del domicilio
			
			if((chStart >= '0' && chStart <= '9') && (chEnd >= '0' && chEnd <= '9')) { // Si arranca y termina con nro => Asumo que es un nro
				nro = split[i];
				break;
			}
			i--;
		}
		return nro;
	}
	
	// Función para agregar 0s deltante de un string hasta llegar a un largo especificado.
	protected String fillWithZerosAtStart(String str, int len) {
		while(str.length() < len) {
			str = "0" + str;
		}
		return str;
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
	
	/**
	 * Creación del documento de exportación
	 * 
	 * @throws Exception en caso de error en la creación del archivo
	 */
	protected void createDocument(String fullFileName) throws IOException {
		setExportFile(new File(fullFileName));

		FileOutputStream fos = new FileOutputStream(fullFileName);
		OutputStreamWriter osw = new OutputStreamWriter(fos, ENCODE_UTF8);
		BufferedWriter bw = new BufferedWriter(osw);

		setFileWriter(new PrintWriter(bw));
	}
	
	protected void saveDocument() throws Exception {
		// Cierro el archivo
		getFileWriter().close();
	}

}
