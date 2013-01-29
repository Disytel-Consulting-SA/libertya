/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_LibroIva
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-04-07 09:05:38.775 */
public class X_T_LibroIva extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_LibroIva (Properties ctx, int T_LibroIva_ID, String trxName)
{
super (ctx, T_LibroIva_ID, trxName);
/** if (T_LibroIva_ID == 0)
{
setAD_PInstance_ID (0);
setDateFrom (new Timestamp(System.currentTimeMillis()));
setDateTo (new Timestamp(System.currentTimeMillis()));
setT_Libroiva_ID (0);
setTransactionType (null);
}
 */
}
/** Load Constructor */
public X_T_LibroIva (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_LibroIva");

/** TableName=T_LibroIva */
public static final String Table_Name="T_LibroIva";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_LibroIva");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_LibroIva[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Category Name */
public void setcategoria_name (String categoria_name)
{
if (categoria_name != null && categoria_name.length() > 40)
{
log.warning("Length > 40 - truncated");
categoria_name = categoria_name.substring(0,40);
}
set_Value ("categoria_name", categoria_name);
}
/** Get Category Name */
public String getcategoria_name() 
{
return (String)get_Value("categoria_name");
}
public static final int C_BPARTNER_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (No Summary)");
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
/** Set Business Partner Name */
public void setC_Bpartner_name (String C_Bpartner_name)
{
if (C_Bpartner_name != null && C_Bpartner_name.length() > 40)
{
log.warning("Length > 40 - truncated");
C_Bpartner_name = C_Bpartner_name.substring(0,40);
}
set_Value ("C_Bpartner_name", C_Bpartner_name);
}
/** Get Business Partner Name */
public String getC_Bpartner_name() 
{
return (String)get_Value("C_Bpartner_name");
}
public static final int C_CATEGORIAIVA_ID_AD_Reference_ID = MReference.getReferenceID("C_Categoria_IVA");
/** Set Categoría IVA */
public void setC_Categoriaiva_ID (int C_Categoriaiva_ID)
{
if (C_Categoriaiva_ID <= 0) set_Value ("C_Categoriaiva_ID", null);
 else 
set_Value ("C_Categoriaiva_ID", new Integer(C_Categoriaiva_ID));
}
/** Get Categoría IVA */
public int getC_Categoriaiva_ID() 
{
Integer ii = (Integer)get_Value("C_Categoriaiva_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
if (C_DocType_ID <= 0) set_Value ("C_DocType_ID", null);
 else 
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
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Account Date.
Accounting Date */
public void setDateAcct (Timestamp DateAcct)
{
set_Value ("DateAcct", DateAcct);
}
/** Get Account Date.
Accounting Date */
public Timestamp getDateAcct() 
{
return (Timestamp)get_Value("DateAcct");
}
/** Set Date From.
Starting date for a range */
public void setDateFrom (Timestamp DateFrom)
{
if (DateFrom == null) throw new IllegalArgumentException ("DateFrom is mandatory");
set_Value ("DateFrom", DateFrom);
}
/** Get Date From.
Starting date for a range */
public Timestamp getDateFrom() 
{
return (Timestamp)get_Value("DateFrom");
}
/** Set Date To.
End date of a date range */
public void setDateTo (Timestamp DateTo)
{
if (DateTo == null) throw new IllegalArgumentException ("DateTo is mandatory");
set_Value ("DateTo", DateTo);
}
/** Get Date To.
End date of a date range */
public Timestamp getDateTo() 
{
return (Timestamp)get_Value("DateTo");
}
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 40)
{
log.warning("Length > 40 - truncated");
DocumentNo = DocumentNo.substring(0,40);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Número de Ingresos Brutos */
public void setIIBB (String IIBB)
{
if (IIBB != null && IIBB.length() > 128)
{
log.warning("Length > 128 - truncated");
IIBB = IIBB.substring(0,128);
}
set_Value ("IIBB", IIBB);
}
/** Get Número de Ingresos Brutos */
public String getIIBB() 
{
return (String)get_Value("IIBB");
}
/** Set Importe */
public void setImporte (BigDecimal Importe)
{
set_Value ("Importe", Importe);
}
/** Get Importe */
public BigDecimal getImporte() 
{
BigDecimal bd = (BigDecimal)get_Value("Importe");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Item */
public void setitem (String item)
{
if (item != null && item.length() > 40)
{
log.warning("Length > 40 - truncated");
item = item.substring(0,40);
}
set_Value ("item", item);
}
/** Get Item */
public String getitem() 
{
return (String)get_Value("item");
}
/** Set Neto */
public void setneto (BigDecimal neto)
{
set_Value ("neto", neto);
}
/** Get Neto */
public BigDecimal getneto() 
{
BigDecimal bd = (BigDecimal)get_Value("neto");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Tax ID.
Tax Identification */
public void setTaxID (String TaxID)
{
if (TaxID != null && TaxID.length() > 40)
{
log.warning("Length > 40 - truncated");
TaxID = TaxID.substring(0,40);
}
set_Value ("TaxID", TaxID);
}
/** Get Tax ID.
Tax Identification */
public String getTaxID() 
{
return (String)get_Value("TaxID");
}
/** Set Libro IVA */
public void setT_Libroiva_ID (int T_Libroiva_ID)
{
set_ValueNoCheck ("T_Libroiva_ID", new Integer(T_Libroiva_ID));
}
/** Get Libro IVA */
public int getT_Libroiva_ID() 
{
Integer ii = (Integer)get_Value("T_Libroiva_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Total Facturado */
public void settotalfacturado (BigDecimal totalfacturado)
{
set_Value ("totalfacturado", totalfacturado);
}
/** Get Total Facturado */
public BigDecimal gettotalfacturado() 
{
BigDecimal bd = (BigDecimal)get_Value("totalfacturado");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Transaction Type */
public void setTransactionType (String TransactionType)
{
if (TransactionType == null) throw new IllegalArgumentException ("TransactionType is mandatory");
if (TransactionType.length() > 10)
{
log.warning("Length > 10 - truncated");
TransactionType = TransactionType.substring(0,10);
}
set_Value ("TransactionType", TransactionType);
}
/** Get Transaction Type */
public String getTransactionType() 
{
return (String)get_Value("TransactionType");
}
}
