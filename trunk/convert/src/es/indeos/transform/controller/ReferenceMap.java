/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Diseño y desarrollo por Indeos Consultoria S.L.
 */

package es.indeos.transform.controller;

import org.openXpertya.util.CLogger;
import es.indeos.transform.model.*;

import java.util.*;

public class ReferenceMap {

	/**	CLogger											*/
	protected CLogger			log = CLogger.getCLogger (getClass());
	
	/** Reference 				*/
	TMReference reference = null;
	
	/** HashMap con las RefList		*/
	HashMap refList = null;
	
	/** RefTable, solo puede ser uno	*/
	TMRefTable refTable = null;
	
	/**
	 * Devuelve el AD_Reference_ID importado
	 * @return AD_Reference_ID importado
	 */
	public int getAD_Reference_ID()	{
		return reference.getImp_ID();
	}
	
	/**
	 * Devuelve el AD_Reference_ID original
	 * @return AD_Reference_ID importado
	 */
	public int getOrigAD_Reference_ID()	{
		return reference.getOrig_ID();
	}
	
	/**
	 * Guarda la Referencia en el ReferenceMap
	 * @param ctx
	 * @param AD_Reference_ID
	 * @return
	 */
	public boolean exportReference(Properties ctx, int AD_Reference_ID, String trx)	{
		reference = new TMReference(ctx, AD_Reference_ID, trx);
		
		if (reference == null)	{
			log.fine("No se ha podido guardar la referencia " + AD_Reference_ID);
			return false;
		}
		
		// Exportamos traduccion
		reference.exportTrl();
		
		log.info("Exportada Referencia: " + AD_Reference_ID);
		
		
		boolean ok = false;
		if (reference.getValidationType().equals(reference.VALIDATIONTYPE_TableValidation))	{
			ok = exportReferenceTable(ctx, AD_Reference_ID, trx);	
		}
		else if (reference.getValidationType().equals(reference.VALIDATIONTYPE_ListValidation))	{
			ok = exportReferenceList(ctx, AD_Reference_ID, trx);
		}
		else { // Otros tipos de validacion no requieren mas que la pripera pestaña
			ok = true;
		}
		
		if (ok == false)	{
			log.fine("No se han encontrado reglas de validacion");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Guarda en el HashMap las referencias de lista
	 * @param ctx
	 * @param AD_Reference_ID
	 * @return
	 */
	public boolean exportReferenceList(Properties ctx, int AD_Reference_ID, String trx)	{
		refList = TMReference.getListValidation(ctx, AD_Reference_ID, trx);
		
		if (refList == null)	{
			return false;
		}
		return true;
	}
	
	/**
	 * Guarda en el HashMap las referencias de tabla
	 * @param ctx
	 * @param AD_Reference_ID
	 * @return
	 */
	public boolean exportReferenceTable(Properties ctx, int AD_Reference_ID, String trx)	{
		refTable = TMReference.getTableValidation(ctx, AD_Reference_ID, trx);
		
		// Exportamos traduccion
		refTable.exportTrl();
		
		if (refTable == null)	{
			return false;
		}
		return true;
	}
	
	
	public boolean doImport (Properties ctx, String trx)	{
		log.info("Importando la referencia " + reference.getName());
		
		// Importamos la referencia
		if (reference.doImport(ctx, trx, null, null) == null)	{
			return false;
		}

		// Obtenemos el nuevo AD_Table_ID
		int AD_Reference_ID= reference.getImp_ID();
		
		// Si la validacion es de tipo tabla
		if (reference.getValidationType().equals(reference.VALIDATIONTYPE_TableValidation))	{
			if (refTable == null)	{
				log.fine("No se ha encontrado TableValidation para comprobar.");
				return false;
			}
			// Modificamos valores
			refTable.setAD_Reference_ID(AD_Reference_ID);
			
			if (refTable.doImport(ctx, trx) == false)	{
				return false;
			}
		}
		// Si es de tipo lista
		else if (reference.getValidationType().equals(reference.VALIDATIONTYPE_ListValidation))	{
			if (ImportReferenceList(ctx, trx, AD_Reference_ID) == false)	{
				return false;
			}
		}
		
		return true;

	}
	
	/**
	 * Importa todas las ReferenceList
	 * @param ctx
	 * @param trx
	 * @param AD_Reference_ID
	 * @return true si se importan sin errores.
	 */
	private boolean ImportReferenceList(Properties ctx, String trx, int AD_Reference_ID)	{
		if (refList == null)	{
			log.fine("No se han encontrado Referencias de lista a importar.");
			return false;
		}
		
		// Recorremos todo el hastmap para importar las validaciones
		Iterator keys = refList.keySet().iterator();
		while (keys.hasNext())	{
			TMRefList ref = (TMRefList)refList.get(keys.next());

			// Actualizamos los datos necesarios
			ref.setAD_Reference_ID(AD_Reference_ID);
			
			if (ref.doImport(ctx, trx) == false)	{
				return false;
			}
		}
		return true;
	}
	
}
