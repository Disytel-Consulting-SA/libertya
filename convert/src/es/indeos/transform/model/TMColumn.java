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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.*;
import org.openXpertya.util.DB;
import org.openXpertya.util.CLogger;

import es.indeos.transform.Convert;

public class TMColumn extends M_Column implements ImpExPoAdapter {

	/** Static CLogger					*/
	static CLogger		s_log = CLogger.getCLogger (TMColumn.class);

	/** ID Original	*/
	private int orig_ID;
	
	/** ID Importado	*/
	private int imp_ID;
	
	
	/** Element	*/
	private TMElement m_element;
	
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
	 *  Carga el TMColumn y su element correspondiente
	 *	@param ctx context
	 *	@param AD_Column_ID
	 */
	public TMColumn (Properties ctx, int AD_Column_ID, String trx)
	{
		super (ctx, AD_Column_ID, trx);
		
		// Cargamos el elemento
		
		if (AD_Column_ID != 0)	{
			m_element = new TMElement(ctx, getAD_Element_ID(), trx);
		}
		
	}	

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public TMColumn (Properties ctx, ResultSet rs, String trx)
	{
		super (ctx, rs, trx);
		try	{
			m_element = new TMElement(ctx, rs.getInt("AD_Element_ID"), trx);
		}
		catch (SQLException e)	{
			log.info("No se ha podido cargar el elemento:" + e.toString());
		}
	}	
	
	/**
	 * Devuelve la columna indicada
	 * @param ctx
	 * @param AD_Table_ID
	 * @param columnName
	 * @return La columna o null si no la encuentra.
	 */	
	public static TMColumn getColumn(Properties ctx, int AD_Table_ID, String columnName, String trx)	{
		// Buscamos todas las pestaï¿½as de esta ventana.
		String sql = "SELECT * FROM AD_Column WHERE AD_Table_ID=? and columnName=?";
		PreparedStatement pstmt = null;
		TMColumn col = null;
		try	{
			pstmt = DB.prepareStatement (sql, trx);
			pstmt.setInt(1, AD_Table_ID);
			pstmt.setString(2, columnName);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				 col = new TMColumn(ctx,rs, trx);
				s_log.info("Cargada Columna " + col.getColumnName() + " con AD_Column_ID=" + col.getAD_Column_ID());
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return col;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMColumn" +  e.toString());
			return null;
		}
		

	}
	
	
	/**
	 * Devuelve un HashMap con todas los campos de la tabla enviada
	 * @param ctx
	 * @param AD_Table_ID
	 * @param trx 
	 * @return
	 */
	public static HashMap getColumns(Properties ctx, int AD_Table_ID, String trx)	{
		HashMap colsmap = new HashMap();
				
		// Buscamos todas las pestaï¿½as de esta ventana.
		String sql = "SELECT * FROM AD_Column WHERE AD_Table_ID=? ";
		PreparedStatement pstmt = null;
	
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Table_ID);
			
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())	{
				// Las cargamos
				TMColumn col = new TMColumn(ctx,rs, trx);
				int AD_Column_ID = rs.getInt("AD_Column_ID");
				
				// Exportamos traduccion
				col.exportTrl();
				
				// Y las guardamos en el hashmap
				colsmap.put(String.valueOf(AD_Column_ID), col);
				s_log.info("Cargada Columna " + rs.getString("name") + " con AD_Column_ID=" + AD_Column_ID);
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			s_log.info("Cargadas "+ colsmap.size()+" Columnas");
			return colsmap;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMColumn"+ e.toString());
			return null;
		}
		
	}

	/**
	 * Importa la columna.
	 * @param ctx
	 * @param trx
	 * @return true si la importacion tiene exito
	 */
	/*
	public boolean doImport (Properties ctx, String trx)	{
		// ID Original
		orig_ID = getAD_Column_ID();
		
		TMColumn no = null;
		
		if (m_element == null)	{
			log.log(Level.SEVERE, "No se ha encontrado AD_element para esta columna");
			return false;
		}
		
		m_element.doImport(ctx, trx);
		
		no = getColumn(ctx, getAD_Table_ID(), getColumnName(), trx);
		
		if (no != null)	{
			log.info("Actualizando Columna " + no.getAD_Column_ID());
		}
		else {
			no =  new TMColumn(ctx, 0, trx);	
		}
		
		PO.copyValues(this, no, 0, 0);

		// Recopiamos
		no.setAD_Table_ID(getAD_Table_ID());
		no.setAD_Val_Rule_ID(getAD_Val_Rule_ID());
		no.setAD_Element_ID(m_element.getImp_ID());

		// Las referencias se deben actualizar luego
		no.setAD_Reference_Value_ID(0);
	
		
		// Guardamos el registro actualizado.
		if (no.save(trx) == false)	{
			log.fine("No se ha podido guardar la columna.");
			return false;
		}
		
		imp_ID = no.getAD_Column_ID();
		
		// Importamos traduccion
		if (importTrl(trx) == false)	{
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
		TMColumn no = null;
		
		if (m_element == null)	{
			log.log(Level.SEVERE, "No se ha encontrado AD_element para esta columna");
			return null;
		}
		
		m_element.doImport(ctx, trx, null, null);
		
		no = getColumn(ctx, getAD_Table_ID(), getColumnName(), trx);
		
		if (no != null)	{
			log.info("Actualizando Columna " + no.getAD_Column_ID());
		}
		else {
			no =  new TMColumn(ctx, 0, trx);	
		}
		
		return no;
	}

	public void setImp_ID(int x) {
		orig_ID = x;
	}

	public void setOrig_ID(int x) {
		imp_ID = x;
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		TMColumn oldcol = (TMColumn)oldpo;
		
		if (passNo == 0) {
			// Recopiamos
			setAD_Table_ID(oldcol.getAD_Table_ID());
			setAD_Val_Rule_ID(oldcol.getAD_Val_Rule_ID());
			setAD_Element_ID(oldcol.m_element.getImp_ID());
	
			// Las referencias se deben actualizar luego
			setAD_Reference_Value_ID(0);
		} else if (passNo == 1) {
			// HACK: Sobreescribo lo que se asigna desde TableMap
			setAD_Table_ID(Convert.getNewId(TMTable.class, oldcol.getAD_Table_ID()));
			setAD_Val_Rule_ID(Convert.getNewId(TMValRule.class, oldcol.getAD_Val_Rule_ID()));
			setAD_Element_ID(Convert.getNewId(TMElement.class, oldcol.getAD_Element_ID()));
			setAD_Reference_Value_ID(Convert.getNewId(TMReference.class, oldcol.getAD_Reference_Value_ID()));
		}
	}
}
