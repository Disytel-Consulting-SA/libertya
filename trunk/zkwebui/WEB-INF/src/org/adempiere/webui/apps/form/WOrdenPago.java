/**
 * VOrdenPago.java
 */

package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.SimpleTreeModel;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Tabpanels;
import org.adempiere.webui.component.Tabs;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.editor.IZoomableEditor;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.window.FDialog;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.apps.form.VModelHelper;
import org.openXpertya.apps.form.VOrdenPago;
import org.openXpertya.apps.form.VOrdenPagoModel;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPago;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoCheque;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoCredito;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoEfectivo;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoTransferencia;
import org.openXpertya.apps.form.VOrdenPagoModel.ResultItemFactura;
import org.openXpertya.model.Lookup;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MSequence;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.model.X_C_BankAccountDoc;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zkmax.zul.Tablechildren;
import org.zkoss.zkmax.zul.Tablelayout;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Separator;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.impl.XulElement;


/**
 *
 * @author  usuario
 */
public class WOrdenPago extends ADForm implements ValueChangeListener, TableModelListener, EventListener /*implements /*FormPanel,ActionListener,TableModelListener,VetoableChangeListener,ChangeListener,TreeModelListener,MouseListener,CellEditorListener,ASyncProcess*/ {

    /** Creates new form WOrdenPago */
    public WOrdenPago() {

    }
    
    /**
     * Incorpora el menu contextual (Zoom, Refrescar, Preferencias) a un elemento, como por ejemplo WSearchEditr, WTableDirEditor, etc. 
     */
    protected void addPopupMenu(Object aComponent, boolean zoom, boolean requery, boolean preferences) {
        WEditorPopupMenu aPopupMenu = new WEditorPopupMenu(true, true, false);
        ((XulElement)((WEditor)aComponent).getComponent()).setContext(aPopupMenu);
        aPopupMenu.addMenuListener((ContextMenuListener)aComponent);
        appendChild(aPopupMenu);
	    Label label = ((WEditor)aComponent).getLabel();
	    if (aPopupMenu.isZoomEnabled() && aComponent instanceof IZoomableEditor)
	    {
	    	label.setStyle("cursor: pointer; text-decoration: underline;");
	          label.addEventListener(Events.ON_CLICK, new ZoomListener((IZoomableEditor) aComponent));
	    }
	    label.setContext(aPopupMenu.getId());  
    }
    
    /**
     *  Migrated from AD_Tabpanel 
     */
	class ZoomListener implements EventListener {

		private IZoomableEditor searchEditor;

		ZoomListener(IZoomableEditor editor) {
			searchEditor = editor;
		}

		public void onEvent(Event event) throws Exception {
			if (Events.ON_CLICK.equals(event.getName())) {
				searchEditor.actionZoom();
			}
		}
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" C�digo Generado  ">//GEN-BEGIN:initComponents
    
    protected void initComponents() {
    	
        buttonGroup1 = new Radiogroup();
        buttonGroup2 = new Radiogroup();
        
        jPanel1 = GridFactory.newGridLayout();

        MLookup lookupClient = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_Client_ID", "AD_Client", DisplayType.TableDir);
        cboClient = new WTableDirEditor("AD_Client_ID", false, false, true, lookupClient);
        cboClient.setValue(Env.getAD_Client_ID(Env.getCtx()));
        cboClient.setReadWrite(false);
        addPopupMenu(cboClient, true, true, false);
        
        MLookup lookupPartner = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "C_BPartner_ID", "C_BPartner", DisplayType.Search);
		BPartnerSel = new WSearchEditor ("C_BPartner_ID", true, false, true, lookupPartner);
		addPopupMenu(BPartnerSel, true, true, false);
//		BPartnerSel.getLabel().setMandatory(true);
		
        fldDocumentNo = new WStringEditor(); 

        MLookup lookupOrg = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_Org_ID", "AD_Org", DisplayType.TableDir);
		cboOrg = new WTableDirEditor("AD_Org_ID", false, false, true, lookupOrg);
		cboOrg.setValue(Env.getAD_Org_ID(Env.getCtx()));
		addPopupMenu(cboOrg, true, true, false);
		
        radPayTypeStd = new Radio();
        radPayTypeAdv = new Radio();
        radPayTypeStd.setValue("PAGO NORMAL");
        radPayTypeStd.setSelected(true);
        
        txtDescription = new WStringEditor();
		MLookupInfo infoDocType = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_DocType_ID", "C_DocType", DisplayType.TableDir, m_model.getDocumentTypeSqlValidation());
		MLookup lookupDocType = new MLookup(infoDocType, 0);
		cboDocumentType = new WTableDirEditor("C_DocType_ID", false, false, true, lookupDocType);
		addPopupMenu(cboDocumentType, true, true, false);
		
		tblFacturas = new Grid();
		tblFacturas.setHeight("350px");
		listModel = new FacturasModel(VModelHelper.HideColumnsTableModelFactory(m_model.m_facturasTableModel), m_WindowNo);
		renderer = new GridRenderer(this);
		tblFacturas.setModel(listModel);
		tblFacturas.setRowRenderer(renderer);
		
        txtTotalPagar1 = new WStringEditor();
        
		rInvoiceAll = new Radio();
		rInvoiceDate = new Radio();

        invoiceDatePick = new WDateEditor();

        cmdEliminar = new Button();
        cmdEditar = new Button();

        cmdGrabar = new Button();

        
		MLookupInfo infoLibroCaja = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_Cash_ID", "C_Cash", DisplayType.Search, m_model.getEfectivoLibroCajaSqlValidation());
		MLookup lookupLibroCaja = new MLookup(infoLibroCaja, 0);
		efectivoLibroCaja = new WSearchEditor("C_Cash_ID", false, false, true, lookupLibroCaja);
		addPopupMenu(efectivoLibroCaja, true, true, false);
        txtEfectivoImporte = new WStringEditor();

        MLookupInfo infoTransf = VComponentsFactory.MLookupInfoFactory(Env.getCtx(),m_WindowNo, 0, "C_BankAccount_ID", "C_BankAccount", DisplayType.Search, m_model.getTransfCtaBancariaSqlValidation());
		MLookup lookupTransf = new MLookup(infoTransf, 0);
		transfCtaBancaria = new WSearchEditor( "C_BankAccount_ID",false,false,true,lookupTransf );
		addPopupMenu(transfCtaBancaria, true, true, false);
		
        txtTransfNroTransf = new WStringEditor();
        txtTransfImporte = new WStringEditor();
        transFecha = new WDateEditor(); // VComponentsFactory.VDateFactory();
        chequeChequera = createChequeChequeraLookup(); 
        txtChequeImporte = new WStringEditor();
        chequeFechaEmision = new WDateEditor(); // VComponentsFactory.VDateFactory();
        chequeFechaPago = new WDateEditor(); // VComponentsFactory.VDateFactory();
        txtChequeALaOrden = new WStringEditor();
        txtChequeNroCheque = new WStringEditor();
        txtChequeBanco = new WStringEditor();
        cboChequeBancoID = createChequeBancoIDLookup();
        txtChequeCUITLibrador = new WStringEditor();
        txtChequeDescripcion = new WStringEditor();     
        
        MLookup lookupCampaign = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "C_Campaign_ID", "C_Campaign", DisplayType.TableDir);
        cboCampaign = new WTableDirEditor("C_Campaign_ID", false, false, true, lookupCampaign);
        addPopupMenu(cboCampaign, true, true, false);
        
