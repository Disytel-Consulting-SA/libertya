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

import org.openXpertya.util.CCache;
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

public class MRegistrationAttribute extends X_A_RegistrationAttribute {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MRegistrationAttribute[] getAll( Properties ctx ) {

        // Store/Refresh Cache and add to List

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM A_RegistrationAttribute " + "WHERE AD_Client_ID=? " + "ORDER BY SeqNo";
        int               AD_Client_ID = Env.getAD_Client_ID( ctx );
        PreparedStatement pstmt        = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRegistrationAttribute value = new MRegistrationAttribute( ctx,rs,null );
                Integer key = new Integer( value.getA_RegistrationAttribute_ID());

                s_cache.put( key,value );
                list.add( value );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getAll",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        MRegistrationAttribute[] retValue = new MRegistrationAttribute[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getAll

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param A_RegistrationAttribute_ID
     * @param trxName
     *
     * @return
     */

    public static MRegistrationAttribute get( Properties ctx,int A_RegistrationAttribute_ID,String trxName ) {
        Integer                key      = new Integer( A_RegistrationAttribute_ID );
        MRegistrationAttribute retValue = ( MRegistrationAttribute )s_cache.get( key );

        if( retValue == null ) {
            retValue = new MRegistrationAttribute( ctx,A_RegistrationAttribute_ID,trxName );
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // getAll

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MRegistrationAttribute.class );

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "A_RegistrationAttribute",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param A_RegistrationAttribute_ID
     * @param trxName
     */

    public MRegistrationAttribute( Properties ctx,int A_RegistrationAttribute_ID,String trxName ) {
        super( ctx,A_RegistrationAttribute_ID,trxName );
    }    // MRegistrationAttribute

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRegistrationAttribute( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRegistrationAttribute
}    // MRegistrationAttribute



/*
 *  @(#)MRegistrationAttribute.java   02.07.07
 * 
 *  Fin del fichero MRegistrationAttribute.java
 *  
 *  Versión 2.2
 *
 */
