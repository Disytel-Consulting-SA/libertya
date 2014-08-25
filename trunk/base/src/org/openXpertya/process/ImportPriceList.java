package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MProductPrice;
import org.openXpertya.model.MProductPriceInstance;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_I_ProductPrice;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class ImportPriceList extends AbstractImportProcess {

	private int m_AD_Org_ID;
	/** Consulta de precios de artículos */
	private PreparedStatement productPricePstmt;
	private PreparedStatement productPriceInstancePstmt;
	/** Indicador de modo de importación del precio. TRUE indica que se
	 * está actualizando un precio existente, FALSE indica inserción de nuevo precio */
	private boolean updatingPrice = false;
	
	@Override
	protected void prepareImport() {
		// Parámetro: Organización
		m_AD_Org_ID = ((BigDecimal) getParameterValue("AD_Org_ID",
				BigDecimal.ZERO)).intValue();
		
		// Se crea la consulta para obtener los ProducPrices existentes.
		productPricePstmt = DB.prepareStatement(
				" SELECT * FROM M_ProductPrice " +
				" WHERE M_Pricelist_Version_ID = ? AND " +
				"       M_Product_ID = ? ");
		
		// Se crea la consulta para obtener los ProducPrices existentes.
		productPriceInstancePstmt = DB.prepareStatement(
				" SELECT * FROM M_ProductPriceInstance " +
				" WHERE M_Pricelist_Version_ID = ? AND " +
				"       M_Product_ID = ? 		   AND " +
				"       M_AttributeSetInstance_ID = ? ");
	}
	
	@Override
	protected void beforeImport() throws Exception {

		// Se asigna el ID de la versión de tarifa. En caso de no existir el nombre 
		// se asigna el mensaje de error correspondiente a la tupla. 
		setPriceListVersionID();
		
		// Se asigna el ID de los productos a partir de la clave de búsqueda
		// importada. En caso de no existir la clave se asigna el mensaje
		// de error correspondiente a la tupla.
		setProductID();
		
		// Se asigna el ID del conjunto de atributo a partir del UPC de instancia
		// importado. En caso de no existir la clave se asigna el mensaje
		// de error correspondiente a la tupla.
		setProductInstanceID();


		// Se asigna la organización a cada registro de importación a partir de la
		// organización que tiene asignada su correspondiente lista de precios.
		setPriceOrganization();
	}
	
	/**
	 * Asigna el ID de los artículo a partir de la clave de búsqueda, 
	 * o bien del UPC de artículo correspondiente, indicado a nivel 
	 * artículo o en la tabla de UPCs de artículo.
	 * En caso de no exisitir la clave de búsqueda se agrega un mensaje de
	 * error en la tupla.
	 */
	private void setProductID() throws Exception {
		int no;
		StringBuffer sql;
		// Se obtiene el ID de producto a partir de la clave de busqueda y se actualiza
		// el campo M_Product_ID. En caso de no existir el campo queda en NULL.
		sql = new StringBuffer(
				" UPDATE I_ProductPrice ip " +
				" SET M_Product_ID = " +
				"		(SELECT M_Product_ID " +
				"		 FROM M_Product p " +
				"        WHERE TRIM(ip.M_Product_Value) = TRIM(p.Value) AND " +
				"              ip.AD_Client_ID = p.AD_Client_ID AND " +
				"              p.IsActive = 'Y' AND " +
				"              ROWNUM=1) "+
				" WHERE M_Product_Value IS NOT NULL AND " + 
				"       I_IsImported <> 'Y' AND ").append(getSecuritySQLCheck());
		
		no = DB.executeUpdate(sql.toString());
		log.info("doIt - Set Product from value = " + no);

		// Se obtiene el ID de producto a partir del UPC indicado y se actualiza
		// el campo M_Product_ID. En caso de no existir el campo queda en NULL.
		sql = new StringBuffer(
				" UPDATE I_ProductPrice ip " +
				" SET M_Product_ID = " +
				"		(SELECT p.M_Product_ID " +
				"		 FROM M_Product p " +
				"        INNER JOIN M_ProductUPC pupc ON p.M_Product_ID = pupc.M_Product_ID " +
				"        WHERE (TRIM(ip.ProductUPC) = TRIM(p.upc) OR TRIM(ip.ProductUPC) = TRIM (pupc.upc)) AND" +
				"              ip.AD_Client_ID = p.AD_Client_ID AND " +
				"              p.IsActive = 'Y' AND " +
				"              ROWNUM=1) "+
				" WHERE ProductUPC IS NOT NULL AND M_Product_ID IS NULL AND " + 
				"       I_IsImported <> 'Y' AND ").append(getSecuritySQLCheck());
		
		no = DB.executeUpdate(sql.toString());
		log.info("doIt - Set Product from UPC = " + no);
		
		// Se asignan mensajes de error a las tuplas en las que no se encontró
		// la clave del artículo.
		no = setImportError("M_Product_ID IS NULL", "ProductValueNotFound");
		if (no > 0)
			log.warning("doIt - Products value not found = " + no);
	}
	
	/**
	 * Asigna el ID del conjunto de atributos a partir del UPC de instancia  
	 * En caso de no exisitir la clave de búsqueda se agrega un mensaje de
	 * error en la tupla.
	 */
	private void setProductInstanceID() throws Exception {
		int no;
		StringBuffer sql;
		// Se obtiene el ID de producto a partir de la clave de busqueda y se actualiza
		// el campo M_Product_ID. En caso de no existir el campo queda en NULL.
		sql = new StringBuffer(
				" UPDATE I_ProductPrice ip " +
				" SET M_AttributeSetInstance_ID = " +
				"		(SELECT p.M_AttributeSetInstance_ID " +
				"		 FROM M_Product_UPC_instance P " +
				"        WHERE TRIM(ip.instanceupc) = TRIM(p.upc) AND " +
				"              ip.AD_Client_ID = p.AD_Client_ID AND " +
				"              p.IsActive = 'Y' AND " +
				"              ROWNUM=1), "+
				" 	  M_Product_ID = " +
				"		(SELECT p.M_Product_ID " +
				"		 FROM M_Product_UPC_instance P " +
				"        WHERE TRIM(ip.instanceupc) = TRIM(p.upc) AND " +
				"              ip.AD_Client_ID = p.AD_Client_ID AND " +
				"              p.IsActive = 'Y' AND " +
				"              ROWNUM=1) "+				
				" WHERE InstanceUPC IS NOT NULL AND " + 
				"       I_IsImported <> 'Y' AND ").append(getSecuritySQLCheck());
		
		no = DB.executeUpdate(sql.toString());
		log.info("doIt - Set ProductInstance from UPC = " + no);
	}

	/**
	 * Asigna el ID de la versión de tarifa a partir del nombre importado.
	 * En caso de no exisitir el nombre se agrega un mensaje de
	 * error en la tupla.
	 */
	private void setPriceListVersionID() throws Exception {
		int no;
		StringBuffer sql;
		// Se obtiene el ID de la versión de tarifa a partir del nombre y se actualiza
		// el campo M_Pricelist_Version_ID. En caso de no existir el campo queda en NULL.
		sql = new StringBuffer(
				" UPDATE I_ProductPrice ip " +
				" SET M_Pricelist_Version_ID = " +
				"		(SELECT M_PriceList_Version_ID " +
				"        FROM M_PriceList_Version p " +
				"        WHERE TRIM(ip.PriceList_Name) = TRIM(p.Name) AND " +
				"              ip.AD_Client_ID = p.AD_Client_ID AND " +
				"              p.IsActive = 'Y' AND " +
				"              ROWNUM=1) " +
				" WHERE PriceList_Name IS NOT NULL AND " +
				"       I_IsImported <> 'Y' AND ").append(getSecuritySQLCheck());

		no = DB.executeUpdate(sql.toString ());
		log.info("doIt - Set PriceList Version from name = " + no);

		// Se asignan mensajes de error a las tuplas en las que no se encontró
		// la lista de precios.
		no = setImportError("M_Pricelist_Version_ID IS NULL", "PriceListVersionNotFound");
		if (no > 0)
			log.warning("doIt - PriceList Version Name not found = " + no);
	}

	/**
	 * Asigna la Organización de la lista de precio a cada registro.
	 */
	private void setPriceOrganization() throws Exception {
		int no;
		// Obtiene la organización de la lista de precios y se la asigna al registro.
		// Solo se modifican los registros que tienen ID de lista de precios asignado.
		StringBuffer sql = new StringBuffer(
				" UPDATE I_ProductPrice ip " +
				" SET AD_Org_ID = " + 
				"       (SELECT pl.AD_Org_ID " +
				"        FROM M_PriceList_Version pl " +
				"        WHERE pl.M_PriceList_Version_ID = ip.M_PriceList_Version_ID) " + 
				" WHERE M_PriceList_Version_ID IS NOT NULL AND " +
				"       I_IsImported <> 'Y' AND ").append(getSecuritySQLCheck());
		
		no = DB.executeUpdate(sql.toString());
		log.info("doIt - Set PriceList Version Organization = " + no);
	}

	@Override
	protected String importRecord(PO importPO) throws Exception {
		X_I_ProductPrice importPP = (X_I_ProductPrice)importPO;
		
		// Si la linea de importacion tiene definido un attributesetinstance
		// entonces, debera actualizar la definicion de precio por instancia
		if (importPP.getM_AttributeSetInstance_ID() == 0)
			return importProductPrice(importPP);
		else
			return importProductPriceInstance(importPP);			
	}

	/**
	 * Realiza la importacion a tabla de producción de una entrada
	 * todavia no procesada contenida en la tabla de importación.
	 * (articulos sin instancias de atributo)
	 */
	protected String importProductPrice(X_I_ProductPrice importPP) throws Exception
	{
		// Se obtiene el ProductPrice, en caso de no exisitr se crea uno nuevo.
		MProductPrice productPrice = getProductPrice(importPP);
		
		// Se modifican/crean los datos del precio del artículo.
		productPrice.setIsActive(true);
		productPrice.changeOrg(importPP.getAD_Org_ID());
		productPrice.setPriceLimit(importPP.getPriceLimit());
		productPrice.setPriceList(importPP.getPriceList());
		productPrice.setPriceStd(importPP.getPriceStd());
		
		// Se intentan guardar los cambios.
		if(!productPrice.save ()) {
			// Se obtiene un mensaje de error descriptivo en caso de que haya ocurrido
			// algún problema en el guardado.
			String errorTitle = updatingPrice ? "@ProductPriceUpdateError@" : "@ProductPriceInsertError@";
			StringBuffer errorMsg = new StringBuffer(errorTitle);
			String errorDesc = getErrorDescription();
			if (errorDesc != null && errorDesc.length() > 0)
				errorMsg.append("<br>- ").append(errorDesc);
			
			// Se asigna el mensaje de error al campo de error del registro de importación.
			return Msg.parseTranslation(getCtx(), errorMsg.toString());
		}
		
		// Retorna el resulado satisfactorio.
		return IMPORT_OK;
	}
	
	/**
	 * Realiza la importacion a tabla de producción de una entrada
	 * todavia no procesada contenida en la tabla de importación.
	 * (articulos con instancias de atributos)
	 */
	protected String importProductPriceInstance(X_I_ProductPrice importPP) throws Exception
	{
		// Se obtiene el ProductPrice, en caso de no exisitr se crea uno nuevo.
		MProductPriceInstance productPriceInstance = getProductPriceInstance(importPP);
		
		// Se modifican/crean los datos del precio del artículo.
		productPriceInstance.setIsActive(true);
		productPriceInstance.changeOrg(importPP.getAD_Org_ID());
		productPriceInstance.setPriceLimit(importPP.getPriceLimit());
		productPriceInstance.setPriceList(importPP.getPriceList());
		productPriceInstance.setPriceStd(importPP.getPriceStd());
		
		// Se intentan guardar los cambios.
		if(!productPriceInstance.save ()) {
			// Se obtiene un mensaje de error descriptivo en caso de que haya ocurrido
			// algún problema en el guardado.
			String errorTitle = updatingPrice ? "@ProductPriceUpdateError@" : "@ProductPriceInsertError@";
			StringBuffer errorMsg = new StringBuffer(errorTitle);
			String errorDesc = getErrorDescription();
			if (errorDesc != null && errorDesc.length() > 0)
				errorMsg.append("<br>- ").append(errorDesc);
			
			// Se asigna el mensaje de error al campo de error del registro de importación.
			return Msg.parseTranslation(getCtx(), errorMsg.toString());
		}
		
		// Retorna el resulado satisfactorio.
		return IMPORT_OK;
	}
	
	
	@Override
	protected String afterImport() throws Exception {
		// Nada que realizar. El mensaje de retorno es el que se asigna por defecto.
		return null;
	}

	/**
	 * Obtiene un MProductPrice a partir de la clave primaria en el PO de importación.
	 * @param importPP Registro a importar.
	 * @return <code>MProductPrice</code> con el precio del artículo.
	 * @throws Exception en caso de producirse algún error SQL.
	 */
	private MProductPrice getProductPrice(X_I_ProductPrice importPP) throws Exception {
		MProductPrice productPrice = null;
		// Indicador de actualización o inserción de precio de producto.
		updatingPrice = false;
		// Se consulta si ya existe un precio asignado similar al que se quiere
		// importar.
		getProductPricePstmt().setInt(1,importPP.getM_PriceList_Version_ID());
		getProductPricePstmt().setInt(2,importPP.getM_Product_ID());
		ResultSet rsProductPrice = getProductPricePstmt().executeQuery();
		// Si existe se obtienen el modelo del mismo para actualizar los precios.
		if(updatingPrice = rsProductPrice.next())
			productPrice = new MProductPrice(getCtx(),rsProductPrice, null);
		// Si no existe se crea uno nuevo.
		else
			productPrice = new MProductPrice(getCtx(), importPP.getM_PriceList_Version_ID(), importPP.getM_Product_ID(), null);
		
		rsProductPrice.close();
		return productPrice;
	}
	
	/**
	 * Obtiene un MProductPriceInstance a partir de la clave primaria en el PO de importación.
	 * @param importPP Registro a importar.
	 * @return <code>MProductPriceInstance</code> con el precio del artículo para la instancia dada.
	 * @throws Exception en caso de producirse algún error SQL.
	 */
	private MProductPriceInstance getProductPriceInstance(X_I_ProductPrice importPP) throws Exception {
		MProductPriceInstance productPriceInstance = null;
		// Indicador de actualización o inserción de precio de producto.
		updatingPrice = false;
		// Se consulta si ya existe un precio asignado similar al que se quiere
		// importar.
		getProductPriceInstacePstmt().setInt(1,importPP.getM_PriceList_Version_ID());
		getProductPriceInstacePstmt().setInt(2,importPP.getM_Product_ID());
		getProductPriceInstacePstmt().setInt(3,importPP.getM_AttributeSetInstance_ID());
		ResultSet rsProductPriceInstance = getProductPriceInstacePstmt().executeQuery();
		// Si existe se obtienen el modelo del mismo para actualizar los precios.
		if(updatingPrice = rsProductPriceInstance.next())
			productPriceInstance = new MProductPriceInstance(getCtx(),rsProductPriceInstance, null);
		// Si no existe se crea uno nuevo.
		else
			productPriceInstance = new MProductPriceInstance( getCtx(), importPP.getM_PriceList_Version_ID(), importPP.getM_Product_ID(), importPP.getM_AttributeSetInstance_ID(), null);
		
		rsProductPriceInstance.close();
		return productPriceInstance;
	}
	

	/**
	 * @return Retorna el filtro SQL de seguridad para el correcto acceso
	 * a los registros de importacion de precios.
	 */
	protected String getSecuritySQLCheck() {
		StringBuffer check = new StringBuffer(
				"(").append(getClientSQLCheck()).append(" AND ")
				.append(getUserSQLCheck()).append(" AND ")
				.append("IsActive = 'Y')");
		return check.toString();
	}

	/**
	 * @return Returns the productPricePstmt.
	 */
	private PreparedStatement getProductPricePstmt() {
		return productPricePstmt;
	}
	
	/**
	 * @return Returns the productPricePstmt.
	 */
	private PreparedStatement getProductPriceInstacePstmt() {
		return productPriceInstancePstmt;
	}
}
