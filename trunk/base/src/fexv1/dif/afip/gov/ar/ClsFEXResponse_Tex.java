/**
 * ClsFEXResponse_Tex.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_Tex  implements java.io.Serializable {
    private short tex_Id;

    private java.lang.String tex_Ds;

    private java.lang.String tex_vig_desde;

    private java.lang.String tex_vig_hasta;

    public ClsFEXResponse_Tex() {
    }

    public ClsFEXResponse_Tex(
           short tex_Id,
           java.lang.String tex_Ds,
           java.lang.String tex_vig_desde,
           java.lang.String tex_vig_hasta) {
           this.tex_Id = tex_Id;
           this.tex_Ds = tex_Ds;
           this.tex_vig_desde = tex_vig_desde;
           this.tex_vig_hasta = tex_vig_hasta;
    }


    /**
     * Gets the tex_Id value for this ClsFEXResponse_Tex.
     * 
     * @return tex_Id
     */
    public short getTex_Id() {
        return tex_Id;
    }


    /**
     * Sets the tex_Id value for this ClsFEXResponse_Tex.
     * 
     * @param tex_Id
     */
    public void setTex_Id(short tex_Id) {
        this.tex_Id = tex_Id;
    }


    /**
     * Gets the tex_Ds value for this ClsFEXResponse_Tex.
     * 
     * @return tex_Ds
     */
    public java.lang.String getTex_Ds() {
        return tex_Ds;
    }


    /**
     * Sets the tex_Ds value for this ClsFEXResponse_Tex.
     * 
     * @param tex_Ds
     */
    public void setTex_Ds(java.lang.String tex_Ds) {
        this.tex_Ds = tex_Ds;
    }


    /**
     * Gets the tex_vig_desde value for this ClsFEXResponse_Tex.
     * 
     * @return tex_vig_desde
     */
    public java.lang.String getTex_vig_desde() {
        return tex_vig_desde;
    }


    /**
     * Sets the tex_vig_desde value for this ClsFEXResponse_Tex.
     * 
     * @param tex_vig_desde
     */
    public void setTex_vig_desde(java.lang.String tex_vig_desde) {
        this.tex_vig_desde = tex_vig_desde;
    }


    /**
     * Gets the tex_vig_hasta value for this ClsFEXResponse_Tex.
     * 
     * @return tex_vig_hasta
     */
    public java.lang.String getTex_vig_hasta() {
        return tex_vig_hasta;
    }


    /**
     * Sets the tex_vig_hasta value for this ClsFEXResponse_Tex.
     * 
     * @param tex_vig_hasta
     */
    public void setTex_vig_hasta(java.lang.String tex_vig_hasta) {
        this.tex_vig_hasta = tex_vig_hasta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_Tex)) return false;
        ClsFEXResponse_Tex other = (ClsFEXResponse_Tex) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.tex_Id == other.getTex_Id() &&
            ((this.tex_Ds==null && other.getTex_Ds()==null) || 
             (this.tex_Ds!=null &&
              this.tex_Ds.equals(other.getTex_Ds()))) &&
            ((this.tex_vig_desde==null && other.getTex_vig_desde()==null) || 
             (this.tex_vig_desde!=null &&
              this.tex_vig_desde.equals(other.getTex_vig_desde()))) &&
            ((this.tex_vig_hasta==null && other.getTex_vig_hasta()==null) || 
             (this.tex_vig_hasta!=null &&
              this.tex_vig_hasta.equals(other.getTex_vig_hasta())));
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
        _hashCode += getTex_Id();
        if (getTex_Ds() != null) {
            _hashCode += getTex_Ds().hashCode();
        }
        if (getTex_vig_desde() != null) {
            _hashCode += getTex_vig_desde().hashCode();
        }
        if (getTex_vig_hasta() != null) {
            _hashCode += getTex_vig_hasta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_Tex.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Tex"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tex_Id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Tex_Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tex_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Tex_Ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tex_vig_desde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Tex_vig_desde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tex_vig_hasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Tex_vig_hasta"));
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
