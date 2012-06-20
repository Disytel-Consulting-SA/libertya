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

public class TMRefTable extends X_AD_Ref_Table {

	/** Static CLogger					*/
	static CLogger		s_log = CLogger.getCLogger (TMRefTable.class);
	
	/** Nombre de la tabla a la que se referencia	*/
	private String tableName = null;
	
	/** Nombre de la columna que se mostrara	*/
	private String displayName = null;
	
	/** Nombre de la columna que hace de clave	*/
	private String keyName = null;
	

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
	public TMRefTable (Properties ctx, int AD_Ref_Table_ID, String trx)	{
		super (ctx, AD_Ref_Table_ID, trx);
		
		// Cargamos los nombres
		if (AD_Ref_Table_ID != 0)	{
			loadNames(ctx, trx);
		}
	}
	
	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public TMRefTable (Properties ctx, ResultSet rs, String trx)
	{
		super (ctx, rs, trx);

		// Cargamos los nombres
		loadNames(ctx, trx);
	}
	
	/**
	 * Inicializa los nombre de la tabla y columnas que necesitaremos
	 * al importar la referencia.
	 * @param ctx
	 * @param trx
	 */
	private void loadNames(Properties ctx, String trx)	{
		// Obtenemos el nombre de la tabla
		TMTable t = new TMTable(ctx, getAD_Table_ID(), trx);
		tableName = t.getTableName();
		
		// El nombre de la columna que se muestra
		TMColumn c = new TMColumn(ctx, getAD_Display(), trx);
		displayName = c.getColumnName();
		
		// Y la que hace de clave
		c = new TMColumn(ctx, getAD_Key(), trx);
		keyName = c.getColumnName();
	}
	
	public static TMRefTable getRefTable(Properties ctx, int AD_Reference_ID, String trx)	{
		//	 Buscamos todas las pestañas de esta ventana.
		String sql = "SELECT * FROM AD_Ref_Table WHERE AD_Reference_ID=?";
		PreparedStatement pstmt = null;
		TMRefTable ref = null;
		try	{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt(1, AD_Reference_ID);
			
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())	{
				// Las cargamos
				 ref = new TMRefTable(ctx,rs, trx);
				s_log.info("Cargada Referencia de Tabla  para ID=" + ref.getAD_Reference_ID());
				
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
			return ref;
		}
		catch (Exception e)
		{
			s_log.fine ("Error Cargando TMRefTable"+ e.toString());
			return null;
		}

	}
	
	
	/**
	 * Importa la refecencia de de tabla.
	 * @param ctx
	 * @param trx
	 * @return true si la importacion tiene exito
	 */
	public boolean doImport (Properties ctx, String trx)	{
		
		TMRefTable no = null;
		int AD_Reference_ID = getAD_Reference_ID();
		
		// Buscamos el campo por la pestaña y el AD_Reference_ID,
		// ya que una restriccion evita que se creen dos campos
		// para la misma columna en una pestaña.
		no = getRefTable(ctx, AD_Reference_ID, trx);
		
		if (no != null)	{
			log.info("Actualizando campo");
		}
		else {
			no =  new TMRefTable(ctx, 0, trx);	
		}
		
		PO.copyValues(this, no, 0, 0);
		
		// Obtenemos el nuevo AD_Table_ID de la tabla
		TMTable t = TMTable.getTable(ctx, tableName, trx);
		if (t == null)	{
			log.severe("No se ha encontrado tabla a la que asociar la referencia:" + tableName + ".");
			return false;
		}
		int AD_Table_ID = t.getAD_Table_ID();
		no.setAD_Table_ID(AD_Table_ID);
		
		// Obtenemos la nueva colunma de display
		TMColumn c = TMColumn.getColumn(ctx, AD_Table_ID, displayName, trx);
		if (c == null)	{
			log.severe("No se ha encontrado columna que debe mostrarse.");
			return false;
		}
		
		no.setAD_Display(c.getAD_Column_ID());
		
		// Obtenemos la nueva colunma de clave
		c = TMColumn.getColumn(ctx, AD_Table_ID, keyName, trx);
		if (c == null)	{
			log.severe("No se ha encontrado columna de clave.");
			return false;
		}
		no.setAD_Key(c.getAD_Column_ID());
		
		// Despues de copiar, hay que actualizar el AD_Reference_ID, ya que es una clave.
		no.setAD_Reference_ID(AD_Reference_ID);

		//		 Guardamos el registro actualizado.
		if (no.save(trx) == false)	{
			log.fine("No se ha podido guardar la RefTable.");
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
