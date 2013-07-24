package org.adempiere.webui.plugin.common;

/**
 * DEPRECADA.  Mantenida solo para compatibilidad con solucion Swing.
 */

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.adempiere.webui.panel.InfoPanel;

public abstract class InfoGeneralPluginPanel extends InfoPanel {

	@Override
	protected String getSQLWhere() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected void setParameters(PreparedStatement pstmt, boolean forCount)
			throws SQLException {
		// TODO Auto-generated method stub
	}
	
	protected InfoGeneralPluginPanel(int WindowNo, boolean multipleSelection, String whereClause) {
		super(WindowNo, null, null, multipleSelection, whereClause);
	}

}
