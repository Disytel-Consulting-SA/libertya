/*
 * @(#)MMPCOrder.java   13.jun 2007  Versión 2.2
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

import java.util.logging.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;
import org.openXpertya.wf.*;

/**
 *  Order Model.
 *      Please do not set DocStatus and C_DocType_ID directly.
 *      They are set in the process() method.
 *      Use DocAction and C_DocTypeTarget_ID instead.
 *
 *  @author Fundesle
 *  @version $Id: MOrder.java,v 2.0 $
 */
public class MMPCOrder extends X_MPC_Order implements DocAction {

    /**
     *  Create new Order by copying
     *  @param ctx context
     *  @param C_Order_ID invoice
     *
     * @param from
     *  @param dateDoc date of the document date
     * @param C_DocTypeTarget_ID
     * @param isSOTrx
     *  @param counter create counter links
     *  @return Order
     */
    public static MMPCOrder copyFrom(MMPCOrder from, Timestamp dateDoc, int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter) {

        MMPCOrder	to	= new MMPCOrder(from.getCtx(), 0, "MPC_Order");

        PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
        to.setMPC_Order_ID(0);
        to.set_ValueNoCheck("DocumentNo", null);

        //
        to.setDocStatus(DOCSTATUS_Drafted);	// Draft
        to.setDocAction(DOCACTION_Complete);

        //
        // to.setC_DocType_ID(this.C_DOCTYPE_ID_ManufacturingOrder);
        // to.setC_DocTypeTarget_ID(C_DocTypeTarget_ID);
        to.setIsSOTrx(isSOTrx);

        //
        to.setIsSelected(false);

        /*
         * to.setDateOrdered (dateDoc);
         * to.setDateAcct (dateDoc);
         * to.setDatePromised (dateDoc);
         * to.setDatePrinted(null);
         * to.setIsPrinted (false);
         * //
         */
        to.setIsApproved(false);

        /*
         * to.setIsCreditApproved(false);
         * to.setC_Payment_ID(0);
         * to.setC_CashLine_ID(0);
         * //      Amounts are updated  when adding lines
         * to.setGrandTotal(Env.ZERO);
         * to.setTotalLines(Env.ZERO);
         * /
         * to.setIsDelivered(false);
         * to.setIsInvoiced(false);
         * to.setIsSelfService(false);
         * to.setIsTransferred (false);
         */
        to.setPosted(false);
        to.setProcessed(false);

        /*
         * if (counter)
         *       to.setRef_Order_ID(from.getC_Order_ID());
         * else
         *       to.setRef_Order_ID(0);
         * /
         */
        if (!to.save()) {
            throw new IllegalStateException("Could not create Order");
        }

        /*
         * if (counter)
         *       from.setRef_Order_ID(to.getC_Order_ID());
         *
         * if (to.copyLinesFrom(from, counter) == 0)
         *       throw new IllegalStateException("Could not create Order Lines");
         */
        return to;

    }		// copyFrom

    /**
     *  Default Constructor
     *  @param ctx context
     *  @param  C_Order_ID    order to load, (0 create new order)
     * @param MPC_Order_ID
     * @param trxName
     */
    public MMPCOrder(Properties ctx, int MPC_Order_ID, String trxName) {

        super(ctx, MPC_Order_ID, trxName);

        // New
        if (MPC_Order_ID == 0) {

            setDocStatus(DOCSTATUS_Drafted);
            setDocAction(DOCACTION_Prepare);
            setC_DocType_ID(0);
            set_ValueNoCheck("DocumentNo", null);

            // setC_DocTypeTarget_ID(1000005);

            //

            /*
             * setDeliveryRule (DELIVERYRULE_Availability);
             * setFreightCostRule (FREIGHTCOSTRULE_FreightIncluded);
             * setInvoiceRule (INVOICERULE_Immediate);
             * setPaymentRule(PAYMENTRULE_OnCredit);
             * setPriorityRule (PRIORITYRULE_Medium);
             * setDeliveryViaRule (DELIVERYVIARULE_Pickup);
             * /
             * setIsDiscountPrinted (false);
             * setIsSelected (false);
             * setIsTaxIncluded (false);
             */
            setIsSOTrx(false);

            /*
             * setIsDropShip(false);
             * setSendEMail (false);
             * //
             */
            setIsApproved(false);
            setIsPrinted(false);

            /*
             * setIsCreditApproved(false);
             * setIsDelivered(false);
             * setIsInvoiced(false);
             * setIsTransferred(false);
             * setIsSelfService(false);
             * //
             */
            setProcessed(false);
            setProcessing(false);
            setPosted(false);

            /*
             * setDateAcct (new Timestamp(System.currentTimeMillis()));
             * setDatePromised (new Timestamp(System.currentTimeMillis()));
             * setDateOrdered (new Timestamp(System.currentTimeMillis()));
             *
             * setFreightAmt (Env.ZERO);
             * setChargeAmt (Env.ZERO);
             * setTotalLines (Env.ZERO);
             * setGrandTotal (Env.ZERO);
             */
        }

    }		// MPC_Order

    /**
     *  Project Constructor
     *  @param  project Project to create Order from
     * @param IsSOTrx
     *  @param  DocSubTypeSO if SO DocType Target (default DocSubTypeSO_OnCredit)
     */
    public MMPCOrder(MProject project, boolean IsSOTrx, String DocSubTypeSO) {

        this(project.getCtx(), 0, "MPC_Order");
        setAD_Client_ID(project.getAD_Client_ID());
        setAD_Org_ID(project.getAD_Org_ID());
        setC_Campaign_ID(project.getC_Campaign_ID());

        // setC_DocTypeTarget_ID(1000005);
        // setSalesRep_ID(project.getSalesRep_ID());
        //
        setC_Project_ID(project.getC_Project_ID());
        setDescription(project.getName());

        Timestamp	ts	= project.getDateContract();

        if (ts != null) {
            setDateOrdered(ts);
        }

        ts	= project.getDateFinish();

        if (ts != null) {
            setDatePromised(ts);
        }

        //
        // setC_BPartner_ID(project.getC_BPartner_ID());
        // setC_BPartner_Location_ID(project.getC_BPartner_Location_ID());
        // setAD_User_ID(project.getAD_User_ID());
        //
        setM_Warehouse_ID(project.getM_Warehouse_ID());

        /*
         * setM_PriceList_ID(project.getM_PriceList_ID());
         * setC_PaymentTerm_ID(project.getC_PaymentTerm_ID());
         * //
         */
        setIsSOTrx(IsSOTrx);

        /*
         * if (IsSOTrx) {
         *   if (DocSubTypeSO == null || DocSubTypeSO.length() == 0)
         *       setC_DocTypeTarget_ID(DocSubTypeSO_OnCredit);
         *   else
         *       setC_DocTypeTarget_ID(DocSubTypeSO);
         * }
         * else]
         */

        // setC_DocTypeTarget_ID();

    }		// MOrder

