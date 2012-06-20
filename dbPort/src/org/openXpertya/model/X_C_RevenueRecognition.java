/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RevenueRecognition
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.828 */
public class X_C_RevenueRecognition extends PO
{
/** Constructor estándar */
public X_C_RevenueRecognition (Properties ctx, int C_RevenueRecognition_ID, String trxName)
{
super (ctx, C_RevenueRecognition_ID, trxName);
/** if (C_RevenueRecognition_ID == 0)
{
setC_RevenueRecognition_ID (0);
setIsTimeBased (false);
setName (null);
setRecognitionFrequency (null);
}
 */
}
/** Load Constructor */
public X_C_RevenueRecognition (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=336 */
public static final int Table_ID=336;

/** TableName=C_RevenueRecognition */
public static final String Table_Name="C_RevenueRecognition";

protected static KeyNamePair Model = new KeyNamePair(336,"C_RevenueRecognition");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RevenueRecognition[").append(getID()).append("]");
return sb.toString();
}
/** Set Revenue Recognition.
Method for recording revenue */
public void setC_RevenueRecognition_ID (int C_RevenueRecognition_ID)
{
set_ValueNoCheck ("C_RevenueRecognition_ID", new Integer(C_RevenueRecognition_ID));
}
/** Get Revenue Recognition.
Method for recording revenue */
public int getC_RevenueRecognition_ID() 
{
Integer ii = (Integer)get_Value("C_RevenueRecognition_ID");
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
/** Set Time based.
Time based Revenue Recognition rather than Service Level based */
public void setIsTimeBased (boolean IsTimeBased)
{
set_Value ("IsTimeBased", new Boolean(IsTimeBased));
}
/** Get Time based.
Time based Revenue Recognition rather than Service Level based */
public boolean isTimeBased() 
{
Object oo = get_Value("IsTimeBased");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Number of Months */
public void setNoMonths (int NoMonths)
{
set_Value ("NoMonths", new Integer(NoMonths));
}
/** Get Number of Months */
public int getNoMonths() 
{
Integer ii = (Integer)get_Value("NoMonths");
if (ii == null) return 0;
return ii.intValue();
}
public static final int RECOGNITIONFREQUENCY_AD_Reference_ID=196;
/** Year = Y */
public static final String RECOGNITIONFREQUENCY_Year = "Y";
/** Month = M */
public static final String RECOGNITIONFREQUENCY_Month = "M";
/** Quarter = Q */
public static final String RECOGNITIONFREQUENCY_Quarter = "Q";
/** Set Recognition frequency */
public void setRecognitionFrequency (String RecognitionFrequency)
{
if (RecognitionFrequency.equals("Y") || RecognitionFrequency.equals("M") || RecognitionFrequency.equals("Q"));
 else throw new IllegalArgumentException ("RecognitionFrequency Invalid value - Reference_ID=196 - Y - M - Q");
if (RecognitionFrequency == null) throw new IllegalArgumentException ("RecognitionFrequency is mandatory");
if (RecognitionFrequency.length() > 1)
{
log.warning("Length > 1 - truncated");
RecognitionFrequency = RecognitionFrequency.substring(0,0);
}
set_Value ("RecognitionFrequency", RecognitionFrequency);
}
/** Get Recognition frequency */
public String getRecognitionFrequency() 
{
return (String)get_Value("RecognitionFrequency");
}
}
