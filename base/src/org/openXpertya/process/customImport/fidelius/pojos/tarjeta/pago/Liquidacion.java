package org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.process.customImport.fidelius.pojos.GenericDatum;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Serializamos el nombre igual a los filteredFields de TarjetaPayments
 * 
 * GenericMAP mapea los nombres coincidentes entre ambas clases y arma los INSERTS correspondientes...
 * 
 * @author jdreher
 *
 */

public class Liquidacion extends GenericDatum{

	@SerializedName("fpag")
	@Expose
	private Timestamp fechaPago;
	
	@SerializedName("fpres")
	@Expose
	private Timestamp fechaPresentacion;
	
	@SerializedName("fant")
	@Expose
	private Timestamp fechaAnticipo;
	
	@SerializedName("nroliq")
	@Expose
	private Long nroLiquidacion;
	
	@SerializedName("anticipo")
	@Expose
	private String antic;
	
	@SerializedName("tarjeta")
	@Expose
	private String tarjeta;
	
	@SerializedName("bancopag")
	@Expose
	private String bancoPagador;
	
	@SerializedName("num_com")
	@Expose
	private String nroComercio;
	
	@SerializedName("pciaiibb")
	@Expose
	private String provinciaIIBB;
	
	@SerializedName("impbruto")
	@Expose
	private BigDecimal bruto;
	
	@SerializedName("impneto")
	@Expose
	private BigDecimal neto;
	
	@SerializedName("totdesc")
	@Expose
	private BigDecimal totalDesc;
	
	@SerializedName("promo")
	@Expose
	private BigDecimal promo;
	
	@SerializedName("arancel")
	@Expose
	private BigDecimal arancel;
	
	@SerializedName("iva_arancel")
	@Expose
	private BigDecimal ivaArancel;
	
	@SerializedName("cfo_total")
	@Expose
	private BigDecimal CFOTotal;
	
	@SerializedName("cfo_21")
	@Expose
	private BigDecimal CFO21;
	
	@SerializedName("cfo_105")
	@Expose
	private BigDecimal CFO105;
	
	@SerializedName("cfo_adel")
	@Expose
	private BigDecimal CFOAdel;
	
	@SerializedName("iva_cfo21")
	@Expose
	private BigDecimal IVACFO21;
	
	@SerializedName("iva_cfo105")
	@Expose
	private BigDecimal IVACFO105;
	
	@SerializedName("iva_adel21")
	@Expose
	private BigDecimal IVAAdel21;
	
	@SerializedName("iva_total")
	@Expose
	private BigDecimal IVATotal;
	
	@SerializedName("ret_iibb")
	@Expose
	private BigDecimal RetIIBB;
	
	@SerializedName("ret_ibsirtac")
	@Expose
	private BigDecimal RetIBSIRTAC;
	
	@SerializedName("ret_iva")
	@Expose
	private BigDecimal RetIVA;
	
	@SerializedName("ret_gcia")
	@Expose
	private BigDecimal RetGcia;
	
	@SerializedName("perc_iva")
	@Expose
	private BigDecimal PercIVA;
	
	@SerializedName("perc_iibb")
	@Expose
	private BigDecimal PercIIBB;
	
	@SerializedName("ret_munic")
	@Expose
	private BigDecimal RetMunic;
	
	@SerializedName("liq_anttn")
	@Expose
	private BigDecimal LiqAntTN;
	
	@SerializedName("perc_1135tn")
	@Expose
	private BigDecimal Perc1135TN;
	
	@SerializedName("dto_financ")
	@Expose
	private BigDecimal DtoFinanc;
	
	@SerializedName("iva_dtofinanc")
	@Expose
	private BigDecimal IVADtoFinanc;
	
	@SerializedName("deb_cred")
	@Expose
	private BigDecimal DebCred;
	
	@SerializedName("saldos")
	@Expose
	private BigDecimal saldos;
	
	@SerializedName("otros_costos")
	@Expose
	private BigDecimal otrosCostos;
	
