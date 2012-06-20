/*
 * @(#)CalloutUser.java   11.jun 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package openXpertya.model;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import org.openXpertya.model.*;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 11.12.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutUser extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     * @param oldValue
     *
     * @return
     */

    public String justAnExample( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value,Object oldValue ) {
        log.info( "JustAnExample" );

        return "";
    }    // justAnExample

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */

    public String Frie_Name( String value ) {
        if( (value == null) || (value.length() == 0) ) {
            return "";
        }

        //

        String retValue = value;
        String SQL      = "SELECT FRIE_Name(?) FROM DUAL";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setString( 1,value );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"CalloutUser.Frie_Name",e );
        }

        return retValue;
    }    // Frie_Name

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */

    public String Frie_Value( String value ) {
        if( (value == null) || (value.length() == 0) ) {
            return "";
        }

        //

        String retValue = value;
        String SQL      = "SELECT FRIE_Value(FRIE_Name(?)) FROM DUAL";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setString( 1,value );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"CalloutUser.Frie_Value",e );
        }

        return retValue;
    }    // Frie_Value

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */

    public String Frie_Status( String value ) {
        String retValue = "N";    // default

        if( (value != null) && value.equals( "A" )) {    // Auslaufartikel
            retValue = "Y";                              //
        }

        return retValue;
    }    // Frie_Status
}    // CalloutUser



/*
 *  @(#)CalloutUser.java   11.12.06
 * 
 *  Fin del fichero CalloutUser.java
 *  
 *  Versión 2.2
 *
 */
