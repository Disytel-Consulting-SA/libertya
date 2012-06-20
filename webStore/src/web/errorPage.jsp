<!--
  - openXpertya (r), Tienda en-l�nea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t�rminos de la Licencia P�blica openXpertya (LPO)
  - con aplicaci�n directa del ADDENDUM A, secci�n 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci�n de la direcci�n de la tienda Web
  -->

<%@ page isErrorPage="true" %>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
		<h1>Error en openXpertya, tienda en l&iacute;nea</h1>
		<p>Ha ocurrido un error. Si este problema persiste, por favor informenos a la mayor brevedad posible. Adjunte la informaci&oacute;n adicional abajo descrita.</p>
		<p>Mensage de error:</p>
		<pre><font color="red"><%= exception.getMessage() %></font></pre>
		<h2>Informaci�n de seguimiento de la pila:</h2>
		<%
			java.lang.Throwable ex = exception;
			while (ex != null)
			{
				out.println("<h3>" + ex.toString() + "</h3>");
				java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(cw,true);
				ex.printStackTrace (pw);
				out.println("<pre><font color=\"red\">");
				out.println(cw.toString());
				out.println("</font></pre>");
				ex = exception.getCause();
			}
		%>
	</body>
</html>
