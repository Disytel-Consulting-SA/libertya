package org.openXpertya.process.release;

import java.util.logging.Level;

import org.openXpertya.model.MWindowAccess;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_Process_Access;
import org.openXpertya.model.X_AD_Window_Access;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;

/**
 * Upgrade de versión 10.02 a 10.03, específicamente para usuarios que iniciaron con 
 * libertya 9.10 y que realizan el UPDATE a 10.02 para posteriormente realizar el UPGRADE a 10.03
 * 
 * @author fcristina
 */

public class PostInstallCoreRelease1003 extends PluginPostInstallProcess {

	/** CONSTANTES */
	// Compania y Organizacion Libertya de la version 09.10
	static final int CLIENT 									= 1000010;
	static final int ORG 										= 1000047;
	// Perfiles	de la version 09.10
	static final int ROLE_GESTION_ALMACENES 					= 1010065;
	static final int ROLE_VENTAS 								= 1010064;
	static final int ROLE_ADMINISTRACION						= 1010069;
	// Ventanas	
	static final int WINDOW_PRODUCT_SPLITTING 					= 1010091; 
	static final int WINDOW_PRODUCT_CHANGE 	  					= 1010096;
	static final int WINDOW_MATERIAL_TRANSFER 					= 1010099;
	static int 		 WINDOW_WAREHOUSE_CLOSURE 					= -1;
	static int 		 WINDOW_TABLA_DE_REFERENCIAS 				= -1;
	static int 		 WINDOW_FORMAT_ARCHIVOS_DUPLICADOS 			= -1;
	// Procesos
	static final int PROCESS_M_SPLITTING_PROCESS 				= 1010197;
	static final int PROCESS_M_PRODUCTCHANGE_PROCESS 			= 1010202;
	static final int PROCESS_M_TRANSFER_PROCESS 				= 1010206;
	static int 		 PROCESS_M_WAREHOUSE_CLOSE_PROCESS  		= -1;
	static int 		 PROCESS_EXPORT_ELECTRONIC_INVOICE_TO_TABLE = -1;
	static int 		 PROCESS_EXPORT_ELECTRONIC_INVOICE_TO_FILE 	= -1;
	
	// Flujo de Trabajo
	static final int WORKFLOW_PROCESS_SPLITTING 				= 1010059;
	static final int WORKFLOW_PROCESS_PRODUCT_CHANGE 			= 1010064;
	static final int WORKFLOW_PROCESS_TRANSFER		 			= 1010066;
	static 		 int WORKFLOW_PROCESS_WAREHOUSE_CLOSE 			= -1;
	// Formularios
	static 		 int FORM_FISCAL_PRINTER_CONTROL_PANEL 			= -1;
	
	@Override
	protected String doIt() throws Exception {

		super.doIt();
		
		/** Obtener los IDs de los nuevos registros (inserciones posteriores a 1002) */
		PROCESS_M_WAREHOUSE_CLOSE_PROCESS 			= DB.getSQLValue(get_TrxName(), " SELECT AD_Process_ID From AD_Process WHERE Value = '" + "M_Warehouse_Close_Process" + "'" );
		PROCESS_EXPORT_ELECTRONIC_INVOICE_TO_TABLE 	= DB.getSQLValue(get_TrxName(), " SELECT AD_Process_ID From AD_Process WHERE Value = '" + "ExportElectronicInvoiceToTables" + "'" );
		PROCESS_EXPORT_ELECTRONIC_INVOICE_TO_FILE  	= DB.getSQLValue(get_TrxName(), " SELECT AD_Process_ID From AD_Process WHERE Value = '" + "ExportElectronicInvoiceToFile" + "'" );
		
		WINDOW_WAREHOUSE_CLOSURE  					= DB.getSQLValue(get_TrxName(), " SELECT AD_Window_ID From AD_Window WHERE Name = '" + "Warehouse Closures" + "'" );
		WINDOW_TABLA_DE_REFERENCIAS 				= DB.getSQLValue(get_TrxName(), " SELECT AD_Window_ID From AD_Window WHERE Name = '" + "Tabla de referencias" + "'" );
		WINDOW_FORMAT_ARCHIVOS_DUPLICADOS 			= DB.getSQLValue(get_TrxName(), " SELECT AD_Window_ID From AD_Window WHERE Name = '" + "Formato archivos duplicado electrónicos" + "'" );
		
		WORKFLOW_PROCESS_WAREHOUSE_CLOSE 			= DB.getSQLValue(get_TrxName(), " SELECT AD_Workflow_ID From AD_Workflow WHERE Name = '" + "Warehouse Close" + "'" ); 

		FORM_FISCAL_PRINTER_CONTROL_PANEL 			= DB.getSQLValue(get_TrxName(), " SELECT AD_Form_ID From AD_Form WHERE Name = '" + "Fiscal Printers Control Panel" + "'" );  
		
		/** Inserciones en ad_window_access */
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, WINDOW_PRODUCT_SPLITTING, "AD_Window_ID", "AD_Window_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, WINDOW_PRODUCT_CHANGE, "AD_Window_ID", "AD_Window_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, WINDOW_WAREHOUSE_CLOSURE, "AD_Window_ID", "AD_Window_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, WINDOW_MATERIAL_TRANSFER, "AD_Window_ID", "AD_Window_Access");
		insertAccess(CLIENT, ORG, ROLE_ADMINISTRACION, WINDOW_TABLA_DE_REFERENCIAS, "AD_Window_ID", "AD_Window_Access");
		insertAccess(CLIENT, ORG, ROLE_ADMINISTRACION, WINDOW_FORMAT_ARCHIVOS_DUPLICADOS, "AD_Window_ID", "AD_Window_Access");

		/** Inserciones en ad_process_access */
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, PROCESS_M_SPLITTING_PROCESS, "AD_Process_ID", "AD_Process_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, PROCESS_M_PRODUCTCHANGE_PROCESS, "AD_Process_ID", "AD_Process_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, PROCESS_M_TRANSFER_PROCESS, "AD_Process_ID", "AD_Process_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, PROCESS_M_WAREHOUSE_CLOSE_PROCESS, "AD_Process_ID", "AD_Process_Access");
		insertAccess(CLIENT, ORG, ROLE_ADMINISTRACION, PROCESS_EXPORT_ELECTRONIC_INVOICE_TO_TABLE, "AD_Process_ID", "AD_Process_Access");
		insertAccess(CLIENT, ORG, ROLE_ADMINISTRACION, PROCESS_EXPORT_ELECTRONIC_INVOICE_TO_FILE, "AD_Process_ID", "AD_Process_Access");
		
		/** Inserciones en ad_workflow_access */
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, WORKFLOW_PROCESS_SPLITTING, "AD_Workflow_ID", "AD_Workflow_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, WORKFLOW_PROCESS_PRODUCT_CHANGE, "AD_Workflow_ID", "AD_Workflow_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, WORKFLOW_PROCESS_TRANSFER, "AD_Workflow_ID", "AD_Workflow_Access");
		insertAccess(CLIENT, ORG, ROLE_GESTION_ALMACENES, WORKFLOW_PROCESS_WAREHOUSE_CLOSE, "AD_Workflow_ID", "AD_Workflow_Access");
		
