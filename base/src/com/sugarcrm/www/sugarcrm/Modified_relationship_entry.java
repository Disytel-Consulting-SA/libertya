/**
 * Modified_relationship_entry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Modified_relationship_entry  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String module_name;

    private com.sugarcrm.www.sugarcrm.Name_value[] name_value_list;

    public Modified_relationship_entry() {
    }

    public Modified_relationship_entry(
           java.lang.String id,
           java.lang.String module_name,
           com.sugarcrm.www.sugarcrm.Name_value[] name_value_list) {
           this.id = id;
           this.module_name = module_name;
           this.name_value_list = name_value_list;
    }


    /**
     * Gets the id value for this Modified_relationship_entry.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this Modified_relationship_entry.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the module_name value for this Modified_relationship_entry.
     * 
     * @return module_name
     */
    public java.lang.String getModule_name() {
        return module_name;
    }


    /**
     * Sets the module_name value for this Modified_relationship_entry.
     * 
     * @param module_name
     */
    public void setModule_name(java.lang.String module_name) {
        this.module_name = module_name;
    }


    /**
     * Gets the name_value_list value for this Modified_relationship_entry.
     * 
     * @return name_value_list
     */
    public com.sugarcrm.www.sugarcrm.Name_value[] getName_value_list() {
        return name_value_list;
    }


    /**
     * Sets the name_value_list value for this Modified_relationship_entry.
     * 
     * @param name_value_list
     */
    public void setName_value_list(com.sugarcrm.www.sugarcrm.Name_value[] name_value_list) {
        this.name_value_list = name_value_list;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Modified_relationship_entry)) return false;
        Modified_relationship_entry other = (Modified_relationship_entry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.module_name==null && other.getModule_name()==null) || 
             (this.module_name!=null &&
              this.module_name.equals(other.getModule_name()))) &&
            ((this.name_value_list==null && other.getName_value_list()==null) || 
             (this.name_value_list!=null &&
              java.util.Arrays.equals(this.name_value_list, other.getName_value_list())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getModule_name() != null) {
            _hashCode += getModule_name().hashCode();
        }
        if (getName_value_list() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getName_value_list());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getName_value_list(), i);
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
        new org.apache.axis.description.TypeDesc(Modified_relationship_entry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "modified_relationship_entry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("module_name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "module_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name_value_list");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name_value_list"));
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
