/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_ReportSource
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:37.928 */
public class X_PA_ReportSource extends PO
{
/** Constructor estándar */
public X_PA_ReportSource (Properties ctx, int PA_ReportSource_ID, String trxName)
{
super (ctx, PA_ReportSource_ID, trxName);
/** if (PA_ReportSource_ID == 0)
{
setElementType (null);
setPA_ReportLine_ID (0);
setPA_ReportSource_ID (0);
}
 */
}
/** Load Constructor */
public X_PA_ReportSource (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=450 */
public static final int Table_ID=450;

/** TableName=PA_ReportSource */
public static final String Table_Name="PA_ReportSource";

protected static KeyNamePair Model = new KeyNamePair(450,"PA_ReportSource");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_ReportSource[").append(getID()).append("]");
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
/** Set Activity.
Business Activity */
public void setC_Activity_ID (int C_Activity_ID)
{
if (C_Activity_ID <= 0) set_Value ("C_Activity_ID", null);
 else 
set_Value ("C_Activity_ID", new Integer(C_Activity_ID));
}
/** Get Activity.
Business Activity */
public int getC_Activity_ID() 
{
Integer ii = (Integer)get_Value("C_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Campaign.
Marketing Campaign */
public void setC_Campaign_ID (int C_Campaign_ID)
{
if (C_Campaign_ID <= 0) set_Value ("C_Campaign_ID", null);
 else 
set_Value ("C_Campaign_ID", new Integer(C_Campaign_ID));
}
/** Get Campaign.
Marketing Campaign */
public int getC_Campaign_ID() 
{
Integer ii = (Integer)get_Value("C_Campaign_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_ELEMENTVALUE_ID_AD_Reference_ID=273;
/** Set Account Element.
Account Element */
public void setC_ElementValue_ID (int C_ElementValue_ID)
{
if (C_ElementValue_ID <= 0) set_Value ("C_ElementValue_ID", null);
 else 
set_Value ("C_ElementValue_ID", new Integer(C_ElementValue_ID));
}
/** Get Account Element.
Account Element */
public int getC_ElementValue_ID() 
{
Integer ii = (Integer)get_Value("C_ElementValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Address.
Location or Address */
public void setC_Location_ID (int C_Location_ID)
{
if (C_Location_ID <= 0) set_Value ("C_Location_ID", null);
 else 
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get Address.
Location or Address */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
if (C_Project_ID <= 0) set_Value ("C_Project_ID", null);
 else 
set_Value ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sales Region.
Sales coverage region */
public void setC_SalesRegion_ID (int C_SalesRegion_ID)
{
if (C_SalesRegion_ID <= 0) set_Value ("C_SalesRegion_ID", null);
 else 
set_Value ("C_SalesRegion_ID", new Integer(C_SalesRegion_ID));
}
/** Get Sales Region.
Sales coverage region */
public int getC_SalesRegion_ID() 
{
Integer ii = (Integer)get_Value("C_SalesRegion_ID");
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
public static final int ELEMENTTYPE_AD_Reference_ID=181;
/** Activity = AY */
public static final String ELEMENTTYPE_Activity = "AY";
/** Org = OO */
public static final String ELEMENTTYPE_Org = "OO";
/** Account = AC */
public static final String ELEMENTTYPE_Account = "AC";
/** Product = PR */
public static final String ELEMENTTYPE_Product = "PR";
/** BPartner = BP */
public static final String ELEMENTTYPE_BPartner = "BP";
/** Org Trx = OT */
public static final String ELEMENTTYPE_OrgTrx = "OT";
/** Location From = LF */
public static final String ELEMENTTYPE_LocationFrom = "LF";
/** Location To = LT */
public static final String ELEMENTTYPE_LocationTo = "LT";
/** Sales Region = SR */
public static final String ELEMENTTYPE_SalesRegion = "SR";
/** Project = PJ */
public static final String ELEMENTTYPE_Project = "PJ";
/** Campaign = MC */
public static final String ELEMENTTYPE_Campaign = "MC";
/** User 1 = U1 */
public static final String ELEMENTTYPE_User1 = "U1";
/** User 2 = U2 */
public static final String ELEMENTTYPE_User2 = "U2";
/** Set Type.
Element Type (account or user defined) */
public void setElementType (String ElementType)
{
if (ElementType.equals("AY") || ElementType.equals("OO") || ElementType.equals("AC") || ElementType.equals("PR") || ElementType.equals("BP") || ElementType.equals("OT") || ElementType.equals("LF") || ElementType.equals("LT") || ElementType.equals("SR") || ElementType.equals("PJ") || ElementType.equals("MC") || ElementType.equals("U1") || ElementType.equals("U2"));
 else throw new IllegalArgumentException ("ElementType Invalid value - Reference_ID=181 - AY - OO - AC - PR - BP - OT - LF - LT - SR - PJ - MC - U1 - U2");
if (ElementType == null) throw new IllegalArgumentException ("ElementType is mandatory");
if (ElementType.length() > 2)
{
log.warning("Length > 2 - truncated");
ElementType = ElementType.substring(0,2);
}
set_Value ("ElementType", ElementType);
}
/** Get Type.
Element Type (account or user defined) */
public String getElementType() 
{
return (String)get_Value("ElementType");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getElementType()));
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ORG_ID_AD_Reference_ID=130;
/** Set Organization.
Organizational entity within client */
public void setOrg_ID (int Org_ID)
{
if (Org_ID <= 0) set_Value ("Org_ID", null);
 else 
set_Value ("Org_ID", new Integer(Org_ID));
}
/** Get Organization.
Organizational entity within client */
public int getOrg_ID() 
{
Integer ii = (Integer)get_Value("Org_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report Line */
public void setPA_ReportLine_ID (int PA_ReportLine_ID)
{
set_ValueNoCheck ("PA_ReportLine_ID", new Integer(PA_ReportLine_ID));
}
/** Get Report Line */
public int getPA_ReportLine_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report Source.
Restriction of what will be shown in Report Line */
public void setPA_ReportSource_ID (int PA_ReportSource_ID)
{
set_ValueNoCheck ("PA_ReportSource_ID", new Integer(PA_ReportSource_ID));
}
/** Get Report Source.
Restriction of what will be shown in Report Line */
public int getPA_ReportSource_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportSource_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
