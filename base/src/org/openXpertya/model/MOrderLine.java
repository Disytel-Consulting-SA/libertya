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
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayUtil;
import org.openXpertya.util.Env;
import org.openXpertya.util.MProductCache;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MOrderLine extends X_C_OrderLine {

	/** Especifica si la línea debe actualizar los impuestos 
	 *  en la cabecera (en caso que esto sea posible) */
	boolean shouldUpdateHeader = true;

	/**
	 * Id de descuento interno de línea manual del discount calculator, aplicado
	 * en esta línea. Sólo para TPV
	 */
	private Integer lineManualDiscountID;
	
	/**
	 * Flag para controlar el stock
	 */
	private boolean controlStock = true;
	
	/** Bypass para que no actualice el precio al guardar la línea */
	private boolean updatePriceInSave = true;
	
	/** Bypass para no controlar las cantidades mínimas ni de empaquetado */
	private boolean allowAnyQty = false;
	
	private Integer tpvGeneratedInvoiceLineID = 0; 
		
	/**
	 * Lugar de Retiro. Utilizado para evitar reserva de stock en pedidos que se
	 * retiran por TPV. Por defecto el lugar de retiro es Almacén lo cual
	 * implica que para esta línea se hará la reserva de stock normalmente. Si
	 * el lugar de retiro se setea a TPV entonces no se hará la reserva de
	 * stock. No se requiere persistir este dato. <br>
	 * Modificado por Matías Cap 20120328 - Ahora sí se requiere persistir el
	 * dato por el tema de la impresión de salida de depósito, ya que en el
	 * framework de impresión fiscal no tenemos el lugar de salida en el PO
	 */
	// private String checkoutPlace = MProduct.CHECKOUTPLACE_Warehouse;
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_OrderLine_ID
     * @param trxName
     */
	
    public MOrderLine( Properties ctx,int C_OrderLine_ID,String trxName ) {
        super( ctx,C_OrderLine_ID,trxName );

        if( C_OrderLine_ID == 0 ) {

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

            setFreightAmt( Env.ZERO );
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
            setQtyDelivered( Env.ZERO );
            setQtyInvoiced( Env.ZERO );
            setQtyReserved( Env.ZERO );

            //

            setIsDescription( false );    // N
            setProcessed( false );
            
            setCheckoutPlace(CHECKOUTPLACE_Warehouse);
        }
    }                                     // MOrderLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param order
     */

    public MOrderLine( MOrder order ) {
        this( order.getCtx(),0,order.get_TrxName());

        if( order.getID() == 0 ) {
            throw new IllegalArgumentException( "Header not saved" );
        }

        setC_Order_ID( order.getC_Order_ID());    // parent
        setOrder( order );

        // Reset

        setC_Tax_ID( 0 );
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

    public MOrderLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MOrderLine

    /** Descripción de Campos */

    public int m_M_PriceList_ID = 0;

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
    
    private MOrder m_order = null;

    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    public void setOrder( MOrder order ) {
        setClientOrg( order );
        setC_BPartner_ID( order.getC_BPartner_ID());
        setC_BPartner_Location_ID( order.getC_BPartner_Location_ID());
        setM_Warehouse_ID( order.getM_Warehouse_ID());
        setDateOrdered( order.getDateOrdered());
        setDatePromised( order.getDatePromised());
        setC_Currency_ID( order.getC_Currency_ID());
        setHeaderInfo( order );
    }    // setOrder

    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    public void setHeaderInfo( MOrder order ) {
        m_precision      = new Integer( order.getPrecision());
        m_M_PriceList_ID = order.getM_PriceList_ID(); 
        m_IsSOTrx        = order.isSOTrx();
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
        m_productPrice = new MProductPricing( getM_Product_ID(),getC_BPartner_ID(),getQtyOrdered(),m_IsSOTrx );
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
    	int ii  = 0;
        // Si los Comprobantes fiscales están activos se busca la tasa de impuesto a partir de la categoría de IVA debe estar condicionado 
        if (CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
        	ii = DB.getSQLValue( null,"SELECT C_Tax_ID FROM C_Categoria_Iva ci INNER JOIN C_BPartner bp ON (ci.C_Categoria_Iva_ID = bp.C_Categoria_Iva_ID) WHERE bp.C_BPartner_ID = ?",getC_BPartner_ID() );
        }
        
        if( ii == 0 ) {
        	ii = Tax.get( getCtx(),getM_Product_ID(),getC_Charge_ID(),getDateOrdered(),getDateOrdered(),getAD_Org_ID(),getM_Warehouse_ID(),getC_BPartner_Location_ID(),    // should be bill to
                    getC_BPartner_Location_ID(),m_IsSOTrx );
        }

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
        log.fine("En SetLineNetAmt de MOrderLine, getPriceActual="+getPriceActual()+", getQtyOrdered"+getQtyOrdered());
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
            MOrder o = new MOrder( getCtx(),getC_Order_ID(),get_TrxName());

            setOrder( o );

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

        String sql = "SELECT c.StdPrecision " + "FROM C_Currency c INNER JOIN C_Order x ON (x.C_Currency_ID=c.C_Currency_ID) " + "WHERE x.C_Order_ID=?";
        int i = DB.getSQLValue( get_TrxName(),sql,getC_Order_ID());

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
        	//Ader: soporte para caches-multidocumento
        	if (m_prodCache!= null)
        		m_product = m_prodCache.get(getM_Product_ID());
        	else
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
        if( getQtyDelivered().signum() != 0 ) {
            log.saveError( "Error",Msg.translate( getCtx(),"QtyDelivered" ) + "=" + getQtyDelivered());

            return false;
        }

//        Comentado: No tiene sentido limitar el cambio si el Pedido sólo está facturado
//        """"""""""
//        if( getQtyInvoiced().signum() != 0 ) {
//            log.saveError( "Error",Msg.translate( getCtx(),"QtyInvoiced" ) + "=" + getQtyInvoiced());
//
//            return false;
//        }

        if( getQtyReserved().signum() != 0 ) {
            log.saveError( "Error",Msg.translate( getCtx(),"QtyReserved" ) + "=" + getQtyReserved());

            return false;
        }

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
        StringBuffer sb = new StringBuffer( "MOrderLine[" ).append( getID()).append( ",Line=" ).append( getLine()).append( ",Ordered=" ).append( getQtyOrdered()).append( ",Delivered=" ).append( getQtyDelivered()).append( ",Invoiced=" ).append( getQtyInvoiced()).append( ",Reserved=" ).append( getQtyReserved()).append( "]" );

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
    
    public BigDecimal calculatePrice(BigDecimal discount) {
		BigDecimal cPrice = getPriceList();
		if(discount != null) {
			cPrice = cPrice.subtract(cPrice.multiply(discount.divide(new BigDecimal(100),10,BigDecimal.ROUND_HALF_UP)));
		}
		return cPrice;
	}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTaxIncluded() {
        if( m_M_PriceList_ID == 0 ) {
            m_M_PriceList_ID = DB.getSQLValue( get_TrxName(),"SELECT M_PriceList_ID FROM C_Order WHERE C_Order_ID=?",getC_Order_ID());
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

//        MMPCMRP.C_OrderLine( this,get_TrxName(),false );

        // end e-evolution vpj 04/11/2004 CMPCS
        // Get Defaults from Parent

        MOrder o = new MOrder( getCtx(),getC_Order_ID(),get_TrxName());
        MDocType orderDocType = MDocType.get(getCtx(), o.getC_DocTypeTarget_ID());
        		
        // En caso de tener una preferencia en el campo m_warehouse_id de la cabecera, se setea incorrectamente
        // ese valor por más de que se haya especificado un valor diferente en dicho campo.  Por lo tanto se
        // fuerza el almacén en la línea a partir del almacén de la cabecera. 
        if (getM_Warehouse_ID() != o.getM_Warehouse_ID())
        	setM_Warehouse_ID(o.getM_Warehouse_ID());
        
        if( (getC_BPartner_ID() == 0) || (getC_BPartner_Location_ID() == 0) || (getM_Warehouse_ID() == 0) || (getC_Currency_ID() == 0) ) {
            setOrder( o );
        }

        if( m_M_PriceList_ID == 0 ) {
            setHeaderInfo( o );
        }

        // R/O Check - Product/Warehouse Change

        if( !newRecord && ( is_ValueChanged( "M_Product_ID" ) || is_ValueChanged( "M_Warehouse_ID" ))) {
            if( !canChangeWarehouse()) {
                return false;
            }
        }    // Product Changed

        // Charge

        if( !o.isSOTrx() && (getC_Charge_ID() != 0) && (getM_Product_ID() != 0) ) {
        	log.saveError("Error", Msg.getMsg( getCtx(),"ChargeExclusively" ));
        	return false;
        }
        
        if( (getC_Charge_ID() != 0) && (getM_Product_ID() != 0) ) {
            setM_Product_ID( 0 );
        }

        // No Product

        if( getM_Product_ID() == 0 ) {
            setM_AttributeSetInstance_ID( 0 );

            // Product

        } else    // Set/check Product Price
        {        	
        	if(isUpdatePriceInSave()){
	        	// Validación de precios positivos.
	        	if(getPriceActual().compareTo(BigDecimal.ZERO) < 0) {
	        		log.saveError( "Error",Msg.getMsg( getCtx(),"PriceUnderZero" ));
	        		return false;
	        	}
	        	
	        	if(getPriceList().compareTo(BigDecimal.ZERO) < 0) {
	        		log.saveError( "Error",Msg.parseTranslation( getCtx(),"@PriceUnderZero@ (@PriceList@)" ));
	        		return false;
	        	}
	        	
	        	// Para pedidos a proveedor no se permite tener artículos repetidos
	        	String whereClause = "c_order_id = ? AND m_product_id = ?";
	        	whereClause += newRecord?"":" AND c_orderline_id <> "+getC_OrderLine_ID();
				if (MDocType.DOCTYPE_PurchaseOrder.equals(orderDocType.getDocTypeKey())
						&& PO.existRecordFor(getCtx(), get_TableName(),
								whereClause, new Object[] { getC_Order_ID(), getM_Product_ID() },
								get_TrxName())) {
	        		log.saveError("AlreadyExistsProductInADocumentLine", "");
	        		return false;
	        	}
	        	
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
            
            // Verificación de restricción del lugar de retiro del artículo según lo indicado
            // por el tipo de documento.
            if(orderDocType.isCheckoutPlaceRestricted()
            		&& !o.isProcessed() 
            		&& MProduct.CHECKOUTPLACE_PointOfSale.equals(getProduct().getCheckoutPlace())) {
                    
            		log.saveError("SaveError", Msg.translate(Env.getCtx(), "InvalidProductCheckoutPlaceError"));
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

        if( Env.ZERO.compareTo( getFreightAmt()) != 0 ) {
            setFreightAmt( Env.ZERO );
        }

        // Set Tax

        if( getC_Tax_ID() == 0 ) {
            setTax();
        }

        // Get Line No

        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_OrderLine WHERE C_Order_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getC_Order_ID());

            setLine( ii );
        }
        
        
        updateDragOrderDiscounts();
        
        // Calculations & Rounding

        setLineNetAmt();    // extended Amount with or without tax
        setDiscount();
        setLineTotalAmt();

        // M_AttributeSetInstance_ID
        
        /*
        if (getM_AttributeSetInstance_ID() == 0 && shouldSetAttrSetInstance()) {
        	log.saveError("FillMandatory", Msg.translate( getCtx(),"M_AttributeSetInstance_ID" ));
        	return false;
        }
        */
        
        /*
         * Añade una comprobacion en el metodo beforSave 
         * de las lineas de pedido y albaran, para que en 
         * las transacciones de venta, no se pueda guardar 
         * seleccionar un conjunto de atributos cuyo 
         * stockage sea menor que el indicado en la linea.
         */
        if (isControlStock() && o.isSOTrx() && getM_AttributeSetInstance_ID() != 0) {
	        // BigDecimal avQty = (BigDecimal)DB.getSQLObject(get_TrxName(), "SELECT COALESCE(SUM(QtyOnHand-QtyReserved), 0.0) FROM M_Storage INNER JOIN M_Locator ON (M_Locator.M_Warehouse_ID=M_Storage.M_Locator_ID) WHERE ? IN (M_AttributeSetInstance_ID,0) AND M_Product_ID = ? AND M_Locator.M_Warehouse_ID = ? ", new Object[]{getM_AttributeSetInstance_ID(), getM_Product_ID(), getM_Warehouse_ID()});
        	// BigDecimal avQty = MStorage.get(getCtx(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), get_TrxName());
        	BigDecimal avQty = MStorage.getQtyAvailable(getM_Warehouse_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), get_TrxName());
	        if (avQty.compareTo(getQtyEntered()) < 0) {
	        	log.saveError("NotEnoughStocked", "");
	        	return false;
	        }
        }
        
        /* Si el project no está seteado, tomar el de la cabecera */
        if (getC_Project_ID() == 0)
        	// setC_Project_ID(DB.getSQLValue(get_TrxName(), " SELECT C_Project_ID FROM C_Order WHERE C_Order_ID = " + getC_Order_ID()));
        	setC_Project_ID(o.getC_Project_ID());
        
        // Controlar cantidades por unidad de medida
        if(!MUOM.isAllowedQty(getCtx(), getC_UOM_ID(), getQtyEntered(), get_TrxName())){
			log.saveError(Msg.getMsg(getCtx(), "UOMNotAllowedQty",
					new Object[] { MUOM.get(getCtx(), getC_UOM_ID()).getName(),
							getQtyEntered() }), "");
			return false;
        }
        
		// Controlar que la cantidad no sea menor a la mínima de compra y que la
		// cantidad sea múltiplo a ordenar 
        if(!o.isSOTrx()
				&& orderDocType.getDocTypeKey().equals(MDocType.DOCTYPE_PurchaseOrder)
				&& o.getDocStatus().equals(MInOut.DOCSTATUS_Drafted)){
			MProductPO ppo = MProductPO.get(getCtx(), getM_Product_ID(), o.getC_BPartner_ID(), get_TrxName());
			if (!isAllowAnyQty() && ppo != null && ppo.isActive()) {
				// Cantidad mínima
				if(ppo.getOrder_Min().compareTo(getQtyEntered()) > 0){
	        		MProduct prod = MProduct.get(getCtx(), getM_Product_ID());
					log.saveError("SaveError", Msg.getMsg(getCtx(), "QtyEnteredLessThanOrderMinQty",
							new Object[] { prod.getValue(), prod.getName(), ppo.getOrder_Min(), getQtyEntered() }));
					return false;
				}
				// Múltiplo a ordenar
				if (!Util.isEmpty(ppo.getOrder_Pack(), true)
						&& getQtyEntered().remainder(ppo.getOrder_Pack()).compareTo(BigDecimal.ZERO) != 0) {
					MProduct prod = MProduct.get(getCtx(), getM_Product_ID());
					log.saveError("SaveError", Msg.getMsg(getCtx(), "QtyEnteredMustBeMultipleOfOrderPack",
							new Object[] { prod.getValue(), prod.getName(), ppo.getOrder_Pack(), getQtyEntered() }));
					return false;
				}
        	}
			// Si el tipo de documento está marcado para que sólo permita
			// artículos del proveedor y el artículo no corresponde con el
			// proveedor del pedido, entonces error
			if(orderDocType.isOnlyVendorProducts() && (ppo == null || !ppo.isActive())){
				MProduct prod = MProduct.get(getCtx(), getM_Product_ID());
				log.saveError("SaveError",
						Msg.getMsg(getCtx(), "OnlyVendorProducts") + " " + prod.getValue() + " - " + prod.getName());
				return false;
			}
        }
        
        return true;
    }    // beforeSave

    
    /**
     * Actualiza los descuentos arrastrados del pedido a nivel de línea y documento 
     */
    public void updateDragOrderDiscounts(){
		if ((is_ValueChanged("QtyEntered") || is_ValueChanged("PriceList"))
			 && existRecordFor(getCtx(),
							MDocumentDiscount.Table_Name, "c_order_id = ?", new Object[] { getC_Order_ID() }, get_TrxName())) {
			BigDecimal totalPriceList = getPriceList().multiply(getQtyOrdered());
			// Si es 0, el precio o la cantidad se modificaron a 0, entonces se
			// debe eliminar los document discounts existentes y setear a 0 los
			// importes de descuentos de la línea
			if(totalPriceList.compareTo(BigDecimal.ZERO) == 0){
				setLineDiscountAmt(BigDecimal.ZERO);
				setLineBonusAmt(BigDecimal.ZERO);
				setDocumentDiscountAmt(BigDecimal.ZERO);
			}
			// Importe base mayor a 0, se modifican los descuentos arrastrados
			// del pedido, si no tenía antes, se crean
			else{
				Integer tmpPrecision = 10;
				List<MDocumentDiscount> lineDiscounts = MDocumentDiscount.get(
						"C_OrderLine_ID = ?",
						new Object[] { getID() }, getCtx(), get_TrxName());
				// Si existen líneas, se actualizan los importes de cada una de
				// ellas, junto con los importes de la línea
				if(lineDiscounts.size() > 0){
					BigDecimal lineDiscountAmt = BigDecimal.ZERO;
					BigDecimal bonusDiscountAmt = BigDecimal.ZERO;
					BigDecimal documentDiscountAmt = BigDecimal.ZERO;
					BigDecimal discountAmt;
					for (MDocumentDiscount mDocumentDiscount : lineDiscounts) {
						discountAmt = Util.getRatedAmt(totalPriceList, mDocumentDiscount.getDiscountAmt(),
								mDocumentDiscount.getDiscountBaseAmt(), tmpPrecision);
						// Si es de documento, actualizo el de documento
						if (MDocumentDiscount.CUMULATIVELEVEL_Document.equals(mDocumentDiscount.getCumulativeLevel())) {
							documentDiscountAmt = documentDiscountAmt.add(discountAmt);
							getOrder().setUpdateChargeAmt(true);
						}
						// Sino en el de línea
						else if (MDocumentDiscount.DISCOUNTAPPLICATION_Bonus.equals(mDocumentDiscount.getDiscountApplication())) {
							bonusDiscountAmt = bonusDiscountAmt.add(discountAmt);				
						}
						else{
							lineDiscountAmt = lineDiscountAmt.add(discountAmt);
						}
					}
					
					setLineDiscountAmt(lineDiscountAmt);
					setLineBonusAmt(bonusDiscountAmt);
					setDocumentDiscountAmt(documentDiscountAmt);
				}
				// Si no existen entonces dejo la forma antigua
				else{
					updateDragOrderDiscountsOld();
				}
			}
        }
    }
    
    /**
     * Actualiza los descuentos de la línea de la forma antigua.
     * @deprecated
     */
    private void updateDragOrderDiscountsOld(){
		BigDecimal oldQty = is_ValueChanged("QtyEntered")
				&& BigDecimal.ZERO
						.compareTo((BigDecimal) get_ValueOld("QtyEntered")) != 0 ? (BigDecimal) get_ValueOld("QtyEntered")
				: getQtyEntered();
		BigDecimal oldPrice = is_ValueChanged("PriceEntered") 
				&& BigDecimal.ZERO
						.compareTo((BigDecimal) get_ValueOld("PriceEntered")) != 0 ? (BigDecimal) get_ValueOld("PriceEntered")
				: getPriceEntered();
		BigDecimal oldAmount = oldPrice.multiply(oldQty);
		BigDecimal actualAmount = getPriceEntered().multiply(getQtyEntered());
		Integer tmpPrecision = 10;
		BigDecimal totalPriceList = getPriceList().multiply(getQtyEntered());
		BigDecimal oldTotalPriceList = getPriceList().multiply(oldQty);
    	
		if(!Util.isEmpty(getDocumentDiscountAmt(), true)){
			BigDecimal documentDiscountRate = Util.getDiscountRate(
					oldAmount, getDocumentDiscountAmt(), tmpPrecision);
			setDocumentDiscountAmt((actualAmount
					.multiply(documentDiscountRate)).setScale(2,
					BigDecimal.ROUND_HALF_UP));
			getOrder().setUpdateChargeAmt(true);
    	}
    	
		if(!Util.isEmpty(getLineDiscountAmt(), true)){
			BigDecimal lineDiscountRate = Util.getDiscountRate(
					oldTotalPriceList, getLineDiscountAmt(),
					tmpPrecision);
			setLineDiscountAmt((totalPriceList.multiply(lineDiscountRate))
					.setScale(2,BigDecimal.ROUND_HALF_UP));
    	}
    	
		if(!Util.isEmpty(getLineBonusAmt(), true)){
			BigDecimal bonusDiscountRate = Util.getDiscountRate(
					oldTotalPriceList, getLineBonusAmt(),
					tmpPrecision);
			setLineBonusAmt((totalPriceList.multiply(bonusDiscountRate))
					.setScale(2, BigDecimal.ROUND_HALF_UP));
    	}
    }
    
    public boolean shouldSetAttrSetInstance() {
    	return shouldSetAttrSetInstance(null);
    }
    
    private boolean shouldSetAttrSetInstance(MOrder o) {
    	int ProductID = getM_Product_ID();
    	
    	if (ProductID == 0)
    		return false;
    	
    	/*
    	 * Modificar C_OrderLine y M_InOutLine para que compruebe que se ha introducido el conjunto de atributos, 
    	 * antes de permitir grabar si producto tiene configurado conjunto de atributos y se deben establacer.
    	 */
    	boolean pnia = MAttributeSet.ProductNeedsInstanceAttribute(ProductID, get_TrxName());
    	
    	if (!pnia)
    		return false;
    	
    	/*
    	int DocTypeID = DB.getSQLValue(get_TrxName(), "SELECT c_doctypetarget_id FROM c_order WHERE c_order_id = ?", getC_Order_ID());
    	MDocType DocType = MDocType.get(getCtx(), DocTypeID);
    	
    	
    	//  Modificar C_OrderLine para que haga el mismo tratamiento con los atributos en caso de que el pedido 
    	//  sea �Pedido a Credito� o �Pedido de Almacen�
    	if (DocType.getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder) && 
    			(DocType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_WarehouseOrder) || DocType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_OnCreditOrder)))
    		return true;
    	*/
    	
    	if (o == null)
    		o = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
    	
    	MAttributeSet.CondicionesCasos cc = MAttributeSet.GetCondicionesAtributos(MAttributeSet.GetCasoByTableName(null, this, o.getC_DocTypeTarget_ID(), o.isSOTrx()));
    	
    	return cc.isAtributeSetInstenceMandatory();
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {

        // R/O Check - Something delivered. etc.

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

        // begin e-evolution vpj 04/11/2004 CMPCS

//        MMPCMRP.C_OrderLine( this,get_TrxName(),false );

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

        // si a esta instancia le corresponde actualizar el encabezado, pero el
        // el header se encuentra completado, no es posible modificar la cabecera
        return !shouldUpdateHeader || (!isHeaderUpdateable(MOrder.Table_Name, getC_Order_ID()) || updateHeader());
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

//        MMPCMRP.C_OrderLine( this,get_TrxName(),true );

        // end e-evolution vpj 04/11/2004 CMPCS

        // si a esta instancia le corresponde actualizar el encabezado, pero el
        // el header se encuentra completado, no es posible modificar la cabecera
        return !shouldUpdateHeader || (!isHeaderUpdateable(MOrder.Table_Name, getC_Order_ID()) || updateHeader());
    }    // afterDelete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updateHeader() {

        // Recalculate Tax for this Tax

        MOrderTax tax = MOrderTax.get( this,getPrecision(),false,get_TrxName());    // current Tax

        if( !tax.calculateTaxFromLines()) {
            return false;
        }

        if( !tax.save( get_TrxName())) {
            return false;
        }
        // Recalcula los importes del encabezado del pedido.
        if (getOrder().updateAmounts()) {
        	return getOrder().save(); 
        } else {
        	return false;
        }
        
    }    // updateHeaderTax
    
    public String getProductName()
    {
    	if (getM_Product_ID() > 0){
    		MProduct prod = new MProduct(p_ctx, getM_Product_ID(), null); 
    		return getDescription() == null?prod.getName():prod.getName()+ " - " + getDescription();
    	}
    	return getDescription();
    }
    
    public String getProductNameOnly()
    {
    	if (getM_Product_ID() > 0){
    		MProduct prod = new MProduct(p_ctx, getM_Product_ID(), null); 
    		return prod.getName();
    	}
    	return getDescription();
    }
    
    public String getProductValue()
    {
    	if (getProduct() != null)
    		return getProduct().getValue();
    	return "";
    }    
    
    public String getProductUPC()
    {
    	if (getProduct() != null)
    		return getProduct().getUPC();
    	return "";
    }   

    public String getUOMName()
    {
    	if (getC_UOM_ID() > 0)
    		return (new MUOM(p_ctx, getC_UOM_ID(), null)).getName();
    	return "";
    }
    
    public String getX12DE355()
    {
    	if (getC_UOM_ID() > 0)
    		return (new MUOM(p_ctx, getC_UOM_ID(), null)).getX12DE355();
    	return "";
    }
    
    public String getLineStr()
    {
    	return "" + getLine();
    }    
    
    public BigDecimal getTotalLineNoDsc()
    {
    	return getPriceList().multiply(getQtyEntered());
    }
  
    // redefinido, según sea issotrx o no
    // si es de venta utiliza el precio de lista
    // si es de compra utiliza el precio ingresado a mano
    public BigDecimal getPriceList2()
    {
    	if (m_IsSOTrx)
    		return getPriceList();
    	else
    		return getPriceEntered();    		
    }
    
    /**
     * @return Indica si la línea contiene artículos que aún no han sido entregados.
     */
    public boolean hasNotDeliveredProducts() {
    	return getPendingDeliveredQty().compareTo(BigDecimal.ZERO) != 0;
    }

	/**
	 * @return la diferencia entre la cantidad pedida y la cantidad entregada
	 */
    public BigDecimal getPendingDeliveredQty(){
    	return getQtyOrdered().subtract(getQtyDelivered()).subtract(getQtyTransferred());
    }

	/**
	 * @return true si esta línea es imprimible por el retiro de depósito, false
	 *         caso contrario
	 */
    public boolean isDeliverDocumentPrintable(){
		return getCheckoutPlace() != null
				&& getCheckoutPlace()
						.equals(MOrderLine.CHECKOUTPLACE_Warehouse)
				&& hasNotDeliveredProducts();
    }

	/**
	 * Crea el wrapper de esta línea para ser manipulada por un calculador de
	 * descuentos.
	 * 
	 * @param order
	 *            Wrapper del pedido que contiene esta línea
	 * @return Nueva instancia del wrapper
	 */
    protected IDocumentLine createDiscountableWrapper(IDocument order) {
    	return new DiscountableMOrderLineWrapper(order);
    }

	/**
	 * Devuelve la instancia del pedido al cual pertenece esta línea
	 * 
	 * @param reload
	 *            <code>true</code> para recargar la instancia desde la BD
	 * @return {@link MOrder}
	 */
    public MOrder getOrder(boolean reload) {
    	if (m_order == null || reload) {
    		m_order = new MOrder(getCtx(), getC_Order_ID(), get_TrxName()); 
    	}
    	return m_order; 
    }

	/**
	 * Devuelve la instancia del pedido al cual pertenece esta línea. No recarga
	 * la instancia desde la BD en caso de que ya haya sido utilizado este métod
	 * (devuelve la referencia que tiene actualmente esta línea)
	 * 
	 * @return {@link MOrder}
	 */
    public MOrder getOrder() {
    	return getOrder(false);
    }
    
    public BigDecimal setLineTotalAmt() {
    	if (getLineNetAmt() == null || getLineNetAmt().compareTo(BigDecimal.ZERO) == 0) {
    		setLineNetAmt();
    	}
    	BigDecimal lineTaxAmt = BigDecimal.ZERO;
    	if (getC_Tax_ID() > 0) {
    		MTax tax = new MTax( getCtx(),getC_Tax_ID(),get_TrxName());
    		lineTaxAmt = tax.calculateTax( getLineNetAmt(),isTaxIncluded(),getPrecision());
    	}
    	BigDecimal lineTotalAmt = getLineNetAmt().add(lineTaxAmt);
    	setLineTotalAmt(lineTotalAmt);
    	return lineTotalAmt;
    }
    
    /**
     * @return importe de impuesto de la línea actual
     */
    public BigDecimal getTaxAmt(){
    	BigDecimal lineTaxAmt = BigDecimal.ZERO;
    	if (getC_Tax_ID() > 0) {
    		MTax tax = new MTax( getCtx(),getC_Tax_ID(),get_TrxName());
    		lineTaxAmt = tax.calculateTax( getTotalPriceEnteredNet(),isTaxIncluded(),getPrecision());
    	}
    	return lineTaxAmt;
    }
    
    /**
     * Wrapper de {@link MOrderLine} para cálculo de descuentos.
     */
    private class DiscountableMOrderLineWrapper extends DiscountableDocumentLine {
    	
		public DiscountableMOrderLineWrapper(IDocument document) {
			super(document);
		}

		@Override
		public BigDecimal getPrice() {
			return MOrderLine.this.getPriceActual();
		}

		@Override
		public BigDecimal getPriceList() {
			return MOrderLine.this.getPriceList();
		}

		@Override
		public int getProductID() {
			return MOrderLine.this.getM_Product_ID();
		}

		@Override
		public BigDecimal getQty() {
			return MOrderLine.this.getQtyEntered();
		}

		@Override
		public void setPrice(BigDecimal newPrice) {
			MOrderLine.this.setPrice(newPrice);		
		}
		
		@Override
		public void setDocumentDiscountAmt(BigDecimal discountAmt) {
			MOrderLine.this.setDocumentDiscountAmt(discountAmt);
			if (!MOrderLine.this.save()) {
				log.severe("Cannot save discounted Order Line");
			}
		}
		
		@Override
		public BigDecimal getLineDiscountAmt() {
			return MOrderLine.this.getLineDiscountAmt();
		}

		@Override
		public void setLineDiscountAmt(BigDecimal lineDiscountAmt) {
			MOrderLine.this.setLineDiscountAmt(lineDiscountAmt);
		}

		@Override
		public BigDecimal getLineBonusAmt() {
			return MOrderLine.this.getLineBonusAmt();
		}

		@Override
		public void setLineBonusAmt(BigDecimal lineBonusAmt) {
			MOrderLine.this.setLineBonusAmt(lineBonusAmt);			
		}
		
		@Override
		public BigDecimal getTaxRate() {
			return MOrderLine.this.getTaxRate();
		}
		
		@Override
		public boolean isTaxIncluded() {
			return MOrderLine.this.isTaxIncluded();
		}

		@Override
		public void setDiscount(BigDecimal discount) {
			MOrderLine.this.setDiscount(discount);
		}

		@Override
		public BigDecimal getDiscount() {
			return MOrderLine.this.getDiscount();
		}

		@Override
		public Integer getLineManualDiscountID() {
			return MOrderLine.this.getLineManualDiscountID();
		}

		@Override
		public void setLineManualDiscountID(Integer lineManualDiscountID) {	
			MOrderLine.this.setLineManualDiscountID(lineManualDiscountID);
		}

		@Override
		public void setDocumentReferences(MDocumentDiscount documentDiscount) {
			documentDiscount.setC_OrderLine_ID(MOrderLine.this.getID());
			documentDiscount.setC_InvoiceLine_ID(MOrderLine.this.getTpvGeneratedInvoiceLineID());
		}

		@Override
		public BigDecimal getDocumentDiscountAmt() {
			return MOrderLine.this.getDocumentDiscountAmt();
		}

		@Override
		public BigDecimal getTemporalTotalDocumentDiscountAmt() {
			// TODO Auto-generated method stub
			return BigDecimal.ZERO;
		}

		@Override
		public void setTemporalTotalDocumentDiscountAmt(
				BigDecimal temporalTotalDocumentDiscount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public BigDecimal getTaxedAmount(BigDecimal amount,
				boolean includeOtherTaxes) {
			return getTaxedAmount(amount);
		}

		@Override
		public Integer getDocumentLineID() {
			return MOrderLine.this.getID();
		}

		@Override
		public void setGeneratedInvoiceLineID(Integer generatedInvoiceLineID) {
			MOrderLine.this.setTpvGeneratedInvoiceLineID(generatedInvoiceLineID);
		}
    }

	public boolean isShouldUpdateHeader() {
		return shouldUpdateHeader;
	}

	public void setShouldUpdateHeader(boolean shouldUpdateHeader) {
		this.shouldUpdateHeader = shouldUpdateHeader;
	}
	
	
    /**
     * @return la taza de impuesto configurada en esta línea 
     */
    public BigDecimal getTaxRate() {
		BigDecimal rate = BigDecimal.ZERO;
		if (getC_Tax_ID() > 0) {
			MTax tax = MTax.get(getCtx(), getC_Tax_ID(), get_TrxName());
			rate = tax !=  null ? tax.getRate() : rate;
		}
		return rate;
	}
	
	/**
     * @return nombre del cargo relacionado con esta línea
     * NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public String getChargeName(){
    	String changeName = "";
    	if(!Util.isEmpty(getC_Charge_ID(), true)){
    		MCharge charge = new MCharge(getCtx(), getC_Charge_ID(), get_TrxName());
    		changeName = charge.getName();
    	}
    	return changeName;
    }
    
    /**
     * @return nombre del proyecto relacionado con esta línea
     * NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public String getProjectName(){
    	String projectName = "";
    	if(!Util.isEmpty(getC_Project_ID(), true)){
    		MProject project = new MProject(getCtx(), getC_Project_ID(), get_TrxName());
    		projectName = project.getName();
    	}
    	return projectName;
    }

    /**
     * @return precio ingresado con impuestos
     * NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getPriceEnteredWithTax(){
    	return amtByTax(getPriceEntered(), getTaxAmt(getPriceEntered()), isTaxIncluded(), true);
    }
    
    /**
     * @return precio ingresado sin impuestos
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getPriceEnteredNet(){
    	return amtByTax(getPriceEntered(), getTaxAmt(getPriceEntered()), isTaxIncluded(), false).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * @return precio ingresado con impuestos * cantidad ingresada
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getTotalPriceEnteredWithTax(){
    	return getPriceEnteredWithTax().multiply(getQtyEntered());
    }
    
    /**
     * @return precio ingresado sin impuestos * cantidad ingresada
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getTotalPriceEnteredNet(){
    	return getPriceEnteredNet().multiply(getQtyEntered());
    }
    
    /**
     * @return precio de lista con impuestos
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getPriceListWithTax(){
    	return amtByTax(getPriceList(),	getTaxAmt(getPriceList()), isTaxIncluded(), true);
    }
    
    /**
     * @return precio de lista sin impuestos
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getPriceListNet(){
    	return amtByTax(getPriceList(),	getTaxAmt(getPriceList()), isTaxIncluded(), false);
    }
    
    /**
     * @return precio de lista con impuestos * cantidad ingresada
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getTotalPriceListWithTax(){
		return getPriceListWithTax().multiply(getQtyEntered());
    }
    
    /**
     * @return precio de lista sin impuestos * cantidad ingresada
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getTotalPriceListNet(){
    	return getPriceListNet().multiply(getQtyEntered());
    }
    
    /**
     * @return precio actual con impuestos
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getPriceActualWithTax(){
    	return amtByTax(getPriceActual(), getTaxAmt(getPriceActual()), isTaxIncluded(), true);
    }
    
    /**
     * @return precio actual sin impuestos
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getPriceActualNet(){
    	return amtByTax(getPriceActual(), getTaxAmt(getPriceActual()), isTaxIncluded(), false);
    }
    
    /**
     * @return precio actual con impuestos * cantiada ingresada
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getTotalPriceActualWithTax(){
    	return getPriceActualWithTax().multiply(getQtyEntered());
    }
    
    /**
     * @return precio actual sin impuestos * cantiada ingresada
     *  NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public BigDecimal getTotalPriceActualNet(){
    	return getPriceActualNet().multiply(getQtyEntered());
    }

	/**
	 * @return bonificación con impuestos por unidad, o sea, bonificación con
	 *         impuesto / cantidad ingresada. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getBonusUnityAmtWithTax(){
    	BigDecimal unityAmt = getUnityAmt(getLineBonusAmt());
		return amtByTax(unityAmt, getTaxAmt(unityAmt), isTaxIncluded(), true);
    }
    
    /**
	 * @return bonificación sin impuestos por unidad, o sea, bonificación sin
	 *         impuesto / cantidad ingresada. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getBonusUnityAmtNet(){
    	BigDecimal unityAmt = getUnityAmt(getLineBonusAmt());
		return amtByTax(unityAmt, getTaxAmt(unityAmt), isTaxIncluded(), false);
    }
    
    /**
	 * @return bonificación con impuestos. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getTotalBonusUnityAmtWithTax(){
		return amtByTax(getLineBonusAmt(), getTaxAmt(getLineBonusAmt()), isTaxIncluded(), true);
    }
    
    /**
	 * @return bonificación sin impuestos. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getTotalBonusUnityAmtNet(){
    	return amtByTax(getLineBonusAmt(), getTaxAmt(getLineBonusAmt()), isTaxIncluded(), false);
    }

	/**
	 * @return descuento de línea con impuestos por unidad, o sea, descuento de
	 *         línea con impuestos / cantidad ingresada. NO MODIFICAR FIRMA, SE
	 *         USA EN LA IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getLineDiscountUnityAmtWithTax(){
    	BigDecimal unityAmt = getUnityAmt(getLineDiscountAmt());
		return amtByTax(unityAmt, getTaxAmt(unityAmt), isTaxIncluded(), true);
    }
    
    /**
	 * @return descuento de línea sin impuestos por unidad, o sea, descuento de
	 *         línea sin impuestos / cantidad ingresada. NO MODIFICAR FIRMA, SE
	 *         USA EN LA IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getLineDiscountUnityAmtNet(){
    	BigDecimal unityAmt = getUnityAmt(getLineDiscountAmt());
		return amtByTax(unityAmt, getTaxAmt(unityAmt), isTaxIncluded(), false);
    }
    
    /**
	 * @return descuento de línea con impuestos. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getTotalLineDiscountUnityAmtWithTax(){
		return amtByTax(getLineDiscountAmt(), getTaxAmt(getLineDiscountAmt()),
				isTaxIncluded(), true);
    }
    
    /**
	 * @return descuento de línea sin impuestos. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getTotalLineDiscountUnityAmtNet(){
		return amtByTax(getLineDiscountAmt(), getTaxAmt(getLineDiscountAmt()),
				isTaxIncluded(), false);
    }

	/**
	 * @return descuento de documento con impuestos por unidad, o sea, descuento
	 *         de documento con impuestos / cantidad ingresada. NO MODIFICAR
	 *         FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getDocumentDiscountUnityAmtWithTax(){
    	BigDecimal unityAmt = getUnityAmt(getDocumentDiscountAmt());
		return amtByTax(unityAmt, getTaxAmt(unityAmt), isTaxIncluded(), true);
    }
    
    /**
	 * @return descuento de documento sin impuestos por unidad, o sea, descuento
	 *         de documento sin impuestos / cantidad ingresada. NO MODIFICAR
	 *         FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getDocumentDiscountUnityAmtNet(){
    	BigDecimal unityAmt = getUnityAmt(getDocumentDiscountAmt());
		return amtByTax(unityAmt, getTaxAmt(unityAmt), isTaxIncluded(), false);
    }
    
    /**
	 * @return descuento de documento con impuestos. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getTotalDocumentDiscountUnityAmtWithTax(){
		return amtByTax(getDocumentDiscountAmt(), getTaxAmt(getDocumentDiscountAmt()),
				isTaxIncluded(), true);
    }
    
    /**
	 * @return descuento de documento sin impuestos. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getTotalDocumentDiscountUnityAmtNet(){
		return amtByTax(getDocumentDiscountAmt(), getTaxAmt(getDocumentDiscountAmt()),
				isTaxIncluded(), false);
    }

	/**
	 * @return Transportista. NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA
	 *         FACTURA
	 */
    public String getShipperName(){
    	String shipperName = null;
    	if(!Util.isEmpty(getM_Shipper_ID(), true)){
    		shipperName = new MShipper(getCtx(), getM_Shipper_ID(), get_TrxName()).getName();
    	}
    	return shipperName;
    }

	/**
	 * @return nombre del depósito. NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN
	 *         DE LA FACTURA
	 */
    public String getWarehouseName(){
    	String warehouseName = null;
    	if(!Util.isEmpty(getM_Warehouse_ID(), true)){
    		warehouseName = new MWarehouse(getCtx(), getM_Warehouse_ID(), get_TrxName()).getName();
    	}
    	return warehouseName;
    }

	/**
	 * @return la descripción por identificadores de la ref order line. NO
	 *         MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
    public String getRefOrderLineDescription(){
    	String refOrderLineDescription = null;
    	if(!Util.isEmpty(getRef_OrderLine_ID(), true)){
    		MOrderLine refOrderLine = new MOrderLine(getCtx(), getRef_OrderLine_ID(), get_TrxName());
			refOrderLineDescription = DisplayUtil.getDisplayByIdentifiers(
					getCtx(), refOrderLine, X_C_OrderLine.Table_ID,
					get_TrxName());
    	}
    	return refOrderLineDescription;
    }

	/**
	 * Obtengo el monto por unidad, o sea, se toma el monto parámetro y se
	 * divide por la cantidad ingresada
	 * 
	 * @param amt
	 *            monto a dividir
	 * @return monto por unidad
	 */
    public BigDecimal getUnityAmt(BigDecimal amt){
		return getQtyEntered().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
				: amt.divide(getQtyEntered(), amt.scale(),
						BigDecimal.ROUND_HALF_EVEN);
    }

	/**
	 * Obtengo el monto de impuesto para un importe parámetro, verificando si
	 * ese importe tiene impuesto incluído o no. Se determina el importe base y
	 * se retorna el monto del impuesto configurado en la línea, a su vez se
	 * determina si el impuesto está incluído en el precio a partir de la tarifa
	 * de la cabecera de la factura.
	 * 
	 * @param amt
	 *            importe con o sin impuestos
	 * @return monto de impuesto a partir del monto parámetro, determinando su
	 *         importe base
	 */
    public BigDecimal getTaxAmt(BigDecimal amt){
    	return MTax.calculateTax(amt, isTaxIncluded(), false, getTaxRate(), amt.scale());
    }

	/**
	 * Extraigo o agrego el monto de impuesto parámetro al importe parámetro,
	 * dependiento si la tasa está incluída en el precio y si se debe obtener el
	 * precio con impuesto o no.
	 * 
	 * @param amt
	 *            importe
	 * @param taxAmt
	 *            monto de impuesto
	 * @param taxIncluded
	 *            impuesto incluído en el precio
	 * @param withTax
	 *            true si se debe obtener el importe con impuestos, false si el
	 *            neto
	 * @return monto neto o con impuestos dependiendo del parámetro withTax
	 */
    public BigDecimal amtByTax(BigDecimal amt, BigDecimal taxAmt, boolean taxIncluded, boolean withTax){
		BigDecimal amtResult = amt;
		if(taxIncluded){
			if(!withTax){
				amtResult = amtResult.subtract(taxAmt);
			}
		}
		else{
			if(withTax){
				amtResult = amtResult.add(taxAmt);
			}
		}
		return amtResult;
	}
    
    //ADER soporte para caches multi-documnto
    private MProductCache m_prodCache;
    public void setProductCache(MProductCache c)
    {
    	m_prodCache = c;
    }

	public void setLineManualDiscountID(Integer lineManualDiscountID) {
		this.lineManualDiscountID = lineManualDiscountID;
	}

	public Integer getLineManualDiscountID() {
		return lineManualDiscountID;
	}
	

	/**
     * @return upc de la pestaña Compras en la ventana de Artículos
     * NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE ETIQUETAS DE ARTICULOS (CMD)
     */    
	public String getVendorUpc(){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT upc FROM M_Product_PO WHERE M_Product_ID = ? AND C_BPartner_ID = ? ");
		  
    	log.finer( sql.toString());

    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;
    	
    	try {
    		pstmt = DB.prepareStatement( sql.toString());
    		pstmt.setInt( 1, getProduct().getID() );
    		pstmt.setInt( 2, getC_BPartner_ID() );
    		rs = pstmt.executeQuery();

    		if( rs.next()) {
    			return rs.getString("upc");
    		}

    	} catch( Exception e ) {
    		log.log( Level.SEVERE,sql.toString(),e );
    	} finally {
    		try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
    		}	catch (Exception e) {}
    	}
    	return "";

	}
	
	public String getVendorProductNo(){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT VendorProductNo FROM M_Product_PO WHERE M_Product_ID = ? ORDER BY iscurrentvendor desc, updated desc LIMIT 1");

    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;
    	String vendorNo = null;
    	
    	try {
    		pstmt = DB.prepareStatement( sql.toString());
    		pstmt.setInt( 1, getProduct().getID() );
    		rs = pstmt.executeQuery();

    		if( rs.next()) {
    			vendorNo = rs.getString("VendorProductNo");
    		}

    	} catch( Exception e ) {
    		log.log( Level.SEVERE,sql.toString(),e );
    	} finally {
    		try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
    		}	catch (Exception e) {}
    	}
    	return vendorNo;
	}
	
	public String getInstanceName(){
		StringBuffer sql;
		int attributeSetInstance_ID = getM_AttributeSetInstance_ID();
		String instanceName = null;

	    sql = new StringBuffer();
		sql.append("select v.name, u.seqno from M_AttributeSetInstance i ")
		.append("INNER JOIN M_AttributeSet s ON (s.M_AttributeSet_ID = i.M_AttributeSet_ID) ") 
		.append("LEFT JOIN M_AttributeUse u ON (u.M_AttributeSet_ID = s.M_AttributeSet_ID) ")
		.append("LEFT JOIN M_AttributeInstance t ON (t.M_Attribute_ID = u.M_Attribute_ID) ")
		.append("LEFT JOIN m_attributeValue v on (v.m_attribute_id = t.m_attribute_id) ")
		.append("where (t.M_AttributeSetInstance_ID = "+ attributeSetInstance_ID +")  and (v.value = t.value)")
		.append("group by t.value, u.seqno, v.name  ")
		.append("order by u.seqno DESC");

		PreparedStatement pstmt = null;
		ResultSet rs 			= null;
		
		try {
			pstmt = DB.prepareStatement( sql.toString());
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				instanceName = rs.getString("Name");
				while( rs.next()) {
					instanceName = instanceName + " - " + rs.getString("Name");
	    		}
				return instanceName;
			}
			else{
				return "";	
			}
		} catch( Exception e ) {
		} finally {
			try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
			}	catch (Exception e) {}
		}
		
		return instanceName;
	}

	public boolean isControlStock() {
		return controlStock;
	}

	public void setControlStock(boolean controlStock) {
		this.controlStock = controlStock;
	}

	public boolean isUpdatePriceInSave() {
		return updatePriceInSave;
	}

	public void setUpdatePriceInSave(boolean updatePriceInSave) {
		this.updatePriceInSave = updatePriceInSave;
	}

	public boolean isAllowAnyQty() {
		return allowAnyQty;
	}

	public void setAllowAnyQty(boolean allowAnyQty) {
		this.allowAnyQty = allowAnyQty;
	}

	public Integer getTpvGeneratedInvoiceLineID() {
		return tpvGeneratedInvoiceLineID;
	}

	public void setTpvGeneratedInvoiceLineID(Integer tpvGeneratedInvoiceLineID) {
		this.tpvGeneratedInvoiceLineID = tpvGeneratedInvoiceLineID;
	}
}    // MOrderLine



/*
 *  @(#)MOrderLine.java   02.07.07
 * 
 *  Fin del fichero MOrderLine.java
 *  
 *  Versión 2.2
 *
 */
