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
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.grid.ed.VCheckBox;
import org.openXpertya.grid.ed.VComboBox;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MPaySelection;
import org.openXpertya.model.MPaySelectionLine;
import org.openXpertya.model.MRole;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VPaySelect extends CPanel implements FormPanel,ActionListener,TableModelListener,ASyncProcess {

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
            frame.getContentPane().add( commandPanel,BorderLayout.SOUTH );
            frame.getContentPane().add( mainPanel,BorderLayout.CENTER );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VPaySelect.init",e );
        }
    }    // init

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private DecimalFormat m_format = DisplayType.getNumberFormat( DisplayType.Amount );

    /** Descripción de Campos */

    private BigDecimal m_bankBalance = new BigDecimal( 0.0 );

    /** Descripción de Campos */

    private String m_sql;

    /** Descripción de Campos */

    private int m_noSelected = 0;

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /*  */

    /** Descripción de Campos */

    private boolean m_isLocked = false;

    /** Descripción de Campos */

    private MPaySelection m_ps = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VPaySelect.class );

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel parameterPanel = new CPanel();

    /** Descripción de Campos */

    private CLabel labelBankAccount = new CLabel();

    /** Descripción de Campos */

    private VComboBox fieldBankAccount = new VComboBox();

    /** Descripción de Campos */

    private GridBagLayout parameterLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel labelBankBalance = new CLabel();

    /** Descripción de Campos */

    private CLabel labelCurrency = new CLabel();

    /** Descripción de Campos */

    private CLabel labelBalance = new CLabel();

    /** Descripción de Campos */

    private VCheckBox onlyDue = new VCheckBox();

    /** Descripción de Campos */

    private CLabel labelBPartner = new CLabel();

    /** Descripción de Campos */

    private VComboBox fieldBPartner = new VComboBox();

    /** Descripción de Campos */

    private JLabel dataStatus = new JLabel();

    /** Descripción de Campos */

    private JScrollPane dataPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable miniTable = new MiniTable();

    /** Descripción de Campos */

    private CPanel commandPanel = new CPanel();

    /** Descripción de Campos */

    private JButton bCancel = ConfirmPanel.createCancelButton( true );

    /** Descripción de Campos */

    private JButton bGenerate = ConfirmPanel.createProcessButton( true );

    /** Descripción de Campos */

    private FlowLayout commandLayout = new FlowLayout();

    /** Descripción de Campos */

    private JButton bRefresh = ConfirmPanel.createRefreshButton( true );

    /** Descripción de Campos */

    private CLabel labelPayDate = new CLabel();

    /** Descripción de Campos */

    private VDate fieldPayDate = new VDate();

    /** Descripción de Campos */

    private CLabel labelPaymentRule = new CLabel();

    /** Descripción de Campos */

    private VComboBox fieldPaymentRule = new VComboBox();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );

        //

        mainPanel.setLayout( mainLayout );
        parameterPanel.setLayout( parameterLayout );

        //

        labelBankAccount.setText( Msg.translate( Env.getCtx(),"C_BankAccount_ID" ));
        fieldBankAccount.addActionListener( this );
        labelBPartner.setText( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
        fieldBPartner.addActionListener( this );
        bRefresh.addActionListener( this );
        labelPayDate.setText( Msg.translate( Env.getCtx(),"PayDate" ));
        labelPaymentRule.setText( Msg.translate( Env.getCtx(),"PaymentRule" ));

        //

        labelBankBalance.setText( Msg.translate( Env.getCtx(),"CurrentBalance" ));
        labelBalance.setText( "0" );
        onlyDue.setText( Msg.getMsg( Env.getCtx(),"OnlyDue" ));
        dataStatus.setText( " " );

        //

        bGenerate.addActionListener( this );
        bCancel.addActionListener( this );

        //

        mainPanel.add( parameterPanel,BorderLayout.NORTH );
        parameterPanel.add( labelBankAccount,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( fieldBankAccount,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( labelBankBalance,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( labelCurrency,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,0,5,5 ),0,0 ));
        parameterPanel.add( labelBalance,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( labelBPartner,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( fieldBPartner,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( bRefresh,new GridBagConstraints( 4,2,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( labelPayDate,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( fieldPayDate,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( labelPaymentRule,new GridBagConstraints( 2,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( fieldPaymentRule,new GridBagConstraints( 3,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        parameterPanel.add( onlyDue,new GridBagConstraints( 3,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        mainPanel.add( dataStatus,BorderLayout.SOUTH );
        mainPanel.add( dataPane,BorderLayout.CENTER );
        dataPane.getViewport().add( miniTable,null );

        //

        commandPanel.setLayout( commandLayout );
        commandLayout.setAlignment( FlowLayout.RIGHT );
        commandLayout.setHgap( 10 );
        commandPanel.add( bCancel,null );
        commandPanel.add( bGenerate,null );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {
        Properties ctx = Env.getCtx();

        // Bank Account Info

        String sql = MRole.getDefault().addAccessSQL( "SELECT ba.C_BankAccount_ID,"    // 1
            + "b.Name || ' ' || ba.AccountNo AS Name,"    // 2
            + "ba.C_Currency_ID, c.ISO_Code,"             // 3..4
            + "ba.CurrentBalance "                        // 5
            + "FROM C_Bank b, C_BankAccount ba, C_Currency c " + "WHERE b.C_Bank_ID=ba.C_Bank_ID" + " AND ba.C_Currency_ID=c.C_Currency_ID " + " AND EXISTS (SELECT * FROM C_BankAccountDoc d WHERE d.C_BankAccount_ID=ba.C_BankAccount_ID) " + "ORDER BY 2","b",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RW );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                boolean  transfers = false;
                BankInfo bi        = new BankInfo( rs.getInt( 1 ),rs.getInt( 3 ),rs.getString( 2 ),rs.getString( 4 ),rs.getBigDecimal( 5 ),transfers );

                fieldBankAccount.addItem( bi );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        if( fieldBankAccount.getItemCount() == 0 ) {
            ADialog.error( m_WindowNo,this,"VPaySelectNoBank" );
        } else {
            fieldBankAccount.setSelectedIndex( 0 );
        }

        // Optional BusinessPartner with unpaid AP Invoices

        KeyNamePair pp = new KeyNamePair( 0,"" );

        fieldBPartner.addItem( pp );
        sql = MRole.getDefault().addAccessSQL( "SELECT bp.C_BPartner_ID, bp.Name FROM C_BPartner bp","bp",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO ) + " AND EXISTS (SELECT * FROM C_Invoice i WHERE bp.C_BPartner_ID=i.C_BPartner_ID" + " AND i.IsSOTrx='N' AND i.IsPaid<>'Y') " + "ORDER BY 2";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                pp = new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 ));
                fieldBPartner.addItem( pp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        fieldBPartner.setSelectedIndex( 0 );
        m_sql = miniTable.prepareTable( new ColumnInfo[] {

            // 0..4

            new ColumnInfo( " ","i.C_Invoice_ID",IDColumn.class,false,false,null ),new ColumnInfo( Msg.translate( ctx,"DateDue" ),"i.DateInvoiced+p.NetDays AS DateDue",Timestamp.class,true,true,null ),new ColumnInfo( Msg.translate( ctx,"C_BPartner_ID" ),"bp.Name",KeyNamePair.class,true,false,"i.C_BPartner_ID" ),new ColumnInfo( Msg.translate( ctx,"DocumentNo" ),"i.DocumentNo",String.class ),new ColumnInfo( Msg.translate( ctx,"C_Currency_ID" ),"c.ISO_Code",KeyNamePair.class,true,false,"i.C_Currency_ID" ),

            // 5..9

            new ColumnInfo( Msg.translate( ctx,"GrandTotal" ),"i.GrandTotal",BigDecimal.class ),new ColumnInfo( Msg.translate( ctx,"DiscountAmt" ),"paymentTermDiscount(i.GrandTotal,i.C_PaymentTerm_ID,i.DateInvoiced, ?)",BigDecimal.class ),new ColumnInfo( Msg.getMsg( ctx,"DiscountDate" ),"SysDate-paymentTermDueDays(i.C_PaymentTerm_ID,i.DateInvoiced,SysDate)",Timestamp.class ),new ColumnInfo( Msg.getMsg( ctx,"AmountDue" ),"currencyConvert(invoiceOpen(i.C_Invoice_ID,i.C_InvoicePaySchedule_ID),i.C_Currency_ID, ?,?,i.C_ConversionType_ID, i.AD_Client_ID,i.AD_Org_ID)",BigDecimal.class ),new ColumnInfo( Msg.getMsg( ctx,"AmountPay" ),"currencyConvert(invoiceOpen(i.C_Invoice_ID,i.C_InvoicePaySchedule_ID)-paymentTermDiscount(i.GrandTotal,i.C_PaymentTerm_ID,i.DateInvoiced, ?),i.C_Currency_ID, ?,?,i.C_ConversionType_ID, i.AD_Client_ID,i.AD_Org_ID)",BigDecimal.class )
        },

        // FROM

        "C_Invoice_v i" + " INNER JOIN C_BPartner bp ON (i.C_BPartner_ID=bp.C_BPartner_ID)" + " INNER JOIN C_Currency c ON (i.C_Currency_ID=c.C_Currency_ID)" + " INNER JOIN C_PaymentTerm p ON (i.C_PaymentTerm_ID=p.C_PaymentTerm_ID)",

        // WHERE

        "i.IsSOTrx='N' AND IsPaid='N'"

        // Different Payment Selection

        + " AND NOT EXISTS (SELECT * FROM C_PaySelectionLine psl" + " WHERE i.C_Invoice_ID=psl.C_Invoice_ID AND psl.C_PaySelectionCheck_ID IS NOT NULL)" + " AND i.DocStatus IN ('CO','CL')" + " AND i.AD_Client_ID=?",    // additional where & order in loadTableInfo()
        true,"i" );

        //

        miniTable.getModel().addTableModelListener( this );

        //

        fieldPayDate.setMandatory( true );
        fieldPayDate.setValue( new Timestamp( System.currentTimeMillis()));

        //

        m_AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void loadBankInfo() {
        BankInfo bi = ( BankInfo )fieldBankAccount.getSelectedItem();

        if( bi == null ) {
            return;
        }

        labelCurrency.setText( bi.Currency );
        labelBalance.setText( m_format.format( bi.Balance ));
        m_bankBalance = bi.Balance;

        // PaymentRule

        fieldPaymentRule.removeAllItems();

        int AD_Reference_ID = 195;    // MLookupInfo.getAD_Reference_ID("All_Payment Rule");
        Language    language = Env.getLanguage( Env.getCtx());
        MLookupInfo info     = MLookupFactory.getLookup_List( language,AD_Reference_ID );
        String sql = info.Query.substring( 0,info.Query.indexOf( " ORDER BY" )) + " AND " + info.KeyColumn + " IN (SELECT PaymentRule FROM C_BankAccountDoc WHERE C_BankAccount_ID=?) " + info.Query.substring( info.Query.indexOf( " ORDER BY" ));

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,bi.C_BankAccount_ID );

            ResultSet     rs = pstmt.executeQuery();
            ValueNamePair vp = null;

            while( rs.next()) {
                vp = new ValueNamePair( rs.getString( 2 ),rs.getString( 3 ));    // returns also not active
                fieldPaymentRule.addItem( vp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VPaySelect.loadBankInfo - SQL=" + sql,e );
        }

        fieldPaymentRule.setSelectedIndex( 0 );
    }    // loadBankInfo

    /**
     * Descripción de Método
     *
     */

    private void loadTableInfo() {
        log.config( "" );

        // not yet initialized

        if( m_sql == null ) {
            return;
        }

        String sql = m_sql;

        // Parameters

        Timestamp payDate = ( Timestamp )fieldPayDate.getValue();

        miniTable.setColorCompare( payDate );
        log.config( "PayDate=" + payDate );

        BankInfo bi = ( BankInfo )fieldBankAccount.getSelectedItem();

        //

        if( onlyDue.isSelected()) {
            sql += " AND i.DateInvoiced+p.NetDays <= ?";
        }

        //

        KeyNamePair pp            = ( KeyNamePair )fieldBPartner.getSelectedItem();
        int         C_BPartner_ID = pp.getKey();

        if( C_BPartner_ID != 0 ) {
            sql += " AND i.C_BPartner_ID=?";
        }

        sql += " ORDER BY 2,3";

        //

        log.finest( sql + " - C_Currecny_ID=" + bi.C_Currency_ID + ", C_BPartner_ID=" + C_BPartner_ID );

        // Get Open Invoices

        try {
            int               index = 1;
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setTimestamp( index++,payDate );       // DiscountAmt
            pstmt.setInt( index++,bi.C_Currency_ID );    // DueAmt
            pstmt.setTimestamp( index++,payDate );
            pstmt.setTimestamp( index++,payDate );       // PayAmt
            pstmt.setInt( index++,bi.C_Currency_ID );
            pstmt.setTimestamp( index++,payDate );
            pstmt.setInt( index++,m_AD_Client_ID );      //

            if( onlyDue.isSelected()) {
                pstmt.setTimestamp( index++,payDate );
            }

            if( C_BPartner_ID != 0 ) {
                pstmt.setInt( index++,C_BPartner_ID );
            }

            //

            ResultSet rs = pstmt.executeQuery();

            miniTable.loadTable( rs );
            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VPaySelect.loadTableInfo - " + sql,e );
        }

        calculateSelection();
    }    // loadTableInfo

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

        // Update Bank Info

        if( e.getSource() == fieldBankAccount ) {
            loadBankInfo();

            // Generate PaySelection

        } else if( e.getSource() == bGenerate ) {
            generatePaySelect();
            dispose();
        } else if( e.getSource() == bCancel ) {
            dispose();

            // Update Open Invoices

        } else if( (e.getSource() == fieldBPartner) || (e.getSource() == bRefresh) ) {
            loadTableInfo();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void tableChanged( TableModelEvent e ) {
        if( e.getColumn() == 0 ) {
            calculateSelection();
        }
    }    // valueChanged

    /**
     * Descripción de Método
     *
     */

    public void calculateSelection() {
        m_noSelected = 0;

        BigDecimal invoiceAmt = new BigDecimal( 0.0 );
        int        rows       = miniTable.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getModel().getValueAt( i,0 );

            if( id.isSelected()) {
                BigDecimal amt = ( BigDecimal )miniTable.getModel().getValueAt( i,9 );

                invoiceAmt = invoiceAmt.add( amt );
                m_noSelected++;
            }
        }

        // Information

        BigDecimal   remaining = m_bankBalance.subtract( invoiceAmt );
        StringBuffer info      = new StringBuffer();

        info.append( m_noSelected ).append( " " ).append( Msg.getMsg( Env.getCtx(),"Selected" )).append( " - " );
        info.append( m_format.format( invoiceAmt )).append( ", " );
        info.append( Msg.getMsg( Env.getCtx(),"Remaining" )).append( " " ).append( m_format.format( remaining ));
        dataStatus.setText( info.toString());

        //

        bGenerate.setEnabled( m_noSelected != 0 );
    }    // calculateSelection

    /**
     * Descripción de Método
     *
     */

    private void generatePaySelect() {
        log.info( "" );

        // String trxName Trx.createTrxName("PaySelect");
        // Trx trx = Trx.get(trxName, true);       trx needs to be committed too

        String trxName = null;
        Trx    trx     = null;

        //

        miniTable.stopEditor( true );

        if( miniTable.getRowCount() == 0 ) {
            return;
        }

        miniTable.setRowSelectionInterval( 0,0 );
        calculateSelection();

        if( m_noSelected == 0 ) {
            return;
        }

        String PaymentRule = (( ValueNamePair )fieldPaymentRule.getSelectedItem()).getValue();

        // Create Header

        m_ps = new MPaySelection( Env.getCtx(),0,trxName );
        m_ps.setName( Msg.getMsg( Env.getCtx(),"VPaySelect" ) + " - " + fieldPayDate.getTimestamp());
        m_ps.setPayDate( fieldPayDate.getTimestamp());

        BankInfo bi = ( BankInfo )fieldBankAccount.getSelectedItem();

        m_ps.setC_BankAccount_ID( bi.C_BankAccount_ID );
        m_ps.setIsApproved( true );

        if( !m_ps.save()) {
            ADialog.error( m_WindowNo,this,"SaveError",Msg.translate( Env.getCtx(),"C_PaySelection_ID" ));
            m_ps = null;

            return;
        }

        log.config( m_ps.toString());

        // Create Lines

        int rows = miniTable.getRowCount();
        int line = 0;

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getModel().getValueAt( i,0 );

            if( id.isSelected()) {
                line += 10;

                MPaySelectionLine psl = new MPaySelectionLine( m_ps,line,PaymentRule );
                int        C_Invoice_ID = id.getRecord_ID().intValue();
                BigDecimal OpenAmt      = ( BigDecimal )miniTable.getModel().getValueAt( i,8 );
                BigDecimal PayAmt = ( BigDecimal )miniTable.getModel().getValueAt( i,9 );
                boolean isSOTrx = false;

                //

                psl.setInvoice( C_Invoice_ID,isSOTrx,OpenAmt,PayAmt,OpenAmt.subtract( PayAmt ));

                if( !psl.save( trxName )) {
                    ADialog.error( m_WindowNo,this,"SaveError",Msg.translate( Env.getCtx(),"C_PaySelectionLine_ID" ));

                    return;
                }

                log.fine( "C_Invoice_ID=" + C_Invoice_ID + ", PayAmt=" + PayAmt );
            }
        }    // for all rows in table

        // Ask to Post it

        if( !ADialog.ask( m_WindowNo,this,"VPaySelectGenerate?","(" + m_ps.getName() + ")" )) {
            return;
        }

        // Prepare Process PaySelectionCreateCheck

        ProcessInfo pi = new ProcessInfo( m_frame.getTitle(),155,MPaySelection.Table_ID,m_ps.getC_PaySelection_ID());

        pi.setAD_User_ID( Env.getAD_User_ID( Env.getCtx()));
        pi.setAD_Client_ID( Env.getAD_Client_ID( Env.getCtx()));

        // Execute Process

        ProcessCtl.process( this,m_WindowNo,pi,trx );

        // ProcessCtl worker = new ProcessCtl(this, pi, trx);
        // worker.start();     //  complete tasks in unlockUI

    }    // generatePaySelect

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void lockUI( ProcessInfo pi ) {
        this.setEnabled( false );
        m_isLocked = true;
    }    // lockUI

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void unlockUI( ProcessInfo pi ) {

        // this.setEnabled(true);
        // m_isLocked = false;
        // Ask to Print it

        if( !ADialog.ask( m_WindowNo,this,"VPaySelectPrint?","(" + pi.getSummary() + ")" )) {
            return;
        }

        // Start PayPrint

        int       AD_Form_ID = 106;    // Payment Print/Export
        FormFrame ff         = new FormFrame();

        ff.openForm( AD_Form_ID );

        // Set Parameter

        if( m_ps != null ) {
            VPayPrint pp = ( VPayPrint )ff.getFormPanel();

            pp.setPaySelection( m_ps.getC_PaySelection_ID());
        }

        //

        ff.pack();
        this.setVisible( false );
        AEnv.showCenterScreen( ff );
        this.dispose();
    }    // unlockUI

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUILocked() {
        return m_isLocked;
    }    // isLoacked

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void executeASync( ProcessInfo pi ) {
        log.config( "VPaySelect.executeASync" );
    }    // executeASync

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    public class BankInfo {

        /**
         * Constructor de la clase ...
         *
         *
         * @param newC_BankAccount_ID
         * @param newC_Currency_ID
         * @param newName
         * @param newCurrency
         * @param newBalance
         * @param newTransfers
         */

        public BankInfo( int newC_BankAccount_ID,int newC_Currency_ID,String newName,String newCurrency,BigDecimal newBalance,boolean newTransfers ) {
            C_BankAccount_ID = newC_BankAccount_ID;
            C_Currency_ID    = newC_Currency_ID;
            Name             = newName;
            Currency         = newCurrency;
            Balance          = newBalance;
        }

        /** Descripción de Campos */

        int C_BankAccount_ID;

        /** Descripción de Campos */

        int C_Currency_ID;

        /** Descripción de Campos */

        String Name;

        /** Descripción de Campos */

        String Currency;

        /** Descripción de Campos */

        BigDecimal Balance;

        /** Descripción de Campos */

        boolean Transfers;

        /**
         * Descripción de Método
         *
         *
         * @return
         */

        public String toString() {
            return Name;
        }
    }    // BankInfo
}        // VPaySelect



/*
 *  @(#)VPaySelect.java   02.07.07
 * 
 *  Fin del fichero VPaySelect.java
 *  
 *  Versión 2.2
 *
 */
