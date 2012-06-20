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

public class MCity extends X_C_City {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_Category_ID
     *
     * @return
     */

    public static MCity get( Properties ctx,int C_City_ID ) {
        Integer          ii = new Integer( C_City_ID );
        MCity pc = ( MCity )s_cache.get( ii );

        if( pc == null ) {
            pc = new MCity( ctx,C_City_ID,null );
        }

        return pc;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_City",20 );

    /** Descripción de Campos */



    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MCity.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Product_Category_ID
     * @param trxName
     */

    public MCity( Properties ctx,int C_City_ID,String trxName ) {
        super( ctx,C_City_ID,trxName );

        if( C_City_ID == 0 ) {

           // setName (null);
            setC_Region_ID (0);
            setC_Country_ID (0);
            setPostal (null);

            // setValue (null);


        }
    }                                        // MProductCategory

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCity( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProductCategory
}



/*
 *  @(#)MProductCategory.java   02.07.07
 *
 *  Fin del fichero MProductCategory.java
 *
 *  Versión 2.2
 *
 */
