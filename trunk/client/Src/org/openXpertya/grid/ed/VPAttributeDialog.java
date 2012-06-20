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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CEditor;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.search.PAttributeInstance;
import org.openXpertya.model.MAttribute;
import org.openXpertya.model.MAttributeInstance;
import org.openXpertya.model.MAttributeSet;
import org.openXpertya.model.MAttributeSetInstance;
import org.openXpertya.model.MAttributeValue;
import org.openXpertya.model.MQuery;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
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

public class VPAttributeDialog extends CDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param M_AttributeSetInstance_ID
     * @param M_Product_ID
     * @param C_BPartner_ID
     * @param productWindow
     */

    public VPAttributeDialog( Frame frame,int M_AttributeSetInstance_ID,int M_Product_ID,int C_BPartner_ID,boolean productWindow,int srcWindowNo,int srcTabNo) {
        super( frame,Msg.translate( Env.getCtx(),"M_AttributeSetInstance_ID" ),true );
        log.config( "M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID + ", M_Product_ID=" + M_Product_ID + ", C_BPartner_ID=" + C_BPartner_ID + ", ProductW=" + productWindow );
        m_WindowNo                  = Env.createWindowNo( this );
        m_M_AttributeSetInstance_ID = M_AttributeSetInstance_ID;
        m_M_Product_ID              = M_Product_ID;
        m_C_BPartner_ID             = C_BPartner_ID;
        m_productWindow             = productWindow;
        m_srcWindowNo = srcWindowNo;
        m_srcTabNo = srcTabNo;
        
        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VPAttributeDialog" + ex );
        }

        // Dynamic Init

        if( !initAttributes()) {
            dispose();

            return;
        }

        AEnv.showCenterWindow( frame,this );
    }    // VPAttributeDialog

    /** Descripción de Campos */

    private int m_WindowNo;

    /** */
    
    private int m_srcWindowNo;
    
    /** */
    
    private int m_srcTabNo;
    
    /** Descripción de Campos */

    private MAttributeSetInstance m_masi;

    /** Descripción de Campos */

    private int m_M_AttributeSetInstance_ID;

    /** Descripción de Campos */

    private String m_M_AttributeSetInstanceName;

    /** Descripción de Campos */

    private int m_M_Product_ID;

    /** Descripción de Campos */

    private int m_C_BPartner_ID;

    /** Descripción de Campos */

    private boolean m_productWindow = false;

    /** Descripción de Campos */

    private boolean m_changed = false;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private int m_row = 0;

    /** Descripción de Campos */

    private Map<Integer, JComponent> m_editors = new HashMap<Integer, JComponent>();

    /** Descripción de Campos */

    private static final int INSTANCE_VALUE_LENGTH = 40;

    /** Descripción de Campos */

    private CCheckBox cbNewEdit = new CCheckBox();

    /** Descripción de Campos */

    private CButton bSelect = new CButton( Env.getImageIcon( "PAttribute16.gif" ));

    // Lot

    /** Descripción de Campos */

    private VString fieldLotString = new VString( "Lot",false,false,true,20,20,null,null );

    /** Descripción de Campos */

    private CComboBox fieldLot = null;

    /** Descripción de Campos */

    private CButton bLot = new CButton( Msg.getMsg( Env.getCtx(),"New" ));

    // Lot Popup

    /** Descripción de Campos */

    JPopupMenu popupMenu = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem mZoom;

    // Ser No

    /** Descripción de Campos */

    private VString fieldSerNo = new VString( "SerNo",false,false,true,20,20,null,null );

    /** Descripción de Campos */

    private CButton bSerNo = new CButton( Msg.getMsg( Env.getCtx(),"New" ));

    // Date

    /** Descripción de Campos */

    private VDate fieldGuaranteeDate = new VDate( "GuaranteeDate",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"GuaranteeDate" ));

    /** Descripción de Campos */

    private VDate fieldDueDate = new VDate( "DueDate",true,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DueDate" ));

    //

    /** Descripción de Campos */

    private CTextField fieldDescription = new CTextField( 20 );

    //

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private ALayout centerLayout = new ALayout( 5,5,true );

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.getContentPane().setLayout( mainLayout );
        this.getContentPane().add( centerPanel,BorderLayout.CENTER );
        this.getContentPane().add( confirmPanel,BorderLayout.SOUTH );
        centerPanel.setLayout( centerLayout );

        //

        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean initAttributes() {
        if( m_M_Product_ID == 0 ) {
            return false;
        }

        if (m_M_AttributeSetInstance_ID == 0 && !allowNewRecord()) {
       		if (!allowExistingRecords()) {
       			checkAllowNewRecord();
       			return false;
       		}
       		
       		cmd_select();
       		// dispose();
       		return false;
        }

        if (m_M_AttributeSetInstance_ID != 0 && !allowExistingRecords()) {
        	if (allowNewRecord()) {
        		m_M_AttributeSetInstance_ID = 0;
        		checkAllowExistingRecords();
        	} else if (isNewRecordMandatory()) {
       			m_M_AttributeSetInstance_ID = 0;
       			checkNewRecordMandatory();
        	} else {
        		// Allow New = False
        		// New Mandatory = False
        		return false;
        	}
        }

        // Get Model

        m_masi = MAttributeSetInstance.get( Env.getCtx(),m_M_AttributeSetInstance_ID,m_M_Product_ID );

        if( m_masi == null ) {
            log.severe( "No Model for M_AttributeSetInstance_ID=" + m_M_AttributeSetInstance_ID + ", M_Product_ID=" + m_M_Product_ID );

            return false;
        }

        Env.setContext( Env.getCtx(),m_WindowNo,"M_AttributeSet_ID",m_masi.getM_AttributeSet_ID());

        // Get Attribute Set

        MAttributeSet as = m_masi.getMAttributeSet();

        // Product has no Attribute Set

        if( as == null ) {
            ADialog.error( m_WindowNo,this,"PAttributeNoAttributeSet" );

            return false;
        }

        // Product has no Instance Attributes

        //if( !m_productWindow &&!as.isInstanceAttribute()) {
        //    ADialog.error( m_WindowNo,this,"PAttributeNoInstanceAttribute" );
        //
        //    return false;
        //}

        // Show Product Attributes

        if( m_productWindow ) {
            MAttribute[] attributes = as.getMAttributes( false );

            log.fine( "Product Attributes=" + attributes.length );

            for( int i = 0;i < attributes.length;i++ ) {
                addAttributeLine( attributes[ i ],true,!m_productWindow );
            }
        } else                                          // Set Instance Attributes
        {

            // New/Edit - Selection

            if( m_M_AttributeSetInstance_ID == 0 ) {    // new
                cbNewEdit.setText( Msg.getMsg( Env.getCtx(),"NewRecord" ));
                
//                if (!this.allowNewRecord()) {
//                	cbNewEdit.setReadWrite(false);
//                	cbNewEdit.setSelected(false);
//                }
            } else {
                cbNewEdit.setText( Msg.getMsg( Env.getCtx(),"EditRecord" ));
                
//                if (!this.allowExistingRecords()) {
//                	cbNewEdit.setReadWrite(false);
//                	cbNewEdit.setSelected(false);
//                }
            }

            if (!this.allowNewRecord()) {
            	cbNewEdit.setReadWrite(false);
            	cbNewEdit.setSelected(this.allowExistingRecords());
            } else if (!this.allowExistingRecords()) {
            	cbNewEdit.setReadWrite(false);
            	cbNewEdit.setSelected(this.allowNewRecord());
            }
            
            cbNewEdit.addActionListener( this );
            centerPanel.add( cbNewEdit,new ALayoutConstraint( m_row++,0 ));
            bSelect.setText( Msg.getMsg( Env.getCtx(),"SelectExisting" ));
            bSelect.addActionListener( this );
            bSelect.setEnabled(allowExistingRecords());
            centerPanel.add( bSelect,null );

            // All Attributes

            MAttribute[] attributes = as.getMAttributes( true );

            log.fine( "Instance Attributes=" + attributes.length );

            for( int i = 0;i < attributes.length;i++ ) {
                addAttributeLine( attributes[ i ],false,false );
            }
        }

        // Lot

        if( !m_productWindow && as.isLot()) {
            CLabel label = new CLabel( Msg.translate( Env.getCtx(),"Lot" ));

            label.setLabelFor( fieldLotString );
            centerPanel.add( label,new ALayoutConstraint( m_row++,0 ));
            centerPanel.add( fieldLotString,null );
            fieldLotString.setText( m_masi.getLot());

            // M_Lot_ID
            // int AD_Column_ID = 9771;        //      M_AttributeSetInstance.M_Lot_ID
            // fieldLot = new VLookup ("M_Lot_ID", false,false, true,
            // MLookupFactory.get(Env.getCtx(), m_WindowNo, 0, AD_Column_ID, DisplayType.TableDir));

            String sql = "SELECT M_Lot_ID, Name " + "FROM M_Lot l " + "WHERE EXISTS (SELECT M_Product_ID FROM M_Product p " + "WHERE p.M_AttributeSet_ID=" + m_masi.getM_AttributeSet_ID() + " AND p.M_Product_ID=l.M_Product_ID)";

            fieldLot = new CComboBox( DB.getKeyNamePairs( sql,true ));
            label    = new CLabel( Msg.translate( Env.getCtx(),"M_Lot_ID" ));
            label.setLabelFor( fieldLot );
            centerPanel.add( label,new ALayoutConstraint( m_row++,0 ));
            centerPanel.add( fieldLot,null );

            if( m_masi.getM_Lot_ID() != 0 ) {
                for( int i = 1;i < fieldLot.getItemCount();i++ ) {
                    KeyNamePair pp = ( KeyNamePair )fieldLot.getItemAt( i );

                    if( pp.getKey() == m_masi.getM_Lot_ID()) {
                        fieldLot.setSelectedIndex( i );
                        fieldLotString.setEditable( false );

                        break;
                    }
                }
            }

            fieldLot.addActionListener( this );

            // New Lot Button

            if( m_masi.getMAttributeSet().getM_LotCtl_ID() != 0 ) {
                centerPanel.add( bLot,null );
                bLot.addActionListener( this );
            }

            // Popup

            fieldLot.addMouseListener( new VPAttributeDialog_mouseAdapter( this ));    // popup
            mZoom = new JMenuItem( Msg.getMsg( Env.getCtx(),"Zoom" ),Env.getImageIcon( "Zoom16.gif" ));
            mZoom.addActionListener( this );
            popupMenu.add( mZoom );
        }    // Lot

        // SerNo

        if( !m_productWindow && as.isSerNo()) {
            CLabel label = new CLabel( Msg.translate( Env.getCtx(),"SerNo" ));

            label.setLabelFor( fieldSerNo );
            fieldSerNo.setText( m_masi.getSerNo());
            centerPanel.add( label,new ALayoutConstraint( m_row++,0 ));
            centerPanel.add( fieldSerNo,null );

            // New SerNo Button

            if( m_masi.getMAttributeSet().getM_SerNoCtl_ID() != 0 ) {
                centerPanel.add( bSerNo,null );
                bSerNo.addActionListener( this );
            }
        }    // SerNo

        // GuaranteeDate

        if( !m_productWindow && as.isGuaranteeDate()) {
            CLabel label = new CLabel( Msg.translate( Env.getCtx(),"GuaranteeDate" ));

            label.setLabelFor( fieldGuaranteeDate );

            if( m_M_AttributeSetInstance_ID == 0 ) {
                fieldGuaranteeDate.setValue( m_masi.getGuaranteeDate( true ));
            } else {
                fieldGuaranteeDate.setValue( m_masi.getGuaranteeDate());
            }

            centerPanel.add( label,new ALayoutConstraint( m_row++,0 ));
            centerPanel.add( fieldGuaranteeDate,null );
        }    // GuaranteeDate

        // Due Date
        
        if (as.isDueDate()) {
        	CLabel label = new CLabel(Msg.translate(Env.getCtx(), "DueDate"));
        	
        	label.setLabelFor(fieldDueDate);
        	
        	if (m_M_AttributeSetInstance_ID == 0)
        		fieldDueDate.setValue( m_masi.getDueDate( true ));
        	else
        		fieldDueDate.setValue( m_masi.getDueDate());
        	
        	centerPanel.add( label,new ALayoutConstraint( m_row++,0 ));
            centerPanel.add( fieldDueDate,null );
        }
        
        if( m_row == 0 ) {
            ADialog.error( m_WindowNo,this,"PAttributeNoInfo" );

            return false;
        }

        // New/Edit Window

        if( !m_productWindow ) {
            cbNewEdit.setSelected( m_M_AttributeSetInstance_ID == 0 );
            cmd_newEdit();
        }

        // Attrribute Set Instance Description

        CLabel label = new CLabel( Msg.translate( Env.getCtx(),"Description" ));

        label.setLabelFor( fieldDescription );
        fieldDescription.setText( m_masi.getDescription());
        fieldDescription.setEditable( false );
        centerPanel.add( label,new ALayoutConstraint( m_row++,0 ));
        centerPanel.add( fieldDescription,null );

        // Window usually to wide (??)

        Dimension dd = centerPanel.getPreferredSize();

        dd.width = Math.min( 500,dd.width );
        centerPanel.setPreferredSize( dd );

        return true;
    }    // initAttribute

    /**
     * Descripción de Método
     *
     *
     * @param attribute
     * @param product
     * @param readOnly
     */

    private void addAttributeLine( MAttribute attribute,boolean product,boolean readOnly ) {
        log.fine( "Attribute=" + attribute.getName() + ", Product=" + product + ", R/O=" + readOnly );

        CLabel label = new CLabel( attribute.getName());

        if( product ) {
            label.setFont( new Font( label.getFont().getFontName(),Font.BOLD,label.getFont().getSize()));
        }

        if( attribute.getDescription() != null ) {
            label.setToolTipText( attribute.getDescription());
        }

        centerPanel.add( label,new ALayoutConstraint( m_row++,0 ));

        //
        readOnly = readOnly || attribute.isReadOnly();

        MAttributeInstance instance = attribute.getMAttributeInstance( m_M_AttributeSetInstance_ID );

        if( MAttribute.ATTRIBUTEVALUETYPE_List.equals( attribute.getAttributeValueType())) {
            MAttributeValue[] values = attribute.getMAttributeValues();    // optional = null
            CComboBox editor = new CComboBox( values );
            boolean   found  = false;

            if( instance != null ) {
                for( int i = 0;i < values.length;i++ ) {
                    if( (values[ i ] != null) && (values[ i ].getM_AttributeValue_ID() == instance.getM_AttributeValue_ID())) {
                        editor.setSelectedIndex( i );
                        found = true;

                        break;
                    }
                }

                if( found ) {
                    log.fine( "addAttributeLine - Attribute=" + attribute.getName() + " #" + values.length + " - found: " + instance );
                } else {
                    log.warning( "addAttributeLine - Attribute=" + attribute.getName() + " #" + values.length + " - NOT found: " + instance );
                }
            }    // setComboBox
                    else {
                log.fine( "addAttributeLine - Attribute=" + attribute.getName() + " #" + values.length + " no instance" );
            }

            label.setLabelFor( editor );
            centerPanel.add( editor,null );

            if( readOnly ) {
                editor.setEnabled( false );
            } else {
                m_editors.put(attribute.getID(), editor);
            }
        } else if( MAttribute.ATTRIBUTEVALUETYPE_Number.equals( attribute.getAttributeValueType())) {
            VNumber editor = new VNumber( attribute.getName(),attribute.isMandatory(),false,true,DisplayType.Number,Msg.translate( Env.getCtx(),"ValueNumber" ));

            if( instance != null ) {
                editor.setValue( instance.getValueNumber());
            } else {
                editor.setValue( Env.ZERO );
            }

            label.setLabelFor( editor );
            centerPanel.add( editor,null );

            if( readOnly ) {
                editor.setEnabled( false );
            } else {
                m_editors.put(attribute.getID(), editor);
            }
        } else if( MAttribute.ATTRIBUTEVALUETYPE_StringMax40.equals( attribute.getAttributeValueType())) {
        	// Text Field
            VString editor = new VString( attribute.getName(),attribute.isMandatory(),false,true,20,INSTANCE_VALUE_LENGTH,null,null );

            if( instance != null ) {
                editor.setText( instance.getValue());
            }

            label.setLabelFor( editor );
            centerPanel.add( editor,null );

            if( readOnly ) {
                editor.setEnabled( false );
            } else {
            	m_editors.put(attribute.getID(), editor);
            }
        } else if( MAttribute.ATTRIBUTEVALUETYPE_Date.equals( attribute.getAttributeValueType())) {
        	// VDate
			VDate editor = new VDate(attribute.getName(),
					attribute.isMandatory(), readOnly, true, DisplayType.Date,
					null);

            if( instance != null ) {
                editor.setValue(instance.getValueDate());
            }

            label.setLabelFor( editor );
            centerPanel.add( editor,null );
            
            if( !readOnly ) {
            	m_editors.put(attribute.getID(), editor);
            }
        }

    }    // addAttributeLine

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        removeAll();
        Env.clearWinContext( m_WindowNo );
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // Select Instance

        if( e.getSource() == bSelect ) {
            if( cmd_select()) {
                dispose();
            }
        }

        // New/Edit

        else if( e.getSource() == cbNewEdit ) {
            cmd_newEdit();
        }

        // Select Lot from existing

        else if( e.getSource() == fieldLot ) {
            KeyNamePair pp = ( KeyNamePair )fieldLot.getSelectedItem();

            if( (pp != null) && (pp.getKey() != -1) ) {
                fieldLotString.setText( pp.getName());
                fieldLotString.setEditable( false );
                m_masi.setM_Lot_ID( pp.getKey());
            } else {
                fieldLotString.setEditable( true );
                m_masi.setM_Lot_ID( 0 );
            }
        }

        // Create New Lot

        else if( e.getSource() == bLot ) {
            KeyNamePair pp = m_masi.createLot( m_M_Product_ID );

            if( pp != null ) {
                fieldLot.addItem( pp );
                fieldLot.setSelectedItem( pp );
            }
        }

        // Create New SerNo

        else if( e.getSource() == bSerNo ) {
            fieldSerNo.setText( m_masi.getSerNo( true ));
        }

        // OK

        else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            if( saveSelection()) {
                dispose();
            }
        }

        // Cancel

        else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
        	checkMasiMandatory(true);
            m_changed = false;
            dispose();
        }

        // Zoom M_Lot

        else if( e.getSource() == mZoom ) {
            cmd_zoom();
        } else {
            log.log( Level.SEVERE,"not found - " + e );
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean cmd_select() {
    	if (!checkAllowExistingRecords())
    		return false;
    	
        log.config( "select" );

        int M_Warehouse_ID = Env.getContextAsInt( Env.getCtx(),"M_Warehouse_ID" );
        String title = "";

        // Get Text

        String sql = "SELECT p.Name, w.Name FROM M_Product p, M_Warehouse w " + "WHERE p.M_Product_ID=? AND w.M_Warehouse_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_M_Product_ID );
            pstmt.setInt( 2,M_Warehouse_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                title = rs.getString( 1 ) + " - " + rs.getString( 2 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"cmd_select",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        PAttributeInstance pai = new PAttributeInstance( this,title,M_Warehouse_ID,0,m_M_Product_ID,m_C_BPartner_ID,m_srcWindowNo );

        if( pai.getM_AttributeSetInstance_ID() != -1 ) {
            m_M_AttributeSetInstance_ID  = pai.getM_AttributeSetInstance_ID();
            m_M_AttributeSetInstanceName = pai.getM_AttributeSetInstanceName();
            m_changed                    = true;

            return true;
        }

        return false;
    }    // cmd_select

    /**
     * Descripción de Método
     *
     */

    private void cmd_newEdit() {
        boolean rw = cbNewEdit.isSelected();

//        if (!checkAllowNewRecord())
//        	return;
        
        log.config( "R/W=" + rw + " " + m_masi );

        //

        fieldLotString.setEditable( rw && (m_masi.getM_Lot_ID() == 0) );

        if( fieldLot != null ) {
            fieldLot.setReadWrite( rw );
        }

        bLot.setReadWrite( rw );
        fieldSerNo.setReadWrite( rw );
        bSerNo.setReadWrite( rw );
        fieldGuaranteeDate.setReadWrite( rw );
        fieldDueDate.setReadWrite( rw );
        
        //
        Set<Integer> keySet = m_editors.keySet();
        for (Integer attributeID : keySet) {
        	CEditor editor = ( CEditor )m_editors.get( attributeID );
            editor.setReadWrite( rw );
		}
    }    // cmd_newEdit

    /**
     * Descripción de Método
     *
     */

    private void cmd_zoom() {
        int         M_Lot_ID = 0;
        KeyNamePair pp       = ( KeyNamePair )fieldLot.getSelectedItem();

        if( pp != null ) {
            M_Lot_ID = pp.getKey();
        }

        MQuery zoomQuery = new MQuery( "M_Lot" );

        zoomQuery.addRestriction( "M_Lot_ID",MQuery.EQUAL,M_Lot_ID );
        log.info( "cmd_zoom - " + zoomQuery );

        //

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        //

        int     AD_Window_ID = 257;    // Lot
        AWindow frame        = new AWindow();

        if( frame.initWindow( AD_Window_ID,zoomQuery )) {
            this.setVisible(false);
            this.setModal( false );    // otherwise blocked
            this.setVisible(true);
            AEnv.showScreen( frame,SwingConstants.EAST );
        }

        // async window - not able to get feedback

        frame = null;

        //

        setCursor( Cursor.getDefaultCursor());
    }    // cmd_zoom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean saveSelection() {
        log.info( "saveSelection" );

        
        MAttributeSet as = m_masi.getMAttributeSet();

        if( as == null ) {
            return true;
        }

        //

        m_changed = false;

        String mandatory = "";

        if( !m_productWindow && as.isLot()) {
            log.fine( "saveSelection - Lot=" + fieldLotString.getText());

            String text = fieldLotString.getText();

            m_masi.setLot( text );

            if( as.isLotMandatory() && ( (text == null) || (text.length() == 0) ) ) {
                mandatory += " - " + Msg.translate( Env.getCtx(),"Lot" );
            }

            m_changed = true;
        }    // Lot

        if( !m_productWindow && as.isSerNo()) {
            log.fine( "saveSelection - SerNo=" + fieldSerNo.getText());

            String text = fieldSerNo.getText();

            m_masi.setSerNo( text );

            if( as.isSerNoMandatory() && ( (text == null) || (text.length() == 0) ) ) {
                mandatory += " - " + Msg.translate( Env.getCtx(),"SerNo" );
            }

            m_changed = true;
        }    // SerNo

        if( !m_productWindow && as.isGuaranteeDate()) {
            log.fine( "saveSelection - GuaranteeDate=" + fieldGuaranteeDate.getValue());

            Timestamp ts = ( Timestamp )fieldGuaranteeDate.getValue();

            m_masi.setGuaranteeDate( ts );

            if( as.isGuaranteeDateMandatory() && (ts == null) ) {
                mandatory += " - " + Msg.translate( Env.getCtx(),"GuaranteeDate" );
            }

            m_changed = true;
        }    // GuaranteeDate

        if (as.isDueDate()) {
        	Timestamp ts = fieldDueDate.getTimestamp();
        	m_masi.setDueDate(ts);
        	
        	// Always Mandatory 
        	
        	if (ts == null)
        		mandatory += " - " + Msg.translate(Env.getCtx(),"DueDate");
        	
        	m_changed = true;
        }
        
        // ***     Save Attributes ***
        // New Instance

        if( m_changed || (m_masi.getM_AttributeSetInstance_ID() == 0) ) {
            m_masi.save();
            m_M_AttributeSetInstance_ID = m_masi.getM_AttributeSetInstance_ID();
            m_M_AttributeSetInstanceName = m_masi.getDescription();
        }

        // Save Instance Attributes

        MAttribute[] attributes = as.getMAttributes( !m_productWindow );

        for( int i = 0;i < attributes.length;i++ ) {
        	if(m_editors.get(attributes[i].getID()) != null){
                if( MAttribute.ATTRIBUTEVALUETYPE_List.equals( attributes[ i ].getAttributeValueType())) {
                    CComboBox       editor = ( CComboBox )m_editors.get(attributes[i].getID());
                    MAttributeValue value  = ( MAttributeValue )editor.getSelectedItem();

                    log.fine( "saveSelection - " + attributes[ i ].getName() + "=" + value );

                    if( attributes[ i ].isMandatory() && (value == null) ) {
                        mandatory += " - " + attributes[ i ].getName();
                    }

                    attributes[ i ].setMAttributeInstance( m_M_AttributeSetInstance_ID,value );
                } else if( MAttribute.ATTRIBUTEVALUETYPE_Number.equals( attributes[ i ].getAttributeValueType())) {
                    VNumber    editor = ( VNumber )m_editors.get(attributes[i].getID());
                    BigDecimal value  = ( BigDecimal )editor.getValue();

                    log.fine( "saveSelection - " + attributes[ i ].getName() + "=" + value );

                    if( attributes[ i ].isMandatory() && (value == null) ) {
                        mandatory += " - " + attributes[ i ].getName();
                    }

                    attributes[ i ].setMAttributeInstance( m_M_AttributeSetInstance_ID,value );
                } else if( MAttribute.ATTRIBUTEVALUETYPE_StringMax40.equals( attributes[ i ].getAttributeValueType())) {
                    VString editor = ( VString )m_editors.get(attributes[i].getID());
                    String  value  = editor.getText();

                    log.fine( "saveSelection - " + attributes[ i ].getName() + "=" + value );

                    if( attributes[ i ].isMandatory() && ( (value == null) || (value.length() == 0) ) ) {
                        mandatory += " - " + attributes[ i ].getName();
                    }

                    attributes[ i ].setMAttributeInstance( m_M_AttributeSetInstance_ID,value );
                } else if( MAttribute.ATTRIBUTEVALUETYPE_Date.equals( attributes[ i ].getAttributeValueType())) {
                	VDate editor = ( VDate )m_editors.get(attributes[i].getID());
                    Timestamp  value  = (Timestamp)editor.getValue();

                    if( attributes[ i ].isMandatory() && (value == null) ) {
                        mandatory += " - " + attributes[ i ].getName();
                    }

                    attributes[ i ].setMAttributeInstance( m_M_AttributeSetInstance_ID,value );
                }
                m_changed = true;
        	}
        }    // for all attributes

        // Save Model

        if( m_changed ) {
            m_masi.setDescription();
            m_masi.save();
        }

        m_M_AttributeSetInstance_ID  = m_masi.getM_AttributeSetInstance_ID();
        m_M_AttributeSetInstanceName = m_masi.getDescription();

        //

        if( mandatory.length() > 0 ) {
            ADialog.error( m_WindowNo,this,"FillMandatory",mandatory );

            return false;
        }

        return true;
    }    // saveSelection

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_AttributeSetInstance_ID() {
        return m_M_AttributeSetInstance_ID;
    }    // getM_AttributeSetInstance_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getM_AttributeSetInstanceName() {
        return m_M_AttributeSetInstanceName;
    }    // getM_AttributeSetInstanceName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isChanged() {
        return m_changed;
    }    // isChanged
    
    private boolean checkAllowNewRecord() {
    	if (!allowNewRecord()) { // "No se permite crear nuevos registros."
        	JOptionPane.showMessageDialog(this, Msg.translate(Env.getCtx(), "CannotCreateNewRecord"), "Error", JOptionPane.ERROR_MESSAGE);
        	return false;
        }
    	
    	return true;
    }
    
    private boolean checkNewRecordMandatory() {
//    	if (m_M_AttributeSetInstance_ID != 0 && !checkAllowExistingRecords())
//    		return false;
    	
    	if (isNewRecordMandatory()) { // "Debe crear un nuevo registro."
    		JOptionPane.showMessageDialog(this, Msg.translate(Env.getCtx(), "MustCreateNewRecord"), "Error", JOptionPane.ERROR_MESSAGE);
        	return false;
    	}
    	
    	return true;
    }
    
    private boolean checkAllowExistingRecords() {
    	if (!allowExistingRecords()) { // "No se permite seleccionar registros ya existentes."
    		JOptionPane.showMessageDialog(this, Msg.translate(Env.getCtx(), "CannotPickExistingRecord"), "Error", JOptionPane.ERROR_MESSAGE);
        	return false;
    	}
    	
    	return true;
    }
    
    private boolean checkMasiMandatory(boolean cancel) { // "Deberá especificar una instancia de conjunto de atributos obligatoriamente."
    	if (cancel && isAtributeSetInstenceMandatory() && m_M_AttributeSetInstance_ID == 0) {
    		JOptionPane.showMessageDialog(this, Msg.translate(Env.getCtx(), "MustSetAttrSetInstance"), "Error", JOptionPane.ERROR_MESSAGE);
        	return false;
    	}
    	
    	return true;
    }
    
    private MAttributeSet.CondicionesCasos getCondicionesCasos() {
    	return MAttributeSet.GetCondicionesAtributosByWindowNo(m_srcWindowNo, m_srcTabNo);
    }
    
    // Devolvera true si podemos crear nuevas instancias de atributos.
    public boolean allowNewRecord() {
    	return getCondicionesCasos().isAllowNewRecord();
    }
    
    // Devolvera trus si podemos seleccionar registros existentes.
    public boolean allowExistingRecords() {
    	return getCondicionesCasos().isAllowExistingRecords();
    }
    
    // Devolvera true si es obligatorio crear un nuevo registros.
    public boolean isNewRecordMandatory() {
    	return getCondicionesCasos().isNewRecordMandatory();
    }
    
    // Devolver� true si se debe indicar instancia del conjunto de atributos obligatoriamente.
    public boolean isAtributeSetInstenceMandatory() {
    	return getCondicionesCasos().isAtributeSetInstenceMandatory();
    }
    
//    public boolean isSoTrx() {
//    	return "Y".equals(Env.getContext(Env.getCtx(), m_srcWindowNo, "IsSOTrx", true));
//    }
    
//    public int getSourceWindowID() {
//    	return Env.getContextAsInt(Env.getCtx(), m_srcWindowNo, "AD_Window_ID ");
//    }
    
}    // VPAttributeDialog


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

final class VPAttributeDialog_mouseAdapter extends java.awt.event.MouseAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    VPAttributeDialog_mouseAdapter( VPAttributeDialog adaptee ) {
        this.adaptee = adaptee;
    }    // VPAttributeDialog_mouseAdapter

    /** Descripción de Campos */

    private VPAttributeDialog adaptee;

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {

        // System.out.println("mouseClicked " + e.getID() + " " + e.getSource().getClass().toString());
        // popup menu

        if( SwingUtilities.isRightMouseButton( e )) {
            adaptee.popupMenu.show(( Component )e.getSource(),e.getX(),e.getY());
        }
    }    // mouse Clicked
}    // VPAttributeDialog_mouseAdapter



/*
 *  @(#)VPAttributeDialog.java   02.07.07
 * 
 *  Fin del fichero VPAttributeDialog.java
 *  
 *  Versión 2.2
 *
 */
