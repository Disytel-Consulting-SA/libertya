JdbcRowSet - Sample

Contents

1. Overview of Sample
   
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
   
2. How to Run the Sample

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
   