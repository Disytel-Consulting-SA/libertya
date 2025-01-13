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
 * Remote interface for openXpertya/FiscalPrint.
 * @author XDOCLET 1.1.2 * @xdoclet-generated at 16-ago-2024 13:04:36
 */
public interface FiscalPrint
   extends javax.ejb.EJBObject
{
   /**
    * Imprime una factura sobre una impresora fiscal
    * @param ctx
            contexto
    * @param invoice
            factura oxp
    * @param document
            factura imprimible por una impresora fiscal, creada a partir
            de la factura oxp parámetro
    * @param originalInvoice
            factura original obtenida de la factura oxp parámetro, usada
            para la impresión de notas de crédito
    * @param docTypeColumnNameUID
            nombre de la columna de clave única del tipo de documento de
            la factura oxp parámetro
    * @param docTypeColumnValueUID
            valor de la columna de clave única del tipo de documento de la
            factura oxp parámetro
    * @return resultado de la llamada, si el resultado está seteado como error
         significa que no se pudo imprimir correctamente y dentro de ésta
         se encuentra el mensaje de error.
    * @throws RemoteException
    */
   public org.openXpertya.reflection.CallResult printDocument( java.util.Properties ctx,org.openXpertya.model.PO invoice,org.openXpertya.print.fiscal.document.Document document,org.openXpertya.model.MInvoice originalInvoice,java.lang.String docTypeColumnNameUID,java.lang.Object docTypeColumnValueUID ) throws java.rmi.RemoteException;

}
