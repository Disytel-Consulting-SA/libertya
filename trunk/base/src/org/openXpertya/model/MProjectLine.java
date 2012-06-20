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



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProjectLine extends X_C_ProjectLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_ProjectLine_ID
     * @param trxName
     */

    public MProjectLine( Properties ctx,int C_ProjectLine_ID,String trxName ) {
        super( ctx,C_ProjectLine_ID,trxName );

        if( C_ProjectLine_ID == 0 ) {

            // setC_Project_ID (0);
            // setC_ProjectLine_ID (0);

            setLine( 0 );
            setIsPrinted( true );
            setProcessed( false );
            setInvoicedAmt( Env.ZERO );
            setInvoicedQty( Env.ZERO );

            //

            setPlannedAmt( Env.ZERO );
            setPlannedMarginAmt( Env.ZERO );
            setPlannedPrice( Env.ZERO );
            setPlannedQty( Env.ONE );
        }
    }    // MProjectLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProjectLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProjectLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param project
     */

    public MProjectLine( MProject project ) {
        this( project.getCtx(),0,project.get_TrxName());
        setClientOrg( project );
        setC_Project_ID( project.getC_Project_ID());    // Parent
        setLine();
    }                                                   // MProjectLine

    /** Descripción de Campos */

    private MProject m_parent = null;

    /**
     * Descripción de Método
     *
     *
     * @param AD_Org_ID
     */

    public void setAD_Org_ID( int AD_Org_ID ) {
        super.setAD_Org_ID( AD_Org_ID );
    }    // setAD_Org_ID

    /**
     * Descripción de Método
     *
     */

    private void setLine() {
        setLine( DB.getSQLValue( get_TrxName(),"SELECT COALESCE(MAX(Line),0)+10 FROM C_ProjectLine WHERE C_Project_ID=?",getC_Project_ID()));
    }    // setLine

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void setMProjectIssue( MProjectIssue pi ) {
        setC_ProjectIssue_ID( pi.getC_ProjectIssue_ID());
        setM_Product_ID( pi.getM_Product_ID());
        setCommittedQty( pi.getMovementQty());

        if( getDescription() != null ) {
            setDescription( pi.getDescription());
        }
    }    // setMProjectIssue

    /**
     * Descripción de Método
     *
     *
     * @param C_OrderPO_ID
     */

    public void setC_OrderPO_ID( int C_OrderPO_ID ) {
        super.setC_OrderPO_ID( C_OrderPO_ID );
    }    // setC_OrderPO_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProject getProject() {
        if( (m_parent == null) && (getC_Project_ID() != 0) ) {
            m_parent = new MProject( getCtx(),getC_Project_ID(),get_TrxName());

            if( get_TrxName() != null ) {
                m_parent.load( get_TrxName());
            }
        }

        return m_parent;
    }    // getProject

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getLimitPrice() {
        BigDecimal limitPrice = getPlannedPrice();

        if( getM_Product_ID() == 0 ) {
            return limitPrice;
        }

        if( getProject() == null ) {
            return limitPrice;
        }

        boolean         isSOTrx = true;
        MProductPricing pp      = new MProductPricing( getM_Product_ID(),m_parent.getC_BPartner_ID(),getPlannedQty(),isSOTrx );

        pp.setM_PriceList_ID( m_parent.getM_PriceList_ID());

        if( pp.calculatePrice()) {
            limitPrice = pp.getPriceLimit();
        }

        return limitPrice;
    }    // getLimitPrice

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getLine() == 0 ) {
            setLine();
        }

        // Planned Amount

        setPlannedAmt( getPlannedQty().multiply( getPlannedPrice()));

        // Planned Margin

        if( is_ValueChanged( "M_Product_ID" ) || is_ValueChanged( "M_Product_Category_ID" ) || is_ValueChanged( "PlannedQty" ) || is_ValueChanged( "PlannedPrice" )) {
            if( getM_Product_ID() != 0 ) {
                BigDecimal marginEach = getPlannedPrice().subtract( getLimitPrice());

                setPlannedMarginAmt( marginEach.multiply( getPlannedQty()));
            } else if( getM_Product_Category_ID() != 0 ) {
                MProductCategory category = MProductCategory.get( getCtx(),getM_Product_Category_ID(), get_TrxName());
                BigDecimal marginEach = category.getPlannedMargin();

                setPlannedMarginAmt( marginEach.multiply( getPlannedQty()));
            }
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        updateHeader();

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        updateHeader();

        return success;
    }    // afterDelete

    /**
     * Descripción de Método
     *
     */

    private void updateHeader() {
        String sql = "UPDATE C_Project p " + "SET (PlannedAmt,PlannedQty,PlannedMarginAmt," + "     CommittedAmt,CommittedQty," + " InvoicedAmt, InvoicedQty) = " + "(SELECT COALESCE(SUM(pl.PlannedAmt),0),COALESCE(SUM(pl.PlannedQty),0),COALESCE(SUM(pl.PlannedMarginAmt),0)," + " COALESCE(SUM(pl.CommittedAmt),0),COALESCE(SUM(pl.CommittedQty),0)," + " COALESCE(SUM(pl.InvoicedAmt),0), COALESCE(SUM(pl.InvoicedQty),0) " + "FROM C_ProjectLine pl " + "WHERE pl.C_Project_ID=p.C_Project_ID AND pl.IsActive='Y') " + "WHERE C_Project_ID=" + getC_Project_ID();
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"updateHeader - #" + no );
        }
    }    // updateHeader
}    // MProjectLine



/*
 *  @(#)MProjectLine.java   02.07.07
 * 
 *  Fin del fichero MProjectLine.java
 *  
 *  Versión 2.2
 *
 */
