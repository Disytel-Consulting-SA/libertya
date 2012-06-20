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

import java.util.logging.Level;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class PaymentProcessor {

    /**
     * Constructor de la clase ...
     *
     */

    public PaymentProcessor() {}    // PaymentProcessor

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    static private CLogger s_log = CLogger.getCLogger( PaymentProcessor.class );

    /**
     * Descripción de Método
     *
     *
     * @param mpp
     * @param mp
     *
     * @return
     */

    public static PaymentProcessor create( MPaymentProcessor mpp,MPayment mp ) {
        s_log.info( "create for " + mpp );

        String className = mpp.getPayProcessorClass();

        if( (className == null) || (className.length() == 0) ) {
            s_log.log( Level.SEVERE,"create - no PaymentProcessor class name in " + mpp );

            return null;
        }

        //

        PaymentProcessor myProcessor = null;

        try {
            Class ppClass = Class.forName( className );

            if( ppClass != null ) {
                myProcessor = ( PaymentProcessor )ppClass.newInstance();
            }
        } catch( Error e1 )    // NoClassDefFound
        {
            s_log.log( Level.SEVERE,"create " + className + " - Error=" + e1.getMessage());

            return null;
        } catch( Exception e2 ) {
            s_log.log( Level.SEVERE,"create " + className,e2 );

            return null;
        }

        if( myProcessor == null ) {
            s_log.log( Level.SEVERE,"create - no class" );

            return null;
        }

        // Initialize

        myProcessor.p_mpp = mpp;
        myProcessor.p_mp  = mp;

        //

        return myProcessor;
    }    // create

    /** Descripción de Campos */

    protected MPaymentProcessor p_mpp = null;

    /** Descripción de Campos */

    protected MPayment p_mp = null;

    //

    /** Descripción de Campos */

    private int m_timeout = 30;

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws IllegalArgumentException
     */

    public abstract boolean processCC() throws IllegalArgumentException;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract boolean isProcessedOK();

    /**
     * Descripción de Método
     *
     *
     * @param newTimeout
     */

    public void setTimeout( int newTimeout ) {
        m_timeout = newTimeout;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTimeout() {
        return m_timeout;
    }
}    // PaymentProcessor



/*
 *  @(#)PaymentProcessor.java   02.07.07
 * 
 *  Fin del fichero PaymentProcessor.java
 *  
 *  Versión 2.2
 *
 */
