/*
 * @(#)ModelValidator.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.reflection.CallResult;

/**
 *      Model Validator
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: ModelValidator.java,v 1.3 2005/03/11 20:28:38 jjanke Exp $
 */
public interface ModelValidator {

    /** Model Change Type New */
    public static final int	TYPE_NEW	= 1;

    /** Model Change Type Delete */
    public static final int	TYPE_DELETE	= 3;

    /** Model Change Type Change */
    public static final int	TYPE_CHANGE	= 2;

    /** Called before document is prepared */
    public static final int	TIMING_BEFORE_PREPARE	= 1;

    /** Called after document is processed */
    public static final int	TIMING_AFTER_COMPLETE	= 9;

    /**
     *      Validate Document.
     *      Called as first step of DocAction.prepareIt
     *      or at the end of DocAction.completeIt
     *  when you called addDocValidate for the table.
     *  Note that totals, etc. may not be correct before the prepare stage.
     *      @param po persistent object
     *      @param timing see TIMING_ constants
     *  @return error message or null -
     *  if not null, the pocument will be marked as Invalid.
     */
    public String docValidate(PO po, int timing);

    /**
     *      Initialize Validation
     *      @param engine validation engine
     *      @param client client
     */
    public void initialize(ModelValidationEngine engine, MClient client);

    /**
     *      User logged in
     *      Called before preferences are set
     *      @param AD_Org_ID org
     *      @param AD_Role_ID role
     *      @param AD_User_ID user
     *      @return error message or null
     */
    public CallResult login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID);

    /**
     *  Model Change of a monitored Table.
     *  Called after PO.beforeSave/PO.beforeDelete
     *  when you called addModelChange for the table
     *  @param po persistent object
     *  @param type TYPE_
     *  @return error message or null
     *  @exception Exception if the recipient wishes the change to be not accept.
     */
    public String modelChange(PO po, int type) throws Exception;

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Client to be monitored
     *      @return AD_Client_ID
     */
    public int getAD_Client_ID();
}	// ModelValidator



/*
 * @(#)ModelValidator.java   02.jul 2007
 * 
 *  Fin del fichero ModelValidator.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
