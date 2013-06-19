/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_AcctSchema_Element
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-06-19 16:09:09.492 */
public class X_C_AcctSchema_Element extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_AcctSchema_Element (Properties ctx, int C_AcctSchema_Element_ID, String trxName)
{
super (ctx, C_AcctSchema_Element_ID, trxName);
/** if (C_AcctSchema_Element_ID == 0)
{
setC_AcctSchema_Element_ID (0);
setC_AcctSchema_ID (0);
setC_Element_ID (0);
setElementType (null);
setIsBalanced (false);
setIsMandatory (false);
setName (null);
setOrg_ID (0);
setSeqNo (0);
}
 */
}
/** Load Constructor */
public X_C_AcctSchema_Element (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_AcctSchema_Element");

/** TableName=C_AcctSchema_Element */
public static final String Table_Name="C_AcctSchema_Element";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_AcctSchema_Element");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_AcctSchema_Element[").append(getID()).append("]");
return sb.toString();
}
/** Set Column.
Column in the table */
public void setAD_Column_ID (int AD_Column_ID)
{
if (AD_Column_ID <= 0) set_Value ("AD_Column_ID", null);
 else 
set_Value ("AD_Column_ID", new Integer(AD_Column_ID));
}
/** Get Column.
Column in the table */
public int getAD_Column_ID() 
{
Integer ii = (Integer)get_Value("AD_Column_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Acct.Schema Element */
public void setC_AcctSchema_Element_ID (int C_AcctSchema_Element_ID)
{
set_ValueNoCheck ("C_AcctSchema_Element_ID", new Integer(C_AcctSchema_Element_ID));
}
/** Get Acct.Schema Element */
public int getC_AcctSchema_Element_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_Element_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_ValueNoCheck ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Activity.
Business Activity */
public void setC_Activity_ID (int C_Activity_ID)
{
if (C_Activity_ID <= 0) set_Value ("C_Activity_ID", null);
 else 
set_Value ("C_Activity_ID", new Integer(C_Activity_ID));
}
/** Get Activity.
Business Activity */
public int getC_Activity_ID() 
{
Integer ii = (Integer)get_Value("C_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Campaign.
Marketing Campaign */
public void setC_Campaign_ID (int C_Campaign_ID)
{
if (C_Campaign_ID <= 0) set_Value ("C_Campaign_ID", null);
 else 
set_Value ("C_Campaign_ID", new Integer(C_Campaign_ID));
}
/** Get Campaign.
Marketing Campaign */
public int getC_Campaign_ID() 
{
Integer ii = (Integer)get_Value("C_Campaign_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Element.
Accounting Element */
public void setC_Element_ID (int C_Element_ID)
{
set_Value ("C_Element_ID", new Integer(C_Element_ID));
}
/** Get Element.
Accounting Element */
public int getC_Element_ID() 
{
Integer ii = (Integer)get_Value("C_Element_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Account Element.
Account Element */
public void setC_ElementValue_ID (int C_ElementValue_ID)
{
if (C_ElementValue_ID <= 0) set_Value ("C_ElementValue_ID", null);
 else 
set_Value ("C_ElementValue_ID", new Integer(C_ElementValue_ID));
}
/** Get Account Element.
Account Element */
public int getC_ElementValue_ID() 
{
Integer ii = (Integer)get_Value("C_ElementValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Address.
Location or Address */
public void setC_Location_ID (int C_Location_ID)
{
if (C_Location_ID <= 0) set_Value ("C_Location_ID", null);
 else 
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get Address.
Location or Address */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
if (C_Project_ID <= 0) set_Value ("C_Project_ID", null);
 else 
set_Value ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sales Region.
Sales coverage region */
public void setC_SalesRegion_ID (int C_SalesRegion_ID)
{
if (C_SalesRegion_ID <= 0) set_Value ("C_SalesRegion_ID", null);
 else 
set_Value ("C_SalesRegion_ID", new Integer(C_SalesRegion_ID));
}
/** Get Sales Region.
Sales coverage region */
public int getC_SalesRegion_ID() 
{
Integer ii = (Integer)get_Value("C_SalesRegion_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ELEMENTTYPE_AD_Reference_ID = MReference.getReferenceID("C_AcctSchema ElementType");
/** Activity = AY */
public static final String ELEMENTTYPE_Activity = "AY";
/** Org = OO */
public static final String ELEMENTTYPE_Org = "OO";
/** Account = AC */
public static final String ELEMENTTYPE_Account = "AC";
/** Product = PR */
public static final String ELEMENTTYPE_Product = "PR";
/** BPartner = BP */
public static final String ELEMENTTYPE_BPartner = "BP";
/** Org Trx = OT */
public static final String ELEMENTTYPE_OrgTrx = "OT";
/** Location From = LF */
public static final String ELEMENTTYPE_LocationFrom = "LF";
/** Location To = LT */
public static final String ELEMENTTYPE_LocationTo = "LT";
/** Sales Region = SR */
public static final String ELEMENTTYPE_SalesRegion = "SR";
/** Project = PJ */
public static final String ELEMENTTYPE_Project = "PJ";
/** Campaign = MC */
public static final String ELEMENTTYPE_Campaign = "MC";
/** User 1 = U1 */
public static final String ELEMENTTYPE_User1 = "U1";
/** User 2 = U2 */
public static final String ELEMENTTYPE_User2 = "U2";
/** Set Type.
Element Type (account or user defined) */
public void setElementType (String ElementType)
{
if (ElementType.equals("AY") || ElementType.equals("OO") || ElementType.equals("AC") || ElementType.equals("PR") || ElementType.equals("BP") || ElementType.equals("OT") || ElementType.equals("LF") || ElementType.equals("LT") || ElementType.equals("SR") || ElementType.equals("PJ") || ElementType.equals("MC") || ElementType.equals("U1") || ElementType.equals("U2"));
 else throw new IllegalArgumentException ("ElementType Invalid value - Reference = ELEMENTTYPE_AD_Reference_ID - AY - OO - AC - PR - BP - OT - LF - LT - SR - PJ - MC - U1 - U2");
if (ElementType == null) throw new IllegalArgumentException ("ElementType is mandatory");
if (ElementType.length() > 2)
{
log.warning("Length > 2 - truncated");
ElementType = ElementType.substring(0,2);
}
set_Value ("ElementType", ElementType);
}
/** Get Type.
Element Type (account or user defined) */
public String getElementType() 
{
return (String)get_Value("ElementType");
}
/** Set Balanced */
public void setIsBalanced (boolean IsBalanced)
{
set_Value ("IsBalanced", new Boolean(IsBalanced));
}
/** Get Balanced */
public boolean isBalanced() 
{
Object oo = get_Value("IsBalanced");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Mandatory.
Data entry is required in this column */
public void setIsMandatory (boolean IsMandatory)
{
set_Value ("IsMandatory", new Boolean(IsMandatory));
}
/** Get Mandatory.
Data entry is required in this column */
public boolean isMandatory() 
{
Object oo = get_Value("IsMandatory");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
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
public static final int ORG_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (Trx)");
/** Set Organization.
Organizational entity within client */
public void setOrg_ID (int Org_ID)
{
set_Value ("Org_ID", new Integer(Org_ID));
}
/** Get Organization.
Organizational entity within client */
public int getOrg_ID() 
{
Integer ii = (Integer)get_Value("Org_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
}
