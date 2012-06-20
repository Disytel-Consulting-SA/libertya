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

import org.openXpertya.process.ProductionSourceGenerator;
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

@SuppressWarnings("serial")
public class MProductionOrderline extends X_C_Production_Orderline {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_OrderLine_ID
     * @param trxName
     */

    public MProductionOrderline( Properties ctx,int C_Production_OrderLine_ID,String trxName ) {
        super( ctx,C_Production_OrderLine_ID,trxName );

        if( C_Production_OrderLine_ID == 0 ) {

            // setC_Order_ID (0);
            // setLine (0);
            // setM_Warehouse_ID (0);  // @M_Warehouse_ID@
            // setC_BPartner_ID(0);
            // setC_BPartner_Location_ID (0);  // @C_BPartner_Location_ID@
            // setC_Currency_ID (0);   // @C_Currency_ID@
            // setDateOrdered (new Timestamp(System.currentTimeMillis()));     // @DateOrdered@
            //
            // setC_Tax_ID (0);
            // setC_UOM_ID (0);
            //



            setM_AttributeSetInstance_ID( 0 );

            //

            setQtyEntered( Env.ZERO );
            setQtyOrdered( Env.ZERO );    // 1

            //

            setIsDescription( false );    // N
            setProcessed( false );
        }
    }                                     // MOrderLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param order
     */

    public MProductionOrderline( MProductionOrder order ) {
        this( order.getCtx(),0,order.get_TrxName());

        if( order.getID() == 0 ) {
            throw new IllegalArgumentException( "Header not saved" );
        }

        setC_Production_Order_ID( order.getC_Production_Order_ID());    // parent
        setOrder( order );

        // Reset

        setLine( 0 );
        setC_UOM_ID( 0 );
    }    // MOrderLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductionOrderline( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MOrderLine

    /** Descripción de Campos */

    private int m_M_PriceList_ID = 0;

    //

    /** Descripción de Campos */

   /* private boolean m_IsSOTrx = true; */

    // Product Pricing

    /** Descripción de Campos */

  /*  private MProductPricing m_productPrice = null; */

    /** Descripción de Campos */

    private Integer m_precision = null; 

    /** Descripción de Campos */

    private MProduct m_product = null;

    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    public void setOrder( MProductionOrder order ) {
        setClientOrg( order );
        setM_Warehouse_ID( order.getM_Warehouse_ID());
        setDateOrdered( order.getDateOrdered());
        setDatePromised( order.getDatePromised());

    }    // setOrder

    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    public void setHeaderInfo( MProductionOrder order ) {
        m_precision      = new Integer( order.getPrecision());
       
    }    // setHeaderInfo

  
 
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
     * @param M_Warehouse_ID
     */

