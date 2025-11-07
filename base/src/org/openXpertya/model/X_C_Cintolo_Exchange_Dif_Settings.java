/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Cintolo_Exchange_Dif_Settings
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2025-05-20 14:00:59.183 */
public class X_C_Cintolo_Exchange_Dif_Settings extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Cintolo_Exchange_Dif_Settings (Properties ctx, int C_Cintolo_Exchange_Dif_Settings_ID, String trxName)
{
super (ctx, C_Cintolo_Exchange_Dif_Settings_ID, trxName);
/** if (C_Cintolo_Exchange_Dif_Settings_ID == 0)
{
setAmount_Limit (Env.ZERO);
setC_Cintolo_Exchange_Dif_Settings_ID (0);
setC_Currency_ID (0);
setC_DocType_ID (0);
setCredit_PriceList (0);
setDebit_PriceList (0);
setM_Product_ID (0);
setPercentage_Limit (Env.ZERO);
setPoint_Of_Sale (0);
}
 */
}
/** Load Constructor */
public X_C_Cintolo_Exchange_Dif_Settings (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Cintolo_Exchange_Dif_Settings");

/** TableName=C_Cintolo_Exchange_Dif_Settings */
public static final String Table_Name="C_Cintolo_Exchange_Dif_Settings";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Cintolo_Exchange_Dif_Settings");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Cintolo_Exchange_Dif_Settings[").append(getID()).append("]");
return sb.toString();
}
/** Set Amount Limit */
public void setAmount_Limit (BigDecimal Amount_Limit)
{
if (Amount_Limit == null) throw new IllegalArgumentException ("Amount_Limit is mandatory");
set_Value ("Amount_Limit", Amount_Limit);
}
/** Get Amount Limit */
public BigDecimal getAmount_Limit() 
{
BigDecimal bd = (BigDecimal)get_Value("Amount_Limit");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int C_BANKACCOUNT_AJUSTE_ID_AD_Reference_ID = MReference.getReferenceID("C_BankAccount");
/** Set C_BankAccount_Ajuste_ID */
public void setC_BankAccount_Ajuste_ID (int C_BankAccount_Ajuste_ID)
{
if (C_BankAccount_Ajuste_ID <= 0) set_Value ("C_BankAccount_Ajuste_ID", null);
 else 
set_Value ("C_BankAccount_Ajuste_ID", new Integer(C_BankAccount_Ajuste_ID));
}
/** Get C_BankAccount_Ajuste_ID */
public int getC_BankAccount_Ajuste_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccount_Ajuste_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Cintolo_Exchange_Dif_Settings_ID */
public void setC_Cintolo_Exchange_Dif_Settings_ID (int C_Cintolo_Exchange_Dif_Settings_ID)
{
set_ValueNoCheck ("C_Cintolo_Exchange_Dif_Settings_ID", new Integer(C_Cintolo_Exchange_Dif_Settings_ID));
}
/** Get C_Cintolo_Exchange_Dif_Settings_ID */
public int getC_Cintolo_Exchange_Dif_Settings_ID() 
{
Integer ii = (Integer)get_Value("C_Cintolo_Exchange_Dif_Settings_ID");
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
public static final int C_DOCTYPE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
set_Value ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CREDIT_PRICELIST_AD_Reference_ID = MReference.getReferenceID("M_PriceList");
/** Set Credit PriceList */
public void setCredit_PriceList (int Credit_PriceList)
{
set_Value ("Credit_PriceList", new Integer(Credit_PriceList));
}
/** Get Credit PriceList */
public int getCredit_PriceList() 
{
Integer ii = (Integer)get_Value("Credit_PriceList");
if (ii == null) return 0;
return ii.intValue();
}
public static final int DEBIT_PRICELIST_AD_Reference_ID = MReference.getReferenceID("M_PriceList");
/** Set Debit PriceList */
public void setDebit_PriceList (int Debit_PriceList)
{
set_Value ("Debit_PriceList", new Integer(Debit_PriceList));
}
/** Get Debit PriceList */
public int getDebit_PriceList() 
{
Integer ii = (Integer)get_Value("Debit_PriceList");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("M_Product (all)");
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Percentage Limit */
public void setPercentage_Limit (BigDecimal Percentage_Limit)
{
if (Percentage_Limit == null) throw new IllegalArgumentException ("Percentage_Limit is mandatory");
set_Value ("Percentage_Limit", Percentage_Limit);
}
/** Get Percentage Limit */
public BigDecimal getPercentage_Limit() 
{
BigDecimal bd = (BigDecimal)get_Value("Percentage_Limit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Point Of Sale */
public void setPoint_Of_Sale (int Point_Of_Sale)
{
set_Value ("Point_Of_Sale", new Integer(Point_Of_Sale));
}
/** Get Point Of Sale */
public int getPoint_Of_Sale() 
{
Integer ii = (Integer)get_Value("Point_Of_Sale");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Umbral_Ajuste_Aut */
public void setUmbral_Ajuste_Aut (BigDecimal Umbral_Ajuste_Aut)
{
set_Value ("Umbral_Ajuste_Aut", Umbral_Ajuste_Aut);
}
/** Get Umbral_Ajuste_Aut */
public BigDecimal getUmbral_Ajuste_Aut() 
{
BigDecimal bd = (BigDecimal)get_Value("Umbral_Ajuste_Aut");
if (bd == null) return Env.ZERO;
return bd;
}
}
