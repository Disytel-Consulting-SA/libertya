/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_JobRemuneration
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:31.785 */
public class X_C_JobRemuneration extends PO
{
/** Constructor estándar */
public X_C_JobRemuneration (Properties ctx, int C_JobRemuneration_ID, String trxName)
{
super (ctx, C_JobRemuneration_ID, trxName);
/** if (C_JobRemuneration_ID == 0)
{
setC_Job_ID (0);
setC_JobRemuneration_ID (0);
setC_Remuneration_ID (0);
setValidFrom (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_JobRemuneration (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=793 */
public static final int Table_ID=793;

/** TableName=C_JobRemuneration */
public static final String Table_Name="C_JobRemuneration";

protected static KeyNamePair Model = new KeyNamePair(793,"C_JobRemuneration");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_JobRemuneration[").append(getID()).append("]");
return sb.toString();
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
/** Set Position.
Job Position */
public void setC_Job_ID (int C_Job_ID)
{
set_ValueNoCheck ("C_Job_ID", new Integer(C_Job_ID));
}
/** Get Position.
Job Position */
public int getC_Job_ID() 
{
Integer ii = (Integer)get_Value("C_Job_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Job_ID()));
}
/** Set Position Remuneration.
Remuneration for the Position */
public void setC_JobRemuneration_ID (int C_JobRemuneration_ID)
{
set_ValueNoCheck ("C_JobRemuneration_ID", new Integer(C_JobRemuneration_ID));
}
/** Get Position Remuneration.
Remuneration for the Position */
public int getC_JobRemuneration_ID() 
{
Integer ii = (Integer)get_Value("C_JobRemuneration_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Remuneration.
Wage or Salary */
public void setC_Remuneration_ID (int C_Remuneration_ID)
{
set_ValueNoCheck ("C_Remuneration_ID", new Integer(C_Remuneration_ID));
}
/** Get Remuneration.
Wage or Salary */
public int getC_Remuneration_ID() 
{
Integer ii = (Integer)get_Value("C_Remuneration_ID");
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
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
}
