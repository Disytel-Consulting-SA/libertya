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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MCharge;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
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

public class VCharge extends CPanel implements FormPanel,ActionListener {

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
            jbInit();
            dynInit();
            frame.getContentPane().add( mainPanel,BorderLayout.CENTER );
            frame.getContentPane().add( confirmPanel,BorderLayout.SOUTH );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"init",e );
        }
    }    // init

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private int m_C_Element_ID = 0;

    /** Descripción de Campos */

    private int m_C_AcctSchema_ID = 0;

    /** Descripción de Campos */

    private int m_C_TaxCategory_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int m_CreatedBy = 0;

    /** Descripción de Campos */

    private MAcctSchema m_acctSchema = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VCharge.class );

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel newPanel = new CPanel();

    /** Descripción de Campos */

    private TitledBorder newBorder;

    /** Descripción de Campos */

    private GridBagLayout newLayout = new GridBagLayout();

    /** Descripción de Campos */

    private JLabel valueLabel = new JLabel();

    /** Descripción de Campos */

    private JTextField valueField = new JTextField();

    /** Descripción de Campos */

    private JCheckBox isExpense = new JCheckBox();

    /** Descripción de Campos */

    private JLabel nameLabel = new JLabel();

    /** Descripción de Campos */

    private JTextField nameField = new JTextField();

    /** Descripción de Campos */

    private JButton newButton = new JButton();

    /** Descripción de Campos */

    private CPanel accountPanel = new CPanel();

    /** Descripción de Campos */

    private TitledBorder accountBorder;

    /** Descripción de Campos */

    private BorderLayout accountLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel accountOKPanel = new CPanel();

    /** Descripción de Campos */

    private JButton accountButton = new JButton();

    /** Descripción de Campos */

    private FlowLayout accountOKLayout = new FlowLayout();

    /** Descripción de Campos */

    private JScrollPane dataPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable dataTable = new MiniTable();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        newBorder     = new TitledBorder( "" );
        accountBorder = new TitledBorder( "" );
        mainPanel.setLayout( mainLayout );
        newPanel.setBorder( newBorder );
        newPanel.setLayout( newLayout );
        newBorder.setTitle( Msg.getMsg( Env.getCtx(),"ChargeNewAccount" ));
        valueLabel.setText( Msg.translate( Env.getCtx(),"Value" ));
        isExpense.setSelected( true );
        isExpense.setText( Msg.getMsg( Env.getCtx(),"Expense" ));
        nameLabel.setText( Msg.translate( Env.getCtx(),"Name" ));
        nameField.setColumns( 20 );
        valueField.setColumns( 10 );
        newButton.setText( Msg.getMsg( Env.getCtx(),"Create" ));
        newButton.addActionListener( this );
        accountPanel.setBorder( accountBorder );
        accountPanel.setLayout( accountLayout );
        accountBorder.setTitle( Msg.getMsg( Env.getCtx(),"ChargeFromAccount" ));
        accountButton.setText( Msg.getMsg( Env.getCtx(),"Create" ));
        accountButton.addActionListener( this );
        accountOKPanel.setLayout( accountOKLayout );
        accountOKLayout.setAlignment( FlowLayout.RIGHT );
        confirmPanel.addActionListener( this );

        //

        mainPanel.add( newPanel,BorderLayout.NORTH );
        newPanel.add( valueLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        newPanel.add( valueField,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,5,5 ),0,0 ));
        newPanel.add( nameLabel,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        newPanel.add( nameField,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,0,5,5 ),0,0 ));
        newPanel.add( isExpense,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        newPanel.add( newButton,new GridBagConstraints( 2,1,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        mainPanel.add( accountPanel,BorderLayout.CENTER );
        accountPanel.add( accountOKPanel,BorderLayout.SOUTH );
        accountOKPanel.add( accountButton,null );
        accountPanel.add( dataPane,BorderLayout.CENTER );
        dataPane.getViewport().add( dataTable,null );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {
        m_C_AcctSchema_ID = Env.getContextAsInt( Env.getCtx(),"$C_AcctSchema_ID" );

        // get Element

        String sql = "SELECT C_Element_ID FROM C_AcctSchema_Element " + "WHERE ElementType='AC' AND C_AcctSchema_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_C_AcctSchema_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_C_Element_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VCharge.dynInit-ase",e );
        }

        if( m_C_Element_ID == 0 ) {
            return;
        }

        // Table

        Vector data = new Vector();

        sql = "SELECT C_ElementValue_ID,Value, Name, AccountType " + "FROM C_ElementValue " + "WHERE AccountType IN ('R','E')" + " AND IsSummary='N'" + " AND C_Element_ID=? " + "ORDER BY 2";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_C_Element_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                Vector line = new Vector( 4 );

                line.add( new Boolean( false ));    // 0-Selection

                KeyNamePair pp = new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 ));

                line.add( pp );                             // 1-Value
                line.add( rs.getString( 3 ));               // 2-Name

                boolean isExpenseType = rs.getString( 4 ).equals( "E" );

                line.add( new Boolean( isExpenseType ));    // 3-Expense
                data.add( line );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VCharge.dynInit-2",e );
        }

        // Header Info

        Vector columnNames = new Vector( 4 );

        columnNames.add( Msg.getMsg( Env.getCtx(),"Select" ));
        columnNames.add( Msg.translate( Env.getCtx(),"Value" ));
        columnNames.add( Msg.translate( Env.getCtx(),"Name" ));
        columnNames.add( Msg.getMsg( Env.getCtx(),"Expense" ));

        // Set Model

        DefaultTableModel model = new DefaultTableModel( data,columnNames );

        dataTable.setModel( model );

        //

        dataTable.setColumnClass( 0,Boolean.class,false );    // 0-Selection
        dataTable.setColumnClass( 1,String.class,true );      // 1-Value
        dataTable.setColumnClass( 2,String.class,true );      // 2-Name
        dataTable.setColumnClass( 3,Boolean.class,true );     // 3-Expense

        // Table UI

        dataTable.autoSize();

        // Other Defaults

        m_AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());
        m_AD_Org_ID    = Env.getAD_Org_ID( Env.getCtx());
        m_CreatedBy    = Env.getAD_User_ID( Env.getCtx());

        // TaxCategory

        sql = "SELECT C_TaxCategory_ID FROM C_TaxCategory " + "WHERE IsDefault='Y' AND AD_Client_ID=?";
        m_C_TaxCategory_ID = 0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_C_TaxCategory_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VCharge.dynInit-tc",e );
        }
    }    // dynInit

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

        if( e.getActionCommand().equals( ConfirmPanel.A_OK ) || (m_C_Element_ID == 0) ) {
            dispose();

            // new Account

        } else if( e.getSource().equals( newButton )) {
            createNew();
        } else if( e.getSource().equals( accountButton )) {
            createAccount();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void createNew() {
        log.config( "VCharge.createNew" );

        // Get Input

        String value = valueField.getText();

        if( value.length() == 0 ) {
            valueField.setBackground( CompierePLAF.getFieldBackground_Error());

            return;
        }

        String name = nameField.getText();

        if( name.length() == 0 ) {
            nameField.setBackground( CompierePLAF.getFieldBackground_Error());

            return;
        }

        // Create Element

        int C_ElementValue_ID = create_ElementValue( value,name,isExpense.isSelected());

        if( C_ElementValue_ID == 0 ) {
            ADialog.error( m_WindowNo,this,"ChargeNotCreated",name );

            return;
        }

        // Create Charge

        int C_Charge_ID = create_Charge( name,C_ElementValue_ID );

        if( C_Charge_ID == 0 ) {
            ADialog.error( m_WindowNo,this,"ChargeNotCreated",name );

            return;
        }

        ADialog.info( m_WindowNo,this,"ChargeCreated",name );
    }    // createNew

    /**
     * Descripción de Método
     *
     */

    private void createAccount() {
        log.config( "VCharge.createAccount" );

        //

        StringBuffer listCreated  = new StringBuffer();
        StringBuffer listRejected = new StringBuffer();

        //

        TableModel model = dataTable.getModel();
        int        rows  = model.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            if((( Boolean )model.getValueAt( i,0 )).booleanValue()) {
                KeyNamePair pp                = ( KeyNamePair )model.getValueAt( i,1 );
                int         C_ElementValue_ID = pp.getKey();
                String      name              = ( String )model.getValueAt( i,2 );

                //

                int C_Charge_ID = create_Charge( name,C_ElementValue_ID );

                if( C_Charge_ID == 0 ) {
                    if( listRejected.length() > 0 ) {
                        listRejected.append( ", " );
                    }

                    listRejected.append( name );
                } else {
                    if( listCreated.length() > 0 ) {
                        listCreated.append( ", " );
                    }

                    listCreated.append( name );
                }

                // reset selection

                model.setValueAt( new Boolean( false ),i,0 );
            }
        }

        if( listCreated.length() > 0 ) {
            ADialog.info( m_WindowNo,this,"ChargeCreated",listCreated.toString());
        }

        if( listRejected.length() > 0 ) {
            ADialog.error( m_WindowNo,this,"ChargeNotCreated",listRejected.toString());
        }
    }    // createAccount

    /**
     * Descripción de Método
     *
     *
     * @param value
     * @param name
     * @param isExpenseType
     *
     * @return
     */

    private int create_ElementValue( String value,String name,boolean isExpenseType ) {
        log.config( "VCharge.create_ElementValue - " + name );

        //

        int C_ElementValue_ID = DB.getNextID( Env.getCtx(),"C_ElementValue",null );
        StringBuffer sql = new StringBuffer( "INSERT INTO C_ElementValue " + "(C_ElementValue_ID, C_Element_ID," + " AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy," + " Value, Name, Description," + " AccountType, AccountSign," + " IsDocControlled, IsSummary," + "     ValidFrom, ValidTo," + " PostActual, PostBudget, PostEncumbrance, PostStatistical," + "     IsBankAccount, C_BankAccount_ID, IsForeignCurrency, C_Currency_ID" + ") VALUES (" );

        // C_ElementValue_ID, C_Element_ID,

        sql.append( C_ElementValue_ID ).append( "," ).append( m_C_Element_ID ).append( "," );

        // AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy,

        sql.append( m_AD_Client_ID ).append( "," ).append( m_AD_Org_ID );
        sql.append( ",'Y',SysDate," ).append( m_CreatedBy ).append( ",SysDate," ).append( m_CreatedBy ).append( "," );

        // Value, Name, Description,

        sql.append( "'" ).append( value ).append( "','" ).append( name ).append( "',NULL," );

        // AccountType, AccountSign,

        sql.append( isExpenseType
                    ?"'E'"
                    :"'R'" ).append( ",'N'," );

        // IsDocControlled, IsSummary,

        sql.append( "'N','N'," );

        // ValidFrom, ValidTo,

        sql.append( "TO_DATE('01-JAN-1970'),NULL," );

        // PostActual, PostBudget, PostEncumbrance, PostStatistical,

        sql.append( "'Y','Y','Y','Y'," );

        // IsBankAccount, C_BankAccount_ID, IsForeignCurrency, C_Currency_ID

        sql.append( "'N',NULL,'N',NULL)" );

        //

        int no = DB.executeUpdate( sql.toString());

        if( no != 1 ) {
            log.log( Level.SEVERE,"VCharge.create_ElementValue #" + no + "\n" + sql.toString());
            C_ElementValue_ID = 0;
        } else {
            log.config( "VCharge.create_ElementValue - C_ElementValue_ID=" + C_ElementValue_ID + " " + value + "=" + name );
        }

        return C_ElementValue_ID;
    }    // create_ElementValue

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param C_ElementValue_ID
     *
     * @return
     */

    private int create_Charge( String name,int C_ElementValue_ID ) {
        log.config( name + " - " );

        //

        MCharge charge = new MCharge( Env.getCtx(),0,null );

        charge.setName( name );
        charge.setC_TaxCategory_ID( m_C_TaxCategory_ID );

        if( !charge.save()) {
            log.log( Level.SEVERE,name + " not created" );

            return 0;
        }

        // Get AcctSchama

        if( m_acctSchema == null ) {
            m_acctSchema = new MAcctSchema( Env.getCtx(),m_C_AcctSchema_ID,null );
        }

        if( (m_acctSchema == null) || (m_acctSchema.getC_AcctSchema_ID() == 0) ) {
            return 0;
        }

        // Target Account

        MAccount defaultAcct = MAccount.getDefault( m_acctSchema,true );    // optional null
        MAccount acct = MAccount.get( Env.getCtx(),charge.getAD_Client_ID(),charge.getAD_Org_ID(),m_acctSchema.getC_AcctSchema_ID(),C_ElementValue_ID,defaultAcct.getM_Product_ID(),defaultAcct.getC_BPartner_ID(),defaultAcct.getAD_OrgTrx_ID(),defaultAcct.getC_LocFrom_ID(),defaultAcct.getC_LocTo_ID(),defaultAcct.getC_SalesRegion_ID(),defaultAcct.getC_Project_ID(),defaultAcct.getC_Campaign_ID(),defaultAcct.getC_Activity_ID(),defaultAcct.getUser1_ID(),defaultAcct.getUser2_ID());

        if( acct == null ) {
            return 0;
        }

        // Update Accounts

        StringBuffer sql = new StringBuffer( "UPDATE C_Charge_Acct " );

        sql.append( "SET CH_Expense_Acct=" ).append( acct.getC_ValidCombination_ID());
        sql.append( ", CH_Revenue_Acct=" ).append( acct.getC_ValidCombination_ID());
        sql.append( " WHERE C_Charge_ID=" ).append( charge.getC_Charge_ID());
        sql.append( " AND C_AcctSchema_ID=" ).append( m_C_AcctSchema_ID );

        //

        int no = DB.executeUpdate( sql.toString());

        if( no != 1 ) {
            log.log( Level.SEVERE,"Update #" + no + "\n" + sql.toString());
        }

        //

        return charge.getC_Charge_ID();
    }    // create_Charge
}    // VCharge



/*
 *  @(#)VCharge.java   02.07.07
 * 
 *  Fin del fichero VCharge.java
 *  
 *  Versión 2.2
 *
 */
