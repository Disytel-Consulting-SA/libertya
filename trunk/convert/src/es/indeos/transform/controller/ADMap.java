/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Diseño y desarrollo por Indeos Consultoria S.L.
 * 
 * Modificado por TecnoXP
 */

package es.indeos.transform.controller;

import es.indeos.transform.Convert;
import es.indeos.transform.model.*;

import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

//import java.awt.image.DataBufferUShort;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;



/**
 * Clase empleada para cargar y almacenar la informacion del AD
 *
 */
public class ADMap {
	
	
	
	/**	CLogger											*/
	protected CLogger			log = CLogger.getCLogger (getClass());
	

	/*************************************************************************
	 * Estructura de los datos
	 * Todos los HashMap usan el ID original como clave
	 */
	
	/** La ventana, solo habra una.	*/
	private TMWindow window = null;
	
	/** HashMap con las pestañas. */
	private HashMap tabs = null;
	
	/** HashMap con los campos de la pestaña	*/
	private HashMap fields = null;
	
	/** HashMap con los mapas de las tablas. */
	private HashMap tablesMap = null;
		
	/** HashMap con los ReferenceMaps	*/
	private HashMap refKeysMap = null;
	
	/** HashMap con las Dynamic Validation */
	private HashMap dynValMap = null;
	
	/** Proceso */
	private TMProcess process = null;
	
	/** HashMap con los parametros del proceso */
	private HashMap<Integer,TMProcessPara> processparaMap = null;
	
	/** */
	private TMPrintFormat printFormat = null;
	
	/** */
	private HashMap<Integer,TMPrintFormatItem> printFormatItemsMap = null;
	
	//
	private HashMap<Integer,TMReportView> reportViewMap = null;
	
	private HashMap<Integer,TMReportViewCol> reportViewItemsMap = null;
	
	private TMPrintTableFormat printTableFormat = null;
	
	/***************************************************************************/
	
	/**
	 * Carga una ventana y todos los elementos que la componen.
	 * @param ctx	Context info
	 * @param name Name of the Window to export.
	 * @return
	 */
	public boolean exportWindow(Properties ctx, String name, String trx)	{
		// Load MWindow
		window = TMWindow.getTMWindow(ctx, name, trx);
		if (window == null)	{
			log.info("Ventana no encontrada: " + name);
			return false;
		}
		
		//	Exportamos traduccion
		window.exportTrl();
		
		// Load Tabs
		exportTabs(ctx, window.getAD_Window_ID(), trx);
		return true;
		
	}
	
	/**
	 * Carga una ventana y todos los elementos que la componen.
	 * @param ctx	Context info
	 * @param AD_Window_ID Window ID
	 * @return
	 */
	public boolean exportWindow(Properties ctx, int AD_Window_ID, String trx)	{
		// Load MWindow
		window = new TMWindow(ctx, AD_Window_ID, trx);
		
		// Exportamos traduccion
		window.exportTrl();
		
		// Load Tabs
		exportTabs(ctx, window.getAD_Window_ID(), trx);
		return true;
		
	}

	public boolean exportProcess(Properties ctx, int AD_Process_ID, String trx)	{
		
		if (AD_Process_ID == 0)
			return false;
		
		process = new TMProcess(ctx, AD_Process_ID, trx);
		process.exportTrl();
		
		System.out.println("Exportado Proceso: " + process.getName() + " ID: " + process.getID());
		
		exportProcessPara(ctx, AD_Process_ID, trx);
		exportPrintFormat(ctx, process.getAD_PrintFormat_ID(), trx);
		exportReportView(ctx, process.getAD_ReportView_ID(), trx);
		
		return true;
	}
	
