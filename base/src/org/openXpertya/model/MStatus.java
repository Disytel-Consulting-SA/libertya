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

public class MStatus extends X_R_Status {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param R_Status_ID
     *
     * @return
     */

    public static MStatus get( Properties ctx,int R_Status_ID ) {
        if( R_Status_ID == 0 ) {
            return null;
        }

        Integer key      = new Integer( R_Status_ID );
        MStatus retValue = ( MStatus )s_cache.get( key );

        if( retValue == null ) {
            retValue = new MStatus( ctx,R_Status_ID,null );
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MStatus.class );

    /** Descripción de Campos */

    static private CCache s_cache = new CCache( "R_Status",10 );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MStatus getDefault( Properties ctx ) {
        MStatus retValue     = null;
        int     AD_Client_ID = Env.getAD_Client_ID( ctx );
        String  sql          = "SELECT * FROM R_Status " + "WHERE AD_Client_ID IN (0,11) " + "ORDER BY IsDefault DESC, AD_Client_ID DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MStatus( ctx,rs,null );

                if( !retValue.isDefault()) {
                    retValue = null;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,sql,ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        return retValue;
    }    // get

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_Status_ID
     * @param trxName
     */

    public MStatus( Properties ctx,int R_Status_ID,String trxName ) {
        super( ctx,R_Status_ID,trxName );

        if( R_Status_ID == 0 ) {

            // setValue (null);
            // setName (null);

            setIsClosed( false );        // N
            setIsDefault( false );
            setIsFinalClose( false );    // N
            setIsOpen( false );
            setIsWebCanUpdate( true );
        }
    }                                    // MStatus

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MStatus( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MStatus

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( isOpen() && isClosed()) {
            setIsClosed( false );
        }

        if( isFinalClose() &&!isClosed()) {
            setIsFinalClose( false );
        }

        //

        if( !isWebCanUpdate() && (getUpdate_Status_ID() != 0) ) {
            setUpdate_Status_ID( 0 );
        }

        if( (getTimeoutDays() == 0) && (getNext_Status_ID() != 0) ) {
            setNext_Status_ID( 0 );
        }

        //

        return true;
    }    // beforeSave
}    // MStatus



/*
 *  @(#)MStatus.java   02.07.07
 * 
 *  Fin del fichero MStatus.java
 *  
 *  Versión 2.2
 *
 */
