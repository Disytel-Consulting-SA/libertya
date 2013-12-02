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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
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

public class MInventoryLine extends X_M_InventoryLine {

    /**
     * Descripción de Método
     *
     *
     * @param inventory
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     *
     * @return
     */

    public static MInventoryLine get( MInventory inventory,int M_Locator_ID,int M_Product_ID,int M_AttributeSetInstance_ID ) {
        MInventoryLine retValue = null;
        String         sql      = "SELECT * FROM M_InventoryLine " + "WHERE M_Inventory_ID=? AND M_Locator_ID=?" + " AND M_Product_ID=? AND M_AttributeSetInstance_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,inventory.get_TrxName());
            pstmt.setInt( 1,inventory.getM_Inventory_ID());
            pstmt.setInt( 2,M_Locator_ID );
            pstmt.setInt( 3,M_Product_ID );
            pstmt.setInt( 4,M_AttributeSetInstance_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MInventoryLine( inventory.getCtx(),rs,inventory.get_TrxName());
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MInventoryLine.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_InventoryLine_ID
     * @param trxName
     */

    public MInventoryLine( Properties ctx,int M_InventoryLine_ID,String trxName ) {
        super( ctx,M_InventoryLine_ID,trxName );

        if( M_InventoryLine_ID == 0 ) {

            // setM_Inventory_ID (0);                  //      Parent
            // setM_InventoryLine_ID (0);              //      PK
            // setM_Locator_ID (0);                    //      FK

            setLine( 0 );

            // setM_Product_ID (0);                    //      FK

            setM_AttributeSetInstance_ID( 0 );    // FK
            setInventoryType( INVENTORYTYPE_InventoryDifference );
            setQtyBook( Env.ZERO );
            setQtyCount( Env.ZERO );
            setProcessed( false );
        }
    }                                             // MInventoryLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInventoryLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInventoryLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param inventory
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param QtyBook
     * @param QtyCount
     */

    public MInventoryLine( MInventory inventory,int M_Locator_ID,int M_Product_ID,int M_AttributeSetInstance_ID,BigDecimal QtyBook,BigDecimal QtyCount ) {
        this( inventory.getCtx(),0,inventory.get_TrxName());

        if( inventory.getID() == 0 ) {
            throw new IllegalArgumentException( "Header not saved" );
        }

        setM_Inventory_ID( inventory.getM_Inventory_ID());    // Parent
        setClientOrg( inventory.getAD_Client_ID(),inventory.getAD_Org_ID());
        setM_Locator_ID( M_Locator_ID );                      // FK
        setM_Product_ID( M_Product_ID );                      // FK
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );

        //

        if( QtyBook != null ) {
            setQtyBook( QtyBook );
        }

        if( (QtyCount != null) && (QtyCount.signum() != 0) ) {
            setQtyCount( QtyCount );
        }
    }    // MInventoryLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQtyBook() {
        BigDecimal bd = super.getQtyBook();

        if( bd == null ) {
            bd = Env.ZERO;
        }

        return bd;
    }    // getQtyBook

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQtyCount() {
        BigDecimal bd = super.getQtyCount();

        if( bd == null ) {
            bd = Env.ZERO;
        }

        return bd;
    }    // getQtyBook

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Set Line No

        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM M_InventoryLine WHERE M_Inventory_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getM_Inventory_ID());

            setLine( ii );
        }
        
        MProduct product = null;
        if(!Util.isEmpty(getM_Product_ID(), true)){
	        product = MProduct.get(getCtx(), getM_Product_ID());
	        // Seteo el precio de costo
	        if(!product.getProductType().equals(MProduct.PRODUCTTYPE_Assets)){
		        setCost(MProductPricing.getCostPrice(getCtx(), getAD_Org_ID(),
						getM_Product_ID(),
						MProductPO.getFirstVendorID(getM_Product_ID(), get_TrxName()),
						Env.getContextAsInt(getCtx(), "$C_Currency_ID"), Env.getDate(),
						false, false, null, false, get_TrxName()));
	        }
        }
        
        // Si el inventario es una Entrada/Salida Simple, se nulean las cantidades de sistema
        // e interna, y se asigna el tipo Cargo a Cuenta seteando el cargo configurado
        // en el inventario encabezado.
        if (MInventory.INVENTORYKIND_SimpleInOut.equals(getInventory().getInventoryKind())) {
			// Si la cantidad sin signo es menor a 0 debería seguir el signo del cargo
			if (getQtyCountWithoutChargeSign().compareTo(BigDecimal.ZERO) < 0) {
        		log.saveError("MustFollowChargeSign", "");
        		return false;
        	}
        	// Actualizar la cantidad contada (qtycount) con la cantidad sin signo.
			// Por ahora sirve para entradas/salidas simples, se debe cambiar de
			// lugar esta porción de código cuando se tome para otros tipos de
			// cargos.
			MCharge charge = new MCharge(getCtx(), getInventory().getC_Charge_ID(), get_TrxName());
        	Integer sign = Integer.parseInt(charge.getSign());
        	setQtyCount(getQtyCountWithoutChargeSign().multiply(new BigDecimal(sign)));
        	setQtyBook(BigDecimal.ZERO);
        	setQtyInternalUse(BigDecimal.ZERO);
        	setInventoryType(INVENTORYTYPE_ChargeAccount);
        	setC_Charge_ID(getInventory().getC_Charge_ID());
        	if(product.getProductType().equals(MProduct.PRODUCTTYPE_Assets)){
				// Si es un producto bien de uso, se debe verificar que si es
				// salida y tiene una instancia asignada, entonces sea de -1 y
				// no mas
				if (!Util.isEmpty(getM_AttributeSetInstance_ID(), true)
						&& getQtyCount().signum() < 0) {
        			setQtyCount(new BigDecimal(-1));
        		}
				// Si la cantidad es mayor a 0 y no existe costo configurado,
				// entonces error
				if (Util.isEmpty(getM_AttributeSetInstance_ID(), true)
						&& getQtyCount().signum() > 0
						&& getCost().compareTo(BigDecimal.ZERO) <= 0) {
					log.saveError( "CostMustBeGreaterThanZero","" );
	                return false;
				}
        	}
        }

        // InternalUse Inventory
		// Si la cantidad interna (diferencia) es distinta de cero no siempre
		// debe ser del tipo Cargo, no es obligatorio

//        if( Env.ZERO.compareTo( getQtyInternalUse()) != 0 ) {
//            if( !INVENTORYTYPE_ChargeAccount.equals( getInventoryType())) {
//                setInventoryType( INVENTORYTYPE_ChargeAccount );
//            }
//
//            //
//
//            if( getC_Charge_ID() == 0 ) {
//                log.saveError( "InternalUseNeedsCharge","" );
//
//                return false;
//            }
//        }

        return true;
    }    // beforeSave
    
    private MInventory inventory = null;
    
    /**
     * @return Devuelve el inventario al que pertenece esta línea.
     */
    public MInventory getInventory() {
    	if (inventory == null) {
    		inventory = new MInventory(getCtx(), getM_Inventory_ID(), get_TrxName());
    	}
    	return inventory;
    }
}    // MInventoryLine



/*
 *  @(#)MInventoryLine.java   02.07.07
 * 
 *  Fin del fichero MInventoryLine.java
 *  
 *  Versión 2.2
 *
 */
