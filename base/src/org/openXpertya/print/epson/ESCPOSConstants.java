package org.openXpertya.print.epson;

public interface ESCPOSConstants {

	///////////////////////////////////////////////////////////////////////
	// ESTADO DE IMPRESORA 
	///////////////////////////////////////////////////////////////////////
	
	/** Error del cajón del dinero (Bit 2 del estado de impresora) */
	public int PST_PRINTER_DRAWER = 0x0004;
	/** Impresora fuera de línea. No ha podido comunicarse con la impresora dentro del per�odo de tiempo establecido. (Bit 3 del estado de impresora) */
	public int PST_PRINTER_OFFLINE = 0x0008;
	
	///////////////////////////////////////////////////////////////////////
	// ESTADO DE OFFLINE
	///////////////////////////////////////////////////////////////////////

	/** Tapa abierta (Bit 2 del estado offline de impresora) */
	public int PST_OFFLINE_COVER = 0x0004;
	/** El papel está siendo corrido por el botón FEED (Bit 3 del estado offline de impresora) */
	public int PST_OFFLINE_FEED = 0x0008;
	/** La impresión fue interrumpida. (Bit 5 del estado offline de impresora) */
	public int PST_OFFLINE_INTERRUPTED = 0x0032;
	/** Ocurrió un error. (Bit 6 del estado offline de impresora) */
	public int PST_OFFLINE_ERROR = 0x0064;
	
	///////////////////////////////////////////////////////////////////////
	// ESTADO DE ERROR
	///////////////////////////////////////////////////////////////////////

	/** Error del cutter. (Bit 3 del estado de error de impresora) */
	public int PST_ERROR_CUTTER = 0x0008;
	/** Error irrecuperable. (Bit 5 del estado de error de impresora) */
	public int PST_ERROR_UNRECOVERABLE = 0x0032;
	/** Error autorecuperable. (Bit 6 del estado de error de impresora) */
	public int PST_ERROR_AUTORECOVERABLE = 0x0064;
	
	///////////////////////////////////////////////////////////////////////
	// ESTADO DE PAPEL
	///////////////////////////////////////////////////////////////////////

	/** Papel por agotarse. (Bit 2 y 3 del estado de papel de impresora) */
	public int PST_PAPER_NEAR_END = 0x0012;
	/** Impresora sin papel. (Bit 5 y 6 del estado offline de impresora) */
	public int PST_PAPER_END = 0x0096;
}
