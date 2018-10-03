package org.openXpertya.pos.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTextField;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.pos.model.Promotion;
import org.openXpertya.pos.view.table.PromotionTableModel;
import org.openXpertya.pos.view.table.TableUtils;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.Util;

public class PromotionsDialog extends JDialog {

	private final int BUTTON_PANEL_WIDTH = 170;
	private final int BUTTON_WIDTH = 155;
	private final int FIELD_WIDTH = 300;
	
	private final String MOVE_PROMOTION_FORWARD = "movePromotionForward";
	private final String MOVE_PROMOTION_BACKWARD = "movePromotionBackward";
	
	// Variables de instancia
	
	private String MSG_OK;
	private String MSG_CANCEL;
	private String MSG_DELETE;
	private String MSG_ASK_DELETE;
	private String MSG_DELETE_NOT_ALLOWED;
	private String MSG_TITLE;
	private String MSG_NAME;
	private String MSG_CODE;
	private String MSG_INSERT_CODE;
	private String MSG_ALREADY_EXISTS_PAYMENT_MEDIUM;
	
	private PoSMainForm poS;
	
	// Paneles
	
	private CPanel cMainPanel;
	private CPanel cPromotionalCodePanel;
	private CPanel cPromotionsPanel;
	private CPanel cButtonsPanel;
	
	// Controles
	
	private CTextField cPromotionalCode;
	private CScrollPane cPromotionsTableScrollPane = null;
	private JTable cPromotionsTable = null;
	private TableUtils promotionsTableUtil = null;
	
	private CButton cOKButton;
	private CButton cCancelButton;
	private CButton cDeleteButton;

	public PromotionsDialog(PoSMainForm pmf) {
		setPoS(pmf);
		initialize();
	}
	
