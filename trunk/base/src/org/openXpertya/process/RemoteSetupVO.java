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

import java.io.Serializable;
import java.math.BigDecimal;

import javax.sql.RowSet;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RemoteSetupVO implements Serializable {

    /** Descripción de Campos */

    public Boolean Test = Boolean.FALSE;

    /** Descripción de Campos */

    public RowSet ReplicationTable = null;

    /** Descripción de Campos */

    public BigDecimal IDRangeStart = null;

    /** Descripción de Campos */

    public BigDecimal IDRangeEnd = null;

    /** Descripción de Campos */

    public int AD_Client_ID = -1;

    /** Descripción de Campos */

    public int AD_Org_ID = -1;

    /** Descripción de Campos */

    public String Prefix = null;

    /** Descripción de Campos */

    public String Suffix = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "RemoteSetupVO[test=" + Test + ",IDRange=" + IDRangeStart + "-" + IDRangeEnd + ",AD_Client_ID=" + AD_Client_ID + ",AD_Org_ID=" + AD_Org_ID + ",Prefix=" + Prefix + ",Suffix=" + Suffix + "]";
    }    // toString
}    // RemoteSetupVO



/*
 *  @(#)RemoteSetupVO.java   02.07.07
 * 
 *  Fin del fichero RemoteSetupVO.java
 *  
 *  Versión 2.2
 *
 */
