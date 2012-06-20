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

public abstract class QueryTokens
{
	private static String toQuote(String identifier)
	{
		if(QueryBuilder.identifierQuoteString.equals(" "))
			return identifier;
		
		StringBuffer quoted = new StringBuffer(identifier);
		
		quoted.insert(0,QueryBuilder.identifierQuoteString);
		quoted.append(QueryBuilder.identifierQuoteString);
		
		if(identifier.indexOf('.')!=-1)
		{
			int point = identifier.indexOf('.');
			quoted.insert(point+1,QueryBuilder.identifierQuoteString);
			quoted.insert(point+3,QueryBuilder.identifierQuoteString);
		}
		
		return quoted.toString();
	}
	
	public static class Expression implements Serializable
	{
		private String value;
		
		public Expression(String value)
		{
			this.value = value;

		}
		
		public String toString()
		{
			return value;
		}
	}
	
	public static class Column extends Expression
	{
	    static final long serialVersionUID = -1556500802815411115L;
	    //begin vpj-cd e-evolution 05/16/2005
		private String desc;
		//end vpj-cd e-evolution 05/16/2005
	    
		private Table owner;
		
		//begin vpj-cd e-evolution 05/16/2005
		//public Column(Table owner, String value)
		public Column(Table owner, String value, String desc)
		//end vpj-cd e-evolution 05/16/2005
		{
			super(value);
			// begin vpj-cd e-evolution 05/16/2005
			this.desc = desc;
			// end vpj-cd e-evolution 05/16/2005
			this.owner = owner;
		}

		public String getAlias()
		{
		    String alias = owner.getAlias() + "." + this.getName();
		    
		    alias = alias.replace(' ','_');
		    alias = alias.replace('.','_');
			
			if(alias.length() > QueryBuilder.maxColumnNameLength
			&& QueryBuilder.maxColumnNameLength>0)
			    alias = alias.substring(0,QueryBuilder.maxColumnNameLength);
		    
			return alias;
		}
		
		public Table getTable()
		{
			return owner;
		}
		
		public String getName()
		{
			return super.toString();
		}
		
		//begin vpj-cd e-evolution 05/16/2005
		public String getDesc()
		{
			return desc;
		}
		//end vpj-cd e-evolution 05/16/2005

		public String toString(boolean showquote, boolean showalias)
		{
			String toReturn = owner.getAlias() + "." + this.getName();

			if(showquote) toReturn = toQuote(toReturn);
			if(showalias) toReturn = toReturn + " AS " + this.getAlias();
			
			//begin vpj-cd 05/16/2005
			String identifier = owner.name + "." + this.getName();
			String ownername = owner.name;
			String ownercol = this.getName();
			String temp = "";
			String temp2 = "";
			int cont=2;
			if(showquote) toReturn = toQuote(identifier);
			if(showalias)
			{				
				ownername = owner.name.replace('"',' ');
				ownername=ownername.trim();
				ownercol = ownercol.replace('"',' ');
				ownercol = ownercol.trim();
				identifier = ownername + "_" + ownercol;
				if (identifier.length()>30)
					identifier = ownercol;
				toReturn = toReturn +" AS " +ownercol;
				
			}
			//end vpj-cd 05/16/2005
			
			return toReturn;
		}
		
		public String toString()
		{
			return toString(QueryBuilder.useIdentifierQuote,QueryBuilder.useColumnAutoAlias);
		}
	}
	
	public static class Table implements Serializable
	{
		static final long serialVersionUID = 5445275221474527245L;
		
		private String alias;
		
		private String name;
		private String schema;
		
		public Table(String schema, String name)
		{
			this.name = name;
			this.schema = schema;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getSchema()
		{
			return schema;
		}

		public String getAlias()
		{
			return alias!=null ? alias : name.replace(' ','_');
		}
		
		public void setAlias(String alias)
		{
			this.alias = alias.replace(' ','_');
		}
		
		public String toString(boolean showquote, boolean showalias)
		{
			String identifier = schema == null ? name : schema + "." + name;
			
			if(showquote)
			    identifier = toQuote(identifier);
			
			if(showalias && this.getAlias()!=null && !this.getAlias().equals(""))
			    identifier = identifier + " " + getAlias();
			
			return identifier;			
		}
		
		public String toString()
		{
			return toString(QueryBuilder.useIdentifierQuote,true);
		}
	}
	
