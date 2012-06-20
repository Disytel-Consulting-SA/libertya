/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_Measure
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.171 */
public class X_PA_Measure extends PO
{
/** Constructor estándar */
public X_PA_Measure (Properties ctx, int PA_Measure_ID, String trxName)
{
super (ctx, PA_Measure_ID, trxName);
/** if (PA_Measure_ID == 0)
{
setMeasureType (null);	// M
setName (null);
setPA_Measure_ID (0);
}
 */
}
/** Load Constructor */
public X_PA_Measure (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=441 */
public static final int Table_ID=441;

/** TableName=PA_Measure */
public static final String Table_Name="PA_Measure";

protected static KeyNamePair Model = new KeyNamePair(441,"PA_Measure");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_Measure[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner Group.
Business Partner Group */
public void setC_BP_Group_ID (int C_BP_Group_ID)
{
if (C_BP_Group_ID <= 0) set_Value ("C_BP_Group_ID", null);
 else 
set_Value ("C_BP_Group_ID", new Integer(C_BP_Group_ID));
}
/** Get Business Partner Group.
Business Partner Group */
public int getC_BP_Group_ID() 
{
Integer ii = (Integer)get_Value("C_BP_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
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
/** Set Calculation Class.
Java Class for calculation, implementing Interface Measure */
public void setCalculationClass (String CalculationClass)
{
if (CalculationClass != null && CalculationClass.length() > 60)
{
log.warning("Length > 60 - truncated");
CalculationClass = CalculationClass.substring(0,59);
}
set_Value ("CalculationClass", CalculationClass);
}
/** Get Calculation Class.
Java Class for calculation, implementing Interface Measure */
public String getCalculationClass() 
{
return (String)get_Value("CalculationClass");
}
/** Set Date From.
Starting date for a range */
public void setDateFrom (Timestamp DateFrom)
{
set_Value ("DateFrom", DateFrom);
}
/** Get Date From.
Starting date for a range */
public Timestamp getDateFrom() 
{
return (Timestamp)get_Value("DateFrom");
}
/** Set Date To.
End date of a date range */
public void setDateTo (Timestamp DateTo)
{
set_Value ("DateTo", DateTo);
}
/** Get Date To.
End date of a date range */
public Timestamp getDateTo() 
{
return (Timestamp)get_Value("DateTo");
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
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
if (M_Product_Category_ID <= 0) set_Value ("M_Product_Category_ID", null);
 else 
set_Value ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Manual Actual.
Manually entered actual value */
public void setManualActual (BigDecimal ManualActual)
{
set_Value ("ManualActual", ManualActual);
}
/** Get Manual Actual.
Manually entered actual value */
public BigDecimal getManualActual() 
{
BigDecimal bd = (BigDecimal)get_Value("ManualActual");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Note.
Note for manual entry */
public void setManualNote (String ManualNote)
{
if (ManualNote != null && ManualNote.length() > 255)
{
log.warning("Length > 255 - truncated");
ManualNote = ManualNote.substring(0,254);
}
set_Value ("ManualNote", ManualNote);
}
/** Get Note.
Note for manual entry */
public String getManualNote() 
{
return (String)get_Value("ManualNote");
}
public static final int MEASURETYPE_AD_Reference_ID=231;
/** Calculated = C */
public static final String MEASURETYPE_Calculated = "C";
/** Progress = P */
public static final String MEASURETYPE_Progress = "P";
/** Manual = M */
public static final String MEASURETYPE_Manual = "M";
/** Set Measure Type.
Determines how the actual performance is derived */
public void setMeasureType (String MeasureType)
{
if (MeasureType.equals("C") || MeasureType.equals("P") || MeasureType.equals("M"));
 else throw new IllegalArgumentException ("MeasureType Invalid value - Reference_ID=231 - C - P - M");
if (MeasureType == null) throw new IllegalArgumentException ("MeasureType is mandatory");
if (MeasureType.length() > 1)
{
log.warning("Length > 1 - truncated");
MeasureType = MeasureType.substring(0,0);
}
set_Value ("MeasureType", MeasureType);
}
/** Get Measure Type.
Determines how the actual performance is derived */
public String getMeasureType() 
{
return (String)get_Value("MeasureType");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
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
public static final int ORG_ID_AD_Reference_ID=130;
/** Set Organization.
Organizational entity within client */
public void setOrg_ID (int Org_ID)
{
if (Org_ID <= 0) set_Value ("Org_ID", null);
 else 
set_Value ("Org_ID", new Integer(Org_ID));
}
/** Get Organization.
Organizational entity within client */
public int getOrg_ID() 
{
Integer ii = (Integer)get_Value("Org_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Achievement.
Performance Achievement */
public void setPA_Achievement_ID (int PA_Achievement_ID)
{
if (PA_Achievement_ID <= 0) set_Value ("PA_Achievement_ID", null);
 else 
set_Value ("PA_Achievement_ID", new Integer(PA_Achievement_ID));
}
/** Get Achievement.
Performance Achievement */
public int getPA_Achievement_ID() 
{
Integer ii = (Integer)get_Value("PA_Achievement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Measure Calculation.
Calculation method for measuring performance */
public void setPA_MeasureCalc_ID (int PA_MeasureCalc_ID)
{
if (PA_MeasureCalc_ID <= 0) set_Value ("PA_MeasureCalc_ID", null);
 else 
set_Value ("PA_MeasureCalc_ID", new Integer(PA_MeasureCalc_ID));
}
/** Get Measure Calculation.
Calculation method for measuring performance */
public int getPA_MeasureCalc_ID() 
{
Integer ii = (Integer)get_Value("PA_MeasureCalc_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Measure.
Concrete Performance Measurement */
public void setPA_Measure_ID (int PA_Measure_ID)
{
set_ValueNoCheck ("PA_Measure_ID", new Integer(PA_Measure_ID));
}
/** Get Measure.
Concrete Performance Measurement */
public int getPA_Measure_ID() 
{
Integer ii = (Integer)get_Value("PA_Measure_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
