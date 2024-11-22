package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Vector;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;


public class GeneratorRetenciones {
	
	
	/** Descripción de Campos */
	
	protected CLogger log = CLogger.getCLogger(SvrProcess.class);
	private RetencionProcessor m_matcher = null;
	private int c_BPartner_ID = 0;
	private String TrxName = "";
	private Properties m_ctx = Env.getCtx();
	
	/* Datos de la facturas a aplicar que debo obtenerlo desde afuera */
	private Vector<Integer> m_facturas = null;
	private Vector<BigDecimal> m_facturasManualAmounts = null;
	private Vector<X_M_Retencion_Invoice> m_retenciones = new Vector<X_M_Retencion_Invoice>(); 
	private BigDecimal m_FacturasTotalPagar = Env.ZERO;
	private Integer m_C_Currency_ID = null;
	private boolean pago_anticipado;
	private java.sql.Timestamp vfechaPago;
	private boolean isSOTrx;
	private Integer projectID = 0;
	private Integer campaignID = 0;
	private String paymentRule;
	/* Listado de retenciones */
	
	Vector<RetencionProcessor> lista_retenciones = new Vector<RetencionProcessor>();
	
	/* Constructores */
	public GeneratorRetenciones(int C_BPartner_ID, Vector<Integer> m_factura, Vector<BigDecimal> m_facturaManualAmount,BigDecimal amttotal, boolean isSOTrx, String paymentRule){	
		// seteo de variables
		this.setC_BPartner_ID(C_BPartner_ID);
		this.m_facturas = m_factura;
		this.m_facturasManualAmounts = m_facturaManualAmount;
		this.pago_anticipado =(m_factura.isEmpty() | m_factura == null);
		this.isSOTrx = isSOTrx;
		this.setM_FacturasTotalPagar(amttotal);
		int C_Currency_ID = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID"); //
		MCurrency currency = new MCurrency(Env.getCtx(),C_Currency_ID,null);
		this.setM_C_Currency_ID(currency);
		this.setPago_anticipado(m_facturas.isEmpty());
		setPaymentRule(paymentRule);
		
		/*
			TODO: si la logica es que debe cargar todas las facturas del periodo, sin importar si estan o no afectadas por el pago, hay q agregar un metodo que agregue
			a m_factura todas las demas facturas que no llegan en el vector m_factura
		
			Por otro lado tambien hay q agregar en m_facturaManualAmount el monto del saldo abierto de cada una, ya que aca llega cuanto se esta pagando por cada factura
	
			dREHER Nov-24
		 */
		
	}
	
	public GeneratorRetenciones(int C_BPartner_ID, Vector<Integer> m_factura, Vector<BigDecimal> m_facturaManualAmount,BigDecimal amttotal, boolean isSOTrx, Timestamp dateTrx, String paymentRule){
		// seteo de variables
		this.setC_BPartner_ID(C_BPartner_ID);
		this.m_facturas = m_factura;
		this.m_facturasManualAmounts = m_facturaManualAmount;
		this.pago_anticipado =(m_factura.isEmpty() | m_factura == null);
		this.isSOTrx = isSOTrx;
		this.setM_FacturasTotalPagar(amttotal);
		int C_Currency_ID = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID"); //
		MCurrency currency = new MCurrency(Env.getCtx(),C_Currency_ID,null);
		this.setM_C_Currency_ID(currency);
		this.setPago_anticipado(m_facturas.isEmpty());
		this.vfechaPago = dateTrx;
		setPaymentRule(paymentRule);
		
		/*
			TODO: si la logica es que debe cargar todas las facturas del periodo, sin importar si estan o no afectadas por el pago, hay q agregar un metodo que agregue
			a m_factura todas las demas facturas que no llegan en el vector m_factura
			
			Por otro lado tambien hay q agregar en m_facturaManualAmount el monto del saldo abierto de cada una, ya que aca llega cuanto se esta pagando por cada factura
		
			dREHER Nov-24
		*/
	}
	
	private void setM_C_Currency_ID(MCurrency currency) {	
		m_C_Currency_ID = currency.getC_Currency_ID();
		
	}
	
