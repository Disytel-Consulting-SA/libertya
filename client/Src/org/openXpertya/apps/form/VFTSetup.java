package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.apps.ADialog;
import org.openXpertya.fastrack.FTConfiguration;
import org.openXpertya.fastrack.FastTracker;
import org.openXpertya.model.MFTSetup;
import org.openXpertya.model.MSetup;
import org.openXpertya.print.PrintUtil;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

public class VFTSetup extends VSetup {

	//Variables de instancia
	
	/** Label para la plantilla de compañía */
	
	private JLabel lblTemplate = new JLabel();
	
	/** Combo para las compañías plantilla */ 
	
	private JComboBox comboTemplate = new JComboBox();
	
	
	/**
	 * Inicializa el formulario openXpertya
	 */
	
	public void init( int WindowNo,FormFrame frame ) {
	       getLog().info( "VFTSetup.init" );
	        this.setM_WindowNo(WindowNo);
	        this.setM_frame(frame);

	        try {
	            jbInit();
	            dynInit();
	            frame.getContentPane().add( this.getCenterPane(),BorderLayout.CENTER );
	            frame.getContentPane().add( this.getConfirmPanel(),BorderLayout.SOUTH );
	        } catch( Exception e ) {
	            getLog().log( Level.SEVERE,"VFTSetup.init",e );
	        }
	  }    // init
	 
	
	/**
	 * Jbinit
	 */
	 
	 public void jbInit() throws Exception{
		 //Inicializo los componentes extra para fast-track
		 
		 //Label del cliente plantilla
		 this.getLblTemplate().setLabelFor(this.getComboTemplate());
		 this.getLblTemplate().setText(Msg.translate( Env.getCtx(),"ClientTemplate" ));
		 
		 //Inicializo los componentes del VSetup
		 super.jbInit();
		 
		 //Saco los componentes de los usuarios
		 this.getFUserClient().setVisible(false);
		 this.getFUserOrg().setVisible(false);
		 this.getLUserClient().setVisible(false);
		 this.getLUserOrg().setVisible(false);
	 }
	 
	 
	 /**
	  * DynInit
	  */
	 
	 public void dynInit(){
		 
		 //Inicializar el combo con las compañías template
		 
		 String sql = "SELECT ad_client_id, name FROM ad_client WHERE (isactive = 'Y')";
		 
		 Statement stmt = null;
		 ResultSet rs = null;
		 		 
		 try{
			 stmt = DB.createStatement();
	         rs   = stmt.executeQuery( sql );
	         
	         while(rs.next()){
	        	 this.getComboTemplate().addItem(new KeyNamePair(rs.getInt(1),rs.getString(2)));
	         }
			 
			 stmt.close();
			 rs.close();
		 }catch(SQLException e){
			 getLog().log(Level.SEVERE, "VFTSetup - Template Client",e);
		 }
		 
		 //Llamo a dynInit del padre para las demás componentes
		 super.dynInit();
	 }

	 /**
	  * Agrega los componentes a la interfaz
	  */
	 public void addComponentsToPanel(){
		getCenterPane().getViewport().add( getCenterPanel(),null );
		getCenterPanel().add( this.getLblTemplate(),new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
		getCenterPanel().add( this.getComboTemplate(),new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getLClientName(),new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        getCenterPanel().add( this.getFClientName(),new GridBagConstraints( 1,1,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,5,5 ),0,0 ));
        //getCenterPanel().add( this.getLOrgName(),new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        //getCenterPanel().add( this.getFOrgName(),new GridBagConstraints( 1,2,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getLAccountSeg(),new GridBagConstraints( 0,7,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( this.getFProject(),new GridBagConstraints( 1,8,3,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getFBPartner(),new GridBagConstraints( 1,7,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getFMCampaign(),new GridBagConstraints( 1,9,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getFCurrency(),new GridBagConstraints( 1,3,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getLCurrency(),new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( this.getButtonLoadAcct(),new GridBagConstraints( 1,10,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getLCountry(),new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( this.getFCountry(),new GridBagConstraints( 1,4,3,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getLCity(),new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( this.getFCity(),new GridBagConstraints( 1,5,3,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getFProduct(),new GridBagConstraints( 2,7,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,0 ),0,0 ));
        getCenterPanel().add( this.getFSRegion(),new GridBagConstraints( 2,9,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getLRegion(),new GridBagConstraints( 0,6,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( this.getFRegion(),new GridBagConstraints( 1,6,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        
        /*getCenterPanel().add( this.getLUserClient(),new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( this.getFUserClient(),new GridBagConstraints( 1,3,4,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        getCenterPanel().add( this.getLUserOrg(),new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,5,5,5 ),0,0 ));
        getCenterPanel().add( this.getFUserOrg(),new GridBagConstraints( 1,4,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        */
	 }
	 
	//Getters y Setters
	 
	public void setLblTemplate(JLabel lblTemplate) {
		this.lblTemplate = lblTemplate;
	}


	public JLabel getLblTemplate() {
		return lblTemplate;
	}


	public void setComboTemplate(JComboBox comboTemplate) {
		this.comboTemplate = comboTemplate;
	}


	public JComboBox getComboTemplate() {
		return comboTemplate;
	}
	
	
	//Métodos varios 
	 
	
	public boolean verifyUsers(){
		return true;
	}
	
	
	public void run(){
        MFTSetup ms = new MFTSetup( Env.getCtx(),getM_WindowNo() );

        getM_frame().setBusyTimer( 45 );

        // Step 1

        boolean ok = ms.createClient( getFClientName().getText(),getFOrgName().getText(),getFUserClient().getText(),getFUserOrg().getText());
        String info = ms.getInfo();

        if( ok ) {

            // Generate Accounting

            KeyNamePair currency = ( KeyNamePair )getFCurrency().getSelectedItem();

            if( !ms.createAccounting( currency,getFProduct().isSelected(),getFBPartner().isSelected(),getFProject().isSelected(),getFMCampaign().isSelected(),getFSRegion().isSelected(),this.getM_file())) {
                ADialog.error( getM_WindowNo(),this,"AccountSetupError" );
                dispose();
            }

            // Generate Entities

            KeyNamePair p            = ( KeyNamePair )getFCountry().getSelectedItem();
            int         C_Country_ID = p.getKey();

            p = ( KeyNamePair )getFRegion().getSelectedItem();

            int C_Region_ID = p.getKey();

            //ms.createEntities( C_Country_ID,getFCity().getText(),C_Region_ID,currency.getKey());
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
            
            // Fast - Track
            
            KeyNamePair template = (KeyNamePair)this.getComboTemplate().getSelectedItem();
            
            // FT - Configure
            
            FTConfiguration ftConfig = new FTConfiguration();
            ftConfig.setCountry_id(C_Country_ID);
            ftConfig.setRegion_id(C_Region_ID);
            ftConfig.setClient_template_id(template.getKey());
            ftConfig.setNew_client_id(ms.getM_client().getID());
            ftConfig.setC_currency_id(currency.getKey());
            ftConfig.setTrxName(ms.getM_trx().getTrxName());
            
            FastTracker fastracker = new FastTracker(ftConfig);
            
            fastracker.ejecutar();
            
            info += fastracker.getInfo();
            
        }

        ADialog.info( getM_WindowNo(),this,"VFTSetup - Fast-Track",info );
        dispose();
	}
	 
}
