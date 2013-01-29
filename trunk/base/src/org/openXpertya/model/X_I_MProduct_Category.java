/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_MProduct_Category
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:34.937 */
public class X_I_MProduct_Category extends PO
{
/** Constructor estándar */
public X_I_MProduct_Category (Properties ctx, int I_MProduct_Category_ID, String trxName)
{
super (ctx, I_MProduct_Category_ID, trxName);
/** if (I_MProduct_Category_ID == 0)
{
setI_IsImported (false);
setI_MProduct_Category_ID (0);
setIsDefault (false);
setIsSelfService (false);
}
 */
}
/** Load Constructor */
public X_I_MProduct_Category (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000107 */
public static final int Table_ID=1000107;

/** TableName=I_MProduct_Category */
public static final String Table_Name="I_MProduct_Category";

protected static KeyNamePair Model = new KeyNamePair(1000107,"I_MProduct_Category");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_MProduct_Category[").append(getID()).append("]");
return sb.toString();
}
/** Set Print Color.
Color used for printing and display */
public void setAD_PrintColor_ID (int AD_PrintColor_ID)
{
if (AD_PrintColor_ID <= 0) set_Value ("AD_PrintColor_ID", null);
 else 
set_Value ("AD_PrintColor_ID", new Integer(AD_PrintColor_ID));
}
/** Get Print Color.
Color used for printing and display */
public int getAD_PrintColor_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintColor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Asset Group.
Group of Assets */
public void setA_Asset_Group_ID (int A_Asset_Group_ID)
{
if (A_Asset_Group_ID <= 0) set_Value ("A_Asset_Group_ID", null);
 else 
set_Value ("A_Asset_Group_ID", new Integer(A_Asset_Group_ID));
}
/** Get Asset Group.
Group of Assets */
public int getA_Asset_Group_ID() 
{
Integer ii = (Integer)get_Value("A_Asset_Group_ID");
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
/** Set I_ErrorMsg */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,1999);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get I_ErrorMsg */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set I_IsImported */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get I_IsImported */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set I_MProduct_Category_ID */
public void setI_MProduct_Category_ID (int I_MProduct_Category_ID)
{
set_ValueNoCheck ("I_MProduct_Category_ID", new Integer(I_MProduct_Category_ID));
}
/** Get I_MProduct_Category_ID */
public int getI_MProduct_Category_ID() 
{
Integer ii = (Integer)get_Value("I_MProduct_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_Value ("IsSelfService", new Boolean(IsSelfService));
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public boolean isSelfService() 
{
Object oo = get_Value("IsSelfService");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int MMPOLICY_AD_Reference_ID=335;
/** LiFo = L */
public static final String MMPOLICY_LiFo = "L";
/** FiFo = F */
public static final String MMPOLICY_FiFo = "F";
/** Set Material Policy.
Material Movement Policy */
public void setMMPolicy (String MMPolicy)
{
if (MMPolicy == null || MMPolicy.equals("L") || MMPolicy.equals("F"));
 else throw new IllegalArgumentException ("MMPolicy Invalid value - Reference_ID=335 - L - F");
if (MMPolicy != null && MMPolicy.length() > 1)
{
log.warning("Length > 1 - truncated");
MMPolicy = MMPolicy.substring(0,0);
}
set_Value ("MMPolicy", MMPolicy);
}
/** Get Material Policy.
Material Movement Policy */
public String getMMPolicy() 
{
return (String)get_Value("MMPolicy");
}
public static final int M_PRODUCT_GAMAS_ID_AD_Reference_ID=1000040;
/** Set M_Product_Gamas_ID */
public void setM_Product_Gamas_ID (int M_Product_Gamas_ID)
{
if (M_Product_Gamas_ID <= 0) set_Value ("M_Product_Gamas_ID", null);
 else 
set_Value ("M_Product_Gamas_ID", new Integer(M_Product_Gamas_ID));
}
/** Get M_Product_Gamas_ID */
public int getM_Product_Gamas_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Gamas_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Planned Margin %.
Project's planned margin as a percentage */
public void setPlannedMargin (BigDecimal PlannedMargin)
{
set_Value ("PlannedMargin", PlannedMargin);
}
/** Get Planned Margin %.
Project's planned margin as a percentage */
public BigDecimal getPlannedMargin() 
{
BigDecimal bd = (BigDecimal)get_Value("PlannedMargin");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Processing */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Processing */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,39);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
