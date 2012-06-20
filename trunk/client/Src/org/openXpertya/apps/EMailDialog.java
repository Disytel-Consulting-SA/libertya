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
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class EMailDialog extends CDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param title
     * @param from
     * @param to
     * @param subject
     * @param message
     * @param attachment
     */

    public EMailDialog( Dialog owner,String title,MUser from,String to,String subject,String message,File attachment ) {
        super( owner,title,true );
        commonInit( from,to,subject,message,attachment );
    }    // EmailDialog

    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param title
     * @param from
     * @param to
     * @param subject
     * @param message
     * @param attachment
     */

    public EMailDialog( Frame owner,String title,MUser from,String to,String subject,String message,File attachment ) {
        super( owner,title,true );
        commonInit( from,to,subject,message,attachment );
    }    // EmailDialog

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param to
     * @param subject
     * @param message
     * @param attachment
     */

    private void commonInit( MUser from,String to,String subject,String message,File attachment ) {
        m_client = MClient.get( Env.getCtx());

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"EMailDialog",ex );
        }

        set( from,to,subject,message );
        setAttachment( attachment );
        AEnv.showCenterScreen( this );
    }    // commonInit

    /** Descripción de Campos */

    private MClient m_client = null;

    /** Descripción de Campos */

    private MUser m_from = null;

    //

    /** Descripción de Campos */

    private String m_to;

    /** Descripción de Campos */

    private String m_subject;

    /** Descripción de Campos */

    private String m_message;

    /** Descripción de Campos */

    private File m_attachFile;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( EMailDialog.class );

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel headerPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout headerLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CTextField fFrom = new CTextField( 20 );

    /** Descripción de Campos */

    private CTextField fTo = new CTextField( 20 );

    /** Descripción de Campos */

    private CTextField fSubject = new CTextField( 40 );

    /** Descripción de Campos */

    private CLabel lFrom = new CLabel();

    /** Descripción de Campos */

    private CLabel lTo = new CLabel();

    /** Descripción de Campos */

    private CLabel lSubject = new CLabel();

    /** Descripción de Campos */

    private CLabel lAttachment = new CLabel();

    /** Descripción de Campos */

    private CTextField fAttachment = new CTextField( 40 );

    /** Descripción de Campos */

    private JScrollPane messagePane = new JScrollPane();

    /** Descripción de Campos */

    private JTextPane fMessage = new JTextPane();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        lFrom.setText( Msg.getMsg( Env.getCtx(),"From" ) + ":" );
        lTo.setText( Msg.getMsg( Env.getCtx(),"To" ) + ":" );
        lSubject.setText( Msg.getMsg( Env.getCtx(),"Subject" ) + ":" );
        lAttachment.setText( Msg.getMsg( Env.getCtx(),"Attachment" ) + ":" );
        fFrom.setReadWrite( false );

        //

        mainPanel.setLayout( mainLayout );
        headerPanel.setLayout( headerLayout );
        mainLayout.setHgap( 5 );
        mainLayout.setVgap( 5 );
        messagePane.setPreferredSize( new Dimension( 150,150 ));
        getContentPane().add( mainPanel );
        mainPanel.add( headerPanel,BorderLayout.NORTH );
        headerPanel.add( lFrom,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,10,0,5 ),0,0 ));
        headerPanel.add( fFrom,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,10 ),0,0 ));
        headerPanel.add( lTo,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,10,0,5 ),0,0 ));
        headerPanel.add( fTo,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,0,10 ),0,0 ));
        headerPanel.add( lSubject,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,10,0,5 ),0,0 ));
        headerPanel.add( fSubject,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,10 ),1,0 ));
        headerPanel.add( lAttachment,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,10,0,5 ),0,0 ));
        headerPanel.add( fAttachment,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,10 ),1,0 ));
        mainPanel.add( messagePane,BorderLayout.CENTER );
        messagePane.getViewport().add( fMessage,null );

        //

        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        this.getContentPane().add( statusBar,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );
        statusBar.setStatusDB( null );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param to
     * @param subject
     * @param message
     */

    public void set( MUser from,String to,String subject,String message ) {

        // Content

        setFrom( from );
        setTo( to );
        setSubject( subject );
        setMessage( message );

        //

        statusBar.setStatusLine( m_client.getSMTPHost());
    }    // set

    /**
     * Descripción de Método
     *
     *
     * @param newTo
     */

    public void setTo( String newTo ) {
        m_to = newTo;
        fTo.setText( m_to );
    }    // setTo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTo() {
        m_to = fTo.getText();

        return m_to;
    }    // getTo

    /**
     * Descripción de Método
     *
     *
     * @param newFrom
     */

    public void setFrom( MUser newFrom ) {
        m_from = newFrom;
        fFrom.setText( m_from.getEMail());
    }    // setFrom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MUser getFrom() {
        return m_from;
    }    // getFrom

    /**
     * Descripción de Método
     *
     *
     * @param newSubject
     */

    public void setSubject( String newSubject ) {
        m_subject = newSubject;
        fSubject.setText( m_subject );
    }    // setSubject

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSubject() {
        m_subject = fSubject.getText();

        return m_subject;
    }    // getSubject

    /**
     * Descripción de Método
     *
     *
     * @param newMessage
     */

    public void setMessage( String newMessage ) {
        m_message = newMessage;
        fMessage.setText( m_message );
        fMessage.setCaretPosition( 0 );
    }    // setMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMessage() {
        m_message = fMessage.getText();

        return m_message;
    }    // getMessage

    /**
     * Descripción de Método
     *
     *
     * @param attachment
     */

    public void setAttachment( File attachment ) {
        m_attachFile = attachment;

        if( attachment == null ) {
            lAttachment.setVisible( false );
            fAttachment.setVisible( false );
        } else {
            lAttachment.setVisible( true );
            fAttachment.setVisible( true );
            fAttachment.setText( attachment.getName());
            fAttachment.setReadWrite( false );
        }
    }    // setAttachment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public File getAttachment() {
        return m_attachFile;
    }    // getAttachment

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
            confirmPanel.getOKButton().setEnabled( false );

            EMail em = new EMail( m_client,getFrom(),getTo(),getSubject(),getMessage());

            if( (m_attachFile != null) && m_attachFile.exists()) {
                em.addAttachment( m_attachFile );
            }

            String status = em.send();

            if( status.equals( EMail.SENT_OK )) {
                ADialog.info( 0,this,"MessageSent" );
                dispose();
            } else {
                ADialog.error( 0,this,"MessageNotSent",status );
            }

            //

            confirmPanel.getOKButton().setEnabled( false );
            setCursor( Cursor.getDefaultCursor());
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        }
    }    // actionPerformed
}    // VEMailDialog



/*
 *  @(#)EMailDialog.java   02.07.07
 * 
 *  Fin del fichero EMailDialog.java
 *  
 *  Versión 2.2
 *
 */
