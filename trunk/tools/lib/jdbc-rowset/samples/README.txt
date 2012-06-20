JDBC RowSet Implementations - JSR-114

Contents

1. Overview of Samples
   
   ----------------------
        CachedRowSet
   ----------------------
   
   This is a sample for demonstrating the functionality of CachedRowSet.
   This example shows how to get the data into the memory,
   (i.e into the CachedRowSet) and also the operations that can be performed on the 
   data in the memory like updation of values, inserting new rows, deleting existing
   rows,etc.
   
      Finally when the user is done with all the operations it shows how to sync the
   data in memory back to the database.
   
   ---------------------
        WebRowSet
   ---------------------
   
   This is a sample for demonstrating the functionality of a WebRowSet.
   It shows how to serialize data in the WebRowSet to an XML file once 
   the WebRowSet has been populated with data in the database. This 
   data in XML format is interoperable.
      
      It also shows how to populate a WebRowSet by reading an XML file,
   this can be used in scenarios where data is received from a remote
   sender in XML format and needs to be written to a database.
   
   
   -------------------------   
        FilteredRowSet
   -------------------------
   
   This is a sample for demonstrating the functionality of a FilteredRowSet.
   This example shows how to filter out the data that is populated into a
   RowSet. The important thing to note here is that filtering happens on the
   data in memory.
      
      This is a very useful component, since the filtering is in memory it 
   avoids multiple database fetches in case the filtering criteria changes.
   The filtering can be set or unset by the user.
   
   
   -----------------------
        JoinRowSet
   -----------------------
   
   This is a sample to demonstrate the functionality of a JoinRowSet.
   This example shows how to perform an in memory join of two already
   fetched rowsets. The user can set the join columns and just add the
   pre fetched rowsets to a JoinRowSet which performs the join.
            
      This is very useful in cases where data is present in two 
   different datasources and need to be joined to form a relation.
   
   
   ------------------------
        JdbcRowSet
   ------------------------
   
   This is sample to demonstrate the functionality of a JdbcRowSet.
   This is like a ResultSet but behaves like a Java Bean component
   providing the getters and setters allowing it to be used in 
   cases where Java Beans functionality is required.
   
      The example demonstartes the operations that can be performed
   with this component which is simlar to a ResultSet.
   
   Project RAVE is using this as a Java Beans component in their tool.
   
   
2. How to Run the Samples

   Make sure that your database is started,whatever database you are using.
   
   Ensure that you are in the directory corresponding to each sample.
   % ant
      Just run the ant command the default target gets invoked, the samples are then 
   compiled and run.
   
      The program takes 4 parameters, they are:
      i) The URL of the database to which we have to connect.
     ii) The username for connecting to this database.
    iii) The password for the above username
    iV ) The name of the class that implements the Driver interface.
    
   Make sure that the driver jar file is present in the lib directory.
   
   Currently the samplesare run against PointBase DB. If you want to run against other
   databases configure the build.xml accordingly to provide the correct parameters for
   the samples to run properly.
      
3. More information

(c) Sun Microsystems Inc. 2004