@Title DOCUMENTACION PARA EL PRODUCTO ENTERO
@Rem $Id: RUN_doc.bat,v 1.0 $

del /F /S /Q API
rmdir /S /Q API

@call documentation.bat  "..\base\src;..\client\src;..\dbPort\src;..\extend\src;..\interfaces\src;..\looks\src;..\print\src;..\serverApps\src\main\ejb;..\serverApps\src\main\servlet;..\serverRoot\src\main\client;..\serverRoot\src\main\ejb;..\serverRoot\src\main\server;..\serverRoot\src\main\servlet;..\tools\src" API

@pause


