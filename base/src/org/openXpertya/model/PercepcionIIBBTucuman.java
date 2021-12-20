package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Calendar;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PercepcionIIBBTucuman extends PercepcionIIBBProvincias {

	/** Jurisdicción Tucumán */
	private int jurisdiccionTucuman = 924;
	
	public PercepcionIIBBTucuman() {
		// TODO Auto-generated constructor stub
	}

	public PercepcionIIBBTucuman(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Obtiene la región sede del cliente
	 * 
	 * @param bpartnerID id del cliente
	 * @return número de jurisdicción sede del cliente
	 */
	private int getJurisdiccionSede(int bpartnerID) {
		return DB.getSQLValue(null, "select r.jurisdictioncode " + 
				"from c_bpartner bp " + 
				"join c_region r on r.c_region_id = bp.c_region_sede_id " +
				"where bp.c_bpartner_id = ? ", 
				bpartnerID);
	}
	
	/**
	 * @param bpartnerID id del cliente
	 * @return el porcentaje Tucumán a aplicar para CM en Régimen Especial 
	 */
	private BigDecimal getPorcentajeTucuman(int bpartnerID) {
		return DB.getSQLValueBD(null,
				"select alicuota from c_bpartner_percepcion where c_bpartner_id = ? and isactive = 'Y' and ad_org_id in ("
						+ getPercepcionData().getDocument().getOrgID()
						+ ", 0) and c_region_id is not null order by ad_org_id desc limit 1",
				bpartnerID);
	}
	
	@Override
	public Percepcion applyDebitPerception() {
		Percepcion p = null;
		BigDecimal perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
				getPercepcionData().getBpartner().getTaxID(), Env.getDate(), 
				MBPartnerPadronBsAs.PADRONTYPE_PadronContribuyentesTucuman, null); 
		if(perc == null) {
			// No se encuentra inscripto
			// Si la percepción no es convenio multilateral, entonces se aplica sólo la alícuota configurada 
			if(!getPercepcionData().isConvenioMultilateral()) {
				if(Util.isEmpty(getPercepcionData().getBpartner().getIsConvenioMultilateral(), true)) {
					if (getPercepcionData().getDocument().getTaxBaseAmt()
							.compareTo(getPercepcionData().getMinimumNetAmt()) >= 0) {
						p = getApplyRate(getPercepcionData().getAlicuota(),
								getPercepcionData().getDocument().getLinesTotalAmt(true),
								getPercepcionData().getMinimumNetAmt());
					}
				}
			}
			else {
				if(!Util.isEmpty(getPercepcionData().getBpartner().getIsConvenioMultilateral(), true)) {
					if(getPercepcionData().getBpartner().getIsConvenioMultilateral().equals(MBPartner.ISCONVENIOMULTILATERAL_RegimenGeneral)) {
						// Consultar con el padrón de coeficientes
						BigDecimal coeficiente = MBPartnerPadronBsAs.getBPartnerPerc("coeficiente",
								getPercepcionData().getBpartner().getTaxID(), Env.getDate(), 
								MBPartnerPadronBsAs.PADRONTYPE_CoeficientesTucuman, null);
						
						if(coeficiente != null) {
							// Si el coeficiente es 0, entonces tomar la alícuota configurada
							if(coeficiente.compareTo(BigDecimal.ZERO) == 0) {
								p = getApplyRate(getPercepcionData().getAlicuota());
							}
							// Si es mayor a 0, entonces aplicar el coeficiente sobre el porcentaje
							else {
								perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
										getPercepcionData().getBpartner().getTaxID(), Env.getDate(), 
										MBPartnerPadronBsAs.PADRONTYPE_CoeficientesTucuman, null);
								perc = perc.multiply(new BigDecimal(0.5)).multiply(coeficiente);
								p = getApplyRate(perc);
							}
						}
					}
				}
			}
		}
		else if(perc.compareTo(BigDecimal.ZERO) > 0) {
			// Existe en padrón, es contribuyente local, se toma el neto * padrón
			if(!getPercepcionData().isConvenioMultilateral()) {
				if(Util.isEmpty(getPercepcionData().getBpartner().getIsConvenioMultilateral(), true)) {
					p = getApplyRate(perc);
				}
			}
			else if(!Util.isEmpty(getPercepcionData().getBpartner().getIsConvenioMultilateral(), true)) {
				if(getPercepcionData().getBpartner().getIsConvenioMultilateral().equals(MBPartner.ISCONVENIOMULTILATERAL_RegimenGeneral)) {
					// Verificar la jurisdicción sede de la EC
					int regionSede = getJurisdiccionSede(getPercepcionData().getBpartner().getID());
					if(regionSede == jurisdiccionTucuman) {
						p = getApplyRate(perc.multiply(new BigDecimal(0.5)));
					}
					else {
						// Consultar con el padrón de coeficientes
						BigDecimal coeficiente = MBPartnerPadronBsAs.getBPartnerPerc("coeficiente",
								getPercepcionData().getBpartner().getTaxID(), Env.getDate(), 
								MBPartnerPadronBsAs.PADRONTYPE_CoeficientesTucuman, null);
						
						if(coeficiente != null) {
							// Si el coeficiente es 0, entonces tomar la alícuota configurada
							if(coeficiente.compareTo(BigDecimal.ZERO) == 0) {
								p = getApplyRate(getPercepcionData().getAlicuota());
							}
							// Si es mayor a 0, entonces aplicar el coeficiente sobre el porcentaje
							else {
								perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
										getPercepcionData().getBpartner().getTaxID(), Env.getDate(), 
										MBPartnerPadronBsAs.PADRONTYPE_CoeficientesTucuman, null);
								perc = perc.multiply(new BigDecimal(0.5)).multiply(coeficiente);
								p = getApplyRate(perc);
							}
						}
					}
				}
				else if(getPercepcionData().getBpartner().getIsConvenioMultilateral().equals(MBPartner.ISCONVENIOMULTILATERAL_RegimenEspecial)) {
					// Aplicar el porcentaje Tucumán
					BigDecimal porcTucuman = getPorcentajeTucuman(getPercepcionData().getBpartner().getID());
					p = getApplyRate(perc.multiply((Util.isEmpty(porcTucuman, true)) ? BigDecimal.ONE
							: porcTucuman.divide(Env.ONEHUNDRED, getPercepcionData().getScale(),
									BigDecimal.ROUND_HALF_DOWN)));
				}
			}
		}
		// Controlar que el importe determinar (importe a percibir) sea mayor al importe
		// mínimo de percepción
		if(p != null && p.getTaxAmt().compareTo(getPercepcionData().getMinimumPercepcionAmt()) <= 0) {
			p = null;
		}
		return p;
	}
	
	@Override
	public Percepcion applyCreditPerception() {
		// Para percepciones de Tucumán, la devolución de percepción parcial sólo se
		// realiza en el mismo mes que el comprobante original
		Percepcion p = super.applyCreditPerception();
		if (p != null
				&& getPercepcionData().getRelatedDocument() != null
				&& !getPercepcionData().isVoiding()) {
			Calendar co = Calendar.getInstance();
			co.setTimeInMillis(getPercepcionData().getRelatedDocument().getDate().getTime());
			Calendar cc = Calendar.getInstance();
			cc.setTimeInMillis(getPercepcionData().getDocument().getDate().getTime());
			if(!(co.get(Calendar.MONTH) == cc.get(Calendar.MONTH) 
					&& co.get(Calendar.YEAR) == cc.get(Calendar.YEAR))) {
				p = null;
			}
		}
		return p;
	}
}
