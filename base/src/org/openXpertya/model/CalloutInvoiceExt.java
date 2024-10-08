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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;


/*
 * 
 * RP5. Información de CUIT e IVA a nivel de Organización
 * respetando jerarquia de busqueda, info a organizacion hija, org carpeta, client (compa#ia)
 * SportClub
 * dREHER jorge.dreher@gmail.com
 * Mar - 2013
 */



public class CalloutInvoiceExt extends CalloutInvoice {

	private static Boolean st_ComprobantesFiscalesActivos = null;
	
	public static boolean ComprobantesFiscalesActivos(boolean reload) {
		if (st_ComprobantesFiscalesActivos == null || reload) {
			CPreparedStatement st = DB.prepareStatement(" SELECT value FROM AD_Preference WHERE attribute = 'LOCAL_AR' ");
			ResultSet rs = null;
			try {
				rs = st.executeQuery();
				
				st_ComprobantesFiscalesActivos = (rs.next()) && rs.getString(1).equals("Y");
			} catch (SQLException e) {
				st_ComprobantesFiscalesActivos = false;
			} finally {
				try {
					st.close();
				} catch (SQLException e) {}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {}
				}
			}
			
		}
		return st_ComprobantesFiscalesActivos;
	}
	
	public static boolean ComprobantesFiscalesActivos() {
		return ComprobantesFiscalesActivos(false);
	}
	
	public static boolean ValidarCUIT(String num) {
		boolean ret = false;
		
		if (num == null || num.trim().length() == 0)
			return false;
		
		num = num.trim();
		
		try {
			int[] valoresMagicos = {5,4,3,2,7,6,5,4,3,2};
			int[] valores = new int[11];
			int i;
			int suma = 0;

			num = num.replace("-", "");
			
			if (num.length() != 11)
				return false;
			
			for (i = 0; i < 11; i++)
				valores[i] = Integer.parseInt(num.substring(i, i+1));
			
			int digVerificador = valores[10];
			
			for (i = 0; i < 10; i++)
				suma = suma + valores[i] * valoresMagicos[i];
			
			int dividendo = suma / 11;
			int producto = dividendo * 11;
			int diferencia = suma - producto;
			digVerificador = (diferencia > 0) ? 11 - diferencia : diferencia;  
			
			ret = (digVerificador == valores[i]); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static HashMap<String, Object> DividirDocumentNo(Integer clientID, String docNo) {
		return DividirDocumentNo(clientID, docNo, 0, Env.getCtx());
	}
	
	public static HashMap<String, Object> DividirDocumentNo(Integer clientID, String docNo, int docTypeId, Properties ctx) {
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		MDocType docType = new MDocType(ctx, docTypeId, null);
		
		hm.put("C_Letra_Comprobante_ID", null);
		hm.put("PuntoDeVenta", docType.isFiscalDocument() ? null : 0);
		hm.put("NumeroComprobante", docType.isFiscalDocument() ? null : 0);

		if (docNo.startsWith("<"))
			docNo = docNo.substring(1, docNo.length() - 1);
		
		String letra = docNo.substring(0, 1);
		String puntoDeVenta = docNo.substring(1, 5);
		String numeroComprobante = docNo.substring(5);
		
		MLetraComprobante mletra = MLetraComprobante.buscarLetraComprobante(clientID, letra, null);
		
		if (mletra != null && mletra.getID() > 0) {
			hm.put("C_Letra_Comprobante_ID", mletra.getID());
			hm.put("PuntoDeVenta", Integer.parseInt(puntoDeVenta));
			hm.put("NumeroComprobante", Integer.parseInt(numeroComprobante));
		}
		
		return hm;
	}
	
	/**
	 * Obtiene el número de comprobante a partir de un número de documento con el formato
	 * APPPPNNNNNNNN. Ej: A000100000234, devuelve 234.
	 * @param documentNo Número de documento a parsear.
	 * @return <code>Integer</code> que contiene el número de comprobante, <code>null</code>
	 * en caso de que no se pueda parsear el nro. de comprobante a partir del documentNo.
	 */
	public static Integer getNroComprobante(String documentNo) {
		Integer nro = null;
		if (documentNo != null) {
			Map<String, Object> parts = DividirDocumentNo(
					Env.getAD_Client_ID(Env.getCtx()), documentNo);
			nro = (Integer)parts.get("NumeroComprobante");
		}
		return nro;
	}
	
	public static Integer getNextNroComprobante(int docTypeID) {
		
		Integer nro = null;
		Properties ctx = Env.getCtx();
		MDocType mDocType = new MDocType(ctx, docTypeID, null);
		if (mDocType != null) {
			MSequence seq = new MSequence(ctx, mDocType.getDocNoSequence_ID(), null);
			if (seq != null) {
				String nextNo = String.valueOf(seq.getCurrentNext());
				try {
					String number = nextNo.substring(1,nextNo.length());
					// dREHER control extra para TPV terminados en CERO
					if(seq.getPrefix()!=null && seq.getPrefix().length() <= 3 && nextNo.length() <= 8) {
						number = nextNo;
					}
					nro = Integer.parseInt(number);
					
				} catch (Exception e) {
					// retorna null
				}
			}
		}
		return nro;
	}
	
	private String docTypeStd( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_DocType_ID = ( Integer )value;
        
        if( (C_DocType_ID == null) || (C_DocType_ID.intValue() == 0) ) {
            return "";
        }
        
        // Para seteo manual del nro de documento no debo sugerir nada
//        Boolean manualDocumentNo = null;
//        Object manualObj = mTab.getValue("ManualDocumentNo");
//        if(manualObj != null){
//        	if(manualObj instanceof String){
//        		manualDocumentNo = ((String)mTab.getValue("ManualDocumentNo")).equals("Y");
//        	}
//        	else{
//        		manualDocumentNo = (Boolean)mTab.getValue("ManualDocumentNo");
//        	}
//        }
//        if(manualDocumentNo != null && manualDocumentNo){
//        	return "";
//        }

        try {
            String SQL = "SELECT d.HasCharges,'N',d.IsDocNoControlled," + "s.CurrentNext, d.DocBaseType, s.prefix, s.suffix, d.caicontrol, d.DocTypeKey, d.docsubtypeinv " + "FROM C_DocType d, AD_Sequence s " + "WHERE C_DocType_ID=?"    // 1
                         + " AND d.DocNoSequence_ID=s.AD_Sequence_ID(+)";
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_DocType_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // Charges - Set Context

                Env.setContext( ctx,WindowNo,"HasCharges",rs.getString( 1 ));

                // DocumentNo
                
                String pre = rs.getString(6);
                String suf = rs.getString(7);

                // HACK
                
                if (pre == null)
                	pre = "";
                
                if (suf == null)
                	suf = "";
                
                if( rs.getString( 3 ).equals( "Y" )) {
                	if(mTab.isInserting() || mField.isChanged()){
                		mTab.setValue( "DocumentNo","<" + pre + rs.getString( 4 ) + suf + ">" );
                	}
                }

                // DocBaseType - Set Context

                String s = rs.getString( 5 );
                
                Env.setContext( ctx,WindowNo,"DocBaseType",s );
                
                String dk = rs.getString( "DocTypeKey" );

                Env.setContext( ctx,WindowNo,"DocTypeKey",dk );
                
                String dsi = rs.getString( "docsubtypeinv" );

                Env.setContext( ctx,WindowNo,"docsubtypeinv", dsi);

                // AP Check & AR Credit Memo
				// Si el campo de forma de pago contiene un valor por defecto,
				// entonces no lo modifico
                MField paymentRuleField = mTab.getField("PaymentRule");
                if(paymentRuleField != null && paymentRuleField.getDefault() == null){
	                if( s.startsWith( "AP" )) {
	                	if(mTab.isInserting() || mField.isChanged()){
	                		mTab.setValue( "PaymentRule","S" );    // Check
	                	}
	                } else if( s.endsWith( "C" )) {
	                	if(mTab.isInserting() || mField.isChanged()){
	                		mTab.setValue( "PaymentRule","P" );    // OnCredit
	                	}
	                }
                }
                
				// Se comenta ya que sino siempre se asigna valor a estos campos y nunca deja de
				// estar habilitado el botón Save
                // Si se controla CAI, entonces buscar el CAI válido para este Tipo de Documento
                /*boolean IsSOTrx = "Y".equals( Env.getContext( ctx,WindowNo,"IsSOTrx" ));
                if(IsSOTrx) {
                	mTab.setValue("CAI", null);
            		mTab.setValue("DateCAI", null);
                }
                if(rs.getString( "caicontrol" ).equals( "Y" )) {
                	Timestamp dateInvoiced = (Timestamp)mTab.getValue("DateInvoiced");
                	String sql = "select c.cai, c.datecai " + 
                			"	from c_cai c " + 
                			"	join c_cai_doctype cd on cd.c_cai_id = c.c_cai_id " + 
                			"	where cd.c_doctype_id = ? and cd.isactive = 'Y' and c.isactive = 'Y' and ?::date between c.validfrom::date and c.datecai::date " + 
                			"	order by c.created desc " +
                			"	limit 1 ";
                	PreparedStatement psCai = DB.prepareStatement(sql, null, true);
                	psCai.setInt(1, C_DocType_ID);
                	psCai.setTimestamp(2, dateInvoiced);
                	ResultSet rsCai = psCai.executeQuery();
                	if(rsCai.next()) {
                		mTab.setValue("CAI", rsCai.getString("cai"));
                		mTab.setValue("DateCAI", rsCai.getTimestamp("datecai"));
                	}
                	rsCai.close();
                	psCai.close();
                }*/
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"socType",e );

            return e.getLocalizedMessage();
        }

        return "";
    }    // docType

	/**
	 * Obtiene el próximo nro del tipo de documento parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param docTypeID
	 *            id del tipo de documento
	 * @param trxName
	 *            transacción actual
	 * @return el nro de documento siguiente para este tipo de documento, null
	 *         caso contrario
	 */
	public static String getNextDocumentNo(Properties ctx, Integer docTypeID, String trxName){
		String documentNo = null;
		if(!Util.isEmpty(docTypeID, true)){
			String sql = "SELECT coalesce(s.prefix,'') || s.CurrentNext || coalesce(s.suffix,'') " +
						 "FROM C_DocType d, AD_Sequence s " +
						 "WHERE C_DocType_ID=? AND d.DocNoSequence_ID=s.AD_Sequence_ID(+)";
			documentNo = DB.getSQLValueString(trxName, sql, docTypeID);
		}
		return documentNo;
	}
	
	// dREHER, cuando se modifique la organizacion setear la letra de comprobante correspondiente
	// en funcion de la categoria de IVA del cliente y de la compa#ia
	public String AD_Org(Properties ctx, int WindowNo, MTab tab, MField field,
			Object value){
		
		if(tab.isInserting() || field.isChanged()){

			Integer AD_Org_ID = ( Integer )value;
			if( (AD_Org_ID == null) || (AD_Org_ID.intValue() == 0) ) {
				return "";
			}

			// Si todavia no se eligio socio, no hacer nada
			if(tab.getValue("C_BPartner_ID")==null)
				return "";
			
			MBPartner partner = new MBPartner(ctx, (Integer)tab.getValue("C_BPartner_ID"),
					null);

			// Llamo pasando como parametro la organizacion del documento
			Integer categoriaIvaClient = darCategoriaIvaClient(AD_Org_ID);

			// TODO: despues eliminar comentario
			log.fine("Trajo condicion IVA de organizacion como =" + categoriaIvaClient);

			categoriaIvaClient = categoriaIvaClient == null ? 0	: categoriaIvaClient;
			int categoriaIva = partner.getC_Categoria_Iva_ID();

			log.fine("Iva cliente=" + categoriaIva + " iva compa#ia=" + categoriaIvaClient);

			// Si ambas categorias estan asignadas, setear letra de comprobante correspondiente
			if (categoriaIvaClient > 0 || categoriaIva > 0) {

				int letraId = 0;
				boolean IsSOTrx = Env.getContext(ctx, WindowNo, "IsSOTrx").equals("Y");

				if (IsSOTrx) { // partner -> customer, empresa -> vendor
					letraId = darLetraComprobante(categoriaIva, categoriaIvaClient);
				} else { // empresa -> customer, partner -> vendor
					letraId = darLetraComprobante(categoriaIvaClient, categoriaIva);
				}

				tab.setValue("C_Letra_comprobante_ID", letraId);
			}

		}

		return "";
	}
	
	@Override
	public String docType(Properties ctx, int WindowNo, MTab tab, MField field,
			Object value) {
		
		String ret = docTypeStd(ctx, WindowNo, tab, field, value);
		
		if(field.isChanged()){
			try {
				String docNo = ((String)tab.getValue("DocumentNo"));
				
				// <A000100000001>
				// 012345678901234
				
				// A000100000001
				// 0123456789012
				
				Integer C_DocType_ID = ( Integer )value;
				HashMap<String, Object> hm = DividirDocumentNo(
						Env.getAD_Client_ID(Env.getCtx()), docNo, C_DocType_ID, ctx);
				Integer bPartnerID = (Integer)tab.getValue("C_BPartner_ID");
				for (String k : hm.keySet()) { 
					// Solo asigna la letra del comprobante si no se ha ingresado
					// la entidad comercial. Si se ingreso la entidad comercial
					// la letra se calcula a partir de las categorias de iva
					// de la EC y la Compañia.
					if(bPartnerID == null || !k.equals("C_Letra_Comprobante_ID") )
						tab.setValue(k, hm.get(k));
				}
				
			} catch (Exception e) {
				
			}
		}
		return ret;
	}
	
	public String calloutCUIT(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (ValidarCUIT(value.toString()))
			return "";
		
		return "InvalidCUIT";
	}
	
	public static Integer darLetraComprobante(int categoriaIvaCustomer, int categoriaIvaVendor) {
		
		Integer letraId = null;
		
		String sq1 = "SELECT C_Letra_comprobante_ID FROM C_LETRA_ACEPTA_IVA ai "
			+ "WHERE CATEGORIA_CUSTOMER = ? AND CATEGORIA_VENDOR = ?";

		PreparedStatement pstmt = DB.prepareStatement(sq1);
		ResultSet rs = null;
		try {
			pstmt.setInt(1, categoriaIvaCustomer);
			pstmt.setInt(2, categoriaIvaVendor);
		
			rs = pstmt.executeQuery();
			if (rs.next()) {
				letraId = new Integer(rs.getInt("C_Letra_comprobante_ID"));
			} 
		} catch (SQLException e) {
			return null;
		} finally {
			try {
				if(rs != null)rs.close();
				if(pstmt != null)pstmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
				return null;
			}
		}
		return letraId;
	}
	
	public static MLetraAceptaIva darLetraAceptaIVA(Properties ctx, int categoriaIvaCustomer, int categoriaIvaVendor, String trxName) {
		
		MLetraAceptaIva letraAceptaIVA = null;
		
		String sq1 = "SELECT * FROM C_LETRA_ACEPTA_IVA "
			+ "WHERE CATEGORIA_CUSTOMER = ? AND CATEGORIA_VENDOR = ?";

		PreparedStatement pstmt = DB.prepareStatement(sq1);
		try {
			pstmt.setInt(1, categoriaIvaCustomer);
			pstmt.setInt(2, categoriaIvaVendor);
		
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				letraAceptaIVA = new MLetraAceptaIva(ctx, rs, trxName);
			} 
		} catch (SQLException e) {
			return null;
		}		
		return letraAceptaIVA;
	}
	
	// Sobrecargo metodos para guardar compatibilidad con codigo existente
	public static Integer darCategoriaIvaClient(){
		return darCategoriaIvaClient(0);
	}
	
	/** Devuelve la categoria de IVA de la Compañía (Client) actual
	 * 
	 * Modificado por dREHER, jorge.dreher@gmail.com - Marzo 2013
	 * para buscar en cascada respetando logica de organizaciones hoja, carpeta, compania
	 * 
	 * @return
	 */
	public static Integer darCategoriaIvaClient(int AD_Org_ID) {
		Integer categoriaIvaClient = null;
		String trxName = null;
		
		//TODO: ver si leo AD_Org_ID y ya lo paso como parametro desde aca, por ahora dejo que se encargue MClient de obtenerla a partir del entorno
		// int AD_Org_ID = Env.getContextAsInt(ctx, WindowNo, "AD_Org_ID");
		
		MClient client = new MClient(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), trxName);
		if(client != null)
			categoriaIvaClient = client.getCategoriaIva(AD_Org_ID);
		
		return categoriaIvaClient;
		
		
