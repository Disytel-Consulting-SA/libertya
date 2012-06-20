package org.openXpertya.pos.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.MDiscountSchemaBreak;

/**
 * Corte de Esquema de Descuento. Esta clase es similar a la clase
 * {@link DiscountSchema} en cuanto a que es una clase desconectada de la Base
 * de Datos. En caso de invocar las operaciones de PO que acceden a la BD se
 * disparará una excepción.
 * 
 * Para instanciar esta clase se debe utilizar el constructor
 * {@link #DiscountSchemaBreak(Properties, ResultSet)} el cual obliga a que la
 * instanciación se haga en un contexto Online debido al ResultSet. De todos
 * modos este constructor no debería ser utilizado por otro cliente mas que la
 * clase {@link DiscountSchema}, la cual en su instanciación obtiene de la BD
 * todos sus cortes e instancia repetidas veces esta clase usando dicho
 * constructor.
 */
public class DiscountSchemaBreak extends MDiscountSchemaBreak {

	/** Mensaje que contiene la excepción lanzada al ejecutar alguno de los
	 * métodos de PO para acceso a BD */
	private static final String INVALID_STATE_MSG = "This object is disconnected from de database";

	/**
	 * Constructor de Corte. Requiere si o si un modo Online debido al ResultSet
	 * 
	 * @param ctx
	 *            Contexto de la aplicación
	 * @param rs
	 *            ResultSet posicionado en el registro a cargar
	 */
	protected DiscountSchemaBreak(Properties ctx, ResultSet rs) {
		// Transacción null ya que es un objeto desconectado.
		super(ctx, rs, null);
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
