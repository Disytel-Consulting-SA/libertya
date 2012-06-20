/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package book.receiver;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.soap.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import javax.sql.*;

import javax.sql.rowset.*;
import com.sun.rowset.*;
import javax.naming.*;
import javax.sql.rowset.spi.*;
import com.sun.xml.messaging.soap.server.SAAJServlet;
import javax.servlet.http.*;
import javax.servlet.*;

/**
 * Sample servlet that receives messages.
 *
 * 
 */

public class ReceivingServlet extends SAAJServlet
{

    static Logger
        logger = Logger.getLogger("Samples/Book");

    // This is the application code for handling the message.. Once the
    // message is received the application can retrieve the soap part, the
    // attachment part if there are any, or any other information from the
    // message.

    public SOAPMessage onMessage(SOAPMessage message) {
        System.out.println("On message called in receiving servlet");
        try {
            System.out.println("Here's the message: ");
            message.writeTo(System.out);

	    SOAPHeader header = message.getSOAPHeader();
	    SOAPBody body = message.getSOAPBody();

	    Iterator attachs = message.getAttachments();

	    while(attachs.hasNext()) {
		AttachmentPart ap = (AttachmentPart)attachs.next();
		System.out.println("Content type: "+ap.getContentType());
		Object content = ap.getContent();
		
		WebRowSet wrs = new WebRowSetImpl();
		wrs.readXml(new StringReader(content.toString()));
		System.out.println("Got a webRowSet");
		
		wrs.next();
		System.out.println("On first row...."+wrs.getInt(1));
		
		
		Context ctx = new InitialContext();
		Context envCtx = (Context)ctx.lookup("java:comp/env");
		
		DataSource ds = (DataSource)envCtx.lookup("jdbc/PointBase");
		Connection con = ds.getConnection();
		DatabaseMetaData dbMd = con.getMetaData();
		System.out.println("URL is: "+dbMd.getURL());
		
		PreparedStatement pDrop = con.prepareStatement("drop table WRS_Objects");
		try
		{
		   pDrop.executeUpdate();
		   con.commit();
		} catch(SQLException sqle) {
		   //Do nothing
		}
		
		PreparedStatement pStmt = con.prepareStatement("create table WRS_Objects(i_val int,f_val real,d_val double precision,date_val date,s_val varchar2(20),b_val numeric(10,3),l_val number(38),bool_val number(1,0),byte_val number(3,0),short_val number(5,0))");
                pStmt.executeUpdate();
                
                
                PreparedStatement pStmt1 = con.prepareStatement("insert into WRS_Objects values(1,3.45,2.77,{d '1998-12-07'}, 'ate',2.375,345,0,1,7)");
                pStmt1.executeUpdate();
                
                con.commit();
                
                
                wrs.setDataSourceName("java:comp/env/jdbc/PointBase");
                wrs.setUsername("pbpublic");
                wrs.setPassword("pbpublic");
                wrs.setTableName("WRS_Objects");
                
                
                Statement stmt = con.createStatement();                
                ResultSet rs = stmt.executeQuery("select * from WRS_Objects");
                rs.next();
                System.out.println("First element is: "+rs.getInt(1));
                            
                CachedRowSet crs = new CachedRowSetImpl();
                crs.populate(rs);                
                
                wrs.beforeFirst();
                while(wrs.next()) {
                   crs.moveToInsertRow();
                   for(int i = 1; i <= (crs.getMetaData()).getColumnCount(); i++) 
                   {
                      crs.updateObject(i,wrs.getObject(i));
                   }
                   crs.insertRow();
                   crs.moveToCurrentRow();
                }
                
                crs.setDataSourceName("java:comp/env/jdbc/PointBase");
                crs.acceptChanges();
                
                System.out.println("Written to the database");                                
			
		pStmt.close();
		//pStmt1.close();
		pDrop.close();
		rs.close();
		stmt.close();
		con.close();
		wrs.close();
	    }

            return message;

        } catch(Exception e) {
            logger.log(
                Level.SEVERE,
                "Error in processing or replying to a message", 
                e);
            return null;
        }
    }        
}

