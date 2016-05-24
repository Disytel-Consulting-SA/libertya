package org.openXpertya.attachment;

import java.util.HashMap;

public class IntegrationMockImpl implements AttachmentIntegrationInterface {

	/** */
	HashMap<String, byte[]> mockRepository = new HashMap<String, byte[]>(); 
	
	@Override
	public String insertEntry(byte[] data) {
		String key = "" + Math.random();
		mockRepository.put(key, data);
		return key;
	}
	
	@Override
	public boolean deleteEntry(String externalUID) {
		mockRepository.remove(externalUID);
		return true;
	}

	@Override
	public byte[] retrieveEntry(String externalUID) {
		if (mockRepository.get(externalUID) != null)
			return mockRepository.get(externalUID);
		return "MOCK".getBytes();
	}

}
