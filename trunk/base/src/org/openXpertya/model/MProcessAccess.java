/*
 * @(#)MProcessAccess.java   12.oct 2007  Versión 2.2
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
 *      Process Access Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MProcessAccess.java,v 1.5 2005/03/11 20:28:36 jjanke Exp $
 */
public class MProcessAccess extends X_AD_Process_Access {

    /**
     *      Parent Constructor
     *      @param parent parent
     *      @param AD_Role_ID role id
     */
    public MProcessAccess(MProcess parent, int AD_Role_ID) {

        super(parent.getCtx(), 0, parent.get_TrxName());
        setClientOrg(parent);
        setAD_Process_ID(parent.getAD_Process_ID());
        setAD_Role_ID(AD_Role_ID);

    }		// MProcessAccess

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param ignored ignored
     * @param trxName
     */
    public MProcessAccess(Properties ctx, int ignored, String trxName) {

        super(ctx, 0, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        } else {

            // setAD_Process_ID (0);
            // setAD_Role_ID (0);
            setIsReadWrite(true);
        }

    }		// MProcessAccess

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MProcessAccess(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MProcessAccess
}	// MProcessAccess



/*
 * @(#)MProcessAccess.java   02.jul 2007
 * 
 *  Fin del fichero MProcessAccess.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
