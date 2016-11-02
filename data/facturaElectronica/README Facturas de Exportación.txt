PASOS ADICIONALES PARA EMITIR FACTURAS DE EXPORTACIÓN ELECTRONICAS
=========================================================================================

1) Instalar el módulo "chardet" en python 2.7. Para ello ejecutar el siguiente comando
   en consola, tanto para windows como para linux:
   
   - pip install chardet

   
2) Editar el archivo run.bat o run.sh. Ejemplo:

   cd c:\pyafipws-wsfex\
   c:\Python27\python.exe wsfexv1.py --archivo --debug > wsfexv1.log

   
3) Modificar el archivo properties.ini para poner el nombre correcto del certificado
   utilizado para facturas de exportación y las URL correctas de WSAA y WSFEX