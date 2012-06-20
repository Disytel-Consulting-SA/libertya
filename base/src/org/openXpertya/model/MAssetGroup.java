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
import java.util.Properties;

import org.openXpertya.util.CCache;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAssetGroup extends X_A_Asset_Group {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param A_Asset_Group_ID
     *
     * @return
     */

    public static MAssetGroup get( Properties ctx,int A_Asset_Group_ID ) {
        Integer     ii = new Integer( A_Asset_Group_ID );
        MAssetGroup pc = ( MAssetGroup )s_cache.get( ii );

        if( pc == null ) {
            pc = new MAssetGroup( ctx,A_Asset_Group_ID,null );
        }

        return pc;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "A_Asset_Group",10 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param A_Asset_Group_ID
     * @param trxName
     */

    public MAssetGroup( Properties ctx,int A_Asset_Group_ID,String trxName ) {
        super( ctx,A_Asset_Group_ID,trxName );

        if( A_Asset_Group_ID == 0 ) {

            // setName (null);

            setIsDepreciated( false );
            setIsOneAssetPerUOM( false );
            setIsOwned( false );
            setIsCreateAsActive( true );
        }
    }    // MAssetGroup

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAssetGroup( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAssetGroup
}    // MAssetGroup



/*
 *  @(#)MAssetGroup.java   02.07.07
 * 
 *  Fin del fichero MAssetGroup.java
 *  
 *  Versión 2.2
 *
 */
