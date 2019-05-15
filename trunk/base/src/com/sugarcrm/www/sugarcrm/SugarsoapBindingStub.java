/**
 * SugarsoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class SugarsoapBindingStub extends org.apache.axis.client.Stub implements com.sugarcrm.www.sugarcrm.SugarsoapPortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[27];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("login");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "user_auth"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "user_auth"), com.sugarcrm.www.sugarcrm.User_auth.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "application_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "name_value_list"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value_list"), com.sugarcrm.www.sugarcrm.Name_value[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "entry_value"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Entry_value.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("logout");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_entry");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "select_fields"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "link_name_to_fields_array"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_names_to_fields_array"), com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "track_view"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_result_version2"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Get_entry_result_version2.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_entries");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ids"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "select_fields"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "link_name_to_fields_array"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_names_to_fields_array"), com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "track_view"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_result_version2"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Get_entry_result_version2.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_entry_list");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "query"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "order_by"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "offset"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "select_fields"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "link_name_to_fields_array"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_names_to_fields_array"), com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "deleted"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "favorites"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_list_result_version2"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("set_relationship");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "link_field_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "related_ids"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "name_value_list"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value_list"), com.sugarcrm.www.sugarcrm.Name_value[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "delete"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_relationship_list_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_set_relationship_list_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("set_relationships");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_names"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_ids"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "link_field_names"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "related_ids"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_relationhip_ids"), java.lang.String[][].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "name_value_lists"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value_lists"), com.sugarcrm.www.sugarcrm.Name_value[][].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "delete_array"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "deleted_array"), int[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_relationship_list_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_set_relationship_list_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_relationships");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "link_field_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "related_module_query"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "related_fields"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "related_module_link_name_to_fields_array"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_names_to_fields_array"), com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "deleted"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "order_by"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "offset"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "limit"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_result_version2"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Get_entry_result_version2.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("set_entry");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "name_value_list"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value_list"), com.sugarcrm.www.sugarcrm.Name_value[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_entry_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_set_entry_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("set_entries");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "name_value_lists"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value_lists"), com.sugarcrm.www.sugarcrm.Name_value[][].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_entries_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_set_entries_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_server_info");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_server_info_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Get_server_info_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_user_id");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_module_fields");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "fields"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_module_fields"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_module_fields.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("seamless_login");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        oper.setReturnClass(int.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("set_note_attachment");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "note"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_note_attachment"), com.sugarcrm.www.sugarcrm.New_note_attachment.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_entry_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_set_entry_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_note_attachment");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_return_note_attachment"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_return_note_attachment.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("set_document_revision");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "note"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "document_revision"), com.sugarcrm.www.sugarcrm.Document_revision.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_entry_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_set_entry_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_document_revision");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "i"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_return_document_revision"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.New_return_document_revision.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("search_by_module");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "search_string"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "modules"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "offset"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "assigned_user_id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "select_fields"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "unified_search_only"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "favorites"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "return_search_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Return_search_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_available_modules");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "module_list"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Module_list.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_user_team_id");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("set_campaign_merge");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "targets"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "campaign_id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_entries_count");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "query"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "deleted"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entries_count_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Get_entries_count_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_module_fields_md5");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_names"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "md5_results"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_last_viewed");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_names"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "module_names"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "last_viewed_list"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Last_viewed_entry[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_upcoming_activities");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "upcoming_activities_list"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Upcoming_activity_entry[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("get_modified_relationships");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "related_module"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "from_date"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "to_date"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "offset"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "max_results"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "deleted"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "module_user_id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "select_fields"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "relationship_name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "deletion_date"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "modified_relationship_result"));
        oper.setReturnClass(com.sugarcrm.www.sugarcrm.Modified_relationship_result.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[26] = oper;

    }

    public SugarsoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public SugarsoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public SugarsoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "acl_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Acl_list_entry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "acl_list_entry");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "acl_list_entry");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Acl_list_entry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "deleted_array");
            cachedSerQNames.add(qName);
            cls = int[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "document_revision");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Document_revision.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "entry_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Entry_value[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "entry_value");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "entry_value");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Entry_value.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "error_value");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Error_value.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "field");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Field.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "field_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Field[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "field");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entries_count_result");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Get_entries_count_result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_list_result_version2");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_result_version2");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Get_entry_result_version2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_server_info_result");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Get_server_info_result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "last_viewed_entry");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Last_viewed_entry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "last_viewed_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Last_viewed_entry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "last_viewed_entry");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_array_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_value2[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_value2");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_field");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_field.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_field_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_field[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_field");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_name_value[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_name_value");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_list2");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_list2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_lists");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_list2[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_list2");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_name_to_fields_array");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_name_to_fields_array.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_name_value");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_name_value.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_names_to_fields_array");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_name_to_fields_array");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_value");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Name_value[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_value2");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Link_value2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "md5_results");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "modified_relationship_entry");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Modified_relationship_entry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "modified_relationship_entry_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Modified_relationship_entry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "modified_relationship_entry");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "modified_relationship_result");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Modified_relationship_result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "module_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Module_list.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "module_list_array");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Module_list_entry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "module_list_entry");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "module_list_entry");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Module_list_entry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "module_names");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Name_value.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Name_value[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value_lists");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Name_value[][].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "name_value_list");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_module_fields");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.New_module_fields.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_note_attachment");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.New_note_attachment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_return_document_revision");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.New_return_document_revision.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_return_note_attachment");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.New_return_note_attachment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_entries_result");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.New_set_entries_result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_entry_result");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.New_set_entry_result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_relationhip_ids");
            cachedSerQNames.add(qName);
            cls = java.lang.String[][].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "new_set_relationship_list_result");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.New_set_relationship_list_result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "return_search_result");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Return_search_result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "search_link_array_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Name_value[][].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "link_value");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "search_link_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Search_link_name_value[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "search_link_name_value");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "search_link_name_value");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Search_link_name_value.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "select_fields");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "upcoming_activities_list");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Upcoming_activity_entry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "upcoming_activity_entry");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "upcoming_activity_entry");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.Upcoming_activity_entry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "user_auth");
            cachedSerQNames.add(qName);
            cls = com.sugarcrm.www.sugarcrm.User_auth.class;
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
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
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

    public com.sugarcrm.www.sugarcrm.Entry_value login(com.sugarcrm.www.sugarcrm.User_auth user_auth, java.lang.String application_name, com.sugarcrm.www.sugarcrm.Name_value[] name_value_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/login");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "login"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {user_auth, application_name, name_value_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Entry_value) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Entry_value) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Entry_value.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void logout(java.lang.String session) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/logout");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "logout"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Get_entry_result_version2 get_entry(java.lang.String session, java.lang.String module_name, java.lang.String id, java.lang.String[] select_fields, com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[] link_name_to_fields_array, boolean track_view) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_entry");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, id, select_fields, link_name_to_fields_array, new java.lang.Boolean(track_view)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Get_entry_result_version2) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Get_entry_result_version2) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Get_entry_result_version2.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Get_entry_result_version2 get_entries(java.lang.String session, java.lang.String module_name, java.lang.String[] ids, java.lang.String[] select_fields, com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[] link_name_to_fields_array, boolean track_view) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_entries");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entries"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, ids, select_fields, link_name_to_fields_array, new java.lang.Boolean(track_view)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Get_entry_result_version2) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Get_entry_result_version2) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Get_entry_result_version2.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2 get_entry_list(java.lang.String session, java.lang.String module_name, java.lang.String query, java.lang.String order_by, int offset, java.lang.String[] select_fields, com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[] link_name_to_fields_array, int max_results, int deleted, boolean favorites) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_entry_list");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_list"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, query, order_by, new java.lang.Integer(offset), select_fields, link_name_to_fields_array, new java.lang.Integer(max_results), new java.lang.Integer(deleted), new java.lang.Boolean(favorites)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_set_relationship_list_result set_relationship(java.lang.String session, java.lang.String module_name, java.lang.String module_id, java.lang.String link_field_name, java.lang.String[] related_ids, com.sugarcrm.www.sugarcrm.Name_value[] name_value_list, int delete) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/set_relationship");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "set_relationship"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, module_id, link_field_name, related_ids, name_value_list, new java.lang.Integer(delete)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_set_relationship_list_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_set_relationship_list_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_set_relationship_list_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_set_relationship_list_result set_relationships(java.lang.String session, java.lang.String[] module_names, java.lang.String[] module_ids, java.lang.String[] link_field_names, java.lang.String[][] related_ids, com.sugarcrm.www.sugarcrm.Name_value[][] name_value_lists, int[] delete_array) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/set_relationships");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "set_relationships"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_names, module_ids, link_field_names, related_ids, name_value_lists, delete_array});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_set_relationship_list_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_set_relationship_list_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_set_relationship_list_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Get_entry_result_version2 get_relationships(java.lang.String session, java.lang.String module_name, java.lang.String module_id, java.lang.String link_field_name, java.lang.String related_module_query, java.lang.String[] related_fields, com.sugarcrm.www.sugarcrm.Link_name_to_fields_array[] related_module_link_name_to_fields_array, int deleted, java.lang.String order_by, int offset, int limit) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_relationships");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_relationships"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, module_id, link_field_name, related_module_query, related_fields, related_module_link_name_to_fields_array, new java.lang.Integer(deleted), order_by, new java.lang.Integer(offset), new java.lang.Integer(limit)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Get_entry_result_version2) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Get_entry_result_version2) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Get_entry_result_version2.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_set_entry_result set_entry(java.lang.String session, java.lang.String module_name, com.sugarcrm.www.sugarcrm.Name_value[] name_value_list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/set_entry");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "set_entry"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, name_value_list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_set_entry_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_set_entry_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_set_entry_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_set_entries_result set_entries(java.lang.String session, java.lang.String module_name, com.sugarcrm.www.sugarcrm.Name_value[][] name_value_lists) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/set_entries");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "set_entries"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, name_value_lists});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_set_entries_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_set_entries_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_set_entries_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Get_server_info_result get_server_info() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_server_info");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_server_info"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Get_server_info_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Get_server_info_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Get_server_info_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String get_user_id(java.lang.String session) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_user_id");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_user_id"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_module_fields get_module_fields(java.lang.String session, java.lang.String module_name, java.lang.String[] fields) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_module_fields");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_module_fields"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, fields});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_module_fields) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_module_fields) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_module_fields.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public int seamless_login(java.lang.String session) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/seamless_login");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "seamless_login"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Integer) _resp).intValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_set_entry_result set_note_attachment(java.lang.String session, com.sugarcrm.www.sugarcrm.New_note_attachment note) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/set_note_attachment");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "set_note_attachment"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, note});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_set_entry_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_set_entry_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_set_entry_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_return_note_attachment get_note_attachment(java.lang.String session, java.lang.String id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_note_attachment");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_note_attachment"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_return_note_attachment) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_return_note_attachment) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_return_note_attachment.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_set_entry_result set_document_revision(java.lang.String session, com.sugarcrm.www.sugarcrm.Document_revision note) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/set_document_revision");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "set_document_revision"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, note});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_set_entry_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_set_entry_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_set_entry_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.New_return_document_revision get_document_revision(java.lang.String session, java.lang.String i) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_document_revision");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_document_revision"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, i});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.New_return_document_revision) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.New_return_document_revision) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.New_return_document_revision.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Return_search_result search_by_module(java.lang.String session, java.lang.String search_string, java.lang.String[] modules, int offset, int max_results, java.lang.String assigned_user_id, java.lang.String[] select_fields, boolean unified_search_only, boolean favorites) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/search_by_module");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "search_by_module"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, search_string, modules, new java.lang.Integer(offset), new java.lang.Integer(max_results), assigned_user_id, select_fields, new java.lang.Boolean(unified_search_only), new java.lang.Boolean(favorites)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Return_search_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Return_search_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Return_search_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Module_list get_available_modules(java.lang.String session, java.lang.String filter) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_available_modules");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_available_modules"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, filter});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Module_list) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Module_list) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Module_list.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String get_user_team_id(java.lang.String session) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_user_team_id");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_user_team_id"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void set_campaign_merge(java.lang.String session, java.lang.String[] targets, java.lang.String campaign_id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/set_campaign_merge");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "set_campaign_merge"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, targets, campaign_id});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Get_entries_count_result get_entries_count(java.lang.String session, java.lang.String module_name, java.lang.String query, int deleted) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_entries_count");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entries_count"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, query, new java.lang.Integer(deleted)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Get_entries_count_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Get_entries_count_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Get_entries_count_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String[] get_module_fields_md5(java.lang.String session, java.lang.String[] module_names) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_module_fields_md5");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_module_fields_md5"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_names});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Last_viewed_entry[] get_last_viewed(java.lang.String session, java.lang.String[] module_names) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_last_viewed");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_last_viewed"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_names});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Last_viewed_entry[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Last_viewed_entry[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Last_viewed_entry[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Upcoming_activity_entry[] get_upcoming_activities(java.lang.String session) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_upcoming_activities");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_upcoming_activities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Upcoming_activity_entry[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Upcoming_activity_entry[]) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Upcoming_activity_entry[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.sugarcrm.www.sugarcrm.Modified_relationship_result get_modified_relationships(java.lang.String session, java.lang.String module_name, java.lang.String related_module, java.lang.String from_date, java.lang.String to_date, int offset, int max_results, int deleted, java.lang.String module_user_id, java.lang.String[] select_fields, java.lang.String relationship_name, java.lang.String deletion_date) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://suitecrmtest.hipertehuelche.com/service/v4_1/soap.php/get_modified_relationships");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_modified_relationships"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {session, module_name, related_module, from_date, to_date, new java.lang.Integer(offset), new java.lang.Integer(max_results), new java.lang.Integer(deleted), module_user_id, select_fields, relationship_name, deletion_date});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.sugarcrm.www.sugarcrm.Modified_relationship_result) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.sugarcrm.www.sugarcrm.Modified_relationship_result) org.apache.axis.utils.JavaUtils.convert(_resp, com.sugarcrm.www.sugarcrm.Modified_relationship_result.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
