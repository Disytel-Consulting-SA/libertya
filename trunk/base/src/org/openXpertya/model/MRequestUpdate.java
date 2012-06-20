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

public class MRequestUpdate extends X_R_RequestUpdate {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_RequestUpdate_ID
     * @param trxName
     */

    public MRequestUpdate( Properties ctx,int R_RequestUpdate_ID,String trxName ) {
        super( ctx,R_RequestUpdate_ID,trxName );
    }    // MRequestUpdate

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRequestUpdate( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRequestUpdate

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MRequestUpdate( MRequest parent ) {
        super( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setR_Request_ID( parent.getR_Request_ID());
        set_ValueNoCheck( "Created",parent.getUpdated());
        set_ValueNoCheck( "CreatedBy",new Integer( parent.getUpdatedBy()));
        set_ValueNoCheck( "Updated",parent.getUpdated());
        set_ValueNoCheck( "UpdatedBy",new Integer( parent.getUpdatedBy()));

        //

        setStartTime( parent.getStartTime());
        setEndTime( parent.getEndTime());
        setResult( parent.getResult());
        setQtySpent( parent.getQtySpent());
        setQtyInvoiced( parent.getQtyInvoiced());
        setM_ProductSpent_ID( parent.getM_ProductSpent_ID());
        setConfidentialTypeEntry( parent.getConfidentialTypeEntry());
    }    // MRequestUpdate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNewInfo() {
        return getResult() != null;
    }    // isNewInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCreatedByName() {
        MUser user = MUser.get( getCtx(),getCreatedBy());

        return user.getName();
    }    // getCreatedByName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getConfidentialEntryText() {
        return MRefList.getListName( getCtx(),CONFIDENTIALTYPEENTRY_AD_Reference_ID,getConfidentialTypeEntry());
    }    // getConfidentialTextEntry

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getConfidentialTypeEntry() == null ) {
            setConfidentialTypeEntry( CONFIDENTIALTYPEENTRY_PublicInformation );
        }

        return true;
    }    // beforeSave
}    // MRequestUpdate



/*
 *  @(#)MRequestUpdate.java   02.07.07
 * 
 *  Fin del fichero MRequestUpdate.java
 *  
 *  Versión 2.2
 *
 */
