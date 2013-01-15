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

package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MPayment;
import org.openXpertya.process.DocAction;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

/**
 * Descripción de Clase
 * 
 * 
 * @version 2.1, 02.07.07
 * @author Equipo de Desarrollo de openXpertya
 */

public class VAllocation extends CPanel implements FormPanel, ActionListener,
		TableModelListener, VetoableChangeListener {

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param WindowNo
	 * @param frame
	 */

			
	public void init(int WindowNo, FormFrame frame) {
		m_WindowNo = WindowNo;
		m_frame = frame;
		Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", "Y"); // defaults
																	// to no
		m_C_Currency_ID = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID"); // default

		//

		log.info("Currency=" + m_C_Currency_ID);

		try {
			dynInit();
			jbInit();
			calculate();
			frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
			frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		} catch (Exception e) {
			log.log(Level.SEVERE, "VAllocation.init", e);
		}
	} // init

	/** Descripción de Campos */

	private int m_WindowNo = 0;

	/** Descripción de Campos */

	private FormFrame m_frame;

	/** Descripción de Campos */

	private static CLogger log = CLogger.getCLogger(VAllocation.class);

	/** Descripción de Campos */

	private boolean m_calculating = false;

	/** Descripción de Campos */

	private int m_C_Currency_ID = 0;

	/** Descripción de Campos */

	private int m_C_BPartner_ID = 0;

	/** Descripción de Campos */

	private int m_noCredits = 0;

	/** Descripción de Campos */

	private int m_noDebits = 0;

	// Index changed if multi-currency

	/** Descripción de Campos */

	private int i_payment = 7;

	//

	/** Descripción de Campos */

	private int i_open = 6;

	/** Descripción de Campos */

	private int i_discount = 7;

	/** Descripción de Campos */

	private int i_writeOff = 8;

	/** Descripción de Campos */

	private int i_applied = 9;

	// private int i_multiplier = 10;
	//

	/** Descripción de Campos */

	private CPanel mainPanel = new CPanel();

	/** Descripción de Campos */

	private BorderLayout mainLayout = new BorderLayout();

	/** Descripción de Campos */

	private CPanel parameterPanel = new CPanel();

	/** Descripción de Campos */

	private CPanel allocationPanel = new CPanel();

	/** Descripción de Campos */

	private GridBagLayout parameterLayout = new GridBagLayout();

	/** Descripción de Campos */

	private JLabel bpartnerLabel = new JLabel();

	/** Descripción de Campos */

	private VLookup bpartnerSearch = null;

	/** Descripción de Campos */

	private MiniTable creditTable = new MiniTable();

	/** Descripción de Campos */

	private MiniTable debitTable = new MiniTable();

	/** Descripción de Campos */

	private JSplitPane infoPanel = new JSplitPane();

	/** Descripción de Campos */

	private CPanel debitPanel = new CPanel();

	/** Descripción de Campos */

	private CPanel creditPanel = new CPanel();

	/** Descripción de Campos */

	private JLabel debitLabel = new JLabel();

	/** Descripción de Campos */

	private JLabel creditLabel = new JLabel();

	/** Descripción de Campos */

	private BorderLayout debitLayout = new BorderLayout(); // @jve:decl-index=0:

	/** Descripción de Campos */

	private BorderLayout creditLayout = new BorderLayout();

	/** Descripción de Campos */

	private JLabel debitInfo = new JLabel();

	/** Descripción de Campos */

	private JLabel creditInfo = new JLabel();

	/** Descripción de Campos */

	private JScrollPane debitScrollPane = new JScrollPane();

	/** Descripción de Campos */

	private JScrollPane creditScrollPane = new JScrollPane();

	/** Descripción de Campos */

	private GridBagLayout allocationLayout = new GridBagLayout();

	/** Descripción de Campos */

	private JLabel differenceLabel = new JLabel();

	/** Descripción de Campos */

	private JTextField differenceField = new JTextField();

	/** Descripción de Campos */

	private JButton allocateButton = new JButton();

	/** Descripción de Campos */

	private JLabel currencyLabel = new JLabel();

	/** Descripción de Campos */

	private VLookup currencyPick = null;

	/** Descripción de Campos */

	private JCheckBox multiCurrency = new JCheckBox();

	/** Descripción de Campos */

	private JLabel allocCurrencyLabel = new JLabel();

	/** Descripción de Campos */

	private StatusBar statusBar = new StatusBar();

	/** Descripción de Campos */

	private JLabel dateLabel = new JLabel();

	/** Descripción de Campos */

	private VDate dateField = new VDate();

	/** Descripción de Campos */

	private JCheckBox autoWriteOff = new JCheckBox();

	private JComboBox debitPick = new JComboBox();

	private JComboBox creditPick = new JComboBox();

	private JRadioButton optionPurchase = new JRadioButton(Msg.translate(Env
			.getCtx(), "IsSOTrx"));

	private JRadioButton optionSales = new JRadioButton(Msg.getMsg(Env
			.getCtx(), "PurchaseTransaction"));// "Transacción de venta");

	private ButtonGroup groupOperation = new ButtonGroup();

	private int i_entityType;

	private boolean isSOTrx = true;

	private ArrayList creditTypes;

	private ArrayList debitTypes;

	private boolean validCreditTotalAmt = true;
	
	private final String T_PAYMENT = "P";

	private final String T_INVOICE = "I";
	
	private Map<Integer,String> creditDocTypes = new HashMap<Integer,String>();
	
	private Map<Integer,String> debitDocTypes = new HashMap<Integer,String>();
	
	

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @throws Exception
	 */

	private void jbInit() throws Exception {
		CompiereColor.setBackground(this);

		//
		
		
		mainPanel.setLayout(mainLayout);
		dateLabel.setText(Msg.getMsg(Env.getCtx(), "Date"));
		/*autoWriteOff.setSelected(false);
		autoWriteOff.setText(Msg.getMsg(Env.getCtx(), "AutoWriteOff", true));
		autoWriteOff.setToolTipText(Msg.getMsg(Env.getCtx(), "AutoWriteOff",
				false));*/

		//

		parameterPanel.setLayout(parameterLayout);
		allocationPanel.setLayout(allocationLayout);
		bpartnerLabel.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		debitLabel.setRequestFocusEnabled(false);
		debitLabel.setText(" " + Msg.translate(Env.getCtx(), "Debits"));
		creditLabel.setRequestFocusEnabled(false);
		creditLabel.setText(" " + Msg.translate(Env.getCtx(), "Credits"));
		debitPanel.setLayout(debitLayout);
		creditPanel.setLayout(creditLayout);
		creditInfo.setHorizontalAlignment(SwingConstants.RIGHT);
		creditInfo.setHorizontalTextPosition(SwingConstants.RIGHT);
		creditInfo.setText(".");
		debitInfo.setHorizontalAlignment(SwingConstants.RIGHT);
		debitInfo.setHorizontalTextPosition(SwingConstants.RIGHT);
		debitInfo.setText(".");
		differenceLabel.setText(Msg.getMsg(Env.getCtx(), "Difference"));
		differenceField.setBackground(CompierePLAF
				.getFieldBackground_Inactive());
		differenceField.setEditable(false);
		differenceField.setText("0");
		differenceField.setColumns(8);
		differenceField.setHorizontalAlignment(SwingConstants.RIGHT);
		allocateButton.setText(Msg.getMsg(Env.getCtx(), "Process"));
		allocateButton.addActionListener(this);
		currencyLabel.setText(Msg.translate(Env.getCtx(), "C_Currency_ID"));
		/*multiCurrency.setText(Msg.getMsg(Env.getCtx(), "MultiCurrency"));
		multiCurrency.addActionListener(this);*/
		allocCurrencyLabel.setText(".");
		creditScrollPane.setPreferredSize(new Dimension(200, 200));
		debitScrollPane.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(parameterPanel, BorderLayout.NORTH);
		parameterPanel.add(bpartnerLabel, new GridBagConstraints(0, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		parameterPanel.add(bpartnerSearch, new GridBagConstraints(1, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));
		parameterPanel.add(dateLabel, new GridBagConstraints(2, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		parameterPanel.add(dateField, new GridBagConstraints(3, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));
		parameterPanel.add(currencyLabel, new GridBagConstraints(0, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		parameterPanel.add(currencyPick, new GridBagConstraints(1, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));
		/*parameterPanel.add(multiCurrency, new GridBagConstraints(3, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));*/

		CPanel panel = new CPanel();
		panel.setLayout(new BorderLayout());
		optionSales.addActionListener(this);
		optionSales.setSelected(true);
		optionPurchase.addActionListener(this);
		groupOperation.add(optionSales);
		groupOperation.add(optionPurchase);
		panel.add(optionSales, BorderLayout.NORTH);
		panel.add(optionPurchase, BorderLayout.SOUTH);
		parameterPanel.add(panel, new GridBagConstraints(4, 0, 1, 2,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 10, 5, 5), 0, 0));

		mainPanel.add(allocationPanel, BorderLayout.SOUTH);
		allocationPanel.add(differenceLabel, new GridBagConstraints(0, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 0), 0, 0));
		allocationPanel.add(differenceField, new GridBagConstraints(2, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));
		allocationPanel.add(allocateButton, new GridBagConstraints(5, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));
		allocationPanel.add(allocCurrencyLabel, new GridBagConstraints(1, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		/*allocationPanel.add(autoWriteOff, new GridBagConstraints(4, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));*/
		String text = Msg.getMsg(Env.getCtx(), "DocumentType");
		//debitPick.setPreferredSize(new Dimension(200, 20));
		//creditPick.setPreferredSize(new Dimension(200, 20));
		CPanel debitNorthPanel = new CPanel(new BorderLayout());
		debitNorthPanel.add(debitLabel, BorderLayout.WEST);
		// debitNorthPanel.add(new JLabel(text), BorderLayout.CENTER);
		//debitNorthPanel.add(debitPick, BorderLayout.EAST);
		debitNorthPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		debitPanel.add(debitNorthPanel, BorderLayout.NORTH);
		debitPanel.add(debitInfo, BorderLayout.SOUTH);
		debitPanel.add(debitScrollPane, BorderLayout.CENTER);
		debitScrollPane.getViewport().add(debitTable, null);
		CPanel creditNorthPanel = new CPanel(new BorderLayout());
		creditNorthPanel.add(creditLabel, BorderLayout.WEST);
		creditNorthPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		// creditNorthPanel.add(new JLabel(text), BorderLayout.CENTER);
		//creditNorthPanel.add(creditPick, BorderLayout.EAST);
		creditPanel.add(creditNorthPanel, BorderLayout.NORTH);
		creditPanel.add(creditInfo, BorderLayout.SOUTH);
		creditPanel.add(creditScrollPane, BorderLayout.CENTER);
		creditScrollPane.getViewport().add(creditTable, null);
		

		//

		mainPanel.add(infoPanel, BorderLayout.CENTER);
		infoPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		//infoPanel.setBorder(BorderFactory.createEtchedBorder());
		
		infoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),BorderFactory.createEmptyBorder(5,5,5,5)));
		infoPanel.setTopComponent(debitPanel);
		infoPanel.setBottomComponent(creditPanel);
		infoPanel.add(debitPanel, JSplitPane.TOP);
		infoPanel.add(creditPanel, JSplitPane.BOTTOM);
		infoPanel.setContinuousLayout(true);
		infoPanel.setPreferredSize(new Dimension(670, 250));
		infoPanel.setDividerLocation(110);
		
		mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
		
	} // jbInit

	/**
	 * Descripción de Método
	 * 
	 */

	public void dispose() {
		if (m_frame != null) {
			m_frame.dispose();
		}

		m_frame = null;
	} // dispose

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @throws Exception
	 */

	private void dynInit() throws Exception {

		// Currency

		int AD_Column_ID = 3505; // C_Invoice.C_Currency_ID
		MLookup lookupCur = MLookupFactory.get(Env.getCtx(), m_WindowNo, 0,
				AD_Column_ID, DisplayType.TableDir);

		currencyPick = new VLookup("C_Currency_ID", true, false, true,
				lookupCur);
		currencyPick.setValue(new Integer(m_C_Currency_ID));
		currencyPick.addVetoableChangeListener(this);

		// BPartner

		AD_Column_ID = 3499; // C_Invoice.C_BPartner_ID

		MLookup lookupBP = MLookupFactory.get(Env.getCtx(), m_WindowNo, 0,
				AD_Column_ID, DisplayType.Search);

		bpartnerSearch = new VLookup("C_BPartner_ID", true, false, true,
				lookupBP);
		bpartnerSearch.addVetoableChangeListener(this);

		// Document types

		/*loadDocuments();
		creditPick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (creditPick.getItemCount() > 0) {
					fillCreditTable();
				}
				m_frame.setCursor(Cursor.getDefaultCursor());
			}
		});

		debitPick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (debitPick.getItemCount() > 0) {
					fillDebitTable();
				}
				m_frame.setCursor(Cursor.getDefaultCursor());
			}
		});*/
		
		// Translation

		statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "AllocateStatus"));
		statusBar.setStatusDB("");

		// Date set to Login Date
		
		dateField.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		dateField.addVetoableChangeListener(this);
		dateField.setMandatory(true);
	} // dynInit

	private void fillCreditDocumentTypes() {
		int signo_isSOTrx = getCreditSignoIsSOTrx();
		Collection creditDocTypes = getDocTypes(getCreditDocTypes());

		creditPick.removeAllItems();
		for (Iterator i = creditDocTypes.iterator(); i.hasNext();) {
			creditPick.addItem(i.next());
		}
	}

	private void fillDebitDocumentTypes() {
		int signo_isSOTrx = getDebitSignoIsSOTrx();
		Collection debitDocTypes = getDocTypes(getDebitDocTypes());

		debitPick.removeAllItems();
		for (Iterator i = debitDocTypes.iterator(); i.hasNext();) {
			debitPick.addItem(i.next());
		}
	}
	
	private int getCreditSignoIsSOTrx() {
		int signo_isSOTrx = (isSOTrx?1:-1);
		return signo_isSOTrx;
	}
	
	private int getDebitSignoIsSOTrx() {
		int signo_isSOTrx = (isSOTrx?-1:1);
		return signo_isSOTrx;
	}
	
	private Collection getDocTypes(Map<Integer,String> docTypesMap) {
		ArrayList docTypes = new ArrayList();
		
		KeyNamePair allDocTypes = new KeyNamePair(0, Msg.translate(Env.getCtx(),"All"));
		docTypes.add(allDocTypes);
		
		for (Integer docTypeId : docTypesMap.keySet()) {
			String docTypeName = docTypesMap.get(docTypeId);
			KeyNamePair docType = new KeyNamePair(docTypeId, docTypeName);
			docTypes.add(docType);
		} 

		return docTypes;
	}

	/**
	 * Descripción de Método
	 * 
	 */

	private void loadBPartner() {
		log.config("BPartner=" + m_C_BPartner_ID + ", Cur=" + m_C_Currency_ID);

		// Need to have both values
		if ((m_C_BPartner_ID == 0) || (m_C_Currency_ID == 0)) {
			return;
		}
		
		setColumnIndexes();

		MAllocationLine.setIsAllocated(Env.getCtx(), m_C_BPartner_ID, null);
		MAllocationLine.setIsPaid(Env.getCtx(), m_C_BPartner_ID, null);

		// Debits
		fillDebitTable();

		// Credits
		fillCreditTable();
		
		// Se cargan los combos de documentos
		loadDocuments();

		// Calculate Totals
		calculate();

	} // loadBPartner

	private int getSelectedCreditDocTypeId() {
		/*
		 * Modificación por incongruencias en los doc types
		 * 
		 * Código anterior
		 * ----------------------------------------
		 * KeyNamePair docType = (KeyNamePair)creditPick.getSelectedItem();
		 * return docType.getKey();
		 */
		return 0;
	}
	
	private int getSelectedDebitDocTypeId() {
		/*
		 * Modificación por incongruencias en los doc types
		 * 
		 * Código anterior
		 * ----------------------------------------
		 * KeyNamePair docType = (KeyNamePair)debitPick.getSelectedItem();
		 * return docType.getKey();
		 */
		
		return 0;
	}
 
	
	private void fillCreditTable() {
		Vector data = null;
		Collection creditParameters = new ArrayList();
		StringBuffer sql = 
			getDocumentsQuery(getCreditSignoIsSOTrx(), 
							  getSelectedCreditDocTypeId(), 
							  creditParameters);
		try {
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			setParameters(pstmt, creditParameters);
			
			ResultSet rs = pstmt.executeQuery();
			creditTypes = new ArrayList();
			data = getCreditData(rs);
			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			log.log(Level.SEVERE, "(invoice)", e);
		}

		// Remove previous listeners

		creditTable.getModel().removeTableModelListener(this);

		// Header Info

		Vector columnNames = getCreditColumnNames();

		// Set Model

		DefaultTableModel modelI = new DefaultTableModel(data, columnNames);

		modelI.addTableModelListener(this);
		creditTable.setModel(modelI);

		setCreditColumClasses();
		// Table UI
		creditTable.autoSize();

	}

	private void fillDebitTable() {
		Collection debitParameters = new ArrayList();
		Vector data = null;
		StringBuffer sql = 
			getDocumentsQuery(getDebitSignoIsSOTrx(),
							  getSelectedDebitDocTypeId(),
							  debitParameters);
		
		try {
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			setParameters(pstmt, debitParameters);
			ResultSet rs = pstmt.executeQuery();
			debitTypes = new ArrayList();
			data = getDebitData(rs);
			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			log.log(Level.SEVERE, "(payment)", e);
		}
		// Remove previous listeners
		debitTable.getModel().removeTableModelListener(this);
		// Header Info
		Vector columnNames = getDebitColumnNames();
		// Set Model
		DefaultTableModel modelP = new DefaultTableModel(data, columnNames);
		modelP.addTableModelListener(this);
		debitTable.setModel(modelP);
		// Set column classes
		setDebitTableColumnClasses();

		// Table UI
		debitTable.autoSize();

	}

	private void setColumnIndexes() {
		i_open = multiCurrency.isSelected() ? 7 : 5;
		i_discount = multiCurrency.isSelected() ? 8 : 6;
		i_writeOff = multiCurrency.isSelected() ? 9 : 7;
		i_applied = multiCurrency.isSelected() ? 10 : 8;

		i_entityType = multiCurrency.isSelected() ? 11 : 9;

		i_payment = multiCurrency.isSelected() ? 8 : 6;

	}

	private void setCreditColumClasses() {
		int i = 0;
		creditTable.setColumnClass(i++, Boolean.class, false); // 0-Selection
		creditTable.setColumnClass(i++, Timestamp.class, true); // 1-TrxDate
		creditTable.setColumnClass(i++, String.class, true); // 2-Value
		creditTable.setColumnClass(i++, String.class, true); // 2-Value

		if (multiCurrency.isSelected()) {
			creditTable.setColumnClass(i++, String.class, true); // 3-Currency
			creditTable.setColumnClass(i++, BigDecimal.class, true); // 4-Amt
		}

		creditTable.setColumnClass(i++, BigDecimal.class, true); // 5-ConvAmt
		creditTable.setColumnClass(i++, BigDecimal.class, true); // 6-ConvAmt
																	// Open
		creditTable.setColumnClass(i++, BigDecimal.class, false); // 7-Conv
																	// Discount
		creditTable.setColumnClass(i++, BigDecimal.class, false); // 8-Conv
																	// WriteOff
		creditTable.setColumnClass(i++, BigDecimal.class, false); // 9-Conv
																	// Applied
	}

private Vector getCreditColumnNames() {
    	Vector columnNames = new Vector();
    	columnNames = new Vector();
            	
    	columnNames.add( Msg.getMsg( Env.getCtx(),"Select" ));
        columnNames.add( Msg.translate( Env.getCtx(),"Date" ));
        columnNames.add( Msg.translate( Env.getCtx(),"DocumentNo" ));
        columnNames.add( Msg.translate( Env.getCtx(),"DocumentType" ));

        if( multiCurrency.isSelected()) {
            columnNames.add( Msg.getMsg( Env.getCtx(),"TrxCurrency" ));
            columnNames.add( Msg.translate( Env.getCtx(),"Amount" ));
        }

        columnNames.add( Msg.getMsg( Env.getCtx(),"ConvertedAmount" ));
        columnNames.add( Msg.getMsg( Env.getCtx(),"OpenAmt" ));
        columnNames.add( Msg.getMsg( Env.getCtx(),"Discount" ));
        //columnNames.add( Msg.getMsg( Env.getCtx(),"WriteOff" ));
        columnNames.add( Msg.getMsg( Env.getCtx(),"FinancialDiscount" ));
        columnNames.add( Msg.getMsg( Env.getCtx(),"AppliedAmt" ));
        
// columnNames.add(" "); // Multiplier

    	return columnNames;
	}
	
	private Vector getCreditData(ResultSet rs) throws SQLException {
		Vector data = new Vector();
		
		// Se limpia el Map con los tipos de documento.
		getCreditDocTypes().clear();
		
		while (rs.next()) {
			Vector line = new Vector();
			
			line.add(new Boolean(false)); // 0-Selection
			line.add(rs.getTimestamp(1)); // 1-TrxDate

			KeyNamePair pp = new KeyNamePair(rs.getInt(3), rs.getString(2));

			line.add(pp); // 2-Value

			String docTypeName = rs.getString("doctype");
			int docTypeId = rs.getInt("C_DocType_ID");
			
			line.add(docTypeName); // 2-Value

			if (multiCurrency.isSelected()) {
				line.add(rs.getString("ISO_Code")); // 3-Currency
				line.add(rs.getBigDecimal("Amt")); // 4-Orig Amount
			}

			line.add(rs.getBigDecimal("ConvertedAmt")); // 3/5-ConvAmt

			BigDecimal open = rs.getBigDecimal("open");

			if (open == null) { // no conversion rate
				open = Env.ZERO;
			} else
				open = round(open);
			

			line.add(open); // 4/6-ConvOpen

			BigDecimal discount = rs.getBigDecimal("discount");

			if (discount == null) { // no concersion rate
				discount = Env.ZERO;
			}

			line.add(discount); // 5/7-ConvAllowedDisc
			line.add(Env.ZERO); // 6/8-WriteOff
			line.add(Env.ZERO); // 7/9-Applied

			// line.add(rs.getBigDecimal(9)); // 8/10-Multiplier
			// Add when open <> 0 (i.e. not if no conversion rate)

			if (Env.ZERO.compareTo(open) != 0) {
				creditTypes.add(rs.getString("EntityType"));
				data.add(line);
				// Se agrega el tipo de documento a la lista de tipos de documentos de credito.
				if(!getCreditDocTypes().containsKey(docTypeId)) {
					getCreditDocTypes().put(docTypeId, docTypeName);
				}
			}
			
				
		}

		return data;
	}

	private void setDebitTableColumnClasses() {
		int i = 0;
		debitTable.setColumnClass(i++, Boolean.class, false); // 0-Selection
		debitTable.setColumnClass(i++, Timestamp.class, true); // 1-TrxDate
		debitTable.setColumnClass(i++, String.class, true); // 2-Value
		debitTable.setColumnClass(i++, String.class, true); // 2-Value
		if (multiCurrency.isSelected()) {
			debitTable.setColumnClass(i++, String.class, true); // 3-Currency
			debitTable.setColumnClass(i++, BigDecimal.class, true); // 4-PayAmt
		}
		debitTable.setColumnClass(i++, BigDecimal.class, true); // 5-ConvAmt
		debitTable.setColumnClass(i++, BigDecimal.class, true); // 6-ConvOpen
		debitTable.setColumnClass(i++, BigDecimal.class, false); // 7-Allocated
	}

	private Vector getDebitColumnNames() {
		Vector columnNames = new Vector();
		
		columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
		columnNames.add(Msg.translate(Env.getCtx(), "Date"));
		columnNames.add(Msg.translate(Env.getCtx(), "DocumentNo"));

		columnNames.add(Msg.translate(Env.getCtx(), "DocumentType"));

		if (multiCurrency.isSelected()) {
			columnNames.add(Msg.getMsg(Env.getCtx(), "TrxCurrency"));
			columnNames.add(Msg.translate(Env.getCtx(), "Amount"));
		}

		columnNames.add(Msg.getMsg(Env.getCtx(), "ConvertedAmount"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "OpenAmt"));
		// columnNames.add( Msg.getMsg( Env.getCtx(),"Discount" ));
		// columnNames.add( Msg.getMsg( Env.getCtx(),"WriteOff" ));
		columnNames.add(Msg.getMsg(Env.getCtx(), "AppliedAmt"));

		// columnNames.add(" "); // Multiplier
		return columnNames;
	}

	private Vector getDebitData(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		Vector data = new Vector();
		while (rs.next()) {
			Vector line = new Vector();
			line.add(new Boolean(false)); // 0-Selection
			line.add(rs.getTimestamp(1)); // 1-TrxDate
			KeyNamePair pp = new KeyNamePair(rs.getInt(3), rs.getString(2)); // Id +
																				// Number
			line.add(pp); // 2-DocumentNo
			
			String docTypeName = rs.getString("doctype");
			int docTypeId = rs.getInt("C_DocType_ID");
			
			
			line.add(docTypeName); // 2-Value
			
			if (multiCurrency.isSelected()) {
				line.add(rs.getString("ISO_Code")); // 3-Currency
				line.add(rs.getBigDecimal("Amt")); // 4-PayAmt
			}
			line.add(rs.getBigDecimal("ConvertedAmt")); // 3/5-ConvAmt

			BigDecimal available = rs.getBigDecimal("open");

			if (!((available == null) || (available.signum() == 0))) { 
				available = round(available);
				line.add(available); // 4/6-ConvOpen/Available
				line.add(Env.ZERO); // 6/8-Payment
				debitTypes.add(rs.getString("EntityType"));
				data.add(line);
				// Se agrega el tipo de documento a la lista de tipos de documentos de credito.
				if(!getDebitDocTypes().containsKey(docTypeId)) {
					getDebitDocTypes().put(docTypeId, docTypeName);
				}

			}
		}

		return data;
	}

	private void setParameters(PreparedStatement pstmt, Collection parameters)
			throws SQLException {
		int n = 1;
		for (Iterator i = parameters.iterator(); i.hasNext(); n++) {
			Object parameter = i.next();
			pstmt.setObject(n, parameter);
		}

	}

	private StringBuffer getDocumentsQuery(int signo_isSOTrx, int docTypeId, Collection parameters) {
		StringBuffer sql = null;
		StringBuffer sqlPayments;
		StringBuffer sqlInvoices;
		sqlInvoices = getInvoicesQuery(signo_isSOTrx, docTypeId, parameters);
		sqlPayments = getPaymentsQuery(signo_isSOTrx, docTypeId, parameters);

		sql = sqlInvoices.append(" UNION ").append(sqlPayments).
				append(" ORDER BY fecha, DocumentNo");
		
		log.fine("DocumentsSQL=" + sql.toString());
		
		return sql;
	}

	private StringBuffer getPaymentsQuery(int signo_isSOTrx, int docTypeId, Collection parameters) {

		StringBuffer sql = new StringBuffer(
				"SELECT p.DateTrx as fecha," +
				"       p.DocumentNo," +
				"       p.C_Payment_ID, " +
				"       d.name as doctype, " +
				"       c.ISO_Code, " +
				"       p.PayAmt as Amt, " +
				"       currencyConvert(p.PayAmt,p.C_Currency_ID,?,p.DateTrx,p.C_ConversionType_ID,p.AD_Client_ID,p.AD_Org_ID) as ConvertedAmt, " +
				"       currencyConvert((paymentAvailable(p.C_Payment_ID)),p.C_Currency_ID,?,p.DateTrx,p.C_ConversionType_ID,p.AD_Client_ID,p.AD_Org_ID) as open, "  + // Open converted to client currency
				"       0 as discount, " +
				"       p.MultiplierAP, " +
				        DB.TO_STRING(T_PAYMENT) + " as EntityType, " +
				"       p.C_DocType_ID " +
				"FROM C_Payment_v p " + 
				"     INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) " +
				"     INNER JOIN C_DocType d ON d.c_doctype_id = p.c_doctype_id " +
				"WHERE p.IsAllocated='N' AND p.Processed='Y' " +
				"  AND p.C_Charge_ID IS NULL " + // Prepayments OK
				"  AND p.C_BPartner_ID=? " +
				"  AND d.signo_isSOTrx=? "
		);
		
		parameters.add(m_C_Currency_ID);
		parameters.add(m_C_Currency_ID);
		parameters.add(new Integer(m_C_BPartner_ID));
		parameters.add(new Integer(signo_isSOTrx));
		
		if (docTypeId != 0 ) {
			sql.append(" AND p.C_DocType_ID = ? ");
			parameters.add(new Integer(docTypeId));
		}

		if (!multiCurrency.isSelected()) {
			sql.append(" AND p.C_Currency_ID = ? "); // #4
			parameters.add(new Integer(m_C_Currency_ID));
		}

		return sql;
	}

	private StringBuffer getInvoicesQuery(int signo_isSOTrx, int docTypeId, Collection parameters) {

		StringBuffer sql = new StringBuffer(
				"SELECT i.DateInvoiced as fecha," +
				"       i.DocumentNo, " +
				"       i.C_Invoice_ID," +
				"       d.name as doctype, " +
				"       c.ISO_Code," +
				"       i.GrandTotal as Amt, " +
				"       currencyConvert(i.grandtotal,i.c_currency_id,?,i.dateacct, i.c_conversiontype_id, i.ad_client_id, i.ad_org_id) as ConvertedAmt, " +
				"       currencyConvert(invoiceOpen(i.C_Invoice_ID, COALESCE(i.C_InvoicePaySchedule_ID, 0)), " +
				"                       i.c_currency_id,?,i.dateinvoiced, i.c_conversiontype_id, i.ad_client_id, i.ad_org_id) as open, " + // Open converted to client currency
				"       currencyConvert(invoiceDiscount(i.C_Invoice_ID, ?, C_InvoicePaySchedule_ID), " +
				"                       i.c_currency_id,?,i.dateinvoiced, i.c_conversiontype_id, i.ad_client_id, i.ad_org_id)*i.Multiplier*i.MultiplierAP as discount, " +
				"       i.MultiplierAP, " +
				        DB.TO_STRING(T_INVOICE) + " as EntityType, " +
				"       i.C_DocType_ID " +        
				"FROM C_Invoice_v i " + 
				"     INNER JOIN C_Currency c ON (i.C_Currency_ID=c.C_Currency_ID) " +
				"     INNER JOIN C_DocType d ON d.c_doctype_id = i.c_doctype_id " +
				"WHERE i.docStatus IN (?)" + // p.IsAllocated='N' AND
										     // p.Processed='Y'" + "
										     // AND p.C_Charge_ID IS
										     // NULL" // Prepayments
										     // OK
				"  AND i.IsPaid='N' AND i.Processed='Y' " +
				"  AND i.C_BPartner_ID = ? " +
				"  AND d.signo_isSOTrx = ? "
		);
		
		parameters.add(m_C_Currency_ID); // gandtotal currency
		parameters.add(m_C_Currency_ID); // open currency
		parameters.add(( Timestamp )dateField.getValue()); //discount timestamp
		parameters.add(m_C_Currency_ID); // discount currency
		parameters.add(MInvoice.DOCSTATUS_Completed);
		parameters.add(new Integer(m_C_BPartner_ID));
		parameters.add(new Integer(signo_isSOTrx));
		
		if (docTypeId != 0 ) {
			sql.append(" AND i.C_DocType_ID = ? ");
			parameters.add(new Integer(docTypeId));
		}

		if (!multiCurrency.isSelected()) {
			sql.append(" AND i.C_Currency_ID=?"); // #4
			parameters.add(new Integer(m_C_Currency_ID));
		}
		
		return sql;
	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param e
	 */

	public void actionPerformed(ActionEvent e) {
		log.config("");
		
		m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (e.getSource().equals(optionPurchase)) {
			isSOTrx = false;
			loadBPartner();
		}

		if (e.getSource().equals(optionSales)) {
			isSOTrx = true;
			loadBPartner();
		}

		if (e.getSource().equals(multiCurrency)) {
			loadBPartner();
		}

		// Allocate

		if (e.getSource().equals(allocateButton)) {
			allocateButton.setEnabled(false);
			if(saveData())
				loadBPartner();
		}
		m_frame.setCursor(Cursor.getDefaultCursor());
	} // actionPerformed

	private void loadDocuments() {
		fillCreditDocumentTypes();
		fillDebitDocumentTypes();
	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param e
	 */

	public void tableChanged(TableModelEvent e) {
		boolean isUpdate = (e.getType() == TableModelEvent.UPDATE);

		// Not a table update

		if (!isUpdate) {
			calculate();

			return;
		}

		if (m_calculating) { // Avoid recursive calls
			return;
		}

		m_calculating = true;

		int row = e.getFirstRow();
		int col = e.getColumn();
		boolean isInvoice = (e.getSource().equals(creditTable.getModel()));

		log.config("Row=" + row + ", Col=" + col + ", InvoiceTable="
				+ isInvoice);
		if(col < 0 || row < 0) {
			m_calculating = false;
			return;
		}
		// null validation
		TableModel table = (TableModel)e.getSource();
		if(table.getValueAt(row,col) == null)
			table.setValueAt(Env.ZERO,row,col);
		
		// Payments

		if (!isInvoice) {
			TableModel payment = debitTable.getModel();

			BigDecimal amount = (BigDecimal) payment.getValueAt(row, i_open); // Open Amount
					
			BigDecimal applied = (BigDecimal) payment
					.getValueAt(row, i_payment);
			
			if (col == 0) {
				// Clear previous selections
				clearDebitTableSelection(row);
				// selected - set payment amount
				
				if (((Boolean) payment.getValueAt(row, col)).booleanValue()) {
					payment.setValueAt(amount, row, i_payment);
				} else { // de-selected
					payment.setValueAt(Env.ZERO, row, i_payment);
				}
			} else if (col == i_payment) {
				if (applied.compareTo(amount) > 0) {
					payment.setValueAt(amount, row, i_payment);
				}
			}
		}

		// Invoice Selection

		else if (col == 0) {
			TableModel invoice = creditTable.getModel();
			clearCreditTableSelection(row);
			// selected - set applied amount

			if (((Boolean) invoice.getValueAt(row, col)).booleanValue()) {
				BigDecimal amount = (BigDecimal) invoice
						.getValueAt(row, i_open); // Open Amount

				amount = amount.subtract((BigDecimal) invoice.getValueAt(row,
						i_discount));

				// Se redondea a dos decimales para no tener diferencias con los aplicados de las facturas.
				amount = amount.setScale(2,BigDecimal.ROUND_HALF_UP); 

				invoice.setValueAt(Env.ZERO, row, i_writeOff); // to be sure
				invoice.setValueAt(Env.ZERO, row, i_discount); // to be sure
				invoice.setValueAt(amount, row, i_applied);
			} else // de-selected
			{
				invoice.setValueAt(Env.ZERO, row, i_writeOff);
				invoice.setValueAt(Env.ZERO, row, i_discount);
				invoice.setValueAt(Env.ZERO, row, i_applied);
			}

			creditTable.repaint(); // update r/o
		}

		// Invoice - Try to balance entry
		/*
		 * Se comentó las líneas siguientes porque se ocultó el campo de saldo automático 
		 */
		/*
		else {
			if (autoWriteOff.isSelected()) {
				TableModel invoice = creditTable.getModel();

				// if applied entered, adjust writeOff

				if (col == i_applied) {
					BigDecimal openAmount = (BigDecimal) invoice.getValueAt(
							row, i_open); // Open Amount
					BigDecimal amount = openAmount
							.subtract((BigDecimal) invoice.getValueAt(row,
									i_discount));

					amount = amount.subtract((BigDecimal) invoice.getValueAt(
							row, i_applied));
					invoice.setValueAt(amount, row, i_writeOff);

					// Warning if > 30%

					if (amount.doubleValue() / openAmount.doubleValue() > .30) {
						ADialog
								.warn(m_WindowNo, this,
										"AllocationWriteOffWarn");
					}
				} else // adjust applied
				{
					BigDecimal amount = (BigDecimal) invoice.getValueAt(row,
							i_open); // Open Amount

					amount = amount.subtract((BigDecimal) invoice.getValueAt(
							row, i_discount));
					amount = amount.subtract((BigDecimal) invoice.getValueAt(
							row, i_writeOff));
					invoice.setValueAt(amount, row, i_applied);
				}
			} 
		}
		*/

		// Check if applied + writeoff + discount is not greather than open. 
		if(isInvoice) {
			
			BigDecimal openAmount = (BigDecimal) creditTable.getValueAt(row,i_open);
			BigDecimal writeoff = (BigDecimal) creditTable.getValueAt(row, i_writeOff);
			BigDecimal discount = (BigDecimal) creditTable.getValueAt(row, i_discount);
			BigDecimal applied = (BigDecimal) creditTable.getValueAt(row, i_applied);
			BigDecimal sum = applied.add(writeoff).add(discount);
			validCreditTotalAmt = (sum.compareTo(openAmount) <= 0);
		}
		
		m_calculating = false;
		calculate();
		
		m_calculating = true;
		if (validCreditTotalAmt) {
			allocateButton.setEnabled(allocateButton.isEnabled());
		} else {
			allocateButton.setEnabled(false);
			if(isInvoice && (col == i_discount || col == i_writeOff || col == i_applied))
				ADialog.error(m_WindowNo,this,"InvalidAllocationCreditTotalAmt");
		}
		m_calculating = false;
	} // tableChanged

	/**
	 * Descripción de Método
	 * 
	 */

	private void calculate() {
		log.config("");

		//

		DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount);

		// Payment

		TableModel payment = debitTable.getModel();
		BigDecimal totalPay = new BigDecimal(0.0);
		int rows = payment.getRowCount();

		m_noDebits = 0;

		for (int i = 0; i < rows; i++) {
			if (((Boolean) payment.getValueAt(i, 0)).booleanValue()) {
				// BigDecimal bd = ( BigDecimal )payment.getValueAt( i,i_payment
				// );
				BigDecimal bd = (BigDecimal) payment.getValueAt(i, i_payment);
				totalPay = totalPay.add(bd); // Applied Pay
				m_noDebits++;
				log.fine("Payment_" + i + " = " + bd + " - Total=" + totalPay);
			}
		}

		debitInfo.setText(String.valueOf(m_noDebits) + " - "
				+ Msg.getMsg(Env.getCtx(), "Sum") + "  "
				+ format.format(totalPay) + " ");

		// Invoices

		TableModel invoice = creditTable.getModel();
		BigDecimal totalInv = new BigDecimal(0.0);

		rows = invoice.getRowCount();
		m_noCredits = 0;

		for (int i = 0; i < rows; i++) {
			if (((Boolean) invoice.getValueAt(i, 0)).booleanValue()) {
				BigDecimal bd = (BigDecimal) invoice.getValueAt(i, i_applied);

				totalInv = totalInv.add(bd); // Applied Inv
				m_noCredits++;
				log.fine("Invoice_" + i + " = " + bd + " - Total=" + totalPay);
			}
		}

		creditInfo.setText(String.valueOf(m_noCredits) + " - "
				+ Msg.getMsg(Env.getCtx(), "Sum") + "  "
				+ format.format(totalInv) + " ");

		// Set Allocation Currency

		allocCurrencyLabel.setText(currencyPick.getDisplay()); //selected currency ISO 
		//allocCurrencyLabel.setText(getClientCurrencyISOCode());   //client currency ISO 


		// Difference
		BigDecimal difference = totalPay.subtract(totalInv);
		differenceField.setText(format.format(difference));

		if ((difference.compareTo(new BigDecimal(0.0)) == 0)
				&& (m_noCredits == 1) && (m_noDebits == 1)) {
			allocateButton.setEnabled(true);
		} else {
			allocateButton.setEnabled(false);
		}

		if (m_noDebits > 1) {
			debitInfo.setText(Msg.getMsg(Env.getCtx(), "SelectOneCredit"));
		}

		if (m_noCredits > 1) {
			creditInfo.setText(Msg.getMsg(Env.getCtx(), "SelectOneDebit"));
		}

	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param e
	 */

	public void vetoableChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		Object value = e.getNewValue();

		log.config(name + "=" + value);
		
		
		if (value == null) {
			return;
		}

		// BPartner

		if (name.equals("C_BPartner_ID")) {
			m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			bpartnerSearch.setValue(value);
			m_C_BPartner_ID = ((Integer) value).intValue();
			loadBPartner();
			m_frame.setCursor(Cursor.getDefaultCursor());
		}

		// Currency

		else if (name.equals("C_Currency_ID")) {
			m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			m_C_Currency_ID = ((Integer) value).intValue();
			loadBPartner();
			m_frame.setCursor(Cursor.getDefaultCursor());
		}
		
		
		
		// Date

		else if (name.equals("Date")) {
			loadBPartner();
		}
		
	} // vetoableChange

	/**
	 * Descripción de Método
	 * 
	 */

	private boolean saveData() {
				
		if (m_noCredits + m_noDebits == 0) {
			allocateButton.setEnabled(false);
			return false;
		}
		
		if(!validateAllocation()) {
			allocateButton.setEnabled(true);
			return false;
		}

		// fixed fields

		int AD_Client_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo,
				"AD_Client_ID");
		int AD_Org_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo,
				"AD_Org_ID");
		
		int C_BPartner_ID = m_C_BPartner_ID;
		int C_Order_ID = 0;
		int C_CashLine_ID = 0;
		Timestamp DateTrx = (Timestamp) dateField.getValue();
		int C_Currency_ID = m_C_Currency_ID;// the allocation currency

		//

		if (AD_Org_ID == 0) {
			ADialog.error(m_WindowNo, this, "Org0NotAllowed", null);

			return false;
		}

		//

		log.config("Client=" + AD_Client_ID + ", Org=" + AD_Org_ID
				+ ", BPartner=" + C_BPartner_ID + ", Date=" + DateTrx);

		Trx trx = Trx.get(Trx.createTrxName("AL"), true);

		// Payment - Loop and add them to paymentList/amountList

		int pRows = debitTable.getRowCount();
		TableModel debit = debitTable.getModel();
		ArrayList debitList = new ArrayList(pRows);
		ArrayList amountList = new ArrayList(pRows);
		ArrayList typeList = new ArrayList();
		BigDecimal debitAppliedAmt = Env.ZERO;

		for (int i = 0; i < pRows; i++) {

			// Debit line is selected

			if (((Boolean) debit.getValueAt(i, 0)).booleanValue()) {
				KeyNamePair pp = (KeyNamePair) debit.getValueAt(i, 2); // Value

				// Payment variables

				int debitDocumentId = pp.getKey();

				debitList.add(new Integer(debitDocumentId));

				//

				BigDecimal debitAmount = (BigDecimal) debit.getValueAt(i,
						i_payment); // Applied Payment

				amountList.add(debitAmount);

				//

				debitAppliedAmt = debitAppliedAmt.add(debitAmount);

				String debitEntityType = (String) debitTypes.get(i);
				typeList.add(debitEntityType);
				//

				log.fine("Debit=" + debitDocumentId + " - PaymentAmt="
						+ debitAmount); // + " * " + Multiplier + " = " +
										// PaymentAmtAbs);
			}
		}

		log.config("Number of Payments=" + debitList.size() + " - Total="
				+ debitAppliedAmt);

		// Invoices - Loop and generate alloctions

		int iRows = creditTable.getRowCount();
		TableModel credit = creditTable.getModel();
		BigDecimal totalAppliedAmt = Env.ZERO;

		// Create Allocation - but don't save yet

		MAllocationHdr alloc = new MAllocationHdr(Env.getCtx(), true, // manual
				DateTrx, C_Currency_ID, Env.getContext(Env.getCtx(),
						"#AD_User_Name"), trx.getTrxName());

		// For all invoices
		
		int invoiceLines = 0;
		int totalinvoices = 0;
		for (int h = 0; h < iRows; h++) {
			if (((Boolean) credit.getValueAt(h, 0)).booleanValue()) {
				totalinvoices++;
			}
		}

		for (int i = 0; i < iRows; i++) {

			// Invoice line is selected

			if (((Boolean) credit.getValueAt(i, 0)).booleanValue()) {
				invoiceLines++;

				KeyNamePair pp = (KeyNamePair) credit.getValueAt(i, 2); // Value
				String creditType = (String) creditTypes.get(i);
				// Invoice variables

				int creditDocumentId = pp.getKey();
				BigDecimal AppliedAmt = (BigDecimal) credit.getValueAt(i,
						i_applied);

				// semi-fixed fields (reset after first invoice)

				BigDecimal DiscountAmt = (BigDecimal) credit.getValueAt(i,
						i_discount);
				BigDecimal WriteOffAmt = (BigDecimal) credit.getValueAt(i,
						i_writeOff);

				// OverUnderAmt needs to be in Allocation Currency

				BigDecimal OverUnderAmt = ((BigDecimal) credit.getValueAt(i,
						i_open)).subtract(AppliedAmt).subtract(DiscountAmt)
						.subtract(WriteOffAmt);

				log.config("Invoice #" + i + " - AppliedAmt=" + AppliedAmt); // + "
																				// -> "
																				// +
																				// AppliedAbs);

				// loop through all payments until invoice applied

				int noPayments = 0;

				for (int j = 0; (j < debitList.size())
						&& (AppliedAmt.compareTo(Env.ZERO) != 0); j++) {
					int debitDocumentId = ((Integer) debitList.get(j))
							.intValue();
					int invoiceId;
					int paymentId;
					String paymentType;

					BigDecimal debitAmt = (BigDecimal) amountList.get(j);

					// Verify that the credit and debit document aren't both
					// payments.

					if (debitAmt.compareTo(Env.ZERO) != 0) {

						String debitType = (String) typeList.get(j);

						log.config(".. with payment #" + j + ", Amt="
								+ debitAmt);
						noPayments++;

						// use Invoice Applied Amt

						if ((creditType.compareTo(T_PAYMENT) == 0)
								&& (debitType.compareTo(T_PAYMENT) == 0)) {
							log.info("Invalid");
						} else {
							// One of them could be payment
							// If the debit is a payment then reverse
							if (creditType.compareTo(T_PAYMENT) == 0) {
								invoiceId = debitDocumentId;
								paymentId = creditDocumentId;
								paymentType = T_PAYMENT;
							} else if (debitType.compareTo(T_PAYMENT) == 0) {
								invoiceId = creditDocumentId;
								paymentId = debitDocumentId;
								paymentType = T_PAYMENT;
							} else {
								invoiceId = creditDocumentId;
								paymentId = debitDocumentId;
								paymentType = T_INVOICE;
							}
							BigDecimal amount = null;
							log.fine("Antes de la comprobacion, totalinvoices="
									+ totalinvoices + ", payments="
									+ debitList.size());
							if (debitList.size() > totalinvoices) {
								amount = debitAmt;
							} else {
								amount = AppliedAmt;
							}
							log
									.fine("C_Payment_ID=" + debitDocumentId
											+ ", C_Invoice_ID="
											+ creditDocumentId + ", Amount="
											+ amount + ", Discount="
											+ DiscountAmt + ", WriteOff="
											+ WriteOffAmt);

							// Allocation Line

							if ((alloc.getID() == 0) && !alloc.save()) {
								log.log(Level.SEVERE, "Allocation not created");

								return false;
							}

							MAllocationLine aLine = new MAllocationLine(alloc,
									amount, DiscountAmt, WriteOffAmt,
									OverUnderAmt);

							aLine.setDocInfo(C_BPartner_ID, C_Order_ID,
									invoiceId);
							if (paymentType.compareTo(T_PAYMENT) == 0) {
								aLine.setPaymentInfo(paymentId, C_CashLine_ID);
							} else {
								aLine.setPaymentInvoiceInfo(paymentId);
							}

							if (!aLine.save(trx.getTrxName())) {
								log.log(Level.SEVERE,
										"Allocation Line not written - Invoice="
												+ creditDocumentId);
							}

							// Apply Discounts and WriteOff only first time

							DiscountAmt = Env.ZERO;
							WriteOffAmt = Env.ZERO;

							// subtract amount from Payment/Invoice

							AppliedAmt = AppliedAmt.subtract(amount);
							debitAmt = debitAmt.subtract(amount);
							log.fine("Allocation Amount=" + amount
									+ " - Remaining  Applied=" + AppliedAmt
									+ ", Payment=" + debitAmt);
							amountList.set(j, debitAmt); // update

						} // for all applied amounts
					}
				} // noop through payments for invoice

				// No Payments allocated and none existing (e.g. Inv/CM)

				if ((noPayments == 0) && (debitList.size() == 0)) {
					int C_Payment_ID = 0;

					log.config(" ... no payment - TotalApplied="
							+ totalAppliedAmt);

					// Create Allocation

					log.fine("C_Payment_ID=" + C_Payment_ID + ", C_Invoice_ID="
							+ creditDocumentId + ", Amount=" + AppliedAmt
							+ ", Discount=" + DiscountAmt + ", WriteOff="
							+ WriteOffAmt);

					// Allocation Line

					if ((alloc.getID() == 0) && !alloc.save()) {
						log.log(Level.SEVERE, "Allocation not created");
					}

					MAllocationLine aLine = new MAllocationLine(alloc,
							AppliedAmt, DiscountAmt, WriteOffAmt, OverUnderAmt);

					aLine.setDocInfo(C_BPartner_ID, C_Order_ID,
							creditDocumentId);
					aLine.setPaymentInfo(C_Payment_ID, C_CashLine_ID);

					if (!aLine.save(trx.getTrxName())) {
						log.log(Level.SEVERE,
								"Allocation Line not written - Invoice="
										+ creditDocumentId);
					}

					log.fine("Allocation Amount=" + AppliedAmt);
				}

				totalAppliedAmt = totalAppliedAmt.add(AppliedAmt);
				log.config("TotalRemaining=" + totalAppliedAmt);
			} // invoice selected
		} // invoice loop

		// Only Payments and total of 0 (e.g. Payment/Reversal)

		if ((invoiceLines == 0) && (debitList.size() > 0)
				&& (debitAppliedAmt.compareTo(Env.ZERO) == 0)) {
			for (int i = 0; i < debitList.size(); i++) {
				int C_Payment_ID = ((Integer) debitList.get(i)).intValue();
				BigDecimal PaymentAmt = (BigDecimal) amountList.get(i);

				// BigDecimal PaymentMultiplier =
				// (BigDecimal)multiplierList.get(i);
				// BigDecimal PaymentAbs =
				// PaymentAmt.multiply(PaymentMultiplier);

				log.fine("Payment=" + C_Payment_ID + ", Amount=" + PaymentAmt); // + ",
																				// Abs="
																				// +
																				// PaymentAbs);

				// Allocation Line

				if ((alloc.getID() == 0) && !alloc.save()) {
					log.log(Level.SEVERE, "Allocation not created");
				}

				MAllocationLine aLine = new MAllocationLine(alloc, PaymentAmt,
						Env.ZERO, Env.ZERO, Env.ZERO);

				aLine.setDocInfo(C_BPartner_ID, 0, 0);
				aLine.setPaymentInfo(C_Payment_ID, 0);

				if (!aLine.save(trx.getTrxName())) {
					log.log(Level.SEVERE,
							"Allocation Line not saved - Payment="
									+ C_Payment_ID);
				}
			}
		} // onlyPayments

		if (totalAppliedAmt.compareTo(Env.ZERO) != 0) {
			log.log(Level.SEVERE, "Remaining TotalAppliedAmt="
					+ totalAppliedAmt);
		}

		// Should start WF

		if (alloc.getID() != 0) {
			alloc.processIt(DocAction.ACTION_Complete);
			alloc.save(trx.getTrxName());
		}

		// Test/Set IsPaid for Invoice - requires that allocation is posted

		for (int i = 0; i < iRows; i++) {

			// Invoice line is selected

			if (((Boolean) credit.getValueAt(i, 0)).booleanValue()) {
				KeyNamePair pp = (KeyNamePair) credit.getValueAt(i, 2); // Value

				// Invoice variables

				int C_Invoice_ID = pp.getKey();
				String sql = "SELECT invoiceOpen(C_Invoice_ID, 0) "
						+ "FROM C_Invoice WHERE C_Invoice_ID=?";
				BigDecimal open = DB.getSQLValueBD(trx.getTrxName(), sql,
						C_Invoice_ID);

				if ((open != null) && (open.signum() == 0)) {
					sql = "UPDATE C_Invoice SET IsPaid='Y' "
							+ "WHERE C_Invoice_ID=" + C_Invoice_ID;

					int no = DB.executeUpdate(sql, trx.getTrxName());

					log.config("Invoice #" + i + " is paid");
				} else {
					log.config("Invoice #" + i + " is not paid - " + open);
				}
			}
		}

		// Test/Set Payment is fully allocated

		for (int i = 0; i < debitList.size(); i++) {
			int C_Payment_ID = ((Integer) debitList.get(i)).intValue();
			if (((String) typeList.get(i)) == "P") {
				MPayment pay = new MPayment(Env.getCtx(), C_Payment_ID, trx
						.getTrxName());

				if (pay.testAllocation()) {
					pay.save();
				}

				log.config("Payment #" + i
						+ (pay.isAllocated() ? " not" : " is")
						+ " fully allocated");
			} else {
				String sql = "SELECT invoiceOpen(C_Invoice_ID, 0) "
						+ "FROM C_Invoice WHERE C_Invoice_ID=?";
				BigDecimal open = DB.getSQLValueBD(trx.getTrxName(), sql,
						C_Payment_ID);

				if ((open != null) && (open.signum() == 0)) {
					sql = "UPDATE C_Invoice SET IsPaid='Y' "
							+ "WHERE C_Invoice_ID=" + C_Payment_ID;

					int no = DB.executeUpdate(sql, trx.getTrxName());

					log.config("Invoice #" + i + " is paid");
				} else {
					log.config("Invoice #" + i + " is not paid - " + open);
				}

			}
		}

		// Disytel: setear EC y grandTotal
		alloc.setC_BPartner_ID(m_C_BPartner_ID);
		MAllocationLine[] lines = alloc.getLines(true);
		BigDecimal total = new BigDecimal(0);
		for (int i=0; i< lines.length; i++)
			total = total.add(lines[0].getAmount());
		alloc.setGrandTotal(total);
		alloc.save(trx.getTrxName());		

		
		debitList.clear();
		amountList.clear();
		trx.commit();
		trx.close();
		return true;
	} // saveData
	
	private String getClientCurrencyISOCode() {
    	int C_Currency_ID = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID"); //
    	MCurrency currency = new MCurrency(Env.getCtx(),C_Currency_ID,null);
    	String curISO = currency.getISO_Code();
    	currency = null;
    	return "(" + curISO + ")";
	}
	
	private void clearCreditTableSelection(int rowChanged) {
		TableModel tm = creditTable.getModel();
		long rows = tm.getRowCount();
		
		for (int i = 0; i < rows; i++) {
			if(i != rowChanged) {
				tm.setValueAt(Boolean.FALSE,i,0);
				tm.setValueAt(Env.ZERO, i, i_writeOff);
				tm.setValueAt(Env.ZERO, i, i_applied);
			}	
		}
	}

	private void clearDebitTableSelection(int rowChanged) {
		TableModel tm = debitTable.getModel();
		long rows = tm.getRowCount();
		
		for (int i = 0; i < rows; i++) {
			if(i != rowChanged) {
				tm.setValueAt(Boolean.FALSE,i,0);
				tm.setValueAt(Env.ZERO, i, i_payment);
			}	
		}
	}
	
	private boolean validateAllocation() {
		boolean result = true; 
		int debitRow = 0;
		int creditRow = 0;
		StringBuffer errorMsg = new StringBuffer("");
		
		// Se obtiene el debito seleccionado.
		for (int i = 0; i < debitTable.getRowCount(); i++) {
			if (((Boolean) debitTable.getValueAt(i, 0)).booleanValue()) {
				debitRow = i;
			}
		}

		// Se obtiene el credito seleccionado.
		for (int i = 0; i < creditTable.getRowCount(); i++) {
			if (((Boolean) creditTable.getValueAt(i, 0)).booleanValue()) {
				creditRow = i;
			}
		}
		
		BigDecimal debitApplied = (BigDecimal)debitTable.getValueAt(debitRow,i_payment);
		BigDecimal debitOpen = (BigDecimal) debitTable.getValueAt(debitRow,i_open);
		
		BigDecimal creditApplied = (BigDecimal)creditTable.getValueAt(creditRow,i_applied);
		BigDecimal creditOpen = (BigDecimal)creditTable.getValueAt(creditRow,i_open);
		BigDecimal creditWriteOff = (BigDecimal)creditTable.getValueAt(creditRow,i_writeOff);
		BigDecimal creditDiscount = (BigDecimal)creditTable.getValueAt(creditRow,i_discount);
		BigDecimal creditTotal = creditApplied.add(creditWriteOff).add(creditDiscount); 
		
		// Se ingreso una fecha para la transaccion
		if(dateField.getValue() == null) {
			result = false;
			errorMsg.append("- ").
					 append(Msg.getMsg(Env.getCtx(),"InvalidAllocationDate")).append("\n");
		}
		
		// Montos aplicados iguales
		if(debitApplied.compareTo(creditApplied) != 0) {
			result = false;
			errorMsg.append("- ").
					 append(Msg.getMsg(Env.getCtx(),"DifferentAllocationApplied")).append("\n");
		}
		
		// Total aplicado del credito menor o igual que el pendiente
		if(creditTotal.compareTo(creditOpen) > 0) {
			result = false;
			errorMsg.append("- ").
			 	     append(Msg.getMsg(Env.getCtx(),"InvalidAllocationCreditTotalAmt")).append("\n");
		}
		
		// Aplicado del debito menor o igual que el pendiente
		if(debitApplied.compareTo(debitOpen) > 0) {
			result = false;
			errorMsg.append("- ").
	 	             append(Msg.getMsg(Env.getCtx(),"InvalidAllocationDebitApplied"));
		}
		
		if(!result)
			ADialog.error(m_WindowNo,this,"AllocationProcessError",errorMsg.toString());
		
		return result;
	}
	
	private BigDecimal round(BigDecimal number) {
		return number.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	private Map<Integer, String> getCreditDocTypes() {
		return creditDocTypes;
	}

	private void setCreditDocTypes(Map<Integer, String> creditDocTypes) {
		this.creditDocTypes = creditDocTypes;
	}

	private Map<Integer, String> getDebitDocTypes() {
		return debitDocTypes;
	}

	private void setDebitDocTypes(Map<Integer, String> debitDocTypes) {
		this.debitDocTypes = debitDocTypes;
	}

} // VAllocation

/*
 * @(#)VAllocation.java 02.07.07
 * 
 * Fin del fichero VAllocation.java
 * 
 * Versión 2.1
 * 
 */
