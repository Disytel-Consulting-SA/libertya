package org.openXpertya.print.fiscal.comm;

import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.hasar.HasarFiscalPacket;

/**
 * Clase helper, utilizada por SpoolerTCPComm, que sirve para saber conservativamente cuando una 
 * secuencia deteminada de bytes enviadas por el Spooler de Hasar es una respuesta completa a un 
 * determinado comando Hasar que la genero.
 * <br>
 * NOTA: esta clase no tiene actualmente en cuenta el modelo de la impresora a la cual se esta conectando
 * por lo tanto, a futuro o se debe parametrizar para que tenga esto en cuenta (ya que las longitudes
 * de las respuestas puede variar dependiendo del modelo de impresor), o dejarlo tal como esta hecho
 * actualmente: solo se consideran completas aquellas respuestas que :
 * <ul>
 * <li> Tienen longitud fija en todos los modelos, teniendo ciertos patrones 
 * <li> El comando inicial que genero la respuesta es muy usado (no tiene mucho sentido
 * optimizar comandos usados muy de vez en cuando, arriesgando la correctitud)
 * </ul> 
 * <br>
 * Version actual soporta chequeos para los sig. comandos:
 * <ul>
 * <li> PrintLineItem
 * </ul>
 */
public class SpoolerManagerResponse {

	
	protected static final int OP_CMD_PRINT_LINE_ITEM	 = 0x42;
	protected static final byte FS_HASAR = 0x1C;
	private static SpoolerManagerResponse defInst= null;
	public static SpoolerManagerResponse getDefInst()
	{
		if (defInst == null)
		{
			defInst = new SpoolerManagerResponse();
		}
		return defInst;
	}
	
	
	public boolean isResponseCompleted(FiscalPacket request, byte[] rspBytes)
	{
		//casos comunes
		if (request == null)
			return false;
		if (rspBytes == null)
			return false;
		if (rspBytes.length == 0)
			return false;
		if (!(request instanceof HasarFiscalPacket))
			return false;
		HasarFiscalPacket reqHasar = (HasarFiscalPacket) request;
		
		//chequeos por tipo de comandos
		if (checkPrintLineItem(reqHasar, rspBytes))
			return true;
		//agregar otros aca o en subclases; por ej, LastItemDiscount, etc
		
		return false;
	}
	
	private boolean checkPrintLineItem(HasarFiscalPacket request,byte[] rspBytes)
	{
		//si el comanod no era PrintLineItem este check no se puede aplicar
		if (!isPrintLineItem(request))
		{
			return false;
		}
		
		//NOTA: las respuestas de Hasar que no contienen datos tienen longitud real
		//de 11 bytes: Op (1 byte) + FS (1 byte) + Status Impresora (4 bytes) + FS (1 byte) + Status Fiscal (4 bytes)
		//Pero el spooler de Hasar elimina OP y el siguiente FS, por lo que le llega a SpoolerTCPComm
		//los últimos nueve bytes (los dos Status y el FS que los separa).
		//Por lo tanto se considera que una respuesta completa a PrintLineItem tiene que 
		//tener 9 bytes, con FS en el 5to. NO SE INTERPRETAN los bits de respuesta de manera exhaustiva, 
		//ya que se asumen que
		//1) En caso de que haya un error marcado en los mismos, de todas maneras la logica
		//de los drivers va a fallar; aún cuando en el futuro no falle (por ej, se reintenta un comando)
		//la respuesta de error sigue (a falta de mejor documentacion de Hasar....) siendo de longitud
		//fija (despues de estos bits no se envia por ej, una string de descricion del error, al menos
		//bajo modelos actuales)
		//2) En caso de que en estos status no haya ningun error, los bits activados pueden ser distintos
		//dependiendo del modelo o de estados no necesariente relacionados a errores (por ej, 
		//"C080"+FS+"3600" o "0080"+FS+"3600" son posibles).
		//De todas maneras, no hay tatans variantes posibles, se podria hacer un mejor chequeo
		//a futuro. Por lo pronto por simplicidad solo se va a verifcar la longitud y el FS
		
		if ( rspBytes.length == 9 && rspBytes[4]== FS_HASAR)
		{
			return true;
		}
		
		return false;
	}

	private boolean isPrintLineItem(HasarFiscalPacket request)
	{
		return request.getCommandCode() == OP_CMD_PRINT_LINE_ITEM;
		
	}
	
	
}
