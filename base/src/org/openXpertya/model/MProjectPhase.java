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



package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProjectPhase extends X_C_ProjectPhase {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_ProjectPhase_ID
     * @param trxName
     */

    public MProjectPhase( Properties ctx,int C_ProjectPhase_ID,String trxName ) {
        super( ctx,C_ProjectPhase_ID,trxName );

        if( C_ProjectPhase_ID == 0 ) {

            // setC_ProjectPhase_ID (0);       //      PK
            // setC_Project_ID (0);            //      Parent
            // setC_Phase_ID (0);                      //      FK

            setCommittedAmt( Env.ZERO );
            setIsCommitCeiling( false );
            setIsComplete( false );
            setSeqNo( 0 );

            // setName (null);

            setQty( Env.ZERO );
        }
    }    // MProjectPhase

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProjectPhase( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProjectPhase

    /**
     * Constructor de la clase ...
     *
     *
     * @param project
     */

    public MProjectPhase( MProject project ) {
        this( project.getCtx(),0,project.get_TrxName());
        setClientOrg( project );
        setC_Project_ID( project.getC_Project_ID());
    }    // MProjectPhase

    /**
     * Constructor de la clase ...
     *
     *
     * @param project
     * @param phase
     */

    public MProjectPhase( MProject project,MProjectTypePhase phase ) {
        this( project );

        //

        setC_Phase_ID( phase.getC_Phase_ID());    // FK
        setName( phase.getName());
        setSeqNo( phase.getSeqNo());
        setDescription( phase.getDescription());
        setHelp( phase.getHelp());

        if( phase.getM_Product_ID() != 0 ) {
            setM_Product_ID( phase.getM_Product_ID());
        }

        setQty( phase.getStandardQty());
    }    // MProjectPhase

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProjectTask[] getTasks(int m_C_DocType_ID) {
        ArrayList list = new ArrayList();
        //Comprobacion para saber si se ha creado ya ese tipo de documento.
        //Se mira en la tabla de m_project_document, y se cojen las tareas que para esa fase aun no esten generadas.
        String    sql  = "SELECT * FROM C_ProjectTask WHERE C_ProjectPhase_ID=? and c_projecttask_id not in (select c_projecttask_id from c_project_document where  c_doctype_id="+ m_C_DocType_ID+") ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_ProjectPhase_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProjectTask( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getTasks",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectTask[] retValue = new MProjectTask[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getTasks
    public MProjectTask[] getTasks() {
        ArrayList list = new ArrayList();
        //Comprobacion para saber si se ha creado ya ese tipo de documento.
        //Se mira en la tabla de m_project_document, y se cojen las tareas que para esa fase aun no esten generadas.
        String    sql  = "SELECT * FROM C_ProjectTask WHERE C_ProjectPhase_ID=? and c_projecttask_id not in (select c_projecttask_id from c_project_document ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_ProjectPhase_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProjectTask( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getTasks",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectTask[] retValue = new MProjectTask[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getTasks

    /**
     * Descripción de Método
     *
     *
     * @param fromPhase
     *
     * @return
     */

    public int copyTasksFrom( MProjectPhase fromPhase ) {
        if( fromPhase == null ) {
            return 0;
        }

        int count = 0;

        //

        MProjectTask[] myTasks   = getTasks();
        MProjectTask[] fromTasks = fromPhase.getTasks();

        // Copy Project Tasks

        for( int i = 0;i < fromTasks.length;i++ ) {

            // Check if Task already exists

            int     C_Task_ID = fromTasks[ i ].getC_Task_ID();
            boolean exists    = false;

            if( C_Task_ID == 0 ) {
                exists = false;
            } else {
                for( int ii = 0;ii < myTasks.length;ii++ ) {
                    if( myTasks[ ii ].getC_Task_ID() == C_Task_ID ) {
                        exists = true;

                        break;
                    }
                }
            }

            // Phase exist

            if( exists ) {
                log.info( "copyTasksFrom - Task already exists here, ignored - " + fromTasks[ i ] );
            } else {
                MProjectTask toTask = new MProjectTask( getCtx(),0,get_TrxName());

                PO.copyValues( fromTasks[ i ],toTask,getAD_Client_ID(),getAD_Org_ID());
                toTask.setC_ProjectPhase_ID( getC_ProjectPhase_ID());

                if( toTask.save()) {
                    count++;
                }
            }
        }

        if( fromTasks.length != count ) {
            log.warning( "copyTasksFrom - Count difference - ProjectPhase=" + fromTasks.length + " <> Saved=" + count );
        }

        return count;
    }    // copyTasksFrom

    /**
     * Descripción de Método
     *
     *
     * @param fromPhase
     *
     * @return
     */

    public int copyTasksFrom( MProjectTypePhase fromPhase ) {
        if( fromPhase == null ) {
            return 0;
        }

        int count = 0;

        // Copy Type Tasks

        MProjectTypeTask[] fromTasks = fromPhase.getTasks();

        for( int i = 0;i < fromTasks.length;i++ ) {
            MProjectTask toTask = new MProjectTask( this,fromTasks[ i ] );

            if( toTask.save()) {
                count++;
            }
        }

        log.fine( "copyTasksFrom - #" + count + " - " + fromPhase );

        if( fromTasks.length != count ) {
            log.log( Level.SEVERE,"copyTasksFrom - Count difference - TypePhase=" + fromTasks.length + " <> Saved=" + count );
        }

        return count;
    }    // copyTasksFrom
}    // MProjectPhase



/*
 *  @(#)MProjectPhase.java   02.07.07
 * 
 *  Fin del fichero MProjectPhase.java
 *  
 *  Versión 2.2
 *
 */
