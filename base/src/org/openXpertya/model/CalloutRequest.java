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



package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutRequest extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String copyMail( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        String colName = mField.getColumnName();

        log.info( colName + "=" + value );

        if( value == null ) {
            return "";
        }

        Integer R_MailText_ID = ( Integer )value;
        String  sql           = "SELECT MailHeader, MailText FROM R_MailText " + "WHERE R_MailText_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,R_MailText_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                String txt = rs.getString( 2 );

                txt = Env.parseContext( ctx,WindowNo,txt,false,true );
                mTab.setValue( "Result",txt );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        return "";
    }    // copyText

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String copyResponse( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        String colName = mField.getColumnName();

        log.info( colName + "=" + value );

        if( value == null ) {
            return "";
        }

        Integer R_StandardResponse_ID = ( Integer )value;
        String  sql                   = "SELECT Name, ResponseText FROM R_StandardResponse " + "WHERE R_StandardResponse_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,R_StandardResponse_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                String txt = rs.getString( 2 );

                txt = Env.parseContext( ctx,WindowNo,txt,false,true );
                mTab.setValue( "Result",txt );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        return "";
    }    // copyResponse
}    // CalloutRequest



/*
 *  @(#)CalloutRequest.java   02.07.07
 * 
 *  Fin del fichero CalloutRequest.java
 *  
 *  Versión 2.2
 *
 */
