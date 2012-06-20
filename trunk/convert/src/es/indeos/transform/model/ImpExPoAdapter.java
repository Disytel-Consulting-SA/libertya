/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Modificado por TecnoXP
 * 
 */

package es.indeos.transform.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.PO;
import org.openXpertya.model.POInfo;

public interface ImpExPoAdapter {

	/** Este m�todo deber� invocar a ImpExPoCommon.exportTrl
	 * 
	 * @see ImpExPoCommon.exportTrl
	 * @return
	 */
	public boolean exportTrl();
	
	
	/** Este m�todo deber� invocar a ImpExPoCommon.importTrl
	 * 
	 * @see ImpExPoCommon.importTrl
	 * @param trx
	 * @return
	 */
	public boolean importTrl(String trx);
	
	/** 
	 * Se invoca en el objeto original durante la importacion, devolviendo una instancia de PO adecuada para el objeto actual
	 * y referenciando al actual objeto en la base de datos destino.
	 * 
	 * Aqui es donde se encuentra la logica de busqueda de un registro ya existente. Cada clase debe implementar adecuadamente
	 * dicha logica a fin de determinar si al importar, hay que modificar un registro existente o crear uno nuevo.
	 * 
	 * Puede devolver NULL, en cuyo caso ImpExPoCommon crear� una nueva instancia del objeto. Para mas detalles mirar el metodo
	 * doImport de dicha clase (ImpExPoCommon).
	 * 
	 * @param ctx
	 * @param trx
	 * @param parentOld
	 * @param parentNew
	 * @return
	 */
	public ImpExPoAdapter searchCurrentObject(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew);
	
	/**
	 * Se invoca en el nuevo objeto durante la importaci�n, copiando los valores desde el parametro other al objeto mismo.
	 * 
	 * @param other Origen de los valores
	 * @param AD_Client_ID
	 * @param AD_Org_ID
	 */
	public void copyValuesFrom(PO other, int AD_Client_ID, int AD_Org_ID);
	
	/** Realiza la importacion del objeto actual. El mismo es el objeto previamente deserialzado del archivo XML.
	 * 
	 * @param ctx
	 * @param trx
	 * @param parentOld
	 * @param parentNew
	 * @return
	 */
	public ImpExPoAdapter doImport(Properties ctx, String trx, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew);
	
	/**
	 * Se invoca en el nuevo objeto, y se utiliza para inicializar los valores del objeto adecuadamente: referencias, claves, etc.
	 * Este m�todo se invoca dos veces: la primer vez durante la importaci�n inicial mediante el m�todo doImport, y la segunda vez al 
	 * finalizar de importar todos los objetos. 
	 * 
	 * Durante ambas invocaciones se garantiza que todos los parametros excepto el �tlimo ser�n id�nticos. Este �ltimo par�metro
	 * se utiliza para distinguir en cual de ambas invocaciones se encuentra actualmente. Puede tomar los valores 0 � 1 respectivamente
	 * a la primer o segunda invocaci�n.
	 * 
	 * La utilidad del �ltimo par�metro es para asignar valores con referencias circulares dentro de una misma tabla, o si hay que
	 * indicar la referencia a un objeto que todav�a no se realiz� la importaci�n.
	 *  
	 * @param oldpo
	 * @param parentOld
	 * @param parentNew
	 * @param passNo
	 */
	public void setCustomDataFrom(PO oldpo, ImpExPoAdapter parentOld, ImpExPoAdapter parentNew, int passNo);
	
	/**
	 * Este m�todo deber� devolver la variable de instancia p_info del objeto.
	 * 
	 * @return
	 */
	public POInfo pa_GetPOInfo();
	
	/**
	 * Este m�todo deber� invocar al m�todo get_Translation de PO.
	 * 
	 * @param columnName
	 * @param AD_Language
	 * @return
	 */
	public String pa_get_Translation(String columnName, String AD_Language);
	
	/**
	 * Devuelve el ID (clave primaria) del nuevo objeto. Se inicializa durante la importaci�n (doImport).
	 *  
	 * @return
	 */
	public int getImp_ID();
	
	/**
	 * Devuelve el ID (clave primaria) del viejo objeto que ten�a al momento de exportar. Se inicializa durante la importaci�n 
	 * (doImport).
	 *  
	 * @return
	 */
	public int getOrig_ID();
	
	/**
	 * Establece el ID (clave primaria) del nuevo objeto. Se invoca durante la importaci�n (doImport).
	 * 
	 * @param x
	 */
	public void setImp_ID(int x);
	
	/**
	 * Establece el ID (clave primaria) del viejo objeto que ten�a al momento de exportar. Se invoca durante la importaci�n.
	 * (doImport).
	 * 
	 * @param x
	 */
	public void setOrig_ID(int x);
	
	/**
	 * Devuleve el mapa de traducciones TrlMap.
	 * 
	 * @return
	 */
	public Map getTrlMap();
	
	/**
	 * Establece un nuevo mapa de traducciones.
	 * 
	 * @param trlMap
	 */
	public void setTrlMap(Map trlMap);
	
}
