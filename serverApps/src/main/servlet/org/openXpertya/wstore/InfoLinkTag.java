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

public class InfoLinkTag extends TagSupport {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private boolean m_oneLine = false;

    /**
     * Descripción de Método
     *
     *
     * @param var
     */

    public void setOneLine( String var ) {
        m_oneLine = "Y".equals( var );
    }    // setOneLine

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws JspException
     */

    public int doStartTag() throws JspException {
        HttpSession   session = pageContext.getSession();
        WebSessionCtx wsc     = WebSessionCtx.get( session,false );

        if( wsc == null ) {
            HttpServletRequest request = ( HttpServletRequest )pageContext.getRequest();

            JSPEnv.getCtx( request );    // creates wsc
            wsc = WebSessionCtx.get( session,false );
        }

        WebUser wu = ( WebUser )session.getAttribute( WebUser.NAME );

        if( (wu != null) && wu.isLoggedIn()) {
            if( wsc.ctx != null ) {
                Info info = ( Info )session.getAttribute( Info.NAME );

                if( (info == null) || (wu.getAD_User_ID() != info.getAD_User_ID())) {
                    session.setAttribute( Info.NAME,new Info( wsc.ctx,wu ));
                }
            }

            //
            // log.fine("doStartTag - WebUser exists - " + wu);
            //

            JspWriter out  = pageContext.getOut();
            HtmlCode  html = new HtmlCode();

            //

            ul ul = new ul();
            
            /*
            menuIndex( html, ul );

            if( wu.isCustomer()) {
                menuCustomer( html, ul );
            }        
            
            if( wu.isSalesRep()) {
                menuSalesRep( html, ul );
            }

            if( wu.isEmployee() || wu.isSalesRep()) {
                menuUser( html, wu.isEmployee(), ul);
            }

            menuAll( html, ul );

            menuContact( html, ul );
            */

            defaultMenu( wu, ul);
            
            //
            
            html.addElement( ul );

            html.output( out );
        } else {
            if( CLogMgt.isLevelFiner()) {
                log.fine( "doStartTag - no WebUser" );
            }

            if( session.getAttribute( Info.NAME ) == null ) {
                session.setAttribute( Info.NAME,Info.getGeneral());
            }
        }

        return( SKIP_BODY );
    }    // doStartTag

    /**
     * Descripción de Método
     *
     *
     * @param main
     */

