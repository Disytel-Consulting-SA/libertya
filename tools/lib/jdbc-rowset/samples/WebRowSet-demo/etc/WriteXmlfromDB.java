// This file is for writing a XML file from a DB using
// WebRowSet. This program takes 5 parameters they are
//
// 1. The Url of the DB
// 2. The username of the DB.
// 3. The password for the above user.
// 4. The class that implement s the Driver interface
// 5. The locaction for writing the XML file, this 
//    variable is passed to the ant while building
//    the application.

import java.sql.*;
import javax.sql.*;
import java.io.*;

import javax.sql.rowset.*;
import com.sun.rowset.*;

public class WriteXmlfromDB {

   public static void main(String []args) {   
      
      String dbUrl;
      String dbUserId;
      String dbPasswd;
      String dbDriver;
      String toLocation;
      String fileSep;
      
      FileWriter fWriter;
      
      dbUrl = args[0];
      System.out.println("DB Url is: "+dbUrl);
      
      dbUserId = args[1];
      System.out.println("DB UserId is: "+dbUserId);
      
      dbPasswd = args[2];
      System.out.println("DB Password is: "+dbPasswd);
      
      dbDriver = args[3];
      System.out.println("DB driver is: "+dbDriver);
      
      toLocation = args[4];
      System.out.println("Location of XML file is: "+toLocation);
      
      try {
         Class.forName(dbDriver);
      } catch(Exception e) {
         System.out.println("Error loading Driver: "+e.getMessage());
         System.exit(1);
      }
      
      try {
         Connection con = DriverManager.getConnection(dbUrl,dbUserId,dbPasswd);
         Statement stmt = con.createStatement();
      
         try {
            stmt.executeUpdate("drop table tmp_samples");
         } catch(Exception e) {
            //Do nothing here
         }
      
         stmt.executeUpdate("create table tmp_samples(i_val int,f_val real,d_val double precision,date_val date,s_val varchar2(20),b_val numeric(10,3),l_val number(38),bool_val number(1,0),byte_val number(3,0),short_val number(5,0))");
         stmt.executeUpdate("insert into tmp_samples values(27,23.45,22.77,{d '1998-12-05'}, 'cat',12.375,12345,1,11,17)");
         stmt.executeUpdate("insert into tmp_samples values(28,23.45,22.77,{d '1998-12-05'}, 'cat',12.375,12345,1,11,17)");
         stmt.executeUpdate("insert into tmp_samples values(29,23.45,22.77,{d '1998-12-05'}, 'cat',12.375,12345,1,11,17)");
         stmt.executeUpdate("insert into tmp_samples values(30,23.45,22.77,{d '1998-12-05'}, 'cat',12.375,12345,1,11,17)");
      
         con.commit();
         
         ResultSet rs = stmt.executeQuery("select * from tmp_samples");
         
         WebRowSet wrs = new WebRowSetImpl();
         wrs.populate(rs);
         
         fileSep = System.getProperty("file.separator");         
         
         fWriter = new FileWriter(toLocation+fileSep+"data.xml");
         wrs.writeXml(fWriter);
         
         System.out.println("Written XML file to : "+toLocation);
         
      } catch(Exception e) {
         System.out.println("Caught Unexpected exception: "+e.getMessage());
      }
      
   }
} // End of class.