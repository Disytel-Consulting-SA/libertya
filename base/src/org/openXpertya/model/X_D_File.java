/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por D_File
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.765 */
public class X_D_File extends PO
{
/** Constructor estándar */
public X_D_File (Properties ctx, int D_File_ID, String trxName)
{
super (ctx, D_File_ID, trxName);
/** if (D_File_ID == 0)
{
setD_File_ID (0);
setD_Filetype_ID (0);
setName (null);
setNameList (null);
setUploadedBy (0);
}
 */
}
/** Load Constructor */
public X_D_File (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000106 */
public static final int Table_ID=1000106;

/** TableName=D_File */
public static final String Table_Name="D_File";

protected static KeyNamePair Model = new KeyNamePair(1000106,"D_File");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_D_File[").append(getID()).append("]");
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
public static final int D_ENTITY_ID_AD_Reference_ID=138;
/** Set D_Entity_ID */
public void setD_Entity_ID (int D_Entity_ID)
{
if (D_Entity_ID <= 0) set_Value ("D_Entity_ID", null);
 else 
set_Value ("D_Entity_ID", new Integer(D_Entity_ID));
}
/** Get D_Entity_ID */
public int getD_Entity_ID() 
{
Integer ii = (Integer)get_Value("D_Entity_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set D_File_ID */
public void setD_File_ID (int D_File_ID)
{
set_ValueNoCheck ("D_File_ID", new Integer(D_File_ID));
}
/** Get D_File_ID */
public int getD_File_ID() 
{
Integer ii = (Integer)get_Value("D_File_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int D_FILETYPE_ID_AD_Reference_ID=1000066;
/** Set D_Filetype_ID */
public void setD_Filetype_ID (int D_Filetype_ID)
{
set_Value ("D_Filetype_ID", new Integer(D_Filetype_ID));
}
/** Get D_Filetype_ID */
public int getD_Filetype_ID() 
{
Integer ii = (Integer)get_Value("D_Filetype_ID");
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
public static final int FILESTATUS_AD_Reference_ID=1000067;
/** Approved = AP */
public static final String FILESTATUS_Approved = "AP";
/** Draft = DR */
public static final String FILESTATUS_Draft = "DR";
/** Set FileStatus */
public void setFileStatus (String FileStatus)
{
if (FileStatus == null || FileStatus.equals("AP") || FileStatus.equals("DR"));
 else throw new IllegalArgumentException ("FileStatus Invalid value - Reference_ID=1000067 - AP - DR");
if (FileStatus != null && FileStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
FileStatus = FileStatus.substring(0,1);
}
set_Value ("FileStatus", FileStatus);
}
/** Get FileStatus */
public String getFileStatus() 
{
return (String)get_Value("FileStatus");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 12)
{
log.warning("Length > 12 - truncated");
Name = Name.substring(0,11);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set NameList */
public void setNameList (String NameList)
{
if (NameList == null) throw new IllegalArgumentException ("NameList is mandatory");
if (NameList.length() > 12)
{
log.warning("Length > 12 - truncated");
NameList = NameList.substring(0,11);
}
set_Value ("NameList", NameList);
}
/** Get NameList */
public String getNameList() 
{
return (String)get_Value("NameList");
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
