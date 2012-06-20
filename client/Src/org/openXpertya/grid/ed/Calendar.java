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



package org.openXpertya.grid.ed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Calendar extends JDialog implements ActionListener,MouseListener,ChangeListener,KeyListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     */

    public Calendar( Frame frame ) {
        this( frame,Msg.getMsg( Env.getCtx(),"Calendar" ),null,DisplayType.Date );
    }    // Calendar

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param title
     * @param startTS
     * @param displayType
     */

    public Calendar( Frame frame,String title,Timestamp startTS,int displayType ) {
        super( frame,title,true );
        log.info( (startTS == null)
                  ?"null"
                  :startTS.toString() + " " + displayType );
        m_displayType = displayType;

        //

        try {
            jbInit();
            setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Calendar",ex );
        }

        //

        loadData( startTS );
    }    // Calendar

    /** Descripción de Campos */

    private int m_displayType;

    /** Descripción de Campos */

    private GregorianCalendar m_calendar;

    /** Descripción de Campos */

    private boolean m_hasAM_PM = false;

    //

    /** Descripción de Campos */

    private CButton[] m_days;

    /** Descripción de Campos */

    private CButton m_today;

    /** Descripción de Campos */

    private int m_firstDay;

    //

    /** Descripción de Campos */

    private int m_currentDay;

    /** Descripción de Campos */

    private int m_currentMonth;

    /** Descripción de Campos */

    private int m_currentYear;

    /** Descripción de Campos */

    private int m_current24Hour = 0;

    /** Descripción de Campos */

    private int m_currentMinute = 0;

    //

    /** Descripción de Campos */

    private boolean m_setting = true;

    /** Descripción de Campos */

    private boolean m_abort = true;

    //

    /** Descripción de Campos */

    private long m_lastClick = System.currentTimeMillis();

    /** Descripción de Campos */

    private int m_lastDay = -1;

    //

    /** Descripción de Campos */

    private static final Insets ZERO_INSETS = new Insets( 0,0,0,0 );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Calendar.class );

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel monthPanel = new CPanel();

    /** Descripción de Campos */

    private CComboBox cMonth = new CComboBox();

    /** Descripción de Campos */

    private JSpinner cYear = new JSpinner( new SpinnerNumberModel( 2000,1900,2100,1 ));

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel dayPanel = new CPanel();

    /** Descripción de Campos */

    private GridLayout dayLayout = new GridLayout();

    /** Descripción de Campos */

    private GridBagLayout monthLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CButton bNext = new CButton();

    /** Descripción de Campos */

    private CButton bBack = new CButton();

    /** Descripción de Campos */

    private CPanel timePanel = new CPanel();

    /** Descripción de Campos */

    private CComboBox fHour = new CComboBox( getHours());

    /** Descripción de Campos */

    private CLabel lTimeSep = new CLabel();

    /** Descripción de Campos */

    private JSpinner fMinute = new JSpinner( new MinuteModel( 5 ));    // 5 minute snap size

    /** Descripción de Campos */

    private JCheckBox cbPM = new JCheckBox();

    /** Descripción de Campos */

    private JLabel lTZ = new JLabel();

    /** Descripción de Campos */

    private CButton bOK = new CButton();

    /** Descripción de Campos */

    private GridBagLayout timeLayout = new GridBagLayout();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        this.addKeyListener( this );

        //

        mainPanel.setLayout( mainLayout );
        mainLayout.setHgap( 2 );
        mainLayout.setVgap( 2 );
        mainPanel.setBorder( BorderFactory.createLoweredBevelBorder());
        getContentPane().add( mainPanel );

        // Month Panel

        monthPanel.setLayout( monthLayout );
        monthPanel.add( bBack,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        monthPanel.add( cYear,new GridBagConstraints( 3,0,1,1,1.0,0.0,GridBagConstraints.SOUTHEAST,GridBagConstraints.HORIZONTAL,new Insets( 0,5,0,0 ),0,0 ));
        monthPanel.add( bNext,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        monthPanel.add( cMonth,new GridBagConstraints( 1,0,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets( 0,0,0,0 ),0,0 ));
        mainPanel.add( monthPanel,BorderLayout.NORTH );
        cMonth.addActionListener( this );
        cYear.addChangeListener( this );
        bBack.setIcon( Env.getImageIcon( "Parent16.gif" ));    // <
        bBack.setMargin( new Insets( 0,0,0,0 ));
        bBack.addActionListener( this );
        bNext.setIcon( Env.getImageIcon( "Detail16.gif" ));    // >
        bNext.setMargin( new Insets( 0,0,0,0 ));
        bNext.addActionListener( this );

        // Day Panel

        dayPanel.setLayout( dayLayout );
        dayLayout.setColumns( 7 );
        dayLayout.setHgap( 2 );
        dayLayout.setRows( 7 );
        dayLayout.setVgap( 2 );
        mainPanel.add( dayPanel,BorderLayout.CENTER );

        // Time Panel

        timePanel.setLayout( timeLayout );
        lTimeSep.setText( " : " );
        timePanel.add( fHour,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets( 0,6,0,0 ),0,0 ));
        timePanel.add( lTimeSep,new GridBagConstraints( 1,0,1,1,0.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        timePanel.add( fMinute,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,0,0 ),0,0 ));
        timePanel.add( cbPM,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,5,0,0 ),0,0 ));
        timePanel.add( lTZ,new GridBagConstraints( 4,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,4,0,0 ),0,0 ));
        timePanel.add( bOK,new GridBagConstraints( 5,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,6,0,2 ),0,0 ));
        mainPanel.add( timePanel,BorderLayout.SOUTH );
        fHour.addKeyListener( this );    // Enter returns

        // JSpinner ignores KeyListener

        (( JSpinner.DefaultEditor )fMinute.getEditor()).getTextField().addKeyListener( this );
        fMinute.addChangeListener( this );
        cbPM.addActionListener( this );
        cbPM.addKeyListener( this );
        bOK.setIcon( Env.getImageIcon( "Ok16.gif" ));
        bOK.setMargin( new Insets( 0,1,0,1 ));
        bOK.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void processWindowEvent( WindowEvent e ) {

        // log.config( "Calendar.processWindowEvent", e);

        super.processWindowEvent( e );

        if( e.getID() == WindowEvent.WINDOW_OPENED ) {
            if( m_displayType == DisplayType.Time ) {
                fHour.requestFocus();
            } else if( m_today != null ) {
                m_today.requestFocus();
            }
        }
    }    // processWindowEvent

    /**
     * Descripción de Método
     *
     *
     * @param startTS
     */

    private void loadData( Timestamp startTS ) {
        m_calendar = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        if( startTS == null ) {
            m_calendar.setTimeInMillis( System.currentTimeMillis());
        } else {
            m_calendar.setTime( startTS );
        }

        m_firstDay = m_calendar.getFirstDayOfWeek();

        //

        Locale           loc        = Language.getLoginLanguage().getLocale();
        SimpleDateFormat formatDate = ( SimpleDateFormat )DateFormat.getDateInstance( DateFormat.LONG,loc );

        // Short: h:mm a - HH:mm   Long: h:mm:ss a z - HH:mm:ss z

        SimpleDateFormat formatTime = ( SimpleDateFormat )DateFormat.getTimeInstance( DateFormat.SHORT,loc );

        m_hasAM_PM = formatTime.toPattern().indexOf( 'a' ) != -1;

        if( m_hasAM_PM ) {
            cbPM.setText( formatTime.getDateFormatSymbols().getAmPmStrings()[ 1 ] );
        } else {
            cbPM.setVisible( false );
        }

        // Years

        m_currentYear = m_calendar.get( java.util.Calendar.YEAR );
        cYear.setEditor( new JSpinner.NumberEditor( cYear,"0000" ));
        cYear.setValue( new Integer( m_currentYear ));

        // Months          -> 0=Jan 12=_

        String[] months = formatDate.getDateFormatSymbols().getMonths();

        for( int i = 0;i < months.length;i++ ) {
            KeyNamePair p = new KeyNamePair( i + 1,months[ i ] );

            if( !months[ i ].equals( "" )) {
                cMonth.addItem( p );
            }
        }

        m_currentMonth = m_calendar.get( java.util.Calendar.MONTH ) + 1;    // Jan=0
        cMonth.setSelectedIndex( m_currentMonth - 1 );

        // Week Days       -> 0=_  1=Su  .. 7=Sa

        String[] days = formatDate.getDateFormatSymbols().getShortWeekdays();    // 0 is blank, 1 is Sunday

        for( int i = m_firstDay;i < 7 + m_firstDay;i++ ) {
            int index = (i > 7)
                        ?i - 7
                        :i;

            dayPanel.add( createWeekday( days[ index ] ),null );
        }

        // Days

        m_days       = new CButton[ 6 * 7 ];
        m_currentDay = m_calendar.get( java.util.Calendar.DATE );

        for( int i = 0;i < 6;i++ ) {    // six weeks a month maximun
            for( int j = 0;j < 7;
                    j++ )               // seven days
            {
                int index = i * 7 + j;

                m_days[ index ] = createDay();
                dayPanel.add( m_days[ index ],null );
            }
        }

        // Today button

        m_days[ m_days.length - 1 ].setBackground( Color.green );
        m_days[ m_days.length - 1 ].setText( "*" );
        m_days[ m_days.length - 1 ].setToolTipText( Msg.getMsg( Env.getCtx(),"Today" ));

        // Date/Time

        m_current24Hour = m_calendar.get( java.util.Calendar.HOUR_OF_DAY );
        m_currentMinute = m_calendar.get( java.util.Calendar.MINUTE );

        // What to show

        timePanel.setVisible( (m_displayType == DisplayType.DateTime) || (m_displayType == DisplayType.Time) );
        monthPanel.setVisible( m_displayType != DisplayType.Time );
        dayPanel.setVisible( m_displayType != DisplayType.Time );

        // update UI from m_current...

        m_setting = false;
        setCalendar();
    }    // loadData

    /**
     * Descripción de Método
     *
     *
     * @param title
     *
     * @return
     */

    private JLabel createWeekday( String title ) {
        JLabel label = new JLabel( title );

        label.setBorder( BorderFactory.createRaisedBevelBorder());
        label.setHorizontalAlignment( SwingConstants.CENTER );
        label.setHorizontalTextPosition( SwingConstants.CENTER );
        label.setRequestFocusEnabled( false );

        return label;
    }    // createWeekday

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private CButton createDay() {
        CButton button = new CButton();

        button.setBorder( BorderFactory.createLoweredBevelBorder());
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setMargin( ZERO_INSETS );
        button.addActionListener( this );
        button.addMouseListener( this );
        button.addKeyListener( this );

        return button;
    }    // createWeekday

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private Object[] getHours() {
        Locale loc = Language.getLoginLanguage().getLocale();

        // Short: h:mm a - HH:mm   Long: h:mm:ss a z - HH:mm:ss z

        SimpleDateFormat formatTime = ( SimpleDateFormat )DateFormat.getTimeInstance( DateFormat.SHORT,loc );

        m_hasAM_PM = formatTime.toPattern().indexOf( 'a' ) != -1;

        //

        Object[] retValue = new Object[ m_hasAM_PM
                                        ?12
                                        :24 ];

        if( m_hasAM_PM ) {
            retValue[ 0 ] = "12";

            for( int i = 1;i < 10;i++ ) {
                retValue[ i ] = " " + String.valueOf( i );
            }

            for( int i = 10;i < 12;i++ ) {
                retValue[ i ] = String.valueOf( i );
            }
        } else {
            for( int i = 0;i < 10;i++ ) {
                retValue[ i ] = "0" + String.valueOf( i );
            }

            for( int i = 10;i < 24;i++ ) {
                retValue[ i ] = String.valueOf( i );
            }
        }

        return retValue;
    }    // getHours

    /**
     * Descripción de Método
     *
     */

    private void setCalendar() {
        if( m_setting ) {
            return;
        }

        // log.config( "Calendar.setCalendar");

        // --- Set Month & Year

        m_setting = true;
        cMonth.setSelectedIndex( m_currentMonth - 1 );
        cYear.setValue( new Integer( m_currentYear ));
        m_setting = false;

        // --- Set Day
        // what is the first day in the selected month?

        m_calendar.set( m_currentYear,m_currentMonth - 1,1 );    // Month is zero based

        int dayOne   = m_calendar.get( java.util.Calendar.DAY_OF_WEEK );
        int lastDate = m_calendar.getActualMaximum( java.util.Calendar.DATE );

        // convert to index

        dayOne -= m_firstDay;

        if( dayOne < 0 ) {
            dayOne += 7;
        }

        lastDate += dayOne - 1;

        // for all buttons but the last

        int curDay = 1;

        for( int i = 0;i < m_days.length - 1;i++ ) {
            if( (i >= dayOne) && (i <= lastDate) ) {
                if( m_currentDay == curDay ) {
                    m_days[ i ].setBackground( Color.blue );
                    m_days[ i ].setForeground( Color.yellow );
                    m_today = m_days[ i ];
                    m_today.requestFocus();
                } else {
                    m_days[ i ].setBackground( Color.white );
                    m_days[ i ].setForeground( Color.black );
                }

                m_days[ i ].setText( String.valueOf( curDay++ ));
                m_days[ i ].setReadWrite( true );
            } else {
                m_days[ i ].setText( "" );
                m_days[ i ].setReadWrite( false );
                m_days[ i ].setBackground( CompierePLAF.getFieldBackground_Inactive());
            }
        }

        // Set Hour

        boolean pm    = m_current24Hour > 12;
        int     index = m_current24Hour;

        if( pm && m_hasAM_PM ) {
            index -= 12;
        }

        if( (index < 0) || (index >= fHour.getItemCount())) {
            index = 0;
        }

        fHour.setSelectedIndex( index );

        // Set Minute

        int m = m_calendar.get( java.util.Calendar.MINUTE );

        fMinute.setValue( new Integer( m ));

        // Set PM

        cbPM.setSelected( pm );

        // Set TZ

        TimeZone tz = m_calendar.getTimeZone();

        lTZ.setText( tz.getDisplayName( tz.inDaylightTime( m_calendar.getTime()),TimeZone.SHORT ));

        // Update Calendar

        m_calendar.set( m_currentYear,m_currentMonth - 1,m_currentDay,m_current24Hour,m_currentMinute,0 );
        m_calendar.set( java.util.Calendar.MILLISECOND,0 );
    }    // setCalendar

    /**
     * Descripción de Método
     *
     */

    private void setTime() {

        // Hour

        int h = fHour.getSelectedIndex();

        m_current24Hour = h;

        if( m_hasAM_PM && cbPM.isSelected()) {
            m_current24Hour += 12;
        }

        if( (m_current24Hour < 0) || (m_current24Hour > 23) ) {
            m_current24Hour = 0;
        }

        // Minute

        Integer ii = ( Integer )fMinute.getValue();

        m_currentMinute = ii.intValue();

        if( (m_currentMinute < 0) || (m_currentMinute > 59) ) {
            m_currentMinute = 0;
        }
    }    // setTime

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getTimestamp() {

        // log.config( "Calendar.getTimeStamp");
        // Set Calendar

        m_calendar.set( m_currentYear,m_currentMonth - 1,m_currentDay,m_current24Hour,m_currentMinute,0 );
        m_calendar.set( java.util.Calendar.MILLISECOND,0 );

        // Return value

        if( m_abort ) {
            return null;
        }

        long time = m_calendar.getTimeInMillis();

        if( m_displayType == DisplayType.Date ) {
            time = new java.sql.Date( time ).getTime();
        } else if( m_displayType == DisplayType.Time ) {
            time = new Time( time ).getTime();    // based on 1970-01-01
        }

        return new Timestamp( time );
    }    // getTimestamp

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( m_setting ) {
            return;
        }

        // log.config( "Calendar.actionPerformed");

        setTime();

        if( e.getSource() == bOK ) {
            m_abort = false;
            dispose();

            return;
        } else if( e.getSource() == bBack ) {
            if( --m_currentMonth < 1 ) {
                m_currentMonth = 12;
                m_currentYear--;
            }

            m_lastDay = -1;
        } else if( e.getSource() == bNext ) {
            if( ++m_currentMonth > 12 ) {
                m_currentMonth = 1;
                m_currentYear++;
            }

            m_lastDay = -1;
        } else if( e.getSource() instanceof JButton ) {
            JButton b    = ( JButton )e.getSource();
            String  text = b.getText();

            // Set to today's date

            if( text.equals( "*" )) {
                m_calendar.setTime( new Timestamp( System.currentTimeMillis()));
                m_currentDay   = m_calendar.get( java.util.Calendar.DATE );
                m_currentMonth = m_calendar.get( java.util.Calendar.MONTH ) + 1;
                m_currentYear = m_calendar.get( java.util.Calendar.YEAR );
            }

            // we have a day

            else if( text.length() > 0 ) {
                m_currentDay = Integer.parseInt( text );

                long currentClick = System.currentTimeMillis();

                if( (m_currentDay == m_lastDay) && (currentClick - m_lastClick < 1000) )    // double click 1 second
                {
                    m_abort = false;
                    dispose();

                    return;
                }

                m_lastClick = currentClick;
                m_lastDay   = m_currentDay;
            }
        } else if( e.getSource() == cbPM ) {
            setTime();
            m_lastDay = -1;
        } else {

            // Set Month

            m_currentMonth = cMonth.getSelectedIndex() + 1;
            m_lastDay      = -1;
        }

        setCalendar();
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        if( m_setting ) {
            return;
        }

        // Set Minute

        if( e.getSource() == fMinute ) {
            setTime();

            return;
        }

        // Set Year

        m_currentYear = (( Integer )cYear.getValue()).intValue();
        m_lastDay     = -1;
        setCalendar();
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        if( e.getClickCount() == 2 ) {
            m_abort = false;
            dispose();
        }
    }    // mouseClicked

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mousePressed( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseEntered( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseExited( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseReleased( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyReleased( KeyEvent e ) {

        // System.out.println("Released " + e);
        // Day Buttons

        if( e.getSource() instanceof JButton ) {
            if( e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ) {
                if( ++m_currentMonth > 12 ) {
                    m_currentMonth = 1;
                    m_currentYear++;
                }

                setCalendar();

                return;
            }

            if( e.getKeyCode() == KeyEvent.VK_PAGE_UP ) {
                if( --m_currentMonth < 1 ) {
                    m_currentMonth = 12;
                    m_currentYear--;
                }

                setCalendar();

                return;
            }

            // Arrows

            int offset = 0;

            if( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
                offset = 1;
            } else if( e.getKeyCode() == KeyEvent.VK_LEFT ) {
                offset = -1;
            } else if( e.getKeyCode() == KeyEvent.VK_UP ) {
                offset = -7;
            } else if( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                offset = 7;
            }

            if( offset != 0 ) {
                System.out.println( m_calendar.getTime() + "  offset=" + offset );
                m_calendar.add( java.util.Calendar.DAY_OF_YEAR,offset );
                System.out.println( m_calendar.getTime());
                m_currentDay = m_calendar.get( java.util.Calendar.DAY_OF_MONTH );
                m_currentMonth = m_calendar.get( java.util.Calendar.MONTH ) + 1;
                m_currentYear = m_calendar.get( java.util.Calendar.YEAR );
                setCalendar();

                return;
            }

            // something else

            actionPerformed( new ActionEvent( e.getSource(),ActionEvent.ACTION_PERFORMED,"" ));
        }

        // Pressed Enter anywhere
        int code = e.getKeyCode();
        if( code == KeyEvent.VK_ENTER ) {
            m_abort = false;
            setTime();
            dispose();

            return;
        
        } else if( (code == KeyEvent.VK_CANCEL) || (code == KeyEvent.VK_ESCAPE) ) {
            m_abort = true;
            dispose();

            return;
        }


        // Modified Hour/Miinute

        setTime();
        m_lastDay = -1;
    }    // keyReleased

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyTyped( KeyEvent e ) {

        // System.out.println("Typed " + e);

    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyPressed( KeyEvent e ) {

        // System.out.println("Pressed " + e);

    }
}    // Calendar


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class MinuteModel extends SpinnerNumberModel {

    /**
     * Constructor de la clase ...
     *
     *
     * @param snapSize
     */

    public MinuteModel( int snapSize ) {
        super( 0,0,59,1 );    // Integer Model
        m_snapSize = snapSize;
    }    // MinuteModel

    /** Descripción de Campos */

    private int m_snapSize;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getNextValue() {
        int minutes = (( Integer )getValue()).intValue();

        minutes += m_snapSize;

        if( minutes >= 60 ) {
            minutes -= 60;
        }

        //

        int steps = minutes / m_snapSize;

        return new Integer( steps * m_snapSize );
    }    // getNextValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getPreviousValue() {
        int minutes = (( Integer )getValue()).intValue();

        minutes -= m_snapSize;

        if( minutes < 0 ) {
            minutes += 60;
        }

        //

        int steps = minutes / m_snapSize;

        if( minutes % m_snapSize != 0 ) {
            steps++;
        }

        if( steps * m_snapSize > 59 ) {
            steps = 0;
        }

        return new Integer( steps * m_snapSize );
    }    // getNextValue
}    // MinuteModel



/*
 *  @(#)Calendar.java   02.07.07
 * 
 *  Fin del fichero Calendar.java
 *  
 *  Versión 2.2
 *
 */
