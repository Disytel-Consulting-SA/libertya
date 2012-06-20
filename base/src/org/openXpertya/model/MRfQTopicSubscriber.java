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

public class MRfQTopicSubscriber extends X_C_RfQ_TopicSubscriber {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_RfQ_TopicSubscriber_ID
     * @param trxName
     */

    public MRfQTopicSubscriber( Properties ctx,int C_RfQ_TopicSubscriber_ID,String trxName ) {
        super( ctx,C_RfQ_TopicSubscriber_ID,trxName );
    }    // MRfQTopic_Subscriber

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRfQTopicSubscriber( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRfQTopic_Subscriber

    /** Descripción de Campos */

    private MRfQTopicSubscriberOnly[] m_restrictions = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MRfQTopicSubscriberOnly[] getRestrictions( boolean requery ) {
        if( (m_restrictions != null) &&!requery ) {
            return m_restrictions;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_RfQ_TopicSubscriberOnly WHERE C_RfQ_TopicSubscriber_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_RfQ_TopicSubscriber_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRfQTopicSubscriberOnly( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getOnlys",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_restrictions = new MRfQTopicSubscriberOnly[ list.size()];
        list.toArray( m_restrictions );

        return m_restrictions;
    }    // getRestrictions

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     *
     * @return
     */

    public boolean isIncluded( int M_Product_ID ) {

        // No restrictions

        if( getRestrictions( false ).length == 0 ) {
            return true;
        }

        for( int i = 0;i < m_restrictions.length;i++ ) {
            MRfQTopicSubscriberOnly restriction = m_restrictions[ i ];

            if( !restriction.isActive()) {
                continue;
            }

            // Product

            if( restriction.getM_Product_ID() == M_Product_ID ) {
                return true;
            }

            // Product Category

            if( MProductCategory.isCategory( restriction.getM_Product_Category_ID(),M_Product_ID )) {
                return true;
            }
        }

        // must be on "positive" list

        return false;
    }    // isIncluded
}    // MRfQTopicSubscriber



/*
 *  @(#)MRfQTopicSubscriber.java   02.07.07
 * 
 *  Fin del fichero MRfQTopicSubscriber.java
 *  
 *  Versión 2.2
 *
 */
