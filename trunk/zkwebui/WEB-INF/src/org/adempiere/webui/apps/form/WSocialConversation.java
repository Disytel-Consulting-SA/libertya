package org.adempiere.webui.apps.form;

import java.util.ArrayList;

import javax.swing.UIManager;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.window.FDialog;
import org.openXpertya.apps.form.SocialConversationModel;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MSocialConversation;
import org.openXpertya.model.MTab;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Space;


public class WSocialConversation extends Window  implements EventListener  {

	// Constantes
	protected static final int DEF_WIDTH = 1000;
	protected static final int DEF_HEIGHT = 600;
	protected static final int COLUMN_MESSAGE = 0;
	protected static final int COLUMN_ATTACH = 1;
	protected static final String ATTACH_BUTTON_TEXT = "Archivos adjuntos (0)";
	
	
	// Miembros de la clase
    protected int m_WindowNo = 0;
//    protected FormFrame m_frame;
    protected MTab m_tab;
    protected int tableID = -1;
    protected int recordID = -1;
    protected int windowID = -1;
    protected int tabID = -1;
    protected SocialConversationModel.ConversationTableModel tableModel = null;
    protected MSocialConversation currentConversation = null;
    protected ArrayList<Integer> conversations = new ArrayList<Integer>(); 
    protected int conversationPos = 0;
	
    // Labels
    protected Label lblSubject = new Label();
    protected Label lblStarted = new Label();
    protected Label lblStartedBy = new Label();
    protected Label lblTable = new Label();
    protected Label lblRecord = new Label();
    protected Label lblMessage = new Label();
    protected Label lblNewParticipant = new Label();
    protected Label lblInThisConversation = new Label();
    protected Label lblConversation = new Label();
    protected Label lblStatus = new Label();
    
    // Campos    
    protected WStringEditor txtSubject = new WStringEditor();
    protected WStringEditor txtStarted = new WStringEditor();
    protected WSearchEditor cboStartedBy = null;
    protected WSearchEditor cboTable = null;
    protected WSearchEditor cboTab = null;
    protected WStringEditor txtRecord = new WStringEditor();
    protected WStringEditor txtRecordDetail = new WStringEditor();
    protected WStringEditor txtMessage = new WStringEditor();
    protected WSearchEditor cboNewParcitipant = null;
    protected WStringEditor txtInThisConversation = new WStringEditor();

	protected Button    buttonMarkAsRead = new Button();
    protected Button    buttonMarkAsNotRead = new Button();
    protected Button    buttonSubscribe = new Button();
    protected Button    buttonUnsubscribe = new Button();
    protected Button    buttonAttach = new Button();
    protected Button    buttonSend = new Button();
	protected Grid		tblConversation = null;	// Se implementa bajo una tabla para soportar eventuales ampliaciones a futuro
    protected Button    buttonPrevious = new Button();
    protected Button    buttonNext = new Button();
    protected Button    buttonGoToRecord = new Button();
    protected Button    buttonNewConversation = new Button();
    protected Button    buttonFindConversation = new Button();
	
    /** Creates new form WSocialConversation */
    public WSocialConversation() {
    	conversations = MSocialConversation.getNotReadConversationsForUser(Env.getAD_User_ID(Env.getCtx()));
    }
    
    public WSocialConversation( int windowNo, MTab mTab ) {
    	m_WindowNo = windowNo;
    	m_tab = mTab;
    	tableID = m_tab.getAD_Table_ID();
    	recordID = m_tab.getRecord_ID();
    	windowID = m_tab.getAD_Window_ID();
    	tabID = m_tab.getAD_Tab_ID();
    	
    	initForm();
    }
    

