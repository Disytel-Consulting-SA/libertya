package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MBrochureLine extends X_M_BrochureLine {

	public MBrochureLine(Properties ctx, int M_BrochureLine_ID, String trxName) {
		super(ctx, M_BrochureLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBrochureLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave( boolean newRecord ) {
		// No pueden existir dos líneas con mismo artículo
		String whereClause = "m_brochure_id = ? and m_product_id = ?";
		whereClause += newRecord?"":" and m_brochureline_id <> "+getID();
		if (existRecordFor(getCtx(), get_TableName(), whereClause,
				new Object[] { getM_Brochure_ID(), getM_Product_ID() },
				get_TrxName())) {
			MProduct product = MProduct.get(getCtx(), getM_Product_ID());
			log.saveError("SaveError", Msg.getMsg(
					getCtx(),
					"ExistsProductInBrochure",
					new Object[] { product.getValue() + " - "
							+ product.getName() }));
			return false;
		}
		// Actualización del campo Linea
		if( getLine() == 0 ) {
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM "
					+ get_TableName() + " WHERE M_Brochure_ID=?";
			int ii = DB.getSQLValue(get_TrxName(), sql, getM_Brochure_ID());
            setLine( ii );
        }
		
		return true;
	}
	
}
