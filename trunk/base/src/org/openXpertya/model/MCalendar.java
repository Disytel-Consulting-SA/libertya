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

import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;

import org.openXpertya.util.CCache;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MCalendar extends X_C_Calendar {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Calendar_ID
     *
     * @return
     */

    public static MCalendar get( Properties ctx,int C_Calendar_ID ) {
        Integer   key      = new Integer( C_Calendar_ID );
        MCalendar retValue = ( MCalendar )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MCalendar( ctx,C_Calendar_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     *
     * @return
     */

    public static MCalendar getDefault( Properties ctx,int AD_Client_ID ) {
        MClientInfo info = MClientInfo.get( ctx,AD_Client_ID );

        return get( ctx,info.getC_Calendar_ID());
    }    // getDefault

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MCalendar getDefault( Properties ctx ) {
        return getDefault( ctx,Env.getAD_Client_ID( ctx ));
    }    // getDefault

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_Calendar",20 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Calendar_ID
     * @param trxName
     */

    public MCalendar( Properties ctx,int C_Calendar_ID,String trxName ) {
        super( ctx,C_Calendar_ID,trxName );
    }    // MCalendar

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCalendar( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCalendar

    /**
     * Constructor de la clase ...
     *
     *
     * @param client
     */

    public MCalendar( MClient client ) {
        super( client.getCtx(),0,client.get_TrxName());
        setClientOrg( client );
        setName( client.getName() + " " + Msg.translate( client.getCtx(),"C_Calendar_ID" ));
    }    // MCalendar

    /**
     * Descripción de Método
     *
     *
     * @param locale
     *
     * @return
     */

    public MYear createYear( Locale locale ) {
        if( getID() == 0 ) {
            return null;
        }

        MYear year = new MYear( this );

        if( year.save()) {
            year.createStdPeriods( locale );
        }

        //

        return year;
    }    // createYear
}    // MCalendar



/*
 *  @(#)MCalendar.java   02.07.07
 * 
 *  Fin del fichero MCalendar.java
 *  
 *  Versión 2.2
 *
 */
