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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
import org.openXpertya.print.ReportCtl;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.util.ASyncProcess;
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

public class ProcessDialog extends JFrame implements ActionListener,ASyncProcess {

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_Process_ID
     * @param isSOTrx
     */

    public ProcessDialog( int AD_Process_ID,boolean isSOTrx ) {
        super();
        log.info( "Process=" + AD_Process_ID + "; SOTrx=" + isSOTrx );
        enableEvents( AWTEvent.WINDOW_EVENT_MASK );
        m_AD_Process_ID = AD_Process_ID;
        m_WindowNo      = Env.createWindowNo( this );
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx",isSOTrx
                ?"Y"
                :"N" );
        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"",ex );
        }
        
        SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				try {
					PreparedStatement ps = ProcessParameter.GetProcessParameters(m_AD_Process_ID);
					ResultSet rs = ps.executeQuery();
					if (rs.next())
						runProcess();
					rs.close();
					ps.close();
				} catch (SQLException e) {
					
				}
			}
        });
    }    // ProcessDialog

    /** Descripción de Campos */

    private int m_AD_Process_ID;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private String m_Name = null;

    /** Descripción de Campos */

    private boolean m_IsReport = false;

    /** Descripción de Campos */

    private int[] m_ids = null;

    /** Descripción de Campos */

    private boolean m_isLocked = false;

    /** Descripción de Campos */

    private StringBuffer m_messageText = new StringBuffer();

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ProcessDialog.class );

    //

    /** Descripción de Campos */

    private CPanel dialog = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private JButton bOK = new JButton();

    /** Descripción de Campos */

    private FlowLayout southLayout = new FlowLayout();

    /** Descripción de Campos */

    private JEditorPane message = new JEditorPane();

    /** Descripción de Campos */

    private JScrollPane messagePane = new JScrollPane( message );

    /** Descripción de Campos */

    private CButton bPrint = new CButton();

    private boolean hasParams = false;
    
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        setIconImage( Env.getImage( "mProcess.gif" ));

        //

        dialog.setLayout( mainLayout );
        bOK.setHorizontalTextPosition( SwingConstants.LEFT );
        bOK.setIcon( Env.getImageIcon( "Ok24.gif" ));
        bOK.setDefaultCapable( true );
        bOK.addActionListener( this );
        bPrint.setIcon( Env.getImageIcon( "Print24.gif" ));
        bPrint.setMargin( new Insets( 2,2,2,2 ));
        bPrint.addActionListener( this );

        //

        southPanel.setLayout( southLayout );
        southLayout.setAlignment( FlowLayout.RIGHT );
        dialog.setPreferredSize( new Dimension( 500,150 ));
        message.setContentType( "text/html" );
        message.setEditable( false );
        message.setBackground( CompierePLAF.getFieldBackground_Inactive());
        message.setFocusable( false );
        getContentPane().add( dialog );
        dialog.add( southPanel,BorderLayout.SOUTH );
        southPanel.add( bPrint,null );
        southPanel.add( bOK,null );
        dialog.add( messagePane,BorderLayout.CENTER );

        //

        this.getRootPane().setDefaultButton( bOK );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    public void show() {
        super.show(); // stackOverflow con setVisible(true) TODO: verificar motivo
        bOK.requestFocus();
    }    // show

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        Env.clearWinContext( m_WindowNo );
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean init() {
        log.config( "" );

        //

        boolean trl = !Env.isBaseLanguage( Env.getCtx(),"AD_Process" );
        String  SQL = "SELECT Name, Description, Help, IsReport " + "FROM AD_Process " + "WHERE AD_Process_ID=?";

        if( trl ) {
            SQL = "SELECT t.Name, t.Description, t.Help, p.IsReport " + "FROM AD_Process p, AD_Process_Trl t " + "WHERE p.AD_Process_ID=t.AD_Process_ID" + " AND p.AD_Process_ID=? AND t.AD_Language=?";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,m_AD_Process_ID );

            if( trl ) {
                pstmt.setString( 2,Env.getAD_Language( Env.getCtx()));
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_Name     = rs.getString( 1 );
                m_IsReport = rs.getString( 4 ).equals( "Y" );

                //

                m_messageText.append( "<b>" );

                String s = rs.getString( 2 );    // Description

                if( rs.wasNull()) {
                    m_messageText.append( Msg.getMsg( Env.getCtx(),"StartProcess?" ));
                } else {
                    m_messageText.append( s );
                }

                m_messageText.append( "</b>" );
                s = rs.getString( 3 );    // Help

                if( !rs.wasNull()) {
                    m_messageText.append( "<p>" ).append( s ).append( "</p>" );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,SQL,e );

            return false;
        }

        if( m_Name == null ) {
            return false;
        }

        //

        this.setTitle( m_Name );
        message.setText( m_messageText.toString());
        bOK.setText( Msg.getMsg( Env.getCtx(),"Start" ));
        
        hasParams();

        return true;
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == bOK ) {
            if( bOK.getText().length() == 0 ) {
                dispose();
            } else {

                runProcess();
            }
        } else if( e.getSource() == bPrint ) {
            printScreen();
        }
    }    // actionPerformed

    private void runProcess() {
//    	 Similar to APanel.actionButton
        this.setVisible(false);
        ProcessInfo pi = new ProcessInfo( m_Name,m_AD_Process_ID );

        pi.setAD_User_ID( Env.getAD_User_ID( Env.getCtx()));
        pi.setAD_Client_ID( Env.getAD_Client_ID( Env.getCtx()));
        m_messageText.append( "<p>** " ).append( m_Name ).append( "</p>" );
        message.setText( m_messageText.toString());

        // Trx trx = Trx.get(Trx.createTrxName("ProcessDialog"), true);

        ProcessCtl.process( this,m_WindowNo,pi,null );
    }
    
    
    private void hasParams(){
		int cantParams = DB.getSQLValue(null,
				"SELECT count(*) FROM ad_process_para WHERE ad_process_id = ?",
				m_AD_Process_ID);
		setHasParams(cantParams > 0);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void lockUI( ProcessInfo pi ) {
        bOK.setText( "" );
        bOK.setEnabled( false );
        this.setEnabled( false );
        m_isLocked = true;
    }    // lockUI

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void unlockUI( ProcessInfo pi ) {
        ProcessInfoUtil.setLogFromDB( pi );
        m_messageText.append( "<font color=\"" ).append( pi.isError()
                ?"#FF0000"
                :"#0000FF" ).append( "\">** " ).append( pi.getSummary()).append( "</font>" );
        m_messageText.append( pi.getLogInfo( true ));
        message.setText( m_messageText.toString());
        message.setCaretPosition( message.getDocument().getLength());    // scroll down
        m_ids = pi.getIDs();

        //

        bOK.setEnabled( true );
        this.setEnabled( true );
        m_isLocked = false;
        
        // Si no hay nada para mostrar al usuario, no mostrar nada.
        if ((pi.getSummary() == null || pi.getSummary().equals("")) && pi.getLogInfo(true).equals(""))
        	bOK.doClick();
        else
        	this.setVisible(true);
        
        //

        afterProcessTask();

        // Close automatically

        if( m_IsReport &&!pi.isError()) {
            bOK.doClick();
        }
    }    // unlockUI

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUILocked() {
        return m_isLocked;
    }    // isLoacked

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void executeASync( ProcessInfo pi ) {
        log.config( "-" );
    }    // executeASync

    /**
     * Descripción de Método
     *
     */

    private void afterProcessTask() {

        // something to do?

        if( (m_ids != null) && (m_ids.length > 0) ) {
            log.config( "" );

            // Print invoices

            if( m_AD_Process_ID == 119 ) {
                printInvoices();
            } else if( m_AD_Process_ID == 118 ) {
                printShipments();
            }
        }
    }    // afterProcessTask

    /**
     * Descripción de Método
     *
     */

    private void printShipments() {
        if( m_ids == null ) {
            return;
        }

        if( !ADialog.ask( m_WindowNo,this,"PrintShipments" )) {
            return;
        }

        m_messageText.append( "<p>" ).append( Msg.getMsg( Env.getCtx(),"PrintShipments" )).append( "</p>" );
        message.setText( m_messageText.toString());

        int retValue = ADialogDialog.A_CANCEL;

        do {

            // Loop through all items

            for( int i = 0;i < m_ids.length;i++ ) {
                int M_InOut_ID = m_ids[ i ];

                ReportCtl.startDocumentPrint( ReportEngine.SHIPMENT,M_InOut_ID,true );
            }

            ADialogDialog d = new ADialogDialog( this,Env.getHeader( Env.getCtx(),m_WindowNo ),Msg.getMsg( Env.getCtx(),"PrintoutOK?" ),JOptionPane.QUESTION_MESSAGE );

            retValue = d.getReturnCode();
        } while( retValue == ADialogDialog.A_CANCEL );
    }    // printInvoices

    /**
     * Descripción de Método
     *
     */

    private void printInvoices() {
        if( m_ids == null ) {
            return;
        }

        if( !ADialog.ask( m_WindowNo,this,"PrintInvoices" )) {
            return;
        }

        m_messageText.append( "<p>" ).append( Msg.getMsg( Env.getCtx(),"PrintInvoices" )).append( "</p>" );
        message.setText( m_messageText.toString());

        int retValue = ADialogDialog.A_CANCEL;

        do {

            // Loop through all items

            for( int i = 0;i < m_ids.length;i++ ) {
                int AD_Invoice_ID = m_ids[ i ];

                ReportCtl.startDocumentPrint( ReportEngine.INVOICE,AD_Invoice_ID,true );
            }

            ADialogDialog d = new ADialogDialog( this,Env.getHeader( Env.getCtx(),m_WindowNo ),Msg.getMsg( Env.getCtx(),"PrintoutOK?" ),JOptionPane.QUESTION_MESSAGE );

            retValue = d.getReturnCode();
        } while( retValue == ADialogDialog.A_CANCEL );
    }    // printInvoices

    /**
     * Descripción de Método
     *
     */

    private void printScreen() {
        PrintScreenPainter.printScreen( this );
    }    // printScreen

	public void setHasParams(boolean hasParams) {
		this.hasParams = hasParams;
	}

	public boolean isHasParams() {
		return hasParams;
	}
}    // ProcessDialog



/*
 *  @(#)ProcessDialog.java   02.07.07
 * 
 *  Fin del fichero ProcessDialog.java
 *  
 *  Versión 2.2
 *
 */
