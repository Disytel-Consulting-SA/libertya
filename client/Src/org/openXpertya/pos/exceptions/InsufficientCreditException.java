package org.openXpertya.pos.exceptions;

public class InsufficientCreditException extends PosException {

	public InsufficientCreditException() {
		
	}
	
	public InsufficientCreditException(String msg) {
		super(msg);
	}
}
