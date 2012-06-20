package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.CLogger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class DiarioMayorJasperDataSource implements JRDataSource {

	
	/** Cuenta inicio				*/
	private int			p_1_ElementValue_ID = 0;
	/** Cuenta Fin			*/
	private int			p_2_ElementValue_ID = 0;
	/** Date Acct From			*/
	private Timestamp	p_DateAcct_From = null;
	/** Date Acct To			*/
	private Timestamp	p_DateAcct_To = null;
	
	/**	Properties				*/
	private Properties p_ctx = null;
	
	/** Log	*/
	protected CLogger log = CLogger.getCLogger( getClass());
	
	
	/** Lineas del informe		*/
	private M_DiarioMayor[] m_reportLines;
		
	/** Registro Actual */
	private int m_currentRecord = -1; // -1 porque lo primero que se hace es un ++
	
	/** Saldos de las cuentas	*/
	HashMap m_saldos;
	
	
	public DiarioMayorJasperDataSource (Properties ctx, Timestamp dateFrom, Timestamp dateTo, int elementFrom_ID, int elementTo_ID)	{
		p_ctx = ctx;
		
		p_DateAcct_From = dateFrom;
		p_DateAcct_To = dateTo;
		
		p_1_ElementValue_ID = elementFrom_ID;
		p_2_ElementValue_ID = elementTo_ID;
		loadData();
	}
	
	/** 
	 * Obtiene la SQL para obtener los datos de los movimientos
	 * @return
	 */
	private String  getSQLData()	{
		StringBuffer sql = new StringBuffer();
		sql.append( " SELECT C_ElementValue_ID, Name, Value, JournalNo, DateAcct, DateDoc, Description, Debe, Haber FROM v_diariomayor WHERE " );
        
		// Añadimos restricciones
		sql.append( " AD_Client_ID=").append(Env.getAD_Client_ID(p_ctx));
		sql.append(" AND DateAcct BETWEEN ").append(DB.TO_DATE(p_DateAcct_From)).append(" AND ").append(DB.TO_DATE(p_DateAcct_To));
		
		
		// Obtenemos la clausula de las cuentas basandonos en el nombre.
		if (p_1_ElementValue_ID > 0  && p_2_ElementValue_ID > 0)	{

			String ev1_rest = "(select name from c_elementvalue where c_elementvalue_id="+ p_1_ElementValue_ID+")";			
			String ev2_rest = "(select name from c_elementvalue where c_elementvalue_id="+ p_2_ElementValue_ID+")";
			
			sql.append(" AND Name BETWEEN ").append(ev1_rest).append( " AND ").append(ev2_rest);
		}
		else if (p_1_ElementValue_ID > 0)	{
			sql.append("AND C_ElementValue_ID = ").append(p_1_ElementValue_ID);
		}
		
		
		// Orden
		sql.append( " ORDER BY Name, DateAcct, JournalNo");

		return  sql.toString();
	}
	
	/**
	 * Devuelve la SQL para obtener los saldos anteriores de las cuentas.
	 * @return
	 */
	private String getSQLSaldos()	{
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT ac.account_id as C_ElementValue_ID, ev.name, ev.value, sum(ac.amtacctdr) as debe, sum(ac.amtacctcr) as haber");
		sql.append("  FROM fact_acct ac, c_elementvalue ev");
		sql.append(" WHERE ac.account_id=ev.c_elementvalue_id ");
		sql.append(" AND ac.AD_Client_ID=").append(Env.getAD_Client_ID(p_ctx));
		sql.append(" AND ac.DateAcct < ").append(DB.TO_DATE(p_DateAcct_From));
		
		// Obtenemos la clausula de las cuentas basandonos en el nombre.
		if (p_1_ElementValue_ID > 0  && p_2_ElementValue_ID > 0)	{

			String ev1_rest = "(select name from c_elementvalue where c_elementvalue_id="+ p_1_ElementValue_ID+")";			
			String ev2_rest = "(select name from c_elementvalue where c_elementvalue_id="+ p_2_ElementValue_ID+")";
			
			sql.append(" AND ev.Name BETWEEN ").append(ev1_rest).append( " AND ").append(ev2_rest);
		}
		else if (p_1_ElementValue_ID > 0)	{
			sql.append(" AND ac.account_id = ").append(p_1_ElementValue_ID);
		}

		sql.append(" GROUP BY ac.account_id, ev.name, ev.value ");
		
		log.info("SQL: " + sql.toString());
		return sql.toString();
	}
	
	
	private  void loadSaldos()	{
		m_saldos = new HashMap();
		
		try	{
			PreparedStatement pstmt = DB.prepareStatement(getSQLSaldos());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())	{
				M_DiarioMayor line = new M_DiarioMayor(rs.getInt("C_ElementValue_ID"), rs.getString("Name"), rs.getString("Value"));
				
				BigDecimal debe = rs.getBigDecimal("Debe");
				BigDecimal haber = rs.getBigDecimal("Haber");
				BigDecimal saldo = debe.subtract(haber);
				line.setDescription("Saldos acumulados....");
				line.setDebe(debe);
				line.setHaber(haber);
				line.setSaldo(saldo);
				
				m_saldos.put(new Integer(rs.getInt("C_ElementValue_ID")), line);
			}
		}
		catch (SQLException e)	{
			log.severe("No se pueden cargar los saldos." + e.toString());
		}
	}
	
	
	
	public void loadData() throws RuntimeException {
		
		// Cargamos los saldos de las cuentas
		loadSaldos();
		int currentAccount = 0;
		
		// Cargamos los datos del informe
		ArrayList list = new ArrayList();
		
		try {
			PreparedStatement pstmt = DB.prepareStatement(getSQLData());
			ResultSet rs = pstmt.executeQuery();
			
			BigDecimal saldo = Env.ZERO;
			
			while (rs.next())	{
				M_DiarioMayor line = new M_DiarioMayor(rs);
				
				if (line.getC_ElementValue_ID() != currentAccount)	{
					currentAccount = line.getC_ElementValue_ID();
					saldo=Env.ZERO;
					
					if (m_saldos.containsKey(new Integer(line.getC_ElementValue_ID())))	{
						M_DiarioMayor lineaSaldo = (M_DiarioMayor)m_saldos.get(new Integer(line.getC_ElementValue_ID()));
					
						saldo = lineaSaldo.getSaldo();
						list.add(lineaSaldo);
					}
					
				}
				
				// Calculamos el saldo
				BigDecimal debe = line.getDebe();
				BigDecimal haber = line.getHaber();
				BigDecimal lineSaldo = debe.subtract(haber);
				saldo = saldo.add(lineSaldo);
				line.setSaldo(saldo);
				
				
				list.add(line);
			}
			
		}
		catch (SQLException e)	{
			throw new RuntimeException("No se puede ejecutar la consulta para crear las lineas del informe.");
		}
		
		// Guardamos la lista en m_reportLines
		m_reportLines = new M_DiarioMayor[list.size()];
		list.toArray(m_reportLines);
		
	}	
	
	
	public Object getFieldValue(JRField jrf) throws JRException {
		
		String name =jrf.getName(); 
		if (name.toUpperCase().equals("C_ELEMENTVALUE_ID"))	{
			return new BigDecimal(m_reportLines[m_currentRecord].getC_ElementValue_ID());
		}
		if (name.toUpperCase().equals("JOURNALNO"))	{
			return new BigDecimal(m_reportLines[m_currentRecord].getJournalNo());
		}
		else if (name.toUpperCase().equals("DATEACCT"))	{
			return m_reportLines[m_currentRecord].getDateAcct();
		}
		else if (name.toUpperCase().equals("DATEDOC"))	{
			return m_reportLines[m_currentRecord].getDateDoc();
		}
		else if (name.toUpperCase().equals("VALUE"))	{
			return m_reportLines[m_currentRecord].getValue();
		}
		else if (name.toUpperCase().equals("NAME"))	{
			return m_reportLines[m_currentRecord].getName();
		}
		else if (name.toUpperCase().equals("DESCRIPTION"))	{
			return m_reportLines[m_currentRecord].getDescription();
		}
		else if (name.toUpperCase().equals("DEBE"))	{
			return m_reportLines[m_currentRecord].getDebe();
		}
		else if (name.toUpperCase().equals("HABER"))	{
			return m_reportLines[m_currentRecord].getHaber();
		}
		else if (name.toUpperCase().equals("SALDO"))	{
			return m_reportLines[m_currentRecord].getSaldo();
		}
		else {
			throw new JRException("No se ha podidod obtener el valor de la columna " + name);
		}
	}

	/**
	 * Salta al siguiente registro del DataSource
	 */
	public boolean next() throws JRException {
		m_currentRecord++;
		
		if (m_currentRecord >= m_reportLines.length )	{
			return false;
		}
		
		return true;
	}

}


