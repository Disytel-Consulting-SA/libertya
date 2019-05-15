/**
 * New_module_fields.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class New_module_fields  implements java.io.Serializable {
    private java.lang.String module_name;

    private java.lang.String table_name;

    private com.sugarcrm.www.sugarcrm.Field[] module_fields;

    private com.sugarcrm.www.sugarcrm.Link_field[] link_fields;

    public New_module_fields() {
    }

    public New_module_fields(
           java.lang.String module_name,
           java.lang.String table_name,
           com.sugarcrm.www.sugarcrm.Field[] module_fields,
           com.sugarcrm.www.sugarcrm.Link_field[] link_fields) {
           this.module_name = module_name;
           this.table_name = table_name;
           this.module_fields = module_fields;
           this.link_fields = link_fields;
    }


    /**
     * Gets the module_name value for this New_module_fields.
     * 
     * @return module_name
     */
    public java.lang.String getModule_name() {
        return module_name;
    }


    /**
     * Sets the module_name value for this New_module_fields.
     * 
     * @param module_name
     */
    public void setModule_name(java.lang.String module_name) {
        this.module_name = module_name;
    }


    /**
     * Gets the table_name value for this New_module_fields.
     * 
     * @return table_name
     */
    public java.lang.String getTable_name() {
        return table_name;
    }


    /**
     * Sets the table_name value for this New_module_fields.
     * 
     * @param table_name
     */
    public void setTable_name(java.lang.String table_name) {
        this.table_name = table_name;
    }


    /**
     * Gets the module_fields value for this New_module_fields.
     * 
     * @return module_fields
     */
    public com.sugarcrm.www.sugarcrm.Field[] getModule_fields() {
        return module_fields;
    }


    /**
     * Sets the module_fields value for this New_module_fields.
     * 
     * @param module_fields
     */
    public void setModule_fields(com.sugarcrm.www.sugarcrm.Field[] module_fields) {
        this.module_fields = module_fields;
    }


    /**
     * Gets the link_fields value for this New_module_fields.
     * 
     * @return link_fields
     */
    public com.sugarcrm.www.sugarcrm.Link_field[] getLink_fields() {
        return link_fields;
    }


    /**
     * Sets the link_fields value for this New_module_fields.
     * 
     * @param link_fields
     */
    public void setLink_fields(com.sugarcrm.www.sugarcrm.Link_field[] link_fields) {
        this.link_fields = link_fields;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof New_module_fields)) return false;
        New_module_fields other = (New_module_fields) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.module_name==null && other.getModule_name()==null) || 
             (this.module_name!=null &&
              this.module_name.equals(other.getModule_name()))) &&
            ((this.table_name==null && other.getTable_name()==null) || 
             (this.table_name!=null &&
              this.table_name.equals(other.getTable_name()))) &&
            ((this.module_fields==null && other.getModule_fields()==null) || 
             (this.module_fields!=null &&
              java.util.Arrays.equals(this.module_fields, other.getModule_fields()))) &&
            ((this.link_fields==null && other.getLink_fields()==null) || 
             (this.link_fields!=null &&
              java.util.Arrays.equals(this.link_fields, other.getLink_fields())));
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
        if (getModule_name() != null) {
            _hashCode += getModule_name().hashCode();
        }
        if (getTable_name() != null) {
            _hashCode += getTable_name().hashCode();
        }
        if (getModule_fields() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getModule_fields());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getModule_fields(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLink_fields() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLink_fields());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLink_fields(), i);
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
        new org.apache.axis.description.TypeDesc(New_module_fields.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_module_fields"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("module_name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "module_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("table_name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "table_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("module_fields");
        elemField.setXmlName(new javax.xml.namespace.QName("", "module_fields"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "field"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("link_fields");
        elemField.setXmlName(new javax.xml.namespace.QName("", "link_fields"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_field"));
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
