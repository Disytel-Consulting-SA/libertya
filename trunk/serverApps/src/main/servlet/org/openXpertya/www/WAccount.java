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

import org.apache.ecs.AlignType;
import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.button;
import org.apache.ecs.xhtml.p;
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
import org.openXpertya.util.WebSessionCtx;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WAccount extends HttpServlet {

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
            throw new ServletException( "WAccount.init" );
        }
    }    // init

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( WAccount.class );

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
        log.config( "WAccount.doGet" );

        WebSessionCtx wsc = WebSessionCtx.get( request );
        WWindowStatus ws  = WWindowStatus.get( request );

        if( (wsc == null) || (ws == null) ) {
            WebUtil.createTimeoutPage( request,response,this,null );

            return;
        }

        // Get Mandatory Parameters

        String formName   = WebUtil.getParameter( request,"FormName" );
        String columnName = WebUtil.getParameter( request,"ColumnName" );

        //

        MField mField = ws.curTab.getField( columnName );

        log.config( "FormName=" + formName + ", ColumnName=" + columnName + ", MField=" + mField.toString());

        if( (mField == null) || (formName == null) || (columnName == null) || formName.equals( "" ) || columnName.equals( "" )) {
            WebUtil.createTimeoutPage( request,response,this,Msg.getMsg( wsc.ctx,"ParameterMissing" ));

            return;
        }

        // Object value = ws.curTab.getValue(columnName);

        String target = "opener.document." + formName + "." + columnName;

        // Create Document

        WebDoc doc  = WebDoc.create( mField.getHeader());
        body   body = doc.getBody();

        body.setOnBlur( "self.focus();" );
        body.addElement( fillTable( ws,mField,target ));

        // Reset, Cancel

        button reset = new button();

        reset.addElement( "Reset" );    // translate
        reset.setOnClick( target + ".value='';" + target + "_D.value='';window.close();" );

        button cancel = new button();

        cancel.addElement( "Cancel" );    // translate
        cancel.setOnClick( "window.close();" );
        body.addElement( new p( AlignType.RIGHT ).addElement( reset ).addElement( "&nbsp" ).addElement( cancel ));

        //
        // log.fine( doc.toString());

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
        log.config( "WAccount.doPost" );
        doGet( request,response );
    }    // doPost

    /**
     * Descripción de Método
     *
     *
     * @param ws
     * @param mField
     * @param target
     *
     * @return
     */

    private table fillTable( WWindowStatus ws,MField mField,String target ) {
        table table = new table( "1" );
        tr    line  = new tr();

        line.addElement( new th( "&nbsp" )).addElement( new th( Msg.translate( ws.ctx,"Name" )));
        table.addElement( line );

        // Fill & list options

        Lookup lookup = mField.getLookup();

        lookup.fillComboBox( mField.isMandatory( false ),true,true,true );    // no context check

        int size = lookup.getSize();

        for( int i = 0;i < size;i++ ) {
            Object lValue = lookup.getElementAt( i );

            if( !( (lValue != null) && (lValue instanceof KeyNamePair) ) ) {
                continue;
            }

            //
            // log.fine( lValue.toString());

            KeyNamePair np     = ( KeyNamePair )lValue;
            button      button = new button();

            button.addElement( "&gt;" );

            StringBuffer script = new StringBuffer( target );

            script.append( ".value='" ).append( np.getKey()).append( "';" ).append( target ).append( "_D.value='" ).append( np.getName()).append( "';window.close();" );
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
}    // WAccount



/*
 *  @(#)WAccount.java   23.03.06
 * 
 *  Fin del fichero WAccount.java
 *  
 *  Versión 2.2
 *
 */
