/*
 * @(#)MArchive.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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
 *      Archive Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MArchive.java,v 1.5 2005/03/11 20:28:33 jjanke Exp $
 */
public class MArchive extends X_AD_Archive {

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MArchive.class);

    /** Descripción de Campo */
    private Integer	m_inflated	= null;

    /** Descripción de Campo */
    private Integer	m_deflated	= null;

    /**
     *      Standard Constructor
     *      @param ctx context
     * @param id
     * @param trxName
     */
    public MArchive(Properties ctx, int id, String trxName) {
        super(ctx, id, trxName);
    }		// MArchive

    /**
     *      Constructor
     *      @param ctx context
     *      @param info print info
     * @param trxName
     */
    public MArchive(Properties ctx, PrintInfo info, String trxName) {

        this(ctx, 0, trxName);
        setName(info.getName());
        setIsReport(info.isReport());

        //
        setAD_Process_ID(info.getAD_Process_ID());
        setAD_Table_ID(info.getAD_Table_ID());
        setRecord_ID(info.getRecord_ID());
        setC_BPartner_ID(info.getC_BPartner_ID());

    }		// MArchive

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MArchive(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MArchive

    /**
     *      Before Save
     *      @param newRecord new
     *      @returntrue if can be saved
     *
     * @return
     */
    protected boolean beforeSave(boolean newRecord) {

        // Binary Data is Mandatory
        byte[]	data	= super.getBinaryData();

        if ((data == null) || (data.length == 0)) {
            return false;
        }

        //
        log.fine(toString());

        return true;
    }		// beforeSave

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MArchive[");

        sb.append(getID()).append(",Name=").append(getName());

        if (m_inflated != null) {
            sb.append(",Inflated=" + m_inflated);
        }

        if (m_deflated != null) {
            sb.append(",Deflated=" + m_deflated);
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Archives
     *      @param ctx context
     *      @param whereClause optional where clause (starting with AND)
     *      @return archives
     */
    public static MArchive[] get(Properties ctx, String whereClause) {

        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;
        String			sql	= "SELECT * FROM AD_Archive WHERE AD_Client_ID=?";

        if ((whereClause != null) && (whereClause.length() > 0)) {
            sql	+= whereClause;
        }

        sql	+= " ORDER BY Created";

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, Env.getAD_Client_ID(ctx));

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MArchive(ctx, rs, null));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        if (list.size() == 0) {
            s_log.fine(sql);
        } else {
            s_log.finer(sql);
        }

        //
        MArchive[]	retValue	= new MArchive[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// get

    /**
     *      Get Binary Data.
     *      (inflate)
     *      @return inflated data
     */
    public byte[] getBinaryData() {

        byte[]	deflatedData	= super.getBinaryData();

        m_deflated	= null;
        m_inflated	= null;

        if (deflatedData == null) {
            return null;
        }

        //
        log.fine("ZipSize=" + deflatedData.length);
        m_deflated	= new Integer(deflatedData.length);

        if (deflatedData.length == 0) {
            return null;
        }

        byte[]	inflatedData	= null;

        try {

            ByteArrayInputStream	in	= new ByteArrayInputStream(deflatedData);
            ZipInputStream		zip	= new ZipInputStream(in);
            ZipEntry			entry	= zip.getNextEntry();

            if (entry != null)		// just one entry
            {

                ByteArrayOutputStream	out	= new ByteArrayOutputStream();
                byte[]			buffer	= new byte[2048];
                int			length	= zip.read(buffer);

                while (length != -1) {

                    out.write(buffer, 0, length);
                    length	= zip.read(buffer);
                }

                //
                inflatedData	= out.toByteArray();
                log.fine("Size=" + inflatedData.length + " - zip=" + entry.getCompressedSize() + "(" + entry.getSize() + ") " + (entry.getCompressedSize() * 100 / entry.getSize()) + "%");
                m_inflated	= new Integer(inflatedData.length);
            }

        } catch (Exception e) {

            log.log(Level.SEVERE, "loadLOBData", e);
            inflatedData	= null;
        }

        return inflatedData;

    }		// getBinaryData

    /**
     *      Get Created By (User) Name
     *      @return name
     */
    public String getCreatedByName() {

        String			name	= "?";
        String			sql	= "SELECT Name FROM AD_User WHERE AD_User_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, getCreatedBy());

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                name	= rs.getString(1);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return name;

    }		// getCreatedByName

    /**
     *      Get Data as Input Stream
     *      @return input stream or null
     */
    public InputStream getInputStream() {

        byte[]	inflatedData	= getBinaryData();

        if (inflatedData == null) {
            return null;
        }

        return new ByteArrayInputStream(inflatedData);

    }		// getInputStream

    //~--- set methods --------------------------------------------------------

    /**
     *      Save Binary Data.
     *      (deflate)
     *
     * @param inflatedData
     */
    public void setBinaryData(byte[] inflatedData) {

        if ((inflatedData == null) || (inflatedData.length == 0)) {
            throw new IllegalArgumentException("InflatedData is NULL");
        }

        m_inflated	= new Integer(inflatedData.length);

        ByteArrayOutputStream	out	= new ByteArrayOutputStream();
        ZipOutputStream		zip	= new ZipOutputStream(out);

        zip.setMethod(ZipOutputStream.DEFLATED);
        zip.setLevel(Deflater.BEST_COMPRESSION);
        zip.setComment("openxpertya");

        //
        byte[]	deflatedData	= null;

        try {

            ZipEntry	entry	= new ZipEntry("OpenxpertyaArchive");

            entry.setTime(System.currentTimeMillis());
            entry.setMethod(ZipEntry.DEFLATED);
            zip.putNextEntry(entry);
            zip.write(inflatedData, 0, inflatedData.length);
            zip.closeEntry();
            log.fine(entry.getCompressedSize() + " (" + entry.getSize() + ") " + (entry.getCompressedSize() * 100 / entry.getSize()) + "%");

            //
            // zip.finish();
            zip.close();
            deflatedData	= out.toByteArray();
            log.fine("Length=" + inflatedData.length);
            m_deflated	= new Integer(deflatedData.length);

        } catch (Exception e) {

            log.log(Level.SEVERE, "saveLOBData", e);
            deflatedData	= null;
            m_deflated		= null;
        }

        super.setBinaryData(deflatedData);

    }		// setBinaryData
}	// MArchive



/*
 * @(#)MArchive.java   02.jul 2007
 * 
 *  Fin del fichero MArchive.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
