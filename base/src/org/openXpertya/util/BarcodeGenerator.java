package org.openXpertya.util;

import java.awt.Color;
import java.awt.Image;

import org.openXpertya.print.pdf.text.pdf.Barcode;

public abstract class BarcodeGenerator {

	private String code;
	
	private Barcode barcode;
	
	private CLogger log;
	
	public BarcodeGenerator() {
		setLog(CLogger.getCLogger(getClass().getName()));
		setBarcode(createBarcode());
		getBarcode().setBarHeight(50);
	}

	public Image getBarcodeImage(boolean generateCode){
		if (generateCode)
			generateCode();
		if(Util.isEmpty(getCode(), true)){
			getLog().severe("El codigo no existe porque falta algun dato");
			return null;
		}
		return getBarcode().createAwtImage(Color.BLACK, Color.WHITE);
	}
	
	public Image getBarcodeImage(){
		return getBarcodeImage(false);
	}
	
	public abstract Barcode createBarcode();
	
	public abstract String generateCode();

	public CLogger getLog() {
		return log;
	}

	public void setLog(CLogger log) {
		this.log = log;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Barcode getBarcode() {
		return barcode;
	}

	public void setBarcode(Barcode barcode) {
		this.barcode = barcode;
	}
}
