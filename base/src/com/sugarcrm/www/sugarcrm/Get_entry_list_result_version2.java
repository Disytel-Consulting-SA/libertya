/**
 * Get_entry_list_result_version2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Get_entry_list_result_version2  implements java.io.Serializable {
    private int result_count;

    private int total_count;

    private int next_offset;

    private com.sugarcrm.www.sugarcrm.Entry_value[] entry_list;

    private com.sugarcrm.www.sugarcrm.Link_list2[] relationship_list;

    public Get_entry_list_result_version2() {
    }

    public Get_entry_list_result_version2(
           int result_count,
           int total_count,
           int next_offset,
           com.sugarcrm.www.sugarcrm.Entry_value[] entry_list,
           com.sugarcrm.www.sugarcrm.Link_list2[] relationship_list) {
           this.result_count = result_count;
           this.total_count = total_count;
           this.next_offset = next_offset;
           this.entry_list = entry_list;
           this.relationship_list = relationship_list;
    }


    /**
     * Gets the result_count value for this Get_entry_list_result_version2.
     * 
     * @return result_count
     */
    public int getResult_count() {
        return result_count;
    }


    /**
     * Sets the result_count value for this Get_entry_list_result_version2.
     * 
     * @param result_count
     */
    public void setResult_count(int result_count) {
        this.result_count = result_count;
    }


    /**
     * Gets the total_count value for this Get_entry_list_result_version2.
     * 
     * @return total_count
     */
    public int getTotal_count() {
        return total_count;
    }


    /**
     * Sets the total_count value for this Get_entry_list_result_version2.
     * 
     * @param total_count
     */
    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }


    /**
     * Gets the next_offset value for this Get_entry_list_result_version2.
     * 
     * @return next_offset
     */
    public int getNext_offset() {
        return next_offset;
    }


    /**
     * Sets the next_offset value for this Get_entry_list_result_version2.
     * 
     * @param next_offset
     */
    public void setNext_offset(int next_offset) {
        this.next_offset = next_offset;
    }


    /**
     * Gets the entry_list value for this Get_entry_list_result_version2.
     * 
     * @return entry_list
     */
    public com.sugarcrm.www.sugarcrm.Entry_value[] getEntry_list() {
        return entry_list;
    }


    /**
     * Sets the entry_list value for this Get_entry_list_result_version2.
     * 
     * @param entry_list
     */
    public void setEntry_list(com.sugarcrm.www.sugarcrm.Entry_value[] entry_list) {
        this.entry_list = entry_list;
    }


    /**
     * Gets the relationship_list value for this Get_entry_list_result_version2.
     * 
     * @return relationship_list
     */
    public com.sugarcrm.www.sugarcrm.Link_list2[] getRelationship_list() {
        return relationship_list;
    }


    /**
     * Sets the relationship_list value for this Get_entry_list_result_version2.
     * 
     * @param relationship_list
     */
    public void setRelationship_list(com.sugarcrm.www.sugarcrm.Link_list2[] relationship_list) {
        this.relationship_list = relationship_list;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Get_entry_list_result_version2)) return false;
        Get_entry_list_result_version2 other = (Get_entry_list_result_version2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.result_count == other.getResult_count() &&
            this.total_count == other.getTotal_count() &&
            this.next_offset == other.getNext_offset() &&
            ((this.entry_list==null && other.getEntry_list()==null) || 
             (this.entry_list!=null &&
              java.util.Arrays.equals(this.entry_list, other.getEntry_list()))) &&
            ((this.relationship_list==null && other.getRelationship_list()==null) || 
             (this.relationship_list!=null &&
              java.util.Arrays.equals(this.relationship_list, other.getRelationship_list())));
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
        _hashCode += getResult_count();
        _hashCode += getTotal_count();
        _hashCode += getNext_offset();
        if (getEntry_list() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEntry_list());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEntry_list(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRelationship_list() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRelationship_list());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRelationship_list(), i);
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
        new org.apache.axis.description.TypeDesc(Get_entry_list_result_version2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_list_result_version2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result_count");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result_count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("total_count");
        elemField.setXmlName(new javax.xml.namespace.QName("", "total_count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("next_offset");
        elemField.setXmlName(new javax.xml.namespace.QName("", "next_offset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entry_list");
        elemField.setXmlName(new javax.xml.namespace.QName("", "entry_list"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "entry_value"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("relationship_list");
        elemField.setXmlName(new javax.xml.namespace.QName("", "relationship_list"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_list2"));
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
