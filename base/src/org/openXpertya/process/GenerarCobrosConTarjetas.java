package org.openXpertya.process; 

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;

import org.fidelius.pojos.CuponPago;
import org.openXpertya.model.AllocationGenerator;
import org.openXpertya.model.AllocationGeneratorException;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_C_Bank;
import org.openXpertya.process.customImport.utils.Utilidades;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Automatiza el pago de facturas segun cupon informado por Fidelius
 * @author dREHER
 * @version 1.0
 * dREHER
 */
public class GenerarCobrosConTarjetas extends SvrProcess {

	private int p_AD_Org_ID; // Organizacion
	private Timestamp p_Date_From; // Fecha desde.
	private Timestamp p_Date_To; // Fecha hasta.
	private String nombreComercio; // Nombre del Comercio en la ORGInfo formato Fidelius-Clover
	private int C_ExternalService_ID = 0;
	private int C_PosJournal_ID = 0;
	private static final String TIPOIMPORTACION = "Fidelius";
	/** Preference de tolerancia de horas para determinar la fecha  */
	public static String HOUR_TOLERANCE_PREFERENCE_NAME = "POSJournalHourTolerance";
	
	private int C_DocTypeHDR_ID = 0;
	private int C_DocTypeRC_ID = 0;
	
	@Override
	protected void prepare() {
		
		ProcessInfoParameter[] params = getParameter();
		for (int i = 0; i < params.length; i++) {
			String name = params[i].getParameterName();
			if (params[i].getParameter() == null) {
				;
			} else if (name.equalsIgnoreCase("FechaDesde")) {
				p_Date_From = (Timestamp) params[i].getParameter();
			} else if (name.equalsIgnoreCase("FechaHasta")) {
				p_Date_To = (Timestamp) params[i].getParameter();
			} else if (name.equalsIgnoreCase("AD_Org_ID")) {
				p_AD_Org_ID = params[i].getParameterAsInt();
			} else {
				log.log(Level.SEVERE, "GenerarCobrosConTarjeta.prepare - Unknown Parameter: " + name);
			}
		}
	
	}

	@Override
	protected String doIt() throws Exception {
		String msg = "";
		
		nombreComercio = DB.getSQLValueString(get_TrxName(), "SELECT NombreComercio FROM AD_OrgInfo WHERE AD_Org_ID=?", p_AD_Org_ID);
		if(Util.isEmpty(nombreComercio, true))
			return "No se encontro la configuracion de Nombre de Comercio en la configuracion de la Organizacion indicada!";

		ArrayList<CuponPago>cupones = CargarPagos();
		if(cupones.isEmpty())
			return "No se encontraron cupones pendientes para la sucursal y periodo indicado!";
		
		C_ExternalService_ID = Utilidades.getExternalServiceByName(TIPOIMPORTACION);
		if(C_ExternalService_ID <= 0)
			return "No se encontro un servicio externo llamado " + TIPOIMPORTACION;
		
		
		C_PosJournal_ID = getCajaDiariaID();
		if(C_PosJournal_ID <= 0)
			return "Debe tener una caja abierta para poder generar pagos!";
		
		// Precargo tipo de documentos Medio de pago y Recibo de cliente
		C_DocTypeHDR_ID=DB.getSQLValue(get_TrxName(), "SELECT C_DocType_ID FROM C_DocType " +
				"WHERE IsSoTRX='Y' AND DocBaseType='ARR' AND DocTypeKey='CRSEC01' AND IsActive='Y'");
		
		C_DocTypeRC_ID=DB.getSQLValue(get_TrxName(), "SELECT C_DocType_ID FROM C_DocType " +
				"WHERE IsSoTRX='Y' AND DocBaseType='ARR' AND DocTypeKey='CR' AND IsActive='Y'");
		
		msg = GenerarPagos(cupones);
		
		return msg;
	}

	private int getCajaDiariaID() throws Exception {
		int C_PosJId = 0;
		
		if (MPOSJournal.isActivated() && Util.isEmpty(0, true) ) {  // dREHER ptoVta==0 debe considerarse para leer config desde Organizacion y no desde caja abierta
			MPOSJournal caja = MPOSJournal.get(Env.getCtx(), 
												Env.getAD_User_ID(getCtx()), 
												Env.getDate(), 
												new String[]{"IP"}, 
												get_TrxName());
			if(caja!=null) {
				C_PosJId = caja.getC_POSJournal_ID();
			}else
				throw new Exception("No se encontro una caja abierta para este usuario!");
			
		}else if (MPOSJournal.isActivated()) {
			MPOSJournal caja = getPOSJournal(Env.getCtx(), 
					Env.getAD_Org_ID(getCtx()), 
					null,
					Env.getDate(), 
					new String[]{"IP"}, 
					get_TrxName());
			
			if(caja!=null) {
				C_PosJId = caja.getC_POSJournal_ID();
			}else
				throw new Exception("No se encontro una caja abierta para esta sucursal!");
		}
		
		return C_PosJId;
	}
	
