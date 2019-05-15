/**
 * New_return_document_revision.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class New_return_document_revision  implements java.io.Serializable {
    private com.sugarcrm.www.sugarcrm.Document_revision document_revision;

    public New_return_document_revision() {
    }

    public New_return_document_revision(
           com.sugarcrm.www.sugarcrm.Document_revision document_revision) {
           this.document_revision = document_revision;
    }


    /**
     * Gets the document_revision value for this New_return_document_revision.
     * 
     * @return document_revision
     */
    public com.sugarcrm.www.sugarcrm.Document_revision getDocument_revision() {
        return document_revision;
    }


    /**
     * Sets the document_revision value for this New_return_document_revision.
     * 
     * @param document_revision
     */
    public void setDocument_revision(com.sugarcrm.www.sugarcrm.Document_revision document_revision) {
        this.document_revision = document_revision;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof New_return_document_revision)) return false;
        New_return_document_revision other = (New_return_document_revision) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.document_revision==null && other.getDocument_revision()==null) || 
             (this.document_revision!=null &&
              this.document_revision.equals(other.getDocument_revision())));
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
        if (getDocument_revision() != null) {
            _hashCode += getDocument_revision().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(New_return_document_revision.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_return_document_revision"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document_revision");
        elemField.setXmlName(new javax.xml.namespace.QName("", "document_revision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "document_revision"));
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