/* Codigo original
 * 		
 
		String sq = "SELECT C_Categoria_Iva_ID FROM AD_CLIENTINFO WHERE "
			+ "AD_CLIENT_ID = ?";
		
		PreparedStatement pstmt = DB.prepareStatement(sq);
		int client = Env.getAD_Client_ID(Env.getCtx());
		try {
			pstmt.setInt(1, client);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				categoriaIvaClient = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return categoriaIvaClient;
		
*/		
		
	}
	
    public String bPartner(Properties ctx, int WindowNo, MTab mTab,
			MField mField, Object value) {
    	String trxName = null;
		// disytel
		int categoriaIva = 0;
		int clocation_id = 0;
		String taxId = "";

		Integer C_BPartner_ID = (Integer) value;
		if (C_BPartner_ID == null || C_BPartner_ID.intValue() == 0)
			return "";

		String SQL = "SELECT p.AD_Language,p.C_PaymentTerm_ID,"
				+ "p.M_PriceList_ID,p.PaymentRule,p.POReference,"
				+ "p.SO_Description,p.IsDiscountPrinted,"
				+ "p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable,"
				+ "l.C_BPartner_Location_ID,c.AD_User_ID,"
				+ "p.PO_PriceList_ID, p.PaymentRulePO, p.PO_PaymentTerm_ID, C_Categoria_Iva_ID, p.C_Location_ID, p.TaxID,  a.C_Region_ID " // disytel
																																			// iva,
																																			// domicilio
																																			// fiscal,
																																			// cuit
				+ "FROM C_BPartner p "
				+ " LEFT OUTER JOIN C_BPartner_Location l ON (p.C_BPartner_ID=l.C_BPartner_ID AND l.IsBillTo='Y' AND l.IsActive='Y')"
				+ " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) "
				+ " LEFT JOIN AD_BPartner_Org_Auth a ON a.C_BPartner_ID = p.C_BPartner_ID AND a.AD_Client_ID = ? AND a.AD_Org_ID = ?  "
				+ "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'"; // #1

		boolean IsSOTrx = Env.getContext(ctx, WindowNo, "IsSOTrx").equals("Y");
		try {
			PreparedStatement pstmt = DB.prepareStatement(SQL);
			pstmt.setInt(1, Env.getAD_Client_ID(ctx));
			pstmt.setInt(2, Env.getAD_Org_ID(ctx));
			pstmt.setInt(3, C_BPartner_ID.intValue());
			ResultSet rs = pstmt.executeQuery();
			//
			if (rs.next()) {
				// PriceList & IsTaxIncluded & Currency
				Integer ii = null;

				// Modificado por Jorge Vidal - Disytel
				// Fecha: 03/10/2006 - Poner lista de precios de la sucursal
				/*
				 * Integer ii = new Integer(rs.getInt(IsSOTrx ? "M_PriceList_ID" :
				 * "PO_PriceList_ID")); if (!rs.wasNull())
				 * mTab.setValue("M_PriceList_ID", ii); else { // get default
				 * PriceList int i = Env.getContextAsInt(ctx,
				 * "#M_PriceList_ID"); if (i != 0)
				 * mTab.setValue("M_PriceList_ID", new Integer(i)); }
				 * 
				 */

				// Agregado para completar con lista de precio de la sucursal
				// Modificado por Jorge Vidal - Disytel
				// Fecha: 03/10/2006
				
				// Verifica si la Entidad Comercial tiene asociada una lista de precios 
				// o setea una que figure como predeterminada 
				int priceListId = rs.getInt(IsSOTrx ? "M_PriceList_ID" : "PO_PriceList_ID");
				if (priceListId != 0){
					mTab.setValue("M_PriceList_ID", priceListId);
				}
				
                /* Se comenta ya que el campo puede tener un valor por defecto 
                 * en el campo de tarifa y estar como sólo lectura, 
                 * este código modifica la tarifa
                 * *******************************************************
				else {
					setPriceList(ctx, WindowNo, mTab, mField, value);
				}
				*******************************************************/
				

				// PaymentRule
				String s = rs.getString(IsSOTrx ? "PaymentRule"
						: "PaymentRulePO");
				if (s != null && s.length() != 0) {
					if (Env.getContext(ctx, WindowNo, "DocBaseType").endsWith(
							"C")) // Credits are Payment Term
						s = "P";
					else if (IsSOTrx && (s.equals("S") || s.equals("U"))) // No
																			// Check/Transfer
																			// for
																			// SO_Trx
						s = "P"; // Payment Term
					mTab.setValue("PaymentRule", s);
				}
				// Payment Term
				ii = new Integer(rs.getInt(IsSOTrx ? "C_PaymentTerm_ID"
						: "PO_PaymentTerm_ID"));
				if (!rs.wasNull())
					mTab.setValue("C_PaymentTerm_ID", ii);

				// Location
				int locID = rs.getInt("C_BPartner_Location_ID");
				// overwritten by InfoBP selection - works only if InfoWindow
				// was used otherwise creates error (uses last value, may belong
				// to differnt BP)
				if (C_BPartner_ID.toString().equals(
						Env.getContext(ctx, Env.WINDOW_INFO, Env.TAB_INFO,
								"C_BPartner_ID"))) {
					String loc = Env.getContext(ctx, Env.WINDOW_INFO,
							Env.TAB_INFO, "C_BPartner_Location_ID");
					if (loc.length() > 0)
						locID = Integer.parseInt(loc);
				}
				if (locID == 0)
					mTab.setValue("C_BPartner_Location_ID", null);
				else
					mTab.setValue("C_BPartner_Location_ID", new Integer(locID));

				// lugar de entrega de mercaderia
				if (rs.getBigDecimal("C_Region_ID") != null)
					mTab.setValue("C_Region_ID", new Integer(rs
							.getInt("C_Region_ID")));

				// Contact - overwritten by InfoBP selection
				int contID = rs.getInt("AD_User_ID");
				if (C_BPartner_ID.toString().equals(
						Env.getContext(ctx, Env.WINDOW_INFO, Env.TAB_INFO,
								"C_BPartner_ID"))) {
					String cont = Env.getContext(ctx, Env.WINDOW_INFO,
							Env.TAB_INFO, "AD_User_ID");
					if (cont.length() > 0)
						contID = Integer.parseInt(cont);
				}
				if (contID == 0)
					mTab.setValue("AD_User_ID", null);
				else
					mTab.setValue("AD_User_ID", new Integer(contID));

				// Seteo Comercial/Usuario dependiendo la EC seleccionada
                
                MBPartner ec = new MBPartner(ctx, C_BPartner_ID, null);
            	int contEC = ec.getSalesRep_ID();
            	
            	if (contEC == 0){
            		mTab.setValue( "SalesRep_ID",null );
            	} else {
            		mTab.setValue( "SalesRep_ID",contEC );
            	}
				
				// CreditAvailable
				if (IsSOTrx) {
					double CreditLimit = rs.getDouble("SO_CreditLimit");
					if (CreditLimit != 0) {
						double CreditAvailable = rs
								.getDouble("CreditAvailable");
						if (!rs.wasNull() && CreditAvailable < 0)
							mTab.fireDataStatusEEvent("CreditLimitOver",
									DisplayType.getNumberFormat(
											DisplayType.Amount).format(
											CreditAvailable));
					}
				}

				// PO Reference
				s = rs.getString("POReference");
				if (s != null && s.length() != 0)
					mTab.setValue("POReference", s);
				else
					mTab.setValue("POReference", null);
				// SO Description
				s = rs.getString("SO_Description");
				if (s != null && s.trim().length() != 0)
					mTab.setValue("Description", s);
				// IsDiscountPrinted
				s = rs.getString("IsDiscountPrinted");
				if (s != null && s.length() != 0)
					mTab.setValue("IsDiscountPrinted", s);
				else
					mTab.setValue("IsDiscountPrinted", "N");

				// disytel
				categoriaIva = rs.getInt("C_Categoria_Iva_ID");
				clocation_id = rs.getInt("C_Location_ID");
				taxId = rs.getString("TaxID");
				mTab.setValue("CUIT", taxId);
				mTab.setValue("C_Location_ID", new Integer(clocation_id));
				
				// Se setea el codigo de iva del bPartner en el contexto
				int codigoIva =  MCategoriaIva.getCodigo(categoriaIva, trxName);
				Env.setContext(ctx, WindowNo, "CodigoCategoriaIVA", codigoIva);
				
				// Si el codigo de iva es CONSUMIDOR FINAL, se limpian los campos
				// de nombre de cliente, dirección e identificación.
				if(codigoIva == MCategoriaIva.CONSUMIDOR_FINAL) {
					mTab.setValue("NombreCli", null);
					mTab.setValue("Invoice_Adress", null);
					mTab.setValue("NroIdentificCliente", null);
				}

			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "bPartner", e);
			return e.getLocalizedMessage();
		}

		Integer letraId = null;
		/*
		 * @Author: Luciano Villalba - Disytel @Fecha:17-ago-2006 @Comentario:
		 * Asigna la letra al comprobante de acuerdo a la categoria de iva
		 * @Parametros: @return:
		 */
		mTab.setValue("C_Letra_comprobante_ID", null);
		if (categoriaIva != 0) {

			// busco la categoria de la empresa
			
			try {
				
				// dREHER - Tomo el ID del tab del comprobante
				int categoriaIvaClient = darCategoriaIvaClient((Integer)mTab.getValue("AD_Org_ID"));
				
				if (IsSOTrx) { // partner -> customer, empresa -> vendor
					letraId = darLetraComprobante(categoriaIva, categoriaIvaClient);
				} else { // empresa -> customer, partner -> vendor
					letraId = darLetraComprobante(categoriaIvaClient, categoriaIva);
				}

				mTab.setValue("C_Letra_comprobante_ID", letraId);
				
				// HARDCODED: para el proveedor Responsable Monotributo, se
				// setea la letra B.
				try {
					if(mTab.getValue("C_DocTypeTarget_ID") == null){
						return "";
					}
					
					int C_DocTypeTarget_ID = Integer.parseInt(mTab.getValue(
							"C_DocTypeTarget_ID").toString());
					
					// TODO: Eliminar hardcode
					
					int letra_A_ID = MLetraComprobante.buscarLetraComprobante(
							Env.getAD_Client_ID(ctx), "A", null)
							.getC_Letra_Comprobante_ID();
					int letra_B_ID = MLetraComprobante.buscarLetraComprobante(
							Env.getAD_Client_ID(ctx), "B", null)
							.getC_Letra_Comprobante_ID();
					
					// "Responsable monotributo"
					int categoria_5_ID = (MCategoriaIva.buscarCodigo(5, null))[0].getC_Categoria_Iva_ID();
					
					// Categoria 0: 
					// - Responsable 'M'
					// - Responsable 'M' con CBU informado
					HashSet<Integer> categs_0 = new HashSet<Integer>();
					
					for (MCategoriaIva c : MCategoriaIva.buscarCodigo(5, null))
						categs_0.add(c.getC_Categoria_Iva_ID());

					// TODO: Eliminar hardcode

					// 1.9 =>
					// 100000056	1000018	0	Y	Nota de debito proveedor	API	N	Y	NDP
					// 1000441		1000018	0	Y	Nota de credito proveedor	API	N	N	NCP
					HashSet<Integer> docTypesNP = new HashSet<Integer>();
					
					for (MDocType d : MDocType.getOfDocBaseType(Env.getCtx(), MDocType.DOCBASETYPE_APInvoice))
						if (d.getName().equalsIgnoreCase("Nota de debito proveedor") || // <= Hardcode HERE 
								d.getName().equalsIgnoreCase("Nota de credito proveedor")) 
							docTypesNP.add(d.getC_DocType_ID());
					
					// TODO: Eliminar hardcode

					if (categoriaIva == categoria_5_ID && docTypesNP.contains(C_DocTypeTarget_ID)) {
						mTab.setValue("C_Letra_comprobante_ID", letra_B_ID); 
					} else if(categs_0.contains(categoriaIva) && docTypesNP.contains(C_DocTypeTarget_ID)) {
						mTab.setValue("C_Letra_comprobante_ID", letra_A_ID); 
					} else {
						
					}
					
				} catch (Exception ex) {
					log.log(Level.SEVERE, "Error al buscar el tipo de documento destino: "
							+ ex);
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "bPartner", e);
				return e.getLocalizedMessage();
			}
		}
		X_C_Letra_Comprobante letra = null;
		if (letraId != null)
			letra = new X_C_Letra_Comprobante(Env.getCtx(), letraId.intValue(), trxName);

		Object puntoDeVenta = mTab.getValue("PuntoDeVenta");
		
		if (!IsSOTrx && puntoDeVenta != null) {
			// TODO: El tipo de datos del campo en los metadatos es inestable... por eso hago toString.
			int pto = new BigDecimal(puntoDeVenta.toString()).intValue();
			
			int AD_Org_ID = Env.getContextAsInt(ctx, WindowNo, "AD_Org_ID");
			int AD_Client_ID = Env.getContextAsInt(ctx, WindowNo,
					"AD_Client_ID");
			// Env.getContextAsDate(ctx, WindowNo, "DateInvoiced");
			if (C_BPartner_ID != null
					&& (letra == null || !letra.getLetra().equals("C")))
				buscarCai(ctx, mTab, AD_Client_ID, AD_Org_ID, C_BPartner_ID
						.intValue(), pto);
		}

		return "";
    }    // bPartner
    
	/**
	 @Author: Jorge Vidal - Disytel 
	 @Fecha: 03/10/2006
	 @Comentario: Setea la lista de precios por default que tiene 
	 la sucursal. Si no la encuentra, pone la del enviroment.
	 @Parametros:
	 ***/

	public String setPriceList(Properties ctx, int WindowNo, MTab mTab,
			MField mField, Object value) {

		String sql = "SELECT M_PRICELIST_ID FROM M_PRICELIST WHERE "
				+ " ISACTIVE='Y' AND AD_CLIENT_ID=?  AND (AD_ORG_ID=? OR AD_ORG_ID=0) AND"
				+ " ISDEFAULT='Y' AND ISSOPRICELIST=?";

		String IsSOTrx = Env.getContext(ctx, WindowNo, "IsSOTrx");
		try {
			PreparedStatement pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1, Env.getAD_Client_ID(ctx));
			pstmt.setInt(2, Env.getAD_Org_ID(ctx));
			pstmt.setString(3, IsSOTrx);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				mTab.setValue("M_PriceList_ID", new Integer(rs.getInt(1)));
				
			}
			else { // get default PriceList
				int i = Env.getContextAsInt(ctx, "#M_PriceList_ID");
				if (i != 0)
					mTab.setValue("M_PriceList_ID", new Integer(i));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "setPriceList", e);
			setCalloutActive(false);
			return e.getLocalizedMessage();
		}
		return "";
	}

	public String numeroComprobante(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value)
	{
		String trxName = null;
		boolean IsSOTrx = Env.getContext(ctx, WindowNo, "IsSOTrx").equals("Y");
		Object ptoObj = mTab.getValue("PUNTODEVENTA");

		Integer comprobante = value != null ? new BigDecimal(value.toString()).intValue() : null;
		Integer pto = ptoObj != null ? new BigDecimal(ptoObj.toString()).intValue() : null;
		Integer letra_comprobante_id = (Integer)mTab.getValue("c_letra_comprobante_id");
		String letra = "";
		
		if (letra_comprobante_id != null) {
			letra = DB.getSQLValueString(trxName, " select letra from c_letra_comprobante where c_letra_comprobante_id = ? ", letra_comprobante_id);
			if (letra == null)
				letra = "";
		}
		
		if(comprobante != null && comprobante.toString().length() < 9 && pto != null){
			setNroDoc(mTab, pto.intValue(), comprobante.intValue(), letra, IsSOTrx);

			// Si la factura es manual y el nro de comprobante del último
			// comprobante impreso fiscalmente del tipo de documento seleccionado es
			// igual al actual entonces warning por mismo nro de comprobante
			Boolean manualDocumentNo = null;
		    Object manualObj = mTab.getValue("ManualDocumentNo");
		    if(manualObj != null){
		    	if(manualObj instanceof String){
		      		manualDocumentNo = ((String)mTab.getValue("ManualDocumentNo")).equals("Y");
		      	}
		      	else{
		      		manualDocumentNo = (Boolean)mTab.getValue("ManualDocumentNo");
		      	}
		    }
			if(manualDocumentNo != null && manualDocumentNo){
				// Tipo de documento
				Integer nroComprobante = MDocType
						.getLastFiscalDocumentNumeroComprobantePrinted(ctx,
								(Integer) mTab.getValue("C_DocTypeTarget_ID"),
								trxName);
				if (nroComprobante != null
						&& nroComprobante.intValue() + 1 == comprobante.intValue()) {
					mTab.setCurrentRecordWarning(Msg.getMsg(ctx, "SameLastFiscalDocumentNroCompPrinted"));
				}
				else{
					mTab.clearCurrentRecordWarning();
				}
			}
			else{
				mTab.clearCurrentRecordWarning();
			}
		}
		
		return "";
	}	
	
	/*
	 * @Author: Luciano Villalba - Disytel 
	 * @Fecha:22-ago-2006 
	 * @Comentario: Actualiza el nro de documento
	 * @Parametros: 
	 * @return:
	 */
	private String setNroDoc(MTab mTab, int pto, int comp, String letra, boolean IsSOTrx)
	{
		mTab.setValue("NUMERODEDOCUMENTO", GenerarNumeroDeDocumento(pto, comp, letra, IsSOTrx));
		
		return "";
	}
	
	/** Sobrecarga para compatibilidad con otras invocaciones a este metodo */
	public static String GenerarNumeroDeDocumento(int pto, int comp, String letra, boolean IsSOTrx)
	{
		return GenerarNumeroDeDocumento(pto, comp, letra, IsSOTrx, true);
	}
	
	public static String GenerarNumeroDeDocumento(int pto, int comp, String letra, boolean IsSOTrx, boolean useSuggestion) 
	{
		String nro = "";
		String comprobante = new Integer(comp).toString();
		String strPto = new Integer(pto).toString();
		
		// Numero de comprobante
		
		while(comprobante.length() < 8){
			comprobante = "0"+comprobante;
		}
		
		// Punto de ventas
		
		if (strPto.length() < 4) 
			strPto = ("0000" + strPto).substring(strPto.length(), strPto.length() + 4);
		
		// Letra: Solo factura de ventas
		// disytel - Anulado: no debería ser así. en las facturas de proveedor
		// el número de documento debe tener la letra de comprobante.
		//if (!IsSOTrx || letra == null)
		//	letra = "";
		letra = (letra == null? "" : letra);
		// Para las facturas de proveedor el numero no debe ser sugerido (la anulacion genera documento inverso con mismo numero) 
		nro = (useSuggestion && IsSOTrx ? "<" : "") + letra + strPto + comprobante + (useSuggestion && IsSOTrx ? ">" : "" );
		
		return nro;
	}
	
	/**
	 * @Author: Luciano Villalba - Disytel 
	 * @Fecha:22-ago-2006 
	 * @Comentario: Busca en la tabla de cai el numero de cai
	 * @Parametros: 
	 * @return:
	 */
	private String buscarCai(Properties ctx, MTab mTab ,int AD_Client_ID, int AD_Org_ID, int part, int pto)
	{	
		if(part != 0 && pto != 0){
			Timestamp date = (Timestamp)mTab.getValue("DateInvoiced");
			mTab.setValue("CAI",null);
			mTab.setValue("DATECAI",null);
			
			String sql = "SELECT * FROM C_BPartner_CAI WHERE AD_CLIENT_ID = ? " +
					"AND posnumber = ? AND C_BPARTNER_ID = ? AND datecai::date >= ?::date " +
					"ORDER BY datecai ";
			try
			{
				PreparedStatement pstmt = DB.prepareStatement(sql.toString(), null, true);
				pstmt.setInt(1,AD_Client_ID);
				pstmt.setInt(2,pto);
				pstmt.setInt(3,part);
				pstmt.setTimestamp(4, date);
				
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()){
					mTab.setValue("CAI",rs.getString("cai"));
					mTab.setValue("DATECAI",rs.getTimestamp("datecai"));
				}
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, "puntoVenta", e);
				return e.getLocalizedMessage();
			}
		}
		return "";
	}
	
	public String tax (Properties ctx, int WindowNo, MTab mTab, MField mField, Object value)
	{
		String trxName = null;
		
		String column = mField.getColumnName();
		if (value == null)
			return "";

		//	Check Product
		int M_Product_ID = 0;
		if (column.equals("M_Product_ID"))
			M_Product_ID = ((Integer)value).intValue();
		else
			M_Product_ID = Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");
		int C_Charge_ID = 0;
		if (column.equals("C_Charge_ID"))
			C_Charge_ID = ((Integer)value).intValue();
		else
			C_Charge_ID = Env.getContextAsInt(ctx, WindowNo, "C_Charge_ID");
		log.fine("Product=" + M_Product_ID + ", C_Charge_ID=" + C_Charge_ID);
		if (M_Product_ID == 0 && C_Charge_ID == 0)
			return amt (ctx, WindowNo, mTab, mField, value);	//

		//	Check Partner Location
		int shipC_BPartner_Location_ID = Env.getContextAsInt(ctx, WindowNo, "C_BPartner_Location_ID");
		if (shipC_BPartner_Location_ID == 0)
			return amt (ctx, WindowNo, mTab, mField, value);	//
		log.fine("Ship BP_Location=" + shipC_BPartner_Location_ID);
		int billC_BPartner_Location_ID = shipC_BPartner_Location_ID;
		log.fine("Bill BP_Location=" + billC_BPartner_Location_ID);

		//	Dates
		Timestamp billDate = Env.getContextAsDate(ctx, WindowNo, "DateInvoiced");
		log.fine("Bill Date=" + billDate);
		Timestamp shipDate = billDate;
		log.fine("Ship Date=" + shipDate);

		int AD_Org_ID = Env.getContextAsInt(ctx, WindowNo, "AD_Org_ID");
		log.fine("Org=" + AD_Org_ID);

		int M_Warehouse_ID = Env.getContextAsInt(ctx, "#M_Warehouse_ID");
		log.fine("Warehouse=" + M_Warehouse_ID);

		/**
		 * Luciano Disytel
		 * Si el proveedor es exento el impuesto es exento
		 * */
		int C_Tax_ID = 0;
		int C_BPartner_ID = Env.getContextAsInt(ctx, WindowNo, "C_BPartner_ID");
		boolean isSOTrx = Env.getContext(ctx, WindowNo, "IsSOTrx").equals("Y");
		
		// Obtengo el impuesto en base a la entidad comercial
		MTax tax = getTax(ctx, isSOTrx, C_BPartner_ID, AD_Org_ID, null);
		if(tax != null){
			C_Tax_ID = tax.getID();
		}
		
		if(C_Tax_ID <= 0){
			C_Tax_ID = Tax.get(ctx, M_Product_ID, C_Charge_ID, billDate, shipDate,
					AD_Org_ID, M_Warehouse_ID, billC_BPartner_Location_ID, shipC_BPartner_Location_ID, isSOTrx);
		}
		
		log.info("Tax ID=" + C_Tax_ID);
		//
		if (C_Tax_ID == 0)
			mTab.fireDataStatusEEvent(CLogger.retrieveError());
		else
			mTab.setValue("C_Tax_ID", new Integer(C_Tax_ID));
		//
		return amt (ctx, WindowNo, mTab, mField, value);
	}	//	tax
	
	
	public static MTax getTax(Properties ctx, boolean isSOTrx, Integer bPartnerID, Integer orgID, String trxName){
		MBPartner partner = new MBPartner(ctx,bPartnerID,trxName);
		MTax tax = null;
		int C_Tax_ID = 0;
		if(!Util.isEmpty(orgID, true)) {
			MOrgInfo oi = MOrgInfo.get(ctx, orgID);
			if(!Util.isEmpty(oi.getC_Categoria_IVA_ID(),true)) {
				X_C_Categoria_Iva categoria_Iva = new X_C_Categoria_Iva(ctx, oi.getC_Categoria_IVA_ID(), trxName);
				C_Tax_ID = categoria_Iva.getC_Tax_ID();
			}
		}
		
		if(C_Tax_ID <= 0 && partner.getC_Categoria_Iva_ID() > 0){
			X_C_Categoria_Iva categoria_Iva = new X_C_Categoria_Iva(ctx, partner.getC_Categoria_Iva_ID(), trxName);
			// 1) verificamos en letra acepta iva
			Integer clientCatIVA = darCategoriaIvaClient(orgID);
			MLetraAceptaIva laiva = darLetraAceptaIVA(ctx, isSOTrx ? categoria_Iva.getID() : clientCatIVA,
					isSOTrx ? clientCatIVA : categoria_Iva.getID(), trxName);
			C_Tax_ID = laiva != null?laiva.getC_Tax_ID():0;
			// 2) el impuesto de la categoría de iva
			if(C_Tax_ID <= 0){
				C_Tax_ID = categoria_Iva.getC_Tax_ID();
			}
		}
		if(C_Tax_ID > 0){
			tax = new MTax(ctx, C_Tax_ID, trxName);
		}
		return tax;
	}
	
	public String documentNo( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		try {
			String docNo = ((String)value);
			
			// <A000100000001>
			// 012345678901234
			
			// A000100000001
			// 0123456789012
			
			HashMap<String, Object> hm = DividirDocumentNo(Env.getAD_Client_ID(ctx), docNo);
			Integer bPartnerID = (Integer)mTab.getValue("C_BPartner_ID");
			for (String k : hm.keySet()) { 
				// Solo asigna la letra del comprobante si no se ha ingresado
				// la entidad comercial. Si se ingreso la entidad comercial
				// la letra se calcula a partir de las categorias de iva
				// de la EC y la Compañia.
				if(bPartnerID == null || !k.equals("C_Letra_Comprobante_ID") )
					mTab.setValue(k, hm.get(k));
			}	
		} catch (Exception e) {
			
		}
		return "";
	}
	
	/**
	 * Valida si el nro de documento matchea con el formato de nros de
	 * documentos de facturas fiscales
	 * 
	 * @param documentNo
	 * @return true si matchea, false caso contrario
	 */
	public static boolean isValidFiscalDocumentNo(String documentNo){
		// 1) Longitud del nro de documento
		String realDocumentNo = documentNo.replace(" ", "0");
		if(realDocumentNo.length() != 13){
			return false;
		}
		// Pasar la primer letra a mayúscula por las dudas
		String firstLetter = realDocumentNo.charAt(0)+"";
		String firstLetterMayus = firstLetter.toUpperCase();
		realDocumentNo = realDocumentNo.replaceFirst(firstLetter, firstLetterMayus);
		
		// 2) Por patrones
		boolean fiscalMatches = Pattern.matches("[ABC]{1}\\d{4}\\d{8}{1}",
				realDocumentNo);
		if(!fiscalMatches){
			return false;
		}
		
		// 3) Diviendo el nro de documento
		Map<String, Object> documentNoParts = DividirDocumentNo(
				Env.getAD_Client_ID(Env.getCtx()), realDocumentNo);
		if(documentNoParts.size() < 3){
			return false;
		}
		
		// 4) El punto de venta no puede ser 0
		if (((Integer) documentNoParts.get("PuntoDeVenta")).intValue() == 0) {
			return false;
		}
		
		// 5) El nro de comprobante no puede ser 0
		if (((Integer) documentNoParts.get("NumeroComprobante")).intValue() == 0) {
			return false;
		}
		
		return true;
	}
	
	public String puntoVenta(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value){
		if(value == null){
			return "";
		}
		int pto = ((Integer)value).intValue();
		Integer partner = (Integer)mTab.getValue("C_BPartner_ID");
		int AD_Org_ID = Env.getContextAsInt(ctx, WindowNo, "AD_Org_ID");
		int AD_Client_ID = Env.getContextAsInt(ctx, WindowNo, "AD_Client_ID");
		
		X_C_Letra_Comprobante letra = null;
		Integer letraId = (Integer)mTab.getValue("C_Letra_Comprobante_ID");
		if( letraId != null)
			 letra = new X_C_Letra_Comprobante(ctx,letraId.intValue(),null);
		
		
		if(partner != null && (letra == null || !letra.getLetra().equals("C")))	{
			return buscarCai(ctx, mTab, AD_Client_ID, AD_Org_ID,partner.intValue(),pto);
		}
		
		return "";
	}
	
}
