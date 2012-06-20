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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

public class QueryModel implements Cloneable,Serializable
{
	static final long serialVersionUID = 2825844158455016602L;
	
	private boolean distinct = false;
	
	private ArrayList group = new ArrayList();
	private ArrayList joins = new ArrayList();
	private ArrayList order = new ArrayList();
	private ArrayList where = new ArrayList();
	private ArrayList having = new ArrayList();
	private ArrayList tables = new ArrayList();
	private ArrayList expressions = new ArrayList();
	//begin vpj-cd 05/16/2005 
	private static Hashtable ht = new Hashtable();
	//end vpj-cd 05/16/2005
	
	private QueryModel(){}
	
	public Object[] getOutputColumns()
	{
		return expressions.toArray();
	}
	
	public static QueryModel save(QueryBuilder builder)
	{
		QueryModel model = new QueryModel();
		
		model.distinct = builder.syntax.isDistinct();
		
		for(int i=0; i<builder.listbar.getItemCount(); i++)
		{
			QueryListbar.ExpressionItem item = builder.listbar.getItemAt(i);
			
			if(item instanceof QueryListbar.ColumnItem)
			{
				QueryListbar.ColumnItem citem = (QueryListbar.ColumnItem)item;
				model.expressions.add(citem.querytoken);
			}
			else
			{
				model.expressions.add(new QueryTokens.Expression(item.getValue()));
			}
		}
		
		DesktopEntity[] entities = builder.desktop.getAllEntities();
		for(int i=0; i<entities.length; i++)
		{
			model.tables.add(entities[i].querytoken);
		}
		
		//joins...
		for(int i=0; i<builder.desktop.joins.size(); i++)
		{
			DesktopRelation relation = (DesktopRelation)builder.desktop.joins.elementAt(i);
			
			QueryTokens.Table ltable = new QueryTokens.Table(relation.leftEntity.querytoken.getSchema(),relation.leftEntity.querytoken.getName());
			if(!relation.leftEntity.querytoken.getName().equals(relation.leftEntity.querytoken.getAlias())) ltable.setAlias(relation.leftEntity.querytoken.getAlias());
			QueryTokens.Table rtable = new QueryTokens.Table(relation.rightEntity.querytoken.getSchema(),relation.rightEntity.querytoken.getName());
			
			if(!relation.rightEntity.querytoken.getName().equals(relation.rightEntity.querytoken.getAlias())) rtable.setAlias(relation.rightEntity.querytoken.getAlias());			
			
			//begin vpj-cd e-evolution 05/16/2005
			//QueryTokens.Column lcolumn = new QueryTokens.Column(ltable,relation.leftField.getText());			
			QueryTokens.Column lcolumn = new QueryTokens.Column(ltable,relation.leftField.querytoken.getName(),relation.leftField.querytoken.getDesc());
			//QueryTokens.Column rcolumn = new QueryTokens.Column(rtable,relation.rightField.getText());
			QueryTokens.Column rcolumn = new QueryTokens.Column(rtable,relation.rightField.querytoken.getName(),relation.rightField.querytoken.getDesc());
			ht.put(relation.leftField.querytoken.getDesc(),relation.leftField.querytoken.getDesc());			
			if (ht.get(relation.rightField.querytoken.getDesc())==null)
			ht.put(relation.rightField.querytoken.getDesc(),relation.rightField.querytoken.getDesc());
			else
			ht.put(relation.rightField.querytoken.getDesc()+2,relation.rightField.querytoken.getDesc());
			//end vpj-cd e-evolution 05/16/20005
			
			model.joins.add(new QueryTokens.Join(relation.getType(),lcolumn,relation.getText(),rcolumn));
		}
		
		String append = null;
		TableModel wheremodel = builder.clauses.where.getModel();
		for(int i=0; i<wheremodel.getRowCount()-1; i++)
		{
			Object[] rowdata = new Object[4];
			for(int j=0; j<4;j++) rowdata[j] = wheremodel.getValueAt(i,j+1);
			
			model.where.add(new QueryTokens.Condition(append,
														(rowdata[0]!=null ? rowdata[0].toString() : null),
														(rowdata[1]!=null ? rowdata[1].toString() : null),
														(rowdata[2]!=null ? rowdata[2].toString() : null)));
			
			append = (rowdata[3]!=null ? rowdata[3].toString() : null);
		}
		
		ListModel groupmodel = builder.clauses.group.selectedColumns.getModel();
		for(int i=0; i<groupmodel.getSize(); i++)
		{
			model.group.add(new QueryTokens.Expression(groupmodel.getElementAt(i).toString()));
		}

		append = null;
		TableModel havingmodel = builder.clauses.having.getModel();
		for(int i=0; i<havingmodel.getRowCount()-1; i++)
		{
			Object[] rowdata = new Object[4];
			for(int j=0; j<4;j++) rowdata[j] = havingmodel.getValueAt(i,j+1);
			
			model.having.add(new QueryTokens.Condition(append,
														(rowdata[0]!=null ? rowdata[0].toString() : null),
														(rowdata[1]!=null ? rowdata[1].toString() : null),
														(rowdata[2]!=null ? rowdata[2].toString() : null)));
			
			append = (rowdata[3]!=null ? rowdata[3].toString() : null);
		}
		
		TableModel ordermodel = builder.clauses.order.selectedExpressions.getModel();
		for(int i=0; i<ordermodel.getRowCount(); i++)
		{
			Boolean type = (Boolean)ordermodel.getValueAt(i,0);
			String value = ordermodel.getValueAt(i,1).toString();
			
			model.order.add(new QueryTokens.Order(value, type.booleanValue()));
		}
		
		return model;
	}
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void load(QueryBuilder builder)
	{
		builder.clean();
		builder.syntax.setDistinct(this.distinct);
	
		for(ListIterator iter = tables.listIterator(); iter.hasNext();)
		{
			QueryTokens.Table table = (QueryTokens.Table)iter.next();
			builder.add(table,false);
		}
		
		//joins...
		for(ListIterator iter = joins.listIterator(); iter.hasNext();)
		{
			QueryTokens.Join token = (QueryTokens.Join)iter.next();
			
			DesktopEntity lentity = builder.desktop.find(token.getLeftColumn().getTable());
			DesktopEntity rentity = builder.desktop.find(token.getRightColumn().getTable());
			
			if(lentity!=null && rentity!=null)
			{
				DesktopRelation relation = new DesktopRelation(builder.desktop);
				
				relation.leftEntity = lentity;
				relation.leftField = lentity.getField(token.getLeftColumn().getName());
				
				relation.setType(token.getType());
				
				relation.rightEntity = rentity;
				relation.rightField = rentity.getField(token.getRightColumn().getName());
				
				builder.desktop.addRelation(relation);
			}
		}

		for(ListIterator iter = expressions.listIterator(); iter.hasNext();)
		{
			QueryTokens.Expression token = (QueryTokens.Expression)iter.next();
			if(token instanceof QueryTokens.Column)
			{
				QueryTokens.Column ctoken = (QueryTokens.Column)token;
				
				DesktopEntity entity = builder.desktop.find(ctoken.getTable());
				if(entity!=null)
					entity.setSelectionStatus(ctoken.getName(),true);
			}
			else 
			{
				builder.clauses.expressions.add(token.toString());
			}
		}

		for(ListIterator iter = where.listIterator(); iter.hasNext();)
		{
			QueryTokens.Condition token = (QueryTokens.Condition)iter.next();
			
			String[] row = new String[4];
			row[0] = token.getLeft();
			row[1] = token.getOperator();
			row[2] = token.getRight();
			row[3] = token.getAppend();
			
			builder.clauses.where.addRow(row);
		}
	
		for(ListIterator iter = group.listIterator(); iter.hasNext();)
		{
			builder.clauses.group.setAsSelected(iter.next().toString());
		}
		
		for(ListIterator iter = order.listIterator(); iter.hasNext();)
		{
			QueryTokens.Order token = (QueryTokens.Order)iter.next();
			builder.clauses.order.setAsSelected(token.getValue(),token.isAscending());
		}
	
		builder.syntax.refresh();
	}
	
