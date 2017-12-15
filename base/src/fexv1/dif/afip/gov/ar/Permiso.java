/**
 * Permiso.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class Permiso  implements java.io.Serializable {
    private java.lang.String id_permiso;

    private int dst_merc;

    public Permiso() {
    }

    public Permiso(
           java.lang.String id_permiso,
           int dst_merc) {
           this.id_permiso = id_permiso;
           this.dst_merc = dst_merc;
    }


    /**
     * Gets the id_permiso value for this Permiso.
     * 
     * @return id_permiso
     */
    public java.lang.String getId_permiso() {
        return id_permiso;
    }


    /**
     * Sets the id_permiso value for this Permiso.
     * 
     * @param id_permiso
     */
    public void setId_permiso(java.lang.String id_permiso) {
        this.id_permiso = id_permiso;
    }


    /**
     * Gets the dst_merc value for this Permiso.
     * 
     * @return dst_merc
     */
    public int getDst_merc() {
        return dst_merc;
    }


    /**
     * Sets the dst_merc value for this Permiso.
     * 
     * @param dst_merc
     */
    public void setDst_merc(int dst_merc) {
        this.dst_merc = dst_merc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Permiso)) return false;
        Permiso other = (Permiso) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id_permiso==null && other.getId_permiso()==null) || 
             (this.id_permiso!=null &&
              this.id_permiso.equals(other.getId_permiso()))) &&
            this.dst_merc == other.getDst_merc();
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
        if (getId_permiso() != null) {
            _hashCode += getId_permiso().hashCode();
        }
        _hashCode += getDst_merc();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Permiso.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Permiso"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id_permiso");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Id_permiso"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dst_merc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Dst_merc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
