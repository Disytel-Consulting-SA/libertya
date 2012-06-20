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



package org.openXpertya.process;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;

import org.openXpertya.model.MLocation;
import org.openXpertya.model.MSystem;
import org.openXpertya.model.M_Registration;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorUsuarioOXP;
import org.openXpertya.util.WebEnv;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RegisterSystem extends SvrProcess {

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        int AD_Registration_ID = getRecord_ID();

        log.info( "doIt - AD_Registration_ID=" + AD_Registration_ID );

        // Check Ststem

        MSystem sys = MSystem.get( getCtx());

        if( sys.getName().equals( "?" ) || (sys.getName().length() < 2) ) {
            throw new ErrorUsuarioOXP( "Set System Name in System Record" );
        }

        if( sys.getUserName().equals( "?" ) || (sys.getUserName().length() < 2) ) {
            throw new ErrorUsuarioOXP( "Set User Name (as in Web Store) in System Record" );
        }

        if( sys.getPassword().equals( "?" ) || (sys.getPassword().length() < 2) ) {
            throw new ErrorUsuarioOXP( "Set Password (as in Web Store) in System Record" );
        }

        // Registration

        M_Registration reg = new M_Registration( getCtx(),AD_Registration_ID,get_TrxName());

        // Location

        MLocation loc = null;

        if( reg.getC_Location_ID() > 0 ) {
            loc = new MLocation( getCtx(),reg.getC_Location_ID(),get_TrxName());

            if( (loc.getCity() == null) || (loc.getCity().length() < 2) ) {
                throw new ErrorUsuarioOXP( "No City in Address" );
            }
        }

        if( loc == null ) {
            throw new ErrorUsuarioOXP( "Please enter Address with City" );
        }

        // Create Query String

        String enc = WebEnv.ENCODING;

        // Send GET Request

        StringBuffer urlString = new StringBuffer( "http://www.openXpertya.org" ).append( "/wstore/registrationServlet?" );

        // System Info

        urlString.append( "Name=" ).append( URLEncoder.encode( sys.getName(),enc )).append( "&UserName=" ).append( URLEncoder.encode( sys.getUserName(),enc )).append( "&Password=" ).append( URLEncoder.encode( sys.getPassword(),enc ));

        // Registration Info

        if( (reg.getDescription() != null) && (reg.getDescription().length() > 0) ) {
            urlString.append( "&Description=" ).append( URLEncoder.encode( reg.getDescription(),enc ));
        }

        urlString.append( "&IsInProduction=" ).append( reg.isInProduction()
                ?"Y"
                :"N" );

        if( reg.getStartProductionDate() != null ) {
            urlString.append( "&StartProductionDate=" ).append( URLEncoder.encode( String.valueOf( reg.getStartProductionDate()),enc ));
        }

        urlString.append( "&IsAllowPublish=" ).append( reg.isAllowPublish()
                ?"Y"
                :"N" ).append( "&NumberEmployees=" ).append( URLEncoder.encode( String.valueOf( reg.getNumberEmployees()),enc )).append( "&C_Currency_ID=" ).append( URLEncoder.encode( String.valueOf( reg.getC_Currency_ID()),enc )).append( "&SalesVolume=" ).append( URLEncoder.encode( String.valueOf( reg.getSalesVolume()),enc ));

        if( (reg.getIndustryInfo() != null) && (reg.getIndustryInfo().length() > 0) ) {
            urlString.append( "&IndustryInfo=" ).append( URLEncoder.encode( reg.getIndustryInfo(),enc ));
        }

        if( (reg.getPlatformInfo() != null) && (reg.getPlatformInfo().length() > 0) ) {
            urlString.append( "&PlatformInfo=" ).append( URLEncoder.encode( reg.getPlatformInfo(),enc ));
        }

        urlString.append( "&IsRegistered=" ).append( reg.isRegistered()
                ?"Y"
                :"N" ).append( "&Record_ID=" ).append( URLEncoder.encode( String.valueOf( reg.getRecord_ID()),enc ));

        // Address

        urlString.append( "&City=" ).append( URLEncoder.encode( loc.getCity(),enc )).append( "&C_Country_ID=" ).append( URLEncoder.encode( String.valueOf( loc.getC_Country_ID()),enc ));

        // Statistics

        if( reg.isAllowStatistics()) {
            urlString.append( "&NumClient=" ).append( URLEncoder.encode( String.valueOf( DB.getSQLValue( null,"SELECT Count(*) FROM AD_Client" )),enc )).append( "&NumOrg=" ).append( URLEncoder.encode( String.valueOf( DB.getSQLValue( null,"SELECT Count(*) FROM AD_Org" )),enc )).append( "&NumBPartner=" ).append( URLEncoder.encode( String.valueOf( DB.getSQLValue( null,"SELECT Count(*) FROM C_BPartner" )),enc )).append( "&NumUser=" ).append( URLEncoder.encode( String.valueOf( DB.getSQLValue( null,"SELECT Count(*) FROM AD_User" )),enc )).append( "&NumProduct=" ).append( URLEncoder.encode( String.valueOf( DB.getSQLValue( null,"SELECT Count(*) FROM M_Product" )),enc )).append( "&NumInvoice=" ).append( URLEncoder.encode( String.valueOf( DB.getSQLValue( null,"SELECT Count(*) FROM C_Invoice" )),enc ));
        }

        log.fine( "doIt - " + urlString );

        // Send it

        URL          url = new URL( urlString.toString());
        StringBuffer sb  = new StringBuffer();

        try {
            URLConnection     uc = url.openConnection();
            InputStreamReader in = new InputStreamReader( uc.getInputStream());
            int               c;

            while(( c = in.read()) != -1 ) {
                sb.append(( char )c );
            }

            in.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt - Connect - " + e.toString());

            throw new IllegalStateException( "Cannot connect to Server - Please try later" );
        }

        //

        String info = sb.toString();

        log.info( "Response=" + info );

        // Record at the end

        int index = sb.indexOf( "Record_ID=" );

        if( index != -1 ) {
            try {
                int Record_ID = Integer.parseInt( sb.substring( index + 10 ));

                reg.setRecord_ID( Record_ID );
                reg.setIsRegistered( true );
                reg.save();

                //

                info = info.substring( 0,index );
            } catch( Exception e ) {
                log.log( Level.SEVERE,"doIt - Record - ",e );
            }
        }

        return info;
    }    // doIt
}    // RegisterSystem



/*
 *  @(#)RegisterSystem.java   02.07.07
 * 
 *  Fin del fichero RegisterSystem.java
 *  
 *  Versión 2.2
 *
 */
