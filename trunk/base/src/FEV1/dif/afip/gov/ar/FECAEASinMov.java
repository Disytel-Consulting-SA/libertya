/**
 * FECAEASinMov.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FECAEASinMov  implements java.io.Serializable {
    private java.lang.String CAEA;

    private java.lang.String fchProceso;

    private int ptoVta;

    public FECAEASinMov() {
    }

    public FECAEASinMov(
           java.lang.String CAEA,
           java.lang.String fchProceso,
           int ptoVta) {
           this.CAEA = CAEA;
           this.fchProceso = fchProceso;
           this.ptoVta = ptoVta;
    }


    /**
     * Gets the CAEA value for this FECAEASinMov.
     * 
     * @return CAEA
     */
    public java.lang.String getCAEA() {
        return CAEA;
    }


    /**
     * Sets the CAEA value for this FECAEASinMov.
     * 
     * @param CAEA
     */
    public void setCAEA(java.lang.String CAEA) {
        this.CAEA = CAEA;
    }


    /**
     * Gets the fchProceso value for this FECAEASinMov.
     * 
     * @return fchProceso
     */
    public java.lang.String getFchProceso() {
        return fchProceso;
    }


    /**
     * Sets the fchProceso value for this FECAEASinMov.
     * 
     * @param fchProceso
     */
    public void setFchProceso(java.lang.String fchProceso) {
        this.fchProceso = fchProceso;
    }


    /**
     * Gets the ptoVta value for this FECAEASinMov.
     * 
     * @return ptoVta
     */
    public int getPtoVta() {
        return ptoVta;
    }


    /**
     * Sets the ptoVta value for this FECAEASinMov.
     * 
     * @param ptoVta
     */
    public void setPtoVta(int ptoVta) {
        this.ptoVta = ptoVta;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FECAEASinMov)) return false;
        FECAEASinMov other = (FECAEASinMov) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.CAEA==null && other.getCAEA()==null) || 
             (this.CAEA!=null &&
              this.CAEA.equals(other.getCAEA()))) &&
            ((this.fchProceso==null && other.getFchProceso()==null) || 
             (this.fchProceso!=null &&
              this.fchProceso.equals(other.getFchProceso()))) &&
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
        if (getCAEA() != null) {
            _hashCode += getCAEA().hashCode();
        }
        if (getFchProceso() != null) {
            _hashCode += getFchProceso().hashCode();
        }
        _hashCode += getPtoVta();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FECAEASinMov.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMov"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CAEA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CAEA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fchProceso");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FchProceso"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
