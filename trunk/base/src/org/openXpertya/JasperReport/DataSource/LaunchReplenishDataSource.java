package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import org.openXpertya.replenish.Replenish;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class LaunchReplenishDataSource implements JRDataSource {
	
	/** Variables */
	private int warehouse_id = 0;		// Almacén
	private int user_id = 0; 			// Responsable
	private boolean envia_mail = false; // Enviar informe por mail
	private HashSet responsables = new HashSet(); 
	
	/** Log	*/
	private CLogger log = CLogger.getCLogger( getClass());
	
	/** Ctx */
	
	private Properties  m_ctx = Env.getCtx();
	
	/** Registro Actual */
	private int m_currentRecord = -1; 	// -1 porque lo primero que se hace es un ++
	private M_LaunchReplenish[] m_reportLines;
	

	/** 
	 *  Constructor
	 *  
	 * @param warehouse
	 * @param mail
	 * @param user
	 * 
	 */
	 
	public LaunchReplenishDataSource(int warehouse, boolean mail, int user){
		warehouse_id = warehouse;
		envia_mail = mail;
		user_id = user;
		
		loadData();
	}
	
	/**
	 *  Métodos de interface
	 */
	
	public boolean next() throws JRException {
		m_currentRecord++;
		
		if (m_currentRecord >= m_reportLines.length )	{
			return false;
		}
		return true;
	}
	

	public Object getFieldValue(JRField jrf) throws JRException {
		String name =jrf.getName();
		
		if (name.toUpperCase().equals("WAREHOUSE"))	{
			return m_reportLines[m_currentRecord].getWarehouse();
		}
		else if (name.toUpperCase().equals("DESCRIPTION"))	{
			return m_reportLines[m_currentRecord].getDescription();
		}
		else if (name.toUpperCase().equals("PRODUCT"))	{
			return m_reportLines[m_currentRecord].getProduct();
		}
		else if (name.toUpperCase().equals("USER"))	{
			return m_reportLines[m_currentRecord].getUser();
		}
		else if (name.toUpperCase().equals("T"))	{
			return m_reportLines[m_currentRecord].getT();
		}
		else if (name.toUpperCase().equals("R"))	{
			return m_reportLines[m_currentRecord].getR();
		}		
		else if (name.toUpperCase().equals("SS"))	{
			return m_reportLines[m_currentRecord].getSS();
		}
		else if (name.toUpperCase().equals("CP"))	{
			return m_reportLines[m_currentRecord].getCP();
		}		
		else if (name.toUpperCase().equals("CS"))	{
			return m_reportLines[m_currentRecord].getCS();
		}
		else if (name.toUpperCase().equals("QTY"))	{
			return m_reportLines[m_currentRecord].getQty();
		}
		else if (name.toUpperCase().equals("UNIT"))	{
			return m_reportLines[m_currentRecord].getUnit();
		}
		else if (name.toUpperCase().equals("STOCK"))	{
			return m_reportLines[m_currentRecord].getStock();
		}
		else {
			throw new JRException("No se ha podido obtener el valor de la columna " + name);
		}
	}
	
	/**
	 *  Funcionalidad
	 */

	@SuppressWarnings("unchecked")
	private void loadData() {
		StringBuffer sql = new StringBuffer(" SELECT mr.m_product_id, mr.m_warehouse_id, mr.ad_client_id, mr.ad_org_id, mr.isactive, " +
											"       mr.created, mr.createdby, mr.updated, mr.updatedby, mr.replenishtype, mr.level_min, " +
											"       mr.level_max, mr.parametro1, mr.parametro2, mr.parametro3, mr.processed, mr.m_replenishsystem_id, " +
											"       mr.t, mr.r, mr.ss, mr.cp, mr.cs," +
											" 		wh.name as warehouse, mp.name as product, mrs.name as description, mrs.ad_user_id, usr.name as user , unt.name as unit," +
											"       bomQtyAvailable(mr.m_product_id, ?,0) as stock" +
											" FROM m_replenish mr " +
											"     LEFT JOIN (SELECT m_replenishsystem_id, name, ad_user_id FROM m_replenishsystem) mrs  ON mrs.m_replenishsystem_id = mr.m_replenishsystem_id " +
											"     LEFT JOIN (SELECT m_product_id, name, c_uom_id FROM m_product ) mp ON mp.m_product_id = mr.m_product_id " +
											"     LEFT JOIN (SELECT m_warehouse_id, name FROM m_warehouse) wh ON wh.m_warehouse_id = mr.m_warehouse_id " +
											"     LEFT JOIN (SELECT ad_user_id, name FROM ad_user) usr ON usr.ad_user_id = mrs.ad_user_id" +
											"     LEFT JOIN (SELECT c_uom_id, name FROM c_uom) as unt ON unt.c_uom_id = mp.c_uom_id"+
											" WHERE mr.ad_client_id = ? AND (mr.m_warehouse_id = ?  OR  0 = ? )AND (mrs.ad_user_id = ? OR 0 = ?)" +
											"      AND mr.replenishtype in ('5')" + 
											"      AND mr.m_replenishsystem_id IS NOT NULL");
				
		// ArrayList donde guardaremos los datos del informe
		ArrayList list = new ArrayList();
		
		try {
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			
			pstmt.setInt(1, warehouse_id);
			pstmt.setInt(2, Env.getAD_Client_ID(m_ctx));
			pstmt.setInt(3, warehouse_id);
			pstmt.setInt(4, warehouse_id);
			pstmt.setInt(5, user_id);
			pstmt.setInt(6, user_id);
			
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next())	{
				Replenish reple = new Replenish(m_ctx, rs, null);
				if(reple.needReplenish()){
					M_LaunchReplenish line = new M_LaunchReplenish(rs, reple.getQtyToOrder());
					list.add(line); 
					getResponsables().add(rs.getInt("ad_user_id"));
				}
			}
					
		}
		catch (SQLException e)	{
			throw new RuntimeException("No se puede ejecutar la consulta para crear las lineas del informe.");
		}
		
		// Guardamos la lista en m_reportLines
		m_reportLines = new M_LaunchReplenish[list.size()];
		list.toArray(m_reportLines);		
	}

	public HashSet getResponsables() {
		return responsables;
	}

}

