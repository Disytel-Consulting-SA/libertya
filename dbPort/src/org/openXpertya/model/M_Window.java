/*
 * @(#)M_Window.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Dimension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Window Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: M_Window.java,v 1.8 2005/04/03 06:47:38 jjanke Exp $
 */
public class M_Window extends X_AD_Window {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(M_Window.class);

    /** The Lines */
    private M_Tab[]	m_tabs	= null;

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Window_ID
     * @param trxName
     */
    public M_Window(Properties ctx, int AD_Window_ID, String trxName) {

        super(ctx, AD_Window_ID, trxName);

        if (AD_Window_ID == 0) {

            setWindowType(WINDOWTYPE_Maintain);			// M
            setEntityType(ENTITYTYPE_UserMaintained);		// U
            setIsBetaFunctionality(false);
            setIsDefault(false);
            setIsSOTrx(true);					// Y
        }

    }								// M_Window

    /**
     *      Koad Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public M_Window(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// M_Window

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (newRecord) {

            int			AD_Role_ID	= Env.getAD_Role_ID(getCtx());
            MWindowAccess	wa		= new MWindowAccess(this, AD_Role_ID);

            wa.save();
        }

        // Menu/Workflow
        else if (is_ValueChanged("IsActive") || is_ValueChanged("Name") || is_ValueChanged("Description") || is_ValueChanged("Help")) {

            MMenu[]	menues	= MMenu.get(getCtx(), "AD_Window_ID=" + getAD_Window_ID());

            for (int i = 0; i < menues.length; i++) {

                menues[i].setName(getName());
                menues[i].setDescription(getDescription());
                menues[i].setIsActive(isActive());
                menues[i].save();
            }

            //
            X_AD_WF_Node[]	nodes	= getWFNodes(getCtx(), "AD_Window_ID=" + getAD_Window_ID());

            for (int i = 0; i < nodes.length; i++) {

                boolean	changed	= false;

                if (nodes[i].isActive() != isActive()) {

                    nodes[i].setIsActive(isActive());
                    changed	= true;
                }

                if (nodes[i].isCentrallyMaintained()) {

                    nodes[i].setName(getName());
                    nodes[i].setDescription(getDescription());
                    nodes[i].setHelp(getHelp());
                    changed	= true;
                }

                if (changed) {
                    nodes[i].save();
                }
            }
        }

        return success;

    }		// afterSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Fields
     *      @param reload reload data
     * @param trxName
     *      @return array of lines
     */
    public M_Tab[] getTabs(boolean reload, String trxName) {

        if ((m_tabs != null) &&!reload) {
            return m_tabs;
        }

        String	sql	= "SELECT * FROM AD_Tab WHERE AD_Window_ID=? ORDER BY SeqNo";
        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);
            pstmt.setInt(1, getAD_Window_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new M_Tab(getCtx(), rs, trxName));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getTabs", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        //
        m_tabs	= new M_Tab[list.size()];
        list.toArray(m_tabs);

        return m_tabs;

    }		// getFields

    /**
     *      Get workflow nodes with where clause.
     *      Is here as MWFNode is in base
     *      @param ctx context
     *      @param whereClause where clause w/o the actual WHERE
     *      @return nodes
     */
    public static X_AD_WF_Node[] getWFNodes(Properties ctx, String whereClause) {

        String	sql	= "SELECT * FROM AD_WF_Node";

        if ((whereClause != null) && (whereClause.length() > 0)) {
            sql	+= " WHERE " + whereClause;
        }

        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new X_AD_WF_Node(ctx, rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getWFNode - " + sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        X_AD_WF_Node[]	retValue	= new X_AD_WF_Node[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getWFNode

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Window Size
     *      @param size size
     */
    public void setWindowSize(Dimension size) {

        if (size != null) {

            setWinWidth(size.width);
            setWinHeight(size.height);
        }

    }		// setWindowSize
}	// M_Window



/*
 * @(#)M_Window.java   02.jul 2007
 * 
 *  Fin del fichero M_Window.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
