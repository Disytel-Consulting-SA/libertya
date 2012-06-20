package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Descripción de Interface
 *
 *
 * @version        2.1, 17.09.07
 * @author         Equipo de Desarrollo de openXpertya    
 */

public interface RetencionProcessor {

	/**
	 * Lee los parámetros asignados en C_RetSchemaConfig y se autoconfigura para poder
	 * calcular el monto de la retención.
	 * @param retSchema <code>MRetencionSchema</code> que contiene el esquema de
	 * retención utilizado para el cálculo de la retención.
	 */
	public void loadConfig(MRetencionSchema retSchema);
    
	
	/**
	 * Asigna la Entidad Comercial a la que se le aplica la retención.
	 */
	public void setBPartner(MBPartner bPartner);
	
	
	/**
	 * Agrega una factura a la lista de facturas pagadas, junto con el importe
	 * que se está pagando de dicha factura. Si el parámetro factura es <code>null</null>
	 * entonces el importe indica un monto por pago anticipado.
	 * @param inv <code>MInvoice</code> que contiene la factura a agregar.
	 * @param payamt <code>BigDecimal</code> con el importe pagado.
	 * @throws Exception en caso de que ocurra un error en el agregado de la factura.
	 */
	public void addInvoice(MInvoice inv, BigDecimal payamt) throws Exception;	


	/**
	 * Limpia la lista de facturas agregadas junto con la de importes pagados.
	 * @return
	 */
	public boolean clearAll();
	
	/**
	 * Asigna el encabezado de imputación del pago.
	 * @param allocHdr <code>MAllocationHdr</code> a asignar.
	 */
	public void setAllocationHrd(MAllocationHdr allocHdr);

	/**
	 * Retorna el monto a retener. Realiza el cálculo para determinar el importe.
	 * No debe recalcular el importe en cada llamado si no se modifican los parámetros
	 * de cálculo (no se invocó a <code>addInvoice(...)</code>, <code>clearAll()</code> o
	 * <code>setBPartner(...)</code>).
	 * @return <code>BigDecimal</code> con el monto calculado de la retención.
	 */
	public BigDecimal getAmount();


	/**
	 * Graba la retención aplicada junto con todos sus datos.
	 * @param alloc <code>MAllocationHdr</code> con la imputación de pagos.
	 * @return
	 * @throws Exception cuando se produce algún error en el guardado de los 
	 * datos de la retención.
	 */
	public boolean save(MAllocationHdr alloc) throws Exception;
	
	/**
	 * Asigna la moneda en la que se guardan los montos de la retención.
	 * @param currency <code>MCurrency</code> con la moneda a utilizar.
	 */
	public void setCurrency(MCurrency currency);

	/**
	 * @return Retorna el nombre del esquema de retención.
	 */
	public String getRetencionSchemaName();
	
	/**
	 * Asigna el esquema de retención que se utiliza para realizar los cálculos.
	 * @param schema <code>MRetencionSchema</code> con el esquema de retención.
	 */
	public void setRetencionSchema(MRetencionSchema schema);

	/**
	 * @return Retorna el nombre del tipo la retención.
	 */
	public String getRetencionTypeName();
	
	/**
	 * Asigna el nombre de la transacción a utilizar en todas las operaciones
	 * de BD.
	 * @param trxName Nombre de la transacción.
	 */
	public void setTrxName(String trxName);

	/**
	 * Asigna la fecha en la que se realiza el pago. Esta fecha es utilizada para
	 * calcular porcentajes de períodos de excepción. Por defecto, el valor será
	 * la fecha actual obtenida del Contexto.
	 */
	public void setDateTrx(Timestamp dateTrx);
	
	/**
	 * @return Retorna la fecha de transacción del pago y retención.
	 */
	public Timestamp getDateTrx();
	
	/**
	 * Asigna el monto de la retención. Está operación solo tiene efecto si el esquema
	 * de retención está configurado como Retención Sufrida.
	 * @param amount Monto a asignar.
	 */
	public void setAmount(BigDecimal amount);
		
	/**
	 * Retorna el esquema de retención asociado al procesador de retenciones.
	 */
	public MRetencionSchema getRetencionSchema();
	
	/**
	 * Asigna el número de retención manual a ser guardado en el comprobante de retención.
	 */
	public void setRetencionNumber(String retencionNumber);
	
	/**
	 * @return Retorna el número de retención manual. 
	 */
	public String getRetencionNumber();
		
	/**
	 * @param isSOTrx Asigna el valor isSOTrx utilizado para la generación
	 * de comprobantes y recibos de rewtenciones.
	 */
	public void setIsSOTrx(boolean isSOTrx);
	
	/**
	 * @return Devuelve el valor de IsSOTrx asociado a este procesador
	 */
	public boolean isSOTrx();
}
