/**
 * New_return_note_attachment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class New_return_note_attachment  implements java.io.Serializable {
    private com.sugarcrm.www.sugarcrm.New_note_attachment note_attachment;

    public New_return_note_attachment() {
    }

    public New_return_note_attachment(
           com.sugarcrm.www.sugarcrm.New_note_attachment note_attachment) {
           this.note_attachment = note_attachment;
    }


    /**
     * Gets the note_attachment value for this New_return_note_attachment.
     * 
     * @return note_attachment
     */
    public com.sugarcrm.www.sugarcrm.New_note_attachment getNote_attachment() {
        return note_attachment;
    }


    /**
     * Sets the note_attachment value for this New_return_note_attachment.
     * 
     * @param note_attachment
     */
    public void setNote_attachment(com.sugarcrm.www.sugarcrm.New_note_attachment note_attachment) {
        this.note_attachment = note_attachment;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof New_return_note_attachment)) return false;
        New_return_note_attachment other = (New_return_note_attachment) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.note_attachment==null && other.getNote_attachment()==null) || 
             (this.note_attachment!=null &&
              this.note_attachment.equals(other.getNote_attachment())));
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
        if (getNote_attachment() != null) {
            _hashCode += getNote_attachment().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(New_return_note_attachment.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_return_note_attachment"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("note_attachment");
        elemField.setXmlName(new javax.xml.namespace.QName("", "note_attachment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_note_attachment"));
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
