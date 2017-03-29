#!/usr/bin/python
# -*- coding: latin-1 -*-
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by the
# Free Software Foundation; either version 3, or (at your option) any later
# version.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTIBILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.

"""Módulo para obtener CAE, código de autorización de impresión o electrónico, 
del web service WSFEXv1 de AFIP (Factura Electrónica Exportación Versión 1)
según RG2758/2010 (Registros Especiales Aduaneros) y RG3689/14 (servicios)
http://www.sistemasagiles.com.ar/trac/wiki/FacturaElectronicaExportacion
"""

__author__ = "Mariano Reingart (reingart@gmail.com)"
__copyright__ = "Copyright (C) 2011-2015 Mariano Reingart"
__license__ = "GPL 3.0"
__version__ = "1.08f"

import datetime
import decimal
import os
import sys
import codecs
from shutil import copyfile
from chardet.universaldetector import UniversalDetector
from utils import inicializar_y_capturar_excepciones, BaseWS, get_install_dir

detector = UniversalDetector()
LOG_FOLDER="log"
HOMO = True
WSDL="https://wswhomo.afip.gov.ar/wsfexv1/service.asmx?WSDL"

def get_encoding_type(current_file):
    detector.reset()
    for line in file(current_file):
        detector.feed(line)
        if detector.done: break
    detector.close()
    return detector.result['encoding']

