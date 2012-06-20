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



package org.openXpertya.impexp;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CopyImportFormat extends SvrProcess {

    /** Descripción de Campos */

    private int from_AD_ImpFormat_ID = 0;

    /** Descripción de Campos */

    private int to_AD_ImpFormat_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_ImpFormat_ID" )) {
                from_AD_ImpFormat_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        to_AD_ImpFormat_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "doIt = From=" + from_AD_ImpFormat_ID + " To=" + to_AD_ImpFormat_ID );

        MImpFormat from = new MImpFormat( getCtx(),from_AD_ImpFormat_ID,get_TrxName());

        if( from.getAD_ImpFormat_ID() != from_AD_ImpFormat_ID ) {
            throw new Exception( "From Format not found - " + from_AD_ImpFormat_ID );
        }

        //

        MImpFormat to = new MImpFormat( getCtx(),to_AD_ImpFormat_ID,get_TrxName());

        if( to.getAD_ImpFormat_ID() != to_AD_ImpFormat_ID ) {
            throw new Exception( "To Format not found - " + from_AD_ImpFormat_ID );
        }

        //

        if( from.getAD_Table_ID() != to.getAD_Table_ID()) {
            throw new Exception( "From-To do Not have same Format Table" );
        }

        //

        MImpFormatRow[] rows = from.getRows();

        for( int i = 0;i < rows.length;i++ ) {
            MImpFormatRow row  = rows[ i ];
            MImpFormatRow copy = new MImpFormatRow( to,row );

            if( !copy.save()) {
                throw new Exception( "Copy error" );
            }
        }

        String msg = "#" + rows.length;

        if( !from.getFormatType().equals( to.getFormatType())) {
            return msg + " - Note: Format Type different!";
        }

        return msg;
    }    // doIt
}    // CopyImportFormat



/*
 *  @(#)CopyImportFormat.java   02.07.07
 * 
 *  Fin del fichero CopyImportFormat.java
 *  
 *  Versión 2.2
 *
 */
