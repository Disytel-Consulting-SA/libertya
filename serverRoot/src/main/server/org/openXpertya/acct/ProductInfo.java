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



package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MUOMConversion;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProductInfo {

    /**
     * Constructor de la clase ...
     *
     *
     * @param M_Product_ID
     * @param trxName
     */

    public ProductInfo( int M_Product_ID,String trxName ) {
        m_trxName = trxName;
        init( M_Product_ID );
    }    // ProductInfo

    /** Descripción de Campos */

    private int m_M_Product_ID = 0;

    /** Descripción de Campos */

    private String m_trxName = null;

    // Product Info

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private String m_productType = null;

    /** Descripción de Campos */

    private String m_ProductCategory = null;

    /** Descripción de Campos */

    private boolean m_isBOM = false;

    /** Descripción de Campos */

    private boolean m_isStocked = true;

    /** Descripción de Campos */

    private int m_C_RevenueRecognition_ID = 0;

    /** Descripción de Campos */

    private int m_C_UOM_ID = 0;

    /** Descripción de Campos */

    private BigDecimal m_qty = Env.ZERO;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     */

    private void init( int M_Product_ID ) {
        m_M_Product_ID = M_Product_ID;

        if( m_M_Product_ID == 0 ) {
            return;
        }

        String sql = "SELECT p.ProductType, pc.Value, "                                                                                                               // 1..2
                     + "p.C_RevenueRecognition_ID,p.C_UOM_ID, "                                                                                                       // 3..4
                     + "p.AD_Client_ID,p.AD_Org_ID, "                                                                                                                 // 5..6
                     + "p.IsBOM, p.IsStocked "                                                                                                                        // 7..8
                     + "FROM M_Product_Category pc" + " INNER JOIN M_Product p ON (pc.M_Product_Category_ID=p.M_Product_Category_ID) " + "WHERE p.M_Product_ID=?";    // #1

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_productType             = rs.getString( 1 );
                m_ProductCategory         = rs.getString( 2 );
                m_C_RevenueRecognition_ID = rs.getInt( 3 );
                m_C_UOM_ID                = rs.getInt( 4 );

                // reference

                m_AD_Client_ID = rs.getInt( 5 );
                m_AD_Org_ID    = rs.getInt( 6 );

                //

                m_isBOM     = "Y".equals( rs.getString( 7 ));
                m_isStocked = "Y".equals( rs.getString( 8 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isProduct() {
        return MProduct.PRODUCTTYPE_Item.equals( m_productType );
    }    // isProduct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isBOM() {
        return m_isBOM;
    }    // isBOM

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isStocked() {
        return m_isStocked;
    }    // isStocked

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isService() {
        return MProduct.PRODUCTTYPE_Service.equals( m_productType );
    }    // isService

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getProductCategory() {
        return m_ProductCategory;
    }    // getProductCategory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isRevenueRecognition() {
        return m_C_RevenueRecognition_ID != 0;
    }    // isRevenueRecognition

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_RevenueRecognition_ID() {
        return m_C_RevenueRecognition_ID;
    }    // getC_RevenueRecognition_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_UOM_ID() {
        return m_C_UOM_ID;
    }    // getC_UOM_ID

    /**
     * Descripción de Método
     *
     *
     * @param qty
     */

    public void setQty( BigDecimal qty ) {
        m_qty = qty;
    }    // setQty

    /**
     * Descripción de Método
     *
     *
     * @param qty
     * @param C_UOM_ID
     */

    public void setQty( BigDecimal qty,int C_UOM_ID ) {
        m_qty = MUOMConversion.convert( C_UOM_ID,m_C_UOM_ID,qty,true );    // StdPrecision

        if( (qty != null) && (m_qty == null) )    // conversion error
        {
            log.severe( "Conversion error - set to " + qty );
            m_qty = qty;
        }
    }                                             // setQty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQty() {
        return m_qty;
    }    // getQty

    /** Descripción de Campos */

    public static final int ACCTTYPE_P_Revenue = 1;

    /** Descripción de Campos */

    public static final int ACCTTYPE_P_Expense = 2;

    /** Descripción de Campos */

    public static final int ACCTTYPE_P_Asset = 3;

    /** Descripción de Campos */

    public static final int ACCTTYPE_P_Cogs = 4;

    /** Descripción de Campos */

    public static final int ACCTTYPE_P_PPV = 5;

    /** Descripción de Campos */

    public static final int ACCTTYPE_P_IPV = 6;

    /** Descripción de Campos */

    public static final int ACCTTYPE_P_TDiscountRec = 7;

    /** Descripción de Campos */

    public static final int ACCTTYPE_P_TDiscountGrant = 8;
    
    public static final int ACCTTYPE_P_Amortization = 9;
    
    public static final int ACCTTYPE_P_Amortization_Realized = 10;
    
    public static final int ACCTTYPE_P_RevenueExchange = 11;

    /**
     * Descripción de Método
     *
     *
     * @param AcctType
     * @param as
     *
     * @return
     */

    public MAccount getAccount( int AcctType,MAcctSchema as ) {
        if( (AcctType < 1) || (AcctType > 11) ) {
            return null;
        }

        // No Product - get Default from Product Category

        if( m_M_Product_ID == 0 ) {
            return getAccountDefault( AcctType,as );
        }

        String sql = "SELECT P_Revenue_Acct, P_Expense_Acct, P_Asset_Acct, P_Cogs_Acct, "    // 1..4
                     + "P_PurchasePriceVariance_Acct, P_InvoicePriceVariance_Acct, "    // 5..6
                     + "P_TradeDiscountRec_Acct, P_TradeDiscountGrant_Acct , "            // 7..8
                     + "P_Amortization_Acct, P_Amortization_Realized_Acct, "            // 9..10
                     + "P_RevenueExchange_Acct "            // 11
                     + "FROM M_Product_Acct " + "WHERE M_Product_ID=? AND C_AcctSchema_ID=?";

        //

        int validCombination_ID = 0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_M_Product_ID );
            pstmt.setInt( 2,as.getC_AcctSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                validCombination_ID = rs.getInt( AcctType );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        if( validCombination_ID == 0 ) {
            return null;
        }

        return MAccount.get( as.getCtx(),validCombination_ID );
    }    // getAccount

    /**
     * Descripción de Método
     *
     *
     * @param AcctType
     * @param as
     *
     * @return
     */

    public MAccount getAccountDefault( int AcctType,MAcctSchema as ) {
        if( (AcctType < 1) || (AcctType > 8) ) {
            return null;
        }

        String sql = "SELECT P_Revenue_Acct, P_Expense_Acct, P_Asset_Acct, P_Cogs_Acct, " + "P_PurchasePriceVariance_Acct, P_InvoicePriceVariance_Acct, " + "P_TradeDiscountRec_Acct, P_TradeDiscountGrant_Acct " + "FROM M_Product_Category pc, M_Product_Category_Acct pca " + "WHERE pc.M_Product_Category_ID=pca.M_Product_Category_ID" + " AND pca.C_AcctSchema_ID=? " + "ORDER BY pc.IsDefault DESC, pc.Created";

        //

        int validCombination_ID = 0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,as.getC_AcctSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                validCombination_ID = rs.getInt( AcctType );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        if( validCombination_ID == 0 ) {
            return null;
        }

        return MAccount.get( as.getCtx(),validCombination_ID );
    }    // getAccountDefault

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    public BigDecimal getProductCosts( MAcctSchema as ) {
        if( m_qty == null ) {
            log.fine( "No Qty" );

            return null;
        }

        BigDecimal cost = getProductItemCost( as,null );

        if( cost == null ) {
            log.fine( "No Costs" );

            return null;
        }

        log.fine( "Qty(" + m_qty + ") * Cost(" + cost + ") = " + m_qty.multiply( cost ));

        return m_qty.multiply( cost );
    }    // getProductCosts

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param costType
     *
     * @return
     */

    public BigDecimal getProductItemCost( MAcctSchema as,String costType ) {
        BigDecimal   current = null;
        BigDecimal   cost    = null;
        String       cm      = as.getCostingMethod();
        StringBuffer sql     = new StringBuffer( "SELECT CurrentCostPrice," );    // 1

        //

        if( ( (costType == null) && MAcctSchema.COSTINGMETHOD_Average.equals( cm )) || MAcctSchema.COSTINGMETHOD_Average.equals( costType )) {
            sql.append( "COSTAVERAGE" );    // 2

            // else if (AcctSchema.COSTING_FIFO.equals(cm))
            // sql.append("COSTFIFO");
            // else if (AcctSchema.COSTING_LIFO.equals(cm))
            // sql.append("COSTLIFO");

        } else if( ( (costType == null) && MAcctSchema.COSTINGMETHOD_LastPOPrice.equals( cm )) || MAcctSchema.COSTINGMETHOD_LastPOPrice.equals( costType )) {
            sql.append( "PRICELASTPO" );
        } else {    // AcctSchema.COSTING_STANDARD
            sql.append( "COSTSTANDARD" );
        }

        sql.append( " FROM M_Product_Costing WHERE M_Product_ID=? AND C_AcctSchema_ID=?" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,m_M_Product_ID );
            pstmt.setInt( 2,as.getC_AcctSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                current = rs.getBigDecimal( 1 );
                cost    = rs.getBigDecimal( 2 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        }

        // Return Costs

        if( (costType != null) && (cost != null) &&!cost.equals( Env.ZERO )) {
            log.fine( "Costs=" + cost );

            return cost;
        } else if( (current != null) &&!current.equals( Env.ZERO )) {
            log.fine( "Current=" + current );

            return current;
        }

        // Create/Update Cost Record

        boolean create = ( (cost == null) && (current == null) );

        return updateCosts( as,create );
    }    // getProductCost

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param create
     *
     * @return
     */

    private BigDecimal updateCosts( MAcctSchema as,boolean create ) {

        // Create Zero Record

        if( create ) {
            StringBuffer sql = new StringBuffer( "INSERT INTO M_Product_Costing " + "(M_Product_ID,C_AcctSchema_ID," + " AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy," + " CurrentCostPrice,CostStandard,FutureCostPrice," + " CostStandardPOQty,CostStandardPOAmt,CostStandardCumQty,CostStandardCumAmt," + " CostAverage,CostAverageCumQty,CostAverageCumAmt," + " PriceLastPO,PriceLastInv, TotalInvQty,TotalInvAmt) " + "VALUES (" );

            sql.append( m_M_Product_ID ).append( "," ).append( as.getC_AcctSchema_ID()).append( "," ).append( m_AD_Client_ID ).append( "," ).append( m_AD_Org_ID ).append( "," ).append( "'Y',SysDate,0,SysDate,0, 0,0,0,  0,0,0,0,  0,0,0,  0,0,  0,0)" );

            int no = DB.executeUpdate( sql.toString(),m_trxName );

            if( no == 1 ) {
                log.fine( "CostingCreated" );
            }
        }

        // Try to find non ZERO Price

        String     costSource = "PriceList-PO";
        BigDecimal costs      = getPriceList( as,true );

        if( (costs == null) || costs.equals( Env.ZERO )) {
            costSource = "PO Cost";
            costs      = getPOCost( as );
        }

        if( (costs == null) || costs.equals( Env.ZERO )) {
            costSource = "PriceList";
            costs      = getPriceList( as,false );
        }

        // if not found use $1 (to be able to do material transactions)

        if( (costs == null) || costs.equals( Env.ZERO )) {
            costSource = "Not Found";
            costs      = new BigDecimal( "1" );
        }

        // update current costs

        StringBuffer sql = new StringBuffer( "UPDATE M_Product_Costing " );

        sql.append( "SET CurrentCostPrice=" ).append( costs ).append( " WHERE M_Product_ID=" ).append( m_M_Product_ID ).append( " AND C_AcctSchema_ID=" ).append( as.getC_AcctSchema_ID());

        int no = DB.executeUpdate( sql.toString(),m_trxName );

        if( no == 1 ) {
            log.fine( costSource + " - " + costs );
        }

        return costs;
    }    // createCosts

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param onlyPOPriceList
     *
     * @return
     */

    private BigDecimal getPriceList( MAcctSchema as,boolean onlyPOPriceList ) {
        StringBuffer sql = new StringBuffer( "SELECT pl.C_Currency_ID, pp.PriceList, pp.PriceStd, pp.PriceLimit " + "FROM M_PriceList pl, M_PriceList_Version plv, M_ProductPrice pp " + "WHERE pl.M_PriceList_ID = plv.M_PriceList_ID" + " AND plv.M_PriceList_Version_ID = pp.M_PriceList_Version_ID" + " AND pp.M_Product_ID=?" );

        if( onlyPOPriceList ) {
            sql.append( " AND pl.IsSOPriceList='N'" );
        }

        sql.append( " ORDER BY pl.IsSOPriceList ASC, plv.ValidFrom DESC" );

        int        C_Currency_ID = 0;
        BigDecimal PriceList     = null;
        BigDecimal PriceStd      = null;
        BigDecimal PriceLimit    = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,m_M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                C_Currency_ID = rs.getInt( 1 );
                PriceList     = rs.getBigDecimal( 2 );
                PriceStd      = rs.getBigDecimal( 3 );
                PriceLimit    = rs.getBigDecimal( 4 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        }

        // nothing found

        if( C_Currency_ID == 0 ) {
            return null;
        }

        BigDecimal price = PriceLimit;    // best bet

        if( (price == null) || price.equals( Env.ZERO )) {
            price = PriceStd;
        }

        if( (price == null) || price.equals( Env.ZERO )) {
            price = PriceList;
        }

        // Convert

        if( (price != null) &&!price.equals( Env.ZERO )) {
            price = MConversionRate.convert( as.getCtx(),price,C_Currency_ID,as.getC_Currency_ID(),as.getAD_Client_ID(),0 );
        }

        return price;
    }    // getPOPrice

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    private BigDecimal getPOCost( MAcctSchema as ) {
        String sql = "SELECT C_Currency_ID, PriceList,PricePO,PriceLastPO " + "FROM M_Product_PO WHERE M_Product_ID=? " + "ORDER BY IsCurrentVendor DESC";
        int        C_Currency_ID = 0;
        BigDecimal PriceList     = null;
        BigDecimal PricePO       = null;
        BigDecimal PriceLastPO   = null;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                C_Currency_ID = rs.getInt( 1 );
                PriceList     = rs.getBigDecimal( 2 );
                PricePO       = rs.getBigDecimal( 3 );
                PriceLastPO   = rs.getBigDecimal( 4 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        // nothing found

        if( C_Currency_ID == 0 ) {
            return null;
        }

        BigDecimal cost = PriceLastPO;    // best bet

        if( (cost == null) || cost.equals( Env.ZERO )) {
            cost = PricePO;
        }

        if( (cost == null) || cost.equals( Env.ZERO )) {
            cost = PriceList;
        }

        // Convert - standard precision!! - should be costing precision

        if( (cost != null) &&!cost.equals( Env.ZERO )) {
            cost = MConversionRate.convert( as.getCtx(),cost,C_Currency_ID,as.getC_Currency_ID(),m_AD_Client_ID,m_AD_Org_ID );
        }

        return cost;
    }    // getPOCost

//       begin e-evolution vpj-cd  09/12/2004

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param costType
     * @param m_warehouse_id
     *
     * @return
     */

    public BigDecimal getProductItemCost( MAcctSchema as,String costType,int m_warehouse_id ) {
    	
	    // ANTONIO: Cuando pide costos con WareHouse, toma la tabla:MPC_Cost_Group_ID que al parecer no tiene nada.
	    // En cambio cuando lo pide sin el Warehouse, usa M_product_costing que si tiene datos...
   		return getProductItemCost( as,costType);
/*	    
        BigDecimal current = null;
        BigDecimal cost    = null;
        String     cm      = as.getCostingMethod();

        // get Resource for this warehouse

        int S_Resource_ID = DB.getSQLValue( "S_Resource","SELECT r.S_Resource_ID FROM S_Resource r WHERE r.IsManufacturingResource = 'Y' AND r.ManufacturingResourceType = 'PT' AND  r.M_Warehouse_ID = ?",m_warehouse_id );
        int    MPC_Cost_Group_ID = 0;
        String sql               = "SELECT cg.MPC_Cost_Group_ID FROM MPC_Cost_Group cg WHERE cg.isGL = 'Y'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );
            ResultSet         rs    = pstmt.executeQuery();

            if( rs.next()) {
                MPC_Cost_Group_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getPOCost",ex );
        }

        // MMPCProductCosting[] getElements
        // org.openXpertya.mfg.model.MMPCProductCosting[] pc = null;
        // pc = org.openXpertya.mfg.model.MMPCProductCosting.getElements(m_M_Product_ID, as.getC_AcctSchema_ID() , MPC_Cost_Group_ID , m_warehouse_id , S_Resource_ID);

        sql = "SELECT CostLLAmt, CostTLAmt FROM MPC_Product_Costing WHERE M_Product_ID=" + m_M_Product_ID + " AND  C_AcctSchema_ID =" + as.getC_AcctSchema_ID() + " AND MPC_Cost_Group_ID =" + MPC_Cost_Group_ID + " AND M_Warehouse_ID =" + m_warehouse_id + " AND S_Resource_ID =" + S_Resource_ID;
        System.out.println( "query ********** " + sql );
        System.out.println( "VARIABLES ********** " + m_M_Product_ID + " : " + as.getC_AcctSchema_ID() + " : " + MPC_Cost_Group_ID + " : " + m_warehouse_id + " : " + S_Resource_ID );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            // pstmtpc.setInt(1, m_M_Product_ID);
            // pstmtpc.setInt(2, as.getC_AcctSchema_ID());
            // pstmtpc.setInt(3, MPC_Cost_Group_ID);
            // pstmtpc.setInt(4, m_warehouse_id);
            // pstmtpc.setInt(5, S_Resource_ID);

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                if( cost == null ) {
                    cost = Env.ZERO;
                }

                cost = cost.add( rs.getBigDecimal( 1 ).add( rs.getBigDecimal( 2 )));
                System.out.println( "COSTO que obtiene del Ciclo ********** " + cost );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getPOCost",ex );
        }
*/
        // System.out.println("COSTO al salir del ciclo ********** " +cost);
        // if(pc != null)
        // {
        // for (int i = 0 ; i < pc.length ; i++)
        // {
        // cost = cost.add(pc[i].getCostLLAmt().add(pc[i].getCostTLAmt()));
        // }
        //
        // }

        /*
         * StringBuffer sql = new StringBuffer("SELECT CurrentCostPrice,");   //      1
         * /
         * if ((costType == null && MAcctSchema.COSTINGMETHOD_Average.equals(cm))
         *            || MAcctSchema.COSTINGMETHOD_Average.equals(costType))
         *            sql.append("COSTAVERAGE");                                                                             //      2
         *            //     else if (AcctSchema.COSTING_FIFO.equals(cm))
         *            //             sql.append("COSTFIFO");
         *            //     else if (AcctSchema.COSTING_LIFO.equals(cm))
         * // sql.append("COSTLIFO");
         *    else if ((costType == null && MAcctSchema.COSTINGMETHOD_LastPOPrice.equals(cm))
         *            || MAcctSchema.COSTINGMETHOD_LastPOPrice.equals(costType))
         *        sql.append("PRICELASTPO");
         *    else    //  AcctSchema.COSTING_STANDARD
         *    sql.append("COSTSTANDARD");
         *    sql.append(" FROM M_Product_Costing WHERE M_Product_ID=? AND C_AcctSchema_ID=?");
         *
         * try
         *    {
         *        PreparedStatement pstmt = DB.prepareStatement(sql.toString());
         *        pstmt.setInt(1, m_M_Product_ID);
         *        pstmt.setInt(2, as.getC_AcctSchema_ID());
         *        ResultSet rs = pstmt.executeQuery();
         *        if (rs.next())
         *        {
         *               current = rs.getBigDecimal(1);
         *               cost = rs.getBigDecimal(2);
         *        }
         *        rs.close();
         *        pstmt.close();
         *    }
         *    catch (SQLException e)
         *    {
         *    log.error("getProductItemCost", e);
         *    }
         */

        // Return Costs
/*
        System.out.println( "COSttype" + costType + " cost " + cost + " ********** " + cost );

        if( (costType != null) && (cost != null) &&!cost.equals( Env.ZERO )) {
            log.fine( "getProductItemCosts = " + cost );

            return cost;
        } else if( (current != null) &&!current.equals( Env.ZERO )) {
            log.fine( "getProductItemCosts - Current=" + current );

            return current;
        }

        // Create/Update Cost Record

        boolean create = ( (cost == null) && (current == null) );

        if( cost != null ) {
            if( cost.compareTo( Env.ZERO ) == 0 ) {
                return updateCosts( as,create,m_warehouse_id,S_Resource_ID,MPC_Cost_Group_ID );
            } else {
                return cost;
            }
        } else {
            return updateCosts( as,create,m_warehouse_id,S_Resource_ID,MPC_Cost_Group_ID );
        }
*/        
    }    // getProductCost

    // end e-evolution vpj-cd  09/12/2004

    // begin e-evolution vpj-cd  09/12/2004

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param create
     * @param m_warehouse_id
     * @param S_Resource_ID
     * @param m_MPC_Cost_Group_ID
     *
     * @return
     */

    private BigDecimal updateCosts( MAcctSchema as,boolean create,int m_warehouse_id,int S_Resource_ID,int m_MPC_Cost_Group_ID ) {
        int        m_MPC_Cost_Element_ID = 0;
        int        currentnext           = 0;
        BigDecimal costs                 = Env.ZERO;

        try {
            String sqlCE = "SELECT MPC_Cost_Element_ID FROM MPC_Cost_Element WHERE AD_Client_ID= " + m_AD_Client_ID;
            PreparedStatement pstmtCE = DB.prepareStatement( sqlCE );
            ResultSet         rsCE    = pstmtCE.executeQuery();

            while( rsCE.next()) {

                // Create Zero Record

                if( create ) {
                    String sql1 = "SELECT s.currentnext FROM AD_Sequence s WHERE s.Name =(SELECT t.TableName FROM t.AD_Table " + "WHERE t.AD_Table_ID=1000022)";
                    PreparedStatement pstmt1 = DB.prepareStatement( sql1 );
                    ResultSet         rs1    = pstmt1.executeQuery();

                    if( rs1.next()) {
                        currentnext = rs1.getInt( 1 );
                    }

                    rs1.close();
                    pstmt1.close();

                    StringBuffer sql = new StringBuffer( "INSERT INTO MPC_Product_Costing " + "(M_Product_ID,C_AcctSchema_ID,MPC_Product_Costing_ID," + " AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy," + " MPC_Cost_Group_ID,MPC_Cost_Element_ID,CostTLAmt,CostLLAmt," + " CostCumAmt,CostCumQty," + " M_Warehouse_ID,S_Resource_ID) " + "VALUES (" );

                    sql.append( m_M_Product_ID ).append( "," ).append( as.getC_AcctSchema_ID()).append( "," ).append( currentnext ).append( "," ).append( m_AD_Client_ID ).append( "," ).append( m_AD_Org_ID ).append( "," ).append( "'Y',SysDate,0,SysDate,0," ).append( m_MPC_Cost_Group_ID ).append( "," ).append( rsCE.getInt( 1 )).append( ",0,0,0,0," ).append( m_warehouse_id ).append( "," ).append( S_Resource_ID ).append( ")" );

                    int no = DB.executeUpdate( sql.toString(),m_trxName );

                    if( no == 1 ) {
                        log.fine( "updateCosts - CostingCreated" );
                    }

                    return costs;
                }
            }

            rsCE.close();
            pstmtCE.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"updateCosts",ex );
        }

        boolean m_ispurchased = false;

        try {
            String sqlp = "SELECT IsPurchased FROM M_Product WHERE AD_Client_ID= " + m_AD_Client_ID + " AND M_Product_ID=" + m_M_Product_ID;
            PreparedStatement pstmtp = DB.prepareStatement( sqlp );
            ResultSet         rsp    = pstmtp.executeQuery();

            if( rsp.next()) {
                if( rsp.getString( 1 ).equals( "Y" )) {
                    m_ispurchased = true;
                } else {
                    m_ispurchased = false;
                }
            }

            rsp.close();
            pstmtp.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"updateCosts",ex );
        }

        if( m_ispurchased ) {

            // Try to find non ZERO Price

            String costSource = "PriceList-PO";

            costs = getPriceList( as,true );

            if( (costs == null) || costs.equals( Env.ZERO )) {
                costSource = "PO Cost";
                costs      = getPOCost( as );
            }

            if( (costs == null) || costs.equals( Env.ZERO )) {
                costSource = "PriceList";
                costs      = getPriceList( as,false );
            }

            // if not found use $1 (to be able to do material transactions)

            if( (costs == null) || costs.equals( Env.ZERO )) {
                costSource = "Not Found";
                costs      = new BigDecimal( "1" );
            }

            // update current costs

            StringBuffer sql = new StringBuffer( "UPDATE MPC_Product_Costing " );

            sql.append( "SET CostTLAmt=" ).append( costs ).append( " WHERE M_Product_ID=" ).append( m_M_Product_ID ).append( " AND C_AcctSchema_ID=" ).append( as.getC_AcctSchema_ID()).append( " AND M_Warehouse_ID=" ).append( m_warehouse_id ).append( " AND MPC_Cost_Group_ID=" ).append( m_MPC_Cost_Group_ID ).append( " AND MPC_Cost_Element_ID=1000000" ).append( " AND S_Resource_ID=" ).append( S_Resource_ID );

            int no = DB.executeUpdate( sql.toString(),m_trxName );

            if( no == 1 ) {
                log.fine( "updateCosts - " + costSource + " - " + costs );
            }

            return costs;
        }

        return costs;
    }    // createCosts

    // end e-evolution vpj-cd  09/12/2004

    // begin fjv e-evolution

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param M_Warehouse_ID
     *
     * @return
     */

    public BigDecimal getProductCosts( MAcctSchema as,int M_Warehouse_ID ) {
        if( m_qty == null ) {
            log.fine( "getProductCosts - No Qty" );

            return null;
        }

        BigDecimal cost = getProductItemCost( as,null,M_Warehouse_ID );

//         System.out.println( "COSTO que regresa el metodo getProductItemCost *******" + cost + " cantidad " + m_qty );

        if( cost == null ) {
            log.fine( "getProductCosts - No Costs" );

            return null;
        }

        log.fine( "getProductCosts - Qty(" + m_qty + ") * Cost(" + cost + ") = " + m_qty.multiply( cost ));

        return m_qty.multiply( cost );
    }    // getProductCosts

    // end fjv e-evolution

}    // ProductInfo



/*
 *  @(#)ProductInfo.java   24.03.06
 * 
 *  Fin del fichero ProductInfo.java
 *  
 *  Versión 2.2
 *
 */
