/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Phase
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:31.781 */
public class X_C_Phase extends PO
{
/** Constructor estándar */
public X_C_Phase (Properties ctx, int C_Phase_ID, String trxName)
{
super (ctx, C_Phase_ID, trxName);
/** if (C_Phase_ID == 0)
{
setC_Phase_ID (0);
setC_ProjectType_ID (0);
setName (null);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM C_Phase WHERE C_ProjectType_ID=@C_ProjectType_ID@
setStandardQty (Env.ZERO);	// 1
}
 */
}
/** Load Constructor */
public X_C_Phase (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=577 */
public static final int Table_ID=577;

/** TableName=C_Phase */
public static final String Table_Name="C_Phase";

protected static KeyNamePair Model = new KeyNamePair(577,"C_Phase");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Phase[").append(getID()).append("]");
return sb.toString();
}
/** Set Standard Phase.
Standard Phase of the Project Type */
public void setC_Phase_ID (int C_Phase_ID)
{
set_ValueNoCheck ("C_Phase_ID", new Integer(C_Phase_ID));
}
/** Get Standard Phase.
Standard Phase of the Project Type */
public int getC_Phase_ID() 
{
Integer ii = (Integer)get_Value("C_Phase_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Type.
Type of the project */
public void setC_ProjectType_ID (int C_ProjectType_ID)
{
set_ValueNoCheck ("C_ProjectType_ID", new Integer(C_ProjectType_ID));
}
/** Get Project Type.
Type of the project */
public int getC_ProjectType_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectType_ID");
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
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
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
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
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
/** Set Standard Quantity.
Standard Quantity */
public void setStandardQty (BigDecimal StandardQty)
{
if (StandardQty == null) throw new IllegalArgumentException ("StandardQty is mandatory");
set_Value ("StandardQty", StandardQty);
}
/** Get Standard Quantity.
Standard Quantity */
public BigDecimal getStandardQty() 
{
BigDecimal bd = (BigDecimal)get_Value("StandardQty");
if (bd == null) return Env.ZERO;
return bd;
}
}
