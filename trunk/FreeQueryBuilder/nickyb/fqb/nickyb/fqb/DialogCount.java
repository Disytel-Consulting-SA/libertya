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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.NumberFormat;

import javax.swing.JLabel;

public class DialogCount extends ConfirmDialog
{
	JLabel lbl;
	DesktopEntity item;
	
	public DialogCount(String title, DesktopEntity item)
	{
		super(item.getQueryBuilder(), title, 320, 120);
		
		this.item = item;
		this.getContentPane().add(lbl = new JLabel("wait..."));
	}
	
	protected void onRunning()
	{
		try
		{
			Statement stmt = item.getQueryBuilder().getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM " + item.querytoken.toString());
			
			if(rs.next())
			{
				int records = rs.getInt(1);
				lbl.setText("table contains " + NumberFormat.getInstance().format(records) + " records, view content?");
			}
			
			rs.close();
			stmt.close();
		}
		catch(SQLException sqle)
		{
			lbl.setText(sqle.getMessage());
		}
	}
	
	protected boolean onConfirm()
	{
		return item.continueWithPreview = true;
	}
}
