/**
 * Link_name_value.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Link_name_value  implements java.io.Serializable {
    private java.lang.String name;

    private com.sugarcrm.www.sugarcrm.Link_value2[] records;

    public Link_name_value() {
    }

    public Link_name_value(
           java.lang.String name,
           com.sugarcrm.www.sugarcrm.Link_value2[] records) {
           this.name = name;
           this.records = records;
    }


    /**
     * Gets the name value for this Link_name_value.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this Link_name_value.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the records value for this Link_name_value.
     * 
     * @return records
     */
    public com.sugarcrm.www.sugarcrm.Link_value2[] getRecords() {
        return records;
    }


    /**
     * Sets the records value for this Link_name_value.
     * 
     * @param records
     */
    public void setRecords(com.sugarcrm.www.sugarcrm.Link_value2[] records) {
        this.records = records;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Link_name_value)) return false;
        Link_name_value other = (Link_name_value) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.records==null && other.getRecords()==null) || 
             (this.records!=null &&
              java.util.Arrays.equals(this.records, other.getRecords())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getRecords() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRecords());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRecords(), i);
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
        new org.apache.axis.description.TypeDesc(Link_name_value.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_name_value"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("records");
        elemField.setXmlName(new javax.xml.namespace.QName("", "records"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_value2"));
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
