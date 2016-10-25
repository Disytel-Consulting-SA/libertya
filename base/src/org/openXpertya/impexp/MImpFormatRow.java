/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */

package org.openXpertya.impexp;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.X_AD_ImpFormat_Row;

/**
 * Descripción de Clase
 * @version 2.2, 12.10.07
 * @author Equipo de Desarrollo de openXpertya
 */
public class MImpFormatRow extends X_AD_ImpFormat_Row {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de la clase ...
	 * @param ctx
	 * @param AD_ImpFormat_Row_ID
	 * @param trxName
	 */
	public MImpFormatRow(Properties ctx, int AD_ImpFormat_Row_ID, String trxName) {
		super(ctx, AD_ImpFormat_Row_ID, trxName);

		if (AD_ImpFormat_Row_ID == 0) {
			setDecimalPoint(".");
			setDivideBy100(false);
		}
	} // MImpFormatRow

	/**
	 * Constructor de la clase ...
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MImpFormatRow(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	} // MImpFormatRow

	/**
	 * Constructor de la clase ...
	 * @param format
	 */
	public MImpFormatRow(MImpFormat format) {
		this(format.getCtx(), 0, format.get_TrxName());
		setAD_ImpFormat_ID(format.getAD_ImpFormat_ID());
	} // MImpFormatRow

	/**
	 * Constructor de la clase ...
	 * @param parent
	 * @param original
	 */
	public MImpFormatRow(MImpFormat parent, MImpFormatRow original) {
		this(parent.getCtx(), 0, parent.get_TrxName());
		copyValues(original, this);
		setClientOrg(parent);
		setAD_ImpFormat_ID(parent.getAD_ImpFormat_ID());
	} // MImpFormatRow

} // MImpFormatRow

/*
 * @(#)MImpFormatRow.java 02.07.07
 * Fin del fichero MImpFormatRow.java
 * Versión 2.2
 */
