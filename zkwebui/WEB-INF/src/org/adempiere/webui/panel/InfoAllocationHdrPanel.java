package org.adempiere.webui.panel;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelListener;
import org.openXpertya.apps.search.Info_Column;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Div;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;

public class InfoAllocationHdrPanel extends InfoPanel implements EventListener,ValueChangeListener, WTableModelListener {
	
	//private Label lBPartner ;
	//private Textbox fBPartner ;
	private WEditor fBPartner;
	private Label lDocumentNo;
	private Textbox fDocumentNo ;

	/**	Logger			*/
	protected CLogger log = CLogger.getCLogger(getClass());
	private Borderlayout layout;
	private Vbox southBody;
	
	/** SalesOrder Trx          */
	private boolean 		m_isSOTrx;
	
	private static final ColumnInfo[] s_allocationHdrLayout = {
        new ColumnInfo( " ","C_AllocationHdr_ID",IDColumn.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"C_DocType_ID" ),"(SELECT name FROM c_doctype d WHERE d.c_doctype_id = C_AllocationHdr.c_doctype_id)",String.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"DateTrx" ),"C_AllocationHdr.DateTrx",Timestamp.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"DateAcct" ),"C_AllocationHdr.DateAcct",Timestamp.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"GrandTotal" ),"C_AllocationHdr.GrandTotal",BigDecimal.class ),
		new ColumnInfo( Msg.translate( Env.getCtx(),"Pendiente" ),"(select case when C_AllocationHdr.allocationtype in ('OPA','OP','RC','RCA') then HTE_POCRAvailable(C_AllocationHdr.c_allocationhdr_id) else null end)",BigDecimal.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"Description" ),"C_AllocationHdr.Description",String.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"Documentno" ),"C_AllocationHdr.Documentno",String.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"Processed" ),"C_AllocationHdr.Processed",Boolean.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"IsActive" ),"C_AllocationHdr.IsActive",Boolean.class )
	};
	
	/**
	 *	Standard Constructor
	 *  @param  queryvalue   Query value Name or Value if contains numbers
	 *  @param isSOTrx  if false, query vendors only
	 *  @param whereClause where clause
	 */
	public InfoAllocationHdrPanel(int windowNo, String queryValue, boolean isSOTrx,boolean multipleSelection, String whereClause)
	{		
		this(windowNo, queryValue, isSOTrx, multipleSelection, whereClause, true);
	}
	
	/**
	 *	Standard Constructor
	 *  @param  queryvalue   Query value Name or Value if contains numbers
	 *  @param isSOTrx  if false, query vendors only
	 *  @param whereClause where clause
	 */
	public InfoAllocationHdrPanel(int windowNo, String queryValue, boolean isSOTrx,boolean multipleSelection, String whereClause, boolean lookup)
	{

		super (windowNo, "C_AllocationHdr", "C_AllocationHdr",multipleSelection, whereClause, lookup);
		setTitle(Msg.getMsg(Env.getCtx(), "InfoAllocationHdr"));
		m_isSOTrx = isSOTrx;
        initComponents();
        init();
		initInfo(queryValue, whereClause);
        
        int no = contentPanel.getRowCount();
        setStatusLine(Integer.toString(no) + " " + Msg.getMsg(Env.getCtx(), "SearchRows_EnterQuery"), false);
        setStatusDB(Integer.toString(no));
        //
		if (queryValue != null && queryValue.length()>0)
		{
			 executeQuery();
             renderItems();
        }
		p_loadedOK = true; // Elaine 2008/07/28
			
	}
	
	private void initComponents()
	{
		fBPartner = new WSearchEditor(
				MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 3499, DisplayType.Search), 
				Msg.translate(Env.getCtx(), "BPartner"), "", false, false, true);
		fBPartner.addValueChangeListener(this);
		
		/*lBPartner = new Label();
		lBPartner.setValue(Util.cleanAmp(Msg.translate(Env.getCtx(), "C_BPartner_ID")));*/
		lDocumentNo = new Label();
		lDocumentNo.setValue(Util.cleanAmp(Msg.translate(Env.getCtx(), "DocumentNo")));
		
		//fBPartner = new Textbox();
		//fBPartner.setMaxlength(40);
		fDocumentNo = new Textbox();
		fDocumentNo.setMaxlength(40);
		
	}
	
	private void init()
	{
		//fBPartner.setWidth("100%");
		fDocumentNo.setWidth("100%");
		
		Grid grid = GridFactory.newGridLayout();
		
		Rows rows = new Rows();
		grid.appendChild(rows);
		
		Row row = new Row();
		rows.appendChild(row);
		/*row.appendChild(lBPartner.rightAlign());
		row.appendChild(fBPartner);
		row.appendChild(lBPartner.rightAlign());
		row.appendChild(fBPartner);
		*/
		
		row.appendChild(fBPartner.getLabel().rightAlign());
		row.appendChild(fBPartner.getComponent());
		
		row.appendChild(lDocumentNo.rightAlign());
		row.appendChild(fDocumentNo);

		layout = new Borderlayout();
        layout.setWidth("100%");
        layout.setHeight("100%");
        if (!isLookup())
        {
        	layout.setStyle("position: absolute");
        }
        this.appendChild(layout);

        North north = new North();
        layout.appendChild(north);
		north.appendChild(grid);

        Center center = new Center();
		layout.appendChild(center);
		center.setFlex(true);
		Div div = new Div();
		div.appendChild(contentPanel);
		if (isLookup())
			contentPanel.setWidth("99%");
        else
        	contentPanel.setStyle("width: 99%; margin: 0px auto;");
        contentPanel.setVflex(true);
		div.setStyle("width :100%; height: 100%");
		center.appendChild(div);

		South south = new South();
		layout.appendChild(south);
		southBody = new Vbox();
		southBody.setWidth("100%");
		south.appendChild(southBody);
		southBody.appendChild(confirmPanel);
		southBody.appendChild(new Separator());
		southBody.appendChild(statusBar);
		
	}
	
	/**
	 *	Dynamic Init
	 *  @param value value
	 *  @param whereClause where clause
	 */
		
	private void initInfo(String value, String whereClause)
	{
		
		//  Set Defaults
		String bp = Env.getContext(Env.getCtx(), p_WindowNo, "C_BPartner_ID");
	
		if (bp != null && bp.length() != 0)
			fBPartner.setValue(new Integer(bp));

	   prepareTable(s_allocationHdrLayout,
	            "C_AllocationHdr",
				Util.isEmpty(p_whereClause, true)?"":p_whereClause, 
				"C_AllocationHdr.documentno");
	}

	@Override
	protected String getSQLWhere() {
		
		StringBuffer sql = new StringBuffer();
		
		if (fDocumentNo.getText().length() > 0)
			sql.append(" AND UPPER(C_AllocationHdr.DocumentNo) LIKE ?");
		
		if (fBPartner.getDisplay() != "")
			sql.append(" AND C_AllocationHdr.C_BPartner_ID=?");

		return sql.toString();
		
	}

	@Override
	protected void setParameters(PreparedStatement pstmt, boolean forCount)
			throws SQLException {
		int index = 1;
		
		if (fDocumentNo.getText().length() > 0)
			pstmt.setString(index++, getSQLText(fDocumentNo));
		
		if (fBPartner.getDisplay() != "")
		{
			Integer bp = (Integer)fBPartner.getValue();
			pstmt.setInt(index++, bp.intValue());
			log.fine("BPartner=" + bp);
		}
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		if (fBPartner.equals(evt.getSource()))
		{
	    	fBPartner.setValue(evt.getNewValue());
		}
	}

}
