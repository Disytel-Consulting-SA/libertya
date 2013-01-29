/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RevenueRecognition_Run
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.906 */
public class X_C_RevenueRecognition_Run extends PO
{
/** Constructor estándar */
public X_C_RevenueRecognition_Run (Properties ctx, int C_RevenueRecognition_Run_ID, String trxName)
{
super (ctx, C_RevenueRecognition_Run_ID, trxName);
/** if (C_RevenueRecognition_Run_ID == 0)
{
setC_RevenueRecognition_Plan_ID (0);
setC_RevenueRecognition_Run_ID (0);
setGL_Journal_ID (0);
setRecognizedAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_RevenueRecognition_Run (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=444 */
public static final int Table_ID=444;

/** TableName=C_RevenueRecognition_Run */
public static final String Table_Name="C_RevenueRecognition_Run";

protected static KeyNamePair Model = new KeyNamePair(444,"C_RevenueRecognition_Run");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RevenueRecognition_Run[").append(getID()).append("]");
return sb.toString();
}
/** Set Revenue Recognition Plan.
Plan for recognizing or recording revenue */
public void setC_RevenueRecognition_Plan_ID (int C_RevenueRecognition_Plan_ID)
{
set_ValueNoCheck ("C_RevenueRecognition_Plan_ID", new Integer(C_RevenueRecognition_Plan_ID));
}
/** Get Revenue Recognition Plan.
Plan for recognizing or recording revenue */
public int getC_RevenueRecognition_Plan_ID() 
{
Integer ii = (Integer)get_Value("C_RevenueRecognition_Plan_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_RevenueRecognition_Plan_ID()));
}
/** Set Revenue Recognition Run.
Revenue Recognition Run or Process */
public void setC_RevenueRecognition_Run_ID (int C_RevenueRecognition_Run_ID)
{
set_ValueNoCheck ("C_RevenueRecognition_Run_ID", new Integer(C_RevenueRecognition_Run_ID));
}
/** Get Revenue Recognition Run.
Revenue Recognition Run or Process */
public int getC_RevenueRecognition_Run_ID() 
{
Integer ii = (Integer)get_Value("C_RevenueRecognition_Run_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Journal.
General Ledger Journal */
public void setGL_Journal_ID (int GL_Journal_ID)
{
set_ValueNoCheck ("GL_Journal_ID", new Integer(GL_Journal_ID));
}
/** Get Journal.
General Ledger Journal */
public int getGL_Journal_ID() 
{
Integer ii = (Integer)get_Value("GL_Journal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Recognized Amount */
public void setRecognizedAmt (BigDecimal RecognizedAmt)
{
if (RecognizedAmt == null) throw new IllegalArgumentException ("RecognizedAmt is mandatory");
set_ValueNoCheck ("RecognizedAmt", RecognizedAmt);
}
/** Get Recognized Amount */
public BigDecimal getRecognizedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("RecognizedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
