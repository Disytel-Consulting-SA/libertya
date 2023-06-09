package org.openXpertya.print.fiscal.hasar;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.openXpertya.print.fiscal.AbstractFiscalPacket;
import org.openXpertya.print.fiscal.FiscalPrinter;
import org.openXpertya.print.fiscal.util.ArrayUtils;
import org.openXpertya.print.fiscal.util.ByteFormatter;

/** The FiscalPacket implementation used by HasarFiscalDevice and BasicFiscalDevice. */
/**
 * @author Franco Bonafine
 * @date 24/01/2008
 */
public class HasarFiscalPacket extends AbstractFiscalPacket
{
	private static final byte FS = 0x1C;
	private static final byte NL  = 0x0D;
	private String encoding;
	private final int baseRolloverYear;
	private final int baseRolloverCentury;
	private final int rolloverYear;

	public HasarFiscalPacket(String encoding, int baseRolloverYear, FiscalPrinter fiscalPrinter)
	{
		super(fiscalPrinter);
		if (baseRolloverYear < 0) throw new IllegalArgumentException();
		this.encoding = encoding;
		this.baseRolloverYear = baseRolloverYear;
		rolloverYear = baseRolloverYear % 100;
		baseRolloverCentury = baseRolloverYear - rolloverYear;
	}

	public String toString()
	{
		StringBuffer b = new StringBuffer();
		b.append('{');
		for (int i = 0, s = getSize(); i < s; i++)
		{
			b.append(' ');
			if (i == 0)
			{
				byte[] f = get(i);
				if (f.length == 1) ByteFormatter.toHex(b, f[0]);
				else ByteFormatter.toHexString(b, f);
			}
			else ByteFormatter.toASCIIString(b, get(i));
		}
		b.append(' ').append('}');
		return b.toString();
	}

	/** Get the encoding in use for strings. */
	public String getEncoding() { return encoding; }

	/** Get the base roll-over year in use for dates. Valid years are from baseRolloverYear to baseRolloverYear + 99 inclusive. */
	public int getBaseRolloverYear() { return baseRolloverYear; }

	// String Fields

	public void setString(int field, String value)
	{
		byte[] f;
		try { f = value.getBytes(encoding); }
		catch (UnsupportedEncodingException e) { throw new RuntimeException("Unsupported encoding (" + encoding + ")"); }
		set(field, f);
	}

	public void setString(int field, String value, int style)
	{
		byte[] f;
		try { f = value.getBytes(encoding); }
		catch (UnsupportedEncodingException e) { throw new RuntimeException("Unsupported encoding (" + encoding + ")"); }
		if (style != STYLE_NORMAL)
		{
			if (style < 0 || style > 15) throw new IllegalArgumentException();
			int l = f.length;
			byte[] x = new byte[l + 1];
			x[0] = (byte) (0xF0 | style);
			System.arraycopy(f, 0, x, 1, l);
			f = x;
		}
		set(field, f);
	}

	public String getString(int field)
	{
		byte[] f = get(field);
		try { return f.length == 0 || (f[0] & 0xF0) != 0xF0 ? new String(f, encoding) : new String(f, 1, f.length - 1, encoding); }
		catch (UnsupportedEncodingException e) { throw new RuntimeException("Unsupported encoding (" + encoding + ")"); }
	}

	public int getStringStyle(int field)
	{
		byte[] f = get(field);
		return f.length == 0 || (f[0] & 0xF0) != 0xF0 ? STYLE_NORMAL : f[0] & 0xF;
	}

	// Date And Time Fields

	public void setDate(int field, int year, int month, int day)
	{
		if (year < baseRolloverYear || year >= baseRolloverYear + 100 || month < 1 || month > 12 || day < 1 || day > 31) throw new IllegalArgumentException();
		setString(field, String.valueOf(1000000 + (year % 100) * 10000 + month * 100 + day).substring(1, 7));
	}

	public int getDateYear(int field)
	{
		String v = getString(field);
		if (v.length() != 6) throw new NumberFormatException();
		int y = Integer.parseInt(v.substring(0, 2));
		if (y < 0) throw new NumberFormatException();
		return y + baseRolloverCentury + (y < rolloverYear ? 100 : 0);
	}

	public int getDateMonth(int field)
	{
		String v = getString(field);
		if (v.length() != 6) throw new NumberFormatException();
		int m = Integer.parseInt(v.substring(2, 4));
		if (m < 0) throw new NumberFormatException();
		return m;
	}

	public int getDateDay(int field)
	{
		String v = getString(field);
		if (v.length() != 6) throw new NumberFormatException();
		int d = Integer.parseInt(v.substring(4, 6));
		if (d < 0) throw new NumberFormatException();
		return d;
	}

	public void setTime(int field, int hour, int minute, int second)
	{
		if (hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59) throw new IllegalArgumentException();
		setString(field, String.valueOf(1000000 + hour * 10000 + minute * 100 + second).substring(1, 7));
	}

	public int getTimeHour(int field)
	{
		String v = getString(field);
		if (v.length() != 6) throw new NumberFormatException();
		int h = Integer.parseInt(v.substring(0, 2));
		if (h < 0) throw new NumberFormatException();
		return h;
	}

	public int getTimeMinute(int field)
	{
		String v = getString(field);
		if (v.length() != 6) throw new NumberFormatException();
		int m = Integer.parseInt(v.substring(2, 4));
		if (m < 0) throw new NumberFormatException();
		return m;
	}

	public int getTimeSecond(int field)
	{
		String v = getString(field);
		if (v.length() != 6) throw new NumberFormatException();
		int s = Integer.parseInt(v.substring(4, 6));
		if (s < 0) throw new NumberFormatException();
		return s;
	}

	// Special Fields

	public void setCommandCode(int value) { setByte(0, value); }
	public int getCommandCode() { return getByte(0); }

	public void setPrinterStatus(int value) { setHex16(1, value); }
	public int getPrinterStatus() { return getHex16(1); }

	public void setFiscalStatus(int value) { setHex16(2, value); }
	public int getFiscalStatus() { return getHex16(2); }

	public byte[] encodeBytes() {
		byte[] result = new byte[0];
		
		for (int i = 0, s = getSize(); i < s; i++) {
			byte[] bytes = get(i);
			if(bytes.length > 0)
				result = ArrayUtils.append(result, bytes);
			if(i != s - 1)
				result = ArrayUtils.append(result, FS);			
		}
		result = ArrayUtils.append(result, NL);
		return result;
	}

	public String encodeString() {
		try {
			return new String(encodeBytes(), getEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void decode(byte[] packetBytes) {
		ByteArrayOutputStream bstr = new ByteArrayOutputStream();
		int f = 0;
		clear();
		bstr.reset();
		for(int i = 0; i < packetBytes.length; i++) {
			int b = packetBytes[i];
			if(!(0x00 <= b && b < 0x20)) {
				bstr.write(b);
			}
			if (b == FS || i == (packetBytes.length - 1)) {
				set(f++, bstr.toByteArray());
				bstr.reset();
			}
		}
	}

	public void decode(int cmd, byte[] packetBytes) {
		byte[] cmdBytes = {(byte) cmd, FS};
		byte[] newPacketBytes = ArrayUtils.append(cmdBytes,packetBytes);
		decode(newPacketBytes);
	}

	@Override
	public void add(String value) {
		byte[] f;
		try {
			f = value.getBytes(getEncoding());
		} catch (Exception e) {
			throw new RuntimeException("Unsupported encoding (" + getEncoding() + ")");
		}
		add(f);
	}
}
