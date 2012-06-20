package org.openXpertya.pos.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.pos.model.BusinessPartner;
import org.openXpertya.pos.model.OrderProduct;
import org.openXpertya.pos.model.PriceList;
import org.openXpertya.pos.model.PriceListVersion;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.MeasurableTask;
import org.openXpertya.util.TimeStatsLogger;

public class BPartnerDialog extends JDialog {

	private final int BUTTON_PANEL_WIDTH = 160;
	private final int BUTTON_WIDTH = 155;
	private final int FIELD_WIDTH = 270;
	
	// Variables de instancia
	
	private String MSG_OK;
	private String MSG_CANCEL;
	private String MSG_CLIENT;
	private String MSG_TITLE;
	private String MSG_PRICE_LIST;
	private String MSG_PRODUCT_NOT_IN_PRICE_LIST;
	
	private PoSMainForm poS;
	private boolean mandatoryData = false; 
	private List<OrderProduct> orderLines;
	private Map<OrderProduct, BigDecimal> orderLinesPrices;
	
	// Paneles
	
	private CPanel cMainPanel;
	private CPanel cDataPanel;
	private CPanel cCustomerPanel;
	private CPanel cPriceListPanel;
	private CPanel cButtonsPanel;
	
	// Controles
	
	private CLabel cBPartnerLabel;
	private VLookup cBPartner;
	private CLabel cPriceListLabel;
	private CComboBox cPriceList;
	private CButton cOKButton;
	private CButton cCancelButton;
	
	
	private FocusTraversalPolicy compFocusTraversalPolicy = new BPartnerComponentsFocusTraversalPolicy();
	private FocusTraversalPolicy oldFocusTraversalPolicy;
	
	// Constructores
	
	public BPartnerDialog() {
		super();
		initialize();
	}
	
