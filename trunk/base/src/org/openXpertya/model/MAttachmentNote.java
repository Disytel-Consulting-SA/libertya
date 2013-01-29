/*
 * @(#)MAttachmentNote.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.ResultSet;

import java.util.Properties;

/**
 *      Attachment Note
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MAttachmentNote.java,v 1.4 2005/03/11 20:28:32 jjanke Exp $
 */
public class MAttachmentNote extends X_AD_AttachmentNote {

    /**
     *      Parent Constructor.
     *      Sets current user.
     *      @param attach attachment
     * @param Title
     * @param TextMsg
     */
    public MAttachmentNote(MAttachment attach, String Title, String TextMsg) {

        this(attach.getCtx(), 0, attach.get_TrxName());
        setClientOrg(attach);
        setAD_Attachment_ID(attach.getAD_Attachment_ID());
        setAD_User_ID(Env.getAD_User_ID(attach.getCtx()));
        setTitle(Title);
        setTextMsg(TextMsg);

    }		// MAttachmentNote

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_AttachmentNote_ID id
     * @param trxName
     */
    public MAttachmentNote(Properties ctx, int AD_AttachmentNote_ID, String trxName) {

        super(ctx, AD_AttachmentNote_ID, trxName);

        /**
         * if (AD_AttachmentNote_ID == 0)
         * {
         *       setAD_Attachment_ID (0);
         *       setAD_User_ID (0);
         *       setTextMsg (null);
         *       setTitle (null);
         * }
         * /*
         */

    }		// MAttachmentNote

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MAttachmentNote(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MAttachmentNote
}	// MAttachmentNote



/*
 * @(#)MAttachmentNote.java   02.jul 2007
 * 
 *  Fin del fichero MAttachmentNote.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
