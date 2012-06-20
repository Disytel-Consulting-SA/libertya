/*
 * @(#)LoadRoles.java   14.jun 2007  Versión 2.2
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

import java.util.logging.*;

import java.math.*;

import java.sql.*;

import java.util.*;

import openXpertya.model.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      LoadRoles
 *
 *  @author Victor Perez, e-Evolution, S.C.
 *  @version $Id: CreateCost.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class LoadRoles extends SvrProcess {

    /**  */

    /**
     *  Prepare - e.g., get Parameters.
     */
    protected void prepare() {
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

        String	sql	= "SELECT i.AD_Role_ID, i.AD_Window_ID ,i.AD_Process_ID , i.AD_Form_ID , i.AD_Workflow_ID , i.IsReadWrite , i.IsView FROM I_Role_Access i";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                int	AD_Role_ID	= rs.getInt(1);
                int	AD_Window_ID	= rs.getInt(2);
                int	AD_Process_ID	= rs.getInt(3);
                int	AD_Form_ID	= rs.getInt(4);
                int	AD_Workflow_ID	= rs.getInt(5);

                // System.out.println ("AD_Role_ID:" + AD_Role_ID + "AD_Window_ID:" + AD_Window_ID +  "AD_Process_ID:"  + AD_Process_ID + "Ad_Form_ID:" +  AD_Form_ID);
                if (AD_Window_ID > 0) {

                    String	sqlupdate	= "UPDATE AD_Window_Access SET IsReadWrite = '" + rs.getString(6) + "', IsActive='" + rs.getString(7) + "' WHERE AD_Window_ID =" + AD_Window_ID + " AND AD_Role_ID =" + AD_Role_ID;

                    // System.out.println("SQL AD_Window" + sql);
                    DB.executeUpdate(sqlupdate);

                } else
                    if (AD_Form_ID > 0) {

                        String	sqlupdate	= "UPDATE AD_Form_Access SET IsReadWrite = '" + rs.getString(6) + "', IsActive = '" + rs.getString(7) + "' WHERE AD_Form_ID =" + AD_Form_ID + " AND AD_Role_ID =" + AD_Form_ID;

                        // System.out.println("SQL AD_Form" + sql);
                        DB.executeUpdate(sqlupdate);

                    } else
                        if (AD_Process_ID > 0) {

                            String	sqlupdate	= "UPDATE AD_Process_Access SET IsReadWrite = '" + rs.getString(6) + "', IsActive = '" + rs.getString(7) + "' WHERE AD_Process_ID =" + AD_Process_ID + " AND AD_Role_ID =" + AD_Role_ID;

                            // System.out.println("SQL AD_Process" + sql);
                            DB.executeUpdate(sqlupdate);

                        } else
                            if (AD_Workflow_ID > 0) {

                                String	sqlupdate	= "UPDATE AD_Workflow_Access SET IsReadWrite = '" + rs.getString(6) + "',  IsActive = '" + rs.getString(7) + "' WHERE AD_Workflow_ID =" + AD_Workflow_ID + " AND AD_Role_ID =" + AD_Role_ID;

                                // System.out.println("SQL AD_Process" + sql);
                                DB.executeUpdate(sqlupdate);
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
 * @(#)LoadRoles.java   14.jun 2007
 * 
 *  Fin del fichero LoadRoles.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
