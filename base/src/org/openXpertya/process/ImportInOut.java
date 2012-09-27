package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MTransaction;
import org.openXpertya.model.X_I_InOut;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class ImportInOut extends SvrProcess {

	/** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private boolean m_deleteOldImported = false;

    /** Descripción de Campos */

    private String m_docAction = MInOut.DOCACTION_Prepare;

    /** Descripción de Campos */

    private Timestamp m_DateValue = null;
    
	private String msgInOutProcessError = null;
	
	private String clientCheck;
	
	private String m_onActionError = null;
	
	int noDeleted	= 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( name.equals( "AD_Client_ID" )) {
                m_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "AD_Org_ID" )) {
                m_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DeleteOldImported" )) {
                m_deleteOldImported = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "DocAction" )) {
                m_docAction = ( String )para[ i ].getParameter();
            } else if( name.equals( "OnActionError" )) {
				m_onActionError = ( String )para[ i ].getParameter();
			} else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        if( m_DateValue == null ) {
            m_DateValue = new Timestamp( System.currentTimeMillis());
        }
    }    // prepare
    
   
    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
    	try {
    		StringBuffer sql         = null;
    		int          no          = 0;
    		clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;
    		String       securityCheck = " AND AD_Client_ID=" + m_AD_Client_ID + " AND CreatedBy=" + getAD_User_ID() + " AND IsActive = 'Y' ";
    		boolean      error = false;

    		// ****    Prepare ****

    		// Delete Old Imported

    		if( m_deleteOldImported ) {
    			sql = new StringBuffer( "DELETE I_InOut " + "WHERE I_IsImported='Y'" ).append( clientCheck );
    			no = DB.executeUpdate( sql.toString());
    			log.log(Level.SEVERE,"doIt - Delete Old Imported =" + no);
    		}
    		
    		// Set Client, IsActive, Created/Updated
    		sql = new StringBuffer( "UPDATE I_InOut " + "SET AD_Client_ID = COALESCE (AD_Client_ID," ).append( m_AD_Client_ID ).append( "), IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " I_ErrorMsg = ''," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.SEVERE, "doIt - Reset=" + no );
    		
    		// ----------------------------------------------------------------------------------
    		// - Número de Documento obligatorio en todos los registros
    		// ----------------------------------------------------------------------------------		
    		sql = new StringBuffer(
    				"UPDATE I_InOut i " + 
    				"SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutDocumentNoMandatory") + ". ' WHERE (DocumentNo IS NULL OR trim(DocumentNo)::bpchar = ''::bpchar) AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid DocumentNo=" + no);
    			throw new Exception("@ImportInOutDocumentNoError@");
    		}

    		// ----------------------------------------------------------------------------------
    		// - Organizacion
    		// ----------------------------------------------------------------------------------		
    		sql = new StringBuffer();
    		sql.append("UPDATE i_inout i ");
    		sql.append( "SET ad_org_id = "); 
    		sql.append(		"( ");
    		sql.append(			"SELECT o.ad_org_id ");
    		sql.append(			"FROM ad_org o ");
    		sql.append(			"WHERE o.value = i.orgvalue AND o.AD_Client_ID = " + m_AD_Client_ID);
    		sql.append(		") ");
    		sql.append("WHERE OrgValue IS NOT NULL AND I_IsImported<>'Y' ").append(clientCheck);		
    		no = DB.executeUpdate( sql.toString());		
    		
    		sql = new StringBuffer("UPDATE I_InOut i " + "SET AD_Org_ID = NULL, I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutInvalidOrgValue") + ". ' WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0) AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid Org=" + no);
    		}

    		// ----------------------------------------------------------------------------------
    		// - Tipo de Documento
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer();
    		sql.append("UPDATE i_inout i ");
    		sql.append(		"SET C_DocType_ID = "); 
    		sql.append(		"( ");
    		sql.append(			"SELECT d.C_DocType_ID ");
    		sql.append(			"FROM C_DocType d ");
    		sql.append(			"WHERE d.DocTypeKey = i.doctypename AND d.AD_Client_ID = " + m_AD_Client_ID);
    		sql.append(		") ");
    		sql.append("WHERE C_DocType_ID IS NULL  AND I_IsImported<>'Y' ").append(clientCheck);		
    		no = DB.executeUpdate( sql.toString());
    		
    		sql = new StringBuffer("UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutInvalidDocType")+". ' " + "WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid DocType=" + no);
    			error = true;
    		}

    		// ----------------------------------------------------------------------------------
    		// - Seteo IsSOTrx
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer();
    		sql.append("UPDATE I_InOut i ");
    		sql.append(		"SET IsSoTrx = ");
    		sql.append(			"(SELECT IsSoTrx ");
    		sql.append(			" FROM C_DocType d ");
    		sql.append(			" WHERE d.c_doctype_id = i.c_doctype_id ");
    		sql.append(			" AND i.AD_Client_ID =d.AD_Client_ID \n");
    		sql.append(			")");
    		sql.append("WHERE  I_IsImported<>'Y' AND i.C_DocType_ID IS NOT NULL ");
    		sql.append(clientCheck);
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set IsSOTrx=" + no);
    		
    		// ----------------------------------------------------------------------------------
    		// - Tipo de Documento del Pedido Asociado
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer();
    		sql.append("UPDATE i_inout i ");
    		sql.append(		"SET C_DocTypeOrder_ID = "); 
    		sql.append(		"( ");
    		sql.append(			"SELECT d.C_DocType_ID ");
    		sql.append(			"FROM C_DocType d ");
    		sql.append(			"WHERE d.DocTypeKey = i.doctypenameorder AND d.AD_Client_ID = " + m_AD_Client_ID);
    		sql.append(		") ");
    		sql.append("WHERE C_DocTypeOrder_ID IS NULL  AND I_IsImported<>'Y' ").append(clientCheck);		
    		no = DB.executeUpdate( sql.toString());
    		
    		sql = new StringBuffer("UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutInvalidDocTypeOrder")+". ' " + "WHERE C_DocTypeOrder_ID IS NULL AND DocTypeNameOrder IS NOT NULL" + " AND I_IsImported<>'Y'" + " AND IsSoTrx='Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid DocTypeOrder=" + no);
    			error = true;
    		}
    		
    		// ----------------------------------------------------------------------------------
    		// - ID del Pedido Asociado
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer();
    		sql.append("UPDATE i_inout i ");
    		sql.append(		"SET C_Order_ID = "); 
    		sql.append(		"( ");
    		sql.append(			"SELECT d.C_Order_ID ");
    		sql.append(			"FROM C_Order d ");
    		sql.append(			"WHERE d.C_DocType_ID = i.C_DocTypeOrder_ID AND d.DocumentNo = i.DocumentNoOrder AND d.AD_Client_ID = " + m_AD_Client_ID);
    		sql.append(		") ");
    		sql.append("WHERE C_Order_ID IS NULL  AND I_IsImported<>'Y' ").append(clientCheck);		
    		no = DB.executeUpdate( sql.toString());
    		
    		sql = new StringBuffer("UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutInvalidOrder")+". ' " + "WHERE C_Order_ID IS NULL" + " AND I_IsImported<>'Y'"  + " AND IsSoTrx='Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid OrderID=" + no);
    			error = true;
    		}
    		
			//////////////////////////////////////////////////////////////////////////////////////
			// Set Locator
			//////////////////////////////////////////////////////////////////////////////////////
			
			// ... From Value
			sql = new StringBuffer( 
			" UPDATE I_InOut i " + 
			" SET M_Locator_ID = " +
			"		(SELECT M_Locator_ID " +
			"	 	 FROM M_Locator l" + 
			"    	 WHERE i.LocatorValue = l.Value AND " +
			"              i.AD_Client_ID = l.AD_Client_ID AND " +
			"              l.IsActive = 'Y' AND " +
			"              ROWNUM=1) " + 
			" WHERE LocatorValue IS NOT NULL AND " +
			"       I_IsImported <> 'Y' ").append( securityCheck );
			no = DB.executeUpdate( sql.toString());
			log.fine( "Set Locator from Value = " + no );
			
			sql = new StringBuffer("UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutInvalidLocator")+". ' " + "WHERE M_Locator_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( securityCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid LocatorID=" + no);
    			error = true;
    		}
    		
			//////////////////////////////////////////////////////////////////////////////////////
			// Set Warehouse
			//////////////////////////////////////////////////////////////////////////////////////
			
			// ... From Value
			sql = new StringBuffer( 
			" UPDATE I_InOut i " + 
			" SET M_Warehouse_ID = " +
			"		(SELECT M_Warehouse_ID " +
			"	 	 FROM M_Warehouse w" + 
			"    	 WHERE (TRIM(i.WarehouseValue) = TRIM(w.Value) OR" +
			"               TRIM(i.WarehouseValue) = TRIM(w.Name)) AND " +
			"              i.AD_Client_ID = w.AD_Client_ID AND " +
			"              w.IsActive = 'Y' AND " +
			"              ROWNUM=1) " + 
			" WHERE WarehouseValue IS NOT NULL AND " +
			"       I_IsImported <> 'Y' ").append( securityCheck );
			no = DB.executeUpdate( sql.toString());
			log.fine( "Set Warehouse from Value = " + no );
			
			// ...From Locator
			sql = new StringBuffer( 
			" UPDATE I_InOut i " + 
			" SET M_Warehouse_ID = " +
			"		(SELECT M_Warehouse_ID " +
			"        FROM M_Locator l " +
			"        WHERE i.M_Locator_ID=l.M_Locator_ID) " + 
			" WHERE M_Warehouse_ID IS NULL AND " +
			"       M_Locator_ID IS NOT NULL AND " +
			"       I_IsImported <> 'Y' ").append( securityCheck );
			no = DB.executeUpdate( sql.toString());
			log.fine( "Set Warehouse from Locator = " + no );
			
			sql = new StringBuffer("UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutInvalidWarehouse")+". ' " + "WHERE M_Warehouse_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( securityCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid WarehouseID=" + no);
    			error = true;
    		}

    		// ----------------------------------------------------------------------------------
    		// - Entidad Comercial
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer( 
    				"UPDATE I_InOut o " + 
    				"SET C_BPartner_ID=" +
    					"(SELECT C_BPartner_ID FROM C_BPartner bp" + 
    					" WHERE o.BPartnerValue=bp.Value AND o.AD_Client_ID=bp.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE BPartnerValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set BP from Value=" + no);

    		sql = new StringBuffer("UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutInvalidBPartner")+". ' " + "WHERE C_BPartner_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );		
    		no = DB.executeUpdate( sql.toString());
            if( no != 0 ) {
            	log.log(Level.SEVERE,"doIt - BP Not Found=" + no);
            }
            
    		sql = new StringBuffer( 
    				"UPDATE I_InOut o " + 
    				"SET C_Bpartner_Location_ID=" +
    					"(SELECT C_Bpartner_Location_ID FROM C_BPartner_Location bp" + 
    					" WHERE bp.C_BPartner_ID = o.C_BPartner_ID AND o.AD_Client_ID=bp.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE BPartnerValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set BPLocation from Value=" + no);

    		// Se valida si
    		// la EC tiene al menos una dirección si no se marca como error
            sql = new StringBuffer("UPDATE I_InOut i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInOutInvalidBPartnerLocation")+". ' " + "WHERE C_BPartner_ID IS NOT NULL AND I_IsImported<>'Y' AND NOT EXISTS (SELECT C_Bpartner_Location_ID FROM C_BPartner_Location bpl WHERE bpl.C_BPartner_ID = i.C_BPartner_ID) " ).append( clientCheck );

    		// ----------------------------------------------------------------------------------
    		// - Comercial / Usuario
    		// ----------------------------------------------------------------------------------

            sql = new StringBuffer( "UPDATE I_InOut i " + 
    			    "SET SalesRep_ID=(SELECT AD_User_ID " +
    			    				 "FROM AD_User u " + 
    			    				 "WHERE u.Name = i.SalesRep_Name AND u.AD_Client_ID IN (0, i.AD_Client_ID)) " + 
    			    "WHERE SalesRep_ID IS NULL AND SalesRep_Name IS NOT NULL " + 
    			    "AND I_IsImported<>'Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "Set SalesRep=" + no );

            sql = new StringBuffer( "UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportInOutInvalidSalesRep")+". ' " + "WHERE SalesRep_ID IS NULL AND SalesRep_Name IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid SalesRep Name=" + no);
    		}

    		// ----------------------------------------------------------------------------------
    		// - Artículo
    		// ----------------------------------------------------------------------------------

    		// Desde Value
    		sql = new StringBuffer( 
    				"UPDATE I_InOut o " + 
    				"SET M_Product_ID=" +
    					"(SELECT M_Product_ID FROM M_Product p" + 
    					" WHERE o.ProductValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE ProductValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set Product from Value=" + no);

    		// Desde UPC
    		sql = new StringBuffer( 
    				"UPDATE I_InOut o " + 
    				"SET M_Product_ID=" +
    					"(SELECT M_Product_ID FROM M_ProductUPC p" + 
    					" WHERE o.UPC=p.UPC AND o.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE UPC IS NOT NULL AND M_Product_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set Product from Value=" + no);

    		sql = new StringBuffer( "UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportInOutInvalidProduct")+". ' " + "WHERE M_Product_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());		

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid Product=" + no);
    		}		
    		
    		sql = new StringBuffer( 
    				"UPDATE I_InOut o " + 
    				"SET C_OrderLine_ID=" +
    					"(SELECT C_OrderLine_ID FROM C_OrderLine p" + 
    					" WHERE o.C_Order_ID=p.C_Order_ID AND o.line = p.line AND o.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE line IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set C_OrderLine_ID from line=" + no);

    		sql = new StringBuffer( "UPDATE I_InOut " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportInOutInvalidProduct")+". ' " + "WHERE C_OrderLine_ID IS NULL AND I_IsImported<>'Y' AND IsSoTrx='Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());		

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid C_OrderLine_ID=" + no);
    		}	

    		// -- New InOuts -----------------------------------------------------

    		int noInsert     = 0;
    		int noInsertLine = 0;
    		int noProcessed = 0;

    		// Go through InOut Records w/o

    		sql = new StringBuffer( "SELECT * FROM I_InOut " + "WHERE I_IsImported='N'" ).append( clientCheck ).append( " ORDER BY DocumentNo, C_Doctype_ID, C_BPartner_ID, C_BPartner_Location_ID, I_InOut_ID" );

    		try {
    			PreparedStatement pstmt = DB.prepareStatement( sql.toString());
    			ResultSet         rs    = pstmt.executeQuery();

    			// Group Change

    			int    oldC_BPartner_ID          = 0;
    			int    oldC_BPartner_Location_ID = 0;
    			int	   oldDocTypeID				 = 0;
    			String oldDocumentNo             = "";
    			String lastInOutSaveError      = null;
    			boolean inOutLineError         = false;
    			Timestamp today 				 = Env.getDate();
    			//

    			MInOut inOut = null;
    			int      lineNo  = 0;

    			while( rs.next()) {
    				X_I_InOut imp           = new X_I_InOut( getCtx(),rs,null );
    				String      cmpDocumentNo = imp.getDocumentNo();

    				// **************************************************************
    				// PRECONDICION: imp.getDocumentNo() no puede ser NULL ni vacío.
    				// (esto está validado al principio).
    				// **************************************************************
    				
    				// Cambia el BPartner o el Número de Documento o el tipo de documento...
    				if (oldC_BPartner_ID != imp.getC_BPartner_ID()
    							|| !oldDocumentNo.equals(cmpDocumentNo)
    							|| oldDocTypeID != imp.getC_DocType_ID()) {
    					
    					if (inOut != null) {

    						// Si hubo error en el guardado de alguna de las líneas
    						// entonces se elimina todo el pedido y se marcan todos
    						// los registro de importación del pedido como no
    						// importados para que el usuario pueda corregir el
    						// problema y volver a intentar importar el pedido
    						if (inOutLineError) {
    							noDeleted += deleteInOut(inOut)?1:0;

    						// Si no hay error en las líneas y el pedido
    						// está guardada correctamente entonces solo
    						// resta procesar la acción indicada como
    						// parámetro siempre y cuando el usuario haya
    						// ingresado una acción
    						} else if (m_docAction != null && lastInOutSaveError == null && processInOut(inOut, m_docAction)) {
    							noProcessed++;
    						}
    					}

    					// Cambio de grupo
    					oldC_BPartner_ID          = imp.getC_BPartner_ID();
    					oldC_BPartner_Location_ID = imp.getC_BPartner_Location_ID();
    					oldDocumentNo             = imp.getDocumentNo();
    					oldDocTypeID			  = imp.getC_DocType_ID();
    					lastInOutSaveError      = null;
    					inOutLineError          = false;

    					// Crea el nuevo pedido
    					inOut = new MInOut( getCtx(),0,null );
    					inOut.setClientOrg( imp.getAD_Client_ID(),imp.getAD_Org_ID());
    					inOut.setIsSOTrx( imp.isSOTrx());
    					inOut.setC_Order_ID(imp.getC_Order_ID());
    					inOut.setM_Warehouse_ID(imp.getM_Warehouse_ID());
    					
    					if(!imp.isDocumentNoBySequence()){
    						inOut.setDocumentNo(imp.getDocumentNo());
    					}

    					inOut.setC_BPartner_ID( imp.getC_BPartner_ID());
    					inOut.setC_BPartner_Location_ID( imp.getC_BPartner_Location_ID());
    					
    					inOut.setC_DocType_ID( imp.getC_DocType_ID());

    					if( imp.getAD_User_ID() != 0 ) {
    						inOut.setAD_User_ID( imp.getAD_User_ID());
    					}

    					if( imp.getDescription() != null ) {
    						inOut.setDescription( imp.getDescription());
    					}
    					
    	                String docTypeKey = imp.getDocTypeName();
    					String DocBaseType = (MDocType.getDocType(getCtx(), imp.getDocTypeName(), get_TrxName())).getDocBaseType();
    					
    	                if( DocBaseType.equals(MDocType.DOCTYPE_MaterialDelivery)) {           // Material Shipments
    	                	if(docTypeKey.equals(MDocType.DOCTYPE_CustomerReturn)){
    	                		inOut.setMovementType(MTransaction.MOVEMENTTYPE_CustomerReturns);
    	                	}
    	                	else{
    	                		inOut.setMovementType(MTransaction.MOVEMENTTYPE_CustomerShipment);
    	                	}
    	                } else if( DocBaseType.equals(MDocType.DOCTYPE_MaterialReceipt)) {    // Material Receipts
    	                	if(docTypeKey.equals(MDocType.DOCTYPE_VendorReturn)){
    	                		inOut.setMovementType(MTransaction.MOVEMENTTYPE_VendorReturns);
    	                	}
    	                	else{
    	                		inOut.setMovementType(MTransaction.MOVEMENTTYPE_VendorReceipts);
    	                	}
    	                }
    					
    					// SalesRep from Import or the person running the import

    					if( imp.getSalesRep_ID() != 0 ) {
    						inOut.setSalesRep_ID( imp.getSalesRep_ID());
    					}

    					if( imp.getAD_OrgTrx_ID() != 0 ) {
    						inOut.setAD_OrgTrx_ID( imp.getAD_OrgTrx_ID());
    					}

    					if( imp.getC_Activity_ID() != 0 ) {
    						inOut.setC_Activity_ID( imp.getC_Activity_ID());
    					}

    					if( imp.getC_Campaign_ID() != 0 ) {
    						inOut.setC_Campaign_ID( imp.getC_Campaign_ID());
    					}

    					if( imp.getC_Project_ID() != 0 ) {
    						inOut.setC_Project_ID( imp.getC_Project_ID());
    					}

    					Timestamp movementDate = imp.getMovementDate() != null ? imp.getMovementDate() : today;
    					
    					inOut.setDateAcct(movementDate);
    					inOut.setMovementDate(movementDate);
    					inOut.setDatePrinted(movementDate);

    					if( imp.getDateAcct() != null ) {
    						inOut.setDateAcct( imp.getDateAcct());
    					}
    					
    					inOut.setDocStatus(MInOut.DOCSTATUS_Drafted);
    					inOut.setDocAction(MInOut.DOCACTION_Complete);
    					inOut.setProcessed(false);
    					inOut.setProcessing(false);
    					
    					if(inOut.save()){
    						noInsert++;
    						lineNo = 10;

    						imp.setM_InOut_ID( inOut.getM_InOut_ID());
    					}else{
    						lastInOutSaveError = CLogger.retrieveErrorAsString();
    						imp.setI_ErrorMsg(lastInOutSaveError);
    						imp.save();
    						continue;
    					}
    				}
    				
    				// Creación de la línea del pedido
    				if(lastInOutSaveError != null) {
    					// Si hubo error al guardar el pedido se guarda el mensaje
    					// de error en cada línea ya que la corrección del error
    					// involucrará corregir alguno de los datos del
    					// encabezado que están presentes en todas las líneas.
    					imp.setI_ErrorMsg(lastInOutSaveError);
    					imp.save();
    				} else {	

    					imp.setM_InOut_ID( inOut.getM_InOut_ID());
    					// New InOutLine
    					MInOutLine line = new MInOutLine( inOut );
    					
    					line.setM_Locator_ID(imp.getM_Locator_ID());

    					if( imp.getLineDescription() != null ) {
    						line.setDescription( imp.getLineDescription());
    					}
    					 					
    					line.setC_OrderLine_ID(imp.getC_OrderLine_ID());
    					//line.setC_OrderLine_ID(DB.getSQLValue(get_TrxName(), "SELECT C_OrderLine_ID FROM C_OrderLine WHERE ((C_Order_ID = ?) AND (line = ?) AND (AD_Client_ID=" + m_AD_Client_ID+"))", imp.getC_Order_ID(), imp.getLine().intValue()));

    					line.setLine( lineNo );
    					lineNo += 10;

    					if( imp.getM_Product_ID() != 0 ) {
    						line.setM_Product_ID( imp.getM_Product_ID(),true );
    					}

    					line.setQty( imp.getMovementQty());

    					if(line.save()){
    						noInsertLine++;
    						imp.setM_InOutLine_ID( line.getM_InOutLine_ID());
    						imp.setM_InOut_ID(inOut.getM_InOut_ID());
    						imp.setI_IsImported( true );
    						imp.setProcessed( true );
    						imp.save();
    					} else {
    						imp.setI_ErrorMsg(imp.getI_ErrorMsg() + " " + CLogger.retrieveErrorAsString());
    						imp.save();
    						inOutLineError = true;
    					}
    				}
    			} // while

    			// Procesa el último remito
    			if( m_docAction != null && inOut != null && lastInOutSaveError == null && processInOut(inOut, m_docAction)) {
    				noProcessed++;
    			}

    			rs.close();
    			pstmt.close();
    		} catch( Exception e ) {
    			log.log( Level.SEVERE,"doIt - CreateInOut",e );
    			log.log(Level.SEVERE,"");
    		}

    		// Set Error to indicator to not imported

    		sql = new StringBuffer( "UPDATE I_InOut " + "SET I_IsImported='N', Updated=SysDate " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		addLog( 0,null,new BigDecimal( no ),"@Errors@" );

    		//

    		addLog( 0,null,new BigDecimal( noInsert ).setScale(0),"Remitos insertados" );
    		addLog( 0,null,new BigDecimal( noInsertLine ).setScale(0),"Lineas de Remitos insertadas" );
    		addLog( 0,null,new BigDecimal( noProcessed ).setScale(0),"Remitos procesados" );
    		addLog( 0,null,new BigDecimal( noDeleted ).setScale(0),"Remitos eliminados" );
    		} catch (Exception e) {
    			e.printStackTrace();
    			throw e;
    		}
    		return "";
    	}    // doIt
    	
        protected String getMsg(String msg) {
        	return Msg.translate(getCtx(), msg);
        }
        
        private boolean processInOut(MInOut inOut, String docAction) {
    		boolean processed = true;
        	if (!DocumentEngine.processAndSave(inOut, docAction, false)) {
    			String processMsg = Msg.parseTranslation(getCtx(), inOut.getProcessMsg());							
    			log.log(Level.SEVERE, "No se pudo procesar el remito nro " + inOut.getDocumentNo() + ": "+ processMsg);
    			// Se carga solo una vez el mensaje de Error al procesar el pedido
    			if (msgInOutProcessError == null) {
    				msgInOutProcessError = Msg.translate(getCtx(), "InOutProcessError");
    			}
    			DB.executeUpdate(
    				"UPDATE I_InOut SET I_ErrorMsg = I_ErrorMsg || '" + msgInOutProcessError + ": " + processMsg +"' WHERE M_InOut_ID = "+ inOut.getM_InOut_ID() + " " + clientCheck);
    			processed = false;
    		}
    		// Si no se pudo procesar porque se encontró un error, entonces se
    		// realiza la acción indicada como parámetro en caso de error
        	if(!processed && m_onActionError != null){
        		// Si se debe eliminar, se elimina
        		if(isOnActionErrorDeleteInOut()){
        			noDeleted += deleteInOut(inOut)?1:0;
        		}
    			// Si se debe dejar procesado, entonces se procesa. En el caso que
    			// tampoco pueda procesarse entonces queda sin procesar 
        		else if(docAction.equals(MInOut.DOCACTION_Complete)){
        			processed = processInOut(inOut, MInOut.DOCACTION_Prepare);
        		}
        	}
        	return processed;
        }
        
        protected boolean isOnActionErrorDeleteInOut() {
        	return "D".equals(m_onActionError);
        }
        
        protected boolean deleteInOut(MInOut inOut) {
        	int inOutID = inOut.getM_InOut_ID();
    		boolean deleted = true;
    		// Borra las referencias a las líneas y el encabezado del pedido
    		// desde la tabla de importación para poder eliminar el pedido.
        	DB.executeUpdate(
        			"UPDATE I_InOut SET M_InOut_ID = NULL, M_InOutLine_ID = NULL, I_IsImported = 'E' " +
        			"WHERE M_InOut_ID = " + inOutID);

        	// Borrado del pedido.
        	if (!inOut.delete(true)) {
        		log.severe("Cannot delete inout generated by invalid import records. InOut =" + inOut.toString());
        		deleted = false;
        	}
        	return deleted;
        }
        
} 