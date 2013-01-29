/*
 * @(#)X_AD_Trace.java   12.oct 2007  Versión 2.2
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

/** Generated Model for AD_Trace
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke (generated)
 *  @version Release 2.5.2 - 2005-02-15 23:00:46.89 */
public class X_AD_Trace extends PO {

/** AD_Table_ID=494 */
    public static final int	Table_ID	= 494;

/** TableName=AD_Trace */
    public static final String	Table_Name	= "AD_Trace";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(494, "AD_Trace");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(4);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param AD_Trace_ID
 * @param trxName
 */
    public X_AD_Trace(Properties ctx, int AD_Trace_ID, String trxName) {

        super(ctx, AD_Trace_ID, trxName);

/** if (AD_Trace_ID == 0)
{
setNo (0);
setWhat (null);
setWhen (new Timestamp(System.currentTimeMillis()));
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
    public X_AD_Trace(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_AD_Trace[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(getID(), String.valueOf(getNo()));
    }

/**
 * Get No
 *
 * @return
 */
    public int getNo() {

        Integer	ii	= (Integer) get_Value("No");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get What
 *
 * @return
 */
    public String getWhat() {
        return (String) get_Value("What");
    }

/**
 * Get When
 *
 * @return
 */
    public Timestamp getWhen() {
        return (Timestamp) get_Value("When");
    }

    //~--- set methods --------------------------------------------------------

/**
 * Set No
 *
 * @param No
 */
    public void setNo(int No) {
        set_Value("No", new Integer(No));
    }

/**
 * Set What
 *
 * @param What
 */
    public void setWhat(String What) {

        if (What == null) {
            throw new IllegalArgumentException("What is mandatory");
        }

        if (What.length() > 2000) {

            log.log(java.util.logging.Level.WARNING, "setWhat - length > 2000 - truncated");
            What	= What.substring(0, 1999);
        }

        set_Value("What", What);
    }

/**
 * Set When
 *
 * @param When
 */
    public void setWhen(Timestamp When) {

        if (When == null) {
            throw new IllegalArgumentException("When is mandatory");
        }

        set_Value("When", When);
    }
}



/*
 * @(#)X_AD_Trace.java   02.jul 2007
 * 
 *  Fin del fichero X_AD_Trace.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
