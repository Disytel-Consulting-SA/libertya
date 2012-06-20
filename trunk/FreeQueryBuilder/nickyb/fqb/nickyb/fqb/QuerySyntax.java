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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class QuerySyntax extends DefaultPanel
{
	QueryModel querymodel;
	private QueryBuilder builder;

	private JTextArea text;
	private JToggleButton distinct;

	QuerySyntax(QueryBuilder builder)
	{
		this.builder = builder;
		this.text = new JTextArea();

		text.setEditable(false);
		text.setOpaque(false);
		text.setTabSize(4);

		JToolBar header = new JToolBar();
		header.add(new JLabel("sintaxis"));
		header.add(Box.createHorizontalGlue());
		header.add(distinct = new JToggleButton(new ActionDistinct()));
		header.add(new ActionCopySyntax());

		setCenterComponent(new DefaultScrollPane(header,text,false));
	}

	void clean()
	{
		querymodel = null;
	}

	public boolean isDistinct()
	{
		return distinct.isSelected();
	}

	public void setDistinct(boolean b)
	{
		distinct.setSelected(b);
	}

	public void setText(String s)
	{
		text.setText(s);
	}

	public String getText()
	{
		return text.getText();
	}

	void refresh()
	{
		if(builder.desktop.getEntityCount() == 0)
		{
			builder.syntax.setText(null);
			return;
		}

		querymodel = QueryModel.save(builder);
		builder.syntax.setText(querymodel.getSyntax());
	}

	private class ActionDistinct extends AbstractAction
	{
		ActionDistinct()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("builder.distinct"));
			this.putValue(SHORT_DESCRIPTION, "seleccionar distintivo");
		}

		public void actionPerformed(ActionEvent e)
		{
			QuerySyntax.this.refresh();
		}
	}

	private class ActionCopySyntax extends AbstractAction
	{
		ActionCopySyntax()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("clipboard.copy"));
			this.putValue(SHORT_DESCRIPTION, "copiar sintaxis");
		}

		public void actionPerformed(ActionEvent e)
		{
			QuerySyntax.this.text.selectAll();
			QuerySyntax.this.text.copy();
		}
	}
}
