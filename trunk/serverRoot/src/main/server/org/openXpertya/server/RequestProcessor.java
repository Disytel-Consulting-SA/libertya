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



package org.openXpertya.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.openXpertya.db.DB_Oracle;
import org.openXpertya.model.MChangeRequest;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MGroup;
import org.openXpertya.model.MRequest;
import org.openXpertya.model.MRequestProcessor;
import org.openXpertya.model.MRequestProcessorLog;
import org.openXpertya.model.MRequestProcessorRoute;
import org.openXpertya.model.MStatus;
import org.openXpertya.model.MUser;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RequestProcessor extends ServidorOXP {

    /**
     * Constructor de la clase ...
     *
     *
     * @param model
     */

    public RequestProcessor( MRequestProcessor model ) {
        super( model,60 );    // 1 minute delay
        m_model  = model;
        m_client = MClient.get( model.getCtx(),model.getAD_Client_ID());
    }    // RequestProcessor

    /** Descripción de Campos */

    private MRequestProcessor m_model = null;

    /** Descripción de Campos */

    private StringBuffer m_summary = new StringBuffer();

    /** Descripción de Campos */

    private MClient m_client = null;

    /**
     * Descripción de Método
     *
     */

    protected void doWork() {
        m_summary = new StringBuffer();

        //

        processEMail();
        findSalesRep();
        processRequests();
        processStatus();
        processECR();

        //

        int no = m_model.deleteLog();

        m_summary.append( "Logs deleted=" ).append( no );

        //

        MRequestProcessorLog pLog = new MRequestProcessorLog( m_model,m_summary.toString());

        pLog.setReference( "#" + String.valueOf( p_runCount ) + " - " + TimeUtil.formatElapsed( new Timestamp( p_startWork )));
        pLog.save();
    }    // doWork

    /**
     * Descripción de Método
     *
     */

    private void processRequests() {
        String sql = "SELECT * FROM R_Request " + "WHERE DueType='7' AND Processed='N'"    // 7=Scheduled
                     + " AND DateNextAction > Current_date" 
                    +  " AND AD_Client_ID=?";

        if( m_model.getR_RequestType_ID() != 0 ) {
            sql += " AND R_RequestType_ID=?";
        }

        PreparedStatement pstmt       = null;
        int               count       = 0;
        int               countEMails = 0;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_model.getAD_Client_ID());

            if( m_model.getR_RequestType_ID() != 0 ) {
                pstmt.setInt( 2,m_model.getR_RequestType_ID());
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRequest request = new MRequest( getCtx(),rs,null );

                request.setDueType();

                if( request.getRequestType().isEMailWhenDue()) {
                    if( sendEmail( request,"RequestDue" )) {
                        request.setDateLastAlert();
                        countEMails++;
                    }
                }

                request.save();
                count++;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(due)",e );
        }

        m_summary.append( "New Due #" ).append( count );

        if( countEMails > 0 ) {
            m_summary.append( " (" ).append( countEMails ).append( " EMail)" );
        }

        m_summary.append( " - " );
        sql = "SELECT * FROM R_Request r " + "WHERE r.DueType<>'3' AND r.Processed='N'"    // 3=Overdue
              + " AND AD_Client_ID=?" + " AND EXISTS (SELECT * FROM R_RequestType rt " + "WHERE r.R_RequestType_ID=rt.R_RequestType_ID" + " AND (r.DateNextAction+rt.DueDateTolerance * interval '1 day') < current_date)";

        if( m_model.getR_RequestType_ID() != 0 ) {
            sql += " AND r.R_RequestType_ID=?";
        }

        count       = 0;
        countEMails = 0;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_model.getAD_Client_ID());

            if( m_model.getR_RequestType_ID() != 0 ) {
                pstmt.setInt( 2,m_model.getR_RequestType_ID());
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRequest request = new MRequest( getCtx(),rs,null );

                request.setDueType();

                if( request.getRequestType().isEMailWhenOverdue()) {
                    if( sendEmail( request,"RequestDue" )) {
                        request.setDateLastAlert();
                        countEMails++;
                    }
                }

                request.save();
                count++;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"(overdue)",e );
        }

        m_summary.append( "New Overdue #" ).append( count );

        if( countEMails > 0 ) {
            m_summary.append( " (" ).append( countEMails ).append( " EMail)" );
        }

        m_summary.append( " - " );

        if( m_model.getOverdueAlertDays() > 0 ) {
            sql = "SELECT * FROM R_Request " + "WHERE Processed='N'" + " AND AD_Client_ID=?" + " AND (DateNextAction+ (" + m_model.getOverdueAlertDays() + "* interval '1 day')) < current_date " + " AND (DateLastAlert IS NULL";

            if( m_model.getRemindDays() > 0 ) {
                sql += " OR (DateLastAlert+" + m_model.getRemindDays() + "* interval '1 day') < current_date ";
            }

            sql += ")";

            if( m_model.getR_RequestType_ID() != 0 ) {
                sql += " AND R_RequestType_ID=?";
            }

            count       = 0;
            countEMails = 0;

            try {
                pstmt = DB.prepareStatement( sql );
                pstmt.setInt( 1,m_model.getAD_Client_ID());

                if( m_model.getR_RequestType_ID() != 0 ) {
                    pstmt.setInt( 2,m_model.getR_RequestType_ID());
                }

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    MRequest request = new MRequest( getCtx(),rs,null );

                    request.setDueType();

                    if( sendEmail( request,"RequestAlert" )) {
                        request.setDateLastAlert();
                        countEMails++;
                    }

                    request.save();
                    count++;
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"(alert): " + getName(),e );
            }

            m_summary.append( "Alerts #" ).append( count );

            if( countEMails > 0 ) {
                m_summary.append( " (" ).append( countEMails ).append( " EMail)" );
            }

            m_summary.append( " - " );
        }    // Overdue

        if( m_model.getOverdueAssignDays() > 0 ) {
            sql = "SELECT * FROM R_Request " + "WHERE Processed='N'" + " AND AD_Client_ID=?" + " AND IsEscalated='N'" + " AND (DateNextAction+(" + m_model.getOverdueAssignDays() + "* interval '1 day')) < current_date ";

            if( m_model.getR_RequestType_ID() != 0 ) {
                sql += " AND R_RequestType_ID=?";
            }

            count       = 0;
            countEMails = 0;

            try {
                pstmt = DB.prepareStatement( sql );
                pstmt.setInt( 1,m_model.getAD_Client_ID());

                if( m_model.getR_RequestType_ID() != 0 ) {
                    pstmt.setInt( 2,m_model.getR_RequestType_ID());
                }

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    MRequest request = new MRequest( getCtx(),rs,null );

                    if( escalate( request )) {
                        count++;
                    }
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"(escalate): " + getName(),e );
            }

            m_summary.append( "Escalated #" ).append( count ).append( " - " );
        }    // Esacalate

        if( m_model.getInactivityAlertDays() > 0 ) {
            sql = "SELECT * FROM R_Request " + "WHERE Processed='N'" + " AND AD_Client_ID=?" + " AND (Updated + (" + m_model.getInactivityAlertDays() + "* interval '1 day')) < current_date " + " AND (DateLastAlert IS NULL";

            if( m_model.getRemindDays() > 0 ) {
                sql += " OR (DateLastAlert+(" + m_model.getRemindDays() + " * interval '1 day')) < current_date ";
            }

            sql += ")";

            if( m_model.getR_RequestType_ID() != 0 ) {
                sql += " AND R_RequestType_ID=?";
            }

            count       = 0;
            countEMails = 0;

            try {
                pstmt = DB.prepareStatement( sql );
                pstmt.setInt( 1,m_model.getAD_Client_ID());

                if( m_model.getR_RequestType_ID() != 0 ) {
                    pstmt.setInt( 2,m_model.getR_RequestType_ID());
                }

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    MRequest request = new MRequest( getCtx(),rs,null );

                    request.setDueType();

                    if( sendEmail( request,"RequestInactive" )) {
                        request.setDateLastAlert();
                        countEMails++;
                    }

                    request.save();
                    count++;
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"(inactivity): " + getName(),e );
            }

            m_summary.append( "Inactivity #" ).append( count );

            if( countEMails > 0 ) {
                m_summary.append( " (" ).append( countEMails ).append( " EMail)" );
            }

            m_summary.append( " - " );
        }    // Inactivity

        //

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }
    }    // processRequests

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param AD_Message
     *
     * @return
     */

    private boolean sendEmail( MRequest request,String AD_Message ) {

        // Alert: Request {0} overdue

        String subject = Msg.getMsg( m_client.getAD_Language(),AD_Message,new Object[]{ request.getDocumentNo()} );
        EMail email = new EMail( m_client,null,request.getSalesRep(),subject,request.getSummary());

        return EMail.SENT_OK.equals( email.send());
    }    // sendAlert

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    private boolean escalate( MRequest request ) {

        // Get Supervisor

        MUser supervisor    = request.getSalesRep();    // self
        int   supervisor_ID = request.getSalesRep().getSupervisor_ID();

        if( (supervisor_ID == 0) && (m_model.getSupervisor_ID() != 0) ) {
            supervisor_ID = m_model.getSupervisor_ID();
        }

        if( (supervisor_ID != 0) && (supervisor_ID != request.getAD_User_ID())) {
            supervisor = MUser.get( getCtx(),supervisor_ID );
        }

        // Escalated: Request {0} to {1}

        String subject = Msg.getMsg( m_client.getAD_Language(),"RequestEscalate",new Object[]{ request.getDocumentNo(),supervisor.getName()} );
        EMail email = new EMail( m_client,null,request.getSalesRep(),subject,request.getSummary());

        email.send();

        // Not the same - send mail to supervisor

        if( request.getSalesRep_ID() != supervisor.getAD_User_ID()) {
            email = new EMail( m_client,null,supervisor,subject,request.getSummary());
            email.send();
        }

        // ----------------

        request.setDueType();
        request.setIsEscalated( true );
        request.setResult( subject );

        return request.save();
    }    // escalate

    /**
     * Descripción de Método
     *
     */

    private void processStatus() {
        int count = 0;

        // Requests with status with an timeout

        // Consulta errónea: hacía un mal uso de fechas.
        //String sql = "SELECT * FROM R_Request r WHERE EXISTS (" + "SELECT * FROM R_Status s " + "WHERE r.R_Status_ID=r.R_Status_ID" + " AND s.TimeoutDays > 0 AND s.Next_Status_ID > 0" + " AND r.Updated+s.TimeoutDays > SysDate" + ") " + "ORDER BY R_Status_ID";
        
        String sql = 
        	" SELECT * " + 
        	" FROM R_Request r " + 
        	" WHERE EXISTS ( " +
        	"	SELECT * FROM R_Status s " +      
        	"	WHERE r.R_Status_ID=r.R_Status_ID " +      
        	"	  AND s.TimeoutDays > 0 AND s.Next_Status_ID > 0 " +      
        	"	  AND r.Updated + (cast(s.TimeoutDays || 'days' AS interval)) > now() " +
        	"      ) " +      
        	" ORDER BY R_Status_ID; ";
        
        PreparedStatement pstmt  = null;
        MStatus           status = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRequest r = new MRequest( getCtx(),rs,null );

                //

                if( (status == null) || (status.getR_Status_ID() != r.getR_Status_ID())) {
                    status = MStatus.get( getCtx(),r.getR_Status_ID());
                }

                //

                r.setResult( Msg.getMsg( getCtx(),"RequestStatusTimeout" ));
                r.setR_Status_ID( status.getNext_Status_ID());

                if( r.save()) {
                    count++;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_summary.append( "Status Timeout #" ).append( count ).append( " - " );
    }    // processStatus

    /**
     * Descripción de Método
     *
     */

    private void processECR() {

        // Get Requests with Request Type-AutoChangeRequest and Group with info

        String sql = "SELECT * FROM R_Request r " + "WHERE M_ChangeRequest_ID IS NULL" + " AND EXISTS (" + "SELECT * FROM R_RequestType rt " + "WHERE rt.R_RequestType_ID=r.R_RequestType_ID" + " AND rt.IsAutoChangeRequest='Y')" + "AND EXISTS (" + "SELECT * FROM R_Group g " + "WHERE g.R_Group_ID=r.R_Group_ID" + " AND (g.M_BOM_ID IS NOT NULL OR g.M_ChangeNotice_ID IS NOT NULL)     )";

        //

        int count   = 0;
        int failure = 0;

        //

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRequest       r   = new MRequest( getCtx(),rs,null );
                MGroup         rg  = MGroup.get( getCtx(),r.getR_Group_ID());
                MChangeRequest ecr = new MChangeRequest( r,rg );

                if( r.save()) {
                    r.setM_ChangeRequest_ID( ecr.getM_ChangeRequest_ID());

                    if( r.save()) {
                        count++;
                    } else {
                        failure++;
                    }
                } else {
                    failure++;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_summary.append( "Auto Change Request #" ).append( count );

        if( failure > 0 ) {
            m_summary.append( "(fail=" ).append( failure ).append( ")" );
        }

        m_summary.append( " - " );
    }    // processECR

    /**
     * Descripción de Método
     *
     */

    private void processEMail() {

        // m_summary.append("Mail #").append(count)
        // .append(" - ");

    }    // processEMail

    /**
     * Descripción de Método
     *
     */

    private void findSalesRep() {
        int        changed  = 0;
        int        notFound = 0;
        Properties ctx      = new Properties();

        //

        String sql = "SELECT * FROM R_Request " + "WHERE AD_Client_ID=?" + " AND SalesRep_ID=0 AND Processed='N'";

        if( m_model.getR_RequestType_ID() != 0 ) {
            sql += " AND R_RequestType_ID=?";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_model.getAD_Client_ID());

            if( m_model.getR_RequestType_ID() != 0 ) {
                pstmt.setInt( 2,m_model.getR_RequestType_ID());
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRequest request     = new MRequest( ctx,rs,null );
                int      SalesRep_ID = findSalesRep( request );

                if( SalesRep_ID != 0 ) {
                    request.setSalesRep_ID( SalesRep_ID );
                    request.save();
                    changed++;
                } else {
                    notFound++;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"findSalesRep",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        if( (changed == 0) && (notFound == 0) ) {
            m_summary.append( "No unallocated Requests" );
        } else {
            m_summary.append( "Allocated SalesRep=" ).append( changed );
        }

        if( notFound > 0 ) {
            m_summary.append( ",Not=" ).append( notFound );
        }

        m_summary.append( " - " );
    }    // findSalesRep

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    private int findSalesRep( MRequest request ) {
        int    AD_User_ID = 0;
        String QText      = request.getSummary();

        if( QText == null ) {
            QText = "";
        } else {
            QText = QText.toUpperCase();
        }

        //

        MRequestProcessorRoute[] routes = m_model.getRoutes( false );

        for( int i = 0;i < routes.length;i++ ) {
            MRequestProcessorRoute route = routes[ i ];

            // Match first on Request Type

            if( (request.getR_RequestType_ID() == route.getR_RequestType_ID()) && (route.getR_RequestType_ID() != 0) ) {
                return route.getAD_User_ID();
            }

            // Match on element of keyword

            String keyword = route.getKeyword();

            if( keyword != null ) {
                StringTokenizer st = new StringTokenizer( keyword.toUpperCase()," ,;\t\n\r\f" );

                while( (AD_User_ID == 0) && st.hasMoreElements()) {
                    if( QText.indexOf( st.nextToken()) != -1 ) {
                        return route.getAD_User_ID();
                    }
                }
            }
        }    // for all routes

        return m_model.getSupervisor_ID();
    }    // findSalesRep

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getServerInfo() {
        return "#" + p_runCount + " - Last=" + m_summary.toString();
    }    // getServerInfo
}    // RequestProcessor



/*
 *  @(#)RequestProcessor.java   24.03.06
 * 
 *  Fin del fichero RequestProcessor.java
 *  
 *  Versión 2.2
 *
 */
