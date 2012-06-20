package org.openXpertya.replication;

/**
 * Redefinición de ChangeLogGroupList para el 
 * rellenado del XML para replicación en función
 * de la información contenida en AD_Changelog_Replication
 * 
 */

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openXpertya.model.MChangelogReplication;
import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.plugin.install.ChangeLogGroup;
import org.openXpertya.plugin.install.ChangeLogGroupList;
import org.openXpertya.util.DB;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ChangeLogGroupListReplication extends ChangeLogGroupList {

	
	/** primer registro del changelog a bitacorear */
	int m_changelog_initial_id = -1; 
	int m_changelog_final_id = -1; 
	
	DocumentBuilder builder = null;
	Document doc = null;
	
	/**
	 * Instancia los elementos a utilizar para la generación
	 * del XML definitivo a pasarle al host destino 
	 * @param replicationArrayPos: criterio de filtrado 
	 * 		  (sucursal para la cual se generará el XML) 
	 * 		
	 * 		La primer posicion es la org numero 1, la segunda es la org numero 2, etc.
	 * 
	 *  Si initial_changelog_record_id > 0, entonces iniciar la busqueda desde dicha posicion
	 *  
	 *  IMPORTANTE: debido a que el contenido XML generalmente es muy voluminoso,
	 *  solo recuperará ReplicationBuilder.MAX_LOG_RECORDS tuplas
	 */
	public void fillList(int replicationArrayPos, int initial_changelog_record_id, int final_changelog_record_id, String trxName) throws Exception
	{
		/** Filtrar el changelog solo para la sucursal especificada en replicationArrayPos */
		StringBuffer sql = new StringBuffer(" SELECT log.ad_table_id, log.recorduid, log.operationtype, log.columnvalues, log.ad_changelog_replication_id, t.tableName ");
								 sql.append(" FROM ad_changelog_replication log ");
								 sql.append(" INNER JOIN ad_table t ON (log.ad_table_id = t.ad_table_id) ");
								 sql.append(" WHERE substring(replicationarray, ?,1) = '1' ");
								 if (initial_changelog_record_id > 0)
								 	sql.append(" AND log.ad_changelog_replication_id >= " + initial_changelog_record_id);
								 if (final_changelog_record_id > 0)
									 	sql.append(" AND log.ad_changelog_replication_id <= " + final_changelog_record_id);
								 sql.append(" ORDER BY log.ad_changelog_replication_id ASC ");
								 sql.append(" LIMIT " + ReplicationBuilder.MAX_LOG_RECORDS );

		PreparedStatement ps = null;
		ResultSet rs = null;
		ChangeLogGroup group = null; 
		Integer ad_table_id = null;
		String operationType = null;
		String ad_componentObjectUID = null;
		String columnValues = null;
		String tableName = null;
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		try{
			ps = DB.prepareStatement(sql.toString(), trxName);
			ps.setInt(1, replicationArrayPos);
			rs = ps.executeQuery();
			boolean started = false;
			int i = 0;
			while(rs.next()){
				/* Guardar el registro de inicio de bitacora */ 
				if (!started)	{
					m_changelog_initial_id = rs.getInt("ad_changelog_replication_id");
					started = true;
				}
				/* Cargo los valores especificos para replicación */
				columnValues = rs.getString("columnvalues");
				ad_table_id = rs.getInt("ad_table_id");
				operationType = rs.getString("operationtype");
				ad_componentObjectUID = rs.getString("recorduid");  // <-- usado para el recordUID
				tableName = rs.getString("tableName");
				
				/* Guardar el registro de fin de bitácora (ultima iteración mantiene ultimo ID) */
				m_changelog_final_id = rs.getInt("ad_changelog_replication_id");
				
				/* Crear un grupo por tupla de AD_Changelog_Replication */ 
				group = new ChangeLogGroup(ad_table_id, ad_componentObjectUID, operationType, tableName);
				group.setAd_componentObjectUID(ad_componentObjectUID);
				group.setOperation(operationType);
				
				/* Insertar los elementos dentro del grupo e incorporarlos al conjunto de grupos */
				/* Si es una insercion o modificacion, verificar que realmente existan modificaciones, si es borrado insertarla siempre */
				if ( operationType.equals("D") || ( (operationType.equals("I") || operationType.equals("M")) && insertElementsIntoGroup(group, columnValues, builder) > 0) )
					getGroups().add(group);
				
				// Limpiar memoria cada cierto intervalo de iteraciones
				if (i++ % 1000 == 0)
					System.gc();
			}
		} catch(Exception e){
			e.printStackTrace();
			throw e;
		} finally{
			try{
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
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

		// Parsear el documento
		doc = builder.parse(new InputSource(new StringReader(columnValuesXML)));
		
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


	public int getM_changelog_initial_id() {
		return m_changelog_initial_id;
	}


	public int getM_changelog_final_id() {
		return m_changelog_final_id;
	}
	
}
