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

import javax.swing.SwingUtilities;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class SwingWorker {

    /**
     * Constructor de la clase ...
     *
     */
	protected Object owner = null;
	
	
    public SwingWorker() {
        final Runnable doFinished = new Runnable() {
            public void run() {
                finished();
            }
        };
        Runnable doConstruct = new Runnable() {
            public void run() {
                try {
                    setValue( construct());
                } finally {
                    m_threadVar.clear();
                }

                SwingUtilities.invokeLater( doFinished );
            }
        };
        Thread t = new Thread( doConstruct );

        m_threadVar = new ThreadVar( t );
    }    // SwingWorker

    /** Descripción de Campos */

    private ThreadVar m_threadVar;

    /** Descripción de Campos */

    private Object m_value;    // see getValue(), setValue()

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract Object construct();

    /**
     * Descripción de Método
     *
     */

    public void finished() {}    // finished

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected synchronized Object getValue() {
        return m_value;
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @param x
     */

    private synchronized void setValue( Object x ) {
        m_value = x;
    }    // setValue

    /**
     * Descripción de Método
     *
     */

    public void start() {
        Thread t = m_threadVar.get();

        if( t != null ) {
            t.start();
        }
    }    // start

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object get() {
        while( true ) {
            Thread t = m_threadVar.get();

            if( t == null ) {
                return getValue();
            }

            try {
                t.join();
            } catch( InterruptedException e ) {
                Thread.currentThread().interrupt();    // propagate

                return null;
            }
        }
    }                                                  // get

    /**
     * Descripción de Método
     *
     */

    public void interrupt() {
        Thread t = m_threadVar.get();

        if( t != null ) {
            t.interrupt();
        }

        m_threadVar.clear();
    }    // interrupt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isAlive() {
        Thread t = m_threadVar.get();

        if( t == null ) {
            return false;
        }

        return t.isAlive();
    }    // isAlive

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    private static class ThreadVar {

        /**
         * Constructor de la clase ...
         *
         *
         * @param t
         */

        ThreadVar( Thread t ) {
            thread = t;
        }

        /** Descripción de Campos */

        private Thread thread;

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        synchronized Thread get() {
            return thread;
        }    // get

        /**
         * Descripción de Método
         *
         */

        synchronized void clear() {
            thread = null;
        }    // clear
    }    // ThreadVar

	public Object getOwner() {
		return owner;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}
}        // SwingWorker



/*
 *  @(#)SwingWorker.java   02.07.07
 * 
 *  Fin del fichero SwingWorker.java
 *  
 *  Versión 2.2
 *
 */
