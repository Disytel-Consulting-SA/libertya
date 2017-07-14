package org.openXpertya.process;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * 
 * Regenera los registros en la tabla _acct especificado como parámetro.
 * La combinación de cuentas base a utilizar será la especificada en C_AcctSchema_Default
 * 
 * @author fcristina 
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
	
	/** Contexto local */
	private Properties localCtx;
	
	public UpdateAcctTablesProcess(Properties ctx, String tableName){
		setLocalCtx(ctx);
		AD_Client_ID = Env.getAD_Client_ID(ctx);
		this.tableName = tableName;
	}
	
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
        
        initAcctTables();
	}
	
	private void initAcctTables(){
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
	
	
	public String mainDoIt() throws Exception {
		// Inicializar las tablas
		initAcctTables();
		System.out.println("=== Inicio de Proceso de Actualizacion de Tabla " + tableName + " === ");
		System.out.println("=== Eliminación de registros - " + Env.getTimestamp() + ".... === ");
		// Eliminar tuplas existentes en tabla de cuentas
		System.out.println("=== Registros Eliminados "+ deteleTableRows() +" === ");
		System.out.println("=== Insercion de registros - " + Env.getTimestamp() + ".... === ");
		// Incorporar los nuevos datos de cuentas
		System.out.println("=== Registros Insertados "+ insertNewRows() +" === ");
		System.out.println("=== Proceso finalizado correctamente - " + Env.getTimestamp() + " === ");
		return "";
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

	private int deteleTableRows()
	{
		// Eliminar todas las tuplas de la tabla _acct
		StringBuffer sql = new StringBuffer ("DELETE FROM " + tableName + " WHERE AD_CLIENT_ID = " + AD_Client_ID);
		// Si la tabla es la de contabilidad de productos, ignorar los configurados manualmente
		if (tableName.equalsIgnoreCase("M_PRODUCT_ACCT"))
			sql.append(" AND isManual = 'N'");
		return DB.executeUpdate(sql.toString());

	}
	
	private int insertNewRows()
	{
		int i = 0;
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
			boolean acctResult = false;
			// recorrer el listado de tuplas
			while (rs.next())
			{
				// instanciar el objeto y guardar
				Object theObject = cons.newInstance(args);				
				// Se discrimina por caso especial los productos, debido a que pueden dependen de la subfamilia
				// Similar para los proveedores, clientes y empleados
				// TODO: mejorar este ArrowAntiPattern!
				if (tableName.equalsIgnoreCase("M_PRODUCT_ACCT"))
					acctResult = ((MProduct)theObject).insert_Accounting(tableName, refTables.get("M_PRODUCT_ACCT"), "p.M_Product_Category_ID=" + ((MProduct)theObject).getM_Product_Category_ID());
				else if (tableName.equalsIgnoreCase("C_BP_EMPLOYEE_ACCT"))
					acctResult = ((MBPartner)theObject).insert_Accounting(tableName, refTables.get("C_BP_EMPLOYEE_ACCT"), "p.C_BP_Group_ID=" + ((MBPartner)theObject).getC_BP_Group_ID());
				else if (tableName.equalsIgnoreCase("C_BP_CUSTOMER_ACCT"))
					acctResult = ((MBPartner)theObject).insert_Accounting(tableName, refTables.get("C_BP_CUSTOMER_ACCT"), "p.C_BP_Group_ID=" + ((MBPartner)theObject).getC_BP_Group_ID());
				else if (tableName.equalsIgnoreCase("C_BP_VENDOR_ACCT"))
					acctResult = ((MBPartner)theObject).insert_Accounting(tableName, refTables.get("C_BP_VENDOR_ACCT"), "p.C_BP_Group_ID=" + ((MBPartner)theObject).getC_BP_Group_ID());				
				else
					acctResult = ((PO)theObject).insert_Accounting(tableName, refTables.get("OTHERS"), null);
				
				if(acctResult){
					i++;
				}
			}
			
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Error insertando las filas: " + e.getMessage());
		}
		return i;
	}
	
	/** Parámetro ID de ad_client */
	public static final String PARAM_CLIENT = "-c";
	/** Parámetro nombre de table acct */
	public static final String PARAM_TABLE = "-t";
	/** Parámetro ayuda */
	public static final String PARAM_HELP = "-h";
	
	public static void main(String args[]) {
		Integer clientID = 0;
		String table = "";
		for (String arg : args) {
			if (arg.toLowerCase().startsWith(PARAM_HELP))
				showHelp(" Ayuda: \n  " 
							+ PARAM_CLIENT 				+ " ID de la Compañía \n " 
							+ PARAM_TABLE 				+ " Nombre de tabla Acct a regenerar \n ");
			// ID de Compañía
			else if (arg.toLowerCase().startsWith(PARAM_CLIENT))
				clientID = Integer.parseInt(arg.substring(PARAM_CLIENT.length()));
			// Nombre de tabla acct
			else if (arg.toLowerCase().startsWith(PARAM_TABLE))
				table = arg.substring(PARAM_TABLE.length());
			else 
				System.out.println("WARNING: Argumento " + arg + " ignorado");
		}
		
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null)
	  		showHelp("ERROR: La variable de entorno OXP_HOME no está seteada ");

	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false ))
	  		showHelp("ERROR: Error al iniciar el ambiente cliente.  Revise la configuración");

	  	// Configuracion
	  	Env.setContext(Env.getCtx(), "#AD_User_ID", 0);
		Env.setContext(Env.getCtx(), "#AD_User_Name", "System");
	  	Env.setContext(Env.getCtx(), "#AD_Language", "es_AR");
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", clientID);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", 0);
		
		UpdateAcctTablesProcess uatp = new UpdateAcctTablesProcess(Env.getCtx(), table); 
	  	try {
	  		uatp.mainDoIt();
	  	}
	  	catch (Exception e) {
	  		e.printStackTrace();
	  	}
	}

	protected static void showHelp(String message) {
		System.out.println(message);
		System.exit(1);
	}

	private Properties getLocalCtx() {
		return localCtx;
	}

	private void setLocalCtx(Properties localCtx) {
		this.localCtx = localCtx;
	}
	
	@Override
	public Properties getCtx(){
		if(getLocalCtx() != null){
			return getLocalCtx();
		}
		return super.getCtx();
	}
	
}
