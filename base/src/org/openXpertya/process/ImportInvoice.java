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
import java.util.HashMap;
import java.util.logging.Level;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.X_I_Invoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportInvoice extends SvrProcess {

	/** Descripción de Campos */

	private int m_AD_Client_ID = 0;

	/** Descripción de Campos */

	private int m_AD_Org_ID = 0;

	/** Descripción de Campos */

	private boolean m_deleteOldImported = false;

	/** Descripción de Campos */

	private String m_docAction = null;

	/** Descripción de Campos */

	private Timestamp m_DateValue = null;
	
	private String msgInvoiceProcessError = null;
	
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
	} // prepare

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
			sql = new StringBuffer( "DELETE I_Invoice " + "WHERE I_IsImported='Y'" ).append( clientCheck );
			no = DB.executeUpdate( sql.toString());
			log.log(Level.SEVERE,"doIt - Delete Old Imported =" + no);
		}
		
		// Set Client, IsActive, Created/Updated
		sql = new StringBuffer( "UPDATE I_Invoice " + "SET AD_Client_ID = COALESCE (AD_Client_ID," ).append( m_AD_Client_ID ).append( "), IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " I_ErrorMsg = ''," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
		no = DB.executeUpdate( sql.toString());
		log.log(Level.SEVERE, "doIt - Reset=" + no );
		
		// ----------------------------------------------------------------------------------
		// - Número de Documento obligatorio en todos los registros
		// ----------------------------------------------------------------------------------		
		sql = new StringBuffer(
				"UPDATE I_Invoice i " + 
				"SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInvDocumentNoMandatory") + ". ' WHERE (DocumentNo IS NULL OR trim(DocumentNo)::bpchar = ''::bpchar) AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());

		if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - Invalid DocumentNo=" + no);
			throw new Exception("@ImportInvDocumentNoError@");
		}

		// ----------------------------------------------------------------------------------
		// - Organizacion
		// ----------------------------------------------------------------------------------		
		sql = new StringBuffer();
		sql.append("UPDATE i_invoice i ");
		sql.append( "SET ad_org_id = "); 
		sql.append(		"( ");
		sql.append(			"SELECT o.ad_org_id ");
		sql.append(			"FROM ad_org o ");
		sql.append(			"WHERE o.value = i.orgvalue AND o.AD_Client_ID = " + m_AD_Client_ID);
		sql.append(		") ");
		sql.append("WHERE OrgValue IS NOT NULL AND I_IsImported<>'Y' ").append(clientCheck);		
		no = DB.executeUpdate( sql.toString());		
		
		sql = new StringBuffer("UPDATE I_Invoice i " + "SET AD_Org_ID = NULL, I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInvInvalidOrgValue") + ". ' WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0) AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());

		if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - Invalid Org=" + no);
		}

		// ----------------------------------------------------------------------------------
		// - Tipo de Documento
		// ----------------------------------------------------------------------------------
		sql = new StringBuffer();
		sql.append("UPDATE i_invoice i ");
		sql.append(		"SET C_DocType_ID = "); 
		sql.append(		"( ");
		sql.append(			"SELECT d.C_DocType_ID ");
		sql.append(			"FROM C_DocType d ");
		sql.append(			"WHERE d.DocTypeKey = i.doctypename AND d.AD_Client_ID = " + m_AD_Client_ID);
		sql.append(		") ");
		sql.append("WHERE C_DocType_ID IS NULL  AND I_IsImported<>'Y' ").append(clientCheck);		
		no = DB.executeUpdate( sql.toString());
		
		sql = new StringBuffer("UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInvInvalidDocType")+". ' " + "WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());

		if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - Invalid DocType=" + no);
			error = true;
		}

		// ----------------------------------------------------------------------------------
		// - Seteo IsSOTrx
		// ----------------------------------------------------------------------------------
		sql = new StringBuffer();
		sql.append("UPDATE I_Invoice i ");
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
		sql.append("UPDATE i_invoice i ");
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
		sql.append("UPDATE i_invoice i SET C_Currency_ID = ").append(Env.getContextAsInt(getCtx(), "$C_Currency_ID"));
		sql.append("WHERE C_Currency_ID IS NULL AND I_IsImported<>'Y' ").append(clientCheck);		
		no = DB.executeUpdate( sql.toString());
		log.log(Level.FINE,"doIt - Set Default Currency = " + no);

		// ----------------------------------------------------------------------------------
		// - Entidad Comercial
		// ----------------------------------------------------------------------------------
		sql = new StringBuffer( 
				"UPDATE I_Invoice o " + 
				"SET C_BPartner_ID=" +
					"(SELECT C_BPartner_ID FROM C_BPartner bp" + 
					" WHERE o.BPartnerValue=bp.Value AND o.AD_Client_ID=bp.AD_Client_ID AND ROWNUM=1) " + 
				"WHERE BPartnerValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
		
		no = DB.executeUpdate( sql.toString());
		log.log(Level.FINE,"doIt - Set BP from Value=" + no);

		sql = new StringBuffer("UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInvInvalidBPartner")+". ' " + "WHERE C_BPartner_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );		
		no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
        	log.log(Level.SEVERE,"doIt - BP Not Found=" + no);
        }
        
        // También se busca por taxid
		sql = new StringBuffer( 
				"UPDATE I_Invoice o " + 
				"SET C_BPartner_ID=" +
					"(SELECT C_BPartner_ID FROM C_BPartner bp" + 
					" WHERE trim(translate(o.taxid,'-',''))=trim(translate(bp.taxid,'-','')) AND o.AD_Client_ID=bp.AD_Client_ID AND ROWNUM=1) " + 
				"WHERE taxid IS NOT NULL and c_bpartner_id is null AND I_IsImported<>'Y'" ).append( clientCheck );
		
		no = DB.executeUpdate( sql.toString());
		log.log(Level.FINE,"doIt - Set BP from Value=" + no);

		sql = new StringBuffer("UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInvInvalidBPartner")+". ' " + "WHERE C_BPartner_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );		
		no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
        	log.log(Level.SEVERE,"doIt - BP Not Found=" + no);
        }
        
        
		// La Dirección de la EC no se setea ya que se obtiene en MInvoice según
		// los valores seteados en la EC si existe. De todas formas se valida si
		// la EC tiene al menos una dirección si no se marca como error
        sql = new StringBuffer("UPDATE I_Invoice i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+ getMsg("ImportInvInvalidBPartnerLocation")+". ' " + "WHERE C_BPartner_ID IS NOT NULL AND I_IsImported<>'Y' AND NOT EXISTS (SELECT C_Bpartner_Location_ID FROM C_BPartner_Location bpl WHERE bpl.C_BPartner_ID = i.C_BPartner_ID) " ).append( clientCheck );

		// ----------------------------------------------------------------------------------
		// - Tarifa
		// ----------------------------------------------------------------------------------
		sql = new StringBuffer(
				" UPDATE I_Invoice i " +
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
				" UPDATE I_Invoice i " +
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
		
		sql = new StringBuffer( "UPDATE I_Invoice SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportInvInvalidPriceList")+". ' " + "WHERE M_PriceList_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());

		if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - No PriceList=" + no);
		}			

		// ----------------------------------------------------------------------------------
		// - Forma de Pago y Esquema de Vencimientos
		// ----------------------------------------------------------------------------------
		sql = new StringBuffer(
				"UPDATE I_Invoice o " + 
				"SET C_PaymentTerm_ID=" +
					"(SELECT C_PaymentTerm_ID FROM C_PaymentTerm p" + 
					" WHERE o.PaymentTermValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) " + 
				"WHERE C_PaymentTerm_ID IS NULL AND PaymentTermValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());
		log.log(Level.FINE,"doIt - Set PaymentTerm=" + no);
		
		sql = new StringBuffer( "UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportInvInvalidPaymentTerm")+". ' " + "WHERE C_PaymentTerm_ID IS NULL AND PaymentTermValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());

		if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - Invalid PaymentTerm=" + no);
		}

		// ----------------------------------------------------------------------------------
		// - Comercial / Usuario
		// ----------------------------------------------------------------------------------

        sql = new StringBuffer( "UPDATE I_Invoice i " + 
			    "SET SalesRep_ID=(SELECT AD_User_ID " +
			    				 "FROM AD_User u " + 
			    				 "WHERE u.Name = i.SalesRep_Name AND u.AD_Client_ID IN (0, i.AD_Client_ID)) " + 
			    "WHERE SalesRep_ID IS NULL AND SalesRep_Name IS NOT NULL " + 
			    "AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        log.fine( "Set SalesRep=" + no );

        sql = new StringBuffer( "UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportInvInvalidSalesRep")+". ' " + "WHERE SalesRep_ID IS NULL AND SalesRep_Name IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());
        if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - Invalid SalesRep Name=" + no);
		}

		// ----------------------------------------------------------------------------------
		// - Artículo
		// ----------------------------------------------------------------------------------

		// Desde Value
		sql = new StringBuffer( 
				"UPDATE I_Invoice o " + 
				"SET M_Product_ID=" +
					"(SELECT M_Product_ID FROM M_Product p" + 
					" WHERE o.ProductValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) " + 
				"WHERE ProductValue IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());
		log.log(Level.FINE,"doIt - Set Product from Value=" + no);

		// Desde UPC
		sql = new StringBuffer( 
				"UPDATE I_Invoice o " + 
				"SET M_Product_ID=" +
					"(SELECT M_Product_ID FROM M_ProductUPC p" + 
					" WHERE o.UPC=p.UPC AND o.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) " + 
				"WHERE UPC IS NOT NULL AND M_Product_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());
		log.log(Level.FINE,"doIt - Set Product from Value=" + no);

		
		/*
		//MODIFICADO - HORACIO ALVAREZ - DISYTEL - 2009-06-10 - PRODUCT IMPORT HARDCODED
		sql = new StringBuffer( "UPDATE I_Invoice o " + "SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p" + " WHERE p.value like 'IMPORT' AND o.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) " + "WHERE M_Product_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());
		log.log(Level.SEVERE,"doIt - Set Product IMPORT=" + no);
		*/
		
		sql = new StringBuffer( "UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg || '"+getMsg("ImportInvInvalidProduct")+". ' " + "WHERE M_Product_ID IS NULL AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());		

		if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - Invalid Product=" + no);
		}			

		// ----------------------------------------------------------------------------------
		// - Impuesto
		// ----------------------------------------------------------------------------------
		//sql = new StringBuffer( "UPDATE I_Invoice o " + "SET C_Tax_ID=(SELECT C_Tax_ID FROM C_Tax t" + " WHERE o.TaxIndicator=t.TaxIndicator AND o.AD_Client_ID=t.AD_Client_ID AND ROWNUM=1) " + "WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
		sql = new StringBuffer( 
				"UPDATE I_Invoice o " + 
				"SET C_Tax_ID=" +
					"(SELECT C_Tax_ID FROM C_Tax t" + 
					" WHERE t.TaxIndicator = o.TaxIndicator AND t.IsActive = 'Y' AND o.AD_Client_ID=t.AD_Client_ID AND ROWNUM=1) " + 
				"WHERE TaxIndicator IS NOT NULL AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());
		log.log(Level.FINE,"doIt - Set Tax From Indicator=" + no);
		
		sql = new StringBuffer( "UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg|| '"+getMsg("ImportInvInvalidTax")+". ' " + "WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());

		if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - Invalid Tax=" + no);
		}
		
		// ----------------------------------------------------------------------------------
		// - Cadena de Autorización
		// ----------------------------------------------------------------------------------

		sql = new StringBuffer( 
				"UPDATE I_Invoice o " + 
				"SET M_AuthorizationChain_ID=" +
					"(SELECT M_AuthorizationChain_ID FROM M_AuthorizationChain t" + 
					" WHERE t.value = o.AuthorizationChainValue AND o.AD_Client_ID=t.AD_Client_ID AND ROWNUM=1) " + 
				"WHERE M_AuthorizationChain_ID is null AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());
		log.log(Level.FINE,"doIt - Set Authorization Chain =" + no);
		
		sql = new StringBuffer( "UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg|| '"+getMsg("ImportInvInvalidAuthorizationChain")+". ' " + "WHERE M_AuthorizationChain_ID IS NULL AND AuthorizationChainValue IS NOT NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());

		if( no != 0 ) {
			log.log(Level.SEVERE,"doIt - Invalid Authorization link =" + no);
		}

		
		/* No se tiene en cuenta el caso de creación de BPartner por el momento.
		// -- New BPartner ---------------------------------------------------

		// Go through Invoice Records w/o C_BPartner_ID

		sql = new StringBuffer( "SELECT * FROM I_Invoice " + "WHERE I_IsImported<>'Y' AND C_BPartner_ID IS NULL" ).append( clientCheck ).append(" ORDER BY BPartnerValue ");
		//sql = new StringBuffer( "SELECT BPartnerValue FROM I_Invoice " + "WHERE I_IsImported='N' AND C_BPartner_ID IS NULL" ).append( clientCheck ).append(" GROUP BY  BPartnerValue ");

		try {
			PreparedStatement pstmt = DB.prepareStatement( sql.toString());
			ResultSet         rs    = pstmt.executeQuery();

			while( rs.next()) {
				X_I_Invoice imp = new X_I_Invoice( getCtx(),rs,null );
				

				if( imp.getBPartnerValue() == null ) {
					if( imp.getEMail() != null ) {
						imp.setBPartnerValue( imp.getEMail());
					} else if( imp.getName() != null ) {
						imp.setBPartnerValue( imp.getName());
					} else {
						continue;
					}
				}

				if( imp.getName() == null ) {
					if( imp.getContactName() != null ) {
						imp.setName( imp.getContactName());
					} else {
						imp.setName( imp.getBPartnerValue());
					}
				}

				// BPartner

				MBPartner bp = MBPartner.get( getCtx(),imp.getBPartnerValue());

				if( bp == null ) {
					bp = new MBPartner( getCtx(),-1,null );
					bp.setClientOrg( imp.getAD_Client_ID(),imp.getAD_Org_ID());
					bp.setValue( imp.getBPartnerValue());
					bp.setName( imp.getName());
					bp.setIsCustomer(imp.isSOTrx());
					bp.setIsVendor(!imp.isSOTrx());
					
					if(bp.isCustomer()){
						bp.setclient_type(MBPartner.CLIENT_TYPE_ClienteDeMayoreo);
					}
					bp.setC_BP_Group_ID(MBPGroup.getDefault(m_AD_Client_ID));

					if( !bp.save()) {
						continue;
					}
				}

				imp.setC_BPartner_ID( bp.getC_BPartner_ID());

				// BP Location

				MBPartnerLocation   bpl  = null;
				MBPartnerLocation[] bpls = bp.getLocations( true );

				for( int i = 0;(bpl == null) && (i < bpls.length);i++ ) {
					imp.setC_BPartner_Location_ID(bpls[i].getC_BPartner_Location_ID());
					imp.setC_Location_ID(bpls[i].getC_Location_ID());
					bpl = bpls[i];
//					if( imp.getC_BPartner_Location_ID() == bpls[ i ].getC_BPartner_Location_ID()) {
//						bpl = bpls[ i ];
//
//						// Same Location ID
//
//					} else if( imp.getC_Location_ID() == bpls[ i ].getC_Location_ID()) {
//						bpl = bpls[ i ];
//
//						// Same Location Info
//
//					} else if( imp.getC_Location_ID() == 0 ) {
//						MLocation loc = bpl.getLocation( false );
//
//						if( loc.equals( imp.getC_Country_ID(),imp.getC_Region_ID(),imp.getPostal(),"",imp.getCity(),imp.getAddress1(),imp.getAddress2())) {
//							bpl = bpls[ i ];
//						}
//					}
				}

				if( bpl == null ) {

					// New Location

					MLocation loc = new MLocation( getCtx(),0,null );

					loc.setAddress1( imp.getAddress1());
					loc.setAddress2( imp.getAddress2());
					loc.setCity( imp.getCity());
					loc.setPostal( imp.getPostal());

					if( imp.getC_Region_ID() != 0 ) {
						loc.setC_Region_ID( imp.getC_Region_ID());
					}

					loc.setC_Country_ID( imp.getC_Country_ID());

					if( !loc.save()) {
						continue;
					}

					//

					bpl = new MBPartnerLocation( bp );
					bpl.setC_Location_ID( loc.getC_Location_ID());

					if( !bpl.save()) {
						continue;
					}
				}

				imp.setC_Location_ID( bpl.getC_Location_ID());
				imp.setC_BPartner_Location_ID( bpl.getC_BPartner_Location_ID());

				// User/Contact

				if( (imp.getContactName() != null) || (imp.getEMail() != null) || (imp.getPhone() != null) ) {
					MUser[] users = bp.getContacts( true );
					MUser   user  = null;

					for( int i = 0;(user == null) && (i < users.length);i++ ) {
						String name = users[ i ].getName();

						if( name.equals( imp.getContactName()) || name.equals( imp.getName())) {
							user = users[ i ];
							imp.setAD_User_ID( user.getAD_User_ID());
						}
					}

					if( user == null ) {
						user = new MUser( bp );

						if( imp.getContactName() == null ) {
							user.setName( imp.getName());
						} else {
							user.setName( imp.getContactName());
						}

						user.setEMail( imp.getEMail());
						user.setPhone( imp.getPhone());

						if( user.save()) {
							imp.setAD_User_ID( user.getAD_User_ID());
						}
					}
				}

				imp.save();
			}    // for all new BPartners

			rs.close();
			pstmt.close();

			//

		} catch( SQLException e ) {
			log.log( Level.SEVERE,"doIt - CreateBP",e );
		}

		sql = new StringBuffer( "UPDATE I_Invoice " + "SET I_IsImported='E', I_ErrorMsg='No se encontró el proveedor o la localizacion' " + "WHERE ( C_BPartner_ID IS NULL OR C_BPartner_Location_ID IS NULL ) " + " AND I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());

		if( no != 0 ) {
			log.log( Level.SEVERE,"doIt - No BPartner OR BPLocation OR Location=" + no );
		} */

		// -- New Invoices -----------------------------------------------------

		int noInsert     = 0;
		int noInsertLine = 0;
		int noProcessed = 0;

		// Go through Invoice Records w/o

		sql = new StringBuffer( "SELECT * FROM I_Invoice " + "WHERE I_IsImported='N'" ).append( clientCheck ).append( " ORDER BY DocumentNo, C_Doctype_ID, C_BPartner_ID, C_BPartner_Location_ID, I_Invoice_ID" );

		try {
			PreparedStatement pstmt = DB.prepareStatement( sql.toString());
			ResultSet         rs    = pstmt.executeQuery();

			// Group Change

			int    oldC_BPartner_ID          = 0;
			int    oldC_BPartner_Location_ID = 0;
			int	   oldDocTypeID				 = 0;
			String oldDocumentNo             = "";
			String lastInvoiceSaveError      = null;
			boolean invoiceLineError         = false;
			Timestamp today 				 = Env.getDate();
			//

			MInvoice invoice = null;
			int      lineNo  = 0;

			while( rs.next()) {
				X_I_Invoice imp           = new X_I_Invoice( getCtx(),rs,null );
				String      cmpDocumentNo = imp.getDocumentNo();

				// **************************************************************
				// PRECONDICION: imp.getDocumentNo() no puede ser NULL ni vacío.
				// (esto está validado al principio).
				// **************************************************************
				
				// Cambia el BPartner o el Número de Documento o el tipo de documento...
				if (oldC_BPartner_ID != imp.getC_BPartner_ID()
							|| !oldDocumentNo.equals(cmpDocumentNo)
							|| oldDocTypeID != imp.getC_DocType_ID()) {
					
					if (invoice != null) {

						// Si hubo error en el guardado de alguna de las líneas
						// entonces se elimina toda la factura y se marcan todos
						// los registro de importación de la factura como no
						// importados para que el usuario pueda corregir el
						// problema y volver a intentar importar la factura
						if (invoiceLineError) {
							noDeleted += deleteInvoice(invoice)?1:0;

						// Si no hay error en las líneas y lafactura
						// está guardada correctamente entonces solo
						// resta procesar la acción indicada como
						// parámetro siempre y cuando el usuario haya
						// ingresado una acción
						} else if (m_docAction != null && lastInvoiceSaveError == null && processInvoice(invoice, m_docAction)) {
							noProcessed++;
						}
					}

					// Cambio de grupo
					oldC_BPartner_ID          = imp.getC_BPartner_ID();
					oldC_BPartner_Location_ID = imp.getC_BPartner_Location_ID();
					oldDocumentNo             = imp.getDocumentNo();
					oldDocTypeID			  = imp.getC_DocType_ID();
					lastInvoiceSaveError      = null;
					invoiceLineError          = false;

					// Crea la nueva factura
					invoice = new MInvoice( getCtx(),0,null );
					invoice.setClientOrg( imp.getAD_Client_ID(),imp.getAD_Org_ID());
					invoice.setC_DocTypeTarget_ID( imp.getC_DocType_ID());
					invoice.setIsSOTrx( imp.isSOTrx());
					
					if(!imp.isDocumentNoBySequence()){
						invoice.setManualDocumentNo(true);
						invoice.setDocumentNo(imp.getDocumentNo());
					}
					
					//

					invoice.setC_BPartner_ID( imp.getC_BPartner_ID());
					invoice.setC_BPartner_Location_ID( imp.getC_BPartner_Location_ID());

					if( imp.getAD_User_ID() != 0 ) {
						invoice.setAD_User_ID( imp.getAD_User_ID());
					}

					//

					if( imp.getDescription() != null ) {
						invoice.setDescription( imp.getDescription());
					}
					
					if (imp.getPaymentRule() != null) {
						invoice.setPaymentRule(imp.getPaymentRule());
					}
					else{
						invoice.setPaymentRule(MInvoice.PAYMENTRULE_OnCredit);
					}
					// No se verifica ni chequea el estado de crédito
					invoice.setCurrentAccountVerified(true);
					
					invoice.setC_PaymentTerm_ID( imp.getC_PaymentTerm_ID());
					invoice.setM_PriceList_ID( imp.getM_PriceList_ID());

					invoice.setCreateCashLine(imp.isCreateCashLine());
					
					// SalesRep from Import or the person running the import

					if( imp.getSalesRep_ID() != 0 ) {
						invoice.setSalesRep_ID( imp.getSalesRep_ID());
					}

//					if( invoice.getSalesRep_ID() == 0 ) {
//						invoice.setSalesRep_ID( getAD_User_ID());
//					}

					//

					if( imp.getAD_OrgTrx_ID() != 0 ) {
						invoice.setAD_OrgTrx_ID( imp.getAD_OrgTrx_ID());
					}

					if( imp.getC_Activity_ID() != 0 ) {
						invoice.setC_Activity_ID( imp.getC_Activity_ID());
					}

					if( imp.getC_Campaign_ID() != 0 ) {
						invoice.setC_Campaign_ID( imp.getC_Campaign_ID());
					}

					if( imp.getC_Project_ID() != 0 ) {
						invoice.setC_Project_ID( imp.getC_Project_ID());
					}
					
					if( imp.getM_AuthorizationChain_ID() != 0 ) {
						invoice.setM_AuthorizationChain_ID(imp.getM_AuthorizationChain_ID());
					}
					
					if(!Util.isEmpty(imp.getAuthorizationChainStatus(), true)){
						invoice.setAuthorizationChainStatus(imp.getAuthorizationChainStatus());
					}

					//
					Timestamp dateInvoiced = imp.getDateInvoiced() != null ? imp
								.getDateInvoiced() : today;
					
					invoice.setDateInvoiced(dateInvoiced);
					invoice.setDateAcct(dateInvoiced);
					invoice.setDateOrdered(dateInvoiced);
					invoice.setDatePrinted(dateInvoiced);

					if( imp.getDateAcct() != null ) {
						invoice.setDateAcct( imp.getDateAcct());
					}
					invoice.setC_Currency_ID(imp.getC_Currency_ID());
					
					invoice.setDocStatus(MInvoice.DOCSTATUS_Drafted);
					invoice.setDocAction(MInvoice.DOCACTION_Complete);
					invoice.setProcessed(false);
					invoice.setProcessing(false);

					// Impreso fiscalmente
					if(invoice.requireFiscalPrint()){
						invoice.setFiscalAlreadyPrinted(imp.isPrinted());
					}
					else{
						invoice.setIsPrinted(imp.isPrinted());
					}		
					
					invoice.setSkipLastFiscalDocumentNoValidation(true);
					
					// Divido el nro de documento en letra, pto de venta y
					// nro de comprobante si es que es posible
					if(CalloutInvoiceExt.ComprobantesFiscalesActivos()){
						HashMap<String, Object> div = CalloutInvoiceExt.DividirDocumentNo(imp.getAD_Client_ID(), imp.getDocumentNo());
						// Letra
						if(!Util.isEmpty((Integer)div.get("C_Letra_Comprobante_ID"), true)){
							invoice.setC_Letra_Comprobante_ID((Integer)div.get("C_Letra_Comprobante_ID"));
						}
						// Punto de venta
						if(!Util.isEmpty((Integer)div.get("PuntoDeVenta"), true)){
							invoice.setPuntoDeVenta((Integer)div.get("PuntoDeVenta"));
						}
						// Nro de comprobante
						if(!Util.isEmpty((Integer)div.get("NumeroComprobante"), true)){
							invoice.setNumeroComprobante((Integer)div.get("NumeroComprobante"));
						}
					}

					if(invoice.save()){
						noInsert++;
						lineNo = 10;

						imp.setC_Invoice_ID( invoice.getC_Invoice_ID());
					}else{
						lastInvoiceSaveError = CLogger.retrieveErrorAsString();
						imp.setI_ErrorMsg(lastInvoiceSaveError);
						imp.save();
						continue;
					}
				}
				
				// Creación de la línea de factura
				if(lastInvoiceSaveError != null) {
					// Si hubo error al guardar factura se guarda el mensaje
					// de error en cada línea ya que la corrección del error
					// invloucrará corregir alguno de los datos del
					// encabezado que están presentes en todas las líneas.
					imp.setI_ErrorMsg(lastInvoiceSaveError);
					imp.save();
				} else {	

					imp.setC_Invoice_ID( invoice.getC_Invoice_ID());
					// New InvoiceLine
					MInvoiceLine line = new MInvoiceLine( invoice );

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

					BigDecimal taxAmt = imp.getTaxAmt();

					if( (taxAmt != null) && (BigDecimal.ZERO.compareTo( taxAmt ) != 0) ) {
						line.setTaxAmt( taxAmt );
					}

					if(line.save()){
						noInsertLine++;
						imp.setC_InvoiceLine_ID( line.getC_InvoiceLine_ID());
						imp.setC_Invoice_ID(invoice.getC_Invoice_ID());
						imp.setI_IsImported( true );
						imp.setProcessed( true );
						imp.save();
					} else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg() + " " + CLogger.retrieveErrorAsString());
						imp.save();
						invoiceLineError = true;
					}
				}
			} // while

			// Procesa la última factura
			if( m_docAction != null && invoice != null && lastInvoiceSaveError == null && processInvoice(invoice, m_docAction)) {
				noProcessed++;
			}

			rs.close();
			pstmt.close();
		} catch( Exception e ) {
			log.log( Level.SEVERE,"doIt - CreateInvoice",e );
			log.log(Level.SEVERE,"");
		}

		// Set Error to indicator to not imported

		sql = new StringBuffer( "UPDATE I_Invoice " + "SET I_IsImported='N', Updated=SysDate " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
		no = DB.executeUpdate( sql.toString());
		addLog( 0,null,new BigDecimal( no ),"@Errors@" );

		//

		addLog( 0,null,new BigDecimal( noInsert ).setScale(0),"Facturas insertadas" );
		addLog( 0,null,new BigDecimal( noInsertLine ).setScale(0),"Lineas de Facturas insertadas" );
		addLog( 0,null,new BigDecimal( noProcessed ).setScale(0),"Facturas procesadas" );
		addLog( 0,null,new BigDecimal( noDeleted ).setScale(0),"Facturas eliminadas" );
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}    // doIt
	
    protected String getMsg(String msg) {
    	return Msg.translate(getCtx(), msg);
    }
    
    private boolean processInvoice(MInvoice invoice, String docAction) {
		boolean processed = true;
    	if (!DocumentEngine.processAndSave(invoice, docAction, false)) {
			String processMsg = Msg.parseTranslation(getCtx(), invoice.getProcessMsg());							
			log.log(Level.SEVERE, "No se pudo procesar la factura nro " + invoice.getDocumentNo() + ": "+ processMsg);
			// Se carga solo una vez el mensaje de Error al procesar la factura
			if (msgInvoiceProcessError == null) {
				msgInvoiceProcessError = Msg.translate(getCtx(), "InvoiceProcessError");
			}
			DB.executeUpdate(
				"UPDATE I_Invoice SET I_ErrorMsg = I_ErrorMsg || '" + msgInvoiceProcessError + ": " + processMsg +"' WHERE C_Invoice_ID = "+ invoice.getC_Invoice_ID() + " " + clientCheck);
			processed = false;
		}
		// Si no se pudo procesar porque se encontró un error, entonces se
		// realiza la acción indicada como parámetro en caso de error
    	if(!processed && m_onActionError != null){
    		// Si se debe eliminar, se elimina
    		if(isOnActionErrorDeleteInvoice()){
    			noDeleted += deleteInvoice(invoice)?1:0;
    		}
			// Si se debe dejar procesado, entonces se procesa. En el caso que
			// tampoco pueda procesarse entonces queda sin procesar 
    		else if(docAction.equals(MInvoice.DOCACTION_Complete)){
    			processed = processInvoice(invoice, MInvoice.DOCACTION_Prepare);
    		}
    	}
    	return processed;
    }
    
    protected boolean isOnActionErrorDeleteInvoice() {
    	return "D".equals(m_onActionError);
    }
    
    protected boolean deleteInvoice(MInvoice invoice) {
    	int invoiceID = invoice.getC_Invoice_ID();
		boolean deleted = true;
		// Borra las referencias a las líneas y el encabezado de la factura
		// desde la tabla de importación para poder eliminar la factura.
    	DB.executeUpdate(
    			"UPDATE I_Invoice SET C_Invoice_ID = NULL, C_InvoiceLine_ID = NULL, I_IsImported = 'E' " +
    			"WHERE C_Invoice_ID = " + invoiceID);

    	// Borrado de la factura.
    	if (!invoice.delete(true)) {
    		log.severe("Cannot delete invoice generated by invalid import records. Invoice =" + invoice.toString());
    		deleted = false;
    	}
    	return deleted;
    }
}    // ImportInvoice



/*
 *  @(#)ImportInvoice.java   02.07.07
 * 
 *  Fin del fichero ImportInvoice.java
 *  
 *  Versión 2.1
 *
 */