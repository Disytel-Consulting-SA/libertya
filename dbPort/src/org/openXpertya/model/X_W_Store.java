/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por W_Store
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.562 */
public class X_W_Store extends PO
{
/** Constructor estándar */
public X_W_Store (Properties ctx, int W_Store_ID, String trxName)
{
super (ctx, W_Store_ID, trxName);
/** if (W_Store_ID == 0)
{
setDocumentDir (null);
setIsMenuAssets (true);	// Y
setIsMenuContact (true);	// Y
setIsMenuInterests (true);	// Y
setIsMenuInvoices (true);	// Y
setIsMenuOrders (true);	// Y
setIsMenuPayments (true);	// Y
setIsMenuRegistrations (true);	// Y
setIsMenuRequests (true);	// Y
setIsMenuRfQs (true);	// Y
setIsMenuShipments (true);	// Y
setM_PriceList_ID (0);
setM_Warehouse_ID (0);
setName (null);
setSalesRep_ID (0);
setWStoreEMail (null);
setWStoreUser (null);
setWStoreUserPW (null);
setW_Store_ID (0);
setWebDir (null);
setWebInfo (null);
setWebParam1 (null);
setWebParam2 (null);
setWebParam3 (null);
setWebParam4 (null);
setWebParam5 (null);
setWebParam6 (null);
}
 */
}
/** Load Constructor */
public X_W_Store (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=778 */
public static final int Table_ID=778;

/** TableName=W_Store */
public static final String Table_Name="W_Store";

protected static KeyNamePair Model = new KeyNamePair(778,"W_Store");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_W_Store[").append(getID()).append("]");
return sb.toString();
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
/** Set Document Directory.
Directory for documents from the application server */
public void setDocumentDir (String DocumentDir)
{
if (DocumentDir == null) throw new IllegalArgumentException ("DocumentDir is mandatory");
if (DocumentDir.length() > 60)
{
log.warning("Length > 60 - truncated");
DocumentDir = DocumentDir.substring(0,59);
}
set_Value ("DocumentDir", DocumentDir);
}
/** Get Document Directory.
Directory for documents from the application server */
public String getDocumentDir() 
{
return (String)get_Value("DocumentDir");
}
/** Set EMail Footer.
Footer added to EMails */
public void setEMailFooter (String EMailFooter)
{
if (EMailFooter != null && EMailFooter.length() > 2000)
{
log.warning("Length > 2000 - truncated");
EMailFooter = EMailFooter.substring(0,1999);
}
set_Value ("EMailFooter", EMailFooter);
}
/** Get EMail Footer.
Footer added to EMails */
public String getEMailFooter() 
{
return (String)get_Value("EMailFooter");
}
/** Set EMail Header.
Header added to EMails */
public void setEMailHeader (String EMailHeader)
{
if (EMailHeader != null && EMailHeader.length() > 2000)
{
log.warning("Length > 2000 - truncated");
EMailHeader = EMailHeader.substring(0,1999);
}
set_Value ("EMailHeader", EMailHeader);
}
/** Get EMail Header.
Header added to EMails */
public String getEMailHeader() 
{
return (String)get_Value("EMailHeader");
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Menu Assets.
Show Menu Assets */
public void setIsMenuAssets (boolean IsMenuAssets)
{
set_Value ("IsMenuAssets", new Boolean(IsMenuAssets));
}
/** Get Menu Assets.
Show Menu Assets */
public boolean isMenuAssets() 
{
Object oo = get_Value("IsMenuAssets");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu Contact.
Show Menu Contact */
public void setIsMenuContact (boolean IsMenuContact)
{
set_Value ("IsMenuContact", new Boolean(IsMenuContact));
}
/** Get Menu Contact.
Show Menu Contact */
public boolean isMenuContact() 
{
Object oo = get_Value("IsMenuContact");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu Interests.
Show Menu Interests */
public void setIsMenuInterests (boolean IsMenuInterests)
{
set_Value ("IsMenuInterests", new Boolean(IsMenuInterests));
}
/** Get Menu Interests.
Show Menu Interests */
public boolean isMenuInterests() 
{
Object oo = get_Value("IsMenuInterests");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu Invoices.
Show Menu Invoices */
public void setIsMenuInvoices (boolean IsMenuInvoices)
{
set_Value ("IsMenuInvoices", new Boolean(IsMenuInvoices));
}
/** Get Menu Invoices.
Show Menu Invoices */
public boolean isMenuInvoices() 
{
Object oo = get_Value("IsMenuInvoices");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu Orders.
Show Menu Orders */
public void setIsMenuOrders (boolean IsMenuOrders)
{
set_Value ("IsMenuOrders", new Boolean(IsMenuOrders));
}
/** Get Menu Orders.
Show Menu Orders */
public boolean isMenuOrders() 
{
Object oo = get_Value("IsMenuOrders");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu Payments.
Show Menu Payments */
public void setIsMenuPayments (boolean IsMenuPayments)
{
set_Value ("IsMenuPayments", new Boolean(IsMenuPayments));
}
/** Get Menu Payments.
Show Menu Payments */
public boolean isMenuPayments() 
{
Object oo = get_Value("IsMenuPayments");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu Registrations.
Show Menu Registrations */
public void setIsMenuRegistrations (boolean IsMenuRegistrations)
{
set_Value ("IsMenuRegistrations", new Boolean(IsMenuRegistrations));
}
/** Get Menu Registrations.
Show Menu Registrations */
public boolean isMenuRegistrations() 
{
Object oo = get_Value("IsMenuRegistrations");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu Requests.
Show Menu Requests */
public void setIsMenuRequests (boolean IsMenuRequests)
{
set_Value ("IsMenuRequests", new Boolean(IsMenuRequests));
}
/** Get Menu Requests.
Show Menu Requests */
public boolean isMenuRequests() 
{
Object oo = get_Value("IsMenuRequests");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu RfQs.
Show Menu RfQs */
public void setIsMenuRfQs (boolean IsMenuRfQs)
{
set_Value ("IsMenuRfQs", new Boolean(IsMenuRfQs));
}
/** Get Menu RfQs.
Show Menu RfQs */
public boolean isMenuRfQs() 
{
Object oo = get_Value("IsMenuRfQs");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Menu Shipments.
Show Menu Shipments */
public void setIsMenuShipments (boolean IsMenuShipments)
{
set_Value ("IsMenuShipments", new Boolean(IsMenuShipments));
}
/** Get Menu Shipments.
Show Menu Shipments */
public boolean isMenuShipments() 
{
Object oo = get_Value("IsMenuShipments");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Price List.
Unique identifier of a Price List */
public void setM_PriceList_ID (int M_PriceList_ID)
{
set_Value ("M_PriceList_ID", new Integer(M_PriceList_ID));
}
/** Get Price List.
Unique identifier of a Price List */
public int getM_PriceList_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_Value ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
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
public static final int SALESREP_ID_AD_Reference_ID=190;
/** Set Sales Representative.
Sales Representative or Company Agent */
public void setSalesRep_ID (int SalesRep_ID)
{
set_Value ("SalesRep_ID", new Integer(SalesRep_ID));
}
/** Get Sales Representative.
Sales Representative or Company Agent */
public int getSalesRep_ID() 
{
Integer ii = (Integer)get_Value("SalesRep_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Web Store EMail.
EMail address used as the sender (From) */
public void setWStoreEMail (String WStoreEMail)
{
if (WStoreEMail == null) throw new IllegalArgumentException ("WStoreEMail is mandatory");
if (WStoreEMail.length() > 60)
{
log.warning("Length > 60 - truncated");
WStoreEMail = WStoreEMail.substring(0,59);
}
set_Value ("WStoreEMail", WStoreEMail);
}
/** Get Web Store EMail.
EMail address used as the sender (From) */
public String getWStoreEMail() 
{
return (String)get_Value("WStoreEMail");
}
/** Set WebStore User.
User ID of the Web Store EMail address */
public void setWStoreUser (String WStoreUser)
{
if (WStoreUser == null) throw new IllegalArgumentException ("WStoreUser is mandatory");
if (WStoreUser.length() > 60)
{
log.warning("Length > 60 - truncated");
WStoreUser = WStoreUser.substring(0,59);
}
set_Value ("WStoreUser", WStoreUser);
}
/** Get WebStore User.
User ID of the Web Store EMail address */
public String getWStoreUser() 
{
return (String)get_Value("WStoreUser");
}
/** Set WebStore Password.
Password of the Web Store EMail address */
public void setWStoreUserPW (String WStoreUserPW)
{
if (WStoreUserPW == null) throw new IllegalArgumentException ("WStoreUserPW is mandatory");
if (WStoreUserPW.length() > 20)
{
log.warning("Length > 20 - truncated");
WStoreUserPW = WStoreUserPW.substring(0,19);
}
set_Value ("WStoreUserPW", WStoreUserPW);
}
/** Get WebStore Password.
Password of the Web Store EMail address */
public String getWStoreUserPW() 
{
return (String)get_Value("WStoreUserPW");
}
/** Set Web Store.
A Web Store of the Client */
public void setW_Store_ID (int W_Store_ID)
{
set_ValueNoCheck ("W_Store_ID", new Integer(W_Store_ID));
}
/** Get Web Store.
A Web Store of the Client */
public int getW_Store_ID() 
{
Integer ii = (Integer)get_Value("W_Store_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Web Directory.
Web Interface  */
public void setWebDir (String WebDir)
{
if (WebDir == null) throw new IllegalArgumentException ("WebDir is mandatory");
if (WebDir.length() > 60)
{
log.warning("Length > 60 - truncated");
WebDir = WebDir.substring(0,59);
}
set_Value ("WebDir", WebDir);
}
/** Get Web Directory.
Web Interface  */
public String getWebDir() 
{
return (String)get_Value("WebDir");
}
/** Set Web Store Info.
Web Store Header Information */
public void setWebInfo (String WebInfo)
{
if (WebInfo == null) throw new IllegalArgumentException ("WebInfo is mandatory");
if (WebInfo.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebInfo = WebInfo.substring(0,1999);
}
set_Value ("WebInfo", WebInfo);
}
/** Get Web Store Info.
Web Store Header Information */
public String getWebInfo() 
{
return (String)get_Value("WebInfo");
}
/** Set Web Parameter 1.
Web Site Parameter 1 (default: header image) */
public void setWebParam1 (String WebParam1)
{
if (WebParam1 == null) throw new IllegalArgumentException ("WebParam1 is mandatory");
if (WebParam1.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam1 = WebParam1.substring(0,1999);
}
set_Value ("WebParam1", WebParam1);
}
/** Get Web Parameter 1.
Web Site Parameter 1 (default: header image) */
public String getWebParam1() 
{
return (String)get_Value("WebParam1");
}
/** Set Web Parameter 2.
Web Site Parameter 2 (default index page) */
public void setWebParam2 (String WebParam2)
{
if (WebParam2 == null) throw new IllegalArgumentException ("WebParam2 is mandatory");
if (WebParam2.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam2 = WebParam2.substring(0,1999);
}
set_Value ("WebParam2", WebParam2);
}
/** Get Web Parameter 2.
Web Site Parameter 2 (default index page) */
public String getWebParam2() 
{
return (String)get_Value("WebParam2");
}
/** Set Web Parameter 3.
Web Site Parameter 3 (default left - menu) */
public void setWebParam3 (String WebParam3)
{
if (WebParam3 == null) throw new IllegalArgumentException ("WebParam3 is mandatory");
if (WebParam3.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam3 = WebParam3.substring(0,1999);
}
set_Value ("WebParam3", WebParam3);
}
/** Get Web Parameter 3.
Web Site Parameter 3 (default left - menu) */
public String getWebParam3() 
{
return (String)get_Value("WebParam3");
}
/** Set Web Parameter 4.
Web Site Parameter 4 (default footer left) */
public void setWebParam4 (String WebParam4)
{
if (WebParam4 == null) throw new IllegalArgumentException ("WebParam4 is mandatory");
if (WebParam4.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam4 = WebParam4.substring(0,1999);
}
set_Value ("WebParam4", WebParam4);
}
/** Get Web Parameter 4.
Web Site Parameter 4 (default footer left) */
public String getWebParam4() 
{
return (String)get_Value("WebParam4");
}
/** Set Web Parameter 5.
Web Site Parameter 5 (default footer center) */
public void setWebParam5 (String WebParam5)
{
if (WebParam5 == null) throw new IllegalArgumentException ("WebParam5 is mandatory");
if (WebParam5.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam5 = WebParam5.substring(0,1999);
}
set_Value ("WebParam5", WebParam5);
}
/** Get Web Parameter 5.
Web Site Parameter 5 (default footer center) */
public String getWebParam5() 
{
return (String)get_Value("WebParam5");
}
/** Set Web Parameter 6.
Web Site Parameter 6 (default footer right) */
public void setWebParam6 (String WebParam6)
{
if (WebParam6 == null) throw new IllegalArgumentException ("WebParam6 is mandatory");
if (WebParam6.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam6 = WebParam6.substring(0,1999);
}
set_Value ("WebParam6", WebParam6);
}
/** Get Web Parameter 6.
Web Site Parameter 6 (default footer right) */
public String getWebParam6() 
{
return (String)get_Value("WebParam6");
}
}
