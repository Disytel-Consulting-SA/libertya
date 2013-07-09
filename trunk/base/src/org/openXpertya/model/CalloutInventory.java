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



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutInventory extends CalloutEngine {

	private void setCost(Properties ctx, MTab mTab, String productColumnName, String costColumnName){
		Integer productID = (Integer)mTab.getValue(productColumnName);
		BigDecimal cost = BigDecimal.ZERO;
		BigDecimal oldCost = (BigDecimal)mTab.getValue(costColumnName);
		if(!Util.isEmpty(productID, true)){
			cost = MProductPricing.getCostPrice(ctx, Env.getAD_Org_ID(ctx),
					productID,
					MProductPO.getFirstVendorID(productID, null),
					Env.getContextAsInt(ctx, "$C_Currency_ID"), Env.getDate(),
					false, false, null, false, null);
		}
		// Se actualiza si es distinto
		if(oldCost == null || cost.compareTo(oldCost) != 0){
			mTab.setValue(costColumnName, cost);
		}
	}
	
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer M_Product_ID = ( Integer )value;

        if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            Env.setContext(ctx, WindowNo, "ProductType", "");
            return "";
        }

        // Set Attribute

        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
        } 

        Integer inventoryID = (Integer)mTab.getValue("M_Inventory_ID");
        boolean calculateQtyBook = true;
        if (inventoryID != null && inventoryID > 0) {
        	// Por el momento solo la clase de inventario "Inventario Físico" requiere
        	// que se calcule la cantidad del sistema cuando se selecciona un artículo.
        	MInventory inventory = new MInventory(ctx, inventoryID, null);
        	calculateQtyBook = 
        		inventory.getInventoryKind().equals(MInventory.INVENTORYKIND_PhysicalInventory);
        }
        
        Integer ID = ( Integer )mTab.getValue( "M_InventoryLine_ID" );

        // New Line - Get Book Value

        if( (ID != null) && (ID.intValue() == 0) && calculateQtyBook) {

            // Set QtyBook from first storage location (not correct)

            int M_Locator_ID = Env.getContextAsInt( ctx,WindowNo,"M_Locator_ID" );
            String sql = "SELECT QtyOnHand FROM M_Storage " + "WHERE M_Product_ID=?"    // 1
                         + " AND M_Locator_ID=?";    // 2

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,M_Product_ID.intValue());
                pstmt.setInt( 2,M_Locator_ID );

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    BigDecimal bd = rs.getBigDecimal( 1 );

                    if( !rs.wasNull()) {
                        mTab.setValue( "QtyBook",bd );
                    }
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"product",e );

                return e.getLocalizedMessage();
            }
        }
        
        // Seteo en el contexto el tipo del artículo
        MProduct product = MProduct.get(ctx, M_Product_ID);
        Env.setContext(ctx, WindowNo, "ProductType", product.getProductType());

        Integer chargeID = Env.getContextAsInt(ctx, WindowNo, "C_Charge_ID");
        if(Util.isEmpty(chargeID, true)){
			MInventory inventory = new MInventory(ctx,
					(Integer) mTab.getValue("M_Inventory_ID"), null); 
			Env.setContext(ctx, WindowNo, "C_Charge_ID",
					inventory.getC_Charge_ID());	
        }
        
        // Seteo el costo siempre que no sea artículo bien de uso
        if(!X_M_Product.PRODUCTTYPE_Assets.equals(product.getProductType())){
        	setCost(ctx, mTab, "M_Product_ID", "Cost");
        }
        
        return "";
    }    // product
    
    public String inventoryKind( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	String inventoryKind = (String)value;
   		// Se asigna el Tipo de Documento según la clase del inventario
    	if (inventoryKind != null) {
    		MDocType docType = null;
    		// - Ingreso/Egreso Simple
    		if (inventoryKind.equals(MInventory.INVENTORYKIND_SimpleInOut)) {
    			docType = MDocType.getDocType(ctx, MDocType.DOCTYPE_SimpleMaterialInOut, null);
    		// - Inventario Físico.
    		} else if (inventoryKind.equals(MInventory.INVENTORYKIND_PhysicalInventory)) {
    			docType = MDocType.getDocType(ctx, MDocType.DOCTYPE_MaterialPhysicalInventory, null);
    		}
    		if (docType != null) {
    			mTab.setValue("C_DocType_ID", docType.getC_DocType_ID());
    		}
    	}
    	return "";
    }
    
    public String paperForm( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		String strValue = (String)value;
		// Verificar si el nro ingresado ya existe, en ese caso elevar un
		// warning al guardar
		setCalloutActive( true );
		
		// Esto se comenta ya que en un principio se pedía la validación, pero
		// luego se decidió que no se implementa
		// ------------------------------------------------------------------------
//		// Verificar si existe un registro con el mismo nro formulario papel
//		String docNo = MInventory.getDocNoInventoryByStrColumnCondition(ctx,
//				"Paper_Form", strValue,
//				(Integer) mTab.getValue("M_Inventory_ID"), null);
//		// Si existe una entrada/salida con ese nro entonces registrar el warning
//		if(docNo != null){
//			mTab.setCurrentRecordWarning(Msg.getMsg(ctx,
//					"PaperFormWarning", new Object[] { docNo }));
//		}
//		else{
//			mTab.clearCurrentRecordWarning();
//		}
		// ------------------------------------------------------------------------
		
		setCalloutActive( false );
		return "";
	}
    
    public String qtycountwithoutchargesign( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	BigDecimal qtyWithoutSign = (BigDecimal)value;
    	Integer chargeID = Env.getContextAsInt(ctx, WindowNo, "C_Charge_ID");
    	if(qtyWithoutSign == null || chargeID == 0){
    		return "";
    	}
    	
		setCalloutActive( true );
		// Setea la cantidad contada con el signo del cargo * la cantidad sin signo
		MCharge charge = new MCharge(ctx, chargeID, null);
		Integer sign = Integer.parseInt(charge.getSign());
		mTab.setValue("QtyCount", qtyWithoutSign.multiply(new BigDecimal(sign)));
		
    	setCalloutActive( false );
    	
		return "";
    }
}    // CalloutInventory



/*
 *  @(#)CalloutInventory.java   02.07.07
 * 
 *  Fin del fichero CalloutInventory.java
 *  
 *  Versión 2.2
 *
 */
