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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AssetDTO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInventory extends X_M_Inventory implements DocAction {

	/**
	 * Obtener el nro de documento del inventario en el caso que exista el
	 * valor pasado como parámetro a la columna parámetro. Además, es posible
	 * filtrar por el registro actual en caso que se requiera
	 * 
	 * @param ctx
	 *            contexto
	 * @param columnName
	 *            nombre de la columna a verificar
	 * @param value
	 *            valor de la columna
	 * @param trxName
	 *            nombre de la transacción actual
	 * @return el nro de documento de la transferencia si es que existe para ese
	 *         valor de columna, null caso contrario
	 */
	public static String getDocNoInventoryByStrColumnCondition(Properties ctx, String columnName, String value, Integer inventorySelfID, String trxName){
		if(Util.isEmpty(value, true)) return null;
		Object[] transferParams1 = new Object[] { Env.getAD_Org_ID(ctx), value };
		Object[] transferParams2 = new Object[] { Env.getAD_Org_ID(ctx), value,
				inventorySelfID };
		boolean selfEmpty = Util.isEmpty(inventorySelfID, true);
		return (String) DB
				.getSQLObject(
						null,
						"SELECT documentno as docno FROM "
								+ Table_Name
								+ " WHERE docstatus IN ('CO','CL') AND ad_org_id = ? AND upper(trim("
								+ columnName + ")) = upper(trim(?))"
								+ (selfEmpty ? "" : " AND m_inventory_id <> ?"),
						selfEmpty ? transferParams1 : transferParams2);
	}
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Inventory_ID
     * @param trxName
     */

    public MInventory( Properties ctx,int M_Inventory_ID,String trxName ) {
        super( ctx,M_Inventory_ID,trxName );

        if( M_Inventory_ID == 0 ) {

            // setName (null);
            // setM_Warehouse_ID (0);              //      FK

            setMovementDate( Env.getDate());
            setDocAction( DOCACTION_Complete );                              // CO
            setDocStatus( DOCSTATUS_Drafted );                               // DR
            setIsApproved( false );
            setMovementDate( Env.getDate());    // @#Date@
            setPosted( false );
            setProcessed( false );
            setInventoryKind(INVENTORYKIND_PhysicalInventory);
        }
    }    // MInventory

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInventory( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInventory

    /**
     * Constructor de la clase ...
     *
     *
     * @param wh
     */

    public MInventory( MWarehouse wh ) {
        this( wh.getCtx(),0,wh.get_TrxName());
        setClientOrg( wh );
        setM_Warehouse_ID( wh.getM_Warehouse_ID());
    }    // MInventory

    /** Descripción de Campos */

    private MInventoryLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MInventoryLine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        //

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_InventoryLine WHERE M_Inventory_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_Inventory_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInventoryLine( getCtx(),rs,get_TrxName()));
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

        m_lines = new MInventoryLine[ list.size()];
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
     * @param AD_Client_ID
     * @param AD_Org_ID
     */

    public void setClientOrg( int AD_Client_ID,int AD_Org_ID ) {
        super.setClientOrg( AD_Client_ID,AD_Org_ID );
    }    // setClientOrg

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

    	// Valor por defecto para el tipo de documento.
    	if( getC_DocType_ID() == 0 ) {
    		// Se busca el tipo de doc según la clase de inventario, intentando obtener
    		// el TipoDoc según la clave única.
    		MDocType docType = null;
    		// - Ingreso/Egreso Simple
    		if (INVENTORYKIND_SimpleInOut.equals(getInventoryKind())) {
    			docType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_SimpleMaterialInOut, null);
    		// - Inventario Físico.
    		} else if (INVENTORYKIND_PhysicalInventory.equals(getInventoryKind())) {
    			docType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_MaterialPhysicalInventory, null);
    		}
    		if (docType != null) {
    			setC_DocType_ID(docType.getC_DocType_ID());
    		// Si no se encontró el tipo según su clave, se busca según el DocBase tal
    		// como se hacía originalmente en este método.	
    		} else {

	    		MDocType types[] = MDocType.getOfDocBaseType( getCtx(),MDocType.DOCBASETYPE_MaterialPhysicalInventory );
	
	            if( types.length > 0 ) {    // get first
	                setC_DocType_ID( types[ 0 ].getC_DocType_ID());
	            } else {
	                log.saveError( "Error",Msg.parseTranslation( getCtx(),"@NotFound@ @C_DocType_ID@" ));
	
	                return false;
	            }
    		}
        }
    	// Si es un inventario por Entrada/Salida Simple, se debe indicar el Cargo que representa
    	// el motivo del movimiento. Este cargo se setea luego a todas las líneas del inventario,
    	// ya sea las que están creadas o aquellas que se crearán posteriormente.
    	if (INVENTORYKIND_SimpleInOut.equals(getInventoryKind())) {
    		if(getC_Charge_ID() == 0){
	            log.saveError("SaveError",Msg.translate(getCtx(), "SimpleInOutReasonRequired"));
	            return false;
    		}
    		// Esto se comenta ya que en un principio se pedía la validación, pero
    		// luego se decidió que no se implementa
    		// ------------------------------------------------------------------------
//			// Verificar que no exista otra entrada/salida simple con la misma
//			// cadena formulario papel
//			String docNo = getDocNoInventoryByStrColumnCondition(getCtx(),
//					"Paper_Form", getPaper_Form(), getID(), get_TrxName());
//    		if(docNo != null){
//    			log.saveError("", Msg.getMsg(getCtx(), "PaperFormWarning",
//    					new Object[] { docNo }));
//    			return false;
//    		}
    		// ------------------------------------------------------------------------
    	}

        return true;
    }    // beforeSave

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		// Si cambió el Cargo se deben actualizar las líneas asignando
		// el nuevo cargo a cada una de ellas.
		if (success && is_ValueChanged("C_Charge_ID")) {
			for (MInventoryLine line : getLines(true)) {
				line.setC_Charge_ID(getC_Charge_ID());
				success = success && line.save();
			}
		}
		return success;
	}

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

        String sql = "UPDATE M_InventoryLine SET Processed='" + ( processed
                ?"Y"
                :"N" ) + "' WHERE M_Inventory_ID=" + getM_Inventory_ID();
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

        MDocType docType = MDocType.get(getCtx(), getC_DocType_ID());
        
        // Cuando está activado el control de cierres de almacenes se actualiza la 
        // fecha en caso de que la misma sea menor a la fecha actual. Esto es necesario 
        // para que se pueda completar el documento pasando la validación de cierre. 
        // (además es lógico que la fecha real del inventario sea igual a la fecha 
        // en que se completó el mismo, y no a la fecha en que se creó).
        if (docType.isWarehouseClosureControl()
        		&& MWarehouseClose.isWarehouseCloseControlActivated() 
        		&& getMovementDate().compareTo(Env.getDate()) < 0
        		&& !MWarehouseClose.existsWarehouseCloseInProgress(getCtx(),
						getM_Warehouse_ID(), get_TrxName())) {
        	setMovementDate(Env.getDate());
        }
        
        // Std Period open?

		if (!MPeriod.isOpen(getCtx(), getMovementDate(),
				MDocType.DOCBASETYPE_MaterialPhysicalInventory,
				getM_Warehouse_ID(), !docType.isWarehouseClosureControl())) {
            if (MWarehouseClose.isWarehouseCloseControlActivated()) {
            	m_processMsg = "@PeriodClosedOrWarehouseClosed@";
            } else {
            	m_processMsg = "@PeriodClosed@";
            }
            return DocAction.STATUS_Invalid;
        }

        // -----------------------------------------------------------------------
		// IMPORTANTE: Estas porciones de código se deben dejar antes de las
		// validaciones de stock 
        // -----------------------------------------------------------------------
		// Para artículos bienes de uso que posean conjunto de atributos
		// asignado y la línea no tenga una instancia del conjunto de atributos,
		// se debe eliminar esa línea y crear tantas líneas como cantidad se
		// haya ingresado, asignarle una instancia de conjunto de atributos 
        // -----------------------------------------------------------------------
        if(getInventoryKind().equals(INVENTORYKIND_SimpleInOut)){
        	manageAssetLines();
        }
        // -----------------------------------------------------------------------
        
        
		boolean existsLines = PO.existRecordFor(getCtx(),
				MInventoryLine.Table_Name, "m_inventory_id = ?",
				new Object[] { getID() }, get_TrxName());