	public void exportReportView(Properties ctx, int AD_ReportView_ID, String trx) {
		
		if (AD_ReportView_ID == 0)
			return ;
		
		if (reportViewMap == null)
			reportViewMap = new HashMap<Integer, TMReportView>();
		
		if (reportViewMap.containsKey(AD_ReportView_ID))
			return;
		
		TMReportView reportView = new TMReportView(ctx, AD_ReportView_ID, trx);
		reportView.exportTrl();
		
		exportReportViewCol(ctx, AD_ReportView_ID, trx);
		exportTable(ctx, reportView.getAD_Table_ID(), trx);
		
		reportViewMap.put(reportView.getID(), reportView);
		
	}
	
	public void exportReportViewCol(Properties ctx, int AD_ReportView_ID, String trx) {
		reportViewItemsMap = new HashMap<Integer, TMReportViewCol>();
		List cols = TMReportViewCol.getReportView(ctx, AD_ReportView_ID, trx);
		
		for (Object o : cols)  {
			TMReportViewCol rvc = (TMReportViewCol)o;
			reportViewItemsMap.put(rvc.getID(), rvc);
		}
	}
	
	public void exportProcessPara(Properties ctx, int AD_Process_ID, String trx) {
		processparaMap = new HashMap<Integer, TMProcessPara>();
		List tmpp = TMProcessPara.getProcessPara(ctx, AD_Process_ID, trx);
		
		for (Object o : tmpp) {
			TMProcessPara p = (TMProcessPara)o;
			p.exportTrl();
			processparaMap.put(p.getID(), p);
		}
	}
	
	public void exportPrintFormat(Properties ctx, int AD_PrintFormat_ID, String trx) {
		
		if (AD_PrintFormat_ID == 0)
			return;
		
		printFormat = new TMPrintFormat(ctx, AD_PrintFormat_ID, trx);
		
		exportPrintFormatItem(ctx, AD_PrintFormat_ID, trx);
		exportTable(ctx, printFormat.getAD_Table_ID(), trx);
		exportPrintTableFormat(ctx, printFormat.getAD_PrintTableFormat_ID(), trx);
		exportReportView(ctx, printFormat.getAD_ReportView_ID(), trx);
		
	}
	
	public void exportPrintTableFormat(Properties ctx, int AD_PrintTableFormat_ID, String trx) {
		
		if (AD_PrintTableFormat_ID == 0)
			return;
		
		printTableFormat = new TMPrintTableFormat(ctx, AD_PrintTableFormat_ID, trx);
	}
	
	public void exportPrintFormatItem(Properties ctx, int AD_PrintFormat_ID, String trx) {
		
		printFormatItemsMap = new HashMap<Integer, TMPrintFormatItem>();
		
		ArrayList al = TMPrintFormatItem.getPrintFormatItem(ctx, AD_PrintFormat_ID, trx);
		
		for (Object o : al) {
			TMPrintFormatItem fi = (TMPrintFormatItem)o;
			fi.exportTrl();
			printFormatItemsMap.put(fi.getID(), fi);
		}
	}
	
	/**
	 * Exporta las pestañas de la ventana dada y todos los elementos que lo componen.
	 * @param ctx
	 * @param AD_Window_ID
	 */
	public void exportTabs (Properties ctx, int AD_Window_ID, String trx)	{
		// Cargamos los tabs.
		tabs = TMTab.getTabs(ctx, AD_Window_ID, trx);
		
		if (tabs == null)	{
			log.fine("No se han encontrado pestañas");
		}
		// Los recorremos para exportar las tablas relacionadas.
		// y los campos
		Iterator keys = tabs.keySet().iterator();
		while (keys.hasNext())	{
			TMTab t = (TMTab)tabs.get(keys.next());
			exportTable(ctx, t.getAD_Table_ID(), trx);
			exportFields(ctx, t.getAD_Tab_ID(),trx);
		}
		
	}	
	
