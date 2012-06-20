// Uso:   WinEnv OXP_HOME JAVA_HOME ; se le pasan como par?metros las dos rutas
// Ejemplo: WinEnv C:\ServidorOXP c:\java\jdk1.5.0_06
// Ejemplo: cscript WinEnv.js C:\ServidorOXP c:\java\jdk1.5.0_06
//
// WinEnv.js - Establece Variables Windows

// $Id: WinEnv.js,v 2.0 $


// Coger Objetos
var Shell = new ActiveXObject("WScript.Shell");
var DesktopPath = Shell.SpecialFolders("Desktop");
var Args = WScript.Arguments;
var SysEnv = Shell.Environment("SYSTEM");

if (Args.length != 2)
{
  WScript.Echo("Uso: cscript WinEnv.js OXP_HOME JAVA_HOME"
	+ "\nEjemplo:\ncscript WinEnv.js C:\ServidorOXP c:\java\jdk1.5.0_06");
  WScript.Quit (1);
}


// Set Environment Variables
SysEnv("OXP_HOME") = Args(0);
WScript.Echo ("SET OXP_HOME="+ Args(0));

SysEnv("JAVA_HOME") = Args(1);
WScript.Echo ("SET JAVA_HOME="+ Args(1));


// Compruebe que JAVA_HOME esta en el PATH
var pathString = SysEnv("PATH"); // Shell.ExpandEnvironmentStrings("%PATH%");
var index = pathString.indexOf(Args(1));
if (index == -1)
{
  SysEnv("PATH") = Args(1) + "\\bin;" + pathString;
  var index_2 = SysEnv("PATH").indexOf(Args(1));
  if (index_2 == -1)
    WScript.Echo ("El PATH no ha sido cambiado - Ejecute como administrador")
  else
    WScript.Echo ("PATH Cambiado = " + SysEnv("PATH"));
}
else
  WScript.Echo ("El PATH es correcto = " + SysEnv("PATH"));


// Crea los accesos directos de openxpertya.exe
// http://msdn.microsoft.com/library/default.asp?url=/library/en-us/script56/html/wsMthCreateShortcut.asp
var link = Shell.CreateShortcut(DesktopPath + "\\openXpertya.lnk");
link.TargetPath = Args(0) + "\\openxpertya.exe";
link.Arguments = "-debug";
link.Description = "Cliente Libertya";
link.IconLocation = Args(0) + "\\openxpertya.exe,0";
link.WorkingDirectory = Args(0);
link.WindowStyle = 3;
link.HotKey = "CTRL+ALT+SHIFT+X";
link.Save();
WScript.Echo ("Creado Acceso Directo para openxpertya.exe");

// Crea el acceso directo a la Web del proyecto openXpertya
var urlLink = Shell.CreateShortcut(DesktopPath + "\\Sitio Web openXpertya.url");
urlLink.TargetPath = "http://www.libertya.org";
urlLink.Save();
WScript.Echo ("Creado Acceso Directo al Sitio Web del proyecto Libertya");
WScript.Echo ("Todo Correcto");
