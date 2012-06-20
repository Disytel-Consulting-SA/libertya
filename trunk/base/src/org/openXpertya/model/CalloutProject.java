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

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutProject extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String planned( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        setCalloutActive( true );

        BigDecimal PlannedQty,PlannedPrice;
        int        StdPrecision = Env.getContextAsInt( ctx,WindowNo,"StdPrecision" );

        // get values

        PlannedQty = ( BigDecimal )mTab.getValue( "PlannedQty" );

        if( PlannedQty == null ) {
            PlannedQty = Env.ONE;
        }

        PlannedPrice = (( BigDecimal )mTab.getValue( "PlannedPrice" ));

        if( PlannedPrice == null ) {
            PlannedPrice = Env.ZERO;
        }

        //

        BigDecimal PlannedAmt = PlannedQty.multiply( PlannedPrice );

        if( PlannedAmt.scale() > StdPrecision ) {
            PlannedAmt = PlannedAmt.setScale( StdPrecision,BigDecimal.ROUND_HALF_UP );
        }

        //

        log.fine( "PlannedQty=" + PlannedQty + " * PlannedPrice=" + PlannedPrice + " -> PlannedAmt=" + PlannedAmt + " (Precision=" + StdPrecision + ")" );
        mTab.setValue( "PlannedAmt",PlannedAmt );
        setCalloutActive( false );

        return "";
    }    // planned
}    // CalloutProject



/*
 *  @(#)CalloutProject.java   02.07.07
 * 
 *  Fin del fichero CalloutProject.java
 *  
 *  Versión 2.2
 *
 */