	@SerializedName("iva_otros")
	@Expose
	private BigDecimal IVAOtros;
	
	@SerializedName("plan_a1218")
	@Expose
	private BigDecimal planA1218;
	
	@SerializedName("iva_plana1218")
	@Expose
	private BigDecimal IVAPlanA1218;
	
	@SerializedName("porc_ivaplana1218")
	@Expose
	private BigDecimal PorIvaPlana1218;
	
	@SerializedName("cuit")
	@Expose
	private String CUIT;
	
	
	public Liquidacion() {}

	public Timestamp getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Timestamp fechaPago) {
		this.fechaPago = fechaPago;
	}

	public Timestamp getFechaPresentacion() {
		return fechaPresentacion;
	}

	public void setFechaPresentacion(Timestamp fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
	}

	public Timestamp getFechaAnticipo() {
		return fechaAnticipo;
	}

	public void setFechaAnticipo(Timestamp fechaAnticipo) {
		this.fechaAnticipo = fechaAnticipo;
	}

	public Long getNroLiquidacion() {
		return nroLiquidacion;
	}

	public void setNroLiquidacion(Long nroLiquidacion) {
		this.nroLiquidacion = nroLiquidacion;
	}

	public String getAntic() {
		return antic;
	}

	public void setAntic(String antic) {
		this.antic = antic;
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}

	public String getBancoPagador() {
		return bancoPagador;
	}

	public void setBancoPagador(String bancoPagador) {
		this.bancoPagador = bancoPagador;
	}

	public String getNroComercio() {
		return nroComercio;
	}

	public void setNroComercio(String nroComercio) {
		this.nroComercio = nroComercio;
	}

	public String getProvinciaIIBB() {
		return provinciaIIBB;
	}

	public void setProvinciaIIBB(String provinciaIIBB) {
		this.provinciaIIBB = provinciaIIBB;
	}

	public BigDecimal getBruto() {
		return bruto;
	}

	public void setBruto(BigDecimal bruto) {
		this.bruto = bruto;
	}

	public BigDecimal getNeto() {
		return neto;
	}

	public void setNeto(BigDecimal neto) {
		this.neto = neto;
	}

	public BigDecimal getTotalDesc() {
		return totalDesc;
	}

	public void setTotalDesc(BigDecimal totalDesc) {
		this.totalDesc = totalDesc;
	}

	public BigDecimal getPromo() {
		return promo;
	}

	public void setPromo(BigDecimal promo) {
		this.promo = promo;
	}

	public BigDecimal getArancel() {
		return arancel;
	}

	public void setArancel(BigDecimal arancel) {
		this.arancel = arancel;
	}

	public BigDecimal getIvaArancel() {
		return ivaArancel;
	}

	public void setIvaArancel(BigDecimal ivaArancel) {
		this.ivaArancel = ivaArancel;
	}

	public BigDecimal getCFOTotal() {
		return CFOTotal;
	}

	public void setCFOTotal(BigDecimal cFOTotal) {
		CFOTotal = cFOTotal;
	}

	public BigDecimal getCFO21() {
		return CFO21;
	}

	public void setCFO21(BigDecimal cFO21) {
		CFO21 = cFO21;
	}

	public BigDecimal getCFO105() {
		return CFO105;
	}

	public void setCFO105(BigDecimal cFO105) {
		CFO105 = cFO105;
	}

	public BigDecimal getCFOAdel() {
		return CFOAdel;
	}

	public void setCFOAdel(BigDecimal cFOAdel) {
		CFOAdel = cFOAdel;
	}

	public BigDecimal getIVACFO21() {
		return IVACFO21;
	}

	public void setIVACFO21(BigDecimal iVACFO21) {
		IVACFO21 = iVACFO21;
	}

	public BigDecimal getIVACFO105() {
		return IVACFO105;
	}

	public void setIVACFO105(BigDecimal iVACFO105) {
		IVACFO105 = iVACFO105;
	}

	public BigDecimal getIVAAdel21() {
		return IVAAdel21;
	}

	public void setIVAAdel21(BigDecimal iVAAdel21) {
		IVAAdel21 = iVAAdel21;
	}

	public BigDecimal getIVATotal() {
		return IVATotal;
	}

	public void setIVATotal(BigDecimal iVATotal) {
		IVATotal = iVATotal;
	}

	public BigDecimal getRetIIBB() {
		return RetIIBB;
	}

	public void setRetIIBB(BigDecimal retIIBB) {
		RetIIBB = retIIBB;
	}

	public BigDecimal getRetIBSIRTAC() {
		return RetIBSIRTAC;
	}

	public void setRetIBSIRTAC(BigDecimal retIBSIRTAC) {
		RetIBSIRTAC = retIBSIRTAC;
	}

	public BigDecimal getRetIVA() {
		return RetIVA;
	}

	public void setRetIVA(BigDecimal retIVA) {
		RetIVA = retIVA;
	}

	public BigDecimal getRetGcia() {
		return RetGcia;
	}

	public void setRetGcia(BigDecimal retGcia) {
		RetGcia = retGcia;
	}

	public BigDecimal getPercIVA() {
		return PercIVA;
	}

	public void setPercIVA(BigDecimal percIVA) {
		PercIVA = percIVA;
	}

	public BigDecimal getPercIIBB() {
		return PercIIBB;
	}

	public void setPercIIBB(BigDecimal percIIBB) {
		PercIIBB = percIIBB;
	}

	public BigDecimal getRetMunic() {
		return RetMunic;
	}

	public void setRetMunic(BigDecimal retMunic) {
		RetMunic = retMunic;
	}

	public BigDecimal getLiqAntTN() {
		return LiqAntTN;
	}

	public void setLiqAntTN(BigDecimal liqAntTN) {
		LiqAntTN = liqAntTN;
	}

	public BigDecimal getPerc1135TN() {
		return Perc1135TN;
	}

	public void setPerc1135TN(BigDecimal perc1135tn) {
		Perc1135TN = perc1135tn;
	}

	public BigDecimal getDtoFinanc() {
		return DtoFinanc;
	}

	public void setDtoFinanc(BigDecimal dtoFinanc) {
		DtoFinanc = dtoFinanc;
	}

	public BigDecimal getIVADtoFinanc() {
		return IVADtoFinanc;
	}

	public void setIVADtoFinanc(BigDecimal iVADtoFinanc) {
		IVADtoFinanc = iVADtoFinanc;
	}

	public BigDecimal getDebCred() {
		return DebCred;
	}

	public void setDebCred(BigDecimal debCred) {
		DebCred = debCred;
	}

	public BigDecimal getSaldos() {
		return saldos;
	}

	public void setSaldos(BigDecimal saldos) {
		this.saldos = saldos;
	}

	public BigDecimal getOtrosCostos() {
		return otrosCostos;
	}

	public void setOtrosCostos(BigDecimal otrosCostos) {
		this.otrosCostos = otrosCostos;
	}

	public BigDecimal getIVAOtros() {
		return IVAOtros;
	}

	public void setIVAOtros(BigDecimal iVAOtros) {
		IVAOtros = iVAOtros;
	}

	public BigDecimal getPlanA1218() {
		return planA1218;
	}

	public void setPlanA1218(BigDecimal planA1218) {
		this.planA1218 = planA1218;
	}

	public BigDecimal getIVAPlanA1218() {
		return IVAPlanA1218;
	}

	public void setIVAPlanA1218(BigDecimal iVAPlanA1218) {
		IVAPlanA1218 = iVAPlanA1218;
	}


	public BigDecimal getPorIvaPlana1218() {
		return PorIvaPlana1218;
	}

	public void setPorIvaPlana1218(BigDecimal porIvaPlana1218) {
		PorIvaPlana1218 = porIvaPlana1218;
	}

	public String getCUIT() {
		return CUIT;
	}

	public void setCUIT(String cUIT) {
		CUIT = cUIT;
	}
	
}
