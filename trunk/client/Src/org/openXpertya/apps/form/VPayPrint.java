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
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MPaySelectionCheck;
import org.openXpertya.model.MPaymentBatch;
import org.openXpertya.print.ReportCtl;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VPayPrint extends CPanel implements FormPanel,ActionListener {

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "VPayPrint.init" );
        m_WindowNo = WindowNo;
        m_frame    = frame;

        try {
            jbInit();
            dynInit();
            frame.getContentPane().add( centerPanel,BorderLayout.CENTER );
            frame.getContentPane().add( southPanel,BorderLayout.SOUTH );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VPayPrint.init",e );
        }
    }    // init

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private int m_C_BankAccount_ID = -1;

    /** Descripción de Campos */

    private MPaySelectionCheck[] m_checks = null;

    /** Descripción de Campos */

    private MPaymentBatch m_batch = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VPayPrint.class );

    // Static Variables

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private FlowLayout southLayout = new FlowLayout();

    /** Descripción de Campos */

    private GridBagLayout centerLayout = new GridBagLayout();

    /** Descripción de Campos */

    private JButton bPrint = ConfirmPanel.createPrintButton( true );

    /** Descripción de Campos */

    private JButton bExport = ConfirmPanel.createExportButton( true );

    /** Descripción de Campos */

    private JButton bCancel = ConfirmPanel.createCancelButton( true );

    /** Descripción de Campos */

    private JButton bProcess = ConfirmPanel.createProcessButton( Msg.getMsg( Env.getCtx(),"VPayPrintProcess" ));

    /** Descripción de Campos */

    private CLabel lPaySelect = new CLabel();

    /** Descripción de Campos */

    private CComboBox fPaySelect = new CComboBox();

    /** Descripción de Campos */

    private CLabel lBank = new CLabel();

    /** Descripción de Campos */

    private CLabel fBank = new CLabel();

    /** Descripción de Campos */

    private CLabel lPaymentRule = new CLabel();

    /** Descripción de Campos */

    private CComboBox fPaymentRule = new CComboBox();

    /** Descripción de Campos */

    private CLabel lDocumentNo = new CLabel();

    /** Descripción de Campos */

    private VNumber fDocumentNo = new VNumber();

    /** Descripción de Campos */

    private CLabel lNoPayments = new CLabel();

    /** Descripción de Campos */

    private CLabel fNoPayments = new CLabel();

    /** Descripción de Campos */

    private CLabel lBalance = new CLabel();

    /** Descripción de Campos */

    private VNumber fBalance = new VNumber();

    /** Descripción de Campos */

    private CLabel lCurrency = new CLabel();

    /** Descripción de Campos */

    private CLabel fCurrency = new CLabel();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );

        //

        southPanel.setLayout( southLayout );
        southLayout.setAlignment( FlowLayout.RIGHT );
        centerPanel.setLayout( centerLayout );

        //

        bPrint.addActionListener( this );
        bExport.addActionListener( this );
        bCancel.addActionListener( this );

        //

        bProcess.setText( Msg.getMsg( Env.getCtx(),"EFT" ));
        bProcess.setEnabled( false );
        bProcess.addActionListener( this );

        //

        lPaySelect.setText( Msg.translate( Env.getCtx(),"C_PaySelection_ID" ));
        fPaySelect.addActionListener( this );

        //

        lBank.setText( Msg.translate( Env.getCtx(),"C_BankAccount_ID" ));

        //

        lPaymentRule.setText( Msg.translate( Env.getCtx(),"PaymentRule" ));
        fPaymentRule.addActionListener( this );

        //

        lDocumentNo.setText( Msg.translate( Env.getCtx(),"DocumentNo" ));
        fDocumentNo.setDisplayType( DisplayType.Integer );
        lNoPayments.setText( Msg.getMsg( Env.getCtx(),"NoOfPayments" ));
        fNoPayments.setText( "0" );
        lBalance.setText( Msg.translate( Env.getCtx(),"CurrentBalance" ));
        fBalance.setReadWrite( false );
        fBalance.setDisplayType( DisplayType.Amount );
        lCurrency.setText( Msg.translate( Env.getCtx(),"C_Currency_ID" ));

        //

        southPanel.add( bCancel,null );
        southPanel.add( bExport,null );
        southPanel.add( bPrint,null );
        southPanel.add( bProcess,null );

        //

        centerPanel.add( lPaySelect,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 12,12,5,5 ),0,0 ));
        centerPanel.add( fPaySelect,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 12,0,5,12 ),0,0 ));
        centerPanel.add( lBank,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        centerPanel.add( fBank,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,12 ),0,0 ));
        centerPanel.add( lPaymentRule,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        centerPanel.add( fPaymentRule,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,12 ),0,0 ));
        centerPanel.add( lDocumentNo,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        centerPanel.add( fDocumentNo,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,12 ),0,0 ));
        centerPanel.add( lNoPayments,new GridBagConstraints( 2,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        centerPanel.add( fNoPayments,new GridBagConstraints( 3,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,12 ),0,0 ));
        centerPanel.add( lBalance,new GridBagConstraints( 2,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        centerPanel.add( fBalance,new GridBagConstraints( 3,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,12 ),0,0 ));
        centerPanel.add( lCurrency,new GridBagConstraints( 2,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,12,5 ),0,0 ));
        centerPanel.add( fCurrency,new GridBagConstraints( 3,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,12,12 ),0,0 ));
    }    // VPayPrint

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {
        log.config( "VPayPrint.dynInit" );

        int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());

        // Load PaySelect

        String sql = "SELECT C_PaySelection_ID, Name || ' - ' || TotalAmt FROM C_PaySelection " + "WHERE AD_Client_ID=? AND Processed='Y' AND IsActive='Y'" + "ORDER BY PayDate DESC";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                KeyNamePair pp = new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 ));

                fPaySelect.addItem( pp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VPayPrint.dynInit",e );
        }

        if( fPaySelect.getItemCount() == 0 ) {
            ADialog.info( m_WindowNo,this,"VPayPrintNoRecords" );
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
     * @param C_PaySelection_ID
     */

    public void setPaySelection( int C_PaySelection_ID ) {
        if( C_PaySelection_ID == 0 ) {
            return;
        }

        //

        for( int i = 0;i < fPaySelect.getItemCount();i++ ) {
            KeyNamePair pp = ( KeyNamePair )fPaySelect.getItemAt( i );

            if( pp.getKey() == C_PaySelection_ID ) {
                fPaySelect.setSelectedIndex( i );

                return;
            }
        }
    }    // setsetPaySelection

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // log.config( "VPayPrint.actionPerformed" + e.toString());

        if( e.getSource() == fPaySelect ) {
            loadPaySelectInfo();
        } else if( e.getSource() == fPaymentRule ) {
            loadPaymentRuleInfo();

            //

        } else if( e.getSource() == bCancel ) {
            dispose();
        } else if( e.getSource() == bExport ) {
            cmd_export();
        } else if( e.getSource() == bProcess ) {
            cmd_EFT();
        } else if( e.getSource() == bPrint ) {
            cmd_print();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void loadPaySelectInfo() {
        log.info( "VPayPrint.loadPaySelectInfo" );

        if( fPaySelect.getSelectedIndex() == -1 ) {
            return;
        }

        // load Banks from PaySelectLine

        int C_PaySelection_ID = (( KeyNamePair )fPaySelect.getSelectedItem()).getKey();

        m_C_BankAccount_ID = -1;

        String sql = "SELECT ps.C_BankAccount_ID, b.Name || ' ' || ba.AccountNo,"    // 1..2
                     + " c.ISO_Code, CurrentBalance "    // 3..4
                     + "FROM C_PaySelection ps" + " INNER JOIN C_BankAccount ba ON (ps.C_BankAccount_ID=ba.C_BankAccount_ID)" + " INNER JOIN C_Bank b ON (ba.C_Bank_ID=b.C_Bank_ID)" + " INNER JOIN C_Currency c ON (ba.C_Currency_ID=c.C_Currency_ID) " + "WHERE ps.C_PaySelection_ID=? AND ps.Processed='Y' AND ba.IsActive='Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_PaySelection_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_C_BankAccount_ID = rs.getInt( 1 );
                fBank.setText( rs.getString( 2 ));
                fCurrency.setText( rs.getString( 3 ));
                fBalance.setValue( rs.getBigDecimal( 4 ));
            } else {
                m_C_BankAccount_ID = -1;
                fBank.setText( "" );
                fCurrency.setText( "" );
                fBalance.setValue( Env.ZERO );
                log.log( Level.SEVERE,"VPayPrint.loadPaySelectInfo - No active BankAccount for C_PaySelection_ID=" + C_PaySelection_ID );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VPayPrint.loadPaySelectInfo",e );
        }

        loadPaymentRule();
    }    // loadPaySelectInfo

    /**
     * Descripción de Método
     *
     */

    private void loadPaymentRule() {
        log.info( "VPayPrint.loadPaymentRule" );

        if( m_C_BankAccount_ID == -1 ) {
            return;
        }

        // load PaymentRule for Bank

        int C_PaySelection_ID = (( KeyNamePair )fPaySelect.getSelectedItem()).getKey();

        fPaymentRule.removeAllItems();

        int AD_Reference_ID = 195;    // MLookupInfo.getAD_Reference_ID("All_Payment Rule");
        Language language = Language.getLanguage( Env.getAD_Language( Env.getCtx()));
        MLookupInfo info = MLookupFactory.getLookup_List( language,AD_Reference_ID );
        String sql = info.Query.substring( 0,info.Query.indexOf( " ORDER BY" )) + " AND " + info.KeyColumn + " IN (SELECT PaymentRule FROM C_PaySelectionCheck WHERE C_PaySelection_ID=?) " + info.Query.substring( info.Query.indexOf( " ORDER BY" ));

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_PaySelection_ID );

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                ValueNamePair pp = new ValueNamePair( rs.getString( 2 ),rs.getString( 3 ));

                fPaymentRule.addItem( pp );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VPayPrint.loadPayBankInfo - SQL=" + sql,e );
        }

        if( fPaymentRule.getItemCount() == 0 ) {
            log.config( "PaySel=" + C_PaySelection_ID + ", BAcct=" + m_C_BankAccount_ID + " - " + sql );
        }

        loadPaymentRuleInfo();
    }    // loadPaymentRule

    /**
     * Descripción de Método
     *
     */

    private void loadPaymentRuleInfo() {
        ValueNamePair pp = ( ValueNamePair )fPaymentRule.getSelectedItem();

        if( pp == null ) {
            return;
        }

        String PaymentRule = pp.getValue();

        log.info( "PaymentRule=" + PaymentRule );
        fNoPayments.setText( " " );

        int C_PaySelection_ID = (( KeyNamePair )fPaySelect.getSelectedItem()).getKey();
        String sql = "SELECT COUNT(*) " + "FROM C_PaySelectionCheck " + "WHERE C_PaySelection_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_PaySelection_ID );

            ResultSet rs = pstmt.executeQuery();

            //

            if( rs.next()) {
                fNoPayments.setText( String.valueOf( rs.getInt( 1 )));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VPayPrint.loadPaymentRuleInfo",e );
        }

        bProcess.setEnabled( PaymentRule.equals( "T" ));

        // DocumentNo

        sql = "SELECT CurrentNext " + "FROM C_BankAccountDoc " + "WHERE C_BankAccount_ID=? AND PaymentRule=? AND IsActive='Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_C_BankAccount_ID );
            pstmt.setString( 2,PaymentRule );

            ResultSet rs = pstmt.executeQuery();

            //

            if( rs.next()) {
                fDocumentNo.setValue( new Integer( rs.getInt( 1 )));
            } else {
                log.log( Level.SEVERE,"VPayPrint.loadPaymentRuleInfo - No active BankAccountDoc for C_BankAccount_ID=" + m_C_BankAccount_ID + " AND PaymentRule=" + PaymentRule );
                ADialog.error( m_WindowNo,this,"VPayPrintNoDoc" );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VPayPrint.loadPaymentRuleInfo ba",e );
        }
    }    // loadPaymentRuleInfo

    /**
     * Descripción de Método
     *
     */

    private void cmd_export() {
        String PaymentRule = (( ValueNamePair )fPaymentRule.getSelectedItem()).getValue();

        log.info( PaymentRule );

        if( !getChecks( PaymentRule )) {
            return;
        }

        // Get File Info

        JFileChooser fc = new JFileChooser();

        fc.setDialogTitle( Msg.getMsg( Env.getCtx(),"Export" ));
        fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
        fc.setMultiSelectionEnabled( false );
        fc.setSelectedFile( new java.io.File( "paymentExport.txt" ));

        if( fc.showSaveDialog( this ) != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        // Create File

        int no = MPaySelectionCheck.exportToFile( m_checks,fc.getSelectedFile());

        ADialog.info( m_WindowNo,this,"Saved",fc.getSelectedFile().getAbsolutePath() + "\n" + Msg.getMsg( Env.getCtx(),"NoOfLines" ) + "=" + no );

        if( ADialog.ask( m_WindowNo,this,"VPayPrintSuccess?" )) {
            int lastDocumentNo = MPaySelectionCheck.confirmPrint( m_checks,m_batch );

            // document No not updated

        }

        dispose();
    }    // cmd_export

    /**
     * Descripción de Método
     *
     */

    private void cmd_EFT() {
        String PaymentRule = (( ValueNamePair )fPaymentRule.getSelectedItem()).getValue();

        log.info( PaymentRule );

        if( !getChecks( PaymentRule )) {
            return;
        }

        dispose();
    }    // cmd_EFT

    /**
     * Descripción de Método
     *
     */

    private void cmd_print() {
        String PaymentRule = (( ValueNamePair )fPaymentRule.getSelectedItem()).getValue();

        log.info( PaymentRule );

        if( !getChecks( PaymentRule )) {
            return;
        }

        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        boolean somethingPrinted = false;
        boolean directPrint      = !Ini.getPropertyBool( Ini.P_PRINTPREVIEW );

        // for all checks

        for( int i = 0;i < m_checks.length;i++ ) {
            MPaySelectionCheck check = m_checks[ i ];

            // ReportCtrl will check BankAccountDoc for PrintFormat

            boolean ok = ReportCtl.startDocumentPrint( ReportEngine.CHECK,check.getID(),directPrint );

            if( !somethingPrinted && ok ) {
                somethingPrinted = true;
            }
        }

        // Confirm Print and Update BankAccountDoc

        if( somethingPrinted && ADialog.ask( m_WindowNo,this,"VPayPrintSuccess?" )) {
            int lastDocumentNo = MPaySelectionCheck.confirmPrint( m_checks,m_batch );

            if( lastDocumentNo != 0 ) {
                StringBuffer sb = new StringBuffer();

                sb.append( "UPDATE C_BankAccountDoc SET CurrentNext=" ).append( ++lastDocumentNo ).append( " WHERE C_BankAccount_ID=" ).append( m_C_BankAccount_ID ).append( " AND PaymentRule='" ).append( PaymentRule ).append( "'" );
                DB.executeUpdate( sb.toString());
            }
        }    // confirm

        if( ADialog.ask( m_WindowNo,this,"VPayPrintPrintRemittance" )) {
            for( int i = 0;i < m_checks.length;i++ ) {
                MPaySelectionCheck check = m_checks[ i ];

                ReportCtl.startDocumentPrint( ReportEngine.REMITTANCE,check.getID(),directPrint );
            }
        }    // remittance

        this.setCursor( Cursor.getDefaultCursor());
        dispose();
    }    // cmd_print

    /**
     * Descripción de Método
     *
     *
     * @param PaymentRule
     *
     * @return
     */

    private boolean getChecks( String PaymentRule ) {

        // do we have values

        if( (fPaySelect.getSelectedIndex() == -1) || (m_C_BankAccount_ID == -1) || (fPaymentRule.getSelectedIndex() == -1) || (fDocumentNo.getValue() == null) ) {
            ADialog.error( m_WindowNo,this,"VPayPrintNoRecords","(" + Msg.translate( Env.getCtx(),"C_PaySelectionLine_ID" ) + "=0)" );

            return false;
        }

        // get data

        int C_PaySelection_ID = (( KeyNamePair )fPaySelect.getSelectedItem()).getKey();
        int startDocumentNo = (( Number )fDocumentNo.getValue()).intValue();

        log.config( "C_PaySelection_ID=" + C_PaySelection_ID + ", PaymentRule=" + PaymentRule + ", DocumentNo=" + startDocumentNo );

        //

        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        // get Checks

        m_checks = MPaySelectionCheck.get( C_PaySelection_ID,PaymentRule,startDocumentNo,null );
        this.setCursor( Cursor.getDefaultCursor());

        //

        if( (m_checks == null) || (m_checks.length == 0) ) {
            ADialog.error( m_WindowNo,this,"VPayPrintNoRecords","(" + Msg.translate( Env.getCtx(),"C_PaySelectionLine_ID" ) + " #0" );

            return false;
        }

        m_batch = MPaymentBatch.getForPaySelection( Env.getCtx(),C_PaySelection_ID,null );

        return true;
    }    // getChecks
}    // PayPrint



/*
 *  @(#)VPayPrint.java   02.07.07
 * 
 *  Fin del fichero VPayPrint.java
 *  
 *  Versión 2.2
 *
 */
