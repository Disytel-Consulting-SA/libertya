package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.print.fiscal.FiscalPrinter;
import org.openXpertya.print.fiscal.comm.FiscalComm;
import org.openXpertya.print.fiscal.comm.SpoolerTCPComm;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class MControladorFiscal extends X_C_Controlador_Fiscal {

    /** Logger de la aplicación */
	protected static CLogger log = CLogger.getCLogger(MControladorFiscal.class);
    
	public MControladorFiscal(Properties ctx, int C_Controlador_Fiscal_ID, String trxName) {
		super(ctx, C_Controlador_Fiscal_ID, trxName);
	}

	public MControladorFiscal(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Obtiene el MControladorFiscal que tiene asignado el tipo de documento.
	 * @param docType_ID ID del tipo de documento.
	 * @return El <code>MControladorFiscal</code> que corresponde al tipo
	 * de documento o <code>null</code> en caso de que el DocType no sea Fiscal
	 * (<code>isfiscal() == false</code>).
	 */
	public static MControladorFiscal getOfDocType(int docType_ID) {
		MControladorFiscal cFiscal = null;
		MDocType docType = new MDocType(Env.getCtx(), docType_ID, null);
		if(docType.isFiscal())
			cFiscal = new MControladorFiscal(Env.getCtx(), docType.getC_Controlador_Fiscal_ID(), null);
		return cFiscal;
	}
	
	/**
	 * Retorna la <code>FiscalPrinter</code> correspondiente al tipo de documento
	 * especificado. Se obtienen los datos del <code>MControladorFiscal</code>
	 * que tiene asignado el DocType, e intenta instanciar la impresora fiscal
	 * según la clase configurada en el tipo del controlador fiscal.
  	 * @param docType_ID ID del tipo de documento
	 * @return La instancia de la impresora. 
	 * @throws Exception cuando ocurre algun error en el intento de instanciar
	 * la clase configurada o si el DocType no es fiscal.
	 */	
	public static FiscalPrinter getFiscalPrinter(int docType_ID) throws Exception {
		Properties ctx = Env.getCtx();
		
		MControladorFiscal cFiscal = getOfDocType(docType_ID);
		
		if(cFiscal == null) 
			throw new Exception(Msg.translate(ctx, "DocTypeNotFiscalError"));
		
		return cFiscal.getFiscalPrinter();
	}
	
	/**
	 * Retorna la <code>FiscalPrinter</code> correspondiente al tipo de documento
	 * especificado. Se obtienen los datos del <code>MControladorFiscal</code>
	 * que tiene asignado el DocType, e intenta instanciar la impresora fiscal
	 * según la clase configurada en el tipo del controlador fiscal.
	 * @return La instancia de la impresora.
	 * @throws Exception cuando ocurre algun error en el intento de instanciar
	 * la clase configurada o si el DocType no es fiscal.
	 */	
	public FiscalPrinter getFiscalPrinter() throws Exception {
		Properties ctx = Env.getCtx();
		FiscalPrinter fiscalPrinter = null;
		X_C_Controlador_Fiscal_Type cType = new X_C_Controlador_Fiscal_Type(Env.getCtx(), getC_Controlador_Fiscal_Type_ID(), null);
		String className = cType.getclazz();
		
		try {
			try {
				fiscalPrinter = (FiscalPrinter)Class.forName(className).newInstance();
			} catch (ClassNotFoundException e) {
				throw new Exception(Msg.translate(ctx,"FiscalPrinterClassNotFound"), e);
			} catch (Exception e) {
				throw new Exception(Msg.translate(ctx,"FiscalPrinterInstanciationError"), e);
			}
			String host = gethost();
			int port = getport();
			FiscalComm fiscalComm = new SpoolerTCPComm(host, port);
			fiscalPrinter.setFiscalComm(fiscalComm);
			
		} catch(Exception e) {
			log.severe(e.getMessage());
			throw e;
		}
		return fiscalPrinter;
		
	}
	
	
}
