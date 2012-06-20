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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.model.MSetup;
import org.openXpertya.print.PrintUtil;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ExtensionFileFilter;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class VSetup extends CPanel implements FormPanel,ActionListener,Runnable {

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        getLog().info( "VSetup.init" );
        setM_WindowNo(WindowNo);
        setM_frame(frame);

        try {
            jbInit();
            dynInit();
            frame.getContentPane().add( getCenterPane(),BorderLayout.CENTER );
            frame.getContentPane().add( getConfirmPanel(),BorderLayout.SOUTH );
        } catch( Exception e ) {
            getLog().log( Level.SEVERE,"VSetup.init",e );
        }
    }    // init

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /* Natural Account file */

    /** Descripción de Campos */

    private File m_file = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VSetup.class );

    //

    /** Descripción de Campos */

    private JScrollPane centerPane = new JScrollPane();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout centerLayout = new GridBagLayout();

    /** Descripción de Campos */

    private JLabel lClientName = new JLabel();

    /** Descripción de Campos */

    private JTextField fClientName = new JTextField();

    /** Descripción de Campos */

    private JLabel lOrgName = new JLabel();

    /** Descripción de Campos */

    private JTextField fOrgName = new JTextField();

    /** Descripción de Campos */

    private JLabel lCurrency = new JLabel();

    /** Descripción de Campos */

    private JComboBox fCurrency = new JComboBox();

    /** Descripción de Campos */

    private JLabel lUserClient = new JLabel();

    /** Descripción de Campos */

    private JTextField fUserClient = new JTextField();

    /** Descripción de Campos */

    private JLabel lUserOrg = new JLabel();

    /** Descripción de Campos */

    private JTextField fUserOrg = new JTextField();

    /** Descripción de Campos */

    private JCheckBox fProject = new JCheckBox();

    /** Descripción de Campos */

    private JCheckBox fProduct = new JCheckBox();

    /** Descripción de Campos */

    private JCheckBox fBPartner = new JCheckBox();

    /** Descripción de Campos */

    private JLabel lAccountSeg = new JLabel();

    /** Descripción de Campos */

    private JCheckBox fMCampaign = new JCheckBox();

    /** Descripción de Campos */

    private JCheckBox fSRegion = new JCheckBox();

    /** Descripción de Campos */

    private JButton buttonLoadAcct = new JButton();

    /** Descripción de Campos */

    private JLabel lCountry = new JLabel();

    /** Descripción de Campos */

    private JLabel lCity = new JLabel();

    /** Descripción de Campos */

    private JComboBox fCountry = new JComboBox();

    /** Descripción de Campos */

    private JTextField fCity = new JTextField();

    /** Descripción de Campos */

    private JLabel lRegion = new JLabel();

    /** Descripción de Campos */

    private JComboBox fRegion = new JComboBox();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    public void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        getCenterPanel().setLayout( centerLayout );

        String optional = Msg.translate( Env.getCtx(),"Optional" );

        //

        getLClientName().setLabelFor( getFClientName() );
        getLClientName().setText( Msg.translate( Env.getCtx(),"AD_Client_ID" ));

        // lClientName.setToolTipText("");

        getFClientName().setText( "Compañia" );
        getFClientName().setColumns( 20 );

        //

        getLOrgName().setLabelFor( getFOrgName() );
        getLOrgName().setText( Msg.translate( Env.getCtx(),"AD_Org_ID" ));
        getFOrgName().setText( "Organizacion" );
        getFOrgName().setColumns( 20 );

        //

        getLCurrency().setLabelFor( getFCurrency() );
        getLCurrency().setText( Msg.translate( Env.getCtx(),"C_Currency_ID" ));

        // lCurrency.setToolTipText("");
        //

        getLUserClient().setLabelFor( getFUserClient() );
        getLUserClient().setText( Msg.parseTranslation( Env.getCtx(),"@AD_User_ID@ @AD_Client_ID@" ));

        // lUserClient.setToolTipText("User name for client level access");

        getFUserClient().setText( "AdminCompañia" );
        getFUserClient().setColumns( 20 );

        //

        getLUserOrg().setLabelFor( getFUserOrg() );
        getLUserOrg().setText( Msg.parseTranslation( Env.getCtx(),"@AD_User_ID@ @AD_Org_ID@" ));

        // lUserOrg.setToolTipText("");

        getFUserOrg().setText( "UsuarioCompañia" );
        getFUserOrg().setColumns( 20 );

        //

        getLCountry().setLabelFor( getFCountry() );
        getLCountry().setText( Msg.translate( Env.getCtx(),"C_Country_ID" ));
        getLCity().setLabelFor( getFCity() );
        getLCity().setText( Msg.translate( Env.getCtx(),"C_City_ID" ));
        getFCity().setText( "Ciudad" );
        getFCity().setColumns( 20 );
        getLRegion().setLabelFor( getFRegion() );
        getLRegion().setText( Msg.translate( Env.getCtx(),"C_Region_ID" ));
        getLRegion().setToolTipText( optional );

        //

        getLAccountSeg().setText( optional );
        getFBPartner().setSelected( true );
        getFBPartner().setText( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
        getFProduct().setSelected( true );
        getFProduct().setText( Msg.translate( Env.getCtx(),"M_Product_ID" ));
        getFProject().setText( Msg.translate( Env.getCtx(),"C_Project_ID" ));
        getFMCampaign().setText( Msg.translate( Env.getCtx(),"C_Campaign_ID" ));
        getFSRegion().setText( Msg.translate( Env.getCtx(),"C_SalesRegion_ID" ));

        //

        getButtonLoadAcct().setText( Msg.getMsg( Env.getCtx(),"LoadAccountingValues" ));
        this.addComponentsToPanel();
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    public void dynInit() {

        // Currency

        String sql = "SELECT C_Currency_ID, Description FROM C_Currency ORDER BY IsActive DESC";    // USD first

        try {
            Statement stmt = DB.createStatement();
            ResultSet rs   = stmt.executeQuery( sql );

            while( rs.next()) {
                getFCurrency().addItem( new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 )));
            }

            rs.close();
            stmt.close();
        } catch( SQLException e1 ) {
            getLog().log( Level.SEVERE,"VSetup.dynInit -currency",e1 );
        }

        getFCurrency().setSelectedIndex( 0 );

        // Country

        sql = "SELECT C_Country_ID, Name FROM C_Country ORDER BY IsActive DESC";    // US first

        try {
            Statement stmt = DB.createStatement();
            ResultSet rs   = stmt.executeQuery( sql );

            while( rs.next()) {
                getFCountry().addItem( new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 )));
            }

            rs.close();
            stmt.close();
        } catch( SQLException e1 ) {
            getLog().log( Level.SEVERE,"VSetup.dynInit -country",e1 );
        }

        getFCountry().setSelectedIndex( 0 );

        // Region (optional)

        sql = "SELECT C_Region_ID, Name FROM C_Region WHERE IsActive = 'Y' ORDER BY C_Country_ID, Name";

        try {
            getFRegion().addItem( new KeyNamePair( 0," " ));

            Statement stmt = DB.createStatement();
            ResultSet rs   = stmt.executeQuery( sql );

            while( rs.next()) {
                getFRegion().addItem( new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 )));
            }

            rs.close();
            stmt.close();
        } catch( SQLException e1 ) {
            getLog().log( Level.SEVERE,"VSetup.dynInit -region",e1 );
        }

        getFRegion().setSelectedIndex( 0 );

        // General Listeners

        getConfirmPanel().addActionListener( this );
        getButtonLoadAcct().addActionListener( this );
        getConfirmPanel().getOKButton().setEnabled( false );
    }    // dynInit

    
    /**
     * Add every components to main panel
     */
    
    public void addComponentsToPanel(){
    	getCenterPane().getViewport().add( getCenterPanel(),null );
        getCenterPanel().add( getLClientName(),new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        getCenterPanel().add( getFClientName(),new GridBagConstraints( 1,0,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,5,5 ),0,0 ));
        getCenterPanel().add( getLOrgName(),new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( getFOrgName(),new GridBagConstraints( 1,1,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getLUserClient(),new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( getFUserClient(),new GridBagConstraints( 1,2,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getLUserOrg(),new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( getFUserOrg(),new GridBagConstraints( 1,3,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getLAccountSeg(),new GridBagConstraints( 0,8,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( getFProject(),new GridBagConstraints( 1,9,3,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getFBPartner(),new GridBagConstraints( 1,8,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getFMCampaign(),new GridBagConstraints( 1,10,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getFCurrency(),new GridBagConstraints( 1,4,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getLCurrency(),new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( getButtonLoadAcct(),new GridBagConstraints( 1,11,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,5,5 ),0,0 ));
        getCenterPanel().add( getLCountry(),new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( getLCity(),new GridBagConstraints( 0,6,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( getFCountry(),new GridBagConstraints( 1,5,3,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getFCity(),new GridBagConstraints( 1,6,3,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getFProduct(),new GridBagConstraints( 2,8,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,0 ),0,0 ));
        getCenterPanel().add( getFSRegion(),new GridBagConstraints( 2,10,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( getLRegion(),new GridBagConstraints( 0,7,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( getFRegion(),new GridBagConstraints( 1,7,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
    }
  

    //Getters y Setters
    
    public void dispose() {
        if( getM_frame() != null ) {
            getM_frame().dispose();
        }

        setM_frame(null);
    }    // dispose

    public void setCenterPanel(CPanel centerPanel) {
		this.centerPanel = centerPanel;
	}

	public CPanel getCenterPanel() {
		return centerPanel;
	}

	public static void setLog(CLogger log) {
		VSetup.log = log;
	}

	public static CLogger getLog() {
		return log;
	}

	public void setM_WindowNo(int m_WindowNo) {
		this.m_WindowNo = m_WindowNo;
	}

	public int getM_WindowNo() {
		return m_WindowNo;
	}

	public void setM_frame(FormFrame m_frame) {
		this.m_frame = m_frame;
	}

	public FormFrame getM_frame() {
		return m_frame;
	}

	public void setCenterPane(JScrollPane centerPane) {
		this.centerPane = centerPane;
	}

	public JScrollPane getCenterPane() {
		return centerPane;
	}

	public void setConfirmPanel(ConfirmPanel confirmPanel) {
		this.confirmPanel = confirmPanel;
	}

	public ConfirmPanel getConfirmPanel() {
		return confirmPanel;
	}

	public void setLClientName(JLabel lClientName) {
		this.lClientName = lClientName;
	}

	public JLabel getLClientName() {
		return lClientName;
	}

	public void setFClientName(JTextField fClientName) {
		this.fClientName = fClientName;
	}

	public JTextField getFClientName() {
		return fClientName;
	}

	public void setLOrgName(JLabel lOrgName) {
		this.lOrgName = lOrgName;
	}

	public JLabel getLOrgName() {
		return lOrgName;
	}

	public void setFOrgName(JTextField fOrgName) {
		this.fOrgName = fOrgName;
	}

	public JTextField getFOrgName() {
		return fOrgName;
	}

	public void setLCurrency(JLabel lCurrency) {
		this.lCurrency = lCurrency;
	}

	public JLabel getLCurrency() {
		return lCurrency;
	}

	public void setFCurrency(JComboBox fCurrency) {
		this.fCurrency = fCurrency;
	}

	public JComboBox getFCurrency() {
		return fCurrency;
	}

	public void setLUserClient(JLabel lUserClient) {
		this.lUserClient = lUserClient;
	}

	public JLabel getLUserClient() {
		return lUserClient;
	}

	public void setFUserClient(JTextField fUserClient) {
		this.fUserClient = fUserClient;
	}

	public JTextField getFUserClient() {
		return fUserClient;
	}

	public void setLUserOrg(JLabel lUserOrg) {
		this.lUserOrg = lUserOrg;
	}

	public JLabel getLUserOrg() {
		return lUserOrg;
	}

	public void setFUserOrg(JTextField fUserOrg) {
		this.fUserOrg = fUserOrg;
	}

	public JTextField getFUserOrg() {
		return fUserOrg;
	}

	public void setFProject(JCheckBox fProject) {
		this.fProject = fProject;
	}

	public JCheckBox getFProject() {
		return fProject;
	}

	public void setFProduct(JCheckBox fProduct) {
		this.fProduct = fProduct;
	}

	public JCheckBox getFProduct() {
		return fProduct;
	}

	public void setFBPartner(JCheckBox fBPartner) {
		this.fBPartner = fBPartner;
	}

	public JCheckBox getFBPartner() {
		return fBPartner;
	}

	public void setLAccountSeg(JLabel lAccountSeg) {
		this.lAccountSeg = lAccountSeg;
	}

	public JLabel getLAccountSeg() {
		return lAccountSeg;
	}

	public void setFMCampaign(JCheckBox fMCampaign) {
		this.fMCampaign = fMCampaign;
	}

	public JCheckBox getFMCampaign() {
		return fMCampaign;
	}

	public void setFSRegion(JCheckBox fSRegion) {
		this.fSRegion = fSRegion;
	}

	public JCheckBox getFSRegion() {
		return fSRegion;
	}

	public void setButtonLoadAcct(JButton buttonLoadAcct) {
		this.buttonLoadAcct = buttonLoadAcct;
	}

	public JButton getButtonLoadAcct() {
		return buttonLoadAcct;
	}

	public void setLCountry(JLabel lCountry) {
		this.lCountry = lCountry;
	}

	public JLabel getLCountry() {
		return lCountry;
	}

	public void setLCity(JLabel lCity) {
		this.lCity = lCity;
	}

	public JLabel getLCity() {
		return lCity;
	}

	public void setFCountry(JComboBox fCountry) {
		this.fCountry = fCountry;
	}

	public JComboBox getFCountry() {
		return fCountry;
	}

	public void setFCity(JTextField fCity) {
		this.fCity = fCity;
	}

	public JTextField getFCity() {
		return fCity;
	}

	public void setLRegion(JLabel lRegion) {
		this.lRegion = lRegion;
	}

	public JLabel getLRegion() {
		return lRegion;
	}

	public void setFRegion(JComboBox fRegion) {
		this.fRegion = fRegion;
	}

	public JComboBox getFRegion() {
		return fRegion;
	}

	public void setM_file(File m_file) {
		this.m_file = m_file;
	}

	public File getM_file() {
		return m_file;
	}

	/**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // load file

        if( e.getSource().equals( getButtonLoadAcct() )) {
            setM_file(getFile());

            // OK

        } else if( e.getActionCommand().equals( ConfirmPanel.A_OK ) && (getM_file() != null) ) {
            getConfirmPanel().getCancelButton().setEnabled( false );
            getConfirmPanel().getOKButton().setEnabled( false );

            if( createSetup()) {
                getM_frame().startBatch( this );
            } else {
                getConfirmPanel().getCancelButton().setEnabled( true );
                getConfirmPanel().getOKButton().setEnabled( true );
            }
        }

        // Cancel

        else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public File getFile() {
        File   file    = null;
        String dirName = org.openXpertya.OpenXpertya.getOXPHome() + File.separator + "data" + File.separator + "import";

        getLog().config( dirName );

        JFileChooser chooser = new JFileChooser( dirName );

        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( false );
        chooser.setDialogTitle( Msg.translate( Env.getCtx(),"LoadAccountingValues" ));
        chooser.addChoosableFileFilter( new ExtensionFileFilter( "csv",Msg.getMsg( Env.getCtx(),"FileCSV" )));

        // Try selecting file

        file = new File( dirName + File.pathSeparator + "AccountingES.csv" );

        if( file.exists()) {
            chooser.setSelectedFile( file );
        }

        // Show it

        if( chooser.showOpenDialog( this.getParent()) == JFileChooser.APPROVE_OPTION ) {
            file = chooser.getSelectedFile();
        } else {
            file = null;
        }

        chooser = null;

        if( file == null ) {
            getButtonLoadAcct().setText( Msg.translate( Env.getCtx(),"LoadAccountingValues" ));
        } else {
            getButtonLoadAcct().setText( file.getAbsolutePath());
        }

        getConfirmPanel().getOKButton().setEnabled( file != null );
        getM_frame().pack();

        return file;
    }    // getFile

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean createSetup() {

        // Change critical characters ' => "  \ => /

        getFClientName().setText( getFClientName().getText().replace( '\'','"' ));
        getFClientName().setText( getFClientName().getText().replace( '\\','/' ));
        getFOrgName().setText( getFOrgName().getText().replace( '\'','"' ));
        getFOrgName().setText( getFOrgName().getText().replace( '\\','/' ));
        getFUserClient().setText( getFUserClient().getText().replace( '\'','"' ));
        getFUserClient().setText( getFUserClient().getText().replace( '\\','/' ));
        getFUserOrg().setText( getFUserOrg().getText().replace( '\'','"' ));
        getFUserOrg().setText( getFUserOrg().getText().replace( '\\','/' ));

        // Unique Client Name

        String SQL = "UPDATE AD_CLient SET CreatedBy=0 WHERE Name='" + getFClientName().getText() + "'";

        if( DB.executeUpdate( SQL ) != 0 ) {
            getFClientName().setBackground( CompierePLAF.getFieldBackground_Error());
            ADialog.error( getM_WindowNo(),this,"NotUnique",getLClientName().getText());
            getFClientName().requestFocus();

            return false;
        }

        getFClientName().setBackground( CompierePLAF.getFieldBackground_Normal());

        if(!this.verifyUsers()){
        	return false;
        }
        
        return true;
    }    // createSetup

    
    /**
     * Verifica que los usuario no estén creados
     * @return éxito del procedimiento
     */
    
    public boolean verifyUsers(){
    	// Unique User Name

        String SQL = "UPDATE AD_User SET CreatedBy=0 WHERE Name='" + getFUserClient().getText() + "'";

        if( DB.executeUpdate( SQL ) != 0 ) {
            getFUserClient().setBackground( CompierePLAF.getFieldBackground_Error());
            ADialog.error( getM_WindowNo(),this,"NotUnique",getLUserClient().getText());
            getFUserClient().requestFocus();

            return false;
        }

        getFUserClient().setBackground( CompierePLAF.getFieldBackground_Normal());
        SQL = "UPDATE AD_User SET CreatedBy=0 WHERE Name='" + getFUserOrg().getText() + "'";

        if( (DB.executeUpdate( SQL ) != 0) || getFUserClient().getText().equals( getFUserOrg().getText())) {
            getFUserOrg().setBackground( CompierePLAF.getFieldBackground_Error());
            ADialog.error( getM_WindowNo(),this,"NotUnique",getLUserOrg().getText());
            getFUserOrg().requestFocus();

            return false;
        }

        getFUserOrg().setBackground( CompierePLAF.getFieldBackground_Normal());
        
        return true;

    }
    
    /**
     * Descripción de Método
     *
     */

    public void run() {
        MSetup ms = new MSetup( Env.getCtx(),getM_WindowNo() );

        getM_frame().setBusyTimer( 45 );

        // Step 1

        boolean ok = ms.createClient( getFClientName().getText(),getFOrgName().getText(),getFUserClient().getText(),getFUserOrg().getText());
        String info = ms.getInfo();

        if( ok ) {

            // Generate Accounting

            KeyNamePair currency = ( KeyNamePair )getFCurrency().getSelectedItem();

            if( !ms.createAccounting( currency,getFProduct().isSelected(),getFBPartner().isSelected(),getFProject().isSelected(),getFMCampaign().isSelected(),getFSRegion().isSelected(),getM_file() )) {
                ADialog.error( getM_WindowNo(),this,"AccountSetupError" );
                dispose();
            }

            // Generate Entities

            KeyNamePair p            = ( KeyNamePair )getFCountry().getSelectedItem();
            int         C_Country_ID = p.getKey();

            p = ( KeyNamePair )getFRegion().getSelectedItem();

            int C_Region_ID = p.getKey();

            if(!ms.createEntities( C_Country_ID,getFCity().getText(),C_Region_ID,currency.getKey())) {
                ValueNamePair msg = CLogger.retrieveError();
            	String msgTitle = "ClientSetupError";
            	String msgDesc = Msg.translate(Env.getCtx(), "SeeTheLog");
            	if (msg != null) {
            		msgTitle = msg.getValue();
            		msgDesc = (msg.getName() != null && msg.getName().length()>0 ? msg.getName() : msgDesc);
            	}
                ADialog.error( getM_WindowNo(),this, msgTitle, msgDesc);
                dispose();
            	return;
            }
            info += ms.getInfo();

            // Create Print Documents

            PrintUtil.setupPrintForm( ms.getAD_Client_ID());
        }

        ADialog.info( getM_WindowNo(),this,"VSetup",info );
        dispose();
    }    // run
}    // VSetup



/*
 *  @(#)VSetup.java   02.07.07
 *
 *  Fin del fichero VSetup.java
 *
 *  Versión 2.2
 *
 */
