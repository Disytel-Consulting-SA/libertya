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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import nickyb.fqb.util.UIUtilities;

public class UserSession
{
	private static String oldadminfile = System.getProperty("user.home") + File.separator + ".fqb_jdbc_admin";
	private static String userfile = System.getProperty("user.home") + File.separator + ".freequerybuilder";

	protected final static String VERSION	= "version";
	
	protected final static String JDBC_PROFILES	= "profile.names";
	protected final static String JDBC_DRIVERS	= "driver.names";

	protected final static String HISTORY_COMMANDS		= "histroy.commands";
	protected final static String HISTORY_MODELS		= "histroy.models";
	protected final static String HISTORY_MODELS_PLUS	= "histroy.models.plus";
	
	protected static Hashtable jdbc;
	protected static Hashtable generic;
	protected static Hashtable histroy;
	
	protected static void load()
	{
		jdbc	= new Hashtable();
		histroy = new Hashtable();
		generic = new Hashtable();

		try
		{
			File file = new File(userfile);
			if(file.exists())
			{
				FileInputStream istream = new FileInputStream(userfile);
				ObjectInputStream p = new ObjectInputStream(istream);
				
				jdbc = (Hashtable)p.readObject();
				histroy = (Hashtable)p.readObject();
				
				if(istream.available() > 0) generic = (Hashtable)p.readObject();
		
				istream.close();
			}
			else
			{
				jdbc.put(JDBC_DRIVERS,"JDBC-ODBC Bridge");
				jdbc.put("JDBC-ODBC Bridge.file"	,"$CLASSPATH");
				jdbc.put("JDBC-ODBC Bridge.driver"	,"sun.jdbc.odbc.JdbcOdbcDriver");
				jdbc.put("JDBC-ODBC Bridge.example"	,"jdbc:odbc:<DSN>");
				
				jdbc.put(JDBC_PROFILES,"<editme>");
				jdbc.put("<editme>.drv"	,"$JDBC-ODBC Bridge");
				jdbc.put("<editme>.uid"	,"<enter>");
				jdbc.put("<editme>.url"	,"jdbc:odbc:<DSN>");
			}
			
			if(!histroy.containsKey(HISTORY_COMMANDS)) histroy.put(HISTORY_COMMANDS,new Vector());
			if(!histroy.containsKey(HISTORY_MODELS)) histroy.put(HISTORY_MODELS,new Vector());
			if(!histroy.containsKey(HISTORY_MODELS_PLUS)) histroy.put(HISTORY_MODELS_PLUS,new Vector());
			
			File oldfile = new File(oldadminfile);
			if(oldfile.exists())
			{
				FileInputStream istream = new FileInputStream(oldfile);
				Properties old = new Properties();
				old.load(istream);
				istream.close();
				
				for(Enumeration e = old.propertyNames(); e.hasMoreElements();)
				{ 
					String propertyName = e.nextElement().toString();
					jdbc.put(propertyName,old.getProperty(propertyName));
				}
				
				oldfile.renameTo(file);
				UserSession.save();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}			
	}
	
	protected static void save()
	{
		generic.put(VERSION,UIUtilities.MAJOR_VERSION + "." + UIUtilities.MINOR_VERSION);
		
		try
		{
			File file = new File(userfile);
			if(!file.exists()) file.createNewFile();
			
			FileOutputStream ostream = new FileOutputStream(file);
			ObjectOutputStream p = new ObjectOutputStream(ostream);
			
			p.writeObject(jdbc);
			p.writeObject(histroy);
			p.writeObject(generic);
			
			p.flush();
			ostream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}			
	}
}
