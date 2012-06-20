/*
 * @(#)MRefList.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ValueNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Reference List Value
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MRefList.java,v 1.7 2005/05/29 05:58:18 jjanke Exp $
 */
public class MRefList extends X_AD_Ref_List {

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MRefList.class);

    /** Value Cache */
    private static CCache	s_cache	= new CCache("AD_Ref_List", 20);

    /**
     *      Persistency Constructor
     *      @param ctx context
     *      @param AD_Ref_List_ID id
     * @param trxName
     */
    public MRefList(Properties ctx, int AD_Ref_List_ID, String trxName) {

        super(ctx, AD_Ref_List_ID, trxName);

        if (AD_Ref_List_ID == 0) {

            // setAD_Reference_ID (0);
            // setAD_Ref_List_ID (0);
            setEntityType(ENTITYTYPE_UserMaintained);		// U

            // setName (null);
            // setValue (null);
        }

    }								// MRef_List

    /**
     *      Load Contructor
     *      @param ctx context
     *      @param rs result
     * @param trxName
     */
    public MRefList(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MRef_List

    /**
     *      String Representation
     *      @return Name
     */
    public String toString() {
        return getName();
    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Reference List
     *      @param ctx context
     *      @param AD_Reference_ID reference
     *      @param Value value
     * @param trxName
     *      @return List or null
     */
    public static MRefList get(Properties ctx, int AD_Reference_ID, String Value, String trxName) {

        MRefList	retValue	= null;
        String		sql		= "SELECT * FROM AD_Ref_List " + "WHERE AD_Reference_ID=? AND Value=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);
            pstmt.setInt(1, AD_Reference_ID);
            pstmt.setString(2, Value);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MRefList(ctx, rs, trxName);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            s_log.log(Level.SEVERE, sql, ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        return retValue;

    }		// get

    /**
     *      Get Reference List
     *      @param AD_Reference_ID reference
     *      @param optional if true add "",""
     *      @return List or null
     */
    public static ValueNamePair[] getList(int AD_Reference_ID, boolean optional, Properties ctx) {
    	boolean isBaseLanguage = ctx == null || Env.isBaseLanguage(ctx, "AD_Ref_List");
    	
    	String	sql = null;
    	String language = null;
    	
    	if (isBaseLanguage) {
		    sql	= "SELECT Value, Name " +
		    	  "FROM AD_Ref_List " + 
		     	  "WHERE AD_Reference_ID=? AND IsActive='Y' ORDER BY 1";
    	} else {
        	language = Env.getAD_Language(ctx);
    		sql = "SELECT l.Value, t.Name " +
        	      "FROM AD_Ref_List_Trl t " +
        	      "INNER JOIN AD_Ref_List l ON (t.AD_Ref_List_ID = l.AD_Ref_List_ID) " + 
        	      "WHERE l.AD_Reference_ID=? AND l.IsActive='Y' AND t.AD_Language=? ORDER BY 1";
    	}
        PreparedStatement	pstmt	= null;
        ArrayList		list	= new ArrayList();

        if (optional) {
            list.add(new ValueNamePair("", ""));
        }

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Reference_ID);
            if (!isBaseLanguage) {
            	pstmt.setString(2, language);
            }
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new ValueNamePair(rs.getString(1), rs.getString(2)));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getList " + sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        ValueNamePair[]	retValue	= new ValueNamePair[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getList
    
    public static ValueNamePair[] getList(int AD_Reference_ID, boolean optional) {
    	return getList(AD_Reference_ID, optional, null); 
    }

    /**
     *      Get Reference List Value Name (cached)
     *      @param ctx context
     *      @param AD_Reference_ID reference
     *      @param Value value
     *      @return List or null
     */
    public static String getListName(Properties ctx, int AD_Reference_ID, String Value) {

        String	AD_Language	= Env.getAD_Language(ctx);
        String	key		= AD_Language + "_" + AD_Reference_ID + "_" + Value;
        String	retValue	= (String) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        boolean	isBaseLanguage	= Env.isBaseLanguage(AD_Language, "AD_Ref_List");
        String	sql	= isBaseLanguage
                          ? "SELECT Name FROM AD_Ref_List " + "WHERE AD_Reference_ID=? AND Value=?"
                          : "SELECT t.Name FROM AD_Ref_List_Trl t" + " INNER JOIN AD_Ref_List r ON (r.AD_Ref_List_ID=t.AD_Ref_List_ID) " + "WHERE r.AD_Reference_ID=? AND r.Value=? AND t.AD_Language=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, null);
            pstmt.setInt(1, AD_Reference_ID);
            pstmt.setString(2, Value);

            if (!isBaseLanguage) {
                pstmt.setString(3, AD_Language);
            }

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= rs.getString(1);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            s_log.log(Level.SEVERE, "getListName - " + sql + " - " + key, ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        // Save into Cache
        if (retValue == null) {

            retValue	= "";
            s_log.warning("getListName - Not found " + key);
        }

        s_cache.put(key, retValue);

        //
        return retValue;

    }		// getListName
}	// MRef_List



/*
 * @(#)MRefList.java   02.jul 2007
 * 
 *  Fin del fichero MRefList.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
