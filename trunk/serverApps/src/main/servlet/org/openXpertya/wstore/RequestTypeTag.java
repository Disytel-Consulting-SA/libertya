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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.ecs.xhtml.option;
import org.apache.ecs.xhtml.select;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RequestTypeTag extends TagSupport {

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int doStartTag() {
        JspWriter out    = pageContext.getOut();
        select    select = getRequestType();

        select.output( out );

        //

        return( SKIP_BODY );
    }    // doStartTag

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private select getRequestType() {
        select select = new select( RequestServlet.P_REQUESTTYPE_ID,getOptions());

        select.setID( "ID_" + RequestServlet.P_REQUESTTYPE_ID );

        return select;
    }    // getRequestType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private option[] getOptions() {
        Properties ctx = JSPEnv.getCtx(( HttpServletRequest )pageContext.getRequest());
        int AD_Client_ID = Env.getAD_Client_ID( ctx );

        if( AD_Client_ID == 0 ) {
            log.log( Level.SEVERE,"getOptions - AD_Client_ID not found" );
        } else {
            log.info( "getOptions - AD_Client_ID=" + AD_Client_ID );
        }

        ArrayList list = new ArrayList();

        //

        String sql = "SELECT R_RequestType_ID, Name FROM R_RequestType " + "WHERE AD_Client_ID=? AND IsActive='Y' AND IsSelfService='Y' " + "ORDER BY IsDefault DESC, Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                option o = new option( rs.getString( 1 ));

                o.addElement( rs.getString( 2 ));
                list.add( o );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getOptions",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        // Return to Array and return

        option options[] = new option[ list.size()];

        list.toArray( options );
        log.fine( "getOptions = #" + options.length );

        return options;
    }    // getOptions
}    // RequestTypeTag



/*
 *  @(#)RequestTypeTag.java   12.10.07
 * 
 *  Fin del fichero RequestTypeTag.java
 *  
 *  Versión 2.2
 *
 */
