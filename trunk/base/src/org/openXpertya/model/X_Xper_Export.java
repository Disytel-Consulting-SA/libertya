/*
 * @(#)X_Xper_Export.java   12.oct 2007  Versión 2.2
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

import java.util.Properties;

/** Modelo Generado para Xper_Export
  *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Comunidad de Desarrollo OpenXpertya (generado)
        *Basado en Diverso Codigo Original Modificado, Revisado y Optimizado de:
        *Copyright ï¿½ 1999-2001 Jorg Janke, Copyright ï¿½ ComPiere, Inc. *  @version Version 1.2 19:41:04.409 */
public class X_Xper_Export extends PO {

/** AD_Table_ID=1000021 */
    public static final int	Table_ID	= 1000021;

/** TableName=Xper_Export */
    public static final String	Table_Name	= "Xper_Export";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000021, "Xper_Export");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(7);

/**
 * Constructor Standard
 *
 * @param ctx
 * @param Xper_Export_ID
 * @param trxName
 */
    public X_Xper_Export(Properties ctx, int Xper_Export_ID, String trxName) {

        super(ctx, Xper_Export_ID, trxName);

/** if (Xper_Export_ID == 0)
{
setName (null);
setXPper_Export_ID (0);
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
    public X_Xper_Export(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_Xper_Export[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get Description.
 *
 * @return
Description */
    public String getDescription() {
        return (String) get_Value("Description");
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
Name */
    public String getName() {
        return (String) get_Value("Name");
    }

/**
 * Get XPper_Export_ID
 *
 * @return
 */
    public int getXPper_Export_ID() {

        Integer	ii	= (Integer) get_Value("XPper_Export_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Process
 *
 * @return
 */
    public boolean isProcess() {

        Object	oo	= get_Value("Process");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/** Set Description.
 *
 * @param Description
Description */
    public void setDescription(String Description) {

        if ((Description != null) && (Description.length() > 510)) {

            log.warning("Length > 510 - truncated");
            Description	= Description.substring(0, 509);
        }

        set_Value("Description", Description);
    }

/** Set Name.
 *
 * @param Name
Name */
    public void setName(String Name) {

        if (Name == null) {
            throw new IllegalArgumentException("Name is mandatory");
        }

        if (Name.length() > 240) {

            log.warning("Length > 240 - truncated");
            Name	= Name.substring(0, 239);
        }

        set_Value("Name", Name);
    }

/**
 * Set Process
 *
 * @param Process
 */
    public void setProcess(boolean Process) {
        set_Value("Process", new Boolean(Process));
    }

/**
 * Set XPper_Export_ID
 *
 * @param XPper_Export_ID
 */
    public void setXPper_Export_ID(int XPper_Export_ID) {
        set_Value("XPper_Export_ID", new Integer(XPper_Export_ID));
    }
}



/*
 * @(#)X_Xper_Export.java   02.jul 2007
 * 
 *  Fin del fichero X_Xper_Export.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
