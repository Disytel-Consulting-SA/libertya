<!--
  - openXpertya (r), Tienda en-línea, Copyright (c) 2003-2007 ConSerTi S.L.
  - este fichero esta licenciado bajo los términos de la Licencia Pública openXpertya (LPO)
  - con aplicación directa del ADDENDUM A, sección 6 (A.6) y posibilidad de relicenciamiento.
  - Licencia y más información en http://www.openxpertya.org/ayuda/Licencia.html
  - Información de la dirección de la tienda Web
  -->

<%@ page session="true" contentType="text/html; charset=UTF-8" errorPage="errorPage.jsp" %>
<%@ taglib uri="webStore.tld" prefix="cws" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=requestAlls.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
		<!-- Set Request Type ID and get Request Type		-->
		<c:set target='${info}' property='id' value='${param.R_RequestType_ID}' />
		<c:set var='requestType' value='${info.requestType}' />
		<c:if test='${empty requestType}'>
			<c:set target='${info}' property='message' value='RequestType not found' />
			<c:redirect url='requestTypes.jsp'/>
		</c:if>	
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Mensajes</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<p><a href="request.jsp">Nuevo mensaje </a></p>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th>Documento</th>
						<th>Sumario</th>
						<th>Estado</th>
						<th>Asignado</th>
						<th>Creado</th>
					</tr>
					<c:forEach items='${requestType.requests}' var='request'> 
						<tr> 
							<td><a href="requestDetails.jsp?R_Request_ID=<c:out value='${request.r_Request_ID}'/>"><c:out value='${request.documentNo}'/></a></td>
							<td><c:out value='${request.summary}'/></td>
							<td><c:out value='${request.statusName}'/></td>
							<td><c:out value='${request.salesRepName}'/></td>
							<td><fmt:formatDate value='${request.created}'/> <c:out value='${request.createdByName}'/></td>
						</tr>
					</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->	
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>