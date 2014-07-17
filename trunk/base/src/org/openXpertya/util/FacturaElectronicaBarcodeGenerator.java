package org.openXpertya.util;

import java.awt.Color;
import java.awt.Image;
import java.text.SimpleDateFormat;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.print.pdf.text.pdf.BarcodeInter25;

/**
*
* Generador de Código de barras de Factura Electrónica
* El código de barras generado es Interleaved 2 of 5
* La Resolución General 1702 de AFIP resuelve
* <p>Artículo 1º — Los contribuyentes y responsables inscritos en el impuesto al valor agregado quedan obligados a consignar en los comprobantes clases "A", "A" con leyenda "Pago en CBU informada", "B", "E" o "M", mediante el sistema de identificación de datos denominado "Código de Barras", los siguientes datos y en el orden que se indica:
* <ul>
* <li>a) Clave Unica de Identificación Tributaria (C.U.I.T.) del emisor de la factura.</li>
* <li>b) Código de tipo de comprobante.</li>
* <li>c) Punto de venta.</li>
* <li>d) Código de Autorización de Impresión (C.A.I.).</li>
* <li>e) Fecha de vencimiento.</li>
* <li>f) Dígito verificador.</li>
* </ul>
* </p>
* En Libertya estos datos corresponden a:
* <ul>
* <li>a) CUIT de la Organización o Compañía.</li>
* <li>b) Valor de la columna DocSubTypeCAE de la Tabla C_DocType para el tipo de documento actual.</li>
* <li>c) Punto de venta de la factura.</li>
* <li>d) En realidad debe ser el CAE, no el CAI.</li>
* <li>e) Fecha de vencimiento del CAE.</li>
* <li>f) El Dígito verificador se calcula con un algoritmo que se describe en el Regimen y se presenta a continuación.</li>
* </ul>
* RUTINA PARA EL CALCULO DEL DIGITO VERIFICADOR<br> 
* Se considera para efectuar el cálculo el siguiente ejemplo: 01234567890 <br>
* 
* Etapa 1: Comenzar desde la izquierda, sumar todos los caracteres ubicados en las posiciones impares.<br>
* 0 + 2 + 4 + 6 + 8 + 0 = 20<br>
* Etapa 2: Multiplicar la suma obtenida en la etapa 1 por el número 3.<br>
* 20 x 3 = 60<br>
* Etapa 3: Comenzar desde la izquierda, sumar todos los caracteres que están ubicados en las posiciones pares.<br>
* 1 + 3 + 5 + 7 + 9 = 25<br>
* Etapa 4: Sumar los resultados obtenidos en las etapas 2 y 3.<br>
* 60 + 25 = 85<br>
* Etapa 5: Buscar el menor número que sumado al resultado obtenido en la etapa 4 dé un número múltiplo de 10. Este será el valor del dígito verificador del módulo 10.<br>
* 85 + 5 = 90<br>
* De esta manera se llega a que el número <strong>5</strong> es el dígito verificador módulo 10 para el código 01234567890.<br>
* 
*/
public class FacturaElectronicaBarcodeGenerator {

	/**  */
	private String clientCuit;
	
	private String docTypeCodeFE;
	
	private String puntoDeVenta;
	
	private String cae;
	
	private String caeDueDate;
	
	private String code;
	
	private BarcodeInter25 barcodeInter25;
	
	private CLogger log;
	
	public FacturaElectronicaBarcodeGenerator() {
		setLog(CLogger.getCLogger(getClass().getName()));
		setBarcodeInter25(new BarcodeInter25());
		getBarcodeInter25().setBarHeight(50);
	}

	public FacturaElectronicaBarcodeGenerator(MInvoice invoice, MDocType docType, String clientCUIT) {
		this();
		setClientCuit(clientCUIT);
		initializeCodeParts(invoice, docType);
	}
	
