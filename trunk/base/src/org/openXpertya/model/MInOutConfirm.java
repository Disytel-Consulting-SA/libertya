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
import org.openXpertya.util.CLogger;
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

public class MInOutConfirm extends X_M_InOutConfirm implements DocAction {

    /**
     * Descripción de Método
     *
     *
     * @param ship
     * @param confirmType
     * @param checkExisting
     *
     * @return
     */

    public static MInOutConfirm create( MInOut ship,String confirmType,boolean checkExisting ) {
        if( checkExisting ) {
            MInOutConfirm[] confirmations = ship.getConfirmations( false );

            for( int i = 0;i < confirmations.length;i++ ) {
                MInOutConfirm confirm = confirmations[ i ];

                if( confirm.getConfirmType().equals( confirmType )) {
                    s_log.info( "create - existing: " + confirm );

                    return confirm;
                }
            }
        }

        MInOutConfirm confirm = new MInOutConfirm( ship,confirmType );

        confirm.save( ship.get_TrxName());

        MInOutLine[] shipLines = ship.getLines( false );

        for( int i = 0;i < shipLines.length;i++ ) {
            MInOutLine        sLine = shipLines[ i ];
            MInOutLineConfirm cLine = new MInOutLineConfirm( confirm );

            cLine.setInOutLine( sLine );
            cLine.save( ship.get_TrxName());
        }

        s_log.info( "New: " + confirm );

        return confirm;
    }    // MInOutConfirm

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MInOutConfirm.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_InOutConfirm_ID
     * @param trxName
     */

    public MInOutConfirm( Properties ctx,int M_InOutConfirm_ID,String trxName ) {
        super( ctx,M_InOutConfirm_ID,trxName );

        if( M_InOutConfirm_ID == 0 ) {

            // setConfirmType (null);

            setDocAction( DOCACTION_Complete );    // CO
            //setDocStatus( DOCSTATUS_Drafted );     // DR
            setDocStatus( DOCSTATUS_InProgress );	//IP
            setIsApproved( false );
            setIsCancelled( false );
            setIsInDispute( false );
            super.setProcessed( false );
        }
    }                                              // MInOutConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInOutConfirm( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInOutConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param ship
     * @param confirmType
     */

    public MInOutConfirm( MInOut ship,String confirmType ) {
        this( ship.getCtx(),0,ship.get_TrxName());
        setClientOrg( ship );
        setM_InOut_ID( ship.getM_InOut_ID());
        setConfirmType( confirmType );
    }    // MInOutConfirm

    /** Descripción de Campos */

    private MInOutLineConfirm[] m_lines = null;

    /** Descripción de Campos */

    private MInvoice m_creditMemo = null;

    /** Descripción de Campos */

