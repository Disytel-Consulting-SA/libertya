package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

public class MControladorFiscalClosingInfo extends X_C_Controlador_Fiscal_Closing_Info {

	public MControladorFiscalClosingInfo(Properties ctx, int C_Controlador_Fiscal_Closing_Info_ID, String trxName) {
		super(ctx, C_Controlador_Fiscal_Closing_Info_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MControladorFiscalClosingInfo(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean beforeSave(boolean newRecord) {
		// Buscar punto de venta
		if (!Util.isEmpty(getC_Controlador_Fiscal_ID(), true) && Util.isEmpty(getPuntoDeVenta(), true)) {
			// Buscar punto de venta en el tipo de documento
			String sql = "SELECT doctypekey FROM c_doctype WHERE c_controlador_fiscal_id = ? and isactive = 'Y' ORDER BY created desc LIMIT 1";
			String doctypekey = DB.getSQLValueString(get_TrxName(), sql, getC_Controlador_Fiscal_ID());
			if(!Util.isEmpty(doctypekey, true) && doctypekey.length() >= 4){
				// Los últimos 4 caracteres del tipo de documento debe ser el punto de venta
				try{
					Integer puntodeventa = Integer
							.parseInt(doctypekey.substring(doctypekey.length() - 4, doctypekey.length()));
					setPuntoDeVenta(puntodeventa);
				} catch(NumberFormatException nfe){
					log.severe("No fue posible determinar el punto de venta relacionado con el controlador fiscal");
				}
			}
		}
		
		// Buscar config de TPV desde punto de venta
		if(!Util.isEmpty(getPuntoDeVenta(), true)){
			// Obtener el tpv de ese punto de venta
			String sql = "SELECT name FROM c_pos WHERE ad_client_id = ? and posnumber = ? and isactive = 'Y' ORDER BY created desc LIMIT 1";
			String name = DB.getSQLValueString(get_TrxName(), sql, getAD_Client_ID(), getPuntoDeVenta());
			if(!Util.isEmpty(name, true)){
				setPOSName(name);
			}
		}
		
		// Si no está procesado, es manual y se deben realizar las validaciones
		if(!isProcessed()) {
			// No es posible mismo punto de venta y mismo número
			String where = newRecord?"":" AND C_Controlador_Fiscal_Closing_Info_ID <> "+getC_Controlador_Fiscal_Closing_Info_ID();
			if (existRecordFor(
					getCtx(), get_TableName(), "puntodeventa = ? AND fiscalclosingno = ? AND fiscalclosingtype = '"
							+ getFiscalClosingType() + "'" + where,
					new Object[] { getPuntoDeVenta(), getFiscalClosingNo() }, get_TrxName())) {
				log.saveError("SaveError", Msg.getMsg(getCtx(), "AlreadyExistsControladorFiscalClosingInfo",
						new Object[] { getFiscalClosingType() }));
				return false;
			}
			
			// No se puede guardar un cierre para una fecha posterior a la actual
			if (getFiscalClosingDate().after(Env.getDate())
					&& !TimeUtil.isSameDay(getFiscalClosingDate(), Env.getDate())) {
				log.saveError("SaveError", Msg.getMsg(getCtx(), "InvalidDateControladoFiscalClosingInfo"));
				return false;
			}
		}
		
		return true;
	}
}
