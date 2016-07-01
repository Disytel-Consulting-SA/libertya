/**
 * FECabResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FECabResponse  implements java.io.Serializable {
    private long cuit;

    private int ptoVta;

    private int cbteTipo;

    private java.lang.String fchProceso;

    private int cantReg;

    private java.lang.String resultado;

    private java.lang.String reproceso;

    public FECabResponse() {
    }

    public FECabResponse(
           long cuit,
           int ptoVta,
           int cbteTipo,
           java.lang.String fchProceso,
           int cantReg,
           java.lang.String resultado,
           java.lang.String reproceso) {
           this.cuit = cuit;
           this.ptoVta = ptoVta;
           this.cbteTipo = cbteTipo;
           this.fchProceso = fchProceso;
           this.cantReg = cantReg;
           this.resultado = resultado;
           this.reproceso = reproceso;
    }


    /**
     * Gets the cuit value for this FECabResponse.
     * 
     * @return cuit
     */
    public long getCuit() {
        return cuit;
    }


    /**
     * Sets the cuit value for this FECabResponse.
     * 
     * @param cuit
     */
    public void setCuit(long cuit) {
        this.cuit = cuit;
    }


    /**
     * Gets the ptoVta value for this FECabResponse.
     * 
     * @return ptoVta
     */
    public int getPtoVta() {
        return ptoVta;
    }


    /**
     * Sets the ptoVta value for this FECabResponse.
     * 
     * @param ptoVta
     */
    public void setPtoVta(int ptoVta) {
        this.ptoVta = ptoVta;
    }


    /**
     * Gets the cbteTipo value for this FECabResponse.
     * 
     * @return cbteTipo
     */
    public int getCbteTipo() {
        return cbteTipo;
    }


    /**
     * Sets the cbteTipo value for this FECabResponse.
     * 
     * @param cbteTipo
     */
    public void setCbteTipo(int cbteTipo) {
        this.cbteTipo = cbteTipo;
    }


    /**
     * Gets the fchProceso value for this FECabResponse.
     * 
     * @return fchProceso
     */
    public java.lang.String getFchProceso() {
        return fchProceso;
    }


    /**
     * Sets the fchProceso value for this FECabResponse.
     * 
     * @param fchProceso
     */
    public void setFchProceso(java.lang.String fchProceso) {
        this.fchProceso = fchProceso;
    }


    /**
     * Gets the cantReg value for this FECabResponse.
     * 
     * @return cantReg
     */
    public int getCantReg() {
        return cantReg;
    }


    /**
     * Sets the cantReg value for this FECabResponse.
     * 
     * @param cantReg
     */
    public void setCantReg(int cantReg) {
        this.cantReg = cantReg;
    }


    /**
     * Gets the resultado value for this FECabResponse.
     * 
     * @return resultado
     */
    public java.lang.String getResultado() {
        return resultado;
    }


    /**
     * Sets the resultado value for this FECabResponse.
     * 
     * @param resultado
     */
    public void setResultado(java.lang.String resultado) {
        this.resultado = resultado;
    }


    /**
     * Gets the reproceso value for this FECabResponse.
     * 
     * @return reproceso
     */
    public java.lang.String getReproceso() {
        return reproceso;
    }


    /**
     * Sets the reproceso value for this FECabResponse.
     * 
     * @param reproceso
     */
    public void setReproceso(java.lang.String reproceso) {
        this.reproceso = reproceso;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FECabResponse)) return false;
        FECabResponse other = (FECabResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.cuit == other.getCuit() &&
            this.ptoVta == other.getPtoVta() &&
            this.cbteTipo == other.getCbteTipo() &&
            ((this.fchProceso==null && other.getFchProceso()==null) || 
             (this.fchProceso!=null &&
              this.fchProceso.equals(other.getFchProceso()))) &&
            this.cantReg == other.getCantReg() &&
            ((this.resultado==null && other.getResultado()==null) || 
             (this.resultado!=null &&
              this.resultado.equals(other.getResultado()))) &&
            ((this.reproceso==null && other.getReproceso()==null) || 
             (this.reproceso!=null &&
              this.reproceso.equals(other.getReproceso())));
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
        _hashCode += new Long(getCuit()).hashCode();
        _hashCode += getPtoVta();
        _hashCode += getCbteTipo();
        if (getFchProceso() != null) {
            _hashCode += getFchProceso().hashCode();
        }
        _hashCode += getCantReg();
        if (getResultado() != null) {
            _hashCode += getResultado().hashCode();
        }
        if (getReproceso() != null) {
            _hashCode += getReproceso().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FECabResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECabResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cuit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Cuit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ptoVta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbteTipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteTipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
        elemField.setFieldName("cantReg");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CantReg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Resultado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reproceso");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Reproceso"));
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
