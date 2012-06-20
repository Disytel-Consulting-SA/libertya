<!--
  - openXpertya (r), Tienda en-línea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los términos de la Licencia Pública openXpertya (LPO)
  - con aplicación directa del ADDENDUM A, sección 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y más información en http://www.openxpertya.org/ayuda/Licencia.html
  - Información de la dirección de la tienda Web
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
		<h2>Información de seguimiento de la pila:</h2>
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
