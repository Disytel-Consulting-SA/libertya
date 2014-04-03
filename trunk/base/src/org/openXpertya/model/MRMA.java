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

public class MRMA extends X_M_RMA implements DocAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_RMA_ID
     * @param trxName
     */

    public MRMA( Properties ctx,int M_RMA_ID,String trxName ) {
        super( ctx,M_RMA_ID,trxName );

        if( M_RMA_ID == 0 ) {

            // setName (null);
            // setSalesRep_ID (0);
            // setC_DocType_ID (0);
            // setM_InOut_ID (0);

            setDocAction( DOCACTION_Complete );    // CO
            setDocStatus( DOCSTATUS_Drafted );     // DR
            setIsApproved( false );
            setProcessed( false );
        }
    }                                              // MRMA

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRMA( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRMA

    /** Descripción de Campos */

    private MRMALine[] m_lines = null;

    /** Descripción de Campos */

    private MInOut m_inout = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MRMALine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM M_RMALine WHERE M_RMA_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_RMA_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRMALine( getCtx(),rs,get_TrxName()));
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

        m_lines = new MRMALine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInOut getShipment() {
        if( (m_inout == null) && (getM_InOut_ID() != 0) ) {
            m_inout = new MInOut( getCtx(),getM_InOut_ID(),get_TrxName());
        }

        return m_inout;
    }    // getShipment

    /**
     * Descripción de Método
     *
     *
     * @param M_InOut_ID
     */

    public void setM_InOut_ID( int M_InOut_ID ) {
        super.setM_InOut_ID( M_InOut_ID );
        setC_Currency_ID( 0 );
        setAmt( Env.ZERO );
        setC_BPartner_ID( 0 );
        m_inout = null;
    }    // setM_InOut_ID

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Set BPartner

        if( getC_BPartner_ID() == 0 ) {
            getShipment();

            if( m_inout != null ) {
                setC_BPartner_ID( m_inout.getC_BPartner_ID());
            }
        }

        // Set Currency

        if( getC_Currency_ID() == 0 ) {
            getShipment();

            if( m_inout != null ) {
                if( m_inout.getC_Order_ID() != 0 ) {
                    MOrder order = new MOrder( getCtx(),m_inout.getC_Order_ID(),get_TrxName());

                    setC_Currency_ID( order.getC_Currency_ID());
                } else if( m_inout.getC_Invoice_ID() != 0 ) {
                    MInvoice invoice = new MInvoice( getCtx(),m_inout.getC_Invoice_ID(),get_TrxName());

                    setC_Currency_ID( invoice.getC_Currency_ID());
                }
            }
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

        MDocType   dt    = MDocType.get( getCtx(),getC_DocType_ID());
        MRMALine[] lines = getLines( false );

        if( lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Check Lines

        BigDecimal amt = Env.ZERO;

        for( int i = 0;i < lines.length;i++ ) {
            MRMALine line = lines[ i ];

            amt = amt.add( line.getAmt());
        }

        setAmt( amt );
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

    	if (!m_justPrepared	&& !existsJustPreparedDoc()) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }

        log.info( "completeIt - " + toString());

        //

        if( true ) {
            m_processMsg = "Need to code creating the credit memo";

            return DocAction.STATUS_InProgress;
        }

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        //

        setProcessed( true );
        setDocAction( DOCACTION_Close );

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

        // Revoke Credit

        return false;
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( "closeIt - " + toString());

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

        // : Total Lines = 123.00 (#1)

        sb.append( ": " ).append( Msg.translate( getCtx(),"Amt" )).append( "=" ).append( getAmt()).append( " (#" ).append( getLines( false ).length ).append( ")" );

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
        return getSalesRep_ID();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        return getAmt();
    }    // getApprovalAmt
}    // MRMA



/*
 *  @(#)MRMA.java   02.07.07
 * 
 *  Fin del fichero MRMA.java
 *  
 *  Versión 2.2
 *
 */
