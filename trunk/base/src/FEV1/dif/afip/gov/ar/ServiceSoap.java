/**
 * ServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package FEV1.dif.afip.gov.ar;

public interface ServiceSoap extends java.rmi.Remote {

    /**
     * Solicitud de Código de Autorización Electrónico (CAE)
     */
    public FEV1.dif.afip.gov.ar.FECAEResponse FECAESolicitar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, FEV1.dif.afip.gov.ar.FECAERequest feCAEReq) throws java.rmi.RemoteException;

    /**
     * Retorna la cantidad maxima de registros que puede tener una
     * invocacion al metodo FECAESolicitar / FECAEARegInformativo
     */
    public FEV1.dif.afip.gov.ar.FERegXReqResponse FECompTotXRequest(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Metodo dummy para verificacion de funcionamiento
     */
    public FEV1.dif.afip.gov.ar.DummyResponse FEDummy() throws java.rmi.RemoteException;

    /**
     * Retorna el ultimo comprobante autorizado para el tipo de comprobante
     * / cuit / punto de venta ingresado / Tipo de Emisión
     */
    public FEV1.dif.afip.gov.ar.FERecuperaLastCbteResponse FECompUltimoAutorizado(FEV1.dif.afip.gov.ar.FEAuthRequest auth, int ptoVta, int cbteTipo) throws java.rmi.RemoteException;

    /**
     * Consulta Comprobante emitido y su código.
     */
    public FEV1.dif.afip.gov.ar.FECompConsultaResponse FECompConsultar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, FEV1.dif.afip.gov.ar.FECompConsultaReq feCompConsReq) throws java.rmi.RemoteException;

    /**
     * Rendición de comprobantes asociados a un CAEA.
     */
    public FEV1.dif.afip.gov.ar.FECAEAResponse FECAEARegInformativo(FEV1.dif.afip.gov.ar.FEAuthRequest auth, FEV1.dif.afip.gov.ar.FECAEARequest feCAEARegInfReq) throws java.rmi.RemoteException;

    /**
     * Solicitud de Código de Autorización Electrónico Anticipado
     * (CAEA)
     */
    public FEV1.dif.afip.gov.ar.FECAEAGetResponse FECAEASolicitar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, int periodo, short orden) throws java.rmi.RemoteException;

    /**
     * Consulta CAEA informado como sin movimientos.
     */
    public FEV1.dif.afip.gov.ar.FECAEASinMovConsResponse FECAEASinMovimientoConsultar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, java.lang.String CAEA, int ptoVta) throws java.rmi.RemoteException;

    /**
     * Informa CAEA sin movimientos.
     */
    public FEV1.dif.afip.gov.ar.FECAEASinMovResponse FECAEASinMovimientoInformar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, int ptoVta, java.lang.String CAEA) throws java.rmi.RemoteException;

    /**
     * Consultar CAEA emitidos.
     */
    public FEV1.dif.afip.gov.ar.FECAEAGetResponse FECAEAConsultar(FEV1.dif.afip.gov.ar.FEAuthRequest auth, int periodo, short orden) throws java.rmi.RemoteException;

    /**
     * Recupera la cotizacion de la moneda consultada y su  fecha
     */
    public FEV1.dif.afip.gov.ar.FECotizacionResponse FEParamGetCotizacion(FEV1.dif.afip.gov.ar.FEAuthRequest auth, java.lang.String monId) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de los diferente tributos que pueden ser
     * utilizados  en el servicio de autorizacion
     */
    public FEV1.dif.afip.gov.ar.FETributoResponse FEParamGetTiposTributos(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de monedas utilizables en servicio de autorización
     */
    public FEV1.dif.afip.gov.ar.MonedaResponse FEParamGetTiposMonedas(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado  de Tipos de Iva utilizables en servicio
     * de autorización.
     */
    public FEV1.dif.afip.gov.ar.IvaTipoResponse FEParamGetTiposIva(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de identificadores para los campos Opcionales
     */
    public FEV1.dif.afip.gov.ar.OpcionalTipoResponse FEParamGetTiposOpcional(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado  de identificadores para el campo Concepto.
     */
    public FEV1.dif.afip.gov.ar.ConceptoTipoResponse FEParamGetTiposConcepto(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de puntos de venta registrados y su estado
     */
    public FEV1.dif.afip.gov.ar.FEPtoVentaResponse FEParamGetPtosVenta(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado  de Tipos de Comprobantes utilizables en
     * servicio de autorización.
     */
    public FEV1.dif.afip.gov.ar.CbteTipoResponse FEParamGetTiposCbte(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado  de Tipos de Documentos utilizables en
     * servicio de autorización.
     */
    public FEV1.dif.afip.gov.ar.DocTipoResponse FEParamGetTiposDoc(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de los diferente paises que pueden ser
     * utilizados  en el servicio de autorizacion
     */
    public FEV1.dif.afip.gov.ar.FEPaisResponse FEParamGetTiposPaises(FEV1.dif.afip.gov.ar.FEAuthRequest auth) throws java.rmi.RemoteException;
}