    public void setM_Warehouse_ID( int M_Warehouse_ID ) {
        if( (getM_Warehouse_ID() > 0) && (getM_Warehouse_ID() != M_Warehouse_ID) ) {
            log.severe( "Ignored - Already Delivered/Invoiced/Reserved" );
        } else {
            super.setM_Warehouse_ID( M_Warehouse_ID );
        }
    }    // setM_Warehouse_ID


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MProductionOrderLine[" ).append( getID()).append( ",Line=" ).append( getLine()).append( ",Ordered=" ).append( getQtyOrdered()).append( ",Delivered=" ).append( ",Invoiced=" ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String desc = getDescription();

        if( desc == null ) {
            setDescription( description );
        } else {
            setDescription( desc + " | " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescriptionText() {
        return super.getDescription();
    }    // getDescriptionText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        getProduct();

        if( m_product == null ) {
            return "";
        }

        return m_product.getName();
    }    // getName


    /**
     * Descripción de Método
     *
     *
     * @param Qty
     */

    public void setQty( BigDecimal Qty ) {
        super.setQtyEntered( Qty );
        super.setQtyOrdered( Qty );
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

        if( m_M_PriceList_ID == 0 ) {
            MProductionOrder o = new MProductionOrder( getCtx(),getC_Production_Order_ID(),get_TrxName());

            setHeaderInfo( o );
        }

        // No Product

        if( getM_Product_ID() == 0 ) {
            setM_AttributeSetInstance_ID( 0 );

            // Product

        } 
        

        // UOM
        if( (getC_UOM_ID() == 0) && ( (getM_Product_ID() != 0) ) ) {
            int C_UOM_ID = MUOM.getDefault_UOM_ID( getCtx());

            if( C_UOM_ID > 0 ) {
                setC_UOM_ID( C_UOM_ID );
            }
        }

        // Get Line No

        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_OrderLine WHERE C_Order_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getC_Production_Order_ID());

            setLine( ii );
        }

        // Calculations & Rounding

        // Qty greather than zero
        if (getQtyEntered().compareTo(BigDecimal.ZERO) <= 0) {
        	log.saveError("SaveError", Msg.getMsg(getCtx(),"FieldMustBePositive", new Object[] { Msg.translate(getCtx(), "Qty") }));
        	return false;
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
    	
    	this.eliminarRegistros();
    	
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

        if( !success ) {
            return success;
        }
        
       /*
        * Crea las fuentes necesarias para el producto y reserva. 
        *  
        */
        if(!newRecord){
        	
        	eliminarRegistros();
        	 //  crear las lineas de los fuentes 
        	ProductionSourceGenerator gen = new ProductionSourceGenerator();
        	gen.procesarLineasDeProduccion(this.getC_Production_Orderline_ID());
        }

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

        return true;
    }    // afterDelete
    
    
    private boolean eliminarRegistros(){
    	
    	/* Elimina los registro fuentes de artículos */
    	StringBuffer borrar= new StringBuffer("DELETE FROM m_productionlinesource WHERE c_production_orderline_id="+ this.getC_Production_Orderline_ID());
		@SuppressWarnings("unused")
		int borrados = DB.executeUpdate(borrar.toString(), get_TrxName());
		return true;
    }
    
    /**
     * Crea las líneas de un pedido a partir de esta línea de producción. Por cada
     * material de la lista del producto, crea una línea en el pedido indicado.
     */
    protected void createBOMOrderLines(MOrder order) throws Exception {
        // Se obtiene la lista de materiales del producto a producir.
    	MProductBOM[] boms   = MProductBOM.getBOMLines(getProduct());

    	// Por cada material, se crea una línea en el pedido de salida.
        for( int j = 0;j < boms.length;j++ ) {
            MProductBOM bom     = boms[j];
            MOrderLine  newLine = new MOrderLine(order);

            newLine.setLine(0); // set in beforeSave
            newLine.setM_Product_ID( bom.getProduct().getM_Product_ID());
            newLine.setC_UOM_ID( bom.getProduct().getC_UOM_ID());
            newLine.setQty( getQtyOrdered().multiply( bom.getBOMQty()));
            newLine.setDescription(Msg.parseTranslation(getCtx(), "@Production@: " + getProduct().getName()));
            newLine.setPrice();
            if(!newLine.save()) {
            	throw new Exception("@BOMOrderLineCreateError@: " + MProductionOrder.getError());
            }
        }
    }

    /**
     * Crea las líneas de un remito a partir de esta línea de producción. Por cada
     * material de la lista del producto, crea una línea en el remito indicado.
     */
    protected void createBOMInOutLines(MInOut inout) throws Exception {
        // Se obtiene la lista de materiales del producto a producir.
    	MProductBOM[] boms   = MProductBOM.getBOMLines(getProduct());

    	// Por cada material, se crea una línea en el pedido de salida.
        for( int j = 0;j < boms.length;j++ ) {
            MProductBOM bom     = boms[j];
            MInOutLine  newLine = new MInOutLine(inout);

            newLine.setLine(0); // set in beforeSave
            newLine.setM_Product_ID( bom.getProduct().getM_Product_ID());
            newLine.setC_UOM_ID( bom.getProduct().getC_UOM_ID());
            newLine.setQty( getQtyEntered().multiply( bom.getBOMQty()));
            newLine.setDescription(Msg.parseTranslation(getCtx(), "@Production@: " + getProduct().getName() + " - @Qty@ = " + getQtyEntered()));
            newLine.setM_Locator_ID(getM_Locator_ID());
            if(!newLine.save()) {
            	throw new Exception("@BOMInOutLineCreateError@: " + MProductionOrder.getError());
            }
        }
    }

    /**
     * Crea la línea del remito de entrada correspondiente a esta línea de producción.
     * La línea del remito representa la entrada de la cantidad de unidades indicadas
     * en esta línea de producción, para el artículo de la misma.
     */
    protected void createProductIncomeLine(MInOut inout) throws Exception {
        MInOutLine  newLine = new MInOutLine(inout);

        newLine.setLine(0); // set in beforeSave
        newLine.setM_Product_ID(getM_Product_ID());
        newLine.setC_UOM_ID(getC_UOM_ID());
        newLine.setQty(getQtyEntered());
        newLine.setM_Locator_ID(getM_Locator_ID());
        if(!newLine.save()) {
        	throw new Exception("@ProductsIncomeLineCreateError@: " + MProductionOrder.getError());
        }
    }
    
}    // MOrderLine



/*
 *  @(#)MProductionOrderLine.java   02.07.07
 * 
 *  Fin del fichero MOrderLine.java
 *  
 *  Versión 2.2
 *
 */
