/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CommissionConcepts
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-11-30 14:28:20.029 */
public class X_C_CommissionConcepts extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CommissionConcepts (Properties ctx, int C_CommissionConcepts_ID, String trxName)
{
super (ctx, C_CommissionConcepts_ID, trxName);
/** if (C_CommissionConcepts_ID == 0)
{
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
protected static BigDecimal AccessLevel = new BigDecimal(4);

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
public static final int CONCEPTTYPE_AD_Reference_ID = MReference.getReferenceID("Concept Type");
/** Commission = C */
public static final String CONCEPTTYPE_Commission = "C";
/** Others = O */
public static final String CONCEPTTYPE_Others = "O";
/** Set Concept Type */
public void setConceptType (String ConceptType)
{
if (ConceptType == null || ConceptType.equals("C") || ConceptType.equals("O"));
 else throw new IllegalArgumentException ("ConceptType Invalid value - Reference = CONCEPTTYPE_AD_Reference_ID - C - O");
if (ConceptType != null && ConceptType.length() > 1)
{
log.warning("Length > 1 - truncated");
ConceptType = ConceptType.substring(0,1);
}
set_Value ("ConceptType", ConceptType);
}
/** Get Concept Type */
public String getConceptType() 
{
return (String)get_Value("ConceptType");
}
}
