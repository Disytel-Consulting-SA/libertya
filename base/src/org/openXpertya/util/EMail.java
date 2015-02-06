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



package org.openXpertya.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MUser;

import com.sun.mail.smtp.SMTPMessage;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class EMail implements Serializable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param client
     * @param userFrom
     * @param userTo
     * @param subject
     * @param message
     */

    public EMail( MClient client,MUser userFrom,MUser userTo,String subject,String message ) {
        this( client,userFrom,userTo.getEMail(),subject,message );
    }    // EMail

    /**
     * Constructor de la clase ...
     *
     *
     * @param client
     * @param userFrom
     * @param to
     * @param subject
     * @param message
     */

    public EMail( MClient client,MUser userFrom,String to,String subject,String message ) {
        this( client.getSMTPHost(),(userFrom == null)
                                   ?client.getRequestEMail()
                                   :userFrom.getEMail(),to,subject,message );

        if( client.isSmtpAuthorization()) {
            if( userFrom != null ) {
                createAuthenticator( userFrom.getEMailUser(),userFrom.getEMailUserPW());
            } else {
                createAuthenticator( client.getRequestUser(),client.getRequestUserPW());
            }
        }
        if (client.isUseSSL()) {
        	m_useSSL = true;
        }
    }    // EMail

    /**
     * Constructor de la clase ...
     *
     *
     * @param smtpHost
     * @param from
     * @param to
     * @param subject
     * @param message
     */

    public EMail( String smtpHost,String from,String to,String subject,String message ) {
        setSmtpHost( smtpHost );
        setFrom( from );
        addTo( to );

        if( (subject == null) || (subject.length() == 0) ) {
            setSubject( "." );    // pass validation
        } else {
            setSubject( subject );
        }

        if( (message != null) && (message.length() > 0) ) {
            setMessageText( message );
        }

        m_valid = isValid( true );
    }    // EMail

    
	/**
	 *	Full Constructor
	 *	@param ctx context
	 *  @param smtpHost The mail server
	 *  @param from Sender's EMail address
	 *  @param to   Recipient EMail address
	 *  @param subject  Subject of message
	 *  @param message  The message
	 *  @param html html email
	 */
	public EMail (Properties ctx, String smtpHost, String from, String to, 
		String subject, String message, boolean html)
	{
		setSmtpHost(smtpHost);
		setFrom(from);
		addTo(to);
		m_ctx = ctx;
		if (subject == null || subject.length() == 0)
			setSubject(".");	//	pass validation
		else
			setSubject (subject);
		if (message != null && message.length() > 0)
		{
			if (html)
				setMessageHTML(subject, message);
			else
				setMessageText (message);
		}
		m_valid = isValid (true);
	}	//	EMail
	
	/**
	 *	Full Constructor
	 *  @param client the client
	 *  @param from Sender's EMail address
	 *  @param to   Recipient EMail address
	 *  @param subject  Subject of message
	 *  @param message  The message
	 *  @param html
	 */
	public EMail (MClient client, String from, String to, 
		String subject, String message, boolean html)
	{
		this (client.getCtx(), client.getSMTPHost(), from, to, subject, message, html);
	}	//	EMail
	
	
    
    /** Descripción de Campos */

    public static final String CTX_EMAIL = "#User_EMail";

    /** Descripción de Campos */

    public static final String CTX_EMAIL_USER = "#User_EMailUser";

    /** Descripción de Campos */

    public static final String CTX_EMAIL_USERPW = "#User_EMailUserPw";

    /** Descripción de Campos */

    public static final String CTX_REQUEST_EMAIL = "#Request_EMail";

    /** Descripción de Campos */

    public static final String CTX_REQUEST_EMAIL_USER = "#Request_EMailUser";

    /** Descripción de Campos */

    public static final String CTX_REQUEST_EMAIL_USERPW = "#Request_EMailUserPw";

    /** Descripción de Campos */

    private InternetAddress m_from;

    
    /** Usar SSL en la autenticacion */
    protected boolean m_useSSL = false;
    
    /** Descripción de Campos */

    private ArrayList m_to;

    /** Descripción de Campos */

    private ArrayList m_cc;

    /** Descripción de Campos */

    private ArrayList m_bcc;

    /** Descripción de Campos */

    private InternetAddress m_replyTo;

    /** Descripción de Campos */

    private String m_subject;

    /** Descripción de Campos */

    private String m_messageText;

    /** Descripción de Campos */

    private String m_messageHTML;

    /** Descripción de Campos */

    private String m_smtpHost;

    /** Descripción de Campos */

    private ArrayList m_attachments;

    /** Descripción de Campos */

    private EMailAuthenticator m_auth = null;

    /** Descripción de Campos */

    private SMTPMessage m_msg = null;

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private boolean m_valid = false;

    /** Descripción de Campos */

    private String m_sentMsg = null;

    /** Descripción de Campos */

    public static final String SENT_OK = "OK";

    /** Descripción de Campos */

    protected static CLogger log = CLogger.getCLogger( EMail.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String send() {
        log.info( "(" + m_smtpHost + ") " + m_from + " -> " + m_to );
        m_sentMsg = null;

        //

        if( !isValid( true )) {
            m_sentMsg = "Invalid Data";

            return m_sentMsg;
        }

        //

        Properties props = System.getProperties();

        props.put( "mail.store.protocol","smtp" );
        props.put( "mail.transport.protocol","smtp" );
        props.put( "mail.host",m_smtpHost ); 

        //

        Session session = null;

        try {
            if( getAuthenticator() != null ) {
                props.put( "mail.smtp.auth","true" );
            }
            if (m_useSSL) {
                props.put("mail.smtp.starttls.enable","true");
            }
            session = Session.getInstance( props,getAuthenticator());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Auth=" + m_auth,e );
            m_sentMsg = e.toString();

            return e.toString();
        }

        session.setDebug( CLogMgt.isLevelFinest());

        try {

            // m_msg = new MimeMessage(session);

            m_msg = new SMTPMessage( session );

            // Addresses

            m_msg.setFrom( m_from );

            InternetAddress[] rec = getTos();

            if( rec.length == 1 ) {
                m_msg.setRecipient( Message.RecipientType.TO,rec[ 0 ] );
            } else {
                m_msg.setRecipients( Message.RecipientType.TO,rec );
            }

            rec = getCcs();

            if( (rec != null) && (rec.length > 0) ) {
                m_msg.setRecipients( Message.RecipientType.CC,rec );
            }

            rec = getBccs();

            if( (rec != null) && (rec.length > 0) ) {
                m_msg.setRecipients( Message.RecipientType.BCC,rec );
            }

            if( m_replyTo != null ) {
                m_msg.setReplyTo( new Address[]{ m_replyTo } );
            }

            //

            m_msg.setSentDate( new java.util.Date());
            m_msg.setHeader( "Comments","CorreoOxp" );

            // m_msg.setDescription("Description");
            // SMTP specifics

            m_msg.setAllow8bitMIME( true );

            // Send notification on Failure & Success - no way to set envid in Java yet
            // m_msg.setNotifyOptions (SMTPMessage.NOTIFY_FAILURE | SMTPMessage.NOTIFY_SUCCESS);
            // Bounce only header

            m_msg.setReturnOption( SMTPMessage.RETURN_HDRS );

            // m_msg.setHeader("X-Mailer", "msgsend");
            //

            setContent();
            m_msg.saveChanges();

            // log.fine("send - message =" + m_msg);
            //
            // Transport.send(msg);

            Transport t = session.getTransport( "smtp" );

            // log.fine("send - transport=" + t);

            t.connect();

            // t.connect(m_smtpHost, user, password);
            // log.fine("send - transport connected");

            Transport.send( m_msg );

            // t.sendMessage(msg, msg.getAllRecipients());

            log.fine( "Success - MessageID=" + m_msg.getMessageID());
        } catch( MessagingException me ) {
            Exception    ex      = me;
            StringBuffer sb      = new StringBuffer( "send(ME)" );
            boolean      printed = false;

            do {
                if( ex instanceof SendFailedException ) {
                    SendFailedException sfex    = ( SendFailedException )ex;
                    Address[]           invalid = sfex.getInvalidAddresses();

                    if( !printed ) {
                        if( (invalid != null) && (invalid.length > 0) ) {
                            sb.append( " - Invalid:" );

                            for( int i = 0;i < invalid.length;i++ ) {
                                sb.append( " " ).append( invalid[ i ] );
                            }
                        }

                        Address[] validUnsent = sfex.getValidUnsentAddresses();

                        if( (validUnsent != null) && (validUnsent.length > 0) ) {
                            sb.append( " - ValidUnsent:" );

                            for( int i = 0;i < validUnsent.length;i++ ) {
                                sb.append( " " ).append( validUnsent[ i ] );
                            }
                        }

                        Address[] validSent = sfex.getValidSentAddresses();

                        if( (validSent != null) && (validSent.length > 0) ) {
                            sb.append( " - ValidSent:" );

                            for( int i = 0;i < validSent.length;i++ ) {
                                sb.append( " " ).append( validSent[ i ] );
                            }
                        }

                        printed = true;
                    }

                    if( sfex.getNextException() == null ) {
                        sb.append( " " ).append( sfex.getLocalizedMessage());
                    }
                } else if( ex instanceof AuthenticationFailedException ) {
                    sb.append( " - Invalid Username/Password - " + m_auth );
                } else {
                    String msg = ex.getLocalizedMessage();

                    if( msg == null ) {
                        msg = ex.toString();
                    }

                    sb.append( ": " ).append( msg );
                }

                // Next Exception

                if( ex instanceof MessagingException ) {
                    ex = (( MessagingException )ex ).getNextException();
                } else {
                    ex = null;
                }
            } while( ex != null );

            //

            if( CLogMgt.isLevelFinest()) {
                log.log( Level.SEVERE,sb.toString(),me );
            } else {
                log.log( Level.SEVERE,sb.toString());
            }

            m_sentMsg = sb.toString();

            return sb.toString();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
            m_sentMsg = e.getLocalizedMessage();

            return e.getLocalizedMessage();
        }

        //

        if( CLogMgt.isLevelFinest()) {
            dumpMessage();
        }

        m_sentMsg = SENT_OK;

        return m_sentMsg;
    }    // send

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSentMsg() {
        return m_sentMsg;
    }    // getSentMsg

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSentOK() {
        return (m_sentMsg != null) && SENT_OK.equals( m_sentMsg );
    }    // isSentOK

    /**
     * Descripción de Método
     *
     */

    private void dumpMessage() {
        if( m_msg == null ) {
            return;
        }

        try {
            Enumeration e = m_msg.getAllHeaderLines();

            while( e.hasMoreElements()) {
                log.fine( "- " + e.nextElement());
            }
        } catch( MessagingException ex ) {
            log.log( Level.SEVERE,m_msg.toString(),ex );
        }
    }    // dumpMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected MimeMessage getMimeMessage() {
        return m_msg;
    }    // getMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMessageID() {
        try {
            if( m_msg != null ) {
                return m_msg.getMessageID();
            }
        } catch( MessagingException ex ) {
            log.log( Level.SEVERE,"",ex );
        }

        return null;
    }    // getMessageID

    /**
     * Descripción de Método
     *
     *
     * @param username
     * @param password
     *
     * @return
     */

    public EMailAuthenticator createAuthenticator( String username,String password ) {
        if( (username == null) || (password == null) ) {
            log.warning( "Ignored - " + username + "/" + password );
            m_auth = null;
        } else {

            // log.fine("setEMailUser: " + username + "/" + password);

            m_auth = new EMailAuthenticator( username,password );
        }

        return m_auth;
    }    // createAuthenticator

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private EMailAuthenticator getAuthenticator() {

        // already set

        if( m_auth != null ) {
            return m_auth;
        }

        //

        String     from = m_from.getAddress();
        Properties ctx  = (m_ctx == null)
                          ?Env.getCtx()
                          :m_ctx;

        // From Context

        String email = Env.getContext( ctx,CTX_EMAIL );
        String usr   = Env.getContext( ctx,CTX_EMAIL_USER );
        String pwd   = Env.getContext( ctx,CTX_EMAIL_USERPW );

        if( from.equals( email ) && (usr.length() > 0) && (pwd.length() > 0) ) {
            return createAuthenticator( usr,pwd );
        }

        // From Request

        email = Env.getContext( ctx,CTX_REQUEST_EMAIL );
        usr   = Env.getContext( ctx,CTX_REQUEST_EMAIL_USER );
        pwd   = Env.getContext( ctx,CTX_REQUEST_EMAIL_USERPW );

        if( from.equals( email ) && (usr.length() > 0) && (pwd.length() > 0) ) {
            return createAuthenticator( usr,pwd );
        }

        //

        return null;
    }    // setAuthenticator

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public InternetAddress getFrom() {
        return m_from;
    }    // getFrom

    /**
     * Descripción de Método
     *
     *
     * @param newFrom
     */

    public void setFrom( String newFrom ) {
        if( newFrom == null ) {
            m_valid = false;

            return;
        }

        try {
            m_from = new InternetAddress( newFrom,true );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
            m_valid = false;
        }
    }    // setFrom

    /**
     * Descripción de Método
     *
     *
     * @param newTo
     *
     * @return
     */

    public boolean addTo( String newTo ) {
        if( (newTo == null) || (newTo.length() == 0) ) {
            m_valid = false;

            return false;
        }

        InternetAddress ia = null;

        try {
            ia = new InternetAddress( newTo,true );
        } catch( Exception e ) {
            log.log( Level.SEVERE,newTo,e );
            m_valid = false;

            return false;
        }

        if( m_to == null ) {
            m_to = new ArrayList();
        }

        m_to.add( ia );

        return true;
    }    // addTo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public InternetAddress getTo() {
        if( (m_to == null) || (m_to.size() == 0) ) {
            return null;
        }

        InternetAddress ia = ( InternetAddress )m_to.get( 0 );

        return ia;
    }    // getTo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public InternetAddress[] getTos() {
        if( (m_to == null) || (m_to.size() == 0) ) {
            return null;
        }

        InternetAddress[] ias = new InternetAddress[ m_to.size()];

        m_to.toArray( ias );

        return ias;
    }    // getTos

    /**
     * Descripción de Método
     *
     *
     * @param newCc
     *
     * @return
     */

    public boolean addCc( String newCc ) {
        if( (newCc == null) || (newCc.length() == 0) ) {
            return false;
        }

        InternetAddress ia = null;

        try {
            ia = new InternetAddress( newCc,true );
        } catch( Exception e ) {
            log.log( Level.SEVERE,newCc,e );

            return false;
        }

        if( m_cc == null ) {
            m_cc = new ArrayList();
        }

        m_cc.add( ia );

        return true;
    }    // addCc

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public InternetAddress[] getCcs() {
        if( (m_cc == null) || (m_cc.size() == 0) ) {
            return null;
        }

        InternetAddress[] ias = new InternetAddress[ m_cc.size()];

        m_cc.toArray( ias );

        return ias;
    }    // getCcs

    /**
     * Descripción de Método
     *
     *
     * @param newBcc
     *
     * @return
     */

    public boolean addBcc( String newBcc ) {
        if( (newBcc == null) || (newBcc.length() == 0) ) {
            return false;
        }

        InternetAddress ia = null;

        try {
            ia = new InternetAddress( newBcc,true );
        } catch( Exception e ) {
            log.log( Level.SEVERE,newBcc,e );

            return false;
        }

        if( m_bcc == null ) {
            m_bcc = new ArrayList();
        }

        m_bcc.add( ia );

        return true;
    }    // addBcc

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public InternetAddress[] getBccs() {
        if( (m_bcc == null) || (m_bcc.size() == 0) ) {
            return null;
        }

        InternetAddress[] ias = new InternetAddress[ m_bcc.size()];

        m_bcc.toArray( ias );

        return ias;
    }    // getBccs

    /**
     * Descripción de Método
     *
     *
     * @param newTo
     *
     * @return
     */

    public boolean setReplyTo( String newTo ) {
        if( (newTo == null) || (newTo.length() == 0) ) {
            return false;
        }

        InternetAddress ia = null;

        try {
            ia = new InternetAddress( newTo,true );
        } catch( Exception e ) {
            log.log( Level.SEVERE,newTo,e );

            return false;
        }

        m_replyTo = ia;

        return true;
    }    // setReplyTo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public InternetAddress getReplyTo() {
        return m_replyTo;
    }    // getReplyTo

    /**
     * Descripción de Método
     *
     *
     * @param newSubject
     */

    public void setSubject( String newSubject ) {
        if( (newSubject == null) || (newSubject.length() == 0) ) {
            m_valid = false;
        } else {
            m_subject = newSubject;
        }
    }    // setSubject

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSubject() {
        return m_subject;
    }    // getSubject

    /**
     * Descripción de Método
     *
     *
     * @param newMessage
     */

    public void setMessageText( String newMessage ) {
        if( (newMessage == null) || (newMessage.length() == 0) ) {
            m_valid = false;
        } else {
            m_messageText = newMessage;

            if( !m_messageText.endsWith( "\n" )) {
                m_messageText += "\n";
            }
        }
    }    // setMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMessageCRLF() {
        if( m_messageText == null ) {
            return "";
        }

        char[]       chars = m_messageText.toCharArray();
        StringBuffer sb    = new StringBuffer();

        for( int i = 0;i < chars.length;i++ ) {
            char c = chars[ i ];

            if( c == '\n' ) {
                int previous = i - 1;

                if( (previous >= 0) && (chars[ previous ] == '\r') ) {
                    sb.append( c );
                } else {
                    sb.append( "\r\n" );
                }
            } else {
                sb.append( c );
            }
        }

        // log.fine("IN  " + m_messageText);
        // log.fine("OUT " + sb);

        return sb.toString();
    }    // getMessageCRLF

    /**
     * Descripción de Método
     *
     *
     * @param html
     */

    public void setMessageHTML( String html ) {
        if( (html == null) || (html.length() == 0) ) {
            m_valid = false;
        } else {
            m_messageHTML = html;

            if( !m_messageHTML.endsWith( "\n" )) {
                m_messageHTML += "\n";
            }
        }
    }    // setMessageHTML

    /**
     * Descripción de Método
     *
     *
     * @param subject
     * @param message
     */

    public void setMessageHTML( String subject,String message ) {
        m_subject = subject;

        StringBuffer sb = new StringBuffer( "<HTML>\n" ).append( "<HEAD>\n" ).append( "<TITLE>\n" ).append( subject + "\n" ).append( "</TITLE>\n" ).append( "</HEAD>\n" );

        sb.append( "<BODY>\n" ).append( "<H2>" + subject + "</H2>" + "\n" ).append( message ).append( "\n" ).append( "</BODY>\n" );
        sb.append( "</HTML>\n" );
        m_messageHTML = sb.toString();
    }    // setMessageHTML

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMessageHTML() {
        return m_messageHTML;
    }    // getMessageHTML

	/**
	 * Add a collection of attachments
	 * @param files collection of files
	 */
	public void addAttachments(Collection<File> files)
	{
		if (files == null || files.size() == 0)
			return;
		for (File f : files) {
			addAttachment(f);
		}
	}

    
    /**
     * Descripción de Método
     *
     *
     * @param file
     */

    public void addAttachment( File file ) {
        if( file == null ) {
            return;
        }

        if( m_attachments == null ) {
            m_attachments = new ArrayList();
        }

        m_attachments.add( file );
    }    // addAttachment

    /**
     * Descripción de Método
     *
     *
     * @param url
     */

    public void addAttachment( URL url ) {
        if( url == null ) {
            return;
        }

        if( m_attachments == null ) {
            m_attachments = new ArrayList();
        }

        m_attachments.add( url );
    }    // addAttachment

    /**
     * Descripción de Método
     *
     *
     * @param data
     * @param type
     * @param name
     */

    public void addAttachment( byte[] data,String type,String name ) {
        ByteArrayDataSource byteArray = new ByteArrayDataSource( data,type ).setName( name );

        addAttachment( byteArray );
    }    // addAttachment

    /**
     * Descripción de Método
     *
     *
     * @param dataSource
     */

    public void addAttachment( DataSource dataSource ) {
        if( dataSource == null ) {
            return;
        }

        if( m_attachments == null ) {
            m_attachments = new ArrayList();
        }

        m_attachments.add( dataSource );
    }    // addAttachment

    /**
     * Descripción de Método
     *
     *
     * @throws IOException
     * @throws MessagingException
     */

    private void setContent() throws MessagingException,IOException {
        m_msg.setSubject( getSubject());

        // Simple Message

        if( (m_attachments == null) || (m_attachments.size() == 0) ) {
            if( (m_messageHTML == null) || (m_messageHTML.length() == 0) ) {
                m_msg.setContent( getMessageCRLF(),"text/plain" );
            } else {
                m_msg.setDataHandler( new DataHandler( new ByteArrayDataSource( m_messageHTML,"text/html" )));
            }

            //

            log.fine( "(simple) " + getSubject());
        } else    // Multi part message      ***************************************
        {

            // First Part - Message

            MimeBodyPart mbp_1 = new MimeBodyPart();

            mbp_1.setText( "" );

            if( (m_messageHTML == null) || (m_messageHTML.length() == 0) ) {
                mbp_1.setContent( getMessageCRLF(),"text/plain" );
            } else {
                mbp_1.setDataHandler( new DataHandler( new ByteArrayDataSource( m_messageHTML,"text/html" )));
            }

            // Create Multipart and its parts to it

            Multipart mp = new MimeMultipart();

            mp.addBodyPart( mbp_1 );
            log.fine( "(multi) " + getSubject() + " - " + mbp_1 );

            // for all attachments

            for( int i = 0;i < m_attachments.size();i++ ) {
                Object     attachment = m_attachments.get( i );
                DataSource ds         = null;

                if( attachment instanceof File ) {
                    File file = ( File )attachment;

                    if( file.exists()) {
                        ds = new FileDataSource( file );
                    } else {
                        log.log( Level.SEVERE,"File does not exist: " + file );

                        continue;
                    }
                } else if( attachment instanceof URL ) {
                    URL url = ( URL )attachment;

                    ds = new URLDataSource( url );
                } else if( attachment instanceof DataSource ) {
                    ds = ( DataSource )attachment;
                } else {
                    log.log( Level.SEVERE,"Attachement type unknown: " + attachment );

                    continue;
                }

                // Attachment Part

                MimeBodyPart mbp_2 = new MimeBodyPart();

                mbp_2.setDataHandler( new DataHandler( ds ));
                mbp_2.setFileName( ds.getName());
                log.fine( "Added Attachment " + ds.getName() + " - " + mbp_2 );
                mp.addBodyPart( mbp_2 );
            }

            // Add to Message

            m_msg.setContent( mp );
        }    // multi=part
    }        // setContent

    /**
     * Descripción de Método
     *
     *
     * @param newSmtpHost
     */

    public void setSmtpHost( String newSmtpHost ) {
        if( (newSmtpHost == null) || (newSmtpHost.length() == 0) ) {
            m_valid = false;
        } else {
            m_smtpHost = newSmtpHost;
        }
    }    // setSMTPHost

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSmtpHost() {
        return m_smtpHost;
    }    // getSmtpHosr

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isValid() {
        return m_valid;
    }    // isValid

    /**
     * Descripción de Método
     *
     *
     * @param recheck
     *
     * @return
     */

    public boolean isValid( boolean recheck ) {
        if( !recheck ) {
            return m_valid;
        }

        // From

        if( (m_from == null) || (m_from.getAddress().length() == 0) || (m_from.getAddress().indexOf( ' ' ) != -1) ) {
            log.warning( "From is invalid=" + m_from );

            return false;
        }

        // To

        InternetAddress[] ias = getTos();

        if( ias == null ) {
            log.warning( "No To" );

            return false;
        }

        for( int i = 0;i < ias.length;i++ ) {
            if( (ias[ i ] == null) || (ias[ i ].getAddress().length() == 0) || (ias[ i ].getAddress().indexOf( ' ' ) != -1) ) {
                log.warning( "To(" + i + ") is invalid=" + ias[ i ] );

                return false;
            }
        }

        // Host

        if( (m_smtpHost == null) || (m_smtpHost.length() == 0) ) {
            log.warning( "SMTP Host is invalid" + m_smtpHost );

            return false;
        }

        // Subject

        if( (m_subject == null) || (m_subject.length() == 0) ) {
            log.warning( "Subject is invalid=" + m_subject );

            return false;
        }

        return true;
    }    // isValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "EMail[" );

        sb.append( "From:" ).append( m_from ).append( ",To:" ).append( getTo()).append( ",Subject=" ).append( getSubject()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startup( true );

        if( args.length != 5 ) {
            System.out.println( "Parameters: smtpHost from to subject message" );
            System.out.println( "Example: java org.openXpertya.util.EMail mail.oxp.com quiensea@openxpertya.org Hola Saludos" );
            System.exit( 1 );
        }

        EMail email = new EMail( args[ 0 ],args[ 1 ],args[ 2 ],args[ 3 ],args[ 4 ] );

        email.send();
    }    // main
}    // EMail



/*
 *  @(#)EMail.java   02.07.07
 * 
 *  Fin del fichero EMail.java
 *  
 *  Versión 2.2
 *
 */
