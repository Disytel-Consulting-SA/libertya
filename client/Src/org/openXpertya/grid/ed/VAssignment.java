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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.openXpertya.apps.search.InfoSchedule;
import org.openXpertya.model.MField;
import org.openXpertya.model.MResourceAssignment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VAssignment extends JComponent implements VEditor,ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VAssignment() {
        this( false,false,true );
    }    // VAssigment

    /**
     * Constructor de la clase ...
     *
     *
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     */

    public VAssignment( boolean mandatory,boolean isReadOnly,boolean isUpdateable ) {
        super();

        // super.setName(columnName);

        LookAndFeel.installBorder( this,"TextField.border" );
        this.setLayout( new BorderLayout());

        // Size

        this.setPreferredSize( m_text.getPreferredSize());

        int height = m_text.getPreferredSize().height;

        // ***     Text    ***

        m_text.setEditable( false );
        m_text.setFocusable( false );
        m_text.setBorder( null );
        m_text.setHorizontalAlignment( JTextField.LEADING );

        // Background

        setMandatory( mandatory );
        this.add( m_text,BorderLayout.CENTER );

        // ***     Button  ***

        m_button.setIcon( Env.getImageIcon( "Assignment10.gif" ));
        m_button.setMargin( new Insets( 0,0,0,0 ));
        m_button.setPreferredSize( new Dimension( height,height ));
        m_button.addActionListener( this );
        m_button.setFocusable( true );
        this.add( m_button,BorderLayout.EAST );

        // Prefereed Size

        this.setPreferredSize( this.getPreferredSize());    // causes r/o to be the same length

        // ReadWrite

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }

        // Popup

        m_text.addMouseListener( new VAssignment_mouseAdapter( this ));
        menuEditor = new JMenuItem( Msg.getMsg( Env.getCtx(),"InfoResource" ),Env.getImageIcon( "Zoom16.gif" ));
        menuEditor.addActionListener( this );
        popupMenu.add( menuEditor );
    }    // VAssignment

    /** Descripción de Campos */

    private Object m_value = null;

    /** Descripción de Campos */

    private PreparedStatement m_pstmt = null;

    /** Descripción de Campos */

    private JTextField m_text = new JTextField( VLookup.DISPLAY_LENGTH );

    /** Descripción de Campos */

    private CButton m_button = new CButton();

    /** Descripción de Campos */

    JPopupMenu popupMenu = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem menuEditor;

    /** Descripción de Campos */

    private boolean m_readWrite;

    /** Descripción de Campos */

    private boolean m_mandatory;

    /** Descripción de Campos */

    private DateFormat m_dateFormat = DisplayType.getDateFormat( DisplayType.DateTime );

    /** Descripción de Campos */

    private NumberFormat m_qtyFormat = DisplayType.getNumberFormat( DisplayType.Quantity );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VAssignment.class );

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        try {
            if( m_pstmt != null ) {
                m_pstmt.close();
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VAssignment.dispose" );
        }

        m_text   = null;
        m_button = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
        m_mandatory = mandatory;
        m_button.setMandatory( mandatory );
        setBackground( false );
    }    // setMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return m_mandatory;
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param rw
     */

    public void setReadWrite( boolean rw ) {
        m_readWrite = rw;
        m_button.setReadWrite( rw );
        setBackground( false );
    }    // setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadWrite() {
        return m_readWrite;
    }    // isReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param color
     */

    public void setForeground( Color color ) {
        m_text.setForeground( color );
    }    // SetForeground

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setBackground( boolean error ) {
        if( error ) {
            setBackground( CompierePLAF.getFieldBackground_Error());
        } else if( !m_readWrite ) {
            setBackground( CompierePLAF.getFieldBackground_Inactive());
        } else if( m_mandatory ) {
            setBackground( CompierePLAF.getFieldBackground_Mandatory());
        } else {
            setBackground( CompierePLAF.getInfoBackground());
        }
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param color
     */

    public void setBackground( Color color ) {
        m_text.setBackground( color );
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        if( value == m_value ) {
            return;
        }

        m_value = value;

        int S_ResourceAssignment_ID = 0;

        if( (m_value != null) && (m_value instanceof Integer) ) {
            S_ResourceAssignment_ID = (( Integer )m_value ).intValue();
        }

        // Set Empty

        if( S_ResourceAssignment_ID == 0 ) {
            m_text.setText( "" );

            return;
        }

        // Statement

        if( m_pstmt == null ) {
            m_pstmt = DB.prepareStatement( "SELECT r.Name,ra.AssignDateFrom,ra.Qty,uom.UOMSymbol " + "FROM S_ResourceAssignment ra, S_Resource r, S_ResourceType rt, C_UOM uom " + "WHERE ra.S_ResourceAssignment_ID=?" + " AND ra.S_Resource_ID=r.S_Resource_ID" + " AND r.S_ResourceType_ID=rt.S_ResourceType_ID" + " and rt.C_UOM_ID=uom.C_UOM_ID" );
        }

        //

        try {
            m_pstmt.setInt( 1,S_ResourceAssignment_ID );

            ResultSet rs = m_pstmt.executeQuery();

            if( rs.next()) {
                StringBuffer sb = new StringBuffer( rs.getString( 1 ));

                sb.append( " " ).append( m_dateFormat.format( rs.getTimestamp( 2 ))).append( " " ).append( m_qtyFormat.format( rs.getBigDecimal( 3 ))).append( " " ).append( rs.getString( 4 ).trim());
                m_text.setText( sb.toString());
            } else {
                m_text.setText( "<" + S_ResourceAssignment_ID + ">" );
            }

            rs.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        return m_value;
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        return m_text.getText();
    }    // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( MField mField ) {}    // setField

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addActionListener( ActionListener listener ) {

        // m_text.addActionListener(listener);

    }    // addActionListener

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( !m_button.isEnabled()) {
            return;
        }

        m_button.setEnabled( false );

        //

        Integer             oldValue                = ( Integer )getValue();
        int                 S_ResourceAssignment_ID = (oldValue == null)
                ?0
                :oldValue.intValue();
        MResourceAssignment ma                      = new MResourceAssignment( Env.getCtx(),S_ResourceAssignment_ID,null );

        // Start VAssignment Dialog

        if( S_ResourceAssignment_ID != 0 ) {
            VAssignmentDialog vad = new VAssignmentDialog( Env.getFrame( this ),ma,true,true );

            ma = vad.getMResourceAssignment();
        }

        // Start InfoSchedule directly

        else {
            InfoSchedule is = new InfoSchedule( Env.getFrame( this ),ma,true );

            ma = is.getMResourceAssignment();
        }

        // Set Value

        if( (ma != null) && (ma.getS_ResourceAssignment_ID() != 0) ) {
            setValue( new Integer( ma.getS_ResourceAssignment_ID()));

            try {
                fireVetoableChange( "S_ResourceAssignment_ID",new Object(),getValue());
            } catch( PropertyVetoException pve ) {
                log.log( Level.SEVERE,"",pve );
            }
        }

        m_button.setEnabled( true );
        requestFocus();
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param evt
     */

    public void propertyChange( PropertyChangeEvent evt ) {
        if( evt.getPropertyName().equals( org.openXpertya.model.MField.PROPERTY )) {
            setValue( evt.getNewValue());
        }
    }    // propertyChange
}    // VAssignment


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VAssignment_mouseAdapter extends MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VAssignment_mouseAdapter( VAssignment adaptee ) {
        this.adaptee = adaptee;
    }    // VAssignment_mouseAdapter

    /** Descripción de Campos */

    private VAssignment adaptee;

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {

        // Double Click

        if( e.getClickCount() > 1 ) {
            adaptee.actionPerformed( new ActionEvent( e.getSource(),e.getID(),"Mouse" ));
        }

        // popup menu

        if( SwingUtilities.isRightMouseButton( e )) {
            adaptee.popupMenu.show(( Component )e.getSource(),e.getX(),e.getY());
        }
    }    // mouse Clicked
}    // VAssignment_mouseAdapter



/*
 *  @(#)VAssignment.java   02.07.07
 * 
 *  Fin del fichero VAssignment.java
 *  
 *  Versión 2.2
 *
 */
