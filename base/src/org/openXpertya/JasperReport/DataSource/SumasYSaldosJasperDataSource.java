package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class SumasYSaldosJasperDataSource implements JRDataSource {

	/** Cuenta inicio				*/
	private int			p_1_ElementValue_ID = 0;
	/** Cuenta Fin			*/
	private int			p_2_ElementValue_ID = 0;
	/** Date Acct From			*/
	private Timestamp	p_DateAcct_From = null;
	/** Date Acct To			*/
	private Timestamp	p_DateAcct_To = null;
	
	/** C_Project_ID			*/
	private int 		p_C_Project_ID = 0;
	
	/** Digitos por lo que hay que agrupar */
	private int 		p_groupByDigits = 0;
	
	private boolean		groupAccounts = false;
	
	/** dREHER agrupa por proyectos */
	private boolean		p_groupByProjects = false;
	
	/**	Properties				*/
	private Properties p_ctx = null;
	
	/** Log	*/
	protected CLogger log = CLogger.getCLogger( getClass());
	
	
	/** Lineas del informe		*/
	private M_SumasYSaldos[] m_reportLines;
		
	/** Registro Actual */
	private int m_currentRecord = -1; // -1 porque lo primero que se hace es un ++
	
	/** Saldos de las cuentas	*/
	HashMap m_saldos;
	
	
	public SumasYSaldosJasperDataSource (Properties ctx, Timestamp dateFrom, Timestamp dateTo, int elementFrom_ID, int elementTo_ID, int grupByDigits, 
			int C_Project_ID, boolean p_groupbyProjects)	{
		p_ctx = ctx;
		
		p_DateAcct_From = dateFrom;
		p_DateAcct_To = dateTo;
		
		p_1_ElementValue_ID = elementFrom_ID;
		p_2_ElementValue_ID = elementTo_ID;
		p_groupByDigits = grupByDigits;

		// dREHER
		p_C_Project_ID = C_Project_ID;
		p_groupByProjects = p_groupbyProjects;
		
		loadData();
	}
	
		
	/**
	 * Obtiene la SQL para obtener los saldos de las cuentas, acumulados, o entre fechas. 
	 * @param SaldosAcumulados Si es true, devuelve al sql para los saldos acumulados
	 * @return
	 */
	private String  getSQLData(boolean SaldosAcumulados)	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		
		if (groupAccounts)	{
			sql.append((p_groupByProjects?"fa.c_project_id,pr.name as proyecto,":"") + // dREHER 
					" substr(ev.name, 0,4) as name ");
		}
		else {
			// Solo ponemos el concatenamos el campo value y name para que muestre el codigo de la cuenta tambien.
			sql.append((p_groupByProjects?"fa.c_project_id,pr.name as proyecto,":"") + // dREHER
					" fa.account_id, ev.value || ' ' || ev.name as name");
		}
		sql.append(" ,sum(fa.amtacctdr)  as debe, sum(fa.amtacctcr) as haber " +
		
		" FROM Fact_acct fa " +
		" LEFT JOIN C_ElementValue ev ON ev.C_ElementValue_ID=fa.Account_ID " + // dREHER
		
		" LEFT JOIN C_Project pr ON pr.C_Project_ID=fa.C_Project_ID " + // dREHER

		" WHERE fa.account_id=ev.c_elementvalue_id ");
        
		// Añadimos restricciones
		sql.append( " AND fa.AD_Client_ID=").append(Env.getAD_Client_ID(p_ctx));
		

		// Si solo queremos saldos acumulados, mostraremos el saldo antes de la fecha de inicio 
		if (SaldosAcumulados)	{
			sql.append(" AND fa.DateAcct::date < ").append(DB.TO_DATE(p_DateAcct_From));
		}
		
		// Si no, nos basamos en las fechas de los parametros
		else {
			sql.append(" AND fa.DateAcct::date BETWEEN ").append(DB.TO_DATE(p_DateAcct_From)).append(" AND ").append(DB.TO_DATE(p_DateAcct_To));
		}
		
		// Obtenemos la clausula de las cuentas basandonos en el nombre.
		if (p_1_ElementValue_ID > 0  && p_2_ElementValue_ID > 0)	{

			String ev1_rest = "(select name from c_elementvalue where c_elementvalue_id="+ p_1_ElementValue_ID+")";			
			String ev2_rest = "(select name from c_elementvalue where c_elementvalue_id="+ p_2_ElementValue_ID+")";
			
			sql.append(" AND ev.Name BETWEEN ").append(ev1_rest).append( " AND ").append(ev2_rest);
		}
		else if (p_1_ElementValue_ID > 0)	{
			sql.append("AND C_ElementValue_ID = ").append(p_1_ElementValue_ID);
		}
		
		// dREHER
		if(p_C_Project_ID > 0)
			sql.append(" AND C_Project_ID= ").append(p_C_Project_ID);
		
		
		if (groupAccounts)	{
			sql.append(" GROUP BY " + (p_groupByProjects?"fa.c_project_id,pr.name,":"") +  
					" rollup(substr(ev.name,0,4)) " +
					" ORDER BY " + 
					(p_groupByProjects?"fa.c_project_id,":"") + " name ");
		}
		else {
			sql.append(" GROUP BY " + (p_groupByProjects?"fa.c_project_id,pr.name,":"") + 
					" fa.account_id, ev.value, ev.name " +
					" ORDER BY " + 
					(p_groupByProjects?"fa.c_project_id,pr.name,":"") +" ev.value");
		}

		// dREHER
		return  sql.toString().replace("current_date()", "current_date");
	}
	
		
	
	private  void loadSaldos()	{
		m_saldos = new HashMap();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try	{
			pstmt = DB.prepareStatement(getSQLData(true), null, true);
			rs = pstmt.executeQuery();
			while (rs.next())	{
				BigDecimal saldoAcumulado = rs.getBigDecimal("Debe").subtract(rs.getBigDecimal("Haber"));
				M_SumasYSaldos line = new M_SumasYSaldos(rs.getString("Name"));
				
				BigDecimal debe = rs.getBigDecimal("Debe");
				BigDecimal haber = rs.getBigDecimal("Haber");
				BigDecimal saldo = debe.subtract(haber);
				line.setSaldoInicial(saldo);
				
				m_saldos.put(rs.getString("Name"), line);
			}
		}
		catch (SQLException e)	{
			log.severe("No se pueden cargar los saldos." + e.toString());
		}
		finally{ // dREHER cierre de conexiones controlado
			DB.close(rs, pstmt);
			rs=null; pstmt=null;
		}
	}
	
	
	
	public void loadData() throws RuntimeException {
		
		// Cargamos los saldos de las cuentas
		loadSaldos();
		
		
		// ArrayList donde guardaremos los datos del informe
		ArrayList list = new ArrayList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(getSQLData(false), null, true);
			rs = pstmt.executeQuery();
			
			BigDecimal saldo = Env.ZERO;
			while (rs.next())	{
				M_SumasYSaldos line = new M_SumasYSaldos(rs);
			
				// Si habiamos guardado una linea con los saldos iniciales
				if (m_saldos.containsKey(line.getName()))	{
					M_SumasYSaldos saldoInicial = (M_SumasYSaldos)m_saldos.get(line.getName());
					line.setSaldoInicial(saldoInicial.getSaldo());
				}
				list.add(line);
			}
					
		}
		catch (SQLException e)	{
			throw new RuntimeException("No se puede ejecutar la consulta para crear las lineas del informe.");
		}
		finally{ // dREHER cierre de conexiones controlado
			DB.close(rs, pstmt);
			rs=null; pstmt=null;
		}
		// Guardamos la lista en m_reportLines
		m_reportLines = new M_SumasYSaldos[list.size()];
		list.toArray(m_reportLines);
		
	}	
	
	
	public Object getFieldValue(JRField jrf) throws JRException {
		
		String name =jrf.getName();
		
		if (name.toUpperCase().equals("NAME"))	{
			return m_reportLines[m_currentRecord].getName();
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
		else if (name.toUpperCase().equals("SALDOINICIAL"))	{
			return m_reportLines[m_currentRecord].getSaldoInicial();
		}
		else if (name.toUpperCase().equals("PROYECTO"))	{
			return m_reportLines[m_currentRecord].getProyecto();
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

class M_SumasYSaldos	{
	
	private String Name;
	private BigDecimal Debe = Env.ZERO;
	private BigDecimal Haber = Env.ZERO;
	private BigDecimal SaldoInicial = Env.ZERO;
	private String Proyecto;
	
	/**
	 * Constructor
	 * @param name
	 * @param saldoInicial
	 * @param debe
	 * @param haber
	 */
	public M_SumasYSaldos(String name, BigDecimal saldoInicial, BigDecimal debe, BigDecimal haber, String proyecto) {
		super();
		Name = name;
		SaldoInicial = saldoInicial;
		Debe = debe;
		Haber = haber;
		Proyecto = proyecto;
	}
	
	
	
	/**
	 * Constructor
	 * @param name
	 */
	public M_SumasYSaldos(String name) {
		super();
		Name = name;
	}

	public M_SumasYSaldos (ResultSet rs)	{
		try {
			Name = rs.getString("Name");
			Debe = rs.getBigDecimal("Debe");
			Haber = rs.getBigDecimal("Haber");
			Proyecto = rs.getString("Proyecto");
		}
		catch (SQLException e)	{
			
		}	
	}
	
	/**
	 * Devuelve el saldo contandolo como saldoInicial + (debe - haber)
	 * @return
	 */
	public BigDecimal getSaldo()	{
		BigDecimal saldo = Debe.subtract(Haber);
		saldo = saldo.add(SaldoInicial);
		return saldo;
	}
	

	public BigDecimal getDebe() {
		return Debe;
	}
	public void setDebe(BigDecimal debe) {
		Debe = debe;
	}
	public BigDecimal getHaber() {
		return Haber;
	}
	public void setHaber(BigDecimal haber) {
		Haber = haber;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public BigDecimal getSaldoInicial() {
		return SaldoInicial;
	}
	public void setSaldoInicial(BigDecimal saldoInicial) {
		SaldoInicial = saldoInicial;
	}
	public String getProyecto() {
		return Proyecto;
	}
	public void setProyecto(String proyecto) {
		Proyecto = proyecto;
	}
	
	
}