//        MInventoryLine[] lines = getLines( true );
//
//        if( lines.length == 0 ) {
//            m_processMsg = "@NoLines@";
//
//            return DocAction.STATUS_Invalid;
//        }

		if( !existsLines ) {
          m_processMsg = "@NoLines@";

          return DocAction.STATUS_Invalid;
		}
		
        // TODO: Add up Amounts
        // setApprovalAmt();
        
        m_justPrepared = true;

        if( !DOCACTION_Complete.equals( getDocAction())) {
            setDocAction( DOCACTION_Complete );
        }

        return DocAction.STATUS_InProgress;
    }    // prepareIt

    
    /**
	 * Gestiona las líneas de bienes de uso, eliminando las líneas que poseen
	 * artículos bienes de uso que no tengan instancia de conjunto de atributos
	 * seteada y además que el artículo propio contenga un conjunto de atributos
	 * seteado. De esa manera, se crea la cantidad de líneas automáticamente con
	 * cada instancia del artículo por la cantidad agregada en cada línea,
	 * eliminando la línea original sin instancia de conjunto de atributos.
	 * 
	 * @return resultado de la operación
	 */
    private CallResult manageAssetLines(){
    	CallResult result = new CallResult();
		List<PO> assetLines = PO
				.find(getCtx(),
						X_M_InventoryLine.Table_Name,
						"m_product_id IN (select m_product_id from m_product where producttype = 'A' and m_attributeset_id is not null and m_attributeset_id <> 0) AND m_inventory_id = ?",
						new Object[]{getID()}, null, get_TrxName());
		MInventoryLine inventoryLine = null, newInventoryLine = null;
		MAttributeSetInstance instance = null;
		AssetDTO assetDTO = null;
		Integer sign;
		for (PO line : assetLines) {
			List<MInventoryLine> newLines = new ArrayList<MInventoryLine>();
			inventoryLine = (MInventoryLine)line;
			int cant = inventoryLine.getQtyCount().abs().intValue();
			for (int i = 0; i < cant; i++) {
				newInventoryLine = new MInventoryLine(getCtx(), 0, get_TrxName());
				PO.copyValues(inventoryLine, newInventoryLine);
				sign = newInventoryLine.getQtyCount().signum();
				newInventoryLine.setQtyCount(new BigDecimal(sign));
				newInventoryLine.setQtyCountWithoutChargeSign(new BigDecimal(1));
				// Crear la clase con los datos necesarios para crear la instancia
				assetDTO = new AssetDTO();
				assetDTO.setAttributeSetInstanceID(!Util.isEmpty(
						inventoryLine.getM_AttributeSetInstance_ID(), true) ? inventoryLine
						.getM_AttributeSetInstance_ID() : 0);
				// En caso de entrada de mercadería, agregar la fecha de alta y el costo
				if(sign >= 0){
					assetDTO.setCost(newInventoryLine.getCost());
					assetDTO.setDateFrom(getMovementDate());
				}
				// En caso de salida, la fecha de baja
				else{
					assetDTO.setDateTo(getMovementDate());
				}
				assetDTO.setProductID(inventoryLine.getM_Product_ID());
				assetDTO.setCtx(getCtx());
				assetDTO.setTrxName(get_TrxName());
				try{
					// Crear la instancia de atributos
					instance = MAttributeSetInstance.createAssetAttributeInstance(assetDTO);
				} catch(Exception e){
					result.setMsg(e.getMessage(), true);
					return result;
				}
				newInventoryLine.setM_AttributeSetInstance_ID(instance.getID());
				newLines.add(newInventoryLine);
			}
			// Eliminar la línea anterior
			if(!inventoryLine.delete(true, get_TrxName())){
				result.setMsg(CLogger.retrieveErrorAsString(), true);
				return result;
			}
			// Guardar todas las líneas nuevas
			for (MInventoryLine mInventoryLine : newLines) {
				if(!mInventoryLine.save()){
					result.setMsg(CLogger.retrieveErrorAsString(), true);
					return result;
				}
			}
		}
		return result;
    }
    
    
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

        MInventoryLine[] lines = getLines( true );
        MInventoryLine line;
        MTransaction trx = null;
        MStorage storage = null;
        BigDecimal qtyDiff, qtyNew;
        BigDecimal qtyOrdered = BigDecimal.ZERO;
		BigDecimal qtyReserved = BigDecimal.ZERO;
		BigDecimal qtyOnHand = BigDecimal.ZERO;
		PreparedStatement ps = DB.prepareStatement("select coalesce(qtyonhand,0) as qtyonhand from m_storage where m_locator_id = ? and m_product_id = ? and M_AttributeSetInstance_ID = ?", get_TrxName());
		ResultSet rs = null;
		PreparedStatement psStorageUpdate = DB.prepareStatement("update m_storage set qtyonhand = ?, DateLastInventory = ? where m_locator_id = ? and m_product_id = ? and M_AttributeSetInstance_ID = ?", get_TrxName());
		boolean existsStorage = false;
		List<MStorage> storages;
        for( int i = 0;i < lines.length;i++ ) {
            line = lines[ i ];

            if( !line.isActive()) {
                continue;
            }

            trx = null;
            //Modificado por ConSerTi para el recuento de stock en el inventario físico
           /* if( line.getM_AttributeSetInstance_ID() == 0 ) {
            	log.fine("la línea de inventario no tiene m_attributesetinstace, line="+line.getM_InventoryLine_ID()+", producto="+line.getM_Product_ID());
                MInventoryLineMA mas[] = MInventoryLineMA.get( getCtx(),line.getM_InventoryLine_ID(),get_TrxName());

                for( int j = 0;j < mas.length;j++ ) {
                    MInventoryLineMA ma = mas[ j ];

                    // Storage

                    MStorage storage = MStorage.get( getCtx(),line.getM_Locator_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),get_TrxName());

                    if( storage == null ) {
                        storage = MStorage.getCreate( getCtx(),line.getM_Locator_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),get_TrxName());
                    }

                    //

                    BigDecimal qtyDiff = ma.getMovementQty();
                   
                    BigDecimal qtyNew  = storage.getQtyOnHand().add( qtyDiff );
                    
                    
                    //Fin Modificación ConSerTi
                    log.fine("En MInventory, al completar,qtynew="+qtyNew+", y qtyDiff="+qtyDiff);
                    log.fine( "MA Qty=" + ma.getMovementQty() + " - OnHand=" + storage.getQtyOnHand());

                    //

                    storage.setQtyOnHand( qtyNew );
                    storage.setDateLastInventory( getMovementDate());

                    if( !storage.save( get_TrxName())) {
                        m_processMsg = "Storage not updated (MA)";

                        return DocAction.STATUS_Invalid;
                    }

                    log.fine( storage.toString());

                    // Transaction

                    trx = new MTransaction( getCtx(),MTransaction.MOVEMENTTYPE_InventoryIn,line.getM_Locator_ID(),line.getM_Product_ID(),ma.getM_AttributeSetInstance_ID(),qtyDiff,getMovementDate(),get_TrxName());
                    trx.setM_InventoryLine_ID( line.getM_InventoryLine_ID());

                    if( !trx.save()) {
                        m_processMsg = "Transaction not inserted (MA)";

                        return DocAction.STATUS_Invalid;
                    }
                }
            }*/
            //Fin modificación
            // Fallback

            if( trx == null ) {

                // Si no hay que sobreescribir el stock, entonces se hace por la diferencia
            	
            	if(!X_M_InventoryLine.INVENTORYTYPE_OverwriteInventory.equals(line.getInventoryType())){
	            	existsStorage = false;
            		try{
            			ps.setInt(1, line.getM_Locator_ID());
                		ps.setInt(2, line.getM_Product_ID());
                		ps.setInt(3, line.getM_AttributeSetInstance_ID());
            			rs = ps.executeQuery();
            			if(rs.next()){
            				qtyOnHand = rs.getBigDecimal("qtyonhand");
            				existsStorage = true;
            			}
            		} catch(Exception e){
            			qtyOnHand = BigDecimal.ZERO;
            		}
            		
            		// Código viejo
            		if(!existsStorage){
            			// Storage
                		storage = MStorage.getCreate( getCtx(),line.getM_Locator_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstance_ID(),get_TrxName());
    	                //
    	
    	                qtyDiff = line.getQtyInternalUse().negate();
    	
    	                if( Env.ZERO.compareTo( qtyDiff ) == 0 ) {
    	                    qtyDiff = line.getQtyCount().subtract( line.getQtyBook());
    	                }
    	
    	                qtyNew = storage.getQtyOnHand().add( qtyDiff );
    	
    	                log.fine( "Count=" + line.getQtyCount() + ",Book=" + line.getQtyBook() + ", Difference=" + qtyDiff + " - OnHand=" + storage.getQtyOnHand());
    	
    	                storage.setQtyOnHand( qtyNew );
    	                storage.setDateLastInventory( getMovementDate());
    	                if( !storage.save( get_TrxName())) {
    	                    m_processMsg = "Storage not updated";
    	
    	                    return DocAction.STATUS_Invalid;
    	                }
    	                log.fine( storage.toString());
    	
    	                // Transaction
    	                // Sólo se guarda la transacción si el movimiento es distinto de 0
    	                if(!Util.isEmpty(qtyDiff, true)){
	    	                trx = new MTransaction( getCtx(),MTransaction.MOVEMENTTYPE_InventoryIn,line.getM_Locator_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstance_ID(),qtyDiff,getMovementDate(),get_TrxName());
	    	                trx.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
	    	                trx.setClientOrg(this);
	    	                trx.setDescription("MInventory.complete() - 1st Transaction Save - Transaction of MTransaction "
	    							+ trx.get_TrxName());
	    	                
	    	                if( !trx.save()) {
	    	                    m_processMsg = "Transaction not inserted";
	    	
	    	                    return DocAction.STATUS_Invalid;
	    	                }
    	                }
            		}
            		// Código nuevo + performante
            		else{
            			
    	                qtyDiff = line.getQtyInternalUse().negate();
    	
    	                if( Env.ZERO.compareTo( qtyDiff ) == 0 ) {
    	                    qtyDiff = line.getQtyCount().subtract( line.getQtyBook());
    	                }
    	
    	                qtyNew = qtyOnHand.add( qtyDiff );
    	                
    	                try{
    	                	psStorageUpdate.setBigDecimal(1, qtyNew);
    	                	psStorageUpdate.setTimestamp(2, getMovementDate());
    	                	psStorageUpdate.setInt(3, line.getM_Locator_ID());
    	                	psStorageUpdate.setInt(4, line.getM_Product_ID());
    	                	psStorageUpdate.setInt(5, line.getM_AttributeSetInstance_ID());
    	                	psStorageUpdate.executeUpdate();
    	                } catch(Exception e){
    	                	m_processMsg = "Storage not updated";
    	                	try{
    	                    	if(rs != null)rs.close();
    	                    	if(ps != null)ps.close();
    	                    	if(psStorageUpdate != null)psStorageUpdate.close();
    	                    } catch(Exception e1){
    	                    	e1.printStackTrace();
    	                    }
    	                    return DocAction.STATUS_Invalid;
    	                }
    	                // Transaction
    	                // Sólo se guarda la transacción si el movimiento es distinto de 0
    	                if(!Util.isEmpty(qtyDiff, true)){
	    	                trx = new MTransaction( getCtx(),MTransaction.MOVEMENTTYPE_InventoryIn,line.getM_Locator_ID(),line.getM_Product_ID(),line.getM_AttributeSetInstance_ID(),qtyDiff,getMovementDate(),get_TrxName());
	    	                trx.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
	    	                trx.setClientOrg(this);
	    	                trx.setDescription("MInventory.complete() - 1st Transaction Save - Transaction of MTransaction "
	    							+ trx.get_TrxName());
	    	                if( !trx.save()) {
	    	                    m_processMsg = "Transaction not inserted";
	    	                    return DocAction.STATUS_Invalid;
	    	                }	
    	                }
            		}
	            }    // Fallback
            	// Sobreescribir el stock de ese producto e instancia de atributos en esa ubicación
            	else{
					// Eliminar todas las ocurrencias de stock que posea ese
					// artículo, instancia en la ubicación. 
            		// Se deben mantener las cantidades pedidas y reservadas
            		qtyOrdered = BigDecimal.ZERO;
            		qtyReserved = BigDecimal.ZERO;
            		qtyOnHand = BigDecimal.ZERO;
            		// Además, hay que guardar estas transacciones
					storages = MStorage.getAll(getCtx(),
							line.getM_Product_ID(), line.getM_Locator_ID(),
							line.getM_AttributeSetInstance_ID(), get_TrxName());
					for (MStorage mStorage : storages) {
						// Me guardo la cantidad pedida y reservada
						qtyOrdered = qtyOrdered.add(mStorage.getQtyOrdered());
						qtyReserved = qtyReserved.add(mStorage.getQtyReserved());
						qtyOnHand = qtyOnHand.add(mStorage.getQtyOnHand());
						// Elimino el stock
						if(!mStorage.delete(true,get_TrxName())){
							m_processMsg = "Storage not updated";
		                    return DocAction.STATUS_Invalid;
						}
					}
					
					// Agrego la transacción
					// Creo la transacción de salida
					trx = new MTransaction(getCtx(),
							MTransaction.MOVEMENTTYPE_InventoryOut,
							line.getM_Locator_ID(), line.getM_Product_ID(),
							line.getM_AttributeSetInstance_ID(),
							qtyOnHand, getMovementDate(),
							get_TrxName());
	                trx.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
	                trx.setClientOrg(this);
	                trx.setDescription("MInventory.complete() - 2nd Transaction Save - Transaction of MTransaction "
							+ trx.get_TrxName());
	                if( !trx.save()) {
	                    m_processMsg = "Transaction not inserted";
	                    return DocAction.STATUS_Invalid;
	                }
					
					// Creo el inventario de entrada con la cantidad actual de la línea
					MStorage newStorage = MStorage.getCreate(getCtx(),
							line.getM_Locator_ID(), line.getM_Product_ID(),
							line.getM_AttributeSetInstance_ID(), get_TrxName());
					
					newStorage.setQtyOnHand(line.getQtyCount());
					newStorage.setQtyOrdered(qtyOrdered);
					newStorage.setQtyReserved(qtyReserved);
					newStorage.setDateLastInventory(getMovementDate());
					if(!newStorage.save()){
						m_processMsg = "Storage not updated";
	                    return DocAction.STATUS_Invalid;
					}
					
					// Creo la transacción de entrada
					trx = new MTransaction(getCtx(),
							MTransaction.MOVEMENTTYPE_InventoryIn,
							line.getM_Locator_ID(), line.getM_Product_ID(),
							line.getM_AttributeSetInstance_ID(),
							line.getQtyCount(), getMovementDate(),
							get_TrxName());
	                trx.setM_InventoryLine_ID( line.getM_InventoryLine_ID());
	                trx.setClientOrg(this);
	                trx.setDescription("MInventory.complete() - 3rd Transaction Save - Transaction of MTransaction "
							+ trx.get_TrxName());
	                if( !trx.save()) {
	                    m_processMsg = "Transaction not inserted";
	                    return DocAction.STATUS_Invalid;
	                }
            	}
            }
        }        // for all lines
        
        try{
        	if(rs != null)rs.close();
        	if(ps != null)ps.close();
        	if(psStorageUpdate != null)psStorageUpdate.close();
        } catch(Exception e1){
        	e1.printStackTrace();
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
     */

    private void checkMaterialPolicy() {
        int no = MInventoryLineMA.deleteInventoryMA( getM_Inventory_ID(),get_TrxName());

        if( no > 0 ) {
            log.config( "Delete old #" + no );
        }

        MInventoryLine[] lines = getLines( false );

        // Incoming Trx

        MClient client = MClient.get( getCtx());

        // Check Lines

        for( int i = 0;i < lines.length;i++ ) {
            MInventoryLine line     = lines[ i ];
            boolean        needSave = false;

            // Attribute Set Instance

            if( line.getM_AttributeSetInstance_ID() == 0 ) {
                MProduct product = MProduct.get( getCtx(),line.getM_Product_ID());
                BigDecimal qtyDiff = line.getQtyInternalUse().negate();

                if( Env.ZERO.compareTo( qtyDiff ) == 0 ) {
                    qtyDiff = line.getQtyCount().subtract( line.getQtyBook());
                }

                log.fine( "Count=" + line.getQtyCount() + ",Book=" + line.getQtyBook() + ", Difference=" + qtyDiff );

                if( qtyDiff.signum() > 0 )    // In
                {
                    MAttributeSetInstance asi = new MAttributeSetInstance( getCtx(),0,get_TrxName());

                    asi.setClientOrg( getAD_Client_ID(),0 );
                    asi.setM_AttributeSet_ID( product.getM_AttributeSet_ID());

                    if( asi.save()) {
                        line.setM_AttributeSetInstance_ID( asi.getM_AttributeSetInstance_ID());
                        needSave = true;
                    }
                } else    // Outgoing Trx
                {
                    MProductCategory pc = MProductCategory.get( getCtx(),product.getM_Product_Category_ID(), get_TrxName());
                    String MMPolicy = pc.getMMPolicy();

                    if( (MMPolicy == null) || (MMPolicy.length() == 0) ) {
                        MMPolicy = client.getMMPolicy();
                    }

                    //

                    MStorage[] storages = MStorage.getAll( getCtx(),line.getM_Product_ID(),line.getM_Locator_ID(),MClient.MMPOLICY_FiFo.equals( MMPolicy ),get_TrxName());
                    BigDecimal qtyToDeliver = qtyDiff.negate();

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

                                MInventoryLineMA ma = new MInventoryLineMA( line,storage.getM_AttributeSetInstance_ID(),storage.getQtyOnHand().negate());

                                if( !ma.save()) {
                                    ;
                                }

                                qtyToDeliver = qtyToDeliver.subtract( storage.getQtyOnHand());
                                log.fine( "#" + ii + ": " + ma + ", QtyToDeliver=" + qtyToDeliver );
                            }
                        } else    // create addl material allocation
                        {
                            MInventoryLineMA ma = new MInventoryLineMA( line,storage.getM_AttributeSetInstance_ID(),qtyToDeliver.negate());

                            if( storage.getQtyOnHand().compareTo( qtyToDeliver ) >= 0 ) {
                                qtyToDeliver = Env.ZERO;
                            } else {
                                ma.setMovementQty( storage.getQtyOnHand().negate());
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
                        MInventoryLineMA ma = new MInventoryLineMA( line,0,qtyToDeliver.negate());

                        if( !ma.save()) {
                            ;
                        }

                        log.fine( "##: " + ma );
                    }
                }    // outgoing Trx
            }        // attributeSetInstance

            if( needSave &&!line.save()) {
                log.severe( "NOT saved " + line );
            }
        }            // for all lines
    }                // checkMaterialPolicy

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
        
        // Imposible anular si el status es: Cerrado, Revertido, Anulado
		if (DOCSTATUS_Closed.equals(getDocStatus())
				|| DOCSTATUS_Reversed.equals(getDocStatus())
				|| DOCSTATUS_Drafted.equals(getDocStatus())
				|| DOCSTATUS_Voided.equals(getDocStatus())) {
			m_processMsg = Msg.getMsg(getCtx(), "InvalidAction") + ", Document status: " + getDocStatus();
			setDocAction(DOCACTION_None);
			return false;
		}

		// Anulación de inventario UNICAMENTE si el status es Completado
		// Se genera un nuevo inventario cuyas líneas contienen los valores multiplicados por -1
		if (DOCSTATUS_Completed.equals(getDocStatus())) {
			// Nuevo documento de inventario reverso
			MInventory reversal = new MInventory(getCtx(), 0, get_TrxName());
			PO.copyValues(this, reversal);
			reversal.setM_Inventory_ID(0);
			reversal.set_ValueNoCheck( "DocumentNo",getDocumentNo()+"^" );
			reversal.setDocStatus(DOCSTATUS_InProgress);	//IP
			reversal.setDocAction( DOCACTION_Complete );
			reversal.setDescription(Msg.getMsg(getCtx(), "Voided") + " --> " + getDocumentNo());
			if (!reversal.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				return false;
			}
			// Iterar por las líneas de inventario, negando los valores
			int[] lineIDs = PO.getAllIDs(MInventoryLine.Table_Name, "M_Inventory_ID = " + getM_Inventory_ID(), get_TrxName());
			for (int i=0; i < lineIDs.length; i++) {
				X_M_InventoryLine aLine = new X_M_InventoryLine(getCtx(), lineIDs[i], get_TrxName());
				X_M_InventoryLine revLine = new X_M_InventoryLine(getCtx(), 0, get_TrxName());
				PO.copyValues(aLine, revLine);
				revLine.setQtyCount(aLine.getQtyCount().negate());
				revLine.setQtyBook(aLine.getQtyBook().negate());
				revLine.setQtyInternalUse(aLine.getQtyInternalUse().negate());
				revLine.setQtyCountWithoutChargeSign(aLine.getQtyCountWithoutChargeSign().negate());
				revLine.setM_Inventory_ID(reversal.getM_Inventory_ID());
				if (!revLine.save()) {
					m_processMsg = CLogger.retrieveErrorAsString();
					return false;
				}
			}
			// Completar el documento reverso
			if (!reversal.processIt(DOCACTION_Complete)) {
				m_processMsg = reversal.getProcessMsg();
				return false;
			}
			// El estado del documento reverso no queda cerrado sino anulado
	        reversal.setDocStatus(DOCSTATUS_Voided);
	        reversal.setDocAction(DOCACTION_None);
	        if (!reversal.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				return false;
	        }
			// Sin más acciones para el documento original
			setDescription((getDescription()!=null?getDescription()+" - ":"") + Msg.getMsg(getCtx(), "Voided"));
			setProcessed(true);
			setDocAction(DOCACTION_None);
			return true;
		}
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
        	return false;
        }
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
    
    /**
     * @return Indica si es posible procesar este inventario según ciertos
     * criterios de validación.
     */
    private boolean canProcessAction(String docAction) {
    	MSplitting splitting = getSplitting();
    	MProductChange productChange = getProductChange();
    	MTransfer transfer = getTransfer();
    	boolean canProcess = true; // Por defecto es posible
    	// Accion: Cerrar
    	if (DOCACTION_Close.equals(docAction)) {
			// Si pertenece a un fraccionamiento, solo se puede cerrar si
			// es el propio fraccionamiento el que está intentando cerrar este
			// inventario.
    		if (splitting != null) {
    			canProcess = 
    				(getCallerDocument() != null && 
    				 getCallerDocument() instanceof MSplitting &&
    				 getCallerDocument().getID() == splitting.getM_Splitting_ID());
    			if (!canProcess) {
    				m_processMsg = "@CannotCloseSplittingInventoryDirectly@";
    			}

    		// Si pertenece a un Cambio de Artículo, solo se puede cerrar si
			// es el propio cambio de ariculo el que está intentando cerrar este
			// inventario.
    		} else if (productChange != null) {
    			canProcess = 
   					(getCallerDocument() != null && 
   					 getCallerDocument() instanceof MProductChange &&
   					 getCallerDocument().getID() == productChange.getM_ProductChange_ID());
    			if (!canProcess) {
    				m_processMsg = "@CannotCloseProdChangeInventoryDirectly@";
    			}
    		
    		// Si pertenece a una Transferencia, solo se puede cerrar si
			// es la propia Transferencia la que está intentando cerrar este
			// inventario.
    		} else if (transfer != null) {
    			canProcess = 
   					(getCallerDocument() != null && 
   					 getCallerDocument() instanceof MTransfer &&
   					 getCallerDocument().getID() == transfer.getM_Transfer_ID());
    			if (!canProcess) {
    				m_processMsg = "@CannotCloseTransferInventoryDirectly@";
    			}
    		}

    	}
    	return canProcess;
    }
    
    /** Documento que invoca el procesamiento de este inventario */
    private PO callerDocument = null;

	/**
	 * @return Devuelve el documento que invoca el procesamiento de este inventario.
	 */
	protected PO getCallerDocument() {
		return callerDocument;
	}

	/**
	 * Asigna el documento que invoca el procesamiento de este inventario.
	 * @param callerDocument PO 
	 */
	protected void setCallerDocument(PO callerDocument) {
		this.callerDocument = callerDocument;
	}
	
	/**
	 * @return Devuelve el fraccionamiento al cual pertenece este inventario. En
	 * caso de NO ser un inventario generado por fraccionamiento devuelve null.
	 */
	public MSplitting getSplitting() {
    	MSplitting splitting = null;
		String sql = 
    		"SELECT M_Splitting_ID " +
    		"FROM M_Splitting " +
    		"WHERE M_Inventory_ID = ? OR Void_Inventory_ID = ?";
    	Integer splittingID = (Integer)DB.getSQLObject(get_TrxName(), sql, 
    		new Object[] {
    			getM_Inventory_ID(), getM_Inventory_ID()
    		}
    	);
    	if (splittingID != null && splittingID > 0) {
    		splitting = new MSplitting(getCtx(), splittingID, get_TrxName());
    	}
    	return splitting;
	}
	
	/**
	 * @return Devuelve el Cambio de Artículo al cual pertenece este inventario. En
	 * caso de NO ser un inventario generado por un Cambio de Ariculo devuelve null.
	 */
	public MProductChange getProductChange() {
		MProductChange productChange = null;
		String sql = 
    		"SELECT M_ProductChange_ID " +
    		"FROM M_ProductChange " +
    		"WHERE M_Inventory_ID = ? OR Void_Inventory_ID = ?";
    	Integer productChangeID = (Integer)DB.getSQLObject(get_TrxName(), sql, 
    		new Object[] {
    			getM_Inventory_ID(), getM_Inventory_ID()
    		}
    	);
    	if (productChangeID != null && productChangeID > 0) {
    		productChange = new MProductChange(getCtx(), productChangeID, get_TrxName());
    	}
    	return productChange;
	}

	/**
	 * @return Devuelve la Transferencia de Mercadería a la cual pertenece este inventario. 
	 * En caso de NO ser un inventario generado por una Transferencia devuelve null.
	 */
	public MTransfer getTransfer() {
		MTransfer transfer = null;
		String sql = 
    		"SELECT M_Transfer_ID " +
    		"FROM M_Transfer " +
    		"WHERE M_Inventory_ID = ? ";
    	Integer transferID = (Integer)DB.getSQLObject(get_TrxName(), sql, 
    		new Object[] {getM_Inventory_ID()}
    	);
    	if (transferID != null && transferID > 0) {
    		transfer = new MTransfer(getCtx(), transferID, get_TrxName());
    	}
    	return transfer;
	}

	
}    // MInventory



/*
 *  @(#)MInventory.java   02.07.07
 * 
 *  Fin del fichero MInventory.java
 *  
 *  Versión 2.2
 *
 */
