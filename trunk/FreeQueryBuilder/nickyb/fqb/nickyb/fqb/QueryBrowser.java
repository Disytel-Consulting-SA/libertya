/*
 * Copyright (C) 2004 Nicky BRAMANTE
 *
 * This file is part of FreeQueryBuilder
 *
 * FreeQueryBuilder is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Send questions or suggestions to nickyb@interfree.it
 */

package nickyb.fqb;

import nickyb.fqb.util.*;

//begin vpj-cd e-evolution 05/16/2005
import org.openXpertya.model.*;
import org.openXpertya.util.*;
import org.openXpertya.grid.tree.*;
//end vpj-cd e-evolutionn 05/16/2005

import java.awt.Cursor;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.JTree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public class QueryBrowser extends DefaultPanel
{
	public final static int DEFAULT_WiDTH = 250;

	private QueryBuilder builder;

	private JToolBar header;
	private JTree tree;
	private String tablename = null;
	private String table = null;


	QueryBrowser(QueryBuilder builder)
	{
		super(2,2);
		this.builder = builder;

		createTree();

		header = new JToolBar();
		header.add(new JLabel("Explorador"));
		header.add(Box.createHorizontalGlue());
		header.add(new JButton(new ActionRefresh()));
		setCenterComponent(new DefaultScrollPane(header,tree,false));
	}

	private void createTree()
	{
		tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("<sin conexión>")));
		tree.putClientProperty("JTree.lineStyle", "Angled");

		TreeClickHandler click = new TreeClickHandler();
		tree.addMouseListener(click);
		tree.addTreeWillExpandListener(click);

		((DefaultTreeCellRenderer)tree.getCellRenderer()).setFont(UIUtilities.fontPLAIN);
		tree.setRowHeight(18);
	}

	public void addTreeSelectionListener(TreeSelectionListener l)
	{
		tree.addTreeSelectionListener(l);
	}

	public void addAfter(Action a, boolean withSeparator)
	{
		if(withSeparator) header.addSeparator();
		JButton btn = header.add(a);

		btn.setBorder(UIUtilities.NO_BORDER);
		btn.setOpaque(false);
	}

	public void addBefore(Action a, boolean withSeparator)
	{
		JButton btn = new JButton(a);
		header.add(btn,2);

		if(withSeparator) header.add(new JToolBar.Separator(),3);

		btn.setBorder(UIUtilities.NO_BORDER);
		btn.setOpaque(false);
	}

	void connectionChanged() throws SQLException
	{
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("<sin conexión>");
		DefaultTreeModel model = new DefaultTreeModel(root);

		if(builder.getConnection()!=null)
		{
			root = new DefaultMutableTreeNode(builder.getConnection().getCatalog());
			model = new DefaultTreeModel(root, true);

			if(builder.jdbcUseSchema())
			{
				DatabaseMetaData dbmd = builder.getConnection().getMetaData();
				ResultSet rsSchemas = dbmd.getSchemas();
				//begin vpj-cd e-evolution 05/16/2005
				//while(rsSchemas.next())
				//	root.add(new DefaultMutableTreeNode(rsSchemas.getString(1), true));
				while(rsSchemas.next())
				{
					if (rsSchemas.getString(1).equals("Openxp"))
					root.add(new DefaultMutableTreeNode(rsSchemas.getString(1), true));
				}
				//end vpj-cd e-evolution 05/16/2005
				rsSchemas.close();
			}
			else
			{
				loadTables(root);
			}
		}
		//root.add(new DefaultMutableTreeNode("openxp", true));
		//end vpj-cd e-evolution 05/16/2005
		tree.setRootVisible(root.toString()!=null);
		tree.setShowsRootHandles(!tree.isRootVisible());
		tree.setModel(model);

		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	private void loadTables(DefaultMutableTreeNode parent)
	{
		if(!parent.isRoot() && (parent.getChildCount() > 0 || parent.toString().endsWith("(0)"))) return; //hasExpanded

		//begin vpj-cd e-evolution 05/16/2005
		ArrayList tables =new ArrayList();
		//end vpj-cd e-evolution 05/16/2005
		try
		{
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			DatabaseMetaData dbmd = builder.getConnection().getMetaData();

			String schema = parent.isRoot() ? null : parent.toString();
			String catalog = schema == null ? null : dbmd.getConnection().getCatalog();

			ResultSet rsTables = dbmd.getTables(catalog, schema, "%", new String[]{"Tabla","Vista"});

			if(rsTables!=null)
			{
				//begin vpj-cd e-evolution 05/16/2005
				//while(rsTables.next())
				//	parent.add(new DefaultMutableTreeNode(rsTables.getString(3),false));
				int role=0;
				role = Env.getAD_Role_ID(Env.getCtx());
				int numtables = 0 ;
				while(rsTables.next())
				{
					String sql = "SELECT AD_Table.TableName, AD_Table.Name FROM AD_Table INNER JOIN AD_WINDOW ON AD_TABLE.AD_WINDOW_ID = AD_WINDOW.AD_WINDOW_ID INNER JOIN AD_WINDOW_ACCESS ON AD_WINDOW.AD_WINDOW_ID = AD_WINDOW_ACCESS.AD_WINDOW_ID INNER JOIN AD_ROLE ON AD_WINDOW_ACCESS.AD_ROLE_ID = AD_ROLE.AD_ROLE_ID WHERE UPPER(TableName)=UPPER('" +rsTables.getString(3) +"') and AD_Role.AD_Role_ID=" +role;
					PreparedStatement pstmt = null;
					pstmt = DB.prepareStatement (sql);

					ResultSet rs = pstmt.executeQuery ();
					if (rs.next ())
					{
						tablename = rs.getString(1);
						  	  table = rs.getString(2);
					}
					else
					{
					      table = null;
					}
					rs.close ();
					pstmt.close ();
					pstmt = null;
					//M_Table t = M_Table.get(Env.getCtx(),rsTables.getString(3).toUpperCase());
					//tablename = t.get_TableName();
					//table = t.getName();
					//System.out.println("tablename " + tablename +" schema*****" + rsTables.getString(3) + "*******");
					//M_Table table = M_Table.get(Env.getCtx(),tablename);

					if (table!=null && tablename!=null)
					{
						//System.out.println("traducido" +Msg.translate(Env.getCtx(),table.getName()));
						//System.out.println("pba error" +nameC +"   " +cont);
						//pba.add(cont,nameC); //sin traducir
						tables.add(numtables,Msg.translate(Env.getCtx(),table));
						//parent.add(new DefaultMutableTreeNode(nameC,false));
						numtables++;
					}
				}

				//end vpj-cd e-evolution 05/16/2005
				rsTables.close();
			}

			//begin vpj-cd e-evolution 05/16/2005
			Object [] sortcols = null;
			sortcols = tables.toArray();
			//String[] predators = (String[])pba.toArray( new String[ pba.size() ] );

			Arrays.sort((String[])sortcols,String.CASE_INSENSITIVE_ORDER);
			//System.out.println("pba2 array " +sortcols.toString());
			for (int i=0; i<sortcols.length;i++)
			{
				parent.add(new DefaultMutableTreeNode(sortcols[i].toString(),false));
			}
			//end vpj-cd e-evolution 05/16/2005

			if(!parent.isRoot())
				parent.setUserObject(parent.toString() + " (" + parent.getChildCount() + ")");
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		finally
		{
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private class TreeClickHandler extends MouseAdapter implements TreeWillExpandListener
	{
		private boolean isTable(TreePath path)
		{
			if(path==null) return false;

			try
			{
				if(QueryBrowser.this.builder.jdbcUseSchema())
					return path.getPathCount() == 3;
				else
					return path.getPathCount() == 2;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}

			return false;
		}

		public void mousePressed(MouseEvent e)
		{
			if(e.getClickCount()!=2) return;

			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if(this.isTable(path))
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

				String schema = null;
				if(path.getPathCount() == 3)
				{
					schema = path.getParentPath().getLastPathComponent().toString();
					schema = schema.substring(0,schema.indexOf("(")).trim();
				}

				// begin vpj-cd  e-evolution 05/16/2005
				//System.out.println("nombre de tabla? " +node.toString());
				//String nameEN="";
				String name="";
				tablename = "";

				Language lang = Env.getLoginLanguage(Env.getCtx());

				System.out.println("Idioma:" + lang.getName());
				try
				{
					if (lang.getName()!="English")
					{
					String sql ="SELECT t.Name FROM AD_Table t INNER JOIN AD_Table_Trl tt ON(t.AD_Table_ID=tt.AD_Table_ID AND tt.Name='" +node.toString() +"')";
					PreparedStatement pstmt = null;

						pstmt = DB.prepareStatement (sql);
						ResultSet rs = pstmt.executeQuery ();
						if (rs.next ())
							name = rs.getString(1);
						rs.close ();
						pstmt.close ();
						pstmt = null;

					}
					else
					{
						name =node.toString();
					}

					String sql = "SELECT TableName FROM AD_Table WHERE UPPER(Name)=UPPER('" +name +"')";
					System.out.println("Nombre:"+ name);
					System.out.println("SQL:"+ sql);
					PreparedStatement pstmt = null;
					pstmt = DB.prepareStatement (sql);

					ResultSet rs = pstmt.executeQuery ();
					while(rs.next ())
					tablename = rs.getString(1);
					rs.close ();
					pstmt.close ();
					pstmt = null;
				}
				catch(SQLException esql)
				{
				}
				System.out.println("TableName:"+ tablename);
				builder.add(new QueryTokens.Table(schema,tablename.toUpperCase()),true);
				//builder.add(new QueryTokens.Table(schema,node.toString()),true);
                //end vpj-cd 05/16/2005
			}
		}

		public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException
		{
		}

		public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException
		{
			if(event.getPath().getPathCount() == 2)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
				loadTables(node);
			}
		}
	}

	private class ActionRefresh extends AbstractAction
	{
		ActionRefresh()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("builder.refresh"));
			this.putValue(SHORT_DESCRIPTION, "actualizar");
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				QueryModel temp = QueryBrowser.this.builder.getModelClone();

				QueryBrowser.this.builder.clean();
				QueryBrowser.this.connectionChanged();

				if(temp == null) return;
				temp.load(QueryBrowser.this.builder);
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
