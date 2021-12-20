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
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTransaction extends X_M_Transaction {

	/** Preference para determinar si se quiere ignorar el seteo del campo description o no */
	public static String MTRANSACTION_IGNORE_DESCRIPTION_PREFERENCE = "MTRANSACTION_IGNORE_DESCRIPTION"; 
	
	/** Boolean que determinar si esta transacción se debe a una anulación */
	private boolean voiding = false;
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Transaction_ID
     * @param trxName
     */

    public MTransaction( Properties ctx,int M_Transaction_ID,String trxName ) {
        super( ctx,M_Transaction_ID,trxName );

        if( M_Transaction_ID == 0 ) {

            // setM_Transaction_ID (0);                //      PK
            // setM_Locator_ID (0);
            // setM_Product_ID (0);

            setMovementDate( new Timestamp( System.currentTimeMillis()));
            setMovementQty( Env.ZERO );

            // setMovementType (MOVEMENTTYPE_CustomerShipment);

        }
    }    // MTransaction

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MTransaction( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MTransaction

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param MovementType
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param MovementQty
     * @param MovementDate
     * @param trxName
     */

    public MTransaction( Properties ctx,String MovementType,int M_Locator_ID,int M_Product_ID,int M_AttributeSetInstance_ID,BigDecimal MovementQty,Timestamp MovementDate,String trxName ) {
        super( ctx,0,trxName );
        setMovementType( MovementType );

        if( M_Locator_ID == 0 ) {
            throw new IllegalArgumentException( "No Locator" );
        }

        setM_Locator_ID( M_Locator_ID );

        if( M_Product_ID == 0 ) {
            throw new IllegalArgumentException( "No Product" );
        }

        setM_Product_ID( M_Product_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );

        //

        if( MovementQty != null ) {    // Can be 0
            setMovementQty( MovementQty );
        }

        if( MovementDate == null ) {
            setMovementDate( new Timestamp( System.currentTimeMillis()));
        } else {
            setMovementDate( MovementDate );
        }
    }    // MTransaction

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MTransaction[" );

        sb.append( getID()).append( "," ).append( getMovementType()).append( ",Qty=" ).append( getMovementQty()).append( ",M_Product_ID=" ).append( getM_Product_ID()).append( ",ASI=" ).append( getM_AttributeSetInstance_ID()).append( "]" );

        return sb.toString();
    }    // toString
    
    
    @Override
    public void setDescription(String Description) {
    	if ("Y".equals(MPreference.GetCustomPreferenceValue(MTRANSACTION_IGNORE_DESCRIPTION_PREFERENCE)))
    		return;
    	super.setDescription(Description);
    }
    
    
    @Override
    protected boolean beforeSave( boolean newRecord ) {
    	if(ImportClearanceManager.isImportClearanceActive(getCtx())) {
	    	CallResult cr = null;
    		// Movimientos de Inventario y Remitos, manejar despachos de importación
	    	if(!Util.isEmpty(getM_InOutLine_ID(), true)) {
	    		cr = manageImportClearance_InOut();
	    	}
	    	else if(!Util.isEmpty(getM_InventoryLine_ID(), true)) {
	    		cr = manageImportClearance_Inventory();
	    	}
	    	
	    	if(cr != null && cr.isError()) {
	    		log.saveError("SaveError", cr.getMsg());
	    		return false;
	    	}
    	}
    	
    	return true;
    }
    
    /**
	 * Manejar despachos de importación de remitos
	 * 
	 * @return resultado de la operación
	 */
    private CallResult manageImportClearance_InOut(){
    	CallResult cr = new CallResult();
    	MInOutLine iol = new MInOutLine(getCtx(), getM_InOutLine_ID(), get_TrxName());
    	// Gestión de despachos por movimientos de compra
    	if(MOVEMENTTYPE_VendorReceipts.equals(getMovementType()) 
    			|| MOVEMENTTYPE_VendorReturns.equals(getMovementType())) {
    		cr = manageMaterialReceiptImportClearance(getMovementQty(), iol);
    	}
    	// Gestión de despachos por movimientos de venta 
    	else {
			cr = updateImportClearance(getMovementQty(), iol.getM_Import_Clearance_ID());
    	}
    	
    	return cr;
    }
    
    /**
	 * Manejar despachos de importación de inventarios
	 * 
	 * @return resultado de la operación
	 */
    private CallResult manageImportClearance_Inventory() {
    	CallResult cr = new CallResult();
    	BigDecimal qtyToIC = getMovementQty().abs();
    	if(!isVoiding()) {
			// Si es una importación negativa, entonces primero se saca de stock nacional y
			// luego de despacho de importación, para eso debo obtener cuanto stock nacional
			// tengo 
	    	if(MOVEMENTTYPE_InventoryOut.equals(getMovementType())
	    			|| getMovementQty().signum() < 0) {
				BigDecimal nacStock = MImportClearance.getNationalStockFrom(getCtx(), getM_Product_ID(), null);
				if(nacStock.compareTo(BigDecimal.ZERO) > 0) {
					qtyToIC = qtyToIC.subtract(nacStock);
				}
	    	}
	    	
	    	// Recorrer por los despachos de importación aplicando la cantidad hasta no tener
	    	cr = applyInventoryImportClearanceQty(qtyToIC, getMovementQty().signum() > 0);
    	}
    	return cr;
    }
    
    /**
	 * Incrementa o decrementa stock de cada despacho hasta que la
	 * cantidad parámetro sea 0, módulo inventarios
	 * 
	 * @param qty       cantidad
	 * @param increment true si se debe incrementar stock, false caso
	 *                  contrario
	 * @return resultado de la operación
	 */
    private CallResult applyInventoryImportClearanceQty(BigDecimal qty, boolean incrementStock){
    	CallResult cr = new CallResult();
    	if(qty.compareTo(BigDecimal.ZERO) == 0) {
    		return cr;
    	}
		ImportClearanceProcessing icp = ImportClearanceManager.getImportClearanceProcessingClass(incrementStock);
		try {
			icp.applyInventoryImportClearanceQty(getCtx(), getM_InventoryLine_ID(), getM_Product_ID(), qty,
					get_TrxName());
		} catch(Exception e) {
			cr.setMsg(e.getMessage(), true);
		}
    	return cr;
    }
    
    /**
	 * Insertar o eliminar los números de despacho dependiendo si es compleción o
	 * anulación
	 * 
	 * @param qty cantidad a registrar
	 * @param iol línea de remito
	 * @return resultado de la operación
	 */
    private CallResult manageMaterialReceiptImportClearance(BigDecimal qty, MInOutLine iol) {
    	CallResult cr = new CallResult();
    	if(isVoiding()) {
    		// Si estamos bajo una anulación:
    		// Para Remitos de Entrada, se eliminan los registros anteriormente creados
    		if(MOVEMENTTYPE_VendorReceipts.equals(getMovementType())) {
    			cr = deleteInOutImportClearance(iol);
    		}
    		// Para Devoluciones de Proveedor, se retrotraen los cambios realizados sobre el despacho
    		else {
    			cr = updateImportClearance(qty, iol.getM_Import_Clearance_ID());
    		}
    	}
    	else {
        	// Bajo Compleción
    		// Remitos de Entrada, se insertan nuevos despachos de importación
    		if(MOVEMENTTYPE_VendorReceipts.equals(getMovementType())) {
    			cr = insertInOutImportClearance(qty, iol);
    		}
    		// Devolución de Cliente, se modifica la cantidad del despacho
    		else {
    			cr = updateImportClearance(qty, iol.getM_Import_Clearance_ID());
    		}
    	}
    	return cr;
    }
    
    /**
	 * Insertar el despacho de importación a partir de la transacción
	 * 
	 * @param qty cantidad
	 * @return resultado de la operación
	 */
    private CallResult insertInOutImportClearance(BigDecimal qty, MInOutLine iol) {
    	CallResult cr = new CallResult();
    	MInOut io = new MInOut(getCtx(), iol.getM_InOut_ID(), get_TrxName());
    	// Crear el despacho
    	if(!Util.isEmpty(io.getClearanceNumber(), true)) {
    		MImportClearance ic = new MImportClearance(getCtx(), 0, get_TrxName());
        	ic.setClearanceNumber(io.getClearanceNumber());
        	ic.setM_Product_ID(getM_Product_ID());
        	ic.setMovementDate(io.getMovementDate());
        	ic.setQty(qty);
        	ic.setQtyUsed(BigDecimal.ZERO);
        	if(!ic.save()) {
        		cr.setMsg(CLogger.retrieveErrorAsString(), true);
        		return cr;
        	}
        	// Asignar el id a la linea del remito
        	iol.setM_Import_Clearance_ID(ic.getID());
        	if(!iol.save()) {
        		cr.setMsg(CLogger.retrieveErrorAsString(), true);
        		return cr;
        	}
    	}
    	
    	return cr;
    }
    
    /**
	 * Elimina el despacho de importación que posee asociado la línea del remito
	 * parámetro
	 * 
	 * @param iol línea de remito
	 * @return resultado de la operación
	 */
    private CallResult deleteInOutImportClearance(MInOutLine iol) {
    	CallResult cr = new CallResult();
    	if(!Util.isEmpty(iol.getM_Import_Clearance_ID(), true)) {
			// Verificar que no tenga cantidad usada mayor a 0 ya que en ese caso no es
			// posible eliminar
    		MImportClearance ic = new MImportClearance(getCtx(), iol.getM_Import_Clearance_ID(), get_TrxName());
    		if(ic.getQtyUsed().compareTo(BigDecimal.ZERO) > 0) {
    			MProduct p = MProduct.get(getCtx(), iol.getM_Product_ID());
				cr.setMsg(
						Msg.getMsg(getCtx(), "DeleteImportClearanceUsed",
								new Object[] { ic.getClearanceNumber(), p.getName() + " (" + p.getValue() + ")" }),
						true);
				return cr;
    		}
    		// Liberar las líneas de remito asociadas a ese despacho
			DB.executeUpdate("UPDATE m_inoutline SET M_Import_Clearance_ID = null WHERE M_Import_Clearance_ID = "
					+ iol.getM_Import_Clearance_ID(), get_TrxName());
    		// Eliminar
			if(!ic.delete(false)) {
				cr.setMsg(CLogger.retrieveErrorAsString(), true);
				return cr;
			}
		}
    	return cr;
    }
    
    /**
	 * Actualiza la cantidad de un despacho de importación dado
	 * 
	 * @param qty               cantidad a modificar
	 * @param importClearanceID id de despacho de importación a modificar
	 * @return resultado de la operación
	 */
    private CallResult updateImportClearance(BigDecimal qty, Integer importClearanceID) {
    	CallResult cr = new CallResult();
    	if(Util.isEmpty(importClearanceID, true)) {
    		return cr;
    	}
    	MImportClearance ic = new MImportClearance(getCtx(), importClearanceID, get_TrxName());
    	ic.setQtyUsed(ic.getQtyUsed().subtract(qty));
    	if(!ic.save()) {
    		cr.setMsg(CLogger.retrieveErrorAsString(), true);
    		return cr;
    	}
    	return cr;
    }
    
	public boolean isVoiding() {
		return voiding;
	}

	public void setVoiding(boolean voiding) {
		this.voiding = voiding;
	}
    
}    // MTransaction



/*
 *  @(#)MTransaction.java   02.07.07
 * 
 *  Fin del fichero MTransaction.java
 *  
 *  Versión 2.2
 *
 */
