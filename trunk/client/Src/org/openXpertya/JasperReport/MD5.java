package org.openXpertya.JasperReport;

/*
 * Generated file - Do not edit!
 */

// import javax.ejb.Remote; <- pendiente!

/**
 * Remote interface for compiere/MD5.
 */
// @Remote  <- OJO!
public interface MD5
{
   public final static String JNDI_NAME = "ejb/compiere/MD5";
   
   public final static String EJB_NAME = "compiereMD5";
   
   /**
    * Business method
    * @param Filename
    * @return AbsolutePath on server    */
   public java.lang.String getFileAsolutePath( java.lang.String Filename );

   /**
    * Business method
    * @param FileName
    * @return hash base64 encoded    */
   public java.lang.String getFileMD5( java.lang.String FileName );

}
