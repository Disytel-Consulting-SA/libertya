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

import org.openXpertya.util.EMail;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MUserMail extends X_AD_UserMail {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_UserMail_ID
     * @param trxName
     */

    public MUserMail( Properties ctx,int AD_UserMail_ID,String trxName ) {
        super( ctx,AD_UserMail_ID,trxName );
    }    // MUserMail

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MUserMail( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MUserMail

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param AD_User_ID
     * @param mail
     */

    public MUserMail( MMailText parent,int AD_User_ID,EMail mail ) {
        this( parent.getCtx(),0,parent.get_TableName());
        setClientOrg( parent );
        setAD_User_ID( AD_User_ID );
        setR_MailText_ID( parent.getR_MailText_ID());

        //

        if( mail.isSentOK()) {
            setMessageID( mail.getMessageID());
        } else {
            setMessageID( mail.getSentMsg());
            setIsDelivered( ISDELIVERED_No );
        }
    }    // MUserMail

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param AD_User_ID
     * @param mail
     */

    public MUserMail( MMailMsg parent,int AD_User_ID,EMail mail ) {
        this( parent.getCtx(),0,parent.get_TableName());
        setClientOrg( parent );
        setAD_User_ID( AD_User_ID );
        setW_MailMsg_ID( parent.getW_MailMsg_ID());
    }    // MUserMail

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDelivered() {
        String s = getIsDelivered();

        return (s != null) && ISDELIVERED_Yes.equals( s );
    }    // isDelivered

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDeliveredNo() {
        String s = getIsDelivered();

        return (s == null) || ISDELIVERED_No.equals( s );
    }    // isDelivered

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDeliveredUnknown() {
        String s = getIsDelivered();

        return s == null;
    }    // isDeliveredUnknown
}    // MUserMail



/*
 *  @(#)MUserMail.java   02.07.07
 * 
 *  Fin del fichero MUserMail.java
 *  
 *  Versión 2.2
 *
 */
