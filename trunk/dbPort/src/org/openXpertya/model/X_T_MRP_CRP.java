/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_MRP_CRP
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.078 */
public class X_T_MRP_CRP extends PO
{
/** Constructor estándar */
public X_T_MRP_CRP (Properties ctx, int T_MRP_CRP_ID, String trxName)
{
super (ctx, T_MRP_CRP_ID, trxName);
/** if (T_MRP_CRP_ID == 0)
{
setT_MRP_CRP_ID (0);
}
 */
}
/** Load Constructor */
public X_T_MRP_CRP (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000074 */
public static final int Table_ID=1000074;

/** TableName=T_MRP_CRP */
public static final String Table_Name="T_MRP_CRP";

protected static KeyNamePair Model = new KeyNamePair(1000074,"T_MRP_CRP");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_MRP_CRP[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
if (AD_PInstance_ID <= 0) set_Value ("AD_PInstance_ID", null);
 else 
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 50)
{
log.warning("Length > 50 - truncated");
Description = Description.substring(0,49);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Range00 */
public void setRange00 (String Range00)
{
if (Range00 != null && Range00.length() > 20)
{
log.warning("Length > 20 - truncated");
Range00 = Range00.substring(0,19);
}
set_Value ("Range00", Range00);
}
/** Get Range00 */
public String getRange00() 
{
return (String)get_Value("Range00");
}
/** Set Range01 */
public void setRange01 (String Range01)
{
if (Range01 != null && Range01.length() > 20)
{
log.warning("Length > 20 - truncated");
Range01 = Range01.substring(0,19);
}
set_Value ("Range01", Range01);
}
/** Get Range01 */
public String getRange01() 
{
return (String)get_Value("Range01");
}
/** Set Range02 */
public void setRange02 (String Range02)
{
if (Range02 != null && Range02.length() > 20)
{
log.warning("Length > 20 - truncated");
Range02 = Range02.substring(0,19);
}
set_Value ("Range02", Range02);
}
/** Get Range02 */
public String getRange02() 
{
return (String)get_Value("Range02");
}
/** Set Range03 */
public void setRange03 (String Range03)
{
if (Range03 != null && Range03.length() > 20)
{
log.warning("Length > 20 - truncated");
Range03 = Range03.substring(0,19);
}
set_Value ("Range03", Range03);
}
/** Get Range03 */
public String getRange03() 
{
return (String)get_Value("Range03");
}
/** Set Range04 */
public void setRange04 (String Range04)
{
if (Range04 != null && Range04.length() > 20)
{
log.warning("Length > 20 - truncated");
Range04 = Range04.substring(0,19);
}
set_Value ("Range04", Range04);
}
/** Get Range04 */
public String getRange04() 
{
return (String)get_Value("Range04");
}
/** Set Range05 */
public void setRange05 (String Range05)
{
if (Range05 != null && Range05.length() > 20)
{
log.warning("Length > 20 - truncated");
Range05 = Range05.substring(0,19);
}
set_Value ("Range05", Range05);
}
/** Get Range05 */
public String getRange05() 
{
return (String)get_Value("Range05");
}
/** Set Range06 */
public void setRange06 (String Range06)
{
if (Range06 != null && Range06.length() > 20)
{
log.warning("Length > 20 - truncated");
Range06 = Range06.substring(0,19);
}
set_Value ("Range06", Range06);
}
/** Get Range06 */
public String getRange06() 
{
return (String)get_Value("Range06");
}
/** Set Range07 */
public void setRange07 (String Range07)
{
if (Range07 != null && Range07.length() > 20)
{
log.warning("Length > 20 - truncated");
Range07 = Range07.substring(0,19);
}
set_Value ("Range07", Range07);
}
/** Get Range07 */
public String getRange07() 
{
return (String)get_Value("Range07");
}
/** Set Range08 */
public void setRange08 (String Range08)
{
if (Range08 != null && Range08.length() > 20)
{
log.warning("Length > 20 - truncated");
Range08 = Range08.substring(0,19);
}
set_Value ("Range08", Range08);
}
/** Get Range08 */
public String getRange08() 
{
return (String)get_Value("Range08");
}
/** Set Range09 */
public void setRange09 (String Range09)
{
if (Range09 != null && Range09.length() > 20)
{
log.warning("Length > 20 - truncated");
Range09 = Range09.substring(0,19);
}
set_Value ("Range09", Range09);
}
/** Get Range09 */
public String getRange09() 
{
return (String)get_Value("Range09");
}
/** Set Range10 */
public void setRange10 (String Range10)
{
if (Range10 != null && Range10.length() > 20)
{
log.warning("Length > 20 - truncated");
Range10 = Range10.substring(0,19);
}
set_Value ("Range10", Range10);
}
/** Get Range10 */
public String getRange10() 
{
return (String)get_Value("Range10");
}
/** Set Range11 */
public void setRange11 (String Range11)
{
if (Range11 != null && Range11.length() > 20)
{
log.warning("Length > 20 - truncated");
Range11 = Range11.substring(0,19);
}
set_Value ("Range11", Range11);
}
/** Get Range11 */
public String getRange11() 
{
return (String)get_Value("Range11");
}
/** Set Range12 */
public void setRange12 (String Range12)
{
if (Range12 != null && Range12.length() > 20)
{
log.warning("Length > 20 - truncated");
Range12 = Range12.substring(0,19);
}
set_Value ("Range12", Range12);
}
/** Get Range12 */
public String getRange12() 
{
return (String)get_Value("Range12");
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set T_MRP_CRP_ID */
public void setT_MRP_CRP_ID (int T_MRP_CRP_ID)
{
set_ValueNoCheck ("T_MRP_CRP_ID", new Integer(T_MRP_CRP_ID));
}
/** Get T_MRP_CRP_ID */
public int getT_MRP_CRP_ID() 
{
Integer ii = (Integer)get_Value("T_MRP_CRP_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
