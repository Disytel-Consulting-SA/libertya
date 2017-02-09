/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CreditCardCouponFilter
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-02-09 12:25:48.307 */
public class X_C_CreditCardCouponFilter extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CreditCardCouponFilter (Properties ctx, int C_CreditCardCouponFilter_ID, String trxName)
{
super (ctx, C_CreditCardCouponFilter_ID, trxName);
/** if (C_CreditCardCouponFilter_ID == 0)
{
setC_CreditCardCouponFilter_ID (0);
setC_CreditCardSettlement_ID (0);
setIsProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_CreditCardCouponFilter (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CreditCardCouponFilter");

/** TableName=C_CreditCardCouponFilter */
public static final String Table_Name="C_CreditCardCouponFilter";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CreditCardCouponFilter");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CreditCardCouponFilter[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
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
/** Set Credit Card Coupon Filter ID */
public void setC_CreditCardCouponFilter_ID (int C_CreditCardCouponFilter_ID)
{
set_ValueNoCheck ("C_CreditCardCouponFilter_ID", new Integer(C_CreditCardCouponFilter_ID));
}
/** Get Credit Card Coupon Filter ID */
public int getC_CreditCardCouponFilter_ID() 
{
Integer ii = (Integer)get_Value("C_CreditCardCouponFilter_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_CREDITCARDSETTLEMENT_ID_AD_Reference_ID = MReference.getReferenceID("Settlements (number)");
/** Set Credit Card Settlement */
public void setC_CreditCardSettlement_ID (int C_CreditCardSettlement_ID)
{
set_Value ("C_CreditCardSettlement_ID", new Integer(C_CreditCardSettlement_ID));
}
/** Get Credit Card Settlement */
public int getC_CreditCardSettlement_ID() 
{
Integer ii = (Integer)get_Value("C_CreditCardSettlement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Processed */
public void setIsProcessed (boolean IsProcessed)
{
set_Value ("IsProcessed", new Boolean(IsProcessed));
}
/** Get Processed */
public boolean isProcessed() 
{
Object oo = get_Value("IsProcessed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int M_ENTIDADFINANCIERA_ID_AD_Reference_ID = MReference.getReferenceID("Entidad Financiera From BPartner");
/** Set Entidad Financiera */
public void setM_EntidadFinanciera_ID (int M_EntidadFinanciera_ID)
{
if (M_EntidadFinanciera_ID <= 0) set_Value ("M_EntidadFinanciera_ID", null);
 else 
set_Value ("M_EntidadFinanciera_ID", new Integer(M_EntidadFinanciera_ID));
}
/** Get Entidad Financiera */
public int getM_EntidadFinanciera_ID() 
{
Integer ii = (Integer)get_Value("M_EntidadFinanciera_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_ENTIDADFINANCIERAPLAN_ID_AD_Reference_ID = MReference.getReferenceID("M_EntidadFinanciera Planes");
/** Set Plan de Entidad Financiera.
Plan de Entidad Financiera */
public void setM_EntidadFinancieraPlan_ID (int M_EntidadFinancieraPlan_ID)
{
if (M_EntidadFinancieraPlan_ID <= 0) set_Value ("M_EntidadFinancieraPlan_ID", null);
 else 
set_Value ("M_EntidadFinancieraPlan_ID", new Integer(M_EntidadFinancieraPlan_ID));
}
/** Get Plan de Entidad Financiera.
Plan de Entidad Financiera */
public int getM_EntidadFinancieraPlan_ID() 
{
Integer ii = (Integer)get_Value("M_EntidadFinancieraPlan_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set PaymentBatch */
public void setPaymentBatch (String PaymentBatch)
{
if (PaymentBatch != null && PaymentBatch.length() > 24)
{
log.warning("Length > 24 - truncated");
PaymentBatch = PaymentBatch.substring(0,24);
}
set_Value ("PaymentBatch", PaymentBatch);
}
/** Get PaymentBatch */
public String getPaymentBatch() 
{
return (String)get_Value("PaymentBatch");
}
/** Set Process */
public void setProcess (String Process)
{
if (Process != null && Process.length() > 1)
{
log.warning("Length > 1 - truncated");
Process = Process.substring(0,1);
}
set_Value ("Process", Process);
}
/** Get Process */
public String getProcess() 
{
return (String)get_Value("Process");
}
/** Set Trx Date From */
public void setTrxDateFrom (Timestamp TrxDateFrom)
{
set_Value ("TrxDateFrom", TrxDateFrom);
}
/** Get Trx Date From */
public Timestamp getTrxDateFrom() 
{
return (Timestamp)get_Value("TrxDateFrom");
}
/** Set Trx Date To */
public void setTrxDateTo (Timestamp TrxDateTo)
{
set_Value ("TrxDateTo", TrxDateTo);
}
/** Get Trx Date To */
public Timestamp getTrxDateTo() 
{
return (Timestamp)get_Value("TrxDateTo");
}
}
