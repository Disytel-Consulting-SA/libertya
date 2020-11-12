package org.openXpertya.print.epson;

import org.openXpertya.print.fiscal.FiscalPacket;

public interface ESCPOSCommands {

	/** Comandos */
	public int HT = 9;
	public int LF = 10;
	public int FF = 12;
	public int CR = 13;
	public int CAN = 24;
	public int DLE = 16;
	public int EOT = 8;
	public int ENQ = 5;
	public int DC4 = 20;
	public int SP = 32;
	public int ESC = 27;
	public int GS = 29;
	
	/** Modo de corte de papel */
	public int CUT_PARTIAL = 49;
	public int CUT_FULL = 48;
	
	/** Posición del texto */
	public int POS_LEFT = 48;
	public int POS_CENTER = 49;
	public int POS_RIGHT = 50;
	
	/** Modo de subrayado */
	public int UNDERLINE_OFF = 48;
	public int UNDERLINE_ONE_DOT = 49;
	public int UNDERLINE_TWO_DOTS = 50;
	
	/** Tamaño de caracteres */
	public int TEXT_NORMAL_SIZE = 0;
	public int TEXT_DOUBLE_SIZE = 16;
	
	/** Pines */
	public int PIN_2 = 48;
	public int PIN_5 = 49;
	
	/** Opciones de Estados */
	public int STATUS_PRINTER = 1;
	public int STATUS_OFFLINE = 2;
	public int STATUS_ERROR = 3;
	public int STATUS_PAPER = 4;
	
	public FiscalPacket cmdPrintLineFeed(String line, int n);
	public FiscalPacket cmdPrintLine(String line);
	public FiscalPacket cmdFeed(int n);
	
	/**
	 * 0 PC437 [U.S.A.Standard Europe]
	 * 1 Katakana
	 * 2 PC850:Multilingual
	 * 3 PC860:Portuguese
	 * 4 PC863 [Canadian French]
	 * 5 PC865:Nodic
	 * 6 West Europe
	 * 7 Greek
	 * 8 Hebrew
	 * 9 PC755:East Europe
	 * 10 Iran
	 * 16 WPC1252
	 * 17 PC866:Cyrillic#2
	 * 18 PC852:Latin2
	 * 19 PC858
	 * 20 IranII
	 * 21 Latvian
	 */
	public FiscalPacket cmdSetEncoding(int encodes);
	public FiscalPacket cmdPulsePin(int pin);
	
	/**
	 * n = 1: Printer status
	 * Bit 	Off/On 	Hex Decimal 	Function
	 * 0 	0 		00 		0 		Not used.Fixed to Off.
	 * 1 	1 		02 		2 		Not used.Fixed to On.
	 * 2 	0 		00 		0 		Drawer open/close signal is LOW(connector pin3)
	 * 		1 		04 		4 		Drawer open/close signal is LOW(connector pin3)
	 * 3	0 		00 		0 		On-line
	 * 		1 		08 		8 		Off-line
	 * 4 	1 		10 		16 		Not used.Fixed to On.
	 * 5,6							Undefined.
	 * 7	0		00		00		Not used.Fixed to Off.
	 * 
	 * n = 2: Off-line status
	 * Bit 	Off/On 	Hex Decimal 	Function
	 * 0 	0 		00 		0 		Not used.Fixed to Off.
	 * 1 	1 		02 		2 		Not used.Fixed to On.
	 * 2 	0 		00 		0 		Cover is closed.
	 *  	1 		04 		4 		Cover is open.
	 * 3	0 		00 		0 		Paper is not being fed by using the FEED button.
	 * 		1 		08 		8 		Paper is beging fed by the FEED button.
	 * 4 	1 		10 		16 		Not used.Fixed to On.
	 * 5 	0 		00 		0 		No paper-end stop.
	 * 		1 		20 		32 		Printing is being stopped.
	 * 6	0 		00 		0 		No error.
	 * 		1 		40 		64 		Error occurs.
	 * 7	0 		00 		0 		Not used.Fixed to Off.
	 * 
	 * n = 3: Error status
	 * Bit 	Off/On 	Hex Decimal 	Function
	 * 0 	0 		00 		0 		Not used.Fixed to Off.
	 * 1 	1 		02 		2 		Not used.Fixed to On.
	 * 2 	- 		- 		- 		Undefined.
	 * 3 	0 		00 		0 		No auto-cutter error.
	 * 		1 		08 		8 		Auto-cutter error occurs.
	 * 4 	1 		10 		16 		Not used.Fixed to On.
	 * 5 	0		00 		0 		No unrecoverable error.
	 * 		1 		20 		32 		Unrecoverable error occurs.
	 * 6	0 		00 		0 		No auto-recoverable error.
	 * 		1 		40 		64 		Auto recoverable error occurs.
	 * 7	0 		00 		0 		Not used.Fixed to Off.
	 * 
	 * n = 4: Continuous paper sensor status
	 * Bit 	Off/On 	Hex Decimal 	Function
	 * 0 	0 		00 		0 		Not used.Fixed to Off.
	 * 1 	1 		02 		2 		Not used Fixed to On.
	 * 2,3 	0 		00 		0 		Paper roll near-end sensor:paper adequate.
	 * 		1 		0C 		12 		Paper near-end is detected by the paper roll near-end sensor.
	 * 4 	1 		10 		16 		Not used.Fixed to On.
	 * 5,6 	0 		00 		0 		Paper roll sensor:Paper present.
	 * 		1 		60 		96 		Paper roll end detected by paper roll senso.
	 * 7	0 		00 		0 		Not used.Fixed to Off.
	 * @param statusCode
	 * @return
	 */
	public FiscalPacket cmdGetStatus(int statusCode);
	public FiscalPacket cmdASB(int statusCode);
	public FiscalPacket cmdCut(int cutMode);
	
	/**
	 * Character Width Selection
	 * Hex Decimal Width
	 * 00 0 1(normal)
	 * 10 16 2(double-width)
	 * 20 32 3
	 * 30 48 4
	 * 40 64 5
	 * 50 80 6
	 * 60 96 7
	 * 70 112 8
	 * 
	 * Character Height Selection
	 * Hex Decimal Width
	 * 00 0 1(normal)
	 * 10 16 2(double-height)
	 * 20 32 3
	 * 30 48 4
	 * 40 64 5
	 * 50 80 6
	 * 60 96 7
	 * 70 112 8
	 */
	public FiscalPacket cmdSetCharacterSize(int size);
	public FiscalPacket cmdSetTextPosition(int position);
	public FiscalPacket cmdSetUnderlineMode(int underlineMode);
	public FiscalPacket cmdSetEmphasizeMode(int emphasizeMode);
	public FiscalPacket cmdOpenDrawer();
}
