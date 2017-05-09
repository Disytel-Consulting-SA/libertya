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
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MExpFormatRow;
import org.openXpertya.model.X_AD_Client;
import org.openXpertya.model.X_AD_ClientInfo;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_AllocationLine;
import org.openXpertya.model.X_C_BankList;
import org.openXpertya.model.X_C_BankListLine;
import org.openXpertya.model.X_C_BankList_Config;
import org.openXpertya.model.X_C_Categoria_Iva;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Location;
import org.openXpertya.model.X_C_Region;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

/**
 * Proceso de exportación de retenciones - Banco Patagonia.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class ExportRetentionsPatagonia extends ExportBankList {

	/** Formato de fechas yyyyMMdd */
	private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
	/** Formato de fechas HHmmss */
	private static final String DEFAULT_HOUR_FORMAT = "HHmmss";
	/** Formato de fecha dia_de_la_semana nro mes año */
	private static final String FULL_DATE_FORMAT = "EEEE dd MMMM yyyy";

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

		sql.append("SELECT ");
		sql.append("	c_banklistline_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_BankListLine.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_banklist_id = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getBankList().getC_BankList_ID());
			rs = ps.executeQuery();

			write(getFileHeader());

			while (rs.next()) {

				write(getLineHeader(rs.getInt(1)));
				write(getLineDetail(rs.getInt(1)));
				write(getLineFooter());
				lineSecNo++;

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

		String documentno = "";
		int totalseqno = 1;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getBankList().getC_BankList_ID());
			rs = ps.executeQuery();
			if (rs.next()) {
				documentno = rs.getString(1);
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
		head.append(zeroFill(documentno, 7)); // Nro. de informante.
		head.append(whiteSpace(80)); // Espacio en blanco.

		return head.toString();
	}

	/** @return Registro cabecera de comprobante */
	private String getLineHeader(int c_banklistline_id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	bl.documentno, ");
		sql.append("	i.dateacct, ");
		sql.append("	i.documentno AS withholdINgno, ");
		sql.append("	i.c_invoice_id, ");
		sql.append("	sucursaldefault ");
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
		sql.append("	INNER JOIN " + X_C_BankList_Config.Table_Name + " blc ");
		sql.append("		ON blc.c_doctype_id = bl.c_doctype_id ");
		sql.append("WHERE ");
		sql.append("	bll.c_banklistline_id = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		String documentno = "";
		String sucursaldefault = "";

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, c_banklistline_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				documentno = rs.getString(1);
				sucursaldefault = rs.getString(5);
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
		head.append(zeroFill(documentno, 7)); // Referencia de la orden de pago.
		head.append(zeroFill(String.valueOf(lineSecNo), 5)); // Nro de comprobante dentro del archivo.
		head.append("22"); // Tipo de certificado de retención o comprobante adjunto.
		head.append("S"); // Incluir firma al documento.
		head.append("BCO"); // Canal de entrega de los comprobantes asociados a pagos electrónicos.
		head.append(zeroFill(sucursaldefault, 0)); // Sucursal a la cual enviar los comprobantes asociados a pagos electrónicos.
		head.append(whiteSpace(80)); // Espacio en blanco.

		return head.toString();
	}

	private String commonMargin() {
		pcCount++;
		registerPcCount++;

		StringBuffer common = new StringBuffer();

		common.append("PC"); // Registro ID.
		common.append(zeroFill(String.valueOf(lineSecNo), 5)); // Nro. de comprobante dentro del archivo.
		common.append(zeroFill(String.valueOf(dailySecNo), 3)); // Nro. de línea de impresión.
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
		detail.append(getTextInfo());
		detail.append(getInvoicesInfo(c_banklistline_id));

		return detail.toString();
	}

	/** @return Registro total de comprobante. */
	private String getLineFooter() {
		StringBuffer total = new StringBuffer();

		total.append("T1"); // Registro ID.
		total.append(zeroFill(registerPcCount, 25)); // Total de líneas a imprimir para este comprobante.
		total.append(zeroFill(registerLineBreaks, 10)); // Total de registros del archivo.
		total.append(whiteSpace(80)); // Espacio en blanco.

		registerPcCount = 0;
		registerLineBreaks = 0;

		return total.toString();
	}

	/** @return Registro total del archivo. */
	protected String getFileFooter() {
		StringBuffer total = new StringBuffer();

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
		sql.append("	INITCAP(cl.city) AS city ");
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
		sql.append("	INNER JOIN " + X_C_Location.Table_Name + " cl ");
		sql.append("		ON cl.c_location_id = ci.c_location_id ");
		sql.append("WHERE ");
		sql.append("	bll.c_banklistline_id = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		Date dateacct = null;
		String withholdingno = "";
		String city = "";

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, c_banklistline_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				withholdingno = rs.getString(5);
				dateacct = new Date(rs.getTimestamp(4).getTime());
				city = rs.getString(7);
				currentInvoiceId = rs.getInt(6);
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
		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + city + ", " + strDate + getRowSeparator());
		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + whiteSpace(37) + "Comprobante de Retencion Nro: " + zeroFill(withholdingno, 7) + getRowSeparator());
		tmp.append(commonMargin() + asciiHr() + getRowSeparator());
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
				tmp.append(fillField(commonMargin() + rs.getString(2), " ", MExpFormatRow.ALIGNMENT_Left, 65, null) + "ORDEN DE PAGO: " + rs.getString(1) + getRowSeparator());
				tmp.append(commonMargin() + "Convenio Multilateral: " + rs.getString(3) + getRowSeparator());
				tmp.append(commonMargin() + rs.getString(4) + getRowSeparator());
				tmp.append(commonMargin() + rs.getString(5) + getRowSeparator());
				tmp.append(commonMargin() + "C.U.I.T. : " + rs.getString(6).replaceAll("(\\d{2})(\\d{8})(\\d{1})", "$1-$2-$3 ") + rs.getString(7) + getRowSeparator());
				tmp.append(commonMargin() + getRowSeparator());
				tmp.append(commonMargin() + asciiHr() + getRowSeparator());
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
				tmp.append(commonMargin() + "NUMERO DE CONTRIBUYENTE: " + rs.getString(1) + getRowSeparator());
				tmp.append(commonMargin() + "NOMBRE O RAZON SOCIAL: " + rs.getString(2) + getRowSeparator());
				tmp.append(commonMargin() + "DOMICILIO: " + (Util.isEmpty(rs.getString(3)) ? "" : rs.getString(3)) + getRowSeparator());
				tmp.append(commonMargin() + "LOCALIDAD: " + (Util.isEmpty(rs.getString(4)) ? "" : rs.getString(4)) + getRowSeparator());
				tmp.append(commonMargin() + "C.U.I.T. : " + rs.getString(5).replaceAll("(\\d{2})(\\d{8})(\\d{1})", "$1-$2-$3 ") + rs.getString(6) + getRowSeparator());
				tmp.append(commonMargin() + getRowSeparator());
				tmp.append(commonMargin() + asciiHr() + getRowSeparator());
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

	private String getTextInfo() {
		StringBuffer tmp = new StringBuffer();

		// TODO
		Date cambiar = new Date();
		DateFormat fmt1 = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat fmt2 = new SimpleDateFormat("MMMM/yyyy", Locale.getDefault());

		BigDecimal grandTotal = new BigDecimal(39627.45);

		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + "                       Por la presente adjuntamos la siguiente nota:             " + getRowSeparator());
		tmp.append(commonMargin() + "Retencion del impuesto a los Ingresos Brutos sobre el pago realizado el          " + getRowSeparator());
		tmp.append(commonMargin() + fmt1.format(cambiar) + " en concepto de IBB SANTA CRUZ -Contribuyente Local                    " + getRowSeparator());
		tmp.append(commonMargin() + "determinado sobre un importe de " + grandTotal.doubleValue() + " (RESOLUCION: 61/995) Comision arbitral." + getRowSeparator());
		tmp.append(commonMargin() + "Esta retencion del mes de " + cap(fmt2.format(cambiar)) + " a ser depositado el mes de            " + getRowSeparator());
		tmp.append(commonMargin() + cap(fmt2.format(cambiar)) + "                                                                     " + getRowSeparator());
		tmp.append(commonMargin() + "En el paquete de pago se encuentran los siguientes comprobantes                  " + getRowSeparator());
		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + "FECHA EMISION                 COMPROBANTE                    IMPORTE             " + getRowSeparator());
		tmp.append(commonMargin() + asciiHr() + getRowSeparator());

		return tmp.toString();
	}

	private String getInvoicesInfo(int c_banklistline_id) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	i.c_invoice_id, ");
		sql.append("	COALESCE(i.numerocomprobante, 0) AS nro, ");
		sql.append("	i.dateinvoiced, ");
		sql.append("	SUM(al.amount) AS amount, ");
		sql.append("	ri.importe_no_imponible_amt, ");
		sql.append("	COALESCE(ri.base_calculo_percent, 100) AS base_percent, ");
		sql.append("	ri.baseimponible_amt, ");
		sql.append("	ri.retencion_percent, ");
		sql.append("	ri.importe_determinado_amt, ");
		sql.append("	c.name ");
		sql.append("FROM ");
		sql.append("	" + X_C_BankList.Table_Name + " bl ");
		sql.append("	INNER JOIN " + X_C_BankListLine.Table_Name + " bll ");
		sql.append("		ON bll.c_banklist_id = bl.c_banklist_id ");
		sql.append("	INNER JOIN " + X_C_AllocationHdr.Table_Name + " a ");
		sql.append("		ON a.c_allocationhdr_id = bll.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_AllocationLine.Table_Name + " al ");
		sql.append("		ON al.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_Invoice.Table_Name + " i ");
		sql.append("		ON al.c_invoice_id = i.c_invoice_id ");
		sql.append("	INNER JOIN " + X_M_Retencion_Invoice.Table_Name + " ri ");
		sql.append("		ON ri.c_allocationhdr_id = a.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_AD_Client.Table_Name + " c ");
		sql.append("		ON c.ad_client_id = i.ad_client_id ");
		sql.append("WHERE ");
		sql.append("	bll.c_banklistline_id = ? ");
		sql.append("GROUP BY ");
		sql.append("	i.c_invoice_id, ");
		sql.append("	i.numerocomprobante, ");
		sql.append("	i.dateinvoiced, ");
		sql.append("	ri.importe_no_imponible_amt, ");
		sql.append("	COALESCE(ri.base_calculo_percent, 100), ");
		sql.append("	ri.baseimponible_amt, ");
		sql.append("	ri.retencion_percent, ");
		sql.append("	ri.importe_determinado_amt, ");
		sql.append("	c.name ");
		sql.append("ORDER BY ");
		sql.append("	i.dateinvoiced, ");
		sql.append("	nro ASC ");

		StringBuffer tmp = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;

		BigDecimal total = BigDecimal.ZERO;
		BigDecimal noImponible = BigDecimal.ZERO;
		BigDecimal basePercent = BigDecimal.ZERO;
		BigDecimal noImponibleImp = BigDecimal.ZERO;
		BigDecimal retencionPercent = BigDecimal.ZERO;
		BigDecimal importeDetermAmt = BigDecimal.ZERO;
		String clientName = "";

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setInt(1, c_banklistline_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				total = total.add(rs.getBigDecimal("amount"));
				noImponibleImp = rs.getBigDecimal(5);
				noImponible = rs.getBigDecimal(7);
				basePercent = rs.getBigDecimal(6);
				retencionPercent = rs.getBigDecimal(8);
				importeDetermAmt = rs.getBigDecimal(9);
				clientName = rs.getString(10);

				Date date = new Date(rs.getTimestamp(3).getTime());
				DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

				tmp.append(fillField(commonMargin() + fmt.format(date), " ", MExpFormatRow.ALIGNMENT_Left, 43, null));
				tmp.append("FAC " + fillField(String.valueOf(rs.getInt(2)), " ", MExpFormatRow.ALIGNMENT_Right, 8, null));
				tmp.append(fillField(String.valueOf(rs.getBigDecimal("amount").doubleValue()), " ", MExpFormatRow.ALIGNMENT_Right, 24, null));
				tmp.append(getRowSeparator());
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

		String tmpStr = NumeroCastellano.numeroACastellano(importeDetermAmt);
		tmpStr = tmpStr.replaceAll("PESOS CON", "......." + getRowSeparator() + commonMargin() + whiteSpace(12) + "CON");

		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + "Este pago" + fillField(String.valueOf(total.doubleValue()), " ", MExpFormatRow.ALIGNMENT_Right, 58, null) + getRowSeparator());
		tmp.append(commonMargin() + "Importe No Imponible" + fillField("" + noImponibleImp.doubleValue(), " ", MExpFormatRow.ALIGNMENT_Right, 47, null) + getRowSeparator());
		tmp.append(commonMargin() + "Porcentaje Base Calculo %" + fillField("" + basePercent.doubleValue(), " ", MExpFormatRow.ALIGNMENT_Right, 42, null)  + getRowSeparator());
		tmp.append(commonMargin() + "Base Imponible" + fillField(String.valueOf(total.doubleValue()), " ", MExpFormatRow.ALIGNMENT_Right, 53, null) + getRowSeparator());
		tmp.append(commonMargin() + "Alicuota %" + fillField("" + retencionPercent.doubleValue(), " ", MExpFormatRow.ALIGNMENT_Right, 57, null) +  getRowSeparator());
		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + "IMPORTE RETENIDO" + fillField("" + importeDetermAmt.doubleValue(), " ", MExpFormatRow.ALIGNMENT_Right, 51, null) +  getRowSeparator());
		tmp.append(commonMargin() + "SON PESOS:  " + tmpStr + getRowSeparator());
		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + getRowSeparator());
		tmp.append(commonMargin() + whiteSpace(27) + clientName + " - AdmCentral");

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