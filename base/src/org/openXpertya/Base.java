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



package org.openXpertya;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MUOMConversion;
import org.openXpertya.model.MWindow;
import org.openXpertya.model.MWindowVO;
import org.openXpertya.util.Env;
import org.openXpertya.util.Login;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class Base {

    /**
     * Descripción de Método
     *
     */

    public static void test() {
        System.out.println( "** Before Init **" );    // $NON-NLS-1$
        getMemoryUsed();

        Properties ctx = Login.initTest( false );

        // Log.printProperties(System.getProperties(), "System", false);
        //

        System.gc();    // cleanup Init

        //

        System.out.println( "** Before Creation **" );

        long start = getMemoryUsed();

        // *******************************************************************

        // Table=100, Shipper=142, Window=102, Reference=101

        int       AD_Window_ID = 102;
        long      startTime    = System.currentTimeMillis();
        MWindowVO vo           = MWindowVO.create( Env.getCtx(),1,AD_Window_ID );
        MWindow   w            = new MWindow( vo );
        long      endDef       = System.currentTimeMillis();

        System.out.println( "Load Definition Time in ms = " + String.valueOf( endDef - startTime ));

        if( 1 == 2 )    // optional step
        {
            w.loadCompete();

            long endDefComplete = System.currentTimeMillis();

            System.out.println( "Load Definition Complete Time in ms = " + String.valueOf( endDefComplete - startTime ));
        }

        w.query();

        long endData = System.currentTimeMillis();

        System.out.println( "Load Data Time in ms = " + String.valueOf( endData - startTime ));
        w.loadCompete();

        long endDataComplete = System.currentTimeMillis();

        System.out.println( "Load Data Complete Time in ms = " + String.valueOf( endDataComplete - startTime ));
        w.getTab( 0 ).navigate( 0 );

        // *******************************************************************
//              sleep();

        System.out.println( "** Before Dispose **" );
        getMemoryUsed();
        w.dispose();

//              sleep();
        //

        System.out.println( "** Before GC **" );
        getMemoryUsed();
        w = null;
        System.gc();
        System.out.println( "** After GC **" );
        getMemoryUsed();
        System.gc();
        System.out.println( "** Final **" );

        long complete = System.currentTimeMillis();

        System.out.println( "Complete Time in ms = " + String.valueOf( complete - startTime ));

        long end = getMemoryUsed();

        System.out.println( "Memory increase in kB = End-Start=" + String.valueOf(( end - start ) / 1024 ));
        listThreads();

        //

        System.out.println( "API Test" );
        System.out.println( "64.72=" + MConversionRate.convert( ctx,new BigDecimal( 100.0 ),116,100,0,0 ));
        System.out.println( "0.647169=" + MConversionRate.getRate( 116,100,null,0,0,0 ));
        System.out.println( "12.5=" + MUOMConversion.convert( 101,102,new BigDecimal( 100.0 ),true ));
    }    // Base

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private static long getMemoryUsed() {
        long free  = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();
        long used  = total - free;

        //

        System.out.println( "Memory used in kB = Total(" + String.valueOf( total / 1024 ) + ")-Free(" + String.valueOf( free / 1024 ) + ") = " + String.valueOf( used / 1024 ));
        System.out.println( "Active Threads=" + Thread.activeCount());

        return used;
    }    // getMemoryUsed

    /**
     * Descripción de Método
     *
     */

    private static void sleep() {
        System.out.println( ".. sleeping-ini .. -> " + Thread.activeCount());
        Thread.yield();

        try {
            Thread.sleep( 1000 );
        } catch( InterruptedException ie ) {
        }

        System.out.println( ".. sleeping-end .. -> " + Thread.activeCount());
    }    // sleep

    /**
     * Descripción de Método
     *
     */

    private static void listThreads() {
        Thread[] list = new Thread[ Thread.activeCount()];

        // int no = Thread.currentThread().enumerate(list);

        for( int i = 0;i < list.length;i++ ) {
            if( list[ i ] != null ) {
                System.out.println( "Thread " + i + " - " + list[ i ].toString());
            }
        }
    }    // listThreads

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        Base.test();
        Env.exitEnv( 0 );
    }    // main
}    // Base



/*
 *  @(#)Base.java   02.07.07
 * 
 *  Fin del fichero Base.java
 *  
 *  Versión 2.2
 *
 */