        MLookup lookupProject = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "C_Project_ID", "C_Project", DisplayType.TableDir);
        cboProject = new WTableDirEditor("C_Project_ID", false, false, true, lookupProject);
        addPopupMenu(cboProject, true, true, false);
        
        MLookupInfo infoCurrency = VComponentsFactory.MLookupInfoFactory(Env.getCtx(),m_WindowNo, 0, "C_Currency_ID", "C_Currency", DisplayType.TableDir, m_model.getCurrencySqlValidation());
        MLookup lookupCurrency = new MLookup(infoCurrency, 0);
        cboCurrency = new WTableDirEditor("C_Currency_ID", false, false, true, lookupCurrency);
        addPopupMenu(cboCurrency, true, true, false);
        
        cmdProcess = new Button();        
        cmdCancel = new Button();

        MLookupInfo infoCreditInvoice = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_Invoice_ID", "C_Invoice", DisplayType.Search, m_model.getCreditSqlValidation());
        MLookup lookupCreditInvoice = new MLookup(infoCreditInvoice, 0);
		creditInvoice = new WSearchEditor("C_Invoice_ID", false, false, true, lookupCreditInvoice);
		addPopupMenu(creditInvoice, true, true, false);
		
        txtCreditAvailable = new WStringEditor();
        txtCreditAvailable.setReadWrite(false);
        txtCreditImporte = new WStringEditor();
        
        checkPayAll = new Checkbox();
        checkPayAll.setText("Pagar Todo");
        checkPayAll.addActionListener(new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				updatePayAllInvoices(false);
				
			}
		});

        BPartnerSel.getLabel().setText("Entidad Comercial");

        chequeFechaEmision.setMandatory(true);
        chequeFechaPago.setMandatory(true);
        buttonGroup1.appendChild(radPayTypeStd);
        radPayTypeStd.addEventListener("onCheck", new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				onTipoPagoChange(false);
			}
		});        
        buttonGroup1.appendChild(radPayTypeAdv);
        radPayTypeAdv.setValue("PAGO ADELANTADO");
        radPayTypeAdv.addEventListener("onCheck", new EventListener() {
            public void onEvent(Event arg0) throws Exception {
                onTipoPagoChange(false);
            }
        });

        txtTotalPagar1.getLabel().setValue("TOTAL A PAGAR");

        buttonGroup2.appendChild(rInvoiceAll);      
        rInvoiceAll.setSelected(true);
        rInvoiceAll.setValue("TODAS");
        rInvoiceAll.addEventListener("onCheck", new EventListener() {
            public void onEvent(Event arg0) throws Exception {
                onFechaChange(false);
            }
        });

        
        buttonGroup2.appendChild(rInvoiceDate);
        rInvoiceDate.setValue("VENCIDAS A FECHA:");
        rInvoiceDate.addEventListener("onCheck", new EventListener() {
            public void onEvent(Event arg0) throws Exception {
                onFechaChange(false);
            }
        });

        
        
        cmdEliminar.setLabel("ELIMINAR");
        cmdEliminar.addEventListener("onClick", new EventListener() {
            public void onEvent(Event evt) throws Exception {
            	cmdEliminarActionPerformed(evt);
            }
        });

        cmdEditar.setLabel("EDITAR");
        cmdEditar.addEventListener("onClick", new EventListener() {
            public void onEvent(Event evt) throws Exception {
            	cmdEditarActionPerformed(evt);
            }
        });

        txtSaldo = new WStringEditor();
        txtSaldo.setReadWrite(false); 
        txtDifCambio = new WStringEditor();
        txtDifCambio.setReadWrite(false);
        txtSaldo.getLabel().setText("SALDO");
        txtMedioPago2 = new WStringEditor();
        txtMedioPago2.setReadWrite(false);
        txtRetenciones2 = new WStringEditor();
        txtRetenciones2.setReadWrite(false);
        txtTotalPagar2 = new WStringEditor();
        txtTotalPagar2.setReadWrite(false);
        txtTotalPagar2.getLabel().setText("TOTAL A PAGAR");
        txtRetenciones2.getLabel().setText("RETENCIONES");
        txtDifCambio.getLabel().setText("Diferecia de Cambio");
        txtMedioPago2.getLabel().setText("MEDIO DE PAGO");

        cmdGrabar.setLabel("GRABAR");
        cmdGrabar.addEventListener("onClick", new EventListener() {
            public void onEvent(Event evt) throws Exception {
            	cmdSavePMActionPerformed(evt);
            }
        });

        cmdProcess.setLabel("SIGUIENTE/PROCESAR");
        cmdProcess.addActionListener(new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				 cmdProcessActionPerformed(arg0);
			}
		});

        cmdCancel.setLabel("CANCELAR");
        cmdCancel.addEventListener("onClick", new EventListener() {
            public void onEvent(Event evt) throws Exception {
            	cmdCancelActionPerformed(evt);
            }
        });
        
        // Pagos anticipados como medios de pago
        createPagoAdelantadoTab();  // llamado simplemente para evitar posteriores NPE
        //Agrego un listener para verificar si es null o no, y deshabilitar el boton Siguiente en caso de que sea null    
        BPartnerSel.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent evt) {
				cmdBPartnerSelActionPerformed(evt);
			}
		}); 
        
        // Ex-zkInit()
    	Borderlayout layout = new Borderlayout();
    	layout.setHeight("100%");
    	layout.setWidth("100%");
    	this.appendChild(layout);
        
        North north = new North();
    	layout.appendChild(north);
    	north.appendChild(jPanel1);
    	
    	createPaymentSelectionTopFields();
    	
    	Center center = new Center();
    	layout.appendChild(center);
    	center.appendChild(tabbox);
    	
    	tabbox.setHeight("100%");
    	tabbox.appendChild(tabs);
    	tabbox.appendChild(tabpanels);
    	tabbox.addEventListener("onSelect", this);
    	
    	createPaymentSelectionTab();
    	createPaymentTab();
    	
    	tabPaymentSelection = new Tab(Msg.getMsg(Env.getCtx(), "PaymentSelection"));
    	tabpanels.appendChild(jTabbedPane1);
    	tabs.appendChild(tabPaymentSelection);
    	
    	tabPaymentRule = new Tab(Msg.getMsg(Env.getCtx(), "Payment"));
    	tabpanels.appendChild(jTabbedPane2);
    	tabs.appendChild(tabPaymentRule);
    	
    	South south = new South();
    	layout.appendChild(south);
    	Div divButton = new Div();
    	divButton.setAlign("end");
    	divButton.appendChild(cmdProcess);
    	south.appendChild(divButton);
        
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Agrega los campos superiores en la pestaña de facturas a pagar
     */
    protected void createPaymentSelectionTopFields() {
    	Rows rows = jPanel1.newRows();
    	Row row = rows.newRow();
    	row.appendChild(cboClient.getLabel().rightAlign());
    	row.appendChild(cboClient.getComponent());
    	
    	row.appendChild(new Space());
    	row.appendChild(cboOrg.getLabel().rightAlign());
    	row.appendChild(cboOrg.getComponent());
    	row.appendChild(new Space());
    	
    	row = rows.newRow();
    	row.appendChild(BPartnerSel.getLabel().rightAlign());
    	row.appendChild(BPartnerSel.getComponent());
    	row.appendChild(new Space());
    	row.appendChild(fldDocumentNo.getLabel().rightAlign());
    	row.appendChild(fldDocumentNo.getComponent());
    	row.appendChild(new Space());
    	
    	row = rows.newRow();
    	row.appendChild(txtDescription.getLabel().rightAlign());
    	row.appendChild(txtDescription.getComponent());
    	txtDescription.getComponent().setWidth("100%");
    	row.appendChild(new Space());

    	row.appendChild(cboDocumentType.getLabel().rightAlign());
    	row.appendChild(cboDocumentType.getComponent());
    	row.appendChild(new Space());

    }
    
    
    /**
     * Crea la pestaña de efectivo 
     */
    protected Tabpanel createCashTab(){
       
    	txtEfectivoImporte.setValue("0");
    	
    	Tabpanel tabpanel = new Tabpanel();
    	tabpanel.setHeight("150px");
    	
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(efectivoLibroCaja.getLabel().rightAlign());
		row.appendChild(efectivoLibroCaja.getComponent());
		row.appendChild(txtEfectivoImporte.getLabel().rightAlign());
		row.appendChild(txtEfectivoImporte.getComponent());
		
		tabpanel.appendChild(gridpanel);
        return tabpanel;
    }
    
    /**
     * Crea el panel de pagos
     */
    protected void createPaymentTab(){
    	
    	Div contenedor = new Div();
		
		Tablelayout tablelayout = new Tablelayout();
		tablelayout.setWidth("100%");
		tablelayout.setColumns(2);
		//tablelayout.setStyle("border: 1px solid red");
		
		
		// Panel de la tabla donde se encuentran Campaña, Proyacto y Moneda
		Tablechildren tableCampProy = new Tablechildren(); 
		tableCampProy.setWidth("50%");
		tableCampProy.appendChild(agregarCampProy());
		
		// Panel de la tabla donde se encuentra el Árbol
		Tablechildren tableTree = new Tablechildren();
		tableTree.setWidth("50%");
		tableTree.setHeight("200px");
		tableTree.setRowspan(2);
		tableTree.appendChild(agregarTree());
		
		// Panel de la tabla donde se encuentran los Tabs
		Tablechildren tableTabs = new Tablechildren(); 
		tableTabs.appendChild(agregarTabs());
		
		/****/
		 
		Panel panel4 = new Panel();
		Panelchildren panelchildren4 = new Panelchildren();
		
		Tablechildren table4 = new Tablechildren(); 
		
		// ---
		Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(cmdGrabar);
		createPaymentMediumDiscountPanel(rows);
		// ---
		
		panelchildren4.appendChild(gridpanel);
		panel4.appendChild(panelchildren4);		
		table4.appendChild(panel4);
		
		tablelayout.appendChild(tableCampProy);
		tablelayout.appendChild(tableTree);
		tablelayout.appendChild(tableTabs);
		tablelayout.appendChild(table4);
		
		contenedor.appendChild(tablelayout);
		jTabbedPane2.appendChild(contenedor);
    }

    /**
     * Componentes adicionales al botón de Guardar Cambios (incorporar un medio de pago)
     */
    protected void createPaymentMediumDiscountPanel(Rows rows) {
    	
    }
    
    protected Tabpanel panelPagoAdelantado;
	// Uso de un pago adelantado como parte de una orden de pago
	protected Tabpanel createPagoAdelantadoTab()
	{
		panelPagoAdelantado = new Tabpanel();
		MLookupInfo infoPago = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, 5043, DisplayType.Search, getModel().getPagoAdelantadoSqlValidation());
		Lookup lookupPago = new MLookup(infoPago, 0);
		pagoAdelantado = new WSearchEditor("C_Payment_ID", false, false, true, lookupPago);
		addPopupMenu(pagoAdelantado, true, true, false);
		
        MLookupInfo infoCash = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, 5283, DisplayType.Search, getModel().getCashAnticipadoSqlValidation());
		Lookup lookupCash = new MLookup(infoCash, 0);
		cashAdelantado = new WSearchEditor("C_CashLine_ID", false, false, true, lookupCash);
		addPopupMenu(cashAdelantado, true, true, false);
		
		cashAdelantado.getLabel().setText(getModel().isSOTrx()?"COBRO":"PAGO");
		pagoAdelantado.getLabel().setText(getModel().isSOTrx()?"COBRO":"PAGO");
		txtPagoAdelantadoImporte = new WStringEditor();
		txtPagoAdelantadoImporte.getLabel().setText("IMPORTE");
        txtPagoAdelantadoImporte.setValue("0");

        lblPagoAdelantadoType.setText("TIPO");
        cboPagoAdelantadoType = new Combobox();
        cboPagoAdelantadoType.appendItem(getMsg("Payment"));
        cboPagoAdelantadoType.appendItem(getMsg("Cash"));		
        // Por defecto pago
        cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX); 
        cboPagoAdelantadoType.addEventListener("onChange", new EventListener() {
        	public void onEvent(Event arg0) throws Exception {
				updatePagoAdelantadoTab();
			}
		});
        txtPagoAdelantadoAvailable = new WStringEditor();
        txtPagoAdelantadoAvailable.setReadWrite(false);  
        txtPagoAdelantadoAvailable.getLabel().setText("PENDIENTE");
		
        Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		panelPagoAdelantado.setHeight("150px"); 
		
		Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblPagoAdelantadoType.rightAlign());
		row.appendChild(cboPagoAdelantadoType);
		
		// Uno de los dos (efectivo o pago)
		Row row2a = rows.newRow();
		row2a.appendChild(pagoAdelantado.getLabel().rightAlign());
		row2a.appendChild(pagoAdelantado.getComponent());
		Row row2b = rows.newRow();
		row2b.appendChild(cashAdelantado.getLabel().rightAlign());
		row2b.appendChild(cashAdelantado.getComponent());
		
		Row row3 = rows.newRow();
		row3.appendChild(txtPagoAdelantadoAvailable.getLabel().rightAlign());
		row3.appendChild(txtPagoAdelantadoAvailable.getComponent());
		Row row4 = rows.newRow();
		row4.appendChild(txtPagoAdelantadoImporte.getLabel().rightAlign());
		row4.appendChild(txtPagoAdelantadoImporte.getComponent());
		
        updatePagoAdelantadoTab();
        panelPagoAdelantado.appendChild(gridpanel);

		return panelPagoAdelantado;	
	}    
    
    private void cmdSavePMActionPerformed(Event evt) {//GEN-FIRST:event_cmdSavePMActionPerformed

    	try {
    		
	    	VOrdenPagoModel.MedioPago mp = null;
	    	
	    	switch (mpTabbox.getSelectedIndex())
	    	{
	    	case 0: // Efectivo
	    		mp = saveCashMedioPago();
	    		break;
	    		
	    	case 1: // Transferencia
	    		mp = saveTransferMedioPago();
	    		break;
	    		
	    	case 2: // Cheque
	    		mp = saveCheckMedioPago();
				break;
	    	case 3: // Crédito
	    		mp = saveCreditMedioPago();
	    		break;
	    	case 4:
				// Adelantado
	    		mp = savePagoAdelantadoMedioPago();
	    		break;
	    	default:
	    		cmdCustomSaveMedioPago(mpTabbox.getSelectedIndex());
	    		break;
	    	}
	    	
	    	if(mp != null){
	    		m_model.addMedioPago(mp);
	    	}
	    	updateTreeModel();
	    	pagosTree.setModel(getMediosPagoTreeModel());
	    	clearMediosPago();
			// Actualizar componentes de interfaz gráfica necesarios luego de
			// agregar el medio de pago 
	    	updateCustomInfoAfterMedioPago(MEDIOPAGO_ACTION_INSERT);
    	} catch (Exception e) {
    		String title = Msg.getMsg(m_ctx, "Error");
    		String msg = Msg.parseTranslation(m_ctx, "@SaveErrorNotUnique@ \n\n" + e.getMessage() /*"@SaveError@"*/ );

    		showError(title + ": " + msg);
    	}
    }//GEN-LAST:event_cmdSavePMActionPerformed
    
    
    protected MedioPagoEfectivo saveCashMedioPago() throws Exception{
    	MedioPagoEfectivo mpe = m_model.getNuevoMedioPagoEfectivo();
    	try {
			mpe.monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			mpe.importe = numberParse(txtEfectivoImporte.getValue().toString());
		} catch (Exception e) {
			throw new Exception(txtEfectivoImporte.getLabel().getValue());
		}		
		try {
			mpe.libroCaja_ID = (Integer)efectivoLibroCaja.getValue();
		} catch (NullPointerException ee) {
			throw new Exception(efectivoLibroCaja.getLabel().getValue());
		}
		if (mpe.importe.compareTo(new BigDecimal(0.0)) <= 0)
			throw new Exception(txtEfectivoImporte.getLabel().getValue());

		mpe.setCampaign(getC_Campaign_ID() == null?0:getC_Campaign_ID());
		mpe.setProject(getC_Project_ID() == null?0:getC_Project_ID());
		
		return mpe;
    }
    
    
    protected MedioPagoTransferencia saveTransferMedioPago() throws Exception{
    	MedioPagoTransferencia mpt = m_model.getNuevoMedioPagoTransferencia();		
    	try {
			mpt.monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			mpt.C_BankAccount_ID = (Integer)transfCtaBancaria.getValue();
		} catch (NullPointerException ee) {
			throw new Exception(transfCtaBancaria.getLabel().getValue());
		}	
		try {
			mpt.fechaTransf = Timestamp.valueOf(transFecha.getValue().toString());
		} catch (Exception e) {
			throw new Exception(transFecha.getLabel().getValue());
		}
		try {
			mpt.importe = numberParse(txtTransfImporte.getValue().toString());
		} catch (Exception e) {
			throw new Exception(txtTransfImporte.getLabel().getValue());
		}		
		mpt.nroTransf = txtTransfNroTransf.getValue().toString();
		
		if (mpt.fechaTransf == null)
			throw new Exception(transFecha.getLabel().getValue());
		
		if (mpt.importe.compareTo(new BigDecimal(0.0)) <= 0)
			throw new Exception(txtTransfImporte.getLabel().getValue());
		
		if (mpt.nroTransf.trim().equals(""))
			throw new Exception(txtTransfImporte.getLabel().getValue());
		
		mpt.setCampaign(getC_Campaign_ID() == null?0:getC_Campaign_ID());
		mpt.setProject(getC_Project_ID() == null?0:getC_Project_ID());
		
		return mpt;
    }
    
    
    protected MedioPagoCheque saveCheckMedioPago() throws Exception{
    	MedioPagoCheque mpc = m_model.getNuevoMedioPagoCheque();		
		mpc.aLaOrden = txtChequeALaOrden.getValue().toString();		
		try {
			mpc.monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			mpc.chequera_ID = (Integer)chequeChequera.getValue();
		} catch (Exception e) {
			throw new Exception(chequeChequera.getLabel().getValue());
		}	
		try {
			mpc.fechaEm = Timestamp.valueOf(chequeFechaEmision.getValue().toString());			
		} catch (Exception e) {
			throw new Exception("@Invalid@ @EmittingDate@");
		}
		try {
			mpc.fechaPago = Timestamp.valueOf(chequeFechaPago.getValue().toString());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @PayDate@");
		}
		try {	    		
			mpc.importe = numberParse(txtChequeImporte.getValue().toString());
		} catch (Exception e) {
			throw new Exception(txtChequeImporte.getLabel().getValue());
		}		
		mpc.nroCheque = txtChequeNroCheque.getValue().toString(); 
		mpc.banco = txtChequeBanco.getValue().toString().trim();
		mpc.cuitLibrador = txtChequeCUITLibrador.getValue().toString().trim();
		mpc.descripcion = txtChequeDescripcion.getValue().toString().trim();
		Timestamp today = new Timestamp(System.currentTimeMillis());
		mpc.dateTrx = mpc.fechaPago.before(today)?mpc.fechaPago:today;
		// A La Orden: Campo no obligatorio
		//
		// if (mpc.aLaOrden.trim().equals(""))
		// 	throw new Exception("");
		
		if (mpc.fechaEm == null)
			throw new Exception(chequeFechaEmision.getLabel().getValue());
		
		if (mpc.fechaPago == null)
			throw new Exception(chequeFechaPago.getLabel().getValue());
		
		if (mpc.fechaPago.compareTo(mpc.fechaEm) < 0) {
			throw new Exception(getMsg("InvalidCheckDueDate"));
		}
		
		if (mpc.importe.compareTo(new BigDecimal(0.0)) <= 0)
			throw new Exception(txtChequeImporte.getLabel().getValue());
		
		if (mpc.nroCheque.trim().equals(""))
			throw new Exception(txtChequeNroCheque.getLabel().getValue());
		
		mpc.setCampaign(getC_Campaign_ID() == null?0:getC_Campaign_ID());
		mpc.setProject(getC_Project_ID() == null?0:getC_Project_ID());
		
		// Se actualiza la secuencia de nros de cheque de la chequera en caso de ser posible.
		if (isActualizarNrosChequera()) {
			String nroChequeStr = mpc.nroCheque.trim();
			try {
				int nroCheque = Integer.parseInt(nroChequeStr);
				// El numero de cheque es numérico.
				int C_BankAccountDoc_ID = mpc.chequera_ID;
				// Se incrementa en 1 el numero de cheque;
				nroCheque++;
				// Se guarda el siguiente numero de cheque en la chequera.
				X_C_BankAccountDoc bankAccountDoc = new X_C_BankAccountDoc(Env.getCtx(),C_BankAccountDoc_ID,null);
				bankAccountDoc.setCurrentNext(nroCheque);
				bankAccountDoc.save();
				bankAccountDoc = null;
			} catch (Exception e) {
				// El usuario modifico el numero de cheque agregandole caracteres que no son numericos.
				// En este caso no se actualiza la secuencia de la chequera.
			}
		}
		
		return mpc;
    }
    
    
    protected MedioPagoCredito saveCreditMedioPago() throws Exception{
    	MedioPagoCredito mpcm = m_model.getNuevoMedioPagoCredito();
    	try {
			mpcm.monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			mpcm.setC_invoice_ID((Integer)creditInvoice.getValue());
		} catch (Exception ee) {
			throw new Exception(creditInvoice.getLabel().getValue());
		}
		
		try {
			mpcm.setAvailableAmt(getModel().getCreditAvailableAmt(mpcm.getC_invoice_ID() ) );
		} catch (Exception e) {
			throw new Exception(txtCreditAvailable.getLabel().getValue());
		}
		
		try {
			mpcm.setImporte(numberParse(txtCreditImporte.getValue().toString()));
		} catch (Exception e) {
			throw new Exception(txtCreditImporte.getLabel().getValue());
		}
		
		mpcm.validate();

		mpcm.setCampaign(getC_Campaign_ID() == null?0:getC_Campaign_ID());
		mpcm.setProject(getC_Project_ID() == null?0:getC_Project_ID());
		return mpcm;
    }
    
	protected MedioPago savePagoAdelantadoMedioPago() throws Exception {
		// Obtengo los datos de la interfaz
		boolean isCash = cboPagoAdelantadoType.getSelectedIndex() == PAGO_ADELANTADO_TYPE_CASH_INDEX;
		Integer payID = null;
		BigDecimal amount = null;
		Integer monedaOriginalID;
		
		payID = (Integer)(isCash ? cashAdelantado.getValue() : pagoAdelantado.getValue());
		
		try {
			monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			amount = numberParse(txtPagoAdelantadoImporte.getValue().toString());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @Amount@");
		}
		// Se agrega el cobro adelantado como medio de cobro
		return getModel().addPagoAdelantado(payID, amount, isCash, monedaOriginalID);		
	}
    
    
    private MyTreeNode darElementoArbolSeleccionado() {
    	if (pagosTree.getSelectedCount() == 1) {
			Treeitem item = pagosTree.getSelectedItem(); // .getSelectionPath();

			MyTreeNode tn = (MyTreeNode)(((SimpleTreeNode)item.getValue()).getData());
			return tn;
    	}
    	
    	return null;
    }
    
    private void cmdEditarActionPerformed(Event evt) {//GEN-FIRST:event_cmdEditarActionPerformed
    	try {
			MyTreeNode tn = darElementoArbolSeleccionado();
			if (tn != null) {
				cmdEditMedioPago(tn);
				// Actualizar componentes de interfaz gráfica necesarios luego de
				// agregar el medio de pago 
		    	updateCustomInfoAfterMedioPago(MEDIOPAGO_ACTION_EDIT);
			}
			updateTreeModel();
			pagosTree.setModel(getMediosPagoTreeModel());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }//GEN-LAST:event_cmdEditarActionPerformed

    private void cmdEliminarActionPerformed(Event evt) {//GEN-FIRST:event_cmdEliminarActionPerformed
    	try {
			MyTreeNode tn = darElementoArbolSeleccionado();

			if (tn != null) {
				cmdDeleteMedioPago(tn);
				// Actualizar componentes de interfaz gráfica necesarios luego de
				// agregar el medio de pago 
		    	updateCustomInfoAfterMedioPago(MEDIOPAGO_ACTION_DELETE);
			}
			updateTreeModel();
			pagosTree.setModel(getMediosPagoTreeModel());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }//GEN-LAST:event_cmdEliminarActionPerformed

    private void onFechaChange(boolean toPayMoment) {//GEN-FIRST:event_onFechaChange
    	if (rInvoiceAll.isSelected()) {
    		invoiceDatePick.setValue(new Timestamp(new Date().getTime()));
    		invoiceDatePick.setReadWrite(false);
    	} else {
    		invoiceDatePick.setReadWrite(true); 
    	}
    	
    	m_model.setFechaTablaFacturas(new Timestamp(((Date)invoiceDatePick.getComponent().getValue()).getTime()), rInvoiceAll.isSelected());
    	updatePayAllInvoices(toPayMoment);
    }//GEN-LAST:event_onFechaChange

    private void onTipoPagoChange(boolean toPayMoment) {//GEN-FIRST:event_onTipoPagoChange
    	if (radPayTypeStd.isSelected()) {
    		tblFacturas.setAttribute("ReadOnly", "false"); 
    		txtTotalPagar1.setReadWrite(false); 
    		rInvoiceAll.setDisabled(false);
    		rInvoiceDate.setDisabled(false);
    	} else {
    		tblFacturas.setAttribute("ReadOnly", "true"); 
    		txtTotalPagar1.setReadWrite(true);
    		rInvoiceAll.setSelected(true);
    		rInvoiceAll.setDisabled(false);
    		rInvoiceDate.setDisabled(true);
    		//forzo la verificación de la entidad comercial
    		this.cmdBPartnerSelActionPerformed(null);
    		
    		//
    		
    	}

    	m_model.setPagoNormal(radPayTypeStd.isSelected(), null);
    	
    	onFechaChange(toPayMoment);
    	// Actualizar custom de las subclases
    	updateCustomTipoPagoChange();
    }//GEN-LAST:event_onTipoPagoChange

    
    protected void updatePayAllInvoices(boolean toPayMoment){
    	getModel().updatePayAllInvoices(checkPayAll.isSelected(), toPayMoment);
		// tblFacturas.renderAll(); // repaint();
		resetModel();
    }

    private void cmdCancelActionPerformed(Event evt) {//GEN-FIRST:event_cmdCancelActionPerformed
    	dispose();   	
    }//GEN-LAST:event_cmdCancelActionPerformed
    
    protected void showError(String msg) {
		//String title = Msg.getMsg(m_ctx, "Error");
		String translatedMsg = Msg.parseTranslation(m_ctx, msg /*"@SaveError@"*/ );
		
		FDialog.error(m_WindowNo, this, translatedMsg);
		
    }
	
	/**
	 * Mostrar una ventana dialog con el mensaje parámetro
	 * 
	 * @param msg
	 *            clave ad_message de un mensaje o la descripción de un mensaje.
	 */
    protected void showInfo(String msg){
    	FDialog.info(m_WindowNo, this, msg);
    }
    
    private void cmdProcessActionPerformed(Event evt) {

    	final int idx = tabbox.getSelectedIndex();
    	
    	if (idx == 0) {

    		clearMediosPago();
    		// Procesar
    		
    		BigDecimal monto = null;
    		
    		try {
    			monto = numberParse(txtTotalPagar1.getValue().toString());
    		} catch (ParseException e) {
    			showError("@SaveErrorNotUnique@ \n\n" + txtTotalPagar1.getLabel().getValue());
    			
        		txtTotalPagar1.getComponent().setFocus(true); // .requestFocusInWindow();
        		if (txtTotalPagar1.getValue().toString().trim().length() > 0) 
        			txtTotalPagar1.getValue().toString().substring(0, txtTotalPagar1.getValue().toString().length() - 1);
        		
        		return;
    		}
    		
    		m_model.setActualizarFacturasAuto(false);
    		// Fuerzo la actualizacion de los valores de la interfaz
    		onTipoPagoChange(true);
    		m_model.setPagoNormal(radPayTypeStd.isSelected(), monto);
    		m_model.setActualizarFacturasAuto(true);
    		m_model.setDocumentNo(fldDocumentNo.getValue().toString());
    		// Realizar acciones antes del PreProcesar
    		try {
        		makeOperationsBeforePreProcesar();
			} catch (Exception e) {
				showError("Se produjo un error al pre procesar. "+e.getMessage());
				return;
			}
  		
    		int status = m_model.doPreProcesar();
    		updateTreeModel();
    		
    		switch ( status )
    		{
    		case VOrdenPagoModel.PROCERROR_OK:
    			break;
    			
    		case VOrdenPagoModel.PROCERROR_INSUFFICIENT_INVOICES:
    			showError("@InsufficientInvoicesToPayError@");
    			return;
    		
    		case VOrdenPagoModel.PROCERROR_NOT_SELECTED_BPARTNER:
    			showError("@NotSelectedBPartner@");
    			return;

    		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_NOT_SET:
    			showError("Debe indicar el número de documento");
    			return;
    		
    		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_ALREADY_EXISTS:
    			showError("Número de documento ya existente");
    			return;
    		
    		case VOrdenPagoModel.PROCERROR_DOCUMENTTYPE_NOT_SET:
    			showError("Debe indicar el tipo de documento");
    			return;
    			
    		case VOrdenPagoModel.PROCERROR_BOTH_EXCHANGE_INVOICES:
    			showError("@BothExchangeInvoices@");
    			return;
    			
    		default:
    			showError("@ValidationError@");
    			return;
    		}
    		
			// Actualizo componentes gráficos antes de pasar a la siguiente
			// pestaña
    		updateComponentsPreProcesar();
    		// Avanza a la siguiente tab
    		m_cambioTab = true;
    		tabbox.setSelectedIndex(1);
//    		m_cambioTab = false;  // <- diferencias con Swing en los tiempos de eventos. Se pone a false en el evento 
    		
    		updatePaymentsTabsState();
    		treeUpdated();
    		// Actualizar el arbol para casos donde hay que mostrar retenciones
	    	updateTreeModel();
	    	pagosTree.setModel(getMediosPagoTreeModel());
    		// Actualizar descuento de entidad comercial
    		customUpdateBPartnerRelatedComponents(false);
    		
    	} else if (idx == 1) {
    		getModel().setDescription(txtDescription.getComponent().getText());
    		
    		m_model.setProjectID(getC_Project_ID() == null?0:getC_Project_ID());
    		m_model.setCampaignID(getC_Campaign_ID() == null?0:getC_Campaign_ID());
    		BigDecimal exchangeDifference = getModel().calculateExchangeDifference();
    		m_model.setExchangeDifference( exchangeDifference == null?BigDecimal.ZERO:exchangeDifference);
    		
    		int status = m_model.doPostProcesar();
    		
    		switch (status) 
    		{
    		case VOrdenPagoModel.PROCERROR_OK:
    			break;
    		
    		case VOrdenPagoModel.PROCERROR_PAYMENTS_AMT_MATCH:
    			showError("@PaymentsAmtMatchError@");
    			break;
    			
    		case VOrdenPagoModel.PROCERROR_PAYMENTS_GENERATION:
    			showError("@PaymentsGenerationError@ : "+ this.m_model.getMsgAMostrar());
    			break;
    			
    		default:
    			showError("@Error@"+ this.m_model.getMsgAMostrar());
    			break;
    		}
    		
    		if (status == VOrdenPagoModel.PROCERROR_OK)
    		{	
    			// FEDE:TODO refactor pending: similar a VOrdenPagoModel.mostrarInforme
    			if (m_model.m_newlyCreatedC_AllocationHeader_ID <= 0)
    				return;
    	        int proc_ID = DB.getSQLValue( null, "SELECT AD_Process_ID FROM AD_Process WHERE value='" + m_model.getReportValue()+ "' " );
    	        if( proc_ID > 0 ) {

    	        	MPInstance instance = new MPInstance( Env.getCtx(), proc_ID, 0, null );
    	            if( !instance.save()) {
    	            	log.log(Level.SEVERE, "Error at mostrarInforme: instance.save()");
    	                showError("Error al mostrar informe. " + CLogger.retrieveErrorAsString());
    	                return;
    	            }
    	            ProcessInfo pi = new ProcessInfo( getReportName(),proc_ID );
    	            pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());
    	            
    	            MPInstancePara ip;
    	            
    	            ip = new MPInstancePara( instance, 10 );
    	            ip.setParameter( "C_AllocationHdr_ID",String.valueOf(m_model.m_newlyCreatedC_AllocationHeader_ID ));
    	            if( !ip.save()) {
    	            	log.log(Level.SEVERE, "Error at mostrarInforme: ip.save()");
    	            	showError("Error al mostrar informe. " + CLogger.retrieveErrorAsString());
    	                return;
    	            }
    	            
    	            MProcess process = new MProcess(Env.getCtx(), proc_ID, null);
    	            try {
    	            	MProcess.execute(Env.getCtx(), process, pi, null);
    	            } catch (Exception e) {
    	            	showError("Error al mostrar informe. " + e.getMessage());
    	            	e.printStackTrace();
    	            }
    	        }
	    		// Reset	    		
	    		reset();
    		}
    	}    	
    }//GEN-LAST:event_cmdProcessActionPerformed
    
    protected String getReportName() {
    	return "Orden de Pago";
    }
    
    /**
     * Metodo que determina el valor que se encuentra dentro de la entidad comercial.
     * Si es null y está seteado el radio button de pago anticipado, no se puede pasar a Siguiente.
     * Para que el boton Siguiente se encuentre habilitado, debería ingresar una entidad comercial en el LookUP
     * @param evt
     */
    private void cmdBPartnerSelActionPerformed(ValueChangeEvent evt){
    	if((this.BPartnerSel.getValue() == null)){
    		this.cmdProcess.setEnabled(false);
    	}
    	else{
    		this.cmdProcess.setEnabled(true);
    	}
    }
    
    
    // Declaración de varibales -no modificar//GEN-BEGIN:variables
    
    protected WSearchEditor BPartnerSel;
    protected WStringEditor fldDocumentNo;
    
    protected Radiogroup buttonGroup1;
    protected Radiogroup buttonGroup2;
   
    protected WTableDirEditor cboCampaign;
    protected WTableDirEditor cboClient;
    protected WTableDirEditor cboOrg;
    protected WTableDirEditor cboProject;
    protected WTableDirEditor cboCurrency;
    
    protected WSearchEditor chequeChequera;
    protected WDateEditor chequeFechaEmision;
    protected WDateEditor chequeFechaPago;
    protected Button cmdCancel;
    
    protected Button cmdEditar;
    protected Button cmdEliminar;
    protected Button cmdGrabar;
    protected Button cmdProcess;
 
    protected WSearchEditor efectivoLibroCaja;    
    
    protected WDateEditor invoiceDatePick;
    protected Grid jPanel1;
    
    // Tabs
    protected Tabpanel jTabbedPane1 = new Tabpanel();
    protected Tabpanel jTabbedPane2 = new Tabpanel();
 	protected Tabbox tabbox = new Tabbox();
 	private Tabs tabs = new Tabs();
 	private Tabpanels tabpanels = new Tabpanels();

    protected WTableDirEditor cboDocumentType;
    
    /*
    protected javax.swing.JPanel panelCamProy;
    */
    protected Radio rInvoiceAll;
    protected Radio rInvoiceDate;
    protected Radio radPayTypeAdv;
    protected Radio radPayTypeStd;
    protected Checkbox checkPayAll;
    
    protected FacturasModel listModel;
    protected Grid tblFacturas;
    protected GridRenderer renderer;
    protected WDateEditor transFecha;
    protected WSearchEditor transfCtaBancaria;

    protected WStringEditor txtDescription;
    
    protected WStringEditor txtChequeALaOrden;
    protected WStringEditor txtChequeImporte;
    protected WStringEditor txtChequeNroCheque;
    protected WStringEditor txtChequeDescripcion;
    
    protected WStringEditor txtEfectivoImporte;
    
    protected WStringEditor txtMedioPago2;
    protected WStringEditor txtRetenciones2;
    protected WStringEditor txtSaldo;
    protected WStringEditor txtDifCambio;
    
    protected WStringEditor txtTotalPagar1;
    
    protected WStringEditor txtTotalPagar2;
    protected WStringEditor txtTransfImporte;
    protected WStringEditor txtTransfNroTransf;
    // Fin de declaraci�n de variables//GEN-END:variables

    protected WSearchEditor creditInvoice;
    protected WStringEditor txtCreditImporte;
    protected WStringEditor txtCreditAvailable;
	protected WStringEditor txtChequeBanco;
	protected WSearchEditor cboChequeBancoID;
	protected WStringEditor txtChequeCUITLibrador;

    protected WStringEditor txtPagoAdelantadoImporte;
    
    protected Label lblPagoAdelantadoType = new Label();
    protected WSearchEditor pagoAdelantado;
    protected WSearchEditor cashAdelantado;
    protected Combobox cboPagoAdelantadoType;
    protected WStringEditor txtPagoAdelantadoAvailable;

    protected static final int PAGO_ADELANTADO_TYPE_PAYMENT_INDEX = 0;
    protected static final int PAGO_ADELANTADO_TYPE_CASH_INDEX = 1;
    protected MSequence seq;
    
    private int m_chequeTerceroTabIndex = -1;
    
    private Tabpanel panelChequeTercero;
    private WSearchEditor chequeTerceroCuenta;
    private WSearchEditor chequeTercero;
    private WStringEditor txtChequeTerceroImporte;
    private WStringEditor txtChequeTerceroDescripcion;

	private boolean m_cambioTab = false;
	protected int m_C_Currency_ID = Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" );
	private static CLogger log = CLogger.getCLogger( WOrdenPago.class );
	protected VOrdenPagoModel m_model = new VOrdenPagoModel();
	protected Properties m_ctx = Env.getCtx();
	private boolean actualizarNrosChequera = true;
    
	Tab tabPaymentSelection;
	Tab tabPaymentRule;

	
    protected static final String GOTO_BPARTNER = "GOTO_BPARTNER";
    protected static final String MOVE_INVOICE_FORWARD = "MOVE_INVOICE_FORWARD";
    protected static final String MOVE_INVOICE_BACKWARD = "MOVE_INVOICE_BACKWARD";
    protected static final String GOTO_PROCESS = "GOTO_PROCESS";
    protected static final String GOTO_EXIT = "GOTO_EXIT";
    protected static final String ADD_PAYMENT = "ADD_PAYMENT";
    protected static final String EDIT_PAYMENT = "EDIT_PAYMENT";
    protected static final String REMOVE_PAYMENT = "REMOVE_PAYMENT";
    protected static final String GO_BACK = "GO_BACK";
    protected static final String MOVE_PAYMENT_FORWARD = "MOVE_PAYMENT_FORWARD";
    protected static final String MOVE_PAYMENT_BACKWARD = "MOVE_PAYMENT_BACKWARD";
    protected static final String GOTO_PAYALL = "GOTO_PAYALL";
    
    protected static final Integer TAB_INDEX_EFECTIVO = 0;
    protected static final Integer TAB_INDEX_TRANSFERENCIA = 1;
    protected static final Integer TAB_INDEX_CHEQUE = 2;
    protected static final Integer TAB_INDEX_CREDITO = 3;
    protected static final Integer TAB_INDEX_PAGO_ADELANTADO = 4;
    
    protected static final Integer MEDIOPAGO_ACTION_INSERT = 0;
    protected static final Integer MEDIOPAGO_ACTION_EDIT = 1;
    protected static final Integer MEDIOPAGO_ACTION_DELETE = 2;

    
	protected void customInitComponents() {
		
		Date d = new Date();
		
		invoiceDatePick.getComponent().setValue(d);
		
		
		clearMediosPago();
		
		//
		
		txtTotalPagar1.getLabel().setText("");
		
		txtSaldo.getLabel().setText("");
		txtDifCambio.getLabel().setText("");
		txtTotalPagar2.getLabel().setText("");
		txtRetenciones2.getLabel().setText("");
		txtMedioPago2.getLabel().setText("");
		txtDescription.getComponent().setText("");

		txtCreditAvailable.getLabel().setText("");
		txtPagoAdelantadoAvailable.getLabel().setText("");
	
		//
		cboCurrency.addValueChangeListener(this);
		efectivoLibroCaja.addValueChangeListener(this);
		transfCtaBancaria.addValueChangeListener(this);
		chequeChequera.addValueChangeListener(this);
		BPartnerSel.addValueChangeListener(this);
		
		chequeFechaEmision.addValueChangeListener(this);
		chequeFechaPago.addValueChangeListener(this);
		invoiceDatePick.addValueChangeListener(this); // addValueChangeListener(this);
		transFecha.addValueChangeListener(this);
		
		tblFacturas.addEventListener("onChange", this); // addValueChangeListener(this);
		getModel().m_facturasTableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				// Se verifica que no se esté intentando pagar una factura que no tiene una tasa de cambio para la fecha actual
				validateConversionRate();
				tableUpdated();		
				resetModel();
			}
		});

        cboClient.setReadWrite(false);
        cboClient.setValue(Env.getAD_Client_ID(m_ctx));
        cboOrg.setMandatory(true);
        cboOrg.addValueChangeListener(this);
        cboOrg.setValue(Env.getAD_Org_ID(m_ctx));
        
        cboDocumentType.setMandatory(true);
        cboDocumentType.addValueChangeListener(this);

        // campo para numero de documento
