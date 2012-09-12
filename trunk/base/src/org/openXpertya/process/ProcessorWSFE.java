package org.openXpertya.process;

/**
 * @author Horacio Alvarez - SERVICIOS DIGITALES S.A.
 * @updated 2011-06-25
 * @notes Adaptado para Factura Electronica Argentina v1.0 - Vigencia a partir del 01-07-2011
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceTax;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MTax;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;


public class ProcessorWSFE {
	
	private String accepted;
	private String cae;
	private String mensaje;
	private String nroCbte;
	private Timestamp dateCae;
	private MInvoice invoice = null;
	protected CLogger log = CLogger.getCLogger(ProcessorWSFE.class);
	private String trxName = null;
	private String result = null;
	private String messageError = null;
	protected Properties m_ctx = Env.getCtx(); 
	
	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Constructor. Setea el objeto de entrada MInvoice.
	 */
	
	public ProcessorWSFE (MInvoice inv){
		invoice = inv;
		setTrxName(invoice.get_TrxName());
	}
	
	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Método principal.
	 */
	
	public String generateCAE(){
		deleteExistingFiles();
		if(messageError == null)
		   createInputFile();
		if(messageError == null)
		   callProcess();
		if(messageError == null)
		   searchForProblems();		
		if(messageError == null)
		   readOutput();		
		if(messageError == null)
		   setValues();		
		if(messageError == null)
		   check();		
		return messageError;
	}	
	
	private void check(){
		if(getMensaje() != null & !getMensaje().equals("None")){
			messageError = getMensaje();
		}
	}
	
	private void deleteExistingFiles(){
		try{
			File file = new File(getPath()+"entrada.txt");
			if(file.exists()){
				if(!file.delete()){
					messageError = Msg.translate(m_ctx, "caeCannotDeleteEntradaTxt");
					log.log(Level.SEVERE, messageError);					
				}
			}
			file = new File(getPath()+"salida.txt");
			if(file.exists()){
				if(!file.delete()){
					messageError = Msg.translate(m_ctx, "caeCannotDeleteSalidaTxt");
					log.log(Level.SEVERE, messageError);
				}
			}
			file = new File(getPath()+"error.txt");
			if(file.exists()){
				if(!file.delete()){
					messageError = Msg.translate(m_ctx, "caeCannotDeleteErrorTxt");
					log.log(Level.SEVERE, messageError);
				}
			}			
		}
		catch(Exception ex){
			messageError = Msg.translate(m_ctx, "caeCannotDeleteFilesError");
			log.log(Level.SEVERE, messageError,ex);
		}
	}
	
	/**
	 * @author: Horacio Alvarez
	 * @descripcion: Crea el archivo de entrada para el WSFE.PY.
	 * Setea los valores necesarios con el objeto MInvoice.
	 */
	private void createInputFile(){
		try{
			StringBuffer line  = new StringBuffer();
			//*****NRO. COMPROBANTE
			if(invoice.getNumeroComprobante() == 0){
				messageError = Msg.translate(m_ctx, "CaeNoNumeroComprobante");
				return;
			}
			line.append(invoice.getNumeroComprobante()+"\n");
			
			//*****PUNTO DE VENTA
			if(invoice.getPuntoDeVenta() == 0){
				messageError = Msg.translate(m_ctx, "CaeNoPuntoDeVenta");
				return;
			}
			line.append(invoice.getPuntoDeVenta()+"\n");
			
			//*****TIPO DE COMPROBANTE
			MDocType docType = new MDocType(Env.getCtx(),invoice.getC_DocTypeTarget_ID(),getTrxName());
			line.append(docType.getdocsubtypecae()+"\n");
			
			//*****TIPO DOC: 80 CUIT / 96 DNI
			MBPartner partner = new MBPartner(m_ctx,invoice.getC_BPartner_ID(),trxName);
			if(partner.isConsumidorFinal()){
				line.append("96"+"\n");
				line.append("1"+"\n");
			}
			else{
				line.append("80"+"\n");
				if(invoice.getCUIT() == null || invoice.getCUIT().equals("")){
					messageError = Msg.translate(m_ctx, "CaeNoCUIT");
					return;
				}
				
				line.append(invoice.getCUIT().replaceAll("-", "")+"\n");
			}
			
			//*****IMPORTE TOTAL
			//line.append(invoice.getGrandTotal().toString().replace(".", "")+"\n");
			line.append(invoice.getGrandTotal()+"\n");
			
			//*****IMPORTE NETO
			//line.append(invoice.getTotalLines().toString().replace(".", "")+"\n");
			line.append(invoice.getTotalLines()+"\n");
			
			//*****FECHA
			if(invoice.getDateAcct() == null){
				messageError = Msg.translate(m_ctx, "CaeNoDateAcct");
				return;
			}			
			line.append(formatTime(invoice.getDateAcct(), "yyyyMMdd")+"\n");
			
			//*****PRESTA SERVICIOS 0-->NO  1-->SI  
			line.append("0\n");
			
			//*****MONEDA
			MCurrency currency = new MCurrency(m_ctx, invoice.getC_Currency_ID(),getTrxName());
			line.append(currency.getWSFECode()+"\n");
			
			//*****CONVERSION
			// Se debe convertir a la moneda del comprobante desde la moneda de la compañía
			BigDecimal cotizacion = MCurrency.currencyConvert(Env.ONE,
					Env.getContextAsInt(m_ctx, "$C_Currency_ID"),
					invoice.getC_Currency_ID(), invoice.getDateInvoiced(), 0,
					m_ctx);
			line.append(cotizacion+"\n");
			
			//*****IMPUESTO
			MInvoiceTax[] taxes = this.invoice.getTaxes(false);
			MTax tax = null;
			int size = taxes.length;
			boolean firstLineAppended = false;
			for (int i = 0; i < size; i++){
				tax = MTax.get(m_ctx, taxes[i].getC_Tax_ID(), getTrxName());
				taxes[i].getTaxAmt();
				taxes[i].getTaxBaseAmt();
				tax.getWSFECode();
				
				if(firstLineAppended){
					line.append(";");
				}
				line.append(tax.getWSFECode()+":"+taxes[i].getTaxBaseAmt()+":"+taxes[i].getTaxAmt());
				firstLineAppended = true;
			}
			line.append("\n");
			
			File textFile = new File(getPath()+"entrada.txt");
			FileWriter textOut;
			textOut = new FileWriter(textFile);
			textOut.write(line.toString());
			textOut.close();
		}catch (Exception ex) {
			messageError = Msg.translate(m_ctx, "caeErrorCreateInputFile");
			log.log(Level.SEVERE, messageError,ex);
		}		
		
	}
	
	/**
	 * @author: Horacio Alvarez
	 * @descripcion: Guarda en @resultado@ la linea del archivo de salida
	 * generado por el WSFE, 
	 */
	
	private void readOutput(){
		String linea = null;
		
		try { 
				BufferedReader reader = new BufferedReader(new FileReader(getPath()+"salida.txt"));
				linea = reader.readLine();
				log.log(Level.SEVERE, linea);
	    } catch (IOException ex) { 
	    	  messageError = Msg.translate(m_ctx, "CaeErrorReadOutput");
	    	  log.log(Level.SEVERE, messageError+ex);
		}
	    result = linea;
	}
	
	
	/**
	 * @author Horacio Alvarez
	 * @return Devuelve la linea de salida generada por wsfe.py.
	 */
	
	private String getOutputLine(){
		return result;
	}

	/**
	 * @author: Horacio Alvarez
	 * @descripcion: Busca si el wsfe.py devolvió algun error, mediante el error.txt.
	 */
	
	private void searchForProblems(){
		String linea = null;
		try{
			File errorFile = new File(getPath()+"error.txt");
			if(errorFile.exists()){
				BufferedReader reader = new BufferedReader(new FileReader(errorFile));
				linea = reader.readLine();
				messageError = linea;
				log.log(Level.SEVERE, linea);
			}
			else{
				log.log(Level.SEVERE, "Everything is OK");
			}
		}
		catch(Exception ex){
			messageError = Msg.translate(m_ctx, "caeErrorSearchForProblems");
			log.log(Level.SEVERE, linea);			
		}
	}		
	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Obtiene y setea el CAE desde la linea guardada del archivo de respuesta.
	 */
	
	public void setCae(String value){
		cae = value;
		
	}
	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Obtiene y setea la fecha de vto. del CAE desde 
	 * la linea guardada del archivo de respuesta.
	 */
	
	public void setDateCae(String value){ 
		try{
			dateCae = getTimestamp(value, "yyyyMMdd");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}	
	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Obtiene y setea el ID del CAE desde 
	 * la linea guardada del archivo de respuesta.
	 */	
	
	private void setAcepted(String value){
		accepted = value;
	}
	
	private void setMensaje(String value){
		mensaje = value;
	}
	
	private void setNroCbte(String value){
		nroCbte = value;
	}		
	
	public void setValues(){
		try{
			String[] values = getOutputLine().split(":");
			setAcepted(values[0]);
			setCae(values[1]);
			setNroCbte(values[2]);
			setMensaje(values[3]);
			setDateCae(values[4]);			
		}
		catch(Exception ex){
			messageError = Msg.translate(m_ctx, "caeErrorSetValues");
			log.log(Level.SEVERE,messageError+ ":" +ex.toString());
		}
	}
	
	public String getAccepted(){
		return accepted;
	}
	
	public String getCAE(){
		return cae;		
	}
	
	public String getNroCbte(){
		return nroCbte;		
	}	
	
	public Timestamp getDateCae(){
		return dateCae;		
	}
	
	public String getMensaje(){
		return mensaje;
	}	

	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Invoca el ejecutable run.sh (wsfe.py)
	 * ******************************************************************************
	 * #!/bin/bash
	 * RUTAWSFE="/home/horacio/soft/Archivos"
	 * RUTAPYTHON="/usr/bin/python"
	 * cd $RUTAWSFE
	 * $RUTAPYTHON $RUTAWSFE/wsfe.py >> $RUTAWSFE/log.error 2>> $RUTAWSFE/log.error 
	 * ******************************************************************************
	 * 
	 */
	
	private void callProcess(){
		String aux = null;
		String auxE = null;
		try { 
		   /* directorio/ejecutable es el path del ejecutable y un nombre */
			Process p = null;
			String osName = System.getProperty("os.name");
			if(osName.equals("Linux")){
				p = Runtime.getRuntime().exec ("sh "+getPath() + "run.sh");
			}
			else{
				p = Runtime.getRuntime().exec (getPath() + "run.bat");
			}
			p.waitFor();
			if(p.exitValue() != 0){
				InputStream is = p.getInputStream();
				InputStream ie = p.getErrorStream();
				BufferedReader br = new BufferedReader (new InputStreamReader (is));
				BufferedReader brE = new BufferedReader (new InputStreamReader (ie));
	            aux = br.readLine();
	            if(aux == null) aux = "";
	            if(auxE == null) auxE = "";
	            auxE = brE.readLine();
	            //messageError = "Exit Value="+p.exitValue()+",InputStream="+aux+",ErrorStream="+auxE;
	            messageError = "La AFIP no aprueba el envio. Por favor corrobore los datos (CUIT, Categoria de IVA, Etc).";
	            log.log(Level.SEVERE,"caeErrorCallProcess:" +aux + auxE);
				
			}
			
		} 
		catch (Exception e) { 
			messageError = "Error callProcess(): "+aux;
			log.log(Level.SEVERE,messageError);
			e.printStackTrace();
		}
	}
	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Formatea el fecha recibida @time@
	 * al formato @format@. Ej: yyyyMMdd
	 */
	
	private String formatTime(Timestamp time,String format){
		SimpleDateFormat simpleformat = new SimpleDateFormat(format);
		Date date = new Date(time.getTime());
		return simpleformat.format(date);
	}
	
	/**
	 * @author Horacio Alvarez
	 * @descripcion: Convierte la fecha string @value@ de formato @format@
	 * en Timestamp.
	 */
	
	private Timestamp getTimestamp(String value, String format){
		Timestamp time = null;
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			java.util.Date date = dateFormat.parse(value);
			time = new Timestamp(date.getTime());
		}
		catch(Exception ex){
			log.log(Level.SEVERE, "Error getTimestamp():"+ex);
		}
		return time;
	}
	
	/**
	 * @author Horacio Alvarez
	 * @return: Devuelve el path donde se encuentra alojado el proceso wsfe.py en el
	 * sistema de archvos local. Este path se guarda en la tabla AD_Preference (Valores Predeterminados)
	 */
	private String getPath(){
		MDocType docType = new MDocType(Env.getCtx(),invoice.getC_DocTypeTarget_ID(),getTrxName());
		/**
		 * El nombre de AD_Preference a consultar se forma concatenando el valor WSFE_PV y el Nro de Punto de Venta
		 */
		MPreference preference = MPreference.getUserPreference(Env.getCtx(), "WSFE_PV"+docType.getPosNumber(), getTrxName());
		if(preference == null){
			// En el caso que no se haya encontrado ninguna preferencia a partir del Nro de Punto de Venta se busca utilizando el valor por defecto que es: WSFE
			preference = MPreference.getUserPreference(Env.getCtx(), "WSFE", getTrxName());
		}
		return preference.getValue();
	}
	
	private String getTrxName(){
		return trxName;
	}
	
	private void setTrxName(String value){
		trxName = value;
	}	

}
