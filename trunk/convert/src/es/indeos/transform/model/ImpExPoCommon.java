/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Modificado por TecnoXP
 * 
 */

package es.indeos.transform.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.PO;
import org.openXpertya.model.POInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

import es.indeos.transform.Convert;

/** Importer/Exporter Helper
 * 
 * @author usuario
 *
 */
public class ImpExPoCommon {

	public static class ImportedObj {
		public ImportedObj(ImpExPoAdapter newobj, PO po, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
			this.newobj = newobj;
			this.po = po;
			this.parentNew = parentNew;
			this.parentOld = parentOld; 
		}
		
		public ImpExPoAdapter newobj;
		public PO po;
		public ImpExPoAdapter parentOld;
		public ImpExPoAdapter parentNew;
	}
	
	static CLogger		s_log = CLogger.getCLogger (ImpExPoCommon.class);
	
	public static PO FactoryPO(Properties ctx, String trx, String TableName, String WhereCond, Class<?> c, Object[] params) {
		ArrayList ret = FactoryPO(ctx, trx, TableName, WhereCond, c, params, 1);
		
		if (ret.size() > 0)
			return (PO)ret.get(0);
		
		return null;
	}
	
	public static PO FactoryPO(Class<?> c, Properties ctx, ResultSet rs, String trx) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> con = c.getConstructor(Properties.class, ResultSet.class, String.class);
		PO p = (PO)con.newInstance(ctx, rs, trx);
		return p;
	}

	public static PO FactoryPO(Class<?> c, Properties ctx, int id, String trx) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> con = c.getConstructor(Properties.class, int.class, String.class);
		PO p = (PO)con.newInstance(ctx, id, trx);
		return p;
	}

	public static ArrayList FactoryPO(Properties ctx, String trx, String TableName, String WhereCond, Class<?> c, Object[] params, int countLimit) {
		String sql = "SELECT * FROM " + TableName + " WHERE " + WhereCond;
		return FactoryPO(ctx, trx, sql, c, params, countLimit);
	}
	
	public static ArrayList FactoryPO(Properties ctx, String trx, String sql, Class<?> c, Object[] params, int countLimit) {
		
		ArrayList ret = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int i = 1;
		
		try {
			ps = DB.prepareStatement(sql, trx);
			 
			if (params != null) {
				for (Object o : params)
					ps.setObject(i++, o);
			}
			
			rs = ps.executeQuery();
			
			try {
				while (rs.next() && ret.size() < countLimit) {
					PO p = FactoryPO(c, ctx, rs, trx);
					
					ret.add(p);
					int newID = p.getID();
					
					s_log.info("Cargado PO " + c.getSimpleName() + " con ID=" + newID);
				}				
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			if (ret.size() == 0) {
				s_log.fine("No se ha encontrado clave de busqueda.");
			}
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "FactoryPO", e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {}
			}
			
			if (rs != null) { 
				try {
					rs.close();
				} catch (SQLException e) {}
			}
		}
		
		return ret;
	}
	
	public static String GetTableName(int AD_Table_ID) {
		return (String)DB.getSQLObject(null, "SELECT TableName FROM AD_Table WHERE AD_Table_ID = ? ", new Object[]{AD_Table_ID});
	}

	public static int GetTableIdByName(String TableName) {
		Integer ii = (Integer)DB.getSQLObject(null, "SELECT AD_Table_ID FROM AD_Table WHERE lower(tablename) = ? ", new Object[]{TableName.toLowerCase()});
		if (ii != null)
			return ii;
		return 0;
	}

	public static String GetColumnName(int AD_Column_ID) {
		return (String)DB.getSQLObject(null, "SELECT columnname FROM AD_Column WHERE AD_Column_ID = ? ", new Object[]{AD_Column_ID});
	}

	public static Integer GetColumnIdByName(int AD_Table_ID, String ColumnName) {
		Integer ii = (Integer)DB.getSQLObject(null, "SELECT AD_Column_ID FROM AD_Column WHERE AD_Table_ID = ? AND lower(columnname) = ? ", new Object[]{AD_Table_ID, ColumnName.toLowerCase()});
		if (ii != null)
			return ii;
		return 0;
	}
	
	public ImpExPoCommon(ImpExPoAdapter poAdapter) {
		init(poAdapter);
	}
	
	private void init(ImpExPoAdapter poAdapter) {
		poAd = poAdapter;
		po = (PO)poAd;
		p_info = poAd.pa_GetPOInfo();
	}
	
	public ImpExPoAdapter doImport(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		// Guaramos el id original
		int orig_ID = po.getID();
		
		ImpExPoAdapter newobj = poAd.searchCurrentObject(ctx, trx, parentOld, parentNew);
		
		if (newobj == null) {
			try {
				newobj = (ImpExPoAdapter)FactoryPO(po.getClass(), ctx, 0, trx);
			} catch (Exception e) {
				
			}
		}
		
		PO newobjPO = (PO)newobj;
		
		// Copiamos el objeto leido al nuevo
		newobj.copyValuesFrom(po,0,0);
		
		
		// Se invoca cuando termina de procesar todo
		newobj.setCustomDataFrom(po, parentOld, parentNew, 0);
		
		// Guardamos el registro actualizado.
		if (newobjPO.save(trx) == false)	{
			log.fine("No se ha podido guardar.");
			return null;
		}
		
		// Guardamos el nuevo ID
		int imp_ID = newobjPO.getID();
		
		newobj.setImp_ID(imp_ID);
		newobj.setOrig_ID(orig_ID);
		
		poAd.setImp_ID(imp_ID);
		poAd.setOrig_ID(orig_ID);
		
		Convert.setNewId(newobjPO.getClass(), orig_ID, imp_ID);
		Convert.getImportedObjects().add(new ImportedObj(newobj,po,parentOld,parentNew));
		
		// Cargamos la traduccion
		if (importTrl(trx) == false)	{
			return null;
		}
		
		return newobj;
	}
	
	/********************************************************************
	 * Importar y exportar Traduccion
	 ********************************************************************/
		
	//
	
	protected transient CLogger	log = CLogger.getCLogger (getClass());
	
	private Object readResolve() {
		log = CLogger.getCLogger(getClass());
		init(poAd);
		return this;
	 }
	
	/**
	 * Exporta la traduccion
	 * Se debe repetir en todas las clases para evitar tocar PO
	 * @return
	 */
	public boolean exportTrl()	{
		// Si no tiene traduccion, salimos
		if (!p_info.isTranslated())	{
			log.info("No existe traduccion.");
			return true;
		}
		
		// Obtenemos los idiomas activos
		String[] lang = es.indeos.transform.Convert.getLanguages();
		if (lang ==null)	{
			log.log(Level.SEVERE, "No se han encontrado paises para los que generar la traduccion.");
			return false;
		}
		
		// Por cada uno de los lenguages activos..
		for (int x=0; x< lang.length; x++)	{
			String AD_Language = lang[x];
			log.info("Exportando traduccion: "+ AD_Language);
			
			poAd.setTrlMap(new HashMap());
			
			Map trlMap = poAd.getTrlMap();

			// Exportamos la traduccion de cada columna
			for (int i=0; i < p_info.getColumnCount(); i++)	{
				// Si la columna esta traducida
				if (p_info.isColumnTranslated(i))	{
					String col = p_info.getColumnName(i);
					String trl = poAd.pa_get_Translation(col , AD_Language);
					
					if (trl != null)	{
						TrlConvert trlconv = new TrlConvert(AD_Language, col, trl);
						// Guardamos la traduccion en el HashMap
						trlMap.put(col, trlconv);
						log.info("Campo: " + col + " - Traduccion: " + trl);
					}
				}
			}
		}
			return true;
	}
	
	/**
	 * Importa la traduccion
	 * @return
	 */
	public boolean importTrl(String trx)	{
		Map trlMap = poAd.getTrlMap();
		
		if (trlMap == null)	{
			log.info("No existe traduccion.");
			return true;
		}
		// Buscamos todas las traducciones
		Iterator keys = trlMap.keySet().iterator();
		while (keys.hasNext())	{
			TrlConvert trlcon = (TrlConvert)trlMap.get(keys.next());
			// Y las guardamos
			if (saveTrl(trlcon, trx) == false)	{
				return false;
			}
		}
		return true;
	}

	/**
	 * Guaramos la traduccion
	 * @param trl
	 * @return
	 */
	private boolean saveTrl(TrlConvert trl, String trx)	{
		
		String keycolumn = po.get_TableName() + "_ID";	// Obtenemos el keyName al estilo Compiere
		String column = trl.getColumn();
		String AD_Language = trl.getAD_Language();
		String translation = trl.getTrl();
		int ID = poAd.getImp_ID();	// El ID sera el nuevo
		
		log.info("Importando traduccion: " + column + " - " + AD_Language);
		
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(po.get_TableName()).append("_Trl ");
		sql.append("set ").append(column).append("=? ");
		sql.append("where ").append(keycolumn).append("=? ");
		sql.append(" and ad_language=?");
		
		try	{
			PreparedStatement pstmt = DB.prepareStatement (sql.toString(), trx);
			pstmt.setString(1,  translation);
			pstmt.setInt(2, ID);
			pstmt.setString(3, AD_Language);
			
			pstmt.executeUpdate();
				
			pstmt.close ();
			pstmt = null;
			return true;
		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, "Error importando la traduccion de: " + column + " - " + e.toString());
			return false;
		}

	}
	
	//
	
	private ImpExPoAdapter poAd = null;
	private transient PO po = null;
	private transient POInfo p_info = null;
	
}
