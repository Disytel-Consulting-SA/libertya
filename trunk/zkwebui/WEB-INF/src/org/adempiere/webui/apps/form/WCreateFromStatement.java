package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.window.FDialog;
import org.openXpertya.grid.CreateFromModel;
import org.openXpertya.grid.CreateFromModel.CreateFromSaveException;
import org.openXpertya.grid.CreateFromModel.ListedSourceEntityInterface;
import org.openXpertya.grid.CreateFromModel.Payment;
import org.openXpertya.grid.CreateFromStatementAll;
import org.openXpertya.grid.CreateFromStatementBoletasDeposito;
import org.openXpertya.grid.CreateFromStatementData;
import org.openXpertya.grid.CreateFromStatementGeneralValues;
import org.openXpertya.grid.CreateFromStatementModel;
import org.openXpertya.grid.CreateFromStatementSettlements;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MTab;
import org.openXpertya.model.PO;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

public class WCreateFromStatement  extends WCreateFrom {
	
	@Override
	protected CreateFromModel createHelper(){
    	return new CreateFromStatementModel();
    }
	
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

        initSource();
        
        bankAccountField = new WSearchEditor( "C_BankAccount_ID",true,true,true,lookup );
        bankAccountField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent e) {
		        log.config( e.getPropertyName() + "=" + e.getNewValue());
		        // BankAccount
		        if( e.getPropertyName() == "C_BankAccount_ID" ) {
		            int C_BankAccount_ID = (( Integer )e.getNewValue()).intValue();
		            ((CreateFromStatementModel)getHelper()).setBankAccount(C_BankAccount_ID);
		            loadData();
		        }
		        window.tableChanged( null );
			}
		});

        // Set Default

        int C_BankAccount_ID = Env.getContextAsInt( Env.getCtx(),p_WindowNo,"C_BankAccount_ID" );

        bankAccountField.setValue( new Integer( C_BankAccount_ID ));

        ((CreateFromStatementModel)getHelper()).setBankAccount(C_BankAccount_ID);
        
        // initial Loading

        loadData();

        return true;
    }    // dynInit

    
    protected void initSource(){
    	jbSourceTable = new Combobox();
    	jbSourceTable.appendItem(new CreateFromStatementAll());
    	jbSourceTable.appendItem(new CreateFromStatementGeneralValues());
		jbSourceTable.appendItem(new CreateFromStatementBoletasDeposito());
		jbSourceTable.appendItem(new CreateFromStatementSettlements());
		jbSourceTable.setSelectedIndex(0);
		((CreateFromStatementModel) getHelper())
				.setCreateFromData((CreateFromStatementData) jbSourceTable.getSelectedItem().getValue());
		jbSourceTable.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				((CreateFromStatementModel) getHelper())
						.setCreateFromData((CreateFromStatementData) jbSourceTable.getSelectedItem().getValue());
				updateVisibleComponents();
				loadData();
			}
		});
		addOtherSources();
    }
    
    /**
     * Este método permite a las subclases incorporar nuevas fuentes de datos
     */
    protected void addOtherSources(){}
    
    
    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    protected void initBPDetails( int C_BPartner_ID ) {}    // initDetails

    /**
	 * Actualiza la visibilidad de los componentes en base al origen de datos 
	 */
	protected void updateVisibleComponents(){
		grouped.setVisible(((CreateFromStatementModel)getHelper()).isAllowGrouped());
		nroLote.getLabel().setVisible(((CreateFromStatementModel)getHelper()).isAllowCouponBatchNoFilter());
		nroLote.setVisible(((CreateFromStatementModel)getHelper()).isAllowCouponBatchNoFilter());
	}
    
    /**
     * Guardar la selección
     */

    protected void save() throws CreateFromSaveException {
    	int C_BankStatement_ID = (( Integer )p_mTab.getValue( "C_BankStatement_ID" )).intValue();
    	((CreateFromStatementModel)getHelper()).save(C_BankStatement_ID, getTrxName(), getSelectedSourceEntities(), this);
    }    // save

	@Override
	protected CreateFromTableModel createTableModelInstance() {
		return new PaymentTableModel();
	}
    
	/**
     * Entidad Oríden: Pagos
     */
    protected class PaymentListImpl extends Payment implements ListedSourceEntityInterface  {
    	
    	@Override
    	public ArrayList<Object> toList() {
			ArrayList<Object> result = new ArrayList<Object>();
			CreateFromTableModel model = getDataTableModel();
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
					case PaymentTableModel.COL_IDX_TENDERTYPE:
						value = new ValueNamePair(tenderType, tenderTypeDescription); break;
					case PaymentTableModel.COL_IDX_CONVAMT:
						value = convertedAmt; break;
					case PaymentTableModel.COL_IDX_DUEDATE:
						value = dueDate; break;
					case PaymentTableModel.COL_IDX_BOLETADEPOSITO:
						value = new KeyNamePair(boletaDepositoID, boletaDepositoDocumentNo); break;
					case PaymentTableModel.COL_IDX_CREDITCARDSETTLEMENT:
						value = new KeyNamePair(creditCardSettlementID, creditCardSettlementDocumentNo); break;
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
    public class PaymentTableModel extends DocumentLineTableModel {

    	// Constantes de índices de las columnas en la grilla.
    	public static final int COL_IDX_DATETRX    				= 1;
    	public static final int COL_IDX_TENDERTYPE 				= 2;
    	public static final int COL_IDX_DOCUMENTNO 				= 3;
    	public static final int COL_IDX_BPARTNER   				= 4;
    	public static final int COL_IDX_CURRENCY   				= 5;
    	public static final int COL_IDX_AMT        				= 6;
    	public static final int COL_IDX_CONVAMT    				= 7;
    	public static final int COL_IDX_DUEDATE    				= 8;
    	public static final int COL_IDX_BOLETADEPOSITO 			= 9;
    	public static final int COL_IDX_CREDITCARDSETTLEMENT	= 10;
    	
    	@Override
    	public int getColumnCount() {
			return getColumnNames().size();
		}
    	
		@Override
		protected void setColumnClasses() {
			setColumnClass(COL_IDX_DATETRX, Timestamp.class);
	        setColumnClass(COL_IDX_TENDERTYPE, String.class);
	        setColumnClass(COL_IDX_DOCUMENTNO, String.class);
	        setColumnClass(COL_IDX_BPARTNER, String.class);
	        setColumnClass(COL_IDX_CURRENCY, String.class);
	        setColumnClass(COL_IDX_AMT, BigDecimal.class);
	        setColumnClass(COL_IDX_CONVAMT, BigDecimal.class);
	        setColumnClass(COL_IDX_DUEDATE, Timestamp.class);
	        setColumnClass(COL_IDX_BOLETADEPOSITO, String.class);
	        setColumnClass(COL_IDX_CREDITCARDSETTLEMENT, String.class);
		}

		@Override
		protected void setColumnNames() {
			setColumnName(COL_IDX_DATETRX, Msg.translate( Env.getCtx(),"Date" ));
	        setColumnName(COL_IDX_TENDERTYPE, Msg.getElement( Env.getCtx(),"TenderType" ));
	        setColumnName(COL_IDX_DOCUMENTNO, Msg.getElement( Env.getCtx(),"C_Payment_ID" ));
	        setColumnName(COL_IDX_BPARTNER, Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
	        setColumnName(COL_IDX_CURRENCY, Msg.translate( Env.getCtx(),"C_Currency_ID" ));
	        setColumnName(COL_IDX_AMT, Msg.translate( Env.getCtx(),"Amount" ));
	        setColumnName(COL_IDX_CONVAMT, Msg.translate( Env.getCtx(),"ConvertedAmount" ));
	        setColumnName(COL_IDX_DUEDATE, Msg.translate(Env.getCtx(), "DueDate"));
	        setColumnName(COL_IDX_BOLETADEPOSITO, Msg.translate(Env.getCtx(), "M_BoletaDeposito_ID"));
	        setColumnName(COL_IDX_CREDITCARDSETTLEMENT, Msg.translate(Env.getCtx(), "C_CreditCardSettlement_ID"));
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			Payment payment = getPayment(rowIndex);
			Object value = null;
			switch (colIndex) {
				case COL_IDX_DATETRX:
					value = payment.dateTrx; break;
				case COL_IDX_TENDERTYPE:
					value = payment.tenderTypeDescription; break;
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
				case COL_IDX_DUEDATE:
					value=payment.dueDate; break;
				case COL_IDX_BOLETADEPOSITO:
					value=payment.boletaDepositoDocumentNo; break;
				case COL_IDX_CREDITCARDSETTLEMENT:
					value=payment.creditCardSettlementDocumentNo; break;
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
		Grid parameterStdLayout = GridFactory.newGridLayout();		
		Rows rows = (Rows) parameterStdLayout.newRows();
		Row row = rows.newRow();
		row.appendChild(bankAccountLabel.rightAlign());
		row.appendChild(bankAccountField.getComponent());
		row.appendChild(jlTipo.rightAlign());
		row.appendChild(jbSourceTable);
		row.appendChild(nroLote.getLabel().rightAlign());
		row.appendChild(nroLote.getComponent());
		row.appendChild(grouped);
		window.getParameterPanel().appendChild(parameterStdLayout);
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

	@Override
	public void customMethod(PO ol, PO iol) {
		// TODO Auto-generated method stub
		
	}
	
	protected void filtrar() {
		loadData();
	}
    
	protected void loadData() {
		// Get StatementDate
		Timestamp ts = (Timestamp) p_mTab.getValue("StatementDate");
		if (ts == null) {
			ts = Env.getDate();
		}
		int C_BankAccount_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo,
				"C_BankAccount_ID");
		((CreateFromStatementModel)getHelper()).setBankAccount(C_BankAccount_ID);
		((CreateFromStatementModel)getHelper()).setStatementDate(ts);
		((CreateFromStatementModel)getHelper()).setCouponBatchNo((String)nroLote.getValue());
		((CreateFromStatementModel)getHelper()).setGrouped(grouped.isChecked());
		
		List<Payment> pays = ((CreateFromStatementModel)getHelper()).getData();
		List<PaymentListImpl> paysWrapped = new ArrayList<WCreateFromStatement.PaymentListImpl>();
		for (Payment payment : pays) {
			PaymentListImpl pli = new PaymentListImpl();
			payment.copyValues(pli);
			paysWrapped.add(pli);
		}
		
		loadTable(paysWrapped);
	}
}