    /**
     *  Load Constructor
     *  @param ctx context
     *  @param rs result set record
     * @param trxName
     */
    public MMPCOrder(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MOrder

    /** Order Lines */
    private MMPCOrderBOMLine[]	m_order_bomlines	= null;

    /**
     *  Overwrite Client/Org if required
     *  @param AD_Client_ID client
     *  @param AD_Org_ID org
     */
    public void setClientOrg(int AD_Client_ID, int AD_Org_ID) {
        super.setClientOrg(AD_Client_ID, AD_Org_ID);
    }		// setClientOrg

    /**
     *  Set AD_Org_ID
     *  @param AD_Org_ID Org ID
     */
    public void setAD_Org_ID(int AD_Org_ID) {
        super.setAD_Org_ID(AD_Org_ID);
    }		// setAD_Org_ID

    /**
     *  Set C_Resource
     *  @param C_Resource Plant
     *
     * @param M_Warehouse_ID
     */

    /*
     * public void setC_Resource_ID (int C_Resource_ID)
     * {
     *       super.setC_Resource_ID (C_Resource_ID);
     * }       //      C_Resource Plant
     */

    /**
     *  Set Warehouse
     *  @param M_Warehouse_ID warehouse
     */
    public void setM_Warehouse_ID(int M_Warehouse_ID) {
        super.setM_Warehouse_ID(M_Warehouse_ID);
    }		// setM_Warehouse_ID

    /**
     * **********************************************************************
     *
     * @return
     */

    /*
     * public static final String              DocSubTypeSO_Standard = "SO";
     * public static final String              DocSubTypeSO_Quotation = "OB";
     * public static final String              DocSubTypeSO_Proposal = "ON";
     * public static final String              DocSubTypeSO_Prepay = "PR";
     * public static final String              DocSubTypeSO_POS = "WR";
     * public static final String              DocSubTypeSO_Warehouse = "WP";
     * public static final String              DocSubTypeSO_OnCredit = "WI";
     * public static final String              DocSubTypeSO_RMA = "RM";
     */

    /**
     *  Set Target Sales Document Type
     *  @param DocSubTypeSO_x SO sub type - see DocSubTypeSO_
     */

    /*
     * public void setC_DocTypeTarget_ID(String DocSubTypeSO_x) {
     *   String sql = "SELECT C_DocType_ID FROM C_DocType WHERE AD_Client_ID=? AND DocSubTypeSO=? ORDER BY IsDefault DESC";
     *   int C_DocType_ID = DB.getSQLValue(sql, getAD_Client_ID(), DocSubTypeSO_x);
     *   if (C_DocType_ID <= 0)
     *       log.log(Level.SEVERE ,("setC_DocTypeTarget_ID - Not found for AD_Client_ID=" + getAD_Client_ID() + ", SubType=" + DocSubTypeSO_x);
     *   else {
     *       log.fine("setC_DocTypeTarget_ID - " + DocSubTypeSO_x);
     *       setC_DocTypeTarget_ID(C_DocType_ID);
     *       setIsSOTrx(true);
     *   }
     * }   //      setC_DocTypeTarget_ID
     */

    /**
     *  Set Target Document Type.
     *  Standard Order or PO
     */

    /*
     * public void setC_DocTypeTarget_ID() {
     *   if (isSOTrx())          //      SO = Std Order
     *   {
     *       //setC_DocTypeTarget_ID(DocSubTypeSO_Standard);
     *       return;
     *   }
     *   //      PO
     *   String sql = "SELECT C_DocType_ID FROM C_DocType WHERE AD_Client_ID=? AND DocBaseType='POO' ORDER BY IsDefault DESC";
     *   int C_DocType_ID = DB.getSQLValue(sql, getAD_Client_ID());
     *   if (C_DocType_ID <= 0)
     *       log.log(Level.SEVERE ,("setC_DocTypeTarget_ID - No POO found for AD_Client_ID=" + getAD_Client_ID());
     *   else {
     *       log.fine("setC_DocTypeTarget_ID (PO) - " + C_DocType_ID);
     *       setC_DocTypeTarget_ID(C_DocType_ID);
     *   }
     * }   //      setC_DocTypeTarget_ID
     */

    /**
     *  Copy Lines From other Order
     *  @param order order
     *  @param counter set counter info
     *  @return number of lines copied
     */

    /*
     * public int copyMPC_Order_BOMLinesFrom(MPC_Order MPC_Order, boolean counter) {
     *   if (isProcessed() || isPosted() || MPC_Order == null)
     *       return 0;
     *   MPC_Order_BOMLine[] fromLines = MPC_Order.getLines(false);
     *   int count = 0;
     *   for (int i = 0; i < fromLines.length; i++) {
     *       MPC_Order_BOMLine line = new MPC_Order_BOMLine(this);
     *       PO.copyValues(fromLines[i], line, getAD_Client_ID(), getAD_Org_ID());
     *       line.setMPC_Order_ID(getMPC_Order_ID());
     *       //line.setOrder(order);
     *       line.setLine(0);
     *       line.setM_AttributeSetInstance_ID(0);
     *       line.setS_ResourceAssignment_ID(0);
     *       /
     *                   /*line.setQtyDelivered(Env.ZERO);
     *                   line.setQtyInvoiced(Env.ZERO);
     *                   line.setQtyReserved(Env.ZERO);
     *                   line.setDateDelivered(null);
     *                   line.setDateInvoiced(null);
     *                   //      Tax
     *                   if (getC_BPartner_ID() != order.getC_BPartner_ID())
     *                           line.setTax();          //      recalculate
     *                   /
     *                   if (counter)
     *                           line.setRef_OrderLine_ID(fromLines[i].getC_OrderLine_ID());
     *                   else
     *                           line.setRef_OrderLine_ID(0);
     *
     *       line.setProcessed(false);
     *       if (line.save())
     *           count++;
     *       //  Cross Link
     *       if (counter) {
     *           fromLines[i].setRef_OrderLine_ID(line.getC_OrderLine_ID());
     *           fromLines[i].save();
     *       }
     *   }
     *   if (fromLines.length != count)
     *       log.log(Level.SEVERE ,("copyLinesFrom - Line difference - From=" + fromLines.length + " <> Saved=" + count);
     *   return count;
     * }   //      copyLinesFrom
     */

    /**
     *  String Representation
     *  @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MMPCOrder[").append(getID()).append("-").append(getDocumentNo()).append(",IsSOTrx=").append(isSOTrx()).append(",C_DocType_ID=").append(getC_DocType_ID()).append("]");

        return sb.toString();

    }		// toString

    /**
     *  Get Lines of Order
     *  @param whereClause where clause or null (starting with AND)
     * @param orderClause
     *  @return invoices
     */
    public MMPCOrderBOMLine[] getLines(String whereClause, String orderClause) {

        ArrayList	list	= new ArrayList();
        StringBuffer	sql	= new StringBuffer("SELECT * FROM MPC_Order_BOMLine WHERE MPC_Order_ID=? ");

        if (whereClause != null) {
            sql.append(whereClause);
        }

        if (orderClause != null) {
            sql.append(" ").append(orderClause);
        }

        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql.toString());
            pstmt.setInt(1, getMPC_Order_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MMPCOrderBOMLine(getCtx(), rs, "MPC_Order_BOM_Line"));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getLines - " + sql, e);
        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (Exception e) {}

            pstmt	= null;
        }

        //
        MMPCOrderBOMLine[]	lines	= new MMPCOrderBOMLine[list.size()];

        list.toArray(lines);

