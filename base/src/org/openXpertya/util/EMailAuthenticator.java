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

import java.util.logging.Level;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class EMailAuthenticator extends Authenticator {

    /**
     * Constructor de la clase ...
     *
     *
     * @param username
     * @param password
     */

    public EMailAuthenticator( String username,String password ) {
        m_pass = new PasswordAuthentication( username,password );

        if( (username == null) || (username.length() == 0) ) {
            log.log( Level.SEVERE,"Username is NULL" );
            Thread.dumpStack();
        }

        if( (password == null) || (password.length() == 0) ) {
            log.log( Level.SEVERE,"Password is NULL" );
            Thread.dumpStack();
        }
    }    // EMailAuthenticator

    /** Descripción de Campos */

    private PasswordAuthentication m_pass;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( EMailAuthenticator.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected PasswordAuthentication getPasswordAuthentication() {
        return m_pass;
    }    // getPasswordAuthentication

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        if( m_pass == null ) {
            return "EMailAuthenticator[]";
        }

        if( CLogMgt.isLevelFinest()) {
            return "EMailAuthenticator[" + m_pass.getUserName() + "/" + m_pass.getPassword() + "]";
        }

        return "EMailAuthenticator[" + m_pass.getUserName() + "/************]";
    }    // toString
}    // EMailAuthenticator



/*
 *  @(#)EMailAuthenticator.java   02.07.07
 * 
 *  Fin del fichero EMailAuthenticator.java
 *  
 *  Versión 2.2
 *
 */
