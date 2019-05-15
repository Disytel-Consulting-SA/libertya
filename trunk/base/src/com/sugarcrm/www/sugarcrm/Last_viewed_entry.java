/**
 * Last_viewed_entry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Last_viewed_entry  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String item_id;

    private java.lang.String item_summary;

    private java.lang.String module_name;

    private java.lang.String monitor_id;

    private java.lang.String date_modified;

    public Last_viewed_entry() {
    }

    public Last_viewed_entry(
           java.lang.String id,
           java.lang.String item_id,
           java.lang.String item_summary,
           java.lang.String module_name,
           java.lang.String monitor_id,
           java.lang.String date_modified) {
           this.id = id;
           this.item_id = item_id;
           this.item_summary = item_summary;
           this.module_name = module_name;
           this.monitor_id = monitor_id;
           this.date_modified = date_modified;
    }


    /**
     * Gets the id value for this Last_viewed_entry.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this Last_viewed_entry.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the item_id value for this Last_viewed_entry.
     * 
     * @return item_id
     */
    public java.lang.String getItem_id() {
        return item_id;
    }


    /**
     * Sets the item_id value for this Last_viewed_entry.
     * 
     * @param item_id
     */
    public void setItem_id(java.lang.String item_id) {
        this.item_id = item_id;
    }


    /**
     * Gets the item_summary value for this Last_viewed_entry.
     * 
     * @return item_summary
     */
    public java.lang.String getItem_summary() {
        return item_summary;
    }


    /**
     * Sets the item_summary value for this Last_viewed_entry.
     * 
     * @param item_summary
     */
    public void setItem_summary(java.lang.String item_summary) {
        this.item_summary = item_summary;
    }


    /**
     * Gets the module_name value for this Last_viewed_entry.
     * 
     * @return module_name
     */
    public java.lang.String getModule_name() {
        return module_name;
    }


    /**
     * Sets the module_name value for this Last_viewed_entry.
     * 
     * @param module_name
     */
    public void setModule_name(java.lang.String module_name) {
        this.module_name = module_name;
    }


    /**
     * Gets the monitor_id value for this Last_viewed_entry.
     * 
     * @return monitor_id
     */
    public java.lang.String getMonitor_id() {
        return monitor_id;
    }


    /**
     * Sets the monitor_id value for this Last_viewed_entry.
     * 
     * @param monitor_id
     */
    public void setMonitor_id(java.lang.String monitor_id) {
        this.monitor_id = monitor_id;
    }


    /**
     * Gets the date_modified value for this Last_viewed_entry.
     * 
     * @return date_modified
     */
    public java.lang.String getDate_modified() {
        return date_modified;
    }


    /**
     * Sets the date_modified value for this Last_viewed_entry.
     * 
     * @param date_modified
     */
    public void setDate_modified(java.lang.String date_modified) {
        this.date_modified = date_modified;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Last_viewed_entry)) return false;
        Last_viewed_entry other = (Last_viewed_entry) obj;
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
            ((this.item_id==null && other.getItem_id()==null) || 
             (this.item_id!=null &&
              this.item_id.equals(other.getItem_id()))) &&
            ((this.item_summary==null && other.getItem_summary()==null) || 
             (this.item_summary!=null &&
              this.item_summary.equals(other.getItem_summary()))) &&
            ((this.module_name==null && other.getModule_name()==null) || 
             (this.module_name!=null &&
              this.module_name.equals(other.getModule_name()))) &&
            ((this.monitor_id==null && other.getMonitor_id()==null) || 
             (this.monitor_id!=null &&
              this.monitor_id.equals(other.getMonitor_id()))) &&
            ((this.date_modified==null && other.getDate_modified()==null) || 
             (this.date_modified!=null &&
              this.date_modified.equals(other.getDate_modified())));
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
        if (getItem_id() != null) {
            _hashCode += getItem_id().hashCode();
        }
        if (getItem_summary() != null) {
            _hashCode += getItem_summary().hashCode();
        }
        if (getModule_name() != null) {
            _hashCode += getModule_name().hashCode();
        }
        if (getMonitor_id() != null) {
            _hashCode += getMonitor_id().hashCode();
        }
        if (getDate_modified() != null) {
            _hashCode += getDate_modified().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Last_viewed_entry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "last_viewed_entry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("item_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "item_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("item_summary");
        elemField.setXmlName(new javax.xml.namespace.QName("", "item_summary"));
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
        elemField.setFieldName("monitor_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "monitor_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("date_modified");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date_modified"));
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
