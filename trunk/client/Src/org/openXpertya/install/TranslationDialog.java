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



package org.openXpertya.install;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TranslationDialog extends CPanel implements FormPanel,ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public TranslationDialog() {}    // TranslationDialog

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( TranslationDialog.class );

    //

    /** Descripción de Campos */

    private GridBagLayout mainLayout = new GridBagLayout();

    /** Descripción de Campos */

    private JComboBox cbLanguage = new JComboBox();

    /** Descripción de Campos */

    private JLabel lLanguage = new JLabel();

    /** Descripción de Campos */

    private JLabel lTable = new JLabel();

    /** Descripción de Campos */

    private JComboBox cbTable = new JComboBox();

    /** Descripción de Campos */

    private JButton bExport = new JButton();

    /** Descripción de Campos */

    private JButton bImport = new JButton();

    //

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private JLabel lClient = new JLabel();

    /** Descripción de Campos */

    private JComboBox cbClient = new JComboBox();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setLayout( mainLayout );
        lClient.setText( Msg.translate( Env.getCtx(),"AD_Client_ID" ));
        lLanguage.setText( Msg.translate( Env.getCtx(),"AD_Language" ));
        lLanguage.setToolTipText( Msg.translate( Env.getCtx(),"IsSystemLanguage" ));
        lTable.setText( Msg.translate( Env.getCtx(),"AD_Table_ID" ));

        //

        bExport.setText( Msg.getMsg( Env.getCtx(),"Export" ));
        bExport.addActionListener( this );
        bImport.setText( Msg.getMsg( Env.getCtx(),"Import" ));
        bImport.addActionListener( this );

        //

        this.add( cbLanguage,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        this.add( lLanguage,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.add( lTable,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.add( cbTable,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        this.add( bExport,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.add( bImport,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.add( lClient,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.add( cbClient,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // Fill Client

        cbClient.addItem( new KeyNamePair( -1,"" ));

        String sql = "SELECT Name, AD_Client_ID " + "FROM AD_Client " + "WHERE IsActive='Y' " + "ORDER BY 2";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                KeyNamePair kp = new KeyNamePair( rs.getInt( 2 ),rs.getString( 1 ));

                cbClient.addItem( kp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"TranslationDialog.dynInit (Client)",e );
        }

        // Fill Language

        sql = "SELECT Name, AD_Language " + "FROM AD_Language " + "WHERE IsActive='Y' AND (IsSystemLanguage='Y' OR IsBaseLanguage='Y')" + "ORDER BY 1";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                ValueNamePair vp = new ValueNamePair( rs.getString( 2 ),rs.getString( 1 ));

                cbLanguage.addItem( vp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"TranslationDialog.dynInit (Language)",e );
        }

        // Fill Table

        cbTable.addItem( new ValueNamePair( "","" ));
        sql = "SELECT Name, TableName " + "FROM AD_Table " + "WHERE TableName LIKE '%_Trl' " + "ORDER BY 1";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                ValueNamePair vp = new ValueNamePair( rs.getString( 2 ),rs.getString( 1 ));

                cbTable.addItem( vp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"TranslationDialog.dynInit (Table)",e );
        }

        // Info

        statusBar.setStatusLine( " " );
        statusBar.setStatusDB( " " );
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "TranslationDialog.init" );
        m_WindowNo = WindowNo;
        m_frame    = frame;
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );

        try {
            jbInit();
            dynInit();
            frame.getContentPane().add( this,BorderLayout.CENTER );
            frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"TranslationDialog.init",ex );
        }
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_frame.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        ValueNamePair AD_Language = ( ValueNamePair )cbLanguage.getSelectedItem();

        if( AD_Language == null ) {
            statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"LanguageSetupError" ),true );

            return;
        }

        ValueNamePair AD_Table = ( ValueNamePair )cbTable.getSelectedItem();

        if( AD_Table == null ) {
            return;
        }

        boolean     imp          = ( e.getSource() == bImport );
        KeyNamePair AD_Client    = ( KeyNamePair )cbClient.getSelectedItem();
        int         AD_Client_ID = -1;

        if( AD_Client != null ) {
            AD_Client_ID = AD_Client.getKey();
        }

        String       startDir = Ini.getOXPHome() + File.separator + "data";
        JFileChooser chooser  = new JFileChooser( startDir );

        chooser.setMultiSelectionEnabled( false );
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

        int returnVal = imp
                        ?chooser.showOpenDialog( this )
                        :chooser.showSaveDialog( this );

        if( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        String directory = chooser.getSelectedFile().getAbsolutePath();

        //

        statusBar.setStatusLine( directory );
        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        Translation t   = new Translation( Env.getCtx());
        String      msg = t.validateLanguage( AD_Language.getValue());

        if( msg.length() > 0 ) {
            ADialog.error( m_WindowNo,this,"LanguageSetupError",msg );

            return;
        }

        // All Tables

        if( AD_Table.getValue().equals( "" )) {
            for( int i = 1;i < cbTable.getItemCount();i++ ) {
                AD_Table = ( ValueNamePair )cbTable.getItemAt( i );
                msg      = null;
                msg      = imp
                           ?t.importTrl( directory,AD_Client_ID,AD_Language.getValue(),AD_Table.getValue())
                           :t.exportTrl( directory,AD_Client_ID,AD_Language.getValue(),AD_Table.getValue());
                statusBar.setStatusLine( msg );
            }

            statusBar.setStatusLine( directory );
        } else    // single table
        {
            msg = null;
            msg = imp
                  ?t.importTrl( directory,AD_Client_ID,AD_Language.getValue(),AD_Table.getValue())
                  :t.exportTrl( directory,AD_Client_ID,AD_Language.getValue(),AD_Table.getValue());
            statusBar.setStatusLine( msg );
        }

        //

        this.setCursor( Cursor.getDefaultCursor());
    }    // actionPerformed
}    // Translation



/*
 *  @(#)TranslationDialog.java   02.07.07
 * 
 *  Fin del fichero TranslationDialog.java
 *  
 *  Versión 2.2
 *
 */
