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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WebSessionCtx implements Serializable {

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    public static WebSessionCtx get( HttpServletRequest request ) {
        return get( request.getSession( false ),false );
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param createNew
     *
     * @return
     */

    public static WebSessionCtx get( HttpServletRequest request,boolean createNew ) {
        HttpSession session = request.getSession( createNew );

        return get( session,createNew );
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param session
     * @param createNew
     *
     * @return
     */

    public static WebSessionCtx get( HttpSession session,boolean createNew ) {
        if( session == null ) {
            return null;
        }

        WebSessionCtx wsc = ( WebSessionCtx )session.getAttribute( NAME );

        // Create New

        if( (wsc == null) && createNew ) {
            wsc = new WebSessionCtx();
            session.setAttribute( NAME,wsc );
        }

        return wsc;
    }    // get

    /**
     * Constructor de la clase ...
     *
     */

    private WebSessionCtx() {
        ctx      = new Properties();
        language = Env.getLanguage( ctx );
    }    // WebSessionCtx

    /** Descripción de Campos */

    public static final String NAME = "WebSessionCtx";

    /** Descripción de Campos */

    public static int s_counter = 0;

    /** Descripción de Campos */

    public int counter = ++s_counter;

    /** Descripción de Campos */

    public Properties ctx = null;

    /** Descripción de Campos */

    public Language language = null;

    /** Descripción de Campos */

    public SimpleDateFormat dateFormat = null;

    /** Descripción de Campos */

    public SimpleDateFormat dateTimeFormat = null;

    /** Descripción de Campos */

    public DecimalFormat amountFormat = null;

    /** Descripción de Campos */

    public DecimalFormat integerFormat = null;

    /** Descripción de Campos */

    public DecimalFormat numberFormat = null;

    /** Descripción de Campos */

    public DecimalFormat quantityFormat = null;

    /** Descripción de Campos */

    public String loginInfo = "";

    /**
     * Descripción de Método
     *
     *
     * @param request
     */

    public void setLanguage( HttpServletRequest request ) {

        // Get Cookie

        Properties cProp = WebUtil.getCookieProprties( request );

        // Get/set Parameter:      Language

        String AD_Language = WebUtil.getParameter( request,Env.LANGUAGE );

        if( AD_Language == null ) {

            // Check Cookie

            AD_Language = cProp.getProperty( Env.LANGUAGE );

            if( AD_Language == null ) {

                // Check Request Locale

                Locale locale = request.getLocale();

                AD_Language = Language.getAD_Language( locale );
            }
        }

        if( AD_Language != null ) {
            Language lang = Language.getLanguage( AD_Language );

            Env.verifyLanguage( ctx,lang );
            Env.setContext( ctx,Env.LANGUAGE,lang.getAD_Language());
            Msg.getMsg( ctx,"0" );
            cProp.setProperty( Env.LANGUAGE,lang.getAD_Language());
            setLanguage( lang );
        } else if( language == null ) {    // set base language
            setLanguage( Language.getBaseLanguage());
        }
    }                                      // setLanguage

    /**
     * Descripción de Método
     *
     *
     * @param lang
     */

    private void setLanguage( Language lang ) {
        language = lang;

        //

        dateFormat     = DisplayType.getDateFormat( DisplayType.Date,language );
        dateTimeFormat = DisplayType.getDateFormat( DisplayType.DateTime,language );

        //

        amountFormat = DisplayType.getNumberFormat( DisplayType.Amount,language );
        integerFormat = DisplayType.getNumberFormat( DisplayType.Integer,language );
        numberFormat = DisplayType.getNumberFormat( DisplayType.Number,language );
        quantityFormat = DisplayType.getNumberFormat( DisplayType.Quantity,language );
    }    // setLanguage

    /**
     * Descripción de Método
     *
     *
     * @throws Throwable
     */

    protected void finalize() throws Throwable {
        super.finalize();
    }    // finalize

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "WSession#" + counter;
    }    // toString
}    // WSessionCtx



/*
 *  @(#)WebSessionCtx.java   02.07.07
 * 
 *  Fin del fichero WebSessionCtx.java
 *  
 *  Versión 2.2
 *
 */
