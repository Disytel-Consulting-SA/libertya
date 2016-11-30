/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PerceptionsSettlement
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-11-30 14:28:52.947 */
public class X_C_PerceptionsSettlement extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_PerceptionsSettlement (Properties ctx, int C_PerceptionsSettlement_ID, String trxName)
{
super (ctx, C_PerceptionsSettlement_ID, trxName);
/** if (C_PerceptionsSettlement_ID == 0)
{
setC_CreditCardSettlement_ID (0);
setC_PerceptionsSettlement_ID (0);
setC_TaxCategory_ID (0);
setInternalNo (null);
}
 */
}
/** Load Constructor */
public X_C_PerceptionsSettlement (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_PerceptionsSettlement");

/** TableName=C_PerceptionsSettlement */
public static final String Table_Name="C_PerceptionsSettlement";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_PerceptionsSettlement");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PerceptionsSettlement[").append(getID()).append("]");
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
/** Set Perceptions Settlement ID */
public void setC_PerceptionsSettlement_ID (int C_PerceptionsSettlement_ID)
{
set_ValueNoCheck ("C_PerceptionsSettlement_ID", new Integer(C_PerceptionsSettlement_ID));
}
/** Get Perceptions Settlement ID */
public int getC_PerceptionsSettlement_ID() 
{
Integer ii = (Integer)get_Value("C_PerceptionsSettlement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Category.
Tax Category */
public void setC_TaxCategory_ID (int C_TaxCategory_ID)
{
set_Value ("C_TaxCategory_ID", new Integer(C_TaxCategory_ID));
}
/** Get Tax Category.
Tax Category */
public int getC_TaxCategory_ID() 
{
Integer ii = (Integer)get_Value("C_TaxCategory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Internal Number */
public void setInternalNo (String InternalNo)
{
if (InternalNo == null) throw new IllegalArgumentException ("InternalNo is mandatory");
if (InternalNo.length() > 24)
{
log.warning("Length > 24 - truncated");
InternalNo = InternalNo.substring(0,24);
}
set_Value ("InternalNo", InternalNo);
}
/** Get Internal Number */
public String getInternalNo() 
{
return (String)get_Value("InternalNo");
}
}
