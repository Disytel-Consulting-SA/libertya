/*
 * @(#)MMPCCostCollector.java   13.jun 2007  Versión 2.2
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

import java.sql.*;

import java.util.*;

import java.math.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      Inventory Movement Model
 *
 *  @author Fundesle
 *  @version $Id: MMovement.java,v 2.0 $
 */
public class MMPCCostCollector extends X_MPC_Cost_Collector implements DocAction {

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param MPC_Cost_Collector id
     * @param MPC_Cost_Collector_ID
     * @param trxName
     */
    public MMPCCostCollector(Properties ctx, int MPC_Cost_Collector_ID, String trxName) {

        super(ctx, MPC_Cost_Collector_ID, trxName);

        if (MPC_Cost_Collector_ID == 0) {

            // setC_DocType_ID (0);
            setDocAction(DOCACTION_Complete);		// CO
            setDocStatus(DOCSTATUS_Drafted);		// DR

            // setIsApproved (false);
            // setIsInTransit (false);
            setMovementDate(new Timestamp(System.currentTimeMillis()));		// @#Date@
            setPosted(false);
            super.setProcessed(false);
        }

    }		// MMovement

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MMPCCostCollector(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MMovement

    /**
     *      Add to Description
     *      @param description text
     */
    public void addDescription(String description) {

        String	desc	= getDescription();

        if (desc == null) {
            setDescription(description);
        } else {
            setDescription(desc + " | " + description);
        }

    }		// addDescription

    /**
     *      Set Processed.
     *      Propergate to Lines/Taxes
     *      @param processed processed
     */
    public void setProcessed(boolean processed) {

        super.setProcessed(processed);

        if (getID() == 0) {
            return;
        }

        String	sql	= "UPDATE MPC_Cost_Collector SET Processed='" + (processed
                ? "Y"
                : "N") + "' WHERE MPC_Cost_Collector_ID =" + getMPC_Cost_Collector_ID();
        int	noLine	= DB.executeUpdate(sql, get_TrxName());

        // m_lines = null;
        log.fine("setProcessed - " + processed + " - Lines=" + noLine);

    }		// setProcessed

    /**
     *      Process document
     *      @param processAction document action
     *      @return true if performed
     */
    public boolean processIt(String processAction) {

        m_processMsg	= null;

        DocumentEngine	engine	= new DocumentEngine(this, getDocStatus());

        return engine.processIt(processAction, getDocAction(), log);

    }		// processIt

    /** Just Prepared Flag */
    private boolean	m_justPrepared	= false;

    /**
     *      Unlock Document.
     *      @return true if success
     */
    public boolean unlockIt() {

        log.info("unlockIt - " + toString());
        setProcessing(false);

        return true;

    }		// unlockIt

    /**
     *      Invalidate Document
     *      @return true if success
     */
    public boolean invalidateIt() {

        log.info("invalidateIt - " + toString());
        setDocAction(DOCACTION_Prepare);

        return true;

    }		// invalidateIt

    /**
     *      Prepare Document
     *      @return new status (In Progress or Invalid)
     */
    public String prepareIt() {

        log.info("prepareIt - " + toString());

        MDocType	dt	= MDocType.get(getCtx(), getC_DocType_ID());	// getC_DocType_ID()

        // Std Period open?
        if (!MPeriod.isOpen(getCtx(), getMovementDate(), dt.getDocBaseType())) {

            m_processMsg	= "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        m_justPrepared	= true;

        if (!DOCACTION_Complete.equals(getDocAction())) {
            setDocAction(DOCACTION_Complete);
        }

        return DocAction.STATUS_InProgress;

    }		// prepareIt

    /**
     *      Approve Document
     *      @return true if success
     */
    public boolean approveIt() {

        log.info("approveIt - " + toString());

        // setIsApproved(true);
        return true;

    }		// approveIt

    /**
     *      Reject Approval
     *      @return true if success
     */
    public boolean rejectIt() {

        log.info("rejectIt - " + toString());

        // setIsApproved(false);
        return true;

    }		// rejectIt

    /**
     *      Complete Document
     *      @return new status (Complete, In Progress, Invalid, Waiting ..)
     */
    public String completeIt() {

        // Re-Check
        if (!m_justPrepared) {

            String	status	= prepareIt();

            if (!DocAction.STATUS_InProgress.equals(status)) {
                return status;
            }
        }

        MProduct	product	= new MProduct(getCtx(), getM_Product_ID(), get_TrxName());

        // Qty & Type
        String		MovementType	= getMovementType();
        BigDecimal	Qty		= getMovementQty();

        if (MovementType.charAt(1) == '-') {	// C- Customer Shipment - V- Vendor Return
            Qty	= Qty.negate();
        }

        BigDecimal	QtyIssue	= Env.ZERO;
        BigDecimal	QtyReceipt	= Env.ZERO;

        // Update Order Line
        MMPCOrderBOMLine	obomline	= null;

        if (getMPC_Order_BOMLine_ID() != 0) {
            obomline	= new MMPCOrderBOMLine(getCtx(), getMPC_Order_BOMLine_ID(), get_TrxName());
        }

        log.info(" Qty=" + getMovementQty());

        // Stock Movement - Counterpart MOrder.reserveStock
        if ((product != null) && product.isStocked()) {

            log.fine("Material Transaction");

            MTransaction	mtrx					= null;
            int			reservationAttributeSetInstance_ID	= getM_AttributeSetInstance_ID();

            if (getM_AttributeSetInstance_ID() == 0) {

                MMPCOrderBOMLineMA	mas[]	= MMPCOrderBOMLineMA.get(getCtx(), getMPC_Cost_Collector_ID(), get_TrxName());

                for (int j = 0; j < mas.length; j++) {

                    MMPCOrderBOMLineMA	ma	= mas[j];
                    BigDecimal		QtyMA	= ma.getMovementQty();

                    if (MovementType.charAt(1) == '-') {	// C- Customer Shipment - V- Vendor Return
                        QtyMA	= QtyMA.negate();
                    }

                    BigDecimal	QtyReceiptMA	= Env.ZERO;
                    BigDecimal	QtyIssueMA	= Env.ZERO;

                    /*
                     * if (getC_CostCollector_ID() != 0)
                     * {
                     *       if (isSOTrx())
                     *               QtySOMA = ma.getMovementQty();
                     *       else
                     *               QtyPOMA = ma.getMovementQty();
                     * }
                     */

                    // Update Storage - see also VMatch.createMatchRecord
                    if (!MStorage.add(getCtx(), getM_Warehouse_ID(), getM_Locator_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), reservationAttributeSetInstance_ID, QtyMA, QtyIssueMA.negate(), QtyReceiptMA.negate(), get_TrxName())) {

                        m_processMsg	= "Cannot correct Inventory (MA)";

                        return DocAction.STATUS_Invalid;
                    }

                    // Create Transaction
                    mtrx	= new MTransaction(getCtx(), MovementType, getM_Locator_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), QtyMA, getMovementDate(), get_TrxName());
                    mtrx.setMPC_Order_ID(getMPC_Order_ID());
                    mtrx.setMPC_Order_BOMLine_ID(getMPC_Order_BOMLine_ID());

                    if (!mtrx.save(get_TrxName())) {

                        m_processMsg	= "Could not create Material Transaction (MA)";

                        return DocAction.STATUS_Invalid;
                    }
                }
            }

            // sLine.getM_AttributeSetInstance_ID() != 0
            if (mtrx == null) {

                // Fallback: Update Storage - see also VMatch.createMatchRecord
                if (!MStorage.add(getCtx(), getM_Warehouse_ID(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), reservationAttributeSetInstance_ID, Qty, QtyIssue.negate(), QtyReceipt.negate(), get_TrxName())) {

                    m_processMsg	= "Cannot correct Inventory";

                    return DocAction.STATUS_Invalid;
                }

                // FallBack: Create Transaction
                mtrx	= new MTransaction(getCtx(), MovementType, getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), Qty, getMovementDate(), get_TrxName());
                mtrx.setMPC_Order_ID(getMPC_Order_ID());
                mtrx.setMPC_Order_BOMLine_ID(getMPC_Order_BOMLine_ID());

                if (!mtrx.save(get_TrxName())) {

                    m_processMsg	= "Could not create Material Transaction";

                    return DocAction.STATUS_Invalid;
                }
            }

            if (getMPC_Order_BOMLine_ID() != 0) {

                log.fine("OrderLine - Reserved=" + obomline.getQtyReserved() + ", Delivered=" + obomline.getQtyDelivered());
                obomline.setQtyReserved(obomline.getQtyReserved().subtract(getMovementQty()));

                /*
                 * if (!obomline.save(get_TrxName()))
                 * {
                 *       m_processMsg = "Order BOM Line From not Update";
                 *       return DocAction.STATUS_Invalid;
                 * }
                 */
            }

        }	// stock movement

