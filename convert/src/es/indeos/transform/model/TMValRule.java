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


public class TMValRule extends X_AD_Val_Rule implements ImpExPoAdapter {
	
	/** Static CLogger					*/
	static CLogger		s_log = CLogger.getCLogger (TMRefTable.class);

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
	public TMValRule (Properties ctx, int AD_Val_Rule_ID, String trx)	{
		super (ctx, AD_Val_Rule_ID, trx);
	}
	
	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public TMValRule(Properties ctx, ResultSet rs, String trx)
	{
		super (ctx, rs, trx);
	}
	
	/**
	 * Devuelve la ValRule indicada
	 * @param ctx
	 * @param Name
	 * @return La ValRule o null si no la encuentra.
	 */	
	public static TMValRule getValRule(Properties ctx, String name, String trx)	{
		return (TMValRule)ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "name=?", TMValRule.class, new Object[]{name});
		/*
		// Buscamos todas las pestañas de esta ventana.
		String sql = "SELECT * FROM AD_Val_Rule WHERE name=?";
		PreparedStatement pstmt = null;
		TMValRule val = null;
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setString(1, name);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				 val = new TMValRule(ctx, rs, trx);
				s_log.info("Cargada ValRule " + val.getName() + " con AD_Val_Rule_ID=" + val.getAD_Val_Rule_ID());
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return val;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMValRule"+ e.toString());
			return null;
		}
		*/
	}
	

	
	
	/**
	 * Importa la Validacion.
	 * @param ctx
	 * @param trx
	 * @return true si la importacion tiene exito
	 */
	/*
	public boolean doImport (Properties ctx, String trx)	{
		// ID Original
		orig_ID = getAD_Val_Rule_ID();
		
		TMValRule no = null;
		
		no = getValRule(ctx, getName(), trx);
		
		if (no != null)	{
			log.info("Actualizando ValidationRule " + no.getName());
		}
		else {
			no =  new TMValRule(ctx, 0, trx);	
		}
		
		PO.copyValues(this, no, 0, 0);
		
		// Guardamos el registro actualizado.
		if (no.save(trx) == false)	{
			log.fine("No se ha podido guardar la columna.");
			return false;
		}
		
		imp_ID = no.getAD_Val_Rule_ID();
		
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
		return getValRule(ctx, getName(), trx);
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		// TODO Auto-generated method stub
		
	}
	
}
