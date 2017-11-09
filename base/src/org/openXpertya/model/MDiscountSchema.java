/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.0, 22.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDiscountSchema extends X_M_DiscountSchema {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_DiscountSchema_ID
     *
     * @return
     */

    public static MDiscountSchema get( Properties ctx,int M_DiscountSchema_ID ) {
        Integer         key      = new Integer( M_DiscountSchema_ID );
        MDiscountSchema retValue = ( MDiscountSchema )s_cache.get( key );
        
        if( retValue != null ) {
            return retValue;
        }

        retValue = new MDiscountSchema( ctx,M_DiscountSchema_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "M_DiscountSchema",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_DiscountSchema_ID
     * @param trxName
     */

    public MDiscountSchema( Properties ctx,int M_DiscountSchema_ID,String trxName ) {
        super( ctx,M_DiscountSchema_ID,trxName );

        if( M_DiscountSchema_ID == 0 ) {

            // setName();
            // setDiscountType (null);

            setIsBPartnerFlatDiscount( false );
            setIsQuantityBased( true );    // Y
            setCumulativeLevel( CUMULATIVELEVEL_Document );
            setFlatDiscount( Env.ZERO );

            setIsGeneralScope(true);
            setIsBPartnerScope(false);
            // setValidFrom (new Timestamp(System.currentTimeMillis()));
            setDiscountContextType(DISCOUNTCONTEXTTYPE_Commercial);

        }
    }                                      // MDiscountSchema

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDiscountSchema( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDiscountSchema
    
    /**
     * Get discount schemas of client
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MDiscountSchema> getOfClient(Properties ctx,String trxName){
    	//script sql
    	String sql = "SELECT * FROM m_discountschema WHERE ad_client_id = ? "; 
    		
    	List<MDiscountSchema> list = new ArrayList<MDiscountSchema>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MDiscountSchema(ctx,rs,trxName));	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
    }

    /** Descripción de Campos */

    private MDiscountSchemaBreak[] m_breaks = null;

    /** Descripción de Campos */

    private MDiscountSchemaLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MDiscountSchemaBreak[] getBreaks( boolean reload ) {
        if( (m_breaks != null) &&!reload ) {
            return m_breaks;
        }

        String sql = "SELECT * FROM M_DiscountSchemaBreak WHERE M_DiscountSchema_ID=? AND isactive = 'Y' ORDER BY SeqNo";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_DiscountSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MDiscountSchemaBreak( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_breaks = new MDiscountSchemaBreak[ list.size()];
        list.toArray( m_breaks );

        return m_breaks;
    }    // getBreaks

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MDiscountSchemaLine[] getLines( boolean reload ) {
        if( (m_lines != null) &&!reload ) {
            return m_lines;
        }

        String sql = "SELECT * FROM M_DiscountSchemaLine WHERE M_DiscountSchema_ID=? ORDER BY SeqNo";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_DiscountSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MDiscountSchemaLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_lines = new MDiscountSchemaLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getBreaks

    /**
     * Descripción de Método
     *
     *
     * @param Qty
     * @param Price
     * @param M_Product_ID
     * @param M_Product_Category_ID
     * @param BPartnerFlatDiscount
     *
     * @return
     */

    public BigDecimal calculatePrice( BigDecimal Qty,BigDecimal Price,int M_Product_ID,int M_Product_Category_ID,BigDecimal BPartnerFlatDiscount, Date date) {
        log.fine("Llegamos en calculatePrice de MDiscountSchema");
    	log.fine( "Price=" + Price + ",Qty=" + Qty );

        if( (Price == null) || (Env.ZERO.compareTo( Price ) == 0) ) {
            return Price;
        }

        //

        BigDecimal discount = calculateDiscount( Qty,Price,M_Product_ID,M_Product_Category_ID,BPartnerFlatDiscount, date);

        // nothing to calculate

        if( (discount == null) || (Env.ZERO.compareTo( discount ) == 0) ) {
            return Price;
        }

        //

        BigDecimal onehundred = new BigDecimal( 100 );
        BigDecimal multiplier = ( onehundred ).subtract( discount );

        multiplier = multiplier.divide( onehundred,5,BigDecimal.ROUND_HALF_UP );

        BigDecimal newPrice = Price.multiply( multiplier );

        log.fine( "=>" + newPrice );

        return newPrice;
    }    // calculatePrice

    /**
     * Descripción de Método
     *
     *
     * @param Qty
     * @param Price
     * @param M_Product_ID
     * @param M_Product_Category_ID
     * @param BPartnerFlatDiscount
     *
     * @return
     */

    public BigDecimal calculateDiscount( BigDecimal Qty,BigDecimal Price,int M_Product_ID,int M_Product_Category_ID,BigDecimal BPartnerFlatDiscount, Date date) {
    	log.fine("Llegamos en calculateDiscount de MDiscountSchema, BPartnerFlatDiscount="+BPartnerFlatDiscount);
    	if( BPartnerFlatDiscount == null ) {
            BPartnerFlatDiscount = Env.ZERO;
        }

        // No se calcula descuento si el esquema mismo no es válido a la fecha parámetro
    	if(!isValid(date)){
    		log.info("Invalid Discount Schema "+getName());
    		return Env.ZERO;
    	}

        if( DISCOUNTTYPE_FlatPercent.equals( getDiscountType())) {
        	
            if( isBPartnerFlatDiscount()) {
            	log.fine("sale por el primer return");
                return BPartnerFlatDiscount;
            }
            log.fine("sale por el segundo return");
            return getFlatDiscount();
        }

        // Not supported

        if( DISCOUNTTYPE_Formula.equals( getDiscountType()) || DISCOUNTTYPE_Pricelist.equals( getDiscountType())) {
            log.warning( "Not supported (yet) DiscountType=" + getDiscountType());
            log.fine("Sale por el tercer return");
            return Env.ZERO;
        }

        // Price Breaks

        getBreaks( false );

        boolean    found = false;
        BigDecimal Amt   = Price.multiply( Qty );

        if( isQuantityBased()) {
            log.finer( "Qty=" + Qty + ",M_Product_ID=" + M_Product_ID + ",M_Product_Category_ID=" + M_Product_Category_ID );
        } else {
            log.finer( "Amt=" + Amt + ",M_Product_ID=" + M_Product_ID + ",M_Product_Category_ID=" + M_Product_Category_ID );
        }

        for( int i = 0;i < m_breaks.length;i++ ) {
            MDiscountSchemaBreak br = m_breaks[ i ];
            
            if( !br.isActive()) {
                continue;
            }

            if( isQuantityBased()) {
                if( !br.applies( Qty,M_Product_ID,M_Product_Category_ID, date)) {
                    log.finer( "Va a ser aqui1,No: " + br );

                    continue;
                }

                log.finer( "Yes: " + br );
            } else {
                if( !br.applies( Amt,M_Product_ID, M_Product_Category_ID, date)) {
                    log.finer( "Va a ser aqui2,No: " + br );

                    continue;
                }

                log.finer( "Yes: " + br );
            }

            // Line applies

            BigDecimal discount = null;

            if( br.isBPartnerFlatDiscount()) {
                discount = BPartnerFlatDiscount;
            } else {
                discount = br.getBreakDiscount();
            }

            log.fine( "Discount=>" + discount );

            return discount;
        }    // for all breaks

        return Env.ZERO;
    }    // calculateDiscount

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getValidFrom() == null ) {
            setValidFrom( TimeUtil.getDay( null ));
        }
        
		// La fecha final de validez no puede ser anterior a la fecha
		// inicial.
		if (getValidTo() != null && getValidTo().compareTo(getValidFrom()) < 0) {
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidDateRange"));
			return false;
		}
        
        // Si no tiene ambito por defecto es General
        if (!isGeneralScope() && !isBPartnerScope()) {
        	setIsGeneralScope(true);
        }

        // Si tiene erróneamente ámbito General y EC tiene precedencia
        // el ámbito EC, con lo cual se quita el General
        if (isGeneralScope() && isBPartnerScope()) {
        	setIsGeneralScope(false);
        }
        
        // Nivel de acumulación por defecto
        if (getCumulativeLevel() == null) {
        	setCumulativeLevel(CUMULATIVELEVEL_Document);
        }
        
		// Si es tipo de contexto Financiero, entonces se debe setear para
		// cantidades en false
        if(getDiscountContextType().equals(DISCOUNTCONTEXTTYPE_Financial)){
        	setCumulativeLevel(MDiscountSchema.CUMULATIVELEVEL_Document);
        	setIsQuantityBased(false);
        }
        
        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int reSeq() {
        int count = 0;

        // Lines

        MDiscountSchemaLine[] lines = getLines( true );

        for( int i = 0;i < lines.length;i++ ) {
            int line = ( i + 1 ) * 10;

            if( line != lines[ i ].getSeqNo()) {
                lines[ i ].setSeqNo( line );

                if( lines[ i ].save()) {
                    count++;
                }
            }
        }

        m_lines = null;

        // Breaks

        MDiscountSchemaBreak[] breaks = getBreaks( true );

        for( int i = 0;i < breaks.length;i++ ) {
            int line = ( i + 1 ) * 10;

            if( line != breaks[ i ].getSeqNo()) {
                breaks[ i ].setSeqNo( line );

                if( breaks[ i ].save()) {
                    count++;
                }
            }
        }

        m_breaks = null;

        return count;
    }    // reSeq
    
	/**
	 * Factory Method para la instanciación de un Corte a partir del ResultSet
	 * correspondiente, posicionado en el registro a cargar.
	 * 
	 * Este método puede ser redefinido por las subclases para instanciar Cortes
	 * con subclases de {@link MDiscountSchemaBreak}.
	 * 
	 * @param rs
	 *            ResultSet del corte
	 * @return {@link MDiscountSchemaBreak}
	 */
    protected MDiscountSchemaBreak createBreak(ResultSet rs) {
    	return new MDiscountSchemaBreak(getCtx(), rs, get_TrxName());
    }
    
	/**
	 * Indica si el esquema es válido para una determinada fecha
	 * 
	 * @param inDate
	 *            Fecha de consulta. NO puede ser <code>null</code>
	 * @return <code>true</code> si es válido, <code>false</code> si no.
	 * @throws IllegalArgumentException
	 *             si <code>inDate</code> es <code>null</code>
	 */
    
    public boolean isValid(Timestamp inDate) {
    	if (inDate == null) {
    		inDate = Env.getDate();
    	}
    	return isActive() 
    				&& (getValidFrom().compareTo(inDate) <= 0)
					&& (getValidTo() == null
									|| inDate.compareTo(getValidTo()) <= 0 
									|| TimeUtil.isSameDay(getValidTo(), inDate));
    }
    
    public boolean isValid(Date inDate) {
    	Timestamp forDate;
    	if (inDate == null) {
    		forDate = Env.getDate();
    	}
    	else{
    		forDate = new Timestamp(inDate.getTime());
    	}
    	return isValid(forDate);
    }
    
    /**
	 * Indica si el esquema es válido para la fecha actual
	 * 
	 * @return <code>true</code> si es válido, <code>false</code> si no.
	 */
    public boolean isValid() {
    	return isValid(Env.getDate());
    }

	/**
	 * @return Indica si este Esquema de Descuentos es aplicable a nivel de
	 *         Documento
	 */
    public boolean isDocumentLevel() {
    	return CUMULATIVELEVEL_Document.equals(getCumulativeLevel());
    }

	/**
	 * @return Indica si este Esquema de Descuentos es aplicable a nivel de
	 *         Línea de Documento
	 */
    public boolean isLineLevel() {
    	return CUMULATIVELEVEL_Line.equals(getCumulativeLevel());
    }
    
	@Override
	public int hashCode() {
		return getM_DiscountSchema_ID();
	}

	@Override
	public void setDiscountApplication(String DiscountApplication) {
		if (DiscountApplication == null) {
			set_ValueNoCheck("DiscountApplication", DiscountApplication);
		} else {
			super.setDiscountApplication(DiscountApplication);
		}
	}
	
	@Override
	public String toString(){
		return getName();
	}
	
}    // MDiscountSchema



/*
 *  @(#)MDiscountSchema.java   22.03.06
 * 
 *  Fin del fichero MDiscountSchema.java
 *  
 *  Versión 2.0
 *
 */
