package org.openXpertya.print.fiscal;

import org.openXpertya.print.fiscal.hasar.HasarFiscalPacket;
import org.openXpertya.print.fiscal.util.ArrayUtils;

public class ESCPOSFiscalPacket extends HasarFiscalPacket {

	public ESCPOSFiscalPacket(String encoding, int baseRolloverYear, FiscalPrinter fiscalPrinter) {
		super(encoding, baseRolloverYear, fiscalPrinter);
	}
	
	@Override
	public byte[] encodeBytes() {
		byte[] result = new byte[0];
		
		for (int i = 0, s = getSize(); i < s; i++) {
			byte[] bytes = get(i);
			if(bytes.length > 0)
				result = ArrayUtils.append(result, bytes);
		}
		return result;
	}

	@Override
	public void decode(byte[] packetBytes) {
		
	}

	@Override
	public void decode(int cmd, byte[] packetBytes) {
		decode(packetBytes);
	}
	
	public void setCommandCode(int value) { setByte(1, value); }
	public int getCommandCode() { return getSize() <= 1?0:getByte(1); }

	public void setPrinterStatus(int value) { setByte(0, value); }
	public int getPrinterStatus() { return getByte(0); }

	public void setFiscalStatus(int value) { setByte(0, value); }
	public int getFiscalStatus() { return getByte(0); }
}