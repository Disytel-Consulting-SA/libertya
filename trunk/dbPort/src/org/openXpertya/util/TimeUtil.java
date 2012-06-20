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



package org.openXpertya.util;

import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TimeUtil {

    /**
     * Descripción de Método
     *
     *
     * @param time
     *
     * @return
     */

    static public Timestamp getDay( long time ) {
        if( time == 0 ) {
            time = System.currentTimeMillis();
        }

        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        cal.setTimeInMillis( time );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        return new Timestamp( cal.getTimeInMillis());
    }    // getDay

    /**
     * Descripción de Método
     *
     *
     * @param dayTime
     *
     * @return
     */

    static public Timestamp getDay( Timestamp dayTime ) {
        if( dayTime == null ) {
            return getDay( System.currentTimeMillis());
        }

        return getDay( dayTime.getTime());
    }    // getDay

    /**
     * Descripción de Método
     *
     *
     * @param year
     * @param month
     * @param day
     *
     * @return
     */

    static public Timestamp getDay( int year,int month,int day ) {
        if( year < 50 ) {
            year += 2000;
        } else if( year < 100 ) {
            year += 1900;
        }

        if( (month < 1) || (month > 12) ) {
            throw new IllegalArgumentException( "Invalid Month: " + month );
        }

        if( (day < 1) || (day > 31) ) {
            throw new IllegalArgumentException( "Invalid Day: " + month );
        }

        GregorianCalendar cal = new GregorianCalendar( year,month - 1,day );

        return new Timestamp( cal.getTimeInMillis());
    }    // getDay

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    static public Calendar getToday() {
        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        // cal.setTimeInMillis(System.currentTimeMillis());

        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        return cal;
    }    // getToday

    /**
     * Descripción de Método
     *
     *
     * @param day
     *
     * @return
     */

    static public Timestamp getNextDay( Timestamp day ) {
        if( day == null ) {
            day = new Timestamp( System.currentTimeMillis());
        }

        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        cal.setTimeInMillis( day.getTime());
        cal.add( Calendar.DAY_OF_YEAR,+1 );    // next
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        return new Timestamp( cal.getTimeInMillis());
    }    // getNextDay

    /**
     * Descripción de Método
     *
     *
     * @param day
     *
     * @return
     */

    static public Timestamp getMonthLastDay( Timestamp day ) {
        if( day == null ) {
            day = new Timestamp( System.currentTimeMillis());
        }

        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        cal.setTimeInMillis( day.getTime());
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        //

        cal.add( Calendar.MONTH,1 );           // next
        cal.set( Calendar.DAY_OF_MONTH,1 );    // first
        cal.add( Calendar.DAY_OF_YEAR,-1 );    // previous

        return new Timestamp( cal.getTimeInMillis());
    }    // getNextDay

    /**
     * Descripción de Método
     *
     *
     * @param day
     * @param time
     *
     * @return
     */

    static public Timestamp getDayTime( Timestamp day,Timestamp time ) {
        GregorianCalendar cal_1 = new GregorianCalendar();

        cal_1.setTimeInMillis( day.getTime());

        GregorianCalendar cal_2 = new GregorianCalendar();

        cal_2.setTimeInMillis( time.getTime());

        //

        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        cal.set( cal_1.get( Calendar.YEAR ),cal_1.get( Calendar.MONTH ),cal_1.get( Calendar.DAY_OF_MONTH ),cal_2.get( Calendar.HOUR_OF_DAY ),cal_2.get( Calendar.MINUTE ),cal_2.get( Calendar.SECOND ));
        cal.set( Calendar.MILLISECOND,0 );

        Timestamp retValue = new Timestamp( cal.getTimeInMillis());

        // log.fine( "TimeUtil.getDayTime", "Day=" + day + ", Time=" + time + " => " + retValue);

        return retValue;
    }    // getDayTime

    /**
     * Descripción de Método
     *
     *
     * @param start_1
     * @param end_1
     * @param start_2
     * @param end_2
     *
     * @return
     */

    static public boolean inRange( Timestamp start_1,Timestamp end_1,Timestamp start_2,Timestamp end_2 ) {

        // validity check

        if( end_1.before( start_1 )) {
            throw new UnsupportedOperationException( "TimeUtil.inRange End_1=" + end_1 + " before Start_1=" + start_1 );
        }

        if( end_2.before( start_2 )) {
            throw new UnsupportedOperationException( "TimeUtil.inRange End_2=" + end_2 + " before Start_2=" + start_2 );
        }

        // case a

        if( !end_2.after( start_1 ))    // end not including
        {

            // log.fine( "TimeUtil.InRange - No", start_1 + "->" + end_1 + " <??> " + start_2 + "->" + end_2);

            return false;
        }

        // case c

        if( !start_2.before( end_1 ))    // end not including
        {

            // log.fine( "TimeUtil.InRange - No", start_1 + "->" + end_1 + " <??> " + start_2 + "->" + end_2);

            return false;
        }

        // log.fine( "TimeUtil.InRange - Yes", start_1 + "->" + end_1 + " <??> " + start_2 + "->" + end_2);

        return true;
    }    // inRange

    /**
     * Descripción de Método
     *
     *
     * @param start
     * @param end
     * @param OnMonday
     * @param OnTuesday
     * @param OnWednesday
     * @param OnThursday
     * @param OnFriday
     * @param OnSaturday
     * @param OnSunday
     *
     * @return
     */

    static public boolean inRange( Timestamp start,Timestamp end,boolean OnMonday,boolean OnTuesday,boolean OnWednesday,boolean OnThursday,boolean OnFriday,boolean OnSaturday,boolean OnSunday ) {

        // are there restrictions?

        if( OnSaturday && OnSunday && OnMonday && OnTuesday && OnWednesday && OnThursday && OnFriday ) {
            return false;
        }

        GregorianCalendar calStart = new GregorianCalendar();

        calStart.setTimeInMillis( start.getTime());

        int dayStart = calStart.get( Calendar.DAY_OF_WEEK );

        //

        GregorianCalendar calEnd = new GregorianCalendar();

        calEnd.setTimeInMillis( end.getTime());
        calEnd.add( Calendar.DAY_OF_YEAR,-1 );    // not including

        int dayEnd = calEnd.get( Calendar.DAY_OF_WEEK );

        // On same day

        if( (calStart.get( Calendar.YEAR ) == calEnd.get( Calendar.YEAR )) && (calStart.get( Calendar.MONTH ) == calEnd.get( Calendar.MONTH )) && (calStart.get( Calendar.DAY_OF_MONTH ) == calEnd.get( Calendar.DAY_OF_YEAR ))) {
            if(( !OnSaturday && (dayStart == Calendar.SATURDAY) ) || ( !OnSunday && (dayStart == Calendar.SUNDAY) ) || ( !OnMonday && (dayStart == Calendar.MONDAY) ) || ( !OnTuesday && (dayStart == Calendar.TUESDAY) ) || ( !OnWednesday && (dayStart == Calendar.WEDNESDAY) ) || ( !OnThursday && (dayStart == Calendar.THURSDAY) ) || ( !OnFriday && (dayStart == Calendar.FRIDAY) ) ) {

                // log.fine( "TimeUtil.InRange - SameDay - Yes", start + "->" + end + " - "
                // + OnMonday+"-"+OnTuesday+"-"+OnWednesday+"-"+OnThursday+"-"+OnFriday+"="+OnSaturday+"-"+OnSunday);

                return true;
            }

            // log.fine( "TimeUtil.InRange - SameDay - No", start + "->" + end + " - "
            // + OnMonday+"-"+OnTuesday+"-"+OnWednesday+"-"+OnThursday+"-"+OnFriday+"="+OnSaturday+"-"+OnSunday);

            return false;
        }

        //
        // log.fine( "TimeUtil.inRange - WeekDay Start=" + dayStart + ", Incl.End=" + dayEnd);

        // Calendar.SUNDAY=1 ... SATURDAY=7

        BitSet days = new BitSet( 8 );

        // Set covered days in BitArray

        if( dayEnd <= dayStart ) {
            dayEnd += 7;
        }

        for( int i = dayStart;i < dayEnd;i++ ) {
            int index = i;

            if( index > 7 ) {
                index -= 7;
            }

            days.set( index );

            // System.out.println("Set index=" + index + " i=" + i);

        }

        // for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++)
        // System.out.println("Result i=" + i + " - " + days.get(i));

        // Compare days to availability

        if(( !OnSaturday && days.get( Calendar.SATURDAY )) || ( !OnSunday && days.get( Calendar.SUNDAY )) || ( !OnMonday && days.get( Calendar.MONDAY )) || ( !OnTuesday && days.get( Calendar.TUESDAY )) || ( !OnWednesday && days.get( Calendar.WEDNESDAY )) || ( !OnThursday && days.get( Calendar.THURSDAY )) || ( !OnFriday && days.get( Calendar.FRIDAY ))) {

            // log.fine( "MAssignment.InRange - Yes",  start + "->" + end + " - "
            // + OnMonday+"-"+OnTuesday+"-"+OnWednesday+"-"+OnThursday+"-"+OnFriday+"="+OnSaturday+"-"+OnSunday);

            return true;
        }

        // log.fine( "MAssignment.InRange - No", start + "->" + end + " - "
        // + OnMonday+"-"+OnTuesday+"-"+OnWednesday+"-"+OnThursday+"-"+OnFriday+"="+OnSaturday+"-"+OnSunday);

        return false;
    }    // isRange

    /**
     * Descripción de Método
     *
     *
     * @param one
     * @param two
     *
     * @return
     */

    static public boolean isSameDay( Timestamp one,Timestamp two ) {
        GregorianCalendar calOne = new GregorianCalendar();

        calOne.setTimeInMillis( one.getTime());

        GregorianCalendar calTwo = new GregorianCalendar();

        calTwo.setTimeInMillis( two.getTime());

        if( (calOne.get( Calendar.YEAR ) == calTwo.get( Calendar.YEAR )) && (calOne.get( Calendar.MONTH ) == calTwo.get( Calendar.MONTH )) && (calOne.get( Calendar.DAY_OF_MONTH ) == calTwo.get( Calendar.DAY_OF_YEAR ))) {
            return true;
        }

        return false;
    }    // isSameDay

    /**
     * Descripción de Método
     *
     *
     * @param start
     * @param end
     *
     * @return
     */

    static public boolean isAllDay( Timestamp start,Timestamp end ) {
        GregorianCalendar calStart = new GregorianCalendar();

        calStart.setTimeInMillis( start.getTime());

        GregorianCalendar calEnd = new GregorianCalendar();

        calEnd.setTimeInMillis( end.getTime());

        if( (calStart.get( Calendar.HOUR_OF_DAY ) == calEnd.get( Calendar.HOUR_OF_DAY )) && (calStart.get( Calendar.MINUTE ) == calEnd.get( Calendar.MINUTE )) && (calStart.get( Calendar.SECOND ) == calEnd.get( Calendar.SECOND )) && (calStart.get( Calendar.MILLISECOND ) == calEnd.get( Calendar.MILLISECOND )) && (calStart.get( Calendar.HOUR_OF_DAY ) == 0) && (calStart.get( Calendar.MINUTE ) == 0) && (calStart.get( Calendar.SECOND ) == 0) && (calStart.get( Calendar.MILLISECOND ) == 0) && start.before( end )) {
            return true;
        }

        //

        return false;
    }    // isAllDay

    /**
     * Descripción de Método
     *
     *
     * @param start
     * @param end
     *
     * @return
     */

    static public int getDaysBetween( Timestamp start,Timestamp end ) {
        boolean negative = false;

        if( end.before( start )) {
            negative = true;

            Timestamp temp = start;

            start = end;
            end   = temp;
        }

        //

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( start );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        GregorianCalendar calEnd = new GregorianCalendar();

        calEnd.setTime( end );
        calEnd.set( Calendar.HOUR_OF_DAY,0 );
        calEnd.set( Calendar.MINUTE,0 );
        calEnd.set( Calendar.SECOND,0 );
        calEnd.set( Calendar.MILLISECOND,0 );

        // System.out.println("Start=" + start + ", End=" + end + ", dayStart=" + cal.get(Calendar.DAY_OF_YEAR) + ", dayEnd=" + calEnd.get(Calendar.DAY_OF_YEAR));

        // in same year

        if( cal.get( Calendar.YEAR ) == calEnd.get( Calendar.YEAR )) {
            if( negative ) {
                return( calEnd.get( Calendar.DAY_OF_YEAR ) - cal.get( Calendar.DAY_OF_YEAR )) * -1;
            }

            return calEnd.get( Calendar.DAY_OF_YEAR ) - cal.get( Calendar.DAY_OF_YEAR );
        }

        // not very efficient, but correct

        int counter = 0;

        while( calEnd.after( cal )) {
            cal.add( Calendar.DAY_OF_YEAR,1 );
            counter++;
        }

        if( negative ) {
            return counter * -1;
        }

        return counter;
    }    // getDatesBetrween

    /**
     * Descripción de Método
     *
     *
     * @param day
     * @param offset
     *
     * @return
     */

    static public Timestamp addDays( Timestamp day,int offset ) {
        if( day == null ) {
            day = new Timestamp( System.currentTimeMillis());
        }

        //

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( day );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        if( offset == 0 ) {
            return new Timestamp( cal.getTimeInMillis());
        }

        cal.add( Calendar.DAY_OF_YEAR,offset );    // may have a problem with negative (before 1/1)

        return new Timestamp( cal.getTimeInMillis());
    }    // addDays

    /**
     * Descripción de Método
     *
     *
     * @param dateTime
     * @param offset
     *
     * @return
     */

    static public Timestamp addMinutess( Timestamp dateTime,int offset ) {
        if( dateTime == null ) {
            dateTime = new Timestamp( System.currentTimeMillis());
        }

        if( offset == 0 ) {
            return dateTime;
        }

        //

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( dateTime );
        cal.add( Calendar.MINUTE,offset );    // may have a problem with negative

        return new Timestamp( cal.getTimeInMillis());
    }    // addMinutes

    /**
     * Descripción de Método
     *
     *
     * @param start
     * @param end
     *
     * @return
     */

    public static String formatElapsed( Timestamp start,Timestamp end ) {
        long startTime = 0;

        if( start == null ) {
            startTime = System.currentTimeMillis();
        } else {
            startTime = start.getTime();
        }

        //

        long endTime = 0;

        if( end == null ) {
            endTime = System.currentTimeMillis();
        } else {
            endTime = end.getTime();
        }

        return formatElapsed( endTime - startTime );
    }    // formatElapsed

    /**
     * Descripción de Método
     *
     *
     * @param start
     *
     * @return
     */

    public static String formatElapsed( Timestamp start ) {
        if( start == null ) {
            return "NoStartTime";
        }

        long startTime = start.getTime();
        long endTime   = System.currentTimeMillis();

        return formatElapsed( endTime - startTime );
    }    // formatElapsed

    /**
     * Descripción de Método
     *
     *
     * @param elapsed
     *
     * @return
     */

    public static String formatElapsed( long elapsed ) {
        long miliSeconds = elapsed % 1000;

        elapsed = elapsed / 1000;

        long seconds = elapsed % 60;

        elapsed = elapsed / 60;

        long minutes = elapsed % 60;

        elapsed = elapsed / 60;

        long hours = elapsed % 24;
        long days  = elapsed / 24;

        //

        StringBuffer sb = new StringBuffer();

        if( days != 0 ) {
            sb.append( days ).append( "'" );
        }

        if( hours != 0 ) {
            sb.append( get2digits( hours )).append( ":" );
        }

        if( minutes != 0 ) {
            sb.append( get2digits( minutes )).append( ":" );
        }

        sb.append( get2digits( seconds )).append( "." ).append( miliSeconds );

        return sb.toString();
    }    // format Elapsed

    /**
     * Descripción de Método
     *
     *
     * @param no
     *
     * @return
     */

    private static String get2digits( long no ) {
        String s = String.valueOf( no );

        if( s.length() > 1 ) {
            return s;
        }

        return "0" + s;
    }    // get2digits

    /**
     * Descripción de Método
     *
     *
     * @param validFrom
     * @param validTo
     *
     * @return
     */

    public static boolean isValid( Timestamp validFrom,Timestamp validTo ) {
        return isValid( validFrom,validTo,new Timestamp( System.currentTimeMillis()));
    }    // isValid

    /**
     * Descripción de Método
     *
     *
     * @param validFrom
     * @param validTo
     * @param testDate
     *
     * @return
     */

    public static boolean isValid( Timestamp validFrom,Timestamp validTo,Timestamp testDate ) {
        if( testDate == null ) {
            return true;
        }

        if( (validFrom == null) && (validTo == null) ) {
            return true;
        }

        // (validFrom)     ok

        if( (validFrom != null) && validFrom.after( testDate )) {
            return false;
        }

        // ok      (validTo)

        if( (validTo != null) && validTo.before( testDate )) {
            return false;
        }

        return true;
    }    // isValid

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        Timestamp t1 = getDay( 01,01,01 );
        Timestamp t2 = getDay( 02,02,02 );
        Timestamp t3 = getDay( 03,03,03 );

        System.out.println( t1 + " - " + t3 );
        System.out.println( t2 + " - " + isValid( t1,t3,t2 ));
    }    // main

	/**
	 * Calcula la cantidad de días de diferencia entre la fecha start y la fecha
	 * end.
	 * 
	 * @param start
	 *            fecha de inicio
	 * @param end
	 *            fecha de fin
	 * @return cantidad de días de diferencia
	 */
    public static int getDiffDays(Timestamp start, Timestamp end){
    	Calendar calStart = Calendar.getInstance();
    	calStart.setTime( start );
    	calStart.set( Calendar.HOUR_OF_DAY,0 );
    	calStart.set( Calendar.MINUTE,0 );
    	calStart.set( Calendar.SECOND,0 );
    	calStart.set( Calendar.MILLISECOND,0 );
    	calStart.getTime();
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime( end );
        calEnd.set( Calendar.HOUR_OF_DAY,0 );
        calEnd.set( Calendar.MINUTE,0 );
        calEnd.set( Calendar.SECOND,0 );
        calEnd.set( Calendar.MILLISECOND,0 );
        calEnd.getTime();
    	long dif = calEnd.getTimeInMillis() - calStart.getTimeInMillis();
    	Long days = dif / (1000L * 3600 * 24);
    	int dias = days.intValue();
    	return dias;	
    }
}    // TimeUtil



/*
 *  @(#)TimeUtil.java   25.03.06
 * 
 *  Fin del fichero TimeUtil.java
 *  
 *  Versión 2.2
 *
 */
