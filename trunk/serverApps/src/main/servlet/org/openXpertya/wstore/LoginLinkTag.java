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



package org.openXpertya.wstore;

import java.util.*;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.ecs.xhtml.*;

import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class LoginLinkTag extends TagSupport {

    /** Descripción de Campos */

    protected static CLogger log = CLogger.getCLogger( LoginLinkTag.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws JspException
     */

    public int doStartTag() throws JspException {
        Properties ctx = JSPEnv.getCtx(( HttpServletRequest )pageContext.getRequest());

        //

        WebUser wu = getWebUser( ctx );

        if( wu == null ) {
            pageContext.getSession().removeAttribute( WebUser.NAME );
        } else {
            pageContext.getSession().setAttribute( WebUser.NAME,wu );
        }

        //

        String serverContext = ctx.getProperty( JSPEnv.CTX_SERVER_CONTEXT );

        // log.fine("doStartTag - ServerContext=" + serverContext);

        HtmlCode html = null;

        if( (wu != null) && wu.isValid()) {
            html = getWelcomeLink( serverContext,wu );
        } else {
            html = getLoginLink( serverContext );
        }

        //

        JspWriter out = pageContext.getOut();

        html.output( out );

        //
        //

        return( SKIP_BODY );
    }    // doStartTag

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws JspException
     */

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }    // doEndTag

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    private WebUser getWebUser( Properties ctx ) {
        String address = pageContext.getRequest().getRemoteAddr();

        // Get stored User

        WebUser wu = ( WebUser )pageContext.getSession().getAttribute( WebUser.NAME );

        if( wu != null ) {
            log.finest( "(" + address + ") - SessionContext: " + wu );
        } else {
            wu = ( WebUser )pageContext.getAttribute( WebUser.NAME );

            if( wu != null ) {
                log.finest( "(" + address + ") - Context: " + wu );
            }
        }

        if( wu != null ) {
            return wu;
        }

        // Check Coockie

        String cookieUser = JSPEnv.getCookieWebUser(( HttpServletRequest )pageContext.getRequest());

        if( (cookieUser == null) || (cookieUser.trim().length() == 0) ) {
            log.finer( "(" + address + ") - no cookie" );
        } else {

            // Try to Load

            wu = WebUser.get( ctx,cookieUser );
            log.finer( "(" + address + ") - Cookie: " + wu );
        }

        if( wu != null ) {
            return wu;
        }

        //

        return null;
    }    // getWebUser

    /**
     * Descripción de Método
     *
     *
     * @param serverContext
     *
     * @return
     */

    private HtmlCode getLoginLink( String serverContext ) {
        HtmlCode retValue = new HtmlCode();

        // Login button

        input button = new input( input.TYPE_BUTTON,"Login","Identificarse" );

        button.setOnClick( "window.top.location.replace('https://" + serverContext + "/loginServlet');" );
        button.setClass( "button" );
        
        retValue.addElement( button );
        retValue.addElement( " " );

        return retValue;
    }    // getLoginLink

    /**
     * Descripción de Método
     *
     *
     * @param serverContext
     * @param wu
     *
     * @return
     */

    private HtmlCode getWelcomeLink( String serverContext,WebUser wu ) {
        HtmlCode retValue = new HtmlCode();

        if( wu.isLoggedIn()) {
            //

            String msg = "<strong>Bienvenido " + wu.getName() + "</strong>";

            // retValue.addElement( msg );

            //

            // retValue.addElement( " &nbsp; " );
            
            // Verify

            div wrap = new div();
            
            wrap.setClass( "loginmenu" );
            
        	div div = new div();
        	
        	div.addElement( msg );
        	div.setClass( "loginbutton" );
        	
        	wrap.addElement( div );

            img img = new img( "checkEmail.png" );
            
            a a = new a( "#" );
        	
            if( !wu.isEMailVerified()) {
                /*input button = new input( input.TYPE_BUTTON,"Verify","Verificar e-mail" );

                button.setOnClick( "window.top.location.replace('emailVerify.jsp');" );
                button.setClass( "button" );
                retValue.addElement( button );
                retValue.addElement( " " ); */           	
               
                img.setAlt( "Verificar correo electr&oacute;nico" );
                img.setTitle( "Verificar correo electr&oacute;nico" );
                img.setAlign( "absmiddle" );
                img.setBorder( 0 );         	

            	a.setOnClick( "window.top.location.replace('emailVerify.jsp');" );
                
            	a.addElement( img );
                a.addElement( "Verificar correo electr&oacute;nico" );
                a.addAttribute( "title", "Verificar correo electr&oacute;nico" );
            	
                div = new div();
                
            	div.addElement( a );
            	div.setClass( "loginbutton" );
           
            	wrap.addElement( div );
            }

            // Update

            /*input button = new input( input.TYPE_BUTTON,"Update","Ver cuenta" );

            button.setOnClick( "window.top.location.replace('login.jsp');" );
            button.setClass( "button" );
            retValue.addElement( button );
            retValue.addElement( " " );*/

            img = new img( "account.png" );

            img.setAlt( "Ver cuenta" );
            img.setTitle( "Ver cuenta" );
            img.setAlign( "absmiddle" );
            img.setBorder( 0 );
        	
        	a = new a( "#" );
        	a.setOnClick( "window.top.location.replace('login.jsp');" );
        	
        	a.addElement( img );
            a.addElement( "Ver cuenta" );
            a.addAttribute( "title", "Ver cuenta" );
        	
        	div = new div();
        	
        	div.addElement( a );
        	div.setClass( "loginbutton" );
       
        	wrap.addElement( div );
            
            // Logout

            /*button = new input( input.TYPE_BUTTON,"Logout","Salir" );
            button.setOnClick( "window.top.location.replace('loginServlet?mode=logout');" );
            button.setClass( "button" );            
            retValue.addElement( button );*/
        	
            img = new img( "logout.png" );

            img.setAlt( "Cerrar sesi&oacute;n" );
            img.setTitle( "Cerrar sesi&oacute;n" );
            img.setAlign( "absmiddle" );
            img.setBorder( 0 );
        	
        	a = new a( "#" );
        	a.setOnClick( "window.top.location.replace('loginServlet?mode=logout');" );
        	
        	a.addElement( img );
            a.addElement( "Cerrar sesi&oacute;n" );
            a.addAttribute( "title", "Cerrar sesi&oacute;n" );
        	
        	div = new div();
        	
        	div.addElement( a );
        	div.setClass( "loginbutton" );
       
        	wrap.addElement( div );
        	
        	retValue.addElement( wrap );
        	
        } else {
            input button = new input( input.TYPE_BUTTON,"Login","Identificarse" );

            button.setOnClick( "window.top.location.replace('https://" + serverContext + "/login.jsp');" );
            button.setClass( "button" );            
            retValue.addElement( button );
        }

        retValue.addElement( " " );

        //

        return retValue;
    }    // getWelcomeLink
}    // LoginLinkTag



/*
 *  @(#)LoginLinkTag.java   12.10.07
 *
 *  Fin del fichero LoginLinkTag.java
 *
 *  Versión 2.2
 *
 */
