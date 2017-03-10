package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Asociación de multiples códigos de barra (UPC/EAN) a un artículo
 * 
 * @author Franco Bonafine - Disytel
 */
@SuppressWarnings("serial")
public class MProductUPC extends X_M_ProductUPC {

	/** Logger estático */
	private static CLogger s_log = CLogger.getCLogger( MProductUPC.class );
	
	/** Indica si la operación en este UPC de artículo se está efectuando a partir
	 * de un cambio en el campo UPC del artículo (M_Product.UPC) */
	private boolean productUPCUpdate = false;
	
	/***
	 * @param ctx contexto
	 * @param upc upc
	 * @param 
	 * @param excludeProductUPCID id de la tabla a excluir
	 * @param trxName nombre de la transacción actual
	 * @return primer ID del artículo con ese UPC
	 */
	public static int getProductIDFromUPC(Properties ctx, String upc, Integer excludeProductID, Integer excludeProductUPCID, String trxName){
		String sql = "SELECT M_Product_ID "
					+ "FROM M_ProductUPC "
					+ "WHERE AD_Client_ID =  " + Env.getAD_Client_ID(ctx)
					+ "		AND UPC = '"+upc+"' "
					+ "		AND isactive = 'Y' ";
		
		if(excludeProductID != null && excludeProductID > 0){
			sql += "	AND M_Product_ID <> "+excludeProductID;
		}
		
		if(excludeProductUPCID != null && excludeProductUPCID > 0){
			sql += "	AND M_ProductUPC_ID <> "+excludeProductUPCID;
		}
		
		return DB.getSQLValue(trxName, sql);
	}
	
	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param M_ProductUPC_ID
	 * @param trxName
	 */
	public MProductUPC(Properties ctx, int M_ProductUPC_ID, String trxName) {
		super(ctx, M_ProductUPC_ID, trxName);
	}

	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MProductUPC(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Constructor de la clase
	 * @param product Artículo al cual se le asocia el nuevo UPC
	 * @param upc Código UPC/EAN a asociar
	 */
	public MProductUPC(MProduct product, String upc) {
		this(product.getCtx(), 0, product.get_TrxName());
		setClientOrg(product);
		setM_Product_ID(product.getM_Product_ID());
		setUPC(upc.trim());
	}

	/**
	 * Busca y devuelve la instancia que contiene el UPC por defecto para un determinado
	 * artículo.
	 * @param ctx Contexto, necesario para instanciar MProductUPC
	 * @param productID ID de artículo
	 * @param trxName Transacción de BD
	 * @return {@link MProductUPC} o <code>null</code> si no hay códigos marcados
	 * como por defecto para el artículo, o el código por defecto está desactivado 
	 * (IsActive = F) 
	 */
	public static MProductUPC getDefault(Properties ctx, int productID, String trxName) {
		MProductUPC productUPC = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = 
			"SELECT * FROM M_ProductUPC " +
			"WHERE M_Product_ID = ? AND IsDefault = 'Y' AND IsActive = 'Y'";
			
		try {
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, productID);
			
			rs = pstmt.executeQuery();
			// Si existe un código UPC se crea la instancia
			if (rs.next()) {
				productUPC = new MProductUPC(ctx, rs, trxName);
			}
		} catch (SQLException e) {
			s_log.severe("Error getting default UPC for product ID=" + productID);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		return productUPC;
	}
	
	/**
	 * Obtiene una lista de los UPCs asociados a un determinado artículo. En la lista <b>se
	 * incluyen los registros que están desactivados</b>. En la lista devuelta aparecen primero
	 * los UPCs marcados como predeterminados, y el orden de los mismos es según su fecha de
	 * creación en forma descendente, es decir, los mas nuevos primero.
	 * @param ctx Contexto, necesario para instanciar MProductUPC
	 * @param productID ID de artículo
	 * @param trxName Transacción de BD
	 * @return {@link List} de {@link MProductUPC}. Si el artículo no tiene ningún
	 * código UPC asociado devuelve una lista vacía.
	 */
	public static List<MProductUPC> getOfProduct(Properties ctx, int productID, String trxName) {
		List<MProductUPC> upcList = new ArrayList<MProductUPC>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = 
			"SELECT * FROM M_ProductUPC " +
			"WHERE M_Product_ID = ? " +
			"ORDER BY IsDefault DESC, Created DESC";
			
		try {
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, productID);
			
			rs = pstmt.executeQuery();
			MProductUPC productUPC = null;
			// Se obtiene las instancias de MProductUPC
			while (rs.next()) {
				productUPC = new MProductUPC(ctx, rs, trxName);
				upcList.add(productUPC);
			}
		} catch (SQLException e) {
			s_log.severe("Error getting UPCs for product ID=" + productID);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		return upcList;
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Borramos espacios de mas
		setUPC(getUPC().trim());
		
		// Validación de unicidad del upc
		if (!validateUniqueUPC()) {
			return false;
		}
		
		// Si se desactiva el registro se le quita la marca de predeterminado.
		// Un UPC inactivo no puede ser el predeterminado.
		if (is_ValueChanged("IsActive") && !isActive()) {
			setIsDefault(false);
		}
		
		return true;
	}
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		if (success) {
			// Si este es el código por defecto...
			if (isDefault()) {
				// Se obtiene el código que estaba marcado por defecto anteriormente
				MProductUPC previousDefaultUPC = 
					MProductUPC.getDefault(getCtx(), getM_Product_ID(), get_TrxName());
				// Si había un UPC que estaba marcado por defecto, se le quita la marca
				// indicando que ya no es mas el código por defecto.
				if (previousDefaultUPC != null && previousDefaultUPC.getM_ProductUPC_ID() != getM_ProductUPC_ID()) {
					previousDefaultUPC.setIsDefault(false);
					success = previousDefaultUPC.save();
				}

				// Actualiza el UPC del artículo. Se mantiene una sincronización entre
				// el UPC en M_Product y el UPC marcado como IsDefault en M_ProductUPC
				// Si el save de este UPC fue disparado por una actualización en el UPC
				// del artículo entonces no se debe actualizar el artículo para no entrar
				// en un loop infinito.
				if (success && !isProductUPCUpdate()) {
					MProduct product = new MProduct(getCtx(), getM_Product_ID(), get_TrxName());
					product.setUPC(getUPC());
					success = product.save();
				}
			}
			
			// Cuando se desactiva un registro o se le quita la marca de predetermina
			// se efectúa el mantenimiento de sincronismo entre el UPC que se está
			// desactivando/desmarcando con el UPC seteado en el artículo asociado.
			// Si el save de este UPC fue disparado por una actualización en el UPC
			// del artículo entonces no se debe actualizar el artículo para no entrar
			// en un loop infinito.
			if (success && (!isActive() || !isDefault()) && !isProductUPCUpdate()) {
				success = clearProductUPCReference();
			}
		}
		return success;
	}

