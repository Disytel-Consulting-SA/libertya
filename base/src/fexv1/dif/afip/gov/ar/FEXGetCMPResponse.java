/**
 * FEXGetCMPResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class FEXGetCMPResponse  implements java.io.Serializable {
    private fexv1.dif.afip.gov.ar.ClsFEXGetCMPR FEXResultGet;

    private fexv1.dif.afip.gov.ar.ClsFEXErr FEXErr;

    private fexv1.dif.afip.gov.ar.ClsFEXEvents FEXEvents;

    public FEXGetCMPResponse() {
    }

    public FEXGetCMPResponse(
           fexv1.dif.afip.gov.ar.ClsFEXGetCMPR FEXResultGet,
           fexv1.dif.afip.gov.ar.ClsFEXErr FEXErr,
           fexv1.dif.afip.gov.ar.ClsFEXEvents FEXEvents) {
           this.FEXResultGet = FEXResultGet;
           this.FEXErr = FEXErr;
           this.FEXEvents = FEXEvents;
    }


    /**
     * Gets the FEXResultGet value for this FEXGetCMPResponse.
     * 
     * @return FEXResultGet
     */
    public fexv1.dif.afip.gov.ar.ClsFEXGetCMPR getFEXResultGet() {
        return FEXResultGet;
    }


    /**
     * Sets the FEXResultGet value for this FEXGetCMPResponse.
     * 
     * @param FEXResultGet
     */
    public void setFEXResultGet(fexv1.dif.afip.gov.ar.ClsFEXGetCMPR FEXResultGet) {
        this.FEXResultGet = FEXResultGet;
    }


    /**
     * Gets the FEXErr value for this FEXGetCMPResponse.
     * 
     * @return FEXErr
     */
    public fexv1.dif.afip.gov.ar.ClsFEXErr getFEXErr() {
        return FEXErr;
    }


    /**
     * Sets the FEXErr value for this FEXGetCMPResponse.
     * 
     * @param FEXErr
     */
    public void setFEXErr(fexv1.dif.afip.gov.ar.ClsFEXErr FEXErr) {
        this.FEXErr = FEXErr;
    }


    /**
     * Gets the FEXEvents value for this FEXGetCMPResponse.
     * 
     * @return FEXEvents
     */
    public fexv1.dif.afip.gov.ar.ClsFEXEvents getFEXEvents() {
        return FEXEvents;
    }


    /**
     * Sets the FEXEvents value for this FEXGetCMPResponse.
     * 
     * @param FEXEvents
     */
    public void setFEXEvents(fexv1.dif.afip.gov.ar.ClsFEXEvents FEXEvents) {
        this.FEXEvents = FEXEvents;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FEXGetCMPResponse)) return false;
        FEXGetCMPResponse other = (FEXGetCMPResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.FEXResultGet==null && other.getFEXResultGet()==null) || 
             (this.FEXResultGet!=null &&
              this.FEXResultGet.equals(other.getFEXResultGet()))) &&
            ((this.FEXErr==null && other.getFEXErr()==null) || 
             (this.FEXErr!=null &&
              this.FEXErr.equals(other.getFEXErr()))) &&
            ((this.FEXEvents==null && other.getFEXEvents()==null) || 
             (this.FEXEvents!=null &&
              this.FEXEvents.equals(other.getFEXEvents())));
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
        if (getFEXResultGet() != null) {
            _hashCode += getFEXResultGet().hashCode();
        }
        if (getFEXErr() != null) {
            _hashCode += getFEXErr().hashCode();
        }
        if (getFEXEvents() != null) {
            _hashCode += getFEXEvents().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FEXGetCMPResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetCMPResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FEXResultGet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResultGet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXGetCMPR"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FEXErr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXErr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXErr"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FEXEvents");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXEvents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXEvents"));
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
