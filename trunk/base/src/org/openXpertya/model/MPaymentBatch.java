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

public class MPaymentBatch extends X_C_PaymentBatch {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_PaySelection_ID
     * @param trxName
     *
     * @return
     */

    public static MPaymentBatch getForPaySelection( Properties ctx,int C_PaySelection_ID,String trxName ) {
        MPaySelection ps       = new MPaySelection( ctx,C_PaySelection_ID,trxName );
        MPaymentBatch retValue = new MPaymentBatch( ps );

        return retValue;
    }    // getForPaySelection

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_PaymentBatch_ID
     * @param trxName
     */

    public MPaymentBatch( Properties ctx,int C_PaymentBatch_ID,String trxName ) {
        super( ctx,C_PaymentBatch_ID,trxName );

        if( C_PaymentBatch_ID == 0 ) {

            // setName (null);

            setProcessed( false );
            setProcessing( false );
        }
    }    // MPaymentBatch

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPaymentBatch( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPaymentBatch

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param Name
     * @param trxName
     */

    public MPaymentBatch( Properties ctx,String Name,String trxName ) {
        this( ctx,0,trxName );
        setName( Name );
    }    // MPaymentBatch

    /**
     * Constructor de la clase ...
     *
     *
     * @param ps
     */

    public MPaymentBatch( MPaySelection ps ) {
        this( ps.getCtx(),0,ps.get_TrxName());
        setClientOrg( ps );
        setName( ps.getName());
    }    // MPaymentBatch
}    // MPaymentBatch



/*
 *  @(#)MPaymentBatch.java   02.07.07
 * 
 *  Fin del fichero MPaymentBatch.java
 *  
 *  Versión 2.2
 *
 */