	@Override
	protected boolean afterDelete(boolean success) {
		if (success) {
			if (isDefault()) {
				clearProductUPCReference();
			}
		}
		return success;
	}

	/**
	 * @return Verifica si existe ya una tupla para la compañía cuyo UPC sea el mismo
	 * que el UPC de esta instancia. En caso de existir setea un mensaje de error en el log
	 * indicando cual es el artículo que tiene asociado el código UPC ingresado.
	 */
	private boolean validateUniqueUPC() {
		Integer productID = getProductIDFromUPC(getCtx(), getUPC(), null, getM_ProductUPC_ID(), get_TrxName());
		if (productID != null && productID > 0) {
			MProduct product = MProduct.get(getCtx(), productID);
			String productStr = "'" + product.getValue() + " " + product.getName() + "'";
			log.saveError("SaveError", 
					Msg.translate(getCtx(), "DuplicateUPCError") + " " + productStr);
		}
		return productID == null || productID <= 0;
	}
	
	/**
	 * Busca y asigna un nuevo UPC como predeterminado para el artículo asociado
	 * a este UPC de artículo. La búsqueda se realiza teniendo en cuenta la fecha de
	 * creación del registro, se considera que el último UPC asociado al artículo es
	 * el predeterminado.
	 * @return <code>true</code> si la asociación fue satisfactoria, <code>false</code>
	 * en caso de que no se haya logrado determinar y asignar un nuevo UPC predeterminado.
	 */
	private boolean setDefaultUPC() {
		boolean success = true;
		MProductUPC productUPC = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// Busca último UPC activo según la fecha de creación, que no sea
		// esta instancia
		String sql = 
			"SELECT * FROM M_ProductUPC " +
			"WHERE M_Product_ID = ? AND IsActive = 'Y' AND M_ProductUPC_ID <> ? " +
			"ORDER BY Created DESC";
			
		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getM_Product_ID());
			pstmt.setInt(2, getM_ProductUPC_ID());
			
			rs = pstmt.executeQuery();
			// Si existe un código UPC se crea la instancia
			if (rs.next()) {
				productUPC = new MProductUPC(getCtx(), rs, get_TrxName());
			}
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "Error setting default UPC for product ID=" + getM_Product_ID(), e);
			success = false;
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		
		// Si se encontró un candidato a ser predeterminado, se actualiza el valor de
		// IsDefault y se guarda.
		if (productUPC != null) {
			productUPC.setIsDefault(true);
			success = productUPC.save();
		}
		
		return success;
	}
	
	/**
	 * @return Borra el campo UPC en el artículo asociado a este registro debido a que
	 * este registro ha sido desactivado o eliminado.
	 */
	private boolean clearProductUPCReference() {
		boolean success = true;
		MProduct product = new MProduct(getCtx(), getM_Product_ID(), get_TrxName());
		if (getUPC().equals(product.getUPC())) {
			product.setUPC(null);
			success = product.save();
		}
		return success;
	}

	@Override
	public String toString() {
        StringBuffer sb = new StringBuffer( "MProductUPC[" );
        sb.append("Product: " ).append(getM_Product_ID()).append(" - ").append(getUPC()).append("]" );
        return sb.toString();
	}

	/**
	 * @return the productUPCUpdate
	 */
	public boolean isProductUPCUpdate() {
		return productUPCUpdate;
	}

	/**
	 * @param productUPCUpdate the productUPCUpdate to set
	 */
	public void setProductUPCUpdate(boolean productUPCUpdate) {
		this.productUPCUpdate = productUPCUpdate;
	}
	
}
