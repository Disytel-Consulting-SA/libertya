/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Diseño y desarrollo por Indeos Consultoria S.L.
 */

package es.indeos.transform.controller;

import java.util.*;


import org.openXpertya.util.*;
import org.openXpertya.model.*;

import es.indeos.transform.model.*;



/**
 *  Clase empleada para cargar y almacenar la informacion de una tabla del AD
 *
 */
public class TableMap {
	
	
	/**	CLogger											*/
	protected CLogger			log = CLogger.getCLogger (getClass());
	
	/** TMTable			*/
	private TMTable table = null;

	/** HashMap con las columnas	*/
	private HashMap columns	= null;

	/** HashMap con los nuevos ID de las referencias	*/
	private HashMap refKeys = null;
	
	/** HashMap con las columnas que necesitan referencia	*/
	private ArrayList columnsWithReferences = null;
	
	/** HashMap con los nuevos ID de las Dynamic Validation	*/
	private HashMap dynVal = null;
	
	/** New AD_Window_ID			*/
	private int AD_Window_ID=0;
	
	/**
	 * Carga una tabla y los elementos que la componen..
	 * @param ctx
	 * @param AD_Table_ID
	 */
	public boolean exportTable(Properties ctx, int AD_Table_ID, String trx)	{
		table = new TMTable (ctx, AD_Table_ID, trx);
		if (table == null)	{
			log.fine("No se ha podido cargar la tabla " + AD_Table_ID);
			return false;
		}
		
		// Exportamos traduccion
		table.exportTrl();
		
		log.info("Cargada la tabla "+table.getTableName()+ "con AD_Table_ID " + table.getAD_Table_ID());
			
		// Cargamos los campos
		columns = TMColumn.getColumns(ctx, AD_Table_ID, trx);
		if (columns == null)	{
			log.fine("No se ha encontrado ninguna columna en la tabla.");
			return false;
		}
		return true;
	}
	
	/**
	 * Devuelve un listado con las ReferenceKeys requeridos por el TableMap
	 * @return lista con los AD_Reference_ID
	 */
	public int[] getReferenceKeys()	{
		return TMTable.getReferenceKeys(table.getAD_Table_ID());
	}
	
	/**
	 * Asigna la lista con los nuevos ID de las referencias
	 * @param map
	 */
	public void setReferenceKeysMap(HashMap map)	{
		refKeys = map;
	}
	
	/**
	 * Devuelve un listado con las Dynamic Validations requeridas por el TableMap
	 * @return lista con los AD_Val_Rule_ID
	 */
	public int[] getDynamicValidations()	{
		return TMTable.getDynamicValidations(table.getAD_Table_ID());
	}
	
	/**
	 * Asigna la lista con los nuevos ID de las Dynamic Validation
	 * @param map
	 */
	public void setDynamicValidationsMap(HashMap map)	{
		dynVal = map;
	}
	
	/**
	 * Devuelve el id con el que se ha exportado el registro.
	 * @return
	 */
	public int getOrig_ID()	{
		return table.getOrig_ID();
	}

	/**
	 * Devuelve el id con el que se ha importado el registro.
	 * @return
	 */
	public int getImp_ID()	{
		return table.getImp_ID();
	}
	
	/**
	 * Asigna el AD_Window_ID
	 * @param id
	 */
	public void setAD_Window_ID(int id)	{
		AD_Window_ID = id;
	}
	
	/**
	 * Importa la tabla y los campos
	 * @param ctx
	 * @param trx
	 * @return
	 */
	public boolean doImport(Properties ctx, String trx)	{
		log.info("Importando la tabla " + table.getName());
		
		// Cambiamos ID
		table.setAD_Window_ID(AD_Window_ID);
		
		// Importamos AD_Table
		if (table.doImport(ctx, trx, null, null) == null)	{
			return false;
		}

		// Obtenemos el nuevo AD_Table_ID
		int AD_Table_ID= table.getImp_ID();
		
		// Y todas las columnas
		if (columns == null)	{
			log.fine("No se han encontrado columnas para esta tabla");
			return false;
		}
		
//		 Recorremos todo el hastmap para importar las columnas
		Iterator keys = columns.keySet().iterator();
		while (keys.hasNext())	{
			TMColumn col = (TMColumn)columns.get(keys.next());

			// Actualizamos los datos necesarios
			// col.setAD_Table_ID( AD_Table_ID);				
			
			// Comprobamos si tiene Dynamic Validation
			int dynid = col.getAD_Val_Rule_ID();
			if (dynid != 0)	{
				//	Si esta en el array asignamos el nuevo valor
				if (dynVal.containsKey(String.valueOf(dynid)))	{
					int newid = Integer.parseInt((String)dynVal.get(String.valueOf(dynid)));
					// col.setAD_Val_Rule_ID(newid);
				}
			}
			
			// Importamos la columna
			if (col.doImport(ctx, trx, null, null) == null)	{
				return false;
			}
			
			// Podemos encontrarnos con que la columna tiene configurada
			// una referencia que apunta a la misma tabla.
			// Por ello, guardaremos en un array estas columnas 
			// y las actualizaremos tras importarlas.
			// Comprobamos si tiene ReferenceValue
			int refid = col.getAD_Reference_Value_ID();
			if ( refid != 0)	{
				if (columnsWithReferences == null)	{
					columnsWithReferences = new ArrayList();
				}
				columnsWithReferences.add(col);
			}

		}
		
		return true;
	}
	
	
	public boolean updateReferences(Properties ctx, String trx)	{
		
		if (columnsWithReferences == null)	{
			log.info("No se han encontrado referencias para actualizar.");
			return true;
		}
		for (int i=0; i < columnsWithReferences.size(); i++)	{
			TMColumn col = (TMColumn)columnsWithReferences.get(i);

			int refid = col.getAD_Reference_Value_ID();
			
			// Si esta en el array asignamos el nuevo valor
			if (!refKeys.containsKey(String.valueOf(refid)))	{
				log.severe("No se ha encontrado la nueva referencia de la columna.");
				return false;
			}
			
			int newrefid = Integer.parseInt((String)refKeys.get(String.valueOf(refid)));
			int col_id = col.getImp_ID();
			TMColumn newcol = new TMColumn (ctx, col_id, trx);			

			// Actualizamos la referencia
			// newcol.setAD_Reference_Value_ID(newrefid);
			if (newcol.save(trx) == false)	{
				log.severe("No se ha podido guardar la referencia de la columna.");
				return false;
			}
		}	
		return true;
	}
	
	/**
	 * Devuelve el ID real de una columna importada
	 * @param oldID
	 * @return
	 */
	public int getNewCol(int oldID )	{
		if (columns == null)	{
			return 0;
		}
		TMColumn c = (TMColumn)columns.get(String.valueOf(oldID));
		if (c == null)	{
			log.fine("No se ha encontrado el nuevo ID de una columna.");
			return 0;
		}
		return c.getImp_ID();
	}
	
}
