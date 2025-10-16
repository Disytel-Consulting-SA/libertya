package org.adempiere.utils;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.Waiting;
import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MAttachmentEntry;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MSysConfig;
import org.openXpertya.model.MUser;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.Task;
import org.openXpertya.util.CPreparedStatement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.JXLException;
import jxl.Sheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Miscfunc {

	// utilizadas para el calculo de pago facil...
	private static String IdentEmpresa = "0123";
	private static String Moneda = "0";
	private static int[] PrimerString = { 1, 3, 5, 7, 9, 3, 5, 7, 9, 3, 5, 7,
			9, 3, 5, 7, 9, 3, 5, 7, 9, 3, 5, 7, 9, 3, 5, 7, 9, 3, 5, 7, 9, 3,
			5, 7, 9, 3, 5, 7, 9 };

	public static boolean isEmpty(BigDecimal numValue, boolean withZeroCheck) {
		return (numValue == null)
				|| (withZeroCheck ? numValue.compareTo(BigDecimal.ZERO) == 0
						: false);
	} // isEmpty
	
	public static int getMonth(Date fecha) {
		int a = 0;
		String s = FechaAMD(fecha);

		a = Integer.valueOf(s.substring(5, 7));
		return a;
	}

	public static int getYear(Date fecha) {
		int a = 0;
		String s = FechaAMD(fecha);

		a = Integer.valueOf(s.substring(0, 4));
		return a;
	}
	
	public static int obtenerAnio(Date date){

	    if (null == date){

	        return 0;

	    }
	    else{

	        String formato="yyyy";
	        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
	        return Integer.parseInt(dateFormat.format(date));

	    }

	}
	
	public static int obtenerMes(Date date){

	    if (null == date){

	        return 0;

	    }
	    else{

	        String formato="MM";
	        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
	        return Integer.parseInt(dateFormat.format(date));

	    }

	}
	
	public static int obtenerDia(Date date){

	    if (null == date){

	        return 0;

	    }
	    else{

	        String formato="dd";
	        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
	        return Integer.parseInt(dateFormat.format(date));

	    }

	}
	
	public static boolean isCargaMinima(int ad_role_id)
	{
		String rolesEspeciales = Miscfunc.LoadRolesCargaDatosMinimos();
		String [] cargaminima = rolesEspeciales.split(",");
		boolean isCargaminima = false;

		for (int index=0 ; index < cargaminima.length; index++){

			if (ad_role_id==Integer.valueOf(cargaminima[index]))
			{
				isCargaminima=true; 
				break;
			}
			
		}
		
		return isCargaminima;
	}
	
	public static int getC_Period(Timestamp fecha)
	{
		int c_period_id = -1;
		
		try
		{
			String sql = "select c_period_id from c_period where startdate <= '" + fecha + "' and enddate >= '" + fecha + "'";
			c_period_id = DB.getSQLValue(null, sql);
			
		}
		catch(Exception ex)
		{
			System.out.println("Error al obtener periodo \n" + ex.getMessage());
		}
		
		return c_period_id;
	}

	public static int getDayOfTheWeek(Timestamp t)
	{
		Date d = TimestampToDate (t);
		
		return getDayOfTheWeek(d);	
	}
	
	public static Date TimestampToDate (Timestamp t)
	{
		Date d = new Date(t.getTime());
	
		return d;
	}
	
	public static int getDayOfTheWeek(Date d){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		return cal.get(Calendar.DAY_OF_WEEK);		
	}
	
	public static String getNombrePeriodo(int C_Period_ID){
		String periodo = "";
		
		try{

			// Nombre del Periodo Seleccionado			
			String sql = "SELECT name" +
					"  FROM C_Period" + 
					"  WHERE C_Period_ID = " + C_Period_ID;
			periodo = DB.getSQLValueString(null, sql);
			
			if(periodo == null || periodo.isEmpty())
				periodo = "Sin Periodo Asignado";
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return periodo;
	}
	
	public static String getNombrePeriodo(Timestamp fecha){
		String periodo = "";
		
		try{
			periodo = getNombrePeriodo(getC_Period(fecha));
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return periodo;
	}
	
	public static String getNombreDiaDeLaSemana(int diadelasemana)
	{
	   String dia = "";
	   
	   switch(diadelasemana)
	   {
	   		case 1:
	   			dia = "Domingo";
	   			break;
	   		case 2:
	   			dia = "Lunes";
	   			break;
	   		case 3:
	   			dia = "Martes";
	   			break;
	   		case 4:
	   			dia = "Miercoles";
	   			break;
	   		case 5:
	   			dia = "Jueves";
	   			break;
	   		case 6:
	   			dia = "Viernes";
	   			break;
	   		case 7:
	   			dia = "Sabado";
	   			break;
	   		default:
	   			dia = "DIA INVALIDO";
	   			break;
	   			
	   }
		
	   return dia;
	}
	
	public static Timestamp StringToTimestamp(String fecha)
	{
		Timestamp valFecha = null;
		
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			java.util.Date date = sdf.parse(fecha + " 00:00:00.000");
			valFecha = new java.sql.Timestamp(date.getTime());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return valFecha;
	}


	public static BigDecimal calculaInteres(Date fecha, BigDecimal saldo,
			Date hoy) {
		BigDecimal tasa = Double2BigDecimal(Double.parseDouble(ValueFromSystem(
				"TASA_INTERES_PUNITORIO", "2.8")));
		return calculaInteres(fecha, saldo, hoy, tasa);
	}

	/* calcula los intereses a una fecha */
	public static BigDecimal calculaInteres(Date fecha, BigDecimal saldo,
			Date hoy, BigDecimal tasa) {
		BigDecimal interes = BigDecimal.ZERO;
		BigDecimal pun = BigDecimal.ZERO;

		// tasa!!!
		int atraso = 0;
		Date fecha1 = fecha;

		if (!String.valueOf(fecha).equals("null")) {

			if (fecha1.compareTo(hoy) < 0
					&& saldo.compareTo(BigDecimal.ZERO) != 0) {
				while (fecha1.compareTo(hoy) < 0 && atraso <= 500) {
					pun = (saldo.multiply(tasa))
							.divide(Double2BigDecimal(100.00));
					saldo = saldo.add(pun);
					atraso++;
					interes = interes.add(pun);
					fecha1 = addMonths(fecha1, 1);
				}
			}

		}
		return redondear(interes, 2);
	}

	public static String FechaDDMMMAA(Date fecha) {
		System.out.println("fecha:" + String.valueOf(fecha));
		if (!String.valueOf(fecha).equals("null")) {
			SimpleDateFormat sqlfmt = new SimpleDateFormat("dd-MM-yy");
			return sqlfmt.format(fecha);
		} else
			return "    -  -  ";
	}
	
	public static String FechaDMA(Date fecha) {
		System.out.println("fecha:" + String.valueOf(fecha));
		if (!String.valueOf(fecha).equals("null")) {
			SimpleDateFormat sqlfmt = new SimpleDateFormat("dd-MM-yyyy");
			return sqlfmt.format(fecha);
		} else
			return "    -  -  ";
	}

	public static void BorraArchivo(String file) throws IOException {

		File f = new File(file);
		f.deleteOnExit();

	}

	public static String LeerArchivo(String file) throws IOException {
		// Leer el Codigo Fuente de este Archivo
		BufferedReader in = new BufferedReader(new FileReader(file));
		String s, s2 = new String();
		try {
			while ((s = in.readLine()) != null)
				s2 += s + "\n";
		} catch (IOException e) {
			e.printStackTrace();
		}
		in.close();
		return s2;
	}

	public static boolean CopyFile(String nombreFuente, String nombreDestino)
			throws IOException {
		boolean ok = false;

		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(nombreFuente);
			fos = new FileOutputStream(nombreDestino);
			FileChannel canalFuente = fis.getChannel();
			FileChannel canalDestino = fos.getChannel();
			canalFuente.transferTo(0, canalFuente.size(), canalDestino);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fis.close();
			fos.close();
		}

		File f = new File(nombreDestino);
		ok = f.exists();

		return ok;
	}

	public static boolean CopyText(String source, String target)
			throws IOException {
		boolean ok = false;
		// Leer el Codigo Fuente de este Archivo
		BufferedReader in = new BufferedReader(new FileReader(source));
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				target), "ISO-8859-1");

		String s, s2 = new String();
		try {
			while ((s = in.readLine()) != null)
				s2 += s + "\n";
			osw.write(s2);
			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			in.close();
			osw.close();
		}
		File f = new File(target);
		ok = f.exists();
		return ok;
	}
	
	public static boolean StringToFile(String source, String target)
			throws IOException {
		boolean ok = false;
		
		// Leer el Codigo Fuente de este Archivo
		String ss = source.toString();
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				target), "ISO-8859-1");

		Boolean write = false;
		try {
			if (!write) {
				osw.write(ss);
				write = true;
			} else
				osw.append(ss);

			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			osw.close();
		}
		
		File f = new File(target);
		ok = f.exists();
		
		return ok;
	}

	public static boolean CopyText(StringBuilder source, String target)
			throws IOException {
		boolean ok = false;
		// Leer el Codigo Fuente de este Archivo
		String ss = source.toString();
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				target), "ISO-8859-1");

		Boolean write = false;
		try {
			if (!write) {
				osw.write(ss);
				write = true;
			} else
				osw.append(ss);

			osw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			osw.close();
		}
		File f = new File(target);
		ok = f.exists();
		return ok;
	}

	public static String FechaAMD(Date fecha) {
		return FechaAMD(fecha, false);
	}

	public static String FechaAAMMDD(Date fecha) {
		return FechaAMD(fecha, false, "yy-MM-dd");
	}

	public static String FechaAAMMDD(Date fecha, boolean SinBarras) {
		return FechaAMD(fecha, false, (SinBarras ? "yyMMdd" : "yy-MM-dd"));
	}

	public static String FechaAMD(Date fecha, Boolean sinBarras) {
		return FechaAMD(fecha, sinBarras, (sinBarras ? "yyyyMMdd"
				: "yyyy-MM-dd"));
	}

	public static String FechaAMD(Date fecha, Boolean sinBarras, String fmt) {
		if (!String.valueOf(fecha).equals("null")) {
			SimpleDateFormat sqlfmt = new SimpleDateFormat((sinBarras ? fmt
					: fmt));
			return sqlfmt.format(fecha);
		} else
			return (sinBarras ? "00000000" : "0000-00-00");
	}

	public static Date FechaHOY() {
		// Date hoy = new Date(); 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        // System.out.println(sdf.format(now.getTime()));
        now.set(Calendar.HOUR_OF_DAY, 0);
        System.out.println(sdf.format(now.getTime()));
		
		
		return now.getTime();
	}

	public static String HoyDDMMMAA() {
		return FechaDDMMMAA(FechaHOY());
	}
	
	public static String HoyDMA() {
		return FechaDMA(FechaHOY());
	}

	public static String HoyAMD() {
		return FechaAMD(FechaHOY());
	}

	public static String HoyAMD(boolean sinbarras) {
		return FechaAMD(FechaHOY(), sinbarras);
	}

	public static String InsertarBarras(String f) {
		String r = "";

		r = f.substring(6) + "-" + f.substring(4, 6) + "-" + f.substring(0, 4);
		return r;
	}

	public static Date CtoD(String fecha, String fmt) {
		Date d = new Date();
		try {
			fecha = fecha.replace("/", "-");
			SimpleDateFormat sdf = new SimpleDateFormat(fmt);
			d = sdf.parse(fecha);
		} catch (Exception ex) {
			System.out.println("No se pudo convertir a fecha valida\n"
					+ ex.toString());
			d = null;
		}
		if(fecha==null || fecha.equals(""))
			d = null;
		return d;
	}

	public static Date CtoD(String fecha) {
		return CtoD(fecha, "dd-MM-yyyy");
	}

	public static BigDecimal getBigDecimal(ResultSet SQLResult, String data) {
		BigDecimal bd = BigDecimal.ZERO;
		try {
			bd = SQLResult.getBigDecimal(data);
		} catch (Exception ex) {
			System.out.println("Error al leer BigDecimal\n" + ex.toString());
		}
		return bd;
	}

	public static BigDecimal Double2BigDecimal(Double d) {
		BigDecimal r = BigDecimal.ZERO;
		if(d != null){
			try{
				double x = d.doubleValue();
				if(x != Double.NaN)
					r = BigDecimal.valueOf(x);
			}catch(Exception ex){
				System.out.println("Error al convertir double to BigDecimal number=" + d);
			}
		}
		return r;
	}

	public static double BigDecimal2Double(BigDecimal d) {
		double r = 0;
		if (d != null)
			r = (double) d.doubleValue();
		return r;
	}

	public static int BigDecimal2Int(BigDecimal d) {
		int r = 0;
		if (d != null) {
			String value = String.valueOf(BigDecimal2Double(d));
			if (value.indexOf(".") > -1)
				value = value.substring(0, value.indexOf("."));

			if (value.indexOf(",") > -1)
				value = value.substring(0, value.indexOf(","));

			r = Integer.valueOf(value);
		}
		return r;
	}

	public static Date addMonths(Date date1, int months) {
		Calendar calendar = toCalendar(date1);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	public static Date restMonths(Date date1, int months) {
		Calendar calendar = toCalendar(date1);
		calendar.add(Calendar.MONTH, months*(-1));
		return calendar.getTime();
	}
	
	public static Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static BigDecimal redondear(BigDecimal dato, int presicion) {
		
		BigDecimal result = Env.ZERO;
		if(dato != null){
			double d = BigDecimal2Double(dato);
			double r = Math.round(d * Math.pow(10, presicion))
			/ Math.pow(10, presicion);
			result =  Double2BigDecimal(Double.valueOf(r));
		}
		return result;
		
	}

	public static Double redondear(Double dato, int presicion) {
		Double result = 0.00;
		if(dato != null){
			double d = dato.doubleValue();
			double r = Math.round(d * Math.pow(10, presicion))
			/ Math.pow(10, presicion);
			result = Double.valueOf(r);
		}
		return result;
	}

	public static String Padleft(String n, int len, String pad) {
		String r = "";
		String dato = (n == null ? "" : n.trim());
		String tmp = "";

		if (dato.length() < len) {
			for (int i = dato.length(); i < len; i++) {
				tmp += pad;
			}
			r = tmp + dato;
		} else
			r = dato.substring(0, len);
		return r;
	}

	public static String PadRight(String n, int len, String pad) {
		String r = "";
		String dato = (n == null ? "" : n.trim());
		String tmp = "";

		if (dato.length() < len) {
			for (int i = dato.length(); i < len; i++) {
				tmp += pad;
			}
			r = dato + tmp;
		} else
			r = dato.substring(0, len);
		return r;
	}
	
	public static String PadCenter(String n, int len, String pad) {
		String r = "";
		String dato = (n == null ? "" : n.trim());
		String tmp = "";

		if (dato.length() < len) {
			
			int dif = len - dato.length();
			int padd = dif / 2;
			
			for (int i = 0; i < padd; i++) {
				tmp += pad;
			}
			
			r = tmp + dato + tmp;
		} else
			r = dato.substring(0, len);
		return r;
	}
	
	public static int fechasDiferenciaEnDiasSQL(Date fechaInicial, Date fechaFinal) 
	{
		String sql = "SELECT adempiere.daysbetween(?::timestamp with time zone,?::timestamp with time zone)";
		Timestamp f = new Timestamp(fechaFinal.getTime());
		Timestamp i = new Timestamp(fechaInicial.getTime());
		int dias = DB.getSQLValueEx(null, sql, new Object[]{f, i});
		
		System.out.println("fechasDiferenciaEnDias. Fecha Inicial=" + fechaInicial + " fecha final=" + fechaFinal + " sql final=" + sql);
		System.out.println("Diferencia en dias decimal= " + dias);
		return dias;
		
	}
	
	public static int difFechasSQL(Date d, Date h) {
		int dias = 0;

		dias = fechasDiferenciaEnDiasSQL(d, h);
		return dias;
	}

	public static int fechasDiferenciaEnDias(Date fechaInicial, Date fechaFinal) 
	{
		long fechaInicialMs = fechaInicial.getTime();
		long fechaFinalMs = fechaFinal.getTime();
		long diferencia = fechaFinalMs - fechaInicialMs;
		double dias = diferencia / (1000 * 60 * 60 * 24);
		
		System.out.println("fechasDiferenciaEnDias. Fecha Inicial=" + fechaInicial + " fecha final=" + fechaFinal + " Inicial ms=" + fechaInicialMs + " Final ms=" + fechaFinalMs);
		System.out.println("Diferencia en dias decimal= " + (dias + 1));
		return ((int) dias) + 1;
		
	}
	
	public static int fechasDiferenciaEnDias_v2(Date fechaInicial, Date fechaFinal) 
	{
		long fechaInicialMs = fechaInicial.getTime();
		long fechaFinalMs = fechaFinal.getTime();
		long diferencia = fechaFinalMs - fechaInicialMs;
		double dias = Math.floor(diferencia / (1000 * 60 * 60 * 24));
		return ((int) dias);
	}

	public static int difFechas(Date d, Date h) {
		int dias = 0;

		dias = fechasDiferenciaEnDias(d, h);
		return dias;
	}

	public static String ValueFromSystem(String key, String defa) {

		String s = ""; // jdbc:extendedsystems:advantage://127.0.0.1:6262;user=;password=;Catalog=C:\\DISCO_D\\dreher\\american;TableType=ntx;LockType=compatible;ShowDeleted=false;";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT value FROM AD_SysConfig WHERE name='" + key + "'  AND AD_Org_ID=0";
		try {
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			if (rs.next())
				s = rs.getString("value");
			else {
				// System.out.println("Falta definir la data de sistema " + key
				// + "\r\n" + "Valor por defecto:" + defa); // debug
				if (defa.equals(null))
					MsgAlert("No se encontro la entrada " + key
							+ " en las datas del sistema!");

				s = defa;
			}
			rs.close();
		} catch (Exception ex) {
			try {
				pstmt.close();
			} catch (Exception ex0) {
				System.out.print("Could not close prepared statement");
			}
		}finally{
			DB.close(rs, pstmt);
			rs=null; pstmt=null;
		}

		return s;
	}
	
	public static String ValueFromSystem(String key, String defa, boolean isAppend) {
		return ValueFromSystem(key, defa, isAppend, "Variable seteada por defecto!");
	}
	
	public static String ValueFromSystem(String key, String defa, boolean isAppend, String descrip) {
		
		String s = ""; // jdbc:extendedsystems:advantage://127.0.0.1:6262;user=;password=;Catalog=C:\\DISCO_D\\dreher\\american;TableType=ntx;LockType=compatible;ShowDeleted=false;";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT value FROM AD_SysConfig WHERE name='" + key + "' AND AD_Org_ID=0";
		try {
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			if (rs.next())
				s = rs.getString("value");
			else {
				// System.out.println("Falta definir la data de sistema " + key
				// + "\r\n" + "Valor por defecto:" + defa); // debug
				if (defa.equals(null))
					MsgAlert("No se encontro la entrada " + key
							+ " en las datas del sistema!");
				else{
					s = defa;
					if(isAppend){
						
						MSysConfig ads = new MSysConfig(Env.getCtx(), 0, null);
						ads.setAD_Org_ID(0);
						ads.setConfigurationLevel("C");
						ads.setDescription(descrip);
						ads.setName(key);
						ads.setValue(defa);
						ads.save(null);

					}
				}
			}
			
		} catch (Exception ex) {
			System.out.println("Excepcion al buscar dato del AD_SysConfig! " + ex.toString());
		}finally{
			DB.close(rs, pstmt);
			rs=null;pstmt=null;
		}

		return s;

	}

	public static void MsgAlert(String string) {
		JOptionPane.showMessageDialog(null, string);
		System.out.println(string);
	}

	public static boolean AD_confirm(String msg) {
		boolean ok = false;

		ok = ADialog.ask(0, null, msg);

		return ok;
	}

	public static boolean MsgYesNo(String string) {
		int iok = JOptionPane.showConfirmDialog(null, string,
				"Confirme por favor", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		boolean ok = false;
		if (iok == JOptionPane.YES_OPTION || iok == JOptionPane.OK_OPTION)
			ok = true;
		return ok;
	}

	public static String ValueFromSystem(String key) {
		return ValueFromSystem(key, null);
	}

	public static String DBFNTXServer(String trx_name) {
		String c = ValueFromSystem("STRING_CONECTION_DBFNTX_SERVER").trim();
		if (c == null || c == "")
			c = "jdbc:extendedsystems:advantage://127.0.0.1:6262;user=;password=;Catalog=C:\\DISCO_D\\dreher\\dbf;TableType=ntx;LockType=compatible;ShowDeleted=false;";
		return c;
	}

	public static BigDecimal TasaIVA(String trx_name) {
		String v = ValueFromSystem("TASA_IVA").trim();
		if (v.equals(""))
			v = "0.00";
		Double d = redondear(Double.valueOf(v), 2);
		BigDecimal t = BigDecimal.valueOf(d);
		return t;
	}

	public static String myIntegerString(double d) {
		String si = "";
		String s = String.valueOf(d).trim();
		int pos = s.indexOf(".");
		int nDecimals = 0;

		if (pos > -1) {
			nDecimals = s.substring(pos).length();
			si = s.substring(0, pos);
		} else
			si = s;

		return si;
	}

	public static String myDecimalString(double d) {
		String si = "";
		String s = String.valueOf(d).trim();
		int pos = s.indexOf(".");

		if (pos > -1)
			si = s.substring(pos + 1);

		return si;

	}
	
	public static String Time(){
		Date now = new Date();
        String hora = String.valueOf(now.getHours())+":"+String.valueOf(now.getMinutes())+":"+String.valueOf(now.getSeconds());
        return hora;
	}
	
	public static String User(){
		String user = Env.getContext(Env.getCtx(), "#AD_User_Name");
		return user;
	}

	// fecha = AAAA-MM-DD

	/*
	 * Codigo de barras de pago facil, composicion
	 * 
	 * 01-04 Codigo de la empresa 05-10 Importe de la cuota (parte entera) 11-12
	 * Importe de la cuota (parte decimal) 13-14 Año de vencimiento 15-17
	 * Cantidad de dias de plazo desde el 1° de enero inclusive 18-27 Codigo
	 * identificador del cliente 28-31 Numero de cuota a abonar 32-32 Moneda
	 * (0=pesos) fijo! 33-36 Importe de recargo segundo vencimiento (parte
	 * entera) 37-38 Importe de recargo segundo vencimiento (parte decimal)
	 * 39-40 Cantidad de dias de plazo para el segundo vencimiento (diferencia
	 * con la primer fecha de vto.) 41-41 Primer digito verificador segun
	 * calculo 42-42 Segundo digito verificador segun calculo
	 * 
	 */

	public static String CodigoBarras(String empresa, double importe,
			String vencimiento, String cliente, String cuota, String moneda,
			double recargo, int dias, String cuenta, String tipo)
			throws java.text.ParseException {

		String original = "";
		String entero = "0";
		String dec = "0";
		String vence1 = "";
		String Identificador = "";
		String CodigoCuenta = "0";
		int diasvence = 0;
		Date inicio;
		Date fin;

		CodigoCuenta = cuenta;

		if (empresa != null && empresa != "")
			IdentEmpresa = empresa;

		original += Padleft(IdentEmpresa, 4, "0");
		entero = myIntegerString(redondear(importe, 2));
		dec = myDecimalString(importe);
		original += Padleft(String.valueOf(entero), 6, "0")
				+ PadRight(dec, 2, "0");

		inicio = CtoD(vencimiento.substring(0, 4) + "-01-01", "yyyy-MM-dd");
		fin = CtoD(vencimiento, "yyyy-MM-dd");
		diasvence = fechasDiferenciaEnDias(inicio, fin) + 1;

		vence1 = vencimiento.substring(2, 4)
				+ Padleft(String.valueOf(diasvence), 3, "0");

		original += Padleft(vence1, 5, "0");

		System.out.println("...." + CodigoCuenta + Padleft(cliente, 9, "0"));

		// Fuerzo codigo de cuenta en cero
		CodigoCuenta = "0";

		// Ver si aca incluyo DNI + CodCli o DNI(8) + CODCLI(6)

		Identificador = CodigoCuenta + Padleft(cliente, 9, "0")
				+ Padleft(cuota, 4, "0");
		original += Padleft(Identificador, 14, "0");

		if (moneda != null && moneda != "")
			original += Moneda;

		entero = myIntegerString(recargo);
		dec = myDecimalString(recargo);

		original += Padleft(entero, 4, "0") + PadRight(dec, 2, "0");
		original += Padleft(String.valueOf(dias), 2, "0");

		/* Desde aqui se calcula el primer digito verificador */

		int[] cadena = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0 };
		int[] primerResultado = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int PrimerSuma = 0;
		double MitadPrimerSuma = 0;
		int MitadPrimerSumaEntero = 0;
		int PrimerVerificador = 0;
		int SegundoVerificador = 0;

		// System.out.println(original); // debug
		for (int i = 0; i < 40; i++) {
			// System.out.println(original.substring(i,i+1)); // debug
			cadena[i] = Integer.valueOf(original.substring(i, i + 1));
		}

		for (int i = 0; i < 40; i++) {
			primerResultado[i] = cadena[i] * PrimerString[i];
			PrimerSuma += primerResultado[i];
		}

		MitadPrimerSuma = PrimerSuma / 2;
		MitadPrimerSumaEntero = Integer
				.valueOf(myIntegerString(MitadPrimerSuma));
		PrimerVerificador = MitadPrimerSumaEntero % 10;

		/* Desde aqui se calcula el segundo digito verificador */

		original += String.valueOf(PrimerVerificador);

		// ss += "Primer Suma:" + PrimerSuma.ToString() + " primerMitadSuma:" +
		// MitadPrimerSuma.ToString() + " entero:" +
		// MitadPrimerSumaEntero.ToString();

		for (int i = 0; i < 41; i++) {
			cadena[i] = Integer.valueOf(original.substring(i, i + 1));
		}

		PrimerSuma = 0;
		for (int i = 0; i < 41; i++) {
			primerResultado[i] = cadena[i] * PrimerString[i];
			PrimerSuma += primerResultado[i];
		}

		MitadPrimerSuma = PrimerSuma / 2;
		MitadPrimerSumaEntero = Integer
				.valueOf(myIntegerString(MitadPrimerSuma));
		SegundoVerificador = MitadPrimerSumaEntero % 10;

		return original + String.valueOf(SegundoVerificador);

	}

	// OJO, ver como se toman los datos del sender...
	public static Boolean EmailSend(String to, String subject, String msg) {
		return EmailSend(to, subject, msg, null, null);
	}

	// OJO, ver como se toman los datos del sender...
	public static Boolean EmailSend(String to, String subject, String msg,
			String cc) {
		return EmailSend(to, subject, msg, null, cc);
	}

	// OJO, ver como se toman los datos del sender...
	public static Boolean EmailSend(String to, String subject, String msg,
			File fileName, String cc) {

		MClient m_client = MClient.get(Env.getCtx());
		MUser from = MUser.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()));
		
		EMail email = m_client.createEMail(from, to, subject, msg);
		
		// Original, Modif el 07/03/2010 
		// EMail email= m_client.createEMail(to, subject, msg);
		
		Properties props = new Properties();

		boolean smtpAuth = Boolean.getBoolean(props.getProperty("mail.smtp.auth"));
		String smtp_host = props.getProperty("mail.host");
		
		if(smtpAuth){

			String mailUser = from.getEMailUser();
			String mailPassword = from.getEMailUserPW();
		
			System.out.println(smtp_host + " " + mailUser + " " + mailPassword);
			
			email.createAuthenticator (mailUser, mailPassword);
		}
		
		email.addCc(cc);
		email.addAttachment(fileName);
		// email.setFrom(from.getEMail());
		email.setSmtpHost(smtp_host);
		email.send();

		Boolean ok = email.isSentOK();

		return ok;

	}
	
	
	// OJO, ver como se toman los datos del sender...
	public static Boolean EmailSendWithAttachs(String to, String subject, String msg,
			File[] fileNames, String cc) {

		MClient m_client = MClient.get(Env.getCtx());
		MUser from = MUser.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()));
		
		EMail email = m_client.createEMail(from, to, subject, msg);
		
		// Original, Modif el 07/03/2010 
		// EMail email= m_client.createEMail(to, subject, msg);
		
		Properties props = new Properties();

		boolean smtpAuth = Boolean.getBoolean(props.getProperty("mail.smtp.auth"));
		String smtp_host = props.getProperty("mail.host");
		
		if(smtpAuth){

			String mailUser = from.getEMailUser();
			String mailPassword = from.getEMailUserPW();
		
			System.out.println(smtp_host + " " + mailUser + " " + mailPassword);
			
			email.createAuthenticator (mailUser, mailPassword);
		}
		
		email.addCc(cc);
		
		for(int i=0; i<fileNames.length; i++)
			email.addAttachment(fileNames[i]);
		
		email.setMessageHTML(msg);
		
		// email.setFrom(from.getEMail());
		email.setSmtpHost(smtp_host);
		email.send();

		Boolean ok = email.isSentOK();

		return ok;

	}

	// OJO, ver como se toman los datos del sender...
	public static Boolean EmailSendWithAttachs(String to, String subject, String msg,
			ArrayList<File> fileNames, String cc) {

		MClient m_client = MClient.get(Env.getCtx());
		MUser from = MUser.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()));
		
		EMail email = m_client.createEMail(from, to, subject, msg);
		
		// Original, Modif el 07/03/2010 
		// EMail email= m_client.createEMail(to, subject, msg);
		
		Properties props = new Properties();

		boolean smtpAuth = Boolean.getBoolean(props.getProperty("mail.smtp.auth"));
		String smtp_host = props.getProperty("mail.host");
		
		if(smtpAuth){

			String mailUser = from.getEMailUser();
			String mailPassword = from.getEMailUserPW();
		
			System.out.println(smtp_host + " " + mailUser + " " + mailPassword);
			
			email.createAuthenticator (mailUser, mailPassword);
			
		}
		
		email.setMessageHTML(msg);
		email.addCc(cc);
		
		for(int i=0; i<fileNames.size(); i++)
			email.addAttachment(fileNames.get(i));
		
		// email.setFrom(from.getEMail());
		email.setSmtpHost(smtp_host);
		email.send();

		Boolean ok = email.isSentOK();

		return ok;

	}
	
	// OJO, ver como se toman los datos del sender...
	public static Boolean EmailMassiveSendWithAttachs(String to, String subject, String msg,
			ArrayList<File> fileNames, String cc) {

		String port = Miscfunc.ValueFromSystem("PortSMTPEmailSendMassive", "25", true);
		
		MClient m_client = MClient.get(Env.getCtx());
		MUser from = MUser.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()));
		
		EMail email = m_client.createEMail(from, to, subject, msg);
		
		// Original, Modif el 07/03/2010 
		// EMail email= m_client.createEMail(to, subject, msg);
		
		Properties props = new Properties();

		boolean smtpAuth = Boolean.getBoolean(props.getProperty("mail.smtp.auth"));
		String smtp_host = props.getProperty("mail.host");
		
		if(smtp_host == null){
			MClient mc = new MClient(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), null);
			smtp_host = mc.getSMTPHost();
		}
		 // smtp_host += ":" + port;
		
		if(smtpAuth){

			String mailUser = from.getEMailUser();
			String mailPassword = from.getEMailUserPW();
		
			System.out.println(smtp_host + " " + mailUser + " " + mailPassword);
			
			email.createAuthenticator (mailUser, mailPassword);
			
		}
		
		// email.setM_smtpPort(new Integer(port).intValue());
		email.setMessageHTML(msg);
		email.addCc(cc);
		
		for(int i=0; i<fileNames.size(); i++)
			email.addAttachment(fileNames.get(i));
		
		// email.setFrom(from.getEMail());
		email.setSmtpHost(smtp_host);
		email.send();

		Boolean ok = email.isSentOK();

		return ok;

	}

	public static String runTask(String cmd) throws Exception {

		StringBuffer sb = new StringBuffer();

		try {

			Task m_task = new Task(cmd);
			m_task.start();

			while (true) {
				// Give it a bit of time
				try {
					Thread.sleep(500);
				} catch (InterruptedException ioe) {
					System.out.println("Error en run task:" + cmd + " err:"
							+ ioe.toString());
				}

				System.out.println("/////////////>"
						+ m_task.getOut().toString().trim() + "<");

				// Info to user
				if ((m_task.getOut() != null
						&& m_task.getOut().toString().trim() != "" && m_task
						.getOut().toString().trim() != ".")
						|| (m_task.getErr() != null
								&& m_task.getErr().toString().trim() != "" && m_task
								.getErr().toString().trim() != ".")) {
					sb.append(m_task.getOut()).append(m_task.getErr());
				}

				// Are we done?
				if (!m_task.isAlive())
					break;
			}
			System.out.println("done run task");

		} catch (Exception ex) {
			ADialog.info(0, null, "Se produjo un error al ejecutar : " + cmd
					+ "\n" + ex.toString());
			ex.printStackTrace();
		}

		return sb.toString();
	}

	// TODO trucho, pero por ahora discrimino las terminadas en A ... pobrisimo!
	public static String Sexo(String nameComplete) {
		String sexo = "M";
		try {
			String name = nameComplete.split(" ")[1].trim();
			if (name.endsWith("A"))
				sexo = "F";

		} catch (Exception ex) {
			System.out.println("Error al buscar genero de la persona");
		}
		return sexo;
	}

	public static String Mes(int mes) {
		String[] meses = new String[] { "Ene", "Feb", "Mar", "Abr", "May",
				"Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
		String r = "";

		if (mes >= 1 && mes <= 12)
			r = meses[mes - 1];

		return r;
	}

	public static void OpenFileDesktop(String nameFile) {
	     Desktop desktop;/* Declaro un objeto Desktop que es una nueva API en JAVA
	     Para mas detalle sobre ésta API ver la siguiente Página web > 
	     http://java.sun.com/developer/technicalArticles/J2SE/Desktop/javase6/desktop_api/
	        */
	     File file = new File(nameFile);//declaro un Objeto File que apunte a mi archivo html
	        if (Desktop.isDesktopSupported()){// si éste Host soporta esta API 
	           desktop = Desktop.getDesktop();//objtengo una instancia del Desktop(Escritorio)de mi host 
	            try {
	                desktop.open(file);//abro el archivo con el programa predeterminado
	                }
	            catch (IOException ex) {
	                System.out.println("Error al abrir archivo=" + nameFile + "\nError: " +  ex);
	                }
	        }
	       else{ System.err.println("Lo lamento,no se puede abrir el archivo; ésta Maquina no soporta la API Desktop");
	       }
	    }  
	
	public static void ViewFile(String fileName) {
		ViewFile(fileName, false);
	}

	public static void ViewFile(String fileName, boolean preguntar) {

		boolean ok = false;

		if (preguntar)
			ok = AD_confirm("Abrir archivo?");
		else
			ok = true;

		if (ok) {

			try {
				File tempFile = new File(fileName);
				System.out.println("Mostrar el archivo= " +tempFile.getAbsolutePath());
				if (Env.isWindows()) {
					System.out.println("Mostrar desde Windows");
					Runtime.getRuntime().exec(
							"rundll32 SHELL32.DLL,ShellExec_RunDLL \""
									+ tempFile + "\"");
				} else if (Env.isMac()) {
					System.out.println("Mostrar desde iOS");
					String[] cmdArray = new String[] { "open",
							tempFile.getAbsolutePath() };
					Runtime.getRuntime().exec(cmdArray);
				} else // other OS
				{
					System.out.println("Mostrar desde Linux");
					String[] cmdArray = new String[]{tempFile.getAbsolutePath()};
					Runtime.getRuntime().exec("run-mailcap",
							cmdArray);
				}
			} catch (Exception e) {
				System.out.print(e.toString());
			}
		}
	}

	public static String ValidaCuit(String cuit) {
		String msg = null;
		String auxi = cuit.replace("-", "").trim();
		int[] tab_cuit = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int acumulo = 0;
		int digitos = 0;

		// Debug System.out.println(auxi + ":" + cuit);

		if (auxi.length() >= 10) {
			try {
				digitos = Integer.parseInt(auxi.substring(0, 2));

				cuit = cuit.replace("-", "").trim();

				if (Integer.parseInt(auxi.substring(2, 9)) == 0)
					msg = "Cuit imcompleto!";
				else if ((digitos == 20) || (digitos == 23) || (digitos == 24)
						|| (digitos == 27) || (digitos == 30)
						|| (digitos == 33) || (digitos == 34)) {
					int p = 10;
					while (p >= 0) {
						tab_cuit[10 - p] = Integer.parseInt(auxi.substring(p,
								p + 1));
						p--;
					}
					for (int i = 0; i < 7; i++)
						acumulo += (tab_cuit[i] * (i + 1));

					for (int i = 7; i < 11; i++)
						acumulo += (tab_cuit[i] * ((i + 1) - 6));

					if (acumulo % 11 != 0)
						msg = "Cuit no v�lido!";
				} else
					msg = "El Cuit comienza con un valor inv�lido!"
							+ "<br>Valor:" + String.valueOf(digitos);
			} catch (Exception e) {
				System.out.println("Se produjo un error al validar Cuit!");
				msg = "Se produjo un error al validar el Cuit";
			}
		} else
			msg = "Cuit Incompleto! <br>Longitud:"
					+ String.valueOf(auxi.length());
		return msg;
	}

	public static ArrayList<BigDecimal[]> sistema_frances(
			BigDecimal monto_a_prestar, int cantidad_cuotas,
			BigDecimal tasa_nominal_anual, BigDecimal tasa_iva,
			BigDecimal tasa_seguro, BigDecimal valor_gasto) {

		ArrayList<BigDecimal[]> tabla = new ArrayList<BigDecimal[]>();
		
		BigDecimal TEM = Env.ZERO;
		BigDecimal saldo_capital = Env.ZERO;
		BigDecimal amortizacion = Env.ZERO;
		BigDecimal interes = Env.ZERO;
		BigDecimal seguro = Env.ZERO;
		BigDecimal iva = Env.ZERO;
		BigDecimal ivaSeguro = Env.ZERO;
		BigDecimal cuota = Env.ZERO;
		BigDecimal total_cuota = Env.ZERO;
		BigDecimal total_cuotas = Env.ZERO;
		BigDecimal gasto_administrativo = valor_gasto;
		BigDecimal cuota_promedio = Env.ZERO;
		BigDecimal iva_gastoadministrativo = Env.ZERO;

		BigDecimal d_monto_a_prestar = monto_a_prestar;
		BigDecimal d_tasa_nominal_anual = tasa_nominal_anual;
		BigDecimal d_tasa_seguro = tasa_seguro;
		BigDecimal d_tasa_iva = tasa_iva;
		
		// dREHER - El gasto tambien un porcentaje, NO FIJO 3/12/2014 Vanesa y Marcelo
		//		5/12/2014 Vanesa Marino me confirma que el gasto es un monto fijo y no un porcentual
		BigDecimal d_tasa_gasto = valor_gasto;
		
		try{

			// dREHER, ya viene dividida por 100
			// TEM = d_tasa_nominal_anual.divide(new BigDecimal(100), RoundingMode.DOWN);
			
			TEM = d_tasa_nominal_anual; // ya esta llegando la tasa mensual, por lo tanto no divido por 12

			// TEM = TEM.divide(new BigDecimal(12), RoundingMode.DOWN);

			saldo_capital = d_monto_a_prestar;

			System.out.println("Calcula valor de una cuota.... tasa nominal anual=" + d_tasa_nominal_anual + "\n TEM=" + TEM + "\n Tasa param=" +
					d_tasa_nominal_anual.divide(new BigDecimal(12), RoundingMode.DOWN));

			cuota = new BigDecimal(calculaCuotaMetodoFrances(d_monto_a_prestar,
					d_tasa_nominal_anual, cantidad_cuotas));

			System.out.println("Crea plan de cuotas segun sistema Frances.... valor cuota=" + cuota);

			for (int i = 0; i < cantidad_cuotas; i++) {

				saldo_capital = saldo_capital.subtract(amortizacion);

				interes = saldo_capital.multiply(TEM);

				amortizacion = cuota.subtract(interes);
				seguro = (saldo_capital.multiply(d_tasa_seguro.divide(new BigDecimal(100), RoundingMode.DOWN)));
				iva = (interes).multiply(d_tasa_iva);

				
				// 5/12/2014 me confirman que es un monto fijo - gasto_administrativo = (saldo_capital.multiply(d_tasa_gasto.divide(new BigDecimal(100), RoundingMode.DOWN)));
				gasto_administrativo = valor_gasto;
				
				// divisor 1.21 --> 1.tasa Descompongo en interes en su parte Neto e IVA
				
				BigDecimal divisor = new BigDecimal(1).add(d_tasa_iva);
				
				BigDecimal neto = interes.divide(divisor, 2, RoundingMode.HALF_DOWN);
				
				BigDecimal interesIVA = interes.subtract(neto);
				
				System.out.println("1 - Calculo Neto e IVA (interes) neto=" + neto + " iva=" + interesIVA + " Interes Orig.:" + interes + " Suma=" + String.valueOf(neto.add(interesIVA)));
								
				//------------------------
				
				// 3/12/2014 Vanesa/Marcelo el Seguro no lleva IVA
				//ivaSeguro = (seguro).multiply(d_tasa_iva.divide(new BigDecimal(100), RoundingMode.DOWN));
				ivaSeguro = new BigDecimal(0);

				iva_gastoadministrativo = gasto_administrativo.multiply(d_tasa_iva);
				total_cuota = cuota.add(neto).add(interesIVA).add(seguro).add(gasto_administrativo).add(iva_gastoadministrativo).add(ivaSeguro);

				total_cuotas = total_cuotas.add(total_cuota);

				System.out.println("Agrega cuota calculada a tabla.... " + i + "\n saldo capital=" + saldo_capital + "\n interes=" + interes +
						"\n cuota=" + cuota + "\n iva=" + interesIVA + "\n gastos=" + gasto_administrativo + "\niva gasto=" + iva_gastoadministrativo + "\n seguro=" + seguro + "\ndtasa_iva=" + d_tasa_iva);

				tabla.add(new BigDecimal[] {
						saldo_capital,  // 0
						neto,        // 1
						amortizacion,   // 2
						cuota,          // 3
						seguro,         // 4
						interesIVA,            // 5
						total_cuota,    // 6
						gasto_administrativo,  // 7
						iva_gastoadministrativo, // 8
						ivaSeguro, // 9
						saldo_capital, // 10
						BigDecimal.ZERO }); // 11 total cuota promedio

				System.out.println("Agrego cuota calculada a tabla.... " + i);
			}

			cuota_promedio = total_cuotas.divide( new BigDecimal(cantidad_cuotas), RoundingMode.DOWN);

			System.out.println("Setea cuota promedio....=" + cuota_promedio);
			
			cuota_promedio = cuota_promedio.divide(new BigDecimal(1), 2, RoundingMode.DOWN);
			
			System.out.println("Setea cuota promedio, redondeo a 2 ....=" + cuota_promedio);

			for (int i = 0; i < tabla.size(); i++) {
				BigDecimal[] data = tabla.get(i);
				data[11] = cuota_promedio;
				tabla.set(i, data);
			}

		}catch(Exception ex){
			System.out.println("Ocurrio una excepcion al calcular cuotas x sistema frances..." + ex.toString());
		}

		return tabla;

	}
	
	public static ArrayList<BigDecimal[]> metodo_frances(
			BigDecimal monto_a_prestar, int cantidad_cuotas,
			BigDecimal tasa_nominal_anual, BigDecimal tasa_iva,
			BigDecimal tasa_seguro, BigDecimal valor_gasto) {

		ArrayList<BigDecimal[]> tabla = new ArrayList<BigDecimal[]>();
		
		Double TEM = 0.00;
		Double saldo_capital = 0.00;
		Double amortizacion = 0.00;
		Double interes = 0.00;
		Double seguro = 0.00;
		Double iva = 0.00;
		Double ivaSeguro = 0.00;
		Double cuota = 0.00;
		Double total_cuota = 0.00;
		Double total_cuotas = 0.00;
		Double gasto_administrativo = BigDecimal2Double(valor_gasto);
		Double cuota_promedio = 0.00;
		Double iva_gastoadministrativo = 0.00;

		Double d_monto_a_prestar = BigDecimal2Double(monto_a_prestar);
		Double d_tasa_nominal_anual = BigDecimal2Double(tasa_nominal_anual);
		Double d_tasa_seguro = BigDecimal2Double(tasa_seguro);
		Double d_tasa_iva = BigDecimal2Double(tasa_iva);

		
		try{

			TEM = d_tasa_nominal_anual / 100;

			TEM = TEM / 12;

			saldo_capital = d_monto_a_prestar;

			System.out.println("Calcula valor de una cuota....");

			cuota = calculaCuotaMetodoFrances(Double2BigDecimal(d_monto_a_prestar),
					Double2BigDecimal(d_tasa_nominal_anual / 12), cantidad_cuotas);

			System.out.println("Crea plan de cuotas segun sistema Frances....");

			for (int i = 0; i < cantidad_cuotas; i++) {

				saldo_capital -= amortizacion;

				interes = saldo_capital * TEM;

				amortizacion = cuota - interes;
				seguro = saldo_capital * d_tasa_seguro / 100;
				iva = (interes) * d_tasa_iva / 100;

				
				// divisor 1.21 --> 1.tasa
				
				double divisor = 1 + (d_tasa_iva / 100);
				
				double neto = interes / divisor;
				
				double interesIVA = interes - neto;
				
				double interesNeto = neto;
								
				//------------------------
				
				System.out.println("2 - Calculo Neto e IVA (interes) neto=" + neto + " iva=" + interesIVA);
				
				// 3/12/2014 Vanesa/Marcelo el seguro no lleva iva
				// ivaSeguro = (seguro) * d_tasa_iva / 100;
				ivaSeguro = 0.00;

				iva_gastoadministrativo = gasto_administrativo * d_tasa_iva / 100;
				total_cuota = cuota + interesIVA + seguro + gasto_administrativo
				+ iva_gastoadministrativo + ivaSeguro;

				total_cuotas += total_cuota;

				System.out.println("Agrega cuota calculada a tabla.... " + i);

				tabla.add(new BigDecimal[] {
						redondear(Double2BigDecimal(saldo_capital), 2),  // 0
						redondear(Double2BigDecimal(interesNeto), 2),        // 1
						redondear(Double2BigDecimal(amortizacion), 2),   // 2
						redondear(Double2BigDecimal(cuota), 2),          // 3
						redondear(Double2BigDecimal(seguro), 2),         // 4
						redondear(Double2BigDecimal(interesIVA), 2),            // 5
						redondear(Double2BigDecimal(total_cuota), 2),    // 6
						redondear(Double2BigDecimal(gasto_administrativo), 2),  // 7
						redondear(Double2BigDecimal(iva_gastoadministrativo), 2), // 8
						redondear(Double2BigDecimal(ivaSeguro), 2), // 9
						redondear(Double2BigDecimal(saldo_capital), 2), // 10
						BigDecimal.ZERO }); // 11 total cuota promedio

				System.out.println("Agrego cuota calculada a tabla.... " + i);
			}

			cuota_promedio = total_cuotas / cantidad_cuotas;

			System.out.println("Setea cuota promedio....");

			for (int i = 0; i < tabla.size(); i++) {
				BigDecimal[] data = tabla.get(i);
				data[11] = redondear(Double2BigDecimal(cuota_promedio), 2);
				tabla.set(i, data);
			}

		}catch(Exception ex){
			System.out.println("Ocurrio una excepcion al calcular cuotas x sistema frances..." + ex.toString());
		}

		return tabla;

	}
	
	/*
	 * dREHER
	 * 	Vn=Valor actual($89.342,45)
		i= tasa subperiódica(0,64%mensual)
		n=cantidad de subperíodos (240)
		c= cuota (?)

		c= Vn*(i*(1+i)^n)/(((1+i)^n)-1)

		Cuota = Capital * (tasa*(1+tasa)^cuotas) / (((1+tasa)^cuotas)-1)
	 * 
	 * 
	 * 
	 * 
	 */
	private static Double calculaCuotaMetodoFrances(BigDecimal monto,
			BigDecimal tasa_mensual, int cantidad_meses) {

		Double d_monto = monto.doubleValue();
		Double d_tasa_mensual = tasa_mensual.doubleValue();
		Double dC = 0.00;
		
		System.out.println("Monto a prestar=" + d_monto);
		System.out.println("Tasa mensual=" + d_tasa_mensual);

		Double primerTermino = 0.00;
		Double segundoTermino = 0.00;
		
		primerTermino = d_monto * ( d_tasa_mensual * Math.pow( (1 + d_tasa_mensual), cantidad_meses ) );
		segundoTermino = (Math.pow( (1+d_tasa_mensual), cantidad_meses)) - 1;
		
		dC = primerTermino / segundoTermino;
		
		/*
		dC = (d_monto * d_tasa_mensual / 1 - (1 + Math.pow(d_tasa_mensual / 12,
				-cantidad_meses))) / 12;
		
		System.out.println("Cuota=" + dC);

		Double i = d_tasa_mensual * 12 / 100;
		dC = (d_monto * i / 12)
				/ (1 - Math.pow((1 + (i / 12)), -cantidad_meses));
		*/
		System.out.println("Cuota 2=" + dC);

		return dC;
	}

	public static void SetBusy(JFrame frame, boolean busy) {

		if (frame == null) // during init
			return;
		if (frame instanceof AWindow)
			((AWindow) frame).setBusy(busy);
		if (busy) {
			((AWindow) frame).setBusyMessage("Procesando...");
			((AWindow) frame).setBusyTimer(2);
		}

		if (busy) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			frame.setEnabled(false);

		} else {
			frame.setCursor(Cursor.getDefaultCursor());
			frame.setEnabled(true);
		}
	}

	private static String fixString(String s) {
		if (s == null || s.length() == 0)
			return "";
		String s2 = s.replaceAll("[\t\n\f\r]+", " ");
		return s2;
	}

	/**
	 * Print data grid, dREHER
	 * 
	 */
	public static void printGrid(JTable jt, String title) {
		if (jt != null)
			try {

				Printable printable = jt.getPrintable(
						JTable.PrintMode.FIT_WIDTH, new MessageFormat(title),
						new MessageFormat("Pag. {0}"));

				PrinterJob job = PrinterJob.getPrinterJob();
				job.setPrintable(printable);
				PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
				boolean printAccepted = job.printDialog(attr);

				if (printAccepted) {
					try {
						job.print(attr);
					} catch (PrinterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} finally {
				;
			}
	}

	private static Double uRetDouble(Object o) {
		Double r = 0.00;

		if (o instanceof BigDecimal)
			r = RetDouble((BigDecimal) o);
		else if (o instanceof Double)
			r = (Double) o;
		else if (o instanceof Integer)
			r = RetDouble((Integer) o);

		if (r == null)
			r = 0.00;

		return r;
	}

	public static Double RetDouble(BigDecimal x) {
		return BigDecimal2Double(x);
	}

	public static Double RetDouble(Integer x) {
		return Double.valueOf(String.valueOf(x));
	}

	public static int CalcularEdad(Date nacio) throws java.text.ParseException {
		Calendar today = Calendar.getInstance();

		Calendar fechaNac = Calendar.getInstance();
		fechaNac.setTime(nacio);

		System.out.println("fecha nac: " + String.valueOf(nacio));

		int diff_year = today.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
		int diff_month = today.get(Calendar.MONTH)
				- fechaNac.get(Calendar.MONTH);
		int diff_day = today.get(Calendar.DAY_OF_MONTH)
				- fechaNac.get(Calendar.DAY_OF_MONTH);

		System.out.println("*** diffyear:"
				+ String.valueOf(fechaNac.get(Calendar.YEAR)) + "-"
				+ String.valueOf(diff_year));
		System.out.println("*** diffmonth:"
				+ String.valueOf(fechaNac.get(Calendar.MONTH)) + "-"
				+ String.valueOf(diff_month));
		System.out.println("*** diffday:"
				+ String.valueOf(fechaNac.get(Calendar.DAY_OF_MONTH)) + "-"
				+ String.valueOf(diff_day));

		// Si est� en ese a�o pero todav�a no los ha cumplido
		if (diff_month < 0 || (diff_month == 0 && diff_day < 0)) {
			diff_year -= 1; // no aparec�an los dos guiones del postincremento
			// :|
		}
		return diff_year;
	}

	public static BigDecimal CalcularAntiguedad(Date inicio) throws java.text.ParseException {
		Calendar today = Calendar.getInstance();

		Calendar fechaIngreso = Calendar.getInstance();
		fechaIngreso.setTime(inicio);

		int diff_year = today.get(Calendar.YEAR) - fechaIngreso.get(Calendar.YEAR);
		int diff_month = (12 + today.get(Calendar.MONTH)) - fechaIngreso.get(Calendar.MONTH);
		int diff_day = today.get(Calendar.DAY_OF_MONTH) - fechaIngreso.get(Calendar.DAY_OF_MONTH);

		// Computo diferencia de meses
		if (diff_day < 0)
			diff_month -=1;
		
		if (diff_month < 12)
			diff_year -=1;
		
		if (diff_month >= 12)
			diff_month -=12;
		
		double antiq =  diff_year;
		antiq += (float) diff_month / 12;
		
		BigDecimal ret = new BigDecimal(antiq).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return ret;
	}

	// TODO: split for character
	public String[] mySplit(char sep) {

		String[] r = new String[] {};
		return r;
	}
	
	public static void loadCMBFromAD(JComboBox cmb, String sql, boolean all) {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			System.out.println("sql to combo=" + sql);

			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();

			if (all) {
				cmb.addItem((Object) "TODOS");
				cmb.addItem((Object) "SIN OPCION");
			}

			while (rs.next()) {
				if (!rs.wasNull()) {
					String field = rs.getString(1);
					if (field == null)
						field = "<sin dato>";

					cmb.addItem((Object) field.toUpperCase());
				}
			}
			cmb.setSelectedIndex(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				DB.close(rs, pstmt);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
    public static String[] retrieveArrayFromAD(String sql, boolean all) {

    	ArrayList<String> data = new ArrayList<String>();

    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try {

    		System.out.println("sql to combo=" + sql);

    		pstmt = DB.prepareStatement(sql, null);
    		rs = pstmt.executeQuery();

    		if (all) {
    			// data.add("TODOS | 0000");
    			data.add("SIN OPCION | 0");
    		}

    		while (rs.next()) {
    			if (!rs.wasNull()) {
    				String field = rs.getString(1);
    				if (field == null)
    					field = "<sin dato>";

    				data.add(field.toUpperCase());
    			}
    		}

    	} catch (Exception ex) {
    		ex.printStackTrace();
    	} finally {
    		try {
    			DB.close(rs, pstmt);
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	}

    	return ConvertObjectArrayToStringArray(data.toArray());
    }
	
	public static ArrayList<Integer> retrieveArrayFromADAsInteger(String sql, boolean all) {
		
		ArrayList<Integer> data = new ArrayList<Integer>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			System.out.println("sql to combo=" + sql);

			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();

			if (all) {
				data.add(0);
			}

			while (rs.next()) {
				if (!rs.wasNull()) {
					Integer field = rs.getInt(1);
					if (field == null)
						field = 0;
					data.add(field);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				DB.close(rs, pstmt);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return data;
	}

	public static String[] ConvertObjectArrayToStringArray(Object[] array, boolean isAddNull) {
		String[] data = new String[array.length + (isAddNull?1:0)];
		
		for(int i=0; i<array.length; i++){
			data[i] = (String)array[i];
		}
		if(isAddNull)
			data[array.length-1] = "NULL";
		
		return data;
	}
	
	public static String[] ConvertObjectArrayToStringArray(Object[] array) {
		return ConvertObjectArrayToStringArray(array, false);
	}

	public static String[] String2Array(String nota, int len) {
		ArrayList<String> tmp = new ArrayList<String>();

		if (nota == null)
			tmp.add("");
		else if (nota.length() <= len)
			tmp.add(nota);
		else {

			nota += " ";
			int z = 0;
			String t = "";
			for (int i = 0; i < nota.length(); i++) {
				z++;
				if (z > len) {
					if (t.trim() != "NULL")
						tmp.add(t);
					z = 1;
					t = "";
				}
				if (i + 1 < nota.length()) {
					String x = nota.substring(i, i + 1);
					if (x.equals("\n")) { // un ENTER pasa al siguiente
						// renglon
						if (t.trim() != "NULL")
							tmp.add(t);
						z = 1;
						t = "";
					} else
						t += x;
				}
			}
			if (t.length() > 0)
				if (t.trim() != "NULL")
					tmp.add(t.trim());
		}

		int l = tmp.size();
		String[] x = new String[l];

		for (int i = 0; i < l; i++)
			x[i] = String.valueOf(tmp.get(i));

		return x;
	}
	
	public static String[] SplitString(String nota, String character) {
		ArrayList<String> tmp = new ArrayList<String>();

		if (nota == null)
			tmp.add("");
		else if (nota.indexOf(character) == -1){
			tmp.add(null);
		}else {

			nota += " ";
			int z = 0;
			String t = "";
			for (int i = 0; i < nota.length(); i++) {

				z++;

				if (i + 1 < nota.length()) {
					String x = nota.substring(i, i + 1);
					if (x.equals(character)) { // un caracter limite, agregar
						tmp.add(t);
						z = 1;
						t = "";
					} else
						t += x;
				}
			}
			if (t.length() > 0)
				tmp.add(t.trim());
		}

		int l = tmp.size();
		String[] x = new String[l];

		for (int i = 0; i < l; i++)
			x[i] = String.valueOf(tmp.get(i));

		return x;
	}

	public static String toString(String i, int len) {
		String r = toString(i);
		if (r.length() > len)
			r = r.substring(0, len);

		return r;
	}

	public static String toString(int i) {

		String r = "";
		r = String.valueOf(i);
		if (r == "NULL" || r == "null")
			r = "";
		return r;
	}

	public static String toString(String i) {

		String r = "";
		r = String.valueOf(i);
		if (r == "NULL" || r == "null")
			r = "";
		return r;
	}

	public static String toString(BigDecimal i) {

		String r = "";
		r = String.valueOf(i);
		if (r == "NULL" || r == "null")
			r = "";
		return r;
	}

	public static String toString(double i) {

		String r = "";
		r = String.valueOf(i);
		if (r == "NULL" || r == "null")
			r = "";
		return r;
	}

	public static String toString(Integer i) {

		String r = "";
		r = String.valueOf(i);
		if (r == "NULL" || r == "null")
			r = "";
		return r;
	}

	public static String toString(boolean i) {

		String r = "";
		r = String.valueOf(i);
		if (r == "NULL" || r == "null")
			r = "";
		return r;
	}

	public static String toString(Date i) {

		String r = "";
		r = String.valueOf(i);
		if (r == "NULL" || r == "null")
			r = "";
		return r;
	}

	public static int Entero(double m) {
		int si = 0;

		// Establece la configuraci�n del formateador de n�meros decimales.
		DecimalFormatSymbols dformater_rules = new DecimalFormatSymbols();
		dformater_rules.setDecimalSeparator('.');
		DecimalFormat dformater = new DecimalFormat("0.00", dformater_rules);

		String s = dformater.format(m);
		int pos = s.indexOf(".");

		if (pos > -1) {
			si = Integer.parseInt(s.substring(0, pos));
			// System.out.println("*** Entero:" + s.substring(0, pos));
		} else
			si = Integer.parseInt(s);

		return si;
	}

	public static int Decimal(Double n) {
		int d = 0;
		// Establece la configuraci�n del formateador de n�meros decimales.
		DecimalFormatSymbols dformater_rules = new DecimalFormatSymbols();
		dformater_rules.setDecimalSeparator('.');
		DecimalFormat dformater = new DecimalFormat("0.00", dformater_rules);

		String s = dformater.format(n);
		int pos = s.indexOf(".");

		if (pos > -1) {
			// System.out.println("*** Decimal: " + s.substring(pos+1));
			d = Integer.parseInt(s.substring(pos + 1));
		}

		return d;
	}

	public static double getDecimal(int numeroDecimales, double decimal) {
		decimal = decimal * (java.lang.Math.pow(10, numeroDecimales));
		decimal = java.lang.Math.round(decimal);
		decimal = decimal / java.lang.Math.pow(10, numeroDecimales);

		return decimal;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList LoadNodesChild(int NodeParent_ID, String treetype) {

		ArrayList<String[]> r = new ArrayList<String[]>();
		int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
		int AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());

		/*
		 * t.AD_Tree_ID -- ID del tree n.Node_ID, -- ID del nodo q representa el
		 * ID hijo del padre dentro de la tabla en cuestion sr.Name -- Nombre
		 * descriptivo dentro de la tabla hija
		 */

		String sql = "SELECT t.AD_Tree_ID,";
		sql += " n.Node_ID,";
		sql += " sr.Name";
		sql += " FROM AD_Tree AS t";
		sql += " LEFT JOIN AD_TreeNode AS n ON t.AD_Tree_ID = n.AD_Tree_ID";
		sql += " LEFT JOIN C_SalesRegion AS sr ON sr.C_SalesRegion_ID = n.Node_ID";
		sql += " WHERE t.AD_Client_ID =" + String.valueOf(AD_Client_ID)
				+ " AND t.AD_Org_ID IN(0," + String.valueOf(AD_Org_ID)
				+ ") AND t.TreeType='" + treetype + "'";
		sql += " AND";
		sql += " n.Parent_ID=" + String.valueOf(NodeParent_ID); // ID de lo q
		// estoy
		// buscando

		ResultSet rs = null;
		PreparedStatement pstmt = DB.prepareStatement(sql, null);

		try {
			// pstmt.setInt(1, AD_Client_ID);
			// pstmt.setInt(2, AD_Org_ID);
			// pstmt.setInt(3, NodeParent_ID);

			// System.out.println("sql tree:" + sql);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				String[] x = new String[] {
						String.valueOf(rs.getInt("AD_Tree_ID")),
						String.valueOf(rs.getInt("Node_ID")),
						String.valueOf(rs.getString("Name")) };
				r.add(x);
			}
			pstmt.close();
			rs.close();
		} // try
		catch (SQLException e) {
				e.printStackTrace();
		}finally{
			DB.close(rs, pstmt);
			rs=null;pstmt=null;
		}

		return r;
	}

	public static boolean isInteger(String trim) {
		boolean isInteger = true;

		String strInteger = "0123456789";
		for (int i = 0; i < trim.length(); i++) {
			String s = trim.substring(i, i + 1);
			if (strInteger.indexOf(s) <= -1) {
				isInteger = false;
				break;
			}
		}

		return isInteger;
	}

	public static double GetPorcentaje(BigDecimal valor, BigDecimal total) {

		double dvalor = BigDecimal2Double(valor);
		double dtotal = BigDecimal2Double(total);

		return (dvalor * 100 / dtotal);
	}
	
	public static String SqlString(String s){
		String r = String.valueOf(s);
		r = r.replace("'", "\"");
		return r;
	}
	
	public static String SqlString(String s, int n){
		String r = String.valueOf(s);
		r = r.replace("'", "\"");
		
		if(r.length() > n)
			r = r.substring(0, n);
		
		return r;
	}
	
	public static String SqlStringNull(String s){
		String r = String.valueOf(s);
		r = r.replace("null", "");
		return r;
	}
	
	public static boolean isVencim(int dia, Object[] o) {
		boolean ok = false;
		int xx = o.length;
		
		for (int i = 0; i < xx; i++) {
			String tmp = String.valueOf(o[i]).trim();
			if (tmp.equals("1 al 10")) {
				if (dia >= 1 && dia <= 10) {
					ok = true;
					break;
				}
			} else if (tmp.equals("11 al 31")) {
				if (dia > 10 && dia <= 31) {
					ok = true;
					break;
				}
			} else if (tmp.equals("Todos")){
						ok = true;
						break;
					}else{
						ok = true;
						break;
					}
				
		}

		return ok;
	}

	public static ArrayList<Integer> loadOrgs() {
		String sql = "SELECT AD_Org_ID FROM AD_Org WHERE AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx());
		ArrayList<Integer> x = new ArrayList<Integer>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			while(rs.next())
				x.add(Integer.valueOf(rs.getInt("AD_Org_ID")));
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DB.close(rs, pstmt);
			rs=null;pstmt=null;
		}
		return x;
	}
	
	/* Devuelve el id de un registro tomando como base el campo value */
	public static int LoadIDFromValue(String value, String tabla, String trx_name){
		
		int id = 0;
		String sql = "SELECT " + tabla + "_ID FROM " + tabla + " WHERE value='" + value + "'";
		
		try{	
			id = DB.getSQLValue(trx_name, sql);
		}catch(Exception ex){
			System.out.println("Error LoadIDFromValue:" + value + " tabla:" + tabla + "\n" + ex.getLocalizedMessage());
		}
		
		return id;
	}
	
	/* Devuelve el id de un registro tomando como base el campo name */
	public static int LoadIDFromName(String name, String tabla, String trx_name){
		
		int id = 0;
		String sql = "SELECT " + tabla + "_ID FROM " + tabla + " WHERE name='" + name + "'";
		
		try{	
			id = DB.getSQLValue(trx_name, sql);
		}catch(Exception ex){
			System.out.println("Error LoadIDFromName:" + name + " tabla:" + tabla + "\n" + ex.getLocalizedMessage());
		}
		
		return id;
	}
	
	/* Devuelve el Name de un registro tomando como base el campo ID */
	public static String LoadNamebyID(int ID, String tabla, String trx_name){
		
		String name = null;
		String sql = "SELECT name " + " FROM " + tabla + " WHERE " + tabla + "_ID=" + ID;
		
		try{
			name = DB.getSQLValueString(trx_name, sql);
		}catch(Exception ex){
			System.out.println("Error LoadNameByID:" + ID + " tabla:" + tabla + "\n" + ex.getLocalizedMessage());
		}
		
		return name;
	}
	
	/* Devuelve el Value de un registro tomando como base el campo ID */
	public static String LoadValuebyID(int ID, String tabla, String trx_name){
		
		String value = null;
		String sql = "SELECT value " + " FROM " + tabla + " WHERE " + tabla + "_ID=" + ID;
		
		try{
			value = DB.getSQLValueString(trx_name, sql);
		}catch(Exception ex){
			System.out.println("Error LoadValueByID:" + ID + " tabla:" + tabla + "\n" + ex.getLocalizedMessage());
		}	
		return value;
	}

	public static void CloseWaiting(Waiting mWaiting) {
		if (mWaiting != null)
			mWaiting.dispose();
		mWaiting = null;
	}
	
	// dREHER, devuelve un array del tipo byte desde un archivo de disco
	public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
        	// File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
        		&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
        	offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
        	throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
	
	// dREHER, este metodo por ahora lee los ID desde el AD_SysConfig. 
	// TODO: 9/2/2011 - Armar la busqueda de estos gerentes basado en el rol
	public static String LoadGerentesSucursal() {
		
		return Miscfunc.ValueFromSystem("GERENTES_SUCURSAL", "1000112,1000171,1000111", true); // por defecto Magliarelli,Muñoz, Fisher
	
	}
	
	public static String LoadAprobadoresForzados() {
		
		return Miscfunc.ValueFromSystem("APROBADORES_FORZADOS", "100,117", true); // por defecto SuperUser, Tere
	
	}

	public static String LoadUsuariosEspeciales() {
		
		return Miscfunc.ValueFromSystem("USUARIOS_ESPECIALES", "100,117,", true); // por defecto SuperUser, Tere, Jimena
	
	}
	
	public static String LoadUsuariosApruebanCualquierCosa() {
		
		return Miscfunc.ValueFromSystem("USUARIOS_APRUEBANMENOSCAPITAL", "100,", true); // por defecto SuperUser, Tere, Jimena
	
	}
	
	public static String LoadRolesCargaDatosMinimos() {
		
		return Miscfunc.ValueFromSystem("ROLES_CARGADATOSMINIMOS", "1000000", true); // por defecto AmericanAdmin
	
	}
	
	public static String LoadRolesDNIDuplicados() {
		
		return Miscfunc.ValueFromSystem("ROLES_CARGADNIDUPLICADOS", "100,1000000", true); // por defecto AmericanAdmin
	
	}
	
	public static String LoadSuperUsuarios() {
		
		return Miscfunc.ValueFromSystem("SUPER_USUARIOS", "100,1000000,", true); // por defecto SuperUser, AmericanAdmin
	
	}
	
	public static String LoadUsuariosCargaMasDeUnCredito(){
		return Miscfunc.ValueFromSystem("USUARIOS_CARGARMASDEUNCREDITO", "100,1000000,", true); // por defecto SuperUser, AmericanAdmin
	}
	
	public static String LoadSuperUser() {
		
		return Miscfunc.ValueFromSystem("SuperUser", "100,117,", true); // SOLO SuperUser y Tere
	
	}
	
	public static String LoadAprobadoresFinanciacionEspeciales() {
		
		return Miscfunc.ValueFromSystem("APROBADORES_FINANCIACION_ESPECIALES", "100,1000117,1000192", true); //Superuser,tere,eliana rojas
	
	}
	
	public static String LoadSucursalesSINControlCamposObligatorios() {
		
		return Miscfunc.ValueFromSystem("SUCURSALES_SIN_CONTROLCAMPOS_OBLIGATORIOS", "0", true); //Ciudad Moto
	
	}
	
	public static String LoadSalesRepSINControlCamposObligatorios() {
		
		return Miscfunc.ValueFromSystem("RepresentanteVentas_SIN_CONTROLCAMPOS_OBLIGATORIOS", "1007393", true); //Ciudad Moto
	
	}
	
	public static String LoadRolesAutorizadosExportarDatos() {
		
		return Miscfunc.ValueFromSystem("RolesAutorizadosExportarDatosGrilla", "1000000,1000011", true); // AmericanAdmin, Encargado Mora
	
	}
	
	public static String LoadRolesAutorizadosCambiarFechaCobro() {
		
		return Miscfunc.ValueFromSystem("RolesAutorizadosCambiarFechaCobro", "1000000", true); // AmericanAdmin, Encargado Mora
	
	}
	
	public static String LoadRolesAutorizadosTodasFormasCobro() {
		
		return Miscfunc.ValueFromSystem("LoadRolesAutorizadosTodasFormasCobro", "1000000, 1000010", true); // AmericanAdmin, Caja + Ventas
	
	}
	
	public static int LoadIDTable(String string) {
		int id = 0;
		
		id = DB.getSQLValue(null, "SELECT AD_Table_ID FROM AD_Table WHERE Trim(Name) = '" + string + "'");
		
		return id;
	}
	
	public static int LoadIDTableFromTableName(String string) {
		int id = 0;
		
		id = DB.getSQLValue(null, "SELECT AD_Table_ID FROM AD_Table WHERE Trim(TableName) = '" + string + "'");
		
		return id;
	}
	
	public static boolean UserEspecial(int adUserID, int opcion) {
		Boolean ok = false;
		
		String usersEspeciales = "";
		
		if(opcion == 1)
			usersEspeciales = Miscfunc.LoadGerentesSucursal();
		else if(opcion == 2)
			usersEspeciales = Miscfunc.LoadAprobadoresForzados();
		else if(opcion == 3)
			usersEspeciales = Miscfunc.LoadUsuariosEspeciales();
		else if(opcion == 4)
			usersEspeciales = Miscfunc.LoadSuperUsuarios();
		else if(opcion == 5)
			usersEspeciales = Miscfunc.LoadAprobadoresFinanciacionEspeciales();
		else if(opcion == 6)
			usersEspeciales = Miscfunc.LoadSucursalesSINControlCamposObligatorios();
		else if(opcion == 7)
			usersEspeciales = Miscfunc.LoadSalesRepSINControlCamposObligatorios();
		else if(opcion == 8)
			usersEspeciales = Miscfunc.LoadRolesAutorizadosExportarDatos();
		else if(opcion == 9)
			usersEspeciales = Miscfunc.LoadRolesAutorizadosCambiarFechaCobro();
		else if(opcion == 10)
			usersEspeciales = Miscfunc.LoadRolesAutorizadosTodasFormasCobro();
		else if(opcion == 11)
			usersEspeciales = LoadUsuariosApruebanCualquierCosa();
		
		ArrayList<Integer> users = new ArrayList<Integer>();
		
		if(usersEspeciales!=null && usersEspeciales.trim()!=""){

			if(usersEspeciales.indexOf(",") > -1){
				String[] x = usersEspeciales.split(",");
				for(int i=0; i<x.length; i++)
					users.add(new Integer(x[i].trim()));
			}else
				users.add(new Integer(usersEspeciales.trim()));

			for(int i=0; i<users.size(); i++){
				if(users.get(i).compareTo(adUserID)==0){
					ok = true;
					break;
				}
			}
			
		}
		
		return ok;
		
	}
	
	public static boolean UserGerenteDeSucursal(int adUserID, int opcion) {
		Boolean ok = false;
		
		String usersEspeciales = "";
		
		if(opcion == 1)
			usersEspeciales = Miscfunc.LoadGerentesSucursal();
		else if(opcion == 2)
			usersEspeciales = Miscfunc.LoadAprobadoresForzados();
		else if(opcion == 3)
			usersEspeciales = Miscfunc.LoadUsuariosEspeciales();
		else if(opcion == 4)
			usersEspeciales = Miscfunc.LoadSuperUsuarios();
		else if(opcion == 5)
			usersEspeciales = Miscfunc.LoadAprobadoresFinanciacionEspeciales();
		else if(opcion == 6)
			usersEspeciales = Miscfunc.LoadSuperUser();
		else if(opcion == 7)
			usersEspeciales = Miscfunc.LoadRolesCargaDatosMinimos();
		else if(opcion == 8)
			usersEspeciales = Miscfunc.LoadUsuariosCargaMasDeUnCredito();
		else if(opcion == 9)
			usersEspeciales = Miscfunc.LoadRolesDNIDuplicados();
		
		ArrayList<Integer> users = new ArrayList<Integer>();
		
		if(usersEspeciales!=null && usersEspeciales.trim()!=""){

			if(usersEspeciales.indexOf(",") > -1){
				String[] x = usersEspeciales.split(",");
				for(int i=0; i<x.length; i++)
					users.add(new Integer(x[i].trim()));
			}else
				users.add(new Integer(usersEspeciales.trim()));

			for(int i=0; i<users.size(); i++){
				if(users.get(i).compareTo(adUserID)==0){
					ok = true;
					break;
				}
			}
			
		}
		
		return ok;
		
	}
	
	public static Timestamp DateToTimestamp(Date fecha)
	{
		Timestamp fechafinal = null;

		try
		{
			if(fecha != null)
				fechafinal = new Timestamp(fecha.getTime());

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return fechafinal;
	}


	private static Timestamp sumaTiempo(Timestamp fechaOriginal, int field, int amount) 
	{
		java.util.Calendar calendario = java.util.Calendar.getInstance();
		calendario.setTimeInMillis(fechaOriginal.getTime());
		calendario.add(field, amount);
		Timestamp fechaResultante = new Timestamp(calendario.getTimeInMillis());

		return fechaResultante;
	}


	 public static Timestamp sumaMilisegundos(Timestamp fechaOriginal, int milisegundos)
	 {
		 return sumaTiempo(fechaOriginal, Calendar.MILLISECOND, milisegundos);
	 }

	 public static Timestamp sumaSegundos(Timestamp fechaOriginal, int segundos)
	 {
		 return sumaTiempo(fechaOriginal, Calendar.SECOND, segundos);
	 }

	 public static Timestamp sumaHoras(Timestamp fechaOriginal, int horas)
	 {
		 return sumaTiempo(fechaOriginal, Calendar.HOUR_OF_DAY, horas);
	 }

	 public static Timestamp sumaMinutos(Timestamp fechaOriginal, int minutos)
	 {
		 return sumaTiempo(fechaOriginal, Calendar.MINUTE, minutos);
	 }

	 public static Timestamp sumaDias(Timestamp fechaOriginal, int dias)
	 {
		 return sumaTiempo(fechaOriginal, Calendar.DAY_OF_MONTH, dias);
	 }

	 public static Timestamp sumaSemanas(Timestamp fechaOriginal, int semanas) 
	 {
		 return sumaTiempo(fechaOriginal, Calendar.WEEK_OF_MONTH, semanas);
	 }

	 public static Timestamp sumaMeses(Timestamp fechaOriginal, int meses) 
	 {
		 return sumaTiempo(fechaOriginal, Calendar.MONTH, meses);
	 }

	 public static Timestamp sumaAnios(Timestamp fechaOriginal, int anyos) 
	 {
		 return sumaTiempo(fechaOriginal, Calendar.YEAR, anyos);
	 }

	public static String ReplaceAllAlphabetic(String cadena, String reeplace) {

		String result = "";
		String CadenaAlphabetic = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz -_/\\=+!@#$%^&*()[]{};.,?<>";
		
		for(int i=0; i<cadena.length(); i++){
			String character = cadena.substring(i,i+1);
			if(CadenaAlphabetic.indexOf(character) == -1)
				result += character;
			else
				result += reeplace;
		}
		
		return result;
	}

	public static int PosicionArray(String[] payment, String valueOf) {
		int pos = -1;
		int posicion = 0;
		for(String data : payment){
			if(data!=null){
				if(data.equals(valueOf)){
					pos = posicion;
					break;
				}
			}
			posicion++;
		}
		return pos;
	}
	
	public static Date addDays(Date date1, int days) {
		Calendar calendar = toCalendar(date1);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}
	
	public static Date restDays(Date date1, int days) {
		Calendar calendar = toCalendar(date1);
		calendar.add(Calendar.DAY_OF_YEAR, days*(-1));
		return calendar.getTime();
	}
	
	private Long dayToMiliseconds(int days){
	    Long result = Long.valueOf(days * 24 * 60 * 60 * 1000);
	    return result;
	}

	public Timestamp addDays(int days, Timestamp t1) throws Exception{
	    if(days < 0){
	        throw new Exception("Day in wrong format.");
	    }
	    Long miliseconds = dayToMiliseconds(days);
	    return new Timestamp(t1.getTime() + miliseconds);
	}
	

	 /*  
	  * dREHER, segun hablado con Mariel/Moyano
	  * 
	  * Calcular lo q representa x dia la tasa elegida, osea tasa / 30 con eso obtenemos la tasa diaria, en funcion de la dif de dias en caso de q superen los 30, por ej si el 1er vto es a los 45 dias, seria 45 -30 =15 multiplicado x la tasa diaria daria un monto q podemos amortizar en todo el plan, osea: ((tasa/30)*(dias 1vto-30))
		Este resultado lo divido x la cantidad de cuotas y eso a su vez se lo sumo a la tasa original. Como es mas dificil explicarlo q verlo, hago un ejemplo:

		Tasa mensual: 6,7%
		Dias 1vto: 45 dias
		Plan 12 cuotas

		Desarrollo:

		Calculo tasa diaria:
		Td = 6,7 / 30 = 0,223

		Dias excedente:
		De = 45 - 30 = 15

		Tasa dias excedente:
		Tde = 15 × 0,223 = 3,34

		Distribucion mensual:
		Dm = 3,34 / 12 = 0,279

		Tasa final:
		Tf = 6,7 + 0,279 = 6,97
	*/
	public static BigDecimal RecalcularTasaInteres(BigDecimal tasaInteres,
			int diasDiff, int cantidadCuotas) {
		
		// Calculo la tasa diaria
		BigDecimal Td = tasaInteres.divide(new BigDecimal(30), 10, RoundingMode.DOWN);
		
		System.out.println("Tasa diaria (divide por 30)=" + Td + " tasa interes=" + tasaInteres);
		
		// Dias excedente:
		BigDecimal De = new BigDecimal(Math.abs(diasDiff));
		System.out.println("Dias excedentes=" + De);
		
		// Tasa dias excedente:
		BigDecimal Tde = De.multiply(Td);
		System.out.println("Tasa dias excedente=" + Tde);
		
		// Distribucion mensual:
		BigDecimal Dm = Tde.divide(new BigDecimal(cantidadCuotas), RoundingMode.DOWN);
		System.out.println("Distribucion menual=" + Dm + " en cuotas=" + cantidadCuotas);
		
		return Dm;
		
	}
	
	public static String[] StringToArray(String cadena, String separator){
		
		String[] result;
		ArrayList<String> lines = new ArrayList<String>();
		
		if(cadena.indexOf(separator) > -1){
			String[] x = cadena.split(separator);
			for(int i=0; i<x.length; i++)
				lines.add(x[i].trim());
		}else
			lines.add(cadena.trim());
		
		result = new String[lines.size()];
		lines.toArray(result);
		return result;
		
	}
	

	// dREHER, De una cadena con valores separados por comas, devuelve verdadero o falso si encuentra
	// el valor pasado como parametro
	public static boolean ValueInStringValues(String value, String values) {
		Boolean ok = false;

		ArrayList<String> valuesReaded = new ArrayList<String>();
		
		if(values!=null && values.trim()!=""){

			if(values.indexOf(",") > -1){
				String[] x = values.split(",");
				for(int i=0; i<x.length; i++)
					valuesReaded.add(x[i].trim());
			}else
				valuesReaded.add(values.trim());

			for(int i=0; i<valuesReaded.size(); i++){
				if(valuesReaded.get(i).equals(value)){
					ok = true;
					break;
				}
			}
			
		}
		
		return ok;
		
	}

	
    private static int getColSheet(String colLetra) {
    	String[] cols = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
    			"AA", "AB", "AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ",
    			"BA", "BB", "BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ",
    			"CA", "CB", "CC","CD","CE","CF","CG","CH","CI","CJ","CK","CL","CM","CN","CO","CP","CQ","CR","CS","CT","CU","CV","CW","CX","CY","CZ",
    			"DA", "DB", "DC","DD","DE","DF","DG","DH","DI","DJ","DK","DL","DM","DN","DO","DP","DQ","DR","DS","DT","DU","DV","DW","DX","DY","DZ",
    			"EA", "EB", "EC","ED","EE","EF","EG","EH","EI","EJ","EK","EL","EM","EN","EO","EP","EQ","ER","ES","ET","EU","EV","EW","EX","EY","EZ",
    			"FA", "FB", "FC","FD","FE","FF","FG","FH","FI","FJ","FK","FL","FM","FN","FO","FP","FQ","FR","FS","FT","FU","FV","FW","FX","FY","FZ",
    			"GA", "GB", "GC","GD","GE","GF","GG","GH","GI","GJ","GK","GL","GM","GN","GO","GP","GQ","GR","GS","GT","GU","GV","GW","GX","GY","GZ"};

    	int col = -1;
    	for(int i=0; i<cols.length; i++)
    		if(cols[i].equalsIgnoreCase(colLetra)){
    			col = i;
    			break;
    		}

    	return col;
    }

	private static int getFilaSheet(String filaLetra) {
		Integer fila = 0;
		try{
			fila = Integer.valueOf(filaLetra.trim()) - 1;
		}catch(Exception ex){
			fila = -1;
		}
		return fila;
	}

	public static Object[] addItemToArray(Object[] arr, Object item) {
		
		Object[] simpleArray = new Object[ arr.length + 1 ];
		int x = 0;
		for(Object o : arr){
			simpleArray[x] = arr[x];
			x++;
		}
		
		simpleArray[x] = item;
		
		return simpleArray;
	}
	
	// dREHER, parsea un string buscando la info en la lista de opciones enviada
	public static String parseArray (String cadena, ArrayList<String[]> datas, boolean ignoreUnparsable)
	{
		
		String token;
		String inStr = new String(cadena);
		StringBuffer outStr = new StringBuffer();

		int i = inStr.indexOf('@');
		while (i != -1)
		{
			outStr.append(inStr.substring(0, i));			// up to @
			inStr = inStr.substring(i+1, inStr.length());	// from first @

			int j = inStr.indexOf('@');						// next @
			if (j < 0)
			{
				 //System.out.println("No second tag: " + inStr);
				return "";						//	no second tag
			}

			// aca leyo la variable contenida entre los arrobas @
			token = inStr.substring(0, j);

			String ctxInfo = getDataReeplace(token, datas);	// get data from arraylist
			if (ctxInfo!= null && ctxInfo.length() == 0)
			{
				 //System.out.println("No Reeplace value = for: " + token);
				if (!ignoreUnparsable)
					return "";						//	token not found
			}
			else{
				//if(token.equals(value))
				//System.out.println("Encontro variable= " + token + " valor de reemplazo= " + ctxInfo);
				outStr.append(ctxInfo);				// replace context with Context
			}
			
			inStr = inStr.substring(j+1, inStr.length());	// from second @
			i = inStr.indexOf('@');
		}
		outStr.append(inStr);						// add the rest of the string

		//System.out.println("Cadena parseada= " + outStr);
		
		return outStr.toString();
	}	//	parseContext

	// dREHER
	private static String getDataReeplace(String token,
			ArrayList<String[]> datas) {
		String data = null;
		
		for(String[] info: datas){
			if(info[0].equals(token)){
				data = info[1];
				break;
			}
		}
		
		return data;
	}

	/**
	 * Parsea el XML recibido con las acciones a realizar sobre los datos
	 * @param xml el string recibido a ser parseado
	 * @param tagName entradas a buscar en el xml con una etiqueta dada
	 * @return un vector con la serie de acciones
	 * @throws Exception
	 */
	public static Vector<HashMap<String, String>> parseaXML(String xml, String tagName) throws Exception{

		// Parsear el documento recibido e ir generando las entradas correspondientes
		Vector<HashMap<String, String>> parameters = new Vector<HashMap<String, String>>();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		// dREHER, coloco bloque try para acorralar al error

		Document doc = null;
		try
		{
			doc = builder.parse(new InputSource(new StringReader(xml)));
			parameters = printElementAttributes(doc);
		}
		catch(Exception ex0){
			System.out.println("Error al parsear XML, error=" + ex0.toString() + "\n xml=" + xml + "\n tagName=" + tagName);
		}

/*		
		// Iterar por todos los tags
		NodeList nodes = doc.getElementsByTagName(tagName);
		for (int i = 0; i < nodes.getLength(); i++)
		{
			// Instanciar e incluir la nueva accion
			HashMap<String, String> hash = new HashMap<String, String>();
			hash.put(tagName, nodes.item(i).getNodeValue());
			
			parameters.add(hash);
			
			System.out.println("\n tagName:" + tagName);

			// Iterar por todos los parametros de un tag
			NamedNodeMap list = nodes.item(i).getAttributes();
			for (int j = 0; j < list.getLength(); j++)
			{
				// Setear los parametros modificadores
				String key = list.item(j).getNodeName();
				String value = list.item(j).getNodeValue();
				
				hash = new HashMap<String, String>();
				hash.put(key, value);
				
				System.out.println("\n key:" + key + "  value:" + value);

				parameters.add(hash);
			}
		}
*/		
		return parameters;
	}
	

	/**
	 * Parsea el XML recibido con las acciones a realizar sobre los datos
	 * @param xml el string recibido a ser parseado
	 * @param tagName entradas a buscar en el xml con una etiqueta dada
	 * @return un vector con la serie de acciones
	 * @throws Exception
	 */
	public static Vector<HashMap<String, String>> parseaXMLbyTagName(String xml, String tagName) throws Exception{

		// Parsear el documento recibido e ir generando las entradas correspondientes
		Vector<HashMap<String, String>> parameters = new Vector<HashMap<String, String>>();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		// dREHER, coloco bloque try para acorralar al error

		Document doc = null;
		try
		{
			doc = builder.parse(new InputSource(new StringReader(xml)));
			parameters = printElementAttributes(doc, tagName);
		}
		catch(Exception ex0){
			System.out.println("Error al parsear XML, error=" + ex0.toString() + "\n xml=" + xml + "\n tagName=" + tagName);
		}

/*		
		// Iterar por todos los tags
		NodeList nodes = doc.getElementsByTagName(tagName);
		for (int i = 0; i < nodes.getLength(); i++)
		{
			// Instanciar e incluir la nueva accion
			HashMap<String, String> hash = new HashMap<String, String>();
			hash.put(tagName, nodes.item(i).getNodeValue());
			
			parameters.add(hash);
			
			System.out.println("\n tagName:" + tagName);

			// Iterar por todos los parametros de un tag
			NamedNodeMap list = nodes.item(i).getAttributes();
			for (int j = 0; j < list.getLength(); j++)
			{
				// Setear los parametros modificadores
				String key = list.item(j).getNodeName();
				String value = list.item(j).getNodeValue();
				
				hash = new HashMap<String, String>();
				hash.put(key, value);
				
				System.out.println("\n key:" + key + "  value:" + value);

				parameters.add(hash);
			}
		}
*/		
		return parameters;
	}
	
	static Vector<HashMap<String, String>> printElementAttributes(Document doc)
	{
	   Vector<HashMap<String, String>> parameters = new Vector<HashMap<String, String>>();
		
	   NodeList nl = doc.getElementsByTagName("*");
	   Element e;
	   Node n;
	   NamedNodeMap nnm;
	 
	   String attrname;
	   String attrval;
	   int i, len;
	 
	   len = nl.getLength();

	   String pather = "";
	   
	   for (int j=0; j < len; j++)
	   {
	      e = (Element)nl.item(j);
	      
	      pather = pather + e.getTagName();
	      
	     System.out.println("xml => childNodes=" + e.getChildNodes().getLength() + " pather=" + pather + ", tagName=" + e.getTagName() + ": Contenido=" + e.getTextContent() + " <- ");
	      
	      if(e.getChildNodes().getLength() == 1 && e.getNodeType() == Node.ELEMENT_NODE){
	    	  HashMap<String, String> hash = new HashMap<String, String>();
	    	  String key = pather; // e.getTagName();
	    	  String value = e.getTextContent();
	    	  hash.put(key, value);

	    	   System.out.println("\n key:" + key + "  value:" + value);
	    	   pather = "";

	    	  parameters.add(hash);  
	      }else{
	    	  if(e.getChildNodes().getLength() == 2 && e.getNodeType() == Node.ELEMENT_NODE){
	    		  
	    		  NodeList cn = e.getChildNodes();
	    		  HashMap<String, String> hash = new HashMap<String, String>();
	    		  
	    		  Node node = cn.item(0);
	    		  
	    		  // Element tmp = (Element)cn.item(0);
	    		  // tmp.getTextContent();
	    		  pather = pather + node.getNodeValue(); 
	    		  
		    	  String key = pather; // node.getNodeValue(); 
		    	  
		    	  // tmp = (Element)cn.item(1);
	    		  node = cn.item(1);
	    		  
		    	  String value = node.getNodeValue(); // tmp.getTextContent();
		    	  System.out.println("\n3 key:" + key + "  value:" + value);
		    	  hash.put(key, value);
		    	  
		    	  pather = "";
		    	  
		    	  parameters.add(hash); 
	    	  }
	      }
	    	  
	      
	      nnm = e.getAttributes();
	 
	      if (nnm != null)
	      {
	         for (i=0; i<nnm.getLength(); i++)
	         {
	            n = nnm.item(i);
	            attrname = n.getNodeName();
	            attrval = n.getNodeValue();
	           // System.out.print("xml            atrtibuto => " + " " + attrname + " = " + attrval);
	         }
	      }
	      // System.out.println();
	   }
	   
	   return parameters;
	   
	}
	
	static Vector<HashMap<String, String>> printElementAttributes(Document doc, String tagName)
	{
	   Vector<HashMap<String, String>> parameters = new Vector<HashMap<String, String>>();
		
	   NodeList nl = doc.getElementsByTagName(tagName);
	   Element e;
	   Node n;
	   NamedNodeMap nnm;
	 
	   String attrname;
	   String attrval;
	   int i, len;
	 
	   len = nl.getLength();

	   for (int j=0; j < len; j++)
	   {
	      e = (Element)nl.item(j);
	      
	      
	      // System.out.println("xml => childNodes=" + e.getChildNodes().getLength() + " , tagName=" + e.getTagName() + ": Contenido=" + e.getTextContent() + " <- ");
	      
	      if(e.getChildNodes().getLength() == 1 && e.getNodeType() == Node.ELEMENT_NODE){
	    	  HashMap<String, String> hash = new HashMap<String, String>();
	    	  String key = e.getTagName();
	    	  String value = e.getTextContent();
	    	  hash.put(key, value);

	    	  System.out.println("\n key:" + key + "  value:" + value);

	    	  parameters.add(hash);  
	      }
	    	  
	      
	      nnm = e.getAttributes();
	 
	      if (nnm != null)
	      {
	         for (i=0; i<nnm.getLength(); i++)
	         {
	            n = nnm.item(i);
	            attrname = n.getNodeName();
	            attrval = n.getNodeValue();
	          //  System.out.print("xml            atrtibuto => " + " " + attrname + " = " + attrval);
	         }
	      }
	      // System.out.println();
	   }
	   
	   return parameters;
	   
	}
	
	public static String CleanCharacters(String res) {
		
		if(res.indexOf("Ã±") >-1)
			res = res.replace("Ã±", "ni");
		if(res.indexOf("Ã‘") >-1)
			res = res.replace("Ã‘", "NI");
		if(res.indexOf("Ãƒ") >-1)
			res = res.replace("Ãƒ","ni");
		if(res.indexOf("Ã") >-1)
			res = res.replace("Ã","ni");
		
		if(res.indexOf("&lt;") > -1)
			res = res.replace("&lt;", "<");
		if(res.indexOf("&gt;") > -1)
			res = res.replace("&gt;", ">");
		
		if(res.indexOf("\n") > -1)
			res = res.replace("\n", "");
		if(res.indexOf("\t") > -1)
			res = res.replace("\t", "");	
		
		if(res.indexOf("Ã“") > -1)
			res = res.replace("Ã“","O");
		if(res.indexOf("Ã³") > -1)
			res = res.replace("Ã³","o");
		
		//if(res.indexOf("<![CDATA[") > -1){
		//	res = res.replace("<![CDATA[","").replace("]]>", "");
		//}
		
		return res;
	}
	
	
	// -------------
	public static Vector<HashMap<String, String>> ReadXMLFile2(String xml, String tagName){
		
		// Parsear el documento recibido e ir generando las entradas correspondientes
		Vector<HashMap<String, String>> parameters = new Vector<HashMap<String, String>>();

		// dREHER, coloco bloque try para acorralar al error

		Document doc = null;
		try {

			// File file = new File("/Users/mkyong/staff.xml");

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			doc = dBuilder.parse(new InputSource(new StringReader(xml)));

			// System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			if (doc.hasChildNodes()) {

				parameters = printNote(doc.getElementsByTagName(tagName), "", parameters);

			}

		} catch (Exception e) {
			System.out.println("Miscfunc.ReadXMLFile2 - Error al leer archivo xml=" + xml + "\n" + e.getMessage());
		}
		
		return parameters;

	}
	
	public static Vector<HashMap<String, String>> ReadXMLFile2(String xml){
		
		// Parsear el documento recibido e ir generando las entradas correspondientes
		Vector<HashMap<String, String>> parameters = new Vector<HashMap<String, String>>();

		// dREHER, coloco bloque try para acorralar al error

		Document doc = null;
		try {

			// File file = new File("/Users/mkyong/staff.xml");

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			doc = dBuilder.parse(new InputSource(new StringReader(xml)));

			// System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			if (doc.hasChildNodes()) {

				parameters = printNote(doc.getChildNodes(), "", parameters);

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return parameters;

	}
	
	public static Vector<HashMap<String, String>> readXMLNodev2(String xml, String tagNode) {

		// Parsear el documento recibido e ir generando las entradas correspondientes
		Vector<HashMap<String, String>> parameters = new Vector<HashMap<String, String>>();
		Document doc = null;
		try {

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			doc = dBuilder.parse(new InputSource(new StringReader(xml)));

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			// System.out.println(xml);
			// System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName(tagNode);

			// System.out.println("----------------------------");
			// System.out.println("nList.getLength()= " + nList.getLength());

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				// System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					HashMap<String, String> hash = new HashMap<String, String>();

					/*System.out.println("Indicador : " + temp+1); // eElement.getAttribute("id"));
					System.out.println("Grupo : " + eElement.getElementsByTagName("GRUPO").item(0).getTextContent());
					System.out.println("Nombre : " + eElement.getElementsByTagName("NOMBRE").item(0).getTextContent());
					System.out.println("Valor : " + eElement.getElementsByTagName("VALOR").item(0).getTextContent());
					System.out.println("Tipo : " + eElement.getElementsByTagName("TIPO").item(0).getTextContent());
					*/
					
					String key = eElement.getElementsByTagName("NOMBRE").item(0).getTextContent();
					//String value = "INDICADOR:" + (temp+1) + "," + "NOMBRE:" + key + "," + "GRUPO:" + eElement.getElementsByTagName("GRUPO").item(0).getTextContent() + "," +
					//				"VALOR:" + eElement.getElementsByTagName("VALOR").item(0).getTextContent() + "," +
					//				"TIPO:" + eElement.getElementsByTagName("TIPO").item(0).getTextContent();
					String value = eElement.getElementsByTagName("VALOR").item(0).getTextContent();
					hash.put(key, value);

					parameters.add(hash);  

				}
			}
			
			// System.out.println("----------------------------");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return parameters;
		
	}
	
	private static Vector<HashMap<String, String>> printNote(NodeList nodeList, String name, Vector<HashMap<String, String>> parameters) {
		
		int contador = 0;
		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);
			
			
			//System.out.println("\n" + tempNode.getNodeName() + ", tempNode.getNodeType=" + tempNode.getNodeType() + " " + Node.ELEMENT_NODE);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE || tempNode.getNodeType() == Node.CDATA_SECTION_NODE) {

				// get node name and value
				//System.out.println("Node Name =" + tempNode.getNodeName() + " [OPEN]");
				// System.out.println("Node Value =" + tempNode.getTextContent());
				
				String attrib = "";
				if (tempNode.hasAttributes()) {

					// get attributes names and values
					NamedNodeMap nodeMap = tempNode.getAttributes();
					String att = "";
					contador++;
					String contenido = "";
					for (int i = 0; i < nodeMap.getLength(); i++) {

						Node node = nodeMap.item(i);
						// System.out.println("attr name : " + node.getNodeName());
						// System.out.println("attr value : " + node.getNodeValue());
						attrib += (attrib!=""?"_":"")+node.getNodeName() + "_" + node.getNodeValue();
						att = "_"+node.getNodeName(); 

						
						HashMap<String, String> hash = new HashMap<String, String>();
						String key = name+(name!=""?"_":"")+tempNode.getNodeName()+att+"_"+Miscfunc.Padleft(String.valueOf(i+1), 4, "0")+"_"+Miscfunc.Padleft(String.valueOf(contador), 4, "0");
						key = cleanField(key);
						
						String value = CleanCharacters(node.getNodeValue());
						hash.put(key, value);

						parameters.add(hash);
						
						contenido += (contenido!=""?",":"") + node.getNodeName() + ":" + value;
						
					}
					
					// Doy de alta una variable con todo el contenido
					HashMap<String, String> hash = new HashMap<String, String>();
					hash.put(name+"_"+Miscfunc.Padleft(String.valueOf(contador), 4, "0"), contenido);
					parameters.add(hash);
					
					//System.out.println("Leyo atributos...");

				}
				
				
				//if(tempNode.getNodeValue().equals("<![CDATA[") || tempNode.getNodeName().equals("#cdata-section")){
				//	System.out.println("Se trata de un cdata-section");
				//}
				
				if (tempNode.getChildNodes().getLength() == 1 || tempNode.getNodeType() == Node.CDATA_SECTION_NODE) {
					
					HashMap<String, String> hash = new HashMap<String, String>();
					String key = name+(name!=""?"_":"")+tempNode.getNodeName()+(attrib!=""?"_":"")+attrib;
					key = cleanField(key);
					
					String value = CleanCharacters(tempNode.getTextContent());
					hash.put(key, value);

					parameters.add(hash);  
					
					//System.out.println("Node length==1 key=" + key + " value=" + value);

				}
				
				//System.out.println("Antes de comprobar que tiene hijos..." + name+(name!=""?"_":"")+tempNode.getNodeName()+(attrib!=""?"_":"")+attrib);

				if (tempNode.hasChildNodes()) {

					// loop again if has child nodes
					printNote(tempNode.getChildNodes(), name+(name!=""?"_":"")+tempNode.getNodeName()+(attrib!=""?"_":"")+attrib, parameters);

				}

				//System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]" + tempNode.hasChildNodes() + " " + tempNode.hashCode() + " " + tempNode.getChildNodes().getLength());
				
				// hash = new HashMap<String, String>();
				// key = tempNode.getNodeName() + " [CLOSE]";
				// value = null;
				// hash.put(key, value);
				
				// parameters.add(hash);  

			}

		}

		return parameters;

	}

	private static Vector<HashMap<String, String>> printNoteX(NodeList nodeList, String name, Vector<HashMap<String, String>> parameters) {
		
		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				// get node name and value
				// System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
				// System.out.println("Node Value =" + tempNode.getTextContent());
				
				String attrib = "";
				if (tempNode.hasAttributes()) {

					// get attributes names and values
					NamedNodeMap nodeMap = tempNode.getAttributes();

					for (int i = 0; i < nodeMap.getLength(); i++) {

						Node node = nodeMap.item(i);
						//System.out.println("attr name : " + node.getNodeName());
						//System.out.println("attr value : " + node.getNodeValue());
						
						attrib = "_"+node.getNodeName(); 
						
						HashMap<String, String> hash = new HashMap<String, String>();
						String key = name+(name!=""?"_":"")+tempNode.getNodeName()+attrib+"_"+Miscfunc.Padleft(String.valueOf(i+1), 4, "0");
						key = cleanField(key);
						
						String value = CleanCharacters(node.getNodeValue());
						hash.put(key, value);

						parameters.add(hash);
						
					}

				}
				
				if (tempNode.getChildNodes().getLength() == 1) {
					
					HashMap<String, String> hash = new HashMap<String, String>();
					String key = name+(name!=""?"_":"")+tempNode.getNodeName()+(attrib!=""?"_":"")+attrib;
					key = cleanField(key);
					
					String value = CleanCharacters(tempNode.getTextContent());
					hash.put(key, value);

					parameters.add(hash);  

				}else{
					//System.out.println("Mas de un hijo..." + tempNode.getNodeName() + ":" + tempNode.getNodeValue());
				}

				if (tempNode.hasChildNodes()) {
																									// 03/04/2018 tenia esto... +(attrib!=""?"_":"")+attrib
					// loop again if has child nodes
					printNote(tempNode.getChildNodes(), name+(name!=""?"_":"")+tempNode.getNodeName(), parameters);

				}

				// System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
				
				// hash = new HashMap<String, String>();
				// key = tempNode.getNodeName() + " [CLOSE]";
				// value = null;
				// hash.put(key, value);
				
				// parameters.add(hash);  

			}

		}

		return parameters;

	}
	
	public static String cleanField(String field){
		
		String ret = field;
		
		ret = ret.replace("soap:Envelope_xmlns:soap_http://schemas.xmlsoap.org/soap/envelope/_xmlns:xsd_http://www.w3.org/2001/XMLSchema_xmlns:xsi_http://www.w3.org/2001/XMLSchema-instance_soap:Body_getROLInfoDNIResponse_xmlns_https://servicios.reportesonline.com/webservices/xmlservice.asmx_getROLInfoDNIResult", "");
		
		return ret;
	}

	
	// -------------
	
	
	// dREHER Devuelve un arraylist de los archivos adjuntos en un registro
		public static ArrayList<File> LoadAttachFiles(int idAtt) throws Exception 
		{
			ArrayList<File> att = new ArrayList<File>();
		    
			MAttachment ma = new MAttachment(Env.getCtx(), idAtt, null);
			if (ma.getEntries() != null) {

				if ( ma.getEntries().length > 0 && ma.getEntry(0) != null) {

					 MAttachmentEntry[] e = ma.getEntries();
					 File file;
					 for (int i = 0; i < e.length; i++){
							file = e[i].getFile();
							att.add(file);
					 }
					 
				}
			}

			return att;
		}
		
		public static int getIDAttach(String TableName, int recordID) {
			int idT = LoadIDTable(TableName);
			return getIDAttach(idT, recordID);
		}
		
		// dREHER, busco si ya tiene ID de Attachment
		public static int getIDAttach(int tableID, int recordID) {
			int i = 0;

			String sql = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID="
					+ tableID + " AND Record_ID=" + recordID + " AND isActive='Y'";
			i = DB.getSQLValue(null, sql);

			return i;
		}

		public static BigDecimal currencyConvert(BigDecimal amount, int currencyFrom, int currencyTo, Date date, int adOrg, Properties ctx )
		{
			BigDecimal result = null;
			try
			{
				StringBuffer sql = new StringBuffer("SELECT currencyconvert (?, ?, ?, ? ::timestamp, null, ?, ");
				if (adOrg > 0)
					sql.append( "? )");
				else
					sql.append( "null )");

				PreparedStatement pstmt = DB.prepareStatement(sql.toString());
				pstmt.setBigDecimal(1, amount);
				pstmt.setInt(2, currencyFrom);
				pstmt.setInt(3, currencyTo);
				//pstmt.setDate(4, new  java.sql.Date(date.getTime()) );
				// currencyconvert requiere un timestamp como parametro. En ciertos casos 
				// estaba funcionando mal con el Date. 
				pstmt.setTimestamp(4, new Timestamp(date.getTime()) ); 
				pstmt.setInt(5, Env.getAD_Client_ID(ctx) );

				if (adOrg > 0)
					pstmt.setInt(6, adOrg );

				ResultSet rs = pstmt.executeQuery();
				if (rs.next())
					result = rs.getBigDecimal(1);
			}
			catch (Exception e ) {
				e.printStackTrace();
			}
			return result;
		}

		public static boolean ComprobantesFiscalesActivos(boolean reload) {
		
			boolean st_ComprobantesFiscalesActivos = false;
			
			if (st_ComprobantesFiscalesActivos == false || reload) {
			CPreparedStatement st = DB.prepareStatement(" SELECT value FROM AD_Preference WHERE attribute = 'LOCAL_AR' ");
			ResultSet rs = null;
			try {
				rs = st.executeQuery();
				
				st_ComprobantesFiscalesActivos = (rs.next()) && rs.getString(1).equals("Y");
			} catch (SQLException e) {
				st_ComprobantesFiscalesActivos = false;
			} finally {
				try {
					st.close();
				} catch (SQLException e) {}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {}
				}
			}
			
		}
		return st_ComprobantesFiscalesActivos;
	}
	
	public static boolean ComprobantesFiscalesActivos() {
		return ComprobantesFiscalesActivos(false);
	}

	public static String getValueFromMap(Vector<HashMap<String, String>> vars,
			String var, String type, String defa, boolean isLastData) {
		String value = "";
		
		boolean isBreak = false;
		
		if(var != null)
			var = var.trim();
		
		for(Map<String, String> map: vars){
			
			// System.out.println("leyo map..." + map);
			
			isBreak = false;
			
			for (String key : map.keySet()) {
				
				if(key!=null){

					// System.out.println("Key=" + key + ", var=" + var + " isLastData= " + isLastData);
					
					boolean isKey = false;
					
					if(var.indexOf(" ") > -1){
						
						isKey = true;
						String[] llaves = Miscfunc.SplitString(var, " ");
						for(String x: llaves){
							if(key.indexOf(x) == -1){
								isKey = false;
								break;
							}
						}
						
						
					}else{
						isKey = key.endsWith(var);
					}
					
					
					if(isKey){ // Original 
						
						value = map.get(key);
						// System.out.println("Key=" + key + ", Value=" + value + " isLastData= " + isLastData);

						try{
							if(type.equals("S"))
								;
							else if(type.equals("I"))
								value = Integer.valueOf(value.trim()).toString();
							else if(type.equals("N"))
								value = Double.valueOf(value.trim()).toString();
							else if(type.equals("D"))
								value = Miscfunc.CtoD(value).toString();
						}catch(Exception ex){
							System.out.println("Error al convertir a " + type + " el contenido= >" + value + "<");
							value = String.valueOf(value);
						}
						
						// Sino solicita la ultima data, corta cuando encuentra coincidencia en la primer data
						if(!isLastData){
							isBreak = true;
							break;
						}
						
					}

				}
	
			}
			
			if(isBreak)
				break;
			
		}
		
		if(value==null || value.equals("")){
			value = defa;
			if(value==null)
				value = "";
		}
		
		return value;
	}

	public static Vector<HashMap<String, String>> getTableFromMap(
			Vector<HashMap<String, String>> vars, String var) {
		
		Vector<HashMap<String, String>> tabla = new Vector<HashMap<String,String>>();
		
		for(Map<String, String> map: vars){
			// System.out.println("leyo map table...");
			for (String key : map.keySet()) {
				// System.out.println("leyo map...key= " + key + " var= " + var);
				if(key.startsWith(var)){
					String label = key;
					int i = key.lastIndexOf("_");
					if(i > -1){
						if(i<=label.length())
							i++;
						label = label.substring(i);
					}
					
					HashMap<String,String> hash = new HashMap<String,String>();
					hash.put(label, map.get(key));
					tabla.add(hash);
				}
			}
		}
		
		
		return tabla;
	}

	public static String getStringSinNullTrim(String string) {
		String ret = string;
		
		if(ret==null)
			ret = "";
		else
			if(ret.equals("null"))
				ret = "";
			else
				ret = ret.trim();

		return ret;
	}

	/* xom
	public static Vector<HashMap<String, String>> parseaXMLXOM(
			String XML, String nodoRaiz) {
		
		// Creamos el builder XOM  
		nu.xom.Builder builder = new nu.xom.Builder();  
		// Construimos el arbol DOM a partir del fichero xml  
		nu.xom.Document doc = null;
		try {
			doc = builder.build(XML);
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		// Obtenemos la etiqueta raíz  
		nu.xom.Element raiz = doc.getRootElement();  
		// Recorremos los hijos de la etiqueta raíz  
		nu.xom.Elements hijosRaiz = raiz.getChildElements();  
		for(int i=0;i<hijosRaiz.size();i++){  
		   nu.xom.Element hijo = hijosRaiz.get(i);  
		     
		   // Obtenemos el nombre y su contenido de tipo texto  
		   String nombre = hijo.getLocalName();  
		   String texto = hijo.getValue();  
		     
		   System.out.println("\nEtiqueta: "+nombre+". Texto: "+texto);  
		     
		   // Obtenemos el atributo id si lo hubiera  
		   String id = hijo.getAttributeValue("id");  
		   if(id!=null){  
		      System.out.println("\tId: "+id);  
		   }  
		}  

		
		return null;
	}
	*/

	/* Requiere XmlNode
	private static String getFromNode(String xml, String firstNode, String[] nodes, String match, String ret) {
		
		String valor = null;
		
		Document doc = null;
		try {
			
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			doc = dBuilder.parse(new InputSource(new StringReader(xml)));
			
			// Parto del nodo raiz
			
			NodeList nl = doc.getElementsByTagName(firstNode);
			XmlNode raiz = new XmlNode(nl.item(0));
			
			s_log.info("Primer nodo = " + raiz.getName());
			
			String nodeX = "";			
			for(String node: nodes){
				
				nodeX += (nodeX!=""?"_":"") + node;
				s_log.fine("Busco subnode= " + node);
				
				XmlNode tmp = raiz.subNode(node);
				if(tmp != null){
					
					String tmpVal = tmp.getValue();
					s_log.fine("Encontre subnode= " + tmp.getName() + " valor= " + tmpVal);
					
					if(tmpVal != null){
						if(tmpVal.equals(match)){
							s_log.fine("Encontro coincidencia de nodo, leo valor en nodo= " + ret);
							XmlNode tmpII = raiz.subNode(ret);
							if(tmpII!=null){
								valor = tmpII.getValue();
								s_log.fine("Encontre node ret, valor= " + valor);
								break;
							}
						}
					}
					
					raiz = tmp;
				}
				
			}
			
			s_log.info("Node= " + nodeX + " valor= " + valor);
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return valor;
		
	}
	*/

		// itextpdf-5.4.1.jar  http://sourceforge.net/projects/itext/files/iText/
		// xmlworker-5.4.1.jar http://sourceforge.net/projects/xmlworker/files/
		/*
		public static boolean HtmlToPDF2(String texto, String fileName ) {
			
			boolean isOk = true;
			
			try {
				com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.LETTER);
				com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance
						(document, new FileOutputStream(fileName));
				document.open();
				document.addAuthor("dREHER");
				document.addCreator("dREHER");
				document.addSubject("PDF's from Adempiere");
				document.addCreationDate();
				document.addTitle("Archivo PDF");

				XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

				worker.parseXHtml(pdfWriter, document, new StringReader(texto));
				document.close();
				System.out.println("Done.");
			}
			catch (Exception e) {
				isOk = false;
				e.printStackTrace();
			}
			
			return isOk;
			
		}
		
		public static boolean HtmlToPDF(String texto, String fileName ) {
			
			boolean isOk = true;
			
			try {
				com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.LETTER);
				com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance
						(document, new FileOutputStream(fileName));
				document.open();
				document.addAuthor("dREHER");
				document.addCreator("dREHER");
				document.addSubject("PDF's from Adempiere");
				document.addCreationDate();
				document.addTitle("Archivo PDF");

				HTMLWorker htmlWorker = new HTMLWorker(document);

				htmlWorker.parse(new StringReader(texto));
			    
				document.close();
				System.out.println("Done.");
			}
			catch (Exception e) {
				isOk = false;
				e.printStackTrace();
			}
			
			return isOk;
			
		}
		*/
	
	/**
 	 * Función que elimina acentos y caracteres especiales de
 	 * una cadena de texto.
 	 * @param input
 	 * @return cadena de texto limpia de acentos y caracteres especiales.
 	 */
 	public static String removeAcentosEnie(String input) {
 		// Cadena de caracteres original a sustituir.
 		String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿñ";
 		// Cadena de caracteres ASCII que reemplazarán los originales.
 		String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyyn";
 		String output = input;
 		int len = original.length();
 		for (int i=0; i<len; i++) {
 			// Reemplazamos los caracteres especiales.
 			output = output.replace(original.charAt(i), ascii.charAt(i));
 		}//for i
 		return output;
 	}

	public static boolean isNumeric(String value) {
		int len = value.length();
		boolean ok = true;
 		for (int i=0; i<len; i++) {
 			
 			String pos = value.substring(i, i+1);
 			if("0123456789-.".indexOf(pos) == -1){
 				ok = false;
 				break;
 			}
 			
 		}//for i
 		
		return ok;
	}

	public static boolean IsOkFecha(String value) {
		boolean isOk = false;
		
		try{
			
			if(value!=null && value!="" && value!="-" && value.length() >= 8 ){
				Timestamp x = Timestamp.valueOf(value);
				isOk = true;
			}else
				System.out.println("Quiere convertir a Timestamp el dato= " + value);
						
		}catch(Exception ex){
			isOk = false;
		}
		
		return isOk;
	}

	public static boolean isDirectory(String ruta, boolean isCreate) {
		boolean isOk = true;
		
		File folder = new File(ruta);
		if(!folder.isDirectory()){
			if(isCreate){
				isOk = folder.mkdir();
			}else
				isOk = false;
		}
		
		return isOk;
	}

	public static Timestamp FechaHOYSQL() {
		Timestamp hoy = DB.getSQLValueTimestamp(null, "SELECT current_date");
		return hoy;
	}
	
	// SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the substring before the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * A <code>null</code> separator will return the input string.</p>
     *
     * <p>If nothing is found, the string input is returned.</p>
     *
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the first occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringBefore(String str, String separator) {
        if (str.isEmpty() || separator == null) {
            return str;
        }
        if (separator.length() == 0) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
   }

    /**
     * <p>Gets the substring after the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * A <code>null</code> separator will return the empty string if the
     * input string is not <code>null</code>.</p>
     *
     * <p>If nothing is found, the empty string is returned.</p>
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringAfter(String str, String separator) {
        
    	
    	if (str.isEmpty()) {
           return str;
        }
        if (separator == null) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
    
    // dREHER, limpia la basura que agrega Adempiere en el parseo de variables
    public static String limpiarBasuraEnSQL(String sql){
    	String sqlFinal = sql;
    	
    	sqlFinal = sqlFinal.replaceAll("</head>", "");
    	sqlFinal = sqlFinal.replaceAll("<head>", "");
    	sqlFinal = sqlFinal.replaceAll("</body>", "");
    	sqlFinal = sqlFinal.replaceAll("<body>", "");
    	sqlFinal = sqlFinal.replaceAll("</html>", "");
    	sqlFinal = sqlFinal.replaceAll("<html>", "");
    	return sqlFinal; 	
    }

    public static String parseContext (String cadena, String value, String reeplace, boolean ignoreUnparsable)
    {
    	if (value == null || value.length() == 0)
    		return "";

    	String token;
    	String inStr = new String(cadena);
    	StringBuffer outStr = new StringBuffer();

    	int i = inStr.indexOf('@');
    	while (i != -1)
    	{
    		outStr.append(inStr.substring(0, i));			// up to @
    		inStr = inStr.substring(i+1, inStr.length());	// from first @

    		int j = inStr.indexOf('@');						// next @
    		if (j < 0)
    		{
    			System.out.println("No second tag: " + inStr);
    			return "";						//	no second tag
    		}

    		token = inStr.substring(0, j);

    		String ctxInfo = reeplace;	// get context
    		if (ctxInfo.equals(null) || ctxInfo.length() == 0)
    		{
    			System.out.println("No Reeplace value = for: " + token);

    			if (!ignoreUnparsable)
    				return "";						//	token not found
    		}
    		else{
    			if(token.equals(value))
    				outStr.append(ctxInfo);				// replace context with Context
    		}
    		
    		inStr = inStr.substring(j+1, inStr.length());	// from second @
    		i = inStr.indexOf('@');
    	}
    	outStr.append(inStr);						// add the rest of the string

    	return outStr.toString();
    }	//	parseContext

    
    public static String parseContext (String cadena, String value, String reeplace, boolean ignoreUnparsable, boolean reeplaceByCero)
    {
    	if (value == null || value.length() == 0)
    		return "";

    	String token;
    	String inStr = new String(cadena);
    	StringBuffer outStr = new StringBuffer();

    	int i = inStr.indexOf('@');
    	while (i != -1)
    	{
    		outStr.append(inStr.substring(0, i));			// up to @
    		inStr = inStr.substring(i+1, inStr.length());	// from first @

    		int j = inStr.indexOf('@');						// next @
    		if (j < 0)
    		{
    			System.out.println("No second tag: " + inStr);
    			return "";						//	no second tag
    		}

    		token = inStr.substring(0, j);

    		String ctxInfo = reeplace;	// get context
    		if (ctxInfo.equals(null) || ctxInfo.length() == 0)
    		{
    			System.out.println("No Reeplace value = for: " + token);
    			if(reeplaceByCero){
    				if(token.toUpperCase().endsWith(value)){
    					outStr.append(ctxInfo);
    				}
    			}else
    				if (!ignoreUnparsable)
    					return "";						//	token not found
    		}
    		else{
    			
    			if(reeplaceByCero){
    				if(token.toUpperCase().endsWith(value)){
    					outStr.append(ctxInfo);
    				}
    			}else
    				if(!value.equals(null) && token.equals(value))
    					outStr.append(ctxInfo);				// replace context with Context
    		}
    		
    		inStr = inStr.substring(j+1, inStr.length());	// from second @
    		i = inStr.indexOf('@');
    	}
    	outStr.append(inStr);						// add the rest of the string

    	return outStr.toString();
    }	//	parseContext

    public static String parseContextSheet (String cadena, Sheet sheet, boolean ignoreUnparsable)
    {
    	if (cadena == null || cadena.length() == 0)
    		return "";

    	String token;
    	String inStr = new String(cadena);
    	StringBuffer outStr = new StringBuffer();

    	int i = inStr.indexOf('$');
    	while (i != -1)
    	{
    		outStr.append(inStr.substring(0, i));			// up to #
    		inStr = inStr.substring(i+1, inStr.length());	// from first #

    		int j = inStr.indexOf('$');						// next #
    		if (j < 0)
    		{
    			System.out.println("No second tag: " + inStr);
    			return "";						//	no second tag
    		}

    		token = inStr.substring(0, j);

    		System.out.println("token=" + token);

    		String colLetra = token.substring(0, token.indexOf(":"));
    		String filaLetra = token.substring(token.indexOf(":")+1);

    		int fila = getFilaSheet(filaLetra);
    		int col = getColSheet(colLetra);

    		System.out.println("colLetra=" + colLetra + " col=" + col);
    		System.out.println("filaLetra=" + filaLetra + " fila=" + fila);

    		Cell cell = sheet.getCell(col, fila);
    		String reeplace = "";
    		if(cell != null){
    			CellType type = cell.getType();
    			if(type.equals(CellType.LABEL)){
    				reeplace = cell.getContents();
    			}else if(type.equals(CellType.NUMBER)){
    				reeplace = cell.getContents();
    			}else if(type.equals(CellType.DATE)){
    				DateCell datecell = (DateCell)cell;
    				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
    				// System.out.println(sdf.format(dCell.getDate()));
    				// ==> 2088-04-22   
    				// reeplace = sdf.format(datecell.getDate());
    				// reeplace = Miscfunc.FechaAMD(datecell.getDate());

    				TimeZone gmtZone = TimeZone.getTimeZone("GMT");
    				sdf.setTimeZone(gmtZone);

    				reeplace = sdf.format(datecell.getDate());
    				System.out.println("Fecha encontrada=" + reeplace);
    			}else{ 
    				reeplace = cell.getContents();
    			}
    		}

    		System.out.println("Reemplazo=" + reeplace);

    		String ctxInfo = reeplace;	// get context
    		if (ctxInfo.length() == 0)
    		{
    			System.out.println("No Reeplace value = for: " + token);
    			if (!ignoreUnparsable)
    				return "";						//	token not found
    		}
    		else{
    			//if(token.equals(value))
    			outStr.append(ctxInfo);				// replace context with Context
    		}
    		inStr = inStr.substring(j+1, inStr.length());	// from second #
    		i = inStr.indexOf('$');
    	}
    	outStr.append(inStr);						// add the rest of the string

    	return outStr.toString();
    }	//	parseContext
    
}
