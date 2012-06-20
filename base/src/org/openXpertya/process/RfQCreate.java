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

import org.openXpertya.model.MRfQ;
import org.openXpertya.model.MRfQResponse;
import org.openXpertya.model.MRfQTopic;
import org.openXpertya.model.MRfQTopicSubscriber;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RfQCreate extends SvrProcess {

    /** Descripción de Campos */

    private boolean p_IsSendRfQ = false;

    /** Descripción de Campos */

    private int p_C_RfQ_ID = 0;

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
            } else if( name.equals( "IsSendRfQ" )) {
                p_IsSendRfQ = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_C_RfQ_ID = getRecord_ID();
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
        MRfQ rfq = new MRfQ( getCtx(),p_C_RfQ_ID,get_TrxName());

        log.info( "doIt - " + rfq + ", Send=" + p_IsSendRfQ );

        String error = rfq.checkQuoteTotalAmtOnly();

        if( (error != null) && (error.length() > 0) ) {
            throw new Exception( error );
        }

        int counter = 0;
        int sent    = 0;
        int notSent = 0;

        // Get all existing responses

        MRfQResponse[] responses = rfq.getResponses( false,false );

        // Topic

        MRfQTopic topic = new MRfQTopic( getCtx(),rfq.getC_RfQ_Topic_ID(),get_TrxName());
        MRfQTopicSubscriber[] subscribers = topic.getSubscribers();

        for( int i = 0;i < subscribers.length;i++ ) {
            MRfQTopicSubscriber subscriber = subscribers[ i ];
            boolean             skip       = false;

            // existing response

            for( int r = 0;r < responses.length;r++ ) {
                if( (subscriber.getC_BPartner_ID() == responses[ r ].getC_BPartner_ID()) && (subscriber.getC_BPartner_Location_ID() == responses[ r ].getC_BPartner_Location_ID())) {
                    skip = true;

                    break;
                }
            }

            if( skip ) {
                continue;
            }

            // Create Response

            MRfQResponse response = new MRfQResponse( rfq,subscriber );

            if( response.getID() == 0 ) {    // no lines
                continue;
            }

            counter++;

            if( p_IsSendRfQ ) {
                if( response.sendRfQ()) {
                    sent++;
                } else {
                    notSent++;
                }
            }
        }                                    // for all subscribers

        String retValue = "@Created@ " + counter;

        if( p_IsSendRfQ ) {
            retValue += " - @IsSendRfQ@=" + sent + " - @Error@=" + notSent;
        }

        return retValue;
    }    // doIt
}    // RfQCreate



/*
 *  @(#)RfQCreate.java   02.07.07
 * 
 *  Fin del fichero RfQCreate.java
 *  
 *  Versión 2.2
 *
 */
