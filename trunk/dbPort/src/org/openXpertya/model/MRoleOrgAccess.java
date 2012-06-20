/*
 * @(#)MRoleOrgAccess.java   12.oct 2007  Versión 2.2
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
 *      Role Org Access Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MRoleOrgAccess.java,v 1.10 2005/04/22 05:49:11 jjanke Exp $
 */
public class MRoleOrgAccess extends X_AD_Role_OrgAccess {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MRoleOrgAccess.class);

    /** Descripción de Campo */
    private String	m_clientName;

    /** Descripción de Campo */
    private String	m_orgName;

    /**
     *      Organization Constructor
     *      @param org org
     *      @param AD_Role_ID role
     */
    public MRoleOrgAccess(MOrg org, int AD_Role_ID) {

        this(org.getCtx(), 0, org.get_TrxName());
        setClientOrg(org);
        setAD_Role_ID(AD_Role_ID);

    }		// MRoleOrgAccess

    /**
     *      Role Constructor
     *      @param role role
     *      @param AD_Org_ID org
     */
    public MRoleOrgAccess(MRole role, int AD_Org_ID) {

        this(role.getCtx(), 0, role.get_TrxName());
        setClientOrg(role.getAD_Client_ID(), AD_Org_ID);
        setAD_Role_ID(role.getAD_Role_ID());

    }		// MRoleOrgAccess

    /**
     *      Persistency Constructor
     *      @param ctx context
     *      @param ignored ignored
     * @param trxName
     */
    public MRoleOrgAccess(Properties ctx, int ignored, String trxName) {

        super(ctx, 0, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

        setIsReadOnly(false);

    }		// MRoleOrgAccess

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MRoleOrgAccess(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MRoleOrgAccess

    /**
     *      Create Organizational Access for all Automatic Roles
     *      @param org org
     *      @return true if created
     */
    public static boolean createForOrg(MOrg org) {

        int	counter	= 0;
        MRole[]	roles	= MRole.getOfClient(org.getCtx());

        for (int i = 0; i < roles.length; i++) {

            if (!roles[i].isManual()) {

                MRoleOrgAccess	orgAccess	= new MRoleOrgAccess(org, roles[i].getAD_Role_ID());

                if (orgAccess.save()) {
                    counter++;
                }
            }
        }

        s_log.info(org + " - created #" + counter);

        return counter != 0;

    }		// createForOrg

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MRoleOrgAccess[");

        sb.append("AD_Role_ID=").append(getAD_Role_ID()).append(",AD_Client_ID=").append(getAD_Client_ID()).append(",AD_Org_ID=").append(getAD_Org_ID()).append(",RO=").append(isReadOnly());
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
     *      @return array of Role Org Access
     */
    private static MRoleOrgAccess[] get(Properties ctx, String sql, int id) {

        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MRoleOrgAccess(ctx, rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "get", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MRoleOrgAccess[]	retValue	= new MRoleOrgAccess[list.size()];

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
     *      Get Organizational Access of Client
     *      @param ctx context
     *      @param AD_Client_ID client
     *      @return array of Role Org Access
     */
    public static MRoleOrgAccess[] getOfClient(Properties ctx, int AD_Client_ID) {
        return get(ctx, "SELECT * FROM AD_Role_OrgAccess WHERE AD_Client_ID=?", AD_Client_ID);
    }		// getOfClient

    /**
     *      Get Organizational Access of Org
     *      @param ctx context
     *      @param AD_Org_ID role
     *      @return array of Role Org Access
     */
    public static MRoleOrgAccess[] getOfOrg(Properties ctx, int AD_Org_ID) {
        return get(ctx, "SELECT * FROM AD_Role_OrgAccess WHERE AD_Org_ID=?", AD_Org_ID);
    }		// getOfOrg

    /**
     *      Get Organizational Access of Role
     *      @param ctx context
     *      @param AD_Role_ID role
     *      @return array of Role Org Access
     */
    public static MRoleOrgAccess[] getOfRole(Properties ctx, int AD_Role_ID) {
        return get(ctx, "SELECT * FROM AD_Role_OrgAccess WHERE AD_Role_ID=?", AD_Role_ID);
    }		// getOfRole

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
}	// MRoleOrgAccess



/*
 * @(#)MRoleOrgAccess.java   02.jul 2007
 * 
 *  Fin del fichero MRoleOrgAccess.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
