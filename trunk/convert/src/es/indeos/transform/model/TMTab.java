/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Diseño y desarrollo por Indeos Consultoria S.L.
 */

package es.indeos.transform.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;

import org.openXpertya.model.*;
import org.openXpertya.util.DB;
import org.openXpertya.util.CLogger;

public class TMTab extends M_Tab {

	
	/** Static CLogger					*/
	static CLogger		s_log = CLogger.getCLogger (TMTab.class);

	/** ID Original	*/
	private int orig_ID;
	
	/** ID Importado	*/
	private int imp_ID;

	/** Antiguo AD_Table_ID		*/
	private int oldAD_Table_ID = 0;
	
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
	
	public void set_OldAD_Table_ID(int id)	{
		oldAD_Table_ID = id;
	}
	
	public int  get_OldAD_Table_ID()	{
		return oldAD_Table_ID;
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
	public TMTab (Properties ctx, int AD_Tab_ID, String trx)	{
		super (ctx, AD_Tab_ID, trx);
	}
	
	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public TMTab (Properties ctx, ResultSet rs, String trx)
	{
		super (ctx, rs, trx);
	}	//	M_Tab
	

	/**
	 * Devuelve un HashMap con todas las pestañas del AD_Window_ID enviado
	 * @param ctx
	 * @param AD_Window_ID
	 * @return
	 */
	public static HashMap getTabs(Properties ctx, int AD_Window_ID, String trx)	{
		HashMap tabsmap = new HashMap();
		
		// Buscamos todas las pestañas de esta ventana.
		String sql = "SELECT * FROM AD_Tab WHERE AD_Window_ID=?";
		PreparedStatement pstmt = null;
	
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Window_ID);
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())	{
				// Las cargamos
				TMTab tab = new TMTab(ctx,rs, trx);
				int AD_Tab_ID = rs.getInt("AD_Tab_ID");
				
				// Exportamos traduccion
				tab.exportTrl();
				
				// Y las guardamos en el hashmap
				tabsmap.put(String.valueOf(AD_Tab_ID), tab);
				s_log.info("Cargada Pestaña " + tab.getName() +"con AD_Tab_ID=" + AD_Window_ID);
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return tabsmap;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando M_Tab"+ e.toString());
			return null;
		}
		
		
	}

	/**
	 * Devuelve el Tab con el nombre indicado y asociado a la ventana
	 * @param ctx
	 * @param AD_Window_ID Ventana
	 * @param name Nombre de la pestaña
	 * @return TMTab o null
	 */
	public TMTab getTab(Properties ctx, int AD_Window_ID, String name, String trx)	{
		
		// Buscamos todas las pestañas de esta ventana.
		String sql = "SELECT * FROM AD_Tab WHERE AD_Window_ID=? and name=?";
		PreparedStatement pstmt = null;
		TMTab tab = null;
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Window_ID);
			pstmt.setString(2, name);
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())	{
				// Las cargamos
				 tab = new TMTab(ctx,rs, trx);

				// Y las guardamos en el hashmap
				s_log.info("Cargada Pestaña " + tab.getName() +"con AD_Tab_ID=" + tab.getAD_Tab_ID());
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return tab;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando M_Tab"+ e.toString());
			return null;
		}
		
		
	}
	
	
	public boolean doImport(Properties ctx, String trx)	{
		// ID Original
		orig_ID = getAD_Tab_ID();
		
		TMTab no = null;

		// Buscamos la pestaña por si existe.
		no = getTab(ctx, getAD_Window_ID(), getName(), trx);
		
		if (no != null)	{
			log.info("Actualizando Pestaña " + no.getAD_Tab_ID());
		}
		else {
			no =  new TMTab(ctx, 0, trx);	
		}
		
		PO.copyValues(this, no, 0, 0);

		// Recopiamos
		no.setAD_Window_ID(getAD_Window_ID());
		no.setAD_Table_ID(getAD_Table_ID());
		
		//		 Guardamos el registro actualizado.
		if (no.save(trx) == false)	{
			log.fine("No se ha podido guardar la pestaña.");
			return false;
		}
		
		
		imp_ID = no.getAD_Tab_ID();
		
		//	Importamos traduccion
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
