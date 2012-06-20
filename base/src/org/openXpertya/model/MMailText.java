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

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MMailText extends X_R_MailText {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_MailText_ID
     * @param trxName
     */

    public MMailText( Properties ctx,int R_MailText_ID,String trxName ) {
        super( ctx,R_MailText_ID,trxName );
    }    // MMailText

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMailText( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MMailText

    /**
     * Descripción de Método
     *
     *
     * @param all
     *
     * @return
     */

    public String getMailText( boolean all ) {
        if( !all ) {
            return super.getMailText();
        }

        //

        StringBuffer sb = new StringBuffer();

        sb.append( super.getMailText());

        String s = getMailText2();

        if( (s != null) && (s.length() > 0) ) {
            sb.append( Env.NL ).append( s );
        }

        s = getMailText3();

        if( (s != null) && (s.length() > 0) ) {
            sb.append( Env.NL ).append( s );
        }

        return sb.toString();
    }    // getMailText
}    // MMailText



/*
 *  @(#)MMailText.java   02.07.07
 * 
 *  Fin del fichero MMailText.java
 *  
 *  Versión 2.2
 *
 */
