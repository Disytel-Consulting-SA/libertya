/**
 * FECAEDetRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class FECAEDetRequest  extends FEV1.dif.afip.gov.ar.FEDetRequest  implements java.io.Serializable {
    public FECAEDetRequest() {
    }

    public FECAEDetRequest(
           int concepto,
           int docTipo,
           long docNro,
           long cbteDesde,
           long cbteHasta,
           java.lang.String cbteFch,
           double impTotal,
           double impTotConc,
           double impNeto,
           double impOpEx,
           double impTrib,
           double impIVA,
           java.lang.String fchServDesde,
           java.lang.String fchServHasta,
           java.lang.String fchVtoPago,
           java.lang.String monId,
           double monCotiz,
           FEV1.dif.afip.gov.ar.CbteAsoc[] cbtesAsoc,
           FEV1.dif.afip.gov.ar.Tributo[] tributos,
           FEV1.dif.afip.gov.ar.AlicIva[] iva,
           FEV1.dif.afip.gov.ar.Opcional[] opcionales) {
        super(
            concepto,
            docTipo,
            docNro,
            cbteDesde,
            cbteHasta,
            cbteFch,
            impTotal,
            impTotConc,
            impNeto,
            impOpEx,
            impTrib,
            impIVA,
            fchServDesde,
            fchServHasta,
            fchVtoPago,
            monId,
            monCotiz,
            cbtesAsoc,
            tributos,
            iva,
            opcionales);
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FECAEDetRequest)) return false;
        FECAEDetRequest other = (FECAEDetRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj);
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FECAEDetRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEDetRequest"));
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
