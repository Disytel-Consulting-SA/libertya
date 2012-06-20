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
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInvoiceBatchLine extends X_C_InvoiceBatchLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_InvoiceBatchLine_ID
     * @param trxName
     */

    public MInvoiceBatchLine( Properties ctx,int C_InvoiceBatchLine_ID,String trxName ) {
        super( ctx,C_InvoiceBatchLine_ID,trxName );

        if( C_InvoiceBatchLine_ID == 0 ) {

            // setC_InvoiceBatch_ID (0);

            setDateAcct( new Timestamp( System.currentTimeMillis()));    // @DateDoc@
            setDateInvoiced( new Timestamp( System.currentTimeMillis()));    // @DateDoc@
            setIsTaxIncluded( false );
            setLineNetAmt( Env.ZERO );
            setLineTotalAmt( Env.ZERO );
            setPriceEntered( Env.ZERO );
            setQtyEntered( Env.ONE );    // 1
            setTaxAmt( Env.ZERO );
            setProcessed( false );
        }
    }                                    // MInvoiceBatchLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInvoiceBatchLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInvoiceBatchLine

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
        if( success ) {
            String sql = "UPDATE C_InvoiceBatch h " + "SET DocumentAmt = NVL((SELECT SUM(LineTotalAmt) FROM C_InvoiceBatchLine l " + "WHERE h.C_InvoiceBatch_ID=l.C_InvoiceBatch_ID AND l.IsActive='Y'),0) " + "WHERE C_InvoiceBatch_ID=" + getC_InvoiceBatch_ID();

            DB.executeUpdate( sql,get_TrxName());
        }

        return success;
    }    // afterSave
}    // MInvoiceBatchLine



/*
 *  @(#)MInvoiceBatchLine.java   02.07.07
 * 
 *  Fin del fichero MInvoiceBatchLine.java
 *  
 *  Versión 2.2
 *
 */
