/*
 * @(#)MMPCOrderWorkflow.java   13.jun 2007  Versión 2.2
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



package openXpertya.model;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;
import org.openXpertya.wf.*;

/**
 *      WorkFlow Model
 *
 *      @author         Jorg Janke
 *      @version        $Id: MWorkflow.java,v 1.27 2005/03/11 20:25:56 jjanke Exp $
 */
public class MMPCOrderWorkflow extends X_MPC_Order_Workflow {

    /**
     *      Get Workflow from Cache
     *      @param ctx context
     *      @param MPC_Order_Workflow_ID id
     *      @return workflow
     */
    public static MMPCOrderWorkflow get(Properties ctx, int MPC_Order_Workflow_ID) {

        Integer			key		= new Integer(MPC_Order_Workflow_ID);
        MMPCOrderWorkflow	retValue	= (MMPCOrderWorkflow) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MMPCOrderWorkflow(ctx, MPC_Order_Workflow_ID, null);

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    /**
     *      Get Doc Value Workflow
     *      @param ctx context
     *      @param AD_Client_ID client
     *      @param AD_Table_ID table
     *      @return document value workflow or null
     */
    public static MMPCOrderWorkflow getDocValue(Properties ctx, int AD_Client_ID, int AD_Table_ID) {

        // Reload
        if (s_cacheDocValue.isReset()) {

            String	sql	= "SELECT * FROM MPC_Order_Workflow " + "WHERE WorkflowType='V' AND IsActive='Y' AND IsValid='Y'";
            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql);

                ResultSet	rs	= pstmt.executeQuery();

                while (rs.next()) {

                    MMPCOrderWorkflow	wf	= new MMPCOrderWorkflow(ctx, rs, null);
                    String	key	= "C" + wf.getAD_Client_ID() + "T" + wf.getAD_Table_ID();

                    s_cacheDocValue.put(key, wf);
                }

                rs.close();
                pstmt.close();
                pstmt	= null;

            } catch (Exception e) {
                s_log.log(Level.SEVERE, "getDocValue", e);
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

        // Look for Entry
        String			key		= "C" + AD_Client_ID + "T" + AD_Table_ID;
        MMPCOrderWorkflow	retValue	= (MMPCOrderWorkflow) s_cacheDocValue.get(key);

        return retValue;
    }		// getDocValue

    /** Single Cache */
    private static CCache	s_cache	= new CCache("MPC_Order_Workflow", 20);

    /** Document Value Cache */
    private static CCache	s_cacheDocValue	= new CCache("MPC_Order_Workflow", 5);

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MMPCOrderWorkflow.class);

    /**
     *      Create/Load Workflow
     *      @param ctx Context
     *      @param MPC_Order_Workflow_ID ID
     * @param trxName
     */
    public MMPCOrderWorkflow(Properties ctx, int MPC_Order_Workflow_ID, String trxName) {

        super(ctx, MPC_Order_Workflow_ID, trxName);

        if (MPC_Order_Workflow_ID == 0) {

            // setMPC_Order_Workflow_ID (0);
            // setValue (null);
            // setName (null);
            setAccessLevel(ACCESSLEVEL_Organization);
            setAuthor("OpenXpertya");
            setDurationUnit(DURATIONUNIT_Day);
            setDuration(1);
            setEntityType(ENTITYTYPE_UserMaintained);		// U
            setIsDefault(false);
            setPublishStatus(PUBLISHSTATUS_UnderRevision);	// U
            setVersion(0);
            setCost(0);
            setWaitingTime(0);
            setWorkingTime(0);
        }

        loadTrl();
        loadNodes();

    }		// MWorkflow

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MMPCOrderWorkflow(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// Workflow

    /** WF Nodes */
    private ArrayList	m_nodes	= new ArrayList();

    /** Ordered Nodes in Array */
    private MMPCOrderNode[]	m_nodeArray	= null;

    /** Translated Name */
    private String	m_name_trl	= null;

    /** Translated Description */
    private String	m_description_trl	= null;

    /** Translated Help */
    private String	m_help_trl	= null;

    /** Translation Flag */
    private boolean	m_translated	= false;

    /**
     *      Load Translation
     */
    private void loadTrl() {

        if (Env.isBaseLanguage(getCtx(), "MPC_Order_Workflow") || (getID() == 0)) {
            return;
        }

        String	sql	= "SELECT Name, Description, Help FROM MPC_Order_Workflow_Trl WHERE MPC_Order_Workflow_ID=? AND AD_Language=?";

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
            log.log(Level.SEVERE, "MWorkflow.loadTrl", e);
        }

        log.fine("loadTrl " + m_translated);

    }		// loadTrl

