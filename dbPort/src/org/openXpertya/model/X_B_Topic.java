/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por B_Topic
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.093 */
public class X_B_Topic extends PO
{
/** Constructor estándar */
public X_B_Topic (Properties ctx, int B_Topic_ID, String trxName)
{
super (ctx, B_Topic_ID, trxName);
/** if (B_Topic_ID == 0)
{
setB_TopicCategory_ID (0);
setB_TopicType_ID (0);
setB_Topic_ID (0);
setDecisionDate (new Timestamp(System.currentTimeMillis()));
setDocumentNo (null);
setIsPublished (false);
setName (null);
setProcessed (false);
setTopicAction (null);
setTopicStatus (null);
}
 */
}
/** Load Constructor */
public X_B_Topic (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=679 */
public static final int Table_ID=679;

/** TableName=B_Topic */
public static final String Table_Name="B_Topic";

protected static KeyNamePair Model = new KeyNamePair(679,"B_Topic");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_B_Topic[").append(getID()).append("]");
return sb.toString();
}
/** Set Topic Category.
Auction Topic Category */
public void setB_TopicCategory_ID (int B_TopicCategory_ID)
{
set_ValueNoCheck ("B_TopicCategory_ID", new Integer(B_TopicCategory_ID));
}
/** Get Topic Category.
Auction Topic Category */
public int getB_TopicCategory_ID() 
{
Integer ii = (Integer)get_Value("B_TopicCategory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Topic Type.
Auction Topic Type */
public void setB_TopicType_ID (int B_TopicType_ID)
{
set_ValueNoCheck ("B_TopicType_ID", new Integer(B_TopicType_ID));
}
/** Get Topic Type.
Auction Topic Type */
public int getB_TopicType_ID() 
{
Integer ii = (Integer)get_Value("B_TopicType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Topic.
Auction Topic */
public void setB_Topic_ID (int B_Topic_ID)
{
set_ValueNoCheck ("B_Topic_ID", new Integer(B_Topic_ID));
}
/** Get Topic.
Auction Topic */
public int getB_Topic_ID() 
{
Integer ii = (Integer)get_Value("B_Topic_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Decision date */
public void setDecisionDate (Timestamp DecisionDate)
{
if (DecisionDate == null) throw new IllegalArgumentException ("DecisionDate is mandatory");
set_Value ("DecisionDate", DecisionDate);
}
/** Get Decision date */
public Timestamp getDecisionDate() 
{
return (Timestamp)get_Value("DecisionDate");
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
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,29);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Published.
The Topic is published and can be viewed */
public void setIsPublished (boolean IsPublished)
{
set_Value ("IsPublished", new Boolean(IsPublished));
}
/** Get Published.
The Topic is published and can be viewed */
public boolean isPublished() 
{
Object oo = get_Value("IsPublished");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Details */
public void setTextDetails (String TextDetails)
{
if (TextDetails != null && TextDetails.length() > 4000)
{
log.warning("Length > 4000 - truncated");
TextDetails = TextDetails.substring(0,3999);
}
set_Value ("TextDetails", TextDetails);
}
/** Get Details */
public String getTextDetails() 
{
return (String)get_Value("TextDetails");
}
/** Set Text Message.
Text Message */
public void setTextMsg (String TextMsg)
{
if (TextMsg != null && TextMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
TextMsg = TextMsg.substring(0,1999);
}
set_Value ("TextMsg", TextMsg);
}
/** Get Text Message.
Text Message */
public String getTextMsg() 
{
return (String)get_Value("TextMsg");
}
/** Set Topic Action */
public void setTopicAction (String TopicAction)
{
if (TopicAction == null) throw new IllegalArgumentException ("TopicAction is mandatory");
if (TopicAction.length() > 2)
{
log.warning("Length > 2 - truncated");
TopicAction = TopicAction.substring(0,1);
}
set_Value ("TopicAction", TopicAction);
}
/** Get Topic Action */
public String getTopicAction() 
{
return (String)get_Value("TopicAction");
}
/** Set Topic Status */
public void setTopicStatus (String TopicStatus)
{
if (TopicStatus == null) throw new IllegalArgumentException ("TopicStatus is mandatory");
if (TopicStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
TopicStatus = TopicStatus.substring(0,1);
}
set_Value ("TopicStatus", TopicStatus);
}
/** Get Topic Status */
public String getTopicStatus() 
{
return (String)get_Value("TopicStatus");
}
}
