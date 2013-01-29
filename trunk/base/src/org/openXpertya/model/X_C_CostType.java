/*
 * @(#)X_C_CostType.java   12.oct 2007  Versión 2.2
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

/** Generated Model for C_CostType
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke (generated)
 *  @version Release 2.5.2c - 2005-04-24 15:24:12.267 */
public class X_C_CostType extends PO {

/** AD_Table_ID=586 */
    public static final int	Table_ID	= 586;

/** TableName=C_CostType */
    public static final String	Table_Name	= "C_CostType";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(586, "C_CostType");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_CostType_ID
 * @param trxName
 */
    public X_C_CostType(Properties ctx, int C_CostType_ID, String trxName) {

        super(ctx, C_CostType_ID, trxName);

/** if (C_CostType_ID == 0)
{
setC_CostType_ID (0);
setIsDirectCost (false);
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
    public X_C_CostType(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_CostType[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get Cost Type.
 *
 * @return
Type of Cost */
    public int getC_CostType_ID() {

        Integer	ii	= (Integer) get_Value("C_CostType_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Description.
 *
 * @return
Optional short description of the record */
    public String getDescription() {
        return (String) get_Value("Description");
    }

/** Get Comment/Help.
 *
 * @return
Comment or Hint */
    public String getHelp() {
        return (String) get_Value("Help");
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

/** Get Direct Cost.
 *
 * @return
This are direct costs (no overhead) */
    public boolean isDirectCost() {

        Object	oo	= get_Value("IsDirectCost");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/** Set Cost Type.
 *
 * @param C_CostType_ID
Type of Cost */
    public void setC_CostType_ID(int C_CostType_ID) {
        set_ValueNoCheck("C_CostType_ID", new Integer(C_CostType_ID));
    }

/** Set Description.
 *
 * @param Description
Optional short description of the record */
    public void setDescription(String Description) {

        if ((Description != null) && (Description.length() > 255)) {

            log.log(java.util.logging.Level.WARNING, "length > 255 - truncated");
            Description	= Description.substring(0, 254);
        }

        set_Value("Description", Description);
    }

/** Set Comment/Help.
 *
 * @param Help
Comment or Hint */
    public void setHelp(String Help) {

        if ((Help != null) && (Help.length() > 2000)) {

            log.log(java.util.logging.Level.WARNING, "length > 2000 - truncated");
            Help	= Help.substring(0, 1999);
        }

        set_Value("Help", Help);
    }

/** Set Direct Cost.
 *
 * @param IsDirectCost
This are direct costs (no overhead) */
    public void setIsDirectCost(boolean IsDirectCost) {
        set_Value("IsDirectCost", new Boolean(IsDirectCost));
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

            log.log(java.util.logging.Level.WARNING, "length > 60 - truncated");
            Name	= Name.substring(0, 59);
        }

        set_Value("Name", Name);
    }
}



/*
 * @(#)X_C_CostType.java   02.jul 2007
 * 
 *  Fin del fichero X_C_CostType.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
