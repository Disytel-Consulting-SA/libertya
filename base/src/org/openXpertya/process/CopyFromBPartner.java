package org.openXpertya.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_User;
import org.openXpertya.model.X_C_BP_BankAccount;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_BPartner_Location;
import org.openXpertya.model.X_C_BPartner_RetExenc;
import org.openXpertya.model.X_C_BPartner_Retencion;
import org.openXpertya.model.X_C_CreditException;
import org.openXpertya.model.X_C_Location;
import org.openXpertya.model.X_M_Product_PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class CopyFromBPartner extends SvrProcess {

	// Variables de instancia
	
	/** ID de Entidad Comercial plantilla */
	
	private Integer p_C_BPartner_ID = null;
	
	/** Entidad Comercial plantilla  */
	
	private MBPartner bpartnerTemplate = null;
	
	/** ID de Nueva Entidad Comercial copia de la plantilla */
	
	private Integer newBPartnerID = null;
	
	/** Entidad Comercial nueva (o existente) */
	
	private MBPartner newBPartner = null;
	
	/** Asociación entre nombre de tabla y map con registros (POs) template y nuevos creados */
	
	private Map<String, Map<Integer, Integer>> records = null;
	
	@Override
	protected void prepare() {
        ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            
            if( para[ i ].getParameter() != null ) {
            	
            	if( name.equals( "C_BPartner_ID" )) {
            		p_C_BPartner_ID = para[ i ].getParameterAsInt();
            	}
            }
        }
        setRecords(new HashMap<String, Map<Integer,Integer>>());
        setBpartnerTemplate(new MBPartner(getCtx(), p_C_BPartner_ID, get_TrxName()));
	}

	@Override
	protected String doIt() throws Exception {
		// Obtener la nueva entidad comercial
		getBPartner();
		// Configurar los parámetros necesarios para los métodos siguientes
		String whereClause = "c_bpartner_id = ?";
		Object[] whereClauseParams = new Object[]{p_C_BPartner_ID};
		String[] customCommonColumnName = new String[]{"C_BPartner_ID"};		
		Object[] customCommonColumnValue = new Object[]{getNewBPartnerID()};
		// Localizaciones
		getRecords()
				.put(
						X_C_Location.Table_Name,
						PO
								.copyFrom(
										getCtx(),
										X_C_Location.Table_Name,
										"c_location_id IN (SELECT c_location_id FROM c_bpartner_location WHERE c_bpartner_id = ?)",
										whereClauseParams, null, null,
										get_TrxName()));
		// Localizaciones de la entidad comercial
		Set<Integer> locationsTemplatesIDs = (getRecords().get(X_C_Location.Table_Name)).keySet();
		// Itero por las localizaciones y creo las localizaciones de entidad
		// comercial con estas nuevas
		for (Integer locationTemplateID : locationsTemplatesIDs) {
			getRecords()
					.put(
							X_C_BPartner_Location.Table_Name,
							PO
									.copyFrom(
											getCtx(),
											X_C_BPartner_Location.Table_Name,
											"(c_bpartner_id = ?) AND (c_location_id = ?)",
											new Object[] { p_C_BPartner_ID,
													locationTemplateID },
											new String[] { "C_BPartner_ID",
													"C_Location_ID", "Name" },
											new Object[] {
													getNewBPartnerID(),
													(getRecords()
															.get(X_C_Location.Table_Name))
															.get(locationTemplateID), 
													null },
											get_TrxName()));
		}
		// Cuentas Bancarias
		getRecords().put(
				X_C_BP_BankAccount.Table_Name,
				PO.copyFrom(getCtx(), X_C_BP_BankAccount.Table_Name,
						whereClause, whereClauseParams, customCommonColumnName,
						customCommonColumnValue, get_TrxName()));
		// Artículos del Proveedor
		getRecords().put(
				X_M_Product_PO.Table_Name,
				PO.copyFrom(getCtx(), X_M_Product_PO.Table_Name, whereClause,
						whereClauseParams, new String[] { "C_BPartner_ID",
								"M_Product_ID" }, new Object[] {
								getNewBPartnerID(), null }, true, 1,
						get_TrxName()));
		// Usuarios
		getRecords()
				.put(
						X_AD_User.Table_Name,
						PO.copyFrom(getCtx(), X_AD_User.Table_Name,
								whereClause, whereClauseParams, new String[] {
										"C_BPartner_ID", "Name" },
								new Object[] {
										getNewBPartnerID(),
										getNewBPartner().getValue().replaceAll(
												" ", "_") }, get_TrxName()));
		// Retenciones
		getRecords().put(
				X_C_BPartner_Retencion.Table_Name,
				PO.copyFrom(getCtx(), X_C_BPartner_Retencion.Table_Name,
						whereClause, whereClauseParams, customCommonColumnName,
						customCommonColumnValue, get_TrxName()));
		// Períodos de Excepción de Retenciones
		// Obtengo los ids de retenciones template
		Set<Integer> retencionesTemplatesIDs = (getRecords()
				.get(X_C_BPartner_Retencion.Table_Name)).keySet();
		// Creo una map temporal para los nuevos períodos
		Map<Integer, Integer> retExencPeriods = new HashMap<Integer, Integer>();
		// Itero por los ids de retenciones template obtenidos para filtrar por
		// cada uno y crear los nuevos períodos seteando en columna custom el id
		// de retención nuevo
		for (Integer retencionTemplateID : retencionesTemplatesIDs) {
			retExencPeriods.putAll(PO.copyFrom(getCtx(),
					X_C_BPartner_RetExenc.Table_Name,
					"c_bpartner_retencion_id = ?",
					new Object[] { retencionTemplateID },
					new String[] { "C_BPartner_Retencion_ID" },
					new Object[] { (getRecords()
							.get(X_C_BPartner_Retencion.Table_Name))
							.get(retencionTemplateID) }, get_TrxName()));
		}
		// Agregar los nuevos períodos creados a la map global de registros
		getRecords().put(X_C_BPartner_RetExenc.Table_Name, retExencPeriods);
		// Excepciones de crédito
		getRecords().put(
				X_C_CreditException.Table_Name,
				PO.copyFrom(getCtx(), X_C_CreditException.Table_Name,
						whereClause, whereClauseParams, customCommonColumnName,
						customCommonColumnValue, get_TrxName()));
		// Actualizar el copyFrom de la nueva entidad comercial
		getNewBPartner().setCopyFrom("N");
		if(!getNewBPartner().save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Retorno mensaje ok
		return getMsg();
	}
	
	/**
	 * Obtiene la nueva entidad comercial, esto se puede realizar de dos maneras:
	 * <ul>
	 * <li>Desde el registro actual tomo una entidad comercial existente</li>
	 * <li>Si no tengo un registro actual significa que no estoy en una ventana por lo que debo crear una entidad comercial nueva</li>
	 * </ul>
	 * @throws Exception si hay error en la creación de la nueva entidad comercial
	 */
	private void getBPartner() throws Exception{
		Integer bpartnerID = null;
		boolean alreadyCopied = false;
		// Si el registro actual está vacío significa que se está ejecutando el
		// proceso desde el menú, por lo tanto se debe crear una entidad nueva a
		// partir de la template
		if(Util.isEmpty(getRecord_ID(), true)){
			// Crear una entidad comercial
			bpartnerID = PO.copyFrom(
					getCtx(),
					X_C_BPartner.Table_Name,
					"c_bpartner_id = ?",
					new Object[] { p_C_BPartner_ID },
					new String[] { "Value", "Name" },
					new Object[] { getBpartnerTemplate().getValue() + "(Copy)",
							getBpartnerTemplate().getName() + "(Copy)" },
					get_TrxName()).get(p_C_BPartner_ID);
			alreadyCopied = true;
		}
		else{
			// Obtengo el registro actual ya que existe
			bpartnerID = getRecord_ID();
		}
		// Seteo localmente la variable con el id de la nueva entidad comercial
		// dependiendo los casos descritos
		setNewBPartnerID(bpartnerID);
		setNewBPartner(new MBPartner(getCtx(), getNewBPartnerID(), get_TrxName()));
		if(!alreadyCopied){
			PO.copyValues(getBpartnerTemplate(), getNewBPartner(), Arrays
					.asList(new String[] { "Value", "Name" }));			
		}
		// Guardarlo en la map global
		Map<Integer, Integer> ids = new HashMap<Integer, Integer>();
		ids.put(p_C_BPartner_ID, getNewBPartnerID());
		getRecords().put(X_C_BPartner.Table_Name, ids);
	}
	
	/**
	 * @return mensaje final del proceso
	 */
	private String getMsg(){
		// Se debería colocar la traducción de las tablas, pero no están todas hechas y para no confundir al usuario mejor no poner nada
		StringBuffer msg = new StringBuffer();
		msg.append("@ProcessOK@").append("<br>");
		msg.append("@ElementsCopied@:").append("<br>");
		msg.append("<ul>");
		Set<String> tableNames = getRecords().keySet();
		// Itero por las tablas y coloco su traducción para los que hay registros replicados
		for (String tableName : tableNames) {
			if (getRecords().get(tableName) != null
					&& !getRecords().get(tableName).keySet().isEmpty()) {
				msg.append("<li>").append(DB.getSQLValueString(get_TrxName(), "SELECT name FROM ad_table_trl WHERE ad_table_id = "+M_Table.getTableID(tableName,get_TrxName())+" AND ad_language = ?", Env.getAD_Language(getCtx()))).append("</li>");
			}
		}
		msg.append("</ul>");
		msg.append("@BPartner@ @ScriptResult@:");
		msg.append(getNewBPartner().getName());
		return Msg.parseTranslation(getCtx(), msg.toString());
	}
	
	// Getters y Setters

	protected void setRecords(Map<String, Map<Integer, Integer>> records) {
		this.records = records;
	}

	protected Map<String, Map<Integer, Integer>> getRecords() {
		return records;
	}

	protected void setNewBPartnerID(Integer newBPartnerID) {
		this.newBPartnerID = newBPartnerID;
	}

	protected Integer getNewBPartnerID() {
		return newBPartnerID;
	}

	protected void setBpartnerTemplate(MBPartner bpartnerTemplate) {
		this.bpartnerTemplate = bpartnerTemplate;
	}

	protected MBPartner getBpartnerTemplate() {
		return bpartnerTemplate;
	}

	protected void setNewBPartner(MBPartner newBPartner) {
		this.newBPartner = newBPartner;
	}

	protected MBPartner getNewBPartner() {
		return newBPartner;
	}
}
