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

import java.util.logging.Level;

import org.openXpertya.model.MUser;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class UserPassword extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_User_ID = -1;

    /** Descripción de Campos */

    private String p_OldPassword = null;

    /** Descripción de Campos */

    private String p_NewPassword = null;

    /** Descripción de Campos */

    private String p_NewEmail = null;

    /** Descripción de Campos */

    private String p_NewEmailUser = null;

    /** Descripción de Campos */

    private String p_NewEmailUserPW = null;

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
            } else if( name.equals( "AD_User_ID" )) {
                p_AD_User_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "OldPassword" )) {
                p_OldPassword = ( String )para[ i ].getParameter();
            } else if( name.equals( "NewPassword" )) {
                p_NewPassword = ( String )para[ i ].getParameter();
            } else if( name.equals( "NewEmail" )) {
                p_NewEmail = ( String )para[ i ].getParameter();
            } else if( name.equals( "NewEmailUser" )) {
                p_NewEmailUser = ( String )para[ i ].getParameter();
            } else if( name.equals( "p_NewEmailUserPW" )) {
                p_NewEmailUserPW = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
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
        log.info( "doIt - AD_User_ID=" + p_AD_User_ID + " from " + getAD_User_ID());

        MUser user     = MUser.get( getCtx(),p_AD_User_ID );
        MUser operator = MUser.get( getCtx(),getAD_User_ID());

        log.fine( "doIt - Operator=" + operator );
        log.fine( "doIt - User=" + user );

        // Do we need a password ?

        if( Util.isEmpty( p_OldPassword )    // Password required if you want to change System
                && ( (p_AD_User_ID == 0) ||!operator.isAdministrator())) {
            throw new IllegalArgumentException( "@OldPasswordMandatory@" );
        }

        // Is Password correct if entered ?

        if( !Util.isEmpty( p_OldPassword ) &&!p_OldPassword.equals( user.getPassword())) {
            throw new IllegalArgumentException( "@OldPasswordNoMatch@" );
        }

        // Change Super User

        if( p_AD_User_ID == 0 ) {
            String sql = "UPDATE AD_User SET Updated=SysDate, UpdatedBy=" + getAD_User_ID();

            if( !Util.isEmpty( p_NewPassword )) {
                sql += ", Password=" + DB.TO_STRING( p_NewPassword );
            }

            if( !Util.isEmpty( p_NewEmail )) {
                sql += ", Email=" + DB.TO_STRING( p_NewEmail );
            }

            if( !Util.isEmpty( p_NewEmailUser )) {
                sql += ", EmailUser=" + DB.TO_STRING( p_NewEmailUser );
            }

            if( !Util.isEmpty( p_NewEmailUserPW )) {
                sql += ", EmailUserPW=" + DB.TO_STRING( p_NewEmailUserPW );
            }

            sql += " WHERE AD_User_ID=0";

            if( DB.executeUpdate( sql,get_TrxName()) == 1 ) {
                return "OK";
            } else {
                return "@Error@";
            }
        } else {
            if( !Util.isEmpty( p_NewPassword )) {
                user.setPassword( p_NewPassword );
            }

            if( !Util.isEmpty( p_NewEmail )) {
                user.setEMail( p_NewEmail );
            }

            if( !Util.isEmpty( p_NewEmailUser )) {
                user.setEMailUser( p_NewEmailUser );
            }

            if( !Util.isEmpty( p_NewEmailUserPW )) {
                user.setEMailUserPW( p_NewEmailUserPW );
            }

            //

            if( user.save()) {
                return "OK";
            } else {
                return "@Error@";
            }
        }
    }    // doIt
}    // UserPassword



/*
 *  @(#)UserPassword.java   02.07.07
 * 
 *  Fin del fichero UserPassword.java
 *  
 *  Versión 2.2
 *
 */
