/*
 * @(#)MCurrencyAcct.java   12.oct 2007  Versión 2.2
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

import java.util.Properties;
import java.util.logging.Level;

/**
 *      Currency Account Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MCurrencyAcct.java,v 1.5 2005/03/11 20:28:38 jjanke Exp $
 */
public class MCurrencyAcct extends X_C_Currency_Acct {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MCurrencyAcct.class);

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MCurrencyAcct(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MCurrencyAcct

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Currency Account for Currency
     *      @param as accounting schema default
     *      @param C_Currency_ID currency
     *      @return Currency Account or null
     */
    public static MCurrencyAcct get(MAcctSchemaDefault as, int C_Currency_ID) {

        MCurrencyAcct	retValue	= null;
        String		sql		= "SELECT * FROM C_Currency_Acct " + "WHERE C_AcctSchema_ID=? AND C_Currency_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, as.getC_AcctSchema_ID());
            pstmt.setInt(2, C_Currency_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MCurrencyAcct(as.getCtx(), rs, null);
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

        return retValue;

    }		// get
}	// MCurrencyAcct



/*
 * @(#)MCurrencyAcct.java   02.jul 2007
 * 
 *  Fin del fichero MCurrencyAcct.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
