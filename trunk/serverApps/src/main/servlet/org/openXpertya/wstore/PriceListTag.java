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

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.tagext.TagSupport;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PriceListTag extends TagSupport {

    /** Descripción de Campos */

    private int m_priceList_ID = 0;

    /** Descripción de Campos */

    private PriceList m_priceList;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @param var
     */

    public void setPriceList_ID( String var ) {
        try {
            m_priceList_ID = Integer.parseInt( var );
        } catch( NumberFormatException ex ) {
            log.warning( "setPriceList_ID - " + ex.toString());
        }
    }    // setM_PriceList_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws JspException
     */

    public int doStartTag() throws JspException {

        // Create Price List

        Properties ctx = JSPEnv.getCtx(( HttpServletRequest )pageContext.getRequest());
        int AD_Client_ID   = Env.getContextAsInt( ctx,"AD_Client_ID" );
        int M_PriceList_ID = m_priceList_ID;

        if( M_PriceList_ID == 0 ) {
            M_PriceList_ID = Env.getContextAsInt( ctx,"M_PriceList_ID" );
        }

        // Check Business Partner

        WebUser wu = ( WebUser )pageContext.getSession().getAttribute( WebUser.NAME );

        if( wu != null ) {
            int PriceList_ID = wu.getM_PriceList_ID();

            if( PriceList_ID != 0 ) {
                log.fine( "- using BP PriceList_ID=" + PriceList_ID );
                M_PriceList_ID = PriceList_ID;
            }
        }

        // Get Parameters

        String searchString    	= ctx.getProperty( ProductServlet.P_SEARCHSTRING );
        String productCategory 	= ctx.getProperty( ProductServlet.P_M_PRODUCT_CATEGORY_ID );
        String minimumPrice    	= ctx.getProperty( ProductServlet.P_MINIMUM_PRICE );
        String maximumPrice 	= ctx.getProperty( ProductServlet.P_MAXIMUM_PRICE );
        String inStock    		= ctx.getProperty( ProductServlet.P_IN_STOCK );
        String order 			= ctx.getProperty( ProductServlet.P_ORDER );
        
        // get price list

        m_priceList = PriceList.get( ctx,AD_Client_ID,M_PriceList_ID,searchString,productCategory,false,inStock,order,minimumPrice,maximumPrice );

        if( M_PriceList_ID == 0 ) {
            Env.setContext( ctx,"#M_PriceList_ID",m_priceList.getPriceList_ID());
        }

        // Set Price List

        HttpSession session = pageContext.getSession();

        session.setAttribute( PriceList.NAME,m_priceList );
        log.fine( "PL=" + m_priceList );

        // Set Locale from Price List

        String AD_Language = m_priceList.getAD_Language();

        if( (AD_Language == null) || (AD_Language.length() == 0) ) {
            AD_Language = "es_ES";
        }

        Config.set( session,Config.FMT_LOCALE,AD_Language );
        Config.set( session,Config.FMT_FALLBACK_LOCALE,"es_ES" );

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
}    // PriceListTag



/*
 *  @(#)PriceListTag.java   12.10.07
 * 
 *  Fin del fichero PriceListTag.java
 *  
 *  Versión 2.2
 *
 */
