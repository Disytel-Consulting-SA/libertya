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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_WF_NodeNext;
import org.openXpertya.process.DocAction;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWFNodeNext extends X_AD_WF_NodeNext {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_WF_NodeNext_ID
     * @param trxName
     */

    public MWFNodeNext( Properties ctx,int AD_WF_NodeNext_ID,String trxName ) {
        super( ctx,AD_WF_NodeNext_ID,trxName );

        if( AD_WF_NodeNext_ID == 0 ) {

            // setAD_WF_Next_ID (0);
            // setAD_WF_Node_ID (0);

            setEntityType( ENTITYTYPE_UserMaintained );    // U
            setIsStdUserWorkflow( false );
            setSeqNo( 10 );                                // 10
        }
    }                                                      // MWFNodeNext

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWFNodeNext( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWFNodeNext

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_WF_Node_ID
     * @param AD_WF_Next_ID
     * @param SeqNo
     */

    public MWFNodeNext( Properties ctx,int AD_WF_Node_ID,int AD_WF_Next_ID,int SeqNo ) {
        super( ctx,0,null );
        setAD_WF_Node_ID( AD_WF_Node_ID );
        setAD_WF_Next_ID( AD_WF_Next_ID );

        //

        setEntityType( ENTITYTYPE_UserMaintained );    // U
        setIsStdUserWorkflow( false );
        setSeqNo( SeqNo );
        save();
    }                                                  // MWFNodeNext

    /** Descripción de Campos */

    private MWFNextCondition[] m_conditions = null;

    /** Descripción de Campos */

    public Boolean m_fromSplitAnd = null;

    /** Descripción de Campos */

    public Boolean m_toJoinAnd = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MWFNodeNext[" );

        sb.append( getSeqNo()).append( ":Node=" ).append( getAD_WF_Node_ID()).append( "->Next=" ).append( getAD_WF_Next_ID());

        if( m_conditions != null ) {
            sb.append( ",#" ).append( m_conditions.length );
        }

        if( (getDescription() != null) && (getDescription().length() > 0) ) {
            sb.append( "," ).append( getDescription());
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MWFNextCondition[] getConditions( boolean requery ) {
        if( !requery && (m_conditions != null) ) {
            return m_conditions;
        }

        //

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM AD_WF_NextCondition WHERE AD_WF_NodeNext_ID=? AND IsActive='Y' ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getAD_WF_NodeNext_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWFNextCondition( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getConditions",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_conditions = new MWFNextCondition[ list.size()];
        list.toArray( m_conditions );

        return m_conditions;
    }    // getConditions

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUnconditional() {
        return !isStdUserWorkflow() && (getConditions( false ).length == 0);
    }    // isUnconditional

    /**
     * Descripción de Método
     *
     *
     * @param activity
     *
     * @return
     */

    public boolean isValidFor( MWFActivity activity ) {
        if( isStdUserWorkflow()) {
            PO po = activity.getPO();

            if( po instanceof DocAction ) {
                DocAction da        = ( DocAction )po;
                String    docStatus = da.getDocStatus();
                String    docAction = da.getDocAction();

                if( !DocAction.ACTION_Complete.equals( docAction ) || DocAction.STATUS_Completed.equals( docStatus ) || DocAction.STATUS_WaitingConfirmation.equals( docStatus ) || DocAction.STATUS_WaitingPayment.equals( docStatus ) || DocAction.STATUS_Voided.equals( docStatus ) || DocAction.STATUS_Closed.equals( docStatus ) || DocAction.STATUS_Reversed.equals( docStatus ))

                /*
                 * || DocAction.ACTION_Complete.equals(docAction)
                 * || DocAction.ACTION_ReActivate.equals(docAction)
                 * || DocAction.ACTION_None.equals(docAction)
                 * || DocAction.ACTION_Post.equals(docAction)
                 * || DocAction.ACTION_Unlock.equals(docAction)
                 * || DocAction.ACTION_Invalidate.equals(docAction)        )
                 */

                {
                    log.fine( "isValidFor =NO= StdUserWF - Status=" + docStatus + " - Action=" + docAction );

                    return false;
                }
            }
        }

        // No Conditions

        if( getConditions( false ).length == 0 ) {
            log.fine( "isValidFor #0 " + toString());

            return true;
        }

        //

        boolean ok = true;

        for( int i = 0;i < m_conditions.length;i++ ) {

            // First condition always AND

            if( (i == 0) && m_conditions[ i ].isOr()) {
                m_conditions[ i ].setAndOr( MWFNextCondition.ANDOR_And );
            }

            // we have an OR condition

            if( m_conditions[ i ].isOr() && (i > 0) ) {

                // with existing True condition

                if( ok ) {
                    log.fine( "isValidFor #" + i + "(true) " + toString());

                    return true;
                }

                ok = true;    // reset
            }

            ok = m_conditions[ i ].evaluate( activity );
        }                     // for all conditions

        log.fine( "isValidFor (" + ok + ") " + toString());

        return ok;
    }    // isValidFor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFromSplitAnd() {
        if( m_fromSplitAnd != null ) {
            return m_fromSplitAnd.booleanValue();
        }

        return false;
    }    // getFromSplitAnd

    /**
     * Descripción de Método
     *
     *
     * @param fromSplitAnd
     */

    public void setFromSplitAnd( boolean fromSplitAnd ) {
        m_fromSplitAnd = new Boolean( fromSplitAnd );
    }    // setFromSplitAnd

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isToJoinAnd() {
        if( (m_toJoinAnd == null) && (getAD_WF_Next_ID() != 0) ) {
            MWFNode next = MWFNode.get( getCtx(),getAD_WF_Next_ID());

            setToJoinAnd( MWFNode.JOINELEMENT_AND.equals( next.getJoinElement()));
        }

        if( m_toJoinAnd != null ) {
            return m_toJoinAnd.booleanValue();
        }

        return false;
    }    // getToJoinAnd

    /**
     * Descripción de Método
     *
     *
     * @param toJoinAnd
     */

    private void setToJoinAnd( boolean toJoinAnd ) {
        m_toJoinAnd = new Boolean( toJoinAnd );
    }    // setToJoinAnd
}    // MWFNodeNext



/*
 *  @(#)MWFNodeNext.java   02.07.07
 * 
 *  Fin del fichero MWFNodeNext.java
 *  
 *  Versión 2.2
 *
 */
