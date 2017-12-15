/**
 * ClsFEXResponse_Mon.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_Mon  implements java.io.Serializable {
    private java.lang.String mon_Id;

    private java.lang.String mon_Ds;

    private java.lang.String mon_vig_desde;

    private java.lang.String mon_vig_hasta;

    public ClsFEXResponse_Mon() {
    }

    public ClsFEXResponse_Mon(
           java.lang.String mon_Id,
           java.lang.String mon_Ds,
           java.lang.String mon_vig_desde,
           java.lang.String mon_vig_hasta) {
           this.mon_Id = mon_Id;
           this.mon_Ds = mon_Ds;
           this.mon_vig_desde = mon_vig_desde;
           this.mon_vig_hasta = mon_vig_hasta;
    }


    /**
     * Gets the mon_Id value for this ClsFEXResponse_Mon.
     * 
     * @return mon_Id
     */
    public java.lang.String getMon_Id() {
        return mon_Id;
    }


    /**
     * Sets the mon_Id value for this ClsFEXResponse_Mon.
     * 
     * @param mon_Id
     */
    public void setMon_Id(java.lang.String mon_Id) {
        this.mon_Id = mon_Id;
    }


    /**
     * Gets the mon_Ds value for this ClsFEXResponse_Mon.
     * 
     * @return mon_Ds
     */
    public java.lang.String getMon_Ds() {
        return mon_Ds;
    }


    /**
     * Sets the mon_Ds value for this ClsFEXResponse_Mon.
     * 
     * @param mon_Ds
     */
    public void setMon_Ds(java.lang.String mon_Ds) {
        this.mon_Ds = mon_Ds;
    }


    /**
     * Gets the mon_vig_desde value for this ClsFEXResponse_Mon.
     * 
     * @return mon_vig_desde
     */
    public java.lang.String getMon_vig_desde() {
        return mon_vig_desde;
    }


    /**
     * Sets the mon_vig_desde value for this ClsFEXResponse_Mon.
     * 
     * @param mon_vig_desde
     */
    public void setMon_vig_desde(java.lang.String mon_vig_desde) {
        this.mon_vig_desde = mon_vig_desde;
    }


    /**
     * Gets the mon_vig_hasta value for this ClsFEXResponse_Mon.
     * 
     * @return mon_vig_hasta
     */
    public java.lang.String getMon_vig_hasta() {
        return mon_vig_hasta;
    }


    /**
     * Sets the mon_vig_hasta value for this ClsFEXResponse_Mon.
     * 
     * @param mon_vig_hasta
     */
    public void setMon_vig_hasta(java.lang.String mon_vig_hasta) {
        this.mon_vig_hasta = mon_vig_hasta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_Mon)) return false;
        ClsFEXResponse_Mon other = (ClsFEXResponse_Mon) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mon_Id==null && other.getMon_Id()==null) || 
             (this.mon_Id!=null &&
              this.mon_Id.equals(other.getMon_Id()))) &&
            ((this.mon_Ds==null && other.getMon_Ds()==null) || 
             (this.mon_Ds!=null &&
              this.mon_Ds.equals(other.getMon_Ds()))) &&
            ((this.mon_vig_desde==null && other.getMon_vig_desde()==null) || 
             (this.mon_vig_desde!=null &&
              this.mon_vig_desde.equals(other.getMon_vig_desde()))) &&
            ((this.mon_vig_hasta==null && other.getMon_vig_hasta()==null) || 
             (this.mon_vig_hasta!=null &&
              this.mon_vig_hasta.equals(other.getMon_vig_hasta())));
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
        if (getMon_Id() != null) {
            _hashCode += getMon_Id().hashCode();
        }
        if (getMon_Ds() != null) {
            _hashCode += getMon_Ds().hashCode();
        }
        if (getMon_vig_desde() != null) {
            _hashCode += getMon_vig_desde().hashCode();
        }
        if (getMon_vig_hasta() != null) {
            _hashCode += getMon_vig_hasta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_Mon.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Mon"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mon_Id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Mon_Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mon_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Mon_Ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mon_vig_desde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Mon_vig_desde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mon_vig_hasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Mon_vig_hasta"));
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
