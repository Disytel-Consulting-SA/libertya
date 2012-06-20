/*
 * @(#)MMPCOrderNode.java   13.jun 2007  Versión 2.2
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



//package org.openXpertya.mfg.model;
package openXpertya.model;

import java.util.*;

import java.sql.*;

import java.awt.Point;

import java.util.logging.*;

import org.openXpertya.model.*;
import org.openXpertya.util.*;
import org.openXpertya.wf.*;

/**
 *      Workflow Node
 *
 *      @author         Jorg Janke
 *      @version        $Id: MMPCOrderNode.java,v 1.15 2004/05/15 06:29:16 jjanke Exp $
 */
public class MMPCOrderNode extends X_MPC_Order_Node {

    /**
     *      Get WF Node from Cache
     *      @param ctx context
     *      @param MPC_Order_Node_ID id
     * @param trxName
     *      @return MMPCOrderNode
     */
    public static MMPCOrderNode get(Properties ctx, int MPC_Order_Node_ID, String trxName) {

        Integer		key		= new Integer(MPC_Order_Node_ID);
        MMPCOrderNode	retValue	= (MMPCOrderNode) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MMPCOrderNode(ctx, MPC_Order_Node_ID, "MPC_Order_Node");

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    /** Cache */
    private static CCache	s_cache	= new CCache("MPC_Order_Node", 50);

    /**
     *      Standard Constructor - save to cache
     *      @param ctx context
     *      @param MPC_Order_Node_ID id
     * @param trxName
     */
    public MMPCOrderNode(Properties ctx, int MPC_Order_Node_ID, String trxName) {

        super(ctx, MPC_Order_Node_ID, trxName);

        if (MPC_Order_Node_ID == 0) {

            // setMPC_Order_Node_ID (0);
            // setMPC_Order_Workflow_ID (0);
            // setValue (null);
            // setName (null);
            setAction(ACTION_WaitSleep);
            setCost(Env.ZERO);

            // setDuration (Env.ZERO);
            setEntityType(ENTITYTYPE_UserMaintained);		// U
            setIsCentrallyMaintained(true);			// Y
            setJoinElement(JOINELEMENT_XOR);			// X
            setDurationLimit(0);
            setSplitElement(SPLITELEMENT_XOR);			// X

//          setWaitingTime (0);
            setXPosition(0);
            setYPosition(0);
            setAD_Column_ID(0);
            setAD_WF_Responsible_ID(0);

            // setAD_Imagen_ID(0);
            setSubflowExecution(SUBFLOWEXECUTION_Synchronously);
            setDocAction(DOCACTION_None);
            setStartMode(STARTMODE_Manual);
            setFinishMode(FINISHMODE_Manual);

            // setDurationLimit(0);
            setPriority(0);

            // setDuration(Env.ZERO);
            // setWorkingTime(0);
            // fjv e-evolution
            setWaitingTime(0);

            // setWaitingTime(Env.ZERO);
            // end e-evolution
        }

        // Save to Cache
        if (getID() != 0) {
            s_cache.put(new Integer(getMPC_Order_Node_ID()), this);
        }

    }		// MMPCOrderNode

    /**
     *      Parent Constructor
     *      @param wf workflow (parent)
     *      @param Value value
     *      @param Name name
     */
    public MMPCOrderNode(MMPCOrderWorkflow wf, String Value, String Name) {

        this(wf.getCtx(), 0, Name);
        setClientOrg(wf);
        setMPC_Order_Workflow_ID(wf.getMPC_Order_Workflow_ID());
        setValue(Value);
        setName(Name);

    }		// MMPCOrderNode

    /**
     *      Load Constructor - save to cache
     *      @param ctx context
     *      @param rs result set to load info from
     * @param trxName
     */
    public MMPCOrderNode(Properties ctx, ResultSet rs, String trxName) {

        super(ctx, rs, trxName);
        loadNext();
        loadTrl();

        // Save to Cache
        s_cache.put(new Integer(getMPC_Order_Node_ID()), this);

    }		// MMPCOrderNode

    /** Next Modes */
    private ArrayList	m_next	= new ArrayList();

    /** Translated Name */
    private String	m_name_trl	= null;

    /** Translated Description */
    private String	m_description_trl	= null;

    /** Translated Help */
    private String	m_help_trl	= null;

    /** Translation Flag */
    private boolean	m_translated	= false;

    /** Column */
    private M_Column	m_column	= null;

    /** Process Parameters */

    // private MMPCOrderNodePara[]   m_paras = null;

    /**
     *      Load Next
     */
    private void loadNext() {

        String	sql	= "SELECT * FROM MPC_Order_NodeNext WHERE MPC_Order_Node_ID=? AND IsActive='Y' ORDER BY SeqNo";
        boolean	splitAnd	= SPLITELEMENT_AND.equals(getSplitElement());

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql);

            pstmt.setInt(1, getID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MMPCOrderNodeNext	next	= new MMPCOrderNodeNext(getCtx(), rs, "MPC_Order_NodeNext");

                next.setFromSplitAnd(splitAnd);
                m_next.add(next);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            log.log(Level.SEVERE, "loadNext", e);
        }

        log.info("loadNext #" + m_next.size());

    }		// loadNext

