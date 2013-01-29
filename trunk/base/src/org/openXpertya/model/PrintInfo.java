/*
 * @(#)PrintInfo.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.process.ProcessInfo;

/**
 *      Print Info
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: PrintInfo.java,v 1.2 2005/03/11 20:28:36 jjanke Exp $
 */
public class PrintInfo {

    /** Descripción de Campo */
    boolean	m_withDialog	= false;

    /** Descripción de Campo */
    private String	m_printerName	= null;

    /** Descripción de Campo */
    private boolean	m_isDocumentCopy	= false;

    /** Descripción de Campo */
    private int	m_copies	= 1;

    /** Descripción de Campo */
    private int	m_Record_ID	= 0;

    //

    /** Descripción de Campo */
    private String	m_Name	= null;

    /** Descripción de Campo */
    private String	m_Help	= null;

    /** Descripción de Campo */
    private String	m_Description	= null;

    /** Descripción de Campo */
    private int	m_C_BPartner_ID	= 0;

    /** Descripción de Campo */
    private int	m_AD_Table_ID	= 0;

    /** Descripción de Campo */
    private int	m_AD_Process_ID	= 0;

    /**
     *      Process Archive Info
     *      @param pi process info
     */
    public PrintInfo(ProcessInfo pi) {

        setName(pi.getTitle());
        setAD_Process_ID(pi.getAD_Process_ID());
        setAD_Table_ID(pi.getTable_ID());
        setRecord_ID(pi.getRecord_ID());

    }		// PrintInfo

    /**
     *      Report Archive Info
     *      @param Name name
     *      @param AD_Table_ID table
     *      @param Record_ID record
     */
    public PrintInfo(String Name, int AD_Table_ID, int Record_ID) {

        setName(Name);
        setAD_Table_ID(AD_Table_ID);
        setRecord_ID(Record_ID);

    }		// ArchiveInfo

    /**
     *      Document Archive Info
     *      @param Name name
     *      @param AD_Table_ID table
     *      @param Record_ID record
     *      @param C_BPartner_ID bpartner
     */
    public PrintInfo(String Name, int AD_Table_ID, int Record_ID, int C_BPartner_ID) {

        setName(Name);
        setAD_Table_ID(AD_Table_ID);
        setRecord_ID(Record_ID);
        setC_BPartner_ID(C_BPartner_ID);

    }		// ArchiveInfo

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("PrintInfo[");

        sb.append(getName());

        if (getAD_Process_ID() != 0) {
            sb.append(",AD_Process_ID=").append(getAD_Process_ID());
        }

        if (getAD_Table_ID() != 0) {
            sb.append(",AD_Table_ID=").append(getAD_Table_ID());
        }

        if (getRecord_ID() != 0) {
            sb.append(",Record_ID=").append(getRecord_ID());
        }

        if (getC_BPartner_ID() != 0) {
            sb.append(",C_BPartner_ID=").append(getC_BPartner_ID());
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     * @return Returns the aD_Process_ID.
     */
    public int getAD_Process_ID() {
        return m_AD_Process_ID;
    }

    /**
     * @return Returns the aD_Table_ID.
     */
    public int getAD_Table_ID() {
        return m_AD_Table_ID;
    }

    /**
     * @return Returns the c_BPartner_ID.
     */
    public int getC_BPartner_ID() {
        return m_C_BPartner_ID;
    }

    /**
     * @return Returns the copies.
     */
    public int getCopies() {
        return m_copies;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return m_Description;
    }

    /**
     * @return Returns the help.
     */
    public String getHelp() {
        return m_Help;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {

        if ((m_Name == null) || (m_Name.length() == 0)) {
            return "Unknown";
        }

        return m_Name;
    }

    /**
     * @return Returns the printerName.
     */
    public String getPrinterName() {
        return m_printerName;
    }

    /**
     * @return Returns the record_ID.
     */
    public int getRecord_ID() {
        return m_Record_ID;
    }

    /**
     *      Is this a Document
     *      @return true if BPartner defined
     */
    public boolean isDocument() {
        return m_C_BPartner_ID != 0;
    }		// isDocument

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isDocumentCopy() {
        return m_isDocumentCopy;
    }		// isDocument

    /**
     *      Is this a Report
     *      @return
     */
    public boolean isReport() {

        return (m_AD_Process_ID != 0	// Menu Report
            ) || (m_C_BPartner_ID == 0);

    }					// isReport

    /**
     * @return Returns the withDialog.
     */
    public boolean isWithDialog() {
        return m_withDialog;
    }

    //~--- set methods --------------------------------------------------------

    /**
     * @param process_ID The aD_Process_ID to set.
     */
    public void setAD_Process_ID(int process_ID) {
        m_AD_Process_ID	= process_ID;
    }

    /**
     * @param table_ID The aD_Table_ID to set.
     */
    public void setAD_Table_ID(int table_ID) {
        m_AD_Table_ID	= table_ID;
    }

    /**
     * @param partner_ID The c_BPartner_ID to set.
     */
    public void setC_BPartner_ID(int partner_ID) {
        m_C_BPartner_ID	= partner_ID;
    }

    /**
     * @param copies The copies to set.
     */
    public void setCopies(int copies) {
        m_copies	= copies;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        m_Description	= description;
    }

    /**
     *
     * @param isDocumentCopy
     */
    public void setDocumentCopy(boolean isDocumentCopy) {
        m_isDocumentCopy	= isDocumentCopy;
    }

    /**
     * @param help The help to set.
     */
    public void setHelp(String help) {
        m_Help	= help;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        m_Name	= name;
    }

    /**
     * @param printerName The printerName to set.
     */
    public void setPrinterName(String printerName) {
        m_printerName	= printerName;
    }

    /**
     * @param record_ID The record_ID to set.
     */
    public void setRecord_ID(int record_ID) {
        m_Record_ID	= record_ID;
    }

    /**
     * @param withDialog The withDialog to set.
     */
    public void setWithDialog(boolean withDialog) {
        m_withDialog	= withDialog;
    }
}	// ArchiveInfo



/*
 * @(#)PrintInfo.java   02.jul 2007
 * 
 *  Fin del fichero PrintInfo.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
