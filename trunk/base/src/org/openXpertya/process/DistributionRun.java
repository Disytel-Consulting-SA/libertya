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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDistributionRun;
import org.openXpertya.model.MDistributionRunDetail;
import org.openXpertya.model.MDistributionRunLine;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MProduct;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DistributionRun extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_DistributionRun_ID = 0;

    /** Descripción de Campos */

    private Timestamp p_DatePromised = null;

    /** Descripción de Campos */

    private int p_C_DocType_ID = 0;

    /** Descripción de Campos */

    private boolean p_IsTest = false;

    /** Descripción de Campos */

    private MDistributionRun m_run = null;

    /** Descripción de Campos */

    private MDistributionRunLine[] m_runLines = null;

    /** Descripción de Campos */

    private MDistributionRunDetail[] m_details = null;

    /** Descripción de Campos */

    private Timestamp m_DateOrdered = null;

    /** Descripción de Campos */

    private int m_counter = 0;

    /** Descripción de Campos */

    private MDocType m_docType = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            // log.fine("prepare - " + para[i]);

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_DocType_ID" )) {
                p_C_DocType_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DatePromised" )) {
                p_DatePromised = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "IsTest" )) {
                p_IsTest = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_M_DistributionRun_ID = getRecord_ID();
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
        log.info( "M_DistributionRun_ID=" + p_M_DistributionRun_ID + ", C_DocType_ID=" + p_C_DocType_ID + ", DatePromised=" + p_DatePromised + ", Test=" + p_IsTest );

        // Distribution Run

        if( p_M_DistributionRun_ID == 0 ) {
            throw new IllegalArgumentException( "No Distribution Run ID" );
        }

        m_run = new MDistributionRun( getCtx(),p_M_DistributionRun_ID,get_TrxName());

        if( m_run.getID() == 0 ) {
            throw new Exception( "Distribution Run not found -  M_DistributionRun_ID=" + p_M_DistributionRun_ID );
        }

        m_runLines = m_run.getLines( false );

        if( (m_runLines == null) || (m_runLines.length == 0) ) {
            throw new Exception( "No active, non-zero Distribution Run Lines found" );
        }

        // Document Type

        if( p_C_DocType_ID == 0 ) {
            throw new IllegalArgumentException( "No Document Type ID" );
        }

        m_docType = new MDocType( getCtx(),p_C_DocType_ID,get_TrxName());

        if( m_docType.getID() == 0 ) {
            throw new Exception( "Documentation Type not found -  C_DocType_ID=" + p_C_DocType_ID );
        }

        //

        m_DateOrdered = new Timestamp( System.currentTimeMillis());

        if( p_DatePromised == null ) {
            p_DatePromised = m_DateOrdered;
        }

        // Create Temp Lines

        if( insertDetails() == 0 ) {
            throw new Exception( "No Lines" );
        }

        // Order By Distribution Run Line

        m_details = MDistributionRunDetail.get( getCtx(),p_M_DistributionRun_ID,false );

        // First Run -- Add & Round

        addAllocations();

        // Do Allocation

        int loops = 0;

        while( !isAllocationEqTotal()) {
            adjustAllocation();
            addAllocations();

            if( ++loops > 10 ) {
                throw new Exception( "Loop detected - more than 10 Allocation attempts" );
            }
        }

        // Order By Business Partner

        m_details = MDistributionRunDetail.get( getCtx(),p_M_DistributionRun_ID,true );

        // Create Orders

        createOrders();

        return "@Created@ #" + m_counter;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int insertDetails() {

        // Handle NULL

        String sql = "UPDATE M_DistributionRunLine SET MinQty = 0 WHERE MinQty IS NULL";
        int no = DB.executeUpdate( sql,get_TrxName());

        sql = "UPDATE M_DistributionListLine SET MinQty = 0 WHERE MinQty IS NULL";
        no = DB.executeUpdate( sql,get_TrxName());

        // Total Ratio

        sql = "UPDATE M_DistributionList l " + "SET RatioTotal = (SELECT SUM(Ratio) FROM M_DistributionListLine ll " + " WHERE l.M_DistributionList_ID=ll.M_DistributionList_ID) " + "WHERE EXISTS (SELECT * FROM M_DistributionRunLine rl" + " WHERE l.M_DistributionList_ID=rl.M_DistributionList_ID" + " AND rl.M_DistributionRun_ID=" + p_M_DistributionRun_ID + ")";
        no = DB.executeUpdate( sql,get_TrxName());

        // Delete Old

        sql = "DELETE FROM T_DistributionRunDetail WHERE M_DistributionRun_ID=" + p_M_DistributionRun_ID;
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "insertDetails - deleted #" + no );

        // Insert New

        sql = "INSERT INTO T_DistributionRunDetail " + "(M_DistributionRun_ID, M_DistributionRunLine_ID, M_DistributionList_ID, M_DistributionListLine_ID," + "AD_Client_ID,AD_Org_ID, IsActive, Created,CreatedBy, Updated,UpdatedBy," + "C_BPartner_ID, C_BPartner_Location_ID, M_Product_ID," + "Ratio, MinQty, Qty) "

        //

        + "SELECT rl.M_DistributionRun_ID, rl.M_DistributionRunLine_ID," + "ll.M_DistributionList_ID, ll.M_DistributionListLine_ID, " + "rl.AD_Client_ID,rl.AD_Org_ID, rl.IsActive, rl.Created,rl.CreatedBy, rl.Updated,rl.UpdatedBy," + "ll.C_BPartner_ID, ll.C_BPartner_Location_ID, rl.M_Product_ID, " + "ll.Ratio, " + "CASE WHEN rl.MinQty > ll.MinQty THEN rl.MinQty ELSE ll.MinQty END, " + "(ll.Ratio/l.RatioTotal*rl.TotalQty)" + "FROM M_DistributionRunLine rl" + " INNER JOIN M_DistributionList l ON (rl.M_DistributionList_ID=l.M_DistributionList_ID)" + " INNER JOIN M_DistributionListLine ll ON (rl.M_DistributionList_ID=ll.M_DistributionList_ID) " + "WHERE rl.M_DistributionRun_ID=" + p_M_DistributionRun_ID + " AND l.RatioTotal<>0 AND rl.IsActive='Y' AND ll.IsActive='Y'";
        no = DB.executeUpdate( sql,get_TrxName());
        log.fine( "inserted #" + no );

        return no;
    }    // insertDetails

    /**
     * Descripción de Método
     *
     */

    private void addAllocations() {

        // Reset

        for( int j = 0;j < m_runLines.length;j++ ) {
            MDistributionRunLine runLine = m_runLines[ j ];

            runLine.resetCalculations();
        }

        // Add Up

        for( int i = 0;i < m_details.length;i++ ) {
            MDistributionRunDetail detail = m_details[ i ];

            for( int j = 0;j < m_runLines.length;j++ ) {
                MDistributionRunLine runLine = m_runLines[ j ];

                if( runLine.getM_DistributionRunLine_ID() == detail.getM_DistributionRunLine_ID()) {

                    // Round

                    detail.round( runLine.getStandardPrecision());

                    // Add

                    runLine.addActualMin( detail.getMinQty());
                    runLine.addActualQty( detail.getQty());
                    runLine.addActualAllocation( detail.getActualAllocation());
                    runLine.setMaxAllocation( detail.getActualAllocation(),false );

                    //

                    log.fine( "RunLine=" + runLine.getLine() + ": BP_ID=" + detail.getC_BPartner_ID() + ", Min=" + detail.getMinQty() + ", Qty=" + detail.getQty() + ", Allocation=" + detail.getActualAllocation());

                    continue;
                }
            }
        }    // for all detail lines

        // Info

        for( int j = 0;j < m_runLines.length;j++ ) {
            MDistributionRunLine runLine = m_runLines[ j ];

            log.fine( "Run - " + runLine.getInfo());
        }
    }    // addAllocations

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    private boolean isAllocationEqTotal() throws Exception {
        boolean allocationEqTotal = true;

        // Check total min qty & delta

        for( int j = 0;j < m_runLines.length;j++ ) {
            MDistributionRunLine runLine = m_runLines[ j ];

            if( runLine.isActualMinGtTotal()) {
                throw new Exception( "Line " + runLine.getLine() + " Sum of Min Qty=" + runLine.getActualMin() + " is greater than Total Qty=" + runLine.getTotalQty());
            }

            if( allocationEqTotal &&!runLine.isActualAllocationEqTotal()) {
                allocationEqTotal = false;
            }
        }    // for all run lines

        log.info( "=" + allocationEqTotal );

        return allocationEqTotal;
    }    // isAllocationEqTotal

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void adjustAllocation() throws Exception {
        for( int j = 0;j < m_runLines.length;j++ ) {
            adjustAllocation( j );
        }
    }    // adjustAllocation

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @throws Exception
     */

    private void adjustAllocation( int index ) throws Exception {
        MDistributionRunLine runLine    = m_runLines[ index ];
        BigDecimal           difference = runLine.getActualAllocationDiff();

        if( difference.compareTo( Env.ZERO ) == 0 ) {
            return;
        }

        // Adjust when difference is -1->1 or last difference is the same

        boolean adjustBiggest = (difference.abs().compareTo( Env.ONE ) <= 0) || (difference.abs().compareTo( runLine.getLastDifference().abs()) == 0);

        log.fine( "Line=" + runLine.getLine() + ", Diff=" + difference + ", Adjust=" + adjustBiggest );

        // Adjust Biggest Amount

        if( adjustBiggest ) {
            for( int i = 0;i < m_details.length;i++ ) {
                MDistributionRunDetail detail = m_details[ i ];

                if( runLine.getM_DistributionRunLine_ID() == detail.getM_DistributionRunLine_ID()) {
                    log.fine( "Biggest - DetailAllocation=" + detail.getActualAllocation() + "MaxAllocation=" + runLine.getMaxAllocation() + ", CanAdjust=" + detail.isCanAdjust());

                    if( (detail.getActualAllocation().compareTo( runLine.getMaxAllocation()) == 0) && detail.isCanAdjust()) {
                        detail.adjustQty( difference );
                        detail.save();

                        return;
                    }
                }
            }    // for all detail lines

            throw new Exception( "Cannot adjust Difference = " + difference + " - You need to change Total Qty or Min Qty" );
        } else    // Distibute
        {

            // New Total Ratio

            BigDecimal ratioTotal = Env.ZERO;

            for( int i = 0;i < m_details.length;i++ ) {
                MDistributionRunDetail detail = m_details[ i ];

                if( runLine.getM_DistributionRunLine_ID() == detail.getM_DistributionRunLine_ID()) {
                    if( detail.isCanAdjust()) {
                        ratioTotal = ratioTotal.add( detail.getRatio());
                    }
                }
            }

            if( ratioTotal.compareTo( Env.ZERO ) == 0 ) {
                throw new Exception( "Cannot distribute Difference = " + difference + " - You need to change Total Qty or Min Qty" );
            }

            // Distribute

            for( int i = 0;i < m_details.length;i++ ) {
                MDistributionRunDetail detail = m_details[ i ];

                if( runLine.getM_DistributionRunLine_ID() == detail.getM_DistributionRunLine_ID()) {
                    if( detail.isCanAdjust()) {
                        BigDecimal diffRatio = detail.getRatio().multiply( difference ).divide( ratioTotal,BigDecimal.ROUND_HALF_UP );

                        detail.adjustQty( diffRatio );
                        detail.save();
                    }
                }
            }
        }

        runLine.setLastDifference( difference );
    }    // adjustAllocation

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean createOrders() {

        // Get Counter Org/BP

        int runAD_Org_ID = m_run.getAD_Org_ID();

        if( runAD_Org_ID == 0 ) {
            runAD_Org_ID = Env.getAD_Org_ID( getCtx());
        }

        MOrg      runOrg           = MOrg.get( getCtx(),runAD_Org_ID );
        int       runC_BPartner_ID = runOrg.getLinkedC_BPartner_ID();
        boolean   counter          = !m_run.isCreateSingleOrder()     // no single Order
                                     && (runC_BPartner_ID > 0         // Org linked to BP
                                         ) &&!m_docType.isSOTrx();    // PO
        MBPartner runBPartner      = counter
                                     ?new MBPartner( getCtx(),runC_BPartner_ID,get_TrxName())
                                     :null;

        if( !counter || (runBPartner == null) || (runBPartner.getID() != runC_BPartner_ID) ) {
            counter = false;
        }

        if( counter ) {
            log.info( "RunBP=" + runBPartner + " - " + m_docType );
        }

        log.info( "Single=" + m_run.isCreateSingleOrder() + " - " + m_docType + ",SO=" + m_docType.isSOTrx());
        log.fine( "Counter=" + counter + ",C_BPartner_ID=" + runC_BPartner_ID + "," + runBPartner );

        //

        MBPartner bp          = null;
        MOrder    singleOrder = null;
        MProduct  product     = null;

        // Consolidated Order

        if( m_run.isCreateSingleOrder()) {
            bp = new MBPartner( getCtx(),m_run.getC_BPartner_ID(),get_TrxName());

            if( bp.getID() == 0 ) {
                throw new IllegalArgumentException( "Business Partner not found - C_BPartner_ID=" + m_run.getC_BPartner_ID());
            }

            //

            if( !p_IsTest ) {
                singleOrder = new MOrder( getCtx(),0,get_TrxName());
                singleOrder.setC_DocTypeTarget_ID( m_docType.getC_DocType_ID());
                singleOrder.setC_DocType_ID( m_docType.getC_DocType_ID());
                singleOrder.setIsSOTrx( m_docType.isSOTrx());
                singleOrder.setBPartner( bp );

                if( m_run.getC_BPartner_Location_ID() != 0 ) {
                    singleOrder.setC_BPartner_Location_ID( m_run.getC_BPartner_Location_ID());
                }

                singleOrder.setDateOrdered( m_DateOrdered );
                singleOrder.setDatePromised( p_DatePromised );

                if( !singleOrder.save()) {
                    log.log( Level.SEVERE,"Order not saved" );

                    return false;
                }

                m_counter++;
            }
        }

        int    lastC_BPartner_ID          = 0;
        int    lastC_BPartner_Location_ID = 0;
        MOrder order                      = null;

        // For all lines

        for( int i = 0;i < m_details.length;i++ ) {
            MDistributionRunDetail detail = m_details[ i ];

            // Create Order Header

            if( m_run.isCreateSingleOrder()) {
                order = singleOrder;

                // New Business Partner

            } else if( (lastC_BPartner_ID != detail.getC_BPartner_ID()) || (lastC_BPartner_Location_ID != detail.getC_BPartner_Location_ID())) {

                // finish order

                order = null;
            }

            lastC_BPartner_ID          = detail.getC_BPartner_ID();
            lastC_BPartner_Location_ID = detail.getC_BPartner_Location_ID();

            // New Order

            if( order == null ) {
                bp = new MBPartner( getCtx(),detail.getC_BPartner_ID(),get_TrxName());

                if( !p_IsTest ) {
                    order = new MOrder( getCtx(),0,get_TrxName());
                    order.setC_DocTypeTarget_ID( m_docType.getC_DocType_ID());
                    order.setC_DocType_ID( m_docType.getC_DocType_ID());
                    order.setIsSOTrx( m_docType.isSOTrx());

                    // Counter Doc

                    if( counter && (bp.getAD_OrgBP_ID_Int() > 0) ) {
                        log.fine( "Counter - From_BPOrg=" + bp.getAD_OrgBP_ID_Int() + "-" + bp + ", To_BP=" + runBPartner );
                        order.setAD_Org_ID( bp.getAD_OrgBP_ID_Int());

                        MOrgInfo oi = MOrgInfo.get( getCtx(),bp.getAD_OrgBP_ID_Int());

                        if( oi.getM_Warehouse_ID() > 0 ) {
                            order.setM_Warehouse_ID( oi.getM_Warehouse_ID());
                        }

                        order.setBPartner( runBPartner );
                    } else    // normal
                    {
                        log.fine( "From_Org=" + runAD_Org_ID + ", To_BP=" + bp );
                        order.setAD_Org_ID( runAD_Org_ID );
                        order.setBPartner( bp );

                        if( detail.getC_BPartner_Location_ID() != 0 ) {
                            order.setC_BPartner_Location_ID( detail.getC_BPartner_Location_ID());
                        }
                    }

                    order.setDateOrdered( m_DateOrdered );
                    order.setDatePromised( p_DatePromised );

                    if( !order.save()) {
                        log.log( Level.SEVERE,"Order not saved" );

                        return false;
                    }
                }
            }

            // Line

            if( (product == null) || (product.getM_Product_ID() != detail.getM_Product_ID())) {
                product = MProduct.get( getCtx(),detail.getM_Product_ID());
            }

            if( p_IsTest ) {
                addLog( 0,null,detail.getActualAllocation(),bp.getName() + " - " + product.getName());

                continue;
            }

            // Create Order Line

            MOrderLine line = new MOrderLine( order );

            if( counter && (bp.getAD_OrgBP_ID_Int() > 0) ) {
                ;     // don't overwrite counter doc
            } else    // normal - optionally overwrite
            {
                line.setC_BPartner_ID( detail.getC_BPartner_ID());

                if( detail.getC_BPartner_Location_ID() != 0 ) {
                    line.setC_BPartner_Location_ID( detail.getC_BPartner_Location_ID());
                }
            }

            //

            line.setProduct( product );
            line.setQty( detail.getActualAllocation());
            line.setPrice();

            if( !line.save()) {
                log.log( Level.SEVERE,"OrderLine not saved" );

                return false;
            }

            addLog( 0,null,detail.getActualAllocation(),order.getDocumentNo() + ": " + bp.getName() + " - " + product.getName());
        }

        // finish order

        order = null;

        return true;
    }    // createOrders
}    // DistributionRun



/*
 *  @(#)DistributionRun.java   02.07.07
 * 
 *  Fin del fichero DistributionRun.java
 *  
 *  Versión 2.2
 *
 */
