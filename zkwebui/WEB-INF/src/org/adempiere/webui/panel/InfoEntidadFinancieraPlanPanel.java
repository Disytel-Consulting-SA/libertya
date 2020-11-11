package org.adempiere.webui.panel;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Div;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;

public class InfoEntidadFinancieraPlanPanel extends InfoPanel implements ValueChangeListener {

	private static final ColumnInfo[] s_entidadFinancieraPlanLayout = {
			new ColumnInfo(" ", "M_EntidadFinancieraPlan.M_EntidadFinancieraPlan_ID", IDColumn.class),
			
			new ColumnInfo(
					Msg.translate(Env.getCtx(), "C_BPartner_ID"),
					"C_BPartner.Value || ' - ' || C_BPartner.Name",
					String.class),
			
			new ColumnInfo(
					Msg.translate(Env.getCtx(), "M_EntidadFinanciera_ID"),
					"M_EntidadFinanciera.Value || ' - ' || M_EntidadFinanciera.Name",
					String.class),
			
			new ColumnInfo(
					Msg.translate(Env.getCtx(), "Value"),
					"M_EntidadFinancieraPlan.Value",
					String.class),
        
			new ColumnInfo(
					Msg.translate(Env.getCtx(), "Name"),
					"M_EntidadFinancieraPlan.Name",
					String.class),
        
			new ColumnInfo(
					Msg.translate(Env.getCtx(), "CuotasPago"),
					"M_EntidadFinancieraPlan.CuotasPago",
					Integer.class),
			
			new ColumnInfo(
					Msg.translate(Env.getCtx(), "CuotasCobro"),
					"M_EntidadFinancieraPlan.CuotasCobro",
					Integer.class)
    };

	private Label lEntidadFinancieraPlanValue;
	
	private WSearchEditor fBPartner;
	private WSearchEditor fEntidadFinanciera;
	private Textbox fEntidadFinancieraPlanValue;
    
	private Borderlayout layout;
	private Vbox southBody;
	
	public InfoEntidadFinancieraPlanPanel(int WindowNo, String value, boolean multiSelection, String whereClause) {
		this(WindowNo, value, multiSelection, whereClause, true);
	}

	protected InfoEntidadFinancieraPlanPanel(int WindowNo, String value, boolean multipleSelection, String whereClause, boolean lookup) {
		super(WindowNo, "M_EntidadFinancieraPlan", "M_EntidadFinancieraPlan_ID", multipleSelection, whereClause, lookup);
		
		setTitle(Msg.getMsg(Env.getCtx(), "InfoEntidadFinancieraPlan"));
        initComponents();
        init();
        
        p_loadedOK = initInfo();
        int no = contentPanel.getRowCount();
        setStatusLine(Integer.toString(no) + " " + Msg.getMsg(Env.getCtx(), "SearchRows_EnterQuery"), false);
        setStatusDB(Integer.toString(no));
        if (value != null && value.length() > 0) {
            executeQuery();
            renderItems();
        }
	}

	protected void initComponents() {
		lEntidadFinancieraPlanValue = new Label(Msg.translate(Env.getCtx(), "Value"));		
		
		MLookup lookupBP = null;
		try {
			lookupBP = MLookupFactory.get(Env.getCtx(), p_WindowNo, 3499, DisplayType.Search,
				Env.getLanguage(Env.getCtx()), "C_BPartner_ID", 0, false,
				"C_BPartner.C_BPartner_ID IN (select ef.c_bpartner_id FROM m_entidadfinanciera ef WHERE ef.isactive = 'Y' and ef.ad_client_id = "
						+ Env.getAD_Client_ID(Env.getCtx()) + ")");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		fBPartner = new WSearchEditor(lookupBP, Msg.translate(
                Env.getCtx(), "C_BPartner_ID"), "", false, false, true);
        
		int efColumnID = DB.getSQLValue(null,
				"select ad_column_id from ad_column where columnname = 'M_EntidadFinanciera_ID' limit 1");
		MLookup lookupEF = MLookupFactory.get(Env.getCtx(), p_WindowNo, 0, efColumnID, DisplayType.Search);
		fEntidadFinanciera = new WSearchEditor(lookupEF, Msg.translate(Env.getCtx(), "M_EntidadFinanciera_ID"), "",
				false, false, true);
		
		fEntidadFinancieraPlanValue = new Textbox();
		
		fBPartner.addValueChangeListener(this);
		fEntidadFinanciera.addValueChangeListener(this);
	}
	
	protected void init() {
    	fEntidadFinancieraPlanValue.setWidth("50%");

    	Grid grid = GridFactory.newGridLayout();
		
		Rows rows = new Rows();
		grid.appendChild(rows);
		
		Row row = new Row();
		rows.appendChild(row);
		row.appendChild(lEntidadFinancieraPlanValue.rightAlign());
		row.appendChild(fEntidadFinancieraPlanValue);
		row.appendChild(fEntidadFinanciera.getLabel().rightAlign());
		row.appendChild(fEntidadFinanciera.getComponent());
		row.appendChild(fBPartner.getLabel().rightAlign());
		row.appendChild(fBPartner.getComponent());
		
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
	
	private boolean initInfo () {
		prepareTable(
				s_entidadFinancieraPlanLayout,
				"M_EntidadFinancieraPlan INNER JOIN M_EntidadFinanciera ON (M_EntidadFinancieraPlan.M_EntidadFinanciera_ID = M_EntidadFinanciera.M_EntidadFinanciera_ID) INNER JOIN C_BPartner ON (M_EntidadFinanciera.C_BPartner_ID = C_BPartner.C_BPartner_ID)",
				Util.isEmpty(p_whereClause, true)?"":p_whereClause, 
				"C_BPartner.Value, M_EntidadFinanciera.Value, M_EntidadFinancieraPlan.Value");
		
        return true;
    }
	
	@Override
	protected String getSQLWhere() {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" (1=1) ");
		
		if(!Util.isEmpty(fEntidadFinancieraPlanValue.getText(), true)){
			sql.append(" AND UPPER(M_EntidadFinancieraPlan.Value) LIKE ? ");
		}
		
		if (fEntidadFinanciera.getValue() != null) {
			sql.append(" AND M_EntidadFinanciera.M_EntidadFinanciera_ID = ? ");
		}
		
		if (fBPartner.getValue() != null) {
			sql.append(" AND C_BPartner.C_BPartner_ID = ? ");
		}
		
		return sql.toString();
	}

	@Override
	protected void setParameters(PreparedStatement pstmt, boolean forCount) throws SQLException {
		int index = 1;

		if(!Util.isEmpty(fEntidadFinancieraPlanValue.getText(), true)){
			pstmt.setString(index++, getSQLText(fEntidadFinancieraPlanValue));
		}
		
		if(fEntidadFinanciera.getValue() != null){
			pstmt.setInt(index++, ((Integer)fEntidadFinanciera.getValue()).intValue());
		}
		
		if(fBPartner.getValue() != null){
			pstmt.setInt(index++, ((Integer)fBPartner.getValue()).intValue());
		}
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		if (fBPartner.equals(evt.getSource())) {
			fBPartner.setValue(evt.getNewValue());
		}
		else if (fEntidadFinanciera.equals(evt.getSource())) {
			fEntidadFinanciera.setValue(evt.getNewValue());
		}
	}

	@Override
	protected void insertPagingComponent() {
		southBody.insertBefore(paging, southBody.getFirstChild());
		layout.invalidate();
	}
}
