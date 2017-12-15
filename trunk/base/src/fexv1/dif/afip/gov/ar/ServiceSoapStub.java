/**
 * ServiceSoapStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public class ServiceSoapStub extends org.apache.axis.client.Stub implements fexv1.dif.afip.gov.ar.ServiceSoap {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[17];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXAuthorize");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cmp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXRequest"), fexv1.dif.afip.gov.ar.ClsFEXRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponseAuthorize"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponseAuthorize.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXAuthorizeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetCMP");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cmp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXGetCMP"), fexv1.dif.afip.gov.ar.ClsFEXGetCMP.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetCMPResponse"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXGetCMPResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetCMPResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_Cbte_Tipo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Cbte_Tipo"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_Cbte_Tipo.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_Cbte_TipoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_Tipo_Expo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Tex"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_Tex.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_Tipo_ExpoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_Incoterms");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Inc"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_Inc.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_IncotermsResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_Idiomas");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Idi"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_Idi.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_IdiomasResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_UMed");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Umed"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_Umed.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_UMedResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_DST_pais");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_DST_pais"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_DST_pais.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_DST_paisResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_DST_CUIT");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_DST_cuit"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_DST_cuit.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_DST_CUITResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_MON");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Mon"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_Mon.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_MONResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetLast_CMP");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEX_LastCMP"), fexv1.dif.afip.gov.ar.ClsFEX_LastCMP.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponseLast_CMP"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponseLast_CMP.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetLast_CMPResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXDummy");
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "DummyResponse"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.DummyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXDummyResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_Ctz");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Mon_id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Ctz"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_Ctz.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_CtzResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetLast_ID");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_LastID"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_LastID.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetLast_IDResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_PtoVenta");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_PtoVenta"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_PtoVenta.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_PtoVentaResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXCheck_Permiso");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ID_Permiso"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Dst_merc"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_CheckPermiso"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_CheckPermiso.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXCheck_PermisoResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FEXGetPARAM_Opcionales");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest"), fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Opc"));
        oper.setReturnClass(fexv1.dif.afip.gov.ar.FEXResponse_Opc.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_OpcionalesResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

    }

    public ServiceSoapStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ServiceSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ServiceSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_Cbte_Tipo");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Cbte_Tipo[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Cbte_Tipo");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Cbte_Tipo");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_DST_cuit");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_DST_cuit[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_DST_cuit");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_DST_cuit");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_DST_pais");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_DST_pais[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_DST_pais");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_DST_pais");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_Idi");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Idi[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Idi");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Idi");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_Inc");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Inc[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Inc");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Inc");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_Mon");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Mon[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Mon");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Mon");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_Opc");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Opc[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Opc");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Opc");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_PtoVenta");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_PtoVenta[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_PtoVenta");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_PtoVenta");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_Tex");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Tex[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Tex");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Tex");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfClsFEXResponse_UMed");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_UMed[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_UMed");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_UMed");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfCmp_asoc");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.Cmp_asoc[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cmp_asoc");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cmp_asoc");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfItem");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.Item[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Item");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Item");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfOpcional");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.Opcional[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opcional");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opcional");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ArrayOfPermiso");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.Permiso[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Permiso");
            qName2 = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Permiso");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEX_LastCMP");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEX_LastCMP.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEX_LastCMP_Response");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEX_LastCMP_Response.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXAuthRequest");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXAuthRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXErr");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXErr.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXEvents");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXEvents.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXGetCMP");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXGetCMP.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXGetCMPR");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXGetCMPR.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXOutAuthorize");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXOutAuthorize.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXRequest");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Cbte_Tipo");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Cbte_Tipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_CheckPermiso");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_CheckPermiso.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Ctz");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Ctz.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_DST_cuit");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_DST_cuit.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_DST_pais");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_DST_pais.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Idi");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Idi.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Inc");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Inc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_LastID");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_LastID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Mon");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Mon.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Opc");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Opc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_PtoVenta");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_PtoVenta.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_Tex");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_Tex.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "ClsFEXResponse_UMed");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.ClsFEXResponse_UMed.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Cmp_asoc");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.Cmp_asoc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "DummyResponse");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.DummyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetCMPResponse");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXGetCMPResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Cbte_Tipo");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_Cbte_Tipo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_CheckPermiso");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_CheckPermiso.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Ctz");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_Ctz.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_DST_cuit");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_DST_cuit.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_DST_pais");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_DST_pais.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Idi");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_Idi.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Inc");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_Inc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_LastID");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_LastID.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Mon");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_Mon.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Opc");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_Opc.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_PtoVenta");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_PtoVenta.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Tex");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_Tex.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponse_Umed");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponse_Umed.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponseAuthorize");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponseAuthorize.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXResponseLast_CMP");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.FEXResponseLast_CMP.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Item");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.Item.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Opcional");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.Opcional.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "Permiso");
            cachedSerQNames.add(qName);
            cls = fexv1.dif.afip.gov.ar.Permiso.class;
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

    public fexv1.dif.afip.gov.ar.FEXResponseAuthorize FEXAuthorize(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth, fexv1.dif.afip.gov.ar.ClsFEXRequest cmp) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXAuthorize");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXAuthorize"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, cmp});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponseAuthorize) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponseAuthorize) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponseAuthorize.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXGetCMPResponse FEXGetCMP(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth, fexv1.dif.afip.gov.ar.ClsFEXGetCMP cmp) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetCMP");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetCMP"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, cmp});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXGetCMPResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXGetCMPResponse) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXGetCMPResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_Cbte_Tipo FEXGetPARAM_Cbte_Tipo(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_Cbte_Tipo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_Cbte_Tipo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Cbte_Tipo) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Cbte_Tipo) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_Cbte_Tipo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_Tex FEXGetPARAM_Tipo_Expo(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_Tipo_Expo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_Tipo_Expo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Tex) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Tex) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_Tex.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_Inc FEXGetPARAM_Incoterms(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_Incoterms");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_Incoterms"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Inc) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Inc) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_Inc.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_Idi FEXGetPARAM_Idiomas(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_Idiomas");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_Idiomas"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Idi) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Idi) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_Idi.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_Umed FEXGetPARAM_UMed(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_UMed");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_UMed"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Umed) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Umed) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_Umed.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_DST_pais FEXGetPARAM_DST_pais(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_DST_pais");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_DST_pais"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_DST_pais) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_DST_pais) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_DST_pais.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_DST_cuit FEXGetPARAM_DST_CUIT(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_DST_CUIT");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_DST_CUIT"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_DST_cuit) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_DST_cuit) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_DST_cuit.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_Mon FEXGetPARAM_MON(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_MON");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_MON"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Mon) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Mon) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_Mon.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponseLast_CMP FEXGetLast_CMP(fexv1.dif.afip.gov.ar.ClsFEX_LastCMP auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetLast_CMP");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetLast_CMP"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponseLast_CMP) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponseLast_CMP) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponseLast_CMP.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.DummyResponse FEXDummy() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXDummy");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXDummy"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.DummyResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.DummyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.DummyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_Ctz FEXGetPARAM_Ctz(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth, java.lang.String mon_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_Ctz");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_Ctz"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, mon_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Ctz) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Ctz) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_Ctz.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_LastID FEXGetLast_ID(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetLast_ID");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetLast_ID"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_LastID) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_LastID) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_LastID.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_PtoVenta FEXGetPARAM_PtoVenta(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_PtoVenta");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_PtoVenta"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_PtoVenta) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_PtoVenta) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_PtoVenta.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_CheckPermiso FEXCheck_Permiso(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth, java.lang.String ID_Permiso, int dst_merc) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXCheck_Permiso");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXCheck_Permiso"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth, ID_Permiso, new java.lang.Integer(dst_merc)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_CheckPermiso) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_CheckPermiso) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_CheckPermiso.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fexv1.dif.afip.gov.ar.FEXResponse_Opc FEXGetPARAM_Opcionales(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ar.gov.afip.dif.fexv1/FEXGetPARAM_Opcionales");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://ar.gov.afip.dif.fexv1/", "FEXGetPARAM_Opcionales"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {auth});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Opc) _resp;
            } catch (java.lang.Exception _exception) {
                return (fexv1.dif.afip.gov.ar.FEXResponse_Opc) org.apache.axis.utils.JavaUtils.convert(_resp, fexv1.dif.afip.gov.ar.FEXResponse_Opc.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
