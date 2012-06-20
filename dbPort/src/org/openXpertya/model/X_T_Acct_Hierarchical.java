/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Acct_Hierarchical
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-06-18 14:08:53.156 */
public class X_T_Acct_Hierarchical extends PO
{
/** Constructor estándar */
public X_T_Acct_Hierarchical (Properties ctx, int T_Acct_Hierarchical_ID, String trxName)
{
super (ctx, T_Acct_Hierarchical_ID, trxName);
/** if (T_Acct_Hierarchical_ID == 0)
{
setAD_PInstance_ID (0);
setSubindex (0);
setT_Acct_Hierarchical_ID (0);
}
 */
}
/** Load Constructor */
public X_T_Acct_Hierarchical (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000174 */
public static final int Table_ID=1000174;

/** TableName=T_Acct_Hierarchical */
public static final String Table_Name="T_Acct_Hierarchical";

protected static KeyNamePair Model = new KeyNamePair(1000174,"T_Acct_Hierarchical");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Acct_Hierarchical[").append(getID()).append("]");
return sb.toString();
}
/** Set Account Code */
public void setAcct_Code (String Acct_Code)
{
if (Acct_Code != null && Acct_Code.length() > 512)
{
log.warning("Length > 512 - truncated");
Acct_Code = Acct_Code.substring(0,512);
}
set_Value ("Acct_Code", Acct_Code);
}
/** Get Account Code */
public String getAcct_Code() 
{
return (String)get_Value("Acct_Code");
}
/** Set Account Description */
public void setAcct_Description (String Acct_Description)
{
if (Acct_Description != null && Acct_Description.length() > 512)
{
log.warning("Length > 512 - truncated");
Acct_Description = Acct_Description.substring(0,512);
}
set_Value ("Acct_Description", Acct_Description);
}
/** Get Account Description */
public String getAcct_Description() 
{
return (String)get_Value("Acct_Description");
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Account Element.
Account Element */
public void setC_ElementValue_ID (int C_ElementValue_ID)
{
if (C_ElementValue_ID <= 0) set_Value ("C_ElementValue_ID", null);
 else 
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
/** Set Subindex */
public void setSubindex (int Subindex)
{
set_Value ("Subindex", new Integer(Subindex));
}
/** Get Subindex */
public int getSubindex() 
{
Integer ii = (Integer)get_Value("Subindex");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Account Hierarchical Report */
public void setT_Acct_Hierarchical_ID (int T_Acct_Hierarchical_ID)
{
set_ValueNoCheck ("T_Acct_Hierarchical_ID", new Integer(T_Acct_Hierarchical_ID));
}
/** Get Account Hierarchical Report */
public int getT_Acct_Hierarchical_ID() 
{
Integer ii = (Integer)get_Value("T_Acct_Hierarchical_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
