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

import java.util.logging.Level;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorOXPSystem;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WorkflowMoveToClient extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int p_AD_Workflow_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Client_ID" )) {
                p_AD_Client_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_Workflow_ID" )) {
                p_AD_Workflow_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "doIt - AD_Client_ID=" + p_AD_Client_ID + ", AD_Workflow_ID=" + p_AD_Workflow_ID );

        int changes = 0;

        // WF

        String sql = "UPDATE AD_Workflow SET AD_Client_ID=" + p_AD_Client_ID + " WHERE AD_Client_ID=0 AND EntityType NOT IN ('D','C')" + " AND AD_Workflow_ID=" + p_AD_Workflow_ID;
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no == -1 ) {
            throw new ErrorOXPSystem( "Error updating Workflow" );
        }

        changes += no;

        // Node

        sql = "UPDATE AD_WF_Node SET AD_Client_ID=" + p_AD_Client_ID + " WHERE AD_Client_ID=0 AND EntityType NOT IN ('D','C')" + " AND AD_Workflow_ID=" + p_AD_Workflow_ID;
        no = DB.executeUpdate( sql,get_TrxName());

        if( no == -1 ) {
            throw new ErrorOXPSystem( "Error updating Workflow Node" );
        }

        changes += no;

        // Node Next

        sql = "UPDATE AD_WF_NodeNext SET AD_Client_ID=" + p_AD_Client_ID + " WHERE AD_Client_ID=0 AND EntityType NOT IN ('D','C')" + " AND (AD_WF_Node_ID IN (SELECT AD_WF_Node_ID FROM AD_WF_Node WHERE AD_Workflow_ID=" + p_AD_Workflow_ID + ") OR AD_WF_Next_ID IN (SELECT AD_WF_Node_ID FROM AD_WF_Node WHERE AD_Workflow_ID=" + p_AD_Workflow_ID + "))";
        no = DB.executeUpdate( sql,get_TrxName());

        if( no == -1 ) {
            throw new ErrorOXPSystem( "Error updating Workflow Transition" );
        }

        changes += no;

        // Node Parameters

        sql = "UPDATE AD_WF_Node_Para SET AD_Client_ID=" + p_AD_Client_ID + " WHERE AD_Client_ID=0 AND EntityType NOT IN ('D','C')" + " AND AD_WF_Node_ID IN (SELECT AD_WF_Node_ID FROM AD_WF_Node WHERE AD_Workflow_ID=" + p_AD_Workflow_ID + ")";
        no = DB.executeUpdate( sql,get_TrxName());

        if( no == -1 ) {
            throw new ErrorOXPSystem( "Error updating Workflow Node Parameters" );
        }

        changes += no;

        // Node Next Condition

        sql = "UPDATE AD_WF_NextCondition SET AD_Client_ID=" + p_AD_Client_ID + " WHERE AD_Client_ID=0 AND EntityType NOT IN ('D','C')" + " AND AD_WF_NodeNext_ID IN (" + "SELECT AD_WF_NodeNext_ID FROM AD_WF_NodeNext " + "WHERE AD_WF_Node_ID IN (SELECT AD_WF_Node_ID FROM AD_WF_Node WHERE AD_Workflow_ID=" + p_AD_Workflow_ID + ") OR AD_WF_Next_ID IN (SELECT AD_WF_Node_ID FROM AD_WF_Node WHERE AD_Workflow_ID=" + p_AD_Workflow_ID + "))";
        no = DB.executeUpdate( sql,get_TrxName());

        if( no == -1 ) {
            throw new ErrorOXPSystem( "Error updating Workflow Transition Condition" );
        }

        changes += no;

        return "@Updated@ - #" + changes;
    }    // doIt
}    // WorkflowMoveToClient



/*
 *  @(#)WorkflowMoveToClient.java   02.07.07
 * 
 *  Fin del fichero WorkflowMoveToClient.java
 *  
 *  Versión 2.2
 *
 */
