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
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.label;
import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.select;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.tr;
import org.apache.ecs.xhtml.br;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import org.openXpertya.model.MCountry;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MRegion;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.HtmlCode;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class LocationTag extends TagSupport {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private String m_countryID_el;

    /** Descripción de Campos */

    private String m_regionID_el;

    /** Descripción de Campos */

    private String m_regionName_el;

    /** Descripción de Campos */

    private String m_city_el;

    /** Descripción de Campos */

    private String m_postal_el;

    /** Descripción de Campos */

    private MCountry m_country;

    // CSS Classes

    /** Descripción de Campos */

    private static final String C_REQUIRED = "required";    
    
    /** Descripción de Campos */

    private static final String C_MANDATORY = "mandatory";

    /** Descripción de Campos */

    private static final String C_ERROR = "error";

    /**
     * Descripción de Método
     *
     *
     * @param info_el
     */

    public void setCountryID( String info_el ) {
        m_countryID_el = info_el;
    }    // setCountry

    /**
     * Descripción de Método
     *
     *
     * @param info_el
     */

    public void setRegionID( String info_el ) {
        m_regionID_el = info_el;
    }    // setRegion

    /**
     * Descripción de Método
     *
     *
     * @param info_el
     */

    public void setRegionName( String info_el ) {
        m_regionName_el = info_el;
    }    // setRegion

    /**
     * Descripción de Método
     *
     *
     * @param info_el
     */

    public void setCity( String info_el ) {
        m_city_el = info_el;
    }    // setCity

    /**
     * Descripción de Método
     *
     *
     * @param info_el
     */

    public void setPostal( String info_el ) {
        m_postal_el = info_el;
    }    // setPostal

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

        HtmlCode html = new HtmlCode();
        
        // Country         *******************************************************

        int C_Country_ID = 0;

        try {
            String info = ( String )ExpressionUtil.evalNotNull( "location","countryID",m_countryID_el,String.class,this,pageContext );

            if( (info != null) && (info.length() != 0) ) {
                C_Country_ID = Integer.parseInt( info );
            }
        } catch( Exception e ) {
            log.severe( "Country - " + e );
        }

        MLocation loc = new MLocation( ctx,0,null );

        if( C_Country_ID == 0 ) {
            C_Country_ID = loc.getC_Country_ID();    // default
        }

        //

        String name = "C_Country_ID";
        select sel  = new select( name,getCountries( loc,C_Country_ID ));

        sel.setID( "ID_" + name );
        sel.setClass( C_MANDATORY );

        // tr tr_country = createRow( name,Msg.translate( ctx,name ),sel );

        label label = new label();
        label.setID( "ID_" + name );
        label.setFor( name );
        label.setClass( C_REQUIRED );
        label.addElement( Msg.translate( ctx,name ) ); 
        html.addElement( label );
        
        html.addElement( sel );
        
        br br = new br();     
        html.addElement( br );        

        // Region          *******************************************************

        int C_Region_ID = 0;

        try {
            String info = ( String )ExpressionUtil.evalNotNull( "location","regionID",m_regionID_el,String.class,this,pageContext );

            if( (info != null) && (info.length() != 0) ) {
                C_Region_ID = Integer.parseInt( info );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"RegionID - " + e );
        }

        if( C_Region_ID == 0 ) {
            C_Region_ID = loc.getC_Region_ID();    // default
        }

        //

        name = "C_Region_ID";

        //tr     tr_region  = null;
        String regionName = ( String )ExpressionUtil.evalNotNull( "location","regionName",m_regionName_el,String.class,this,pageContext );
        input field = new input( input.TYPE_TEXT,"RegionName",regionName );

        field.setSize( 40 ).setMaxlength( 60 ).setID( "ID_RegionName" );

        label = new label();
        label.setID( "ID_" + name );
        label.setFor( name );
        label.addElement( Msg.translate( ctx,name ) ); 
        html.addElement( label );
        
        if( (m_country != null) && m_country.isHasRegion()) {
            sel = new select( name,getRegions( loc,C_Country_ID,C_Region_ID ));
            sel.setID( "ID_" + name );
            // tr_region = createRow( name,m_country.getRegionName(),sel,field );    // Region & Name
            html.addElement( sel );
        } else {
            // tr_region = createRow( name,Msg.translate( ctx,name ),field );    // Name only
            html.addElement( field );
        }
        
        br = new br();     
        html.addElement( br );

        // City    ***********************************************************

        name = "City";

        String city = ( String )ExpressionUtil.evalNotNull( "location","city",m_city_el,String.class,this,pageContext );

        field = new input( input.TYPE_TEXT,name,city );
        // field.setSize( 40 ).setMaxlength( 60 ).setID( "ID_" + name );
        field.setID( "ID_" + name );
        field.setClass( C_MANDATORY );

        // tr tr_city = createRow( name,Msg.translate( ctx,name ),field );
        
        label = new label();
        label.setID( "ID_" + name );
        label.setFor( name );
        label.setClass( C_REQUIRED );
        label.addElement( Msg.translate( ctx,name ) ); 
        html.addElement( label );       
        
        html.addElement( field );
        
        br = new br();     
        html.addElement( br );

        //

        name = "Postal";

        String postal = ( String )ExpressionUtil.evalNotNull( "location","postal",m_postal_el,String.class,this,pageContext );

        field = new input( input.TYPE_TEXT,name,postal );
        field.setSize( 10 ).setMaxlength( 10 ).setID( "ID_" + name );
        field.setClass( C_MANDATORY );

        // tr tr_postal = createRow( name,Msg.translate( ctx,name ),field );
        
        label = new label();
        label.setID( "ID_" + name );
        label.setFor( name );
        label.setClass( C_REQUIRED );
        label.addElement( Msg.translate( ctx,name ) ); 
        html.addElement( label );
        
        html.addElement( field );
        
        br = new br();     
        html.addElement( br );

        log.fine( "C_Country_ID=" + C_Country_ID + ", C_Region_ID=" + C_Region_ID + ", RegionName=" + regionName + ", City=" + city + ", Postal=" + postal );

        // Assemble

        // HtmlCode html = new HtmlCode();

        /*if( m_country != null ) {

            // m_country.DisplaySequence;

            html.addElement( tr_city );
            html.addElement( tr_postal );
            html.addElement( tr_region );
            html.addElement( tr_country );
        } else {
            html.addElement( tr_city );
            html.addElement( tr_postal );
            html.addElement( tr_region );
            html.addElement( tr_country );
        }*/

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
     * @param loc
     * @param C_Country_ID
     *
     * @return
     */

    private option[] getCountries( MLocation loc,int C_Country_ID ) {
        MCountry[] countries = MCountry.getCountries( loc.getCtx());
        option[]   options   = new option[ countries.length ];

        m_country = null;

        //

        for( int i = 0;i < countries.length;i++ ) {
            options[ i ] = new option( String.valueOf( countries[ i ].getC_Country_ID()));
            options[ i ].addElement( countries[ i ].getName());

            if( countries[ i ].getC_Country_ID() == C_Country_ID ) {
                m_country = countries[ i ];
                options[ i ].setSelected( true );
            }
        }

        //

        return options;
    }    // getCountries

    /**
     * Descripción de Método
     *
     *
     * @param loc
     * @param C_Country_ID
     * @param C_Region_ID
     *
     * @return
     */

    private option[] getRegions( MLocation loc,int C_Country_ID,int C_Region_ID ) {
        MRegion[] regions = MRegion.getRegions( loc.getCtx(),C_Country_ID );
        option[]  options = new option[ regions.length + 1 ];

        //

        options[ 0 ] = new option( "0" );
        options[ 0 ].addElement( " " );

        //

        for( int i = 0;i < regions.length;i++ ) {
            options[ i + 1 ] = new option( String.valueOf( regions[ i ].getC_Region_ID()));
            options[ i + 1 ].addElement( regions[ i ].getName());

            if( regions[ i ].getC_Region_ID() == C_Region_ID ) {
                options[ i + 1 ].setSelected( true );
            }
        }

        return options;
    }    // getRegions

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param labelText
     * @param data
     *
     * @return
     */

    private tr createRow( String name,String labelText,ConcreteElement data ) {
        tr tr = new tr();

        // Label

        td td = new td();

        tr.addElement( td );
        td.setAlign( "right" );

        label label = new label();

        td.addElement( label );
        label.setID( "ID_" + name );
        label.setFor( name );
        label.addElement( labelText );

        // Data

        td = new td();
        tr.addElement( td );
        td.setAlign( "left" );
        td.addElement( data );

        //

        return tr;
    }    // addLines

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param labelText
     * @param data
     * @param data2
     *
     * @return
     */

    private tr createRow( String name,String labelText,ConcreteElement data,ConcreteElement data2 ) {
        tr tr = new tr();

        // Label

        td td = new td();

        tr.addElement( td );
        td.setAlign( "right" );

        label label = new label();

        td.addElement( label );
        label.setID( "ID_" + name );
        label.setFor( name );
        label.addElement( labelText );

        // Data

        td = new td();
        tr.addElement( td );
        td.setAlign( "left" );
        td.addElement( data );
        td.addElement( " - " );
        td.addElement( data2 );

        //

        return tr;
    }    // addLines
}    // LocationTag



/*
 *  @(#)LocationTag.java   12.10.07
 * 
 *  Fin del fichero LocationTag.java
 *  
 *  Versión 2.2
 *
 */
