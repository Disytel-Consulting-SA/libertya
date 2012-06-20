 
/** 
  * This program is a part of the samples for rowset to be bundled with JWSDP.
  * This is a basic program to demonstarte the use of a JdbcRowSet.
  * This example demonstrates how data present in the JdbcRowSet can be 
  * serialized to an XML file and deserialized back again into the JdbcRowSet.
  * This will help in inter operability of data between web services.
  */
  

 // Import the necessary packages
 
 import java.io.*;
 import java.sql.*;
 import javax.sql.*; 
 
 import javax.sql.rowset.*;
 import com.sun.rowset.*;
 
 public class JdbcRowSetSample {
 
    public static void main(String [] args) {
       
       // Declaration of all the variables used
       String dbUrl;
       String dbUserId;
       String dbPasswd;
       String dbDriver;
       String dbCommand = "select * from employees";
       ResultSet res = null;
       Connection con = null;
       Statement stmt = null;
              
       // Declaring the object to be instantiated through
       // the JdbcRowSet interface.
       
       JdbcRowSet jdbcRs;
       
       //First setup all the necessary properties
       
       dbUrl = args[0];
       System.out.println("Url: "+dbUrl);
       
       dbUserId = args[1];
       System.out.println("UserId: "+dbUserId);
       
       dbPasswd = args[2];
       System.out.println("Password: "+dbPasswd);
       
       dbDriver = args[3];
       System.out.println("Driver: "+dbDriver);
       
       try {
          Class.forName(dbDriver);
       } catch(Exception e) {
          System.out.println("Unable to load driver: "+e.getMessage());
          System.exit(1);
       }
       
       try
       {                             
          // Get the data into memory ,using populate
          // This JdbcRowSet behaves exactly like the ResultSet
          // whose handle has been passed to it.
          
          con = DriverManager.getConnection(dbUrl,dbUserId,dbPasswd);                    
      
      	  stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);     
          res = stmt.executeQuery("select * from COFFES");
                 
          // Creating a new JdbcRowSet
          jdbcRs = new JdbcRowSetImpl(res);
                    
      
          // Moving the cursor to the third row and updating
          // value of Espresso with 10.99
      
          jdbcRs.absolute(3);
          jdbcRs.updateFloat("PRICE",10.99f);
          jdbcRs.updateRow();
      
          jdbcRs.first();
                    
      
          // Inserting the first row of two rows
          jdbcRs.moveToInsertRow();
          jdbcRs.updateString("COF_NAME","HouseBlend");
          jdbcRs.updateInt("SUP_ID",49);
          jdbcRs.updateFloat("PRICE",7.99f);
          jdbcRs.updateInt("SALES",0);
          jdbcRs.updateInt("TOTAL",0);
          jdbcRs.insertRow();
          jdbcRs.moveToCurrentRow();
      
          // Inserting the second row of two rows
          jdbcRs.moveToInsertRow();
          jdbcRs.updateString("COF_NAME","HouseDecaf");
          jdbcRs.updateInt("SUP_ID",49);
          jdbcRs.updateFloat("PRICE",8.99f);
          jdbcRs.updateInt("SALES",0);
          jdbcRs.updateInt("TOTAL",0);
          jdbcRs.insertRow();
          jdbcRs.moveToCurrentRow();
                    
      
          // Move the cursor to the last row.
          // Then delete this row.
      
          jdbcRs.last();
          jdbcRs.deleteRow();
          
          jdbcRs.close();          
          
       } catch (SQLException sqle) {
          sqle.printStackTrace();
          System.out.println("Caught SQLException: "+sqle.getMessage());
       } catch (Exception e) {
          e.printStackTrace();
          System.out.println("Unexpected Exception caught: "+e.getMessage());
       }
       
    }
 }    