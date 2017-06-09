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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import org.compiere.swing.CComboBox;
import org.openXpertya.apps.ADialog;
import org.openXpertya.grid.CreateFromModel.CreateFromSaveException;
import org.openXpertya.grid.CreateFromModel.Payment;
import org.openXpertya.grid.CreateFromModel.SourceEntity;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VString;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MTab;
import org.openXpertya.model.PO;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VCreateFromStatement extends VCreateFrom implements VetoableChangeListener {

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

    protected VCreateFromStatement( MTab mTab ) {
        super( mTab );
        log.info( "" );
    }    // VCreateFromStatement

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
            ADialog.error( 0,this,"SaveErrorRowNotFound" );

            return false;
        }

        setTitle( Msg.translate( Env.getCtx(),"C_BankStatement_ID" ) + " .. " + Msg.translate( Env.getCtx(),"CreateFrom" ));
        parameterStdPanel.setVisible( false );

        int     AD_Column_ID = 4917;    // C_BankStatement.C_BankAccount_ID
        MLookup lookup       = MLookupFactory.get( Env.getCtx(),p_WindowNo,0,AD_Column_ID,DisplayType.TableDir );

        bankAccountField = new VLookup( "C_BankAccount_ID",true,true,true,lookup );
        bankAccountField.addVetoableChangeListener( this );

        initSource();
        
        // Set Default

        int C_BankAccount_ID = Env.getContextAsInt( Env.getCtx(),p_WindowNo,"C_BankAccount_ID" );

        bankAccountField.setValue( new Integer( C_BankAccount_ID ));
        
        ((CreateFromStatementModel)getHelper()).setBankAccount(C_BankAccount_ID);
        
        // initial Loading
        
        loadData();

        return true;
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    protected void initBPDetails( int C_BPartner_ID ) {}    // initDetails

    
    protected void initSource(){
    	jbSourceTable = new CComboBox();
    	jbSourceTable.addItem(new CreateFromStatementAll());
    	jbSourceTable.addItem(new CreateFromStatementGeneralValues());
		jbSourceTable.addItem(new CreateFromStatementBoletasDeposito());
		jbSourceTable.addItem(new CreateFromStatementSettlements());
		jbSourceTable.setMandatory(true);
		jbSourceTable.setSelectedIndex(0);
		((CreateFromStatementModel)getHelper()).setCreateFromData((CreateFromStatementData)jbSourceTable.getValue());
		jbSourceTable.addActionListener(this);
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
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        log.config( e.getPropertyName() + "=" + e.getNewValue());

        // BankAccount

        if( e.getPropertyName() == "C_BankAccount_ID" ) {
            int C_BankAccount_ID = (( Integer )e.getNewValue()).intValue();
            ((CreateFromStatementModel)getHelper()).setBankAccount(C_BankAccount_ID);
            loadData();
        }

        tableChanged( null );
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     */

    protected void info() {
        DecimalFormat format = DisplayType.getNumberFormat( DisplayType.Amount );
        BigDecimal total = new BigDecimal( 0.0 );
        int        count = 0;

        for(SourceEntity sourceEntity : getSelectedSourceEntities()) {
            total = total.add(((Payment)sourceEntity).payAmt);
            count++;
        }

        statusBar.setStatusLine( String.valueOf( count ) + " - " + Msg.getMsg( Env.getCtx(),"Sum" ) + "  " + format.format( total ));
    }    // infoStatement

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected void save() throws CreateFromSaveException {
    	int C_BankStatement_ID = (( Integer )p_mTab.getValue( "C_BankStatement_ID" )).intValue();
    	((CreateFromStatementModel)getHelper()).save(C_BankStatement_ID, getTrxName(), getSelectedSourceEntities(), this);
    }    // save
    
	@Override
	protected CreateFromTableModel createTableModelInstance() {
		return new PaymentTableModel();
	}

	public void actionPerformed( ActionEvent e ) {
		if (e.getSource().equals(jbSourceTable)){
			((CreateFromStatementModel)getHelper()).setCreateFromData((CreateFromStatementData)jbSourceTable.getSelectedItem());
			updateVisibleComponents();
			loadData();
		}
		else{
			super.actionPerformed(e);
		}
	}

	/**
	 * Actualiza la visibilidad de los componentes en base al origen de datos 
	 */
	protected void updateVisibleComponents(){
		grouped.setVisible(((CreateFromStatementModel)getHelper()).isAllowGrouped());
		nroLoteLabel.setVisible(((CreateFromStatementModel)getHelper()).isAllowCouponBatchNoFilter());
		nroLote.setVisible(((CreateFromStatementModel)getHelper()).isAllowCouponBatchNoFilter());
	}
	
    /**
     * Modelo de tabla para presentación de los pagos de una cuenta bancaria.
     */
    protected class PaymentTableModel extends CreateFromTableModel {

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
		// TODO Auto-generated method stub
	}
	
	@Override
	protected boolean lazyEvaluation() {
		return true;
	}

	@Override
	public void customMethod(PO ol, PO iol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean addSecurityValidation() {
		return true;
	}

	@Override
	protected void filtrar(VString numeroLote, Boolean agrupacioncupones) {
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
		((CreateFromStatementModel)getHelper()).setGrouped(grouped.isSelected());
		loadTable(((CreateFromStatementModel)getHelper()).getData());
	}
	
}    // VCreateFromStatement



/*
 *  @(#)VCreateFromStatement.java   02.07.07
 * 
 *  Fin del fichero VCreateFromStatement.java
 *  
 *  Versión 2.2
 *
 */
