/**
 * Link_list2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Link_list2  implements java.io.Serializable {
    private com.sugarcrm.www.sugarcrm.Link_name_value[] link_list;

    public Link_list2() {
    }

    public Link_list2(
           com.sugarcrm.www.sugarcrm.Link_name_value[] link_list) {
           this.link_list = link_list;
    }


    /**
     * Gets the link_list value for this Link_list2.
     * 
     * @return link_list
     */
    public com.sugarcrm.www.sugarcrm.Link_name_value[] getLink_list() {
        return link_list;
    }


    /**
     * Sets the link_list value for this Link_list2.
     * 
     * @param link_list
     */
    public void setLink_list(com.sugarcrm.www.sugarcrm.Link_name_value[] link_list) {
        this.link_list = link_list;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Link_list2)) return false;
        Link_list2 other = (Link_list2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.link_list==null && other.getLink_list()==null) || 
             (this.link_list!=null &&
              java.util.Arrays.equals(this.link_list, other.getLink_list())));
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
        if (getLink_list() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLink_list());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLink_list(), i);
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
        new org.apache.axis.description.TypeDesc(Link_list2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_list2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("link_list");
        elemField.setXmlName(new javax.xml.namespace.QName("", "link_list"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_name_value"));
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
