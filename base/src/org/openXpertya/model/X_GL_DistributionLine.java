/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por GL_DistributionLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:34.203 */
public class X_GL_DistributionLine extends PO
{
/** Constructor estándar */
public X_GL_DistributionLine (Properties ctx, int GL_DistributionLine_ID, String trxName)
{
super (ctx, GL_DistributionLine_ID, trxName);
/** if (GL_DistributionLine_ID == 0)
{
setGL_DistributionLine_ID (0);
setGL_Distribution_ID (0);
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM GL_DistributionLine WHERE GL_Distribution_ID=@GL_Distribution_ID@
setOverwriteAcct (false);
setOverwriteActivity (false);
setOverwriteBPartner (false);
setOverwriteCampaign (false);
setOverwriteLocFrom (false);
setOverwriteLocTo (false);
setOverwriteOrg (false);
setOverwriteOrgTrx (false);
setOverwriteProduct (false);
setOverwriteProject (false);
setOverwriteSalesRegion (false);
setOverwriteUser1 (false);
setOverwriteUser2 (false);
setPercent (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_GL_DistributionLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=707 */
public static final int Table_ID=707;

/** TableName=GL_DistributionLine */
public static final String Table_Name="GL_DistributionLine";

protected static KeyNamePair Model = new KeyNamePair(707,"GL_DistributionLine");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_GL_DistributionLine[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_ORGTRX_ID_AD_Reference_ID=130;
/** Set Trx Organization.
Performing or initiating organization */
public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
{
if (AD_OrgTrx_ID <= 0) set_Value ("AD_OrgTrx_ID", null);
 else 
set_Value ("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
}
/** Get Trx Organization.
Performing or initiating organization */
public int getAD_OrgTrx_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgTrx_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ACCOUNT_ID_AD_Reference_ID=132;
/** Set Account.
Account used */
public void setAccount_ID (int Account_ID)
{
if (Account_ID <= 0) set_Value ("Account_ID", null);
 else 
set_Value ("Account_ID", new Integer(Account_ID));
}
/** Get Account.
Account used */
public int getAccount_ID() 
{
Integer ii = (Integer)get_Value("Account_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int C_LOCFROM_ID_AD_Reference_ID=133;
/** Set Location From.
Location that inventory was moved from */
public void setC_LocFrom_ID (int C_LocFrom_ID)
{
if (C_LocFrom_ID <= 0) set_Value ("C_LocFrom_ID", null);
 else 
set_Value ("C_LocFrom_ID", new Integer(C_LocFrom_ID));
}
/** Get Location From.
Location that inventory was moved from */
public int getC_LocFrom_ID() 
{
Integer ii = (Integer)get_Value("C_LocFrom_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_LOCTO_ID_AD_Reference_ID=133;
/** Set Location To.
Location that inventory was moved to */
public void setC_LocTo_ID (int C_LocTo_ID)
{
if (C_LocTo_ID <= 0) set_Value ("C_LocTo_ID", null);
 else 
set_Value ("C_LocTo_ID", new Integer(C_LocTo_ID));
}
/** Get Location To.
Location that inventory was moved to */
public int getC_LocTo_ID() 
{
Integer ii = (Integer)get_Value("C_LocTo_ID");
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
/** Set GL Distribution Line.
General Ledger Distribution Line */
public void setGL_DistributionLine_ID (int GL_DistributionLine_ID)
{
set_ValueNoCheck ("GL_DistributionLine_ID", new Integer(GL_DistributionLine_ID));
}
/** Get GL Distribution Line.
General Ledger Distribution Line */
public int getGL_DistributionLine_ID() 
{
Integer ii = (Integer)get_Value("GL_DistributionLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set GL Distribution.
General Ledger Distribution */
public void setGL_Distribution_ID (int GL_Distribution_ID)
{
set_ValueNoCheck ("GL_Distribution_ID", new Integer(GL_Distribution_ID));
}
/** Get GL Distribution.
General Ledger Distribution */
public int getGL_Distribution_ID() 
{
Integer ii = (Integer)get_Value("GL_Distribution_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Line No.
Unique line for this document */
public void setLine (int Line)
{
set_Value ("Line", new Integer(Line));
}
/** Get Line No.
Unique line for this document */
public int getLine() 
{
Integer ii = (Integer)get_Value("Line");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getLine()));
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
/** Set Overwrite Account.
Overwrite the account segment Account with the value specified */
public void setOverwriteAcct (boolean OverwriteAcct)
{
set_Value ("OverwriteAcct", new Boolean(OverwriteAcct));
}
/** Get Overwrite Account.
Overwrite the account segment Account with the value specified */
public boolean isOverwriteAcct() 
{
Object oo = get_Value("OverwriteAcct");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Activity.
Overwrite the account segment Activity with the value specified */
public void setOverwriteActivity (boolean OverwriteActivity)
{
set_Value ("OverwriteActivity", new Boolean(OverwriteActivity));
}
/** Get Overwrite Activity.
Overwrite the account segment Activity with the value specified */
public boolean isOverwriteActivity() 
{
Object oo = get_Value("OverwriteActivity");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Bus.Partner.
Overwrite the account segment Business Partner with the value specified */
public void setOverwriteBPartner (boolean OverwriteBPartner)
{
set_Value ("OverwriteBPartner", new Boolean(OverwriteBPartner));
}
/** Get Overwrite Bus.Partner.
Overwrite the account segment Business Partner with the value specified */
public boolean isOverwriteBPartner() 
{
Object oo = get_Value("OverwriteBPartner");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Campaign.
Overwrite the account segment Campaign with the value specified */
public void setOverwriteCampaign (boolean OverwriteCampaign)
{
set_Value ("OverwriteCampaign", new Boolean(OverwriteCampaign));
}
/** Get Overwrite Campaign.
Overwrite the account segment Campaign with the value specified */
public boolean isOverwriteCampaign() 
{
Object oo = get_Value("OverwriteCampaign");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Location From.
Overwrite the account segment Location From with the value specified */
public void setOverwriteLocFrom (boolean OverwriteLocFrom)
{
set_Value ("OverwriteLocFrom", new Boolean(OverwriteLocFrom));
}
/** Get Overwrite Location From.
Overwrite the account segment Location From with the value specified */
public boolean isOverwriteLocFrom() 
{
Object oo = get_Value("OverwriteLocFrom");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Location To.
Overwrite the account segment Location From with the value specified */
public void setOverwriteLocTo (boolean OverwriteLocTo)
{
set_Value ("OverwriteLocTo", new Boolean(OverwriteLocTo));
}
/** Get Overwrite Location To.
Overwrite the account segment Location From with the value specified */
public boolean isOverwriteLocTo() 
{
Object oo = get_Value("OverwriteLocTo");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Organization.
Overwrite the account segment Organization with the value specified */
public void setOverwriteOrg (boolean OverwriteOrg)
{
set_Value ("OverwriteOrg", new Boolean(OverwriteOrg));
}
/** Get Overwrite Organization.
Overwrite the account segment Organization with the value specified */
public boolean isOverwriteOrg() 
{
Object oo = get_Value("OverwriteOrg");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Trx Organuzation.
Overwrite the account segment Transaction Organization with the value specified */
public void setOverwriteOrgTrx (boolean OverwriteOrgTrx)
{
set_Value ("OverwriteOrgTrx", new Boolean(OverwriteOrgTrx));
}
/** Get Overwrite Trx Organuzation.
Overwrite the account segment Transaction Organization with the value specified */
public boolean isOverwriteOrgTrx() 
{
Object oo = get_Value("OverwriteOrgTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Product.
Overwrite the account segment Product with the value specified */
public void setOverwriteProduct (boolean OverwriteProduct)
{
set_Value ("OverwriteProduct", new Boolean(OverwriteProduct));
}
/** Get Overwrite Product.
Overwrite the account segment Product with the value specified */
public boolean isOverwriteProduct() 
{
Object oo = get_Value("OverwriteProduct");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Project.
Overwrite the account segment Project with the value specified */
public void setOverwriteProject (boolean OverwriteProject)
{
set_Value ("OverwriteProject", new Boolean(OverwriteProject));
}
/** Get Overwrite Project.
Overwrite the account segment Project with the value specified */
public boolean isOverwriteProject() 
{
Object oo = get_Value("OverwriteProject");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite Sales Region.
Overwrite the account segment Sales Region with the value specified */
public void setOverwriteSalesRegion (boolean OverwriteSalesRegion)
{
set_Value ("OverwriteSalesRegion", new Boolean(OverwriteSalesRegion));
}
/** Get Overwrite Sales Region.
Overwrite the account segment Sales Region with the value specified */
public boolean isOverwriteSalesRegion() 
{
Object oo = get_Value("OverwriteSalesRegion");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite User1.
Overwrite the account segment User 1 with the value specified */
public void setOverwriteUser1 (boolean OverwriteUser1)
{
set_Value ("OverwriteUser1", new Boolean(OverwriteUser1));
}
/** Get Overwrite User1.
Overwrite the account segment User 1 with the value specified */
public boolean isOverwriteUser1() 
{
Object oo = get_Value("OverwriteUser1");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Overwrite User2.
Overwrite the account segment User 2 with the value specified */
public void setOverwriteUser2 (boolean OverwriteUser2)
{
set_Value ("OverwriteUser2", new Boolean(OverwriteUser2));
}
/** Get Overwrite User2.
Overwrite the account segment User 2 with the value specified */
public boolean isOverwriteUser2() 
{
Object oo = get_Value("OverwriteUser2");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Percent.
Percentage */
public void setPercent (BigDecimal Percent)
{
if (Percent == null) throw new IllegalArgumentException ("Percent is mandatory");
set_Value ("Percent", Percent);
}
/** Get Percent.
Percentage */
public BigDecimal getPercent() 
{
BigDecimal bd = (BigDecimal)get_Value("Percent");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int USER1_ID_AD_Reference_ID=134;
/** Set User1.
User defined element #1 */
public void setUser1_ID (int User1_ID)
{
if (User1_ID <= 0) set_Value ("User1_ID", null);
 else 
set_Value ("User1_ID", new Integer(User1_ID));
}
/** Get User1.
User defined element #1 */
public int getUser1_ID() 
{
Integer ii = (Integer)get_Value("User1_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int USER2_ID_AD_Reference_ID=137;
/** Set User2.
User defined element #2 */
public void setUser2_ID (int User2_ID)
{
if (User2_ID <= 0) set_Value ("User2_ID", null);
 else 
set_Value ("User2_ID", new Integer(User2_ID));
}
/** Get User2.
User defined element #2 */
public int getUser2_ID() 
{
Integer ii = (Integer)get_Value("User2_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
