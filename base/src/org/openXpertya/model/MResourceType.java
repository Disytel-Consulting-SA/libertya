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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MResourceType extends X_S_ResourceType {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param S_ResourceType_ID
     * @param trxName
     */

    public MResourceType( Properties ctx,int S_ResourceType_ID,String trxName ) {
        super( ctx,S_ResourceType_ID,trxName );
    }    // MResourceType

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MResourceType( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MResourceType

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        if( !success ) {
            return success;
        }

        // Update Products

        if( !newRecord ) {
            MProduct[] products = MProduct.get( getCtx(),"S_Resource_ID IN " + "(SELECT S_Resource_ID FROM S_Resource WHERE S_ResourceType_ID=" + getS_ResourceType_ID() + ")",get_TrxName());

            for( int i = 0;i < products.length;i++ ) {
                MProduct product = products[ i ];

                if( product.setResource( this )) {
                    product.save( get_TrxName());
                }
            }
        }

        return success;
    }    // afterSave
}    // MResourceType



/*
 *  @(#)MResourceType.java   02.07.07
 * 
 *  Fin del fichero MResourceType.java
 *  
 *  Versión 2.2
 *
 */
