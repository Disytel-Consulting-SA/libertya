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

public class MAlertRecipient extends X_AD_AlertRecipient {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_AlertRecipient_ID
     * @param trxName
     */

    public MAlertRecipient( Properties ctx,int AD_AlertRecipient_ID,String trxName ) {
        super( ctx,AD_AlertRecipient_ID,trxName );
    }    // MAlertRecipient

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAlertRecipient( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAlertRecipient

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_User_ID() {
        Integer ii = ( Integer )get_Value( "AD_User_ID" );

        if( ii == null ) {
            return -1;
        }

        return ii.intValue();
    }    // getAD_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Role_ID() {
        Integer ii = ( Integer )get_Value( "AD_Role_ID" );

        if( ii == null ) {
            return -1;
        }

        return ii.intValue();
    }    // getAD_Role_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MAlertRecipient[" );

        sb.append( getID()).append( ",AD_User_ID=" ).append( getAD_User_ID()).append( ",AD_Role_ID=" ).append( getAD_Role_ID()).append( "]" );

        return sb.toString();
    }    // toString
}    // MAlertRecipient



/*
 *  @(#)MAlertRecipient.java   02.07.07
 * 
 *  Fin del fichero MAlertRecipient.java
 *  
 *  Versión 2.2
 *
 */
