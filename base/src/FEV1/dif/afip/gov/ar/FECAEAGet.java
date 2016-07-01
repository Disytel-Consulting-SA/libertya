/**
 * FECAEAGet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FECAEAGet  implements java.io.Serializable {
    private java.lang.String CAEA;

    private int periodo;

    private short orden;

    private java.lang.String fchVigDesde;

    private java.lang.String fchVigHasta;

    private java.lang.String fchTopeInf;

    private java.lang.String fchProceso;

    public FECAEAGet() {
    }

    public FECAEAGet(
           java.lang.String CAEA,
           int periodo,
           short orden,
           java.lang.String fchVigDesde,
           java.lang.String fchVigHasta,
           java.lang.String fchTopeInf,
           java.lang.String fchProceso) {
           this.CAEA = CAEA;
           this.periodo = periodo;
           this.orden = orden;
           this.fchVigDesde = fchVigDesde;
           this.fchVigHasta = fchVigHasta;
           this.fchTopeInf = fchTopeInf;
           this.fchProceso = fchProceso;
    }


    /**
     * Gets the CAEA value for this FECAEAGet.
     * 
     * @return CAEA
     */
    public java.lang.String getCAEA() {
        return CAEA;
    }


    /**
     * Sets the CAEA value for this FECAEAGet.
     * 
     * @param CAEA
     */
    public void setCAEA(java.lang.String CAEA) {
        this.CAEA = CAEA;
    }


    /**
     * Gets the periodo value for this FECAEAGet.
     * 
     * @return periodo
     */
    public int getPeriodo() {
        return periodo;
    }


    /**
     * Sets the periodo value for this FECAEAGet.
     * 
     * @param periodo
     */
    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }


    /**
     * Gets the orden value for this FECAEAGet.
     * 
     * @return orden
     */
    public short getOrden() {
        return orden;
    }


    /**
     * Sets the orden value for this FECAEAGet.
     * 
     * @param orden
     */
    public void setOrden(short orden) {
        this.orden = orden;
    }


    /**
     * Gets the fchVigDesde value for this FECAEAGet.
     * 
     * @return fchVigDesde
     */
    public java.lang.String getFchVigDesde() {
        return fchVigDesde;
    }


    /**
     * Sets the fchVigDesde value for this FECAEAGet.
     * 
     * @param fchVigDesde
     */
    public void setFchVigDesde(java.lang.String fchVigDesde) {
        this.fchVigDesde = fchVigDesde;
    }


    /**
     * Gets the fchVigHasta value for this FECAEAGet.
     * 
     * @return fchVigHasta
     */
    public java.lang.String getFchVigHasta() {
        return fchVigHasta;
    }


    /**
     * Sets the fchVigHasta value for this FECAEAGet.
     * 
     * @param fchVigHasta
     */
    public void setFchVigHasta(java.lang.String fchVigHasta) {
        this.fchVigHasta = fchVigHasta;
    }


    /**
     * Gets the fchTopeInf value for this FECAEAGet.
     * 
     * @return fchTopeInf
     */
    public java.lang.String getFchTopeInf() {
        return fchTopeInf;
    }


    /**
     * Sets the fchTopeInf value for this FECAEAGet.
     * 
     * @param fchTopeInf
     */
    public void setFchTopeInf(java.lang.String fchTopeInf) {
        this.fchTopeInf = fchTopeInf;
    }


    /**
     * Gets the fchProceso value for this FECAEAGet.
     * 
     * @return fchProceso
     */
    public java.lang.String getFchProceso() {
        return fchProceso;
    }


    /**
     * Sets the fchProceso value for this FECAEAGet.
     * 
     * @param fchProceso
     */
    public void setFchProceso(java.lang.String fchProceso) {
        this.fchProceso = fchProceso;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FECAEAGet)) return false;
        FECAEAGet other = (FECAEAGet) obj;
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
            this.periodo == other.getPeriodo() &&
            this.orden == other.getOrden() &&
            ((this.fchVigDesde==null && other.getFchVigDesde()==null) || 
             (this.fchVigDesde!=null &&
              this.fchVigDesde.equals(other.getFchVigDesde()))) &&
            ((this.fchVigHasta==null && other.getFchVigHasta()==null) || 
             (this.fchVigHasta!=null &&
              this.fchVigHasta.equals(other.getFchVigHasta()))) &&
            ((this.fchTopeInf==null && other.getFchTopeInf()==null) || 
             (this.fchTopeInf!=null &&
              this.fchTopeInf.equals(other.getFchTopeInf()))) &&
            ((this.fchProceso==null && other.getFchProceso()==null) || 
             (this.fchProceso!=null &&
              this.fchProceso.equals(other.getFchProceso())));
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
        _hashCode += getPeriodo();
        _hashCode += getOrden();
        if (getFchVigDesde() != null) {
            _hashCode += getFchVigDesde().hashCode();
        }
        if (getFchVigHasta() != null) {
            _hashCode += getFchVigHasta().hashCode();
        }
        if (getFchTopeInf() != null) {
            _hashCode += getFchTopeInf().hashCode();
        }
        if (getFchProceso() != null) {
            _hashCode += getFchProceso().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FECAEAGet.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAGet"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CAEA");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CAEA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("periodo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Periodo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orden");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Orden"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fchVigDesde");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FchVigDesde"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fchVigHasta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FchVigHasta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fchTopeInf");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FchTopeInf"));
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
