/**
 * Module_list_entry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Module_list_entry  implements java.io.Serializable {
    private java.lang.String module_key;

    private java.lang.String module_label;

    private boolean favorite_enabled;

    private com.sugarcrm.www.sugarcrm.Acl_list_entry[] acls;

    public Module_list_entry() {
    }

    public Module_list_entry(
           java.lang.String module_key,
           java.lang.String module_label,
           boolean favorite_enabled,
           com.sugarcrm.www.sugarcrm.Acl_list_entry[] acls) {
           this.module_key = module_key;
           this.module_label = module_label;
           this.favorite_enabled = favorite_enabled;
           this.acls = acls;
    }


    /**
     * Gets the module_key value for this Module_list_entry.
     * 
     * @return module_key
     */
    public java.lang.String getModule_key() {
        return module_key;
    }


    /**
     * Sets the module_key value for this Module_list_entry.
     * 
     * @param module_key
     */
    public void setModule_key(java.lang.String module_key) {
        this.module_key = module_key;
    }


    /**
     * Gets the module_label value for this Module_list_entry.
     * 
     * @return module_label
     */
    public java.lang.String getModule_label() {
        return module_label;
    }


    /**
     * Sets the module_label value for this Module_list_entry.
     * 
     * @param module_label
     */
    public void setModule_label(java.lang.String module_label) {
        this.module_label = module_label;
    }


    /**
     * Gets the favorite_enabled value for this Module_list_entry.
     * 
     * @return favorite_enabled
     */
    public boolean isFavorite_enabled() {
        return favorite_enabled;
    }


    /**
     * Sets the favorite_enabled value for this Module_list_entry.
     * 
     * @param favorite_enabled
     */
    public void setFavorite_enabled(boolean favorite_enabled) {
        this.favorite_enabled = favorite_enabled;
    }


    /**
     * Gets the acls value for this Module_list_entry.
     * 
     * @return acls
     */
    public com.sugarcrm.www.sugarcrm.Acl_list_entry[] getAcls() {
        return acls;
    }


    /**
     * Sets the acls value for this Module_list_entry.
     * 
     * @param acls
     */
    public void setAcls(com.sugarcrm.www.sugarcrm.Acl_list_entry[] acls) {
        this.acls = acls;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Module_list_entry)) return false;
        Module_list_entry other = (Module_list_entry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.module_key==null && other.getModule_key()==null) || 
             (this.module_key!=null &&
              this.module_key.equals(other.getModule_key()))) &&
            ((this.module_label==null && other.getModule_label()==null) || 
             (this.module_label!=null &&
              this.module_label.equals(other.getModule_label()))) &&
            this.favorite_enabled == other.isFavorite_enabled() &&
            ((this.acls==null && other.getAcls()==null) || 
             (this.acls!=null &&
              java.util.Arrays.equals(this.acls, other.getAcls())));
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
        if (getModule_key() != null) {
            _hashCode += getModule_key().hashCode();
        }
        if (getModule_label() != null) {
            _hashCode += getModule_label().hashCode();
        }
        _hashCode += (isFavorite_enabled() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getAcls() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAcls());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAcls(), i);
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
        new org.apache.axis.description.TypeDesc(Module_list_entry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "module_list_entry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("module_key");
        elemField.setXmlName(new javax.xml.namespace.QName("", "module_key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("module_label");
        elemField.setXmlName(new javax.xml.namespace.QName("", "module_label"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("favorite_enabled");
        elemField.setXmlName(new javax.xml.namespace.QName("", "favorite_enabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acls");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acls"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "acl_list_entry"));
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
