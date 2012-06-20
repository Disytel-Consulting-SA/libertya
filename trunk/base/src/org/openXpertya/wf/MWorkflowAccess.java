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

import org.openXpertya.model.X_AD_Workflow_Access;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWorkflowAccess extends X_AD_Workflow_Access {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MWorkflowAccess( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        } else {

            // setAD_Role_ID (0);
            // setAD_Workflow_ID (0);

            setIsReadWrite( true );
        }
    }    // MWorkflowAccess

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWorkflowAccess( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWorkflowAccess

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param AD_Role_ID
     */

    public MWorkflowAccess( MWorkflow parent,int AD_Role_ID ) {
        super( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setAD_Workflow_ID( parent.getAD_Workflow_ID());
        setAD_Role_ID( AD_Role_ID );
    }    // MWorkflowAccess
}    // MWorkflowAccess



/*
 *  @(#)MWorkflowAccess.java   02.07.07
 * 
 *  Fin del fichero MWorkflowAccess.java
 *  
 *  Versión 2.2
 *
 */
