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



package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductBOM;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectLine;
import org.openXpertya.model.MRole;
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

public class VBOMDrop extends CPanel implements FormPanel,ActionListener {

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "VBOMDrop.init" );
        m_WindowNo = WindowNo;
        m_frame    = frame;

        try {

            // Top Selection Panel

            createSelectionPanel( true,true,true );
            m_frame.getContentPane().add( selectionPanel,BorderLayout.NORTH );

            // Center

            createMainPanel();

            CScrollPane scroll = new CScrollPane( this );

            m_frame.getContentPane().add( scroll,BorderLayout.CENTER );
            confirmPanel.addActionListener( this );

            // South

            m_frame.getContentPane().add( confirmPanel,BorderLayout.SOUTH );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VBOMDrop.init",e );
        }

        sizeIt();
    }    // init

    /**
     * Descripción de Método
     *
     */

    private void sizeIt() {

        // Frame

        m_frame.pack();

        Dimension size = m_frame.getPreferredSize();

        size.width = WINDOW_WIDTH;
        m_frame.setSize( size );
    }    // size

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
        removeAll();

        if( selectionPanel != null ) {
            selectionPanel.removeAll();
        }

        selectionPanel = null;

        if( m_selectionList != null ) {
            m_selectionList.clear();
        }

        m_selectionList = null;

        if( m_productList != null ) {
            m_productList.clear();
        }

        m_productList = null;

        if( m_qtyList != null ) {
            m_qtyList.clear();
        }

        m_qtyList = null;

        if( m_buttonGroups != null ) {
            m_buttonGroups.clear();
        }

        m_buttonGroups = null;
    }    // dispose

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private MProduct m_product;

    /** Descripción de Campos */

    private BigDecimal m_qty = Env.ONE;

    /** Descripción de Campos */

    private int m_bomLine = 0;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VBOMDrop.class );

    /** Descripción de Campos */

    private ArrayList m_selectionList = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_qtyList = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_productList = new ArrayList();

    /** Descripción de Campos */

    private HashMap m_buttonGroups = new HashMap();

    /** Descripción de Campos */

    private static final int WINDOW_WIDTH = 600;    // width of the window

    //

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private CPanel selectionPanel = new CPanel( new ALayout());

    /** Descripción de Campos */

    private CComboBox productField;

    /** Descripción de Campos */

    private VNumber productQty = new VNumber( "Qty",true,false,true,DisplayType.Quantity,Msg.translate( Env.getCtx(),"Qty" ));

    /** Descripción de Campos */

    private CComboBox orderField;

    /** Descripción de Campos */

    private CComboBox invoiceField;

    /** Descripción de Campos */

    private CComboBox projectField;

    /**
     * Descripción de Método
     *
     *
     * @param order
     * @param invoice
     * @param project
     */

    private void createSelectionPanel( boolean order,boolean invoice,boolean project ) {
        int row = 0;

        selectionPanel.setBorder( new TitledBorder( Msg.translate( Env.getCtx(),"Selection" )));
        productField = new CComboBox( getProducts());

        CLabel label = new CLabel( Msg.translate( Env.getCtx(),"M_Product_ID" ));

        label.setLabelFor( productField );
        selectionPanel.add( label,new ALayoutConstraint( row++,0 ));
        selectionPanel.add( productField );
        productField.addActionListener( this );

        // Qty

        label = new CLabel( productQty.getTitle());
        label.setLabelFor( productQty );
        selectionPanel.add( label );
        selectionPanel.add( productQty );
        productQty.setValue( Env.ONE );
        productQty.addActionListener( this );

        if( order ) {
            orderField = new CComboBox( getOrders());
            label      = new CLabel( Msg.translate( Env.getCtx(),"C_Order_ID" ));
            label.setLabelFor( orderField );
            selectionPanel.add( label,new ALayoutConstraint( row++,0 ));
            selectionPanel.add( orderField );
            orderField.addActionListener( this );
        }

        if( invoice ) {
            invoiceField = new CComboBox( getInvoices());
            label        = new CLabel( Msg.translate( Env.getCtx(),"C_Invoice_ID" ));
            label.setLabelFor( invoiceField );
            selectionPanel.add( label,new ALayoutConstraint( row++,0 ));
            selectionPanel.add( invoiceField );
            invoiceField.addActionListener( this );
        }

        if( project ) {
            projectField = new CComboBox( getProjects());
            label        = new CLabel( Msg.translate( Env.getCtx(),"C_Project_ID" ));
            label.setLabelFor( projectField );
            selectionPanel.add( label,new ALayoutConstraint( row++,0 ));
            selectionPanel.add( projectField );
            projectField.addActionListener( this );
        }

        // Enabled in ActionPerformed

        confirmPanel.getOKButton().setEnabled( false );

        // Size

        Dimension size = selectionPanel.getPreferredSize();

        size.width = WINDOW_WIDTH;
        selectionPanel.setPreferredSize( size );
    }    // createSelectionPanel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private KeyNamePair[] getProducts() {
        String sql = "SELECT M_Product_ID, Name " + "FROM M_Product " + "WHERE IsBOM='Y' AND IsVerified='Y' AND IsActive='Y' " + "ORDER BY Name";

        return DB.getKeyNamePairs( MRole.getDefault().addAccessSQL( sql,"M_Product",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO ),true );
    }    // getProducts

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private KeyNamePair[] getOrders() {
        String sql = "SELECT C_Order_ID, DocumentNo || '_' || GrandTotal " + "FROM C_Order " + "WHERE Processed='N' AND DocStatus='DR' " + "ORDER BY DocumentNo";

        return DB.getKeyNamePairs( MRole.getDefault().addAccessSQL( sql,"C_Order",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO ),true );
    }    // getOrders

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private KeyNamePair[] getProjects() {
        String sql = "SELECT C_Project_ID, Name " + "FROM C_Project " + "WHERE Processed='N' AND IsSummary='N' AND IsActive='Y'" + " AND ProjectCategory<>'S' " + "ORDER BY Name";

        return DB.getKeyNamePairs( MRole.getDefault().addAccessSQL( sql,"C_Project",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO ),true );
    }    // getProjects

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private KeyNamePair[] getInvoices() {
        String sql = "SELECT C_Invoice_ID, DocumentNo || '_' || GrandTotal " + "FROM C_Invoice " + "WHERE Processed='N' AND DocStatus='DR' " + "ORDER BY DocumentNo";

        return DB.getKeyNamePairs( MRole.getDefault().addAccessSQL( sql,"C_Invoice",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO ),true );
    }    // getInvoices

    /**
     * Descripción de Método
     *
     */

    private void createMainPanel() {
        log.config( ": " + m_product );
        this.removeAll();
        this.setPreferredSize( null );
        this.invalidate();
        this.setBorder( null );

        //

        m_selectionList.clear();
        m_productList.clear();
        m_qtyList.clear();
        m_buttonGroups.clear();

        //

        this.setLayout( new ALayout());

        String title = Msg.getMsg( Env.getCtx(),"SelectProduct" );

        if( (m_product != null) && (m_product.getID() > 0) ) {
            title = m_product.getName();

            if( (m_product.getDescription() != null) && (m_product.getDescription().length() > 0) ) {
                this.setToolTipText( m_product.getDescription());
            }

            m_bomLine = 0;
            addBOMLines( m_product,m_qty );
        }

        this.setBorder( new TitledBorder( title ));
    }    // createMainPanel

    /**
     * Descripción de Método
     *
     *
     * @param product
     * @param qty
     */

    private void addBOMLines( MProduct product,BigDecimal qty ) {
        MProductBOM[] bomLines = MProductBOM.getBOMLines( product );

        for( int i = 0;i < bomLines.length;i++ ) {
            addBOMLine( bomLines[ i ],qty );
        }

        log.fine( "VBOMDrop.addBOMLines #" + bomLines.length );
    }    // addBOMLines

    /**
     * Descripción de Método
     *
     *
     * @param line
     * @param qty
     */

    private void addBOMLine( MProductBOM line,BigDecimal qty ) {
        log.fine( line.toString());

        String bomType = line.getBOMType();

        if( bomType == null ) {
            bomType = MProductBOM.BOMTYPE_StandardPart;
        }

        //

        BigDecimal lineQty = line.getBOMQty().multiply( qty );
        MProduct   product = line.getProduct();

        if( product == null ) {
            return;
        }

        if( product.isBOM() && product.isVerified()) {
            addBOMLines( product,lineQty );    // recursive
        } else {
            addDisplay( line.getM_Product_ID(),product.getM_Product_ID(),bomType,product.getName(),lineQty );
        }
    }    // addBOMLine

    /**
     * Descripción de Método
     *
     *
     * @param parentM_Product_ID
     * @param M_Product_ID
     * @param bomType
     * @param name
     * @param lineQty
     */

    private void addDisplay( int parentM_Product_ID,int M_Product_ID,String bomType,String name,BigDecimal lineQty ) {
        log.fine( "M_Product_ID=" + M_Product_ID + ",Type=" + bomType + ",Name=" + name + ",Qty=" + lineQty );

        //

        boolean selected = true;

        if( MProductBOM.BOMTYPE_StandardPart.equals( bomType )) {
            JCheckBox cb = new JCheckBox();

            cb.setSelected( true );
            cb.setEnabled( false );

            // cb.addActionListener(this);             //      will not change

            m_selectionList.add( cb );
            this.add( cb,new ALayoutConstraint( m_bomLine++,0 ));
        } else if( MProductBOM.BOMTYPE_OptionalPart.equals( bomType )) {
            JCheckBox cb = new JCheckBox();

            cb.setSelected( false );
            selected = false;
            cb.addActionListener( this );
            m_selectionList.add( cb );
            this.add( cb,new ALayoutConstraint( m_bomLine++,0 ));
        } else {
            JRadioButton b         = new JRadioButton();
            String       groupName = String.valueOf( parentM_Product_ID ) + "_" + bomType;
            ButtonGroup group = ( ButtonGroup )m_buttonGroups.get( groupName );

            if( group == null ) {
                log.fine( "VBOMDrop.addDisplay - ButtonGroup=" + groupName );
                group = new ButtonGroup();
                m_buttonGroups.put( groupName,group );
                group.add( b );
                b.setSelected( true );    // select first one
            } else {
                group.add( b );
                b.setSelected( false );
                selected = false;
            }

            b.addActionListener( this );
            m_selectionList.add( b );
            this.add( b,new ALayoutConstraint( m_bomLine++,0 ));
        }

        // Add to List & display

        m_productList.add( new Integer( M_Product_ID ));

        VNumber qty = new VNumber( "Qty",true,false,true,DisplayType.Quantity,name );

        qty.setValue( lineQty );
        qty.setReadWrite( selected );
        m_qtyList.add( qty );

        CLabel label = new CLabel( name );

        label.setLabelFor( qty );
        this.add( label );
        this.add( qty );
    }    // addDisplay

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();

        if( size.width > WINDOW_WIDTH ) {
            size.width = WINDOW_WIDTH - 30;
        }

        return size;
    }    // getPreferredSize

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.config( e.getActionCommand());

        Object source = e.getSource();

        // Toggle Qty Enabled

        if( (source instanceof JCheckBox) || (source instanceof JRadioButton) ) {
            cmd_selection( source );

            // need to de-select the others in group

            if( source instanceof JRadioButton ) {

                // find Button Group

                Iterator it = m_buttonGroups.values().iterator();

                while( it.hasNext()) {
                    ButtonGroup group = ( ButtonGroup )it.next();
                    Enumeration en    = group.getElements();

                    while( en.hasMoreElements()) {

                        // We found the group

                        if( source == en.nextElement()) {
                            Enumeration info = group.getElements();

                            while( info.hasMoreElements()) {
                                Object infoObj = info.nextElement();

                                if( source != infoObj ) {
                                    cmd_selection( infoObj );
                                }
                            }
                        }
                    }
                }
            }
        }    // JCheckBox or JRadioButton

        // Product / Qty

        else if( (source == productField) || (source == productQty) ) {
            m_qty = ( BigDecimal )productQty.getValue();

            KeyNamePair pp = ( KeyNamePair )productField.getSelectedItem();

            m_product = MProduct.get( Env.getCtx(),pp.getKey());
            createMainPanel();
            sizeIt();
        }

        // Order

        else if( source == orderField ) {
            KeyNamePair pp    = ( KeyNamePair )orderField.getSelectedItem();
            boolean     valid = ( (pp != null) && (pp.getKey() > 0) );

            //

            if( invoiceField != null ) {
                invoiceField.setReadWrite( !valid );
            }

            if( projectField != null ) {
                projectField.setReadWrite( !valid );
            }
        }

        // Invoice

        else if( source == invoiceField ) {
            KeyNamePair pp    = ( KeyNamePair )invoiceField.getSelectedItem();
            boolean     valid = ( (pp != null) && (pp.getKey() > 0) );

            //

            if( orderField != null ) {
                orderField.setReadWrite( !valid );
            }

            if( projectField != null ) {
                projectField.setReadWrite( !valid );
            }
        }

        // Project

        else if( source == projectField ) {
            KeyNamePair pp    = ( KeyNamePair )projectField.getSelectedItem();
            boolean     valid = ( (pp != null) && (pp.getKey() > 0) );

            //

            if( orderField != null ) {
                orderField.setReadWrite( !valid );
            }

            if( invoiceField != null ) {
                invoiceField.setReadWrite( !valid );
            }
        }

        // OK

        else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            if( cmd_save()) {
                dispose();
            }
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        }

        // Enable OK

        boolean OK = m_product != null;

        if( OK ) {
            KeyNamePair pp = null;

            if( orderField != null ) {
                pp = ( KeyNamePair )orderField.getSelectedItem();
            }

            if( ( (pp == null) || (pp.getKey() <= 0) ) && (invoiceField != null) ) {
                pp = ( KeyNamePair )invoiceField.getSelectedItem();
            }

            if( ( (pp == null) || (pp.getKey() <= 0) ) && (projectField != null) ) {
                pp = ( KeyNamePair )projectField.getSelectedItem();
            }

            OK = ( (pp != null) && (pp.getKey() > 0) );
        }

        confirmPanel.getOKButton().setEnabled( OK );
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param source
     */

    private void cmd_selection( Object source ) {
        for( int i = 0;i < m_selectionList.size();i++ ) {
            if( source == m_selectionList.get( i )) {
                boolean selected = isSelectionSelected( source );
                VNumber qty      = ( VNumber )m_qtyList.get( i );

                qty.setReadWrite( selected );

                return;
            }
        }

        log.log( Level.SEVERE,"VBOMDrop.cmd_selection - not found - " + source );
    }    // cmd_selection

    /**
     * Descripción de Método
     *
     *
     * @param source
     *
     * @return
     */

    private boolean isSelectionSelected( Object source ) {
        boolean retValue = false;

        if( source instanceof JCheckBox ) {
            retValue = (( JCheckBox )source ).isSelected();
        } else if( source instanceof JRadioButton ) {
            retValue = (( JRadioButton )source ).isSelected();
        } else {
            log.log( Level.SEVERE,"VBOMDrop.isSelectionSelected - not valid - " + source );
        }

        return retValue;
    }    // isSelected

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean cmd_save() {
        KeyNamePair pp = ( KeyNamePair )orderField.getSelectedItem();

        if( (pp != null) && (pp.getKey() > 0) ) {
            return cmd_saveOrder( pp.getKey());
        }

        //

        pp = ( KeyNamePair )invoiceField.getSelectedItem();

        if( (pp != null) && (pp.getKey() > 0) ) {
            return cmd_saveInvoice( pp.getKey());
        }

        //

        pp = ( KeyNamePair )projectField.getSelectedItem();

        if( (pp != null) && (pp.getKey() > 0) ) {
            return cmd_saveProject( pp.getKey());
        }

        //

        log.log( Level.SEVERE,"cmd_save - nothing selected" );

        return false;
    }    // cmd_save

    /**
     * Descripción de Método
     *
     *
     * @param C_Order_ID
     *
     * @return
     */

    private boolean cmd_saveOrder( int C_Order_ID ) {
        log.config( "VBOMDrop.cmd_saveOrder - C_Order_ID=" + C_Order_ID );

        MOrder order = new MOrder( Env.getCtx(),C_Order_ID,null );

        if( order.getID() == 0 ) {
            log.log( Level.SEVERE,"VBOMDrop.cmd_saveOrder - Not found - C_Order_ID=" + C_Order_ID );

            return false;
        }

        int lineCount = 0;

        // for all bom lines

        for( int i = 0;i < m_selectionList.size();i++ ) {
            if( isSelectionSelected( m_selectionList.get( i ))) {
                BigDecimal qty = ( BigDecimal )(( VNumber )m_qtyList.get( i )).getValue();
                int M_Product_ID = (( Integer )m_productList.get( i )).intValue();

                // Create Line

                MOrderLine ol = new MOrderLine( order );

                ol.setM_Product_ID( M_Product_ID,true );
                ol.setQty( qty );
                ol.setPrice();
                ol.setTax();

                if( ol.save()) {
                    lineCount++;
                } else {
                    log.log( Level.SEVERE,"VBOMDrop.cmd_saveOrder - Line not saved" );
                }
            }    // line selected
        }        // for all bom lines

        log.config( "VBOMDrop.cmd_saveOrder - #" + lineCount );

        return true;
    }    // cmd_saveOrder

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     *
     * @return
     */

    private boolean cmd_saveInvoice( int C_Invoice_ID ) {
        log.config( "C_Invoice_ID=" + C_Invoice_ID );

        MInvoice invoice = new MInvoice( Env.getCtx(),C_Invoice_ID,null );

        if( invoice.getID() == 0 ) {
            log.log( Level.SEVERE,"Not found - C_Invoice_ID=" + C_Invoice_ID );

            return false;
        }

        int lineCount = 0;

        // for all bom lines

        for( int i = 0;i < m_selectionList.size();i++ ) {
            if( isSelectionSelected( m_selectionList.get( i ))) {
                BigDecimal qty = ( BigDecimal )(( VNumber )m_qtyList.get( i )).getValue();
                int M_Product_ID = (( Integer )m_productList.get( i )).intValue();

                // Create Line

                MInvoiceLine il = new MInvoiceLine( invoice );

                il.setM_Product_ID( M_Product_ID,true );
                il.setQty( qty );
                il.setPrice();
                il.setTax();

                if( il.save()) {
                    lineCount++;
                } else {
                    log.log( Level.SEVERE,"VBOMDrop.cmd_saveInvoice - Line not saved" );
                }
            }    // line selected
        }        // for all bom lines

        log.config( "VBOMDrop.cmd_saveInvoice - #" + lineCount );

        return true;
    }    // cmd_saveInvoice

    /**
     * Descripción de Método
     *
     *
     * @param C_Project_ID
     *
     * @return
     */

    private boolean cmd_saveProject( int C_Project_ID ) {
        log.config( "VBOMDrop.cmd_saveProject - C_Project_ID=" + C_Project_ID );

        MProject project = new MProject( Env.getCtx(),C_Project_ID,null );

        if( project.getID() == 0 ) {
            log.log( Level.SEVERE,"VBOMDrop.cmd_saveProject - Not found - C_Project_ID=" + C_Project_ID );

            return false;
        }

        int lineCount = 0;

        // for all bom lines

        for( int i = 0;i < m_selectionList.size();i++ ) {
            if( isSelectionSelected( m_selectionList.get( i ))) {
                BigDecimal qty = ( BigDecimal )(( VNumber )m_qtyList.get( i )).getValue();
                int M_Product_ID = (( Integer )m_productList.get( i )).intValue();

                // Create Line

                MProjectLine pl = new MProjectLine( project );

                pl.setM_Product_ID( M_Product_ID );
                pl.setPlannedQty( qty );

                // pl.setPlannedPrice();

                if( pl.save()) {
                    lineCount++;
                } else {
                    log.log( Level.SEVERE,"VBOMDrop.cmd_saveProject - Line not saved" );
                }
            }    // line selected
        }        // for all bom lines

        log.config( "VBOMDrop.cmd_saveProject - #" + lineCount );

        return true;
    }    // cmd_saveProject
}    // VBOMDrop



/*
 *  @(#)VBOMDrop.java   02.07.07
 * 
 *  Fin del fichero VBOMDrop.java
 *  
 *  Versión 2.2
 *
 */
