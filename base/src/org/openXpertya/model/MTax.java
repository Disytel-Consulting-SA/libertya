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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTax extends X_C_Tax {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MTax[] getAll( Properties ctx ) {
        int     AD_Client_ID = Env.getAD_Client_ID( ctx );
        Integer key          = new Integer( AD_Client_ID );
        MTax[]  retValue     = ( MTax[] )s_cacheAll.get( key );

        if( retValue != null ) {
            return retValue;
        }

        // Create it

        String sql = "SELECT * FROM C_Tax WHERE AD_Client_ID=?" + "ORDER BY C_Country_ID, C_Region_ID, To_Country_ID, To_Region_ID";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MTax tax = new MTax( ctx,rs,null );

                s_cache.put( new Integer( tax.getC_Tax_ID()),tax );
                list.add( tax );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getAll",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Create Array

        retValue = new MTax[ list.size()];
        list.toArray( retValue );

        //

        s_cacheAll.put( key,retValue );

        return retValue;
    }    // getAll

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Tax_ID
     * @param trxName
     *
     * @return
     */

    public static MTax get( Properties ctx,int C_Tax_ID,String trxName ) {
        Integer key      = new Integer( C_Tax_ID );
        MTax    retValue = ( MTax )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MTax( ctx,C_Tax_ID,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get
    
    /**
     * Get taxs of tax category
     * @param ctx
     * @param C_TaxCategory_ID
     * @param trxName
     * @return
     */
    public static List<MTax> getOfTaxCategory(Properties ctx,int C_TaxCategory_ID,String trxName){
    	//Script sql
    	String sql = "SELECT * FROM c_tax WHERE c_taxcategory_id = ? ORDER BY created";
    	List<MTax> list = new ArrayList<MTax>();
    	
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set c_taxcategory
			ps.setInt(1, C_TaxCategory_ID);
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MTax(ctx,rs,trxName));				
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

	/**
	 * Obtiene el impuesto que se encuentra exento de impuesto, si no existe
	 * ninguno verifica alguno que posea tasa 0. Si no existe alguno en ninguno
	 * de los casos, entonces devuelve null.
	 * 
	 * @param ctx
	 *            contexto
	 * @param trxName
	 *            nombre de transacción en curso
	 * @return impuesto exento, tasa 0 o null
	 */
    public static MTax getTaxExemptRate(Properties ctx, String trxName){
		String sql = "SELECT * " +
					 "FROM c_tax " +
					 "WHERE (istaxexempt = 'Y' OR rate = 0) AND (ad_client_id = ?) " +
					 "ORDER BY istaxexempt desc";
		PreparedStatement ps = null;
		ResultSet rs = null;
		MTax tax = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			if(rs.next()){
				tax = new MTax(ctx, rs, trxName);
			}
		} catch (Exception e) {
			s_log.saveError("", "Error finding tax exempt or tax rate equals zero");
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				s_log.saveError("", "Error finding tax exempt or tax rate equals zero");
			}
		}
		return tax;
    }
    

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_Tax",5 );

    /** Descripción de Campos */

    private static CCache s_cacheAll = new CCache( "C_Tax",5 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MTax.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Tax_ID
     * @param trxName
     */

    public MTax( Properties ctx,int C_Tax_ID,String trxName ) {
        super( ctx,C_Tax_ID,trxName );

        if( C_Tax_ID == 0 ) {

            // setC_Tax_ID (0);                PK

            setIsDefault( false );
            setIsDocumentLevel( true );
            setIsSummary( false );
            setIsTaxExempt( false );

            // setName (null);

            setRate( Env.ZERO );
            setRequiresTaxCertificate( false );

            // setC_TaxCategory_ID (0);        //      FK

            setSOPOType( SOPOTYPE_Both );
            setValidFrom( TimeUtil.getDay( 1990,1,1 ));
        }
    }    // MTax

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MTax( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MTax

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param Name
     * @param Rate
     * @param C_TaxCategory_ID
     * @param trxName
     */

    public MTax( Properties ctx,String Name,BigDecimal Rate,int C_TaxCategory_ID,String trxName ) {
        this( ctx,0,trxName );
        setName( Name );
        setRate( (Rate == null)
                 ?Env.ZERO
                 :Rate );
        setC_TaxCategory_ID( C_TaxCategory_ID );    // FK
    }                                               // MTax

    /** Descripción de Campos */

    private static BigDecimal ONEHUNDRED = new BigDecimal( 100 );

    /** Descripción de Campos */

    private MTax[] m_childTaxes = null;

    /** Descripción de Campos */

    private MTaxPostal[] m_postals = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MTax[] getChildTaxes( boolean requery ) {
        if( !isSummary()) {
            return null;
        }

        if( (m_childTaxes != null) &&!requery ) {
            return m_childTaxes;
        }

        //

        String            sql   = "SELECT * FROM C_Tax WHERE Parent_Tax_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Tax_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MTax( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getChildTaxes",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_childTaxes = new MTax[ list.size()];
        list.toArray( m_childTaxes );

        return m_childTaxes;
    }    // getChildTaxes

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MTaxPostal[] getPostals( boolean requery ) {
        if( (m_postals != null) &&!requery ) {
            return m_postals;
        }

        String sql = "SELECT * FROM C_TaxPostal WHERE C_Tax_ID=? ORDER BY Postal, Postal_To";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Tax_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {}

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPostals",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_postals = new MTaxPostal[ list.size()];
        list.toArray( m_postals );

        return m_postals;
    }    // getPostals

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPostal() {
        return getPostals( false ).length > 0;
    }    // isPostal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isZeroTax() {
        return isZeroTax(getRate());
    }    // isZeroTax

	/**
	 * Verifica si la tasa de impuesto parámetro es 0
	 * 
	 * @param rate
	 *            tasa de impuesto
	 * @return true si la tasa es 0, false caso contrario
	 */
    public static boolean isZeroTax(BigDecimal rate) {
        return Env.ZERO.compareTo(rate) == 0;
    }    // isZeroTax

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MTax[" );

        sb.append( getID()).append( "," ).append( getName()).append( ", SO/PO=" ).append( getSOPOType()).append( ",Rate=" ).append( getRate()).append( ",C_TaxCategory_ID=" ).append( getC_TaxCategory_ID()).append( ",Summary=" ).append( isSummary()).append( ",Parent=" ).append( getParent_Tax_ID()).append( ",Country=" ).append( getC_Country_ID()).append( "|" ).append( getTo_Country_ID()).append( ",Region=" ).append( getC_Region_ID()).append( "|" ).append( getTo_Region_ID()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param amount
     * @param taxIncluded
     * @param scale
     *
     * @return
     */

    public BigDecimal calculateTax( BigDecimal amount,boolean taxIncluded,int scale ) {
    	return calculateTax(amount, taxIncluded, getRate(), scale);
    }    // calculateTax

	/**
	 * Calcula el impuesto a partir del monto parámetro, obteniendo el importe
	 * base dependiendo si está incluído el impuesto en el precio o no
	 * 
	 * @param amount
	 *            monto
	 * @param taxIncluded
	 *            true si el impuesto está incluído en el precio, false caso
	 *            contrario
	 * @param rate
	 *            tasa de impuesto
	 * @param scale
	 *            escala del monto de impuesto a retornar
	 * @return monto de impuesto
	 */
    public static BigDecimal calculateTax( BigDecimal amount,boolean taxIncluded,BigDecimal rate, int scale ) {
        if( isZeroTax(rate)) {
            return Env.ZERO;
        }
        BigDecimal multiplier = rate.divide( ONEHUNDRED,10,BigDecimal.ROUND_HALF_UP );
        BigDecimal tax = null;

        if( !taxIncluded )    // $100 * 6 / 100 == $6 == $100 * 0.06
        {
            tax = amount.multiply( multiplier );
        } else                // $106 - ($106 / (100+6)/100) == $6 == $106 - ($106/1.06)
        {
            multiplier = multiplier.add( Env.ONE );
            BigDecimal base = amount.divide( multiplier,10,BigDecimal.ROUND_HALF_UP );
            tax = amount.subtract( base );
        }
        BigDecimal finalTax = tax.setScale( scale,BigDecimal.ROUND_HALF_UP );
        return finalTax;
    }    // calculateTax
    
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
        if( newRecord ) {
            insert_Accounting( "C_Tax_Acct","C_AcctSchema_Default",null );
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        return delete_Accounting( "C_Tax_Acct" );
    }    // beforeDelete
    
    public boolean isCategoriaManual(){
    	// devuelve true cuando el impuesto corresponde a una categoria manual
    	 boolean retValue = true;
    	 
    	X_C_TaxCategory category = new X_C_TaxCategory(getCtx(),getC_TaxCategory_ID(), null);
    	retValue = category.isManual();
    	return retValue;
    }

	@Override
	protected boolean beforeSave(boolean newRecord) {
		String taxType = getTaxType();
		boolean error = false;		
		
		// Si taxType es null se lanza un error.
		if(taxType == null) {
			log.saveError( "FillMandatory",Msg.translate( getCtx(),"TaxType" ));
			return false;
		}
		
		// Si la regla de aplicación es General, se borran los valores
		// de los campos de configuración de regla de aplicación.
		if(taxType.equals(TAXTYPE_General)) {
			setC_BP_Group_ID(0);
			setM_Product_Category_ID(0);
			setM_Product_ID(0);
		
		// Si es Por Categoria, se borra el parámetro de atículo. 
		} else if(taxType.equals(TAXTYPE_ByCategory)) {
			if(getM_Product_Category_ID() <= 0) {
				String errorDesc = Msg.parseTranslation(Env.getCtx(),"@MustIntroduceValueFor@ @M_Product_Category_ID@");
				log.saveError("InvalidAplicationRule",errorDesc);
				error = true;
			} else
				setM_Product_ID(0);

		// Si es Por Producto, se borra el parámetro de categoría.
		} else if(taxType.equals(TAXTYPE_ByProduct)) {
			if(getM_Product_ID() <= 0) {
				String errorDesc = Msg.parseTranslation(Env.getCtx(),"@MustIntroduceValueFor@ @M_Product_ID@");
				log.saveError("InvalidAplicationRule",errorDesc);
				error = true;
			} else
				setM_Product_Category_ID(0);
		}
		
		return !error;
	}
    
    
}    // MTax



/*
 *  @(#)MTax.java   02.07.07
 * 
 *  Fin del fichero MTax.java
 *  
 *  Versión 2.1
 *
 */
