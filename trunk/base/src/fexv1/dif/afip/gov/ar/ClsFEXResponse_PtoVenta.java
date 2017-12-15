/**
 * ClsFEXResponse_PtoVenta.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXResponse_PtoVenta  implements java.io.Serializable {
    private short pve_Nro;

    private java.lang.String pve_Bloqueado;

    private java.lang.String pve_FchBaja;

    public ClsFEXResponse_PtoVenta() {
    }

    public ClsFEXResponse_PtoVenta(
           short pve_Nro,
           java.lang.String pve_Bloqueado,
           java.lang.String pve_FchBaja) {
           this.pve_Nro = pve_Nro;
           this.pve_Bloqueado = pve_Bloqueado;
           this.pve_FchBaja = pve_FchBaja;
    }


    /**
     * Gets the pve_Nro value for this ClsFEXResponse_PtoVenta.
     * 
     * @return pve_Nro
     */
    public short getPve_Nro() {
        return pve_Nro;
    }


    /**
     * Sets the pve_Nro value for this ClsFEXResponse_PtoVenta.
     * 
     * @param pve_Nro
     */
    public void setPve_Nro(short pve_Nro) {
        this.pve_Nro = pve_Nro;
    }


    /**
     * Gets the pve_Bloqueado value for this ClsFEXResponse_PtoVenta.
     * 
     * @return pve_Bloqueado
     */
    public java.lang.String getPve_Bloqueado() {
        return pve_Bloqueado;
    }


    /**
     * Sets the pve_Bloqueado value for this ClsFEXResponse_PtoVenta.
     * 
     * @param pve_Bloqueado
     */
    public void setPve_Bloqueado(java.lang.String pve_Bloqueado) {
        this.pve_Bloqueado = pve_Bloqueado;
    }


    /**
     * Gets the pve_FchBaja value for this ClsFEXResponse_PtoVenta.
     * 
     * @return pve_FchBaja
     */
    public java.lang.String getPve_FchBaja() {
        return pve_FchBaja;
    }


    /**
     * Sets the pve_FchBaja value for this ClsFEXResponse_PtoVenta.
     * 
     * @param pve_FchBaja
     */
    public void setPve_FchBaja(java.lang.String pve_FchBaja) {
        this.pve_FchBaja = pve_FchBaja;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXResponse_PtoVenta)) return false;
        ClsFEXResponse_PtoVenta other = (ClsFEXResponse_PtoVenta) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.pve_Nro == other.getPve_Nro() &&
            ((this.pve_Bloqueado==null && other.getPve_Bloqueado()==null) || 
             (this.pve_Bloqueado!=null &&
              this.pve_Bloqueado.equals(other.getPve_Bloqueado()))) &&
            ((this.pve_FchBaja==null && other.getPve_FchBaja()==null) || 
             (this.pve_FchBaja!=null &&
              this.pve_FchBaja.equals(other.getPve_FchBaja())));
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
        _hashCode += getPve_Nro();
        if (getPve_Bloqueado() != null) {
            _hashCode += getPve_Bloqueado().hashCode();
        }
        if (getPve_FchBaja() != null) {
            _hashCode += getPve_FchBaja().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXResponse_PtoVenta.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_PtoVenta"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pve_Nro");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pve_Nro"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pve_Bloqueado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pve_Bloqueado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pve_FchBaja");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pve_FchBaja"));
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
