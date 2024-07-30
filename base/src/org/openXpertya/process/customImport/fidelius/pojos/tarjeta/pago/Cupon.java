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

public class Cupon extends GenericDatum{

	@SerializedName("fpag")
	@Expose
	private Timestamp fechaPago;
	
	@SerializedName("fvta")
	@Expose
	private Timestamp fechaVenta;
	
	@SerializedName("fant")
	@Expose
	private Timestamp fechaAnticipo;
	
	@SerializedName("nroliq")
	@Expose
	private Long nroLiquidacion;
	
	@SerializedName("nroequipo")
	@Expose
	private int nroEquipo;

	@SerializedName("nomequipo")
	@Expose
	private String nomEquipo;

	@SerializedName("nrolote")
	@Expose
	private int nroLote;

	@SerializedName("nrocupon")
	@Expose
	private int nroCupon;

	@SerializedName("tarjeta")
	@Expose
	private String tarjeta;

	@SerializedName("ult4tarjeta")
	@Expose
	private int Ult4tarjeta;

	@SerializedName("autorizacion")
	@Expose
	private String autorizacion;

	@SerializedName("cuotas")
	@Expose
	private int cuotas;

	@SerializedName("imp_vta")
	@Expose
	private BigDecimal ImporteVenta;
	
	@SerializedName("extra_cash")
	@Expose
	private String ExtraCash;
	
	@SerializedName("num_com")
	@Expose
	private String nroComercio;
		
	@SerializedName("bancopag")
	@Expose
	private String bancoPagador;
	
	@SerializedName("rechazo")
	@Expose
	private String Rechazo;
	
	@SerializedName("arancel")
	@Expose
	private BigDecimal arancel;
	
	@SerializedName("iva_arancel")
	@Expose
	private BigDecimal ivaArancel;

	@SerializedName("cfo")
	@Expose
	private BigDecimal CFO;
	
	@SerializedName("iva_cfo")
	@Expose
	private BigDecimal IvaCFO;

	@SerializedName("alic_ivacfo")
	@Expose
	private BigDecimal AlicIvaCFO;
	
	@SerializedName("tipo_oper")
	@Expose
	private String TipoOperacion;
	
	@SerializedName("id_unico")
	@Expose
	private String IDUnico;
	
	@SerializedName("revisado")
	@Expose
	private String totalDesc;

	public Cupon() {}

	public BigDecimal getImporteVenta() {
		return ImporteVenta;
	}

	public void setImporteVenta(BigDecimal importeVenta) {
		ImporteVenta = importeVenta;
	}

	public Timestamp getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Timestamp fechaPago) {
		this.fechaPago = fechaPago;
	}

	public Timestamp getFechaVenta() {
		return fechaVenta;
	}

	public void setFechaVenta(Timestamp fechaVenta) {
		this.fechaVenta = fechaVenta;
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

	public int getNroEquipo() {
		return nroEquipo;
	}

	public void setNroEquipo(int nroEquipo) {
		this.nroEquipo = nroEquipo;
	}

	public String getNomEquipo() {
		return nomEquipo;
	}

	public void setNomEquipo(String nomEquipo) {
		this.nomEquipo = nomEquipo;
	}

	public int getNroLote() {
		return nroLote;
	}

	public void setNroLote(int nroLote) {
		this.nroLote = nroLote;
	}

	public int getNroCupon() {
		return nroCupon;
	}

	public void setNroCupon(int nroCupon) {
		this.nroCupon = nroCupon;
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}

	public int getUlt4tarjeta() {
		return Ult4tarjeta;
	}

	public void setUlt4tarjeta(int ult4tarjeta) {
		Ult4tarjeta = ult4tarjeta;
	}

	public String getAutorizacion() {
		return autorizacion;
	}

	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}

	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}

	public String getExtraCash() {
		return ExtraCash;
	}

	public void setExtraCash(String extraCash) {
		ExtraCash = extraCash;
	}

	public String getNroComercio() {
		return nroComercio;
	}

	public void setNroComercio(String nroComercio) {
		this.nroComercio = nroComercio;
	}

	public String getBancoPagador() {
		return bancoPagador;
	}

	public void setBancoPagador(String bancoPagador) {
		this.bancoPagador = bancoPagador;
	}

	public String getRechazo() {
		return Rechazo;
	}

	public void setRechazo(String rechazo) {
		Rechazo = rechazo;
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

	public BigDecimal getCFO() {
		return CFO;
	}

	public void setCFO(BigDecimal cFO) {
		CFO = cFO;
	}

	public BigDecimal getIvaCFO() {
		return IvaCFO;
	}

	public void setIvaCFO(BigDecimal ivaCFO) {
		IvaCFO = ivaCFO;
	}

	public BigDecimal getAlicIvaCFO() {
		return AlicIvaCFO;
	}

	public void setAlicIvaCFO(BigDecimal alicIvaCFO) {
		AlicIvaCFO = alicIvaCFO;
	}

	public String getTipoOperacion() {
		return TipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		TipoOperacion = tipoOperacion;
	}

	public String getIDUnico() {
		return IDUnico;
	}

	public void setIDUnico(String iDUnico) {
		this.IDUnico = iDUnico;
	}

	public String getTotalDesc() {
		return totalDesc;
	}

	public void setTotalDesc(String totalDesc) {
		this.totalDesc = totalDesc;
	}
	
}
