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

public class MBPGroup extends X_C_BP_Group {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param BP_Group_ID
     *
     * @return
     */

    public static MBPGroup get( Properties ctx,int BP_Group_ID ) {
        Integer  key      = new Integer( BP_Group_ID );
        MBPGroup retValue = ( MBPGroup )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MBPGroup( ctx,BP_Group_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "BP_Group",10 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BP_Group_ID
     * @param trxName
     */

    public MBPGroup( Properties ctx,int C_BP_Group_ID,String trxName ) {
        super( ctx,C_BP_Group_ID,trxName );

        if( C_BP_Group_ID == 0 ) {

            // setValue (null);
            // setName (null);

            setIsConfidentialInfo( false );    // N
            setIsDefault( false );
            setPriorityBase( PRIORITYBASE_Same );
        }
    }                                          // MBPGroup

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBPGroup( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBPGroup

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
        if( newRecord ) {
            return insert_Accounting( "C_BP_Group_Acct","C_AcctSchema_Default",null );
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        return delete_Accounting( "C_BP_Group_Acct" );
    }    // beforeDelete
}    // MBPGroup



/*
 *  @(#)MBPGroup.java   02.07.07
 * 
 *  Fin del fichero MBPGroup.java
 *  
 *  Versión 2.2
 *
 */
