/**
 * ClsFEXResponse_Inc.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_Inc  implements java.io.Serializable {
    private java.lang.String inc_Id;

    private java.lang.String inc_Ds;

    private java.lang.String inc_vig_desde;

    private java.lang.String inc_vig_hasta;

    public ClsFEXResponse_Inc() {
    }

    public ClsFEXResponse_Inc(
           java.lang.String inc_Id,
           java.lang.String inc_Ds,
           java.lang.String inc_vig_desde,
           java.lang.String inc_vig_hasta) {
           this.inc_Id = inc_Id;
           this.inc_Ds = inc_Ds;
           this.inc_vig_desde = inc_vig_desde;
           this.inc_vig_hasta = inc_vig_hasta;
    }


    /**
     * Gets the inc_Id value for this ClsFEXResponse_Inc.
     * 
     * @return inc_Id
     */
    public java.lang.String getInc_Id() {
        return inc_Id;
    }


    /**
     * Sets the inc_Id value for this ClsFEXResponse_Inc.
     * 
     * @param inc_Id
     */
    public void setInc_Id(java.lang.String inc_Id) {
        this.inc_Id = inc_Id;
    }


    /**
     * Gets the inc_Ds value for this ClsFEXResponse_Inc.
     * 
     * @return inc_Ds
     */
    public java.lang.String getInc_Ds() {
        return inc_Ds;
    }


    /**
     * Sets the inc_Ds value for this ClsFEXResponse_Inc.
     * 
     * @param inc_Ds
     */
    public void setInc_Ds(java.lang.String inc_Ds) {
        this.inc_Ds = inc_Ds;
    }


    /**
     * Gets the inc_vig_desde value for this ClsFEXResponse_Inc.
     * 
     * @return inc_vig_desde
     */
    public java.lang.String getInc_vig_desde() {
        return inc_vig_desde;
    }


    /**
     * Sets the inc_vig_desde value for this ClsFEXResponse_Inc.
     * 
     * @param inc_vig_desde
     */
    public void setInc_vig_desde(java.lang.String inc_vig_desde) {
        this.inc_vig_desde = inc_vig_desde;
    }


    /**
     * Gets the inc_vig_hasta value for this ClsFEXResponse_Inc.
     * 
     * @return inc_vig_hasta
     */
    public java.lang.String getInc_vig_hasta() {
        return inc_vig_hasta;
    }


    /**
     * Sets the inc_vig_hasta value for this ClsFEXResponse_Inc.
     * 
     * @param inc_vig_hasta
     */
    public void setInc_vig_hasta(java.lang.String inc_vig_hasta) {
        this.inc_vig_hasta = inc_vig_hasta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_Inc)) return false;
        ClsFEXResponse_Inc other = (ClsFEXResponse_Inc) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.inc_Id==null && other.getInc_Id()==null) || 
             (this.inc_Id!=null &&
              this.inc_Id.equals(other.getInc_Id()))) &&
            ((this.inc_Ds==null && other.getInc_Ds()==null) || 
             (this.inc_Ds!=null &&
              this.inc_Ds.equals(other.getInc_Ds()))) &&
            ((this.inc_vig_desde==null && other.getInc_vig_desde()==null) || 
             (this.inc_vig_desde!=null &&
              this.inc_vig_desde.equals(other.getInc_vig_desde()))) &&
            ((this.inc_vig_hasta==null && other.getInc_vig_hasta()==null) || 
             (this.inc_vig_hasta!=null &&
              this.inc_vig_hasta.equals(other.getInc_vig_hasta())));
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
        if (getInc_Id() != null) {
            _hashCode += getInc_Id().hashCode();
        }
        if (getInc_Ds() != null) {
            _hashCode += getInc_Ds().hashCode();
        }
        if (getInc_vig_desde() != null) {
            _hashCode += getInc_vig_desde().hashCode();
        }
        if (getInc_vig_hasta() != null) {
            _hashCode += getInc_vig_hasta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_Inc.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Inc"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inc_Id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Inc_Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inc_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Inc_Ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inc_vig_desde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Inc_vig_desde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inc_vig_hasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Inc_vig_hasta"));
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
