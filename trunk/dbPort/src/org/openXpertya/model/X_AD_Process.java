/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Process
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-01-16 19:12:19.061 */
public class X_AD_Process extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Process (Properties ctx, int AD_Process_ID, String trxName)
{
super (ctx, AD_Process_ID, trxName);
/** if (AD_Process_ID == 0)
{
setAccessLevel (null);
setAD_Process_ID (0);
setEntityType (null);	// U
setIsBetaFunctionality (false);
setIsReport (false);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_AD_Process (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Process");

/** TableName=AD_Process */
public static final String Table_Name="AD_Process";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Process");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Process[").append(getID()).append("]");
return sb.toString();
}
public static final int ACCESSLEVEL_AD_Reference_ID = MReference.getReferenceID("AD_Table Access Levels");
/** Organization = 1 */
public static final String ACCESSLEVEL_Organization = "1";
/** Client+Organization = 3 */
public static final String ACCESSLEVEL_ClientPlusOrganization = "3";
/** System only = 4 */
public static final String ACCESSLEVEL_SystemOnly = "4";
/** All = 7 */
public static final String ACCESSLEVEL_All = "7";
/** Client only = 2 */
public static final String ACCESSLEVEL_ClientOnly = "2";
/** System+Client = 6 */
public static final String ACCESSLEVEL_SystemPlusClient = "6";
/** Set Data Access Level.
Access Level required */
public void setAccessLevel (String AccessLevel)
{
if (AccessLevel.equals("1") || AccessLevel.equals("3") || AccessLevel.equals("4") || AccessLevel.equals("7") || AccessLevel.equals("2") || AccessLevel.equals("6"));
 else throw new IllegalArgumentException ("AccessLevel Invalid value - Reference = ACCESSLEVEL_AD_Reference_ID - 1 - 3 - 4 - 7 - 2 - 6");
if (AccessLevel == null) throw new IllegalArgumentException ("AccessLevel is mandatory");
if (AccessLevel.length() > 1)
{
log.warning("Length > 1 - truncated");
AccessLevel = AccessLevel.substring(0,1);
}
set_Value ("AccessLevel", AccessLevel);
}
/** Get Data Access Level.
Access Level required */
public String getAccessLevel() 
{
return (String)get_Value("AccessLevel");
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
/** Set Special Form.
Special Form */
public void setAD_Form_ID (int AD_Form_ID)
{
if (AD_Form_ID <= 0) set_Value ("AD_Form_ID", null);
 else 
set_Value ("AD_Form_ID", new Integer(AD_Form_ID));
}
/** Get Special Form.
Special Form */
public int getAD_Form_ID() 
{
Integer ii = (Integer)get_Value("AD_Form_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_JasperReport_ID */
public void setAD_JasperReport_ID (int AD_JasperReport_ID)
{
if (AD_JasperReport_ID <= 0) set_Value ("AD_JasperReport_ID", null);
 else 
set_Value ("AD_JasperReport_ID", new Integer(AD_JasperReport_ID));
}
/** Get AD_JasperReport_ID */
public int getAD_JasperReport_ID() 
{
Integer ii = (Integer)get_Value("AD_JasperReport_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Print Format.
Data Print Format */
public void setAD_PrintFormat_ID (int AD_PrintFormat_ID)
{
if (AD_PrintFormat_ID <= 0) set_Value ("AD_PrintFormat_ID", null);
 else 
set_Value ("AD_PrintFormat_ID", new Integer(AD_PrintFormat_ID));
}
/** Get Print Format.
Data Print Format */
public int getAD_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Process.
Process or Report */
public void setAD_Process_ID (int AD_Process_ID)
{
set_ValueNoCheck ("AD_Process_ID", new Integer(AD_Process_ID));
}
/** Get Process.
Process or Report */
public int getAD_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report View.
View used to generate this report */
public void setAD_ReportView_ID (int AD_ReportView_ID)
{
if (AD_ReportView_ID <= 0) set_Value ("AD_ReportView_ID", null);
 else 
set_Value ("AD_ReportView_ID", new Integer(AD_ReportView_ID));
}
/** Get Report View.
View used to generate this report */
public int getAD_ReportView_ID() 
{
Integer ii = (Integer)get_Value("AD_ReportView_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Workflow.
Workflow or combination of tasks */
public void setAD_Workflow_ID (int AD_Workflow_ID)
{
if (AD_Workflow_ID <= 0) set_Value ("AD_Workflow_ID", null);
 else 
set_Value ("AD_Workflow_ID", new Integer(AD_Workflow_ID));
}
/** Get Workflow.
Workflow or combination of tasks */
public int getAD_Workflow_ID() 
{
Integer ii = (Integer)get_Value("AD_Workflow_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Classname.
Java Classname */
public void setClassname (String Classname)
{
if (Classname != null && Classname.length() > 255)
{
log.warning("Length > 255 - truncated");
Classname = Classname.substring(0,255);
}
set_Value ("Classname", Classname);
}
/** Get Classname.
Java Classname */
public String getClassname() 
{
return (String)get_Value("Classname");
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
/** Set DynamicReport */
public void setDynamicReport (boolean DynamicReport)
{
set_Value ("DynamicReport", new Boolean(DynamicReport));
}
/** Get DynamicReport */
public boolean isDynamicReport() 
{
Object oo = get_Value("DynamicReport");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int ENTITYTYPE_AD_Reference_ID = MReference.getReferenceID("_Entity Type");
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("A") || EntityType.equals("C") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("CUST"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference = ENTITYTYPE_AD_Reference_ID - A - C - D - U - CUST");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 4)
{
log.warning("Length > 4 - truncated");
EntityType = EntityType.substring(0,4);
}
set_Value ("EntityType", EntityType);
}
/** Get Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public String getEntityType() 
{
return (String)get_Value("EntityType");
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,2000);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set isAlwaysInClient */
public void setisAlwaysInClient (boolean isAlwaysInClient)
{
set_Value ("isAlwaysInClient", new Boolean(isAlwaysInClient));
}
/** Get isAlwaysInClient */
public boolean isAlwaysInClient() 
{
Object oo = get_Value("isAlwaysInClient");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Beta Functionality.
This functionality is considered Beta */
public void setIsBetaFunctionality (boolean IsBetaFunctionality)
{
set_Value ("IsBetaFunctionality", new Boolean(IsBetaFunctionality));
}
/** Get Beta Functionality.
This functionality is considered Beta */
public boolean isBetaFunctionality() 
{
Object oo = get_Value("IsBetaFunctionality");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Direct print.
Print without dialog */
public void setIsDirectPrint (boolean IsDirectPrint)
{
set_Value ("IsDirectPrint", new Boolean(IsDirectPrint));
}
/** Get Direct print.
Print without dialog */
public boolean isDirectPrint() 
{
Object oo = get_Value("IsDirectPrint");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsJasperReport */
public void setIsJasperReport (boolean IsJasperReport)
{
set_Value ("IsJasperReport", new Boolean(IsJasperReport));
}
/** Get IsJasperReport */
public boolean isJasperReport() 
{
Object oo = get_Value("IsJasperReport");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Report.
Indicates a Report record */
public void setIsReport (boolean IsReport)
{
set_Value ("IsReport", new Boolean(IsReport));
}
/** Get Report.
Indicates a Report record */
public boolean isReport() 
{
Object oo = get_Value("IsReport");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set JasperReport.
Dynamic JasperReport */
public void setJasperReport (String JasperReport)
{
if (JasperReport != null && JasperReport.length() > 255)
{
log.warning("Length > 255 - truncated");
JasperReport = JasperReport.substring(0,255);
}
set_Value ("JasperReport", JasperReport);
}
/** Get JasperReport.
Dynamic JasperReport */
public String getJasperReport() 
{
return (String)get_Value("JasperReport");
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
/** Set Procedure.
Name of the Database Procedure */
public void setProcedureName (String ProcedureName)
{
if (ProcedureName != null && ProcedureName.length() > 60)
{
log.warning("Length > 60 - truncated");
ProcedureName = ProcedureName.substring(0,60);
}
set_Value ("ProcedureName", ProcedureName);
}
/** Get Procedure.
Name of the Database Procedure */
public String getProcedureName() 
{
return (String)get_Value("ProcedureName");
}
/** Set Statistic Count.
Internal statistics how often the entity was used */
public void setStatistic_Count (int Statistic_Count)
{
set_Value ("Statistic_Count", new Integer(Statistic_Count));
}
/** Get Statistic Count.
Internal statistics how often the entity was used */
public int getStatistic_Count() 
{
Integer ii = (Integer)get_Value("Statistic_Count");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Statistic Seconds.
Internal statistics how many seconds a process took */
public void setStatistic_Seconds (int Statistic_Seconds)
{
set_Value ("Statistic_Seconds", new Integer(Statistic_Seconds));
}
/** Get Statistic Seconds.
Internal statistics how many seconds a process took */
public int getStatistic_Seconds() 
{
Integer ii = (Integer)get_Value("Statistic_Seconds");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
/** Set Workflow Key.
Key of the Workflow to start */
public void setWorkflowValue (String WorkflowValue)
{
if (WorkflowValue != null && WorkflowValue.length() > 40)
{
log.warning("Length > 40 - truncated");
WorkflowValue = WorkflowValue.substring(0,40);
}
set_Value ("WorkflowValue", WorkflowValue);
}
/** Get Workflow Key.
Key of the Workflow to start */
public String getWorkflowValue() 
{
return (String)get_Value("WorkflowValue");
}
}