	protected void initForm() {
        
        // Primera actividad a realizar
        initComponents();
        
        // Textos a mostrar
        initTexts();
        
        // Incorporar los componentes al frame
        addComponents();
        
        // Valores a cargar/setear
        loadValues();
		
	}
	
	
	protected void initTexts() {
		// Labels
		lblSubject.setText("Asunto");
		lblStarted.setText("Iniciado");
		lblStartedBy.setText("Iniciado Por");
		lblTable.setText("Documento (Tabla)");
		lblRecord.setText("Documento (Registro)");
		lblInThisConversation.setText("Participantes de esta conversacion");
		lblMessage.setText("Escribir mensaje");
		lblNewParticipant.setText("Agregar participante");
		lblConversation.setText("Conversación");
		
		// Botones
		buttonMarkAsRead.setLabel("Marcar conversación leida");
		buttonMarkAsNotRead.setLabel("Marcar conversación no leida");
		buttonSubscribe.setLabel("Suscribirse a la conversación");
		buttonUnsubscribe.setLabel("Desuscribirse de la conversación");
		buttonAttach.setLabel("Archivos adjuntos (0)");
		buttonSend.setLabel("Agregar mensaje");
		buttonPrevious.setLabel("<<");
		buttonNext.setLabel(">>");
		buttonGoToRecord.setLabel("Documento relacionado");
		buttonNewConversation.setLabel("Iniciar otra conversación");
		buttonFindConversation.setLabel("Buscar conversaciones");
	}
	
	protected void loadConversation() {
		// Multi o simple conversation?
		if (conversations.size() > 0) {
			int conversationID = conversations.get(conversationPos);
			currentConversation = new MSocialConversation(Env.getCtx(), conversationID, null);
			tableID = currentConversation.getAD_Table_ID();
			recordID = currentConversation.getRecordID();
			windowID = currentConversation.getAD_Window_ID();
			tabID = currentConversation.getAD_Tab_ID();
		} else {
			// Conversacion actual a partir de una tabla/registro
			currentConversation = MSocialConversation.getForTableAndRecord(Env.getCtx(), tableID, recordID, windowID, tabID, null);
		}		
	}
	
