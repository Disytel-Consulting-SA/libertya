package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.X_AD_User;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_BPartner_BankList;
import org.openXpertya.model.X_C_BPartner_Location;
import org.openXpertya.model.X_C_Categoria_Iva;
import org.openXpertya.model.X_C_ElectronicPaymentBranch;
import org.openXpertya.model.X_C_Location;
import org.openXpertya.model.X_C_Region;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ExportListaHSBCProveedores extends ExportListaHSBC {

	/** Registrar las entidades comerciales para llevar registro y no repetir */
	private Set<Integer> bpartnerIDs = new HashSet<Integer>();
	
	public ExportListaHSBCProveedores(Properties ctx, MBankList bankList, String trxName) {
		super(ctx, bankList, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getBankListExportFormatValue() {
		return "LHSBC_PROV";
	}
	
	@Override
	protected String getQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT ");
		sql.append("	bp.c_bpartner_id, ");
		sql.append("	bp.value, ");
		sql.append("	bp.name, ");
		sql.append("	bp.taxid, ");
		sql.append(" 	l.address1, ");
		sql.append(" 	l.city, ");
		sql.append(" 	l.postal, ");
		sql.append(" 	l.address1, ");
		sql.append(" 	r.name as provincia, ");
		sql.append("	epb.value as sucursal, ");
		sql.append("	bp.iibb, ");
		sql.append("	ci.name as categoriaiva, ");
		sql.append("	coalesce(bpl.phone, u.phone) as phone, ");
		sql.append("	coalesce(bpl.email,u.email) as email, ");
		sql.append("	u.name as contact ");
		
		sql.append("FROM ");
		sql.append(X_C_BPartner.Table_Name + " bp ");
		sql.append("	INNER JOIN " + X_C_Categoria_Iva.Table_Name + " ci ");
		sql.append("		ON ci.c_categoria_iva_id = bp.c_categoria_iva_id ");
		sql.append("	INNER JOIN " + X_C_BPartner_BankList.Table_Name + " bpbl ");
		sql.append("		ON bpbl.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_ElectronicPaymentBranch.Table_Name + " AS epb ");
		sql.append("		ON epb.c_electronicpaymentbranch_id = bpbl.c_electronicpaymentbranch_id ");
		sql.append("	INNER JOIN " + X_C_BPartner_Location.Table_Name + " bpl ");
		sql.append("		ON (bpl.c_bpartner_id = bp.c_bpartner_id and bpl.isactive = 'Y') ");
		sql.append("	INNER JOIN " + X_C_Location.Table_Name + " l ");
		sql.append("		ON l.c_location_id = bpl.c_location_id ");
		sql.append("	LEFT JOIN " + X_AD_User.Table_Name + " u ");
		sql.append("		ON (u.c_bpartner_id = bp.c_bpartner_id and u.isactive = 'Y') ");
		sql.append("	LEFT JOIN " + X_C_Region.Table_Name + " r ");
		sql.append("		ON r.c_region_id = l.c_region_id ");
		
		sql.append("WHERE ");
		sql.append("	bp.ad_client_id = ").append(Env.getAD_Client_ID(getCtx()));
		sql.append("	and bp.isvendor = 'Y' ");
		sql.append("	and bp.isactive = 'Y' ");
		// Tener en cuenta la última fecha exportada del formato de exportación
		// para aplicarla a las fechas de creación o de actualización de
		// entidades comerciales
		if(getExportFormat().getLastExportedDate() != null){
			String lastExportedDate = dateFormat_yyyyMMdd.format(getExportFormat().getLastExportedDate());
			sql.append(" and ( ");
			sql.append(" bp.created::date >= '"+lastExportedDate+"'::date ");
			sql.append(" or ");
			sql.append(" bp.updated::date >= '"+lastExportedDate+"'::date ");
			sql.append(" or ");
			sql.append(" bpbl.created::date >= '"+lastExportedDate+"'::date ");
			sql.append(" or ");
			sql.append(" bpbl.updated::date >= '"+lastExportedDate+"'::date ");
			sql.append(" ) ");
		}
		sql.append("ORDER BY bp.value ");
		
		return sql.toString();
	}
	
	/**
	 * @param rs
	 *            result set actual
	 * @return línea del archivo SP
	 * @throws Exception
	 */
	protected String getSPLine(ResultSet rs) throws Exception{
		StringBuffer spLine = new StringBuffer();
		// Si ya existe ese bpartner en el contenido no lo registro
		if(!bpartnerIDs.contains(rs.getInt("c_bpartner_id"))){
			// Línea SP
			String taxid = rs.getString("taxid").replace("-", "");
			spLine.append("SP");
			spLine.append(getFieldSeparator());
			spLine.append("I");
			spLine.append(getFieldSeparator());
			spLine.append("U");
			spLine.append(getFieldSeparator());
			spLine.append(taxid);
			spLine.append(getFieldSeparator());
			spLine.append(rs.getString("name"));
			spLine.append(getFieldSeparator());
			spLine.append(rs.getString("address1"));
			spLine.append(getFieldSeparator());
			// Localidad no puede superar los 20 caracteres
			spLine.append(rs.getString("city") != null && rs.getString("city").length() > 20
					? rs.getString("city").substring(0, 19)
					: (rs.getString("city") != null ? rs.getString("city") : ""));
			spLine.append(getFieldSeparator());
			spLine.append(rs.getString("postal") == null?"":rs.getString("postal"));
			spLine.append(getFieldSeparator());
			spLine.append(rs.getString("provincia") == null?"":rs.getString("provincia"));
			spLine.append(getFieldSeparator());
			spLine.append(rs.getString("sucursal"));
			spLine.append(getFieldSeparator());
			spLine.append(!Util.isEmpty(rs.getString("iibb"), true)?rs.getString("iibb"):taxid);
			spLine.append(getFieldSeparator());
			spLine.append(rs.getString("categoriaiva"));
			spLine.append(getFieldSeparator());
			// Por lo pronto no se envía el teléfono
			// spLine.append(rs.getString("phone"));
			spLine.append("");
			spLine.append(getFieldSeparator());
			spLine.append(rs.getString("email") == null?"":rs.getString("email"));
			spLine.append(getFieldSeparator());
			spLine.append(rs.getString("contact") == null?"":rs.getString("contact"));
			spLine.append(getFieldSeparator());
			// Por ahora estos datos no se llenan, por lo pronto sólo se va a
			// utilizar para transferencias, requiere una estructura de datos
			// adicional
			spLine.append(""); // Tipo y Nro del Autorizado
			spLine.append(getFieldSeparator());
			spLine.append(""); // Nombre del Autorizado
			spLine.append(getFieldSeparator());
			spLine.append(getRowSeparator());
			bpartnerIDs.add(rs.getInt("c_bpartner_id"));
			fileContentLength++;
		}		
		return spLine.toString();
	}
	
	protected void setWhereClauseParams(PreparedStatement ps) throws SQLException{
		// No hace nada porque los parámetros se pasan directamente en la query
	}
	
	protected String getIDLine(ResultSet rs) throws Exception{
		return "";
	}
	
	protected String getDCLine(ResultSet rs) throws Exception{
		return "";
	}
	
	protected String getRELine(ResultSet rs) throws Exception{
		return "";
	}
	
	@Override
	protected void doExport(ResultSet rs) throws Exception{
		// Registro SP - Proveedores y autorizados. Verificar
		// si pasamos todos como inserción pasa algo en la importación del
		// aplicativo porque sino hay que ver la forma de registrar los ya
		// enviados. Los autorizados son los usuarios relacionados al BPartner.
		fileContent.append(getSPLine(rs));
	}
}