	public MPOSJournal getPOSJournal(Properties ctx, int orgID, Integer posID, Timestamp date, String[] docStatus, String trxName) {
		MPOSJournal journal = null;
		StringBuffer sql = new StringBuffer(); 
		sql.append("SELECT * ")
		   .append("FROM C_POSJournal ")
		   .append("WHERE AD_Org_ID = ? ")
		   .append(  "AND date_trunc('day',DateTrx) = date_trunc('day',?::date) ");
		if(!Util.isEmpty(posID, true)){
			sql.append(" AND c_pos_id = ").append(posID).append(" ");
		}
		
		// Filtro de los dosStatus
		if (docStatus != null && docStatus.length > 0) {
			sql.append( "AND DocStatus IN (");
			for (int i = 0; i < docStatus.length; i++) {
				String status = docStatus[i];
				sql.append("'").append(status).append("'");
				if (i < docStatus.length - 1) {
					sql.append(",");
				}
			}
			sql.append(") "); 
		}
		sql.append("ORDER BY Created DESC");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// Verificar si existe la preference para la tolerancia de horas
		String hourToleranceValue = MPreference.searchCustomPreferenceValue(
				HOUR_TOLERANCE_PREFERENCE_NAME, Env.getAD_Client_ID(ctx),
				Env.getAD_Org_ID(ctx), Env.getAD_User_ID(ctx), true);
		if(!Util.isEmpty(hourToleranceValue, true)){
			Integer hourTolerance = Integer.parseInt(hourToleranceValue);
			Calendar newDate = Calendar.getInstance();
			// Busco la fecha actual en la base
			Timestamp actualDate = DB.getDBTimestamp(trxName);
			// Seteo la fecha parámetro
			newDate.setTimeInMillis(actualDate.getTime());
			// Restar la cantidad de horas y tomar la fecha resultante para la
			// comparación
			newDate.add(Calendar.HOUR_OF_DAY, hourTolerance * -1);
			date = new Timestamp(newDate.getTimeInMillis());
		}
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, orgID);
			pstmt.setTimestamp(2, date);
			