	public BPartnerDialog(PoSMainForm poS, boolean mandatoryData) {
		super();
		this.setPoS(poS);
		this.setMandatoryData(mandatoryData);
		initialize();
	}
	
	
	// Inicialización de componentes
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        initMsgs();
		this.setSize(new java.awt.Dimension(520,118));
        this.setResizable(false);
        this.setPreferredSize(new java.awt.Dimension(520,118));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getCMainPanel());
        this.setTitle(MSG_TITLE);
        oldFocusTraversalPolicy = this.getFocusTraversalPolicy();
        //this.setFocusTraversalPolicy(compFocusTraversalPolicy);
	}
	
	private void initMsgs() {
		MSG_TITLE = getMsg("InfoBPartner");
		MSG_CLIENT = getMsg("Customer");
		MSG_PRICE_LIST = getMsg("M_PriceList_ID");;
		MSG_OK = getMsg("OK");
		MSG_CANCEL = getMsg("Cancel");
		MSG_PRODUCT_NOT_IN_PRICE_LIST = getMsg("ProductsNotInPriceList");
	}
	
	private CPanel getCMainPanel() {
		if(cMainPanel == null){
			cMainPanel = new CPanel();
			cMainPanel.setLayout(new BorderLayout());
			cMainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			cMainPanel.add(getCDataPanel(), java.awt.BorderLayout.CENTER);
			cMainPanel.add(getCButtonsPanel(), java.awt.BorderLayout.EAST);
		}
		return cMainPanel;
	}

	private CPanel getCDataPanel() {
		if(cDataPanel == null){
			cDataPanel = new CPanel();
			cDataPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
			cDataPanel.setLayout(new BoxLayout(getCDataPanel(), BoxLayout.Y_AXIS));
			cDataPanel.add(getCCustomerPanel());
		}
		return cDataPanel;
	}

	private CPanel getCCustomerPanel() {
		if(cCustomerPanel == null){
			final int V_SPAN = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(0,0,5,0);
			cBPartnerLabel = new CLabel();
			cBPartnerLabel.setText(MSG_CLIENT);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.insets = new java.awt.Insets(0,5,5,0);
			//gridBagConstraints1.gridwidth = 5;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.insets = new java.awt.Insets(V_SPAN,0,5,0);
			cPriceListLabel = new CLabel();
			cPriceListLabel.setText(MSG_PRICE_LIST);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.insets = new java.awt.Insets(V_SPAN,5,5,0);
			//gridBagConstraints4.gridwidth = 5;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			cCustomerPanel = new CPanel();
			cCustomerPanel.setLayout(new GridBagLayout());
			cCustomerPanel.add(cBPartnerLabel,gridBagConstraints);
			cCustomerPanel.add(getCBPartner(),gridBagConstraints1);
			cCustomerPanel.add(cPriceListLabel,gridBagConstraints3);
			cCustomerPanel.add(getCPriceList(),gridBagConstraints4);
		}
		return cCustomerPanel;
	}

	private CPanel getCButtonsPanel() {
		if(cButtonsPanel == null){
			cButtonsPanel = new CPanel();
			cButtonsPanel.setPreferredSize(new java.awt.Dimension(BUTTON_PANEL_WIDTH,36));
			cButtonsPanel.add(getCOKButton());
			cButtonsPanel.add(getCCancelButton());
		}
		return cButtonsPanel;
	}

	
	private VLookup getCBPartner(){
		if(cBPartner == null){
			cBPartner = getPoS().getComponentFactory().createBPartnerSearch();
			cBPartner.setPreferredSize(new java.awt.Dimension(FIELD_WIDTH,20));
			cBPartner.setEnabled(getPoS().getModel().getOrder().getId() == 0);
			cBPartner.setMandatory(true);
			cBPartner.setValue(getPoS().getModel().getOrder().getBusinessPartner() == null?getPoS().getModel().getDefaultBPartner():getPoS().getModel().getOrder().getBusinessPartner().getId());
			cBPartner.addVetoableChangeListener(new VetoableChangeListener() {
				public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
//					String pName = event.getPropertyName();
					Object pValue = event.getNewValue();
					PriceList priceListCombo = null;
					if(pValue != null) {
						int bPartnerID = ((Integer)pValue).intValue();
						BusinessPartner customer = getPoS().getModel().getBPartner(bPartnerID);
						priceListCombo = getPriceListByID(customer.getPriceListId());
						if(priceListCombo == null){
							priceListCombo = getPriceListByID(getPoS().getModel().getPriceList().getId());
						}
					}
					else{
						priceListCombo = getPriceListByID(getPoS().getModel().getPriceList().getId());
					}
					getCPriceList().setSelectedItem(priceListCombo);
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							if(getCBPartner().getValue() != null){
								getCOKButton().requestFocus();
							}
						}
					});
				}
			});
			FocusUtils.addFocusHighlight(cBPartner);
		}
		return cBPartner;
	}
	
	private CComboBox getCPriceList(){
		if(cPriceList == null){
			cPriceList = getPoS().getComponentFactory().createPriceListCombo();
			cPriceList.setSelectedItem(getPriceListByID(getPoS().getModel().getPriceList().getId()));
			cPriceList.setMandatory(true);
			cPriceList.setPreferredSize(new java.awt.Dimension(FIELD_WIDTH,20));
			FocusUtils.addFocusHighlight(cPriceList);
		}
		return cPriceList;
	}
	
	
	private CButton getCOKButton(){
		if(cOKButton == null){
			cOKButton = new CButton();
			cOKButton.setIcon(getImageIcon("Ok16.gif"));
			cOKButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cOKButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					updateData();
				}
			});
			
			KeyUtils.setOkButtonKeys(cOKButton);
			KeyUtils.setButtonText(cOKButton, MSG_OK);
			FocusUtils.addFocusHighlight(cOKButton);
		}
		return cOKButton;
	}
	
	private CButton getCCancelButton() {
		if (cCancelButton == null) {
			cCancelButton = new CButton();
			cCancelButton.setIcon(getImageIcon("Cancel16.gif"));
			cCancelButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cCancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
			KeyUtils.setCancelButtonKeys(cCancelButton);
			KeyUtils.setButtonText(cCancelButton, MSG_CANCEL);
			FocusUtils.addFocusHighlight(cCancelButton);
		}
		return cCancelButton;
	}
	
	
	
	protected String getMsg(String name) {
		return getPoS().getMsg(name);
	}

	private ImageIcon getImageIcon(String name) {
		return getPoS().getImageIcon(name);
	}
	
	
	
	private void updateData(){
		// Verificar obligatoriedad
		if(passMandatory()){
			// Entidad Comercial - Cliente
			if(manageBPartner()){
				// Lista de Precios
				managePriceList();
				// Close
				close();
			}
		}
	}	
	
	/**
	 * Verifico que todos los productos de la orden de compra 
	 * esten dentro de la tarifa seleccionada
	 */
	private boolean allProductsInPriceList(){
		boolean all = true;
		setOrderLinesPrices(new HashMap<OrderProduct, BigDecimal>());
		setOrderLines(getPoS().getModel().getOrder().getOrderProducts());
		PriceListVersion currentVersion = getPoS().getModel().getPriceListVersion();
		BigDecimal price;
		for (int i = 0; i < getOrderLines().size() && all; i++) {
			price = getPoS().getModel().getProductPrice(getOrderLines().get(i).getProduct(),currentVersion);
			if(price != null){
				getOrderLinesPrices().put(getOrderLines().get(i), price);
			}
			else{
				all = false;
			}
		}
		return all;
	}
	
	/**
	 * Busco en el combo la lista de precios con ese id.
	 * @param priceListId id de la lista de precios
	 * @return lista de precios del combo con ese id o null en caso que no se encuentre
	 */
	private PriceList getPriceListByID(int priceListId){
		if(priceListId == 0){
			return null;
		}
		boolean find = false;
		PriceList priceList = null;
		// Itero por los ítems del combo y 
		for (int i = 0; i < getCPriceList().getItemCount() && !find;i++) {
			priceList = (PriceList)getCPriceList().getItemAt(i);
			find = priceList.getId() == priceListId;
		}
		if(!find){
			priceList = null;
		}
		return priceList;
	}
	
	
	private boolean passMandatory(){
		String errorFieldsMsg = "";
		if(getCBPartner().isMandatory() && getCBPartner().getValue() == null){
			errorFieldsMsg += MSG_CLIENT + ", ";
		}
		if(getCPriceList().isMandatory() && getCPriceList().getSelectedItem() == null){
			errorFieldsMsg += MSG_PRICE_LIST + ", ";
		}
		errorFieldsMsg = (errorFieldsMsg.endsWith(", ")?errorFieldsMsg.substring(0, errorFieldsMsg.length()-2) : errorFieldsMsg);
		if (errorFieldsMsg.length() > 0){
			getPoS().errorMsg(getMsg("FillMandatory"),errorFieldsMsg);
		}
		return errorFieldsMsg.trim().length() == 0;		
	}
	
	
	private boolean manageBPartner(){
		boolean ok = false;
		if((getCBPartner().getValue() != null)
				&& getCBPartner().isEnabled()){
			// Load BPartner
			TimeStatsLogger.beginTask(MeasurableTask.POS_LOAD_BPARTNER);
			ok = getPoS().loadBPartner((Integer)getCBPartner().getValue());
			if(!ok){
				getCBPartner().setValue(null);
				getPoS().getModel().getOrder().setBusinessPartner(null);
				PriceList priceListCombo = getPriceListByID(getPoS().getModel().getPriceList().getId());
				getCPriceList().setSelectedItem(priceListCombo);
			}
			getPoS().getModel().updateBPartner(getPoS().getWindowNo());
			TimeStatsLogger.endTask(MeasurableTask.POS_LOAD_BPARTNER);
		}
		return ok;
	}
	
	
	private void managePriceList(){
		PriceList oldPriceList = getPoS().getModel().getPriceList(); 
		PriceList priceListSelected = (PriceList)getCPriceList().getSelectedItem();
		// Si la tarifa es distinta a la que está elegida con anterioridad
		// la seteo en el contexto y realizo las validaciones necesarias
		if(oldPriceList.getId() != priceListSelected.getId()){
			// Seteo la nueva tarifa
			getPoS().getModel().updatePriceList(priceListSelected, getPoS().getWindowNo());
			// Actualizo el status bar del form principal
			getPoS().updateStatusDB();
			// Validación de que todos los productos se encuentren 
			// dentro de la versión mas reciente de la tarifa 
			if(!allProductsInPriceList()){
				getPoS().errorMsg(MSG_PRODUCT_NOT_IN_PRICE_LIST);
				// Seteo en el contexto la tarifa anterior
				getPoS().getModel().updatePriceList(oldPriceList, getPoS().getWindowNo());
				// Actualizo el status bar del form principal
				getPoS().updateStatusDB();
			}
			else{
				// Si tienen a todos los productos, los actualizo
				for (OrderProduct orderProduct : getOrderLines()) {
					orderProduct.getProduct().setStdPrice(getOrderLinesPrices().get(orderProduct));
					orderProduct.setPrice(getOrderLinesPrices().get(orderProduct));
					orderProduct.getProduct().setTaxIncludedInPrice(priceListSelected.isTaxIncluded());
				}
				getPoS().refreshOrderProductsTable();
			}
		}
	}
	
	
	private void close(){
		dispose();
	}
	
	// Getters y Setters
	
	private void setPoS(PoSMainForm poS) {
		this.poS = poS;
	}

	private PoSMainForm getPoS() {
		return poS;
	}

	private void setMandatoryData(boolean mandatoryData) {
		this.mandatoryData = mandatoryData;
	}

	private boolean isMandatoryData() {
		return mandatoryData;
	}

	private void setOrderLines(List<OrderProduct> orderLines) {
		this.orderLines = orderLines;
	}

	private List<OrderProduct> getOrderLines() {
		return orderLines;
	}

	private void setOrderLinesPrices(Map<OrderProduct, BigDecimal> orderLinesPrices) {
		this.orderLinesPrices = orderLinesPrices;
	}

	private Map<OrderProduct, BigDecimal> getOrderLinesPrices() {
		return orderLinesPrices;
	}
	
	
	
    public class BPartnerComponentsFocusTraversalPolicy extends FocusTraversalPolicy{

		@Override
		public Component getComponentAfter(Container arg0, Component arg1) {
			Component result = null;
			if(arg1.equals(getCBPartner().getM_text())){
				result = getCOKButton();
			}
			else{
				result = oldFocusTraversalPolicy.getComponentAfter(arg0, arg1);
			}
			return result;
		}

		@Override
		public Component getComponentBefore(Container arg0, Component arg1) {
			return oldFocusTraversalPolicy.getComponentBefore(arg0, arg1);
		}

		@Override
		public Component getDefaultComponent(Container arg0) {
			return getCBPartner().getM_text();
		}

		@Override
		public Component getFirstComponent(Container arg0) {
			return getCBPartner().getM_text();
		}

		@Override
		public Component getLastComponent(Container arg0) {
			// TODO Auto-generated method stub
			return null;
		}
    	
    } 
	
	
}
