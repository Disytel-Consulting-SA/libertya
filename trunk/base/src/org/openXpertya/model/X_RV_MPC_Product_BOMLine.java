/*
 * @(#)X_RV_MPC_Product_BOMLine.java   12.oct 2007  Versión 2.2
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

/** Generated Model for RV_MPC_Product_BOMLine
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke (generated)
 *  @version Release 2.5.2d - 2005-06-09 21:09:42.923 */
public class X_RV_MPC_Product_BOMLine extends PO {

/** AD_Table_ID=1000073 */
    public static final int	Table_ID	= 1000073;

/** TableName=RV_MPC_Product_BOMLine */
    public static final String	Table_Name	= "RV_MPC_Product_BOMLine";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000073, "RV_MPC_Product_BOMLine");

/** Issue = 0 */
    public static final String	ISSUEMETHOD_Issue	= "0";

/** BackFlush = 1 */
    public static final String	ISSUEMETHOD_BackFlush	= "1";

    /** Descripción de Campo */
    public static final int	ISSUEMETHOD_AD_Reference_ID	= 1000002;

/** Tools = TL */
    public static final String	COMPONENTTYPE_Tools	= "TL";

/** Planning = PL */
    public static final String	COMPONENTTYPE_Planning	= "PL";

/** Phantom = PH */
    public static final String	COMPONENTTYPE_Phantom	= "PH";

/** Packing = PK */
    public static final String	COMPONENTTYPE_Packing	= "PK";

/** Component = CO */
    public static final String	COMPONENTTYPE_Component	= "CO";

