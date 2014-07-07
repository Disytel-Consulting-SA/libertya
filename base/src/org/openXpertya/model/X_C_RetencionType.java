/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RetencionType
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-07-07 10:36:09.519 */
public class X_C_RetencionType extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_RetencionType (Properties ctx, int C_RetencionType_ID, String trxName)
{
super (ctx, C_RetencionType_ID, trxName);
/** if (C_RetencionType_ID == 0)
{
setC_RetencionType_ID (0);
setis_by_region (false);
setM_Product_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_RetencionType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_RetencionType");

/** TableName=C_RetencionType */
public static final String Table_Name="C_RetencionType";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_RetencionType");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RetencionType[").append(getID()).append("]");
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
/** Set Retencion Type */
public void setC_RetencionType_ID (int C_RetencionType_ID)
{
set_ValueNoCheck ("C_RetencionType_ID", new Integer(C_RetencionType_ID));
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
/** Set Is By Region */
public void setis_by_region (boolean is_by_region)
{
set_Value ("is_by_region", new Boolean(is_by_region));
}
/** Get Is By Region */
public boolean is_by_region() 
{
Object oo = get_Value("is_by_region");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 30)
{
log.warning("Length > 30 - truncated");
Name = Name.substring(0,30);
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
public static final int RETENTIONTYPE_AD_Reference_ID = MReference.getReferenceID("Retention - Perception Type");
/** Ganancias = G */
public static final String RETENTIONTYPE_Ganancias = "G";
/** SIJP = J */
public static final String RETENTIONTYPE_SIJP = "J";
/** Suss = S */
public static final String RETENTIONTYPE_Suss = "S";
/** IVA = I */
public static final String RETENTIONTYPE_IVA = "I";
/** Ingresos Brutos = B */
public static final String RETENTIONTYPE_IngresosBrutos = "B";
/** Set RetentionType */
public void setRetentionType (String RetentionType)
{
if (RetentionType == null || RetentionType.equals("G") || RetentionType.equals("J") || RetentionType.equals("S") || RetentionType.equals("I") || RetentionType.equals("B"));
 else throw new IllegalArgumentException ("RetentionType Invalid value - Reference = RETENTIONTYPE_AD_Reference_ID - G - J - S - I - B");
if (RetentionType != null && RetentionType.length() > 1)
{
log.warning("Length > 1 - truncated");
RetentionType = RetentionType.substring(0,1);
}
set_Value ("RetentionType", RetentionType);
}
/** Get RetentionType */
public String getRetentionType() 
{
return (String)get_Value("RetentionType");
}
}
