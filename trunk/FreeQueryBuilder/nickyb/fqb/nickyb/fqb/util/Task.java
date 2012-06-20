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

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.text.NumberFormat;

import java.util.Arrays;

public class Task implements Runnable
{
	private TaskSource source;
	private TaskTarget target;

	private Statement stmt = null;
	private ResultSet rs = null;

	private int maxSize = 50;
	
	public Task(TaskSource source, TaskTarget target)
	{
		this.source = source;
		this.target = target;
	}
	
	public void run()
	{
		target.onTaskStarting();
		
		try
		{
			String syntax = source.getSyntax().trim();
			if(source.getConnection()!=null)
			{
				if(syntax.length() > 6)
				{
					target.message("waiting response...");
					stmt = source.getConnection().createStatement();
					
					String sqlcmd = syntax.toUpperCase().substring(0,6);
					if(sqlcmd.equals("SELECT"))
					{
						rs = stmt.executeQuery(syntax);
						printSelect(target.getOutputBuffer());
						rs.close();
					}
					else
					{
						rs = null;
						stmt.executeUpdate(syntax);
						printUpdate(target.getOutputBuffer());
					}
					
					stmt.close();
					
					if(!target.continueRun()) target.message("stopped!");
				}
			}
			else
			{
				target.message("No connection!");
			}
		}
		catch(SQLException sqle)
		{
			target.message("SQLException!");
			target.getOutputBuffer().append(sqle.toString()+"\n");
		}
		finally
		{
			target.onTaskFinished();
		}
	}
	
	private void printUpdate(StringBuffer stream)
		throws SQLException
	{
		target.message(stmt.getUpdateCount() + " row/s affected");
	}
	
	private void printSelect(StringBuffer stream)
		throws SQLException
	{
		if(rs==null) return;
		
		long started = System.currentTimeMillis();

		target.message("reading...");
		
		StringBuffer header = new StringBuffer("| ");
		StringBuffer divider = new StringBuffer("+-");
		
		int columnDisplaySize[] = new int[this.getColumnCount()];
		for(int i=1; i<=this.getColumnCount(); i++)
		{
			header.append(this.getColumnLabel(i));
			
			char[] filler = new char[this.getColumnLabel(i).length()];
			Arrays.fill(filler, '-');
			divider.append(filler);
			
			columnDisplaySize[i-1] = this.getColumnDisplaySize(i);
			int diff = columnDisplaySize[i-1] - this.getColumnLabel(i).length();
			
			if(diff > 0)
			{
				if(diff > maxSize)
				{
					diff = maxSize - this.getColumnLabel(i).length();
					columnDisplaySize[i-1] = maxSize;
				}
				
				filler = new char[diff];
				Arrays.fill(filler, ' ');
				header.append(filler);

				Arrays.fill(filler, '-');
				divider.append(filler);
			}
			else
			{
				columnDisplaySize[i-1] = this.getColumnLabel(i).length();
			}

			header.append(" | ");
			divider.append("-+-");
		}
		
		divider.deleteCharAt(divider.length()-1);
		header.deleteCharAt(header.length()-1);
		
		stream.append(divider.toString() + "\n");
		stream.append(header.toString() + "\n");
		stream.append(divider.toString() + "\n");
		
		int bytes = 0;
		int rowcount = 0;
		while(rs.next() && target.continueRun())
		{
			target.message("reading record " + (++rowcount) + " , bytes " + bytes);
			
			StringBuffer row = new StringBuffer("| ");
				
			for(int i=1; i<=this.getColumnCount(); i++)
			{
				String value = rs.getString(i);
				if(value==null) value = new String();
				
				bytes += value.length();
				
				int diff = columnDisplaySize[i-1] - value.length();
				if(diff > 0)
				{
					char[] filler = new char[diff];
					Arrays.fill(filler, ' ');
					row.append(value + new String(filler));
				}
				else if(diff < 0)
				{
					value = value.substring(0, columnDisplaySize[i-1]-3);
					row.append(value + "...");
				}
				else
					row.append(value);
				
				row.append(" | ");
			}
				
			stream.append(row.toString()+"\n");
		}
		
		long ended = System.currentTimeMillis();
		
		target.message("records: " + NumberFormat.getInstance().format(rowcount) + " [ time: " + NumberFormat.getInstance().format((ended - started)) + " bytes: " + NumberFormat.getInstance().format(bytes) + " ]");
		if(rowcount>0) stream.append(divider.toString() + "\n");
	}
	
	public int getColumnCount() throws SQLException
	{
		return rs.getMetaData().getColumnCount();
	}

	public int getColumnDisplaySize(int index) throws SQLException
	{
		return rs.getMetaData().getColumnDisplaySize(index);
	}

	public String getColumnLabel(int index) throws SQLException
	{
		return rs.getMetaData().getColumnLabel(index);
	}
}
