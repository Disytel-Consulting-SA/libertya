/**
 * FEPtoVentaResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FEPtoVentaResponse  implements java.io.Serializable {
    private FEV1.dif.afip.gov.ar.PtoVenta[] resultGet;

    private FEV1.dif.afip.gov.ar.Err[] errors;

    private FEV1.dif.afip.gov.ar.Evt[] events;

    public FEPtoVentaResponse() {
    }

    public FEPtoVentaResponse(
           FEV1.dif.afip.gov.ar.PtoVenta[] resultGet,
           FEV1.dif.afip.gov.ar.Err[] errors,
           FEV1.dif.afip.gov.ar.Evt[] events) {
           this.resultGet = resultGet;
           this.errors = errors;
           this.events = events;
    }


    /**
     * Gets the resultGet value for this FEPtoVentaResponse.
     * 
     * @return resultGet
     */
    public FEV1.dif.afip.gov.ar.PtoVenta[] getResultGet() {
        return resultGet;
    }


    /**
     * Sets the resultGet value for this FEPtoVentaResponse.
     * 
     * @param resultGet
     */
    public void setResultGet(FEV1.dif.afip.gov.ar.PtoVenta[] resultGet) {
        this.resultGet = resultGet;
    }


    /**
     * Gets the errors value for this FEPtoVentaResponse.
     * 
     * @return errors
     */
    public FEV1.dif.afip.gov.ar.Err[] getErrors() {
        return errors;
    }


    /**
     * Sets the errors value for this FEPtoVentaResponse.
     * 
     * @param errors
     */
    public void setErrors(FEV1.dif.afip.gov.ar.Err[] errors) {
        this.errors = errors;
    }


    /**
     * Gets the events value for this FEPtoVentaResponse.
     * 
     * @return events
     */
    public FEV1.dif.afip.gov.ar.Evt[] getEvents() {
        return events;
    }


    /**
     * Sets the events value for this FEPtoVentaResponse.
     * 
     * @param events
     */
    public void setEvents(FEV1.dif.afip.gov.ar.Evt[] events) {
        this.events = events;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FEPtoVentaResponse)) return false;
        FEPtoVentaResponse other = (FEPtoVentaResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.resultGet==null && other.getResultGet()==null) || 
             (this.resultGet!=null &&
              java.util.Arrays.equals(this.resultGet, other.getResultGet()))) &&
            ((this.errors==null && other.getErrors()==null) || 
             (this.errors!=null &&
              java.util.Arrays.equals(this.errors, other.getErrors()))) &&
            ((this.events==null && other.getEvents()==null) || 
             (this.events!=null &&
              java.util.Arrays.equals(this.events, other.getEvents())));
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
        if (getResultGet() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResultGet());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResultGet(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getErrors() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getErrors());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getErrors(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEvents() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEvents());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEvents(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FEPtoVentaResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEPtoVentaResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultGet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ResultGet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVenta"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVenta"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errors");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Errors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Err"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Err"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("events");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Events"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Evt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Evt"));
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
