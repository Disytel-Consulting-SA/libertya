package org.openXpertya.process.customImport.fidelius.jobs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MCommissionConcepts;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MExpenseConcepts;
import org.openXpertya.model.MIVASettlements;
import org.openXpertya.model.MPerceptionsSettlement;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.MWithholdingSettlement;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.model.X_I_FideliusLiquidaciones;
import org.openXpertya.process.customImport.fidelius.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.fidelius.http.Get;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Cupon;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.CuponPendiente;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Liquidacion;
import org.openXpertya.process.customImport.utils.Utilidades;
import org.openXpertya.process.customImport.fidelius.mapping.TarjetaCupon;
import org.openXpertya.process.customImport.fidelius.mapping.TarjetaCuponPendiente;
import org.openXpertya.process.customImport.fidelius.mapping.TarjetaPayments;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Proceso de importación. (basado en importacion Visa - CentralPoS)
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class ImportTarjeta extends Import {

	public ImportTarjeta(Properties ctx, String trxName) throws Exception {
		super(EXTERNAL_SERVICE_TARJETA, ctx, trxName);
	}

	@Override
	public String excecute() throws SaveFromAPIException, Exception {
		int areadyExists = 0; // Elementos omitidos.
		int processed = 0; // Elementos procesados.
		int leidos = 0; // Liquidaciones leidas
		String ret = "";
		
		Get get = makeGetter(); // Metodo get para obtener liquidaciones/cupones.
			
		// Fidelius necesita recibir usuario y pass en cada peticion
		get.addQueryParam("user", externalService.getUserName());
		get.addQueryParam("pass", externalService.getPassword());
		
		// Si hay parámetros extra, los agrego.
		if (!extraParams.isEmpty()) {
			get.addQueryParams(extraParams);
		}

		// Log del get...
		get.explain();
		
		if(getType().equals("liquidaciones")) {

			List<Liquidacion> resp = get.getDataLiquidacion(); // Ejecuto la consulta.

			// Por cada resultado, inserto en la tabla de importación.
			for(Liquidacion liq: resp) {
				leidos++;
				log.finest("Liquidacion #" + leidos);

				TarjetaPayments payment = new TarjetaPayments(liq);
				payment.setUniquesFields(new String[] {"nroliq", "num_com", "tarjeta"});
				payment.setUniquesFieldsValues(new String[] {String.valueOf(liq.getNroLiquidacion()), 
						liq.getNroComercio(), 
						liq.getTarjeta()});
				
				int no = payment.save(ctx, trxName);
				if (no > 0) {
					processed += no;
				} else if (no < 0) {
					areadyExists += (no * -1);
				}
			}

		}else if(getType().equals("cupones")){

			List<Cupon> resp = get.getDataCupones(); // Ejecuto la consulta.
			StringBuffer sb = new StringBuffer();
			
			// Por cada resultado, inserto en la tabla de importación.
			for(Cupon cup: resp) {
				leidos++;
				
				// debug
				// if(cup.getTarjeta().indexOf("CABAL") == -1 || cup.getNroCupon()!=3587)
				//	continue;
				
				log.finest("Cupon #" + leidos + " Fec.Pag=" + cup.getFechaPago());
				// if(!cup.getFechaPago().equals(Timestamp.valueOf("2024-03-25 00:00:00.0")))
				// 	continue;

				ArrayList<Long> idLink = getI_FideliusLiquidaciones_ID(cup);
				if(!idLink.isEmpty() && idLink.get(0) > 0) {
					TarjetaCupon payment = new TarjetaCupon(cup);
					cup.setNroLiquidacion(idLink.get(1));
					
					payment.setLinkField("I_FideliusLiquidaciones_ID");
					payment.setLinkFieldID(idLink.get(0));
					payment.setUniquesFields(new String[] {"nroliq", "num_com", "tarjeta", "nrolote", "extra_cash", "nrocupon"});
					payment.setUniquesFieldsValues(new String[] {String.valueOf(cup.getNroLiquidacion()), 
							cup.getNroComercio(), 
							cup.getTarjeta(), 
							String.valueOf(cup.getNroLote()), 
							cup.getExtraCash(),
							String.valueOf(cup.getNroCupon())});

					payment.addDefaultField("is_reconciled");
					payment.addDefaultValue("'N'");
					payment.setNroLiq(cup.getNroLiquidacion());

					int no = payment.save(ctx, trxName);
					if (no > 0) {
						processed += no;
						/*sb.append("processed;" +
								cup.getNroLiquidacion()+";"+
								cup.getNroLote()+";"+
								cup.getNroCupon()+";"+
								cup.getTarjeta()+";"+
								cup.getFechaPago()+";"+
								cup.getCuotas()+";"+
								cup.getImporteVenta()+";"+
					"\n");*/
					} else if (no < 0) {
						areadyExists += (no * -1);
						/*sb.append("nosave;" +
								cup.getNroLiquidacion()+";"+
								cup.getNroLote()+";"+
								cup.getNroCupon()+";"+
								cup.getTarjeta()+";"+
								cup.getFechaPago()+";"+
								cup.getCuotas()+";"+
								cup.getImporteVenta()+";"+
					"\n");*/
					}
				}else {
					areadyExists++;
					/*sb.append("alreadyExists;" +
								cup.getNroLiquidacion()+";"+
								cup.getNroLote()+";"+
								cup.getNroCupon()+";"+
								cup.getTarjeta()+";"+
								cup.getFechaPago()+";"+
								cup.getCuotas()+";"+
								cup.getImporteVenta()+";"+
					"\n");*/
				}
			}
			
			/*Buffer2Disk(sb);*/

		}else if(getType().equals("pendientes")) {

			// setear el nombre del comercio
			get.setOrgName(getOrgName());
			
			List<CuponPendiente> resp = get.getDataPendientes(); // Ejecuto la consulta.

			// Por cada resultado, inserto en la tabla de importación.
			for(CuponPendiente pen: resp) {
				leidos++;
				log.finest("Pendiente #" + leidos);

				TarjetaCuponPendiente payment = new TarjetaCuponPendiente(pen);
				payment.setUniquesFields(new String[] {"id", "codcom", "nrotarjeta"});
				payment.setUniquesFieldsValues(new String[] {String.valueOf(pen.getId()), 
						pen.getCodCom(), 
						pen.getNroTarjeta()});
				
				int no = payment.save(ctx, trxName);
				if (no > 0) {
					processed += no;
				} else if (no < 0) {
					areadyExists += (no * -1);
				}
			}

		}else
			log.warning("No se configuro correctamente el tipo de importacion");
		
		ret = "Procesados = " + processed + ", Preexistentes/Sin Liquidar = " + areadyExists;
			
		log.finest(ret);
		
		return ret;
		// Msg CentralPoS
		// return msg(new Object[] { processed, areadyExists });
		
	}
	
	private void Buffer2Disk(StringBuffer sb) {
		BufferedWriter writer = null;
		String filePath = "/home/jorge/disytel/Cupones.txt";

        try {
            // Crear un BufferedWriter para escribir en el archivo
            writer = new BufferedWriter(new FileWriter(filePath));

            // Escribir el contenido del StringBuffer en el archivo
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Cerrar el BufferedWriter
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
	}

	/**
	 * Para el caso de CABAL por ej, las liquidaciones vienen con 4 digitos, pero en los cupones al numero
	 * de liquidacion le suman dos digitos mas, ej: Liquidacion=5878 en Cupones la misma liquidacion dice: Liquidacion=745878
	 * Entonces se usan preferencias con las marcas de las tarjetas para definir el esquema de mascara.
	 * En este caso -> Preferencia CABAL (todos los usuarios y organizaciones) Valor=NNNN donde las enes (N) representan cuantos digitos desde la derecha hay q comparar
	 * 
	 * @param cup (Objeto cupon)
	 * @return identificacion de la liquidacion Fidelius y nro de liquidacion (encabezado)
	 */
	private ArrayList<Long> getI_FideliusLiquidaciones_ID(Cupon cup) {
		String plantillaLargo = MPreference.GetCustomPreferenceValue(cup.getTarjeta());
		Long liquidacion = cup.getNroLiquidacion();
		String tarjeta = cup.getTarjeta();
		ArrayList<Long> liqIds = new ArrayList<Long>();
		
		if(plantillaLargo!=null && !plantillaLargo.isEmpty()) {
			String liq = String.valueOf(liquidacion);
			if(liq.length() > plantillaLargo.length()) {
				liq = liq.substring(liq.length()-plantillaLargo.length());
				liquidacion = Long.valueOf(liq);
			}
			tarjeta = tarjeta.replace("DEBITO", "").replace("CREDITO", "").trim();
		}
		
		String sql = "SELECT I_FideliusLiquidaciones_ID FROM " +
					" I_FideliusLiquidaciones WHERE " +
					" NroLiq=?::varchar AND " +
					" Num_Com=? AND " +
					" Tarjeta=? AND " +
					" FPag::DATE=?::DATE";
		
		// if(cup.getFechaPago().equals(Timestamp.valueOf("2024-03-25 00:00:00.0")))
		log.finest("===> ImportTarjeta.getI_FideliusLiquidaciones_ID. Busco liquidacion, sql=" + sql + " - liquidacion=" + liquidacion + " Comercio=" + cup.getNroComercio() + " Tarjeta=" + cup.getTarjeta() + "/" + tarjeta + " " + " FechaPago=" + cup.getFechaPago());
		
		Long tmpI = Utilidades.getSQLValueEx(trxName, sql, new Object[]{
				liquidacion,
				cup.getNroComercio(),
				tarjeta,
				cup.getFechaPago()
				});
		
		if(tmpI > 0) {	
			// I_FideliusLiquidaciones_ID
			liqIds.add((long)tmpI);

			// Nro Liquidacion del encabezado
			liqIds.add(liquidacion);
		}

		return liqIds;
	}

	@Override
	public void setDateFromParam(Timestamp date) {
		if (date != null) {
			addParam("desde", Env.getDateFormatted(date));
		}
	}

	@Override
	public void setDateToParam(Timestamp date) {
		if (date != null) {
			addParam("hasta", Env.getDateFormatted(date));
		}
	}
	
	@Override
	public void setType(String tipo) {
		if (tipo == null)
			tipo = "L";
		type = tipo;
		
		addParam("action", getType());
		
	}
	
	@Override
	public String getType() {
		String r = null;
		if (type == null || type.equals("L"))
			 r = "liquidaciones";
		else if(type.equals("C"))
			r = "cupones";
		else if(type.equals("P"))
			r = "pendientes";
		
		return r;
	}
	
	@Override
	public void setEstado(String estado) {
		if (estado == null)
			estado = "false";
		this.estado = estado;
		
		addParam("estado", getEstado());
		
	}
	
	@Override
	public String getEstado() {
		String r = null;
		if (estado == null)
			r = "false";
		else 
			r = estado;
		
		return r;
	}
	
	@Override
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	@Override
	public String getOrgName() {
		return this.orgName;
	}


	/**
	 * Este metodo es utilizado por la clase ImportSettelements
	 * 
	 * jdreher
	 */
	
	@Override
	public void validate(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes,
			String trxName, int iteracion) throws Exception {
		
		int id = getC_BPartner_ID(ctx, rs.getString("num_com"), trxName);
		if (id <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		
		//IIBB
		id = getRetencionSchemaByNroEst(ctx, rs.getString("num_com"), trxName);
		if (id <= 0) {
			throw new Exception("No existe retención de IIBB sufrida, para la región configurada en la E.Financiera");
		} 
		
		// Solo valido en la primer iteracion
		if(iteracion == 0) {

			String name = "Ret IVA";
			id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto Ret IVA");
			}

			name = "Ret Ganancias";
			id = getRetencionSchemaIDByValue(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto Ret Ganancias");
			}

			name = "Dto por ventas de campañas";
			id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto Dto por ventas de campañas");
			}

			name = "Costo plan acelerado cuotas";
			id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto Costo plan acelerado cuotas");
			}

			name = "CFO Total";
			id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto CFO Total");
			}
			
			name = "Cargo adic por planes cuotas";
			id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto Cargo adic por planes cuotas");
			}

			name = "Importe Arancel";
			id = getCardSettlementConceptIDByValue(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto Importe Arancel");
			}

			name = "IVA 10.5";
			id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto " + attributes.get(name).getName());
			}

			name = "IVA 21";
			id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
			if (id <= 0) {
				throw new Exception("No se encontró el concepto IVA21");
			}

			/**
		name = "IVA";
		id = getTaxIDByName(ctx, attributes.get(name).getName(), trxName);
		if (id <= 0) {
			throw new Exception("No se encontró el concepto IVA");
		}
			 */

		}
	}

	
	// TODO: asegurar que esten todos los campos a guardarse como importes...
	
	@Override
	public boolean create(Properties ctx, ResultSet rs, Map<String, X_C_ExternalServiceAttributes> attributes,
			String trxName) throws Exception {
		int C_BPartner_ID = -1;
		
		// Tipo de tarjeta mapeada desde los atributos del servicio externo...
		String tt = getCreditCardType(ctx, rs.getString("tarjeta"), trxName);
		
		int M_EntidadFinanciera_ID = getM_EntidadFinanciera_ID(ctx, rs.getString("num_com"), tt, trxName);
		if (M_EntidadFinanciera_ID <= 0) {
			throw new Exception("Ignorado: no se encontró Nro. de comercio en E.Financieras");
		}
		
		MEntidadFinanciera ef = new MEntidadFinanciera(ctx, M_EntidadFinanciera_ID, trxName);
		Timestamp fecha = null; 
		
		// Pueden existir varias entidades comerciales con el mismo numero de establecimiento
		// leer el que corresponde a partir de la entidad financiera que corresponde -> numero comercio + tarjeta
		C_BPartner_ID = ef.getC_BPartner_ID();
		if(C_BPartner_ID <= 0) {
			C_BPartner_ID = getC_BPartner_ID(ctx, rs.getString("num_com"), trxName);
			if (C_BPartner_ID <= 0) {
				throw new Exception("Número de comercio \"" + rs.getString("num_com") + "\" ignorado.");
			}
		}
		
		try {
			fecha = rs.getTimestamp("fpag");
		}catch(Exception ex) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = sdf.parse(rs.getString("fpag"));
			fecha = new Timestamp(date.getTime()); 
		}
		
		int C_CreditCardSettlement_ID = getSettlementIdFromNroAndBPartner(ctx, rs.getString("nroliq"), C_BPartner_ID,
				fecha, trxName);
		MCreditCardSettlement settlement = null;
		if (C_CreditCardSettlement_ID > 0) {
			settlement = new MCreditCardSettlement(ctx, C_CreditCardSettlement_ID, trxName);
			if (!settlement.getDocStatus().equals(MCreditCardSettlement.DOCSTATUS_Drafted)) {
				// Se marca importado si ya existe para que luego no se
				// levante como un registro pendiente de importación 
				return false;
			}
		}

		BigDecimal netAmt = safeMultiply(rs.getString("impneto"), "+");
		BigDecimal amt = safeMultiply(rs.getString("impbruto"), "+");

		//Acumuladores para totales de impuestos, tasas, etc.
		BigDecimal withholdingAmt = new BigDecimal(0);
		BigDecimal perceptionAmt = new BigDecimal(0);
		BigDecimal expensesAmt = new BigDecimal(0);
		BigDecimal ivaAmt = new BigDecimal(0);
		BigDecimal commissionAmt = new BigDecimal(0);
		
		if(settlement == null) {
			settlement = new MCreditCardSettlement(ctx, 0, trxName);
		}
		settlement.setGenerateChildrens(false);
		settlement.setAD_Org_ID(ef.getAD_Org_ID());
		settlement.setCreditCardType(tt);
		settlement.setC_BPartner_ID(C_BPartner_ID);
		settlement.setPaymentDate(fecha);
		settlement.setAmount(amt);
		settlement.setNetAmount(netAmt);
		settlement.setC_Currency_ID(Env.getC_Currency_ID(ctx));
		settlement.setSettlementNo(rs.getString("nroliq"));

		if (!settlement.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		} 
		
		
		/**
		 * Si ya existia esta liquidacion de tarjetas, limpiar tablas hijas para no duplicar los movimientos
		 * 
		 * dREHER
		 */
		if (C_CreditCardSettlement_ID > 0) {
			
			// Limpia retenciones
			String sql = "DELETE FROM C_WithholdingSettlement WHERE C_CreditCardSettlement_ID=" + C_CreditCardSettlement_ID;
			DB.executeUpdate(sql, trxName);
			
			// Limpia otros conceptos
			sql = "DELETE FROM C_ExpenseConcepts WHERE C_CreditCardSettlement_ID=" + C_CreditCardSettlement_ID;
			DB.executeUpdate(sql, trxName);
			
			// Limpia comisiones
			sql = "DELETE FROM C_CommissionConcepts WHERE C_CreditCardSettlement_ID=" + C_CreditCardSettlement_ID;
			DB.executeUpdate(sql, trxName);
			
			// Limpia IVA
			sql = "DELETE FROM C_IVASettlements WHERE C_CreditCardSettlement_ID=" + C_CreditCardSettlement_ID;
			DB.executeUpdate(sql, trxName);

			// Limpia Percepciones
			sql = "DELETE FROM C_PerceptionsSettlement WHERE C_CreditCardSettlement_ID=" + C_CreditCardSettlement_ID;
			DB.executeUpdate(sql, trxName);

		}else
			log.info("Se crea una nueva liquidacion de tarjeta. Liq=" + rs.getString("nroliq"));
		
		try {
			int C_RetencionSchema_ID = getRetencionSchemaByNroEst(ctx, rs.getString("num_com"), trxName);
			BigDecimal withholding = safeMultiply(rs.getString("ret_iibb"), "+");
			
			if(withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID <= 0) {
				throw new Exception("No existe retención de IIBB sufrida, para la región configurada en la E.Financiera");
			}
			
			if (withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID > 0) {
				MRetencionSchema retSchema = new MRetencionSchema(ctx, C_RetencionSchema_ID, trxName);
				MWithholdingSettlement ws = new MWithholdingSettlement(ctx, 0, trxName);
				ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
				ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ws.setAD_Org_ID(settlement.getAD_Org_ID());
				ws.setC_Region_ID(retSchema.getC_Region_ID());
				ws.setAmount(withholding);
				if(!ws.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				withholdingAmt = withholdingAmt.add(withholding);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		try {
			String name = "Ret IVA";
			
			String value = attributes.get(name).getName();
			log.finest("Se busca esquema de retencion de IVA por value= " + value);
			
			int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, value, trxName);
			BigDecimal withholding = safeMultiply(rs.getString("ret_iva"), "+");
			
			if(withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID <= 0) {
				throw new Exception("No existe retención de IVA, para la región configurada en la E.Financiera");
			}
			
			if (withholding.compareTo(new BigDecimal(0)) != 0) {
				MWithholdingSettlement ws = new MWithholdingSettlement(ctx, 0, trxName);
				ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
				ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ws.setAD_Org_ID(settlement.getAD_Org_ID());
				ws.setAmount(withholding);
				if(!ws.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				withholdingAmt = withholdingAmt.add(withholding);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: Ret IVA");
			e.printStackTrace();
		}
		
		try {
			String name = "Ret IB SIRTAC";
			
			String value = attributes.get(name).getName();
			log.finest("Se busca esquema de retencion de IB SIRTAC por value= " + value);
			
			int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, value, trxName);
			BigDecimal withholding = safeMultiply(rs.getString("ret_ibsirtac"), "+");
			
			if(withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID <= 0) {
				throw new Exception("No existe retención IB SIRTAC, para la región configurada en la E.Financiera");
			}
			
			if (withholding.compareTo(new BigDecimal(0)) != 0) {
				MWithholdingSettlement ws = new MWithholdingSettlement(ctx, 0, trxName);
				ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
				ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ws.setAD_Org_ID(settlement.getAD_Org_ID());
				ws.setAmount(withholding);
				if(!ws.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				withholdingAmt = withholdingAmt.add(withholding);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: Ret IB SIRTAC");
			e.printStackTrace();
		}
		
		try {
			String name = "Ret Ganancias";
			
			String value = attributes.get(name).getName();
			log.finest("Se busca esquema de retencion de ganancias por value= " + value);
			
			int C_RetencionSchema_ID = getRetencionSchemaIDByValue(ctx, value, trxName);
			BigDecimal withholding = safeMultiply(rs.getString("ret_gcia"), "+");
			
			if(withholding.compareTo(new BigDecimal(0)) != 0 && C_RetencionSchema_ID <= 0) {
				throw new Exception("No existe retención de Ganancias, para la región configurada en la E.Financiera");
			}
			
			if (withholding.compareTo(new BigDecimal(0)) != 0) {
				MWithholdingSettlement ws = new MWithholdingSettlement(ctx, 0, trxName);
				ws.setC_RetencionSchema_ID(C_RetencionSchema_ID);
				ws.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ws.setAD_Org_ID(settlement.getAD_Org_ID());
				ws.setAmount(withholding);
				if(!ws.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				withholdingAmt = withholdingAmt.add(withholding);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: Ret Ganancias");
			e.printStackTrace();
		}
		
		/* -- NO -- -- 
		try {
			String name = "Dto por ventas de campañas";
			
			String value = attributes.get(name).getName();
			log.info("Concepto de descuento campañas por value= " + value);
			
			int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, value, trxName);
			BigDecimal expense = safeMultiply(rs.getString("dto_campania"), "+");
			if (expense.compareTo(new BigDecimal(0)) != 0) {
				MExpenseConcepts ec = new MExpenseConcepts(ctx, 0, trxName);
				ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
				ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ec.setAD_Org_ID(settlement.getAD_Org_ID());
				ec.setAmount(expense);
				if(!ec.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				expensesAmt = expensesAmt.add(expense);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		*/
		
		try {
			String name = "Costo plan acelerado cuotas";
			
			String value = attributes.get(name).getName();
			log.info("Se busca Costo plan acelerado cuotas por value= " + value);
			
			int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, value, trxName);
			BigDecimal expense = safeMultiply(rs.getString("cfo_adel"), "+");
			if (expense.compareTo(new BigDecimal(0)) != 0) {
				if(C_CardSettlementConcept_ID <= 0)
					throw new Exception("No se encontro el concepto de liquidacion: " + name);
				
				MExpenseConcepts ec = new MExpenseConcepts(ctx, 0, trxName);
				ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
				ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ec.setAD_Org_ID(settlement.getAD_Org_ID());
				ec.setAmount(expense);
				if(!ec.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				expensesAmt = expensesAmt.add(expense);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: Costo plan acelerado cuotas");
			e.printStackTrace();
		}
		
		try {
			String name = "Plan A12/18";
			
			String value = attributes.get(name).getName();
			log.info("Se busca Plan A12/18 por value= " + value);
			
			int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, value, trxName);
			BigDecimal expense = safeMultiply(rs.getString("plan_a1218"), "+");
			if (expense.compareTo(new BigDecimal(0)) != 0) {
				if(C_CardSettlementConcept_ID <= 0)
					throw new Exception("No se encontro el concepto de liquidacion: " + name);
				
				MExpenseConcepts ec = new MExpenseConcepts(ctx, 0, trxName);
				ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
				ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ec.setAD_Org_ID(settlement.getAD_Org_ID());
				ec.setAmount(expense);
				if(!ec.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				expensesAmt = expensesAmt.add(expense);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: Plan A12/18");
			e.printStackTrace();
		}
		
		try {
			String name = "CFO Total";
			
			String value = attributes.get(name).getName();
			log.info("Se busca CFO Total por value= " + value);
			
			int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, value, trxName);
			BigDecimal expense = safeMultiply(rs.getString("cfo_total"), "+");
			if (expense.compareTo(new BigDecimal(0)) != 0) {
				MExpenseConcepts ec = new MExpenseConcepts(ctx, 0, trxName);
				ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
				ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ec.setAD_Org_ID(settlement.getAD_Org_ID());
				ec.setAmount(expense);
				if(!ec.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				expensesAmt = expensesAmt.add(expense);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: CFO Total");
			e.printStackTrace();
		}
		
		/* -- NO -- -- 
		try {
			String name = "Cargo adic por planes cuotas";
			
			String value = attributes.get(name).getName();
			log.info("Se busca Cargo adic por planes cuotas por value= " + value);
			
			int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, value, trxName);
			BigDecimal expense = safeMultiply(rs.getString("adic_plancuo"), "+");
			if (expense.compareTo(new BigDecimal(0)) != 0) {
				MExpenseConcepts ec = new MExpenseConcepts(ctx, 0, trxName);
				ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
				ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ec.setAD_Org_ID(settlement.getAD_Org_ID());
				ec.setAmount(expense);
				if(!ec.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				expensesAmt = expensesAmt.add(expense);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		*/
		
		/* -- NO -- -- 
		try {
			String name = "Cargo adic por op internacionales";
			
			String value = attributes.get(name).getName();
			log.info("Se busca Cargo adic por op internacionales por value= " + value);
			
			int C_CardSettlementConcept_ID = getCardSettlementConceptIDByValue(ctx, value, trxName);
			BigDecimal expense = safeMultiply(rs.getString("adic_opinter"), "+");
			if (expense.compareTo(new BigDecimal(0)) != 0) {
				MExpenseConcepts ec = new MExpenseConcepts(ctx, 0, trxName);
				ec.setC_Cardsettlementconcepts_ID(C_CardSettlementConcept_ID);
				ec.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ec.setAD_Org_ID(settlement.getAD_Org_ID());
				ec.setAmount(expense);
				if(!ec.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				expensesAmt = expensesAmt.add(expense);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		*/
		
		try {
			String name = "Importe Arancel";
			
			String value = attributes.get(name).getName();
			log.info("Se busca Importe Arancel por value= " + value);
			
			int C_CardSettlementConcepts_ID = getCardSettlementConceptIDByValue(ctx, value, trxName);
			BigDecimal commission = safeMultiply(rs.getString("arancel"), "+");
			if (commission.compareTo(new BigDecimal(0)) != 0) {
				if(C_CardSettlementConcepts_ID <= 0)
					throw new Exception("No se encontro el concepto de liquidacion: " + name);
				
				MCommissionConcepts cc = new MCommissionConcepts(ctx, 0, trxName);
				cc.setC_CardSettlementConcepts_ID(C_CardSettlementConcepts_ID);
				cc.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				cc.setAD_Org_ID(settlement.getAD_Org_ID());
				cc.setAmount(commission);
				if(!cc.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				commissionAmt = commissionAmt.add(commission);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: Importe Arancel");
			e.printStackTrace();
		}
		
		/* -- NO -- -- 
		try {
			String name = "IVA 10.5";
			
			String value = attributes.get(name).getName();
			log.info("Se busca IVA 10.5 por value= " + value);
			
			int C_Tax_ID = getTaxIDByName(ctx, value, trxName);
			BigDecimal iva = safeMultiply(rs.getString("retiva_cuo1"), "+");
			if (iva.compareTo(new BigDecimal(0)) != 0) {
				MIVASettlements iv = new MIVASettlements(ctx, 0, trxName); 
				iv.setC_Tax_ID(C_Tax_ID);
				iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				iv.setAD_Org_ID(settlement.getAD_Org_ID());
				iv.setAmount(iva);
				if(!iv.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				ivaAmt = ivaAmt.add(iva);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		*/
		
		int C_Tax_ID = -1;
		
		try {
			String name = "IVA 21";
			
			String value = attributes.get(name).getName();
			log.info("Se busca IVA21 por value= " + value);
			
			C_Tax_ID = getTaxIDByName(ctx, value, trxName);
			BigDecimal iva = safeMultiply(rs.getString("iva_total"), "+");
			if (iva.compareTo(new BigDecimal(0)) != 0) {
				/*MIVASettlements iv = new MIVASettlements(ctx, 0, trxName); 
				iv.setC_Tax_ID(C_Tax_ID);
				iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				iv.setAD_Org_ID(settlement.getAD_Org_ID());
				iv.setAmount(iva);
				if(!iv.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}*/
				ivaAmt = ivaAmt.add(iva);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: IVA 21");
			e.printStackTrace();
		}
		
		try {
			String name = "IVA OTROS";
			
			String value = attributes.get(name).getName();
			log.info("Se busca IVAOTROS por value= " + value);
			
			/*int C_Tax_ID = getTaxIDByName(ctx, value, trxName);*/
			BigDecimal iva = safeMultiply(rs.getString("iva_otros"), "+");
			if (iva.compareTo(new BigDecimal(0)) != 0) {
				/*MIVASettlements iv = new MIVASettlements(ctx, 0, trxName); 
				iv.setC_Tax_ID(C_Tax_ID);
				iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				iv.setAD_Org_ID(settlement.getAD_Org_ID());
				iv.setAmount(iva);
				if(!iv.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				*/
				ivaAmt = ivaAmt.add(iva);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: IVA OTROS");
			e.printStackTrace();
		}
		
		try {
			String name = "IVA PLANA1218";
			
			String value = attributes.get(name).getName();
			log.info("Se busca PLANA1218 por value= " + value);
			
			/*int C_Tax_ID = getTaxIDByName(ctx, value, trxName);*/
			BigDecimal iva = safeMultiply(rs.getString("iva_plana1218"), "+");
			if (iva.compareTo(new BigDecimal(0)) != 0) {
				/*MIVASettlements iv = new MIVASettlements(ctx, 0, trxName); 
				iv.setC_Tax_ID(C_Tax_ID);
				iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				iv.setAD_Org_ID(settlement.getAD_Org_ID());
				iv.setAmount(iva);
				if(!iv.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}*/
				ivaAmt = ivaAmt.add(iva);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: IVA PLANA1218");
			e.printStackTrace();
		}
		
		/**
		 * Desde impuestos solo toman IVA 21, por ende desde Tarjetas solicitan que todo
		 * el IVA se acumule en un solo item
		 * 
		 * dREHER
		 */
		
		if (ivaAmt.compareTo(new BigDecimal(0)) != 0) {
			MIVASettlements iv = new MIVASettlements(ctx, 0, trxName); 
			iv.setC_Tax_ID(C_Tax_ID);
			iv.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
			iv.setAD_Org_ID(settlement.getAD_Org_ID());
			iv.setAmount(ivaAmt);
			if(!iv.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		
		 
		try {
			String name = "Perc IVA";
			
			String value = attributes.get(name).getName();
			log.info("Se busca Percepcion de IVA por value= " + value);
			
			C_Tax_ID = getTaxIDByName(ctx, value, trxName);
			BigDecimal perception = safeMultiply(rs.getString("perc_iva"), "+");
			if (perception.compareTo(new BigDecimal(0)) != 0) {
				MPerceptionsSettlement ps = new MPerceptionsSettlement(ctx, 0, trxName);
				ps.setC_Tax_ID(C_Tax_ID);
				ps.setC_CreditCardSettlement_ID(settlement.getC_CreditCardSettlement_ID());
				ps.setAD_Org_ID(settlement.getAD_Org_ID());
				ps.setAmount(perception);
				if(!ps.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				perceptionAmt = perceptionAmt.add(perception);
			}
		} catch (NullPointerException e) {
			log.warning("No se encontro un atributo de Entidad Externa, con clave: Perc IVA");
			e.printStackTrace();
		}
		
		
		settlement.setWithholding(withholdingAmt);
		settlement.setPerception(perceptionAmt);
		settlement.setExpenses(expensesAmt);
		settlement.setIVAAmount(ivaAmt);
		settlement.setCommissionAmount(commissionAmt);
		if (!settlement.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		} 
		return true;
	}

	@Override
	public String getTableName() {
		return X_I_FideliusLiquidaciones.Table_Name;
	}

	@Override
	public String[] getFilteredFields() {
		return TarjetaPayments.filteredFields;
	}
}
