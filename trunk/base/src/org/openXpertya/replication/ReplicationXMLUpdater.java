package org.openXpertya.replication;

/**
 * Redefinición de PluginXMLUpdater a fin de 
 * procesar los XMLs de replicación basándose en las
 * convenciones especiales para recuperación de referencias (FK)
 * 
 * 
 * -------------------------------------------------------------------------
 * Para cada changegroup, el UID es utilizado para alamacenar el retrieveUID
 * -------------------------------------------------------------------------
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.plugin.install.PluginXMLUpdater;
import org.openXpertya.plugin.install.PluginXMLUpdater.ChangeGroup;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;


public class ReplicationXMLUpdater extends PluginXMLUpdater {

	/** Sucursal origen de la cual se está replicando */
	protected int m_AD_Org_ID = -1;

	/** Relacion entre la posicion del una organizacion en el replicationArrayPos y el AD_Org_ID */
	protected HashMap<Integer, Integer> map_RepArrayPos_OrgID = null;
	
	/** Ultimo changelogID a replicar */
	protected int m_initialChangelogID;
	protected int m_finalChangelogID;
	
	protected static final int NO_UPDATE_CHANGELOG = -99;
	
	/** Rellena el buffer que mapea los AD_ORG_IDs con las posiciones de cada una de estas en el replicationArrayPos */
	protected void loadOrgsMap() throws Exception
	{
		if (map_RepArrayPos_OrgID == null)	{
			map_RepArrayPos_OrgID = new HashMap<Integer, Integer>();
			String sql = new String (	" SELECT replicationarraypos, ad_org_id FROM AD_ReplicationHost WHERE AD_Client_ID = " +
										" (SELECT AD_CLIENT_ID FROM AD_ReplicationHost WHERE thisHost = 'Y') " );
			PreparedStatement pstmt = DB.prepareStatement(sql, m_trxName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				map_RepArrayPos_OrgID.put(rs.getInt(1), rs.getInt(2));
			rs.close();
			rs = null;
			pstmt = null;
		}
	}
	
	
	/**
	 * Impacta el changelog en base de datos.  De no presentarse 
	 * inconvenientes durante dicha tarea, actualizará el valor 
	 * correspondiente al ultimo changelog replicado para el host origen.
	 */
	public static void processChangelog(String contentXML, String m_trxName, int AD_Org_ID, int initialChangelogID, int finalChangelogID) throws Exception
	{
		// Procesar el changelog 
		new ReplicationXMLUpdater(contentXML, m_trxName, AD_Org_ID, initialChangelogID, finalChangelogID).processChangeLog();

		// Guardar el dato de ultimo changelog impactado para el host origen 
		int no = DB.executeUpdate(	" UPDATE AD_ReplicationHost SET LastChangelog_ID = " + 
									finalChangelogID + " WHERE AD_Org_ID = " + AD_Org_ID, m_trxName);
		if (no != 1)
			throw new Exception("Error al actualizar ultimo changelog (" + finalChangelogID + ") para host " + AD_Org_ID);
	}
	
	
	/**
	 * Constructor que debe recibir:
	 * 	1) El xml a procesar
	 *  2) La transacción
	 *  3) La organización origen de la cual se está replicando
	 */
	public ReplicationXMLUpdater(String xml, String trxName, int AD_Org_ID, int initialChangelogID, int finalChangelogID) throws Exception
	{
		super(xml, trxName, true);
		m_AD_Org_ID = AD_Org_ID;
		m_initialChangelogID = initialChangelogID;
		m_finalChangelogID = finalChangelogID;
		loadOrgsMap();
	}
	
	/**
	 * Constructor especial para replicación tardía.  En este caso el changelog desde y hasta
	 * es irrelevante, dado que no se deberá actualizar el dato de último changelog replicado
	 * (para que luego sea posible replicar los registros previos del changelog en el host origen )
	 */
	public ReplicationXMLUpdater(String xml, String trxName, int AD_Org_ID) throws Exception
	{
		super(xml, trxName, true);
		m_AD_Org_ID = AD_Org_ID;
		m_initialChangelogID = NO_UPDATE_CHANGELOG;
		m_finalChangelogID = NO_UPDATE_CHANGELOG;
		loadOrgsMap();
	}
	
	/**
	 * Constructor especial para realizar el procesamiento de un changeGroup en particular.
	 * Esto es utilizado con la finalidad de replicar con antelación un registro de replicación tardía
	 * (tipico caso en el que se omitió marcar una tabla para replicación, pero luego se intentó 
	 * referenciar un registro de dicha tabla, con lo cual la bitácora de inserción quedará relegada)
	 * Para este caso, no se deben realizar validaciones/actualizaciones relacionadas con ultimo changelog
	 */
	public ReplicationXMLUpdater(ChangeGroup aChangeGroup, String trxName, int AD_Org_ID) throws Exception
	{
		super(aChangeGroup, trxName, true);
		m_AD_Org_ID = AD_Org_ID;
		m_initialChangelogID = NO_UPDATE_CHANGELOG;
		m_finalChangelogID = NO_UPDATE_CHANGELOG;
		loadOrgsMap();
	}
	
	/** 
	 * Redefinición para validaciones por campo finalChangelogID en tabla AD_ReplicationHost.
	 * Estas validaciones intentan reducir las posibilidades de error en los casos en que
	 * se intente procesar más de una vez el mismo lote de replicación.
	 * 
	 * Ejemplo.  Suponiendo ultimo changelog para el host origen X es igual a 5, entonces:
	 * 
	 * Si se recibe initial=2 y final=4 -> Omitir todo procesamiento sin mayores problemas
	 * Si se recibe initial=6 y final=8 -> Realizar procesamiento de manera tradicional
	 * Si se recibe initial=3 y final=7 -> Error! El lote debería ser depurado 
	 */
	public void processChangeLog() throws Exception
	{
		// Leer el valor del ultimo changelog del host origen correctamente replicado
		int lastReplicatedID = MReplicationHost.getLastChangelogID(m_AD_Org_ID, m_trxName);
		
		// Si el valor maximo del lote a replicar es menor que el ultimo replicado,
		// entonces no es necesario realizar tarea alguna, ya que fue completamente replicado
		// Esto realizarlo unicamente cuando no se recibe la constante de ignorar el changelogID
		if (m_finalChangelogID != NO_UPDATE_CHANGELOG && m_finalChangelogID <= lastReplicatedID)
		{
			System.out.println(" Sin procesamiento. Lote ya replicado. " + getReplicationDetails(lastReplicatedID));
			return;	
		}
		
		// Si el ultimo changelog replicado se encuentra entre los valores inicial y final
		// del lote a replicar, entonces hay un error, ya que debería omitirse una parte
		// de dicho lote, iniciando luego del ultimo registrado
		// Esto realizarlo unicamente cuando no se recibe la constante de ignorar el changelogID
		if (m_finalChangelogID != NO_UPDATE_CHANGELOG && lastReplicatedID >= m_initialChangelogID && lastReplicatedID <= m_finalChangelogID)
			throw new Exception (" Imposible replicar el lote especificado (superposicion de changelogs). " + getReplicationDetails(lastReplicatedID));
		
		// Reiniciar el changelog
		PluginUtils.resetStatus();
		
		// Si es initial es mayor al ultimo replicado, entonces replicar de manera tradicional
		super.processChangeLog();
		
		String fileName = "Replication_from_" + m_AD_Org_ID + "_" + Env.getDateTime("yyyyMMdd_HHmmss") + ".log";
		PluginUtils.writeInstallLog(OpenXpertya.getOXPHome(), fileName);
	}
	
	/**
	 * Detalle sobre la replicación a realizar
	 */
	protected String getReplicationDetails(int lastReplicatedID)
	{
		return
			" Ultimo changelog replicado para host origen: " + lastReplicatedID +
			" - Changelog inicial del lote: " + m_initialChangelogID +
			" - Changelog final del lote: " + m_finalChangelogID +
			" - Host origen: " + m_AD_Org_ID;
	}
	
	/**
	 * Redefinición para determinar si es una columna de referenia
	 */
	protected boolean isReferenceColumn(Column column)
	{
		/* Si tiene una referencia a una tabla, entonces es una columna de FK*/
		return (!"".equals(column.getRefTable()));
	}
	
	/**
	 * Redefinición para obtener el registro referenciado correcto según el retrieveUID
	 * (usado para recuperación de registros en columnas de tablas foraneas)
	 */
	protected int getReferenceRecordID(StringBuffer query, String refKeyColumnName, Column column) throws Exception
	{
		/* Determinar si la referencia a buscar está alojada en retrieveUID o bien es directa (no existe el registro en la tabla) */
		boolean useRetrieveUID = column.getNewValue().startsWith(ReplicationBuilder.UID_REFERENCE_PREFIX);	// (1 == DB.getSQLValue(m_trxName, "SELECT count(1) FROM information_schema.columns WHERE table_name = '" + column.getRefTable().toLowerCase() + "' AND column_name = 'retrieveuid'"));
		
		/* Si existe el campo retrieveUID, utilizar este, sino hacer bypass del dato (si es dato vacio pasar null) */
		if (!useRetrieveUID)
			return (column.getNewValue()==null||column.getNewValue().equals(""))?-1:Integer.parseInt(column.getNewValue());
				
		/* valor a retornar via retrieveUID (despreciar los 4 caracteres de UID) */
		String retrieveUIDSQL = " SELECT " + refKeyColumnName + " FROM " + column.getRefTable() + 
								" WHERE " + appendUniversalRefenceWhereClause(column.getNewValue().substring(ReplicationBuilder.UID_REFERENCE_PREFIX.length()));
		int retValue = DB.getSQLValue(m_trxName, retrieveUIDSQL);
		
		/* Ultima alternativa: ver si se realizó la inserción tardía dentro del changelog */
		if (retValue == -1)
			retValue = searchForDelayedInsert(refKeyColumnName, column);
		
		/* Elevar una excepción si no pudieron mapearse correctamente las referencias se dispara la excepción correspondiente */
		if (refKeyColumnName == null || retValue == -1)
			raiseException(" - imposible determinar referencia (" + retrieveUIDSQL + ")");
	
		return retValue;
	}
	
	/**
	 * Busca en el changelog una entrada generada de manera tardía
	 * (posterior en el tiempo con respecto a una entrada que la referenciaba)
	 * @return el ID local de la referencia buscada, o -1 en caso contrario
	 */
	protected int searchForDelayedInsert(String refKeyColumnName, Column column) throws Exception
	{
		// Existe una entrada con la referencia buscada (una operacion de insercion con dicho refUID?
		String refUID = column.getNewValue().substring(ReplicationBuilder.UID_REFERENCE_PREFIX.length());
		for (ChangeGroup changeGroup : getUpdateDocument().getChageGroupList())
			if (changeGroup.getUid().equals(refUID) && changeGroup.getOperation().equals(MChangeLog.OPERATIONTYPE_Insertion))
			{
				// En caso de existir, generar el SQL correspondiente, impactar en la transaccion y recuperar su ID local
				new ReplicationXMLUpdater(changeGroup, m_trxName, m_AD_Org_ID).processChangeLog();
				return getReferenceRecordID(null, refKeyColumnName, column);
			}

		// si no es posible encontrarla, retornar -1 
		return -1;
	}
	
	/**
	 * Redefinición de método a fin de almacenar el retrieveUID (clave original en host origen)
	 * en la tabla correspondiente.  Es por esto que la tabla destino DEBE contener el campo retrieveUID
	 */
	protected void appendKeyColumnValue(StringBuffer columnNames, StringBuffer columnValues, String tableName, String valueID)
	{
		super.appendKeyColumnValue(columnNames, columnValues, tableName, valueID);
	}
		
	/**
	 * Redefinición para recuperación de ID
	 * Se deben contemplar casos en los que la referencia 
	 * sea una tabla con multiples columnas clave
	 */
	protected String appendUniversalRefenceWhereClause(String reference)
	{
		return " retrieveUID = '" + reference + "' ";
	}
	
	/**
	 * El camo retrieveID se encuentra almacenado en el UID del changeGroup
	 */
	protected String getUniversalReference(ChangeGroup changeGroup)
	{
		return changeGroup.getUid();	
	}

	/**
	 * El camo retrieveID se encuentra almacenado en el UID del changeGroup
	 */
	protected boolean validateUniversalReference(ChangeGroup changeGroup)
	{
		return (changeGroup.getUid() != null);
	}
	
	/**
	 * Redefinicion: Es necesario incorporar una columna mas: el retrieveUID!
	 */
	protected void customizeInsertionQuery(StringBuffer sql, ChangeGroup changeGroup)
	{
		// Insertar en el query el nombre de las columnas 
		int lastColumnPos =  sql.indexOf(")");
		sql.insert(lastColumnPos, ",retrieveUID");
		// Insertar en el query los valores de las columnas
		int lastValuePos =  sql.lastIndexOf(")");
		sql.insert(lastValuePos, ",'" + changeGroup.getUid() + "'");
	}
	
	/**
	 * Redefiniciones especiales
	 */
	protected boolean appendSpecialValues(StringBuffer query, Column column, String tableName) throws Exception 
	{
		boolean retValue = false;
		
		/* En la tabla C_BPartner se almacena el campo AD_Language, los cuales no son para replicacion, debido a que ya existen con anterioridad */
		if (tableName.equalsIgnoreCase("C_BPartner") && column.getName().equalsIgnoreCase("AD_Language") && (column.getRefUID() == null || column.getRefUID().length() == 0))
		{
			query.append( "'" + column.getNewValue() + "'");
			retValue = true;
		}
		/* Tablas de traducciones, para el campo AD_Language no hay que resolver valores */
		else if (tableName.toLowerCase().endsWith("_trl") && "AD_Language".equalsIgnoreCase(column.getName()))
		{
			query.append( "'" + column.getNewValue() + "'");
			retValue = true;
		}
		/* A fin de que el procesador contable genere las entradas contables correspondientes, el Posted deben ser pasadas como false */
		else if ("Posted".equals(column.getName()))
		{
			query.append( "'N'");
			retValue = true;
		}
		/* Dado que una misma sucursal puede tener un AD_Org_ID distinto en cada host, se debe realiza el mapeo correspondiente 
		 * (siempre y cuando sea una organizacion con valor distinto de cero, en este caso no es necesario realizar mapeo alguno
		 * 	Para el caso en que la tabla AD_Org se encuentra marcada para replicación, el registro "0"
		 * 	en realidad llegará como UID=o0_0, con lo cual hay que tener en cuenta este caso adicional */
		else if ("AD_Org_ID".equals(column.getName()) && (!"0".equals(column.getNewValue())) && (!"UID=o0_0".equals(column.getNewValue())))
		{
			// En el AD_Org_ID en realidad no me llega el AD_Org_ID sino que me llega el host (replicationArrayPos) cargado,
			// dado que este es el único valor en común que comparten todas las organizaciones
			Integer newOrgID = map_RepArrayPos_OrgID.get(Integer.parseInt(column.getNewValue()));

			// Si no se puede mapear la sucursal, elevar la excepcion correspondiente
			if (newOrgID == null)
				raiseException(" - imposible determinar el AD_Org_ID para la sucursal (" + column.getNewValue() + ")");

			// Incorporar el AD_Org_ID correspondiente en este host
			query.append(newOrgID);
			retValue = true;
		}
		
		/* Si es una columna especial, concatenar la coma final */
		if (retValue)
			query.append(",");
		
		/* Si ya se aplico una columna especial, entonces no invocar a super */
		return retValue || super.appendSpecialValues(query, column, tableName);
	}
	
}
