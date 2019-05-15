/**
 * Get_server_info_result.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Get_server_info_result  implements java.io.Serializable {
    private java.lang.String flavor;

    private java.lang.String version;

    private java.lang.String gmt_time;

    public Get_server_info_result() {
    }

    public Get_server_info_result(
           java.lang.String flavor,
           java.lang.String version,
           java.lang.String gmt_time) {
           this.flavor = flavor;
           this.version = version;
           this.gmt_time = gmt_time;
    }


    /**
     * Gets the flavor value for this Get_server_info_result.
     * 
     * @return flavor
     */
    public java.lang.String getFlavor() {
        return flavor;
    }


    /**
     * Sets the flavor value for this Get_server_info_result.
     * 
     * @param flavor
     */
    public void setFlavor(java.lang.String flavor) {
        this.flavor = flavor;
    }


    /**
     * Gets the version value for this Get_server_info_result.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this Get_server_info_result.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }


    /**
     * Gets the gmt_time value for this Get_server_info_result.
     * 
     * @return gmt_time
     */
    public java.lang.String getGmt_time() {
        return gmt_time;
    }


    /**
     * Sets the gmt_time value for this Get_server_info_result.
     * 
     * @param gmt_time
     */
    public void setGmt_time(java.lang.String gmt_time) {
        this.gmt_time = gmt_time;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Get_server_info_result)) return false;
        Get_server_info_result other = (Get_server_info_result) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.flavor==null && other.getFlavor()==null) || 
             (this.flavor!=null &&
              this.flavor.equals(other.getFlavor()))) &&
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion()))) &&
            ((this.gmt_time==null && other.getGmt_time()==null) || 
             (this.gmt_time!=null &&
              this.gmt_time.equals(other.getGmt_time())));
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
        if (getFlavor() != null) {
            _hashCode += getFlavor().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        if (getGmt_time() != null) {
            _hashCode += getGmt_time().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Get_server_info_result.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_server_info_result"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flavor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "flavor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("", "version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gmt_time");
        elemField.setXmlName(new javax.xml.namespace.QName("", "gmt_time"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
