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



package org.openXpertya.wf;

import java.io.File;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.fastrack.ValidationGenerate;
import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MNote;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MUser;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_WF_Activity;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.StateEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.StringUtil;
import org.openXpertya.util.Trace;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWFActivity extends X_AD_WF_Activity implements Runnable {
	
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Table_ID
     * @param Record_ID
     * @param activeOnly
     *
     * @return
     */

    public static MWFActivity[] get( Properties ctx,int AD_Table_ID,int Record_ID,boolean activeOnly ) {
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;
        String            sql   = "SELECT * FROM AD_WF_Activity WHERE AD_Table_ID=? AND Record_ID=?";

        if( activeOnly ) {
            sql += " AND Processed<>'Y'";
        }

        sql += " ORDER BY AD_WF_Activity_ID";

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Table_ID );
            pstmt.setInt( 2,Record_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWFActivity( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MWFActivity[] retValue = new MWFActivity[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Table_ID
     * @param Record_ID
     *
     * @return
     */

    public static String getActiveInfo( Properties ctx,int AD_Table_ID,int Record_ID ) {
        MWFActivity[] acts = get( ctx,AD_Table_ID,Record_ID,true );

        if( (acts == null) || (acts.length == 0) ) {
            return null;
        }

        //

        StringBuffer sb = new StringBuffer();

        for( int i = 0;i < acts.length;i++ ) {
            if( i > 0 ) {
                sb.append( "\n" );
            }

            MWFActivity activity = acts[ i ];

            sb.append( activity.toStringX());
        }

        return sb.toString();
    }    // getActivityInfo

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MWFActivity.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_WF_Activity_ID
     * @param trxName
     */

    public MWFActivity( Properties ctx,int AD_WF_Activity_ID,String trxName ) {
        super( ctx,AD_WF_Activity_ID,trxName );

        if( AD_WF_Activity_ID == 0 ) {
            throw new IllegalArgumentException( "Cannot create new WF Activity directly" );
        }

        m_state = new StateEngine( getWFState());
    }    // MWFActivity

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWFActivity( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
        m_state = new StateEngine( getWFState());
    }    // MWFActivity

    /**
     * Constructor de la clase ...
     *
     *
     * @param process
     * @param AD_WF_Node_ID
     */

    public MWFActivity( MWFProcess process,int AD_WF_Node_ID ) {
        super( process.getCtx(),0,process.get_TrxName());
        setAD_WF_Process_ID( process.getAD_WF_Process_ID());
        setPriority( process.getPriority());

        // Document Link

        setAD_Table_ID( process.getAD_Table_ID());
        setRecord_ID( process.getRecord_ID());

        // Status

        super.setWFState( WFSTATE_NotStarted );
        m_state = new StateEngine( getWFState());
        setProcessed( false );

        // Set Workflow Node

        setAD_Workflow_ID( process.getAD_Workflow_ID());
        setAD_WF_Node_ID( AD_WF_Node_ID );

        // Node Priority & End Duration

        MWFNode node     = MWFNode.get( getCtx(),AD_WF_Node_ID );
        int     priority = node.getPriority();

        if( (priority != 0) && (priority != getPriority())) {
            setPriority( priority );
        }

        long limitMS = node.getLimitMS();

        if( limitMS != 0 ) {
            setEndWaitTime( new Timestamp( limitMS + System.currentTimeMillis()));
        }

        // Responsible

        setResponsible( process );
        save();

        //

        m_audit = new MWFEventAudit( this );
        m_audit.save();

        //

        m_process = process;
    }    // MWFActivity

    /** Descripción de Campos */

    private StateEngine m_state = null;

    /** Descripción de Campos */

    private MWFNode m_node = null;

    /** Descripción de Campos */

    private MWFEventAudit m_audit = null;

    /** Descripción de Campos */

    private PO m_po = null;

    /** Descripción de Campos */

    private String m_newValue = null;

    /** Descripción de Campos */

    private MWFProcess m_process = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public StateEngine getState() {
        return m_state;
    }    // getState

    /**
     * Descripción de Método
     *
     *
     * @param WFState
     */

    public void setWFState( String WFState ) {
        if( m_state == null ) {
            m_state = new StateEngine( getWFState());
        }

        if( m_state.isClosed()) {
            return;
        }

        if( getWFState().equals( WFState )) {
            return;
        }

        //

        if( m_state.isValidNewState( WFState )) {
            String oldState = getWFState();

            log.fine( oldState + "->" + WFState + ", Msg=" + getTextMsg());
            super.setWFState( WFState );
            m_state = new StateEngine( getWFState());
            save();    // closed in MWFProcess.checkActivities()
            updateEventAudit();

            // Inform Process

            if( m_process == null ) {
                m_process = new MWFProcess( getCtx(),getAD_WF_Process_ID(),null );
            }

            m_process.checkActivities();
        } else {
            String msg = "Set WFState - Ignored Invalid Transformation - New=" + WFState + ", Current=" + getWFState();

            log.log( Level.SEVERE,msg );
            Trace.printStack();
            setTextMsg( msg );
            save();
        }
    }    // setWFState

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isClosed() {
        return m_state.isClosed();
    }    // isClosed

    /**
     * Descripción de Método
     *
     */

    private void updateEventAudit() {

        // log.fine("updateEventAudit");

        getEventAudit();
        m_audit.setTextMsg( getTextMsg());
        m_audit.setWFState( getWFState());

        if( m_newValue != null ) {
            m_audit.setNewValue( m_newValue );
        }

        if( m_state.isClosed()) {
            m_audit.setEventType( MWFEventAudit.EVENTTYPE_ProcessCompleted );

            long ms = System.currentTimeMillis() - m_audit.getCreated().getTime();

            m_audit.setElapsedTimeMS( new BigDecimal( ms ));
        } else {
            m_audit.setEventType( MWFEventAudit.EVENTTYPE_StateChanged );
        }

        m_audit.save();
    }    // updateEventAudit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MWFEventAudit getEventAudit() {
        if( m_audit != null ) {
            return m_audit;
        }

        MWFEventAudit[] events = MWFEventAudit.get( getCtx(),getAD_WF_Process_ID(),getAD_WF_Node_ID());

        if( (events == null) || (events.length == 0) ) {
            m_audit = new MWFEventAudit( this );
        } else {
            m_audit = events[ events.length - 1 ];    // last event
        }

        return m_audit;
    }    // getEventAudit

    /**
     * Descripción de Método
     *
     *
     * @param trx
     *
     * @return
     */

    public PO getPO( Trx trx ) {
        if( m_po != null ) {
            return m_po;
        }

        M_Table table = M_Table.get( getCtx(),getAD_Table_ID());

        if( trx != null ) {
            m_po = table.getPO( getRecord_ID(),trx.getTrxName());
        } else {
            m_po = table.getPO( getRecord_ID(),null );
        }

        // Disytel
        // Agrega el listener de estado de la acción del documento, en caso
        // que el el ProcessInfo tenga asignado uno.
        if(m_process != null && m_process.getProcessInfo() != null && m_process.getProcessInfo().getDocActionStatusListener() != null)
        	m_po.addDocActionStatusListener(m_process.getProcessInfo().getDocActionStatusListener());

        return m_po;
    }    // getPO

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PO getPO() {
        return getPO( null );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getAttributeValue() {
        MWFNode node = getNode();

        if( node == null ) {
            return null;
        }

        int AD_Column_ID = node.getAD_Column_ID();

        if( AD_Column_ID == 0 ) {
            return null;
        }

        PO po = getPO();

        if( po.getID() == 0 ) {
            return null;
        }

        return po.get_ValueOfColumn( AD_Column_ID );
    }    // getAttributeValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSOTrx() {
        PO po = getPO();

        if( po.getID() == 0 ) {
            return true;
        }

        // Is there a Column?

        int index = po.get_ColumnIndex( "IsSOTrx" );

        if( index < 0 ) {
            if( po.get_TableName().startsWith( "M_" )) {
                return false;
            }

            return true;
        }

        // we have a column

        try {
            Boolean IsSOTrx = ( Boolean )po.get_Value( index );

            return IsSOTrx.booleanValue();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"isSOTrx",e );
        }

        return true;
    }    // isSOTrx

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     */

    public void setAD_WF_Node_ID( int AD_WF_Node_ID ) {
        if( AD_WF_Node_ID == 0 ) {
            throw new IllegalArgumentException( "Workflow Node is not defined" );
        }

        super.setAD_WF_Node_ID( AD_WF_Node_ID );

        //

        if( !WFSTATE_NotStarted.equals( getWFState())) {
            super.setWFState( WFSTATE_NotStarted );
            m_state = new StateEngine( getWFState());
        }

        if( isProcessed()) {
            setProcessed( false );
        }
    }    // setAD_WF_Node_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MWFNode getNode() {
        if( m_node == null ) {
            m_node = MWFNode.get( getCtx(),getAD_WF_Node_ID());
        }

        return m_node;
    }    // getNode

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getNodeName() {
        return getNode().getName( true );
    }    // getNodeName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getNodeDescription() {
        return getNode().getDescription( true );
    }    // getNodeDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getNodeHelp() {
        return getNode().getHelp( true );
    }    // getNodeHelp

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUserApproval() {
        return getNode().isUserApproval();
    }    // isNodeApproval

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUserManual() {
        return getNode().isUserManual();
    }    // isUserManual

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUserChoice() {
        return getNode().isUserChoice();
    }    // isUserChoice

    /**
     * Descripción de Método
     *
     *
     * @param TextMsg
     */

    public void setTextMsg( String TextMsg ) {
        if( (TextMsg == null) || (TextMsg.length() == 0) ) {
            return;
        }

        String oldText = getTextMsg();

        if( (oldText == null) || (oldText.length() == 0) ) {
            super.setTextMsg( StringUtil.trim( TextMsg,1000 ));
        } else if( (TextMsg != null) && (TextMsg.length() > 0) ) {
            super.setTextMsg( StringUtil.trim( oldText + "\n - " + TextMsg,1000 ));
        }
    }    // setTextMsg

    /**
     * Descripción de Método
     *
     *
     * @param obj
     */

    public void addTextMsg( Object obj ) {
        if( obj == null ) {
            return;
        }

        //

        StringBuffer TextMsg = new StringBuffer( obj.toString());

        if( obj instanceof Exception ) {
            Exception ex = ( Exception )obj;

            while( ex != null ) {
                StackTraceElement[] st = ex.getStackTrace();

                for( int i = 0;i < st.length;i++ ) {
                    StackTraceElement ste = st[ i ];

                    if( (i == 0) || ste.getClassName().startsWith( "org.openXpertya" )) {
                        TextMsg.append( " (" ).append( i ).append( "): " ).append( ste.toString()).append( "\n" );
                    }
                }

                if( ex.getCause() instanceof Exception ) {
                    ex = ( Exception )ex.getCause();
                } else {
                    ex = null;
                }
            }
        }

        //

        String oldText = getTextMsg();

        if( (oldText == null) || (oldText.length() == 0) ) {
            super.setTextMsg( StringUtil.trim( TextMsg.toString(),1000 ));
        } else if( (TextMsg != null) && (TextMsg.length() > 0) ) {
            super.setTextMsg( StringUtil.trim( oldText + "\n - " + TextMsg.toString(),1000 ));
        }
    }    // setTextMsg

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getWFStateText() {
        return MRefList.getListName( getCtx(),WFSTATE_AD_Reference_ID,getWFState());
    }    // getWFStateText

    /**
     * Descripción de Método
     *
     *
     * @param process
     */

    private void setResponsible( MWFProcess process ) {

        // Responsible

        int AD_WF_Responsible_ID = getNode().getAD_WF_Responsible_ID();

        if( AD_WF_Responsible_ID == 0 ) {    // not defined on Node Level
            AD_WF_Responsible_ID = process.getAD_WF_Responsible_ID();
        }

        setAD_WF_Responsible_ID( AD_WF_Responsible_ID );

        MWFResponsible resp = MWFResponsible.get( getCtx(),AD_WF_Responsible_ID );

        // User - Directly responsible

        int AD_User_ID = resp.getAD_User_ID();

        // Invoker - get Sales Rep or last updater of document

        if( (AD_User_ID == 0) && resp.isInvoker()) {
            AD_User_ID = process.getAD_User_ID();
        }

        //

        setAD_User_ID( AD_User_ID );
    }    // setResponsible

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInvoker() {
        MWFResponsible resp = MWFResponsible.get( getCtx(),getAD_WF_Responsible_ID());

        return resp.isInvoker();
    }    // isInvoker

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     * @param C_Currency_ID
     * @param amount
     * @param AD_Org_ID
     * @param ownDocument
     *
     * @return
     */

    public int getApprovalUser( int AD_User_ID,int C_Currency_ID,BigDecimal amount,int AD_Org_ID,boolean ownDocument ) {

        // Nothing to approve

        if( (amount == null) || (amount.compareTo( Env.ZERO ) == 0) ) {
            return AD_User_ID;
        }

        // Starting user

        MUser user = MUser.get( getCtx(),AD_User_ID );

        log.info( "For User=" + user + ", Amt=" + amount + ", Own=" + ownDocument );

        MUser oldUser = null;

        while( user != null ) {
            if( user.equals( oldUser )) {
                log.info( "Loop - " + user.getName());

                return -1;
            }

            oldUser = user;
            log.fine( "User=" + user.getName());

            // Get Roles of User

            MRole[] roles = user.getRoles( AD_Org_ID );

            for( int i = 0;i < roles.length;i++ ) {
                MRole role = roles[ i ];

                if( ownDocument &&!role.isCanApproveOwnDoc()) {
                    continue;
                }

                BigDecimal roleAmt = role.getAmtApproval();

                if( (roleAmt == null) || (roleAmt.compareTo( Env.ZERO ) == 0) ) {
                    continue;
                }

                if( (C_Currency_ID != role.getC_Currency_ID()) && (role.getC_Currency_ID() != 0) )    // No currency = amt only
                {
                    roleAmt = MConversionRate.convert( getCtx(),    // today & default rate
                                                       roleAmt,role.getC_Currency_ID(),C_Currency_ID,getAD_Client_ID(),AD_Org_ID );

                    if( (roleAmt == null) || (roleAmt.compareTo( Env.ZERO ) == 0) ) {
                        continue;
                    }
                }

                boolean approved = amount.compareTo( roleAmt ) <= 0;

                log.fine( "Approved=" + approved + " - User=" + user.getName() + ", Role=" + role.getName() + ", ApprovalAmt=" + roleAmt );

                if( approved ) {
                    return user.getAD_User_ID();
                }
            }

            // **** Find next User
            // Get Supervisor

            if( user.getSupervisor_ID() != 0 ) {
                user = MUser.get( getCtx(),user.getSupervisor_ID());
                log.fine( "Supervisor: " + user.getName());
            } else {
                log.fine( "No Supervisor" );

                MOrg     org     = MOrg.get( getCtx(),AD_Org_ID );
                MOrgInfo orgInfo = org.getInfo();

                // Get Org Supervisor

                if( orgInfo.getSupervisor_ID() != 0 ) {
                    user = MUser.get( getCtx(),orgInfo.getSupervisor_ID());
                    log.fine( "Org=" + org.getName() + ",Supervisor: " + user.getName());
                } else {
                    log.fine( "No Org Supervisor" );

                    // Get Parent Org Supervisor

                    if( orgInfo.getParent_Org_ID() != 0 ) {
                        org     = MOrg.get( getCtx(),orgInfo.getParent_Org_ID());
                        orgInfo = org.getInfo();

                        if( orgInfo.getSupervisor_ID() != 0 ) {
                            user = MUser.get( getCtx(),orgInfo.getSupervisor_ID());
                            log.fine( "Parent Org Supervisor: " + user.getName());
                        }
                    }
                }
            }    // No Supervisor
        }        // while there is a user to approve

        log.fine( "No user found" );

        return -1;
    }    // getApproval

    /**
     * Descripción de Método
     *
     */

    public void run() {
        log.info( "Node=" + getNode());
        m_newValue = null;

        if( !m_state.isValidAction( StateEngine.ACTION_Start )) {
            setTextMsg( "State=" + getWFState() + " - cannot start" );
            setWFState( StateEngine.STATE_Terminated );

            return;
        }

        //

        setWFState( StateEngine.STATE_Running );

        Trx trx = Trx.get( Trx.createTrxName( "WF" ),true );
        trx.start();
        
        //

        try {
            if( getNode().getID() == 0 ) {
                setTextMsg( "Node not found - AD_WF_Node_ID=" + getAD_WF_Node_ID());
                setWFState( StateEngine.STATE_Aborted );

                return;
            }

            // Do Work

            boolean done = performWork( trx );

            // begin vpj-cd e-evolution 03/08/2005 PostgreSQL
            // Reason: When is execute setWFState create a transaction for same table is generate a clicle Trx
            // is cause that PostgreSQL wait into transaction idle
            // setWFState (done ? StateEngine.STATE_Completed : StateEngine.STATE_Suspended);
            // end vpj-cd e-evolution 03/08/2005 PostgreSQL

            addJustPreparedDocs();
            
            trx.commit();
            trx.close();

            // begin vpj-cd e-evolution 03/08/2005 PostgreSQL
            // Reason: setWFState moved for the first trx be finished

            setWFState( done
                        ?StateEngine.STATE_Completed
                        :StateEngine.STATE_Suspended );

            removeJustPreparedDocs(true);
            
            // end vpj-cd e-evolution 03/08/2005 PostgreSQL

        } catch( Exception e ) {
            trx.rollback();
            trx.close();

            //
            removeJustPreparedDocs(false);

            log.log( Level.SEVERE,"run",e );

            if( e.getCause() != null ) {
                log.log( Level.SEVERE,"cause",e.getCause());
            }

            String processMsg = e.getLocalizedMessage();

            if( (processMsg == null) || (processMsg.length() == 0) ) {
                processMsg = e.getMessage();
            }

            setTextMsg( processMsg );
            addTextMsg( e );
            setWFState( StateEngine.STATE_Terminated );
        }
    }    // run

    public synchronized void addJustPreparedDocs(){
		if (m_po != null && m_po instanceof DocAction
				&& getNode().getDocAction().equals(DocAction.ACTION_Prepare)) {
    		docs_justPrepared.put(m_po.get_Table_ID()+"_"+m_po.getID(), true);
    	}
    }
    
    public synchronized void removeJustPreparedDocs(boolean onlyComplete){
		if (m_po != null && m_po instanceof DocAction
				&& (!onlyComplete 
						|| (onlyComplete && getNode().getDocAction().equals(DocAction.ACTION_Complete)))) {
    		docs_justPrepared.remove(m_po.get_Table_ID()+"_"+m_po.getID());
    	}
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param trx
     *
     * @return
     *
     * @throws Exception
     */

    private boolean performWork( Trx trx ) throws Exception {
        log.info( m_node + " [" + trx.getTrxName() + "]" );

        if( m_node.getPriority() != 0 ) {    // overwrite priority if defined
            setPriority( m_node.getPriority());
        }

        String action = m_node.getAction();

        if( MWFNode.ACTION_WaitSleep.equals( action )) {
            log.fine( "Sleep:WaitTime=" + m_node.getWaitTime());

            if( m_node.getWaitingTime() == 0 ) {
                return true;    // done
            }

            Calendar cal = Calendar.getInstance();

            cal.add( m_node.getDurationCalendarField(),m_node.getWaitTime());
            setEndWaitTime( new Timestamp( cal.getTimeInMillis()));

            return false;       // not done
        } else if( MWFNode.ACTION_DocumentAction.equals( action )) {
            log.fine( "DocumentAction=" + m_node.getDocAction());
            getPO( trx );

            if( m_po == null ) {
                throw new Exception( "Persistent Object not found - AD_Table_ID=" + getAD_Table_ID() + ", Record_ID=" + getRecord_ID());
            }

            m_po.set_TrxName( trx.getTrxName());

            boolean success    = false;
            String  processMsg = null;

            if( m_po instanceof DocAction ) {
                DocAction doc = ( DocAction )m_po;

                /*
                * Adición por Matías Cap - Disytel
                * 
                * Código que se define para el dispositivo anticopia
                * 
                * -----------------------------------------------------
                * Código anterior
                * -----------------------------------------------------
                * 
                * success = doc.processIt( m_node.getDocAction());    // ** Do the work
                * setTextMsg( doc.getSummary());
                * processMsg = doc.getProcessMsg();
                *
                * -----------------------------------------------------
                * Fin Código anterior
                * -----------------------------------------------------
                */
               
                if(true || ValidationGenerate.validate()){ // bypass
                	success = doc.processIt( m_node.getDocAction());    // ** Do the work
                    setTextMsg( doc.getSummary());
                    processMsg = doc.getProcessMsg();
                }
                else{
                	processMsg = ValidationGenerate.getMsg();
                }              
                
                /*
                 * Fin adición Matías Cap - Disytel
                 */
                //

                if( m_process != null ) {
                    m_process.setProcessMsg( processMsg );
                }
            } else {
                throw new IllegalStateException( "Persistent Object not DocAction - " + m_po.getClass().getName() + " - AD_Table_ID=" + getAD_Table_ID() + ", Record_ID=" + getRecord_ID());
            }

            //

            if( !m_po.save()) {
                success    = false;
                String error = CLogger.retrieveErrorAsString();
				processMsg = Util.isEmpty(error, true)
						? (Util.isEmpty(m_po.getProcessMsg(), true) ? "SaveError" : m_po.getProcessMsg()) : error;
            }

            if( !success ) {
                if( (processMsg == null) || (processMsg.length() == 0) ) {
                    processMsg = "PerformWork Error - " + m_node.toStringX();
                }

                throw new Exception( processMsg );
            }

            return success;
        }    // DocumentAction
                else if( MWFNode.ACTION_AppsReport.equals( action )) {
            log.fine( "Report:AD_Process_ID=" + m_node.getAD_Process_ID());

            // Process

            MProcess process = MProcess.get( getCtx(),m_node.getAD_Process_ID());

            if( !process.isReport() || (process.getAD_ReportView_ID() == 0) ) {
                throw new IllegalStateException( "Not a Report AD_Process_ID=" + m_node.getAD_Process_ID());
            }

            //

            ProcessInfo pi = new ProcessInfo( m_node.getName( true ),m_node.getAD_Process_ID(),getAD_Table_ID(),getRecord_ID());

            pi.setAD_User_ID( getAD_User_ID());
            pi.setAD_Client_ID( getAD_Client_ID());

            MPInstance pInstance = new MPInstance( process,getRecord_ID());

            fillParameter( pInstance,trx );
            pi.setAD_PInstance_ID( pInstance.getAD_PInstance_ID());

            // Report

            ReportEngine re = ReportEngine.get( getCtx(),pi );

            if( re == null ) {
                throw new IllegalStateException( "Cannot create Report AD_Process_ID=" + m_node.getAD_Process_ID());
            }

            File report = re.getPDF();

            // Notice

            int   AD_Message_ID = 753;    // HARDCODED WorkflowResult
            MNote note          = new MNote( getCtx(),AD_Message_ID,getAD_User_ID(),trx.getTrxName());

            note.setTextMsg( m_node.getName( true ));
            note.setDescription( m_node.getDescription( true ));
            note.setRecord( getAD_Table_ID(),getRecord_ID());
            note.save();

            // Attachment

            MAttachment attachment = new MAttachment( getCtx(),MNote.Table_ID,note.getAD_Note_ID(),get_TrxName());

            attachment.addEntry( report );
            attachment.setTextMsg( m_node.getName( true ));
            attachment.save();

            return true;
        } else if( MWFNode.ACTION_AppsProcess.equals( action )) {
            log.fine( "Process:AD_Process_ID=" + m_node.getAD_Process_ID());

            // Process

            MProcess process = MProcess.get( getCtx(),m_node.getAD_Process_ID());

            //

            ProcessInfo pi = new ProcessInfo( m_node.getName( true ),m_node.getAD_Process_ID(),getAD_Table_ID(),getRecord_ID());

            pi.setAD_User_ID( getAD_User_ID());
            pi.setAD_Client_ID( getAD_Client_ID());

            MPInstance pInstance = new MPInstance( process,getRecord_ID());

            fillParameter( pInstance,trx );
            pi.setAD_PInstance_ID( pInstance.getAD_PInstance_ID());

            return process.processIt( pi,trx );
        } else if( MWFNode.ACTION_AppsTask.equals( action )) {
            log.warning( "Task:AD_Task_ID=" + m_node.getAD_Task_ID());
        } else if( MWFNode.ACTION_SetVariable.equals( action )) {
            String value = m_node.getAttributeValue();

            log.fine( "SetVariable:AD_Column_ID=" + m_node.getAD_Column_ID() + " to " + value );

            M_Column column = m_node.getColumn();
            int      dt     = column.getAD_Reference_ID();

            return setVariable( value,dt,null );
        }    // SetVariable
                else if( MWFNode.ACTION_SubWorkflow.equals( action )) {
            log.warning( "Workflow:AD_Workflow_ID=" + m_node.getAD_Workflow_ID());
        } else if( MWFNode.ACTION_UserChoice.equals( action )) {
            log.fine( "UserChoice:AD_Column_ID=" + m_node.getAD_Column_ID());

            return false;
        } else if( MWFNode.ACTION_UserWorkbench.equals( action )) {
            log.fine( "Workbench:?" );

            return false;
        } else if( MWFNode.ACTION_UserForm.equals( action )) {
            log.fine( "Form:AD_Form_ID=" + m_node.getAD_Form_ID());

            return false;
        } else if( MWFNode.ACTION_UserWindow.equals( action )) {
            log.fine( "Window:AD_Window_ID=" + m_node.getAD_Window_ID());

            return false;
        }

        //

        throw new IllegalArgumentException( "Invalid Action (Not Implemented) =" + action );
    }    // performWork

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param displayType
     * @param textMsg
     *
     * @return
     *
     * @throws Exception
     */

    private boolean setVariable( String value,int displayType,String textMsg ) throws Exception {
        m_newValue = null;
        getPO();

        if( m_po == null ) {
            throw new Exception( "Persistent Object not found - AD_Table_ID=" + getAD_Table_ID() + ", Record_ID=" + getRecord_ID());
        }

        // Set Value

        Object dbValue = null;

        if( value == null ) {
            ;
        } else if( displayType == DisplayType.YesNo ) {
            dbValue = new Boolean( "Y".equals( value ));
        } else if( DisplayType.isNumeric( displayType )) {
            dbValue = new BigDecimal( value );
        } else {
            dbValue = value;
        }

        /*m_po.set_ValueOfColumn( getNode().getAD_Column_ID(),dbValue );
        m_po.save();

        if( !dbValue.equals( m_po.get_ValueOfColumn( getNode().getAD_Column_ID()))) {
            throw new Exception( "Persistent Object not updated - AD_Table_ID=" + getAD_Table_ID() + ", Record_ID=" + getRecord_ID() + " - Should=" + value + ", Is=" + m_po.get_ValueOfColumn( m_node.getAD_Column_ID()));
        }*/

        // Info

        String msg = getNode().getAttributeName() + "=" + value;

        if( (textMsg != null) && (textMsg.length() > 0) ) {
            msg += " - " + textMsg;
        }

        setTextMsg( msg );
        m_newValue = value;

        return true;
    }    // setVariable

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     * @param value
     * @param displayType
     * @param textMsg
     *
     * @return
     *
     * @throws Exception
     */

    public boolean setUserChoice( int AD_User_ID,String value,int displayType,String textMsg ) throws Exception {
        setWFState( StateEngine.STATE_Running );
        setAD_User_ID( AD_User_ID );

        boolean ok = setVariable( value,displayType,textMsg );

        if( !ok ) {
            return false;
        }

        String newState = StateEngine.STATE_Completed;

        // Approval

        if( getNode().isUserApproval() && (getPO() instanceof DocAction) ) {
            DocAction doc = ( DocAction )m_po;

            try {

                // Not pproved

                if( !"Y".equals( value )) {
                    newState = StateEngine.STATE_Aborted;

                    if( !( doc.processIt( DocAction.ACTION_Reject ))) {
                        setTextMsg( "Cannot Reject - Document Status: " + doc.getDocStatus());
                    }
                } else {
                    if( isInvoker()) {
                        int startAD_User_ID = getAD_User_ID();

                        if( startAD_User_ID == 0 ) {
                            startAD_User_ID = doc.getDoc_User_ID();
                        }

                        int nextAD_User_ID = getApprovalUser( startAD_User_ID,doc.getC_Currency_ID(),doc.getApprovalAmt(),doc.getAD_Org_ID(),startAD_User_ID == doc.getDoc_User_ID());

                        // No Approver

                        if( nextAD_User_ID <= 0 ) {
                            newState = StateEngine.STATE_Aborted;
                            setTextMsg( "Cannot Approve - No Approver" );
                            doc.processIt( DocAction.ACTION_Reject );
                        } else if( startAD_User_ID != nextAD_User_ID ) {
                            forwardTo( nextAD_User_ID,"Next Approver" );
                            newState = StateEngine.STATE_Suspended;
                        } else    // Approve
                        {
                            if( !( doc.processIt( DocAction.ACTION_Approve ))) {
                                newState = StateEngine.STATE_Aborted;
                                setTextMsg( "Cannot Approve - Document Status: " + doc.getDocStatus());
                            }
                        }
                    }

                    // No Invoker - Approve

                    else if( !( doc.processIt( DocAction.ACTION_Approve ))) {
                        newState = StateEngine.STATE_Aborted;
                        setTextMsg( "Cannot Approve - Document Status: " + doc.getDocStatus());
                    }
                }

                doc.save();
            } catch( Exception e ) {
                newState = StateEngine.STATE_Terminated;
                setTextMsg( "User Choice: " + e.toString());
                log.log( Level.SEVERE,"setUserChoice",e );
            }
        }

        setWFState( newState );

        return ok;
    }    // setUserChoice

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     * @param textMsg
     *
     * @return
     */

    public boolean forwardTo( int AD_User_ID,String textMsg ) {
        if( AD_User_ID == getAD_User_ID()) {
            log.log( Level.SEVERE,"Same User - AD_User_ID=" + AD_User_ID );

            return false;
        }

        //

        MUser oldUser = MUser.get( getCtx(),getAD_User_ID());
        MUser user    = MUser.get( getCtx(),AD_User_ID );

        if( (user == null) || (user.getID() == 0) ) {
            log.log( Level.SEVERE,"Does not exist - AD_User_ID=" + AD_User_ID );

            return false;
        }

        // Update

        setAD_User_ID( user.getAD_User_ID());
        setTextMsg( textMsg );
        save();

        // Close up Old Event

        getEventAudit();
        m_audit.setAD_User_ID( oldUser.getAD_User_ID());
        m_audit.setTextMsg( getTextMsg());
        m_audit.setAttributeName( "AD_User_ID" );
        m_audit.setOldValue( oldUser.getName() + "(" + oldUser.getAD_User_ID() + ")" );
        m_audit.setNewValue( user.getName() + "(" + user.getAD_User_ID() + ")" );

        //

        m_audit.setWFState( getWFState());
        m_audit.setEventType( MWFEventAudit.EVENTTYPE_StateChanged );

        long ms = System.currentTimeMillis() - m_audit.getCreated().getTime();

        m_audit.setElapsedTimeMS( new BigDecimal( ms ));
        m_audit.save();

        // Create new one

        m_audit = new MWFEventAudit( this );
        m_audit.save();

        return true;
    }    // forwardTo

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     * @param textMsg
     */

    public void setUserConfirmation( int AD_User_ID,String textMsg ) {
        log.fine( textMsg );
        setWFState( StateEngine.STATE_Running );
        setAD_User_ID( AD_User_ID );

        if( textMsg != null ) {
            setTextMsg( textMsg );
        }

        setWFState( StateEngine.STATE_Completed );
    }    // setUserConfirmation

    /**
     * Descripción de Método
     *
     *
     * @param pInstance
     * @param trx
     */

    private void fillParameter( MPInstance pInstance,Trx trx ) {
        getPO( trx );

        //

        MWFNodePara[]    nParams = m_node.getParameters();
        MPInstancePara[] iParams = pInstance.getParameters();

        for( int pi = 0;pi < iParams.length;pi++ ) {
            MPInstancePara iPara = iParams[ pi ];

            for( int np = 0;np < nParams.length;np++ ) {
                MWFNodePara nPara = nParams[ np ];

                if( iPara.getParameterName().equals( nPara.getAttributeName())) {
                    String variableName = nPara.getAttributeValue();

                    log.fine( nPara.getAttributeName() + " = " + variableName );

                    // Value - Constant/Variable

                    Object value = variableName;

                    if( (variableName != null) && (variableName.length() == 0) ) {
                        value = null;
                    } else if( (variableName.indexOf( "@" ) != -1) && (m_po != null) )    // we have a variable
                    {

                        // Strip

                        int    index      = variableName.indexOf( "@" );
                        String columnName = variableName.substring( index + 1 );

                        index = columnName.indexOf( "@" );

                        if( index == -1 ) {
                            log.warning( nPara.getAttributeName() + " - cannot evaluate=" + variableName );

                            break;
                        }

                        columnName = columnName.substring( 0,index );
                        index      = m_po.get_ColumnIndex( columnName );

                        if( index != -1 ) {
                            value = m_po.get_Value( index );
                        } else    // not a column
                        {

                            // try Env

                            String env = Env.getContext( getCtx(),columnName );

                            if( env.length() == 0 ) {
                                log.warning( nPara.getAttributeName() + " - not column nor environment =" + columnName + "(" + variableName + ")" );

                                break;
                            } else {
                                value = env;
                            }
                        }
                    }    // @variable@

                    // No Value

                    if( value == null ) {
                        if( nPara.isMandatory()) {
                            log.warning( nPara.getAttributeName() + " - empty - mandatory!" );
                        } else {
                            log.fine( nPara.getAttributeName() + " - empty" );
                        }

                        break;
                    }

                    // Convert to Type

                    try {
                        if( DisplayType.isNumeric( nPara.getDisplayType()) || DisplayType.isID( nPara.getDisplayType())) {
                            BigDecimal bd = null;

                            if( value instanceof BigDecimal ) {
                                bd = ( BigDecimal )value;
                            } else if( value instanceof Integer ) {
                                bd = new BigDecimal((( Integer )value ).intValue());
                            } else {
                                bd = new BigDecimal( value.toString());
                            }

                            iPara.setP_Number( bd );
                            log.fine( nPara.getAttributeName() + " = " + variableName + " (=" + bd + "=)" );
                        } else if( DisplayType.isDate( nPara.getDisplayType())) {
                            Timestamp ts = null;

                            if( value instanceof Timestamp ) {
                                ts = ( Timestamp )value;
                            } else {
                                ts = Timestamp.valueOf( value.toString());
                            }

                            iPara.setP_Date( ts );
                            log.fine( nPara.getAttributeName() + " = " + variableName + " (=" + ts + "=)" );
                        } else {
                            iPara.setP_String( value.toString());
                            log.fine( nPara.getAttributeName() + " = " + variableName + " (=" + value + "=) " + value.getClass().getName());
                        }
                    } catch( Exception e ) {
                        log.warning( nPara.getAttributeName() + " = " + variableName + " (" + value + ") " + value.getClass().getName() + " - " + e.getLocalizedMessage());
                    }

                    break;
                }
            }    // node parameter loop
        }        // instance parameter loop
    }            // fillParameter

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHistoryHTML() {
        SimpleDateFormat format = DisplayType.getDateFormat( DisplayType.DateTime );
        StringBuffer    sb     = new StringBuffer();
        MWFEventAudit[] events = MWFEventAudit.get( getCtx(),getAD_WF_Process_ID());

        for( int i = 0;i < events.length;i++ ) {
            MWFEventAudit audit = events[ i ];

            // sb.append("<p style=\"width:400\">");

            sb.append( "<p>" );
            sb.append( format.format( audit.getCreated())).append( " " ).append( getHTMLpart( "b",audit.getNodeName())).append( ": " ).append( getHTMLpart( null,audit.getDescription())).append( getHTMLpart( "i",audit.getTextMsg()));
            sb.append( "</p>" );
        }

        return sb.toString();
    }    // getHistory

    /**
     * Descripción de Método
     *
     *
     * @param tag
     * @param content
     *
     * @return
     */

    private StringBuffer getHTMLpart( String tag,String content ) {
        StringBuffer sb = new StringBuffer();

        if( (content == null) || (content.length() == 0) ) {
            return sb;
        }

        if( (tag != null) && (tag.length() > 0) ) {
            sb.append( "<" ).append( tag ).append( ">" );
        }

        sb.append( content );

        if( (tag != null) && (tag.length() > 0) ) {
            sb.append( "</" ).append( tag ).append( ">" );
        }

        return sb;
    }    // getHTMLpart

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPdfAttachment() {
        if( getPO() == null ) {
            return false;
        }

        return m_po.isPdfAttachment();
    }    // isPDFAttachment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public byte[] getPdfAttachment() {
        if( getPO() == null ) {
            return null;
        }

        return m_po.getPdfAttachment();
    }    // getPdfAttachment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MWFActivity[" );

        sb.append( getID()).append( ",Node=" );

        if( m_node == null ) {
            sb.append( getAD_WF_Node_ID());
        } else {
            sb.append( m_node.getName());
        }

        sb.append( ",State=" ).append( getWFState()).append( ",AD_User_ID=" ).append( getAD_User_ID()).append( "," ).append( getCreated()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toStringX() {
        StringBuffer sb = new StringBuffer();

        sb.append( getWFStateText()).append( ": " ).append( getNode().getName());

        if( getAD_User_ID() > 0 ) {
            MUser user = MUser.get( getCtx(),getAD_User_ID());

            sb.append( " (" ).append( user.getName()).append( ")" );
        }

        return sb.toString();
    }    // toStringX
}    // MWFActivity



/*
 *  @(#)MWFActivity.java   02.07.07
 * 
 *  Fin del fichero MWFActivity.java
 *  
 *  Versión 2.2
 *
 */
