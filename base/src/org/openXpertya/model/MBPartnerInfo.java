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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBPartnerInfo extends X_RV_BPartner {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param Value
     * @param Name
     * @param Contact
     * @param EMail
     * @param Phone
     * @param City
     *
     * @return
     */

    public static MBPartnerInfo[] find( Properties ctx,String Value,String Name,String Contact,String EMail,String Phone,String City ) {
        StringBuffer sql = new StringBuffer( "SELECT * FROM RV_BPartner WHERE IsActive='Y'" );
        StringBuffer sb = new StringBuffer();

        Value = getFindParameter( Value );

        if( Value != null ) {
            sb.append( "UPPER(Value) LIKE ?" );
        }

        Name = getFindParameter( Name );

        if( Name != null ) {
            if( sb.length() > 0 ) {
                sb.append( " OR " );
            }

            sb.append( "UPPER(Name) LIKE ?" );
        }

        Contact = getFindParameter( Contact );

        if( Contact != null ) {
            if( sb.length() > 0 ) {
                sb.append( " OR " );
            }

            sb.append( "UPPER(Contact) LIKE ?" );
        }

        EMail = getFindParameter( EMail );

        if( EMail != null ) {
            if( sb.length() > 0 ) {
                sb.append( " OR " );
            }

            sb.append( "UPPER(EMail) LIKE ?" );
        }

        Phone = getFindParameter( Phone );

        if( Phone != null ) {
            if( sb.length() > 0 ) {
                sb.append( " OR " );
            }

            sb.append( "UPPER(Phone) LIKE ?" );
        }

        City = getFindParameter( City );

        if( City != null ) {
            if( sb.length() > 0 ) {
                sb.append( " OR " );
            }

            sb.append( "UPPER(City) LIKE ?" );
        }

        if( sb.length() > 0 ) {
            sql.append( " AND (" ).append( sb ).append( ")" );
        }

        sql.append( " ORDER BY Value" );

        //

        String finalSQL = MRole.getDefault().addAccessSQL( sql.toString(),"RV_BPartner",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( finalSQL );

            int index = 1;

            if( Value != null ) {
                pstmt.setString( index++,Value );
            }

            if( Name != null ) {
                pstmt.setString( index++,Name );
            }

            if( Contact != null ) {
                pstmt.setString( index++,Contact );
            }

            if( EMail != null ) {
                pstmt.setString( index++,EMail );
            }

            if( Phone != null ) {
                pstmt.setString( index++,Phone );
            }

            if( City != null ) {
                pstmt.setString( index++,City );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MBPartnerInfo( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"find - " + finalSQL,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Return

        MBPartnerInfo[] retValue = new MBPartnerInfo[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // find

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MBPartnerInfo.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBPartnerInfo( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBPartnerInfo
}    // MBPartnerInfo



/*
 *  @(#)MBPartnerInfo.java   02.07.07
 * 
 *  Fin del fichero MBPartnerInfo.java
 *  
 *  Versión 2.2
 *
 */
