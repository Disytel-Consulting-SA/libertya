package org.openXpertya.process;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.POInfo;
import org.openXpertya.util.DB;

/**
 * 
 * Regenera los registros en la tabla _acct especificado como parámetro.
 * La combinación de cuentas base a utilizar será la especificada en C_AcctSchema_Default
 * 
 * @author fcristina 
 * 
 * --------- PENDIENTES -----------
 * TODO : 	Para los casos C_BP_Customer_Acct, C_BP_Vendor_Acct, M_Product_Acct también utiliza
 * 			C_AcctSchema_Default pero en realidad deberia utilizar C_BP_GROUP_ACCT y M_PRODUCT_CATEGORY_ACCT.
 */


public class UpdateAcctTablesProcess extends SvrProcess {

	// tabla de cuentas a ser actualizada
	private int AD_Table_ID = -1;
	
	// compañía 
	private int AD_Client_ID = -1;
	
	// nombre de tabla de cuentas a ser actualizada
	private String tableName = "";
	
	// tablas de cuentas (ej. M_Product_Acct) y su respectiva tabla de datos (ej. M_Product)
	private HashMap<String, String> acctTables = new HashMap<String, String>();
	
	// busqueda de tabla referencial para determinar la combinacion de cuentas default
	private HashMap<String, String> refTables = new HashMap<String, String>();
		
	
	@Override
	protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        // deteminar la tabla base que será actualizada 
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Table_ID" )) {
            	AD_Table_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_Client_ID" )) {
            	AD_Client_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
        
        // asignar tabla a obtener la combinacion de cuentas default
        refTables.put("C_BP_EMPLOYEE_ACCT", "C_BP_GROUP_ACCT");
        refTables.put("C_BP_CUSTOMER_ACCT", "C_BP_GROUP_ACCT");
        refTables.put("C_BP_VENDOR_ACCT", "C_BP_GROUP_ACCT");
        refTables.put("M_PRODUCT_ACCT", "M_PRODUCT_CATEGORY_ACCT");
        refTables.put("OTHERS", "C_ACCTSCHEMA_DEFAULT");
        
        // asignar tablas de datos a partir de tablas de cuentas
        acctTables.put("M_PRODUCT_ACCT", "M_PRODUCT");
        acctTables.put("C_TAX_ACCT", "C_TAX");
        acctTables.put("C_BANKACCOUNT_ACCT", "C_BANKACCOUNT");
        acctTables.put("C_BP_EMPLOYEE_ACCT", "C_BPARTNER");
        acctTables.put("C_BP_CUSTOMER_ACCT", "C_BPARTNER");
        acctTables.put("C_BP_VENDOR_ACCT", "C_BPARTNER");
        acctTables.put("C_BP_GROUP_ACCT", "C_BP_GROUP");
        acctTables.put("C_CASHBOOK_ACCT", "C_CASHBOOK");
        acctTables.put("C_CHARGE_ACCT", "C_CHARGE");
        acctTables.put("M_PRODUCT_CATEGORY_ACCT", "M_PRODUCT_CATEGORY");
        acctTables.put("C_PROJECT_ACCT", "C_PROJECT");
        acctTables.put("M_WAREHOUSE_ACCT", "M_WAREHOUSE");
        acctTables.put("C_WITHHOLDING_ACCT", "C_WITHHOLDING");
	}
	
	@Override
	protected String doIt() throws Exception {
		
		// setear tableName de cuentas a actualizar
		getTableName();
		
		// eliminar tuplas existentes en tabla de cuentas
		deteleTableRows();
	
		// incorporar los nuevos datos de cuentas
		insertNewRows();
		
		return "@El proceso se ejecuto correctamente@";
	}
	
	private void getTableName()
	{
		try 
		{
			PreparedStatement pstmt = DB.prepareStatement("SELECT tablename FROM AD_TABLE WHERE AD_TABLE_ID = " + AD_Table_ID, null);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				tableName = rs.getString(1);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Error obteniendo tablename");
		}
	}

	private void deteleTableRows()
	{
		// Eliminar todas las tuplas de la tabla _acct
		StringBuffer sql = new StringBuffer ("DELETE FROM " + tableName + " WHERE AD_CLIENT_ID = " + AD_Client_ID);
		// Si la tabla es la de contabilidad de productos, ignorar los configurados manualmente
		if (tableName.equalsIgnoreCase("M_PRODUCT_ACCT"))
			sql.append(" AND isManual = 'N'");
		DB.executeUpdate(sql.toString());

	}
	
	private void insertNewRows()
	{
		try 
		{
			// obtener la tabla de datos (por ejemplo M_Product) a partir de la tabla de cuentas (por ejemplo M_Product_Acct)
			String dataTable = acctTables.get(tableName.toUpperCase());
			if (tableName == null || tableName.length() == 0 || dataTable == null)
				throw new NoSuchElementException("No se pudo encontrar la tabla");
			
			// obtener las tuplas de la tabla de datos que requieren su actualizacion en tabla _acct
			PreparedStatement pstmt = DB.prepareStatement("SELECT * FROM " + dataTable + " WHERE AD_CLIENT_ID = " + AD_Client_ID, null);
			ResultSet rs = pstmt.executeQuery();
			 
			// obtener una instancia de PO a fin de acceder a su actualizacion
			M_Table table = new M_Table(getCtx(), M_Table.getID(dataTable) , null);
			Class<?> clazz = M_Table.getClass(table.getTableName());

			// parametros para instanciacion
			Class<?>[] paramTypes = { Properties.class, ResultSet.class, String.class };
			Object[] args = { getCtx(), rs, null };			
			Constructor<?> cons = clazz.getConstructor(paramTypes);
			
			// recorrer el listado de tuplas
			while (rs.next())
			{
				// instanciar el objeto y guardar
				Object theObject = cons.newInstance(args);				
				// Se discrimina por caso especial los productos, debido a que pueden dependen de la subfamilia
				// Similar para los proveedores, clientes y empleados
				// TODO: mejorar este ArrowAntiPattern!
				if (tableName.equalsIgnoreCase("M_PRODUCT_ACCT"))
					((MProduct)theObject).insert_Accounting(tableName, refTables.get("M_PRODUCT_ACCT"), "p.M_Product_Category_ID=" + ((MProduct)theObject).getM_Product_Category_ID());
				else if (tableName.equalsIgnoreCase("C_BP_EMPLOYEE_ACCT"))
					((MBPartner)theObject).insert_Accounting(tableName, refTables.get("C_BP_EMPLOYEE_ACCT"), "p.C_BP_Group_ID=" + ((MBPartner)theObject).getC_BP_Group_ID());
				else if (tableName.equalsIgnoreCase("C_BP_CUSTOMER_ACCT"))
					((MBPartner)theObject).insert_Accounting(tableName, refTables.get("C_BP_CUSTOMER_ACCT"), "p.C_BP_Group_ID=" + ((MBPartner)theObject).getC_BP_Group_ID());
				else if (tableName.equalsIgnoreCase("C_BP_VENDOR_ACCT"))
					((MBPartner)theObject).insert_Accounting(tableName, refTables.get("C_BP_VENDOR_ACCT"), "p.C_BP_Group_ID=" + ((MBPartner)theObject).getC_BP_Group_ID());				
				else
					((PO)theObject).insert_Accounting(tableName, refTables.get("OTHERS"), null);    
			}
			
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Error insertando las filas: " + e.getMessage());
		}
	}
	
}
