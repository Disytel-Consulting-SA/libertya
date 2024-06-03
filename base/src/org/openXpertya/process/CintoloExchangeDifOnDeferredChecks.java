package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class CintoloExchangeDifOnDeferredChecks extends SvrProcess {
	private BigDecimal days;

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
    					new MPayment(getCtx(), paymentID, get_TrxName()), 
    					new MBPartner(getCtx(), bPartnerID, get_TrxName()), 
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
    	
		return generateDocuments(chequesNC, true, date) + "<br>" + generateDocuments(chequesND, false, date);
	}

	
	private String generateDocuments(ArrayList<ChequeEnCartera> cheques, boolean isCredit, LocalDate date) {
		String formatedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String response = "";
		MBPartner bPartner = null; // Para corte de control
		int invoiceID = 0;				
		
		for(ChequeEnCartera cheq : cheques) {			
			boolean skipHeader = false;
			int invoiceLineID = 0;
			
			//Si el cheque ya está referenciado, actualizo solo la línea
			if(cheq.getPayment().get_Value("CINTOLO_Ref_Invoiceline_ID") != null &&
				((Integer) cheq.getPayment().get_Value("CINTOLO_Ref_Invoiceline_ID") > 0)) {
				
				skipHeader = true;
				
				invoiceLineID = (Integer) cheq.getPayment().get_Value("CINTOLO_Ref_Invoiceline_ID");
				MInvoiceLine il = new MInvoiceLine(getCtx(),invoiceLineID,get_TrxName());
				
				MInvoice i = new MInvoice(getCtx(), il.getC_Invoice_ID(), get_TrxName());
				invoiceID = i.getC_Invoice_ID();
				
				if(!i.getDocStatus().equals("DR")) {
					// EL DOC SE ENCUENTRA COMPLETO. NO SE MODIFICA LA LÍNEA.
					response += "Documento " + i.getDocumentNo() 
							+ " en estado completo. Cheque " + cheq.getPayment().getCheckNo() + " sin actualizar <br>";
					continue;
				}
			}
			
			if(!skipHeader && 
					(bPartner == null || cheq.getbPartner().getC_BPartner_ID() != bPartner.getC_BPartner_ID())) {					
				
				invoiceID = 0; // De primeras, hay que crear una NC/ND nueva
				
				// Si la EC tiene activado el check "Acumular diferencia de Cambio", busco si existe alguna
				if(invoiceID == 0 && cheq.getbPartner().isCintolo_Acumulate_Exchange_Dif()) {
					int oldInvoiceID = DB.getSQLValue(get_TrxName(),
							" SELECT c_invoice_id "
							+ " FROM c_invoice i "
							+ " JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id "
							+ " WHERE i.issotrx = 'Y' "
							+ " AND i.cintolo_apply_exchange_dif = 'Y' "
							+ " AND i.docstatus IN ('DR','CO','CL') "
							+ " AND dt.docbasetype = " + (isCredit ? "'CUC'" : "'CUD'") + " "
							+ " AND i.c_bpartner_id = " + cheq.getbPartner().getC_BPartner_ID() + " "
							+ " ORDER BY i.created DESC "
							+ " LIMIT 1");
					invoiceID = oldInvoiceID < 0 ? 0 : oldInvoiceID;
				}
				
				MInvoice i = new MInvoice(getCtx(), invoiceID, get_TrxName());
				
				// Si la NC/ND es nueva, se deben settear los campos
				if(invoiceID == 0) {
					i.setC_BPartner_ID(cheq.getbPartner().getC_BPartner_ID());
					i.setDateInvoiced(new Timestamp(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()));
					i.set_Value("Cintolo_Apply_Exchange_Dif", true);
					i.setManualGeneralDiscount(Env.ZERO);
					int priceListID = DB.getSQLValue(get_TrxName(), 
							" SELECT " + (isCredit ? "credit_pricelist ": "debit_pricelist") + 
							" FROM C_Cintolo_Exchange_Dif_Settings ORDER BY created DESC LIMIT 1");
					if(priceListID > 0) {
						i.setM_PriceList_ID(priceListID);
					}
					// Parámetros para la determinación del DOCTYPE
					i.setC_DocTypeTarget_ID(0);
					i.setTipoComprobante(isCredit? "NC" : "ND");
					int ptoVenta = DB.getSQLValue(get_TrxName(), 
							" SELECT point_of_sale FROM C_Cintolo_Exchange_Dif_Settings ORDER BY created DESC LIMIT 1");
					i.setPuntoDeVenta(ptoVenta > 0 ? ptoVenta: 1);						
					i.setC_Letra_Comprobante_ID(DB.getSQLValue(get_TrxName(), 
							"SELECT c_letra_comprobante_id FROM c_letra_comprobante WHERE letra = 'A'"));
					
					if(!i.save()) {
						System.out.println("Error al guardar la NC/ND");
					}
					
					invoiceID = i.getC_Invoice_ID();
				}
				
			}
			
			// Guardo la EC actual para la siguiente vuelta
			bPartner = cheq.getbPartner();
			
			// Se obtiene la linea (nueva / referenciada de un documento sin completar)
			MInvoiceLine il = new MInvoiceLine(getCtx(), invoiceLineID, get_TrxName());
			
			if(invoiceLineID != 0) {
				// Se debe actualizar únicamente el monto 
				il.setPriceEntered(cheq.getArsExchangeDif());	
				response += "Diferencia de cambio por cheque nro. " 
						+ cheq.getPayment().getCheckNo() 
						+ " ACTUALIZADA, tasa cobro: " + cheq.getTrxRate().floatValue()
						+ " tasa vto: " + cheq.getDueRate().floatValue() + "<br>";
			} else {
				// Linea nueva. Se deben settear todos sus campos
				il.setC_Invoice_ID(invoiceID);
				il.setQty(1);
				il.setM_Product_ID(DB.getSQLValue(get_TrxName(), 
						"SELECT m_product_id FROM c_cintolo_exchange_dif_settings ORDER BY created DESC LIMIT 1"));
				BigDecimal price = cheq.getArsExchangeDif();
				il.setPriceActual(price);
				il.setPriceEntered(price);
				il.setPriceList(price);
				String desc = "Diferencia de cambio por cheque nro. " 
						+ cheq.getPayment().getCheckNo() 
						+ ", tasa cobro: " + cheq.getTrxRate().floatValue()
						+ " tasa vto: " + cheq.getDueRate().floatValue();
				il.setDescription(desc);
				il.setC_Tax_ID(DB.getSQLValue(get_TrxName(), 
						"SELECT c_tax_id FROM c_tax WHERE name = 'Standard'"));
				response += desc + "<br>";
			}
						
			if(!il.save()) {
				System.out.println("Error al guardar la línea de NC/ND");
			} 
			
			// Se guarda la ref de la línea creada en el cheque
			cheq.getPayment().set_Value("CINTOLO_Ref_Invoiceline_ID", il.getC_InvoiceLine_ID());
			if(!cheq.getPayment().save()) {
				System.out.println("Error al guardar el cheque");
			} 
		}
		
		return response;
	}

	public class ChequeEnCartera {
        private MPayment payment;
        private MBPartner bPartner;
        private BigDecimal arsExchangeDif;
        private BigDecimal usdAlRecibir;
        private BigDecimal usdAlCobrar;
        private BigDecimal trxRate;
        private BigDecimal dueRate;
        
        public ChequeEnCartera(MPayment payment, MBPartner bPartner, BigDecimal arsExchangeDif, BigDecimal usdAlRecibir, BigDecimal usdAlCobrar, BigDecimal trxRate, BigDecimal dueRate) {
            this.payment = payment;
            this.bPartner = bPartner;
            this.arsExchangeDif = arsExchangeDif;
            this.usdAlRecibir = usdAlRecibir;
            this.usdAlCobrar = usdAlCobrar;
            this.trxRate = trxRate;
            this.dueRate = dueRate;
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
        
    }
}
