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



package org.openXpertya.apps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openXpertya.apps.form.ArchiveViewer;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.model.MBPartner;
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

public class AArchive implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param invoker
     * @param AD_Table_ID
     * @param Record_ID
     */

    public AArchive( JComponent invoker,int AD_Table_ID,int Record_ID ) {
        super();
        log.config( "AD_Table_ID=" + AD_Table_ID + ", Record_ID=" + Record_ID );
        m_AD_Table_ID = AD_Table_ID;
        m_Record_ID   = Record_ID;
        getArchives( invoker );
    }    // AArchive

    /** Descripción de Campos */

    private int m_AD_Table_ID;

    /** Descripción de Campos */

    private int m_Record_ID;

    /** Descripción de Campos */

    private JPopupMenu m_popup = new JPopupMenu( "ArchiveMenu" );

    /** Descripción de Campos */

    private JMenuItem m_reports = null;

    /** Descripción de Campos */

    private JMenuItem m_reportsAll = null;

    /** Descripción de Campos */

    private JMenuItem m_documents = null;

    /** Descripción de Campos */

    StringBuffer m_where = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AArchive.class );

    /**
     * Descripción de Método
     *
     *
     * @param invoker
     */

    private void getArchives( JComponent invoker ) {
        int reportCount   = 0;
        int documentCount = 0;

        m_where = new StringBuffer();
        m_where.append( "(AD_Table_ID=" ).append( m_AD_Table_ID ).append( " AND Record_ID=" ).append( m_Record_ID ).append( ")" );

        // Get all for BP

        if( m_AD_Table_ID == MBPartner.Table_ID ) {
            m_where.append( " OR C_BPartner_ID=" ).append( m_Record_ID );
        }

        //

        String sql = "SELECT IsReport, COUNT(*) FROM AD_Archive WHERE " + m_where + " GROUP BY IsReport";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                if( "Y".equals( rs.getString( 1 ))) {
                    reportCount = rs.getInt( 2 );
                } else {
                    documentCount += rs.getInt( 2 );
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

        //

        if( documentCount > 0 ) {
            m_documents = new JMenuItem( Msg.getMsg( Env.getCtx(),"ArchivedDocuments" ) + " (" + documentCount + ")" );
            m_popup.add( m_documents ).addActionListener( this );
        }

        if( reportCount > 0 ) {
            m_reports = new JMenuItem( Msg.getMsg( Env.getCtx(),"ArchivedReports" ) + " (" + reportCount + ")" );
            m_popup.add( m_reports ).addActionListener( this );
        }

        // All Reports

        sql = "SELECT COUNT(*) FROM AD_Archive WHERE AD_Table_ID=? AND IsReport='Y'";

        int allReports = DB.getSQLValue( null,sql,m_AD_Table_ID );

        if( allReports > 0 ) {
            m_reportsAll = new JMenuItem( Msg.getMsg( Env.getCtx(),"ArchivedReportsAll" ) + " (" + reportCount + ")" );
            m_popup.add( m_reportsAll ).addActionListener( this );
        }

        if( (documentCount == 0) && (reportCount == 0) && (allReports == 0) ) {
            m_popup.add( Msg.getMsg( Env.getCtx(),"ArchivedNone" ));
        }

        //

        if( invoker.isShowing()) {
            m_popup.show( invoker,0,invoker.getHeight());    // below button
        }
    }                                                        // getZoomTargets

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        int       AD_Form_ID = 118;    // ArchiveViewer
        FormFrame ff         = new FormFrame();

        ff.openForm( AD_Form_ID );

        ArchiveViewer av = ( ArchiveViewer )ff.getFormPanel();

        //

        if( e.getSource() == m_documents ) {
            av.query( false,m_AD_Table_ID,m_Record_ID );
        } else if( e.getSource() == m_reports ) {
            av.query( true,m_AD_Table_ID,m_Record_ID );
        } else {    // all Reports
            av.query( true,m_AD_Table_ID,0 );
        }

        //

        ff.pack();
        AEnv.showCenterScreen( ff );
        ff = null;
    }    // actionPerformed
}    // AArchive



/*
 *  @(#)AArchive.java   02.07.07
 * 
 *  Fin del fichero AArchive.java
 *  
 *  Versión 2.2
 *
 */
