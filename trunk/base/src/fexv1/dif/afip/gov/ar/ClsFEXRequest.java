/**
 * ClsFEXRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXRequest  implements java.io.Serializable {
    private long id;

    private java.lang.String fecha_cbte;

    private short cbte_Tipo;

    private short punto_vta;

    private long cbte_nro;

    private short tipo_expo;

    private java.lang.String permiso_existente;

    private fexv1.dif.afip.gov.ar.Permiso[] permisos;

    private short dst_cmp;

    private java.lang.String cliente;

    private long cuit_pais_cliente;

    private java.lang.String domicilio_cliente;

    private java.lang.String id_impositivo;

    private java.lang.String moneda_Id;

    private java.math.BigDecimal moneda_ctz;

    private java.lang.String obs_comerciales;

    private java.math.BigDecimal imp_total;

    private java.lang.String obs;

    private fexv1.dif.afip.gov.ar.Cmp_asoc[] cmps_asoc;

    private java.lang.String forma_pago;

    private java.lang.String incoterms;

    private java.lang.String incoterms_Ds;

    private short idioma_cbte;

    private fexv1.dif.afip.gov.ar.Item[] items;

    private fexv1.dif.afip.gov.ar.Opcional[] opcionales;

    public ClsFEXRequest() {
    }

    public ClsFEXRequest(
           long id,
           java.lang.String fecha_cbte,
           short cbte_Tipo,
           short punto_vta,
           long cbte_nro,
           short tipo_expo,
           java.lang.String permiso_existente,
           fexv1.dif.afip.gov.ar.Permiso[] permisos,
           short dst_cmp,
           java.lang.String cliente,
           long cuit_pais_cliente,
           java.lang.String domicilio_cliente,
           java.lang.String id_impositivo,
           java.lang.String moneda_Id,
           java.math.BigDecimal moneda_ctz,
           java.lang.String obs_comerciales,
           java.math.BigDecimal imp_total,
           java.lang.String obs,
           fexv1.dif.afip.gov.ar.Cmp_asoc[] cmps_asoc,
           java.lang.String forma_pago,
           java.lang.String incoterms,
           java.lang.String incoterms_Ds,
           short idioma_cbte,
           fexv1.dif.afip.gov.ar.Item[] items,
           fexv1.dif.afip.gov.ar.Opcional[] opcionales) {
           this.id = id;
           this.fecha_cbte = fecha_cbte;
           this.cbte_Tipo = cbte_Tipo;
           this.punto_vta = punto_vta;
           this.cbte_nro = cbte_nro;
           this.tipo_expo = tipo_expo;
           this.permiso_existente = permiso_existente;
           this.permisos = permisos;
           this.dst_cmp = dst_cmp;
           this.cliente = cliente;
           this.cuit_pais_cliente = cuit_pais_cliente;
           this.domicilio_cliente = domicilio_cliente;
           this.id_impositivo = id_impositivo;
           this.moneda_Id = moneda_Id;
           this.moneda_ctz = moneda_ctz;
           this.obs_comerciales = obs_comerciales;
           this.imp_total = imp_total;
           this.obs = obs;
           this.cmps_asoc = cmps_asoc;
           this.forma_pago = forma_pago;
           this.incoterms = incoterms;
           this.incoterms_Ds = incoterms_Ds;
           this.idioma_cbte = idioma_cbte;
           this.items = items;
           this.opcionales = opcionales;
    }


    /**
     * Gets the id value for this ClsFEXRequest.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this ClsFEXRequest.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the fecha_cbte value for this ClsFEXRequest.
     * 
     * @return fecha_cbte
     */
    public java.lang.String getFecha_cbte() {
        return fecha_cbte;
    }


    /**
     * Sets the fecha_cbte value for this ClsFEXRequest.
     * 
     * @param fecha_cbte
     */
    public void setFecha_cbte(java.lang.String fecha_cbte) {
        this.fecha_cbte = fecha_cbte;
    }


    /**
     * Gets the cbte_Tipo value for this ClsFEXRequest.
     * 
     * @return cbte_Tipo
     */
    public short getCbte_Tipo() {
        return cbte_Tipo;
    }


    /**
     * Sets the cbte_Tipo value for this ClsFEXRequest.
     * 
     * @param cbte_Tipo
     */
    public void setCbte_Tipo(short cbte_Tipo) {
        this.cbte_Tipo = cbte_Tipo;
    }


    /**
     * Gets the punto_vta value for this ClsFEXRequest.
     * 
     * @return punto_vta
     */
    public short getPunto_vta() {
        return punto_vta;
    }


    /**
     * Sets the punto_vta value for this ClsFEXRequest.
     * 
     * @param punto_vta
     */
    public void setPunto_vta(short punto_vta) {
        this.punto_vta = punto_vta;
    }


    /**
     * Gets the cbte_nro value for this ClsFEXRequest.
     * 
     * @return cbte_nro
     */
    public long getCbte_nro() {
        return cbte_nro;
    }


    /**
     * Sets the cbte_nro value for this ClsFEXRequest.
     * 
     * @param cbte_nro
     */
    public void setCbte_nro(long cbte_nro) {
        this.cbte_nro = cbte_nro;
    }


    /**
     * Gets the tipo_expo value for this ClsFEXRequest.
     * 
     * @return tipo_expo
     */
    public short getTipo_expo() {
        return tipo_expo;
    }


    /**
     * Sets the tipo_expo value for this ClsFEXRequest.
     * 
     * @param tipo_expo
     */
    public void setTipo_expo(short tipo_expo) {
        this.tipo_expo = tipo_expo;
    }


    /**
     * Gets the permiso_existente value for this ClsFEXRequest.
     * 
     * @return permiso_existente
     */
    public java.lang.String getPermiso_existente() {
        return permiso_existente;
    }


    /**
     * Sets the permiso_existente value for this ClsFEXRequest.
     * 
     * @param permiso_existente
     */
    public void setPermiso_existente(java.lang.String permiso_existente) {
        this.permiso_existente = permiso_existente;
    }


    /**
     * Gets the permisos value for this ClsFEXRequest.
     * 
     * @return permisos
     */
    public fexv1.dif.afip.gov.ar.Permiso[] getPermisos() {
        return permisos;
    }


    /**
     * Sets the permisos value for this ClsFEXRequest.
     * 
     * @param permisos
     */
    public void setPermisos(fexv1.dif.afip.gov.ar.Permiso[] permisos) {
        this.permisos = permisos;
    }


    /**
     * Gets the dst_cmp value for this ClsFEXRequest.
     * 
     * @return dst_cmp
     */
    public short getDst_cmp() {
        return dst_cmp;
    }


    /**
     * Sets the dst_cmp value for this ClsFEXRequest.
     * 
     * @param dst_cmp
     */
    public void setDst_cmp(short dst_cmp) {
        this.dst_cmp = dst_cmp;
    }


    /**
     * Gets the cliente value for this ClsFEXRequest.
     * 
     * @return cliente
     */
    public java.lang.String getCliente() {
        return cliente;
    }


    /**
     * Sets the cliente value for this ClsFEXRequest.
     * 
     * @param cliente
     */
    public void setCliente(java.lang.String cliente) {
        this.cliente = cliente;
    }


    /**
     * Gets the cuit_pais_cliente value for this ClsFEXRequest.
     * 
     * @return cuit_pais_cliente
     */
    public long getCuit_pais_cliente() {
        return cuit_pais_cliente;
    }


    /**
     * Sets the cuit_pais_cliente value for this ClsFEXRequest.
     * 
     * @param cuit_pais_cliente
     */
    public void setCuit_pais_cliente(long cuit_pais_cliente) {
        this.cuit_pais_cliente = cuit_pais_cliente;
    }


    /**
     * Gets the domicilio_cliente value for this ClsFEXRequest.
     * 
     * @return domicilio_cliente
     */
    public java.lang.String getDomicilio_cliente() {
        return domicilio_cliente;
    }


    /**
     * Sets the domicilio_cliente value for this ClsFEXRequest.
     * 
     * @param domicilio_cliente
     */
    public void setDomicilio_cliente(java.lang.String domicilio_cliente) {
        this.domicilio_cliente = domicilio_cliente;
    }


    /**
     * Gets the id_impositivo value for this ClsFEXRequest.
     * 
     * @return id_impositivo
     */
    public java.lang.String getId_impositivo() {
        return id_impositivo;
    }


    /**
     * Sets the id_impositivo value for this ClsFEXRequest.
     * 
     * @param id_impositivo
     */
    public void setId_impositivo(java.lang.String id_impositivo) {
        this.id_impositivo = id_impositivo;
    }


    /**
     * Gets the moneda_Id value for this ClsFEXRequest.
     * 
     * @return moneda_Id
     */
    public java.lang.String getMoneda_Id() {
        return moneda_Id;
    }


    /**
     * Sets the moneda_Id value for this ClsFEXRequest.
     * 
     * @param moneda_Id
     */
    public void setMoneda_Id(java.lang.String moneda_Id) {
        this.moneda_Id = moneda_Id;
    }


    /**
     * Gets the moneda_ctz value for this ClsFEXRequest.
     * 
     * @return moneda_ctz
     */
    public java.math.BigDecimal getMoneda_ctz() {
        return moneda_ctz;
    }


    /**
     * Sets the moneda_ctz value for this ClsFEXRequest.
     * 
     * @param moneda_ctz
     */
    public void setMoneda_ctz(java.math.BigDecimal moneda_ctz) {
        this.moneda_ctz = moneda_ctz;
    }


    /**
     * Gets the obs_comerciales value for this ClsFEXRequest.
     * 
     * @return obs_comerciales
     */
    public java.lang.String getObs_comerciales() {
        return obs_comerciales;
    }


    /**
     * Sets the obs_comerciales value for this ClsFEXRequest.
     * 
     * @param obs_comerciales
     */
    public void setObs_comerciales(java.lang.String obs_comerciales) {
        this.obs_comerciales = obs_comerciales;
    }


    /**
     * Gets the imp_total value for this ClsFEXRequest.
     * 
     * @return imp_total
     */
    public java.math.BigDecimal getImp_total() {
        return imp_total;
    }


    /**
     * Sets the imp_total value for this ClsFEXRequest.
     * 
     * @param imp_total
     */
    public void setImp_total(java.math.BigDecimal imp_total) {
        this.imp_total = imp_total;
    }


    /**
     * Gets the obs value for this ClsFEXRequest.
     * 
     * @return obs
     */
    public java.lang.String getObs() {
        return obs;
    }


    /**
     * Sets the obs value for this ClsFEXRequest.
     * 
     * @param obs
     */
    public void setObs(java.lang.String obs) {
        this.obs = obs;
    }


    /**
     * Gets the cmps_asoc value for this ClsFEXRequest.
     * 
     * @return cmps_asoc
     */
    public fexv1.dif.afip.gov.ar.Cmp_asoc[] getCmps_asoc() {
        return cmps_asoc;
    }


    /**
     * Sets the cmps_asoc value for this ClsFEXRequest.
     * 
     * @param cmps_asoc
     */
    public void setCmps_asoc(fexv1.dif.afip.gov.ar.Cmp_asoc[] cmps_asoc) {
        this.cmps_asoc = cmps_asoc;
    }


    /**
     * Gets the forma_pago value for this ClsFEXRequest.
     * 
     * @return forma_pago
     */
    public java.lang.String getForma_pago() {
        return forma_pago;
    }


    /**
     * Sets the forma_pago value for this ClsFEXRequest.
     * 
     * @param forma_pago
     */
    public void setForma_pago(java.lang.String forma_pago) {
        this.forma_pago = forma_pago;
    }


    /**
     * Gets the incoterms value for this ClsFEXRequest.
     * 
     * @return incoterms
     */
    public java.lang.String getIncoterms() {
        return incoterms;
    }


    /**
     * Sets the incoterms value for this ClsFEXRequest.
     * 
     * @param incoterms
     */
    public void setIncoterms(java.lang.String incoterms) {
        this.incoterms = incoterms;
    }


    /**
     * Gets the incoterms_Ds value for this ClsFEXRequest.
     * 
     * @return incoterms_Ds
     */
    public java.lang.String getIncoterms_Ds() {
        return incoterms_Ds;
    }


    /**
     * Sets the incoterms_Ds value for this ClsFEXRequest.
     * 
     * @param incoterms_Ds
     */
    public void setIncoterms_Ds(java.lang.String incoterms_Ds) {
        this.incoterms_Ds = incoterms_Ds;
    }


    /**
     * Gets the idioma_cbte value for this ClsFEXRequest.
     * 
     * @return idioma_cbte
     */
    public short getIdioma_cbte() {
        return idioma_cbte;
    }


    /**
     * Sets the idioma_cbte value for this ClsFEXRequest.
     * 
     * @param idioma_cbte
     */
    public void setIdioma_cbte(short idioma_cbte) {
        this.idioma_cbte = idioma_cbte;
    }


    /**
     * Gets the items value for this ClsFEXRequest.
     * 
     * @return items
     */
    public fexv1.dif.afip.gov.ar.Item[] getItems() {
        return items;
    }


    /**
     * Sets the items value for this ClsFEXRequest.
     * 
     * @param items
     */
    public void setItems(fexv1.dif.afip.gov.ar.Item[] items) {
        this.items = items;
    }


    /**
     * Gets the opcionales value for this ClsFEXRequest.
     * 
     * @return opcionales
     */
    public fexv1.dif.afip.gov.ar.Opcional[] getOpcionales() {
        return opcionales;
    }


    /**
     * Sets the opcionales value for this ClsFEXRequest.
     * 
     * @param opcionales
     */
    public void setOpcionales(fexv1.dif.afip.gov.ar.Opcional[] opcionales) {
        this.opcionales = opcionales;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXRequest)) return false;
        ClsFEXRequest other = (ClsFEXRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            ((this.fecha_cbte==null && other.getFecha_cbte()==null) || 
             (this.fecha_cbte!=null &&
              this.fecha_cbte.equals(other.getFecha_cbte()))) &&
            this.cbte_Tipo == other.getCbte_Tipo() &&
            this.punto_vta == other.getPunto_vta() &&
            this.cbte_nro == other.getCbte_nro() &&
            this.tipo_expo == other.getTipo_expo() &&
            ((this.permiso_existente==null && other.getPermiso_existente()==null) || 
             (this.permiso_existente!=null &&
              this.permiso_existente.equals(other.getPermiso_existente()))) &&
            ((this.permisos==null && other.getPermisos()==null) || 
             (this.permisos!=null &&
              java.util.Arrays.equals(this.permisos, other.getPermisos()))) &&
            this.dst_cmp == other.getDst_cmp() &&
            ((this.cliente==null && other.getCliente()==null) || 
             (this.cliente!=null &&
              this.cliente.equals(other.getCliente()))) &&
            this.cuit_pais_cliente == other.getCuit_pais_cliente() &&
            ((this.domicilio_cliente==null && other.getDomicilio_cliente()==null) || 
             (this.domicilio_cliente!=null &&
              this.domicilio_cliente.equals(other.getDomicilio_cliente()))) &&
            ((this.id_impositivo==null && other.getId_impositivo()==null) || 
             (this.id_impositivo!=null &&
              this.id_impositivo.equals(other.getId_impositivo()))) &&
            ((this.moneda_Id==null && other.getMoneda_Id()==null) || 
             (this.moneda_Id!=null &&
              this.moneda_Id.equals(other.getMoneda_Id()))) &&
            ((this.moneda_ctz==null && other.getMoneda_ctz()==null) || 
             (this.moneda_ctz!=null &&
              this.moneda_ctz.equals(other.getMoneda_ctz()))) &&
            ((this.obs_comerciales==null && other.getObs_comerciales()==null) || 
             (this.obs_comerciales!=null &&
              this.obs_comerciales.equals(other.getObs_comerciales()))) &&
            ((this.imp_total==null && other.getImp_total()==null) || 
             (this.imp_total!=null &&
              this.imp_total.equals(other.getImp_total()))) &&
            ((this.obs==null && other.getObs()==null) || 
             (this.obs!=null &&
              this.obs.equals(other.getObs()))) &&
            ((this.cmps_asoc==null && other.getCmps_asoc()==null) || 
             (this.cmps_asoc!=null &&
              java.util.Arrays.equals(this.cmps_asoc, other.getCmps_asoc()))) &&
            ((this.forma_pago==null && other.getForma_pago()==null) || 
             (this.forma_pago!=null &&
              this.forma_pago.equals(other.getForma_pago()))) &&
            ((this.incoterms==null && other.getIncoterms()==null) || 
             (this.incoterms!=null &&
              this.incoterms.equals(other.getIncoterms()))) &&
            ((this.incoterms_Ds==null && other.getIncoterms_Ds()==null) || 
             (this.incoterms_Ds!=null &&
              this.incoterms_Ds.equals(other.getIncoterms_Ds()))) &&
            this.idioma_cbte == other.getIdioma_cbte() &&
            ((this.items==null && other.getItems()==null) || 
             (this.items!=null &&
              java.util.Arrays.equals(this.items, other.getItems()))) &&
            ((this.opcionales==null && other.getOpcionales()==null) || 
             (this.opcionales!=null &&
              java.util.Arrays.equals(this.opcionales, other.getOpcionales())));
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
        if (getFecha_cbte() != null) {
            _hashCode += getFecha_cbte().hashCode();
        }
        _hashCode += getCbte_Tipo();
        _hashCode += getPunto_vta();
        _hashCode += new Long(getCbte_nro()).hashCode();
        _hashCode += getTipo_expo();
        if (getPermiso_existente() != null) {
            _hashCode += getPermiso_existente().hashCode();
        }
        if (getPermisos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPermisos());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPermisos(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getDst_cmp();
        if (getCliente() != null) {
            _hashCode += getCliente().hashCode();
        }
        _hashCode += new Long(getCuit_pais_cliente()).hashCode();
        if (getDomicilio_cliente() != null) {
            _hashCode += getDomicilio_cliente().hashCode();
        }
        if (getId_impositivo() != null) {
            _hashCode += getId_impositivo().hashCode();
        }
        if (getMoneda_Id() != null) {
            _hashCode += getMoneda_Id().hashCode();
        }
        if (getMoneda_ctz() != null) {
            _hashCode += getMoneda_ctz().hashCode();
        }
        if (getObs_comerciales() != null) {
            _hashCode += getObs_comerciales().hashCode();
        }
        if (getImp_total() != null) {
            _hashCode += getImp_total().hashCode();
        }
        if (getObs() != null) {
            _hashCode += getObs().hashCode();
        }
        if (getCmps_asoc() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCmps_asoc());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCmps_asoc(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getForma_pago() != null) {
            _hashCode += getForma_pago().hashCode();
        }
        if (getIncoterms() != null) {
            _hashCode += getIncoterms().hashCode();
        }
        if (getIncoterms_Ds() != null) {
            _hashCode += getIncoterms_Ds().hashCode();
        }
        _hashCode += getIdioma_cbte();
        if (getItems() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getItems());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getItems(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOpcionales() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOpcionales());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOpcionales(), i);
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
        new org.apache.axis.description.TypeDesc(ClsFEXRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fecha_cbte");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Fecha_cbte"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cbte_Tipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cbte_Tipo"));
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
        elemField.setFieldName("tipo_expo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Tipo_expo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("permiso_existente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Permiso_existente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("permisos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Permisos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Permiso"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Permiso"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dst_cmp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Dst_cmp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cuit_pais_cliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cuit_pais_cliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("domicilio_cliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Domicilio_cliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id_impositivo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Id_impositivo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("moneda_Id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Moneda_Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("moneda_ctz");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Moneda_ctz"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("obs_comerciales");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Obs_comerciales"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imp_total");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Imp_total"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("obs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Obs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cmps_asoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cmps_asoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cmp_asoc"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cmp_asoc"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forma_pago");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Forma_pago"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("incoterms");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Incoterms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("incoterms_Ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Incoterms_Ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idioma_cbte");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Idioma_cbte"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("items");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Items"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Item"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Item"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("opcionales");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opcionales"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opcional"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opcional"));
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
