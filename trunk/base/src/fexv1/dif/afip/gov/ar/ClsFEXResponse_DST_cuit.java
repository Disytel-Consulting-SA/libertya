/**
 * ClsFEXResponse_DST_cuit.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_DST_cuit  implements java.io.Serializable {
    private long DST_CUIT;

    private java.lang.String DST_Ds;

    public ClsFEXResponse_DST_cuit() {
    }

    public ClsFEXResponse_DST_cuit(
           long DST_CUIT,
           java.lang.String DST_Ds) {
           this.DST_CUIT = DST_CUIT;
           this.DST_Ds = DST_Ds;
    }


    /**
     * Gets the DST_CUIT value for this ClsFEXResponse_DST_cuit.
     * 
     * @return DST_CUIT
     */
    public long getDST_CUIT() {
        return DST_CUIT;
    }


    /**
     * Sets the DST_CUIT value for this ClsFEXResponse_DST_cuit.
     * 
     * @param DST_CUIT
     */
    public void setDST_CUIT(long DST_CUIT) {
        this.DST_CUIT = DST_CUIT;
    }


    /**
     * Gets the DST_Ds value for this ClsFEXResponse_DST_cuit.
     * 
     * @return DST_Ds
     */
    public java.lang.String getDST_Ds() {
        return DST_Ds;
    }


    /**
     * Sets the DST_Ds value for this ClsFEXResponse_DST_cuit.
     * 
     * @param DST_Ds
     */
    public void setDST_Ds(java.lang.String DST_Ds) {
        this.DST_Ds = DST_Ds;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_DST_cuit)) return false;
        ClsFEXResponse_DST_cuit other = (ClsFEXResponse_DST_cuit) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.DST_CUIT == other.getDST_CUIT() &&
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
        _hashCode += new Long(getDST_CUIT()).hashCode();
        if (getDST_Ds() != null) {
            _hashCode += getDST_Ds().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_DST_cuit.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_DST_cuit"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DST_CUIT");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "DST_CUIT"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
