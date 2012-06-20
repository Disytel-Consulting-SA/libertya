package org.openXpertya.pos.model;

/**
 * Esta clase permite obtener el nombre del cliente y el nro de tarjeta del
 * string devuelto por el lector de tarjetas para las tarjetas que contienen
 * máscaras iniciales standard mundialmente. La obtención de los campos
 * indicados están marcados estáticamente en esta clase, modificar en el caso
 * que se modifique lo que define el standard.<br>
 * El standard define que los campos nombre de cliente y nro de tarjeta se descubren de la siguiente manera:
 * <ul>
 * <li>Nombre del cliente: Posición 1 de la lista de partes. Longitud de la parte.</li>
 * <li>Nro de tarjeta: Posición 0 de la lista de partes. Longitud desde la posición 3 hasta longitud de la parte - 3.</li>
 * </ul>
 * 
 * 
 * @author Equipo de Desarrollo de Disytel
 * 
 */
public class StdCreditCard extends CreditCard { 
	
	public StdCreditCard(String creditCardStr) {
		super(creditCardStr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getFields() {
		setCustomerName(getCreditCardStrParts().get(1));
		setCreditCardNo(getCreditCardStrParts().get(0).substring(2,
				getCreditCardStrParts().get(0).length()));
	}

}
