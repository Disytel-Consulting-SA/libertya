<!--
  - openXpertya (r), Tienda en-l�nea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los t�rminos de la Licencia P�blica openXpertya (LPO)
  - con aplicaci�n directa del ADDENDUM A, secci�n 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y m�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
  - Informaci�n de la direcci�n de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=iso-8859-1" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=template.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Mis plantillas</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr>
						<th>Nombre</th>
						<th>Descripci&oacute;n</th>
						<th>Fecha</th>
					</tr>
				<c:forEach items='${info.template}' var='asset'> 
					<tr> 
						<td><c:out value='${asset.name}'/></td>
						<td><c:out value='${asset.description}'/></td>
						<td><fmt:formatDate value='${asset.guaranteeDate}'/></td>
					</tr>
				</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->		
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>