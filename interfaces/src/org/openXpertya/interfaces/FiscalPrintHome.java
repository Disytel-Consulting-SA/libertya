/*
 * Generated file - Do not edit!
 */
package org.openXpertya.interfaces;

import java.lang.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.PO;
import org.openXpertya.model.RemoteFiscalDocumentPrint;
import org.openXpertya.print.fiscal.document.CreditNote;
import org.openXpertya.print.fiscal.document.DebitNote;
import org.openXpertya.print.fiscal.document.Document;
import org.openXpertya.print.fiscal.document.Invoice;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;

/**
 * Home interface for openXpertya/FiscalPrint. Lookup using {1}
 * @author XDOCLET 1.1.2 * @xdoclet-generated at 16-ago-2024 13:04:36
 */
public interface FiscalPrintHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/openXpertya/FiscalPrint";
   public static final String JNDI_NAME="ejb/openXpertya/FiscalPrint";

   public org.openXpertya.interfaces.FiscalPrint create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
