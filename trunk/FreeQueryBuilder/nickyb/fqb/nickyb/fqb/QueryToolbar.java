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

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class QueryToolbar extends JToolBar
{
	private QueryBuilder builder;

	QueryToolbar(QueryBuilder builder)
	{
		super(JToolBar.VERTICAL);

		this.builder = builder;
		this.setFloatable(false);

		add(new NewQueryAction());
		add(new JToolBar.Separator(new Dimension(0,10)));
		add(new JToggleButton(new ChangeWorkModeAction()));
		add(new JToolBar.Separator(new Dimension(0,10)));
	}

	private class ChangeWorkModeAction extends AbstractAction
	{
		ChangeWorkModeAction()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("builder.join"));
			this.putValue(SHORT_DESCRIPTION, "unir");
		}

		public void actionPerformed(ActionEvent e)
		{
			QueryToolbar.this.builder.changeWorkMode();
		}
	}

	private class NewQueryAction extends AbstractAction
	{
		NewQueryAction()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("builder.new"));
			this.putValue(SHORT_DESCRIPTION, "nueva consulta");
		}

		public void actionPerformed(ActionEvent e)
		{
			QueryToolbar.this.builder.clean();
		}
	}
}
