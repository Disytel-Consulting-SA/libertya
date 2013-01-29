/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Package
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:38.718 */
public class X_M_Package extends PO
{
/** Constructor estándar */
public X_M_Package (Properties ctx, int M_Package_ID, String trxName)
{
super (ctx, M_Package_ID, trxName);
/** if (M_Package_ID == 0)
{
setDocumentNo (null);
setM_InOut_ID (0);
setM_Package_ID (0);
setM_Shipper_ID (0);
}
 */
}
/** Load Constructor */
public X_M_Package (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=664 */
public static final int Table_ID=664;

/** TableName=M_Package */
public static final String Table_Name="M_Package";

protected static KeyNamePair Model = new KeyNamePair(664,"M_Package");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Package[").append(getID()).append("]");
return sb.toString();
}
/** Set Date received.
Date a product was received */
public void setDateReceived (Timestamp DateReceived)
{
set_Value ("DateReceived", DateReceived);
}
/** Get Date received.
Date a product was received */
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
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,29);
}
set_ValueNoCheck ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set M_Envio_ID */
public void setM_Envio_ID (int M_Envio_ID)
{
if (M_Envio_ID <= 0) set_Value ("M_Envio_ID", null);
 else 
set_Value ("M_Envio_ID", new Integer(M_Envio_ID));
}
/** Get M_Envio_ID */
public int getM_Envio_ID() 
{
Integer ii = (Integer)get_Value("M_Envio_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt.
Material Shipment Document */
public void setM_InOut_ID (int M_InOut_ID)
{
set_ValueNoCheck ("M_InOut_ID", new Integer(M_InOut_ID));
}
/** Get Shipment/Receipt.
Material Shipment Document */
public int getM_InOut_ID() 
{
Integer ii = (Integer)get_Value("M_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Package.
Shipment Package */
public void setM_Package_ID (int M_Package_ID)
{
set_ValueNoCheck ("M_Package_ID", new Integer(M_Package_ID));
}
/** Get Package.
Shipment Package */
public int getM_Package_ID() 
{
Integer ii = (Integer)get_Value("M_Package_ID");
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
/** Set Ship Date.
Shipment Date/Time */
public void setShipDate (Timestamp ShipDate)
{
set_Value ("ShipDate", ShipDate);
}
/** Get Ship Date.
Shipment Date/Time */
public Timestamp getShipDate() 
{
return (Timestamp)get_Value("ShipDate");
}
/** Set Tracking Info */
public void setTrackingInfo (String TrackingInfo)
{
if (TrackingInfo != null && TrackingInfo.length() > 255)
{
log.warning("Length > 255 - truncated");
TrackingInfo = TrackingInfo.substring(0,254);
}
set_Value ("TrackingInfo", TrackingInfo);
}
/** Get Tracking Info */
public String getTrackingInfo() 
{
return (String)get_Value("TrackingInfo");
}
}