        if (MovementType.charAt(1) == '-') {

            // Update Order Line
            if (getMPC_Order_BOMLine_ID() != 0) {

                obomline	= new MMPCOrderBOMLine(getCtx(), getMPC_Order_BOMLine_ID(), get_TrxName());
                obomline.setQtyDelivered(obomline.getQtyDelivered().subtract(Qty));
                obomline.setDateDelivered(getMovementDate());		// overwrite=last

                if (!obomline.save(get_TrxName())) {

                    m_processMsg	= "Could not update Order Line";

                    return DocAction.STATUS_Invalid;

                } else {
                    log.fine("OrderLine -> Reserved=" + obomline.getQtyReserved() + ", Delivered=" + obomline.getQtyDelivered());
                }
            }
        } else if (MovementType.charAt(1) == '+') {

            MMPCOrder	order	= new MMPCOrder(getCtx(), getMPC_Order_ID(), get_TrxName());

            order.setQtyDelivered(order.getQtyDelivered().add(Qty));
            order.setQtyScrap(order.getQtyScrap().add(getScrappedQty()));

            // order.setQtyReject(order.getQtyReject().add(m_rejectQty));
            order.setDateDelivered(getMovementDate());		// overwrite=last

            if (!order.save(get_TrxName())) {

                m_processMsg	= "Could not update Order";

                return DocAction.STATUS_Invalid;

            } else {
                log.fine("Order -> Delivered=" + order.getQtyDelivered());
            }
        }

