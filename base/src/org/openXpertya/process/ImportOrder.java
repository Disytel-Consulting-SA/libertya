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
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.X_I_Order;
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

public class ImportOrder extends SvrProcess {

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private boolean m_deleteOldImported = false;

    /** Descripción de Campos */

    private String m_docAction = MOrder.DOCACTION_Prepare;

    /** Descripción de Campos */

    private Timestamp m_DateValue = null;
    
	private String msgOrderProcessError = null;
	
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
    		boolean      error = false;

    		// ****    Prepare ****

    		// Delete Old Imported

    		if( m_deleteOldImported ) {
    			sql = new StringBuffer( "DELETE I_Order " + "WHERE I_IsImported='Y'" ).append( clientCheck );
    			no = DB.executeUpdate( sql.toString());
    			log.log(Level.SEVERE,"doIt - Delete Old Imported =" + no);
    		}
    		
    		// Set Client, IsActive, Created/Updated
    		sql = new StringBuffer( "UPDATE I_Order " + "SET AD_Client_ID = COALESCE (AD_Client_ID," ).append( m_AD_Client_ID ).append( "), IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " I_ErrorMsg = ''," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.SEVERE, "doIt - Reset=" + no );
    		
    		// ----------------------------------------------------------------------------------
    		// - Número de Documento obligatorio en todos los registros
    		// ----------------------------------------------------------------------------------		
    		sql = new StringBuffer(
    				"UPDATE I_Order i " + 
    				"SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportOrderDocumentNoMandatory") + ". ' WHERE (DocumentNo IS NULL OR trim(DocumentNo)::bpchar = ''::bpchar) AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid DocumentNo=" + no);
    			throw new Exception("@ImportOrderDocumentNoError@");
    		}

    		// ----------------------------------------------------------------------------------
    		// - Organizacion
    		// ----------------------------------------------------------------------------------		
    		sql = new StringBuffer();
    		sql.append("UPDATE i_order i ");
    		sql.append( "SET ad_org_id = "); 
    		sql.append(		"( ");
    		sql.append(			"SELECT o.ad_org_id ");
    		sql.append(			"FROM ad_org o ");
    		sql.append(			"WHERE o.value = i.orgvalue AND o.AD_Client_ID = " + m_AD_Client_ID);
    		sql.append(		") ");
    		sql.append("WHERE OrgValue IS NOT NULL AND I_IsImported<>'Y' ").append(clientCheck);		
    		no = DB.executeUpdate( sql.toString());		
    		
    		sql = new StringBuffer("UPDATE I_Order i " + "SET AD_Org_ID = NULL, I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportOrderInvalidOrgValue") + ". ' WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0) AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid Org=" + no);
    		}

    		// ----------------------------------------------------------------------------------
    		// - Tipo de Documento
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer();
    		sql.append("UPDATE i_order i ");
    		sql.append(		"SET C_DocType_ID = "); 
    		sql.append(		"( ");
    		sql.append(			"SELECT d.C_DocType_ID ");
    		sql.append(			"FROM C_DocType d ");
    		sql.append(			"WHERE d.DocTypeKey = i.doctypename AND d.AD_Client_ID = " + m_AD_Client_ID);
    		sql.append(		") ");
    		sql.append("WHERE C_DocType_ID IS NULL  AND I_IsImported<>'Y' ").append(clientCheck);		
    		no = DB.executeUpdate( sql.toString());
    		
    		sql = new StringBuffer("UPDATE I_Order " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportOrderInvalidDocType")+". ' " + "WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid DocType=" + no);
    			error = true;
    		}

    		// ----------------------------------------------------------------------------------
    		// - Seteo IsSOTrx
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer();
    		sql.append("UPDATE I_Order i ");
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
    		// - Moneda a partir de ISO CODE
    		// ----------------------------------------------------------------------------------
    		
    		// NOTA: Tener en cuenta que si la EC tiene una Tarifa asociada (se pisa la moneda)
    		sql = new StringBuffer();
    		sql.append("UPDATE i_order i ");
    		sql.append(		"SET C_Currency_ID = "); 
    		sql.append(		"( ");
    		sql.append(			"SELECT d.C_Currency_ID ");
    		sql.append(			"FROM C_Currency d ");
    		sql.append(			"WHERE d.Iso_Code = i.Iso_Code");
    		sql.append(		") ");
    		sql.append("WHERE C_Currency_ID IS NULL  AND I_IsImported<>'Y' ").append(clientCheck);		
    		no = DB.executeUpdate( sql.toString());
    		
    		// ----------------------------------------------------------------------------------
    		// - Moneda por defecto
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer();
    		sql.append("UPDATE i_Order i SET C_Currency_ID = ").append(Env.getContextAsInt(getCtx(), "$C_Currency_ID"));
    		sql.append("WHERE C_Currency_ID IS NULL AND I_IsImported<>'Y' ").append(clientCheck);		
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set Default Currency = " + no);

    		// ----------------------------------------------------------------------------------
    		// - Entidad Comercial
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer( 
    				"UPDATE I_Order o " + 
    				"SET C_BPartner_ID=" +
    					"(SELECT C_BPartner_ID FROM C_BPartner bp" + 
    					" WHERE o.BPartnerValue=bp.Value AND o.AD_Client_ID=bp.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE BPartnerValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set BP from Value=" + no);

    		sql = new StringBuffer("UPDATE I_Order " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportOrderInvalidBPartner")+". ' " + "WHERE C_BPartner_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );		
    		no = DB.executeUpdate( sql.toString());
            if( no != 0 ) {
            	log.log(Level.SEVERE,"doIt - BP Not Found=" + no);
            }
            
    		// La Dirección de la EC no se setea ya que se obtiene en MOrder según
    		// los valores seteados en la EC si existe. De todas formas se valida si
    		// la EC tiene al menos una dirección si no se marca como error
            sql = new StringBuffer("UPDATE I_Order i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportOrderInvalidBPartnerLocation")+". ' " + "WHERE C_BPartner_ID IS NOT NULL AND I_IsImported<>'Y' AND NOT EXISTS (SELECT C_Bpartner_Location_ID FROM C_BPartner_Location bpl WHERE bpl.C_BPartner_ID = i.C_BPartner_ID) " ).append( clientCheck );

    		// ----------------------------------------------------------------------------------
    		// - Tarifa
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer(
    				" UPDATE I_Order i " +
    				" SET M_Pricelist_ID = " +
    				"		(SELECT M_PriceList_ID " +
    				"        FROM M_PriceList p " +
    				"        WHERE TRIM(i.PriceList_Name) = TRIM(p.Name) AND " +
    				"              i.AD_Client_ID = p.AD_Client_ID AND " +
    				"              p.IsActive = 'Y' AND " +
    				"              ROWNUM=1) " +
    				" WHERE PriceList_Name IS NOT NULL AND " +
    				"       I_IsImported <> 'Y' ").append(clientCheck);
    		no = DB.executeUpdate( sql.toString());
    		
    		// Valor por defecto (Compra o Venta según IsSOTrx)
    		sql = new StringBuffer(
    				" UPDATE I_Order i " +
    				" SET M_Pricelist_ID = " +
    				"		(SELECT M_PriceList_ID " +
    				"        FROM M_PriceList p " +
    				"        WHERE i.IsSOTrx = p.IsSOPriceList AND " +
    				"              i.AD_Client_ID = p.AD_Client_ID AND " +
    				"              p.IsActive = 'Y' AND " +
    				"			   p.IsDefault = 'Y' " +
    				"              LIMIT 1) " +
    				" WHERE M_PriceList_ID IS NULL AND " +
    				"       I_IsImported <> 'Y' ").append(clientCheck);
    		no = DB.executeUpdate( sql.toString());
    		
    		sql = new StringBuffer( "UPDATE I_Order SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportOrderInvalidPriceList")+". ' " + "WHERE M_PriceList_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - No PriceList=" + no);
    		}			

    		// ----------------------------------------------------------------------------------
    		// - Forma de Pago y Esquema de Vencimientos
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer(
    				"UPDATE I_Order o " + 
    				"SET C_PaymentTerm_ID=" +
    					"(SELECT C_PaymentTerm_ID FROM C_PaymentTerm p" + 
    					" WHERE o.PaymentTermValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) " + 
    				"WHERE C_PaymentTerm_ID IS NULL AND PaymentTermValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set PaymentTerm=" + no);
    		
    		sql = new StringBuffer( "UPDATE I_Order " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportOrderInvalidPaymentTerm")+". ' " + "WHERE C_PaymentTerm_ID IS NULL AND PaymentTermValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid PaymentTerm=" + no);
    		}

    		// ----------------------------------------------------------------------------------
    		// - Comercial / Usuario
    		// ----------------------------------------------------------------------------------

            sql = new StringBuffer( "UPDATE I_Order i " + 
    			    "SET SalesRep_ID=(SELECT AD_User_ID " +
    			    				 "FROM AD_User u " + 
    			    				 "WHERE u.Name = i.SalesRep_Name AND u.AD_Client_ID IN (0, i.AD_Client_ID)) " + 
    			    "WHERE SalesRep_ID IS NULL AND SalesRep_Name IS NOT NULL " + 
    			    "AND I_IsImported<>'Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "Set SalesRep=" + no );

            sql = new StringBuffer( "UPDATE I_Order " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportOrerInvalidSalesRep")+". ' " + "WHERE SalesRep_ID IS NULL AND SalesRep_Name IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid SalesRep Name=" + no);
    		}

    		// ----------------------------------------------------------------------------------
    		// - Artículo
    		// ----------------------------------------------------------------------------------

    		// Desde Value
    		sql = new StringBuffer( 
    				"UPDATE I_Order o " + 
    				"SET M_Product_ID=" +
    					"(SELECT M_Product_ID FROM M_Product p" + 
    					" WHERE o.ProductValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE ProductValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set Product from Value=" + no);

    		// Desde UPC
    		sql = new StringBuffer( 
    				"UPDATE I_Order o " + 
    				"SET M_Product_ID=" +
    					"(SELECT M_Product_ID FROM M_ProductUPC p" + 
    					" WHERE o.UPC=p.UPC AND o.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE UPC IS NOT NULL AND M_Product_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set Product from Value=" + no);

    		sql = new StringBuffer( "UPDATE I_Order " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportOrderInvalidProduct")+". ' " + "WHERE M_Product_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());		

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid Product=" + no);
    		}			

    		// ----------------------------------------------------------------------------------
    		// - Impuesto
    		// ----------------------------------------------------------------------------------
    		sql = new StringBuffer( 
    				"UPDATE I_Order o " + 
    				"SET C_Tax_ID=" +
    					"(SELECT C_Tax_ID FROM C_Tax t" + 
    					" WHERE t.TaxIndicator = o.TaxIndicator AND t.IsActive = 'Y' AND o.AD_Client_ID=t.AD_Client_ID AND ROWNUM=1) " + 
    				"WHERE TaxIndicator IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		log.log(Level.FINE,"doIt - Set Tax From Indicator=" + no);
    		
    		sql = new StringBuffer( "UPDATE I_Order " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg|| '"+getMsg("ImportOrderInvalidTax")+". ' " + "WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());

    		if( no != 0 ) {
    			log.log(Level.SEVERE,"doIt - Invalid Tax=" + no);
    		}

    		// -- New Orders -----------------------------------------------------

    		int noInsert     = 0;
    		int noInsertLine = 0;
    		int noProcessed = 0;

    		// Go through Order Records w/o

    		sql = new StringBuffer( "SELECT * FROM I_Order " + "WHERE I_IsImported='N'" ).append( clientCheck ).append( " ORDER BY DocumentNo, C_Doctype_ID, C_BPartner_ID, C_BPartner_Location_ID, I_Order_ID" );

    		try {
    			PreparedStatement pstmt = DB.prepareStatement( sql.toString());
    			ResultSet         rs    = pstmt.executeQuery();

    			// Group Change

    			int    oldC_BPartner_ID          = 0;
    			int    oldC_BPartner_Location_ID = 0;
    			int	   oldDocTypeID				 = 0;
    			String oldDocumentNo             = "";
    			String lastOrderSaveError      = null;
    			boolean orderLineError         = false;
    			Timestamp today 				 = Env.getDate();
    			//

    			MOrder order = null;
    			int      lineNo  = 0;

    			while( rs.next()) {
    				X_I_Order imp           = new X_I_Order( getCtx(),rs,null );
    				String      cmpDocumentNo = imp.getDocumentNo();

    				// **************************************************************
    				// PRECONDICION: imp.getDocumentNo() no puede ser NULL ni vacío.
    				// (esto está validado al principio).
    				// **************************************************************
    				
    				// Cambia el BPartner o el Número de Documento o el tipo de documento...
    				if (oldC_BPartner_ID != imp.getC_BPartner_ID()
    							|| !oldDocumentNo.equals(cmpDocumentNo)
    							|| oldDocTypeID != imp.getC_DocType_ID()) {
    					
    					if (order != null) {

    						// Si hubo error en el guardado de alguna de las líneas
    						// entonces se elimina todo el pedido y se marcan todos
    						// los registro de importación del pedido como no
    						// importados para que el usuario pueda corregir el
    						// problema y volver a intentar importar el pedido
    						if (orderLineError) {
    							noDeleted += deleteOrder(order)?1:0;

    						// Si no hay error en las líneas y el pedido
    						// está guardada correctamente entonces solo
    						// resta procesar la acción indicada como
    						// parámetro siempre y cuando el usuario haya
    						// ingresado una acción
    						} else if (m_docAction != null && lastOrderSaveError == null && processOrder(order, m_docAction)) {
    							noProcessed++;
    						}
    					}

    					// Cambio de grupo
    					oldC_BPartner_ID          = imp.getC_BPartner_ID();
    					oldC_BPartner_Location_ID = imp.getC_BPartner_Location_ID();
    					oldDocumentNo             = imp.getDocumentNo();
    					oldDocTypeID			  = imp.getC_DocType_ID();
    					lastOrderSaveError      = null;
    					orderLineError          = false;

    					// Crea el nuevo pedido
    					order = new MOrder( getCtx(),0,null );
    					order.setClientOrg( imp.getAD_Client_ID(),imp.getAD_Org_ID());
    					order.setC_DocTypeTarget_ID( imp.getC_DocType_ID());
    					order.setIsSOTrx( imp.isSOTrx());
    					
    					if(!imp.isDocumentNoBySequence()){
    						order.setDocumentNo(imp.getDocumentNo());
    					}

    					order.setC_BPartner_ID( imp.getC_BPartner_ID());
    					order.setC_BPartner_Location_ID( imp.getC_BPartner_Location_ID());

    					if( imp.getAD_User_ID() != 0 ) {
    						order.setAD_User_ID( imp.getAD_User_ID());
    					}

    					if( imp.getDescription() != null ) {
    						order.setDescription( imp.getDescription());
    					}
    					
    					if (imp.getPaymentRule() != null) {
    						order.setPaymentRule(imp.getPaymentRule());
    					}
    					else{
    						order.setPaymentRule(MOrder.PAYMENTRULE_OnCredit);
    					}
    					// No se verifica ni chequea el estado de crédito
//    					order.setCurrentAccountVerified(true);
    					
    					order.setC_PaymentTerm_ID( imp.getC_PaymentTerm_ID());
    					order.setM_PriceList_ID( imp.getM_PriceList_ID());
    					
    					// SalesRep from Import or the person running the import

    					if( imp.getSalesRep_ID() != 0 ) {
    						order.setSalesRep_ID( imp.getSalesRep_ID());
    					}

//    					if( order.getSalesRep_ID() == 0 ) {
//    						order.setSalesRep_ID( getAD_User_ID());
//    					}

    					if( imp.getAD_OrgTrx_ID() != 0 ) {
    						order.setAD_OrgTrx_ID( imp.getAD_OrgTrx_ID());
    					}

    					if( imp.getC_Activity_ID() != 0 ) {
    						order.setC_Activity_ID( imp.getC_Activity_ID());
    					}

    					if( imp.getC_Campaign_ID() != 0 ) {
    						order.setC_Campaign_ID( imp.getC_Campaign_ID());
    					}

    					if( imp.getC_Project_ID() != 0 ) {
    						order.setC_Project_ID( imp.getC_Project_ID());
    					}

    					//
    					Timestamp dateOrdered = imp.getDateOrdered() != null ? imp
    								.getDateOrdered() : today;
    					
    					order.setDateAcct(dateOrdered);
    					order.setDateOrdered(dateOrdered);
    					order.setDatePrinted(dateOrdered);

    					if( imp.getDateAcct() != null ) {
    						order.setDateAcct( imp.getDateAcct());
    					}
    					order.setC_Currency_ID(imp.getC_Currency_ID());
    					
    					order.setDocStatus(MOrder.DOCSTATUS_Drafted);
    					order.setDocAction(MOrder.DOCACTION_Complete);
    					order.setProcessed(false);
    					order.setProcessing(false);

    					//

    					if(order.save()){
    						noInsert++;
    						lineNo = 10;

    						imp.setC_Order_ID( order.getC_Order_ID());
    					}else{
    						lastOrderSaveError = CLogger.retrieveErrorAsString();
    						imp.setI_ErrorMsg(lastOrderSaveError);
    						imp.save();
    						continue;
    					}
    				}
    				
    				// Creación de la línea del pedido
    				if(lastOrderSaveError != null) {
    					// Si hubo error al guardar el pedido se guarda el mensaje
    					// de error en cada línea ya que la corrección del error
    					// involucrará corregir alguno de los datos del
    					// encabezado que están presentes en todas las líneas.
    					imp.setI_ErrorMsg(lastOrderSaveError);
    					imp.save();
    				} else {	

    					imp.setC_Order_ID( order.getC_Order_ID());
    					// New OrderLine
    					MOrderLine line = new MOrderLine( order );

    					if( imp.getLineDescription() != null ) {
    						line.setDescription( imp.getLineDescription());
    					}

    					line.setLine( lineNo );
    					lineNo += 10;

    					if( imp.getM_Product_ID() != 0 ) {
    						line.setM_Product_ID( imp.getM_Product_ID(),true );
    					}

    					line.setQty( imp.getQtyOrdered());
    					line.setPrice();

    					BigDecimal price = imp.getPriceActual();

    					if( (price != null) && (BigDecimal.ZERO.compareTo( price ) != 0) ) {
    						line.setPrice( price );
    					}

    					if( imp.getC_Tax_ID() != 0 ) {
    						line.setC_Tax_ID( imp.getC_Tax_ID());
    					} else {
    						line.setTax();
    						imp.setC_Tax_ID( line.getC_Tax_ID());
    					}

//    					BigDecimal taxAmt = imp.getTaxAmt();
//
//    					if( (taxAmt != null) && (BigDecimal.ZERO.compareTo( taxAmt ) != 0) ) {
//    						line.setTaxAmt( taxAmt );
//    					}

    					if(line.save()){
    						noInsertLine++;
    						imp.setC_OrderLine_ID( line.getC_OrderLine_ID());
    						imp.setC_Order_ID(order.getC_Order_ID());
    						imp.setI_IsImported( true );
    						imp.setProcessed( true );
    						imp.save();
    					} else {
    						imp.setI_ErrorMsg(imp.getI_ErrorMsg() + " " + CLogger.retrieveErrorAsString());
    						imp.save();
    						orderLineError = true;
    					}
    				}
    			} // while

    			// Procesa el último pedido
    			if( m_docAction != null && order != null && lastOrderSaveError == null && processOrder(order, m_docAction)) {
    				noProcessed++;
    			}

    			rs.close();
    			pstmt.close();
    		} catch( Exception e ) {
    			log.log( Level.SEVERE,"doIt - CreateOrder",e );
    			log.log(Level.SEVERE,"");
    		}

    		// Set Error to indicator to not imported

    		sql = new StringBuffer( "UPDATE I_Order " + "SET I_IsImported='N', Updated=SysDate " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
    		no = DB.executeUpdate( sql.toString());
    		addLog( 0,null,new BigDecimal( no ),"@Errors@" );

    		//

    		addLog( 0,null,new BigDecimal( noInsert ).setScale(0),"Pedidos insertados" );
    		addLog( 0,null,new BigDecimal( noInsertLine ).setScale(0),"Lineas de Pedidos insertadas" );
    		addLog( 0,null,new BigDecimal( noProcessed ).setScale(0),"Pedidos procesados" );
    		addLog( 0,null,new BigDecimal( noDeleted ).setScale(0),"Pedidos eliminados" );
    		} catch (Exception e) {
    			e.printStackTrace();
    			throw e;
    		}
    		return "";
    	}    // doIt
    	
        protected String getMsg(String msg) {
        	return Msg.translate(getCtx(), msg);
        }
        
        private boolean processOrder(MOrder order, String docAction) {
    		boolean processed = true;
        	if (!DocumentEngine.processAndSave(order, docAction, false)) {
    			String processMsg = Msg.parseTranslation(getCtx(), order.getProcessMsg());							
    			log.log(Level.SEVERE, "No se pudo procesar el pedido nro " + order.getDocumentNo() + ": "+ processMsg);
    			// Se carga solo una vez el mensaje de Error al procesar el pedido
    			if (msgOrderProcessError == null) {
    				msgOrderProcessError = Msg.translate(getCtx(), "OrderProcessError");
    			}
    			DB.executeUpdate(
    				"UPDATE I_Order SET I_ErrorMsg = I_ErrorMsg || '" + msgOrderProcessError + ": " + processMsg +"' WHERE C_Order_ID = "+ order.getC_Order_ID() + " " + clientCheck);
    			processed = false;
    		}
    		// Si no se pudo procesar porque se encontró un error, entonces se
    		// realiza la acción indicada como parámetro en caso de error
        	if(!processed && m_onActionError != null){
        		// Si se debe eliminar, se elimina
        		if(isOnActionErrorDeleteOrder()){
        			noDeleted += deleteOrder(order)?1:0;
        		}
    			// Si se debe dejar procesado, entonces se procesa. En el caso que
    			// tampoco pueda procesarse entonces queda sin procesar 
        		else if(docAction.equals(MOrder.DOCACTION_Complete)){
        			processed = processOrder(order, MOrder.DOCACTION_Prepare);
        		}
        	}
        	return processed;
        }
        
        protected boolean isOnActionErrorDeleteOrder() {
        	return "D".equals(m_onActionError);
        }
        
        protected boolean deleteOrder(MOrder order) {
        	int orderID = order.getC_Order_ID();
    		boolean deleted = true;
    		// Borra las referencias a las líneas y el encabezado del pedido
    		// desde la tabla de importación para poder eliminar el pedido.
        	DB.executeUpdate(
        			"UPDATE I_Order SET C_Order_ID = NULL, C_OrderLine_ID = NULL, I_IsImported = 'E' " +
        			"WHERE C_Order_ID = " + orderID);

        	// Borrado del pedido.
        	if (!order.delete(true)) {
        		log.severe("Cannot delete order generated by invalid import records. Order =" + order.toString());
        		deleted = false;
        	}
        	return deleted;
        }
        
} 