	/* Funcionalidad */
	public Vector<RetencionProcessor> getRetenciones(){	
		return lista_retenciones;
	} 
	
	
	public void evaluarRetencion() {

		MBPartner bpr = new MBPartner(getM_ctx(),getC_BPartner_ID(), getTrxName());
		Vector<MRetencionSchema> esquemas = bpr.get_EsquemasRetencion();

		for (int i = 0; i < esquemas.size(); i++){
			addRetencion(esquemas.get(i));
		}
	}
	
	private boolean cargarFacturas(RetencionProcessor procesador) throws Exception{
		/*
		 * 	si (pago_anticipado)
		 * 		procesador.addInvoice(null, importe_a_Pagar) 
		 * 	else
		 * 		para cada factura fc con importe a pagar
		 * 	procesador.addInvoice(fc, importe_a_pagar(fc))
		 *  
		 */
		//this.setPago_anticipado(m_facturas.isEmpty());
		boolean invoiceLoaded = false;
		if(isPago_anticipado()){
			procesador.addInvoice(null, getM_FacturasTotalPagar());
		}else{
			
// TODO: para el caso de que el C_BPartner_ID comparta CUIT's cargar TODAS LAS FACTURAS DEL PERIODO
/*
Al realizar pagos a un proveedor que comparte el mismo CUIT con otros, debes considerar el monto total de los comprobantes emitidos por ese CUIT durante el período fiscal
(por ejemplo, el mes) para determinar la base de cálculo de las retenciones, incluso si solo estás pagando una factura en ese momento.
Esto se debe a que las retenciones se calculan sobre la base acumulada de las operaciones realizadas con el mismo CUIT, independientemente de cuántos pagos se efectúen o 
a cuántos comprobantes correspondan esos pagos.

Cómo Funciona en la Práctica:
-----------------------------

Acumulación por CUIT:
---------------------

Aunque pagues una sola factura, debes considerar el total facturado en el período (en este caso, las tres facturas: $36.340,59 + $40.516,48 + $201.946,49 = $278.803,56) 
para calcular las retenciones.
Si realizas un pago parcial, la retención se calcula sobre la proporción correspondiente, pero siempre considerando la base total acumulada.

Ejemplo: Pago de la Primera Factura

Supongamos que pagas únicamente la factura de $36.340,59.
El total facturado por el CUIT en el período es $278.803,56.
Si la alícuota de retención de Ingresos Brutos es del 3%, deberás calcular la retención considerando el monto total facturado en el período, 
no solo el importe de la factura que estás pagando.

Cálculo:
--------

Base acumulada: $278.803,56
Alícuota: 3% 
	Retencion total acumulada=278.803,56 × 0,03 = 8.364,11
Ahora, calcula la proporción correspondiente al monto que estás pagando:
Retencion sobre el pago = Monto del pago / Monto total facturado × Retencion total acumulada
Retencion sobre el pago = 36.340,59 / 278.803,56 × 8.364,11 = 1.090,72

Por lo tanto, al pagar esta primera factura, deberás retener $1.090,72.

Pagos Posteriores:

Cuando realices los pagos de las otras facturas, deberás considerar las retenciones acumuladas menos lo que ya hayas retenido en pagos anteriores.
			 */
			 
			
			for(int i = 0; i< m_facturas.size(); i++){
				MInvoice fc;
				MDocType dt;
				if(m_facturasManualAmounts.get(i).compareTo(Env.ZERO)>0){
					fc = new MInvoice(m_ctx,m_facturas.get(i),getTrxName());
					dt = MDocType.get(m_ctx, fc.getC_DocTypeTarget_ID());
					if(dt.isApplyRetention()) {
						procesador.addInvoice(fc,m_facturasManualAmounts.get(i));
						invoiceLoaded = true;
					}
				}
			}
		}
		return isPago_anticipado() || invoiceLoaded;
	};
	
	
	
	private void setC_BPartner_ID(int c_BPartner_ID) {
		this.c_BPartner_ID = c_BPartner_ID;
	}
	
	private int getC_BPartner_ID() {
		return c_BPartner_ID;
	}
	
	public void setTrxName(String trxName) {
		TrxName = trxName;
	}
	
	private String getTrxName() {
		return TrxName;
	}
	
	private Properties getM_ctx() {
		return m_ctx;
	}
	
	
	public Vector<X_M_Retencion_Invoice> getM_retenciones() {
		return m_retenciones;
	}
	
