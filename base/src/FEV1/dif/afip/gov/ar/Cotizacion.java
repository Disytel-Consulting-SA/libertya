/**
 * Cotizacion.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class Cotizacion  implements java.io.Serializable {
    private java.lang.String monId;

    private double monCotiz;

    private java.lang.String fchCotiz;

    public Cotizacion() {
    }

    public Cotizacion(
           java.lang.String monId,
           double monCotiz,
           java.lang.String fchCotiz) {
           this.monId = monId;
           this.monCotiz = monCotiz;
           this.fchCotiz = fchCotiz;
    }


    /**
     * Gets the monId value for this Cotizacion.
     * 
     * @return monId
     */
    public java.lang.String getMonId() {
        return monId;
    }


    /**
     * Sets the monId value for this Cotizacion.
     * 
     * @param monId
     */
    public void setMonId(java.lang.String monId) {
        this.monId = monId;
    }


    /**
     * Gets the monCotiz value for this Cotizacion.
     * 
     * @return monCotiz
     */
    public double getMonCotiz() {
        return monCotiz;
    }


    /**
     * Sets the monCotiz value for this Cotizacion.
     * 
     * @param monCotiz
     */
    public void setMonCotiz(double monCotiz) {
        this.monCotiz = monCotiz;
    }


    /**
     * Gets the fchCotiz value for this Cotizacion.
     * 
     * @return fchCotiz
     */
    public java.lang.String getFchCotiz() {
        return fchCotiz;
    }


    /**
     * Sets the fchCotiz value for this Cotizacion.
     * 
     * @param fchCotiz
     */
    public void setFchCotiz(java.lang.String fchCotiz) {
        this.fchCotiz = fchCotiz;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Cotizacion)) return false;
        Cotizacion other = (Cotizacion) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.monId==null && other.getMonId()==null) || 
             (this.monId!=null &&
              this.monId.equals(other.getMonId()))) &&
            this.monCotiz == other.getMonCotiz() &&
            ((this.fchCotiz==null && other.getFchCotiz()==null) || 
             (this.fchCotiz!=null &&
              this.fchCotiz.equals(other.getFchCotiz())));
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
        if (getMonId() != null) {
            _hashCode += getMonId().hashCode();
        }
        _hashCode += new Double(getMonCotiz()).hashCode();
        if (getFchCotiz() != null) {
            _hashCode += getFchCotiz().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Cotizacion.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Cotizacion"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "MonId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monCotiz");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "MonCotiz"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fchCotiz");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FchCotiz"));
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
