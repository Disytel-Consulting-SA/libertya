package org.openXpertya.print.fiscal.hasar.pojos;

public class Totales { 
	public int Orden;
	public double Base;
	public double MontoIVA;
	public double MontoII;
	public double MontoOtrosTributos;
	public double MontoNoGravado;
	public double MontoExento;
	public double MontoGravado;
	public double AjusteRedondeo;
	public double Final;
	public double ImpresionBase;
	public double ImpresionMontoIVA;
	public double ImpresionMontoII;
	public double ImpresionFinal;
	public String TipoTotales;
	public double Total;
	public double TotalBase;
	public double TotalNoGravado;
	public double TotalGravado;
	public double TotalExento;
	public double TotalIVA;
	public double TotalII;
	public double TotalOtrosTributos;
	public double TotalPagos;
	public int OperacionesVenta;
	public DiscriminacionIVA DiscriminacionIVA;
	public DiscriminacionOtrosTributos DiscriminacionOtrosTributos;
	
	public Totales() {
		// Inicializa los totales a CERO
		setBase(0.00);
		setTotal(0.00);
		setMontoGravado(0.00);
		setMontoNoGravado(0.00);
		setMontoExento(0.00);
		setFinal(0.00);
		setTotalBase(0.00);
		setMontoIVA(0.00);
		setMontoII(0.00);
		setMontoOtrosTributos(0.00);
		
	}
	
	public int getOrden() {
		return Orden;
	}
	public void setOrden(int orden) {
		Orden = orden;
	}
	public double getBase() {
		return Base;
	}
	public void setBase(double base) {
		Base = base;
	}
	public double getMontoIVA() {
		return MontoIVA;
	}
	public void setMontoIVA(double montoIVA) {
		MontoIVA = montoIVA;
	}
	public double getMontoII() {
		return MontoII;
	}
	public void setMontoII(double montoII) {
		MontoII = montoII;
	}
	public double getMontoOtrosTributos() {
		return MontoOtrosTributos;
	}
	public void setMontoOtrosTributos(double montoOtrosTributos) {
		MontoOtrosTributos = montoOtrosTributos;
	}
	public double getMontoNoGravado() {
		return MontoNoGravado;
	}
	public void setMontoNoGravado(double montoNoGravado) {
		MontoNoGravado = montoNoGravado;
	}
	public double getMontoExento() {
		return MontoExento;
	}
	public void setMontoExento(double montoExento) {
		MontoExento = montoExento;
	}
	public double getMontoGravado() {
		return MontoGravado;
	}
	public void setMontoGravado(double montoGravado) {
		MontoGravado = montoGravado;
	}
	public double getAjusteRedondeo() {
		return AjusteRedondeo;
	}
	public void setAjusteRedondeo(double ajusteRedondeo) {
		AjusteRedondeo = ajusteRedondeo;
	}
	public double getFinal() {
		return Final;
	}
	public void setFinal(double final1) {
		Final = final1;
	}
	public double getImpresionBase() {
		return ImpresionBase;
	}
	public void setImpresionBase(double impresionBase) {
		ImpresionBase = impresionBase;
	}
	public double getImpresionMontoIVA() {
		return ImpresionMontoIVA;
	}
	public void setImpresionMontoIVA(double impresionMontoIVA) {
		ImpresionMontoIVA = impresionMontoIVA;
	}
	public double getImpresionMontoII() {
		return ImpresionMontoII;
	}
	public void setImpresionMontoII(double impresionMontoII) {
		ImpresionMontoII = impresionMontoII;
	}
	public double getImpresionFinal() {
		return ImpresionFinal;
	}
	public void setImpresionFinal(double impresionFinal) {
		ImpresionFinal = impresionFinal;
	}
	public String getTipoTotales() {
		return TipoTotales;
	}
	public void setTipoTotales(String tipoTotales) {
		TipoTotales = tipoTotales;
	}
	public double getTotal() {
		return Total;
	}
	public void setTotal(double total) {
		Total = total;
	}
	public double getTotalBase() {
		return TotalBase;
	}
	public void setTotalBase(double totalBase) {
		TotalBase = totalBase;
	}
	public double getTotalNoGravado() {
		return TotalNoGravado;
	}
	public void setTotalNoGravado(double totalNoGravado) {
		TotalNoGravado = totalNoGravado;
	}
	public double getTotalGravado() {
		return TotalGravado;
	}
	public void setTotalGravado(double totalGravado) {
		TotalGravado = totalGravado;
	}
	public double getTotalExento() {
		return TotalExento;
	}
	public void setTotalExento(double totalExento) {
		TotalExento = totalExento;
	}
	public double getTotalIVA() {
		return TotalIVA;
	}
	public void setTotalIVA(double totalIVA) {
		TotalIVA = totalIVA;
	}
	public double getTotalII() {
		return TotalII;
	}
	public void setTotalII(double totalII) {
		TotalII = totalII;
	}
	public double getTotalOtrosTributos() {
		return TotalOtrosTributos;
	}
	public void setTotalOtrosTributos(double totalOtrosTributos) {
		TotalOtrosTributos = totalOtrosTributos;
	}
	public double getTotalPagos() {
		return TotalPagos;
	}
	public void setTotalPagos(double totalPagos) {
		TotalPagos = totalPagos;
	}
	public int getOperacionesVenta() {
		return OperacionesVenta;
	}
	public void setOperacionesVenta(int operacionesVenta) {
		OperacionesVenta = operacionesVenta;
	}
	public DiscriminacionIVA getDiscriminacionIVA() {
		return DiscriminacionIVA;
	}
	public void setDiscriminacionIVA(DiscriminacionIVA discriminacionIVA) {
		DiscriminacionIVA = discriminacionIVA;
	}
	public DiscriminacionOtrosTributos getDiscriminacionOtrosTributos() {
		return DiscriminacionOtrosTributos;
	}
	public void setDiscriminacionOtrosTributos(DiscriminacionOtrosTributos discriminacionOtrosTributos) {
		DiscriminacionOtrosTributos = discriminacionOtrosTributos;
	}
}
