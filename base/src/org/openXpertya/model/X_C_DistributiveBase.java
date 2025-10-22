/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import org.openXpertya.model.*;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_DistributiveBase
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2024-01-17 18:32:10.869 */
//
//Modelo tomado desde LP_C_DistributiveBase de micro contabilidad_en_linea 20251016
//
public class X_C_DistributiveBase extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_DistributiveBase (Properties ctx, int C_DistributiveBase_ID, String trxName)
{
super (ctx, C_DistributiveBase_ID, trxName);
/** if (C_DistributiveBase_ID == 0)
{
setC_Distributivebase_ID (0);
setC_ElementValue_ID (0);
setC_Project_ID (0);
setPercentage (Env.ZERO);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_DistributiveBase (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_DistributiveBase");

/** TableName=C_DistributiveBase */
public static final String Table_Name="C_DistributiveBase";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_DistributiveBase");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_DistributiveBase[").append(getID()).append("]");
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
/** Set C_Distributivebase_ID */
public void setC_Distributivebase_ID (int C_Distributivebase_ID)
{
set_ValueNoCheck ("C_Distributivebase_ID", new Integer(C_Distributivebase_ID));
}
/** Get C_Distributivebase_ID */
public int getC_Distributivebase_ID() 
{
Integer ii = (Integer)get_Value("C_Distributivebase_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Account Element.
Account Element */
public void setC_ElementValue_ID (int C_ElementValue_ID)
{
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
public static final int C_PROJECT_ID_AD_Reference_ID = MReference.getReferenceID("C_Project (No summary)");
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 255)
{
log.warning("Length > 255 - truncated");
Name = Name.substring(0,255);
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
/** Set Percentage.
Percent of the entire amount */
public void setPercentage (BigDecimal Percentage)
{
if (Percentage == null) throw new IllegalArgumentException ("Percentage is mandatory");
set_Value ("Percentage", Percentage);
}
/** Get Percentage.
Percent of the entire amount */
public BigDecimal getPercentage() 
{
BigDecimal bd = (BigDecimal)get_Value("Percentage");
if (bd == null) return Env.ZERO;
return bd;
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
