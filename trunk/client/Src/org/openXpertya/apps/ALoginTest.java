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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.compiere.plaf.CompierePLAF;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ALoginTest extends JDialog implements ActionListener,Runnable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param host
     * @param dbName
     * @param port
     * @param uid
     * @param pwd
     */

    public ALoginTest( Dialog frame,String host,String dbName,String port,String uid,String pwd ) {
        super( frame,"Connect Test: " + host,true );
        m_host   = host;
        m_dbName = dbName;
        m_port   = port;
        m_uid    = uid;
        m_pwd    = pwd;

        try {
            jbInit();
            pack();
        } catch( Exception ex ) {
            inform( "Internal Error = " + ex.getMessage());
        }

        // Start Tests

        try {
            m_worker = new Thread( this );
            m_worker.start();
        } catch( Exception e1 ) {
            inform( "Internal Error = " + e1 );
        }

        AEnv.showCenterScreen( this );
    }    // ALoginTest

    /** Descripción de Campos */

    private String m_host;

    /** Descripción de Campos */

    private String m_port;

    /** Descripción de Campos */

    private String m_dbName;

    /** Descripción de Campos */

    private String m_uid;

    /** Descripción de Campos */

    private String m_pwd;

    /** Descripción de Campos */

    private Thread m_worker;

    /** Descripción de Campos */

    private JPanel mainPanel = new JPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private JPanel southPanel = new JPanel();

    /** Descripción de Campos */

    private JButton bOK = new JButton();

    /** Descripción de Campos */

    private JScrollPane infoPane = new JScrollPane();

    /** Descripción de Campos */

    private JTextArea info = new JTextArea();

    /** Descripción de Campos */

    private FlowLayout southLayout = new FlowLayout();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        mainPanel.setLayout( mainLayout );
        bOK.setText( "Exit" );
        bOK.addActionListener( this );
        info.setBackground( CompierePLAF.getFieldBackground_Inactive());
        southPanel.setLayout( southLayout );
        southLayout.setAlignment( FlowLayout.RIGHT );
        infoPane.setPreferredSize( new Dimension( 400,400 ));
        getContentPane().add( mainPanel );
        mainPanel.add( southPanel,BorderLayout.SOUTH );
        southPanel.add( bOK,null );
        mainPanel.add( infoPane,BorderLayout.CENTER );
        infoPane.getViewport().add( info,null );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    private void inform( String text ) {
        System.out.println( text );
        info.append( text );
        info.append( "\n" );
        info.setCaretPosition( info.getText().length());
    }    // inform

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == bOK ) {
            while( (m_worker != null) && m_worker.isAlive()) {
                m_worker.interrupt();
            }

            dispose();
        }
    }    // actionEvent

    /**
     * Descripción de Método
     *
     */

    public void run() {
        String vmName    = System.getProperty( "java.vm.name" );
        String vmVersion = System.getProperty( "java.vm.version" );

        inform( "Using Java=" + vmName + " " + vmVersion );
        inform( "" );

        //

        boolean found     = false;
        boolean foundJDBC = false;

        inform( "*** Testing connection to Server: " + m_host + " ***" );

        if( (m_host == null) || (m_host.length() == 0) ) {
            inform( "ERROR: invalid host name" );

            return;
        }

        String host = m_host;

        inform( "Trying Echo - Port 7" );
        found = testHostPort( host,7 );
        inform( "Trying FTP - Port 21" );

        if( testHostPort( host,21 ) &&!found ) {
            found = true;
        }

        inform( "Trying HTTP - Port 80" );

        if( testHostPort( host,80 ) &&!found ) {
            found = true;
        }

        inform( "Trying Kerberos - Port 88" );

        if( testHostPort( host,88 ) &&!found ) {
            found = true;
        }

        inform( "Trying NetBios Session - Port 139" );

        if( testHostPort( host,139 ) &&!found ) {
            found = true;
        }

        inform( "Trying RMI - Port 1099" );

        if( testHostPort( host,1099 ) &&!found ) {
            found = true;
        }

        inform( "Trying Oracle Connection Manager - Port 1630" );

        if( testHostPort( host,1630 ) &&!found ) {
            found = true;
        }

        inform( "Trying Oracle JDBC - TCP Port 1521" );
        foundJDBC = testHostPort( host,1521 );

        int jdbcPort = 0;

        try {
            jdbcPort = Integer.parseInt( m_port );
        } catch( Exception e ) {
            inform( "ERROR: Cannot parse port=" + m_port );
            inform( e.getMessage());

            return;
        }

        if( jdbcPort != 1521 ) {
            inform( "Trying Oracle JDBC - TCP Port " + jdbcPort );

            if( testHostPort( host,jdbcPort ) &&!foundJDBC ) {
                foundJDBC = true;
            }
        }

        // Test Interrupt

        if( (m_worker != null) && m_worker.isInterrupted()) {
            return;
        }

        if( found && foundJDBC ) {
            inform( "*** Server found: " + host + " ***" );
            inform( "" );
        } else if( !found && foundJDBC ) {
            inform( "*** Server found: " + host + " (JDBC only) ***" );
            inform( "" );
        } else if( found &&!foundJDBC ) {
            inform( "ERROR: Server found: " + host + " - but no JDBC ***" );
            inform( "Make sure that the Oracle Listener process is active" );

            return;
        } else {
            inform( "ERROR: Server NOT found: " + host + "***" );
            inform( "End Test: Make sure that you can ping the Server" );

            return;
        }

        inform( "Connect to DB: " + m_dbName );
        inform( "Connect with entered parameters" );

        if( !testJDBC( host,jdbcPort,m_dbName,m_uid,m_pwd )) {
            if( (m_worker != null) && m_worker.isInterrupted()) {
                return;
            }

            if( jdbcPort != 1521 ) {
                inform( "Connect with standard JDBC port 1521" );

                if( testJDBC( host,1521,m_dbName,m_uid,m_pwd )) {
                    inform( "Please set port to 1521" );

                    return;
                }

                if( (m_worker != null) && m_worker.isInterrupted()) {
                    return;
                }
            }

            inform( "Connect with user system/manager" );

            if( testJDBC( host,1521,m_dbName,"system","manager" )) {
                inform( "Por favor, compruebe el Usuario openxp" );
                inform( ".... y cambie la contrasena de system" );

                return;
            }
        }

        inform( "*** OpenXpertya database found: " + host + ":" + jdbcPort + "/" + m_dbName + " ***" );

        if( (m_worker != null) && m_worker.isInterrupted()) {
            return;
        }

        inform( "" );
        inform( "Testing available application users:" );
        testOXPUsers( host,jdbcPort );
        inform( "" );
        inform( "*** Test complete **" );
    }    // run

    /**
     * Descripción de Método
     *
     *
     * @param host
     * @param port
     *
     * @return
     */

    private boolean testHostPort( String host,int port ) {
        Socket pingSocket = null;

        try {

            /* Resolve address. */

            InetAddress server = InetAddress.getByName( host );

            /* Establish socket. */

            pingSocket = new Socket( server,port );
        } catch( UnknownHostException e ) {
            inform( "  Unknown Host: " + e );
        } catch( IOException io ) {
            inform( "  IO Exception: " + io );
        }

        if( pingSocket != null ) {
            try {
                pingSocket.close();
            } catch( IOException e ) {
                inform( "  IO close exception: " + e );
            }

            inform( "  *** success ***" );

            return true;
        } else {
            return false;
        }
    }    // testHostPort

    /**
     * Descripción de Método
     *
     *
     * @param host
     * @param port
     * @param sid
     * @param uid
     * @param pwd
     *
     * @return
     */

    private boolean testJDBC( String host,int port,String sid,String uid,String pwd ) {
        boolean ok   = false;
        String  urlC = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + sid;

        try {
            inform( "  Trying Client connection URL=" + urlC + ", User=" + uid );

            Connection con = DriverManager.getConnection( urlC,uid,pwd );

            inform( "  - connected" );

            //

            DatabaseMetaData conMD = con.getMetaData();

            inform( "  - Driver Name:\t" + conMD.getDriverName());
            inform( "  - Driver Version:\t" + conMD.getDriverVersion());
            inform( "  - DB Name:\t" + conMD.getDatabaseProductName());
            inform( "  - DB Version:\t" + conMD.getDatabaseProductVersion());

            //

            con.close();
            inform( "  *** success ***" );
            ok = true;
        } catch( SQLException e ) {
            inform( "  ERROR: " + e.getMessage());
        }

        String urlS = "jdbc:oracle:oci8:@";

        try {
            inform( "  Trying Server connection URL=" + urlS + ", User=" + uid );

            Connection con = DriverManager.getConnection( urlS,uid,pwd );

            inform( "  - connected" );

            //

            DatabaseMetaData conMD = con.getMetaData();

            inform( "  - Driver Name:\t" + conMD.getDriverName());
            inform( "  - Driver Version:\t" + conMD.getDriverVersion());
            inform( "  - DB Name:\t" + conMD.getDatabaseProductName());
            inform( "  - DB Version:\t" + conMD.getDatabaseProductVersion());

            //

            con.close();
            inform( "  *** success ***" );
        } catch( SQLException e ) {
            inform( "  ERROR: " + e.getMessage());
        }

        return ok;
    }    // testJDBC

    /**
     * Descripción de Método
     *
     *
     * @param host
     * @param port
     */

    private void testOXPUsers( String host,int port ) {
        String sql  = "SELECT Name, Password FROM AD_User WHERE IsActive='Y'";
        String urlC = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + m_dbName;

        try {
            inform( "  - Client connection URL=" + urlC + ", User=" + m_uid );

            Connection con = DriverManager.getConnection( urlC,m_uid,m_pwd );

            inform( "  - connected" );

            Statement stmt = con.createStatement();

            inform( "  - statement created" );

            ResultSet rs = stmt.executeQuery( sql );

            inform( "  - query executed listing active application users:" );

            while( rs.next()) {
                String user     = rs.getString( 1 );
                String password = rs.getString( 2 );
                String answer   = ">>  User = " + user;

                if(( user.equals( "System" ) || user.equals( "SuperUser" )) && password.equals( "System" )) {
                    answer += "  with standard password (should be changed)";
                }

                inform( answer );
            }

            rs.close();
            inform( "  - query closed" );
            stmt.close();
            inform( "  - statement closed" );
            con.close();
            inform( "  - connection closed" );
        } catch( SQLException e ) {
            inform( "  ERROR: " + e.getMessage());
        }
    }    // testOXPUsers
}    // ALoginTest



/*
 *  @(#)ALoginTest.java   02.07.07
 * 
 *  Fin del fichero ALoginTest.java
 *  
 *  Versión 2.2
 *
 */
