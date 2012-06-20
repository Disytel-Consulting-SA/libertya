/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Envio
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.796 */
public class X_M_Envio extends PO
{
/** Constructor estándar */
public X_M_Envio (Properties ctx, int M_Envio_ID, String trxName)
{
super (ctx, M_Envio_ID, trxName);
/** if (M_Envio_ID == 0)
{
setDateReceived (new Timestamp(System.currentTimeMillis()));
setIsSelected (false);
setM_Envio_ID (0);
setM_Shipper_ID (0);
setShipDate (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_M_Envio (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000104 */
public static final int Table_ID=1000104;

/** TableName=M_Envio */
public static final String Table_Name="M_Envio";

protected static KeyNamePair Model = new KeyNamePair(1000104,"M_Envio");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Envio[").append(getID()).append("]");
return sb.toString();
}
/** Set Validation code.
Validation Code */
public void setCode (BigDecimal Code)
{
set_Value ("Code", Code);
}
/** Get Validation code.
Validation Code */
public BigDecimal getCode() 
{
BigDecimal bd = (BigDecimal)get_Value("Code");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set TIMESTAMP received.
TIMESTAMP a product was received */
public void setDateReceived (Timestamp DateReceived)
{
if (DateReceived == null) throw new IllegalArgumentException ("DateReceived is mandatory");
set_Value ("DateReceived", DateReceived);
}
/** Get TIMESTAMP received.
TIMESTAMP a product was received */
public Timestamp getDateReceived() 
{
return (Timestamp)get_Value("DateReceived");
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
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 255)
{
log.warning("Length > 255 - truncated");
DocumentNo = DocumentNo.substring(0,254);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Selected */
public void setIsSelected (boolean IsSelected)
{
set_Value ("IsSelected", new Boolean(IsSelected));
}
/** Get Selected */
public boolean isSelected() 
{
Object oo = get_Value("IsSelected");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set M_Envio_ID */
public void setM_Envio_ID (int M_Envio_ID)
{
set_ValueNoCheck ("M_Envio_ID", new Integer(M_Envio_ID));
}
/** Get M_Envio_ID */
public int getM_Envio_ID() 
{
Integer ii = (Integer)get_Value("M_Envio_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipper.
Method or manner of product delivery */
public void setM_Shipper_ID (int M_Shipper_ID)
{
set_Value ("M_Shipper_ID", new Integer(M_Shipper_ID));
}
/** Get Shipper.
Method or manner of product delivery */
public int getM_Shipper_ID() 
{
Integer ii = (Integer)get_Value("M_Shipper_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set No_Paquetes */
public void setNo_Paquetes (int No_Paquetes)
{
set_Value ("No_Paquetes", new Integer(No_Paquetes));
}
/** Get No_Paquetes */
public int getNo_Paquetes() 
{
Integer ii = (Integer)get_Value("No_Paquetes");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Info Received.
Information of the receipt of the package (acknowledgement) */
public void setReceivedInfo (String ReceivedInfo)
{
if (ReceivedInfo != null && ReceivedInfo.length() > 255)
{
log.warning("Length > 255 - truncated");
ReceivedInfo = ReceivedInfo.substring(0,254);
}
set_Value ("ReceivedInfo", ReceivedInfo);
}
/** Get Info Received.
Information of the receipt of the package (acknowledgement) */
public String getReceivedInfo() 
{
return (String)get_Value("ReceivedInfo");
}
/** Set Ship TIMESTAMP.
Shipment TIMESTAMP/Time */
public void setShipDate (Timestamp ShipDate)
{
if (ShipDate == null) throw new IllegalArgumentException ("ShipDate is mandatory");
set_Value ("ShipDate", ShipDate);
}
/** Get Ship TIMESTAMP.
Shipment TIMESTAMP/Time */
public Timestamp getShipDate() 
{
return (Timestamp)get_Value("ShipDate");
}
/** Set trakinginfo */
public void settrakinginfo (String trakinginfo)
{
if (trakinginfo != null && trakinginfo.length() > 255)
{
log.warning("Length > 255 - truncated");
trakinginfo = trakinginfo.substring(0,254);
}
set_Value ("trakinginfo", trakinginfo);
}
/** Get trakinginfo */
public String gettrakinginfo() 
{
return (String)get_Value("trakinginfo");
}
}
