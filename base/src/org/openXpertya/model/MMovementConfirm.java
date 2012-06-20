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

public class MMovementConfirm extends X_M_MovementConfirm implements DocAction {

    /**
     * Descripción de Método
     *
     *
     * @param move
     * @param checkExisting
     *
     * @return
     */

    public static MMovementConfirm create( MMovement move,boolean checkExisting ) {
        if( checkExisting ) {
            MMovementConfirm[] confirmations = move.getConfirmations( false );

            for( int i = 0;i < confirmations.length;i++ ) {
                MMovementConfirm confirm = confirmations[ i ];

                return confirm;
            }
        }

        MMovementConfirm confirm = new MMovementConfirm( move );

        confirm.save( move.get_TrxName());

        MMovementLine[] moveLines = move.getLines( false );

        for( int i = 0;i < moveLines.length;i++ ) {
            MMovementLine        mLine = moveLines[ i ];
            MMovementLineConfirm cLine = new MMovementLineConfirm( confirm );

            cLine.setMovementLine( mLine );
            cLine.save( move.get_TrxName());
        }

        return confirm;
    }    // MInOutConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_MovementConfirm_ID
     * @param trxName
     */

    public MMovementConfirm( Properties ctx,int M_MovementConfirm_ID,String trxName ) {
        super( ctx,M_MovementConfirm_ID,trxName );

        if( M_MovementConfirm_ID == 0 ) {

            // setM_Movement_ID (0);

            setDocAction( DOCACTION_Complete );
            setDocStatus( DOCSTATUS_Drafted );
            setIsApproved( false );    // N
            setProcessed( false );
        }
    }                                  // MMovementConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMovementConfirm( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MMovementConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param move
     */

    public MMovementConfirm( MMovement move ) {
        this( move.getCtx(),0,move.get_TrxName());
        setClientOrg( move );
        setM_Movement_ID( move.getM_Movement_ID());
    }    // MInOutConfirm

    /** Descripción de Campos */

    private MMovementLineConfirm[] m_lines = null;

    /** Descripción de Campos */

    private MInventory m_inventoryFrom = null;

    /** Descripción de Campos */

    private MInventory m_inventoryTo = null;

    /** Descripción de Campos */

