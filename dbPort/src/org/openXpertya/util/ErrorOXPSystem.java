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



package org.openXpertya.util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ErrorOXPSystem extends Exception {

    /**
     * Constructor de la clase ...
     *
     *
     * @param message
     */

    public ErrorOXPSystem( String message ) {
        super( message );
    }    // ErrorOXPSystem

    /**
     * Constructor de la clase ...
     *
     *
     * @param message
     * @param detail
     */

    public ErrorOXPSystem( String message,Object detail ) {
        super( message );
        setDetail( detail );
    }    // ErrorOXPSystem

    /**
     * Constructor de la clase ...
     *
     *
     * @param message
     * @param cause
     */

    public ErrorOXPSystem( String message,Throwable cause ) {
        super( message,cause );
    }    // ErrorOXPSystem

    /** Descripción de Campos */

    private Object m_detail = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getDetail() {
        return m_detail;
    }

    /**
     * Descripción de Método
     *
     *
     * @param detail
     */

    public void setDetail( Object detail ) {
        m_detail = detail;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        super.toString();

        StringBuffer sb = new StringBuffer( "SystemError: " );

        sb.append( getLocalizedMessage());

        if( m_detail != null ) {
            sb.append( " (" ).append( m_detail ).append( ")" );
        }

        return sb.toString();
    }    // toString
}    // ErrorOXPSystem



/*
 *  @(#)ErrorOXPSystem.java   25.03.06
 * 
 *  Fin del fichero ErrorOXPSystem.java
 *  
 *  Versión 2.2
 *
 */
