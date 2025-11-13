package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MLetraComprobante;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_M_PriceList;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class CintoloExchangeDifOnDeferredChecks extends SvrProcess {
	private BigDecimal days;

	/** Locale AR activo? */
	public final boolean LOCALE_AR_ACTIVE = CalloutInvoiceExt.ComprobantesFiscalesActivos();

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			log.fine("prepare - " + para[i]);

			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else if (name.equalsIgnoreCase("Days")) {
				days = (BigDecimal) para[i].getParameter();
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		ArrayList<ChequeEnCartera> chequesND = new ArrayList<ChequeEnCartera>();
		ArrayList<ChequeEnCartera> chequesNC = new ArrayList<ChequeEnCartera>();
        
		LocalDate date = LocalDate.now().minusDays(days.intValue());
        String formatedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
        String sql = 
        		" SELECT " +
    				" q.c_payment_id, " +
    				" q.c_bpartner_id, " +
	        		" currencyConvert(abs(q.usd_al_recibir - q.usd_al_cobrar),100, q.c_currency_id, q.duedate,null,q.ad_client_id,q.ad_org_id) as ars_exchange_dif, " +
	        		" q.usd_al_recibir, " +
	        		" q.usd_al_cobrar, " +
	        		" q.trx_rate, " +
	        		" q.due_rate " +
	        		" FROM ( " +
	        		" SELECT " + 
	        		" 		p.ad_client_id, " + 
	        		" 		p.ad_org_id, " + 
	        		" 		p.checkno, " + 
	        		" 		p.c_payment_id, " + 
	        		" 		bp.c_bpartner_id, " + 
	        		" 		p.datetrx, " + 
	        		" 		p.duedate,  " + 
	        		" 		p.c_currency_id, " + 
	        		" 		currencyConvert(p.payamt,p.c_currency_id, 100, p.datetrx,null, p.ad_client_id, p.ad_org_id) as usd_al_recibir, " + 
	        		" 		currencyConvert(p.payamt, p.c_currency_id, 100, p.duedate,null, p.ad_client_id, p.ad_org_id) as usd_al_cobrar, " + 
	        		" 		currencyRate(100, p.c_currency_id, p.datetrx,null, p.ad_client_id , p.ad_org_id) as trx_rate, " + 
	        		" 		currencyRate(100, p.c_currency_id, p.duedate,null, p.ad_client_id , p.ad_org_id) as due_rate, " + 
	        		" 		p.cintolo_ref_invoiceline_id  " + 
	        		" 	FROM c_payment p " + 
	        		" 	JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id " + 
	        		" 	JOIN c_bankaccount b on b.c_bankaccount_id = p.c_bankaccount_id " + 
	        		" 	JOIN c_bpartner bp on bp.c_bpartner_id =  p.c_bpartner_id	 " + 
	        		" 	WHERE p.isreceipt = 'Y' " + 
	        		" 		AND p.duedate::date = '" + formatedDate + "' " + 
	        		" 		AND p.tendertype = 'K' " + 
	        		" 		AND p.payamt > 0 " + 
	        		" 		AND p.docstatus IN ('CO','CL') " + 
	        		" 		AND p.trxtype <> 'X' " + 
	        		" 		AND dt.doctypekey != 'DRC' " + 
	        		" 		AND (p.checkstatus IS NULL OR p.checkstatus != 'R') " + 
	        		" 		AND (p.m_boletadeposito_id IS NULL OR  " + 
	        		"   			(p.m_boletadeposito_id IS NOT NULL AND " + 
		        	"   				NOT EXISTS ( " + 
		        	"   				SELECT b.m_boletadeposito_id " + 
		        	"   				FROM m_boletadeposito b " + 
		        	"   				WHERE p.m_boletadeposito_id = b.m_boletadeposito_id " + 
		        	"   			  	AND b.fechadeposito::date <= '" + formatedDate + "' " +
		        	"   			  	AND b.docstatus IN ('CO','CL') " + 
		        	" 					) " + 
		        	" 	  			) " + 
	        		"   		) " + 
	        		"   	AND NOT EXISTS ( " + 
	        		"   		SELECT * " + 
	        		"   		FROM m_boletadeposito b " + 
	        		"   		JOIN m_boletadepositoline bl ON bl.m_boletadeposito_id = b.m_boletadeposito_id " + 
	        		"   		WHERE (bl.c_depo_payment_id = p.c_payment_id OR c_reverse_payment_id = p.c_payment_id OR bl.c_payment_id = p.c_payment_id) " + 
	        		"     			AND b.fechadeposito::date <= '" + formatedDate + "' " + 
	        		" 	  			AND b.docstatus IN ('CO','CL') " + 
	        		"   		) " + 
	        		"  		AND NOT EXISTS (  " + 
	        		"  			SELECT DISTINCT pp.c_payment_id " + 
	        		"   			FROM c_payment pp  " + 
	        		" 			JOIN c_allocationline al ON al.c_payment_id = pp.c_payment_id " + 
	        		" 			JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id  " + 
	        		" 			WHERE pp.original_ref_payment_id = p.c_payment_id " + 
	        		" 		  		AND pp.docstatus IN ('CO','CL')  " + 
	        		" 		  		AND ah.datetrx::date <= '" + formatedDate + "' " +
	        		" 		  		AND ah.docstatus IN ('CO','CL') " + 
	        		"   		) " + 
	        		"	ORDER BY p.c_bpartner_id, p.cintolo_ref_invoiceline_id, p.datetrx  " + 
	        		" ) q ";
        
	        	/*	
        		" FROM ( " +
		    		" SELECT " +
			    		" cp.ad_client_id, " +
			    		" cp.ad_org_id, " +
			    		" cp.checkno, " +
			    		" cp.c_payment_id, " +
			    		" bp.c_bpartner_id, " +
			    		" cp.datetrx, " +
			    		" cp.duedate,  " +
			    		" cp.c_currency_id, " +
			    		" currencyConvert(cp.payamt,cp.c_currency_id, 100, cp.datetrx,null,cp.ad_client_id,cp.ad_org_id) as usd_al_recibir, " +
			    		" currencyConvert(cp.payamt, cp.c_currency_id, 100, cp.duedate,null,cp.ad_client_id,cp.ad_org_id) as usd_al_cobrar, " +
			    		" currencyRate(100,cp.c_currency_id,cp.datetrx,null,cp.ad_client_id ,cp.ad_org_id) as trx_rate, " +
			    		" currencyRate(100,cp.c_currency_id,cp.duedate,null,cp.ad_client_id ,cp.ad_org_id) as due_rate " +
		    		" FROM c_payment cp " +
		    		" JOIN c_bankaccount cb on cb.c_bankaccount_id = cp.c_bankaccount_id " +
		    		" JOIN c_bank b on b.c_bank_id = cb.c_bank_id " +
		    		" JOIN c_bpartner bp on bp.c_bpartner_id = cp.c_bpartner_id " +
		    		" WHERE  cp.isreceipt = 'Y' " +
			    		" AND cp.tendertype ='K' " +
			    		" AND NOT cp.checkno ISNULL " +
			    		" AND cp.checkstatus ISNULL " +
			    		" AND cp.docstatus IN ('CO','CL') " +
			    		" AND cp.duedate::date = '" + formatedDate + "' " +
		    		" ORDER BY cp.c_bpartner_id, cp.cintolo_ref_invoiceline_id, cp.datetrx " + 
		    		" ) q ";
		    */
		PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	
    	try {
    		pstmt = DB.prepareStatement(sql);
    		rs = pstmt.executeQuery();
    		while(rs.next()) {
    			int paymentID = rs.getInt("c_payment_id");
    			int bPartnerID = rs.getInt("c_bpartner_id");
    			BigDecimal arsExchangeDif = rs.getBigDecimal("ars_exchange_dif");
    			BigDecimal usdAlRecibir = rs.getBigDecimal("usd_al_recibir");
    			BigDecimal usdAlCobrar = rs.getBigDecimal("usd_al_cobrar");
    			BigDecimal trxRate = rs.getBigDecimal("trx_rate");
    			BigDecimal dueRate = rs.getBigDecimal("due_rate");
    			
    			ChequeEnCartera cheq = new ChequeEnCartera(
    					new MPayment(getCtx(), paymentID, null), 
    					new MBPartner(getCtx(), bPartnerID, null), 
    					arsExchangeDif, usdAlRecibir, usdAlCobrar, trxRate, dueRate);
    			
    			// Carga la lista de cheques en cartera para NC / ND
    			if(usdAlRecibir.floatValue() > usdAlCobrar.floatValue()) {
    				chequesND.add(cheq);
    			} else if (usdAlRecibir.floatValue() < usdAlCobrar.floatValue()) {
					chequesNC.add(cheq);    					
				}
    		}

    	} catch( Exception e ) {
    		log.log( Level.SEVERE,sql.toString(),e );
    	} finally {
    		try {
	    		if (rs != null) rs.close();
	    		if (pstmt != null) pstmt.close();
    		}	catch (Exception e) {}
    	}
    	
    	String response = chequesNC.size() > 0 ? generateDocuments(chequesNC, true, date): "No hay cheques para generar Creditos.";
    	response += "<br>";
    	response += chequesND.size() > 0 ? generateDocuments(chequesND, false, date) : "No hay cheques para generar Debitos.";
		return response;
	}

	
	private String generateDocuments(ArrayList<ChequeEnCartera> cheques, boolean isCredit, LocalDate date) throws Exception {
		String formatedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String response = "";
		MBPartner bPartner = null; // Para corte de control
		int invoiceID = 0;				
		
		/**
		 * Si el check Acumular diferencias de cambio en el cliente no está activo, 
		 * debe generar una nueva propuesta de diff. de cambio según configuración, 
		 * si está activo debe verificar primero si hay una ND o NC (según corresponda) previa en borrador. 
		 * En caso afirmativo, agrega una línea a la existente, en caso negativo, genera una nueva como se mencionó anteriormente. 
		 * Para los cheques que ya calculó diferencia de cambio no debe volver a hacerlo
		 * 
		 * dREHER sep 24
		 */
		
		for(ChequeEnCartera cheq : cheques) {			
			
			boolean skipHeader = false;
			int invoiceLineID = 0;
			
			//Si el cheque ya está referenciado, actualizo solo la línea
			if(cheq.getPayment().get_Value("CINTOLO_Ref_Invoiceline_ID") != null &&
				((Integer) cheq.getPayment().get_Value("CINTOLO_Ref_Invoiceline_ID") > 0)) {
				
				/* dREHER sep 24 - si ya esta procesado el cheque NO hacer nada
				skipHeader = true;
				
				invoiceLineID = (Integer) cheq.getPayment().get_Value("CINTOLO_Ref_Invoiceline_ID");
				MInvoiceLine il = new MInvoiceLine(getCtx(),invoiceLineID,null);
				
				MInvoice i = new MInvoice(getCtx(), il.getC_Invoice_ID(), null);
				invoiceID = i.getC_Invoice_ID();
				
				if(!i.getDocStatus().equals("DR")) {
					// EL DOC SE ENCUENTRA COMPLETO. NO SE MODIFICA LA LÍNEA.
					response += "Documento <b>" + i.getDocumentNo() 
							+ " </b> en estado completo. Cheque <b>" + cheq.getPayment().getCheckNo() + "</b> anteriormente actualizado <br>";
					continue;
				}else
					response += "El cheque " + cheq.getNroCheque() + " ya esta relacionado a la factura: " 
							+ "<b> " + i.getDocumentNo() + "</b> " 
							+ ", por lo tanto se actualiza la linea correspondiente! <br>";
				*/
				invoiceLineID = (Integer) cheq.getPayment().get_Value("CINTOLO_Ref_Invoiceline_ID");
				MInvoiceLine il = new MInvoiceLine(getCtx(),invoiceLineID,null);
				MInvoice i = new MInvoice(getCtx(), il.getC_Invoice_ID(), null);
				
				response += "Documento <b>" + i.getDocumentNo() 
				+ " </b> en estado completo. Cheque <b>" + cheq.getPayment().getCheckNo() + "</b> anteriormente actualizado <br>";
				continue;
			}
			
			if(!skipHeader && 
					(bPartner == null || cheq.getbPartner().getC_BPartner_ID() != bPartner.getC_BPartner_ID())) {					
				
				invoiceID = 0; // De primeras, hay que crear una NC/ND nueva
				
				// Si la EC tiene activado el check "Acumular diferencia de Cambio", busco si existe alguna
				if(invoiceID == 0 && cheq.getbPartner().isCintolo_Acumulate_Exchange_Dif()) {
					int oldInvoiceID = DB.getSQLValue(null,
							" SELECT i.c_invoice_id "
							+ " FROM c_invoice i "
							+ " JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id "
							+ " WHERE i.issotrx = 'Y' "
							+ " AND i.cintolo_apply_exchange_dif = 'Y' "
							+ " AND i.docstatus IN ('DR') " // dREHER sep 24 solo buscar dif de cambio en borrador, NO completas
							+ " AND dt.docbasetype = " + (isCredit ? "'ARC'" : "'ARI'") + " "
							+ " AND i.c_bpartner_id = " + cheq.getbPartner().getC_BPartner_ID() + " "
							+ " ORDER BY i.created DESC "
							+ " LIMIT 1");
					invoiceID = oldInvoiceID < 0 ? 0 : oldInvoiceID;
				}
				
				MInvoice i = new MInvoice(getCtx(), invoiceID, get_TrxName());
				
				// Si la NC/ND es nueva, se deben settear los campos
				if(invoiceID == 0) {
					i.setIsSOTrx(true);
					i.setDateInvoiced(new Timestamp((new Date()).getTime()));
					i.setDateAcct(new Timestamp((new Date()).getTime()));
					i.set_Value("Cintolo_Apply_Exchange_Dif", true);
					i.setManualGeneralDiscount(Env.ZERO);
					String sql = " SELECT " + (isCredit ? "credit_pricelist ": "debit_pricelist") + 
							" FROM C_Cintolo_Exchange_Dif_Settings WHERE isActive='Y' ORDER BY created DESC LIMIT 1"; 
					int priceListID = DB.getSQLValue(null, sql);
					if(priceListID > 0) {
						i.setM_PriceList_ID(priceListID);
					}else {
						priceListID = getDebitCreditExchangeDiffPriceList(isCredit);
						i.setM_PriceList_ID(priceListID);
					}
					
					// Parámetros para la determinación del DOCTYPE
					i.setC_DocTypeTarget_ID(0);
					i.setTipoComprobante(isCredit? "NC" : "ND");
					i.setCreateCashLine(false);
					
					i.setDocAction(MInvoice.DOCACTION_Complete);
					i.setDocStatus(MInvoice.DOCSTATUS_Drafted);
					// Seteo el bypass de la factura para que no chequee el saldo del
					// cliente porque ya lo chequea el tpv
					i.setCurrentAccountVerified(true);
					// Seteo el bypass para que no actualice el crédito del cliente ya
					// que se realiza luego al finalizar las operaciones
					i.setUpdateBPBalance(false);
					
					// dREHER primero buscar el punto de venta desde la config particular del cliente, sino encuentra desde config general
					int ptoVenta = (cheq.getbPartner().get_Value("Cintolo_Point_Of_Sale")!=null? 
							(Integer)cheq.getbPartner().get_Value("Cintolo_Point_Of_Sale")
							:0);
					
					if(ptoVenta <=0)
						ptoVenta = DB.getSQLValue(null, 
							" SELECT point_of_sale FROM C_Cintolo_Exchange_Dif_Settings WHERE isactive='Y' ORDER BY created DESC LIMIT 1");
					
					
					i.setPuntoDeVenta(ptoVenta > 0 ? ptoVenta: 1);						
					i.setC_Letra_Comprobante_ID(DB.getSQLValue(null, 
							"SELECT c_letra_comprobante_id FROM c_letra_comprobante WHERE letra = 'A'"));
					i.setBPartner(cheq.getbPartner());
					
					
					i = setDocType(i, cheq.getbPartner(), isCredit, ptoVenta);
					
					debug("Tipo de comprobante: " + i.getC_DocTypeTarget_ID());
					
					boolean isFiscal = i.getC_DocTypeTarget_ID() > 0 ? (new MDocType(Env.getCtx(), i.getC_DocTypeTarget_ID(), null)).isFiscal(): false; 
					if(isFiscal) {
						i.set_Value("LYEIPeriodFrom", i.getDateAcct());
						i.set_Value("LYEIPeriodTo", i.getDateAcct());
					}
					
					if(i.getC_DocTypeTarget_ID() <= 0) {
						if(!isCredit) {
							int docTypeID = DB.getSQLValue(null, "SELECT C_DocType_ID FROM C_Cintolo_Exchange_Dif_Settings ORDER BY created DESC LIMIT 1");
							i.setC_DocTypeTarget_ID(docTypeID);
						}
					}
				
					if(LOCALE_AR_ACTIVE){
						String cuit = cheq.getbPartner().getTaxID();
						i.setCUIT(cuit);
						i.setApplyPercepcion(!isCredit);
						
						i = addLocaleARData(i, cheq.getbPartner(), isCredit);
						
					}
					
					if(!i.save()) {
						System.out.println("Error al guardar la NC/ND. " + i.getProcessMsg());
						response += "Error al guardar " + (isCredit ? "NC" : "ND.") + " " + i.getProcessMsg() + "<br>";
					}else
						response += ((invoiceID==0 ? "Se creo la "  
												  : "Se actualizo la ") + 
												  	(isCredit?"NC":"ND")
									) + 
									"<b> : " + 
									i.getDocumentNo() + 
									"</b><br>";
					
					invoiceID = i.getC_Invoice_ID();
				}
				
			}
			
			// Guardo la EC actual para la siguiente vuelta
			bPartner = cheq.getbPartner();
			
			if(invoiceID > 0) {
			// Se obtiene la linea (nueva / referenciada de un documento sin completar)
			MInvoiceLine il = new MInvoiceLine(getCtx(), invoiceLineID, get_TrxName());
			
			if(invoiceLineID != 0) {
				// Se debe actualizar únicamente el monto 
				il.setPriceEntered(cheq.getArsExchangeDif());	
					response += "Diferencia de cambio por cheque nro. <b>" 
						+ cheq.getPayment().getCheckNo() 
							+ " </b>ACTUALIZADA, tasa cobro: <b>" + cheq.getTrxRate().floatValue()
							+ " </b>tasa vto: <b>" + cheq.getDueRate().floatValue() + "</b><br>";
			} else {
				// Linea nueva. Se deben settear todos sus campos
				il.setC_Invoice_ID(invoiceID);
				il.setQty(1);
					il.setM_Product_ID(DB.getSQLValue(null, 
						"SELECT m_product_id FROM c_cintolo_exchange_dif_settings ORDER BY created DESC LIMIT 1"));
				BigDecimal price = cheq.getArsExchangeDif();
				il.setPriceActual(price);
				il.setPriceEntered(price);
				il.setPriceList(price);
				String desc = "Diferencia de cambio por cheque nro. " 
						+ cheq.getPayment().getCheckNo() 
							+ " Fecha cobro: " + cheq.getPayment().getDateTrx().toString().substring(0, 10) 
							+ ", tasa cobro: " + cheq.getTrxRate().floatValue() + " - "
							+ " Fecha vto: " + cheq.getPayment().getDueDate().toString().substring(0, 10) 
							+ ", tasa vto: " + cheq.getDueRate().floatValue();
				il.setDescription(desc);
					il.setC_Tax_ID(DB.getSQLValue(null, 
						"SELECT c_tax_id FROM c_tax WHERE name = 'Standard'"));
					response += "Diferencia de cambio por cheque nro. <b>" 
							+ cheq.getPayment().getCheckNo() 
							+ " Fecha cobro: <b>" + cheq.getPayment().getDateTrx().toString().substring(0, 10) + "</b>"
							+ " tasa cobro: <b>" + cheq.getTrxRate().floatValue() + "</b>"
							+ " Fecha vto: <b>" + cheq.getPayment().getDueDate().toString().substring(0, 10) + "</b>"
							+ " tasa vto: <b>" + cheq.getDueRate().floatValue() + "</b><br>";
			}
						
			if(!il.save()) {
					System.out.println("Error al guardar la línea de NC/ND. " + il.getProcessMsg());
					response += "Error al guardar linea NC/ND. " + il.getProcessMsg() + "<br>";
			} 
			
				// dREHER sep 24, hacerlo por BDD, ya que el payment puede estar completo o cerrado y 
				// seguramente de algun error al guardar la refencia a la linea de factura
				if(DB.executeUpdate("UPDATE C_Payment SET CINTOLO_Ref_Invoiceline_ID=" + il.getC_InvoiceLine_ID() +
						" WHERE C_Payment_ID=" + cheq.getPayment().getC_Payment_ID(), null) <= 0) {
					System.out.println("Error al guardar el cheque referenciado en el pago!");
					response += "El cheque: <b>" + cheq.getNroCheque() + " </b>No se pudo vincular al medio de pago: <b>" 
							+ cheq.getPayment().getDocumentNo() + "</b><br>";
				} 

			} 
		}
		
		return response;
	}
	
	private void debug(String string) {
		System.out.println("CintoloExchangeDifOnDeferredChecks. " + string);
	}

	/**
	 * Retorna la lista de precio configurada para diferencia de cambio
	 */
	private Integer getDebitCreditExchangeDiffPriceList(boolean isCredit) throws Exception {
		String valuePriceList = null;
		if (isCredit)
			valuePriceList = MPreference.GetCustomPreferenceValue("DIF_CAMBIO_TARIFA_CREDITO", Env.getAD_Client_ID(getCtx()));
		else
			valuePriceList = MPreference.GetCustomPreferenceValue("DIF_CAMBIO_TARIFA_DEBITO", Env.getAD_Client_ID(getCtx()));
		if(Util.isEmpty(valuePriceList,true)){
			throw new Exception("Debe configurar las preferencias DIF_CAMBIO_TARIFA_CREDITO y DIF_CAMBIO_TARIFA_DEBITO, indicando el nombre de las tarifas por diferencia de cambio de compra y venta con impuestos y percepciones incluidas en el precio.");
		}
		
		int priceList = DB.getSQLValue(null, "SELECT M_PriceList_ID FROM M_PriceList WHERE name = ?", valuePriceList);
		
		if (priceList <= 0)
			throw new Exception("Debe configurar las tarifas por diferencia de cambio de compra y venta con impuestos y percepciones incluidas en el precio.");			

		X_M_PriceList pl = new X_M_PriceList(getCtx(), priceList, null);
		if (!isCredit && !pl.isPerceptionsIncluded()) 
			throw new Exception("La tarifa configurada " + pl.getName() + " para diferencia de cambio debe tener activado el check: percepciones incluidas en el precio.");
		if (!pl.isTaxIncluded())
			throw new Exception("La tarifa configurada " + pl.getName() + " para diferencia de cambio debe tener activado el check impuestos incluidos en el precio.");
		
		return priceList; 
	}
	
	private String getRealDocTypeKey(boolean isCredit) throws Exception{
		// La lista de tipos de documento generales tiene como value los doc
		// type keys de los tipos de documento
		String docTypeKey = null;
		
		// Obtengo los doctype a partir de las nuevas preferences para poder
		// diferenciar Créditos y Débitos para clientes y proveedores.
		
		if (isCredit)
			docTypeKey = String.valueOf(MPreference
						.GetCustomPreferenceValue("DIF_CAMBIO_DOCTYPE_RC_CRED",
								Env.getAD_Client_ID(getCtx())));
		else
			docTypeKey = String.valueOf(MPreference
						.GetCustomPreferenceValue("DIF_CAMBIO_DOCTYPE_RC_DEB",
								Env.getAD_Client_ID(getCtx())));
		
		//Si no existen las nuevas preferencias, sigue ejecutando como estaba pero solo para clientes
		if ((docTypeKey == null) || ("".equals(docTypeKey))) {
			// Para Locale AR, Abono de Cliente es Nota de Crédito o Nota de Débito
			// dependiendo si estamos creando un crédito o un débito respectivamente
			if (LOCALE_AR_ACTIVE) {
				// Nota de Crédito
				if (isCredit) {
					docTypeKey = MDocType.DOCTYPE_CustomerCreditNote;
				}
				// Nota de Débito
				else {
					docTypeKey = MDocType.DOCTYPE_CustomerDebitNote;
				}
			}
		}
		//Si no es un crédtio o débito de cliente, el doctype quedará vacío y se tira una excepción
		if ((docTypeKey == null) || ("".equals(docTypeKey))){
				throw new Exception(
						"No se pudo determinar el Tipo de Documento para el comprobante de Diferencia de Cambio. Se deben configurar las variables DIF_CAMBIO_DOCTYPE_RC_CRED, y DIF_CAMBIO_DOCTYPE_RC_DEB");
		}
		return docTypeKey;
	}
	
	
	private MInvoice setDocType(MInvoice invoice, MBPartner bp, boolean isCredit, int puntoDeVenta) throws Exception{
		MDocType documentType = null;

		//buscar los doctype a partir de las nuevas preferencias
		//si encuentra setea doctype y return
		//Si no:
		// Obtener la clave del tipo de documento a general
		String docTypeKey = getRealDocTypeKey(isCredit);
		//Si hay nuevas preferencias se guarda el doctype, sino se ejecuta el código de siempre
		if ((isCredit && docTypeKey != MDocType.DOCTYPE_CustomerCreditNote)
				|| (!isCredit && docTypeKey != MDocType.DOCTYPE_CustomerDebitNote)) {
			documentType = MDocType.getDocType(getCtx(), docTypeKey,
					null);
		} else {
			// Si está activo locale_ar entonces se debe obtener en base al pto
			// de venta y la letra
			if (LOCALE_AR_ACTIVE) {
				MLetraComprobante letra = getLetraComprobante(bp);
				Integer posNumber = Integer.valueOf(MPreference
						.GetCustomPreferenceValue("DIF_CAMBIO_PTO_VENTA",
								Env.getAD_Client_ID(getCtx())));

				// dREHER Si llega como parametro, tomar este punto de venta
				if(puntoDeVenta>0)
					posNumber = puntoDeVenta;
				
				if (Util.isEmpty(posNumber, true))
					throw new Exception("NotExistPOSNumber");
				// Se obtiene el tipo de documento para la factura.
				documentType = MDocType.getDocType(getCtx(),
						invoice.getAD_Org_ID(), docTypeKey, letra.getLetra(),
						posNumber, null);
				if (documentType == null) {
					throw new Exception(Msg.getMsg(getCtx(),
							"NonexistentPOSDocType", new Object[] { letra,
									posNumber }));
				}
				if (!Util.isEmpty(posNumber, true)) {
					invoice.setPuntoDeVenta(posNumber);
				}
			} else {
				// Tipo de documento en base a la key
				documentType = MDocType.getDocType(getCtx(), docTypeKey,
						null);
			}
		}
		
		invoice.setC_DocTypeTarget_ID(documentType.getID());
		invoice.setIsSOTrx(documentType.isSOTrx());
		return invoice;
	}
	
	private MInvoice addLocaleARData(MInvoice invoice, MBPartner bPartner, boolean credit) throws Exception{
		
		MLetraComprobante letraCbte = getLetraComprobante(bPartner);
		// Se asigna la letra de comprobante, punto de venta y número de comprobante
		// a la factura creada.
		invoice.setC_Letra_Comprobante_ID(letraCbte.getID());
		// Nro de comprobante.
		Integer nroComprobante = CalloutInvoiceExt
				.getNextNroComprobante(invoice.getC_DocTypeTarget_ID());
		if (nroComprobante != null)
			invoice.setNumeroComprobante(nroComprobante);

		// Asignación de CUIT en caso de que se requiera.

		String cuit = bPartner.getTaxID();
		invoice.setCUIT(cuit);

		// Setear una factura original al crédito que estamos creando
		if(LOCALE_AR_ACTIVE){ // dREHER credit && <-- la ND tambien requiere el comprobante origen por parte de AFIP
			// Obtengo la primer factura como random (la impresora fiscal puede tirar un error si no existe una factura original seteada)
			invoice.setC_Invoice_Orig_ID(invoice.getC_Invoice_ID());
		}

		// Para notas de credito NO DEBE aplicar percepcion
		invoice.setApplyPercepcion(!credit);
		return invoice;
	}

	private MLetraComprobante getLetraComprobante(MBPartner bPartner) throws Exception{
		Integer categoriaIVAclient = CalloutInvoiceExt.darCategoriaIvaClient();
		Integer categoriaIVACustomer = bPartner.getC_Categoria_Iva_ID();
		// Se validan las categorias de IVA de la compañia y el cliente.
		if (categoriaIVAclient == null || categoriaIVAclient == 0) {
			throw new Exception("ClientWithoutIVAError");
		} else if (categoriaIVACustomer == null || categoriaIVACustomer == 0) {
			throw new Exception("BPartnerWithoutIVAError");
		}
		// Se obtiene el ID de la letra del comprobante a partir de las categorias de IVA.
		Integer letraID = CalloutInvoiceExt.darLetraComprobante(categoriaIVACustomer, categoriaIVAclient);
		if (letraID == null || letraID == 0){
			throw new Exception("LetraCalculationError");
		}
		// Se obtiene el PO de letra del comprobante.
		System.out.println("letraID=" + letraID);
		return new MLetraComprobante(getCtx(), letraID, null);
	}

	public class ChequeEnCartera {
        private MPayment payment;
        private MBPartner bPartner;
        private BigDecimal arsExchangeDif;
        private BigDecimal usdAlRecibir;
        private BigDecimal usdAlCobrar;
        private BigDecimal trxRate;
        private BigDecimal dueRate;
        private String nroCheque;  // dREHER sep 24
        
        public ChequeEnCartera(MPayment payment, MBPartner bPartner, BigDecimal arsExchangeDif, BigDecimal usdAlRecibir, BigDecimal usdAlCobrar, BigDecimal trxRate, BigDecimal dueRate) {
            this.payment = payment;
            this.bPartner = bPartner;
            this.arsExchangeDif = arsExchangeDif;
            this.usdAlRecibir = usdAlRecibir;
            this.usdAlCobrar = usdAlCobrar;
            this.trxRate = trxRate;
            this.dueRate = dueRate;
            
            // dREHER sep 24
            this.setNroCheque(payment.getCheckNo());
        }

		public MPayment getPayment() {
			return payment;
		}

		public MBPartner getbPartner() {
			return bPartner;
		}
		
		public BigDecimal getArsExchangeDif() {
			return arsExchangeDif;
		}
		
		public BigDecimal getUsdAlRecibir() {
			return usdAlRecibir;
		}

		public BigDecimal getUsdAlCobrar() {
			return usdAlCobrar;
		}

		public BigDecimal getTrxRate() {
			return trxRate;
		}
		
		public BigDecimal getDueRate() {
			return dueRate;
		}

		public String getNroCheque() {
			return nroCheque;
		}

		public void setNroCheque(String nroCheque) {
			this.nroCheque = nroCheque;
		}
        
    }
}
