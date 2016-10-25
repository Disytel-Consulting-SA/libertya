/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */

package org.openXpertya.impexp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import org.openXpertya.model.Callout;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Env;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 * @version 2.2, 12.10.07
 * @author Equipo de Desarrollo de openXpertya
 */
public final class ImpFormatRow {

	/**
	 * Constructor de la clase.
	 * @param seqNo
	 * @param columnName
	 * @param startNo
	 * @param endNo
	 * @param dataType
	 * @param maxLength
	 */
	public ImpFormatRow(int seqNo, String columnName, int startNo, int endNo, String dataType, int maxLength) {
		m_seqNo = seqNo;
		setColumnName(columnName);
		m_startNo = startNo;
		m_endNo = endNo;
		setDataType(dataType);
		setMaxLength(maxLength);
	} // ImpFormatRow

	/**
	 * Constructor de la clase.
	 * @param seqNo
	 * @param columnName
	 * @param dataType
	 * @param maxLength
	 */
	public ImpFormatRow(int seqNo, String columnName, String dataType, int maxLength) {
		m_seqNo = seqNo;
		setColumnName(columnName);
		setDataType(dataType);
		setMaxLength(maxLength);
	} // ImpFormatRow

	/** Descripción de Campos */
	private int m_seqNo;

	/** Descripción de Campos */
	private String m_columnName;

	/** Descripción de Campos */
	private int m_startNo = 0;

	/** Descripción de Campos */
	private int m_endNo = 0;

	/** Descripción de Campos */
	private String m_dataType;

	/** Descripción de Campos */
	private String m_dataFormat = "";

	/** Descripción de Campos */
	private String m_decimalPoint = ".";

	/** Descripción de Campos */
	private boolean m_divideBy100 = false;

	/** Descripción de Campos */
	private String m_constantValue = "";

	/** Descripción de Campos */
	private boolean m_constantIsString = true;

	/** Descripción de Campos */
	private Callout m_callout = null;

	/** Descripción de Campos */
	private String m_method = null;

	/** Descripción de Campos */
	private SimpleDateFormat m_dformat = null;

	/** Descripción de Campos */
	private int m_maxLength = 0;

	/** Descripción de Campos */
	private CLogger log = CLogger.getCLogger(getClass());

	/**
	 * Descripción de Método
	 * @return
	 */
	public int getSeqNo() {
		return m_seqNo;
	} // getSeqNo

	/**
	 * Descripción de Método
	 * @param newSeqNo
	 */
	public void setSeqNo(int newSeqNo) {
		m_seqNo = newSeqNo;
	} // setSeqNo

	/**
	 * Descripción de Método
	 * @param newStartNo
	 */
	public void setStartNo(int newStartNo) {
		m_startNo = newStartNo;
	} // setStartNo

	/**
	 * Descripción de Método
	 * @return
	 */
	public int getStartNo() {
		return m_startNo;
	} // getStartNo

	/**
	 * Descripción de Método
	 * @param newEndNo
	 */
	public void setEndNo(int newEndNo) {
		m_endNo = newEndNo;
	} // setEndNo

	/**
	 * Descripción de Método
	 * @return
	 */
	public int getEndNo() {
		return m_endNo;
	} // getEndNo

	public String getDisplayName() {
		return Msg.getElement(Env.getCtx(), getColumnName());
	}

	/**
	 * Descripción de Método
	 * @param columnName
	 */
	public void setColumnName(String columnName) {
		if ((columnName == null) || (columnName.length() == 0)) {
			throw new IllegalArgumentException("ColumnName must be at least 1 char");
		} else {
			m_columnName = columnName;
		}
	} // setColumnName

	/**
	 * Descripción de Método
	 * @return
	 */
	public String getColumnName() {
		return m_columnName;
	} // getColumnName

	/**
	 * Descripción de Método
	 * @param dataType
	 */
	public void setDataType(String dataType) {
		if (dataType.equals(DATATYPE_String) || dataType.equals(DATATYPE_Date) || dataType.equals(DATATYPE_Number) || dataType.equals(DATATYPE_Constant)) {
			m_dataType = dataType;
		} else {
			throw new IllegalArgumentException("DataType must be S/D/N/C");
		}
	} // setDataType

