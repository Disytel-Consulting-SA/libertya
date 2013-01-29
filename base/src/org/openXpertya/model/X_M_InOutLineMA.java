/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_InOutLineMA
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-10-20 01:32:04.506 */
public class X_M_InOutLineMA extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_InOutLineMA (Properties ctx, int M_InOutLineMA_ID, String trxName)
{
super (ctx, M_InOutLineMA_ID, trxName);
/** if (M_InOutLineMA_ID == 0)
{
setM_AttributeSetInstance_ID (0);
setM_InOutLine_ID (0);
setM_InOutLineMA_ID (0);
setMovementQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_InOutLineMA (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_InOutLineMA");

/** TableName=M_InOutLineMA */
public static final String Table_Name="M_InOutLineMA";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_InOutLineMA");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_InOutLineMA[").append(getID()).append("]");
return sb.toString();
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
/** Set Shipment/Receipt Line.
Line on Shipment or Receipt document */
public void setM_InOutLine_ID (int M_InOutLine_ID)
{
set_ValueNoCheck ("M_InOutLine_ID", new Integer(M_InOutLine_ID));
}
/** Get Shipment/Receipt Line.
Line on Shipment or Receipt document */
public int getM_InOutLine_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_InOutLine_ID()));
}
/** Set M_InOutLineMA_ID */
public void setM_InOutLineMA_ID (int M_InOutLineMA_ID)
{
set_ValueNoCheck ("M_InOutLineMA_ID", new Integer(M_InOutLineMA_ID));
}
/** Get M_InOutLineMA_ID */
public int getM_InOutLineMA_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLineMA_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Movement Quantity.
Quantity of a product moved. */
public void setMovementQty (BigDecimal MovementQty)
{
if (MovementQty == null) throw new IllegalArgumentException ("MovementQty is mandatory");
set_Value ("MovementQty", MovementQty);
}
/** Get Movement Quantity.
Quantity of a product moved. */
public BigDecimal getMovementQty() 
{
BigDecimal bd = (BigDecimal)get_Value("MovementQty");
if (bd == null) return Env.ZERO;
return bd;
}
}
