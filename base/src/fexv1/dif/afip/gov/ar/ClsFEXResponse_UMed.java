/**
 * ClsFEXResponse_UMed.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_UMed  implements java.io.Serializable {
    private short umed_Id;

    private java.lang.String umed_Ds;

    private java.lang.String umed_vig_desde;

    private java.lang.String umed_vig_hasta;

    public ClsFEXResponse_UMed() {
    }

    public ClsFEXResponse_UMed(
           short umed_Id,
           java.lang.String umed_Ds,
           java.lang.String umed_vig_desde,
           java.lang.String umed_vig_hasta) {
           this.umed_Id = umed_Id;
           this.umed_Ds = umed_Ds;
           this.umed_vig_desde = umed_vig_desde;
           this.umed_vig_hasta = umed_vig_hasta;
    }


    /**
     * Gets the umed_Id value for this ClsFEXResponse_UMed.
     * 
     * @return umed_Id
     */
    public short getUmed_Id() {
        return umed_Id;
    }


    /**
     * Sets the umed_Id value for this ClsFEXResponse_UMed.
     * 
     * @param umed_Id
     */
    public void setUmed_Id(short umed_Id) {
        this.umed_Id = umed_Id;
    }


    /**
     * Gets the umed_Ds value for this ClsFEXResponse_UMed.
     * 
     * @return umed_Ds
     */
    public java.lang.String getUmed_Ds() {
        return umed_Ds;
    }


    /**
     * Sets the umed_Ds value for this ClsFEXResponse_UMed.
     * 
     * @param umed_Ds
     */
    public void setUmed_Ds(java.lang.String umed_Ds) {
        this.umed_Ds = umed_Ds;
    }


    /**
     * Gets the umed_vig_desde value for this ClsFEXResponse_UMed.
     * 
     * @return umed_vig_desde
     */
    public java.lang.String getUmed_vig_desde() {
        return umed_vig_desde;
    }


    /**
     * Sets the umed_vig_desde value for this ClsFEXResponse_UMed.
     * 
     * @param umed_vig_desde
     */
    public void setUmed_vig_desde(java.lang.String umed_vig_desde) {
        this.umed_vig_desde = umed_vig_desde;
    }


    /**
     * Gets the umed_vig_hasta value for this ClsFEXResponse_UMed.
     * 
     * @return umed_vig_hasta
     */
    public java.lang.String getUmed_vig_hasta() {
        return umed_vig_hasta;
    }


    /**
     * Sets the umed_vig_hasta value for this ClsFEXResponse_UMed.
     * 
     * @param umed_vig_hasta
     */
    public void setUmed_vig_hasta(java.lang.String umed_vig_hasta) {
        this.umed_vig_hasta = umed_vig_hasta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_UMed)) return false;
        ClsFEXResponse_UMed other = (ClsFEXResponse_UMed) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.umed_Id == other.getUmed_Id() &&
            ((this.umed_Ds==null && other.getUmed_Ds()==null) || 
             (this.umed_Ds!=null &&
              this.umed_Ds.equals(other.getUmed_Ds()))) &&
            ((this.umed_vig_desde==null && other.getUmed_vig_desde()==null) || 
             (this.umed_vig_desde!=null &&
              this.umed_vig_desde.equals(other.getUmed_vig_desde()))) &&
            ((this.umed_vig_hasta==null && other.getUmed_vig_hasta()==null) || 
             (this.umed_vig_hasta!=null &&
              this.umed_vig_hasta.equals(other.getUmed_vig_hasta())));
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
        _hashCode += getUmed_Id();
        if (getUmed_Ds() != null) {
            _hashCode += getUmed_Ds().hashCode();
        }
        if (getUmed_vig_desde() != null) {
            _hashCode += getUmed_vig_desde().hashCode();
        }
        if (getUmed_vig_hasta() != null) {
            _hashCode += getUmed_vig_hasta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_UMed.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_UMed"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("umed_Id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Umed_Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("umed_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Umed_Ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("umed_vig_desde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Umed_vig_desde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("umed_vig_hasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Umed_vig_hasta"));
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
