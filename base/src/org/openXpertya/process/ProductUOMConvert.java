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

import org.openXpertya.model.MProduct;
import org.openXpertya.model.MUOM;
import org.openXpertya.model.MUOMConversion;
import org.openXpertya.util.Env;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProductUOMConvert extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_Product_ID = 0;

    /** Descripción de Campos */

    private int p_M_Product_To_ID = 0;

    /** Descripción de Campos */

    private int p_M_Locator_ID = 0;

    /** Descripción de Campos */

    private BigDecimal p_Qty = null;

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
            } else if( name.equals( "M_Product_ID" )) {
                p_M_Product_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_Product_To_ID" )) {
                p_M_Product_To_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_Locator_ID" )) {
                p_M_Locator_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "Qty" )) {
                p_Qty = ( BigDecimal )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
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
        if( (p_M_Product_ID == 0) || (p_M_Product_To_ID == 0) || (p_M_Locator_ID == 0) || (p_Qty == null) || (Env.ZERO.compareTo( p_Qty ) == 0) ) {
            throw new ErrorUsuarioOXP( "Invalid Parameter" );
        }

        //

        MProduct product   = MProduct.get( getCtx(),p_M_Product_ID );
        MProduct productTo = MProduct.get( getCtx(),p_M_Product_To_ID );

        log.info( "Product=" + product + ", ProductTo=" + productTo + ", M_Locator_ID=" + p_M_Locator_ID + ", Qty=" + p_Qty );

        MUOMConversion[] conversions = MUOMConversion.getProductConversions( getCtx(),product.getM_Product_ID());
        MUOMConversion conversion = null;

        for( int i = 0;i < conversions.length;i++ ) {
            if( conversions[ i ].getC_UOM_To_ID() == productTo.getC_UOM_ID()) {
                conversion = conversions[ i ];
            }
        }

        if( conversion == null ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @C_UOM_Conversion_ID@" );
        }

        MUOM       uomTo = MUOM.get( getCtx(),productTo.getC_UOM_ID());
        BigDecimal qtyTo = p_Qty.divide( conversion.getDivideRate(),uomTo.getStdPrecision(),BigDecimal.ROUND_HALF_UP );
        BigDecimal qtyTo6 = p_Qty.divide( conversion.getDivideRate(),6,BigDecimal.ROUND_HALF_UP );

        if( qtyTo.compareTo( qtyTo6 ) != 0 ) {
            throw new ErrorUsuarioOXP( "@StdPrecision@: " + qtyTo + " <> " + qtyTo6 + " (" + p_Qty + "/" + conversion.getDivideRate() + ")" );
        }

        log.info( conversion + " -> " + qtyTo );

        // Set to Beta

        return "Not completed yet";
    }    // doIt
}    // ProductUOMConvert



/*
 *  @(#)ProductUOMConvert.java   02.07.07
 * 
 *  Fin del fichero ProductUOMConvert.java
 *  
 *  Versión 2.2
 *
 */