	public static class Join implements Serializable
	{
		public static final int INNER_JOIN		= 0;
		public static final int LEFT_OUTER_JOIN	= 1;
		public static final int RIGHT_OUTER_JOIN= 2;
		public static final int FULL_OUTER_JOIN	= 3;
		
		private int type;
		
		private Column left;
		private Column right;
		
		private String operator;		
		
		public Join(Column left, String operator, Column right)
		{
			this(INNER_JOIN,left,operator,right);
		}
		
		public Join(int type, Column left, String operator, Column right)
		{
			this.type = type;
			this.left = left;
			this.right = right;
			this.operator = operator;
		}
		
		public int getType()
		{
			return type;
		}
		
		public Column getLeftColumn()
		{
			return left;
		}
		
		public Column getRightColumn()
		{
			return right;
		}
		
		public String getJoinDescription()
		{
			switch(type)
			{
			case LEFT_OUTER_JOIN	: return " LEFT OUTER JOIN ";
			case RIGHT_OUTER_JOIN	: return  " RIGHT OUTER JOIN ";
			case FULL_OUTER_JOIN	: return  " FULL OUTER JOIN ";
			default					: return " INNER JOIN ";
			}
		}
		
		private String getCondition()
		{
			return left.toString(QueryBuilder.useIdentifierQuote,false) + " " + operator + " " + right.toString(QueryBuilder.useIdentifierQuote,false);
		}
		
		public String getOnlyCondition()
		{
			return " AND " + this.getCondition();
		}

		public String getWithoutRight()
		{
			String joinDesc;
			switch(type)
			{
			case RIGHT_OUTER_JOIN: joinDesc = " LEFT OUTER JOIN "; break;
			case LEFT_OUTER_JOIN: joinDesc = " RIGHT OUTER JOIN "; break;
			case FULL_OUTER_JOIN: joinDesc = " FULL OUTER JOIN "; break;
			default : joinDesc = " INNER JOIN ";
			}
		
			return joinDesc + left.owner.toString() + " ON " + this.getCondition();
		}
		
		public String getWithoutLeft()
		{
			String joinDesc;
			switch(type)
			{
			case LEFT_OUTER_JOIN: joinDesc = " LEFT OUTER JOIN "; break;
			case RIGHT_OUTER_JOIN: joinDesc = " RIGHT OUTER JOIN "; break;
			case FULL_OUTER_JOIN: joinDesc = " FULL OUTER JOIN "; break;
			default : joinDesc = " INNER JOIN ";
			}
		
			return joinDesc + right.owner.toString() + " ON " + this.getCondition();
		}
		
		public String toString()
		{
			return left.owner.toString() + getWithoutLeft();
		}
	}
	
	public static class Condition implements Serializable
	{
		private String left;
		private String right;
		
		private String append;
		private String operator;
		
		public Condition(String left, String operator, String right)
		{
			this(null,left, operator, right);
		}
		
		public Condition(String append, String left, String operator, String right)
		{
			this.left = left;
			this.right = right;
			this.append = append;
			this.operator = operator;
		}
		
		public String getAppend()
		{
			return append;
		}
		
		public String getLeft()
		{
			return left;
		}
		
		public String getRight()
		{
			return right;
		}
		
		public String getOperator()
		{
			return operator;
		}
		
		public String toString()
		{
			return (append!=null ? append + " " : "") + left + " " + operator + " " + right;
		}
	}
	
	public static class Order extends Expression
	{
		public static final int ASCENDING = 0;
		public static final int DESCENDING = 1;
		
		private int type;
		
		public Order(String value)
		{
			this(value,ASCENDING);
		}
		
		public Order(String value, int type)
		{
			super(value);
			this.type = type;
		}
		
		public Order(String exp, boolean ascending)
		{
			this(exp, ascending ? ASCENDING : DESCENDING);
		}
		
		public boolean isAscending()
		{
			return type == ASCENDING;
		}
		
		public String getValue()
		{
			return super.toString();
		}
		
		public String toString()
		{
			return this.getValue() + (isAscending() ? " ASC" : " DESC");
		}
	}
}
