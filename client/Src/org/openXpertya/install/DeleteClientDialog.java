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

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

import java.sql.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;

import org.compiere.swing.*;

import org.openXpertya.apps.*;
import org.openXpertya.apps.form.*;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class DeleteClientDialog extends CPanel implements FormPanel,ActionListener,ASyncProcess {

    /**
     * Constructor de la clase DeleteClientDialog
     * 
     * A raiz del cambio en DeleteClient.java, algunos parametros del algoritmo  se hicieron innecesarios
	 * Por lo que cambiamos el codigo de creacion de la ventana para no pedirselos al usuario
	 * Concretamente en Iteraciones y en IgnoreErrors
     *
     */

    public DeleteClientDialog() {}    // DeleteClientDialog

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private GridBagLayout mainLayout = new GridBagLayout();

    /** Descripción de Campos */

    private JButton bDelete = new JButton();

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private JLabel lClient = new JLabel();

    /** Descripción de Campos */

    private JComboBox cbClient = new JComboBox();

    /** Descripción de Campos */

    //private JLabel lIterations = new JLabel();

    /** Descripción de Campos */

    //private JCheckBox cbIgnoreError = new JCheckBox();

    /** Descripción de Campos */

    //private JLabel lIgnoreError = new JLabel();

    /** Descripción de Campos */

    private VNumber vIterations = new VNumber( "Iterations",false,false,true,DisplayType.Integer,"Iterations" );

    /** Descripción de Campos */

    private String info = new String( "" );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setLayout( mainLayout );
        lClient.setText( Msg.translate( Env.getCtx(),"AD_Client_ID" ));
        //lIgnoreError.setText( Msg.translate( Env.getCtx(),"IgnoreIfError" ));

        //

        bDelete.setText( Msg.getMsg( Env.getCtx(),"Delete" ));
        bDelete.addActionListener( this );

        //

        //this.add( vIterations,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        //this.add( lIterations,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.add( bDelete,new GridBagConstraints( 0,3,2,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.add( lClient,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        this.add( cbClient,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        //this.add( lIgnoreError,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        //this.add( cbIgnoreError,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // Fill Client

        cbClient.addItem( new KeyNamePair( -1,"" ));

        String sql = "SELECT Name, AD_Client_ID " + "FROM AD_Client " + "WHERE IsActive='Y' AND AD_Client_ID != 0 " + "ORDER BY 2";

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
        }

        vIterations.setValue( new Integer( 1 ));
        //cbIgnoreError.setSelected( true );

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
        System.out.println( "DeleteClientDialog.init" );
        m_WindowNo = WindowNo;
        m_frame    = frame;
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );

        try {
            jbInit();
            dynInit();
            frame.getContentPane().add( this,BorderLayout.CENTER );
            frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
        } catch( Exception ex ) {
            System.out.println( "DeleteClientDialog.init " + ex );
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
        boolean     imp          = ( e.getSource() == bDelete );
        KeyNamePair AD_Client    = ( KeyNamePair )cbClient.getSelectedItem();
        int         AD_Client_ID = -1;

        if( AD_Client != null ) {
            AD_Client_ID = AD_Client.getKey();
        }

        // Prepare Process

        int process = DB.getSQLValue( null,"SELECT AD_Process_ID FROM AD_Process WHERE value='DeleteClient'" );

        if( (process != -1) && (AD_Client_ID != -1) && (AD_Client_ID != 0) ) {

            //

            statusBar.setStatusLine( "Borrando..." );
            this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

            MPInstance instance = new MPInstance( Env.getCtx(),process,0,null );

            if( !instance.save()) {
                return;
            }

            ProcessInfo pi = new ProcessInfo( "Crear Precios",process );

            pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

            // Add Parameter - AD_Client_ID=Y

            MPInstancePara ip = new MPInstancePara( instance,10 );

            ip.setParameter( "AD_Client_ID",String.valueOf( AD_Client_ID ));

            if( !ip.save()) {
                return;
            }

            // Add Parameter - Iterations=x





            // Add Parameter - AD_Org_ID=x


            if( !ip.save()) {
                return;
            }

            // Execute Process

            ProcessCtl worker = new ProcessCtl( this,pi,null );

            worker.start();

            //

            this.setCursor( Cursor.getDefaultCursor());
        } else {
            System.out.println( "Error en Proceso o Compañía" );
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void lockUI( ProcessInfo pi ) {
        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        this.setEnabled( false );
    }    // lockUI

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void unlockUI( ProcessInfo pi ) {
        this.setEnabled( true );
        this.setCursor( Cursor.getDefaultCursor());

        //

        dispose();
    }    // unlockUI

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUILocked() {
        return this.isEnabled();
    }    // isUILocked

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void executeASync( ProcessInfo pi ) {}    // executeASync
}    // DeleteClientDialog



/*
 *  @(#)DeleteClientDialog.java   02.07.07
 *
 *  Fin del fichero DeleteClientDialog.java
 *
 *  Versión 2.2
 *
 */
