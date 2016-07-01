/**
 * FECAEDetResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FECAEDetResponse  extends FEV1.dif.afip.gov.ar.FEDetResponse  implements java.io.Serializable {
    private java.lang.String CAE;

    private java.lang.String CAEFchVto;

    public FECAEDetResponse() {
    }

    public FECAEDetResponse(
           int concepto,
           int docTipo,
           long docNro,
           long cbteDesde,
           long cbteHasta,
           java.lang.String cbteFch,
           java.lang.String resultado,
           FEV1.dif.afip.gov.ar.Obs[] observaciones,
           java.lang.String CAE,
           java.lang.String CAEFchVto) {
        super(
            concepto,
            docTipo,
            docNro,
            cbteDesde,
            cbteHasta,
            cbteFch,
            resultado,
            observaciones);
        this.CAE = CAE;
        this.CAEFchVto = CAEFchVto;
    }


    /**
     * Gets the CAE value for this FECAEDetResponse.
     * 
     * @return CAE
     */
    public java.lang.String getCAE() {
        return CAE;
    }


    /**
     * Sets the CAE value for this FECAEDetResponse.
     * 
     * @param CAE
     */
    public void setCAE(java.lang.String CAE) {
        this.CAE = CAE;
    }


    /**
     * Gets the CAEFchVto value for this FECAEDetResponse.
     * 
     * @return CAEFchVto
     */
    public java.lang.String getCAEFchVto() {
        return CAEFchVto;
    }


    /**
     * Sets the CAEFchVto value for this FECAEDetResponse.
     * 
     * @param CAEFchVto
     */
    public void setCAEFchVto(java.lang.String CAEFchVto) {
        this.CAEFchVto = CAEFchVto;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FECAEDetResponse)) return false;
        FECAEDetResponse other = (FECAEDetResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.CAE==null && other.getCAE()==null) || 
             (this.CAE!=null &&
              this.CAE.equals(other.getCAE()))) &&
            ((this.CAEFchVto==null && other.getCAEFchVto()==null) || 
             (this.CAEFchVto!=null &&
              this.CAEFchVto.equals(other.getCAEFchVto())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getCAE() != null) {
            _hashCode += getCAE().hashCode();
        }
        if (getCAEFchVto() != null) {
            _hashCode += getCAEFchVto().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FECAEDetResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEDetResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CAE");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CAE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CAEFchVto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CAEFchVto"));
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
