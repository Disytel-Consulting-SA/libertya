/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_POSLetter
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-08-28 00:24:53.652 */
public class X_C_POSLetter extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_POSLetter (Properties ctx, int C_POSLetter_ID, String trxName)
{
super (ctx, C_POSLetter_ID, trxName);
/** if (C_POSLetter_ID == 0)
{
setC_POS_ID (0);	// @C_POS_ID@
setC_POSLetter_ID (0);
setLetter (null);
setPOSNumber (0);	// 1
}
 */
}
/** Load Constructor */
public X_C_POSLetter (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_POSLetter");

/** TableName=C_POSLetter */
public static final String Table_Name="C_POSLetter";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_POSLetter");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_POSLetter[").append(getID()).append("]");
return sb.toString();
}
/** Set POS Terminal.
Point of Sales Terminal */
public void setC_POS_ID (int C_POS_ID)
{
set_Value ("C_POS_ID", new Integer(C_POS_ID));
}
/** Get POS Terminal.
Point of Sales Terminal */
public int getC_POS_ID() 
{
Integer ii = (Integer)get_Value("C_POS_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_POSLetter_ID */
public void setC_POSLetter_ID (int C_POSLetter_ID)
{
set_ValueNoCheck ("C_POSLetter_ID", new Integer(C_POSLetter_ID));
}
/** Get C_POSLetter_ID */
public int getC_POSLetter_ID() 
{
Integer ii = (Integer)get_Value("C_POSLetter_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int LETTER_AD_Reference_ID = MReference.getReferenceID("Letras de Comprobantes");
/** A = A */
public static final String LETTER_A = "A";
/** B = B */
public static final String LETTER_B = "B";
/** C = C */
public static final String LETTER_C = "C";
/** E = E */
public static final String LETTER_E = "E";
/** Set Letter.
Document Letter */
public void setLetter (String Letter)
{
if (Letter.equals("A") || Letter.equals("B") || Letter.equals("C") || Letter.equals("E"));
 else throw new IllegalArgumentException ("Letter Invalid value - Reference = LETTER_AD_Reference_ID - A - B - C - E");
if (Letter == null) throw new IllegalArgumentException ("Letter is mandatory");
if (Letter.length() > 1)
{
log.warning("Length > 1 - truncated");
Letter = Letter.substring(0,1);
}
set_Value ("Letter", Letter);
}
/** Get Letter.
Document Letter */
public String getLetter() 
{
return (String)get_Value("Letter");
}
/** Set POS Number */
public void setPOSNumber (int POSNumber)
{
set_Value ("POSNumber", new Integer(POSNumber));
}
/** Get POS Number */
public int getPOSNumber() 
{
Integer ii = (Integer)get_Value("POSNumber");
if (ii == null) return 0;
return ii.intValue();
}
}
