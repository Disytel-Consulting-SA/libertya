/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ProjectIssueMA
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.015 */
public class X_C_ProjectIssueMA extends PO
{
/** Constructor estándar */
public X_C_ProjectIssueMA (Properties ctx, int C_ProjectIssueMA_ID, String trxName)
{
super (ctx, C_ProjectIssueMA_ID, trxName);
/** if (C_ProjectIssueMA_ID == 0)
{
setC_ProjectIssue_ID (0);
setM_AttributeSetInstance_ID (0);
setMovementQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_ProjectIssueMA (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=761 */
public static final int Table_ID=761;

/** TableName=C_ProjectIssueMA */
public static final String Table_Name="C_ProjectIssueMA";

protected static KeyNamePair Model = new KeyNamePair(761,"C_ProjectIssueMA");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ProjectIssueMA[").append(getID()).append("]");
return sb.toString();
}
/** Set Project Issue.
Project Issues (Material, Labor) */
public void setC_ProjectIssue_ID (int C_ProjectIssue_ID)
{
set_ValueNoCheck ("C_ProjectIssue_ID", new Integer(C_ProjectIssue_ID));
}
/** Get Project Issue.
Project Issues (Material, Labor) */
public int getC_ProjectIssue_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectIssue_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_ProjectIssue_ID()));
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
