/**
 * DummyResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class DummyResponse  implements java.io.Serializable {
    private java.lang.String appServer;

    private java.lang.String dbServer;

    private java.lang.String authServer;

    public DummyResponse() {
    }

    public DummyResponse(
           java.lang.String appServer,
           java.lang.String dbServer,
           java.lang.String authServer) {
           this.appServer = appServer;
           this.dbServer = dbServer;
           this.authServer = authServer;
    }


    /**
     * Gets the appServer value for this DummyResponse.
     * 
     * @return appServer
     */
    public java.lang.String getAppServer() {
        return appServer;
    }


    /**
     * Sets the appServer value for this DummyResponse.
     * 
     * @param appServer
     */
    public void setAppServer(java.lang.String appServer) {
        this.appServer = appServer;
    }


    /**
     * Gets the dbServer value for this DummyResponse.
     * 
     * @return dbServer
     */
    public java.lang.String getDbServer() {
        return dbServer;
    }


    /**
     * Sets the dbServer value for this DummyResponse.
     * 
     * @param dbServer
     */
    public void setDbServer(java.lang.String dbServer) {
        this.dbServer = dbServer;
    }


    /**
     * Gets the authServer value for this DummyResponse.
     * 
     * @return authServer
     */
    public java.lang.String getAuthServer() {
        return authServer;
    }


    /**
     * Sets the authServer value for this DummyResponse.
     * 
     * @param authServer
     */
    public void setAuthServer(java.lang.String authServer) {
        this.authServer = authServer;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DummyResponse)) return false;
        DummyResponse other = (DummyResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.appServer==null && other.getAppServer()==null) || 
             (this.appServer!=null &&
              this.appServer.equals(other.getAppServer()))) &&
            ((this.dbServer==null && other.getDbServer()==null) || 
             (this.dbServer!=null &&
              this.dbServer.equals(other.getDbServer()))) &&
            ((this.authServer==null && other.getAuthServer()==null) || 
             (this.authServer!=null &&
              this.authServer.equals(other.getAuthServer())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAppServer() != null) {
            _hashCode += getAppServer().hashCode();
        }
        if (getDbServer() != null) {
            _hashCode += getDbServer().hashCode();
        }
        if (getAuthServer() != null) {
            _hashCode += getAuthServer().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DummyResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "DummyResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("appServer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "AppServer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dbServer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "DbServer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authServer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "AuthServer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
