/*
 * @(#)PrintBOM.java   14.jun 2007  Versión 2.2
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;
import java.util.logging.*;

import java.math.BigDecimal;

import org.openXpertya.model.MQuery;
import org.openXpertya.model.PrintInfo;
import org.openXpertya.model.X_RV_MPC_Product_BOMLine;
import org.openXpertya.model.X_T_BOMLine;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.print.Viewer;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;

/**
 *  BOM lines explosion for print
 *
 *        @author Sergio Ramazzina,Victor Perez
 *        @version $Id: PrintBOM.java,v 1.2 2005/04/19 12:54:30 srama Exp $
 */
public class PrintBOM extends SvrProcess {

    /** Descripción de Campo */
    private static final Properties	ctx	= Env.getCtx();

    /** Descripción de Campo */
    private static final String	AD_Client_ID	= ctx.getProperty("#AD_Client_ID");

    /** Descripción de Campo */
    private static final String	AD_Org_ID	= ctx.getProperty("#AD_Org_ID");

    // private int p_MPC_Product_BOM_ID = 0;

    /** Descripción de Campo */
    private int	p_M_Product_ID	= 0;

    /** Descripción de Campo */
    private boolean	p_implotion	= false;

    /** Descripción de Campo */
    private ProcessInfo	info	= null;

    /** Descripción de Campo */
    private int	LevelNo	= 1;

    /** Descripción de Campo */
    private int	SeqNo	= 0;

    /** Descripción de Campo */
    private String	levels	= new String("..........");

    /** Descripción de Campo */
    private int	AD_PInstance_ID	= 0;

    /**
     *  Prepare - e.g., get Parameters.
     */
    protected void prepare() {

        ProcessInfoParameter[]	para	= getParameter();

        for (int i = 0; i < para.length; i++) {

            String	name	= para[i].getParameterName();

            if (para[i].getParameter() == null)
                ;
            else
                if (name.equals("M_Product_ID")) {
                    p_M_Product_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                } else
                    if (name.equals("Implotion")) {

                        p_implotion	= ((String) para[i].getParameter()).equals("N")
                                          ? false
                                          : true;

                    } else
                        log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }

    }		// prepare

    /**
     *  Perform process.
     *  @return Message (clear text)
     *  @throws Exception if not successful
     */
    protected String doIt() throws Exception {

        info		= getProcessInfo();
        AD_PInstance_ID	= getAD_PInstance_ID();
        loadBOM();
        print();

        return "@ProcessOK@";

    }		// doIt

    /**
     * Descripción de Método
     *
     */
    void print() {

        MPrintFormat	format		= null;
        Language	language	= Language.getLoginLanguage();		// Base Language

        format	= MPrintFormat.get(getCtx(), 1000525, false);
        format.setLanguage(language);
        format.setTranslationLanguage(language);

        // query
        MQuery	query	= new MQuery("RV_MPC_Product_BOMLine");

        query.addRestriction("AD_PInstance_ID", MQuery.EQUAL, new Integer(AD_PInstance_ID));

        // Engine
        PrintInfo	info	= new PrintInfo("RV_MPC_Product_BOMLine", X_RV_MPC_Product_BOMLine.Table_ID, getRecord_ID());
        ReportEngine	re	= new ReportEngine(getCtx(), format, query, info);

        new Viewer(re);

        String	sql	= "DELETE FROM T_BomLine WHERE AD_PInstance_ID = " + AD_PInstance_ID;

        DB.executeUpdate(sql);
    }

    /*
     * private int getBOMForProduct(int M_Product_ID) {
     *   int bomID = DB.getSQLValue(null,"SELECT MPC_PRODUCT_BOM_ID FROM MPC_PRODUCT_BOM WHERE M_PRODUCT_ID=?",
     *                   M_Product_ID);
     *
     *   return bomID;
     * }
     *
     * private int getBOMForProduct(int M_Product_ID, int M_ATTRIBUTESETINSTANCE_ID) {
     *   int bomID = DB.getSQLValue(null,
     *           "SELECT MPC_PRODUCT_BOM_ID FROM MPC_PRODUCT_BOM WHERE M_PRODUCT_ID=?" +
     *           " AND M_ATTRIBUTESETINSTANCE_ID=?", M_Product_ID,
     *                           M_ATTRIBUTESETINSTANCE_ID);
     *
     *   return bomID;
     * }
     *
     * private int getProductForBOM(int MPC_PRODUCT_BOM_ID) {
     *   int bomID = DB.getSQLValue(null,"SELECT M_PRODUCT_ID FROM MPC_PRODUCT_BOM WHERE MPC_PRODUCT_BOM_ID=?",
     *                   MPC_PRODUCT_BOM_ID);
     *
     *   return bomID;
     * }
     */

