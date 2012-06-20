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

public class MRepairOrderLine extends X_C_Repair_Order_Line {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_OrderLine_ID
     * @param trxName
     */

    public MRepairOrderLine( Properties ctx,int C_RepairOrderLine_ID,String trxName ) {
        super( ctx,C_RepairOrderLine_ID,trxName );

        if( C_RepairOrderLine_ID == 0 ) {

            setLineNetAmt( Env.ZERO );

            //

            setPriceEntered( Env.ZERO );
            setPriceActual( Env.ZERO );
            setPriceLimit( Env.ZERO );
            setPriceList( Env.ZERO );

            //

            setM_AttributeSetInstance_ID( 0 );

            //

            setQtyEntered( Env.ZERO );
            setQtyOrdered( Env.ZERO );    // 1


            //

            setIsDescription( false );    // N
            setProcessed( false );
        }
    }	// MRepairOrderLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param order
     */

    public MRepairOrderLine( MRepairOrder order ) {
        this( order.getCtx(),0,order.get_TrxName());

        if( order.getID() == 0 ) {
            throw new IllegalArgumentException( "Header not saved" );
        }

        setC_Repair_Order_ID( order.getC_Repair_Order_ID());    // parent
        setRepairOrder( order );

        // Reset

        setC_Tax_ID( 0 );
        setLine( 0 );
        setC_UOM_ID( 0 );
    }    // MRepairOrderLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRepairOrderLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRepairOrderLine

    /** Descripción de Campos */

    private int m_M_PriceList_ID = 0;

    //

    /** Descripción de Campos */

    private boolean m_IsSOTrx = true;

    // Product Pricing

    /** Descripción de Campos */

    private MProductPricing m_productPrice = null;

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

    public void setRepairOrder( MRepairOrder order ) {
        setClientOrg( order );
        setM_Warehouse_ID( order.getM_Warehouse_ID());
        setDateOrdered( order.getDateOrdered());
        setDatePromised( order.getDatePromised());
        setC_Currency_ID( order.getC_Currency_ID());
        setHeaderInfo( order );
    }    // setRepairOrder

    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    public void setHeaderInfo( MRepairOrder order ) {
        m_precision      = new Integer( order.getPrecision());
        m_M_PriceList_ID = order.getM_PriceList_ID();
        // TODO: ¿son transacciones de ventas siempre?
        // m_IsSOTrx        = order.isSOTrx();
    }    // setHeaderInfo

    /**
     * Descripción de Método
     *
     *
     * @param PriceActual
     */

