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

package nickyb.fqb.util;

import java.net.URL;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageStore
{
	private static HashMap store = new HashMap();
	
	static
	{
		store.put("command.clean"	, createIcon("/images/stock-tool-button-eraser-16.png"));
		store.put("command.stop"	, createIcon("/images/stock_stop-16.png"));
		
		store.put("store.open"		, createIcon("/images/stock_open-16.png"));
		store.put("store.save"		, createIcon("/images/stock_save-16.png"));
		
		store.put("clipboard.copy" , createIcon("/images/stock_copy-16.png"));
		store.put("clipboard.paste", createIcon("/images/stock_paste-16.png"));

		store.put("database.jdbc"		, createIcon("/images/stock_data-sources-hand-16.png"));
		store.put("database.search"		, createIcon("/images/stock_macro-watch-variable-16.png"));
		store.put("database.connect"	, createIcon("/images/stock_connect-16.png"));
		store.put("database.disconnect"	, createIcon("/images/stock_disconnect-16.png"));
		
		store.put("report.new"			, createIcon("/images/stock_insert_graphic-16.png"));
		store.put("report.preview"		, createIcon("/images/stock_presentation-styles-16.png"));
		store.put("report.page.next"	, createIcon("/images/stock_right-16.png"));
		store.put("report.page.prev"	, createIcon("/images/stock_left-16.png"));
		store.put("report.page.print"	, createIcon("/images/stock_print-16.png"));
		store.put("report.group.add"	, createIcon("/images/stock_group-cells-16.png"));
		store.put("report.group.del"	, createIcon("/images/stock_ungroup-cells-16.png"));
		
		store.put("builder.new"		, createIcon("/images/stock_data-new-query-16.png"));
		store.put("builder.join"	, createIcon("/images/stock_node-add-16.png"));
		store.put("builder.update"	, createIcon("/images/stock_update-data-16.png"));
		store.put("builder.execute"	, createIcon("/images/stock_exec-16.png"));
		store.put("builder.preview"	, createIcon("/images/stock_slide-show-16.png"));
		store.put("builder.refresh"	, createIcon("/images/stock_refresh-16.png"));
		store.put("builder.distinct", createIcon("/images/stock_autofilter-16.png"));

		store.put("history.edit"	, createIcon("/images/stock_data-edit-query-16.png"));
		store.put("history.delete"	, createIcon("/images/stock_delete-16.png"));
		store.put("history.comment"	, createIcon("/images/stock_draw-callouts-16.png"));
		
		store.put("window.builder"	, createIcon("/images/stock_data-queries-16.png"));
		store.put("window.command"	, createIcon("/images/stock_data-edit-sql-query-16.png"));
		store.put("window.history"	, createIcon("/images/stock_slide-reherse-timings-16.png"));
		store.put("window.report"	, createIcon("/images/stock_graphic-styles-16.png"));
	}

	public static Icon getIcon(String iconkey)
	{
		return (Icon)store.get(iconkey);
	}
	
	public static Icon createIcon(String filename)
	{
		if(filename!=null)
		{
			Class c = ImageStore.class;
			return createIcon(c.getResource(filename));
		}
		
		return null;
	}
	
	public static Icon createIcon(URL url)
	{
		return new ImageIcon(url);
	}	
}
