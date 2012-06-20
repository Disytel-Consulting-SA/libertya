/**
  * This program is responsible for initializing the database.
  * This should be run before the samples to ensure that all the samples
  * have a consistent set of data to work with. This is placed in the 
  * common directory as it is common to all the samples.
  */
  
 // Import the necessary packages.
 
 import java.sql.*;
import java.util.Calendar;

import javax.sql.*;
 
 public class InitDatabase {
    
    public static void main(String []args) {
    
       // Declare the variables that are going to be used 
       
       Connection con = null;
       PreparedStatement pStmt = null;       
       String url = null;
       String userId;
       String passwd;
       String driver;
       Statement stmt1,stmt2,stmt3,stmt4,stmt5,stmt6,stmt7,stmt8,stmt9;
       
       url = args[0];
       System.out.println("Url is: "+url);
       
       userId = args[1];
       System.out.println("User Id is: "+userId);
       
       passwd = args[2];
       
       driver = args[3];
       System.out.println("Driver is: "+driver);
       
       try 
       {
          Class.forName(driver);
          System.out.println("Loading class driver");
       
          con = DriverManager.getConnection(url,userId,passwd);
          System.out.println("Got a connection handle...");
       
          //First drop all the tables and then create and populate data
       
       
          pStmt = con.prepareStatement("drop table merch_inventory");
          pStmt.executeUpdate();
                    
          pStmt = null;
          
          pStmt = con.prepareStatement("drop table coffees");
          pStmt.executeUpdate();
         
          pStmt = null;
          
          pStmt = con.prepareStatement("drop table Coffee_Houses");
          pStmt.executeUpdate();
          
          pStmt = null;
          
          pStmt = con.prepareStatement("drop table coffee");
          pStmt.executeUpdate();
          
          pStmt = null;
          
          pStmt = con.prepareStatement("drop table suppliers");
          pStmt.executeUpdate();
          
          pStmt = null;
          
          pStmt = con.prepareStatement("drop table coffes");
          pStmt.executeUpdate();
          
          con.commit();
          
       } catch (Exception e) {
          // Don't do anythin here.             
       }
       
       try
       {
       // Now create the tables and populate data one by one.
       
        stmt1 = con.createStatement();
        stmt1.executeUpdate("create table merch_inventory(item_id int, item_name varchar(20),sup_id int, quan int, date_val timestamp)");
        
        pStmt = con.prepareStatement("insert into merch_inventory values(?,?,?,?,?)");
        
        Calendar cld = Calendar.getInstance();
		cld.set( 2006, 4, 1, 0, 0 ,0);
        
        // this has to be done 13 times for each row   
        pStmt.setInt(1,1234);
        pStmt.setString(2,"Cup_Large");
        pStmt.setInt(3,456);
        pStmt.setInt(4,28);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,1235);
        pStmt.setString(2,"Cup_Small");
        pStmt.setInt(3,456);
        pStmt.setInt(4,36);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate();          
        
        pStmt.setInt(1,1236);
        pStmt.setString(2,"Saucer");
        pStmt.setInt(3,456);
        pStmt.setInt(4,64);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,1287);
        pStmt.setString(2,"Carafe");
        pStmt.setInt(3,456);
        pStmt.setInt(4,12);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,6931);
        pStmt.setString(2,"Carafe");
        pStmt.setInt(3,927);
        pStmt.setInt(4,3);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,6935);
        pStmt.setString(2,"PotHolder");
        pStmt.setInt(3,927);
        pStmt.setInt(4,88);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,6977);
        pStmt.setString(2,"Napkin");
        pStmt.setInt(3,927);
        pStmt.setInt(4,108);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,6979);
        pStmt.setString(2,"Towel");
        pStmt.setInt(3,927);
        pStmt.setInt(4,24);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,4488);
        pStmt.setString(2,"CofMaker");
        pStmt.setInt(3,8372);
        pStmt.setInt(4,5);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,4490);
        pStmt.setString(2,"CofGrinder");
        pStmt.setInt(3,8732);
        pStmt.setInt(4,9);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,4495);
        pStmt.setString(2,"EspMaker");
        pStmt.setInt(3,8732);
        pStmt.setInt(4,4);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate(); 
        
        pStmt.setInt(1,6914);
        pStmt.setString(2,"Cookbook");
        pStmt.setInt(3,927);
        pStmt.setInt(4,12);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate();
        
        pStmt.setInt(1,6917);
        pStmt.setString(2,"Boiler");
        pStmt.setInt(3,927);
        pStmt.setInt(4,10);
        pStmt.setObject(5,cld.getTime());
        pStmt.executeUpdate();
        
        
        
        stmt1 = con.createStatement();
        stmt1.executeUpdate("create table coffees(cof_name varchar(30),price decimal(6,2))");
         
        stmt2 = con.createStatement();
        stmt2.executeUpdate("insert into coffees values('Colombian',7.99)");
         
        stmt3 = con.createStatement();
        stmt3.executeUpdate("insert into coffees values('French_Roast',8.99)");
         
        stmt4 = con.createStatement();
        stmt4.executeUpdate("insert into coffees values('Espresso',9.99)");
         
        stmt5 = con.createStatement();
        stmt5.executeUpdate("insert into coffees values('Colombian_Decaf',8.99)");
         
        stmt6 = con.createStatement();
        stmt6.executeUpdate("insert into coffees values('French_Roast_Decaf',9.99)");
        
        
        
        stmt2 = con.createStatement();
	stmt2.executeUpdate("create table Coffee_Houses(store_id int,city varchar(20),coffee int,merch int,total int)");
	  
	stmt3 = con.createStatement();
	stmt3.executeUpdate("insert into Coffee_Houses values(10023,'Mendoncino',3450,2005,5455)");
	  
	stmt4 = con.createStatement();
	stmt4.executeUpdate("insert into Coffee_Houses values(33002,'Seattle',4699,3108,7808)");
	  
	stmt5 = con.createStatement();
	stmt5.executeUpdate("insert into Coffee_Houses values(10040,'SFO',5386,2841,8227)");
	  
	stmt6 = con.createStatement();
	stmt6.executeUpdate("insert into Coffee_Houses values(32001,'Portland',3147,3759,6726)");
	  
	stmt7 = con.createStatement();
	stmt7.executeUpdate("insert into Coffee_Houses values(10042,'SFO',2863,1874,4710)");
	  
	stmt8 = con.createStatement();
	stmt8.executeUpdate("insert into Coffee_Houses values(10024,'Sacremento',1987,2341,4328)");
	  
	stmt9 = con.createStatement();
	stmt9.executeUpdate("insert into Coffee_Houses values(10039,'Caramel',2691,1121,3812)");
	  
	stmt1 = con.createStatement();
	stmt1.executeUpdate("insert into Coffee_Houses values(10041,'LA',1533,1007,2540)");
	  
	stmt2 = con.createStatement();
	stmt2.executeUpdate("insert into Coffee_Houses values(33002,'Olympia',2733,1550,4283)");
	
	
	
	stmt2 = con.createStatement();
        stmt2.executeUpdate("create table coffee(sup_id int, cof_id int, cof_name varchar(10))");
          
        stmt3 = con.createStatement();
        stmt3.executeUpdate("create table suppliers(sup_id int)");
          
        stmt4 = con.createStatement();
        stmt4.executeUpdate("insert into coffee values (100,10,'Espresso')");
          
        stmt5 = con.createStatement();
        stmt5.executeUpdate("insert into coffee values (100,11,'Capaccino')");
          
        stmt6 = con.createStatement();
        stmt6.executeUpdate("insert into suppliers values (100)");
          
        stmt7 = con.createStatement();
        stmt7.executeUpdate("insert into suppliers values (101)");
        
        
        
        stmt1 = con.createStatement();
        stmt1.executeUpdate("create table coffes(cof_name varchar(30),sup_id int, price decimal(6,2), sales int,total int)");
        
        stmt2 = con.createStatement();
        stmt2.executeUpdate("insert into coffes values('Colombian',100,7.99,0,0)");
         
        stmt3 = con.createStatement();
        stmt3.executeUpdate("insert into coffes values('French_Roast',101,8.99,0,0)");
         
        stmt4 = con.createStatement();
        stmt4.executeUpdate("insert into coffes values('Espresso',100,9.99,0,0)");
         
        stmt5 = con.createStatement();
        stmt5.executeUpdate("insert into coffes values('Colombian_Decaf',101,8.99,0,0)");
         
        stmt6 = con.createStatement();
        stmt6.executeUpdate("insert into coffes values('French_Roast_Decaf',49,9.99,0,0)");
        
        
        con.commit();
        
        
       } catch (SQLException sqle) {          
       }
    }
    
    
 } 
