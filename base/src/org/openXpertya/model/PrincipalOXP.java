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

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrincipalOXP implements Principal {

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param password
     * @param roles
     */

    public PrincipalOXP( String name,String password,List roles ) {
        super();
        m_name     = name;
        m_password = password;

        if( roles != null ) {
            m_roles = new String[ roles.size()];
            m_roles = ( String[] )roles.toArray( m_roles );

            if( m_roles.length > 0 ) {
                Arrays.sort( m_roles );
            }
        }
    }    // PrincipalOXP

    /** Descripción de Campos */

    private String m_name = null;

    /** Descripción de Campos */

    private String m_password = null;

    /** Descripción de Campos */

    private String m_roles[] = new String[ 0 ];

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    String getPassword() {
        return m_password;
    }    // getPassword

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    String[] getRoles() {
        return m_roles;
    }    // getRoles

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "PrincipalOXP[" );

        sb.append( m_name ).append( ": " );

        for( int i = 0;i < m_roles.length;i++ ) {
            sb.append( m_roles[ i ] ).append( " " );
        }

        sb.append( "]" );

        return( sb.toString());
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param role
     *
     * @return
     */

    public boolean hasRole( String role ) {
        if( role == null ) {
            return false;
        }

        return( Arrays.binarySearch( m_roles,role ) >= 0 );
    }    // hasRole
}    // PrincipalOXP



/*
 *  @(#)PrincipalOXP.java   02.07.07
 * 
 *  Fin del fichero PrincipalOXP.java
 *  
 *  Versión 2.2
 *
 */
