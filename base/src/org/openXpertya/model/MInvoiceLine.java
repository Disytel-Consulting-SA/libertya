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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
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

public class MInvoiceLine extends X_C_InvoiceLine {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Tolerancia de centavos en importes de impuestos */
	private static final String TAXAMT_TOLERANCE_PREFERENCE_NAME = "TaxAmt_Tolerance";
	
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
	private boolean dragLineSurchargesAmts = false;
	private boolean dragDocumentSurchargesAmts = false;
	
	private boolean dragOrderPrice = true;
	private List<MDocumentDiscount> documentDiscountsToSave = null;
	
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
            pstmt = DB.prepareStatement( sql,sLine.get_TrxName());
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

        setC_Tax_ID( oLine.getC_Tax_ID());
        setSalesRep_Orig_ID(oLine.getSalesRep_Orig_ID());
        
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

	        // Arrastre de descuentos
	        doDragDiscounts(oLine, invoiceCurrency.getStdPrecision(), true);
	        
			if (isDragDocumentDiscountAmts()
					|| isDragLineDiscountAmts()
					|| isDragDocumentSurchargesAmts()
					|| isDragLineSurchargesAmts()
					|| !isDragOrderPrice()) {
		        setLineNetAmt();
	        }
			else{
				setLineNetAmt( oLine.getLineNetAmt());
			}

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
	        
        	// Arrastre de descuentos
	        doDragDiscounts(oLine, invoiceCurrency.getStdPrecision(), true);
			
