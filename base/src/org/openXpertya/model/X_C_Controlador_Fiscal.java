/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Controlador_Fiscal
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-07-04 19:14:05.305 */
public class X_C_Controlador_Fiscal extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Controlador_Fiscal (Properties ctx, int C_Controlador_Fiscal_ID, String trxName)
{
super (ctx, C_Controlador_Fiscal_ID, trxName);
/** if (C_Controlador_Fiscal_ID == 0)
{
setC_Controlador_Fiscal_ID (0);
setC_Controlador_Fiscal_Type_ID (0);
setCmdCancelBeforePrintDocument (false);
sethost (null);
setIsRemote (false);
setName (null);
setOnPrintProductFormat (null);
setOnPrintUseProductReference (false);
setPort (0);
}
 */
}
/** Load Constructor */
public X_C_Controlador_Fiscal (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Controlador_Fiscal");

/** TableName=C_Controlador_Fiscal */
public static final String Table_Name="C_Controlador_Fiscal";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Controlador_Fiscal");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Controlador_Fiscal[").append(getID()).append("]");
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
/** Set C_Controlador_Fiscal_ID */
public void setC_Controlador_Fiscal_ID (int C_Controlador_Fiscal_ID)
{
set_ValueNoCheck ("C_Controlador_Fiscal_ID", new Integer(C_Controlador_Fiscal_ID));
}
/** Get C_Controlador_Fiscal_ID */
public int getC_Controlador_Fiscal_ID() 
{
Integer ii = (Integer)get_Value("C_Controlador_Fiscal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Controlador_Fiscal_Type_ID */
public void setC_Controlador_Fiscal_Type_ID (int C_Controlador_Fiscal_Type_ID)
{
set_Value ("C_Controlador_Fiscal_Type_ID", new Integer(C_Controlador_Fiscal_Type_ID));
}
/** Get C_Controlador_Fiscal_Type_ID */
public int getC_Controlador_Fiscal_Type_ID() 
{
Integer ii = (Integer)get_Value("C_Controlador_Fiscal_Type_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cmd Cancel Before Print Document.
Cancel command before print document */
public void setCmdCancelBeforePrintDocument (boolean CmdCancelBeforePrintDocument)
{
set_Value ("CmdCancelBeforePrintDocument", new Boolean(CmdCancelBeforePrintDocument));
}
/** Get Cmd Cancel Before Print Document.
Cancel command before print document */
public boolean isCmdCancelBeforePrintDocument() 
{
Object oo = get_Value("CmdCancelBeforePrintDocument");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 200)
{
log.warning("Length > 200 - truncated");
Description = Description.substring(0,200);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set host */
public void sethost (String host)
{
if (host == null) throw new IllegalArgumentException ("host is mandatory");
if (host.length() > 100)
{
log.warning("Length > 100 - truncated");
host = host.substring(0,100);
}
set_Value ("host", host);
}
/** Get host */
public String gethost() 
{
return (String)get_Value("host");
}
/** Set Is Remote.
Remote Fiscal Print */
public void setIsRemote (boolean IsRemote)
{
set_Value ("IsRemote", new Boolean(IsRemote));
}
/** Get Is Remote.
Remote Fiscal Print */
public boolean isRemote() 
{
Object oo = get_Value("IsRemote");
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
if (Name.length() > 100)
{
log.warning("Length > 100 - truncated");
Name = Name.substring(0,100);
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
public static final int ONPRINTPRODUCTFORMAT_AD_Reference_ID = MReference.getReferenceID("PrintProductFormat");
/** Name = N */
public static final String ONPRINTPRODUCTFORMAT_Name = "N";
/** Value = V */
public static final String ONPRINTPRODUCTFORMAT_Value = "V";
/** Name Value = NV */
public static final String ONPRINTPRODUCTFORMAT_NameValue = "NV";
/** Value Name = VN */
public static final String ONPRINTPRODUCTFORMAT_ValueName = "VN";
/** Set OnPrintProductFormat */
public void setOnPrintProductFormat (String OnPrintProductFormat)
{
if (OnPrintProductFormat.equals("N") || OnPrintProductFormat.equals("V") || OnPrintProductFormat.equals("NV") || OnPrintProductFormat.equals("VN"));
 else throw new IllegalArgumentException ("OnPrintProductFormat Invalid value - Reference = ONPRINTPRODUCTFORMAT_AD_Reference_ID - N - V - NV - VN");
if (OnPrintProductFormat == null) throw new IllegalArgumentException ("OnPrintProductFormat is mandatory");
if (OnPrintProductFormat.length() > 2)
{
log.warning("Length > 2 - truncated");
OnPrintProductFormat = OnPrintProductFormat.substring(0,2);
}
set_Value ("OnPrintProductFormat", OnPrintProductFormat);
}
/** Get OnPrintProductFormat */
public String getOnPrintProductFormat() 
{
return (String)get_Value("OnPrintProductFormat");
}
/** Set OnPrintUseProductReference */
public void setOnPrintUseProductReference (boolean OnPrintUseProductReference)
{
set_Value ("OnPrintUseProductReference", new Boolean(OnPrintUseProductReference));
}
/** Get OnPrintUseProductReference */
public boolean isOnPrintUseProductReference() 
{
Object oo = get_Value("OnPrintUseProductReference");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set operation_date */
public void setoperation_date (Timestamp operation_date)
{
set_Value ("operation_date", operation_date);
}
/** Get operation_date */
public Timestamp getoperation_date() 
{
return (Timestamp)get_Value("operation_date");
}
/** Set Port */
public void setPort (int Port)
{
set_Value ("Port", new Integer(Port));
}
/** Get Port */
public int getPort() 
{
Integer ii = (Integer)get_Value("Port");
if (ii == null) return 0;
return ii.intValue();
}
public static final int STATUS_AD_Reference_ID = MReference.getReferenceID("Controlador_Fiscal_Status");
/** BUSY = BSY */
public static final String STATUS_BUSY = "BSY";
/** ERROR = ERR */
public static final String STATUS_ERROR = "ERR";
/** IDLE = IDL */
public static final String STATUS_IDLE = "IDL";
/** Set status */
public void setstatus (String status)
{
if (status == null || status.equals("BSY") || status.equals("ERR") || status.equals("IDL"));
 else throw new IllegalArgumentException ("status Invalid value - Reference = STATUS_AD_Reference_ID - BSY - ERR - IDL");
if (status != null && status.length() > 3)
{
log.warning("Length > 3 - truncated");
status = status.substring(0,3);
}
set_Value ("status", status);
}
/** Get status */
public String getstatus() 
{
return (String)get_Value("status");
}
public static final int USEDBY_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set UsedBy_ID */
public void setUsedBy_ID (int UsedBy_ID)
{
if (UsedBy_ID <= 0) set_Value ("UsedBy_ID", null);
 else 
set_Value ("UsedBy_ID", new Integer(UsedBy_ID));
}
/** Get UsedBy_ID */
public int getUsedBy_ID() 
{
Integer ii = (Integer)get_Value("UsedBy_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
