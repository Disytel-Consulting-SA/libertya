/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_FixedTermRetention
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2023-08-28 17:19:22.011 */
public class X_C_FixedTermRetention extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_FixedTermRetention (Properties ctx, int C_FixedTermRetention_ID, String trxName)
{
super (ctx, C_FixedTermRetention_ID, trxName);
/** if (C_FixedTermRetention_ID == 0)
{
setC_FixedTerm_ID (0);
setC_FixedTermRetention_ID (0);
setC_RetencionSchema_ID (0);
}
 */
}
/** Load Constructor */
public X_C_FixedTermRetention (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_FixedTermRetention");

/** TableName=C_FixedTermRetention */
public static final String Table_Name="C_FixedTermRetention";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_FixedTermRetention");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_FixedTermRetention[").append(getID()).append("]");
return sb.toString();
}
public static final int C_FIXEDTERM_ID_AD_Reference_ID = MReference.getReferenceID("C_FixedTerm");
/** Set C_FixedTerm_ID */
public void setC_FixedTerm_ID (int C_FixedTerm_ID)
{
set_Value ("C_FixedTerm_ID", new Integer(C_FixedTerm_ID));
}
/** Get C_FixedTerm_ID */
public int getC_FixedTerm_ID() 
{
Integer ii = (Integer)get_Value("C_FixedTerm_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_FixedTermRetention_ID */
public void setC_FixedTermRetention_ID (int C_FixedTermRetention_ID)
{
set_ValueNoCheck ("C_FixedTermRetention_ID", new Integer(C_FixedTermRetention_ID));
}
/** Get C_FixedTermRetention_ID */
public int getC_FixedTermRetention_ID() 
{
Integer ii = (Integer)get_Value("C_FixedTermRetention_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_RETENCIONSCHEMA_ID_AD_Reference_ID = MReference.getReferenceID("C_RetencionSchema");
/** Set Retencion Schema */
public void setC_RetencionSchema_ID (int C_RetencionSchema_ID)
{
set_Value ("C_RetencionSchema_ID", new Integer(C_RetencionSchema_ID));
}
/** Get Retencion Schema */
public int getC_RetencionSchema_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_RETENCION_INVOICE_ID_AD_Reference_ID = MReference.getReferenceID("M_RetencionInvoice");
/** Set Retencion */
public void setM_Retencion_Invoice_ID (int M_Retencion_Invoice_ID)
{
if (M_Retencion_Invoice_ID <= 0) set_Value ("M_Retencion_Invoice_ID", null);
 else 
set_Value ("M_Retencion_Invoice_ID", new Integer(M_Retencion_Invoice_ID));
}
/** Get Retencion */
public int getM_Retencion_Invoice_ID() 
{
Integer ii = (Integer)get_Value("M_Retencion_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RetentionAmt */
public void setRetentionAmt (BigDecimal RetentionAmt)
{
set_Value ("RetentionAmt", RetentionAmt);
}
/** Get RetentionAmt */
public BigDecimal getRetentionAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("RetentionAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
