<!--
  - openXpertya (r), Tienda en-línea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los términos de la Licencia Pública openXpertya (LPO)
  - con aplicación directa del ADDENDUM A, sección 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y más información en http://www.openxpertya.org/ayuda/Licencia.html
  - Información de la dirección de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<cws:priceList priceList_ID="0"/> 

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
		<script language="javascript">
		function mensajeria(){
			var newWin = window.open("http://<c:out value='${ctx.SMTPHost}'/>:3000/WorldClient.dll?View=Main&amp;User=<c:out value='${ctx.#Request_EMail}'/>&Password=<c:out value='${ctx.Password}'/>", "subWindow","height=500,width=700,resizable=yes,scrollbars=yes"); 
		}
		window.onLoad = mensajeria; //debería funcionar, pero no siempre lo hace. La falta de paréntesis () es correcta.
		</script>
	</head>
	<body onLoad="initClock()">
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<p align="center"><a href="http://<c:out value='${ctx.SMTPHost}'/>:3000/WorldClient.dll?View=Main&User=<c:out value='${ctx.#Request_EMail}'/>&Password=<c:out value='${ctx.Password}'/>" target="_blank">Si no se abre autom&aacute;ticamente una nueva ventana pulse aqu&iacute;</a></p>
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>