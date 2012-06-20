/*
 * @(#)MWindowAccess.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 *
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MWindowAccess.java,v 1.4 2005/03/11 20:28:32 jjanke Exp $
 */
public class MWindowAccess extends X_AD_Window_Access {

    /**
     *      Parent Constructor
     *      @param parent parent
     *      @param AD_Role_ID role id
     */
    public MWindowAccess(M_Window parent, int AD_Role_ID) {

        super(parent.getCtx(), 0, parent.get_TrxName());
        setClientOrg(parent);
        setAD_Window_ID(parent.getAD_Window_ID());
        setAD_Role_ID(AD_Role_ID);

    }		// MWindowAccess

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param ignored -
     * @param trxName
     */
    public MWindowAccess(Properties ctx, int ignored, String trxName) {

        super(ctx, 0, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        } else {

            // setAD_Role_ID (0);
            // setAD_Window_ID (0);
            setIsReadWrite(true);
        }

    }		// MWindowAccess

    /**
     *      MWindowAccess
     *      @param ctx
     *      @param rs
     * @param trxName
     */
    public MWindowAccess(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MWindowAccess
    
    
    /**
     * Get windows access of role id
     * @param ctx
     * @param AD_Role_ID
     * @param trxName
     * @return
     */
    public static List<MWindowAccess> getOfRole(Properties ctx, int AD_Role_ID, String trxName){
    	//script sql
    	String sql = "SELECT * FROM ad_window_access WHERE ad_role_id = ? "; 
    		
    	List<MWindowAccess> list = new ArrayList<MWindowAccess>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_role
			ps.setInt(1, AD_Role_ID);
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MWindowAccess(ctx,rs,trxName));	
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
    
    
    public static MWindowAccess getOfRoleAndWindow(Properties ctx, int AD_Role_ID,int AD_Window_ID,String trxName){
    	//script sql
    	String sql = "SELECT * FROM ad_window_access WHERE (ad_role_id = ?) and (ad_window_id = ?)"; 
    		
    	MWindowAccess mwa = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_role
			ps.setInt(1, AD_Role_ID);
			// set ad_window
			ps.setInt(2, AD_Window_ID);
			rs = ps.executeQuery();
			
			if(rs.next()){
				mwa = new MWindowAccess(ctx,rs,trxName);	
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
		
		return mwa;
    }
    
}	// MWindowAccess






/*
 * @(#)MWindowAccess.java   02.jul 2007
 * 
 *  Fin del fichero MWindowAccess.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
