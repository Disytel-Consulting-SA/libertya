package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MCategoriaIva extends X_C_Categoria_Iva {

	private static CLogger log = CLogger.getCLogger(MLetraComprobante.class);
	
	public static final int CONSUMIDOR_FINAL = 1;
	public static final int RESPONSABLE_INSCRIPTO = 2;
	public static final int RESPONSABLE_NO_INSCRIPTO = 3;
	public static final int EXENTO = 4;
	public static final int RESPONSABLE_MONOTRIBUTO = 5;
	public static final int NO_RESPONSABLE = 6;
	public static final int NO_CATEGORIZADO = 7;
	public static final int RESPONSABLE_NO_INSCRIPTO_BIENES_DE_USO = 8;
	public static final int RESPONSABLE_INSCRIPTO_FACTURA_M = 9;
	public static final int RESPONSABLE_M_CON_CBU_INFORMADO = 10;

	public static MCategoriaIva[] buscarCodigo(int codigo, int recordId, String trxName) {
		MCategoriaIva[] ret = null;
		
		try {
			String sql = " SELECT C_Categoria_Iva_ID FROM C_Categoria_Iva WHERE codigo = ? AND C_Categoria_Iva_ID <> ? ";
			sql = MRole.getDefault().addAccessSQL( sql, "C_Categoria_Iva", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO );
			sql = sql + " ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, C_Categoria_Iva_ID ASC ";
			
			CPreparedStatement pp = DB.prepareStatement(sql, trxName);
			
			pp.setInt(1, codigo);
			pp.setInt(2, recordId);
			
			
			ResultSet rs = pp.executeQuery();
			
			ArrayList<MCategoriaIva> arr = new ArrayList<MCategoriaIva>();
			
			while (rs.next())
				arr.add( new MCategoriaIva(Env.getCtx(), rs.getInt(1), trxName) );
			
			pp.close();
			rs.close();
			
			ret = new MCategoriaIva[arr.size()];
			arr.toArray(ret);
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "buscarCodigo", e);
		}
		
		return ret;
	}
	
	public static MCategoriaIva[] buscarCodigo(int codigo, String trxName) {
		return buscarCodigo(codigo, 0,trxName);
	}
	
	public static int getCodigo(int C_Categoria_IVA_ID, String trxName) {
		int codigo = 0;
		if(C_Categoria_IVA_ID > 0) {
			Integer result = (Integer)DB.getSQLObject(trxName, "SELECT codigo FROM C_Categoria_IVA WHERE C_Categoria_IVA_ID = ?", new Object[] {C_Categoria_IVA_ID});
			if(result != null)
				codigo = result;
		}
		return codigo;
	}

	public static int getCodigoOfBPartner(int C_BPartner_ID, String trxName) {
		int codigo = 0;
		Integer categoriaIvaID = (Integer)DB.getSQLObject(trxName, "SELECT C_Categoria_IVA_ID FROM C_BPartner WHERE C_BPartner_ID = ?", new Object[] {C_BPartner_ID});
		if(categoriaIvaID != null)
			codigo = getCodigo(categoriaIvaID, trxName);
		return codigo;
	}
	
	public MCategoriaIva (Properties ctx, int C_Categoria_Iva_ID, String trxName) {
		super (ctx, C_Categoria_Iva_ID, trxName);
	}

	public MCategoriaIva (Properties ctx, ResultSet rs, String trxName) {
		super (ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		MCategoriaIva[] ivas = buscarCodigo(getCodigo(), getC_Categoria_Iva_ID(), get_TrxName());
		
		if(ivas != null && ivas.length > 0) {
			log.saveError("IvaCodeExistentError","");
			return false;
		}
			
		return true;
	}

	
}

