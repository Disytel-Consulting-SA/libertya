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

import java.util.Properties;

/**
 * Descripción de Interface
 *
 *
 * @version    2.2, 12.10.07
 * @author         Equipo de Desarrollo de openXpertya    
 */

public interface Callout {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param method
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     * @param oldValue
     *
     * @return
     */

    public String start( Properties ctx,String method,int WindowNo,MTab mTab,MField mField,Object value,Object oldValue );

    /**
	 *	Start Callout.
	 *  <p>
	 *	Callout's are used for cross field validation and setting values in other fields
	 *	when returning a non empty (error message) string, an exception is raised
	 *  <p>
	 *	When invoked, the Tab model has the new value!
	 *
	 *  @param ctx      Context
	 *  @param method   Method name
	 *  @param WindowNo current Window No
	 *  @param mTab     Model Tab
	 *  @param mField   Model Field
	 *  @param value    The new value
	 *  @param oldValue The old value
	 *  @return Error message or ""
	 */
	public String start (Properties ctx, String method, int WindowNo,
		GridTab mTab, GridField mField, Object value, Object oldValue);
    
    /**
     * Descripción de Método
     *
     *
     * @param method
     * @param value
     *
     * @return
     */

    public String convert( String method,String value );
    
    
    
    public boolean isPluginInstance();
}    // callout



/*
 *  @(#)Callout.java   02.07.07
 * 
 *  Fin del fichero Callout.java
 *  
 *  Versión 2.2
 *
 */
