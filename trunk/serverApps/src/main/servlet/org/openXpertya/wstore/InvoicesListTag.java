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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.select;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.HtmlCode;
import org.openXpertya.util.KeyNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InvoicesListTag extends TagSupport {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

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
        int      AD_Client_ID     = Env.getAD_Client_ID( ctx );
        int      C_PaymentTerm_ID = Env.getC_PaymentTerm_ID( ctx );
        String   name             = "C_PaymentTerm_ID";
        option[] options          = getSchedules( AD_Client_ID,C_PaymentTerm_ID );
        select   sel              = new select( name,options );

        sel.setID( "ID_" + name );
        log.info( "AD_Client_ID=" + AD_Client_ID + ", #=" + options.length );

        // Assemble

        HtmlCode html = new HtmlCode();

        html.addElement( sel );

        JspWriter out = pageContext.getOut();

        html.output( out );

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
     * @param AD_Client_ID
     * @param C_PaymentTerm_ID
     *
     * @return
     */

    private option[] getSchedules( int AD_Client_ID,int C_PaymentTerm_ID ) {
        option[] options = ( option[] )s_categories.get( new Integer( AD_Client_ID ));
        int i;

        // if (options != null)
        // return options;

        int    index = 0;
        String sql   = "SELECT C_PaymentTerm_ID, Value" + " FROM C_PaymentTerm" + " ORDER BY Value";
        KeyNamePair[] pairs = DB.getKeyNamePairs( sql,true );

        options = new option[ pairs.length + 1 ];

        //

        for( i = 0;i < pairs.length;i++ ) {
            if( i == 0 ) {
                options[ i ] = new option( "-1" );
                options[ i ].addElement( " " );
            } else {
                if( pairs[ i ].getKey() == C_PaymentTerm_ID ) {
                    index = i;
                }

                options[ i ] = new option( String.valueOf( pairs[ i ].getKey()));
                options[ i ].addElement( pairs[ i ].getName());
            }
        }

        //

        options[ i ] = new option( "-2" );
        options[ i ].addElement( "Todas" );

        if( C_PaymentTerm_ID == -2 ) {
            options[ i ].setSelected( true );
        } else {
            options[ index ].setSelected( true );
        }

        s_categories.put( new Integer( AD_Client_ID ),options );

        return options;
    }    // getSchedules

    /** Descripción de Campos */

    static CCache s_categories = new CCache( "Schedules",10,60 );
}    // InvoicesListTag



/*
 *  @(#)InvoicesListTag.java   12.10.07
 * 
 *  Fin del fichero InvoicesListTag.java
 *  
 *  Versión 2.2
 *
 */
