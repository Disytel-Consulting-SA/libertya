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

"""Módulo para obtener código de autorización electrónico del web service 
WSFEv1 de AFIP (Factura Electrónica Nacional - Version 1 - RG2904 opción B)
"""

__author__ = "Mariano Reingart <reingart@gmail.com>"
__copyright__ = "Copyright (C) 2010 Mariano Reingart"
__license__ = "GPL 3.0"
__version__ = "1.12g"

import datetime
import decimal
import os
import socket
import sys
import traceback
from cStringIO import StringIO
from pysimplesoap.client import SimpleXMLElement, SoapClient, SoapFault, parse_proxy, set_http_wrapper

def main():
    "Función principal de pruebas (obtener CAE)"
    import os, time
    print "hola mundooooo"

    try:
    	entrada = open("entrada.txt")
    except:
        try:
           open("error.txt","w").write("Error abriendo entrada.txt")
        except:
           sys.exit("Error escribiendo error.txt\n")
        sys.exit("Error abriendo entrada.txt\n")
        
    var_numero = entrada.readline()
    var_punto_vta = entrada.readline()
    var_tipo_cbte = entrada.readline()
    var_tipo_doc = entrada.readline()
    var_nro_doc = entrada.readline()
    var_imp_total = entrada.readline()
    var_imp_neto = entrada.readline()
    var_fecha_cbte = entrada.readline()
    var_presta_serv = entrada.readline()
    var_moneda = entrada.readline()
    var_cotizacion = entrada.readline()
    var_impuestos = entrada.readline().split(";")
    print len(var_impuestos)
    print var_impuestos[1]

    i = 0
    while i < len(var_impuestos):
        #datos = var_impuestos[i].split(":")
        print "bucle", var_impuestos[i]
        datos = var_impuestos[i].split(":")
        i = i+1
        print "datos[0]", datos[0]
        print "datos[1]", datos[1]
        print "datos[2]", datos[2]
#        print "id", id;
#        print "base_imp", base_imp
#        print "importe", importe
        #wsfev1.AgregarIva(id, base_imp, importe)


                
# busco el directorio de instalación (global para que no cambie si usan otra dll)
if not hasattr(sys, "frozen"): 
    basepath = __file__
elif sys.frozen=='dll':
    import win32api
    basepath = win32api.GetModuleFileName(sys.frozendllhandle)
else:
    basepath = sys.executable
INSTALL_DIR = os.path.dirname(os.path.abspath(basepath))

if __name__ == '__main__':
        main()
