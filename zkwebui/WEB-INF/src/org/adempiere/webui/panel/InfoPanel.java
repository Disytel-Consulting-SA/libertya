/******************************************************************************
 * Product: Posterita Ajax UI 												  *
 * Copyright (C) 2007 Posterita Ltd.  All Rights Reserved.                    *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/

package org.adempiere.webui.panel;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.apps.BusyDialog;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Datebox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Searchbox;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListItemRenderer;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.part.ITabOnSelectHandler;
import org.adempiere.webui.plugin.common.PluginInfoPanelUtils;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.window.FDialog;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MRole;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.zkoss.util.media.AMedia;
import org.zkoss.zhtml.Filedownload;
import org.zkoss.zk.au.out.AuEcho;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.ZulEvents;

/**
 *	Search Information and return selection - Base Class.
 *  Based on Info written by Jorg Janke
 *  
 *  @author Sendy Yagambrum
 *  
 * Zk Port
 * @author Elaine
 * @version	Info.java Adempiere Swing UI 3.4.1
 */
public abstract class InfoPanel extends Window implements EventListener, WTableModelListener, ListModelExt
{
	
	/**
	 * generated serial version ID
	 */
	private static final long serialVersionUID = 325050327514511004L;
	private final static int PAGE_SIZE = 100;

	/** Sobrecarga de método para uso tradicional */
	public static InfoPanel create (int WindowNo, String tableName, String keyColumn, String value, boolean multiSelection, String whereClause) {
		return create(WindowNo, tableName, keyColumn, value, multiSelection, whereClause, false, null, null);
	}

	/** Sobrecarga de método para uso bajo BPartner */	
	public static InfoPanel create (int WindowNo, String tableName, String keyColumn, String value, boolean multiSelection, String whereClause, boolean isSoTrx) {
		return create(WindowNo, tableName, keyColumn, value, multiSelection, whereClause, isSoTrx, null, null);
	}

	/** Sobrecarga de método para uso bajo Product */	
	public static InfoPanel create (int WindowNo, String tableName, String keyColumn, String value, boolean multiSelection, String whereClause, Integer M_Warehouse_ID, Integer M_PriceList_ID) {
		return create(WindowNo, tableName, keyColumn, value, multiSelection, whereClause, false, M_Warehouse_ID, M_PriceList_ID);
	}
	
	/** Metodo general de acceso */
    public static InfoPanel create (int WindowNo, String tableName, String keyColumn, String value, boolean multiSelection, String whereClause, boolean isSoTrx, Integer M_Warehouse_ID, Integer M_PriceList_ID)
	{
    	return create (WindowNo, tableName, keyColumn, value, multiSelection, whereClause, isSoTrx, M_Warehouse_ID, M_PriceList_ID, true);
    }
    
    public static InfoPanel create (int WindowNo, String tableName, String keyColumn, String value, boolean multiSelection, String whereClause, boolean isSoTrx, Integer M_Warehouse_ID, Integer M_PriceList_ID, boolean addSecurityValidation) {
    	InfoPanel info = null;
        /**
         * Logica para plugins - Redefinicion de clases InfoPanel
         */
        info = PluginInfoPanelUtils.getInfoPanel(tableName, WindowNo, value, multiSelection, whereClause, M_Warehouse_ID, M_PriceList_ID, isSoTrx, keyColumn);
        
        if (info!=null)        
        	;
        else if (tableName.equals("C_BPartner"))
            info = new InfoBPartnerPanel (WindowNo, value, isSoTrx, multiSelection, whereClause);
        else if (tableName.equals("M_Product"))
            info = new InfoProductPanel ( WindowNo, M_Warehouse_ID, M_PriceList_ID, multiSelection, value,whereClause);
        else if (tableName.equals("C_Invoice"))
            info = new InfoInvoicePanel ( WindowNo, value, multiSelection, whereClause, true, addSecurityValidation);
        else if (tableName.equals("A_Asset"))
            info = new InfoAssetPanel (WindowNo, 0, value, multiSelection, whereClause);
        else if (tableName.equals("C_Order"))
            info = new InfoOrderPanel ( WindowNo, value, multiSelection, whereClause, true, addSecurityValidation);
        else if (tableName.equals("M_InOut"))
            info = new InfoInOutPanel (WindowNo, value, multiSelection, whereClause, true, addSecurityValidation);
        else if (tableName.equals("C_Payment"))
            info = new InfoPaymentPanel (WindowNo, value, multiSelection, whereClause);
        else if (tableName.equals("C_CashLine"))
           info = new InfoCashLinePanel (WindowNo, value, multiSelection, whereClause);
        else if (tableName.equals("S_ResourceAssigment"))
            info = new InfoAssignmentPanel (WindowNo, value, multiSelection, whereClause);
        else if (tableName.equals("C_POSJournal"))
            info = new InfoPOSJournalPanel(WindowNo, value, multiSelection, whereClause);
        else if (tableName.equals("C_AllocationHdr"))
        	info = new InfoAllocationHdrPanel(WindowNo,value,isSoTrx ,multiSelection, whereClause);
        else if (tableName.equals("M_EntidadFinancieraPlan"))
        	info = new InfoEntidadFinancieraPlanPanel(WindowNo, value, multiSelection, whereClause);
        else
            info = new InfoGeneralPanel (value, WindowNo, tableName, keyColumn, isSoTrx, multiSelection, whereClause);
        //
        info.setAddSecurityValidation(addSecurityValidation);
        
        return info;
	}

