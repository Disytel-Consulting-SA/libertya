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
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.search.PAttributeInstance;
import org.openXpertya.model.MAttributeSet;
import org.openXpertya.model.MAttributeSetInstance;
import org.openXpertya.model.MField;
import org.openXpertya.model.MPAttributeLookup;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VPAttribute extends JComponent implements VEditor,ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public VPAttribute() {
        this( false,false,true,0,null,0 );
    }    // VAssigment

    public VPAttribute( boolean mandatory,boolean isReadOnly,boolean isUpdateable,int WindowNo,MPAttributeLookup lookup ) {
    	this(mandatory, isReadOnly, isUpdateable, WindowNo, lookup, 0);
    }
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param WindowNo
     * @param lookup
     */

    public VPAttribute( boolean mandatory,boolean isReadOnly,boolean isUpdateable,int WindowNo,MPAttributeLookup lookup, int tabNo ) {
        super.setName( "M_AttributeSetInstance_ID" );
        m_WindowNo      = WindowNo;
        m_TabNo = tabNo;
        m_mPAttribute   = lookup;
        m_C_BPartner_ID = Env.getContextAsInt( Env.getCtx(),WindowNo,"C_BPartner_ID" );
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

        m_button.setIcon( Env.getImageIcon( "PAttribute10.gif" ));
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

        m_text.addMouseListener( new VPAttribute_mouseAdapter( this ));
        menuEditor = new JMenuItem( Msg.getMsg( Env.getCtx(),"PAttribute" ),Env.getImageIcon( "Zoom16.gif" ));
        menuEditor.addActionListener( this );
        popupMenu.add( menuEditor );
    }    // VPAttribute

    /**
     * Constructor de la clase ...
     *
     *
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param WindowNo
     * @param lookup
     * @param columnName
     */

    public VPAttribute( boolean mandatory,boolean isReadOnly,boolean isUpdateable,int WindowNo,MPAttributeLookup lookup, int tabNo,String columnName ) {
        super.setName( "M_AttributeSetInstance_ID" );
        m_WindowNo      = WindowNo;
        m_TabNo = tabNo;
        m_mPAttribute   = lookup;
        m_C_BPartner_ID = Env.getContextAsInt( Env.getCtx(),WindowNo,"C_BPartner_ID" );
        this.columnName = columnName;        
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

        m_button.setIcon( Env.getImageIcon( "PAttribute10.gif" ));
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

        m_text.addMouseListener( new VPAttribute_mouseAdapter( this ));
        menuEditor = new JMenuItem( Msg.getMsg( Env.getCtx(),"PAttribute" ),Env.getImageIcon( "Zoom16.gif" ));
        menuEditor.addActionListener( this );
        popupMenu.add( menuEditor );
    }    // VPAttribute
    
    /** Descripción de Campos */

    private Object m_value = new Object();

    /** Descripción de Campos */

    private MPAttributeLookup m_mPAttribute;

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

    private int m_WindowNo;
    
    private String columnName;

    /** */
    
    private int m_TabNo;
    
    /** Descripción de Campos */

    private int m_C_BPartner_ID;

    /** Descripción de Campos */

    private int m_AD_Column_ID = 0;

    /** Descripción de Campos */

    private static Integer NO_INSTANCE = new Integer( 0 );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VPAttribute.class );

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_text   = null;
        m_button = null;
        m_mPAttribute.dispose();
        m_mPAttribute = null;
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
        if( (value == null) || NO_INSTANCE.equals( value )) {
            m_text.setText( "" );
            m_value = value;

            return;
        }

        // The same

        if( value.equals( m_value )) {
            return;
        }

        // new value

        log.fine( "Value=" + value );
        m_value = value;
        m_text.setText( m_mPAttribute.getDisplay( value ));    // loads value
    }                                                          // setValue

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

    public void setField( MField mField ) {

        // To determine behavior

        m_AD_Column_ID = mField.getAD_Column_ID();
    }    // setField

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addActionListener( ActionListener listener ) {}    // addActionListener

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

        Integer oldValue                  = ( Integer )getValue();
        int     M_AttributeSetInstance_ID = (oldValue == null)
                ?0
                :oldValue.intValue();
        String productColumnName = "M_Product_ID";

        if (columnName.equalsIgnoreCase("M_AttributeSetInstanceTo_ID")) {
            productColumnName = "M_Product_To_ID";
        }
        int M_Product_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,productColumnName );
        int M_ProductBOM_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"M_ProductBOM_ID" );

        log.config( "M_Product_ID=" + M_Product_ID + "/" + M_ProductBOM_ID + ",M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID + ", AD_Column_ID=" + m_AD_Column_ID );

        // M_Product.M_AttributeSetInstance_ID = 8418

        boolean productWindow = m_AD_Column_ID == 8418;    // HARDCODED

        // Instance Creation
        // M_InOutLine.M_AttributeSetInstance_ID = 8772
        // M_ProductionLine.M_AttributeSetInstance_ID = 8552

        boolean instanceCreation = true;

//                      m_AD_Column_ID == 8772 
//                      || m_AD_Column_ID == 8552;

        boolean changed = false;

        if( M_ProductBOM_ID != 0 ) {    // Use BOM Component
            M_Product_ID = M_ProductBOM_ID;
        }

        if( M_Product_ID == 0 ) {
            changed = true;
            m_text.setText( null );
            M_AttributeSetInstance_ID = 0;
            ADialog.error( m_WindowNo,this,"SelectProduct" );
        } else if( productWindow || instanceCreation ) {
            VPAttributeDialog vad = new VPAttributeDialog( Env.getFrame( this ),M_AttributeSetInstance_ID,M_Product_ID,m_C_BPartner_ID,productWindow,m_WindowNo,m_TabNo);

            if( vad.isChanged()) {
                m_text.setText( vad.getM_AttributeSetInstanceName());
                M_AttributeSetInstance_ID = vad.getM_AttributeSetInstance_ID();
                changed                   = true;
            }
        } else    // Selection
        {

            // Get Model

            MAttributeSetInstance masi = MAttributeSetInstance.get( Env.getCtx(),M_AttributeSetInstance_ID,M_Product_ID );

            if( masi == null ) {
                log.log( Level.SEVERE,"No Model for M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID + ", M_Product_ID=" + M_Product_ID );
            } else {
                Env.setContext( Env.getCtx(),m_WindowNo,"M_AttributeSet_ID",masi.getM_AttributeSet_ID());

                // Get Attribute Set

                MAttributeSet as = masi.getMAttributeSet();

                // Product has no Attribute Set

                if( as == null ) {
                    ADialog.error( m_WindowNo,this,"PAttributeNoAttributeSet" );

                    // Product has no Instance Attributes

               // } else if( !as.isInstanceAttribute()) {
               //     ADialog.error( m_WindowNo,this,"PAttributeNoInstanceAttribute" );
                } else {
                    int M_Warehouse_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"M_Warehouse_ID" );
                    int M_Locator_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"M_Locator_ID" );
                    String             title = "";
                    PAttributeInstance pai   = new PAttributeInstance( Env.getFrame( this ),title,M_Warehouse_ID,M_Locator_ID,M_Product_ID,m_C_BPartner_ID,m_WindowNo );

                    if( pai.getM_AttributeSetInstance_ID() != -1 ) {
                        m_text.setText( pai.getM_AttributeSetInstanceName());
                        M_AttributeSetInstance_ID = pai.getM_AttributeSetInstance_ID();
                        changed = true;
                    }
                }
            }
        }
 
        // Set Value

        if( changed ) {
            log.finest( "Changed M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID );
            m_value = new Object();    // force re-query display

            if( M_AttributeSetInstance_ID == 0 ) {
                setValue( null );
            } else {
                setValue( new Integer( M_AttributeSetInstance_ID ));
            }

            try {
            	//fireVetoableChange( "M_AttributeSetInstance_ID",null,getValue());
            	fireVetoableChange( columnName,null,getValue());
            } catch( PropertyVetoException pve ) {
                log.log( Level.SEVERE,"",pve );
            }
        }    // change

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
    
}    // VPAttribute


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VPAttribute_mouseAdapter extends MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VPAttribute_mouseAdapter( VPAttribute adaptee ) {
        this.adaptee = adaptee;
    }    // VPAttribute_mouseAdapter

    /** Descripción de Campos */

    private VPAttribute adaptee;

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
}    // VPAttribute_mouseAdapter



/*
 *  @(#)VPAttribute.java   02.07.07
 * 
 *  Fin del fichero VPAttribute.java
 *  
 *  Versión 2.2
 *
 */
