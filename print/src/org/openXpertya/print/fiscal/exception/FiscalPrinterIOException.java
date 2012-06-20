package org.openXpertya.print.fiscal.exception;

import java.io.*;
import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.msg.MsgRepository;

/** Clase base de todas las excepciones que pueden
 * generar las impresoras fiscales */
public class FiscalPrinterIOException extends IOException
{
	private static String STR_REQUEST = MsgRepository.get("FiscalRequest");
	private static String STR_RESPONSE = MsgRepository.get("FiscalResponse");
	
	private FiscalPacket request;
	private FiscalPacket response;
	private String fullMessage = null;

	public FiscalPrinterIOException() {}
	public FiscalPrinterIOException(String s) { super(s); }

	public FiscalPrinterIOException(FiscalPacket request, FiscalPacket response) { this(null, request, response); }
	public FiscalPrinterIOException(String s, FiscalPacket request, FiscalPacket response)
	{
		super(s);
		this.request = request;
		this.response = response;
	}

	private static String buildMessage(String s, FiscalPacket request, FiscalPacket response)
	{
		String d;
		if (request != null)
		{
			if (response != null) d = STR_REQUEST + ": " + request + ", "+ STR_RESPONSE + ": " + response;
			else d = STR_REQUEST + ": " + request;
		}
		else
		{
			if (response != null) d = STR_RESPONSE + ": " + response;
			else d = null;
		}
		if (d != null)
		{
			if (s != null) s += "\n(" + d + ")";
			else s = d;
		}
		return s;
	}
	
	public String getFullMessage() {
		if(fullMessage == null)
			fullMessage = buildMessage(getMessage(), getRequestPacket(), getResponsePacket());
		return fullMessage;
	}

	/** Get the request associated with this exception, if any. */
	public FiscalPacket getRequestPacket() { return request; }
	/** Get the response associated with this exception, if any. */
	public FiscalPacket getResponsePacket() { return response; }
}
