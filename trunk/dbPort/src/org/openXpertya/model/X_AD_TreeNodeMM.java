/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_TreeNodeMM
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-12-07 15:45:37.232 */
public class X_AD_TreeNodeMM extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_TreeNodeMM (Properties ctx, int AD_TreeNodeMM_ID, String trxName)
{
super (ctx, AD_TreeNodeMM_ID, trxName);
/** if (AD_TreeNodeMM_ID == 0)
{
setAD_Tree_ID (0);
setNode_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_TreeNodeMM (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_TreeNodeMM");

/** TableName=AD_TreeNodeMM */
public static final String Table_Name="AD_TreeNodeMM";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_TreeNodeMM");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_TreeNodeMM[").append(getID()).append("]");
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
/** Set Tree.
Identifies a Tree */
public void setAD_Tree_ID (int AD_Tree_ID)
{
set_ValueNoCheck ("AD_Tree_ID", new Integer(AD_Tree_ID));
}
/** Get Tree.
Identifies a Tree */
public int getAD_Tree_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int NODE_ID_AD_Reference_ID = MReference.getReferenceID("AD_Menu");
/** Set Node_ID */
public void setNode_ID (int Node_ID)
{
set_ValueNoCheck ("Node_ID", new Integer(Node_ID));
}
/** Get Node_ID */
public int getNode_ID() 
{
Integer ii = (Integer)get_Value("Node_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getNode_ID()));
}
public static final int PARENT_ID_AD_Reference_ID = MReference.getReferenceID("AD_Menu");
/** Set Parent.
Parent of Entity */
public void setParent_ID (int Parent_ID)
{
if (Parent_ID <= 0) set_Value ("Parent_ID", null);
 else 
set_Value ("Parent_ID", new Integer(Parent_ID));
}
/** Get Parent.
Parent of Entity */
public int getParent_ID() 
{
Integer ii = (Integer)get_Value("Parent_ID");
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