	/**
	 * Exporta los campos de esta pestaña
	 * @param ctx
	 * @param AD_Tab_ID
	 */	
	public void exportFields(Properties ctx, int AD_Tab_ID, String trx)	{
		//	Cargamos los campos
		HashMap f = TMField.getFields(ctx, AD_Tab_ID, trx);
		
		if (f == null)	{
			log.fine("No se han encontrado campos para esta pestaña: " + AD_Tab_ID);
			return;
		}
		
		if (fields == null)	{
			fields = new HashMap();
		}
		
		// Recorremos el HashMap con los campos de esta pestaña para añadirlos
		// al HashMap de campos
		Iterator keys = f.keySet().iterator();
		while (keys.hasNext())	{
			TMField field = (TMField)f.get(keys.next());
			// Añadimos los campos al HashMap
			fields.put(String.valueOf(field.getAD_Field_ID()), field);
		}		
	}
	
	/**
	 * Carga un TableMap de la tabla dada y los elementos que la componen..
	 * @param ctx
	 * @param AD_Table_ID
	 */
	public boolean exportTable(Properties ctx, int AD_Table_ID, String trx)	{
		// Exportamos la tabla
		TableMap tmap = new TableMap();
		
		if (!tmap.exportTable(ctx, AD_Table_ID, trx))
			return false;
		
		// La añadimos al tablesMap
		if (tablesMap == null)	{
			tablesMap = new HashMap();
		}
		tablesMap.put(String.valueOf(AD_Table_ID), tmap);
			
		// Cargamos elementos requeridos para la tabla, 
		// pero que no tienen porque ser exclusivos de ella.

		// Primero las Reference Keys
		int[] refkeys = tmap.getReferenceKeys();

		// Creamos el HashMap si no no existe y se han encontrado refkeys
		if (refKeysMap == null && refkeys.length > 0)	{
			refKeysMap = new HashMap();
		}

		// Guardamos las referenceKeys
		for (int i=0; i< refkeys.length; i++)	{
			ReferenceMap ref = new ReferenceMap();
			if (ref.exportReference(ctx, refkeys[i], trx) != false)	{
				refKeysMap.put(String.valueOf(refkeys[i]), ref);
				log.info("Exportando Referencia ["+ i +"]" + refkeys[i]);	
			}
			
		}
		
		// Y las Dynamic Validations
		int[] dynval = tmap.getDynamicValidations();

		// Creamos el HashMap si no no existe y se han encontrado dynval
		if (dynValMap == null && dynval.length > 0)	{
			dynValMap = new HashMap();
		}

		// Guardamos las validaciones.
		for (int i=0; i< dynval.length; i++)	{
			TMValRule val = new TMValRule(ctx, dynval[i], trx);
			val.exportTrl();
			dynValMap.put(String.valueOf(val.getAD_Val_Rule_ID()), val);
			log.info("Exportando DynamicValidation ["+ i +"]" + dynval[i]);
		}
		
		return true;
	}
	
	
/***************************************************************************************************************
 * Importacion
 */	
	
	
	public boolean doImport(Properties ctx, String trx)	{
		log.info("Iniciando Importacion.");
				
		
		// Importamos las Dynamic Validation
		if (dynValMap != null)	{
			log.info("Importando Validaciones Dinamicas.");
			Iterator keys = dynValMap.keySet().iterator();
			while (keys.hasNext())	{
				TMValRule val = (TMValRule)dynValMap.get(keys.next());
				// Importamos el TMValRule
				if (val.doImport(ctx, trx, null, null) == null)	{
					return false;
				}
			}
			
		}
		
		// Creamos la ventana, ya que la tabla puede enlazar a una ventana
		if (window != null)	{
			TMWindow w = (TMWindow)window.doImport(ctx, trx, null, null);
			if (w == null)	{
				return false;
			}
		}
		
		// Creamos las tablas y sus campos
		if (tablesMap != null)	{
			log.info("Importando Tablas");
			
			// Recorremos todo el hashmap
			Iterator keys = tablesMap.keySet().iterator();
			while (keys.hasNext())	{
				TableMap tmap = (TableMap)tablesMap.get(keys.next());
				// Debemos añadir al tableMap un mapa con las DynamicValidation
				// Para que pueda hacer sustituciones al importar las columnas
				// y en nuevo AD_Window_ID
				tmap.setReferenceKeysMap(getRefKeyAsignMap());
				tmap.setDynamicValidationsMap(getDynValAsignMap());
				
				if (window != null)
					tmap.setAD_Window_ID(window.getImp_ID());
				
				// Importamos el tableMap
				if (tmap.doImport(ctx, trx) == false)	{
					return false;
				}
				
				// Actualizamos el objeto en el tablesMap
				tablesMap.put(String.valueOf(tmap.getOrig_ID()), tmap);	
			}
		}

		// Importamos las Referencias
		if (refKeysMap != null)	{
			log.info("Importando Referencias");
			//	 Recorremos todo el hastmap
			Iterator keys = refKeysMap.keySet().iterator();
			while (keys.hasNext())	{
				ReferenceMap rmap = (ReferenceMap)refKeysMap.get(keys.next());
				// Importamos el tableMap
				if (rmap.doImport(ctx, trx) == false)	{
					return false;
				}
			}

			// Actualizamos las referencias del tablesMap
			if (tablesMap != null)	{
				keys = tablesMap.keySet().iterator();
				while (keys.hasNext())	{
					TableMap tmap = (TableMap)tablesMap.get(keys.next());

					// Debemos añadir al tableMap un mapa con las referencias importadas
					// Para que pueda hacer sustituciones al actualizar las columnas
					tmap.setReferenceKeysMap(getRefKeyAsignMap());		
					if (tmap.updateReferences(ctx, trx) == false)	{
						return false;
					}
				}
			}
		}
		
		
		// Las pestañas de la ventana
		if (tabs != null)	{
			log.info("Importando Pestañas");
			
			// Recorremos todo el hashmap
			Iterator keys = tabs.keySet().iterator();
			while (keys.hasNext())	{
				TMTab tab = (TMTab)tabs.get(keys.next());
				
				// Actualizamos los datos de la ventana
				tab.setAD_Window_ID(window.getImp_ID());
				
				// Buscamos el nuevo TableID
				if (tablesMap != null)	{
					TableMap tmap = (TableMap)tablesMap.get(String.valueOf(tab.getAD_Table_ID()));
					
					// Guardamos la antigua AD_Table_ID, que nos hara falta..
					tab.set_OldAD_Table_ID(tab.getAD_Table_ID());
					tab.setAD_Table_ID(tmap.getImp_ID());
				}
				
				// Importamos la pestaña
				if (tab.doImport(ctx, trx) == false)	{
					return false;
				}
			}
		}
		
		// Importamos los campos
		if (fields != null)	{
			Iterator keys = fields.keySet().iterator();
			while (keys.hasNext())	{
				TMField field = (TMField)fields.get(keys.next());
				
				// Actualizamos los datos de la ventana
				// Buscamos el nuevo AD_Tab_ID
				if (tabs == null)	{
					log.fine("No se han encontrado pestañas a las que añadir estos campos");
					return false;
				}
				
				TMTab tab = (TMTab)tabs.get(String.valueOf(field.getAD_Tab_ID()));
				if (tab == null)	{
					log.fine("No se ha encontrado el nuevo ID de una pestaña.");
					return false;
				}
				// Y le asignamos al campo el nuevo ID
				field.setAD_Tab_ID(tab.getImp_ID());
				
				// Buscamos el nuevo AD_Column_ID
				if (tablesMap != null)	{
					int AD_Table_ID = tab.get_OldAD_Table_ID();
					int AD_Column_ID=field.getAD_Column_ID();
					
					TableMap tmap = (TableMap)tablesMap.get(String.valueOf(AD_Table_ID));
					if (tmap == null)	{
						log.fine("No se ha encontrado tabla importada: " + AD_Table_ID);
						return false;
					}
					int col_id = tmap.getNewCol(AD_Column_ID);
					
					if (col_id == 0)	{
						return false;
					}
					field.setAD_Column_ID(col_id);
				}
				
				// Importamos el campo
				if (field.doImport(ctx, trx, null, null) == null)	{
					return false;
				}
			}
		}
		
		TMProcess newProcess = null;
		
		if (process != null) {
			newProcess = (TMProcess)process.doImport(ctx, trx, null, null);
			if (newProcess == null)
				return false;
			log.log(Level.WARNING, "Proceso Viejo: " + process.getName() + " ID: " + process.getID());
			log.log(Level.WARNING, "Proceso Importado: " + newProcess.getName() + " ID: " + newProcess.getID());
		}
		
		if (processparaMap != null) {
			for (TMProcessPara pp : processparaMap.values()) {
				TMProcessPara newPara = (TMProcessPara)pp.doImport(ctx, trx, process, newProcess);
				if (newPara == null)
					return false;
			}
		}
		
		TMPrintFormat newPrintFormat = null;
		
		if (printFormat != null) {
			newPrintFormat = (TMPrintFormat)printFormat.doImport(ctx, trx, null, null);
			if (newPrintFormat == null)
				return false;
		}
		
		if (printFormatItemsMap != null) {
			for (TMPrintFormatItem pfi : printFormatItemsMap.values()) {
				TMPrintFormatItem newPfi = (TMPrintFormatItem)pfi.doImport(ctx, trx, printFormat, newPrintFormat);
				if (newPfi == null)
					return false;
			}
		}
		
		HashMap<Integer,TMReportView> newReportViewMap = new HashMap<Integer, TMReportView>();
		
		if (reportViewMap != null) {
			for (TMReportView reportView : reportViewMap.values()) {
				TMReportView newReportView = (TMReportView)reportView.doImport(ctx, trx, null, null);
				if (newReportView == null)
					return false;
				
				// Key: OLD ID, Value: NEW PO
				newReportViewMap.put(reportView.getID(), newReportView);
			}
		}
		
		if (reportViewItemsMap != null) {
			for (TMReportViewCol rvc : reportViewItemsMap.values()) {
				TMReportViewCol newRvc = (TMReportViewCol)rvc.doImport(ctx, trx, reportViewMap.get(rvc.getAD_ReportView_ID()), newReportViewMap.get(rvc.getAD_ReportView_ID()));
				if (newRvc == null)
					return false;
			}
		}
		
		List<ImpExPoCommon.ImportedObj> ios = Convert.getImportedObjects();
		
		for (ImpExPoCommon.ImportedObj o : ios) {
			PO newpo = (PO)o.newobj;
			o.newobj.setCustomDataFrom(o.po, o.parentOld, o.parentNew, 1);
			newpo.save();
		}
		
		return true;
	}
	
