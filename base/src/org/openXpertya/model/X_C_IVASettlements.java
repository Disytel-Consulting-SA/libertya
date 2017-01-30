/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_IVASettlements
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-30 16:11:11.551 */
public class X_C_IVASettlements extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_IVASettlements (Properties ctx, int C_IVASettlements_ID, String trxName)
{
super (ctx, C_IVASettlements_ID, trxName);
/** if (C_IVASettlements_ID == 0)
{
setC_CreditCardSettlement_ID (0);
setC_IVASettlements_ID (0);
setC_Tax_ID (0);
}
 */
}
/** Load Constructor */
public X_C_IVASettlements (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_IVASettlements");

/** TableName=C_IVASettlements */
public static final String Table_Name="C_IVASettlements";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_IVASettlements");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_IVASettlements[").append(getID()).append("]");
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
/** Set IVA Settlements */
public void setC_IVASettlements_ID (int C_IVASettlements_ID)
{
set_ValueNoCheck ("C_IVASettlements_ID", new Integer(C_IVASettlements_ID));
}
/** Get IVA Settlements */
public int getC_IVASettlements_ID() 
{
Integer ii = (Integer)get_Value("C_IVASettlements_ID");
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
}
