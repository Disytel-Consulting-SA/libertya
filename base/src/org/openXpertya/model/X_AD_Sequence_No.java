/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Sequence_No
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:25.0 */
public class X_AD_Sequence_No extends PO
{
/** Constructor estándar */
public X_AD_Sequence_No (Properties ctx, int AD_Sequence_No_ID, String trxName)
{
super (ctx, AD_Sequence_No_ID, trxName);
/** if (AD_Sequence_No_ID == 0)
{
setAD_Sequence_ID (0);
setCurrentNext (0);
setYear (null);
}
 */
}
/** Load Constructor */
public X_AD_Sequence_No (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=122 */
public static final int Table_ID=122;

/** TableName=AD_Sequence_No */
public static final String Table_Name="AD_Sequence_No";

protected static KeyNamePair Model = new KeyNamePair(122,"AD_Sequence_No");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Sequence_No[").append(getID()).append("]");
return sb.toString();
}
/** Set Sequence.
Document Sequence */
public void setAD_Sequence_ID (int AD_Sequence_ID)
{
set_ValueNoCheck ("AD_Sequence_ID", new Integer(AD_Sequence_ID));
}
/** Get Sequence.
Document Sequence */
public int getAD_Sequence_ID() 
{
Integer ii = (Integer)get_Value("AD_Sequence_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Current Next.
The next number to be used */
public void setCurrentNext (int CurrentNext)
{
set_Value ("CurrentNext", new Integer(CurrentNext));
}
/** Get Current Next.
The next number to be used */
public int getCurrentNext() 
{
Integer ii = (Integer)get_Value("CurrentNext");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Year.
Calendar Year */
public void setYear (String Year)
{
if (Year == null) throw new IllegalArgumentException ("Year is mandatory");
if (Year.length() > 4)
{
log.warning("Length > 4 - truncated");
Year = Year.substring(0,3);
}
set_ValueNoCheck ("Year", Year);
}
/** Get Year.
Calendar Year */
public String getYear() 
{
return (String)get_Value("Year");
}
}
