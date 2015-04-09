/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_BrochureLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-04-08 20:54:10.138 */
public class X_M_BrochureLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_BrochureLine (Properties ctx, int M_BrochureLine_ID, String trxName)
{
super (ctx, M_BrochureLine_ID, trxName);
/** if (M_BrochureLine_ID == 0)
{
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_BrochureLine WHERE M_Brochure_ID=@M_Brochure_ID@
setM_Brochure_ID (0);	// @M_Brochure_ID@
setM_BrochureLine_ID (0);
setM_Product_ID (0);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_M_BrochureLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_BrochureLine");

/** TableName=M_BrochureLine */
public static final String Table_Name="M_BrochureLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_BrochureLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_BrochureLine[").append(getID()).append("]");
return sb.toString();
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
/** Set Line No.
Unique line for this document */
public void setLine (int Line)
{
set_Value ("Line", new Integer(Line));
}
/** Get Line No.
Unique line for this document */
public int getLine() 
{
Integer ii = (Integer)get_Value("Line");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getLine()));
}
/** Set Brochure */
public void setM_Brochure_ID (int M_Brochure_ID)
{
set_Value ("M_Brochure_ID", new Integer(M_Brochure_ID));
}
/** Get Brochure */
public int getM_Brochure_ID() 
{
Integer ii = (Integer)get_Value("M_Brochure_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Brochure Line */
public void setM_BrochureLine_ID (int M_BrochureLine_ID)
{
set_ValueNoCheck ("M_BrochureLine_ID", new Integer(M_BrochureLine_ID));
}
/** Get Brochure Line */
public int getM_BrochureLine_ID() 
{
Integer ii = (Integer)get_Value("M_BrochureLine_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
