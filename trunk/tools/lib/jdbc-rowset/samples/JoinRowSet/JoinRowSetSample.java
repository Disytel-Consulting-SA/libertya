
/**
  * This program is a part of the samples for rowset to be bundled with JWSDP.
  * This demonstrates the basic use case where in memory Joins are performed 
  * on data to to construct meaningful information which would be of little
  * use independently.
  */
  
 // Import the necessary packages
 
 import java.sql.*;
 import javax.sql.*;
 
 import javax.sql.rowset.*;
 import com.sun.rowset.*;
 
 public class JoinRowSetSample {
    
    public static void main(String [] args) {
       
       // Declaration of all the variables used
       String dbUrl;
       String dbUserId;
       String dbPasswd;
       String dbDriver;       
              
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
          
          Statement stmt8 = con.createStatement();
          ResultSet rs = stmt8.executeQuery("select * from coffee");
          
          Statement stmt9 = con.createStatement();
          ResultSet rs1 = stmt9.executeQuery("select * from suppliers");
          
          // Now populate two cached RowSets and add them to a JoinRowSet.
          
          CachedRowSet crs1 = new CachedRowSetImpl();
          crs1.setMatchColumn(1);
          crs1.populate(rs);
          System.out.println("Size of the first cached rowset is: "+crs1.size());
          
          CachedRowSet crs2 = new CachedRowSetImpl();
          crs2.setMatchColumn(1);
          crs2.populate(rs1);
          System.out.println("Size of the first cached rowset is: "+crs2.size());
          
          JoinRowSet jrs = new JoinRowSetImpl();
          jrs.addRowSet(crs1);
          jrs.addRowSet(crs2);
          
          System.out.println("Size of the joinRowSet is: "+jrs.size());
          System.out.println("Contents are");
          
          while(jrs.next()) {
                System.out.println("---------------------------------");
     		System.out.println("First Column : "+jrs.getObject(1));
     		System.out.println("Second Column: "+jrs.getObject(2));
     		System.out.println("Second Column: "+jrs.getObject(3));
     		System.out.println("---------------------------------");
          }
     
          con.close();          
          rs.close();
          rs1.close();
          crs1.close();
          crs2.close();
          jrs.close();          
    } catch (SQLException sqle) {
        System.out.println("SQLException caught: "+sqle.getMessage());
    } catch (Exception e) {
        System.out.println("Unexpected Exception caught: "+e.getMessage());
    }
  }
}