    protected void initComponents() {
    	try {
    		// Recuperar conversacion actual
    		loadConversation();
    		
        	// Table & model
        	tableModel = new SocialConversationModel.ConversationTableModel();
    		tableModel.reload(currentConversation);

	    	tblConversation = new Grid();
//	    	tblConversation.setModel(tableModel);
//	    	tblConversation.getColumnModel().getColumn(COLUMN_MESSAGE).setPreferredWidth(DEF_WIDTH);
	    	//tblConversation.setRowHeight(20);
//	    	tblConversation.setShowHorizontalLines(false);
//	    	tblConversation.setShowVerticalLines(false);
//	    	tblConversation.setFocusable(false);
//	    	tblConversation.setRowSelectionAllowed(false);
	    	
	    	// Botones
//	    	buttonGoToRecord.setBackground(UIManager.getColor("Button.shadow"));
//	    	buttonMarkAsRead.setBackground(UIManager.getColor("Button.shadow"));
//	    	buttonMarkAsNotRead.setBackground(UIManager.getColor("Button.shadow"));
//	    	buttonSubscribe.setBackground(UIManager.getColor("Button.shadow"));
//	    	buttonUnsubscribe.setBackground(UIManager.getColor("Button.shadow"));
//	    	buttonSend.setBackground(new Color(250,120,50));	
	    	
	    	// Combos
	    	MLookup lookupStartedBy = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_User_ID", "AD_User", DisplayType.Search);
	    	cboStartedBy = new WSearchEditor("AD_User_ID", true, true, true, lookupStartedBy);
	    	
	    	MLookup lookupTable = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_Table_ID", "AD_Table", DisplayType.Search);
	    	cboTable = new WSearchEditor("AD_User_ID", true, true, true, lookupTable);
	    			
   			MLookup lookupTab = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_Tab_ID", "AD_Tab", DisplayType.Search);	    
	    	cboTab = new WSearchEditor("AD_Tab_ID", true, true, true, lookupTab);

   			MLookup lookupNewParticipant = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_User_ID", "AD_User", DisplayType.Search);
	    	cboNewParcitipant = new WSearchEditor("AD_User_ID", false, false, true, lookupNewParticipant);
//	    	cboNewParcitipant.setBackground(UIManager.getColor("TextField.background"));
	    	
	    	// Campos solo lectura
	    	cboTable.setReadWrite(false);
	    	cboTab.setReadWrite(false);
	    	txtRecord.setReadWrite(false);
	    	txtRecordDetail.setReadWrite(false);
	    	txtStarted.setReadWrite(false);
	    	cboStartedBy.setReadWrite(false);
	    	txtInThisConversation.setReadWrite(false);
	    	txtInThisConversation.setBackground(UIManager.getColor("TextField.background"));
	    	
	    	// Definiciones visuales adicionales
//	    	txtMessage.setAlignmentY(Component.TOP_ALIGNMENT);
//	    	txtMessage.setPreferredSize(new Dimension(DEF_WIDTH, 80));
//	    	txtMessage.setLineWrap(true);
//	    	txtMessage.setWrapStyleWord(true);
	    	
	    	// Eventos
//	    	buttonSend.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					sendMessage();
//				}
//			});
//	    	buttonSubscribe.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					subscribe(true);
//				}
//			});
//	    	buttonUnsubscribe.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					subscribe(false);
//				}
//			});
//	    	buttonMarkAsRead.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					markAsRead(true);
//				}
//			});
//	    	buttonMarkAsNotRead.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					markAsRead(false);
//				}
//			});
//	    	buttonNext.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					nextConversation();
//				}
//			});
//	    	buttonPrevious.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					previousConversation();
//				}
//			});
//	    	buttonGoToRecord.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					goToRecord();
//				}
//			});
//	    	buttonNewConversation.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					newConversation();
//				}
//			});
//	    	buttonFindConversation.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					findConversation();
//				}
//			});
//	    	buttonAttach.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					attach();
//				}
//			});	   
//	    	cboNewParcitipant.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) { 
//					newParticipant();
//				}
//			});
//	    	txtMessage.addKeyListener(new KeyListener() {
//				public void keyTyped(KeyEvent e) {
//				}
//				public void keyReleased(KeyEvent e) {
//					toggleComponents(true);
//				}
//				public void keyPressed(KeyEvent e) {
//				}
//			});
	    	
    	} catch (Exception e) {
    		FDialog.error(m_WindowNo, this, e.getMessage());
    	}
    
    }
    

