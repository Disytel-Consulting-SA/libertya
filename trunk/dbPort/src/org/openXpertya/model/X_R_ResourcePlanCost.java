/*
 * @(#)X_R_ResourcePlanCost.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.ResultSet;

import java.util.Properties;

/** Generated Model for R_ResourcePlanCost
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke (generated)
 *  @version Release 2.5.2c - 2005-04-24 15:24:16.393 */
public class X_R_ResourcePlanCost extends PO {

/** AD_Table_ID=582 */
    public static final int	Table_ID	= 582;

/** TableName=R_ResourcePlanCost */
    public static final String	Table_Name	= "R_ResourcePlanCost";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(582, "R_ResourcePlanCost");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param R_ResourcePlanCost_ID
 * @param trxName
 */
    public X_R_ResourcePlanCost(Properties ctx, int R_ResourcePlanCost_ID, String trxName) {

        super(ctx, R_ResourcePlanCost_ID, trxName);

/** if (R_ResourcePlanCost_ID == 0)
{
setC_AcctSchema_ID (0);
setC_CostType_ID (0);
setPlanCost (Env.ZERO);
setR_ResourcePlanCost_ID (0);
setS_Resource_ID (0);
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
    public X_R_ResourcePlanCost(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_R_ResourcePlanCost[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get Accounting Schema.
 *
 * @return
Rules for accounting */
    public int getC_AcctSchema_ID() {

        Integer	ii	= (Integer) get_Value("C_AcctSchema_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

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
        return new KeyNamePair(getID(), String.valueOf(getS_Resource_ID()));
    }

/** Get Plan Cost.
 *
 * @return
Planned Costs */
    public BigDecimal getPlanCost() {

        BigDecimal	bd	= (BigDecimal) get_Value("PlanCost");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Resource Plan Cost.
 *
 * @return
Planned Costs of the Resource */
    public int getR_ResourcePlanCost_ID() {

        Integer	ii	= (Integer) get_Value("R_ResourcePlanCost_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Resource.
 *
 * @return
Resource */
    public int getS_Resource_ID() {

        Integer	ii	= (Integer) get_Value("S_Resource_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

    //~--- set methods --------------------------------------------------------

/** Set Accounting Schema.
 *
 * @param C_AcctSchema_ID
Rules for accounting */
    public void setC_AcctSchema_ID(int C_AcctSchema_ID) {
        set_Value("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
    }

/** Set Cost Type.
 *
 * @param C_CostType_ID
Type of Cost */
    public void setC_CostType_ID(int C_CostType_ID) {
        set_Value("C_CostType_ID", new Integer(C_CostType_ID));
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

/** Set Plan Cost.
 *
 * @param PlanCost
Planned Costs */
    public void setPlanCost(BigDecimal PlanCost) {

        if (PlanCost == null) {
            throw new IllegalArgumentException("PlanCost is mandatory");
        }

        set_Value("PlanCost", PlanCost);
    }

/** Set Resource Plan Cost.
 *
 * @param R_ResourcePlanCost_ID
Planned Costs of the Resource */
    public void setR_ResourcePlanCost_ID(int R_ResourcePlanCost_ID) {
        set_ValueNoCheck("R_ResourcePlanCost_ID", new Integer(R_ResourcePlanCost_ID));
    }

/** Set Resource.
 *
 * @param S_Resource_ID
Resource */
    public void setS_Resource_ID(int S_Resource_ID) {
        set_ValueNoCheck("S_Resource_ID", new Integer(S_Resource_ID));
    }
}



/*
 * @(#)X_R_ResourcePlanCost.java   02.jul 2007
 * 
 *  Fin del fichero X_R_ResourcePlanCost.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
