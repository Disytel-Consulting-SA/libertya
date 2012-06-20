/*
 * @(#)MMPCProductBOMLine.java   13.jun 2007  Versión 2.2
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



//package org.openXpertya.mfg.model;
package openXpertya.model;

import java.util.*;

import java.sql.*;

import java.math.*;

import javax.swing.*;
import javax.swing.tree.*;

import java.util.logging.*;

import org.openXpertya.grid.tree.*;
import org.openXpertya.model.*;
import org.openXpertya.util.*;

/**
 *  Order Line Model.
 *      <code>
 *                      MOrderLine ol = new MOrderLine(m_order);
                        ol.setM_Product_ID(wbl.getM_Product_ID());
                        ol.setQtyOrdered(wbl.getQuantity());
                        ol.setPrice();
                        ol.setPriceActual(wbl.getPrice());
                        ol.setTax();
                        ol.save();

 *      </code>
 *  @author Jorg Janke
 *  @version $Id: MOrderLine.java,v 1.22 2004/03/22 07:15:03 jjanke Exp $
 */
public class MMPCProductBOMLine extends X_MPC_Product_BOMLine {

    /*
     * private Vector dataBOM = new Vector();
     * private Vector layout = new Vector();
     * private Vector columnNames;
     * private static JTree                    m_tree;
     */

    // static private int level  = 0;
    // static private int lowlevel = 0;
    // static private int comp = 0;
    // static private int parent = 0 ;

    /** Descripción de Campo */
    static private int	AD_Client_ID	= 0;

    // static private int m_M_Product_ID = 0;

    /** Descripción de Campo */
    static Hashtable	tableproduct	= new Hashtable();

    // static DefaultMutableTreeNode bom = new DefaultMutableTreeNode(Msg.translate(Env.getCtx(), "BOM"));

    /**
     *  Default Constructor
     *  @param ctx context
     *  @param  C_OrderLine_ID  order line to load
     * @param MPC_Product_BOMLine
     * @param trxName
     */
    public MMPCProductBOMLine(Properties ctx, int MPC_Product_BOMLine, String trxName) {
        super(ctx, MPC_Product_BOMLine, trxName);
    }		// MOrderLine

    /**
     *  Parent Constructor.
     *               ol.setM_Product_ID(wbl.getM_Product_ID());
     *               ol.setQtyOrdered(wbl.getQuantity());
     *               ol.setPrice();
     *               ol.setPriceActual(wbl.getPrice());
     *               ol.setTax();
     *               ol.save();
     *  @param  order parent order
     *
     * @param bom
     */
    public MMPCProductBOMLine(MMPCProductBOM bom) {

        super(bom.getCtx(), 0, null);

        if (bom.getID() == 0) {
            throw new IllegalArgumentException("Header not saved");
        }

        setMPC_Product_BOM_ID(bom.getMPC_Product_BOM_ID());	// parent

        // setMMPCProductBOM(bom);
        //
        // setC_Tax_ID (0);
        // setLine (0);
        // setC_UOM_ID (0);
        //
        // setIsDescription(false);
        //
        // setPriceList (Env.ZERO);
        // setPriceActual (Env.ZERO);
        // setPriceLimit (Env.ZERO);
        // setLineNetAmt (Env.ZERO);
        //
        // setQtyOrdered (Env.ZERO);
        // setQtyDelivered (Env.ZERO);
        // setQtyReserved (Env.ZERO);
        // setQtyInvoiced (Env.ZERO);
        //
        // setFreightAmt (Env.ZERO);
        // setChargeAmt (Env.ZERO);

    }		// MOrderLine

