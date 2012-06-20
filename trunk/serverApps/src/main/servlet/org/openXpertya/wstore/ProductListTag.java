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

public class ProductListTag extends TagSupport {

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
        int      AD_Client_ID        = Env.getAD_Client_ID( ctx );
        int      M_Product_Family_ID = Env.getM_Product_Family_ID( ctx );
        String   name                = "M_Product_Family_ID";
        option[] options             = getFamilies( AD_Client_ID,M_Product_Family_ID );
        select   sel                 = new select( name,options );

        sel.setID( "ID_" + name );
        log.info( "AD_Client_ID=" + AD_Client_ID + ", #=" + options.length );

        // Assemble

        HtmlCode html = new HtmlCode();

        sel.setClass("optionlist");
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
     * @param M_Product_Family_ID
     *
     * @return
     */

    private option[] getFamilies( int AD_Client_ID,int M_Product_Family_ID ) {
        option[] options = ( option[] )s_categories.get( new Integer( AD_Client_ID ));

        // if (options != null)
        // return options;

        int    index = 0;
        String sql   = "SELECT M_Product_Family_ID, Name " + "FROM M_Product_Family " + "WHERE AD_Client_ID=" + AD_Client_ID + " AND IsActive='Y' AND IsSelfService='Y' " + "ORDER BY Name";
        KeyNamePair[] pairs = DB.getKeyNamePairs( sql,true );

        options = new option[ pairs.length ];

        //

        for( int i = 0;i < pairs.length;i++ ) {
            if( i == 0 ) {
                options[ i ] = new option( "-1" );
                options[ i ].addElement( " " );
            } else {
                if( pairs[ i ].getKey() == M_Product_Family_ID ) {
                    index = i;
                }

                options[ i ] = new option( String.valueOf( pairs[ i ].getKey()));
                options[ i ].addElement( pairs[ i ].getName());
            }
        }

        //

        options[ index ].setSelected( true );
        s_categories.put( new Integer( AD_Client_ID ),options );

        return options;
    }    // getFamilies

    /** Descripción de Campos */

    static CCache s_categories = new CCache( "Product",10,60 );
}    // ProductListTag



/*
 *  @(#)ProductListTag.java   12.10.07
 *
 *  Fin del fichero ProductListTag.java
 *
 *  Versión 2.2
 *
 */