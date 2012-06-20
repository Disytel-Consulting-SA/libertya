/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CreditException
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-11-26 10:41:34.093 */
public class X_C_CreditException extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CreditException (Properties ctx, int C_CreditException_ID, String trxName)
{
super (ctx, C_CreditException_ID, trxName);
/** if (C_CreditException_ID == 0)
{
setC_BPartner_ID (0);
setC_CreditException_ID (0);
setCreditLimitException (Env.ZERO);
setCreditStatusException (null);
setExceptionEndDate (new Timestamp(System.currentTimeMillis()));
setExceptionStartDate (new Timestamp(System.currentTimeMillis()));
setExceptionType (null);
}
 */
}
/** Load Constructor */
public X_C_CreditException (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CreditException");

/** TableName=C_CreditException */
public static final String Table_Name="C_CreditException";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CreditException");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CreditException[").append(getID()).append("]");
return sb.toString();
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
/** Set C_CreditException_ID */
public void setC_CreditException_ID (int C_CreditException_ID)
{
set_ValueNoCheck ("C_CreditException_ID", new Integer(C_CreditException_ID));
}
/** Get C_CreditException_ID */
public int getC_CreditException_ID() 
{
Integer ii = (Integer)get_Value("C_CreditException_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit Limit Exception.
Exception amt to credit limit */
public void setCreditLimitException (BigDecimal CreditLimitException)
{
if (CreditLimitException == null) throw new IllegalArgumentException ("CreditLimitException is mandatory");
set_Value ("CreditLimitException", CreditLimitException);
}
/** Get Credit Limit Exception.
Exception amt to credit limit */
public BigDecimal getCreditLimitException() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditLimitException");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int CREDITSTATUSEXCEPTION_AD_Reference_ID = MReference.getReferenceID("C_BPartner SOCreditStatus");
/** Credit Stop = S */
public static final String CREDITSTATUSEXCEPTION_CreditStop = "S";
/** Credit OK = O */
public static final String CREDITSTATUSEXCEPTION_CreditOK = "O";
/** Credit Hold = H */
public static final String CREDITSTATUSEXCEPTION_CreditHold = "H";
/** No Credit Check = X */
public static final String CREDITSTATUSEXCEPTION_NoCreditCheck = "X";
/** Credit Watch = W */
public static final String CREDITSTATUSEXCEPTION_CreditWatch = "W";
/** Credit Disabled = D */
public static final String CREDITSTATUSEXCEPTION_CreditDisabled = "D";
/** Set Credit Status Exception.
Credit status exception */
public void setCreditStatusException (String CreditStatusException)
{
if (CreditStatusException.equals("S") || CreditStatusException.equals("O") || CreditStatusException.equals("H") || CreditStatusException.equals("X") || CreditStatusException.equals("W") || CreditStatusException.equals("D"));
 else throw new IllegalArgumentException ("CreditStatusException Invalid value - Reference = CREDITSTATUSEXCEPTION_AD_Reference_ID - S - O - H - X - W - D");
if (CreditStatusException == null) throw new IllegalArgumentException ("CreditStatusException is mandatory");
if (CreditStatusException.length() > 1)
{
log.warning("Length > 1 - truncated");
CreditStatusException = CreditStatusException.substring(0,1);
}
set_Value ("CreditStatusException", CreditStatusException);
}
/** Get Credit Status Exception.
Credit status exception */
public String getCreditStatusException() 
{
return (String)get_Value("CreditStatusException");
}
/** Set Exception End Date */
public void setExceptionEndDate (Timestamp ExceptionEndDate)
{
if (ExceptionEndDate == null) throw new IllegalArgumentException ("ExceptionEndDate is mandatory");
set_Value ("ExceptionEndDate", ExceptionEndDate);
}
/** Get Exception End Date */
public Timestamp getExceptionEndDate() 
{
return (Timestamp)get_Value("ExceptionEndDate");
}
/** Set Exception Start Date */
public void setExceptionStartDate (Timestamp ExceptionStartDate)
{
if (ExceptionStartDate == null) throw new IllegalArgumentException ("ExceptionStartDate is mandatory");
set_Value ("ExceptionStartDate", ExceptionStartDate);
}
/** Get Exception Start Date */
public Timestamp getExceptionStartDate() 
{
return (Timestamp)get_Value("ExceptionStartDate");
}
public static final int EXCEPTIONTYPE_AD_Reference_ID = MReference.getReferenceID("Credit exception types");
/** Credit Limit = L */
public static final String EXCEPTIONTYPE_CreditLimit = "L";
/** Credit Status = S */
public static final String EXCEPTIONTYPE_CreditStatus = "S";
/** Set Exception Type */
public void setExceptionType (String ExceptionType)
{
if (ExceptionType.equals("L") || ExceptionType.equals("S"));
 else throw new IllegalArgumentException ("ExceptionType Invalid value - Reference = EXCEPTIONTYPE_AD_Reference_ID - L - S");
if (ExceptionType == null) throw new IllegalArgumentException ("ExceptionType is mandatory");
if (ExceptionType.length() > 1)
{
log.warning("Length > 1 - truncated");
ExceptionType = ExceptionType.substring(0,1);
}
set_Value ("ExceptionType", ExceptionType);
}
/** Get Exception Type */
public String getExceptionType() 
{
return (String)get_Value("ExceptionType");
}
}
