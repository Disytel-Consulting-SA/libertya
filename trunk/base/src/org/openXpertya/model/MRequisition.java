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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRequisition extends X_M_Requisition implements DocAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Requisition_ID
     * @param trxName
     */

    public MRequisition( Properties ctx,int M_Requisition_ID,String trxName ) {
        super( ctx,M_Requisition_ID,trxName );

        if( M_Requisition_ID == 0 ) {

            // setDocumentNo (null);
            // setAD_User_ID (0);
            // setM_PriceList_ID (0);
            // setM_Warehouse_ID(0);

            setDateRequired( new Timestamp( System.currentTimeMillis()));
            setDocAction( DocAction.ACTION_Complete );    // CO
            setDocStatus( DocAction.STATUS_Drafted );     // DR
            setPriorityRule( PRIORITYRULE_Medium );       // 5
            setTotalLines( Env.ZERO );
            setIsApproved( false );
            setPosted( false );
            setProcessed( false );
        }
    }                                                     // MRequisition

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRequisition( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRequisition

    /** Descripción de Campos */

    private MRequisitionLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequisitionLine[] getLines() {
        if( m_lines != null ) {
            return m_lines;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_RequisitionLine WHERE M_Requisition_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_Requisition_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRequisitionLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_lines = new MRequisitionLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRequisition[" );

        sb.append( getID()).append( "-" ).append( getDocumentNo()).append( ",Status=" ).append( getDocStatus()).append( ",Action=" ).append( getDocAction()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     */

    public void setM_PriceList_ID() {
        MPriceList defaultPL = MPriceList.getDefault( getCtx(),false );

        if( defaultPL == null ) {
            defaultPL = MPriceList.getDefault( getCtx(),true );
        }

        if( defaultPL != null ) {
            setM_PriceList_ID( defaultPL.getM_PriceList_ID());
        }
    }    // setM_PriceList_ID()

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getM_PriceList_ID() == 0 ) {
            setM_PriceList_ID();
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param processAction
     *
     * @return
     */

    public boolean processIt( String processAction ) {
        m_processMsg = null;

        DocumentEngine engine = new DocumentEngine( this,getDocStatus());

        return engine.processIt( processAction,getDocAction(),log );
    }    // process


    /** Descripción de Campos */

    private boolean m_justPrepared = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt() {
        log.info( "unlockIt - " + toString());
        setProcessing( false );

        return true;
    }    // unlockIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean invalidateIt() {
        log.info( "invalidateIt - " + toString());

        return true;
    }    // invalidateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String prepareIt() {
        log.info( toString());
        m_processMsg = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_BEFORE_PREPARE );

        if( m_processMsg != null ) {
            return DocAction.STATUS_Invalid;
        }

        MRequisitionLine[] lines = getLines();

        // Invalid

        if( (getAD_User_ID() == 0) || (getM_PriceList_ID() == 0) || (getM_Warehouse_ID() == 0) || (lines.length == 0) ) {
            return DocAction.STATUS_Invalid;
        }

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getDateRequired(),MDocType.DOCBASETYPE_PurchaseRequisition )) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        // Add up Amounts

        int precision = MPriceList.getStandardPrecision( getCtx(),getM_PriceList_ID());
        BigDecimal totalLines = Env.ZERO;

        for( int i = 0;i < lines.length;i++ ) {
            MRequisitionLine line    = lines[ i ];
            BigDecimal       lineNet = line.getQty().multiply( line.getPriceActual());

            lineNet = lineNet.setScale( precision,BigDecimal.ROUND_HALF_UP );

            if( lineNet.compareTo( line.getLineNetAmt()) != 0 ) {
                line.setLineNetAmt( lineNet );
                line.save();
            }

            totalLines = totalLines.add( line.getLineNetAmt());
        }

        if( totalLines.compareTo( getTotalLines()) != 0 ) {
            setTotalLines( totalLines );
            save();
        }

        m_justPrepared = true;

        return DocAction.STATUS_InProgress;
    }    // prepareIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt() {
        log.info( "approveIt - " + toString());
        setIsApproved( true );

        return true;
    }    // approveIt

    /*
     * begin vpj-cd e-evolution 01/25/2005
     *
     *      After Save
     *      @param newRecord new
     *      @param success success
     */

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
        if( !success || newRecord ) {
            return success;
        }

        MMPCMRP.M_Requisition( this,get_TrxName());

        return true;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        if( isProcessed()) {
            return false;
        }

        getLines();

        for( int i = 0;i < m_lines.length;i++ ) {
            if( !m_lines[ i ].beforeDelete()) {
                return false;
            }
        }

        return true;
    }    // beforeDelete

    /*
     * end vpj-cd e-evolution 01/25/2005
     *
     *
     *
     *      Reject Approval
     *      @return true if success
     */

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rejectIt() {
        log.info( "rejectIt - " + toString());
        setIsApproved( false );

        return true;
    }    // rejectIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String completeIt() {

        // Re-Check

        if( !m_justPrepared ) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }

        log.info( toString());

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        //

        setProcessed( true );
        setDocAction( ACTION_Close );

        return DocAction.STATUS_Completed;
    }    // completeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt() {
        log.info( "postIt - " + toString());

        return false;
    }    // postIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean voidIt() {
        log.info( "voidIt - " + toString());

        return closeIt();
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( "closeIt - " + toString());

        // Close Not delivered Qty

        MRequisitionLine[] lines      = getLines();
        BigDecimal         totalLines = Env.ZERO;

        for( int i = 0;i < lines.length;i++ ) {
            MRequisitionLine line     = lines[ i ];
            BigDecimal       finalQty = line.getQty();

            if( line.getC_OrderLine_ID() == 0 ) {
                finalQty = Env.ZERO;
            } else {
                MOrderLine ol = new MOrderLine( getCtx(),line.getC_OrderLine_ID(),get_TrxName());

                finalQty = ol.getQtyOrdered();
            }

            // final qty is not line qty

            if( finalQty.compareTo( line.getQty()) != 0 ) {
                String description = line.getDescription();

                if( description == null ) {
                    description = "";
                }

                description += " [" + line.getQty() + "]";
                line.setDescription( description );
                line.setQty( finalQty );
                line.setLineNetAmt();
                line.save();
            }

            totalLines = totalLines.add( line.getLineNetAmt());
        }

        if( totalLines.compareTo( getTotalLines()) != 0 ) {
            setTotalLines( totalLines );
            save();
        }

        return true;
    }    // closeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt() {
        log.info( "reverseCorrectIt - " + toString());

        return false;
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( "reverseAccrualIt - " + toString());

        return false;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        log.info( "reActivateIt - " + toString());

        // setProcessed(false);

        if( reverseCorrectIt()) {
            return true;
        }

        return false;
    }    // reActivateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        StringBuffer sb = new StringBuffer();

        sb.append( getDocumentNo());

        // - User

        sb.append( " - " ).append( getUserName());

        // : Total Lines = 123.00 (#1)

        sb.append( ": " ).append( Msg.translate( getCtx(),"TotalLines" )).append( "=" ).append( getTotalLines()).append( " (#" ).append( getLines().length ).append( ")" );

        // - Description

        if( (getDescription() != null) && (getDescription().length() > 0) ) {
            sb.append( " - " ).append( getDescription());
        }

        return sb.toString();
    }    // getSummary


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID() {
        return getAD_User_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        MPriceList pl = MPriceList.get( getCtx(),getM_PriceList_ID(),get_TrxName());

        return pl.getC_Currency_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        return getTotalLines();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getUserName() {
        return MUser.get( getCtx(),getAD_User_ID()).getName();
    }    // getUserName
}    // MRequisition



/*
 *  @(#)MRequisition.java   02.07.07
 * 
 *  Fin del fichero MRequisition.java
 *  
 *  Versión 2.2
 *
 */
