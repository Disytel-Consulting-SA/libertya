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



package org.openXpertya.server;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MClient;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.EMailAuthenticator;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class EMailProcessor {

    /**
     * Constructor de la clase ...
     *
     *
     * @param client
     */

    public EMailProcessor( MClient client ) {
        this( client.getSMTPHost(),client.getRequestUser(),client.getRequestUserPW());
    }    // EMailProcessor

    /**
     * Constructor de la clase ...
     *
     *
     * @param host
     * @param user
     * @param password
     */

    public EMailProcessor( String host,String user,String password ) {
        m_host = host;
        m_user = user;
        m_pass = password;
    }    // EMail

    /** Descripción de Campos */

    private String m_host = null;

    /** Descripción de Campos */

    private String m_user = null;

    /** Descripción de Campos */

    private String m_pass = null;

    /** Descripción de Campos */

    private Session m_session = null;

    /** Descripción de Campos */

    private Store m_store = null;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private static final int ERROR = 0;

    /** Descripción de Campos */

    private static final int REQUEST = 1;

    /** Descripción de Campos */

    private static final int WORKFLOW = 2;

    /** Descripción de Campos */

    private static final int DELIVERY = 9;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int processMessages() {
        int processed = 0;

        try {
            getSession();
            getStore();
            processed = processInBox();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"processInBox",e );
        }

        // Cleanup

        try {
            if( m_store.isConnected()) {
                m_store.close();
            }
        } catch( Exception e ) {
        }

        m_store = null;

        return processed;
    }    // processMessages

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    private Session getSession() throws Exception {
        if( m_session != null ) {
            return m_session;
        }

        // Session

        Properties props = System.getProperties();

        props.put( "mail.store.protocol","smtp" );
        props.put( "mail.transport.protocol","smtp" );
        props.put( "mail.host",m_host );
        props.put( "mail.smtp.auth","true" );

        EMailAuthenticator auth = new EMailAuthenticator( m_user,m_pass );

        //

        m_session = Session.getDefaultInstance( props,auth );
        m_session.setDebug( CLogMgt.isLevelFinest());
        log.fine( "getSession - " + m_session );

        return m_session;
    }    // getSession

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    private Store getStore() throws Exception {
        if( m_store != null ) {
            return m_store;
        }

        if( getSession() == null ) {
            throw new IllegalStateException( "No Session" );
        }

        // Get IMAP Store

        m_store = m_session.getStore( "imap" );

        // Connect

        m_store.connect();

        //

        log.fine( "getStore - " + m_store );

        return m_store;
    }    // getStore

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    private int processInBox() throws Exception {

        // Folder

        Folder folder;

        folder = m_store.getDefaultFolder();

        if( folder == null ) {
            throw new IllegalStateException( "No default folder" );
        }

        // Open Inbox

        Folder inbox = folder.getFolder( "INBOX" );

        if( !inbox.exists()) {
            throw new IllegalStateException( "No Inbox" );
        }

        inbox.open( Folder.READ_WRITE );
        log.fine( "processInBox - " + inbox.getName() + "; Messages Total=" + inbox.getMessageCount() + "; New=" + inbox.getNewMessageCount());

        // Open Request

        Folder requestFolder = folder.getFolder( "CRequest" );

        if( !requestFolder.exists() &&!requestFolder.create( Folder.HOLDS_MESSAGES )) {
            throw new IllegalStateException( "Cannot create Request Folder" );
        }

        requestFolder.open( Folder.READ_WRITE );

        // Open Workflow

        Folder workflowFolder = folder.getFolder( "CWorkflow" );

        if( !workflowFolder.exists() &&!workflowFolder.create( Folder.HOLDS_MESSAGES )) {
            throw new IllegalStateException( "Cannot create Workflow Folder" );
        }

        workflowFolder.open( Folder.READ_WRITE );

        // Open Error

        Folder errorFolder = folder.getFolder( "OXPError" );

        if( !errorFolder.exists() &&!errorFolder.create( Folder.HOLDS_MESSAGES )) {
            throw new IllegalStateException( "Cannot create Error Folder" );
        }

        errorFolder.open( Folder.READ_WRITE );

        // Messages

        Message[] messages = inbox.getMessages();

        //

        int noProcessed = 0;
        int noError     = 0;

        for( int i = 0;i < messages.length;i++ )

//              for (int i = messages.length-1; i >= 0; i--)    //      newest first

        {
            Message msg    = messages[ i ];
            int     result = processMessage( msg );

            if( result == REQUEST ) {
                msg.setFlag( Flags.Flag.SEEN,true );
                msg.setFlag( Flags.Flag.ANSWERED,true );

                // Copy to processed

                requestFolder.appendMessages( new Message[]{ msg } );
            } else if( result == WORKFLOW ) {
                msg.setFlag( Flags.Flag.SEEN,true );
                msg.setFlag( Flags.Flag.ANSWERED,true );

                // Copy to processed

                workflowFolder.appendMessages( new Message[]{ msg } );
            } else if( result == DELIVERY ) {
                msg.setFlag( Flags.Flag.SEEN,true );
                msg.setFlag( Flags.Flag.ANSWERED,true );
            } else    // error
            {
                errorFolder.appendMessages( new Message[]{ msg } );
                noError++;
            }

            // Delete in InBox
//                      msg.setFlag(Flags.Flag.DELETED, true);
//                      Message[] deleted = inbox.expunge();
            //

            noProcessed++;
        }

        log.info( "processInBox - Total=" + noProcessed + " - Errors=" + noError );

        // Fini

        errorFolder.close( false );
        requestFolder.close( false );
        workflowFolder.close( false );

        //

        inbox.close( true );

        return noProcessed;
    }    // processInBox

    /**
     * Descripción de Método
     *
     *
     * @param msg
     *
     * @return
     *
     * @throws Exception
     */

    private int processMessage( Message msg ) throws Exception {
        dumpEnvelope( msg );
        dumpBody( msg );
        printOut( ":::::::::::::::" );
        printOut( getSubject( msg ));
        printOut( ":::::::::::::::" );
        printOut( getMessage( msg ));
        printOut( ":::::::::::::::" );

        String delivery = getDeliveryReport( msg );

        printOut( delivery );
        printOut( ":::::::::::::::" );

        // if (delivery != null)
        // return DELIVERY;

        // Unknown

        return ERROR;
    }    // processMessage

    /**
     * Descripción de Método
     *
     *
     * @param msg
     *
     * @return
     */

    private String getSubject( Message msg ) {
        try {
            String str = msg.getSubject();

            if( str != null ) {
                return str.trim();
            }
        } catch( MessagingException e ) {
            log.log( Level.SEVERE,"getSubject",e );
        }

        return "";
    }    // getSubject

    /**
     * Descripción de Método
     *
     *
     * @param msg
     *
     * @return
     */

    private String getMessage( Part msg ) {
        StringBuffer sb = new StringBuffer();

        try {

            // Text

            if( msg.isMimeType( "text/plain" )) {
                sb.append( msg.getContent());
            }

            // Other Text (e.g. html/xml)

            else if( msg.isMimeType( "text/*" )) {
                sb.append( msg.getContent());
            }

            // Nested

            else if( msg.isMimeType( "message/rfc822" )) {
                sb.append( msg.getContent());
            }

            // Multi Part Alternative

            else if( msg.isMimeType( "multipart/alternative" )) {
                String plainText  = null;
                String otherStuff = null;

                //

                Multipart mp    = ( Multipart )msg.getContent();
                int       count = mp.getCount();

                for( int i = 0;i < count;i++ ) {
                    Part   part    = mp.getBodyPart( i );
                    Object content = part.getContent();

                    if( (content == null) || (content.toString().trim().length() == 0) ) {
                        continue;
                    }

                    if( part.isMimeType( "text/plain" )) {
                        plainText = content.toString();
                    } else {
                        otherStuff = content.toString();
                    }
                }

                if( plainText != null ) {
                    sb.append( plainText );
                } else if( otherStuff != null ) {
                    sb.append( otherStuff );
                }
            }

            // Multi Part

            else if( msg.isMimeType( "multipart/*" )) {
                Multipart mp    = ( Multipart )msg.getContent();
                int       count = mp.getCount();

                for( int i = 0;i < count;i++ ) {
                    String str = getMessage( mp.getBodyPart( i ));

                    if( str.length() > 0 ) {
                        if( sb.length() > 0 ) {
                            sb.append( "\n-----\n" );
                        }

                        sb.append( str );
                    }
                }
            } else {

                /*
                 * If we actually want to see the data, and it's not a
                 * MIME type we know, fetch it and check its Java type.
                 */

                Object o = msg.getContent();

                if( o instanceof String ) {
                    sb.append( o );
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getMessage",e );
        }

        return sb.toString().trim();
    }    // getMessage

    /**
     * Descripción de Método
     *
     *
     * @param msg
     *
     * @return
     */

    private String getDeliveryReport( Part msg ) {
        try {
            if( msg.isMimeType( "multipart/report" )) {
                String deliveryMessage = null;
                String otherStuff      = null;

                //

                Multipart mp    = ( Multipart )msg.getContent();
                int       count = mp.getCount();

                for( int i = 0;i < count;i++ ) {
                    Part   part    = mp.getBodyPart( i );
                    Object content = part.getContent();

                    if( content == null ) {
                        continue;
                    }

                    if( part.isMimeType( "message/*" )) {
                        deliveryMessage = getDeliveredReportDetail( part );
                    } else {
                        otherStuff = content.toString().trim();
                    }
                }

                if( deliveryMessage != null ) {
                    return deliveryMessage;
                }

                return otherStuff;
            } else if( msg.isMimeType( "message/*" )) {
                return getDeliveredReportDetail( msg );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getDeliveryReport",e );
        }

        // Nothing

        return null;
    }    // getDeliveryReport

    /**
     * Descripción de Método
     *
     *
     * @param part
     *
     * @return
     *
     * @throws Exception
     */

    private String getDeliveredReportDetail( Part part ) throws Exception {
        Object content = part.getContent();

        if( content == null ) {
            return null;
        }

        String deliveryMessage = null;

        if( content instanceof InputStream ) {
            StringBuffer sb = new StringBuffer();
            InputStream  is = ( InputStream )content;
            int          c;

            while(( c = is.read()) != -1 ) {
                sb.append(( char )c );
            }

            deliveryMessage = sb.toString().trim();
        } else {
            deliveryMessage = content.toString().trim();
        }

        //

        if( deliveryMessage == null ) {
            return null;
        }

        // Final-Recipient: 
        int index = deliveryMessage.indexOf( "Final-Recipient:" );

        if( index != -1 ) {
            String finalRecipient = deliveryMessage.substring( index );
            int    atIndex        = finalRecipient.indexOf( "@" );

            if( atIndex != -1 ) {
                index = finalRecipient.lastIndexOf( ' ',atIndex );

                if( index != -1 ) {
                    finalRecipient = finalRecipient.substring( index + 1 );
                }

                atIndex = finalRecipient.indexOf( "@" );

                if( atIndex != -1 ) {
                    index = finalRecipient.indexOf( ' ',atIndex );
                }

                if( index != -1 ) {
                    finalRecipient = finalRecipient.substring( 0,index );
                }

                index = finalRecipient.indexOf( '\n' );

                if( index != -1 ) {
                    finalRecipient = finalRecipient.substring( 0,index );
                }

                return finalRecipient.trim();
            }
        }

        return deliveryMessage;
    }    // getDeliveredReportDetail

    /**
     * Descripción de Método
     *
     *
     * @param m
     *
     * @throws Exception
     */

    private void dumpEnvelope( Message m ) throws Exception {
        printOut( "-----------------------------------------------------------------" );

        Address[] a;

        // FROM

        if(( a = m.getFrom()) != null ) {
            for( int j = 0;j < a.length;j++ ) {
                printOut( "FROM: " + a[ j ].toString());
            }
        }

        // TO

        if(( a = m.getRecipients( Message.RecipientType.TO )) != null ) {
            for( int j = 0;j < a.length;j++ ) {
                printOut( "TO: " + a[ j ].toString());
            }
        }

        // SUBJECT

        printOut( "SUBJECT: " + m.getSubject());

        // DATE

        java.util.Date d = m.getSentDate();

        printOut( "SendDate: " + ( (d != null)
                                   ?d.toString()
                                   :"UNKNOWN" ));

        // FLAGS

        Flags        flags = m.getFlags();
        StringBuffer sb    = new StringBuffer();
        Flags.Flag[] sf    = flags.getSystemFlags();    // get the system flags
        boolean      first = true;

        for( int i = 0;i < sf.length;i++ ) {
            String     s;
            Flags.Flag f = sf[ i ];

            if( f == Flags.Flag.ANSWERED ) {
                s = "\\Answered";
            } else if( f == Flags.Flag.DELETED ) {
                s = "\\Deleted";
            } else if( f == Flags.Flag.DRAFT ) {
                s = "\\Draft";
            } else if( f == Flags.Flag.FLAGGED ) {
                s = "\\Flagged";
            } else if( f == Flags.Flag.RECENT ) {
                s = "\\Recent";
            } else if( f == Flags.Flag.SEEN ) {
                s = "\\Seen";
            } else {
                continue;    // skip it
            }

            if( first ) {
                first = false;
            } else {
                sb.append( ' ' );
            }

            sb.append( s );
        }

        String[] uf = flags.getUserFlags();    // get the user flag strings

        for( int i = 0;i < uf.length;i++ ) {
            if( first ) {
                first = false;
            } else {
                sb.append( ' ' );
            }

            sb.append( uf[ i ] );
        }

        printOut( "FLAGS: " + sb.toString());

        // X-MAILER

        String[] hdrs = m.getHeader( "X-Mailer" );

        if( hdrs != null ) {
            StringBuffer sb1 = new StringBuffer( "X-Mailer: " );

            for( int i = 0;i < hdrs.length;i++ ) {
                sb1.append( hdrs[ i ] ).append( "  " );
            }

            printOut( sb1.toString());
        } else {
            printOut( "X-Mailer NOT available" );
        }

        // Message ID

        hdrs = m.getHeader( "Message-ID" );

        if( hdrs != null ) {
            StringBuffer sb1 = new StringBuffer( "Message-ID: " );

            for( int i = 0;i < hdrs.length;i++ ) {
                sb1.append( hdrs[ i ] ).append( "  " );
            }

            printOut( sb1.toString());
        } else {
            printOut( "Message-ID NOT available" );
        }

        // All

        printOut( "ALL HEADERs:" );

        Enumeration en = m.getAllHeaders();

        while( en.hasMoreElements()) {
            Header hdr = ( Header )en.nextElement();

            printOut( "  " + hdr.getName() + " = " + hdr.getValue());
        }

        printOut( "-----------------------------------------------------------------" );
    }    // printEnvelope

    /**
     * Descripción de Método
     *
     *
     * @param p
     *
     * @throws Exception
     */

    private void dumpBody( Part p ) throws Exception {

        // http://www.iana.org/assignments/media-types/

        printOut( "=================================================================" );
        printOut( "CONTENT-TYPE: " + p.getContentType());

        if( p.isMimeType( "text/plain" )) {
            printOut( "Plain text ---------------------------" );
            System.out.println(( String )p.getContent());
        } else if( p.getContentType().toUpperCase().startsWith( "TEXT" )) {
            printOut( "Other text ---------------------------" );
            System.out.println(( String )p.getContent());
        } else if( p.isMimeType( "multipart/*" )) {
            printOut( "Multipart ---------------------------" );

            Multipart mp    = ( Multipart )p.getContent();
            int       count = mp.getCount();

            for( int i = 0;i < count;i++ ) {
                dumpBody( mp.getBodyPart( i ));
            }
        } else if( p.isMimeType( "message/rfc822" )) {
            printOut( "Nested ---------------------------" );
            dumpBody(( Part )p.getContent());
        } else {

            /*
             * If we actually want to see the data, and it's not a
             * MIME type we know, fetch it and check its Java type.
             */

            Object o = p.getContent();

            if( o instanceof String ) {
                printOut( "This is a string ---------------------------" );
                System.out.println(( String )o );
            } else if( o instanceof InputStream ) {
                printOut( "This is just an input stream ---------------------------" );

                InputStream is = ( InputStream )o;
                int         c;

                while(( c = is.read()) != -1 ) {
                    System.out.write( c );
                }
            } else {
                printOut( "This is an unknown type ---------------------------" );
                printOut( o.toString());
            }
        }

        printOut( "=================================================================" );
    }    // printBody

    /**
     * Descripción de Método
     *
     *
     * @param s
     */

    private static void printOut( String s ) {

        // System.out.print(indentStr.substring(0, level * 2));

        System.out.println( s );
    }

    /**
     * Descripción de Método
     *
     *
     * @param arg
     */

    public static void main( String[] arg ) {
        OpenXpertya.startupEnvironment( true );

        EMailProcessor m = new EMailProcessor( "admin","test","testOXP" );

        m.processMessages();

      

    }    // main
}    // EMailProcessor



/*
 *  @(#)EMailProcessor.java   24.03.06
 * 
 *  Fin del fichero EMailProcessor.java
 *  
 *  Versión 2.2
 *
 */
