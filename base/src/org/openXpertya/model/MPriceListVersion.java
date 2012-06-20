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

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPriceListVersion extends X_M_PriceList_Version {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_PriceList_Version_ID
     * @param trxName
     */

    public MPriceListVersion( Properties ctx,int M_PriceList_Version_ID,String trxName ) {
        super( ctx,M_PriceList_Version_ID,trxName );

        if( M_PriceList_Version_ID == 0 ) {

            // setName (null); // @#Date@
            // setM_PriceList_ID (0);
            // setValidFrom (TimeUtil.getDay(null));   // @#Date@
            // setM_DiscountSchema_ID (0);

        }
    }    // MPriceListVersion

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPriceListVersion( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPriceListVersion

    /**
     * Constructor de la clase ...
     *
     *
     * @param pl
     */

    public MPriceListVersion( MPriceList pl ) {
        this( pl.getCtx(),0,pl.get_TrxName());
        setClientOrg( pl );
        setM_PriceList_ID( pl.getM_PriceList_ID());
    }    // MPriceListVersion
    
    
    /**
     * Get price lists versions of client
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MPriceListVersion> getOfClient(Properties ctx,String trxName){
    	//script sql
    	String sql = "SELECT * FROM m_pricelist_version WHERE ad_client_id = ? ORDER BY created"; 
    		
    	List<MPriceListVersion> list = new ArrayList<MPriceListVersion>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MPriceListVersion(ctx,rs,trxName));				
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
    }//	getOfClient
    
    /**
     * Get price list versions of price list id 
     * @param ctx
     * @param PriceList_ID
     * @param trxName
     * @return
     */
    public static List<MPriceListVersion> getOfPriceList(Properties ctx,int PriceList_ID,String trxName){
    	//script sql
    	String sql = "SELECT * FROM m_pricelist_version WHERE m_pricelist_id = ? "; 
    		
    	List<MPriceListVersion> list = new ArrayList<MPriceListVersion>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set price list
			ps.setInt(1, PriceList_ID);
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MPriceListVersion(ctx,rs,trxName));				
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
    } //	getOfPriceList

    /**
     * Descripción de Método
     *
     */

    public void setName() {
        if( getValidFrom() == null ) {
            setValidFrom( TimeUtil.getDay( null ));
        }

        if( getName() == null ) {
            String name = DisplayType.getDateFormat( DisplayType.Date ).format( getValidFrom());

            setName( name );
        }
    }    // setName

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        setName();

        return true;
    }    // beforeSave
}    // MPriceListVersion



/*
 *  @(#)MPriceListVersion.java   02.07.07
 * 
 *  Fin del fichero MPriceListVersion.java
 *  
 *  Versión 2.2
 *
 */