class M_DiarioMayor	{
	
	/** Log	*/
	protected CLogger log = CLogger.getCLogger( getClass());
	
	private int C_ElementValue_ID;
	private int JournalNo;
	private Timestamp DateAcct;
	private Timestamp DateDoc;
	private String Value;
	private String Name;
	private String Description;
	private BigDecimal Debe = Env.ZERO;
	private BigDecimal Haber = Env.ZERO;
	private BigDecimal Saldo = Env.ZERO;
	
	/**
	 * Constructor Estandard
	 * 
	 */
	
	public M_DiarioMayor(ResultSet rs)		{
		
		try {
			setC_ElementValue_ID(rs.getInt("C_ElementValue_ID"));
			setJournalNo(rs.getInt("JournalNo"));
			setDateAcct(rs.getTimestamp("DateAcct"));
			setDateDoc(rs.getTimestamp("DateDoc"));
			setValue(rs.getString("Value"));
			setName(rs.getString("Name"));
			setDescription(rs.getString("Description"));
			setDebe(rs.getBigDecimal("Debe"));
			setHaber(rs.getBigDecimal("Haber"));
			log.info("Creada la linea para la cuenta " + getName() + " Debe: " + getDebe() + " Haber: "+ getHaber() + " Saldo: " + getSaldo());
			
		}
		catch (SQLException e)	{
			log.severe("No se ha podido crear la linea de informe por problemas al crear el M_DiarioMayor." + e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor especial usado para crear las primeras lineas con los saldos acumulados.
	 * @param p_C_ElementValue_ID
	 * @param p_name
	 * @param p_value
	 */
	public M_DiarioMayor(int p_C_ElementValue_ID, String p_name, String p_value)	{
		setC_ElementValue_ID(p_C_ElementValue_ID);
		setValue(p_value);
		setName(p_name);
		setDebe(Env.ZERO);
		setHaber(Env.ZERO);
		setSaldo(Env.ZERO);
	}
	
	
	/**
	 * @return the c_ElementValue_ID
	 */
	public int getC_ElementValue_ID() {
		return C_ElementValue_ID;
	}
	/**
	 * @param elementValue_ID the c_ElementValue_ID to set
	 */
	public void setC_ElementValue_ID(int elementValue_ID) {
		C_ElementValue_ID = elementValue_ID;
	}
	/**
	 * @return the dateAcct
	 */
	public Timestamp getDateAcct() {
		return DateAcct;
	}
	/**
	 * @param dateAcct the dateAcct to set
	 */
	public void setDateAcct(Timestamp dateAcct) {
		DateAcct = dateAcct;
	}
	/**
	 * @return the dateDoc
	 */
	public Timestamp getDateDoc() {
		return DateDoc;
	}
	/**
	 * @param dateDoc the dateDoc to set
	 */
	public void setDateDoc(Timestamp dateDoc) {
		DateDoc = dateDoc;
	}
	/**
	 * @return the debe
	 */
	public BigDecimal getDebe() {
		return Debe;
	}
	/**
	 * @param debe the debe to set
	 */
	public void setDebe(BigDecimal debe) {
		Debe = debe;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
	}
	/**
	 * @return the haber
	 */
	public BigDecimal getHaber() {
		return Haber;
	}
	/**
	 * @param haber the haber to set
	 */
	public void setHaber(BigDecimal haber) {
		Haber = haber;
	}
	/**
	 * @return the journalNo
	 */
	public int getJournalNo() {
		return JournalNo;
	}
	/**
	 * @param journalNo the journalNo to set
	 */
	public void setJournalNo(int journalNo) {
		JournalNo = journalNo;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}
	/**
	 * @return the saldo
	 */
	public BigDecimal getSaldo() {
		return Saldo;
	}
	/**
	 * @param saldo the saldo to set
	 */
	public void setSaldo(BigDecimal saldo) {

		Saldo = saldo;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return Value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		Value = value;
	}
	
}


