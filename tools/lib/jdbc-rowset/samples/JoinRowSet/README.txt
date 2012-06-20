JoinRowSet - Sample

Contents

1. Overview of Samples
   
   -----------------------
        JoinRowSet
   -----------------------
   
   This is a sample to demonstrate the functionality of a JoinRowSet.
   This example shows how to perform an in memory join of two already
   fetched rowsets. The user can set the join columns and just add the
   pre fetched rowsets to a JoinRowSet which performs the join.
            
      This is very useful in cases where data is present in two 
   different datasources and need to be joined to form a relation.
   
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
   
   