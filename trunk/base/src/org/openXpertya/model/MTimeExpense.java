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
import java.sql.SQLException;
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

public class MTimeExpense extends X_S_TimeExpense implements DocAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param S_TimeExpense_ID
     * @param trxName
     */

    public MTimeExpense( Properties ctx,int S_TimeExpense_ID,String trxName ) {
        super( ctx,S_TimeExpense_ID,trxName );

        if( S_TimeExpense_ID == 0 ) {

            // setC_BPartner_ID (0);

            setDateReport( new Timestamp( System.currentTimeMillis()));

            // setDocumentNo (null);

            setIsApproved( false );

            // setM_PriceList_ID (0);
            // setM_Warehouse_ID (0);

            super.setProcessed( false );
            setProcessing( false );
        }
    }    // MTimeExpense

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MTimeExpense( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MTimeExpense

    /** Descripción de Campos */

    private int m_M_Locator_ID = 0;

    /** Descripción de Campos */

    private MTimeExpenseLine[] m_lines = null;

    /** Descripción de Campos */

    private int m_AD_User_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MTimeExpenseLine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        //

        int       C_Currency_ID = getC_Currency_ID();
        ArrayList list          = new ArrayList();

        //

        String sql = "SELECT * FROM S_TimeExpenseLine WHERE S_TimeExpense_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getS_TimeExpense_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MTimeExpenseLine te = new MTimeExpenseLine( getCtx(),rs,get_TrxName());

                te.setC_Currency_Report_ID( C_Currency_ID );
                list.add( te );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getLines",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        m_lines = new MTimeExpenseLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String desc = getDescription();

        if( desc == null ) {
            setDescription( description );
        } else {
            setDescription( desc + " | " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_Locator_ID() {
        if( m_M_Locator_ID != 0 ) {
            return m_M_Locator_ID;
        }

        //

        String sql = "SELECT M_Locator_ID FROM M_Locator " + "WHERE M_Warehouse_ID=? AND IsActive='Y' ORDER BY IsDefault DESC, Created";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getM_Warehouse_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_M_Locator_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getM_Locator_ID",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        return m_M_Locator_ID;
    }    // getM_Locator_ID

    /**
     * Descripción de Método
     *
     *
     * @param processed
     */

    public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        if( getID() == 0 ) {
            return;
        }

        String sql = "UPDATE S_TimeExpenseLine SET Processed='" + ( processed
                ?"Y"
                :"N" ) + "' WHERE S_TimeExpense_ID=" + getS_TimeExpense_ID();
        int noLine = DB.executeUpdate( sql,get_TrxName());

        m_lines = null;
        log.fine( "setProcessed - " + processed + " - Lines=" + noLine );
    }    // setProcessed

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
    }    // processIt


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
        setDocAction( DOCACTION_Prepare );

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

        // Std Period open? - AP (Reimbursement) Invoice

        if( !MPeriod.isOpen( getCtx(),getDateReport(),MDocType.DOCBASETYPE_APInvoice )) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        MTimeExpenseLine[] lines = getLines( false );

        if( lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Add up Amounts

        BigDecimal amt = Env.ZERO;

        for( int i = 0;i < lines.length;i++ ) {
            MTimeExpenseLine line = lines[ i ];

            amt = amt.add( line.getApprovalAmt());
        }

        setApprovalAmt( amt );

        // Invoiced but no BP

        for( int i = 0;i < lines.length;i++ ) {
            MTimeExpenseLine line = lines[ i ];

            if( line.isInvoiced() && (line.getC_BPartner_ID() == 0) ) {
                m_processMsg = "@Line@ " + line.getLine() + ": Invoiced, but no Business Partner";

                return DocAction.STATUS_Invalid;
            }
        }

        m_justPrepared = true;

        if( !DOCACTION_Complete.equals( getDocAction())) {
            setDocAction( DOCACTION_Complete );
        }

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

        log.info( "completeIt - " + toString());

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
        // setDocAction(DOCACTION_None);

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

        sb.append( ": " ).append( Msg.translate( getCtx(),"ApprovalAmt" )).append( "=" ).append( getApprovalAmt()).append( " (#" ).append( getLines( false ).length ).append( ")" );

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
        if( m_AD_User_ID != 0 ) {
            return m_AD_User_ID;
        }

        if( getC_BPartner_ID() != 0 ) {
            MUser[] users = MUser.getOfBPartner( getCtx(),getC_BPartner_ID());

            if( users.length > 0 ) {
                m_AD_User_ID = users[ 0 ].getAD_User_ID();

                return m_AD_User_ID;
            }
        }

        return getCreatedBy();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        MPriceList pl = MPriceList.get( getCtx(),getM_PriceList_ID(),get_TrxName());

        return pl.getC_Currency_ID();
    }    // getC_Currency_ID
}    // MTimeExpense



/*
 *  @(#)MTimeExpense.java   02.07.07
 * 
 *  Fin del fichero MTimeExpense.java
 *  
 *  Versión 2.2
 *
 */
