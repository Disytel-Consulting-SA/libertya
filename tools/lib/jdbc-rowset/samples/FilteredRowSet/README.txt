FilteredRowSet - Sample

Contents

1. Overview of Sample

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
   
   