			rs = pstmt.executeQuery();
			if (rs.next()) {
				journal = new MPOSJournal(ctx, rs, trxName);
			}
			
		} catch (Exception e) {
			System.out.println("Error getting POS Journal. OrgID="
					+ orgID + ", Date=" + date);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) { }
		}
		return journal;
	}

	/**
	 * Lee los cupones leidos y si encuentra una unica factura asociada, llama al metodo para crear el pago y la asignacion
	 * correspondiente
	 * @param cupones
	 * @return
	 * dREHER
	 * @throws AllocationGeneratorException 
	 */
	private String GenerarPagos(ArrayList<CuponPago> cupones) throws AllocationGeneratorException {
		String result = "";
		
		for(CuponPago cp: cupones) {
		
			// validar que solo encuentre una FC de estas caracteristicas, si encuentra mas de una saltear...
			int C_Invoice_ID = -1;
			String sql = "SELECT C_Invoice_ID " +
					" FROM C_Invoice " +
					" WHERE DocumentNo iLike '%" + cp.getFactura() + "'" +
					" AND GrandTotal=" + cp.getImporte() + " " +
					" AND InvoiceOpen(C_Invoice_ID, null) > 0 " +
					" AND AD_Org_ID=? ORDER BY DateInvoiced DESC";
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			int i = 0;
			
			try {
				pstmt = DB.prepareStatement(sql, get_TrxName(), true);
				pstmt.setInt(1, p_AD_Org_ID);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					i++;
					C_Invoice_ID = rs.getInt("C_Invoice_ID");
				}
				
			}catch(Exception ex) {
				log.warning("Se produjo un error al buscar facturas");
			}finally {
				DB.close(rs, pstmt);
				rs = null; pstmt=null;
			}
			
			if(C_Invoice_ID > 0 && i == 1) {
				
				result += "<br>" + CrearPago(cp, C_Invoice_ID);
				
			}else
				if(i > 1) {
					result += "<br>No se encontro una unica factura para el cupon :" + cp.getNroCupon() + " FC: " + cp.getFactura();
				}else
					result += "<br>No se encontro factura con saldo abierto relacionada al cupon: " + cp.getNroCupon() + " FC: " + cp.getFactura();
			
		}
		
		return result;
	}

	/**
	 * Este metodo crea el pago y la asignacion correspondiente
	 * @param cp CuponPago
	 * @param c_Invoice_ID
	 * dREHER
	 * @throws AllocationGeneratorException 
	 */
	private String CrearPago(CuponPago cp, int c_Invoice_ID) throws AllocationGeneratorException {
		String result = "";
		Timestamp dateTrx = cp.getFechaPago();

		// Instancio factura a pagar
		MInvoice inv = new MInvoice(Env.getCtx(), c_Invoice_ID, get_TrxName());
		
		// Instancio la entidad comercial
		MBPartner bp = new MBPartner(Env.getCtx(), inv.getC_BPartner_ID(), get_TrxName());
		
		// Creo la asignacion
		MAllocationHdr hdr = new MAllocationHdr( getCtx(), false, dateTrx, inv.getC_Currency_ID(), "", get_TrxName());
		hdr.setAD_Org_ID(p_AD_Org_ID);
		hdr.setDescription("Asignacion automatica desde cupones pendientes. Cupon # " + cp.getNroCupon() + " Id #" + cp.getId());
		hdr.setDateTrx(dateTrx); // TODO: ver si se crea con fecha de hoy o del pago ?
		hdr.setC_BPartner_ID(inv.getC_BPartner_ID());
		
		hdr.setC_DocType_ID(C_DocTypeHDR_ID);
		hdr.setAllocationType(MAllocationHdr.ALLOCATIONTYPE_CustomerReceipt);
		
		// Creo el pago
		MPayment pay = new MPayment(Env.getCtx(), 0, get_TrxName());
		pay.setAD_Org_ID(p_AD_Org_ID);
		pay.setAmount(118, cp.getImporte());
		pay.setDateTrx(dateTrx);
		pay.setDateAcct(dateTrx);
		pay.setC_Currency_ID(118);
		pay.setC_BPartner_ID(inv.getC_BPartner_ID());
		pay.setTenderType(MPayment.TENDERTYPE_CreditCard);
		pay.setCreditCardNumber(cp.getNroTarjeta());
		pay.setTrxType("S");
		pay.setA_Name(bp.getName());
		pay.setA_Street(getDomicilio(bp.getPrimaryC_BPartner_Location_ID()));
		pay.setCouponNumber(cp.getNroCupon());
		pay.setCouponBatchNumber(cp.getNroLote());
		pay.setPosnet(String.valueOf(cp.getCuotas()));
		pay.setAuditStatus(MPayment.AUDITSTATUS_ToVerify);
		
		// pay.setC_POSJournal_ID();
		
		String tipoTarjeta = Utilidades.getDataExtraSEbyName(C_ExternalService_ID, "Tipo Tarjeta", cp.getTarjeta());
		if(tipoTarjeta==null) {
			return "No se encontro la tarjeta NOMBRE=" + cp.getTarjeta() + " en la configuracion de Servicios Externos!";
		}
		int M_EntidadFinancieraPlan_ID = DB.getSQLValue(get_TrxName(), 
				"SELECT M_EntidadFinancieraPlan_ID " +
				"FROM M_EntidadFinancieraPlan efp " +
				"INNER JOIN M_EntidadFinanciera ef ON ef.M_EntidadFinanciera_ID=efp.M_EntidadFinanciera_ID " +
				"WHERE efp.AD_Org_ID=? " +
				"AND efp.cuotascobro=" + cp.getCuotas() + " " +
				"AND ef.EstablishmentNumber iLIKE '%" + cp.getCodigoComercio() + "%' " +
				"AND ef.FinancingService='" + tipoTarjeta + "' "
				,p_AD_Org_ID);
		
		if(M_EntidadFinancieraPlan_ID <= 0)
			return "No se encontro el plan de financiacion para el comercio: " + cp.getCodigoComercio() + 
				 " tarjeta: " + tipoTarjeta;

		MEntidadFinancieraPlan efp = new MEntidadFinancieraPlan(Env.getCtx(), M_EntidadFinancieraPlan_ID, get_TrxName());
		MEntidadFinanciera ef = new MEntidadFinanciera(Env.getCtx(), efp.getM_EntidadFinanciera_ID(), get_TrxName());
		
		pay.setM_EntidadFinancieraPlan_ID(M_EntidadFinancieraPlan_ID);

		String CreditCardType = ef.getCreditCardType();
		pay.setCreditCardType(CreditCardType);
		
		int C_BankAccount_ID = ef.getC_BankAccount_ID();
		int C_Bank_ID = DB.getSQLValue(get_TrxName(), "SELECT C_Bank_ID FROM C_BankAccount WHERE C_BankAccount_ID=?", C_BankAccount_ID);
		
		X_C_Bank bank = new X_C_Bank(Env.getCtx(), C_Bank_ID, get_TrxName());
		pay.setA_Bank(bank.getName());
		pay.setC_BankAccount_ID(C_BankAccount_ID);
		
		
		// TODO: ver si debo validar DateFrom and DateTo (rangos de fechas validas)
		int C_POSPaymentMedium_ID = DB.getSQLValue(get_TrxName(), 
				"SELECT C_POSPaymentMedium_ID FROM C_POSPaymentMedium WHERE M_EntidadFinanciera_ID=? AND IsActive='Y'", 
				efp.getM_EntidadFinanciera_ID());
		pay.setC_POSPaymentMedium_ID(C_POSPaymentMedium_ID);
		pay.setC_DocType_ID(C_DocTypeRC_ID);
		pay.setC_POSJournal_ID(C_PosJournal_ID);
		
		// Guardar y completar el pago
		if(DocumentEngine.processAndSave(pay, DocAction.ACTION_Complete, false)) { 
			if(pay.save())
				result += "Se creo el medio de cobro: <b>" + pay.getDocumentNo() + "</b>";
			else
				return "No se pudo guardar el medio de cobro: " + pay.getDocumentNo() + " " + pay.getProcessMsg();
		}else
			return "No se pudo completar el medio de cobro: " + pay.getDocumentNo() + " Error: " + pay.getProcessMsg();

		if(!pay.getDocStatus().equals(MPayment.ACTION_Complete)) {
			result += "Error: " + pay.getProcessMsg() + "<br>";
			return "No se pudo completar el medio de cobro: " + pay.getDocumentNo() + " Estado: " + pay.getDocStatus();
			
		}
		
		
		// Cargo el generador de asignaciones
		AllocationGenerator ag = new AllocationGenerator(Env.getCtx(), hdr, get_TrxName());
		
		// Agrego el pago
		ag.addCreditPayment(pay.getC_Payment_ID(), pay.getPayAmt());
		
		// Agrego la fatura
		ag.addDebitInvoice(c_Invoice_ID, inv.getGrandTotal());
		
		// Genero Lineas de asignacion y completo
		ag.generateLines();
		ag.completeAllocation();
		
		result += "<br>Se creo el recibo de cliente: <b>" + hdr.getDocumentNo() + "</b>";
		
		return result;
		
	}

	private String getDomicilio(int C_BPartner_Location_ID) {
		String domicilio = "";
		MBPartnerLocation bpl = new MBPartnerLocation(Env.getCtx(), C_BPartner_Location_ID, get_TrxName());
		MLocation loc = new MLocation(Env.getCtx(), bpl.getC_Location_ID(), get_TrxName());
		domicilio = loc.getAddress1().trim() + " " + loc.getRegionName();
		return domicilio;
	}

	/**
	 * Lee los cupones pendientes importados desde Fidelius
	 * @return
	 * dREHER
	 */
	private ArrayList<CuponPago> CargarPagos() {
		
		ArrayList<CuponPago>cupones = new ArrayList<CuponPago>();
		
		String sql = "SELECT * FROM I_FideliusPendientes WHERE fechaoper::date BETWEEN ?::date AND ?::date " +
				" AND LOWER(nombre_comerc)=LOWER(?) " +
				" AND NOT factura ISNULL";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			pstmt = DB.prepareStatement(sql, get_TrxName(), true);
			pstmt.setTimestamp(1, p_Date_From);
			pstmt.setTimestamp(2, p_Date_To);
			pstmt.setString(3, nombreComercio);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				
				CuponPago cp = new CuponPago();
				cp.setCodigoComercio(rs.getString("CodCom"));
				cp.setCuotas(rs.getInt("Cuota_Tipeada"));
				cp.setFactura(rs.getString("Factura").replace("-", ""));
				cp.setFechaPago(rs.getTimestamp("FechaOper"));
				cp.setImporte(rs.getBigDecimal("Importe"));
				cp.setNroCupon(rs.getString("Ticket"));
				cp.setNroLote(rs.getString("nroLote"));
				cp.setNroTarjeta(rs.getString("NroTarjeta"));
				cp.setTarjeta(rs.getString("Tarjeta"));
				cp.setId(rs.getInt("id"));
				
				cupones.add(cp);
			}
			
		}catch(Exception ex) { 
			log.warning("Se produjo un error al procesar cupones pendientes" + ex.toString());
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt=null;
		}
		
		return cupones;
	}

}
