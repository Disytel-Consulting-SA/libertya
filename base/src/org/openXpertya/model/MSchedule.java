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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MSchedule {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public MSchedule( Properties ctx ) {
        m_ctx = ctx;
    }    // MSchedule

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private int m_S_Resource_ID;

    /** Descripción de Campos */

    private boolean m_isAvailable = true;

    /** Descripción de Campos */

    private boolean m_isSingleAssignment = true;

    /** Descripción de Campos */

    private int m_S_ResourceType_ID = 0;

    /** Descripción de Campos */

    private int m_C_UOM_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_startDate = null;

    /** Descripción de Campos */

    private Timestamp m_endDate = null;

    /** Descripción de Campos */

    private String m_typeName = null;

    /** Descripción de Campos */

    private Timestamp m_slotStartTime = null;

    /** Descripción de Campos */

    private Timestamp m_slotEndTime = null;

    /** Descripción de Campos */

    private MAssignmentSlot[] m_timeSlots = null;

    /** Descripción de Campos */

    public static final Timestamp EARLIEST = new Timestamp( new GregorianCalendar( 1970,1,1 ).getTimeInMillis());

    /** Descripción de Campos */

    public static final Timestamp LATEST = new Timestamp( new GregorianCalendar( 2070,12,31 ).getTimeInMillis());

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MSchedule.class );

    /**
     * Descripción de Método
     *
     *
     * @param S_Resource_ID
     * @param start_Date
     * @param end_Date
     * @param qty
     * @param getAll
     * @param trxName
     *
     * @return
     */

    public MAssignmentSlot[] getAssignmentSlots( int S_Resource_ID,Timestamp start_Date,Timestamp end_Date,BigDecimal qty,boolean getAll,String trxName ) {
        log.config( start_Date.toString());

        if( m_S_Resource_ID != S_Resource_ID ) {
            getBaseInfo( S_Resource_ID );
        }

        //

        ArrayList       list = new ArrayList();
        MAssignmentSlot ma   = null;

        if( !m_isAvailable ) {
            ma = new MAssignmentSlot( EARLIEST,LATEST,Msg.getMsg( m_ctx,"ResourceNotAvailable" ),"",MAssignmentSlot.STATUS_NotAvailable );

            if( !getAll ) {
                return new MAssignmentSlot[]{ ma };
            }

            list.add( ma );
        }

        m_startDate = start_Date;
        m_endDate   = end_Date;

        if( m_endDate == null ) {
            m_endDate = MUOMConversion.getEndDate( m_ctx,m_startDate,m_C_UOM_ID,qty );
        }

        log.fine( "- EndDate=" + m_endDate );

        // Resource Unavailability -------------------------------------------
        // log.fine( "- Unavailability -");

        String sql = "SELECT Description, DateFrom, DateTo " + "FROM S_ResourceUnavailable " + "WHERE S_Resource_ID=?"    // #1
                     + " AND DateTo >= ?"      // #2      start
                     + " AND DateFrom <= ?"    // #3      end
                     + " AND IsActive='Y'";

        try {

            // log.fine( sql, "ID=" + S_Resource_ID + ", Start=" + m_startDate + ", End=" + m_endDate);

            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_S_Resource_ID );
            pstmt.setTimestamp( 2,m_startDate );
            pstmt.setTimestamp( 3,m_endDate );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                ma = new MAssignmentSlot( TimeUtil.getDay( rs.getTimestamp( 2 )),TimeUtil.getNextDay( rs.getTimestamp( 3 )),    // user entered date need to convert to not including end time
                                          Msg.getMsg( m_ctx,"ResourceUnAvailable" ),rs.getString( 1 ),MAssignmentSlot.STATUS_UnAvailable );

                // log.fine( "- Unavailable", ma);

                if( getAll ) {
                    createDaySlot( list,ma );
                } else {
                    list.add( ma );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MSchedule.getAssignmentSlots-2",e );
            ma = new MAssignmentSlot( EARLIEST,LATEST,Msg.getMsg( m_ctx,"ResourceUnAvailable" ),e.toString(),MAssignmentSlot.STATUS_UnAvailable );
        }

        if( (ma != null) &&!getAll ) {
            return new MAssignmentSlot[]{ ma };
        }

        // NonBusinessDay ----------------------------------------------------
        // log.fine( "- NonBusinessDay -");
        // "WHERE TRUNC(Date1) BETWEEN TRUNC(?) AND TRUNC(?)"   causes
        // ORA-00932: inconsistent datatypes: expected NUMBER got TIMESTAMP

        sql = MRole.getDefault( m_ctx,false ).addAccessSQL( "SELECT Name, Date1 FROM C_NonBusinessDay " + "WHERE TRUNC(Date1) BETWEEN ? AND ?","C_NonBusinessDay",false,false );    // not qualified - RO

        try {
            Timestamp startDay = TimeUtil.getDay( m_startDate );
            Timestamp endDay   = TimeUtil.getDay( m_endDate );

            // log.fine( sql, "Start=" + startDay + ", End=" + endDay);

            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setTimestamp( 1,startDay );
            pstmt.setTimestamp( 2,endDay );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                ma = new MAssignmentSlot( TimeUtil.getDay( rs.getTimestamp( 2 )),TimeUtil.getNextDay( rs.getTimestamp( 2 )),    // user entered date need to convert to not including end time
                                          Msg.getMsg( m_ctx,"NonBusinessDay" ),rs.getString( 1 ),MAssignmentSlot.STATUS_NonBusinessDay );
                log.finer( "- NonBusinessDay " + ma );
                list.add( ma );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MSchedule.getAssignmentSlots-3",e );
            ma = new MAssignmentSlot( EARLIEST,LATEST,Msg.getMsg( m_ctx,"NonBusinessDay" ),e.toString(),MAssignmentSlot.STATUS_NonBusinessDay );
        }

        if( (ma != null) &&!getAll ) {
            return new MAssignmentSlot[]{ ma };
        }

        // ResourceType Available --------------------------------------------
        // log.fine( "- ResourceTypeAvailability -");

        sql = "SELECT Name, IsTimeSlot,TimeSlotStart,TimeSlotEnd, "    // 1..4
              + "IsDateSlot,OnMonday,OnTuesday,OnWednesday,"           // 5..8
              + "OnThursday,OnFriday,OnSaturday,OnSunday "             // 9..12
              + "FROM S_ResourceType " + "WHERE S_ResourceType_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_S_ResourceType_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_typeName = rs.getString( 1 );

                // TimeSlot

                if( "Y".equals( rs.getString( 2 ))) {
                    m_slotStartTime = TimeUtil.getDayTime( m_startDate,rs.getTimestamp( 3 ));
                    m_slotEndTime = TimeUtil.getDayTime( m_endDate,rs.getTimestamp( 4 ));

                    if( TimeUtil.inRange( m_startDate,m_endDate,m_slotStartTime,m_slotEndTime )) {
                        ma = new MAssignmentSlot( m_slotStartTime,m_slotEndTime,Msg.getMsg( m_ctx,"ResourceNotInSlotTime" ),m_typeName,MAssignmentSlot.STATUS_NotInSlotTime );

                        if( getAll ) {
                            createTimeSlot( list,rs.getTimestamp( 3 ),rs.getTimestamp( 4 ));
                        }
                    }
                }                                                                                                                 // TimeSlot

                // DaySlot

                if( "Y".equals( rs.getString( 5 ))) {
                    if( TimeUtil.inRange( m_startDate,m_endDate,"Y".equals( rs.getString( 6 )),"Y".equals( rs.getString( 7 )),    // Mo..Tu
                                          "Y".equals( rs.getString( 8 )),"Y".equals( rs.getString( 9 )),"Y".equals( rs.getString( 10 )),    // We..Fr
                                          "Y".equals( rs.getString( 11 )),"Y".equals( rs.getString( 12 )))) {
                        ma = new MAssignmentSlot( m_startDate,m_endDate,Msg.getMsg( m_ctx,"ResourceNotInSlotDay" ),m_typeName,MAssignmentSlot.STATUS_NotInSlotDay );

                        if( getAll ) {
                            createDaySlot( list,"Y".equals( rs.getString( 6 )),"Y".equals( rs.getString( 7 )),    // Mo..Tu
                                "Y".equals( rs.getString( 8 )),"Y".equals( rs.getString( 9 )),"Y".equals( rs.getString( 10 )),    // We..Fr
                                    "Y".equals( rs.getString( 11 )),"Y".equals( rs.getString( 12 )));
                        }
                    }
                }    // DaySlot
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MSchedule.getAssignmentSlots-4",e );
            ma = new MAssignmentSlot( EARLIEST,LATEST,Msg.getMsg( m_ctx,"ResourceNotInSlotDay" ),e.toString(),MAssignmentSlot.STATUS_NonBusinessDay );
        }

        if( (ma != null) &&!getAll ) {
            return new MAssignmentSlot[]{ ma };
        }

        // Assignments -------------------------------------------------------

        sql = "SELECT S_ResourceAssignment_ID " + "FROM S_ResourceAssignment " + "WHERE S_Resource_ID=?"    // #1
              + " AND AssignDateTo >= ?"      // #2      start
              + " AND AssignDateFrom <= ?"    // #3      end
              + " AND IsActive='Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_S_Resource_ID );
            pstmt.setTimestamp( 2,m_startDate );
            pstmt.setTimestamp( 3,m_endDate );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MResourceAssignment mAssignment = new MResourceAssignment( Env.getCtx(),rs.getInt( 1 ),trxName );

                ma = new MAssignmentSlot( mAssignment );

                if( !getAll ) {
                    break;
                }

                list.add( ma );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MSchedule.getAssignmentSlots-5",e );
            ma = new MAssignmentSlot( EARLIEST,LATEST,Msg.translate( m_ctx,"S_R" ),e.toString(),MAssignmentSlot.STATUS_NotConfirmed );
        }

        if( (ma != null) &&!getAll ) {
            return new MAssignmentSlot[]{ ma };
        }

        // fill m_timeSlots (required for layout)

        createTimeSlots();

        // Clean list - date range

        ArrayList clean = new ArrayList( list.size());

        for( int i = 0;i < list.size();i++ ) {
            MAssignmentSlot mas = ( MAssignmentSlot )list.get( i );

            if(( mas.getStartTime().equals( m_startDate ) || mas.getStartTime().after( m_startDate )) && ( mas.getEndTime().equals( m_endDate ) || mas.getEndTime().before( m_endDate ))) {
                clean.add( mas );
            }
        }

        // Delete Unavailability TimeSlots when all day assigments exist

        MAssignmentSlot[] sorted = new MAssignmentSlot[ clean.size()];

        clean.toArray( sorted );
        Arrays.sort( sorted,new MAssignmentSlot());    // sorted by start/end date
        list.clear();                                  // used as day list
        clean.clear();                                 // cleaned days

        Timestamp sortedDay = null;

        for( int i = 0;i < sorted.length;i++ ) {
            if( sortedDay == null ) {
                sortedDay = TimeUtil.getDay( sorted[ i ].getStartTime());
            }

            if( sortedDay.equals( TimeUtil.getDay( sorted[ i ].getStartTime()))) {
                list.add( sorted[ i ] );
            } else {

                // process info list -> clean

                layoutSlots( list,clean );

                // prepare next

                list.clear();
                list.add( sorted[ i ] );
                sortedDay = TimeUtil.getDay( sorted[ i ].getStartTime());
            }
        }

        // process info list -> clean

        layoutSlots( list,clean );

        // Return

        MAssignmentSlot[] retValue = new MAssignmentSlot[ clean.size()];

        clean.toArray( retValue );
        Arrays.sort( retValue,new MAssignmentSlot());    // sorted by start/end date

        return retValue;
    }    // getAssignmentSlots

    /**
     * Descripción de Método
     *
     *
     * @param list
     * @param clean
     */

    private void layoutSlots( ArrayList list,ArrayList clean ) {
        int size = list.size();

        // System.out.println("Start List=" + size + ", Clean=" + clean.size());

        if( size == 0 ) {
            return;
        } else if( size == 1 ) {
            MAssignmentSlot mas = ( MAssignmentSlot )list.get( 0 );

            layoutY( mas );
            clean.add( mas );

            return;
        }

        // Delete Unavailability TimeSlots when all day assigments exist

        boolean allDay = false;

        for( int i = 0;!allDay && (i < size);i++ ) {
            MAssignmentSlot mas = ( MAssignmentSlot )list.get( i );

            if( (mas.getStatus() == MAssignmentSlot.STATUS_NotAvailable) || (mas.getStatus() == MAssignmentSlot.STATUS_UnAvailable) || (mas.getStatus() == MAssignmentSlot.STATUS_NonBusinessDay) || (mas.getStatus() == MAssignmentSlot.STATUS_NotInSlotDay) ) {
                allDay = true;
            }
        }

        if( allDay ) {

            // delete Time Slot

            for( int i = 0;i < list.size();i++ ) {
                MAssignmentSlot mas = ( MAssignmentSlot )list.get( i );

                if( mas.getStatus() == MAssignmentSlot.STATUS_NotInSlotTime ) {
                    list.remove( i-- );
                }
            }
        }

        // Copy & Y layout remaining

        for( int i = 0;i < list.size();i++ ) {
            MAssignmentSlot mas = ( MAssignmentSlot )list.get( i );

            layoutY( mas );
            clean.add( mas );
        }

        // X layout

        int   maxYslots = m_timeSlots.length;
        int[] xSlots    = new int[ maxYslots ];    // number of parallel slots

        for( int i = 0;i < list.size();i++ ) {
            MAssignmentSlot mas = ( MAssignmentSlot )list.get( i );

            for( int y = mas.getYStart();y < mas.getYEnd();y++ ) {
                xSlots[ y ]++;
            }
        }

        // Max parallel X Slots

        int maxXslots = 0;

        for( int y = 0;y < xSlots.length;y++ ) {
            if( xSlots[ y ] > maxXslots ) {
                maxXslots = xSlots[ y ];
            }
        }

        // Only one column

        if( maxXslots < 2 ) {
            for( int i = 0;i < list.size();i++ ) {
                MAssignmentSlot mas = ( MAssignmentSlot )list.get( i );

                mas.setX( 0,1 );
            }

            return;
        }

        // Create xy Matrix

        ArrayList[][] matrix = new ArrayList[ maxXslots ][ maxYslots ];

        // Populate Matrix first column

        for( int y = 0;y < maxYslots;y++ ) {
            ArrayList xyList = new ArrayList();

            matrix[ 0 ][ y ] = xyList;

            // see if one assignment fits into slot

            for( int i = 0;i < list.size();i++ ) {
                MAssignmentSlot mas = ( MAssignmentSlot )list.get( i );

                if( (y >= mas.getYStart()) && (y <= mas.getYEnd())) {
                    xyList.add( mas );
                }
            }

            // initiate right columns

            for( int x = 1;x < maxXslots;x++ ) {
                matrix[ x ][ y ] = new ArrayList();
            }
        }    // for all y slots

        // if in one column cell, there is more than one, move it to the right

        for( int y = 0;y < maxYslots;y++ ) {

            // if an element is the same as the line above, move it there

            if( (y > 0) && (matrix[ 0 ][ y ].size() > 0) ) {
                for( int x = 1;x < maxXslots;x++ ) {
                    if( matrix[ x ][ y - 1 ].size() > 0 )    // above slot is not empty
                    {
                        Object above = matrix[ x ][ y - 1 ].get( 0 );

                        for( int i = 0;i < matrix[ x ][ y ].size();i++ ) {
                            if( above.equals( matrix[ 0 ][ y ].get( i )))    // same - move it
                            {
                                matrix[ x ][ y ].add( matrix[ 0 ][ y ].get( i ));
                                matrix[ 0 ][ y ].remove( i-- );
                            }
                        }
                    }
                }
            }                                     // if an element is the same as the line above, move it there

            // we need to move items to the right

            if( matrix[ 0 ][ y ].size() > 1 ) {
                Object above = null;

                if( (y > 0) && (matrix[ 0 ][ y - 1 ].size() > 0) ) {
                    above = matrix[ 0 ][ y - 1 ].get( 0 );
                }

                //

                for( int i = 0;matrix[ 0 ][ y ].size() > 1;i++ ) {
                    Object move = matrix[ 0 ][ y ].get( i );

                    if( !move.equals( above ))    // we can move it
                    {
                        for( int x = 1;(move != null) && (x < maxXslots);x++ ) {
                            if( matrix[ x ][ y ].size() == 0 )    // found an empty slot
                            {
                                matrix[ x ][ y ].add( move );
                                matrix[ 0 ][ y ].remove( i-- );
                                move = null;
                            }
                        }
                    }
                }
            }    // we need to move items to the right
        }        // for all y slots

        // go through the matrix and assign the X position

        for( int y = 0;y < maxYslots;y++ ) {
            for( int x = 0;x < maxXslots;x++ ) {
                if( matrix[ x ][ y ].size() > 0 ) {
                    MAssignmentSlot mas = ( MAssignmentSlot )matrix[ x ][ y ].get( 0 );

                    mas.setX( x,xSlots[ y ] );
                }
            }
        }

        // clean up

        matrix = null;
    }    // layoutSlots

    /**
     * Descripción de Método
     *
     *
     * @param mas
     */

    private void layoutY( MAssignmentSlot mas ) {
        int timeSlotStart = getTimeSlotIndex( mas.getStartTime(),false );
        int timeSlotEnd   = getTimeSlotIndex( mas.getEndTime(),true );

        if( TimeUtil.isAllDay( mas.getStartTime(),mas.getEndTime())) {
            timeSlotEnd = m_timeSlots.length - 1;
        }

        //

        mas.setY( timeSlotStart,timeSlotEnd );
    }    // layoutY

    /**
     * Descripción de Método
     *
     *
     * @param time
     * @param endTime
     *
     * @return
     */

    private int getTimeSlotIndex( Timestamp time,boolean endTime ) {

        // Just one slot

        if( m_timeSlots.length <= 1 ) {
            return 0;
        }

        // search for it

        for( int i = 0;i < m_timeSlots.length;i++ ) {
            if( m_timeSlots[ i ].inSlot( time,endTime )) {
                return i;
            }
        }

        log.log( Level.SEVERE,"MSchedule.getTimeSlotIndex - did not find Slot for " + time + " end=" + endTime );

        return 0;
    }    // getTimeSlotIndex

    /**
     * Descripción de Método
     *
     *
     * @param S_Resource_ID
     */

    private void getBaseInfo( int S_Resource_ID ) {

        // Resource is Active and Available

        String sql = MRole.getDefault( m_ctx,false ).addAccessSQL( "SELECT r.IsActive,r.IsAvailable,null,"    // r.IsSingleAssignment,"
                                       + "r.S_ResourceType_ID,rt.C_UOM_ID " + "FROM S_Resource r, S_ResourceType rt " + "WHERE r.S_Resource_ID=?" + " AND r.S_ResourceType_ID=rt.S_ResourceType_ID","r",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );

        //

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,S_Resource_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                if( !"Y".equals( rs.getString( 1 ))) {                    // Active
                    m_isAvailable = false;
                }

                if( m_isAvailable &&!"Y".equals( rs.getString( 2 ))) {    // Available
                    m_isAvailable = false;
                }

                m_isSingleAssignment = "Y".equals( rs.getString( 3 ));

                //

                m_S_ResourceType_ID = rs.getInt( 4 );
                m_C_UOM_ID          = rs.getInt( 5 );

                // log.fine( "- Resource_ID=" + m_S_ResourceType_ID + ",IsAvailable=" + m_isAvailable);

            } else {
                m_isAvailable = false;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"MSchedule.getBaseInfo",e );
            m_isAvailable = false;
        }

        m_S_Resource_ID = S_Resource_ID;
    }    // getBaseInfo

    /**
     * Descripción de Método
     *
     *
     * @param list
     * @param startTime
     * @param endTime
     */

    private void createTimeSlot( ArrayList list,Timestamp startTime,Timestamp endTime ) {

        // log.fine( "MSchedule.createTimeSlot");

        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        cal.setTimeInMillis( m_startDate.getTime());

        // End Date for Comparison

        GregorianCalendar calEnd = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        calEnd.setTimeInMillis( m_endDate.getTime());

        while( cal.before( calEnd )) {

            // 00:00..startTime

            cal.set( Calendar.HOUR_OF_DAY,0 );
            cal.set( Calendar.MINUTE,0 );
            cal.set( Calendar.SECOND,0 );
            cal.set( Calendar.MILLISECOND,0 );

            Timestamp start = new Timestamp( cal.getTimeInMillis());

            //

            GregorianCalendar cal_1 = new GregorianCalendar( Language.getLoginLanguage().getLocale());

            cal_1.setTimeInMillis( startTime.getTime());
            cal.set( Calendar.HOUR_OF_DAY,cal_1.get( Calendar.HOUR_OF_DAY ));
            cal.set( Calendar.MINUTE,cal_1.get( Calendar.MINUTE ));
            cal.set( Calendar.SECOND,cal_1.get( Calendar.SECOND ));

            Timestamp end = new Timestamp( cal.getTimeInMillis());

            //

            MAssignmentSlot ma = new MAssignmentSlot( start,end,Msg.getMsg( m_ctx,"ResourceNotInSlotTime" ),"",MAssignmentSlot.STATUS_NotInSlotTime );

            list.add( ma );

            // endTime .. 00:00 next day

            cal_1.setTimeInMillis( endTime.getTime());
            cal.set( Calendar.HOUR_OF_DAY,cal_1.get( Calendar.HOUR_OF_DAY ));
            cal.set( Calendar.MINUTE,cal_1.get( Calendar.MINUTE ));
            cal.set( Calendar.SECOND,cal_1.get( Calendar.SECOND ));
            start = new Timestamp( cal.getTimeInMillis());

            //

            cal.set( Calendar.HOUR_OF_DAY,0 );
            cal.set( Calendar.MINUTE,0 );
            cal.set( Calendar.SECOND,0 );
            cal.add( Calendar.DAY_OF_YEAR,1 );
            end = new Timestamp( cal.getTimeInMillis());

            //

            ma = new MAssignmentSlot( start,end,Msg.getMsg( m_ctx,"ResourceNotInSlotTime" ),"",MAssignmentSlot.STATUS_NotInSlotTime );
            list.add( ma );
        }
    }    // createTimeSlot

    /**
     * Descripción de Método
     *
     *
     * @param list
     * @param OnMonday
     * @param OnTuesday
     * @param OnWednesday
     * @param OnThursday
     * @param OnFriday
     * @param OnSaturday
     * @param OnSunday
     */

    private void createDaySlot( ArrayList list,boolean OnMonday,boolean OnTuesday,boolean OnWednesday,boolean OnThursday,boolean OnFriday,boolean OnSaturday,boolean OnSunday ) {

        // log.fine( "MSchedule.createDaySlot");

        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        cal.setTimeInMillis( m_startDate.getTime());

        // End Date for Comparison

        GregorianCalendar calEnd = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        calEnd.setTimeInMillis( m_endDate.getTime());

        while( cal.before( calEnd )) {
            int weekday = cal.get( Calendar.DAY_OF_WEEK );

            if(( !OnSaturday && (weekday == Calendar.SATURDAY) ) || ( !OnSunday && (weekday == Calendar.SUNDAY) ) || ( !OnMonday && (weekday == Calendar.MONDAY) ) || ( !OnTuesday && (weekday == Calendar.TUESDAY) ) || ( !OnWednesday && (weekday == Calendar.WEDNESDAY) ) || ( !OnThursday && (weekday == Calendar.THURSDAY) ) || ( !OnFriday && (weekday == Calendar.FRIDAY) ) ) {

                // 00:00..00:00 next day

                cal.set( Calendar.HOUR_OF_DAY,0 );
                cal.set( Calendar.MINUTE,0 );
                cal.set( Calendar.SECOND,0 );
                cal.set( Calendar.MILLISECOND,0 );

                Timestamp start = new Timestamp( cal.getTimeInMillis());

                cal.add( Calendar.DAY_OF_YEAR,1 );

                Timestamp       end = new Timestamp( cal.getTimeInMillis());
                MAssignmentSlot ma  = new MAssignmentSlot( start,end,Msg.getMsg( m_ctx,"ResourceNotInSlotDay" ),"",MAssignmentSlot.STATUS_NotInSlotDay );

                list.add( ma );
            } else {    // next day
                cal.add( Calendar.DAY_OF_YEAR,1 );
            }
        }
    }                   // createDaySlot

    /**
     * Descripción de Método
     *
     *
     * @param list
     * @param ma
     */

    private void createDaySlot( ArrayList list,MAssignmentSlot ma ) {

        // log.fine( "MSchedule.createDaySlot", ma);
        //

        Timestamp         start    = ma.getStartTime();
        GregorianCalendar calStart = new GregorianCalendar();

        calStart.setTime( start );
        calStart.set( Calendar.HOUR_OF_DAY,0 );
        calStart.set( Calendar.MINUTE,0 );
        calStart.set( Calendar.SECOND,0 );
        calStart.set( Calendar.MILLISECOND,0 );

        Timestamp         end    = ma.getEndTime();
        GregorianCalendar calEnd = new GregorianCalendar();

        calEnd.setTime( end );
        calEnd.set( Calendar.HOUR_OF_DAY,0 );
        calEnd.set( Calendar.MINUTE,0 );
        calEnd.set( Calendar.SECOND,0 );
        calEnd.set( Calendar.MILLISECOND,0 );

        //

        while( calStart.before( calEnd )) {
            Timestamp xStart = new Timestamp( calStart.getTimeInMillis());

            calStart.add( Calendar.DAY_OF_YEAR,1 );

            Timestamp       xEnd = new Timestamp( calStart.getTimeInMillis());
            MAssignmentSlot myMa = new MAssignmentSlot( xStart,xEnd,ma.getName(),ma.getDescription(),ma.getStatus());

            list.add( myMa );
        }
    }    // createDaySlot

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAssignmentSlot[] getDayTimeSlots() {
        return m_timeSlots;
    }    // getDayTimeSlots

    /**
     * Descripción de Método
     *
     */

    private void createTimeSlots() {

        // development error

        if( m_typeName == null ) {
            throw new IllegalStateException( "ResourceTyoeName not set" );
        }

        ArrayList list    = new ArrayList();
        MUOM      uom     = MUOM.get( m_ctx,m_C_UOM_ID );
        int       minutes = MUOMConversion.convertToMinutes( m_ctx,m_C_UOM_ID,Env.ONE );

        log.config( "Minutes=" + minutes );

        //

        if( (minutes > 0) && (minutes < 60 * 24) ) {

            // Set Start Time

            GregorianCalendar cal = new GregorianCalendar();

            cal.setTime( m_startDate );
            cal.set( Calendar.HOUR_OF_DAY,0 );
            cal.set( Calendar.MINUTE,0 );
            cal.set( Calendar.SECOND,0 );
            cal.set( Calendar.MILLISECOND,0 );

            // we have slots - create first

            if( m_slotStartTime != null ) {
                long start = cal.getTimeInMillis();

                cal.setTime( TimeUtil.getDayTime( m_startDate,m_slotStartTime ));    // set to start time
                cal.set( Calendar.SECOND,0 );
                cal.set( Calendar.MILLISECOND,0 );
                list.add( new MAssignmentSlot( start,cal.getTimeInMillis()));
            }

            // Set End Time

            GregorianCalendar calEnd = new GregorianCalendar();

            if( m_slotEndTime != null ) {
                calEnd.setTime( TimeUtil.getDayTime( m_startDate,m_slotEndTime ));
                calEnd.set( Calendar.SECOND,0 );
                calEnd.set( Calendar.MILLISECOND,0 );
            } else    // No Slot - all day
            {
                calEnd.setTime( m_startDate );
                calEnd.set( Calendar.HOUR_OF_DAY,0 );
                calEnd.set( Calendar.MINUTE,0 );
                calEnd.set( Calendar.SECOND,0 );
                calEnd.set( Calendar.MILLISECOND,0 );
                calEnd.add( Calendar.DAY_OF_YEAR,1 );
            }

//System.out.println("Start=" + new Timestamp(cal.getTimeInMillis()));
//System.out.println("Endt=" + new Timestamp(calEnd.getTimeInMillis()));

            // Set end Slot Time

            GregorianCalendar calEndSlot = new GregorianCalendar();

            calEndSlot.setTime( cal.getTime());
            calEndSlot.add( Calendar.MINUTE,minutes );

            while( cal.before( calEnd )) {
                list.add( new MAssignmentSlot( cal.getTimeInMillis(),calEndSlot.getTimeInMillis()));

                // Next Slot

                cal.add( Calendar.MINUTE,minutes );
                calEndSlot.add( Calendar.MINUTE,minutes );
            }

            // create last slot

            calEndSlot.setTime( cal.getTime());
            calEndSlot.set( Calendar.HOUR_OF_DAY,0 );
            calEndSlot.set( Calendar.MINUTE,0 );
            calEndSlot.set( Calendar.SECOND,0 );
            calEndSlot.set( Calendar.MILLISECOND,0 );
            calEndSlot.add( Calendar.DAY_OF_YEAR,1 );    // 00:00 next day
            list.add( new MAssignmentSlot( cal.getTimeInMillis(),calEndSlot.getTimeInMillis()));
        } else    // Day, ....
        {
            list.add( new MAssignmentSlot( TimeUtil.getDay( m_startDate ),TimeUtil.getNextDay( m_startDate )));
        }

        //

        m_timeSlots = new MAssignmentSlot[ list.size()];
        list.toArray( m_timeSlots );
    }    // createTimeSlots

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getS_Resource_ID() {
        return m_S_Resource_ID;
    }    // getS_Resource_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStartDate() {
        return m_startDate;
    }    // getStartDate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getEndDate() {
        return m_endDate;
    }    // getEndDate
}    // MSchedule



/*
 *  @(#)MSchedule.java   02.07.07
 * 
 *  Fin del fichero MSchedule.java
 *  
 *  Versión 2.2
 *
 */
