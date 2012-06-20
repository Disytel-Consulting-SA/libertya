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



package org.openXpertya.wf;

import org.openXpertya.process.ServicioOXP;
import org.openXpertya.process.ServidorOXP;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WorkflowServer extends ServidorOXP {

    /**
     * Constructor de la clase ...
     *
     */

    public WorkflowServer() {
        super( "WorkflowServer" + s_no++ );
    }    // WorkflowServer

    /** Descripción de Campos */

    private static int s_no = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MWorkflowProcessor getProcessor() {
        return( MWorkflowProcessor )p_processor;
    }    // getProcessor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getProcessorName() {
        if( getProcessor() == null ) {
            return getName();
        }

        return getProcessor().getName();
    }    // getProcessorName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean canDoWork() {
        log.info( "canDoWork" );

        return true;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean doWork() {
        log.info( getName() + ": doWork - start" );
        log.info( getName() + ": doWork - fini" );

        return true;
    }    // doWork

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "WorkflowServer[" );

        sb.append( getName()).append( ":" ).append( getStatistics()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        System.out.println( new java.awt.Rectangle( 1,2,3,4 ));
        org.openXpertya.OpenXpertya.startup( true );

        MWorkflowProcessor processor = new MWorkflowProcessor( Env.getCtx(),100,null );
        ServicioOXP cs = new ServicioOXP( processor,WorkflowServer.class );

        cs.start();

        int i = 0;

        while( true ) {
            System.out.println( "** (" + i++ + ") " + cs );

            try {
                Thread.sleep( 2000 );

                if( i == 20 ) {
                    cs.complete();
                }
            } catch( InterruptedException e ) {
            }
        }
    }    // main
}    // WorkflowServer



/*
 *  @(#)WorkflowServer.java   02.07.07
 * 
 *  Fin del fichero WorkflowServer.java
 *  
 *  Versión 2.2
 *
 */
