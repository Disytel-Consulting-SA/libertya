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
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPOSKeyLayout extends X_C_POSKeyLayout {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_POSKeyLayout_ID
     *
     * @return
     */

    public static MPOSKeyLayout get( Properties ctx,int C_POSKeyLayout_ID ) {
        Integer       key      = new Integer( C_POSKeyLayout_ID );
        MPOSKeyLayout retValue = ( MPOSKeyLayout )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MPOSKeyLayout( ctx,C_POSKeyLayout_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_POSKeyLayout",3 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_POSKeyLayout_ID
     * @param trxName
     */

    public MPOSKeyLayout( Properties ctx,int C_POSKeyLayout_ID,String trxName ) {
        super( ctx,C_POSKeyLayout_ID,trxName );
    }    // MPOSKeyLayout

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPOSKeyLayout( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPOSKeyLayout

    /** Descripción de Campos */

    private MPOSKey[] m_keys = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MPOSKey[] getKeys( boolean requery ) {
        if( (m_keys != null) &&!requery ) {
            return m_keys;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_POSKey WHERE C_POSKeyLayout_ID=? ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_POSKeyLayout_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MPOSKey( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getKeys",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_keys = new MPOSKey[ list.size()];
        list.toArray( m_keys );

        return m_keys;
    }    // getKeys

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNoOfKeys() {
        return getKeys( false ).length;
    }    // getNoOfKeys
}    // MPOSKeyLayout



/*
 *  @(#)MPOSKeyLayout.java   02.07.07
 * 
 *  Fin del fichero MPOSKeyLayout.java
 *  
 *  Versión 2.2
 *
 */