	public void initializeCodeParts(MInvoice invoice, MDocType docType){
		if(Util.isEmpty(getClientCuit(), true)){
			getLog().severe("No existe CUIT de la compañia");
			return;
		}
		// b) Código de tipo de comprobante.
		if(Util.isEmpty(docType.getdocsubtypecae(), true)){
			getLog().severe("No existe codigo de tipo de documento electronico");
			return;
		}
		setDocTypeCodeFE(docType.getdocsubtypecae());
		// c) Punto de venta.
		if(Util.isEmpty(invoice.getPuntoDeVenta(), true)){
			getLog().severe("No existe punto de venta");
			return;
		}
		Integer puntoDeVenta = invoice.getPuntoDeVenta();
		String ptoVta = String.valueOf(puntoDeVenta);
		// El punto de venta debe ir con 0 cuando es menor a 1000
		if(puntoDeVenta < 1000){
			ptoVta = "0";
			// Si es menor a 100, hay que agregar otro 0
			if(puntoDeVenta < 100){
				ptoVta += "0";
			}
			// Si es menor a 10, hay que agregar otro 0
			if(puntoDeVenta < 10){
				ptoVta += "0";
			}
			ptoVta = ptoVta+String.valueOf(puntoDeVenta);
		}
		setPuntoDeVenta(ptoVta);
		// d) Código de Autorización de Impresión (C.A.I.).
		// En realidad es el CAE lo que se debe imprimir
		if(Util.isEmpty(invoice.getcae(), true)){
			getLog().severe("No existe CAE");
			return;
		}
		setCae(invoice.getcae());
		// e) Fecha de vencimiento.
		// Fecha de Vencimiento del CAE - Formato AAAAMMDD
		if(invoice.getvtocae() == null){
			getLog().severe("No existe fecha de vencimieto de cae");
			return;
		}
		setCaeDueDate(new SimpleDateFormat("yyyyMMdd").format(invoice.getvtocae()));
	}
	
	public String generateCode(){
		if (Util.isEmpty(getClientCuit(), true)
				|| Util.isEmpty(getDocTypeCodeFE(), true)
				|| Util.isEmpty(getPuntoDeVenta(), true)
				|| Util.isEmpty(getCae(), true) || getCaeDueDate() == null) {
			log.severe("Alguno de los datos requeridos no existe");
			return "";
		}
		// Generar el código del código de barras para calcular el dígito verificador
		String code = getClientCuit() + getDocTypeCodeFE() + getPuntoDeVenta()
				+ getCae() + getCaeDueDate();
		// Dejar sólo dígitos en el código
		code = BarcodeInter25.keepNumbers(code);
		// f) Dígito verificador.
		String digitoVerificador = getDigitoVerificador(code);
		code += digitoVerificador;
		setCode(code);
		getBarcodeInter25().setCode(code);
		return code;
	}
	
	public String getDigitoVerificador(String code){
		Integer etapa1 = getEtapa1DigitoVerificador(code);
		Integer etapa2 = getEtapa2DigitoVerificador(code, etapa1);
		Integer etapa3 = getEtapa3DigitoVerificador(code, etapa1, etapa2);
		Integer etapa4 = getEtapa4DigitoVerificador(code, etapa1, etapa2, etapa3);
		Integer etapa5 = getEtapa5DigitoVerificador(code, etapa1, etapa2, etapa3, etapa4);
		return String.valueOf(etapa5);
	}
	
	private Integer getCodeSumPositions(String code, int deltaInitial){
		Integer sum = 0;
		for (int i = deltaInitial; i < code.length(); i += 2) {
			sum += Character.getNumericValue(code.charAt(i));
		}
		return sum;
	}
	
	/**
	 * Etapa 1: Comenzar desde la izquierda, sumar todos los caracteres ubicados
	 * en las posiciones impares.
	 * 
	 * @param code
	 *            código sin el dígito verificador
	 * @return nro de la etapa 1
	 */
	public Integer getEtapa1DigitoVerificador(String code){
		return getCodeSumPositions(code, 0);
	}
	
