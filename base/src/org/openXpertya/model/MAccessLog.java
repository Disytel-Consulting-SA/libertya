/*
 * @(#)MAccessLog.java   12.oct 2007  Versión 2.2
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
 *      Access Log Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MAccessLog.java,v 1.6 2005/03/11 20:28:36 jjanke Exp $
 */
public class MAccessLog extends X_AD_AccessLog {

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_AccessLog_ID id
     * @param trxName
     */
    public MAccessLog(Properties ctx, int AD_AccessLog_ID, String trxName) {
        super(ctx, AD_AccessLog_ID, trxName);
    }		// MAccessLog

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MAccessLog(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MAccessLog

    /**
     *      Discontinue
     *
     * @param ctx
     * @param Remote_Host
     * @param Remote_Addr
     * @param TextMsg
     */
    public MAccessLog(Properties ctx, String Remote_Host, String Remote_Addr, String TextMsg) {
        this(ctx, Remote_Host, Remote_Addr, TextMsg, null);
    }		// MAccessLog

    /**
     *      New Constructor
     *      @param ctx context
     * @param AD_Table_ID
     * @param AD_Column_ID
     * @param Record_ID
     * @param trxName
     */
    public MAccessLog(Properties ctx, int AD_Table_ID, int AD_Column_ID, int Record_ID, String trxName) {

        this(ctx, 0, trxName);
        setAD_Table_ID(AD_Table_ID);
        setAD_Column_ID(AD_Column_ID);
        setRecord_ID(Record_ID);

    }		// MAccessLog

    /**
     *      New Constructor
     *      @param ctx context
     * @param Remote_Host
     * @param Remote_Addr
     * @param TextMsg
     * @param trxName
     */
    public MAccessLog(Properties ctx, String Remote_Host, String Remote_Addr, String TextMsg, String trxName) {

        this(ctx, 0, trxName);
        setRemote_Addr(Remote_Addr);
        setRemote_Host(Remote_Host);
        setTextMsg(TextMsg);

    }		// MAccessLog
}	// MAccessLog



/*
 * @(#)MAccessLog.java   02.jul 2007
 * 
 *  Fin del fichero MAccessLog.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
