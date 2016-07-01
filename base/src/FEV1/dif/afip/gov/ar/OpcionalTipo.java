/**
 * OpcionalTipo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class OpcionalTipo  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String desc;

    private java.lang.String fchDesde;

    private java.lang.String fchHasta;

    public OpcionalTipo() {
    }

    public OpcionalTipo(
           java.lang.String id,
           java.lang.String desc,
           java.lang.String fchDesde,
           java.lang.String fchHasta) {
           this.id = id;
           this.desc = desc;
           this.fchDesde = fchDesde;
           this.fchHasta = fchHasta;
    }


    /**
     * Gets the id value for this OpcionalTipo.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this OpcionalTipo.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the desc value for this OpcionalTipo.
     * 
     * @return desc
     */
    public java.lang.String getDesc() {
        return desc;
    }


    /**
     * Sets the desc value for this OpcionalTipo.
     * 
     * @param desc
     */
    public void setDesc(java.lang.String desc) {
        this.desc = desc;
    }


    /**
     * Gets the fchDesde value for this OpcionalTipo.
     * 
     * @return fchDesde
     */
    public java.lang.String getFchDesde() {
        return fchDesde;
    }


    /**
     * Sets the fchDesde value for this OpcionalTipo.
     * 
     * @param fchDesde
     */
    public void setFchDesde(java.lang.String fchDesde) {
        this.fchDesde = fchDesde;
    }


    /**
     * Gets the fchHasta value for this OpcionalTipo.
     * 
     * @return fchHasta
     */
    public java.lang.String getFchHasta() {
        return fchHasta;
    }


    /**
     * Sets the fchHasta value for this OpcionalTipo.
     * 
     * @param fchHasta
     */
    public void setFchHasta(java.lang.String fchHasta) {
        this.fchHasta = fchHasta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OpcionalTipo)) return false;
        OpcionalTipo other = (OpcionalTipo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.desc==null && other.getDesc()==null) || 
             (this.desc!=null &&
              this.desc.equals(other.getDesc()))) &&
            ((this.fchDesde==null && other.getFchDesde()==null) || 
             (this.fchDesde!=null &&
              this.fchDesde.equals(other.getFchDesde()))) &&
            ((this.fchHasta==null && other.getFchHasta()==null) || 
             (this.fchHasta!=null &&
              this.fchHasta.equals(other.getFchHasta())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getDesc() != null) {
            _hashCode += getDesc().hashCode();
        }
        if (getFchDesde() != null) {
            _hashCode += getFchDesde().hashCode();
        }
        if (getFchHasta() != null) {
            _hashCode += getFchHasta().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OpcionalTipo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "OpcionalTipo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("desc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Desc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fchDesde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FchDesde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fchHasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FchHasta"));
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
