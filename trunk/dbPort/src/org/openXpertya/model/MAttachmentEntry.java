/*
 * @(#)MAttachmentEntry.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.MimeType;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Random;
import java.util.logging.Level;

/**
 *      Individual Attachment Entry of MAttachment
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MAttachmentEntry.java,v 1.4 2005/03/11 20:28:36 jjanke Exp $
 */
public class MAttachmentEntry {

    /** Random Seed */
    private static long	s_seed	= System.currentTimeMillis();

    /** Random Number */
    private static Random	s_random	= new Random(s_seed);

    /** The Name */
    private String	m_name	= "?";

    /** Index */
    private int	m_index	= 0;

    /** The Data */
    private byte[]	m_data	= null;

    /** Logger */
    protected CLogger	log	= CLogger.getCLogger(getClass());

    /**
     *      Attachment Entry
     *      @param name name
     *      @param data binary data
     */
    public MAttachmentEntry(String name, byte[] data) {
        this(name, data, 0);
    }		// MAttachmentItem

    /**
     *      Attachment Entry
     *      @param name name
     *      @param data binary data
     *      @param index optional index
     */
    public MAttachmentEntry(String name, byte[] data, int index) {

        super();
        setName(name);
        setData(data);

        if (index > 0) {
            m_index	= index;
        } else {

            long	now	= System.currentTimeMillis();

            if (s_seed + 3600000l < now)	// older then 1 hour
            {

                s_seed		= now;
                s_random	= new Random(s_seed);
            }

            m_index	= s_random.nextInt();
        }

    }						// MAttachmentItem

    /**
     *      Dump Data
     */
    public void dump() {

        String	hdr	= "----- " + getName() + " -----";

        System.out.println(hdr);

        if (m_data == null) {

            System.out.println("----- no data -----");

            return;
        }

        // raw data
        for (int i = 0; i < m_data.length; i++) {

            char	data	= (char) m_data[i];

            System.out.print(data);
        }

        System.out.println();
        System.out.println(hdr);

        // Count nulls at end
        int	ii		= m_data.length - 1;
        int	nullCount	= 0;

        while (m_data[ii--] == 0) {
            nullCount++;
        }

        System.out.println("----- Length=" + m_data.length + ", EndNulls=" + nullCount + ", RealLength=" + (m_data.length - nullCount));

        /**
         * //      Dump w/o nulls
         * if (nullCount > 0)
         * {
         *       for (int i = 0; i < m_data.length-nullCount; i++)
         *               System.out.print((char)m_data[i]);
         *       System.out.println ();
         *       System.out.println (hdr);
         * }
         * /** *
         */

    }		// dump

    /**
     *      To String
     *      @return name
     */
    public String toString() {
        return m_name;
    }		// toString

    /**
     *      To String Extended
     *      @return name (length)
     */
    public String toStringX() {

        StringBuffer	sb	= new StringBuffer(m_name);

        if (m_data != null) {

            sb.append(" (");

            //
            float	size	= m_data.length;

            if (size <= 1024) {
                sb.append(m_data.length).append(" B");
            } else {

                size	/= 1024;

                if (size > 1024) {

                    size	/= 1024;
                    sb.append(size).append(" MB");

                } else {
                    sb.append(size).append(" kB");
                }
            }

            //
            sb.append(")");
        }

        sb.append(" - ").append(getContentType());

        return sb.toString();

    }		// toStringX

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Content (Mime) Type
     *      @return content type
     */
    public String getContentType() {
        return MimeType.getMimeType(m_name);
    }		// getContentType

    /**
     * @return Returns the data.
     */
    public byte[] getData() {
        return m_data;
    }

    /**
     *      Get File with default name
     *      @return File
     */
    public File getFile() {
        return getFile(getName());
    }		// getFile

    /**
     *      Get File
     *      @param file out file
     *      @return file
     */
    public File getFile(File file) {

        if ((m_data == null) || (m_data.length == 0)) {
            return null;
        }

        try {
            FileOutputStream	fos	= new FileOutputStream(file);

            fos.write(m_data);
            fos.close();

        } catch (IOException ioe) {
            log.log(Level.SEVERE, "getFile", ioe);
        }

        return file;

    }		// getFile

    /**
     *      Get File with name
     *      @param fileName optional file name
     *      @return file
     */
    public File getFile(String fileName) {

        if ((fileName == null) || (fileName.length() == 0)) {
            fileName	= getName();
        }

        return getFile(new File(fileName));

    }		// getFile

    /**
     *      Get Attachment Index
     *      @return timestamp
     */
    public int getIndex() {
        return m_index;
    }		// getIndex

    /**
     *      Get Data as Input Stream
     *      @return input stream
     */
    public InputStream getInputStream() {

        if (m_data == null) {
            return null;
        }

        return new ByteArrayInputStream(m_data);

    }		// getInputStream

    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }

    /**
     *      Isattachment entry a Graphic
     *      @return true if *.gif, *.jpg, *.png
     */
    public boolean isGraphic() {
    	/*
    	 * Modified By Matías Cap - Disytel
    	 * 
    	 * Agregación de un nuevo tipo de formato, formato bmp, para que los tome como imágen y los 
    	 * muestre en el panel para ver las imágenes.
    	 */
    	
        return m_name.toLowerCase().endsWith(".gif") || m_name.toLowerCase().endsWith(".jpg") || m_name.toLowerCase().endsWith(".png") || m_name.toLowerCase().endsWith(".bmp");
        
        /*
         * Fin de la modificación - Matías Cap - Disytel
         */
    }		// isGraphic

    /**
     *      Is attachment entry a PDF
     *      @return true if PDF
     */
    public boolean isPDF() {
        return m_name.toLowerCase().endsWith(".pdf");
    }		// isPDF

    public boolean isText() {
        return m_name.toLowerCase().endsWith(".txt");
    }		// isPDF

    public boolean isRTF() {
        return m_name.toLowerCase().endsWith(".rtf");
    }		// isPDF
    
    public boolean isHTML() {
        return m_name.toLowerCase().endsWith(".html") || m_name.toLowerCase().endsWith(".htm");
    }		// isPDF    
    
    //~--- set methods --------------------------------------------------------

    /**
     * @param data The data to set.
     */
    public void setData(byte[] data) {
        m_data	= data;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {

        if (name != null) {
            m_name	= name;
        }

        if (m_name == null) {
            m_name	= "?";
        }

    }		// setName
}	// MAttachmentItem



/*
 * @(#)MAttachmentEntry.java   02.jul 2007
 * 
 *  Fin del fichero MAttachmentEntry.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
