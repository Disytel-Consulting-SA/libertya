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

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectIssue;
import org.openXpertya.model.MProjectLine;
import org.openXpertya.model.MStorage;
import org.openXpertya.model.MTimeExpense;
import org.openXpertya.model.MTimeExpenseLine;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProjectIssue extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_Project_ID = 0;

    /** Descripción de Campos */

    private int m_M_InOut_ID = 0;

    /** Descripción de Campos */

    private int m_S_TimeExpense_ID = 0;

    /** Descripción de Campos */

    private int m_M_Locator_ID = 0;

    /** Descripción de Campos */

    private int m_C_ProjectLine_ID = 0;

    /** Descripción de Campos */

    private int m_M_Product_ID = 0;

    /** Descripción de Campos */

    private int m_M_AttributeSetInstance_ID = 0;

    /** Descripción de Campos */

    private BigDecimal m_MovementQty = null;

    /** Descripción de Campos */

    private Timestamp m_MovementDate = null;

    /** Descripción de Campos */

    private String m_Description = null;

    /** Descripción de Campos */

    private MProject m_project = null;

    /** Descripción de Campos */

    private MProjectIssue[] m_projectIssues = null;

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
            } else if( name.equals( "C_Project_ID" )) {
                m_C_Project_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "M_InOut_ID" )) {
                m_M_InOut_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "S_TimeExpense_ID" )) {
                m_S_TimeExpense_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "M_Locator_ID" )) {
                m_M_Locator_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_ProjectLine_ID" )) {
                m_C_ProjectLine_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "M_Product_ID" )) {
                m_M_Product_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "M_AttributeSetInstance_ID" )) {
                m_M_AttributeSetInstance_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "MovementQty" )) {
                m_MovementQty = ( BigDecimal )para[ i ].getParameter();
            } else if( name.equals( "MovementDate" )) {
                m_MovementDate = ( Timestamp )para[ i ].getParameter();
            } else if( name.equals( "Description" )) {
                m_Description = ( String )para[ i ].getParameter();
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

        // Check Parameter

        m_project = new MProject( getCtx(),m_C_Project_ID,get_TrxName());

        if( !( MProject.PROJECTCATEGORY_WorkOrderJob.equals( m_project.getProjectCategory()) || MProject.PROJECTCATEGORY_AssetProject.equals( m_project.getProjectCategory()))) {
            throw new IllegalArgumentException( "Project not Work Order or Asset =" + m_project.getProjectCategory());
        }

        log.info( "doIt - " + m_project );

        //

        if( m_M_InOut_ID != 0 ) {
            return issueReceipt();
        }

        if( m_S_TimeExpense_ID != 0 ) {
            return issueExpense();
        }

        if( m_M_Locator_ID == 0 ) {
            throw new IllegalArgumentException( "Locator missing" );
        }

        if( m_C_ProjectLine_ID != 0 ) {
            return issueProjectLine();
        }

        return issueInventory();
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String issueReceipt() {
        MInOut inOut = new MInOut( getCtx(),m_M_InOut_ID,null );

        if( inOut.isSOTrx() ||!inOut.isProcessed() ||!( MInOut.DOCSTATUS_Completed.equals( inOut.getDocStatus()) || MInOut.DOCSTATUS_Closed.equals( inOut.getDocStatus()))) {
            throw new IllegalArgumentException( "Receipt not valid - " + inOut );
        }

        log.info( "issueReceipt - " + inOut );

        // Set Project of Receipt

        if( inOut.getC_Project_ID() == 0 ) {
            inOut.setC_Project_ID( m_project.getC_Project_ID());
            inOut.save();
        } else if( inOut.getC_Project_ID() == m_project.getC_Project_ID()) {
            throw new IllegalArgumentException( "Receipt for other Project" );
        }

        MInOutLine[] inOutLines = inOut.getLines( false );
        int          counter    = 0;

        for( int i = 0;i < inOutLines.length;i++ ) {

            // Need to have a Product

            if( inOutLines[ i ].getM_Product_ID() == 0 ) {
                continue;
            }

            // Need to have Quantity

            if( (inOutLines[ i ].getMovementQty() == null) || (inOutLines[ i ].getMovementQty().compareTo( Env.ZERO ) == 0) ) {
                continue;
            }

            // not issued yet

            if( projectIssueHasReceipt( inOutLines[ i ].getM_InOutLine_ID())) {
                continue;
            }

            // Create Issue

            MProjectIssue pi = new MProjectIssue( m_project );

            pi.setMandatory( inOutLines[ i ].getM_Locator_ID(),inOutLines[ i ].getM_Product_ID(),inOutLines[ i ].getMovementQty());

            if( m_MovementDate != null ) {    // default today
                pi.setMovementDate( m_MovementDate );
            }

            if( (m_Description != null) && (m_Description.length() > 0) ) {
                pi.setDescription( m_Description );
            } else if( inOutLines[ i ].getDescription() != null ) {
                pi.setDescription( inOutLines[ i ].getDescription());
            } else if( inOut.getDescription() != null ) {
                pi.setDescription( inOut.getDescription());
            }

            pi.setM_InOutLine_ID( inOutLines[ i ].getM_InOutLine_ID());
            pi.process();

            // Find/Create Project Line

            MProjectLine   pl  = null;
            MProjectLine[] pls = m_project.getLines();

            for( int ii = 0;ii < pls.length;ii++ ) {

                // The Order we generated is the same as the Order of the receipt

                if( (pls[ ii ].getC_OrderPO_ID() == inOut.getC_Order_ID()) && (pls[ ii ].getM_Product_ID() == inOutLines[ i ].getM_Product_ID()) && (pls[ ii ].getC_ProjectIssue_ID() == 0) )    // not issued
                {
                    pl = pls[ ii ];

                    break;
                }
            }

            if( pl == null ) {
                pl = new MProjectLine( m_project );
            }

            pl.setMProjectIssue( pi );    // setIssue
            pl.save();
            addLog( pi.getLine(),pi.getMovementDate(),pi.getMovementQty(),null );
            counter++;
        }    // all InOutLines

        return "@Created@ " + counter;
    }    // issueReceipt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String issueExpense() {

        // Get Expense Report

        MTimeExpense expense = new MTimeExpense( getCtx(),m_S_TimeExpense_ID,get_TrxName());

        if( !expense.isProcessed()) {
            throw new IllegalArgumentException( "Time+Expense not processed - " + expense );
        }

        // for all expense lines

        MTimeExpenseLine[] expenseLines = expense.getLines( false );
        int                counter      = 0;

        for( int i = 0;i < expenseLines.length;i++ ) {

            // Need to have a Product

            if( expenseLines[ i ].getM_Product_ID() == 0 ) {
                continue;
            }

            // Need to have Quantity

            if( (expenseLines[ i ].getQty() == null) || (expenseLines[ i ].getQty().signum() == 0) ) {
                continue;
            }

            // Need to the same project

            if( expenseLines[ i ].getC_Project_ID() != m_project.getC_Project_ID()) {
                continue;
            }

            // not issued yet

            if( projectIssueHasExpense( expenseLines[ i ].getS_TimeExpenseLine_ID())) {
                continue;
            }

            // Find Location

            int M_Locator_ID = 0;

            // MProduct product = new MProduct (getCtx(), expenseLines[i].getM_Product_ID());
            // if (product.isStocked())

            M_Locator_ID = MStorage.getM_Locator_ID( expense.getM_Warehouse_ID(),expenseLines[ i ].getM_Product_ID(),0,    // no ASI
                    expenseLines[ i ].getQty(),null );

            if( M_Locator_ID == 0 ) {    // Service/Expense - get default (and fallback)
                M_Locator_ID = expense.getM_Locator_ID();
            }

            // Create Issue

            MProjectIssue pi = new MProjectIssue( m_project );

            pi.setMandatory( M_Locator_ID,expenseLines[ i ].getM_Product_ID(),expenseLines[ i ].getQty());

            if( m_MovementDate != null ) {    // default today
                pi.setMovementDate( m_MovementDate );
            }

            if( (m_Description != null) && (m_Description.length() > 0) ) {
                pi.setDescription( m_Description );
            } else if( expenseLines[ i ].getDescription() != null ) {
                pi.setDescription( expenseLines[ i ].getDescription());
            }

            pi.setS_TimeExpenseLine_ID( expenseLines[ i ].getS_TimeExpenseLine_ID());
            pi.process();

            // Find/Create Project Line

            MProjectLine pl = new MProjectLine( m_project );

            pl.setMProjectIssue( pi );    // setIssue
            pl.save();
            addLog( pi.getLine(),pi.getMovementDate(),pi.getMovementQty(),null );
            counter++;
        }    // allExpenseLines

        return "@Created@ " + counter;
    }    // issueExpense

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String issueProjectLine() {
        MProjectLine pl = new MProjectLine( getCtx(),m_C_ProjectLine_ID,get_TrxName());

        if( pl.getM_Product_ID() == 0 ) {
            throw new IllegalArgumentException( "Projet Line has no Product" );
        }

        if( pl.getC_ProjectIssue_ID() != 0 ) {
            throw new IllegalArgumentException( "Projet Line already been issued" );
        }

        if( m_M_Locator_ID == 0 ) {
            throw new IllegalArgumentException( "No Locator" );
        }

        // Set to Qty 1

        if( (pl.getPlannedQty() == null) || (pl.getPlannedQty().compareTo( Env.ZERO ) == 0) ) {
            pl.setPlannedQty( Env.ONE );
        }

        //

        MProjectIssue pi = new MProjectIssue( m_project );

        pi.setMandatory( m_M_Locator_ID,pl.getM_Product_ID(),pl.getPlannedQty());

        if( m_MovementDate != null ) {    // default today
            pi.setMovementDate( m_MovementDate );
        }

        if( (m_Description != null) && (m_Description.length() > 0) ) {
            pi.setDescription( m_Description );
        } else if( pl.getDescription() != null ) {
            pi.setDescription( pl.getDescription());
        }

        pi.process();

        // Update Line

        pl.setMProjectIssue( pi );
        pl.save();
        addLog( pi.getLine(),pi.getMovementDate(),pi.getMovementQty(),null );

        return "@Created@ 1";
    }    // issueProjectLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String issueInventory() {
        if( m_M_Locator_ID == 0 ) {
            throw new IllegalArgumentException( "No Locator" );
        }

        if( m_M_Product_ID == 0 ) {
            throw new IllegalArgumentException( "No Product" );
        }

        // Set to Qty 1

        if( (m_MovementQty == null) || (m_MovementQty.compareTo( Env.ZERO ) == 0) ) {
            m_MovementQty = Env.ONE;
        }

        //

        MProjectIssue pi = new MProjectIssue( m_project );

        pi.setMandatory( m_M_Locator_ID,m_M_Product_ID,m_MovementQty );

        if( m_MovementDate != null ) {    // default today
            pi.setMovementDate( m_MovementDate );
        }

        if( (m_Description != null) && (m_Description.length() > 0) ) {
            pi.setDescription( m_Description );
        }

        pi.process();

        // Create Project Line

        MProjectLine pl = new MProjectLine( m_project );

        pl.setMProjectIssue( pi );
        pl.save();
        addLog( pi.getLine(),pi.getMovementDate(),pi.getMovementQty(),null );

        return "@Created@ 1";
    }    // issueInventory

    /**
     * Descripción de Método
     *
     *
     * @param S_TimeExpenseLine_ID
     *
     * @return
     */

    private boolean projectIssueHasExpense( int S_TimeExpenseLine_ID ) {
        if( m_projectIssues == null ) {
            m_projectIssues = m_project.getIssues();
        }

        for( int i = 0;i < m_projectIssues.length;i++ ) {
            if( m_projectIssues[ i ].getS_TimeExpenseLine_ID() == S_TimeExpenseLine_ID ) {
                return true;
            }
        }

        return false;
    }    // projectIssueHasExpense

    /**
     * Descripción de Método
     *
     *
     * @param M_InOutLine_ID
     *
     * @return
     */

    private boolean projectIssueHasReceipt( int M_InOutLine_ID ) {
        if( m_projectIssues == null ) {
            m_projectIssues = m_project.getIssues();
        }

        for( int i = 0;i < m_projectIssues.length;i++ ) {
            if( m_projectIssues[ i ].getM_InOutLine_ID() == M_InOutLine_ID ) {
                return true;
            }
        }

        return false;
    }    // projectIssueHasReceipt
}    // ProjectIssue



/*
 *  @(#)ProjectIssue.java   02.07.07
 * 
 *  Fin del fichero ProjectIssue.java
 *  
 *  Versión 2.2
 *
 */
