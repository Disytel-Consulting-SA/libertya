 
/** 
  * This program is a part of the samples for rowset to be bundled with JWSDP.
  * This is a basic program to demonstarte the use of a WebRowSet.
  * This example demonstrates how data present in the WebRowSet can be 
  * serialized to an XML file and deserialized back again into the WebRowSet.
  * This will help in inter operability of data between web services.
  */
  

 // Import the necessary packages
 
 import java.io.*;
 import java.sql.*;
 import javax.sql.*; 
 
 import javax.sql.rowset.*;
 import com.sun.rowset.*;
 
 public class WebRowSetSample {
 
    public static void main(String [] args) {
       
       // Declaration of all the variables used
       String dbUrl;
       String dbUserId;
       String dbPasswd;
       String dbDriver;
       String dbCommand = "select * from coffees";
       
       FileWriter fWriter;
       FileReader fReader;  
       
       int [] keyCols ={1};     
       
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
                    
         // Create a WebRowSet and set properties.
         
         WebRowSet sender = new WebRowSetImpl();
         sender.setUrl(dbUrl);
         sender.setUsername(dbUserId);
         sender.setPassword(dbPasswd);
         sender.setCommand(dbCommand);
         sender.setKeyColumns(keyCols);
         // Now populate the WebRowSet
         
         sender.execute();
         System.out.println("Size of the WebRowSet is: "+sender.size());                                           
         
         //Delete the row with "Espresso"
         sender.beforeFirst();
         while(sender.next())
         {
            if(sender.getString(1).equals("Espresso"))
            {
               System.out.println("Deleting row with Espresso...");
               sender.deleteRow();
               break;
            }
         }
         
         // Update price of Colombian
         sender.beforeFirst();
         while(sender.next())
         {
            if(sender.getString(1).equals("Colombian"))
            {
               System.out.println("Updating row with Colombian...");
               sender.updateFloat(2,6.99f);
               sender.updateRow();
               break;
            }
         }
         
         
         //sender.acceptChanges();
         int size1 = sender.size();
         
         fWriter = new FileWriter("priceList.xml");
         sender.writeXml(fWriter);
         fWriter.flush();
         fWriter.close();
         
         // Create the receiving WebRowSet object
         
         WebRowSet receiver = new WebRowSetImpl();
         receiver.setUrl(dbUrl);
         receiver.setUsername(dbUserId);
         receiver.setPassword(dbPasswd);
         
         //Now read the XML file.
         
         fReader = new FileReader("priceList.xml");
         receiver.readXml(fReader);
         receiver.acceptChanges();        
         
         int size2 = receiver.size();
         
         if(size1 == size2)
         {
            System.out.println("WebRowSet serialized and de-serialiazed properly");
         } else {
            System.out.println("Error....serializing/de-serializng the WebRowSet");
         }
         
         sender.close();
         receiver.close();
          
       } catch (SQLException sqle) {
          System.out.println("Caught SQLException: "+sqle.getMessage());
       } catch (Exception e) {
          System.out.println("Unexpected Exception caught: "+e.getMessage());
       }
       
    }
 }    