    private MInventory m_inventory = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MInOutLineConfirm[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        String sql = "SELECT * FROM M_InOutLineConfirm " + "WHERE M_InOutConfirm_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_InOutConfirm_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInOutLineConfirm( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_lines = new MInOutLineConfirm[ list.size()];
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

    public String getConfirmTypeName() {
        return MRefList.getListName( getCtx(),CONFIRMTYPE_AD_Reference_ID,getConfirmType());
    }    // getConfirmTypeName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MInOutConfirm[" );

        sb.append( getID()).append( "-" ).append( getSummary()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param IsApproved
     */

    public void setIsApproved( boolean IsApproved ) {
        if( IsApproved &&!isApproved()) {
            int    AD_User_ID = Env.getAD_User_ID( getCtx());
            MUser  user       = MUser.get( getCtx(),AD_User_ID );
            String info       = user.getName() + ": " + Msg.translate( getCtx(),"IsApproved" ) + " - " + new Timestamp( System.currentTimeMillis());

            addDescription( info );
        }

        super.setIsApproved( IsApproved );
    }    // setIsApproved

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
        log.info( toString());
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
        log.info( toString());
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

        MInOutLineConfirm[] lines = getLines( true );

        if( lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Set dispute if not fully confirmed

        boolean difference = false;

        for( int i = 0;i < lines.length;i++ ) {
            if( !lines[ i ].isFullyConfirmed()) {
                difference = true;

                break;
            }
        }

        setIsInDispute( difference );

        //

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
        log.info( toString());
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
        log.info( toString());
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

        log.info( toString());

        //

        MInOut              inout = new MInOut( getCtx(),getM_InOut_ID(),get_TrxName());
        MInOutLineConfirm[] lines = getLines( false );

        // Check if we need to split Shipment

        if( isInDispute()) {
            MDocType dt = MDocType.get( getCtx(),inout.getC_DocType_ID());

            if( dt.isSplitWhenDifference()) {
                if( dt.getC_DocTypeDifference_ID() == 0 ) {
                    m_processMsg = "No Split Document Type defined for: " + dt.getName();

                    return DocAction.STATUS_Invalid;
                }

                splitInOut( inout,dt.getC_DocTypeDifference_ID(),lines );
                m_lines = null;
            }
        }

        // All lines

        for( int i = 0;i < lines.length;i++ ) {
            MInOutLineConfirm confirmLine = lines[ i ];

            confirmLine.set_TrxName( get_TrxName());

            if( !confirmLine.processLine( inout.isSOTrx(),getConfirmType())) {
                m_processMsg = "ShipLine not saved - " + confirmLine;

                return DocAction.STATUS_Invalid;
            }

            if( confirmLine.isFullyConfirmed()) {
                confirmLine.setProcessed( true );
                confirmLine.save( get_TrxName());
            } else {
                if( createDifferenceDoc( inout,confirmLine )) {
                    confirmLine.setProcessed( true );
                    confirmLine.save( get_TrxName());
                } else {
                    log.log( Level.SEVERE,"Scrapped=" + confirmLine.getScrappedQty() + " - Difference=" + confirmLine.getDifferenceQty());

                    return DocAction.STATUS_Invalid;
                }
            }
        }    // for all lines

        if( m_creditMemo != null ) {
            m_processMsg += " @C_Invoice_ID@=" + m_creditMemo.getDocumentNo();
        }

        if( m_inventory != null ) {
            m_processMsg += " @M_Inventory_ID@=" + m_inventory.getDocumentNo();
        }

        // Try to complete Shipment
        // if (inout.processIt(DocAction.ACTION_Complete))
        // m_processMsg = "@M_InOut_ID@ " + inout.getDocumentNo() + ": @Completed@";

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        setProcessed( true );
        setDocAction( DOCACTION_Close );

        return DocAction.STATUS_Completed;
    }    // completeIt

    /**
     * Descripción de Método
     *
     *
     * @param original
     * @param C_DocType_ID
     * @param confirmLines
     */

    private void splitInOut( MInOut original,int C_DocType_ID,MInOutLineConfirm[] confirmLines ) {
        MInOut split = new MInOut( original,C_DocType_ID,original.getMovementDate());

        split.addDescription( "Splitted from " + original.getDocumentNo());
        split.setIsInDispute( true );

        if( !split.save( get_TrxName())) {
            throw new IllegalStateException( "Cannot save Split" );
        }

        original.addDescription( "Split: " + split.getDocumentNo());

        if( !original.save( get_TrxName())) {
            throw new IllegalStateException( "Cannot update original Shipment" );
        }

        // Go through confirmations

        for( int i = 0;i < confirmLines.length;i++ ) {
            MInOutLineConfirm confirmLine   = confirmLines[ i ];
            BigDecimal        differenceQty = confirmLine.getDifferenceQty();

            if( differenceQty.compareTo( Env.ZERO ) == 0 ) {
                continue;
            }

            //

            MInOutLine oldLine = confirmLine.getLine();

            log.fine( "Qty=" + differenceQty + ", Old=" + oldLine );

            //

            MInOutLine splitLine = new MInOutLine( split );

            splitLine.setC_OrderLine_ID( oldLine.getC_OrderLine_ID());
            splitLine.setC_UOM_ID( oldLine.getC_UOM_ID());
            splitLine.setDescription( oldLine.getDescription());
            splitLine.setIsDescription( oldLine.isDescription());
            splitLine.setLine( oldLine.getLine());
            splitLine.setM_AttributeSetInstance_ID( oldLine.getM_AttributeSetInstance_ID());
            splitLine.setM_Locator_ID( oldLine.getM_Locator_ID());
            splitLine.setM_Product_ID( oldLine.getM_Product_ID());
            splitLine.setM_Warehouse_ID( oldLine.getM_Warehouse_ID());
            splitLine.setRef_InOutLine_ID( oldLine.getRef_InOutLine_ID());
            splitLine.addDescription( "Split: from " + oldLine.getMovementQty());

            // Qtys

            splitLine.setQty( differenceQty );    // Entered/Movement

            if( !splitLine.save( get_TrxName())) {
                throw new IllegalStateException( "Cannot save Split Line" );
            }

            // Old

            oldLine.addDescription( "Splitted: from " + oldLine.getMovementQty());
            oldLine.setQty( oldLine.getMovementQty().subtract( differenceQty ));

            if( !oldLine.save( get_TrxName())) {
                throw new IllegalStateException( "Cannot save Splited Line" );
            }

            // Update Confirmation Line

            confirmLine.setTargetQty( confirmLine.getTargetQty().subtract( differenceQty ));
            confirmLine.setDifferenceQty( Env.ZERO );

            if( !confirmLine.save( get_TrxName())) {
                throw new IllegalStateException( "Cannot save Split Confirmation" );
            }
        }    // for all confirmations

        m_processMsg = "Split @M_InOut_ID@=" + split.getDocumentNo() + " - @M_InOutConfirm_ID@=";

        // Create Dispute Confirmation

        split.processIt( DocAction.ACTION_Prepare );

        // split.createConfirmation();

        split.save( get_TrxName());

        MInOutConfirm[] splitConfirms = split.getConfirmations( true );

        if( splitConfirms.length > 0 ) {
            int index = 0;

            if( splitConfirms[ index ].isProcessed()) {
                if( splitConfirms.length > 1 ) {
                    index++;    // try just next
                }

                if( splitConfirms[ index ].isProcessed()) {
                    m_processMsg += splitConfirms[ index ].getDocumentNo() + " processed??";

                    return;
                }
            }

            splitConfirms[ index ].setIsInDispute( true );
            splitConfirms[ index ].save( get_TrxName());
            m_processMsg += splitConfirms[ index ].getDocumentNo();

            // Set Lines to unconfirmed

            MInOutLineConfirm[] splitConfirmLines = splitConfirms[ index ].getLines( false );

            for( int i = 0;i < splitConfirmLines.length;i++ ) {
                MInOutLineConfirm splitConfirmLine = splitConfirmLines[ i ];

                splitConfirmLine.setScrappedQty( Env.ZERO );
                splitConfirmLine.setConfirmedQty( Env.ZERO );
                splitConfirmLine.save( get_TrxName());
            }
        } else {
            m_processMsg += "??";
        }
    }    // splitInOut

    /**
     * Descripción de Método
     *
     *
     * @param inout
     * @param confirm
     *
     * @return
     */

    private boolean createDifferenceDoc( MInOut inout,MInOutLineConfirm confirm ) {
        if( m_processMsg == null ) {
            m_processMsg = "";
        } else if( m_processMsg.length() > 0 ) {
            m_processMsg += "; ";
        }

        // Credit Memo if linked Document

        if( (confirm.getDifferenceQty().signum() != 0) &&!inout.isSOTrx() && (inout.getRef_InOut_ID() != 0) ) {
            log.info( "Difference=" + confirm.getDifferenceQty());

            if( m_creditMemo == null ) {
                m_creditMemo = new MInvoice( inout,null );
                m_creditMemo.setDescription( Msg.translate( getCtx(),"M_InOutConfirm_ID" ) + " " + getDocumentNo());
                m_creditMemo.setC_DocTypeTarget_ID( MDocType.DOCBASETYPE_APCreditMemo );

                if( !m_creditMemo.save( get_TrxName())) {
                    m_processMsg += "Credit Memo not created";

                    return false;
                }

                setC_Invoice_ID( m_creditMemo.getC_Invoice_ID());
            }

            MInvoiceLine line = new MInvoiceLine( m_creditMemo );

            line.setShipLine( confirm.getLine());
            line.setQty( confirm.getDifferenceQty());    // Entered/Invoiced

            if( !line.save( get_TrxName())) {
                m_processMsg += "Credit Memo Line not created";

                return false;
            }

            confirm.setC_InvoiceLine_ID( line.getC_InvoiceLine_ID());
        }

        // Create Inventory Difference

        if( confirm.getScrappedQty().signum() != 0 ) {
            log.info( "Scrapped=" + confirm.getScrappedQty());

            if( m_inventory == null ) {
                MWarehouse wh = MWarehouse.get( getCtx(),inout.getM_Warehouse_ID());

                m_inventory = new MInventory( wh );
                m_inventory.setDescription( Msg.translate( getCtx(),"M_InOutConfirm_ID" ) + " " + getDocumentNo());

                if( !m_inventory.save( get_TrxName())) {
                    m_processMsg += "Inventory not created";

                    return false;
                }

                setM_Inventory_ID( m_inventory.getM_Inventory_ID());
            }

            MInOutLine     ioLine = confirm.getLine();
            MInventoryLine line   = new MInventoryLine( m_inventory,ioLine.getM_Locator_ID(),ioLine.getM_Product_ID(),ioLine.getM_AttributeSetInstance_ID(),confirm.getScrappedQty(),Env.ZERO );

            if( !line.save( get_TrxName())) {
                m_processMsg += "Inventory Line not created";

                return false;
            }

            confirm.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
        }

        //

        if( !confirm.save( get_TrxName())) {
            m_processMsg += "Confirmation Line not saved";

            return false;
        }

        return true;
    }    // createDifferenceDoc

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt() {
        log.info( toString());

        return false;
    }    // postIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean voidIt() {
        log.info( toString());

        return false;
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( toString());
        setDocAction( DOCACTION_None );

        return true;
    }    // closeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt() {
        log.info( toString());

        return false;
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( toString());

        return false;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        log.info( toString());

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
        return getUpdatedBy();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {

        // MPriceList pl = MPriceList.get(getCtx(), getM_PriceList_ID());
        // return pl.getC_Currency_ID();

        return 0;
    }    // getC_Currency_ID
}    // MInOutConfirm



/*
 *  @(#)MInOutConfirm.java   02.07.07
 * 
 *  Fin del fichero MInOutConfirm.java
 *  
 *  Versión 2.2
 *
 */
