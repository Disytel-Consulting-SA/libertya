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
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.MProductCache;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInvoiceLine extends X_C_InvoiceLine {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Especifica si la línea debe actualizar los impuestos 
	 *  en la cabecera (en caso que esto sea posible) */
	boolean shouldUpdateHeader = true;
	
	/**
	 * Bypass para actualización del descuento manual general de la factura
	 * (Sólo para Facturas de Cliente, no TPV)
	 */
	private boolean skipManualGeneralDiscount = false;
	
	private boolean dragLineDiscountAmts = false;
	private boolean dragDocumentDiscountAmts = false;
	private boolean dragOrderPrice = true;
	
	
    /**
     * Descripción de Método
     *
     *
     * @param sLine
     *
     * @return
     */

    public static MInvoiceLine getOfInOutLine( MInOutLine sLine ) {
        if( sLine == null ) {
            return null;
        }

        MInvoiceLine      retValue = null;
        String            sql      = "SELECT * FROM C_InvoiceLine WHERE M_InOutLine_ID=?";
        PreparedStatement pstmt    = null;

        try {
            pstmt = DB.prepareStatement( sql,sLine.get_TableName());
            pstmt.setInt( 1,sLine.getM_InOutLine_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MInvoiceLine( sLine.getCtx(),rs,sLine.get_TrxName());

                if( rs.next()) {
                    s_log.warning( "More than one C_InvoiceLine of " + sLine );
                }
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
    }    // getOfInOutLine

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MInvoiceLine.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_InvoiceLine_ID
     * @param trxName
     */

    public MInvoiceLine( Properties ctx,int C_InvoiceLine_ID,String trxName ) {
        super( ctx,C_InvoiceLine_ID,trxName );

        if( C_InvoiceLine_ID == 0 ) {
            setIsDescription( false );
            setIsPrinted( true );
            setLineNetAmt( Env.ZERO );
            setPriceEntered( Env.ZERO );
            setPriceActual( Env.ZERO );
            setPriceLimit( Env.ZERO );
            setPriceList( Env.ZERO );
            setM_AttributeSetInstance_ID( 0 );
            setTaxAmt( Env.ZERO );

            //

            setQtyEntered( Env.ZERO );
            setQtyInvoiced( Env.ZERO );
        }
    }    // MInvoiceLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param invoice
     */

    public MInvoiceLine( MInvoice invoice ) {
        this( invoice.getCtx(),0,invoice.get_TrxName());

        if( invoice.getID() == 0 ) {
            throw new IllegalArgumentException( "Header not saved" );
        }

        setClientOrg( invoice.getAD_Client_ID(),invoice.getAD_Org_ID());
        setC_Invoice_ID( invoice.getC_Invoice_ID());
        setInvoice( invoice );
    }    // MInvoiceLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInvoiceLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInvoiceLine

    /** Descripción de Campos */

    private int m_M_PriceList_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateInvoiced = null;

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int m_C_BPartner_Location_ID = 0;

    /** Descripción de Campos */

    private boolean m_IsSOTrx = true;

    /** Descripción de Campos */

    private boolean m_priceSet = false;

    /** Descripción de Campos */

    private MProduct m_product = null;

    /** Descripción de Campos */

    private String m_name = null;

    /** Descripción de Campos */

    private Integer m_precision = null;

    /** Descripción de Campos */

    private MProductPricing m_productPricing = null;
    
    private MInvoice invoice = null;

    /**
     * Descripción de Método
     *
     *
     * @param invoice
     */

    public void setInvoice( MInvoice invoice ) {
        setClientOrg( invoice );
        m_M_PriceList_ID         = invoice.getM_PriceList_ID();
        m_DateInvoiced           = invoice.getDateInvoiced();
        m_C_BPartner_ID          = invoice.getC_BPartner_ID();
        m_C_BPartner_Location_ID = invoice.getC_BPartner_Location_ID();
        m_IsSOTrx                = invoice.isSOTrx();
        m_precision              = new Integer( invoice.getPrecision());
        
        this.invoice = invoice; 
    }    // setOrder

    /**
     * Descripción de Método
     *
     *
     * @param oLine
     */

    public void setOrderLine( MOrderLine oLine ) {
        setC_OrderLine_ID( oLine.getC_OrderLine_ID());
 
        //

        setLine( oLine.getLine());
        setIsDescription( oLine.isDescription());
        setDescription( oLine.getDescription());

        //
        setC_Project_ID(oLine.getC_Project_ID());
        setC_Charge_ID( oLine.getC_Charge_ID());

        //

        setM_Product_ID( oLine.getM_Product_ID());
        setM_AttributeSetInstance_ID( oLine.getM_AttributeSetInstance_ID());
        setS_ResourceAssignment_ID( oLine.getS_ResourceAssignment_ID());
        setC_UOM_ID( oLine.getC_UOM_ID());

        //
        int invoiceCurrencyID = getInvoice().getC_Currency_ID();
        int orderCurrencyID = oLine.getC_Currency_ID();
        MCurrency invoiceCurrency = MCurrency.get(getCtx(), invoiceCurrencyID); 
        if (invoiceCurrencyID == oLine.getC_Currency_ID()) {
        	if(isDragOrderPrice()){
        		setPriceEntered( oLine.getPriceEntered());
    	        setPriceActual( oLine.getPriceActual());
    	        setPriceLimit( oLine.getPriceLimit());
    	        setPriceList( oLine.getPriceList());
        	}
        	else{
        		setPrice();
        	}

	        //
	
	        setC_Tax_ID( oLine.getC_Tax_ID());
	        // Descuento a nivel de línea
	        if(isDragLineDiscountAmts()){
	        	BigDecimal lineDiscountRate = Util.getDiscountRate(oLine
						.getPriceList().multiply(getQtyInvoiced()), oLine
						.getLineDiscountAmt(), invoiceCurrency
						.getStdPrecision());
	        	BigDecimal bonusDiscountRate = Util.getDiscountRate(oLine
						.getPriceList().multiply(getQtyInvoiced()), oLine
						.getLineBonusAmt(), invoiceCurrency
						.getStdPrecision());
	        	
	        	BigDecimal totalPriceList = getPriceList().multiply(getQtyInvoiced());
				setLineBonusAmt(totalPriceList.multiply(bonusDiscountRate));
		        setLineDiscountAmt(totalPriceList.multiply(lineDiscountRate));
		        if(!isDragOrderPrice()){
					setPrice(getPriceEntered().subtract(
							getPriceList().multiply(
									lineDiscountRate.add(bonusDiscountRate))));
		        }
	        }
	        // Descuento a nivel de documento
	        if(isDragDocumentDiscountAmts()){
				BigDecimal documentDiscountRate = Util.getDiscountRate(oLine
						.getPriceEntered().multiply(getQtyInvoiced()), oLine
						.getDocumentDiscountAmt(), invoiceCurrency
						.getStdPrecision());
				setDocumentDiscountAmt(getPriceEntered().multiply(
						getQtyInvoiced()).multiply(documentDiscountRate));
	        }
	        
			if (isDragDocumentDiscountAmts()
					|| isDragLineDiscountAmts()
					|| !isDragOrderPrice()) {
		        setLineNetAmt();
	        }
			else{
				setLineNetAmt( oLine.getLineNetAmt());
			}
	        
	        setTaxAmt();
	        setLineTotalAmt(getLineNetAmt().add(getTaxAmt()));
        } else {
        
        	if(isDragOrderPrice()){
	        	setPriceEntered(MCurrency.currencyConvert(oLine.getPriceEntered(), orderCurrencyID, invoiceCurrencyID, getInvoice().getDateInvoiced(), 0, getCtx()));
		        setPriceActual(MCurrency.currencyConvert(oLine.getPriceActual(), orderCurrencyID, invoiceCurrencyID, getInvoice().getDateInvoiced(), 0, getCtx()));
		        setPriceLimit(MCurrency.currencyConvert(oLine.getPriceLimit(), orderCurrencyID, invoiceCurrencyID, getInvoice().getDateInvoiced(), 0, getCtx())); 
		        setPriceList(MCurrency.currencyConvert(oLine.getPriceList(), orderCurrencyID, invoiceCurrencyID, getInvoice().getDateInvoiced(), 0, getCtx()));
        	}
        	else{
        		setPrice();
        	}
	        //
	
	        setC_Tax_ID( oLine.getC_Tax_ID()); 
	        
	        // Descuento a nivel de línea
	        if(isDragLineDiscountAmts()){
	        	BigDecimal lineDiscountRate = Util.getDiscountRate(oLine
						.getPriceList().multiply(getQtyInvoiced()), oLine
						.getLineDiscountAmt(), invoiceCurrency
						.getStdPrecision());
	        	BigDecimal bonusDiscountRate = Util.getDiscountRate(oLine
						.getPriceList().multiply(getQtyInvoiced()), oLine
						.getLineBonusAmt(), invoiceCurrency
						.getStdPrecision());
	        	
	        	BigDecimal totalPriceList = getPriceList().multiply(getQtyInvoiced());
				setLineBonusAmt(MCurrency.currencyConvert(totalPriceList
						.multiply(bonusDiscountRate), orderCurrencyID,
						invoiceCurrencyID, getInvoice().getDateInvoiced(), 0,
						getCtx()));
				setLineDiscountAmt(MCurrency.currencyConvert(totalPriceList
						.multiply(lineDiscountRate), orderCurrencyID,
						invoiceCurrencyID, getInvoice().getDateInvoiced(), 0,
						getCtx()));
				if(!isDragOrderPrice()){
					setPrice(MCurrency.currencyConvert(
							getPriceEntered().subtract(
									getPriceList().multiply(
											lineDiscountRate
													.add(bonusDiscountRate))),
							orderCurrencyID, invoiceCurrencyID, getInvoice()
									.getDateInvoiced(), 0, getCtx()));
				}
	        }
	        
	        // Descuento a nivel de documento
	        if(isDragDocumentDiscountAmts()){
	        	BigDecimal documentDiscountRate = Util.getDiscountRate(oLine
						.getPriceEntered().multiply(getQtyInvoiced()), oLine
						.getDocumentDiscountAmt(), invoiceCurrency
						.getStdPrecision());
				setDocumentDiscountAmt(MCurrency.currencyConvert(
						getPriceEntered().multiply(getQtyInvoiced()).multiply(
								documentDiscountRate), orderCurrencyID,
						invoiceCurrencyID, getInvoice().getDateInvoiced(), 0,
						getCtx()));
	        }
			
			if (isDragDocumentDiscountAmts()
					|| isDragLineDiscountAmts()
					|| !isDragOrderPrice()) {
	        	setLineNetAmt();
	        }
	        else{
				setLineNetAmt(MCurrency.currencyConvert(oLine.getLineNetAmt(),
						orderCurrencyID, invoiceCurrencyID, getInvoice()
								.getDateInvoiced(), 0, getCtx()));
	        }
	        
	        setTaxAmt();
	        setLineTotalAmt(getLineNetAmt().add(getTaxAmt()));
        }
    }    // setOrderLine

    /**
     * Descripción de Método
     *
     *
     * @param sLine
     */

    public void setShipLine( MInOutLine sLine ) {
        setM_InOutLine_ID( sLine.getM_InOutLine_ID());
        setC_OrderLine_ID( sLine.getC_OrderLine_ID());

        //

        setLine( sLine.getLine());
        setIsDescription( sLine.isDescription());
        setDescription( sLine.getDescription());

        //
        setC_Project_ID(sLine.getC_Project_ID());
        setM_Product_ID( sLine.getM_Product_ID());
        setC_UOM_ID( sLine.getC_UOM_ID());
        setM_AttributeSetInstance_ID( sLine.getM_AttributeSetInstance_ID());

        // setS_ResourceAssignment_ID(sLine.getS_ResourceAssignment_ID());

        setC_Charge_ID( sLine.getC_Charge_ID());

        //

        int C_OrderLine_ID = sLine.getC_OrderLine_ID();

        if( C_OrderLine_ID != 0 ) {
            MOrderLine oLine = new MOrderLine( getCtx(),C_OrderLine_ID,get_TrxName());

            setS_ResourceAssignment_ID( oLine.getS_ResourceAssignment_ID());

            //

            setPriceEntered( oLine.getPriceEntered());
            setPriceActual( oLine.getPriceActual());
            setPriceLimit( oLine.getPriceLimit());
            setPriceList( oLine.getPriceList());

            //

            setC_Tax_ID( oLine.getC_Tax_ID());
            setLineNetAmt( oLine.getLineNetAmt());
        } else {
            setPrice();
            setTax();
        }
    }    // setOrderLine

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
     */

    public void setPrice() {
        if( (getM_Product_ID() == 0) || isDescription()) {
            return;
        }

        if( (m_M_PriceList_ID == 0) || (m_C_BPartner_ID == 0) ) {
            MInvoice invoice = new MInvoice( getCtx(),getC_Invoice_ID(),get_TrxName());

            setInvoice( invoice );
        }

        if( (m_M_PriceList_ID == 0) || (m_C_BPartner_ID == 0) ) {
            throw new IllegalStateException( "setPrice - PriceList unknown!" );
        }

        setPrice( m_M_PriceList_ID,m_C_BPartner_ID );
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_ID
     * @param C_BPartner_ID
     */

    public void setPrice( int M_PriceList_ID,int C_BPartner_ID ) {
        if( (getM_Product_ID() == 0) || isDescription()) {
            return;
        }

        //

        log.fine( "M_PriceList_ID=" + M_PriceList_ID );
        m_productPricing = new MProductPricing( getM_Product_ID(),C_BPartner_ID,getQtyInvoiced(),m_IsSOTrx );
        m_productPricing.setM_PriceList_ID( M_PriceList_ID );
        m_productPricing.setPriceDate( m_DateInvoiced );

        //

        setPriceActual( m_productPricing.getPriceStd());
        setPriceList( m_productPricing.getPriceList());
        setPriceLimit( m_productPricing.getPriceLimit());

        //

        if( getQtyEntered().compareTo( getQtyInvoiced()) == 0 ) {
            setPriceEntered( getPriceActual());
        } else {
            setPriceEntered( getPriceActual().multiply( getQtyInvoiced().divide( getQtyEntered(),BigDecimal.ROUND_HALF_UP )));    // no precision
        }

        //

        if( getC_UOM_ID() == 0 ) {
            setC_UOM_ID( m_productPricing.getC_UOM_ID());
        }

        //

        m_priceSet = true;
    }    // setPrice

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
     *
     * @return
     */

    public boolean setTax() {
        if( isDescription()) {
            return true;
        }

        //

        int M_Warehouse_ID = Env.getContextAsInt( getCtx(),"#M_Warehouse_ID" );

        //
        
        int C_Tax_ID  = 0;
        // Si los Comprobantes fiscales están activos se busca la tasa de impuesto a partir de la categoría de IVA debe estar condicionado 
        if (CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
        	C_Tax_ID = DB.getSQLValue( null,"SELECT C_Tax_ID FROM C_Categoria_Iva ci INNER JOIN C_BPartner bp ON (ci.C_Categoria_Iva_ID = bp.C_Categoria_Iva_ID) WHERE bp.C_BPartner_ID = ?",m_C_BPartner_ID );
        }
        
        if( C_Tax_ID == 0 ) {
        	C_Tax_ID = Tax.get( getCtx(),getM_Product_ID(),getC_Charge_ID(),m_DateInvoiced,m_DateInvoiced,getAD_Org_ID(),M_Warehouse_ID,m_C_BPartner_Location_ID,    // should be bill to
                    m_C_BPartner_Location_ID,m_IsSOTrx );
        }

        if( C_Tax_ID == 0 ) {
            log.log( Level.SEVERE,"No Tax found" );

            return false;
        }

        setC_Tax_ID( C_Tax_ID );

        if( m_IsSOTrx ) {}

        return true;
    }    // setTax

    /**
     * Descripción de Método
     *
     */

    public void setTaxAmt() {
        BigDecimal TaxAmt = Env.ZERO;

        if( getC_Tax_ID() != 0 ) {

            // setLineNetAmt();

            MTax tax = new MTax( getCtx(),getC_Tax_ID(),get_TrxName());

            if((getTaxAmt() == null || (getTaxAmt() != null && getTaxAmt().compareTo(BigDecimal.ZERO) == 0))
            		&& tax.getRate().compareTo(BigDecimal.ZERO) > 0){
            	TaxAmt = tax.calculateTax( getLineNetAmt().subtract(getDocumentDiscountAmt()),isTaxIncluded(),getPrecision());
            }
            else{
            	TaxAmt = getTaxAmt();
            }
        }

        super.setTaxAmt( TaxAmt );
    }    // setTaxAmt

    /**
     * Descripción de Método
     *
     */

    public void setLineNetAmt() {

        // Calculations & Rounding

        BigDecimal net = getPriceActual().multiply( getQtyInvoiced());

        if( net.scale() > getPrecision()) {
            net = net.setScale( getPrecision(),BigDecimal.ROUND_HALF_UP );
        }

        super.setLineNetAmt( net );
    }    // setLineNetAmt

    /**
     * Descripción de Método
     *
     *
     * @param Qty
     */

    public void setQty( int Qty ) {
        setQty( new BigDecimal( Qty ));
    }    // setQtyInvoiced

    /**
     * Descripción de Método
     *
     *
     * @param Qty
     */

    public void setQty( BigDecimal Qty ) {
        setQtyEntered( Qty );
        setQtyInvoiced( Qty );
    }    // setQtyInvoiced

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

    public String toString() {
        StringBuffer sb = new StringBuffer( "MInvoiceLine[" ).append( getID()).append( "," ).append( getLine()).append( ",QtyInvoiced=" ).append( getQtyInvoiced()).append( ",LineNetAmt=" ).append( getLineNetAmt()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        if( m_name == null ) {
            String sql = "SELECT COALESCE (p.Name, c.Name) " + "FROM C_InvoiceLine il" + " LEFT OUTER JOIN M_Product p ON (il.M_Product_ID=p.M_Product_ID)" + " LEFT OUTER JOIN C_Charge C ON (il.C_Charge_ID=c.C_Charge_ID) " + "WHERE C_InvoiceLine_ID=?";
            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,getC_InvoiceLine_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    m_name = rs.getString( 1 );
                }

                rs.close();
                pstmt.close();
                pstmt = null;

                if( m_name == null ) {
                    m_name = "??";
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"getName",e );
            } finally {
                try {
                    if( pstmt != null ) {
                        pstmt.close();
                    }
                } catch( Exception e ) {
                }

                pstmt = null;
            }
        }

        return m_name;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @param tempName
     */

    public void setName( String tempName ) {
        m_name = tempName;
    }    // setName

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

    public int getPrecision() {
        if( m_precision != null ) {
            return m_precision.intValue();
        }

        String sql = "SELECT c.StdPrecision " + "FROM C_Currency c INNER JOIN C_Invoice x ON (x.C_Currency_ID=c.C_Currency_ID) " + "WHERE x.C_Invoice_ID=?";
        int i = DB.getSQLValue( get_TrxName(),sql,getC_Invoice_ID());

        if( i < 0 ) {
            log.warning( "getPrecision = " + i + " - set to 2" );
            i = 2;
        }

        m_precision = new Integer( i );

        return m_precision.intValue();
    }    // getPrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTaxIncluded() {
		m_M_PriceList_ID = DB.getSQLValue(get_TrxName(),
				"SELECT M_PriceList_ID FROM C_Invoice WHERE C_Invoice_ID=?",
				getC_Invoice_ID());
        MPriceList pl = MPriceList.get( getCtx(),m_M_PriceList_ID,get_TrxName());
        return pl.isTaxIncluded();
    }    // isTaxIncluded
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isPerceptionsIncluded() {
		m_M_PriceList_ID = DB.getSQLValue(get_TrxName(),
				"SELECT M_PriceList_ID FROM C_Invoice WHERE C_Invoice_ID=?",
				getC_Invoice_ID());
        MPriceList pl = MPriceList.get( getCtx(),m_M_PriceList_ID,get_TrxName());
        return pl.isPerceptionsIncluded();
    }    // isTaxIncluded

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        log.fine( "New=" + newRecord );

        // Charge

        if( getC_Charge_ID() != 0 ) {
            if( getM_Product_ID() != 0 ) {
                setM_Product_ID( 0 );
            }
        } else    // Set Product Price
        {
            if( !m_priceSet && (Env.ZERO.compareTo( getPriceActual()) == 0) && (Env.ZERO.compareTo( getPriceList()) == 0) ) {
                setPrice();
            }
        }

        // Set Tax

        if( getC_Tax_ID() == 0 ) {
            setTax();
        }

        // Get Line No

        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_InvoiceLine WHERE C_Invoice_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getC_Invoice_ID());

            setLine( ii );
        }

        // UOM

        if( getC_UOM_ID() == 0 ) {
            int C_UOM_ID = MUOM.getDefault_UOM_ID( getCtx());

            if( C_UOM_ID > 0 ) {
                setC_UOM_ID( C_UOM_ID );
            }
        }

		// Si la factura debe manejar los descuentos arrastrados del pedido y
		// modificó la cantidad o el precio debo recalcular los descuentos
		if (!newRecord 
				&& (is_ValueChanged("QtyEntered")
						|| is_ValueChanged("PriceEntered"))) {
			updateDragOrderDiscounts();
        }
        
		// Actualización de precio en base al descuento manual general
        // Esto es importante dejar antes de actualizar el total de la línea
        if(!isSkipManualGeneralDiscount()){
        	// Descuento manual general
    		BigDecimal generalDiscountManual = DB
    				.getSQLValueBD(
    						get_TrxName(),
    						"SELECT ManualGeneralDiscount FROM c_invoice WHERE c_invoice_id = ?",
    						getC_Invoice_ID());
			if (generalDiscountManual.compareTo(BigDecimal.ZERO) != 0) {
        		int M_PriceList_ID = Env.getContextAsInt( getCtx(),"M_PriceList_ID" );
                int stdPrecision = MPriceList.getStandardPrecision( getCtx(),M_PriceList_ID );
        		updateGeneralManualDiscount(generalDiscountManual, stdPrecision);	
    		}
        }
        
        // Calculations & Rounding

        setLineNetAmt();
       	setLineNetAmount();
        
       	// Si la Tarifa tiene impuesto incluido y percepciones incluidas, se actualiza el LineNetAmt y el TaxAmt
       	if( isPerceptionsIncluded() && isTaxIncluded() ) {
      		updateTaxAmt();
       		updateLineNetAmt();
       	}
       			
        // Comentado para poder calcular TaxAmt y LineTotalAmt en Facturas de Cliente
        /*
        if( !m_IsSOTrx    // AP Inv Tax Amt
                && (getTaxAmt().compareTo( Env.ZERO ) == 0) ) {
            setTaxAmt();
        }
		*/
        //

        /* Si el project no está seteado, tomar el de la cabecera */
        if (getC_Project_ID() == 0)
        	setC_Project_ID(DB.getSQLValue(get_TrxName(), " SELECT C_Project_ID FROM C_Invoice WHERE C_Invoice_ID = " + getC_Invoice_ID()));
        
        // Calculo TaxAmt y LineTotalAmt
        // Recupero el impuesto aplicado a la línea
        setTaxAmt();
        setLineTotalAmt(getLineNetAmt().add(getTaxAmt()));
        
        // Setear el proveedor del artículo actual y el precio de costo
        if(!Util.isEmpty(getM_Product_ID(), true)){    		
	        MInvoice invoice = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
	        MProductPO po = null;
	        if(invoice.isSOTrx()){
	        	// Obtener el proveedor actual del artículo
				po = MProductPO.getOfOneProduct(getCtx(), getM_Product_ID(), get_TrxName());
				setC_BPartner_Vendor_ID(po != null?po.getC_BPartner_ID():0);
	        }
	        else{
	        	setC_BPartner_Vendor_ID(invoice.getC_BPartner_ID());
	        }
	        // Seteo el precio de costo
			setCostPrice(MProductPricing.getCostPrice(getCtx(), getAD_Org_ID(),
					getM_Product_ID(), getC_BPartner_Vendor_ID(),
					invoice.getC_Currency_ID(), invoice.getDateInvoiced(), true, 
					isTaxIncluded(), getTaxRate(), isPerceptionsIncluded(),
					get_TrxName()));
        }
        /*
        String sql = "select rate from c_tax where c_tax_id = " + getC_Tax_ID();
        PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName());
		try {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()){
				BigDecimal vRate = rs.getBigDecimal("rate").divide(new BigDecimal(100));
				BigDecimal vTaxAmt = getLineNetAmt().multiply(vRate);
				
				// Seteo TaxAmt y LineTotalAmt
				setTaxAmt(vTaxAmt);
				setLineTotalAmt(getLineNetAmt().add(vTaxAmt));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
        return true;
    }    // beforeSave

    
    public void updateDragOrderDiscounts(){
    	if(getInvoice().isManageDragOrderDiscounts()){
	    	// Recalcular los discount amts de la línea
			// Cuanto era el descuento anterior? La suma de los descuentos de
			// línea. Se debe decrementar el descuento manual general que se
			// aplica a la línea
			// Calcular la proporción de cada descuento sobre la suma de los
			// descuentos
			// TODO Por ahora no se puede aplicar un descuento manual general
			// cuando se arrastraron descuentos por lo que no hay problemas
			// entre ellos. El descuento manual general se guarda en
			// lineDiscountAmt. Para que no se complique tanto quizás sea mejor
			// crear un campo adicional para el descuento manual general
			BigDecimal discountAmt = getLineBonusAmt()
					.add(getLineDiscountAmt());
			BigDecimal lineDiscountRate = getLineDiscountAmt().compareTo(
					BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : Util
					.getDiscountRate(discountAmt, getLineDiscountAmt(),
							getPrecision());
			BigDecimal bonusDiscountRate = getLineBonusAmt().compareTo(
					BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : Util
					.getDiscountRate(discountAmt, getLineBonusAmt(), 
							getPrecision()); 
			
			// Calcular el descuento actual: (PL - PE) * QE
			BigDecimal actualDiscountAmt = getPriceList()
					.subtract(getPriceEntered()).multiply(getQtyEntered());
			setLineDiscountAmt(actualDiscountAmt.multiply(lineDiscountRate));
			setLineBonusAmt(actualDiscountAmt.multiply(bonusDiscountRate));
	    	
			// Para el descuento a nivel de documento se debe calcular cuál era
			// el porcentaje para el precio y la cantidad anterior, de esta
			// forma al precio actual y la cantidad actual le recalculo del
			// documento discount
			BigDecimal documentDiscountRate = Util
					.getDiscountRate(
							((BigDecimal) get_ValueOld("PriceEntered"))
									.multiply(((BigDecimal) get_ValueOld("QtyEntered"))),
							getDocumentDiscountAmt(), getPrecision());
			setDocumentDiscountAmt(getPriceEntered().multiply(
					getQtyEntered()).multiply(documentDiscountRate));
    	}
    }
    
    /**
	 * Actualiza el descuento de la línea en base del descuento manual general
	 * 
	 * @param generalManualDiscount
	 * @param scale
	 */
    public void updateGeneralManualDiscount(BigDecimal generalManualDiscount, int scale){
		BigDecimal priceList = getPriceList().compareTo(BigDecimal.ZERO) != 0 ? getPriceList()
				: getPriceActual();
		BigDecimal lineDiscountAmtUnit = priceList.multiply(
				generalManualDiscount).divide(HUNDRED, scale,
				BigDecimal.ROUND_HALF_UP);
		// Seteo el precio ingresado con el precio de lista - monto de
		// descuento
		setPrice(priceList.subtract(lineDiscountAmtUnit));
		setLineDiscountAmt(lineDiscountAmtUnit.multiply(getQtyEntered()));
		setTaxAmt(BigDecimal.ZERO);
    }
    
    public BigDecimal getDiscountAmt(BigDecimal baseAmt, BigDecimal discountPerc, Integer scale){
		return getDiscountAmt(baseAmt, discountPerc.divide(HUNDRED, scale,
				BigDecimal.ROUND_HALF_UP));
    }
    
    public BigDecimal getDiscountAmt(BigDecimal baseAmt, BigDecimal discountRate){
    	return baseAmt.multiply(discountRate);
    }
    
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

        if( !newRecord && is_ValueChanged( "C_Tax_ID" )) {

            // Recalculate Tax for old Tax

            MInvoiceTax tax = MInvoiceTax.get( this,getPrecision(),true,get_TrxName());    // old Tax

            if( tax != null ) {
                if( !tax.calculateTaxFromLines()) {
                    return false;
                }

                if( !tax.save( get_TrxName())) {
                    return true;
                }
            }
        }
        
        if(shouldUpdateHeader){
        	if(!updateHeaderTax()){
        		return false;
        	}
	        MInvoice invoice = getInvoice();
        	
			// Si debe manejar los descuentos arrastrados de la factura,
			// entonces actualizo el descuento de documento de la cabecera
	        if(invoice.isManageDragOrderDiscounts()){
	        	try{
	        		invoice.updateTotalDocumentDiscount();
	        	} catch(Exception e){
	        		log.saveError("", e.getMessage());
	        		return false;
	        	}
	        }
	        
        	// Esquema de vencimientos
			MPaymentTerm pt = new MPaymentTerm(getCtx(), invoice.getC_PaymentTerm_ID(), get_TrxName());
			if (!pt.apply(invoice.getID()))
				return false;
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
        if( !success ) {
            return success;
        }

        return !shouldUpdateHeader || updateHeaderTax();
    }    // afterDelete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updateHeaderTax() {

        // Recalculate Tax for this Tax

        MInvoiceTax tax = MInvoiceTax.get( this,getPrecision(),false,get_TrxName());    // current Tax

        if( tax != null ) {
            if( !tax.calculateTaxFromLines()) {
                return false;
            }

            if( !tax.save( get_TrxName())) {
                return false;
            }
        }
        
        // Update Invoice Header
        
        String sql = "UPDATE C_Invoice i" + " SET TotalLines=" + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM C_InvoiceLine il WHERE i.C_Invoice_ID=il.C_Invoice_ID) " + "WHERE C_Invoice_ID=" + getC_Invoice_ID();
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.warning( "updateHeaderTax (1) #" + no );
        }

        // Calcular las percepciones
        try{
			getInvoice().calculatePercepciones();
		} catch(Exception e){
			log.severe("ERROR generating percepciones. "+e.getMessage());
			e.printStackTrace();
		}

        no = getInvoice().updateGrandTotal(get_TrxName()); 

        if( no != 1 ) {
            log.warning( "updateHeaderTax (2) #" + no );
        }
        
        no = getInvoice().updateNetAmount(get_TrxName()); 

        if( no != 1 ) {
            log.warning( "updateHeaderTax (3) #" + no );
        }
        
        getInvoice().setNetAmount(getInvoice().calculateNetAmount(get_TrxName()));
           
        return no == 1;
    }    // updateHeaderTax
    
    /** Devuelve la descripcion del producto asociado a la línea */
    public String getProductName()
    {
    	if (getM_Product_ID() > 0){
    		//MProduct prod = new MProduct(p_ctx, getM_Product_ID(), null);
    		//soporte para caches multi-documento
    		MProduct prod = getProduct();
    		//puede ser null... aunque no deberia
    		String prodName = prod == null? "" : prod.getName();
    		
    		//return getDescription() == null ? prod.getName() : (prod.getName() + " - " + getDescription());
       		return getDescription() == null ? prodName : (prodName + " - " + getDescription());
    	}
    	return getDescription();
    }

    /** Devuelve la descripcion del producto asociado a la línea */
    public String getProductValue()
    {
    	if (getM_Product_ID() > 0)
    	{
    		//soprote para caches multi-documento
    		MProduct prod = getProduct();
    		//puede ser null... aunque no deberia
    		String prodValue = prod == null? "": prod.getValue();
    		
    		//return (new MProduct(p_ctx, getM_Product_ID(), null)).getValue();
    		return prodValue;
    	}
    	return "";
    }    
    
    /** Devuelve la descripcion del producto asociado a la línea */
    public String getUOMName()
    {
    	if (getM_Product_ID() > 0 && getC_UOM_ID() > 0)
    		return (new MUOM(p_ctx, getC_UOM_ID(), null)).getName();
    	return "";
    }
    
    public String getLineStr()
    {
    	return "" + getLine();
    }
    
    public BigDecimal getTotalLineNoDsc()
    {
    	return getPriceEntered().multiply(getQtyEntered());
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
	 * @return Indica si esta línea ha sufrido bonificaciones de su precio
	 *         original.
	 */
    public boolean hasBonus() {
    	return getLineBonusAmt().compareTo(BigDecimal.ZERO) != 0;
    }

	/**
	 * @return nombre de la entidad comercial configurada como proveedor
	 * NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
    public String getBPartnerVendorName(){
    	String vendorName = "";
    	if(Util.isEmpty(getC_BPartner_Vendor_ID(), true)){
			MBPartner bpartner = new MBPartner(getCtx(),
					getC_BPartner_Vendor_ID(), get_TrxName());
			vendorName = bpartner.getName();
		}
    	return vendorName;
    }
    
    /**
     * @return nombre del cargo relacionado con esta línea
     * NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
     */
    public String getChargeName(){
    	String changeName = "";
    	if(Util.isEmpty(getC_Charge_ID(), true)){
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
    	if(Util.isEmpty(getC_Project_ID(), true)){
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
    	return amtByTax(getPriceEntered(), getTaxAmt(getPriceEntered()), isTaxIncluded(), false);
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
	 * Obtengo el monto por unidad, o sea, se toma el monto parámetro y se
	 * divide por la cantidad ingresada
	 * 
	 * @param amt
	 *            monto a dividir
	 * @return monto por unidad
	 */
    public BigDecimal getUnityAmt(BigDecimal amt){
		return amt.divide(getQtyEntered(), amt.scale(),	BigDecimal.ROUND_HALF_EVEN);
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
    //por ahroa privado; se usa por lo pronto para dar soporte a los metodos
    //que usan los reportes jasper
    private MProduct getProduct()
    {
    	int id = getM_Product_ID();
    	if (id <= 0)
    		return null;
    	//si se tiene cache multidocuemtntos, se usa esta
    	if (m_prodCache != null)
    		return m_prodCache.get(id);
    	
    	//si no se la mismo que se estaba usando hasta ahora.... (en realidad
    	//se podria usar MProduct.get.... pero bueno
    	
    	return new MProduct(p_ctx, id, null);
   }

	public void setSkipManualGeneralDiscount(boolean skipManualGeneralDiscount) {
		this.skipManualGeneralDiscount = skipManualGeneralDiscount;
	}

	public boolean isSkipManualGeneralDiscount() {
		return skipManualGeneralDiscount;
	}

	/**
	 * @return el valor de invoice
	 */
	public MInvoice getInvoice() {
		if (invoice == null) {
			invoice = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
		}
		return invoice;
	}
	
	public void setDragLineDiscountAmts(boolean dragLineDiscountAmts) {
		this.dragLineDiscountAmts = dragLineDiscountAmts;
	}

	public boolean isDragLineDiscountAmts() {
		return dragLineDiscountAmts;
	}

	public void setDragDocumentDiscountAmts(boolean dragDocumentDiscountAmts) {
		this.dragDocumentDiscountAmts = dragDocumentDiscountAmts;
	}

	public boolean isDragDocumentDiscountAmts() {
		return dragDocumentDiscountAmts;
	}

	public void setDragOrderPrice(boolean dragOrderPrice) {
		this.dragOrderPrice = dragOrderPrice;
	}

	public boolean isDragOrderPrice() {
		return dragOrderPrice;
	}
	
	/**
	 * Crea el wrapper de esta línea para ser manipulada por un calculador de
	 * descuentos.
	 * 
	 * @param invoice
	 *            Wrapper de la factura que contiene esta línea
	 * @return Nueva instancia del wrapper
	 */
    protected IDocumentLine createDiscountableWrapper(IDocument invoice) {
    	return new DiscountableMInvoiceLineWrapper(invoice);
    }
    
    /**
     * Wrapper de {@link MInvoiceLine} para cálculo de descuentos.
     */
    private class DiscountableMInvoiceLineWrapper extends DiscountableDocumentLine {
    	
		public DiscountableMInvoiceLineWrapper(IDocument document) {
			super(document);
		}

		@Override
		public BigDecimal getPrice() {
			return MInvoiceLine.this.getPriceActual();
		}

		@Override
		public BigDecimal getPriceList() {
			return MInvoiceLine.this.getPriceList();
		}

		@Override
		public int getProductID() {
			return MInvoiceLine.this.getM_Product_ID();
		}

		@Override
		public BigDecimal getQty() {
			return MInvoiceLine.this.getQtyEntered();
		}

		@Override
		public void setPrice(BigDecimal newPrice) {
			MInvoiceLine.this.setPrice(newPrice);		
		}
		
		@Override
		public void setDocumentDiscountAmt(BigDecimal discountAmt) {
			MInvoiceLine.this.setDocumentDiscountAmt(discountAmt);
			if (!MInvoiceLine.this.save()) {
				log.severe("Cannot save discounted Invoice Line");
			}
		}
		
		@Override
		public BigDecimal getLineDiscountAmt() {
			return MInvoiceLine.this.getLineDiscountAmt();
		}

		@Override
		public void setLineDiscountAmt(BigDecimal lineDiscountAmt) {
			MInvoiceLine.this.setLineDiscountAmt(lineDiscountAmt);
		}

		@Override
		public BigDecimal getLineBonusAmt() {
			return MInvoiceLine.this.getLineBonusAmt();
		}

		@Override
		public void setLineBonusAmt(BigDecimal lineBonusAmt) {
			MInvoiceLine.this.setLineBonusAmt(lineBonusAmt);			
		}
		
		@Override
		public BigDecimal getTaxRate() {
			return MInvoiceLine.this.getTaxRate();
		}
		
		@Override
		public boolean isTaxIncluded() {
			return MInvoiceLine.this.isTaxIncluded();
		}

		@Override
		public void setDiscount(BigDecimal discount) {
			// No hace nada por aca ya que no existen descuentos a nivel de
			// línea
		}

		@Override
		public BigDecimal getDiscount() {
			// No hace nada por aca ya que no existen descuentos a nivel de
			// línea
			return null;
		}

		@Override
		public Integer getLineManualDiscountID() {
			// No hace nada por aca ya que no existen descuentos a nivel de
			// línea
			return null;
		}

		@Override
		public void setLineManualDiscountID(Integer lineManualDiscountID) {	
			// No hace nada por aca ya que no existen descuentos a nivel de
			// línea
		}
    }

	public void setRMALine(MRMALine rmaLine)
	{
		// Check if this invoice is CreditMemo - teo_sarca [ 2804142 ]
		/*if (!getParent().isCreditMemo())
		{
			//throw new AdempiereException("InvoiceNotCreditMemo");
		}
		setAD_Org_ID(rmaLine.getAD_Org_ID());
        setM_RMALine_ID(rmaLine.getM_RMALine_ID());
        setDescription(rmaLine.getDescription());
        setLine(rmaLine.getLine());
        setC_Charge_ID(rmaLine.getC_Charge_ID());
        setM_Product_ID(rmaLine.getM_Product_ID());
        setC_UOM_ID(rmaLine.getC_UOM_ID());
        setC_Tax_ID(rmaLine.getC_Tax_ID());
        setPrice(rmaLine.getAmt());
        BigDecimal qty = rmaLine.getQty();
        if (rmaLine.getQtyInvoiced() != null)
        	qty = qty.subtract(rmaLine.getQtyInvoiced());
        setQty(qty);
        setLineNetAmt();
        setTaxAmt();
        setLineTotalAmt(rmaLine.getLineNetAmt());
        setC_Project_ID(rmaLine.getC_Project_ID());
        setC_Activity_ID(rmaLine.getC_Activity_ID());
        setC_Campaign_ID(rmaLine.getC_Campaign_ID()); TODO Hernandez*/
	}

    /**
     * Se guarda el valor neto de la linea en el campo LineNetAmount.
     */
    public void setLineNetAmount() {
        BigDecimal net = getLineNetAmt();
        
        BigDecimal tax = isTaxIncluded() ? getTaxRate().divide(new BigDecimal(100)) : BigDecimal.ZERO;
        
        // Si la Tarifa tiene impuesto incluido y percepciones incluidas, el neto se calcula haciendo: monto / (1 + Tasa de Impuesto + Tasa de Percepciones)  
        if(isTaxIncluded() && isPerceptionsIncluded()){
        	GeneratorPercepciones generator = new GeneratorPercepciones(getCtx(), getInvoice().getDiscountableWrapper(), get_TrxName());
            try {
            	BigDecimal rate = generator.totalPercepcionesRate();
            	rate = rate.divide(new BigDecimal(100));
            	try
                {
            		net = net.divide( (BigDecimal.ONE.add(rate).add(tax)));
                }
                catch (Exception e)
                {
                	net = new BigDecimal(net.doubleValue() / (BigDecimal.ONE.add(rate).add(tax)).doubleValue());
                }
            	
    		} catch (Exception e) {
    			e.printStackTrace();
    		}	
        }
        else{
        	// Si la Tarifa tiene solo impuesto incluido, pero sin percepciones incluidas, el neto se calcula haciendo: monto / (1 + Tasa de Impuesto)
        	if(isTaxIncluded()){
        		net = net.divide( (BigDecimal.ONE.add(tax)), 2, BigDecimal.ROUND_HALF_UP );
        	}
        }

        if( net.scale() > getPrecision()) {
            net = net.setScale( getPrecision(),BigDecimal.ROUND_HALF_UP );
        }

        super.setLineNetAmount(net);
    }    // setLineNetAmt
    
    /**
     * Descripción de Método
     * @throws Exception 
     *
     */
    public void updateLineNetAmt() {
        BigDecimal net = getLineNetAmount().add(getTaxAmt());
        if( net.scale() > getPrecision()) {
            net = net.setScale( getPrecision(),BigDecimal.ROUND_HALF_UP );
        }
        super.setLineNetAmt(net);
    }    // setLineNetAmt
    
    /**
     * Descripción de Método
     * @throws Exception 
     *
     */
    public void updateTaxAmt() {
    	BigDecimal tax = getTaxRate().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    	BigDecimal net = getLineNetAmt();
         
    	GeneratorPercepciones generator = new GeneratorPercepciones(getCtx(), getInvoice().getDiscountableWrapper(), get_TrxName());
        try {
        	BigDecimal rate = generator.totalPercepcionesRate();
        	rate = rate.divide(new BigDecimal(100));
        	try{
        		net = net.divide( (BigDecimal.ONE.add(rate).add(tax)));
            }
            catch (Exception e){
            	net = new BigDecimal(net.doubleValue() / (BigDecimal.ONE.add(rate).add(tax)).doubleValue());
            }
		} catch (Exception e) {
			e.printStackTrace();
		}	
    	
        BigDecimal taxAmt = net.multiply(tax);
        if( taxAmt.scale() > getPrecision()) {
        	taxAmt = taxAmt.setScale( getPrecision(),BigDecimal.ROUND_HALF_UP );
        }
        super.setTaxAmt(taxAmt);
    }    // setLineNetAmt
    
}    // MInvoiceLine



/*
 *  @(#)MInvoiceLine.java   02.07.07
 * 
 *  Fin del fichero MInvoiceLine.java
 *  
 *  Versión 2.2
 *
 */
