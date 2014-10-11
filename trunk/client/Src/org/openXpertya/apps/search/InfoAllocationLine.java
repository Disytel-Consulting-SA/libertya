package org.openXpertya.apps.search;

import java.awt.Dimension;
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
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class InfoAllocationLine extends Info {

	private CLabel lLineDescription = new CLabel(Msg.translate(Env.getCtx(), "Line_Description"));
	private CTextField fLineDescription = new CTextField( 100 );
	
	private static final Info_Column[] s_allocationLineLayout = {
		new Info_Column(" ", "C_AllocationLine.C_AllocationLine_ID", IDColumn.class),
    
		new Info_Column(
				Msg.translate(Env.getCtx(), "Line_Description"),
				"C_AllocationLine.Line_Description",
				String.class),
	};

	public InfoAllocationLine(Frame frame, boolean modal, int WindowNo,
			String tableName, String keyColumn, boolean multiSelection,
			String whereClause) {
		super(frame, modal, WindowNo, tableName, keyColumn, multiSelection,
				whereClause);
		
		setTitle( Msg.getMsg( Env.getCtx(),"InfoAllocationLine" ));
		
		statInit();
        initInfo();

        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        executeQuery();

        p_loadedOK = true;

        lLineDescription.setPreferredSize(new Dimension(220, 20));
        fLineDescription.requestFocus();
        
        AEnv.positionCenterWindow( frame,this );
	}
	
	private void statInit(){ 
		lLineDescription.setLabelFor( fLineDescription );
		fLineDescription.setBackground( CompierePLAF.getInfoBackground());
		fLineDescription.addActionListener( this );
		
		parameterPanel.setLayout( new ALayout());
        parameterPanel.add( lLineDescription,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( fLineDescription,null );
	}
	
	private boolean initInfo() {
		prepareTable(
				s_allocationLineLayout,
				"C_AllocationLine",
				Util.isEmpty(p_whereClause, true)?"":p_whereClause, 
				"C_AllocationLine.Line_Description");
        return true;
    } 
	
	@Override
	protected String getSQLWhere() {
		StringBuffer whereClause = new StringBuffer();
		
		if(Util.isEmpty(p_whereClause, true)){
			whereClause.append(" (1=1) ");
		}
		
		// Descripción de la línea
		if(!Util.isEmpty(fLineDescription.getText(), true)){
			whereClause.append(" AND C_AllocationLine.Line_Description like ? ");
		}
		
		return whereClause.toString();
	}

	@Override
	protected void setParameters(PreparedStatement pstmt) throws SQLException {
		int index = 1;
		// Descripción de la línea
		if(!Util.isEmpty(fLineDescription.getText(), true)){
			pstmt.setString(index++, fLineDescription.getText().toUpperCase());
		}
	}

}
