/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Promotion_Code_Batch
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2018-10-03 12:02:42.938 */
public class X_C_Promotion_Code_Batch extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Promotion_Code_Batch (Properties ctx, int C_Promotion_Code_Batch_ID, String trxName)
{
super (ctx, C_Promotion_Code_Batch_ID, trxName);
/** if (C_Promotion_Code_Batch_ID == 0)
{
setC_Promotion_Code_Batch_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));
setDocumentNo (null);
setProcessed (false);
setVoidBatch (null);
}
 */
}
/** Load Constructor */
public X_C_Promotion_Code_Batch (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Promotion_Code_Batch");

/** TableName=C_Promotion_Code_Batch */
public static final String Table_Name="C_Promotion_Code_Batch";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Promotion_Code_Batch");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Promotion_Code_Batch[").append(getID()).append("]");
return sb.toString();
}
/** Set Promotional Code Batch */
public void setC_Promotion_Code_Batch_ID (int C_Promotion_Code_Batch_ID)
{
set_ValueNoCheck ("C_Promotion_Code_Batch_ID", new Integer(C_Promotion_Code_Batch_ID));
}
/** Get Promotional Code Batch */
public int getC_Promotion_Code_Batch_ID() 
{
Integer ii = (Integer)get_Value("C_Promotion_Code_Batch_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
if (DateTrx == null) throw new IllegalArgumentException ("DateTrx is mandatory");
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction Date.
Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,30);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDocumentNo());
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int VOIDBATCH_AD_Reference_ID = MReference.getReferenceID("Promotional Code Batch Void");
/** Void Batch = N */
public static final String VOIDBATCH_VoidBatch = "N";
/** Batch Voided = Y */
public static final String VOIDBATCH_BatchVoided = "Y";
/** Set Void Batch.
Void promotional codes includes in this batch */
public void setVoidBatch (String VoidBatch)
{
if (VoidBatch.equals("N") || VoidBatch.equals("Y") || ( refContainsValue("CORE-AD_Reference-1010408", VoidBatch) ) );
 else throw new IllegalArgumentException ("VoidBatch Invalid value: " + VoidBatch + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010408") );
if (VoidBatch == null) throw new IllegalArgumentException ("VoidBatch is mandatory");
if (VoidBatch.length() > 1)
{
log.warning("Length > 1 - truncated");
VoidBatch = VoidBatch.substring(0,1);
}
set_Value ("VoidBatch", VoidBatch);
}
/** Get Void Batch.
Void promotional codes includes in this batch */
public String getVoidBatch() 
{
return (String)get_Value("VoidBatch");
}
}
