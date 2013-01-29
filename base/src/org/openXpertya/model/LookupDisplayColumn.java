/*
 * @(#)LookupDisplayColumn.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.io.Serializable;

/**
 *  Lookup Display Column Value Object
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: LookupDisplayColumn.java,v 1.4 2005/03/11 20:28:35 jjanke Exp $
 */
public class LookupDisplayColumn implements Serializable {

    /** Descripción de Campo */
    public int	AD_Reference_ID;

    /** Descripción de Campo */
    public String	ColumnName;

    /** Descripción de Campo */
    public int	DisplayType;

    /** Descripción de Campo */
    public boolean	IsTranslated;

    /**
     *      Lookup Column Value Object
     *      @param columnName column name
     *      @param isTranslated translated
     *      @param ad_Reference_ID display type
     *      @param ad_Reference_Value_ID table/list reference id
     */
    public LookupDisplayColumn(String columnName, boolean isTranslated, int ad_Reference_ID, int ad_Reference_Value_ID) {

        ColumnName	= columnName;
        IsTranslated	= isTranslated;
        DisplayType	= ad_Reference_ID;
        AD_Reference_ID	= ad_Reference_Value_ID;

    }		//

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("LookupDisplayColumn[");

        sb.append("ColumnName=").append(ColumnName);

        if (IsTranslated) {
            sb.append(",IsTranslated");
        }

        sb.append(",DisplayType=").append(DisplayType);

        if (AD_Reference_ID != 0) {
            sb.append(",AD_Reference_ID=").append(AD_Reference_ID);
        }

        sb.append("]");

        return sb.toString();

    }		// toString
}	// LookupDisplayColumn



/*
 * @(#)LookupDisplayColumn.java   02.jul 2007
 * 
 *  Fin del fichero LookupDisplayColumn.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
