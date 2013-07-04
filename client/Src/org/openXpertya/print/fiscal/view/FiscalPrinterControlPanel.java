package org.openXpertya.print.fiscal.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AInfoFiscalPrinter;
import org.openXpertya.apps.AUserAuth;
import org.openXpertya.apps.SwingWorker;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.images.ImageFactory;
import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MReference;
import org.openXpertya.print.fiscal.action.FiscalCloseAction;
import org.openXpertya.print.fiscal.action.FiscalPrinterAction;
import org.openXpertya.print.fiscal.action.OpenDrawerAction;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.UserAuthConstants;
import org.openXpertya.util.UserAuthData;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

public class FiscalPrinterControlPanel extends CPanel implements FormPanel{

	// Constantes
	
	private final AInfoFiscalPrinter infoFiscalPrinter = 
		new AInfoFiscalPrinter(
				null,
				Msg.parseTranslation(Env.getCtx(),"@FiscalPrinterControlPanel@"),
				"",
				JOptionPane.INFORMATION_MESSAGE
		);
	
	// Variables de instancia
	
	/** Frame contenedor */
	
	private FormFrame frame;
	
	/** Window no */
	
	private int windowNo;
		
	/** Interface de conexión con la impresora fiscal */
	
	private FiscalDocumentPrint iFiscalPrinter;
	
	/** Acción actual a procesar o en proceso */
	
	private FiscalPrinterAction actualAction;
	
	/** Panel de autorización de operaciones */
	private AUserAuth userAuthPanel = null;
	
	/** Autorización actual */
	private String actualAuthOperation;
	
	/** Semáforo barrera */
	private Semaphore barrerSem = new Semaphore(1);
	
	/**
	 * Booleano que determina si se debe mostrar la ventana de info de la
	 * impresora fiscal
	 */
	private boolean showInfoFiscalPrinter;
	
	// Mensajes
	
	private String MSG_PROCESSING;
	private String MSG_PLEASEWAIT;
	private String MSG_CLOSE;
	private String MSG_FISCAL_CLOSE;
	private String MSG_FISCAL_CLOSE_TYPE;
	private String MSG_FISCAL_CONTROLLER;
	private String MSG_FISCAL_PRINTER_CONTROL_PANEL;
	private String FISCAL_CLOSE_TYPES_REF_NAME;
	private String MSG_OPEN_DRAWER;
	private String MSG_OPEN;
	
	// *********************************
	// 	   Panel principal
	// *********************************
	
	/** Panel principal */
	
	private CPanel mainPanel;
	
	/** Layout principal */
	
	private BorderLayout mainLayout = new BorderLayout();
	
	// *********************************
	// 	   Panel de cierre fiscal
	// *********************************
	
	/** Panel de comandos de cierre fiscal */
	
	private CPanel fiscalClosePanel;
	
	// Componentes del panel
	
	/** Tipo de cierre fiscal */
	
	public CComboBox comboFiscalCloseTypes;
	
	/** Combo con los controladores fiscales */
	
	public VLookup comboFiscalControllers;
	
	/** Botón para ejecutar el cierre */
	
	private CButton btnFiscalClose;
	
	// Labels
	
	private CLabel lblFiscalCloseType;
	private CLabel lblFiscalControllers;
	
	// *************************************
	// Panel de Apertura de cajón de dinero
	// *************************************
		
	/** Panel de comandos de cierre fiscal */
	
	private CPanel openDrawerPanel;
	
	// Componentes del panel
	
	/** Combo con los controladores fiscales */
	
	public VLookup comboFiscalOpenDrawerControllers;
	
	/** Botón para ejecutar el cierre */
	
	private CButton btnOpenDrawer;
	
	private CLabel lblOpenDrawerControllers;
	
	// *********************************
	// 	     Panel inferior
	// *********************************

	/** Panel inferior */
	
	private CPanel bottomPanel;
	
	/** Botón Cerrar  */
	
	private CButton btnCloseForm;

	// *********************************
	
	// Constructores
	
	public FiscalPrinterControlPanel(){
		initFiscalInterface();
		initMsgs();
	}

	// Heredados
	
	@Override
	public void dispose() {
		setVisible(false);
		getFrame().dispose();
	}

