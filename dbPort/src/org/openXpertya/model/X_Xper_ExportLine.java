/*
 * @(#)X_Xper_ExportLine.java   12.oct 2007  Versión 2.2
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

/** Modelo Generado para Xper_ExportLine
  *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Comunidad de Desarrollo OpenXpertya (generado)
        *Basado en Diverso Codigo Original Modificado, Revisado y Optimizado de:
        *Copyright ï¿½ 1999-2001 Jorg Janke, Copyright ï¿½ ComPiere, Inc. *  @version Version 1.2 19:41:04.429 */
public class X_Xper_ExportLine extends PO {

/** AD_Table_ID=1000020 */
    public static final int	Table_ID	= 1000020;

/** TableName=Xper_ExportLine */
    public static final String	Table_Name	= "Xper_ExportLine";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000020, "Xper_ExportLine");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(7);

/**
 * Constructor Standard
 *
 * @param ctx
 * @param Xper_ExportLine_ID
 * @param trxName
 */
    public X_Xper_ExportLine(Properties ctx, int Xper_ExportLine_ID, String trxName) {

        super(ctx, Xper_ExportLine_ID, trxName);

/** if (Xper_ExportLine_ID == 0)
{
setAD_Table_ID (0);
setXPper_Export_ID (0);
setXper_Exportline_ID (0);
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
    public X_Xper_ExportLine(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_Xper_ExportLine[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get AD_Table_ID
 *
 * @return
 */
    public int getAD_Table_ID() {

        Integer	ii	= (Integer) get_Value("AD_Table_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
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
 * Get Xper_Exportline_ID
 *
 * @return
 */
    public int getXper_Exportline_ID() {

        Integer	ii	= (Integer) get_Value("Xper_Exportline_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

    //~--- set methods --------------------------------------------------------

/**
 * Set AD_Table_ID
 *
 * @param AD_Table_ID
 */
    public void setAD_Table_ID(int AD_Table_ID) {
        set_Value("AD_Table_ID", new Integer(AD_Table_ID));
    }

/**
 * Set XPper_Export_ID
 *
 * @param XPper_Export_ID
 */
    public void setXPper_Export_ID(int XPper_Export_ID) {
        set_Value("XPper_Export_ID", new Integer(XPper_Export_ID));
    }

/**
 * Set Xper_Exportline_ID
 *
 * @param Xper_Exportline_ID
 */
    public void setXper_Exportline_ID(int Xper_Exportline_ID) {
        set_Value("Xper_Exportline_ID", new Integer(Xper_Exportline_ID));
    }
}



/*
 * @(#)X_Xper_ExportLine.java   02.jul 2007
 * 
 *  Fin del fichero X_Xper_ExportLine.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
