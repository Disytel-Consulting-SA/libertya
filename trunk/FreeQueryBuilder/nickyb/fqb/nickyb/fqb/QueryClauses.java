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

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;

public class QueryClauses extends JTabbedPane implements ClauseOwner
{
	private Vector columns = new Vector();

	QueryBuilder builder;

	ClauseCondition where;
	ClauseCondition having;

	ExpressionList expressions;

	ClauseGroup group;
	ClauseOrder order;

	QueryClauses(QueryBuilder builder)
	{
		this.builder = builder;
		this.setBorder(LineBorder.createGrayLineBorder());

		add("where"		, new JScrollPane(where = new ClauseCondition(this)));
		add("group by"	, group = new ClauseGroup(this));
		add("having"	, new JScrollPane(having = new ClauseCondition(this)));
		add("order by"	, order = new ClauseOrder(this));
		add("expression", new JScrollPane(expressions = new ExpressionList(this)));
	}

	public void fireRefreshSyntax()
	{
		builder.syntax.refresh();
	}

	public Object[] getColumns()
	{
		return columns.toArray();
	}

	public Object[] getFunctions()
	{
		return builder.getAllFunctions();
	}

	public JComponent getComponent()
	{
		return this;
	}

	void clean()
	{
		where.clean();
		order.clean();
		having.clean();
		expressions.clean();
	}

	void addColumn(String col)
	{
		columns.add(col);

		where.cbxCols.addItem(col);
		having.cbxCols.addItem(col);
	}

	void removeColumn(String col)
	{
		columns.remove(col);

		where.cbxCols.removeItem(col);
		having.cbxCols.removeItem(col);
	}

	public Dimension getPreferredSize()
	{
		Dimension preferred = super.getPreferredSize();
		preferred.height = 150;

		return preferred;
	}
}
