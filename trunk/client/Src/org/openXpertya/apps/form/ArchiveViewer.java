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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.PdfPanel;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VString;
import org.openXpertya.grid.ed.VText;
import org.openXpertya.model.MArchive;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
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

public class ArchiveViewer extends CTabbedPane implements FormPanel,ActionListener,VetoableChangeListener {

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "" );
        m_WindowNo = WindowNo;
        m_frame    = frame;

        try {
            dynInit();
            jbInit();
            frame.getContentPane().add( this,BorderLayout.CENTER );
            frame.getContentPane().add( confirmPanel,BorderLayout.SOUTH );

            //

            m_frame.setIconImage( Env.getImage( "Archive16.gif" ));
        } catch( Exception e ) {
            log.log( Level.SEVERE,"init",e );
        }
    }    // init

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private MArchive[] m_archives = new MArchive[ 0 ];

    /** Descripción de Campos */

    private int m_index = 0;

    /** Descripción de Campos */

    private int m_AD_Table_ID = 0;

    /** Descripción de Campos */

    private int m_Record_ID = 0;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ArchiveViewer.class );

    /** Descripción de Campos */

    private CPanel queryPanel = new CPanel( new GridBagLayout());

    /** Descripción de Campos */

    private CCheckBox reportField = new CCheckBox( Msg.translate( Env.getCtx(),"IsReport" ));

    /** Descripción de Campos */

    private CLabel processLabel = new CLabel( Msg.translate( Env.getCtx(),"AD_Process_ID" ));

    /** Descripción de Campos */

    private CComboBox processField = null;

    /** Descripción de Campos */

    private CLabel tableLabel = new CLabel( Msg.translate( Env.getCtx(),"AD_Table_ID" ));

    /** Descripción de Campos */

    private CComboBox tableField = null;

    /** Descripción de Campos */

    private CLabel bPartnerLabel = new CLabel( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));

    /** Descripción de Campos */

    private VLookup bPartnerField = null;

    /** Descripción de Campos */

    private CLabel nameQLabel = new CLabel( Msg.translate( Env.getCtx(),"Name" ));

    /** Descripción de Campos */

    private CTextField nameQField = new CTextField( 15 );

    /** Descripción de Campos */

    private CLabel descriptionQLabel = new CLabel( Msg.translate( Env.getCtx(),"Description" ));

    /** Descripción de Campos */

    private CTextField descriptionQField = new CTextField( 15 );

    /** Descripción de Campos */

    private CLabel helpQLabel = new CLabel( Msg.translate( Env.getCtx(),"Help" ));

    /** Descripción de Campos */

    private CTextField helpQField = new CTextField( 15 );

    /** Descripción de Campos */

    private CLabel createdByQLabel = new CLabel( Msg.translate( Env.getCtx(),"CreatedBy" ));

    /** Descripción de Campos */

    private CComboBox createdByQField = null;

    /** Descripción de Campos */

    private CLabel createdQLabel = new CLabel( Msg.translate( Env.getCtx(),"Created" ));

    /** Descripción de Campos */

    private VDate createdQFrom = new VDate();

    /** Descripción de Campos */

    private VDate createdQTo = new VDate();

    //

    /** Descripción de Campos */

    private CPanel viewPanel = new CPanel( new BorderLayout( 5,5 ));

    /** Descripción de Campos */

    private CPanel viewEnterPanel = new CPanel( new GridBagLayout());

    /** Descripción de Campos */

    private CButton bBack = new CButton( Env.getImageIcon( "wfBack24.gif" ));

    /** Descripción de Campos */

    private CButton bNext = new CButton( Env.getImageIcon( "wfNext24.gif" ));

    /** Descripción de Campos */

    private CLabel positionInfo = new CLabel( "." );

    /** Descripción de Campos */

    private CLabel createdByLabel = new CLabel( Msg.translate( Env.getCtx(),"CreatedBy" ));

    /** Descripción de Campos */

    private CTextField createdByField = new CTextField( 20 );

    /** Descripción de Campos */

    private CLabel createdLabel = new CLabel( Msg.translate( Env.getCtx(),"Created" ));

    /** Descripción de Campos */

    private VDate createdField = new VDate();

    //

    /** Descripción de Campos */

    private CLabel nameLabel = new CLabel( Msg.translate( Env.getCtx(),"Name" ));

    /** Descripción de Campos */

    private VString nameField = new VString( "Name",true,false,true,20,60,null,null );

    /** Descripción de Campos */

    private CLabel descriptionLabel = new CLabel( Msg.translate( Env.getCtx(),"Description" ));

    /** Descripción de Campos */

    private VText descriptionField = new VText( "Description",false,false,true,20,255 );

    /** Descripción de Campos */

    private CLabel helpLabel = new CLabel( Msg.translate( Env.getCtx(),"Help" ));

    /** Descripción de Campos */

    private VText helpField = new VText( "Help",false,false,true,20,2000 );

    /** Descripción de Campos */

    private CButton updateArchive = ConfirmPanel.createOKButton( Msg.getMsg( Env.getCtx(),"Update" ));

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private PdfPanel pdfpanel = null;

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {
        int AD_Role_ID = Env.getAD_Role_ID( Env.getCtx());

        // Processes

        String sql = "SELECT DISTINCT p.AD_Process_ID, p.Name " + "FROM AD_Process p INNER JOIN AD_Process_Access pa ON (p.AD_Process_ID=pa.AD_Process_ID) " + "WHERE pa.AD_Role_ID=" + AD_Role_ID + " AND p.IsReport='Y' AND p.IsActive='Y' AND pa.IsActive='Y' " + "ORDER BY 2";

        processField = new CComboBox( DB.getKeyNamePairs( sql,true ));

        // Tables

        sql = "SELECT DISTINCT t.AD_Table_ID, t.Name " + "FROM AD_Table t INNER JOIN AD_Tab tab ON (tab.AD_Table_ID=t.AD_Table_ID)" + " INNER JOIN AD_Window_Access wa ON (tab.AD_Window_ID=wa.AD_Window_ID) " + "WHERE wa.AD_Role_ID=" + AD_Role_ID + " AND t.IsActive='Y' AND tab.IsActive='Y' " + "ORDER BY 2";
        tableField = new CComboBox( DB.getKeyNamePairs( sql,true ));

        // Internal Users

        sql = "SELECT AD_User_ID, Name " + "FROM AD_User u WHERE EXISTS " + "(SELECT * FROM AD_User_Roles ur WHERE u.AD_User_ID=ur.AD_User_ID) " + "ORDER BY 2";
        createdByQField = new CComboBox( DB.getKeyNamePairs( sql,true ));

        //

        bPartnerField = VLookup.createBPartner( m_WindowNo );
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        int line = 0;

        queryPanel.add( reportField,new GridBagConstraints( 0,line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        reportField.addActionListener( this );

        //

        queryPanel.add( processLabel,new GridBagConstraints( 0,++line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        queryPanel.add( processField,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,0 ),0,0 ));
        queryPanel.add( bPartnerLabel,new GridBagConstraints( 0,++line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        queryPanel.add( bPartnerField,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,0 ),0,0 ));
        queryPanel.add( tableLabel,new GridBagConstraints( 0,++line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        queryPanel.add( tableField,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,0 ),0,0 ));

        //

        queryPanel.add( nameQLabel,new GridBagConstraints( 0,++line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 10,0,0,5 ),0,0 ));
        queryPanel.add( nameQField,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 10,0,0,0 ),0,0 ));
        queryPanel.add( descriptionQLabel,new GridBagConstraints( 0,++line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        queryPanel.add( descriptionQField,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,0 ),0,0 ));
        queryPanel.add( helpQLabel,new GridBagConstraints( 0,++line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        queryPanel.add( helpQField,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,0 ),0,0 ));

        //

        queryPanel.add( createdByQLabel,new GridBagConstraints( 0,++line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 10,0,0,5 ),0,0 ));
        queryPanel.add( createdByQField,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 10,0,0,0 ),0,0 ));
        queryPanel.add( createdQLabel,new GridBagConstraints( 0,++line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        queryPanel.add( createdQFrom,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,0 ),0,0 ));
        queryPanel.add( createdQTo,new GridBagConstraints( 2,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,0 ),0,0 ));
        this.add( queryPanel,"Peticion" );

        //
        //

        line = 0;

        //

        bBack.addActionListener( this );
        bNext.addActionListener( this );
        positionInfo.setFontBold( true );
        positionInfo.setHorizontalAlignment( CLabel.CENTER );
        viewEnterPanel.add( bBack,new GridBagConstraints( 0,line,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        viewEnterPanel.add( positionInfo,new GridBagConstraints( 1,line,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,5,0,5 ),0,0 ));
        viewEnterPanel.add( bNext,new GridBagConstraints( 2,line,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,0,0,5 ),0,0 ));

        //

        createdByField.setReadWrite( false );
        createdField.setReadWrite( false );
        nameField.addVetoableChangeListener( this );
        descriptionField.addVetoableChangeListener( this );
        helpField.addVetoableChangeListener( this );
        viewEnterPanel.add( createdByLabel,new GridBagConstraints( 0,++line,3,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        viewEnterPanel.add( createdByField,new GridBagConstraints( 0,++line,3,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 2,0,0,5 ),0,0 ));

        // viewEnterPanel.add(createdLabel, new ALayoutConstraint(line++,0));

        viewEnterPanel.add( createdField,new GridBagConstraints( 0,++line,3,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,0,5 ),0,0 ));

        //

        viewEnterPanel.add( nameLabel,new GridBagConstraints( 0,++line,3,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        viewEnterPanel.add( nameField,new GridBagConstraints( 0,++line,3,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 2,0,0,5 ),0,0 ));

        //

        viewEnterPanel.add( descriptionLabel,new GridBagConstraints( 0,++line,3,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        viewEnterPanel.add( descriptionField,new GridBagConstraints( 0,++line,3,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 2,0,0,5 ),0,0 ));

        //

        viewEnterPanel.add( helpLabel,new GridBagConstraints( 0,++line,3,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        viewEnterPanel.add( helpField,new GridBagConstraints( 0,++line,3,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 2,0,0,5 ),0,0 ));

        //

        viewEnterPanel.add( updateArchive,new GridBagConstraints( 0,++line,3,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));

        //

        viewEnterPanel.setPreferredSize( new Dimension( 220,500 ));
        updateArchive.addActionListener( this );
        viewPanel.add( viewEnterPanel,BorderLayout.CENTER );
        this.add( viewPanel,"Ver" );

        //

        confirmPanel.addActionListener( this );
        updateQDisplay();

        //

        this.setPreferredSize( new Dimension( 720,500 ));
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info( e.getActionCommand());

        //

        if( e.getSource() == updateArchive ) {
            cmd_updateArchive();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            if( getSelectedIndex() == 1 ) {
                dispose();
            } else {
                cmd_query();
            }
        } else if( e.getSource() == reportField ) {
            updateQDisplay();
        } else if( e.getSource() == bBack ) {
            updateVDisplay( false );
        } else if( e.getSource() == bNext ) {
            updateVDisplay( true );
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param evt
     *
     * @throws PropertyVetoException
     */

    public void vetoableChange( PropertyChangeEvent evt ) throws PropertyVetoException {
        if( m_archives.length > 0 ) {
            updateArchive.setEnabled( true );
        }
    }    // vetableChange

    /**
     * Descripción de Método
     *
     */

    private void updateQDisplay() {
        boolean reports = reportField.isSelected();

        log.config( "Reports=" + reports );

        // Show

        processLabel.setVisible( reports );
        processField.setVisible( reports );

        // Hide

        bPartnerLabel.setVisible( !reports );
        bPartnerField.setVisible( !reports );
    }    // updateQDisplay

    /**
     * Descripción de Método
     *
     *
     * @param next
     */

    private void updateVDisplay( boolean next ) {
        if( m_archives == null ) {
            m_archives = new MArchive[ 0 ];
        }

        if( next ) {
            m_index++;
        } else {
            m_index--;
        }

        if( m_index >= m_archives.length - 1 ) {
            m_index = m_archives.length - 1;
        }

        if( m_index < 0 ) {
            m_index = 0;
        }

        bBack.setEnabled( m_index > 0 );
        bNext.setEnabled( m_index < m_archives.length - 1 );
        updateArchive.setEnabled( false );

        //

        log.info( "Index=" + m_index + ", Length=" + m_archives.length );

        if( m_archives.length == 0 ) {
            positionInfo.setText( "No Record Found" );
            createdByField.setText( "" );
            createdField.setValue( null );
            nameField.setText( "" );
            descriptionField.setText( "" );
            helpField.setText( "" );

            if( pdfpanel != null ) {
                pdfpanel.dispose();
                pdfpanel = null;
            }

            return;
        }

        //

        positionInfo.setText( m_index + 1 + " of " + m_archives.length );

        MArchive ar = m_archives[ m_index ];

        createdByField.setText( ar.getCreatedByName());
        createdField.setValue( ar.getCreated());
        nameField.setText( ar.getName());
        descriptionField.setText( ar.getDescription());
        helpField.setText( ar.getHelp());

        //

        try {
            InputStream in = ar.getInputStream();

            if( in != null ) {
                if( pdfpanel != null ) {
                    pdfpanel.dispose();
                    pdfpanel = null;
                }

                int       salir = 1;
                byte[]    r     = new byte[ 1 ];
                ArrayList arl   = new ArrayList();

                while( salir == 1 ) {
                    salir = in.read( r );
                    arl.add( new Byte( r[ 0 ] ));
                }

                byte[] pdf = new byte[ arl.size()];

                for( int i = 0;i < arl.size();i++ ) {
                    pdf[ i ] = (( Byte )arl.get( i )).byteValue();
                }

                pdfpanel = PdfPanel.loadPdf( pdf,viewPanel,false,true,true,true,true,true );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"pdf",e );
        }
    }    // updateVDisplay

    /**
     * Descripción de Método
     *
     */

    private void cmd_updateArchive() {
        MArchive ar     = m_archives[ m_index ];
        boolean  update = false;

        if( !isSame( nameField.getText(),ar.getName())) {
            String newText = nameField.getText();

            if( (newText != null) && (newText.length() > 0) ) {
                ar.setName( newText );
                update = true;
            }
        }

        if( !isSame( descriptionField.getText(),ar.getDescription())) {
            ar.setDescription( descriptionField.getText());
            update = true;
        }

        if( !isSame( helpField.getText(),ar.getHelp())) {
            ar.setHelp( helpField.getText());
            update = true;
        }

        log.info( "Update=" + update );

        if( update ) {
            ar.save();
        }

        //

        m_index++;
        updateVDisplay( false );
    }    // cmd_updateArchive

    /**
     * Descripción de Método
     *
     *
     * @param s1
     * @param s2
     *
     * @return
     */

    private boolean isSame( String s1,String s2 ) {
        if( s1 == null ) {
            return s2 == null;
        } else if( s2 == null ) {
            return false;
        } else {
            return s1.equals( s2 );
        }
    }    // isSame

    /**
     * Descripción de Método
     *
     *
     * @param isReport
     * @param AD_Table_ID
     * @param Record_ID
     */

    public void query( boolean isReport,int AD_Table_ID,int Record_ID ) {
        log.config( "Report=" + isReport + ", AD_Table_ID=" + AD_Table_ID + ",Record_ID=" + Record_ID );
        reportField.setSelected( isReport );
        m_AD_Table_ID = AD_Table_ID;
        m_Record_ID   = Record_ID;
        cmd_query();
    }    // query

    /**
     * Descripción de Método
     *
     */

    private void cmd_query() {
        StringBuffer sql     = new StringBuffer();
        boolean      reports = reportField.isSelected();
        MRole        role    = MRole.getDefault();

        if( !role.isCanReport()) {
            log.warning( "User/Role cannot Report AD_User_ID=" + Env.getAD_User_ID( Env.getCtx()));

            return;
        }

        sql.append( " AND IsReport=" ).append( reports
                ?"'Y'"
                :"'N'" );

        // Process

        if( reports ) {
            KeyNamePair nn = ( KeyNamePair )processField.getSelectedItem();

            if( (nn != null) && (nn.getKey() > 0) ) {
                sql.append( " AND AD_Process_ID=" ).append( nn.getKey());
            }
        }

        // Table

        if( m_AD_Table_ID > 0 ) {
            sql.append( " AND ((AD_Table_ID=" ).append( m_AD_Table_ID );

            if( m_Record_ID > 0 ) {
                sql.append( " AND Record_ID=" ).append( m_Record_ID );
            }

            sql.append( ")" );

            if( (m_AD_Table_ID == MBPartner.Table_ID) && (m_Record_ID > 0) ) {
                sql.append( " OR C_BPartner_ID=" ).append( m_Record_ID );
            }

            sql.append( ")" );

            // Reset for query

            m_AD_Table_ID = 0;
            m_Record_ID   = 0;
        } else {
            KeyNamePair nn = ( KeyNamePair )tableField.getSelectedItem();

            if( (nn != null) && (nn.getKey() > 0) ) {
                sql.append( " AND AD_Table_ID=" ).append( nn.getKey());
            }
        }

        // Business Partner

        if( !reports ) {
            Integer ii = ( Integer )bPartnerField.getValue();

            if( ii != null ) {
                sql.append( " AND C_BPartner_ID=" ).append( ii );
            } else {
                sql.append( " AND C_BPartner_ID IS NOT NULL" );
            }
        }

        // Name

        String ss = nameQField.getText();

        if( (ss != null) && (ss.length() > 0) ) {
            if( (ss.indexOf( "%" ) != -1) || (ss.indexOf( "_" ) != -1) ) {
                sql.append( " AND Name LIKE " ).append( DB.TO_STRING( ss ));
            } else {
                sql.append( " AND Name=" ).append( DB.TO_STRING( ss ));
            }
        }

        // Description

        ss = descriptionQField.getText();

        if( (ss != null) && (ss.length() > 0) ) {
            if( (ss.indexOf( "%" ) != -1) || (ss.indexOf( "_" ) != -1) ) {
                sql.append( " AND Description LIKE " ).append( DB.TO_STRING( ss ));
            } else {
                sql.append( " AND Description=" ).append( DB.TO_STRING( ss ));
            }
        }

        // Help

        ss = helpQField.getText();

        if( (ss != null) && (ss.length() > 0) ) {
            if( (ss.indexOf( "%" ) != -1) || (ss.indexOf( "_" ) != -1) ) {
                sql.append( " AND Help LIKE " ).append( DB.TO_STRING( ss ));
            } else {
                sql.append( " AND Help=" ).append( DB.TO_STRING( ss ));
            }
        }

        // CreatedBy

        KeyNamePair nn = ( KeyNamePair )createdByQField.getSelectedItem();

        if( (nn != null) && (nn.getKey() > 0) ) {
            sql.append( " AND CreatedBy=" ).append( nn.getKey());
        }

        // Created

        Timestamp tt = createdQFrom.getTimestamp();

        if( tt != null ) {
            sql.append( " AND Created>=" ).append( DB.TO_DATE( tt,true ));
        }

        tt = createdQTo.getTimestamp();

        if( tt != null ) {
            sql.append( " AND Created<" ).append( DB.TO_DATE( TimeUtil.addDays( tt,1 ),true ));
        }

        log.fine( sql.toString());

        // Process Access

        sql.append( " AND (AD_Process_ID IS NULL OR AD_Process_ID IN " + "(SELECT AD_Process_ID FROM AD_Process_Access WHERE AD_Role_ID=" ).append( role.getAD_Role_ID()).append( "))" );

        // Table Access

        sql.append( " AND (AD_Table_ID IS NULL " + "OR (AD_Table_ID IS NOT NULL AND AD_Process_ID IS NOT NULL) "    // Menu Reports
                    + "OR AD_Table_ID IN " + "(SELECT t.AD_Table_ID FROM AD_Tab t" + " INNER JOIN AD_Window_Access wa ON (t.AD_Window_ID=wa.AD_Window_ID) " + "WHERE wa.AD_Role_ID=" ).append( role.getAD_Role_ID()).append( "))" );
        log.finest( sql.toString());

        //

        m_archives = MArchive.get( Env.getCtx(),sql.toString());
        log.info( "Length=" + m_archives.length );

        // Display

        this.setSelectedIndex( 1 );
        m_index = 1;
        updateVDisplay( false );
    }    // cmd_query
}    // ArchiveViewer



/*
 *  @(#)ArchiveViewer.java   02.07.07
 *
 *  Fin del fichero ArchiveViewer.java
 *
 *  Versión 2.2
 *
 */
