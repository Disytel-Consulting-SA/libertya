CachedRowSet - Sample

Contents

1. Overview of Sample
   
   This is a sample for demonstrating the functionality of CachedRowSet.
   This example shows how to get the data into the memory,
   (i.e into the CachedRowSet) and also the operations that can be performed on the 
   data in the memory like updation of values, inserting new rows, deleting existing
   rows,etc.
      Finally when the user is done with all the operations it shows how to sync the
   data in memory back to the database.
   
2. How to Run the Sample

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