    /**
     *      Load Translation
     */
    private void loadTrl() {

        if (Env.isBaseLanguage(getCtx(), "MPC_Order_Workflow") || (getID() == 0)) {
            return;
        }

        String	sql	= "SELECT Name, Description, Help FROM MPC_Order_Node_Trl WHERE MPC_Order_Node_ID=? AND AD_Language=?";

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql);

            pstmt.setInt(1, getID());
            pstmt.setString(2, Env.getAD_Language(getCtx()));

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                m_name_trl		= rs.getString(1);
                m_description_trl	= rs.getString(2);
                m_help_trl		= rs.getString(3);
                m_translated		= true;
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            log.log(Level.SEVERE, "loadTrl", e);
        }

        log.info("loadTrl " + m_translated);

    }		// loadTrl

    /**
     *      Get Number of Next Nodes
     *      @return number of next nodes
     */
    public int getNextNodeCount() {
        return m_next.size();
    }		// getNextNodeCount

    /**
     *      Get the transitions
     *      @return array of next nodes
     */
    public MMPCOrderNodeNext[] getTransitions() {

        MMPCOrderNodeNext[]	retValue	= new MMPCOrderNodeNext[m_next.size()];

        m_next.toArray(retValue);

        return retValue;

    }		// getNextNodes

    /**
     *      Get Name
     *      @param translated translated
     *      @return Name
     */
    public String getName(boolean translated) {

        if (translated && m_translated) {
            return m_name_trl;
        }

        return getName();

    }		// getName

    /**
     *      Get Description
     *      @param translated translated
     *      @return Description
     */
    public String getDescription(boolean translated) {

        if (translated && m_translated) {
            return m_description_trl;
        }

        return getDescription();

    }		// getDescription

    /**
     *      Get Help
     *      @param translated translated
     *      @return Name
     */
    public String getHelp(boolean translated) {

        if (translated && m_translated) {
            return m_help_trl;
        }

        return getHelp();

    }		// getHelp

    /**
     *      Set Position
     *      @param position point
     */
    public void setPosition(Point position) {
        setPosition(position.x, position.y);
    }		// setPosition

    /**
     *      Set Position
     *      @param x x
     *      @param y y
     */
    public void setPosition(int x, int y) {

        setXPosition(x);
        setYPosition(y);

    }		// setPosition

    /**
     *      Get Position
     *      @return position point
     */
    public Point getPosition() {
        return new Point(getXPosition(), getYPosition());
    }		// getPosition

    /**
     *      Get Action Info
     *      @return info
     */
    public String getActionInfo() {

        String	action	= getAction();

        if (ACTION_AppsProcess.equals(action)) {
            return "Process:AD_Process_ID=" + getAD_Process_ID();
        } else if (ACTION_DocumentAction.equals(action)) {
            return "DocumentAction=" + getDocAction();
        } else if (ACTION_AppsReport.equals(action)) {
            return "Report:AD_Process_ID=" + getAD_Process_ID();
        } else if (ACTION_AppsTask.equals(action)) {
            return "Task:AD_Task_ID=" + getAD_Task_ID();
        } else if (ACTION_SetVariable.equals(action)) {
            return "SetVariable:AD_Column_ID=" + getAD_Column_ID();
        } else if (ACTION_SubWorkflow.equals(action)) {
            return "Workflow:MPC_Order_Workflow_ID=" + getMPC_Order_Workflow_ID();
        } else if (ACTION_UserChoice.equals(action)) {
            return "UserChoice:AD_Column_ID=" + getAD_Column_ID();
        } else if (ACTION_UserWorkbench.equals(action)) {
            return "Workbench:?";
        } else if (ACTION_UserForm.equals(action)) {
            return "Form:AD_Form_ID=" + getAD_Form_ID();
        } else if (ACTION_UserWindow.equals(action)) {
            return "Window:AD_Window_ID=" + getAD_Window_ID();
        }

        /*
         * else if (ACTION_WaitSleep.equals(action))
         *       return "Sleep:WaitTime=" + getWaitTime();
         */
        return "??";

    }		// getActionInfo

    /**
     *      Get Attribute Name
     *      @see org.openXpertya.model.X_MPC_Order_Node#getAttributeName()
     *      @return Attribute Name
     */
    public String getAttributeName() {

        if (getAD_Column_ID() == 0) {
            return super.getAttributeName();
        }

        // We have a column
        String	attribute	= super.getAttributeName();

        if ((attribute != null) && (attribute.length() > 0)) {
            return attribute;
        }

        setAttributeName(getColumn().getColumnName());

        return super.getAttributeName();

    }		// getAttributeName

    /**
     *      Get Column
     *      @return column if valid
     */
    public M_Column getColumn() {

        if (getAD_Column_ID() == 0) {
            return null;
        }

        if (m_column == null) {
            m_column	= M_Column.get(getCtx(), getAD_Column_ID());
        }

        return m_column;

    }		// getColumn

    /**
     *      Is this an Approval setp?
     *      @return true if User Approval
     */
    public boolean isUserApproval() {

        if (!ACTION_UserChoice.equals(getAction())) {
            return false;
        }

        return (getColumn() != null) && "IsApproved".equals(getColumn().getColumnName());

    }		// isApproval

    /**
     *      Is this a User Choice step?
     *      @return true if User Choice
     */
    public boolean isUserChoice() {
        return ACTION_UserChoice.equals(getAction());
    }		// isUserChoice

    /**
     *      Is this a Manual user step?
     *      @return true if Window/Form/Workbench
     */
    public boolean isUserManual() {

        if (ACTION_UserForm.equals(getAction()) || ACTION_UserWindow.equals(getAction()) || ACTION_UserWorkbench.equals(getAction())) {
            return true;
        }

        return false;

    }		// isUserManual

    /**
     *      Get Parameters
     *      @return array of parameters
     */

    /**
     * no apply for Manufacture
     * public MMPCOrderNodePara[] getParameters()
     * {
     *       if (m_paras == null)
     *               m_paras = MMPCOrderNodePara.getParameters(getCtx(), getMPC_Order_Node_ID());
     *       return m_paras;
     * }       //      getParameters
     */

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MMPCOrderNode[");

        sb.append(getID()).append("-").append(getName()).append(",Action=").append(getActionInfo()).append("]");

        return sb.toString();

    }		// toString

    /**
     *      User String Representation
     *      @return info
     */
    public String toStringX() {

        StringBuffer	sb	= new StringBuffer("MMPCOrderNode[");

        sb.append(getName()).append("-").append(getActionInfo());

        return sb.toString();

    }		// toStringX

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return saved
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (!success) {
            return success;
        }

        // TranslationTable.save(this, newRecord);
        return true;

    }		// afterSave

    /**
     *      After Delete
     *      @param success success
     *      @return deleted
     */
    protected boolean afterDelete(boolean success) {

        if (!TranslationTable.isActiveLanguages(false)) {
            return true;
        }

        TranslationTable.delete(this);

        return true;

    }		// afterDelete
}	// M_WFNext



/*
 * @(#)MMPCOrderNode.java   13.jun 2007
 * 
 *  Fin del fichero MMPCOrderNode.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
