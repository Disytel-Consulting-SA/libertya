/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaymentFixLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-10-10 23:52:03.775 */
public class X_C_PaymentFixLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_PaymentFixLine (Properties ctx, int C_PaymentFixLine_ID, String trxName)
{
super (ctx, C_PaymentFixLine_ID, trxName);
/** if (C_PaymentFixLine_ID == 0)
{
setAction (null);	// V
setC_AllocationLine_ID (0);
setC_CashLine_ID (0);
setC_PaymentFix_ID (0);	// @C_PaymentFix_ID@
setC_PaymentFixLine_ID (0);
setC_Payment_ID (0);
setDocumentType (null);	// C
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM C_PaymentFixLine WHERE C_PaymentFix_ID=@C_PaymentFix_ID@
setPayAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_PaymentFixLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_PaymentFixLine");

/** TableName=C_PaymentFixLine */
public static final String Table_Name="C_PaymentFixLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_PaymentFixLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaymentFixLine[").append(getID()).append("]");
return sb.toString();
}
public static final int ACTION_AD_Reference_ID = MReference.getReferenceID("C_PaymentFixLine Action");
/** Void = V */
public static final String ACTION_Void = "V";
/** Allocate = A */
public static final String ACTION_Allocate = "A";
/** Set Action.
Indicates the Action to be performed */
public void setAction (String Action)
{
if (Action.equals("V") || Action.equals("A"));
 else throw new IllegalArgumentException ("Action Invalid value - Reference = ACTION_AD_Reference_ID - V - A");
if (Action == null) throw new IllegalArgumentException ("Action is mandatory");
if (Action.length() > 1)
{
log.warning("Length > 1 - truncated");
Action = Action.substring(0,1);
}
set_ValueNoCheck ("Action", Action);
}
/** Get Action.
Indicates the Action to be performed */
public String getAction() 
{
return (String)get_Value("Action");
}
/** Set Allocation Line.
Allocation Line */
public void setC_AllocationLine_ID (int C_AllocationLine_ID)
{
set_Value ("C_AllocationLine_ID", new Integer(C_AllocationLine_ID));
}
/** Get Allocation Line.
Allocation Line */
public int getC_AllocationLine_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cash Journal Line.
Cash Journal Line */
public void setC_CashLine_ID (int C_CashLine_ID)
{
set_Value ("C_CashLine_ID", new Integer(C_CashLine_ID));
}
/** Get Cash Journal Line.
Cash Journal Line */
public int getC_CashLine_ID() 
{
Integer ii = (Integer)get_Value("C_CashLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Fix.
Payment Fix */
public void setC_PaymentFix_ID (int C_PaymentFix_ID)
{
set_Value ("C_PaymentFix_ID", new Integer(C_PaymentFix_ID));
}
/** Get Payment Fix.
Payment Fix */
public int getC_PaymentFix_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentFix_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Fix Line.
Payment Fix Line */
public void setC_PaymentFixLine_ID (int C_PaymentFixLine_ID)
{
set_ValueNoCheck ("C_PaymentFixLine_ID", new Integer(C_PaymentFixLine_ID));
}
/** Get Payment Fix Line.
Payment Fix Line */
public int getC_PaymentFixLine_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentFixLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
set_Value ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
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
public static final int DOCUMENTTYPE_AD_Reference_ID = MReference.getReferenceID("C_PaymentFixLine DocType");
/** Cash = C */
public static final String DOCUMENTTYPE_Cash = "C";
/** Payment = P */
public static final String DOCUMENTTYPE_Payment = "P";
/** Set Document Type.
Document Type */
public void setDocumentType (String DocumentType)
{
if (DocumentType.equals("C") || DocumentType.equals("P"));
 else throw new IllegalArgumentException ("DocumentType Invalid value - Reference = DOCUMENTTYPE_AD_Reference_ID - C - P");
if (DocumentType == null) throw new IllegalArgumentException ("DocumentType is mandatory");
if (DocumentType.length() > 1)
{
log.warning("Length > 1 - truncated");
DocumentType = DocumentType.substring(0,1);
}
set_ValueNoCheck ("DocumentType", DocumentType);
}
/** Get Document Type.
Document Type */
public String getDocumentType() 
{
return (String)get_Value("DocumentType");
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
/** Set Payment amount.
Amount being paid */
public void setPayAmt (BigDecimal PayAmt)
{
if (PayAmt == null) throw new IllegalArgumentException ("PayAmt is mandatory");
set_Value ("PayAmt", PayAmt);
}
/** Get Payment amount.
Amount being paid */
public BigDecimal getPayAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PayAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
