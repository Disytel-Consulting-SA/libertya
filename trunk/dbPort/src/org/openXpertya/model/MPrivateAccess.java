/*
 * @(#)MPrivateAccess.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Private Access
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MPrivateAccess.java,v 1.8 2005/03/11 20:28:33 jjanke Exp $
 */
public class MPrivateAccess extends X_AD_Private_Access {

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MPrivateAccess.class);

    /**
     *      Persistency Constructor
     *      @param ctx context
     *      @param ignored ignored
     * @param trxName
     */
    public MPrivateAccess(Properties ctx, int ignored, String trxName) {

        super(ctx, 0, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

    }		// MPrivateAccess

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MPrivateAccess(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MPrivateAccess

    /**
     *      New Constructor
     *      @param ctx context
     *      @param AD_User_ID user
     *      @param AD_Table_ID table
     *      @param Record_ID record
     */
    public MPrivateAccess(Properties ctx, int AD_User_ID, int AD_Table_ID, int Record_ID) {

        super(ctx, 0, null);
        setAD_User_ID(AD_User_ID);
        setAD_Table_ID(AD_Table_ID);
        setRecord_ID(Record_ID);

    }		// MPrivateAccess

    //~--- get methods --------------------------------------------------------

    /**
     *      Load Pricate Access
     *      @param ctx context
     *      @param AD_User_ID user
     *      @param AD_Table_ID table
     *      @param Record_ID record
     *      @return access or null if not found
     */
    public static MPrivateAccess get(Properties ctx, int AD_User_ID, int AD_Table_ID, int Record_ID) {

        MPrivateAccess		retValue	= null;
        PreparedStatement	pstmt		= null;
        String			sql		= "SELECT * FROM AD_Private_Access WHERE AD_User_ID=? AND AD_Table_ID=? AND Record_ID=?";

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_User_ID);
            pstmt.setInt(2, AD_Table_ID);
            pstmt.setInt(3, Record_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MPrivateAccess(ctx, rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "MPrivateAccess", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return retValue;

    }		// get

    /**
     *      Get Where Clause of Locked Records for Table
     *      @param AD_Table_ID table
     *      @param AD_User_ID user requesting info
     *      @return "<>1" or " NOT IN (1,2)" or null
     */
    public static String getLockedRecordWhere(int AD_Table_ID, int AD_User_ID) {

        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;
        String			sql	= "SELECT Record_ID FROM AD_Private_Access WHERE AD_Table_ID=? AND AD_User_ID<>? AND IsActive='Y'";

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Table_ID);
            pstmt.setInt(2, AD_User_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Integer(rs.getInt(1)));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "MPrivateAccess", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        //
        if (list.size() == 0) {
            return null;
        }

        if (list.size() == 1) {
            return "<>" + list.get(0);
        }

        //
        StringBuffer	sb	= new StringBuffer(" NOT IN(");

        for (int i = 0; i < list.size(); i++) {

            if (i > 0) {
                sb.append(",");
            }

            sb.append(list.get(i));
        }

        sb.append(")");

        return sb.toString();

    }		// get
}	// MPrivateAccess



/*
 * @(#)MPrivateAccess.java   02.jul 2007
 * 
 *  Fin del fichero MPrivateAccess.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
