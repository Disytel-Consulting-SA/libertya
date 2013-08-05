package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.window.FDialog;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MTab;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

public class WCreateFromStatement  extends WCreateFrom {

    /**
     * Constructor de la clase ...
     *
     *
     * @param mTab
     */

    protected WCreateFromStatement( MTab mTab ) {
		super(mTab);
        log.info( mTab.toString());
		p_WindowNo = mTab.getWindowNo();
		AEnv.showWindow(window);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected boolean dynInit() throws Exception {
        if( p_mTab.getValue( "C_BankStatement_ID" ) == null ) {
            FDialog.error( 0,this,"SaveErrorRowNotFound" );

            return false;
        }

        setTitle( Msg.translate( Env.getCtx(),"C_BankStatement_ID" ) + " .. " + Msg.translate( Env.getCtx(),"CreateFrom" ));
        int     AD_Column_ID = 4917;    // C_BankStatement.C_BankAccount_ID
        MLookup lookup       = MLookupFactory.get( Env.getCtx(),p_WindowNo,0,AD_Column_ID,DisplayType.TableDir );

        bankAccountField = new WSearchEditor( "C_BankAccount_ID",true,true,true,lookup );
        bankAccountField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent e) {
		        log.config( e.getPropertyName() + "=" + e.getNewValue());
		        // BankAccount
		        if( e.getPropertyName() == "C_BankAccount_ID" ) {
		            int C_BankAccount_ID = (( Integer )e.getNewValue()).intValue();
		            loadBankAccount( C_BankAccount_ID );
		        }
		        window.tableChanged( null );
			}
		});

        // Set Default

        int C_BankAccount_ID = Env.getContextAsInt( Env.getCtx(),p_WindowNo,"C_BankAccount_ID" );

        bankAccountField.setValue( new Integer( C_BankAccount_ID ));

        // initial Loading

        loadBankAccount( C_BankAccount_ID );

        return true;
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    protected void initBPDetails( int C_BPartner_ID ) {}    // initDetails

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     */

    private void loadBankAccount( int C_BankAccount_ID ) {
        log.config( "C_BankAccount_ID=" + C_BankAccount_ID );

        List<Payment> data = new ArrayList<Payment>();
        
        StringBuffer sql = new StringBuffer(); 
        sql.append("SELECT ")
           .append(   "p.DateTrx, ")
           .append(   "p.C_Payment_ID, ")
           .append(	  "p.DocumentNo, ")
           .append(   "p.C_Currency_ID, ")
           .append(   "c.ISO_Code, ")
           .append(   "p.PayAmt, ")
           .append(   "currencyConvert(p.PayAmt,p.C_Currency_ID,ba.C_Currency_ID,?,null,p.AD_Client_ID,p.AD_Org_ID) AS ConvertedAmt, ")
           .append(   "bp.Name AS BPartnerName ")
           
           .append("FROM C_BankAccount ba ")
           .append("INNER JOIN C_Payment_v p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID) ")
           .append("INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) ")
           .append("INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) ")
        		   
           .append("WHERE p.Processed='Y' ")
           .append(  "AND p.IsReconciled='N' ")
           .append(  "AND p.DocStatus IN ('CO','CL','RE') ")
           .append(  "AND p.PayAmt<>0 ")
           .append(  "AND p.C_BankAccount_ID=? ")
           .append(  "AND NOT EXISTS ")
           .append(      "(SELECT * FROM C_BankStatementLine l ")
           // Voided Bank Statements have 0 StmtAmt
           .append(       "WHERE p.C_Payment_ID=l.C_Payment_ID AND l.StmtAmt <> 0)");

        // Get StatementDate

        Timestamp ts = ( Timestamp )p_mTab.getValue( "StatementDate" );

        if( ts == null ) {
            ts = new Timestamp( System.currentTimeMillis());
        }
    	PreparedStatement pstmt = null;
    	ResultSet rs 			= null;

        try {
            pstmt = DB.prepareStatement( sql.toString());
            pstmt.setTimestamp( 1,ts );
            pstmt.setInt( 2,C_BankAccount_ID );
            rs = pstmt.executeQuery();

            while( rs.next()) {
                Payment payment = new Payment();
                
                payment.selected = false;
                payment.paymentID = rs.getInt("C_Payment_ID");
                payment.dateTrx = rs.getTimestamp("DateTrx");
                payment.documentNo = rs.getString("DocumentNo");
                payment.currencyID = rs.getInt("C_Currency_ID");
                payment.currencyISO = rs.getString("ISO_Code");
                payment.payAmt = rs.getBigDecimal("PayAmt");
                payment.convertedAmt = rs.getBigDecimal("ConvertedAmt");
                payment.bPartnerName = rs.getString("BPartnerName");                
                
                data.add(payment);
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
    	} finally {
    		try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
    		}	catch (Exception e) {}
    	}

    	loadTable(data);
    }    // loadBankAccount

    /**
     * Descripción de Método
     *
     */


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected void save() throws CreateFromSaveException {
        log.config( "" );

        // fixed values

        int C_BankStatement_ID = (( Integer )p_mTab.getValue( "C_BankStatement_ID" )).intValue();
        MBankStatement bs = new MBankStatement( Env.getCtx(),C_BankStatement_ID, getTrxName());

        log.config( bs.toString());

        // Lines
        for (SourceEntity sourceEntity : getSelectedSourceEntities()) {
        	Payment payment = (Payment)sourceEntity;	
            Timestamp trxDate = payment.dateTrx;
            int C_Payment_ID = payment.paymentID;
            int C_Currency_ID = payment.currencyID;
            BigDecimal TrxAmt  = payment.payAmt;
            BigDecimal StmtAmt = payment.convertedAmt;

            //

            log.fine( "Line Date=" + trxDate + ", Payment=" + C_Payment_ID + ", Currency=" + C_Currency_ID + ", Amt=" + TrxAmt );

            //
            MPayment pay = new MPayment( Env.getCtx(),C_Payment_ID, getTrxName());
            MBankStatementLine bsl = new MBankStatementLine( bs );

            bsl.setStatementLineDate( trxDate );
            bsl.setPayment(pay);
            
            if( !bsl.save()) {
                throw new CreateFromSaveException(
             		   "@StatementLineSaveError@ (@C_Paymenty_ID@ # " + payment.documentNo + "):<br>" + 
             		   CLogger.retrieveErrorAsString()
             	);

            }
        }        // for all rows
    }    // save
    
	@Override
	protected CreateFromTableModel createTableModelInstance() {
		return new PaymentTableModel();
	}

	/**
     * Entidad Oríden: Pagos
     */
    protected class Payment extends SourceEntity {
    	/** ID del pago */
    	protected int paymentID = 0;
    	/** Nro de Documento del pago */
    	protected String documentNo = null;
    	/** Fecha de transacción */
    	protected Timestamp dateTrx = null;
        /** Nombre o descripción de la EC asociada al pago */
    	protected String bPartnerName = null;
    	/** ID de la moneda en la que se encuentra expresada el monto
    	 * del pago */
    	protected int currencyID = 0;
    	/** Código ISO de la moneda en la que se encuentra expresada el monto
    	 * del pago */
    	protected String currencyISO = null;
        /** Importe del pago expresado en la moneda currencyID */
    	protected BigDecimal payAmt = BigDecimal.ZERO;
        /** Importe del pago convertido a la moneda de la cuenta bancaria a la que pertence*/
    	protected BigDecimal convertedAmt = BigDecimal.ZERO;
    
    	@Override
    	public ArrayList<Object> toList() {
			ArrayList<Object> result = new ArrayList<Object>();
			CreateFromTableModel model = (CreateFromTableModel)window.getDataTable().getModel();
			for (int i=0; i < model.getColumnCount(); i++ ) {
				Object value = null;
				switch (i) {
					case DocumentLineTableModel.COL_IDX_SELECTION:
						value = selected; break;
					case PaymentTableModel.COL_IDX_DATETRX:
						value = dateTrx; break;
					case PaymentTableModel.COL_IDX_DOCUMENTNO:
						value = new KeyNamePair(paymentID, documentNo) ; break;
					case PaymentTableModel.COL_IDX_BPARTNER:
						value = bPartnerName; break;
					case PaymentTableModel.COL_IDX_CURRENCY:
						value = new KeyNamePair(currencyID, currencyISO) ; break;
					case PaymentTableModel.COL_IDX_AMT:
						value = payAmt; break;
					case PaymentTableModel.COL_IDX_CONVAMT:
						value = convertedAmt; break;
					default:
						value = null; break;
				}
				result.add(value);
			}
			return result;
		}
    }
    
    /**
     * Modelo de tabla para presentación de los pagos de una cuenta bancaria.
     */
    protected class PaymentTableModel extends DocumentLineTableModel {

		// Constantes de índices de las columnas en la grilla.
    	public static final int COL_IDX_DATETRX    = 1;
    	public static final int COL_IDX_DOCUMENTNO = 2;
    	public static final int COL_IDX_BPARTNER   = 3;
    	public static final int COL_IDX_CURRENCY   = 4;
    	public static final int COL_IDX_AMT        = 5;
    	public static final int COL_IDX_CONVAMT    = 6;
    	    	
		@Override
		protected void setColumnClasses() {
	        setColumnClass(COL_IDX_DATETRX, Timestamp.class);
	        setColumnClass(COL_IDX_DOCUMENTNO, String.class);
	        setColumnClass(COL_IDX_BPARTNER, String.class);
	        setColumnClass(COL_IDX_CURRENCY, String.class);
	        setColumnClass(COL_IDX_AMT, BigDecimal.class);
	        setColumnClass(COL_IDX_CONVAMT, BigDecimal.class);
		}

		@Override
		protected void setColumnNames() {
	        setColumnName(COL_IDX_DATETRX, Msg.translate( Env.getCtx(),"Date" ));
	        setColumnName(COL_IDX_DOCUMENTNO, Msg.getElement( Env.getCtx(),"C_Payment_ID" ));
	        setColumnName(COL_IDX_BPARTNER, Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
	        setColumnName(COL_IDX_CURRENCY, Msg.translate( Env.getCtx(),"C_Currency_ID" ));
	        setColumnName(COL_IDX_AMT, Msg.translate( Env.getCtx(),"Amount" ));
	        setColumnName(COL_IDX_CONVAMT, Msg.translate( Env.getCtx(),"ConvertedAmount" ));
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			Payment payment = getPayment(rowIndex);
			Object value = null;
			switch (colIndex) {
				case COL_IDX_DATETRX:
					value = payment.dateTrx; break;
				case COL_IDX_DOCUMENTNO:
					value = payment.documentNo; break;
				case COL_IDX_BPARTNER:
					value = payment.bPartnerName; break;
				case COL_IDX_CURRENCY:
					value = payment.currencyISO; break;
				case COL_IDX_AMT:
					value = payment.payAmt; break;
				case COL_IDX_CONVAMT:
					value = payment.convertedAmt; break;
				default:
					value = super.getValueAt(rowIndex, colIndex); break;
			}
			return value;
		}

		/**
		 * Devuelve el pago ubicado en una determinada fila
		 * @param rowIndex Índice de la fila
		 * @return {@link Payment}
		 */
		public Payment getPayment(int rowIndex) {
			return (Payment)getSourceEntity(rowIndex);
		}
		
		@Override
		protected void updateColumns() {
		}
    }

	@Override
	protected void customizarPanel() {
		
		orderLabel.setVisible(false);
		orderField.setVisible(false);
		locatorLabel.setVisible(false);
		locatorField.setVisible(false);
		
		window.getParameterPanel().appendChild(bankAccountLabel.rightAlign());
		window.getParameterPanel().appendChild(bankAccountField.getComponent());
	}
	
	@Override
	protected boolean lazyEvaluation() {
		return true;
	}
	

	public void showWindow()
	{
		window.setVisible(true);
	}
	
	public void closeWindow()
	{
		window.dispose();
	}
	
}
