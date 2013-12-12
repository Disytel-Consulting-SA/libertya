package org.openXpertya.apps.form;

import java.math.BigDecimal;

import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.util.Env;

public class OPRCTerminalTest {

	public static void main(String[] args) 
	{
		initEnv();

	  	createOPA();
	  	createOP();
	  	createRCA();
	  	createRC();
	}
	
	
	public static void initEnv() {
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null) { 
	  		System.err.println("ERROR: La variable de entorno OXP_HOME no está seteada ");
	  		System.exit(1);
	  	}
	  	
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false )) {
	  		System.err.println("ERROR: Error al iniciar la configuracion... Postgres esta levantado?");
	  		System.exit(1);
	  	}
	  	
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", 1010016);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", 1010053);
	  	Env.setContext(Env.getCtx(), "$C_Currency_ID", 118);
	}
	
	
	public static void createOPA() {
	  	/* === Orden de Pago Adelantada === */ 
	  	VOrdenPagoModel opm = new VOrdenPagoModel();
	  	opm.setPagoNormal(false, new BigDecimal(100));
	  	// === 1. Definir Entidad comercial y demás datos ===
	  	opm.setBPartnerFacturas(1012142);
	  	// Setear fecha de factura ¿?
	  	opm.setFechaTablaFacturas(Env.getDate(), true);
	  	// Nro y tipo documento obligatorio
	  	opm.setDocumentNo((""+System.currentTimeMillis()).substring(4));
	  	opm.setDocumentType(1010569);
	  	// === 2. Medios de Pago ===
	  	opm.doPreProcesar();
	  	// Efectivo
	  	VOrdenPagoModel.MedioPagoEfectivo mpe = opm.new MedioPagoEfectivo(1010070, new BigDecimal(30), 118);
	  	// Cheque
	  	VOrdenPagoModel.MedioPagoCheque mpc = opm.new MedioPagoCheque(1010085, "9999", new BigDecimal(70), Env.getDate(), Env.getDate(), "a la orden!");
	  	// Agregar medios de pago
	  	opm.addMedioPago(mpe);
	  	opm.addMedioPago(mpc);
	  	// === 3. Efectivizar el pago ===
	  	System.out.println(VOrdenPagoModel.PROCERROR_OK==opm.doPostProcesar()?"Yeah!":"Doh!");
	}
	
	public static void createOP() {
	  	/* === Orden de Pago Normal === */
	  	VOrdenPagoModel opm2 = new VOrdenPagoModel();
	  	opm2.setPagoNormal(true, null);
	  	// === 1. Definir Entidad comercial y demás datos ===  
	  	//Al setear este campo tambien se inicializan las estructuras de facturas a cancelar
	  	opm2.setBPartnerFacturas(1012142);
	  	// Setear fecha de factura
	  	opm2.setFechaTablaFacturas(Env.getDate(), true);
	  	// Nro y tipo documento obligatorio
	  	opm2.setDocumentNo((""+System.currentTimeMillis()).substring(4));
	  	opm2.setDocumentType(1010569);
	  	// Agregar factura a pagar 
	  	MInvoice inv1 = new MInvoice(Env.getCtx(), 1021703, null);
	  	opm2.addDebit(inv1, new BigDecimal(50));
	  	MInvoice inv2 = new MInvoice(Env.getCtx(), 1021706, null);
	  	opm2.addDebit(inv2, new BigDecimal(150));
	  	// === 2. Medios de Pago ===
	  	opm2.doPreProcesar();
	  	// Efectivo
	  	VOrdenPagoModel.MedioPagoEfectivo mpe2 = opm2.new MedioPagoEfectivo(1010070, new BigDecimal(130), 118);
	  	// Cheque
	  	VOrdenPagoModel.MedioPagoCheque mpc2 = opm2.new MedioPagoCheque(1010085, "9999", new BigDecimal(70), Env.getDate(), Env.getDate(), "a la orden!");
	  	// Agregar medios de pago
	  	opm2.addMedioPago(mpe2);
	  	opm2.addMedioPago(mpc2);
	  	// === 3. Efectivizar el pago ===
	  	System.out.println(VOrdenPagoModel.PROCERROR_OK==opm2.doPostProcesar()?"Yeah!":"Doh!");
	}
	
	public static void createRCA() {
	  	/* === Recibo de cliente Adelantado === */ 
	  	VOrdenCobroModel ocm = new VOrdenCobroModel();
	  	ocm.setPagoNormal(false, new BigDecimal(100));
	  	// === 1. Definir Entidad comercial y demás datos ===
	  	ocm.setBPartnerFacturas(1012142);
	  	// Setear fecha de factura ¿?
	  	ocm.setFechaTablaFacturas(Env.getDate(), true);
	  	// Nro y tipo documento obligatorio
	  	ocm.setDocumentNo((""+System.currentTimeMillis()).substring(4));
	  	ocm.setDocumentType(1010569);
	  	// === 2. Medios de Pago ===
	  	ocm.doPreProcesar();
	  	// Efectivo (con su PosPaymentMedium)
	  	VOrdenPagoModel.MedioPagoEfectivo mpe = ocm.new MedioPagoEfectivo(1010070, new BigDecimal(30), 118);
	  	MPOSPaymentMedium ppme = new MPOSPaymentMedium(Env.getCtx(), 1010041, null);
	  	mpe.setPaymentMedium(ppme);
	  	// Cheque (con su PosPaymentMedium)
	  	VOrdenPagoModel.MedioPagoCheque mpc = ocm.new MedioPagoCheque(1010085, "9999", new BigDecimal(70), Env.getDate(), Env.getDate(), "a la orden!");
	  	MPOSPaymentMedium ppmc = new MPOSPaymentMedium(Env.getCtx(), 1010043, null);	  	
	  	mpc.setPaymentMedium(ppmc);
	  	// Agregar medios de pago
	  	ocm.addMedioPago(mpe);
	  	ocm.addMedioPago(mpc);
	  	// === 3. Efectivizar el pago ===
	  	System.out.println(VOrdenPagoModel.PROCERROR_OK==ocm.doPostProcesar()?"Yeah!":"Doh!");
	}
	
	public static void createRC() {
	  	/* === Recibo de cliente normal === */ 
	  	VOrdenCobroModel ocm = new VOrdenCobroModel();
	  	ocm.setPagoNormal(true, null);
	  	// === 1. Definir Entidad comercial y demás datos ===
	  	ocm.setBPartnerFacturas(1012142);
	  	// Setear fecha de factura ¿?
	  	ocm.setFechaTablaFacturas(Env.getDate(), true);
	  	// Nro y tipo documento obligatorio
	  	ocm.setDocumentNo((""+System.currentTimeMillis()).substring(4));
	  	ocm.setDocumentType(1010569);
	  	// Agregar factura a pagar 
	  	MInvoice inv1 = new MInvoice(Env.getCtx(), 1021702, null);
	  	ocm.addDebit(inv1, new BigDecimal(50));
	  	// === 2. Medios de Pago ===
	  	ocm.doPreProcesar();
	  	// Efectivo (con su PosPaymentMedium)
	  	VOrdenPagoModel.MedioPagoEfectivo mpe = ocm.new MedioPagoEfectivo(1010070, new BigDecimal(30), 118);
	  	MPOSPaymentMedium ppme = new MPOSPaymentMedium(Env.getCtx(), 1010041, null);
	  	mpe.setPaymentMedium(ppme);
	  	// Cheque (con su PosPaymentMedium)
	  	VOrdenPagoModel.MedioPagoCheque mpc = ocm.new MedioPagoCheque(1010085, "9999", new BigDecimal(20), Env.getDate(), Env.getDate(), "a la orden!");
	  	MPOSPaymentMedium ppmc = new MPOSPaymentMedium(Env.getCtx(), 1010043, null);	  	
	  	mpc.setPaymentMedium(ppmc);
	  	// Agregar medios de pago
	  	ocm.addMedioPago(mpe);
	  	ocm.addMedioPago(mpc);
	  	// === 3. Efectivizar el pago ===
	  	System.out.println(VOrdenPagoModel.PROCERROR_OK==ocm.doPostProcesar()?"Yeah!":"Doh!");		
	}
}
