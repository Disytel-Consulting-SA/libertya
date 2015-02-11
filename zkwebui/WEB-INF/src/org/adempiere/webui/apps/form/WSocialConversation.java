package org.adempiere.webui.apps.form;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.WAttachment;
import org.adempiere.webui.window.FDialog;
import org.adempiere.webui.window.FindWindow;
import org.openXpertya.apps.form.SocialConversationModel;
import org.openXpertya.model.MField;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MSocialConversation;
import org.openXpertya.model.MTab;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.X_C_SocialConversation;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Space;


public class WSocialConversation extends Window  implements EventListener  {

	// Constantes
	protected static final int DEF_WIDTH = 1000;
	protected static final int DEF_HEIGHT = 600;
	protected static final int COLUMN_MESSAGE = 0;
	protected static final int COLUMN_ATTACH = 1;
	protected static final String ATTACH_BUTTON_TEXT = Msg.translate(Env.getCtx(), "Attachment") + " (0)";
	
	
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
	
    protected GridRenderer renderer;
    MessagesModel listModel = null;
    
    /** Creates new form WSocialConversation */
    public WSocialConversation() {
    	conversations = MSocialConversation.getNotReadConversationsForUser(Env.getAD_User_ID(Env.getCtx()));
    	initForm();
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
		lblSubject.setText(Msg.translate(Env.getCtx(), "Subject"));
		lblStarted.setText(Msg.translate(Env.getCtx(), "Started"));
		lblStartedBy.setText(Msg.translate(Env.getCtx(), "StartedBy"));
		lblTable.setText(Msg.translate(Env.getCtx(), "Document") + " (" + Msg.translate(Env.getCtx(), "AD_Table_ID") + ")");
		lblRecord.setText(Msg.translate(Env.getCtx(), "Document") + " (" + Msg.translate(Env.getCtx(), "Record") + ")");
		lblInThisConversation.setText(Msg.translate(Env.getCtx(), "InThisConversation"));
		lblMessage.setText(Msg.translate(Env.getCtx(), "WriteMessage"));
		lblNewParticipant.setText(Msg.translate(Env.getCtx(), "NewParticipant"));
		lblConversation.setText(Msg.translate(Env.getCtx(), "Conversation"));
		
		// Botones
		buttonMarkAsRead.setLabel(Msg.translate(Env.getCtx(), "MarkAsRead"));
		buttonMarkAsNotRead.setLabel(Msg.translate(Env.getCtx(), "MarkAsNotRead"));
		buttonSubscribe.setLabel(Msg.translate(Env.getCtx(), "Subscribe"));
		buttonUnsubscribe.setLabel(Msg.translate(Env.getCtx(), "Unsubscribe"));
		buttonAttach.setLabel(ATTACH_BUTTON_TEXT);
		buttonSend.setLabel(Msg.translate(Env.getCtx(), "AddMessage"));
		buttonPrevious.setLabel("<<");
		buttonNext.setLabel(">>");
		buttonGoToRecord.setLabel(Msg.translate(Env.getCtx(), "RelatedDocument"));
		buttonNewConversation.setLabel(Msg.translate(Env.getCtx(), "StartAnotherConversation"));
		buttonFindConversation.setLabel(Msg.translate(Env.getCtx(), "FindConversations"));
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
		if (currentConversation.getC_SocialConversation_ID() > 0)
			setTitle(Msg.translate(Env.getCtx(), "Conversation") + " " + currentConversation.getC_SocialConversation_ID());
		else 
			setTitle(Msg.translate(Env.getCtx(), "Conversations"));
	}
	