    protected void addComponents() {
    	String defComponentWidth = "" + (int)((DEF_WIDTH - 50) / 5) + "px"; 	// 5 columnas de componentes
    	String defComponentWidthDouble = "" + (int)((DEF_WIDTH - 50) / 2.5) + "px";
		this.setMaximizable(true);
		this.setWidth(DEF_WIDTH+"px");
		this.setHeight(DEF_HEIGHT+"px");
		this.setTitle("Conversaciones");
		this.setClosable(true);
		this.setSizable(true);
		this.setBorder("normal");
    	
    	// Definición de layout de la ventana
    	Borderlayout mainPanel = new Borderlayout();
    	mainPanel.setHeight("100%");
    	mainPanel.setWidth("100%");
    	this.appendChild(mainPanel);

    	// Filas de componentes visuales
    	Grid gridPanel = GridFactory.newGridLayout();
		gridPanel.setWidth("100%");
    	Rows rows = gridPanel.newRows();
		
       	// Navigate panel
		Row navigateRow = rows.newRow();
		buttonGoToRecord.setWidth(defComponentWidth);
		buttonPrevious.setWidth(defComponentWidth);
		buttonNext.setWidth(defComponentWidth);
		buttonFindConversation.setWidth(defComponentWidth);
		buttonNewConversation.setWidth(defComponentWidth);
		navigateRow.appendChild(buttonGoToRecord);
		navigateRow.appendChild(buttonPrevious);
		navigateRow.appendChild(buttonNext);
		navigateRow.appendChild(buttonFindConversation);
		navigateRow.appendChild(buttonNewConversation);

    	// Asunto header
		Row subjectHeadRow = rows.newRow();
		subjectHeadRow.appendChild(lblSubject);
		subjectHeadRow.appendChild(new Space());
		subjectHeadRow.appendChild(new Space());
		subjectHeadRow.appendChild(new Space());
		subjectHeadRow.appendChild(lblStatus.rightAlign());
		
    	// Asunto
		Row subjectRow = rows.newRow();
		subjectRow.setSpans("5");
		txtSubject.getComponent().setWidth("99%");
		subjectRow.appendChild(txtSubject.getComponent());
		
    	// Detalle Head
		Row detailHeadRow = rows.newRow();
		detailHeadRow.setSpans("1,2");
		detailHeadRow.appendChild(lblTable);
		detailHeadRow.appendChild(lblRecord);
		detailHeadRow.appendChild(lblStarted);
		detailHeadRow.appendChild(lblStartedBy);
		
    	// Detalle
		Row detailRow = rows.newRow();
		detailRow.setSpans("1,2");
		txtRecordDetail.getComponent().setWidth(defComponentWidthDouble);
		txtStarted.getComponent().setWidth(defComponentWidth);
		detailRow.appendChild(cboTab.getComponent());
		detailRow.appendChild(txtRecordDetail.getComponent());
		detailRow.appendChild(txtStarted.getComponent());
		detailRow.appendChild(cboStartedBy.getComponent());
		
    	// Message Head panel
		Row messageHeadRow = rows.newRow();
		messageHeadRow.appendChild(lblMessage);
		
    	// Message panel 
		Row messageRow = rows.newRow();
		messageRow.setSpans("5");
		txtMessage.getComponent().setWidth("99%");
		txtMessage.getComponent().setHeight("60px");
		messageRow.appendChild(txtMessage.getComponent());
		
    	// InThisConv Head panel 
		Row inThisConvHeadRow = rows.newRow();
		inThisConvHeadRow.appendChild(lblInThisConversation);
		
    	// InThisConv panel
		Row inThisConvRow = rows.newRow();
		inThisConvRow.setSpans("5");
		txtInThisConversation.getComponent().setWidth("99%");
		inThisConvRow.appendChild(txtInThisConversation.getComponent());
		
    	// To head panel
		Row toHeadRow = rows.newRow();
		toHeadRow.appendChild(lblNewParticipant);
		
    	// To panel
		Row toRow = rows.newRow();
		buttonAttach.setWidth(defComponentWidthDouble);
		buttonSend.setWidth(defComponentWidthDouble);
		toRow.setSpans("1,2,2");
		toRow.appendChild(cboNewParcitipant.getComponent());
		toRow.appendChild(buttonAttach);
		toRow.appendChild(buttonSend);
		
    	// Conversation head panel
		Row conversationHeadRow = rows.newRow();
		conversationHeadRow.appendChild(lblConversation);
		
    	// Conversation panel
		Row conversationRow = rows.newRow();
		conversationRow.setSpans("5");
		tblConversation.setWidth("99%");
		tblConversation.setHeight("200px");
		conversationRow.appendChild(tblConversation);
		
    	// Actions panel
		Row actionsRow = rows.newRow();
		buttonMarkAsRead.setWidth(defComponentWidth);
		buttonMarkAsNotRead.setWidth(defComponentWidth);
		buttonSubscribe.setWidth(defComponentWidth);
		buttonUnsubscribe.setWidth(defComponentWidth);
		actionsRow.appendChild(buttonMarkAsRead);
		actionsRow.appendChild(buttonMarkAsNotRead);
		actionsRow.appendChild(new Space());
		actionsRow.appendChild(buttonSubscribe);
		actionsRow.appendChild(buttonUnsubscribe);
		
    	Center center = new Center();
    	center.appendChild(gridPanel);
    	
    	mainPanel.appendChild(center);
    	
    }
    
    
    /** Cargar los valores de los componentes */
    protected void loadValues() {
    	// Cargar los valores de cabecera segun si estos ya existen o no
    	if (currentConversation.getC_SocialConversation_ID() > 0) {
    		txtSubject.setValue(currentConversation.getSubject());
    		cboStartedBy.setValue(currentConversation.getStartedBy());
    		txtStarted.setValue(currentConversation.getStarted().toString());
    		txtInThisConversation.setValue(currentConversation.getParticipantsNames());
    		lblStatus.setText(conversations.size() > 0 ? "[Conversación " + (conversationPos+1) + " de " + conversations.size() + "]" : "[Conversación 1 de 1]");
    	}
    	else {
    		txtSubject.setValue("");
	    	cboStartedBy.setValue(Env.getAD_User_ID(Env.getCtx()));
	    	txtStarted.setValue(Env.getDateTime("yyyy-MM-dd HH:mm:ss.SSS"));
	    	txtMessage.setValue("");
	    	txtInThisConversation.setValue("");
	    	lblStatus.setText("[Escriba un mensaje...]");	    	
    	}
    	cboTable.setValue(tableID);
    	cboTab.setValue(tabID);
    	txtRecord.setValue(""+recordID);
    	txtRecordDetail.setValue(MSocialConversation.getDetailFrom(tableID, recordID, true));
    	toggleComponents(false);
    }

