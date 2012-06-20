/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_Category
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-12-02 16:44:46.089 */
public class X_M_Product_Category extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Product_Category (Properties ctx, int M_Product_Category_ID, String trxName)
{
super (ctx, M_Product_Category_ID, trxName);
/** if (M_Product_Category_ID == 0)
{
setAmortizationPerc (Env.ZERO);
setIsDefault (false);
setIsSelfService (true);	// Y
setMMPolicy (null);	// F
setM_Product_Category_ID (0);
setName (null);
setPlannedMargin (Env.ZERO);
setValue (null);
setYearLife (0);
}
 */
}
/** Load Constructor */
public X_M_Product_Category (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Product_Category");

/** TableName=M_Product_Category */
public static final String Table_Name="M_Product_Category";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Product_Category");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_Category[").append(getID()).append("]");
return sb.toString();
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
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
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
/** Set Amortization Percentage.
Anual Amortization Percentage */
public void setAmortizationPerc (BigDecimal AmortizationPerc)
{
if (AmortizationPerc == null) throw new IllegalArgumentException ("AmortizationPerc is mandatory");
set_Value ("AmortizationPerc", AmortizationPerc);
}
/** Get Amortization Percentage.
Anual Amortization Percentage */
public BigDecimal getAmortizationPerc() 
{
BigDecimal bd = (BigDecimal)get_Value("AmortizationPerc");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
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
public static final int MMPOLICY_AD_Reference_ID = MReference.getReferenceID("_MMPolicy");
/** LiFo = L */
public static final String MMPOLICY_LiFo = "L";
/** FiFo = F */
public static final String MMPOLICY_FiFo = "F";
/** Set Material Policy.
Material Movement Policy */
public void setMMPolicy (String MMPolicy)
{
if (MMPolicy.equals("L") || MMPolicy.equals("F"));
 else throw new IllegalArgumentException ("MMPolicy Invalid value - Reference = MMPOLICY_AD_Reference_ID - L - F");
if (MMPolicy == null) throw new IllegalArgumentException ("MMPolicy is mandatory");
if (MMPolicy.length() > 1)
{
log.warning("Length > 1 - truncated");
MMPolicy = MMPolicy.substring(0,1);
}
set_Value ("MMPolicy", MMPolicy);
}
/** Get Material Policy.
Material Movement Policy */
public String getMMPolicy() 
{
return (String)get_Value("MMPolicy");
}
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
set_ValueNoCheck ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCT_GAMAS_ID_AD_Reference_ID = MReference.getReferenceID("m_product_gamas");
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
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
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
if (PlannedMargin == null) throw new IllegalArgumentException ("PlannedMargin is mandatory");
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
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
/** Set Year Life */
public void setYearLife (int YearLife)
{
set_Value ("YearLife", new Integer(YearLife));
}
/** Get Year Life */
public int getYearLife() 
{
Integer ii = (Integer)get_Value("YearLife");
if (ii == null) return 0;
return ii.intValue();
}
}
