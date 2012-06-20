

/** This program is a part of the samples for rowset to be bundled with JWSDP.
  * This is a basic program to demonstarte the functionality of a 
  * FilteredRowSet. This demonstrates the basic use case of filtering out the
  * data in memory, so will avoide multiple fetches from the DB when the 
  * filter is removed or the filtering criteria changes.
  */
  
 //Import the necessary packages
 
 import java.sql.*;
 import javax.sql.*;
 
 import javax.sql.rowset.*;
 import com.sun.rowset.*; 
 
 public class FilteredRowSetSample {
    
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
	  frs.setCommand(dbCommand);
	  frs.execute();
	  
	  // Now create the filter and set it.
	  // Range 1 is the calss that implements the Predicate interface.
	  
	  Range1 storeFilter= new Range1(10000,10999,1);
	  frs.setFilter(storeFilter);
	  
	  // After setting the filter, the contents of the rowset will 
	  // only be those rows that satisfy the filter criteria.
	  // Display the contents here.
	  
	  while(frs.next()) {
	     // After setting the filter, the contents of the rowset will 
	     // only be those rows that satisfy the filter criteria.
	     // Set the second filter also to filter out the houses only in
	     // SFO city 10000 and 10999 are displayed.
	     
	     System.out.println("Store id is: "+frs.getInt(1));
	  }
	  
	   // Now try to insert a row that does not satisfy the criteria
	   // a SQLException will be thrown.
	   
	   try 
	   {
	      frs.moveToInsertRow();
	      frs.updateInt(1,22999);
	      frs.updateString(2,"LA");
	      frs.updateInt(3,4455);
	      frs.updateInt(4,1579);
	      frs.updateInt(5,6289);
	      frs.insertRow();
	      frs.moveToCurrentRow();
	   } catch(SQLException sqle) {
	      System.out.println("A row that does not staisfy the filter is being inserted");
	      System.out.println("Message: "+sqle.getMessage());
	   }
	   frs.close();
	   con.close();
          
       } catch (SQLException sqle) {
          System.out.println("SQLException caught: "+sqle.getMessage());
       } catch(Exception e) {
          System.out.println("Unexpected exception caught: "+e.getMessage());
       }
    }
 }