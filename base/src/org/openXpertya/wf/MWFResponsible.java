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



package org.openXpertya.wf;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.X_AD_WF_Responsible;
import org.openXpertya.util.CCache;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWFResponsible extends X_AD_WF_Responsible {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_WF_Responsible_ID
     *
     * @return
     */

    public static MWFResponsible get( Properties ctx,int AD_WF_Responsible_ID ) {
        Integer        key      = new Integer( AD_WF_Responsible_ID );
        MWFResponsible retValue = ( MWFResponsible )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MWFResponsible( ctx,AD_WF_Responsible_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "AD_WF_Responsible",10 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param id
     * @param trxName
     */

    public MWFResponsible( Properties ctx,int id,String trxName ) {
        super( ctx,id,trxName );
    }    // MWFResponsible

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWFResponsible( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWFResponsible

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInvoker() {
        return (getAD_User_ID() == 0) && (getAD_Role_ID() == 0);
    }    // isInvoker

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // if (RESPONSIBLETYPE_Human.equals(getResponsibleType()) && getAD_User_ID() == 0)
        // return true;

        if( RESPONSIBLETYPE_Role.equals( getResponsibleType()) && (getAD_Role_ID() == 0) && (getAD_Client_ID() > 0) ) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@RequiredEnter@ @AD_Role_ID@" ));

            return false;
        }

        return true;
    }    // beforeSave
}    // MWFResponsible



/*
 *  @(#)MWFResponsible.java   02.07.07
 * 
 *  Fin del fichero MWFResponsible.java
 *  
 *  Versión 2.2
 *
 */
