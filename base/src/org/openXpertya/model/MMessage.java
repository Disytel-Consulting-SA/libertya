/*
 * @(#)MMessage.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Properties;
import java.util.logging.Level;

/**
 *      Message Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MMessage.java,v 1.6 2005/03/11 20:28:35 jjanke Exp $
 */
public class MMessage extends X_AD_Message {

    /** Cache */
    static private CCache	s_cache	= new CCache("AD_Message", 100);

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MMessage.class);

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Message_ID id
     * @param trxName
     */
    public MMessage(Properties ctx, int AD_Message_ID, String trxName) {
        super(ctx, AD_Message_ID, trxName);
    }		// MMessage

    /**
     *      Load Cosntructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MMessage(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MMessage

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Message (cached)
     *      @param ctx context
     *      @param AD_Message_ID id
     *
     * @return
     */
    public static MMessage get(Properties ctx, int AD_Message_ID) {

        String		key		= String.valueOf(AD_Message_ID);
        MMessage	retValue	= (MMessage) s_cache.get(key);

        if (retValue == null) {

            retValue	= new MMessage(ctx, AD_Message_ID, null);
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    /**
     *      Get Message (cached)
     *      @param ctx context
     *      @param Value message value
     *
     * @return
     */
    public static MMessage get(Properties ctx, String Value) {

        if ((Value == null) || (Value.length() == 0)) {
            return null;
        }

        MMessage	retValue	= (MMessage) s_cache.get(Value);

        //
        if (retValue == null) {

            String		sql	= "SELECT * FROM AD_Message WHERE Value=?";
            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql);
                pstmt.setString(1, Value);

                ResultSet	rs	= pstmt.executeQuery();

                if (rs.next()) {
                    retValue	= new MMessage(ctx, rs, null);
                }

                rs.close();
                pstmt.close();
                pstmt	= null;

            } catch (Exception e) {
                s_log.log(Level.SEVERE, "get", e);
            }

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

                pstmt	= null;

            } catch (Exception e) {
                pstmt	= null;
            }

            if (retValue != null) {
                s_cache.put(Value, retValue);
            }
        }

        return retValue;

    }		// get

    /**
     *      Get Message ID (cached)
     *      @param ctx context
     *      @param Value message value
     *      @return AD_Message_ID
     */
    public static int getAD_Message_ID(Properties ctx, String Value) {

        MMessage	msg	= get(ctx, Value);

        if (msg == null) {
            return 0;
        }

        return msg.getAD_Message_ID();

    }		// getAD_Message_ID
}	// MMessage



/*
 * @(#)MMessage.java   02.jul 2007
 * 
 *  Fin del fichero MMessage.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
