/*
 * @(#)MElement.java   12.oct 2007  Versión 2.2
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

/**
 *      Accounting Element Model.
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MElement.java,v 1.6 2005/03/11 20:28:38 jjanke Exp $
 */
public class MElement extends X_C_Element {

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param C_Element_ID id
     * @param trxName
     */
    public MElement(Properties ctx, int C_Element_ID, String trxName) {

        super(ctx, C_Element_ID, trxName);

        if (C_Element_ID == 0) {

            // setName (null);
            // setAD_Tree_ID (0);
            // setElementType (null);  // A
            setIsBalancing(false);
            setIsNaturalAccount(false);
        }

    }		// MElement

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MElement(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MElement

    /**
     *      Full Constructor
     *      @param client client
     *      @param Name name
     *      @param ElementType type
     *      @param AD_Tree_ID tree
     */
    public MElement(MClient client, String Name, String ElementType, int AD_Tree_ID) {

        this(client.getCtx(), 0, client.get_TrxName());
        setClientOrg(client);
        setName(Name);
        setElementType(ElementType);	// A
        setAD_Tree_ID(AD_Tree_ID);
        setIsNaturalAccount(ELEMENTTYPE_Account.equals(ElementType));

    }					// MElement

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true
     */
    protected boolean beforeSave(boolean newRecord) {

        if (ELEMENTTYPE_UserDefined.equals(getElementType()) && isNaturalAccount()) {
            setIsNaturalAccount(false);
        }

        return true;

    }		// beforeSave
}	// MElement



/*
 * @(#)MElement.java   02.jul 2007
 * 
 *  Fin del fichero MElement.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
