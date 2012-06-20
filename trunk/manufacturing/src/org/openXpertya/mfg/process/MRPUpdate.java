/*
 * @(#)MRPUpdate.java   14.jun 2007  Versión 2.2
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

import java.sql.*;

import openXpertya.model.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      MRPUpdate
 *
 *  @author Victor Pï¿½rez, e-Evolution, S.C.
 *  @version $Id: CreateCost.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class MRPUpdate extends SvrProcess {

    /**  */

    /**  */

    /*
     * private int             p_AD_Org_ID = 0;
     * //private int               p_M_Warehouse_ID = 0;
     * private int               p_S_Resource_ID = 0 ;
     * /
     * private String             p_Version = "1";
     */
    private int	AD_Client_ID	= 0;

    /**
     *  Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        AD_Client_ID	= getAD_Client_ID();
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

        deleteMRP();
        update();

        return "";
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean deleteMRP() {

        System.out.println("begin deleteMRP()");

        // String sql = "DELETE FROM MPC_MRP mrp WHERE mrp.TypeMRP = 'MOP' AND EXISTS(SELECT MPC_Order_ID FROM MPC_Order o WHERE o.MPC_Order_ID = mrp.MPC_Order_ID AND o.DocStatus IN ('NA','CL')) AND mrp.AD_Client_ID = " + AD_Client_ID;
        String	sql	= "DELETE MPC_MRP mrp WHERE mrp.TypeMRP = 'MOP' AND EXISTS(SELECT MPC_Order_ID FROM MPC_Order o WHERE o.MPC_Order_ID = mrp.MPC_Order_ID AND o.DocStatus IN ('NA','CL')) AND mrp.AD_Client_ID = " + AD_Client_ID;

        DB.executeUpdate(sql);
        sql	= "DELETE FROM MPC_MRP mrp WHERE mrp.TypeMRP = 'FCT' AND mrp.AD_Client_ID = " + AD_Client_ID;
        DB.executeUpdate(sql);
        sql	= "DELETE FROM MPC_MRP mrp WHERE mrp.TypeMRP = 'POR' AND EXISTS(SELECT M_Requisition_ID FROM M_Requisition r WHERE r.M_Requisition_ID = mrp.M_Requisition_ID AND (r.DocStatus='DR' AND r.DocStatus='CL')) AND mrp.AD_Client_ID = " + AD_Client_ID;
        DB.executeUpdate(sql);
        sql	= "DELETE FROM AD_Note n WHERE AD_Table_ID =  " + MMPCMRP.Table_ID + " AND AD_Client_ID = " + AD_Client_ID;
        DB.executeUpdate(sql);
        sql	= "SELECT o.MPC_Order_ID FROM MPC_Order o WHERE o.DocStatus = 'NA' AND o.AD_Client_ID = " + AD_Client_ID;

        try {

            PreparedStatement	pstmt	= null;

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setInt(1, p_M_Warehouse_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MMPCOrder	order	= new MMPCOrder(getCtx(), rs.getInt(1), null);

                order.delete(true);
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {

            log.log(Level.SEVERE, "doIt - " + sql, e);

            return false;
        }

        sql	= "SELECT r.M_Requisition_ID FROM M_Requisition r WHERE  r.DocStatus = 'DR' AND r.AD_Client_ID = " + AD_Client_ID;

        try {

            PreparedStatement	pstmt	= null;

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setInt(1, p_M_Warehouse_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MRequisition	r	= new MRequisition(getCtx(), rs.getInt(1), null);

                r.delete(true);
            }

            rs.close();
            pstmt.close();
            System.out.println("end deleteMRP()");

            return true;

        } catch (Exception e) {

            log.log(Level.SEVERE, "doIt - " + sql, e);

            return false;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean update() {

        // Get Forcast
        String	sql	= "SELECT fl.M_FORECASTLINE_ID  FROM M_FORECASTLINE fl WHERE fl.Qty > 0  AND fl.AD_Client_ID = " + AD_Client_ID;
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setInt(1, p_M_Warehouse_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MForecastLine	fl	= new MForecastLine(Env.getCtx(), rs.getInt(1), null);

                MMPCMRP.M_ForecastLine(fl, null, false);
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            log.log(Level.SEVERE, "doIt - " + sql, e);
        }

        // Get scheduled work order receipts
        sql	= "SELECT o.MPC_Order_ID FROM MPC_Order o WHERE  (o.QtyOrdered - o.QtyDelivered) > 0 AND o.DocStatus IN ('IP','CO') AND o.AD_Client_ID = " + AD_Client_ID;

        try {

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setInt(1, p_M_Warehouse_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MMPCOrder	o	= new MMPCOrder(Env.getCtx(), rs.getInt(1), null);

                MMPCMRP.MPC_Order(o, null);
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            log.log(Level.SEVERE, "doIt - " + sql, e);
        }

        // Get sales order requirements and Get scheduled purchase order receipts
        sql	= "SELECT ol.C_OrderLine_ID FROM C_OrderLine ol INNER JOIN C_Order o ON (o.C_Order_ID = ol.C_Order_ID) WHERE (ol.QtyOrdered - ol.QtyDelivered) > 0 AND o.DocStatus IN ('IP','CO') AND ol.AD_Client_ID = " + AD_Client_ID;
        pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setInt(1, p_M_Warehouse_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MOrderLine	ol	= new MOrderLine(Env.getCtx(), rs.getInt(1), "C_OrderLine");

                MMPCMRP.C_OrderLine(ol, null, false);
            }

            rs.close();
            pstmt.close();

            // return true;

        } catch (Exception e) {

            log.log(Level.SEVERE, "doIt - " + sql, e);

            // return false;
        }

        // Get sales order requirements and Get scheduled purchase order receipts
        sql	= "SELECT rl.M_RequisitionLine_ID  FROM M_RequisitionLine rl INNER JOIN M_Requisition r ON (r.M_Requisition_ID = rl.M_Requisition_ID) WHERE rl.Qty > 0 AND r.DocStatus <>'CL' AND rl.AD_Client_ID = " + AD_Client_ID;
        pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setInt(1, p_M_Warehouse_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MRequisitionLine	rl	= new MRequisitionLine(Env.getCtx(), rs.getInt(1), null);

                MMPCMRP.M_RequisitionLine(rl, null, false);
            }

            rs.close();
            pstmt.close();

            return true;

        } catch (Exception e) {

            log.log(Level.SEVERE, "doIt - " + sql, e);

            return false;
        }
    }
}



/*
 * @(#)MRPUpdate.java   14.jun 2007
 * 
 *  Fin del fichero MRPUpdate.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