    /**
     *      Load Nodes
     */
    private void loadNodes() {

        String	sql	= "SELECT * FROM MPC_Order_Node WHERE MPC_Order_Workflow_ID=? AND IsActive='Y'";

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql, get_TrxName());

            pstmt.setInt(1, getID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                m_nodes.add(new MMPCOrderNode(getCtx(), rs, get_TrxName()));
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            log.log(Level.SEVERE, "MWorkflow.loadNodes", e);
        }

        log.fine("loadNodes #" + m_nodes.size());

    }		// loadNodes

    /**
     *      Get Number of Nodes
     *      @return number of nodes
     */
    public int getNodeCount() {
        return m_nodes.size();
    }		// getNextNodeCount

    /**
     *      Get the nodes
     *  @param ordered ordered array
     *      @return array of nodes
     */
    public MMPCOrderNode[] getNodes(boolean ordered) {

        if (ordered) {
            return getNodesInOrder();
        }

        //
        MMPCOrderNode[]	retValue	= new MMPCOrderNode[m_nodes.size()];

        m_nodes.toArray(retValue);

        return retValue;

    }		// getNodes

    /**
     *      Get the first node
     *      @return array of next nodes
     */
    public MMPCOrderNode getFirstNode() {
        return getNode(getMPC_Order_Node_ID());
    }		// getFirstNode

    /**
     *      Get Node with ID in Workflow
     *      @param MPC_Order_Node_ID ID
     *      @return node or null
     */
    protected MMPCOrderNode getNode(int MPC_Order_Node_ID) {

        for (int i = 0; i < m_nodes.size(); i++) {

            MMPCOrderNode	node	= (MMPCOrderNode) m_nodes.get(i);

            if (node.getMPC_Order_Node_ID() == MPC_Order_Node_ID) {
                return node;
            }
        }

        return null;

    }		// getNode

    /**
     *      Get the next nodes
     *      @param MPC_Order_Node_ID ID
     *      @return array of next nodes or null
     */
    public MMPCOrderNode[] getNextNodes(int MPC_Order_Node_ID) {

        MMPCOrderNode	node	= getNode(MPC_Order_Node_ID);

        if ((node == null) || (node.getNextNodeCount() == 0)) {
            return null;
        }

        //
        MMPCOrderNodeNext[]	nexts	= node.getTransitions();
        ArrayList		list	= new ArrayList();

        for (int i = 0; i < nexts.length; i++) {

            MMPCOrderNode	next	= getNode(nexts[i].getMPC_Order_Next_ID());

            if (next != null) {
                list.add(next);
            }
        }

        // Return Nodes
        MMPCOrderNode[]	retValue	= new MMPCOrderNode[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getNextNodes

    /**
     *      Get The Nodes in Sequence Order
     *      @return Nodes in sequence
     */
    private MMPCOrderNode[] getNodesInOrder() {

        if (m_nodeArray == null) {

            ArrayList	list	= new ArrayList();

            addNodes(list, getMPC_Order_Node_ID());	// start with first

            if (m_nodes.size() != list.size()) {
                log.log(Level.SEVERE, "MWorkflow.getNodesInOrder - sizes different: " + m_nodes.size() + "<->" + list.size());
            }

            //
            m_nodeArray	= new MMPCOrderNode[list.size()];
            list.toArray(m_nodeArray);
        }

        return m_nodeArray;

    }		// getNodesInOrder

    /**
     *      Add Nodes recursively to Ordered List
     *  @param list list to add to
     *      @param MPC_Order_Node_ID start node id
     */
    private void addNodes(ArrayList list, int MPC_Order_Node_ID) {

        MMPCOrderNode	node	= getNode(MPC_Order_Node_ID);

        if ((node != null) &&!list.contains(node)) {

            list.add(node);

            MMPCOrderNodeNext[]	nexts	= node.getTransitions();

            for (int i = 0; i < nexts.length; i++) {
                addNodes(list, nexts[i].getMPC_Order_Next_ID());
            }
        }

    }		// addNode

    /**
     *      Get first transition (Next Node) of ID
     *      @param MPC_Order_Node_ID id
     *      @return next MPC_Order_Node_ID or 0
     */
    public int getNext(int MPC_Order_Node_ID) {

        MMPCOrderNode[]	nodes	= getNodesInOrder();

        for (int i = 0; i < nodes.length; i++) {

            if (nodes[i].getMPC_Order_Node_ID() == MPC_Order_Node_ID) {

                MMPCOrderNodeNext[]	nexts	= nodes[i].getTransitions();

                if (nexts.length > 0) {
                    return nexts[0].getMPC_Order_Next_ID();
                }

                return 0;
            }
        }

        return 0;

    }		// getNext

    /**
     *      Get Transitions (NodeNext) of ID
     *      @param MPC_Order_Node_ID id
     *      @return array of next nodes
     */
    public MMPCOrderNodeNext[] getNodeNexts(int MPC_Order_Node_ID) {

        MMPCOrderNode[]	nodes	= getNodesInOrder();

        for (int i = 0; i < nodes.length; i++) {

            if (nodes[i].getMPC_Order_Node_ID() == MPC_Order_Node_ID) {
                return nodes[i].getTransitions();
            }
        }

        return null;

    }		// getNext

    /**
     *      Get (first) Previous Node of ID
     *      @param MPC_Order_Node_ID id
     *      @return next MPC_Order_Node_ID or 0
     */
    public int getPrevious(int MPC_Order_Node_ID) {

        MMPCOrderNode[]	nodes	= getNodesInOrder();

        for (int i = 0; i < nodes.length; i++) {

            if (nodes[i].getMPC_Order_Node_ID() == MPC_Order_Node_ID) {

                if (i > 0) {
                    return nodes[i - 1].getMPC_Order_Node_ID();
                }

                return 0;
            }
        }

        return 0;

    }		// getPrevious

    /**
     *      Get very Last Node
     *      @param MPC_Order_Node_ID ignored
     *      @return next MPC_Order_Node_ID or 0
     */
    public int getLast(int MPC_Order_Node_ID) {

        MMPCOrderNode[]	nodes	= getNodesInOrder();

        if (nodes.length > 0) {
            return nodes[nodes.length - 1].getMPC_Order_Node_ID();
        }

        return 0;

    }		// getLast

    /**
     *      Is this the first Node
     *      @param MPC_Order_Node_ID id
     *      @return true if first node
     */
    public boolean isFirst(int MPC_Order_Node_ID) {
        return MPC_Order_Node_ID == getMPC_Order_Node_ID();
    }		// isFirst

    /**
     *      Is this the last Node
     *      @param MPC_Order_Node_ID id
     *      @return true if last node
     */
    public boolean isLast(int MPC_Order_Node_ID) {

        MMPCOrderNode[]	nodes	= getNodesInOrder();

        return MPC_Order_Node_ID == nodes[nodes.length - 1].getMPC_Order_Node_ID();

    }		// isLast

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
     *      Before Save
     *      @param newRecord new
     * @param success
     *      @return true
     */

    /*
     * protected boolean beforeSave (boolean newRecord)
     * {
     *       validate();
     *       return true;
     * }       //      beforeSave
     */

    /**
     *  After Save.
     *  @param newRecord new record
     *  @param success success
     *  @return true if save complete (if not overwritten true)
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        log.fine("afterSave - Success=" + success);

        if (success && newRecord) {

            // save all nodes -- Creating new Workflow
            MMPCOrderNode[]	nodes	= getNodesInOrder();

            for (int i = 0; i < nodes.length; i++) {
                nodes[i].save(get_TrxName());
            }
        }

        if (newRecord) {

            int	AD_Role_ID	= Env.getAD_Role_ID(getCtx());

            // MWorkflowAccess wa = new MWorkflowAccess(this, AD_Role_ID);
            // wa.save();
        }

        // Menu/Workflow
        else if (is_ValueChanged("IsActive") || is_ValueChanged("Name") || is_ValueChanged("Description") || is_ValueChanged("Help")) {

            MMenu[]	menues	= MMenu.get(getCtx(), "MPC_Order_Workflow_ID=" + getMPC_Order_Workflow_ID());

            for (int i = 0; i < menues.length; i++) {

                menues[i].setIsActive(isActive());
                menues[i].setName(getName());
                menues[i].setDescription(getDescription());
                menues[i].save(get_TrxName());
            }

            /*
             * X_MPC_Order_Node[] nodes = M_Window.getWFNodes(getCtx(), "MPC_Order_Workflow_ID=" + getMPC_Order_Workflow_ID());
             * for (int i = 0; i < nodes.length; i++)
             * {
             *       boolean changed = false;
             *       if (nodes[i].isActive() != isActive())
             *       {
             *               nodes[i].setIsActive(isActive());
             *               changed = true;
             *       }
             *       if (nodes[i].isCentrallyMaintained())
             *       {
             *               nodes[i].setName(getName());
             *               nodes[i].setDescription(getDescription());
             *               nodes[i].setHelp(getHelp());
             *               changed = true;
             *       }
             *       if (changed)
             *               nodes[i].save();
             * }
             */
        }

        return success;

    }		// afterSave

    /**
     *      Start Workflow.
     *      @param pi Process Info (Record_ID)
     *      @return process
     */

    /*
     * public MWFProcess start (ProcessInfo pi)
     * {
     *       MWFProcess retValue = null;
     *       try
     *       {
     *               retValue = new MWFProcess (this, pi);
     *               retValue.save();
     *               retValue.startWork();
     *       }
     *       catch (Exception e)
     *       {
     *               log.log(Level.SEVERE, e.getLocalizedMessage(), e);
     *               pi.setSummary(e.getMessage(), true);
     *               retValue = null;
     *       }
     *       return retValue;
     * }       //      MWFProcess
     */

    /**
     *      Start Workflow and Wait for completion.
     *      @param pi process info with Record_ID record for the workflow
     *      @return process
     */

    /*
     * public MWFProcess startWait (ProcessInfo pi)
     * {
     *       final int SLEEP = 500;          //      1/2 sec
     *       final int MAXLOOPS = 30;        //      15 sec
     *       /
     *       MWFProcess process = start(pi);
     *       if (process == null)
     *               return null;
     *       Thread.yield();
     *       StateEngine state = process.getState();
     *       int loops = 0;
     *       while (!state.isClosed() && !state.isSuspended())
     *       {
     *               if (loops > MAXLOOPS)
     *               {
     *                       log.warning("Timeout after sec " + ((SLEEP*MAXLOOPS)/1000));
     *                       pi.setSummary("Started ... still running (requery later)");
     *                       return process;
     *               }
     *       //      System.out.println("--------------- " + loops + ": " + state);
     *               try
     *               {
     *                       Thread.sleep(SLEEP);
     *                       loops++;
     *               }
     *               catch (InterruptedException e)
     *               {
     *                       log.log(Level.SEVERE, "startWait interrupted", e);
     *                       pi.setSummary("Interrupted");
     *                       return process;
     *               }
     *               Thread.yield();
     *               state = process.getState();
     *       }
     *       String summary = process.getProcessMsg();
     *       if (summary == null || summary.trim().length() == 0)
     *               summary = state.toString();
     *       pi.setSummary(summary, state.isTerminated() || state.isAborted());
     *       log.fine(summary);
     *       return process;
     * }       //      startWait
     */

    /**
     *      Get Duration Base in Seconds
     *      @return duration unit in seconds
     */
    public long getDurationBaseSec() {

        if (getDurationUnit() == null) {
            return 0;
        } else if (DURATIONUNIT_Second.equals(getDurationUnit())) {
            return 1;
        } else if (DURATIONUNIT_Minute.equals(getDurationUnit())) {
            return 60;
        } else if (DURATIONUNIT_Hour.equals(getDurationUnit())) {
            return 3600;
        } else if (DURATIONUNIT_Day.equals(getDurationUnit())) {
            return 86400;
        } else if (DURATIONUNIT_Month.equals(getDurationUnit())) {
            return 2592000;
        } else if (DURATIONUNIT_Year.equals(getDurationUnit())) {
            return 31536000;
        }

        return 0;

    }		// getDurationBaseSec

    /**
     *      Get Duration CalendarField
     *      @return Calendar.MINUTE, etc.
     */
    public int getDurationCalendarField() {

        if (getDurationUnit() == null) {
            return Calendar.MINUTE;
        } else if (DURATIONUNIT_Second.equals(getDurationUnit())) {
            return Calendar.SECOND;
        } else if (DURATIONUNIT_Minute.equals(getDurationUnit())) {
            return Calendar.MINUTE;
        } else if (DURATIONUNIT_Hour.equals(getDurationUnit())) {
            return Calendar.HOUR;
        } else if (DURATIONUNIT_Day.equals(getDurationUnit())) {
            return Calendar.DAY_OF_YEAR;
        } else if (DURATIONUNIT_Month.equals(getDurationUnit())) {
            return Calendar.MONTH;
        } else if (DURATIONUNIT_Year.equals(getDurationUnit())) {
            return Calendar.YEAR;
        }

        return Calendar.MINUTE;

    }		// getDurationCalendarField

    /**
     *      Validate workflow.
     *      Sets Valid flag
     *
     * @param args
     *      @return errors or ""
     */

    /*
     * public String validate()
     * {
     *       StringBuffer errors = new StringBuffer();
     *       /
     *       if (getMPC_Order_Node_ID() == 0)
     *               errors.append(" - No Start Node");
     *       /
     *       if (WORKFLOWTYPE_DocumentValue.equals(getWorkflowType())
     *               && (getDocValueLogic() == null || getDocValueLogic().length() == 0))
     *               errors.append(" - No Document Value Logic");
     *       /
     *
     *
     *       //      final
     *       boolean valid = errors.length() == 0;
     *       setIsValid(valid);
     *       if (!valid)
     *               log.info("validate: " + errors);
     *       return errors.toString();
     * }       //      validate
     */