    public void setPrice( BigDecimal PriceActual ) {
        setPriceEntered( PriceActual );
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
     */

    public void setPrice() {
        if( getM_Product_ID() == 0 ) {
            return;
        }

        if( m_M_PriceList_ID == 0 ) {
            throw new IllegalStateException( "PriceList unknown!" );
        }

        setPrice( m_M_PriceList_ID );
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_ID
     */

    public void setPrice( int M_PriceList_ID ) {
        if( getM_Product_ID() == 0 ) {
            return;
        }

        //

        log.fine( "M_PriceList_ID=" + M_PriceList_ID );
        getProductPricing( M_PriceList_ID );
        setPriceActual( m_productPrice.getPriceStd());
        setPriceList( m_productPrice.getPriceList());
        setPriceLimit( m_productPrice.getPriceLimit());

        //

        if( getQtyEntered().compareTo( getQtyOrdered()) == 0 ) {
            //setPriceEntered( getPriceActual()); Original
            setPriceEntered( getPriceActual());
        } else {
            setPriceEntered( getPriceActual().multiply( getQtyOrdered().divide( getQtyEntered(),BigDecimal.ROUND_HALF_UP )));    // no precision
        }

        // Calculate Discount

        setDiscount( m_productPrice.getDiscount());

        // Set UOM

        setC_UOM_ID( m_productPrice.getC_UOM_ID());
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_ID
     *
     * @return
     */

    private MProductPricing getProductPricing( int M_PriceList_ID ) {
    	MRepairOrder o=new MRepairOrder(getCtx(), getC_Repair_Order_ID(), get_TrxName());
    	
        m_productPrice = new MProductPricing( getM_Product_ID(),o.getC_BPartner_ID(),getQtyOrdered(),m_IsSOTrx );
        m_productPrice.setM_PriceList_ID( M_PriceList_ID );
        m_productPrice.setPriceDate( getDateOrdered());

        //

        m_productPrice.calculatePrice();

        return m_productPrice;
    }    // getProductPrice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean setTax() {
    	MRepairOrder o=new MRepairOrder(getCtx(), getC_Repair_Order_ID(), get_TrxName()); 
        int ii = Tax.get( getCtx(),getM_Product_ID(),/*getC_Charge_ID()*/0,getDateOrdered(),getDateOrdered(),getAD_Org_ID(),getM_Warehouse_ID(),o.getC_BPartner_Location_ID(),    // should be bill to
                          o.getC_BPartner_Location_ID(),m_IsSOTrx );

        if( ii == 0 ) {
            log.log( Level.SEVERE,"No Tax found" );

            return false;
        }

        setC_Tax_ID( ii );

        return true;
    }    // setTax

    /**
     * Descripción de Método
     *
     */

    public void setLineNetAmt() {
        BigDecimal bd = getPriceActual().multiply( getQtyOrdered());
        log.fine("En SetLineNetAmt de MRepairOrderLine, getPriceActual="+getPriceActual()+", getQtyOrdered"+getQtyOrdered());
        if( bd.scale() > getPrecision()) {
            bd = bd.setScale( getPrecision(),BigDecimal.ROUND_HALF_UP );
        }

        super.setLineNetAmt( bd );
    }    // setLineNetAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrecision() {
        if( m_precision != null ) {
            return m_precision.intValue();
        }

        //

        if( getC_Currency_ID() == 0 ) {
            MRepairOrder o = new MRepairOrder( getCtx(),getC_Repair_Order_ID(),get_TrxName());

            setRepairOrder( o );

            if( m_precision != null ) {
                return m_precision.intValue();
            }
        }

        if( getC_Currency_ID() != 0 ) {
            MCurrency cur = MCurrency.get( getCtx(),getC_Currency_ID());

            if( cur.getID() != 0 ) {
                m_precision = new Integer( cur.getStdPrecision());

                return m_precision.intValue();
            }
        }

        // Fallback

        String sql = "SELECT c.StdPrecision " + "FROM C_Currency c INNER JOIN C_Repair_Order x ON (x.C_Currency_ID=c.C_Currency_ID) " + "WHERE x.C_Repair_Order_ID=?";
        int i = DB.getSQLValue( get_TrxName(),sql,getC_Repair_Order_ID());

        m_precision = new Integer( i );

        return m_precision.intValue();
    }    // getPrecision

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
        if( (getM_Warehouse_ID() > 0) && (getM_Warehouse_ID() != M_Warehouse_ID) &&!canChangeWarehouse()) {
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

    public boolean canChangeWarehouse() {
    	/*
        if( getQtyDelivered().signum() != 0 ) {
            log.saveError( "Error",Msg.translate( getCtx(),"QtyDelivered" ) + "=" + getQtyDelivered());

            return false;
        }

        if( getQtyInvoiced().signum() != 0 ) {
            log.saveError( "Error",Msg.translate( getCtx(),"QtyInvoiced" ) + "=" + getQtyInvoiced());

            return false;
        }

        if( getQtyReserved().signum() != 0 ) {
            log.saveError( "Error",Msg.translate( getCtx(),"QtyReserved" ) + "=" + getQtyReserved());

            return false;
        }
        */

        // We can change

        return true;
    }    // canChangeWarehouse

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRepairOrderLine[" ).append( getID()).append( ",Line=" ).append( getLine()).append( ",Ordered=" ).append( getQtyOrdered()).append( "]" );

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
     */

    public void setDiscount() {
        BigDecimal list = getPriceList();

        // No List Price

        if( Env.ZERO.compareTo( list ) == 0 ) {
            return;
        }

        BigDecimal discount = list.subtract( getPriceActual()).multiply( new BigDecimal( 100 )).divide( list,2,BigDecimal.ROUND_HALF_UP );

        setDiscount( discount );
    }    // setDiscount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTaxIncluded() {
        if( m_M_PriceList_ID == 0 ) {
            m_M_PriceList_ID = DB.getSQLValue( get_TrxName(),"SELECT M_PriceList_ID FROM C_Repair_Order WHERE C_Repair_Order_ID=?",getC_Repair_Order_ID());
        }

        MPriceList pl = MPriceList.get( getCtx(),m_M_PriceList_ID,get_TrxName());

        return pl.isTaxIncluded();
    }    // isTaxIncluded

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

        // begin e-evolution vpj 04/11/2004 CMPCS

    	/*
        MMPCMRP.C_OrderLine( this,get_TrxName(),false );
        */

        // end e-evolution vpj 04/11/2004 CMPCS
        // Get Defaults from Parent

        if((getM_Warehouse_ID() == 0) || (getC_Currency_ID() == 0) ) {
            MRepairOrder o = new MRepairOrder( getCtx(),getC_Repair_Order_ID(),get_TrxName());

            setRepairOrder( o );
        }

        if( m_M_PriceList_ID == 0 ) {
            MRepairOrder o = new MRepairOrder( getCtx(),getC_Repair_Order_ID(),get_TrxName());

            setHeaderInfo( o );
        }

        // R/O Check - Product/Warehouse Change

        if( !newRecord && ( is_ValueChanged( "M_Product_ID" ) || is_ValueChanged( "M_Warehouse_ID" ))) {
            if( !canChangeWarehouse()) {
                return false;
            }
        }    // Product Changed

        // Charge

        /*
        if( (getC_Charge_ID() != 0) && (getM_Product_ID() != 0) ) {
            setM_Product_ID( 0 );
        }
        */

        // No Product

        if( getM_Product_ID() == 0 ) {
            setM_AttributeSetInstance_ID( 0 );

            // Product

        } else    // Set/check Product Price
        {

            // Set Price if Actual = 0

            if( (m_productPrice == null) && (Env.ZERO.compareTo( getPriceActual()) == 0) && (Env.ZERO.compareTo( getPriceList()) == 0) ) {
                setPrice();
            }

            // Check if on Price list

            if( m_productPrice == null ) {
                getProductPricing( m_M_PriceList_ID );
            }

            if( !m_productPrice.isCalculated()) {
                log.saveError( "Error",Msg.getMsg( getCtx(),"ProductNotOnPriceList" ));

                return false;
            }
        }

        // UOM

        if( (getC_UOM_ID() == 0) && ( (getM_Product_ID() != 0) || (getPriceEntered().compareTo( Env.ZERO ) != 0) ) ) {
            int C_UOM_ID = MUOM.getDefault_UOM_ID( getCtx());

            if( C_UOM_ID > 0 ) {
                setC_UOM_ID( C_UOM_ID );
            }
        }

        // FreightAmt Not used

        /*
        if( Env.ZERO.compareTo( getFreightAmt()) != 0 ) {
            setFreightAmt( Env.ZERO );
        }
        */

        // Set Tax

        if( getC_Tax_ID() == 0 ) {
            setTax();
        }

        // Get Line No

        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_Repair_Order_Line WHERE C_Repair_Order_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getC_Repair_Order_ID());

