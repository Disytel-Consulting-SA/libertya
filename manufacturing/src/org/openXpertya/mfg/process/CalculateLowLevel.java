/*
 * @(#)CalculateLowLevel.java   14.jun 2007  Versión 2.2
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



package org.openXpertya.mfg.process;

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import openXpertya.model.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      CalculateLowLevel for MRP
 *
 *  @author Victor Perez, e-Evolution, S.C.
 *  @version $Id: CalculateLowLevel.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class CalculateLowLevel extends SvrProcess {

    /**  */
    private int	AD_Client_ID	= 0;

    /**
     *  Prepare - e.g., get Parameters.
     */
    protected void prepare() {

        AD_Client_ID	= Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));

        ProcessInfoParameter[]	para	= getParameter();

    }		// prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */
    protected String doIt() throws Exception {

        String	sql	= "SELECT p.M_Product_ID FROM M_Product p WHERE AD_Client_ID = " + AD_Client_ID;
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                int	m_M_Product_ID	= rs.getInt(1);

                if (m_M_Product_ID != 0) {

                    MProduct	product	= new MProduct(getCtx(), m_M_Product_ID, null);
                    int	lowlevel	= MMPCProductBOMLine.getlowLevel(m_M_Product_ID);

                    // System.out.println("Low Level" + lowlevel);
                    product.setLowLevel(lowlevel);
                    product.save(get_TrxName());
                }
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            log.log(Level.SEVERE, "doIt - " + sql, e);
        }

        return "ok";
    }
}



/*
 * @(#)CalculateLowLevel.java   14.jun 2007
 * 
 *  Fin del fichero CalculateLowLevel.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
