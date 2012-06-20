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

package nickyb.fqb.runtime;

import javax.swing.JTabbedPane;

import nickyb.fqb.*;
import nickyb.fqb.util.*;

public class DialogUpdateData extends ModalDialog
{
	private QueryBuilder builder;
	private QueryTokens.Table querytoken;
	
	private BuildBasePane delete;
	private BuildBasePane insert;
	private BuildBasePane update;
	
	public DialogUpdateData(QueryBuilder builder, QueryTokens.Table table)
	{
		super(builder, table.toString(false,false),640,480);
		
		this.querytoken = table;
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("delete",delete = new BuildDelete(builder.getConnection()));
		tabs.addTab("insert",insert = new BuildInsert(builder.getConnection()));
		tabs.addTab("update",update = new BuildUpdate(builder.getConnection()));
		getContentPane().add(tabs);
	}
	
	private void onApply()
	{
	}

	protected void onRunning()
	{
		delete.setEnabled(false);
		insert.setEnabled(false);
		update.setEnabled(false);
		
		delete.setQueryToken(querytoken);
		insert.setQueryToken(querytoken);
		update.setQueryToken(querytoken);
		
		delete.setEnabled(true);
		insert.setEnabled(true);
		update.setEnabled(true);
	}
}
