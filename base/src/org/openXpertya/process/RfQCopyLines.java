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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.model.MRfQ;
import org.openXpertya.model.MRfQLine;
import org.openXpertya.model.MRfQLineQty;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RfQCopyLines extends SvrProcess {

    /** Descripción de Campos */

    private int p_From_RfQ_ID = 0;

    /** Descripción de Campos */

    private int p_To_RfQ_ID = 0;

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
            } else if( name.equals( "C_RfQ_ID" )) {
                p_From_RfQ_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_To_RfQ_ID = getRecord_ID();
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
        log.info( "doIt - From_RfQ_ID=" + p_From_RfQ_ID + ", To_RfQ_ID=" + p_To_RfQ_ID );

        //

        MRfQ to = new MRfQ( getCtx(),p_To_RfQ_ID,get_TrxName());

        if( to.getID() == 0 ) {
            throw new IllegalArgumentException( "No To RfQ found" );
        }

        MRfQ from = new MRfQ( getCtx(),p_From_RfQ_ID,get_TrxName());

        if( from.getID() == 0 ) {
            throw new IllegalArgumentException( "No From RfQ found" );
        }

        // Copy Lines

        int        counter = 0;
        MRfQLine[] lines   = from.getLines();

        for( int i = 0;i < lines.length;i++ ) {
            MRfQLine newLine = new MRfQLine( to );

            newLine.setLine( lines[ i ].getLine());
            newLine.setDescription( lines[ i ].getDescription());
            newLine.setHelp( lines[ i ].getHelp());
            newLine.setM_Product_ID( lines[ i ].getM_Product_ID());
            newLine.setM_AttributeSetInstance_ID( lines[ i ].getM_AttributeSetInstance_ID());

            // newLine.setDateWorkStart();
            // newLine.setDateWorkComplete();

            newLine.setDeliveryDays( lines[ i ].getDeliveryDays());
            newLine.save();

            // Copy Qtys

            MRfQLineQty[] qtys = lines[ i ].getQtys();

            for( int j = 0;j < qtys.length;j++ ) {
                MRfQLineQty newQty = new MRfQLineQty( newLine );

                newQty.setC_UOM_ID( qtys[ j ].getC_UOM_ID());
                newQty.setQty( qtys[ j ].getQty());
                newQty.setIsOfferQty( qtys[ j ].isOfferQty());
                newQty.setIsPurchaseQty( qtys[ j ].isPurchaseQty());
                newQty.setMargin( qtys[ j ].getMargin());
                newQty.save();
            }

            counter++;
        }    // copy all lines

        //

        return "# " + counter;
    }    // doIt
}



/*
 *  @(#)RfQCopyLines.java   02.07.07
 * 
 *  Fin del fichero RfQCopyLines.java
 *  
 *  Versión 2.2
 *
 */