    /**
     *  Load Constructor
     *  @param ctx context
     *  @param rs result set record
     * @param trxName
     */
    public MMPCProductBOMLine(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MOrderLine

    /**
     *      Set Defaults from Order.
     *      Does not set Parent !!
     *      @param order order
     *
     * @param bom
     */
    public void setMMPCProductBOM(MMPCProductBOM bom) {

        setClientOrg(bom);

        // setC_BPartner_ID(order.getC_BPartner_ID());
        // setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
        // setM_Warehouse_ID(order.getM_Warehouse_ID());
        // setDateOrdered(order.getDateOrdered());
        // setDatePromised(order.getDatePromised());
        // m_M_PriceList_ID = order.getM_PriceList_ID();
        // m_IsSOTrx = order.isSOTrx();
        // setC_Currency_ID(order.getC_Currency_ID());

    }		// setOrder

    /**
     *      Set Defaults if not set
     */
    private void setDefaults() {

        /*
         * //      Get Defaults from Parent
         * if (getC_BPartner_ID() == 0 || getC_BPartner_Location_ID() == 0
         *       || getM_Warehouse_ID() == 0)
         * {
         *       MOrder o = new MOrder (getCtx(), getC_Order_ID());
         *       setOrder (o);
         * }
         *
         * //      Set Price
         * if (!m_priceSet && Env.ZERO.compareTo(getPriceActual()) == 0)
         *       setPrice();
         *
         * //      Set Tax
         * if (getC_Tax_ID() == 0)
         *       setTax();
         *
         * //      Get Line No
         * if (getLine() == 0)
         * {
         *       String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_OrderLine WHERE C_Order_ID=?";
         *       int ii = DB.getSQLValue (sql, getC_Order_ID());
         *       setLine (ii);
         * }
         * //      UOM
         * if (getC_UOM_ID() == 0)
         *       setC_UOM_ID (Env.getContextAsInt(getCtx(), "#C_UOM_ID"));
         * if (getC_UOM_ID() == 0)
         * {
         *       int C_UOM_ID = MUOM.getDefault_UOM_ID(getCtx());
         *       if (C_UOM_ID > 0)
         *               setC_UOM_ID (C_UOM_ID);
         * }
         *
         * //      Calculations
         * setLineNetAmt(getPriceActual().multiply(getQtyOrdered()));
         * setDiscount();
         */
    }		// setDefaults

    /**
     *      Save
     *      @return true if saved
     */
    public boolean save() {

        log.fine("MProduct_BOMLine.save");
        setDefaults();

        return super.save();

    }		// save

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MMPCProductBOMLine[").append(getID()).append("]");

        return sb.toString();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDescriptionText() {
        return super.getDescription();
    }		// getDescriptionText

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     *
     * @return
     */
    public static int getlowLevel(int M_Product_ID) {

        // m_M_Product_ID=M_Product_ID;
        AD_Client_ID	= Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));
        tableproduct.clear();

        DefaultMutableTreeNode	ibom	= iparent(M_Product_ID, 0);

