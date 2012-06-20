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
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.compiere.plaf.CompiereColor;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.model.MResourceAssignment;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InfoSchedule extends JDialog implements ActionListener,ChangeListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param mAssignment
     * @param createNew
     */

    public InfoSchedule( Frame frame,MResourceAssignment mAssignment,boolean createNew ) {
        super( frame,Msg.getMsg( Env.getCtx(),"InfoSchedule" ),(frame != null) && createNew );

        if( mAssignment == null ) {
            m_mAssignment = new MResourceAssignment( Env.getCtx(),0,null );
        } else {
            m_mAssignment = mAssignment;
        }

        if( mAssignment != null ) {
            log.info( mAssignment.toString());
        }

        m_dateFrom = m_mAssignment.getAssignDateFrom();

        if( m_dateFrom == null ) {
            m_dateFrom = new Timestamp( System.currentTimeMillis());
        }

        m_createNew = createNew;

        try {
            jbInit();
            dynInit( createNew );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"InfoSchedule",ex );
        }

        AEnv.showCenterWindow( frame,this );
    }    // InfoSchedule

    /**
     * Constructor de la clase ...
     *
     */

    public InfoSchedule() {
        this( null,null,false );
    }    // InfoSchedule

    /** Descripción de Campos */

    private MResourceAssignment m_mAssignment;

    /** Descripción de Campos */

    private Timestamp m_dateFrom = null;

    /** Descripción de Campos */

    private boolean m_loading = false;

    /** Descripción de Campos */

    private boolean m_createNew;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( InfoSchedule.class );

    /** Descripción de Campos */

    private JPanel mainPanel = new JPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private JPanel parameterPanel = new JPanel();

    /** Descripción de Campos */

    private GridBagLayout parameterLayout = new GridBagLayout();

    /** Descripción de Campos */

    private JLabel labelResourceType = new JLabel();

    /** Descripción de Campos */

    private JComboBox fieldResourceType = new JComboBox();

    /** Descripción de Campos */

    private JLabel labelResource = new JLabel();

    /** Descripción de Campos */

    private JComboBox fieldResource = new JComboBox();

    /** Descripción de Campos */

    private JButton bPrevious = new JButton();

    /** Descripción de Campos */

    private JLabel labelDate = new JLabel();

    /** Descripción de Campos */

    private VDate fieldDate = new VDate();

    /** Descripción de Campos */

    private JButton bNext = new JButton();

    /** Descripción de Campos */

    private JTabbedPane timePane = new JTabbedPane();

    /** Descripción de Campos */

    private VSchedule daySchedule = new VSchedule( this,VSchedule.TYPE_DAY );

    /** Descripción de Campos */

    private VSchedule weekSchedule = new VSchedule( this,VSchedule.TYPE_WEEK );

    /** Descripción de Campos */

    private VSchedule monthSchedule = new VSchedule( this,VSchedule.TYPE_MONTH );

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        mainPanel.setLayout( mainLayout );
        parameterPanel.setLayout( parameterLayout );
        labelResourceType.setHorizontalTextPosition( SwingConstants.LEADING );
        labelResourceType.setText( Msg.translate( Env.getCtx(),"S_ResourceType_ID" ));
        labelResource.setHorizontalTextPosition( SwingConstants.LEADING );
        labelResource.setText( Msg.translate( Env.getCtx(),"S_Resource_ID" ));
        bPrevious.setMargin( new Insets( 0,0,0,0 ));
        bPrevious.setText( "<" );
        labelDate.setText( Msg.translate( Env.getCtx(),"Date" ));
        bNext.setMargin( new Insets( 0,0,0,0 ));
        bNext.setText( ">" );
        getContentPane().add( mainPanel,BorderLayout.CENTER );
        mainPanel.add( parameterPanel,BorderLayout.NORTH );
        parameterPanel.add( labelResourceType,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 8,8,0,0 ),0,0 ));
        parameterPanel.add( fieldResourceType,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,8,8,4 ),0,0 ));
        parameterPanel.add( labelResource,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 8,4,0,4 ),0,0 ));
        parameterPanel.add( fieldResource,new GridBagConstraints( 1,1,1,1,0.5,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets( 0,0,8,4 ),0,0 ));
        parameterPanel.add( bPrevious,new GridBagConstraints( 2,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,8,8,0 ),0,0 ));
        parameterPanel.add( labelDate,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 8,0,0,0 ),0,0 ));
        parameterPanel.add( fieldDate,new GridBagConstraints( 3,1,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,0,8,0 ),0,0 ));
        parameterPanel.add( bNext,new GridBagConstraints( 4,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,8,8 ),0,0 ));

        //

        mainPanel.add( new JScrollPane( timePane ),BorderLayout.CENTER );
        timePane.add( daySchedule,Msg.getMsg( Env.getCtx(),"Day" ));
        timePane.add( weekSchedule,Msg.getMsg( Env.getCtx(),"Week" ));
        timePane.add( monthSchedule,Msg.getMsg( Env.getCtx(),"Month" ));

        // timePane.add(daySchedule,  Msg.getMsg(Env.getCtx(), "Day"));
        // timePane.add(weekSchedule,  Msg.getMsg(Env.getCtx(), "Week"));
        // timePane.add(monthSchedule,   Msg.getMsg(Env.getCtx(), "Month"));

        timePane.addChangeListener( this );

        //

        mainPanel.add( confirmPanel,BorderLayout.SOUTH );

        //

        this.getContentPane().add( statusBar,BorderLayout.SOUTH );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param createNew
     */

    private void dynInit( boolean createNew ) {

        // Resource

        fillResourceType();
        fillResource();
        fieldResourceType.addActionListener( this );
        fieldResource.addActionListener( this );

        // Date

        fieldDate.setValue( m_dateFrom );
        fieldDate.addActionListener( this );
        bPrevious.addActionListener( this );
        bNext.addActionListener( this );

        // Set Init values

        daySchedule.setCreateNew( createNew );
        weekSchedule.setCreateNew( createNew );
        monthSchedule.setCreateNew( createNew );

        //

        confirmPanel.addActionListener( this );
        displayCalendar();
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void fillResourceType() {

        // Get ResourceType of selected Resource

        int S_ResourceType_ID = 0;

        if( m_mAssignment.getS_Resource_ID() != 0 ) {
            String sql = "SELECT S_ResourceType_ID FROM S_Resource WHERE S_Resource_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,m_mAssignment.getS_Resource_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    S_ResourceType_ID = rs.getInt( 1 );
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"InfoSchedule.fillResourceType-1",e );
            }
        }

        // Get Resource Types

        String sql = MRole.getDefault().addAccessSQL( "SELECT S_ResourceType_ID, Name FROM S_ResourceType WHERE IsActive='Y' ORDER BY 2","S_ResourceType",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
        KeyNamePair defaultValue = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                KeyNamePair pp = new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 ));

                if( S_ResourceType_ID == pp.getKey()) {
                    defaultValue = pp;
                }

                fieldResourceType.addItem( pp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"InfoSchedule.fillResourceType-2",e );
        }

        if( defaultValue != null ) {
            fieldResourceType.setSelectedItem( defaultValue );
        }
    }    // fillResourceType

    /**
     * Descripción de Método
     *
     */

    private void fillResource() {

        // Get Resource Type

        KeyNamePair pp = ( KeyNamePair )fieldResourceType.getSelectedItem();

        if( pp == null ) {
            return;
        }

        int         S_ResourceType_ID = pp.getKey();
        KeyNamePair defaultValue      = null;

        // Load Resources

        m_loading = true;
        fieldResource.removeAllItems();

        String sql = "SELECT S_Resource_ID, Name FROM S_Resource WHERE S_ResourceType_ID=? ORDER BY 2";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,S_ResourceType_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                pp = new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 ));

                if( m_mAssignment.getS_Resource_ID() == pp.getKey()) {
                    defaultValue = pp;
                }

                fieldResource.addItem( pp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        if( defaultValue != null ) {
            fieldResource.setSelectedItem( defaultValue );
        }

        m_loading = false;
    }    // fillResource

    /**
     * Descripción de Método
     *
     */

    private void displayCalendar() {

        // Get Values

        KeyNamePair pp = ( KeyNamePair )fieldResource.getSelectedItem();

        if( pp == null ) {
            return;
        }

        int S_Resource_ID = pp.getKey();

        m_mAssignment.setS_Resource_ID( S_Resource_ID );

        Timestamp date  = fieldDate.getTimestamp();
        int       index = timePane.getSelectedIndex();

        log.config( "Index=" + index + ", ID=" + S_Resource_ID + " - " + date );

        // Set Info

        m_loading = true;

        if( index == 0 ) {
            daySchedule.recreate( S_Resource_ID,date );
        } else if( index == 1 ) {
            weekSchedule.recreate( S_Resource_ID,date );
        } else {
            monthSchedule.recreate( S_Resource_ID,date );
        }

        m_loading = false;
        repaint();
    }    // displayCalendar

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        daySchedule.dispose();
        weekSchedule.dispose();
        monthSchedule.dispose();
        this.removeAll();
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( m_loading ) {
            return;
        }

        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();

            //

        } else if( e.getSource() == fieldResourceType ) {
            fillResource();
            displayCalendar();
        }

        //

        else if( (e.getSource() == fieldResource) || (e.getSource() == fieldDate) ) {
            displayCalendar();

            //

        } else if( e.getSource() == bPrevious ) {
            adjustDate( -1 );
        } else if( e.getSource() == bNext ) {
            adjustDate( +1 );
        }

        //

        this.setCursor( Cursor.getDefaultCursor());
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        displayCalendar();
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param diff
     */

    private void adjustDate( int diff ) {
        Timestamp         date = fieldDate.getTimestamp();
        GregorianCalendar cal  = new GregorianCalendar();

        cal.setTime( date );

        if( timePane.getSelectedIndex() == 0 ) {
            cal.add( java.util.Calendar.DAY_OF_YEAR,diff );
        } else if( timePane.getSelectedIndex() == 1 ) {
            cal.add( java.util.Calendar.WEEK_OF_YEAR,diff );
        } else {
            cal.add( java.util.Calendar.MONTH,diff );
        }

        //

        fieldDate.setValue( new Timestamp( cal.getTimeInMillis()));
        displayCalendar();
    }    // adjustDate

    /**
     * Descripción de Método
     *
     *
     * @param assignment
     */

    public void mAssignmentCallback( MResourceAssignment assignment ) {
        m_mAssignment = assignment;

        if( m_createNew ) {
            dispose();
        } else {
            displayCalendar();
        }
    }    // mAssignmentCallback

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MResourceAssignment getMResourceAssignment() {
        return m_mAssignment;
    }    // getMResourceAssignment
}    // InfoSchedule



/*
 *  @(#)InfoSchedule.java   02.07.07
 * 
 *  Fin del fichero InfoSchedule.java
 *  
 *  Versión 2.2
 *
 */
