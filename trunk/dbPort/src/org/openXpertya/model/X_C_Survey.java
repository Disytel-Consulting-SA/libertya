/*
 * @(#)X_C_Survey.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.KeyNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.Properties;

/** Generated Model for C_Survey
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-06-29 11:58:41.515 */
public class X_C_Survey extends PO {

/** AD_Table_ID=1000097 */
    public static final int	Table_ID	= 1000097;

/** TableName=C_Survey */
    public static final String	Table_Name	= "C_Survey";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000097, "C_Survey");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_Survey_ID
 * @param trxName
 */
    public X_C_Survey(Properties ctx, int C_Survey_ID, String trxName) {

        super(ctx, C_Survey_ID, trxName);

/** if (C_Survey_ID == 0)
{
setActive_From (new Timestamp(System.currentTimeMillis()));
setActive_To (new Timestamp(System.currentTimeMillis()));
setC_Survey_ID (0);
setIsVisible (false);
setName (null);
}
 */
    }

/**
 * Load Constructor
 *
 * @param ctx
 * @param rs
 * @param trxName
 */
    public X_C_Survey(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

/**
 * Load Meta Data
 *
 * @param ctx
 *
 * @return
 */
    protected POInfo initPO(Properties ctx) {

        POInfo	poi	= POInfo.getPOInfo(ctx, Table_ID);

        return poi;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("X_C_Survey[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get Active From
 *
 * @return
 */
    public Timestamp getActive_From() {
        return (Timestamp) get_Value("Active_From");
    }

/**
 * Get Active To
 *
 * @return
 */
    public Timestamp getActive_To() {
        return (Timestamp) get_Value("Active_To");
    }

/**
 * Get C_Survey_ID
 *
 * @return
 */
    public int getC_Survey_ID() {

        Integer	ii	= (Integer) get_Value("C_Survey_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(getID(), getName());
    }

/** Get Name.
 *
 * @return
Alphanumeric identifier of the entity */
    public String getName() {
        return (String) get_Value("Name");
    }

/** Get URL.
 *
 * @return
URL */
    public String getURL() {
        return (String) get_Value("URL");
    }

/**
 * Get Is Visible
 *
 * @return
 */
    public boolean isVisible() {

        Object	oo	= get_Value("IsVisible");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/**
 * Set Active From
 *
 * @param Active_From
 */
    public void setActive_From(Timestamp Active_From) {

        if (Active_From == null) {
            throw new IllegalArgumentException("Active_From is mandatory");
        }

        set_Value("Active_From", Active_From);
    }

/**
 * Set Active To
 *
 * @param Active_To
 */
    public void setActive_To(Timestamp Active_To) {

        if (Active_To == null) {
            throw new IllegalArgumentException("Active_To is mandatory");
        }

        set_Value("Active_To", Active_To);
    }

/**
 * Set C_Survey_ID
 *
 * @param C_Survey_ID
 */
    public void setC_Survey_ID(int C_Survey_ID) {
        set_ValueNoCheck("C_Survey_ID", new Integer(C_Survey_ID));
    }

/**
 * Set Is Visible
 *
 * @param IsVisible
 */
    public void setIsVisible(boolean IsVisible) {
        set_Value("IsVisible", new Boolean(IsVisible));
    }

/** Set Name.
 *
 * @param Name
Alphanumeric identifier of the entity */
    public void setName(String Name) {

        if (Name == null) {
            throw new IllegalArgumentException("Name is mandatory");
        }

        if (Name.length() > 60) {

            log.warning("Length > 60 - truncated");
            Name	= Name.substring(0, 59);
        }

        set_Value("Name", Name);
    }

/** Set URL.
 *
 * @param URL
URL */
    public void setURL(String URL) {

        if ((URL != null) && (URL.length() > 255)) {

            log.warning("Length > 255 - truncated");
            URL	= URL.substring(0, 254);
        }

        set_Value("URL", URL);
    }
}



/*
 * @(#)X_C_Survey.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Survey.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
