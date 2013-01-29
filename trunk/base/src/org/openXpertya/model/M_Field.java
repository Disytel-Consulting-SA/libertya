/*
 * @(#)M_Field.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */

package org.openXpertya.model;

import java.sql.ResultSet;

import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.util.DB;

/**
 * Field Model
 * 
 * @author Comunidad de Desarrollo openXpertya *Basado en Codigo Original
 *         Modificado, Revisado y Optimizado de: * Jorg Janke
 * @version $Id: M_Field.java,v 1.6 2005/05/01 07:04:16 jjanke Exp $
 */
public class M_Field extends X_AD_Field {

	/**
	 * Parent Constructor
	 * 
	 * @param parent
	 *            parent
	 */
	public M_Field(M_Tab parent) {

		this(parent.getCtx(), 0, parent.get_TrxName());
		setClientOrg(parent);
		setAD_Tab_ID(parent.getAD_Tab_ID());

	} // M_Field

	/**
	 * Copy Constructor
	 * 
	 * @param parent
	 *            parent
	 * @parem from copy from
	 * @param from
	 */
	public M_Field(M_Tab parent, M_Field from) {

		this(parent.getCtx(), 0, parent.get_TrxName());
		copyValues(from, this);
		setClientOrg(parent);
		setAD_Tab_ID(parent.getAD_Tab_ID());
		setEntityType(parent.getEntityType());

	} // M_Field

	/**
	 * Standard Constructor
	 * 
	 * @param ctx
	 *            context
	 * @param AD_Field_ID
	 *            id
	 * @param trxName
	 */
	public M_Field(Properties ctx, int AD_Field_ID, String trxName) {

		super(ctx, AD_Field_ID, trxName);

		if (AD_Field_ID == 0) {

			// setAD_Tab_ID (0); // parent
			// setAD_Column_ID (0);
			// setName (null);
			setEntityType(ENTITYTYPE_UserMaintained); // U
			setIsCentrallyMaintained(true); // Y
			setIsDisplayed(true); // Y
			setisdisplayedingrid(true); // Y
			setIsEncrypted(false);
			setIsFieldOnly(false);
			setIsHeading(false);
			setIsReadOnly(false);
			setIsSameLine(false);

			// setObscureType(OBSCURETYPE_ObscureDigitsButLast4);
		}

	} // M_Field

	/**
	 * Load Constructor
	 * 
	 * @param ctx
	 *            context
	 * @param rs
	 *            result set
	 * @param trxName
	 */
	public M_Field(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	} // M_Field

	// ~--- set methods --------------------------------------------------------

	/**
	 * Set Column Values
	 * 
	 * @param column
	 *            column
	 */
	public void setColumn(M_Column column) {

		setAD_Column_ID(column.getAD_Column_ID());
		setName(column.getName());

		/*
		 * if (column.getName() == "Oficina") {
		 * JOptionPane.showMessageDialog(null, "En M_Fiel.java, colum name= " +
		 * column.getName() + "Logitud= " + column.getFieldLength(), "..Fin",
		 * JOptionPane.INFORMATION_MESSAGE); }
		 */

		setDescription(column.getDescription());
		setHelp(column.getHelp());
		setDisplayLength(column.getFieldLength());
		setEntityType(column.getEntityType());

	} // setColumn
} // M_Field

/*
 * @(#)M_Field.java 02.jul 2007
 * 
 * Fin del fichero M_Field.java
 * 
 * Versión 2.2 - Fundesle (2007)
 * 
 */

// ~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
