/*
 * @(#)MAttachment.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import org.openXpertya.attachment.AttachmentIntegrationInterface;
import org.openXpertya.attachment.IntegrationMockImpl;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.MimeType;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *      Attachment Model.
 *      One Attachment can have multiple entries
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MAttachment.java,v 1.12 2005/03/11 20:28:32 jjanke Exp $
 */
public class MAttachment extends X_AD_Attachment {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MAttachment.class);

    /** Indicator for zip data */
    public static final String	ZIP	= "zip";

    /** Indicator for no data */
    public static final String	NONE	= ".";

    /** List of Entry Data */
    private ArrayList	m_items	= null;

    /** Prefijo almacenado en las entradas con almacenamiento externo */
    public static final String EXTERNAL_ATTACHMENT_PREFIX = "ExternalUID=";
    
    /** Atributo en donde se define el handler para adjuntos externos */
    public static final String EXTERNAL_ATTACHMENT_IMPLEMENTATION_KEY = "ExternalAttachmentImplementation"; 
    
    /** Atributo que define si hay soporte de adjuntos externos habilitado*/
    public static final String EXTERNAL_ATTACHMENT_ENABLED = "ExternalAttachmentEnabled"; 

    /** Atributo que define si hay soporte de adjuntos locales habilitado*/
    public static final String LOCAL_ATTACHMENT_ENABLED = "LocalAttachmentEnabled"; 
    
    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Attachment_ID id
     * @param trxName
     */
    public MAttachment(Properties ctx, int AD_Attachment_ID, String trxName) {
        super(ctx, AD_Attachment_ID, trxName);
    }		// MAttachment

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MAttachment(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MAttachment

    /**
     *      New Constructor
     *      @param ctx context
     *      @param AD_Table_ID table
     *      @param Record_ID record
     * @param trxName
     */
    public MAttachment(Properties ctx, int AD_Table_ID, int Record_ID, String trxName) {

        this(ctx, 0, trxName);
        setAD_Table_ID(AD_Table_ID);
        setRecord_ID(Record_ID);

    }		// MAttachment

    
    /** Sobrecarga para compatibilidad */
    public boolean addEntry(File file) {
    	return addEntry(file, null);
    }
    
    /**
     *      Add new Data Entry
     *      @param file file
     *      @return true if added
     */
    public boolean addEntry(File file, AttachmentIntegrationInterface handler) {

        if (file == null) {

            log.warning("addEntry - No File");

            return false;
        }

        if (!file.exists() || file.isDirectory()) {

            log.warning("addEntry - not added - " + file + ", Exists=" + file.exists() + ", Directory=" + file.isDirectory());

            return false;
        }

        log.fine("addEntry - " + file);

        //
        String	name	= file.getName();
        byte[]	data	= null;

        try {

            FileInputStream		fis	= new FileInputStream(file);
            ByteArrayOutputStream	os	= new ByteArrayOutputStream();
            byte[]			buffer	= new byte[1024 * 8];		// 8kB
            int				length	= -1;

            while ((length = fis.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }

            fis.close();
            data	= os.toByteArray();
            os.close();

        } catch (IOException ioe) {
            log.log(Level.SEVERE, "addEntry (file)", ioe);
        }

        return addEntry(name, data, handler);

    }		// addEntry

    /**
     *      Add Entry
     *      @param item attachment entry
     *      @returns true if added
     *
     * @return
     */
    public boolean addEntry(MAttachmentEntry item) {

        if (item == null) {
            return false;
        }

        if (m_items == null) {
            loadLOBData();
        }

        boolean	retValue	= m_items.add(item);

        log.fine("addEntry - " + item.toStringX());
        addTextMsg(" ");	// otherwise not saved

        return retValue;

    }		// addEntry

    
    /** Sobrecarga por compatibilidad */
    public boolean addEntry(String name, byte[] data) {
    	return addEntry(name, data, null);
    }
    
    /**
     *      Add new Data Entry
     *      @param name name
     *      @param data data
     *      @return true if added
     */
    public boolean addEntry(String name, byte[] data, AttachmentIntegrationInterface handler) {

        if ((name == null) || (data == null)) {
            return false;
        }

        MAttachmentEntry newEntry = new MAttachmentEntry(name, data);
        newEntry.setM_handler(handler);
        return addEntry(newEntry);	// random index

    }								// addEntry

    /**
     *      Add to Text Msg
     *      @param added text
     */
    public void addTextMsg(String added) {

        String	oldTextMsg	= getTextMsg();

        if (oldTextMsg == null) {
            setTextMsg(added);
        } else if (added != null) {
            setTextMsg(oldTextMsg + added);
        }

    }		// addTextMsg

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true if can be saved
     */
    protected boolean beforeSave(boolean newRecord) {

        if ((getTitle() == null) ||!getTitle().equals(ZIP)) {
            setTitle(ZIP);
        }

        return saveLOBData();		// save in BinaryData

    }					// beforeSave

    
    @Override
    protected boolean beforeDelete() {

    	try {
	    	// Eliminar todos los documentos externos
	        for (int i = 0; i < m_items.size(); i++) {
	        	if (m_items.get(i) == null || ((MAttachmentEntry)m_items.get(i)).getM_UID() == null) 
	        		continue;
	        	if (!((MAttachmentEntry)m_items.get(i)).getM_handler().deleteEntry(((MAttachmentEntry)m_items.get(i)).getM_UID())) {
	        		log.saveError("Error", "Error al eliminar el archivo adjunto remoto");
	    			return false;
	        	}
	        }
	    	
	    	// Si hay previas entradas marcadas para su eliminacion remota, eliminarlas
	    	for (MAttachmentEntry aRemoteEntry : remoteEntriesMarkedForDeletion) {
	    		if (!aRemoteEntry.getM_handler().deleteEntry(aRemoteEntry.getM_UID())) {
	    			log.saveError("Error", "Error al eliminar el archivo adjunto remoto");
	    			return false;
	    		}
	    	}
    	} catch (Exception e) {
    		log.saveError("Error", "Error al eliminar adjunto externo: " + e.getMessage());
    		return false;
    	}
        
    	return super.beforeDelete();
    }
    
    /** nomina temporal de entradas remotas marcadas para su eliminacion */
    protected ArrayList<MAttachmentEntry> remoteEntriesMarkedForDeletion = new ArrayList<MAttachmentEntry>(); 
    
    /**
     *      Delete Entry
     *      @param index index
     *      @return true if deleted
     */
    public boolean deleteEntry(int index) {

        if ((index >= 0) && (index < m_items.size())) {

        	// Las entradas remotas no se deben eliminar en este momento, sino marcarlas para su eliminacion
        	// Si luego el usuario confirma la eliminacion entonces se eliminará al
        	// actualizar el LOBData (saveLOBData) al aceptar la ventana general
        	if (((MAttachmentEntry)m_items.get(index)).getM_UID() != null) {
        		remoteEntriesMarkedForDeletion.add((MAttachmentEntry)m_items.get(index));
        	}
        	
            m_items.remove(index);
            log.config("Index=" + index + " - NewSize=" + m_items.size());

            return true;
        }

        log.warning("Not deleted Index=" + index + " - Size=" + m_items.size());

        return false;

    }		// deleteEntry

    /**
     *      Dump Entry Names
     */
    public void dumpEntryNames() {

        if (m_items == null) {
            loadLOBData();
        }

        if ((m_items == null) || (m_items.size() == 0)) {

            System.out.println("- no entries -");

            return;
        }

        System.out.println("- entries: " + m_items.size());

        for (int i = 0; i < m_items.size(); i++) {
            System.out.println("  - " + getEntryName(i));
        }

    }		// dumpEntryNames

    /**
     *      Load Data into local m_data
     *      @return true if success
     */
    private boolean loadLOBData() {
    	
    	// Vaciar la nomina de entradas remotas marcadas para su eliminacion
    	remoteEntriesMarkedForDeletion = new ArrayList<MAttachmentEntry>();
    	
        // Reset
        m_items	= new ArrayList();

        //
        byte[]	data	= getBinaryData();

        if (data == null) {
            return true;
        }

        log.fine("ZipSize=" + data.length);

        if (data.length == 0) {
            return true;
        }

        // Old Format - single file
        if (!ZIP.equals(getTitle())) {

            m_items.add(new MAttachmentEntry(getTitle(), data, 1));

            return true;
        }

        try {

            ByteArrayInputStream	in	= new ByteArrayInputStream(data);
            ZipInputStream		zip	= new ZipInputStream(in);
            ZipEntry			entry	= zip.getNextEntry();

            while (entry != null) {

                String			name	= entry.getName();
                ByteArrayOutputStream	out	= new ByteArrayOutputStream();
                byte[]			buffer	= new byte[2048];
                int			length	= zip.read(buffer);

                while (length != -1) {

                    out.write(buffer, 0, length);
                    length	= zip.read(buffer);
                }

                //
                byte[]	dataEntry	= out.toByteArray();

                log.fine(name + " - size=" + dataEntry.length + " - zip=" + entry.getCompressedSize() + "(" + entry.getSize() + ") " + (entry.getCompressedSize() * 100 / entry.getSize()) + "%");

                //
                MAttachmentEntry newEntry = new MAttachmentEntry(name, dataEntry, m_items.size() + 1);
                m_items.add(newEntry);
                
                // El zip entry es una referencia a un documento externo?
                String extras = (entry.getExtra() != null ? new String(entry.getExtra()) : null);
                if (extras != null && extras.startsWith(EXTERNAL_ATTACHMENT_PREFIX)) {
                	newEntry.setM_UID(extras.substring(EXTERNAL_ATTACHMENT_PREFIX.length()));
                	newEntry.setM_handler(getIntegrationImpl());
                }
                
                entry	= zip.getNextEntry();
            }

        } catch (Exception e) {

            log.log(Level.SEVERE, "loadLOBData", e);
            m_items	= null;

            return false;
        }

        return true;
    }		// loadLOBData

    /**
     *      Test
     *      @param args ignored
     */
    public static void main(String[] args) {

        // System.setProperty("javax.activation.debug", "true");
        System.out.println(MimeType.getMimeType("data.xls"));
        System.out.println(MimeType.getMimeType("data.cvs"));
        System.out.println(MimeType.getMimeType("data.txt"));
        System.out.println(MimeType.getMimeType("data.log"));
        System.out.println(MimeType.getMimeType("data.html"));
        System.out.println(MimeType.getMimeType("data.htm"));
        System.out.println(MimeType.getMimeType("data.png"));
        System.out.println(MimeType.getMimeType("data.gif"));
        System.out.println(MimeType.getMimeType("data.jpg"));
        System.out.println(MimeType.getMimeType("data.xml"));
        System.out.println(MimeType.getMimeType("data.rtf"));
        System.exit(0);
        org.openXpertya.OpenXpertya.startupEnvironment(true);

        MAttachment	att	= new MAttachment(Env.getCtx(), 100, 0, null);

        att.addEntry(new File("C:\\OpenXpertya\\Dev.properties"));
        att.addEntry(new File("C:\\ServidorOXP\\index.html"));
        att.save();
        System.out.println(att);
        att.dumpEntryNames();

        int	AD_Attachment_ID	= att.getAD_Attachment_ID();

        //
        System.out.println("===========================================");
        att	= new MAttachment(Env.getCtx(), AD_Attachment_ID, null);
        System.out.println(att);
        att.dumpEntryNames();
        System.out.println("===========================================");

        MAttachmentEntry[]	entries	= att.getEntries();

        for (int i = 0; i < entries.length; i++) {

            MAttachmentEntry	entry	= entries[i];

            entry.dump();
        }

        System.out.println("===========================================");
        att.delete(true);
    }		// main

    /**
     *      Save Entry Data in Zip File format
     *      @return true if saved
     */
    private boolean saveLOBData() {

        if ((m_items == null) || (m_items.size() == 0)) {

            setBinaryData(null);

            return true;
        }

        ByteArrayOutputStream	out	= new ByteArrayOutputStream();
        ZipOutputStream		zip	= new ZipOutputStream(out);

        zip.setMethod(ZipOutputStream.DEFLATED);
        zip.setLevel(Deflater.BEST_COMPRESSION);
        zip.setComment("openXpertya");

        //
        try {
        	
        	// Si hay entradas marcadas para su eliminacion remota, eliminarlas
        	for (MAttachmentEntry aRemoteEntry : remoteEntriesMarkedForDeletion) {
        		aRemoteEntry.getM_handler().deleteEntry(aRemoteEntry.getM_UID());
        	}
        	// Vaciar la nomina
        	remoteEntriesMarkedForDeletion = new ArrayList<MAttachmentEntry>();

            for (int i = 0; i < m_items.size(); i++) {

                MAttachmentEntry	item	= getEntry(i);
                ZipEntry		entry	= new ZipEntry(item.getName());

                entry.setTime(System.currentTimeMillis());
                entry.setMethod(ZipEntry.DEFLATED);

                byte[]	data	= item.getData();
                
                // Si es una entrada externa todavía no existente, hay que: 1) Persistir remotamente y 2) localmente almacenar el UID
                String extUID = null;
                if (item.isExternalEntry() && item.getM_UID() == null) {
                	extUID = item.getM_handler().insertEntry(data, item.getName());		// Interaccion con el manejador externo
                	item.setM_UID(extUID);												// Seteamos el UID recibido como respuesta
                }
                if (item.getM_UID() != null) {
                	extUID = item.getM_UID();
                	data = (EXTERNAL_ATTACHMENT_PREFIX + extUID).getBytes();			// Almacenamos la referencia como datos del binario
                	entry.setExtra((EXTERNAL_ATTACHMENT_PREFIX + extUID).getBytes());	// Y tambien como comentario extra
                }

                zip.putNextEntry(entry);
                zip.write(data, 0, data.length);
                zip.closeEntry();
                log.fine(entry.getName() + " - " + entry.getCompressedSize() + " (" + entry.getSize() + ") " + (entry.getCompressedSize() * 100 / entry.getSize()) + "%");
            }

            // zip.finish();
            zip.close();

            byte[]	zipData	= out.toByteArray();

            log.fine("Length=" + zipData.length);
            setBinaryData(zipData);

            return true;

        } catch (Exception e) {
            log.log(Level.SEVERE, "saveLOBData", e);
            log.saveError("Error", e.getMessage());
        }

        setBinaryData(null);

        return false;

    }		// saveLOBData

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MAttachment[");

        sb.append(getAD_Attachment_ID()).append(",Title=").append(getTitle()).append(",Entries=").append(getEntryCount());

        for (int i = 0; i < getEntryCount(); i++) {

            if (i == 0) {
                sb.append(":");
            } else {
                sb.append(",");
            }

            sb.append(getEntryName(i));
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Attachment
     *      @param ctx context
     *      @param AD_Table_ID table
     *      @param Record_ID record
     *      @return attachment or null
     */
    public static MAttachment get(Properties ctx, int AD_Table_ID, int Record_ID) {

        MAttachment		retValue	= null;
        PreparedStatement	pstmt		= null;
        String			sql		= "SELECT * FROM AD_Attachment WHERE AD_Table_ID=? AND Record_ID=?";

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Table_ID);
            pstmt.setInt(2, Record_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new MAttachment(ctx, rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "MAttachment", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return retValue;

    }		// get

    /**
     *      Get Attachment Entries as array
     *      @returns array or null
     *
     * @return
     */
    public MAttachmentEntry[] getEntries() {

        if (m_items == null) {
            loadLOBData();
        }

        MAttachmentEntry[]	retValue	= new MAttachmentEntry[m_items.size()];

        m_items.toArray(retValue);

        return retValue;

    }		// getEntries

    /**
     *      Get Attachment Entry
     *      @param index index of the item
     *      @returns Entry or null
     *
     * @return
     */
    public MAttachmentEntry getEntry(int index) {

        if (m_items == null) {
            loadLOBData();
        }

        if ((index < 0) || (index >= m_items.size())) {
            return null;
        }

        return (MAttachmentEntry) m_items.get(index);

    }		// getEntry

    /**
     *      Get Entry Count
     *      @return number of entries
     */
    public int getEntryCount() {

        if (m_items == null) {
            loadLOBData();
        }

        return m_items.size();

    }		// getEntryCount

    /**
     *      Get Entry Data
     *      @param index index
     *      @return data or null
     */
    public byte[] getEntryData(int index) {

        MAttachmentEntry	item	= getEntry(index);

        if (item != null) {
            return item.getData();
        }

        return null;

    }		// getEntryData

    /**
     *      Get Entry File with name
     *      @param index index
     *      @param file file
     *      @return file
     */
    public File getEntryFile(int index, File file) {

        MAttachmentEntry	item	= getEntry(index);

        if (item != null) {
            return item.getFile(file);
        }

        return null;

    }		// getEntryFile

    /**
     *      Get Entry File with name
     *      @param index index
     *      @param fileName optional file name
     *      @return file
     */
    public File getEntryFile(int index, String fileName) {

        MAttachmentEntry	item	= getEntry(index);

        if (item != null) {
            return item.getFile(fileName);
        }

        return null;

    }		// getEntryFile

    /**
     *      Get Entry Name
     *      @param index index
     *      @return name or null
     */
    public String getEntryName(int index) {

        MAttachmentEntry	item	= getEntry(index);

        if (item != null) {
            return item.getName();
        }

        return null;

    }		// getEntryName

    /**
     *      Get Text Msg
     *      @return trimmed message
     */
    public String getTextMsg() {

        String	msg	= super.getTextMsg();

        if (msg == null) {
            return null;
        }

        return msg.trim();

    }		// setTextMsg

    
    /** Instancia de gestion externa de documentos, como por ejemplo un gestionador de documentos remoto */
    protected static AttachmentIntegrationInterface externalImpl = null;
    
    /** 
     * Devuelve la implementación a utilizar para la gestion remota de adjuntos
     */
    public static AttachmentIntegrationInterface getIntegrationImpl() {
    	// Ya está cacheada? Retornarla
    	if (externalImpl != null)
    		return externalImpl;
    	
    	// Obtener la preferencia.  Si no existe, retornar clase mock
	    String className = MPreference.GetCustomPreferenceValue(EXTERNAL_ATTACHMENT_IMPLEMENTATION_KEY);
	    if (className == null || className.length() == 0) {
	    	externalImpl = new IntegrationMockImpl();
	    } else {
		    try {
		    	// Instanciar la clase manejadora externa
			    Class clazz = Class.forName(className);
			    externalImpl = (AttachmentIntegrationInterface)clazz.newInstance();
	    	} catch (Exception e) {
	    		s_log.severe("Error al instanciar manejador de adjuntos remotos: " + e.getMessage());
	    	}	    	
	    }
	    return externalImpl;
    }

    /** Visualizar la botonera de adjuntos locales unicamente si la preferencia lo habilita (si no existe, habilitado por defecto) */
    public static boolean isLocalAttachmentEnabled() {
    	return !"N".equalsIgnoreCase(MPreference.GetCustomPreferenceValue(LOCAL_ATTACHMENT_ENABLED));  
    }

    /** Visualizar la botonera de adjuntos externos unicamente si la preferencia lo habilita (si no existe, deshabilitado por defecto) */
    public static boolean isExternalAttachmentEnabled() {
    	return "Y".equalsIgnoreCase(MPreference.GetCustomPreferenceValue(EXTERNAL_ATTACHMENT_ENABLED));  
    }
}	// MAttachment



/*
 * @(#)MAttachment.java   02.jul 2007
 * 
 *  Fin del fichero MAttachment.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