    /**
     *          Action: Fill Tree with all nodes
     */
    private void loadBOM() {

        if (p_M_Product_ID == 0)
            return;

        X_T_BOMLine	tboml	= new X_T_BOMLine(ctx, 0, null);

        tboml.setMPC_Product_BOM_ID(0);
        tboml.setMPC_Product_BOMLine_ID(0);
        tboml.setM_Product_ID(p_M_Product_ID);
        tboml.setLevelNo(0);
        tboml.setLevels("0");
        tboml.setSeqNo(0);
        tboml.setAD_PInstance_ID(AD_PInstance_ID);
        tboml.save(get_TrxName());

        if (p_implotion) {

            PreparedStatement	stmt	= null;
            ResultSet		rs	= null;
            String		sql	= new String("SELECT MPC_Product_BOMLine_ID FROM MPC_Product_BOMLine WHERE IsActive = 'Y' AND M_Product_ID = ? ");

            // System.out.println("Implotion " + sql);
            try {

                stmt	= DB.prepareStatement(sql);
                stmt.setInt(1, p_M_Product_ID);
                rs	= stmt.executeQuery();

                while (rs.next()) {

                    parentImplotion(rs.getInt(1));

                    // System.out.println("Implotion MPC_Product_BOMLine_ID:"+ rs.getInt(1));
                }

            } catch (SQLException e) {
                log.log(Level.SEVERE, "explodeBOM ", e);
            }

        } else {

            PreparedStatement	stmt	= null;
            ResultSet		rs	= null;
            String		sql	= new String("SELECT MPC_Product_BOM_ID FROM MPC_Product_BOM WHERE IsActive = 'Y' AND M_Product_ID = ? ");

            System.out.println("Explotion" + sql);

            try {

                stmt	= DB.prepareStatement(sql);
                stmt.setInt(1, p_M_Product_ID);
                rs	= stmt.executeQuery();

                while (rs.next()) {

                    parentExplotion(rs.getInt(1));
                    System.out.println("Explotion MPC_Product_BOM_ID " + rs.getInt(1));
                }

            } catch (SQLException e) {
                log.log(Level.SEVERE, "explodeBOM ", e);
            }
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param MPC_Product_BOMLine_ID
     */
    public void parentImplotion(int MPC_Product_BOMLine_ID) {

        X_T_BOMLine	tboml			= new X_T_BOMLine(ctx, 0, null);
        int		MPC_Product_BOM_ID	= DB.getSQLValue(null, "SELECT MPC_Product_BOM_ID FROM MPC_Product_BOMLine WHERE MPC_Product_BOMLine_ID=?", MPC_Product_BOMLine_ID);
        int	M_Product_ID	= DB.getSQLValue(null, "SELECT M_Product_ID FROM MPC_Product_BOM WHERE MPC_Product_BOM_ID=?", MPC_Product_BOM_ID);

        tboml.setMPC_Product_BOM_ID(MPC_Product_BOM_ID);
        tboml.setMPC_Product_BOMLine_ID(MPC_Product_BOMLine_ID);
        tboml.setM_Product_ID(M_Product_ID);
        tboml.setLevelNo(LevelNo);
        tboml.setLevels(levels.substring(0, LevelNo) + LevelNo);
        tboml.setSeqNo(SeqNo);
        tboml.setAD_PInstance_ID(AD_PInstance_ID);
        tboml.save(get_TrxName());

        PreparedStatement	stmt	= null;
        ResultSet		rs	= null;
        String			sql	= new String("SELECT MPC_Product_BOM_ID, M_Product_ID FROM MPC_Product_BOM WHERE IsActive = 'Y' AND M_Product_ID = ? ");

        try {

            stmt	= DB.prepareStatement(sql);
            stmt.setInt(1, M_Product_ID);
            rs	= stmt.executeQuery();

            while (rs.next()) {

                SeqNo	+= 1;
                component(rs.getInt(2));
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, "explodeBOM ", e);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param MPC_Product_BOM_ID
     */
    public void parentExplotion(int MPC_Product_BOM_ID) {

        PreparedStatement	stmt	= null;
        ResultSet		rs	= null;
        String			sql	= new String("SELECT MPC_Product_BOMLine_ID, M_Product_ID FROM MPC_Product_BOMLine boml WHERE IsActive = 'Y' AND MPC_Product_BOM_ID = ? ");

        // LevelNo += 1;
        try {

            stmt	= DB.prepareStatement(sql);
            stmt.setInt(1, MPC_Product_BOM_ID);
            rs	= stmt.executeQuery();

            while (rs.next()) {

                System.out.println("Lines:" + rs.getInt(1));
                SeqNo	+= 1;

                X_T_BOMLine	tboml	= new X_T_BOMLine(ctx, 0, null);

                tboml.setMPC_Product_BOM_ID(MPC_Product_BOM_ID);
                tboml.setMPC_Product_BOMLine_ID(rs.getInt(1));
                tboml.setM_Product_ID(rs.getInt(2));
                tboml.setLevelNo(LevelNo);
                tboml.setLevels(levels.substring(0, LevelNo) + LevelNo);
                tboml.setSeqNo(SeqNo);
                tboml.setAD_PInstance_ID(AD_PInstance_ID);
                tboml.save(get_TrxName());
                component(rs.getInt(2));
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, "explodeBOM ", e);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     */
    public void component(int M_Product_ID) {

        if (p_implotion) {

            LevelNo	+= 1;

            PreparedStatement	stmt	= null;
            ResultSet		rs	= null;
            String		sql	= new String("SELECT MPC_Product_BOMLine_ID FROM MPC_Product_BOMLine  WHERE IsActive = 'Y' AND M_Product_ID = ? ");

            try {

                stmt	= DB.prepareStatement(sql);
                stmt.setInt(1, M_Product_ID);
                rs	= stmt.executeQuery();

                boolean	level	= false;

                while (rs.next()) {

                    level	= true;
                    parentImplotion(rs.getInt(1));
                }

            } catch (SQLException e) {

                log.log(Level.SEVERE, "explodeBOM ", e);

                // return -1;
            }

        } else {

            LevelNo	+= 1;

            PreparedStatement	stmt	= null;
            ResultSet		rs	= null;
            String		sql	= new String("SELECT MPC_Product_BOM_ID FROM MPC_Product_BOM  WHERE IsActive = 'Y' AND Value = ? ");

            try {

                stmt	= DB.prepareStatement(sql);

                String	Value	= DB.getSQLValueString(null, "SELECT Value FROM M_PRODUCT WHERE M_PRODUCT_ID=?", M_Product_ID);

                stmt.setString(1, Value);
                rs	= stmt.executeQuery();

                boolean	level	= false;

                while (rs.next()) {

                    level	= true;
                    parentExplotion(rs.getInt(1));
                }

                if (!level)
                    LevelNo	-= 1;

            } catch (SQLException e) {
                log.log(Level.SEVERE, "explodeBOM ", e);
            }
        }
    }

    /**
     * @param productBOMId
     */

    /*
     * private int explodeBOM(int MPC_PRODUCT_BOM_ID)
     * {
     *   String sqlCmd1 = "SELECT bomlp.M_Product_ID, bomlp.IsBOM, boml.qtybom, " +
     *       "bomlp.c_uom_id, boml.m_attributesetinstance_id " +
     *       " from mpc_product_bomline boml inner join m_product " +
     *       "bomlp on boml.m_product_id=bomlp.m_product_id " +
     *       " where boml.mpc_product_bom_id=? and boml.AD_Client_ID=? order by line";
     *   String sqlCmd2 =
     *       "insert into t_ita_printbomline (ad_pinstance_id, ad_client_id, ad_org_id, " +
     *       "createdby, updatedby, seqno, m_product_id, mpc_product_bom_id, " +
     *       "lvl, c_uom_id, qtybom, m_attributesetinstance_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
     *
     *   PreparedStatement prsm = null;
     *   PreparedStatement prsm1 = null;
     *   ResultSet rs = null;
     *
     *   // Check If it is printable
     *   try {
     *       prsm = DB.prepareStatement(sqlCmd1);
     *       prsm1 = DB.prepareStatement(sqlCmd2);
     *
     *       prsm.setInt(1, MPC_PRODUCT_BOM_ID);
     *       prsm.setString(2, AD_Client_ID);
     *
     *       rs = prsm.executeQuery();
     *
     *       LevelNo += 1;
     *
     *       while (rs.next())
     *       {
     *           SeqNo += 1;
     *           prsm1.setInt(1, info.getAD_PInstance_ID());
     *           prsm1.setString(2, AD_Client_ID);
     *           prsm1.setString(3, AD_Org_ID);
     *           prsm1.setInt(4, info.getAD_User_ID().intValue());
     *           prsm1.setInt(5, info.getAD_User_ID().intValue());
     *           prsm1.setInt(6, SeqNo);
     *           prsm1.setInt(7, rs.getInt(1));
     *           prsm1.setInt(8, p_MPC_Product_BOM_ID);
     *           prsm1.setInt(9, LevelNo);
     *           prsm1.setInt(10, rs.getInt(4));
     *           prsm1.setInt(11, rs.getInt(3));
     *           prsm1.setInt(12, rs.getInt(5));
     *
     *           prsm1.executeUpdate();
     *
     *           if (rs.getString(2).equals("Y"))
     *           {
     *               // Childs are BOM as well
     *                   if (rs.getInt(5) != 0)
     *                   {
     *                           explodeBOM(getBOMForProduct(rs.getInt(1), rs.getInt(5)));
     *                   }
     *                   else
     *                   {
     *                           explodeBOM(getBOMForProduct(rs.getInt(1)));
     *                   }
     *
     *               LevelNo -= 1;
     *           }
     *       }
     *   }
     *   catch (SQLException e)
     *           {
     *           log.log(Level.SEVERE,"explodeBOM " , e);
     *       return -1;
     *   }
     *   finally
     *           {
     *       try
     *                   {
     *           if (rs != null)
     *           {
     *               rs.close();
     *           }
     *
     *           if (prsm != null)
     *           {
     *               prsm.close();
     *           }
     *
     *           if (prsm1 != null)
     *           {
     *               prsm1.close();
     *           }
     *       }
     *       catch (SQLException e1)
     *                   {
     *           // TODO Auto-generated catch block
     *           e1.printStackTrace();
     *       }
     *   }
     *
     *   return 0;
     * }
     */
}



/*
 * @(#)PrintBOM.java   14.jun 2007
 * 
 *  Fin del fichero PrintBOM.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