	/**
	 * Show BPartner Info (non modal)
	 * @param WindowNo window no
	 */
	public static void showBPartner (int WindowNo)
	{
        /* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("C_BPartner", WindowNo, "", false, "", null, null, !Env.getContext( Env.getCtx(),"IsSOTrx" ).equals( "N" ), null);
        if (infoPanel==null)
        	infoPanel = new InfoBPartnerPanel ( WindowNo, "", !Env.getContext(Env.getCtx(),"IsSOTrx").equals("N"), false, "", false);
		AEnv.showWindow(infoPanel);
	}   //  showBPartner

	/**
	 * Show Asset Info (non modal)
	 * @param frame Parent Frame
	 * @param WindowNo window no
	 */
	public static void showAsset (int WindowNo)
	{
        /* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("A_Asset", WindowNo, "", false, "", null, null, null, null);
        if (infoPanel==null)
        	infoPanel = new InfoAssetPanel (WindowNo, 0, "", false, "", false);
		AEnv.showWindow(infoPanel);
	}   //  showBPartner

	/**
	 * Show Product Info (non modal)
	 * @param frame Parent Frame
	 * @param WindowNo window no
	 */
	public static void showProduct (int WindowNo)
	{        
		/* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("M_Product", WindowNo, "", false, "", Env.getContextAsInt( Env.getCtx(),WindowNo,"M_Warehouse_ID" ), Env.getContextAsInt( Env.getCtx(),WindowNo,"M_PriceList_ID" ), null, null);
        if (infoPanel==null)
			infoPanel = new InfoProductPanel(WindowNo, Env.getContextAsInt(Env.getCtx(), WindowNo, "M_Warehouse_ID"), Env.getContextAsInt(Env.getCtx(), WindowNo, "M_PriceList_ID"), false, "", "", false);
		AEnv.showWindow(infoPanel);
	}   //  showProduct
	
	/**
	 * Show Order Info (non modal)
	 * @param frame Parent Frame
	 * @param WindowNo window no
	 * @param value query value
	 */
	public static void showOrder (int WindowNo, String value)
	{
        /* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("C_Order", WindowNo, value, false, "", null, null, null, null);       
        if (infoPanel==null)
        	infoPanel = new InfoOrderPanel(WindowNo, "", false, "", false);
		AEnv.showWindow(infoPanel);
	}   //  showOrder

	/**
	 * Show Invoice Info (non modal)
	 * @param frame Parent Frame
	 * @param WindowNo window no
	 * @param value query value
	 */
	public static void showInvoice (int WindowNo, String value)
	{
        /* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("C_Invoice", WindowNo, value, false, "", null, null, null, null);      
        if (infoPanel==null)
        	infoPanel = new InfoInvoicePanel(WindowNo, "", false, "", false);
		AEnv.showWindow(infoPanel);
	}   //  showInvoice

	/**
	 * Show Shipment Info (non modal)
	 * @param frame Parent Frame
	 * @param WindowNo window no
	 * @param value query value
	 */
	public static void showInOut (int WindowNo, String value)
	{
        /* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("M_InOut", WindowNo, value, false, "", null, null, null, null);       
        if (infoPanel==null)
        	infoPanel = new InfoInOutPanel (WindowNo, value,false, "", false);
		AEnv.showWindow(infoPanel);
	}   //  showInOut

	/**
	 * Show Payment Info (non modal)
	 * @param frame Parent Frame
	 * @param WindowNo window no
	 * @param value query value
	 */
	public static void showPayment (int WindowNo, String value)
	{        
		/* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("C_Payment", WindowNo, value, false, "", null, null, null, null);
        if (infoPanel==null)
			infoPanel = new InfoPaymentPanel (WindowNo, value, false, "", false);
		AEnv.showWindow(infoPanel);
	}   //  showPayment

	/**
	 * Show Cash Line Info (non modal)
	 * @param frame Parent Frame
	 * @param WindowNo window no
	 * @param value query value
	 */
	public static void showCashLine (int WindowNo, String value)
	{
        /* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("C_CashLine", WindowNo, value, false, "", null, null, null, null);
        if (infoPanel==null)
        	infoPanel = new InfoCashLinePanel (WindowNo, value, false, "", false);
		AEnv.showWindow(infoPanel);
	}   //  showCashLine

	/**
	 * Show Assignment Info (non modal)
	 * @param frame Parent Frame
	 * @param WindowNo window no
	 * @param value query value
	 */
	public static void showAssignment (int WindowNo, String value)
	{
        /* Logica para plugins - Redefinicion de clases InfoPanel  */
        InfoPanel infoPanel = PluginInfoPanelUtils.getInfoPanel("S_ResourceAssigment", WindowNo, value, false, "", null, null, null, null);
        if (infoPanel==null)
        	infoPanel = new InfoAssignmentPanel (WindowNo, value, false, "", false);
		AEnv.showWindow(infoPanel);
	}   //  showAssignment

	/** Window Width                */
	static final int        INFO_WIDTH = 800;
	private boolean m_lookup;

	/**************************************************
     *  Detail Constructor 
     * @param WindowNo  WindowNo
     * @param tableName tableName
     * @param keyColumn keyColumn   
     * @param whereClause   whereClause
	 */
	protected InfoPanel (int WindowNo,
		String tableName, String keyColumn,boolean multipleSelection,
		 String whereClause)
	{
		this(WindowNo, tableName, keyColumn, multipleSelection, whereClause, true);
	}
		
	/**************************************************
     *  Detail Constructor
     * @param WindowNo  WindowNo
     * @param tableName tableName
     * @param keyColumn keyColumn
     * @param whereClause   whereClause
	 */
	protected InfoPanel (int WindowNo,
		String tableName, String keyColumn,boolean multipleSelection,
		 String whereClause, boolean lookup)
	{

		log.info("WinNo=" + p_WindowNo + " " + whereClause);
		p_WindowNo = WindowNo;
		p_tableName = tableName;
		p_keyColumn = keyColumn;
        p_multipleSelection = multipleSelection;
        m_lookup = lookup;
		
		if (whereClause == null || whereClause.indexOf('@') == -1)
			p_whereClause = whereClause;
		else
		{
			p_whereClause = Env.parseContext(Env.getCtx(), p_WindowNo, whereClause, false, false);
			if (p_whereClause.length() == 0)
				log.log(Level.SEVERE, "Cannot parse context= " + whereClause);
		}
		init();
		
		this.setAttribute(ITabOnSelectHandler.ATTRIBUTE_KEY, new ITabOnSelectHandler() {
			public void onSelect() {
				scrollToSelectedRow();
			}
		});
	}	//	InfoPanel
	
	private void init()
	{
		if (isLookup())
		{
			setAttribute(Window.MODE_KEY, Window.MODE_MODAL);
			setBorder("normal");
			setClosable(true);
			int height = SessionManager.getAppDesktop().getClientInfo().desktopHeight * 85 / 100;
    		int width = SessionManager.getAppDesktop().getClientInfo().desktopWidth * 80 / 100;
    		setWidth(width + "px");
    		setHeight(height + "px");
    		this.setContentStyle("overflow: auto");
		}
		else
		{
			setAttribute(Window.MODE_KEY, Window.MODE_EMBEDDED);
			setBorder("none");
			setWidth("100%");
			setHeight("100%");
			setStyle("position: absolute");
		}
		
        confirmPanel = new ConfirmPanel(true, true, false, true, true, true);  // Elaine 2008/12/16
        confirmPanel.addActionListener(Events.ON_CLICK, this);
        confirmPanel.setStyle("border-top: 2px groove #444; padding-top: 4px");
        
        // Elaine 2008/12/16
		confirmPanel.getButton(ConfirmPanel.A_CUSTOMIZE).setVisible(hasCustomize());
		confirmPanel.getButton(ConfirmPanel.A_HISTORY).setVisible(hasHistory());
		confirmPanel.getButton(ConfirmPanel.A_ZOOM).setVisible(hasZoom());		
		//
		if (!isLookup()) 
		{
			confirmPanel.getButton(ConfirmPanel.A_OK).setVisible(false);
		}
		
		// dREHER Jun 25 por ahora no activar...
		customizeButtons();
		
        this.setSizable(true);      
        this.setMaximizable(true);
        
        this.addEventListener(Events.ON_OK, this);

        contentPanel.setOddRowSclass(null);
	}  //  init
	protected ConfirmPanel confirmPanel;
	/** Master (owning) Window  */
	protected int				p_WindowNo;
	/** Table Name              */
	protected String            p_tableName;
	/** Key Column Name         */
	protected String            p_keyColumn;
	/** Enable more than one selection  */
	protected boolean			p_multipleSelection;
	/** Initial WHERE Clause    */
	protected String			p_whereClause = "";
	protected StatusBarPanel statusBar = new StatusBarPanel();
	/**                    */
    private List<Object> line;

	private boolean			    m_ok = false;
	/** Cancel pressed - need to differentiate between OK - Cancel - Exit	*/
	private boolean			    m_cancel = false;
	/** Result IDs              */
	private ArrayList<Integer>	m_results = new ArrayList<Integer>(3);
    
    private ListModelTable model;
	/** Layout of Grid          */
	protected ColumnInfo[]     p_layout;
	/** Main SQL Statement      */
	private String              m_sqlMain;
	/** Count SQL Statement		*/
	private String              m_sqlCount;
	/** Order By Clause         */
	private String              m_sqlOrder;
	private String              m_sqlUserOrder;
	/**ValueChange listeners       */
    private ArrayList<ValueChangeListener> listeners = new ArrayList<ValueChangeListener>();
	/** Loading success indicator       */
	protected boolean	        p_loadedOK = false;
	/**	SO Zoom Window						*/
	private int					m_SO_Window_ID = -1;
	/**	PO Zoom Window						*/
	private int					m_PO_Window_ID = -1;

	/**	Logger			*/
	protected CLogger log = CLogger.getCLogger(getClass());
	
	protected WListbox contentPanel = new WListbox();
	protected Paging paging;
	protected int pageNo;
	private int m_count;
	private int cacheStart;
	private int cacheEnd;
	private boolean m_useDatabasePaging = false;
	private BusyDialog progressWindow;

    /** Add Security Validation */
    private boolean addSecurityValidation = true;
	
	private static final String[] lISTENER_EVENTS = {};

	/** layout para excel de las columnas 
	 * dREHER Jun 25
	 * */
	protected LayoutXLS[]  p_layoutXLS;
	protected Grid grid;
	protected abstract Grid getFilterGrid();
	protected String title = null;

	/**
	 *  Loaded correctly
	 *  @return true if loaded OK
	 */
	public boolean loadedOK()
	{
		return p_loadedOK;
	}   //  loadedOK

	/**
	 *	Set Status Line
	 *  @param text text
	 *  @param error error
	 */
	public void setStatusLine (String text, boolean error)
	{
		statusBar.setStatusLine(text, error);
	}	//	setStatusLine

	/**
	 *	Set Status DB
	 *  @param text text
	 */
	public void setStatusDB (String text)
	{
		statusBar.setStatusDB(text);
	}	//	setStatusDB

	protected void prepareTable (ColumnInfo[] layout, 
            String from, 
            String where, 
            String orderBy)
	{
        String sql =contentPanel.prepareTable(layout, from,
                where,p_multipleSelection && m_lookup,
                getTableName(),false);
        p_layout = contentPanel.getLayout();
		m_sqlMain = sql;
		m_sqlCount = "SELECT COUNT(*) FROM " + from + " WHERE " + where;
		//
		m_sqlOrder = "";
		m_sqlUserOrder = "";
		if (orderBy != null && orderBy.length() > 0)
			m_sqlOrder = " ORDER BY " + orderBy;			
		
		
		layotXLSFromLayout(layout);
		
		debug("InfoPanel.sql main=" + m_sqlMain);
		debug("InfoPanel.sql count=" + m_sqlCount);
		debug("InfoPanel.sql order=" + m_sqlOrder);
		
	}   //  prepareTable

	
	/**
	 * Set Layout for XLS export
	 * 
	 * @param layout layout
	 */
	protected void layotXLSFromLayout(ColumnInfo[] layout) {
		
		if (layout == null || layout.length == 0) {
			p_layoutXLS = null;
			return;
		}

		p_layoutXLS = new LayoutXLS[layout.length];
		for (int i = 0; i < layout.length; i++) {
			debug("InfoPanel.layotXLSFromLayout: " + layout[i].getColHeader() + " - " + layout[i].getColClass());
			p_layoutXLS[i] = new LayoutXLS(layout[i].getColSQL(), // colSQL
											layout[i].getColClass().equals(BigDecimal.class)||layout[i].getColClass().equals(Double.class), // totalize 
											layout[i].getColClass().equals(Integer.class)||layout[i].getColClass().equals(Long.class), // count
											layout[i].getColHeader(), // title
											25, // width
											15, // height
											false, // bold
											"#000000" // color
											);
		}
		debug("InfoPanel.layotXLSFromLayout: " + p_layoutXLS.length + " columns");
		if (p_layoutXLS.length == 0) {
			debug("InfoPanel.layotXLSFromLayout: No columns defined");
		}
	} // layotXLSFromLayout
	

	private void debug(String string) {
		System.out.println("InfoPanel." + string);
	}

	/**************************************************************************
	 *  Execute Query
	 */
	protected void executeQuery()
	{
		line = new ArrayList<Object>();
		cacheStart = -1;
		cacheEnd = -1;

		testCount();
		m_useDatabasePaging = (m_count > 1000);
		if (m_useDatabasePaging)
		{			
			return ;
		}
		else
		{
			readLine(0, -1);
		}
	}
            
	private void readData(ResultSet rs) throws SQLException {
		int colOffset = 1;  //  columns start with 1
		List<Object> data = new ArrayList<Object>();
		for (int col = 0; col < p_layout.length; col++)
		{
			Object value = null;
			Class<?> c = p_layout[col].getColClass();
			int colIndex = col + colOffset;
			if (c == IDColumn.class)
			{
		        value = new IDColumn(rs.getInt(colIndex));
						
			}
			else if (c == Boolean.class)
		        value = new Boolean("Y".equals(rs.getString(colIndex)));
			else if (c == Timestamp.class)
		        value = rs.getTimestamp(colIndex);
			else if (c == BigDecimal.class)
		        value = rs.getBigDecimal(colIndex);
			else if (c == Double.class)
		        value = new Double(rs.getDouble(colIndex));
			else if (c == Integer.class)
		        value = new Integer(rs.getInt(colIndex));
			else if (c == KeyNamePair.class)
			{
				String display = rs.getString(colIndex);
				int key = rs.getInt(colIndex+1);
                value = new KeyNamePair(key, display);

				colOffset++;
			}
			else
			{
		        value = rs.getString(colIndex);
			}
			data.add(value);
		}
        line.add(data);
	}
            
    protected void renderItems()
    {
        if (m_count > 0)
        {
        	if (m_count > PAGE_SIZE)
        	{
        		if (paging == null) 
        		{
	        		paging = new Paging();
	    			paging.setPageSize(PAGE_SIZE);
	    			paging.setTotalSize(m_count);
	    			paging.setDetailed(true);
	    			paging.addEventListener(ZulEvents.ON_PAGING, this);
	    			insertPagingComponent();
        		}
        		else
        		{
        			paging.setTotalSize(m_count);
        			paging.setActivePage(0);
        		}
    			List<Object> subList = readLine(0, PAGE_SIZE);
    			model = new ListModelTable(subList);
    			model.setSorter(this);
	            model.addTableModelListener(this);
	            contentPanel.setData(model, null);
	            
	            pageNo = 0;
        	}
        	else
        	{
        		if (paging != null) 
        		{
        			paging.setTotalSize(m_count);
        			paging.setActivePage(0);
        			pageNo = 0;
        		}
	            model = new ListModelTable(readLine(0, -1));
	            model.setSorter(this);
	            model.addTableModelListener(this);
	            contentPanel.setData(model, null);
        	}
        }
        else {
        	// Si no hay registros que mostrar, limpiar las opciones a fin 
        	// de no visualizar el resultado de busquedas anteriores
            model = new ListModelTable();
            model.clear();
            model.addTableModelListener(this);
            contentPanel.setData(model, null);
        }
        	
       
        int no = m_count;
        setStatusLine(Integer.toString(no) + " " + Msg.getMsg(Env.getCtx(), "SearchRows_EnterQuery"), false);
        setStatusDB(Integer.toString(no));
                
        addDoubleClickListener();
    }
    
    private List<Object> readLine(int start, int end) {
    	//cacheStart & cacheEnd - 1 based index, start & end - 0 based index
    	if (cacheStart >= 1 && cacheEnd > cacheStart)
    	{
    		if (start+1 >= cacheStart && end+1 <= cacheEnd)
    		{
    			return end == -1 ? line : line.subList(start-cacheStart+1, end-cacheStart+2);
    		}
    	}

    	cacheStart = start + 1 - (PAGE_SIZE * 4);
    	if (cacheStart <= 0)
    		cacheStart = 1;

    	if (end == -1)
    	{
    		cacheEnd = m_count;
    	}
    	else
    	{
	    	cacheEnd = end + 1 + (PAGE_SIZE * 4);
	    	if (cacheEnd > m_count)
	    		cacheEnd = m_count;
    	}

    	line = new ArrayList<Object>();

    	PreparedStatement m_pstmt = null;
		ResultSet m_rs = null;

		long startTime = System.currentTimeMillis();
			//

        String dynWhere = getSQLWhere();
        StringBuffer sql = new StringBuffer (m_sqlMain);
        if (dynWhere.length() > 0)
            sql.append(dynWhere);   //  includes first AND
        if (m_sqlUserOrder != null && m_sqlUserOrder.trim().length() > 0)
        	sql.append(m_sqlUserOrder);
        else
        	sql.append(m_sqlOrder);
        String dataSql = Msg.parseTranslation(Env.getCtx(), sql.toString());    //  Variables
        if(isAddSecurityValidation()){
        	dataSql = MRole.getDefault().addAccessSQL(dataSql, getTableName(), MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
        }
        if (end > start && m_useDatabasePaging && DB.getDatabase().isPagingSupported())
        {
        	dataSql = DB.getDatabase().addPagingSQL(dataSql, cacheStart, cacheEnd);
        }
        log.finer(dataSql);
		try
		{

			m_pstmt = DB.prepareStatement(dataSql, null);
			setParameters (m_pstmt, false);	//	no count
			log.fine("Start query - " + (System.currentTimeMillis()-startTime) + "ms");
			m_pstmt.setFetchSize(100);
			m_rs = m_pstmt.executeQuery();
			log.fine("End query - " + (System.currentTimeMillis()-startTime) + "ms");
			//skips the row that we dont need if we can't use native db paging
			if (end > start && m_useDatabasePaging && !DB.getDatabase().isPagingSupported())
			{
				for (int i = 0; i < cacheStart - 1; i++)
				{
					if (!m_rs.next())
						break;
				}
			}

			int rowPointer = cacheStart-1;
			while (m_rs.next())
			{
				rowPointer++;
				readData(m_rs);
				//check now of rows loaded, break if we hit the suppose end
				if (m_useDatabasePaging && rowPointer >= cacheEnd)
				{
					break;
				}
			}
		}

		catch (SQLException e)
		{
			log.log(Level.SEVERE, dataSql, e);
		}

		finally
		{
			DB.close(m_rs, m_pstmt);
		}

		return line;
	}

    private void addDoubleClickListener() {
		Iterator<?> i = contentPanel.getListenerIterator(Events.ON_DOUBLE_CLICK);
		while (i.hasNext()) {
			if (i.next() == this)
				return;
		}
		contentPanel.addEventListener(Events.ON_DOUBLE_CLICK, this);
	}
    
    protected void insertPagingComponent() {
		contentPanel.getParent().insertBefore(paging, contentPanel.getNextSibling());
	}
    
    public Vector<String> getColumnHeader(ColumnInfo[] p_layout)
    {
        Vector<String> columnHeader = new Vector<String>();
        
        for (ColumnInfo info: p_layout)
        {
             columnHeader.add(info.getColHeader());
        }
        return columnHeader;
    }
	/**
	 * 	Test Row Count
	 *	@return true if display
	 */
	private boolean testCount()
	{
		long start = System.currentTimeMillis();
		String dynWhere = getSQLWhere();
		StringBuffer sql = new StringBuffer (m_sqlCount);
		
		if (dynWhere.length() > 0)
			sql.append(dynWhere);   //  includes first AND
		
		String countSql = Msg.parseTranslation(Env.getCtx(), sql.toString());	//	Variables
		if(isAddSecurityValidation()){
			countSql = MRole.getDefault().addAccessSQL	(countSql, getTableName(),MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
		}
		log.finer(countSql);
		m_count = -1;
		
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(countSql, null);
			setParameters (pstmt, true);
			ResultSet rs = pstmt.executeQuery();
		
			if (rs.next())
				m_count = rs.getInt(1);
			
			rs.close();
			pstmt.close();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, countSql, e);
			m_count = -2;
		}
		
		log.fine("#" + m_count + " - " + (System.currentTimeMillis()-start) + "ms");
		
		//Armen: add role checking (Patch #1694788 )
		//MRole role = MRole.getDefault(); 		
		//if (role.isQueryMax(no))
		//	return ADialog.ask(p_WindowNo, this, "InfoHighRecordCount", String.valueOf(no));
		
		return true;
	}	//	testCount
			

	/**
	 *	Save Selection	- Called by dispose
	 */
	protected void saveSelection ()
	{
		//	Already disposed
		if (contentPanel == null)
			return;

		log.config( "OK=" + m_ok);
		
		if (!m_ok)      //  did not press OK
		{
			m_results.clear();
			contentPanel = null;
			this.detach();
            return;
		}

		//	Multi Selection
		if (p_multipleSelection)
		{
			m_results.addAll(getSelectedRowKeys());
		}
		else    //  singleSelection
		{
			Integer data = getSelectedRowKey();
			if (data != null)
				m_results.add(data);
		}
		
		log.config(getSelectedSQL());

		//	Save Settings of detail info screens
		saveSelectionDetail();
		
	}	//	saveSelection

	/**
	 *  Get the key of currently selected row
	 *  @return selected key
	 */
	public Integer getSelectedRowKey()
	{
		Integer key = contentPanel.getSelectedRowKey();
		
		return key;        
	}   //  getSelectedRowKey
	
	/**
     *  Get the keys of selected row/s based on layout defined in prepareTable
     *  @return IDs if selection present
     *  @author ashley
     */
    public ArrayList<Integer> getSelectedRowKeys()
    {
        ArrayList<Integer> selectedDataList = new ArrayList<Integer>();
        
        if (contentPanel.getKeyColumnIndex() == -1)
        {
            return selectedDataList;
        }
        
        if (p_multipleSelection)
        {
        	int[] rows = contentPanel.getSelectedIndices();
            for (int row = 0; row < rows.length; row++)
            {
                Object data = contentPanel.getModel().getValueAt(rows[row], contentPanel.getKeyColumnIndex());
                if (data instanceof IDColumn)
                {
                    IDColumn dataColumn = (IDColumn)data;
                    selectedDataList.add(dataColumn.getRecord_ID());
                }
                else
                {
                    log.severe("For multiple selection, IDColumn should be key column for selection");
                }
            }
        }
        
        if (selectedDataList.size() == 0)
        {
        	int row = contentPanel.getSelectedRow();
    		if (row != -1 && contentPanel.getKeyColumnIndex() != -1)
    		{
    			Object data = contentPanel.getModel().getValueAt(row, contentPanel.getKeyColumnIndex());
    			if (data instanceof IDColumn)
    				selectedDataList.add(((IDColumn)data).getRecord_ID());
    			if (data instanceof Integer)
    				selectedDataList.add((Integer)data);
    		}
        }
      
        return selectedDataList;
    }   //  getSelectedRowKeys

	/**
	 *	Get selected Keys
	 *  @return selected keys (Integers)
	 */
	public Object[] getSelectedKeys()
	{
		if (!m_ok || m_results.size() == 0)
			return null;
		return m_results.toArray(new Integer[0]);
	}	//	getSelectedKeys;

	/**
	 *	Get (first) selected Key
	 *  @return selected key
	 */
	public Object getSelectedKey()
	{
		if (!m_ok || m_results.size() == 0)
			return null;
		return m_results.get(0);
	}	//	getSelectedKey

	/**
	 *	Is cancelled?
	 *	- if pressed Cancel = true
	 *	- if pressed OK or window closed = false
	 *  @return true if cancelled
	 */
	public boolean isCancelled()
	{
		return m_cancel;
	}	//	isCancelled

	/**
	 *	Get where clause for (first) selected key
	 *  @return WHERE Clause
	 */
	public String getSelectedSQL()
	{
		//	No results
		Object[] keys = getSelectedKeys();
		if (keys == null || keys.length == 0)
		{
			log.config("No Results - OK=" 
				+ m_ok + ", Cancel=" + m_cancel);
			return "";
		}
		//
		StringBuffer sb = new StringBuffer(getKeyColumn());
		if (keys.length > 1)
			sb.append(" IN (");
		else
			sb.append("=");

		//	Add elements
		for (int i = 0; i < keys.length; i++)
		{
			if (getKeyColumn().endsWith("_ID"))
				sb.append(keys[i].toString()).append(",");
			else
				sb.append("'").append(keys[i].toString()).append("',");
		}

		sb.replace(sb.length()-1, sb.length(), "");
		if (keys.length > 1)
			sb.append(")");
		return sb.toString();
	}	//	getSelectedSQL;

		
		
	/**
	 *  Get Table name Synonym
	 *  @return table name
	 */
	protected String getTableName()
	{
		return p_tableName;
	}   //  getTableName

	/**
	 *  Get Key Column Name
	 *  @return column name
	 */
	protected String getKeyColumn()
	{
		return p_keyColumn;
	}   //  getKeyColumn

	
	public String[] getEvents()
    {
        return InfoPanel.lISTENER_EVENTS;
    }
	
	// Elaine 2008/11/28
	/**
	 *  Enable OK, History, Zoom if row/s selected
     *  ---
     *  Changes: Changed the logic for accomodating multiple selection
     *  @author ashley
	 */
	protected void enableButtons ()
	{
		boolean enable = (contentPanel.getSelectedCount() == 1);
		confirmPanel.getOKButton().setEnabled(contentPanel.getSelectedCount() > 0);
		
		if (hasHistory())
			confirmPanel.getButton(ConfirmPanel.A_HISTORY).setEnabled(enable);
		if (hasZoom())
			confirmPanel.getButton(ConfirmPanel.A_ZOOM).setEnabled(enable);
	}   //  enableButtons
	//
		
	/**************************************************************************
	 *  Get dynamic WHERE part of SQL
	 *	To be overwritten by concrete classes
	 *  @return WHERE clause
	 */
	protected abstract String getSQLWhere();
      	
	/**
	 *  Set Parameters for Query
	 *	To be overwritten by concrete classes
	 *  @param pstmt statement
	 *  @param forCount for counting records
	 *  @throws SQLException
	 */
	protected abstract void setParameters (PreparedStatement pstmt, boolean forCount) 
		throws SQLException;
    /**
     * notify to search editor of a value change in the selection info
     * @param event event
    *
     */

	protected void showHistory()					{}
	/**
	 *  Has History (false)
	 *	To be overwritten by concrete classes
	 *  @return true if it has history (default false)
	 */
	protected boolean hasHistory()				{return false;}
	/**
	 *  Customize dialog
	 *	To be overwritten by concrete classes
	 */
	protected void customize()					{}
	/**
	 *  Has Customize (false)
	 *	To be overwritten by concrete classes
	 *  @return true if it has customize (default false)
	 */
	protected boolean hasCustomize()				{return false;}
	/**
	 *  Has Zoom (false)
	 *	To be overwritten by concrete classes
	 *  @return true if it has zoom (default false)
	 */
	protected boolean hasZoom()					{return false;}
	/**
	 *  Save Selection Details
	 *	To be overwritten by concrete classes
	 */
	protected void saveSelectionDetail()          {}

	/**
	 * 	Get Zoom Window
	 *	@param tableName table name
	 *	@param isSOTrx sales trx
	 *	@return AD_Window_ID
	 */
	protected int getAD_Window_ID (String tableName, boolean isSOTrx)
	{
		if (!isSOTrx && m_PO_Window_ID > 0)
			return m_PO_Window_ID;
		if (m_SO_Window_ID > 0)
			return m_SO_Window_ID;
		//
		String sql = "SELECT AD_Window_ID, PO_Window_ID FROM AD_Table WHERE TableName=?";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setString(1, tableName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				m_SO_Window_ID = rs.getInt(1);
				m_PO_Window_ID = rs.getInt(2);
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		try
		{
			if (pstmt != null)
				pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}
		//
		if (!isSOTrx && m_PO_Window_ID > 0)
			return m_PO_Window_ID;
		return m_SO_Window_ID;
	}	//	getAD_Window_ID
    
    public void onEvent(Event event)
    {
        if  (event!=null)
        {
            if (event.getTarget().equals(confirmPanel.getButton(ConfirmPanel.A_OK)))
            {
                onOk();
            }
            else if (event.getTarget() == contentPanel && event.getName().equals(Events.ON_DOUBLE_CLICK))
            {
            	onDoubleClick();
            }
            else if (event.getTarget().equals(confirmPanel.getButton(ConfirmPanel.A_REFRESH)))
            {
            	showBusyDialog();
            	Clients.response(new AuEcho(this, "onQueryCallback", null));
            }
            else if (event.getTarget().equals(confirmPanel.getButton(ConfirmPanel.A_CANCEL)))
            {
            	m_cancel = true;
                dispose(false);
            }
            // Elaine 2008/12/16
            else if (event.getTarget().equals(confirmPanel.getButton(ConfirmPanel.A_HISTORY)))
            {
            	if (!contentPanel.getChildren().isEmpty() && contentPanel.getSelectedRowKey()!=null)
                {
            		showHistory();
                }
            }
    		else if (event.getTarget().equals(confirmPanel.getButton(ConfirmPanel.A_CUSTOMIZE)))
    		{
            	if (!contentPanel.getChildren().isEmpty() && contentPanel.getSelectedRowKey()!=null)
                {
            		customize();
                }
    		}
            //
            else if (event.getTarget().equals(confirmPanel.getButton(ConfirmPanel.A_ZOOM)))
            {
                if (!contentPanel.getChildren().isEmpty() && contentPanel.getSelectedRowKey()!=null)
                {
                    zoom();
                    if (isLookup())
                    	this.detach();
                }
            }
            else if (event.getTarget() == paging)
            {
            	int pgNo = paging.getActivePage();
            	if (pageNo != pgNo) 
            	{
            	
            		contentPanel.clearSelection();
    			
            		pageNo = pgNo;
            		int start = pageNo * PAGE_SIZE;
            		int end = start + PAGE_SIZE;
            		List<Object> subList = readLine(start, end);
        			model = new ListModelTable(subList);
        			model.setSorter(this);
    	            model.addTableModelListener(this);
    	            contentPanel.setData(model, null);
    	            
    				contentPanel.setSelectedIndex(0);
    			}
            }
            //default
            else
            {
            	showBusyDialog();
            	Clients.response(new AuEcho(this, "onQueryCallback", null));
            }
        }
    }  //  onEvent

	private void showBusyDialog() {
		progressWindow = new BusyDialog();
		progressWindow.setPage(this.getPage());
		progressWindow.doHighlighted();
	}

	private void hideBusyDialog() {
		if(progressWindow != null) {
			progressWindow.dispose();
			progressWindow = null;
		}
	}

    public void onQueryCallback()
    {
    	try
    	{
            	executeQuery();
                renderItems();
            }
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	finally
    	{
    		hideBusyDialog();
    	}
    }
    
    private void onOk() 
    {
		if (!contentPanel.getChildren().isEmpty() && contentPanel.getSelectedRowKey()!=null)
		{
		    dispose(true);
		}
	}
    
    private void onDoubleClick()
	{
		if (isLookup())
		{
			dispose(true);
		}
		else
		{
			zoom();
		}

	}

    public void tableChanged(WTableModelEvent event)
    {
    	enableButtons();
    }
    
    public void zoom()
    {
    	if (listeners != null && listeners.size() > 0)
    	{
	        ValueChangeEvent event = new ValueChangeEvent(this,"zoom",
	                   contentPanel.getSelectedRowKey(),contentPanel.getSelectedRowKey());
	        fireValueChange(event);
    	}
    	else
    	{
    		Integer recordId = contentPanel.getSelectedRowKey();
    		int AD_Table_ID = M_Table.getTableID(p_tableName);
    		if (AD_Table_ID <= 0)
    		{
    			if (p_keyColumn.endsWith("_ID")) 
    			{
    				AD_Table_ID = M_Table.getTableID(p_keyColumn.substring(0, p_keyColumn.length() - 3));
    			}
    		}
    		if (AD_Table_ID > 0)
    			AEnv.zoom(AD_Table_ID, recordId);
    	}
    }
    
    public void addValueChangeListener(ValueChangeListener listener)
    {
        if (listener == null)
        {
            return;
        }
        
        listeners.add(listener);
    }
        
    public void fireValueChange(ValueChangeEvent event)
    {
        for (ValueChangeListener listener : listeners)
        {
           listener.valueChange(event);
        }
    }
    /**
     *  Dispose and save Selection
     *  @param ok OK pressed
     */
    public void dispose(boolean ok)
    {
        log.config("OK=" + ok);
        m_ok = ok;

        //  End Worker
        if (isLookup())
        {
        	saveSelection();
        }
        if (Window.MODE_EMBEDDED.equals(getAttribute(Window.MODE_KEY)))
        	SessionManager.getAppDesktop().closeActiveWindow();
        else
	        this.detach();
    }   //  dispose
        
	public void sort(Comparator cmpr, boolean ascending) {
		WListItemRenderer.ColumnComparator lsc = (WListItemRenderer.ColumnComparator) cmpr;
		if (m_useDatabasePaging)
		{
			int col = lsc.getColumnIndex();
			String colsql = p_layout[col].getColSQL().trim();
			int lastSpaceIdx = colsql.lastIndexOf(" ");
			if (lastSpaceIdx > 0)
			{
				String tmp = colsql.substring(0, lastSpaceIdx).trim();
				char last = tmp.charAt(tmp.length() - 1);
				if (tmp.toLowerCase().endsWith("as"))
				{
					colsql = colsql.substring(lastSpaceIdx).trim();
				}
				else if (!(last == '*' || last == '-' || last == '+' || last == '/' || last == '>' || last == '<' || last == '='))
				{
					tmp = colsql.substring(lastSpaceIdx).trim();
					if (tmp.startsWith("\"") && tmp.endsWith("\""))
					{
						colsql = colsql.substring(lastSpaceIdx).trim();
					}
					else
					{
						boolean hasAlias = true;
						for(int i = 0; i < tmp.length(); i++)
						{
							char c = tmp.charAt(i);
							if (Character.isLetterOrDigit(c))
							{
								continue;
							}
							else
							{
								hasAlias = false;
								break;
							}
						}
						if (hasAlias)
						{
							colsql = colsql.substring(lastSpaceIdx).trim();
						}
					}
				}
			}
			m_sqlUserOrder = " ORDER BY " + colsql;
			if (!ascending)
				m_sqlUserOrder += " DESC ";
			executeQuery();
			renderItems();
		}
		else
		{
			Collections.sort(line, lsc);
			renderItems();
		}
	}

    public boolean isLookup()
    {
    	return m_lookup;
    }

    public void scrollToSelectedRow()
    {
    	if (contentPanel != null && contentPanel.getSelectedIndex() >= 0) {
    		Listitem selected = contentPanel.getItemAtIndex(contentPanel.getSelectedIndex());
    		if (selected != null) {
    			selected.focus();
    		}
    	}
    }
    
    /**
     *  Get SQL WHERE parameter
     *  @param f field
     *  @return sql
     */
    protected String getSQLText (Textbox f)
    {
        String s = f.getText().toUpperCase();
        if (!s.endsWith("%"))
            s += "%";
        log.fine("String=" + s);
        return s;
    }   //  getSQLText
    
    protected boolean isAddSecurityValidation() {
		return addSecurityValidation;
	}

	protected void setAddSecurityValidation(boolean addSecurityValidation) {
		this.addSecurityValidation = addSecurityValidation;
	}
	
	// dREHER Jun 25
	private void exportarCSV() {
	    try {
	        StringBuilder sb = new StringBuilder();

	        // Encabezado
	        List<String> columnNames = getColumnNames(); // método heredado de InfoZK
	        int col  = 0;
	        for (String columnName : columnNames) {
	        	if(col > 1)
	        		sb.append(columnName).append(";");
	        	col++;
	        }
	        sb.append("\n");

	        // Datos
	        ListModelTable model = getTableModel(); // también heredado de InfoZK
	        for (int row = 0; row < model.getSize(); row++) {
	            ArrayList<Object> rowData = (ArrayList<Object>) model.getElementAt(row);
	            col  = 0;
	            for (Object value : rowData) {
	            	if(col > 1)
	            		sb.append(value != null ? value.toString().replace(";", ",") : "").append(";");
	            	col++;
	            }
	            sb.append("\n");
	        }

	        // Convertir a archivo y lanzar descarga
	        String fileName = "export_" + p_tableName.trim().replace(" ", "_") + "_" + Env.getDate(Env.getCtx()) + ".csv";
	        AMedia media = new AMedia(fileName, "csv", "text/csv",
	                sb.toString().getBytes("UTF-8"));

	        Filedownload.save(media);
	    } catch (Exception e) {
	        FDialog.error(0, this, "Error al exportar CSV: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	// dREHER Jun 25
	private List<String> getColumnNames() {
		ArrayList<String> names = new ArrayList<String>();
		ColumnInfo[] ci = contentPanel.getLayout();
		for(ColumnInfo i: ci) {
			names.add(i.getColHeader());
		}
		return names;
	}
	
	/*
	 * Apache POI en el classpath:

		Para XLSX necesitás al menos:

		poi-*.jar

		poi-ooxml-*.jar

		commons-collections4

		xmlbeans

		ooxml-schemas

		En Libertya ya se incluye poi-3.9 ?
	 */
	
	
	private void exportarXLSX() {
	    try {
	        // Crear workbook
	        XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("Exportado desde Libertya");

	        List<String> columnNames = getColumnNames();
	        ListModelTable model = getTableModel();

	        // Estilo para encabezados
	        XSSFCellStyle headerStyle = workbook.createCellStyle();
	        headerStyle.setFont(workbook.createFont());
	        headerStyle.getFont().setBold(true);
	        headerStyle.setAlignment(HorizontalAlignment.CENTER);

	        // Agregar el título de la ventana como la primera fila
	        String translatedTableName = getTitleXLS(); // Obtener el título de la ventana

	        // Formatear la fecha actual
	        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());

	        // Construir descripción de los filtros aplicados
	        String appliedFilters = "Filtro Aplicado: " + getFilterDescription();
			if (appliedFilters.isEmpty()) {
				appliedFilters = "No se aplicaron filtros.";
			}
	        // Título
	        int rowNum = 0;
	        XSSFRow titleRow = sheet.createRow(rowNum++);
	        XSSFCell titleCell = titleRow.createCell(0);
	        titleCell.setCellValue("Exportación de " + translatedTableName);
	        titleCell.setCellStyle(headerStyle);
	        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, columnNames.size())); // Combinar celdas

	        // Fecha actual
	        XSSFRow dateRow = sheet.createRow(rowNum++);
	        XSSFCell dateCell = dateRow.createCell(0);
	        dateCell.setCellValue("Fecha de exportación: " + formattedDate);
	        dateCell.setCellStyle(headerStyle);
	        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, columnNames.size())); // Combinar celdas

	        // Filtros aplicados
	        XSSFRow filterRow = sheet.createRow(rowNum++);
	        XSSFCell filterCell = filterRow.createCell(0);
	        filterCell.setCellValue(appliedFilters);
	        filterCell.setCellStyle(headerStyle);
	        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 0, columnNames.size())); // Combinar celdas
	        // Espacio en blanco
	        rowNum++;

	        // Encabezados
	        XSSFRow headerRow = sheet.createRow(rowNum++);
	        int col = 0;
	        int columna = 0;
	        
	        BigDecimal[] totals = new BigDecimal[p_layoutXLS.length];
	        BigDecimal[] counts = new BigDecimal[p_layoutXLS.length];
	        
	        
	        for (int i = 0; i < columnNames.size(); i++) {
	        	if(i>1) {
	        		XSSFCell cell = headerRow.createCell(columna);
	        		cell.setCellValue(columnNames.get(col));
					XSSFCellStyle style = workbook.createCellStyle();
					style.setFont(workbook.createFont());
					style.getFont().setBold(true);
					style.getFont().setColor(IndexedColors.WHITE.getIndex());
					style.setAlignment(HorizontalAlignment.CENTER);
			        style.setFillPattern(FillPatternType.BIG_SPOTS);
			        style.setBorderBottom(BorderStyle.THIN);
			        style.setFillBackgroundColor(IndexedColors.AQUA.index);
			        headerRow.setRowStyle(style);
					cell.setCellStyle(style);
	        		columna++;
	        	}
	        	col++;
	        }
	        
	        // Inicializar totales y conteos
			for (int i = 0; i < p_layoutXLS.length; i++) {
				if (p_layoutXLS[i].isTotalize()) {
					totals[i] = BigDecimal.ZERO;
				}
				if (p_layoutXLS[i].isCount()) {
					counts[i] = BigDecimal.ZERO;
				}
			}
			
			int round = MCurrency.getStdPrecision(Env.getCtx(), Env.getC_Currency_ID(Env.getCtx()));

	        // Filas de datos
	        for (int row = 0; row < model.getSize(); row++) {
	        	ArrayList<Object> rowData = (ArrayList<Object>) model.getElementAt(row);
	            col  = 0;
	            XSSFRow dataRow = sheet.createRow(rowNum++);
	            columna = 0;
	            for (Object value : rowData) {
	            	if(col > 1) {
	            		
	            		boolean isNumber = false;
	            		boolean isDate = false;
	            		
	            		XSSFCell cell = dataRow.createCell(columna);
	            		if (value != null) {
	            			
	            			if(value instanceof Date || value instanceof Timestamp) 
	            				isDate = true;
	            			
							if (value instanceof BigDecimal || value instanceof Double || value instanceof Integer) {
								isNumber = true;
							}
	            			
							if(isDate) {
								cell.setCellValue((Timestamp)value);
							}else if(isNumber) {
								
								if (value instanceof BigDecimal) {
									cell.setCellValue(((BigDecimal) value).setScale(round).doubleValue());
								} else if (value instanceof Integer) {
									cell.setCellValue(((Integer) value).doubleValue());
								} else if (value instanceof Double) {
									cell.setCellValue((Double) value);
								} else
									cell.setCellValue((Double)value);
							}else {
								String valorFinal = value.toString();
		            			valorFinal = valorFinal.equals("true") ? "Si": valorFinal.equals("false") ? "No" : valorFinal;
		            			cell.setCellValue(valorFinal);	
							}
	            		}
	            		
						if (p_layoutXLS[col].isTotalize()) {
							if (value instanceof BigDecimal) {
								totals[col] = totals[col].add((BigDecimal) value);
							} else if (value instanceof Double) {
								totals[col] = totals[col].add(BigDecimal.valueOf((Double) value));
							}
							isNumber = true;
						}
						
						if (p_layoutXLS[col].isCount()) {
							if (value instanceof BigDecimal) {
                                counts[col] = counts[col].add(new BigDecimal(1));
                            } else if (value instanceof Integer) {
                                counts[col] = counts[col].add(new BigDecimal(1));
                            } else if (value instanceof Double) {
                                counts[col] = counts[col].add(new BigDecimal(1));
                            } else {
                                counts[col] = counts[col].add(BigDecimal.ONE);
                            }
							isNumber = true;
                        }
                		
						XSSFCellStyle style = workbook.createCellStyle();
						style.setFont(workbook.createFont());
						
						if (p_layoutXLS[col].isBold()) {
							style.getFont().setBold(true);
						}

						if (isNumber) {
							setLocalizedNumberFormat(workbook, style);
						}
						
						if (isDate) {
							setLocalizedDateFormat(workbook, style);
						}
						
						if (p_layoutXLS[col].getColWidth() > 0) {
							style.setWrapText(true);
						}
						
						cell.setCellStyle(style);

						// Ajustar el ancho de la columna
						if (p_layoutXLS[col].getColWidth() > 0) {
							sheet.setColumnWidth(columna, p_layoutXLS[columna].getColWidth() * 256); // ancho en caracteres
						} else {
							sheet.autoSizeColumn(columna);
						}
	            		
	            		columna++;
	            	}
	            	col++;
	            }
	        }
	        
	        // Totales
	        XSSFRow totalRow = sheet.createRow(rowNum++);
	        
	        columna = 0;
			for (int i = 0; i < p_layoutXLS.length; i++) {
				
				if(i>1) {

					XSSFCell  cell = null;
					boolean isNumber = false;
					if (p_layoutXLS[i].isTotalize()) {
						cell = totalRow.createCell(columna);
						cell.setCellValue(((BigDecimal) totals[i]).setScale(round).doubleValue());
						isNumber = true;
					} else if (p_layoutXLS[i].isCount()) {
						cell = totalRow.createCell(columna);
						cell.setCellValue(((BigDecimal) counts[i]).setScale(round).doubleValue());
						isNumber = true;
					} else {
						cell = totalRow.createCell(columna);
						cell.setCellValue("");
					}
					
					XSSFCellStyle style = workbook.createCellStyle();
					style.setFont(workbook.createFont());
					style.getFont().setBold(true);
					style.getFont().setColor(IndexedColors.WHITE.getIndex());
			        style = getCellStyleBackgroundColorWithPattern(style);
			        style.setBorderTop(BorderStyle.THIN);
			        totalRow.setRowStyle(style);
					if (isNumber) {
						setLocalizedNumberFormat(workbook, style);
					}
					cell.setCellStyle(style);

					columna++;

				}
			}


			// Ajustar el ancho de las columnas después de escribir los datos
			for (int colIndex = 0; colIndex < columnNames.size(); colIndex++) {
			    sheet.autoSizeColumn(colIndex);
			}

			
	        // Convertir a byte[]
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        workbook.write(bos);
	        workbook.close();

	        byte[] data = bos.toByteArray();

	        // Descargar
	        String fileName = "export_" + p_tableName.trim().replace(" ", "_") + "_" + Env.getDate(Env.getCtx()) + ".xlsx";
	        AMedia media = new AMedia(fileName, "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data);
	        Filedownload.save(media);
	    } catch (Exception e) {
	        FDialog.error(0, this, "Error al exportar XLSX: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	// dREHER Jun 25
	private String getTitleXLS() {
		if (getTitle() != null && !getTitle().isEmpty()) {
			return getTitle();
		}
		return title != null && !title.isEmpty() ? title : p_tableName != null ? p_tableName : "Ventana de Informacion Libertya";
	}

	private String getFilterDescription() {
		StringBuilder description = new StringBuilder();

		String lastLabel = "";
		
		// Obtener las filas del Grid
		for (Object rowObject : grid.getRows().getChildren()) {
			if (rowObject instanceof Component) {
				Component rowComponent = (Component) rowObject;

				if (rowComponent instanceof Row) {
					Row row = (Row) rowComponent;

					// Recorrer los componentes de cada fila
					for (Object componentObject : row.getChildren()) {
						if (componentObject instanceof Component) {
							Component component = (Component) componentObject;

							// debug("Component: " + component.getClass().getSimpleName());
							
							if (component instanceof Textbox) {
								Textbox textbox = (Textbox) component;
								if (textbox.getValue() != null && !textbox.getValue().isEmpty()) {
									if (description.length() > 0) {
										description.append(", ");
									}
									description.append(lastLabel).append("=").append(textbox.getValue());
								}
							} else if (component instanceof Checkbox) {
								Checkbox checkbox = (Checkbox) component;
								if (checkbox.isChecked()) {
									if (description.length() > 0) {
										description.append(", ");
									}
									description.append(lastLabel).append("=Sí");
								}
							} else if (component instanceof Combobox) {
								Combobox combobox = (Combobox) component;
								if (combobox.getSelectedItem() != null) {
									String selectedValue = combobox.getSelectedItem().getLabel();
									if (selectedValue != null && !selectedValue.isEmpty()) {
										if (description.length() > 0) {
											description.append(", ");
										}
										description.append(lastLabel).append("=").append(selectedValue);
									}
								}
							} else if (component instanceof Listbox) {
								Listbox combobox = (Listbox) component;
								if (combobox.getSelectedItem() != null) {
									String selectedValue = combobox.getSelectedItem().getLabel();
									if (selectedValue != null && !selectedValue.isEmpty()) {
										if (description.length() > 0) {
											description.append(", ");
										}
										description.append(lastLabel).append("=").append(selectedValue);
									}
								}
							} else if (component instanceof Searchbox) {
								Searchbox searchbox = (Searchbox) component;
								if (searchbox.getText() != null) {
									String selectedValue = searchbox.getText();
									if (selectedValue != null && !selectedValue.isEmpty()) {
										if (description.length() > 0) {
											description.append(", ");
										}
										description.append(lastLabel).append("=").append(selectedValue);
									}
								}
							} else if (component instanceof NumberBox) {
								NumberBox numberbox = (NumberBox) component;
								if (numberbox.getValue() != null) {
									if (description.length() > 0) {
										description.append(", ");
									}
									description.append(lastLabel).append("=").append(numberbox.getValue());
								}
							} else if (component instanceof Datebox) {
								Datebox datebox = (Datebox) component;
								if (datebox.getValue() != null) {
									if (description.length() > 0) {
										description.append(", ");
									}
									description.append(lastLabel).append("=").append(datebox.getValue());
								}
							} else if (component instanceof Label) {
								Label label = (Label) component;
								if (label.getValue() != null) {
									if (description.length() > 0) {
										description.append(", ");
									}
									description.append(lastLabel).append(label.getValue());
								}
							} else if (component instanceof Div) {
								Div div = (Div) component;
								if (div.getFirstChild() instanceof Label) {
									Label label = (Label) div.getFirstChild();
									lastLabel = label.getValue();
								}
							}
						}
					}
				}
			}
		}

		// Si no hay filtros aplicados
		if (description.length() == 0) {
			description.append("No se aplicaron filtros.");
		}

		return description.toString();
	}

	
	private void setLocalizedNumberFormat(Workbook workbook, XSSFCellStyle style) {
		
		// Configurar el Locale para español de Argentina
	    Locale locale = new Locale("es", "AR");

	    // Crear un formato numérico con separadores de miles y decimales adecuados
	    String pattern = "#,##0.00"; // Separador de miles con punto y decimal con coma

	    // Configurar el formato en el estilo de la celda
	    DataFormat dataFormat = workbook.createDataFormat();
	    style.setAlignment(HorizontalAlignment.RIGHT);
	    style.setDataFormat(dataFormat.getFormat(pattern));
	}
	
	private void setLocalizedDateFormat(Workbook workbook, XSSFCellStyle style) {
		
		// Configurar el Locale para español de Argentina
	    Locale locale = new Locale("es", "AR");

	    // Crear un formato de fecha adecuado
	    String pattern = "dd/MM/yyyy"; // dias, mes y año

	    // Configurar el formato en el estilo de la celda
	    DataFormat dataFormat = workbook.createDataFormat();
	    style.setAlignment(HorizontalAlignment.LEFT);
	    style.setDataFormat(dataFormat.getFormat(pattern));
	}

	public XSSFCellStyle getCellStyleBackgroundColorWithPattern(XSSFCellStyle cellStyle) {
	    // Cambiar el color de fondo de la celda con un patrón
	    cellStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.index);
	    cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	    
	    return cellStyle;
	}
	

	// dREHER Jun 25
	protected ListModelTable getTableModel() {
		return model;
	};

	protected void customizeButtons() {
		// Crear un contenedor para los botones
		Hbox buttonContainer = new Hbox();
		buttonContainer.setSpacing("5px"); // Ajustar el espaciado entre botones

		// Botón Exportar CSV
		Button btnExport = new Button("Exportar");
		btnExport.setTooltiptext("Exportar resultados a CSV");
		btnExport.setImage("/images/Export24.png");
		LayoutUtils.addSclass("action-text-button", btnExport);
		btnExport.addEventListener(Events.ON_CLICK, ev -> exportarCSV());
		buttonContainer.appendChild(btnExport);

		// Botón Exportar XLSX
		Button btnXLSX = new Button("Exportar XLSX");
		btnXLSX.setImage("/images/Export24.png");
		btnXLSX.setTooltiptext("Exportar resultados a Excel");
		LayoutUtils.addSclass("action-text-button", btnXLSX);
		btnXLSX.addEventListener(Events.ON_CLICK, ev -> exportarXLSX());
		buttonContainer.appendChild(btnXLSX);

		// Insertar el contenedor antes del último botón de confirmPanel
		confirmPanel.insertBefore(buttonContainer, (Component) confirmPanel.getChildren().get(confirmPanel.getChildren().size() - 1));
	}
	
}	//	Info