class WSFEXv1(BaseWS):
    "Interfaz para el WebService de Factura Electrónica Exportación Versión 1"
    _public_methods_ = ['CrearFactura', 'AgregarItem', 'Authorize', 'GetCMP',
                        'AgregarPermiso', 'AgregarCmpAsoc',
                        'GetParamMon', 'GetParamTipoCbte', 'GetParamTipoExpo', 
                        'GetParamIdiomas', 'GetParamUMed', 'GetParamIncoterms', 
                        'GetParamDstPais','GetParamDstCUIT', 'GetParamIdiomas',
                        'GetParamIncoterms', 'GetParamDstCUIT',
                        'GetParamPtosVenta', 'GetParamCtz', 'LoadTestXML',
                        'AnalizarXml', 'ObtenerTagXml', 'DebugLog', 
                        'SetParametros', 'SetTicketAcceso', 'GetParametro',
                        'GetLastCMP', 'GetLastID',
                        'Dummy', 'Conectar', 'SetTicketAcceso']
    _public_attrs_ = ['Token', 'Sign', 'Cuit', 
        'AppServerStatus', 'DbServerStatus', 'AuthServerStatus', 
        'XmlRequest', 'XmlResponse', 'Version',
        'Resultado', 'Obs', 'Reproceso',
        'CAE','Vencimiento', 'Eventos', 'ErrCode', 'ErrMsg', 'FchVencCAE',
        'Excepcion', 'LanzarExcepciones', 'Traceback', "InstallDir",
        'PuntoVenta', 'CbteNro', 'FechaCbte', 'ImpTotal']
        
    _reg_progid_ = "WSFEXv1"
    _reg_clsid_ = "{8106F039-D132-4F87-8AFE-ADE47B5503D4}"

    # Variables globales para BaseWS:
    HOMO = HOMO
    WSDL = WSDL
    Version = "%s %s" % (__version__, HOMO and 'Homologación' or '')
    factura = None

    def inicializar(self):
        BaseWS.inicializar(self)
        self.AppServerStatus = self.DbServerStatus = self.AuthServerStatus = None
        self.Resultado = self.Motivo = self.Reproceso = ''
        self.LastID = self.LastCMP = self.CAE = self.Vencimiento = ''
        self.CbteNro = self.FechaCbte = self.PuntoVenta = self.ImpTotal = None
        self.InstallDir = INSTALL_DIR
        self.FchVencCAE = ""              # retrocompatibilidad

    def __analizar_errores(self, ret):
        "Comprueba y extrae errores si existen en la respuesta XML"
        if 'FEXErr' in ret:
            errores = [ret['FEXErr']]
            for error in errores:
                self.Errores.append("%s: %s" % (
                    error['ErrCode'],
                    error['ErrMsg'],
                    ))
            self.ErrCode = ' '.join([str(error['ErrCode']) for error in errores])
            self.ErrMsg = '\n'.join(self.Errores)
        if 'FEXEvents' in ret:
            events = [ret['FEXEvents']]
            self.Eventos = ['%s: %s' % (evt['EventCode'], evt.get('EventMsg',"")) for evt in events]
        
    def CrearFactura(self, tipo_cbte=19, punto_vta=1, cbte_nro=0, fecha_cbte=None,
            imp_total=0.0, tipo_expo=1, permiso_existente="N", pais_dst_cmp=None,
            nombre_cliente="", cuit_pais_cliente="", domicilio_cliente="",
            id_impositivo="", moneda_id="PES", moneda_ctz=1.0,
            obs_comerciales="", obs_generales="", forma_pago="", incoterms="", 
            idioma_cbte=7, incoterms_ds=None, **kwargs):
        "Creo un objeto factura (interna)"
        # Creo una factura electronica de exportación 

        fact = {'tipo_cbte': tipo_cbte, 'punto_vta': punto_vta,
                'cbte_nro': cbte_nro, 'fecha_cbte': fecha_cbte,
                'tipo_doc': 80, 'nro_doc':  cuit_pais_cliente,
                'imp_total': imp_total, 
                'permiso_existente': permiso_existente, 
                'pais_dst_cmp': pais_dst_cmp,
                'nombre_cliente': nombre_cliente,
                'domicilio_cliente': domicilio_cliente,
                'id_impositivo': id_impositivo,
                'moneda_id': moneda_id, 'moneda_ctz': moneda_ctz,
                'obs_comerciales': obs_comerciales,
                'obs_generales': obs_generales,
                'forma_pago': forma_pago,
                'incoterms': incoterms,
                'incoterms_ds': incoterms_ds,
                'tipo_expo': tipo_expo,
                'idioma_cbte': idioma_cbte,
                'cbtes_asoc': [],
                'permisos': [],
                'detalles': [],
            }
        self.factura = fact

        return True
    
    def AgregarItem(self, codigo, ds, qty, umed, precio, importe, bonif=None, **kwargs):
        "Agrego un item a una factura (interna)"
        # Nota: no se calcula total (debe venir calculado!)
        self.factura['detalles'].append({
                'codigo': codigo,                
                'ds': ds,
                'qty': qty,
                'umed': umed,
                'precio': precio,
                'bonif': bonif,
                'importe': importe,
                })
        return True
       
    def AgregarPermiso(self, id_permiso, dst_merc, **kwargs):
        "Agrego un permiso a una factura (interna)"
        self.factura['permisos'].append({
                'id_permiso': id_permiso,
                'dst_merc': dst_merc,
                })        
        return True
        
    def AgregarCmpAsoc(self, cbte_tipo=19, cbte_punto_vta=0, cbte_nro=0, cbte_cuit=None, **kwargs):
        "Agrego un comprobante asociado a una factura (interna)"
        self.factura['cbtes_asoc'].append({
            'cbte_tipo': cbte_tipo, 'cbte_punto_vta': cbte_punto_vta, 
            'cbte_nro': cbte_nro, 'cbte_cuit': cbte_cuit})
        return True

    @inicializar_y_capturar_excepciones
    def Authorize(self, id):
        "Autoriza la factura cargada en memoria"
        f = self.factura
        ret = self.client.FEXAuthorize(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit},
            Cmp={
                'Id': id,
                'Fecha_cbte': f['fecha_cbte'],
                'Cbte_Tipo': f['tipo_cbte'],
                'Punto_vta': f['punto_vta'],
                'Cbte_nro': f['cbte_nro'],
                'Tipo_expo': f['tipo_expo'],
                'Permiso_existente': f['permiso_existente'],
                'Permisos': f['permisos'] and [
                    {'Permiso': {
                        'Id_permiso': p['id_permiso'],
                        'Dst_merc': p['dst_merc'],
                    }} for p in f['permisos']] or None,
                'Dst_cmp': f['pais_dst_cmp'],
                'Cliente': f['nombre_cliente'],
                'Cuit_pais_cliente': f['nro_doc'],
                'Domicilio_cliente': f['domicilio_cliente'],
                'Id_impositivo': f['id_impositivo'],
                'Moneda_Id': f['moneda_id'],
                'Moneda_ctz': f['moneda_ctz'],                
                'Obs_comerciales': f['obs_comerciales'],
                'Imp_total': f['imp_total'],
                'Obs': f['obs_generales'],
                'Cmps_asoc': f['cbtes_asoc'] and [
                    {'Cmp_asoc': {
                        'Cbte_tipo': c['cbte_tipo'],
                        'Cbte_punto_vta': c['cbte_punto_vta'],
                        'Cbte_nro': c['cbte_nro'],
                        'Cbte_cuit': c['cbte_cuit'],
                    }} for c in f['cbtes_asoc']] or None,
                'Forma_pago': f['forma_pago'],
                'Incoterms': f['incoterms'],
                'Incoterms_Ds': f['incoterms_ds'],
                'Idioma_cbte':  f['idioma_cbte'],
                'Items': [
                    {'Item': {
                        'Pro_codigo': d['codigo'],
                        'Pro_ds': d['ds'],
                        'Pro_qty': d['qty'],
                        'Pro_umed': d['umed'],
                        'Pro_precio_uni': d['precio'],
                        'Pro_bonificacion': d['bonif'],
                        'Pro_total_item': d['importe'],
                     }} for d in f['detalles']],                    
            })

        result = ret['FEXAuthorizeResult']
        self.__analizar_errores(result)
        if 'FEXResultAuth' in result:
            auth = result['FEXResultAuth']
            # Resultado: A: Aceptado, R: Rechazado
            self.Resultado = auth.get('Resultado', "")
            # Obs:
            self.Obs = auth.get('Motivos_Obs', "")
            self.Reproceso = auth.get('Reproceso', "")
            self.CAE = auth.get('Cae', "")
            self.CbteNro  = auth.get('Cbte_nro', "")
            vto = str(auth.get('Fch_venc_Cae', ""))
            self.FchVencCAE = vto
            self.Vencimiento = "%s/%s/%s" % (vto[6:8], vto[4:6], vto[0:4])
            return self.CAE

    @inicializar_y_capturar_excepciones
    def Dummy(self):
        "Obtener el estado de los servidores de la AFIP"
        result = self.client.FEXDummy()['FEXDummyResult']
        self.__analizar_errores(result)
        self.AppServerStatus = str(result.get('AppServer', ''))
        self.DbServerStatus = str(result.get('DbServer', ''))
        self.AuthServerStatus = str(result.get('AuthServer', ''))
        return True

    @inicializar_y_capturar_excepciones
    def GetCMP(self, tipo_cbte, punto_vta, cbte_nro):
        "Recuperar los datos completos de un comprobante ya autorizado"
        ret = self.client.FEXGetCMP(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit},
            Cmp={
                'Cbte_tipo': tipo_cbte,
                'Punto_vta': punto_vta,
                'Cbte_nro': cbte_nro,
            })
        result = ret['FEXGetCMPResult']
        self.__analizar_errores(result)
        if 'FEXResultGet' in result:
            resultget = result['FEXResultGet']
            # Obs, cae y fecha cae
            self.Obs = resultget.get('Obs') and resultget['Obs'].strip(" ") or ''
            self.CAE = resultget.get('Cae', '')
            vto = str(resultget.get('Fch_venc_Cae', ''))
            self.Vencimiento = "%s/%s/%s" % (vto[6:8], vto[4:6], vto[0:4])
            self.FechaCbte = resultget.get('Fecha_cbte', '') #.strftime("%Y/%m/%d")
            self.PuntoVenta = resultget['Punto_vta'] # 4000
            self.Resultado = resultget.get('Resultado', '')
            self.CbteNro =resultget['Cbte_nro']
            self.ImpTotal = str(resultget['Imp_total'])
            return self.CAE
        else:
            return 0
    
    @inicializar_y_capturar_excepciones
    def GetLastCMP(self, tipo_cbte, punto_vta):
        "Recuperar último número de comprobante emitido"
        ret = self.client.FEXGetLast_CMP(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit,
                  'Cbte_Tipo': tipo_cbte,
                  'Pto_venta': punto_vta,
            })
        result = ret['FEXGetLast_CMPResult']
        self.__analizar_errores(result)
        if 'FEXResult_LastCMP' in result:
            resultget = result['FEXResult_LastCMP']
            self.CbteNro =resultget.get('Cbte_nro')
            self.FechaCbte = resultget.get('Cbte_fecha') #.strftime("%Y/%m/%d")
            return self.CbteNro
            
    @inicializar_y_capturar_excepciones
    def GetLastID(self):
        "Recuperar último número de transacción (ID)"
        ret = self.client.FEXGetLast_ID(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetLast_IDResult']
        self.__analizar_errores(result)
        if 'FEXResultGet' in result:
            resultget = result['FEXResultGet']
            return resultget.get('Id')

    @inicializar_y_capturar_excepciones
    def GetParamUMed(self, sep="|"):
        ret = self.client.FEXGetPARAM_UMed(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetPARAM_UMedResult']
        self.__analizar_errores(result)
     
        umeds = [] # unidades de medida
        for u in result['FEXResultGet']:
            u = u['ClsFEXResponse_UMed']
            try:
                umed = {'id': u.get('Umed_Id'), 'ds': u.get('Umed_Ds'), 
                        'vig_desde': u.get('Umed_vig_desde'), 
                        'vig_hasta': u.get('Umed_vig_hasta')}
            except Exception, e:
                print e
                if u is None:
                    # <ClsFEXResponse_UMed xsi:nil="true"/> WTF!
                    umed = {'id':'', 'ds':'','vig_desde':'','vig_hasta':''}
                    #import pdb; pdb.set_trace()
                    #print u
                
            
            umeds.append(umed)
        if sep:
            return [("\t%(id)s\t%(ds)s\t%(vig_desde)s\t%(vig_hasta)s\t"
                      % it).replace("\t", sep) for it in umeds]
        else:
            return umeds

    @inicializar_y_capturar_excepciones
    def GetParamMon(self, sep="|"):
        ret = self.client.FEXGetPARAM_MON(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetPARAM_MONResult']
        self.__analizar_errores(result)
     
        mons = [] # monedas
        for u in result['FEXResultGet']:
            u = u['ClsFEXResponse_Mon']
            try:
                mon = {'id': u.get('Mon_Id'), 'ds': u.get('Mon_Ds'), 
                        'vig_desde': u.get('Mon_vig_desde'), 
                        'vig_hasta': u.get('Mon_vig_hasta')}
            except Exception, e:
                print e
                if u is None:
                    # <ClsFEXResponse_UMed xsi:nil="true"/> WTF!
                    mon = {'id':'', 'ds':'','vig_desde':'','vig_hasta':''}
                    #import pdb; pdb.set_trace()
                    #print u
                
            
            mons.append(mon)
        if sep:
            return [("\t%(id)s\t%(ds)s\t%(vig_desde)s\t%(vig_hasta)s\t"
                      % it).replace("\t", sep) for it in mons]
        else:
            return mons

    @inicializar_y_capturar_excepciones
    def GetParamDstPais(self, sep="|"):
        "Recuperador de valores referenciales de códigos de Países"
        ret = self.client.FEXGetPARAM_DST_pais(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetPARAM_DST_paisResult']
        self.__analizar_errores(result)
     
        ret = []
        for u in result['FEXResultGet']:
            u = u['ClsFEXResponse_DST_pais']
            try:
                r = {'codigo': u.get('DST_Codigo'), 'ds': u.get('DST_Ds'), }
            except Exception, e:
                print e
            
            ret.append(r)
        if sep:
            return [("\t%(codigo)s\t%(ds)s\t"
                      % it).replace("\t", sep) for it in ret]
        else:
            return ret

    @inicializar_y_capturar_excepciones
    def GetParamDstCUIT(self, sep="|"):
        "Recuperar lista de valores referenciales de CUIT de Países"
        ret = self.client.FEXGetPARAM_DST_CUIT(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetPARAM_DST_CUITResult']
        self.__analizar_errores(result)
     
        ret = []
        for u in result['FEXResultGet']:
            u = u['ClsFEXResponse_DST_cuit']
            try:
                r = {'codigo': u.get('DST_CUIT'), 'ds': u.get('DST_Ds'), }
            except Exception, e:
                print e
            
            ret.append(r)
        if sep:
            return [("\t%(codigo)s\t%(ds)s\t"
                      % it).replace("\t", sep) for it in ret]
        else:
            return ret

    @inicializar_y_capturar_excepciones
    def GetParamTipoCbte(self, sep="|"):
        "Recuperador de valores referenciales de códigos de Tipo de comprobantes"
        ret = self.client.FEXGetPARAM_Cbte_Tipo(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetPARAM_Cbte_TipoResult']
        self.__analizar_errores(result)
     
        ret = []
        for u in result['FEXResultGet']:
            u = u['ClsFEXResponse_Cbte_Tipo']
            try:
                r = {'codigo': u.get('Cbte_Id'), 
                     'ds': u.get('Cbte_Ds').replace('\n', '').replace('\r', ''),
                     'vig_desde': u.get('Cbte_vig_desde'), 
                     'vig_hasta': u.get('Cbte_vig_hasta')}
            except Exception, e:
                print e
            
            ret.append(r)
        if sep:
            return [("\t%(codigo)s\t%(ds)s\t%(vig_desde)s\t%(vig_hasta)s\t"
                      % it).replace("\t", sep) for it in ret]
        else:
            return ret

    @inicializar_y_capturar_excepciones
    def GetParamTipoExpo(self, sep="|"):
        "Recuperador de valores referenciales de códigos de Tipo de exportación"
        ret = self.client.FEXGetPARAM_Tipo_Expo(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetPARAM_Tipo_ExpoResult']
        self.__analizar_errores(result)
     
        ret = []
        for u in result['FEXResultGet']:
            u = u['ClsFEXResponse_Tex']
            try:
                r = {'codigo': u.get('Tex_Id'), 'ds': u.get('Tex_Ds'),
                     'vig_desde': u.get('Tex_vig_desde'), 
                     'vig_hasta': u.get('Tex_vig_hasta')}
            except Exception, e:
                print e
            
            ret.append(r)
        if sep:
            return [("\t%(codigo)s\t%(ds)s\t%(vig_desde)s\t%(vig_hasta)s\t"
                      % it).replace("\t", sep) for it in ret]
        else:
            return ret

    @inicializar_y_capturar_excepciones
    def GetParamIdiomas(self, sep="|"):
        "Recuperar lista de valores referenciales de códigos de Idiomas"
        ret = self.client.FEXGetPARAM_Idiomas(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetPARAM_IdiomasResult']
        self.__analizar_errores(result)
     
        ret = []
        for u in result['FEXResultGet']:
            u = u['ClsFEXResponse_Idi']
            try:
                r = {'codigo': u.get('Idi_Id'), 'ds': u.get('Idi_Ds'),
                     'vig_desde': u.get('Idi_vig_hasta'), 
                     'vig_hasta': u.get('Idi_vig_desde')}
            except Exception, e:
                print e
            
            ret.append(r)
        if sep:
            return [("\t%(codigo)s\t%(ds)s\t%(vig_desde)s\t%(vig_hasta)s\t"
                      % it).replace("\t", sep) for it in ret]
        else:
            return ret
    
    def GetParamIncoterms(self, sep="|"):
        "Recuperar lista de valores referenciales de Incoterms"
        ret = self.client.FEXGetPARAM_Incoterms(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit, })
        result = ret['FEXGetPARAM_IncotermsResult']
        self.__analizar_errores(result)
     
        ret = []
        for u in result['FEXResultGet']:
            u = u['ClsFEXResponse_Inc']
            try:
                r = {'codigo': u.get('Inc_Id'), 'ds': u.get('Inc_Ds'),
                     'vig_desde': u.get('Inc_vig_hasta'), 
                     'vig_hasta': u.get('Inc_vig_desde')}
            except Exception, e:
                print e
            
            ret.append(r)
        if sep:
            return [("\t%(codigo)s\t%(ds)s\t%(vig_desde)s\t%(vig_hasta)s\t"
                      % it).replace("\t", sep) for it in ret]
        else:
            return ret

    @inicializar_y_capturar_excepciones
    def GetParamCtz(self, moneda_id):
        "Recuperador de cotización de moneda"
        ret = self.client.FEXGetPARAM_Ctz(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit},
            Mon_id=moneda_id,
            )
        self.__analizar_errores(ret['FEXGetPARAM_CtzResult'])
        res = ret['FEXGetPARAM_CtzResult'].get('FEXResultGet')
        if res:
            ctz = str(res.get('Mon_ctz',""))
        else:
            ctz = ''
        return ctz
    
    @inicializar_y_capturar_excepciones
    def GetParamPtosVenta(self):
        "Recupera el listado de los puntos de venta para exportacion y estado"
        ret = self.client.FEXGetPARAM_PtoVenta(
            Auth={'Token': self.Token, 'Sign': self.Sign, 'Cuit': self.Cuit},
            )
        self.__analizar_errores(ret['FEXGetPARAM_PtoVentaResult'])
        res = ret['FEXGetPARAM_PtoVentaResult'].get('FEXResultGet')
        ret = []
        for pu in res:
            p = pu['ClsFEXResponse_PtoVenta']
            try:
                r = {'nro': u.get('Pve_Nro'), 'baja': u.get('Pve_FchBaj'),
                     'bloqueado': u.get('Pve_Bloqueado'), }
            except Exception, e:
                print e
            ret.append(r)
        return [(u"%(nro)s\tBloqueado:%(bloqueado)s\tFchBaja:%(baja)s" % r).replace("\t", sep)
                 for r in ret]


class WSFEX(WSFEXv1):
    "Wrapper para retrocompatibilidad con WSFEX"
    
    _reg_progid_ = "WSFEX"
    _reg_clsid_ = "{B3C8D3D3-D5DA-44C9-B003-11845803B2BD}"

    def __init__(self):
        WSFEXv1.__init__(self)
        self.Version = "%s %s WSFEXv1" % (__version__, HOMO and 'Homologación' or '')

    def Conectar(self, url="", proxy=""):
        # Ajustar URL de V0 a V1:
        if url in ("https://wswhomo.afip.gov.ar/wsfex/service.asmx",
                   "http://wswhomo.afip.gov.ar/WSFEX/service.asmx"):
            url = "https://wswhomo.afip.gov.ar/wsfexv1/service.asmx"
        elif url in ("https://servicios1.afip.gov.ar/wsfex/service.asmx",
                     "http://servicios1.afip.gov.ar/WSFEX/service.asmx"):
            url = "https://servicios1.afip.gov.ar/wsfexv1/service.asmx"
        return WSFEXv1.Conectar(self, cache=None, wsdl=url, proxy=proxy)


# busco el directorio de instalación (global para que no cambie si usan otra dll)
INSTALL_DIR = WSFEXv1.InstallDir = get_install_dir()


def p_assert_eq(a,b):
    print a, a==b and '==' or '!=', b


if __name__ == "__main__":
    DEBUG = '--debug' in sys.argv
    if "--register" in sys.argv or "--unregister" in sys.argv:
        import win32com.server.register
        win32com.server.register.UseCommandLine(WSFEXv1)
        if '--wsfex' in sys.argv:
            win32com.server.register.UseCommandLine(WSFEX)
    #elif "/Automate" in sys.argv:
    #    # MS seems to like /automate to run the class factories.
    #    import win32com.server.localserver
    #    #win32com.server.localserver.main()
    #    # start the server.
    #    win32com.server.localserver.serve([WSFEXv1._reg_clsid_])
    else:

    # Crear objeto interface Web Service de Factura Electrónica de Exportación
        wsfexv1 = WSFEXv1()
        
        # PERSONALIZACION LIBERTYA 2011-06-30 -- LEER PROPERTIES.INI
        try:
            properties = open("properties.ini")
        except:
            try:
                open("properties_error.txt","w").write("No se puede abrir properties.ini")
            except:
                sys.exit("Error escribiendo properties_error.txt\n")
            sys.exit("Error abriendo properties.ini\n")

        try:
            while True:
                linea = properties.readline()
                splited = linea.rsplit("=")
                name = splited[0]
                if name == 'CUIT':
                    var_CUIT = splited[1].rstrip()
                    print "CUIT: %s " % splited[1]
                if name == 'URL_WSAA':
                    WSAAURL = splited[1].rstrip()
                    print "URL_WSAA: %s " % splited[1]
                if name == 'URL_WSFEX':
                    WSFEXURL = splited[1].rstrip()
                    print "URL_WSFEX: %s " % splited[1]
                if name == 'CERTIFICADO':
                    CERTIFICADO = splited[1].rstrip()
                    print "CERTIFICADO: %s " % splited[1]
                if name == 'KEY':
                    KEY = splited[1].rstrip()
                    print "KEY: %s " % splited[1]                          
                if not linea: break
        except:
            try:
                open("error.txt","w").write("Faltan parametros en el archivo properties.ini")
            except:
                sys.exit("Error escribiendo error.txt\n")
            sys.exit("Faltan parametros en el archivo properties.ini")
        
        #obteniendo el TA para pruebas
        from wsaa import WSAA
        ta = WSAA().Autenticar("wsfex", CERTIFICADO, KEY, WSAAURL, debug=True, cacert="conf/afip_ca_info.crt")
        wsfexv1.SetTicketAcceso(ta)

        # CUIT del emisor (debe estar registrado en la AFIP)
        wsfexv1.Cuit = var_CUIT

        # Conectar al Servicio Web de Facturación (producción u homologación)
        #if "--prod" in sys.argv:
        #    wsdl = "https://servicios1.afip.gov.ar/wsfexv1/service.asmx?WSDL"
        #else:
        #    wsdl = "https://wswhomo.afip.gov.ar/wsfexv1/service.asmx?WSDL"
        wsdl = WSFEXURL
        cache = proxy = ""
        wrapper = "httplib2"
        cacert = open("conf/afip_ca_info.crt").read()
        ok = wsfexv1.Conectar(cache, wsdl, proxy, wrapper, cacert)
        
        if not ok:
            raise RuntimeError(wsfexv1.Excepcion)
            
        try:
            format = get_encoding_type("entrada.txt")
            entrada = codecs.open("entrada.txt", "r", format)
        except:
            try:
                open("error.txt","w").write("Error abriendo entrada.txt")
            except:

                sys.exit("Error escribiendo error.txt\n")
            sys.exit("Error abriendo entrada.txt\n")
        print "Definiendo variables..."
        var_numero = entrada.readline()
        var_punto_vta = entrada.readline()
        var_tipo_cbte = entrada.readline()
        var_imp_total = entrada.readline()
        var_fecha_cbte = entrada.readline()
        var_moneda = entrada.readline()
        var_cotizacion = entrada.readline()
        var_tipo_exportacion = entrada.readline()
        var_permiso_embarque = entrada.readline()
        var_pais_destino = entrada.readline()
        var_cliente = entrada.readline()
        var_domicilio_cliente = entrada.readline()
        var_id_impositivo = entrada.readline()
        var_idioma_comprobante = entrada.readline()
        entrada.readline()
        var_cantidad_items = entrada.readline()
        #FIN
    
        if '--dummy' in sys.argv:
            #wsfexv1.LanzarExcepciones = False
            print wsfexv1.Dummy()
            print "AppServerStatus", wsfexv1.AppServerStatus
            print "DbServerStatus", wsfexv1.DbServerStatus
            print "AuthServerStatus", wsfexv1.AuthServerStatus
        
        if "--archivo" in sys.argv:
            try:
                #cbte_nro = int(wsfexv1.GetLastCMP(tipo_cbte, punto_vta)) + 1
                #cbte_nro = long((wsfexv1.CompUltimoAutorizado(tipo_cbte, punto_vta) or 0) + 1)
                print "main --> var_fecha_cbte=", var_fecha_cbte
                fecha_cbte = datetime.datetime(int(var_fecha_cbte[0:4]),int(var_fecha_cbte[4:6]),int(var_fecha_cbte[6:8]),00,00).strftime("%Y%m%d")
                print "main --> fecha_cbte=", fecha_cbte
                tipo_cbte = var_tipo_cbte
                punto_vta = var_punto_vta
                print "mail --> obteniendo GetLastCMP"
                cbte_nro = int(wsfexv1.GetLastCMP(tipo_cbte, punto_vta)) + 1
                print "mail --> comprobante obtenido: ", cbte_nro
                tipo_expo = var_tipo_exportacion
                permiso_existente = var_permiso_embarque
                if permiso_existente:
                    if not permiso_existente.strip():
                        permiso_existente = ""
                else:
                    permiso_existente = ""
                dst_cmp = var_pais_destino
                cliente = var_cliente
                cuit_pais_cliente = None
                domicilio_cliente = var_domicilio_cliente
                id_impositivo = var_id_impositivo
                moneda_id = var_moneda.replace('\n','')
                moneda_ctz = var_cotizacion
                obs_comerciales = None
                imp_total = float(var_imp_total)
                obs = None
                forma_pago = None
                incoterms = None
                incoterms_ds = None
                idioma_cbte = var_idioma_comprobante

                print "---------- INICIO DATOS ENCABEZADO DE LA FACTURA ----------"
                print "FECHA DE COMPROBANTE:", fecha_cbte
                print "TIPO COMPROBANTE:", tipo_cbte
                print "PUNTO DE VENTA:", punto_vta
                print "NUMERO DE COMPROBANTE:", cbte_nro
                print "TIPO DE EXPORTACION:", tipo_expo
                print "PERMISO EXISTENTE:", permiso_existente
                print "DESTINO DEL COMPROBANTE:", dst_cmp
                print "CLIENTE:", cliente
                print "CUIT PAIS CLIENTE:", cuit_pais_cliente
                print "DOMICILIO CLIENTE:", domicilio_cliente
                print "ID IMPOSITIVO:", id_impositivo
                print "MONEDA: ", moneda_id
                print "COTIZACION: ", moneda_ctz
                print "OBSERVACIONES COMERCIALES:", obs_comerciales
                print "IMPORTE TOTAL:", imp_total
                print "OBSERVACIONES:", obs
                print "FORMA DE PAGO:", forma_pago
                print "INCO TERMS:", incoterms
                print "IDIOMA DEL COMPROBANTE:", idioma_cbte
                print "---------- FIN DATOS ENCABEZADO DE LA FACTURA ----------"
                
                #LOG: si no existe la carpeta de log, la creo				
                try:
                    os.stat(LOG_FOLDER)
                except:
                    os.mkdir(LOG_FOLDER) 
                #LOG: creo carpeta dentro del LOG_FOLDER
                log_folder_name = LOG_FOLDER + "/" + fecha_cbte + "_" + punto_vta.rstrip() + "_" + str(cbte_nro) + "_" + tipo_cbte.rstrip()
                print log_folder_name
                os.mkdir(log_folder_name.rstrip())
                #LOG: copio entrada.txt
                copyfile("entrada.txt", log_folder_name.rstrip() + "/" + "entrada.txt")
                
                # Creo una factura (internamente, no se llama al WebService):
                ok = wsfexv1.CrearFactura(tipo_cbte, punto_vta, cbte_nro, fecha_cbte, 
                    imp_total, tipo_expo, permiso_existente, dst_cmp, 
                    cliente, cuit_pais_cliente, domicilio_cliente, 
                    id_impositivo, moneda_id, moneda_ctz, 
                    obs_comerciales, obs, forma_pago, incoterms, 
                    idioma_cbte, incoterms_ds)

                print "---------- INICIO ITEMS DE LA FACTURA ----------"
                # Agrego los items:
                print "CANTIDAD DE ITEMS:", var_cantidad_items
                for j in range(int(var_cantidad_items)):
                    var_item = entrada.readline().split(";")
                    i = 0
                    while i < len(var_item):
                        datos = var_item[i].split(":")
                        var_item_name = datos[0]
                        var_item_qty = datos[1]
                        var_item_uom = datos[2]
                        var_item_importe_unidad = datos[3]
                        var_item_importe_total = datos[4]
                        print "----- INICIO ITEM -----"
                        print "NOMBRE ITEM:", var_item_name
                        print "NOMBRE QTY:", var_item_qty
                        print "NOMBRE UOM:", var_item_uom
                        print "NOMBRE IMPORTE UNIDAD:", var_item_importe_unidad
                        print "NOMBRE IMPORTE TOTAL:", var_item_importe_total
                        print "----- FIN ITEM -----"
                        # lo agrego a la factura (internamente, no se llama al WebService):
                        ok = wsfexv1.AgregarItem(var_item_name, var_item_name, var_item_qty, var_item_uom, var_item_importe_unidad, var_item_importe_total, None)
                        i = i+1
                print "---------- FIN ITEMS DE LA FACTURA ----------"

                print "---------- INICIO AUTORIZACION DE LA FACTURA ----------"
                # obtengo el último ID y le adiciono 1 
                # (advertencia: evitar overflow y almacenar!)
                id = long(wsfexv1.GetLastID()) + 1

                # Llamo al WebService de Autorización para obtener el CAE
                cae = wsfexv1.Authorize(id)
                if cae:
                    print "Comprobante", tipo_cbte, wsfexv1.CbteNro
                    print "Resultado", wsfexv1.Resultado
                    print "CAE", wsfexv1.CAE
                    print "Vencimiento", wsfexv1.Vencimiento
                    print "---------- FIN AUTORIZACION DE LA FACTURA ----------"

                if DEBUG:
                    open("xmlrequest.xml","wb").write(wsfexv1.XmlRequest)
                    open("xmlresponse.xml","wb").write(wsfexv1.XmlResponse)	
                    #LOG: copio request y response xml
                    copyfile("xmlrequest.xml", log_folder_name.rstrip() + "/" + "xmlrequest.xml")
                    copyfile("xmlresponse.xml", log_folder_name.rstrip() + "/" + "xmlresponse.xml")
					
                var_msg = str(wsfexv1.ObtenerTagXml('Obs',0,'Msg'))
                if var_msg == "None":
                    var_msg = str(wsfexv1.ObtenerTagXml('Err',0,'Msg'))
                    var_msg = var_msg.replace(':','=')

                    try:
                        fecha_vencimiento_result = wsfexv1.Vencimiento.replace('/','')
                        fecha_vencimiento_result = datetime.datetime(int(fecha_vencimiento_result[4:8]),int(fecha_vencimiento_result[2:4]),int(fecha_vencimiento_result[0:2]),00,00).strftime("%Y%m%d")
                        salida = str(wsfexv1.Resultado)+ ":" + str(wsfexv1.CAE) + ":" + str(cbte_nro) + ":" + var_msg + ":" + str(fecha_vencimiento_result) + ":" + str(fecha_cbte)
                        
                        print "salida: %s " %salida
                        open("salida.txt","w").write(salida)
                        #LOG: salida.txt
                        copyfile("salida.txt", log_folder_name.rstrip() + "/" + "salida.txt")
                        print "El archivo salida.txt se ha generado correctamente."
                    except:
                        sys.exit("Error escribiendo salida.txt\n")
                        
                else:
                    print "CAE:" % cae
                    
            except:
                print wsfexv1.XmlRequest        
                print wsfexv1.XmlResponse        
                print wsfexv1.ErrCode
                print wsfexv1.ErrMsg
                print wsfexv1.Excepcion
                print wsfexv1.Traceback
        
        if "--prueba" in sys.argv:
            try:
                # Establezco los valores de la factura a autorizar:
                tipo_cbte = '--nc' in sys.argv and 21 or 19 # FC/NC Expo (ver tabla de parámetros)
                punto_vta = 7
                # Obtengo el último número de comprobante y le agrego 1
                cbte_nro = int(wsfexv1.GetLastCMP(tipo_cbte, punto_vta)) + 1
                fecha_cbte = datetime.datetime.now().strftime("%Y%m%d")
                tipo_expo = 1 # tipo de exportación (ver tabla de parámetros)
                permiso_existente = (tipo_cbte not in (20, 21) or tipo_expo!=1) and "S" or ""
                print "permiso_existente", permiso_existente
                dst_cmp = 203 # país destino
                cliente = "Joao Da Silva"
                cuit_pais_cliente = "50000000016"
                domicilio_cliente = u"Rúa Ñ°76 km 34.5 Alagoas"
                id_impositivo = "PJ54482221-l"
                moneda_id = "DOL" # para reales, "DOL" o "PES" (ver tabla de parámetros)
                moneda_ctz = "8.00" # wsfexv1.GetParamCtz('DOL') <- no funciona
                obs_comerciales = "Observaciones comerciales"
                obs = "Sin observaciones"
                forma_pago = "30 dias"
                incoterms = "FOB" # (ver tabla de parámetros)
                incoterms_ds = "Flete a Bordo" 
                idioma_cbte = 1 # (ver tabla de parámetros)
                imp_total = "250.00"
                
                # Creo una factura (internamente, no se llama al WebService):
                ok = wsfexv1.CrearFactura(tipo_cbte, punto_vta, cbte_nro, fecha_cbte, 
                        imp_total, tipo_expo, permiso_existente, dst_cmp, 
                        cliente, cuit_pais_cliente, domicilio_cliente, 
                        id_impositivo, moneda_id, moneda_ctz, 
                        obs_comerciales, obs, forma_pago, incoterms, 
                        idioma_cbte, incoterms_ds)
                
                # Agrego un item:
                codigo = "PRO1"
                ds = "Producto Tipo 1 Exportacion MERCOSUR ISO 9001"
                qty = 2
                precio = "150.00"
                umed = 1 # Ver tabla de parámetros (unidades de medida)
                bonif = "50.00"
                imp_total = "250.00" # importe total final del artículo
                # lo agrego a la factura (internamente, no se llama al WebService):
                ok = wsfexv1.AgregarItem(codigo, ds, qty, umed, precio, imp_total, bonif)
                ok = wsfexv1.AgregarItem(codigo, ds, qty, umed, precio, imp_total, bonif)
                ok = wsfexv1.AgregarItem(codigo, ds, 0, 99, 0, -float(imp_total), 0)

                # Agrego un permiso (ver manual para el desarrollador)
                if permiso_existente:
                    id = "99999AAXX999999A"
                    dst = 225 # país destino de la mercaderia
                    ok = wsfexv1.AgregarPermiso(id, dst)

                # Agrego un comprobante asociado (solo para N/C o N/D)
                if tipo_cbte in (20,21): 
                    cbteasoc_tipo = 19
                    cbteasoc_pto_vta = 2
                    cbteasoc_nro = 1234
                    cbteasoc_cuit = 20111111111
                    wsfexv1.AgregarCmpAsoc(cbteasoc_tipo, cbteasoc_pto_vta, cbteasoc_nro, cbteasoc_cuit)
                    
                ##id = "99000000000100" # número propio de transacción
                # obtengo el último ID y le adiciono 1 
                # (advertencia: evitar overflow y almacenar!)
                id = long(wsfexv1.GetLastID()) + 1

                # Llamo al WebService de Autorización para obtener el CAE
                cae = wsfexv1.Authorize(id)

                print "Comprobante", tipo_cbte, wsfexv1.CbteNro
                print "Resultado", wsfexv1.Resultado
                print "CAE", wsfexv1.CAE
                print "Vencimiento", wsfexv1.Vencimiento

                if wsfexv1.Resultado and False:
                    print wsfexv1.client.help("FEXGetCMP").encode("latin1")
                    wsfexv1.GetCMP(tipo_cbte, punto_vta, cbte_nro)
                    print "CAE consulta", wsfexv1.CAE, wsfexv1.CAE==cae 
                    print "NRO consulta", wsfexv1.CbteNro, wsfexv1.CbteNro==cbte_nro 
                    print "TOTAL consulta", wsfexv1.ImpTotal, wsfexv1.ImpTotal==imp_total

            except Exception, e:
                print wsfexv1.XmlRequest        
                print wsfexv1.XmlResponse        
                print wsfexv1.ErrCode
                print wsfexv1.ErrMsg
                print wsfexv1.Excepcion
                print wsfexv1.Traceback
                raise

        if "--get" in sys.argv:
            wsfexv1.client.help("FEXGetCMP")
            tipo_cbte = 19
            punto_vta = 7
            cbte_nro = wsfexv1.GetLastCMP(tipo_cbte, punto_vta)

            wsfexv1.GetCMP(tipo_cbte, punto_vta, cbte_nro)

            print "FechaCbte = ", wsfexv1.FechaCbte
            print "CbteNro = ", wsfexv1.CbteNro
            print "PuntoVenta = ", wsfexv1.PuntoVenta
            print "ImpTotal =", wsfexv1.ImpTotal
            print "CAE = ", wsfexv1.CAE
            print "Vencimiento = ", wsfexv1.Vencimiento

            wsfexv1.AnalizarXml("XmlResponse")
            p_assert_eq(wsfexv1.ObtenerTagXml('Cae'), str(wsfexv1.CAE))
            p_assert_eq(wsfexv1.ObtenerTagXml('Fecha_cbte'), wsfexv1.FechaCbte)
            p_assert_eq(wsfexv1.ObtenerTagXml('Moneda_Id'), "DOL")
            p_assert_eq(wsfexv1.ObtenerTagXml('Moneda_ctz'), "8")
            p_assert_eq(wsfexv1.ObtenerTagXml('Id_impositivo'), "PJ54482221-l")

        if "--params" in sys.argv:
            import codecs, locale
            sys.stdout = codecs.getwriter('latin1')(sys.stdout); 

            print "=== Incoterms ==="
            idiomas = wsfexv1.GetParamIncoterms(sep="||")
            for idioma in idiomas:
                print idioma

            print "=== Idiomas ==="
            idiomas = wsfexv1.GetParamIdiomas(sep="||")
            for idioma in idiomas:
                print idioma

            print "=== Tipos Comprobantes ==="
            tipos = wsfexv1.GetParamTipoCbte(sep=False)    
            for t in tipos:
                print "||%(codigo)s||%(ds)s||" % t

            print "=== Tipos Expo ==="
            tipos = wsfexv1.GetParamTipoExpo(sep=False)    
            for t in tipos:
                print "||%(codigo)s||%(ds)s||%(vig_desde)s||%(vig_hasta)s||" % t
            #umeds = dict([(u.get('id', ""),u.get('ds', "")) for u in umedidas])
                
            print "=== Monedas ==="
            mons = wsfexv1.GetParamMon(sep=False)    
            for m in mons:
                print "||%(id)s||%(ds)s||%(vig_desde)s||%(vig_hasta)s||" % m
            #umeds = dict([(u.get('id', ""),u.get('ds', "")) for u in umedidas])

            print "=== Unidades de medida ==="
            umedidas = wsfexv1.GetParamUMed(sep=False)    
            for u in umedidas:
                print "||%(id)s||%(ds)s||%(vig_desde)s||%(vig_hasta)s||" % u
            umeds = dict([(u.get('id', ""),u.get('ds', "")) for u in umedidas])

            print u"=== Código Pais Destino ==="
            ret = wsfexv1.GetParamDstPais(sep=False)    
            for r in ret:
                print "||%(codigo)s||%(ds)s||" % r

            print u"=== CUIT Pais Destino ==="
            ret = wsfexv1.GetParamDstCUIT(sep=False)    
            for r in ret:
                print "||%(codigo)s||%(ds)s||" % r
            
        if "--ctz" in sys.argv:
            print wsfexv1.GetParamCtz('DOL')
            
        if "--ptosventa" in sys.argv:
            print wsfexv1.GetParamPtosVenta()

