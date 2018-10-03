/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Promotion_Code
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2018-10-03 12:02:42.638 */
public class X_C_Promotion_Code extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Promotion_Code (Properties ctx, int C_Promotion_Code_ID, String trxName)
{
super (ctx, C_Promotion_Code_ID, trxName);
/** if (C_Promotion_Code_ID == 0)
{
setCode (null);
setC_Promotion_Code_Batch_ID (0);
setC_Promotion_Code_ID (0);
setC_Promotion_ID (0);
setProcessed (false);
setUsed (false);
setValidFrom (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_Promotion_Code (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Promotion_Code");

/** TableName=C_Promotion_Code */
public static final String Table_Name="C_Promotion_Code";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Promotion_Code");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Promotion_Code[").append(getID()).append("]");
return sb.toString();
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Validation code.
Validation Code */
public void setCode (String Code)
{
if (Code == null) throw new IllegalArgumentException ("Code is mandatory");
if (Code.length() > 255)
{
log.warning("Length > 255 - truncated");
Code = Code.substring(0,255);
}
set_Value ("Code", Code);
}
/** Get Validation code.
Validation Code */
public String getCode() 
{
return (String)get_Value("Code");
}
/** Set Promotional Code Batch */
public void setC_Promotion_Code_Batch_ID (int C_Promotion_Code_Batch_ID)
{
set_Value ("C_Promotion_Code_Batch_ID", new Integer(C_Promotion_Code_Batch_ID));
}
/** Get Promotional Code Batch */
public int getC_Promotion_Code_Batch_ID() 
{
Integer ii = (Integer)get_Value("C_Promotion_Code_Batch_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Promotion Code */
public void setC_Promotion_Code_ID (int C_Promotion_Code_ID)
{
set_ValueNoCheck ("C_Promotion_Code_ID", new Integer(C_Promotion_Code_ID));
}
/** Get Promotion Code */
public int getC_Promotion_Code_ID() 
{
Integer ii = (Integer)get_Value("C_Promotion_Code_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Products Promotion.
Products Promotion */
public void setC_Promotion_ID (int C_Promotion_ID)
{
set_Value ("C_Promotion_ID", new Integer(C_Promotion_ID));
}
/** Get Products Promotion.
Products Promotion */
public int getC_Promotion_ID() 
{
Integer ii = (Integer)get_Value("C_Promotion_ID");
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
/** Set Used */
public void setUsed (boolean Used)
{
set_Value ("Used", new Boolean(Used));
}
/** Get Used */
public boolean isUsed() 
{
Object oo = get_Value("Used");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
}
