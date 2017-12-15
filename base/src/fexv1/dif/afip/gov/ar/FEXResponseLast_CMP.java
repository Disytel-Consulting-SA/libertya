/**
 * FEXResponseLast_CMP.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class FEXResponseLast_CMP  implements java.io.Serializable {
    private fexv1.dif.afip.gov.ar.ClsFEX_LastCMP_Response FEXResult_LastCMP;

    private fexv1.dif.afip.gov.ar.ClsFEXErr FEXErr;

    private fexv1.dif.afip.gov.ar.ClsFEXEvents FEXEvents;

    public FEXResponseLast_CMP() {
    }

    public FEXResponseLast_CMP(
           fexv1.dif.afip.gov.ar.ClsFEX_LastCMP_Response FEXResult_LastCMP,
           fexv1.dif.afip.gov.ar.ClsFEXErr FEXErr,
           fexv1.dif.afip.gov.ar.ClsFEXEvents FEXEvents) {
           this.FEXResult_LastCMP = FEXResult_LastCMP;
           this.FEXErr = FEXErr;
           this.FEXEvents = FEXEvents;
    }


    /**
     * Gets the FEXResult_LastCMP value for this FEXResponseLast_CMP.
     * 
     * @return FEXResult_LastCMP
     */
    public fexv1.dif.afip.gov.ar.ClsFEX_LastCMP_Response getFEXResult_LastCMP() {
        return FEXResult_LastCMP;
    }


    /**
     * Sets the FEXResult_LastCMP value for this FEXResponseLast_CMP.
     * 
     * @param FEXResult_LastCMP
     */
    public void setFEXResult_LastCMP(fexv1.dif.afip.gov.ar.ClsFEX_LastCMP_Response FEXResult_LastCMP) {
        this.FEXResult_LastCMP = FEXResult_LastCMP;
    }


    /**
     * Gets the FEXErr value for this FEXResponseLast_CMP.
     * 
     * @return FEXErr
     */
    public fexv1.dif.afip.gov.ar.ClsFEXErr getFEXErr() {
        return FEXErr;
    }


    /**
     * Sets the FEXErr value for this FEXResponseLast_CMP.
     * 
     * @param FEXErr
     */
    public void setFEXErr(fexv1.dif.afip.gov.ar.ClsFEXErr FEXErr) {
        this.FEXErr = FEXErr;
    }


    /**
     * Gets the FEXEvents value for this FEXResponseLast_CMP.
     * 
     * @return FEXEvents
     */
    public fexv1.dif.afip.gov.ar.ClsFEXEvents getFEXEvents() {
        return FEXEvents;
    }


    /**
     * Sets the FEXEvents value for this FEXResponseLast_CMP.
     * 
     * @param FEXEvents
     */
    public void setFEXEvents(fexv1.dif.afip.gov.ar.ClsFEXEvents FEXEvents) {
        this.FEXEvents = FEXEvents;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FEXResponseLast_CMP)) return false;
        FEXResponseLast_CMP other = (FEXResponseLast_CMP) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.FEXResult_LastCMP==null && other.getFEXResult_LastCMP()==null) || 
             (this.FEXResult_LastCMP!=null &&
              this.FEXResult_LastCMP.equals(other.getFEXResult_LastCMP()))) &&
            ((this.FEXErr==null && other.getFEXErr()==null) || 
             (this.FEXErr!=null &&
              this.FEXErr.equals(other.getFEXErr()))) &&
            ((this.FEXEvents==null && other.getFEXEvents()==null) || 
             (this.FEXEvents!=null &&
              this.FEXEvents.equals(other.getFEXEvents())));
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
        if (getFEXResult_LastCMP() != null) {
            _hashCode += getFEXResult_LastCMP().hashCode();
        }
        if (getFEXErr() != null) {
            _hashCode += getFEXErr().hashCode();
        }
        if (getFEXEvents() != null) {
            _hashCode += getFEXEvents().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FEXResponseLast_CMP.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponseLast_CMP"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FEXResult_LastCMP");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResult_LastCMP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEX_LastCMP_Response"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FEXErr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXErr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXErr"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FEXEvents");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXEvents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXEvents"));
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
