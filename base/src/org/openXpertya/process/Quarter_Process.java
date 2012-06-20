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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MQuarter;
import org.openXpertya.model.MQuarterCategory;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Quarter_Process extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public Quarter_Process() {
        super();
    }    // Quarter_Process

    /** Descripción de Campos */

    private boolean m_OutGoings = true;

    /** Descripción de Campos */

    private boolean m_Tam12 = true;

    /** Descripción de Campos */

    private boolean m_Tam3 = true;

    /** Descripción de Campos */

    private boolean m_Stock = true;

    /** Descripción de Campos */

    private boolean m_Incomings = true;

    /** Descripción de Campos */

    private int p_M_Warehouse_ID = 0;

    /** Descripción de Campos */

    StringBuffer info = new StringBuffer( "Quarter_Process - Calculate parameters: " );

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
                log.fine("M_WArehouse_prepare_QuarterProcess="+p_M_Warehouse_ID);
            } else if( name.equals( "OutGoings" )) {
                m_OutGoings = "Y".equals( para[ i ].getParameter());
                log.fine("M_outgoings_prepare_QuarterProcess="+m_OutGoings+para[ i ].getParameter());
            } else if( name.equals( "Tam12" )) {
                m_Tam12 = "Y".equals( para[ i ].getParameter());
                log.fine("M_Tam12_prepare_QuarterProcess="+m_Tam12+para[ i ].getParameter());
            } else if( name.equals( "Tam3" )) {
                m_Tam3 = "Y".equals( para[ i ].getParameter());
                log.fine("M_Tam3_prepare_QuarterProcess="+m_Tam3+para[ i ].getParameter());
            } else if( name.equals( "Stock" )) {
                m_Stock = "Y".equals( para[ i ].getParameter());
                log.fine("M_stock_prepare_QuarterProcess="+m_Stock+para[ i ].getParameter());
            } else if( name.equals( "Incomings" )) {
                m_Incomings = "Y".equals( para[ i ].getParameter());
                log.fine("M_Incomings_prepare_QuarterProcess="+m_Incomings+para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }

            if( "Y".equals( para[ i ].getParameter())) {
                info.append( para[ i ].getParameterName()).append( " - " );
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
        log.info( info.toString());
        log.fine("Llego a doIt en QuarterProcess");
        if( super.getRecord_ID() < 1 ) {
            throw new Exception( MSG_SaveErrorRowNotFound );
        }

        MQuarter to_MQ  = new MQuarter( getCtx(),super.getRecord_ID(),null );
        MQuarter pre_MQ = to_MQ.getPrevious();

        // copy all except name and product category

        MQuarterCategory to_MQC = new MQuarterCategory( getCtx(),to_MQ.getC_Quarter_Category_ID(),null );
        MQuarterCategory pre_MQC  = to_MQC.getPreviousBySeq();
        MQuarterCategory next_MQC = to_MQC.getNextBySeq();

        // OutGoings

        BigDecimal to_outgoings = new BigDecimal( 0 );

        if( (pre_MQ != null) && (pre_MQ.getTam12() != null) && (pre_MQ.getTam3() != null) && (to_MQC.getDistribution() != null) ) {
            double preTam12 = pre_MQ.getTam12().doubleValue();
            double preTam3  = pre_MQ.getTam3().doubleValue();
            double toDistri = to_MQC.getDistribution().doubleValue();

            to_outgoings = new BigDecimal(( preTam12 + preTam3 ) * toDistri / 200 );
        }

        if( m_OutGoings ) {
            to_MQ.setOutGoings( to_outgoings );
        }

        to_MQ.save();

        // TAM 12

        double     to_tam12 = 0;
        MQuarter[] pre12_MQ = to_MQ.getPrevious( 12 );

        for( int i = 0;i < 12;i++ ) {
            if( pre12_MQ[ i ] != null ) {
                to_tam12 += pre12_MQ[ i ].getOutGoings().doubleValue();
            } else {
                to_tam12 = 0;
            }
        }

        if( m_Tam12 ) {
            to_MQ.setTam12( new BigDecimal( to_tam12 ));
        }

        to_MQ.save();

        // TAM 3

        double             to_tam3           = 0;
        double             sum3_OutGoings    = 0;
        double             sum3_Distribution = 0;
        MQuarter[]         pre3_MQ           = to_MQ.getPrevious( 3 );
        MQuarterCategory[] pre3_MQC          = to_MQC.getPreviousBySeq( 3 );

        for( int i = 0;i < 3;i++ ) {
            if( pre3_MQ[ i ] != null ) {
                sum3_OutGoings += pre3_MQ[ i ].getOutGoings().doubleValue();
            } else {
                sum3_OutGoings = 0;
            }

            if( pre3_MQC[ i ] != null ) {
                sum3_Distribution += pre3_MQC[ i ].getDistribution().doubleValue();
            } else {
                sum3_Distribution = 0;
            }
        }

        if(( sum3_OutGoings != 0 ) & ( sum3_Distribution != 0 )) {
            to_tam3 = ( sum3_OutGoings / sum3_Distribution ) * 100;
        }

        if( m_Tam3 ) {
            to_MQ.setTam3( new BigDecimal( to_tam3 ));
        }

        to_MQ.save();

        // STOCK

        if( m_Stock ) {
        	log.fine("Log del m_Stock");
            StringBuffer sql = new StringBuffer( "SELECT BOM_Qty_OnHand(p.M_Product_ID,?) AS QtyOnHand" );

            sql.append( " FROM M_Product p" );
            sql.append( " WHERE p.IsActive='Y' AND p.IsSummary='N' AND p.m_product_id=?" );

            int               to_stock = 0;
            PreparedStatement pstmt    = null;

            try {
                pstmt = DB.prepareStatement( sql.toString());
                pstmt.setInt( 1,p_M_Warehouse_ID );
                pstmt.setInt( 2,to_MQ.getM_Product_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    to_stock = rs.getInt( 1 );
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"getPrevious",e );
            }

            to_MQ.setStock( to_stock );
            to_MQ.save();
        }

        // INCOMINGS

        if( m_Incomings ) {
        	log.fine("If del m_Incomings");
            float to_incomings = 0;

            if( (pre_MQ != null) && (next_MQC != null) && (to_MQC.getStockTime() != 0) && (next_MQC.getDistribution() != null) ) {
                log.fine("If del if de m_Incomings");
            	float toOut    = to_MQ.getOutGoings().floatValue();
                float toTam12  = to_MQ.getTam12().floatValue();
                float toTam3   = to_MQ.getTam3().floatValue();
                int   toST     = to_MQC.getStockTime();
                int   preStock = pre_MQ.getStock();
                float nextDist = next_MQC.getDistribution().floatValue();

                to_incomings = toOut - preStock + (( toTam12 + toTam3 ) * nextDist * toST / 6000 );
            }

            to_MQ.setIncomings( Math.round( to_incomings ));
        }

        to_MQ.save();
        
        return "@Calculated@=" + info.toString();
    }    // doIt
}    // ReportLineSet_Copy



/*
 *  @(#)Quarter_Process.java   02.07.07
 * 
 *  Fin del fichero Quarter_Process.java
 *  
 *  Versión 2.2
 *
 */
