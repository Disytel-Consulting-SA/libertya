/**
 * PtoVenta.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class PtoVenta  implements java.io.Serializable {
    private short nro;

    private java.lang.String emisionTipo;

    private java.lang.String bloqueado;

    private java.lang.String fchBaja;

    public PtoVenta() {
    }

    public PtoVenta(
           short nro,
           java.lang.String emisionTipo,
           java.lang.String bloqueado,
           java.lang.String fchBaja) {
           this.nro = nro;
           this.emisionTipo = emisionTipo;
           this.bloqueado = bloqueado;
           this.fchBaja = fchBaja;
    }


    /**
     * Gets the nro value for this PtoVenta.
     * 
     * @return nro
     */
    public short getNro() {
        return nro;
    }


    /**
     * Sets the nro value for this PtoVenta.
     * 
     * @param nro
     */
    public void setNro(short nro) {
        this.nro = nro;
    }


    /**
     * Gets the emisionTipo value for this PtoVenta.
     * 
     * @return emisionTipo
     */
    public java.lang.String getEmisionTipo() {
        return emisionTipo;
    }


    /**
     * Sets the emisionTipo value for this PtoVenta.
     * 
     * @param emisionTipo
     */
    public void setEmisionTipo(java.lang.String emisionTipo) {
        this.emisionTipo = emisionTipo;
    }


    /**
     * Gets the bloqueado value for this PtoVenta.
     * 
     * @return bloqueado
     */
    public java.lang.String getBloqueado() {
        return bloqueado;
    }


    /**
     * Sets the bloqueado value for this PtoVenta.
     * 
     * @param bloqueado
     */
    public void setBloqueado(java.lang.String bloqueado) {
        this.bloqueado = bloqueado;
    }


    /**
     * Gets the fchBaja value for this PtoVenta.
     * 
     * @return fchBaja
     */
    public java.lang.String getFchBaja() {
        return fchBaja;
    }


    /**
     * Sets the fchBaja value for this PtoVenta.
     * 
     * @param fchBaja
     */
    public void setFchBaja(java.lang.String fchBaja) {
        this.fchBaja = fchBaja;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PtoVenta)) return false;
        PtoVenta other = (PtoVenta) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.nro == other.getNro() &&
            ((this.emisionTipo==null && other.getEmisionTipo()==null) || 
             (this.emisionTipo!=null &&
              this.emisionTipo.equals(other.getEmisionTipo()))) &&
            ((this.bloqueado==null && other.getBloqueado()==null) || 
             (this.bloqueado!=null &&
              this.bloqueado.equals(other.getBloqueado()))) &&
            ((this.fchBaja==null && other.getFchBaja()==null) || 
             (this.fchBaja!=null &&
              this.fchBaja.equals(other.getFchBaja())));
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
        _hashCode += getNro();
        if (getEmisionTipo() != null) {
            _hashCode += getEmisionTipo().hashCode();
        }
        if (getBloqueado() != null) {
            _hashCode += getBloqueado().hashCode();
        }
        if (getFchBaja() != null) {
            _hashCode += getFchBaja().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PtoVenta.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVenta"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nro");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Nro"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emisionTipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "EmisionTipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bloqueado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Bloqueado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fchBaja");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FchBaja"));
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
