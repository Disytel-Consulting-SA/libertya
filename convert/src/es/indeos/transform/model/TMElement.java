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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.*;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class TMElement extends X_AD_Element implements ImpExPoAdapter {
	
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
	 * Standard Constructor
	 * @param ctx
	 * @param AD_Element_ID
	 * @param trxName
	 */
	public TMElement(Properties ctx, int AD_Element_ID, String trxName) {
		super(ctx, AD_Element_ID, trxName);
	}

	/**
	 * Load Constructor
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public TMElement(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	
	public static TMElement getElement(Properties ctx, String columnname, String trx )	{
		// Buscamos elementos con ese columnname
		return (TMElement)ImpExPoCommon.FactoryPO(ctx, trx, Table_Name, "columnname=?", TMElement.class, new Object[]{columnname});
		
		/*
		String sql = "SELECT * FROM AD_Element WHERE columnname=?";
		PreparedStatement pstmt = null;
		TMElement elem = null;
		try	{
			pstmt = DB.prepareStatement (sql, trx);
			pstmt.setString(1, columnname);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				 elem = new TMElement(ctx, rs, trx);
				s_log.info("Cargado Elemento " + elem.getName() + " con AD_Element=" + elem.getAD_Element_ID());
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return elem;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMElement"+ e.toString());
			return null;
		}
		*/
	}
	

	/**
	 * Importa el AD_Element.
	 * @param ctx
	 * @param trx
	 * @return true si la importacion tiene exito
	 */
	/*
	public boolean doImport_old (Properties ctx, String trx)	{
		// ID Original
		orig_ID = getAD_Element_ID();
		
		TMElement no = null;
		
		no = getElement(ctx, getColumnName(), trx);
		
		if (no != null)	{
			log.info("Actualizando Element " + no.getName());
		}
		else {
			no =  new TMElement(ctx, 0, trx);	
		}
		
		PO.copyValues(this, no, 0, 0);
		
		// Guardamos el registro actualizado.
		if (no.save(trx) == false)	{
			log.fine("No se ha podido guardar el elemento.");
			return false;
		}
		
		imp_ID = no.getAD_Element_ID();
		
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
		// TODO Auto-generated method stub
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

	public ImpExPoAdapter searchCurrentObject(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew) {
		TMPrintFormat pn = (TMPrintFormat)parentNew;
		
		return getElement(ctx, getColumnName(), trx);
	}

	public void setImp_ID(int x) {
		imp_ID = x;		
	}

	public void setOrig_ID(int x) {
		orig_ID = x;
	}

	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo) {
		
	}
}
