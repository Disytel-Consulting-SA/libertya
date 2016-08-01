package org.openXpertya.attachment;

import java.util.HashMap;
import java.util.Properties;

public class IntegrationMockImpl implements AttachmentIntegrationInterface {

	/** */
	HashMap<String, byte[]> mockRepository = new HashMap<String, byte[]>(); 
	
	@Override
	public String insertEntry(byte[] data, String name, int clientID, int orgID) throws Exception {
		String key = "" + Math.random();
		mockRepository.put(key, data);
		return key;
	}
	
	@Override
	public boolean deleteEntry(String externalUID) throws Exception {
		mockRepository.remove(externalUID);
		return true;
	}

	@Override
	public byte[] retrieveEntry(String externalUID) throws Exception {
		if (mockRepository.get(externalUID) != null)
			return mockRepository.get(externalUID);
		return "MOCK".getBytes();
	}

}
