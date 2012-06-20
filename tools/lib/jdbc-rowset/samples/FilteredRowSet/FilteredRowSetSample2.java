/* This is a sample program as a part of the RowSet Tutorial.
 * This sample illustrates the basi use case for a 
 * FilteredRowSet in the coffee break scenario. This example
 * illustrates how to filter out a set of rows from the data
 * already fetched from the database.
 */
 
 import java.sql.*;
 import javax.sql.rowset.*;
 import com.sun.rowset.*;
 
 public class FilteredRowSetSample2 {
 
    public static void main(String [] args) {
    
       // Declaration of all the variables used
       String dbUrl;
       String dbUserId;
       String dbPasswd;
       String dbDriver;
       String dbCommand = "select * from Coffee_Houses";
       
       // The upper and lower limits for the filter
       int lo_depid = 1000;
       int hi_depid = 1003;
       
       // Declaring the object to be instantiated through
       // the FilteredRowSet interface.              
       
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
                 
          // Now get the connection handle
          Connection con = DriverManager.getConnection(dbUrl,dbUserId,dbPasswd);    	  
	  
	  // Now all the data has been inserted into the DB.
	  // Create a FilteredRowSet, set the properties and
	  // populate it with this data.
	  
	  FilteredRowSet frs = new FilteredRowSetImpl();
	  frs.setUsername(dbUserId);
	  frs.setPassword(dbPasswd);
	  frs.setUrl(dbUrl);
	  frs.setCommand("select * from Coffee_Houses");
	  frs.execute();
	  
	  // Now create the filter and set it.
	  // Range 1 is the calss that implements the Predicate interface.
	  
	  Range1 storeFilter= new Range1(10000,10999,1);
	  frs.setFilter(storeFilter);
	  
	  // After setting the filter, the contents of the rowset will 
	  // only be those rows that satisfy the filter criteria.
	  // Set the second filter also to filter out the houses only in
	  // SFO city
	  
	  Range2 cityFilter = new Range2("SFO","SFO",2);
	  frs.setFilter(cityFilter);
	  
	  // Display the contents here.	  
	  
	  while(frs.next()) {
	     // Now only those stores whose store id is between 
	     // 10000 and 10999 and only those in the city SFO
	     // are displayed  
	     
	     System.out.println("Store id is: "+frs.getInt(1)+"...City is: "+frs.getString(2));	     
	  }
	  	  	   
	   frs.close();
	   con.close();
	   	  
       } catch(Exception e ) {
          System.err.println("Caught unexpected Exception: "+e.getMessage());       
       } 
    }
 }