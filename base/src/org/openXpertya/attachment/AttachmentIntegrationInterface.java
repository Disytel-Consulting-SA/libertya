package org.openXpertya.attachment;

public interface AttachmentIntegrationInterface {

	
	/** Inserta una entrada en la ubicacion remota especificada */
	public String insertEntry(byte[] data);
	
	/** Elimina una entrada remota a partir de su UID externo */
	public boolean deleteEntry(String externalUID);

	/** Recupera una entrada remota a partir de su UID externo */
	public byte[] retrieveEntry(String externalUID);

}
