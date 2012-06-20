/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RetencionSchema
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-09-19 14:59:15.957 */
public class X_C_RetencionSchema extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_RetencionSchema (Properties ctx, int C_RetencionSchema_ID, String trxName)
{
super (ctx, C_RetencionSchema_ID, trxName);
/** if (C_RetencionSchema_ID == 0)
{
setC_BPartner_Recaudador_ID (0);
setC_RetencionProcessor_ID (0);
setC_RetencionSchema_ID (0);
setC_RetencionType_ID (0);
setName (null);
setRetencionApplication (null);	// E
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_RetencionSchema (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_RetencionSchema");

/** TableName=C_RetencionSchema */
public static final String Table_Name="C_RetencionSchema";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_RetencionSchema");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RetencionSchema[").append(getID()).append("]");
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
public static final int C_BPARTNER_RECAUDADOR_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (No Summary)");
/** Set BPartner Recaudador */
public void setC_BPartner_Recaudador_ID (int C_BPartner_Recaudador_ID)
{
set_Value ("C_BPartner_Recaudador_ID", new Integer(C_BPartner_Recaudador_ID));
}
/** Get BPartner Recaudador */
public int getC_BPartner_Recaudador_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Recaudador_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_CREDIT_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Credit Document Type */
public void setC_DocType_Credit_ID (int C_DocType_Credit_ID)
{
if (C_DocType_Credit_ID <= 0) set_Value ("C_DocType_Credit_ID", null);
 else 
set_Value ("C_DocType_Credit_ID", new Integer(C_DocType_Credit_ID));
}
/** Get Credit Document Type */
public int getC_DocType_Credit_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_Credit_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_INVOICE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Invoice Document Type */
public void setC_DocType_Invoice_ID (int C_DocType_Invoice_ID)
{
if (C_DocType_Invoice_ID <= 0) set_Value ("C_DocType_Invoice_ID", null);
 else 
set_Value ("C_DocType_Invoice_ID", new Integer(C_DocType_Invoice_ID));
}
/** Get Invoice Document Type */
public int getC_DocType_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Region.
Identifies a geographical Region */
public void setC_Region_ID (int C_Region_ID)
{
if (C_Region_ID <= 0) set_Value ("C_Region_ID", null);
 else 
set_Value ("C_Region_ID", new Integer(C_Region_ID));
}
/** Get Region.
Identifies a geographical Region */
public int getC_Region_ID() 
{
Integer ii = (Integer)get_Value("C_Region_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Processor */
public void setC_RetencionProcessor_ID (int C_RetencionProcessor_ID)
{
set_Value ("C_RetencionProcessor_ID", new Integer(C_RetencionProcessor_ID));
}
/** Get Retencion Processor */
public int getC_RetencionProcessor_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionProcessor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Schema */
public void setC_RetencionSchema_ID (int C_RetencionSchema_ID)
{
set_ValueNoCheck ("C_RetencionSchema_ID", new Integer(C_RetencionSchema_ID));
}
/** Get Retencion Schema */
public int getC_RetencionSchema_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Type */
public void setC_RetencionType_ID (int C_RetencionType_ID)
{
set_Value ("C_RetencionType_ID", new Integer(C_RetencionType_ID));
}
/** Get Retencion Type */
public int getC_RetencionType_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 100)
{
log.warning("Length > 100 - truncated");
Description = Description.substring(0,100);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 80)
{
log.warning("Length > 80 - truncated");
Name = Name.substring(0,80);
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
/** Set Name 2.
Additional Name */
public void setName2 (String Name2)
{
if (Name2 != null && Name2.length() > 50)
{
log.warning("Length > 50 - truncated");
Name2 = Name2.substring(0,50);
}
set_Value ("Name2", Name2);
}
/** Get Name 2.
Additional Name */
public String getName2() 
{
return (String)get_Value("Name2");
}
public static final int RETENCIONAPPLICATION_AD_Reference_ID = MReference.getReferenceID("Retencion Application");
/** Emitted Retencion = E */
public static final String RETENCIONAPPLICATION_EmittedRetencion = "E";
/** Suffered Retencion = S */
public static final String RETENCIONAPPLICATION_SufferedRetencion = "S";
/** Set Retencion Application */
public void setRetencionApplication (String RetencionApplication)
{
if (RetencionApplication.equals("E") || RetencionApplication.equals("S"));
 else throw new IllegalArgumentException ("RetencionApplication Invalid value - Reference = RETENCIONAPPLICATION_AD_Reference_ID - E - S");
if (RetencionApplication == null) throw new IllegalArgumentException ("RetencionApplication is mandatory");
if (RetencionApplication.length() > 1)
{
log.warning("Length > 1 - truncated");
RetencionApplication = RetencionApplication.substring(0,1);
}
set_Value ("RetencionApplication", RetencionApplication);
}
/** Get Retencion Application */
public String getRetencionApplication() 
{
return (String)get_Value("RetencionApplication");
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
}
