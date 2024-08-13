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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTaxCategory extends X_C_TaxCategory {

	/** Caché para evitar accesos a la BD */
	private static CCache s_cache = new CCache( "C_TaxCategory",5 );
	
	/**
	 * Obtener la categoría de impuesto a partir del ID parámetro
	 * @param ctx
	 * @param C_Tax_ID
	 * @param trxName
	 * @return categoría de impuesto del ID parámetro
	 */
	public static MTaxCategory get( Properties ctx,int C_TaxCategory_ID,String trxName ) {
		Integer key = new Integer(C_TaxCategory_ID);
		MTaxCategory retValue = (MTaxCategory)s_cache.get(key);

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MTaxCategory( ctx,key,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put(key,retValue);
        }

        return retValue;
    } 
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_TaxCategory_ID
     * @param trxName
     */

    public MTaxCategory( Properties ctx,int C_TaxCategory_ID,String trxName ) {
        super( ctx,C_TaxCategory_ID,trxName );

        if( C_TaxCategory_ID == 0 ) {

            // setName (null);

            setIsDefault( false );
        }
    }    // MTaxCategory

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MTaxCategory( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MTaxCategory
    
    
    /**
     * Get tax category of client
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MTaxCategory> getOfClient(Properties ctx,String trxName){
    	//script sql
    	String sql = "SELECT * FROM c_taxcategory WHERE ad_client_id = ?"; 
    		
    	List<MTaxCategory> list = new ArrayList<MTaxCategory>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MTaxCategory(ctx,rs,trxName));				
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
    }	// getOfClient
    
}    // MTaxCategory



/*
 *  @(#)MTaxCategory.java   02.07.07
 * 
 *  Fin del fichero MTaxCategory.java
 *  
 *  Versión 2.2
 *
 */
