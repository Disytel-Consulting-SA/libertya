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
import org.apache.ecs.xhtml.label;
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

public class ProductCategoryListTag extends TagSupport {

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
        int      AD_Client_ID = Env.getAD_Client_ID( ctx );
        String   name         = "M_Product_Category_ID";
        option[] options      = getCategories( AD_Client_ID );
        select   sel          = new select( name,options );

        sel.setID( "ID_" + name );
        log.fine( "AD_Client_ID=" + AD_Client_ID + ", #=" + options.length );
       
        // Assemble

        HtmlCode html = new HtmlCode();

        label label = new label();
        label.addElement( "Categor&iacute;a" );
        label.setFor( "ID_" + name );
        html.addElement( label );
        
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
     *
     * @return
     */

    private option[] getCategories( int AD_Client_ID ) {
        option[] options = ( option[] )s_categories.get( new Integer( AD_Client_ID ));

        if( options != null ) {
            return options;
        }

        String sql = "SELECT M_Product_Category_ID, Name " + "FROM M_Product_Category " + "WHERE AD_Client_ID=" + AD_Client_ID + " AND IsActive='Y' AND IsSelfService='Y' " + "ORDER BY Name";
        KeyNamePair[] pairs = DB.getKeyNamePairs( sql,true );

        options = new option[ pairs.length ];

        //

        for( int i = 0;i < pairs.length;i++ ) {
            if( i == 0 ) {
                options[ i ] = new option( "-1" );
                options[ i ].addElement( " " );
            } else {
                options[ i ] = new option( pairs[ i ].getID());
                options[ i ].addElement( pairs[ i ].getName());
            }
        }

        //

        s_categories.put( new Integer( AD_Client_ID ),options );

        return options;
    }    // getCountries

    /** Descripción de Campos */

    static CCache s_categories = new CCache( "ProductCategory",10,60 );
}    // ProductCategoryListTag



/*
 *  @(#)ProductCategoryListTag.java   12.10.07
 * 
 *  Fin del fichero ProductCategoryListTag.java
 *  
 *  Versión 2.2
 *
 */