//  public boolean  

    /**
     *      main
     *      @param args
     */
    public static void main(String[] args) {

        /*
         * org.openXpertya.OpenXpertya.startup(true);
         *
         * //      Create Standard Document Process
         * MMPCOrderWorkflow wf = new MMPCOrderWorkflow(Env.getCtx(), 0, null);
         * wf.setValue ("Process_xx");
         * wf.setName (wf.getValue());
         * wf.setDescription("(Standard " + wf.getValue());
         * wf.setEntityType (ENTITYTYPE_Dictionary);
         * wf.save();
         * /
         * MMPCOrderNode node10 = new MMPCOrderNode (wf, "10", "(Start)");
         * node10.setDescription("(Standard Node)");
         * node10.setEntityType (ENTITYTYPE_Dictionary);
         * node10.setAction(MMPCOrderNode.ACTION_WaitSleep);
         * node10.setWaitTime(0);
         * node10.setPosition(5, 5);
         * node10.save();
         * wf.setMPC_Order_Node_ID(node10.getMPC_Order_Node_ID());
         * wf.save();
         *
         * MMPCOrderNode node20 = new MMPCOrderNode (wf, "20", "(DocAuto)");
         * node20.setDescription("(Standard Node)");
         * node20.setEntityType (ENTITYTYPE_Dictionary);
         * node20.setAction(MMPCOrderNode.ACTION_DocumentAction);
         * node20.setDocAction(MMPCOrderNode.DOCACTION_None);
         * node20.setPosition(5, 120);
         * node20.save();
         * MMPCOrderNodeNext tr10_20 = new MMPCOrderNodeNext(Env.getCtx(), node10.getMPC_Order_Node_ID(), node20.getMPC_Order_Node_ID(), 100);
         * tr10_20.setEntityType (ENTITYTYPE_Dictionary);
         * tr10_20.setDescription("(Standard unconditional Transition)");
         * tr10_20.save();
         *
         * MMPCOrderNode node100 = new MMPCOrderNode (wf, "100", "(DocPrepare)");
         * node100.setDescription("(Standard Node)");
         * node100.setEntityType (ENTITYTYPE_Dictionary);
         * node100.setAction(MMPCOrderNode.ACTION_DocumentAction);
         * node100.setDocAction(MMPCOrderNode.DOCACTION_Prepare);
         * node100.setPosition(170, 5);
         * node100.save();
         * MMPCOrderNodeNext tr10_100 = new MMPCOrderNodeNext(Env.getCtx(), node10.getMPC_Order_Node_ID(), node100.getMPC_Order_Node_ID(), 10);
         * tr10_100.setEntityType (ENTITYTYPE_Dictionary);
         * tr10_100.setDescription("(Standard approval Transition)");
         * tr10_100.setIsStdUserWorkflow(true);
         * tr10_100.save();
         *
         * MMPCOrderNode node200 = new MMPCOrderNode (wf, "200", "(DocComplete)");
         * node200.setDescription("(Standard Node)");
         * node200.setEntityType (ENTITYTYPE_Dictionary);
         * node200.setAction(MMPCOrderNode.ACTION_DocumentAction);
         * node200.setDocAction(MMPCOrderNode.DOCACTION_Complete);
         * node200.setPosition(170, 120);
         * node200.save();
         * MMPCOrderNodeNext tr100_200 = new MMPCOrderNodeNext(Env.getCtx(), node100.getMPC_Order_Node_ID(), node200.getMPC_Order_Node_ID(), 100);
         * tr100_200.setEntityType (ENTITYTYPE_Dictionary);
         * tr100_200.setDescription("(Standard unconditional Transition)");
         * tr100_200.save();
         *
         *
         *
         * Env.setContext(Env.getCtx(), "#AD_Client_ID ", "11");
         * Env.setContext(Env.getCtx(), "#AD_Org_ID ", "11");
         * Env.setContext(Env.getCtx(), "#AD_User_ID ", "100");
         * /
         * int MPC_Order_Workflow_ID = 115;                        //      Requisition WF
         * int M_Requsition_ID = 100;
         * MRequisition req = new MRequisition (Env.getCtx(), M_Requsition_ID);
         * req.setDocStatus(DocAction.DOCSTATUS_Drafted);
         * req.save();
         * Log.setTraceLevel(8);
         * System.out.println("---------------------------------------------------");
         * MWorkflow wf = MWorkflow.get (Env.getCtx(), MPC_Order_Workflow_ID);
         */

        // wf.start(M_Requsition_ID);
    }		// main
}	// MWorkflow_ID



/*
 * @(#)MMPCOrderWorkflow.java   13.jun 2007
 * 
 *  Fin del fichero MMPCOrderWorkflow.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
