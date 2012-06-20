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


import org.openXpertya.model.*;

import org.openXpertya.util.*;

import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Level;


public class TMWindow extends M_Window implements ImpExPoAdapter {

	/** Static CLogger					*/
	static CLogger		s_log = CLogger.getCLogger (TMWindow.class);
	
	/** AD_Window_ID Original	*/
	private int orig_AD_Window_ID;
	
	/** AD_Window_ID Importado	*/
	private int imp_AD_Window_ID;
	
	/**
	 * Devuelve el id con el que se ha exportado el registro.
	 * @return
	 */
	public int getOrig_ID()	{
		return orig_AD_Window_ID;
	}

	/**
	 * Devuelve el id con el que se ha importado el registro.
	 * @return
	 */
	public int getImp_ID()	{
		return imp_AD_Window_ID;
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
	
	
	/**
	 * Constructor
	 * @param ctx Context info
	 * @param rs ResultSet
	 */
	public TMWindow(Properties ctx, ResultSet rs, String trx)	{
		super(ctx, rs, trx);
		log.info("Creada la ventana: "+ getName());
	}

	/**
	 * Constructor
	 * @param ctx Properties
	 * @param AD_Window_ID ID de la ventana a exportar.
	 */
	public TMWindow(Properties ctx, int AD_Window_ID, String trx)	{
		super(ctx, AD_Window_ID, trx);
	}

	
	public static TMWindow getTMWindow (Properties ctx, String name, String trx)	{
		return (TMWindow)ImpExPoCommon.FactoryPO(ctx, trx, "AD_Window", "name=?", TMWindow.class, new Object[]{name});
		
/*
		String sql = "SELECT * FROM AD_Window WHERE name=?";
		
		PreparedStatement pstmt = null;
		
		TMWindow m_w = null;
		int AD_Window_ID;
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setString(1,  name);
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				m_w = new TMWindow(ctx,rs, trx);
				AD_Window_ID = rs.getInt("AD_Window_ID");
				s_log.info("Cargada ventana "+ m_w.getName() +" con AD_Window_ID=" + AD_Window_ID);
			}
			else {
				s_log.fine("No se ha encontrado ninguna ventana con esa clave de busqueda.");
				return null;
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return m_w;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando M_Window"+ e.toString());
			return null;
		}
*/
	}
	
	public ImpExPoAdapter searchCurrentObject(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		// Comprobamos si la ventana ya existe para actualizarla
		TMWindow newwin = null; 
		newwin = getTMWindow(ctx, getName(), trx);
		
		if (newwin != null)	{
			log.info("Actualizando AD_Window " + getAD_Window_ID());
		}
		else {
			newwin = new TMWindow(ctx,0, trx);
		}
		
		return newwin;
	}
	
	public ImpExPoAdapter doImport(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew)	{		
		return tie.doImport(ctx, trx, parentOld, parentNew);
	}
	
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

	public void setImp_ID(int x) {
		imp_AD_Window_ID = x;
	}

	public void setOrig_ID(int x) {
		orig_AD_Window_ID = x;
	}

	public void setCustomDataFrom(PO old, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {

	}
	
}

