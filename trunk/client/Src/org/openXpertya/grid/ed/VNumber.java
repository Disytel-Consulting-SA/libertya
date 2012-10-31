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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.text.Document;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AEnv;
import org.openXpertya.model.MField;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VNumber extends JComponent implements VEditor,ActionListener,KeyListener,FocusListener {

    /** Descripción de Campos */

    public final static int SIZE = 12;

    /**
     * Constructor de la clase ...
     *
     */

    public VNumber() {
        this( "Number",false,false,true,DisplayType.Number,"Number" );
    }    // VNumber

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param displayType
     * @param title
     */

    public VNumber( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,int displayType,String title ) {
        super();
        super.setName( columnName );
        m_columnName = columnName;
        m_title      = title;
        setDisplayType( displayType );

        //

        LookAndFeel.installBorder( this,"TextField.border" );
        this.setLayout( new BorderLayout());

//              this.setPreferredSize(m_text.getPreferredSize());               //      causes r/o to be the same length
//              int height = m_text.getPreferredSize().height;
//              setMinimumSize(new Dimension (30,height));

        // ***     Text    ***

        m_text.setBorder( null );
        m_text.setHorizontalAlignment( JTextField.TRAILING );
        m_text.addKeyListener( this );
        m_text.addFocusListener( this );

        // Background

        setMandatory( mandatory );
        this.add( m_text,BorderLayout.CENTER );

        // ***     Button  ***

        m_button.setIcon( Env.getImageIcon( "Calculator10.gif" ));
        m_button.setMargin( new Insets( 0,0,0,0 ));
        m_button.setFocusable( false );
        m_button.addActionListener( this );
        this.add( m_button,BorderLayout.EAST );

        // Prefereed Size

        this.setPreferredSize( this.getPreferredSize());    // causes r/o to be the same length

        // Size

        setColumns( SIZE,CComboBox.FIELD_HIGHT - 4 );

        // ReadWrite

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }
    }    // VNumber

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_text   = null;
        m_button = null;
        m_mField = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param doc
     */

    protected void setDocument( Document doc ) {
        m_text.setDocument( doc );
    }    // getDocument

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    protected int m_displayType;    // Currency / UoM via Context

    /** Descripción de Campos */

    private DecimalFormat m_format;

    /** Descripción de Campos */

    private String m_title;

    /** Descripción de Campos */

    private boolean m_setting;

    /** Descripción de Campos */

    private String m_oldText;

    /** Descripción de Campos */

    private String m_initialText;

    /** Descripción de Campos */

    private boolean m_rangeSet = false;

    /** Descripción de Campos */

    private Double m_minValue;

    /** Descripción de Campos */

    private Double m_maxValue;

    /** Descripción de Campos */

    private boolean m_modified = false;

    /** Descripción de Campos */

    private CTextField m_text = new CTextField( SIZE );    // Standard

    /** Descripción de Campos */

    private CButton m_button = new CButton();

    /** Descripción de Campos */

    private MField m_mField = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VNumber.class );

    /**
     * Descripción de Método
     *
     *
     * @param columns
     * @param height
     */

    public void setColumns( int columns,int height ) {
        m_text.setPreferredSize( null );
        m_text.setColumns( columns );

        Dimension size = m_text.getPreferredSize();

        if( height > size.height ) {    // default 16
            size.height = height;
        }

        if( CComboBox.FIELD_HIGHT - 4 > size.height ) {
            size.height = VLookup.FIELD_HIGHT - 4;
        }

        this.setPreferredSize( size );    // causes r/o to be the same length
        this.setMinimumSize( new Dimension( columns * 10,size.height ));
        m_button.setPreferredSize( new Dimension( size.height,size.height ));
    }                                     // setColumns

    /**
     * Descripción de Método
     *
     *
     * @param minValue
     * @param maxValue
     *
     * @return
     */

    public boolean setRange( Double minValue,Double maxValue ) {
        m_rangeSet = true;
        m_minValue = minValue;
        m_maxValue = maxValue;

        return m_rangeSet;
    }    // setRange

    /**
     * Descripción de Método
     *
     *
     * @param minValue
     * @param maxValue
     *
     * @return
     */

    public boolean setRange( String minValue,String maxValue ) {
        if( (minValue == null) || (maxValue == null) ) {
            return false;
        }

        try {
            m_minValue = Double.valueOf( minValue );
            m_maxValue = Double.valueOf( maxValue );
        } catch( NumberFormatException nfe ) {
            return false;
        }

        m_rangeSet = true;

        return m_rangeSet;
    }    // setRange

    /**
     * Descripción de Método
     *
     *
     * @param displayType
     */

    public void setDisplayType( int displayType ) {
        m_displayType = displayType;

        if( !DisplayType.isNumeric( displayType )) {
            m_displayType = DisplayType.Number;
        }

        m_format = DisplayType.getNumberFormat( displayType );
        m_text.setDocument( new MDocNumber( displayType,m_format,m_text,m_title ));
    }    // setDisplayType

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setReadWrite( boolean value ) {
        if( m_text.isReadWrite() != value ) {
            m_text.setReadWrite( value );
        }

        if( m_button.isReadWrite() != value ) {
            m_button.setReadWrite( value );
        }

        // Don't show button if not ReadWrite

        if( m_button.isVisible() != value ) {
            m_button.setVisible( value );
        }
    }    // setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadWrite() {
        return m_text.isReadWrite();
    }    // isReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
        m_text.setMandatory( mandatory );
    }    // setMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return m_text.isMandatory();
    }    // isMandatory

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
     * @param error
     */

    public void setBackground( boolean error ) {
        m_text.setBackground( error );
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
        log.finest( "Value=" + value );
        
        if(m_rangeSet && !isInRange(value)){
        	return;
        }
        
        if( value == null || value.equals("") ) {
            m_oldText = "";
        } else {
            m_oldText = m_format.format( value );
        }

        // only set when not updated here

        if( m_setting ) {
            return;
        }

        m_text.setText( m_oldText );
        m_initialText = m_oldText;
        m_modified    = false;
    }    // setValue

    
    public boolean isInRange(Object value){
    	if(value == null || value.toString().length() == 0){
    		return true;
    	}
    	String strValue = value.toString();
    	if(strValue.indexOf(",") >= 0 && strValue.indexOf(".") < 0){
    		strValue = strValue.replace(",", ".");
    	}
    	
    	
    	try {
    		strValue = getFormatValue(strValue);
            value = getParseValue(strValue);
        } catch( ParseException e ) {
            log.log( Level.SEVERE,"VNumber.isInRange() Parse error "+strValue,e );
            return true;
        }

    	double valueDouble = 0;
        if( m_displayType == DisplayType.Integer ) {
        	Integer intValue = (Integer)value;
        	valueDouble = intValue.doubleValue();
        }
        else{
        	BigDecimal bigValue = (BigDecimal)value;
        	valueDouble = bigValue.doubleValue();
        }
    	return valueDouble >= m_minValue && valueDouble <= m_maxValue;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param evt
     */

    public void propertyChange( PropertyChangeEvent evt ) {
    	if( evt.getPropertyName().equals( org.openXpertya.model.MField.PROPERTY )) {
        	//if(evt.getOldValue() != null && !evt.getOldValue().equals(evt.getNewValue()))
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
        if( (m_text == null) || (m_text.getText() == null) || (m_text.getText().length() == 0) ) {
            return null;
        }

        String value = m_text.getText();

        // return 0 if text deleted

        if( (value == null) || (value.length() == 0) ) {
            if( !m_modified ) {
                return null;
            }

            if( m_displayType == DisplayType.Integer ) {
                return new Integer( 0 );
            }

            return Env.ZERO;
        }

        if( value.equals( "." ) || value.equals( "," ) || value.equals( "-" )) {
            value = "0";
        }

        try {
            return getParseValue(value);
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getValue",e );
        }

        if( m_displayType == DisplayType.Integer ) {
            return new Integer( 0 );
        }

        return Env.ZERO;
    }    // getValue    
    
    public Object getParseValue(String value) throws ParseException {
    	Number number = m_format.parse( value );

        value = number.toString();    // converts it to US w/o thousands

        BigDecimal bd = new BigDecimal( value );

        if( m_displayType == DisplayType.Integer ) {
            return new Integer( bd.intValue());
        }

        return bd.setScale( m_format.getMaximumFractionDigits(),BigDecimal.ROUND_HALF_UP );
    }
   
    
    public String getFormatValue(Object value){
    	String formattedValue = value.toString();
    	if(value != null && !value.equals("")){
    		try {
    			Number number = new BigDecimal(value.toString());
    			formattedValue = m_format.format( number );
			} catch (Exception e) {
				log.severe("Error formatting value "+value);
			}
    	}
    	return formattedValue;
    }
    
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
     * @return
     */

    public String getTitle() {
        return m_title;
    }    // getTitle

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object plus() {
        Object value = getValue();

        if( value == null ) {
            if( m_displayType == DisplayType.Integer ) {
                value = new Integer( 0 );
            } else {
                value = Env.ZERO;
            }
        }

        // Add

        if( value instanceof BigDecimal ) {
            value = (( BigDecimal )value ).add( Env.ONE );
        } else {
            value = new Integer((( Integer )value ).intValue() + 1 );
        }

        //

        setValue( value );

        return value;
    }    // plus

    /**
     * Descripción de Método
     *
     *
     * @param minimum
     *
     * @return
     */

    public Object minus( int minimum ) {
        Object value = getValue();

        if( value == null ) {
            if( m_displayType == DisplayType.Integer ) {
                value = new Integer( minimum );
            } else {
                value = new BigDecimal( minimum );
            }

            setValue( value );

            return value;
        }

        // Subtract

        if( value instanceof BigDecimal ) {
            BigDecimal bd  = (( BigDecimal )value ).subtract( Env.ONE );
            BigDecimal min = new BigDecimal( minimum );

            if( bd.compareTo( min ) < 0 ) {
                value = min;
            } else {
                value = bd;
            }
        } else {
            int i = (( Integer )value ).intValue();

            i--;

            if( i < minimum ) {
                i = minimum;
            }

            value = new Integer( i );
        }

        //

        setValue( value );

        return value;
    }    // minus

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.config( e.getActionCommand());

        if( ValuePreference.NAME.equals( e.getActionCommand())) {
            if( MRole.getDefault().isShowPreference()) {
                ValuePreference.start( m_mField,getValue());
            }

            return;
        }

        if( e.getSource() == m_button ) {
        	openCalculator();
        }
    }    // actionPerformed

    private void openCalculator() {
        m_button.setEnabled( false );

        String str = startCalculator( this,m_text.getText(),m_format,m_displayType,m_title );

        if(m_rangeSet && !isInRange(str)){
        	str = m_text.getText();
        }
        
        m_text.setText( str );
        m_button.setEnabled( true );

        try {
            fireVetoableChange( m_columnName,m_oldText,getValue());
        } catch( PropertyVetoException pve ) {
        }

        m_text.requestFocus();
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyTyped( KeyEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyPressed( KeyEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyReleased( KeyEvent e ) {
        log.finest( "Key=" + e.getKeyCode());

        if(m_rangeSet && !isInRange(m_text.getText())){
        	e.consume();
        	m_text.setText( m_oldText );
        }
        
        // ESC

        if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            m_text.setText( m_initialText );
        }       
        

        // Disytel - Deprecado actualmente.
        // En algunos casos no era deseable que la tecla ENTER abra la calculadora.
        // Ej. TPV. Se debe reveer esta funcionalidad para adaptarla correctamente.
        //if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
        //    openCalculator();
        //}

        m_modified = true;
        m_setting  = true;

        try {
            if( e.getKeyCode() == KeyEvent.VK_ENTER )    // 10
            {
                fireVetoableChange( m_columnName,m_oldText,getValue());
                fireActionPerformed();
            } else {                                     // indicate change
                if(!m_text.getText().equals(m_oldText)) {
                	fireVetoableChange( m_columnName,m_oldText, getValue());
                	m_oldText = m_text.getText();
                }
            }
        } catch( PropertyVetoException pve ) {
        }

        m_setting = false;
    }    // keyReleased

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusGained( FocusEvent e ) {
        if( m_text != null ) {
            m_text.selectAll();
        }
    }    // focusGained

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusLost( FocusEvent e ) {
        try {
            if(m_text != null && !m_text.getText().equals(m_oldText))
            	fireVetoableChange( m_columnName,m_initialText,getValue());
            fireActionPerformed();
        } catch( PropertyVetoException pve ) {
        }
    }    // focusLost

    /**
     * Descripción de Método
     *
     *
     * @param jc
     * @param value
     * @param format
     * @param displayType
     * @param title
     *
     * @return
     */

    public static String startCalculator( Container jc,String value,DecimalFormat format,int displayType,String title ) {
        log.config( "Value=" + value );

        BigDecimal startValue = new BigDecimal( 0.0 );

        try {
            if( (value != null) && (value.length() > 0) ) {
                Number number = format.parse( value );

                startValue = new BigDecimal( number.toString());
            }
        } catch( ParseException pe ) {
            log.info( "InvalidEntry - " + pe.getMessage());
        }

        // Find frame

        Frame frame = Env.getFrame( jc );

        // Actual Call

        Calculator calc = new Calculator( frame,title,displayType,format,startValue );

        AEnv.showCenterWindow( frame,calc );

        BigDecimal result = calc.getNumber();

        log.config( "Result=" + result );

        //

        calc = null;

        if( result != null ) {
            return format.format( result );
        } else {
            return value;    // original value
        }
    }                        // startCalculator

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( MField mField ) {
        m_mField = mField;
    }    // setField

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public void removeActionListener( ActionListener l ) {
        listenerList.remove( ActionListener.class,l );
    }    // removeActionListener

    /**
     * Descripción de Método
     *
     *
     * @param l
     */

    public void addActionListener( ActionListener l ) {
        listenerList.add( ActionListener.class,l );
    }    // addActionListener

    /**
     * Descripción de Método
     *
     */

    protected void fireActionPerformed() {
        int      modifiers    = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();

        if( currentEvent instanceof InputEvent ) {
            modifiers = (( InputEvent )currentEvent ).getModifiers();
        } else if( currentEvent instanceof ActionEvent ) {
            modifiers = (( ActionEvent )currentEvent ).getModifiers();
        }

        ActionEvent ae = new ActionEvent( this,ActionEvent.ACTION_PERFORMED,"VNumber",EventQueue.getMostRecentEventTime(),modifiers );

        // Guaranteed to return a non-null array

        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying those that are interested in this event

        for( int i = listeners.length - 2;i >= 0;i -= 2 ) {
            if( listeners[ i ] == ActionListener.class ) {
                (( ActionListener )listeners[ i + 1 ] ).actionPerformed( ae );
            }
        }
    }    // fireActionPerformed

    public void addAction(String actionName, KeyStroke keyStroke, Action action) {
    	m_text.getInputMap().put(keyStroke, actionName);
    	m_text.getActionMap().put(actionName, action);
    }

	@Override
	public void requestFocus() {
		m_text.requestFocus();
	}
	
	public JTextField getTextField() {
		return m_text;
	}

    /*  */

}    // VNumber



/*
 *  @(#)VNumber.java   02.07.07
 * 
 *  Fin del fichero VNumber.java
 *  
 *  Versión 2.2
 *
 */
