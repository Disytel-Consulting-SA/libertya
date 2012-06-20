package org.openXpertya.pos.model;

import java.sql.ResultSet;

import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.model.MDiscountSchemaBreak;
import org.openXpertya.pos.ctrl.PoSOnline;

/**
 * <p>
 * Esquema de Descuento. Esta es una subclase de {@link MDiscountSchema} a fin
 * de heredar la lógica de cálculo de descuentos simples y por cortes. Aún así,
 * es una clase desconectada de la Base de Datos debido a que respeta el modelo
 * Online/Offline del TPV. En caso de invocar las operaciones de PO que acceden
 * a la BD se disparará una excepción.
 * </p>
 * 
 * <p>
 * Para crear una instancia de esta clase se debe utilizar el constructor
 * {@link #DiscountSchema(int, PoSOnline)}, el cual obliga a que el único acceso
 * a la BD para obtener todo el esquema se haga en un contexto de
 * {@link PoSOnline} con lo cual se debe pasar la referencia a este objeto para
 * poder crear la instancia. En caso de que el
 * <code>M_DiscountSchema_ID = 0</code> se levanta una excepción ya que no es
 * posible crear nuevos esquemas de descuento mediante esta clase (solo leerlos
 * y utilizar su lógica de aplicación).
 * </p>
 * 
 * <p>
 * Es responsabilidad de esta clase y sus subclases redefinir los métodos de
 * {@link MDiscountSchema} que sean utilizados para la obtención del descuento
 * final y que accedan a la BD mediante cualquier medio (PO o SQL) a fin de que
 * esta clase realmente pueda ser utilizada Offline tal como lo requiere el TPV.
 * </p>
 */
public class DiscountSchema extends MDiscountSchema {
	
	/** Mensaje que contiene la excepción lanzada al ejecutar alguno de los
	 * métodos de PO para acceso a BD */
	private static final String INVALID_STATE_MSG = "This object is disconnected from de database";
	
	/**
	 * Crea una instancia a partir de un Esquema de Descuento existente en la
	 * Base de Datos. Esta es la única operación que realiza un acceso a la Base
	 * de Datos y por eso se requiere un {@link PoSOnline} como parámetro,
	 * indicando que el contexto de creación es justamente el modo Online de
	 * TPV.
	 * 
	 * @param M_DiscountSchema_ID
	 *            ID del esquema a recuperar de la BD
	 * @param posOnline
	 *            Estado de conexión Online del TPV que está creando este
	 *            objeto.
	 * @throws IllegalArgumentException
	 *             cuando el <code>M_DiscountSchema_ID</code> es igual a 0, ya
	 *             que no es posible crear esquemas de descuentos mediante esta
	 *             clase.
	 */
	public DiscountSchema(int M_DiscountSchema_ID, PoSOnline posOnline) {
		// La transacción es null porque este es un objeto desconectado de la BD
		super(posOnline.getCtx(), M_DiscountSchema_ID, null);
		// No se permiten crear nuevos registros de esta clase.
		if (M_DiscountSchema_ID == 0) {
			throw new IllegalArgumentException("Could not create new records of this type");
		}
		// Se cargan los Cortes asociados a este esquema de descuento. Esto
		// se realiza aquí ya que es el único método que dispone de
		// conexión a la Base de Datos
		super.getBreaks(true);
	}
	
	/* (non-Javadoc)
	 * @see org.openXpertya.model.MDiscountSchema#createBreak(java.sql.ResultSet)
	 */
	@Override
	protected MDiscountSchemaBreak createBreak(ResultSet rs) {
		// Instancia la subclase desconectada de MDiscountSchemaBreak
		return new DiscountSchemaBreak(getCtx(), rs);
	}
	
	/* (non-Javadoc)
	 * @see org.openXpertya.model.PO#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object cmp) {
		boolean equals = false;
		if (cmp != null && cmp instanceof DiscountSchema) {
			equals = getM_DiscountSchema_ID() == ((DiscountSchema) cmp)
					.getM_DiscountSchema_ID();
		}
		return equals;
	}
	
	/* **************************************************************
	 * Anulación de métodos Delete, Save 
	 * **************************************************************/

	/* (non-Javadoc)
	 * @see org.openXpertya.model.PO#delete(boolean, java.lang.String)
	 */
	@Override
	public boolean delete(boolean force, String trxName) {
		throw new IllegalStateException(INVALID_STATE_MSG);
	}

	/* (non-Javadoc)
	 * @see org.openXpertya.model.PO#delete(boolean)
	 */
	@Override
	public boolean delete(boolean force) {
		throw new IllegalStateException(INVALID_STATE_MSG);
	}

	/* (non-Javadoc)
	 * @see org.openXpertya.model.PO#save()
	 */
	@Override
	public boolean save() {
		throw new IllegalStateException(INVALID_STATE_MSG);
	}

	/* (non-Javadoc)
	 * @see org.openXpertya.model.PO#save(java.lang.String)
	 */
	@Override
	public boolean save(String trxName) {
		throw new IllegalStateException(INVALID_STATE_MSG);
	}

}
