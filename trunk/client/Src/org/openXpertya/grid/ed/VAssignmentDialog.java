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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.search.InfoSchedule;
import org.openXpertya.model.MResourceAssignment;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MUOMConversion;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VAssignmentDialog extends JDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param mAssignment
     * @param allowZoom
     * @param allowDelete
     */

    public VAssignmentDialog( Frame frame,MResourceAssignment mAssignment,boolean allowZoom,boolean allowDelete ) {
        super( frame,Msg.getMsg( Env.getCtx(),"VAssignmentDialog" ),true );
        log.config( mAssignment.toString());
        m_mAssignment = mAssignment;
        m_frame       = frame;

        try {
            jbInit();

            if( !allowZoom ) {
                confirmPanel.getZoomButton().setVisible( false );
            }

            delete.setVisible( allowDelete );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }

        setDisplay();    // from mAssignment

        //

        AEnv.showCenterScreen( this );
    }    // VAssignmentDialog

    /** Descripción de Campos */

    private MResourceAssignment m_mAssignment;

    /** Descripción de Campos */

    private Frame m_frame;

    /** Descripción de Campos */

    private boolean m_setting = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VAssignmentDialog.class );

    /** Descripción de Campos */

    private HashMap m_lookup = new HashMap();

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout mainLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel lResource = new CLabel( Msg.translate( Env.getCtx(),"S_Resource_ID" ));

    /** Descripción de Campos */

    private VComboBox fResource = new VComboBox( getResources());

    /** Descripción de Campos */

    private CLabel lDate = new CLabel( Msg.translate( Env.getCtx(),"DateFrom" ));

    /** Descripción de Campos */

    private VDate fDateFrom = new VDate( DisplayType.DateTime );

    /** Descripción de Campos */

    private CLabel lQty = new CLabel( Msg.translate( Env.getCtx(),"Qty" ));

    /** Descripción de Campos */

    private VNumber fQty = new VNumber();

    /** Descripción de Campos */

    private CLabel lUOM = new CLabel();

    /** Descripción de Campos */

    private CLabel lName = new CLabel( Msg.translate( Env.getCtx(),"Name" ));

    /** Descripción de Campos */

    private CLabel lDescription = new CLabel( Msg.translate( Env.getCtx(),"Description" ));

    /** Descripción de Campos */

    private CTextField fName = new CTextField( 30 );

    /** Descripción de Campos */

    private CTextField fDescription = new CTextField( 30 );

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true,false,false,false,false,true,true );

    /** Descripción de Campos */

    private JButton delete = ConfirmPanel.createDeleteButton( true );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        CompiereColor.setBackground( this );
        fResource.addActionListener( this );
        delete.addActionListener( this );
        confirmPanel.addButton( delete );
        confirmPanel.addActionListener( this );

        //

        mainPanel.setLayout( mainLayout );
        mainPanel.add( lResource,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 8,8,4,4 ),0,0 ));
        mainPanel.add( fResource,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 8,0,4,4 ),0,0 ));
        mainPanel.add( lDate,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,8,4,4 ),0,0 ));
        mainPanel.add( fDateFrom,new GridBagConstraints( 1,1,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,0,4,8 ),100,0 ));
        mainPanel.add( lQty,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,8,4,4 ),0,0 ));
        mainPanel.add( fQty,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 2,0,4,4 ),0,0 ));
        mainPanel.add( lUOM,new GridBagConstraints( 2,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,4,4,8 ),0,0 ));
        mainPanel.add( lName,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,8,4,4 ),0,0 ));
        mainPanel.add( lDescription,new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,8,8,4 ),0,0 ));
        mainPanel.add( fName,new GridBagConstraints( 1,3,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,0,4,8 ),0,0 ));
        mainPanel.add( fDescription,new GridBagConstraints( 1,4,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,0,8,8 ),0,0 ));

        //

        this.getContentPane().add( mainPanel,BorderLayout.CENTER );
        this.getContentPane().add( confirmPanel,BorderLayout.SOUTH );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void setDisplay() {
        m_setting = true;

        // Set Resource

        int           S_Resource_ID = m_mAssignment.getS_Resource_ID();
        KeyNamePair[] resources     = new KeyNamePair[ m_lookup.size()];

        m_lookup.keySet().toArray( resources );

        for( int i = 0;i < resources.length;i++ ) {
            if( resources[ i ].getKey() == S_Resource_ID ) {
                fResource.setSelectedItem( resources[ i ] );

                break;
            }
        }

        KeyNamePair check = ( KeyNamePair )fResource.getSelectedItem();

        if( (check == null) || (check.getKey() != S_Resource_ID) ) {
            if( m_mAssignment.getS_ResourceAssignment_ID() == 0 ) {    // new record select first
                fResource.setSelectedItem( fResource.getSelectedItem());    // initiates UOM display
            } else {
                log.log( Level.SEVERE,"Resource not found ID=" + S_Resource_ID );
            }
        }

        // Set Date, Qty

        fDateFrom.setValue( m_mAssignment.getAssignDateFrom());
        fQty.setValue( m_mAssignment.getQty());

        // Name, Description

        fName.setValue( m_mAssignment.getName());
        fDescription.setValue( m_mAssignment.getDescription());

        // Set Editor to R/O if confirmed

        boolean readWrite = true;

        if( m_mAssignment.isConfirmed()) {
            readWrite = false;
        }

        confirmPanel.getCancelButton().setVisible( readWrite );
        fResource.setReadWrite( readWrite );
        fDateFrom.setReadWrite( readWrite );
        fQty.setReadWrite( readWrite );
        m_setting = false;
    }    // dynInit

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

        // Update Assignment

        KeyNamePair resource = ( KeyNamePair )fResource.getSelectedItem();

        if( resource != null ) {
            int S_Resource_ID = resource.getKey();

            m_mAssignment.setS_Resource_ID( S_Resource_ID );
        }

        Timestamp assignDateFrom = fDateFrom.getTimestamp();

        if( assignDateFrom != null ) {
            m_mAssignment.setAssignDateFrom( assignDateFrom );
        }

        BigDecimal qty = ( BigDecimal )fQty.getValue();

        if( qty != null ) {
            m_mAssignment.setQty( qty );
        }

        m_mAssignment.setName(( String )fName.getValue());
        m_mAssignment.setDescription(( String )fDescription.getValue());

        // Resource - Look up UOM

        if( e.getSource() == fResource ) {
            Object o = m_lookup.get( fResource.getSelectedItem());

            if( o == null ) {
                lUOM.setText( " ? " );
            } else {
                lUOM.setText( o.toString());
            }
        }

        // Zoom - InfoResource

        else if( e.getActionCommand().equals( ConfirmPanel.A_ZOOM )) {
            InfoSchedule is = new InfoSchedule( m_frame,m_mAssignment,true );

            if( is.getMResourceAssignment() != null ) {
                m_mAssignment = is.getMResourceAssignment();

                // setDisplay();

                dispose();
            }

            is = null;
        }

        // cancel - return

        else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        }

        // delete - delete and return

        else if( e.getActionCommand().equals( ConfirmPanel.A_DELETE )) {
            if( m_mAssignment.delete( true )) {
                m_mAssignment = null;
                dispose();
            } else {
                ADialog.error( 0,this,"ResourceAssignmentNotDeleted" );
            }
        }

        // OK - Save

        else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            if( cmd_save()) {
                dispose();
            }
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MResourceAssignment getMResourceAssignment() {
        return m_mAssignment;
    }    // getMResourceAssignment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean cmd_save() {
        log.config( "VAssignment.cmd_save" );

        // Set AssignDateTo

        Timestamp   assignDateFrom = fDateFrom.getTimestamp();
        BigDecimal  qty            = ( BigDecimal )fQty.getValue();
        KeyNamePair uom            = ( KeyNamePair )m_lookup.get( fResource.getSelectedItem());
        int minutes = MUOMConversion.convertToMinutes( Env.getCtx(),uom.getKey(),qty );
        Timestamp assignDateTo = TimeUtil.addMinutess( assignDateFrom,minutes );

        m_mAssignment.setAssignDateTo( assignDateTo );

        //
        // m_mAssignment.dump();

        return m_mAssignment.save();
    }    // cmdSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private KeyNamePair[] getResources() {
        if( m_lookup.size() == 0 ) {
            String sql = MRole.getDefault().addAccessSQL( "SELECT r.S_Resource_ID, r.Name, r.IsActive,"    // 1..3
                + "uom.C_UOM_ID,uom.UOMSymbol "    // 4..5
                + "FROM S_Resource r, S_ResourceType rt, C_UOM uom " + "WHERE r.S_ResourceType_ID=rt.S_ResourceType_ID AND rt.C_UOM_ID=uom.C_UOM_ID","r",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );
                ResultSet         rs    = pstmt.executeQuery();

                while( rs.next()) {
                    StringBuffer sb = new StringBuffer( rs.getString( 2 ));

                    if( !"Y".equals( rs.getString( 3 ))) {
                        sb.insert( 0,'~' ).append( '~' );    // inactive marker
                    }

                    // Key             S_Resource_ID/Name

                    KeyNamePair key = new KeyNamePair( rs.getInt( 1 ),sb.toString());

                    // Value   C_UOM_ID/Name

                    KeyNamePair value = new KeyNamePair( rs.getInt( 4 ),rs.getString( 5 ).trim());

                    m_lookup.put( key,value );
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,sql,e );
            }
        }

        // Convert to Array

        KeyNamePair[] retValue = new KeyNamePair[ m_lookup.size()];

        m_lookup.keySet().toArray( retValue );
        Arrays.sort( retValue );

        return retValue;
    }    // getResources
}    // VAssignmentDialog



/*
 *  @(#)VAssignmentDialog.java   02.07.07
 * 
 *  Fin del fichero VAssignmentDialog.java
 *  
 *  Versión 2.2
 *
 */
