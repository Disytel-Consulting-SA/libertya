package org.openXpertya.plugin;

import java.awt.Frame;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openXpertya.apps.search.Info;

public abstract class InfoGeneralPlugin extends Info {

	@Override
	protected String getSQLWhere() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected void setParameters(PreparedStatement pstmt) throws SQLException {
		// TODO Auto-generated method stub
	}
	
	public InfoGeneralPlugin( Frame frame,boolean modal,int WindowNo,String value,boolean multiSelection,String whereClause )
	{
		super(frame, modal, WindowNo, "AD_Window", "AD_Window_ID", multiSelection, whereClause);
	}
	
	
	

}
