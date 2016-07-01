/**
 * Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public interface Service extends javax.xml.rpc.Service {

/**
 * Web Service orientado  al  servicio  de Facturacion electronica
 * RG2485 V1
 */
    public java.lang.String getServiceSoapAddress();

    public FEV1.dif.afip.gov.ar.ServiceSoap getServiceSoap() throws javax.xml.rpc.ServiceException;

    public FEV1.dif.afip.gov.ar.ServiceSoap getServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getServiceSoap12Address();

    public FEV1.dif.afip.gov.ar.ServiceSoap getServiceSoap12() throws javax.xml.rpc.ServiceException;

    public FEV1.dif.afip.gov.ar.ServiceSoap getServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
