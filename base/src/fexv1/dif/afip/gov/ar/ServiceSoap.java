/**
 * ServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fexv1.dif.afip.gov.ar;

public interface ServiceSoap extends java.rmi.Remote {

    /**
     * Autoriza un comprobante, devolviendo  su CAE correspondiente
     */
    public fexv1.dif.afip.gov.ar.FEXResponseAuthorize FEXAuthorize(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth, fexv1.dif.afip.gov.ar.ClsFEXRequest cmp) throws java.rmi.RemoteException;

    /**
     * Recupera los datos completos de un comprobante ya autorizado
     */
    public fexv1.dif.afip.gov.ar.FEXGetCMPResponse FEXGetCMP(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth, fexv1.dif.afip.gov.ar.ClsFEXGetCMP cmp) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de los tipos de comprobante  y su codigo
     * utilizables en servicio de autorizacion
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_Cbte_Tipo FEXGetPARAM_Cbte_Tipo(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de los tipos de exportacion  y sus codigo
     * utilizables en servicio de autorizacion
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_Tex FEXGetPARAM_Tipo_Expo(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado Incoterms  utilizables en servicio de autorizacion
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_Inc FEXGetPARAM_Incoterms(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de los idiomas  y sus codigos utilizables
     * en servicio de autorizacion
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_Idi FEXGetPARAM_Idiomas(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de las unidades de medida  y su codigo
     * utilizables en servicio de autorizacion
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_Umed FEXGetPARAM_UMed(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de paises
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_DST_pais FEXGetPARAM_DST_pais(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de las cuits de los paises de destinacion
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_DST_cuit FEXGetPARAM_DST_CUIT(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado  de monedas y su codigo utilizables en
     * servicio de autorizacion
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_Mon FEXGetPARAM_MON(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el ultimos comprobante autorizado
     */
    public fexv1.dif.afip.gov.ar.FEXResponseLast_CMP FEXGetLast_CMP(fexv1.dif.afip.gov.ar.ClsFEX_LastCMP auth) throws java.rmi.RemoteException;

    /**
     * Metodo dummy para verificacion de funcionamiento
     */
    public fexv1.dif.afip.gov.ar.DummyResponse FEXDummy() throws java.rmi.RemoteException;

    /**
     * Recupera la cotizacion de la moneda consultada y su  fecha
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_Ctz FEXGetPARAM_Ctz(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth, java.lang.String mon_id) throws java.rmi.RemoteException;

    /**
     * Recupera el ultimo ID y su  fecha
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_LastID FEXGetLast_ID(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de los puntos de venta registrados para
     * Factura electronica de exportacion - WS y su estado
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_PtoVenta FEXGetPARAM_PtoVenta(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;

    /**
     * Verifica la  existencia de la permiso/pais de destinación 
     * de embarque ingresado
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_CheckPermiso FEXCheck_Permiso(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth, java.lang.String ID_Permiso, int dst_merc) throws java.rmi.RemoteException;

    /**
     * Recupera el listado de identificadores para los campos Opcionales
     */
    public fexv1.dif.afip.gov.ar.FEXResponse_Opc FEXGetPARAM_Opcionales(fexv1.dif.afip.gov.ar.ClsFEXAuthRequest auth) throws java.rmi.RemoteException;
}
