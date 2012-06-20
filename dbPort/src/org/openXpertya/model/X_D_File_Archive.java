/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por D_File_Archive
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.796 */
public class X_D_File_Archive extends PO
{
/** Constructor estándar */
public X_D_File_Archive (Properties ctx, int D_File_Archive_ID, String trxName)
{
super (ctx, D_File_Archive_ID, trxName);
/** if (D_File_Archive_ID == 0)
{
setD_File_Archive_ID (0);
setD_File_ID (0);
setUploaded (new Timestamp(System.currentTimeMillis()));
setUploadedBy (0);
}
 */
}
/** Load Constructor */
public X_D_File_Archive (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000132 */
public static final int Table_ID=1000132;

/** TableName=D_File_Archive */
public static final String Table_Name="D_File_Archive";

protected static KeyNamePair Model = new KeyNamePair(1000132,"D_File_Archive");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_D_File_Archive[").append(getID()).append("]");
return sb.toString();
}
/** Set Binartyfile */
public void setBinartyfile (byte[] Binartyfile)
{
set_Value ("Binartyfile", Binartyfile);
}
/** Get Binartyfile */
public byte[] getBinartyfile() 
{
return (byte[])get_Value("Binartyfile");
}
/** Set D_File_Archive_ID */
public void setD_File_Archive_ID (int D_File_Archive_ID)
{
set_ValueNoCheck ("D_File_Archive_ID", new Integer(D_File_Archive_ID));
}
/** Get D_File_Archive_ID */
public int getD_File_Archive_ID() 
{
Integer ii = (Integer)get_Value("D_File_Archive_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set D_File_ID */
public void setD_File_ID (int D_File_ID)
{
set_Value ("D_File_ID", new Integer(D_File_ID));
}
/** Get D_File_ID */
public int getD_File_ID() 
{
Integer ii = (Integer)get_Value("D_File_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Uploaded */
public void setUploaded (Timestamp Uploaded)
{
if (Uploaded == null) throw new IllegalArgumentException ("Uploaded is mandatory");
set_Value ("Uploaded", Uploaded);
}
/** Get Uploaded */
public Timestamp getUploaded() 
{
return (Timestamp)get_Value("Uploaded");
}
public static final int UPLOADEDBY_AD_Reference_ID=110;
/** Set UploadedBy */
public void setUploadedBy (int UploadedBy)
{
set_Value ("UploadedBy", new Integer(UploadedBy));
}
/** Get UploadedBy */
public int getUploadedBy() 
{
Integer ii = (Integer)get_Value("UploadedBy");
if (ii == null) return 0;
return ii.intValue();
}
}
