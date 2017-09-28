package org.openXpertya.JasperReport.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.util.DB;

/*
 * *********************************************************** /
 * OPDataSource: Clase que contiene la funcionalidad común a todos los
 * DataSource de los subreportes del reporte.
 * ***********************************************************
 */
abstract class OPDataSource implements JRDataSource {
	/** Lineas del informe */
	private Object[] m_reportLines;
	/** Registro Actual */
	private int m_currentRecord = -1; // -1 porque lo primero que se hace es
										// un ++
	
	private OrdenPagoDataSource ordenPagoDS;

	public boolean next() throws JRException {
		m_currentRecord++;
		if (m_currentRecord >= getM_reportLines().length)
			return false;

		return true;
	}

	public Object getFieldValue(JRField jrf) throws JRException {
		return getFieldValue(jrf.getName(), getM_reportLines()[m_currentRecord]);
	}

	protected abstract Object getFieldValue(String name, Object record)
			throws JRException;

	protected abstract String getDataSQL();

	protected abstract Object createRecord(ResultSet rs) throws SQLException;

	protected abstract void setQueryParameters(PreparedStatement pstmt)
			throws SQLException;

	public void loadData() {

		// ArrayList donde se guardan los datos del informe.
		ArrayList<Object> list = new ArrayList<Object>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(getDataSQL());
			setQueryParameters(pstmt);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Object line = createRecord(rs);
				list.add(line);
			}
		} catch (SQLException e) {
			throw new RuntimeException(
					"No se puede ejecutar la consulta para crear las lineas del informe.");
		} finally{
			try {
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		// Se guarda la lista de líneas en el arreglo de líneas del reporte.
		setM_reportLines(new Object[list.size()]);
		list.toArray(getM_reportLines());
	}
	
	public void setOrdenPagoDataSource(OrdenPagoDataSource opds){
		ordenPagoDS = opds;
	}
	
	public OrdenPagoDataSource getOrdenPagoDataSource(){
		return ordenPagoDS;
	}

	public Object[] getM_reportLines() {
		return m_reportLines;
	}

	public void setM_reportLines(Object[] m_reportLines) {
		this.m_reportLines = m_reportLines;
	}

}
