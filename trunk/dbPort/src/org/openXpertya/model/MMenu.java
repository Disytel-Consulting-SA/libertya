/*
 * @(#)MMenu.java   12.oct 2007  Versión 2.2
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
 *      Menu Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MMenu.java,v 1.5 2005/05/14 05:32:16 jjanke Exp $
 */
public class MMenu extends X_AD_Menu {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MMenu.class);

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Menu_ID id
     * @param trxName
     */
    public MMenu(Properties ctx, int AD_Menu_ID, String trxName) {

        super(ctx, AD_Menu_ID, trxName);

        if (AD_Menu_ID == 0) {

            setEntityType(ENTITYTYPE_UserMaintained);		// U
            setIsReadOnly(false);				// N
            setIsSOTrx(false);
            setIsSummary(false);

            // setName (null);
        }

    }								// MMenu

    /**
     *      MMenu
     *      @param ctx
     *      @param rs
     * @param trxName
     */
    public MMenu(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MMenu

    /**
     *      After Delete
     *      @param success
     *      @return deleted
     */
    protected boolean afterDelete(boolean success) {

        if (success) {
            delete_Tree(MTree_Base.TREETYPE_Menu);
        }

        return true;

    }		// afterDelete

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (newRecord) {
            insert_Tree(MTree_Base.TREETYPE_Menu);
        }

        return success;

    }		// afterSave

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true
     */
    protected boolean beforeSave(boolean newRecord) {

        // Reset info
        if (isSummary() && (getAction() != null)) {
            setAction(null);
        }

        String	action	= getAction();

        if (action == null) {
            action	= "";
        }

        // Clean up references
        if ((getAD_Window_ID() != 0) &&!action.equals(ACTION_Window)) {
            setAD_Window_ID(0);
        }

        if ((getAD_Form_ID() != 0) &&!action.equals(ACTION_Form)) {
            setAD_Form_ID(0);
        }

        if ((getAD_Workflow_ID() != 0) &&!action.equals(ACTION_WorkFlow)) {
            setAD_Workflow_ID(0);
        }

        if ((getAD_Workbench_ID() != 0) &&!action.equals(ACTION_Workbench)) {
            setAD_Workbench_ID(0);
        }

        if ((getAD_Task_ID() != 0) &&!action.equals(ACTION_Task)) {
            setAD_Task_ID(0);
        }

        if ((getAD_Process_ID() != 0) &&!(action.equals(ACTION_Process) || action.equals(ACTION_Report))) {
            setAD_Process_ID(0);
        }

        return true;
    }		// beforeSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get menues with where clause
     *      @param ctx context
     *      @param whereClause where clause w/o the actual WHERE
     *      @return MMenu
     */
    public static MMenu[] get(Properties ctx, String whereClause) {

        String	sql	= "SELECT * FROM AD_Menu";

        if ((whereClause != null) && (whereClause.length() > 0)) {
            sql	+= " WHERE " + whereClause;
        }

        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MMenu(ctx, rs, null));
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

        MMenu[]	retValue	= new MMenu[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// get
}	// MMenu



/*
 * @(#)MMenu.java   02.jul 2007
 * 
 *  Fin del fichero MMenu.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
