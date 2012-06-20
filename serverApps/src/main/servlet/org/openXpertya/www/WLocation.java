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



package org.openXpertya.www;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.AlignType;
import org.apache.ecs.xhtml.b;
import org.apache.ecs.xhtml.button;
import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.input;
import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.p;
import org.apache.ecs.xhtml.script;
import org.apache.ecs.xhtml.select;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.model.MCountry;
import org.openXpertya.model.MField;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MRegion;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.WebDoc;
import org.openXpertya.util.WebEnv;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WLocation extends HttpServlet {

    /** Descripción de Campos */

    protected static CLogger log = CLogger.getCLogger( WLocation.class );

    /**
     * Descripción de Método
     *
     *
     * @param config
     *
     * @throws ServletException
     */

    public void init( ServletConfig config ) throws ServletException {
        super.init( config );

        if( !WebEnv.initWeb( config )) {
            throw new ServletException( "WLocation.init" );
        }
    }    // init

    /** Descripción de Campos */

    private static final String P_TARGET = "TARGET";

    /** Descripción de Campos */

    private static final String P_C_LOCATION_ID = "C_LOCATION_ID";

    /** Descripción de Campos */

    private static final String P_ADDRESS1 = "ADDRESS1";

    /** Descripción de Campos */

    private static final String P_ADDRESS2 = "ADDRESS2";

    /** Descripción de Campos */

    private static final String P_CITY = "CITY";

    /** Descripción de Campos */

    private static final String P_POSTAL = "POSTAL";

    /** Descripción de Campos */

    private static final String P_C_COUNTRY_ID = "C_COUNTRY_ID";

    /** Descripción de Campos */

    private static final String P_C_REGION_ID = "C_REGION_ID";

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doGet( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        log.fine( "" );

        HttpSession   sess = request.getSession();
        WWindowStatus ws   = WWindowStatus.get( request );

        if( ws == null ) {
            WebUtil.createTimeoutPage( request,response,this,null );

            return;
        }

        // Get Mandatory Parameters

        String columnName = WebUtil.getParameter( request,"ColumnName" );

        log.info( "ColumnName=" + columnName + " - " + ws.toString());

        //

        MField mField = ws.curTab.getField( columnName );

        log.config( "ColumnName=" + columnName + ", MField=" + mField );

        if( (mField == null) || (columnName == null) || columnName.equals( "" )) {
            WebUtil.createErrorPage( request,response,this,Msg.getMsg( ws.ctx,"ParameterMissing" ));

            return;
        }

        MLocation location = null;
        Object    value    = mField.getValue();

        if( (value != null) && (value instanceof Integer) ) {
            location = new MLocation( ws.ctx,(( Integer )value ).intValue(),null );
        } else {
            location = new MLocation( ws.ctx,0,null );
        }

        String targetBase = "parent.WWindow." + WWindow.FORM_NAME + "." + columnName;
        String action = request.getRequestURI();

        // Create Document

        WebDoc doc = WebDoc.createPopup( mField.getHeader());

        doc.addPopupClose();

        boolean hasDependents = ws.curTab.hasDependants( columnName );
        boolean hasCallout    = mField.getCallout().length() > 0;

        // Reset

        button reset = new button();

        reset.addElement( "Reset" );    // translate

        String script = targetBase + "F.value='';" + targetBase + "D.value='';closePopup();";

        if( hasDependents || hasCallout ) {
            script += "startUpdate(" + targetBase + "F);";
        }

        reset.setOnClick( script );

        //

        doc.getTable().addElement( new tr().addElement( fillForm( ws,action,location,targetBase,hasDependents || hasCallout )).addElement( reset ));

        //

        doc.addPopupClose();

        // log.trace(log.l6_Database, doc.toString());

        WebUtil.createResponse( request,response,this,null,doc,false );
    }    // doGet

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */

    public void doPost( HttpServletRequest request,HttpServletResponse response ) throws ServletException,IOException {
        log.fine( "" );

        HttpSession   sess = request.getSession();
        WWindowStatus ws   = WWindowStatus.get( request );

        if( ws == null ) {
            WebUtil.createTimeoutPage( request,response,this,null );

            return;
        }

        int C_Location_ID = WebUtil.getParameterAsInt( request,P_C_LOCATION_ID );
        String targetBase = "parent.WWindow." + WWindow.FORM_NAME + ".C_Location_ID";

        // Create Location

        MLocation location = new MLocation( ws.ctx,C_Location_ID,null );

        log.fine( "doPost updating C_Location_ID=" + C_Location_ID + " - " + targetBase );
        location.setAddress1( WebUtil.getParameter( request,P_ADDRESS1 ));
        location.setAddress2( WebUtil.getParameter( request,P_ADDRESS2 ));
        location.setCity( WebUtil.getParameter( request,P_CITY ));
        location.setPostal( WebUtil.getParameter( request,P_POSTAL ));
        location.setC_Country_ID( WebUtil.getParameterAsInt( request,P_C_COUNTRY_ID ));
        location.setC_Region_ID( WebUtil.getParameterAsInt( request,P_C_REGION_ID ));

        // Document

        WebDoc doc = WebDoc.createPopup( "WLocation" );

        doc.addPopupClose();

        // Save Location

        location.save();
        C_Location_ID = location.getC_Location_ID();

        td center = doc.addPopupCenter( false );

        if( C_Location_ID == 0 ) {
            center.addElement( new p( new b( "ERROR - Location=0" )));
        }

        center.addElement( new p().addElement( location.toString()));

        // Update Target

        script script = new script( new StringBuffer().append( targetBase ).append( "D.value='" ).append( C_Location_ID ).append( "';" ).append( targetBase ).append( "F.value='" ).append( location.toString()).append( "';closePopup();" ).toString());

        doc.getBody().addElement( script );
        log.fine( "script=" + script.toString());

        //

        WebUtil.createResponse( request,response,this,null,doc,false );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param ws
     * @param action
     * @param location
     * @param targetBase
     * @param addStart
     *
     * @return
     */

    private form fillForm( WWindowStatus ws,String action,MLocation location,String targetBase,boolean addStart ) {
        form myForm = null;

        myForm = new form( action );
        myForm.addElement( new input( input.TYPE_HIDDEN,P_TARGET,targetBase ));
        myForm.addElement( new input( input.TYPE_HIDDEN,P_C_LOCATION_ID,location.getC_Location_ID()));

        //

        table table = new table();

        table.setID( "WLocation" );

        // --  Line 1

        tr line = new tr();

        line.addElement( new td( Msg.getMsg( ws.ctx,"Address" ) + " 1" ).setAlign( AlignType.RIGHT ));

        input myInput = null;

        myInput = new input( input.TYPE_TEXT,P_ADDRESS1,location.getAddress1());
        myInput.setMaxlength( 50 ).setSize( 50 );
        line.addElement( new td( myInput ).setAlign( AlignType.LEFT ).setColSpan( 5 ));
        table.addElement( line );

        // --  Line 2

        line = new tr();
        line.addElement( new td( Msg.getMsg( ws.ctx,"Address" ) + " 2" ).setAlign( AlignType.RIGHT ));
        myInput = new input( input.TYPE_TEXT,P_ADDRESS2,location.getAddress2());
        myInput.setMaxlength( 50 ).setSize( 50 );
        line.addElement( new td( myInput ).setAlign( AlignType.LEFT ).setColSpan( 5 ));
        table.addElement( line );

        // --  Line 3

        line = new tr();
        line.addElement( new td( Msg.getMsg( ws.ctx,"City" )).setAlign( AlignType.RIGHT ));    // 1
        myInput = new input( input.TYPE_TEXT,P_CITY,location.getCity());
        myInput.setMaxlength( 30 ).setSize( 30 );
        line.addElement( new td( myInput ).setAlign( AlignType.LEFT ));    // 2

        //

        if( location.getCountry().isHasRegion()) {
            line.addElement( new td( Msg.getMsg( ws.ctx,"Region" )).setAlign( AlignType.RIGHT ));    // 3
            line.addElement( new td( getRegion( location,ws )).setAlign( AlignType.LEFT ));    // 4
        }

        //

        line.addElement( new td( Msg.getMsg( ws.ctx,"Postal" )).setAlign( AlignType.RIGHT ));    // 5
        myInput = new input( input.TYPE_TEXT,P_POSTAL,location.getPostal());
        myInput.setMaxlength( 10 ).setSize( 6 );
        line.addElement( new td( myInput ).setAlign( AlignType.LEFT ));    // 6

        //
        // input = new input (input.TYPE_TEXT, "PostalAdd", mLocation.PostalAdd );
        // line.addElement(new td(input).setAlign(AlignType.LEFT));

        table.addElement( line );

        // --  Line 4

        line = new tr();
        line.addElement( new td( Msg.getMsg( ws.ctx,"Country" )).setAlign( AlignType.RIGHT ));
        line.addElement( new td( this.getCountry( location,ws )).setAlign( AlignType.LEFT ).setColSpan( 5 ));
        table.addElement( line );

        // --  Line 5

        line = new tr();

        // Submit

        line.addElement( new td( "&nbsp;" ));

        input submit = new input( input.TYPE_SUBMIT,"Submit","Submit" );    // translate

        line.addElement( new td( submit ).setAlign( AlignType.RIGHT ).setColSpan( 5 ));
        table.addElement( line );
        myForm.addElement( table );

        //

        return myForm;
    }    // fillform

    /**
     * Descripción de Método
     *
     *
     * @param location
     * @param ws
     *
     * @return
     */

    private select getCountry( MLocation location,WWindowStatus ws ) {
        MCountry[] countries = MCountry.getCountries( location.getCtx());
        int        comp      = location.getC_Country_ID();

        if( comp == 0 ) {
            comp = Env.getContextAsInt( ws.ctx,"C_Country_ID" );
        }

        option[] options = new option[ countries.length ];

        for( int i = 0;i < countries.length;i++ ) {
            options[ i ] = new option( String.valueOf( countries[ i ].getC_Country_ID()));
            options[ i ].addElement( countries[ i ].getName());

            if( comp == countries[ i ].getC_Country_ID()) {
                options[ i ].setSelected( true );
            }
        }

        select select = new select( P_C_COUNTRY_ID,options );

        return select;
    }    // getRegion

    /**
     * Descripción de Método
     *
     *
     * @param location
     * @param ws
     *
     * @return
     */

    private select getRegion( MLocation location,WWindowStatus ws ) {
        MRegion[] regions = MRegion.getRegions( location.getCtx(),location.getC_Country_ID());
        int comp = location.getC_Region_ID();

        if( comp == 0 ) {
            comp = Env.getContextAsInt( ws.ctx,"C_Region_ID" );
        }

        option[] options = new option[ regions.length ];

        for( int i = 0;i < regions.length;i++ ) {
            options[ i ] = new option( String.valueOf( regions[ i ].getC_Region_ID()));
            options[ i ].addElement( regions[ i ].getName());

            if( comp == regions[ i ].getC_Region_ID()) {
                options[ i ].setSelected( true );
            }
        }

        select select = new select( P_C_REGION_ID,options );

        return select;
    }    // getRegion
}    // WLocation



/*
 *  @(#)WLocation.java   23.03.06
 * 
 *  Fin del fichero WLocation.java
 *  
 *  Versión 2.2
 *
 */