        // for all lines
        setProcessed(true);
        setDocAction(DOCACTION_Close);

        return DocAction.STATUS_Completed;
    }		// completeIt

    /**
     *      Post Document - nothing
     *      @return true if success
     */
    public boolean postIt() {

        log.info("postIt - " + toString());

        return false;

    }		// postIt

    /**
     *      Void Document.
     *      @return true if success
     */
    public boolean voidIt() {

        log.info("voidIt - " + toString());

        return false;

    }		// voidIt

    /**
     *      Close Document.
     *      @return true if success
     */
    public boolean closeIt() {

        log.info("closeIt - " + toString());

        // Close Not delivered Qty
        setDocAction(DOCACTION_None);

        return true;

    }		// closeIt

    /**
     *      Reverse Correction
     *      @return false
     */
    public boolean reverseCorrectIt() {

        log.info("reverseCorrectIt - " + toString());

        return false;

    }		// reverseCorrectionIt

    /**
     *      Reverse Accrual - none
     *      @return false
     */
    public boolean reverseAccrualIt() {

        log.info("reverseAccrualIt - " + toString());

        return false;

    }		// reverseAccrualIt

    /**
     *      Re-activate
     *      @return false
     */
    public boolean reActivateIt() {

        log.info("reActivateIt - " + toString());

        return false;

    }		// reActivateIt

    /**
     *      Get Summary
     *      @return Summary of Document
     */
    public String getSummary() {

        StringBuffer	sb	= new StringBuffer();

        sb.append(getDescription());

        // : Total Lines = 123.00 (#1)
        // sb.append(": ")
        // .append(Msg.translate(getCtx(),"ApprovalAmt")).append("=").append(getApprovalAmt())
        // .append(" (#").append(")");
        // - Description
        if ((getDescription() != null) && (getDescription().length() > 0)) {
            sb.append(" - ").append(getDescription());
        }

        return sb.toString();

    }		// getSummary


    /**
     *      Get Document Owner (Responsible)
     *      @return AD_User_ID
     */
    public int getDoc_User_ID() {
        return getCreatedBy();
    }		// getDoc_User_ID

    /**
     *      Get Document Currency
     *      @return C_Currency_ID
     */
    public int getC_Currency_ID() {

        // MPriceList pl = MPriceList.get(getCtx(), getM_PriceList_ID());
        // return pl.getC_Currency_ID();
        return 0;
    }

    /**
     *      Get Document Approval Amount
     *      @return amount
     */
    public BigDecimal getApprovalAmt() {
        return new BigDecimal(0);
    }		// getApprovalAmt

    /*
     * public BigDecimal ApprovalAmt()
     * {
     *   return new BigDecimal(0);
     * }
     */

    // getC_Currency_ID
}	// MMovement



/*
 * @(#)MMPCCostCollector.java   13.jun 2007
 * 
 *  Fin del fichero MMPCCostCollector.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