//        fldDocumentNo.setMandatory(true);
        
        updateOrg((Integer)cboOrg.getValue());
		
        String Element_MC = Env.getContext(m_ctx,"$Element_MC");
        String Element_PJ = Env.getContext(m_ctx,"$Element_PJ");
        
        cboCampaign.setVisible("Y".equals(Element_MC));
        cboCampaign.setMandatory(false);
        cboCampaign.setValue(null);
//        cboCampaign.refresh();
        
        cboProject.setVisible("Y".equals(Element_PJ));
        cboProject.setMandatory(false);
        cboProject.setValue(null);
//        cboProject.refresh();
        
        cboCurrency.setMandatory(true);
        cboCurrency.setValue(m_C_Currency_ID);
//        cboCurrency.refresh();
        setCurrencyContext();

        // Cuando cambia el documento de crédito, se carga el 
        // importe disponible en el text correspondiente
        creditInvoice.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent evt) {
				Integer invoiceID = (Integer)creditInvoice.getNewValueOnChange();
				if (invoiceID != null)
					txtCreditAvailable.setValue(getModel().numberFormat(getModel().getCreditAvailableAmt(invoiceID)));
			}
		});
        
    	BPartnerSel.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent evt) {
				setBPartnerContext();
			}
		});
    	
    	cboCurrency.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent evt) {
				setCurrencyContext();
    			updatePayAmt(getModel().getSaldoMediosPago());
			}
		});
        // Cuando cambia el documento de pago adelantado, se carga el 
        // importe disponible en el text correspondiente
        pagoAdelantado.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent evt) {
				Integer paymentID = (Integer)pagoAdelantado.getNewValueOnChange();
				if (paymentID != null)
					txtPagoAdelantadoAvailable.setValue(getModel().numberFormat(getModel().getPagoAdelantadoAvailableAmt(paymentID)));
			}
		});

        // Cuando cambia el documento de efectivo adelantado, se carga el 
        // importe disponible en el text correspondiente
        cashAdelantado.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent evt) {
				Integer cashLineID = (Integer)cashAdelantado.getNewValueOnChange();
				if (cashLineID != null)
					txtPagoAdelantadoAvailable.setValue(getModel().numberFormat(getModel().getCashAdelantadoAvailableAmt(cashLineID)));
			}
		});
        
        // Total a pagar 1
    	updateTotalAPagar1();
        // Agregado de pestañas con otras formas de pago. Método vacío que deben
        // implementar las subclases en caso de que quieran agregar otras pestañas.
        addCustomPaymentTabs();
        // Agregado de operaciones luego de crear las pestañas custom
        addCustomOperationAfterTabsDefinition();
        
	}
	
	protected void initTranslations() {
		String name;

		// Efectivo
		
		txtEfectivoImporte.getLabel().setText(Msg.translate(Env.getCtx(), "Amount"));
		efectivoLibroCaja.getLabel().setText(Msg.translate(Env.getCtx(), "C_CashBook_ID"));
		
		// Transferencia 
		
		transfCtaBancaria.getLabel().setText(Msg.getElement(m_ctx, "C_BankAccount_ID"));
		transFecha.getLabel().setText(Msg.getMsg(m_ctx, "Date"));
		txtTransfImporte.getLabel().setText(Msg.getElement(m_ctx, "Amount"));
		txtTransfNroTransf.getLabel().setText(Msg.getMsg(m_ctx, "TransferNumber"));
		
		// Cheques
		
		txtChequeALaOrden.getLabel().setText(Msg.getMsg(m_ctx, "ALaOrden"));
		chequeChequera.getLabel().setText(Msg.getElement(m_ctx, "CheckAccount"));
		chequeFechaEmision.getLabel().setText(Msg.getMsg(m_ctx, "EmittingDate"));
		chequeFechaPago.getLabel().setText(Msg.getElement(m_ctx, "PayDate"));
		txtChequeImporte.getLabel().setText(Msg.getElement(m_ctx, "Amount"));
		txtChequeNroCheque.getLabel().setText(Msg.getElement(m_ctx, "CheckNo"));
		txtChequeBanco.getLabel().setText(Msg.translate(m_ctx, "C_Bank_ID"));
		txtChequeCUITLibrador.getLabel().setText(Msg.translate(m_ctx, "CUITLibrador"));
		txtChequeDescripcion.getLabel().setText(getMsg("Description"));
		
		// Credito
		creditInvoice.getLabel().setText(Msg.translate(m_ctx, "Credit"));
		txtCreditAvailable.getLabel().setText(Msg.translate(m_ctx, "OpenAmt"));
		txtCreditImporte.getLabel().setText(Msg.getElement(m_ctx, "Amount"));
		
		BPartnerSel.getLabel().setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		
		txtTotalPagar1.getLabel().setText(Msg.translate(Env.getCtx(), "Amount"));
		
		txtDescription.getLabel().setText(Msg.translate(Env.getCtx(), "Description"));
		
		radPayTypeStd.setLabel(Msg.translate(Env.getCtx(), "StandardPayment"));
		radPayTypeAdv.setLabel(Msg.translate(Env.getCtx(), "AdvancedPayment"));
		
		txtTotalPagar2.getLabel().setText(Msg.getElement(m_ctx, "Amount"));
		txtMedioPago2.getLabel().setText(Msg.getElement(m_ctx, "TenderType"));
		txtRetenciones2.getLabel().setText(Msg.getElement(m_ctx, "C_Withholding_ID"));
		txtDifCambio.getLabel().setText(Msg.getMsg(m_ctx, "ExchangeDifference"));
		
		rInvoiceAll.setLabel(Msg.translate(Env.getCtx(), "SearchAND"));
		//rInvoiceDate.setText(Msg.translate(m_ctx, "DueStart"));
		rInvoiceDate.setLabel(Msg.translate(Env.getCtx(), "BeforeDueDate"));
	
		checkPayAll.setText(Msg.translate(Env.getCtx(), "PayAll"));
		
		// Saldo Total
		name = VModelHelper.GetReferenceValueTrlFromColumn("I_ReportLine", "AmountType", "BT", "name");
		txtSaldo.getLabel().setText(name != null ? name : "");
		
		//
		
		cmdCancel.setLabel(Msg.getMsg(m_ctx, "Close")/*+" "+KeyUtils.getKeyStr(getActionKeys().get(GOTO_EXIT))*/);
		cmdEditar.setLabel(Msg.translate(Env.getCtx(), "Edit").replace("&", ""));
		cmdEliminar.setLabel(Msg.translate(Env.getCtx(), "Delete").replace("&", ""));
		cmdGrabar.setLabel(Msg.translate(Env.getCtx(), "Save").replace("&", ""));
		cmdProcess.setLabel(Msg.translate(Env.getCtx(), "Processing"));

		cboClient.getLabel().setText(Msg.translate(Env.getCtx(), "AD_Client_ID"));
		fldDocumentNo.getLabel().setText("Nro. Documento");
        cboOrg.getLabel().setText(Msg.translate(Env.getCtx(),"AD_Org_ID"));
        cboDocumentType.getLabel().setText(Msg.translate(Env.getCtx(),"C_DOCTYPE_ID"));
        
        
		cboProject.getLabel().setText(Msg.translate(Env.getCtx(), "C_Project_ID"));
		cboCurrency.getLabel().setText(Msg.translate(Env.getCtx(), "C_Currency_ID"));
		cboCampaign.getLabel().setText(Msg.translate(Env.getCtx(), "C_Campaign_ID"));
		
		lblPagoAdelantadoType.setText(getMsg("Type"));
		txtPagoAdelantadoAvailable.getLabel().setText(Msg.translate(m_ctx, "OpenAmt"));
		
		// Pagos Adelantados
		txtPagoAdelantadoImporte.getLabel().setText(Msg.getElement(m_ctx, "Amount"));
		cashAdelantado.getLabel().setText(getMsg("Payment"));
		
        updateCaptions();
        
	}
	
	protected void updateCaptions() {
		
		// Traduccion del boton PROCESAR
		// Actualizar las acciones habilitadas de los atajos para cada pestaña
		if (tabbox.getSelectedIndex() == 0){
			cmdProcess.setLabel(Msg.translate(Env.getCtx(), "NextStep"));
		}
		else if (tabbox.getSelectedIndex() == 1){
			cmdProcess.setLabel(Msg.translate(Env.getCtx(),"EmitPayment"));
		}
		
		// Las subclases también deben realizar las operaciones necesarias
		// correspondientes en esta instancia
		customUpdateCaptions();
		
	}
	
	protected void clearMediosPago() {
		
		Date d = new Date();
		
		// Efectivo
		
		efectivoLibroCaja.setValue(null);
		txtEfectivoImporte.setValue("0");
		
		// Transferencia 
		
		transfCtaBancaria.setValue(null);
		transFecha.setValue(d);
		txtTransfImporte.setValue("");
		txtTransfNroTransf.setValue("");
		
		// Cheque
		
		chequeChequera.setValue(null);
		chequeFechaEmision.setValue(d);
		chequeFechaPago.setValue(null);
		if (getModel().getBPartner() == null) 
			txtChequeALaOrden.setValue("");
		// Modified by Matías Cap -Disytel
		// El campo de cheque a la orden no se limpia cuando se pasan de
		// pestañas, es mas queda la misma descripción para diferentes OP 
		// para distintas entidades comerciales. La modificación involucra que
		// cuando tenemos una entidad comercial entonces coloque el nombre
		// sacando lo que había puesto antes.
		// -------------------------------------------------------------
		// Líneas anteriores comentadas 
		// -------------------------------------------------------------
//		else if (txtChequeALaOrden.getText().equals(""))
//			txtChequeALaOrden.setText(getModel().getBPartner().getName());
		// -------------------------------------------------------------
		else
			txtChequeALaOrden.setValue(getModel().getBPartner().getName());
		// -------------------------------------------------------------
		txtChequeImporte.setValue("");
		txtChequeNroCheque.setValue("");
		txtChequeBanco.setValue("");
		txtChequeCUITLibrador.setValue("");
		txtChequeDescripcion.setValue("");
		
		// Credito
		creditInvoice.setValue(null);
		txtCreditAvailable.setValue("");
		txtCreditImporte.setValue("");
		
		// Pago anticipado
		pagoAdelantado.setValue(null);
		txtPagoAdelantadoImporte.setValue("");
		cashAdelantado.setValue(null);
		txtPagoAdelantadoAvailable.setValue("");
		
		// Cheque de tercero
		if (m_chequeTerceroTabIndex >= 0) { // Está disponible la opcion de cheques de tercero
			//chequeTerceroCuenta.setValue(null); No se borra la cuenta para la comodidad del usuario, dado que es un campo de filtro.
			chequeTercero.setValue(null);
			txtChequeTerceroImporte.setValue("");
			txtChequeTerceroDescripcion.setValue("");
		}
		updatePaymentsTabsState();
	}
	
	protected void loadMedioPago(VOrdenPagoModel.MedioPago mp) {
		
		clearMediosPago();
		
		if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_EFECTIVO)) { // Efectivo - Cash 
		
			VOrdenPagoModel.MedioPagoEfectivo mpe = (VOrdenPagoModel.MedioPagoEfectivo)mp;
			
			efectivoLibroCaja.setValue(mpe.libroCaja_ID);
			txtEfectivoImporte.setValue(m_model.numberFormat(mpe.importe));
			
			mpTabbox.setSelectedIndex(TAB_INDEX_EFECTIVO);
			
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_TRANSFERENCIA)) { // Transferencia - Transfer
			
			VOrdenPagoModel.MedioPagoTransferencia mpt = (VOrdenPagoModel.MedioPagoTransferencia)mp;
			
			transfCtaBancaria.setValue(mpt.C_BankAccount_ID);
			transFecha.setValue(mpt.fechaTransf);
			txtTransfImporte.setValue(m_model.numberFormat(mpt.importe));
			txtTransfNroTransf.setValue(mpt.nroTransf);
			
			mpTabbox.setSelectedIndex(TAB_INDEX_TRANSFERENCIA);
			
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_CHEQUE)) { // Cheque - Check
			
			VOrdenPagoModel.MedioPagoCheque mpc = (VOrdenPagoModel.MedioPagoCheque)mp;
			
			chequeChequera.setValue(mpc.chequera_ID);
			chequeFechaEmision.setValue(mpc.fechaEm);
			chequeFechaPago.setValue(mpc.fechaPago);
			txtChequeALaOrden.setValue(mpc.aLaOrden);
			txtChequeImporte.setValue(m_model.numberFormat(mpc.importe));
			txtChequeNroCheque.setValue(mpc.nroCheque);
			txtChequeBanco.setValue(mpc.banco);
			txtChequeCUITLibrador.setValue(mpc.cuitLibrador);
			txtChequeDescripcion.setValue(mpc.descripcion);
			
			mpTabbox.setSelectedIndex(TAB_INDEX_CHEQUE);
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_CREDITO)) { // Credito
			VOrdenPagoModel.MedioPagoCredito mpcm = (VOrdenPagoModel.MedioPagoCredito)mp;
			creditInvoice.setValue(mpcm.getC_invoice_ID());
			txtCreditAvailable.setValue(mpcm.getAvailableAmt().toString());
			txtCreditImporte.setValue(mpcm.getImporte().toString());
			
			mpTabbox.setSelectedIndex(TAB_INDEX_CREDITO);
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_PAGOANTICIPADO)) { // Pago adelantado
			VOrdenPagoModel.MedioPagoAdelantado mpa = (VOrdenPagoModel.MedioPagoAdelantado)mp;
			cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX);
			pagoAdelantado.setValue(mpa.getC_Payment_ID());
			txtPagoAdelantadoImporte.setValue(mpa.getImporte().toString());
			txtPagoAdelantadoAvailable.setValue(getModel().getPagoAdelantadoAvailableAmt(mpa.getC_Payment_ID()).toString());
			
			mpTabbox.setSelectedIndex(TAB_INDEX_PAGO_ADELANTADO);
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_EFECTIVOADELANTADO)) { // Efectivo adelantado
			VOrdenPagoModel.MedioPagoEfectivoAdelantado mpa = (VOrdenPagoModel.MedioPagoEfectivoAdelantado)mp;
			cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_CASH_INDEX);
			cashAdelantado.setValue(mpa.getCashLineID());
			txtPagoAdelantadoImporte.setValue(mpa.getImporte().toString());
			txtPagoAdelantadoAvailable.setValue(getModel().getCashAdelantadoAvailableAmt(mpa.getCashLineID()).toString());
			
			mpTabbox.setSelectedIndex(TAB_INDEX_PAGO_ADELANTADO);
		} else if (mp.getTipoMP().equals(VOrdenPagoModel.MedioPago.TIPOMEDIOPAGO_CHEQUETERCERO)) { // Cheque de tercero
			VOrdenPagoModel.MedioPagoChequeTercero mpct = (VOrdenPagoModel.MedioPagoChequeTercero)mp;
			chequeTercero.setValue(mpct.getC_Payment_ID());
			txtChequeTerceroImporte.setValue(mpct.getImporte().toString());
			txtChequeTerceroDescripcion.setValue(mpct.description);
			
			mpTabbox.setSelectedIndex(m_chequeTerceroTabIndex);
		}
			
	}
		
	public void dispose() {
		super.dispose();
		m_model.dispose();
		m_model = null;
		
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		// System.out.println("tableChanged: " + arg0);
		if ( (arg0.getColumn() == m_model.m_facturasTableModel.getColumnCount() - 1) || (arg0.getColumn() == m_model.m_facturasTableModel.getColumnCount() - 2) ){
			// Se actualizó el monto manual
			for (int row = arg0.getFirstRow(); row <= arg0.getLastRow() && row < m_model.m_facturas.size(); row++) {
				ResultItemFactura rif = (ResultItemFactura)m_model.m_facturas.get(row);
				int currency_ID_To = (Integer) m_model.m_facturas.get(row).getItem(m_model.m_facturasTableModel.getCurrencyColIdx());
				if (arg0.getColumn() == m_model.m_facturasTableModel.getColumnCount() - 1){
					m_model.actualizarPagarConPagarCurrency(row,rif,currency_ID_To, false);
				}
				else{
					m_model.actualizarPagarCurrencyConPagar(row,rif,currency_ID_To, false);
				}
				m_model.m_facturasTableModel.fireTableDataChanged();	
			}
			resetModel();
		}
	}

	public void valueChange(ValueChangeEvent e) {
		// System.out.println("vetoableChange: " + arg0);
		if (e.getSource() == BPartnerSel) {

        	// Actualizo el modelo. Utilizo newValueOnChange, dado que si utilizo newValue,
			// obtengo null (o el valor viejo) en este punto del código
			getModel().updateBPartner(null);
        	getModel().updateBPartner((Integer)BPartnerSel.getNewValueOnChange());
			// Actualizo los componentes custom de la interfaz gráfica
			// relacionados con el cambio de la entidad comercial
        	customUpdateBPartnerRelatedComponents(true);
        	buscarPagos();
        	// Actualizar interfaz grafica para null value
			if (BPartnerSel.getNewValueOnChange() == null)
				this.cmdProcess.setEnabled(false); //cmdBPartnerSelActionPerformed(null);
        	updatePayAllInvoices(false);
		
		} else if (e.getSource() == cboCurrency) {
			updateDependent();
		} else if (e.getSource() == efectivoLibroCaja) {
			
		} else if (e.getSource() == transfCtaBancaria) {
			
		} else if (e.getSource() == chequeChequera) {
			chequeraChange(e);
		} else if (e.getSource() == invoiceDatePick) {
			
			onFechaChange(false);
			
		} else if (e.getSource() == cboOrg) {
			int AD_Org_ID = (Integer)e.getNewValue();
			updateOrg(AD_Org_ID);
		
		} else if (e.getSource() == cboDocumentType) {
			if(e.getNewValue() != null){
				m_model.setDocumentType((Integer)e.getNewValue());
				seq = MSequence.get(m_ctx, getSeqName(), false, Env.getAD_Client_ID(m_ctx));
				fldDocumentNo.setValue(seq.getCurrentNext().toString());
			}
			else{
				fldDocumentNo.setValue(null);
				m_model.setDocumentType(null);
			}
		}
	}

	private void updateDependent() {
		efectivoLibroCaja.setValue(null);
	}


	@Override
	public void onEvent(Event arg0) throws Exception {
		if (arg0.getTarget().getParent().getParent() == tabbox) {
			// TAB principal
			if (!m_cambioTab && tabbox.getSelectedIndex() == 1) {
				tabbox.setSelectedIndex(0);
			}
			if (m_cambioTab)
				m_cambioTab = false;
			updateCaptions();
			
			BPartnerSel.setReadWrite(tabbox.getSelectedIndex() == 0);
			cboOrg.setReadWrite(tabbox.getSelectedIndex() == 0);
			cboDocumentType.setReadWrite(tabbox.getSelectedIndex() == 0);
			fldDocumentNo.setReadWrite(tabbox.getSelectedIndex() == 0);
		} else if (arg0.getTarget().getParent().getParent() == mpTabbox) {
			// TAB de medios de pago
			updateContextValues();
		}
	}
	
	private void treeUpdated() {
		// Update stats text fields
		updateSummaryInfo();
	}

	/**
	 * Actualización de la organización
	 * @param AD_Org_ID id de la organización nueva
	 */
    
	protected void updateOrg(Integer AD_Org_ID){
		getModel().updateOrg(AD_Org_ID);
    	updatePayAllInvoices(false);
	}
	
	
	/**
	 * Actualización de la información de resumen de pagos, saldo, retenciones,
	 * etc.
	 */
	protected void updateSummaryInfo(){
		BigDecimal sumaMediosPago = m_model.getSumaMediosPago();
		
		txtSaldo.setValue(numberFormat(m_model.getSaldoMediosPago()));
		txtDifCambio.setValue(numberFormat(getModel().calculateExchangeDifference()));
		txtTotalPagar2.setValue(numberFormat(m_model.getSumaTotalPagarFacturas()));
		txtRetenciones2.setValue(numberFormat(m_model.getSumaRetenciones()));
		txtMedioPago2.setValue(numberFormat(sumaMediosPago));
		
		cboProject.setReadWrite(sumaMediosPago.signum() == 0);
		cboCampaign.setReadWrite(sumaMediosPago.signum() == 0);
	}

	protected String numberFormat(BigDecimal nn) {
		return m_model.numberFormat(nn);
	}
	
	protected BigDecimal numberParse(String nn) throws ParseException {
		return m_model.numberParse(nn);
	}
	
	protected void tableUpdated() {
		updateTotalAPagar1();
	}
	
	protected void validateConversionRate() {
		if (!m_model.validateConversionRate()){
			showError("@NoCurrencyConvertError@");
		}
	}

	protected void setModel(VOrdenPagoModel model) {
		m_model = model;
	}
	
	protected VOrdenPagoModel getModel() {
		return m_model;
	}
	
	protected WSearchEditor createChequeChequeraLookup() {
		MLookupInfo info = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_BankAccountDoc_ID", "C_BankAccountDoc", DisplayType.Search, m_model.getChequeChequeraSqlValidation());
		MLookup lookup = new MLookup(info, 0);
		WSearchEditor editor = new WSearchEditor("C_BankAccountDoc_ID", false, false, true, lookup);
		addPopupMenu(editor, true, true, false);
		return editor;
	}

	protected WSearchEditor createChequeBancoIDLookup() {
		MLookupInfo info = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_Bank_ID", "C_Bank", DisplayType.Search, null);
		MLookup lookup = new MLookup(info, 0);
		WSearchEditor editor = new WSearchEditor("C_Bank_ID", false, false, true, lookup);
		addPopupMenu(editor, true, true, false);
		return editor;
	}
	
	protected void chequeraChange(ValueChangeEvent e) {
		Integer C_BankAccountDoc_ID = (Integer) e.getNewValue();
		if(C_BankAccountDoc_ID != null && C_BankAccountDoc_ID > 0) {
			X_C_BankAccountDoc bankAccountDoc = new X_C_BankAccountDoc(Env.getCtx(),C_BankAccountDoc_ID,null);
			int nextNroCheque = bankAccountDoc.getCurrentNext();
			txtChequeNroCheque.setValue(String.valueOf(nextNroCheque));
		} else
			txtChequeNroCheque.setValue("");
	}

	/**
	 * @return the actualizarNrosChequera
	 */
 
	protected boolean isActualizarNrosChequera() {
		return actualizarNrosChequera;
	}
	
	/**
	 * @param actualizarNrosChequera the actualizarNrosChequera to set
	 */
	protected void setActualizarNrosChequera(boolean actualizarNrosChequera) {
		this.actualizarNrosChequera = actualizarNrosChequera;
	}
	
	/**
	 * Redefinir en caso de ser nesario ampliar el conjunto de pestañas con medios de
	 * pago. Aquí se deben agregar las pestañas necesarias por las especializaciones.
	 * @param tabbedPane Panel de pestaña que contiene los medios de pagos.
	 */
	protected void addCustomPaymentTabs() { 
		// Agregado de pestaña de medio de pago cheques de terceros.
		// Incorporar nueva pestaña y panel
		Tab tab = new Tab(getMsg("ThirdPartyCheck"));
		mpTabs.appendChild(tab);
		mpTabpanels.appendChild(createChequeTerceroTab());
		// Determinar posicion de la pestaña
		int i = 0;
		for (Object child : mpTabpanels.getChildren()) {
			if (child == panelChequeTercero)
				m_chequeTerceroTabIndex = i;
			i++;
		}
	}
	
	/**
	 * Redefinir en caso que de ser necesario ampliar la funcionalidad y
	 * operaciones luego de crear pestañas custom si hubiere.
	 */
	protected void addCustomOperationAfterTabsDefinition(){
		// Incorporación de operaciones luego de la definición de las pestañas
		// custom
	}
	
	/**
	 * Guardado de medios de pago específicos de las subclases. En caso de que se intente
	 * guardar un medio de pago que no es de esta clase, entonces se invoca este método
	 * el cual debe implementar la lógica de guardado del medio de pago indicado por
	 * el tabIndex.
	 * @param tabIndex Indice de pestaña que contiene los datos del medio de pago.
	 */
    
	protected void cmdCustomSaveMedioPago(int tabIndex) throws Exception { 
		if (tabIndex == m_chequeTerceroTabIndex)
			saveChequeTerceroMedioPago();
	}
	
	protected Integer getC_Campaign_ID() {
		return (Integer)cboCampaign.getValue();
	}
	
	protected Integer getC_Project_ID() {
		return (Integer)cboProject.getValue();
	}
	
	protected boolean canEditTreeNode(MyTreeNode treeNode) {
		return treeNode.isMedioPago();
	}
	
	/**
	 * Ingresa en modo de edición el medio de pago seleccionado en el árbol.
	 */
 
	protected void cmdEditMedioPago(MyTreeNode tn) {
		VOrdenPagoModel.MedioPago mp = (VOrdenPagoModel.MedioPago)tn.getUserObject();
		
		if (mp == null)
			return;
		
		if (tn.isMedioPago()) {
			m_model.removeMedioPago(mp);
			loadMedioPago(mp);
		}
	}
	
	/**
	 * Borrado del nodo actual del arbol de medios de pago y retenciones.
	 */
    
	protected void cmdDeleteMedioPago(MyTreeNode tn) {
		VOrdenPagoModel.MedioPago mp = (VOrdenPagoModel.MedioPago)tn.getUserObject();
		
		if (mp == null)
			return;
		
		if (tn.isMedioPago()) {
			if (confirmDeleteMP(mp)){
				m_model.removeMedioPago(mp);
				updatePayAmt(getModel().getSaldoMediosPago());
			}
		}
	}

	protected boolean confirmDeleteMP(Object mp) {
		String msg = Msg.parseTranslation(m_ctx, "@DeleteRecord?@\r\n" + mp.toString());
		String title = Msg.getMsg(m_ctx, "Delete"); 
		return FDialog.ask(m_WindowNo, this, title + ": " + msg);
	}

	protected String getMsg(String name) {
		return Msg.translate(m_ctx, name);
	}
	
	protected void updatePaymentsTabsState() {
		((Tab)(mpTabs.getChildren().get(TAB_INDEX_CREDITO))).setDisabled(!m_model.isNormalPayment());
		((Tab)(mpTabs.getChildren().get(TAB_INDEX_PAGO_ADELANTADO))).setDisabled(!m_model.isNormalPayment());		
		// Refrescar el monto de la pestaña con el total a pagar
		updatePayAmt(getModel().getSaldoMediosPago());
//		mpTabbox.setSelectedIndex(TAB_INDEX_EFECTIVO);
	}
	
	protected void updatePagoAdelantadoTab() {
		pagoAdelantado.setValue(null);
		cashAdelantado.setValue(null);
		txtPagoAdelantadoAvailable.setValue("");
		if (cboPagoAdelantadoType.getSelectedIndex() == PAGO_ADELANTADO_TYPE_PAYMENT_INDEX) {
			pagoAdelantado.setVisible(true);
			cashAdelantado.setVisible(false);
			pagoAdelantado.getLabel().setText(getMsg("Payment"));
			pagoAdelantado.getLabel().setVisible(true);
			cashAdelantado.getLabel().setVisible(false);
		} else if (cboPagoAdelantadoType.getSelectedIndex() == PAGO_ADELANTADO_TYPE_CASH_INDEX) {
			cashAdelantado.setVisible(true);
			pagoAdelantado.setVisible(false);
			cashAdelantado.getLabel().setText(getMsg("Cash"));
			cashAdelantado.getLabel().setVisible(true);
			pagoAdelantado.getLabel().setVisible(false);
		}
	}
	

	private Tabpanel createChequeTerceroTab() {
		panelChequeTercero = new Tabpanel();
		
		// Cuenta
		MLookupInfo info = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_BankAccount_ID", "C_BankAccount", DisplayType.Search, m_model.getChequeTerceroCuentaSqlValidation());
		Lookup lookup = new MLookup(info, 0);
		chequeTerceroCuenta = new WSearchEditor("C_BankAccount_ID", false, false, true, lookup);
		chequeTerceroCuenta.getLabel().setText(getMsg("Account"));
		addPopupMenu(chequeTerceroCuenta, true, true, false);
        chequeTerceroCuenta.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent evt) {
	        		Integer bankAccountID = (Integer)chequeTerceroCuenta.getNewValueOnChange();
	        		if (bankAccountID == null)
	        			bankAccountID = 0;
	        		Env.setContext(m_ctx, m_WindowNo, "C_BankAccount_ID", bankAccountID);
			}
		});
        chequeTerceroCuenta.setValue(null);
		// Cheque
		info = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_Payment_ID", "C_Payment", DisplayType.Search, m_model.getChequeTerceroSqlValidation());
		lookup = new MLookup(info, 0);
		chequeTercero = new WSearchEditor("C_Payment_ID", false, false, true, lookup);
        chequeTercero.getLabel().setText(getMsg("Check"));
        addPopupMenu(chequeTercero, true, true, false);
        chequeTercero.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent evt) {
				Integer paymentID = (Integer)chequeTercero.getNewValueOnChange();
				String importe = "";
				if (paymentID != null)
					importe = getModel().numberFormat(getModel().getChequeAmt(paymentID));
				txtChequeTerceroImporte.setValue(importe);			
			}
		}); 
        // Importe
        txtChequeTerceroImporte = new WStringEditor();
        txtChequeTerceroImporte.getLabel().setText(Msg.getElement(m_ctx, "Amount"));
        txtChequeTerceroImporte.setValue("0");
        txtChequeTerceroImporte.setValue(null);
        txtChequeTerceroImporte.setReadWrite(false);
        // Descripcion
        txtChequeTerceroDescripcion = new WStringEditor();
        txtChequeTerceroDescripcion.getLabel().setText(getMsg("Description"));

        panelChequeTercero.setHeight("150px");
    	
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(chequeTerceroCuenta.getLabel().rightAlign());
		row.appendChild(chequeTerceroCuenta.getComponent());
		row.appendChild(chequeTercero.getLabel().rightAlign());
		row.appendChild(chequeTercero.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(txtChequeTerceroImporte.getLabel().rightAlign());
		row2.appendChild(txtChequeTerceroImporte.getComponent());
		row2.appendChild(txtChequeTerceroDescripcion.getLabel().rightAlign());
		row2.appendChild(txtChequeTerceroDescripcion.getComponent());
		
		panelChequeTercero.appendChild(gridpanel);

        
		return panelChequeTercero;
	}
	
	protected Tabpanel createCheckTab() {
        chequeChequera.getLabel().setText("CHEQUERA");
        txtChequeNroCheque.getLabel().setText("NUMERO DE CHEQUE");
        txtChequeImporte.getLabel().setText("IMPORTE");
        chequeFechaEmision.getLabel().setText("FECHA EMISION");
        chequeFechaPago.getLabel().setText("FECHA PAGO");
        txtChequeALaOrden.getLabel().setText(getModel().isSOTrx()?"LIBRADOR":"A LA ORDEN");
        txtChequeBanco.getLabel().setText("BANCO");
        txtChequeCUITLibrador.getLabel().setText("CUIT LIBRADOR");
        txtChequeDescripcion.getLabel().setText("DESCRIPCION");      
        
        Tabpanel tabpanel = new Tabpanel();
    	tabpanel.setHeight("150px");
    	
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(chequeChequera.getLabel().rightAlign());
		row.appendChild(chequeChequera.getComponent());
		row.appendChild(txtChequeNroCheque.getLabel().rightAlign());
		row.appendChild(txtChequeNroCheque.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(txtChequeImporte.getLabel().rightAlign());
		row2.appendChild(txtChequeImporte.getComponent());
		Row row3 = rows.newRow();
		row3.appendChild(chequeFechaEmision.getLabel().rightAlign());
		row3.appendChild(chequeFechaEmision.getComponent());
		row3.appendChild(chequeFechaPago.getLabel().rightAlign());
		row3.appendChild(chequeFechaPago.getComponent());
		Row row4 = rows.newRow();
		row4.appendChild(txtChequeALaOrden.getLabel().rightAlign());
		row4.appendChild(txtChequeALaOrden.getComponent());
		row4.appendChild(txtChequeBanco.getLabel().rightAlign());
		row4.appendChild(txtChequeBanco.getComponent());
		Row row5 = rows.newRow();
		row5.appendChild(txtChequeCUITLibrador.getLabel().rightAlign());
		row5.appendChild(txtChequeCUITLibrador.getComponent());
		row5.appendChild(txtChequeDescripcion.getLabel().rightAlign());
		row5.appendChild(txtChequeDescripcion.getComponent());

		tabpanel.appendChild(gridpanel);
        return tabpanel;

	}

	protected Tabpanel createCreditTab() {
        Tabpanel tabpanel = new Tabpanel();
    	tabpanel.setHeight("150px");
		
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(creditInvoice.getLabel().rightAlign());
		row.appendChild(creditInvoice.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(txtCreditAvailable.getLabel().rightAlign());
		row2.appendChild(txtCreditAvailable.getComponent());
		Row row3 = rows.newRow();
		row3.appendChild(txtCreditImporte.getLabel().rightAlign());
		row3.appendChild(txtCreditImporte.getComponent());
		
        txtCreditAvailable.setValue("0");        
        txtCreditImporte.setValue("0");
		
		tabpanel.appendChild(gridpanel);
        return tabpanel;
	}
	
	protected Tabpanel createTransferTab() {
		
        transfCtaBancaria.getLabel().setText("CUENTA BANCARIA");
        txtTransfNroTransf.getLabel().setText("NRO TRANSFERENCIA");
        txtTransfImporte.getLabel().setText("IMPORTE");
        transFecha.getLabel().setText("FECHA");
        txtTransfImporte.setValue("0");
        
        Tabpanel tabpanel = new Tabpanel();
    	tabpanel.setHeight("150px");
		
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(transfCtaBancaria.getLabel().rightAlign());
		row.appendChild(transfCtaBancaria.getComponent());
		row.appendChild(txtTransfNroTransf.getLabel().rightAlign());
		row.appendChild(txtTransfNroTransf.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(txtTransfImporte.getLabel().rightAlign());
		row2.appendChild(txtTransfImporte.getComponent());
		row2.appendChild(transFecha.getLabel().rightAlign());
		row2.appendChild(transFecha.getComponent());
		
		txtTransfImporte.setValue("0");
		
		tabpanel.appendChild(gridpanel);
        return tabpanel;
	}
	
	
	
	private void saveChequeTerceroMedioPago() throws Exception {
		Integer paymentID = (Integer)chequeTercero.getValue();
		BigDecimal importe; 
		Integer monedaOriginalID;
		try {
			monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			importe = numberParse(txtChequeTerceroImporte.getValue().toString());
		} catch (Exception e) {
			throw new Exception(txtChequeTerceroImporte.getLabel().getValue());
		}
		String description = txtChequeTerceroDescripcion.getValue().toString().trim();
		getModel().addChequeTercero(paymentID, importe, description, monedaOriginalID);
	}
	
	private void updateContextValues() {
		if (mpTabbox.getSelectedIndex() == m_chequeTerceroTabIndex) {
			setIsSOTrxContext("Y");
			setBPartnerContext(null);
		} else {
			setIsSOTrxContext();
			setBPartnerContext();
		}
		updatePaymentsTabsState();
	}
	
	private void setIsSOTrxContext(String value) {
		Env.setContext(m_ctx,m_WindowNo,"IsSOTrx", value);
	}
	
	private void setIsSOTrxContext() {
		setIsSOTrxContext(getModel().getIsSOTrx());
	}
	
	private void setBPartnerContext(Integer bPartnerID) {
		if (bPartnerID != null)
			Env.setContext(m_ctx, m_WindowNo, "C_BPartner_ID", bPartnerID);
		else
			Env.setContext(m_ctx, m_WindowNo, "C_BPartner_ID", (String)null);
	}
	
	private void setCurrencyContext(Integer currencyID) {
		if (currencyID != null){
			Env.setContext(m_ctx, m_WindowNo, "C_Currency_ID", currencyID);
			// Es necesario setear el Currency_Id a 0 ya que al querer utilizarlo en alguna consulta de filtro para un VLookUp lo busca con m_WindowNo 0.
			Env.setContext(m_ctx, 0, "C_Currency_ID", currencyID);
		}
		else
			Env.setContext(m_ctx, m_WindowNo, "C_Currency_ID", (String)null);
	}
	
	private void setBPartnerContext() {
		Integer value = 0;
		if (BPartnerSel.getValue() != null)
			value = (Integer)BPartnerSel.getValue(); 
		setBPartnerContext(value);
	}
	
	private void setCurrencyContext() {
		Integer value = 0;
		if (cboCurrency.getValue() != null)
			value = (Integer) cboCurrency.getValue(); 
		setCurrencyContext(value);
	}
	
	/**
	 * Busca las notas de créditos o pagos anticipados sin imputar para la
	 * entidad comercial seleccionada
	 */
    
	private void buscarPagos() {
		String title = Msg.getMsg(m_ctx, "InfoPayment");
		Integer value = 0;
		if (BPartnerSel.getValue() != null) {
			value = (Integer) BPartnerSel.getValue();
			if (getModel().buscarPagos(value) == true) {
				showInfo("El Proveedor tiene notas de crédito o pagos anticipados sin imputar");
			}
		}
	}

	private void viewDescription() {		
		txtDescription.getComponent().setText("");
		Integer value = 0;
		if (BPartnerSel.getValue() != null) {
			value = (Integer) BPartnerSel.getValue();
			MBPartner bpartner = new MBPartner(m_ctx, value.intValue(), null);			
			if (bpartner.getDescription() != null) {
				if (bpartner.getDescription().compareTo("") != 0) {
					String description=bpartner.getDescription();					
					getModel().setDescription(description);
					txtDescription.getComponent().setText(getModel().getDescription());
				}
			}
		}
	}
	
	/**
	 * Actualizar el total a pagar de la primer pestaña
	 */    
	protected void updateTotalAPagar1(){
		txtTotalPagar1.setValue(numberFormat(m_model.getSumaTotalPagarFacturas()));
	}
	
	/**
	 * Actualiza componentes custom de la interfaz gráfica relacionadas con el
	 * cambio de entidad comercial
	 */
    
	protected void customUpdateBPartnerRelatedComponents(boolean loadingBP){
		if(loadingBP==true)
		{	
			viewDescription();	
		}
		// Por ahora aca no se hace nada, verificar subclases
	}
	
	protected void updatePayAmt(BigDecimal amt){
		int currencyID = ( (Integer) cboCurrency.getValue() == null) ? m_C_Currency_ID : (Integer) cboCurrency.getValue();

		amt = MCurrency.currencyConvert(amt, m_C_Currency_ID, currencyID, new Timestamp(System.currentTimeMillis()), getModel().AD_Org_ID, m_ctx);

		Integer tabIndexSelected = mpTabbox.getSelectedIndex();
		if(tabIndexSelected.equals(TAB_INDEX_CHEQUE)){
			txtChequeImporte.setValue(numberFormat(amt));
		}
		else if(tabIndexSelected.equals(TAB_INDEX_CREDITO)){
			txtCreditImporte.setValue(numberFormat(amt));
		}
		else if(tabIndexSelected.equals(TAB_INDEX_EFECTIVO)){
			txtEfectivoImporte.setValue(numberFormat(amt));
		}
		else if(tabIndexSelected.equals(TAB_INDEX_PAGO_ADELANTADO)){
			txtPagoAdelantadoImporte.setValue(numberFormat(amt));
		}
		else if(tabIndexSelected.equals(TAB_INDEX_TRANSFERENCIA)){
			txtTransfImporte.setValue(numberFormat(amt));
		}
		else {
			updateCustomPayAmt(amt);
		}
	}
	
	protected void updateCustomPayAmt(BigDecimal amt){
		// Por ahora aca no se hace nada, verificar subclases
	}
	
	
	/**
	 * Actualizo componentes luego del preprocesar y antes de procesar.
	 */
	protected void updateComponentsPreProcesar(){
		// Por ahora aca no se hace nada, verificar subclases		
	}
	/**
	 * Actualiza componentes de interfaz gráfica luego de agregar o eliminar un
	 * medio de pago
	 * 
	 * @param recibe
	 *            la acción que se realizó sobre el medio de pago, las acciones
	 *            pueden ser inserción, edición y eliminación, valores
	 *            referenciados en {@link VOrdenPago#MEDIOPAGO_ACTION_INSERT},
	 *            {@link VOrdenPago#MEDIOPAGO_ACTION_EDIT} y
	 *            {@link VOrdenPago#MEDIOPAGO_ACTION_DELETE}.
	 */
	protected void updateCustomInfoAfterMedioPago(Integer medioPagoAction){
		// Por ahora aca no se hace nada, verificar subclases
	}
	/**
	 * Realizar operaciones antes del pro-procesar
	 */   
	protected void makeOperationsBeforePreProcesar() throws Exception{
		// Por ahora no hace nada aquí
	}
	
	/**
	 * Realizar operaciones luego de cambiar el tipo de pago, normal o
	 * adelantado
	 */
	protected void updateCustomTipoPagoChange(){
		// Por ahora no hace nada aquí
	}
	
	/**
	 * Realizar operaciones luego de iniciar los atajos y sus operaciones
	 */
	protected void customKeyBindingsInit(){
		// Por ahora no hace nada aquí		
	}
	
	/**
	 * Realizar operaciones luego de actualizar los captions y acciones
	 */
	protected void customUpdateCaptions(){
		// Por ahora no hace nada aquí
	}
	/**
	 * Resetear info.
	 */
	protected void reset(){
		BPartnerSel.setValue(null);
		m_model.setBPartnerFacturas(0);
		radPayTypeStd.setSelected(true);
		m_model.actualizarFacturas();
		cboCampaign.setValue(null);
		cboProject.setValue(null);
		cboCurrency.setValue(m_C_Currency_ID);
		txtTotalPagar1.setValue(null);
		m_cambioTab = true;
		tabbox.setSelectedIndex(0);
		m_cambioTab = false;
		txtDescription.getComponent().setText("");
		m_model.setDescription("");
		checkPayAll.setSelected(false);
		// actualizar secuencia
		seq.setCurrentNext(seq.getCurrentNext().add(BigDecimal.ONE));
		seq.save();
		fldDocumentNo.setValue(seq.getCurrentNext().toString());
		
		m_model.setDocumentNo("");
		getModel().reset();
		updateTreeModel();
		pagosTree.setModel(getMediosPagoTreeModel());
	}
	
	protected String getSeqName()
	{
		if (m_model.getDocumentType() != null){
			return DB.getSQLValueString( null,"SELECT s.name FROM AD_Sequence s INNER JOIN C_DocType d ON (s.AD_Sequence_ID = d.docnosequence_ID) WHERE C_DocType_ID =?",m_model.getDocumentType());
		}
		return null;
		
	}
    
	@Override
	protected void initForm() {

		try
		{
			
			setIsSOTrxContext();
			
	        if (Env.getAD_Org_ID(Env.getCtx()) == 0) {
	        	showError("@InvalidPORCOrg@");
	        	return;
        	};
			
			initComponents();
			dynInit();
			customInitComponents();
			initTranslations();
			getModel().m_facturasTableModel.addTableModelListener(this);
			
			onTipoPagoChange(false);
			
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "init", e);
		}
	}


	private void dynInit() {
		// TODO Auto-generated method stub
		
	}

	
	protected Panel agregarCampProy() {
		
		Panel panel = new Panel();
		Panelchildren panelchildren = new Panelchildren();
		//panelchildren.setStyle("border: 1px solid red");
		
		Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
		Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(cboCampaign.getLabel().rightAlign());
		row.appendChild(cboCampaign.getComponent());
		
		//row = rows.newRow();
		row.appendChild(cboProject.getLabel().rightAlign());
		row.appendChild(cboProject.getComponent());
		
		row = rows.newRow();
		row.appendChild(cboCurrency.getLabel().rightAlign());
		row.appendChild(cboCurrency.getComponent());
		
		addFieldsToCampProy(row);
		
		panelchildren.appendChild(gridpanel);
		panel.appendChild(panelchildren);
		
        return panel;
	}
	
	protected void addFieldsToCampProy(Row row) {
	}
	
	// Nodos base del arbol
	private MyTreeNode m_nodoRaiz = null;
	private MyTreeNode m_nodoRetenciones = null;
	private MyTreeNode m_nodoMediosPago = null;
	
	// Arbol de medios de pago
	protected Tree pagosTree;
	// Modelo del arbol
	protected ExtendedTreeModel m_arbolModel;
	
	/**
	 * Crea el arbol y resumen totalizado
	 */
	private Panel agregarTree() {
		
		Panel panel = new Panel();
		Panelchildren panelchildren = new Panelchildren();

		pagosTree = new Tree();
		pagosTree.setRows(15);
		pagosTree.setModel(getMediosPagoTreeModel());
		pagosTree.setTreeitemRenderer(m_arbolModel);
		
		panelchildren.appendChild(pagosTree);
		panelchildren.appendChild(cmdEliminar);
		panelchildren.appendChild(cmdEditar);
		
		// Summary
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row1 = rows.newRow();
		row1.appendChild(txtSaldo.getLabel().rightAlign());
		row1.appendChild(txtSaldo.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(txtDifCambio.getLabel().rightAlign());
		row2.appendChild(txtDifCambio.getComponent());
		row2.appendChild(txtTotalPagar2.getLabel().rightAlign());
		row2.appendChild(txtTotalPagar2.getComponent());
		Row row3 = rows.newRow();
		row3.appendChild(txtRetenciones2.getLabel().rightAlign());
		row3.appendChild(txtRetenciones2.getComponent());
		row3.appendChild(txtMedioPago2.getLabel().rightAlign());
		row3.appendChild(txtMedioPago2.getComponent());
		addSummaryCustomFields(rows);
		
		panelchildren.appendChild(gridpanel);
		
		panel.appendChild(panelchildren);
        return panel;
	}

	/**
	 * Permite agregar campos en el tab summary
	 */
	protected void addSummaryCustomFields(Rows rows) {
		
	}
	
	/** Panel de medios de pago */
	protected Tabpanels mpTabpanels;
	/** Pestañas de medios de pago */
	protected Tabs mpTabs;
	/** Contenedor de pestañas/paneles de medios de pago */
	protected Tabbox mpTabbox = new Tabbox();
	
	private Panel agregarTabs() {
		
		Panel panel = new Panel();
		Panelchildren panelchildren = new Panelchildren();
		//panelchildren.setStyle("border: 1px solid red");
	    
		mpTabpanels = new Tabpanels();
		mpTabpanels.appendChild(createCashTab());
		mpTabpanels.appendChild(createTransferTab());
		mpTabpanels.appendChild(createCheckTab());
		mpTabpanels.appendChild(createCreditTab());
		mpTabpanels.appendChild(createPagoAdelantadoTab());
	    
	    Tab tab0 = new Tab(VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "B", "name"));
	    Tab tab1 = new Tab(VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "Tr", "name"));
	    Tab tab2 = new Tab(VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "S", "name"));
	    Tab tab3 = new Tab(Msg.translate(m_ctx, "Credit"));
	    Tab tab4 = new Tab(getMsg("AdvancedPayment"));
		 
	    mpTabs = new Tabs();
	    mpTabs.appendChild(tab0);
	    mpTabs.appendChild(tab1);
	    mpTabs.appendChild(tab2);
	    mpTabs.appendChild(tab3);
	    mpTabs.appendChild(tab4);
	 	
	    mpTabbox = new Tabbox();
	    mpTabbox.setMold("accordion");;
	    mpTabbox.setHeight("100%");
	    mpTabbox.appendChild(mpTabs);
	    mpTabbox.appendChild(mpTabpanels);
	    mpTabbox.addEventListener("onSelect", this);
	    
		panelchildren.appendChild(mpTabbox);
		panel.appendChild(panelchildren);
	    
	    return panel;
	}


	protected void createPaymentSelectionTab() {
		Hbox contenedor1 = new Hbox();
		contenedor1.setWidth("100%");
		//contenedor1.setStyle("border: 1px solid red");
		
		Div divButtonGroup1 = new Div();
		//caja1.setStyle("border: 1px solid red");
		divButtonGroup1.appendChild(buttonGroup1);
		
		Div divCheckPayAll = new Div();
		createPayAllDiv(divCheckPayAll);
		
		contenedor1.appendChild(divButtonGroup1);
		contenedor1.appendChild(divCheckPayAll);
		jTabbedPane1.appendChild(contenedor1);
		
		jTabbedPane1.appendChild(new Separator());
	
		jTabbedPane1.appendChild(tblFacturas);
		
		jTabbedPane1.appendChild(new Separator());
		
		Hbox contenedor2 = new Hbox();
		contenedor2.setWidth("100%");
		
		Div divButtonGroup2 = new Div();
		divButtonGroup2.appendChild(buttonGroup2);
		divButtonGroup2.appendChild(invoiceDatePick.getComponent());
		
		Div divTxtEfectivoImporte = new Div();
		setDivTxtTotal(divTxtEfectivoImporte);

		contenedor2.appendChild(divButtonGroup2);
		contenedor2.appendChild(divTxtEfectivoImporte);
		jTabbedPane1.appendChild(contenedor2);
	}
	
	/**
	 * Agrega campo de PayAll (u otros en subclase)
	 */
	protected void createPayAllDiv(Div divCheckPayAll) {
		divCheckPayAll.setAlign("end");
		divCheckPayAll.appendChild(checkPayAll);
	}
	
	
	/**
	 * Seteo de campos en la part inferior derecha
	 */
	protected void setDivTxtTotal(Div divTxtEfectivoImporte) {
		divTxtEfectivoImporte.setAlign("end");
		divTxtEfectivoImporte.appendChild(txtTotalPagar1.getLabel());
		divTxtEfectivoImporte.appendChild(txtTotalPagar1.getComponent());		
	}
	
	
	/**
	 * Fuerza el reseteo del modelo de la Grid en base a los datos del modelo de VOrdenPagoModel
	 */
	protected void resetModel() {
		listModel = new FacturasModel(VModelHelper.HideColumnsTableModelFactory(m_model.m_facturasTableModel), m_WindowNo);
		tblFacturas.setModel(listModel);
		updateTotalAPagar1();
	}
	
	/**
	 * Encargada de renderizar la grilla de facturas a pagar
	 */
	public static class GridRenderer implements RowRenderer {

		WOrdenPago owner = null;
		Columns cols = new Columns();

		public GridRenderer(WOrdenPago owner) {
			this.owner = owner;
		}
		
		@Override
		public void render(org.zkoss.zul.Row arg0, Object arg1) throws Exception {	
			// Si no hay modelo, nada para dibujar
			if (owner.listModel == null)
				return;
			// Si no hay columnas, nada para dibujar
			if (owner.listModel.getColumnCount() == 0)
				return;

			// Resetear las columnas
			owner.tblFacturas.removeChild(cols);
			cols = new Columns();
			int colCount = owner.listModel.getColumnCount();
			for (int i=0; i < colCount; i++) {
				Column col = new Column();
				col.setLabel(owner.listModel.getColumnName(i));
				cols.appendChild(col);
				col.setVisible(!owner.shouldHideColumn(i));
			}
			owner.tblFacturas.appendChild(cols);		
			
			// Setear la fila
			Object[] _data = (Object[])arg1;
			for (int i = 0; i < colCount; i++) {
				// Las dos ultimas columnas son para seteo de datos 
				if (owner.listModel.model.isCellEditable(1, i) && !owner.checkPayAll.isChecked()) {
					Textbox aTextbox = new Textbox();
					aTextbox.setValue(_data[i].toString()); 
					aTextbox.setWidth("60px");
					aTextbox.setParent(arg0);
					// Setear toPay (anteultima columna) hacia toPayCurrency (ultima columna)
					if (i == colCount - 2) {
						aTextbox.addEventListener(Events.ON_OK, new EventListener() {
							@Override
							public void onEvent(Event evt) throws Exception {
								toPay2toPayCurrency();
							}
						});
					}
					// Setea toPayCurrency (ultima columna) hacia toPay (anteultima columna)
					if (i == colCount - 1) {					
						aTextbox.addEventListener(Events.ON_OK, new EventListener() {
							@Override
							public void onEvent(Event evt) throws Exception {
								toPayCurrency2toPay();
							}
						});
					}
				} 
				else {
					Label aLabel = null;
					if (_data[i] instanceof BigDecimal)
						aLabel = new Label(owner.numberFormat((BigDecimal)_data[i]));
					else
						aLabel = new Label(_data[i].toString()); 
					aLabel.setParent(arg0);
				}
				arg0.setVisible(!owner.shouldHideColumn(i));
			}

		}
		
		/**
		 * Setea toPay (anteultima columna) hacia toPayCurrency (ultima columna) 
		 */
		public void toPay2toPayCurrency() {
			int rows = owner.listModel.model.getRowCount(); 
			int cols = owner.listModel.model.getColumnCount();
			ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
			// Recuperar valores de la columna toPay a partir del Textbox
			for (int row = 0; row < rows; row++) {
				BigDecimal newValue = new BigDecimal(0);
				try {
					newValue = new BigDecimal(((Textbox)owner.tblFacturas.getCell(row, cols - 2)).getValue());
				}
				catch (Exception e) { }
				values.add(newValue);
			}
			// Setear valores en el modelo
			for (int row = 0; row < rows; row++) {
				((FacturasModel)owner.tblFacturas.getModel()).model.setValueAt(values.get(row), row, cols - 2);
			}
			// Invocar al metodo para actualizar toPayCurrency
			for (int row = 0; row < rows; row++) {
				ResultItemFactura rif = (ResultItemFactura)owner.getModel().m_facturas.get(row);
				int currency_ID_To = (Integer)owner.getModel().m_facturas.get(row).getItem(owner.getModel().m_facturasTableModel.getCurrencyColIdx());
				owner.getModel().actualizarPagarCurrencyConPagar(row,rif,currency_ID_To, false);
			}
			owner.resetModel();
		}
		
		/**
		 * Setea toPayCurrency (ultima columna) hacia toPay (anteultima columna) 
		 */
		public void toPayCurrency2toPay() {
			int rows = owner.listModel.model.getRowCount(); 
			int cols = owner.listModel.model.getColumnCount();
			ArrayList<BigDecimal> values = new ArrayList<BigDecimal>();
			// Recuperar valores de la columna toPayCurrency  a partir del Textbox
			for (int row = 0; row < rows; row++) {
				BigDecimal newValue = new BigDecimal(0);
				try {
					newValue = new BigDecimal(((Textbox)owner.tblFacturas.getCell(row, cols - 1)).getValue());
				}
				catch (Exception e) { }
				values.add(newValue);
			}
			// Setear valores en el modelo
			for (int row = 0; row < rows; row++) {
				((FacturasModel)owner.tblFacturas.getModel()).model.setValueAt(values.get(row), row, cols - 1);
			}
			// Invocar al metodo para actualizar toPay
			for (int row = 0; row < rows; row++) {
				ResultItemFactura rif = (ResultItemFactura)owner.getModel().m_facturas.get(row);
				int currency_ID_To = (Integer)owner.getModel().m_facturas.get(row).getItem(owner.getModel().m_facturasTableModel.getCurrencyColIdx());
				owner.getModel().actualizarPagarConPagarCurrency(row,rif,currency_ID_To, false);
			}
			owner.resetModel();
		}
	}
	
	/**
	 * Para que el renderer pueda visualizar o no ciertas columnas (a redefinir por subclases)
	 */
	protected boolean shouldHideColumn(int columnNo) {
		return false;
	}
	
	
	/**
	 * Model para la grilla de Facturas
	 * 
	 * Esta clase delega la estructura y lógica de determinación de los registros a recuperar al 
	 * módulo original de Ordenes de Pago, a fin de centralizar la lógica en un único lugar y
	 * simplificar ademas la complejidad de WOrdenPago.
	 */
	public static class FacturasModel extends AbstractListModel implements TableModelListener, ListModelExt {

		public TableModel model = null;
		
		public FacturasModel(TableModel model, int windowNo) {
			this.model = model;
		}

		@Override
		public Object getElementAt(int rowIndex) {
			if (model==null)
				return null;
			int columnCount = model.getColumnCount();
			Object[] values = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) {
					values[i] = model.getValueAt(rowIndex, i);
				}		
			return values;
		}

		public Object getElementAt(int row, int col) {
			return model.getValueAt(row, col);
		}
		
		@Override
		public int getSize() {
			if (model==null)
				return 0;
			return model.getRowCount();
		}

		@Override
		public void sort(Comparator arg0, boolean arg1) {
			System.out.println("Sorted!");
			
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			System.out.println("Changed!");		
		}	
		
		public int getColumnCount() {
			return model.getColumnCount();
		}
		
		public String getColumnName(int columnIndex) {
			return model.getColumnName(columnIndex);
		}
		
	}

	/**
	 * Nodos de pago
	 */
	public static class MyTreeNode extends DefaultMutableTreeNode {
		
		protected String m_msg;
		protected boolean m_leaf;
		
		public MyTreeNode(String msg, Object obj, boolean leaf) {
			m_msg = msg;
			m_leaf = leaf;
			userObject = obj;
		}
		
		public void setMsg(String msg) {
			m_msg = msg;
		}
		
		public String toString() {
			return m_msg != null ? m_msg : userObject.toString();
		}
		
		public boolean isLeaf() {
			return m_leaf;
		}
		
		public boolean isMedioPago() {
			return userObject != null && userObject instanceof MedioPago;
		}
		
		public boolean isRetencion() {
			return userObject != null && !isMedioPago();
		}

		public Object getUserObject() {
			return userObject;
		}

		public void setUserObject(Object userObject) {
			this.userObject = userObject;
		}
	}

	
	protected void initTreeModel() {
		String nodoRaizMsg ;
		String nodoRetencionesMsg = Msg.getElement(m_ctx, "C_Withholding_ID");
		String nodoMediosPagoMsg = Msg.translate(m_ctx, m_model.getMsgMap().get("TenderType"));
		String bpName = m_model.BPartner != null ? m_model.BPartner.getName() + " - " : "";
		
		String monto = numberFormat(m_model.getSumaTotalPagarFacturas());
		
		nodoRaizMsg = bpName + Msg.getElement(m_ctx, "Amount") + ": " + monto;
		
		// Crear los nodos base o actualizar sus mensajes
		
		if (m_nodoRaiz == null) {
			m_nodoRaiz = new MyTreeNode(nodoRaizMsg, null, false);
		} else {
			m_nodoRaiz.setMsg(nodoRaizMsg);
			m_nodoRaiz.removeAllChildren();
		}
		
		if (m_nodoRetenciones == null)
			m_nodoRetenciones = new MyTreeNode(nodoRetencionesMsg, null, false);
		else
			m_nodoRetenciones.setMsg(nodoRetencionesMsg);
		
		if (m_nodoMediosPago == null)
			m_nodoMediosPago = new MyTreeNode(nodoMediosPagoMsg, null, false);
		else
			m_nodoMediosPago.setMsg(nodoMediosPagoMsg);
		
		// Agrego los hijos del nodo raiz
		
		if (m_model.m_retenciones.size() > 0) 
			m_nodoRaiz.add(m_nodoRetenciones);
		
		m_nodoRaiz.add(m_nodoMediosPago);
		
		// Actualizo el Modelo 
		
//		if (m_arbolModel == null)
			m_arbolModel = ExtendedTreeModel.createFrom(m_nodoRaiz);
//		else
//			m_arbolModel.setRoot(m_nodoRaiz);
	}
	
	public void updateTreeModel() {
		initTreeModel();
		
		// Agrego las retenciones
		
		if (m_nodoRetenciones != null) {
			m_nodoRetenciones.removeAllChildren();
			
			for (RetencionProcessor r : m_model.m_retenciones)
				m_nodoRetenciones.add(new MyTreeNode(r.getRetencionTypeName() + ": " + numberFormat( r.getAmount() ), r, true));
		}
		
		// Agrego los medios de pago
		
		if (m_nodoMediosPago != null) {
			m_nodoMediosPago.removeAllChildren();
			
			for (MedioPago mp : m_model.m_mediosPago) 
				m_nodoMediosPago.add(new MyTreeNode(null, mp, true));
		}
		
//		pagosTree.setTreeitemRenderer(getMediosPagoTreeModel()); <- stackoverflow // m_arbolModel.nodeStructureChanged(m_nodoRaiz);
		treeUpdated();
	}
	
	public SimpleTreeModel getMediosPagoTreeModel() {
		updateTreeModel();
		return m_arbolModel;
	}
	
	
	public static class ExtendedTreeModel extends SimpleTreeModel {
		
		public ExtendedTreeModel(SimpleTreeNode root) {
			super(root);
		}
		
		public static ExtendedTreeModel createFrom(DefaultMutableTreeNode root) {
			ExtendedTreeModel model = null;
			Enumeration nodeEnum = root.children();
		    
			SimpleTreeNode stRoot = new SimpleTreeNode(root, new ArrayList());
	        while(nodeEnum.hasMoreElements()) {
	        	DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)nodeEnum.nextElement();
	        	SimpleTreeNode stNode = new SimpleTreeNode(childNode, new ArrayList());
	        	stRoot.getChildren().add(stNode);
	        	if (childNode.getChildCount() > 0) {
	        		populate(stNode, childNode);
	        	}
	        }
	        model = new ExtendedTreeModel(stRoot);
			return model;
		}
		
		private static void populate(SimpleTreeNode stNode, DefaultMutableTreeNode root) {
			Enumeration nodeEnum = root.children();
			while(nodeEnum.hasMoreElements()) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)nodeEnum.nextElement();
				SimpleTreeNode stChildNode = new SimpleTreeNode(childNode, new ArrayList());
				stNode.getChildren().add(stChildNode);
				if (childNode.getChildCount() > 0) {
					populate(stChildNode, childNode);
				}
			}
		}

		/**
		 * @param ti
		 * @param node
		 */
		public void render(Treeitem ti, Object node) {
			Treecell tc = new Treecell(Objects.toString(node));
			Treerow tr = null;
			if(ti.getTreerow()==null){
				tr = new Treerow();			
				tr.setParent(ti);
				if (isItemDraggable()) {
					tr.setDraggable("true");
				}
//				if (!onDropListners.isEmpty()) {
//					tr.setDroppable("true");
//					tr.addEventListener(Events.ON_DROP, this);
//				}
			}else{
				tr = ti.getTreerow(); 
				tr.getChildren().clear();
			}				
			tc.setParent(tr);
			ti.setValue(node);
			ti.setOpen(true);
		}
	}

	
}
