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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import org.openXpertya.util.CCache;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInvoiceSchedule extends X_C_InvoiceSchedule {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_InvoiceSchedule_ID
     * @param trxName
     *
     * @return
     */

    public static MInvoiceSchedule get( Properties ctx,int C_InvoiceSchedule_ID,String trxName ) {
        Integer          key      = new Integer( C_InvoiceSchedule_ID );
        MInvoiceSchedule retValue = ( MInvoiceSchedule )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MInvoiceSchedule( ctx,C_InvoiceSchedule_ID,trxName );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_InvoiceSchedule",5 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_InvoiceSchedule_ID
     * @param trxName
     */

    public MInvoiceSchedule( Properties ctx,int C_InvoiceSchedule_ID,String trxName ) {
        super( ctx,C_InvoiceSchedule_ID,trxName );
    }    // MInvoiceSchedule

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInvoiceSchedule( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInvoiceSchedule

    /**
     * Descripción de Método
     *
     *
     * @param xDate
     * @param orderAmt
     *
     * @return
     */

    public boolean canInvoice( Timestamp xDate,BigDecimal orderAmt ) {

        // Amount

        if( isAmount() && (getAmt() != null) && (orderAmt != null) && (orderAmt.compareTo( getAmt()) >= 0) ) {
            return true;
        }

        // Daily

        if( INVOICEFREQUENCY_Daily.equals( getInvoiceFrequency())) {
            return true;
        }

        // Remove time

        xDate = TimeUtil.getDay( xDate );

        Calendar today = TimeUtil.getToday();

        // Weekly

        if( INVOICEFREQUENCY_Weekly.equals( getInvoiceFrequency())) {
            Calendar cutoff = TimeUtil.getToday();

            cutoff.set( Calendar.DAY_OF_WEEK,getCalendarDay( getInvoiceWeekDayCutoff()));

            if( cutoff.after( today )) {
                cutoff.add( Calendar.DAY_OF_YEAR,-7 );
            }

            Timestamp cutoffDate = new Timestamp( cutoff.getTimeInMillis());

            log.fine( "canInvoice - Date=" + xDate + " > Cutoff=" + cutoffDate + " - " + xDate.after( cutoffDate ));

            if( xDate.after( cutoffDate )) {
                return false;
            }

            //

            Calendar invoice = TimeUtil.getToday();

            invoice.set( Calendar.DAY_OF_WEEK,getCalendarDay( getInvoiceWeekDay()));

            if( invoice.after( today )) {
                invoice.add( Calendar.DAY_OF_YEAR,-7 );
            }

            Timestamp invoiceDate = new Timestamp( invoice.getTimeInMillis());

            log.fine( "canInvoice - Date=" + xDate + " > Invoice=" + invoiceDate + " - " + xDate.after( invoiceDate ));

            if( xDate.after( invoiceDate )) {
                return false;
            }

            return true;
        }

        // Monthly

        if( INVOICEFREQUENCY_Monthly.equals( getInvoiceFrequency()) || INVOICEFREQUENCY_TwiceMonthly.equals( getInvoiceFrequency())) {
            if( getInvoiceDayCutoff() > 0 ) {
                Calendar cutoff = TimeUtil.getToday();

                cutoff.set( Calendar.DAY_OF_MONTH,getInvoiceDayCutoff());

                if( cutoff.after( today )) {
                    cutoff.add( Calendar.MONTH,-1 );
                }

                Timestamp cutoffDate = new Timestamp( cutoff.getTimeInMillis());

                log.fine( "canInvoice - Date=" + xDate + " > Cutoff=" + cutoffDate + " - " + xDate.after( cutoffDate ));

                if( xDate.after( cutoffDate )) {
                    return false;
                }
            }

            Calendar invoice = TimeUtil.getToday();

            invoice.set( Calendar.DAY_OF_MONTH,getInvoiceDay());

            if( invoice.after( today )) {
                invoice.add( Calendar.MONTH,-1 );
            }

            Timestamp invoiceDate = new Timestamp( invoice.getTimeInMillis());

            log.fine( "canInvoice - Date=" + xDate + " > Invoice=" + invoiceDate + " - " + xDate.after( invoiceDate ));

            if( xDate.after( invoiceDate )) {
                return false;
            }

            return true;
        }

        // Bi-Monthly (+15)

        if( INVOICEFREQUENCY_TwiceMonthly.equals( getInvoiceFrequency())) {
            if( getInvoiceDayCutoff() > 0 ) {
                Calendar cutoff = TimeUtil.getToday();

                cutoff.set( Calendar.DAY_OF_MONTH,getInvoiceDayCutoff() + 15 );

                if( cutoff.after( today )) {
                    cutoff.add( Calendar.MONTH,-1 );
                }

                Timestamp cutoffDate = new Timestamp( cutoff.getTimeInMillis());

                if( xDate.after( cutoffDate )) {
                    return false;
                }
            }

            Calendar invoice = TimeUtil.getToday();

            invoice.set( Calendar.DAY_OF_MONTH,getInvoiceDay() + 15 );

            if( invoice.after( today )) {
                invoice.add( Calendar.MONTH,-1 );
            }

            Timestamp invoiceDate = new Timestamp( invoice.getTimeInMillis());

            if( xDate.after( invoiceDate )) {
                return false;
            }

            return true;
        }

        return false;
    }    // canInvoice

    /**
     * Descripción de Método
     *
     *
     * @param day
     *
     * @return
     */

    private int getCalendarDay( String day ) {
        if( INVOICEWEEKDAY_Friday.equals( day )) {
            return Calendar.FRIDAY;
        }

        if( INVOICEWEEKDAY_Saturday.equals( day )) {
            return Calendar.SATURDAY;
        }

        if( INVOICEWEEKDAY_Sunday.equals( day )) {
            return Calendar.SUNDAY;
        }

        if( INVOICEWEEKDAY_Monday.equals( day )) {
            return Calendar.MONDAY;
        }

        if( INVOICEWEEKDAY_Tuesday.equals( day )) {
            return Calendar.TUESDAY;
        }

        if( INVOICEWEEKDAY_Wednesday.equals( day )) {
            return Calendar.WEDNESDAY;
        }

        // if (INVOICEWEEKDAY_Thursday.equals(day))

        return Calendar.THURSDAY;
    }    // getCalendarDay
}    // MInvoiceSchedule



/*
 *  @(#)MInvoiceSchedule.java   02.07.07
 * 
 *  Fin del fichero MInvoiceSchedule.java
 *  
 *  Versión 2.2
 *
 */
