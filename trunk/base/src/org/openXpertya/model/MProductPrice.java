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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProductPrice extends X_M_ProductPrice {

	/** Log estático */
	private static CLogger s_log = CLogger.getCLogger( MProductPrice.class );

	/**
	 * Obtener un precio de producto a partir del producto y de la versión de la
	 * tarifa
	 * 
	 * @param ctx
	 *            contexto
	 * @param priceListVersionID
	 *            id de la versión de tarifa
	 * @param productID
	 *            id del producto
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return precio de producto o null si no existe
	 */
	public static MProductPrice get(Properties ctx, int priceListVersionID, int productID, String trxName){
		String sql = "SELECT * FROM " + Table_Name
				+ " WHERE m_pricelist_version_id = ? and m_product_id = ?";
		MProductPrice price = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, priceListVersionID);
			ps.setInt(2, productID);
			rs = ps.executeQuery();
			if(rs.next()){
				price = new MProductPrice(ctx, rs, trxName);
			}
		} catch (Exception e) {
			s_log.severe("Error finding product price, method get. "+e.getMessage());
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				s_log.severe("Error finding product price, method get. "+e2.getMessage());
			}
		}
		return price;
	}
	
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MProductPrice( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }

        setPriceLimit( Env.ZERO );
        setPriceList( Env.ZERO );
        setPriceStd( Env.ZERO );
    }    // MProductPrice

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductPrice( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProductPrice

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_PriceList_Version_ID
     * @param M_Product_ID
     * @param trxName
     */

    public MProductPrice( Properties ctx,int M_PriceList_Version_ID,int M_Product_ID,String trxName ) {
        this( ctx,0,trxName );
        setM_PriceList_Version_ID( M_PriceList_Version_ID );    // FK
        setM_Product_ID( M_Product_ID );                        // FK
    }                                                           // MProductPrice

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_PriceList_Version_ID
     * @param M_Product_ID
     * @param PriceList
     * @param PriceStd
     * @param PriceLimit
     * @param trxName
     */

    public MProductPrice( Properties ctx,int M_PriceList_Version_ID,int M_Product_ID,BigDecimal PriceList,BigDecimal PriceStd,BigDecimal PriceLimit,String trxName ) {
        this( ctx,M_PriceList_Version_ID,M_Product_ID,trxName );
        setPrices( PriceList,PriceStd,PriceLimit );
    }    // MProductPrice

    /**
     * Constructor de la clase ...
     *
     *
     * @param plv
     * @param M_Product_ID
     * @param PriceList
     * @param PriceStd
     * @param PriceLimit
     */

    public MProductPrice( MPriceListVersion plv,int M_Product_ID,BigDecimal PriceList,BigDecimal PriceStd,BigDecimal PriceLimit ) {
        this( plv.getCtx(),0,plv.get_TrxName());
        setClientOrg( plv );
        setM_PriceList_Version_ID( plv.getM_PriceList_Version_ID());
        setM_Product_ID( M_Product_ID );
        setPrices( PriceList,PriceStd,PriceLimit );
    }    // MProductPrice

    /**
     * Descripción de Método
     *
     *
     * @param PriceList
     * @param PriceStd
     * @param PriceLimit
     */

    public void setPrices( BigDecimal PriceList,BigDecimal PriceStd,BigDecimal PriceLimit ) {
        setPriceLimit( PriceLimit );
        setPriceList( PriceList );
        setPriceStd( PriceStd );
    }    // setPrice
    
    public void changeOrg(int AD_Org_ID) {
    	set_ValueNoCheck("AD_Org_ID", AD_Org_ID);
    }
}    // MProductPrice



/*
 *  @(#)MProductPrice.java   02.07.07
 * 
 *  Fin del fichero MProductPrice.java
 *  
 *  Versión 2.2
 *
 */
