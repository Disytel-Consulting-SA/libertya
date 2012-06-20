/**
  * This program is a part of the samples for rowset to be bundled with JWSDP.
  * This is a basic sample that illustrates the use of CachedRowSet.
  * This presents the basic use case and shows how operations happen in the 
  * memory and are then synchronized back to the database.
  */
  
 //Import the necessary packages.
 
 import java.sql.*;
import java.util.Calendar;

 import javax.sql.*;
 
 import javax.sql.rowset.*;
import com.sun.rowset.*;
 
 public class CachedRowSetSampl { 
 
    public static void main(String [] args) {
    
       // Declaration of all the variables used
       String dbUrl;
       String dbUserId;
       String dbPasswd;
       String dbDriver;
       String dbCommand = "select * from merch_inventory";
       
       int i = 0;
       
       // Declaring the object to be instantiated through
       // the CachedRowSet interface.
       
       CachedRowSet crs;
       
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
         
        Connection con = DriverManager.getConnection(dbUrl,dbUserId,dbPasswd);          
        
        // Create new cachedRowSet object.
        crs = new CachedRowSetImpl();
        
        // Set all the properties.
        crs.setUrl(dbUrl);
        crs.setUsername(dbUserId);
        crs.setPassword(dbPasswd);
        crs.setCommand(dbCommand);
        
        // Setting the page size to 4, such that we
        // get the data in chunks of 4 rows @ a time.
        
        crs.setPageSize(4);
        
        // Now get the first set of data.
        crs.execute();
        
        // Keep on getting data in chunks until done.
        while(crs.nextPage()) {
           System.out.println("Page number: "+i);
           while(crs.next()) {
              if(crs.getInt("ITEM_ID") == 1235) {
                 System.out.println("QUAN value: "+crs.getInt("QUAN"));
                 crs.updateInt("QUAN",99);
                 crs.updateRow(); 
                 
                 // This acceptChanges eventhough inside a loop will happen 
                 // only once since the if condition is satisfied only for
                 // one ITEM_ID.
                 // Syncing the row back to the DB                  
                 crs.acceptChanges();               
                 
              }
           } // End of inner while
                      
          i++; 
        } // End of outer while                        
                
        // Inserting a new row

        // Doing a previous page to come back to the last page
        // as we'll be after the last page.
        crs.previousPage();
        
    Calendar cld = Calendar.getInstance();
	cld.set( 2006, 4, 1, 0, 0 ,0);
		
	crs.moveToInsertRow();
	crs.updateInt("ITEM_ID",6922);
	crs.updateString("ITEM_NAME","TableCloth");
	crs.updateInt("SUP_ID",927);
	crs.updateInt("QUAN",14);
	crs.updateDate("DATE_VAL",(Date)cld.getTime());
	crs.insertRow();
	crs.moveToCurrentRow();

	// Syncing the new ro back to the database.

	crs.acceptChanges();
 
        crs.close();
          
       } catch (SQLException sqle) {
          sqle.printStackTrace();
          System.out.println("Caught SQLException: "+sqle.getMessage());
       } catch (Exception e) {
          System.out.println("Caught unexpected Exception: "+e.getMessage());
       }
    }
 }