	@Override
	public void init(int WindowNo, FormFrame frame) {
		setFrame(frame);
		setWindowNo(WindowNo);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Inicializa la interface de conexión con la impresora fiscal
	 */
	private void initFiscalInterface(){
		setiFiscalPrinter(new FiscalDocumentPrint());
		addFiscalPrinterListeners();
	}
	
	/**
	 * Agregar los listeners a la interface de la impresora fiscal 
	 */
	private void addFiscalPrinterListeners() {
		getiFiscalPrinter().addDocumentPrintListener(infoFiscalPrinter);
		getiFiscalPrinter().setPrinterEventListener(infoFiscalPrinter);
		
		// Referenciar la impresora desde la ventana de info.
		infoFiscalPrinter.setFiscalDocumentPrint(getiFiscalPrinter());
		infoFiscalPrinter.setCloseOnFiscalClose(true);
		infoFiscalPrinter.setCloseOnOpenDrawer(true);
	}
	
	/**
	 * Inicializo los mensajes
	 */
	private void initMsgs(){
		// Mensajes
		MSG_PROCESSING = getMsg("Processing");
		MSG_PLEASEWAIT = getMsg("PleaseWait");
		MSG_CLOSE = getMsg("Close");
		MSG_FISCAL_CLOSE = getMsg("FiscalClose");
		MSG_FISCAL_CLOSE_TYPE = getMsg("FiscalCloseType");
		MSG_FISCAL_CONTROLLER = Msg.getElement(Env.getCtx(), "C_Controlador_Fiscal_ID");
		MSG_FISCAL_PRINTER_CONTROL_PANEL = getMsg("FiscalPrinterControlPanel"); 
		MSG_OPEN_DRAWER = getMsg("OpenDrawer");
		MSG_OPEN = getMsg("Open");
		// Nombres
		FISCAL_CLOSE_TYPES_REF_NAME = "Fiscal_Close_Types";
	}
		
	private void jbInit(){
		// Inicializar los componentes principales del form
		mainLayout = new BorderLayout();
		mainPanel = new CPanel();
		
		mainPanel.setLayout(mainLayout);
		getFrame().setContentPane(mainPanel);
		getFrame().setTitle(MSG_FISCAL_PRINTER_CONTROL_PANEL);
		userAuthPanel = AUserAuth.get();
		// Crear e inicializar las áreas del panel de control  
		initAreas();
		// Agregarlas al panel principal
		addAreasToMainPanel();
	}
	
	/**
	 * Inicializo las áreas del panel de control
	 */
	private void initAreas(){
		initFiscalClosePanel();
		initOpenDrawerPanel();
		initBottomPanel();
	}
	
	/**
	 * Área de cierre fiscal
	 */
	private void initFiscalClosePanel(){
		// Inicializar el panel
		fiscalClosePanel = new CPanel();
		fiscalClosePanel.setBorder(BorderFactory.createTitledBorder(MSG_FISCAL_CLOSE));
		fiscalClosePanel.setLayout(new GridBagLayout());
		// Crear sus componentes y agregarlos al contenedor
		lblFiscalCloseType = new CLabel(MSG_FISCAL_CLOSE_TYPE);
		lblFiscalControllers = new CLabel(MSG_FISCAL_CONTROLLER);
		fiscalClosePanel.add(lblFiscalCloseType, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
		fiscalClosePanel.add(getComboFiscalCloseTypes(), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		fiscalClosePanel.add(lblFiscalControllers, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
		fiscalClosePanel.add(getComboFiscalControllers(), new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		fiscalClosePanel.add(getBtnFiscalClose(), new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.SOUTH, new Insets(15, 0, 5, 0), 0, 0));
	}
	
	/**
	 * Área de cierre fiscal
	 */
	protected void initOpenDrawerPanel(){
		// Inicializar el panel
		openDrawerPanel = new CPanel();
		lblOpenDrawerControllers = new CLabel(MSG_FISCAL_CONTROLLER);
		openDrawerPanel.setBorder(BorderFactory.createTitledBorder(MSG_OPEN_DRAWER));
		openDrawerPanel.setLayout(new GridBagLayout());
		// Crear sus componentes y agregarlos al contenedor
		openDrawerPanel.add(lblOpenDrawerControllers, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
		openDrawerPanel.add(getComboFiscalOpenDrawerControllers(), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		openDrawerPanel.add(getBtnOpenDrawer(), new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.SOUTH, new Insets(15, 0, 5, 0), 0, 0));
	}
	
	/**
	 * Área inferior
	 */
	private void initBottomPanel(){
		// inicializar el panel
		bottomPanel = new CPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(userAuthPanel.getAuthPanel());
		// Crear sus componentes y agregarlos al contenedor
		bottomPanel.add(getBtnCloseForm());
	}
	
	/**
	 * Retornar o crear en caso que no exista el combo con los 
	 * tipos de cierres fiscales. 
	 * @return
	 */
	private CComboBox getComboFiscalCloseTypes(){
		if(comboFiscalCloseTypes == null){
			comboFiscalCloseTypes = new CComboBox(MRefList.getList(MReference.getReferenceID(FISCAL_CLOSE_TYPES_REF_NAME), false));
			comboFiscalCloseTypes.setMandatory(true);
		}
		return comboFiscalCloseTypes;
	}
	
	/**
	 * Retornar o crear el lookup con los controladores fiscales
	 * @return
	 */
	private VLookup getComboFiscalControllers(){
		if(comboFiscalControllers == null){
			comboFiscalControllers = VComponentsFactory.VLookupFactory("C_Controlador_Fiscal_ID", "C_Controlador_Fiscal", getWindowNo(), DisplayType.TableDir);
			comboFiscalControllers.setMandatory(true);
		}
		return comboFiscalControllers;
	}
	
	/**
	 * Retornar o crear el lookup con los controladores fiscales
	 * @return
	 */
	private VLookup getComboFiscalOpenDrawerControllers(){
		if(comboFiscalOpenDrawerControllers == null){
			comboFiscalOpenDrawerControllers = VComponentsFactory.VLookupFactory("C_Controlador_Fiscal_ID", "C_Controlador_Fiscal", getWindowNo(), DisplayType.TableDir);
			comboFiscalOpenDrawerControllers.setMandatory(true);
		}
		return comboFiscalOpenDrawerControllers;
	}
	
	/**
	 * Retornar o crear el botón que dispara el cierre fiscal
	 * @return
	 */
	private CButton getBtnFiscalClose(){
		if(btnFiscalClose == null){
			btnFiscalClose = new CButton(MSG_FISCAL_CLOSE,ImageFactory.getImageIcon("Process24.gif"));
			btnFiscalClose.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setActualAction(new FiscalCloseAction(getiFiscalPrinter(), null, ((ValueNamePair)getComboFiscalCloseTypes().getValue()).getValue(), (Integer)getComboFiscalControllers().getValue()));
					executeAction();
				}
			});
		}
		return btnFiscalClose;
	}
	
	/**
	 * Retornar o crear el botón que dispara el apertura del cajón de dinero
	 * 
	 * @return
	 */
	private CButton getBtnOpenDrawer(){
		if(btnOpenDrawer == null){
			btnOpenDrawer = new CButton(MSG_OPEN,ImageFactory.getImageIcon("Process24.gif"));
			btnOpenDrawer.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Autorización actual
					setActualAuthOperation(UserAuthConstants.OPEN_DRAWER_UID);
					// Operación actual
					setActualAction(new OpenDrawerAction(getiFiscalPrinter(),
							null,
							(Integer) getComboFiscalOpenDrawerControllers()
									.getValue()));
					// Ejecución de acción
					executeAction();
				}
			});
		}
		return btnOpenDrawer;
	}
	
	/**
	 * Retornar o crear el botón de cierre de form
	 * @return
	 */
	private CButton getBtnCloseForm(){
		if(btnCloseForm == null){
			btnCloseForm = new CButton(MSG_CLOSE,ImageFactory.getImageIcon("End24.gif"));
			btnCloseForm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		return btnCloseForm;
	}
	
	/**
	 * Agregar las áreas del panel de control al panel principal
	 */
	private void addAreasToMainPanel(){
		mainPanel.add(fiscalClosePanel,BorderLayout.CENTER);
		mainPanel.add(openDrawerPanel,BorderLayout.EAST);
		mainPanel.add(bottomPanel,BorderLayout.SOUTH);
	}
	
	
	// Funciones del panel de control
	
	private void executeAction(){
		SwingWorker worker = new SwingWorker() {

			private String errorMsg = null;
			private String errorDesc = null;
			
			@Override
			public Object construct() {
				try{
					getBarrerSem().acquire();
				} catch(InterruptedException ie){
					ie.printStackTrace();
				}
				errorMsg = null;
				errorDesc = null;
				boolean authorized = true;
				// Autorización
				if(!Util.isEmpty(getActualAuthOperation(), true)){
					UserAuthData userAuthData = new UserAuthData();
					List<String> operations = new ArrayList<String>();
					operations.add(getActualAuthOperation());
					userAuthData.setAuthOperations(operations);
					CallResult result = userAuthPanel.validateAuthorization(userAuthData);
					authorized = !result.isError();
					if(!authorized){
						errorMsg = result.getMsg();
					}
				}
				setShowInfoFiscalPrinter(authorized);
				getBarrerSem().release();
				// Ejeución de acción
				if(authorized && !getActualAction().execute()){
					errorMsg = getActualAction().getErrorMsg();
					errorDesc = getActualAction().getErrorDesc();
				}
				return errorMsg == null;
			}

			@Override
			public void finished() {
				boolean success = (Boolean)getValue();
				if(!success){
					if(errorDesc == null)
						errorMsg(errorMsg);
					else
						errorMsg(errorMsg, errorDesc);
				}
				setActualAuthOperation(null);
				userAuthPanel.clear();
				getFrame().setBusy(false);
				mNormal();
			}
		};

		
		mWait();
		String waitMsg = MSG_PROCESSING + ", " + MSG_PLEASEWAIT;
		getFrame().setBusyMessage(waitMsg);
		getFrame().setBusyTimer(4);
		getFrame().setBusy(true);
		
		worker.start();
		
		// Mostrar ventana de la impresora fiscal
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try{
					getBarrerSem().acquire();
				} catch(InterruptedException ie){
					ie.printStackTrace();
				}
				infoFiscalPrinter.setVisible(isShowInfoFiscalPrinter());
				getBarrerSem().release();
			}
		});
	}
	
	protected String getMsg(String name) {
		return Msg.getMsg(Env.getCtx(), name);
	}
	
	protected void errorMsg(String msg) {
		errorMsg(msg,null);
	}
	
	protected void errorMsg(String msg, String subMsg) {
		ADialog.error(getWindowNo(),this,msg,subMsg);
	}
	
    private void mWait() {
    	getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    
    private void mNormal() {
    	getFrame().setCursor(Cursor.getDefaultCursor());
    }
	
	// Getters y Setters

	private void setFrame(FormFrame frame) {
		this.frame = frame;
	}

	private FormFrame getFrame() {
		return frame;
	}

	private void setWindowNo(int windowNo) {
		this.windowNo = windowNo;
	}

	private int getWindowNo() {
		return windowNo;
	}

	private void setiFiscalPrinter(FiscalDocumentPrint iFiscalPrinter) {
		this.iFiscalPrinter = iFiscalPrinter;
	}

	private FiscalDocumentPrint getiFiscalPrinter() {
		return iFiscalPrinter;
	}

	private void setActualAction(FiscalPrinterAction actualAction) {
		this.actualAction = actualAction;
	}

	private FiscalPrinterAction getActualAction() {
		return actualAction;
	}

	protected AUserAuth getUserAuthPanel() {
		return userAuthPanel;
	}

	protected void setUserAuthPanel(AUserAuth userAuthPanel) {
		this.userAuthPanel = userAuthPanel;
	}

	protected String getActualAuthOperation() {
		return actualAuthOperation;
	}

	protected void setActualAuthOperation(String actualAuthOperation) {
		this.actualAuthOperation = actualAuthOperation;
	}

	protected synchronized boolean isShowInfoFiscalPrinter() {
		return showInfoFiscalPrinter;
	}

	protected synchronized void setShowInfoFiscalPrinter(boolean showInfoFiscalPrinter) {
		this.showInfoFiscalPrinter = showInfoFiscalPrinter;
	}

	protected synchronized Semaphore getBarrerSem() {
		return barrerSem;
	}

	protected synchronized void setBarrerSem(Semaphore barrerSem) {
		this.barrerSem = barrerSem;
	}
}
