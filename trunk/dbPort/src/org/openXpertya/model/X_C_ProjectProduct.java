/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ProjectProduct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.125 */
public class X_C_ProjectProduct extends PO
{
/** Constructor estándar */
public X_C_ProjectProduct (Properties ctx, int C_ProjectProduct_ID, String trxName)
{
super (ctx, C_ProjectProduct_ID, trxName);
/** if (C_ProjectProduct_ID == 0)
{
setC_ProjectTask_ID (0);
setC_Projectproduct_ID (0);
setC_Tax_ID (0);
setName (null);
setSeqNo (0);	// @sql=select COALESCE(MAX(SEQNO),0)+10 AS DefaultValue from C_ProjectProduct where C_ProjectTask_ID=@C_ProjectTask_ID@
}
 */
}
/** Load Constructor */
public X_C_ProjectProduct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000112 */
public static final int Table_ID=1000112;

/** TableName=C_ProjectProduct */
public static final String Table_Name="C_ProjectProduct";

protected static KeyNamePair Model = new KeyNamePair(1000112,"C_ProjectProduct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ProjectProduct[").append(getID()).append("]");
return sb.toString();
}
/** Set Project Task.
Actual Project Task in a Phase */
public void setC_ProjectTask_ID (int C_ProjectTask_ID)
{
set_Value ("C_ProjectTask_ID", new Integer(C_ProjectTask_ID));
}
/** Get Project Task.
Actual Project Task in a Phase */
public int getC_ProjectTask_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectTask_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Projectproduct_ID */
public void setC_Projectproduct_ID (int C_Projectproduct_ID)
{
set_ValueNoCheck ("C_Projectproduct_ID", new Integer(C_Projectproduct_ID));
}
/** Get C_Projectproduct_ID */
public int getC_Projectproduct_ID() 
{
Integer ii = (Integer)get_Value("C_Projectproduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Tax_ID */
public void setC_Tax_ID (int C_Tax_ID)
{
set_Value ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get C_Tax_ID */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CreateDocument */
public void setCreateDocument (String CreateDocument)
{
if (CreateDocument != null && CreateDocument.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateDocument = CreateDocument.substring(0,0);
}
set_ValueNoCheck ("CreateDocument", CreateDocument);
}
/** Get CreateDocument */
public String getCreateDocument() 
{
return (String)get_Value("CreateDocument");
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
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Sequence.
Method of ordering records;
 lowest NUMERIC comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest NUMERIC comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
}