        return ibom.getDepth();
    }		// getDescriptionText

    // Explotion

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     * @param MPC_Product_BOM_ID
     *
     * @return
     */
    private static DefaultMutableTreeNode parent(int M_Product_ID, int MPC_Product_BOM_ID) {

        DefaultMutableTreeNode	parent	= new DefaultMutableTreeNode(Integer.toString(M_Product_ID) + "|" + Integer.toString(MPC_Product_BOM_ID));
        String	sql	= new String("SELECT pbom.MPC_Product_BOM_ID FROM  MPC_Product_BOM pbom WHERE pbom.IsActive = 'Y'  AND  pbom.AD_Client_ID= ? AND pbom.M_Product_ID = ? ");
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
            pstmt.setInt(2, M_Product_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                DefaultMutableTreeNode	bom	= component(rs.getInt(1), M_Product_ID, parent);

                if (bom != null) {
                    parent.add(bom);
                }

                // parent.add(component(rs.getInt(1), M_Product_ID , parent));
            }

            rs.close();
            pstmt.close();

            return parent;

        } catch (Exception e) {

            // log.error ("doIt - " + sql, e);
            System.out.println("doIt - " + sql + e);
        }

        return parent;
    }

    // Explotion

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_BOM_ID
     * @param M_Product_ID
     * @param bom
     *
     * @return
     */
    private static DefaultMutableTreeNode component(int M_Product_BOM_ID, int M_Product_ID, DefaultMutableTreeNode bom) {

        // System.out.println("Level" + bom.getLevel());
        String	sql	= new String("SELECT pboml.M_Product_ID , pbom.Value , pboml.MPC_Product_BOMLine_ID , pbom.MPC_Product_BOM_ID FROM  MPC_Product_BOM pbom INNER JOIN MPC_Product_BOMLine pboml ON (pbom.MPC_Product_BOM_ID = pboml.MPC_Product_BOM_ID) WHERE pbom.IsActive= 'Y' AND pboml.IsActive= 'Y' AND pbom.AD_Client_ID= ? AND pbom.MPC_Product_BOM_ID = ? ");

        // System.out.println("SQL Component:" + sql +  "Parametro:" + M_Product_BOM_ID);
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setString(1,  Value);
            pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
            pstmt.setInt(2, M_Product_BOM_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                if (M_Product_ID != rs.getInt(1)) {

                    // System.out.println("Component:"+ rs.getInt(1));
                    bom.add(parent(rs.getInt(1), rs.getInt(4)));

                    // System.out.println("Componet"+ bom.toString());
                } else {

                    JOptionPane.showMessageDialog(null, "Componet will be deactivated for BOM & Formula:" + rs.getString(2) + "(" + rs.getString(3) + ")", "Error Cycle BOM", JOptionPane.ERROR_MESSAGE);

                    MMPCProductBOMLine	MPC_Product_BOMLine	= new MMPCProductBOMLine(Env.getCtx(), rs.getInt(3), null);

                    MPC_Product_BOMLine.setIsActive(false);
                    MPC_Product_BOMLine.save();
                }
            }

            // System.out.println("Renglones Componet" + rs.getRow());
            if (rs.getRow() == 0) {

                DefaultMutableTreeNode	parent	= new DefaultMutableTreeNode(Integer.toString(M_Product_ID) + "|0");

                bom.add(parent);

                return bom;
            }

            rs.close();
            pstmt.close();

            // return bom;

        } catch (Exception e) {

            // log.error ("doIt - " + sql, e);
            System.out.println("doIt - " + sql + e);
        }

        return null;
    }

    // imp

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     * @param MPC_Product_BOM_ID
     *
     * @return
     */
    private static DefaultMutableTreeNode iparent(int M_Product_ID, int MPC_Product_BOM_ID) {

        DefaultMutableTreeNode	parent	= new DefaultMutableTreeNode(Integer.toString(M_Product_ID) + "|" + Integer.toString(MPC_Product_BOM_ID));
        String	sql	= new String("SELECT pboml.MPC_Product_BOMLine_ID FROM  MPC_Product_BOMLine pboml WHERE pboml.IsActive= 'Y' AND pboml.AD_Client_ID = ? AND pboml.M_Product_ID = ? ");

        // System.out.println("Padre" +  M_Product_ID);
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Client_ID);
            pstmt.setInt(2, M_Product_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                DefaultMutableTreeNode	bom	= icomponent(rs.getInt(1), M_Product_ID, parent);

                if (bom != null) {
                    parent.add(bom);
                }
            }

            rs.close();
            pstmt.close();

            return parent;

        } catch (Exception e) {

            // log.error ("doIt - " + sql, e);
            System.out.println("doIt - " + sql + e);
        }

        return parent;
    }

    // Imp

    /**
     * Descripción de Método
     *
     *
     * @param MPC_Product_BOMLine_ID
     * @param M_Product_ID
     * @param bom
     *
     * @return
     */
    private static DefaultMutableTreeNode icomponent(int MPC_Product_BOMLine_ID, int M_Product_ID, DefaultMutableTreeNode bom) {

        // System.out.println("Level" + bom.getLevel());
        String	sql	= new String("SELECT pbom.M_Product_ID , pbom.Value , pbom.MPC_Product_BOM_ID FROM  MPC_Product_BOMLine pboml INNER JOIN MPC_Product_BOM pbom ON (pbom.MPC_Product_BOM_ID = pboml.MPC_Product_BOM_ID) WHERE pbom.IsActive= 'Y' AND pboml.IsActive= 'Y' AND pboml.AD_Client_ID =? AND pboml.MPC_Product_BOMLine_ID = ? ");
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Client_ID);
            pstmt.setInt(2, MPC_Product_BOMLine_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                // System.out.println("Componet:" + rs.getInt(1));
                if (M_Product_ID != rs.getInt(1)) {

                    if (!tableproduct(rs.getInt(1), rs.getInt(3))) {
                        bom.add(iparent(rs.getInt(1), rs.getInt(3)));
                    } else {
                        System.out.println("Cycle BOM & Formula:" + rs.getString(2) + "(" + rs.getString(3) + ")");
                    }

                    // JOptionPane.showMessageDialog(null,"Cycle BOM & Formula:" + rs.getString(2) + "(" + rs.getString(3) + ")", "Error Cycle BOM" , JOptionPane.ERROR_MESSAGE);

                } else {

                    JOptionPane.showMessageDialog(null, "Componet will be deactivated for BOM & Formula:" + rs.getString(2) + "(" + rs.getString(3) + ")", "Error Cycle BOM", JOptionPane.ERROR_MESSAGE);

                    MMPCProductBOMLine	MPC_Product_BOMLine	= new MMPCProductBOMLine(Env.getCtx(), rs.getInt(3), null);

                    MPC_Product_BOMLine.setIsActive(false);
                    MPC_Product_BOMLine.save();
                }
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {

            // log.error ("doIt - " + sql, e);
            System.out.println("doIt - " + sql + e);
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     * @param MPC_Product_BOM_ID
     *
     * @return
     */
    private static boolean tableproduct(int M_Product_ID, int MPC_Product_BOM_ID) {

        Integer	p	= new Integer(M_Product_ID);
        Integer	bom	= new Integer(MPC_Product_BOM_ID);

        // String key = p.toString() ; //+ bom.toString();

        if (!tableproduct.containsKey(p)) {

            tableproduct.put(p, bom);

            return false;

        } else {
            return true;
        }
    }
}	// MOrderLine



/*
 * @(#)MMPCProductBOMLine.java   13.jun 2007
 * 
 *  Fin del fichero MMPCProductBOMLine.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
