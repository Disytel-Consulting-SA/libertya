/**
 * ClsFEXResponse_Cbte_Tipo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_Cbte_Tipo  implements java.io.Serializable {
    private short cbte_Id;

    private java.lang.String cbte_Ds;

    private java.lang.String cbte_vig_desde;

    private java.lang.String cbte_vig_hasta;

    public ClsFEXResponse_Cbte_Tipo() {
    }

    public ClsFEXResponse_Cbte_Tipo(
           short cbte_Id,
           java.lang.String cbte_Ds,
           java.lang.String cbte_vig_desde,
           java.lang.String cbte_vig_hasta) {
           this.cbte_Id = cbte_Id;
           this.cbte_Ds = cbte_Ds;
           this.cbte_vig_desde = cbte_vig_desde;
           this.cbte_vig_hasta = cbte_vig_hasta;
    }


    /**
     * Gets the cbte_Id value for this ClsFEXResponse_Cbte_Tipo.
     * 
     * @return cbte_Id
     */
    public short getCbte_Id() {
        return cbte_Id;
    }


    /**
     * Sets the cbte_Id value for this ClsFEXResponse_Cbte_Tipo.
     * 
     * @param cbte_Id
     */
    public void setCbte_Id(short cbte_Id) {
        this.cbte_Id = cbte_Id;
    }


    /**
     * Gets the cbte_Ds value for this ClsFEXResponse_Cbte_Tipo.
     * 
     * @return cbte_Ds
     */
    public java.lang.String getCbte_Ds() {
        return cbte_Ds;
    }


    /**
     * Sets the cbte_Ds value for this ClsFEXResponse_Cbte_Tipo.
     * 
     * @param cbte_Ds
     */
    public void setCbte_Ds(java.lang.String cbte_Ds) {
        this.cbte_Ds = cbte_Ds;
    }


    /**
     * Gets the cbte_vig_desde value for this ClsFEXResponse_Cbte_Tipo.
     * 
     * @return cbte_vig_desde
     */
    public java.lang.String getCbte_vig_desde() {
        return cbte_vig_desde;
    }


    /**
     * Sets the cbte_vig_desde value for this ClsFEXResponse_Cbte_Tipo.
     * 
     * @param cbte_vig_desde
     */
    public void setCbte_vig_desde(java.lang.String cbte_vig_desde) {
        this.cbte_vig_desde = cbte_vig_desde;
    }


    /**
     * Gets the cbte_vig_hasta value for this ClsFEXResponse_Cbte_Tipo.
     * 
     * @return cbte_vig_hasta
     */
    public java.lang.String getCbte_vig_hasta() {
        return cbte_vig_hasta;
    }


    /**
     * Sets the cbte_vig_hasta value for this ClsFEXResponse_Cbte_Tipo.
     * 
     * @param cbte_vig_hasta
     */
    public void setCbte_vig_hasta(java.lang.String cbte_vig_hasta) {
        this.cbte_vig_hasta = cbte_vig_hasta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_Cbte_Tipo)) return false;
        ClsFEXResponse_Cbte_Tipo other = (ClsFEXResponse_Cbte_Tipo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.cbte_Id == other.getCbte_Id() &&
            ((this.cbte_Ds==null && other.getCbte_Ds()==null) || 
             (this.cbte_Ds!=null &&
              this.cbte_Ds.equals(other.getCbte_Ds()))) &&
            ((this.cbte_vig_desde==null && other.getCbte_vig_desde()==null) || 
             (this.cbte_vig_desde!=null &&
              this.cbte_vig_desde.equals(other.getCbte_vig_desde()))) &&
            ((this.cbte_vig_hasta==null && other.getCbte_vig_hasta()==null) || 
             (this.cbte_vig_hasta!=null &&
              this.cbte_vig_hasta.equals(other.getCbte_vig_hasta())));
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
        _hashCode += getCbte_Id();
        if (getCbte_Ds() != null) {
            _hashCode += getCbte_Ds().hashCode();
        }
        if (getCbte_vig_desde() != null) {
            _hashCode += getCbte_vig_desde().hashCode();
        }
        if (getCbte_vig_hasta() != null) {
            _hashCode += getCbte_vig_hasta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_Cbte_Tipo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Cbte_Tipo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbte_Id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cbte_Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbte_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cbte_Ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbte_vig_desde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cbte_vig_desde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbte_vig_hasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cbte_vig_hasta"));
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
