/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_PercExenc
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-10-27 01:43:42.144 */
public class X_C_BPartner_PercExenc extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner_PercExenc (Properties ctx, int C_BPartner_PercExenc_ID, String trxName)
{
super (ctx, C_BPartner_PercExenc_ID, trxName);
/** if (C_BPartner_PercExenc_ID == 0)
{
setAD_Org_Percepcion_ID (0);
setC_BPartner_ID (0);
setC_BPartner_PercExenc_ID (0);
setC_Tax_ID (0);
setDate_From (new Timestamp(System.currentTimeMillis()));
setDate_To (new Timestamp(System.currentTimeMillis()));
setPercent (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_BPartner_PercExenc (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_PercExenc");

/** TableName=C_BPartner_PercExenc */
public static final String Table_Name="C_BPartner_PercExenc";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_PercExenc");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_PercExenc[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_Org_Percepcion_ID */
public void setAD_Org_Percepcion_ID (int AD_Org_Percepcion_ID)
{
set_Value ("AD_Org_Percepcion_ID", new Integer(AD_Org_Percepcion_ID));
}
/** Get AD_Org_Percepcion_ID */
public int getAD_Org_Percepcion_ID() 
{
Integer ii = (Integer)get_Value("AD_Org_Percepcion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_BPartner_PercExenc_ID */
public void setC_BPartner_PercExenc_ID (int C_BPartner_PercExenc_ID)
{
set_ValueNoCheck ("C_BPartner_PercExenc_ID", new Integer(C_BPartner_PercExenc_ID));
}
/** Get C_BPartner_PercExenc_ID */
public int getC_BPartner_PercExenc_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_PercExenc_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax.
Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
set_Value ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get Tax.
Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set From Date */
public void setDate_From (Timestamp Date_From)
{
if (Date_From == null) throw new IllegalArgumentException ("Date_From is mandatory");
set_Value ("Date_From", Date_From);
}
/** Get From Date */
public Timestamp getDate_From() 
{
return (Timestamp)get_Value("Date_From");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getDate_From()));
}
/** Set To Date */
public void setDate_To (Timestamp Date_To)
{
if (Date_To == null) throw new IllegalArgumentException ("Date_To is mandatory");
set_Value ("Date_To", Date_To);
}
/** Get To Date */
public Timestamp getDate_To() 
{
return (Timestamp)get_Value("Date_To");
}
/** Set Percent.
Percentage */
public void setPercent (BigDecimal Percent)
{
if (Percent == null) throw new IllegalArgumentException ("Percent is mandatory");
set_Value ("Percent", Percent);
}
/** Get Percent.
Percentage */
public BigDecimal getPercent() 
{
BigDecimal bd = (BigDecimal)get_Value("Percent");
if (bd == null) return Env.ZERO;
return bd;
}
}
