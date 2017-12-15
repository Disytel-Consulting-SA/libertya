/**
 * ClsFEXResponse_Opc.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_Opc  implements java.io.Serializable {
    private short opc_Id;

    private java.lang.String opc_Ds;

    private java.lang.String opc_vig_desde;

    private java.lang.String opc_vig_hasta;

    public ClsFEXResponse_Opc() {
    }

    public ClsFEXResponse_Opc(
           short opc_Id,
           java.lang.String opc_Ds,
           java.lang.String opc_vig_desde,
           java.lang.String opc_vig_hasta) {
           this.opc_Id = opc_Id;
           this.opc_Ds = opc_Ds;
           this.opc_vig_desde = opc_vig_desde;
           this.opc_vig_hasta = opc_vig_hasta;
    }


    /**
     * Gets the opc_Id value for this ClsFEXResponse_Opc.
     * 
     * @return opc_Id
     */
    public short getOpc_Id() {
        return opc_Id;
    }


    /**
     * Sets the opc_Id value for this ClsFEXResponse_Opc.
     * 
     * @param opc_Id
     */
    public void setOpc_Id(short opc_Id) {
        this.opc_Id = opc_Id;
    }


    /**
     * Gets the opc_Ds value for this ClsFEXResponse_Opc.
     * 
     * @return opc_Ds
     */
    public java.lang.String getOpc_Ds() {
        return opc_Ds;
    }


    /**
     * Sets the opc_Ds value for this ClsFEXResponse_Opc.
     * 
     * @param opc_Ds
     */
    public void setOpc_Ds(java.lang.String opc_Ds) {
        this.opc_Ds = opc_Ds;
    }


    /**
     * Gets the opc_vig_desde value for this ClsFEXResponse_Opc.
     * 
     * @return opc_vig_desde
     */
    public java.lang.String getOpc_vig_desde() {
        return opc_vig_desde;
    }


    /**
     * Sets the opc_vig_desde value for this ClsFEXResponse_Opc.
     * 
     * @param opc_vig_desde
     */
    public void setOpc_vig_desde(java.lang.String opc_vig_desde) {
        this.opc_vig_desde = opc_vig_desde;
    }


    /**
     * Gets the opc_vig_hasta value for this ClsFEXResponse_Opc.
     * 
     * @return opc_vig_hasta
     */
    public java.lang.String getOpc_vig_hasta() {
        return opc_vig_hasta;
    }


    /**
     * Sets the opc_vig_hasta value for this ClsFEXResponse_Opc.
     * 
     * @param opc_vig_hasta
     */
    public void setOpc_vig_hasta(java.lang.String opc_vig_hasta) {
        this.opc_vig_hasta = opc_vig_hasta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_Opc)) return false;
        ClsFEXResponse_Opc other = (ClsFEXResponse_Opc) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.opc_Id == other.getOpc_Id() &&
            ((this.opc_Ds==null && other.getOpc_Ds()==null) || 
             (this.opc_Ds!=null &&
              this.opc_Ds.equals(other.getOpc_Ds()))) &&
            ((this.opc_vig_desde==null && other.getOpc_vig_desde()==null) || 
             (this.opc_vig_desde!=null &&
              this.opc_vig_desde.equals(other.getOpc_vig_desde()))) &&
            ((this.opc_vig_hasta==null && other.getOpc_vig_hasta()==null) || 
             (this.opc_vig_hasta!=null &&
              this.opc_vig_hasta.equals(other.getOpc_vig_hasta())));
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
        _hashCode += getOpc_Id();
        if (getOpc_Ds() != null) {
            _hashCode += getOpc_Ds().hashCode();
        }
        if (getOpc_vig_desde() != null) {
            _hashCode += getOpc_vig_desde().hashCode();
        }
        if (getOpc_vig_hasta() != null) {
            _hashCode += getOpc_vig_hasta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_Opc.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Opc"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("opc_Id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opc_Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("opc_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opc_Ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("opc_vig_desde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opc_vig_desde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("opc_vig_hasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opc_vig_hasta"));
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
