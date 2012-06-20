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
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.*;
import org.openXpertya.util.DB;
import org.openXpertya.util.CLogger;

public class TMRefList extends MRefList {
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
	    return this;
	 }
	
	
	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param AD_Tab_ID id
	 */
	public TMRefList (Properties ctx, int AD_Ref_List_ID, String trx)	{
		super (ctx, AD_Ref_List_ID, trx);
		exportTrl();
	}
	
	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public TMRefList (Properties ctx, ResultSet rs, String trx)
	{
		super (ctx, rs, trx);
		exportTrl();
	}
	
	
	/**
	 * Devuelve la validacion de lista con la clave de busqueda dada.
	 * @param ctx
	 * @param AD_Tab_ID
	 * @param name
	 * @return el registro o null si no lo encuentra
	 */	
	public static TMRefList getRefList(Properties ctx, int AD_Reference_ID, String value, String trx)	{
		// Buscamos los registros
		
		String sql = "SELECT * FROM AD_Ref_List WHERE AD_Reference_ID=? and value=?";
		PreparedStatement pstmt = null;
		TMRefList o = null;
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Reference_ID);
			pstmt.setString(2, value);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				 o = new TMRefList(ctx,rs, trx);
				s_log.info("Cargada Validacion " + o.getValue() + " : " + o.getAD_Ref_List_ID());
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return o;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMRefList"+ e.toString());
			return null;
		}
	}

	
	
	/**
	 * Importa la refecencia de lista.
	 * @param ctx
	 * @param trx
	 * @return true si la importacion tiene exito
	 */
	public boolean doImport (Properties ctx, String trx)	{
		// ID Original
		orig_ID = getAD_Ref_List_ID();
		
		TMRefList no = null;
		// Buscamos el campo por la pestaña y el AD_Column_ID,
		// ya que una restriccion evita que se creen dos campos
		// para la misma columna en una pestaña.
		no = getRefList(ctx, getAD_Reference_ID(), getValue(), trx);
		
		if (no != null)	{
			log.info("Actualizando campo " + no.getAD_Ref_List_ID());
		}
		else {
			no =  new TMRefList(ctx, 0, trx);	
		}
		
		PO.copyValues(this, no, 0, 0);

		// Ya que el metodo copyValues, solo mira los valores antiguos, 
		// debemos recargar el valor.
		no.setAD_Reference_ID(getAD_Reference_ID());
		
		//		 Guardamos el registro actualizado.
		if (no.save(trx) == false)	{
			log.fine("No se ha podido guardar la RefList.");
			return false;
		}
		
		imp_ID = no.getAD_Ref_List_ID();
		
		// Importamos traduccion
		if (importTrl(trx) == false)	{
			return false;
		}
		
		return true;
	}
	
	/********************************************************************
	 * Importar y exportar Traduccion
	 ********************************************************************/
	
	/** HashMap con la traduccion 		*/
	private HashMap trlMap = null;
	
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
			
			trlMap = new HashMap();

			// Exportamos la traduccion de cada columna
			for (int i=0; i < p_info.getColumnCount(); i++)	{
				// Si la columna esta traducida
				if (p_info.isColumnTranslated(i))	{
					String col = p_info.getColumnName(i);
					String trl = get_Translation(col , AD_Language);
					
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
		
		String keycolumn = get_TableName() + "_ID";	// Obtenemos el keyName al estilo Compiere
		String column = trl.getColumn();
		String AD_Language = trl.getAD_Language();
		String translation = trl.getTrl();
		int ID=getImp_ID();	// El ID sera el nuevo
		
		log.info("Importando traduccion: " + column + " - " + AD_Language);
		
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(get_TableName()).append("_Trl ");
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

	
}
