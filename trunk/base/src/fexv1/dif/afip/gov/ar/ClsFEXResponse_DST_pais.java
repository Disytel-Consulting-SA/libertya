/**
 * ClsFEXResponse_DST_pais.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_DST_pais  implements java.io.Serializable {
    private java.lang.String DST_Codigo;

    private java.lang.String DST_Ds;

    public ClsFEXResponse_DST_pais() {
    }

    public ClsFEXResponse_DST_pais(
           java.lang.String DST_Codigo,
           java.lang.String DST_Ds) {
           this.DST_Codigo = DST_Codigo;
           this.DST_Ds = DST_Ds;
    }


    /**
     * Gets the DST_Codigo value for this ClsFEXResponse_DST_pais.
     * 
     * @return DST_Codigo
     */
    public java.lang.String getDST_Codigo() {
        return DST_Codigo;
    }


    /**
     * Sets the DST_Codigo value for this ClsFEXResponse_DST_pais.
     * 
     * @param DST_Codigo
     */
    public void setDST_Codigo(java.lang.String DST_Codigo) {
        this.DST_Codigo = DST_Codigo;
    }


    /**
     * Gets the DST_Ds value for this ClsFEXResponse_DST_pais.
     * 
     * @return DST_Ds
     */
    public java.lang.String getDST_Ds() {
        return DST_Ds;
    }


    /**
     * Sets the DST_Ds value for this ClsFEXResponse_DST_pais.
     * 
     * @param DST_Ds
     */
    public void setDST_Ds(java.lang.String DST_Ds) {
        this.DST_Ds = DST_Ds;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_DST_pais)) return false;
        ClsFEXResponse_DST_pais other = (ClsFEXResponse_DST_pais) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.DST_Codigo==null && other.getDST_Codigo()==null) || 
             (this.DST_Codigo!=null &&
              this.DST_Codigo.equals(other.getDST_Codigo()))) &&
            ((this.DST_Ds==null && other.getDST_Ds()==null) || 
             (this.DST_Ds!=null &&
              this.DST_Ds.equals(other.getDST_Ds())));
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
        if (getDST_Codigo() != null) {
            _hashCode += getDST_Codigo().hashCode();
        }
        if (getDST_Ds() != null) {
            _hashCode += getDST_Ds().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_DST_pais.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_DST_pais"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DST_Codigo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "DST_Codigo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DST_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "DST_Ds"));
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
