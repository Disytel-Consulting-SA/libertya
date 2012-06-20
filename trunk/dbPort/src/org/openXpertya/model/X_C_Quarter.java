/*
 * @(#)X_C_Quarter.java   12.oct 2007  Versión 2.2
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
import java.sql.Timestamp;

import java.util.Properties;

/** Modelo Generado para C_Quarter
  *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Comunidad de Desarrollo OpenXpertya (generado)
        *Basado en Diverso Codigo Original Modificado, Revisado y Optimizado de:
        *Copyright ï¿½ 1999-2001 Jorg Janke, Copyright ï¿½ ComPiere, Inc. *  @version Version 1.2 19:41:04.11 */
public class X_C_Quarter extends PO {

/** AD_Table_ID=1000003 */
    public static final int	Table_ID	= 1000114;

/** TableName=C_Quarter */
    public static final String	Table_Name	= "C_Quarter";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000114, "C_Quarter");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(7);

/**
 * Constructor Standard
 *
 * @param ctx
 * @param C_Quarter_ID
 * @param trxName
 */
    public X_C_Quarter(Properties ctx, int C_Quarter_ID, String trxName) {

        super(ctx, C_Quarter_ID, trxName);

/** if (C_Quarter_ID == 0)
{
setC_Quarter_ID (0);
setDistribution (Env.ZERO);
setIncomings (0);
setIsFinal (false);
setIsInitial (false);
setM_Product_ID (0);
setOutGoings (Env.ZERO);
setStock (0);
setStockTime (Env.ZERO);
setTam12 (Env.ZERO);
setTam3 (Env.ZERO);
setYear (0);
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
    public X_C_Quarter(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_Quarter[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get BeginDate
 *
 * @return
 */
    public Timestamp getBeginDate() {
        return (Timestamp) get_Value("BeginDate");
    }

/** Get C_Quarter_Category_ID.
 *
 * @return
C_Quarter_Category_ID */
    public int getC_Quarter_Category_ID() {

        Integer	ii	= (Integer) get_Value("C_Quarter_Category_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get C_Quarter_ID.
 *
 * @return
C_Quarter_ID */
    public int getC_Quarter_ID() {

        Integer	ii	= (Integer) get_Value("C_Quarter_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get C_Quarter Procesing.
 *
 * @return
C_Quarter Procesing */
    public String getCopyFrom() {
        return (String) get_Value("CopyFrom");
    }

/** Get Distribution.
 *
 * @return
Distribution */
    public BigDecimal getDistribution() {

        BigDecimal	bd	= (BigDecimal) get_Value("Distribution");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get EndDate.
 *
 * @return
EndDate */
    public Timestamp getEndDate() {
        return (Timestamp) get_Value("EndDate");
    }

/**
 * Get Incomings
 *
 * @return
 */
    public int getIncomings() {

        Integer	ii	= (Integer) get_Value("Incomings");

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
        return new KeyNamePair(getID(), String.valueOf(getC_Quarter_ID()));
    }

/** Get M_Product_ID.
 *
 * @return
M_Product_ID */
    public int getM_Product_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Name
 *
 * @return
 */
    public String getName() {
        return (String) get_Value("Name");
    }

/**
 * Get OutGoings
 *
 * @return
 */
    public BigDecimal getOutGoings() {

        BigDecimal	bd	= (BigDecimal) get_Value("OutGoings");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Replenish.
 *
 * @return
Replenish */
    public String getReplenish() {
        return (String) get_Value("Replenish");
    }

/**
 * Get Stock
 *
 * @return
 */
    public int getStock() {

        Integer	ii	= (Integer) get_Value("Stock");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get StockTime.
 *
 * @return
StockTime */
    public BigDecimal getStockTime() {

        BigDecimal	bd	= (BigDecimal) get_Value("StockTime");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Tam12.
 *
 * @return
Tam12 */
    public BigDecimal getTam12() {

        BigDecimal	bd	= (BigDecimal) get_Value("Tam12");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Tam3.
 *
 * @return
Tam3 */
    public BigDecimal getTam3() {

        BigDecimal	bd	= (BigDecimal) get_Value("Tam3");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Year.
 *
 * @return
Year */
    public int getYear() {

        Integer	ii	= (Integer) get_Value("Year");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get IsFinal.
 *
 * @return
IsFinal */
    public boolean isFinal() {

        Object	oo	= get_Value("IsFinal");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/** Get IsInitial.
 *
 * @return
IsInitial */
    public boolean isInitial() {

        Object	oo	= get_Value("IsInitial");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/** Get Processing.
 *
 * @return
Processing */
    public boolean isProcessing() {

        Object	oo	= get_Value("Processing");

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
 * Set BeginDate
 *
 * @param BeginDate
 */
    public void setBeginDate(Timestamp BeginDate) {
        set_Value("BeginDate", BeginDate);
    }

/** Set C_Quarter_Category_ID.
 *
 * @param C_Quarter_Category_ID
C_Quarter_Category_ID */
    public void setC_Quarter_Category_ID(int C_Quarter_Category_ID) {

        if (C_Quarter_Category_ID <= 0) {
            set_Value("C_Quarter_Category_ID", null);
        } else {
            set_Value("C_Quarter_Category_ID", new Integer(C_Quarter_Category_ID));
        }
    }

/** Set C_Quarter_ID.
 *
 * @param C_Quarter_ID
C_Quarter_ID */
    public void setC_Quarter_ID(int C_Quarter_ID) {
        set_Value("C_Quarter_ID", new Integer(C_Quarter_ID));
    }

/** Set C_Quarter Procesing.
 *
 * @param CopyFrom
C_Quarter Procesing */
    public void setCopyFrom(String CopyFrom) {

        if ((CopyFrom != null) && (CopyFrom.length() > 1)) {

            log.warning("Length > 1 - truncated");
            CopyFrom	= CopyFrom.substring(0, 0);
        }

        set_Value("CopyFrom", CopyFrom);
    }

/** Set Distribution.
 *
 * @param Distribution
Distribution */
    public void setDistribution(BigDecimal Distribution) {

        if (Distribution == null) {
            throw new IllegalArgumentException("Distribution is mandatory");
        }

        set_Value("Distribution", Distribution);
    }

/** Set EndDate.
 *
 * @param EndDate
EndDate */
    public void setEndDate(Timestamp EndDate) {
        set_Value("EndDate", EndDate);
    }

/**
 * Set Incomings
 *
 * @param Incomings
 */
    public void setIncomings(int Incomings) {
        set_Value("Incomings", new Integer(Incomings));
    }

/** Set IsFinal.
 *
 * @param IsFinal
IsFinal */
    public void setIsFinal(boolean IsFinal) {
        set_Value("IsFinal", new Boolean(IsFinal));
    }

/** Set IsInitial.
 *
 * @param IsInitial
IsInitial */
    public void setIsInitial(boolean IsInitial) {
        set_Value("IsInitial", new Boolean(IsInitial));
    }

/** Set M_Product_ID.
 *
 * @param M_Product_ID
M_Product_ID */
    public void setM_Product_ID(int M_Product_ID) {
        set_Value("M_Product_ID", new Integer(M_Product_ID));
    }

/**
 * Set Name
 *
 * @param Name
 */
    public void setName(String Name) {

        if ((Name != null) && (Name.length() > 510)) {

            log.warning("Length > 510 - truncated");
            Name	= Name.substring(0, 509);
        }

        set_Value("Name", Name);
    }

/**
 * Set OutGoings
 *
 * @param OutGoings
 */
    public void setOutGoings(BigDecimal OutGoings) {

        if (OutGoings == null) {
            throw new IllegalArgumentException("OutGoings is mandatory");
        }

        set_Value("OutGoings", OutGoings);
    }

/** Set Processing.
 *
 * @param Processing
Processing */
    public void setProcessing(boolean Processing) {
        set_Value("Processing", new Boolean(Processing));
    }

/** Set Replenish.
 *
 * @param Replenish
Replenish */
    public void setReplenish(String Replenish) {

        if ((Replenish != null) && (Replenish.length() > 1)) {

            log.warning("Length > 1 - truncated");
            Replenish	= Replenish.substring(0, 0);
        }

        set_Value("Replenish", Replenish);
    }

/**
 * Set Stock
 *
 * @param Stock
 */
    public void setStock(int Stock) {
        set_Value("Stock", new Integer(Stock));
    }

/** Set StockTime.
 *
 * @param StockTime
StockTime */
    public void setStockTime(BigDecimal StockTime) {

        if (StockTime == null) {
            throw new IllegalArgumentException("StockTime is mandatory");
        }

        set_Value("StockTime", StockTime);
    }

/** Set Tam12.
 *
 * @param Tam12
Tam12 */
    public void setTam12(BigDecimal Tam12) {

        if (Tam12 == null) {
            throw new IllegalArgumentException("Tam12 is mandatory");
        }

        set_Value("Tam12", Tam12);
    }

/** Set Tam3.
 *
 * @param Tam3
Tam3 */
    public void setTam3(BigDecimal Tam3) {

        if (Tam3 == null) {
            throw new IllegalArgumentException("Tam3 is mandatory");
        }

        set_Value("Tam3", Tam3);
    }

/** Set Year.
 *
 * @param Year
Year */
    public void setYear(int Year) {
        set_Value("Year", new Integer(Year));
    }
}



/*
 * @(#)X_C_Quarter.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Quarter.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
