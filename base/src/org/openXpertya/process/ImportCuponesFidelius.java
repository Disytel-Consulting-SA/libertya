package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MCouponsSettlements;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_CouponsSettlements;
import org.openXpertya.model.X_C_CreditCardCouponFilter;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.model.X_I_FideliusCupones;
import org.openXpertya.model.X_M_EntidadFinanciera;
import org.openXpertya.model.X_M_EntidadFinancieraPlan;
import org.openXpertya.process.customImport.utils.Utilidades;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Proceso para realizar matching entre los cupones de la liquidacion y los de fidelius (I_FideliusCupones)
 * @author jdREHER
 * 
 */
public class ImportCuponesFidelius extends SvrProcess {

	private static final Object AUDITSTATUS_Verified = "VE";
	private int m_C_CreditCardCouponFilter_ID;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
		m_C_CreditCardCouponFilter_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {

		if (m_C_CreditCardCouponFilter_ID == 0) {
			throw new IllegalArgumentException("C_CreditCardCouponFilter_ID = 0");
		}

		// Cargo el filtro de cupones correspondiente
		X_C_CreditCardCouponFilter filter = new X_C_CreditCardCouponFilter(getCtx(), m_C_CreditCardCouponFilter_ID, get_TrxName());
		
		// Cargo la liquidacion correspondiente
		MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), filter.getC_CreditCardSettlement_ID(), get_TrxName());
		
		if (settlement.isReconciled()) {
			throw new IllegalArgumentException(Msg.translate(getCtx(),
					"ReconciledSettlement"));
		}
		
		MEntidadFinanciera ef = null;
		if(filter.getM_EntidadFinanciera_ID() > 0)
			ef = new MEntidadFinanciera(Env.getCtx(), filter.getM_EntidadFinanciera_ID(), get_TrxName());

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ce.C_CouponsSettlements_ID ");
		sql.append(" FROM C_CouponsSettlements ce ");
		sql.append(" WHERE ce.C_CreditCardSettlement_ID=? ");
		
		// FILTROS OPCIONALES
		// TODO: debe filtrar por algun campo del registro de filtro ?
		
		// Filtro opcional por fecha "desde"
		if (filter.getTrxDateFrom() != null) {
			sql.append("AND ce.trxdate::date >= ?::date ");
		}
		// Filtro opcional por fecha "hasta"
		if (filter.getTrxDateTo() != null) {
			sql.append("AND ce.trxdate::date <= ?::date ");
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int saved = 0;
		int lost = 0;

		int nCupones = 0;
		int nCuponesConciliados = 0;
		BigDecimal totalCupones = Env.ZERO;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName(), true);

			int aux = 1;
			pstmt.setInt(aux, filter.getC_CreditCardSettlement_ID());
			
			if (filter.getTrxDateFrom() != null) {
				aux++;
				pstmt.setDate(aux, new Date(filter.getTrxDateFrom().getTime()));
			}
			if (filter.getTrxDateTo() != null) {
				aux++;
				pstmt.setDate(aux, new Date(filter.getTrxDateTo().getTime()));
			}

			log.finest("Sql para filtrar cupones: " + sql);
			
			String nroliq = settlement.getSettlementNo();

			String nroComercio = null;
			String tarjeta = null;
			
			if(ef!=null) {
				nroComercio = ef.getEstablishmentNumber();
				tarjeta = ef.getFinancingService();
				
				ArrayList<String> tarjetas = Utilidades.getAttrNames(ef.getM_EntidadFinanciera_ID(), "Tipo Tarjeta", tarjeta, get_TrxName());
				tarjeta = "";
				for(String t: tarjetas)
					tarjeta += (tarjeta.isEmpty()? "'": ",'") + t + "'";
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				
				X_C_CouponsSettlements couponsSettlements = new X_C_CouponsSettlements(getCtx(), rs.getInt(1), get_TrxName());
				String cupon = couponsSettlements.getCouponNo();
				BigDecimal monto = couponsSettlements.getAmount();
				Date dateTrx = new Date(couponsSettlements.getTrxDate().getTime());
				String lote = couponsSettlements.getPaymentBatch();
				
				nCupones++;
				
				// int C_Payment_ID = couponsSettlements.getC_Payment_ID();
				
				/** 
				 Matching entre Libertya y Fidelius (I_FideliusCupones)
				 El matching se debe realizar con:
				 
				 nroliq+fechavta+nrolote+nrocupon+importe
				 
				 Las ventas con extracash en Fidelius se guardan en registros separados, por ende
				 hacer sumario con los campos arriba detallados...
				 
				 Por ende sumarizar por nroliq+fechavta+nrolote+cupon -> importe total a comparar
				 
				 *********************************************************************************************
				 20230126 - Fidelius cambia el nroLote en las liquidaciones, por ende se cambia el matching a:
				 
				 nroLiq+FechaVta+Cupon+Tarjeta+Comercio+Monto
				 
				 Tarjeta y Comercio no son obligatorios y dependera de que en el filtro incluyan la Entidad Financiera, 
				 caso contrario solo se valida:
				 
				 nroLiq+FechaVta+Cupon+Monto
				 
				*/
				
				List<Integer> I_FideliusCupones_IDs = getIDCuponFidelius(nroliq, dateTrx, lote, cupon, tarjeta, nroComercio, monto);
				
				log.warning("Cupones que coinciden: " + I_FideliusCupones_IDs.size());
				
				if(I_FideliusCupones_IDs.size() > 0) {
				
					for(Integer i: I_FideliusCupones_IDs) {
						
						// dREHER Si el cupon esta verificado, marcarlo como conciliado
						if(couponsSettlements.getC_Payment_ID() > 0) {
							MPayment pay = new MPayment(Env.getCtx(), couponsSettlements.getC_Payment_ID(), get_TrxName());
							if(pay.getAuditStatus().equals(MPayment.AUDITSTATUS_Verified))
								couponsSettlements.setIsReconciled(true);

							// Si el cupon esta rechazado en Fidelius, marcarlo como rechazado y monto en negativo
							X_I_FideliusCupones fc = new X_I_FideliusCupones(Env.getCtx(), i, get_TrxName());
							if(fc.getrechazo().equals("Y")) {
								couponsSettlements.setIsRefused(true);
								couponsSettlements.setAmount(couponsSettlements.getAmount().multiply(new BigDecimal(-1)));
							}
							
						}
						couponsSettlements.setInclude(true);
						couponsSettlements.save();
						
						// Marcar en I_FideliusCupones el ID de este cupon libertya
						// para faciliar luego la conciliacion de los cupones en Fidelius
						DB.executeUpdate("UPDATE I_FideliusCupones SET C_CouponsSettlements_ID=" + couponsSettlements.getC_CouponsSettlements_ID() + " WHERE I_FideliusCupones_ID=" + i, get_TrxName());
					}
					
					saved++;
					
					if(couponsSettlements.isReconciled())
						nCuponesConciliados++;
					
					totalCupones = totalCupones.add(monto);
					
				}else
					lost++;
				
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, sql.toString(), e);
		} finally {
			try {
				pstmt.close();
				rs.close();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		
		if(saved > 0) {
			settlement.calculateSettlementCouponsTotalAmount(get_TrxName());
		}
		
		/**
		 * Si el total de cupones esta conciliado y el monto total de cupones coincide con el monto bruto de la liquidacion
		 * marcar la liquidacion como CONCILIADA
		 * dREHER
		 */
		
		if(nCupones == nCuponesConciliados && nCupones > 0) {
			if(totalCupones.compareTo(settlement.getAmount()) == 0) {
				settlement.setIsReconciled(true);
			}
		}
		
		// guardar la info en la liquidacion de tarjetas
		settlement.save();
		
		return "@CuponesFidelius@ Incluidos # " + saved + " Sin incluir # " + lost;
		
	}
	
	private List<Integer> getIDCuponFidelius(String nroliq, Date dateTrx, String nrolote, String cupon, String tarjeta, String nroComercio, BigDecimal monto) {
		List<Integer> ids = new ArrayList<Integer>();
		
		String sql = "SELECT I_FideliusCupones_ID, SUM(imp_vta::numeric) AS imp_vta " +
					" FROM I_FideliusCupones " +
					" WHERE nroliq=? " +
					" AND fvta::date=?::date" +
					" AND nrocupon::numeric=?::numeric";

		/**
			" AND nrolote=?" +
			Fidelius modifica la info del nroLote al enviar la liquidacion, por ende no coincide
			y no se puede utilizar como parte del matching 
		 */
		
		/**
		 * El servicio financiero puede incluir mas de una tarjeta del mismo tipo, por ej:
		 * VISA, VISA DEBITO
		 */
		if(tarjeta!=null)
			sql += " AND tarjeta IN (" + tarjeta + ")";
					
		if(nroComercio!=null)
			sql += " AND num_com='" + nroComercio + "'";
		
		sql += " GROUP BY I_FideliusCupones_ID, nroliq, nrocupon, fvta";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BigDecimal totalCupon = Env.ZERO;
		
		try {
			
			// se quito nro de lote, ya que Fidelius informa un numero de lote cambiado...
			// - lote=" + nrolote +
			log.warning("SQL busca cupon Fidelius= " + sql + " " +
					"nroliq:" + nroliq + " - dateTrx=" + dateTrx + " - cupon=" + cupon +
					" - nroComercio=" + nroComercio + " - tarjeta=" + tarjeta);
			
			pstmt = DB.prepareStatement(sql, get_TrxName(), true);

			pstmt.setString(1, nroliq);
			// pstmt.setString(2, nrolote);
			pstmt.setDate(2, dateTrx);
			pstmt.setString(3, cupon);
			
			rs = pstmt.executeQuery();
			int id = 0;
			
			while (rs.next()) {
				
				id = rs.getInt("I_FideliusCupones_ID");
				
				totalCupon = totalCupon.add(rs.getBigDecimal("imp_vta"));
				
				ids.add(id);
			
			}
			
			// al pasar a otro cupon, si el importe total es coincidente con el registro de cupon Libertya
			// guardamos todos los ID's de la tabla de importacion de cupones Fidelius
			// sino coincide total, limpio los ids
			if(totalCupon.compareTo(monto) != 0)
				ids.clear();
			
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, sql.toString(), e);
		} finally {
			try {
				pstmt.close();
				rs.close();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		
		return ids;
	}

}
