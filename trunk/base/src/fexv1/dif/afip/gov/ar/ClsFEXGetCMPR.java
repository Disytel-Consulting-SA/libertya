/**
 * ClsFEXGetCMPR.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ClsFEXGetCMPR  implements java.io.Serializable {
    private long id;

    private java.lang.String fecha_cbte;

    private short cbte_tipo;

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

    private java.lang.String fecha_cbte_cae;

    private java.lang.String fch_venc_Cae;

    private java.lang.String cae;

    private java.lang.String resultado;

    private java.lang.String motivos_Obs;

    private fexv1.dif.afip.gov.ar.Opcional[] opcionales;

    public ClsFEXGetCMPR() {
    }

    public ClsFEXGetCMPR(
           long id,
           java.lang.String fecha_cbte,
           short cbte_tipo,
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
           java.lang.String fecha_cbte_cae,
           java.lang.String fch_venc_Cae,
           java.lang.String cae,
           java.lang.String resultado,
           java.lang.String motivos_Obs,
           fexv1.dif.afip.gov.ar.Opcional[] opcionales) {
           this.id = id;
           this.fecha_cbte = fecha_cbte;
           this.cbte_tipo = cbte_tipo;
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
           this.fecha_cbte_cae = fecha_cbte_cae;
           this.fch_venc_Cae = fch_venc_Cae;
           this.cae = cae;
           this.resultado = resultado;
           this.motivos_Obs = motivos_Obs;
           this.opcionales = opcionales;
    }


    /**
     * Gets the id value for this ClsFEXGetCMPR.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this ClsFEXGetCMPR.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the fecha_cbte value for this ClsFEXGetCMPR.
     * 
     * @return fecha_cbte
     */
    public java.lang.String getFecha_cbte() {
        return fecha_cbte;
    }


    /**
     * Sets the fecha_cbte value for this ClsFEXGetCMPR.
     * 
     * @param fecha_cbte
     */
    public void setFecha_cbte(java.lang.String fecha_cbte) {
        this.fecha_cbte = fecha_cbte;
    }


    /**
     * Gets the cbte_tipo value for this ClsFEXGetCMPR.
     * 
     * @return cbte_tipo
     */
    public short getCbte_tipo() {
        return cbte_tipo;
    }


    /**
     * Sets the cbte_tipo value for this ClsFEXGetCMPR.
     * 
     * @param cbte_tipo
     */
    public void setCbte_tipo(short cbte_tipo) {
        this.cbte_tipo = cbte_tipo;
    }


    /**
     * Gets the punto_vta value for this ClsFEXGetCMPR.
     * 
     * @return punto_vta
     */
    public short getPunto_vta() {
        return punto_vta;
    }


    /**
     * Sets the punto_vta value for this ClsFEXGetCMPR.
     * 
     * @param punto_vta
     */
    public void setPunto_vta(short punto_vta) {
        this.punto_vta = punto_vta;
    }


    /**
     * Gets the cbte_nro value for this ClsFEXGetCMPR.
     * 
     * @return cbte_nro
     */
    public long getCbte_nro() {
        return cbte_nro;
    }


    /**
     * Sets the cbte_nro value for this ClsFEXGetCMPR.
     * 
     * @param cbte_nro
     */
    public void setCbte_nro(long cbte_nro) {
        this.cbte_nro = cbte_nro;
    }


    /**
     * Gets the tipo_expo value for this ClsFEXGetCMPR.
     * 
     * @return tipo_expo
     */
    public short getTipo_expo() {
        return tipo_expo;
    }


    /**
     * Sets the tipo_expo value for this ClsFEXGetCMPR.
     * 
     * @param tipo_expo
     */
    public void setTipo_expo(short tipo_expo) {
        this.tipo_expo = tipo_expo;
    }


    /**
     * Gets the permiso_existente value for this ClsFEXGetCMPR.
     * 
     * @return permiso_existente
     */
    public java.lang.String getPermiso_existente() {
        return permiso_existente;
    }


    /**
     * Sets the permiso_existente value for this ClsFEXGetCMPR.
     * 
     * @param permiso_existente
     */
    public void setPermiso_existente(java.lang.String permiso_existente) {
        this.permiso_existente = permiso_existente;
    }


    /**
     * Gets the permisos value for this ClsFEXGetCMPR.
     * 
     * @return permisos
     */
    public fexv1.dif.afip.gov.ar.Permiso[] getPermisos() {
        return permisos;
    }


    /**
     * Sets the permisos value for this ClsFEXGetCMPR.
     * 
     * @param permisos
     */
    public void setPermisos(fexv1.dif.afip.gov.ar.Permiso[] permisos) {
        this.permisos = permisos;
    }


    /**
     * Gets the dst_cmp value for this ClsFEXGetCMPR.
     * 
     * @return dst_cmp
     */
    public short getDst_cmp() {
        return dst_cmp;
    }


    /**
     * Sets the dst_cmp value for this ClsFEXGetCMPR.
     * 
     * @param dst_cmp
     */
    public void setDst_cmp(short dst_cmp) {
        this.dst_cmp = dst_cmp;
    }


    /**
     * Gets the cliente value for this ClsFEXGetCMPR.
     * 
     * @return cliente
     */
    public java.lang.String getCliente() {
        return cliente;
    }


    /**
     * Sets the cliente value for this ClsFEXGetCMPR.
     * 
     * @param cliente
     */
    public void setCliente(java.lang.String cliente) {
        this.cliente = cliente;
    }


    /**
     * Gets the cuit_pais_cliente value for this ClsFEXGetCMPR.
     * 
     * @return cuit_pais_cliente
     */
    public long getCuit_pais_cliente() {
        return cuit_pais_cliente;
    }


    /**
     * Sets the cuit_pais_cliente value for this ClsFEXGetCMPR.
     * 
     * @param cuit_pais_cliente
     */
    public void setCuit_pais_cliente(long cuit_pais_cliente) {
        this.cuit_pais_cliente = cuit_pais_cliente;
    }


    /**
     * Gets the domicilio_cliente value for this ClsFEXGetCMPR.
     * 
     * @return domicilio_cliente
     */
    public java.lang.String getDomicilio_cliente() {
        return domicilio_cliente;
    }


    /**
     * Sets the domicilio_cliente value for this ClsFEXGetCMPR.
     * 
     * @param domicilio_cliente
     */
    public void setDomicilio_cliente(java.lang.String domicilio_cliente) {
        this.domicilio_cliente = domicilio_cliente;
    }


    /**
     * Gets the id_impositivo value for this ClsFEXGetCMPR.
     * 
     * @return id_impositivo
     */
    public java.lang.String getId_impositivo() {
        return id_impositivo;
    }


    /**
     * Sets the id_impositivo value for this ClsFEXGetCMPR.
     * 
     * @param id_impositivo
     */
    public void setId_impositivo(java.lang.String id_impositivo) {
        this.id_impositivo = id_impositivo;
    }


    /**
     * Gets the moneda_Id value for this ClsFEXGetCMPR.
     * 
     * @return moneda_Id
     */
    public java.lang.String getMoneda_Id() {
        return moneda_Id;
    }


    /**
     * Sets the moneda_Id value for this ClsFEXGetCMPR.
     * 
     * @param moneda_Id
     */
    public void setMoneda_Id(java.lang.String moneda_Id) {
        this.moneda_Id = moneda_Id;
    }


    /**
     * Gets the moneda_ctz value for this ClsFEXGetCMPR.
     * 
     * @return moneda_ctz
     */
    public java.math.BigDecimal getMoneda_ctz() {
        return moneda_ctz;
    }


    /**
     * Sets the moneda_ctz value for this ClsFEXGetCMPR.
     * 
     * @param moneda_ctz
     */
    public void setMoneda_ctz(java.math.BigDecimal moneda_ctz) {
        this.moneda_ctz = moneda_ctz;
    }


    /**
     * Gets the obs_comerciales value for this ClsFEXGetCMPR.
     * 
     * @return obs_comerciales
     */
    public java.lang.String getObs_comerciales() {
        return obs_comerciales;
    }


    /**
     * Sets the obs_comerciales value for this ClsFEXGetCMPR.
     * 
     * @param obs_comerciales
     */
    public void setObs_comerciales(java.lang.String obs_comerciales) {
        this.obs_comerciales = obs_comerciales;
    }


    /**
     * Gets the imp_total value for this ClsFEXGetCMPR.
     * 
     * @return imp_total
     */
    public java.math.BigDecimal getImp_total() {
        return imp_total;
    }


    /**
     * Sets the imp_total value for this ClsFEXGetCMPR.
     * 
     * @param imp_total
     */
    public void setImp_total(java.math.BigDecimal imp_total) {
        this.imp_total = imp_total;
    }


    /**
     * Gets the obs value for this ClsFEXGetCMPR.
     * 
     * @return obs
     */
    public java.lang.String getObs() {
        return obs;
    }


    /**
     * Sets the obs value for this ClsFEXGetCMPR.
     * 
     * @param obs
     */
    public void setObs(java.lang.String obs) {
        this.obs = obs;
    }


    /**
     * Gets the cmps_asoc value for this ClsFEXGetCMPR.
     * 
     * @return cmps_asoc
     */
    public fexv1.dif.afip.gov.ar.Cmp_asoc[] getCmps_asoc() {
        return cmps_asoc;
    }


    /**
     * Sets the cmps_asoc value for this ClsFEXGetCMPR.
     * 
     * @param cmps_asoc
     */
    public void setCmps_asoc(fexv1.dif.afip.gov.ar.Cmp_asoc[] cmps_asoc) {
        this.cmps_asoc = cmps_asoc;
    }


    /**
     * Gets the forma_pago value for this ClsFEXGetCMPR.
     * 
     * @return forma_pago
     */
    public java.lang.String getForma_pago() {
        return forma_pago;
    }


    /**
     * Sets the forma_pago value for this ClsFEXGetCMPR.
     * 
     * @param forma_pago
     */
    public void setForma_pago(java.lang.String forma_pago) {
        this.forma_pago = forma_pago;
    }


    /**
     * Gets the incoterms value for this ClsFEXGetCMPR.
     * 
     * @return incoterms
     */
    public java.lang.String getIncoterms() {
        return incoterms;
    }


    /**
     * Sets the incoterms value for this ClsFEXGetCMPR.
     * 
     * @param incoterms
     */
    public void setIncoterms(java.lang.String incoterms) {
        this.incoterms = incoterms;
    }


    /**
     * Gets the incoterms_Ds value for this ClsFEXGetCMPR.
     * 
     * @return incoterms_Ds
     */
    public java.lang.String getIncoterms_Ds() {
        return incoterms_Ds;
    }


    /**
     * Sets the incoterms_Ds value for this ClsFEXGetCMPR.
     * 
     * @param incoterms_Ds
     */
    public void setIncoterms_Ds(java.lang.String incoterms_Ds) {
        this.incoterms_Ds = incoterms_Ds;
    }


    /**
     * Gets the idioma_cbte value for this ClsFEXGetCMPR.
     * 
     * @return idioma_cbte
     */
    public short getIdioma_cbte() {
        return idioma_cbte;
    }


    /**
     * Sets the idioma_cbte value for this ClsFEXGetCMPR.
     * 
     * @param idioma_cbte
     */
    public void setIdioma_cbte(short idioma_cbte) {
        this.idioma_cbte = idioma_cbte;
    }


    /**
     * Gets the items value for this ClsFEXGetCMPR.
     * 
     * @return items
     */
    public fexv1.dif.afip.gov.ar.Item[] getItems() {
        return items;
    }


    /**
     * Sets the items value for this ClsFEXGetCMPR.
     * 
     * @param items
     */
    public void setItems(fexv1.dif.afip.gov.ar.Item[] items) {
        this.items = items;
    }


    /**
     * Gets the fecha_cbte_cae value for this ClsFEXGetCMPR.
     * 
     * @return fecha_cbte_cae
     */
    public java.lang.String getFecha_cbte_cae() {
        return fecha_cbte_cae;
    }


    /**
     * Sets the fecha_cbte_cae value for this ClsFEXGetCMPR.
     * 
     * @param fecha_cbte_cae
     */
    public void setFecha_cbte_cae(java.lang.String fecha_cbte_cae) {
        this.fecha_cbte_cae = fecha_cbte_cae;
    }


    /**
     * Gets the fch_venc_Cae value for this ClsFEXGetCMPR.
     * 
     * @return fch_venc_Cae
     */
    public java.lang.String getFch_venc_Cae() {
        return fch_venc_Cae;
    }


    /**
     * Sets the fch_venc_Cae value for this ClsFEXGetCMPR.
     * 
     * @param fch_venc_Cae
     */
    public void setFch_venc_Cae(java.lang.String fch_venc_Cae) {
        this.fch_venc_Cae = fch_venc_Cae;
    }


    /**
     * Gets the cae value for this ClsFEXGetCMPR.
     * 
     * @return cae
     */
    public java.lang.String getCae() {
        return cae;
    }


    /**
     * Sets the cae value for this ClsFEXGetCMPR.
     * 
     * @param cae
     */
    public void setCae(java.lang.String cae) {
        this.cae = cae;
    }


    /**
     * Gets the resultado value for this ClsFEXGetCMPR.
     * 
     * @return resultado
     */
    public java.lang.String getResultado() {
        return resultado;
    }


    /**
     * Sets the resultado value for this ClsFEXGetCMPR.
     * 
     * @param resultado
     */
    public void setResultado(java.lang.String resultado) {
        this.resultado = resultado;
    }


    /**
     * Gets the motivos_Obs value for this ClsFEXGetCMPR.
     * 
     * @return motivos_Obs
     */
    public java.lang.String getMotivos_Obs() {
        return motivos_Obs;
    }


    /**
     * Sets the motivos_Obs value for this ClsFEXGetCMPR.
     * 
     * @param motivos_Obs
     */
    public void setMotivos_Obs(java.lang.String motivos_Obs) {
        this.motivos_Obs = motivos_Obs;
    }


    /**
     * Gets the opcionales value for this ClsFEXGetCMPR.
     * 
     * @return opcionales
     */
    public fexv1.dif.afip.gov.ar.Opcional[] getOpcionales() {
        return opcionales;
    }


    /**
     * Sets the opcionales value for this ClsFEXGetCMPR.
     * 
     * @param opcionales
     */
    public void setOpcionales(fexv1.dif.afip.gov.ar.Opcional[] opcionales) {
        this.opcionales = opcionales;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ClsFEXGetCMPR)) return false;
        ClsFEXGetCMPR other = (ClsFEXGetCMPR) obj;
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
            this.cbte_tipo == other.getCbte_tipo() &&
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
            ((this.fecha_cbte_cae==null && other.getFecha_cbte_cae()==null) || 
             (this.fecha_cbte_cae!=null &&
              this.fecha_cbte_cae.equals(other.getFecha_cbte_cae()))) &&
            ((this.fch_venc_Cae==null && other.getFch_venc_Cae()==null) || 
             (this.fch_venc_Cae!=null &&
              this.fch_venc_Cae.equals(other.getFch_venc_Cae()))) &&
            ((this.cae==null && other.getCae()==null) || 
             (this.cae!=null &&
              this.cae.equals(other.getCae()))) &&
            ((this.resultado==null && other.getResultado()==null) || 
             (this.resultado!=null &&
              this.resultado.equals(other.getResultado()))) &&
            ((this.motivos_Obs==null && other.getMotivos_Obs()==null) || 
             (this.motivos_Obs!=null &&
              this.motivos_Obs.equals(other.getMotivos_Obs()))) &&
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
        _hashCode += getCbte_tipo();
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
        if (getFecha_cbte_cae() != null) {
            _hashCode += getFecha_cbte_cae().hashCode();
        }
        if (getFch_venc_Cae() != null) {
            _hashCode += getFch_venc_Cae().hashCode();
        }
        if (getCae() != null) {
            _hashCode += getCae().hashCode();
        }
        if (getResultado() != null) {
            _hashCode += getResultado().hashCode();
        }
        if (getMotivos_Obs() != null) {
            _hashCode += getMotivos_Obs().hashCode();
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
        new org.apache.axis.description.TypeDesc(ClsFEXGetCMPR.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXGetCMPR"));
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
        elemField.setFieldName("fecha_cbte_cae");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Fecha_cbte_cae"));
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
        elemField.setFieldName("cae");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cae"));
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
        elemField.setFieldName("motivos_Obs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Motivos_Obs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
