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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRepairOrderProduct extends X_C_Repair_Order_Product {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_OrderLine_ID
     * @param trxName
     */

    public MRepairOrderProduct( Properties ctx,int C_RepairOrderProduct_ID,String trxName ) {
        super( ctx,C_RepairOrderProduct_ID,trxName );

        if( C_RepairOrderProduct_ID == 0 ) {

            //

            //

            setM_AttributeSetInstance_ID( 0 );

            //

            setQtyEntered( Env.ZERO );
        }
    }	// MRepairOrderProduct

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRepairOrderProduct( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRepairOrderProduct

    
    /** Descripción de Campos */

    private MProduct m_product = null;
    
    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    public void setRepairOrder( MRepairOrder order ) {
        setClientOrg( order );
        setHeaderInfo( order );
    }    // setRepairOrder

    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    public void setHeaderInfo( MRepairOrder order ) {
    }    // setHeaderInfo

    /**
     * Descripción de Método
     *
     *
     * @param PriceActual
     */

    public void setPrice( BigDecimal PriceActual ) {
        setPriceActual( PriceActual );
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @param PriceActual
     */

    public void setPriceActual( BigDecimal PriceActual ) {
        if( PriceActual == null ) {
            throw new IllegalArgumentException( "PriceActual is mandatory" );
        }

        set_ValueNoCheck( "PriceActual",PriceActual );
    }    // setPriceActual


    /**
     * Descripción de Método
     *
     *
     * @param product
     */

    public void setProduct( MProduct product ) {
        m_product = product;

        if( m_product != null ) {
            setM_Product_ID( m_product.getM_Product_ID());
            setC_UOM_ID( m_product.getC_UOM_ID());
        } else {
            setM_Product_ID( 0 );
            setC_UOM_ID( 0 );
        }

        setM_AttributeSetInstance_ID( 0 );
    }    // setProduct

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     * @param setUOM
     */

    public void setM_Product_ID( int M_Product_ID,boolean setUOM ) {
        if( setUOM ) {
            setProduct( MProduct.get( getCtx(),M_Product_ID ));
        } else {
            super.setM_Product_ID( M_Product_ID );
        }

        setM_AttributeSetInstance_ID( 0 );
    }    // setM_Product_ID

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     * @param C_UOM_ID
     */

    public void setM_Product_ID( int M_Product_ID,int C_UOM_ID ) {
        super.setM_Product_ID( M_Product_ID );
        super.setC_UOM_ID( C_UOM_ID );
        setM_AttributeSetInstance_ID( 0 );
    }    // setM_Product_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProduct getProduct() {
        if( (m_product == null) && (getM_Product_ID() != 0) ) {
            m_product = MProduct.get( getCtx(),getM_Product_ID());
        }

        return m_product;
    }    // getProduct

    /**
     * Descripción de Método
     *
     *
     * @param M_AttributeSetInstance_ID
     */

    public void setM_AttributeSetInstance_ID( int M_AttributeSetInstance_ID ) {
        if( M_AttributeSetInstance_ID == 0 ) {    // 0 is valid ID
            set_Value( "M_AttributeSetInstance_ID",new Integer( 0 ));
        } else {
            super.setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        }
    }                                             // setM_AttributeSetInstance_ID

    

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRepairOrderProduct[" ).append( getID()).append( ",Entered=" ).append( getQtyEntered()).append( "]" );

        return sb.toString();
    }    // toString

    

    /**
     * Descripción de Método
     *
     *
     * @param Qty
     */

    public void setQty( BigDecimal Qty ) {
        super.setQtyEntered( Qty );
    }    // setQty

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
    	
    	if(newRecord==true)
    	{
    		String nombre=getName();
    		if(nombre==null || nombre.length()==0)
    			generateName();
    	}

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {

        return true;
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {

    	return true;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( !success ) {
            return success;
        }

        return true;
    }    // afterDelete
    
    /**
     * Genera el nombre del artículo de reparación, en caso de que no se haya asignado
     * 
     * @return	true si se ha generado el nombre
     */
    protected boolean generateName()
    {
    	boolean dev=true;
    	
    	MProduct articulo=new MProduct(getCtx(), getM_Product_ID(), get_TrxName());
    	if(articulo==null || articulo.getM_Product_ID()==0)
    	{
    		log.warning("generando nombre de articulo en reparación: no se ha podido cargar el articulo");
    		return false;
    	}
    	
    	String nombre=articulo.getName();
    	
    	if(getM_AttributeSetInstance_ID()!=0)
    	{
    		MAttributeSetInstance instancia=new MAttributeSetInstance(getCtx(), getM_AttributeSetInstance_ID(), get_TrxName());
    		if(instancia==null || instancia.getM_AttributeSetInstance_ID()==0)
    		{
        		log.warning("generando nombre de articulo en reparación: no se ha podido cargar la instancia del conjunto de atributos");
        		return false;
        	}
    		
    		nombre+=" ";
    		nombre+=instancia.getDescription();
    	}
    	
    	setName(nombre);
    	
    	return dev;
    }	// generateName

}    // MRepairOrderProduct



/*
 *  @(#)MRepairOrderProduct.java   02.07.07
 * 
 *  Fin del fichero MRepairOrderProduct.java
 *  
 *  Versión 2.2
 *
 */
