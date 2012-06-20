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

import java.awt.Color;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import org.openXpertya.util.Language;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAssignmentSlot implements Comparator {

    /**
     * Constructor de la clase ...
     *
     */

    public MAssignmentSlot() {
        this( null,null,null,null,STATUS_TimeSlot );
    }    // MAssignmentSlot

    /**
     * Constructor de la clase ...
     *
     *
     * @param startTime
     * @param endTime
     */

    public MAssignmentSlot( Timestamp startTime,Timestamp endTime ) {
        this( startTime,endTime,null,null,STATUS_TimeSlot );
        setDisplay( DISPLAY_TIME_FROM );
    }    // MAssignmentSlot

    /**
     * Constructor de la clase ...
     *
     *
     * @param startTime
     * @param endTime
     */

    public MAssignmentSlot( long startTime,long endTime ) {
        this( new Timestamp( startTime ),new Timestamp( endTime ),null,null,STATUS_TimeSlot );
        setDisplay( DISPLAY_TIME_FROM );
    }    // MAssignmentSlot

    /**
     * Constructor de la clase ...
     *
     *
     * @param startTime
     * @param endTime
     * @param name
     * @param description
     * @param status
     */

    public MAssignmentSlot( Timestamp startTime,Timestamp endTime,String name,String description,int status ) {
        setStartTime( startTime );
        setEndTime( endTime );
        setName( name );
        setDescription( description );
        setStatus( status );

        //
        // log.fine( toString());

    }    // MAssignmentSlot

    /**
     * Constructor de la clase ...
     *
     *
     * @param assignment
     */

    public MAssignmentSlot( MResourceAssignment assignment ) {
        setStatus( assignment.isConfirmed()
                   ?STATUS_Confirmed
                   :STATUS_NotConfirmed );
        setMAssignment( assignment );

        // log.fine( toString());

    }    // MAssignmentSlot

    /** Descripción de Campos */

    public static final int STATUS_NotAvailable = 0;

    /** Descripción de Campos */

    public static final int STATUS_UnAvailable = 11;

    /** Descripción de Campos */

    public static final int STATUS_NonBusinessDay = 12;

    /** Descripción de Campos */

    public static final int STATUS_NotInSlotDay = 21;

    /** Descripción de Campos */

    public static final int STATUS_NotInSlotTime = 22;

    /** Descripción de Campos */

    public static final int STATUS_NotConfirmed = 101;

    /** Descripción de Campos */

    public static final int STATUS_Confirmed = 102;

    /** Descripción de Campos */

    public static final int STATUS_TimeSlot = 100000;

    /** Descripción de Campos */

    private Timestamp m_startTime;

    /** Descripción de Campos */

    private Timestamp m_endTime;

    /** Descripción de Campos */

    private String m_name;

    /** Descripción de Campos */

    private String m_description;

    /** Descripción de Campos */

    private int m_status = STATUS_NotAvailable;

    /** Descripción de Campos */

    private int m_yStart = 0;

    /** Descripción de Campos */

    private int m_yEnd = 0;

    /** Descripción de Campos */

    private int m_xPos = 0;

    /** Descripción de Campos */

    private int m_xMax = 1;

    /** Descripción de Campos */

    private MResourceAssignment m_mAssignment;

    /** Descripción de Campos */

    private Language m_language = Language.getLoginLanguage();

    /** Descripción de Campos */

    public static final int DISPLAY_ALL = 0;

    /** Descripción de Campos */

    public static final int DISPLAY_TIME_FROM = 1;

    /** Descripción de Campos */

    public static final int DISPLAY_TIME_FROM_TO = 1;

    /** Descripción de Campos */

    public static final int DISPLAY_DATETIME_FROM_TO = 1;

    /** Descripción de Campos */

    public static final int DISPLAY_NAME = 1;

    /** Descripción de Campos */

    public static final int DISPLAY_NAME_DESCRIPTION = 1;

    /** Descripción de Campos */

    public static final int DISPLAY_FULL = 1;

    /** Descripción de Campos */

    private int m_displayMode = DISPLAY_FULL;

    /**
     * Descripción de Método
     *
     *
     * @param status
     */

    public void setStatus( int status ) {
        m_status = status;
    }    // setStatus

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getStatus() {
        return m_status;
    }    // getStatus

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isAssignment() {
        return( (m_status == STATUS_NotConfirmed) || (m_status == STATUS_Confirmed) );
    }    // isAssignment

    /**
     * Descripción de Método
     *
     *
     * @param background
     *
     * @return
     */

    public Color getColor( boolean background ) {

        // Not found, Inactive, not available

        if( m_status == STATUS_NotAvailable ) {
            return background
                   ?Color.gray
                   :Color.magenta;

            // Holiday

        } else if( m_status == STATUS_UnAvailable ) {
            return background
                   ?Color.gray
                   :Color.pink;

            // Vacation

        } else if( m_status == STATUS_NonBusinessDay ) {
            return background
                   ?Color.lightGray
                   :Color.red;

            // Out of normal hours

        } else if( (m_status == STATUS_NotInSlotDay) || (m_status == STATUS_NotInSlotTime) ) {
            return background
                   ?Color.lightGray
                   :Color.black;

            // Assigned

        } else if( m_status == STATUS_NotConfirmed ) {
            return background
                   ?Color.blue
                   :Color.white;

            // Confirmed

        } else if( m_status == STATUS_Confirmed ) {
            return background
                   ?Color.blue
                   :Color.black;
        }

        // Unknown

        return background
               ?Color.black
               :Color.white;
    }    // getColor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStartTime() {
        return m_startTime;
    }

    /**
     * Descripción de Método
     *
     *
     * @param startTime
     */

    public void setStartTime( Timestamp startTime ) {
        if( startTime == null ) {
            m_startTime = new Timestamp( System.currentTimeMillis());
        } else {
            m_startTime = startTime;
        }
    }    // setStartTime

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getEndTime() {
        return m_endTime;
    }

    /**
     * Descripción de Método
     *
     *
     * @param endTime
     */

    public void setEndTime( Timestamp endTime ) {
        if( endTime == null ) {
            m_endTime = m_startTime;
        } else {
            m_endTime = endTime;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param assignment
     */

    public void setMAssignment( MResourceAssignment assignment ) {
        if( assignment == null ) {
            return;
        }

        if( !isAssignment()) {
            throw new IllegalArgumentException( "Assignment Slot not an Assignment" );
        }

        //

        m_mAssignment = assignment;
        setStartTime( m_mAssignment.getAssignDateFrom());
        setEndTime( m_mAssignment.getAssignDateTo());
        setName( m_mAssignment.getName());
        setDescription( m_mAssignment.getDescription());
        setStatus( m_mAssignment.isConfirmed()
                   ?STATUS_Confirmed
                   :STATUS_NotConfirmed );
    }    // setMAssignment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MResourceAssignment getMAssignment() {
        return m_mAssignment;
    }    // getAssignment

    /**
     * Descripción de Método
     *
     *
     * @param name
     */

    public void setName( String name ) {
        if( name == null ) {
            m_name = "";
        } else {
            m_name = name;
        }
    }    // setName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void setDescription( String description ) {
        if( description == null ) {
            m_description = "";
        } else {
            m_description = description;
        }
    }    // setDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_description;
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @param yStart
     * @param yEnd
     */

    public void setY( int yStart,int yEnd ) {
        m_yStart = yStart;
        m_yEnd   = yEnd;
    }    // setY

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getYStart() {
        return m_yStart;
    }    // getYStart

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getYEnd() {
        return m_yEnd;
    }    // setYEnd

    /**
     * Descripción de Método
     *
     *
     * @param xPos
     * @param xMax
     */

    public void setX( int xPos,int xMax ) {
        m_xPos = xPos;

        if( xMax > m_xMax ) {
            m_xMax = xMax;
        }
    }    // setX

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getXPos() {
        return m_xPos;
    }    // setXPos

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getXMax() {
        return m_xMax;
    }    // setXMax

    /**
     * Descripción de Método
     *
     *
     * @param language
     */

    public void setLanguage( Language language ) {
        m_language = language;
    }    // setLanguage

    /**
     * Descripción de Método
     *
     *
     * @param displayMode
     */

    public void setDisplay( int displayMode ) {
        m_displayMode = displayMode;
    }    // setDisplay

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        if( m_displayMode == DISPLAY_TIME_FROM ) {
            return getInfoTimeFrom();
        } else if( m_displayMode == DISPLAY_TIME_FROM_TO ) {
            return getInfoTimeFromTo();
        } else if( m_displayMode == DISPLAY_DATETIME_FROM_TO ) {
            return getInfoDateTimeFromTo();
        } else if( m_displayMode == DISPLAY_NAME ) {
            return m_name;
        } else if( m_displayMode == DISPLAY_NAME_DESCRIPTION ) {
            return getInfoNameDescription();
        } else if( m_displayMode == DISPLAY_FULL ) {
            return getInfo();
        }

        // DISPLAY_ALL

        StringBuffer sb = new StringBuffer( "MAssignmentSlot[" );

        sb.append( m_startTime ).append( "-" ).append( m_endTime ).append( "-Status=" ).append( m_status ).append( ",Name=" ).append( m_name ).append( "," ).append( m_description ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfoTimeFrom() {
        return m_language.getTimeFormat().format( m_startTime );
    }    // getInfoTimeFrom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfoTimeFromTo() {
        StringBuffer sb = new StringBuffer();

        sb.append( m_language.getTimeFormat().format( m_startTime )).append( " - " ).append( m_language.getTimeFormat().format( m_endTime ));

        return sb.toString();
    }    // getInfoTimeFromTo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfoDateTimeFromTo() {
        StringBuffer sb = new StringBuffer();

        sb.append( m_language.getDateTimeFormat().format( m_startTime )).append( " - " );

        if( TimeUtil.isSameDay( m_startTime,m_endTime )) {
            sb.append( m_language.getTimeFormat().format( m_endTime ));
        } else {
            m_language.getDateTimeFormat().format( m_endTime );
        }

        return sb.toString();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfoNameDescription() {
        StringBuffer sb = new StringBuffer( m_name );

        if( m_description.length() > 0 ) {
            sb.append( " (" ).append( m_description ).append( ")" );
        }

        return sb.toString();
    }    // getInfoNameDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfo() {
        StringBuffer sb = new StringBuffer( getInfoDateTimeFromTo());

        sb.append( ": " ).append( m_name );

        if( m_description.length() > 0 ) {
            sb.append( " (" ).append( m_description ).append( ")" );
        }

        return sb.toString();
    }    // getInfo

    /**
     * Descripción de Método
     *
     *
     * @param time
     * @param endTime
     *
     * @return
     */

    public boolean inSlot( Timestamp time,boolean endTime ) {

        // Compare --

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( time );
        cal.set( Calendar.YEAR,1970 );
        cal.set( Calendar.DAY_OF_YEAR,1 );

        // handle -00:00 (end time)

        if( endTime && (cal.get( Calendar.HOUR_OF_DAY ) == 0) && (cal.get( Calendar.MINUTE ) == 0) ) {
            cal.set( Calendar.HOUR_OF_DAY,23 );
            cal.set( Calendar.MINUTE,59 );
        }

        Time compare = new Time( cal.getTimeInMillis());

        // Start Time --

        cal.setTime( m_startTime );
        cal.set( Calendar.YEAR,1970 );
        cal.set( Calendar.DAY_OF_YEAR,1 );

        Time start = new Time( cal.getTimeInMillis());

        // End time --

        cal.setTime( m_endTime );
        cal.set( Calendar.YEAR,1970 );
        cal.set( Calendar.DAY_OF_YEAR,1 );

        if( (cal.get( Calendar.HOUR_OF_DAY ) == 0) && (cal.get( Calendar.MINUTE ) == 0) ) {
            cal.set( Calendar.HOUR_OF_DAY,23 );
            cal.set( Calendar.MINUTE,59 );
        }

        Time end = new Time( cal.getTimeInMillis());

        // before start                    x |---|

        if( compare.before( start )) {

            // System.out.println("InSlot-false Compare=" + compare + " before start " + start);

            return false;
        }

        // after end                               |---| x

        if( compare.after( end )) {

            // System.out.println("InSlot-false Compare=" + compare + " after end " + end);

            return false;
        }

        // start                                   x---|

        if( !endTime && compare.equals( start )) {

            // System.out.println("InSlot-true Compare=" + compare + " = Start=" + start);

            return true;
        }

        //
        // end                                             |---x

        if( endTime && compare.equals( end )) {

            // System.out.println("InSlot-true Compare=" + compare + " = End=" + end);

            return true;
        }

        // between start/end               |-x-|

        if( compare.before( end )) {

            // System.out.println("InSlot-true Compare=" + compare + " before end " + end);

            return true;
        }

        return false;
    }    // inSlot

    /**
     * Descripción de Método
     *
     *
     * @param o1
     * @param o2
     *
     * @return
     */

    public int compare( Object o1,Object o2 ) {
        if( !( (o1 instanceof MAssignmentSlot) && (o2 instanceof MAssignmentSlot) ) ) {
            throw new ClassCastException( "MAssignmentSlot.compare arguments not MAssignmentSlot" );
        }

        MAssignmentSlot s1 = ( MAssignmentSlot )o1;
        MAssignmentSlot s2 = ( MAssignmentSlot )o2;

        // Start Date

        int result = s1.getStartTime().compareTo( s2.getStartTime());

        if( result != 0 ) {
            return result;
        }

        // Status

        result = s2.getStatus() - s1.getStatus();

        if( result != 0 ) {
            return result;
        }

        // End Date

        result = s1.getEndTime().compareTo( s2.getEndTime());

        if( result != 0 ) {
            return result;
        }

        // Name

        result = s1.getName().compareTo( s2.getName());

        if( result != 0 ) {
            return result;
        }

        // Description

        return s1.getDescription().compareTo( s2.getDescription());
    }    // compare

    /**
     * Descripción de Método
     *
     *
     * @param obj
     *
     * @return
     */

    public boolean equals( Object obj ) {
        if( obj instanceof MAssignmentSlot ) {
            MAssignmentSlot cmp = ( MAssignmentSlot )obj;

            if( m_startTime.equals( cmp.getStartTime()) && m_endTime.equals( cmp.getEndTime()) && (m_status == cmp.getStatus()) && m_name.equals( cmp.getName()) && m_description.equals( cmp.getDescription())) {
                return true;
            }
        }

        return false;
    }    // equals

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int hashCode() {
        return m_startTime.hashCode() + m_endTime.hashCode() + m_status + m_name.hashCode() + m_description.hashCode();
    }    // hashCode
}    // MAssignmentSlot



/*
 *  @(#)MAssignmentSlot.java   02.07.07
 * 
 *  Fin del fichero MAssignmentSlot.java
 *  
 *  Versión 2.2
 *
 */
