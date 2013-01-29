/*
 * @(#)MUserRoles.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      User Roles Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MUserRoles.java,v 1.6 2005/03/11 20:28:37 jjanke Exp $
 */
public class MUserRoles extends X_AD_User_Roles {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MUserRoles.class);

    /**
     *      Persistence Constructor
     *      @param ctx context
     *      @param ignored invalid
     * @param trxName
     */
    public MUserRoles(Properties ctx, int ignored, String trxName) {

        super(ctx, ignored, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

    }		// MUserRoles

    /**
     *      Load constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MUserRoles(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MUserRoles

    /**
     *      Full Constructor
     *      @param ctx context
     *      @param AD_User_ID user
     *      @param AD_Role_ID role
     * @param trxName
     */
    public MUserRoles(Properties ctx, int AD_User_ID, int AD_Role_ID, String trxName) {

        this(ctx, 0, trxName);
        setAD_User_ID(AD_User_ID);
        setAD_Role_ID(AD_Role_ID);

    }		// MUserRoles

    //~--- get methods --------------------------------------------------------

    /**
     *      Get User Roles Of Role
     *      @param ctx context
     *      @param AD_Role_ID role
     *      @return array of user roles
     */
    public static MUserRoles[] getOfRole(Properties ctx, int AD_Role_ID) {

        String			sql	= "SELECT * FROM AD_User_Roles WHERE AD_Role_ID=?";
        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Role_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MUserRoles(ctx, rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getOfRole", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MUserRoles[]	retValue	= new MUserRoles[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getOfRole

    /**
     *      Get User Roles Of User
     *      @param ctx context
     *      @param AD_User_ID role
     *      @return array of user roles
     */
    public static MUserRoles[] getOfUser(Properties ctx, int AD_User_ID) {

        String			sql	= "SELECT * FROM AD_User_Roles WHERE AD_User_ID=?";
        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_User_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MUserRoles(ctx, rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getOfUser", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MUserRoles[]	retValue	= new MUserRoles[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getOfUser
    
    
    public static MUserRoles[] getOfUserAndClient(Properties ctx, int AD_User_ID, int AD_Client_ID, String trxName){
    	
    	 String			sql	= "SELECT * FROM AD_User_Roles WHERE AD_Client_ID=? AND AD_User_ID=?";
         ArrayList		list	= new ArrayList();
         PreparedStatement	pstmt	= null;

         try {

             pstmt	= DB.prepareStatement(sql,trxName);
             pstmt.setInt(1, AD_Client_ID);
             pstmt.setInt(2, AD_User_ID);

             ResultSet	rs	= pstmt.executeQuery();

             while (rs.next()) {
                 list.add(new MUserRoles(ctx, rs, trxName));
             }

             rs.close();
             pstmt.close();
             pstmt	= null;

         } catch (Exception e) {
             s_log.log(Level.SEVERE, "getOfUser", e);
         }

         try {

             if (pstmt != null) {
                 pstmt.close();
             }

             pstmt	= null;

         } catch (Exception e) {
             pstmt	= null;
         }

         MUserRoles[]	retValue	= new MUserRoles[list.size()];

         list.toArray(retValue);

         return retValue;

    }
}	// MUserRoles



/*
 * @(#)MUserRoles.java   02.jul 2007
 * 
 *  Fin del fichero MUserRoles.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
