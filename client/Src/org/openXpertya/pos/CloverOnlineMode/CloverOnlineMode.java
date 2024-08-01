package org.openXpertya.pos.CloverOnlineMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.openXpertya.pos.ctrl.PoSConfig;
import org.openXpertya.util.Env;

/**
 * Clase que permite interactuar al TPV con la terminal de cobro Clover
 * 
 * FLUJO
	A continuación, describimos brevemente el flujo de una transacción:
		La automación informa el número de cuotas y el valor de la transacción
		La lectura de la tarjeta y la captura de la contraseña se realiza en el Clover
		Clover realiza la transacción de TEF
		El cupón se imprime en Clover
		La automación recibe los datos de la transacción después de realizarla
		La automación confirma la transacción.
 * 
 * @author dREHER
 *
 */
public class CloverOnlineMode {
	
	private PoSConfig posConfig = null;
	private int cupon = 0;
	private int lote = 0;
	private int cuotas = 0;
	private BigDecimal montoCuotas = Env.ZERO;
	private BigDecimal montoTotal = Env.ZERO;
	private BigDecimal montoRetiraEfec = Env.ZERO;
	private String numeroTarjeta = "";
	private Date vtoTarjeta = null;
	private int CCV = 0; 
	private String nombreTitular = "";
	private int IDTransaccion = 0;
	private int errorCode = 0;
	
	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
	}

	public Date getVtoTarjeta() {
		return vtoTarjeta;
	}

	public void setVtoTarjeta(Date vtoTarjeta) {
		this.vtoTarjeta = vtoTarjeta;
	}

	public int getCCV() {
		return CCV;
	}

	public void setCCV(int cCV) {
		CCV = cCV;
	}

	public String getNombreTitular() {
		return nombreTitular;
	}

	public void setNombreTitular(String nombreTitular) {
		this.nombreTitular = nombreTitular;
	}

	public int getIDTransaccion() {
		return IDTransaccion;
	}

	public void setIDTransaccion(int iDTransaccion) {
		IDTransaccion = iDTransaccion;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public PoSConfig getPosConfig() {
		return posConfig;
	}

	public void setPosConfig(PoSConfig posConfig) {
		this.posConfig = posConfig;
	}

	public int getCupon() {
		return cupon;
	}

	public void setCupon(int cupon) {
		this.cupon = cupon;
	}

	public int getLote() {
		return lote;
	}

	public void setLote(int lote) {
		this.lote = lote;
	}

	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}

	public BigDecimal getMontoCuotas() {
		return montoCuotas;
	}

	public void setMontoCuotas(BigDecimal montoCuotas) {
		this.montoCuotas = montoCuotas;
	}

	public BigDecimal getMontoTotal() {
		return montoTotal;
	}

	public void setMontoTotal(BigDecimal montoTotal) {
		this.montoTotal = montoTotal;
	}

	public BigDecimal getMontoRetiraEfec() {
		return montoRetiraEfec;
	}

	public void setMontoRetiraEfec(BigDecimal montoRetiraEfec) {
		this.montoRetiraEfec = montoRetiraEfec;
	}

	/**
	 * Paso la configuracion del Pos
	 * @param posCfg
	 */
	public CloverOnlineMode(PoSConfig posCfg) {
		setPosConfig(posCfg);
	}
	
	/**
	 * Leo configuracion de datos para conectar con Clover
	 * 
	 * TODO: definir IP, puerto, usuario, clave, etc.
	 */
	public void leeConfigTerminal() {
		return;
	}
	
	/**
	 * Setea configuracion incial para comenzar flujo Clover
	 * Solo se activa una vez al día o al activar el CliSiTefPlus
	 */
	public int aperturaTerminal() {
		int codigoResp = 0;
		
		return codigoResp;
	}
	
	/**
	 * Inicia Venta o Devolucion
	 * Se ejecuta una sola vez al iniciar una venta o devolucion
	 * @return
	 */
	public int IniciaVenta(boolean isDevolucion) {
		int codigoResp = 0;
		
		return codigoResp;
	}
	
	/**
	 * Continua venta
	 * Es un loop hasta que devuelva un codigo de terminacion o error
	 * en esta rutina se ira leyendo la informacion recibida de clover y en
	 * funcion del resultado se iran guardando los datos correspondientes y
	 * terminando la transaccion o cancelandola si corresponde
	 * @return
	 */
	public int ContinuaVenta(){
		int codigoResp = 0;
		
		return codigoResp;
	}
	
	/**
	 * Finaliza la venta o devolucion (Aceptando o cancelando la misma)
	 */
	public int FinalizaVenta() {
		int codigoResp = 0;
		
		return codigoResp;
	}
	
	
}
