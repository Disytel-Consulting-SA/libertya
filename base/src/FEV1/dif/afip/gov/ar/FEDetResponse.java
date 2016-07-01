/**
 * FEDetResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FEDetResponse  implements java.io.Serializable {
    private int concepto;

    private int docTipo;

    private long docNro;

    private long cbteDesde;

    private long cbteHasta;

    private java.lang.String cbteFch;

    private java.lang.String resultado;

    private FEV1.dif.afip.gov.ar.Obs[] observaciones;

    public FEDetResponse() {
    }

    public FEDetResponse(
           int concepto,
           int docTipo,
           long docNro,
           long cbteDesde,
           long cbteHasta,
           java.lang.String cbteFch,
           java.lang.String resultado,
           FEV1.dif.afip.gov.ar.Obs[] observaciones) {
           this.concepto = concepto;
           this.docTipo = docTipo;
           this.docNro = docNro;
           this.cbteDesde = cbteDesde;
           this.cbteHasta = cbteHasta;
           this.cbteFch = cbteFch;
           this.resultado = resultado;
           this.observaciones = observaciones;
    }


    /**
     * Gets the concepto value for this FEDetResponse.
     * 
     * @return concepto
     */
    public int getConcepto() {
        return concepto;
    }


    /**
     * Sets the concepto value for this FEDetResponse.
     * 
     * @param concepto
     */
    public void setConcepto(int concepto) {
        this.concepto = concepto;
    }


    /**
     * Gets the docTipo value for this FEDetResponse.
     * 
     * @return docTipo
     */
    public int getDocTipo() {
        return docTipo;
    }


    /**
     * Sets the docTipo value for this FEDetResponse.
     * 
     * @param docTipo
     */
    public void setDocTipo(int docTipo) {
        this.docTipo = docTipo;
    }


    /**
     * Gets the docNro value for this FEDetResponse.
     * 
     * @return docNro
     */
    public long getDocNro() {
        return docNro;
    }


    /**
     * Sets the docNro value for this FEDetResponse.
     * 
     * @param docNro
     */
    public void setDocNro(long docNro) {
        this.docNro = docNro;
    }


    /**
     * Gets the cbteDesde value for this FEDetResponse.
     * 
     * @return cbteDesde
     */
    public long getCbteDesde() {
        return cbteDesde;
    }


    /**
     * Sets the cbteDesde value for this FEDetResponse.
     * 
     * @param cbteDesde
     */
    public void setCbteDesde(long cbteDesde) {
        this.cbteDesde = cbteDesde;
    }


    /**
     * Gets the cbteHasta value for this FEDetResponse.
     * 
     * @return cbteHasta
     */
    public long getCbteHasta() {
        return cbteHasta;
    }


    /**
     * Sets the cbteHasta value for this FEDetResponse.
     * 
     * @param cbteHasta
     */
    public void setCbteHasta(long cbteHasta) {
        this.cbteHasta = cbteHasta;
    }


    /**
     * Gets the cbteFch value for this FEDetResponse.
     * 
     * @return cbteFch
     */
    public java.lang.String getCbteFch() {
        return cbteFch;
    }


    /**
     * Sets the cbteFch value for this FEDetResponse.
     * 
     * @param cbteFch
     */
    public void setCbteFch(java.lang.String cbteFch) {
        this.cbteFch = cbteFch;
    }


    /**
     * Gets the resultado value for this FEDetResponse.
     * 
     * @return resultado
     */
    public java.lang.String getResultado() {
        return resultado;
    }


    /**
     * Sets the resultado value for this FEDetResponse.
     * 
     * @param resultado
     */
    public void setResultado(java.lang.String resultado) {
        this.resultado = resultado;
    }


    /**
     * Gets the observaciones value for this FEDetResponse.
     * 
     * @return observaciones
     */
    public FEV1.dif.afip.gov.ar.Obs[] getObservaciones() {
        return observaciones;
    }


    /**
     * Sets the observaciones value for this FEDetResponse.
     * 
     * @param observaciones
     */
    public void setObservaciones(FEV1.dif.afip.gov.ar.Obs[] observaciones) {
        this.observaciones = observaciones;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FEDetResponse)) return false;
        FEDetResponse other = (FEDetResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.concepto == other.getConcepto() &&
            this.docTipo == other.getDocTipo() &&
            this.docNro == other.getDocNro() &&
            this.cbteDesde == other.getCbteDesde() &&
            this.cbteHasta == other.getCbteHasta() &&
            ((this.cbteFch==null && other.getCbteFch()==null) || 
             (this.cbteFch!=null &&
              this.cbteFch.equals(other.getCbteFch()))) &&
            ((this.resultado==null && other.getResultado()==null) || 
             (this.resultado!=null &&
              this.resultado.equals(other.getResultado()))) &&
            ((this.observaciones==null && other.getObservaciones()==null) || 
             (this.observaciones!=null &&
              java.util.Arrays.equals(this.observaciones, other.getObservaciones())));
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
        _hashCode += getConcepto();
        _hashCode += getDocTipo();
        _hashCode += new Long(getDocNro()).hashCode();
        _hashCode += new Long(getCbteDesde()).hashCode();
        _hashCode += new Long(getCbteHasta()).hashCode();
        if (getCbteFch() != null) {
            _hashCode += getCbteFch().hashCode();
        }
        if (getResultado() != null) {
            _hashCode += getResultado().hashCode();
        }
        if (getObservaciones() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getObservaciones());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObservaciones(), i);
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
        new org.apache.axis.description.TypeDesc(FEDetResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEDetResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("concepto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Concepto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docTipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DocTipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docNro");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DocNro"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbteDesde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteDesde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbteHasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteHasta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbteFch");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteFch"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("observaciones");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Observaciones"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Obs"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Obs"));
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
