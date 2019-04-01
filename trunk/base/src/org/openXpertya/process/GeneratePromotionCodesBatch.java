package org.openXpertya.process;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import org.openXpertya.model.MProcess;
import org.openXpertya.model.MPromotion;
import org.openXpertya.model.MPromotionCode;
import org.openXpertya.model.X_C_Promotion_Code;
import org.openXpertya.model.X_C_Promotion_Code_Batch;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.PromotionBarcodeGenerator;

public class GeneratePromotionCodesBatch extends AbstractSvrProcess {

	private static String PRINT_PROCESS_VALUE = "PromotionalCodesBatchPrintProcess";
	
	// Cantidad de cupones a generar
	protected Integer numberOfCodes = null;
	// C_Invoice_ID de la factura con la que se generará este cupon
	protected int invoiceOrigID = -1;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			// Factura original
			if (name.equals("C_Invoice_Orig_ID")) {
				invoiceOrigID = para[i].getParameterAsInt();
			}
			// Numero de cupones
			if (name.equals("QTY")) {
				numberOfCodes = para[i].getParameterAsInt();
			} 

		}
	}
	
	@Override
	protected String doIt() throws Exception {
		Timestamp today = Env.getDate();
		// Si no tenemos cantidad de códigos parámetro, por defecto es 1
		if(numberOfCodes == null || numberOfCodes <= 0){
			numberOfCodes = 1;
		}

		// Se ingreso un C_Invoice_Orig_ID como argumento? (por cliente LY es obligatorio, pero este control no existe en LYWS)
		if (invoiceOrigID <= 0) {
			throw new Exception("Debe especificar la factura origen con la que se va a generar el cupon promocional");
		}
		
		// La factura ya fue utilizada para generar un cupon? En ese caso no es posible reutilizar
		int count = DB.getSQLValue(get_TrxName(), "SELECT count(1) FROM C_Promotion_Code WHERE C_Invoice_Orig_ID = " + invoiceOrigID);
		if (count>0) {
			throw new Exception("La factura ingresada ya fue utilizada para generar un cupon promocional");
		}
		
		// Deben existir promociones válidas para la fecha actual de tipo
		// código promocional y que la cantidad máxima de códigos generados sea
		// mayor a los códigos ya generados en lotes no anulados
		List<MPromotion> promotionsCodes = MPromotion.getValidForCodesGeneration(today, getCtx(), get_TrxName());
		if(promotionsCodes.size() == 0){
			throw new Exception(Msg.getMsg(getCtx(), "NotExistPromotionValidForCodeGeneration"));
		}
		
		// Random de promociones existentes a tomar
		Integer maxCodes = numberOfCodes > promotionsCodes.size() ? promotionsCodes.size() : numberOfCodes;
		
		// TODO Qué pasa si se deben generar más códigos que promociones haya,
		// se repiten las promos o se corta ahi? 
		// Esto se debe decidir si se genera mas de 1 cupón, por lo pronto se genera solo 1
		// Hay que tenerlo en cuenta en el random de mas abajo y en la variable
		// maxCodes ya que por lo pronto se toma el mínimo entre lo ingresado y
		// la cantidad de promociones válidas. 
		// Es decir, si la cantidad de promociones válidas es mayor a la
		// cantidad a generar entonces me quedo con la cantidad de promociones
		// válidas, sino con la cantidad ingresada   
		
		X_C_Promotion_Code_Batch pcBatch = new X_C_Promotion_Code_Batch(getCtx(), 0, get_TrxName());
		pcBatch.setDateTrx(today);
		pcBatch.setProcessed(true);
		if(!pcBatch.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		
		Random r = new Random();
		int randomPromoIndex;
		MPromotion randomPromo;
		X_C_Promotion_Code pc;
		PromotionBarcodeGenerator codeGenerator;
		// Generar cada código
		for (int i = 0; i < maxCodes; i++) {
			// Obtener una promo random de la lista de promos vigentes
			randomPromoIndex = r.nextInt(promotionsCodes.size());
			randomPromo = promotionsCodes.get(randomPromoIndex);
			codeGenerator = new PromotionBarcodeGenerator(pcBatch, randomPromo);
			// Generar el código promocional
			pc = new MPromotionCode(getCtx(), 0, get_TrxName());
			pc.setC_Promotion_Code_Batch_ID(pcBatch.getID());
			pc.setC_Promotion_ID(randomPromo.getID());
			pc.setCode(codeGenerator.generateCode());
			pc.setValidFrom(randomPromo.getValidFrom());
			pc.setValidTo(randomPromo.getValidTo());
			pc.setC_Invoice_Orig_ID(invoiceOrigID);
			pc.setProcessed(true);
			if(!pc.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			promotionsCodes.remove(randomPromoIndex);
		}

		// == Tirar la impresión ==
		ProcessInfo pi = new ProcessInfo("Impresion Cupones Promocionales", getPrintProcessID(),
				X_C_Promotion_Code_Batch.Table_ID, pcBatch.getID());
		// Propagar la configuracion de envio a stream unicamente o no de este processInfo al pi de impresion
		pi.setToStreamOnly(getProcessInfo().isToStreamOnly());
		// Ejecucion de la impresion
		MProcess.execute(getCtx(), getPrintProcess(), pi, get_TrxName());
		if(pi.isError()){
			throw new Exception(pi.getSummary());
		}
		// Recuperar el stream resultante de pi (si se configuro mediante isToStreamOnly) y asignarlo a este processInfo.
		getProcessInfo().setReportResultStream(pi.getReportResultStream());

		// Almaceno en el log, el ID del lote generado, bajo el P_ID 
		addLog(pcBatch.getC_Promotion_Code_Batch_ID(), null, null, null);
		
		return Msg.getMsg(getCtx(), "GeneratedBatch")+" : "+pcBatch.getDocumentNo();
	}
	
	/**
	 * @return proceso de impresión de cupones
	 */
	private MProcess getPrintProcess(){
		return MProcess.get(getCtx(), getPrintProcessID(), get_TrxName());
	}

	/**
	 * @return ID del proceso de impresión de cupones
	 */
	private Integer getPrintProcessID(){
		return MProcess.getProcess_ID(PRINT_PROCESS_VALUE, get_TrxName());
	}
}
