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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AWindow;
import org.openXpertya.model.MLocatorLookup;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VLocator extends JComponent implements VEditor,ActionListener, FocusListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VLocator() {
        this( "M_Locator_ID",false,false,true,null,0 );
        addFocusListener(this);
    }    // VLocator

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param mLocator
     * @param WindowNo
     */

    public VLocator( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,MLocatorLookup mLocator,int WindowNo ) {
        super();
        super.setName( columnName );
        addFocusListener(this);
        m_columnName = columnName;
        m_mLocator   = mLocator;
        m_WindowNo   = WindowNo;

        //

        LookAndFeel.installBorder( this,"TextField.border" );
        this.setLayout( new BorderLayout());

        // Size

        this.setPreferredSize( m_text.getPreferredSize());    // causes r/o to be the same length

        int height = m_text.getPreferredSize().height;

        // ***     Button & Text   ***

        m_text.setBorder( null );
        m_text.setEditable( true );
        m_text.setFocusable( true );
        m_text.addMouseListener( new VLocator_mouseAdapter( this ));    // popup
        m_text.setFont( CompierePLAF.getFont_Field());
        m_text.setForeground( CompierePLAF.getTextColor_Normal());
        m_text.addActionListener( this );
        m_text.addFocusListener(this);
        this.add( m_text,BorderLayout.CENTER );
        m_button.setIcon( new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/Locator10.gif" )));
        m_button.setMargin( new Insets( 0,0,0,0 ));
        m_button.setPreferredSize( new Dimension( height,height ));
        m_button.addActionListener( this );
        this.add( m_button,BorderLayout.EAST );

        // Prefereed Size

        this.setPreferredSize( this.getPreferredSize());    // causes r/o to be the same length

        // ReadWrite

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }

        setMandatory( mandatory );

        //

        mZoom = new JMenuItem( Msg.getMsg( Env.getCtx(),"Zoom" ),Env.getImageIcon( "Zoom16.gif" ));
        mZoom.addActionListener( this );
        popupMenu.add( mZoom );
        mRefresh = new JMenuItem( Msg.getMsg( Env.getCtx(),"Refresh" ),Env.getImageIcon( "Refresh16.gif" ));
        mRefresh.addActionListener( this );
        popupMenu.add( mRefresh );
    }    // VLocator

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_text     = null;
        m_button   = null;
        m_mLocator = null;
    }    // dispose

    private boolean showingDialog = false;
    
    /** Descripción de Campos */

    private JTextField m_text = new JTextField( VLookup.DISPLAY_LENGTH );

    /** Descripción de Campos */

    private CButton m_button = new CButton();

    /** Descripción de Campos */

    private MLocatorLookup m_mLocator;

    /** Descripción de Campos */

    private Object m_value;

    //

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VLocator.class );

    // Popup

    /** Descripción de Campos */

    JPopupMenu popupMenu = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem mZoom;

    /** Descripción de Campos */

    private JMenuItem mRefresh;
    
    private String m_lastDisplay = null;

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setReadWrite( boolean value ) {
        m_button.setReadWrite( value );

        if( m_button.isVisible() != value ) {
            m_button.setVisible( value );
        }

        setBackground( false );
    }    // setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadWrite() {
        return m_button.isReadWrite();
    }    // isReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
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
        return m_button.isMandatory();
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param color
     */

    public void setBackground( Color color ) {
        if( !color.equals( m_text.getBackground())) {
            m_text.setBackground( color );
        }
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setBackground( boolean error ) {
        if( error ) {
            setBackground( CompierePLAF.getFieldBackground_Error());
        } else if( !isReadWrite()) {
            setBackground( CompierePLAF.getFieldBackground_Inactive());
        } else if( isMandatory()) {
            setBackground( CompierePLAF.getFieldBackground_Mandatory());
        } else {
            setBackground( CompierePLAF.getFieldBackground_Normal());
        }
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param fg
     */

    public void setForeground( Color fg ) {
        m_text.setForeground( fg );
    }    // setForeground

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        setValue( value,false );
    }    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param fire
     */

    private void setValue( Object value,boolean fire ) {
        if( value != null ) {
            m_mLocator.setOnly_Warehouse_ID( getOnly_Warehouse_ID());

            if( !m_mLocator.isValid( value )) {
                value = null;
            }
        }

        //

        m_value = value;
        m_text.setText( m_mLocator.getDisplay( value ));    // loads value
        m_lastDisplay = m_text.getText();
        
        // Data Binding

        try {
            fireVetoableChange( m_columnName,null,value );
        } catch( PropertyVetoException pve ) {
        }
    }    // setValue

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

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        if( getM_Locator_ID() == 0 ) {
            return null;
        }

        return m_value;
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_Locator_ID() {
        if( (m_value != null) && (m_value instanceof Integer) ) {
            return(( Integer )m_value ).intValue();
        }

        return 0;
    }    // getM_Locator_ID

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
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // Refresh

        if( e.getSource() == mRefresh ) {
            m_mLocator.refresh();

            return;
        }

        // Zoom to M_Warehouse

        if( e.getSource() == mZoom ) {
            actionZoom();

            return;
        }

        // Warehouse

        int only_Warehouse_ID = getOnly_Warehouse_ID();

        log.config( "Only Wharehouse_ID=" + only_Warehouse_ID );

        // Text Entry ok

        if( (e.getSource() == m_text) && actionText( only_Warehouse_ID )) {
            return;
        }

        // Button - Start Dialog

        setShowingDialog(true);
        
        int M_Locator_ID = 0;

        if( m_value instanceof Integer ) {
            M_Locator_ID = (( Integer )m_value ).intValue();
        }

        //

        m_mLocator.setOnly_Warehouse_ID( only_Warehouse_ID );

        VLocatorDialog ld = new VLocatorDialog( Env.getFrame( this ),Msg.translate( Env.getCtx(),m_columnName ),m_mLocator,M_Locator_ID,isMandatory(),only_Warehouse_ID );

        // display

        ld.setVisible( true );
        m_mLocator.setOnly_Warehouse_ID( 0 );

        // redisplay

        if( !ld.isChanged()) {
        	setValue(null, true);
        	//return;
        }

        setShowingDialog(false);

        
        setValue( ld.getValue(),true );
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param only_Warehouse_ID
     *
     * @return
     */

    private boolean actionText( int only_Warehouse_ID ) {
        String text = m_text.getText();

        log.fine( text );

        if( (text == null) || (text.length() == 0) ) {
            if( isMandatory()) {
                return false;
            } else {
                setValue( null,true );

                return true;
            }
        }

        if( text.endsWith( "%" )) {
            text = text.toUpperCase();
        } else {
            text = text.toUpperCase() + "%";
        }

        // Look up

        int    M_Locator_ID = 0;
        String sql          = "SELECT M_Locator_ID FROM M_Locator WHERE UPPER(Value) LIKE " + DB.TO_STRING( text );

        if( only_Warehouse_ID != 0 ) {
            sql += " AND M_Warehouse_ID=?";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            if( only_Warehouse_ID != 0 ) {
                pstmt.setInt( 1,only_Warehouse_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                M_Locator_ID = rs.getInt( 1 );

                if( rs.next()) {
                    M_Locator_ID = 0;    // more than one
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"actionText",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        if( M_Locator_ID == 0 ) {
            return false;
        }

        setValue( new Integer( M_Locator_ID ),true );

        return true;
    }    // actionText

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addActionListener( ActionListener listener ) {
        m_text.addActionListener( listener );
    }    // addActionListener

    /**
     * Descripción de Método
     *
     */

    private void actionZoom() {
        int AD_Window_ID = 139;    // hardcoded

        log.info( "" );

        //

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,null )) {
            return;
        }

        AEnv.showCenterScreen( frame );
        frame = null;
        setCursor( Cursor.getDefaultCursor());
    }    // actionZoom

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( org.openXpertya.model.MField mField ) {}    // setField

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getOnly_Warehouse_ID() {
    	String only_Warehouse = null;
    	// Si la columna es una ubicación destino verifica si existe en el contexto
    	// un almacén destino (M_WarehouseTo_ID). Si no existe, sigue el curso normal
    	// buscando M_Warehouse_ID.
    	if (m_columnName.equalsIgnoreCase("M_Locator_To_ID")) {
    		only_Warehouse = Env.getContext( Env.getCtx(),m_WindowNo,"M_WarehouseTo_ID",true );
    	}
    	if (only_Warehouse == null || only_Warehouse.length() == 0) {
    		only_Warehouse = Env.getContext( Env.getCtx(),m_WindowNo,"M_Warehouse_ID",true );
    	}
        int only_Warehouse_ID = 0;

        try {
            if( (only_Warehouse != null) && (only_Warehouse.length() > 0) ) {
                only_Warehouse_ID = Integer.parseInt( only_Warehouse );
            }
        } catch( Exception ex ) {
        }

        return only_Warehouse_ID;
    }    // getOnly_Warehouse_ID

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) {

        if( e.isTemporary() || (m_mLocator == null) ||!m_button.isEnabled()) {
            return;
        }
		
		if (!isShowingDialog()) { 
			int M_Locator_ID = 0;
	        int only_Warehouse_ID = getOnly_Warehouse_ID();
	
	        if( (e.getSource() == m_text) && actionText( only_Warehouse_ID )) {
	            return;
	        }
            
	        if(e.getSource() == m_text && m_lastDisplay.equals( m_text.getText() )) {
                return;
            }
	     
	        setShowingDialog(true);
	        
	        if( m_value instanceof Integer ) {
	            M_Locator_ID = (( Integer )m_value ).intValue();
	        }
	
	        //
	
	        m_mLocator.setOnly_Warehouse_ID( only_Warehouse_ID );
	
	        VLocatorDialog ld = new VLocatorDialog( Env.getFrame( this ),Msg.translate( Env.getCtx(),m_columnName ),m_mLocator,M_Locator_ID,isMandatory(),only_Warehouse_ID );
	
	        // display
	
	        ld.setVisible( true );
	        m_mLocator.setOnly_Warehouse_ID( 0 );
	
	        // redisplay
	
	        if( !ld.isChanged()) {
	        	setValue(null, true);
	        	//return;
	        }
	
	        setShowingDialog(false);
	        
	        setValue( ld.getValue(),true );
		
	        
		}
	}


    private synchronized void setShowingDialog(boolean value) {
    	this.showingDialog = value;
    }
    
    private boolean isShowingDialog() {
    	return showingDialog;
    }
	
	
}    // VLocator


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VLocator_mouseAdapter extends java.awt.event.MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VLocator_mouseAdapter( VLocator adaptee ) {
        this.adaptee = adaptee;
    }    // VLookup_mouseAdapter

    /** Descripción de Campos */

    private VLocator adaptee;

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {

        // popup menu

        if( SwingUtilities.isRightMouseButton( e )) {
            adaptee.popupMenu.show(( Component )e.getSource(),e.getX(),e.getY());
        }
    }    // mouse Clicked
    
}    // VLocator_mouseAdapter



/*
 *  @(#)VLocator.java   02.07.07
 * 
 *  Fin del fichero VLocator.java
 *  
 *  Versión 2.2
 *
 */
