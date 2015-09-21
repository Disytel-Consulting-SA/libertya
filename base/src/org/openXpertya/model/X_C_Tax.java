/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Tax
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-09-20 23:16:28.43 */
public class X_C_Tax extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Tax (Properties ctx, int C_Tax_ID, String trxName)
{
super (ctx, C_Tax_ID, trxName);
/** if (C_Tax_ID == 0)
{
setC_TaxCategory_ID (0);
setC_Tax_ID (0);
setIsDefault (false);
setIsDocumentLevel (false);
setIsPercepcion (false);
setIsSummary (false);
setIsTaxExempt (false);
setName (null);
setRate (Env.ZERO);
setRequiresTaxCertificate (false);
setSOPOType (null);	// B
setTaxType (null);	// G
setValidFrom (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_Tax (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Tax");

/** TableName=C_Tax */
public static final String Table_Name="C_Tax";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Tax");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Tax[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set Arciba Norm Code */
public void setArcibaNormCode (String ArcibaNormCode)
{
if (ArcibaNormCode != null && ArcibaNormCode.length() > 10)
{
log.warning("Length > 10 - truncated");
ArcibaNormCode = ArcibaNormCode.substring(0,10);
}
set_Value ("ArcibaNormCode", ArcibaNormCode);
}
/** Get Arciba Norm Code */
public String getArcibaNormCode() 
{
return (String)get_Value("ArcibaNormCode");
}
/** Set Business Partner Group.
Business Partner Group */
public void setC_BP_Group_ID (int C_BP_Group_ID)
{
if (C_BP_Group_ID <= 0) set_Value ("C_BP_Group_ID", null);
 else 
set_Value ("C_BP_Group_ID", new Integer(C_BP_Group_ID));
}
/** Get Business Partner Group.
Business Partner Group */
public int getC_BP_Group_ID() 
{
Integer ii = (Integer)get_Value("C_BP_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_COUNTRY_ID_AD_Reference_ID = MReference.getReferenceID("C_Country");
/** Set Country.
Country  */
public void setC_Country_ID (int C_Country_ID)
{
if (C_Country_ID <= 0) set_Value ("C_Country_ID", null);
 else 
set_Value ("C_Country_ID", new Integer(C_Country_ID));
}
/** Get Country.
Country  */
public int getC_Country_ID() 
{
Integer ii = (Integer)get_Value("C_Country_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CODIGOOPERACION_AD_Reference_ID = MReference.getReferenceID("Operation Code");
/** Exportaciones a la zona franca = Z */
public static final String CODIGOOPERACION_ExportacionesALaZonaFranca = "Z";
/** Exportaciones al Exterior = X */
public static final String CODIGOOPERACION_ExportacionesAlExterior = "X";
/** Operaciones Exentas = E */
public static final String CODIGOOPERACION_OperacionesExentas = "E";
/** No Gravado = N */
public static final String CODIGOOPERACION_NoGravado = "N";
/** Operaciones de Canje = C */
public static final String CODIGOOPERACION_OperacionesDeCanje = "C";
/** Set Código Operación */
public void setCodigoOperacion (String CodigoOperacion)
{
if (CodigoOperacion == null || CodigoOperacion.equals("Z") || CodigoOperacion.equals("X") || CodigoOperacion.equals("E") || CodigoOperacion.equals("N") || CodigoOperacion.equals("C"));
 else throw new IllegalArgumentException ("CodigoOperacion Invalid value - Reference = CODIGOOPERACION_AD_Reference_ID - Z - X - E - N - C");
if (CodigoOperacion != null && CodigoOperacion.length() > 1)
{
log.warning("Length > 1 - truncated");
CodigoOperacion = CodigoOperacion.substring(0,1);
}
set_Value ("CodigoOperacion", CodigoOperacion);
}
/** Get Código Operación */
public String getCodigoOperacion() 
{
return (String)get_Value("CodigoOperacion");
}
public static final int C_REGION_ID_AD_Reference_ID = MReference.getReferenceID("C_Region");
/** Set Region.
Identifies a geographical Region */
public void setC_Region_ID (int C_Region_ID)
{
if (C_Region_ID <= 0) set_Value ("C_Region_ID", null);
 else 
set_Value ("C_Region_ID", new Integer(C_Region_ID));
}
/** Get Region.
Identifies a geographical Region */
public int getC_Region_ID() 
{
Integer ii = (Integer)get_Value("C_Region_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Category.
Tax Category */
public void setC_TaxCategory_ID (int C_TaxCategory_ID)
{
set_Value ("C_TaxCategory_ID", new Integer(C_TaxCategory_ID));
}
/** Get Tax Category.
Tax Category */
public int getC_TaxCategory_ID() 
{
Integer ii = (Integer)get_Value("C_TaxCategory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax.
Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
set_ValueNoCheck ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get Tax.
Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
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
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Document Level.
Tax is calculated on document level (rather than line by line) */
public void setIsDocumentLevel (boolean IsDocumentLevel)
{
set_Value ("IsDocumentLevel", new Boolean(IsDocumentLevel));
}
/** Get Document Level.
Tax is calculated on document level (rather than line by line) */
public boolean isDocumentLevel() 
{
Object oo = get_Value("IsDocumentLevel");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Percepcion */
public void setIsPercepcion (boolean IsPercepcion)
{
set_Value ("IsPercepcion", new Boolean(IsPercepcion));
}
/** Get Is Percepcion */
public boolean isPercepcion() 
{
Object oo = get_Value("IsPercepcion");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Tax exempt.
Business partner is exempt from tax */
public void setIsTaxExempt (boolean IsTaxExempt)
{
set_Value ("IsTaxExempt", new Boolean(IsTaxExempt));
}
/** Get Tax exempt.
Business partner is exempt from tax */
public boolean isTaxExempt() 
{
Object oo = get_Value("IsTaxExempt");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
if (M_Product_Category_ID <= 0) set_Value ("M_Product_Category_ID", null);
 else 
set_Value ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int PARENT_TAX_ID_AD_Reference_ID = MReference.getReferenceID("C_Tax");
/** Set Parent Tax.
Parent Tax indicates a tax that is made up of multiple taxes */
public void setParent_Tax_ID (int Parent_Tax_ID)
{
if (Parent_Tax_ID <= 0) set_Value ("Parent_Tax_ID", null);
 else 
set_Value ("Parent_Tax_ID", new Integer(Parent_Tax_ID));
}
/** Get Parent Tax.
Parent Tax indicates a tax that is made up of multiple taxes */
public int getParent_Tax_ID() 
{
Integer ii = (Integer)get_Value("Parent_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PERCEPTIONTYPE_AD_Reference_ID = MReference.getReferenceID("Retention - Perception Type");
/** Ganancias = G */
public static final String PERCEPTIONTYPE_Ganancias = "G";
/** SIJP = J */
public static final String PERCEPTIONTYPE_SIJP = "J";
/** Suss = S */
public static final String PERCEPTIONTYPE_Suss = "S";
/** IVA = I */
public static final String PERCEPTIONTYPE_IVA = "I";
/** Ingresos Brutos = B */
public static final String PERCEPTIONTYPE_IngresosBrutos = "B";
/** Set PerceptionType */
public void setPerceptionType (String PerceptionType)
{
if (PerceptionType == null || PerceptionType.equals("G") || PerceptionType.equals("J") || PerceptionType.equals("S") || PerceptionType.equals("I") || PerceptionType.equals("B"));
 else throw new IllegalArgumentException ("PerceptionType Invalid value - Reference = PERCEPTIONTYPE_AD_Reference_ID - G - J - S - I - B");
if (PerceptionType != null && PerceptionType.length() > 1)
{
log.warning("Length > 1 - truncated");
PerceptionType = PerceptionType.substring(0,1);
}
set_Value ("PerceptionType", PerceptionType);
}
/** Get PerceptionType */
public String getPerceptionType() 
{
return (String)get_Value("PerceptionType");
}
/** Set Rate.
Rate or Tax or Exchange */
public void setRate (BigDecimal Rate)
{
if (Rate == null) throw new IllegalArgumentException ("Rate is mandatory");
set_Value ("Rate", Rate);
}
/** Get Rate.
Rate or Tax or Exchange */
public BigDecimal getRate() 
{
BigDecimal bd = (BigDecimal)get_Value("Rate");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Requires Tax Certificate.
This tax rate requires the Business Partner to be tax exempt */
public void setRequiresTaxCertificate (boolean RequiresTaxCertificate)
{
set_Value ("RequiresTaxCertificate", new Boolean(RequiresTaxCertificate));
}
/** Get Requires Tax Certificate.
This tax rate requires the Business Partner to be tax exempt */
public boolean isRequiresTaxCertificate() 
{
Object oo = get_Value("RequiresTaxCertificate");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int SOPOTYPE_AD_Reference_ID = MReference.getReferenceID("C_Tax SPPOType");
/** Both = B */
public static final String SOPOTYPE_Both = "B";
/** Sales Tax = S */
public static final String SOPOTYPE_SalesTax = "S";
/** Purchase Tax = P */
public static final String SOPOTYPE_PurchaseTax = "P";
/** Set SO/PO Type.
Sales Tax applies to sales situations, Purchase Tax to purchase situations */
public void setSOPOType (String SOPOType)
{
if (SOPOType.equals("B") || SOPOType.equals("S") || SOPOType.equals("P"));
 else throw new IllegalArgumentException ("SOPOType Invalid value - Reference = SOPOTYPE_AD_Reference_ID - B - S - P");
if (SOPOType == null) throw new IllegalArgumentException ("SOPOType is mandatory");
if (SOPOType.length() > 1)
{
log.warning("Length > 1 - truncated");
SOPOType = SOPOType.substring(0,1);
}
set_Value ("SOPOType", SOPOType);
}
/** Get SO/PO Type.
Sales Tax applies to sales situations, Purchase Tax to purchase situations */
public String getSOPOType() 
{
return (String)get_Value("SOPOType");
}
public static final int TAXACCUSATION_AD_Reference_ID = MReference.getReferenceID("C_Tax Accusation");
/** Direct Encumbered = G */
public static final String TAXACCUSATION_DirectEncumbered = "G";
/** Not Computable = N */
public static final String TAXACCUSATION_NotComputable = "N";
/** Prorrateable = P */
public static final String TAXACCUSATION_Prorrateable = "P";
/** Set Tax Accusation.
Tax acussation method */
public void setTaxAccusation (String TaxAccusation)
{
if (TaxAccusation == null || TaxAccusation.equals("G") || TaxAccusation.equals("N") || TaxAccusation.equals("P"));
 else throw new IllegalArgumentException ("TaxAccusation Invalid value - Reference = TAXACCUSATION_AD_Reference_ID - G - N - P");
if (TaxAccusation != null && TaxAccusation.length() > 1)
{
log.warning("Length > 1 - truncated");
TaxAccusation = TaxAccusation.substring(0,1);
}
set_Value ("TaxAccusation", TaxAccusation);
}
/** Get Tax Accusation.
Tax acussation method */
public String getTaxAccusation() 
{
return (String)get_Value("TaxAccusation");
}
/** Set Tax Indicator.
Short form for Tax to be printed on documents */
public void setTaxIndicator (String TaxIndicator)
{
if (TaxIndicator != null && TaxIndicator.length() > 10)
{
log.warning("Length > 10 - truncated");
TaxIndicator = TaxIndicator.substring(0,10);
}
set_Value ("TaxIndicator", TaxIndicator);
}
/** Get Tax Indicator.
Short form for Tax to be printed on documents */
public String getTaxIndicator() 
{
return (String)get_Value("TaxIndicator");
}
public static final int TAXTYPE_AD_Reference_ID = MReference.getReferenceID("C_Tax Type");
/** General  = G */
public static final String TAXTYPE_General = "G";
/** By Category = C */
public static final String TAXTYPE_ByCategory = "C";
/** By Product = P */
public static final String TAXTYPE_ByProduct = "P";
/** Set Tax Type */
public void setTaxType (String TaxType)
{
if (TaxType.equals("G") || TaxType.equals("C") || TaxType.equals("P"));
 else throw new IllegalArgumentException ("TaxType Invalid value - Reference = TAXTYPE_AD_Reference_ID - G - C - P");
if (TaxType == null) throw new IllegalArgumentException ("TaxType is mandatory");
if (TaxType.length() > 1)
{
log.warning("Length > 1 - truncated");
TaxType = TaxType.substring(0,1);
}
set_Value ("TaxType", TaxType);
}
/** Get Tax Type */
public String getTaxType() 
{
return (String)get_Value("TaxType");
}
public static final int TO_COUNTRY_ID_AD_Reference_ID = MReference.getReferenceID("C_Country");
/** Set To.
Receiving Country */
public void setTo_Country_ID (int To_Country_ID)
{
if (To_Country_ID <= 0) set_Value ("To_Country_ID", null);
 else 
set_Value ("To_Country_ID", new Integer(To_Country_ID));
}
/** Get To.
Receiving Country */
public int getTo_Country_ID() 
{
Integer ii = (Integer)get_Value("To_Country_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int TO_REGION_ID_AD_Reference_ID = MReference.getReferenceID("C_Region");
/** Set To.
Receiving Region */
public void setTo_Region_ID (int To_Region_ID)
{
if (To_Region_ID <= 0) set_Value ("To_Region_ID", null);
 else 
set_Value ("To_Region_ID", new Integer(To_Region_ID));
}
/** Get To.
Receiving Region */
public int getTo_Region_ID() 
{
Integer ii = (Integer)get_Value("To_Region_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set WSFE Code.
WSFE Code */
public void setWSFECode (int WSFECode)
{
set_Value ("WSFECode", new Integer(WSFECode));
}
/** Get WSFE Code.
WSFE Code */
public int getWSFECode() 
{
Integer ii = (Integer)get_Value("WSFECode");
if (ii == null) return 0;
return ii.intValue();
}
}
