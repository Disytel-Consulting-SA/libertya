/*
 * @(#)X_M_Product_Spareparts.java   12.oct 2007  Versión 2.2
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

/** Modelo Generado para M_Product_Spareparts
  *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Comunidad de Desarrollo OpenXpertya (generado)
        *Basado en Diverso Codigo Original Modificado, Revisado y Optimizado de:
        *Copyright ï¿½ 1999-2001 Jorg Janke, Copyright ï¿½ ComPiere, Inc. *  @version Version 1.2 19:41:04.329 */
public class X_M_Product_Spareparts extends PO {

/** AD_Table_ID=1000019 */
    public static final int	Table_ID	= 1000019;

/** TableName=M_Product_Spareparts */
    public static final String	Table_Name	= "M_Product_Spareparts";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000019, "M_Product_Spareparts");

    /** Descripción de Campo */
    public static final int	M_PRODUCTSPAREPARTS_ID_AD_Reference_ID	= 162;

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(7);

/**
 * Constructor Standard
 *
 * @param ctx
 * @param M_Product_Spareparts_ID
 * @param trxName
 */
    public X_M_Product_Spareparts(Properties ctx, int M_Product_Spareparts_ID, String trxName) {

        super(ctx, M_Product_Spareparts_ID, trxName);

/** if (M_Product_Spareparts_ID == 0)
{
setM_Product_ID (0);
setM_Product_Spareparts_ID (0);
setM_Productspareparts_ID (0);
setSparepartsqty (0);
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
    public X_M_Product_Spareparts(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_M_Product_Spareparts[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get Description
 *
 * @return
 */
    public String getDescription() {
        return (String) get_Value("Description");
    }

/**
 * Get M_Product_ID
 *
 * @return
 */
    public int getM_Product_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get M_Product_Spareparts_ID.
 *
 * @return
M_Product_Spareparts_ID */
    public int getM_Product_Spareparts_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_Spareparts_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get M_Productspareparts_ID
 *
 * @return
 */
    public int getM_Productspareparts_ID() {

        Integer	ii	= (Integer) get_Value("M_Productspareparts_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Sparepartsqty
 *
 * @return
 */
    public int getSparepartsqty() {

        Integer	ii	= (Integer) get_Value("Sparepartsqty");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

    //~--- set methods --------------------------------------------------------

/**
 * Set Description
 *
 * @param Description
 */
    public void setDescription(String Description) {

        if ((Description != null) && (Description.length() > 510)) {

            log.warning("Length > 510 - truncated");
            Description	= Description.substring(0, 509);
        }

        set_Value("Description", Description);
    }

/**
 * Set M_Product_ID
 *
 * @param M_Product_ID
 */
    public void setM_Product_ID(int M_Product_ID) {
        set_Value("M_Product_ID", new Integer(M_Product_ID));
    }

/** Set M_Product_Spareparts_ID.
 *
 * @param M_Product_Spareparts_ID
M_Product_Spareparts_ID */
    public void setM_Product_Spareparts_ID(int M_Product_Spareparts_ID) {
        set_Value("M_Product_Spareparts_ID", new Integer(M_Product_Spareparts_ID));
    }

/**
 * Set M_Productspareparts_ID
 *
 * @param M_Productspareparts_ID
 */
    public void setM_Productspareparts_ID(int M_Productspareparts_ID) {
        set_Value("M_Productspareparts_ID", new Integer(M_Productspareparts_ID));
    }

/**
 * Set Sparepartsqty
 *
 * @param Sparepartsqty
 */
    public void setSparepartsqty(int Sparepartsqty) {
        set_Value("Sparepartsqty", new Integer(Sparepartsqty));
    }
}



/*
 * @(#)X_M_Product_Spareparts.java   02.jul 2007
 * 
 *  Fin del fichero X_M_Product_Spareparts.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
