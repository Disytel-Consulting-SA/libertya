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

<c:if test='${empty webUser || !webUser.loggedIn}'>
	<c:redirect url='loginServlet?ForwardTo=rfqs.jsp'/>
</c:if>

<html>
	<head>
		<title><c:out value='${ctx.name}'/></title>
		<%@ include file="/WEB-INF/jspf/head.jspf" %>
	</head>
	<body onLoad="initClock()">
		<%@ include file="/WEB-INF/jspf/header.jspf" %>
		<!-- Set RfQ ID and get RfQ		-->
		<c:set target='${info}' property='id' value='${param.C_RfQ_ID}' />
		<c:set var='rfqResponse' value='${info.rfQResponse}' />
		<c:if test='${empty rfqResponse}'>
			<c:set target='${info}' property='message' value='No RfQ Response' />
			<c:redirect url='rfqs.jsp'/>
		</c:if>
		<c:set var='rfq' value='${rfqResponse.rfQ}' />
		<c:if test='${empty rfq}'>
		  <c:set target='${info}' property='message' value='RfQ not found' />
		  <c:redirect url='rfqs.jsp'/>
		</c:if>
		<div id="wrap">
			<div id="header"><%@ include file="/WEB-INF/jspf/header.jspf" %></div>
			<div id="left"><%@ include file="/WEB-INF/jspf/left.jspf" %></div>
			<div id="right"><%@ include file="/WEB-INF/jspf/right.jspf" %></div>
			<div id="center">
				<h1>Mi gesti&oacute;n de pedidos</h1>
				<c:if test='${not empty info.info}'>
					<p><strong><c:out value='${info.message}'/></strong></p>
				</c:if>
				<!--[if IE]> <div style="width: 100%;"> <![endif]-->
				<table class="tablelist">
					<tr> 
						<th>Nombre</th>
						<th>Descripci&oacute;n</th>
						<th>Detalles</th>
						<th>Respondido el</th>
						<th>Comienzo de la tarea</th>
						<th>Entrega</th>
					</tr>
					<c:forEach items='${info.rfQs}' var='rfq'> 
						<tr> 
							<td><a href="rfqDetails.jsp?C_RfQ_ID=<c:out value='${rfq.c_RfQ_ID}'/>"><c:out value='${rfq.name}'/></a></td>
							<td><c:out value='${rfq.description}'/></td>
							<td><c:out value='${rfq.help}'/></td>
							<td><fmt:formatDate value='${rfq.dateResponse}'/></td>
							<td><fmt:formatDate value='${rfq.dateWorkStart}'/></td>
							<td><fmt:formatDate value='${rfq.dateWorkComplete}'/> - <c:out value='${rfq.deliveryDays}'/> d&iacute;a(s)</td>
						</tr>
					</c:forEach> 
				</table>
				<!--[if IE]> </div> <![endif]-->	
			</div>
			<div id="footer"><%@ include file="/WEB-INF/jspf/footer.jspf" %></div>
		</div>
	</body>
</html>