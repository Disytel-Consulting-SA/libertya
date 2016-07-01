/**
 * ServiceSoap12Stub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public class ServiceSoap12Stub extends org.apache.axis.client.Stub implements FEV1.dif.afip.gov.ar.ServiceSoap {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[20];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECAESolicitar");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FeCAEReq"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAERequest"), FEV1.dif.afip.gov.ar.FECAERequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FECAEResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAESolicitarResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECompTotXRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FERegXReqResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FERegXReqResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompTotXRequestResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEDummy");
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DummyResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.DummyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEDummyResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECompUltimoAutorizado");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVta"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteTipo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FERecuperaLastCbteResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FERecuperaLastCbteResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompUltimoAutorizadoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECompConsultar");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FeCompConsReq"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompConsultaReq"), FEV1.dif.afip.gov.ar.FECompConsultaReq.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompConsultaResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FECompConsultaResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompConsultarResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECAEARegInformativo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FeCAEARegInfReq"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEARequest"), FEV1.dif.afip.gov.ar.FECAEARequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FECAEAResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEARegInformativoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECAEASolicitar");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Periodo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Orden"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"), short.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAGetResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FECAEAGetResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASolicitarResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECAEASinMovimientoConsultar");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CAEA"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVta"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMovConsResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FECAEASinMovConsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMovimientoConsultarResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECAEASinMovimientoInformar");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVta"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CAEA"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMovResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FECAEASinMovResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMovimientoInformarResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FECAEAConsultar");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Periodo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Orden"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"), short.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAGetResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FECAEAGetResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAConsultarResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetCotizacion");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "MonId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECotizacionResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FECotizacionResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetCotizacionResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetTiposTributos");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FETributoResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FETributoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposTributosResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetTiposMonedas");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "MonedaResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.MonedaResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposMonedasResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetTiposIva");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "IvaTipoResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.IvaTipoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposIvaResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetTiposOpcional");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "OpcionalTipoResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.OpcionalTipoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposOpcionalResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetTiposConcepto");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ConceptoTipoResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.ConceptoTipoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposConceptoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetPtosVenta");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEPtoVentaResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FEPtoVentaResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetPtosVentaResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetTiposCbte");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteTipoResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.CbteTipoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposCbteResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetTiposDoc");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DocTipoResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.DocTipoResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposDocResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEParamGetTiposPaises");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest"), FEV1.dif.afip.gov.ar.FEAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEPaisResponse"));
        oper.setReturnClass(FEV1.dif.afip.gov.ar.FEPaisResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposPaisesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[19] = oper;

    }

    public ServiceSoap12Stub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ServiceSoap12Stub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ServiceSoap12Stub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "AlicIva");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.AlicIva.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfAlicIva");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.AlicIva[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "AlicIva");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "AlicIva");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfCbteAsoc");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.CbteAsoc[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteAsoc");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteAsoc");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfCbteTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.CbteTipo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteTipo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteTipo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfConceptoTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.ConceptoTipo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ConceptoTipo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ConceptoTipo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfDocTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.DocTipo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DocTipo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DocTipo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfErr");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Err[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Err");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Err");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfEvt");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Evt[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Evt");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Evt");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfFECAEADetRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEADetRequest[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEADetRequest");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEADetRequest");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfFECAEADetResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEADetResponse[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEADetResponse");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEADetResponse");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfFECAEASinMov");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEASinMov[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMov");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMov");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfFECAEDetRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEDetRequest[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEDetRequest");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEDetRequest");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfFECAEDetResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEDetResponse[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEDetResponse");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEDetResponse");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfIvaTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.IvaTipo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "IvaTipo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "IvaTipo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfMoneda");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Moneda[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Moneda");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Moneda");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfObs");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Obs[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Obs");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Obs");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfOpcional");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Opcional[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Opcional");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Opcional");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfOpcionalTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.OpcionalTipo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "OpcionalTipo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "OpcionalTipo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfPaisTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.PaisTipo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PaisTipo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PaisTipo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfPtoVenta");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.PtoVenta[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVenta");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVenta");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfTributo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Tributo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Tributo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Tributo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ArrayOfTributoTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.TributoTipo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "TributoTipo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "TributoTipo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteAsoc");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.CbteAsoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.CbteTipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "CbteTipoResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.CbteTipoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ConceptoTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.ConceptoTipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "ConceptoTipoResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.ConceptoTipoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Cotizacion");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Cotizacion.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DocTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.DocTipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DocTipoResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.DocTipoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "DummyResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.DummyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Err");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Err.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Evt");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Evt.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEAuthRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FEAuthRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECabRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECabRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECabResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECabResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEACabRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEACabRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEACabResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEACabResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEADetRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEADetRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEADetResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEADetResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAGet");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEAGet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAGetResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEAGetResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEARequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEARequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEAResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMov");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEASinMov.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMovConsResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEASinMovConsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMovResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEASinMovResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAECabRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAECabRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAECabResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAECabResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEDetRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEDetRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEDetResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEDetResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAERequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAERequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECAEResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompConsResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECompConsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompConsultaReq");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECompConsultaReq.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompConsultaResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECompConsultaResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECotizacionResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FECotizacionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEDetRequest");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FEDetRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEDetResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FEDetResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEPaisResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FEPaisResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEPtoVentaResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FEPtoVentaResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FERecuperaLastCbteResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FERecuperaLastCbteResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FERegXReqResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FERegXReqResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FETributoResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.FETributoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "IvaTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.IvaTipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "IvaTipoResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.IvaTipoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Moneda");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Moneda.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "MonedaResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.MonedaResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Obs");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Obs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Opcional");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Opcional.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "OpcionalTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.OpcionalTipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "OpcionalTipoResponse");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.OpcionalTipoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PaisTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.PaisTipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "PtoVenta");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.PtoVenta.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "Tributo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.Tributo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "TributoTipo");
            cachedSerQNames.add(qName);
            cls = FEV1.dif.afip.gov.ar.TributoTipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public FEV1.dif.afip.gov.ar.FECAEResponse FECAESolicitar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, FEV1.dif.afip.gov.ar.FECAERequest feCAEReq) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECAESolicitar");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAESolicitar"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, feCAEReq});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FECAEResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FECAEResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FECAEResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FERegXReqResponse FECompTotXRequest(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECompTotXRequest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompTotXRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FERegXReqResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FERegXReqResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FERegXReqResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.DummyResponse FEDummy() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEDummy");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEDummy"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.DummyResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.DummyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.DummyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FERecuperaLastCbteResponse FECompUltimoAutorizado(FEV1.dif.afip.gov.ar.FEAuthRequest auth, int ptoVta, int cbteTipo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompUltimoAutorizado"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, new java.lang.Integer(ptoVta), new java.lang.Integer(cbteTipo)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FERecuperaLastCbteResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FERecuperaLastCbteResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FERecuperaLastCbteResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FECompConsultaResponse FECompConsultar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, FEV1.dif.afip.gov.ar.FECompConsultaReq feCompConsReq) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECompConsultar");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECompConsultar"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, feCompConsReq});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FECompConsultaResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FECompConsultaResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FECompConsultaResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FECAEAResponse FECAEARegInformativo(FEV1.dif.afip.gov.ar.FEAuthRequest auth, FEV1.dif.afip.gov.ar.FECAEARequest feCAEARegInfReq) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECAEARegInformativo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEARegInformativo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, feCAEARegInfReq});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FECAEAResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FECAEAResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FECAEAResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FECAEAGetResponse FECAEASolicitar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, int periodo, short orden) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECAEASolicitar");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASolicitar"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, new java.lang.Integer(periodo), new java.lang.Short(orden)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FECAEAGetResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FECAEAGetResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FECAEAGetResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FECAEASinMovConsResponse FECAEASinMovimientoConsultar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, java.lang.String CAEA, int ptoVta) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECAEASinMovimientoConsultar");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMovimientoConsultar"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, CAEA, new java.lang.Integer(ptoVta)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FECAEASinMovConsResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FECAEASinMovConsResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FECAEASinMovConsResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FECAEASinMovResponse FECAEASinMovimientoInformar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, int ptoVta, java.lang.String CAEA) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECAEASinMovimientoInformar");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEASinMovimientoInformar"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, new java.lang.Integer(ptoVta), CAEA});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FECAEASinMovResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FECAEASinMovResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FECAEASinMovResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FECAEAGetResponse FECAEAConsultar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, int periodo, short orden) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FECAEAConsultar");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FECAEAConsultar"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, new java.lang.Integer(periodo), new java.lang.Short(orden)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FECAEAGetResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FECAEAGetResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FECAEAGetResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FECotizacionResponse FEParamGetCotizacion(FEV1.dif.afip.gov.ar.FEAuthRequest auth, java.lang.String monId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetCotizacion");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetCotizacion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, monId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FECotizacionResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FECotizacionResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FECotizacionResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FETributoResponse FEParamGetTiposTributos(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetTiposTributos");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposTributos"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FETributoResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FETributoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FETributoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.MonedaResponse FEParamGetTiposMonedas(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetTiposMonedas");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposMonedas"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.MonedaResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.MonedaResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.MonedaResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.IvaTipoResponse FEParamGetTiposIva(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetTiposIva");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposIva"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.IvaTipoResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.IvaTipoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.IvaTipoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.OpcionalTipoResponse FEParamGetTiposOpcional(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetTiposOpcional");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposOpcional"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.OpcionalTipoResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.OpcionalTipoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.OpcionalTipoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.ConceptoTipoResponse FEParamGetTiposConcepto(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetTiposConcepto");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposConcepto"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.ConceptoTipoResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.ConceptoTipoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.ConceptoTipoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FEPtoVentaResponse FEParamGetPtosVenta(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetPtosVenta");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetPtosVenta"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FEPtoVentaResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FEPtoVentaResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FEPtoVentaResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.CbteTipoResponse FEParamGetTiposCbte(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetTiposCbte");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposCbte"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.CbteTipoResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.CbteTipoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.CbteTipoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.DocTipoResponse FEParamGetTiposDoc(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetTiposDoc");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposDoc"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.DocTipoResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.DocTipoResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.DocTipoResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public FEV1.dif.afip.gov.ar.FEPaisResponse FEParamGetTiposPaises(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.FEV1/FEParamGetTiposPaises");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.FEV1/", "FEParamGetTiposPaises"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (FEV1.dif.afip.gov.ar.FEPaisResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (FEV1.dif.afip.gov.ar.FEPaisResponse) org.apache.axis.utils.JavaUtils.convert(_resp, FEV1.dif.afip.gov.ar.FEPaisResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