	private void initialize(){
		initMsgs();
		this.setSize(new java.awt.Dimension(650,180));
        this.setResizable(true);
        this.setPreferredSize(new java.awt.Dimension(650,180));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getCMainPanel());
        this.setTitle(MSG_TITLE);
        pack();
	}
	
	private void initMsgs() {
		MSG_TITLE = getMsg("Promotions");
		MSG_OK = getMsg("OK");
		MSG_CANCEL = getMsg("Cancel");
		MSG_DELETE = getMsg("POSDelete");
		MSG_NAME = getMsg("Name");
		MSG_CODE = getMsg("Code");
		MSG_ASK_DELETE = getMsg("PromotionDeleteAsking");
		MSG_INSERT_CODE = getMsg("InsertPromotionalCode");
		MSG_DELETE_NOT_ALLOWED = getMsg("PromotionDeletionNotAllowed");
		MSG_ALREADY_EXISTS_PAYMENT_MEDIUM = getMsg("PromotionalCodeOnPaymentMedium");
	}
	
	private CPanel getCMainPanel(){
		if(cMainPanel == null){
			cMainPanel = new CPanel();
			cMainPanel.setLayout(new BorderLayout());
			//cMainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			cMainPanel.add(getCPromotionalCodePanel(), java.awt.BorderLayout.NORTH);
			cMainPanel.add(getCPromotionsPanel(), java.awt.BorderLayout.CENTER);
			cMainPanel.add(getCButtonsPanel(), java.awt.BorderLayout.EAST);
		}
		return cMainPanel;
	}
	
	private CPanel getCPromotionalCodePanel(){
		if(cPromotionalCodePanel == null){
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints.gridy = 0;
			
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			
			cPromotionalCodePanel = new CPanel();
			cPromotionalCodePanel.setSize(new Dimension(600, 50));
			cPromotionalCodePanel.setPreferredSize(new Dimension(600, 50));
			cPromotionalCodePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
					javax.swing.BorderFactory.createTitledBorder(
							javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray, 1), MSG_INSERT_CODE,
							javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
							javax.swing.border.TitledBorder.DEFAULT_POSITION,
							new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12), new java.awt.Color(51, 51, 51)),
					javax.swing.BorderFactory.createEmptyBorder(0, 10, 5, 10)));
			cPromotionalCodePanel.setLayout(new GridBagLayout());
			//cPromotionalCodeLabel = new CLabel(MSG_INSERT_CODE);
			//cPromotionalCodePanel.add(cPromotionalCodeLabel, gridBagConstraints);
			cPromotionalCodePanel.add(getCPromotionalCode(), gridBagConstraints);
		}
		return cPromotionalCodePanel;
	}
	
	private CTextField getCPromotionalCode(){
		if(cPromotionalCode == null){
			cPromotionalCode = new CTextField();
			cPromotionalCode.setMinimumSize(new java.awt.Dimension(FIELD_WIDTH,20));
			cPromotionalCode.setPreferredSize(new java.awt.Dimension(FIELD_WIDTH,20));
			cPromotionalCode.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addPromotionalCode((String)cPromotionalCode.getValue());
				}
			});
		}
		return cPromotionalCode;
	}
	
	private CPanel getCPromotionsPanel(){
		if(cPromotionsPanel == null){
			cPromotionsPanel = new CPanel();
			cPromotionsPanel.setLayout(new FlowLayout());
			cPromotionsPanel.add(getCPromotionTableScrollPane());
		}
		return cPromotionsPanel;
	}
	
	private CScrollPane getCPromotionTableScrollPane() {
		if (cPromotionsTableScrollPane == null) {
			cPromotionsTableScrollPane = new CScrollPane();
			//cPromotionsTableScrollPane.setPreferredSize(new java.awt.Dimension(300,55));
			cPromotionsTableScrollPane.setViewportView(getCPromotionsTable());
		}
		return cPromotionsTableScrollPane;
	}
	
	private JTable getCPromotionsTable() {
		if (cPromotionsTable == null) {
			cPromotionsTable = new MiniTable();
			cPromotionsTable.setRowSelectionAllowed(true);
			cPromotionsTable.setFocusable(false);
			
			// Creo el Modelo de la tabla.
			PromotionTableModel promoTableModel = new PromotionTableModel();
			// Se vincula la lista de promociones con el table model
			// para que se muestren en la tabla.
			promoTableModel.setPromotions(getPoS().getModel().getPromotions());
			promoTableModel.addColumName(MSG_NAME);
			promoTableModel.addColumName(MSG_CODE);
			cPromotionsTable.setModel(promoTableModel);

			setActionEnabled(MOVE_PROMOTION_FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), true);
			setActionEnabled(MOVE_PROMOTION_BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), true);
			
			getRootPane().getActionMap().put(MOVE_PROMOTION_FORWARD, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					moveTableSelection(cPromotionsTable, true);
				}
			});

			getRootPane().getActionMap().put(MOVE_PROMOTION_BACKWARD, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					moveTableSelection(cPromotionsTable, false);
				}
			});
			
			// Funcionalidades extras.
			ArrayList minWidth = new ArrayList();
			minWidth.add(0);
			minWidth.add(0);
			
			// Se wrapea la tabla con funcionalidad extra.
			setPromotionsTableUtil(new TableUtils(minWidth,cPromotionsTable));
			getPromotionsTableUtil().refreshTable();
			getPromotionsTableUtil().removeSorting();
		}
		return cPromotionsTable;
	}

	
	private CPanel getCButtonsPanel(){
		if(cButtonsPanel == null){
			cButtonsPanel = new CPanel();
			cButtonsPanel.setPreferredSize(new java.awt.Dimension(BUTTON_PANEL_WIDTH,36));
			cButtonsPanel.add(getCDeleteButton());
			cButtonsPanel.add(getCOKButton());
			//cButtonsPanel.add(getCCancelButton());	
		}
		return cButtonsPanel;
	}
	
	// TAMOS SEGUROS?
	private CButton getCOKButton(){
		if(cOKButton == null){
			cOKButton = new CButton();
			cOKButton.setIcon(getImageIcon("Ok16.gif"));
			cOKButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cOKButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Actualiza descuentos y sale de la ventana
					getPoS().getModel().getOrder().updateDiscounts();
					exit();
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
					exit();
				}
			});
			KeyUtils.setCancelButtonKeys(cCancelButton);
			KeyUtils.setButtonText(cCancelButton, MSG_CANCEL);
			FocusUtils.addFocusHighlight(cCancelButton);
		}
		return cCancelButton;
	}
	
	private CButton getCDeleteButton() {
		if (cDeleteButton == null) {
			cDeleteButton = new CButton();
			cDeleteButton.setIcon(getImageIcon("Delete16.gif"));
			cDeleteButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cDeleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removePromotionalCode(getPromotionsTableUtil().getSelection());
				}
			});
			KeyUtils.setRemoveButtonKeys(cDeleteButton);
			KeyUtils.setButtonText(cDeleteButton, MSG_DELETE);
			FocusUtils.addFocusHighlight(cDeleteButton);
		}
		return cDeleteButton;
	}
	
	/**
	 * Agrega un código promocional para aplicar, previamente realiza
	 * validaciones
	 * 
	 * @param code código promocional
	 */
	protected void addPromotionalCode(String code){
		if(Util.isEmpty(code, true))
			return;

		// Validar que no exista un medio de cobro ya cargado
		if(getPoS().getModel().getOrder().getPayments().size() > 0){
			errorMsg(MSG_ALREADY_EXISTS_PAYMENT_MEDIUM);
			return;
		}
		// Validar que exista una promo vigente para ese código
		CallResult result = getPoS().getModel().isPromotionalCodeValid(code);
		if(result.isError()){
			errorMsg(result.getMsg());
			return;
		}
		// Agregar el código promocional y reaplicar los descuentos/recargos
		getPoS().getModel().addPromotionalCode(code);
		
		// Refrescar la tabla del pedido
		getPoS().refreshOrderProductsTable();
		
		// Resetear el campo de código y refrescar la tabla
		getCPromotionalCode().setValue(null);
		((PromotionTableModel) getCPromotionsTable().getModel()).setPromotions(getPoS().getModel().getPromotions());
		getPromotionsTableUtil().refreshTable();
		// Salir de la ventana??
	}
	
	protected void removePromotionalCode(Object selectedPromotion){
		if(selectedPromotion == null){
			return;
		}
		Promotion promo = (Promotion)selectedPromotion;
		if(!Util.isEmpty(promo.getCode())){
			if(getPoS().askMsg(MSG_ASK_DELETE)){
				getPoS().getModel().removePromotionalCode(promo.getCode());
				// Refrescar la tabla del pedido
				getPoS().refreshOrderProductsTable();
				((PromotionTableModel) getCPromotionsTable().getModel())
						.setPromotions(getPoS().getModel().getPromotions());
				getPromotionsTableUtil().refreshTable();
			}
		}
		else{
			// Por lo pronto no se puede eliminar una promo que no sea de tipo
			// código promocional
			errorMsg(MSG_DELETE_NOT_ALLOWED);
		}
	}
	
	private void setActionEnabled(String action, KeyStroke ks, boolean enabled) {
		String kAction = (enabled?action:"none");
        
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            	ks, kAction);
	}
	
	private void moveTableSelection(JTable table, boolean forward) {
		if (table.getRowCount() == 0) {
			return;
		}
		int srow = table.getSelectedRow();
		if (srow == -1) {
			srow = 0;
		} else {
			srow = forward ? srow+1 : srow-1;
			if (srow < 0) {
				srow = table.getRowCount() - 1;
			} else if (srow >= table.getRowCount()) {
				srow = 0;
			}
		}
		table.setRowSelectionInterval(srow, srow);
	}
	
	private void exit(){
		setVisible(false);
		getPoS().getCProductCodeText().requestFocus();
	}	
	
	protected String getMsg(String name) {
		return getPoS().getMsg(name);
	}
	
	private ImageIcon getImageIcon(String name) {
		return getPoS().getImageIcon(name);
	}
	
	private void errorMsg(String msg) {
		errorMsg(msg,null);
	}
	
	private void errorMsg(String msg, String subMsg) {
		getPoS().errorMsg(msg,subMsg);
		
	}
	
	public PoSMainForm getPoS() {
		return poS;
	}

	public void setPoS(PoSMainForm poS) {
		this.poS = poS;
	}

	private TableUtils getPromotionsTableUtil() {
		return promotionsTableUtil;
	}

	private void setPromotionsTableUtil(TableUtils promotionsTableUtil) {
		this.promotionsTableUtil = promotionsTableUtil;
	}
}
