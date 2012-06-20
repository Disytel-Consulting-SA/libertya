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



package org.openXpertya.dbPort;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openXpertya.OpenXpertya;
import org.openXpertya.db.CConnection;
import org.openXpertya.db.CConnectionEditor;
import org.openXpertya.db.DB_PostgreSQL;
import org.openXpertya.db.Database;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ConvertDialog extends JFrame implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public ConvertDialog() {
        try {
            jbInit();

            //

            fSelectFile.addItem( "C:\\openxpertya\\db\\database\\create\\views.sql" );
            fSelectFile.addItem( "C:\\openxpertya\\db\\database\\create\\temporary.sql" );
            fSelectFile.addItem( "C:\\openxpertya\\db\\database\\create\\sequences.sql" );
            fSelectFile.addItem( "C:\\openxpertya\\db\\database\\create\\openxpertya.sql" );

            // Set up environment

            fConnect.setValue( CConnection.get( Database.DB_POSTGRESQL,"linux",DB_PostgreSQL.DEFAULT_PORT,"openxp" ));
            fTarget.setSelectedItem( Database.DB_POSTGRESQL );
            fExecute.setSelected( true );
            cmd_execute();    // set UI

            //

            pack();
            setVisible( true );
        } catch( Exception e ) {
            System.err.println( e );
        }
    }    // ConvertDialog

    /** Descripción de Campos */

    private JPanel parameterPanel = new JPanel();

    /** Descripción de Campos */

    private GridBagLayout gridBagLayout1 = new GridBagLayout();

    /** Descripción de Campos */

    private JLabel lSelectFile = new JLabel();

    /** Descripción de Campos */

    private JComboBox fSelectFile = new JComboBox();

    /** Descripción de Campos */

    private JButton bSelectFile = new JButton();

    /** Descripción de Campos */

    private JCheckBox fExecute = new JCheckBox();

    /** Descripción de Campos */

    private JLabel lConnect = new JLabel();

    /** Descripción de Campos */

    private CConnectionEditor fConnect = new CConnectionEditor();

    /** Descripción de Campos */

    private JButton bStart = new JButton();

    /** Descripción de Campos */

    private JScrollPane scrollPane = new JScrollPane();

    /** Descripción de Campos */

    private JTextArea infoPane = new JTextArea();

    /** Descripción de Campos */

    private Component component1;

    /** Descripción de Campos */

    private Component component2;

    /** Descripción de Campos */

    private Component component3;

    /** Descripción de Campos */

    private Component component4;

    /** Descripción de Campos */

    private JLabel lTarget = new JLabel();

    /** Descripción de Campos */

    private JComboBox fTarget = new JComboBox( Database.DB_NAMES );

    /** Descripción de Campos */

    private JCheckBox fVerbose = new JCheckBox();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        component1 = Box.createHorizontalStrut( 8 );
        component2 = Box.createHorizontalStrut( 8 );
        component3 = Box.createVerticalStrut( 8 );
        component4 = Box.createVerticalStrut( 8 );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.setTitle( "DB Convert Dialog" );

        //

        parameterPanel.setLayout( gridBagLayout1 );
        lSelectFile.setText( "Select File" );
        fSelectFile.setEditable( true );
        bSelectFile.setText( "add file" );
        bSelectFile.addActionListener( this );
        fExecute.setText( "Execute Directly" );
        fExecute.addActionListener( this );
        lConnect.setText( "Connection" );
        bStart.setText( "Start" );
        bStart.addActionListener( this );

        //

        infoPane.setBackground( Color.lightGray );
        infoPane.setEditable( false );
        scrollPane.setPreferredSize( new Dimension( 200,200 ));
        lTarget.setText( "Target" );
        fVerbose.setText( "Verbose" );

        //

        this.getContentPane().add( parameterPanel,BorderLayout.NORTH );
        parameterPanel.add( lSelectFile,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        parameterPanel.add( fSelectFile,new GridBagConstraints( 2,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( bSelectFile,new GridBagConstraints( 3,1,1,1,0.0,0.0,GridBagConstraints.SOUTHWEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( fExecute,new GridBagConstraints( 2,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( lConnect,new GridBagConstraints( 1,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        parameterPanel.add( fConnect,new GridBagConstraints( 2,4,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        parameterPanel.add( bStart,new GridBagConstraints( 3,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( component1,new GridBagConstraints( 5,0,1,2,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        parameterPanel.add( component2,new GridBagConstraints( 0,0,1,2,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        parameterPanel.add( component3,new GridBagConstraints( 1,6,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        parameterPanel.add( component4,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        parameterPanel.add( lTarget,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.getContentPane().add( scrollPane,BorderLayout.CENTER );
        scrollPane.getViewport().add( infoPane,null );
        parameterPanel.add( fTarget,new GridBagConstraints( 2,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( fVerbose,new GridBagConstraints( 3,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        //

        if( e.getSource() == bStart ) {
            bStart.setEnabled( false );
            cmd_start();
            bStart.setEnabled( true );
        } else if( e.getSource() == bSelectFile ) {
            cmd_selectFile();
        } else if( e.getSource() == fExecute ) {
            cmd_execute();
        }

        //

        setCursor( Cursor.getDefaultCursor());
    }    // actionListener

    /**
     * Descripción de Método
     *
     */

    private void cmd_execute() {
        lConnect.setEnabled( fExecute.isSelected());
        fConnect.setReadWrite( fExecute.isSelected());
        lTarget.setEnabled( !fExecute.isSelected());
        fTarget.setEnabled( !fExecute.isSelected());
    }    // cmd_execute

    /**
     * Descripción de Método
     *
     */

    private void cmd_selectFile() {
        JFileChooser fc = new JFileChooser();

        fc.setMultiSelectionEnabled( false );

        if( fc.showOpenDialog( this ) != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        File f = fc.getSelectedFile();

        if( (f == null) ||!f.isFile()) {
            return;
        }

        String fileName = f.getAbsolutePath();

        //

        fSelectFile.addItem( fileName );
        fSelectFile.setSelectedItem( fileName );
    }    // cmd_selectFile

    /**
     * Descripción de Método
     *
     */

    private void cmd_start() {

        // Open and read File

        File file = new File(( String )fSelectFile.getSelectedItem());

        if( !file.exists() || file.isDirectory()) {
            infoPane.append( "File does not exist or a directory: " + file + "\n" );

            return;
        }

        infoPane.append( "Opening file: " + file + "\n" );

        StringBuffer sb = new StringBuffer( 1000 );

        //

        try {
            FileReader     fr    = new FileReader( file );
            BufferedReader in    = new BufferedReader( fr );
            String         line  = null;
            int            lines = 0;

            while(( line = in.readLine()) != null ) {
                lines++;
                sb.append( line ).append( '\n' );
            }

            in.close();
            fr.close();
            infoPane.append( "- Read lines: " + lines + ", size: " + sb.length() + "\n" );
        } catch( FileNotFoundException fnf ) {
            infoPane.append( "Error: " + fnf + "\n" );

            return;
        } catch( IOException ioe ) {
            infoPane.append( "Error: " + ioe + "\n" );

            return;
        }

        // Target system

        if( fExecute.isSelected()) {
            CConnection cc      = ( CConnection )fConnect.getValue();
            Convert     convert = new Convert( cc.getType());

            convert.setVerbose( fVerbose.isSelected());

            //

            Connection conn = cc.getConnection( true,Connection.TRANSACTION_READ_COMMITTED );

            convert.execute( sb.toString(),conn );

            if( convert.hasError()) {
                StringBuffer sbb = new StringBuffer( "- Error: " );

                if( convert.getConversionError() != null ) {
                    sbb.append( convert.getConversionError()).append( ' ' );
                }

                if( convert.getException() != null ) {
                    sbb.append( convert.getException());
                    convert.getException().printStackTrace();
                }

                sbb.append( "\n" );
                infoPane.append( sbb.toString());
            } else {
                infoPane.append( "- OK\n" );
            }
        } else {
            String target = ( String )fTarget.getSelectedItem();

            if( Database.DB_ORACLE.equals( target )) {
                infoPane.append( "No conversion needed.\n" );

                return;
            }

            Convert convert = new Convert( target );

            //

            String cc = convert.convertAll( sb.toString());

            // Output file name

            String fileName = file.getAbsolutePath();
            int    pos      = fileName.lastIndexOf( "." );

            if( pos == -1 ) {
                fileName += target;
            } else {
                fileName = fileName.substring( 0,pos ) + target + fileName.substring( pos );
            }

            infoPane.append( "Writing to: " + fileName + "\n" );

            // Write to file

            try {
                FileWriter     fw  = new FileWriter( fileName,false );
                BufferedWriter out = new BufferedWriter( fw );

                out.write( "-- OpenXpertya dbPort - Convert Oracle to " + target );
                out.newLine();
                out.write( "-- " + OpenXpertya.getSummary());
                out.newLine();

                //

                out.write( cc );

                //

                out.close();
                fw.close();
            } catch( IOException ioe ) {
                infoPane.append( "Error: " + ioe + "\n" );
            }

            infoPane.append( "- Written: " + cc.length() + "\n" );
        }
    }    // cmd_start

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        new ConvertDialog();
    }    // main
}    // ConvertDialog



/*
 *  @(#)ConvertDialog.java   25.03.06
 * 
 *  Fin del fichero ConvertDialog.java
 *  
 *  Versión 2.2
 *
 */
