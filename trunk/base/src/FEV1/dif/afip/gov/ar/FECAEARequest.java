/**
 * FECAEARequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FECAEARequest  implements java.io.Serializable {
    private FEV1.dif.afip.gov.ar.FECAEACabRequest feCabReq;

    private FEV1.dif.afip.gov.ar.FECAEADetRequest[] feDetReq;

    public FECAEARequest() {
    }

    public FECAEARequest(
           FEV1.dif.afip.gov.ar.FECAEACabRequest feCabReq,
           FEV1.dif.afip.gov.ar.FECAEADetRequest[] feDetReq) {
           this.feCabReq = feCabReq;
           this.feDetReq = feDetReq;
    }


    /**
     * Gets the feCabReq value for this FECAEARequest.
     * 
     * @return feCabReq
     */
    public FEV1.dif.afip.gov.ar.FECAEACabRequest getFeCabReq() {
        return feCabReq;
    }


    /**
     * Sets the feCabReq value for this FECAEARequest.
     * 
     * @param feCabReq
     */
    public void setFeCabReq(FEV1.dif.afip.gov.ar.FECAEACabRequest feCabReq) {
        this.feCabReq = feCabReq;
    }


    /**
     * Gets the feDetReq value for this FECAEARequest.
     * 
     * @return feDetReq
     */
    public FEV1.dif.afip.gov.ar.FECAEADetRequest[] getFeDetReq() {
        return feDetReq;
    }


    /**
     * Sets the feDetReq value for this FECAEARequest.
     * 
     * @param feDetReq
     */
    public void setFeDetReq(FEV1.dif.afip.gov.ar.FECAEADetRequest[] feDetReq) {
        this.feDetReq = feDetReq;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FECAEARequest)) return false;
        FECAEARequest other = (FECAEARequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.feCabReq==null && other.getFeCabReq()==null) || 
             (this.feCabReq!=null &&
              this.feCabReq.equals(other.getFeCabReq()))) &&
            ((this.feDetReq==null && other.getFeDetReq()==null) || 
             (this.feDetReq!=null &&
              java.util.Arrays.equals(this.feDetReq, other.getFeDetReq())));
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
        if (getFeCabReq() != null) {
            _hashCode += getFeCabReq().hashCode();
        }
        if (getFeDetReq() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFeDetReq());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFeDetReq(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FECAEARequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEARequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feCabReq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FeCabReq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEACabRequest"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feDetReq");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FeDetReq"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEADetRequest"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEADetRequest"));
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
