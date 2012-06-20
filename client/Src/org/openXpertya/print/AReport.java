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



package org.openXpertya.print;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.openXpertya.apps.ADialog;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AReport implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_Table_ID
     * @param invoker
     * @param query
     */

    public AReport( int AD_Table_ID,JComponent invoker,MQuery query ) {
        log.config( "AD_Table_ID=" + AD_Table_ID + " " + query );

        if( !MRole.getDefault().isCanReport( AD_Table_ID )) {
            ADialog.error( 0,invoker,"AccessCannotReport",query.getTableName());

            return;
        }

        m_query = query;

        // See What is there

        getPrintFormats( AD_Table_ID,invoker );
    }    // AReport

    /** Descripción de Campos */

    private MQuery m_query;

    /** Descripción de Campos */

    private JPopupMenu m_popup = new JPopupMenu( "ReportMenu" );

    /** Descripción de Campos */

    private ArrayList m_list = new ArrayList();

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AReport.class );

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param invoker
     */

    private void getPrintFormats( int AD_Table_ID,JComponent invoker ) {
        int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());

        //

        String sql = MRole.getDefault().addAccessSQL( "SELECT AD_PrintFormat_ID, Name, AD_Client_ID " + "FROM AD_PrintFormat " + "WHERE AD_Table_ID=? AND IsTableBased='Y' AND IsActive='Y' " + "ORDER BY AD_Client_ID DESC, IsDefault DESC, Name",    // Own First
            "AD_PrintFormat",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
        KeyNamePair pp = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                pp = new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 ));
                int pf_AD_Client_ID = rs.getInt( 3 );
                // Allows System Print Formats.
                if( pf_AD_Client_ID == AD_Client_ID || pf_AD_Client_ID == 0) {
                    m_list.add( pp );
                    m_popup.add( pp.toString()).addActionListener( this );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AReport.getPrintFormats",e );
        }

        // No Format exists - create it

        if( m_list.size() == 0 ) {
            if( pp == null ) {
                createNewFormat( AD_Table_ID );              // calls launch
            } else {
                copyFormat( pp.getKey(),AD_Client_ID );
            }
        }

        // One Format exists or no invoker - show it

        else if( (m_list.size() == 1) || (invoker == null) ) {
            launchReport(( KeyNamePair )m_list.get( 0 ));

            // Multiple Formats exist - show selection

        } else {
            m_popup.show( invoker,0,invoker.getHeight());    // below button
        }
    }                                                        // getPrintFormats

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     */

    private void createNewFormat( int AD_Table_ID ) {
        MPrintFormat pf = MPrintFormat.createFromTable( Env.getCtx(),AD_Table_ID );

        launchReport( pf );
    }    // createNewFormat

    /**
     * Descripción de Método
     *
     *
     * @param AD_PrintFormat_ID
     * @param To_Client_ID
     */

    private void copyFormat( int AD_PrintFormat_ID,int To_Client_ID ) {
        MPrintFormat pf = MPrintFormat.copyToClient( Env.getCtx(),AD_PrintFormat_ID,To_Client_ID );

        launchReport( pf );
    }    // copyFormatFromClient

    /**
     * Descripción de Método
     *
     *
     * @param pp
     */

    private void launchReport( KeyNamePair pp ) {
        MPrintFormat pf = MPrintFormat.get( Env.getCtx(),pp.getKey(),false );

        launchReport( pf );
    }    // launchReport

    /**
     * Descripción de Método
     *
     *
     * @param pf
     */

    private void launchReport( MPrintFormat pf ) {
        int Record_ID = 0;

        if( (m_query.getRestrictionCount() == 1) && (m_query.getCode( 0 ) instanceof Integer) ) {
            Record_ID = (( Integer )m_query.getCode( 0 )).intValue();
        }

        PrintInfo info = new PrintInfo( pf.getName(),pf.getAD_Table_ID(),Record_ID );

        info.setDescription( m_query.getInfo());

        ReportEngine re = new ReportEngine( Env.getCtx(),pf,m_query,info );

        new Viewer( re );

        // if (m_popup.isVisible())
        // m_popup.setVisible(false);

    }    // launchReport

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        m_popup.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        String cmd = e.getActionCommand();

        for( int i = 0;i < m_list.size();i++ ) {
            KeyNamePair pp = ( KeyNamePair )m_list.get( i );

            if( cmd.equals( pp.getName())) {
                launchReport( pp );

                return;
            }
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param TableName
     *
     * @return
     */

    static public int getAD_Table_ID( String TableName ) {
        int    AD_Table_ID = 0;
        String sql         = "SELECT AD_Table_ID FROM AD_Table WHERE TableName=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setString( 1,TableName );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                AD_Table_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        return AD_Table_ID;
    }    // getAD_Table_ID
}    // AReport



/*
 *  @(#)AReport.java   02.07.07
 * 
 *  Fin del fichero AReport.java
 *  
 *  Versión 2.2
 *
 */