    private String m_inventoryInfo = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MMovementLineConfirm[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        String sql = "SELECT * FROM M_MovementLineConfirm " + "WHERE M_MovementConfirm_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_MovementConfirm_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MMovementLineConfirm( getCtx(),rs,get_TrxName()));
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

        m_lines = new MMovementLineConfirm[ list.size()];
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

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getUpdated(),MDocType.DOCBASETYPE_MaterialMovement )) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        MMovementLineConfirm[] lines = getLines( true );

        if( lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        boolean difference = false;

        for( int i = 0;i < lines.length;i++ ) {
            if( !lines[ i ].isFullyConfirmed()) {
                difference = true;

                break;
            }
        }

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

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

        //

        MMovement move = new MMovement( getCtx(),getM_Movement_ID(),get_TrxName());
        MMovementLineConfirm[] lines = getLines( false );

        for( int i = 0;i < lines.length;i++ ) {
            MMovementLineConfirm confirm = lines[ i ];

            confirm.set_TrxName( get_TrxName());

            if( !confirm.processLine()) {
                m_processMsg = "ShipLine not saved - " + confirm;

                return DocAction.STATUS_Invalid;
            }

            if( confirm.isFullyConfirmed()) {
                confirm.setProcessed( true );
                confirm.save( get_TrxName());
            } else {
                if( createDifferenceDoc( move,confirm )) {
                    confirm.setProcessed( true );
                    confirm.save( get_TrxName());
                } else {
                    log.log( Level.SEVERE,"completeIt - Scrapped=" + confirm.getScrappedQty() + " - Difference=" + confirm.getDifferenceQty());
                    m_processMsg = "Differnce Doc not created";

                    return DocAction.STATUS_Invalid;
                }
            }
        }    // for all lines

        if( m_inventoryInfo != null ) {
            m_processMsg = " @M_Inventory_ID@: " + m_inventoryInfo;
            addDescription( Msg.translate( getCtx(),"M_Inventory_ID" ) + ": " + m_inventoryInfo );
        }

        setProcessed( true );
        setDocAction( DOCACTION_Close );

        return DocAction.STATUS_Completed;
    }    // completeIt

    /**
     * Descripción de Método
     *
     *
     * @param move
     * @param confirm
     *
     * @return
     */

    private boolean createDifferenceDoc( MMovement move,MMovementLineConfirm confirm ) {
        MMovementLine mLine = confirm.getLine();

        // Difference - Create Inventory Difference for Source Location

        if( Env.ZERO.compareTo( confirm.getDifferenceQty()) != 0 ) {

            // Get Warehouse for Source

            MLocator loc = MLocator.get( getCtx(),mLine.getM_Locator_ID());

            if( (m_inventoryFrom != null) && (m_inventoryFrom.getM_Warehouse_ID() != loc.getM_Warehouse_ID())) {
                m_inventoryFrom = null;
            }

            if( m_inventoryFrom == null ) {
                MWarehouse wh = MWarehouse.get( getCtx(),loc.getM_Warehouse_ID());

                m_inventoryFrom = new MInventory( wh );
                m_inventoryFrom.setDescription( Msg.translate( getCtx(),"M_MovementConfirm_ID" ) + " " + getDocumentNo());

                if( !m_inventoryFrom.save( get_TrxName())) {
                    m_processMsg += "Inventory not created";

                    return false;
                }

                // First Inventory

                if( getM_Inventory_ID() == 0 ) {
                    setM_Inventory_ID( m_inventoryFrom.getM_Inventory_ID());
                    m_inventoryInfo = m_inventoryFrom.getDocumentNo();
                } else {
                    m_inventoryInfo += "," + m_inventoryFrom.getDocumentNo();
                }
            }

            log.info( "createDifferenceDoc - Difference=" + confirm.getDifferenceQty());

            MInventoryLine line = new MInventoryLine( m_inventoryFrom,mLine.getM_Locator_ID(),mLine.getM_Product_ID(),mLine.getM_AttributeSetInstance_ID(),confirm.getDifferenceQty(),Env.ZERO );

            line.setDescription( Msg.translate( getCtx(),"DifferenceQty" ));

            if( !line.save( get_TrxName())) {
                m_processMsg += "Inventory Line not created";

                return false;
            }

            confirm.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
        }    // Difference

        // Scrapped - Create Inventory Difference for Target Location

        if( Env.ZERO.compareTo( confirm.getScrappedQty()) != 0 ) {

            // Get Warehouse for Target

            MLocator loc = MLocator.get( getCtx(),mLine.getM_LocatorTo_ID());

            if( (m_inventoryTo != null) && (m_inventoryTo.getM_Warehouse_ID() != loc.getM_Warehouse_ID())) {
                m_inventoryTo = null;
            }

            if( m_inventoryTo == null ) {
                MWarehouse wh = MWarehouse.get( getCtx(),loc.getM_Warehouse_ID());

                m_inventoryTo = new MInventory( wh );
                m_inventoryTo.setDescription( Msg.translate( getCtx(),"M_MovementConfirm_ID" ) + " " + getDocumentNo());

                if( !m_inventoryTo.save( get_TrxName())) {
                    m_processMsg += "Inventory not created";

                    return false;
                }

                // First Inventory

                if( getM_Inventory_ID() == 0 ) {
                    setM_Inventory_ID( m_inventoryTo.getM_Inventory_ID());
                    m_inventoryInfo = m_inventoryTo.getDocumentNo();
                } else {
                    m_inventoryInfo += "," + m_inventoryTo.getDocumentNo();
                }
            }

            log.info( "createDifferenceDoc - Scrapped=" + confirm.getScrappedQty());

            MInventoryLine line = new MInventoryLine( m_inventoryTo,mLine.getM_LocatorTo_ID(),mLine.getM_Product_ID(),mLine.getM_AttributeSetInstance_ID(),confirm.getScrappedQty(),Env.ZERO );

            line.setDescription( Msg.translate( getCtx(),"ScrappedQty" ));

            if( !line.save( get_TrxName())) {
                m_processMsg += "Inventory Line not created";

                return false;
            }

            confirm.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
        }    // Scrapped

        return true;
    }    // createDifferenceDoc

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

        // Close Not delivered Qty

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
}    // MMovementConfirm



/*
 *  @(#)MMovementConfirm.java   02.07.07
 * 
 *  Fin del fichero MMovementConfirm.java
 *  
 *  Versión 2.2
 *
 */
