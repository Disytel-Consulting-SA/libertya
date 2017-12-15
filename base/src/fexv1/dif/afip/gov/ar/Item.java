/**
 * Item.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class Item  implements java.io.Serializable {
    private java.lang.String pro_codigo;

    private java.lang.String pro_ds;

    private java.math.BigDecimal pro_qty;

    private int pro_umed;

    private java.math.BigDecimal pro_precio_uni;

    private java.math.BigDecimal pro_bonificacion;

    private java.math.BigDecimal pro_total_item;

    public Item() {
    }

    public Item(
           java.lang.String pro_codigo,
           java.lang.String pro_ds,
           java.math.BigDecimal pro_qty,
           int pro_umed,
           java.math.BigDecimal pro_precio_uni,
           java.math.BigDecimal pro_bonificacion,
           java.math.BigDecimal pro_total_item) {
           this.pro_codigo = pro_codigo;
           this.pro_ds = pro_ds;
           this.pro_qty = pro_qty;
           this.pro_umed = pro_umed;
           this.pro_precio_uni = pro_precio_uni;
           this.pro_bonificacion = pro_bonificacion;
           this.pro_total_item = pro_total_item;
    }


    /**
     * Gets the pro_codigo value for this Item.
     * 
     * @return pro_codigo
     */
    public java.lang.String getPro_codigo() {
        return pro_codigo;
    }


    /**
     * Sets the pro_codigo value for this Item.
     * 
     * @param pro_codigo
     */
    public void setPro_codigo(java.lang.String pro_codigo) {
        this.pro_codigo = pro_codigo;
    }


    /**
     * Gets the pro_ds value for this Item.
     * 
     * @return pro_ds
     */
    public java.lang.String getPro_ds() {
        return pro_ds;
    }


    /**
     * Sets the pro_ds value for this Item.
     * 
     * @param pro_ds
     */
    public void setPro_ds(java.lang.String pro_ds) {
        this.pro_ds = pro_ds;
    }


    /**
     * Gets the pro_qty value for this Item.
     * 
     * @return pro_qty
     */
    public java.math.BigDecimal getPro_qty() {
        return pro_qty;
    }


    /**
     * Sets the pro_qty value for this Item.
     * 
     * @param pro_qty
     */
    public void setPro_qty(java.math.BigDecimal pro_qty) {
        this.pro_qty = pro_qty;
    }


    /**
     * Gets the pro_umed value for this Item.
     * 
     * @return pro_umed
     */
    public int getPro_umed() {
        return pro_umed;
    }


    /**
     * Sets the pro_umed value for this Item.
     * 
     * @param pro_umed
     */
    public void setPro_umed(int pro_umed) {
        this.pro_umed = pro_umed;
    }


    /**
     * Gets the pro_precio_uni value for this Item.
     * 
     * @return pro_precio_uni
     */
    public java.math.BigDecimal getPro_precio_uni() {
        return pro_precio_uni;
    }


    /**
     * Sets the pro_precio_uni value for this Item.
     * 
     * @param pro_precio_uni
     */
    public void setPro_precio_uni(java.math.BigDecimal pro_precio_uni) {
        this.pro_precio_uni = pro_precio_uni;
    }


    /**
     * Gets the pro_bonificacion value for this Item.
     * 
     * @return pro_bonificacion
     */
    public java.math.BigDecimal getPro_bonificacion() {
        return pro_bonificacion;
    }


    /**
     * Sets the pro_bonificacion value for this Item.
     * 
     * @param pro_bonificacion
     */
    public void setPro_bonificacion(java.math.BigDecimal pro_bonificacion) {
        this.pro_bonificacion = pro_bonificacion;
    }


    /**
     * Gets the pro_total_item value for this Item.
     * 
     * @return pro_total_item
     */
    public java.math.BigDecimal getPro_total_item() {
        return pro_total_item;
    }


    /**
     * Sets the pro_total_item value for this Item.
     * 
     * @param pro_total_item
     */
    public void setPro_total_item(java.math.BigDecimal pro_total_item) {
        this.pro_total_item = pro_total_item;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Item)) return false;
        Item other = (Item) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.pro_codigo==null && other.getPro_codigo()==null) || 
             (this.pro_codigo!=null &&
              this.pro_codigo.equals(other.getPro_codigo()))) &&
            ((this.pro_ds==null && other.getPro_ds()==null) || 
             (this.pro_ds!=null &&
              this.pro_ds.equals(other.getPro_ds()))) &&
            ((this.pro_qty==null && other.getPro_qty()==null) || 
             (this.pro_qty!=null &&
              this.pro_qty.equals(other.getPro_qty()))) &&
            this.pro_umed == other.getPro_umed() &&
            ((this.pro_precio_uni==null && other.getPro_precio_uni()==null) || 
             (this.pro_precio_uni!=null &&
              this.pro_precio_uni.equals(other.getPro_precio_uni()))) &&
            ((this.pro_bonificacion==null && other.getPro_bonificacion()==null) || 
             (this.pro_bonificacion!=null &&
              this.pro_bonificacion.equals(other.getPro_bonificacion()))) &&
            ((this.pro_total_item==null && other.getPro_total_item()==null) || 
             (this.pro_total_item!=null &&
              this.pro_total_item.equals(other.getPro_total_item())));
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
        if (getPro_codigo() != null) {
            _hashCode += getPro_codigo().hashCode();
        }
        if (getPro_ds() != null) {
            _hashCode += getPro_ds().hashCode();
        }
        if (getPro_qty() != null) {
            _hashCode += getPro_qty().hashCode();
        }
        _hashCode += getPro_umed();
        if (getPro_precio_uni() != null) {
            _hashCode += getPro_precio_uni().hashCode();
        }
        if (getPro_bonificacion() != null) {
            _hashCode += getPro_bonificacion().hashCode();
        }
        if (getPro_total_item() != null) {
            _hashCode += getPro_total_item().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Item.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Item"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pro_codigo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pro_codigo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pro_ds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pro_ds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pro_qty");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pro_qty"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pro_umed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pro_umed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pro_precio_uni");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pro_precio_uni"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pro_bonificacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pro_bonificacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pro_total_item");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Pro_total_item"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
