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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.openXpertya.model.MQuery;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AZoomAcross implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param invoker
     * @param tableName
     * @param query
     */

    public AZoomAcross( JComponent invoker,String tableName,MQuery query ) {
        log.config( "TableName=" + tableName + " - " + query );
        m_query = query;

        // See What is there

        getZoomTargets( invoker,tableName );
    }    // AReport

    /** Descripción de Campos */

    private MQuery m_query;

    /** Descripción de Campos */

    private JPopupMenu m_popup = new JPopupMenu( "ZoomMenu" );

    /** Descripción de Campos */

    private ArrayList m_list = new ArrayList();

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AZoomAcross.class );
    
    private Properties m_ctx = Env.getCtx();

    /**
     * Descripción de Método
     *
     *
     * @param invoker
     * @param tableName
     */

    private void getZoomTargets( JComponent invoker,String tableName ) {
        String sql = "SELECT DISTINCT ws.AD_Window_ID,ws.Name, wp.AD_Window_ID,wp.Name, t.TableName " + "FROM AD_Table t ";
        boolean baseLanguage = Env.isBaseLanguage( Env.getCtx(),"AD_Window" );

        if( baseLanguage ) {
            sql += "INNER JOIN AD_Window ws ON (t.AD_Window_ID=ws.AD_Window_ID)" + " LEFT OUTER JOIN AD_Window wp ON (t.PO_Window_ID=wp.AD_Window_ID) ";
        } else {
            sql += "INNER JOIN AD_Window_Trl ws ON (t.AD_Window_ID=ws.AD_Window_ID AND ws.AD_Language=?)" + " LEFT OUTER JOIN AD_Window_Trl wp ON (t.PO_Window_ID=wp.AD_Window_ID AND wp.AD_Language=?) ";
        }

        //

        sql += "WHERE t.TableName NOT LIKE 'I%'"                                                                                                                                                                                                                                   // No Import
               + " AND EXISTS (SELECT * FROM AD_Tab tt "                                                                                                                                                                                                                           // First Tab
               + "WHERE (tt.AD_Window_ID=ws.AD_Window_ID OR tt.AD_Window_ID=wp.AD_Window_ID)" + " AND tt.AD_Table_ID=t.AD_Table_ID AND tt.SeqNo=10)" + " AND t.AD_Table_ID IN " + "(SELECT AD_Table_ID FROM AD_Column " + "WHERE ColumnName=? AND IsKey='N' AND IsParent='N') "    // #x
               + "ORDER BY 2";

        KeyNamePair pp = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            int               index = 1;

            if( !baseLanguage ) {
                pstmt.setString( index++,Env.getAD_Language( Env.getCtx()));
                pstmt.setString( index++,Env.getAD_Language( Env.getCtx()));
            }

            pstmt.setString( index++,tableName + "_ID" );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int    AD_Window_ID    = rs.getInt( 1 );
                String Name            = rs.getString( 2 );
                int    PO_Window_ID    = rs.getInt( 3 );
                String targetTableName = rs.getString( 5 );

                if( PO_Window_ID == 0 ) {
                    addTarget( targetTableName,AD_Window_ID,Name,null );
                } else {
                    addTarget( targetTableName,AD_Window_ID,Name,Boolean.TRUE );
                }

                // PO

                if( PO_Window_ID != 0 ) {
                    Name = rs.getString( 4 );
                    addTarget( targetTableName,PO_Window_ID,Name,Boolean.FALSE );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        // No Zoom
        
        if( m_list.size() == 0 ) {
            String msg = Msg.getMsg(m_ctx, "NoZoomTarget", 
            		new Object[] { Msg.translate(m_ctx, tableName + "_ID") }); 
        	ADialog.info( 0,invoker, msg);
            log.info( "BaseLanguage=" + baseLanguage );
        } else if( invoker.isShowing()) {
            m_popup.show( invoker,0,invoker.getHeight());    // below button
        }
    }                                                        // getZoomTargets

    /**
     * Descripción de Método
     *
     *
     * @param targetTableName
     * @param AD_Window_ID
     * @param Name
     * @param isSO
     *
     * @return
     */

    private boolean addTarget( String targetTableName,int AD_Window_ID,String Name,Boolean isSO ) {
        String sql = "SELECT COUNT(*) FROM " + targetTableName + " WHERE " + m_query.getWhereClause( false );
        String sqlAdd = "";

        if( isSO != null ) {
            sqlAdd = " AND IsSOTrx=" + ( isSO.booleanValue()
                                         ?"'Y'"
                                         :"'N'" );
        }

        int count = DB.getSQLValue( null,sql + sqlAdd );

        if( (count < 0) && (isSO != null) ) {    // error try again w/o SO
            DB.getSQLValue( null,sql );
        }

        if( count <= 0 ) {
            return false;
        }

        //

        KeyNamePair pp = new KeyNamePair( AD_Window_ID,Name );

        m_list.add( pp );
        m_popup.add( pp.toString()).addActionListener( this );

        return true;
    }    // checkTarget

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
                launchZoom( pp );

                return;
            }
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param pp
     */

    private void launchZoom( KeyNamePair pp ) {
        int AD_Window_ID = pp.getKey();

        log.info( "AD_Window_ID=" + AD_Window_ID + " - " + m_query );

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,m_query )) {
            return;
        }

        AEnv.showCenterScreen( frame );
        frame = null;
    }    // launchZoom
}    // AZoom



/*
 *  @(#)AZoomAcross.java   02.07.07
 * 
 *  Fin del fichero AZoomAcross.java
 *  
 *  Versión 2.2
 *
 */
