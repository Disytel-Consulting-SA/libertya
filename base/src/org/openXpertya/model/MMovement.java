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

public class MMovement extends X_M_Movement implements DocAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Movement_ID
     * @param trxName
     */

    public MMovement( Properties ctx,int M_Movement_ID,String trxName ) {
        super( ctx,M_Movement_ID,trxName );

        if( M_Movement_ID == 0 ) {

            // setC_DocType_ID (0);

            setDocAction( DOCACTION_Complete );                              // CO
            setDocStatus( DOCSTATUS_Drafted );                               // DR
            setIsApproved( false );
            setIsInTransit( false );
            setMovementDate( new Timestamp( System.currentTimeMillis()));    // @#Date@
            setPosted( false );
            super.setProcessed( false );
        }
    }    // MMovement

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMovement( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MMovement

    /** Descripción de Campos */

    private MMovementLine[] m_lines = null;

    /** Descripción de Campos */

    private MMovementConfirm[] m_confirms = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MMovementLine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        //

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_MovementLine WHERE M_Movement_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_Movement_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MMovementLine( getCtx(),rs,get_TrxName()));
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

        m_lines = new MMovementLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MMovementConfirm[] getConfirmations( boolean requery ) {
        if( (m_confirms != null) &&!requery ) {
            return m_confirms;
        }

        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM M_MovementConfirm WHERE M_Movement_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_Movement_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MMovementConfirm( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getConfirmations",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_confirms = new MMovementConfirm[ list.size()];
        list.toArray( m_confirms );

        return m_confirms;
    }    // getConfirmations

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
     * @param AD_Org_ID
     */

    public void setAD_Org_ID( int AD_Org_ID ) {
        super.setAD_Org_ID( AD_Org_ID );
    }    // setAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getC_DocType_ID() == 0 ) {
            MDocType types[] = MDocType.getOfDocBaseType( getCtx(),MDocType.DOCBASETYPE_MaterialMovement );

            if( types.length > 0 ) {    // get first
                setC_DocType_ID( types[ 0 ].getC_DocType_ID());
            } else {
                log.saveError( "Error",Msg.parseTranslation( getCtx(),"@NotFound@ @C_DocType_ID@" ));

                return false;
            }
        }

        return true;
    }    // beforeSave

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

        String sql = "UPDATE M_MovementLine SET Processed='" + ( processed
                ?"Y"
                :"N" ) + "' WHERE M_Movement_ID=" + getM_Movement_ID();
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

        MDocType dt = MDocType.get( getCtx(),getC_DocType_ID());

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getMovementDate(),dt.getDocBaseType())) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        MMovementLine[] lines = getLines( false );

        if( lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Add up Amounts

        checkMaterialPolicy();

        // Confirmation

        if( dt.isInTransit()) {
            createConfirmation();
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
     */

    private void createConfirmation() {
        MMovementConfirm[] confirmations = getConfirmations( false );

        if( confirmations.length > 0 ) {
            return;
        }

        // Create Confirmation

        MMovementConfirm.create( this,false );
    }    // createConfirmation

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

        // Outstanding (not processed) Incoming Confirmations ?

        MMovementConfirm[] confirmations = getConfirmations( true );

        for( int i = 0;i < confirmations.length;i++ ) {
            MMovementConfirm confirm = confirmations[ i ];

            if( !confirm.isProcessed()) {
                m_processMsg = "Open: @M_MovementConfirm_ID@ - " + confirm.getDocumentNo();

                return DocAction.STATUS_InProgress;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }

        log.info( toString());

        //

        MMovementLine[] lines = getLines( false );

        for( int i = 0;i < lines.length;i++ ) {
            MMovementLine line    = lines[ i ];
            MTransaction  trxFrom = null;

            if( line.getM_AttributeSetInstance_ID() == 0 ) {
                MMovementLineMA mas[] = MMovementLineMA.get( getCtx(),line.getM_MovementLine_ID(),get_TrxName());

                for( int j = 0;j < mas.length;j++ ) {
                    MMovementLineMA ma = mas[ j ];

                    //

                    MStorage storageFrom = MStorage.get( getCtx(),line.getM_Locator_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),get_TrxName());

                    if( storageFrom == null ) {
                        storageFrom = MStorage.getCreate( getCtx(),line.getM_Locator_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),get_TrxName());
                    }

                    //

                    storageFrom.setQtyOnHand( storageFrom.getQtyOnHand().subtract( ma.getMovementQty()));

                    if( !storageFrom.save( get_TrxName())) {
                        m_processMsg = "Storage From not updated (MA)";

                        return DocAction.STATUS_Invalid;
                    }

                    //

                    MStorage storageTo = MStorage.get( getCtx(),line.getM_LocatorTo_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),get_TrxName());

                    if( storageTo == null ) {
                        storageTo = MStorage.getCreate( getCtx(),line.getM_LocatorTo_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),get_TrxName());
                    }

                    //

                    storageTo.setQtyOnHand( storageTo.getQtyOnHand().add( ma.getMovementQty()));

                    if( !storageTo.save( get_TrxName())) {
                        m_processMsg = "Storage To not updated (MA)";

                        return DocAction.STATUS_Invalid;
                    }

                    //

                    trxFrom = new MTransaction( getCtx(),MTransaction.MOVEMENTTYPE_MovementFrom,line.getM_Locator_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),ma.getMovementQty().negate(),getMovementDate(),get_TrxName());
                    trxFrom.setM_MovementLine_ID( line.getM_MovementLine_ID());
                    trxFrom.setClientOrg(this);
                    trxFrom.setDescription("MMovement.complete() - 1st Transaction Save - Transaction of MTransaction "
							+ trxFrom.get_TrxName());
                    if( !trxFrom.save()) {
                        m_processMsg = "Transaction From not inserted (MA)";

                        return DocAction.STATUS_Invalid;
                    }

                    //

                    MTransaction trxTo = new MTransaction( getCtx(),MTransaction.MOVEMENTTYPE_MovementTo,line.getM_LocatorTo_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),ma.getMovementQty(),getMovementDate(),get_TrxName());

                    trxTo.setM_MovementLine_ID( line.getM_MovementLine_ID());
                    trxTo.setClientOrg(this);
                    trxTo.setDescription("MMovement.complete() - 2nd Transaction Save - Transaction of MTransaction "
							+ trxTo.get_TrxName());
                    if( !trxTo.save()) {
                        m_processMsg = "Transaction To not inserted (MA)";

                        return DocAction.STATUS_Invalid;
                    }
                }
            }

            // Fallback - We have ASI

            if( trxFrom == null ) {
                MStorage storageFrom = MStorage.get( getCtx(),line.getM_Locator_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstance_ID(),get_TrxName());

                if( storageFrom == null ) {
                    storageFrom = MStorage.getCreate( getCtx(),line.getM_Locator_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstance_ID(),get_TrxName());
                }

                //

                MStorage storageTo = MStorage.get( getCtx(),line.getM_LocatorTo_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstanceTo_ID(),get_TrxName());

                if( storageTo == null ) {
                    storageTo = MStorage.getCreate( getCtx(),line.getM_LocatorTo_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstanceTo_ID(),get_TrxName());
                }

                //

                storageFrom.setQtyOnHand( storageFrom.getQtyOnHand().subtract( line.getMovementQty()));

                if( !storageFrom.save( get_TrxName())) {
                    m_processMsg = "Storage From not updated";

                    return DocAction.STATUS_Invalid;
                }

                //

                storageTo.setQtyOnHand( storageTo.getQtyOnHand().add( line.getMovementQty()));

                if( !storageTo.save( get_TrxName())) {
                    m_processMsg = "Storage To not updated";

                    return DocAction.STATUS_Invalid;
                }

                //

                trxFrom = new MTransaction( getCtx(),MTransaction.MOVEMENTTYPE_MovementFrom,line.getM_Locator_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstance_ID(),line.getMovementQty().negate(),getMovementDate(),get_TrxName());
                trxFrom.setM_MovementLine_ID( line.getM_MovementLine_ID());
                trxFrom.setClientOrg(this);
                trxFrom.setDescription("MMovement.complete() - 3rd Transaction Save - Transaction of MTransaction "
						+ trxFrom.get_TrxName());
                if( !trxFrom.save()) {
                    m_processMsg = "Transaction From not inserted";

                    return DocAction.STATUS_Invalid;
                }

                //

                MTransaction trxTo = new MTransaction( getCtx(),MTransaction.MOVEMENTTYPE_MovementTo,line.getM_LocatorTo_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstanceTo_ID(),line.getMovementQty(),getMovementDate(),get_TrxName());

                trxTo.setM_MovementLine_ID( line.getM_MovementLine_ID());
                trxTo.setClientOrg(this);
                trxTo.setDescription("MMovement.complete() - 4th Transaction Save - Transaction of MTransaction "
						+ trxTo.get_TrxName());
                
                if( !trxTo.save()) {
                    m_processMsg = "Transaction To not inserted";

                    return DocAction.STATUS_Invalid;
                }
            }    // Fallback
        }        // for all lines

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
     */

    private void checkMaterialPolicy() {
        int no = MMovementLineMA.deleteMovementMA( getM_Movement_ID(),get_TrxName());

        if( no > 0 ) {
            log.config( "Delete old #" + no );
        }

        MMovementLine[] lines  = getLines( false );
        MClient         client = MClient.get( getCtx());

        // Check Lines

        for( int i = 0;i < lines.length;i++ ) {
            MMovementLine line     = lines[ i ];
            boolean       needSave = false;

            // Attribute Set Instance

            if( line.getM_AttributeSetInstance_ID() == 0 ) {
                MProduct product = MProduct.get( getCtx(),line.getM_Product_ID());
                MProductCategory pc = MProductCategory.get( getCtx(),product.getM_Product_Category_ID(), get_TrxName());
                String MMPolicy = pc.getMMPolicy();

                if( (MMPolicy == null) || (MMPolicy.length() == 0) ) {
                    MMPolicy = client.getMMPolicy();
                }

                //

                MStorage[] storages = MStorage.getAll( getCtx(),line.getM_Product_ID(),line.getM_Locator_ID(),MClient.MMPOLICY_FiFo.equals( MMPolicy ),get_TrxName());
                BigDecimal qtyToDeliver = line.getMovementQty();

                for( int ii = 0;ii < storages.length;ii++ ) {
                    MStorage storage = storages[ ii ];

                    if( ii == 0 ) {
                        if( storage.getQtyOnHand().compareTo( qtyToDeliver ) >= 0 ) {
                            line.setM_AttributeSetInstance_ID( storage.getM_AttributeSetInstance_ID());
                            needSave = true;
                            log.config( "Direct - " + line );
                            qtyToDeliver = Env.ZERO;
                        } else {
                            log.config( "Split - " + line );

                            MMovementLineMA ma = new MMovementLineMA( line,storage.getM_AttributeSetInstance_ID(),storage.getQtyOnHand());

                            if( !ma.save()) {
                                ;
                            }

                            qtyToDeliver = qtyToDeliver.subtract( storage.getQtyOnHand());
                            log.fine( "#" + ii + ": " + ma + ", QtyToDeliver=" + qtyToDeliver );
                        }
                    } else    // create addl material allocation
                    {
                        MMovementLineMA ma = new MMovementLineMA( line,storage.getM_AttributeSetInstance_ID(),qtyToDeliver );

                        if( storage.getQtyOnHand().compareTo( qtyToDeliver ) >= 0 ) {
                            qtyToDeliver = Env.ZERO;
                        } else {
                            ma.setMovementQty( storage.getQtyOnHand());
                            qtyToDeliver = qtyToDeliver.subtract( storage.getQtyOnHand());
                        }

                        if( !ma.save()) {
                            ;
                        }

                        log.fine( "#" + ii + ": " + ma + ", QtyToDeliver=" + qtyToDeliver );
                    }

                    if( qtyToDeliver.signum() == 0 ) {
                        break;
                    }
                }    // for all storages

                // No AttributeSetInstance found for remainder

                if( qtyToDeliver.signum() != 0 ) {
                    MMovementLineMA ma = new MMovementLineMA( line,0,qtyToDeliver );

                    if( !ma.save()) {
                        ;
                    }

                    log.fine( "##: " + ma );
                }
            }    // attributeSetInstance

            if( needSave &&!line.save()) {
                log.severe( "NOT saved " + line );
            }
        }        // for all lines
    }            // checkMaterialPolicy

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
        
        // Si el inventario fue generado por un fraccionamiento no es posible
        // 
        if (!canProcessAction(DOCACTION_Close)) {
        	m_processMsg = "@CannotCloseProdChangeMovementDirectly@";
        	return false;
        }
        
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
        return getCreatedBy();
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
    
    /** Cambio de Artículo que invoca el procesamiento de este movimiento de inventario */
    private MProductChange callerProductChange = null;

	/**
	 * @return the callerProductChange
	 */
	protected MProductChange getCallerProductChange() {
		return callerProductChange;
	}

	/**
	 * @param callerProductChange the callerProductChange to set
	 */
	protected void setCallerProductChange(MProductChange callerProductChange) {
		this.callerProductChange = callerProductChange;
	}
    
	/**
	 * @return Devuelve el Cambio de Artículo al cual pertenece este movimiento. En
	 * caso de NO ser un movemiento generado por un cambio de artículo devuelve null.
	 */
	public MProductChange getProductChange() {
    	MProductChange productChange = null;
		String sql = 
    		"SELECT M_ProductChange_ID " +
    		"FROM M_ProductChange " +
    		"WHERE M_Movement_ID = ? OR M_Void_Movement_ID = ?";
    	Integer productChangeID = (Integer)DB.getSQLObject(get_TrxName(), sql, 
    		new Object[] {
    			getM_Movement_ID(), getM_Movement_ID()
    		}
    	);
    	if (productChangeID != null && productChangeID > 0) {
    		productChange = new MProductChange(getCtx(), productChangeID, get_TrxName());
    	}
    	return productChange;
	}
	
    /**
     * @return Indica si es posible procesar este movimiento inventario según ciertos
     * criterios de validación.
     */
    private boolean canProcessAction(String docAction) {
    	MProductChange productChange = getProductChange();
    	boolean canProcess = true; // Por defecto es posible
    	// Accion: Cerrar
    	if (DOCACTION_Close.equals(docAction)) {
    		canProcess = 
    			// Si no pertenece a un cambio de artículo se puede cerrar
    			productChange == null ||  
    			// Si pertenece a un cambio de artículo, solo se puede cerrar si
    			// es el propio cambio de artículo el que está intentando cerrar este
    			// movimiento de inventario.
    			(getCallerProductChange() != null && 
    				productChange.getM_ProductChange_ID() == getCallerProductChange().getM_ProductChange_ID());
    	}
    	return canProcess;
    }

}    // MMovement



/*
 *  @(#)MMovement.java   02.07.07
 * 
 *  Fin del fichero MMovement.java
 *  
 *  Versión 2.2
 *
 */
