/*
 * Generated file - Do not edit!
 */
package org.openXpertya.interfaces;

import java.lang.*;
import java.rmi.RemoteException;
import java.util.logging.Level;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import org.openXpertya.OpenXpertya;
import org.openXpertya.db.CConnection;
import org.openXpertya.util.CLogger;

/**
 * Remote interface for openXpertya/Status.
 * @version XDOCLET 1.1.2
 * @author XDOCLET 1.1.2 * @xdoclet-generated at 16-ago-2024 13:04:36
 */
public interface Status
   extends javax.ejb.EJBObject
{
   /**
    * Get Database URL
    * @return Database URL
    * @throws RemoteException    */
   public java.lang.String getConnectionURL(  ) throws java.rmi.RemoteException;

   /**
    * Get Database Count
    * @return number of database inquiries    */
   public int getDatabaseCount(  ) throws java.rmi.RemoteException;

   /**
    * Get Version (Date)
    * @return version e.g. 2002-09-02
    * @throws RemoteException    */
   public java.lang.String getDateVersion(  ) throws java.rmi.RemoteException;

   /**
    * Get Database Host
    * @return Database Host Name
    * @throws RemoteException    */
   public java.lang.String getDbHost(  ) throws java.rmi.RemoteException;

   /**
    * Get Database SID
    * @return Database SID
    * @throws RemoteException    */
   public java.lang.String getDbName(  ) throws java.rmi.RemoteException;

   /**
    * Get Database Port
    * @return Database Posrt
    * @throws RemoteException    */
   public int getDbPort(  ) throws java.rmi.RemoteException;

   /**
    * Get Database PWD
    * @return Database User Password
    * @throws RemoteException    */
   public java.lang.String getDbPwd(  ) throws java.rmi.RemoteException;

   /**
    * Get Database Type
    * @return Database Type
    * @throws RemoteException    */
   public java.lang.String getDbType(  ) throws java.rmi.RemoteException;

   /**
    * Get Database UID
    * @return Database User Name
    * @throws RemoteException    */
   public java.lang.String getDbUid(  ) throws java.rmi.RemoteException;

   /**
    * Get Connection Manager Host
    * @return Connection Manager Host
    * @throws RemoteException    */
   public java.lang.String getFwHost(  ) throws java.rmi.RemoteException;

   /**
    * Get Connection Manager Port
    * @return Connection Manager Port
    * @throws RemoteException    */
   public int getFwPort(  ) throws java.rmi.RemoteException;

   /**
    * Get Main Version
    * @return main version - e.g. Version 2.4.3b
    * @throws RemoteException    */
   public java.lang.String getMainVersion(  ) throws java.rmi.RemoteException;

   /**
    * Describes the instance and its content for debugging purpose
    * @return Debugging information about the instance and its content    */
   public java.lang.String getStatus(  ) throws java.rmi.RemoteException;

   /**
    * Get Version Count
    * @return number of version inquiries    */
   public int getVersionCount(  ) throws java.rmi.RemoteException;

}