			if (isDragDocumentDiscountAmts()
					|| isDragLineDiscountAmts()
					|| isDragDocumentSurchargesAmts()
					|| isDragLineSurchargesAmts()
					|| !isDragOrderPrice()) {
	        	setLineNetAmt();
	        }
	        else{
				setLineNetAmt(MCurrency.currencyConvert(oLine.getLineNetAmt(),
						orderCurrencyID, invoiceCurrencyID, getInvoice()
								.getDateInvoiced(), 0, getCtx()));
	        }
        }
        
        setTaxAmt();
        setLineTotalAmt(getLineNetAmt().add(getTaxAmt()));
    }    // setOrderLine

    /**
     * Arrastre de descuentos del pedido
     * @param orderLine
     * @param scale
     * @param updatePrice
     */
    public boolean doDragDiscounts(MOrderLine orderLine, Integer scale, boolean updatePrice){
    	boolean result = true;
    	if(!getInvoice().isManageDragOrderDiscountsSurcharges(false) 
    			|| Util.isEmpty(getC_OrderLine_ID(), true)){
    		return result;
    	}
    	try {
        	// Descuentos de línea
        	dragLineDiscounts(orderLine, scale, updatePrice);
        	// Descuentos de documento
        	dragDocumentDiscounts(orderLine, scale, updatePrice);			
		} catch (Exception e) {
			result = false;
		}
    	return result;
    }
    
    /**
     * Arrastre de descuentos a nivel de línea del pedido
     * @param orderLine
     * @param scale
     * @param updatePrice
     */
    public void dragLineDiscounts(MOrderLine orderLine, Integer scale, boolean updatePrice) throws Exception{
    	if(!isDragLineDiscountAmts() && !isDragLineSurchargesAmts()){
    		return;
    	}
    	Integer tmpPrecision = 10;
		List<MDocumentDiscount> lineDiscounts = MDocumentDiscount.get(
				"C_OrderLine_ID = ? AND cumulativelevel = '" + MDiscountSchema.CUMULATIVELEVEL_Line+"'",
				new Object[] { orderLine.getC_OrderLine_ID() }, "DiscountKind, DiscountApplication, Description, M_DiscountSchema_ID, C_Payment_ID", 
				getCtx(), get_TrxName());
		BigDecimal totalPriceList = (updatePrice ? getPriceList() : getPriceEntered()).multiply(getQtyInvoiced());
		BigDecimal totalLineDiscountAmt = BigDecimal.ZERO, totalLineDiscountBaseAmt = BigDecimal.ZERO;
		BigDecimal totalBonusDiscountAmt = BigDecimal.ZERO, totalBonusDiscountBaseAmt = BigDecimal.ZERO;
		// Existen descuentos configurados en la línea del pedido, entonces
		// itero por ellos y los aplico a la línea de factura
		if(lineDiscounts.size() > 0){
			int signToCompare = isDragLineDiscountAmts() && isDragLineSurchargesAmts() ? 0
					: (isDragLineDiscountAmts() ? 1 : -1);
			/*String keyDD = null, oldKeyDD = null;
			String dk = null, da = null, d = null;
			Integer ds = 0, p = 0;*/
			for (MDocumentDiscount mDocumentDiscount : lineDiscounts) {
				// Se debe verificar si se debe arrastrar el descuento o recargo
				// dependiendo de la configuración
				if(signToCompare == 0 || mDocumentDiscount.getDiscountAmt().signum() == signToCompare){
					/*keyDD = mDocumentDiscount.getDiscountKind()+"_"+mDocumentDiscount.getDiscountApplication()+"_"+mDocumentDiscount.getDescription()+"_"
							+(Util.isEmpty(mDocumentDiscount.getM_DiscountSchema_ID(), true)?"0":mDocumentDiscount.getM_DiscountSchema_ID())
							+(Util.isEmpty(mDocumentDiscount.getC_Payment_ID(), true)?"0":mDocumentDiscount.getC_Payment_ID());
					// Si es diferente al anterior, se guarda el document discount 
					if(oldKeyDD != null && !oldKeyDD.equals(keyDD)) {
						BigDecimal discountAmt = Util.getRatedAmt(totalPriceList, totalDiscountAmt,
								totalDiscountBaseAmt, tmpPrecision);
						BigDecimal propAmt = Util.getProportionalAmt(discountAmt, totalPriceList, totalDiscountBaseAmt);
						// Si es Bonificación, acumulo en el bonus amt
						if (MDocumentDiscount.DISCOUNTAPPLICATION_Bonus.equals(da)) {
							bonusDiscountAmt = bonusDiscountAmt.add(propAmt);
						}
						// Sino en el de línea
						else{
							lineDiscountAmt = lineDiscountAmt.add(propAmt);				
						}
						createDocumentDiscountToSave(ds, d, totalPriceList, propAmt, 
								MDiscountSchema.CUMULATIVELEVEL_Line, da, dk, p, true);
					}
					
					oldKeyDD = keyDD;
					dk = mDocumentDiscount.getDiscountKind();
					da = mDocumentDiscount.getDiscountApplication();
					ds = mDocumentDiscount.getM_DiscountSchema_ID();
					p = mDocumentDiscount.getC_Payment_ID();
					d = mDocumentDiscount.getDescription();*/

					if (MDocumentDiscount.DISCOUNTAPPLICATION_Bonus.equals(mDocumentDiscount.getDiscountApplication())) {
						totalBonusDiscountBaseAmt = totalBonusDiscountBaseAmt.add(mDocumentDiscount.getDiscountBaseAmt());
						totalBonusDiscountAmt = totalBonusDiscountAmt.add(mDocumentDiscount.getDiscountAmt());
					}
					// Sino en el de línea
					else{
						totalLineDiscountBaseAmt = totalLineDiscountBaseAmt.add(mDocumentDiscount.getDiscountBaseAmt());
						totalLineDiscountAmt = totalLineDiscountAmt.add(mDocumentDiscount.getDiscountAmt());				
					}
				}
			}
			
			// Se guardan los document discount
			// Bonus
			if(totalBonusDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal discountBonusAmt = Util.getRatedAmt(totalPriceList, totalBonusDiscountAmt,
						totalBonusDiscountBaseAmt, tmpPrecision);
				
				// Si es Bonificación, guardo la bonificación
				if (discountBonusAmt.compareTo(BigDecimal.ZERO) != 0) {
					createDocumentDiscountToSave(0, getProductName(), totalPriceList, discountBonusAmt, 
							MDiscountSchema.CUMULATIVELEVEL_Line, MDocumentDiscount.DISCOUNTAPPLICATION_Bonus,
							MDocumentDiscount.DISCOUNTKIND_DiscountLine, 0, true);
				}
				totalBonusDiscountAmt = discountBonusAmt;
			}
			// Descuentos de línea
			if(totalLineDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal discountLineAmt = Util.getRatedAmt(totalPriceList, totalLineDiscountAmt,
						totalLineDiscountBaseAmt, tmpPrecision);
				// Sino en el de línea
				if (discountLineAmt.compareTo(BigDecimal.ZERO) != 0) {
					createDocumentDiscountToSave(0, getProductName(), totalPriceList, discountLineAmt, 
							MDiscountSchema.CUMULATIVELEVEL_Line, MDocumentDiscount.DISCOUNTAPPLICATION_DiscountToPrice,
							MDocumentDiscount.DISCOUNTKIND_DiscountLine, 0, true);
				}
				totalLineDiscountAmt = discountLineAmt;
			}
			
		}
		// DEPRECATED: En caso que no existan, se deja el código anterior, tomando en cuenta
		// los importes de descuento de las líneas
		else{
			totalLineDiscountAmt = Util.getRatedAmt(totalPriceList, orderLine.getLineDiscountAmt(),
					orderLine.getPriceList().multiply(orderLine.getQtyOrdered()), tmpPrecision);
			if(!Util.isEmpty(totalLineDiscountAmt, true)){
				createDocumentDiscountToSave(0, getProductName(), totalPriceList, totalLineDiscountAmt,
						MDocumentDiscount.CUMULATIVELEVEL_Line, MDocumentDiscount.DISCOUNTAPPLICATION_DiscountToPrice,
						MDocumentDiscount.DISCOUNTKIND_DiscountLine, 0, true);
			}
			
			totalBonusDiscountAmt = Util.getRatedAmt(totalPriceList, orderLine.getLineBonusAmt(),
					orderLine.getPriceList().multiply(orderLine.getQtyOrdered()), tmpPrecision);
			if(!Util.isEmpty(totalBonusDiscountAmt, true)){
				createDocumentDiscountToSave(0, getProductName(), totalPriceList, totalBonusDiscountAmt,
						MDocumentDiscount.CUMULATIVELEVEL_Line, MDocumentDiscount.DISCOUNTAPPLICATION_Bonus,
						MDocumentDiscount.DISCOUNTKIND_DiscountLine, 0, true);
			}
		}
		
		setLineBonusAmt(totalBonusDiscountAmt.setScale(scale, BigDecimal.ROUND_HALF_DOWN));
		setLineDiscountAmt(totalLineDiscountAmt.setScale(scale, BigDecimal.ROUND_HALF_DOWN));
		
		// Actualizar el precio
		if(updatePrice && !isDragOrderPrice()){
			BigDecimal lineDiscountRate = Util.getDiscountRate(totalPriceList, getLineDiscountAmt(), tmpPrecision);
			BigDecimal bonusDiscountRate = Util.getDiscountRate(totalPriceList, getLineBonusAmt(), tmpPrecision);
			setPrice(getPriceList().subtract(getPriceList().multiply(lineDiscountRate.add(bonusDiscountRate))));
        }
    }
    
    /**
     * Arrastre de descuentos a nivel de documento del pedido
     * @param orderLine
     * @param scale
     * @param usePriceList
     */
    public void dragDocumentDiscounts(MOrderLine orderLine, Integer scale, boolean usePriceList){
    	if(!isDragDocumentDiscountAmts() && !isDragDocumentSurchargesAmts()){
    		return;
    	}
    	Integer tmpPrecision = 10;
    	List<MDocumentDiscount> documentDiscounts = MDocumentDiscount.get(
				"C_OrderLine_ID = ? AND cumulativelevel = '" + MDiscountSchema.CUMULATIVELEVEL_Document+"'",
				new Object[] { orderLine.getC_OrderLine_ID() }, getCtx(), get_TrxName());
		//BigDecimal totalPriceList = (usePriceList ? getPriceList() : getPriceEntered()).multiply(getQtyInvoiced());
    	BigDecimal totalPriceList = getPriceEntered().multiply(getQtyInvoiced());
		BigDecimal documentDiscountAmt = BigDecimal.ZERO;
		BigDecimal discountAmt = null;
		// Existen descuentos configurados en la línea del pedido, entonces
		// itero por ellos y los aplico a la línea de factura
		if(documentDiscounts.size() > 0){
			int signToCompare = isDragDocumentDiscountAmts() && isDragDocumentSurchargesAmts() ? 0
					: (isDragDocumentDiscountAmts() ? 1 : -1);
			BigDecimal accDiscountAmt = BigDecimal.ZERO;
			BigDecimal accDiscountBaseAmt = BigDecimal.ZERO;
			MDocumentDiscount old_dd = null;
			for (MDocumentDiscount mDocumentDiscount : documentDiscounts) {
				// Se debe verificar si se debe arrastrar el descuento o recargo
				// dependiendo de la configuración
				if(signToCompare == 0 || mDocumentDiscount.getDiscountAmt().signum() == signToCompare){
					
					// Si es el mismo descuento, lo sumo al acumulado y sigo
					if(old_dd == null 
							|| old_dd.getDiscountKind() != mDocumentDiscount.getDiscountKind()
							|| old_dd.getM_DiscountSchema_ID() != mDocumentDiscount.getM_DiscountSchema_ID()) {
						old_dd = mDocumentDiscount;
						accDiscountBaseAmt = accDiscountBaseAmt.add(mDocumentDiscount.getDiscountBaseAmt());
						accDiscountAmt = accDiscountAmt.add(mDocumentDiscount.getDiscountAmt());
						continue;
					}
					
					discountAmt = Util.getRatedAmt(totalPriceList, accDiscountAmt, accDiscountBaseAmt, tmpPrecision);
					
					// Determinar el ratio correspondiente a esta línea para
					// saber cuanto es realmente el descuento
					/*parent = parentDocumentDiscounts.get(mDocumentDiscount.getC_DocumentDiscount_Parent_ID());
					if (parent == null) {
						parent = new MDocumentDiscount(getCtx(), mDocumentDiscount.getC_DocumentDiscount_Parent_ID(),
								get_TrxName());
						parentDocumentDiscounts.put(mDocumentDiscount.getC_DocumentDiscount_Parent_ID(), parent);
					}
					discountAmt = discountAmt.multiply(totalPriceList
							.divide(mDocumentDiscount.getDiscountBaseAmt(), tmpPrecision, BigDecimal.ROUND_HALF_DOWN));
							*/
					documentDiscountAmt = documentDiscountAmt.add(discountAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
					// Registro el descuento para luego guardar
					createDocumentDiscountToSave(old_dd, totalPriceList, discountAmt, true);
					accDiscountAmt = BigDecimal.ZERO;
					accDiscountBaseAmt = BigDecimal.ZERO;
					old_dd = null;
				}
				
			}
			if(old_dd != null) {
				discountAmt = Util.getRatedAmt(totalPriceList, accDiscountAmt, accDiscountBaseAmt, tmpPrecision);
				documentDiscountAmt = documentDiscountAmt.add(discountAmt.setScale(2, BigDecimal.ROUND_HALF_DOWN));
				// Registro el descuento para luego guardar
				createDocumentDiscountToSave(old_dd, totalPriceList, discountAmt, true);
			}
			
		}
		else{
			// DEPRECATED En caso que no existan, se deja el código anterior, tomando en cuenta
			// los importes de descuento de las líneas
			documentDiscountAmt = Util.getRatedAmt(totalPriceList, orderLine.getDocumentDiscountAmt(),
					orderLine.getPriceList().multiply(orderLine.getQtyOrdered()), tmpPrecision);
			if(!Util.isEmpty(documentDiscountAmt, true)){
				createDocumentDiscountToSave(0, getProductName(), totalPriceList, documentDiscountAmt,
						MDocumentDiscount.CUMULATIVELEVEL_Document, null,
						MDocumentDiscount.DISCOUNTKIND_DocumentDiscount, 0, true);
			}
		}
		
		setDocumentDiscountAmt(documentDiscountAmt.setScale(scale, BigDecimal.ROUND_HALF_DOWN));
    }
    
    /**
     * Creación del document discount a partir de la información del documentdiscount base
     * @param from documentdiscount base
     * @param discountBaseAmt
     * @param discountAmt
     * @param addToList
     * @return
     */
    private MDocumentDiscount createDocumentDiscountToSave(MDocumentDiscount from, BigDecimal discountBaseAmt, BigDecimal discountAmt, boolean addToList){
		return createDocumentDiscountToSave(from.getM_DiscountSchema_ID(),
				from.getDescription(), discountBaseAmt, discountAmt, from.getCumulativeLevel(),
				from.getDiscountApplication(), from.getDiscountKind(), from.getC_Payment_ID(), true);
    }
    
    /**
     * Creación del document discount con los datos parámetro
     * @param discountSchemaID
     * @param description
     * @param discountBaseAmt
     * @param discountAmt
     * @param cumulativeLevel
     * @param discountApplication
     * @param discountKind
     * @param addToList
     * @return
     */
	private MDocumentDiscount createDocumentDiscountToSave(Integer discountSchemaID, String description,
			BigDecimal discountBaseAmt, BigDecimal discountAmt, String cumulativeLevel, String discountApplication,
			String discountKind, Integer paymentID, boolean addToList) {
    	// Creo el nuevo document discount para la línea de la factura y
		// lo agrego a la lista para luego guardar
    	MDocumentDiscount newDocumentDiscount = new MDocumentDiscount(getCtx(), 0, get_TrxName());
    	newDocumentDiscount.setClientOrg(getAD_Client_ID(), getAD_Org_ID());
		newDocumentDiscount.setM_DiscountSchema_ID(discountSchemaID);
		newDocumentDiscount.setDescription(description);
		newDocumentDiscount.setCumulativeLevel(cumulativeLevel);
		newDocumentDiscount.setDiscountApplication(discountApplication);
		newDocumentDiscount.setDiscountKind(discountKind);
		newDocumentDiscount.setDiscountBaseAmt(discountBaseAmt);
		newDocumentDiscount.setDiscountAmt(discountAmt);
		newDocumentDiscount.setC_Payment_ID(paymentID);
		if(addToList){
			if(documentDiscountsToSave == null){
				documentDiscountsToSave = new ArrayList<MDocumentDiscount>();
			}
			documentDiscountsToSave.add(newDocumentDiscount);
		}
		return newDocumentDiscount;
    }
    
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

        log.fine( "M_PriceList_ID=" + M_PriceList_ID );
		m_productPricing = new MProductPricing(getM_Product_ID(), C_BPartner_ID, getQtyInvoiced(), m_IsSOTrx,
				!getInvoice().isManageDragOrderDiscountsSurcharges(false));
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
            setPriceEntered( getPriceActual().multiply( getQtyInvoiced().divide( getQtyEntered(),BigDecimal.ROUND_HALF_DOWN )));    // no precision
        }

        //

        if( getC_UOM_ID() == 0 ) {
            setC_UOM_ID( m_productPricing.getC_UOM_ID());
        }

        //

        setM_priceSet(true);
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
        	MTax tax = CalloutInvoiceExt.getTax(getCtx(), m_IsSOTrx, m_C_BPartner_ID, getAD_Org_ID(), get_TrxName());
    		if(tax != null){
    			C_Tax_ID = tax.getID();
    		}
        }
        
        if( C_Tax_ID <= 0 ) {
        	C_Tax_ID = Tax.get( getCtx(),getM_Product_ID(),getC_Charge_ID(),m_DateInvoiced,m_DateInvoiced,getAD_Org_ID(),M_Warehouse_ID,m_C_BPartner_Location_ID,    // should be bill to
                    m_C_BPartner_Location_ID,m_IsSOTrx );
        }

        if( C_Tax_ID == 0 ) {
            log.log( Level.SEVERE,"No Tax found" );

            return false;
        }

        setC_Tax_ID( C_Tax_ID );

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

            if (getTaxAmt() == null || getTaxAmt().compareTo(BigDecimal.ZERO) == 0 || getInvoice().isSOTrx()) {
            	TaxAmt = tax.calculateTax(getTaxBaseAmtToEvaluateTax(),isTaxIncluded(),getPrecision());
            }
            else{
            	TaxAmt = getTaxAmt();
            }
        }

        super.setTaxAmt( TaxAmt );
    }    // setTaxAmt

    /**
	 * En este método se encapsula el importe base para el cálculo de impuestos.
	 * Luego de esto se debe verificar si este importe contiene impuestos
	 * incluídos o no
	 * 
	 * @return el importe base para el cálculo de impuestos para esta línea
	 */
    public BigDecimal getTaxBaseAmtToEvaluateTax(){
    	return getLineNetAmt().subtract(getDocumentDiscountAmt());
    }
    
    /**
	 * En este método se encapsula el importe base para el cálculo de impuestos.
	 * El importe devuelto es el neto real para aplicar directamente el
	 * descuento
	 * 
	 * @return el importe base para el cálculo de impuestos para esta línea
	 */
    public BigDecimal getNetTaxBaseAmt(){
    	return getTotalPriceEnteredNet().subtract(getTotalDocumentDiscountUnityAmtNet());
    }
    
    /**
     * Descripción de Método
     *
     */

    public void setLineNetAmt() {

        // Calculations & Rounding

        BigDecimal net = getPriceActual().multiply( getQtyInvoiced());

        if( net.scale() > getPrecision()) {
            net = net.setScale( getPrecision(),BigDecimal.ROUND_HALF_DOWN );
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
            if( !isM_priceSet() && (Env.ZERO.compareTo( getPriceActual()) == 0) && (Env.ZERO.compareTo( getPriceList()) == 0) ) {
                setPrice();
            }
        }
        
        /**
         * Si se detecta que el precio de lista sigue siendo CERO 
         * se toma el precio ingresado y se recalcula
         * 
         * El precio de lista en cero trae problemas a la hora de imprimir documentos como por ej NC's
         * dREHER
         */
        
        if(getPriceList().compareTo(Env.ZERO)==0) {
        	if(getPriceEntered().compareTo(Env.ZERO) > 0) {
        		
        		// dREHER 2024-01-11 en caso de que la linea tenga un descuento, el precio de lista debe ser el ingresado + descuento
        		debug("beforeSave. priceList=0. priceEntered>0...");
        		BigDecimal priceList = getPriceEntered();
        		if(this.getLineBonusAmt().compareTo(Env.ZERO) > 0) {
        			priceList = priceList.add(getLineBonusAmt());
        			
        			debug("getLineBonusAmt=" + getLineBonusAmt() + ". priceList=" + priceList);
        		}
        		
        		setPriceList(priceList);
        		
        	}else {
        		log.warning("No se encontro precio de lista y tampoco se ingreso precio manualmente!");
        		return false;
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
        
        // Actualizo la cantidad facturada en base a la unidad de medida de la ingresada
        if(getQtyEntered().compareTo(getQtyInvoiced()) != 0 && !Util.isEmpty(getM_Product_ID(), true)){
			BigDecimal QtyInvoiced = MUOMConversion.convertProductFrom(getCtx(), getM_Product_ID(), getC_UOM_ID(),
					getQtyEntered());

            if( QtyInvoiced == null ) {
                QtyInvoiced = getQtyEntered();
            }
            
            setQtyInvoiced(QtyInvoiced);
        }

		// Si la factura debe manejar los descuentos arrastrados del pedido y
		// modificó la cantidad o el precio debo recalcular los descuentos
        if(!newRecord){
        	if(!updateDragOrderDiscounts()){
        		// El log se actualiza en el método
        		return false;
        	}
        }
        
		// Actualización de precio en base al descuento manual general
        // Esto es importante dejar antes de actualizar el total de la línea
		if (!isSkipManualGeneralDiscount() && !getInvoice().isTPVInstance()
				&& !getInvoice().isManageDragOrderDiscountsSurcharges(false)
				&& !getInvoice().isVoidProcess()) {
        	updatePriceList();
	}
        
        // Calculations & Rounding

        if(!getInvoice().isTPVInstance() && !getInvoice().isVoidProcess()){
        	setLineNetAmt();
        	setLineNetAmount();
        	// Si la Tarifa tiene impuesto incluido y percepciones incluidas, se actualiza el LineNetAmt y el TaxAmt
           	if( isPerceptionsIncluded() && isTaxIncluded()) {
          		updateTaxAmt();
           		updateLineNetAmt();
           	}
            // Calculo TaxAmt y LineTotalAmt
            // Recupero el impuesto aplicado a la línea
            setTaxAmt();
            setLineTotalAmt(getLineNetAmt().add(getTaxAmt()));
        }

        /* Si el project no está seteado, tomar el de la cabecera */
        if (getC_Project_ID() == 0)
        	setC_Project_ID(DB.getSQLValue(get_TrxName(), " SELECT C_Project_ID FROM C_Invoice WHERE C_Invoice_ID = " + getC_Invoice_ID()));
        
    	// Controlar cantidades por unidad de medida
        if(!MUOM.isAllowedQty(getCtx(), getC_UOM_ID(), getQtyEntered(), get_TrxName())){
			log.saveError(Msg.getMsg(getCtx(), "UOMNotAllowedQty",
					new Object[] { MUOM.get(getCtx(), getC_UOM_ID()).getName(),
							getQtyEntered() }), "");
			return false;
        }
        
        // Validar que el importe de impuesto y neto sean consistentes
    	CallResult cr = validateTaxAmt();
		if(cr.isError()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "TaxAmtInvalid",
					new Object[] { cr.getResult(), getTaxAmt(), getNetTaxBaseAmt()}));
        	return false;
		}       
        
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
	        
	        // Costo
	        if(Util.isEmpty(getCostPrice(), true)){
		        // Seteo el precio de costo
				setCostPrice(MProductPricing.getCostPrice(getCtx(), getAD_Org_ID(),
						getM_Product_ID(), getC_BPartner_Vendor_ID(),
						invoice.getC_Currency_ID(), invoice.getDateInvoiced(), false, 
						isTaxIncluded(), getTaxRate(), isPerceptionsIncluded(),
						get_TrxName()));
	        }
        }        	
        
		/*try {
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
        
        // Chequeo del producto para determinar si está habilitado para comercializar
        if(getM_Product_ID() != 0) {
        	MProduct product = new MProduct(getCtx(), getM_Product_ID(),get_TrxName());
        	if(product.ismarketingblocked()) {
        		log.saveError("Error", product.getmarketingblockeddescr());
    			return false;
        	}
        }
        
		// Si se crea a partir de un remito, entonces se debe copiar el contenido de la
		// columna despacho de importación
		if (getInvoice().isSOTrx() 
				&& ImportClearanceManager.isImportClearanceActive(getCtx())
				&& Util.isEmpty(getM_Import_Clearance_ID(), true) 
				&& !Util.isEmpty(getM_InOutLine_ID(), true)) {
        	Integer icDI = DB.getSQLValue(get_TrxName(), "select M_Import_Clearance_ID from m_inoutline where m_inoutline_id = ?", getM_InOutLine_ID());
        	if(icDI > 0) {
        		setM_Import_Clearance_ID(icDI);
        	}
        }
        
        return true;
    }    // beforeSave

    /**
     * Actualiza los descuentos arrastrados del pedido a nivel de línea y documento 
     */
    public boolean updateDragOrderDiscounts(){
    	boolean dragStatus = true;
		if ((is_ValueChanged("QtyEntered") || is_ValueChanged("PriceList")) 
				&& getInvoice().isManageDragOrderDiscountsSurcharges(false)
				&& !Util.isEmpty(getC_OrderLine_ID(), true)) {
			BigDecimal totalPriceList = getPriceList().multiply(getQtyInvoiced());
			// Si es 0, el precio o la cantidad se modificaron a 0, entonces se
			// debe eliminar los document discounts existentes y setear a 0 los
			// importes de descuentos de la línea
			if(totalPriceList.compareTo(BigDecimal.ZERO) == 0){
				setLineDiscountAmt(BigDecimal.ZERO);
				setLineBonusAmt(BigDecimal.ZERO);
				setDocumentDiscountAmt(BigDecimal.ZERO);
				deleteDragOrderDiscounts();
			}
			// Importe base mayor a 0, se modifican los descuentos arrastrados
			// del pedido, si no tenía antes, se crean
			else{
				Integer tmpPrecision = 10;
				List<MDocumentDiscount> lineDiscounts = MDocumentDiscount.get(
						"C_InvoiceLine_ID = ?",
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
						}
						// Sino en el de línea
						else if (MDocumentDiscount.DISCOUNTAPPLICATION_Bonus.equals(mDocumentDiscount.getDiscountApplication())) {
							bonusDiscountAmt = bonusDiscountAmt.add(discountAmt);				
						}
						else{
							lineDiscountAmt = lineDiscountAmt.add(discountAmt);
						}
						
						mDocumentDiscount.setDiscountBaseAmt(totalPriceList);
						mDocumentDiscount.setDiscountAmt(discountAmt);
						if(!mDocumentDiscount.save()){
							log.saveError("SaveError", CLogger.retrieveErrorAsString());
							return false;
						}
					}
					
					setLineDiscountAmt(lineDiscountAmt);
					setLineBonusAmt(bonusDiscountAmt);
					setDocumentDiscountAmt(documentDiscountAmt);
					
					// Actualizar precio
					if(is_ValueChanged("PriceList")){
						BigDecimal lineDiscountRate = Util.getDiscountRate(totalPriceList, getLineDiscountAmt(), tmpPrecision);
						BigDecimal bonusDiscountRate = Util.getDiscountRate(totalPriceList, getLineBonusAmt(), tmpPrecision);
						setPrice(getPriceList().subtract(getPriceList().multiply(lineDiscountRate.add(bonusDiscountRate))));
					}
				}
				// Si no existen y el tipo de documento está configurado para
				// arrastrar los descuentos del pedido, entonces los creo
				else{
					MDocType docType = MDocType.get(getCtx(), getInvoice().getC_DocTypeTarget_ID());
					setDragDocumentDiscountAmts(docType.isDragOrderDocumentDiscounts());
					setDragLineDiscountAmts(docType.isDragOrderLineDiscounts());
					setDragDocumentSurchargesAmts(docType.isDragOrderDocumentSurcharges());
					setDragLineSurchargesAmts(docType.isDragOrderLineSurcharges());
					setDragOrderPrice(false);
					dragStatus = doDragDiscounts(new MOrderLine(getCtx(), getC_OrderLine_ID(), get_TrxName()),
							MCurrency.getStdPrecision(getCtx(), getInvoice().getC_Currency_ID()), true);
				}
			}
        }
		return dragStatus;
    }
    
    /**
     * Elimina los descuentos arrastrados del pedido para esta línea de factura
     * @return
     */
    private int deleteDragOrderDiscounts(){
		return DB.executeUpdate("DELETE FROM " + MDocumentDiscount.Table_Name + " WHERE C_InvoiceLine_ID = " + getID(),
				get_TrxName());
    }
    
    /**
	 * Actualiza el descuento de la línea en base del descuento manual general
	 * 
	 * @param generalManualDiscount
	 * @param scale
	 */
    public void updateGeneralManualDiscount(BigDecimal generalManualDiscount, int scale){
		// Tener en cuenta el descuento de linea aplicado en esta linea para
		// calcular el descuento manual general
		BigDecimal priceList = getPriceList().compareTo(BigDecimal.ZERO) != 0
				? (getPriceList().subtract(getLineDiscountUnityAmtNet()).subtract(getBonusUnityAmtNet()))
				: getPriceActual();
		BigDecimal lineDiscountAmtUnit = priceList.multiply(
				generalManualDiscount).divide(HUNDRED, scale,
				BigDecimal.ROUND_HALF_DOWN);
		
		// dREHER
		debug("updateGeneralManualDiscount. before... priceList:" + priceList);
		
		// Seteo el precio ingresado con el precio de lista - monto de
		// descuento
		setPrice(priceList.subtract(lineDiscountAmtUnit));
		setManualGeneralDiscountAmt(lineDiscountAmtUnit.multiply(getQtyEntered()));
		setTaxAmt(BigDecimal.ZERO);
	}
    
    /**
     * Salida por consolta
     * @param string
     * dREHER
     */
    private void debug(String string) {
    	System.out.println("==> MInvoiceLine. " + string);
	}

	public BigDecimal getDiscountAmt(BigDecimal baseAmt, BigDecimal discountPerc, Integer scale){
		return getDiscountAmt(baseAmt, discountPerc.divide(HUNDRED, scale,
				BigDecimal.ROUND_HALF_DOWN));
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

		if (!newRecord && is_ValueChanged("C_Tax_ID") && !getInvoice().isTPVInstance()
				&& !getInvoice().isVoidProcess()) {

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
        
        // Guardo los descuentos arrastrados del pedido en caso que existan
		if(!Util.isEmpty(documentDiscountsToSave)){
			for (MDocumentDiscount newDD : documentDiscountsToSave) {
				newDD.setC_Invoice_ID(getC_Invoice_ID());
				newDD.setC_InvoiceLine_ID(getID());
				if(!newDD.save()){
					log.saveError("SaveError", CLogger.retrieveErrorAsString());
					return false;
				}
			}
			documentDiscountsToSave = null;
		}
        
 		// Actualización de la cabecera
        if(shouldUpdateHeader){
	        MInvoice invoice = getInvoice();
	        invoice.setSkipExtraValidations(true);
	        invoice.setSkipModelValidations(true);
	        
	        // dREHER, validar si viene de la actualizacion de factura (MInvoice)
	        // para evitar bucle infinito
        	if(!updateHeaderTax(isSkipManualGeneralDiscount())){
        		return false;
        	}
        	
			// Si debe manejar los descuentos arrastrados de la factura,
			// entonces actualizo el descuento de documento de la cabecera
	        if(invoice.isManageDragOrderDiscountsSurcharges(false)){
	        	try{
	        		invoice.updateTotalDocumentDiscount();
	        	} catch(Exception e){
	        		log.saveError("", e.getMessage());
	        		return false;
	        	}
	        }
	        
        	// Esquema de vencimientos
			MPaymentTerm pt = new MPaymentTerm(getCtx(), invoice.getC_PaymentTerm_ID(), get_TrxName());
			if (!pt.apply(invoice))
				return false;
        }
        
        /**
         * Se encontraron algunos casos donde existe error entre el monto neto y los impuestos
         * 
         * TODO: agregar validacion que controle estos valores y en caso de NO coincidir devolver error
         * 
         * 
         * dREHER
         */
		
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

		// Actualizar el importe de descuento de documento de la cabecera, antes
		// de actualizar el impuesto
        // Si debe manejar los descuentos arrastrados de la factura,
     	// entonces actualizo el descuento de documento de la cabecera
     	if(getInvoice().isManageDragOrderDiscountsSurcharges(false)){
        	try{
        		getInvoice().updateTotalDocumentDiscount();
        	} catch(Exception e){
        		log.saveError("", e.getMessage());
        		return false;
        	}
        }
        
        return !shouldUpdateHeader || updateHeaderTax();
    }    // afterDelete

    /**
     * Sobrecargo metodo para compatibilidad de llamadas varias
     * 
     * dREHER
     */
    private boolean updateHeaderTax() {
    	return updateHeaderTax(false);
    }
    
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updateHeaderTax(boolean isSkipManualGeneralDiscount) {
    	if(getInvoice().isTPVInstance() || getInvoice().isVoidProcess()){
    		return true;
    	}
    	
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
        
        no = getInvoice().updateNetAmount(get_TrxName()); 

        if( no != 1 ) {
            log.warning( "updateHeaderTax (3) #" + no );
        }
        
        getInvoice().setNetAmount(getInvoice().calculateNetAmount(get_TrxName()));

        // Calcular las percepciones
        try{
        	if (!getInvoice().isTPVInstance()) {
    			getInvoice().calculatePercepciones();
        	}
		} catch(Exception e){
			log.severe("ERROR generating percepciones. "+e.getMessage());
			e.printStackTrace();
		}

        try {
        	getInvoice().calculateTotalAmounts();
	    } catch(Exception e){
			log.severe("ERROR calculate totales. "+e.getMessage());
			e.printStackTrace();
		}
        
        if(!isSkipManualGeneralDiscount) {
        	if(!getInvoice().save()) {
        		log.severe(CLogger.retrieveErrorAsString());
        		no = 0;
        	}
        }else
        	log.warning("Esta realizando recalculo de descuento global, no guardar comprobante!");           
        return no == 1;
    }    // updateHeaderTax
    
    /** Devuelve la descripcion del producto asociado a la línea */
    public String getProductName()
    {
    	if (getM_Product_ID() > 0){
    		
    		// dREHER, verificar si en las preferencias esta marcado que debe imprimir la marca en los productos    		
    		String mostrarMarcas = MPreference.GetCustomPreferenceValue("print_mark_in_invoices");
    		if(mostrarMarcas.isEmpty())
    			mostrarMarcas = "N";
    		
    		//MProduct prod = new MProduct(p_ctx, getM_Product_ID(), null);
    		//soporte para caches multi-documento
    		MProduct prod = getProduct();
    		//puede ser null... aunque no deberia
    		String prodName = prod == null? "" : prod.getName();
    		
    		
    		String marca = null;
    		if(mostrarMarcas.equals("Y")) {
    			int familiaMarca = prod.getM_Product_Family_ID();
    			if(familiaMarca > 0) {
    				X_M_Product_Family pf = new X_M_Product_Family(Env.getCtx(), familiaMarca, get_TrxName());
    				if(pf!=null)
    					marca = pf.getName();
    			}
    		}
    		
    		//return getDescription() == null ? prod.getName() : (prod.getName() + " - " + getDescription());
       		return getDescription() == null ? prodName + (marca!=null ? " - " + marca : "") : (prodName + (marca!=null ? " - " + marca : "") + " - " + getDescription());
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
     * @return nombre del impuesto asociado a la línea
     */
    public String getTaxName() {
		String taxName = null;
		if (getC_Tax_ID() > 0) {
			MTax tax = MTax.get(getCtx(), getC_Tax_ID(), get_TrxName());
			taxName = tax !=  null ? tax.getName() : taxName;
		}
		return taxName;
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
	 * @return descuento MANUAL de documento con impuestos por unidad, o sea, descuento
	 *         de documento con impuestos / cantidad ingresada. NO MODIFICAR
	 *         FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getManualDocumentDiscountUnityAmtWithTax(){
    	BigDecimal unityAmt = getUnityAmt(getManualGeneralDiscountAmt());
		return amtByTax(unityAmt, getTaxAmt(unityAmt), isTaxIncluded(), true);
    }
    
    /**
	 * @return descuento MANUAL de documento sin impuestos por unidad, o sea, descuento
	 *         de documento sin impuestos / cantidad ingresada. NO MODIFICAR
	 *         FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getManualDocumentDiscountUnityAmtNet(){
    	BigDecimal unityAmt = getUnityAmt(getManualGeneralDiscountAmt());
		return amtByTax(unityAmt, getTaxAmt(unityAmt), isTaxIncluded(), false);
    }
    
    /**
	 * @return descuento MANUAL de documento con impuestos. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getManualTotalDocumentDiscountUnityAmtWithTax(){
		return amtByTax(getManualGeneralDiscountAmt(), getTaxAmt(getManualGeneralDiscountAmt()),
				isTaxIncluded(), true);
    }
    
    /**
	 * @return descuento MANUAL de documento sin impuestos. NO MODIFICAR FIRMA, SE USA EN LA
	 *         IMPRESIÓN DE LA FACTURA
	 */
    public BigDecimal getManualTotalDocumentDiscountUnityAmtNet(){
		return amtByTax(getManualGeneralDiscountAmt(), getTaxAmt(getManualGeneralDiscountAmt()),
				isTaxIncluded(), false);
    }

/** dREHER, si se encuentra seteado el flag de ocultar descuento en el socio de negocios
     * se debe igualar el PriceList con el PriceEntered
     */
    public BigDecimal getPriceList(boolean isVerificaMarcaOcultarDescuento) {

    	BigDecimal priceList = super.getPriceList();
    	
    	// si viene con la opcion de verificar ocultar descuento, SOLO utilizado para las A4
    	// dREHER
    	if(isVerificaMarcaOcultarDescuento) {

    		int C_Invoice_ID = getC_Invoice_ID();

    		// en caso de que no haya precio ingresado manual, debe seguir trayendo precio de lista
    		if(getPriceEntered().compareTo(Env.ZERO) > 0) {

    			MInvoice inv = new MInvoice(getCtx(), C_Invoice_ID, get_TrxName());
    			if(inv!=null) {
    				MBPartner bp = new MBPartner(getCtx(), inv.getC_BPartner_ID(), get_TrxName());
    				if(bp!=null) {
    					boolean isOcultarDesctoLineaFC = false;
    					if(bp.get_Value("IsOcultarDesctoLineaFC")!=null) {
    						isOcultarDesctoLineaFC = (Boolean)bp.get_Value("IsOcultarDesctoLineaFC");
    					}
    					if(isOcultarDesctoLineaFC)
    						priceList = getPriceEntered();

    				}
    			}

    		}

    	}
    	
    	log.info("getPriceList desde micro componente Facturacion :" + priceList);
    	
    	return priceList;
    }
    
    /** dREHER, si se encuentra seteado el flag de ocultar descuento en el socio de negocios
     * se debe igualar el PriceList con el PriceEntered
     */
    public BigDecimal getPriceList() {

    	BigDecimal priceList = getPriceList(true);
    	
    	log.info("getPriceList desde micro componente Facturacion  (NO verifica ocultar descuento - estandar):" + priceList);
    	
    	return priceList;
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
		return amt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : amt
				.divide(getQtyEntered(), amt.scale(),
						BigDecimal.ROUND_HALF_DOWN);
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

		@Override
		public void setDocumentReferences(MDocumentDiscount documentDiscount) {
			documentDiscount.setC_OrderLine_ID(MInvoiceLine.this.getC_OrderLine_ID());
			documentDiscount.setC_InvoiceLine_ID(MInvoiceLine.this.getID());
		}

		@Override
		public BigDecimal getDocumentDiscountAmt() {
			return MInvoiceLine.this.getDocumentDiscountAmt();
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
			return MInvoiceLine.this.getID();
		}

		@Override
		public void setGeneratedInvoiceLineID(Integer generatedInvoiceLineID) {
			// En este método no hace nada ya que estamos bajo una factura
		}

		@Override
		public int getProductCategoryID() {
			return MProduct.get(MInvoiceLine.this.getCtx(), getProductID()).getM_Product_Category_ID();
		}

		@Override
		public int getProductGamasID() {
			return MProductGamas.getGamaIDFromCategory(getProductCategoryID(), MInvoiceLine.this.get_TrxName());
		}

		@Override
		public int getProductLinesID() {
			return MProductLines.getProductLineIDFromGama(getProductGamasID(), MInvoiceLine.this.get_TrxName());
		}

		@Override
		public List<Integer> getProductVendorIDs() {
			return MProductPO.getBPartnerIDsOfProduct(MInvoiceLine.this.getCtx(), getProductID(), true,
					MInvoiceLine.this.get_TrxName());
		}

		@Override
		public BigDecimal getNetAmt() {
			return MInvoiceLine.this.getLineNetAmount();
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
        		net = net.divide( (BigDecimal.ONE.add(rate).add(tax)), 6, BigDecimal.ROUND_HALF_DOWN);
            } catch (Exception e) {
    			e.printStackTrace();
    		}	
        }
        else{
        	// Si la Tarifa tiene solo impuesto incluido, pero sin percepciones incluidas, el neto se calcula haciendo: monto / (1 + Tasa de Impuesto)
        	if(isTaxIncluded()){
        		net = net.divide( (BigDecimal.ONE.add(tax)), 2, BigDecimal.ROUND_HALF_DOWN );
        	}
        }

        if( net.scale() > getPrecision()) {
            net = net.setScale( getPrecision(),BigDecimal.ROUND_HALF_DOWN );
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
            net = net.setScale( getPrecision(),BigDecimal.ROUND_HALF_DOWN );
        }
        super.setLineNetAmt(net);
    }    // setLineNetAmt
    
    /**
     * Descripción de Método
     * @throws Exception 
     *
     */
    public void updateTaxAmt() {
    	BigDecimal tax = getTaxRate().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_DOWN);
    	BigDecimal net = getLineNetAmt();
         
    	GeneratorPercepciones generator = new GeneratorPercepciones(getCtx(), getInvoice().getDiscountableWrapper(), get_TrxName());
        try {
        	BigDecimal rate = generator.totalPercepcionesRate();
        	rate = rate.divide(new BigDecimal(100));
    		net = net.divide( (BigDecimal.ONE.add(rate).add(tax)), 6, BigDecimal.ROUND_HALF_DOWN);
		} catch (Exception e) {
			e.printStackTrace();
		}	
    	
        BigDecimal taxAmt = net.multiply(tax);
        if( taxAmt.scale() > getPrecision()) {
        	taxAmt = taxAmt.setScale( getPrecision(),BigDecimal.ROUND_HALF_DOWN );
        }
        super.setTaxAmt(taxAmt);
    }    // setLineNetAmt
    
    private void updateNetAmount() {
		BigDecimal taxamt = DB.getSQLValueBD(get_TrxName(), "SELECT coalesce(SUM(it.taxamt),0) FROM C_InvoiceTax it WHERE (it.C_Invoice_ID = ?)", getC_Invoice_ID());
		taxamt = taxamt == null?BigDecimal.ZERO:taxamt;
		BigDecimal grandTotal = DB.getSQLValueBD(get_TrxName(), "SELECT coalesce(i.grandtotal,0) FROM C_Invoice i WHERE (i.C_Invoice_ID = ?)", getC_Invoice_ID());
		BigDecimal taxbaseamt = DB.getSQLValueBD(get_TrxName(), "SELECT coalesce(it.taxbaseamt,0) FROM C_InvoiceTax it WHERE (it.C_Invoice_ID = ?) ORDER BY it.Created DESC LIMIT 1", getC_Invoice_ID());
		taxbaseamt = taxbaseamt == null?BigDecimal.ZERO:taxbaseamt;
		// Si existe un diferencia de hasta 0.02 se ajusta el neto.
		if((Math.abs((grandTotal.subtract(taxamt).subtract(taxbaseamt)).doubleValue()) >= 0.01) && (Math.abs((grandTotal.subtract(taxamt).subtract(taxbaseamt)).doubleValue()) <= 0.02)){
			String queryUpdate = "UPDATE C_Invoice SET NetAmount = " + (grandTotal.subtract(taxamt)) + " WHERE (C_Invoice_ID = " + getInvoice().getC_Invoice_ID() + ")";
			getInvoice().setNetAmount(grandTotal.subtract(taxamt));
			DB.executeUpdate(queryUpdate, get_TrxName());
    	  }
	}

	public boolean isM_priceSet() {
		return m_priceSet;
	}

	public void setM_priceSet(boolean m_priceSet) {
		this.m_priceSet = m_priceSet;
	}

	public boolean isDragLineSurchargesAmts() {
		return dragLineSurchargesAmts;
	}

	public void setDragLineSurchargesAmts(boolean dragLineSurchargesAmts) {
		this.dragLineSurchargesAmts = dragLineSurchargesAmts;
	}

	public boolean isDragDocumentSurchargesAmts() {
		return dragDocumentSurchargesAmts;
	}

	public void setDragDocumentSurchargesAmts(boolean dragDocumentSurchargesAmts) {
		this.dragDocumentSurchargesAmts = dragDocumentSurchargesAmts;
	}

	public MProductPricing createProductPricing(Integer priceListID, Integer bPartnerID, Timestamp dateInvoiced, boolean isSOTrx) {
		MProductPricing pp = new MProductPricing(getM_Product_ID(), bPartnerID, getQtyInvoiced(), isSOTrx,
				!getInvoice().isManageDragOrderDiscountsSurcharges(false));
        pp.setM_PriceList_ID( priceListID );
        pp.setPriceDate( dateInvoiced );
		return pp;
	}
	
	/**
	 * @return el número del despacho de importación para esta línea.
	 * NO MODIFICAR FIRMA, SE USA EN LA IMPRESIÓN DE LA FACTURA
	 */
	public String getClearanceNumber() {
		String cn = null;
		if(!Util.isEmpty(getM_Import_Clearance_ID(), true)) {
			cn = DB.getSQLValueString(get_TrxName(),
					"select clearancenumber from m_import_clearance where m_import_clearance_id = ?",
					getM_Import_Clearance_ID());
		}
		return cn;
	}
	
	public void updatePriceList() {
		// Descuento de Entidad Comercial
		MProductPricing pp = createProductPricing(getInvoice().getM_PriceList_ID(),
				getInvoice().getC_BPartner_ID(), getInvoice().getDateInvoiced(), getInvoice().isSOTrx());
		pp.calculatePrice();
		pp.calculateDiscount(getPriceList());
		if (getQtyEntered() != null && pp.getDiscountSchema() != null) {
			// Calcular cuanto es el porcentaje de descuento aplicado 
			// y aplicarlo al precio de lista de la linea
			setPrice(pp.getPriceStd());
			
			BigDecimal discountAmt = pp.discountAmt.multiply(getQtyEntered());
			if (MDiscountSchema.DISCOUNTAPPLICATION_DiscountToPrice
					.equals(pp.getDiscountSchema().getDiscountApplication())) {
				setLineDiscountAmt(discountAmt);
			}
			else {
				setLineBonusAmt(discountAmt);
			}
        }
    	
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

/**
	 * Validación del importe de impuesto automático respecto al importe base y tasa
	 * 
	 * @return resultado de la operación
	 */
    private CallResult validateTaxAmt() {
    	CallResult cr = new CallResult();
    	BigDecimal taxBaseAmt = getTaxBaseAmtToEvaluateTax();
    	// Si la tasa de impuesto es mayor a 0 y el importe de impuesto es menor o igual a 0
        MTax tax = MTax.get(getCtx(), getC_Tax_ID(), get_TrxName());
        MTaxCategory tc = MTaxCategory.get(getCtx(), tax.getC_TaxCategory_ID(), get_TrxName());
        if(!tc.isManual() && !Util.isEmpty(tax.getRate(), true)) {
        	// Verificar que tenga coherencia el importe de impuesto y el importe base
        	// Casos a verificar
        	// 1) Importe Base = 0 e Importe Impuesto > 0
        	// 2) Importe Base > 0 e Importe Impuesto <= 0
        	// 3) Importe Base > 0 e Importe Impuesto >= Importe Base
			if ((taxBaseAmt.compareTo(BigDecimal.ZERO) == 0 && getTaxAmt().compareTo(BigDecimal.ZERO) > 0)
					|| (taxBaseAmt.compareTo(BigDecimal.ZERO) > 0 && (getTaxAmt().compareTo(BigDecimal.ZERO) <= 0
							|| getTaxAmt().compareTo(taxBaseAmt) >= 0))) {
        		cr.setError(true);
	        	cr.setResult(tax.getRate());
	        	return cr;
        	}
        	
        	// Si pasó la validación anterior significa que existe importe de iva y base,
    		// verificar si es correcto
			String p = MPreference.searchCustomPreferenceValue(TAXAMT_TOLERANCE_PREFERENCE_NAME, getAD_Client_ID(),
					getAD_Org_ID(), getCreatedBy(), false);
			BigDecimal tolerance = Util.isEmpty(p) ? BigDecimal.ZERO : new BigDecimal(p);
			MPriceList pl = new MPriceList(getCtx(), getInvoice().getM_PriceList_ID(), get_TrxName());
			BigDecimal newTaxAmt = tax.calculateTax(taxBaseAmt, pl.isTaxIncluded(), pl.getStandardPrecision());
			BigDecimal diff = newTaxAmt.subtract(getTaxAmt()).abs();
			if(diff.compareTo(tolerance) > 0) {
	        	cr.setError(true);
	        	cr.setResult(tax.getRate());
	        	return cr;
			}
        }
        return cr;
    }
}    // MInvoiceLine



/*
 *  @(#)MInvoiceLine.java   02.07.07
 * 
 *  Fin del fichero MInvoiceLine.java
 *  
 *  Versión 2.2
 *
 */
