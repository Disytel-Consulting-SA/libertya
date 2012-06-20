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
import java.util.logging.Level;

import org.openXpertya.model.MInventory;
import org.openXpertya.model.MInventoryLine;
import org.openXpertya.model.X_I_Inventory;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportInventory extends SvrProcess {

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
        
    	log.info( "M_Locator_ID=" + p_M_Locator_ID + ",MovementDate=" + p_MovementDate );

        //

        StringBuffer sql         = null;
        int          no          = 0;
        String       securityCheck = " AND AD_Client_ID=" + p_AD_Client_ID + " AND CreatedBy=" + getAD_User_ID() + " AND IsActive = 'Y' ";

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
        // Set Locator
        //////////////////////////////////////////////////////////////////////////////////////

        // ... From Value
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
        
        // ... From X,Y,Z
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

        // ... From parameter (default locator)
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

        //////////////////////////////////////////////////////////////////////////////////////
        // Set Warehouse
        //////////////////////////////////////////////////////////////////////////////////////

        // ... From Value
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
        
        // ...From Locator
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
                
        
        MInventory inventory    = null;
        int        noInsert     = 0;
        int        noInsertLine = 0;

        // Go through Inventory Records

        sql = new StringBuffer( 
        	" SELECT * " +
        	" FROM I_Inventory " + 
        	" WHERE I_IsImported = 'N'" ).append( securityCheck ).append(
        	" ORDER BY M_Warehouse_ID, TRUNC(MovementDate), I_Inventory_ID" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            //

            int       x_M_Warehouse_ID = -1;
            Timestamp x_MovementDate   = null;

            while( rs.next()) {
                X_I_Inventory imp          = new X_I_Inventory( getCtx(),rs,null );
                Timestamp     MovementDate = TimeUtil.getDay( imp.getMovementDate());

                if( (inventory == null) || (imp.getM_Warehouse_ID() != x_M_Warehouse_ID) ||!MovementDate.equals( x_MovementDate )) {
                    inventory = new MInventory( getCtx(),0,null );
                    inventory.setClientOrg( imp.getAD_Client_ID(),imp.getAD_Org_ID());
                    inventory.setDescription( "I " + imp.getM_Warehouse_ID() + " " + MovementDate );
                    inventory.setM_Warehouse_ID( imp.getM_Warehouse_ID());
                    inventory.setMovementDate( MovementDate );

                    if( !inventory.save()) {
                        log.log( Level.SEVERE,"Inventory not saved" );

                        break;
                    }

                    x_M_Warehouse_ID = imp.getM_Warehouse_ID();
                    x_MovementDate   = MovementDate;
                    noInsert++;
                }

                // Line

                // Added by Lucas Hernandez - Kunan
                //int            M_AttributeSetInstance_ID = 0;
                MInventoryLine line                      = new MInventoryLine( inventory,imp.getM_Locator_ID(),imp.getM_Product_ID(),imp.getM_AttributeSetInstance_ID(),imp.getQtyBook(),imp.getQtyCount());

                if( line.save()) {
                    imp.setI_IsImported( true );
                    imp.setI_ErrorMsg(null);
                    imp.setM_Inventory_ID( line.getM_Inventory_ID());
                    imp.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
                    imp.setProcessed( true );

                    if( imp.save()) {
                        noInsertLine++;
                    }
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

        //

        addLog( 0,null,new BigDecimal( noInsert ),"@M_Inventory_ID@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noInsertLine ),"@M_InventoryLine_ID@: @Inserted@" );

        return "";
    }    // doIt
    
    private String getMsg(String message) {
    	return Msg.translate(getCtx(), message);
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
