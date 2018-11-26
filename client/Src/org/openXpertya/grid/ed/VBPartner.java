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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MField;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MLocationLookup;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MUser;
import org.openXpertya.model.X_C_BP_Group;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VBPartner extends CDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param WindowNo
     */

    public VBPartner( Frame frame,int WindowNo ) {
        super( frame,Msg.translate( Env.getCtx(),"C_BPartner_ID" ),true );
        m_WindowNo = WindowNo;
		m_readOnly = !MRole.getDefault().canUpdate(clientID, Env.getAD_Org_ID(getCtx()),
				MBPartner.Table_ID, false);
        setCompoLabels(new HashMap<JComponent, JLabel>());

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,ex.getMessage());
        }

        initBPartner();

        postInit();

        AEnv.positionCenterWindow( frame,this );
    }    // VBPartner

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private MBPartner m_partner = null;

    /** Descripción de Campos */

    private MBPartnerLocation m_pLocation = null;

    /** Descripción de Campos */

    private MUser m_user = null;

    /** Descripción de Campos */

    private boolean m_readOnly = false;

    /** Descripción de Campos */

    private Insets m_labelInsets = new Insets( 2,15,2,0 );    // top,left,bottom,right

    /** Descripción de Campos */

    private Insets m_fieldInsets = new Insets( 2,5,2,10 );    // top,left,bottom,right

    /** Descripción de Campos */

    private GridBagConstraints m_gbc = new GridBagConstraints();

    /** Descripción de Campos */

    private int m_line;

    /** Descripción de Campos */

    private Object[] m_greeting;
    
    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VBPartner.class );

    //

    /** Descripción de Campos */

    protected VString fValue,fName,fName2,fContact,fTitle,fPhone,fFax,fPhone2,fEMail,fCIF,fTaxID;
    
    protected VLookup fTaxIdType;

    /** Descripción de Campos */

    protected VLocation fAddress;

    /** Descripción de Campos */

    protected JComboBox fGreetingBP,fGreetingC;
    
    protected VLookup fPartnerGroup;

    /** Descripción de Campos */

    protected CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    protected BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    protected CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    protected CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    protected GridBagLayout centerLayout = new GridBagLayout();

    /** Descripción de Campos */

    protected ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();
    
    protected VLookup fCategoriaIVA = null;
    
    private boolean m_localeARActive  = CalloutInvoiceExt.ComprobantesFiscalesActivos();

    private Properties ctx = Env.getCtx();
    private String trxName;
    private int clientID = Env.getAD_Client_ID(Env.getCtx());
    
    private Map<JComponent, JLabel> compoLabels;
    
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        mainPanel.setLayout( mainLayout );
        southPanel.setLayout( southLayout );
        centerPanel.setLayout( centerLayout );
        mainLayout.setVgap( 5 );
        getContentPane().add( mainPanel );
        mainPanel.add( centerPanel,BorderLayout.CENTER );
        mainPanel.add( southPanel,BorderLayout.SOUTH );
        southPanel.add( confirmPanel,BorderLayout.CENTER );

        //

        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void initBPartner() {

        // Get Data

        m_greeting = fillGreeting();
        
        // Display

        m_gbc.anchor    = GridBagConstraints.NORTHWEST;
        m_gbc.gridx     = 0;
        m_gbc.gridy     = 0;
        m_gbc.gridwidth = 1;
        m_gbc.weightx   = 0;
        m_gbc.weighty   = 0;
        m_gbc.fill      = GridBagConstraints.HORIZONTAL;
        m_gbc.ipadx     = 0;
        m_gbc.ipady     = 0;
        m_line          = 0;

        // Greeting Business Partner

        fGreetingBP = new JComboBox( m_greeting );
        fGreetingBP.setPreferredSize(new Dimension(60,20));
        createLine( fGreetingBP,"Greeting",true );

        // Value

        fValue = new VString( "Value",true,false,true,30,60,"",null );
        fValue.addActionListener( this );
        createLine( fValue,"Value",false );
        
        // CIF
        
        fCIF = new VString( "DUNS",false,false,true,30,60,"",null);
        if (!CalloutInvoiceExt.ComprobantesFiscalesActivos())
        	createLine(fCIF,"DUNS",false);

        // Name

        fName = new VString( "Name",true,false,true,30,60,"",null );
        fName.addActionListener( this );
        createLine( fName,"Name",false ).setFontBold( true );

        // Name2

        fName2 = new VString( "Name2",false,false,true,30,60,"",null );
        createLine( fName2,"Name2",false );

        // Group Business Partner

        fPartnerGroup = VComponentsFactory.VLookupFactory("C_BP_Group_ID", X_C_BP_Group.Table_Name, m_WindowNo,
				DisplayType.TableDir, null, false);
        fPartnerGroup.setPreferredSize(new Dimension(60,20));
        createLine( fPartnerGroup,"PartnerGroup",true );        
        
        // Campos específicos para comprobantes fiscales
        
        if (m_localeARActive) {
        
        	// Categoría IVA
        	fCategoriaIVA = VComponentsFactory.VLookupFactory("C_Categoria_IVA_ID", "C_Categoria_IVA", m_WindowNo, DisplayType.TableDir, null, false);
        	createLine(fCategoriaIVA,"C_Categoria_IVA_ID",false);
        	
        	// CUIT
        	fTaxID = new VString( "TaxID",false,false,true,30,60,"",null);
            createLine(fTaxID,"TaxID",false);
            
            // Tipo de Identificación
			setfTaxIdType(VComponentsFactory.VLookupFactory("TaxIdType", X_C_BPartner.Table_Name, m_WindowNo,
					DisplayType.List, null, false));
            createLine(getfTaxIdType(),"TaxIdType",false);
        }
        
        // Contact

        fContact = new VString( "Contact",false,false,true,30,60,"",null );
        createLine( fContact,"Contact",true ).setFontBold( true );

        // Greeting Contact

        fGreetingC = new JComboBox( m_greeting );
        fGreetingC.setPreferredSize(new Dimension(60,20));
        createLine( fGreetingC,"Greeting",false );

        // Title

        fTitle = new VString( "Title",false,false,true,30,60,"",null );
        createLine( fTitle,"Title",false );

        // Email

        fEMail = new VString( "EMail",false,false,true,30,40,"",null );
        createLine( fEMail,"EMail",false );

        // Location

        fAddress = new VLocation( "C_Location_ID",false,false,true,new MLocationLookup(getCtx(),m_WindowNo ));
        fAddress.setValue( null );
        fAddress.addActionListener(this);
        createLine( fAddress,"C_Location_ID",true ).setFontBold( true );

        // Phone

        fPhone = new VString( "Phone",false,false,true,30,40,"",null );
        createLine( fPhone,"Phone",true );

        // Phone2

        fPhone2 = new VString( "Phone2",false,false,true,30,40,"",null );
        createLine( fPhone2,"Phone2",false );

        // Fax

        fFax = new VString( "Fax",false,false,true,30,40,"",null );
        createLine( fFax,"Fax",false );

        //

        fName.setBackground( CompierePLAF.getFieldBackground_Mandatory());
        fAddress.setBackground( CompierePLAF.getFieldBackground_Mandatory());
        fCIF.setBackground(CompierePLAF.getFieldBackground_Mandatory());
    }    // initBPartner

    /**
     * Descripción de Método
     *
     *
     * @param field
     * @param title
     * @param addSpace
     *
     * @return
     */

    private CLabel createLine( JComponent field,String title,boolean addSpace ) {
        if( addSpace ) {
            m_gbc.gridy  = m_line++;
            m_gbc.gridx  = 1;
            m_gbc.insets = m_fieldInsets;
            centerPanel.add( Box.createHorizontalStrut( 6 ),m_gbc );
        }

        // Line

        m_gbc.gridy = m_line++;

        // Label

        m_gbc.gridx  = 0;
        m_gbc.insets = m_labelInsets;
        m_gbc.fill   = GridBagConstraints.HORIZONTAL;

        String labelStr = Msg.getElement(getCtx(), title);
        if (labelStr == null || labelStr.equals(""))
        	labelStr = Msg.translate(getCtx(),title );
        CLabel label = new CLabel( labelStr );

        centerPanel.add( label,m_gbc );

        // Field

        m_gbc.gridx  = 1;
        m_gbc.insets = m_fieldInsets;
        m_gbc.fill   = GridBagConstraints.HORIZONTAL;
        centerPanel.add( field,m_gbc );

        if( m_readOnly ) {
            field.setEnabled( false );
        }

        getCompoLabels().put(field, label);
        
        return label;
    }    // createLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private Object[] fillGreeting() {
        String sql = "SELECT C_Greeting_ID, Name FROM C_Greeting WHERE IsActive='Y' ORDER BY 2";

        sql = MRole.getDefault().addAccessSQL( sql,"C_Greeting",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        return DB.getKeyNamePairs( sql,true );
    }    // fillGreeting

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    private KeyNamePair getGreeting( int key ) {
        for( int i = 0;i < m_greeting.length;i++ ) {
            KeyNamePair p = ( KeyNamePair )m_greeting[ i ];

            if( p.getKey() == key ) {
                return p;
            }
        }

        return new KeyNamePair( -1," " );
    }    // getGreeting

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     *
     * @return
     */

    public boolean loadBPartner( int C_BPartner_ID ) {
        log.config( "C_BPartner_ID=" + C_BPartner_ID );

        // New bpartner

        if( C_BPartner_ID == 0 ) {
            m_partner   = null;
            m_pLocation = null;
            m_user      = null;

            return true;
        }

        m_partner = new MBPartner( getCtx(),C_BPartner_ID,null );

        if( m_partner.getID() == 0 ) {
            ADialog.error( m_WindowNo,this,"BPartnerNotFound" );

            return false;
        }

        // BPartner - Load values

        fValue.setText( m_partner.getValue());
        fGreetingBP.setSelectedItem( getGreeting( m_partner.getC_Greeting_ID()));
        fName.setText( m_partner.getName());
        fName2.setText( m_partner.getName2());
        fCIF.setText(m_partner.getDUNS());
        fPartnerGroup.setValue(m_partner.getC_BP_Group_ID());
        
        // Campos específicos de localización argentina
        if (m_localeARActive) {
        	fTaxID.setText(m_partner.getTaxID());
        	fTaxIdType.setValue(m_partner.getTaxIdType());
        	fCategoriaIVA.setValue(m_partner.getC_Categoria_Iva_ID());
        }

        // Contact - Load values

        m_pLocation = m_partner.getLocation( Env.getContextAsInt( getCtx(),m_WindowNo,"C_BPartner_Location_ID" ));

        if( m_pLocation != null ) {
            int location = m_pLocation.getC_Location_ID();

            fAddress.setValue( new Integer( location ));

            //

            fPhone.setText( m_pLocation.getPhone());
            fPhone2.setText( m_pLocation.getPhone2());
            fFax.setText( m_pLocation.getFax());
        }

        // User - Load values

        m_user = m_partner.getContact( Env.getContextAsInt( getCtx(),m_WindowNo,"AD_User_ID" ));

        if( m_user != null ) {
            fGreetingC.setSelectedItem( getGreeting( m_user.getC_Greeting_ID()));
            fContact.setText( m_user.getName());
            fTitle.setText( m_user.getTitle());
            fEMail.setText( m_user.getEMail());

            //

            fPhone.setText( m_user.getPhone());
            fPhone2.setText( m_user.getPhone2());
            fFax.setText( m_user.getFax());
        }

        return true;
    }    // loadBPartner

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( m_readOnly ) {
            dispose();

            // copy value

        } else if( e.getSource() == fValue ) {
            if( (fName.getText() == null) || (fName.getText().length() == 0) ) {
                fName.setText( fValue.getText());
            }
        } else if( e.getSource() == fName ) {
            if( (fContact.getText() == null) || (fContact.getText().length() == 0) ) {
                fContact.setText( fName.getText());
            }
        } else if(e.getSource() == fAddress.getM_button()){
        	return;
        }

        // OK pressed

        else if( e.getActionCommand().equals( ConfirmPanel.A_OK ) && actionSave()) {
            dispose();

            // Cancel pressed

        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean actionSave() {
    	boolean saved = true;
        setTrxName(Trx.createTrxName());
        Trx trx = Trx.get(getTrxName(), true);
        trx.start();
        
        // Check Mandatory fields

        try{
        
	        if( fName.getText().equals( "" )) {
	            fName.setBackground( CompierePLAF.getFieldBackground_Error());
	
	            return false;
	        } else {
	            fName.setBackground( CompierePLAF.getFieldBackground_Mandatory());
	        }
	
	        if( fAddress.getC_Location_ID() == 0 ) {
	            fAddress.setBackground( CompierePLAF.getFieldBackground_Error());
	
	            return false;
	        } else {
	            fAddress.setBackground( CompierePLAF.getFieldBackground_Mandatory());
	        }
	
	        // ***** Business Partner *****
	
	        if( m_partner == null ) {	
	            m_partner = new MBPartner(getCtx(), 0, getTrxName());
	            m_partner.setClientOrg(clientID, 0);
	            
	            boolean isSOTrx = !"N".equals( Env.getContext( getCtx(),m_WindowNo,"IsSOTrx" ));
	
	            m_partner.setIsCustomer( isSOTrx );
	            m_partner.setIsVendor( !isSOTrx );
	            
	        }
	
	        // Check Value
	        String value = fValue.getText();
	        if(!Util.isEmpty(value, true)){
	        	m_partner.set_ValueNoCheck("Value", fValue.getText());
	        }
	        
	        m_partner.setName( fName.getText());
	        m_partner.setName2( fName2.getText());
	        m_partner.setDUNS(fCIF.getText());
	        
	        KeyNamePair p = ( KeyNamePair )fGreetingBP.getSelectedItem();
	
	        if( (p != null) && (p.getKey() > 0) ) {
	            m_partner.setC_Greeting_ID( p.getKey());
	        } else {
	            m_partner.setC_Greeting_ID( 0 );
	        }
	
	        // Campos especificos de la localización argentina.
	        if (m_localeARActive) {
				// CUIT
	        	m_partner.setTaxID(fTaxID.getText());
				
	        	// Tipo de identificación
	        	m_partner.setTaxIdType((String)getfTaxIdType().getValue());
	        	
	        	// Categoria de Iva
	        	Integer categoriaIva = (Integer)fCategoriaIVA.getValue();
	        	if (categoriaIva != null && categoriaIva > 0)
	        		m_partner.setC_Categoria_Iva_ID(categoriaIva);
	        	else
	        		m_partner.setC_Categoria_Iva_ID(0);
	        }
	        
	        if(fPartnerGroup.getValue() != null) {
	            m_partner.setC_BP_Group_ID((Integer)fPartnerGroup.getValue());
	        } else {
	            m_partner.setC_BP_Group_ID( 0 );        	
			}
	
			// Asociar los valores default en cada columna donde su valor sea
			// null siempre y cuando no se haya cargado una entidad comercial
			// existente
	        if(m_partner.getID() == 0){
	        	setDefaultValues(m_partner);
		        // Para que no quede referencia a ninguna lista de precios
		        m_partner.setM_PriceList_ID(0);
		        m_partner.setSOCreditStatus(MBPartner.SOCREDITSTATUS_NoCreditCheck);
	        }
	        
	        if(!m_partner.save()) {
	        	throw new Exception(CLogger.retrieveErrorAsString());
	        }
	        
	
	        // ***** Business Partner - Location *****
	
	        if( m_pLocation == null ) {
	            m_pLocation = new MBPartnerLocation( m_partner );
	        }
	
	        m_pLocation.setC_Location_ID( fAddress.getC_Location_ID());
	        MLocation location = new MLocation (getCtx(), fAddress.getC_Location_ID(), getTrxName());
	        m_pLocation.setName(location.toString());
	        
	        //
	
	        m_pLocation.setPhone( fPhone.getText());
	        m_pLocation.setPhone2( fPhone2.getText());
	        m_pLocation.setFax( fFax.getText());
	        m_pLocation.setEMail(fEMail.getText());
	
	        if(!m_pLocation.save()) {
	        	throw new Exception(CLogger.retrieveErrorAsString());
	        }
	
	        // ***** Business Partner - User *****
	
	        String contact = fContact.getText();
	        String email   = fEMail.getText();
	        
	
	        if( (m_user == null) && ( (contact.length() > 0) || (email.length() > 0) ) ) {
	            m_user = new MUser( m_partner );
	        }
	
	        if( m_user != null ) {
	            if( contact.length() == 0 ) {
	                contact = fName.getText();
	            }
	
	            m_user.setName( contact );
	            m_user.setEMail( email );
	            
	            m_user.setTitle( fTitle.getText());
	            p = ( KeyNamePair )fGreetingC.getSelectedItem();
	
	            if( (p != null) && (p.getKey() > 0) ) {
	                m_user.setC_Greeting_ID( p.getKey());
	            } else {
	                m_user.setC_Greeting_ID( 0 );
	            }
	
	            //
	           
	            m_user.setPhone( fPhone.getText());
	            m_user.setPhone2( fPhone2.getText());
	            m_user.setFax( fFax.getText());
	
	            if(!m_user.save()) {
	            	throw new Exception(CLogger.retrieveErrorAsString());
	            }
	        }
	        
	        trx.commit();
	        saved = true;
        } catch(Exception e){
        	ADialog.error( m_WindowNo,this,"BPartnerNotSaved",e.getMessage());
        	trx.rollback();
        	m_partner = null;
        	m_pLocation = null;
        	m_user = null;
        	saved = false;
        } finally{
        	trx.close();
        }

        return saved;
    }    // actionSave

    protected void postInit(){
    	
    }

    /**
     * Asigna los valores por defecto a las columnas que poseen valor null
     * @param bp
     */
    protected void setDefaultValues(MBPartner bp){
    	setDefaultValues(bp, getBPTabID());
    	setDefaultValues(bp, getBPSpecificTabID(bp.isCustomer()));
    }
    
    protected void setDefaultValues(MBPartner bp, Integer tabID){
    	MField[] bpFields = null;
    	if(tabID != null && tabID > 0){
    		bpFields = MField.createFields(getCtx(), m_WindowNo, 0, tabID);
    	}
    	for (MField field : bpFields) {
			if((DisplayType.YesNo != field.getDisplayType() 
					&& bp.get_Value(field.getColumnName()) == null) 
				|| (DisplayType.isID(field.getDisplayType())
					&& bp.get_Value(field.getColumnName()) instanceof Integer
					&& ((Integer) bp.get_Value(field.getColumnName())) == 0)) {
				bp.set_Value(field.getColumnName(), field.getDefault());
			}
		}
    }
    
    protected Integer getTabID(String uid){
    	return DB.getSQLValue(getTrxName(), "SELECT ad_tab_id FROM ad_tab WHERE ad_componentobjectUID = '"+uid+"'");
    }
    
    /**
     * @return el ID de la primer pestaña de la ventana Entidades Comerciales
     */
    protected Integer getBPTabID(){
    	return getTabID("CORE-AD_Tab-220");
    }
    
    /**
	 * @return el ID de la pestaña Cliente o Proveedor (dependiendo el issotrx)
	 *         de la ventana Entidades Comerciales
	 */
    protected Integer getBPSpecificTabID(boolean isSOTrx){
    	return getTabID(isSOTrx?"CORE-AD_Tab-223":"CORE-AD_Tab-224");
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        if( m_partner == null ) {
            return 0;
        }

        return m_partner.getC_BPartner_ID();
    }    // getBPartner_ID

	protected VLookup getfTaxIdType() {
		return fTaxIdType;
	}

	protected void setfTaxIdType(VLookup fTaxIdType) {
		this.fTaxIdType = fTaxIdType;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected String getTrxName() {
		return trxName;
	}

	protected void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	protected Map<JComponent, JLabel> getCompoLabels() {
		return compoLabels;
	}

	protected void setCompoLabels(Map<JComponent, JLabel> compoLabels) {
		this.compoLabels = compoLabels;
	}
}    // VBPartner



/*
 *  @(#)VBPartner.java   02.07.07
 * 
 *  Fin del fichero VBPartner.java
 *  
 *  Versión 2.2
 *
 */
