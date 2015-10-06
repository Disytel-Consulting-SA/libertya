/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_OrgInfo
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-10-06 11:47:22.888 */
public class X_AD_OrgInfo extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_OrgInfo (Properties ctx, int AD_OrgInfo_ID, String trxName)
{
super (ctx, AD_OrgInfo_ID, trxName);
/** if (AD_OrgInfo_ID == 0)
{
setAllowAutomaticAllocation (false);
setAuthorizations (false);
setCheckCuitControl (false);
setDUNS (null);
setInitialCheckLimit (Env.ZERO);
setOverdueInvoicesCharge (Env.ZERO);
setShipmentPermitFE (false);
setTaxID (null);
}
 */
}
/** Load Constructor */
public X_AD_OrgInfo (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_OrgInfo");

/** TableName=AD_OrgInfo */
public static final String Table_Name="AD_OrgInfo";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_OrgInfo");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_OrgInfo[").append(getID()).append("]");
return sb.toString();
}
/** Set Organization Type.
Organization Type allows you to categorize your organizations */
public void setAD_OrgType_ID (int AD_OrgType_ID)
{
if (AD_OrgType_ID <= 0) set_Value ("AD_OrgType_ID", null);
 else 
set_Value ("AD_OrgType_ID", new Integer(AD_OrgType_ID));
}
/** Get Organization Type.
Organization Type allows you to categorize your organizations */
public int getAD_OrgType_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Allow Automatic Allocation */
public void setAllowAutomaticAllocation (boolean AllowAutomaticAllocation)
{
set_Value ("AllowAutomaticAllocation", new Boolean(AllowAutomaticAllocation));
}
/** Get Allow Automatic Allocation */
public boolean isAllowAutomaticAllocation() 
{
Object oo = get_Value("AllowAutomaticAllocation");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Authorizations */
public void setAuthorizations (boolean Authorizations)
{
set_Value ("Authorizations", new Boolean(Authorizations));
}
/** Get Authorizations */
public boolean isAuthorizations() 
{
Object oo = get_Value("Authorizations");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int C_CATEGORIA_IVA_ID_AD_Reference_ID = MReference.getReferenceID("C_Categoria_IVA");
/** Set C_Categoria_IVA_ID */
public void setC_Categoria_IVA_ID (int C_Categoria_IVA_ID)
{
if (C_Categoria_IVA_ID <= 0) set_Value ("C_Categoria_IVA_ID", null);
 else 
set_Value ("C_Categoria_IVA_ID", new Integer(C_Categoria_IVA_ID));
}
/** Get C_Categoria_IVA_ID */
public int getC_Categoria_IVA_ID() 
{
Integer ii = (Integer)get_Value("C_Categoria_IVA_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Check CUIT Control */
public void setCheckCuitControl (boolean CheckCuitControl)
{
set_Value ("CheckCuitControl", new Boolean(CheckCuitControl));
}
/** Get Check CUIT Control */
public boolean isCheckCuitControl() 
{
Object oo = get_Value("CheckCuitControl");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Cod_INE */
public void setCod_INE (int Cod_INE)
{
set_Value ("Cod_INE", new Integer(Cod_INE));
}
/** Get Cod_INE */
public int getCod_INE() 
{
Integer ii = (Integer)get_Value("Cod_INE");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT != null && CUIT.length() > 13)
{
log.warning("Length > 13 - truncated");
CUIT = CUIT.substring(0,13);
}
set_Value ("CUIT", CUIT);
}
/** Get CUIT */
public String getCUIT() 
{
return (String)get_Value("CUIT");
}
/** Set D-U-N-S.
Dun & Bradstreet Number */
public void setDUNS (String DUNS)
{
if (DUNS == null) throw new IllegalArgumentException ("DUNS is mandatory");
if (DUNS.length() > 11)
{
log.warning("Length > 11 - truncated");
DUNS = DUNS.substring(0,11);
}
set_Value ("DUNS", DUNS);
}
/** Get D-U-N-S.
Dun & Bradstreet Number */
public String getDUNS() 
{
return (String)get_Value("DUNS");
}
public static final int EXPORTTYPEFE_AD_Reference_ID = MReference.getReferenceID("Tipo de Exportación Electrónica");
/** Exportación definitiva de Bienes = 1 */
public static final String EXPORTTYPEFE_ExportaciónDefinitivaDeBienes = "1";
/** Servicios = 2 */
public static final String EXPORTTYPEFE_Servicios = "2";
/** Otros = 4 */
public static final String EXPORTTYPEFE_Otros = "4";
/** Set Export Type FE */
public void setExportTypeFE (String ExportTypeFE)
{
if (ExportTypeFE == null || ExportTypeFE.equals("1") || ExportTypeFE.equals("2") || ExportTypeFE.equals("4"));
 else throw new IllegalArgumentException ("ExportTypeFE Invalid value - Reference = EXPORTTYPEFE_AD_Reference_ID - 1 - 2 - 4");
if (ExportTypeFE != null && ExportTypeFE.length() > 1)
{
log.warning("Length > 1 - truncated");
ExportTypeFE = ExportTypeFE.substring(0,1);
}
set_Value ("ExportTypeFE", ExportTypeFE);
}
/** Get Export Type FE */
public String getExportTypeFE() 
{
return (String)get_Value("ExportTypeFE");
}
/** Set faxnumber */
public void setfaxnumber (String faxnumber)
{
if (faxnumber != null && faxnumber.length() > 22)
{
log.warning("Length > 22 - truncated");
faxnumber = faxnumber.substring(0,22);
}
set_Value ("faxnumber", faxnumber);
}
/** Get faxnumber */
public String getfaxnumber() 
{
return (String)get_Value("faxnumber");
}
/** Set Initial Check Limit */
public void setInitialCheckLimit (BigDecimal InitialCheckLimit)
{
if (InitialCheckLimit == null) throw new IllegalArgumentException ("InitialCheckLimit is mandatory");
set_Value ("InitialCheckLimit", InitialCheckLimit);
}
/** Get Initial Check Limit */
public BigDecimal getInitialCheckLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("InitialCheckLimit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
if (M_Warehouse_ID <= 0) set_Value ("M_Warehouse_ID", null);
 else 
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
/** Set Overdue Invoices Charge */
public void setOverdueInvoicesCharge (BigDecimal OverdueInvoicesCharge)
{
if (OverdueInvoicesCharge == null) throw new IllegalArgumentException ("OverdueInvoicesCharge is mandatory");
set_Value ("OverdueInvoicesCharge", OverdueInvoicesCharge);
}
/** Get Overdue Invoices Charge */
public BigDecimal getOverdueInvoicesCharge() 
{
BigDecimal bd = (BigDecimal)get_Value("OverdueInvoicesCharge");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Goal.
Performance Goal */
public void setPA_Goal_ID (int PA_Goal_ID)
{
if (PA_Goal_ID <= 0) set_Value ("PA_Goal_ID", null);
 else 
set_Value ("PA_Goal_ID", new Integer(PA_Goal_ID));
}
/** Get Goal.
Performance Goal */
public int getPA_Goal_ID() 
{
Integer ii = (Integer)get_Value("PA_Goal_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PARENT_ORG_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (Trx)");
/** Set Parent Organization.
Parent (superior) Organization  */
public void setParent_Org_ID (int Parent_Org_ID)
{
if (Parent_Org_ID <= 0) set_Value ("Parent_Org_ID", null);
 else 
set_Value ("Parent_Org_ID", new Integer(Parent_Org_ID));
}
/** Get Parent Organization.
Parent (superior) Organization  */
public int getParent_Org_ID() 
{
Integer ii = (Integer)get_Value("Parent_Org_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Register_Info */
public void setRegister_Info (String Register_Info)
{
if (Register_Info != null && Register_Info.length() > 255)
{
log.warning("Length > 255 - truncated");
Register_Info = Register_Info.substring(0,255);
}
set_Value ("Register_Info", Register_Info);
}
/** Get Register_Info */
public String getRegister_Info() 
{
return (String)get_Value("Register_Info");
}
/** Set Shipment Permit FE */
public void setShipmentPermitFE (boolean ShipmentPermitFE)
{
set_Value ("ShipmentPermitFE", new Boolean(ShipmentPermitFE));
}
/** Get Shipment Permit FE */
public boolean isShipmentPermitFE() 
{
Object oo = get_Value("ShipmentPermitFE");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Tax ID.
Tax Identification */
public void setTaxID (String TaxID)
{
if (TaxID == null) throw new IllegalArgumentException ("TaxID is mandatory");
if (TaxID.length() > 20)
{
log.warning("Length > 20 - truncated");
TaxID = TaxID.substring(0,20);
}
set_Value ("TaxID", TaxID);
}
/** Get Tax ID.
Tax Identification */
public String getTaxID() 
{
return (String)get_Value("TaxID");
}
/** Set telephone */
public void settelephone (String telephone)
{
if (telephone != null && telephone.length() > 22)
{
log.warning("Length > 22 - truncated");
telephone = telephone.substring(0,22);
}
set_Value ("telephone", telephone);
}
/** Get telephone */
public String gettelephone() 
{
return (String)get_Value("telephone");
}
}
