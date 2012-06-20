/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_PInstance_Para
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-10-29 15:01:43.815 */
public class X_AD_PInstance_Para extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_PInstance_Para (Properties ctx, int AD_PInstance_Para_ID, String trxName)
{
super (ctx, AD_PInstance_Para_ID, trxName);
/** if (AD_PInstance_Para_ID == 0)
{
setAD_PInstance_ID (0);
setSeqNo (0);
}
 */
}
/** Load Constructor */
public X_AD_PInstance_Para (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_PInstance_Para");

/** TableName=AD_PInstance_Para */
public static final String Table_Name="AD_PInstance_Para";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_PInstance_Para");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_PInstance_Para[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_ValueNoCheck ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Info.
Information */
public void setInfo (String Info)
{
if (Info != null && Info.length() > 255)
{
log.warning("Length > 255 - truncated");
Info = Info.substring(0,255);
}
set_Value ("Info", Info);
}
/** Get Info.
Information */
public String getInfo() 
{
return (String)get_Value("Info");
}
/** Set Info To */
public void setInfo_To (String Info_To)
{
if (Info_To != null && Info_To.length() > 255)
{
log.warning("Length > 255 - truncated");
Info_To = Info_To.substring(0,255);
}
set_Value ("Info_To", Info_To);
}
/** Get Info To */
public String getInfo_To() 
{
return (String)get_Value("Info_To");
}
/** Set Parameter Name */
public void setParameterName (String ParameterName)
{
if (ParameterName != null && ParameterName.length() > 60)
{
log.warning("Length > 60 - truncated");
ParameterName = ParameterName.substring(0,60);
}
set_Value ("ParameterName", ParameterName);
}
/** Get Parameter Name */
public String getParameterName() 
{
return (String)get_Value("ParameterName");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getParameterName());
}
/** Set Process Date.
Process Parameter */
public void setP_Date (Timestamp P_Date)
{
set_Value ("P_Date", P_Date);
}
/** Get Process Date.
Process Parameter */
public Timestamp getP_Date() 
{
return (Timestamp)get_Value("P_Date");
}
/** Set Process Date To.
Process Parameter */
public void setP_Date_To (Timestamp P_Date_To)
{
set_Value ("P_Date_To", P_Date_To);
}
/** Get Process Date To.
Process Parameter */
public Timestamp getP_Date_To() 
{
return (Timestamp)get_Value("P_Date_To");
}
/** Set Process Number.
Process Parameter */
public void setP_Number (BigDecimal P_Number)
{
set_Value ("P_Number", P_Number);
}
/** Get Process Number.
Process Parameter */
public BigDecimal getP_Number() 
{
BigDecimal bd = (BigDecimal)get_Value("P_Number");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Process Number To.
Process Parameter */
public void setP_Number_To (BigDecimal P_Number_To)
{
set_Value ("P_Number_To", P_Number_To);
}
/** Get Process Number To.
Process Parameter */
public BigDecimal getP_Number_To() 
{
BigDecimal bd = (BigDecimal)get_Value("P_Number_To");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Process String.
Process Parameter */
public void setP_String (String P_String)
{
if (P_String != null && P_String.length() > 255)
{
log.warning("Length > 255 - truncated");
P_String = P_String.substring(0,255);
}
set_Value ("P_String", P_String);
}
/** Get Process String.
Process Parameter */
public String getP_String() 
{
return (String)get_Value("P_String");
}
/** Set Process String To.
Process Parameter */
public void setP_String_To (String P_String_To)
{
if (P_String_To != null && P_String_To.length() > 255)
{
log.warning("Length > 255 - truncated");
P_String_To = P_String_To.substring(0,255);
}
set_Value ("P_String_To", P_String_To);
}
/** Get Process String To.
Process Parameter */
public String getP_String_To() 
{
return (String)get_Value("P_String_To");
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_ValueNoCheck ("SeqNo", new Integer(SeqNo));
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
}
