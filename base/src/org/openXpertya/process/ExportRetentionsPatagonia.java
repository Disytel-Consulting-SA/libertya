package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBankList;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MExpFormatRow;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.X_AD_Client;
import org.openXpertya.model.X_AD_ClientInfo;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_BankList;
import org.openXpertya.model.X_C_BankListLine;
import org.openXpertya.model.X_C_BankList_Config;
import org.openXpertya.model.X_C_Categoria_Iva;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Location;
import org.openXpertya.model.X_C_Region;
import org.openXpertya.model.X_C_RetencionSchema;
import org.openXpertya.model.X_C_RetencionType;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

/**
 * Proceso de exportación de retenciones - Banco Patagonia.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class ExportRetentionsPatagonia extends ExportBankList {
	
	/** Provincias */
	private static Map<String, String> provincias;
	static {
		provincias = new HashMap<String, String>();
		provincias.put("0", "0000");
		provincias.put("CORE-C_Region-1000082", "3");
		provincias.put("CORE-C_Region-1000083", "4");
		provincias.put("CORE-C_Region-1000084", "5");
		provincias.put("CORE-C_Region-1000087", "6");
		provincias.put("CORE-C_Region-1000088", "7");
		provincias.put("CORE-C_Region-1000085", "8");
		provincias.put("CORE-C_Region-1000086", "9");
		provincias.put("CORE-C_Region-1000089", "10");
		provincias.put("CORE-C_Region-1000090", "11");
		provincias.put("CORE-C_Region-1000091", "12");
		provincias.put("CORE-C_Region-1000092", "13");
		provincias.put("CORE-C_Region-1000093", "14");
		provincias.put("CORE-C_Region-1000094", "15");
		provincias.put("CORE-C_Region-1000095", "16");
		provincias.put("CORE-C_Region-1000096", "17");
		provincias.put("CORE-C_Region-1000097", "18");
		provincias.put("CORE-C_Region-1000098", "19");
		provincias.put("CORE-C_Region-1000099", "20");
		provincias.put("CORE-C_Region-1000100", "21");
		provincias.put("CORE-C_Region-1000101", "22");
		provincias.put("CORE-C_Region-1000102", "23");
		provincias.put("CORE-C_Region-1000103", "24");
		provincias.put("CORE-C_Region-1000105", "25");
		provincias.put("CORE-C_Region-1000104", "26");
	}

	/** Formato de fechas yyyyMMdd */
	private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
	/** Formato de fechas HHmmss */
	private static final String DEFAULT_HOUR_FORMAT = "HHmmss";
	/** Formato de fecha dia_de_la_semana nro mes año */
	private static final String FULL_DATE_FORMAT = "EEEE dd MMMM yyyy";
	/**
	 * Longitud de cada línea de detalle, registros PC. Siempre deben ser 2
	 * caracteres menos ya que los primeros 2 se usan para el margen izquierdo.
	 * En este caso, la longitud según el formato es de 132.
	 */
	private static final Integer PC_LINE_LENGTH = 130;
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/** Secuencia para nro. de archivo enviado en el dia. */
	private int dailySecNo;
	/** Secuencia para nro. de comprobante dentro del archivo. */
	private int lineSecNo;
	/** ID de factura de retencion. */
	private int currentInvoiceId;
	/** Cantidad de lineas a imprimir. */
	private int pcCount;
	/** Cantidad de lineas a imprimir, por comprobante. */
	private int registerPcCount;
	/** Cantidad de lineas del archivo. */
	private int lineBreaks;
	/** Cantidad de lineas, por comprobante. */
	private int registerLineBreaks;

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public ExportRetentionsPatagonia(Properties ctx, MBankList bankList, String trxName) {
		super(ctx, bankList, trxName);
	}

	@Override
	protected void prepare() {
		super.prepare();
		dailySecNo = 1;
		lineSecNo = 1;
		pcCount = 0;
		registerPcCount = 0;
		lineBreaks = 0;
		registerLineBreaks = 0;
	}

	@Override
	protected String getBankListExportFormatValue() {
		return "LPR";
	}

	@Override
	protected void loadExpFormatRows() {
		// Dado que el formato del archivo es complejo,
		// no utiliza un formato definido por columnas.
		return;
	}

	@Override
	protected void fillDocument() throws Exception {
		// Dado que el formato del archivo es complejo, no basta con una query
		// sobre la cual iterar para llenar el archivo, es por eso que se
		// redefine este método.

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT distinct ");
		sql.append("	bll.c_banklistline_id, ");
		sql.append("	bll.line, ");
		sql.append("	ri.c_invoice_id, ");
		sql.append("	i.documentno ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " a ");
		sql.append("	INNER JOIN " + X_C_BankListLine.Table_Name + " bll ");
		sql.append("		ON bll.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_BankList.Table_Name + " bl ");
		sql.append("		ON bl.c_banklist_id = bll.c_banklist_id ");
		sql.append("	INNER JOIN " + X_M_Retencion_Invoice.Table_Name + " ri ");
		sql.append("		ON ri.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_Invoice.Table_Name + " i ");
		sql.append("		ON ri.c_invoice_id = i.c_invoice_id ");
		sql.append("WHERE ");
		sql.append("	bll.c_banklist_id = ? ");
		sql.append("ORDER BY bll.line, i.documentno ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getBankList().getC_BankList_ID());
			rs = ps.executeQuery();

			write(getFileHeader());

			while (rs.next()) {
				lineSecNo++;
				currentInvoiceId = rs.getInt("c_invoice_id");
				write(getLineHeader(rs.getInt("c_banklistline_id")));
				write(getLineDetail(rs.getInt("c_banklistline_id")));
				write(getLineFooter());
			}

			write(getFileFooter());

		} catch (Exception e) {
			log.log(Level.SEVERE, "getFileHeader", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
	}

	@Override
	protected String getFilePath() {
		StringBuffer filepath = new StringBuffer(getExportFormat().getFileName());
		filepath.append("PC"); // Constante.
		filepath.append(zeroFill(getBankListConfig().getRegisterNumber(), 7)); // Nro. de adherente al servicio de pago a proveedores.
		filepath.append(zeroFill(dailySecNo, 3)); // Nro. de éste archivo, enviado en el día.
		filepath.append(".");
		filepath.append(getFileExtension());
		return filepath.toString();
	}

	@Override
	protected void write(String line) throws Exception {
		// Escribir la linea.
		super.write(line);
		// Meter el separador de línea.
		super.write(getRowSeparator());
		// Aumentar la cantidad de líneas exportadas.
		setExportedLines(getExportedLines() + 1);
	}

	/**
	 * Devuelve una secuencia de caracteres de espacio.
	 * @param length longitud de la cadena resultante.
	 * @return Cadena de caracteres de espacio.
	 */
	private String whiteSpace(int length) {
		return fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, length, null);
	}

	/**
	 * Devuelve una secuencia de caracteres conformada por un dato con ceros delante.
	 * @param data dato al final de la cadena.
	 * @param length longitud de la cadena resultante.
	 * @return Cadena de caracteres conformada por ceros y el dato ingresado, al final.
	 */
	private String zeroFill(String data, int length) {
		return fillField(data, "0", MExpFormatRow.ALIGNMENT_Right, length, null);
	}

	/**
	 * Devuelve una secuencia de caracteres conformada por un dato con ceros delante.
	 * @param data dato al final de la cadena.
	 * @param length longitud de la cadena resultante.
	 * @return Cadena de caracteres conformada por ceros y el dato ingresado, al final.
	 */
	private String zeroFill(String data, int length, String alignment) {
		return fillField(data, "0", alignment, length, null);
	}
	
	/**
	 * Devuelve una secuencia de caracteres conformada por un dato con ceros delante.
	 * @param data dato al final de la cadena.
	 * @param length longitud de la cadena resultante.
	 * @return Cadena de caracteres conformada por ceros y el dato ingresado, al final.
	 */
	private String zeroFill(int data, int length) {
		return fillField(String.valueOf(data), "0", MExpFormatRow.ALIGNMENT_Right, length, null);
	}

	private String asciiHr() {
		return fillField("-", "-", MExpFormatRow.ALIGNMENT_Right, 80, null);
	}

	/** @return Registro cabecera del archivo. */
	protected String getFileHeader() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	documentno, ");
		sql.append("	dailyseqno, ");
		sql.append("	totalseqno ");
		sql.append("FROM ");
		sql.append("	" + X_C_BankList.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_banklist_id = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		int totalseqno = 1;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getBankList().getC_BankList_ID());
			rs = ps.executeQuery();
			if (rs.next()) {
				dailySecNo = rs.getInt(2);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getFileHeader", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		SimpleDateFormat shf = new SimpleDateFormat(DEFAULT_HOUR_FORMAT);
		String currentDate = sdf.format(today);
		String currentTime = shf.format(today);

		StringBuffer head = new StringBuffer();

		head.append("FH"); // Registro ID.
		head.append("PC").append(zeroFill(getBankListConfig().getRegisterNumber(), 7)).append(currentDate).append(zeroFill(dailySecNo, 3)); // Id de Archivo.
		head.append(currentTime); // Hora de creación del archivo.
		head.append(zeroFill(totalseqno, 7)); // Nro. secuencial del archivo p/adherente.
		head.append("COMPROBADJU"); // Identificación de archivo.
		head.append("0000001"); // Nro de Convenio. TODO ver de donde sale.
		head.append(whiteSpace(29)); // Espacio en blanco.
		head.append("ADH"); // Informante.
		head.append(fillField(getBankListConfig().getRegisterNumber(), "0", MExpFormatRow.ALIGNMENT_Right, 7, null)); // Nro. de informante.
		head.append(whiteSpace(80)); // Espacio en blanco.

		return head.toString();
	}

	/** @return Registro cabecera de comprobante */
	private String getLineHeader(int c_banklistline_id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	a.documentno, ");
		sql.append("	i.dateacct, ");
		sql.append("	i.documentno AS withholdINgno, ");
		sql.append("	i.c_invoice_id, ");
		sql.append("	sucursaldefault, ");
		sql.append("	rt.retentiontype, ");
		sql.append("	COALESCE(r.ad_componentobjectuid,'0') AS provincia ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " a ");
		sql.append("	INNER JOIN " + X_C_BankListLine.Table_Name + " bll ");
		sql.append("		ON bll.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_BankList.Table_Name + " bl ");
		sql.append("		ON bl.c_banklist_id = bll.c_banklist_id ");
		sql.append("	INNER JOIN " + X_M_Retencion_Invoice.Table_Name + " ri ");
		sql.append("		ON ri.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("		ON rs.c_retencionschema_id = ri.c_retencionschema_id ");
		sql.append("	INNER JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("		ON rt.c_retenciontype_id = rs.c_retenciontype_id ");
		sql.append("	LEFT JOIN " + X_C_Region.Table_Name + " r ");
		sql.append("		ON r.c_region_id = rs.c_region_id ");
		sql.append("	INNER JOIN " + X_C_Invoice.Table_Name + " i ");
		sql.append("		ON i.c_invoice_id = ri.c_invoice_id ");
		sql.append("	INNER JOIN " + X_C_BankList_Config.Table_Name + " blc ");
		sql.append("		ON blc.c_doctype_id = bl.c_doctype_id ");
		sql.append("WHERE ");
		sql.append("	bll.c_banklistline_id = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		String documentno = "";
		String sucursaldefault = "";
		String retentionType = "";
		String provincia = "";

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, c_banklistline_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				documentno = rs.getString(1);
				sucursaldefault = rs.getString(5);
				retentionType = rs.getString("retentiontype");
				provincia = rs.getString("provincia");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getFileHeader", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
		StringBuffer head = new StringBuffer();

		head.append("H1"); // Registro ID.
		head.append(whiteSpace(25)); // Nro. de Beneficiario del pago.
		head.append(fillField(documentno, " ", MExpFormatRow.ALIGNMENT_Left, 25, null)); // Referencia de la orden de pago.
		head.append(zeroFill(String.valueOf(lineSecNo), 5)); // Nro de comprobante dentro del archivo.
		head.append(zeroFill(getRentencionCode(retentionType, provincia), 3)); // Tipo de certificado de retención o comprobante adjunto.
		head.append("S"); // Incluir firma al documento.
		head.append("BCO"); // Canal de entrega de los comprobantes asociados a pagos electrónicos.
		head.append(zeroFill(sucursaldefault, 8)); // Sucursal a la cual enviar los comprobantes asociados a pagos electrónicos.
		head.append(whiteSpace(80)); // Espacio en blanco.

		return head.toString();
	}
	
	private String getRentencionCode(String retentionType, String provincia) {
		if ("I".equals(retentionType)) 
			return "1";
		else if ("G".equals(retentionType)) 
			return "2";
		else if ("S".equals(retentionType) || "J".equals(retentionType)) 
			return "3";
		else if ("B".equals(retentionType))
			return provincias.get(provincia);
		else
			return null;
	}
	
	private String getPCLine(String data){
		data = data != null?data:"";
		return commonLeftMargin()+data+commonRightMargin(data);
	}
	
	private String getLastPCLine(String data){
		data = data != null?data:"";
		return commonLeftMargin()+data+commonRightMarginNoRowSeparator(data);
	}
	
	private String getBlankPCLine(){
		return getPCLine(null);
	}

	private String commonRightMargin(String data) {
		Integer dataLength = data.length();
		return (dataLength >= PC_LINE_LENGTH?"":whiteSpace(PC_LINE_LENGTH - dataLength))+getRowSeparator();
	}
	
	private String commonRightMarginNoRowSeparator(String data) {
		Integer dataLength = data.length();
		return (dataLength >= PC_LINE_LENGTH?"":whiteSpace(PC_LINE_LENGTH - dataLength));
	}
	
	private String commonLeftMargin() {
		pcCount++;
		registerPcCount++;

		StringBuffer common = new StringBuffer();

		common.append("PC"); // Registro ID.
		common.append(zeroFill(String.valueOf(lineSecNo), 5)); // Nro. de comprobante dentro del archivo.
		common.append(zeroFill(String.valueOf(registerPcCount), 3)); // Nro. de línea de impresión.
		common.append("  "); // Línea de impresión.

		return common.toString();
	}

	@Override
	public String getRowSeparator() {
		lineBreaks++;
		registerLineBreaks++;
		return super.getRowSeparator();
	}

	/** @return Detalle de comprobante */
	private String getLineDetail(int c_banklistline_id) {
		StringBuffer detail = new StringBuffer();

		detail.append(getDocumentInfo(c_banklistline_id));
		detail.append(getCompanyInfo(c_banklistline_id));
		detail.append(getBPartnerInfo());
		detail.append(getTextInfo(c_banklistline_id));
		detail.append(getInvoicesInfo(c_banklistline_id));

		return detail.toString();
	}

	/** @return Registro total de comprobante. */
	private String getLineFooter() {
		StringBuffer total = new StringBuffer();

		total.append("T1"); // Registro ID.
		total.append(zeroFill(registerPcCount, 25)); // Total de líneas a imprimir para este comprobante.
		total.append(zeroFill(registerLineBreaks, 10)); // Total de registros del archivo.
		total.append(whiteSpace(40)); // Espacio en blanco.

		registerPcCount = 0;
		registerLineBreaks = 0;

		return total.toString();
	}

	/** @return Registro total del archivo. */
	protected String getFileFooter() {
		StringBuffer total = new StringBuffer();
		lineBreaks++;

		total.append("FT"); // Registro ID.
		total.append(zeroFill(pcCount, 25)); // Total de líneas a imprimir de todos los comprobantes.
		total.append(zeroFill(lineBreaks, 10)); // Total de registros del archivo.
		total.append(whiteSpace(8)); // Número de checksum.
		total.append(whiteSpace(46)); // Espacio en blanco.

		return total.toString();
	}

	private String getDocumentInfo(int c_banklistline_id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	bl.documentno, ");
		sql.append("	bl.dailyseqno, ");
		sql.append("	bl.totalseqno, ");
		sql.append("	i.dateacct, ");
		sql.append("	i.documentno AS withholdingno, ");
		sql.append("	i.c_invoice_id, ");
		sql.append("	CASE ");
		sql.append("	  WHEN cl.city IS NOT NULL AND cl.city != '' THEN cl.city ");
		sql.append("	  WHEN r.name IS NOT NULL AND r.name != '' THEN r.name ");
		sql.append("	  ELSE '' ");
		sql.append("	END as city ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " a ");
		sql.append("	INNER JOIN " + X_C_BankListLine.Table_Name + " bll ");
		sql.append("		ON bll.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_BankList.Table_Name + " bl ");
		sql.append("		ON bl.c_banklist_id = bll.c_banklist_id ");
		sql.append("	INNER JOIN " + X_M_Retencion_Invoice.Table_Name + " ri ");
		sql.append("		ON ri.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_Invoice.Table_Name + " i ");
		sql.append("		ON i.c_invoice_id = ri.c_invoice_id ");
		sql.append("	INNER JOIN " + X_AD_Client.Table_Name + " c ");
		sql.append("		ON c.ad_client_id = a.ad_client_id ");
		sql.append("	INNER JOIN " + X_AD_ClientInfo.Table_Name + " ci ");
		sql.append("		ON c.ad_client_id = ci.ad_client_id ");
		sql.append("	LEFT JOIN " + X_C_Location.Table_Name + " cl ");
		sql.append("		ON cl.c_location_id = ci.c_location_id ");
		sql.append("	LEFT JOIN " + X_C_Region.Table_Name + " r ");
		sql.append("		ON cl.c_region_id = r.c_region_id  ");
		sql.append("WHERE ");
		sql.append("	bll.c_banklistline_id = ? ");
		sql.append("	AND ri.c_invoice_id = ? ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		Date dateacct = null;
		String withholdingno = "";
		String city = "";

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, c_banklistline_id);
			ps.setInt(2, currentInvoiceId);
			rs = ps.executeQuery();
			if (rs.next()) {
				withholdingno = rs.getString(5);
				dateacct = new Date(rs.getTimestamp(4).getTime());
				city = rs.getString(7);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getDocumentInfo", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
		DateFormat fmt = new SimpleDateFormat(FULL_DATE_FORMAT, Locale.getDefault());
		String strDate = "";
		if (dateacct != null) {
			strDate = fmt.format(dateacct);
			// Paso a title case dia y mes.
			strDate = cap(strDate);
			// Agrego los "de" a la fecha.
			strDate = strDate.replaceAll("(\\w+\\s\\d{2})\\s(\\w+)\\s(\\d{4})", "$1 de $2 de $3");
		}
		StringBuffer tmp = new StringBuffer();
		tmp.append(getBlankPCLine());
		tmp.append(getPCLine(city + ", " + strDate));
		tmp.append(getBlankPCLine());
		tmp.append(getPCLine(whiteSpace(37) + "Comprobante de Retencion Nro: " + zeroFill(withholdingno, 7)));
		tmp.append(getPCLine(asciiHr()));
		return tmp.toString();
	}

	private String getCompanyInfo(int c_banklistline_id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	a.documentno, ");
		sql.append("	c.name, ");
		sql.append("	ci.iibb, ");
		sql.append("	INITCAP(cl.address1 || cl.address2) AS address, ");
		sql.append("	INITCAP('(' || cl.postal || ') ' || cl.city || ' - ' || cr.name) AS town, ");
		sql.append("	TRANSLATE(ci.cuit, '-', '') AS cuit, ");
		sql.append("	INITCAP(ct.name) AS taxcategORy ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " a ");
		sql.append("	INNER JOIN " + X_C_BankListLine.Table_Name + " bll ");
		sql.append("		ON bll.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_AD_Client.Table_Name + " c ");
		sql.append("		ON c.ad_client_id = a.ad_client_id ");
		sql.append("	INNER JOIN " + X_AD_ClientInfo.Table_Name + " ci ");
		sql.append("		ON c.ad_client_id = ci.ad_client_id ");
		sql.append("	INNER JOIN " + X_C_Location.Table_Name + " cl ");
		sql.append("		ON cl.c_location_id = ci.c_location_id ");
		sql.append("	INNER JOIN " + X_C_Region.Table_Name + " cr ");
		sql.append("		ON cr.c_region_id = cl.c_region_id ");
		sql.append("	INNER JOIN " + X_C_Categoria_Iva.Table_Name + " ct ");
		sql.append("		ON ct.c_categoria_iva_id = ci.c_categoria_iva_id ");
		sql.append("WHERE ");
		sql.append("	bll.c_banklistline_id = ?");

		StringBuffer tmp = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, c_banklistline_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				tmp.append(getPCLine(fillField(rs.getString(2), " ", MExpFormatRow.ALIGNMENT_Left, 53, null) + "ORDEN DE PAGO: " + rs.getString(1)));
				tmp.append(getPCLine("Convenio Multilateral: " + rs.getString(3)));
				tmp.append(getPCLine(rs.getString(4)));
				tmp.append(getPCLine(rs.getString(5)));
				tmp.append(getPCLine("C.U.I.T. : " + rs.getString(6).replaceAll("(\\d{2})(\\d{8})(\\d{1})", "$1-$2-$3 ") + rs.getString(7)));
				tmp.append(getBlankPCLine());
				tmp.append(getPCLine(asciiHr()));
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getCompanyInfo", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
		return tmp.toString();
	}

	private String getBPartnerInfo() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	bpr.taxid, ");
		sql.append("	INITCAP(bp.name), ");
		sql.append("	INITCAP(bpl.address1 || bpl.address2) AS address, ");
		sql.append("	INITCAP('(' || bpl.postal || ') ' || bpl.city || ' - ' || bpr.name) AS town, ");
		sql.append("	COALESCE(bp.taxid, i.cuit) AS cuit, ");
		sql.append("	INITCAP(bpt.name) AS taxcategory ");
		sql.append("FROM ");
		sql.append("	libertya.c_INvoice i ");
		sql.append("	INNER JOIN libertya.c_bpartner bp ");
		sql.append("		ON i.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("	INNER JOIN libertya.c_bpartner_retencion bpr ");
		sql.append("		ON bpr.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("	LEFT OUTER JOIN libertya.c_location bpl ");
		sql.append("		ON bpl.c_location_id = bp.c_location_id ");
		sql.append("	LEFT OUTER JOIN libertya.c_categoria_iva bpt ");
		sql.append("		ON bpt.c_categoria_iva_id = bp.c_categoria_iva_id ");
		sql.append("WHERE ");
		sql.append("	i.c_invoice_id = ?");

		StringBuffer tmp = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, currentInvoiceId);
			rs = ps.executeQuery();
			if (rs.next()) {
				tmp.append(getPCLine("NUMERO DE CONTRIBUYENTE: " + rs.getString(1)));
				tmp.append(getPCLine("NOMBRE O RAZON SOCIAL: " + rs.getString(2)));
				tmp.append(getPCLine("DOMICILIO: " + (Util.isEmpty(rs.getString(3)) ? "" : rs.getString(3))));
				tmp.append(getPCLine("LOCALIDAD: " + (Util.isEmpty(rs.getString(4)) ? "" : rs.getString(4))));
				tmp.append(getPCLine("C.U.I.T. : " + rs.getString(5).replaceAll("(\\d{2})(\\d{8})(\\d{1})", "$1-$2-$3 ") + rs.getString(6)));
				tmp.append(getBlankPCLine());
				tmp.append(getPCLine(asciiHr()));
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getBPartnerInfo", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
		return tmp.toString();
	}

	private String getTextInfo(int c_banklistline_id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	CASE WHEN rt.retentiontype = 'I' THEN 'de IVA' ");
		sql.append("		WHEN rt.retentiontype = 'G' THEN 'a las Ganancias' ");
		sql.append("		WHEN rt.retentiontype = 'B' THEN 'a los Ingresos Brutos' ");
		sql.append("		WHEN rt.retentiontype = 'S' THEN 'de S.U.S.S.' ");
		sql.append("		WHEN rt.retentiontype = 'J' THEN 'de S.I.J.P.' ");
		sql.append("	ELSE '0' END AS impuesto, ");
		sql.append("	a.dateacct as fechaop, ");
		sql.append("	i.dateacct as fecharet, ");
		sql.append("	rs.name as concepto, ");
		sql.append("	ri.baseimponible_amt, ");
		sql.append("	ri.pago_actual_amt ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " a ");
		sql.append("	INNER JOIN " + X_C_BankListLine.Table_Name + " bll ");
		sql.append("		ON bll.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_BankList.Table_Name + " bl ");
		sql.append("		ON bl.c_banklist_id = bll.c_banklist_id ");
		sql.append("	INNER JOIN " + X_M_Retencion_Invoice.Table_Name + " ri ");
		sql.append("		ON ri.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("		ON rs.c_retencionschema_id = ri.c_retencionschema_id ");
		sql.append("	INNER JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("		ON rt.c_retenciontype_id = rs.c_retenciontype_id ");
		sql.append("	INNER JOIN " + X_C_Invoice.Table_Name + " i ");
		sql.append("		ON i.c_invoice_id = ri.c_invoice_id ");
		sql.append("WHERE ");
		sql.append("	bll.c_banklistline_id = ? ");
		sql.append("	AND ri.c_invoice_id = ? ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		String impuesto = "";
		Date fechaop = null;
		Date fecharet = null;
		String concepto = "";
		BigDecimal baseImponible = null;
		BigDecimal pagoActual = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, c_banklistline_id);
			ps.setInt(2, currentInvoiceId);
			rs = ps.executeQuery();
			if (rs.next()) {
				//Verifico tipo de retención, si falta configurar disparo error
				if ("0".equals(rs.getString("impuesto"))) {
					throw new Exception("Existen Tipos de Retenciones sin configurar");
				}
				impuesto = rs.getString("impuesto");
				fechaop = new Date(rs.getTimestamp("fechaop").getTime());
				fecharet = new Date(rs.getTimestamp("fecharet").getTime());
				concepto = rs.getString("concepto");
				baseImponible = rs.getBigDecimal("baseimponible_amt");
				pagoActual = rs.getBigDecimal("pago_actual_amt");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getFileHeader", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
		
		StringBuffer tmp = new StringBuffer();
		DateFormat fmt1 = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat fmt2 = new SimpleDateFormat("MMMM/yyyy", Locale.getDefault());

		tmp.append(getBlankPCLine());
		tmp.append(getBlankPCLine());
		tmp.append(getPCLine("                       Por la presente adjuntamos la siguiente nota:             "));
		tmp.append(getPCLine("Retencion del impuesto " + impuesto + " sobre el pago realizado el          "));
		tmp.append(getPCLine(fmt1.format(fechaop) + " en concepto de " + concepto));
		tmp.append(getPCLine("determinado sobre un importe de " + pagoActual.doubleValue()));
		tmp.append(getPCLine("Esta retencion del mes de " + cap(fmt2.format(fecharet)) + " a ser depositado el mes de            "));
		tmp.append(getPCLine(cap(fmt2.format(fecharet)) + "                                                                     "));
		tmp.append(getPCLine("En el paquete de pago se encuentran los siguientes comprobantes                  "));
		tmp.append(getBlankPCLine());
		tmp.append(getPCLine("FECHA EMISION                                     COMPROBANTE                    "));
		tmp.append(getPCLine(asciiHr()));

		return tmp.toString();
	}

	private String getInvoicesInfo(int c_banklistline_id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT c_allocationhdr_id ");
		sql.append("FROM " + X_C_BankListLine.Table_Name + " bll ");
		sql.append("WHERE bll.c_banklistline_id = ? ");

		int allocationHdr = DB.getSQLValue(get_TrxName(), sql.toString(), c_banklistline_id);
		MAllocationHdr aHdr = new MAllocationHdr(getCtx(), allocationHdr, get_TrxName());
		MInvoice currentInvoice = new MInvoice(getCtx(), currentInvoiceId, get_TrxName()); 
		DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		
		StringBuffer tmp = new StringBuffer();
		for (MInvoice debit : aHdr.getAllocationDebits()) {
			tmp.append(getPCLine(fillField(fmt.format(debit.getDateInvoiced()), " ", MExpFormatRow.ALIGNMENT_Left, 41, null)
					+ fillField(String.valueOf(debit.getDocumentNo()), " ", MExpFormatRow.ALIGNMENT_Right, 36, null)));
		}

		String tmpStr = NumeroCastellano.numeroACastellano(currentInvoice.getGrandTotal());
		//tmpStr = tmpStr.replaceAll("PESOS CON", "......." + getRowSeparator() + commonMargin() + whiteSpace(12) + "CON");

		tmp.append(getBlankPCLine());
		tmp.append(getBlankPCLine());
		tmp.append(getPCLine("IMPORTE RETENIDO" + fillField("" + currentInvoice.getGrandTotal().doubleValue(), " ",
				MExpFormatRow.ALIGNMENT_Right, 51, null)));
		tmp.append(getBlankPCLine());
		tmp.append(getBlankPCLine());
		tmp.append(getLastPCLine(whiteSpace(27) + MClient.get(getCtx()).getName() + " - AdmCentral"));

		return tmp.toString();
	}

	/**
	 * Capitaliza cada palabra.
	 * @param string cadena a transformar.
	 * @return cadena transformada.
	 */
	private String cap(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	@Override
	protected void validate() throws Exception {
		BankListConfigFieldsException blcfe = new BankListConfigFieldsException(getCtx(),
				getBankList().getC_DocType_ID(), new ArrayList<String>());
		if(Util.isEmpty(getBankListConfig().getSucursalDefault(), true)){
			blcfe.addField("SucursalDefault");
		}
		if(Util.isEmpty(getBankListConfig().getRegisterNumber(), true)){
			blcfe.addField("RegisterNumber");
		}
		if(blcfe.getFields().size() > 0){
			throw blcfe;
		}
	}

}