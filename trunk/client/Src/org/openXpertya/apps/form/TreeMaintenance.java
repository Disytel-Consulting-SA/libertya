/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.openXpertya.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.apps.form.VTreeMaintenance.ListItem;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MTree;
import org.openXpertya.model.MTree_Node;
import org.openXpertya.model.MTree_NodeBP;
import org.openXpertya.model.MTree_NodeMM;
import org.openXpertya.model.MTree_NodePR;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;

public class TreeMaintenance {

	/**	Window No				*/
	public int         	m_WindowNo = 0;
	/**	Active Tree				*/
	public MTree		 	m_tree;
	/**	Logger			*/
	public static CLogger log = CLogger.getCLogger(TreeMaintenance.class);
	
	public KeyNamePair[] getTreeData()
	{
		return DB.getKeyNamePairs(MRole.getDefault().addAccessSQL(
				"SELECT AD_Tree_ID, Name FROM AD_Tree WHERE TreeType NOT IN ('BB','PC') ORDER BY 2", 
				"AD_Tree", MRole.SQL_NOTQUALIFIED, MRole.SQL_RW), false);
	}
	
	public ArrayList<ListItem> getTreeItemData()
	{
		ArrayList<ListItem> data = new ArrayList<ListItem>();
		
		String fromClause = m_tree.getSourceTableName(false);	//	fully qualified
		String columnNameX = m_tree.getSourceTableName(true);
		String actionColor = m_tree.getActionColorName();
		
        String hasTrlSql = "SELECT IsTranslated FROM AD_Column INNER JOIN AD_Table ON (AD_Table.AD_Table_ID=AD_Column.AD_Table_ID) WHERE TableName = ? AND ColumnName = ? ";
        boolean hasNameTrl = "Y".equals(DB.getSQLObject(null, hasTrlSql, new Object[]{columnNameX, "Name"}));
        boolean hasDescriptionTrl = "Y".equals(DB.getSQLObject(null, hasTrlSql, new Object[]{columnNameX, "Description"}));
        String sql;
		
        if (hasNameTrl || hasDescriptionTrl) {
        	sql = "SELECT t." + columnNameX + "_ID," + (hasNameTrl ? "COALESCE(trl.Name,t.Name)" : "t.Name") + "," + (hasDescriptionTrl ? "COALESCE(trl.Description,t.Description)" : "t.Description") + ",t.IsSummary," + actionColor + ",t.Name,t.Description " + 
        		" FROM " + fromClause +
        		" LEFT JOIN " + columnNameX + "_trl trl ON (t." + columnNameX + "_ID = trl." + columnNameX + "_ID AND trl.AD_Language = ?) " +
        		" ORDER BY 2";
        } else {
        	sql = "SELECT t." + columnNameX + "_ID,t.Name,t.Description,t.IsSummary," + actionColor + " FROM " + fromClause + " ORDER BY 2";
        }

		sql = MRole.getDefault().addAccessSQL(sql, 
			"t", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
		log.config(sql);
		//	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			
            if (hasNameTrl || hasDescriptionTrl)
            	pstmt.setString(1, Env.getAD_Language(Env.getCtx()));
            
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
            	ListItem item ;
            	if (hasNameTrl || hasDescriptionTrl)
            		item = new ListItem( rs.getInt( 1 ),rs.getString( 2 ),rs.getString( 6 ),rs.getString( 3 ),rs.getString( 7 ),"Y".equals( rs.getString( 4 )),rs.getString( 5 ));
            	else
            		item = new ListItem( rs.getInt( 1 ),rs.getString( 2 ),rs.getString( 3 ),"Y".equals( rs.getString( 4 )),rs.getString( 5 ));
				data.add(item);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		return data;
	}
	
	/**
	 * 	Action: Add Node to Tree
	 * 	@param item item
	 */
	public void addNode(ListItem item)
	{
		if (item != null)
		{
			//	May cause Error if in tree
			if (m_tree.isProduct())
			{
				MTree_NodePR node = new MTree_NodePR (m_tree, item.id);
				node.save();
			}
			else if (m_tree.isBPartner())
			{
				MTree_NodeBP node = new MTree_NodeBP (m_tree, item.id);
				node.save();
			}
			else if (m_tree.isMenu())
			{
				MTree_NodeMM node = new MTree_NodeMM (m_tree, item.id);
				node.save();
			}
			else
			{
				MTree_Node node = new MTree_Node (m_tree, item.id);
				node.save();
			}
		}
	}	//	action_treeAdd
	
	/**
	 * 	Action: Delete Node from Tree
	 * 	@param item item
	 */
	public void deleteNode(ListItem item)
	{
		if (item != null)
		{
			if (m_tree.isProduct())
			{
				MTree_NodePR node = MTree_NodePR.get (m_tree, item.id);
				if (node != null)
					node.delete(true);
			}
			else if (m_tree.isBPartner())
			{
				MTree_NodeBP node = MTree_NodeBP.get (m_tree, item.id);
				if (node != null)
					node.delete(true);
			}
			else if (m_tree.isMenu())
			{
				MTree_NodeMM node = MTree_NodeMM.get (m_tree, item.id);
				if (node != null)
					node.delete(true);
			}
			else
			{
				MTree_Node node = MTree_Node.get (m_tree, item.id);
				if (node != null)
					node.delete(true);
			}
		}
	}	//	action_treeDelete
	
	/**************************************************************************
	 * 	Tree Maintenance List Item
	 */
	public class ListItem
	{
		/**
		 * 	ListItem
		 *	@param ID
		 *	@param Name
		 *	@param Description
		 *	@param summary
		 *	@param ImageIndicator
		 */
		public ListItem (int ID, String Name, String Description, 
			boolean summary, String ImageIndicator)
		{
			id = ID;
			name = Name;
			description = Description;
			isSummary = summary;
			imageIndicator = ImageIndicator;
		}	//	ListItem
		
        public ListItem( int id,String name, String defName,String description, String defDescription,boolean isSummary,String imageIndicator ) {
            this.id             = id;
            this.name           = name;
            this.description    = description;
            this.isSummary      = isSummary;
            this.imageIndicator = imageIndicator;
            this.defname        = defName;
            this.defdescription = defDescription;
        }    // ListItem
		
		/**	ID			*/
		public int id;
		/** Name		*/
		public String name;
		/** Description	*/
		public String description;
		/** Summary		*/
		public boolean isSummary;
		/** Indicator	*/
		public String imageIndicator;  //  Menu - Action
        /** Descripción de Campos */
        public String defname;
        /** Descripción de Campos */
        public String defdescription;
		/**
		 * 	To String
		 *	@return	String Representation
		 */
		public String toString ()
		{
			String retValue = name;
			if (description != null && description.length() > 0)
				retValue += " (" + description + ")";
			return retValue;
		}	//	toString
		
	}	//	ListItem
}
