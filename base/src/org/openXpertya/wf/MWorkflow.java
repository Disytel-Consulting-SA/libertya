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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MMenu;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MStorage;
import org.openXpertya.model.M_Window;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_WF_Node;
import org.openXpertya.model.X_AD_WF_Node_Trl;
import org.openXpertya.model.X_AD_Workflow;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.StateEngine;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWorkflow extends X_AD_Workflow {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Workflow_ID
     *
     * @return
     */

    public static MWorkflow get( Properties ctx,int AD_Workflow_ID ) {
        Integer   key      = new Integer( AD_Workflow_ID );
        MWorkflow retValue = ( MWorkflow )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MWorkflow( ctx,AD_Workflow_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param AD_Table_ID
     *
     * @return
     */

    private static final String DOCVALUE_WORKFLOW = "DV_WF";
    private static HashMap<String, MWorkflow[]> wf_cache = null;
    
    public static MWorkflow[] getDocValue( Properties ctx,int AD_Client_ID,int AD_Table_ID ) {
        String key = "C" + AD_Client_ID + "T" + AD_Table_ID;

        // Reload

        if( s_cacheDocValue.isReset()) {
        	
        	// En caso de haber consultado una vez a bbdd, ya alcanza para no tener que invalidar la cache
        	if (wf_cache == null)
        		wf_cache = new HashMap<String, MWorkflow[]>();
        	else
        		return wf_cache.get(DOCVALUE_WORKFLOW);
        	
            String sql = "SELECT * FROM AD_Workflow " + "WHERE WorkflowType='V' AND IsActive='Y' AND IsValid='Y' " + "ORDER BY AD_Client_ID, AD_Table_ID";
            ArrayList         list   = new ArrayList();
            String            oldKey = "";
            String            newKey = null;
            PreparedStatement pstmt  = null;

            try {
                pstmt = DB.prepareStatement( sql );

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    MWorkflow wf = new MWorkflow( ctx,rs,null );

                    newKey = "C" + wf.getAD_Client_ID() + "T" + wf.getAD_Table_ID();

                    if( !newKey.equals( oldKey ) && (list.size() > 0) ) {
                        MWorkflow[] wfs = new MWorkflow[ list.size()];

                        list.toArray( wfs );
                        s_cacheDocValue.put( oldKey,wfs );
                        list = new ArrayList();
                    }

                    oldKey = newKey;
                    list.add( wf );
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

            // Last one

            if( list.size() > 0 ) {
                MWorkflow[] wfs = new MWorkflow[ list.size()];

                list.toArray( wfs );
                s_cacheDocValue.put( oldKey,wfs );
            }

            s_log.config( "#" + s_cacheDocValue.size());
        }

        // Look for Entry

        MWorkflow[] retValue = ( MWorkflow[] )s_cacheDocValue.get( key );

        wf_cache.put(DOCVALUE_WORKFLOW, retValue);
        
        return retValue;
    }    // getDocValue

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "AD_Workflow",20 );

    /** Descripción de Campos */

    private static CCache s_cacheDocValue = new CCache( "AD_Workflow",5 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MWorkflow.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Workflow_ID
     * @param trxName
     */

    public MWorkflow( Properties ctx,int AD_Workflow_ID,String trxName ) {
        super( ctx,AD_Workflow_ID,trxName );

        if( AD_Workflow_ID == 0 ) {

            // setAD_Workflow_ID (0);
            // setValue (null);
            // setName (null);

            setAccessLevel( ACCESSLEVEL_Organization );
            setAuthor( "FUNDESLE" );
            setDurationUnit( DURATIONUNIT_Day );
            setDuration( 1 );
            setEntityType( ENTITYTYPE_UserMaintained );         // U
            setIsDefault( false );
            setPublishStatus( PUBLISHSTATUS_UnderRevision );    // U
            setVersion( 0 );
            setCost( 0 );
            setWaitingTime( 0 );
            setWorkingTime( 0 );
        }

        loadTrl();
        loadNodes();
    }    // MWorkflow

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWorkflow( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // Workflow

    /** Descripción de Campos */

    private ArrayList m_nodes = new ArrayList();

    /** Descripción de Campos */

    private MWFNode[] m_nodeArray = null;

    /** Descripción de Campos */

    private String m_name_trl = null;

    /** Descripción de Campos */

    private String m_description_trl = null;

    /** Descripción de Campos */

    private String m_help_trl = null;

    /** Descripción de Campos */

    private boolean m_translated = false;

    /**
     * Descripción de Método
     *
     */

    private void loadTrl() {
        if( Env.isBaseLanguage( getCtx(),"AD_Workflow" ) || (getID() == 0) ) {
            return;
        }

        String sql = "SELECT Name, Description, Help FROM AD_Workflow_Trl WHERE AD_Workflow_ID=? AND AD_Language=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,getID());
            pstmt.setString( 2,Env.getAD_Language( getCtx()));

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_name_trl        = rs.getString( 1 );
                m_description_trl = rs.getString( 2 );
                m_help_trl        = rs.getString( 3 );
                m_translated      = true;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MWorkflow.loadTrl",e );
        }

        log.fine( "loadTrl " + m_translated );
    }    // loadTrl

    /**
     * Descripción de Método
     *
     */

    private void loadNodes() {
        String sql = "SELECT * FROM AD_WF_Node WHERE AD_WorkFlow_ID=? AND IsActive='Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,getID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                m_nodes.add( new MWFNode( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MWorkflow.loadNodes",e );
        }

        log.fine( "loadNodes #" + m_nodes.size());
    }    // loadNodes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNodeCount() {
        return m_nodes.size();
    }    // getNextNodeCount

    /**
     * Descripción de Método
     *
     *
     * @param ordered
     *
     * @return
     */

    public MWFNode[] getNodes( boolean ordered ) {
        if( ordered ) {
            return getNodesInOrder();
        }

        //

        MWFNode[] retValue = new MWFNode[ m_nodes.size()];

        m_nodes.toArray( retValue );

        return retValue;
    }    // getNodes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MWFNode getFirstNode() {
        return getNode( getAD_WF_Node_ID());
    }    // getFirstNode

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    protected MWFNode getNode( int AD_WF_Node_ID ) {
        for( int i = 0;i < m_nodes.size();i++ ) {
            MWFNode node = ( MWFNode )m_nodes.get( i );

            if( node.getAD_WF_Node_ID() == AD_WF_Node_ID ) {
                return node;
            }
        }

        return null;
    }    // getNode

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public MWFNode[] getNextNodes( int AD_WF_Node_ID ) {
        MWFNode node = getNode( AD_WF_Node_ID );

        if( (node == null) || (node.getNextNodeCount() == 0) ) {
            return null;
        }

        //

        MWFNodeNext[] nexts = node.getTransitions();
        ArrayList     list  = new ArrayList();

        for( int i = 0;i < nexts.length;i++ ) {
            MWFNode next = getNode( nexts[ i ].getAD_WF_Next_ID());

            if( next != null ) {
                list.add( next );
            }
        }

        // Return Nodes

        MWFNode[] retValue = new MWFNode[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getNextNodes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MWFNode[] getNodesInOrder() {
        if( m_nodeArray == null ) {
            ArrayList list = new ArrayList();

            addNodes( list,getAD_WF_Node_ID());    // start with first

            if( m_nodes.size() != list.size()) {
                log.log( Level.SEVERE,"MWorkflow.getNodesInOrder - sizes different: " + m_nodes.size() + "<->" + list.size());
            }

            //

            m_nodeArray = new MWFNode[ list.size()];
            list.toArray( m_nodeArray );
        }

        return m_nodeArray;
    }    // getNodesInOrder

    /**
     * Descripción de Método
     *
     *
     * @param list
     * @param AD_WF_Node_ID
     */

    private void addNodes( ArrayList list,int AD_WF_Node_ID ) {
        MWFNode node = getNode( AD_WF_Node_ID );

        if( (node != null) &&!list.contains( node )) {
            list.add( node );

            MWFNodeNext[] nexts = node.getTransitions();

            for( int i = 0;i < nexts.length;i++ ) {
                addNodes( list,nexts[ i ].getAD_WF_Next_ID());
            }
        }
    }    // addNode

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public int getNext( int AD_WF_Node_ID ) {
        MWFNode[] nodes = getNodesInOrder();

        for( int i = 0;i < nodes.length;i++ ) {
            if( nodes[ i ].getAD_WF_Node_ID() == AD_WF_Node_ID ) {
                MWFNodeNext[] nexts = nodes[ i ].getTransitions();

                if( nexts.length > 0 ) {
                    return nexts[ 0 ].getAD_WF_Next_ID();
                }

                return 0;
            }
        }

        return 0;
    }    // getNext

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public MWFNodeNext[] getNodeNexts( int AD_WF_Node_ID ) {
        MWFNode[] nodes = getNodesInOrder();

        for( int i = 0;i < nodes.length;i++ ) {
            if( nodes[ i ].getAD_WF_Node_ID() == AD_WF_Node_ID ) {
                return nodes[ i ].getTransitions();
            }
        }

        return null;
    }    // getNext

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public int getPrevious( int AD_WF_Node_ID ) {
        MWFNode[] nodes = getNodesInOrder();

        for( int i = 0;i < nodes.length;i++ ) {
            if( nodes[ i ].getAD_WF_Node_ID() == AD_WF_Node_ID ) {
                if( i > 0 ) {
                    return nodes[ i - 1 ].getAD_WF_Node_ID();
                }

                return 0;
            }
        }

        return 0;
    }    // getPrevious

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public int getLast( int AD_WF_Node_ID ) {
        MWFNode[] nodes = getNodesInOrder();

        if( nodes.length > 0 ) {
            return nodes[ nodes.length - 1 ].getAD_WF_Node_ID();
        }

        return 0;
    }    // getLast

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public boolean isFirst( int AD_WF_Node_ID ) {
        return AD_WF_Node_ID == getAD_WF_Node_ID();
    }    // isFirst

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public boolean isLast( int AD_WF_Node_ID ) {
        MWFNode[] nodes = getNodesInOrder();

        return AD_WF_Node_ID == nodes[ nodes.length - 1 ].getAD_WF_Node_ID();
    }    // isLast

    /**
     * Descripción de Método
     *
     *
     * @param translated
     *
     * @return
     */

    public String getName( boolean translated ) {
        if( translated && m_translated ) {
            return m_name_trl;
        }

        return getName();
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @param translated
     *
     * @return
     */

    public String getDescription( boolean translated ) {
        if( translated && m_translated ) {
            return m_description_trl;
        }

        return getDescription();
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @param translated
     *
     * @return
     */

    public String getHelp( boolean translated ) {
        if( translated && m_translated ) {
            return m_help_trl;
        }

        return getHelp();
    }    // getHelp

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        validate();

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        log.fine( "afterSave - Success=" + success );

        if( success && newRecord ) {

            // save all nodes -- Creating new Workflow

            MWFNode[] nodes = getNodesInOrder();

            for( int i = 0;i < nodes.length;i++ ) {
                nodes[ i ].save( get_TrxName());
            }
        }

        if( newRecord ) {
            int             AD_Role_ID = Env.getAD_Role_ID( getCtx());
            MWorkflowAccess wa         = new MWorkflowAccess( this,AD_Role_ID );

            wa.save();
        }

        // Menu/Workflow

        else if( is_ValueChanged( "IsActive" ) || is_ValueChanged( "Name" ) || is_ValueChanged( "Description" ) || is_ValueChanged( "Help" )) {
            MMenu[] menues = MMenu.get( getCtx(),"AD_Workflow_ID=" + getAD_Workflow_ID());

            for( int i = 0;i < menues.length;i++ ) {
                menues[ i ].setIsActive( isActive());
                menues[ i ].setName( getName());
                menues[ i ].setDescription( getDescription());
                menues[ i ].save();
            }

            X_AD_WF_Node[] nodes = M_Window.getWFNodes( getCtx(),"AD_Workflow_ID=" + getAD_Workflow_ID());

            for( int i = 0;i < nodes.length;i++ ) {
                boolean changed = false;

                if( nodes[ i ].isActive() != isActive()) {
                    nodes[ i ].setIsActive( isActive());
                    changed = true;
                }

                if( nodes[ i ].isCentrallyMaintained()) {
                    nodes[ i ].setName( getName());
                    nodes[ i ].setDescription( getDescription());
                    nodes[ i ].setHelp( getHelp());
                    changed = true;
                }

                if( changed ) {
                    nodes[ i ].save();
                }
            }
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param pi
     *
     * @return
     */

    public MWFProcess start( ProcessInfo pi ) {
        MWFProcess retValue = null;

        try {
            retValue = new MWFProcess( this,pi );
            retValue.save();
            retValue.startWork();
        } catch( Exception e ) {
            log.log( Level.SEVERE,e.getLocalizedMessage(),e );
            pi.setSummary( e.getMessage(),true );
            retValue = null;
        }

        return retValue;
    }    // MWFProcess

    /**
     * Descripción de Método
     *
     *
     * @param pi
     *
     * @return
     */

    public MWFProcess startWait( ProcessInfo pi ) {
    	// Tiempo para esperar que el proceso finalice antes de separarse del worker (0 = sin limite)
    	int WAIT_FOR_WORKER_SECONDS = 60;	// por defecto, limite de 60 segundos
    	try {	
    		// Buscar la preferencia a nivel sistema
    		WAIT_FOR_WORKER_SECONDS = Integer.parseInt(MPreference.GetCustomPreferenceValue("WAIT_FOR_WORKER_SECONDS", null, null, null, false));
    	} catch (Exception e) {	/* Sin preferencia */  	}
    	// 1/2 sec. Intervalo entre evaluación y evaluación
        final int SLEEP_MS    = 500;    									
        // loops hasta interrumpir la espera
        final int MAXLOOPS = WAIT_FOR_WORKER_SECONDS * (1000 / SLEEP_MS);  

        //

        MWFProcess process = start( pi );

        if( process == null ) {
            return null;
        }

        Thread.yield();

        StateEngine state = process.getState();
        int         loops = 0;

        while( !state.isClosed() &&!state.isSuspended()) {
            if( MAXLOOPS > 0 && loops > MAXLOOPS ) {
                log.warning( "Timeout after sec " + (( SLEEP_MS * MAXLOOPS ) / 1000 ));
                pi.setSummary( Msg.parseTranslation(Env.getCtx(), "@StillRunning@") );

                return process;
            }

            // System.out.println("--------------- " + loops + ": " + state);

            try {
                Thread.sleep( SLEEP_MS );
                loops++;
            } catch( InterruptedException e ) {
                log.log( Level.SEVERE,"startWait interrupted",e );
                pi.setSummary( Msg.parseTranslation(Env.getCtx(), "@Interrupted@") );

                return process;
            }

            Thread.yield();
            state = process.getState();
        }

        String summary = process.getProcessMsg();

        if( (summary == null) || (summary.trim().length() == 0) ) {
            summary = Msg.translate(Env.getCtx(), state.toString());
        }

        pi.setSummary( summary,state.isTerminated() || state.isAborted());
        log.fine( summary );

        return process;
    }    // startWait

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public long getDurationBaseSec() {
        if( getDurationUnit() == null ) {
            return 0;
        } else if( DURATIONUNIT_Second.equals( getDurationUnit())) {
            return 1;
        } else if( DURATIONUNIT_Minute.equals( getDurationUnit())) {
            return 60;
        } else if( DURATIONUNIT_Hour.equals( getDurationUnit())) {
            return 3600;
        } else if( DURATIONUNIT_Day.equals( getDurationUnit())) {
            return 86400;
        } else if( DURATIONUNIT_Month.equals( getDurationUnit())) {
            return 2592000;
        } else if( DURATIONUNIT_Year.equals( getDurationUnit())) {
            return 31536000;
        }

        return 0;
    }    // getDurationBaseSec

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDurationCalendarField() {
        if( getDurationUnit() == null ) {
            return Calendar.MINUTE;
        } else if( DURATIONUNIT_Second.equals( getDurationUnit())) {
            return Calendar.SECOND;
        } else if( DURATIONUNIT_Minute.equals( getDurationUnit())) {
            return Calendar.MINUTE;
        } else if( DURATIONUNIT_Hour.equals( getDurationUnit())) {
            return Calendar.HOUR;
        } else if( DURATIONUNIT_Day.equals( getDurationUnit())) {
            return Calendar.DAY_OF_YEAR;
        } else if( DURATIONUNIT_Month.equals( getDurationUnit())) {
            return Calendar.MONTH;
        } else if( DURATIONUNIT_Year.equals( getDurationUnit())) {
            return Calendar.YEAR;
        }

        return Calendar.MINUTE;
    }    // getDurationCalendarField

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String validate() {
        StringBuffer errors = new StringBuffer();

        //

        if( getAD_WF_Node_ID() == 0 ) {
            errors.append( " - No Start Node" );
        }

        //

        if( WORKFLOWTYPE_DocumentValue.equals( getWorkflowType()) && ( (getDocValueLogic() == null) || (getDocValueLogic().length() == 0) ) ) {
            errors.append( " - No Document Value Logic" );
        }

        //

        // final

        boolean valid = errors.length() == 0;

        setIsValid( valid );

        if( !valid ) {
            log.info( "validate: " + errors );
        }

        return errors.toString();
    }    // validate

//      public boolean  

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startup( true );

        // Create Standard Document Process

        MWorkflow wf = new MWorkflow( Env.getCtx(),0,null );

        wf.setValue( "Process_xx" );
        wf.setName( wf.getValue());
        wf.setDescription( "(Standard " + wf.getValue());
        wf.setEntityType( ENTITYTYPE_Dictionary );
        wf.save();

        //

        MWFNode node10 = new MWFNode( wf,"10","(Start)" );

        node10.setDescription( "(Standard Node)" );
        node10.setEntityType( ENTITYTYPE_Dictionary );
        node10.setAction( MWFNode.ACTION_WaitSleep );
        node10.setWaitTime( 0 );
        node10.setPosition( 5,5 );
        node10.save();
        wf.setAD_WF_Node_ID( node10.getAD_WF_Node_ID());
        wf.save();

        MWFNode node20 = new MWFNode( wf,"20","(DocAuto)" );

        node20.setDescription( "(Standard Node)" );
        node20.setEntityType( ENTITYTYPE_Dictionary );
        node20.setAction( MWFNode.ACTION_DocumentAction );
        node20.setDocAction( MWFNode.DOCACTION_None );
        node20.setPosition( 5,120 );
        node20.save();

        MWFNodeNext tr10_20 = new MWFNodeNext( Env.getCtx(),node10.getAD_WF_Node_ID(),node20.getAD_WF_Node_ID(),100 );

        tr10_20.setEntityType( ENTITYTYPE_Dictionary );
        tr10_20.setDescription( "(Standard unconditional Transition)" );
        tr10_20.save();

        MWFNode node100 = new MWFNode( wf,"100","(DocPrepare)" );

        node100.setDescription( "(Standard Node)" );
        node100.setEntityType( ENTITYTYPE_Dictionary );
        node100.setAction( MWFNode.ACTION_DocumentAction );
        node100.setDocAction( MWFNode.DOCACTION_Prepare );
        node100.setPosition( 170,5 );
        node100.save();

        MWFNodeNext tr10_100 = new MWFNodeNext( Env.getCtx(),node10.getAD_WF_Node_ID(),node100.getAD_WF_Node_ID(),10 );

        tr10_100.setEntityType( ENTITYTYPE_Dictionary );
        tr10_100.setDescription( "(Standard approval Transition)" );
        tr10_100.setIsStdUserWorkflow( true );
        tr10_100.save();

        MWFNode node200 = new MWFNode( wf,"200","(DocComplete)" );

        node200.setDescription( "(Standard Node)" );
        node200.setEntityType( ENTITYTYPE_Dictionary );
        node200.setAction( MWFNode.ACTION_DocumentAction );
        node200.setDocAction( MWFNode.DOCACTION_Complete );
        node200.setPosition( 170,120 );
        node200.save();

        MWFNodeNext tr100_200 = new MWFNodeNext( Env.getCtx(),node100.getAD_WF_Node_ID(),node200.getAD_WF_Node_ID(),100 );

        tr100_200.setEntityType( ENTITYTYPE_Dictionary );
        tr100_200.setDescription( "(Standard unconditional Transition)" );
        tr100_200.save();

        // wf.start(M_Requsition_ID);

    }    // main

	public int copyNodesFrom(MWorkflow otherWorkflow) throws Exception{
		
		if (otherWorkflow == null) {
			  throw new Exception( "Source Workflow is Null" );
		}
		// Recupera todos los nodos de otherWorkflow
		List<PO> fromNodes = PO.find(getCtx(), "ad_wf_node", "ad_workflow_id = ?", new Object[]{otherWorkflow.getAD_Workflow_ID()}, null, get_TrxName());
		
		// Guarda los ID del nodo original y el ID del nodo copia
		HashMap<Integer, Integer> nodesId = new HashMap<Integer, Integer>();
		
		int count = 0;
		// Realiza una copia de cada nodo del flujo de trabajo 
		for (PO po : fromNodes){

			MWFNode nodePO = (MWFNode) po;
			MWFNode node = new MWFNode(getCtx(), 0, get_TrxName());

			// Copia todos los datos del original a la copia
			PO.copyValues(nodePO, node, getAD_Client_ID(), getAD_Org_ID());
			
			// Seteo que es un nuevo nodo y l flujo al que pertenece
			node.setAD_Workflow_ID(getAD_Workflow_ID());
			node.setAD_WF_Node_ID(0); // new
			// Graba el nuevo nodo
			if (node.save()) {
				count++;
				nodesId.put(nodePO.getAD_WF_Node_ID(), node.getAD_WF_Node_ID()); // nodesId.put(original, copia);
				node.copyTranslation(nodePO);
			}// if
			
			// Setea el nodo inicial si el nodo recientemente copiado era el nodo
			// inicial del flujo de trabajo origen.
			if (otherWorkflow.getAD_WF_Node_ID() == nodePO.getAD_WF_Node_ID()) {
				setAD_WF_Node_ID(node.getAD_WF_Node_ID());
			}

		}// for
		
		// Para cada nodo creado anteriomente recupero sus nodos siguientes
		for (PO po : fromNodes){
			
			MWFNode nodePO = (MWFNode) po;
			
			List<PO> nextNodes = PO.find(getCtx(), "ad_wf_nodenext", "ad_wf_node_id = ?", new Object[]{nodePO.getAD_WF_Node_ID()}, new String[]{"seqno"}, get_TrxName());
			
			if (nextNodes != null){
			
				MWFNodeNext c;
				MWFNodeNext c_aux;

				// Itera sobre cada nodo siguiente haciendo una copia
				for (PO po2 : nextNodes) {
					c = (MWFNodeNext)po2;
					c_aux = new MWFNodeNext(getCtx(), 0, get_TrxName());
					
					PO.copyValues(c, c_aux, getAD_Client_ID(), getAD_Org_ID());
					// setea para el nuevo siguiente el ID del nodo nuevo y el ID de su siguiente
					c_aux.setAD_WF_Node_ID(nodesId.get(nodePO.getID()));
					c_aux.setAD_WF_Next_ID(nodesId.get(c.getAD_WF_Next_ID()));
					
					// Graba el nuevo siguiente
					if (!c_aux.save()) {
						log.log(Level.WARNING, "Error al insertar Next Node");
						}
				}// for
			}// if
		}// for
		
		if (fromNodes.size() != count) {
			log.log(Level.SEVERE, "copyNodesFrom - Node difference - From="
					+ fromNodes.size() + " <> Saved=" + count);
		}// if
			
		return count;
	}

}    // MWorkflow_ID



/*
 *  @(#)MWorkflow.java   02.07.07
 * 
 *  Fin del fichero MWorkflow.java
 *  
 *  Versión 2.2
 *
 */
