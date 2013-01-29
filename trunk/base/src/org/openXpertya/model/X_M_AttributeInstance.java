/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AttributeInstance
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-12-02 16:44:43.552 */
public class X_M_AttributeInstance extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_AttributeInstance (Properties ctx, int M_AttributeInstance_ID, String trxName)
{
super (ctx, M_AttributeInstance_ID, trxName);
/** if (M_AttributeInstance_ID == 0)
{
setM_Attribute_ID (0);
setM_AttributeSetInstance_ID (0);
}
 */
}
/** Load Constructor */
public X_M_AttributeInstance (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_AttributeInstance");

/** TableName=M_AttributeInstance */
public static final String Table_Name="M_AttributeInstance";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_AttributeInstance");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AttributeInstance[").append(getID()).append("]");
return sb.toString();
}
/** Set Attribute.
Product Attribute */
public void setM_Attribute_ID (int M_Attribute_ID)
{
set_ValueNoCheck ("M_Attribute_ID", new Integer(M_Attribute_ID));
}
/** Get Attribute.
Product Attribute */
public int getM_Attribute_ID() 
{
Integer ii = (Integer)get_Value("M_Attribute_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
set_ValueNoCheck ("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
}
/** Get Attribute Set Instance.
Product Attribute Set Instance */
public int getM_AttributeSetInstance_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSetInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Value.
Product Attribute Value */
public void setM_AttributeValue_ID (int M_AttributeValue_ID)
{
if (M_AttributeValue_ID <= 0) set_Value ("M_AttributeValue_ID", null);
 else 
set_Value ("M_AttributeValue_ID", new Integer(M_AttributeValue_ID));
}
/** Get Attribute Value.
Product Attribute Value */
public int getM_AttributeValue_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_AttributeValue_ID()));
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 40)
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
/** Set Value.
Date value */
public void setValueDate (Timestamp ValueDate)
{
set_Value ("ValueDate", ValueDate);
}
/** Get Value.
Date value */
public Timestamp getValueDate() 
{
return (Timestamp)get_Value("ValueDate");
}
/** Set Value.
Numeric Value */
public void setValueNumber (BigDecimal ValueNumber)
{
set_Value ("ValueNumber", ValueNumber);
}
/** Get Value.
Numeric Value */
public BigDecimal getValueNumber() 
{
BigDecimal bd = (BigDecimal)get_Value("ValueNumber");
if (bd == null) return Env.ZERO;
return bd;
}
}
