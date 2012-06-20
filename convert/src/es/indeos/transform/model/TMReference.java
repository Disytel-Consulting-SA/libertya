/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Diseño y desarrollo por Indeos Consultoria S.L.
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

public class TMReference extends X_AD_Reference implements ImpExPoAdapter {

	/** Static CLogger					*/
	static CLogger		s_log = CLogger.getCLogger (TMReference.class);

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
	
	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param AD_Tab_ID id
	 */
	public TMReference (Properties ctx, int AD_Reference_ID, String trx)	{
		super (ctx, AD_Reference_ID, trx);
	}
	
	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public TMReference (Properties ctx, ResultSet rs, String trx)
	{
		super (ctx, rs, trx);
	}	//	M_Tab
	
	
	/**
	 * Devuelve un HashMap con todas los Validaciones de lista
	 * de la referencia indicada
	 * @param ctx
	 * @param AD_Reference_ID
	 * @return
	 */
	public static HashMap getListValidation(Properties ctx, int AD_Reference_ID, String trx)	{
		HashMap valmap = new HashMap();
				
		// Buscamos todas las pestaï¿½as de esta ventana.
		String sql = "SELECT * FROM AD_Ref_List WHERE AD_Reference_ID=? ";
		PreparedStatement pstmt = null;
	
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Reference_ID);
			
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())	{
				// Las cargamos
				TMRefList ref = new TMRefList(ctx, rs, trx);
				int AD_Ref_List_ID = rs.getInt("AD_Ref_List_ID");

				// Exportamos traduccion
				ref.exportTrl();
				
				// Y las guardamos en el hashmap
				valmap.put(String.valueOf(AD_Ref_List_ID), ref);
				s_log.info("Cargada Referencia Lista " + rs.getString("name") + " con AD_Ref_List_ID=" + AD_Ref_List_ID);
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			s_log.info("Cargadas "+ valmap.size()+" Referencias de Lista");
			return valmap;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMColumn"+ e.toString());
			return null;
		}
	}

	/**
	 * Devuelve un HashMap con todas los Validaciones de Tabla
	 * de la referencia indicada
	 * @param ctx
	 * @param AD_Reference_ID
	 * @return
	 */
	public static TMRefTable getTableValidation(Properties ctx, int AD_Reference_ID, String trx)	{
		TMRefTable ref = null;
		// Buscamos todas las pestaï¿½as de esta ventana.
		String sql = "SELECT * FROM AD_Ref_Table WHERE AD_Reference_ID=? ";
		PreparedStatement pstmt = null;
	
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Reference_ID);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				ref = new TMRefTable(ctx, rs, trx);
				s_log.info("Cargada Referencia Tabla");
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return ref;

		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMColumn"+ e.toString());
			return null;
		}
	}
	
	/**
	 * Devuelve un TMReference con el nombre dado
	 * @param ctx
	 * @param name
	 * @return TMReference o null si no lo encuentra.
	 */
	public static TMReference getReference(Properties ctx, String name, String trx)	{
		return (TMReference)ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "Name=?", TMReference.class, new Object[]{name});
		/*
		// Buscamos todas las pestaï¿½as de esta ventana.
		String sql = "SELECT * FROM AD_Reference WHERE Name=?";
		PreparedStatement pstmt = null;
		TMReference ref = null;
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setString(1, name);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				 ref = new TMReference(ctx,rs, trx);
				s_log.info("Cargada Referencia " + ref.getName() + " con ID=" + ref.getAD_Reference_ID());
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return ref;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMReferences"+ e.toString());
			return null;
		}
		*/
	}
	
	
	/**
	 * Importa la refecencia
	 * @param ctx
	 * @param trx
	 * @return true si la importacion tiene exito
	 */
	/*
	public boolean doImport_old (Properties ctx, String trx)	{
		// ID Original
		orig_ID = getAD_Reference_ID();
		
		TMReference no = null;
		// Buscamos el campo por la pestaï¿½a y el AD_Column_ID,
		// ya que una restriccion evita que se creen dos campos
		// para la misma columna en una pestaï¿½a.
		no = getReference(ctx, getName(), trx);
		
		if (no != null)	{
			log.info("Actualizando campo " + no.getAD_Reference_ID());
		}
		else {
			no =  new TMReference(ctx, 0, trx);	
		}
		
		PO.copyValues(this, no, 0, 0);

		//		 Guardamos el registro actualizado.
		if (no.save(trx) == false)	{
			log.fine("No se ha podido guardar la Referencia.");
			return false;
		}

		imp_ID = no.getAD_Reference_ID();
		
		// Importamos traduccion
		if (importTrl(trx) == false)	{
			return false;
		}
		
		return true;
	}
*/
	
	protected ImpExPoCommon tie = new ImpExPoCommon(this);
	
	/** HashMap con la traduccion 		*/
	private Map trlMap = null;
	
	public Map getTrlMap() {
		return trlMap;
	}

	public void setTrlMap(Map trlMap) {
		this.trlMap = trlMap;
	}
	
	public boolean exportTrl() {
		return tie.exportTrl();
	}

	public boolean importTrl(String trx) {
		return tie.importTrl(trx);
	}

	public POInfo pa_GetPOInfo() {
		return p_info;
	}

	public String pa_get_Translation(String columnName, String AD_Language) {
		return get_Translation(columnName, AD_Language);
	}

	public ImpExPoAdapter doImport(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		return tie.doImport(ctx, trx, parentOld, parentNew);
	}

	public void copyValuesFrom(PO other, int AD_Client_ID, int AD_Org_ID) {
		copyValues(other, this, AD_Client_ID, AD_Org_ID);
	}

	public void setImp_ID(int x) {
		imp_ID = x;
	}

	public void setOrig_ID(int x) {
		orig_ID = x;
	}

	public ImpExPoAdapter searchCurrentObject(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		return getReference(ctx, getName(), trx);
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		// TODO Auto-generated method stub
		
	}	
}
