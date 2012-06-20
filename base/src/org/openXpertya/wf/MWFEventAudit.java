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

import org.openXpertya.model.X_AD_WF_EventAudit;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWFEventAudit extends X_AD_WF_EventAudit {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_WF_Process_ID
     * @param AD_WF_Node_ID
     *
     * @return
     */

    public static MWFEventAudit[] get( Properties ctx,int AD_WF_Process_ID,int AD_WF_Node_ID ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM AD_WF_EventAudit " + "WHERE AD_WF_Process_ID=?";

        if( AD_WF_Node_ID > 0 ) {
            sql += " AND AD_WF_Node_ID=?";
        }

        sql += " ORDER BY AD_WF_EventAudit_ID";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_WF_Process_ID );

            if( AD_WF_Node_ID > 0 ) {
                pstmt.setInt( 2,AD_WF_Node_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWFEventAudit( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        MWFEventAudit[] retValue = new MWFEventAudit[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_WF_Process_ID
     *
     * @return
     */

    public static MWFEventAudit[] get( Properties ctx,int AD_WF_Process_ID ) {
        return get( ctx,AD_WF_Process_ID,0 );
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MWFEventAudit.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_WF_EventAudit_ID
     * @param trxName
     */

    public MWFEventAudit( Properties ctx,int AD_WF_EventAudit_ID,String trxName ) {
        super( ctx,AD_WF_EventAudit_ID,trxName );
    }    // MWFEventAudit

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWFEventAudit( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWFEventAudit

    /**
     * Constructor de la clase ...
     *
     *
     * @param activity
     */

    public MWFEventAudit( MWFActivity activity ) {
        super( activity.getCtx(),0,activity.get_TrxName());
        setAD_WF_Process_ID( activity.getAD_WF_Process_ID());
        setAD_WF_Node_ID( activity.getAD_WF_Node_ID());
        setAD_Table_ID( activity.getAD_Table_ID());
        setRecord_ID( activity.getRecord_ID());

        //

        setAD_WF_Responsible_ID( activity.getAD_WF_Responsible_ID());
        setAD_User_ID( activity.getAD_User_ID());

        //

        setWFState( activity.getWFState());
        setEventType( EVENTTYPE_ProcessCreated );
        setElapsedTimeMS( Env.ZERO );

        //

        MWFNode node = activity.getNode();

        if( (node != null) && (node.getID() != 0) ) {
            String action = node.getAction();

            if( MWFNode.ACTION_SetVariable.equals( action ) || MWFNode.ACTION_UserChoice.equals( action )) {
                setAttributeName( node.getAttributeName());
                setOldValue( String.valueOf( activity.getAttributeValue()));

                if( MWFNode.ACTION_SetVariable.equals( action )) {
                    setNewValue( node.getAttributeValue());
                }
            }
        }
    }    // MWFEventAudit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getNodeName() {
        MWFNode node = MWFNode.get( getCtx(),getAD_WF_Node_ID());

        if( node.getID() == 0 ) {
            return "?";
        }

        return node.getName( true );
    }    // getNodeName
}    // MWFEventAudit



/*
 *  @(#)MWFEventAudit.java   02.07.07
 * 
 *  Fin del fichero MWFEventAudit.java
 *  
 *  Versión 2.2
 *
 */
