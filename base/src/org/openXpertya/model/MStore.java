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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MStore extends X_W_Store {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param W_Store_ID
     * @param trxName
     */

    public MStore( Properties ctx,int W_Store_ID,String trxName ) {
        super( ctx,W_Store_ID,trxName );
    }    // MStore

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MStore( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MStore

    /** Descripción de Campos */

    private MMailMsg[] m_msgs = null;

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MMailMsg[] getMailMsgs( boolean reload ) {
        if( (m_msgs != null) &&!reload ) {
            return m_msgs;
        }

        ArrayList list = new ArrayList();

        //

        String sql = "SELECT * FROM W_MailMsg WHERE W_Store_ID=? ORDER BY MailMsgType";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getW_Store_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MMailMsg( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        m_msgs = new MMailMsg[ list.size()];
        list.toArray( m_msgs );

        return m_msgs;
    }    // getMailMsgs

    /**
     * Descripción de Método
     *
     *
     * @param MailMsgType
     *
     * @return
     */

    public MMailMsg getMailMsg( String MailMsgType ) {
        if( m_msgs == null ) {
            getMailMsgs( false );
        }

        // existing msg

        for( int i = 0;i < m_msgs.length;i++ ) {
            if( m_msgs[ i ].getMailMsgType().equals( MailMsgType )) {
                return m_msgs[ i ];
            }
        }

        // create missing

        if( createMessages() == 0 ) {
            log.severe( "Not created/found: " + MailMsgType );

            return null;
        }

        getMailMsgs( true );

        // try again

        for( int i = 0;i < m_msgs.length;i++ ) {
            if( m_msgs[ i ].getMailMsgType().equals( MailMsgType )) {
                return m_msgs[ i ];
            }
        }

        // nothing found

        log.severe( "Not found: " + MailMsgType );

        return null;
    }    // getMailMsg

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int createMessages() {
        String[][] initMsgs = new String[][] {
            new String[]{ MMailMsg.MAILMSGTYPE_OrderAcknowledgement,"Order Acknowledgement","Subject","Message" },new String[]{ MMailMsg.MAILMSGTYPE_PaymentAcknowledgement,"","","" },new String[]{ MMailMsg.MAILMSGTYPE_PaymentError,"","","" },new String[]{ MMailMsg.MAILMSGTYPE_UserPassword,"","","" },new String[]{ MMailMsg.MAILMSGTYPE_UserValidation,"","","" }
        };

        if( m_msgs == null ) {
            getMailMsgs( false );
        }

        if( m_msgs.length == initMsgs.length ) {    // may create a problem if user defined own ones - unlikely
            return 0;    // nothing to do
        }

        int counter = 0;

        for( int i = 0;i < initMsgs.length;i++ ) {
            boolean found = false;

            for( int m = 0;m < m_msgs.length;m++ ) {
                if( initMsgs[ i ][ 0 ].equals( m_msgs[ m ].getMailMsgType())) {
                    found = true;

                    break;
                }
            }    // for all existing msgs

            if( found ) {
                continue;
            }

            MMailMsg msg = new MMailMsg( this,initMsgs[ i ][ 0 ],initMsgs[ i ][ 1 ],initMsgs[ i ][ 2 ],initMsgs[ i ][ 3 ] );

            if( msg.save()) {
                counter++;
            } else {
                log.severe( "Not created MailMsgType=" + initMsgs[ i ][ 0 ] );
            }
        }    // for all initMsgs

        log.info( "#" + counter );
        m_msgs = null;    // reset

        return counter;
    }    // createMessages
}    // MStore



/*
 *  @(#)MStore.java   02.07.07
 * 
 *  Fin del fichero MStore.java
 *  
 *  Versión 2.2
 *
 */
