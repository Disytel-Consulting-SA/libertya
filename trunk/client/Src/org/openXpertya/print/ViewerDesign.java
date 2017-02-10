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



package org.openXpertya.print;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Login;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ViewerDesign extends JFrame implements ActionListener,ChangeListener,WindowStateListener,VetoableChangeListener,KeyListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param rd
     */

    public ViewerDesign( ReportDesign rd ) {
        super();
        log.info( "ViewerDesign" );
        m_WindowNo     = Env.createWindowNo( this );
        m_reportDesign = rd;
        m_printFormat  = m_reportDesign.getPrintFormat();

        try {
            m_viewDesignPanel = rd.getViewDesign( this );
            m_ctx             = m_reportDesign.getCtx();
            jbInit();
            dynInit();
            designSave = null;
            AEnv.showCenterScreen( this );
            updateTemporalItem();

            if( !m_printFormat.isForm()) {
                updateTemporalTable();
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Viewer",e );
            ADialog.error( m_WindowNo,this,"LoadError",e.getLocalizedMessage());
            this.dispose();
        }

        isFormatTable = false;
    }    // ViewerDesign

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ViewerDesign.class );

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private int m_pageNo = 1;

    /** Descripción de Campos */

    private int m_pageMax = 1;

    /** Descripción de Campos */

    private ViewDesign m_viewDesignPanel;

    /** Descripción de Campos */

    private boolean m_setting = false;

    /** Descripción de Campos */

    private ReportDesign m_reportDesign;

    /** Descripción de Campos */

    private MPrintFormat m_printFormat;

    /** Descripción de Campos */

    private InterfaceDesign designSave = null;

    /** Descripción de Campos */

    private int oldSeqNo;

    /** Descripción de Campos */

    private BorderLayout northLayout = new BorderLayout();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private JScrollPane centerScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar( false );

    /** Descripción de Campos */

    private JToolBar toolBar = new JToolBar();

    /** Descripción de Campos */

    private JToolBar toolBar2 = new JToolBar();

    /** Descripción de Campos */

    private JToolBar toolBar3 = new JToolBar();

    /** Descripción de Campos */

    private JToolBar toolBar4 = new JToolBar();

    /** Descripción de Campos */

    private boolean isFormatTable = false;

    /** Descripción de Campos */

    private CButton bSave = new CButton();

    /** Descripción de Campos */

    private CButton bRefresh = new CButton();

    /** Descripción de Campos */

    private CButton bEnd = new CButton();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        this.setIconImage( org.openXpertya.OpenXpertya.getImage16());
        this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        southPanel.setLayout( southLayout );
        northPanel.setLayout( northLayout );
        this.getContentPane().add( northPanel,BorderLayout.NORTH );
        toolBar.setFloatable( false );
        toolBar.setSize( 853,24 );
        toolBar.setPreferredSize( toolBar.getSize());
        toolBar2.setFloatable( false );
        toolBar2.setSize( 853,25 );
        toolBar2.setPreferredSize( toolBar2.getSize());
        toolBar3.setFloatable( false );
        toolBar3.setSize( 853,23 );
        toolBar3.setPreferredSize( toolBar3.getSize());
        toolBar4.setFloatable( false );
        toolBar4.setSize( 853,23 );
        toolBar4.setPreferredSize( toolBar4.getSize());
        northPanel.add( toolBar,BorderLayout.NORTH );
        northPanel.add( toolBar2,BorderLayout.SOUTH );

        if( m_printFormat.isForm()) {
            southPanel.add( toolBar4,BorderLayout.NORTH );
            southPanel.add( toolBar3,BorderLayout.SOUTH );
        } else {
            southPanel.add( toolBar3,BorderLayout.NORTH );
            southPanel.add( statusBar,BorderLayout.SOUTH );
        }

        this.getContentPane().add( centerScrollPane,BorderLayout.CENTER );
        centerScrollPane.getViewport().add( m_viewDesignPanel,null );
        this.getContentPane().add( southPanel,BorderLayout.SOUTH );
        bEnd.setSize( 25,25 );
        bEnd.setPreferredSize( bEnd.getSize());

        String text = Msg.getMsg( m_ctx,"End" );

        bEnd.setToolTipText( text );
        bEnd.setActionCommand( "End" );
        bEnd.setIcon( Env.getImageIcon( "End24.gif" ));
        bEnd.addActionListener( this );
        bSave.setSize( 25,25 );
        bSave.setPreferredSize( bSave.getSize());
        bSave.setToolTipText( "Guardar" );
        bSave.setActionCommand( "Guardar" );
        bSave.setIcon( Env.getImageIcon( "Save24.gif" ));
        bSave.addActionListener( this );
        bSave.setEnabled( true );
        bRefresh.setSize( 25,25 );
        bRefresh.setPreferredSize( bRefresh.getSize());
        bRefresh.setToolTipText( Msg.getMsg( Env.getCtx(),"Refresh" ));
        bRefresh.setActionCommand( "Actualizar" );
        bRefresh.setIcon( Env.getImageIcon( "Ignore24.gif" ));
        bRefresh.addActionListener( this );
        bRefresh.setEnabled( true );

        if( m_printFormat.isForm()) {
            vaciarToolBar();
        } else {
            vaciarToolBarNotForm();
        }
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {
        centerScrollPane.getViewport().addChangeListener( this );

        // Max Page

        m_pageMax = m_viewDesignPanel.getPageCount();
        revalidate();
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    public void revalidate() {
        m_pageMax = m_viewDesignPanel.getPageCount();

        // scroll area (page size dependent)

        centerScrollPane.setPreferredSize( new Dimension( m_viewDesignPanel.getPaperWidth() + 30,m_viewDesignPanel.getPaperHeight() + 15 ));
        centerScrollPane.getViewport().setViewSize( new Dimension( m_viewDesignPanel.getPaperWidth() + 2 * ViewDesign.MARGIN,m_viewDesignPanel.getPaperHeight() + 2 * ViewDesign.MARGIN ));

        // Report Info

        setTitle( Msg.getMsg( m_ctx,"Report" ) + ": " + m_reportDesign.getName() + "  " + Env.getHeader( m_ctx,0 ));

        StringBuffer sb = new StringBuffer();

        sb.append( m_viewDesignPanel.getPaper().toString( m_ctx )).append( " - " ).append( Msg.getMsg( m_ctx,"DataCols" )).append( "=" ).append( m_reportDesign.getColumnCount()).append( ", " ).append( Msg.getMsg( m_ctx,"DataRows" )).append( "=" ).append( m_reportDesign.getRowCount());
        statusBar.setStatusLine( sb.toString());

        //

        setPage( m_pageNo );
    }    // revalidate

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        Env.clearWinContext( m_WindowNo );
        m_reportDesign    = null;
        m_viewDesignPanel = null;
        m_ctx             = null;
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        String cmd = e.getActionCommand();

        if( cmd.equals( "End" )) {
            updateTemporalItem();
            updateAD_PrintFormatItem();

            if( !m_printFormat.isForm() && isFormatTable ) {
                updateTemporalTable();
                updateAD_PrintTableFormat();
            }

            dispose();
        } else if( cmd.equals( "Actualizar" )) {
            int id = 0;

            if( !m_printFormat.isForm()) {
                id = m_viewDesignPanel.getTableDesignElementItemID();
            }

            updateAD_PrintFormatItem();

            if( !m_printFormat.isForm() && isFormatTable ) {
                updateAD_PrintTableFormat();
                m_viewDesignPanel.changeFormat( true );
            }

            m_viewDesignPanel.ChangeByToolBar();
            m_viewDesignPanel.updateLayout();

            if( !m_printFormat.isForm() && (id != 0) ) {
                m_viewDesignPanel.setColSelectedByItemID( id );

                // m_viewDesignPanel.ChangeByToolBar();

                m_viewDesignPanel.updateLayout();

                // m_viewDesignPanel.refreshToolBar();

            }
        } else if( cmd.equals( "Guardar" )) {
            updateTemporalItem();

            if( !m_printFormat.isForm()) {
                updateTemporalTable();
            }

            bSave.setEnabled( true );
        } else {
            if( m_printFormat.isForm()) {
                if( designSave != null ) {
                    actualizar();
                }
            } else {
                m_viewDesignPanel.ChangeByToolBar();
                isFormatTable = true;
                actualizarNotForm( m_viewDesignPanel.getTableDesignElementItemID());
            }
        }
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

        // log.config( "Viewer.stateChanged", e);

        m_setting = true;

        int   newPage = 0;
        Point p       = centerScrollPane.getViewport().getViewPosition();

        newPage = Math.round( m_viewDesignPanel.getPageNoAt( p ));
        setPage( newPage );
        m_setting = false;
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param page
     */

    private void setPage( int page ) {
        m_setting = true;
        m_pageNo  = page;

        if( m_pageNo < 1 ) {
            m_pageNo = 1;
        }

        if( page > m_pageMax ) {
            m_pageNo = m_pageMax;
        }

        Rectangle pageRectangle = m_viewDesignPanel.getRectangleOfPage( m_pageNo );

        pageRectangle.x -= ViewDesign.MARGIN;
        pageRectangle.y -= ViewDesign.MARGIN;

        MPrintFormat pf = m_reportDesign.getPrintFormat();

//              centerScrollPane.getViewport().setViewPosition(
        // pageRectangle.getLocation());

        // Set Page

        StringBuffer sb = new StringBuffer( Msg.getMsg( m_ctx,"Page" )).append( " " ).append( m_pageNo ).append( m_viewDesignPanel.getPageInfo( m_pageNo )).append( " " ).append( Msg.getMsg( m_ctx,"of" )).append( " " ).append( m_pageMax ).append( m_viewDesignPanel.getPageInfoMax());

        statusBar.setStatusDB( sb.toString());
        m_setting = false;
    }    // setPage

    /**
     * Descripción de Método
     *
     *
     * @param deSelected
     */

    public void updateToolBar( InterfaceDesign deSelected ) {
        if( deSelected != null ) {
            cargarToolBar( deSelected );
            oldSeqNo = deSelected.getSeqNo();
        }

        if(( deSelected == null ) && ( designSave != null )) {
            oldSeqNo = 0;
            m_viewDesignPanel.updateLayout();
            vaciarToolBar();
            bSave.setEnabled( true );
            bRefresh.setEnabled( true );
        }

        designSave = deSelected;
    }

    /**
     * Descripción de Método
     *
     */

    public void vaciarToolBarNotForm() {
        isFormatTable = false;
        toolBar.removeAll();
        toolBar2.removeAll();
        toolBar3.removeAll();

        CLabel labelPrintFormatType = new CLabel( "Formato de impresi�n:" );

        toolBar.add( labelPrintFormatType );

        CComboBox printFormatType = new CComboBox();

        printFormatType.setSize( 150,24 );
        printFormatType.setMaximumSize( printFormatType.getSize());
        printFormatType.setPreferredSize( printFormatType.getSize());
        printFormatType.setEnabled( false );
        toolBar.add( printFormatType );
        toolBar.addSeparator();

        CLabel labelFieldAlignement = new CLabel( "Alineacion del campo:" );

        toolBar.add( labelFieldAlignement );

        CComboBox FieldAlignement = new CComboBox();

        FieldAlignement.setSize( 150,24 );
        FieldAlignement.setMaximumSize( FieldAlignement.getSize());
        FieldAlignement.setPreferredSize( FieldAlignement.getSize());
        FieldAlignement.setEnabled( false );
        toolBar.add( FieldAlignement );
        toolBar.addSeparator();

        try {
            CLabel labelColumn = new CLabel( "Columna:" );

            toolBar.add( labelColumn );

            CComboBox column = new CComboBox();

            column.setSize( 150,24 );
            column.setMaximumSize( column.getSize());
            column.setPreferredSize( column.getSize());
            column.setEnabled( false );
            toolBar.add( column );
            toolBar.addSeparator();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }

        // Segunda barra de herramientas

        CLabel labelSeqNo = new CLabel( "Numero Secuencia:" );

        toolBar2.add( labelSeqNo );

        VNumber seqNo = new VNumber( "SeqNo",false,true,false,DisplayType.Integer,"Numero de Secuencia" );

        seqNo.setSize( 60,20 );
        seqNo.setMaximumSize( seqNo.getSize());
        seqNo.setPreferredSize( seqNo.getSize());
        seqNo.setEnabled( false );
        toolBar2.add( seqNo );
        toolBar2.addSeparator();

        CLabel labelMaxWidth = new CLabel( "M�ximo Ancho:" );

        toolBar2.add( labelMaxWidth );

        VNumber maxWidth = new VNumber( "MaxWidth",false,true,false,DisplayType.Integer,"M�ximo Ancho" );

        maxWidth.setSize( 60,20 );
        maxWidth.setMaximumSize( maxWidth.getSize());
        maxWidth.setPreferredSize( maxWidth.getSize());
        maxWidth.setEnabled( false );
        toolBar2.add( maxWidth );
        toolBar2.addSeparator();

        CLabel labelMaxHeight = new CLabel( "M�ximo Alto:" );

        toolBar2.add( labelMaxHeight );

        VNumber maxHeight = new VNumber( "MaxHeight",false,true,false,DisplayType.Integer,"M�ximo Alto" );

        maxHeight.setSize( 60,20 );
        maxHeight.setMaximumSize( maxHeight.getSize());
        maxHeight.setPreferredSize( maxHeight.getSize());
        maxHeight.setEnabled( false );
        toolBar2.add( maxHeight );
        toolBar2.addSeparator();

        CLabel labelName = new CLabel( "Nombre:" );

        toolBar2.add( labelName );

        // CTextField Name = new CTextField();

        VNumber Name = new VNumber( "Name",false,true,false,DisplayType.Integer,"Nombre" );

        Name.setSize( 140,23 );
        Name.setMaximumSize( Name.getSize());
        Name.setPreferredSize( Name.getSize());
        Name.setEnabled( false );
        toolBar2.add( Name );
        toolBar2.addSeparator();

        CLabel labelBelowColum = new CLabel( "Columna Abajo:" );

        toolBar2.add( labelBelowColum );

        // CTextField Name = new CTextField();

        VNumber belowColumn = new VNumber( "BelowColumn",false,true,false,DisplayType.Integer,"Nombre" );

        belowColumn.setSize( 60,23 );
        belowColumn.setMaximumSize( belowColumn.getSize());
        belowColumn.setPreferredSize( belowColumn.getSize());
        belowColumn.setEnabled( false );
        toolBar2.add( belowColumn );
        toolBar2.addSeparator();

        // tercera barra de herramientas

        CLabel labelPrintName = new CLabel( "Nombre a imprimir:" );

        toolBar3.add( labelPrintName );

        // CTextField PrintName = new CTextField();

        VNumber PrintName = new VNumber( "PrintName",false,true,false,DisplayType.Integer,"Nombre a Imprimir" );

        PrintName.setSize( 140,23 );
        PrintName.setMaximumSize( PrintName.getSize());
        PrintName.setPreferredSize( PrintName.getSize());
        PrintName.setEnabled( false );
        toolBar3.add( PrintName );
        toolBar3.addSeparator();

        CLabel labelPrintFont = new CLabel( "Fuente de impresi�n:" );

        toolBar3.add( labelPrintFont );

        JComboBox printFont = new CComboBox();

        printFont.setSize( 150,24 );
        printFont.setMaximumSize( printFont.getSize());
        printFont.setPreferredSize( printFont.getSize());
        printFont.addItem( null );
        printFont.setEnabled( false );
        toolBar3.add( printFont );
        toolBar3.addSeparator();

        CLabel labelPrintColor = new CLabel( "Color de impresi�n:" );

        toolBar3.add( labelPrintColor );

        JComboBox printColor = new CComboBox();

        printColor.setSize( 150,24 );
        printColor.setMaximumSize( printColor.getSize());
        printColor.setPreferredSize( printColor.getSize());
        printColor.addItem( null );
        printColor.setEnabled( false );
        toolBar3.add( printColor );
        southPanel.remove( toolBar3 );
        southPanel.remove( toolBar4 );
        southPanel.add( statusBar,BorderLayout.SOUTH );
        southPanel.add( toolBar3,BorderLayout.NORTH );
        this.getContentPane().remove( southPanel );
        this.getContentPane().add( southPanel,BorderLayout.SOUTH );
        this.getContentPane().repaint();
        toolBar2.add( bEnd,null );
        toolBar.add( bRefresh,null );
        toolBar.add( bSave,null );
        toolBar.revalidate();
        toolBar.repaint();
        toolBar2.revalidate();
        toolBar2.repaint();
        toolBar3.revalidate();
        toolBar3.repaint();
    }

    /**
     * Descripción de Método
     *
     */

    public void vaciarToolBar() {
        toolBar.removeAll();
        toolBar2.removeAll();

        CLabel labelPrintFormatType = new CLabel( "Formato de impresi�n:" );

        toolBar.add( labelPrintFormatType );

        CComboBox printFormatType = new CComboBox();

        printFormatType.setSize( 150,24 );
        printFormatType.setMaximumSize( printFormatType.getSize());
        printFormatType.setPreferredSize( printFormatType.getSize());
        printFormatType.setEnabled( false );
        toolBar.add( printFormatType );
        toolBar.addSeparator();

        CLabel labelFieldAlignement = new CLabel( "Alineacion del campo:" );

        toolBar.add( labelFieldAlignement );

        CComboBox FieldAlignement = new CComboBox();

        FieldAlignement.setSize( 150,24 );
        FieldAlignement.setMaximumSize( FieldAlignement.getSize());
        FieldAlignement.setPreferredSize( FieldAlignement.getSize());
        FieldAlignement.setEnabled( false );
        toolBar.add( FieldAlignement );
        toolBar.addSeparator();

        CLabel labelLineAlignement = new CLabel( "Alineacion de la linea:" );

        toolBar.add( labelLineAlignement );

        CComboBox lineAlignement = new CComboBox();

        lineAlignement.setSize( 150,24 );
        lineAlignement.setMaximumSize( lineAlignement.getSize());
        lineAlignement.setPreferredSize( lineAlignement.getSize());
        lineAlignement.setEnabled( false );
        toolBar.add( lineAlignement );

        // Segunda barra de herramientas

        CLabel labelSeqNo = new CLabel( "Numero de Secuencia:" );

        toolBar2.add( labelSeqNo );

        VNumber seqNo = new VNumber( "SeqNo",false,true,false,DisplayType.Integer,"Numero de Secuencia" );

        seqNo.setSize( 60,20 );
        seqNo.setMaximumSize( seqNo.getSize());
        seqNo.setPreferredSize( seqNo.getSize());
        seqNo.setEnabled( false );
        toolBar2.add( seqNo );
        toolBar2.addSeparator();

        CLabel labelMaxWidth = new CLabel( "M�ximo Ancho:" );

        toolBar2.add( labelMaxWidth );

        VNumber maxWidth = new VNumber( "MaxWidth",false,true,false,DisplayType.Integer,"M�ximo Ancho" );

        maxWidth.setSize( 60,20 );
        maxWidth.setMaximumSize( maxWidth.getSize());
        maxWidth.setPreferredSize( maxWidth.getSize());
        maxWidth.setEnabled( false );
        toolBar2.add( maxWidth );
        toolBar2.addSeparator();

        CLabel labelMaxHeight = new CLabel( "M�ximo Alto:" );

        toolBar2.add( labelMaxHeight );

        VNumber maxHeight = new VNumber( "MaxHeight",false,true,false,DisplayType.Integer,"M�ximo Alto" );

        maxHeight.setSize( 60,20 );
        maxHeight.setMaximumSize( maxHeight.getSize());
        maxHeight.setPreferredSize( maxHeight.getSize());
        maxHeight.setEnabled( false );
        toolBar2.add( maxHeight );
        toolBar2.addSeparator();

        CLabel labelXPosition = new CLabel( "Posici�n X:" );

        toolBar2.add( labelXPosition );

        VNumber XPosition = new VNumber( "XPosition",false,true,false,DisplayType.Integer,"Posici�n X" );

        XPosition.setSize( 60,20 );
        XPosition.setMaximumSize( XPosition.getSize());
        XPosition.setPreferredSize( XPosition.getSize());
        XPosition.setEnabled( false );
        toolBar2.add( XPosition );
        toolBar2.addSeparator();

        CLabel labelYPosition = new CLabel( "Posici�n Y:" );

        toolBar2.add( labelYPosition );

        VNumber YPosition = new VNumber( "YPosition",false,true,false,DisplayType.Integer,"Posici�n Y" );

        YPosition.setSize( 60,20 );
        YPosition.setMaximumSize( YPosition.getSize());
        YPosition.setPreferredSize( YPosition.getSize());
        YPosition.setEnabled( false );
        toolBar2.add( YPosition );
        toolBar2.addSeparator();
        this.getContentPane().remove( southPanel );
        this.getContentPane().add( statusBar,BorderLayout.SOUTH );
        this.getContentPane().repaint();
        toolBar2.add( bRefresh,null );
        toolBar2.add( bSave,null );
        toolBar2.addSeparator();
        toolBar2.add( bEnd,null );
        toolBar.revalidate();
        toolBar.repaint();
        toolBar2.revalidate();
        toolBar2.repaint();
    }

    /**
     * Descripción de Método
     *
     *
     * @param fields
     */

    public void cargarToolBarNotForm( ArrayList fields ) {
        isFormatTable = false;
        toolBar.removeAll();
        toolBar2.removeAll();
        toolBar3.removeAll();
        this.getContentPane().remove( statusBar );
        this.getContentPane().add( southPanel,BorderLayout.SOUTH );
        this.getContentPane().repaint();

        int pos = 0;
        int sep = 0;

        for( int i = 0;i < fields.size();i++ ) {
            if( fields.get( i ) instanceof String ) {
                pos = pos + 1;
                sep = 0;
            } else {
                if( pos == 1 ) {
                    if( fields.get( i ) instanceof VLookup ) {
                        toolBar.add(( VLookup )fields.get( i ),null );
                        (( VLookup )fields.get( i )).addVetoableChangeListener( this );
                    }

                    if( fields.get( i ) instanceof CLabel ) {
                        toolBar.add(( CLabel )fields.get( i ),null );
                    }

                    if( sep == 1 ) {
                        toolBar.addSeparator();
                        sep = 0;
                    } else {
                        sep = sep + 1;
                    }
                }

                if( pos == 2 ) {
                    if( fields.get( i ) instanceof VNumber ) {
                        toolBar2.add(( VNumber )fields.get( i ),null );
                        (( VNumber )fields.get( i )).addVetoableChangeListener( this );
                    }

                    if( fields.get( i ) instanceof CLabel ) {
                        toolBar2.add(( CLabel )fields.get( i ),null );
                    }

                    if( fields.get( i ) instanceof CTextField ) {
                        toolBar2.add(( CTextField )fields.get( i ),null );
                        (( CTextField )fields.get( i )).addKeyListener( this );
                    }

                    if( sep == 1 ) {
                        toolBar2.addSeparator();
                        sep = 0;
                    } else {
                        sep = sep + 1;
                    }
                }

                if( pos == 3 ) {
                    if( fields.get( i ) instanceof CLabel ) {
                        toolBar3.add(( CLabel )fields.get( i ),null );
                    }

                    if( fields.get( i ) instanceof CTextField ) {
                        toolBar3.add(( CTextField )fields.get( i ),null );
                        (( CTextField )fields.get( i )).addKeyListener( this );
                    }

                    if( fields.get( i ) instanceof VLookup ) {
                        toolBar3.add(( VLookup )fields.get( i ),null );
                        (( VLookup )fields.get( i )).addActionListener( this );
                    }

                    if( sep == 1 ) {
                        toolBar3.addSeparator();
                        sep = 0;
                    } else {
                        sep = sep + 1;
                    }
                }
            }
        }

        toolBar.add( bRefresh,null );
        toolBar.add( bSave,null );
        toolBar2.addSeparator();
        toolBar2.add( bEnd,null );
        southPanel.remove( toolBar3 );
        southPanel.remove( toolBar4 );
        southPanel.add( statusBar,BorderLayout.SOUTH );
        southPanel.add( toolBar3,BorderLayout.NORTH );
        this.getContentPane().remove( southPanel );
        this.getContentPane().add( southPanel,BorderLayout.SOUTH );
        toolBar.revalidate();
        toolBar.repaint();
        toolBar2.revalidate();
        toolBar2.repaint();
        toolBar3.revalidate();
        toolBar3.repaint();
    }

    /**
     * Descripción de Método
     *
     *
     * @param tde
     */

    public void cargarToolBarTable( TableDesignElement tde ) {
        isFormatTable = true;
        southPanel.remove( toolBar3 );
        southPanel.remove( statusBar );
        southPanel.add( toolBar4,BorderLayout.NORTH );
        southPanel.add( toolBar3,BorderLayout.SOUTH );
        this.getContentPane().remove( southPanel );
        this.getContentPane().add( southPanel,BorderLayout.SOUTH );
        this.getContentPane().repaint();
        toolBar.removeAll();
        toolBar2.removeAll();
        toolBar3.removeAll();
        toolBar4.removeAll();

        ArrayList fields = tde.getFields();
        int       pos    = 0;
        int       sep    = 0;

        for( int i = 0;i < fields.size();i++ ) {
            if( fields.get( i ) instanceof String ) {
                pos = pos + 1;
                sep = 0;

                if( pos == 4 ) {
                    toolBar4.setVisible( true );
                    toolBar4.setEnabled( true );
                }
            } else {
                if( pos == 1 ) {
                    if( fields.get( i ) instanceof CCheckBox ) {
                        toolBar.add(( CCheckBox )fields.get( i ),null );
                        (( CCheckBox )fields.get( i )).addActionListener( this );
                        sep = 1;
                    }

                    if( fields.get( i ) instanceof CLabel ) {
                        toolBar.add(( CLabel )fields.get( i ),null );
                    }

                    if( fields.get( i ) instanceof VLookup ) {
                        toolBar.add(( VLookup )fields.get( i ),null );
                        (( VLookup )fields.get( i )).addActionListener( this );
                    }

                    if( sep == 1 ) {
                        toolBar.addSeparator();
                        sep = 0;
                    } else {
                        sep = sep + 1;
                    }
                }

                if( pos == 2 ) {
                    if( fields.get( i ) instanceof CCheckBox ) {
                        toolBar2.add(( CCheckBox )fields.get( i ),null );
                        (( CCheckBox )fields.get( i )).addActionListener( this );
                        sep = 1;
                    }

                    if( fields.get( i ) instanceof VLookup ) {
                        toolBar2.add(( VLookup )fields.get( i ),null );
                        (( VLookup )fields.get( i )).addActionListener( this );
                    }

                    if( fields.get( i ) instanceof VNumber ) {
                        toolBar2.add(( VNumber )fields.get( i ),null );
                        (( VNumber )fields.get( i )).addVetoableChangeListener( this );
                    }

                    if( fields.get( i ) instanceof CLabel ) {
                        toolBar2.add(( CLabel )fields.get( i ),null );
                    }

                    if( sep == 1 ) {
                        toolBar2.addSeparator();
                        sep = 0;
                    } else {
                        sep = sep + 1;
                    }
                }

                if( pos == 3 ) {
                    if( fields.get( i ) instanceof CCheckBox ) {
                        toolBar3.add(( CCheckBox )fields.get( i ),null );
                        (( CCheckBox )fields.get( i )).addActionListener( this );
                        sep = 1;
                    }

                    if( fields.get( i ) instanceof CLabel ) {
                        toolBar3.add(( CLabel )fields.get( i ),null );
                    }

                    if( fields.get( i ) instanceof VLookup ) {
                        toolBar3.add(( VLookup )fields.get( i ),null );
                        (( VLookup )fields.get( i )).addActionListener( this );
                    }

                    if( sep == 1 ) {
                        toolBar3.addSeparator();
                        sep = 0;
                    } else {
                        sep = sep + 1;
                    }
                }

                if( pos == 4 ) {
                    if( fields.get( i ) instanceof CCheckBox ) {
                        toolBar4.add(( CCheckBox )fields.get( i ),null );
                        (( CCheckBox )fields.get( i )).addActionListener( this );
                        sep = 1;
                    }

                    if( fields.get( i ) instanceof CLabel ) {
                        toolBar4.add(( CLabel )fields.get( i ),null );
                    }

                    if( fields.get( i ) instanceof VLookup ) {
                        toolBar4.add(( VLookup )fields.get( i ),null );
                        (( VLookup )fields.get( i )).addActionListener( this );
                    }

                    if( fields.get( i ) instanceof VNumber ) {
                        toolBar4.add(( VNumber )fields.get( i ),null );
                        (( VNumber )fields.get( i )).addActionListener( this );
                    }

                    if( sep == 1 ) {
                        toolBar4.addSeparator();
                        sep = 0;
                    } else {
                        sep = sep + 1;
                    }
                }
            }
        }

        toolBar2.add( bRefresh,null );
        toolBar2.add( bSave,null );
        toolBar2.addSeparator();
        toolBar2.add( bEnd,null );
        toolBar.revalidate();
        toolBar.repaint();
        toolBar2.revalidate();
        toolBar2.repaint();
        toolBar3.revalidate();
        toolBar3.repaint();
        toolBar4.revalidate();
        toolBar4.repaint();
    }

    /**
     * Descripción de Método
     *
     *
     * @param deSelected
     */

    public void cargarToolBar( InterfaceDesign deSelected ) {
        toolBar.removeAll();
        toolBar2.removeAll();
        toolBar3.removeAll();
        toolBar4.removeAll();

        int prueba = 1;

        if( deSelected != null ) {
            this.getContentPane().remove( statusBar );
            toolBar4.setVisible( false );
            toolBar4.setEnabled( false );
            this.getContentPane().add( southPanel,BorderLayout.SOUTH );
            this.getContentPane().repaint();

            ArrayList fields = deSelected.getFields();
            int       pos    = 0;
            int       sep    = 0;

            for( int i = 0;i < fields.size();i++ ) {
                if( fields.get( i ) instanceof String ) {
                    pos = pos + 1;
                    sep = 0;

                    if( pos == 4 ) {
                        toolBar4.setVisible( true );
                        toolBar4.setEnabled( true );
                    }
                } else {
                    if( pos == 1 ) {
                        if( fields.get( i ) instanceof VLookup ) {
                            if( prueba == 1 ) {
                                VLookup printFormatType = ( VLookup )fields.get( i );

                                toolBar.add( printFormatType );
                                printFormatType.addVetoableChangeListener( this );
                                prueba = 2;
                            } else {
                                toolBar.add(( VLookup )fields.get( i ),null );
                                (( VLookup )fields.get( i )).addVetoableChangeListener( this );
                            }
                        }

                        if( fields.get( i ) instanceof CLabel ) {
                            toolBar.add(( CLabel )fields.get( i ),null );
                        }

                        if( sep == 1 ) {
                            toolBar.addSeparator();
                            sep = 0;
                        } else {
                            sep = sep + 1;
                        }
                    }

                    if( pos == 2 ) {
                        if( fields.get( i ) instanceof VNumber ) {
                            toolBar2.add(( VNumber )fields.get( i ),null );
                            (( VNumber )fields.get( i )).addVetoableChangeListener( this );
                        }

                        if( fields.get( i ) instanceof CLabel ) {
                            toolBar2.add(( CLabel )fields.get( i ),null );
                        }

                        if( sep == 1 ) {
                            toolBar2.addSeparator();
                            sep = 0;
                        } else {
                            sep = sep + 1;
                        }
                    }

                    if( pos == 3 ) {
                        if( fields.get( i ) instanceof CLabel ) {
                            toolBar3.add(( CLabel )fields.get( i ),null );
                        }

                        if( fields.get( i ) instanceof CTextField ) {
                            toolBar3.add(( CTextField )fields.get( i ),null );
                            (( CTextField )fields.get( i )).addKeyListener( this );
                        }

                        if( sep == 1 ) {
                            toolBar3.addSeparator();
                            sep = 0;
                        } else {
                            sep = sep + 1;
                        }
                    }

                    if( pos == 4 ) {
                        if( fields.get( i ) instanceof VLookup ) {
                            toolBar4.add(( VLookup )fields.get( i ),null );
                            (( VLookup )fields.get( i )).addVetoableChangeListener( this );
                        }

                        if( fields.get( i ) instanceof VNumber ) {
                            toolBar4.add(( VNumber )fields.get( i ),null );
                            (( VNumber )fields.get( i )).addVetoableChangeListener( this );
                        }

                        if( fields.get( i ) instanceof CLabel ) {
                            toolBar4.add(( CLabel )fields.get( i ),null );
                        }

                        if( sep == 1 ) {
                            toolBar4.addSeparator();
                            sep = 0;
                        } else {
                            sep = sep + 1;
                        }
                    }
                }
            }
        }

        toolBar2.add( bRefresh,null );
        toolBar2.add( bSave,null );
        toolBar2.addSeparator();
        toolBar2.add( bEnd,null );
        toolBar.revalidate();
        toolBar.repaint();
        toolBar2.revalidate();
        toolBar2.repaint();
        toolBar3.revalidate();
        toolBar3.repaint();
        toolBar4.revalidate();
        toolBar4.repaint();
    }

    /**
     * Descripción de Método
     *
     */

    public void actualizar() {
        if( designSave != null ) {
            MPrintFormatItem pfi = new MPrintFormatItem( Env.getCtx(),designSave.getPrintFormatItemID(),null );
            VNumber valueVNumber = null;
            int     casilla      = 0;

            for( int i = 0;i < toolBar2.getComponentCount();i++ ) {
                if( toolBar2.getComponent( i ) instanceof VNumber ) {
                    valueVNumber = ( VNumber )toolBar2.getComponent( i );

                    int value = 0;

                    if(( Integer )valueVNumber.getValue() != null ) {
                        value = (( Integer )valueVNumber.getValue()).intValue();
                    }

                    casilla = casilla + 1;

                    if( casilla == 1 ) {
                        if( value < 0 ) {
                            pfi.setSeqNo( -value );
                        } else {
                            pfi.setSeqNo( value );
                        }
                    }

                    if( casilla == 2 ) {
                        if( (value < 0) &&!pfi.getPrintFormatType().equals( MPrintFormatItem.PRINTFORMATTYPE_Line )) {
                            pfi.setMaxWidth( -value );
                        } else {
                            pfi.setMaxWidth( value );
                        }
                    }

                    if( casilla == 3 ) {
                        if( (value < 0) &&!pfi.getPrintFormatType().equals( MPrintFormatItem.PRINTFORMATTYPE_Line )) {
                            pfi.setMaxHeight( -value );
                        } else {
                            pfi.setMaxHeight( value );
                        }
                    }

                    if( pfi.isRelativePosition()) {
                        if( casilla == 4 )    // && ((Integer)valueVNumber.getValue()) != null)
                        {
                            if( value < 0 ) {
                                pfi.setXSpace( -value );
                            } else {
                                pfi.setXSpace( value );
                            }
                        }

                        if( casilla == 5 )    // && ((Integer)valueVNumber.getValue()) != null)
                        {
                            if( value < 0 ) {
                                pfi.setYSpace( -value );
                            } else {
                                pfi.setYSpace( value );
                            }
                        }
                    } else {
                        if( casilla == 4 )    // && ((Integer)valueVNumber.getValue()) != null)
                        {
                            if( value < 0 ) {
                                pfi.setXPosition( -value );
                            } else {
                                pfi.setXPosition( value );
                            }
                        }

                        if( casilla == 5 )    // && ((Integer)valueVNumber.getValue()) != null)
                        {
                            if( value < 0 ) {
                                pfi.setYPosition( -value );
                            } else {
                                pfi.setYPosition( value );
                            }
                        }
                    }
                }
            }

            casilla = 0;

            VLookup valueVLookup = null;

            for( int i = 0;i < toolBar.getComponentCount();i++ ) {
                if( toolBar.getComponent( i ) instanceof VLookup ) {
                    valueVLookup = ( VLookup )toolBar.getComponent( i );
                    casilla      = casilla + 1;

                    if( casilla == 1 ) {
                        pfi.setPrintFormatType( valueVLookup.getValue().toString());
                    }

                    if( casilla == 2 ) {
                        pfi.setFieldAlignmentType((( String )valueVLookup.getValue()).toString());
                    }

                    if( designSave instanceof BoxDesignElement ) {
                        if( pfi.getPrintFormatType().equals( MPrintFormatItem.PRINTFORMATTYPE_Line )) {
                            if( (casilla == 3) && valueVLookup.isEnabled() && pfi.isRelativePosition()) {
                                pfi.setLineAlignmentType((( String )valueVLookup.getValue()).toString());
                            }
                        } else {
                            pfi.setShapeType((( String )valueVLookup.getValue()).toString());
                        }
                    } else {
                        if( (casilla == 3) && valueVLookup.isEnabled() && pfi.isRelativePosition()) {
                            pfi.setLineAlignmentType((( String )valueVLookup.getValue()).toString());
                        }
                    }
                }
            }

            casilla = 0;

            CTextField valueTextField = null;

            for( int i = 0;i < toolBar3.getComponentCount();i++ ) {
                if( toolBar3.getComponent( i ) instanceof CTextField ) {
                    valueTextField = ( CTextField )toolBar3.getComponent( i );
                    casilla        = casilla + 1;

                    if( casilla == 1 ) {
                        pfi.setName( valueTextField.getValue().toString());
                    }

                    if( casilla == 2 ) {
                        pfi.setPrintName( valueTextField.getValue().toString());
                    }

                    if( casilla == 3 ) {
                        if( pfi.getPrintFormatType().charAt( 0 ) == 'F' ) {
                            pfi.setPrintNameSuffix( valueTextField.getValue().toString());
                        } else {
                            pfi.setImageURL( valueTextField.getValue().toString());
                        }
                    }
                }
            }

            casilla = 0;

            String            sql;
            PreparedStatement pstmt;
            ResultSet         rs;

            for( int i = 0;i < toolBar4.getComponentCount();i++ ) {
                if( toolBar4.getComponent( i ) instanceof VLookup ) {
                    valueVLookup = ( VLookup )toolBar4.getComponent( i );
                    casilla      = casilla + 1;

                    if( casilla == 1 ) {
                        if( valueVLookup.getValue() != null ) {
                            pfi.setAD_PrintFont_ID((( Integer )valueVLookup.getValue()).intValue());
                        }
                    }

                    if( casilla == 2 ) {
                        if( valueVLookup.getValue() != null ) {
                            pfi.setAD_PrintColor_ID((( Integer )valueVLookup.getValue()).intValue());
                        }
                    }

                    if( casilla == 3 ) {
                        if( valueVLookup.getValue() != null ) {
                            pfi.setAD_Column_ID((( Integer )valueVLookup.getValue()).intValue());
                        }
                    }
                }

//                  if (designSave instanceof BoxDesignElement)
                // {

                if( toolBar4.getComponent( i ) instanceof VNumber ) {
                    valueVNumber = ( VNumber )toolBar4.getComponent( i );

                    int value = 0;

                    if(( Integer )valueVNumber.getValue() != null ) {
                        value = (( Integer )valueVNumber.getValue()).intValue();

                        if( value < 0 ) {
                            value = -value;
                        }
                    }

                    pfi.setLineWidth( value );
                }

                // }

            }

            pfi.save();
            m_viewDesignPanel.updateLayout();
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param ID
     */

    public void actualizarNotForm( int ID ) {
        if( !isFormatTable ) {
            MPrintFormatItem pfi = new MPrintFormatItem( Env.getCtx(),ID,null );
            VNumber    valueVNumber   = null;
            CTextField valueTextField = null;
            int        casilla        = 0;

            for( int i = 0;i < toolBar2.getComponentCount();i++ ) {
                if( toolBar2.getComponent( i ) instanceof VNumber ) {
                    valueVNumber = ( VNumber )toolBar2.getComponent( i );

                    int value = 0;

                    if(( Integer )valueVNumber.getValue() != null ) {
                        value = (( Integer )valueVNumber.getValue()).intValue();
                    }

                    casilla = casilla + 1;

                    if( casilla == 1 ) {

                        // if ((Integer)valueVNumber.getValue() != null)
                        // {

                        if( value < 0 ) {
                            pfi.setSeqNo( -value );
                        } else {
                            pfi.setSeqNo( value );
                        }

                        // }

                    }

                    if( casilla == 2 ) {

                        // if ((Integer)valueVNumber.getValue()!= null)
                        // {

                        if( value < 0 ) {
                            pfi.setMaxWidth( -value );
                        } else {
                            pfi.setMaxWidth( value );
                        }

                        // }

                    }

                    if( casilla == 3 ) {

                        // if ((Integer)valueVNumber.getValue() != null)
                        // {

                        if( value < 0 ) {
                            pfi.setMaxHeight( -value );
                        } else {
                            pfi.setMaxHeight( value );
                        }

                        // }

                    }

                    if( pfi.isNextLine()) {
                        if( casilla == 4 ) {    // && ((Integer)valueVNumber.getValue()) != null)
                            pfi.setBelowColumn( value );
                        }
                    }
                }

                if( toolBar2.getComponent( i ) instanceof CTextField ) {
                    valueTextField = ( CTextField )toolBar2.getComponent( i );
                    pfi.setName( valueTextField.getValue().toString());
                }
            }

            casilla = 0;

            VLookup valueVLookup = null;

            for( int i = 0;i < toolBar.getComponentCount();i++ ) {
                if( toolBar.getComponent( i ) instanceof VLookup ) {
                    valueVLookup = ( VLookup )toolBar.getComponent( i );
                    casilla      = casilla + 1;

                    if( casilla == 1 ) {
                        pfi.setPrintFormatType( valueVLookup.getValue().toString());
                    }

                    if( casilla == 2 ) {
                        pfi.setFieldAlignmentType((( String )valueVLookup.getValue()).toString());
                    }

                    if( (casilla == 3) && valueVLookup.isEnabled() && (valueVLookup.getValue() != null) ) {
                        pfi.setAD_Column_ID((( Integer )valueVLookup.getValue()).intValue());
                    }
                }
            }

            casilla        = 0;
            valueTextField = null;
            valueVLookup   = null;

            String            sql;
            PreparedStatement pstmt;
            ResultSet         rs;

            for( int i = 0;i < toolBar3.getComponentCount();i++ ) {
                if( toolBar3.getComponent( i ) instanceof CTextField ) {
                    valueTextField = ( CTextField )toolBar3.getComponent( i );
                    pfi.setPrintName( valueTextField.getValue().toString());
                }

                if(( toolBar3.getComponent( i ) instanceof VLookup )) {
                    valueVLookup = ( VLookup )toolBar3.getComponent( i );

                    if( casilla == 0 ) {
                        if((( Integer )valueVLookup.getValue()) != null ) {
                            pfi.setAD_PrintFont_ID((( Integer )valueVLookup.getValue()).intValue());
                        }
                    }

                    if( casilla == 1 ) {
                        if((( Integer )valueVLookup.getValue()) != null ) {
                            pfi.setAD_PrintColor_ID((( Integer )valueVLookup.getValue()).intValue());
                        }
                    }

                    casilla = casilla + 1;
                    pfi.setPrintName( valueTextField.getValue().toString());
                }
            }

            pfi.save();
        } else {
            MPrintTableFormat ptf = new MPrintTableFormat( Env.getCtx(),m_viewDesignPanel.getTableFormatID(),null );
            VNumber   valueVNumber  = null;
            CCheckBox valueCheckBox = null;
            VLookup   valueVLookup  = null;
            int       casilla       = 0;

            for( int i = 0;i < toolBar.getComponentCount();i++ ) {
                if( toolBar.getComponent( i ) instanceof CCheckBox ) {
                    valueCheckBox = ( CCheckBox )toolBar.getComponent( i );
                    ptf.setIsDefault( valueCheckBox.isSelected());
                }

                if( toolBar.getComponent( i ) instanceof VLookup ) {
                    valueVLookup = ( VLookup )toolBar.getComponent( i );
                    casilla      = casilla + 1;

                    if( casilla == 1 ) {
                        if( valueVLookup.getValue() != null ) {
                            ptf.setHdrTextFG_PrintColor_ID((( Integer )valueVLookup.getValue()).intValue());
                        } else {
                            ptf.setHdrTextFG_PrintColor_ID( 0 );
                        }
                    }

                    if( casilla == 2 ) {
                        if( valueVLookup.getValue() != null ) {
                            ptf.setHdrTextBG_PrintColor_ID((( Integer )valueVLookup.getValue()).intValue());
                        } else {
                            ptf.setHdrTextBG_PrintColor_ID( 0 );
                        }
                    }

                    if( casilla == 3 ) {
                        if( valueVLookup.getValue() != null ) {
                            ptf.setHdr_PrintFont_ID((( Integer )valueVLookup.getValue()).intValue());
                        } else {
                            ptf.setHdr_PrintFont_ID( 0 );
                        }
                    }
                }
            }

            casilla       = 0;
            valueVLookup  = null;
            valueCheckBox = null;

            for( int i = 0;i < toolBar2.getComponentCount();i++ ) {
                if( toolBar2.getComponent( i ) instanceof CCheckBox ) {
                    valueCheckBox = ( CCheckBox )toolBar2.getComponent( i );
                    ptf.setIsPaintHeaderLines( valueCheckBox.isSelected());
                }

                if( toolBar2.getComponent( i ) instanceof VLookup ) {
                    casilla      = casilla + 1;
                    valueVLookup = ( VLookup )toolBar2.getComponent( i );

                    if( casilla == 1 ) {
                        if( valueVLookup.getValue() != null ) {
                            ptf.setHdrLine_PrintColor_ID((( Integer )valueVLookup.getValue()).intValue());
                        } else {
                            ptf.setHdrLine_PrintColor_ID( 0 );
                        }
                    }

                    if( casilla == 2 ) {
                        if( (valueVLookup.getValue().toString() != null) &&!valueVLookup.getValue().toString().equals( "" )) {
                            ptf.setHdrStrokeType( valueVLookup.getValue().toString());
                        } else {
                            ptf.setHdrStrokeType( null );
                        }
                    }
                }

                if( toolBar2.getComponent( i ) instanceof VNumber ) {
                    valueVNumber = ( VNumber )toolBar2.getComponent( i );

                    BigDecimal bg = new BigDecimal( valueVNumber.getValue().toString());

                    if( bg.intValue() < 0 ) {
                        bg = new BigDecimal( -bg.floatValue());
                    }

                    ptf.setHdrStroke( bg );
                }
            }

            casilla       = 0;
            valueVLookup  = null;
            valueCheckBox = null;
            valueVNumber  = null;

            for( int i = 0;i < toolBar3.getComponentCount();i++ ) {
                if( toolBar3.getComponent( i ) instanceof CCheckBox ) {
                    casilla       = casilla + 1;
                    valueCheckBox = ( CCheckBox )toolBar3.getComponent( i );

                    if( casilla == 1 ) {
                        ptf.setIsPaintVLines( valueCheckBox.isSelected());
                    }

                    if( casilla == 2 ) {
                        ptf.setIsPrintFunctionSymbols( valueCheckBox.isSelected());
                    }
                }

                if(( toolBar3.getComponent( i ) instanceof VLookup )) {
                    casilla      = casilla + 1;
                    valueVLookup = ( VLookup )toolBar3.getComponent( i );

                    if( casilla == 3 ) {
                        if( valueVLookup.getValue() != null ) {
                            ptf.setFunctFG_PrintColor_ID((( Integer )valueVLookup.getValue()).intValue());
                        } else {
                            ptf.setFunctFG_PrintColor_ID( 0 );
                        }
                    }

                    if( casilla == 4 ) {
                        if( valueVLookup.getValue() != null ) {
                            ptf.setFunctBG_PrintColor_ID((( Integer )valueVLookup.getValue()).intValue());
                        } else {
                            ptf.setFunctBG_PrintColor_ID( 0 );
                        }
                    }

                    if( casilla == 5 ) {
                        if( valueVLookup.getValue() != null ) {
                            ptf.setFunct_PrintFont_ID((( Integer )valueVLookup.getValue()).intValue());
                        } else {
                            ptf.setFunct_PrintFont_ID( 0 );
                        }
                    }
                }
            }

            valueVLookup  = null;
            valueCheckBox = null;
            casilla       = 0;

            for( int i = 0;i < toolBar4.getComponentCount();i++ ) {
                if( toolBar4.getComponent( i ) instanceof CCheckBox ) {
                    casilla       = casilla + 1;
                    valueCheckBox = ( CCheckBox )toolBar4.getComponent( i );

                    if( casilla == 1 ) {
                        ptf.setIsPaintHLines( valueCheckBox.isSelected());
                    }

                    if( casilla == 2 ) {
                        ptf.setIsPaintBoundaryLines( valueCheckBox.isSelected());
                    }
                }

                if(( toolBar4.getComponent( i ) instanceof VLookup )) {
                    casilla      = casilla + 1;
                    valueVLookup = ( VLookup )toolBar4.getComponent( i );

                    if( casilla == 3 ) {
                        if( valueVLookup.getValue() != null ) {
                            ptf.setLine_PrintColor_ID((( Integer )valueVLookup.getValue()).intValue());
                        } else {
                            ptf.setLine_PrintColor_ID( 0 );
                        }
                    }

                    if( casilla == 4 ) {
                        ptf.setLineStrokeType( valueVLookup.getValue().toString());
                    }
                }

                if( toolBar4.getComponent( i ) instanceof VNumber ) {
                    valueVNumber = ( VNumber )toolBar4.getComponent( i );

                    BigDecimal bg = new BigDecimal( valueVNumber.getValue().toString());

                    if( bg.intValue() < 0 ) {
                        bg = new BigDecimal( -bg.floatValue());
                    }

                    ptf.setLineStroke( bg );
                }
            }

            ptf.save();
            m_viewDesignPanel.changeFormat( false );
        }

        m_viewDesignPanel.updateLayout();
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void windowStateChanged( WindowEvent e ) {

        // The Customize Window was closed

        if( (e.getID() == WindowEvent.WINDOW_CLOSED) && (m_reportDesign != null) ) {
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
            log.info( "Re-read PrintFormat" );

            int          AD_PrintFormat_ID = m_reportDesign.getPrintFormat().getID();
            Language     language          = m_reportDesign.getPrintFormat().getLanguage();
            MPrintFormat pf                = MPrintFormat.get( Env.getCtx(),AD_PrintFormat_ID,true );

            pf.setLanguage( language );    // needs to be re-set - otherwise viewer will be blank
            pf.setTranslationLanguage( language );
            m_reportDesign.setPrintFormat( pf );
            revalidate();

            // cmd_drill(); //   setCursor

        }
    }    // windowStateChanged

    /**
     * Descripción de Método
     *
     *
     * @param newSeqno
     */

    public void updateSeqNo( int newSeqno ) {
        oldSeqNo = newSeqno;
    }

    /**
     * Descripción de Método
     *
     *
     * @param evt
     *
     * @throws PropertyVetoException
     */

    public void vetoableChange( PropertyChangeEvent evt ) throws PropertyVetoException {

        // TODO Auto-generated method stub

        m_viewDesignPanel.ChangeByToolBar();

        if( m_printFormat.isForm()) {
            actualizar();

            if( evt.getPropertyName() == "AD_Print_Format_Type" ) {
                designSave = m_viewDesignPanel.getDeSelected();
                updateToolBar( designSave );
            }
        } else {
            if( evt.getPropertyName().equals( "SeqNo" )) {
                m_viewDesignPanel.setColSelectedByItemID();
            } else {
                actualizarNotForm( m_viewDesignPanel.getTableDesignElementItemID());
            }
        }
    }

    /**
     * Descripción de Método
     *
     */

    public void updateTemporalItem() {
        String sql;
        int    execInsert;

        sql        = new String( "DELETE FROM t_printformatitem" );
        execInsert = DB.executeUpdate( sql );
        sql        = new String( "INSERT INTO t_printformatitem " + " (ad_printformatitem_id, ad_client_id, ad_org_id, isactive, created , createdby, updated, updatedby, ad_printformat_id, name, printname, isprinted, printareatype , seqno, printformattype, ad_column_id, ad_printformatchild_id, isrelativeposition, isnextline, xspace, yspace, xposition, yposition, maxwidth, isheightoneline, maxheight , fieldalignmenttype , linealignmenttype , ad_printcolor_id  , ad_printfont_id , isorderby, sortno, isgroupby, ispagebreak, issummarized, imageisattached , imageurl , isaveraged, iscounted , issetnlposition , issuppressnull , belowcolumn , ad_printgraph_id , isfixedwidth , isnextpage , printnamesuffix , ismincalc , ismaxcalc , isrunningtotal , runningtotallines , isvariancecalc , isdeviationcalc, isfilledrectangle, linewidth, arcdiameter, shapetype, iscentrallymaintained, isimagefield )" + " SELECT * FROM ad_printformatitem WHERE ad_printformat_id = " + m_reportDesign.getPrintFormat().getAD_PrintFormat_ID());
        execInsert = DB.executeUpdate( sql );
        sql        = new String( "UPDATE ad_printformatitem " + " SET maxwidth = 50" + " WHERE ad_printformat_id = " + m_reportDesign.getPrintFormat().getAD_PrintFormat_ID() + " AND maxwidth < 15" + " AND printformattype not like 'L'" );
        execInsert = DB.executeUpdate( sql );
        sql        = new String( "UPDATE ad_printformatitem " + " SET maxheight = 18" + " WHERE ad_printformat_id = " + m_reportDesign.getPrintFormat().getAD_PrintFormat_ID() + " AND maxheight < 15" + " AND printformattype not like 'L'" );
        execInsert = DB.executeUpdate( sql );
        m_viewDesignPanel.ChangeByToolBar();
        m_viewDesignPanel.updateLayout();
    }

    /**
     * Descripción de Método
     *
     */

    public void updateAD_PrintFormatItem() {
        String sql;
        int    execInsert;

        try {
            sql = new String( "UPDATE AD_PrintFormatItem ad " + " SET (ad_printformatitem_id, ad_client_id, ad_org_id, isactive," + " created , createdby, updated, updatedby, ad_printformat_id, " + " name, printname, isprinted, printareatype , seqno, printformattype," + " ad_column_id, ad_printformatchild_id, isrelativeposition, isnextline," + " xspace, yspace, xposition, yposition, maxwidth, isheightoneline, maxheight," + " fieldalignmenttype, linealignmenttype, ad_printcolor_id, ad_printfont_id," + " isorderby, sortno, isgroupby, ispagebreak, issummarized, imageisattached," + " imageurl, isaveraged, iscounted, issetnlposition, issuppressnull, belowcolumn," + " ad_printgraph_id, isfixedwidth, isnextpage, printnamesuffix, ismincalc, ismaxcalc," + " isrunningtotal , runningtotallines , isvariancecalc , isdeviationcalc, " + " isfilledrectangle, linewidth, arcdiameter, shapetype, iscentrallymaintained, isimagefield) = " + " (SELECT * FROM t_printformatitem t WHERE t.ad_printformatitem_id = ad.ad_printformatitem_id)" + " WHERE ad_printformat_id IN (SELECT DISTINCT ad_printFormat_ID FROM t_printFormatItem)" );
            execInsert = DB.executeUpdate( sql );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }
    }

    /**
     * Descripción de Método
     *
     */

    public void updateTemporalTable() {
        String sql;
        int    execInsert;

        sql        = new String( "DELETE FROM t_printtableformat" );
        execInsert = DB.executeUpdate( sql );
        sql        = new String( "INSERT INTO t_printtableformat " + " (ad_printtableformat_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, hdr_printfont_id, hdrtextfg_printcolor_id, hdrtextbg_printcolor_id, hdrline_printcolor_id, funct_printfont_id, functbg_printcolor_id, functfg_printcolor_id, line_printcolor_id, description, ispaintboundarylines, ispainthlines, ispaintvlines, isprintfunctionsymbols, name, isdefault, imageurl, headerleft, headercenter, headerright, footerleft, footercenter, footerright, imageisattached, hdrstroke, linestroke, hdrstroketype, linestroketype, ispaintheaderlines) " + " SELECT * FROM ad_printtableformat WHERE ad_printtableformat_id = " + m_reportDesign.getPrintFormat().getAD_PrintTableFormat_ID());
        execInsert = DB.executeUpdate( sql );
        m_viewDesignPanel.ChangeByToolBar();
        m_viewDesignPanel.updateLayout();
    }

    /**
     * Descripción de Método
     *
     */

    public void updateAD_PrintTableFormat() {
        MPrintFormat pf = ( MPrintFormat )m_reportDesign.getPrintFormat();
        String       sql;
        int          execInsert;

        try {
            sql = new String( "SELECT ad_printtableformat_id FROM t_printtableformat" );

            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            rs.next();
            pf.setAD_PrintTableFormat_ID( rs.getInt( 1 ));
            pf.save();
            sql = new String( "UPDATE ad_printtableformat ad " + " SET (ad_printtableformat_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, hdr_printfont_id, hdrtextfg_printcolor_id, hdrtextbg_printcolor_id, hdrline_printcolor_id, funct_printfont_id, functbg_printcolor_id, functfg_printcolor_id, line_printcolor_id, description, ispaintboundarylines, ispainthlines, ispaintvlines, isprintfunctionsymbols, name, isdefault, imageurl, headerleft, headercenter, headerright, footerleft, footercenter, footerright, imageisattached, hdrstroke, linestroke, hdrstroketype, linestroketype, ispaintheaderlines ) = " + " (SELECT * FROM t_printtableformat)" +    // t WHERE t.ad_printtableformat_id = ad.ad_printtableformat_id)" +
                " WHERE ad.ad_printtableformat_id = " + m_reportDesign.getPrintFormat().getAD_PrintTableFormat_ID());
            execInsert = DB.executeUpdate( sql );
            Env.reset( false );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }
    }

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
        m_viewDesignPanel.ChangeByToolBar();

        if( m_printFormat.isForm()) {
            actualizar();
        } else {
            actualizarNotForm( m_viewDesignPanel.getTableDesignElementItemID());
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyTyped( KeyEvent e ) {

        // TODO Auto-generated method stub

    }

    /**
     * Descripción de Método
     *
     *
     * @param page
     */

    public void changePage( int page ) {
        revalidate();
        m_setting = true;
        m_pageNo  = page;

        if( m_pageNo < 1 ) {
            m_pageNo = 1;
        }

        if( page > m_pageMax ) {
            m_pageNo = m_pageMax;
        }

        Rectangle pageRectangle = m_viewDesignPanel.getRectangleOfPage( m_pageNo );

        pageRectangle.x -= ViewDesign.MARGIN;
        pageRectangle.y -= ViewDesign.MARGIN;

        MPrintFormat pf = m_reportDesign.getPrintFormat();

        centerScrollPane.getViewport().setViewPosition( pageRectangle.getLocation());

        // Set Page

        StringBuffer sb = new StringBuffer( Msg.getMsg( m_ctx,"Page" )).append( " " ).append( m_pageNo ).append( m_viewDesignPanel.getPageInfo( m_pageNo )).append( " " ).append( Msg.getMsg( m_ctx,"of" )).append( " " ).append( m_pageMax ).append( m_viewDesignPanel.getPageInfoMax());

        statusBar.setStatusDB( sb.toString());
        m_setting = false;
    }

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    static public void main( String[] args ) {
        Login.initTest( true );

        MQuery q = new MQuery( "C_Invoice" );

        q.addRestriction( "C_Invoice_ID",MQuery.EQUAL,new Integer( 103 ));

        // 102 = Invoice - 100 = Order

        PrintInfo    i  = new PrintInfo( "test",X_C_Invoice.Table_ID,102,0 );
        MPrintFormat f  = MPrintFormat.get( Env.getCtx(),102,false );
        ReportDesign rd = new ReportDesign( Env.getCtx(),f,q,i );

        // MPrintFormat f = new MPrintFormat(Env.getCtx(), 101);
        // ReportDesign rd = new ReportDesign(f, null);

        new ViewerDesign( rd );
    }    // main
}    // ViewerDesign



/*
 *  @(#)ViewerDesign.java   02.07.07
 * 
 *  Fin del fichero ViewerDesign.java
 *  
 *  Versión 2.2
 *
 */