	/**
	 * Etapa 2: Multiplicar la suma obtenida en la etapa 1 por el número 3.
	 * 
	 * @param code
	 *            código sin el dígito verificador
	 * @param etapa1
	 *            número obtenido de la etapa 1
	 * @return nro de la etapa 2
	 */
	public Integer getEtapa2DigitoVerificador(String code, Integer etapa1){
		return etapa1 * 3;
	}
	
	/**
	 * Etapa 3: Comenzar desde la izquierda, sumar todos los caracteres que
	 * están ubicados en las posiciones pares.
	 * 
	 * @param code
	 *            código sin el dígito verificador
	 * @param etapa1
	 *            número obtenido de la etapa 1
	 * @param etapa2
	 *            número obtenido de la etapa 2
	 * @return nro de la etapa 3
	 */
	public Integer getEtapa3DigitoVerificador(String code, Integer etapa1, Integer etapa2){
		return getCodeSumPositions(code, 1);
	}
	
	/**
	 * Etapa 4: Sumar los resultados obtenidos en las etapas 2 y 3.
	 * 
	 * @param code
	 *            código sin el dígito verificador
	 * @param etapa1
	 *            número obtenido de la etapa 1
	 * @param etapa2
	 *            número obtenido de la etapa 2
	 * @param etapa3
	 *            número obtenido de la etapa 3
	 * @return nro de la etapa 4
	 */
	public Integer getEtapa4DigitoVerificador(String code, Integer etapa1, Integer etapa2, Integer etapa3){
		return etapa2 + etapa3;
	}
	
	/**
	 * Etapa 5: Buscar el menor número que sumado al resultado obtenido en la
	 * etapa 4 dé un número múltiplo de 10. Este será el valor del dígito
	 * verificador del módulo 10.
	 * 
	 * @param code
	 *            código sin el dígito verificador
	 * @param etapa1
	 *            número obtenido de la etapa 1
	 * @param etapa2
	 *            número obtenido de la etapa 2
	 * @param etapa3
	 *            número obtenido de la etapa 3
	 * @param etapa4
	 *            número obtenido de la etapa 4
	 * @return nro de la etapa 5
	 */
	public Integer getEtapa5DigitoVerificador(String code, Integer etapa1, Integer etapa2, Integer etapa3, Integer etapa4){
		boolean found = false;
		Integer digito = 0;
		for (int i = 0; i < 10 && !found; i++) {
			if((etapa4 + i) % 10 == 0){
				digito = i;
				found = true;
			}
		}
		return digito;
	}
	
	public Image getBarcodeImage(boolean generateCode){
		if (generateCode)
			generateCode();
		if(Util.isEmpty(getCode(), true)){
			getLog().severe("El codigo no existe porque falta algun dato");
			return null;
		}
		return getBarcodeInter25().createAwtImage(Color.BLACK, Color.WHITE);
	}
	
	public Image getBarcodeImage(){
		return getBarcodeImage(false);
	}
	
	public String getClientCuit() {
		return clientCuit;
	}

	public void setClientCuit(String clientCuit) {
		this.clientCuit = clientCuit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDocTypeCodeFE() {
		return docTypeCodeFE;
	}

	public void setDocTypeCodeFE(String docTypeCodeFE) {
		this.docTypeCodeFE = docTypeCodeFE;
	}

	public String getPuntoDeVenta() {
		return puntoDeVenta;
	}

	public void setPuntoDeVenta(String puntoDeVenta) {
		this.puntoDeVenta = puntoDeVenta;
	}

	public String getCae() {
		return cae;
	}

	public void setCae(String cae) {
		this.cae = cae;
	}

	public String getCaeDueDate() {
		return caeDueDate;
	}

	public void setCaeDueDate(String caeDueDate) {
		this.caeDueDate = caeDueDate;
	}

	public CLogger getLog() {
		return log;
	}

	public void setLog(CLogger log) {
		this.log = log;
	}

	public BarcodeInter25 getBarcodeInter25() {
		return barcodeInter25;
	}

	public void setBarcodeInter25(BarcodeInter25 barcodeInter25) {
		this.barcodeInter25 = barcodeInter25;
	}

}
