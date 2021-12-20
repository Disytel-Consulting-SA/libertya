package org.openXpertya.process;

import java.math.BigDecimal;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerPadronBsAs;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MRetSchemaConfig;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class RetencionIIBBTucuman extends RetencionIIBBForRegion {

	/** Jurisdicción Tucumán */
	private int jurisdiccionTucuman = 924;
	
	/** Importe Neto Mínimo */
	private BigDecimal minimumNetAmt;
	
	@Override
	public void loadConfig(MRetencionSchema retSchema) {
		super.loadConfig(retSchema);
		// Importe Mínimo Neto
		setMinimumNetAmt(getParamBigDecimal(MRetSchemaConfig.NAME_MinimumNetAmount, BigDecimal.ZERO, null));
	}
	
	/**
	 * @param bpartnerID id del cliente
	 * @return número de jurisdicción sede
	 */
	private int getJurisdiccionSede(int bpartnerID) {
		return DB.getSQLValue(null, "select r.jurisdictioncode " + 
				"from c_bpartner bp " + 
				"join c_region r on r.c_region_id = bp.c_region_sede_id " +
				"where bp.c_bpartner_id = ? ", 
				bpartnerID);
	}
	
	/**
	 * @return número de jurisdicción sede de la compañía
	 */
	private int getClientJurisdiccionSede() {
		return DB.getSQLValue(null, "select r.jurisdictioncode " + 
				"from ad_clientinfo ci " + 
				"join c_region r on r.c_region_id = ci.c_region_sede_id " +
				"where ci.ad_client_id = ? ", 
				getAD_Client_ID());
	}
	
	/**
	 * @param bpartnerID id del cliente
	 * @return el porcentaje Tucumán a aplicar para CM en Régimen Especial 
	 */
	private BigDecimal getPorcentajeTucuman(int bpartnerID) {
		return DB.getSQLValueBD(null,
				"select alicuota "
				+ "from c_bpartner_retencion "
				+ "where c_bpartner_id = "+bpartnerID+" and c_retencionschema_id = ? and isactive = 'Y' ",
				getRetencionSchema().getID());
	}
	
	protected BigDecimal calculateAmount() {
		BigDecimal baseImponible = Env.ZERO;
		BigDecimal importeDeterminado = Env.ZERO;
		BigDecimal importeRetenido = Env.ZERO;
		
		BigDecimal porcentajeRetencion = MBPartnerPadronBsAs.getBPartnerPerc("retencion",
				getBPartner().getTaxID(), Env.getDate(), 
				getPadrones().size() > 0? getPadrones().get(0): MBPartnerPadronBsAs.PADRONTYPE_PadronContribuyentesTucuman,
				getTrxName());
		// No se encuentra inscripto, se toma el total y el porcentaje por defecto
		// siempre que no sea Convenio Multilateral
		if(porcentajeRetencion == null) {
			if(Util.isEmpty(getBPartner().getIsConvenioMultilateral(), true)) {
				importeRetenido = applyPercentAndSave(getTotalAmt(), getPorcentajeRetencionDefault(), getPayNetAmt());
			}
			else {
				// Entra por aca cuando es convenio multilateral y no está inscripto en Tucumán
				if (getBPartner().getIsConvenioMultilateral().equals(MBPartner.ISCONVENIOMULTILATERAL_RegimenGeneral)) {
					// Sólo se aplica la retención a los comprobantes fuera de Tucumán cuando la
					// jurisdicción sede de la compañía es distinto a Tucumán y es convenio
					// multilateral en régimen general 
					int clientRegionSede = getClientJurisdiccionSede();
					MClientInfo ci = MClientInfo.get(Env.getCtx(), getAD_Client_ID());
					if (clientRegionSede != jurisdiccionTucuman 
							&& !Util.isEmpty(ci.getConvenioMultilateral(), true)
							&& ci.getConvenioMultilateral().equals(MClientInfo.CONVENIOMULTILATERAL_RegimenGeneral)) {
						baseImponible = getPayNetAmtAllInvoices();
					}
					else {
						baseImponible = getPayNetAmt();
					}
					// Si hay base para calcular respecto al mínimo
					if(baseImponible.compareTo(getMinimumNetAmt()) > 0) {
						// Consultar con el padrón de coeficientes
						BigDecimal coeficiente = MBPartnerPadronBsAs.getBPartnerPerc("coeficiente",
								getBPartner().getTaxID(), Env.getDate(), 
								getPadrones().size() > 1? getPadrones().get(1): MBPartnerPadronBsAs.PADRONTYPE_CoeficientesTucuman, 
								null);

						if(coeficiente != null) {
							// Si el coeficiente es 0, entonces tomar la alícuota configurada
							if(coeficiente.compareTo(BigDecimal.ZERO) == 0) {
								importeRetenido = applyPercentAndSave(baseImponible, getPorcentajeRetencionDefault(), null);
							}
							// Si es mayor a 0, entonces aplicar el coeficiente sobre el porcentaje
							else {
								porcentajeRetencion = MBPartnerPadronBsAs.getBPartnerPerc("retencion",
										getBPartner().getTaxID(), Env.getDate(), 
										getPadrones().size() > 1? getPadrones().get(1): MBPartnerPadronBsAs.PADRONTYPE_CoeficientesTucuman, 
										null);
								baseImponible = baseImponible.multiply(coeficiente);
								porcentajeRetencion = porcentajeRetencion.multiply(new BigDecimal(0.5));
								importeDeterminado = baseImponible.multiply(porcentajeRetencion).divide(Env.ONEHUNDRED, 2,
										BigDecimal.ROUND_HALF_EVEN);
								importeRetenido = importeDeterminado;
								setPorcentajeRetencion(porcentajeRetencion);
								setBaseImponible(baseImponible);
								setImporteDeterminado(importeDeterminado);
							}
						}
					}				
				}
			}
		}
		else if(porcentajeRetencion.compareTo(BigDecimal.ZERO) > 0) {
			// Existe en padrón, si es contribuyente local, se toma el neto * padrón 
			if(Util.isEmpty(getBPartner().getIsConvenioMultilateral(), true)) {
				importeRetenido = applyPercentAndSave(getPayNetAmt(), porcentajeRetencion, null);
			}
			// Es convenio multilateral régimen general, hay que consultar el coeficiente
			else if(getBPartner().getIsConvenioMultilateral().equals(MBPartner.ISCONVENIOMULTILATERAL_RegimenGeneral)) {
				// Verificar la jurisdicción sede de la EC
				int regionSede = getJurisdiccionSede(getBPartner().getID());
				if(regionSede == jurisdiccionTucuman) {
					importeRetenido = applyPercentAndSave(getPayNetAmt(),
							porcentajeRetencion.multiply(new BigDecimal(0.5)), null);
				}
				else {
					// Sólo se aplica la retención a los comprobantes fuera de Tucumán cuando la
					// jurisdicción sede de la compañía es distinto a Tucumán y es convenio
					// multilateral en régimen general 
					int clientRegionSede = getClientJurisdiccionSede();
					MClientInfo ci = MClientInfo.get(Env.getCtx(), getAD_Client_ID());
					if (clientRegionSede != jurisdiccionTucuman 
							&& !Util.isEmpty(ci.getConvenioMultilateral(), true)
							&& ci.getConvenioMultilateral().equals(MClientInfo.CONVENIOMULTILATERAL_RegimenGeneral)) {
						baseImponible = getPayNetAmtAllInvoices();
					}
					else {
						baseImponible = getPayNetAmt();
					}
					// Si hay base para calcular respecto al mínimo
					if(baseImponible.compareTo(getMinimumNetAmt()) > 0) {
						// Consultar con el padrón de coeficientes
						BigDecimal coeficiente = MBPartnerPadronBsAs.getBPartnerPerc("coeficiente",
								getBPartner().getTaxID(), Env.getDate(), 
								getPadrones().size() > 1? getPadrones().get(1): MBPartnerPadronBsAs.PADRONTYPE_CoeficientesTucuman, 
								null);

						if(coeficiente != null) {
							// Si el coeficiente es 0, entonces tomar la alícuota configurada
							if(Util.isEmpty(coeficiente, true)) {
								importeRetenido = applyPercentAndSave(baseImponible, getPorcentajeRetencionDefault(), null);
							}
							// Si es mayor a 0, entonces aplicar el coeficiente sobre el porcentaje
							else {
								porcentajeRetencion = MBPartnerPadronBsAs.getBPartnerPerc("retencion",
										getBPartner().getTaxID(), Env.getDate(), 
										getPadrones().size() > 1? getPadrones().get(1): MBPartnerPadronBsAs.PADRONTYPE_CoeficientesTucuman, 
										null);
								baseImponible = baseImponible.multiply(coeficiente);
								porcentajeRetencion = porcentajeRetencion.multiply(new BigDecimal(0.5));
								importeDeterminado = baseImponible.multiply(porcentajeRetencion).divide(Env.ONEHUNDRED, 2,
										BigDecimal.ROUND_HALF_EVEN);
								importeRetenido = importeDeterminado;
								setPorcentajeRetencion(porcentajeRetencion);
								setBaseImponible(baseImponible);
								setImporteDeterminado(importeDeterminado);
							}
						}
					}
				}				
			}
			// Es convenio multilateral régimen especial, se toma neto, porcentaje padrón y porcentaje Tucumán
			else if(getBPartner().getIsConvenioMultilateral().equals(MBPartner.ISCONVENIOMULTILATERAL_RegimenEspecial)) {
				BigDecimal porcentajeTucuman = getPorcentajeTucuman(getBPartner().getID());
				porcentajeTucuman = porcentajeTucuman == null? BigDecimal.ZERO: porcentajeTucuman;
				baseImponible = getPayNetAmt();
				if(baseImponible.compareTo(getMinimumNetAmt()) > 0) {
					baseImponible = baseImponible.multiply(porcentajeTucuman).divide(Env.ONEHUNDRED, 2,
							BigDecimal.ROUND_HALF_EVEN);
					importeDeterminado = baseImponible.multiply(porcentajeRetencion).divide(Env.ONEHUNDRED, 2,
							BigDecimal.ROUND_HALF_EVEN);
					importeRetenido = importeDeterminado;
					setPorcentajeRetencion(porcentajeRetencion);
					setBaseImponible(baseImponible);
					setImporteDeterminado(importeDeterminado);
				}
				
				
			}
		}
		return importeRetenido;
	}
	
	/**
	 * Aplicar el porcentaje al base imponible parámetro menos el importe no
	 * imponible de la config del esquema y lo guarda
	 * 
	 * @param baseImponible     base imponible
	 * @param percentage        porcentaje
	 * @param compareMinimumAmt el importe a comparar con el mínimo, si es null se
	 *                          compara con el parámetro baseimponible
	 * @return el importe retenido
	 */
	private BigDecimal applyPercentAndSave(BigDecimal baseImponible, BigDecimal percentage, BigDecimal compareMinimumAmt) {
		BigDecimal ir = BigDecimal.ZERO;
		compareMinimumAmt = compareMinimumAmt == null?baseImponible:compareMinimumAmt;
		if(compareMinimumAmt.compareTo(getMinimumNetAmt()) > 0) {
			if(percentage.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal id = baseImponible.multiply(percentage).divide(Env.ONEHUNDRED, 2, BigDecimal.ROUND_HALF_EVEN);
				ir = id;
				
				setPorcentajeRetencion(percentage);
				setBaseImponible(baseImponible);
				setImporteDeterminado(id);
			}
		}
		return ir;
	}

	protected BigDecimal getMinimumNetAmt() {
		return minimumNetAmt;
	}

	protected void setMinimumNetAmt(BigDecimal minimumNetAmt) {
		this.minimumNetAmt = minimumNetAmt;
	}
	
}
