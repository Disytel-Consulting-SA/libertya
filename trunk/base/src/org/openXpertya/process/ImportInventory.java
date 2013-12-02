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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MInventoryLine;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_I_Inventory;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportInventory extends SvrProcess {

	/**
	 * Preference para el mantenimiento de la tabla de Inventario, para que no
	 * crezca demasiado, se define ésta que valoriza en meses los registros
	 * permanentes. Esto significa que si posee valor 1, entonces se guardarán
	 * los del mes anterior al actual, si posee valor 2, de dos meses hacia
	 * atrás, y así sucesivamente. El resto de registros anteriores se
	 * eliminarán.
	 */
	private static final String INVENTORY_MAINTENANCE_PREFERENCE = "Maintenance_Inventory";
	
    /** Descripción de Campos */

    private int p_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int p_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int p_M_Locator_ID = 0;

    /** Descripción de Campos */

    private Timestamp p_MovementDate = null;

    /** Descripción de Campos */

    private boolean p_DeleteOldImported = false;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            //} else if( name.equals( "AD_Client_ID" )) {
            //    p_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            //} else if( name.equals( "AD_Org_ID" )) {
            //    p_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "M_Locator_ID" )) {
                p_M_Locator_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "MovementDate" )) {
                p_MovementDate = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "DeleteOldImported" )) {
                p_DeleteOldImported = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
        
        p_AD_Client_ID = Env.getAD_Client_ID(getCtx());
        p_AD_Org_ID = Env.getAD_Org_ID(getCtx());
        p_MovementDate = p_MovementDate != null?p_MovementDate:Env.getDate();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
        String errorLocatorNotFound   = "'"+getMsg("LocatorNotFound")+". '";
        String errorWarehouseNotFound = "'"+getMsg("WarehouseNotFound")+". '";
        String errorInvalidLocator    = "'"+getMsg("InvalidLocatorWarehouse")+". '";
        String errorProductNotFound   = "'"+getMsg("ProductNotFound")+". '";
        String errorInvalidQtyCount   = "'"+getMsg("InvalidQtyCount")+". '";
        String errorNoInventory		  = "'"+getMsg("NoExistsInventory")+". '";
        
    	log.info( "M_Locator_ID=" + p_M_Locator_ID + ",MovementDate=" + p_MovementDate );

        

        StringBuffer sql         = null;
        int          no          = 0;
        String       securityCheck = " AND AD_Client_ID=" + p_AD_Client_ID +" AND IsActive = 'Y' ";

        maintainInventoryImportTable(securityCheck);
        
        // Delete Old Imported

        if( p_DeleteOldImported ) {
            sql = new StringBuffer( "DELETE I_Inventory " + "WHERE I_IsImported='Y'" ).append( securityCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "Delete Old Impored =" + no );
        }

        // Reset

        sql = new StringBuffer( 
        	" UPDATE I_Inventory " + 
        	" SET AD_Client_ID   = COALESCE (AD_Client_ID," ).append( p_AD_Client_ID ).append( ")," + 
        	"     AD_Org_ID      = COALESCE (AD_Org_ID," ).append( p_AD_Org_ID ).append( ")," +
            "     IsActive       = COALESCE (IsActive, 'Y')," + 
            "     Created        = COALESCE (Created, SysDate)," + 
            "     CreatedBy      = COALESCE (CreatedBy, 0)," + 
            "     Updated        = COALESCE (Updated, SysDate)," + 
            "     UpdatedBy      = COALESCE (UpdatedBy, 0)," + 
            "     I_ErrorMsg     = NULL, " + 
            "     I_IsImported   = 'N' " + 
            " WHERE I_IsImported <> 'Y' OR I_IsImported IS NULL").append(securityCheck);
        
        no = DB.executeUpdate( sql.toString());
        log.info( "Reset=" + no );
        
        //sql = new StringBuffer( "UPDATE I_Inventory i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Org, '" + " WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0" + " OR EXISTS (SELECT * FROM AD_Org oo WHERE i.AD_Org_ID=oo.AD_Org_ID AND (oo.IsSummary='Y' OR oo.IsActive='N')))" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        //no = DB.executeUpdate( sql.toString());

        //if( no != 0 ) {
            //log.warning( "Invalid Org=" + no );
        //}

        //////////////////////////////////////////////////////////////////////////////////////
        // Set Movement Date
        //////////////////////////////////////////////////////////////////////////////////////
        sql = new StringBuffer(
        	" UPDATE I_Inventory " +
        	" SET MovementDate = ").append( DB.TO_DATE( p_MovementDate )).append(
        	" WHERE I_IsImported <> 'Y' ").append(securityCheck);
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Movement Date = " + no );
        
		//////////////////////////////////////////////////////////////////////////////////////
		// Set Warehouse and Locator
		//////////////////////////////////////////////////////////////////////////////////////
		
		// Warehouse From Value
		sql = new StringBuffer( 
			" UPDATE I_Inventory i " + 
			" SET M_Warehouse_ID = " +
			"		(SELECT M_Warehouse_ID " +
			"	 	 FROM M_Warehouse w" + 
			"    	 WHERE (TRIM(i.WarehouseValue) = TRIM(w.Value) OR" +
			"               TRIM(i.WarehouseValue) = TRIM(w.Name)) AND " +
			"              i.AD_Client_ID = w.AD_Client_ID AND " +
			"              w.IsActive = 'Y' AND " +
			"              ROWNUM=1) " + 
			" WHERE WarehouseValue IS NOT NULL AND " +
			"       I_IsImported <> 'Y' ").append( securityCheck );
		no = DB.executeUpdate( sql.toString());
		log.fine( "Set Warehouse from Value = " + no );
        
        // Locator From Value
        sql = new StringBuffer( 
        	" UPDATE I_Inventory i " + 
        	" SET M_Locator_ID = " +
        	"		(SELECT M_Locator_ID " +
        	"	 	 FROM M_Locator l" + 
        	"    	 WHERE i.LocatorValue = l.Value AND " +
        	"              i.AD_Client_ID = l.AD_Client_ID AND " +
        	"              l.IsActive = 'Y' AND " +
        	"              ROWNUM=1) " + 
        	" WHERE LocatorValue IS NOT NULL AND " +
        	"       I_IsImported <> 'Y' ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Locator from Value = " + no );
        
        // locator from X,Y,Z
        sql = new StringBuffer( 
        	" UPDATE I_Inventory i " + 
        	" SET M_Locator_ID=" +
        	"		(SELECT M_Locator_ID " +
        	"        FROM M_Locator l" + 
            "        WHERE i.X=l.X AND i.Y=l.Y AND i.Z=l.Z AND " +
            "              i.AD_Client_ID=l.AD_Client_ID AND " +
            "              l.IsActive = 'Y' AND " +
            "              ROWNUM=1) " + 
            " WHERE M_Locator_ID IS NULL AND " +
            "       X IS NOT NULL AND Y IS NOT NULL AND Z IS NOT NULL AND " +
            "       I_IsImported<>'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Locator from X,Y,Z = " + no );
        
        // Warehouse From Locator
        sql = new StringBuffer( 
        	" UPDATE I_Inventory i " + 
        	" SET M_Warehouse_ID = " +
        	"		(SELECT M_Warehouse_ID " +
        	"        FROM M_Locator l " +
        	"        WHERE i.M_Locator_ID=l.M_Locator_ID) " + 
        	" WHERE M_Warehouse_ID IS NULL AND " +
        	"       M_Locator_ID IS NOT NULL AND " +
        	"       I_IsImported <> 'Y' ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Warehouse from Locator = " + no );
        
        // locator default from warehouse
        sql = new StringBuffer( 
        	" UPDATE I_Inventory i " + 
        	" SET M_Locator_ID = " +
        	"		(SELECT M_Locator_ID " +
        	"        FROM M_Locator l " +
        	"        WHERE i.M_Warehouse_ID=l.M_Warehouse_ID" +
        	"		 ORDER BY isdefault DESC " +
        	"		 LIMIT 1) " + 
        	" WHERE M_Warehouse_ID IS NOT NULL AND " +
        	"       M_Locator_ID IS NULL AND " +
        	"       I_IsImported <> 'Y' ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Locator Default from Warehouse = " + no );
        
        // Locator From parameter (default locator)
        if( p_M_Locator_ID != 0 ) {
            sql = new StringBuffer( 
            	" UPDATE I_Inventory " + 
            	" SET M_Locator_ID = " ).append(p_M_Locator_ID).append(", "+
            	"     LocatorValue = (SELECT Value FROM M_Locator WHERE M_Locator_ID = ").append(p_M_Locator_ID).append(") "+		
            	" WHERE M_Locator_ID IS NULL AND " +
            	"       I_IsImported<>'Y'" ).append( securityCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "Set Locator from Parameter = " + no );
        }

        // No locator detection
        sql = new StringBuffer( 
        	" UPDATE I_Inventory " + 
        	" SET I_IsImported = 'E', " +
        	"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + errorLocatorNotFound + 
        	" WHERE M_Locator_ID IS NULL AND " +
        	"       I_IsImported <> 'Y' ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
            log.warning( "Locator not found = " + no );
        }

        // No Warehouse detection
        sql = new StringBuffer( 
        	" UPDATE I_Inventory " + 
        	" SET I_IsImported = 'E', " +
        	"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + errorWarehouseNotFound + 
        	" WHERE M_Warehouse_ID IS NULL AND " +
        	"       I_IsImported <> 'Y' ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
            log.warning( "Warehouse not found = " + no );
        }
        
        // Locator warehouse not the line warehouse detection
        sql = new StringBuffer( 
            	" UPDATE I_Inventory i " + 
            	" SET I_IsImported = 'E', " +
            	"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + errorInvalidLocator + 
            	" WHERE i.M_Warehouse_ID IS NOT NULL AND " +
            	"       i.M_Locator_ID IS NOT NULL AND " +
            	"       i.I_IsImported <> 'Y' AND " +
            	"       i.M_Warehouse_ID <> (SELECT l.M_Warehouse_ID " +
            	"                            FROM M_Locator l " +
            	"                            WHERE l.M_Locator_ID = i.M_Locator_ID) ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
            log.warning( "Locator Warehouse not the line Warehouse = " + no );
        }
        
        //////////////////////////////////////////////////////////////////////////////////////
        // Set Product
        //////////////////////////////////////////////////////////////////////////////////////
        
        // ... From Value
        sql = new StringBuffer( 
        	" UPDATE I_Inventory i " + 
        	" SET M_Product_ID = " +
        	"		(SELECT M_Product_ID " +
        	"        FROM M_Product p" + 
        	"        WHERE i.Value = p.Value AND " +
        	"              i.AD_Client_ID = p.AD_Client_ID AND " +
        	"              p.IsActive = 'Y' AND " +
        	"              ROWNUM=1) " + 
        	" WHERE Value IS NOT NULL AND " + 
        	"       I_IsImported <> 'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Product from Value=" + no );


        // ... From UPC Instance
        sql = new StringBuffer( 
        	" UPDATE I_Inventory i " + 
        	" SET M_AttributeSetInstance_ID = " +
        	"		(SELECT M_AttributeSetInstance_ID " +
        	"		 FROM M_Product_UPC_Instance p " + 
        	"        WHERE i.UPC = p.UPC AND " +
        	"		 	   i.M_Product_ID = p.M_Product_ID AND " +
        	"              i.AD_Client_ID = p.AD_Client_ID AND " +
        	"              p.IsActive = 'Y' AND " +
        	"              ROWNUM=1) " + 
        	" WHERE M_AttributeSetInstance_ID IS NULL AND " +
        	"       UPC IS NOT NULL AND " +
        	"       I_IsImported <> 'Y' ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set AttributeInstance from UPC = " + no );
        
        sql = new StringBuffer( 
            	" UPDATE I_Inventory i " + 
            	" SET INSTANCE_DESCRIPTION = " +
            	"		(SELECT Description " +
            	"		 FROM M_AttributeSetInstance p " + 
            	"        WHERE i.M_AttributeSetInstance_ID = p.M_AttributeSetInstance_ID AND " +
            	"              i.AD_Client_ID = p.AD_Client_ID AND " +
            	"              p.IsActive = 'Y' AND " +
            	"              ROWNUM=1) " + 
            	" WHERE INSTANCE_DESCRIPTION IS NULL AND " +
            	"       M_AttributeSetInstance_ID IS NOT NULL AND " +
            	"       I_IsImported <> 'Y' ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Instance_Description from AttributeInstance = " + no );
        
        if(no==0){
	        // ... From UPC
	        sql = new StringBuffer( 
	        	" UPDATE I_Inventory i " + 
	        	" SET M_Product_ID = " +
	        	"		(SELECT M_Product_ID " +
	        	"		 FROM M_Product p " + 
	        	"        WHERE i.UPC = p.UPC AND " +
	        	"              i.AD_Client_ID = p.AD_Client_ID AND " +
	        	"              p.IsActive = 'Y' AND " +
	        	"              ROWNUM=1) " + 
	        	" WHERE M_Product_ID IS NULL AND " +
	        	"       UPC IS NOT NULL AND " +
	        	"       I_IsImported <> 'Y' ").append( securityCheck );
	        no = DB.executeUpdate( sql.toString());
	        log.fine( "Set Product from UPC = " + no );
        }
        
        // No product detection
        sql = new StringBuffer( 
        	" UPDATE I_Inventory " + 
        	" SET I_IsImported = 'E', " +
        	"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + errorProductNotFound +
        	" WHERE M_Product_ID IS NULL AND " +
        	"       I_IsImported<>'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
            log.warning( "Product not found = " + no );
        }

        // No QtyCount
        sql = new StringBuffer( 
        	" UPDATE I_Inventory " + 
        	" SET I_IsImported = 'E', " +
        	"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + errorInvalidQtyCount + 
        	" WHERE QtyCount IS NULL AND " +
        	"       I_IsImported <> 'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
            log.warning( "No QtyCount = " + no );
        }
        
        //////////////////////////////////////////////////////////////////////////////////////
        // Set Organization from VALUE
        //////////////////////////////////////////////////////////////////////////////////////
        sql = new StringBuffer( 
        	" UPDATE I_Inventory i " + 
        	" SET AD_Org_ID = " +
        	"		(SELECT AD_Org_ID " +
        	"        FROM AD_Org p" + 
        	"        WHERE i.OrgValue = p.Value AND " +
        	"              i.AD_Client_ID = p.AD_Client_ID AND " +
        	"              p.IsActive = 'Y'" +
        	"              ) " + 
        	" WHERE OrgValue IS NOT NULL AND " + 
        	"       I_IsImported <> 'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Org from OrgValue=" + no );

        //////////////////////////////////////////////////////////////////////////////////////
        // Set Organization
        //////////////////////////////////////////////////////////////////////////////////////
        sql = new StringBuffer( 
        	" UPDATE I_Inventory i " + 
        	" SET AD_Org_ID = " +
        	"		(SELECT AD_Org_ID " +
        	"		 FROM M_Warehouse p " + 
        	"        WHERE i.M_Warehouse_ID = p.M_Warehouse_ID AND " +
        	"              ROWNUM=1) " + 
        	" WHERE M_Warehouse_ID IS NOT NULL AND " +
        	"       I_IsImported <> 'Y' ").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Organization from Warehouse = " + no );
        
		//////////////////////////////////////////////////////////////////////////////////////
		// Set Inventory
		//////////////////////////////////////////////////////////////////////////////////////
		MDocType docTypePI = MDocType.getDocType(getCtx(),
				MDocType.DOCTYPE_MaterialPhysicalInventory, get_TrxName());
        sql = new StringBuffer("UPDATE I_Inventory i " +
        		"SET M_Inventory_ID = (SELECT m.m_inventory_id " +
        		"						FROM m_inventory m " +
        		"						WHERE m.documentno = i.inventory_documentno " +
        		"								AND m.m_warehouse_id = i.m_warehouse_id" +
        		"								AND m.docstatus IN ('DR','IP') " +
        		"								AND m.inventorykind = '"
				+ MInventory.INVENTORYKIND_PhysicalInventory + "'"
				+
				"								AND m.c_doctype_id = "+docTypePI.getC_DocType_ID() +
        		"						LIMIT 1) " +
        		"WHERE inventory_documentno is not null " +
        		"		AND M_Warehouse_ID IS NOT NULL " +
        		"		AND I_IsImported <> 'Y'").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Inventory ID From Inventory DocumentNo = " + no );
        
        // Error si tiene un nro de documento pero no existe tal inventario
        sql = new StringBuffer( 
            	" UPDATE I_Inventory " + 
            	" SET I_IsImported = 'E', " +
            	"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + errorNoInventory + 
            	" WHERE inventory_documentno is not null AND " +
            	"		M_Inventory_ID IS NULL AND " +
            	"       I_IsImported <> 'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
            log.warning( "No Inventory ID = " + no );
        }
        
		//////////////////////////////////////////////////////////////////////////////////////
		// Set Inventory Line
		//////////////////////////////////////////////////////////////////////////////////////
        sql = new StringBuffer("UPDATE I_Inventory i " +
        		"SET M_InventoryLine_ID = (SELECT m.m_inventoryline_id " +
        		"						FROM m_inventoryline m " +
        		"						WHERE m.m_product_id = i.m_product_id " +
        		"								AND m.m_inventory_id = i.m_inventory_id" +
        		"								AND (CASE WHEN i.m_attributesetinstance_id is null THEN m.m_attributesetinstance_id = 0" +
        		"											WHEN i.m_attributesetinstance_id = 0 THEN m.m_attributesetinstance_id = 0" +
        		"											ELSE m.m_attributesetinstance_id = i.m_attributesetinstance_id " +
        		"										END)" +
        		"								AND m.isactive = 'Y'" +
        		"						LIMIT 1) " +
        		"WHERE M_Inventory_ID is not null " +
        		"		AND M_Product_ID is not null " +
        		"		AND I_IsImported <> 'Y'").append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set Inventory Line ID = " + no );
        
        MInventory inventory    = null;
        MInventoryLine line 	= null;
        int        noInsert     = 0;
        int        noUpdate     = 0;
        int        noInsertLine = 0;
        int        noUpdateLine = 0;
        boolean insertLine = false;

        // Go through Inventory Records

        sql = new StringBuffer( 
        	" SELECT I_Inventory_ID " +
        	" FROM I_Inventory " + 
        	" WHERE I_IsImported = 'N'" ).append( securityCheck ).append(
        	" ORDER BY M_Inventory_ID, M_Warehouse_ID, TRUNC(MovementDate), I_Inventory_ID" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            //

            int       x_M_Warehouse_ID = -1;
            Timestamp x_MovementDate   = null;
            X_I_Inventory imp = null;
            while( rs.next()) {
				imp = new X_I_Inventory(getCtx(), rs.getInt("I_Inventory_ID"), null);
                Timestamp     MovementDate = TimeUtil.getDay( imp.getMovementDate());

                // Inventario existente
                if(!Util.isEmpty(imp.getM_Inventory_ID(), true)){
                	if(inventory == null || inventory.getM_Inventory_ID() != imp.getM_Inventory_ID()){
                		inventory = new MInventory( getCtx(),imp.getM_Inventory_ID(),null );
                		manageInventory(inventory);
                		noUpdate++;
                	}
                }
                else if ( (inventory == null) || (imp.getM_Warehouse_ID() != x_M_Warehouse_ID) ||!MovementDate.equals( x_MovementDate )) {
                    inventory = new MInventory( getCtx(),0,null );
                    inventory.setClientOrg( imp.getAD_Client_ID(),imp.getAD_Org_ID());
                    inventory.setDescription( "I " + imp.getM_Warehouse_ID() + " " + MovementDate );
                    inventory.setM_Warehouse_ID( imp.getM_Warehouse_ID());
                    inventory.setMovementDate( MovementDate );
                    manageInventory(inventory);
                    
                    if( !inventory.save()) {
                        log.log( Level.SEVERE,"Inventory not saved: "+CLogger.retrieveErrorAsString() );

                        break;
                    }

                    x_M_Warehouse_ID = imp.getM_Warehouse_ID();
                    x_MovementDate   = MovementDate;
                    noInsert++;
                }
                
                

                // Line
                
                // Added by Lucas Hernandez - Kunan
                //int            M_AttributeSetInstance_ID = 0;
           
                // Línea de inventario existente
                if(!Util.isEmpty(imp.getM_InventoryLine_ID(), true)){
                	line = new MInventoryLine(getCtx(), imp.getM_InventoryLine_ID(), null);
                	line.setQtyCount(imp.getQtyCount());
                	line.setQtyInternalUse(line.getQtyBook().subtract(imp.getQtyCount()));
                }
                else{
					line = new MInventoryLine(inventory, imp.getM_Locator_ID(),
							imp.getM_Product_ID(),
							imp.getM_AttributeSetInstance_ID(),
							imp.getQtyBook(), imp.getQtyCount());
                }
                
                insertLine = line.getM_InventoryLine_ID() == 0;
                
                if( line.save()) {
                	
					no = DB.executeUpdate("UPDATE I_Inventory SET i_isimported='Y',i_errormsg=null,m_inventory_id="
							+ line.getM_Inventory_ID()
							+ ",M_InventoryLine_ID="
							+ line.getM_InventoryLine_ID()
							+ ",processed='Y' WHERE i_inventory_id = "
							+ imp.getID());
//                    imp.setI_IsImported( true );
//                    imp.setI_ErrorMsg(null);
//                    imp.setM_Inventory_ID( line.getM_Inventory_ID());
//                    imp.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
//                    imp.setProcessed( true );

//                    if( imp.save()) {
					if(no == 1){
						if(insertLine){
							noInsertLine++;
						}
						else{
							noUpdateLine++;
						}
                    }
                }
                else{
					no = DB.executeUpdate("UPDATE I_Inventory SET i_isimported='E',i_errormsg="
							+ CLogger.retrieveErrorAsString()
							+ " WHERE i_inventory_id = " + imp.getID());
                }
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt",e );
        }

        // Set Error to indicator to not imported

        sql = new StringBuffer( 
        	" UPDATE I_Inventory " + 
        	" SET I_IsImported = 'N', " +
        	"     Updated=SysDate " + 
        	" WHERE I_IsImported <> 'Y'" ).append( securityCheck );
        no = DB.executeUpdate( sql.toString());
        addLog( 0,null,new BigDecimal( no ),"@Errors@" );

        // Post Import
        postImport();

        addLog( 0,null,new BigDecimal( noInsert ),"@M_Inventory_ID@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noUpdate ),"@M_Inventory_ID@: @Updated@" );
        addLog( 0,null,new BigDecimal( noInsertLine ),"@M_InventoryLine_ID@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noUpdateLine ),"@M_InventoryLine_ID@: @Updated@" );

        return "";
    }    // doIt
    
    private String getMsg(String message) {
    	return Msg.translate(getCtx(), message);
    }
    
    /**
	 * Mantenimiento de la tabla de inventario para que no crezca en cada
	 * importación 
	 * 
	 * @throws Exception
	 */
	protected int maintainInventoryImportTable(String securityCheck) throws Exception{
		String maintenanceMonth = MPreference.searchCustomPreferenceValue(
				INVENTORY_MAINTENANCE_PREFERENCE, getAD_Client_ID(), 
				Env.getAD_Org_ID(getCtx()),	Env.getAD_User_ID(getCtx()), true);
		// Si la preference no tiene nada, no se elimina nada
		if(Util.isEmpty(maintenanceMonth, true)){
			return 0;
		}
		Integer months = 0;
		try {
			months = Integer.parseInt(maintenanceMonth);
		} catch (Exception e) {
			throw new Exception("La preferencia o valor predeterminado "
					+ INVENTORY_MAINTENANCE_PREFERENCE
					+ " no esta existo o contiene un valor no numerico");
		}
		months = months * -1;
		// Eliminación de registros anteriores a los meses de tolerancia hacia atrás
		String sql = "DELETE FROM "
				+ X_I_Inventory.Table_Name
				+ " WHERE date_trunc('month',?::date) >= date_trunc('month',created) "
				+ securityCheck;
		Calendar toleranceDate = Calendar.getInstance();
		toleranceDate.setTimeInMillis(Env.getDate().getTime());
		toleranceDate.add(Calendar.MONTH, months);
		PreparedStatement ps = new CPreparedStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql,
				get_TrxName(), true);
		ps.setTimestamp(1, new Timestamp(toleranceDate.getTimeInMillis()));
		return ps.executeUpdate();
	}
	
	protected void manageInventory(MInventory inventory){
		// Las subclases pueden extender este método para realizar operaciones custom
		// NO agregar código en este método en esta clase
	}
	
	protected void postImport(){
		// Las subclases pueden extender este método para realizar operaciones custom
		// NO agregar código en este método en esta clase
	}
}    // ImportInventory



/*
 *  @(#)ImportInventory.java   02.07.07
 * 
 *  Fin del fichero ImportInventory.java
 *  
 *  Versión 2.2
 *
 */
