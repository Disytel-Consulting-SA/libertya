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
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class InfoEntidadFinancieraPlan extends Info {

	private static final Info_Column[] s_entidadFinancieraPlanLayout = {
			new Info_Column(" ", "M_EntidadFinancieraPlan.M_EntidadFinancieraPlan_ID", IDColumn.class),
			
			new Info_Column(
					Msg.translate(Env.getCtx(), "C_BPartner_ID"),
					"C_BPartner.Value || ' - ' || C_BPartner.Name",
					String.class),
			
			new Info_Column(
					Msg.translate(Env.getCtx(), "M_EntidadFinanciera_ID"),
					"M_EntidadFinanciera.Value || ' - ' || M_EntidadFinanciera.Name",
					String.class),
			
			new Info_Column(
					Msg.translate(Env.getCtx(), "Value"),
					"M_EntidadFinancieraPlan.Value",
					String.class),
        
			new Info_Column(
					Msg.translate(Env.getCtx(), "Name"),
					"M_EntidadFinancieraPlan.Name",
					String.class),
        
			new Info_Column(
					Msg.translate(Env.getCtx(), "CuotasPago"),
					"M_EntidadFinancieraPlan.CuotasPago",
					Integer.class),
			
			new Info_Column(
					Msg.translate(Env.getCtx(), "CuotasCobro"),
					"M_EntidadFinancieraPlan.CuotasCobro",
					Integer.class)
    };
	
	public InfoEntidadFinancieraPlan(Frame frame, boolean modal, int WindowNo, String tableName, String keyColumn,
			boolean multiSelection, String whereClause) {
		super(frame, modal, WindowNo, tableName, keyColumn, multiSelection, whereClause);
		setTitle( Msg.getMsg( Env.getCtx(),"InfoPOSJournal" ));
		
		statInit();
        initInfo();

        int no = p_table.getRowCount();

        setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
        setStatusDB( Integer.toString( no ));

        executeQuery();

        p_loadedOK = true;

        fEntidadFinancieraPlanValue.requestFocus();
        
        AEnv.positionCenterWindow( frame,this );
	}

	private CLabel lBPartner = new CLabel(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
	private CLabel lEntidadFinanciera = new CLabel(Msg.translate(Env.getCtx(), "M_EntidadFinanciera_ID"));
	private CLabel lEntidadFinancieraPlanValue = new CLabel(Msg.translate(Env.getCtx(), "Value"));
	
	private VLookup fBPartner = VComponentsFactory.VLookupFactory("C_BPartner_ID", "C_BPartner", p_WindowNo,
			DisplayType.Search,
			"C_BPartner.C_BPartner_ID IN (select ef.c_bpartner_id FROM m_entidadfinanciera ef WHERE ef.isactive = 'Y' and ef.ad_client_id = "
					+ Env.getAD_Client_ID(Env.getCtx()) + ")", false, false);
	private VLookup fEntidadFinanciera = VComponentsFactory.VLookupFactory("M_EntidadFinanciera_ID",
			"M_EntidadFinanciera", p_WindowNo, DisplayType.Search, "", false, false);
	private CTextField fEntidadFinancieraPlanValue = new CTextField( 10 );

	private void statInit(){ 
		lBPartner.setLabelFor( fBPartner );
		fBPartner.setBackground( CompierePLAF.getInfoBackground());
		fBPartner.addActionListener( this );

		lEntidadFinanciera.setLabelFor( fEntidadFinanciera );
		fEntidadFinanciera.setBackground( CompierePLAF.getInfoBackground());
		fEntidadFinanciera.addActionListener( this );

		lEntidadFinancieraPlanValue.setLabelFor( fEntidadFinancieraPlanValue );
		fEntidadFinancieraPlanValue.setBackground( CompierePLAF.getInfoBackground());
		fEntidadFinancieraPlanValue.addActionListener( this );

        parameterPanel.setLayout( new ALayout());

        parameterPanel.add( lEntidadFinancieraPlanValue,new ALayoutConstraint( 0,0 ));
        parameterPanel.add( fEntidadFinancieraPlanValue,null );
        parameterPanel.add( lEntidadFinanciera,null );
        parameterPanel.add( fEntidadFinanciera,null );
        parameterPanel.add( lBPartner,null );
        parameterPanel.add( fBPartner,null );
    }    // statInit
	
	private boolean initInfo() {

		prepareTable(
				s_entidadFinancieraPlanLayout,
				"M_EntidadFinancieraPlan INNER JOIN M_EntidadFinanciera ON (M_EntidadFinancieraPlan.M_EntidadFinanciera_ID = M_EntidadFinanciera.M_EntidadFinanciera_ID) INNER JOIN C_BPartner ON (M_EntidadFinanciera.C_BPartner_ID = C_BPartner.C_BPartner_ID)",
				Util.isEmpty(p_whereClause, true)?"":p_whereClause, 
				"C_BPartner.Value, M_EntidadFinanciera.Value, M_EntidadFinancieraPlan.Value");
		
        return true;
    }    // initInfo
	
	@Override
	protected String getSQLWhere() {
		StringBuffer whereClause = new StringBuffer();
		
		if(Util.isEmpty(p_whereClause, true)){
			whereClause.append(" (1=1) ");
		}
		
		if(!Util.isEmpty(fEntidadFinancieraPlanValue.getText(), true)){
			whereClause.append(" AND UPPER(M_EntidadFinancieraPlan.Value) LIKE ? ");
		}
		
		if(fEntidadFinanciera.getValue() != null){
			whereClause.append(" AND M_EntidadFinanciera.M_EntidadFinanciera_ID = ? ");
		}
		
		if(fBPartner.getValue() != null){
			whereClause.append(" AND C_BPartner.C_BPartner_ID = ? ");
		}
		
		return whereClause.toString();
	}

	@Override
	protected void setParameters(PreparedStatement pstmt) throws SQLException {
		int index = 1;

		if(!Util.isEmpty(fEntidadFinancieraPlanValue.getText(), true)){
			pstmt.setString(index++, fEntidadFinancieraPlanValue.getText().toUpperCase());
		}
		
		if(fEntidadFinanciera.getValue() != null){
			pstmt.setInt(index++, (Integer)fEntidadFinanciera.getValue());
		}
		
		if(fBPartner.getValue() != null){
			pstmt.setInt(index++, (Integer)fBPartner.getValue());
		}
	}

}
