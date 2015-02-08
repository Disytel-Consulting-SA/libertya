package org.openXpertya.apps.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.Attachment;
import org.openXpertya.apps.form.SocialConversationModel.ConversationTableModel;
import org.openXpertya.apps.search.Find;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.MField;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MSocialConversation;
import org.openXpertya.model.MTab;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.X_C_SocialConversation;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.ValueNamePair;

public class VSocialConversation extends CPanel  implements FormPanel {

	// Constantes
	protected static final int DEF_WIDTH = 1000;
	protected static final int DEF_HEIGHT = 600;
	protected static final int COLUMN_MESSAGE = 0;
	protected static final int COLUMN_ATTACH = 1;
	protected static final String ATTACH_BUTTON_TEXT = "Archivos adjuntos (0)";
	
	
	// Miembros de la clase
    protected int m_WindowNo = 0;
    protected FormFrame m_frame;
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
    protected JLabel lblSubject = new JLabel();
    protected JLabel lblStarted = new JLabel();
    protected JLabel lblStartedBy = new JLabel();
    protected JLabel lblTable = new JLabel();
    protected JLabel lblRecord = new JLabel();
    protected JLabel lblMessage = new JLabel();
    protected JLabel lblNewParticipant = new JLabel();
    protected JLabel lblInThisConversation = new JLabel();
    protected JLabel lblConversation = new JLabel();
    protected JLabel lblStatus = new JLabel();
    
    // Campos    
    protected JTextField txtSubject = new JTextField();
    protected JTextField txtStarted = new JTextField();
    protected VLookup 	 cboStartedBy = null;
    protected VLookup 	 cboTable = null;
    protected VLookup    cboTab = null;
    protected JTextField txtRecord = new JTextField();
    protected JTextField txtRecordDetail = new JTextField();
    protected JTextArea  txtMessage = new JTextArea();
    protected VLookup    cboNewParcitipant = null;
    protected JTextField txtInThisConversation = new JTextField();
    protected CButton    buttonMarkAsRead = new CButton();
    protected CButton    buttonMarkAsNotRead = new CButton();
    protected CButton    buttonSubscribe = new CButton();
    protected CButton    buttonUnsubscribe = new CButton();
    protected CButton    buttonAttach = new CButton();
    protected CButton    buttonSend = new CButton();
	protected JTable	 tblConversation = null;	// Se implementa bajo una tabla para soportar eventuales ampliaciones a futuro
    protected CButton    buttonPrevious = new CButton();
    protected CButton    buttonNext = new CButton();
    protected CButton    buttonGoToRecord = new CButton();
    protected CButton    buttonNewConversation = new CButton();
    protected CButton    buttonFindConversation = new CButton();
    
    protected ConversationTableModel listModel;
    
    public VSocialConversation() {
    	conversations = MSocialConversation.getNotReadConversationsForUser(Env.getAD_User_ID(Env.getCtx()));	
    }
    
    public VSocialConversation( MTab mTab ) {
    	m_tab = mTab;
    	tableID = m_tab.getAD_Table_ID();
    	recordID = m_tab.getRecord_ID();
    	windowID = m_tab.getAD_Window_ID();
    	tabID = m_tab.getAD_Tab_ID();
    }
    
	public void init(int WindowNo, FormFrame frame) {

        m_WindowNo = WindowNo;
        m_frame    = frame;
        m_frame.setPreferredSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
        
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
		buttonMarkAsRead.setText("Marcar conversación leida");
		buttonMarkAsNotRead.setText("Marcar conversación no leida");
		buttonSubscribe.setText("Suscribirse a la conversación");
		buttonUnsubscribe.setText("Desuscribirse de la conversación");
		buttonAttach.setText("Archivos adjuntos (0)");
		buttonSend.setText("Agregar mensaje");
		buttonPrevious.setText("<<");
		buttonNext.setText(">>");
		buttonGoToRecord.setText("Documento relacionado");
		buttonNewConversation.setText("Iniciar otra conversación");
		buttonFindConversation.setText("Buscar conversaciones");
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
			m_frame.setTitle("Conversación " + currentConversation.getC_SocialConversation_ID());
	}
	