	public String getSyntax()
	{
		return getSyntax(true);
	}
	
	public String getSyntax(boolean wrapped)
	{
		StringBuffer syntax = new StringBuffer();
		
		syntax.append("SELECT" + (distinct ? " DISTINCT " : " "));
		syntax.append(concat(expressions,',',wrapped));
		syntax.append("FROM " 	+ concatFrom(wrapped));
		
		if(!where.isEmpty())
			syntax.append("WHERE " + concat(where,' ',wrapped));
		
		if(!group.isEmpty())
			syntax.append("GROUP BY " + concat(group,',',wrapped));
		
		if(!having.isEmpty())
			syntax.append("HAVING " + concat(having,' ',wrapped));
		
		if(!order.isEmpty())
			syntax.append("ORDER BY " + concat(order,',',wrapped));
		
		return syntax.toString();
	}
	
	private String concatFrom(boolean wrap)
	{
		StringBuffer buffer = new StringBuffer();
		
		ArrayList joined = new ArrayList();
		Hashtable notJoined = new Hashtable();
		for(int i=0; i<tables.size(); i++)
			notJoined.put(tables.get(i).toString(), tables.get(i));
		
		for(ListIterator iter = joins.listIterator(); iter.hasNext(); buffer.append((wrap ? "\n":" ")))
		{
			QueryTokens.Join join = (QueryTokens.Join)iter.next();
			
			boolean withLeft = false;
			if(withLeft = notJoined.containsKey(join.getLeftColumn().getTable().toString()))
			{
				notJoined.remove(join.getLeftColumn().getTable().toString());
				joined.add(join.getLeftColumn().getTable());
			}
			
			boolean withRight = false;
			if(withRight = notJoined.containsKey(join.getRightColumn().getTable().toString()))
			{
				notJoined.remove(join.getRightColumn().getTable().toString());
				joined.add(join.getRightColumn().getTable());
			}
			
			if(withLeft && withRight)
			{
				if(buffer.length() > 0) buffer.insert(buffer.length()-1,",");
				buffer.append(join.toString());
			}
			else if(!withLeft && withRight)
				buffer.append(join.getWithoutLeft());
			else if(withLeft && !withRight)
				buffer.append(join.getWithoutRight());
			else
				buffer.append(join.getOnlyCondition());
		}
		
		if(!notJoined.isEmpty())
		{
			StringBuffer not = new StringBuffer();
			not.append(concat(new ArrayList(notJoined.values()),',',wrap));
			
			if(!joined.isEmpty())
				not.insert(not.length()-1,',');
			
			buffer.insert(0,not);
		}
		
		return buffer.toString();
	}
	
	private String concat(ArrayList list, char delimiter, boolean wrap)
	{
		StringBuffer buffer = new StringBuffer();
		String wraper = String.valueOf(delimiter);
		if(wrap) wraper = wraper + "\n\t";
		
		for(ListIterator iter = list.listIterator(); iter.hasNext(); buffer.append(wraper))
			buffer.append(iter.next().toString());
		
		if(list.isEmpty())
		    buffer.append("<empty>");
		else
		    for(int i=0; i<wraper.length(); i++)
		        buffer.deleteCharAt(buffer.length()-1);
		
		if(wrap)
		    buffer.append('\n');
		else
		    buffer.append(' ');
		
		return buffer.toString();
	}
	
	public String toString()
	{
		return getSyntax(false);
	}
}
