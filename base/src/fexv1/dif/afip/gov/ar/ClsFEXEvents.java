/**
 * ClsFEXEvents.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXEvents  implements java.io.Serializable {
    private int eventCode;

    private java.lang.String eventMsg;

    public ClsFEXEvents() {
    }

    public ClsFEXEvents(
           int eventCode,
           java.lang.String eventMsg) {
           this.eventCode = eventCode;
           this.eventMsg = eventMsg;
    }


    /**
     * Gets the eventCode value for this ClsFEXEvents.
     * 
     * @return eventCode
     */
    public int getEventCode() {
        return eventCode;
    }


    /**
     * Sets the eventCode value for this ClsFEXEvents.
     * 
     * @param eventCode
     */
    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }


    /**
     * Gets the eventMsg value for this ClsFEXEvents.
     * 
     * @return eventMsg
     */
    public java.lang.String getEventMsg() {
        return eventMsg;
    }


    /**
     * Sets the eventMsg value for this ClsFEXEvents.
     * 
     * @param eventMsg
     */
    public void setEventMsg(java.lang.String eventMsg) {
        this.eventMsg = eventMsg;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXEvents)) return false;
        ClsFEXEvents other = (ClsFEXEvents) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.eventCode == other.getEventCode() &&
            ((this.eventMsg==null && other.getEventMsg()==null) || 
             (this.eventMsg!=null &&
              this.eventMsg.equals(other.getEventMsg())));
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
        _hashCode += getEventCode();
        if (getEventMsg() != null) {
            _hashCode += getEventMsg().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXEvents.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXEvents"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "EventCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventMsg");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "EventMsg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
