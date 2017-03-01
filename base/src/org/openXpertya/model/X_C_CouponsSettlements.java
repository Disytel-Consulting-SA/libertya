/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CouponsSettlements
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-03-01 10:18:24.288 */
public class X_C_CouponsSettlements extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CouponsSettlements (Properties ctx, int C_CouponsSettlements_ID, String trxName)
{
super (ctx, C_CouponsSettlements_ID, trxName);
/** if (C_CouponsSettlements_ID == 0)
{
setC_CouponsSettlements_ID (0);
setC_CreditCardCouponFilter_ID (0);
setC_CreditCardSettlement_ID (0);	// @SQL=SELECT C_CreditCardSettlement_ID FROM C_CreditCardCouponFilter WHERE C_CreditCardCouponFilter_ID=@C_CreditCardCouponFilter_ID@
setC_Currency_ID (0);
setC_Payment_ID (0);
setInclude (false);
setIsReconciled (false);
setM_EntidadFinanciera_ID (0);
setM_EntidadFinancieraPlan_ID (0);
}
 */
}
/** Load Constructor */
public X_C_CouponsSettlements (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CouponsSettlements");

/** TableName=C_CouponsSettlements */
public static final String Table_Name="C_CouponsSettlements";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CouponsSettlements");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CouponsSettlements[").append(getID()).append("]");
return sb.toString();
}
/** Set Amount.
Amount in a defined currency */
public void setAmount (BigDecimal Amount)
{
set_Value ("Amount", Amount);
}
/** Get Amount.
Amount in a defined currency */
public BigDecimal getAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("Amount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Coupons Settlements ID */
public void setC_CouponsSettlements_ID (int C_CouponsSettlements_ID)
{
set_ValueNoCheck ("C_CouponsSettlements_ID", new Integer(C_CouponsSettlements_ID));
}
/** Get Coupons Settlements ID */
public int getC_CouponsSettlements_ID() 
{
Integer ii = (Integer)get_Value("C_CouponsSettlements_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit Card Coupon Filter */
public void setC_CreditCardCouponFilter_ID (int C_CreditCardCouponFilter_ID)
{
set_Value ("C_CreditCardCouponFilter_ID", new Integer(C_CreditCardCouponFilter_ID));
}
/** Get Credit Card Coupon Filter */
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
/** Set CouponNo */
public void setCouponNo (String CouponNo)
{
if (CouponNo != null && CouponNo.length() > 24)
{
log.warning("Length > 24 - truncated");
CouponNo = CouponNo.substring(0,24);
}
set_Value ("CouponNo", CouponNo);
}
/** Get CouponNo */
public String getCouponNo() 
{
return (String)get_Value("CouponNo");
}
public static final int C_PAYMENT_ID_AD_Reference_ID = MReference.getReferenceID("C_Payment");
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
/** Set Credit Card Number */
public void setCreditCardNo (String CreditCardNo)
{
if (CreditCardNo != null && CreditCardNo.length() > 24)
{
log.warning("Length > 24 - truncated");
CreditCardNo = CreditCardNo.substring(0,24);
}
set_Value ("CreditCardNo", CreditCardNo);
}
/** Get Credit Card Number */
public String getCreditCardNo() 
{
return (String)get_Value("CreditCardNo");
}
/** Set Include */
public void setInclude (boolean Include)
{
set_Value ("Include", new Boolean(Include));
}
/** Get Include */
public boolean isInclude() 
{
Object oo = get_Value("Include");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Reconciled.
Payment is reconciled with bank statement */
public void setIsReconciled (boolean IsReconciled)
{
set_Value ("IsReconciled", new Boolean(IsReconciled));
}
/** Get Reconciled.
Payment is reconciled with bank statement */
public boolean isReconciled() 
{
Object oo = get_Value("IsReconciled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int M_ENTIDADFINANCIERA_ID_AD_Reference_ID = MReference.getReferenceID("M_EntidadFinanciera");
/** Set Entidad Financiera */
public void setM_EntidadFinanciera_ID (int M_EntidadFinanciera_ID)
{
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
/** Set Trx Date */
public void setTrxDate (Timestamp TrxDate)
{
set_Value ("TrxDate", TrxDate);
}
/** Get Trx Date */
public Timestamp getTrxDate() 
{
return (Timestamp)get_Value("TrxDate");
}
}
