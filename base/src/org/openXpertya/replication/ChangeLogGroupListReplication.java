package org.openXpertya.replication;

/**
 * Redefinición de ChangeLogGroupList para el 
 * rellenado del XML para replicación en función
 * de la información contenida en AD_Changelog_Replication
 * 
 */

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MChangelogReplication;
import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.plugin.install.ChangeLogGroup;
import org.openXpertya.plugin.install.ChangeLogGroupList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ChangeLogGroupListReplication extends ChangeLogGroupList {

	DocumentBuilder builder = null;
	Document doc = null;
	
	protected List<ChangeLogGroupReplication> groups;
	
	
	/**
	 * Instancia los elementos a utilizar para la generación
	 * del XML definitivo a pasarle al host destino 
	 */
	public void fillList(String trxName) throws Exception
	{
		ReplicationTableManager rtm = new ReplicationTableManager(trxName);
		rtm.evaluateChanges();
		
		ChangeLogGroupReplication group = null; 
		Integer ad_table_id = null;
		String operationType = null;
		String ad_componentObjectUID = null;
		String columnValues = null;
		String tableName = null;
		String tempRepArray = null;
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		try{
			
			int i = 0;
			// Iterar por todos los registros a replicar
			while (rtm.getNextChange()) {

				/* Incorporar un nuevo changegroup por cada host destino */
				// Cargo los valores especificos para replicación 
				i++;
				tempRepArray = rtm.getCurrentRecordRepArray();
				columnValues = rtm.getColumnValuesForReplication();
				ad_table_id = rtm.getCurrentRecordTableID();
				operationType = rtm.isDeletionAction()?MChangeLog.OPERATIONTYPE_Deletion:"";  // se determina posteriormente por cada host de manera independiente (salvo para eliminacion)
				ad_componentObjectUID = rtm.getCurrentRecordRetrieveUID(); 	// <-- usado para el retrieveUID
				tableName = rtm.getCurrentRecordTableName();
				
				/* Crear un grupo por tupla de AD_Changelog_Replication */ 
				group = new ChangeLogGroupReplication(ad_table_id, ad_componentObjectUID, operationType, tableName, tempRepArray);
				group.setAd_componentObjectUID(ad_componentObjectUID);
				group.setOperation(operationType);
				// El timeout se da solo en los casos en que no haya estados de replicación 
				// (en ese caso se sabe que se disparó la replicación de este grupo por dicho motivo)
				group.setTimeOut(!repArrayContainsRepStates(group));
				
				/* Insertar los elementos dentro del grupo e incorporarlos al conjunto de grupos */
				if (insertElementsIntoGroup(group, columnValues, builder) > 0)
				{
					// Aplicar filtrados adicionales de replicaciòn por registro
					ReplicationFilterFactory.applyFilters(trxName, group);
					// Incorporar a la nomina de grupos
					groups.add(group);
				}
				// Limpiar memoria cada cierto intervalo de iteraciones
				if (i++ % 1000 == 0)
					System.gc();
			}
		} catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally
		{
			// Ayudar al garbage collector a liberar memoria
			rtm.finalize();
			rtm = null;
		}
	}
	
	/**
	 * Retorna true si en alguna de las posiciones del repArray se presenta un estado de replicación 
	 * (ya sea por Insercion, Modificación, o bien por reintentos luego de presentarse un error).
	 */
	protected boolean repArrayContainsRepStates(ChangeLogGroupReplication group)
	{
		boolean found = false;
		String repArray = group.getRepArray();
		if (repArray==null)
			return false;
		int size = repArray.length();
		for (int i=0; i < size && !found; i++)
			if (ReplicationConstants.replicateStates.contains(repArray.charAt(i)))
				found = true;
		return found;
	}
	
	
	/**
	 * Convierte la estructura básica almacenada en el XML de AD_Changelog_Replication
	 * en un conjunto de elemento de tipo ChangeLogElement a fin de agruparlos en un
	 * unico ChagenLogGroup.  De esta manera, se logra reutilizar la logica de exportación
	 * de plugins para el caso de replicación de datos.  
	 * @param group grupo a cargar los elementos
	 * @param columnValuesXML String con el XML basico 
	 * @throws Exception en caso de error
	 */
	protected int insertElementsIntoGroup(ChangeLogGroup group, String columnValuesXML, DocumentBuilder builder) throws Exception
	{
		/* Variables para la insercion */
		Element aColumn = null;
		String columnID = null;
		boolean valueIsNull = false;
		String value = null;

		// Parsear el documento (si hay codificaciones no parseables, omitir e informar)
		try {
			doc = builder.parse(new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <columns> "+columnValuesXML+" </columns> ")));
		}
		catch (Exception e) {
			System.out.println("Error insertando en XML de replicación.  Tabla: " + group.getTableName() + ". Registro: " + group.getAd_componentObjectUID());
			e.printStackTrace();
			return 0;
		}
		
		/* Leer cada columna e insertar los elementos */
		NodeList nodes = doc.getElementsByTagName(MChangelogReplication.XML_COLUMN_TAG);
		for (int i = 0; i < nodes.getLength(); i++)
		{
			/* Recuperar cada atributo de cada columna (ID de columna, Valor, flag de nulo */
			aColumn = (Element)nodes.item(i);
			columnID = aColumn.getAttribute(MChangelogReplication.XML_COL_ID_ATT);
			valueIsNull = aColumn.hasAttribute(MChangelogReplication.XML_NULL_ATT);
			value = valueIsNull?null:aColumn.getAttribute(MChangelogReplication.XML_VALUE_ATT);
			
			/* Incorporar al grupo */
			ChangeLogElement element = new ChangeLogElement(Integer.parseInt(columnID), null, value, null, 0);	
			group.addElement(element);
			
			/* Ayudar al garbage collector */
			columnID = null;
			value = null;
			aColumn = null;
		}
		
		/* Ayudar al garbage collector */
		builder = null;
		doc = null;
		
		/* Retornar la cantidad de nodos contenidos */
		return nodes.getLength();
	}

	public ChangeLogGroupListReplication(){
		groups = new  ArrayList<ChangeLogGroupReplication>(); 
	}
	
	public List<ChangeLogGroupReplication> getGroupsReplication() {
		return groups;
	}

	
}
