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

import java.awt.Point;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.M_Column;
import org.openXpertya.model.TranslationTable;
import org.openXpertya.model.X_AD_WF_Node;
import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWFNode extends X_AD_WF_Node {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public static MWFNode get( Properties ctx,int AD_WF_Node_ID ) {
        Integer key      = new Integer( AD_WF_Node_ID );
        MWFNode retValue = ( MWFNode )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MWFNode( ctx,AD_WF_Node_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "AD_WF_Node",50 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_WF_Node_ID
     * @param trxName
     */

    public MWFNode( Properties ctx,int AD_WF_Node_ID,String trxName ) {
        super( ctx,AD_WF_Node_ID,trxName );

        if( AD_WF_Node_ID == 0 ) {

            // setAD_WF_Node_ID (0);
            // setAD_Workflow_ID (0);
            // setValue (null);
            // setName (null);

            setAction( ACTION_WaitSleep );
            setCost( Env.ZERO );
            setDuration( 0 );
            setEntityType( ENTITYTYPE_UserMaintained );    // U
            setIsCentrallyMaintained( true );              // Y
            setJoinElement( JOINELEMENT_XOR );             // X

            // begin vpj-cd e-evolution PostgreSQL
            // setLimit (0);

            setDurationLimit( 0 );

            // end vpj-cd e-evolution PostgreSQL

            setSplitElement( SPLITELEMENT_XOR );    // X
            setWaitingTime( 0 );
            setXPosition( 0 );
            setYPosition( 0 );
        }

        // Save to Cache

        if( getID() != 0 ) {
            s_cache.put( new Integer( getAD_WF_Node_ID()),this );
        }
    }    // MWFNode

    /**
     * Constructor de la clase ...
     *
     *
     * @param wf
     * @param Value
     * @param Name
     */

    public MWFNode( MWorkflow wf,String Value,String Name ) {
        this( wf.getCtx(),0,wf.get_TrxName());
        setClientOrg( wf );
        setAD_Workflow_ID( wf.getAD_Workflow_ID());
        setValue( Value );
        setName( Name );
        m_durationBaseMS = wf.getDurationBaseSec() * 1000;
    }    // MWFNode

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWFNode( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
        loadNext();
        loadTrl();

        // Save to Cache

        s_cache.put( new Integer( getAD_WF_Node_ID()),this );
    }    // MWFNode

    /** Descripción de Campos */

    private ArrayList m_next = new ArrayList();

    /** Descripción de Campos */

    private String m_name_trl = null;

    /** Descripción de Campos */

    private String m_description_trl = null;

    /** Descripción de Campos */

    private String m_help_trl = null;

    /** Descripción de Campos */

    private boolean m_translated = false;

    /** Descripción de Campos */

    private M_Column m_column = null;

    /** Descripción de Campos */

    private MWFNodePara[] m_paras = null;

    /** Descripción de Campos */

    private long m_durationBaseMS = -1;

    /**
     * Descripción de Método
     *
     */

    private void loadNext() {
        String sql = "SELECT * FROM AD_WF_NodeNext WHERE AD_WF_Node_ID=? AND IsActive='Y' ORDER BY SeqNo";
        boolean splitAnd = SPLITELEMENT_AND.equals( getSplitElement());

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,getID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MWFNodeNext next = new MWFNodeNext( getCtx(),rs,get_TrxName());

                next.setFromSplitAnd( splitAnd );
                m_next.add( next );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadNext",e );
        }

        log.fine( "loadNext #" + m_next.size());
    }    // loadNext

    /**
     * Descripción de Método
     *
     */

    private void loadTrl() {
        if( Env.isBaseLanguage( getCtx(),"AD_Workflow" ) || (getID() == 0) ) {
            return;
        }

        String sql = "SELECT Name, Description, Help FROM AD_WF_Node_Trl WHERE AD_WF_Node_ID=? AND AD_Language=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

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
            log.log( Level.SEVERE,"loadTrl",e );
        }

        log.fine( "loadTrl " + m_translated );
    }    // loadTrl

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNextNodeCount() {
        return m_next.size();
    }    // getNextNodeCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MWFNodeNext[] getTransitions() {
        MWFNodeNext[] retValue = new MWFNodeNext[ m_next.size()];

        m_next.toArray( retValue );

        return retValue;
    }    // getNextNodes

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
     * @param position
     */

    public void setPosition( Point position ) {
        setPosition( position.x,position.y );
    }    // setPosition

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     */

    public void setPosition( int x,int y ) {
        setXPosition( x );
        setYPosition( y );
    }    // setPosition

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Point getPosition() {
        return new Point( getXPosition(),getYPosition());
    }    // getPosition

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getActionInfo() {
        String action = getAction();

        if( ACTION_AppsProcess.equals( action )) {
            return "Process:AD_Process_ID=" + getAD_Process_ID();
        } else if( ACTION_DocumentAction.equals( action )) {
            return "DocumentAction=" + getDocAction();
        } else if( ACTION_AppsReport.equals( action )) {
            return "Report:AD_Process_ID=" + getAD_Process_ID();
        } else if( ACTION_AppsTask.equals( action )) {
            return "Task:AD_Task_ID=" + getAD_Task_ID();
        } else if( ACTION_SetVariable.equals( action )) {
            return "SetVariable:AD_Column_ID=" + getAD_Column_ID();
        } else if( ACTION_SubWorkflow.equals( action )) {
            return "Workflow:AD_Workflow_ID=" + getAD_Workflow_ID();
        } else if( ACTION_UserChoice.equals( action )) {
            return "UserChoice:AD_Column_ID=" + getAD_Column_ID();
        } else if( ACTION_UserWorkbench.equals( action )) {
            return "Workbench:?";
        } else if( ACTION_UserForm.equals( action )) {
            return "Form:AD_Form_ID=" + getAD_Form_ID();
        } else if( ACTION_UserWindow.equals( action )) {
            return "Window:AD_Window_ID=" + getAD_Window_ID();
        } else if( ACTION_WaitSleep.equals( action )) {
            return "Sleep:WaitTime=" + getWaitTime();
        }

        return "??";
    }    // getActionInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getAttributeName() {
        if( getAD_Column_ID() == 0 ) {
            return super.getAttributeName();
        }

        // We have a column

        String attribute = super.getAttributeName();

        if( (attribute != null) && (attribute.length() > 0) ) {
            return attribute;
        }

        setAttributeName( getColumn().getColumnName());

        return super.getAttributeName();
    }    // getAttributeName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public M_Column getColumn() {
        if( getAD_Column_ID() == 0 ) {
            return null;
        }

        if( m_column == null ) {
            m_column = M_Column.get( getCtx(),getAD_Column_ID());
        }

        return m_column;
    }    // getColumn

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUserApproval() {
        if( !ACTION_UserChoice.equals( getAction())) {
            return false;
        }

        return (getColumn() != null) && "IsApproved".equals( getColumn().getColumnName());
    }    // isApproval

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUserChoice() {
        return ACTION_UserChoice.equals( getAction());
    }    // isUserChoice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUserManual() {
        if( ACTION_UserForm.equals( getAction()) || ACTION_UserWindow.equals( getAction()) || ACTION_UserWorkbench.equals( getAction())) {
            return true;
        }

        return false;
    }    // isUserManual

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public long getDurationMS() {
        long duration = super.getDuration();

        if( duration == 0 ) {
            return 0;
        }

        if( m_durationBaseMS == -1 ) {
            m_durationBaseMS = getWorkflow().getDurationBaseSec() * 1000;
        }

        return duration * m_durationBaseMS;
    }    // getDurationMS

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public long getLimitMS() {

        // begin vpj-cd  e-evolution PostgreSQL
        // long limit = super.getLimit ();

        long limit = super.getDurationLimit();

        // end vpj-cd e-evolution PostgreSQL

        if( limit == 0 ) {
            return 0;
        }

        if( m_durationBaseMS == -1 ) {
            m_durationBaseMS = getWorkflow().getDurationBaseSec() * 1000;
        }

        return limit * m_durationBaseMS;
    }    // getLimitMS

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDurationCalendarField() {
        return getWorkflow().getDurationCalendarField();
    }    // getDirationCalendarField

    /**
     * Descripción de Método
     *
     *
     * @param seconds
     *
     * @return
     */

    public int calculateDynamicPriority( int seconds ) {
        if( (seconds == 0) || (getDynPriorityUnit() == null) || (getDynPriorityChange() == null) || (Env.ZERO.compareTo( getDynPriorityChange()) == 0) ) {
            return 0;
        }

        //

        BigDecimal divide = Env.ZERO;

        if( DYNPRIORITYUNIT_Minute.equals( getDynPriorityUnit())) {
            divide = new BigDecimal( 60 );
        } else if( DYNPRIORITYUNIT_Hour.equals( getDynPriorityUnit())) {
            divide = new BigDecimal( 3600 );
        } else if( DYNPRIORITYUNIT_Day.equals( getDynPriorityUnit())) {
            divide = new BigDecimal( 86400 );
        } else {
            return 0;
        }

        //

        BigDecimal change = new BigDecimal( seconds ).divide( divide,BigDecimal.ROUND_DOWN ).multiply( getDynPriorityChange());

        return change.intValue();
    }    // calculateDynamicPriority

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MWFNodePara[] getParameters() {
        if( m_paras == null ) {
            m_paras = MWFNodePara.getParameters( getCtx(),getAD_WF_Node_ID());
        }

        return m_paras;
    }    // getParameters

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MWorkflow getWorkflow() {
        return MWorkflow.get( getCtx(),getAD_Workflow_ID());
    }    // getWorkflow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MWFNode[" );

        sb.append( getID()).append( "-" ).append( getName()).append( ",Action=" ).append( getActionInfo()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toStringX() {
        StringBuffer sb = new StringBuffer( "MWFNode[" );

        sb.append( getName()).append( "-" ).append( getActionInfo());

        return sb.toString();
    }    // toStringX

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
        if( !success ) {
            return success;
        }

        TranslationTable.save( this,newRecord );

        return true;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( !TranslationTable.isActiveLanguages( false )) {
            return true;
        }

        TranslationTable.delete( this );

        return true;
    }    // afterDelete
}    // M_WFNext



/*
 *  @(#)MWFNode.java   02.07.07
 * 
 *  Fin del fichero MWFNode.java
 *  
 *  Versión 2.2
 *
 */
