package nickyb.fqb.runtime;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

import java.util.Hashtable;
import java.util.Properties;

public class ConnectionManager
{
	private Hashtable registry = new Hashtable();
 	private ClassLoader loader;

	private String drv;
	private String url;
	private String uid;
	private String pwd;

	public void setLoader(ClassLoader cl)
	{
		loader = cl;
	}
	
	public void setInfo(String drv,String url,String uid,String pwd)
	{
		this.drv = drv;
		this.url = url;
		this.uid = uid;
		this.pwd = pwd;
	}
	
	public Connection open() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		Driver d = null;		
		if(!registry.containsKey(drv))
		{
			Class c = (loader==null) ? Class.forName(drv) : Class.forName(drv,true,loader);
			d = (Driver)c.newInstance();
	
			registry.put(drv,d);
		}
		else
		{
			d = (Driver)registry.get(drv);
		}
		
		Properties info = new Properties();
		if (uid != null)
			info.put("user", uid);
		if (pwd != null)
			info.put("password", pwd);

		return d.connect(url,info);
	}
}