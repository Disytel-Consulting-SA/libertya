package org.openXpertya.pos.model;

/**
 * Esta clase permite obtener el nombre del cliente y el nro de tarjeta del
 * string devuelto por el lector de tarjetas para las tarjetas TDF. La obtención de los campos
 * indicados están marcados estáticamente en esta clase, modificar en el caso
 * que se modifique lo que define el standard TDF.<br>
 * TDF define que los campos nombre de cliente y nro de tarjeta se descubren de la siguiente manera:
 * <ul>
 * <li>Nombre del cliente: Posición 1 de la lista de partes. Longitud de la parte.</li>
 * <li>Nro de tarjeta: Posición 2 de la lista de partes. Longitud de la parte.</li>
 * </ul>
 * 
 * 
 * @author Equipo de Desarrollo de Disytel
 * 
 */
public class TDFCreditcard extends CreditCard {

	public TDFCreditcard(String creditCardStr) {
		super(creditCardStr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getFields() {
		setCustomerName(getCreditCardStrParts().get(1));
		setCreditCardNo(getCreditCardStrParts().get(2));
	}

}