    private void defaultMenu( WebUser wu, ul main ) { 
    	li menu1 = new li();
    	ul sub1 = new ul();
   	
    	// Index
    	a a = new a( "index.jsp" );
    	a.addElement( "Buscar productos" );
    	a.addAttribute( "title", "Buscar productos" );
    	sub1.addElement( new li().addElement( a ) );

    	if( wu.isCustomer()) {
		/*
    		// Proposals
        	a = new a( "proposals.jsp" );
        	a.addElement( "Presupuestos" );
        	a.addAttribute( "title", "Presupuestos" );
        	sub1.addElement( new li().addElement( a ) );

        	// Requisitions
        	a = new a( "requisitions.jsp" );
        	a.addElement( "Solicitudes" );
        	a.addAttribute( "title", "Solicitudes" );
        	sub1.addElement( new li().addElement( a ) );    
		*/

        	// Orders
        	a = new a( "orders.jsp" );
        	a.addElement( "Pedidos" );
        	a.addAttribute( "title", "Pedidos" );
        	sub1.addElement( new li().addElement( a ) );

        	// Invoices
        	a = new a( "invoices.jsp" );
        	a.addElement( "Facturas" );
        	a.addAttribute( "title", "Facturas" );
        	sub1.addElement( new li().addElement( a ) );

        	// Payments
        	a = new a( "payments.jsp" );
        	a.addElement( "Pagos" );
        	a.addAttribute( "title", "Pagos" );
        	sub1.addElement( new li().addElement( a ) );

        	// Invoice Schedule
        	a = new a( "invoiceSchedule.jsp" );
        	a.addElement( "Vencimientos" );
        	a.addAttribute( "title", "Vencimientos" );
        	sub1.addElement( new li().addElement( a ) );

        	// Shipments
        	a = new a( "shipments.jsp" );
        	a.addElement( "Env&iacute;os" );
        	a.addAttribute( "title", "Env&iacute;os" );
        	sub1.addElement( new li().addElement( a ) );	
    	}
    	
    	if( wu.isSalesRep()) {
	    	// C.Invoices
	    	a = new a( "commissionedInvoices.jsp" );
	    	a.addElement( "Autofacturas" );
	    	a.addAttribute( "title", "Autofacturas" );
	    	sub1.addElement( new li().addElement( a ) );
    	}
    	
    	menu1.addElement( "Men&uacute; principal" );
    	menu1.addElement( sub1 );
    	main.addElement( menu1 );

    	li menu2 = new li();
    	ul sub2 = new ul();
    	
    	if( wu.isEmployee() ) {
	    	// Notices
	    	a = new a( "notes.jsp" );
	    	a.addElement( "Noticias" );
	    	a.addAttribute( "title", "Noticias" );
	    	sub2.addElement( new li().addElement( a ) );
    	}

    	if( wu.isSalesRep()) {
	    	// Assigned Requests
	    	a = new a( "requests_sr.jsp" );
	    	a.addElement( "Mensajes asignados" );
	    	a.addAttribute( "title", "Mensajes asignados" );
	    	sub2.addElement( new li().addElement( a ) );  
    	}
	    	
    	a = new a( "requests.jsp" );
    	a.addElement( "Mis mensajes" );
    	a.addAttribute( "title", "Mis mensajes" );
    	sub2.addElement( new li().addElement( a ) );

    	// Interest Area
    	a = new a( "info.jsp" );
    	a.addElement( "Boletines" );
    	a.addAttribute( "title", "Boletines" );
    	sub2.addElement( new li().addElement( a ) );

    	// Contact
    	a = new a( "request.jsp?ForwardTo=/wstore/" );
    	a.addElement( "Contacto" );  
    	a.addAttribute( "title", "Contacto" );
    	sub2.addElement( new li().addElement( a ) );
        
    	menu2.addElement( "Mensajer&iacute;a" );
    	menu2.addElement( sub2 );
    	main.addElement( menu2 );
    }    // createMenu
    
    /**
     * Descripción de Método
     *
     *
     * @param html
     */

    private void menuIndex( HtmlCode html, ul ul1 ) {  	
    	// Index
        a a = new a( "index.jsp" );
        a.addElement( "Inicio" );
        a.addAttribute( "title", "Inicio" );
        
        ul1.addElement( new li().addElement( a ) );
    }    // menuIndex

    /**
     * Descripción de Método
     *
     *
     * @param html
     */

    private void menuContact( HtmlCode html, ul ul1 ) {
        // Contact
        a a = new a( "request.jsp?ForwardTo=/wstore/" );
        a.addElement( "Contacto" );  
        a.addAttribute( "title", "Contacto" );
        
        ul1.addElement( new li().addElement( a ) );
    }    // menuContact

    /**
     * Descripción de Método
     *
     *
     * @param html
     */

