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
 * Home interface for openXpertya/Status. Lookup using {1}
 * @version XDOCLET 1.1.2
 * @author XDOCLET 1.1.2 * @xdoclet-generated at 16-ago-2024 13:04:36
 */
public interface StatusHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/openXpertya/Status";
   public static final String JNDI_NAME="ejb/openXpertya/Status";

   public org.openXpertya.interfaces.Status create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
