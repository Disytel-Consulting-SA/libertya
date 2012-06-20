/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MRequisition;
import org.openXpertya.model.MRequisitionLine;
import org.openXpertya.model.X_T_Replenish;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ReplenishReport extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_Warehouse_ID = 0;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private String p_ReplenishmentCreate = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "M_Warehouse_ID" )) {
                p_M_Warehouse_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "ReplenishmentCreate" )) {
                p_ReplenishmentCreate = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "doIt - M_Warehouse_ID=" + p_M_Warehouse_ID + ", C_BPartner_ID=" + p_C_BPartner_ID + " - ReplenishmentCreate=" + p_ReplenishmentCreate );
        prepareTable();
        fillTable();

        //

        if( p_ReplenishmentCreate == null ) {
            return "OK";
        }

        if( p_ReplenishmentCreate.equals( "P" )) {
            createPO();
        } else if( p_ReplenishmentCreate.equals( "R" )) {
            createRequisition();
        }

        return "";
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    private void prepareTable() {

        // Level_Max must be >= Level_Max

        String sql = "UPDATE M_Replenish" + " SET Level_Max = Level_Min " + "WHERE Level_Max < Level_Min";
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no != 0 ) {
            log.fine( "prepareTable - Corrected Max_Level=" + no );
        }

        // Minimum Order should be 1

        sql = "UPDATE M_Product_PO" + " SET Order_Min = 1 " + "WHERE Order_Min IS NULL OR Order_Min < 1";
        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 0 ) {
            log.fine( "prepareTable - Corrected Order Min=" + no );
        }

        // Pack should be 1

        sql = "UPDATE M_Product_PO" + " SET Order_Pack = 1 " + "WHERE Order_Pack IS NULL OR Order_Pack < 1";
        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 0 ) {
            log.fine( "prepareTable - Corrected Order Pack=" + no );
        }

        // Set Current Vendor where only one vendor

        sql = "UPDATE M_Product_PO p" + " SET IsCurrentVendor='Y' " + "WHERE IsCurrentVendor<>'Y'" + " AND EXISTS (SELECT mj.M_Product_ID FROM M_Product_PO mj " + "WHERE p.M_Product_ID=mj.M_Product_ID " + "GROUP BY mj.M_Product_ID " + "HAVING COUNT(*) = 1)";
        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 0 ) {
            log.fine( "prepareTable - Corrected CurrentVendor(0)=" + no );
        }

        // More then one current vendor

        sql = "UPDATE M_Product_PO p" + " SET IsCurrentVendor='N' " + "WHERE IsCurrentVendor = 'Y'" + " AND EXISTS (SELECT mj.M_Product_ID FROM M_Product_PO mj " + "WHERE p.M_Product_ID=mj.M_Product_ID AND mj.IsCurrentVendor='Y' " + "GROUP BY mj.M_Product_ID " + "HAVING COUNT(*) > 1)";
        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 0 ) {
            log.fine( "prepareTable - Corrected CurrentVendor(0)=" + no );
        }

        // Just to be sure

        sql = "DELETE T_Replenish WHERE AD_PInstance_ID=" + getAD_PInstance_ID();
        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 0 ) {
            log.fine( "prepareTable - Delete Existing=" + no );
        }
    }    // prepareTable

    /**
     * Descripción de Método
     *
     */

    private void fillTable() {
        String sql = "INSERT INTO T_Replenish " + "(AD_PInstance_ID, M_Warehouse_ID, M_Product_ID, AD_Client_ID, AD_Org_ID," + " ReplenishType, Level_Min, Level_Max," + " C_BPartner_ID, Order_Min, Order_Pack, QtyToOrder, ReplenishmentCreate) " + "SELECT " + getAD_PInstance_ID() + ", r.M_Warehouse_ID, r.M_Product_ID, r.AD_Client_ID, r.AD_Org_ID," + " r.ReplenishType, r.Level_Min, r.Level_Max," + " po.C_BPartner_ID, po.Order_Min, po.Order_Pack, 0, ";

        if( p_ReplenishmentCreate == null ) {
            sql += "null";
        } else {
            sql += "'" + p_ReplenishmentCreate + "'";
        }

        sql += " FROM M_Replenish r" + " INNER JOIN M_Product_PO po ON (r.M_Product_ID=po.M_Product_ID) " + "WHERE po.IsCurrentVendor='Y'"    // Only Current Vendor
               + " AND r.ReplenishType<>'0'" + " AND po.IsActive='Y' AND r.IsActive='Y'" + " AND r.M_Warehouse_ID=" + p_M_Warehouse_ID;

        if( p_C_BPartner_ID != 0 ) {
            sql += " AND po.C_BPartner_ID=" + p_C_BPartner_ID;
        }

        int no = DB.executeUpdate( sql,get_TrxName());

        log.fine( "fillTable - " + sql );
        log.fine( "fillTable - Insert Replenish Records=" + no );

        if( p_C_BPartner_ID == 0 ) {
            sql = "INSERT INTO T_Replenish " + "(AD_PInstance_ID, M_Warehouse_ID, M_Product_ID, AD_Client_ID, AD_Org_ID," + " ReplenishType, Level_Min, Level_Max," + " C_BPartner_ID, Order_Min, Order_Pack, QtyToOrder, ReplenishmentCreate) " + "SELECT " + getAD_PInstance_ID() + ", r.M_Warehouse_ID, r.M_Product_ID, r.AD_Client_ID, r.AD_Org_ID," + " r.ReplenishType, r.Level_Min, r.Level_Max," + " null, 1, 1, 0, ";

            if( p_ReplenishmentCreate == null ) {
                sql += "null";
            } else {
                sql += "'" + p_ReplenishmentCreate + "'";
            }

            sql += " FROM M_Replenish r " + "WHERE r.ReplenishType<>'0' AND r.IsActive='Y'" + " AND r.M_Warehouse_ID=" + p_M_Warehouse_ID + " AND NOT EXISTS (SELECT * FROM T_Replenish t " + "WHERE r.M_Product_ID=t.M_Product_ID" + " AND AD_PInstance_ID=" + getAD_PInstance_ID() + ")";
            no = DB.executeUpdate( sql,get_TrxName());
            log.fine( "fillTable - Insert Replenish Records=" + no );
        }

        sql = "UPDATE T_Replenish t SET " + "QtyOnHand = (SELECT SUM(QtyOnHand) FROM M_Storage s, M_Locator l WHERE t.M_Product_ID=s.M_Product_ID" + " AND l.M_Locator_ID=s.M_Locator_ID AND l.M_Warehouse_ID=t.M_Warehouse_ID)," + "QtyReserved = (SELECT SUM(QtyReserved) FROM M_Storage s, M_Locator l WHERE t.M_Product_ID=s.M_Product_ID" + " AND l.M_Locator_ID=s.M_Locator_ID AND l.M_Warehouse_ID=t.M_Warehouse_ID)," + "QtyOrdered = (SELECT SUM(QtyOrdered) FROM M_Storage s, M_Locator l WHERE t.M_Product_ID=s.M_Product_ID" + " AND l.M_Locator_ID=s.M_Locator_ID AND l.M_Warehouse_ID=t.M_Warehouse_ID) " + "WHERE AD_PInstance_ID=" + getAD_PInstance_ID();
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "fillTable - Update Replenish Records=" + no );

        // Delete inactive products and replenishments

        sql = "DELETE T_Replenish r " + "WHERE (EXISTS (SELECT * FROM M_Product p " + "WHERE p.M_Product_ID=r.M_Product_ID AND p.IsActive='N')" + " OR EXISTS (SELECT * FROM M_Replenish mh " + " WHERE mh.M_Product_ID=r.M_Product_ID AND mh.IsActive='N'))" + " AND AD_PInstance_ID=" + getAD_PInstance_ID();
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "fillTable - Delete Inactive=" + no );

        // Ensure Data consistency

        sql = "UPDATE T_Replenish SET QtyOnHand = 0 WHERE QtyOnHand IS NULL";
        no  = DB.executeUpdate( sql,get_TrxName());
        sql = "UPDATE T_Replenish SET QtyReserved = 0 WHERE QtyReserved IS NULL";
        no  = DB.executeUpdate( sql,get_TrxName());
        sql = "UPDATE T_Replenish SET QtyOrdered = 0 WHERE QtyOrdered IS NULL";
        no  = DB.executeUpdate( sql,get_TrxName());

        // Set Minimum / Maximum Maintain Level

        sql = "UPDATE T_Replenish" + " SET QtyToOrder = Level_Min - QtyOnHand + QtyReserved - QtyOrdered " + "WHERE ReplenishType='1'" + " AND AD_PInstance_ID=" + getAD_PInstance_ID();
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "fillTable - Update Type-1=" + no );
        sql = "UPDATE T_Replenish" + " SET QtyToOrder = Level_Max - QtyOnHand + QtyReserved - QtyOrdered " + "WHERE ReplenishType='2'" + " AND AD_PInstance_ID=" + getAD_PInstance_ID();
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "fillTable - Update Type-2=" + no );

        // Delete rows where nothing to order

        sql = "DELETE T_Replenish " + "WHERE QtyToOrder < 1" + " AND AD_PInstance_ID=" + getAD_PInstance_ID();
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "fillTable - Delete No QtyToOrder=" + no );

        // Minimum Order Quantity

        sql = "UPDATE T_Replenish" + " SET QtyToOrder = Order_Min " + "WHERE QtyToOrder < Order_Min" + " AND AD_PInstance_ID=" + getAD_PInstance_ID();
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "fillTable - Set MinOrderQty=" + no );

        // Even dividable by Pack

        sql = "UPDATE T_Replenish" + " SET QtyToOrder = QtyToOrder - MOD(QtyToOrder, Order_Pack) + Order_Pack " + "WHERE MOD(QtyToOrder, Order_Pack) <> 0" + " AND AD_PInstance_ID=" + getAD_PInstance_ID();
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "fillTable - Set OrderPackQty=" + no );
    }    // fillTable

    /**
     * Descripción de Método
     *
     */

    private void createPO() {
        MOrder          order      = null;
        X_T_Replenish[] replenishs = getReplenish();

        for( int i = 0;i < replenishs.length;i++ ) {
            X_T_Replenish replenish = replenishs[ i ];

            if( (order == null) || (order.getC_BPartner_ID() != replenish.getC_BPartner_ID())) {
                order = new MOrder( getCtx(),0,get_TrxName());
                order.setIsSOTrx( false );
                order.setC_DocTypeTarget_ID();

                MBPartner bp = new MBPartner( getCtx(),replenish.getC_BPartner_ID(),get_TrxName());

                order.setBPartner( bp );
                order.setM_Warehouse_ID( replenish.getM_Warehouse_ID());
                order.setSalesRep_ID( getAD_User_ID());
                order.setDescription( Msg.getMsg( getCtx(),"Replenishment" ));

                if( !order.save()) {
                    return;
                }
            }

            MOrderLine line = new MOrderLine( order );

            line.setM_Product_ID( replenish.getM_Product_ID());
            line.setQty( replenish.getQtyToOrder());
            line.setPrice();
            line.save();
        }
    }    // createPO

    /**
     * Descripción de Método
     *
     */

    private void createRequisition() {
        MRequisition    requisition = null;
        X_T_Replenish[] replenishs  = getReplenish();

        for( int i = 0;i < replenishs.length;i++ ) {
            X_T_Replenish replenish = replenishs[ i ];

            if( requisition == null )    // same WH for all
            {
                requisition = new MRequisition( getCtx(),0,get_TrxName());
                requisition.setAD_User_ID( getAD_User_ID());
                requisition.setM_Warehouse_ID( replenish.getM_Warehouse_ID());
                requisition.setDescription( Msg.getMsg( getCtx(),"Replenishment" ));

                if( !requisition.save()) {
                    return;
                }
            }

            //

            MRequisitionLine line = new MRequisitionLine( requisition );

            line.setM_Product_ID( replenish.getM_Product_ID());
            line.setC_BPartner_ID( replenish.getC_BPartner_ID());
            line.setQty( replenish.getQtyToOrder());
            line.setPrice();
            line.save();
        }
    }    // createRequisition

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private X_T_Replenish[] getReplenish() {
        String sql = "SELECT * FROM T_Replenish " + "WHERE AD_PInstance_ID=? AND C_BPartner_ID > 0 " + "ORDER BY C_BPartner_ID";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getAD_PInstance_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new X_T_Replenish( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getReplenish",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        X_T_Replenish[] retValue = new X_T_Replenish[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getReplenish
}    // Replenish



/*
 *  @(#)ReplenishReport.java   02.07.07
 * 
 *  Fin del fichero ReplenishReport.java
 *  
 *  Versión 2.2
 *
 */
