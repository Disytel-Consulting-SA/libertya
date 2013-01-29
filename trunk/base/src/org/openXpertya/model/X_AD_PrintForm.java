/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_PrintForm
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:27.159 */
public class X_AD_PrintForm extends PO
{
/** Constructor estándar */
public X_AD_PrintForm (Properties ctx, int AD_PrintForm_ID, String trxName)
{
super (ctx, AD_PrintForm_ID, trxName);
/** if (AD_PrintForm_ID == 0)
{
setAD_PrintForm_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_PrintForm (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=454 */
public static final int Table_ID=454;

/** TableName=AD_PrintForm */
public static final String Table_Name="AD_PrintForm";

protected static KeyNamePair Model = new KeyNamePair(454,"AD_PrintForm");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_PrintForm[").append(getID()).append("]");
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
/** Set Print Form.
Form */
public void setAD_PrintForm_ID (int AD_PrintForm_ID)
{
set_ValueNoCheck ("AD_PrintForm_ID", new Integer(AD_PrintForm_ID));
}
/** Get Print Form.
Form */
public int getAD_PrintForm_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintForm_ID");
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
public static final int INVOICE_MAILTEXT_ID_AD_Reference_ID=274;
/** Set Invoice Mail Text.
Email text used for sending invoices */
public void setInvoice_MailText_ID (int Invoice_MailText_ID)
{
if (Invoice_MailText_ID <= 0) set_Value ("Invoice_MailText_ID", null);
 else 
set_Value ("Invoice_MailText_ID", new Integer(Invoice_MailText_ID));
}
/** Get Invoice Mail Text.
Email text used for sending invoices */
public int getInvoice_MailText_ID() 
{
Integer ii = (Integer)get_Value("Invoice_MailText_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int INVOICE_PRINTFORMAT_ID_AD_Reference_ID=261;
/** Set Invoice Print Format.
Print Format for printing Invoices */
public void setInvoice_PrintFormat_ID (int Invoice_PrintFormat_ID)
{
if (Invoice_PrintFormat_ID <= 0) set_Value ("Invoice_PrintFormat_ID", null);
 else 
set_Value ("Invoice_PrintFormat_ID", new Integer(Invoice_PrintFormat_ID));
}
/** Get Invoice Print Format.
Print Format for printing Invoices */
public int getInvoice_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("Invoice_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int ORDER_MAILTEXT_ID_AD_Reference_ID=274;
/** Set Order Mail Text.
Email text used for sending order acknowledgements or quotations */
public void setOrder_MailText_ID (int Order_MailText_ID)
{
if (Order_MailText_ID <= 0) set_Value ("Order_MailText_ID", null);
 else 
set_Value ("Order_MailText_ID", new Integer(Order_MailText_ID));
}
/** Get Order Mail Text.
Email text used for sending order acknowledgements or quotations */
public int getOrder_MailText_ID() 
{
Integer ii = (Integer)get_Value("Order_MailText_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ORDER_PRINTFORMAT_ID_AD_Reference_ID=262;
/** Set Order Print Format.
Print Format for Orders, Quotes, Offers */
public void setOrder_PrintFormat_ID (int Order_PrintFormat_ID)
{
if (Order_PrintFormat_ID <= 0) set_Value ("Order_PrintFormat_ID", null);
 else 
set_Value ("Order_PrintFormat_ID", new Integer(Order_PrintFormat_ID));
}
/** Get Order Print Format.
Print Format for Orders, Quotes, Offers */
public int getOrder_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("Order_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PROJECT_MAILTEXT_ID_AD_Reference_ID=274;
/** Set Project Mail Text.
Standard text for Project EMails */
public void setProject_MailText_ID (int Project_MailText_ID)
{
if (Project_MailText_ID <= 0) set_Value ("Project_MailText_ID", null);
 else 
set_Value ("Project_MailText_ID", new Integer(Project_MailText_ID));
}
/** Get Project Mail Text.
Standard text for Project EMails */
public int getProject_MailText_ID() 
{
Integer ii = (Integer)get_Value("Project_MailText_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PROJECT_PRINTFORMAT_ID_AD_Reference_ID=259;
/** Set Project Print Format.
Standard Project Print Format */
public void setProject_PrintFormat_ID (int Project_PrintFormat_ID)
{
if (Project_PrintFormat_ID <= 0) set_Value ("Project_PrintFormat_ID", null);
 else 
set_Value ("Project_PrintFormat_ID", new Integer(Project_PrintFormat_ID));
}
/** Get Project Print Format.
Standard Project Print Format */
public int getProject_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("Project_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int REMITTANCE_MAILTEXT_ID_AD_Reference_ID=274;
/** Set Remittance Mail Text.
Email text used for sending payment remittances */
public void setRemittance_MailText_ID (int Remittance_MailText_ID)
{
if (Remittance_MailText_ID <= 0) set_Value ("Remittance_MailText_ID", null);
 else 
set_Value ("Remittance_MailText_ID", new Integer(Remittance_MailText_ID));
}
/** Get Remittance Mail Text.
Email text used for sending payment remittances */
public int getRemittance_MailText_ID() 
{
Integer ii = (Integer)get_Value("Remittance_MailText_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int REMITTANCE_PRINTFORMAT_ID_AD_Reference_ID=268;
/** Set Remittance Print Format.
Print Format for separate Remittances */
public void setRemittance_PrintFormat_ID (int Remittance_PrintFormat_ID)
{
if (Remittance_PrintFormat_ID <= 0) set_Value ("Remittance_PrintFormat_ID", null);
 else 
set_Value ("Remittance_PrintFormat_ID", new Integer(Remittance_PrintFormat_ID));
}
/** Get Remittance Print Format.
Print Format for separate Remittances */
public int getRemittance_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("Remittance_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SHIPMENT_MAILTEXT_ID_AD_Reference_ID=274;
/** Set Shipment Mail Text.
Email text used for sending delivery notes */
public void setShipment_MailText_ID (int Shipment_MailText_ID)
{
if (Shipment_MailText_ID <= 0) set_Value ("Shipment_MailText_ID", null);
 else 
set_Value ("Shipment_MailText_ID", new Integer(Shipment_MailText_ID));
}
/** Get Shipment Mail Text.
Email text used for sending delivery notes */
public int getShipment_MailText_ID() 
{
Integer ii = (Integer)get_Value("Shipment_MailText_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SHIPMENT_PRINTFORMAT_ID_AD_Reference_ID=263;
/** Set Shipment Print Format.
Print Format for Shipments, Receipts, Pick Lists */
public void setShipment_PrintFormat_ID (int Shipment_PrintFormat_ID)
{
if (Shipment_PrintFormat_ID <= 0) set_Value ("Shipment_PrintFormat_ID", null);
 else 
set_Value ("Shipment_PrintFormat_ID", new Integer(Shipment_PrintFormat_ID));
}
/** Get Shipment Print Format.
Print Format for Shipments, Receipts, Pick Lists */
public int getShipment_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("Shipment_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