    protected void initComponents() {
    	try {
    		// Recuperar conversacion actual
    		loadConversation();
    		
        	// Table & model
        	tableModel = new SocialConversationModel.ConversationTableModel();
    		tableModel.reload(currentConversation);
    		
    		// Grilla web
	    	tblConversation = new Grid();
	    	listModel = new MessagesModel(tableModel, m_WindowNo);
	    	tblConversation.setModel(listModel);
	    	renderer = new GridRenderer(this);
	    	tblConversation.setRowRenderer(renderer);
	    		
	    	// Combos
	    	MLookup lookupStartedBy = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_User_ID", "AD_User", DisplayType.Search);
	    	cboStartedBy = new WSearchEditor("AD_User_ID", true, true, true, lookupStartedBy);
	    	
	    	MLookup lookupTable = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_Table_ID", "AD_Table", DisplayType.Search);
	    	cboTable = new WSearchEditor("AD_User_ID", true, true, true, lookupTable);
	    			
   			MLookup lookupTab = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_Tab_ID", "AD_Tab", DisplayType.Search);	    
	    	cboTab = new WSearchEditor("AD_Tab_ID", true, true, true, lookupTab);

   			MLookup lookupNewParticipant = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "AD_User_ID", "AD_User", DisplayType.Search);
	    	cboNewParcitipant = new WSearchEditor("AD_User_ID", false, false, true, lookupNewParticipant);
	    	
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
	    	txtMessage.getComponent().setRows(3);
	    	txtMessage.getComponent().setMultiline(true); 
	    	
	    	String defComponentWidth = "" + (int)((DEF_WIDTH - 50) / 5) + "px"; 	// 5 columnas de componentes
	    	String defComponentWidthDouble = "" + (int)((DEF_WIDTH - 50) / 2.5) + "px";
			this.setMaximizable(true);
			this.setWidth(DEF_WIDTH+"px");
			this.setHeight(DEF_HEIGHT+"px");
			this.setClosable(true);
			this.setSizable(true);
			this.setBorder("normal");
			
			buttonGoToRecord.setWidth(defComponentWidth);
			buttonPrevious.setWidth(defComponentWidth);
			buttonNext.setWidth(defComponentWidth);
			buttonFindConversation.setWidth(defComponentWidth);
			buttonNewConversation.setWidth(defComponentWidth);
			
			txtSubject.getComponent().setWidth("99%");
	    	
			txtRecordDetail.getComponent().setWidth(defComponentWidthDouble);
			txtStarted.getComponent().setWidth(defComponentWidth);
			
			txtMessage.getComponent().setWidth("99%");
			txtMessage.getComponent().setHeight("60px");
			
			buttonAttach.setWidth(defComponentWidth);
			buttonSend.setWidth(defComponentWidthDouble);
			
			buttonMarkAsRead.setWidth(defComponentWidth);
			buttonMarkAsNotRead.setWidth(defComponentWidth);
			buttonSubscribe.setWidth(defComponentWidth);
			buttonUnsubscribe.setWidth(defComponentWidth);
	    	
	    	
	    	// Eventos
	    	buttonSend.addActionListener(new EventListener() {
				public void onEvent(Event e) throws Exception {
					sendMessage();				
				}
			});
	    	buttonSubscribe.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					subscribe(true);
				}
			});
	    	buttonUnsubscribe.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					subscribe(false);
				}
			});
	    	buttonMarkAsRead.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					markAsRead(true);
				}
			});
	    	buttonMarkAsNotRead.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					markAsRead(false);
				}
			});
	    	buttonNext.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					nextConversation();
				}
			});
	    	buttonPrevious.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					previousConversation();
				}
			});
	    	buttonGoToRecord.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					goToRecord();
				}
			});
	    	buttonNewConversation.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					newConversation();
				}
			});
	    	buttonFindConversation.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					findConversation();
				}
			});
	    	buttonAttach.addActionListener(new EventListener() {
				public void onEvent(Event e) {
					attach();
				}
			});	   
	    	cboNewParcitipant.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent evt) {
					newParticipant();
				}
			});
	    	txtMessage.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent evt) {
					toggleComponents(true);	
				}
			});
	    	
    	} catch (Exception e) {
    		FDialog.error(m_WindowNo, this, e.getMessage());
    	}
    
    }
    

    protected void addComponents() {
    	
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
		toRow.setSpans("1,1,1,2");
		toRow.appendChild(cboNewParcitipant.getComponent());
		toRow.appendChild(buttonAttach);
		toRow.appendChild(new Space());
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
    	cboTable.setValue(tableID);
    	cboTab.setValue(tabID);
    	txtRecord.setValue(""+recordID);
    	txtRecordDetail.setValue(MSocialConversation.getDetailFrom(tableID, recordID, true));
    	// Cargar los valores de cabecera segun si estos ya existen o no
    	if (currentConversation.getC_SocialConversation_ID() > 0) {
    		txtSubject.setValue(currentConversation.getSubject());
    		cboStartedBy.setValue(currentConversation.getStartedBy());
    		txtStarted.setValue(currentConversation.getStarted().toString());
    		txtInThisConversation.setValue(currentConversation.getParticipantsNames());
    		lblStatus.setText(conversations.size() > 0 ? "[" + Msg.translate(Env.getCtx(), "Conversation") + " " + (conversationPos+1) + " " + Msg.translate(Env.getCtx(), "of") + " " + conversations.size() + "]" : "[" + Msg.translate(Env.getCtx(), "Conversation") + " 1 " + Msg.translate(Env.getCtx(), "of") + " 1]");
    	}
    	else {
    		txtSubject.setValue(cboTab.getValue() != null && (Integer)cboTab.getValue() > 0 ? txtRecordDetail.getValue() + " @ " + cboTab.getDisplay() : "");
	    	cboStartedBy.setValue(Env.getAD_User_ID(Env.getCtx()));
	    	txtStarted.setValue(Env.getDateTime("yyyy-MM-dd HH:mm:ss.SSS"));
	    	txtMessage.setValue("");
	    	txtInThisConversation.setValue("");
	    	lblStatus.setText("[" + Msg.translate(Env.getCtx(), "WriteAMessage") + "]");	    	
    	}
    	toggleComponents(false);
    }

    /** Habilitar o deshabilitar componentes segun situacion */
    protected void toggleComponents(boolean buttonSendOnly) {
    	
    	// Hay texto?
    	buttonSend.setEnabled(true);
//    	buttonSend.setEnabled(txtMessage.getValue() != null && txtMessage.getValue().toString() != null && txtMessage.getValue().toString().trim().length() > 0);
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
    	
    	// Actualizar mensajes
    	listModel = new MessagesModel(tableModel, m_WindowNo);
    	tblConversation.setModel(listModel);
    	
		if (currentConversation.getC_SocialConversation_ID() > 0)
			setTitle(Msg.translate(Env.getCtx(), "Conversation") + " " + currentConversation.getC_SocialConversation_ID());
    }

	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
    
	protected void sendMessage() {
		try {
			if (txtMessage.getValue() == null || txtMessage.getValue().toString() == null || txtMessage.getValue().toString().trim().length() == 0)
				return;
			SocialConversationModel.sendMessage(currentConversation, txtSubject.getValue().toString(), txtMessage.getValue().toString(), (Integer)cboTable.getValue(), Integer.parseInt(txtRecord.getValue().toString()), windowID, tabID);
			SocialConversationModel.subscribe(currentConversation, Env.getAD_User_ID(Env.getCtx()), true, false, true);
			txtInThisConversation.setValue(currentConversation.getParticipantsNames());

			// Si todo ok -> Resetear campos
    		txtMessage.setValue("");
    		tableModel.reload(currentConversation);
    		toggleComponents(false);
		} catch (Exception e) {
			FDialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	protected void subscribe(boolean actionSubscribe) {
		try {
			SocialConversationModel.subscribe(currentConversation, Env.getAD_User_ID(Env.getCtx()), actionSubscribe, true, true);
			txtInThisConversation.setValue(currentConversation.getParticipantsNames());
			toggleComponents(false);
		} catch (Exception e) {
			FDialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	protected void markAsRead(boolean asRead) {
		try {
			SocialConversationModel.markAsReadNotRead(currentConversation, Env.getAD_User_ID(Env.getCtx()), asRead);
			toggleComponents(false);
		} catch (Exception e) {
			FDialog.error(m_WindowNo, this, e.getMessage());
		}	
	}

	protected void newParticipant() {
		try {
			if (cboNewParcitipant.getNewValueOnChange() != null && (Integer)cboNewParcitipant.getNewValueOnChange() > 0) {
				// Verificar si el usuario puede ser incorporado a la conversación
				if (!SocialConversationModel.canBeSubscribed(currentConversation, (Integer)cboNewParcitipant.getNewValueOnChange())) {
					cboNewParcitipant.setValue(null);
					throw new Exception(Msg.translate(Env.getCtx(), "AddParticipantErrorUserNotAllowed"));
				}
				// Suscribirlo y actualizar los participantes
				SocialConversationModel.subscribe(currentConversation, (Integer)cboNewParcitipant.getNewValueOnChange(), true, false, false);
				txtInThisConversation.setValue(currentConversation.getParticipantsNames());
			}
		} catch (Exception e) {
			FDialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	protected void nextConversation() {
		if (conversations.size()-1 > conversationPos) {
			try {
				conversationPos++;
				loadConversation();
				loadValues(); 
				tableModel.reload(currentConversation);
				toggleComponents(false);
			}
			catch (Exception e) {
				FDialog.error(m_WindowNo, this, e.getMessage());
			}
		}
	}
	
	protected void previousConversation() {
		if (conversationPos > 0) {
			try {
				conversationPos--;
				loadConversation();
				loadValues(); 
				tableModel.reload(currentConversation);
				toggleComponents(false);
			}
			catch (Exception e) {
				FDialog.error(m_WindowNo, this, e.getMessage());
			}
		}		
	}
	
	protected void goToRecord() {
		AEnv.zoom(tableID, recordID);
		dispose();
	}
	
	protected void newConversation() {
		try {
			currentConversation = new MSocialConversation(Env.getCtx(), 0, null);
			conversations = new ArrayList<Integer>();
			conversationPos = 0;
			tableID = -1;
			tabID = -1;
			recordID = -1;
			windowID = -1;
			loadValues();
			tableModel.reload(currentConversation);
			toggleComponents(false);
			setTitle(Msg.translate(Env.getCtx(), "NewConversation"));
		} catch (Exception e) {
			FDialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	protected void attach() {
        int record_ID = currentConversation.getC_SocialConversation_ID();

        if( record_ID <= 0 )    // No Key
            return;

        WAttachment va = new WAttachment(  m_WindowNo, currentConversation.getAttachmentID(), M_Table.getTableID("C_SocialConversation"), record_ID, null );       
        buttonAttach.setLabel(SocialConversationModel.getAttachmentCountStr(ATTACH_BUTTON_TEXT, currentConversation));
	}
	
	protected void findConversation() {
		try {
			int tab = DB.getSQLValue(null, "select ad_tab_id from ad_tab where ad_componentobjectuid = 'CORE-AD_Tab-1010346' ");
	        MField[] findFields = MField.createFields( Env.getCtx(), m_WindowNo, 0, tab);
	        // Forzar el uso de campo tipo entero
	        for (MField aField : findFields) {
	        	if ("C_SocialConversation_ID".equals(aField.getColumnName()))
	        		aField.setDisplayType(DisplayType.Integer);
	        }
	        // Aplicar la busqueda de conversaciones, contemplando si la política de seguridad de conversaciones se encuentra activa o no
	        String additionalWhereClause = MSocialConversation.isEnabledConversationSecurityPolicy() ? "C_SocialConversation_ID IN (" + MSocialConversation.getConversationAccessSQLFilter() + ")" : null;
	        FindWindow find = new FindWindow( m_WindowNo, Msg.translate(Env.getCtx(), "Find"), X_C_SocialConversation.Table_ID, X_C_SocialConversation.Table_Name, additionalWhereClause, findFields, 1, tab );
	        if (find.getQuery() == null)
	        	return;
	        
	        // Recuperar las conversaciones
			conversations = MSocialConversation.getConversationsForSearch(find.getQuery().getWhereClause().trim().length() > 0 ? find.getQuery().getWhereClause() : "1=1");			
			conversationPos = 0;
			loadConversation();
			loadValues();
			tableModel.reload(currentConversation);
			toggleComponents(false);
		} catch (Exception e) {
			FDialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	
	// =======================================================================================================
	
	/**
	 * Model para la grilla de Mensajes
	 * 
	 * Esta clase delega la estructura y lógica de determinación de los registros a recuperar al 
	 * módulo original de Conversaciones, a fin de centralizar la lógica en un único lugar y
	 * simplificar ademas la complejidad de WSocialConversation.
	 */
	public static class MessagesModel extends AbstractListModel implements TableModelListener, ListModelExt {

		public TableModel model = null;
		
		public MessagesModel(TableModel model, int windowNo) {
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
	 * Encargada de renderizar la grilla de facturas a pagar
	 */
	public static class GridRenderer implements RowRenderer {

		WSocialConversation owner = null;
		Columns cols = new Columns();

		public GridRenderer(WSocialConversation owner) {
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
			owner.tblConversation.removeChild(cols);
			cols = new Columns();
			int colCount = owner.listModel.getColumnCount();
			for (int i=0; i < colCount; i++) {
				Column col = new Column();
				col.setLabel(owner.listModel.getColumnName(i));
				cols.appendChild(col);
				col.setVisible(true);
			}
			owner.tblConversation.appendChild(cols);		
			
			// Setear la fila
			Object[] _data = (Object[])arg1;
			for (int i = 0; i < colCount; i++) {
				Label aLabel = new Label(_data[i].toString() + "\u00A0");
				aLabel.setParent(arg0);
			}

		}
	}
}