    /** Habilitar o deshabilitar componentes segun situacion */
    protected void toggleComponents(boolean buttonSendOnly) {
    	
    	// Hay texto?
    	buttonSend.setEnabled(txtMessage.getValue() != null && txtMessage.getValue().toString() != null && txtMessage.getValue().toString().trim().length() > 0);
    	if (buttonSendOnly)
    		return;
    	
    	// Habilitar o no botones dependiendo si la conversación existe o no (y si la leyo o no , o si esta suscripto o no)
    	boolean conversationActive = currentConversation.getC_SocialConversation_ID() > 0;
    	buttonSubscribe.setEnabled(conversationActive && !currentConversation.isSuscribed(Env.getAD_User_ID(Env.getCtx())));
    	buttonUnsubscribe.setEnabled(conversationActive && currentConversation.isSuscribed(Env.getAD_User_ID(Env.getCtx())));
    	buttonMarkAsRead.setEnabled(conversationActive && currentConversation.isSuscribed(Env.getAD_User_ID(Env.getCtx())) && !currentConversation.isRead(Env.getAD_User_ID(Env.getCtx())));
    	buttonMarkAsNotRead.setEnabled(conversationActive && currentConversation.isSuscribed(Env.getAD_User_ID(Env.getCtx())) && currentConversation.isRead(Env.getAD_User_ID(Env.getCtx())));
    	
    	// Botones de navegacion multi conversacion
    	buttonNext.setEnabled(conversations.size() > 0);
    	buttonPrevious.setEnabled(conversations.size() > 0);
    	buttonNext.setEnabled(conversations.size() > 0 && conversations.size()-1 > conversationPos);
    	buttonPrevious.setEnabled(conversations.size() > 0 && conversationPos > 0);
    
    	// Otros componentes
    	buttonGoToRecord.setEnabled(currentConversation.getAD_Table_ID() > 0 && currentConversation.getRecordID() > 0);
    	buttonAttach.setEnabled(currentConversation.getC_SocialConversation_ID() > 0);
    	buttonAttach.setLabel(SocialConversationModel.getAttachmentCountStr(ATTACH_BUTTON_TEXT, currentConversation));
    	cboNewParcitipant.setVisible(currentConversation.getC_SocialConversation_ID() > 0);
    	
    	// Campos
    	// cboTable.setVisible(tableID > 0);  Comentado: no es agregado al panel directamente. Se opto por mostrar el nombre de pestaña
    	cboTab.setVisible(tableID > 0);
    	// txtRecord.setVisible(recordID > 0); Comentado: no es agregado al panel directamente.  Se opto por mostrar un detalle del registro (mas intuitivo que el recordID) 
    	txtRecordDetail.setVisible(recordID > 0);
    }

	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
    
    
}
