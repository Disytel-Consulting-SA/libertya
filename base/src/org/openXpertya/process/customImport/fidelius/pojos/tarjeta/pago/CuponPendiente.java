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

public class CuponPendiente extends GenericDatum{

	@SerializedName("fechaoper")
	@Expose
	private Timestamp fechaOper;
	
	@SerializedName("horaoper")
	@Expose
	private String horaOper;
	
	@SerializedName("nroterminal")
	@Expose
	private Long nroTerminal;
	
	@SerializedName("equipo")
	@Expose
	private String Equipo;
	
	@SerializedName("nombre_comerc")
	@Expose
	private String nombreComerc;

	@SerializedName("tipotrx")
	@Expose
	private String tipoTrx;

	@SerializedName("id_clover")
	@Expose
	private String idClover;

	@SerializedName("codcom")
	@Expose
	private String codCom;

	@SerializedName("nrolote")
	@Expose
	private int nroLote;

	@SerializedName("ticket")
	@Expose
	private int Ticket;

	@SerializedName("codaut")
	@Expose
	private String codAut;

	@SerializedName("factura")
	@Expose
	private String Factura;

	@SerializedName("tarjeta")
	@Expose
	private String Tarjeta;
	
	@SerializedName("nrotarjeta")
	@Expose
	private String nroTarjeta;
	
	@SerializedName("cuota_tipeada")
	@Expose
	private int cuotaTipeada;
		
	@SerializedName("importe")
	@Expose
	private BigDecimal Importe;
	
	@SerializedName("montosec")
	@Expose
	private BigDecimal montoSec;
	
	@SerializedName("fechapagoest")
	@Expose
	private Timestamp fechaPagoEst;
	
	@SerializedName("id")
	@Expose
	private int Id;
	

	public CuponPendiente() {}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public Timestamp getFechaOper() {
		return fechaOper;
	}

	public void setFechaOper(Timestamp fechaOper) {
		this.fechaOper = fechaOper;
	}

	public String getHoraOper() {
		return horaOper;
	}

	public void setHoraOper(String horaoper) {
		this.horaOper = horaoper;
	}

	public Timestamp getFechaPagoEst() {
		return fechaPagoEst;
	}

	public void setFechaPagoEst(Timestamp fechaPagoEst) {
		this.fechaPagoEst = fechaPagoEst;
	}

	public Long getNroTerminal() {
		return nroTerminal;
	}

	public void setNroTerminal(Long nroTerminal) {
		this.nroTerminal = nroTerminal;
	}

	public String getEquipo() {
		return Equipo;
	}

	public void setEquipo(String Equipo) {
		this.Equipo = Equipo;
	}

	public String getNombre_Comerc() {
		return nombreComerc;
	}

	public void setNombre_Comerc(String nomCom) {
		this.nombreComerc = nomCom;
	}

	public int getNroLote() {
		return nroLote;
	}

	public void setNroLote(int nroLote) {
		this.nroLote = nroLote;
	}
	
	public String getTipoTrx() {
		return tipoTrx;
	}

	public void setTipoTrx(String tipoTrx) {
		this.tipoTrx = tipoTrx;
	}
	
	public String getId_Clover() {
		return idClover;
	}

	public void setId_Clover(String idClover) {
		this.idClover = idClover;
	}

	public int getTicket() {
		return Ticket;
	}

	public void setTicket(int ticket) {
		this.Ticket = ticket;
	}
	
	public String getCodAut() {
		return codAut;
	}

	public void setCodAut(String codAut) {
		this.codAut = codAut;
	}
	
	public String getCodCom() {
		return codCom;
	}

	public void setCodCom(String codCom) {
		this.codCom = codCom;
	}
	
	public String getFactura() {
		return Factura;
	}

	public void setFactura(String factura) {
		this.Factura = factura;
	}

	public String getTarjeta() {
		return Tarjeta;
	}

	public void setTarjeta(String tarjeta) {
		this.Tarjeta = tarjeta;
	}

	public String getNroTarjeta() {
		return nroTarjeta;
	}

	public void setNroTarjeta(String nrotarjeta) {
		nroTarjeta = nrotarjeta;
	}
	
	public int getCuotaTipeada() {
		return cuotaTipeada;
	}

	public void setCuotaTipeada(int cuota) {
		this.cuotaTipeada = cuota;
	}

	public BigDecimal getImporte() {
		return Importe;
	}

	public void setImporte(BigDecimal importe) {
		this.Importe = importe;
	}

	public BigDecimal getMontoSec() {
		return montoSec;
	}

	public void setMontoSec(BigDecimal montoSec) {
		this.montoSec = montoSec;
	}
	
}
