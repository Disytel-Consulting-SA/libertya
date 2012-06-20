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

import org.apache.ecs.xhtml.button;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.th;
import org.apache.ecs.xhtml.tr;
import org.openXpertya.model.Lookup;
import org.openXpertya.model.MField;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.KeyNamePair;
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

public class WLookup extends HttpServlet {

    /** Descripción de Campos */

    protected static CLogger log = CLogger.getCLogger( WLookup.class );

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
            throw new ServletException( "WLookup.init" );
        }
    }    // init

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
        WebEnv.dump( request );
        WebEnv.dump( request.getSession());

        // FIXME: siempre devuelve null!!

        WWindowStatus ws = WWindowStatus.get( request );

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

        // parent = framesetWindow
        // Label - Dtata - Field - Button

        String targetBase = "parent.WWindow." + WWindow.FORM_NAME + "." + columnName;

        // Object value = ws.curTab.getValue(columnName);

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

        doc.getTable().addElement( new tr().addElement( fillTable( ws,mField,targetBase,hasDependents || hasCallout )).addElement( reset ));

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
        log.config( "" );
        doGet( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param ws
     * @param mField
     * @param targetBase
     * @param addStart
     *
     * @return
     */

    private table fillTable( WWindowStatus ws,MField mField,String targetBase,boolean addStart ) {

        //

        table table = new table( "1" );    // Border 1

        table.setID( "WLookup" );

        tr line = new tr();

        line.addElement( new th( "&nbsp" )).addElement( new th( Msg.translate( ws.ctx,"Name" )));
        table.addElement( line );

        Lookup lookup = mField.getLookup();

        log.info( mField.getColumnName());

        // Fill & list options

        lookup.fillComboBox( mField.isMandatory( false ),true,true,true );    // no context check

        int size = lookup.getSize();

        for( int i = 0;i < size;i++ ) {
            Object lValue = lookup.getElementAt( i );

            if( !( (lValue != null) && (lValue instanceof KeyNamePair) ) ) {
                continue;
            }

            //
            // log.trace(log.l6_Database, lValue.toString());

            KeyNamePair np     = ( KeyNamePair )lValue;
            button      button = new button();

            button.addElement( "&gt;" );

            StringBuffer script = new StringBuffer();

            script.append( targetBase ).append( "D.value='" ).append( np.getKey()).append( "';" ).append( targetBase ).append( "F.value='" ).append( np.getName()).append( "';closePopup();" );

            if( addStart ) {
                script.append( "startUpdate(" ).append( targetBase ).append( "F);" );
            }

            button.setOnClick( script.toString());

            //

            line = new tr();
            line.addElement( new td( button ));

            String name = np.getName();

            if( (name == null) || (name.length() == 0) ) {
                name = "&nbsp";
            }

            line.addElement( new td( name ));
            table.addElement( line );
        }

        // Restore

        lookup.fillComboBox( true );

        return table;
    }    // fillTable

    /**
     * Descripción de Método
     *
     *
     * @param ws
     * @param mField
     * @param targetBase
     *
     * @return
     */

    private table fillTable_BPartner( WWindowStatus ws,MField mField,String targetBase ) {
        return null;
    }    // fillTable_BPartner

    /**
     * Descripción de Método
     *
     *
     * @param ws
     * @param mField
     * @param targetBase
     *
     * @return
     */

    private table fillTable_Product( WWindowStatus ws,MField mField,String targetBase ) {
        return null;
    }    // fillTable_Product
}    // WLookup



/*
 *  @(#)WLookup.java   23.03.06
 * 
 *  Fin del fichero WLookup.java
 *  
 *  Versión 2.2
 *
 */
