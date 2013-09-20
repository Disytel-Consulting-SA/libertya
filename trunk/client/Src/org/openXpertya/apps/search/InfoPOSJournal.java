package org.openXpertya.apps.search;

import java.awt.Frame;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CLabel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class InfoPOSJournal extends Info {

	private CLabel lPOS = new CLabel(Msg.translate(Env.getCtx(), "C_POS_ID"));
	private CLabel lDateTrx = new CLabel(Msg.translate(Env.getCtx(), "Date"));
	private CLabel lUser = new CLabel(Msg.translate(Env.getCtx(), "AD_User_ID"));
	
	private CTextField fPOS = new CTextField( 10 );
	private VDate fDateTrx = new VDate( "DateTrx",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"Date" ));
	private CTextField fUserName = new CTextField( 10 );
	
	
	private static final Info_Column[] s_posJournalLayout = {
			new Info_Column(" ", "C_POSJournal.C_POSJournal_ID", IDColumn.class),
        
			new Info_Column(
					Msg.translate(Env.getCtx(), "C_POS_ID"),
					"C_POS.Name",
					String.class),
        
			new Info_Column(Msg.translate(Env.getCtx(), "Date"),
					"C_POSJournal.DateTrx", Timestamp.class),
        
			new Info_Column(
					Msg.translate(Env.getCtx(), "AD_User_ID"),
					"AD_User.Name",
					String.class),
        
			new Info_Column(
					Msg.translate(Env.getCtx(), "DocStatus"),
					"(SELECT AD_Ref_List_Trl.Name FROM AD_Ref_List_Trl WHERE AD_Ref_List_ID = (SELECT AD_Ref_List_ID FROM AD_Ref_List WHERE AD_Reference_ID="
							+ X_C_POSJournal.DOCSTATUS_AD_Reference_ID
							+ " AND Value = C_POSJournal.DocStatus LIMIT 1) AND AD_Language = '"
							+ Env.getAD_Language(Env.getCtx()) + "')",
					String.class)
    };
	
	
	public InfoPOSJournal(Frame frame, boolean modal, int WindowNo,
			String tableName, String keyColumn, boolean multiSelection,
			String whereClause) {
		super(frame, modal, WindowNo, tableName, keyColumn, multiSelection,
				whereClause);

		setTitle( Msg.getMsg( Env.getCtx(),"InfoPOSJournal" ));
		
		statInit();
        initInfo();

        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        executeQuery();

        p_loadedOK = true;

        fPOS.requestFocus();
        
        AEnv.positionCenterWindow( frame,this );
	}

	private void statInit(){ 
		lPOS.setLabelFor( fPOS );
		fPOS.setBackground( CompierePLAF.getInfoBackground());
		fPOS.addActionListener( this );

        lDateTrx.setLabelFor( fDateTrx );
        fDateTrx.setBackground( CompierePLAF.getInfoBackground());

        lUser.setLabelFor( fUserName );
        fUserName.setBackground( CompierePLAF.getInfoBackground());
        fUserName.addActionListener( this );

        parameterPanel.setLayout( new ALayout());

        parameterPanel.add( lPOS,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( fPOS,null );
        parameterPanel.add( lDateTrx,null );
        parameterPanel.add( fDateTrx,null );
        parameterPanel.add( lUser,null );
        parameterPanel.add( fUserName,null );
    }    // statInit
	
	private boolean initInfo() {

		prepareTable(
				s_posJournalLayout,
				"C_POSJournal INNER JOIN C_POS ON (C_POSJournal.C_POS_ID = C_POS.C_POS_ID) INNER JOIN AD_User ON (AD_User.AD_User_ID = C_POSJournal.AD_User_ID)",
				"", "C_POSJournal.DateTrx DESC, C_POS.Name");
		
        return true;
    }    // initInfo
	
	@Override
	protected String getSQLWhere() {
		StringBuffer whereClause = new StringBuffer();
		
		whereClause.append(" (1=1) ");
		
		if(!Util.isEmpty(fPOS.getText(), true)){
			whereClause.append(" AND UPPER(C_POS.Name) LIKE ? ");
		}
		
		if(fDateTrx.getValue() != null){
			whereClause.append(" AND date_trunc('day',C_POSJournal.DateTrx) = date_trunc('day',?::date) ");
		}
		
		if(!Util.isEmpty(fUserName.getText(), true)){
			whereClause.append(" AND UPPER(AD_User.Name) LIKE ? ");
		}
		
		return whereClause.toString();
	}

	@Override
	protected void setParameters(PreparedStatement pstmt) throws SQLException {
		int index = 1;

		if(!Util.isEmpty(fPOS.getText(), true)){
			pstmt.setString(index++, fPOS.getText().toUpperCase());
		}
		
		if(fDateTrx.getValue() != null){
			pstmt.setTimestamp(index++, (Timestamp)fDateTrx.getValue());
		}
		
		if(!Util.isEmpty(fUserName.getText(), true)){
			pstmt.setString(index++, fUserName.getText().toUpperCase());
		}
	}

}
