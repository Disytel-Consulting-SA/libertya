/**
 * FECompConsultaReq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FECompConsultaReq  implements java.io.Serializable {
    private int cbteTipo;

    private long cbteNro;

    private int ptoVta;

    public FECompConsultaReq() {
    }

    public FECompConsultaReq(
           int cbteTipo,
           long cbteNro,
           int ptoVta) {
           this.cbteTipo = cbteTipo;
           this.cbteNro = cbteNro;
           this.ptoVta = ptoVta;
    }


    /**
     * Gets the cbteTipo value for this FECompConsultaReq.
     * 
     * @return cbteTipo
     */
    public int getCbteTipo() {
        return cbteTipo;
    }


    /**
     * Sets the cbteTipo value for this FECompConsultaReq.
     * 
     * @param cbteTipo
     */
    public void setCbteTipo(int cbteTipo) {
        this.cbteTipo = cbteTipo;
    }


    /**
     * Gets the cbteNro value for this FECompConsultaReq.
     * 
     * @return cbteNro
     */
    public long getCbteNro() {
        return cbteNro;
    }


    /**
     * Sets the cbteNro value for this FECompConsultaReq.
     * 
     * @param cbteNro
     */
    public void setCbteNro(long cbteNro) {
        this.cbteNro = cbteNro;
    }


    /**
     * Gets the ptoVta value for this FECompConsultaReq.
     * 
     * @return ptoVta
     */
    public int getPtoVta() {
        return ptoVta;
    }


    /**
     * Sets the ptoVta value for this FECompConsultaReq.
     * 
     * @param ptoVta
     */
    public void setPtoVta(int ptoVta) {
        this.ptoVta = ptoVta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FECompConsultaReq)) return false;
        FECompConsultaReq other = (FECompConsultaReq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.cbteTipo == other.getCbteTipo() &&
            this.cbteNro == other.getCbteNro() &&
            this.ptoVta == other.getPtoVta();
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
        _hashCode += getCbteTipo();
        _hashCode += new Long(getCbteNro()).hashCode();
        _hashCode += getPtoVta();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FECompConsultaReq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompConsultaReq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbteTipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteTipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbteNro");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteNro"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ptoVta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVta"));
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
