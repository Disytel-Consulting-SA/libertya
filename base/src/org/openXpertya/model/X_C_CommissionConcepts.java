/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CommissionConcepts
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-30 16:10:51.684 */
public class X_C_CommissionConcepts extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CommissionConcepts (Properties ctx, int C_CommissionConcepts_ID, String trxName)
{
super (ctx, C_CommissionConcepts_ID, trxName);
/** if (C_CommissionConcepts_ID == 0)
{
setC_CardSettlementConcepts_ID (0);
setC_CommissionConcepts_ID (0);
setC_CreditCardSettlement_ID (0);
}
 */
}
/** Load Constructor */
public X_C_CommissionConcepts (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CommissionConcepts");

/** TableName=C_CommissionConcepts */
public static final String Table_Name="C_CommissionConcepts";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CommissionConcepts");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CommissionConcepts[").append(getID()).append("]");
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
public static final int C_CARDSETTLEMENTCONCEPTS_ID_AD_Reference_ID = MReference.getReferenceID("C_CardSettlementConcepts");
/** Set Card Settlement Concept */
public void setC_CardSettlementConcepts_ID (int C_CardSettlementConcepts_ID)
{
set_Value ("C_CardSettlementConcepts_ID", new Integer(C_CardSettlementConcepts_ID));
}
/** Get Card Settlement Concept */
public int getC_CardSettlementConcepts_ID() 
{
Integer ii = (Integer)get_Value("C_CardSettlementConcepts_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Commission Concepts ID */
public void setC_CommissionConcepts_ID (int C_CommissionConcepts_ID)
{
set_ValueNoCheck ("C_CommissionConcepts_ID", new Integer(C_CommissionConcepts_ID));
}
/** Get Commission Concepts ID */
public int getC_CommissionConcepts_ID() 
{
Integer ii = (Integer)get_Value("C_CommissionConcepts_ID");
if (ii == null) return 0;
return ii.intValue();
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
}
