/**
 * ClsFEXOutAuthorize.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXOutAuthorize  implements java.io.Serializable {
    private long id;

    private long cuit;

    private short cbte_tipo;

    private short punto_vta;

    private long cbte_nro;

    private java.lang.String cae;

    private java.lang.String fch_venc_Cae;

    private java.lang.String fch_cbte;

    private java.lang.String resultado;

    private java.lang.String reproceso;

    private java.lang.String motivos_Obs;

    public ClsFEXOutAuthorize() {
    }

    public ClsFEXOutAuthorize(
           long id,
           long cuit,
           short cbte_tipo,
           short punto_vta,
           long cbte_nro,
           java.lang.String cae,
           java.lang.String fch_venc_Cae,
           java.lang.String fch_cbte,
           java.lang.String resultado,
           java.lang.String reproceso,
           java.lang.String motivos_Obs) {
           this.id = id;
           this.cuit = cuit;
           this.cbte_tipo = cbte_tipo;
           this.punto_vta = punto_vta;
           this.cbte_nro = cbte_nro;
           this.cae = cae;
           this.fch_venc_Cae = fch_venc_Cae;
           this.fch_cbte = fch_cbte;
           this.resultado = resultado;
           this.reproceso = reproceso;
           this.motivos_Obs = motivos_Obs;
    }


    /**
     * Gets the id value for this ClsFEXOutAuthorize.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this ClsFEXOutAuthorize.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the cuit value for this ClsFEXOutAuthorize.
     * 
     * @return cuit
     */
    public long getCuit() {
        return cuit;
    }


    /**
     * Sets the cuit value for this ClsFEXOutAuthorize.
     * 
     * @param cuit
     */
    public void setCuit(long cuit) {
        this.cuit = cuit;
    }


    /**
     * Gets the cbte_tipo value for this ClsFEXOutAuthorize.
     * 
     * @return cbte_tipo
     */
    public short getCbte_tipo() {
        return cbte_tipo;
    }


    /**
     * Sets the cbte_tipo value for this ClsFEXOutAuthorize.
     * 
     * @param cbte_tipo
     */
    public void setCbte_tipo(short cbte_tipo) {
        this.cbte_tipo = cbte_tipo;
    }


    /**
     * Gets the punto_vta value for this ClsFEXOutAuthorize.
     * 
     * @return punto_vta
     */
    public short getPunto_vta() {
        return punto_vta;
    }


    /**
     * Sets the punto_vta value for this ClsFEXOutAuthorize.
     * 
     * @param punto_vta
     */
    public void setPunto_vta(short punto_vta) {
        this.punto_vta = punto_vta;
    }


    /**
     * Gets the cbte_nro value for this ClsFEXOutAuthorize.
     * 
     * @return cbte_nro
     */
    public long getCbte_nro() {
        return cbte_nro;
    }


    /**
     * Sets the cbte_nro value for this ClsFEXOutAuthorize.
     * 
     * @param cbte_nro
     */
    public void setCbte_nro(long cbte_nro) {
        this.cbte_nro = cbte_nro;
    }


    /**
     * Gets the cae value for this ClsFEXOutAuthorize.
     * 
     * @return cae
     */
    public java.lang.String getCae() {
        return cae;
    }


    /**
     * Sets the cae value for this ClsFEXOutAuthorize.
     * 
     * @param cae
     */
    public void setCae(java.lang.String cae) {
        this.cae = cae;
    }


    /**
     * Gets the fch_venc_Cae value for this ClsFEXOutAuthorize.
     * 
     * @return fch_venc_Cae
     */
    public java.lang.String getFch_venc_Cae() {
        return fch_venc_Cae;
    }


    /**
     * Sets the fch_venc_Cae value for this ClsFEXOutAuthorize.
     * 
     * @param fch_venc_Cae
     */
    public void setFch_venc_Cae(java.lang.String fch_venc_Cae) {
        this.fch_venc_Cae = fch_venc_Cae;
    }


    /**
     * Gets the fch_cbte value for this ClsFEXOutAuthorize.
     * 
     * @return fch_cbte
     */
    public java.lang.String getFch_cbte() {
        return fch_cbte;
    }


    /**
     * Sets the fch_cbte value for this ClsFEXOutAuthorize.
     * 
     * @param fch_cbte
     */
    public void setFch_cbte(java.lang.String fch_cbte) {
        this.fch_cbte = fch_cbte;
    }


    /**
     * Gets the resultado value for this ClsFEXOutAuthorize.
     * 
     * @return resultado
     */
    public java.lang.String getResultado() {
        return resultado;
    }


    /**
     * Sets the resultado value for this ClsFEXOutAuthorize.
     * 
     * @param resultado
     */
    public void setResultado(java.lang.String resultado) {
        this.resultado = resultado;
    }


    /**
     * Gets the reproceso value for this ClsFEXOutAuthorize.
     * 
     * @return reproceso
     */
    public java.lang.String getReproceso() {
        return reproceso;
    }


    /**
     * Sets the reproceso value for this ClsFEXOutAuthorize.
     * 
     * @param reproceso
     */
    public void setReproceso(java.lang.String reproceso) {
        this.reproceso = reproceso;
    }


    /**
     * Gets the motivos_Obs value for this ClsFEXOutAuthorize.
     * 
     * @return motivos_Obs
     */
    public java.lang.String getMotivos_Obs() {
        return motivos_Obs;
    }


    /**
     * Sets the motivos_Obs value for this ClsFEXOutAuthorize.
     * 
     * @param motivos_Obs
     */
    public void setMotivos_Obs(java.lang.String motivos_Obs) {
        this.motivos_Obs = motivos_Obs;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXOutAuthorize)) return false;
        ClsFEXOutAuthorize other = (ClsFEXOutAuthorize) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            this.cuit == other.getCuit() &&
            this.cbte_tipo == other.getCbte_tipo() &&
            this.punto_vta == other.getPunto_vta() &&
            this.cbte_nro == other.getCbte_nro() &&
            ((this.cae==null && other.getCae()==null) || 
             (this.cae!=null &&
              this.cae.equals(other.getCae()))) &&
            ((this.fch_venc_Cae==null && other.getFch_venc_Cae()==null) || 
             (this.fch_venc_Cae!=null &&
              this.fch_venc_Cae.equals(other.getFch_venc_Cae()))) &&
            ((this.fch_cbte==null && other.getFch_cbte()==null) || 
             (this.fch_cbte!=null &&
              this.fch_cbte.equals(other.getFch_cbte()))) &&
            ((this.resultado==null && other.getResultado()==null) || 
             (this.resultado!=null &&
              this.resultado.equals(other.getResultado()))) &&
            ((this.reproceso==null && other.getReproceso()==null) || 
             (this.reproceso!=null &&
              this.reproceso.equals(other.getReproceso()))) &&
            ((this.motivos_Obs==null && other.getMotivos_Obs()==null) || 
             (this.motivos_Obs!=null &&
              this.motivos_Obs.equals(other.getMotivos_Obs())));
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
        _hashCode += new Long(getId()).hashCode();
        _hashCode += new Long(getCuit()).hashCode();
        _hashCode += getCbte_tipo();
        _hashCode += getPunto_vta();
        _hashCode += new Long(getCbte_nro()).hashCode();
        if (getCae() != null) {
            _hashCode += getCae().hashCode();
        }
        if (getFch_venc_Cae() != null) {
            _hashCode += getFch_venc_Cae().hashCode();
        }
        if (getFch_cbte() != null) {
            _hashCode += getFch_cbte().hashCode();
        }
        if (getResultado() != null) {
            _hashCode += getResultado().hashCode();
        }
        if (getReproceso() != null) {
            _hashCode += getReproceso().hashCode();
        }
        if (getMotivos_Obs() != null) {
            _hashCode += getMotivos_Obs().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ClsFEXOutAuthorize.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXOutAuthorize"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cuit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cuit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbte_tipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cbte_tipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("punto_vta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Punto_vta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbte_nro");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cbte_nro"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cae");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cae"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fch_venc_Cae");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Fch_venc_Cae"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fch_cbte");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Fch_cbte"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Resultado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reproceso");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Reproceso"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("motivos_Obs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Motivos_Obs"));
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
