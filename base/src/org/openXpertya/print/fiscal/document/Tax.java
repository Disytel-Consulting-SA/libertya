package org.openXpertya.print.fiscal.document;

import java.math.BigDecimal;

public class Tax {

	private Integer id;
	private String name;
	private BigDecimal rate;
	private BigDecimal baseAmt;
	private BigDecimal amt;
	private boolean isPercepcion;
	private String percepcionType;
	
	public Tax(){		
	}
	
	public Tax(Integer id, String name, BigDecimal rate, BigDecimal baseAmt, BigDecimal amt, boolean isPercepcion, String percepcionType){
		setId(id);
		setName(name);
		setRate(rate);
		setBaseAmt(baseAmt);
		setAmt(amt);
		setPercepcion(isPercepcion);
		setPercepcionType(percepcionType);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setBaseAmt(BigDecimal baseAmt) {
		this.baseAmt = baseAmt;
	}

	public BigDecimal getBaseAmt() {
		return baseAmt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setPercepcion(boolean isPercepcion) {
		this.isPercepcion = isPercepcion;
	}

	public boolean isPercepcion() {
		return isPercepcion;
	}

	public String getPercepcionType() {
		return percepcionType;
	}

	public void setPercepcionType(String percepcionType) {
		this.percepcionType = percepcionType;
	}
}
