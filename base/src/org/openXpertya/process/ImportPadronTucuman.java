package org.openXpertya.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.util.DB;
import org.openXpertya.util.Trx;

public abstract class ImportPadronTucuman extends ImportPadronBsAsFromCopy {

	/** Archivo de padrón */
	private File file = null;
	
	public ImportPadronTucuman(Properties ctx, int orgID, String fileName, String padronType, int chunkSize,
			String trxName) {
		super(ctx, orgID, fileName, padronType, chunkSize, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void validate() throws Exception {
		// No se realizan validaciones ya que si somos esta clase entonces seleccionamos
		// el tipo de padrón correspondiente
	}

	@Override
	protected void deleteDataTempTable() throws Exception {
		// No se elimina nada previamente de ninguna tabla ya que se impacta directo en
		// el padrón, no existen tablas temporales de padrón Tucumán
	}
	
	@Override
	protected void copyFileToTempTable() throws Exception {
		File file = new File(getPath() + p_NameCsvFile);
		if(!file.exists()) {
			throw new Exception("No existe el archivo o no es posible acceder: "+getPath() + p_NameCsvFile);
		}
		setFile(file);
	}
	
	@Override
	protected void actualizarPadron() throws Exception {
		// Iterar por el principio del archivo, omitiendo las líneas que no posean CUIT
		String line = null;
		boolean found = false;
		BufferedReader br = null;
		int ri = 0;
		String cuit = null;
		try {
			br = new BufferedReader(new FileReader(getFile())); 
			while (!found && (line = br.readLine()) != null) {
				// Obtener los primeros 11 caracteres para ver si es un CUIT, si es asi se sale
				// de aca
				if(line != null && line.length() > 10) {
					cuit = line.substring(0, 11);
					found = CalloutInvoiceExt.ValidarCUIT(cuit);
				}
			}
			// Iterar por las líneas del archivo y realizar el insert
			int i = 0;
			if(found) {
				Trx.getTrx(get_TrxName()).start();
				String insert = "INSERT INTO c_bpartner_padron_bsas (c_bpartner_padron_bsas_id, isactive, ad_client_id, ad_org_id, created, updated, createdby, updatedby, alta_baja, padrontype, cuit, "
						+ getInsertColumnNames() + ") VALUES ";
				StringBuffer sql = new StringBuffer(insert);
				do {
					cuit = line.substring(0, 11);
					sql.append(" ( nextval('seq_c_bpartner_padron_bsas'), 'Y', " + ad_Client_ID + ", " + p_AD_Org_ID
							+ ", now(), now(), " + ad_User_ID + ", " + ad_User_ID + ", 'A', '" + p_PadronType + "', '"+cuit+"', ");
					sql.append(getInsertValues(line));
					sql.append("), ");
					i++;
					// Llegamos al chunksize, commitear
					if(i == p_ChunkSize) {
						sql.deleteCharAt(sql.lastIndexOf(","));
						sql.append(";");
						ri += DB.executeUpdate(sql.toString(), false, get_TrxName(), true);
						Trx.getTrx(get_TrxName()).commit();
						sql = new StringBuffer(insert);
						i = 0;
					}
				} while ((line = br.readLine()) != null);
				// El remanente de los registros fuera del chunksize
				if(i > 0) {
					sql.deleteCharAt(sql.lastIndexOf(","));
					sql.append(";");
					ri += DB.executeUpdate(sql.toString(), false, get_TrxName(), true);
					Trx.getTrx(get_TrxName()).commit();
				}
			}
		} catch(IOException ioe) {
			throw new Exception(ioe);
		} finally {
			br.close();
			regInserted = ri;
		}
	}

	/**
	 * @return los nombres de las columnas a insertar para armar el INSERT INTO
	 */
	protected abstract String getInsertColumnNames();

	/**
	 * @param line línea actual del archivo
	 * @return valores de las columnas a insertar para armar el VALUES
	 */
	protected abstract String getInsertValues(String line);
	
	protected File getFile() {
		return file;
	}

	protected void setFile(File file) {
		this.file = file;
	}
}
