/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Role
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-03-18 12:40:30.683 */
public class X_AD_Role extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Role (Properties ctx, int AD_Role_ID, String trxName)
{
super (ctx, AD_Role_ID, trxName);
/** if (AD_Role_ID == 0)
{
setAD_Role_ID (0);
setIsAccessAllOrgs (false);	// N
setIsCanApproveOwnDoc (false);
setIsCanExport (true);	// Y
setIsCanReport (true);	// Y
setIsChangeLog (false);	// N
setIsInfoProductAccess (true);	// Y
setIsManual (false);
setIsPersonalAccess (false);	// N
setIsPersonalLock (false);	// N
setIsShowAcct (false);	// N
setIsUseUserOrgAccess (false);	// N
setName (null);
setOverwritePriceLimit (false);	// N
setPreferenceType (null);	// O
}
 */
}
/** Load Constructor */
public X_AD_Role (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Role");

/** TableName=AD_Role */
public static final String Table_Name="AD_Role";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Role");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Role[").append(getID()).append("]");
return sb.toString();
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
set_ValueNoCheck ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_MENU_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Menu Tree.
Tree of the menu */
public void setAD_Tree_Menu_ID (int AD_Tree_Menu_ID)
{
if (AD_Tree_Menu_ID <= 0) set_Value ("AD_Tree_Menu_ID", null);
 else 
set_Value ("AD_Tree_Menu_ID", new Integer(AD_Tree_Menu_ID));
}
/** Get Menu Tree.
Tree of the menu */
public int getAD_Tree_Menu_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_Menu_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_ORG_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Organization Tree.
Tree to determine organizational hierarchy */
public void setAD_Tree_Org_ID (int AD_Tree_Org_ID)
{
if (AD_Tree_Org_ID <= 0) set_Value ("AD_Tree_Org_ID", null);
 else 
set_Value ("AD_Tree_Org_ID", new Integer(AD_Tree_Org_ID));
}
/** Get Organization Tree.
Tree to determine organizational hierarchy */
public int getAD_Tree_Org_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_Org_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Approval Amount.
The approval amount limit for this role */
public void setAmtApproval (BigDecimal AmtApproval)
{
set_Value ("AmtApproval", AmtApproval);
}
/** Get Approval Amount.
The approval amount limit for this role */
public BigDecimal getAmtApproval() 
{
BigDecimal bd = (BigDecimal)get_Value("AmtApproval");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
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
/** Set Access all Orgs.
Access all Organizations (no org access control) of the client */
public void setIsAccessAllOrgs (boolean IsAccessAllOrgs)
{
set_Value ("IsAccessAllOrgs", new Boolean(IsAccessAllOrgs));
}
/** Get Access all Orgs.
Access all Organizations (no org access control) of the client */
public boolean isAccessAllOrgs() 
{
Object oo = get_Value("IsAccessAllOrgs");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Approve own Documents.
Users with this role can approve their own documents */
public void setIsCanApproveOwnDoc (boolean IsCanApproveOwnDoc)
{
set_Value ("IsCanApproveOwnDoc", new Boolean(IsCanApproveOwnDoc));
}
/** Get Approve own Documents.
Users with this role can approve their own documents */
public boolean isCanApproveOwnDoc() 
{
Object oo = get_Value("IsCanApproveOwnDoc");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Can Export.
Users with this role can export data */
public void setIsCanExport (boolean IsCanExport)
{
set_Value ("IsCanExport", new Boolean(IsCanExport));
}
/** Get Can Export.
Users with this role can export data */
public boolean isCanExport() 
{
Object oo = get_Value("IsCanExport");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Can Report.
Users with this role can create reports */
public void setIsCanReport (boolean IsCanReport)
{
set_Value ("IsCanReport", new Boolean(IsCanReport));
}
/** Get Can Report.
Users with this role can create reports */
public boolean isCanReport() 
{
Object oo = get_Value("IsCanReport");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Maintain Change Log.
Maintain a log of changes */
public void setIsChangeLog (boolean IsChangeLog)
{
set_Value ("IsChangeLog", new Boolean(IsChangeLog));
}
/** Get Maintain Change Log.
Maintain a log of changes */
public boolean isChangeLog() 
{
Object oo = get_Value("IsChangeLog");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Info Product Access.
Info Product Access */
public void setIsInfoProductAccess (boolean IsInfoProductAccess)
{
set_Value ("IsInfoProductAccess", new Boolean(IsInfoProductAccess));
}
/** Get Info Product Access.
Info Product Access */
public boolean isInfoProductAccess() 
{
Object oo = get_Value("IsInfoProductAccess");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Manual.
This is a manual process */
public void setIsManual (boolean IsManual)
{
set_Value ("IsManual", new Boolean(IsManual));
}
/** Get Manual.
This is a manual process */
public boolean isManual() 
{
Object oo = get_Value("IsManual");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Personal Access.
Allow access to all personal records */
public void setIsPersonalAccess (boolean IsPersonalAccess)
{
set_Value ("IsPersonalAccess", new Boolean(IsPersonalAccess));
}
/** Get Personal Access.
Allow access to all personal records */
public boolean isPersonalAccess() 
{
Object oo = get_Value("IsPersonalAccess");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Personal Lock.
Allow users with role to lock access to personal records */
public void setIsPersonalLock (boolean IsPersonalLock)
{
set_Value ("IsPersonalLock", new Boolean(IsPersonalLock));
}
/** Get Personal Lock.
Allow users with role to lock access to personal records */
public boolean isPersonalLock() 
{
Object oo = get_Value("IsPersonalLock");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Show Accounting.
Users with this role can see accounting information */
public void setIsShowAcct (boolean IsShowAcct)
{
set_Value ("IsShowAcct", new Boolean(IsShowAcct));
}
/** Get Show Accounting.
Users with this role can see accounting information */
public boolean isShowAcct() 
{
Object oo = get_Value("IsShowAcct");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Use User Org Access.
Use Org Access defined by user instead of Role Org Access */
public void setIsUseUserOrgAccess (boolean IsUseUserOrgAccess)
{
set_Value ("IsUseUserOrgAccess", new Boolean(IsUseUserOrgAccess));
}
/** Get Use User Org Access.
Use Org Access defined by user instead of Role Org Access */
public boolean isUseUserOrgAccess() 
{
Object oo = get_Value("IsUseUserOrgAccess");
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
/** Set Overwrite Price Limit.
Overwrite Price Limit if the Price List  enforces the Price Limit */
public void setOverwritePriceLimit (boolean OverwritePriceLimit)
{
set_Value ("OverwritePriceLimit", new Boolean(OverwritePriceLimit));
}
/** Get Overwrite Price Limit.
Overwrite Price Limit if the Price List  enforces the Price Limit */
public boolean isOverwritePriceLimit() 
{
Object oo = get_Value("OverwritePriceLimit");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int PREFERENCETYPE_AD_Reference_ID = MReference.getReferenceID("AD_Role PreferenceType");
/** Client = C */
public static final String PREFERENCETYPE_Client = "C";
/** Organization = O */
public static final String PREFERENCETYPE_Organization = "O";
/** User = U */
public static final String PREFERENCETYPE_User = "U";
/** None = N */
public static final String PREFERENCETYPE_None = "N";
/** Set Preference Level.
Determines what preferences the user can set */
public void setPreferenceType (String PreferenceType)
{
if (PreferenceType.equals("C") || PreferenceType.equals("O") || PreferenceType.equals("U") || PreferenceType.equals("N"));
 else throw new IllegalArgumentException ("PreferenceType Invalid value - Reference = PREFERENCETYPE_AD_Reference_ID - C - O - U - N");
if (PreferenceType == null) throw new IllegalArgumentException ("PreferenceType is mandatory");
if (PreferenceType.length() > 1)
{
log.warning("Length > 1 - truncated");
PreferenceType = PreferenceType.substring(0,1);
}
set_Value ("PreferenceType", PreferenceType);
}
/** Get Preference Level.
Determines what preferences the user can set */
public String getPreferenceType() 
{
return (String)get_Value("PreferenceType");
}
public static final int SUPERVISOR_ID_AD_Reference_ID = MReference.getReferenceID("AD_User - Internal");
/** Set Supervisor.
Supervisor for this user/organization - used for escalation and approval */
public void setSupervisor_ID (int Supervisor_ID)
{
if (Supervisor_ID <= 0) set_Value ("Supervisor_ID", null);
 else 
set_Value ("Supervisor_ID", new Integer(Supervisor_ID));
}
/** Get Supervisor.
Supervisor for this user/organization - used for escalation and approval */
public int getSupervisor_ID() 
{
Integer ii = (Integer)get_Value("Supervisor_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int USERLEVEL_AD_Reference_ID = MReference.getReferenceID("AD_Role User Level");
/** All = SCO */
public static final String USERLEVEL_All = "SCO";
/** Client =  C  */
public static final String USERLEVEL_Client = " C ";
/** Client+Organization =  CO */
public static final String USERLEVEL_ClientPlusOrganization = " CO";
/** Organization =   O */
public static final String USERLEVEL_Organization = "  O";
/** System = S   */
public static final String USERLEVEL_System = "S  ";
/** Set User Level.
System Client Organization */
public void setUserLevel (String UserLevel)
{
if (UserLevel == null || UserLevel.equals("SCO") || UserLevel.equals(" C ") || UserLevel.equals(" CO") || UserLevel.equals("  O") || UserLevel.equals("S  "));
 else throw new IllegalArgumentException ("UserLevel Invalid value - Reference = USERLEVEL_AD_Reference_ID - SCO -  C  -  CO -   O - S  ");
if (UserLevel != null && UserLevel.length() > 3)
{
log.warning("Length > 3 - truncated");
UserLevel = UserLevel.substring(0,3);
}
set_Value ("UserLevel", UserLevel);
}
/** Get User Level.
System Client Organization */
public String getUserLevel() 
{
return (String)get_Value("UserLevel");
}
}
