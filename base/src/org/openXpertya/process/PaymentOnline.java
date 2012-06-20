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

//import org.openXpertya.process.*;

import java.util.logging.Level;

import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPaymentValidate;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PaymentOnline extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_Payment_ID = -1;

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
        log.info( "doIt - Record_ID=" + getRecord_ID());

        // get Payment

        MPayment pp = new MPayment( getCtx(),getRecord_ID(),get_TrxName());

        // Validate Number

        String msg = MPaymentValidate.validateCreditCardNumber( pp.getCreditCardNumber(),pp.getCreditCardType());

        if( (msg != null) && (msg.length() > 0) ) {
            throw new IllegalArgumentException( Msg.getMsg( getCtx(),msg ));
        }

        msg = MPaymentValidate.validateCreditCardExp( pp.getCreditCardExpMM(),pp.getCreditCardExpYY());

        if( (msg != null) && (msg.length() > 0) ) {
            throw new IllegalArgumentException( Msg.getMsg( getCtx(),msg ));
        }

        if( pp.getCreditCardVV().length() > 0 ) {
            msg = MPaymentValidate.validateCreditCardVV( pp.getCreditCardVV(),pp.getCreditCardType());

            if( (msg != null) && (msg.length() > 0) ) {
                throw new IllegalArgumentException( Msg.getMsg( getCtx(),msg ));
            }
        }

        // Process it

        boolean ok = pp.processOnline();

        pp.save();

        if( !ok ) {
            throw new Exception( pp.getErrorMessage());
        }

        return "OK";
    }    // doIt
}    // PaymentOnline



/*
 *  @(#)PaymentOnline.java   02.07.07
 * 
 *  Fin del fichero PaymentOnline.java
 *  
 *  Versión 2.2
 *
 */
