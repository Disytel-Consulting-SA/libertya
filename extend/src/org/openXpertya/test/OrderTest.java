/*
 * @(#)OrderTest.java   11.jun 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.test;

import java.math.*;

import java.util.*;
import java.util.logging.*;

import org.openXpertya.*;
import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 11.12.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class OrderTest implements Runnable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param no
     * @param numberOrders
     * @param avgLines
     */

    public OrderTest( int no,int numberOrders,int avgLines ) {
        super();
        m_no           = no;
        m_numberOrders = numberOrders;
        m_maxLines     = avgLines * 2;
    }    // OrderTest

    /** Descripción de Campos */

    int m_no = 0;

    /** Descripción de Campos */

    int m_numberOrders = 0;

    /** Descripción de Campos */

    int m_maxLines = 20;

    /** Descripción de Campos */

    int m_errors = 0;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( OrderTest.class );

    /**
     * Descripción de Método
     *
     */

    public void run() {
        long      time  = System.currentTimeMillis();
        int       count = 0;
        MBPartner bp    = new MBPartner( Env.getCtx(),117,null );

        bp.setSOCreditStatus( MBPartner.SOCREDITSTATUS_NoCreditCheck );
        bp.save();

        //

        for( int i = 0;i < m_numberOrders;i++ ) {
            Trx trx = Trx.get( Trx.createTrxName( "Test" + m_no + "_" + i ),true );

            trx.start();

            //

            MOrder order = new MOrder( Env.getCtx(),0,trx.getTrxName());

            order.setDescription( "#" + m_no + "_" + i );
            order.setC_DocTypeTarget_ID( 135 );    // POS
            order.setC_BPartner_ID( 117 );         // C&W
            order.setSalesRep_ID( 101 );           // GardenAdmin
            order.setDeliveryRule( MOrder.DELIVERYRULE_Force );

            if( !order.save()) {
                log.warning( "#" + m_no + "_" + i + ": No guardada(1)" );
                m_errors++;

                continue;
            }

            Random r           = new Random();
            int    linesNumber = r.nextInt( m_maxLines ) + 1;

            for( int j = 0;j < linesNumber;j++ ) {
                MOrderLine line = new MOrderLine( order );

                line.setM_Product_ID( 123 );       // Oak Tree
                line.setQty( new BigDecimal( 5 ));

                if( !line.save()) {
                    log.warning( "#" + m_no + "_" + i + ": Linea no guardada" );
                    m_errors++;
                }
            }

            // Process

            order.setDocAction( DocAction.ACTION_Complete );

            if( !order.processIt( DocAction.ACTION_Complete )) {
                log.warning( "#" + m_no + "_" + i + ": No procesado" );
                m_errors++;
                trx.rollback();
                trx.close();

                continue;
            }

            if( !order.save()) {
                log.warning( "#" + m_no + "_" + i + ": No guardado(2)" );
                m_errors++;
            } else {
                count++;
            }

            trx.commit();
            trx.close();

            //

            log.info( order.toString());
        }

        time = System.currentTimeMillis() - time;
        log.warning( "#" + m_no + ", Errors=" + m_errors + ", Count=" + count + " " + (( float )count * 100 / m_numberOrders ) + "% - " + time + "ms - ea " + (( float )time / count ) + "ms" );
    }    // run

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        OpenXpertya.startup( true );
        CLogMgt.setLoggerLevel( Level.INFO,null );
        CLogMgt.setLevel( Level.INFO );

        //

        Ini.setProperty( Ini.P_UID,"SuperUser" );
        Ini.setProperty( Ini.P_PWD,"System" );
        Ini.setProperty( Ini.P_ROLE,"GardenWorld Admin" );
        Ini.setProperty( Ini.P_CLIENT,"GardenWorld" );
        Ini.setProperty( Ini.P_ORG,"HQ" );
        Ini.setProperty( Ini.P_WAREHOUSE,"HQ Warehouse" );
        Ini.setProperty( Ini.P_LANGUAGE,"English" );

        Login login = new Login( Env.getCtx());

        if( !login.batchLogin( null )) {
            System.exit( 1 );
        }

        //

        CLogMgt.setLoggerLevel( Level.WARNING,null );
        CLogMgt.setLevel( Level.WARNING );

        int      NO_TESTS  = 2;
        int      NO_ORDERS = 200;
        int      NO_LINES  = 20;
        long     time      = System.currentTimeMillis();
        Thread[] tests     = new Thread[ NO_TESTS ];

        for( int i = 0;i < tests.length;i++ ) {
            tests[ i ] = new Thread( new OrderTest( i,NO_ORDERS,NO_LINES ));
            tests[ i ].start();
        }

        // Wait

        for( int i = 0;i < tests.length;i++ ) {
            try {
                tests[ i ].join();
            } catch( InterruptedException e ) {
            }
        }

        time = System.currentTimeMillis() - time;
        System.out.println( "Time (ms)=" + time );
    }    // main
}    // OrderTest



/*
 *  @(#)OrderTest.java   11.12.06
 * 
 *  Fin del fichero OrderTest.java
 *  
 *  Versión 2.2
 *
 */
