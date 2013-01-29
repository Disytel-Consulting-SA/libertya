/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_DistributionListLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.625 */
public class X_M_DistributionListLine extends PO
{
/** Constructor estándar */
public X_M_DistributionListLine (Properties ctx, int M_DistributionListLine_ID, String trxName)
{
super (ctx, M_DistributionListLine_ID, trxName);
/** if (M_DistributionListLine_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Location_ID (0);
setM_DistributionListLine_ID (0);
setM_DistributionList_ID (0);
setMinQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_DistributionListLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=665 */
public static final int Table_ID=665;

/** TableName=M_DistributionListLine */
public static final String Table_Name="M_DistributionListLine";

protected static KeyNamePair Model = new KeyNamePair(665,"M_DistributionListLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_DistributionListLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
set_Value ("C_BPartner_Location_ID", new Integer(C_BPartner_Location_ID));
}
/** Get Partner Location.
Identifies the (ship to) address for this Business Partner */
public int getC_BPartner_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Distribution List Line.
Distribution List Line with Business Partner and Quantity/Percentage */
public void setM_DistributionListLine_ID (int M_DistributionListLine_ID)
{
set_ValueNoCheck ("M_DistributionListLine_ID", new Integer(M_DistributionListLine_ID));
}
/** Get Distribution List Line.
Distribution List Line with Business Partner and Quantity/Percentage */
public int getM_DistributionListLine_ID() 
{
Integer ii = (Integer)get_Value("M_DistributionListLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Distribution List.
Distribution Lists allow to distribute products to a selected list of partners */
public void setM_DistributionList_ID (int M_DistributionList_ID)
{
set_ValueNoCheck ("M_DistributionList_ID", new Integer(M_DistributionList_ID));
}
/** Get Distribution List.
Distribution Lists allow to distribute products to a selected list of partners */
public int getM_DistributionList_ID() 
{
Integer ii = (Integer)get_Value("M_DistributionList_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_DistributionList_ID()));
}
/** Set Minimum Quantity.
Minimum quantity for the business partner */
public void setMinQty (BigDecimal MinQty)
{
if (MinQty == null) throw new IllegalArgumentException ("MinQty is mandatory");
set_Value ("MinQty", MinQty);
}
/** Get Minimum Quantity.
Minimum quantity for the business partner */
public BigDecimal getMinQty() 
{
BigDecimal bd = (BigDecimal)get_Value("MinQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Ratio.
Relative Ratio for Distributions */
public void setRatio (BigDecimal Ratio)
{
set_Value ("Ratio", Ratio);
}
/** Get Ratio.
Relative Ratio for Distributions */
public BigDecimal getRatio() 
{
BigDecimal bd = (BigDecimal)get_Value("Ratio");
if (bd == null) return Env.ZERO;
return bd;
}
}
