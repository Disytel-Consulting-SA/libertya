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

import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DiscountSchemaReSeq extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_DiscountSchema_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        p_M_DiscountSchema_ID = getRecord_ID();
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
        log.info( "M_DiscountSchema_ID=" + p_M_DiscountSchema_ID );

        if( p_M_DiscountSchema_ID == 0 ) {
            throw new ErrorUsuarioOXP( "@M_DiscountSchema_ID@ = 0" );
        }

        MDiscountSchema ds = new MDiscountSchema( getCtx(),p_M_DiscountSchema_ID,get_TrxName());

        if( ds.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ M_DiscountSchema_ID=" + p_M_DiscountSchema_ID );
        }

        //

        int updated = ds.reSeq();

        return "@Updated@ #" + updated;
    }    // doIt
}    // DiscountSchemaRenumber



/*
 *  @(#)DiscountSchemaReSeq.java   02.07.07
 * 
 *  Fin del fichero DiscountSchemaReSeq.java
 *  
 *  Versión 2.2
 *
 */