    protected void initComponents() {
    	try {
    		// Recuperar conversacion actual
    		loadConversation();
    		
        	// Table & model
        	tableModel = new SocialConversationModel.ConversationTableModel();
    		tableModel.reload(currentConversation);

	    	tblConversation = new JTable(tableModel);
	    	tblConversation.getColumnModel().getColumn(COLUMN_MESSAGE).setPreferredWidth(DEF_WIDTH);
	    	//tblConversation.setRowHeight(20);
	    	tblConversation.setShowHorizontalLines(false);
	    	tblConversation.setShowVerticalLines(false);
	    	tblConversation.setFocusable(false);
	    	tblConversation.setRowSelectionAllowed(false);
	    	
	    	// Botones
	    	buttonGoToRecord.setBackground(UIManager.getColor("Button.shadow"));
	    	buttonMarkAsRead.setBackground(UIManager.getColor("Button.shadow"));
	    	buttonMarkAsNotRead.setBackground(UIManager.getColor("Button.shadow"));
	    	buttonSubscribe.setBackground(UIManager.getColor("Button.shadow"));
	    	buttonUnsubscribe.setBackground(UIManager.getColor("Button.shadow"));
	    	buttonSend.setBackground(new Color(250,120,50));	
	    	
	    	// Combos
	    	cboStartedBy = VComponentsFactory.VLookupFactory("AD_User_ID", "AD_User", m_WindowNo, DisplayType.Search);
	    	cboTable = VComponentsFactory.VLookupFactory("AD_Table_ID", "AD_Table", m_WindowNo, DisplayType.Search);
	    	cboTab = VComponentsFactory.VLookupFactory("AD_Tab_ID", "AD_Tab", m_WindowNo, DisplayType.Search);
	    	cboNewParcitipant = VComponentsFactory.VLookupFactory("AD_User_ID", "AD_User", m_WindowNo, DisplayType.Search);
	    	cboNewParcitipant.setBackground(UIManager.getColor("TextField.background"));
	    	
	    	// Campos solo lectura
	    	cboTable.setReadWrite(false);
	    	cboTab.setReadWrite(false);
	    	txtRecord.setEditable(false);
	    	txtRecordDetail.setEditable(false);
	    	txtStarted.setEditable(false);
	    	cboStartedBy.setReadWrite(false);
	    	txtInThisConversation.setEditable(false);
	    	txtInThisConversation.setBackground(UIManager.getColor("TextField.background"));
	    	
	    	// Definiciones visuales adicionales
	    	txtMessage.setAlignmentY(Component.TOP_ALIGNMENT);
	    	txtMessage.setPreferredSize(new Dimension(DEF_WIDTH, 80));
	    	txtMessage.setLineWrap(true);
	    	txtMessage.setWrapStyleWord(true);
	    	
	    	// Eventos
	    	buttonSend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendMessage();
				}
			});
	    	buttonSubscribe.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					subscribe(true);
				}
			});
	    	buttonUnsubscribe.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					subscribe(false);
				}
			});
	    	buttonMarkAsRead.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					markAsRead(true);
				}
			});
	    	buttonMarkAsNotRead.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					markAsRead(false);
				}
			});
	    	buttonNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					nextConversation();
				}
			});
	    	buttonPrevious.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					previousConversation();
				}
			});
	    	buttonGoToRecord.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					goToRecord();
				}
			});
	    	buttonNewConversation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newConversation();
				}
			});
	    	buttonFindConversation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					findConversation();
				}
			});
	    	buttonAttach.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					attach();
				}
			});	   
	    	cboNewParcitipant.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					newParticipant();
				}
			});
	    	txtMessage.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {
				}
				public void keyReleased(KeyEvent e) {
					toggleComponents(true);
				}
				public void keyPressed(KeyEvent e) {
				}
			});
	    	
    	} catch (Exception e) {
    		ADialog.error(m_WindowNo, this, e.getMessage());
    	}
    
    }
    
    protected void addComponents() {
    	
    	// Contenedor principal
    	BoxLayout layout = new BoxLayout(m_frame.getContentPane(), BoxLayout.Y_AXIS);
    	m_frame.getContentPane().setLayout(layout);

       	// Navigate panel 
    	GridLayout navigateLayout = new GridLayout(0,5);
    	JPanel navigatePanel = new JPanel();
    	navigatePanel.setLayout(navigateLayout);
    	navigatePanel.add(buttonGoToRecord);
    	navigatePanel.add(buttonPrevious);
    	navigatePanel.add(buttonNext);
    	navigatePanel.add(buttonFindConversation);
    	navigatePanel.add(buttonNewConversation);
    	
    	// Asunto header
    	GridLayout subjectHeadLayout = new GridLayout(0,2);
    	JPanel subjectHeadPanel = new JPanel();
    	subjectHeadPanel.setLayout(subjectHeadLayout);
    	lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);
    	subjectHeadPanel.add(lblSubject);
    	subjectHeadPanel.add(lblStatus);

    	// Asunto
    	GridLayout subjectLayout = new GridLayout(0,1);
    	JPanel subjectPanel = new JPanel();
    	subjectPanel.setLayout(subjectLayout);
    	subjectPanel.add(txtSubject);
    	
    	// Detalle Head
    	GridLayout detailHeadLayout = new GridLayout(0, 4);
    	JPanel detailHeadPanel = new JPanel();
    	detailHeadPanel.setLayout(detailHeadLayout);
    	detailHeadPanel.add(lblTable);
    	detailHeadPanel.add(lblRecord);
    	detailHeadPanel.add(lblStarted);
    	detailHeadPanel.add(lblStartedBy);
    	
    	// Detalle
    	GridLayout detailLayout = new GridLayout(0,4);
    	JPanel detailPanel = new JPanel();
    	detailPanel.setLayout(detailLayout);
    	// detailPanel.add(cboTable);   Comentado: Es mas intuitivo para el usuario el nombre de la pestaña que la de la tabla
    	detailPanel.add(cboTab);
    	// detailPanel.add(txtRecord); 	Comentado: Es mas intuitivo para el usuario el detalle que el número de registro
    	detailPanel.add(txtRecordDetail);
    	detailPanel.add(txtStarted);
    	detailPanel.add(cboStartedBy);

    	// Message Head panel 
    	GridLayout messageHeadLayout = new GridLayout(0,1);
    	JPanel messageHeadPanel = new JPanel();
    	messageHeadPanel.setLayout(messageHeadLayout);
    	messageHeadPanel.add(lblMessage);

    	// Message panel 
    	GridLayout messageLayout = new GridLayout(0,1);
    	JPanel messagePanel = new JPanel();
    	messagePanel.setLayout(messageLayout);
    	messagePanel.add(new JScrollPane(txtMessage));
    	
    	// InThisConv Head panel 
    	GridLayout inThisConvHeadLayout = new GridLayout(0,1);
    	JPanel inThisConvHeadPanel = new JPanel();
    	inThisConvHeadPanel.setLayout(inThisConvHeadLayout);
    	inThisConvHeadPanel.add(lblInThisConversation);
	
    	// InThisConv panel 
    	GridLayout inThisConvLayout = new GridLayout(0,1);
    	JPanel inThisConvPanel = new JPanel();
    	inThisConvPanel.setLayout(inThisConvLayout);
    	inThisConvPanel.add(txtInThisConversation); 

    	// To head panel 
    	GridLayout toHeadLayout = new GridLayout(0,3);
    	JPanel toHeadPanel = new JPanel();
    	toHeadPanel.setLayout(toHeadLayout);
    	toHeadPanel.add(lblNewParticipant);
    	toHeadPanel.add(new JLabel(""));
    	toHeadPanel.add(new JLabel(""));
    	
    	// To panel 
    	GridLayout toLayout = new GridLayout(0,3);
    	JPanel toPanel = new JPanel();
    	toPanel.setLayout(toLayout);
    	toPanel.add(cboNewParcitipant);
    	toPanel.add(buttonAttach);
    	toPanel.add(buttonSend);

    	// Conversation header panel
    	GridLayout conversationHeadLayout = new GridLayout(0,1);
    	JPanel conversationHeadPanel = new JPanel();
    	conversationHeadPanel.setLayout(conversationHeadLayout);
    	conversationHeadPanel.add(lblConversation);
    	
    	// Conversation panel
    	GridLayout conversationLayout = new GridLayout(0,1);
    	JPanel conversationPanel = new JPanel();
    	conversationPanel.setLayout(conversationLayout);
    	conversationPanel.add(new JScrollPane(tblConversation));
    	
    	// Actions panel 
    	GridLayout actionsLayout = new GridLayout(0,4);
    	JPanel actionsPanel = new JPanel();
    	actionsPanel.setLayout(actionsLayout);
    	actionsPanel.add(buttonMarkAsRead);
    	actionsPanel.add(buttonMarkAsNotRead);
    	actionsPanel.add(buttonSubscribe);
    	actionsPanel.add(buttonUnsubscribe);
    	
    	m_frame.getContentPane().add(navigatePanel);
    	m_frame.getContentPane().add(subjectHeadPanel);
    	m_frame.getContentPane().add(subjectPanel);
    	m_frame.getContentPane().add(detailHeadPanel);
    	m_frame.getContentPane().add(detailPanel);
    	m_frame.getContentPane().add(messageHeadPanel);
    	m_frame.getContentPane().add(messagePanel);
    	m_frame.getContentPane().add(inThisConvHeadPanel);
    	m_frame.getContentPane().add(inThisConvPanel);
    	m_frame.getContentPane().add(toHeadPanel);
    	m_frame.getContentPane().add(toPanel);
    	m_frame.getContentPane().add(conversationHeadPanel);
    	m_frame.getContentPane().add(conversationPanel);
    	m_frame.getContentPane().add(actionsPanel);
    }
    
    /** Cargar los valores de los componentes */
    protected void loadValues() {
    	// Cargar los valores de cabecera segun si estos ya existen o no
    	if (currentConversation.getC_SocialConversation_ID() > 0) {
    		txtSubject.setText(currentConversation.getSubject());
    		cboStartedBy.setValue(currentConversation.getStartedBy());
    		txtStarted.setText(currentConversation.getStarted().toString());
    		txtInThisConversation.setText(currentConversation.getParticipantsNames());
    		lblStatus.setText(conversations.size() > 0 ? "[Conversación " + (conversationPos+1) + " de " + conversations.size() + "]" : "[Conversación 1 de 1]");
    	}
    	else {
    		txtSubject.setText("");
	    	cboStartedBy.setValue(Env.getAD_User_ID(Env.getCtx()));
	    	txtStarted.setText(Env.getDateTime("yyyy-MM-dd HH:mm:ss.SSS"));
	    	txtMessage.setText("");
	    	txtInThisConversation.setText("");
	    	lblStatus.setText("[Escriba un mensaje...]");	    	
    	}
    	cboTable.setValue(tableID);
    	cboTab.setValue(tabID);
    	txtRecord.setText(""+recordID);
    	txtRecordDetail.setText(MSocialConversation.getDetailFrom(tableID, recordID, true));
    	toggleComponents(false);
    }

    /** Habilitar o deshabilitar componentes segun situacion */
    protected void toggleComponents(boolean buttonSendOnly) {
    	
    	// Hay texto?
    	buttonSend.setEnabled(txtMessage.getText() != null && txtMessage.getText().trim().length() > 0);
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
    	buttonAttach.setText(SocialConversationModel.getAttachmentCountStr(ATTACH_BUTTON_TEXT, currentConversation));
    	cboNewParcitipant.setVisible(currentConversation.getC_SocialConversation_ID() > 0);
    	
    	// Campos
    	// cboTable.setVisible(tableID > 0);  Comentado: no es agregado al panel directamente. Se opto por mostrar el nombre de pestaña
    	cboTab.setVisible(tableID > 0);
    	// txtRecord.setVisible(recordID > 0); Comentado: no es agregado al panel directamente.  Se opto por mostrar un detalle del registro (mas intuitivo que el recordID) 
    	txtRecordDetail.setVisible(recordID > 0);
    	
		if (currentConversation.getC_SocialConversation_ID() > 0)
			m_frame.setTitle("Conversación " + currentConversation.getC_SocialConversation_ID());

    }
    
    
	@Override
	public void dispose() {
        if( m_frame != null ) {
            m_frame.dispose();
        }
        m_frame = null;
	}
	
	protected void sendMessage() {
		try { 			
			SocialConversationModel.sendMessage(currentConversation, txtSubject.getText(), txtMessage.getText(), (Integer)cboTable.getValue(), Integer.parseInt(txtRecord.getText()), windowID, tabID);
			SocialConversationModel.subscribe(currentConversation, Env.getAD_User_ID(Env.getCtx()), true, false, true);
			txtInThisConversation.setText(currentConversation.getParticipantsNames());

			// Si todo ok -> Resetear campos
    		txtMessage.setText("");
    		tableModel.reload(currentConversation);
    		toggleComponents(false);
		} catch (Exception e) {
			ADialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	protected void subscribe(boolean actionSubscribe) {
		try {
			SocialConversationModel.subscribe(currentConversation, Env.getAD_User_ID(Env.getCtx()), actionSubscribe, true, true);
			txtInThisConversation.setText(currentConversation.getParticipantsNames());
			toggleComponents(false);
		} catch (Exception e) {
			ADialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	protected void markAsRead(boolean asRead) {
		try {
			SocialConversationModel.markAsReadNotRead(currentConversation, Env.getAD_User_ID(Env.getCtx()), asRead);
			toggleComponents(false);
		} catch (Exception e) {
			ADialog.error(m_WindowNo, this, e.getMessage());
		}	
	}

	protected void newParticipant() {
		try {
			if (cboNewParcitipant.getValue() != null && (Integer)cboNewParcitipant.getValue() > 0) {
				SocialConversationModel.subscribe(currentConversation, (Integer)cboNewParcitipant.getValue(), true, false, false);
				txtInThisConversation.setText(currentConversation.getParticipantsNames());
			}
		} catch (Exception e) {
			ADialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	protected void nextConversation() {
		if (conversations.size()-1 > conversationPos) {
			try {
				conversationPos++;
				loadConversation();
				loadValues(); 
				tableModel.reload(currentConversation); 
			}
			catch (Exception e) {
				ADialog.error(m_WindowNo, this, e.getMessage());
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
			}
			catch (Exception e) {
				ADialog.error(m_WindowNo, this, e.getMessage());
			}
		}		
	}
	
	protected void goToRecord() {
		AWindow frame = new AWindow();
		MQuery gotoQuery = new MQuery();
		
		M_Table refTable = new M_Table(Env.getCtx(), tableID, null);
		gotoQuery.addRestriction(refTable.getKeyColumns().get(0), MQuery.EQUAL, recordID);
		
		if( !frame.initWindow( windowID, gotoQuery )) {
			ValueNamePair pp  = CLogger.retrieveError();
	        String        msg = (pp == null) ? "AccessTableNoView" : pp.getValue();
	        ADialog.error( windowID, this, msg, (pp == null) ? ""  : pp.getName());
		} else {
			AEnv.showCenterScreen( frame );
		}
		frame = null;
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
			m_frame.setTitle("Nueva conversación...");
		} catch (Exception e) {
			ADialog.error(m_WindowNo, this, e.getMessage());
		}
	}
	
	protected void attach() {
        int record_ID = currentConversation.getC_SocialConversation_ID();

        if( record_ID <= 0 )    // No Key
            return;

        Attachment va = new Attachment( Env.getFrame( this ), m_WindowNo, currentConversation.getAttachmentID(), M_Table.getTableID("C_SocialConversation"), record_ID, null );       
        buttonAttach.setText(SocialConversationModel.getAttachmentCountStr(ATTACH_BUTTON_TEXT, currentConversation));
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
	        Find find = new Find( Env.getFrame( this ),m_WindowNo, "Buscar", X_C_SocialConversation.Table_ID, X_C_SocialConversation.Table_Name, null, findFields, 1 );
	        if (find.getQuery() == null)
	        	return;
			conversations = MSocialConversation.getConversationsForSearch(find.getQuery().getWhereClause().trim().length() > 0 ? find.getQuery().getWhereClause() : "1=1");
			
			conversationPos = 0;
			loadConversation();
			loadValues();
			tableModel.reload(currentConversation);
		} catch (Exception e) {
			ADialog.error(m_WindowNo, this, e.getMessage());
		}
	}

}
