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



package org.openXpertya.grid;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JDialog;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VButton;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPaymentValidate;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MTab;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.process.DocAction;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 * 
 * 
 * @version 2.2, 12.10.07
 * @author Equipo de Desarrollo de openXpertya
 * 
 *         IMPORTANTE: 2010-11-25 Disytel<br>
 *         Esta clase no está siendo utilizada actualmente ya que se cambió el
 *         tipo de dato de la columna en la factura a una lista. Por este motivo
 *         la clase no se actualizó para soportar el estado de líneas de caja.
 *         En caso de querer volver a utilizarla es necesario primero ampliar la
 *         lógica en donde se crean o eliminan líneas de caja para que tenga en
 *         cuenta los estados de la misma, y realice el procesamiento
 *         correspondiente.
 */

public class VPayment extends JDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param WindowNo
     * @param mTab
     * @param button
     */

    public VPayment( int WindowNo,MTab mTab,VButton button ) {
        super( Env.getWindow( WindowNo ),Msg.getMsg( Env.getCtx(),"Payment" ),true );
        m_WindowNo = WindowNo;
        m_isSOTrx  = "Y".equals( Env.getContext( Env.getCtx(),WindowNo,"IsSOTrx" ));
        m_mTab = mTab;

        try {
            bDateField = new VDate( "DateAcct",false,false,true,DisplayType.Date,"DateAcct" );
            jbInit();
            m_initOK = dynInit( button );    // Null Pointer if order/invoice not saved yet
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VPayment",ex );
            m_initOK = false;
        }

        //

        AEnv.positionCenterWindow( Env.getWindow( WindowNo ),this );
    }    // VPayment

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private MTab m_mTab;

    // Data from Order/Invoice

    /** Descripción de Campos */

    private String m_DocStatus = null;

    /** Descripción de Campos */

    private String m_PaymentRule = "";

    /** Descripción de Campos */

    private int m_C_PaymentTerm_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateAcct = null;

    /** Descripción de Campos */

    private int m_C_Payment_ID = 0;

    /** Descripción de Campos */

    private MPayment m_mPayment = null;

    /** Descripción de Campos */

    private MPayment m_mPaymentOriginal = null;

    /** Descripción de Campos */

    private int m_C_CashLine_ID = 0;

    /** Descripción de Campos */

    private MCashLine m_cashLine = null;

    /** Descripción de Campos */

    private String m_CCType = "";

    /** Descripción de Campos */

    private int m_C_BankAccount_ID = 0;

    /** Descripción de Campos */

    private int m_C_CashBook_ID = 0;

    /** Descripción de Campos */

    private boolean m_isSOTrx = true;

    /** Descripción de Campos */

    private int m_C_Currency_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private BigDecimal m_Amount = Env.ZERO;    // Payment Amount

    //

    /** Descripción de Campos */

    private boolean m_initOK = true;

    /** Descripción de Campos */

    private boolean m_onlyRule = false;

    /** Descripción de Campos */

    private DecimalFormat m_Format = DisplayType.getNumberFormat( DisplayType.Amount );

    /** Descripción de Campos */

    private static Hashtable s_Currencies = null;    // EMU Currencies

    /** Descripción de Campos */

    private boolean m_needSave = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VPayment.class );

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private FlowLayout northLayout = new FlowLayout();

    /** Descripción de Campos */

    private CComboBox paymentCombo = new CComboBox();

    /** Descripción de Campos */

    private CLabel paymentLabel = new CLabel();

    /** Descripción de Campos */

    private CardLayout centerLayout = new CardLayout();

    /** Descripción de Campos */

    private CPanel bPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel kPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout kLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel kTypeLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox kTypeCombo = new CComboBox();

    /** Descripción de Campos */

    private CLabel kNumnerLabel = new CLabel();

    /** Descripción de Campos */

    private CTextField kNumberField = new CTextField();

    /** Descripción de Campos */

    private CLabel kExpLabel = new CLabel();

    /** Descripción de Campos */

    private CTextField kExpField = new CTextField();

    /** Descripción de Campos */

    private CLabel kApprovalLabel = new CLabel();

    /** Descripción de Campos */

    private CTextField kApprovalField = new CTextField();

    /** Descripción de Campos */

    private CPanel tPanel = new CPanel();

    /** Descripción de Campos */

    private CLabel tAccountLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox tAccountCombo = new CComboBox();

    /** Descripción de Campos */

    private CPanel sPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout sPanelLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel sNumberLabel = new CLabel();

    /** Descripción de Campos */

    private CTextField sNumberField = new CTextField();

    /** Descripción de Campos */

    private CLabel sRoutingLabel = new CLabel();

    /** Descripción de Campos */

    private CTextField sRoutingField = new CTextField();

    /** Descripción de Campos */

    private CLabel sCurrencyLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox sCurrencyCombo = new CComboBox();

    /** Descripción de Campos */

    private CLabel bCurrencyLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox bCurrencyCombo = new CComboBox();

    /** Descripción de Campos */

    private CPanel pPanel = new CPanel();

    /** Descripción de Campos */

    private CLabel pTermLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox pTermCombo = new CComboBox();

    /** Descripción de Campos */

    private GridBagLayout bPanelLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel bAmountLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel bAmountField = new CLabel();

    /** Descripción de Campos */

    private CLabel sAmountLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel sAmountField = new CLabel();

    /** Descripción de Campos */

    private VDate bDateField;

    /** Descripción de Campos */

    private CLabel bDateLabel = new CLabel();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private CTextField sCheckField = new CTextField();

    /** Descripción de Campos */

    private CLabel sCheckLabel = new CLabel();

    /** Descripción de Campos */

    private CButton kOnline = new CButton();

    /** Descripción de Campos */

    private CButton sOnline = new CButton();

    /** Descripción de Campos */

    private CComboBox sBankAccountCombo = new CComboBox();

    /** Descripción de Campos */

    private CLabel sBankAccountLabel = new CLabel();

    /** Descripción de Campos */

    private GridBagLayout pPanelLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel bCashBookLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox bCashBookCombo = new CComboBox();

    /** Descripción de Campos */

    private GridBagLayout tPanelLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CButton tOnline = new CButton();

    /** Descripción de Campos */

    private CLabel kStatus = new CLabel();

    /** Descripción de Campos */

    private CTextField tRoutingField = new CTextField();

    /** Descripción de Campos */

    private CTextField tNumberField = new CTextField();

    /** Descripción de Campos */

    private CLabel tStatus = new CLabel();

    /** Descripción de Campos */

    private CLabel tRoutingText = new CLabel();

    /** Descripción de Campos */

    private CLabel tNumberText = new CLabel();

    /** Descripción de Campos */

    private CLabel sStatus = new CLabel();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        centerPanel.setBorder( BorderFactory.createRaisedBevelBorder());
        getContentPane().add( mainPanel );
        mainPanel.setLayout( mainLayout );
        mainPanel.add( centerPanel,BorderLayout.CENTER );

        //

        northPanel.setLayout( northLayout );
        paymentLabel.setText( Msg.translate( Env.getCtx(),"PaymentRule" ));
        mainPanel.add( northPanel,BorderLayout.NORTH );
        northPanel.add( paymentLabel,null );
        northPanel.add( paymentCombo,null );

        //

        centerPanel.setLayout( centerLayout );

        //

        kPanel.setLayout( kLayout );
        kNumberField.setPreferredSize( new Dimension( 120,21 ));
        kExpField.setPreferredSize( new Dimension( 40,21 ));
        kApprovalField.setPreferredSize( new Dimension( 120,21 ));
        kTypeLabel.setText( Msg.translate( Env.getCtx(),"CreditCardType" ));
        kNumnerLabel.setText( Msg.translate( Env.getCtx(),"CreditCardNumber" ));
        kExpLabel.setText( Msg.getMsg( Env.getCtx(),"Expires" ));
        kApprovalLabel.setText( Msg.translate( Env.getCtx(),"VoiceAuthCode" ));
        kOnline.setText( Msg.getMsg( Env.getCtx(),"Online" ));
        kOnline.addActionListener( this );
        kStatus.setText( " " );
        centerPanel.add( kPanel,"kPanel" );
        centerLayout.addLayoutComponent( kPanel,"kPanel" );
        kPanel.add( kTypeLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,2,0 ),0,0 ));
        kPanel.add( kTypeCombo,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,5,2,5 ),0,0 ));
        kPanel.add( kNumnerLabel,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,2,0 ),0,0 ));
        kPanel.add( kNumberField,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.VERTICAL,new Insets( 2,5,2,5 ),0,0 ));
        kPanel.add( kExpLabel,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,2,0 ),0,0 ));
        kPanel.add( kExpField,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,5,2,5 ),0,0 ));
        kPanel.add( kApprovalLabel,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,0 ),0,0 ));
        kPanel.add( kApprovalField,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        kPanel.add( kStatus,new GridBagConstraints( 0,4,2,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        kPanel.add( kOnline,new GridBagConstraints( 2,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));

        //

        tPanel.setLayout( tPanelLayout );
        tAccountLabel.setText( Msg.translate( Env.getCtx(),"C_BP_BankAccount_ID" ));
        tRoutingField.setColumns( 8 );
        tNumberField.setColumns( 10 );
        tRoutingText.setText( Msg.translate( Env.getCtx(),"RoutingNo" ));
        tNumberText.setText( Msg.translate( Env.getCtx(),"AccountNo" ));
        tOnline.setText( Msg.getMsg( Env.getCtx(),"Online" ));
        tStatus.setText( " " );
        centerPanel.add( tPanel,"tPanel" );
        centerLayout.addLayoutComponent( tPanel,"tPanel" );
        tPanel.add( tAccountLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,0 ),0,0 ));
        tPanel.add( tAccountCombo,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        tPanel.add( tRoutingField,new GridBagConstraints( 1,1,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        tPanel.add( tNumberField,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        tPanel.add( tStatus,new GridBagConstraints( 0,3,2,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        tPanel.add( tRoutingText,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,0 ),0,0 ));
        tPanel.add( tNumberText,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,0 ),0,0 ));
        tPanel.add( tOnline,new GridBagConstraints( 3,2,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));

        //

        sPanel.setLayout( sPanelLayout );
        sBankAccountLabel.setText( Msg.translate( Env.getCtx(),"C_BankAccount_ID" ));
        sAmountLabel.setText( Msg.getMsg( Env.getCtx(),"Amount" ));
        sAmountField.setText( "" );
        sRoutingLabel.setText( Msg.translate( Env.getCtx(),"RoutingNo" ));
        sNumberLabel.setText( Msg.translate( Env.getCtx(),"AccountNo" ));
        sCheckLabel.setText( Msg.translate( Env.getCtx(),"CheckNo" ));
        sCheckField.setColumns( 8 );
        sCurrencyLabel.setText( Msg.translate( Env.getCtx(),"C_Currency_ID" ));
        sNumberField.setPreferredSize( new Dimension( 100,21 ));
        sRoutingField.setPreferredSize( new Dimension( 70,21 ));
        sStatus.setText( " " );
        sOnline.setText( Msg.getMsg( Env.getCtx(),"Online" ));
        centerPanel.add( sPanel,"sPanel" );
        centerLayout.addLayoutComponent( sPanel,"sPanel" );
        sPanel.add( sCurrencyLabel,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,2,0 ),0,0 ));
        sPanel.add( sCurrencyCombo,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,5,2,5 ),0,0 ));
        sPanel.add( sRoutingField,new GridBagConstraints( 1,4,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,5,2,0 ),0,0 ));
        sPanel.add( sNumberField,new GridBagConstraints( 1,5,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 0,5,2,0 ),0,0 ));
        sPanel.add( sCheckField,new GridBagConstraints( 1,6,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,5,2,0 ),0,0 ));
        sPanel.add( sRoutingLabel,new GridBagConstraints( 0,3,1,2,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,0,2,0 ),0,0 ));
        sPanel.add( sNumberLabel,new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,2,0 ),0,0 ));
        sPanel.add( sCheckLabel,new GridBagConstraints( 0,6,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,2,0 ),0,0 ));
        sPanel.add( sBankAccountCombo,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,2,5 ),0,0 ));
        sPanel.add( sBankAccountLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,2,0 ),0,0 ));
        sPanel.add( sStatus,new GridBagConstraints( 0,7,3,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        sPanel.add( sOnline,new GridBagConstraints( 3,6,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        sPanel.add( sAmountField,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,5,5,5 ),0,0 ));
        sPanel.add( sAmountLabel,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,5,0 ),0,0 ));

        //

        pPanel.setLayout( pPanelLayout );
        pTermLabel.setText( Msg.translate( Env.getCtx(),"C_PaymentTerm_ID" ));
        centerPanel.add( pPanel,"pPanel" );
        centerLayout.addLayoutComponent( pPanel,"pPanel" );
        pPanel.add( pTermLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,5,2,0 ),0,0 ));
        pPanel.add( pTermCombo,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,5,2,5 ),0,0 ));

        //

        bCashBookLabel.setText( Msg.translate( Env.getCtx(),"C_CashBook_ID" ));
        bCurrencyLabel.setText( Msg.translate( Env.getCtx(),"C_Currency_ID" ));
        bPanel.setLayout( bPanelLayout );
        bAmountLabel.setText( Msg.getMsg( Env.getCtx(),"Amount" ));
        bAmountField.setText( "" );
        bDateLabel.setText( Msg.translate( Env.getCtx(),"DateAcct" ));
        centerLayout.addLayoutComponent( bPanel,"bPanel" );
        centerPanel.add( bPanel,"bPanel" );
        bPanel.add( bCurrencyLabel,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,2,0 ),0,0 ));
        bPanel.add( bCurrencyCombo,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,5,2,5 ),0,0 ));
        bPanel.add( bDateField,new GridBagConstraints( 1,2,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,2,5 ),0,0 ));
        bPanel.add( bDateLabel,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,2,0 ),0,0 ));
        bPanel.add( bCashBookLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 2,0,2,0 ),0,0 ));
        bPanel.add( bCashBookCombo,new GridBagConstraints( 1,0,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,5,2,5 ),0,0 ));
        bPanel.add( bAmountLabel,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,2,0 ),0,0 ));
        bPanel.add( bAmountField,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,2,5 ),0,0 ));

        //

        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param button
     *
     * @return
     *
     * @throws Exception
     */

    private boolean dynInit( VButton button ) throws Exception {
        m_DocStatus = ( String )m_mTab.getValue( "DocStatus" );
        log.config( m_DocStatus );

        if( m_mTab.getValue( "C_BPartner_ID" ) == null ) {
            ADialog.error( 0,this,"SaveErrorRowNotFound" );

            return false;
        }

        // Is the Trx posted?
        // String Posted = (String)m_mTab.getValue("Posted");
        // if (Posted != null && Posted.equals("Y"))
        // return false;

        // DocStatus

        m_DocStatus = ( String )m_mTab.getValue( "DocStatus" );

        if( m_DocStatus == null ) {
            m_DocStatus = "";
        }

        // Is the Trx closed?              Reversed / Voided / Cloased

        if( m_DocStatus.equals( "RE" ) || m_DocStatus.equals( "VO" ) || m_DocStatus.equals( "CL" )) {
            return false;
        }

        // Document is not complete - allow to change the Payment Rule only

        if( m_DocStatus.equals( "CO" ) || m_DocStatus.equals( "WP" )) {
            m_onlyRule = false;
        } else {
            m_onlyRule = true;
        }

        // PO only  Rule

        if( !m_onlyRule    // Only order has Warehouse
                &&!m_isSOTrx && (m_mTab.getValue( "M_Warehouse_ID" ) != null) ) {
            m_onlyRule = true;
        }

        centerPanel.setVisible( !m_onlyRule );

        // Amount

        m_Amount = ( BigDecimal )m_mTab.getValue( "GrandTotal" );

        if( !m_onlyRule && (m_Amount.compareTo( Env.ZERO ) == 0) ) {
            ADialog.error( m_WindowNo,this,"PaymentZero" );

            return false;
        }

        bAmountField.setText( m_Format.format( m_Amount ));
        sAmountField.setText( m_Format.format( m_Amount ));
        m_AD_Client_ID = (( Integer )m_mTab.getValue( "AD_Client_ID" )).intValue();
        m_AD_Org_ID     = (( Integer )m_mTab.getValue( "AD_Org_ID" )).intValue();
        m_C_BPartner_ID = (( Integer )m_mTab.getValue( "C_BPartner_ID" )).intValue();
        m_PaymentRule   = ( String )m_mTab.getValue( "PaymentRule" );
        m_C_Currency_ID = (( Integer )m_mTab.getValue( "C_Currency_ID" )).intValue();
        m_DateAcct = ( Timestamp )m_mTab.getValue( "DateAcct" );

        if( m_mTab.getValue( "C_PaymentTerm_ID" ) != null ) {
            m_C_PaymentTerm_ID = (( Integer )m_mTab.getValue( "C_PaymentTerm_ID" )).intValue();
        }

        // Existing Payment

        if( m_mTab.getValue( "C_Payment_ID" ) != null ) {
            m_C_Payment_ID = (( Integer )m_mTab.getValue( "C_Payment_ID" )).intValue();

            if( m_C_Payment_ID != 0 ) {
                m_mPayment         = new MPayment( Env.getCtx(),m_C_Payment_ID,null );
                m_mPaymentOriginal = new MPayment( Env.getCtx(),m_C_Payment_ID,null );    // full copy

                // CreditCard

                m_CCType = m_mPayment.getCreditCardType();
                kNumberField.setText( m_mPayment.getCreditCardNumber());
                kExpField.setText( m_mPayment.getCreditCardExp());
                kApprovalField.setText( m_mPayment.getVoiceAuthCode());
                kStatus.setText( m_mPayment.getR_PnRef());

                // if approved/paid, don't let it change

                kTypeCombo.setReadWrite( !m_mPayment.isApproved());
                kNumberField.setReadWrite( !m_mPayment.isApproved());
                kExpField.setReadWrite( !m_mPayment.isApproved());
                kApprovalField.setReadWrite( !m_mPayment.isApproved());
                kOnline.setReadWrite( !m_mPayment.isApproved());

                // Check

                m_C_BankAccount_ID = m_mPayment.getC_BankAccount_ID();
                sRoutingField.setText( m_mPayment.getRoutingNo());
                sNumberField.setText( m_mPayment.getAccountNo());
                sCheckField.setText( m_mPayment.getCheckNo());
                sStatus.setText( m_mPayment.getR_PnRef());

                // Transfer

                tRoutingField.setText( m_mPayment.getRoutingNo());
                tNumberField.setText( m_mPayment.getAccountNo());
                tStatus.setText( m_mPayment.getR_PnRef());
            }
        }

        if( m_mPayment == null ) {
            m_mPayment = new MPayment( Env.getCtx(),0,null );
            m_mPayment.setAmount( m_C_Currency_ID,m_Amount );
        }

        // Existing Cahbook entry

        m_cashLine      = null;
        m_C_CashLine_ID = 0;

        if( m_mTab.getValue( "C_CashLine_ID" ) != null ) {
            m_C_CashLine_ID = (( Integer )m_mTab.getValue( "C_CashLine_ID" )).intValue();

            if( m_C_CashLine_ID == 0 ) {
                m_cashLine = null;
            } else {
                m_cashLine = new MCashLine( Env.getCtx(),m_C_CashLine_ID,null );
                m_DateAcct = m_cashLine.getStatementDate();
            }
        }

        // Accounting Date

        bDateField.setValue( m_DateAcct );

        if( s_Currencies == null ) {
            loadCurrencies();
        }

        // Is the currency an EMU currency?

        Integer C_Currency_ID = new Integer( m_C_Currency_ID );

        if( s_Currencies.containsKey( C_Currency_ID )) {
            Enumeration en = s_Currencies.keys();

            while( en.hasMoreElements()) {
                Object key = en.nextElement();

                bCurrencyCombo.addItem( s_Currencies.get( key ));
                sCurrencyCombo.addItem( s_Currencies.get( key ));
            }

            sCurrencyCombo.addActionListener( this );
            sCurrencyCombo.setSelectedItem( s_Currencies.get( C_Currency_ID ));
            bCurrencyCombo.addActionListener( this );
            bCurrencyCombo.setSelectedItem( s_Currencies.get( C_Currency_ID ));
        } else                                     // No EMU Currency
        {
            bCurrencyLabel.setVisible( false );    // Cash
            bCurrencyCombo.setVisible( false );
            sCurrencyLabel.setVisible( false );    // Check
            sCurrencyCombo.setVisible( false );
        }

        if( m_PaymentRule == null ) {
            m_PaymentRule = "";
        }

        ValueNamePair vp     = null;
        HashMap       values = button.getValues();
        Object[]      a      = values.keySet().toArray();

        for( int i = 0;i < a.length;i++ ) {
            String        PaymentRule = ( String )a[ i ];
            ValueNamePair pp          = new ValueNamePair( PaymentRule,( String )values.get( a[ i ] ));

            paymentCombo.addItem( pp );

            if( PaymentRule.toString().equals( m_PaymentRule )) {    // to select
                vp = pp;
            }
        }

        // Set PaymentRule

        paymentCombo.addActionListener( this );

        if( vp != null ) {
            paymentCombo.setSelectedItem( vp );
        }

        String SQL = MRole.getDefault().addAccessSQL( "SELECT C_PaymentTerm_ID, Name FROM C_PaymentTerm WHERE IsActive='Y' ORDER BY Name","C_PaymentTerm",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
        KeyNamePair kp = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                int         key  = rs.getInt( 1 );
                String      name = rs.getString( 2 );
                KeyNamePair pp   = new KeyNamePair( key,name );

                pTermCombo.addItem( pp );

                if( key == m_C_PaymentTerm_ID ) {
                    kp = pp;
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ept ) {
            log.log( Level.SEVERE,"-PaymentTerm-",ept );
        }

        // Set Selection

        if( kp != null ) {
            pTermCombo.setSelectedItem( kp );
        }

        SQL = "SELECT a.C_BP_BankAccount_ID, NVL(b.Name, ' ')||a.AccountNo AS Acct " + "FROM C_BP_BankAccount a,C_Bank b " + "WHERE C_BPartner_ID=? AND a.IsActive='Y'";
        kp = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,m_C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int         key  = rs.getInt( 1 );
                String      name = rs.getString( 2 );
                KeyNamePair pp   = new KeyNamePair( key,name );

                tAccountCombo.addItem( pp );

                // kp = pp;

            }

            rs.close();
            pstmt.close();
        } catch( SQLException eac ) {
            log.log( Level.SEVERE,"-BPAcct-",eac );
        }

        // Set Selection

        if( kp != null ) {
            tAccountCombo.setSelectedItem( kp );
        }

        ValueNamePair[] ccs = m_mPayment.getCreditCards();

        vp = null;

        for( int i = 0;i < ccs.length;i++ ) {
            kTypeCombo.addItem( ccs[ i ] );

            if( ccs[ i ].getValue().equals( m_CCType )) {
                vp = ccs[ i ];
            }
        }

        // Set Selection

        if( vp != null ) {
            kTypeCombo.setSelectedItem( vp );
        }

        SQL = MRole.getDefault().addAccessSQL( "SELECT C_BankAccount_ID, Name || ' ' || AccountNo, IsDefault " + "FROM C_BankAccount ba" + " INNER JOIN C_Bank b ON (ba.C_Bank_ID=b.C_Bank_ID) " + "WHERE b.IsActive='Y'","ba",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );
        kp = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                int         key  = rs.getInt( 1 );
                String      name = rs.getString( 2 );
                KeyNamePair pp   = new KeyNamePair( key,name );

                sBankAccountCombo.addItem( pp );

                if( key == m_C_BankAccount_ID ) {
                    kp = pp;
                }

                if( (kp == null) && rs.getString( 3 ).equals( "Y" )) {    // Default
                    kp = pp;
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ept ) {
            log.log( Level.SEVERE,"-PaymentTerm-",ept );
        }

        // Set Selection

        if( kp != null ) {
            sBankAccountCombo.setSelectedItem( kp );
        }

        SQL = MRole.getDefault().addAccessSQL( "SELECT C_CashBook_ID, Name, AD_Org_ID FROM C_CashBook WHERE IsActive='Y'","C_CashBook",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
        kp = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                int         key  = rs.getInt( 1 );
                String      name = rs.getString( 2 );
                KeyNamePair pp   = new KeyNamePair( key,name );

                bCashBookCombo.addItem( pp );

                if( key == m_C_CashBook_ID ) {
                    kp = pp;
                }

                if( (kp == null) && (key == m_AD_Org_ID) ) {    // Default Org
                    kp = pp;
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException epc ) {
            log.log( Level.SEVERE,"-CashBook-",epc );
        }

        // Set Selection

        if( kp != null ) {
            bCashBookCombo.setSelectedItem( kp );

            if( m_C_CashBook_ID == 0 ) {
                m_C_CashBook_ID = kp.getKey();    // set to default to avoid 'cashbook changed' message
            }
        }

        //

        return true;
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInitOK() {
        return m_initOK;
    }    // isInitOK

    /**
     * Descripción de Método
     *
     */

    private void loadCurrencies() {
        s_Currencies = new Hashtable( 12 );    // Currenly only 10+1

        String SQL = "SELECT C_Currency_ID, ISO_Code FROM C_Currency " + "WHERE (IsEMUMember='Y' AND EMUEntryDate<SysDate) OR IsEuro='Y' " + "ORDER BY 2";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                int    id   = rs.getInt( 1 );
                String name = rs.getString( 2 );

                s_Currencies.put( new Integer( id ),new KeyNamePair( id,name ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"",e );
        }
    }    // loadCurrencies

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // log.fine( "VPayment.actionPerformed - " + e.getActionCommand());

        // Finish

        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            if( checkMandatory()) {
                saveChanges();    // cannot recover
                dispose();
            }
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();

            // Payment Method Change

        } else if( e.getSource() == paymentCombo ) {

            // get selection

            ValueNamePair pp = ( ValueNamePair )paymentCombo.getSelectedItem();

            if( pp != null ) {
                String s = pp.getValue().toLowerCase() + "Panel";

                centerLayout.show( centerPanel,s );    // switch to panel
            }
        }

        // Check Currency change

        else if( e.getSource() == sCurrencyCombo ) {
            KeyNamePair pp  = ( KeyNamePair )sCurrencyCombo.getSelectedItem();
            BigDecimal  amt = MConversionRate.convert( Env.getCtx(),m_Amount,m_C_Currency_ID,pp.getKey(),m_AD_Client_ID,m_AD_Org_ID );

            sAmountField.setText( m_Format.format( amt ));
        }

        // Cash Currency change

        else if( e.getSource() == bCurrencyCombo ) {
            KeyNamePair pp  = ( KeyNamePair )bCurrencyCombo.getSelectedItem();
            BigDecimal  amt = MConversionRate.convert( Env.getCtx(),m_Amount,m_C_Currency_ID,pp.getKey(),m_AD_Client_ID,m_AD_Org_ID );

            bAmountField.setText( m_Format.format( amt ));
        }

        // Online

        else if( (e.getSource() == kOnline) || (e.getSource() == sOnline) || (e.getSource() == tOnline) ) {
            processOnline();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean saveChanges() {
        ValueNamePair vp             = ( ValueNamePair )paymentCombo.getSelectedItem();
        String        newPaymentRule = vp.getValue();

        log.info( "New Rule: " + newPaymentRule );

        // only Payment Rule

        if( m_onlyRule ) {
            if( !newPaymentRule.equals( m_PaymentRule )) {
                m_mTab.setValue( "PaymentRule",newPaymentRule );
            }

            return true;
        }

        // New Values

        Timestamp newDateAcct         = m_DateAcct;
        int       newC_PaymentTerm_ID = m_C_PaymentTerm_ID;
        int       newC_CashLine_ID    = m_C_CashLine_ID;
        int       newC_CashBook_ID    = m_C_CashBook_ID;
        String    newCCType           = m_CCType;
        int       newC_BankAccount_ID = 0;

        // B (Cash)                (Currency)

        if( newPaymentRule.equals( X_C_Order.PAYMENTRULE_Cash )) {
            KeyNamePair kp = ( KeyNamePair )bCashBookCombo.getSelectedItem();

            if( kp != null ) {
                newC_CashBook_ID = kp.getKey();
            }

            newDateAcct = ( Timestamp )bDateField.getValue();
        }

        // K (CreditCard)  Type, Number, Exp, Approval

        else if( newPaymentRule.equals( X_C_Order.PAYMENTRULE_CreditCard )) {
            vp = ( ValueNamePair )kTypeCombo.getSelectedItem();

            if( vp != null ) {
                newCCType = vp.getValue();
            }
        }

        // T (Transfer)    BPartner_Bank

        else if( newPaymentRule.equals( MOrder.PAYMENTRULE_DirectDeposit ) || newPaymentRule.equals( MOrder.PAYMENTRULE_DirectDebit )) {
            tAccountCombo.getSelectedItem();
        }

        // P (PaymentTerm) PaymentTerm

        else if( newPaymentRule.equals( X_C_Order.PAYMENTRULE_OnCredit )) {
            KeyNamePair kp = ( KeyNamePair )pTermCombo.getSelectedItem();

            if( kp != null ) {
                newC_PaymentTerm_ID = kp.getKey();
            }
        }

        // S (Check)               (Currency) CheckNo, Routing

        else if( newPaymentRule.equals( X_C_Order.PAYMENTRULE_Check )) {

            // sCurrencyCombo.getSelectedItem();

            KeyNamePair kp = ( KeyNamePair )sBankAccountCombo.getSelectedItem();

            if( kp != null ) {
                newC_BankAccount_ID = kp.getKey();
            }
        } else {
            return false;
        }

        // find Bank Account if not qualified yet

        if( ("KTSD".indexOf( newPaymentRule ) != -1) && (newC_BankAccount_ID == 0) ) {
            String tender = MPayment.TENDERTYPE_CreditCard;

            if( newPaymentRule.equals( MOrder.PAYMENTRULE_DirectDeposit )) {
                tender = MPayment.TENDERTYPE_DirectDeposit;
            } else if( newPaymentRule.equals( MOrder.PAYMENTRULE_DirectDebit )) {
                tender = MPayment.TENDERTYPE_DirectDebit;
            } else if( newPaymentRule.equals( MOrder.PAYMENTRULE_Check )) {
                tender = MPayment.TENDERTYPE_Check;
            }
        }

        if( !newPaymentRule.equals( m_PaymentRule )) {
            log.fine( "Changed PaymentRule: " + m_PaymentRule + " -> " + newPaymentRule );

            // We had a CashBook Entry

            if( m_PaymentRule.equals( X_C_Order.PAYMENTRULE_Cash )) {
                log.fine( "Old Cash - " + m_cashLine );

                if( m_cashLine != null ) {
                    MCashLine cl = m_cashLine.createReversal(m_cashLine.getC_Cash_ID());

                    if( cl.save()) {
                        log.config( "CashCancelled" );
                    } else {
                        ADialog.error( m_WindowNo,this,"PaymentError","CashNotCancelled" );
                    }
                }

                newC_CashLine_ID = 0;    // reset
            }

            // We had a change in Payment type (e.g. Check to CC)

            else if( ("KTSD".indexOf( m_PaymentRule ) != -1) && ("KTSD".indexOf( newPaymentRule ) != -1) && (m_mPaymentOriginal != null) ) {
                log.fine( "Old Payment(1) - " + m_mPaymentOriginal );
                m_mPaymentOriginal.setDocAction( DocAction.ACTION_Reverse_Correct );

                boolean ok = m_mPaymentOriginal.processIt( DocAction.ACTION_Reverse_Correct );

                m_mPaymentOriginal.save();

                if( ok ) {
                    log.info( "Payment Canecelled - " + m_mPaymentOriginal );
                } else {
                    ADialog.error( m_WindowNo,this,"PaymentError","PaymentNotCancelled " + m_mPaymentOriginal.getDocumentNo());
                }

                m_mPayment.resetNew();
            }

            // We had a Payment and something else (e.g. Check to Cash)

            else if( ("KTSD".indexOf( m_PaymentRule ) != -1) && ("KTSD".indexOf( newPaymentRule ) == -1) ) {
                log.fine( "Old Payment(2) - " + m_mPaymentOriginal );

                if( m_mPaymentOriginal != null ) {
                    m_mPaymentOriginal.setDocAction( DocAction.ACTION_Reverse_Correct );

                    boolean ok = m_mPaymentOriginal.processIt( DocAction.ACTION_Reverse_Correct );

                    m_mPaymentOriginal.save();

                    if( ok )    // Cancel Payment
                    {
                        log.fine( "PaymentCancelled " + m_mPayment.getDocumentNo());
                        m_mTab.getTableModel().dataSave( true );
                        m_mPayment.resetNew();
                        m_mPayment.setAmount( m_C_Currency_ID,m_Amount );
                    } else {
                        ADialog.error( m_WindowNo,this,"PaymentError","PaymentNotCancelled " + m_mPayment.getDocumentNo());
                    }
                }
            }
        }

        // Get Order and optionally Invoice

        int C_Order_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"C_Order_ID" );
        int C_Invoice_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"C_Invoice_ID" );

        if( (C_Invoice_ID == 0) && m_DocStatus.equals( "CO" )) {
            C_Invoice_ID = getInvoiceID( C_Order_ID );
        }

        // Amount sign negative, if ARC (Credit Memo) or API (AP Invoice)

        boolean  negateAmt = false;
        MInvoice invoice   = null;

        if( C_Invoice_ID != 0 ) {
            invoice   = new MInvoice( Env.getCtx(),C_Invoice_ID,null );
            negateAmt = invoice.isCreditMemo();
        }

        MOrder order = null;

        if( (invoice == null) && (C_Order_ID != 0) ) {
            order = new MOrder( Env.getCtx(),C_Order_ID,null );
        }

        BigDecimal payAmount = m_Amount;

        if( negateAmt ) {
            payAmount = m_Amount.negate();
        }

        // Info

        log.config( "C_Order_ID=" + C_Order_ID + ", C_Invoice_ID=" + C_Invoice_ID + ", NegateAmt=" + negateAmt );

        if( newPaymentRule.equals( X_C_Order.PAYMENTRULE_Cash )) {
            log.fine( "Cash" );

            String description = ( String )m_mTab.getValue( "DocumentNo" );

            if( (C_Invoice_ID == 0) && (order == null) ) {
                log.config( "No Invoice!" );
                ADialog.error( m_WindowNo,this,"PaymentError","CashNotCreated" );
            } else {

                // Changed Amount

                if( (m_cashLine != null) && (payAmount.compareTo( m_cashLine.getAmount()) != 0) ) {
                    log.config( "Changed CashBook Amount" );
                    m_cashLine.setAmount( payAmount );

                    if( m_cashLine.save()) {
                        log.config( "CashAmt Changed" );
                    }
                }

                // Different Date/CashBook

                if( (m_cashLine != null) && ( (newC_CashBook_ID != m_C_CashBook_ID) ||!TimeUtil.isSameDay( m_cashLine.getStatementDate(),newDateAcct ))) {
                    log.config( "Changed CashBook/Date: " + m_C_CashBook_ID + "->" + newC_CashBook_ID );

                    MCashLine reverse = m_cashLine.createReversal(m_cashLine.getC_Cash_ID());

                    if( !reverse.save()) {
                        ADialog.error( m_WindowNo,this,"PaymentError","CashNotCancelled" );
                    }

                    m_cashLine = null;
                }

                // Create new

                if( m_cashLine == null ) {
                    log.config( "New CashBook" );

                    int C_Currency_ID = 0;

                    if( invoice != null ) {
                        C_Currency_ID = invoice.getC_Currency_ID();
                    }

                    if( (C_Currency_ID == 0) && (order != null) ) {
                        C_Currency_ID = order.getC_Currency_ID();
                    }

                    MCash cash = null;

                    if( newC_CashBook_ID != 0 ) {
                        cash = MCash.get( Env.getCtx(),newC_CashBook_ID,newDateAcct,null );
                    } else {    // Default
                        cash = MCash.get( Env.getCtx(),m_AD_Org_ID,newDateAcct,C_Currency_ID,null );
                    }

                    if( (cash == null) || (cash.getID() == 0) ) {
                        ADialog.error( m_WindowNo,this,"PaymentError","CashNotCreated" );
                    } else {
                        MCashLine cl = new MCashLine( cash );

                        if( invoice != null ) {
                            cl.setInvoice( invoice );
                        }

                        if( order != null ) {
                            cl.setOrder( order,null );
                            m_needSave = true;
                        }

                        if( cl.save()) {
                            log.config( "CashCreated" );
                        } else {
                            ADialog.error( m_WindowNo,this,"PaymentError","CashNotCreated" );
                        }
                    }
                }
            }    // have invoice
        }

        if( "KTSD".indexOf( newPaymentRule ) != -1 ) {
            log.fine( "Payment - " + newPaymentRule );

            // Set Amount

            m_mPayment.setAmount( m_C_Currency_ID,payAmount );

            if( newPaymentRule.equals( MOrder.PAYMENTRULE_CreditCard )) {
                m_mPayment.setCreditCard( MPayment.TRXTYPE_Sales,newCCType,kNumberField.getText(),"",kExpField.getText());
                m_mPayment.setPaymentProcessor();
            } else if( newPaymentRule.equals( MOrder.PAYMENTRULE_DirectDeposit ) || newPaymentRule.equals( MOrder.PAYMENTRULE_DirectDebit )) {
                m_mPayment.setBankACH( newC_BankAccount_ID,m_isSOTrx,newPaymentRule,tRoutingField.getText(),tNumberField.getText());
            } else if( newPaymentRule.equals( MOrder.PAYMENTRULE_Check )) {
                m_mPayment.setBankCheck( newC_BankAccount_ID,m_isSOTrx,sRoutingField.getText(),sNumberField.getText(),sCheckField.getText());
            }

            m_mPayment.setC_BPartner_ID( m_C_BPartner_ID );
            m_mPayment.setC_Invoice_ID( C_Invoice_ID );

            if( order != null ) {
                m_mPayment.setC_Order_ID( C_Order_ID );
                m_needSave = true;
            }

            m_mPayment.setDateTrx( m_DateAcct );
            m_mPayment.setDateAcct( m_DateAcct );
            m_mPayment.save();

            // Save/Post

            if( MPayment.DOCSTATUS_Drafted.equals( m_mPayment.getDocStatus())) {
                boolean ok = m_mPayment.processIt( DocAction.ACTION_Complete );

                m_mPayment.save();

                if( ok ) {
                    ADialog.info( m_WindowNo,this,"PaymentCreated",m_mPayment.getDocumentNo());
                } else {
                    ADialog.error( m_WindowNo,this,"PaymentError","PaymentNotCreated" );
                }
            } else {
                log.fine( "NotDraft " + m_mPayment );
            }
        }

        log.config( "Saving changes" );

        //

        if( !newPaymentRule.equals( m_PaymentRule )) {
            m_mTab.setValue( "PaymentRule",newPaymentRule );
        }

        //

        if( !newDateAcct.equals( m_DateAcct )) {
            m_mTab.setValue( "DateAcct",newDateAcct );
        }

        //

        if( newC_PaymentTerm_ID != m_C_PaymentTerm_ID ) {
            m_mTab.setValue( "C_PaymentTerm_ID",new Integer( newC_PaymentTerm_ID ));
        }

        // Set Payment

        if( m_mPayment.getC_Payment_ID() != m_C_Payment_ID ) {
            if( m_mPayment.getC_Payment_ID() == 0 ) {
                m_mTab.setValue( "C_Payment_ID",null );
            } else {
                m_mTab.setValue( "C_Payment_ID",new Integer( m_mPayment.getC_Payment_ID()));
            }
        }

        // Set Cash

        if( newC_CashLine_ID != m_C_CashLine_ID ) {
            if( newC_CashLine_ID == 0 ) {
                m_mTab.setValue( "C_CashLine_ID",null );
            } else {
                m_mTab.setValue( "C_CashLine_ID",new Integer( newC_CashLine_ID ));
            }
        }

        return true;
    }    // saveChanges

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean checkMandatory() {

        // log.config( "VPayment.checkMandatory");

        ValueNamePair vp          = ( ValueNamePair )paymentCombo.getSelectedItem();
        String        PaymentRule = vp.getValue();

        // only Payment Rule

        if( m_onlyRule ) {
            return true;
        }

        Timestamp DateAcct         = m_DateAcct;
        int       C_PaymentTerm_ID = m_C_PaymentTerm_ID;
        int       C_CashBook_ID    = m_C_CashBook_ID;
        String    CCType           = m_CCType;

        //

        int     C_BankAccount_ID = 0;
        boolean dataOK           = true;

        // B (Cash)                (Currency)

        if( PaymentRule.equals( MOrder.PAYMENTRULE_Cash )) {
            KeyNamePair kp = ( KeyNamePair )bCashBookCombo.getSelectedItem();

            if( kp != null ) {
                C_CashBook_ID = kp.getKey();
            }

            DateAcct = ( Timestamp )bDateField.getValue();
        }

        // K (CreditCard)  Type, Number, Exp, Approval

        else if( PaymentRule.equals( MOrder.PAYMENTRULE_CreditCard )) {
            vp = ( ValueNamePair )kTypeCombo.getSelectedItem();

            if( vp != null ) {
                CCType = vp.getValue();
            }

            //

            String error = MPaymentValidate.validateCreditCardNumber( kNumberField.getText(),CCType );

            if( error.length() != 0 ) {
                kNumberField.setBackground( CompierePLAF.getFieldBackground_Error());

                if( error.indexOf( "?" ) == -1 ) {
                    ADialog.error( m_WindowNo,this,error );
                    dataOK = false;
                } else    // warning
                {
                    if( !ADialog.ask( m_WindowNo,this,error )) {
                        dataOK = false;
                    }
                }
            }

            error = MPaymentValidate.validateCreditCardExp( kExpField.getText());

            if( error.length() != 0 ) {
                kExpField.setBackground( CompierePLAF.getFieldBackground_Error());
                ADialog.error( m_WindowNo,this,error );
                dataOK = false;
            }
        }

        // T (Transfer)    BPartner_Bank

        else if( PaymentRule.equals( MOrder.PAYMENTRULE_DirectDeposit ) || PaymentRule.equals( MOrder.PAYMENTRULE_DirectDebit )) {
            tAccountCombo.getSelectedItem();

            String error = MPaymentValidate.validateRoutingNo( tRoutingField.getText());

            if( error.length() != 0 ) {
                tRoutingField.setBackground( CompierePLAF.getFieldBackground_Error());
                ADialog.error( m_WindowNo,this,error );
                dataOK = false;
            }

            error = MPaymentValidate.validateAccountNo( tNumberField.getText());

            if( error.length() != 0 ) {
                tNumberField.setBackground( CompierePLAF.getFieldBackground_Error());
                ADialog.error( m_WindowNo,this,error );
                dataOK = false;
            }
        }

        // P (PaymentTerm) PaymentTerm

        else if( PaymentRule.equals( MOrder.PAYMENTRULE_OnCredit )) {
            KeyNamePair kp = ( KeyNamePair )pTermCombo.getSelectedItem();

            if( kp != null ) {
                C_PaymentTerm_ID = kp.getKey();
            }
        }

        // S (Check)               (Currency) CheckNo, Routing

        else if( PaymentRule.equals( MOrder.PAYMENTRULE_Check )) {

            // sCurrencyCombo.getSelectedItem();

            KeyNamePair kp = ( KeyNamePair )sBankAccountCombo.getSelectedItem();

            if( kp != null ) {
                C_BankAccount_ID = kp.getKey();
            }

            String error = MPaymentValidate.validateRoutingNo( sRoutingField.getText());

            if( error.length() != 0 ) {
                sRoutingField.setBackground( CompierePLAF.getFieldBackground_Error());
                ADialog.error( m_WindowNo,this,error );
                dataOK = false;
            }

            error = MPaymentValidate.validateAccountNo( sNumberField.getText());

            if( error.length() != 0 ) {
                sNumberField.setBackground( CompierePLAF.getFieldBackground_Error());
                ADialog.error( m_WindowNo,this,error );
                dataOK = false;
            }

            error = MPaymentValidate.validateCheckNo( sCheckField.getText());

            if( error.length() != 0 ) {
                sCheckField.setBackground( CompierePLAF.getFieldBackground_Error());
                ADialog.error( m_WindowNo,this,error );
                dataOK = false;
            }
        } else {
            log.log( Level.SEVERE,"Unknown PaymentRule " + PaymentRule );

            return false;
        }

        // find Bank Account if not qualified yet

        if( ("KTSD".indexOf( PaymentRule ) != -1) && (C_BankAccount_ID == 0) ) {
            String tender = MPayment.TENDERTYPE_CreditCard;

            if( PaymentRule.equals( MOrder.PAYMENTRULE_DirectDeposit )) {
                tender = MPayment.TENDERTYPE_DirectDeposit;
            } else if( PaymentRule.equals( MOrder.PAYMENTRULE_DirectDebit )) {
                tender = MPayment.TENDERTYPE_DirectDebit;
            } else if( PaymentRule.equals( MOrder.PAYMENTRULE_Check )) {
                tender = MPayment.TENDERTYPE_Check;
            }

            // ACH & Check must have a bank account

            if( (C_BankAccount_ID == 0) && ("TS".indexOf( PaymentRule ) != -1) ) {
                ADialog.error( m_WindowNo,this,"PaymentNoProcessor" );
                dataOK = false;
            }
        }

        //

        log.config( "OK=" + dataOK );

        return dataOK;
    }    // checkMandatory

    /**
     * Descripción de Método
     *
     *
     * @param C_Order_ID
     *
     * @return
     */

    private static int getInvoiceID( int C_Order_ID ) {
        int    retValue = 0;
        String sql      = "SELECT C_Invoice_ID FROM C_Invoice WHERE C_Order_ID=? " + "ORDER BY C_Invoice_ID DESC";    // last invoice

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_Order_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"",e );
        }

        return retValue;
    }    // getInvoiceID

    /**
     * Descripción de Método
     *
     */

    private void processOnline() {
        log.config( "" );

        if( !checkMandatory()) {
            return;
        }

        boolean approved = false;
        String  info     = "";

        //

        ValueNamePair vp          = ( ValueNamePair )paymentCombo.getSelectedItem();
        String        PaymentRule = vp.getValue();

        // --  CreditCard

        if( PaymentRule.equals( X_C_Order.PAYMENTRULE_CreditCard )) {
            vp = ( ValueNamePair )kTypeCombo.getSelectedItem();

            String CCType = vp.getValue();

            m_mPayment.setCreditCard( MPayment.TRXTYPE_Sales,CCType,kNumberField.getText(),"",kExpField.getText());
            m_mPayment.setAmount( m_C_Currency_ID,m_Amount );
            m_mPayment.setPaymentProcessor();
            m_mPayment.setC_BPartner_ID( m_C_BPartner_ID );

            //

            int C_Invoice_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"C_Invoice_ID" );

            if( (C_Invoice_ID == 0) && m_DocStatus.equals( "CO" )) {
                int C_Order_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"C_Order_ID" );

                C_Invoice_ID = getInvoiceID( C_Order_ID );
            }

            m_mPayment.setC_Invoice_ID( C_Invoice_ID );
            m_mPayment.setDateTrx( m_DateAcct );

            // Set Amount

            m_mPayment.setAmount( m_C_Currency_ID,m_Amount );
            approved = m_mPayment.processOnline();
            info     = m_mPayment.getR_RespMsg() + " (" + m_mPayment.getR_AuthCode() + ") ID=" + m_mPayment.getR_PnRef();

            boolean saved = m_mPayment.save();

            if( approved ) {
                boolean ok = m_mPayment.processIt( DocAction.ACTION_Complete );

                m_mPayment.save();

                if( ok ) {
                    ADialog.info( m_WindowNo,this,"PaymentProcessed",info + "\n" + m_mPayment.getDocumentNo());
                } else {
                    ADialog.error( m_WindowNo,this,"PaymentError","PaymentNotCreated" );
                }

                saveChanges();
                dispose();
            } else {
                ADialog.error( m_WindowNo,this,"PaymentNotProcessed",info );
            }
        } else {
            ADialog.error( m_WindowNo,this,"PaymentNoProcessor" );
        }
    }    // online

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean needSave() {
        return m_needSave;
    }    // needSave
}    // VPayment



/*
 *  @(#)VPayment.java   02.07.07
 *
 *  Fin del fichero VPayment.java
 *
 *  Versión 2.2
 *
 */
