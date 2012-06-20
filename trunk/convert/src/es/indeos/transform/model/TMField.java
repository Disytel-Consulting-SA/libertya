/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Dise�o y desarrollo por Indeos Consultoria S.L.
 * 
 * Modificado por TecnoXP
 * 
 */

package es.indeos.transform.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.*;
import org.openXpertya.util.DB;
import org.openXpertya.util.CLogger;

public class TMField extends M_Field implements ImpExPoAdapter {

	/** Static CLogger					*/
	static CLogger		s_log = CLogger.getCLogger (TMField.class);

	/** ID Original	*/
	private int orig_ID;
	
	/** ID Importado	*/
	private int imp_ID;
	
	/** */
	protected ImpExPoCommon tie = new ImpExPoCommon(this);
	
	protected Map trlMap = null;

	public Map getTrlMap() {
		// TODO Auto-generated method stub
		return trlMap;
	}

	public void setTrlMap(Map trlMap) {
		this.trlMap = trlMap;
	}
	
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
	
	
	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param AD_Field_ID id
	 */
	public TMField (Properties ctx, int AD_Field_ID, String trx)
	{
		super (ctx, AD_Field_ID, trx);
	}	

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public TMField (Properties ctx, ResultSet rs, String trx)
	{
		super (ctx, rs, trx);
	}

	/**
	 * Devuelve el campo que de la pestaña que enlaza al campo de base de datos dado
	 * @param ctx
	 * @param AD_Tab_ID
	 * @param name
	 * @return el registro o null si no lo encuentra
	 */	
	public static TMField getField(Properties ctx, int AD_Tab_ID, int AD_Column_ID, String trx)	{
		// Buscamos todas las pestañas de esta ventana.
		String sql = "SELECT * FROM AD_Field WHERE AD_Tab_ID=? and ad_column_id=?";
		PreparedStatement pstmt = null;
		TMField o = null;
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Tab_ID);
			pstmt.setInt(2, AD_Column_ID);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				 o = new TMField(ctx,rs, trx);
				s_log.info("Cargado registro " + o.getName() + " con AD_Field_ID=" + o.getAD_Field_ID());
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return o;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMField"+ e.toString());
			return null;
		}
		

	}
	
	
	/**
	 * Devuelve un HashMap con todas los campos de la pestaña enviada
	 * @param ctx
	 * @param AD_Tab_ID
	 * @return
	 */
	public static HashMap getFields(Properties ctx, int AD_Tab_ID, String trx)	{
		HashMap omap = new HashMap();
				
		// Buscamos todas las pestañas de esta ventana.
		String sql = "SELECT * FROM AD_Field WHERE AD_Tab_ID=? ";
		PreparedStatement pstmt = null;
	
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Tab_ID);
			
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())	{
				// Las cargamos
				TMField col = new TMField(ctx,rs, trx);
				int AD_Field_ID = rs.getInt("AD_Field_ID");
				
				// Exportamos traduccion
				col.exportTrl();
				
				// Y las guardamos en el hashmap
				omap.put(String.valueOf(AD_Field_ID), col);
				s_log.info("Cargada Columna " + rs.getString("name") + " con AD_Field_ID=" + AD_Field_ID);
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			s_log.info("Cargadas "+ omap.size()+" Columnas");
			return omap;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMField"+ e.toString());
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
		orig_ID = getAD_Field_ID();
		
		TMField no = null;
		// Buscamos el campo por la pestaña y el AD_Column_ID,
		// ya que una restriccion evita que se creen dos campos
		// para la misma columna en una pestaña.
		no = getField(ctx, getAD_Tab_ID(), getAD_Column_ID(), trx);
		
		if (no != null)	{
			log.info("Actualizando campo " + no.getAD_Field_ID());
		}
		else {
			no =  new TMField(ctx, 0, trx);	
		}
		
		PO.copyValues(this, no, 0, 0);
		// Recopiamos
		no.setAD_Tab_ID(getAD_Tab_ID());
		no.setAD_Column_ID(getAD_Column_ID());

		//		 Guardamos el registro actualizado.
		if (no.save(trx) == false)	{
			log.fine("No se ha podido guardar campo.");
			return false;
		}
		
		imp_ID = no.getAD_Field_ID();
		
		// Importamos traduccion
		if (importTrl(trx) == false)	{
			return false;
		}
		
		return true;
	}
	*/
	
	public void copyValuesFrom(PO other, int AD_Client_ID, int AD_Org_ID) {
		copyValues(other, this, AD_Client_ID, AD_Org_ID);
	}

	public POInfo pa_GetPOInfo() {
		return p_info;
	}

	public String pa_get_Translation(String columnName, String AD_Language) {
		return get_Translation(columnName, AD_Language);
	}

	public ImpExPoAdapter searchCurrentObject(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		TMField no = null;
		// Buscamos el campo por la pestaña y el AD_Column_ID,
		// ya que una restriccion evita que se creen dos campos
		// para la misma columna en una pestaña.
		no = getField(ctx, getAD_Tab_ID(), getAD_Column_ID(), trx);
		
		if (no != null)	{
			log.info("Actualizando campo " + no.getAD_Field_ID());
		}
		else {
			no =  new TMField(ctx, 0, trx);	
		}
		
		return no;
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		TMField old = (TMField)oldpo;
		
		setAD_Tab_ID(old.getAD_Tab_ID());
		setAD_Column_ID(old.getAD_Column_ID());
	}

	public void setImp_ID(int x) {
		imp_ID = x;
	}

	public void setOrig_ID(int x) {
		orig_ID = x;
	}

	public ImpExPoAdapter doImport(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		return tie.doImport(ctx, trx, parentOld, parentNew);
	}

	public boolean exportTrl() {
		return tie.exportTrl();
	}

	public boolean importTrl(String trx) {
		return tie.importTrl(trx);
	}
	
}