class M_LaunchReplenish {
	/**
	 *  Variables
	 */
	
	private String Warehouse;
	private String Description;
	private BigDecimal T;
	private BigDecimal R;
	private BigDecimal SS;
	private BigDecimal CP;
	private BigDecimal CS;
	private String User;
	private String Product;
	private BigDecimal qty = Env.ZERO;
	private String Unit;
	private BigDecimal Stock = Env.ZERO;
	
	
	/**
	 * Constructor
	 * @param warehouse
	 * @param value
	 * @param description
	 * @param t
	 * @param r
	 * @param ss
	 * @param cp
	 * @param cs
	 * 
	 */	
	public M_LaunchReplenish(String warehouse,
							String value, 
							String description, 
							BigDecimal t, 
							BigDecimal r,
							BigDecimal ss, 
							BigDecimal cp, 
							BigDecimal cs){
		
		super();
		setWarehouse(warehouse);
		setDescription(description);
		setCP(t);
		setCP(r);
		setCP(ss);
		setCP(cp);
		setCS(cs); 		
		
	}


	public M_LaunchReplenish(ResultSet rs,BigDecimal cant) {
		try {
			Warehouse = rs.getString("warehouse");
			setUser(rs.getString("user"));
			setProduct(rs.getString("product"));
			Description = rs.getString("description");
			T = rs.getBigDecimal("T");
			R = rs.getBigDecimal("R");
			SS = rs.getBigDecimal("SS");
			CP = rs.getBigDecimal("CP");
			CS = rs.getBigDecimal("CS");
			setUnit(rs.getString("unit"));
			qty = cant;
		}
		catch (SQLException e)	{
			
		}	
	}


	private void setWarehouse(String warehouse) {
		Warehouse = warehouse;
	}

	public void setCP(BigDecimal cp) {
		CP = cp;
	}

	public BigDecimal getCP() {
		return CP;
	}

	public void setCS(BigDecimal cs) {
		CS = cs;
	}

	public BigDecimal getCS() {
		return CS;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getDescription() {
		return Description;
	}

	public BigDecimal getR() {
		return R;
	}

	public BigDecimal getSS() {
		return SS;
	}

	public BigDecimal getT() {
		return T;
	}


	public String getWarehouse() {
		return Warehouse;
	}


	public void setUser(String user) {
		User = user;
	}


	public String getUser() {
		return User;
	}


	public void setProduct(String product) {
		Product = product;
	}


	public String getProduct() {
		return Product;
	}


	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}


	public BigDecimal getQty() {
		return qty;
	}


	public void setUnit(String unit) {
		Unit = unit;
	}


	public String getUnit() {
		return Unit;
	}


	public void setStock(BigDecimal stock) {
		Stock = stock;
	}


	public BigDecimal getStock() {
		return Stock;
	}

}