        return lines;

    }		// getLines

    /**
     *      Get Lines of Order
     *      @param requery requery
     *      @param orderBy optional order by column
     *      @return lines
     */
    public MMPCOrderBOMLine[] getLines(boolean requery, String orderBy) {

        if ((m_order_bomlines != null) &&!requery) {
            return m_order_bomlines;
        }

        //
        String	orderClause	= "ORDER BY ";

        if ((orderBy != null) && (orderBy.length() > 0)) {
            orderClause	+= orderBy;
        } else {
            orderClause	+= "Line";
        }

        m_order_bomlines	= getLines(null, orderClause);

        return m_order_bomlines;

    }		// getLines

    /**
     *  Set Processed.
     *  Propergate to Lines/Taxes
     *  @param processed processed
     */
    public void setProcessed(boolean processed) {

        super.setProcessed(processed);

        if (getID() == 0) {
            return;
        }

        String	set	= "SET Processed='" + (processed
                ? "Y"
                : "N") + "' WHERE MPC_Order_ID=" + getMPC_Order_ID();
        int	noLine	= DB.executeUpdate("UPDATE MPC_Order " + set);

        // int noTax = DB.executeUpdate("UPDATE C_OrderTax " + set);
        // m_lines = null;
        // m_taxes = null;
        // log.fine("setProcessed - " + processed + " - Lines=" + noLine + ", Tax=" + noTax);

    }		// setProcessed

    /**
     *  Before Save
     *  @param newRecord new
     *  @return save
     */
    protected boolean beforeSave(boolean newRecord) {

        if (newRecord) {

            // make sure DocType set to 0
            if (getC_DocType_ID() == 0) {
                setC_DocType_ID(0);
            }

            setDocStatus(DocumentEngine.STATUS_NotApproved);
            setDocAction(DocumentEngine.ACTION_Void);
        }

        // Client/Org Check
        if (getAD_Client_ID() == 0) {

            m_processMsg	= "AD_Client_ID = 0";

            return false;
        }

        if (getAD_Org_ID() == 0) {

            int	context_AD_Org_ID	= Env.getAD_Org_ID(getCtx());

            if (context_AD_Org_ID == 0) {

                m_processMsg	= "AD_Org_ID = 0";

                return false;
            }

            setAD_Org_ID(context_AD_Org_ID);
            log.warning("beforeSave - Changed Org to Context=" + context_AD_Org_ID);
        }

        /*
         *       //      No Partner Info - set Template
         *       if (getC_BPartner_ID() == 0)
         *               setBPartner(MBPartner.getTemplate(getCtx(), getAD_Client_ID()));
         *       if (getC_BPartner_Location_ID() == 0)
         *               setBPartner(new MBPartner(getCtx(), getC_BPartner_ID()));
         *       //      No Bill - get from Ship
         *       if (getBill_BPartner_ID() == 0)
         *       {
         *               setBill_BPartner_ID(getC_BPartner_ID());
         *               setBill_Location_ID(getC_BPartner_Location_ID());
         *       }
         *       if (getBill_Location_ID() == 0)
         *               setBill_Location_ID(getC_BPartner_Location_ID());
         *
         *       //      Price List
         *       if (getM_PriceList_ID() == 0)
         *       {
         *               int ii = DB.getSQLValue(
         *                       "SELECT M_PriceList_ID FROM M_PriceList "
         *                       + "WHERE AD_Client_ID=? AND IsSOPriceList=? "
         *                       + "ORDER BY IsDefault DESC", getAD_Client_ID(), isSOTrx() ? "Y" : "N");
         *               if (ii != 0)
         *                       setM_PriceList_ID (ii);
         *       }
         *       //      Currency
         *       if (getC_Currency_ID() == 0)
         *       {
         *               String sql = "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID=?";
         *               int ii = DB.getSQLValue (sql, getM_PriceList_ID());
         *               if (ii != 0)
         *                       setC_Currency_ID (ii);
         *               else
         *                       setC_Currency_ID(Env.getContextAsInt(getCtx(), "#C_Currency_ID"));
         *       }
         *
         *       //      Sales Rep
         *       if (getSalesRep_ID() == 0)
         *       {
         *               int ii = Env.getContextAsInt(getCtx(), "#SalesRep_ID");
         *               if (ii != 0)
         *                       setSalesRep_ID (ii);
         *       }
         *
         *       //      Document Type
         *       if (getC_DocTypeTarget_ID() == 0)
         *               setC_DocTypeTarget_ID(DocSubTypeSO_Standard);
         *
         *       //      Payment Term
         *       if (getC_PaymentTerm_ID() == 0)
         *       {
         *               int ii = Env.getContextAsInt(getCtx(), "#C_PaymentTerm_ID");
         *               if (ii != 0)
         *                       setC_PaymentTerm_ID(ii);
         *               else
         *               {
         *                       String sql = "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE AD_Client_ID=? AND IsDefault='Y'";
         *                       ii = DB.getSQLValue(sql, getAD_Client_ID());
         *                       if (ii != 0)
         *                               setC_PaymentTerm_ID (ii);
         *               }
         *       }
         */
        if (getM_Warehouse_ID() == 0) {

            int	ii	= Env.getContextAsInt(getCtx(), "#M_Warehouse_ID");

            if (ii != 0) {
                setM_Warehouse_ID(ii);
            }
        }

        return true;

    }		// beforeSave

    /**
     *  After Save
     *  @param newRecord new
     *  @param success success
     *
     * @return
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        X_MPC_Order	MPC_Order	= new X_MPC_Order(getCtx(), getMPC_Order_ID(), null);

        MMPCMRP.MPC_Order(MPC_Order, get_TrxName());
        log.fine("afterSave - MMPCOrder Query ok");

        if (!newRecord) {
            return success;
        }

        setC_DocType_ID(0);

        // Create BOM Head
        MMPCOrderBOM	MPC_Order_BOM	= new MMPCOrderBOM(getCtx(), 0, null);
        MMPCProductBOM	MPC_Product_BOM	= new MMPCProductBOM(getCtx(), getMPC_Product_BOM_ID(), null);

        MPC_Order_BOM.setMPC_Order_ID(getMPC_Order_ID());

        // MPC_Order_BOM.setMPC_Product_BOM_ID(MPC_Product_BOM.getMPC_Product_BOM_ID());
        MPC_Order_BOM.setBOMType(MPC_Product_BOM.getBOMType());
        MPC_Order_BOM.setDescription(MPC_Product_BOM.getDescription());
        MPC_Order_BOM.setM_AttributeSetInstance_ID(MPC_Product_BOM.getM_AttributeSetInstance_ID());
        MPC_Order_BOM.setM_Product_ID(MPC_Product_BOM.getM_Product_ID());
        MPC_Order_BOM.setName(MPC_Product_BOM.getName());
        MPC_Order_BOM.setRevision(MPC_Product_BOM.getRevision());
        MPC_Order_BOM.setValidFrom(MPC_Product_BOM.getValidFrom());
        MPC_Order_BOM.setValidTo(MPC_Product_BOM.getValidTo());
        MPC_Order_BOM.setValue(MPC_Product_BOM.getValue());
        MPC_Order_BOM.setDocumentNo(MPC_Product_BOM.getDocumentNo());
        MPC_Order_BOM.setC_UOM_ID(MPC_Product_BOM.getC_UOM_ID());
        MPC_Order_BOM.save(get_TrxName());

        // Create BOM List ---------------------------------------------------------
        MMPCProductBOMLine[]	MPC_Product_BOMline	= MPC_Product_BOM.getLines();

        for (int i = 0; i < MPC_Product_BOMline.length; i++) {

            // MMPCOrderBOMLine MPC_Order_BOMLine = new MMPCOrderBOMLine(getCtx(),0,trx.getTrxName());
            MMPCOrderBOMLine	MPC_Order_BOMLine	= new MMPCOrderBOMLine(getCtx(), 0, null);

            MPC_Order_BOMLine.setAssay(MPC_Product_BOMline[i].getAssay());
            MPC_Order_BOMLine.setQtyBatch(MPC_Product_BOMline[i].getQtyBatch());
            MPC_Order_BOMLine.setQtyBOM(MPC_Product_BOMline[i].getQtyBOM());
            MPC_Order_BOMLine.setIsQtyPercentage(MPC_Product_BOMline[i].isQtyPercentage());
            MPC_Order_BOMLine.setComponentType(MPC_Product_BOMline[i].getComponentType());
            MPC_Order_BOMLine.setC_UOM_ID(MPC_Product_BOMline[i].getC_UOM_ID());
            MPC_Order_BOMLine.setForecast(MPC_Product_BOMline[i].getForecast());
            MPC_Order_BOMLine.setIsCritical(MPC_Product_BOMline[i].isCritical());
            MPC_Order_BOMLine.setIssueMethod(MPC_Product_BOMline[i].getIssueMethod());
            MPC_Order_BOMLine.setLine(MPC_Product_BOMline[i].getLine());
            MPC_Order_BOMLine.setLTOffSet(MPC_Product_BOMline[i].getLTOffSet());
            MPC_Order_BOMLine.setM_AttributeSetInstance_ID(MPC_Product_BOMline[i].getM_AttributeSetInstance_ID());
            MPC_Order_BOMLine.setMPC_Order_BOM_ID(MPC_Order_BOM.getMPC_Order_BOM_ID());
            MPC_Order_BOMLine.setMPC_Order_ID(getMPC_Order_ID());
            MPC_Order_BOMLine.setM_Product_ID(MPC_Product_BOMline[i].getM_Product_ID());
            MPC_Order_BOMLine.setScrap(MPC_Product_BOMline[i].getScrap());
            MPC_Order_BOMLine.setValidFrom(MPC_Product_BOMline[i].getValidFrom());
            MPC_Order_BOMLine.setValidTo(MPC_Product_BOMline[i].getValidTo());
            MPC_Order_BOMLine.setM_Warehouse_ID(getM_Warehouse_ID());

            if (getQtyOrdered().compareTo(Env.ZERO) == 0) {
                setQtyOrdered(getQtyEntered());
            }

            if (MPC_Order_BOMLine.isQtyPercentage()) {

                BigDecimal	qty	= MPC_Order_BOMLine.getQtyBatch().multiply(getQtyOrdered());

                if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Packing)) {
                    MPC_Order_BOMLine.setQtyRequiered(qty.divide(new BigDecimal(100), 0, qty.ROUND_UP));
                }

                if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Component) || MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Phantom)) {
                    MPC_Order_BOMLine.setQtyRequiered(qty.divide(new BigDecimal(100), 4, qty.ROUND_UP));
                } else if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Tools)) {
                    MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyBOM());
                }

            } else {

                if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Component) || MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Phantom)) {
                    MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyBOM().multiply(getQtyOrdered()));
                }

                if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Packing)) {
                    MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyBOM().multiply(getQtyOrdered()));
                } else if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Tools)) {
                    MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyBOM());
                }
            }

            // Set Scrap of Component
            BigDecimal	Scrap	= new BigDecimal(MPC_Order_BOMLine.getScrap());

            if (!Scrap.equals(Env.ZERO)) {

                Scrap	= Scrap.divide(new BigDecimal(100), 4, BigDecimal.ROUND_UP);
                MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyRequiered().divide(Env.ONE.subtract(Scrap), 4, BigDecimal.ROUND_HALF_UP));
            }

            MPC_Order_BOMLine.save(get_TrxName());

            if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Phantom)) {

                MPC_Order_BOMLine.setQtyRequiered(Env.ZERO);
                MPC_Order_BOMLine.save(get_TrxName());
            }
        }	// end Create Order BOM

        MMPCMRP.MPC_Order(MPC_Order, get_TrxName());

        // Create Workflow (Routing & Process
        MWorkflow	AD_Workflow	= new MWorkflow(getCtx(), getAD_Workflow_ID(), null);
        MMPCOrderWorkflow	MPC_Order_Workflow	= new MMPCOrderWorkflow(getCtx(), 0, null);

        MPC_Order_Workflow.setValue(AD_Workflow.getValue());
        MPC_Order_Workflow.setQtyBatchSize(AD_Workflow.getQtyBatchSize());
        MPC_Order_Workflow.setName(AD_Workflow.getName());
        MPC_Order_Workflow.setAccessLevel(AD_Workflow.getAccessLevel());
        MPC_Order_Workflow.setAuthor(AD_Workflow.getAuthor());
        MPC_Order_Workflow.setDurationUnit(AD_Workflow.getDurationUnit());
        MPC_Order_Workflow.setDuration(AD_Workflow.getDuration());
        MPC_Order_Workflow.setEntityType(AD_Workflow.getEntityType());		// U
        MPC_Order_Workflow.setIsDefault(AD_Workflow.isDefault());
        MPC_Order_Workflow.setPublishStatus(AD_Workflow.getPublishStatus());	// U
        MPC_Order_Workflow.setVersion(AD_Workflow.getVersion());
        MPC_Order_Workflow.setCost(AD_Workflow.getCost());
        MPC_Order_Workflow.setWaitingTime(AD_Workflow.getWaitingTime());
        MPC_Order_Workflow.setWorkingTime(AD_Workflow.getWorkingTime());
        MPC_Order_Workflow.setAD_WF_Responsible_ID(AD_Workflow.getAD_WF_Responsible_ID());
        MPC_Order_Workflow.setAD_Workflow_ID(AD_Workflow.getAD_Workflow_ID());
        MPC_Order_Workflow.setDurationLimit(AD_Workflow.getDurationLimit());
        MPC_Order_Workflow.setMPC_Order_ID(getMPC_Order_ID());
        MPC_Order_Workflow.setPriority(AD_Workflow.getPriority());
        MPC_Order_Workflow.setValidateWorkflow(AD_Workflow.getValidateWorkflow());
        MPC_Order_Workflow.save(get_TrxName());

        // MPC_Order_Workflow.set

        MWFNode[]	AD_WF_Node	= AD_Workflow.getNodes(false);

        if (AD_WF_Node != null) {

            for (int g = 0; g < AD_WF_Node.length; g++) {

                MMPCOrderNode	MPC_Order_Node	= new MMPCOrderNode(getCtx(), 0, null);

                MPC_Order_Node.setAction(AD_WF_Node[g].getAction());	// N
                MPC_Order_Node.setAD_WF_Node_ID(AD_WF_Node[g].getAD_WF_Node_ID());
                MPC_Order_Node.setAD_WF_Responsible_ID(AD_WF_Node[g].getAD_WF_Responsible_ID());
                MPC_Order_Node.setAD_Workflow_ID(AD_WF_Node[g].getAD_Workflow_ID());
                MPC_Order_Node.setCost(AD_WF_Node[g].getCost());
                MPC_Order_Node.setDuration(AD_WF_Node[g].getDuration());
                MPC_Order_Node.setEntityType(AD_WF_Node[g].getEntityType());
                MPC_Order_Node.setIsCentrallyMaintained(AD_WF_Node[g].isCentrallyMaintained());
                MPC_Order_Node.setJoinElement(AD_WF_Node[g].getJoinElement());		// X
                MPC_Order_Node.setDurationLimit(AD_WF_Node[g].getDurationLimit());
                MPC_Order_Node.setMPC_Order_ID(getMPC_Order_ID());
                MPC_Order_Node.setMPC_Order_Workflow_ID(MPC_Order_Workflow.getMPC_Order_Workflow_ID());
                MPC_Order_Node.setName(AD_WF_Node[g].getName());
                MPC_Order_Node.setPriority(AD_WF_Node[g].getPriority());
                MPC_Order_Node.setSplitElement(AD_WF_Node[g].getSplitElement());	// X
                MPC_Order_Node.setSubflowExecution(AD_WF_Node[g].getSubflowExecution());
                MPC_Order_Node.setValue(AD_WF_Node[g].getValue());
                MPC_Order_Node.setS_Resource_ID(AD_WF_Node[g].getS_Resource_ID());
                MPC_Order_Node.setSetupTime(AD_WF_Node[g].getSetupTime());
                MPC_Order_Node.setSetupTimeRequiered(AD_WF_Node[g].getSetupTime());

                BigDecimal	time	= new BigDecimal(AD_WF_Node[g].getDuration()).multiply(MPC_Order.getQtyOrdered());

                MPC_Order_Node.setDurationRequiered(time.intValue());
                MPC_Order_Node.setMovingTime(AD_WF_Node[g].getMovingTime());
                MPC_Order_Node.setWaitingTime(AD_WF_Node[g].getWaitingTime());
                MPC_Order_Node.setWorkingTime(AD_WF_Node[g].getWorkingTime());
                ;
                MPC_Order_Node.setQueuingTime(AD_WF_Node[g].getQueuingTime());
                MPC_Order_Node.setXPosition(AD_WF_Node[g].getXPosition());	// e-evolution generatemodel
                MPC_Order_Node.setYPosition(AD_WF_Node[g].getYPosition());	// e-evolution generatemodel
                MPC_Order_Node.save(get_TrxName());

                MWFNodeNext[]	AD_WF_NodeNext	= AD_WF_Node[g].getTransitions();

                System.out.println("AD_WF_NodeNext" + AD_WF_NodeNext.length);

                if (AD_WF_NodeNext != null) {

                    for (int n = 0; n < AD_WF_NodeNext.length; n++) {

                        MMPCOrderNodeNext	MPC_Order_NodeNext	= new MMPCOrderNodeNext(getCtx(), 0, null);

                        MPC_Order_NodeNext.setAD_WF_Node_ID(AD_WF_NodeNext[n].getAD_WF_Node_ID());
                        MPC_Order_NodeNext.setAD_WF_Next_ID(AD_WF_NodeNext[n].getAD_WF_Next_ID());
                        MPC_Order_NodeNext.setMPC_Order_Node_ID(MPC_Order_Node.getMPC_Order_Node_ID());
                        MPC_Order_NodeNext.setMPC_Order_Next_ID(0);
                        MPC_Order_NodeNext.setDescription(AD_WF_NodeNext[n].getDescription());
                        MPC_Order_NodeNext.setEntityType(AD_WF_NodeNext[n].getEntityType());
                        MPC_Order_NodeNext.setIsStdUserWorkflow(AD_WF_NodeNext[n].isStdUserWorkflow());
                        MPC_Order_NodeNext.setMPC_Order_ID(getMPC_Order_ID());
                        MPC_Order_NodeNext.setSeqNo(AD_WF_NodeNext[n].getSeqNo());
                        MPC_Order_NodeNext.setTransitionCode(AD_WF_NodeNext[n].getTransitionCode());
                        MPC_Order_NodeNext.save(get_TrxName());

                    }		// end for Node Next
                }

            }			// end for Node

            // set transition for order
            MMPCOrderWorkflow	OrderWorkflow	= new MMPCOrderWorkflow(getCtx(), MPC_Order_Workflow.getMPC_Order_Workflow_ID(), null);
            MMPCOrderNode[]	OrderNodes	= OrderWorkflow.getNodes(false);

            System.out.println("OrderNodes" + OrderNodes.length);

            if (OrderNodes != null) {

                OrderWorkflow.setMPC_Order_Node_ID(OrderNodes[0].getMPC_Order_Node_ID());
                OrderWorkflow.save(get_TrxName());

                for (int g = 0; g < OrderNodes.length; g++) {

                    MMPCOrderNodeNext[]	nexts	= OrderNodes[g].getTransitions();

                    System.out.println("MPC_Order_NodeNext" + nexts.length);

                    if (nexts != null) {

                        for (int n = 0; n < nexts.length; n++) {

                            String	sql	= "SELECT MPC_Order_Node_ID FROM MPC_Order_Node WHERE MPC_Order_ID = ?  AND AD_WF_Node_ID = ? ";

                            try {

                                PreparedStatement	pstmt	= null;

                                pstmt	= DB.prepareStatement(sql);
                                pstmt.setInt(1, nexts[n].getMPC_Order_ID());
                                pstmt.setInt(2, nexts[n].getAD_WF_Next_ID());

                                ResultSet	rs	= pstmt.executeQuery();

                                while (rs.next()) {

                                    nexts[n].setMPC_Order_Next_ID(rs.getInt(1));
                                    nexts[n].save(get_TrxName());
                                }

                                rs.close();
                                pstmt.close();

                            } catch (Exception e) {
                                log.log(Level.SEVERE, "doIt - " + sql, e);
                            }

                        }	// end for Node Next
                    }
                }
            }
        }

        return true;

    }		// afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    protected boolean beforeDelete() {

        // OrderBOMLine
        if (getDocAction().equals(DOCACTION_Void) && getDocStatus().equals(DOCSTATUS_NotApproved)) {

            String	sql	= "DELETE MPC_Order_Cost WHERE AD_Client_ID= " + getAD_Client_ID() + " AND AD_Org_ID = " + getAD_Org_ID() + "  AND MPC_Order_ID = " + getMPC_Order_ID();

            DB.executeUpdate(sql);
            sql	= "DELETE  MPC_Order_Node_Asset WHERE AD_Client_ID= " + getAD_Client_ID() + " AND AD_Org_ID = " + getAD_Org_ID() + " AND MPC_Order_ID = " + getMPC_Order_ID();
            DB.executeUpdate(sql);
            sql	= "DELETE  MPC_Order_NodeNext WHERE AD_Client_ID= " + getAD_Client_ID() + " AND AD_Org_ID = " + getAD_Org_ID() + "AND MPC_Order_ID = " + getMPC_Order_ID();
            DB.executeUpdate(sql);
            sql	= "DELETE MPC_Order_Node_Product WHERE AD_Client_ID= " + getAD_Client_ID() + " AND AD_Org_ID = " + getAD_Org_ID() + " AND MPC_Order_ID = " + getMPC_Order_ID();
            DB.executeUpdate(sql);

            // OrderBOMLine
            sql	= "DELETE MPC_Order_Node WHERE AD_Client_ID=  " + getAD_Client_ID() + "  AND AD_Org_ID =  " + getAD_Org_ID() + "  AND MPC_Order_ID = " + getMPC_Order_ID();
            DB.executeUpdate(sql);

            // OrderBOMLine
            sql	= "DELETE MPC_Order_Workflow WHERE AD_Client_ID=  " + getAD_Client_ID() + "  AND AD_Org_ID =  " + getAD_Org_ID() + "   AND MPC_Order_ID = " + getMPC_Order_ID();
            DB.executeUpdate(sql);

            // OrderBOMLine
            sql	= "DELETE MPC_Order_BOMLine  WHERE AD_Client_ID= " + getAD_Client_ID() + "  AND AD_Org_ID = " + getAD_Org_ID() + "  AND MPC_Order_ID =  " + getMPC_Order_ID();
            DB.executeUpdate(sql);

            // MRP Delete
            sql	= "DELETE MPC_MRP  WHERE AD_Client_ID= " + getAD_Client_ID() + "  AND AD_Org_ID = " + getAD_Org_ID() + "  AND MPC_Order_ID =  " + getMPC_Order_ID();
            DB.executeUpdate(sql);
        }

        return true;
    }		// beforeDelete

    /**
     *  Process Order - Start Process
     *
     * @param processAction
     *  @return true if ok
     */

    /*
     * public boolean processOrder(String docAction) {
     *   setDocAction(docAction);
     *   save();
     *   log.fine("processOrder - " + getDocAction());
     *   int AD_Process_ID = 104;        //      C_Order_Post
     *   MProcess pp = new MProcess(getCtx(), AD_Process_ID);
     *   boolean ok = pp.processIt(getMPC_Order_ID()).isOK();
     *   load();         //      reload
     *   //log.fine("processOrder - ok=" + ok + " - GrandTotal=" + getGrandTotal());
     *   return ok;
     * }   //      process
     */

    /**
     *  Process document
     *  @param processAction document action
     *  @return true if performed
     */
    public boolean processIt(String processAction) {

        m_processMsg	= null;

        DocumentEngine	engine	= new DocumentEngine(this, getDocStatus());

        return engine.processIt(processAction, getDocAction(), log);

    }		// processIt


    /** Just Prepared Flag */
    private boolean	m_justPrepared	= false;

    /**
     *  Unlock Document.
     *  @return true if success
     */
    public boolean unlockIt() {

        log.info("unlockIt - " + toString());
        setProcessing(false);

        return true;

    }		// unlockIt

    /**
     *  Invalidate Document
     *  @return true if success
     */
    public boolean invalidateIt() {

        log.info("invalidateIt - " + toString());
        setDocAction(DOCACTION_Prepare);

        return true;

    }		// invalidateIt

    /**
     *  Prepare Document
     *  @return new status (In Progress or Invalid)
     */
    public String prepareIt() {

        log.info("prepareIt - " + toString());

        if (getC_DocTypeTarget_ID() == MMPCMRP.getDocType("MOP", true)) {

            setC_DocTypeTarget_ID(MMPCMRP.getDocType("MOP", false));
            setC_DocType_ID(getC_DocTypeTarget_ID());
        }

        MDocType	dt		= MDocType.get(getCtx(), getC_DocTypeTarget_ID());
        Integer		DocumentNo	= new Integer(getMPC_Order_ID());

        setDocumentNo("<" + DocumentNo.toString() + ">");

        if (!orderStock()) {

            m_processMsg	= "Cannot Order Stock";

            return DocAction.STATUS_Invalid;
        }

        // Std Period open?

        /*
         * if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType()))
         * {
         *       m_processMsg = "@PeriodClosed@";
         *       return DocAction.STATUS_Invalid;
         * }
         */

        // Convert DocType
        if (getC_DocType_ID() != getC_DocTypeTarget_ID()) {

            // New or in Progress/Invalid
            if (DOCSTATUS_Drafted.equals(getDocStatus()) || DOCSTATUS_InProgress.equals(getDocStatus()) || DOCSTATUS_Invalid.equals(getDocStatus()) || (getC_DocType_ID() == 0)) {
                setC_DocType_ID(getC_DocTypeTarget_ID());
            } else	// convert only if offer
            {

                if (dt.isOffer()) {
                    setC_DocType_ID(getC_DocTypeTarget_ID());
                } else {

                    m_processMsg	= "@CannotChangeDocType@";

                    return DocAction.STATUS_Invalid;
                }
            }
        }		// convert DocType

        // Mandatory Product Attribute Set Instance
        String	mandatoryType	= "='Y'";	// IN ('Y','S')
        String	sql		= "SELECT COUNT(*) " + "FROM MPC_Order_BOMLine obl" + " INNER JOIN M_Product p ON (obl.M_Product_ID=p.M_Product_ID)" + " INNER JOIN M_AttributeSet pas ON (p.M_AttributeSet_ID=pas.M_AttributeSet_ID) " + " INNER JOIN MPC_Order_BOM obom ON (obl.MPC_Order_BOM_ID=obom.MPC_Order_BOM_ID) " + " WHERE pas.MandatoryType" + mandatoryType + " AND obl.M_AttributeSetInstance_ID IS NULL" + " AND obom.MPC_Order_ID=?";
        int	no	= DB.getSQLValue("MPC_Order", sql, getMPC_Order_ID());

        if (no != 0) {

            m_processMsg	= "@LinesWithoutProductAttribute@ (" + no + ")";

            return DocAction.STATUS_Invalid;
        }

        // Lines
        MMPCOrderBOMLine[]	lines	= getLines(true, "M_Product_ID");

        if (lines.length == 0) {

            m_processMsg	= "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Clear Reservations
        if (!reserveStock(null, lines)) {

            m_processMsg	= "Cannot unreserve Stock (close)";

            return DocAction.STATUS_Invalid;
        }

        m_justPrepared	= true;

        // if (!DOCACTION_Complete.equals(getDocAction()))         don't set for just prepare
        // setDocAction(DOCACTION_Complete);
        return DocAction.STATUS_InProgress;

    }		// prepareIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    private boolean orderStock() {

        MProduct	product	= new MProduct(getCtx(), getM_Product_ID(), get_TrxName());

        if ((product != null) && product.isStocked()) {

            BigDecimal	ordered		= getQtyOrdered();
            BigDecimal	reserved	= Env.ZERO;
            int		M_Locator_ID	= 0;

            // Get Locator to reserve
            if (getM_AttributeSetInstance_ID() != 0) {		// Get existing Location
                M_Locator_ID	= MStorage.getM_Locator_ID(getM_Warehouse_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), ordered, get_TrxName());
            }

            // Get default Location
            if (M_Locator_ID == 0) {

                MWarehouse	wh	= MWarehouse.get(getCtx(), getM_Warehouse_ID());

                M_Locator_ID	= wh.getDefaultLocator().getM_Locator_ID();
            }

            // Update Storage
            if (!MStorage.add(getCtx(), getM_Warehouse_ID(), M_Locator_ID, getM_Product_ID(), getM_AttributeSetInstance_ID(), getM_AttributeSetInstance_ID(), Env.ZERO, reserved, ordered, get_TrxName())) {
                return false;
            }

            // update line
            if (!save(get_TrxName())) {
                return false;
            }
        }

        return true;
    }

    /**
     *          Reserve Inventory.
     *          Counterpart: MInOut.completeIt()
     *          @param dt document type or null
     *          @param lines order lines (ordered by M_Product_ID for deadlock prevention)
     *          @return true if (un) reserved
     */
    private boolean reserveStock(MDocType dt, MMPCOrderBOMLine[] lines) {

        if (dt == null) {
            dt	= MDocType.get(getCtx(), getC_DocType_ID());
        }

        // Binding
        boolean	binding	= !dt.isProposal();

        // Not binding - i.e. Target=0

        /*
         * if (DOCACTION_Void.equals(getDocAction())
         *       //      Closing Binding Quotation
         *       || (MDocType.DOCSUBTYPESO_Quotation.equals(dt.getDocSubTypeSO())
         *               && DOCACTION_Close.equals(getDocAction()))
         *       || isDropShip() )
         *       binding = false;
         */
        boolean	isSOTrx	= isSOTrx();

        log.fine("Binding=" + binding + " - IsSOTrx=" + isSOTrx);

        // Force same WH for all but SO/PO
        int	header_M_Warehouse_ID	= getM_Warehouse_ID();

        if (MDocType.DOCSUBTYPESO_StandardOrder.equals(dt.getDocSubTypeSO()) || MDocType.DOCBASETYPE_PurchaseOrder.equals(dt.getDocBaseType())) {
            header_M_Warehouse_ID	= 0;	// don't enforce
        }

        // Always check and (un) Reserve Inventory
        for (int i = 0; i < lines.length; i++) {

            MMPCOrderBOMLine	line	= lines[i];

            // Check/set WH/Org
            if (header_M_Warehouse_ID != 0)	// enforce WH
            {

                if (header_M_Warehouse_ID != line.getM_Warehouse_ID()) {
                    line.setM_Warehouse_ID(header_M_Warehouse_ID);
                }

                // if (getAD_Org_ID() != line.getAD_Org_ID())
                // line.setAD_Org_ID(getAD_Org_ID());
            }

            // Binding
            BigDecimal	target		= binding
                                          ? line.getQtyRequiered()
                                          : Env.ZERO;
            BigDecimal	difference	= target.subtract(line.getQtyReserved()).subtract(line.getQtyDelivered());

            if (difference.compareTo(Env.ZERO) == 0) {
                continue;
            }

            log.fine("Line=" + line.getLine() + " - Target=" + target + ",Difference=" + difference + " - Requiered=" + line.getQtyRequiered() + ",Reserved=" + line.getQtyReserved() + ",Delivered=" + line.getQtyDelivered());

            // Check Product - Stocked and Item
            MProduct	product	= line.getProduct();

            if ((product != null) && product.isStocked()) {

                BigDecimal	ordered		= isSOTrx
                                                  ? Env.ZERO
                                                  : difference;
                BigDecimal	reserved	= isSOTrx
                                                  ? difference
                                                  : Env.ZERO;
                int		M_Locator_ID	= 0;

                // Get Locator to reserve
                if (line.getM_AttributeSetInstance_ID() != 0) {		// Get existing Location
                    M_Locator_ID	= MStorage.getM_Locator_ID(line.getM_Warehouse_ID(), line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), ordered, get_TrxName());
                }

                // Get default Location
                if (M_Locator_ID == 0) {

                    MWarehouse	wh	= MWarehouse.get(getCtx(), line.getM_Warehouse_ID());

                    M_Locator_ID	= wh.getDefaultLocator().getM_Locator_ID();
                }

                // Update Storage
                if (!MStorage.add(getCtx(), line.getM_Warehouse_ID(), M_Locator_ID, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), line.getM_AttributeSetInstance_ID(), Env.ZERO, reserved, ordered, get_TrxName())) {
                    return false;
                }

                // update line
                line.setQtyReserved(line.getQtyReserved().add(difference));

                if (!line.save(get_TrxName())) {
                    return false;
                }
            }

        }	// reverse inventory

        return true;

    }		// reserveStock

    /**
     *  Approve Document
     *  @return true if success
     */
    public boolean approveIt() {

        log.info("approveIt - " + toString());
        setIsApproved(true);

        return true;

    }		// approveIt

    /**
     *  Reject Approval
     *  @return true if success
     */
    public boolean rejectIt() {

        log.info("rejectIt - " + toString());
        setIsApproved(false);

        return true;

    }		// rejectIt

    /**
     *  Complete Document
     *  @return new status (Complete, In Progress, Invalid, Waiting ..)
     */
    public String completeIt() {

        // MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
        // String DocSubTypeSO = dt.getDocSubTypeSO();

        /*
         * //      Just prepare
         * if (DOCACTION_Prepare.equals(getDocAction())    )
         * {
         *       setProcessed(false);
         *       return DocAction.STATUS_InProgress;
         * }
         * //      Offers
         * if (MDocType.DOCSUBTYPESO_Proposal.equals(DocSubTypeSO)
         *       || MDocType.DOCSUBTYPESO_Quotation.equals(DocSubTypeSO))
         * {
         *       //      Binding
         *       if (MDocType.DOCSUBTYPESO_Quotation.equals(DocSubTypeSO))
         *               reserveStock(dt);
         *       setProcessed(true);
         *       return DocAction.STATUS_Completed;
         * }
         *
         *
         * //      Re-Check
         * if (!m_justPrepared)
         * {
         *       String status = prepareIt();
         *       if (!DocAction.STATUS_InProgress.equals(status))
         *               return status;
         * }
         * //      Implicit Approval
         * if (!isApproved())
         *       approveIt();
         * log.info("completeIt - " + toString());
         * StringBuffer info = new StringBuffer();
         *
         *
         * //      Create SO Shipment - Force Shipment
         * MInOut shipment = null;
         * if (MDocType.DOCSUBTYPESO_OnCreditOrder.equals(DocSubTypeSO)            //      (W)illCall(I)nvoice
         *       || MDocType.DOCSUBTYPESO_WarehouseOrder.equals(DocSubTypeSO)    //      (W)illCall(P)ickup
         *       || MDocType.DOCSUBTYPESO_POSOrder.equals(DocSubTypeSO))                 //      (W)alkIn(R)eceipt
         * {
         *       if (!DELIVERYRULE_Force.equals(getDeliveryRule()))
         *               setDeliveryRule(DELIVERYRULE_Force);
         *       /
         *       shipment = createShipment (dt);
         *       if (shipment == null)
         *               return DocAction.STATUS_Invalid;
         *       info.append("@M_InOut_ID@: ").append(shipment.getDocumentNo());
         *       String msg = shipment.getProcessMsg();
         *       if (msg != null && msg.length() > 0)
         *               info.append("(").append(msg).append(")");
         * }       //      Shipment
         *
         *
         * //      Create SO Invoice - Always invoice complete Order
         * if ( MDocType.DOCSUBTYPESO_POSOrder.equals(DocSubTypeSO)
         *       || MDocType.DOCSUBTYPESO_OnCreditOrder.equals(DocSubTypeSO) )
         * {
         *       MInvoice invoice = createInvoice (dt, shipment);
         *       if (invoice == null)
         *               return DocAction.STATUS_Invalid;
         *       info.append(" - @C_Invoice_ID@: ").append(invoice.getDocumentNo());
         *       String msg = invoice.getProcessMsg();
         *       if (msg != null && msg.length() > 0)
         *               info.append("(").append(msg).append(")");
         * }       //      Invoice
         *
         * //      Counter Documents
         * MPC_Order_Plan counter = createCounterDoc();
         * if (counter != null)
         *       info.append(" - @CounterDoc@: @Order@=").append(counter.getDocumentNo());
         */

        //
        int	m_C_AcctSchema_ID	= Env.getContextAsInt(getCtx(), "$C_AcctSchema_ID");

        log.info("AcctSchema_ID" + m_C_AcctSchema_ID);

        int	m_MPC_Cost_Group_ID	= MMPCCostGroup.getGLCostGroup();

        log.info("Cost_Group_ID" + m_MPC_Cost_Group_ID);

        MMPCProductCosting[]	MPC_Product_Costing	= MMPCProductCosting.getElements(getM_Product_ID(), m_C_AcctSchema_ID, m_MPC_Cost_Group_ID, getM_Warehouse_ID(), getS_Resource_ID());

        log.info("MMPCProductCosting[]" + MPC_Product_Costing.toString());

        if (MPC_Product_Costing != null) {

            log.info("Elements Total" + MPC_Product_Costing.length);

            for (int j = 0; j < MPC_Product_Costing.length; j++) {

                MMPCOrderCost	MPC_Order_Cost	= new MMPCOrderCost(getCtx(), 0, "MPC_Order_Cost");

                MPC_Order_Cost.setMPC_Order_ID(getMPC_Order_ID());
                MPC_Order_Cost.setS_Resource_ID(MPC_Product_Costing[j].getS_Resource_ID());
                MPC_Order_Cost.setC_AcctSchema_ID(MPC_Product_Costing[j].getC_AcctSchema_ID());
                MPC_Order_Cost.setCostCumAmt(MPC_Product_Costing[j].getCostCumAmt());
                MPC_Order_Cost.setCostCumQty(MPC_Product_Costing[j].getCostCumQty());
                MPC_Order_Cost.setCostLLAmt(MPC_Product_Costing[j].getCostLLAmt());
                MPC_Order_Cost.setCostTLAmt(MPC_Product_Costing[j].getCostTLAmt());
                MPC_Order_Cost.setM_Product_ID(getM_Product_ID());
                MPC_Order_Cost.setM_Warehouse_ID(getM_Warehouse_ID());
                MPC_Order_Cost.setMPC_Cost_Element_ID(MPC_Product_Costing[j].getMPC_Cost_Element_ID());
                MPC_Order_Cost.setS_Resource_ID(MPC_Product_Costing[j].getS_Resource_ID());
                MPC_Order_Cost.save(get_TrxName());
            }
        }

        MMPCOrderBOMLine[]	lines	= getLines(getMPC_Order_ID());

        log.info("MMPCOrderBOMLine[]" + lines.toString());

        for (int i = 0; i < lines.length; i++) {

            MPC_Product_Costing	= MMPCProductCosting.getElements(lines[i].getM_Product_ID(), m_C_AcctSchema_ID, m_MPC_Cost_Group_ID, getM_Warehouse_ID(), getS_Resource_ID());
            log.info("Elements Total" + MPC_Product_Costing.length);

            if (MPC_Product_Costing != null) {

                for (int j = 0; j < MPC_Product_Costing.length; j++) {

                    MMPCOrderCost	MPC_Order_Cost	= new MMPCOrderCost(getCtx(), 0, "MPC_Order_Cost");

                    MPC_Order_Cost.setMPC_Order_ID(getMPC_Order_ID());
                    MPC_Order_Cost.setS_Resource_ID(MPC_Product_Costing[j].getS_Resource_ID());
                    MPC_Order_Cost.setC_AcctSchema_ID(MPC_Product_Costing[j].getC_AcctSchema_ID());
                    MPC_Order_Cost.setCostCumAmt(MPC_Product_Costing[j].getCostCumAmt());
                    MPC_Order_Cost.setCostCumQty(MPC_Product_Costing[j].getCostCumQty());
                    MPC_Order_Cost.setCostLLAmt(MPC_Product_Costing[j].getCostLLAmt());
                    MPC_Order_Cost.setCostTLAmt(MPC_Product_Costing[j].getCostTLAmt());
                    MPC_Order_Cost.setM_Product_ID(lines[i].getM_Product_ID());
                    MPC_Order_Cost.setM_Warehouse_ID(lines[i].getM_Warehouse_ID());
                    MPC_Order_Cost.setMPC_Cost_Element_ID(MPC_Product_Costing[j].getMPC_Cost_Element_ID());
                    MPC_Order_Cost.save(get_TrxName());
                }
            }
        }

        setProcessed(true);

        // m_processMsg = info.toString();
        setDocAction(DOCACTION_Close);
        setDocStatus(DocAction.STATUS_Completed);

        return DocAction.STATUS_Completed;
    }		// completeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isAvailable() {

        StringBuffer	sql	= new StringBuffer("SELECT * FROM RV_MPC_Order_Storage WHERE QtyOnHand - QtyRequiered < 0 AND MPC_Order_ID=? ");
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql.toString());
            pstmt.setInt(1, getMPC_Order_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                return false;
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

            return true;

        } catch (Exception e) {

            log.log(Level.SEVERE, "getLines - " + sql, e);

            return false;

        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (Exception e) {}

            pstmt	= null;
        }
    }

    /**
     *  Create Counter Document
     *
     * @return
     */

    /*
     * private MPC_Order createCounterDoc()
     * {
     *       //      Is this a counter doc ?
     *       if (getRef_Order_ID() != 0)
     *               return null;
     *
     *       //      Org Must be linked to BPartner
     *       MOrg org = MOrg.get(getCtx(), getAD_Org_ID());
     *       int counterC_BPartner_ID = org.getLinkedC_BPartner_ID();
     *       if (counterC_BPartner_ID == 0)
     *               return null;
     *       //      Business Partner needs to be linked to Org
     *       MBPartner bp = new MBPartner (getCtx(), getC_BPartner_ID());
     *       int counterAD_Org_ID = bp.getAD_OrgBP_ID_Int();
     *       if (counterAD_Org_ID == 0)
     *               return null;
     *
     *       MBPartner counterBP = new MBPartner (getCtx(), counterC_BPartner_ID);
     *       MOrgInfo counterOrgInfo = MOrgInfo.get(getCtx(), counterAD_Org_ID);
     *       log.info("createCounterDoc - Counter BP=" + counterBP.getName());
     *
     *       //      Document Type
     *       int C_DocTypeTarget_ID = 0;
     *       MDocTypeCounter counterDT = MDocTypeCounter.getCounterDocType(getCtx(), getC_DocType_ID());
     *       if (counterDT != null)
     *       {
     *               C_DocTypeTarget_ID = counterDT.getCounter_C_DocType_ID();
     *               log.fine("createCounterDoc - " + counterDT);
     *       }
     *       else    //      indirect
     *       {
     *               C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocType_ID(getCtx(), getC_DocType_ID());
     *               log.fine("createCounterDoc - Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID);
     *       }
     *       //      Deep Copy
     *       MOrder counter = copyFrom (this, getDateOrdered(),
     *               C_DocTypeTarget_ID, !isSOTrx(), true);
     *       /
     *       counter.setAD_Org_ID(counterAD_Org_ID);
     *       counter.setM_Warehouse_ID(counterOrgInfo.getM_Warehouse_ID());
     *       /
     *       counter.setBPartner(counterBP);
     *       //      Refernces (Should not be required
     *       counter.setSalesRep_ID(getSalesRep_ID());
     *       counter.save();
     *
     *       //      Update copied lines
     *       MOrderLine[] counterLines = counter.getLines(true);
     *       for (int i = 0; i < counterLines.length; i++)
     *       {
     *               MOrderLine counterLine = counterLines[i];
     *               counterLine.setOrder(counter);  //      copies header values (BP, etc.)
     *               counterLine.setPrice();
     *               counterLine.setTax();
     *               counterLine.save();
     *       }
     *       log.fine("createCounterDoc = " + counter);
     *
     *       //      Document Action
     *       if (counterDT != null)
     *       {
     *               if (counterDT.getDocAction() != null)
     *               {
     *                       counter.setDocAction(counterDT.getDocAction());
     *                       counter.processIt(counterDT.getDocAction());
     *                       counter.save();
     *               }
     *       }
     *       return counter;
     * }       //      createCounterDoc
     */

    /**
     *  Post Document - nothing
     *  @return true if success
     */
    public boolean postIt() {

        log.info("postIt - " + toString());

        return false;

    }		// postIt

    /**
     *  Void Document.
     *  Set Qtys to 0 - Sales: reverse all documents
     *  @return true if success
     */
    public boolean voidIt() {

        log.info("voidIt - " + toString());

        /*
         * MOrderLine[] lines = getLines(false);
         * for (int i = 0; i < lines.length; i++)
         * {
         *       MOrderLine line = lines[i];
         *       BigDecimal old = line.getQtyOrdered();
         *       if (old.compareTo(Env.ZERO) != 0)
         *       {
         *               line.setQtyOrdered(Env.ZERO);
         *               line.setLineNetAmt(Env.ZERO);
         *               line.addDescription("Void (" + old + ")");
         *               line.save();
         *       }
         * }
         * //      Clear Reservations
         * if (!reserveStock(null))
         * {
         *       m_processMsg = "Cannot unreserve Stock (void)";
         *       return false;
         * }
         *
         * if (!createReversals())
         *       return false;
         */
        setProcessed(true);
        setDocAction(DOCACTION_None);

        return true;

    }		// voidIt

    /**
     *  Create Shipment/Invoice Reversals
     *  @param true if success
     *
     * @return
     */

    /*
     * private boolean createReversals()
     * {
     *       //      Cancel only Sales
     *       if (!isSOTrx())
     *               return true;
     *
     *       log.fine("createReversals");
     *       StringBuffer info = new StringBuffer();
     *
     *       //      Reverse All Shipments
     *       info.append("@M_InOut_ID@:");
     *       MInOut[] shipments = getShipments();
     *       for (int i = 0; i < shipments.length; i++)
     *       {
     *               MInOut ship = shipments[i];
     *               //      if closed - ignore
     *               if (MInOut.DOCSTATUS_Closed.equals(ship.getDocStatus())
     *                       || MInOut.DOCSTATUS_Reversed.equals(ship.getDocStatus())
     *                       || MInOut.DOCSTATUS_Voided.equals(ship.getDocStatus()) )
     *                       continue;
     *
     *               //      If not completed - void - otherwise reverse it
     *               if (!MInOut.DOCSTATUS_Completed.equals(ship.getDocStatus()))
     *               {
     *                       if (ship.voidIt())
     *                               ship.setDocStatus(MInOut.DOCSTATUS_Voided);
     *               }
     *               else if (ship.reverseCorrectIt())       //      completed shipment
     *               {
     *                       ship.setDocStatus(MInOut.DOCSTATUS_Reversed);
     *                       info.append(" ").append(ship.getDocumentNo());
     *               }
     *               else
     *               {
     *                       m_processMsg = "Could not reverse Shipment " + ship;
     *                       return false;
     *               }
     *               ship.setDocAction(MInOut.DOCACTION_None);
     *               ship.save();
     *       }       //      for all shipments
     *
     *       //      Reverse All Invoices
     *       info.append(" - @C_Invoice_ID@:");
     *       MInvoice[] invoices = getInvoices();
     *       for (int i = 0; i < invoices.length; i++)
     *       {
     *               MInvoice invoice = invoices[i];
     *               //      if closed - ignore
     *               if (MInvoice.DOCSTATUS_Closed.equals(invoice.getDocStatus())
     *                       || MInvoice.DOCSTATUS_Reversed.equals(invoice.getDocStatus())
     *                       || MInvoice.DOCSTATUS_Voided.equals(invoice.getDocStatus()) )
     *                       continue;
     *
     *               //      If not compleded - void - otherwise reverse it
     *               if (!MInvoice.DOCSTATUS_Completed.equals(invoice.getDocStatus()))
     *               {
     *                       if (invoice.voidIt())
     *                               invoice.setDocStatus(MInvoice.DOCSTATUS_Voided);
     *               }
     *               else if (invoice.reverseCorrectIt())    //      completed invoice
     *               {
     *                       invoice.setDocStatus(MInvoice.DOCSTATUS_Reversed);
     *                       info.append(" ").append(invoice.getDocumentNo());
     *               }
     *               else
     *               {
     *                       m_processMsg = "Could not reverse Invoice " + invoice;
     *                       return false;
     *               }
     *               invoice.setDocAction(MInvoice.DOCACTION_None);
     *               invoice.save();
     *       }       //      for all shipments
     *
     *       m_processMsg = info.toString();
     *       return true;
     * }       //      createReversals
     */

    /**
     *      Close Document.
     *      Cancel not delivered Qunatities
     *      @return true if success
     */
    public boolean closeIt() {

        log.info(toString());

        // Close Not delivered Qty - SO/PO
        MMPCOrderBOMLine[]	lines	= getLines(true, "M_Product_ID");

        /*
         * for (int i = 0; i < lines.length; i++)
         * {
         *       MMPCOrderBOMLine line = lines[i];
         *       BigDecimal old = line.getQtyOrdered();
         *       if (old.compareTo(line.getQtyDelivered()) != 0)
         *       {
         *               line.setQtyOrdered(line.getQtyDelivered());
         *               //      QtyEntered unchanged
         *               line.addDescription("Close (" + old + ")");
         *               line.save(get_TrxName());
         *       }
         * }
         */

        // Clear Reservations
        if (!reserveStock(null, lines)) {

            m_processMsg	= "Cannot unreserve Stock (close)";

            return false;
        }

        setProcessed(true);
        setDocAction(DOCACTION_None);

        return true;

    }		// closeIt

    /**
     *  Reverse Correction - same void
     *  @return true if success
     */
    public boolean reverseCorrectIt() {

        log.info("reverseCorrectIt - " + toString());

        return voidIt();

    }		// reverseCorrectionIt

    /**
     *  Reverse Accrual - none
     *  @return true if success
     */
    public boolean reverseAccrualIt() {

        log.info("reverseAccrualIt - " + toString());

        return false;

    }		// reverseAccrualIt

    /**
     *  Re-activate.
     *  @return true if success
     */
    public boolean reActivateIt() {

        log.info("reActivateIt - " + toString());

        MDocType	dt		= MDocType.get(getCtx(), getC_DocType_ID());
        String		DocSubTypeSO	= dt.getDocSubTypeSO();

        // PO - just re-open
        if (!isSOTrx()) {
            log.fine("reActivateIt - Existing documents not modified - " + dt);
        }

        // Reverse Direct Documents
        if (MDocType.DOCSUBTYPESO_OnCreditOrder.equals(DocSubTypeSO)	// (W)illCall(I)nvoice
                || MDocType.DOCSUBTYPESO_WarehouseOrder.equals(DocSubTypeSO)	// (W)illCall(P)ickup
                || MDocType.DOCSUBTYPESO_POSOrder.equals(DocSubTypeSO))		// (W)alkIn(R)eceipt
                {

            // if (!createReversals())
            return false;
        } else {
            log.fine("reActivateIt - Existing documents not modified - SubType=" + DocSubTypeSO);
        }

        setDocAction(DOCACTION_Complete);
        setProcessed(false);

        return true;

    }		// reActivateIt

    /**
     *      Get Invoices of Order
     *      @param C_Order_ID id
     *
     * @param MPC_Order_ID
     *      @return invoices
     */
    public static MMPCOrderBOMLine[] getLines(int MPC_Order_ID) {

        ArrayList		list	= new ArrayList();
        String			sql	= "SELECT * FROM MPC_Order_BOMLine WHERE MPC_Order_ID=? ";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, MPC_Order_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MMPCOrderBOMLine(Env.getCtx(), rs, "MPC_Order_BOM_Line"));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {

            // log.log(Level.SEVERE ,("getLines", e);
            System.out.println("getLines" + e);
        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (Exception e) {}

            pstmt	= null;
        }

        //
        MMPCOrderBOMLine[]	retValue	= new MMPCOrderBOMLine[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getLines

    /**
     *
     *
     *
     *
     *  Get Summary
     *  @return Summary of Document
     */

    /*
     * public String getSummary()
     * {
     *       StringBuffer sb = new StringBuffer();
     *       sb.append(getDocumentNo());
     *       //      : Grand Total = 123.00 (#1)
     *       sb.append(": ").
     *               append(Msg.translate(getCtx(),"GrandTotal")).append("=").append(getGrandTotal())
     *               .append(" (#").append(getLines(true).length).append(")");
     *       //       - Description
     *       if (getDescription() != null && getDescription().length() > 0)
     *               sb.append(" - ").append(getDescription());
     *       return sb.toString();
     * }       //      getSummary
     */

    /**
     *  Get Document Owner (Responsible)
     *  @return AD_User_ID
     */
    public int getDoc_User_ID() {
        return getPlanner_ID();
    }		// getDoc_User_ID

    /**
     *  Get Document Approval Amount
     *  @return amount
     */
    public java.math.BigDecimal getApprovalAmt() {

        // return getGrandTotal();
        return new BigDecimal(0);
    }		// getApprovalAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getC_Currency_ID() {
        return 0;
    }


    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getSummary() {
        return "";
    }
}	// MOrder



/*
 * @(#)MMPCOrder.java   13.jun 2007
 * 
 *  Fin del fichero MMPCOrder.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