	/** Descripción de Campos */
	public static final String DATATYPE_String = "S";

	/** Descripción de Campos */
	public static final String DATATYPE_Date = "D";

	/** Descripción de Campos */
	public static final String DATATYPE_Number = "N";

	/** Descripción de Campos */
	public static final String DATATYPE_Constant = "C";

	/**
	 * Descripción de Método
	 * @return
	 */
	public String getDataType() {
		return m_dataType;
	} // getDataType

	/**
	 * Descripción de Método
	 * @return
	 */
	public boolean isString() {
		if (m_dataType.equals(DATATYPE_Constant)) {
			return m_constantIsString;
		}

		return m_dataType.equals(DATATYPE_String);
	} // isString

	/**
	 * Descripción de Método
	 * @return
	 */
	public boolean isNumber() {
		return m_dataType.equals(DATATYPE_Number);
	}

	/**
	 * Descripción de Método
	 * @return
	 */
	public boolean isDate() {
		return m_dataType.equals(DATATYPE_Date);
	}

	/**
	 * Descripción de Método
	 * @return
	 */
	public boolean isConstant() {
		return m_dataType.equals(DATATYPE_Constant);
	}

	/**
	 * Descripción de Método
	 * @param dataFormat
	 * @param decimalPoint
	 * @param divideBy100
	 * @param constantValue
	 * @param callout
	 */
	public void setFormatInfo(String dataFormat, String decimalPoint, boolean divideBy100, String constantValue, String callout) {
		if (dataFormat == null) {
			m_dataFormat = "";
		} else {
			m_dataFormat = dataFormat;
		}

		// number

		if ((decimalPoint == null) || !decimalPoint.equals(",")) {
			m_decimalPoint = ".";
		} else {
			m_decimalPoint = ",";
		}

		m_divideBy100 = divideBy100;

		// constant

		if ((constantValue == null) || (constantValue.length() == 0) || !m_dataType.equals(DATATYPE_Constant)) {
			m_constantValue = "";
			m_constantIsString = true;
		} else {
			m_constantValue = constantValue;
			m_constantIsString = false;

			for (int i = 0; i < m_constantValue.length(); i++) {
				char c = m_constantValue.charAt(i);

				if (!(Character.isDigit(c) || (c == '.'))) // if a constant number, it must be with . (not ,)
				{
					m_constantIsString = true;

					break;
				}
			}
		}

		// callout

		if (callout != null) {
			int methodStart = callout.lastIndexOf(".");

			try {
				if (methodStart != -1) // no class
				{
					Class cClass = Class.forName(callout.substring(0, methodStart));

					m_callout = (Callout) cClass.newInstance();
					m_method = callout.substring(methodStart + 1);
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "MTab.setFormatInfo - " + e.toString());
			}

			if ((m_callout == null) || (m_method == null) || (m_method.length() == 0)) {
				log.log(Level.SEVERE, "MTab.setFormatInfo - Invalid Callout " + callout);
				m_callout = null;
			}
		}
	} // setFormatInfo

	/**
	 * Descripción de Método
	 * @return
	 */
	public String getDataFormat() {
		return m_dataFormat;
	}

	/**
	 * Descripción de Método
	 * @return
	 */
	public String getDecimalPoint() {
		return m_decimalPoint;
	}

	/**
	 * Descripción de Método
	 * @return
	 */
	public boolean isDivideBy100() {
		return m_divideBy100;
	}

	/**
	 * Descripción de Método
	 * @return
	 */
	public String getConstantValue() {
		return m_constantValue;
	}

	/**
	 * Descripción de Método
	 * @param maxLength
	 */
	public void setMaxLength(int maxLength) {
		m_maxLength = maxLength;
	} // setMaxLength

