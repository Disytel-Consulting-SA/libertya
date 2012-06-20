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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.ProcesadorLogOXP;
import org.openXpertya.model.ProcesadorOXP;
import org.openXpertya.model.X_AD_WorkflowProcessor;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWorkflowProcessor extends X_AD_WorkflowProcessor implements ProcesadorOXP {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MWorkflowProcessor[] getActive( Properties ctx ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM AD_WorkflowProcessor WHERE IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWorkflowProcessor( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getActive",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MWorkflowProcessor[] retValue = new MWorkflowProcessor[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getActive

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MWorkflowProcessor.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param id
     * @param trxName
     */

    public MWorkflowProcessor( Properties ctx,int id,String trxName ) {
        super( ctx,id,trxName );
    }    // MWorkflowProcessor

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWorkflowProcessor( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWorkflowProcessor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerID() {
        return "WorkflowProcessor" + getID();
    }    // getServerID

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public Timestamp getDateNextRun( boolean requery ) {
        if( requery ) {
            load( get_TrxName());
        }

        return getDateNextRun();
    }    // getDateNextRun

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcesadorLogOXP[] getLogs() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * " + "FROM AD_WorkflowProcessorLog " + "WHERE AD_WorkflowProcessor_ID=? " + "ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getAD_WorkflowProcessor_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWorkflowProcessorLog( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLogs",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MWorkflowProcessorLog[] retValue = new MWorkflowProcessorLog[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLogs

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int deleteLog() {
        if( getKeepLogDays() < 1 ) {
            return 0;
        }

        String sql = "DELETE AD_WorkflowProcessorLog " + "WHERE AD_WorkflowProcessor_ID=" + getAD_WorkflowProcessor_ID() + " AND Created+ cast(cast(" + getKeepLogDays() + "as text)|| 'days' as interval) < SysDate";
        int no = DB.executeUpdate( sql,get_TrxName());

        return 0;
    }    // deleteLog
}    // MWorkflowProcessor



/*
 *  @(#)MWorkflowProcessor.java   02.07.07
 * 
 *  Fin del fichero MWorkflowProcessor.java
 *  
 *  Versión 2.2
 *
 */
