      **************************************************************************************************************** 
      						WebRowSet Demo
      ****************************************************************************************************************
      
      
   This is for the demonstration of the WebRowSet to be used for interoperability
 among two applications that understand XML, but the data is present in a 
 DataStore on one of the end.
 
    This has a build.xml file that builds the samples and takes as parameter the
 directory where this sample is installed. The build process first takes the
 data in the datastore on one oof the ends from where data needs to be sent
 and generates an XML file from the WebRowSet which is populated with data that 
 needs to be sent.
 
     For this step where the XML file is written from the WebRowSet the build
 step needs to be supplied with four arguments as said in the build.xml file.
 
     The build process will prodcue two "war " files that can be deployed in 
 a J2EE compatible Web Container and the sample run.