	/**
	 * Descripción de Método
	 * @param info
	 * @return
	 */
	public String parse(String info) {
		if ((info == null) || (info.length() == 0)) {
			return "";
		}

		// Comment ?

		if (info.startsWith("[") && info.endsWith("]")) {
			return "";
		}

		//

		String retValue = null;

		if (isNumber()) {
			retValue = parseNumber(info);
		} else if (isDate()) {
			retValue = parseDate(info);
		} else if (isConstant()) {
			retValue = m_constantIsString ? parseString(m_constantValue) : m_constantValue;
		} else {
			retValue = parseString(info);
		}

		//

		if (m_callout != null) {
			try {
				retValue = m_callout.convert(m_method, retValue);
			} catch (Exception e) {
				log.log(Level.SEVERE, "ImpFormatRow.parse - " + info + " (" + retValue + ")", e);
			}
		}

		//

		if (retValue == null) {
			retValue = "";
		}

		return retValue.trim();
	} // parse

	/**
	 * Descripción de Método
	 * @param info
	 * @return
	 */
	private String parseDate(String info) {
		if (m_dformat == null) {
			try {
				m_dformat = new SimpleDateFormat(m_dataFormat);
			} catch (Exception e) {
				log.log(Level.SEVERE, "ImpFormatRow.parseDate Format=" + m_dataFormat, e);
			}

			if (m_dformat == null) {
				m_dformat = (SimpleDateFormat) DateFormat.getDateInstance();
			}

			m_dformat.setLenient(true);
		}

		Timestamp ts = null;

		try {
			ts = new Timestamp(m_dformat.parse(info).getTime());
		} catch (ParseException pe) {
			log.log(Level.SEVERE, "ImpFormatRow.parseDate - " + info, pe);
		}

		if (ts == null) {
			ts = new Timestamp(System.currentTimeMillis());
		}

		//

		String dateString = ts.toString();

		return dateString.substring(0, dateString.indexOf(".")); // cut off miliseconds
	} // parseNumber

	/**
	 * Descripción de Método
	 * @param info
	 * @return
	 */
	private String parseString(String info) {
		String retValue = info;

		// Length restriction

		if ((m_maxLength > 0) && (retValue.length() > m_maxLength)) {
			retValue = retValue.substring(0, m_maxLength);
		}

		// copy characters (wee need to look through anyway)

		StringBuffer out = new StringBuffer(retValue.length());

		for (int i = 0; i < retValue.length(); i++) {
			char c = retValue.charAt(i);

			if (c == '\'') {
				out.append("''");
			} else if (c == '\\') {
				out.append("\\\\");
			} else {
				out.append(c);
			}
		}

		return out.toString();
	} // parseString

	/**
	 * Descripción de Método
	 * @param info
	 * @return
	 */
	private String parseNumber(String info) {
		boolean hasPoint = info.indexOf(".") != -1;
		boolean hasComma = info.indexOf(",") != -1;

		// delete thousands

		if (hasComma && m_decimalPoint.equals(".")) {
			info = info.replace(',', ' ');
		}

		if (hasPoint && m_decimalPoint.equals(",")) {
			info = info.replace('.', ' ');
		}

		hasComma = info.indexOf(",") != -1;

		// replace decimal

		if (hasComma && m_decimalPoint.equals(",")) {
			info = info.replace(',', '.');
		}

		// remove everything but digits & '.'

		char[] charArray = info.toCharArray();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < charArray.length; i++) {
			if (Character.isDigit(charArray[i]) || (charArray[i] == '.')) {
				sb.append(charArray[i]);
			}
		}

		if (sb.length() == 0) {
			return "0";
		}

		BigDecimal bd = new BigDecimal(sb.toString());

		// Si es negativo, se debe negar
		boolean negative = charArray[0] == '-';
		bd = negative ? bd.negate() : bd;

		if (m_divideBy100) { // assumed two decimal scale
			bd = bd.divide(new BigDecimal(100.0), 2, BigDecimal.ROUND_HALF_UP);
		}

		return bd.toString();
	} // parseNumber

} // ImpFormatFow

/*
 * @(#)ImpFormatRow.java 02.07.07
 * Fin del fichero ImpFormatRow.java
 * Versión 2.2
 */