/** By Product = BY */
    public static final String	COMPONENTTYPE_ByProduct	= "BY";

    /** Descripción de Campo */
    public static final int	COMPONENTTYPE_AD_Reference_ID	= 1000037;

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(7);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param RV_MPC_Product_BOMLine_ID
 * @param trxName
 */
    public X_RV_MPC_Product_BOMLine(Properties ctx, int RV_MPC_Product_BOMLine_ID, String trxName) {

        super(ctx, RV_MPC_Product_BOMLine_ID, trxName);

/** if (RV_MPC_Product_BOMLine_ID == 0)
{
setLine (0);
setQtyBatch (Env.ZERO);
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
    public X_RV_MPC_Product_BOMLine(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_RV_MPC_Product_BOMLine[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get Process Instance.
 *
 * @return
Instance of the process */
    public int getAD_PInstance_ID() {

        Integer	ii	= (Integer) get_Value("AD_PInstance_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get UOM.
 *
 * @return
Unit of Measure */
    public int getC_UOM_ID() {

        Integer	ii	= (Integer) get_Value("C_UOM_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Component Type
 *
 * @return
 */
    public String getComponentType() {
        return (String) get_Value("ComponentType");
    }

/** Get Description.
 *
 * @return
Optional short description of the record */
    public String getDescription() {
        return (String) get_Value("Description");
    }

/**
 * Get IssueMethod
 *
 * @return
 */
    public String getIssueMethod() {
        return (String) get_Value("IssueMethod");
    }

/**
 * Get Level no
 *
 * @return
 */
    public int getLevelNo() {

        Integer	ii	= (Integer) get_Value("LevelNo");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Levels
 *
 * @return
 */
    public String getLevels() {
        return (String) get_Value("Levels");
    }

/** Get Line No.
 *
 * @return
Unique line for this document */
    public int getLine() {

        Integer	ii	= (Integer) get_Value("Line");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get MPC_Product_BOMLine_ID
 *
 * @return
 */
    public int getMPC_Product_BOMLine_ID() {

        Integer	ii	= (Integer) get_Value("MPC_Product_BOMLine_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get BOM & Formula
 *
 * @return
 */
    public int getMPC_Product_BOM_ID() {

        Integer	ii	= (Integer) get_Value("MPC_Product_BOM_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Attribute Set Instance.
 *
 * @return
Product Attribute Set Instance */
    public int getM_AttributeSetInstance_ID() {

        Integer	ii	= (Integer) get_Value("M_AttributeSetInstance_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Product.
 *
 * @return
Product, Service, Item */
    public int getM_Product_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Qty.
 *
 * @return
Bill of Materials Quantity */
    public BigDecimal getQtyBOM() {

        BigDecimal	bd	= (BigDecimal) get_Value("QtyBOM");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get Qty %
 *
 * @return
 */
    public BigDecimal getQtyBatch() {

        BigDecimal	bd	= (BigDecimal) get_Value("QtyBatch");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get Scrap
 *
 * @return
 */
    public BigDecimal getScrap() {

        BigDecimal	bd	= (BigDecimal) get_Value("Scrap");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Sequence.
Method of ordering records;
 *
 * @return
 lowest number comes first */
    public int getSeqNo() {

        Integer	ii	= (Integer) get_Value("SeqNo");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Valid from.
 *
 * @return
Valid from including this date (first day) */
    public Timestamp getValidFrom() {
        return (Timestamp) get_Value("ValidFrom");
    }

/** Get Valid to.
 *
 * @return
Valid to including this date (last day) */
    public Timestamp getValidTo() {
        return (Timestamp) get_Value("ValidTo");
    }

/**
 * Get IsCritical
 *
 * @return
 */
    public boolean isCritical() {

        Object	oo	= get_Value("IsCritical");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/**
 * Get IsQtyPercentage
 *
 * @return
 */
    public boolean isQtyPercentage() {

        Object	oo	= get_Value("IsQtyPercentage");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/** Set Process Instance.
 *
 * @param AD_PInstance_ID
Instance of the process */
    public void setAD_PInstance_ID(int AD_PInstance_ID) {

        if (AD_PInstance_ID <= 0) {
            set_Value("AD_PInstance_ID", null);
        } else {
            set_Value("AD_PInstance_ID", new Integer(AD_PInstance_ID));
        }
    }

/** Set UOM.
 *
 * @param C_UOM_ID
Unit of Measure */
    public void setC_UOM_ID(int C_UOM_ID) {

        if (C_UOM_ID <= 0) {
            set_Value("C_UOM_ID", null);
        } else {
            set_Value("C_UOM_ID", new Integer(C_UOM_ID));
        }
    }

/**
 * Set Component Type
 *
 * @param ComponentType
 */
    public void setComponentType(String ComponentType) {

        if ((ComponentType == null) || ComponentType.equals("BY") || ComponentType.equals("CO") || ComponentType.equals("PH") || ComponentType.equals("PK") || ComponentType.equals("PL") || ComponentType.equals("TL")) {
            ;
        } else {
            throw new IllegalArgumentException("ComponentType Invalid value - Reference_ID=1000037 - BY - CO - PH - PK - PL - TL");
        }

        if ((ComponentType != null) && (ComponentType.length() > 2)) {

            log.warning("Length > 2 - truncated");
            ComponentType	= ComponentType.substring(0, 1);
        }

        set_Value("ComponentType", ComponentType);
    }

/** Set Description.
 *
 * @param Description
Optional short description of the record */
    public void setDescription(String Description) {

        if ((Description != null) && (Description.length() > 510)) {

            log.warning("Length > 510 - truncated");
            Description	= Description.substring(0, 509);
        }

        set_Value("Description", Description);
    }

/**
 * Set IsCritical
 *
 * @param IsCritical
 */
    public void setIsCritical(boolean IsCritical) {
        set_Value("IsCritical", new Boolean(IsCritical));
    }

/**
 * Set IsQtyPercentage
 *
 * @param IsQtyPercentage
 */
    public void setIsQtyPercentage(boolean IsQtyPercentage) {
        set_Value("IsQtyPercentage", new Boolean(IsQtyPercentage));
    }

/**
 * Set IssueMethod
 *
 * @param IssueMethod
 */
    public void setIssueMethod(String IssueMethod) {

        if ((IssueMethod == null) || IssueMethod.equals("0") || IssueMethod.equals("1")) {
            ;
        } else {
            throw new IllegalArgumentException("IssueMethod Invalid value - Reference_ID=1000002 - 0 - 1");
        }

        if ((IssueMethod != null) && (IssueMethod.length() > 1)) {

            log.warning("Length > 1 - truncated");
            IssueMethod	= IssueMethod.substring(0, 0);
        }

        set_Value("IssueMethod", IssueMethod);
    }

/**
 * Set Level no
 *
 * @param LevelNo
 */
    public void setLevelNo(int LevelNo) {
        set_Value("LevelNo", new Integer(LevelNo));
    }

/**
 * Set Levels
 *
 * @param Levels
 */
    public void setLevels(String Levels) {

        if ((Levels != null) && (Levels.length() > 250)) {

            log.warning("Length > 250 - truncated");
            Levels	= Levels.substring(0, 249);
        }

        set_Value("Levels", Levels);
    }

/** Set Line No.
 *
 * @param Line
Unique line for this document */
    public void setLine(int Line) {
        set_Value("Line", new Integer(Line));
    }

/**
 * Set MPC_Product_BOMLine_ID
 *
 * @param MPC_Product_BOMLine_ID
 */
    public void setMPC_Product_BOMLine_ID(int MPC_Product_BOMLine_ID) {

        if (MPC_Product_BOMLine_ID <= 0) {
            set_Value("MPC_Product_BOMLine_ID", null);
        } else {
            set_Value("MPC_Product_BOMLine_ID", new Integer(MPC_Product_BOMLine_ID));
        }
    }

/**
 * Set BOM & Formula
 *
 * @param MPC_Product_BOM_ID
 */
    public void setMPC_Product_BOM_ID(int MPC_Product_BOM_ID) {

        if (MPC_Product_BOM_ID <= 0) {
            set_Value("MPC_Product_BOM_ID", null);
        } else {
            set_Value("MPC_Product_BOM_ID", new Integer(MPC_Product_BOM_ID));
        }
    }

/** Set Attribute Set Instance.
 *
 * @param M_AttributeSetInstance_ID
Product Attribute Set Instance */
    public void setM_AttributeSetInstance_ID(int M_AttributeSetInstance_ID) {

        if (M_AttributeSetInstance_ID <= 0) {
            set_Value("M_AttributeSetInstance_ID", null);
        } else {
            set_Value("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
        }
    }

/** Set Product.
 *
 * @param M_Product_ID
Product, Service, Item */
    public void setM_Product_ID(int M_Product_ID) {

        if (M_Product_ID <= 0) {
            set_Value("M_Product_ID", null);
        } else {
            set_Value("M_Product_ID", new Integer(M_Product_ID));
        }
    }

/** Set Qty.
 *
 * @param QtyBOM
Bill of Materials Quantity */
    public void setQtyBOM(BigDecimal QtyBOM) {
        set_Value("QtyBOM", QtyBOM);
    }

/**
 * Set Qty %
 *
 * @param QtyBatch
 */
    public void setQtyBatch(BigDecimal QtyBatch) {

        if (QtyBatch == null) {
            throw new IllegalArgumentException("QtyBatch is mandatory");
        }

        set_Value("QtyBatch", QtyBatch);
    }

/**
 * Set Scrap
 *
 * @param Scrap
 */
    public void setScrap(BigDecimal Scrap) {
        set_Value("Scrap", Scrap);
    }

/** Set Sequence.
Method of ordering records;
 *
 * @param SeqNo
 lowest number comes first */
    public void setSeqNo(int SeqNo) {
        set_Value("SeqNo", new Integer(SeqNo));
    }

/** Set Valid from.
 *
 * @param ValidFrom
Valid from including this date (first day) */
    public void setValidFrom(Timestamp ValidFrom) {
        set_Value("ValidFrom", ValidFrom);
    }

/** Set Valid to.
 *
 * @param ValidTo
Valid to including this date (last day) */
    public void setValidTo(Timestamp ValidTo) {
        set_Value("ValidTo", ValidTo);
    }
}



/*
 * @(#)X_RV_MPC_Product_BOMLine.java   02.jul 2007
 * 
 *  Fin del fichero X_RV_MPC_Product_BOMLine.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
