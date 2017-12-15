/**
 * ClsFEXResponse_Idi.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_Idi  implements java.io.Serializable {
    private short idi_Id;

    private java.lang.String idi_Ds;

    private java.lang.String idi_vig_desde;

    private java.lang.String idi_vig_hasta;

    public ClsFEXResponse_Idi() {
    }

    public ClsFEXResponse_Idi(
           short idi_Id,
           java.lang.String idi_Ds,
           java.lang.String idi_vig_desde,
           java.lang.String idi_vig_hasta) {
           this.idi_Id = idi_Id;
           this.idi_Ds = idi_Ds;
           this.idi_vig_desde = idi_vig_desde;
           this.idi_vig_hasta = idi_vig_hasta;
    }


    /**
     * Gets the idi_Id value for this ClsFEXResponse_Idi.
     * 
     * @return idi_Id
     */
    public short getIdi_Id() {
        return idi_Id;
    }


    /**
     * Sets the idi_Id value for this ClsFEXResponse_Idi.
     * 
     * @param idi_Id
     */
    public void setIdi_Id(short idi_Id) {
        this.idi_Id = idi_Id;
    }


    /**
     * Gets the idi_Ds value for this ClsFEXResponse_Idi.
     * 
     * @return idi_Ds
     */
    public java.lang.String getIdi_Ds() {
        return idi_Ds;
    }


    /**
     * Sets the idi_Ds value for this ClsFEXResponse_Idi.
     * 
     * @param idi_Ds
     */
    public void setIdi_Ds(java.lang.String idi_Ds) {
        this.idi_Ds = idi_Ds;
    }


    /**
     * Gets the idi_vig_desde value for this ClsFEXResponse_Idi.
     * 
     * @return idi_vig_desde
     */
    public java.lang.String getIdi_vig_desde() {
        return idi_vig_desde;
    }


    /**
     * Sets the idi_vig_desde value for this ClsFEXResponse_Idi.
     * 
     * @param idi_vig_desde
     */
    public void setIdi_vig_desde(java.lang.String idi_vig_desde) {
        this.idi_vig_desde = idi_vig_desde;
    }


    /**
     * Gets the idi_vig_hasta value for this ClsFEXResponse_Idi.
     * 
     * @return idi_vig_hasta
     */
    public java.lang.String getIdi_vig_hasta() {
        return idi_vig_hasta;
    }


    /**
     * Sets the idi_vig_hasta value for this ClsFEXResponse_Idi.
     * 
     * @param idi_vig_hasta
     */
    public void setIdi_vig_hasta(java.lang.String idi_vig_hasta) {
        this.idi_vig_hasta = idi_vig_hasta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_Idi)) return false;
        ClsFEXResponse_Idi other = (ClsFEXResponse_Idi) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.idi_Id == other.getIdi_Id() &&
            ((this.idi_Ds==null && other.getIdi_Ds()==null) || 
             (this.idi_Ds!=null &&
              this.idi_Ds.equals(other.getIdi_Ds()))) &&
            ((this.idi_vig_desde==null && other.getIdi_vig_desde()==null) || 
             (this.idi_vig_desde!=null &&
              this.idi_vig_desde.equals(other.getIdi_vig_desde()))) &&
            ((this.idi_vig_hasta==null && other.getIdi_vig_hasta()==null) || 
             (this.idi_vig_hasta!=null &&
              this.idi_vig_hasta.equals(other.getIdi_vig_hasta())));
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
        _hashCode += getIdi_Id();
        if (getIdi_Ds() != null) {
            _hashCode += getIdi_Ds().hashCode();
        }
        if (getIdi_vig_desde() != null) {
            _hashCode += getIdi_vig_desde().hashCode();
        }
        if (getIdi_vig_hasta() != null) {
            _hashCode += getIdi_vig_hasta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_Idi.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Idi"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idi_Id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Idi_Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idi_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Idi_Ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idi_vig_desde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Idi_vig_desde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idi_vig_hasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Idi_vig_hasta"));
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