	public void setM_facturas(Vector<Integer> m_facturas) {
		this.m_facturas = m_facturas;
	}
	
	
	public void setM_FacturasTotalPagar(BigDecimal m_FacturasTotalPagar) {
		this.m_FacturasTotalPagar = m_FacturasTotalPagar;
	}
	
	public BigDecimal getM_FacturasTotalPagar() {
		return m_FacturasTotalPagar;
	}
	
	public void setM_C_Currency_ID(Integer m_C_Currency_ID) {
		this.m_C_Currency_ID = m_C_Currency_ID;
	}
	
	public Integer getM_C_Currency_ID() {
		return m_C_Currency_ID;
	}
	
	private void setPago_anticipado(boolean pago_anticipado) {
		this.pago_anticipado = pago_anticipado;
	}
	
	private boolean isPago_anticipado() {
		return pago_anticipado;
	}
	
	
	public void save(MAllocationHdr alloc) throws Exception{
		// guarda todas las retenciones
		
		for(int i=0; i<= lista_retenciones.size()-1; i++){
			lista_retenciones.get(i).setTrxName(getTrxName());
			lista_retenciones.get(i).setProjectID(getProjectID());
			lista_retenciones.get(i).setCampaignID(getCampaignID());
			lista_retenciones.get(i).setPaymentRule(getPaymentRule());
			m_retenciones.addAll(lista_retenciones.get(i).save(alloc, true));
			//lista_retenciones.get(i).save(alloc);
		}
	}
	
	/**
	 * Agrega un procesador de retención a partir de un esquema, a la lista de retenciones 
	 * que contiene el generador.
	 * @param retencionSchemaID ID del esquema de retención.
	 * @return Retorna la instancia del procesador de la retención recientemente agregado.
	 */
	public RetencionProcessor addRetencion(int retencionSchemaID) {
		return addRetencion(new MRetencionSchema(getM_ctx(), retencionSchemaID, getTrxName()));
	}
	
	private RetencionProcessor addRetencion(MRetencionSchema retSchema ) {
		// buscar clase asociada que realiza el procesamiento de ese esquema de retencion
		String nameClass = retSchema.getProcessorClass();
		// agregar a la lista de retenciones(para guardarlo despues)
		BigDecimal importeRetencion = Env.ZERO;
		RetencionProcessor retProcessor = null;
		try {
			Class procesador = Class.forName(nameClass);
			try {
				retProcessor = ( RetencionProcessor )procesador.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			retProcessor.setIsSOTrx(isSOTrx()); 
			retProcessor.loadConfig(retSchema);
			retProcessor.setDateTrx(vfechaPago);
			retProcessor.setBPartner(new MBPartner(Env.getCtx(),getC_BPartner_ID(), getTrxName()));		
			retProcessor.setCurrency(new MCurrency(getM_ctx(),getM_C_Currency_ID().intValue(),getTrxName()));
			retProcessor.setRetencionSchema(retSchema);
			retProcessor.setTrxName(getTrxName());
			//vfechaPago = java.sql.Timestamp.valueOf(Env.getContextAsDate(Env.getCtx(), "#Date").toString());
			if(cargarFacturas(retProcessor)) {
				importeRetencion = retProcessor.getAmount();	
			}
			
			if(retSchema.isSufferedRetencion() || importeRetencion.compareTo(Env.ZERO)> 0){
				lista_retenciones.add(retProcessor);
			}
			
		} catch (ClassNotFoundException e) {
			String errorMessage  = "ClassNotLoaded";
			String errorDescription = e.getMessage();
			log.info("Error: "+ errorMessage +" descripcion: "+ errorDescription);
		} catch (Exception e) {
			log.info("Error :" + e);
			e.printStackTrace();
		}

		return retProcessor;
	}

	/**
	 * @return the isSOTrx
	 */
	public boolean isSOTrx() {
		return isSOTrx;
	}

	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
	}

	public Integer getProjectID() {
		return projectID;
	}

	public void setCampaignID(Integer campaignID) {
		this.campaignID = campaignID;
	}

	public Integer getCampaignID() {
		return campaignID;
	}
	public String getPaymentRule() {
		return paymentRule;
	}
	public void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
	}
}
