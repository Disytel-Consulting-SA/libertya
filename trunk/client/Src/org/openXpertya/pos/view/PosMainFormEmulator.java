package org.openXpertya.pos.view;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.pos.ctrl.PoSConfig;
import org.openXpertya.pos.model.BusinessPartner;
import org.openXpertya.pos.model.CashPayment;
import org.openXpertya.pos.model.OrderProduct;
import org.openXpertya.pos.model.Payment;
import org.openXpertya.pos.model.PaymentMedium;
import org.openXpertya.pos.model.PaymentTerm;
import org.openXpertya.pos.model.Product;
import org.openXpertya.pos.model.Tax;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeStatsLogger;


public class PosMainFormEmulator extends PoSMainForm {

	// LY specifics
	public static final int AD_CLIENT_ID = 1010016;
	public static final int AD_ORG_ID = 1010053;
	public static final int AD_ROLE_ID = 1010074;
	public static final String AD_LANGUAGE = "es_AR";
	public static int AD_USER_ID = 1010734;
	
	// Default iterations
	public static int TPV_SIMULATIONS = 200;
	public static int TPV_LINES_PER_SIMULATION = 1;
	
	// Simulation data
	public static final int 	TPV_BPARTNER_CUSTOMER_ID = 1012142; // Consumidor Final
	public static final int 	TPV_BPARTNER_CUSTOMER_LOCATION_ID = 1012157; 
	public static final int 	TPV_CURRENCY_ID = 118; // Argentine Peso
	public static final String 	TPV_BPARTNER_TAX_ID = "";
	public static final String 	TPV_BPARTNER_NAME = "Consumidor Final";
	public static final String 	TPV_TENDER_TYPE = "CA";
	public static final String 	TPV_TENDER_NAME = "Efectivo";
	
	public static final String 	TPV_TIME_LOG_CONF = "Configurar venta TPV";
	public static final String 	TPV_TIME_LOG_SELL = "Construct venta";
	public static final String 	TPV_TIME_LOG_WAIT = "Espera entre venta";
	
	// Espera entre venta y venta
	public static int 	TPV_MAX_LOOP_WAIT_ID = 2000; 
	
	public static void main(String[] args)
	{
		
		// Redefinir iteraciones
		if (args.length > 0)
			TPV_SIMULATIONS = Integer.parseInt(args[0]);
		// Redefinir lineas		
		if (args.length > 1)
			TPV_LINES_PER_SIMULATION = Integer.parseInt(args[1]);
		if (args.length > 2)
			AD_USER_ID = Integer.parseInt(args[2]);
		if (args.length > 3)
			TPV_MAX_LOOP_WAIT_ID = Integer.parseInt(args[3]);		

		
    	if (!org.openXpertya.OpenXpertya.startupEnvironment( true ))
    	{
    		System.err.println("Error al iniciar la configuración de replicacion");
    		return;
    	}
        Env.setContext(Env.getCtx(), "#AD_Client_ID", AD_CLIENT_ID);
        Env.setContext(Env.getCtx(), "#AD_Org_ID", AD_ORG_ID);
        Env.setContext(Env.getCtx(), "#AD_User_ID", AD_USER_ID);
        Env.setContext(Env.getCtx(), "#AD_Role_ID", AD_ROLE_ID);
//        Env.setContext(Env.getCtx(), "#AD_Language", AD_LANGUAGE);  <- comentado para que no imprima el reporte en pantalla
        
		PosMainFormEmulator mainForm = new PosMainFormEmulator();
		mainForm.init(0, new FormFrame());
		
		// Configuraciones TPV
		List<PoSConfig> posConfigs = mainForm.getModel().getPoSConfigs();
		PoSConfig poSConfig = posConfigs.get(0);
		poSConfig.setCurrencyID(TPV_CURRENCY_ID);
		
		// Iterar simulando ventas
		for (int i = 0; i < TPV_SIMULATIONS; i++)
		{
			System.out.println( " ========================= " + i + " ======================== ");
			TimeStatsLogger.beginTask(TPV_TIME_LOG_CONF);
			
			// Entidad Comercial
			BusinessPartner aBP = new BusinessPartner(TPV_BPARTNER_CUSTOMER_ID, TPV_BPARTNER_CUSTOMER_LOCATION_ID, TPV_BPARTNER_TAX_ID, TPV_BPARTNER_NAME);
			// Impuesto
			Tax aTax = new Tax(1010085, Env.ZERO);
			// Articulos
			ArrayList<OrderProduct> productsList = new ArrayList<OrderProduct>();
			for (int j = 0; j < TPV_LINES_PER_SIMULATION; j++)
			{
				Product aProduct = new Product(1032483, "999-999", "ART-LIB", BigDecimal.ONE, BigDecimal.ONE, 0, "---", true, false, 1010402, new ArrayList<Integer>(), "B", true);      
				OrderProduct anOrderProduct = new OrderProduct(1, BigDecimal.ZERO, aTax, aProduct, "P");
				productsList.add(anOrderProduct);
			}
			// Medio de Pagos
			PaymentMedium aPaymentMedium = new PaymentMedium(1010005, TPV_TENDER_NAME, TPV_TENDER_TYPE, TPV_CURRENCY_ID);
			// Pagos
			Payment aPayment = new CashPayment(BigDecimal.ONE.multiply(new BigDecimal(TPV_LINES_PER_SIMULATION))); 
			aPayment.setAmount(BigDecimal.ONE.multiply(new BigDecimal(TPV_LINES_PER_SIMULATION)));
			aPayment.setCurrencyId(TPV_CURRENCY_ID);
			aPayment.setTenderType(TPV_TENDER_TYPE);
			aPayment.setTypeName(TPV_TENDER_NAME);
			aPayment.setPaymentMedium(aPaymentMedium);
			ArrayList<Payment> paymentsList = new ArrayList<Payment>();
			paymentsList.add(aPayment);

			// Esquema de pago
			PaymentTerm aPaymentTerm = new PaymentTerm(1010083, "Inmediato", 1010005);
			
			// Setear todo al pedido
			mainForm.getOrder().setPaymentTerm(aPaymentTerm);
			mainForm.getOrder().setBusinessPartner(aBP);
			mainForm.getOrder().setChangeAmount(BigDecimal.ZERO);
			mainForm.getOrder().setDate(new Timestamp(new Date().getTime()));
			mainForm.getOrder().setPayments(paymentsList);
			mainForm.getOrder().setOrderProducts(productsList);
			mainForm.getModel().setPoSConfig(poSConfig);
			
			TimeStatsLogger.endTask(TPV_TIME_LOG_CONF);
			
			// Invocar la venta
//			mainForm.completeOrder();	<-- no se utiliza para evitar el uso de SwingWorker

			TimeStatsLogger.beginTask(TPV_TIME_LOG_SELL);
			mainForm.construct();
			TimeStatsLogger.endTask(TPV_TIME_LOG_SELL);	
			
		}
		
		System.exit(0);
		
	}
	
		public void construct() {
			try {
				getModel().setDocActionStatusListener(docActionStatusListener);
				getModel().setFiscalPrintListeners(infoFiscalPrinter, infoFiscalPrinter);
				getModel().completeOrder();
		
				// Espera aleatoria 
				TimeStatsLogger.beginTask(TPV_TIME_LOG_WAIT);
				Random rnd = new Random(); 
				Thread.sleep(rnd.nextInt(TPV_MAX_LOOP_WAIT_ID));
				TimeStatsLogger.endTask(TPV_TIME_LOG_WAIT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
}
