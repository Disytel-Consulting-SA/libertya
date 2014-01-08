package org.adempiere.webui.panel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.adempiere.webui.component.Datebox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Div;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;

public class InfoPOSJournalPanel extends InfoPanel  {

	private Label lPOS = new Label(Msg.translate(Env.getCtx(), "C_POS_ID"));
	private Label lDateTrx = new Label(Msg.translate(Env.getCtx(), "Date"));
	private Label lUserName = new Label(Msg.translate(Env.getCtx(), "AD_User_ID"));
	
	private Textbox fPOS = new Textbox();
	private Datebox fDateTrx = new Datebox();
	private Textbox fUserName = new Textbox();
	
	private Borderlayout layout;
	private Vbox southBody;
	
    /**  Array of Column Info    */
    private static final ColumnInfo[] s_posJournalLayout = {
        new ColumnInfo(" ", "p.C_POSJournal_ID", IDColumn.class),
        new ColumnInfo(
        		Msg.translate(Env.getCtx(), "C_POS_ID"), 
        		"C_POS.Name", 
        		String.class),
        new ColumnInfo(
        		Msg.translate(Env.getCtx(), "Date"),
				"p.DateTrx", 
				Timestamp.class),
        new ColumnInfo(
        		Msg.translate(Env.getCtx(), "AD_User_ID"),
				"AD_User.Name",
				String.class),
        new ColumnInfo(
        		Msg.translate(Env.getCtx(), "DocStatus"),
				"(SELECT AD_Ref_List_Trl.Name FROM AD_Ref_List_Trl WHERE AD_Ref_List_ID = (SELECT AD_Ref_List_ID FROM AD_Ref_List WHERE AD_Reference_ID="
						+ X_C_POSJournal.DOCSTATUS_AD_Reference_ID
						+ " AND Value = p.DocStatus LIMIT 1) AND AD_Language = '"
						+ Env.getAD_Language(Env.getCtx()) + "')",
				String.class)
    };
	
	/**
     * Detail protected constructor
     * @param WindowNo window no
     * @param value query value
     * @param multiSelection multiple selection
     * @param whereClause where clause
    *
     */
    protected InfoPOSJournalPanel(int WindowNo, String value, boolean multiSelection, String whereClause)
    {
    	this(WindowNo, value, multiSelection, whereClause, true);
    }
	
    
    
	protected InfoPOSJournalPanel(int WindowNo, String value, boolean multipleSelection, String whereClause, boolean lookup) {
		super(WindowNo, "p", "C_POSJournal_ID", multipleSelection, whereClause, lookup);
		
		setTitle(Msg.getMsg(Env.getCtx(), "InfoPOSJournal"));
        initComponents();
        init();
           
       p_loadedOK = initInfo();
       int no = contentPanel.getRowCount();
       setStatusLine(Integer.toString(no) + " " + Msg.getMsg(Env.getCtx(), "SearchRows_EnterQuery"), false);
       setStatusDB(Integer.toString(no));
       if (value != null && value.length() > 0)
       {
           String values[] = value.split("_");
//           txtDocumentNo.setText(values[0]);
           executeQuery();
           renderItems();
       }
	}

	
    
    /**
     *  General Init
     *  @return true, if success
     */
    private boolean initInfo ()
    {
        //  Set Defaults
//        String bp = Env.getContext(Env.getCtx(), p_WindowNo, "C_BPartner_ID");
//        if (bp != null && bp.length() != 0)
//            editorBPartner.setValue(new Integer(bp));

        //  prepare table
        StringBuffer where = new StringBuffer("p.IsActive='Y'");
        if (p_whereClause.length() > 0)
            where.append(" AND ").append(Util.replace(p_whereClause, "C_POSJournal.", "p."));
        prepareTable(s_posJournalLayout,
            " C_POSJournal p INNER JOIN C_POS ON (p.C_POS_ID = C_POS.C_POS_ID) INNER JOIN AD_User ON (AD_User.AD_User_ID = p.AD_User_ID) ",   //  corrected for CM
            "",
            "p.DateTrx DESC, C_POS.Name");
        //
        return true;
           
    }   //  initInfo
	
    private void initComponents()
    {
    	lPOS = new Label(Msg.translate(Env.getCtx(), "C_POS_ID"));
    	lDateTrx = new Label(Msg.translate(Env.getCtx(), "Date"));
    	lUserName = new Label(Msg.translate(Env.getCtx(), "AD_User_ID"));
    	
    	fPOS = new Textbox();
    	fDateTrx = new Datebox();
    	fUserName = new Textbox();
    	
    	lPOS.addEventListener(Events.ON_CHANGE, this);
    	lDateTrx.addEventListener(Events.ON_CHANGE, this);
    	lUserName.addEventListener(Events.ON_CHANGE, this);
    }
	
    
    private void init()
    {
    	fPOS.setWidth("50%");
    	fDateTrx.setWidth("50%");
    	fUserName.setWidth("50%");

    	Grid grid = GridFactory.newGridLayout();
		
		Rows rows = new Rows();
		grid.appendChild(rows);
		
		Row row = new Row();
		rows.appendChild(row);
		row.appendChild(lPOS.rightAlign());
		row.appendChild(fPOS);
		row.appendChild(lDateTrx.rightAlign());
		row.appendChild(fDateTrx);
		row.appendChild(lUserName.rightAlign());
		row.appendChild(fUserName);
		
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

	
	@Override
	protected String getSQLWhere() {
		StringBuffer sql = new StringBuffer();

		sql.append(" (1=1) ");
		
		// POS
		if (fPOS.getText().length() > 0)
			sql.append(" AND UPPER(C_POS.Name) LIKE ? ");
		
		// Fecha
		Date date = null;
		try {
			date = fDateTrx.getValue();
		}        
		catch (WrongValueException e) {
            e.printStackTrace();
        }
		if (date != null)
			 sql.append(" AND date_trunc('day',p.DateTrx) = date_trunc('day',?::date) ");
		
		// Usuario		
		if (fUserName.getText().length() > 0)
			sql.append(" AND UPPER(AD_User.Name) LIKE ? ");

		return sql.toString();
	}

	@Override
	protected void setParameters(PreparedStatement pstmt, boolean forCount) throws SQLException {
		int index = 1;
		// POS
		if (fPOS.getText().length() > 0)
			pstmt.setString(index++, getSQLText(fPOS));

		// Fecha
        Date date = null;
        Timestamp dateTS = null;
        try
        {
            if (fDateTrx.getValue() != null)
            {
                date = fDateTrx.getValue();
                dateTS = new Timestamp(date.getTime());
            }
        }
        catch (WrongValueException e){
            e.printStackTrace();
        }
        if (dateTS != null) {
        	pstmt.setTimestamp(index++, dateTS);
        }
        
		// Usuario		
		if (fUserName.getText().length() > 0)
			pstmt.setString(index++, getSQLText(fPOS));
	}


	protected boolean hasZoom()
	{
		return true;
	}	//	hasZoom

}
