/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_JasperReport
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.818 */
public class X_AD_JasperReport extends PO
{
/** Constructor estándar */
public X_AD_JasperReport (Properties ctx, int AD_JasperReport_ID, String trxName)
{
super (ctx, AD_JasperReport_ID, trxName);
/** if (AD_JasperReport_ID == 0)
{
setAD_JasperReport_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_JasperReport (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000136 */
public static final int Table_ID=1000136;

/** TableName=AD_JasperReport */
public static final String Table_Name="AD_JasperReport";

protected static KeyNamePair Model = new KeyNamePair(1000136,"AD_JasperReport");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_JasperReport[").append(getID()).append("]");
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
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_JasperReport_ID */
public void setAD_JasperReport_ID (int AD_JasperReport_ID)
{
set_ValueNoCheck ("AD_JasperReport_ID", new Integer(AD_JasperReport_ID));
}
/** Get AD_JasperReport_ID */
public int getAD_JasperReport_ID() 
{
Integer ii = (Integer)get_Value("AD_JasperReport_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set BinaryData */
public void setBinaryData (byte[] BinaryData)
{
set_Value ("BinaryData", BinaryData);
}
/** Get BinaryData */
public byte[] getBinaryData() 
{
return (byte[])get_Value("BinaryData");
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
/** Set ReportSource */
public void setReportSource (byte[] ReportSource)
{
set_Value ("ReportSource", ReportSource);
}
/** Get ReportSource */
public byte[] getReportSource() 
{
return (byte[])get_Value("ReportSource");
}
}
