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

public class ErrorUsuarioOXP extends Exception {

    /**
     * Constructor de la clase ...
     *
     *
     * @param message
     */

    public ErrorUsuarioOXP( String message ) {
        super( message );
    }    // ErrorUsuarioOXP

    /**
     * Constructor de la clase ...
     *
     *
     * @param message
     * @param fixHint
     */

    public ErrorUsuarioOXP( String message,String fixHint ) {
        super( message );
        setFixHint( fixHint );
    }    // ErrorUsuarioOXP

    /**
     * Constructor de la clase ...
     *
     *
     * @param message
     * @param cause
     */

    public ErrorUsuarioOXP( String message,Throwable cause ) {
        super( message,cause );
    }    // ErrorUsuarioOXP

    /** Descripción de Campos */

    private String m_fixHint = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFixHint() {
        return m_fixHint;
    }    // getFixHint

    /**
     * Descripción de Método
     *
     *
     * @param fixHint
     */

    public void setFixHint( String fixHint ) {
        m_fixHint = fixHint;
    }    // setFixHint

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        super.toString();

        StringBuffer sb = new StringBuffer( "UserError: " );

        sb.append( getLocalizedMessage());

        if( (m_fixHint != null) && (m_fixHint.length() > 0) ) {
            sb.append( " (" ).append( m_fixHint ).append( ")" );
        }

        return sb.toString();
    }    // toString
}    // ErrorUsuarioOXP



/*
 *  @(#)ErrorUsuarioOXP.java   25.03.06
 * 
 *  Fin del fichero ErrorUsuarioOXP.java
 *  
 *  Versión 2.2
 *
 */
