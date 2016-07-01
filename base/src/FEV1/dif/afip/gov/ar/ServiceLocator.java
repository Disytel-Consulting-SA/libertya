/**
 * ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class ServiceLocator extends org.apache.axis.client.Service implements FEV1.dif.afip.gov.ar.Service {

/**
 * Web Service orientado  al  servicio  de Facturacion electronica
 * RG2485 V1
 */

    public ServiceLocator() {
    }


    public ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ServiceSoap
    private java.lang.String ServiceSoap_address = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";

    public java.lang.String getServiceSoapAddress() {
        return ServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ServiceSoapWSDDServiceName = "ServiceSoap";

    public java.lang.String getServiceSoapWSDDServiceName() {
        return ServiceSoapWSDDServiceName;
    }

    public void setServiceSoapWSDDServiceName(java.lang.String name) {
        ServiceSoapWSDDServiceName = name;
    }

    public FEV1.dif.afip.gov.ar.ServiceSoap getServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getServiceSoap(endpoint);
    }

    public FEV1.dif.afip.gov.ar.ServiceSoap getServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            FEV1.dif.afip.gov.ar.ServiceSoapStub _stub = new FEV1.dif.afip.gov.ar.ServiceSoapStub(portAddress, this);
            _stub.setPortName(getServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setServiceSoapEndpointAddress(java.lang.String address) {
        ServiceSoap_address = address;
    }


    // Use to get a proxy class for ServiceSoap12
    private java.lang.String ServiceSoap12_address = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";

    public java.lang.String getServiceSoap12Address() {
        return ServiceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ServiceSoap12WSDDServiceName = "ServiceSoap12";

    public java.lang.String getServiceSoap12WSDDServiceName() {
        return ServiceSoap12WSDDServiceName;
    }

    public void setServiceSoap12WSDDServiceName(java.lang.String name) {
        ServiceSoap12WSDDServiceName = name;
    }

    public FEV1.dif.afip.gov.ar.ServiceSoap getServiceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ServiceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getServiceSoap12(endpoint);
    }

    public FEV1.dif.afip.gov.ar.ServiceSoap getServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            FEV1.dif.afip.gov.ar.ServiceSoap12Stub _stub = new FEV1.dif.afip.gov.ar.ServiceSoap12Stub(portAddress, this);
            _stub.setPortName(getServiceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setServiceSoap12EndpointAddress(java.lang.String address) {
        ServiceSoap12_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (FEV1.dif.afip.gov.ar.ServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                FEV1.dif.afip.gov.ar.ServiceSoapStub _stub = new FEV1.dif.afip.gov.ar.ServiceSoapStub(new java.net.URL(ServiceSoap_address), this);
                _stub.setPortName(getServiceSoapWSDDServiceName());
                return _stub;
            }
            if (FEV1.dif.afip.gov.ar.ServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                FEV1.dif.afip.gov.ar.ServiceSoap12Stub _stub = new FEV1.dif.afip.gov.ar.ServiceSoap12Stub(new java.net.URL(ServiceSoap12_address), this);
                _stub.setPortName(getServiceSoap12WSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ServiceSoap".equals(inputPortName)) {
            return getServiceSoap();
        }
        else if ("ServiceSoap12".equals(inputPortName)) {
            return getServiceSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ServiceSoap"));
            ports.add(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ServiceSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ServiceSoap".equals(portName)) {
            setServiceSoapEndpointAddress(address);
        }
        else 
if ("ServiceSoap12".equals(portName)) {
            setServiceSoap12EndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
