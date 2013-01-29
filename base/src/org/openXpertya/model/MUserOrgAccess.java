/*
 * @(#)MUserOrgAccess.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.Msg;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      User Org Access
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MUserOrgAccess.java,v 1.1 2005/04/22 05:49:09 jjanke Exp $
 */
public class MUserOrgAccess extends X_AD_User_OrgAccess {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MUserOrgAccess.class);

    /** Descripción de Campo */
    private String	m_clientName;

    /** Descripción de Campo */
    private String	m_orgName;

    /**
     *      Organization Constructor
     *      @param org org
     *      @param AD_User_ID role
     */
    public MUserOrgAccess(MOrg org, int AD_User_ID) {

        this(org.getCtx(), 0, org.get_TrxName());
        setClientOrg(org);
        setAD_User_ID(AD_User_ID);

    }		// MUserOrgAccess

    /**
     *      Persistency Constructor
     *      @param ctx context
     *      @param ignored ignored
     * @param trxName
     */
    public MUserOrgAccess(Properties ctx, int ignored, String trxName) {

        super(ctx, 0, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

        setIsReadOnly(false);

    }		// MUserOrgAccess

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MUserOrgAccess(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MUserOrgAccess

    /**
     *      User Constructor
     *
     * public MUserOrgAccess (MUser user, int AD_Org_ID)
     * {
     *       this (user.getCtx(), 0, user.get_TrxName());
     *       setClientOrg (user.getAD_Client_ID(), AD_Org_ID);
     *       setAD_User_ID (user.getAD_User_ID());
     * }       //      MUserOrgAccess
     *
     *
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MUserOrgAccess[");

        sb.append("AD_User_ID=").append(getAD_User_ID()).append(",AD_Client_ID=").append(getAD_Client_ID()).append(",AD_Org_ID=").append(getAD_Org_ID()).append(",RO=").append(isReadOnly());
        sb.append("]");

        return sb.toString();

    }		// toString

    /**
     *      Extended String Representation
     *
     * @param ctx
     *      @return extended info
     */
    public String toStringX(Properties ctx) {

        StringBuffer	sb	= new StringBuffer();

        sb.append(Msg.translate(ctx, "AD_Client_ID")).append("=").append(getClientName()).append(" - ").append(Msg.translate(ctx, "AD_Org_ID")).append("=").append(getOrgName());

        return sb.toString();

    }		// toStringX

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Organizational Info
     *      @param ctx context
     *      @param sql sql command
     *      @param id id
     *      @return array of User Org Access
     */
    private static MUserOrgAccess[] get(Properties ctx, String sql, int id) {

        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MUserOrgAccess(ctx, rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MUserOrgAccess[]	retValue	= new MUserOrgAccess[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// get

    /**
     *      Get Client Name
     *      @return name
     */
    public String getClientName() {

        if (m_clientName == null) {

            String	sql	= "SELECT c.Name, o.Name " + "FROM AD_Client c INNER JOIN AD_Org o ON (c.AD_Client_ID=o.AD_Client_ID) " + "WHERE o.AD_Org_ID=?";
            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql);
                pstmt.setInt(1, getAD_Org_ID());

                ResultSet	rs	= pstmt.executeQuery();

                if (rs.next()) {

                    m_clientName	= rs.getString(1);
                    m_orgName		= rs.getString(2);
                }

                rs.close();
                pstmt.close();
                pstmt	= null;

            } catch (Exception e) {
                log.log(Level.SEVERE, "getClientName", e);
            }

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

                pstmt	= null;

            } catch (Exception e) {
                pstmt	= null;
            }
        }

        return m_clientName;

    }		// getClientName

    /**
     *      Get Organizational Access of User
     *      @param ctx context
     *      @param AD_User_ID user
     *      @return array of User Org Access
     */
    public static MUserOrgAccess[] getOfUser(Properties ctx, int AD_User_ID) {
        return get(ctx, "SELECT * FROM AD_User_OrgAccess WHERE AD_User_ID=?", AD_User_ID);
    }		// getOfUser

    /**
     *      Get Client Name
     *      @return name
     */
    public String getOrgName() {

        if (m_orgName == null) {
            getClientName();
        }

        return m_orgName;

    }		// getOrgName
}	// MUserOrgAccess



/*
 * @(#)MUserOrgAccess.java   02.jul 2007
 * 
 *  Fin del fichero MUserOrgAccess.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
