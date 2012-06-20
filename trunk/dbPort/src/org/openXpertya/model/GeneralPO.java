package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class GeneralPO extends PO {

	// Variables de instancia
	
	/** Id de tabla */
	
	private int tableID;

	
	// Constructores
	
	public GeneralPO(Properties ctx, Integer ID, ResultSet rs, int tableID, String trxName){
		super(ctx, ID, trxName, rs);
		this.tableID = tableID;
		super.initialize(ID, rs);
	}
	
	
	protected void initialize(int ID, ResultSet rs){
		// No dejo inicializar para que se pueda setear el table id
	}
	
	
	@Override
	protected POInfo initPO(Properties ctx) {
		POInfo poi = POInfo.getPOInfo (ctx, tableID);
		return poi;
	}
}
