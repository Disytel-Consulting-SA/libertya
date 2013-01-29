/*
 * @(#)M_Registration.java   12.oct 2007  Versión 2.2
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
 *      System Registration Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: M_Registration.java,v 1.5 2005/03/11 20:28:38 jjanke Exp $
 */
public class M_Registration extends X_AD_Registration {

    /**
     *      Default Constructor
     *      @param ctx context
     *      @param AD_Registration_ID id
     * @param trxName
     */
    public M_Registration(Properties ctx, int AD_Registration_ID, String trxName) {

        super(ctx, AD_Registration_ID, trxName);
        setAD_Client_ID(0);
        setAD_Org_ID(0);
        setAD_System_ID(0);

    }		// M_Registration

    /**
     *      Load Cosntructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public M_Registration(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// M_Registration

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true/false
     */
    protected boolean beforeSave(boolean newRecord) {

        MSystem	system	= MSystem.get(getCtx());

        if (system.getName().equals("?") || system.getUserName().equals("?")) {

            log.saveError("Error", "Define System first");

            return false;
        }

        return true;

    }		// beforeSave
}	// M_Registration



/*
 * @(#)M_Registration.java   02.jul 2007
 * 
 *  Fin del fichero M_Registration.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
