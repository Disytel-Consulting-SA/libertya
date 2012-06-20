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



package org.openXpertya.apps.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openXpertya.model.MAssignmentSlot;
import org.openXpertya.model.MSchedule;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VSchedule extends JPanel {

    /**
     * Constructor de la clase ...
     *
     *
     * @param is
     * @param type
     */

    public VSchedule( InfoSchedule is,int type ) {
        m_type  = type;
        m_model = new MSchedule( Env.getCtx());
        schedulePanel.setType( m_type );
        schedulePanel.setTimePanel( timePanel );
        schedulePanel.setInfoSchedule( is );    // for callback

        try {
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VSchedule",e );
        }
    }    // VSchedule

    /** Descripción de Campos */

    static public final int TYPE_DAY = Calendar.DAY_OF_MONTH;

    /** Descripción de Campos */

    static public final int TYPE_WEEK = Calendar.WEEK_OF_YEAR;

    /** Descripción de Campos */

    static public final int TYPE_MONTH = Calendar.MONTH;

    /** Descripción de Campos */

    private int m_type = TYPE_DAY;

    /** Descripción de Campos */

    private MSchedule m_model = null;

    /** Descripción de Campos */

    private Timestamp m_startDate;

    /** Descripción de Campos */

    private Timestamp m_endDate;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VSchedule.class );

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private VScheduleTimePanel timePanel = new VScheduleTimePanel();

    /** Descripción de Campos */

    private VSchedulePanel schedulePanel = new VSchedulePanel();

    /** Descripción de Campos */

    private JScrollPane schedulePane = new JScrollPane();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setLayout( mainLayout );
        this.add( timePanel,BorderLayout.WEST );

        //
        // schedulePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        schedulePane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER );
        schedulePane.getViewport().add( schedulePanel,null );
        schedulePane.setPreferredSize( new Dimension( 200,200 ));
        schedulePane.setBorder( null );
        this.add( schedulePane,BorderLayout.CENTER );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param S_Resource_ID
     * @param date
     */

    public void recreate( int S_Resource_ID,Timestamp date ) {

        // Calculate Start Day

        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( date );
        cal.set( Calendar.HOUR,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        if( m_type == TYPE_WEEK ) {
            cal.set( Calendar.DAY_OF_WEEK,cal.getFirstDayOfWeek());
        } else if( m_type == TYPE_MONTH ) {
            cal.set( Calendar.DAY_OF_MONTH,1 );
        }

        m_startDate = new Timestamp( cal.getTimeInMillis());

        // Calculate End Date

        cal.add( m_type,1 );
        m_endDate = new Timestamp( cal.getTimeInMillis());

        //

        log.config( "(" + m_type + ") Resource_ID=" + S_Resource_ID + ": " + m_startDate + "->" + m_endDate );

        // Create Slots

        MAssignmentSlot[] mas = m_model.getAssignmentSlots( S_Resource_ID,m_startDate,m_endDate,null,true,null );
        MAssignmentSlot[] mts = m_model.getDayTimeSlots();

        // Set Panels

        timePanel.setTimeSlots( mts );
        schedulePanel.setAssignmentSlots( mas,S_Resource_ID,m_startDate,m_endDate );

        // Set Height

        schedulePanel.setHeight( timePanel.getPreferredSize().height );

        // repaint();

    }    // recreate

    /**
     * Descripción de Método
     *
     *
     * @param createNew
     */

    public void setCreateNew( boolean createNew ) {
        schedulePanel.setCreateNew( createNew );
    }    // setCreateNew

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_model   = null;
        timePanel = null;

        if( schedulePanel != null ) {
            schedulePanel.dispose();
        }

        schedulePanel = null;
        this.removeAll();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStartDate() {
        return m_startDate;
    }    // getStartDate
}    // VSchedule



/*
 *  @(#)VSchedule.java   02.07.07
 * 
 *  Fin del fichero VSchedule.java
 *  
 *  Versión 2.2
 *
 */