	/**
	 * Devuelve un HashMap con los nuevos ID asignados a las Referencias
	 * @return HashMap
	 */
	public HashMap getRefKeyAsignMap()	{
		if (refKeysMap == null)	{
			return null;
		}
		HashMap asig = new HashMap();
		Iterator keys = refKeysMap.keySet().iterator();
		while (keys.hasNext())	{
			ReferenceMap ref = (ReferenceMap)refKeysMap.get(keys.next());
			asig.put(String.valueOf(ref.getOrigAD_Reference_ID()), String.valueOf(ref.getAD_Reference_ID()));
			log.info("Añadiendo mapa de " + ref.getOrigAD_Reference_ID() + " a " + ref.getAD_Reference_ID());
		}
		return asig;
	}
	
	/**
	 * Devuelve un HashMap con los nuevos ID asignados a las Referencias
	 * @return HashMap
	 */
	public HashMap getDynValAsignMap()	{
		if (dynValMap == null)	{
			return null;
		}
		HashMap asig = new HashMap();
		Iterator keys = dynValMap.keySet().iterator();
		while (keys.hasNext())	{
			TMValRule val = (TMValRule)dynValMap.get(keys.next());
			asig.put(String.valueOf(val.getOrig_ID()), String.valueOf(val.getImp_ID()));
			log.info("Añadiendo mapa de " + val.getOrig_ID() + " a " + val.getImp_ID());
		}
		return asig;
	}	
	
}
