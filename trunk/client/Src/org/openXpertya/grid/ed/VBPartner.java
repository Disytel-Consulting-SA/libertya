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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.model.CalloutBPartner;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MLocationLookup;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
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
        m_readOnly = !MRole.getDefault().canUpdate( Env.getAD_Client_ID( Env.getCtx()),Env.getAD_Org_ID( Env.getCtx()),MBPartner.Table_ID,false );
        log.info( "R/O=" + m_readOnly );

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,ex.getMessage());
        }

        initBPartner();

        //

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

    private Object[] m_partnergroup ;
    
    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VBPartner.class );

    //

    /** Descripción de Campos */

    private VString fValue,fName,fName2,fContact,fTitle,fPhone,fFax,fPhone2,fEMail,fCIF,fTaxID;

    /** Descripción de Campos */

    private VLocation fAddress;

    /** Descripción de Campos */

    private JComboBox fGreetingBP,fGreetingC, fPartnerGroup;

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout centerLayout = new GridBagLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();
    
    private VLookup fCategoriaIVA = null;
    
    private boolean m_localeARActive  = CalloutInvoiceExt.ComprobantesFiscalesActivos();

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

        m_partnergroup = fillPartnerGroup();
        
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

        fPartnerGroup = new JComboBox( m_partnergroup );
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

        fAddress = new VLocation( "C_Location_ID",false,false,true,new MLocationLookup( Env.getCtx(),m_WindowNo ));
        fAddress.setValue( null );
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

        String labelStr = Msg.getElement(Env.getCtx(), title);
        if (labelStr == null || labelStr.equals(""))
        	labelStr = Msg.translate( Env.getCtx(),title );
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

    private Object[] fillPartnerGroup() {
        String sql = "SELECT C_BP_Group_ID, Name FROM C_BP_Group WHERE IsActive='Y' AND AD_Client_ID = "+Env.getAD_Client_ID(Env.getCtx())+" ORDER BY 2";

        return DB.getKeyNamePairs( sql,true );
    }    // fillPartnerGroup      
    
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

        m_partner = new MBPartner( Env.getCtx(),C_BPartner_ID,null );

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
        
        // Campos específicos de localización argentina
        if (m_localeARActive) {
        	fTaxID.setText(m_partner.getTaxID());
        	fCategoriaIVA.setValue(m_partner.getC_Categoria_Iva_ID());
        }

        // Contact - Load values

        m_pLocation = m_partner.getLocation( Env.getContextAsInt( Env.getCtx(),m_WindowNo,"C_BPartner_Location_ID" ));

        if( m_pLocation != null ) {
            int location = m_pLocation.getC_Location_ID();

            fAddress.setValue( new Integer( location ));

            //

            fPhone.setText( m_pLocation.getPhone());
            fPhone2.setText( m_pLocation.getPhone2());
            fFax.setText( m_pLocation.getFax());
        }

        // User - Load values

        m_user = m_partner.getContact( Env.getContextAsInt( Env.getCtx(),m_WindowNo,"AD_User_ID" ));

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
        log.config( "save" );

        // Check Mandatory fields

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
            int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());

            m_partner = MBPartner.getTemplate( Env.getCtx(),AD_Client_ID );

            boolean isSOTrx = !"N".equals( Env.getContext( Env.getCtx(),m_WindowNo,"IsSOTrx" ));

            m_partner.setIsCustomer( isSOTrx );
            m_partner.setIsVendor( !isSOTrx );
            
            if(isSOTrx){
            	m_partner.setSOCreditStatus(MBPartner.SOCREDITSTATUS_CreditOK);
            	m_partner.setSO_CreditLimit(new BigDecimal("100000"));
            }
        }

        // Check Value

        String value = fValue.getText();

        if( (value == null) || (value.length() == 0) ) {

            // get Table Documet No

            value = DB.getDocumentNo( Env.getAD_Client_ID( Env.getCtx()),"C_BPartner",null );
            fValue.setText( value );
        }

        m_partner.setValue( fValue.getText());

        //
        
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
			
        	// Categoria de Iva
        	Integer categoriaIva = (Integer)fCategoriaIVA.getValue();
        	if (categoriaIva != null && categoriaIva > 0)
        		m_partner.setC_Categoria_Iva_ID(categoriaIva);
        	else
        		m_partner.setC_Categoria_Iva_ID(0);
        }
        
        KeyNamePair pg = ( KeyNamePair )fPartnerGroup.getSelectedItem();
        if( (pg != null) && (pg.getKey() > 0) ) {
            m_partner.setC_BP_Group_ID( pg.getKey());
        } else {
            m_partner.setC_BP_Group_ID( 0 );        	
		}

        if( m_partner.save()) {
            log.fine( "VBPartner.save - C_BPartner_ID=" + m_partner.getC_BPartner_ID());
            /*
             * Verificar si se repiten los cuits en otra entidad comercial para la localización Argentina
             */
            if(m_localeARActive){
            	String bpName = CalloutBPartner.existCUIT(Env.getCtx(), fTaxID.getText(), m_partner.getID());
            	if(bpName != null){
            		ADialog.info(this.m_WindowNo,this, "",Msg.getMsg(Env.getCtx(), "ExistentBPartnerCUIT", new Object[] {fTaxID.getText(), bpName}));
            	}
            }
        } else {
        	ValueNamePair vnp = log.retrieveError();
            ADialog.error( m_WindowNo,this,"BPartnerNotSaved", (vnp.getName().length() == 0)?Msg.getMsg(Env.getCtx(), vnp.getValue()):vnp.getName());
            return true;
        }
        

        // ***** Business Partner - Location *****

        if( m_pLocation == null ) {
            m_pLocation = new MBPartnerLocation( m_partner );
        }

        m_pLocation.setC_Location_ID( fAddress.getC_Location_ID());
        MLocation location = new MLocation (Env.getCtx(), fAddress.getC_Location_ID(), null );
        m_pLocation.setName(location.toString());
        
        //

        m_pLocation.setPhone( fPhone.getText());
        m_pLocation.setPhone2( fPhone2.getText());
        m_pLocation.setFax( fFax.getText());
        m_pLocation.setEMail(fEMail.getText());

        if( m_pLocation.save()) {
            log.fine( "VBPartner.save - C_BPartner_Location_ID=" + m_pLocation.getC_BPartner_Location_ID());
        } else {
            ADialog.error( m_WindowNo,this,"BPartnerNotSaved",Msg.translate( Env.getCtx(),"C_BPartner_Location_ID" ));
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

            if( m_user.save()) {
                log.fine( "VBPartner.save - AD_User_ID=" + m_user.getAD_User_ID());
            } else {
                ADialog.error( m_WindowNo,this,"BPartnerNotSaved",Msg.translate( Env.getCtx(),"AD_User_ID" ));
            }
        }

        return true;
    }    // actionSave

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
}    // VBPartner



/*
 *  @(#)VBPartner.java   02.07.07
 * 
 *  Fin del fichero VBPartner.java
 *  
 *  Versión 2.2
 *
 */
