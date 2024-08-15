package org.openXpertya.pos.paymentterminal;

import java.math.BigDecimal;

import org.openXpertya.apps.VTrxPaymentTerminalForm;
import org.openXpertya.util.Env;

/**
 * Interface para manejar los metodos genericos de transacciones de cobro en terminales de pago Ej: Clover 
 * 
 * @author dREHER
 */
public interface IPaymentTerminal {

	String cardNumber = "";
	String couponNumber = "";
	String couponBatchNumber = "";
	BigDecimal cashRetirementAmt = Env.ZERO;
	BigDecimal payAmt = Env.ZERO;
	String card = "";
	String cardType = "";
	String referenceID = "";
	String authorizationID = "";
	String transactionNumber = "";
	String AID = "";
	String documentNo = "";
	int cuotas = 0;
	String plan = "";
	String titularName = "";
	
	boolean isRetiraEfectivo = true;
	
	boolean isDebug = true;

	boolean isError = false;
	String errorMsg = "";
	
	String serviceName = "";
	
	/**
	 * Inicializa con info por default
	 */
	public void setDefault(); 
	
	/**
	 * Desconecta la terminal de pago
	 */
	public void exitConnect();
	
	/**
	 * Realiza la transaccion en la terminal de pago
	 * @param info de usuario de evolucion del pago
	 * @return true=Ok, false=error
	 */
	public boolean SaleRequest(VTrxPaymentTerminalForm info, String ip, String port);
	
	/**
	 * Cerrar el conector a la terminal
	 */
	public void close();
	
	
	/**
	 * Obtiene la informacion del servicio externo para poder realizar conexion
	 */
	public void getExternalService();
	
	/**
	 * Obtiene el dato de un atributo determinado
	 * @param attrib
	 * @return
	 */
	public String getExternalServiceAttribute(String attrib);
	
	/**
	 * Lee la url desde el servicio externo
	 * @return
	 */
	public String getUrl();
	
	/**
	 * Lee el puerto desde el servicio externo
	 * @return
	 */
	public int getPort();

	
	/**
	 * Propiedades del objeto PaymentTerminal
	 * @return
	 */
	public String getCardNumber();

	public void setCardNumber(String cardNumber);

	public String getCouponNumber();

	public void setCouponNumber(String couponNumber);

	public String getCouponBatchNumber();

	public void setCouponBatchNumber(String couponBatchNumber);

	public BigDecimal getCashRetirementAmt();

	public void setCashRetirementAmt(BigDecimal cashRetirementAmt);

	public BigDecimal getPayAmt();

	public void setPayAmt(BigDecimal payAmt);

	public String getCard();

	public void setCard(String card);

	public String getCardType();

	public void setCardType(String cardType);

	public String getReferenceID();

	public void setReferenceID(String referenceID);

	public String getAuthorizationID();

	public void setAuthorizationID(String authorizationID);

	public String getTransactionNumber();

	public void setTransactionNumber(String transactionNumber);

	public String getAID();

	public void setAID(String aID);

	public String getDocumentNo();

	public void setDocumentNo(String documentNo);

	public int getCuotas();

	public void setCuotas(int cuotas);

	public String getPlan();

	public void setPlan(String plan);

	public boolean isRetiraEfectivo();

	public void setRetiraEfectivo(boolean isRetiraEfectivo);

	public boolean isError();

	public void setError(boolean error);

	public String getErrorMsg();

	public void setErrorMsg(String errorMsg);

	public String getTitularName();

	public void setTitularName(String titularName);

	
	
}
