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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trace;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProductPricing implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Obtengo el precio de costo de un artículo en base al siguiente orden:
	 * <ol>
	 * <li>Tarifa de costo relacionado al proveedor parámetro.</li>
	 * <li>Tarifas de costo de la organización parámetro ordenadas por defecto
	 * descendiente esto hace que las tarifas por defecto queden prioritarias
	 * con respecto a las restantes.</li>
	 * <li>Tarifas de costo de todas las organizaciones ordenadas por defecto
	 * descendiente esto hace que las tarifas por defecto queden prioritarias
	 * con respecto a las restantes.</li>
	 * <li>Precio configurado en la relación entre el artículo y el proveedor
	 * parámetro.</li>
	 * </ol>
	 * 
	 * @param ctx
	 *            contexto
	 * @param orgID
	 *            id de organización
	 * @param productID
	 *            id de artículo
	 * @param vendorID
	 *            id de proveedor
	 * @param convertCurrencyID
	 *            id de moneda a la cual se debe convertir
	 * @param convertDate
	 *            fecha a la cual se debe convertir
	 * @param manageTax
	 *            true si se debe incrementar/decrementar el impuesto en el
	 *            precio dependiendo del parámetro de inclusión de impuesto y de
	 *            la tarifa de costo encontrada
	 * @param taxIncluded
	 *            el monto de impuesto está incluído en la operación actual que
	 *            consulta este precio de costo, este dato es necesario sólo si
	 *            se debe gestionar el incremento/decremento de impuestos
	 * @param taxRate
	 *            la tasa de impuesto en la operación actual que consulta este
	 *            precio de costo, este dato es necesario sólo si se debe
	 *            gestionar el incremento/decremento de impuestos
	 * @param percepcionIncluded
	 *            el monto de percepciones está incluído en la operación actual
	 *            que consulta este precio de costo, este dato es necesario sólo
	 *            si se debe gestionar el incremento/decremento de impuestos
	 * @param trxName
	 *            transacción actual
	 * @return el precio de costo obtenido o 0 en caso de no encontrar ninguno
	 */
	public static BigDecimal getCostPrice(Properties ctx, Integer orgID, Integer productID, Integer vendorID, Integer convertCurrencyID, Timestamp convertDate, boolean manageTax, boolean taxIncluded, BigDecimal taxRate, boolean percepcionIncluded, String trxName){
		BigDecimal costPrice = BigDecimal.ZERO;
		int deltaTax = 0;
		int costCurrency = Env.getContextAsInt(ctx, "$C_Currency_ID");
		// 1) Tarifas de costo del proveedor
		if(!Util.isEmpty(vendorID, true)){
			MBPartner vendor = new MBPartner(ctx, vendorID, trxName);
			if(!Util.isEmpty(vendor.getPO_PriceList_ID(), true)){
				MProductPrice pp = MProductPrice.getProductPrice(ctx,
						productID, 0, vendor.getPO_PriceList_ID(), false,
						trxName);
				if(pp != null){
					costPrice = pp.getPriceStd();
					// Determino si tengo que decrementar el impuesto y la moneda
					MPriceList priceList = MPriceList.get(ctx,
							vendor.getPO_PriceList_ID(), trxName);
					if(taxIncluded != priceList.isTaxIncluded()){
						deltaTax = taxIncluded?1:-1;
					}
					costCurrency = priceList.getC_Currency_ID();
				}
			}
			
		}
		// 2) Tarifas de costo (primero la de la organización de la factura,
		// sino todas)
		if(costPrice.compareTo(BigDecimal.ZERO) == 0){
			MProductPrice pp = MProductPrice.getProductPrice(ctx, productID,
					orgID, null, false, trxName);
			if(pp != null){
				costPrice = pp.getPriceStd();
				// Determino si tengo que decrementar el impuesto y la moneda
				MPriceListVersion priceListVersion = new MPriceListVersion(ctx,
						pp.getM_PriceList_Version_ID(), trxName);
				MPriceList priceList = MPriceList.get(ctx,
						priceListVersion.getM_PriceList_ID(), trxName);
				if(taxIncluded != priceList.isTaxIncluded()){
					deltaTax = taxIncluded?1:-1;
				}
				costCurrency = priceList.getC_Currency_ID();
			}
			else{
				pp = MProductPrice.getProductPrice(ctx, productID,	0, null, false, trxName);
				if(pp != null){
					costPrice = pp.getPriceStd();
					// Determino si tengo que decrementar el impuesto y la moneda
					MPriceListVersion priceListVersion = new MPriceListVersion(
							ctx, pp.getM_PriceList_Version_ID(), trxName);
					MPriceList priceList = MPriceList.get(ctx,
							priceListVersion.getM_PriceList_ID(), trxName);
					if(taxIncluded != priceList.isTaxIncluded()){
						deltaTax = taxIncluded?1:-1;
					}
					costCurrency = priceList.getC_Currency_ID();
				}
			}
		}
		
		// 3) m_producto_po con ese proveedor
		if(costPrice.compareTo(BigDecimal.ZERO) == 0){
			MProductPO po = null;
			// Si no tengo a priori el PO, entonces lo busco
			if(!Util.isEmpty(vendorID, true)){
				// Obtención del po
				po = MProductPO.get(ctx, productID, vendorID, trxName);
			}
			// Si puedo obtener el precio de ahí entonces lo obtengo
			if(po != null){
				costPrice = po.getPriceList();
				// Verificar la moneda si hay que convertir
				costCurrency = po.getC_Currency_ID();
			}
		}
		// Seteo el precio de costo
		BigDecimal costConverted = costPrice;
		if(costPrice.compareTo(BigDecimal.ZERO) > 0){
			costConverted = MConversionRate.convert(ctx, costPrice,
					costCurrency, convertCurrencyID, convertDate, 0,
					Env.getAD_Client_ID(ctx), orgID);
			costConverted = costConverted != null?costConverted:costPrice;
		}
		else{
			deltaTax = 0;
		}

		// Decrementar/incrementar el monto de impuesto al precio de costo
		// si las tarifas difieren en el campo impuesto incluido. Si la
		// tarifa de ventas de esta factura posee impuesto incluido y la de
		// costo no, entonces se debe agregar el impuesto al costo, caso
		// contrario decrementar. En el caso que no difieran en ese campo,
		// no se incrementa ni decrementa
		if(manageTax && deltaTax != 0){
			BigDecimal costTaxAmt = MTax.calculateTax(costConverted, true,
					percepcionIncluded, taxRate, 2);
			costPrice = costConverted.add(costTaxAmt.multiply(new BigDecimal(
					deltaTax)));
		}
		
		return costPrice;
	}
	
	/**
     * Constructor de la clase ...
     *
     *
     * @param M_Product_ID
     * @param C_BPartner_ID
     * @param Qty
     * @param isSOTrx
     */

    public MProductPricing( int M_Product_ID,int C_BPartner_ID,BigDecimal Qty,boolean isSOTrx ) {
        m_M_Product_ID  = M_Product_ID;
        m_C_BPartner_ID = C_BPartner_ID;

        if( (Qty != null) && (Env.ZERO.compareTo( Qty ) != 0) ) {
            m_Qty = Qty;
        }

        m_isSOTrx = isSOTrx;
    }    // MProductPricing

    public MProductPricing( int M_Product_ID,int C_BPartner_ID,BigDecimal Qty,boolean isSOTrx,int M_AttributeSetInstance_ID ) {
        this(M_Product_ID, C_BPartner_ID, Qty, isSOTrx);
        
        this.m_M_AttributeSetInstance_ID = M_AttributeSetInstance_ID;
    }    // MProductPricing
    
    /** Descripción de Campos */

    private int m_M_Product_ID;

    /** Descripción de Campos */
    
    private int m_M_AttributeSetInstance_ID;
    
    /** Descripción de Campos */

    private int m_C_BPartner_ID;

    /** Descripción de Campos */

    private BigDecimal m_Qty = Env.ONE;

    /** Descripción de Campos */

    private boolean m_isSOTrx = true;

    //

    /** Descripción de Campos */

    private int m_M_PriceList_ID = 0;

    /** Descripción de Campos */

    private int m_M_PriceList_Version_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_PriceDate;

    /** Descripción de Campos */

    private int m_precision = -1;

    /** Descripción de Campos */

    private boolean m_calculated = false;

    /** Descripción de Campos */

    private Boolean m_found = null;

    /** Descripción de Campos */

    private BigDecimal m_PriceList = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_PriceStd = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_PriceLimit = Env.ZERO;

    /** Descripción de Campos */

    private int m_C_Currency_ID = 0;

    /** Descripción de Campos */

    private boolean m_enforcePriceLimit = false;

    /** Descripción de Campos */

    private int m_C_UOM_ID = 0;

    /** Descripción de Campos */

    private int m_M_Product_Category_ID;

    /** Descripción de Campos */

    private boolean m_discountSchema = false;

    /** Descripción de Campos */

    private boolean m_isTaxIncluded = false;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

	/**
	 * Contexto de aplicación de descuentos. Debe llevar los valores que
	 * contiene la columna DiscountContext de {@link MBPartner} accedido por
	 * medio de {@link MBPartner#getDiscountContext()}.
	 */
    private String context = MBPartner.DISCOUNTCONTEXT_Bill;
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean calculatePrice() {
        if( (m_M_Product_ID == 0) || ( (m_found != null) &&!m_found.booleanValue())) {
            return false;
        }

        // Price List Version known
        // m_calculated = calculatePLV ();
        // Price List known

        if( !m_calculated ) {
            m_calculated = calculatePL();
        }

        // Base Price List used

        if( !m_calculated ) {
            m_calculated = calculateBPL();
        }

        // Set UOM, Prod.Category

        if( !m_calculated ) {
            setBaseInfo();
        }

        // User based Discount

        if( m_calculated ) {
            calculateDiscount();
        }

        setPrecision();    // from Price List

        //

        m_found = new Boolean( m_calculated );

        return m_calculated;
    }    // calculatePrice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean calculatePLV() {
        if( (m_M_Product_ID == 0) || (m_M_PriceList_Version_ID == 0) ) {
            return false;
        }

        //

        String sql = "SELECT bomPriceStd(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceStd,"    // 1
                     + " bomPriceList(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceList,"    // 2
                     + " bomPriceLimit(p.M_Product_ID,pv.M_PriceList_Version_ID) AS PriceLimit,"    // 3
                     + " p.C_UOM_ID,pv.ValidFrom,pl.C_Currency_ID,p.M_Product_Category_ID,"    // 4..7
                     + " pl.EnforcePriceLimit, pl.IsTaxIncluded "                                                                                                                                                                                                                                                                                // 8..9
                     + "FROM M_Product p" + " INNER JOIN M_ProductPrice pp ON (p.M_Product_ID=pp.M_Product_ID)" + " INNER JOIN  M_PriceList_Version pv ON (pp.M_PriceList_Version_ID=pv.M_PriceList_Version_ID)" + " INNER JOIN M_Pricelist pl ON (pv.M_PriceList_ID=pl.M_PriceList_ID) " + "WHERE pv.IsActive='Y'" + " AND p.M_Product_ID=?"    // #1
                     + " AND pv.M_PriceList_Version_ID=?";    // #2

        m_calculated = false;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_M_Product_ID );
            pstmt.setInt( 2,m_M_PriceList_Version_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // Prices

                m_PriceStd = rs.getBigDecimal( 1 );

                if( rs.wasNull()) {
                    m_PriceStd = Env.ZERO;
                }

                m_PriceList = rs.getBigDecimal( 2 );

                if( rs.wasNull()) {
                    m_PriceList = Env.ZERO;
                }

                m_PriceLimit = rs.getBigDecimal( 3 );

                if( rs.wasNull()) {
                    m_PriceLimit = Env.ZERO;
                }

                //

                m_C_UOM_ID              = rs.getInt( 4 );
                m_C_Currency_ID         = rs.getInt( 6 );
                m_M_Product_Category_ID = rs.getInt( 7 );
                m_enforcePriceLimit     = "Y".equals( rs.getString( 8 ));
                m_isTaxIncluded         = "Y".equals( rs.getString( 9 ));

                //

                log.fine( "M_PriceList_Version_ID=" + m_M_PriceList_Version_ID + " - " + m_PriceStd );
                m_calculated = true;
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
            m_calculated = false;
        }

        return m_calculated;
    }    // calculatePLV

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean calculatePL() {
        if( m_M_Product_ID == 0 ) {
            return false;
        }

        // Get Price List

        if( m_M_PriceList_ID == 0 ) {
            log.log( Level.SEVERE,"No PriceList" );
            Trace.printStack();

            return false;
        }

        // Get Prices for Price List

        String sql = "SELECT bomPriceStd(p.M_Product_ID,pv.M_PriceList_Version_ID,?) AS PriceStd,"    // 1
                     + " bomPriceList(p.M_Product_ID,pv.M_PriceList_Version_ID,?) AS PriceList,"    // 2
                     + " bomPriceLimit(p.M_Product_ID,pv.M_PriceList_Version_ID,?) AS PriceLimit,"    // 3
                     + " p.C_UOM_ID,pv.ValidFrom,pl.C_Currency_ID,p.M_Product_Category_ID,pl.EnforcePriceLimit "    // 4..8
                     + "FROM M_Product p" + " INNER JOIN M_ProductPrice pp ON (p.M_Product_ID=pp.M_Product_ID)" + " INNER JOIN  M_PriceList_Version pv ON (pp.M_PriceList_Version_ID=pv.M_PriceList_Version_ID)" + " INNER JOIN M_Pricelist pl ON (pv.M_PriceList_ID=pl.M_PriceList_ID) " + "WHERE pv.IsActive='Y'" + " AND p.M_Product_ID=?"    // #1
                     + " AND pv.M_PriceList_ID=? "    // #2
                     + " ORDER BY pv.ValidFrom DESC";

        m_calculated = false;

        if( m_PriceDate == null ) {
            m_PriceDate = new Timestamp( System.currentTimeMillis());
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            int pn = 1;
            
            pstmt.setInt( pn++, m_M_AttributeSetInstance_ID );
            pstmt.setInt( pn++, m_M_AttributeSetInstance_ID );
            pstmt.setInt( pn++, m_M_AttributeSetInstance_ID );
            
            pstmt.setInt( pn++, m_M_Product_ID );
            pstmt.setInt( pn++, m_M_PriceList_ID );

            ResultSet rs = pstmt.executeQuery();

            while( !m_calculated && rs.next()) {
                Timestamp plDate = rs.getTimestamp( 5 );

                // we have the price list
                // if order date is after or equal PriceList validFrom

                if( (plDate == null) ||!m_PriceDate.before( plDate )) {

                    // Prices

                    m_PriceStd = rs.getBigDecimal( 1 );

                    if( rs.wasNull()) {
                        m_PriceStd = Env.ZERO;
                    }

                    m_PriceList = rs.getBigDecimal( 2 );

                    if( rs.wasNull()) {
                        m_PriceList = Env.ZERO;
                    }

                    m_PriceLimit = rs.getBigDecimal( 3 );

                    if( rs.wasNull()) {
                        m_PriceLimit = Env.ZERO;
                    }

                    //

                    m_C_UOM_ID              = rs.getInt( 4 );
                    m_C_Currency_ID         = rs.getInt( 6 );
                    m_M_Product_Category_ID = rs.getInt( 7 );
                    m_enforcePriceLimit     = "Y".equals( rs.getString( 8 ));

                    //

                    log.fine( "M_PriceList_ID=" + m_M_PriceList_ID + "(" + plDate + ")" + " - " + m_PriceStd );
                    m_calculated = true;

                    break;
                }
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
            m_calculated = false;
        }

        return m_calculated;
    }    // calculatePL

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean calculateBPL() {
        if( (m_M_Product_ID == 0) || (m_M_PriceList_ID == 0) ) {
            return false;
        }

        //

        String sql = "SELECT bomPriceStd(p.M_Product_ID,pv.M_PriceList_Version_ID,?) AS PriceStd,"    // 1
                     + " bomPriceList(p.M_Product_ID,pv.M_PriceList_Version_ID,?) AS PriceList,"    // 2
                     + " bomPriceLimit(p.M_Product_ID,pv.M_PriceList_Version_ID,?) AS PriceLimit,"    // 3
                     + " p.C_UOM_ID,pv.ValidFrom,pl.C_Currency_ID,p.M_Product_Category_ID,"    // 4..7
                     + " pl.EnforcePriceLimit, pl.IsTaxIncluded "                                                                                                                                                                                                                                                                                                                                                             // 8..9
                     + "FROM M_Product p" + " INNER JOIN M_ProductPrice pp ON (p.M_Product_ID=pp.M_Product_ID)" + " INNER JOIN  M_PriceList_Version pv ON (pp.M_PriceList_Version_ID=pv.M_PriceList_Version_ID)" + " INNER JOIN M_Pricelist bpl ON (pv.M_PriceList_ID=bpl.M_PriceList_ID)" + " INNER JOIN M_Pricelist pl ON (bpl.M_PriceList_ID=pl.BasePriceList_ID) " + "WHERE pv.IsActive='Y'" + " AND p.M_Product_ID=?"    // #1
                     + " AND pl.M_PriceList_ID=?"    // #2
                     + "ORDER BY pv.ValidFrom DESC";

        m_calculated = false;

        if( m_PriceDate == null ) {
            m_PriceDate = new Timestamp( System.currentTimeMillis());
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            int pn = 1;
            
            pstmt.setInt( pn++,m_M_AttributeSetInstance_ID );
            pstmt.setInt( pn++,m_M_AttributeSetInstance_ID );
            pstmt.setInt( pn++,m_M_AttributeSetInstance_ID );
            
            pstmt.setInt( pn++,m_M_Product_ID );
            pstmt.setInt( pn++,m_M_PriceList_ID );

            ResultSet rs = pstmt.executeQuery();

            while( !m_calculated && rs.next()) {
                Timestamp plDate = rs.getTimestamp( 5 );

                // we have the price list
                // if order date is after or equal PriceList validFrom

                if( (plDate == null) ||!m_PriceDate.before( plDate )) {

                    // Prices

                    m_PriceStd = rs.getBigDecimal( 1 );

                    if( rs.wasNull()) {
                        m_PriceStd = Env.ZERO;
                    }

                    m_PriceList = rs.getBigDecimal( 2 );

                    if( rs.wasNull()) {
                        m_PriceList = Env.ZERO;
                    }

                    m_PriceLimit = rs.getBigDecimal( 3 );

                    if( rs.wasNull()) {
                        m_PriceLimit = Env.ZERO;
                    }

                    //

                    m_C_UOM_ID              = rs.getInt( 4 );
                    m_C_Currency_ID         = rs.getInt( 6 );
                    m_M_Product_Category_ID = rs.getInt( 7 );
                    m_enforcePriceLimit     = "Y".equals( rs.getString( 8 ));
                    m_isTaxIncluded         = "Y".equals( rs.getString( 9 ));

                    //

                    log.fine( "M_PriceList_ID=" + m_M_PriceList_ID + "(" + plDate + ")" + " - " + m_PriceStd );
                    m_calculated = true;

                    break;
                }
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
            m_calculated = false;
        }

        return m_calculated;
    }    // calculateBPL

    /**
     * Descripción de Método
     *
     */

    private void setBaseInfo() {
        if( m_M_Product_ID == 0 ) {
            return;
        }

        //

        String sql = "SELECT C_UOM_ID, M_Product_Category_ID FROM M_Product WHERE M_Product_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_C_UOM_ID              = rs.getInt( 1 );
                m_M_Product_Category_ID = rs.getInt( 2 );
            }

            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }
    }    // setBaseInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTaxIncluded() {
        return m_isTaxIncluded;
    }    // isTaxIncluded

    /**
     * Descripción de Método
     *
     */

    private void calculateDiscount() {
        m_discountSchema = false;

        if( (m_C_BPartner_ID == 0) || (m_M_Product_ID == 0) ) {
            return;
        }
        // Reemplazado código original por Calculador de Descuentos
		// FIXME Podemos saber aca si estamos en el momento de Cobro? Por ahora
		// se deja como momento de Compra. Mejor dicho, al crear este objeto
		// podemos saber donde estamos?
        MBPartner bpartner = new MBPartner(Env.getCtx(), m_C_BPartner_ID, null);
        DiscountCalculator calculator = DiscountCalculator.create(m_C_BPartner_ID, m_isSOTrx, context, bpartner.getDiscountContext());

        m_discountSchema = calculator.getBPartnerDiscountSchema() != null;
        //Modificado por ConSerTi para que coja el precio de tarifa no el estandar 
        // m_PriceStd       = sd.calculatePrice( m_Qty,m_PriceStd,m_M_Product_ID,m_M_Product_Category_ID,FlatDiscount );
        
        // Revertido por Disytel - Si calculamos el descuento del PriceSTD el parámetro
        // debe ser necesariamente PriceSTD (sino se asigna el PriceList al PriceSTD). 
        // Hay que tener en cuenta que el PriceSTD se lee de la BD con lo cual no hay forma
        // de que se apliquen descuentos sucesivos que produzcan valores incorrectos
        m_PriceStd       = calculator.calculatePrice(m_PriceStd, m_Qty, m_M_Product_Category_ID,m_M_Product_ID, m_PriceDate);
    }    // calculateDiscount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getDiscount() {
        BigDecimal Discount = Env.ZERO;

        if( m_PriceList.intValue() != 0 ) {
            Discount = new BigDecimal(( m_PriceList.doubleValue() - m_PriceStd.doubleValue()) / m_PriceList.doubleValue() * 100.0 );
        }

        if( Discount.scale() > 2 ) {
            Discount = Discount.setScale( 2,BigDecimal.ROUND_HALF_UP );
        }

        return Discount;
    }    // getDiscount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_Product_ID() {
        return m_M_Product_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_PriceList_ID() {
        return m_M_PriceList_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_ID
     */

    public void setM_PriceList_ID( int M_PriceList_ID ) {
        m_M_PriceList_ID = M_PriceList_ID;
        m_calculated     = false;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_PriceList_Version_ID() {
        return m_M_PriceList_Version_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_Version_ID
     */

    public void setM_PriceList_Version_ID( int M_PriceList_Version_ID ) {
        m_M_PriceList_Version_ID = M_PriceList_Version_ID;
        m_calculated             = false;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getPriceDate() {
        return m_PriceDate;
    }

    /**
     * Descripción de Método
     *
     *
     * @param priceDate
     */

    public void setPriceDate( Timestamp priceDate ) {
        m_PriceDate  = priceDate;
        m_calculated = false;
    }

    /**
     * Descripción de Método
     *
     */

    private void setPrecision() {
        if( m_M_PriceList_ID != 0 ) {
            m_precision = MPriceList.getPricePrecision( Env.getCtx(),getM_PriceList_ID());
        }
    }    // setPrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrecision() {
        return m_precision;
    }    // getPrecision

    /**
     * Descripción de Método
     *
     *
     * @param bd
     *
     * @return
     */

    private BigDecimal round( BigDecimal bd ) {
        if( (m_precision >= 0    // -1 = no rounding
                ) && (bd.scale() > m_precision) ) {
            return bd.setScale( m_precision,BigDecimal.ROUND_HALF_UP );
        }

        return bd;
    }    // round

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_UOM_ID() {
        if( !m_calculated ) {
            calculatePrice();
        }

        return m_C_UOM_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getPriceList() {
        if( !m_calculated ) {
            calculatePrice();
        }

        return round( m_PriceList );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getPriceStd() {
        if( !m_calculated ) {
            calculatePrice();
        }

        return round( m_PriceStd );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getPriceLimit() {
        if( !m_calculated ) {
            calculatePrice();
        }

        return round( m_PriceLimit );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        if( !m_calculated ) {
            calculatePrice();
        }

        return m_C_Currency_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEnforcePriceLimit() {
        if( !m_calculated ) {
            calculatePrice();
        }

        return m_enforcePriceLimit;
    }    // isEnforcePriceLimit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDiscountSchema() {
        return m_discountSchema;
    }    // isDiscountSchema

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCalculated() {
        return m_calculated;
    }    // isCalculated

	public void setContext(String context) {
		this.context = context;
	}

	public String getContext() {
		return context;
	}
}    // MProductPrice



/*
 *  @(#)MProductPricing.java   02.07.07
 * 
 *  Fin del fichero MProductPricing.java
 *  
 *  Versión 2.2
 *
 */
