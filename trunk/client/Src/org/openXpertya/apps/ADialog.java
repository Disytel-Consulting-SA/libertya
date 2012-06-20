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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trace;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class ADialog {

    /** Descripción de Campos */

    public static boolean showDialog = true;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ADialog.class );

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param clearHeading
     * @param clearMessage
     * @param clearText
     */

    public static void info( int WindowNo,Container c,String clearHeading,String clearMessage,String clearText ) {
        log.info( clearHeading + ": " + clearMessage + " " + clearText );

        String out = clearMessage;

        if( (clearText != null) &&!clearText.equals( "" )) {
            out += "\n" + clearText;
        }

        //

        Window parent = Env.getParent( c );

        if( parent == null ) {
            parent = Env.getWindow( WindowNo );
        }

        //

        if( showDialog && (parent != null) ) {
            if( parent instanceof JFrame ) {
                new ADialogDialog(( JFrame )parent,clearHeading,out,JOptionPane.INFORMATION_MESSAGE );
            } else {
                new ADialogDialog(( JDialog )parent,clearHeading,out,JOptionPane.INFORMATION_MESSAGE );
            }
        } else {
            JOptionPane.showMessageDialog( parent,out + "\n",    // message
                clearHeading,                                    // title
                    JOptionPane.INFORMATION_MESSAGE );
        }
    }                                                            // info

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param AD_Message
     * @param msg
     */

    public static void info( int WindowNo,Container c,String AD_Message,String msg ) {
        log.info( AD_Message + " - " + msg );

        Properties   ctx = Env.getCtx();
        StringBuffer out = new StringBuffer();

        if( (AD_Message != null) &&!AD_Message.equals( "" )) {
            out.append( Msg.getMsg( ctx,AD_Message ));
        }

        if( (msg != null) && (msg.length() > 0) ) {
            out.append( "\n" ).append( msg );
        }

        //

        Window parent = Env.getParent( c );

        if( parent == null ) {
            parent = Env.getWindow( WindowNo );
        }

        //

        if( showDialog && (parent != null) ) {
            if( parent instanceof JFrame ) {
                new ADialogDialog(( JFrame )parent,Env.getHeader( ctx,WindowNo ),out.toString(),JOptionPane.INFORMATION_MESSAGE );
            } else {
                new ADialogDialog(( JDialog )parent,Env.getHeader( ctx,WindowNo ),out.toString(),JOptionPane.INFORMATION_MESSAGE );
            }
        } else {
            JOptionPane.showMessageDialog( parent,out.toString() + "\n",    // message
                Env.getHeader( ctx,WindowNo ),    // title
                    JOptionPane.INFORMATION_MESSAGE );
        }
    }                                             // info

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param AD_Message
     */

    public static void info( int WindowNo,Container c,String AD_Message ) {
        info( WindowNo,c,AD_Message,null );
    }    // info

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param AD_Message
     * @param msg
     */

    public static void warn( int WindowNo,Container c,String AD_Message,String msg ) {
        log.info( AD_Message + " - " + msg );

        Properties   ctx = Env.getCtx();
        StringBuffer out = new StringBuffer();

        if( (AD_Message != null) &&!AD_Message.equals( "" )) {
            out.append( Msg.getMsg( ctx,AD_Message ));
        }

        if( (msg != null) && (msg.length() > 0) ) {
            out.append( "\n" ).append( msg );
        }

        //

        Window parent = Env.getParent( c );

        if( parent == null ) {
            parent = Env.getWindow( WindowNo );
        }

        //

        if( showDialog && (parent != null) ) {
            if( parent instanceof JFrame ) {
                new ADialogDialog(( JFrame )parent,Env.getHeader( ctx,WindowNo ),out.toString(),JOptionPane.WARNING_MESSAGE );
            } else {
                new ADialogDialog(( JDialog )parent,Env.getHeader( ctx,WindowNo ),out.toString(),JOptionPane.WARNING_MESSAGE );
            }
        } else {
            JOptionPane.showMessageDialog( parent,out.toString() + "\n",    // message
                Env.getHeader( ctx,WindowNo ),    // title
                    JOptionPane.WARNING_MESSAGE );
        }
    }                                             // warn (int, String)

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param AD_Message
     */

    public static void warn( int WindowNo,Container c,String AD_Message ) {
        warn( WindowNo,c,AD_Message,null );
    }    // warn (int, String)

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param AD_Message
     * @param msg
     */

    public static void error( int WindowNo,Container c,String AD_Message,String msg ) {
        log.info( AD_Message + " - " + msg );

        if( CLogMgt.isLevelFinest()) {
            Trace.printStack();
        }

        Properties   ctx = Env.getCtx();
        StringBuffer out = new StringBuffer();

        if( (AD_Message != null) &&!AD_Message.equals( "" )) {
            out.append( Msg.getMsg( ctx,AD_Message ));
        }

        if( (msg != null) && (msg.length() > 0) ) {
            out.append( "\n" ).append( msg );
        }

        //

        Window parent = Env.getParent( c );

        if( parent == null ) {
            parent = Env.getWindow( WindowNo );
        }

        //

        if( showDialog && (parent != null) ) {
            if( parent instanceof JFrame ) {
                new ADialogDialog(( JFrame )parent,Env.getHeader( ctx,WindowNo ),out.toString(),JOptionPane.ERROR_MESSAGE );
            } else if( parent instanceof JDialog ) {
                new ADialogDialog(( JDialog )parent,Env.getHeader( ctx,WindowNo ),out.toString(),JOptionPane.ERROR_MESSAGE );
            }
        } else {
            JOptionPane.showMessageDialog( Env.getWindow( WindowNo ),out.toString() + "\n",    // message
                Env.getHeader( ctx,WindowNo ),    // title
                    JOptionPane.ERROR_MESSAGE );
        }
    }                                             // error (int, String)

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param AD_Message
     */

    public static void error( int WindowNo,Container c,String AD_Message ) {
        error( WindowNo,c,AD_Message,null );
    }    // error (int, String)

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param AD_Message
     * @param msg
     *
     * @return
     */

    public static boolean ask( int WindowNo,Container c,String AD_Message,String msg ) {
        return ask(WindowNo, c, AD_Message, null, msg);
    }    // ask
    
    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param AD_Message
     *
     * @return
     */

    public static boolean ask( int WindowNo,Container c,String AD_Message ) {
        return ask( WindowNo,c,AD_Message,null );
    }    // ask

    
    public static boolean ask( int WindowNo,Container c, String AD_Message, Object[] messageParams,String msg ) {
        log.info( AD_Message + " - " + msg );

        Properties   ctx = Env.getCtx();
        StringBuffer out = new StringBuffer();

        if( (AD_Message != null) &&!AD_Message.equals( "" )) {
        	if(messageParams != null && messageParams.length > 0){
                out.append( Msg.getMsg( ctx,AD_Message,messageParams ));
        	}
        	else{
                out.append( Msg.getMsg( ctx,AD_Message ));
        	}
        }

        if( (msg != null) && (msg.length() > 0) ) {
            out.append( "\n" ).append( msg );
        }

        //

        Window parent = Env.getParent( c );

        if( parent == null ) {
            parent = Env.getWindow( WindowNo );
        }

        boolean retValue = false;

        if( showDialog && (parent != null) ) {
            if( parent instanceof JFrame ) {
                ADialogDialog d = new ADialogDialog(( JFrame )parent,Env.getHeader( ctx,WindowNo ),out.toString(),JOptionPane.QUESTION_MESSAGE );

                retValue = d.getReturnCode() == ADialogDialog.A_OK;
            } else {
                ADialogDialog d = new ADialogDialog(( JDialog )parent,Env.getHeader( ctx,WindowNo ),out.toString(),JOptionPane.QUESTION_MESSAGE );

                retValue = d.getReturnCode() == ADialogDialog.A_OK;
            }
        } else {
            Object[] optionsOC = { Msg.getMsg( ctx,"OK" ),Msg.getMsg( ctx,"Cancel" )};
            int i = JOptionPane.showOptionDialog( parent,out.toString() + "\n",    // message
                Env.getHeader( ctx,WindowNo ),    // title
                JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,optionsOC,optionsOC[ 0 ] );

            retValue = i == JOptionPane.YES_OPTION;
        }

        return retValue;
    }    // ask
    
    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param ParseString
     */

    public static void clear( int WindowNo,Container c,String ParseString ) {
        log.info( "Dialog.clear: " + ParseString );

        Properties ctx   = Env.getCtx();
        String     parse = Env.parseContext( ctx,WindowNo,ParseString,false );

        if( parse.length() == 0 ) {
            parse = "ERROR parsing: " + ParseString;
        }

        //

        Window parent = Env.getParent( c );

        if( parent == null ) {
            parent = Env.getWindow( WindowNo );
        }

        //

        if( showDialog && (parent != null) ) {
            if( parent instanceof JFrame ) {
                new ADialogDialog(( JFrame )parent,Env.getHeader( ctx,WindowNo ),"=> " + parse,JOptionPane.INFORMATION_MESSAGE );
            } else {
                new ADialogDialog(( JDialog )parent,Env.getHeader( ctx,WindowNo ),"=> " + parse,JOptionPane.INFORMATION_MESSAGE );
            }
        } else {
            JOptionPane.showMessageDialog( parent,"=> " + parse + "\n",    // message
                Env.getHeader( ctx,WindowNo ),    // title
                    JOptionPane.INFORMATION_MESSAGE );
        }
    }                                             // clear

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param c
     * @param ParseString
     * @param condition
     */

    public static void clear( int WindowNo,Container c,String ParseString,boolean condition ) {
        if( !condition ) {
            return;
        }

        clear( WindowNo,c,ParseString );

        if( WindowNo == 0 ) {
            log.log( Level.SEVERE,"WIndowNo == 0" );
        }
    }    // clear

    /**
     * Descripción de Método
     *
     *
     * @param ParseString
     */

    public static void clear( String ParseString ) {
        clear( 0,null,ParseString );
    }    // clear

    /**
     * Descripción de Método
     *
     *
     * @param owner
     * @param subject
     * @param message
     */

    public static void createSupportEMail( Dialog owner,String subject,String message ) {
        log.config( "ADialog.createSupportEMail" );

        String to   = OpenXpertya.getSupportEMail();
        MUser  from = MUser.get( Env.getCtx(),Env.getAD_User_ID( Env.getCtx()));

        //

        StringBuffer myMessage = new StringBuffer( message );

        myMessage.append( "\n" );
        CLogMgt.getInfo( myMessage );
        CLogMgt.getInfoDetail( myMessage,Env.getCtx());

        EMailDialog emd = new EMailDialog( owner,Msg.getMsg( Env.getCtx(),"EMailSupport" ),from,to,"Support: " + subject,myMessage.toString(),null );
    }    // createEmail

    /**
     * Descripción de Método
     *
     *
     * @param owner
     * @param subject
     * @param message
     */

    public static void createSupportEMail( Frame owner,String subject,String message ) {
        log.config( "ADialog.createSupportEMail" );

        String to   = OpenXpertya.getSupportEMail();
        MUser  from = MUser.get( Env.getCtx(),Env.getAD_User_ID( Env.getCtx()));

        //

        StringBuffer myMessage = new StringBuffer( message );

        myMessage.append( "\n" );
        CLogMgt.getInfo( myMessage );
        CLogMgt.getInfoDetail( myMessage,Env.getCtx());

        EMailDialog emd = new EMailDialog( owner,Msg.getMsg( Env.getCtx(),"EMailSupport" ),from,to,"Support: " + subject,myMessage.toString(),null );
    }    // createEmail

    /**
     * Descripción de Método
     *
     */

    public static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }    // beep
}    // Dialog



/*
 *  @(#)ADialog.java   02.07.07
 * 
 *  Fin del fichero ADialog.java
 *  
 *  Versión 2.2
 *
 */