            setLine( ii );
        }

        // Calculations & Rounding

        setLineNetAmt();    // extended Amount with or without tax
        setDiscount();

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {

        // R/O Check - Something delivered. etc.

    	/*
        if( Env.ZERO.compareTo( getQtyDelivered()) != 0 ) {
            log.saveError( "DeleteError",Msg.translate( getCtx(),"QtyDelivered" ) + "=" + getQtyDelivered());

            return false;
        }

        if( Env.ZERO.compareTo( getQtyInvoiced()) != 0 ) {
            log.saveError( "DeleteError",Msg.translate( getCtx(),"QtyInvoiced" ) + "=" + getQtyInvoiced());

            return false;
        }

        if( Env.ZERO.compareTo( getQtyReserved()) != 0 ) {

            // For PO should be On Order

            log.saveError( "DeleteError",Msg.translate( getCtx(),"QtyReserved" ) + "=" + getQtyReserved());

            return false;
        }
        */

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

    	/*
        // begin e-evolution vpj 04/11/2004 CMPCS

        MMPCMRP.C_OrderLine( this,get_TrxName(),false );

        // end e-evolution vpj 04/11/2004 CMPCS

        if( !success ) {
            return success;
        }

        if( !newRecord && is_ValueChanged( "C_Tax_ID" )) {

            // Recalculate Tax for old Tax

            MOrderTax tax = MOrderTax.get( this,getPrecision(),true,get_TrxName());    // old Tax

            if( tax != null ) {
                if( !tax.calculateTaxFromLines()) {
                    return false;
                }

                if( !tax.save( get_TrxName())) {
                    return false;
                }
            }
        }
        */

        return updateHeaderTax();
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

        if( getS_ResourceAssignment_ID() != 0 ) {
            MResourceAssignment ra = new MResourceAssignment( getCtx(),getS_ResourceAssignment_ID(),get_TrxName());

            ra.delete( true );
        }

        // begin e-evolution vpj 04/11/2004 CMPCS

        /*
        MMPCMRP.C_OrderLine( this,get_TrxName(),true );
        */

        // end e-evolution vpj 04/11/2004 CMPCS

        return updateHeaderTax();
    }    // afterDelete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updateHeaderTax() {

    	/*
    	// Recalculate Tax for this Tax

        MOrderTax tax = MOrderTax.get( this,getPrecision(),false,get_TrxName());    // current Tax

        if( !tax.calculateTaxFromLines()) {
            return false;
        }

        if( !tax.save( get_TrxName())) {
            return false;
        }

        // Update Order Header

        String sql = "UPDATE C_Order i" + " SET TotalLines=" + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM C_OrderLine il WHERE i.C_Order_ID=il.C_Order_ID) " + "WHERE C_Order_ID=" + getC_Order_ID();
        int no = DB.executeUpdate( sql,get_TrxName());
        log.warning( "Actualizando en mOrderLine =====#" + no + "-------" +sql );

        if( no != 1 ) {
            log.warning( "updateHeaderTax (1) #" + no );
        }

        if( isTaxIncluded()) {
            sql = "UPDATE C_Order i " + " SET GrandTotal=TotalLines " + "WHERE C_Order_ID=" + getC_Order_ID();
        } else {
            sql = "UPDATE C_Order i " + " SET GrandTotal=TotalLines+" + "(SELECT COALESCE(SUM(TaxAmt),0) FROM C_OrderTax it WHERE i.C_Order_ID=it.C_Order_ID) " + "WHERE C_Order_ID=" + getC_Order_ID();
        }

        no = DB.executeUpdate( sql,get_TrxName());
        log.warning( "updateHeaderTax (2) #" + no +"------------" + sql );

        if( no != 1 ) {
            log.warning( "updateHeaderTax (2) #" + no );
        }

        return no == 1;
        */
    	
    	return true;
    }    // updateHeaderTax
    
    /**
     * Comprobacion para saber si una linea de reparacion se debe incluir en un pedido.
     * Se incluira siempre que no esté ya en un pedido.
     * 
     * Se llama desde MRepairOrder.generateOrder()
     * 
     * @return	true si puede ser incluida en un pedido, false en caso contrario
     */
    public boolean includeLineInOrder(boolean esDestinoPresupuesto)
    {
    	boolean dev=true;
    	
    	if(isWarranty()==true)
    		dev=false;
    	
    	if(esDestinoPresupuesto==false)
    	{
    		if(getC_OrderLine_ID()!=0)
    			dev=false;
    	}
    	
    	return dev;
    }
    
}    // MRepairOrderLine



/*
 *  @(#)MRepairOrderLine.java   02.07.07
 * 
 *  Fin del fichero MRepairOrderLine.java
 *  
 *  Versión 2.2
 *
 */