		/** Inserciones en ad_form_access */
		insertAccess(CLIENT, ORG, ROLE_VENTAS, FORM_FISCAL_PRINTER_CONTROL_PANEL, "AD_Form_ID", "AD_Form_Access");
		
		/** Actualizacion fecha de version */
		DB.executeUpdate(" UPDATE AD_System SET version = '15-03-2010' WHERE AD_System_ID = 0 ", get_TrxName());
		
		return "";
	}
	
	/**
	 * Inserta una nueva entrada en los permisos
	 * @param AD_Client_ID compañía
	 * @param AD_Org_ID organización
	 * @param AD_Role_ID rol
	 * @param ResourceID ID del recurso (un AD_Window_ID, AD_Process_ID, AD_WorkFlow_ID, AD_Form_ID)
	 * @param table tabla (AD_Process_Access, AD_Window_Access, etc.)
	 * @param refColumn columna a setear el valor (AD_Process_ID, AD_Window_ID, etc.)
	 */
	void insertAccess(int AD_Client_ID, int AD_Org_ID, int AD_Role_ID, int resourceID, String resourceColumn, String tableName )
	{
		try 
		{
			if (existsAccessEntry(AD_Role_ID, resourceID, resourceColumn, tableName) || resourceID <= 0)
				throw new Exception();	
			
			// Obtener la tabla
			int tableID = DB.getSQLValue(get_TrxName(), " SELECT AD_Table_ID FROM AD_Table WHERE tablename ilike '" + tableName + "'");
			if (tableID == 0)
				throw new Exception();

			M_Table table = new M_Table(getCtx(), tableID, get_TrxName());
			if (table == null || table.getAD_Table_ID() == 0)
				throw new Exception();
			
			PO accessPO = table.getPO(0, get_TrxName());
	
			// Setear los valores
			accessPO.set_ValueNoCheck("AD_Client_ID", AD_Client_ID);
			accessPO.set_ValueNoCheck("AD_Org_ID", AD_Org_ID);
			accessPO.set_ValueNoCheck("AD_Role_ID", AD_Role_ID);
			accessPO.set_ValueNoCheck(resourceColumn, resourceID);
	
			// Si devuelve un error, el mismo no se propaga debido a que es un problema menor
			if (!accessPO.save())
				throw new Exception();
		}
		catch (Exception e)
		{
			log.log( Level.INFO, " No insertado en " + tableName + " - Client:" + AD_Client_ID + ", Org:" + AD_Org_ID + ", Role:" + AD_Role_ID + ", resourceID: " + resourceID );
		}
	}
	

	private boolean existsAccessEntry(int AD_Role_ID, int resourceID, String resourceColumn, String tableName) 
	{
		return 1 == DB.getSQLValue(get_TrxName(), " SELECT 1 FROM " + tableName + " WHERE " + resourceColumn + " = " + resourceID + " AND AD_Role_ID = " + AD_Role_ID);
	}
	
	
	@Override
	protected void prepare() {
		super.prepare();

	}
	

}
