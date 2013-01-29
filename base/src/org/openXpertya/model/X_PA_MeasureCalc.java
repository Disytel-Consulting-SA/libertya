/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_MeasureCalc
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.218 */
public class X_PA_MeasureCalc extends PO
{
/** Constructor estándar */
public X_PA_MeasureCalc (Properties ctx, int PA_MeasureCalc_ID, String trxName)
{
super (ctx, PA_MeasureCalc_ID, trxName);
/** if (PA_MeasureCalc_ID == 0)
{
setDateColumn (null);	// x.Date
setName (null);
setOrgColumn (null);	// x.AD_Org_ID
setPA_MeasureCalc_ID (0);
setSelectClause (null);	// SELECT ... FROM ...
setWhereClause (null);	// WHERE ...
}
 */
}
/** Load Constructor */
public X_PA_MeasureCalc (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=442 */
public static final int Table_ID=442;

/** TableName=PA_MeasureCalc */
public static final String Table_Name="PA_MeasureCalc";

protected static KeyNamePair Model = new KeyNamePair(442,"PA_MeasureCalc");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_MeasureCalc[").append(getID()).append("]");
return sb.toString();
}
/** Set B.Partner Column.
Fully qualified Business Partner key column (C_BPartner_ID) */
public void setBPartnerColumn (String BPartnerColumn)
{
if (BPartnerColumn != null && BPartnerColumn.length() > 60)
{
log.warning("Length > 60 - truncated");
BPartnerColumn = BPartnerColumn.substring(0,59);
}
set_Value ("BPartnerColumn", BPartnerColumn);
}
/** Get B.Partner Column.
Fully qualified Business Partner key column (C_BPartner_ID) */
public String getBPartnerColumn() 
{
return (String)get_Value("BPartnerColumn");
}
/** Set Date Column.
Fully qualified date column */
public void setDateColumn (String DateColumn)
{
if (DateColumn == null) throw new IllegalArgumentException ("DateColumn is mandatory");
if (DateColumn.length() > 60)
{
log.warning("Length > 60 - truncated");
DateColumn = DateColumn.substring(0,59);
}
set_Value ("DateColumn", DateColumn);
}
/** Get Date Column.
Fully qualified date column */
public String getDateColumn() 
{
return (String)get_Value("DateColumn");
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
/** Set Org Column.
Fully qualified Organization column (AD_Org_ID) */
public void setOrgColumn (String OrgColumn)
{
if (OrgColumn == null) throw new IllegalArgumentException ("OrgColumn is mandatory");
if (OrgColumn.length() > 60)
{
log.warning("Length > 60 - truncated");
OrgColumn = OrgColumn.substring(0,59);
}
set_Value ("OrgColumn", OrgColumn);
}
/** Get Org Column.
Fully qualified Organization column (AD_Org_ID) */
public String getOrgColumn() 
{
return (String)get_Value("OrgColumn");
}
/** Set Measure Calculation.
Calculation method for measuring performance */
public void setPA_MeasureCalc_ID (int PA_MeasureCalc_ID)
{
set_ValueNoCheck ("PA_MeasureCalc_ID", new Integer(PA_MeasureCalc_ID));
}
/** Get Measure Calculation.
Calculation method for measuring performance */
public int getPA_MeasureCalc_ID() 
{
Integer ii = (Integer)get_Value("PA_MeasureCalc_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Column.
Fully qualified Product column (M_Product_ID) */
public void setProductColumn (String ProductColumn)
{
if (ProductColumn != null && ProductColumn.length() > 60)
{
log.warning("Length > 60 - truncated");
ProductColumn = ProductColumn.substring(0,59);
}
set_Value ("ProductColumn", ProductColumn);
}
/** Get Product Column.
Fully qualified Product column (M_Product_ID) */
public String getProductColumn() 
{
return (String)get_Value("ProductColumn");
}
/** Set Sql SELECT.
SQL SELECT clause */
public void setSelectClause (String SelectClause)
{
if (SelectClause == null) throw new IllegalArgumentException ("SelectClause is mandatory");
if (SelectClause.length() > 255)
{
log.warning("Length > 255 - truncated");
SelectClause = SelectClause.substring(0,254);
}
set_Value ("SelectClause", SelectClause);
}
/** Get Sql SELECT.
SQL SELECT clause */
public String getSelectClause() 
{
return (String)get_Value("SelectClause");
}
/** Set Sql WHERE.
Fully qualified SQL WHERE clause */
public void setWhereClause (String WhereClause)
{
if (WhereClause == null) throw new IllegalArgumentException ("WhereClause is mandatory");
if (WhereClause.length() > 255)
{
log.warning("Length > 255 - truncated");
WhereClause = WhereClause.substring(0,254);
}
set_Value ("WhereClause", WhereClause);
}
/** Get Sql WHERE.
Fully qualified SQL WHERE clause */
public String getWhereClause() 
{
return (String)get_Value("WhereClause");
}
}
