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

public class MMailMsg extends X_W_MailMsg {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param W_MailMsg_ID
     * @param trxName
     */

    public MMailMsg( Properties ctx,int W_MailMsg_ID,String trxName ) {
        super( ctx,W_MailMsg_ID,trxName );

        if( W_MailMsg_ID == 0 ) {

            // setW_Store_ID (0);
            // setMailMsgType (null);
            // setName (null);
            // setSubject (null);
            // setMessage (null);

        }
    }    // MMailMsg

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMailMsg( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MMailMsg

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param MailMsgType
     * @param Name
     * @param Subject
     * @param Message
     */

    public MMailMsg( MStore parent,String MailMsgType,String Name,String Subject,String Message ) {
        this( parent.getCtx(),0,parent.get_TrxName());;
        setClientOrg( parent );
        setW_Store_ID( parent.getW_Store_ID());
        setMailMsgType( MailMsgType );
        setName( Name );
        setSubject( Subject );
        setMessage( Message );
    }    // MMailMsg
}    // MMailMsg



/*
 *  @(#)MMailMsg.java   02.07.07
 * 
 *  Fin del fichero MMailMsg.java
 *  
 *  Versión 2.2
 *
 */
