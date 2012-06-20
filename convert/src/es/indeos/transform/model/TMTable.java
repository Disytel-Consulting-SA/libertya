/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Diseño y desarrollo por Indeos Consultoria S.L.
 * 
 * Modificado por TecnoXP
 * 
 */

package es.indeos.transform.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;

import org.openXpertya.model.*;
import org.openXpertya.util.DB;
import org.openXpertya.util.CLogger;

import es.indeos.transform.Convert;

public class TMTable extends M_Table implements ImpExPoAdapter {


	/** Static CLogger					*/
	private static CLogger s_log = CLogger.getCLogger (TMWindow.class);

	
	/** ID Original	*/
	private int orig_ID;
	
	/** ID Importado	*/
	private int imp_ID;
	
	/**
	 * Devuelve el id con el que se ha exportado el registro.
	 * @return
	 */
	public int getOrig_ID()	{
		return orig_ID;
	}

	/**
	 * Devuelve el id con el que se ha importado el registro.
	 * @return
	 */
	public int getImp_ID()	{
		return imp_ID;
	}
	
	/**
	 * Inicializa los campos transient despues de las deserializacion
	 * @return
	 */
	private Object readResolve() {
		log = CLogger.getCLogger(getClass());
		if (tie == null) tie = new ImpExPoCommon(this);
		return this;
	 }
	
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param AD_Table_ID id
	 */
	public TMTable (Properties ctx, int AD_Table_ID, String trx)
	{
		super (ctx, AD_Table_ID, trx);
		
	}	//	

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public TMTable (Properties ctx, ResultSet rs, String trx)
	{
		super (ctx, rs, trx);
	}	//	
	
	/**
	 * Devuelve la tabla indicada
	 * @param ctx
	 * @param AD_Table_ID
	 * @param columnName
	 * @return La columna o null si no la encuentra.
	 */	
	public static TMTable getTable(Properties ctx, String tableName, String trx)	{
		return (TMTable)ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "tableName=?", TMTable.class, new Object[]{tableName});
		/*
		// Buscamos todas las pestaÃ±as de esta ventana.
		String sql = "SELECT * FROM AD_Table WHERE tableName=?";
		PreparedStatement pstmt = null;
		TMTable tab = null;
		try	{
			pstmt = DB.prepareStatement (sql, trx);
			pstmt.setString(1, tableName);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				 tab = new TMTable(ctx,rs, trx);
				s_log.info("Cargada Tabla " + tab.getTableName() + " con AD_Table_ID=" + tab.getAD_Table_ID());
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return tab;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMTable"+ e.toString());
			return null;
		}
		*/
	}

	
	
	/**
	 * Devuelve una lista de las claves de elementos 
	 * relacionados con esta tabla
	 * @param field Campo que queremos buscar
	 * @param AD_Table_ID
	 * @return Lista de las claves encontradas.
	 */
	private static int[] getRelated(String field, int AD_Table_ID)	{		
		ArrayList list = new ArrayList();

		// Buscamos los ad_reference_value_id asociados a esta tabla. 
		String sql = "select distinct c." + field +
				" from ad_column c" +
				" where c." + field + " is not null" +
				" and c.ad_table_id=?";
		PreparedStatement pstmt = null;
		
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Table_ID);
			ResultSet rs = pstmt.executeQuery ();
		
			while (rs.next ())	{
				// Los guaramos en  la lista.
				list.add( String.valueOf( rs.getInt(field) ) );
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando Buscando " + field + e.toString());
			return null;
		}

		// Convertimos el array 
		int[] rel = new int[list.size()];
		for (int i=0;i < list.size(); i++)	{
			rel[i] = Integer.parseInt((String)list.get(i));
		}
		
		return rel;
	}
	
	
	/**
	 * Busca ReferenceKeysValues relacionados con la tabla.
	 * @param AD_Table_ID
	 * @return Lista con los AD_Reference encontrados. 
	 */
	public static int[] getReferenceKeys(int AD_Table_ID)	{
		return getRelated("ad_reference_value_id", AD_Table_ID);
	}
	
	/**
	 * Busca DynamicValidation Rules relacionados con la tabla.
	 * @param AD_Table_ID
	 * @return Lista con los AD_Val_Rule encontrados. 
	 */
	public static int[] getDynamicValidations(int AD_Table_ID)	{
		return getRelated("ad_val_rule_id", AD_Table_ID);
	}
	
	/**
	 * Importa el elemento al AD
	 * @param ctx
	 * @param trx
	 * @return
	 */
	/*
	public boolean doImport_OLD(Properties ctx, String trx)	{
		// ID Original
		orig_ID = getAD_Table_ID();
		

		// Copiasmos los valores.
		PO.copyValues(this, ntable, 0, 0);
		
		// Re seteamos
		ntable.setAD_Window_ID(getAD_Window_ID());

		// Guardamos el registro actualizado.
		if (ntable.save(trx) == false)	{
			log.fine("No se ha podido guardar la tabla.");
			return false;
		}
		log.info("Tabla importada. " + ntable.getName());
		
		imp_ID = ntable.getAD_Table_ID();
		
		// Importamos traduccion
		if (tie.importTrl(trx) == false)	{
			return false;
		}
		
		return true;
	}
*/

	protected ImpExPoCommon tie = new ImpExPoCommon(this);

	protected Map trlMap = null;

	public Map getTrlMap() {
		// TODO Auto-generated method stub
		return trlMap;
	}

	public void setTrlMap(Map trlMap) {
		this.trlMap = trlMap;
	}
	
	public POInfo pa_GetPOInfo() {
		return p_info;
	}

	public String pa_get_Translation(String columnName, String AD_Language) {
		return get_Translation(columnName, AD_Language);
	}

	public boolean exportTrl() {
		return tie.exportTrl();
	}

	public boolean importTrl(String trx) {
		return tie.importTrl(trx);
	}

	public void copyValuesFrom(PO other, int AD_Client_ID, int AD_Org_ID) {
		copyValues(other, this, AD_Client_ID, AD_Org_ID);
	}

	public ImpExPoAdapter doImport(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		return tie.doImport(ctx, trx, parentOld, parentNew);
	}

	public ImpExPoAdapter searchCurrentObject(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		TMTable ntable = null;

		// Comprobamos si la tabla existe en el sistema
		ntable = getTable(ctx, getTableName(), trx);
		
		if (ntable != null)	{
			log.info("Actualizando ventana " + ntable.getAD_Table_ID());
		}
		else {
			// Si no existe, creamos una nueva
			ntable = new TMTable(ctx, 0, trx);	
		}
		
		return ntable;
	}

	public void setImp_ID(int x) {
		imp_ID = x;
	}

	public void setOrig_ID(int x) {
		orig_ID = x;
	}

	public void setCustomDataFrom(PO old, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		TMTable oldt = (TMTable)old;
		
		if (passNo == 1) {
			setAD_Window_ID(Convert.getNewId(TMWindow.class, oldt.getAD_Window_ID()));
		} else {
			setAD_Window_ID(0);
		}
	}
}


