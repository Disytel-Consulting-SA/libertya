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

public class MAttributeValue extends X_M_AttributeValue {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_AttributeValue_ID
     * @param trxName
     */

    public MAttributeValue( Properties ctx,int M_AttributeValue_ID,String trxName ) {
        super( ctx,M_AttributeValue_ID,trxName );
    }    // MAttributeValue

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAttributeValue( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAttributeValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return getName();
    }    // toString
}    // MAttributeValue



/*
 *  @(#)MAttributeValue.java   02.07.07
 * 
 *  Fin del fichero MAttributeValue.java
 *  
 *  Versión 2.2
 *
 */