    private void menuCustomer( HtmlCode html, ul ul1 ) {
        ul ul2 = new ul();
    	
    	// Proposals
        a a = new a( "proposals.jsp" );
        a.addElement( "Presupuestos" );
        a.addAttribute( "title", "Presupuestos" );
        ul2.addElement( new li().addElement( a ) );        

    	// Requisitions
        a = new a( "requisitions.jsp" );
        a.addElement( "Solicitudes" );
        a.addAttribute( "title", "Solicitudes" );
        ul2.addElement( new li().addElement( a ) );    
        
    	// Orders
        a = new a( "orders.jsp" );
        a.addElement( "Pedidos" );
        a.addAttribute( "title", "Pedidos" );
        ul2.addElement( new li().addElement( a ) );
    	
        // Invoices
        a = new a( "invoices.jsp" );
        a.addElement( "Facturas" );
        a.addAttribute( "title", "Facturas" );
        ul2.addElement( new li().addElement( a ) );

        // Payments
        a = new a( "payments.jsp" );
        a.addElement( "Pagos" );
        a.addAttribute( "title", "Pagos" );
        ul2.addElement( new li().addElement( a ) );

        // Invoice Schedule
        a = new a( "invoiceSchedule.jsp" );
        a.addElement( "Vencimientos" );
        a.addAttribute( "title", "Vencimientos" );
        ul2.addElement( new li().addElement( a ) );        

        // Shipments
        a = new a( "shipments.jsp" );
        a.addElement( "Env&iacute;os" );
        a.addAttribute( "title", "Env&iacute;os" );
        ul2.addElement( new li().addElement( a ) );
       
        ul1.addElement( ul2 );    
    }    // menuCustomer

    /**
     * Descripción de Método
     *
     *
     * @param html
     */

    private void menuAll( HtmlCode html, ul ul1 ) {
    	ul ul2 = new ul();
    	
    	// Requests
        a a = new a( "requests.jsp" );
        a.addElement( "Mensajer&iacute;a" );
        a.addAttribute( "title", "Mensajer&iacute;a" );
        ul2.addElement( new li().addElement( a ) );

        // Interest Area
        a = new a( "info.jsp" );
        a.addElement( "Boletines" );
        a.addAttribute( "title", "Boletines" );
        ul2.addElement( new li().addElement( a ) );

        // Registration
        a = new a( "registrations.jsp" );
        a.addElement( "Registros" );
        a.addAttribute( "title", "Registros" );
        ul2.addElement( new li().addElement( a ) );

        ul1.addElement( ul2 );    
    }    // menuAll

    /**
     * Descripción de Método
     *
     *
     * @param html
     */

    private void menuSalesRep( HtmlCode html, ul ul1 ) {
    	ul ul2 = new ul();

        // C.Invoices
        a a = new a( "commissionedInvoices.jsp" );
        a.addElement( "Autofacturas" );
        a.addAttribute( "title", "Autofacturas" );
        ul2.addElement( new li().addElement( a ) );    	
    	
        // Assigned Requests
        a = new a( "requests_sr.jsp" );
        a.addElement( "Mensajes" );
        a.addAttribute( "title", "Mensajes" );
        ul2.addElement( new li().addElement( a ) );    

        ul1.addElement( ul2 );
    }    // menuAll

    /**
     * Descripción de Método
     *
     *
     * @param html
     * @param isEmployee
     */

    private void menuUser( HtmlCode html, boolean isEmployee, ul ul1 ) {
    	ul ul2 = new ul();
    	
    	// Notices
        if( isEmployee ) {
            a a = new a( "notes.jsp" );
            a.addElement( "Notas" );
            a.addAttribute( "title", "Notas" );
            ul1.addElement( new li().addElement( a ) );
        }

        // Expenses
        a a = new a( "expenses.jsp" );
        a.addElement( "Gastos" );
        a.addAttribute( "title", "Gastos" );
        ul2.addElement( new li().addElement( a ) );

        ul1.addElement( ul2 );
    }    // menuAll

    /**
     * Descripción de Método
     *
     *
     * @param html
     * @param hr
     */

    private void nl( HtmlCode html,boolean hr ) {
        if( m_oneLine ) {
            html.addElement( " - " );
        } else if( hr ) {
            html.addElement( new hr( "100%","left" ));
        } else {
            html.addElement( new br());
        }
    }    // nl

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
}    // InfoLinkTag



/*
 *  @(#)InfoLinkTag.java   29.03.06
 *
 *  Fin del fichero InfoLinkTag.java
 *
 *  Versión 2.2
 *
 */