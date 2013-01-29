/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ProjectType
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.187 */
public class X_C_ProjectType extends PO
{
/** Constructor estándar */
public X_C_ProjectType (Properties ctx, int C_ProjectType_ID, String trxName)
{
super (ctx, C_ProjectType_ID, trxName);
/** if (C_ProjectType_ID == 0)
{
setC_ProjectType_ID (0);
setName (null);
setProjectCategory (null);	// N
}
 */
}
/** Load Constructor */
public X_C_ProjectType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=575 */
public static final int Table_ID=575;

/** TableName=C_ProjectType */
public static final String Table_Name="C_ProjectType";

protected static KeyNamePair Model = new KeyNamePair(575,"C_ProjectType");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ProjectType[").append(getID()).append("]");
return sb.toString();
}
/** Set Project Type.
Type of the project */
public void setC_ProjectType_ID (int C_ProjectType_ID)
{
set_ValueNoCheck ("C_ProjectType_ID", new Integer(C_ProjectType_ID));
}
/** Get Project Type.
Type of the project */
public int getC_ProjectType_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectType_ID");
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
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
public static final int PROJECTCATEGORY_AD_Reference_ID=288;
/** Service (Charge) Project = S */
public static final String PROJECTCATEGORY_ServiceChargeProject = "S";
/** General = N */
public static final String PROJECTCATEGORY_General = "N";
/** Asset Project = A */
public static final String PROJECTCATEGORY_AssetProject = "A";
/** Work Order (Job) = W */
public static final String PROJECTCATEGORY_WorkOrderJob = "W";
/** Set Project Category.
Project Category */
public void setProjectCategory (String ProjectCategory)
{
if (ProjectCategory.equals("S") || ProjectCategory.equals("N") || ProjectCategory.equals("A") || ProjectCategory.equals("W"));
 else throw new IllegalArgumentException ("ProjectCategory Invalid value - Reference_ID=288 - S - N - A - W");
if (ProjectCategory == null) throw new IllegalArgumentException ("ProjectCategory is mandatory");
if (ProjectCategory.length() > 1)
{
log.warning("Length > 1 - truncated");
ProjectCategory = ProjectCategory.substring(0,0);
}
set_ValueNoCheck ("ProjectCategory", ProjectCategory);
}
/** Get Project Category.
Project Category */
public String getProjectCategory() 
{
return (String)get_Value("ProjectCategory");
}
}
