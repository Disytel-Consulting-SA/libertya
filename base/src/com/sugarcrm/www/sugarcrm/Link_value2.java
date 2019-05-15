/**
 * Link_value2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Link_value2  implements java.io.Serializable {
    private com.sugarcrm.www.sugarcrm.Name_value[] link_value;

    public Link_value2() {
    }

    public Link_value2(
           com.sugarcrm.www.sugarcrm.Name_value[] link_value) {
           this.link_value = link_value;
    }


    /**
     * Gets the link_value value for this Link_value2.
     * 
     * @return link_value
     */
    public com.sugarcrm.www.sugarcrm.Name_value[] getLink_value() {
        return link_value;
    }


    /**
     * Sets the link_value value for this Link_value2.
     * 
     * @param link_value
     */
    public void setLink_value(com.sugarcrm.www.sugarcrm.Name_value[] link_value) {
        this.link_value = link_value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Link_value2)) return false;
        Link_value2 other = (Link_value2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.link_value==null && other.getLink_value()==null) || 
             (this.link_value!=null &&
              java.util.Arrays.equals(this.link_value, other.getLink_value())));
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
        if (getLink_value() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLink_value());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLink_value(), i);
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
        new org.apache.axis.description.TypeDesc(Link_value2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_value2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("link_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "link_value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value"));
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
