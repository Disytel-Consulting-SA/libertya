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



package org.openXpertya.apps;

import java.util.ListResourceBundle;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class ALoginRes extends ListResourceBundle {

    /** Descripción de Campos */

    static final Object[][] contents = new String[][] {
        { "Connection","Connection" },{ "Defaults","Defaults" },{ "Login","OpenXpertya Login" },{ "File","File" },{ "Exit","Exit" },{ "Help","Help" },{ "About","About" },{ "Host","Host" },{ "Database","Database" },{ "User","User ID" },{ "EnterUser","Enter Application User ID" },{ "Password","Password" },{ "EnterPassword","Enter Application Password" },{ "Language","Language" },{ "SelectLanguage","Select your language" },{ "Role","Role" },{ "Client","Client" },{ "Organization","Organization" },{ "Date","Date" },{ "Warehouse","Warehouse" },{ "Printer","Printer" },{ "Connected","Connected" },{ "NotConnected","Not Connected" },{ "DatabaseNotFound","Database not found" },{ "UserPwdError","User does not match password" },{ "RoleNotFound","Role not found/complete" },{ "Authorized","Authorized" },{ "Ok","Ok" },{ "Cancel","Cancel" },{ "VersionConflict","Version Conflict:" },{ "VersionInfo","Server <> Client" },{ "PleaseUpgrade","Please run the update program" }
    };

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object[][] getContents() {
        return contents;
    }    // getContents
}    // ALoginRes



/*
 *  @(#)ALoginRes.java   02.07.07
 * 
 *  Fin del fichero ALoginRes.java
 *  
 *  Versión 2.2
 *
 */
