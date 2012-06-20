/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AttributeUse
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-12-02 16:44:43.618 */
public class X_M_AttributeUse extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_AttributeUse (Properties ctx, int M_AttributeUse_ID, String trxName)
{
super (ctx, M_AttributeUse_ID, trxName);
/** if (M_AttributeUse_ID == 0)
{
setIsDescription (false);
setM_Attribute_ID (0);
setM_AttributeSet_ID (0);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM M_AttributeUse WHERE M_AttributeSet_ID=@M_AttributeSet_ID@
}
 */
}
/** Load Constructor */
public X_M_AttributeUse (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_AttributeUse");

/** TableName=M_AttributeUse */
public static final String Table_Name="M_AttributeUse";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_AttributeUse");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AttributeUse[").append(getID()).append("]");
return sb.toString();
}
/** Set Description Only.
if true, the line is just description and no transaction */
public void setIsDescription (boolean IsDescription)
{
set_Value ("IsDescription", new Boolean(IsDescription));
}
/** Get Description Only.
if true, the line is just description and no transaction */
public boolean isDescription() 
{
Object oo = get_Value("IsDescription");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Attribute Set.
Product Attribute Set */
public void setM_AttributeSet_ID (int M_AttributeSet_ID)
{
set_ValueNoCheck ("M_AttributeSet_ID", new Integer(M_AttributeSet_ID));
}
/** Get Attribute Set.
Product Attribute Set */
public int getM_AttributeSet_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSet_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_AttributeSet_ID()));
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
}
